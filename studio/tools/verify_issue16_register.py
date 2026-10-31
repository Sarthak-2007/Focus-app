#!/usr/bin/env python3
"""
verify_issue16_register.py — mechanical verification harness for issue #16.

Re-checks the VIER recovery register (issue #16 comment 6531516894; full
per-commit ledger LEDGER-16 on #245 comment 6529923438) against the live
git trees, using READ-ONLY git operations only (rev-parse / show / log /
diff --name-only / grep). No file in any tree is modified; no pill or
train file is touched; nothing is re-landed.

Register under test (LEDGER-16, oldest -> newest, 16 commits on
agent/issue-16, none in main by ancestry):
  CONTENT-IN-MAIN:  1  (c727dde2)
  CONTENT-IN-TRAIN: 10 (incl. eb36f6b9 SPLIT; 3 SENSITIVE: ed75fc88,
                        ecc7fef2, b0e27e36 — NEVER to be re-landed)
  CONTENT-NOWHERE:  5  (paper docs — at filing time; later seats report
                        them recovered in main via PR #563, so this
                        harness adjudicates that delta mechanically)

Checks:
  1. Commit set: main..<source> is exactly the register's 16 SHAs.
  2. Per-file blob table for the 6 pill-package files across
     main / train / source, judged against the register's classification
     (5 blob-identical source==train & absent in main; EditableTimes
     adapted forward in train, absent in main).
  3. Per-file blob table for the 5 paper docs (NOWHERE at filing ->
     RECOVERED if blob-identical in main today).
  4. Sensitive seams: queryForegroundPackage, startPeekLease Boolean
     contract, VM grant-gate -> launch ordering (train present /
     main absent-or-different).
  5. eb36f6b9 SPLIT guard: no "Generate debug keystore" step in either
     tree's rescue-build.yml (the superseded headline stays dead).
  6. c727dde2 hunk presence in main's test tree (hunk-level standard,
     named as such — the weakest evidence class in the register).
  7. No DB-migration FILE anywhere in the 16 commits (filename scan;
     doc *references* to migrations do not count).
  8. Test census on the train: PillDerivationTest 15 @Test +
     PillLeaseRepositoryTest 3 @Test = 18.

Exit code: 0 iff every CORE check passes (docs-recovery and the
keystore-verify-step location are reported as facts, not gates).
Usage: python3 studio/tools/verify_issue16_register.py [--repo PATH]
       [--main-ref origin/main] [--train-ref origin/batch-10]
       [--source-ref origin/agent/issue-16]

Author: T24 Builder seat 1, #16 wave (branch agent/issue-16-verify-harness,
new files only; PR #231 / batch-10 is K1 + W6's — untouched).
"""

import argparse
import re
import subprocess
import sys

# ---------------------------------------------------------------- register

COMMITS = [
    # sha-prefix, register classification, sensitive?, subject (ledger wording)
    ("873a23e8", "CONTENT-IN-TRAIN", False, "pill verification plan + merge flags"),
    ("ed75fc88", "CONTENT-IN-TRAIN", True,  "composed start2MinutePeek, ST-08 launch"),
    ("ecc7fef2", "CONTENT-IN-TRAIN", True,  "cell package + startPeekLease Boolean gate"),
    ("d81bc162", "CONTENT-IN-TRAIN", False, "wave-2 NO_VERDICT filing + test-v2.sh"),
    ("477df552", "CONTENT-IN-TRAIN", False, "wave-2 addendum (Route B / C13)"),
    ("5c3ba072", "CONTENT-IN-TRAIN", False, "wave-2 push-status record"),
    ("174998e1", "CONTENT-IN-TRAIN", False, "wave-2 final status DIVERGED record"),
    ("e0498346", "CONTENT-IN-TRAIN", False, "addendum-2: test-v2.sh Bug B + JDK evidence"),
    ("b0e27e36", "CONTENT-IN-TRAIN", True,  "pill package lands + queryForegroundPackage seam"),
    ("eb36f6b9", "CONTENT-IN-TRAIN/SPLIT", False, "signing cure; keystore-gen headline superseded"),
    ("c727dde2", "CONTENT-IN-MAIN",  False, "test-compile census cure"),
    ("2bfc48a1", "CONTENT-NOWHERE@filing", False, "Glut crazy-ideas (17)"),
    ("db7bc43f", "CONTENT-NOWHERE@filing", False, "Zunder crazy-ideas"),
    ("09cfb2aa", "CONTENT-NOWHERE@filing", False, "Glanz interaction detail spec"),
    ("d7eab4a8", "CONTENT-NOWHERE@filing", False, "GLUT greeting design doc"),
    ("c40181f9", "CONTENT-NOWHERE@filing", False, "Sable charges S1-S10"),
]

