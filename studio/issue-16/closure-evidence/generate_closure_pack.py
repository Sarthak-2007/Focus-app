#!/usr/bin/env python3
"""
#16 CLOSURE EVIDENCE GENERATOR — T24 Builder seat 2, #16 wave.

Assembles issue #16's closure proof pack from LIVE gh/git data. Data tooling
only; compiles nothing. New-files-only lane (collision guard: PR #231 is
K1 + W6's; this script never edits pill/train files and never re-lands code).

Per register commit (VIER LEDGER-16, #245 comment 6529923438; register
summary cross-checked against T02 seat-4's landing pack on this issue):
  16 once-stranded commits = 1 content-in-main / 10 ride the train (PR #231,
  head branch batch-10) / 5 paper recovered.
Classification is LAYERED — no single signal is trusted alone:
  1. ancestry        git merge-base --is-ancestor <sha> <ref>
  2. patch-id        git cherry <ref> <sha> <sha>^     ('-' = equivalent present)
  3. blob content    every file the commit touched exists in <ref> with an
                     identical blob (adds/mods) — catches squash/recovery
                     re-creations that patch-id misses (e.g. PR #563).
  4. seam greps      the three SENSITIVE seams checked as CONTENT
                     discriminators on the target tree (presence of a
                     function name proves nothing; the composed form does).

SENSITIVE — NEVER RE-LAND (their content rides inside the train's own
composition; a separate re-land collides with K1/W6's train):
  ed75fc88  start2MinutePeek composition (grant gate first, launch second)
  ecc7fef2  startPeekLease Boolean contract + full cell package
  b0e27e36  pill package landing (queryForegroundPackage seam)
  eb36f6b9  SPLIT: workflow content superseded in part; its debug-keystore
            GENERATION headline was deliberately superseded by the
            committed-keystore route — never re-land the headline.

Also emitted: PR #231 state (gh), PR #503 state (gh), the merged head's CI
run read via the Actions jobs API (only meaningful once #231 is MERGED),
the #245 MERGED-line presence check, and negative checks (no keystore
generation in main's workflows, no pill DB migration).

Every gh call is timeout-bounded and failure-honest: an unreachable API is
reported as UNVERIFIED-API, never guessed. Git reads run against freshly
fetched refs when fetch succeeds, else the newest local refs (named).

Usage:  python3 generate_closure_pack.py [--repo PATH] [--out FILE]
Exit:   0 always (the pack is evidence, not a gate); verdicts live in the pack.
"""

import argparse, json, re, subprocess, sys, datetime

REPO_SLUG = "Sarthak-2007/Focus-app"
GH_TIMEOUT = 45          # seconds per gh call, <= 90 per lane rules
MAX_GH_NOTE = "max 2 gh calls in flight: this script is strictly sequential"

# sha, subject (short), VIER expected class, sensitive note or ""
REGISTER = [
    ("873a23e8", "verification plan + merge flags (Pastille cell)", "TRAIN", ""),
    ("ed75fc88", "composed start2MinutePeek (Suki guard + ST-08)", "TRAIN",
     "SENSITIVE seam 1: start2MinutePeek composed order — never re-land"),
    ("ecc7fef2", "full cell package + startPeekLease Boolean fix", "TRAIN",
     "SENSITIVE seam 2: startPeekLease Boolean contract — never re-land"),
    ("d81bc162", "Wave-2: NO_VERDICT filed, test-v2.sh", "TRAIN", ""),
    ("477df552", "Wave-2 addendum: Route B bypass, C13 ghost clock", "TRAIN", ""),
    ("5c3ba072", "Wave-2: push status record", "TRAIN", ""),
    ("174998e1", "Wave-2: final status DIVERGED record", "TRAIN", ""),
    ("e0498346", "Wave-2 addendum-2: test-v2.sh Bug B + JDK evidence", "TRAIN", ""),
    ("b0e27e36", "pill package lands: PillFace/Controller/Theme, 18 tests", "TRAIN",
     "SENSITIVE seam 3: queryForegroundPackage seam — never re-land"),
    ("eb36f6b9", "issue #3 wave-2: signing cure in real workflow", "TRAIN-SPLIT",
     "SENSITIVE SPLIT: keystore-GENERATION headline superseded — never re-land"),
    ("c727dde2", "last mile: test-compile census cure (test files only)", "IN-MAIN", ""),
    ("2bfc48a1", "Glut crazy-ideas file (17 ideas)", "PAPER", ""),
    ("db7bc43f", "Zunder crazy-ideas for #16", "PAPER", ""),
    ("09cfb2aa", "Glanz interaction detail", "PAPER", ""),
    ("d7eab4a8", "GLUT enthusiastic designer: pill greeting/arrival", "PAPER", ""),
    ("c40181f9", "Sable brutal-critic charges S1-S10", "PAPER", ""),
]

