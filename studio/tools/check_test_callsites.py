#!/usr/bin/env python3
"""
check_test_callsites.py — Wave-2 anti-drift census for issue #2 (Fuge's cell).

WHY THIS EXISTS
  Batch 9's unit tests stopped compiling not because anyone broke a
  test, but because production moved and test call-sites froze:
  ParkDrawer gained required parameters in two separate lanes, and a
  hand-built StatusBarNotification froze at the SDK-35 constructor
  arity. Kotlin reports this as a wall of "No value passed for
  parameter" errors — but only at compile time, and this studio
  compiles rarely, on purpose, at gates.

  This script is the cheap, always-available pre-gate: a static
  census (no Gradle, no compile, stdlib only) that answers, in
  seconds, "does every test call-site still speak the current
  production signature?" Run it after ANY production signature
  change and before handing a tree to the compile gate.

WHAT IT CHECKS
  1. Composable/function calls in app/src/test that use NAMED
     arguments against a production declaration found in
     app/src/main: every required (no-default) parameter must be
     present, and every named argument must exist in the signature.
     (Calls made purely positionally are reported as UNCHECKED —
     arity of constructors is check 2's job, and mixed calls are
     checked for their named part only.)
  2. StatusBarNotification positional arity == 10, the only public
     constructor under SDK 36 (the @Deprecated form:
     pkg, opPkg, id, tag, uid, initialPid, score, notification,
     user, postTime). If a future SDK changes this, update
     SBN_SDK36_ARITY and this comment together.
  3. The two known-bogus top-level imports stay dead:
     androidx.compose.ui.test.assertExists /
     androidx.compose.ui.test.assertDoesNotExist are NOT top-level
     functions — they are member functions of
     SemanticsNodeInteraction. Importing them is a compile error;
     calling .assertExists() on a node is correct and is not
     flagged.

LIMITS (stated plainly, per TELL-THE-TRUTH)
  - This is not a compiler. Type mismatches, unresolved symbols in
    bodies, and errors behind these sites stay invisible to it.
  - Signature parsing is heuristic: parameters whose default values
    contain top-level-looking commas inside lambdas/strings may be
    mis-split. A flagged site is a suspicion to read, not a verdict.
  - Only declarations discoverable as `fun Name(` in main sources
    are censused.

EXIT CODE: 0 = census clean, 1 = findings (printed, one per line).
USAGE: python3 studio/tools/check_test_callsites.py [repo-root]
"""

import re
import sys
from pathlib import Path

SBN_SDK36_ARITY = 10
BANNED_IMPORTS = (
    "androidx.compose.ui.test.assertExists",
    "androidx.compose.ui.test.assertDoesNotExist",
)


def strip_comments(text):
    """Remove // and /* */ comments, respecting string literals.
    Signatures in this codebase carry long explanatory comments
    between parameters; left in, a comment's ':' or ',' parses as
    a phantom parameter (caught in Wave-2: '// Slice E: ...' became
    a required parameter named 'E')."""
    out = []
    i, in_str, prev = 0, False, ""
    while i < len(text):
        ch = text[i]
        nxt = text[i + 1] if i + 1 < len(text) else ""
        if ch == '"' and prev != "\\":
            in_str = not in_str
            out.append(ch)
        elif not in_str and ch == "/" and nxt == "/":
            while i < len(text) and text[i] != "\n":
                i += 1
            continue
        elif not in_str and ch == "/" and nxt == "*":
            i += 2
            while i + 1 < len(text) and not (text[i] == "*" and text[i + 1] == "/"):
                i += 1
            i += 2
            continue
        else:
            out.append(ch)
        prev = ch
        i += 1
    return "".join(out)


def extract_call(text, open_paren_idx):
    """Return (call_text, end_idx) for the balanced (...) at idx."""
    depth = 0
    j = open_paren_idx
    in_str = False
    prev = ""
    while j < len(text):
        ch = text[j]
        if ch == '"' and prev != "\\":
            in_str = not in_str
        if not in_str:
            if ch == "(":
                depth += 1
            elif ch == ")":
                depth -= 1
                if depth == 0:
                    return text[open_paren_idx : j + 1], j
        prev = ch
        j += 1
    return text[open_paren_idx:], len(text)


