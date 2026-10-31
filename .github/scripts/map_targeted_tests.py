#!/usr/bin/env python3
"""Map a pull request's changed files to the unit tests that cover them.

Issue #166 (targeted-tests mapper cure). The previous mapping lived in
inline bash in the `targeted` job of rescue-build.yml and derived the
Gradle `--tests` filter from the changed FILE's name. Two defects,
both observed on live PRs and filed on #166:

1. File-name blind spot. A Kotlin file can declare classes whose
   names differ from the file's (ShieldSetupTruthTest.kt held
   ShieldSetupTruthPureTest + ShieldSetupTruthRenderTest). The old
   filter named a class that does not exist -> Gradle "No tests found
   for given includes", an empty artifact, and a gate that read as a
   test failure where no test ran. Where the file-named class happens
   to exist alongside others (ShieldSetupTruthRenderTest.kt today),
   the old filter silently ran only that one class.
2. Vacuous green. When a diff held only production files and no test
   file name matched a changed file's stem, the filter list was empty,
   the "Targeted unit tests" step was SKIPPED, and the job concluded
   SUCCESS with zero tests executed and no artifact at all.

This mapper therefore derives every filter from file CONTENTS:

- A changed test file contributes the FQCNs of the top-level
  class/object declarations in that file whose bodies contain @Test.
- A changed main-source (.kt) file selects candidate test files two
  ways: the test file's name contains the changed file's stem, or the
  test file's contents reference the stem as a whole word (an import,
  a call, a type reference). Each candidate contributes its declared
  test classes as above. This routes a production-only change to its
  covering tests even when the covering test file is unmoved by the
  diff (the PR #705 shape: DrawerScreen.kt changed alone maps to
  VaultHistoryTruthTest, the one test file that references it).
- Any other changed file under app/src/main/ (resources, manifest,
  assets) is a production change that maps to nothing mechanically;
  it is reported as unmapped production so the workflow can conclude
  honestly instead of green.

The unmapped report separates CODE (a .kt file under
app/src/main/java/ — the vacuous-green incident's shape: production
logic whose tests must be named or the gate is unmet) from RESOURCE
files (strings, manifest, assets — no unit-test filter can name
their coverage; the compile job and the batch/main venue judge them,
and the summary says so in plain words rather than dressing the PR
in a code-shaped failure).

Facts are written to filters.txt (one FQCN per line, sorted, unique),
unmapped.txt (one `code`/`resource`-prefixed production file per
line), and — when GITHUB_OUTPUT is set — the step outputs `count`,
`production_changed`, `unmapped_count`, and `unmapped_code`. The
script itself always exits 0: it reports facts; the workflow decides
the verdict.
"""

import os
import re
import sys

TEST_ROOT = "app/src/test/java/"
MAIN_ROOT = "app/src/main/"

DECL_LINE_RE = re.compile(
    r"^\s*(?:(?:public|private|internal|protected|open|abstract|final|"
    r"sealed|data|enum|annotation|inner|value|companion)\s+)*"
    r"(?:class|object)\s+([A-Za-z_][A-Za-z0-9_]*)"
)
PACKAGE_RE = re.compile(r"^\s*package\s+([A-Za-z_][\w.]*)", re.MULTILINE)


def code_skeleton(text):
    """Return `text` with comments and string/char literals blanked,
    so brace counting and declaration search see only code shape.
    Newlines are preserved so offsets stay line-comparable."""
    out = []
    i, n = 0, len(text)
    state = None  # None | 'line' | 'block' | 'str' | 'raw' | 'char'
    while i < n:
        c = text[i]
        nxt = text[i + 1] if i + 1 < n else ""
        if state is None:
            if c == "/" and nxt == "/":
                state = "line"; out.append("  "); i += 2
            elif c == "/" and nxt == "*":
                state = "block"; out.append("  "); i += 2
            elif c == '"' and text.startswith('"""', i):
                state = "raw"; out.append("   "); i += 3
            elif c == '"':
                state = "str"; out.append(" "); i += 1
            elif c == "'":
                state = "char"; out.append(" "); i += 1
            else:
                out.append(c); i += 1
        elif state == "line":
            if c == "\n":
                state = None; out.append(c)
            else:
                out.append(" ")
            i += 1
        elif state == "block":
            if c == "*" and nxt == "/":
                state = None; out.append("  "); i += 2
            else:
                out.append("\n" if c == "\n" else " "); i += 1
        elif state == "raw":
            if text.startswith('"""', i):
                state = None; out.append("   "); i += 3
            else:
                out.append("\n" if c == "\n" else " "); i += 1
        elif state in ("str", "char"):
            quote = '"' if state == "str" else "'"
            if c == "\\":
                out.append("  "); i += 2
            elif c == quote:
                state = None; out.append(" "); i += 1
            else:
                out.append("\n" if c == "\n" else " "); i += 1
    return "".join(out)