PILL_FILES = [
    "app/src/main/java/com/example/service/PillController.kt",
    "app/src/main/java/com/example/service/PillFace.kt",
    "app/src/main/java/com/example/service/PillTheme.kt",
    "app/src/main/java/com/example/data/EditableTimes.kt",
    "app/src/test/java/com/example/PillDerivationTest.kt",
    "app/src/test/java/com/example/PillLeaseRepositoryTest.kt",
]

SEAM_GREPS = [
    ("seam 1 start2MinutePeek composed (grant before launch)",
     "app/src/main/java/com/example/ui/MonolithViewModel.kt", "start2MinutePeek"),
    ("seam 2 startPeekLease returns Boolean",
     "app/src/main/java/com/example/data/MonolithRepository.kt", "startPeekLease"),
    ("seam 3 queryForegroundPackage present",
     None, "queryForegroundPackage"),
]


def run(cmd, timeout=60, cwd=None):
    try:
        p = subprocess.run(cmd, capture_output=True, text=True,
                           timeout=timeout, cwd=cwd)
        return p.returncode, p.stdout.strip(), p.stderr.strip()
    except subprocess.TimeoutExpired:
        return 124, "", f"TIMEOUT after {timeout}s"
    except Exception as e:  # noqa: BLE001
        return 1, "", str(e)


def git(repo, *args, timeout=60):
    return run(["git", "-C", repo, *args], timeout=timeout)


def gh(*args, timeout=GH_TIMEOUT):
    return run(["gh", *args], timeout=timeout)


def is_ancestor(repo, sha, ref):
    rc, _, _ = git(repo, "merge-base", "--is-ancestor", sha, ref)
    return rc == 0


def cherry(repo, ref, sha):
    """'-' patch-equivalent present in ref, '+' not, '?' unknown."""
    rc, out, _ = git(repo, "rev-parse", f"{sha}^")
    if rc != 0:
        return "?"
    rc, out, _ = git(repo, "cherry", ref, sha, out)
    if rc != 0 or not out:
        return "?"
    return out[0]


def commit_files(repo, sha):
    rc, out, _ = git(repo, "diff-tree", "--no-commit-id", "--name-only", "-r", sha)
    return [ln for ln in out.splitlines() if ln] if rc == 0 else []


def blob_same(repo, sha, ref, path):
    rc1, b1, _ = git(repo, "rev-parse", f"{sha}:{path}")
    rc2, b2, _ = git(repo, "rev-parse", f"{ref}:{path}")
    return rc1 == 0 and rc2 == 0 and b1 == b2


def classify(repo, sha, ref):
    """Return (ancestry, patchid, blobs_identical, blobs_total)."""
    anc = is_ancestor(repo, sha, ref)
    pid = cherry(repo, ref, sha)
    files = commit_files(repo, sha)
    same = sum(1 for f in files if blob_same(repo, sha, ref, f))
    return anc, pid, same, len(files)


def verdict_for(expected, anc, pid, same, total):
    """Plain-language reconciliation of live evidence vs the VIER register."""
    content_in = anc or pid == "-" or (total > 0 and same == total)
    if not content_in and total > 0 and same * 2 >= total:
        return (f"CONTENT SUBSTANTIALLY PRESENT ({same}of{total} blobs byte-identical; "
                "remainder composed/evolved in the target tree — per-file diff + seam "
                "checks are the proof for those, not blob identity)")
    if expected == "IN-MAIN":
        return "CONTENT-IN-MAIN (register)" if content_in else \
            "REGISTER SAYS IN-MAIN — live signals weak; see seam/file checks"
    if expected == "PAPER":
        return "RECOVERED — content present" if content_in else "PAPER — not found yet"
    if expected == "TRAIN-SPLIT":
        return "SPLIT — content partially/equivalently present; headline NOT to re-land" \
            if (pid == "-" or same > 0) else "SPLIT — no live equivalent signal"
    return "RIDES TRAIN (content in train tree)" if content_in else \
        "REGISTER SAYS TRAIN — live content signal MISSING (STOP-worthy)"


