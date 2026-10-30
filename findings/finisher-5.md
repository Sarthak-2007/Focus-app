# Issue #17 — Snapshot #3: THE NUMBERS PAGE (one authoritative figure per quantity)

Prepared by: FINISHER-5, Anker cell. **Read-only measurement only — `du`, `find`, `wc`, `sha256sum`, `lstat` sums. Nothing was copied, nothing was run.**
Measured: Fri 2026-10-30, ~03:20–03:40 IST. Every figure below names its method. Where an older record disagrees, both figures are printed, the method difference is named, and one is crowned. Quote the crowned column and no other.

**Missing source, stated plainly:** the brief pointed at `findings/verifier.md` — **no such file exists** in this cell (findings on disk: `planner.md`, `critic.md`, `innovator.md`, `finisher-1.md`). The conflicting figures reconciled in §7 therefore come from PLANNER vs CRITIC / INNOVATOR / FINISHER-1 and the registry. If a verifier report is filed later, it must be checked against this page, not the other way round.

---

## 0. The four methods — never mix them again

| Method | Command / definition | What it counts | Use it for |
|---|---|---|---|
| **Manifest-sum ★** | Sum of `lstat` sizes of exactly the files listed in `MANIFEST.sha256` (= the runner's `build_manifest` total, printed as `BYTES` in the run log and in `SNAPSHOT.md`) | Payload only. Excludes `MANIFEST.sha256` itself and `SNAPSHOT.md` by spec | **The size of a snapshot. This is the crowned figure.** |
| Whole-tree apparent | `du -sb <dir>` | Every regular file in the tree, including `MANIFEST.sha256` + `SNAPSHOT.md` | Cross-check: must equal manifest-sum + manifest size + SNAPSHOT.md size, exactly (it does — §1, §2) |
| Disk-block usage | `du -sh <dir>` (4 KiB blocks, rounded) | Blocks actually allocated, incl. per-file round-up and directory blocks | Disk planning only. **Never quote it as a snapshot's size** — it is where "551M" and "944M" came from |
| File count | Manifest lines (`wc -l MANIFEST.sha256`) ★ vs `find <dir> -type f \| wc -l` | Manifest count = payload files; `find` count = payload + 2 (manifest + SNAPSHOT.md) | Crowned count is the **manifest count**; the `find` count must always be exactly +2 |

Symlinks: snapshots #1/#2 contain **0 symlinks** (`find -type l` = 0 in all four trees), so no link convention affects any crowned figure below.

---

## 1. Snapshot #1 — `2026-10-29_1148_manual-first` — CROWNED

| Quantity | Crowned figure ★ | Method |
|---|---|---|
| Payload files | **3,325** | Manifest lines, identical in A and B |
| Payload bytes | **568,973,290 B** (569.0 MB decimal / 542.6 MiB) | Manifest-sum; equals run-log `BYTES` and `SNAPSHOT.md` |
| Manifest sha256 | **`11ada2f4fa16e947b28c1911f7c1ce8bc953dbd952e68a375c9dce3fe4e05fcf`** | `sha256sum MANIFEST.sha256`, recomputed today, A = B |
| Whole tree (each location) | 3,327 files, 569,404,651 B | `find -type f` / `du -sb` |
| Disk-block usage (each location) | 577,523,712 B allocated (`du -sh` = 551M) | `du -s -B4096` × 4096 — planning figure only |
| Restore test | PASS, 2026-10-29 11:49 IST, from B, 0 checksum failures | `SNAPSHOT.md` + `snapshot1-run.log` |

Reconciliation (exact): 568,973,290 + 430,498 (`MANIFEST.sha256`) + 863 (`SNAPSHOT.md`) = **569,404,651 = `du -sb`**. No residue, no missing bytes.
Section split (files / bytes, `du -sb` per section, A): swarm 1,484 / 21,115,891 · trunk 136 / 10,624,729 · lanes 1,575 / 184,943,988 · records 111 / 581,662 · apks 19 / 351,707,020. Counts sum to 3,325 ✔ (matches `snapshot1-run.log` COUNTS exactly).
Locations: A `~/workspace/preservation/monolith-snapshots/…` and B `~/workspace/your_files/monolith-archive/snapshots/…` are **identical on every quantity above** — same counts, same bytes, same manifest hash.

## 2. Snapshot #2 — `2026-10-29_2104_manual-post-batch7-pre-art-merge` — CROWNED

| Quantity | Crowned figure ★ | Method |
|---|---|---|
| Payload files | **5,999** | Manifest lines, identical in A and B |
| Payload bytes | **974,007,921 B** (974.0 MB decimal / 928.9 MiB) | Manifest-sum; equals `SNAPSHOT.md` |
| Manifest sha256 | **`0de9adf208f7e963ecbc50357944fac4e09b2000194397e5cd90e740a174866c`** | `sha256sum MANIFEST.sha256`, recomputed today, A = B. **This full string replaces the corrupted splice in planner §2 (see §7, item 2)** |
| Whole tree (each location) | 6,001 files, 974,818,095 B | `find -type f` / `du -sb` |
| Disk-block usage (each location) | 989,827,072 B allocated (`du -sh` = 944M) | `du -s -B4096` × 4096 — planning figure only |
| Restore test | PASS, 2026-10-29 21:46 IST, from B, 0 checksum failures | `SNAPSHOT.md` |
| Successful-window memory | MemAvailable 2,551 MB at launch; the failed first attempt is `2026-10-29_2102_…` (FAILED, debris cleaned) | `SNAPSHOT.md` copy history + registry |

Reconciliation (exact): 974,007,921 + 808,502 (`MANIFEST.sha256`) + 1,672 (`SNAPSHOT.md`) = **974,818,095 = `du -sb`**. ✔
Section split (files / bytes, A): swarm 816 / 22,694,292 · trunk 156 / 11,101,348 · lanes 4,887 / 513,567,318 · records 117 / 632,781 · apks 23 / 426,012,182. Counts sum to 5,999 ✔.
Name note: the directory is named `2104` (attempt start) but `SNAPSHOT.md` records *taken: 21:45 IST* — the name is not the preserved state's timestamp (critic C-6).

## 3. core-001 — NOT a workspace snapshot

| Quantity | Crowned figure ★ | Method |
|---|---|---|
| What it is | A **partial decision-record core**: 16 studio decision files (plan/, swarm/plan/, BUILD-CHANGES.md, tools/constants.json) copied file-by-file through the file tools on Oct 29 ~10:45 IST, after the full snapshot's copy path failed 3 times | Its own `SNAPSHOT.md` |
| Files on disk | **17 total = 16 payload + its own `SNAPSHOT.md`** (identical in A and B) | `find -type f` |
| Bytes on disk | **414,511 B total; 410,850 B excluding `SNAPSHOT.md` (3,661 B)** | `du -sb` / `lstat` sum |
| Manifest | **None — no `MANIFEST.sha256`, no restore test** | `ls` — it does not meet the counted-snapshot bar and must never be quoted as one |

Planner §2's "(17 files, no manifest — ignore it)" gets the count right by accident (it is the whole-tree count, not a payload count) and the disposition wrong: core-001 is a registered PARTIAL, kept until a counted replacement exists (critic C-12). It contains **no** source code, lanes, APKs, mail archive, or COMMONS.md.

## 4. Snapshot #3 — the failed-attempt record — CROWNED

**5 registered attempts, 0 counted, 0 bytes written to either location.** Source: `studio/plan/WORK-REGISTRY.md` snapshot block (in the issue-17 cell clone) — the registry, not any single log, is the complete record. `snapshot3-run.log` covers only the last attempt.

| # | Attempt (IST) | How it died | MemAvailable |
|---|---|---|---|
| 1 | `2026-10-29_2353` | Staging died on a dangling-by-design symlink (`swarm/methods/queue-sim/…/compile-ghost.flag`, target `/nonexistent-target`); `shutil.copy2` follows links. Runner patched to preserve links | 2,593 MB at launch → 2,585 MB at stop (not a memory death) |
| 2 | `2026-10-30_0014` | `Errno 1` mid-staging, write-refusal signature | 3,104 MB at launch → 2,088 MB at stop |
| 3 | `2026-10-30_0018` | `Errno 1` after exactly **1 staged file** (`swarm/MAXIMPL.md`) despite sustained gate | 3,073 → 3,037 MB; 2,909 MB launch-check; 2,916 MB at stop |
| 4 | `2026-10-30_0115` | All **12** resumable staging passes died `Errno 1`; total progress still 1 file | Deaths across 672–1,850 MB (registry diagnosis readings) |
| 5 | `2026-10-30_0137` | All **12** staging attempts died `Errno 1` (`snapshot3-run.log`: `STAGE-ATTEMPT 1…12`, 25 s backoffs, `FAIL-STOP`) | 2,251 MB at start → 1,363 MB at failure (per-attempt readings in the log) |

Staging leftovers on disk today (measured, not inferred): `~/workspace/preservation/.staging-snapshot-3/2026-10-30_0137_manual-pre-truth3-merge/` holds **exactly 1 file, `swarm/MAXIMPL.md`, 0 bytes on disk** — a shell, not the 24.7 KB source the 0137 diagnosis mentions as its one passing probe copy. (An older unrelated leftover, `.staging-001/…/01-hero-….jpg`, is also 0 bytes / 1 file.) The next run of either runner `rmtree`s its staging parent at start — this debris is destroyed by launch, by design (planner D9 / finisher-1 Gate 7).
Counts that must not be conflated: **5** = registered #3 attempts · **12** = in-run staging passes inside attempts 4 and 5 each · **"five failed attempts"** in planner/finisher-1 = the same 5.

## 5. Last night's 02:38 daily snapshot — FAILED before any copy

There is **no daily-run log** in `~/workspace/preservation/` (checked: only `snapshot1-run.log` and `snapshot3-run.log` exist; `.staging-daily/` does not exist). The record lives in **one place**: registry entry `2026-10-30_0238_daily` in `studio/plan/WORK-REGISTRY.md`, corroborated by the cron definition (`~/workspace/goals/monolith-focus-app-revamp/crons/daily/monolith-daily-snapshot__daily@02:38:00_user_current.md`, daily 02:38 IST) and critic C-5 (`cron.status`: last run Fri 2026-10-30 02:59 IST, `failed`, 1 consecutive failure, never succeeded).

| Quantity | Crowned figure ★ |
|---|---|
| Failure step | **Step 1, MEMORY CHECK** — the copy was never started; no snapshot dir, staging payload, or location write exists from this attempt |
| Gate | Single check, MemAvailable **≥ ~2,500 MB** (operator gate per Anker's letter 2148; `run_daily.py`'s own code floor is only 350 MB — finisher-1) |
| Poll window | ~20 min of short polls, **02:38–02:58 IST** |
| MemAvailable readings | **923 MB at start, then 1475 / 1092 / 843 / 662 / 505 / 1527 / 1145 / 1098 / 1000 MB — peak 1,527 MB, never within 1,000 MB of the gate** |
| Gates that DID pass (not partial success) | Disk: **111 GB free** vs ≥ 15 GB required (the daily spec's own floor) · Write-health probe: passed in both destinations |
| Box state | Free RAM 143–262 MB across the window; **2 Gradle daemons resident**, one a 1.5 GB RSS blockit-mod build in flight from 02:28 IST |

## 6. Snapshot #3 payload estimate + disk headroom — CROWNED (fresh replica measurement)

Method: a read-only walk replicating `run_snapshot3.py`'s `stage_payload` **exactly** — same source set, same lane rule (every `builds/<lane>` holding a `Focus-app` dir), same prune set (`guh`, `build`, `.gradle`, `tmp`), sizes by `lstat` (the runner's manifest method). Measured ~03:30 IST today; the source tree is live, so this is a copy-minute figure, not a constant.

| Component | Files | Bytes |
|---|---|---|
| swarm (`design-refs/swarm/`) | **0** — source **deleted** Oct 30 ~03:00; stages silently empty (planner D1) | 0 |
| trunk (app/src 125 f / 2,023,726 B + 6 named files + gradle 2 f + public 39 f / 9,273,474 B) | 172 | 11,314,521 |
| lanes — 21 qualifying (dawn, flow-clock, look-blueprint, look-dawn, look-noir, look-signal, look-temple, merge-batch9, nofire, obsidian, onboarding, pill, routine-edit, truth-3-breaks, truth-3-intercept, truth-3-proofbase, truth-3-shorts, truth-3-sound, truth-3-sweep, truth-3-times, warrant) | 5,731 | 506,976,321 |
| records (6 base files + 3 surviving brief globs; all `*.log` globs dead post-cleanup) | 9 | 159,732 |
| apks (`monolith-batch8-truth-fixed.apk` 11,228,957 + `monolith-batch9.apk` 10,995,124) | 2 | 22,224,081 |
| **#3 PAYLOAD, script as-is ★** | **5,914 files** | **540,674,655 B (515.6 MiB)** |
| of which lane `Focus-app/.git` (20 lanes × 9,224,889 B identical clones + merge-batch9's 15,453,720 B) | 1,204 files | 199,951,500 |
| **#3 PAYLOAD, with `.git` pruned ★** | **4,710 files** | **340,723,155 B (324.9 MiB)** |

Not captured by the script's rules (measured, for the D4 decision): `builds/cells/` **609,802,026 B** (`du -sb`; 11,638 files) · `builds/wt-ci/` 24,786,631 B · `design-refs/` flat files 3,113,457 B (33 files: 23 numbered/named + 10 `view*`/`viewcrit*` derivatives). Correctly out of scope: `tools/` 3.3 GB and `trunk-guh/` 2.0 GB (regenerable toolchain / Gradle home).

| Headroom | Crowned figure ★ | Method |
|---|---|---|
| Disk available | **123,013,009,408 B available** (115 GiB shown; 150 GiB total, 23% used) on `/dev/mapper/rv` (`/home/hatch`) — A and B are this same disk (critic C-1) | `df -B1 ~/workspace`, today |
| Peak transient need (staging + A + B + restore scratch ≈ 4× payload) | 2,162,698,620 B as-is / 1,362,892,620 B `.git`-pruned | 4 × crowned payload |
| Final footprint (A + B ≈ 2× payload) | 1,081,349,310 B as-is / 681,446,310 B `.git`-pruned | 2 × crowned payload |
| Verdict | Headroom is **~57× the worst-case peak** — disk has never been the binding gate; memory/write-health has (finisher-1 Gates 5–6). Planner §5.1's ≥ 10 GB gate and the daily spec's ≥ 15 GB gate both pass trivially today | — |

## 7. Disagreements — both figures printed, one crowned

| # | Quantity | Figure A | Figure B | Method difference | Crowned ★ |
|---|---|---|---|---|---|
| 1 | Size of #1 / #2 | 551M / 944M (planner §2, innovator's disk notes) | 568,973,290 B / 974,007,921 B | A is `du -sh` **disk-block** usage; B is **manifest-sum** payload. Innovator's "569 MB / 974 MB raw" is B rounded — same figure, coarser dress | **B** — manifest-sum (§1, §2). Block figures survive only as disk-planning numbers |
| 2 | #2 manifest sha256 | Planner §2: `0de9adf2…f4e09b2000194397e5cd90e740a174866c` | `0de9adf208f7e963ecbc50357944fac4e09b2000194397e5cd90e740a174866c` | Planner's is a **corrupted splice** — the substring `f4e09b…` occurs nowhere in the true hash (critic C-12, verified) | **The recomputed full hash** — `sha256sum` today, A = B = SNAPSHOT.md |
| 3 | #3 payload estimate | Planner §5.1: ≈520M as-is / ≈330M `.git`-pruned; lanes "~470M raw before pruning" | **540,674,655 B as-is / 340,723,155 B pruned; lanes 506,976,321 B** (this page, §6) | Planner's lane figure matches no method measured today (post-prune `lstat` sum = raw sum — the cleanup already stripped `guh`/`build`/`.gradle`/`tmp` from lanes, so "before pruning" no longer differs); planner's small trunk figures (app/src "2.3M", gradle "12K") are `du -sh` block numbers vs 2,023,726 B / 6,433 B by `lstat`. Both estimates also predate ~25 min of live lane/cell writing | **This page's replica measurement** — same rules as the runner, byte method, later minute. Planner's figures are retired, not averaged |
| 4 | `builds/cells/` size (D4 decision input) | Planner §3: 515M | `du -sb`: **609,802,026 B**; naive `lstat` walk: 624,646,044 B | Three methods, three numbers: planner's is stale (cells grew during issue work); `du` counts hardlinked `.git` objects once, the `lstat` walk counts every link — the 14.8 MB gap is that, not error | **`du -sb` 609,802,026 B** for disk purposes; either way it has grown ~95–110 MB since the plan, which strengthens the case that the D4 include/exclude decision cannot ride on planner's figure |
| 5 | Memory gate for #3 / daily | 350 MB (both runners' code) · ~2,500 MB (planner §5.3, cron body, Anker letter 2148) · ≥3,000 MB sustained (registry, #2's 2102 replacement criterion) | Deaths on record at 2,585 / 2,088 / 2,916 MB and a PASS (#1) at 967 MB | Not a measurement dispute — three different *rules* in four documents (critic C-9, finisher-1 numbers table) | **No memory number is crowned as predictive.** The crowned operational figures are finisher-1's: **≥2,500 MB screening floor (necessary, never sufficient) + the sustained write probe as the decisive gate**. The 350 MB code floor is crowned **rejected** |
| 6 | core-001 file count | Planner §2: "17 files" | 17 on disk = **16 payload + SNAPSHOT.md** | Whole-tree `find` count quoted where a payload count was implied — the same +1/+2 trap as §0 | **16 payload / 17 total**, 414,511 B, no manifest (§3) |
| 7 | #1/#2 A/B equality | Assumed in several records | **Verified today**: identical file counts, byte totals, and manifest hashes in A and B for both snapshots; `sha256sum -c` clean per critic C-13 | — | **Equality is a measured fact**, not an assumption — for #1 and #2 only; it says nothing about #3, which exists in neither location |

---

## Crowned numbers — the only ones to quote

- **#1:** 3,325 files · **568,973,290 B** · manifest `11ada2f4fa16e947b28c1911f7c1ce8bc953dbd952e68a375c9dce3fe4e05fcf` · restore PASS
- **#2:** 5,999 files · **974,007,921 B** · manifest `0de9adf208f7e963ecbc50357944fac4e09b2000194397e5cd90e740a174866c` · restore PASS
- **core-001:** 16 payload files (17 on disk) · 414,511 B · no manifest · PARTIAL, not a snapshot
- **#3:** not taken — **5 failed attempts**, 0 counted files/bytes; payload if run now: **540,674,655 B / 5,914 files as-is**, or **340,723,155 B / 4,710 files with lane `.git` pruned**; disk available 123,013,009,408 B
- **02:38 daily:** FAILED at the memory gate — peak MemAvailable **1,527 MB** against a ~2,500 MB gate over 02:38–02:58 IST; disk gate passed at 111 GB free; copy never started

— FINISHER-5
