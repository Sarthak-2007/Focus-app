# Issue #17 — Snapshot #3 ceremony: NAMES AND IDENTITY (FEEL-3)

*Seat: FEEL-3, feel specialist, Anker's cell. Beat: names and identity only.
Evidentiary only — every charge below carries the artifact that proves it.
Read-only: nothing on disk was renamed, moved, or edited. The mapping in §9
is a proposal for the registry, not a rename.
Checked: Fri 2026-10-30, both snapshot stores, the staged runner, and the
records in `studio/plan/` (SNAPSHOT-SPEC.md, WORK-REGISTRY.md §Snapshots).*

## Verdict in one breath

A reader standing in front of `ls` in either store cannot tell what any set
is, what state the app was in, whether it passed, or which sets are twins —
and the one set whose name shouts an identity (`core-001`) is not a snapshot
at all. The naming did not merely *fail to prevent* the #1/_001 confusion;
it manufactured it: Snapshot #1's campaign owns **four** competing names,
and the registry then physically filed the rerun's PASS note under
Snapshot #2's entry. Snapshot #3's staged name is worse — it describes a
moment (pre-Truth-Car-3-merge, batch 8, DB v11, uncompiled lanes, live
mail/wall) that no longer exists, so the ceremony would open by lying
about what it preserves.

---

## 1. The listing test — what `ls` alone tells a reader

Both stores contain exactly three names (verified today, both locations):

```
2026-10-29_1148_manual-first
2026-10-29_2104_manual-post-batch7-pre-art-merge
core-001
```

| Name | What a reader can infer from the name alone | What is actually true (evidence) |
|---|---|---|
| `2026-10-29_1148_manual-first` | "The first manual snapshot, taken 11:48 on Oct 29." | It is a **re-test / rerun**, not a first attempt. Its own SNAPSHOT.md: *"trigger: manual-first (Ren's commission — Snapshot #1 re-test under the fixed memory regime…)"*. It is attempt #3 of its campaign (after `monolith-snapshot-001` FAILED ~10:33 and the `core-001` partial ~10:45). |
| `2026-10-29_2104_manual-post-batch7-pre-art-merge` | "Taken 21:04, after batch 7, before the art merge." | The state words were true at the copy minute, but **2104 is no timestamp in its own record**: SNAPSHOT.md says *taken 2026-10-29 21:45 IST*, copies ran 21:44–21:45, restore PASS 21:46; the failed first pass was 21:02 (WORK-REGISTRY §Snapshots). 2104 matches nothing — attempt start, copy, and verdict are three different minutes and the name uses a fourth. |
| `core-001` | "Core snapshot number 1" — reads as the foundational, possibly *first*, snapshot. | A **PARTIAL decision-record bundle**: 17 files, 440K, **no MANIFEST.sha256 at all** (`ls` on the dir: only `BUILD-CHANGES.md`, `SNAPSHOT.md`, `design-refs/`, `plan/`), no trunk, no lanes, no APKs. Its own SNAPSHOT.md: *"Status: PARTIAL — does not satisfy the spec's COUNTED bar."* The other two sets are 3,325 files / 551M and 5,999 files / 944M. Nothing in the name carries any of that. |

Not inferable from **any** name in the store: the ordinal (#1/#2 — the
ordinals live only in prose), the DB version (v9 in #1, v10 in #2 — only
inside SNAPSHOT.md), the verdict (COUNTED vs PARTIAL), and twinship
(§6). The spec promised *"Names sort chronologically"* (SNAPSHOT-SPEC §3);
`core-001` has no date and sorts **after** both dated sets, so even the
sort promise breaks in the actual listing.

## 2. F-1 — HIGH — Four names for one campaign: the #1/_001 collision was built in

Snapshot #1's single trigger (`manual-first`) accumulated four identities:

