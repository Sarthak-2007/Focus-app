# Issue #17 — VERIFIER findings (Anker cell): integrity of Snapshots #1, #2, and the non-existence of #3

Measured independently, read-only, Fri 2026-10-30 ~03:05–04:05 IST, by direct
filesystem measurement (file counts, byte sums, manifest parsing, and a full
`sha256sum -c` re-hash of every payload file in both locations — no copies made).
Records (SNAPSHOT.md, logs, WORK-REGISTRY) were treated as claims, not evidence,
except where a contemporaneous run log is itself the artifact under test.

Locations found (the only Monolith snapshot locations anywhere under `~/workspace`;
other `*snapshot*` dirs found are GMC, browser, and Android-AVD — unrelated):

- **Location A:** `~/workspace/preservation/monolith-snapshots/` — dirs: `2026-10-29_1148_manual-first`, `2026-10-29_2104_manual-post-batch7-pre-art-merge`, `core-001`
- **Location B:** `~/workspace/your_files/monolith-archive/snapshots/` — identical three dirs
- A and B are the **same device** (device 88, `/dev/mapper/rv`, `/home/hatch`) — A/B protects against botched/partial writes, not device loss.

---

## Claim 1 — Snapshot #1 has 3,325 files and its restore test PASSED

**Verdict: PROVEN** (file count and integrity proven by my own re-hash; restore PASS proven by the contemporaneous run log + stamped record, corroborated by re-verification of the restore source today)

Measured, `2026-10-29_1148_manual-first`, identical in A and B:

| Measure | Value |
|---|---|
| Files on disk, total | 3,327 (= 3,325 payload + `MANIFEST.sha256` + `SNAPSHOT.md`) |
| Payload files | **3,325** |
| Payload bytes (sum over manifest entries, from disk) | **568,973,290** — matches the claimed figure exactly |
| Total on disk incl. meta | 569,404,651 B (`du` 551M per location) |
| Manifest | 430,498 B, 3,325 lines, sha256 of the manifest file itself = `11ada2f4fa16e947b28c1911f7c1ce8bc953dbd952e68a375c9dce3fe4e05fcf` — equals the value recorded in SNAPSHOT.md |
| Manifest ↔ disk | entry set == payload set exactly: 0 missing, 0 extra, 0 duplicates |
| **Full re-hash today (`sha256sum -c`)** | **A: 3,325/3,325 OK, 0 failures · B: 3,325/3,325 OK, 0 failures** |
| Section counts (B) | swarm 1,484 + trunk 136 + lanes 1,575 + records 111 + apks 19 = 3,325 — matches the run log's COUNTS line exactly |
| A ↔ B | file lists identical; `MANIFEST.sha256` and `SNAPSHOT.md` byte-identical (SNAPSHOT.md sha256 `22adfb5f…dc7b`) |

Restore test — what "PASS" actually was, and what survives of it:

- The runner (`run_snapshot1.py`, S5 ceremony) copied Location B into
  `~/workspace/preservation/restore-test/<NAME>`, re-hashed every file against the
  manifest, spot-opened 3 files, stamped the result into SNAPSHOT.md in A and B,
  then **deleted the scratch copy itself**. `restore-test/` is empty today — that
  is by design, not a gap.
- The contemporaneous artifact survives: `~/workspace/preservation/snapshot1-run.log`
  records the full sequence — `STAGE-VERIFY ok=3325 fail=0`,
  `VERIFY-A ok=3325 fail=0 manifest_matches=True`, `VERIFY-B` same,
  `RESTORE ok=3325 fail=0 files=3325 bytes=568973290`,
  `SPOT-OPEN plan-md:SNAPSHOT-SPEC.md:True trunk-kt:ExampleInstrumentedTest.kt:True mail-letter:1158__from-hale__every-screen-review-build1.md:True`,
  `RESTORE-TEST PASS`.
- I re-checked the three spot files in B today: `swarm/plan/SNAPSHOT-SPEC.md`
  (11,893 B, readable), first trunk `.kt` = `ExampleInstrumentedTest.kt`, and the
  Hale letter (4,977 B, readable) — all present. And B — the restore source — still
  hashes 3,325/3,325 OK today, so the restore's precondition is re-proven, not just asserted.