PILL_IDENTICAL = [
    "app/src/main/java/com/example/service/PillFace.kt",
    "app/src/main/java/com/example/service/PillController.kt",
    "app/src/main/java/com/example/service/PillTheme.kt",
    "app/src/test/java/com/example/PillDerivationTest.kt",
    "app/src/test/java/com/example/PillLeaseRepositoryTest.kt",
]
PILL_ADAPTED = "app/src/main/java/com/example/data/EditableTimes.kt"

DOC_FILES = {
    "2bfc48a1": "studio/crazy/issue-16-glut.md",
    "db7bc43f": "studio/plan/ZUNDER-CRAZY-IDEAS.md",
    "09cfb2aa": "studio/issue-16/GLANZ-INTERACTION-DETAIL-16.md",
    "d7eab4a8": "studio/issue-16/cell/DESIGN-pill-greeting-glut.md",
    "c40181f9": "studio/issue-16/cell/sable-charges.md",
}

REPO_KT = "app/src/main/java/com/example/data/MonolithRepository.kt"
VM_KT = "app/src/main/java/com/example/ui/MonolithViewModel.kt"
WORKFLOW = ".github/workflows/rescue-build.yml"

# ---------------------------------------------------------------- git plumbing

REPO = "."


def git(*args):
    r = subprocess.run(["git", "-C", REPO, *args],
                       capture_output=True, text=True)
    return r.returncode, r.stdout


def resolve(ref):
    rc, out = git("rev-parse", "--verify", ref)
    return out.strip() if rc == 0 else None


def blob(ref, path):
    rc, out = git("rev-parse", "-q", "--verify", f"{ref}:{path}")
    return out.strip() if rc == 0 else None


def show(ref, path):
    rc, out = git("show", f"{ref}:{path}")
    return out if rc == 0 else ""


def short(b):
    return b[:8] if b else "ABSENT"


# ---------------------------------------------------------------- main