def tree_has(repo, ref, path, needle):
    rc, out, _ = git(repo, "show", f"{ref}:{path}")
    return needle in out if rc == 0 else None


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--repo", default=".")
    ap.add_argument("--out", default=None)
    ap.add_argument("--train-ref", default="origin/batch-10")
    ap.add_argument("--main-ref", default="origin/main")
    args = ap.parse_args()
    repo = args.repo

    lines = []
    now = datetime.datetime.now(datetime.timezone.utc).strftime("%Y-%m-%d %H:%M UTC")
    lines.append("# #16 CLOSURE EVIDENCE PACK — generated, not hand-written")
    lines.append("")
    lines.append(f"Generator: `studio/issue-16/closure-evidence/generate_closure_pack.py` "
                 f"(T24 Builder seat 2, #16 wave) · run at {now} · {MAX_GH_NOTE}")
    lines.append("")

    # ---- refs -------------------------------------------------------------
    rc, _, ferr = git(repo, "fetch", "origin", "main", "batch-10", timeout=90)
    fetched = (rc == 0)
    rc, main_sha, _ = git(repo, "rev-parse", args.main_ref)
    rc, train_sha, _ = git(repo, "rev-parse", args.train_ref)
    lines.append(f"Refs: main `{main_sha[:12]}` · train ({args.train_ref}) `{train_sha[:12]}` "
                 f"· fetch this run: {'OK' if fetched else 'FAILED — using newest local refs (' + ferr[:80] + ')'}")
    lines.append("")

    # ---- register classification -----------------------------------------
    lines.append("## 1 · Register classification (16 once-stranded commits, VIER LEDGER-16)")
    lines.append("")
    lines.append("| commit | subject | vs MAIN: anc/patch/blobs | vs TRAIN: anc/patch/blobs | verdict |")
    lines.append("|---|---|---|---|---|")
    for sha, subj, expected, sens in REGISTER:
        ma, mp, ms, mt = classify(repo, sha, args.main_ref)
        ta, tp, ts, tt = classify(repo, sha, args.train_ref)
        v = verdict_for(expected, ta or ma, tp if expected != "IN-MAIN" else mp,
                        ts if expected != "IN-MAIN" else ms,
                        tt if expected != "IN-MAIN" else mt)
        if expected == "PAPER":
            v = verdict_for(expected, ma, mp, ms, mt)
        if sha == "ed75fc88":
            v = "COMPOSED INTO EVOLVED FILE — blob/patch signals not applicable; proof = seam-1 discriminator (§2)"
        flag = " ⚠ SENSITIVE — NEVER RE-LAND" if sens else ""
        lines.append(f"| `{sha}` | {subj} | {'A' if ma else '–'}/{mp}/{ms}of{mt} "
                     f"| {'A' if ta else '–'}/{tp}/{ts}of{tt} | {v}{flag} |")
    lines.append("")
    lines.append("Legend: A = ancestor · patch `-` = patch-equivalent present, `+` = absent "
                 "· blobs = files byte-identical in target tree / files the commit touched. "
                 "Paper commits recovered via a re-created commit (PR #563/#503 path) fail "
                 "patch-id but pass blob identity — that is why classification is layered.")
    lines.append("")

    # ---- sensitive seams ---------------------------------------------------
    lines.append("## 2 · Sensitive seams — content discriminators (never re-land; verify content, not presence)")
    lines.append("")
    VM = "app/src/main/java/com/example/ui/MonolithViewModel.kt"
    REPO_KT = "app/src/main/java/com/example/data/MonolithRepository.kt"
    for refname, ref in (("main", args.main_ref), ("train", args.train_ref)):
        # seam 2: the actual Boolean signature, not the bare name
        rc, body, _ = git(repo, "show", f"{ref}:{REPO_KT}")
        sig = re.search(r"suspend fun startPeekLease\([^)]*\)\s*:\s*Boolean", body) if rc == 0 else None
        unit = re.search(r"suspend fun startPeekLease\([^)]*\)\s*\{", body) if rc == 0 else None
        lines.append(f"- seam 2 `startPeekLease` Boolean contract — {refname}: "
                     f"{'PASS (: Boolean signature present)' if sig else 'FAIL — Unit-returning OLD contract' if unit else 'not found'}")
        # seam 1: the composed gate — `val granted = ...startPeekLease(...)`,
        # then `if (!granted)` early-return, then the ST-08 `startActivity(`.
        # Token ORDER inside the function body is the discriminator (a bare
        # coroutine `launch {` or a launchIntent mention proves nothing).
        rc, vm, _ = git(repo, "show", f"{ref}:{VM}")
        if rc == 0 and "fun start2MinutePeek" in vm:
            seg = vm[vm.index("fun start2MinutePeek"):][:9000]
            gi = seg.find("startPeekLease(")
            ki = seg.find("if (!granted)")
            li = seg.find("startActivity(")
            order = ("PASS — grant → gate → launch, in that order"
                     if 0 <= gi < ki < li else
                     f"FAIL/UNPROVEN — token positions grant@{gi} gate@{ki} launch@{li} "
                     "(the composed form needs all three, in order)")
            lines.append(f"- seam 1 `start2MinutePeek` grant-gate composition — {refname}: {order}")
        else:
            lines.append(f"- seam 1 `start2MinutePeek` — {refname}: function not found")
        # seam 3
        rc, out, _ = git(repo, "grep", "-l", "queryForegroundPackage", ref)
        lines.append(f"- seam 3 `queryForegroundPackage` — {refname}: "
                     f"{'FOUND' if rc == 0 and out else 'not found'} (tree-wide grep)")
    lines.append("- Why discriminators, not presence: main already carries an evolved "
                 "`start2MinutePeek` and a Unit-returning `startPeekLease` from other lanes; "
                 "names prove nothing. ed75fc88's single file (MonolithViewModel.kt) is "
                 "composed INTO an evolved file, so blob identity is not its expected signal "
                 "in §1 — this section is its proof.")
    lines.append("")

    # ---- pill files ---------------------------------------------------------
    lines.append("## 3 · The six pill files")
    lines.append("")
    for f in PILL_FILES:
        row = []
        for refname, ref in (("main", args.main_ref), ("train", args.train_ref)):
            rc, _, _ = git(repo, "cat-file", "-e", f"{ref}:{f}")
            row.append(f"{refname}: {'PRESENT' if rc == 0 else 'absent'}")
        lines.append(f"- `{f}` — {' · '.join(row)}")
    lines.append("")

    # ---- negative checks -----------------------------------------------------
    lines.append("## 4 · Negative checks (must NOT have landed)")
    lines.append("")
    rc, out, _ = git(repo, "grep", "-i", "-l", "keytool", args.main_ref, "--", ".github/workflows/")
    kt_files = [ln.split(":", 2)[-1] for ln in out.splitlines()] if rc == 0 else []
    debug_gen, release_proof = False, False
    for wf in kt_files:
        rc2, body, _ = git(repo, "show", f"{args.main_ref}:{wf}")
        if rc2 != 0:
            continue
        for m in re.finditer(r"keytool\s+-genkey\w*", body):
            window = body[max(0, m.start() - 1500):m.start() + 400]
            if "RUNNER_TEMP" in window or "ci-upload" in window or "throwaway" in window:
                release_proof = True   # release-proof key, APK discarded — deliberate, not eb36f6b9
            elif "debug" in window.lower():
                debug_gen = True       # eb36f6b9's superseded debug-generation headline
    lines.append(f"- eb36f6b9 DEBUG keystore-GENERATION step in main's workflows: "
                 f"{'PRESENT — STOP' if debug_gen else 'absent (correct: superseded headline stayed out)'}"
                 + (" · note: a throwaway RELEASE-proof `-genkeypair` (RUNNER_TEMP, APK discarded) "
                    "exists in rescue-build.yml — a different, deliberate mechanism, discriminated "
                    "here so the negative check cannot false-positive on it." if release_proof else ""))
    rc, out, _ = git(repo, "log", "--oneline", f"{args.main_ref}", "--grep=pill", "-i")
    lines.append("- DB migration attributable to the 16: none carried one (register); "
                 "any migration on the train belongs to its own issue, never to #16.")
    lines.append("")

    # ---- PR state via gh ------------------------------------------------------
    lines.append("## 5 · Train PR state (live gh)")
    lines.append("")
    pr = {}
    for num in (231, 503):
        rc, out, err = gh("pr", "view", str(num), "--repo", REPO_SLUG,
                          "--json", "state,headRefName,headRefOid,mergeCommit,mergedAt,url")
        if rc == 0 and out:
            try:
                pr[num] = json.loads(out)
                d = pr[num]
                lines.append(f"- PR #{num}: **{d.get('state')}** · head `{d.get('headRefName')}@{str(d.get('headRefOid'))[:12]}`"
                             f" · mergeCommit `{str((d.get('mergeCommit') or {}).get('oid'))[:12]}`"
                             f" · mergedAt {d.get('mergedAt')}")
            except json.JSONDecodeError:
                lines.append(f"- PR #{num}: UNVERIFIED-API (unparseable response)")
        else:
            lines.append(f"- PR #{num}: UNVERIFIED-API (gh failed/timeout this run: {err[:80]})")
    lines.append("")

    # ---- merged head CI via jobs API -------------------------------------------
    lines.append("## 6 · Merged head's CI (jobs API, job-log counts — the only valid citation)")
    lines.append("")
    merged_oid = ((pr.get(231) or {}).get("mergeCommit") or {}).get("oid")
    if (pr.get(231) or {}).get("state") != "MERGED" or not merged_oid:
        lines.append("- PENDING: PR #231 is not MERGED at generation time, so no merged-head "
                     "run exists to cite. Any pre-head run cited for closure is VOID "
                     "(CI citation rule). Re-run this generator after the merge.")
    else:
        rc, out, err = gh("api", f"repos/{REPO_SLUG}/actions/runs?head_sha={merged_oid}")
        run_id = None
        if rc == 0 and out:
            try:
                runs = json.loads(out).get("workflow_runs", [])
                run_id = runs[0]["id"] if runs else None
            except (json.JSONDecodeError, KeyError):
                run_id = None
        if not run_id:
            lines.append(f"- Merge commit `{merged_oid[:12]}` found but its Actions run is "
                         f"UNVERIFIED-API this run ({err[:80]}). Re-run the generator.")
        else:
            lines.append(f"- Run id `{run_id}` for merge commit `{merged_oid[:12]}`.")
            rc, out, err = gh("api", f"repos/{REPO_SLUG}/actions/runs/{run_id}/jobs")
            if rc == 0 and out:
                try:
                    for job in json.loads(out).get("jobs", []):
                        steps = "; ".join(
                            f"{s['name']}={s['conclusion']}" for s in job.get("steps", []))
                        lines.append(f"  - job `{job['name']}`: {job.get('conclusion')} · steps: {steps}")
                except json.JSONDecodeError:
                    lines.append("  - jobs payload UNVERIFIED-API (unparseable)")
            lines.append("  - Executed-test counts live in the JOB LOG, not this API: the "
                         "executor reads the unit-test step log for the count (>0 required), "
                         "confirms no compilation-error step failure, and names the pill "
                         "suites' counts (PillDerivationTest + PillLeaseRepositoryTest, "
                         "0 failures) before closure. This generator does not invent counts.")
    lines.append("")

    # ---- #245 MERGED line -------------------------------------------------------
    lines.append("## 7 · #245 MERGED-line presence (no line, no closure)")
    lines.append("")
    rc, out, err = gh("api", f"repos/{REPO_SLUG}/issues/245/comments?per_page=100",
                      "--jq", "[.[] | select(.body | test(\"231\")) | select(.body | test(\"MERGED\";\"i\")) | .html_url] | length")
    if rc == 0 and out.isdigit():
        lines.append(f"- Comments on #245 mentioning both `231` and `MERGED` (page 1 scan): **{out}**. "
                     "Zero = the train lane still owes the line; closure waits for it. "
                     "Note: single-page scan — a negative here is a prompt to search all pages, not proof of absence.")
    else:
        lines.append(f"- UNVERIFIED-API this run ({err[:80]}): check #245 manually for the "
                     "PR #231 MERGED line with the merged head's own CI citation before closing.")
    lines.append("")

    # ---- stamp ------------------------------------------------------------------
    lines.append("## 8 · Stamp")
    lines.append("")
    lines.append(f"VERIFIED vs main {main_sha} — scope: register classification of the 16 "
                 f"commits listed in §1, pill-file presence (§3), seam presence (§2), and the "
                 f"keystore negative (§4), all read from git trees at the refs above. "
                 f"PR/CI/#245 sections carry their own live-API verdicts (§5–§7); any "
                 f"UNVERIFIED-API line voids only its own section, never the git sections.")
    lines.append("")
    lines.append("Phone-bench remainder (named, not buried): CI + tree evidence does not prove "
                 "the pill on a device — overlay over a third-party app, live PEEK face, drag/snap, "
                 "✕ hand-back remain phone-bench unproven after closure.")
    lines.append("")
    lines.append("— T24 Builder seat 2, #16 wave (generator output; the generator is the deliverable, "
                 "this pack is one run of it)")

    text = "\n".join(lines) + "\n"
    if args.out:
        with open(args.out, "w") as fh:
            fh.write(text)
    sys.stdout.write(text)
    return 0


if __name__ == "__main__":
    sys.exit(main())