- Timestamp consistency: manifest mtime on disk is Oct 29 06:19 UTC = 11:49 IST,
  matching the stamped "restore-test: PASS 2026-10-29 11:49 IST" line.

## Claim 2 — Snapshot #2 has 5,999 files and its restore test PASSED

**Verdict: PROVEN for the file count and present-day integrity; the restore PASS is
corroborated but UNPROVABLE as a historical event** (no run log survives; the only
evidence is the stamped record — see below). Nothing found contradicts it.

Measured, `2026-10-29_2104_manual-post-batch7-pre-art-merge`, identical in A and B:

| Measure | Value |
|---|---|
| Files on disk, total | 6,001 (= 5,999 payload + `MANIFEST.sha256` + `SNAPSHOT.md`) |
| Payload files | **5,999** |
| Payload bytes | **974,007,921** — matches the claimed figure exactly |
| Total on disk incl. meta | 974,818,095 B (`du` 944M per location) |
| Manifest | 808,502 B, 5,999 lines, sha256 of the manifest file itself = `0de9adf208f7e963ecbc50357944fac4e09b2000194397e5cd90e740a174866c` — equals the value recorded in SNAPSHOT.md |
| Manifest ↔ disk | entry set == payload set exactly: 0 missing, 0 extra, 0 duplicates |
| **Full re-hash today (`sha256sum -c`)** | **A: 5,999/5,999 OK, 0 failures · B: 5,999/5,999 OK, 0 failures** |
| Section counts (B) | swarm 816 + trunk 156 + lanes 4,887 + records 117 + apks 23 = 5,999 |
| A ↔ B | file lists identical; `MANIFEST.sha256` and `SNAPSHOT.md` byte-identical (SNAPSHOT.md sha256 `23b3974f…8c5c2`) |

Restore test — weaker paper trail than #1, stated plainly:

- **No run log for #2 exists** in `~/workspace/preservation/` (only
  `snapshot1-run.log` and `snapshot3-run.log` are on disk). The restore scratch was
  likewise deleted by the runner, so no restore artifact survives.
- What exists: the stamped line in SNAPSHOT.md (byte-identical in A and B) —
  "restore-test: PASS 2026-10-29 21:46 IST from Location B, 5999 files, 0 checksum
  failures" — and a WORK-REGISTRY entry asserting an independent `sha256sum -c`
  in Location B with 0 non-OK lines. Both are *records*, i.e. the thing I was told
  not to trust on their own.
- What I can say from my own measurement: the restore's entire precondition is
  true today — B is complete and hashes 5,999/5,999 OK, so a restore from B
  performed now would pass verification. The historical 21:46 event itself cannot
  be re-observed. Hence: count/integrity PROVEN; restore PASS **UNPROVABLE**
  (corroborated, uncontradicted).
- Timestamp consistency: manifest mtime Oct 29 16:15 UTC = 21:45 IST, matching
  SNAPSHOT.md's "taken: 2026-10-29 21:45 IST" (the `2104` in the directory name is
  the failed first attempt's minute; the completing pass ran 21:44–21:45).

## Claim 3 — Snapshot #1's manifest is truncated mid-line

**Verdict: DISPROVEN — precisely.**

Measured on `2026-10-29_1148_manual-first/MANIFEST.sha256` in **both** A and B
(the two copies are byte-identical):

- Size 430,498 B; **3,325 lines**; the file **ends with a trailing newline**.
- **Every one of the 3,325 lines is well-formed** against `^[0-9a-f]{64}  <path>$`
  — 0 malformed lines, 0 duplicates.
- The **last line is complete**, 91 chars:
  `2fbfab90c6cbe18927dde52b4c3dc2dcdac219640663ab7566e19a4c2ec0b697  trunk/settings.gradle.kts`
- Entries vs files on disk: **3,325 entries vs 3,325 payload files** (3,327 files
  total including the manifest itself and SNAPSHOT.md, both excluded by spec) —
  symmetric difference is empty in both directions. A truncated manifest would
  necessarily show a short/malformed final line and missing tail entries; neither exists.
