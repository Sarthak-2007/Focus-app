# #16 CLOSURE EVIDENCE PACK — generated, not hand-written

Generator: `studio/issue-16/closure-evidence/generate_closure_pack.py` (T24 Builder seat 2, #16 wave) · run at 2026-10-31 01:37 UTC · max 2 gh calls in flight: this script is strictly sequential

Refs: main `30ce1b41423a` · train (origin/batch-10) `833b8d407225` · fetch this run: OK

## 1 · Register classification (16 once-stranded commits, VIER LEDGER-16)

| commit | subject | vs MAIN: anc/patch/blobs | vs TRAIN: anc/patch/blobs | verdict |
|---|---|---|---|---|
| `873a23e8` | verification plan + merge flags (Pastille cell) | –/+/0of2 | –/+/2of2 | RIDES TRAIN (content in train tree) |
| `ed75fc88` | composed start2MinutePeek (Suki guard + ST-08) | –/+/0of1 | –/+/0of1 | COMPOSED INTO EVOLVED FILE — blob/patch signals not applicable; proof = seam-1 discriminator (§2) ⚠ SENSITIVE — NEVER RE-LAND |
| `ecc7fef2` | full cell package + startPeekLease Boolean fix | –/+/0of16 | –/+/13of16 | CONTENT SUBSTANTIALLY PRESENT (13of16 blobs byte-identical; remainder composed/evolved in the target tree — per-file diff + seam checks are the proof for those, not blob identity) ⚠ SENSITIVE — NEVER RE-LAND |
| `d81bc162` | Wave-2: NO_VERDICT filed, test-v2.sh | –/+/0of2 | –/+/0of2 | REGISTER SAYS TRAIN — live content signal MISSING (STOP-worthy) |
| `477df552` | Wave-2 addendum: Route B bypass, C13 ghost clock | –/+/0of1 | –/+/0of1 | REGISTER SAYS TRAIN — live content signal MISSING (STOP-worthy) |
| `5c3ba072` | Wave-2: push status record | –/+/0of1 | –/+/0of1 | REGISTER SAYS TRAIN — live content signal MISSING (STOP-worthy) |
| `174998e1` | Wave-2: final status DIVERGED record | –/+/0of1 | –/+/0of1 | REGISTER SAYS TRAIN — live content signal MISSING (STOP-worthy) |
| `e0498346` | Wave-2 addendum-2: test-v2.sh Bug B + JDK evidence | –/+/0of2 | –/+/2of2 | RIDES TRAIN (content in train tree) |
| `b0e27e36` | pill package lands: PillFace/Controller/Theme, 18 tests | –/+/0of7 | –/+/5of7 | CONTENT SUBSTANTIALLY PRESENT (5of7 blobs byte-identical; remainder composed/evolved in the target tree — per-file diff + seam checks are the proof for those, not blob identity) ⚠ SENSITIVE — NEVER RE-LAND |
| `eb36f6b9` | issue #3 wave-2: signing cure in real workflow | –/+/0of2 | –/+/1of2 | CONTENT SUBSTANTIALLY PRESENT (1of2 blobs byte-identical; remainder composed/evolved in the target tree — per-file diff + seam checks are the proof for those, not blob identity) ⚠ SENSITIVE — NEVER RE-LAND |
| `c727dde2` | last mile: test-compile census cure (test files only) | –/+/0of5 | –/+/0of5 | REGISTER SAYS IN-MAIN — live signals weak; see seam/file checks |
| `2bfc48a1` | Glut crazy-ideas file (17 ideas) | –/+/1of1 | –/+/1of1 | RECOVERED — content present |
| `db7bc43f` | Zunder crazy-ideas for #16 | –/+/1of1 | –/+/1of1 | RECOVERED — content present |
| `09cfb2aa` | Glanz interaction detail | –/+/1of1 | –/+/1of1 | RECOVERED — content present |
| `d7eab4a8` | GLUT enthusiastic designer: pill greeting/arrival | –/+/1of1 | –/+/1of1 | RECOVERED — content present |
| `c40181f9` | Sable brutal-critic charges S1-S10 | –/+/1of1 | –/+/1of1 | RECOVERED — content present |

Legend: A = ancestor · patch `-` = patch-equivalent present, `+` = absent · blobs = files byte-identical in target tree / files the commit touched. Paper commits recovered via a re-created commit (PR #563/#503 path) fail patch-id but pass blob identity — that is why classification is layered.

## 2 · Sensitive seams — content discriminators (never re-land; verify content, not presence)

- seam 2 `startPeekLease` Boolean contract — main: FAIL — Unit-returning OLD contract
- seam 1 `start2MinutePeek` grant-gate composition — main: FAIL/UNPROVEN — token positions grant@2984 gate@-1 launch@4229 (the composed form needs all three, in order)
- seam 3 `queryForegroundPackage` — main: not found (tree-wide grep)
- seam 2 `startPeekLease` Boolean contract — train: PASS (: Boolean signature present)
- seam 1 `start2MinutePeek` grant-gate composition — train: PASS — grant → gate → launch, in that order
- seam 3 `queryForegroundPackage` — train: FOUND (tree-wide grep)
- Why discriminators, not presence: main already carries an evolved `start2MinutePeek` and a Unit-returning `startPeekLease` from other lanes; names prove nothing. ed75fc88's single file (MonolithViewModel.kt) is composed INTO an evolved file, so blob identity is not its expected signal in §1 — this section is its proof.

## 3 · The six pill files

- `app/src/main/java/com/example/service/PillController.kt` — main: absent · train: PRESENT
- `app/src/main/java/com/example/service/PillFace.kt` — main: absent · train: PRESENT
- `app/src/main/java/com/example/service/PillTheme.kt` — main: absent · train: PRESENT
- `app/src/main/java/com/example/data/EditableTimes.kt` — main: absent · train: PRESENT
- `app/src/test/java/com/example/PillDerivationTest.kt` — main: absent · train: PRESENT
- `app/src/test/java/com/example/PillLeaseRepositoryTest.kt` — main: absent · train: PRESENT

## 4 · Negative checks (must NOT have landed)

- eb36f6b9 DEBUG keystore-GENERATION step in main's workflows: absent (correct: superseded headline stayed out) · note: a throwaway RELEASE-proof `-genkeypair` (RUNNER_TEMP, APK discarded) exists in rescue-build.yml — a different, deliberate mechanism, discriminated here so the negative check cannot false-positive on it.
- DB migration attributable to the 16: none carried one (register); any migration on the train belongs to its own issue, never to #16.

## 5 · Train PR state (live gh)

- PR #231: **OPEN** · head `batch-10@833b8d407225` · mergeCommit `None` · mergedAt None
- PR #503: **OPEN** · head `agent/issue-16-recovery@921529d0bd2a` · mergeCommit `None` · mergedAt None

## 6 · Merged head's CI (jobs API, job-log counts — the only valid citation)

- PENDING: PR #231 is not MERGED at generation time, so no merged-head run exists to cite. Any pre-head run cited for closure is VOID (CI citation rule). Re-run this generator after the merge.

## 7 · #245 MERGED-line presence (no line, no closure)

- Comments on #245 mentioning both `231` and `MERGED` (page 1 scan): **9**. Zero = the train lane still owes the line; closure waits for it. Note: single-page scan — a negative here is a prompt to search all pages, not proof of absence.

## 8 · Stamp

VERIFIED vs main 30ce1b41423ac656f3493d6963f42af613fbaad2 — scope: register classification of the 16 commits listed in §1, pill-file presence (§3), seam presence (§2), and the keystore negative (§4), all read from git trees at the refs above. PR/CI/#245 sections carry their own live-API verdicts (§5–§7); any UNVERIFIED-API line voids only its own section, never the git sections.

Phone-bench remainder (named, not buried): CI + tree evidence does not prove the pill on a device — overlay over a third-party app, live PEEK face, drag/snap, ✕ hand-back remain phone-bench unproven after closure.

— T24 Builder seat 2, #16 wave (generator output; the generator is the deliverable, this pack is one run of it)