def split_top_level(s):
    # Note: '>' only closes a generic '<' when it is not half of a
    # lambda arrow '->' — parameter lists are full of '() -> Unit',
    # and misreading those arrows truncates the signature at the
    # first lambda parameter (this bug was caught by the script's
    # own first run, Wave-2).
    parts, depth, cur, in_str, prev = [], 0, "", False, ""
    for ch in s:
        if ch == '"' and prev != "\\":
            in_str = not in_str
        if not in_str:
            if ch in "(<[{":
                depth += 1
            elif ch in ")]}" or (ch == ">" and prev != "-"):
                depth -= 1
            elif ch == "," and depth == 0:
                parts.append(cur)
                cur = ""
                prev = ch
                continue
        cur += ch
        prev = ch
    if cur.strip():
        parts.append(cur)
    return parts


def parse_signatures(main_root):
    """name -> (required_params, all_params) from main sources."""
    sigs = {}
    for kt in main_root.rglob("*.kt"):
        text = strip_comments(kt.read_text(errors="ignore"))
        for m in re.finditer(r"\bfun\s+([A-Z]\w*)\s*\(", text):
            call, _ = extract_call(text, m.end() - 1)
            params = split_top_level(call[1:-1])
            required, allp = [], []
            for p in params:
                p = " ".join(p.split())
                if not p or ":" not in p:
                    continue
                name = p.split(":")[0].strip().split()[-1]
                if not re.fullmatch(r"\w+", name):
                    continue
                allp.append(name)
                if "=" not in p:
                    required.append(name)
            if allp:
                sigs.setdefault(m.group(1), (required, allp))
    return sigs


def count_positional_args(call):
    inner = call[1:-1]
    if not inner.strip():
        return 0
    return len(split_top_level(inner))


def main():
    repo = Path(sys.argv[1]) if len(sys.argv) > 1 else Path.cwd()
    main_root = repo / "app/src/main/java"
    test_root = repo / "app/src/test/java"
    if not main_root.exists() or not test_root.exists():
        print(f"FATAL: expected app/src/main/java and app/src/test/java under {repo}")
        return 1

    sigs = parse_signatures(main_root)
    findings = []
    unchecked_positional = 0

    for kt in sorted(test_root.rglob("*.kt")):
        raw = kt.read_text(errors="ignore")
        text = strip_comments(raw)
        rel = kt.relative_to(repo)

        # Check 3 — banned imports.
        for imp in BANNED_IMPORTS:
            if re.search(r"^import\s+" + re.escape(imp) + r"\s*$", text, re.M):
                findings.append(f"{rel}: banned import present: {imp}")

        # Check 2 — StatusBarNotification arity (positional ctor).
        for m in re.finditer(r"\bStatusBarNotification\s*\(", text):
            call, _ = extract_call(text, m.end() - 1)
            n = count_positional_args(call)
            if n != SBN_SDK36_ARITY:
                findings.append(
                    f"{rel}: StatusBarNotification called with {n} args; "
                    f"SDK 36's only public constructor takes {SBN_SDK36_ARITY}"
                )

        # Check 1 — named-arg call-sites vs production signatures.
        for name, (required, allp) in sigs.items():
            for m in re.finditer(r"\b" + re.escape(name) + r"\s*\(", text):
                call, end_idx = extract_call(text, m.end() - 1)
                named = re.findall(r"(\w+)\s*=(?!=)", call)
                # Trailing-lambda syntax — `Theme { ... }` — supplies
                # the signature's final parameter without naming it.
                tail = text[end_idx + 1 : end_idx + 40].lstrip()
                if tail.startswith("{") and allp:
                    named = named + [allp[-1]]
                if not named:
                    unchecked_positional += 1
                    continue
                missing = [p for p in required if p not in named]
                unknown = [a for a in named if a not in allp]
                if missing:
                    findings.append(
                        f"{rel}: {name}( missing required: {', '.join(missing)}"
                    )
                if unknown:
                    findings.append(
                        f"{rel}: {name}( unknown named args: {', '.join(sorted(set(unknown)))}"
                    )

    print(f"census: {len(sigs)} production declarations, "
          f"{unchecked_positional} positional-only call-sites unchecked by design")
    if findings:
        print(f"FAIL — {len(findings)} finding(s):")
        for f in findings:
            print("  - " + f)
        return 1
    print("PASS — every named-arg test call-site speaks its current production signature; "
          f"StatusBarNotification arity = {SBN_SDK36_ARITY}; banned imports absent.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