1. `monolith-snapshot-001-2026-10-29` — the registry ID for the FAILED
   ~10:33 attempt (WORK-REGISTRY: *"manifest sha256: none | files: 0 |
   restore-test: NOT RUN | status: FAILED… Debris cleaned."*). Date at the
   **end**, no time, a different word order from every directory name.
2. `core-001` — the ~10:45 PARTIAL (its own SNAPSHOT.md). No date, no
   trigger word; the `001` here is a *second, different* 001.
3. `2026-10-29_1148_manual-first` — the 11:48 COUNTED rerun.
4. "Snapshot #1" — the ordinal used in every letter, log, and summary,
   present in **no** directory name.

So "001" can mean the failed tar-era attempt, the partial core bundle, or
(by ordinal shorthand) the counted 1148 set — three different artifacts,
two of them not counted, one of them not a snapshot. Worse, the failed
set's name did not fully die with its debris: a staging remnant still sits
on disk today at `~/workspace/preservation/.staging-001/monolith-snapshot-001-2026-10-29/design-refs/01-hero-wheel-teenage-engineering-tp7.jpg`
— a 0-byte shell — so the ghost name is findable by anyone who greps the
preservation tree, with nothing at that path saying FAILED.

And `manual-first` itself is a false label on the survivor: the set named
"first" is the campaign's *third* artifact. The name records the trigger's
hope, not the set's history.

## 3. F-2 — HIGH — The rerun's PASS is filed under the wrong set in the registry

This is the confusion, consummated. In WORK-REGISTRY §Snapshots, the entry
for `2026-10-29_2104_manual-post-batch7-pre-art-merge` (lines 776–784)
ends with a note that belongs entirely to the 1148 rerun of Snapshot #1:

> *"Note: the owed replacement for the FAILED monolith-snapshot-001
> attempts above — the write path held for the entire run (MemAvailable
> 967 MB at start, 1391 MB at end; no EPERM anywhere). Runner rewritten…
> (~/workspace/preservation/run_snapshot1.py v2)… Independently
> re-verified by Anker… sha256sum -c in Location B: 0 non-OK lines."*

Proof it is #1's note, not #2's: `~/workspace/preservation/snapshot1-run.log`
opens `NAME 2026-10-29_1148_manual-first / MemAvailable at start: 967 MB`
and closes `MemAvailable at end: 1391 MB` — those exact figures; and
`run_snapshot1.py` is #1's runner. A later summariser reading the registry
top-down will credit #2 with #1's memory window, #1's runner, and #1's
re-verification — or, reading loosely, conclude "snapshot-001" eventually
passed *as* the 2104 set. The 1148 entry directly above (lines 759–763)
is clean and complete, which makes the stray note pure trap: the true
record exists, and the misfiled copy sits where a hurried reader lands.
Two name-systems (`monolith-snapshot-001` vs the timestamped dirs) plus
an unanchored "Note:" paragraph is all it took.

## 4. F-3 — HIGH — `core-001` is an impostor in the snapshot store

- It predates the spec shape and never joined it: no manifest, no restore
  test, no `swarm/ trunk/ lanes/ records/ apks/` layout (planner §2:
  *"17 files, no manifest — predates this shape"*).
- It has **no registry entry**: OPEN-LOOPS L49 — *"Pre-spec snapshot dirs
  named core-001 exist at BOTH locations… with no registry entry and no
  ruling"* (owner: Ren; still open).
- Its internal shelving is uneven by its own admission (*"Ren's tranche
  sits under design-refs/swarm/plan/…; copyist 1's tranche… under plan/…
  the shelving is just uneven"*) — even inside, identity is improvised.
- Yet in both stores it sits in the same listing, at the same level, with
  the same visual weight as two COUNTED sets. A preservation store where a
  440K partial and a 944M counted set are indistinguishable until opened
  is a store whose names have stopped doing their one job.

## 5. F-4 — MEDIUM — Verdicts and failures are invisible from names

- No name carries a verdict. COUNTED (#1, #2) and PARTIAL (core-001)
  differ only inside SNAPSHOT.md. The spec's registry format has a status
  field (§7); the names — the surface a human actually scans — do not.
- Failures have **no names on disk at all**: `2026-10-29_2102_…` (FAILED,
  cleaned), the five #3 attempts (`2353`, `0014`, `0018`, `0115`, `0137`
  — all FAILED, none written), and `2026-10-30_0238_daily` (FAILED at the
  memory gate) exist only as registry prose. The disk therefore presents
  a falsely clean universe — 2 passes + 1 partial, zero failures — and
  anyone auditing from the filesystem alone over-counts the ceremony's
  reliability. The asymmetry is the sloppiness: success gets a directory,
  failure gets a paragraph in a different tree.

## 6. F-5 — MEDIUM — Twins are unmarked; the two store names disagree in vocabulary

- A and B copies carry **byte-identical names and byte-identical
  SNAPSHOT.md** (verified today: `diff` clean for both sets and for
  core-001; MANIFEST.sha256 hashes match across A/B — `11ada2f4…` and
  `0de9adf2…` in both). Identical naming *is* the right twin signal — but
  nothing says so where a reader can see it: there is no index file in
  either store root, and each SNAPSHOT.md lists *both* locations, so
  opening the B copy gives you no name-level way to know you are holding
  the mirror, not the primary. Identity lives entirely in the path
  prefix — outside the set.
- The prefixes themselves use mismatched vocabulary for the same role:
  Location A is `preservation/monolith-snapshots/`, Location B is
  `your_files/monolith-archive/snapshots/` (plural/singular flip,
  "preservation" vs "archive"). A reader searching one word misses the
  other store.

## 7. F-6 — MEDIUM — The spec's own name format was never once followed

SNAPSHOT-SPEC §3 prescribes `<YYYY-MM-DD>_<HHMM-IST>_<trigger>` (example:
`2026-10-29_1430_merge-shieldpick/`). No actual set contains the `-IST`
segment; #1's timestamp means *taken*, #2's matches no recorded minute
(§1); trigger syntax drifted across three runs — ordinal (`manual-first`),
sandwich (`manual-post-batch7-pre-art-merge`), single-sided
(`manual-pre-truth3-merge`). Three snapshots, three grammars, zero
conformance to the filed one. A grammar nobody follows is decoration.

## 8. F-7 — BLOCKER (for #3) — The staged #3 name lies about the moment it preserves

`~/workspace/preservation/run_snapshot3.py`, line 33:

```python
NAME = NOW.strftime("%Y-%m-%d_%H%M") + "_manual-pre-truth3-merge"
```

and line 36 / lines 237–246 stamp into SNAPSHOT.md: pre-merge point,
*"Truth Car 3 lanes landed UNCOMPILED"*, *"trunk at batch-8 truth-merge
state (DB v11…)"*, *"shipped APKs batches 1-8"*, *"studio tree live at
copy time (mail and wall as of the copy minute)"*.

Reality at the copy minute today (each item verified):

- Truth Car 3 is **merged**; batch 9 is **built and delivered** —
  `monolith-batch9.apk` sits in the live tree, and the current code is
  **DB v12** (`MonolithDatabase.kt: version = 12` in this cell's tree).
- The swarm tree — mail and wall — is **deleted** (Oct 30 ~03:00);
  collaboration moved to GitHub (`Sarthak-2007/Focus-app`), records
  migrated to `studio/plan/` + `studio/methods/`.
- The suffix has already been spent five times on FAILED attempts
  (2353 → 0137), so it names a *campaign*, not a set (planner D3 reaches
  the same conclusion and suggests `manual-post-batch9-github-studio`).

The compounding defect: the name is minted from the **run minute**, so
running the staged script today produces e.g.
`2026-10-30_<HHMM>_manual-pre-truth3-merge` — a name whose date says
*now* and whose suffix says *before a merge that already happened*. The
ceremony's first act would be to file a false label into both permanent
stores, where spec §3 forbids ever renaming it. Sloppy before it starts
is exactly right: the lie is load-bearing, because it can never be
corrected in place.

**Credit where due:** #2's state sandwich (`post-batch7-pre-art-merge`)
was *true at its minute* and is the best name in the store; #1/#2's
manifests are genuinely identical across A/B; the dated prefix does sort.
The grammar below keeps those three virtues and removes the rest.

---

## 9. Proposed grammar + retroactive mapping (proposal only — nothing renamed)

**Grammar:** `snap-<seq>__<YYYY-MM-DD>_<taken-HHMM>IST__<kind>__db<NN>-<state-slug>__<verdict>`

Rules that make it work:

1. **`<seq>` is the only identity.** Minted once per snapshot campaign
   (snap-001, snap-002, snap-003…), never reused, present in the dir
   name, the registry entry, the runner, and every summary. The words
   "Snapshot #N", the seq, and the directory are then the same thing.
2. **Attempts never get new names.** Retries are `-a`, `-b`, `-c` in the
   *registry only*; a failed attempt creates no directory. One campaign,
   one seq, however many attempts — the _001 trap cannot recur.
3. **Kind is a closed vocabulary:** `workspace` (full spec set),
   `core-records` (decision-record bundle), `daily`, `merge`. A bundle
   that is not a workspace snapshot can never again sit in the store
   wearing a snapshot's clothes.
4. **Timestamp = taken (copy-complete) minute**, the minute the state
   was actually captured — one meaning, always. Attempt-start times live
   in the registry.
5. **State slug must be true at the copy minute and carries the DB
   version** (`db09-post-drawer-merge`). `pre-`/`post-` refer to events
   as they stand *when the shutter fires*, never when the script was
   written. A staged runner whose slug has gone stale is a blocker,
   not a label.
6. **Verdict is minted at sealing:** staging names end `__incomplete`;
   the final rename to `__counted` / `__partial` is the sealing act
   (the spec's no-rename rule binds *after* sealing). A reader of `ls`
   then knows the verdict without opening anything.
7. **Twins share the name exactly** — same name in both stores *means*
   twin, differing name *means* different set — and each store root gets
   an `INDEX.md` stating that rule plus one line per set (seq, verdict,
   manifest hash, twin path). Location is path, never part of the name.

**Retroactive mapping for the existing sets (registry annotation only):**

| On disk today (both stores, unchanged) | Proposed canonical name | Campaign |
|---|---|---|
| *(no directory — FAILED, debris cleaned; staging remnant in `.staging-001/`)* `monolith-snapshot-001-2026-10-29` | `snap-001__2026-10-29_1033IST__workspace__db09-post-drawer-merge__failed-a` (registry-only) | snap-001, attempt a |
| `core-001` | `snap-001__2026-10-29_1045IST__core-records__notrunk-decision-record__partial-b` | snap-001, attempt b — a *core bundle*, not the snapshot |
| `2026-10-29_1148_manual-first` | `snap-001__2026-10-29_1148IST__workspace__db09-post-drawer-merge__counted` | snap-001, the counted set ("Snapshot #1") |
| *(no directory — FAILED, cleaned)* `2026-10-29_2102_manual-post-batch7-pre-art-merge` | `snap-002__2026-10-29_2102IST__workspace__db10-post-batch7-pre-art-merge__failed-a` (registry-only) | snap-002, attempt a |
| `2026-10-29_2104_manual-post-batch7-pre-art-merge` | `snap-002__2026-10-29_2145IST__workspace__db10-post-batch7-pre-art-merge__counted` | snap-002, the counted set — note the corrected taken-minute 2145 |
| *(no directory — five FAILED attempts 2353/0014/0018/0115/0137)* `…_manual-pre-truth3-merge` | `snap-003__…__workspace__db11-pre-truth3-merge__failed-a…e` (registry-only; the slug *was* true then) | snap-003, attempts a–e |
| Staged #3, not yet run (`run_snapshot3.py`) | `snap-003__<run-date>_<taken-HHMM>IST__workspace__db12-post-batch9-post-truth3-github-studio__counted` (verdict at sealing) | snap-003, the counted set when it lands |

Under this mapping the WORK-REGISTRY note at lines 776–784 moves to the
snap-001 entry where its numbers (967 → 1391 MB, `run_snapshot1.py` v2)
belong, and no future summary can attach it to snap-002 again — because
every name in the file would carry the seq it describes.

## 10. What I would do next, unassigned

Rename nothing. (a) File the §9 mapping as an annotation block in
WORK-REGISTRY §Snapshots and as `INDEX.md` in both store roots — the
stores become self-describing without touching a sealed directory.
(b) Rule on core-001 (OPEN-LOOPS L49) under its mapped name, so the
ruling names the thing it rules on. (c) Block the #3 run until the
runner's `NAME`/trigger/source-state are rewritten to the db12 /
post-batch9 slug — one line in the script, cheaper than a false name in
two permanent stores.

— FEEL-3