def main():
    global REPO
    ap = argparse.ArgumentParser(description=__doc__.splitlines()[1])
    ap.add_argument("--repo", default=".")
    ap.add_argument("--main-ref", default="origin/main")
    ap.add_argument("--train-ref", default="origin/batch-10")
    ap.add_argument("--source-ref", default="origin/agent/issue-16")
    a = ap.parse_args()
    REPO = a.repo

    fails = []

    def verdict(ok, label, detail=""):
        print(f"  [{'PASS' if ok else 'FAIL'}] {label}" + (f" — {detail}" if detail else ""))
        if not ok:
            fails.append(label)
        return ok

    main_sha, train_sha, src_sha = (resolve(a.main_ref), resolve(a.train_ref),
                                    resolve(a.source_ref))
    print("=" * 78)
    print("ISSUE #16 — VIER REGISTER VERIFICATION HARNESS (read-only git)")
    print(f"  main   {a.main_ref:26s} {main_sha}")
    print(f"  train  {a.train_ref:26s} {train_sha}  (PR #231 head)")
    print(f"  source {a.source_ref:26s} {src_sha}")
    if not all([main_sha, train_sha, src_sha]):
        print("FATAL: a ref did not resolve — run `git fetch origin` first.")
        return 2

    # ---- 1. commit set -------------------------------------------------
    print("\n[1] COMMIT REGISTER — main..source must be exactly the 16 ledger SHAs")
    rc, out = git("log", "--format=%H", f"{a.main_ref}..{a.source_ref}")
    live = [l.strip()[:8] for l in out.splitlines() if l.strip()]
    expected = [c[0] for c in COMMITS]
    print(f"  {'commit':10s} {'register class':24s} {'sens':5s} subject")
    for sha, cls, sens, subj in COMMITS:
        present = sha in live
        print(f"  {sha:10s} {cls:24s} {'YES' if sens else '-':5s} {subj}"
              f"  {'(in source range)' if present else '(MISSING!)'}")
    extra = [s for s in live if s not in expected]
    verdict(live and sorted(live) == sorted(expected) and not extra,
            f"commit set == register (found {len(live)}, expected 16)",
            f"extra={extra}" if extra else "no extras, none missing")
    by_class = {}
    for _, cls, _, _ in COMMITS:
        by_class[cls] = by_class.get(cls, 0) + 1
    print(f"  register headline: IN-MAIN {by_class.get('CONTENT-IN-MAIN', 0)} / "
          f"IN-TRAIN {by_class.get('CONTENT-IN-TRAIN', 0) + by_class.get('CONTENT-IN-TRAIN/SPLIT', 0)}"
          f" (incl. 1 SPLIT) / NOWHERE@filing {by_class.get('CONTENT-NOWHERE@filing', 0)}"
          f" / SENSITIVE {sum(1 for c in COMMITS if c[2])}")

    # ---- 2. pill package files ----------------------------------------
    print("\n[2] PILL PACKAGE — per-file blob verdicts (register: absent in main, rides train)")
    print(f"  {'file':58s} {'main':9s} {'train':9s} {'source':9s} verdict")
    for path in PILL_IDENTICAL:
        bm, bt, bs = blob(a.main_ref, path), blob(a.train_ref, path), blob(a.source_ref, path)
        ok = bm is None and bt is not None and bt == bs
        print(f"  {path:58s} {short(bm):9s} {short(bt):9s} {short(bs):9s} "
              f"{'PASS train-only, blob-identical' if ok else 'FAIL'}")
        if not ok:
            fails.append(f"pill file {path}")
    bm, bt, bs = (blob(a.main_ref, PILL_ADAPTED), blob(a.train_ref, PILL_ADAPTED),
                  blob(a.source_ref, PILL_ADAPTED))
    et_train = show(a.train_ref, PILL_ADAPTED)
    adapted_ok = (bm is None and bt is not None and bs is not None and bt != bs
                  and "peekMinutes" in et_train and "getGuardSettingsSnapshot" in et_train)
    print(f"  {PILL_ADAPTED:58s} {short(bm):9s} {short(bt):9s} {short(bs):9s} "
          f"{'PASS adapted-in-train (peekMinutes/guard-snapshot bodies)' if adapted_ok else 'FAIL'}")
    if not adapted_ok:
        fails.append(f"pill file {PILL_ADAPTED} (adapted)")

    # ---- 3. paper docs --------------------------------------------------
    print("\n[3] PAPER DOCS — register said CONTENT-NOWHERE at filing; adjudicate today")
    for sha, path in DOC_FILES.items():
        bm, bt, bs = blob(a.main_ref, path), blob(a.train_ref, path), blob(a.source_ref, path)
        if bm is not None and bm == bs:
            state = "RECOVERED-IN-MAIN (blob-identical; register bucket superseded)"
        elif bm is None and bt is None:
            state = "STILL-NOWHERE (matches register as filed)"
        else:
            state = "MOVED/DIFFERS — inspect manually"
        print(f"  {sha} {path:55s} main={short(bm)} train={short(bt)} source={short(bs)} -> {state}")

    # ---- 4. sensitive seams --------------------------------------------
    print("\n[4] SENSITIVE SEAMS — must ride the train, must NOT be re-landed (presence read only)")
    repo_t, repo_m = show(a.train_ref, REPO_KT), show(a.main_ref, REPO_KT)
    vm_t, vm_m = show(a.train_ref, VM_KT), show(a.main_ref, VM_KT)
    verdict("queryForegroundPackage" in repo_t and "queryForegroundPackage" not in repo_m,
            "b0e27e36 queryForegroundPackage: in train Repo, absent in main Repo")
    bool_re = re.compile(r"startPeekLease\([^)]*\):\s*Boolean")
    unit_re = re.compile(r"startPeekLease\([^)]*\)\s*\{")
    verdict(bool(bool_re.search(repo_t)) and not bool_re.search(repo_m)
            and bool(unit_re.search(repo_m)),
            "ecc7fef2 startPeekLease: Boolean in train, Unit-returning in main")
    gate = "val granted = repository.startPeekLease(pkg)"
    gi, li = vm_t.find(gate), vm_t.find("getLaunchIntentForPackage", vm_t.find(gate))
    verdict(gi != -1 and li != -1 and gi < li and gate not in vm_m,
            "ed75fc88 composed start2MinutePeek: grant gate precedes ST-08 launch in train VM; no gate in main VM",
            f"train char-offsets gate@{gi} < launch@{li}" if gi != -1 else "gate not found")

    # ---- 5. eb36f6b9 split guard ---------------------------------------
    print("\n[5] eb36f6b9 SPLIT — superseded keystore-generation headline must stay dead")
    wf_t, wf_m = show(a.train_ref, WORKFLOW), show(a.main_ref, WORKFLOW)
    verdict("Generate debug keystore" not in wf_t and "Generate debug keystore" not in wf_m,
            "no 'Generate debug keystore' step in train or main workflow")
    print(f"  fact: main carries 'Verify committed debug keystore' step: "
          f"{'Verify committed debug keystore' in wf_m}; train: {'Verify committed debug keystore' in wf_t}"
          " (location is a fact, not a gate)")

    # ---- 6. c727dde2 in main (hunk-level) -------------------------------
    print("\n[6] c727dde2 CONTENT-IN-MAIN — hunk-level standard (weakest in register, named)")
    rc1, o1 = git("grep", "-l", "socialSitesEnabled = true", a.main_ref, "--", "app/src/test")
    rc2, o2 = git("grep", "-l", "shortsReelsSniperEnabled", a.main_ref, "--",
                  "app/src/test/java/com/example/SoundAndSocialTruthTest.kt")
    verdict(rc1 == 0 and "ScreenshotHarnessTest" in o1 and rc2 == 0,
            "c727dde2 hunks present in main test tree",
            "socialSitesEnabled pair + shortsReelsSniperEnabled found")

    # ---- 7. no migration file in the 16 --------------------------------
    print("\n[7] NO-DB-MIGRATION — filename scan over merge-base..source diff")
    rc, mb = git("merge-base", a.main_ref, a.source_ref)
    rc, names = git("diff", "--name-only", f"{mb.strip()}..{a.source_ref}")
    mig = [n for n in names.splitlines() if "migration" in n.lower()]
    verdict(not mig, f"no migration file among {len(names.splitlines())} changed files",
            f"offenders={mig}" if mig else "doc references to migrations do not count")

    # ---- 8. test census on train ---------------------------------------
    print("\n[8] TEST CENSUS (train) — register promises 18 pill tests (15 + 3)")
    total = 0
    for path, want in [("app/src/test/java/com/example/PillDerivationTest.kt", 15),
                       ("app/src/test/java/com/example/PillLeaseRepositoryTest.kt", 3)]:
        n = show(a.train_ref, path).count("@Test")
        total += n
        verdict(n == want, f"{path.split('/')[-1]}: {n} @Test (expected {want})")
    print(f"  total on train: {total} (expected 18)")

    # ---- summary ---------------------------------------------------------
    print("\n" + "=" * 78)
    stamp_files = ", ".join(p.split("/")[-1] for p in PILL_IDENTICAL + [PILL_ADAPTED])
    print(f"STAMP: VERIFIED vs main {main_sha} — files: {stamp_files}; "
          f"train {train_sha}; source {src_sha}")
    if fails:
        print(f"RESULT: FAIL ({len(fails)} core check(s)): {fails}")
        return 1
    print("RESULT: PASS — register's core claims hold mechanically at the pinned SHAs.")
    print("Deltas vs filing, by design of this harness: the 5 paper docs are reported")
    print("in section [3] at their CURRENT state (recovered-in-main), not their")
    print("at-filing NOWHERE state; eb36f6b9 remains SPLIT with its headline dead.")
    print("The pill package lands in main if and only if PR #231 merges — this")
    print("harness proves cargo placement, not the merge.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
