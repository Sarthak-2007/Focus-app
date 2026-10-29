# SNAPSHOT SPEC — preservation snapshots of the Monolith studio

*Filed by Anker (Keepers of Record, preservation seat) — Oct 29, 2026, 10:20 IST.*
*Authority: KEEPERS-CHARTER.md, standing duty 5 (preservation law): "no
deliverable exists in only one place… a snapshot that was never
restore-tested does not count."*

**Placement note for Protokoll:** this spec was briefed for
`plan/WORK-REGISTRY.md`, but that file does not exist yet — Register has
not created it (checked on disk 10:19 IST, Oct 29; `plan/REGISTER.md` is
Sana's constants register, a different document). Filed here as
`plan/SNAPSHOT-SPEC.md` instead, per the brief's fallback. Protokoll to
reconcile: when WORK-REGISTRY.md is created, the snapshot log (§7) moves
into it and this spec is linked from it.

**Ownership:** Ren runs the FIRST snapshot personally (Oct 29, this hour,
his read-and-preserve pass on Sarthak's order). Anker owns every snapshot
from the NEXT one on — taking them, verifying them, registering them,
and reporting failures. This spec governs both: Ren's first snapshot is
registered under it retroactively once Anker receives its location +
manifest path.

---

## 1. WHAT — the snapshot set

One snapshot = one timestamped directory (§3 naming) containing the
following five components. A snapshot missing any component is
INCOMPLETE and does not count.

### 1a. Swarm tree — the studio's mind
Path: `~/workspace/clone-inspect/design-refs/swarm/`
Include, in full:
- `plan/` — every plan, brief, ruling, register, ledger, this spec
- `mail/` — every inbox, including `read/` subfolders (read mail is
  record, never deleted, therefore always snapshotted)
- `COMMONS.md` — the wall, complete
- `tools/` — `studio_check.py`, `constants.json`, and any tool added later
- All cohort trees and seat papers (`concept/`, `critics/`, `designers/`,
  `humans/`, `improvers/`, `maximpl/`, `wild/`, `invention/`, and the
  top-level cohort `.md` files, `SYNTHESIS.md`)
Exclude: `scratch*/` render/scratch dirs **only if** regenerable from a
filed spec; when in doubt, include — the whole swarm tree is ~22 MB,
cheap enough that exclusion arguments lose by default.

### 1b. Trunk source — the app itself
Path: `~/workspace/clone-inspect/Focus-app/` (the trunk)
Include: all source and build definition — `app/src/`, `app/build.gradle.kts`,
root `build.gradle.kts`, `settings.gradle.kts`, `gradle/`, `gradle.properties`,
`metadata.json`, `public/`, `debug.keystore`.
Exclude (build outputs, regenerable): `app/build/`, `build/`,
`.gradle/`, `local.properties`, any `*.apk` inside the tree (APKs are
component 1e, copied from their canonical locations, not scraped from
build dirs).

### 1c. Lane source trees — parallel build lanes
Paths: `~/workspace/clone-inspect/builds/<lane>/` for every lane present
at snapshot time. At spec time the lanes are: `counterfoil`, `drawer`,
`routines`, `shieldpick`, `slide`, `token`. New lanes are included
automatically — the rule is "every directory under `builds/` that holds
a `Focus-app/` tree", never a hardcoded list.
Per lane include: its `Focus-app/` source tree (same include/exclude
rules as 1b), `env.sh` / `env21.sh`, lane letters, drafts, proof logs,
and `shots-own/` renders.
Exclude (per-lane build outputs): the lane's `guh/` (its private Gradle
user home — caches and daemons, pure weight), every `build/` and
`.gradle/` inside the lane tree, `tmp/`. The `builds/` tree is ~11 GB
with outputs and a small fraction of that without; outputs are
regenerable from source + the trunk toolchain, sources are not.

### 1d. Root records — the clone-inspect ledger
Paths: direct children of `~/workspace/clone-inspect/`:
- `BUILD-CHANGES.md` (mandatory — named in the brief)
- `DEAD-CODE-AUDIT.md`, `REMOVAL-LOG.md`, `VERDICT.md`
- The one-shot briefs (`monolith-brief-*.md`, `monolith-correction-brief.md`)
- `env.sh`, `env21.sh`
- Build/test logs (`build-*.log`, `test-*.log`) — small, and they are
  the only proof of what was compiled and tested when
Exclude: `tools/` toolchain binaries under clone-inspect (JDK, Gradle
distributions, SDK zips — re-downloadable, gigabytes), `trunk-guh/`,
`fonts-dl/`, `shots/` and `*.jpg` screenshots (renders live with their
lanes; trunk renders are regenerable).

### 1e. APKs — the shippable artefacts
Every `monolith-*.apk` at `~/workspace/clone-inspect/` root (at spec
time this includes `monolith-final-debug.apk`, the current accepted
build) plus any APK a lane has produced that Ren has named as a
deliverable. APKs are copied, never moved — the originals stay live.

---

## 2. WHERE ×2 — two physically separate locations

This workspace has lost whole directory trees twice (Oct 4: the
erotica-stream goal dir; Oct 10: the claude-for-startups goal dir — both
vanished, survivors only under `your_files/deleted-goal-files/`). Both
losses struck inside a single tree while sibling trees survived. So the
two copies must live in **different top-level trees**, neither nested
inside the other, neither inside `clone-inspect/` (the tree being
preserved cannot preserve itself), and neither inside `goals/` (the
tree with the vanishing history).

- **Location A — primary:**
  `~/workspace/preservation/monolith-snapshots/`
  A new top-level workspace tree owned by preservation. Nothing else
  writes here. Snapshots land here first.
- **Location B — mirror:**
  `~/workspace/your_files/monolith-archive/snapshots/`
  A different top-level tree (`your_files/` — the tree that has
  historically *survived* deletions and held the recovered copies).
  Written second, from Location A's verified copy, never directly
  from the live sources — so B mirrors a checksummed A, not a
  second, possibly divergent, read of a moving tree.

"Physically separate" here means: separate directory trees with no
shared parent below `~/workspace`, no symlinks between them (a symlink
makes two paths one location — forbidden for snapshot dirs), and
separate write operations. If either location is ever found to be a
symlink or bind of the other, both copies count as ONE and the
snapshot is downgraded to UNVERIFIED until a true second copy exists.

---

## 3. Naming and layout

```
<location>/2026-10-29_1430_merge-shieldpick/
    swarm/            (1a)
    trunk/            (1b)
    lanes/<lane>/     (1c)
    records/          (1d)
    apks/             (1e)
    MANIFEST.sha256   (§4)
    SNAPSHOT.md       (trigger, operator, source HEAD/state notes,
                       file count, total bytes, restore-test result §5)
```
Name format: `<YYYY-MM-DD>_<HHMM-IST>_<trigger>` where trigger is
`merge-<lane>`, `daily`, or `manual-<reason>`. Names sort
chronologically; no snapshot directory is ever renamed or edited after
its restore test passes — corrections are a new snapshot.

## 4. CHECKSUMS — sha256 manifest per snapshot

- Every file in the snapshot gets one line in `MANIFEST.sha256`:
  `<sha256>  <path relative to snapshot root>`, generated with
  `sha256sum` at copy time, sorted by path.
- `MANIFEST.sha256` itself is excluded from its own manifest; instead
  SNAPSHOT.md records the manifest's own sha256.
- Verification of a copy = re-run `sha256sum -c MANIFEST.sha256`;
  Location B is verified against Location A's manifest — B must match
  A's manifest byte-for-byte before B counts.
- Any checksum mismatch = the copy does not exist, for registry
  purposes. It is re-copied or struck, never explained away.

## 5. RESTORE TEST — a snapshot counts only after this passes

Per the charter, a snapshot that was never restore-tested does not
count. Procedure, run by the snapshot's owner before registering it:

1. Create a scratch dir **outside both snapshot locations**:
   `~/workspace/preservation/restore-test/<snapshot-name>/`
2. Copy the snapshot from **Location B** (test the mirror, not the
   primary — B is the copy that would actually be used if A and the
   live tree die together) into the scratch dir.
3. `sha256sum -c MANIFEST.sha256` inside the restored copy — must be
   100% OK, zero failures.
4. Diff the restored tree against the manifest's file list: file
   count and total bytes must match SNAPSHOT.md exactly (the manifest
   check in step 3 already proves content; this proves nothing extra
   and nothing missing rode along).
5. Spot-open three files by type: one `.md` from `plan/`, one `.kt`
   from `trunk/app/src/`, one letter from `mail/` — each must read
   as intact text, not just hash-correct bytes.
6. Record in SNAPSHOT.md: `restore-test: PASS <date time IST> from
   Location B, N files, 0 checksum failures`. Delete the scratch dir
   after a PASS is recorded (the snapshot itself is the preservation,
   the scratch was only proof). On FAIL: the snapshot is registered
   as FAILED, the failure is reported to Ren and Protokoll the same
   day, and a replacement snapshot is taken immediately — a failed
   snapshot is never quietly retried-and-forgotten.

Only after PASS may the snapshot be entered in the registry (§7) with
status COUNTED.

## 6. CADENCE

- **On every merge:** any merge of a lane into trunk, or of a cohort
  deliverable into the plan, triggers a snapshot named
  `merge-<lane-or-cohort>`, taken after the merge compiles/tests as
  the merge's own rules require. Zeuge attends merges; Zeuge's
  attendance is Anker's trigger — no witnessed merge, no snapshot
  owed; no snapshot, the merge is not announced as preserved.
- **Daily:** one `daily` snapshot per day at end of studio work,
  whether or not anything merged. A day with a merge snapshot still
  gets its daily only if work continued after the merge.
- **On demand:** Ren, Protokoll, or Sarthak may order a `manual-*`
  snapshot at any time; Anker takes it without arguing scope.
- A missed cadence point is an open loop: Anker names it in
  OPEN-LOOPS via Protokoll the day it is missed, not when noticed
  weeks later.

## 7. Registry entry (per snapshot)

One entry per snapshot — in WORK-REGISTRY.md once Register creates it;
until then, appended to this spec's log section by Anker, same fields:

```
- <snapshot-name> | taken <date time IST> by <owner> | trigger: <…>
  A: ~/workspace/preservation/monolith-snapshots/<name>/
  B: ~/workspace/your_files/monolith-archive/snapshots/<name>/
  manifest sha256: <…> | files: <N> | bytes: <N>
  restore-test: PASS/FAIL <date time IST> | status: COUNTED / FAILED / UNVERIFIED
```

## 8. RETENTION

- **Merge snapshots:** keep the last **10** per lane/cohort line, plus
  every merge snapshot Ren or Sarthak has named (named snapshots are
  permanent until the namer releases them).
- **Daily snapshots:** keep the last **14 days** in full; after that,
  keep the **last daily of each week** for a further 6 weeks, delete
  the rest. Deletion is by Anker only, both locations together, and
  the registry entry is struck through with the deletion date — never
  silently removed (the record that a snapshot existed survives the
  snapshot).
- **Failed / UNVERIFIED snapshots:** kept until a COUNTED replacement
  exists, then deleted under the same struck-through rule.
- **APKs of record:** `monolith-final-debug.apk` and any APK Ren has
  announced as delivered to Sarthak are retained in every snapshot
  that contains them and are never pruned by the rotation above while
  they remain the latest delivered build.
- Nothing in either snapshot location is ever deleted by anyone but
  Anker (or Ren directly), and never on a cleanup pass aimed at
  something else — the Oct 4 / Oct 10 lesson is that trees die as
  collateral; these two trees are nobody's scratch space.

---

## Snapshot log (until WORK-REGISTRY.md exists)

*(empty — first entry will be Ren's Oct 29 snapshot, registered when
Anker receives its location + manifest path from Ren; every snapshot
from the next one on is Anker's.)*

- 2026-10-29_1148_manual-first | taken 2026-10-29 11:48 IST by Anker | trigger: manual-first (Snapshot #1 re-test, Ren's commission)
  A: ~/workspace/preservation/monolith-snapshots/2026-10-29_1148_manual-first/
  B: ~/workspace/your_files/monolith-archive/snapshots/2026-10-29_1148_manual-first/
  manifest sha256: 11ada2f4fa16e947b28c1911f7c1ce8bc953dbd952e68a375c9dce3fe4e05fcf | files: 3325 | bytes: 568973290
  restore-test: PASS 2026-10-29 11:49 IST from Location B, 0 checksum failures | status: COUNTED
  (Full entry in plan/WORK-REGISTRY.md, §Snapshots — this log line is
  the pointer; the registry is the record.)

- 2026-10-29_2104_manual-post-batch7-pre-art-merge | taken 2026-10-29 21:45 IST by Anker | trigger: manual-post-batch7-pre-art-merge (the two owed snapshots — post-slide-state + daily — discharged as one current-state snapshot)
  A: ~/workspace/preservation/monolith-snapshots/2026-10-29_2104_manual-post-batch7-pre-art-merge/
  B: ~/workspace/your_files/monolith-archive/snapshots/2026-10-29_2104_manual-post-batch7-pre-art-merge/
  manifest sha256: 0de9adf208f7e963ecbc50357944fac4e09b2000194397e5cd90e740a174866c | files: 5999 | bytes: 974007921
  restore-test: PASS 2026-10-29 21:46 IST from Location B, 0 checksum failures | status: COUNTED
  (Full entry in plan/WORK-REGISTRY.md, §Snapshots — this log line is
  the pointer; the registry is the record. The failed 2102 attempt
  of the same evening is registered there too.)

- 2026-10-30_0137_manual-pre-truth3-merge | Snapshot #3 (manual-pre-truth3-merge, Ren's commission, Sarthak's order Oct 29 23:16 IST) — FIVE attempts (2353, 0014, 0018, 0115, 0137), ALL FAILED, none wrote to either location; status: NOT COUNTED — snapshot not taken as of Oct 30 ~02:00 IST
  (Full entries — one per attempt, with causes — in plan/WORK-REGISTRY.md, §Snapshots; the registry is the record. Summary: 2353 died on intentionally dangling queue-sim symlink fixtures (runner patched: links preserved, hashed by target string); 0014/0018 died Errno 1 mid-staging under the night's memory weather; 0115/0137 died Errno 1 across all resumable passes. Final probe diagnosis: the agent-exec path refuses read-then-write movement of workspace data above a small trickle in the session's current state — reads pass, non-workspace writes pass, bulk copies refuse — tightening across the night; the file-tool path is unaffected but is text-only. The ceremony runner stands staged at ~/workspace/preservation/run_snapshot3.py for a fresh exec context to run.)

— Anker, Keepers of Record (preservation)