- The manifest's own sha256 equals the value recorded at creation time
  (`11ada2f4…e05fcf`), so the file on disk today is bit-for-bit the file that was
  written on Oct 29 — it cannot have been truncated and later silently repaired
  without changing that hash.
- Subsidiary manifests checked to close the loophole: the lane
  `BASE-MANIFEST.sha256` files inside #1 (6 lanes) and #2 (9 lanes) also all end
  with a newline and a complete final hash line; their only non-hash lines are
  `#` comment headers (9 per retro-reconstructed lane, 7 per lane-at-creation).
  No truncated manifest of any kind was found in either snapshot, and no record
  in `~/workspace/preservation/` claims one.

## Claim 4 — Snapshot #3 does not exist

**Verdict: PROVEN** (no completed/counted Snapshot #3 exists anywhere; attempts and debris do, and are inventoried below so "does not exist" is not over-read)

- Both snapshot locations contain exactly three directories: #1 (`…_1148_manual-first`),
  #2 (`…_2104_manual-post-batch7-pre-art-merge`), and `core-001`. A workspace-wide
  search for snapshot directories/manifests found no other Monolith snapshot location.
- No directory exists for **any** #3 attempt name: `2026-10-29_2353_…`,
  `2026-10-30_0014_…`, `…_0018_…`, `…_0115_…`, `…_0137_manual-pre-truth3-merge`,
  nor the daily attempt `2026-10-30_0238_daily` — in either location.
- What does exist for #3 (none of it is a snapshot):
  - the staged runner `~/workspace/preservation/run_snapshot3.py` (never completed a run);
  - `~/workspace/preservation/snapshot3-run.log` — the 01:37 attempt: 12 staging
    attempts, each dying `PermissionError: [Errno 1] Operation not permitted`,
    ending `FAIL-STOP`;
  - staging debris `~/workspace/preservation/.staging-snapshot-3/2026-10-30_0137_manual-pre-truth3-merge/`
    containing **exactly 1 file, `swarm/MAXIMPL.md`, at 0 bytes** — a failed-write
    shell, not payload (cf. the earliest attempt's debris in `.staging-001/`:
    exactly 1 file, a 0-byte `.jpg`).
  - WORK-REGISTRY entries registering all five #3 attempts (2353, 0014, 0018,
    0115, 0137) plus the 0238 daily as FAILED / NOT WRITTEN — consistent with
    everything measurable on disk.
- Not #3, for the record: `core-001` is an earlier partial mini-snapshot
  (17 files, 414,511 B / 440K on disk, **no manifest**, its own SNAPSHOT.md
  declares it PARTIAL with no restore test) — it predates the #1/#2 shape and
  satisfies none of the snapshot bar.

---

## Summary

| # | Claim | Verdict |
|---|---|---|
| 1 | #1 = 3,325 files, restore PASS | **PROVEN** — count, bytes (568,973,290), and full re-hash (A+B, 3,325/3,325 OK each) measured today; PASS backed by the contemporaneous `snapshot1-run.log` ceremony record |
| 2 | #2 = 5,999 files, restore PASS | **PROVEN** (count, bytes 974,007,921, full re-hash A+B 5,999/5,999 OK each) / restore PASS **UNPROVABLE** as a historical event — no run log or artifact survives, only the stamped SNAPSHOT.md line; uncontradicted, and its precondition re-proven today |
| 3 | #1's manifest is truncated mid-line | **DISPROVEN** — 3,325/3,325 well-formed lines, trailing newline present, final line complete, entry set == disk set, manifest hash unchanged since creation |
| 4 | Snapshot #3 does not exist | **PROVEN** — no #3 directory in either location under any attempt name; only a runner, a FAIL-STOP log, registry FAILED entries, and 1 zero-byte staging file |

Method note: verification was by in-place hashing only — nothing was copied,
modified, or deleted (the full re-hash of ~3 GB across A+B for both snapshots is
the same check the restore test performs on its copy, minus the copy itself).