def test_classes_in_file(path):
    """FQCNs of top-level class/object declarations in `path` whose
    own body contains at least one @Test annotation."""
    try:
        with open(path, encoding="utf-8") as fh:
            raw = fh.read()
    except OSError:
        return []
    skel = code_skeleton(raw)
    pkg_match = PACKAGE_RE.search(raw)
    pkg = pkg_match.group(1) if pkg_match else ""

    raw_lines = raw.splitlines()
    skel_lines = skel.splitlines()
    # Brace depth at the START of each line, over the skeleton.
    depth_at_line = []
    depth = 0
    for line in skel_lines:
        depth_at_line.append(depth)
        depth += line.count("{") - line.count("}")

    decls = []  # (line_index, simple_name) for top-level declarations
    for i, line in enumerate(skel_lines):
        if depth_at_line[i] == 0:
            m = DECL_LINE_RE.match(line)
            if m:
                decls.append((i, m.group(1)))
    classes = []
    for idx, (start, name) in enumerate(decls):
        end = decls[idx + 1][0] if idx + 1 < len(decls) else len(raw_lines)
        if "@Test" in "\n".join(raw_lines[start:end]):
            classes.append(f"{pkg}.{name}" if pkg else name)
    return classes


def main():
    changed_path = sys.argv[1] if len(sys.argv) > 1 else "changed.txt"
    with open(changed_path, encoding="utf-8") as fh:
        changed = [ln.strip() for ln in fh if ln.strip()]

    all_test_files = []
    for root, _dirs, files in os.walk(TEST_ROOT):
        for name in files:
            if name.endswith(".kt"):
                all_test_files.append(os.path.join(root, name))
    all_test_files.sort()

    filters = set()
    production_changed = False
    unmapped = []

    def add_test_file(path, why):
        found = test_classes_in_file(path)
        for fqcn in found:
            if fqcn not in filters:
                print(f"map: {fqcn}  <- {path} ({why})")
            filters.add(fqcn)
        if not found:
            print(f"map: {path} ({why}) declares no @Test class — no filter")
        return found

    for f in changed:
        if f.startswith(TEST_ROOT) and f.endswith(".kt"):
            if os.path.exists(f):
                add_test_file(f, "changed test file")
            else:
                print(f"map: {f} — changed test file absent from the "
                      "head tree (deleted/renamed); no filter")
        elif f.startswith(MAIN_ROOT):
            production_changed = True
            # A production file is mapped iff at least one test
            # file selects for its stem (by name or by content
            # reference) and declares a @Test class — judged on
            # THIS file's own candidates, never on whether those
            # classes were already selected by an earlier file in
            # the diff. (BUILDER-264, PR #731: ArmProvingController.kt
            # was declared unmapped although ArmProvingControllerTest
            # and ArmProductionWiringTest both reference its stem,
            # ran, and passed — MonolithRepository, earlier in the
            # diff, had already pulled both into the filter set, so
            # the per-file filter-growth check read shared coverage
            # as no coverage. The filter set itself is unchanged by
            # this accounting: the same classes are selected; only
            # the unmapped verdict becomes accurate.)
            mapped_for_file = set()
            if f.startswith(MAIN_ROOT + "java/") and f.endswith(".kt"):
                stem = os.path.basename(f)[:-3]
                word = re.compile(r"\b" + re.escape(stem) + r"\b")
                for tf in all_test_files:
                    base = os.path.basename(tf)
                    if stem in base:
                        mapped_for_file.update(
                            add_test_file(tf, f"name matches stem {stem}"))
                    else:
                        try:
                            with open(tf, encoding="utf-8") as fh:
                                if word.search(fh.read()):
                                    mapped_for_file.update(add_test_file(
                                        tf, f"contents reference {stem}"))
                        except OSError:
                            pass
            if not mapped_for_file:
                kind = ("code" if f.startswith(MAIN_ROOT + "java/")
                        and f.endswith(".kt") else "resource")
                unmapped.append((kind, f))
                print(f"map: {f} — production change ({kind}), "
                      "NO test maps to it")
        else:
            print(f"map: {f} — outside app/src/main and app/src/test; "
                  "not the targeted job's business")

    with open("filters.txt", "w", encoding="utf-8") as fh:
        for fqcn in sorted(filters):
            fh.write(fqcn + "\n")
    with open("unmapped.txt", "w", encoding="utf-8") as fh:
        for kind, f in unmapped:
            fh.write(f"{kind}\t{f}\n")

    unmapped_code = sum(1 for kind, _f in unmapped if kind == "code")
    print(f"map: {len(filters)} test class(es) mapped; "
          f"production_changed={production_changed}; "
          f"unmapped_production={len(unmapped)} "
          f"(code={unmapped_code})")
    gh_out = os.environ.get("GITHUB_OUTPUT")
    if gh_out:
        with open(gh_out, "a", encoding="utf-8") as fh:
            fh.write(f"count={len(filters)}\n")
            fh.write(f"production_changed="
                     f"{'true' if production_changed else 'false'}\n")
            fh.write(f"unmapped_count={len(unmapped)}\n")
            fh.write(f"unmapped_code={unmapped_code}\n")
    return 0


if __name__ == "__main__":
    sys.exit(main())
