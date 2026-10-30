# FEEL-5 — The Two Locations and the Morning After: what a stranger finds

Beat: ANKER's cell, GitHub issue #17 (Preservation — snapshot #3).
Evidence only, gathered read-only Fri 2026-10-30 ~03:26–03:40 IST from:
Location A `~/workspace/preservation/monolith-snapshots/`, Location B
`~/workspace/your_files/monolith-archive/snapshots/`, all six
`SNAPSHOT.md` files therein, `~/workspace/preservation/run_snapshot3.py`,
`run_daily.py`, `snapshot1-run.log`, `snapshot3-run.log`, and `ls`/`du`/`df`/
`wc`/`diff` over both homes. Nothing run, nothing copied. Where the brief's
premise was not in the surviving artifact, that is filed as such (F-9).

## Findings (one line each, evidence attached)

- F-1 NO INDEX EXISTS ANYWHERE: neither home, nor `monolith-archive/`, nor `preservation/` contains any README/INDEX/registry page (`ls -la` of all four dirs: only snapshot folders, runners and logs; `find -iname '*index*'/'*readme*'/'*registry*'` at both roots: no hits) — the state of the whole backup system can only be reconstructed by opening 3 folders × 2 locations × 1 `SNAPSHOT.md` each and comparing by hand.
- F-2 "TWO LOCATIONS" READS AS TWO COPIES, PROVES AS ONE DISK: `df` for both paths returns the identical line `/dev/mapper/rv 150G … /home/hatch`, and `du` is mirror-exact (551M + 944M + 440K in A, 551M + 944M + 440K in B) — a stranger sees redundancy theatre: ~1.5 GB duplicated, ~3.0 GB total, zero protection against the disk/VM/workspace event that takes both.
- F-3 LOCATION B SITS UNMARKED INSIDE THE USER'S OWN DELIVERABLES: `ls ~/workspace/your_files/` shows `monolith-archive/` (18 bytes of directory, no explanatory file at its root) filed between a 42 MB Muse APK, GMC videos and UT-7 posters/PDFs — nothing at the `monolith-archive/` or `snapshots/` level says "these are backups, do not delete, do not tidy"; a ~1.5 GB, 9,324-file tree of `trunk/ lanes/ swarm/ records/ apks/` presents to Sarthak browsing his own files as clutter that wandered in, not as an archive with a keeper.
- F-4 A STRANGER OPENING SNAPSHOT #1/#2 LEARNS THE VERDICT LAST AND THE MEANING NEVER: both `SNAPSHOT.md`s are 10–15 lines of insider bullets (`trigger: manual-first`, `DB v9`, `spec S4`, `spec S5 step 6`, `Anker (Keepers of Record, preservation seat)`) with `restore-test: PASS …` as the final bullet only; neither says what Monolith is, why this state mattered, how to actually restore from it (no command, no procedure — `grep -i restore` returns only the PASS line and the manifest-exclusion note), or what the reader should do/never do with the folder.
- F-5 THE ONE PLAIN-LANGUAGE FILE IS THE FAILED ONE: `core-001/SNAPSHOT.md` is the only certificate a stranger can actually read (headed `PARTIAL, honest status`, explains why, lists what is missing) — yet at folder-list level it sits indistinguishable from the two counted snapshots (same level, same filename), its PARTIAL status exists only inside the file, and its text is already stale inside its own artifact (`reports pending at filing time`, `the shelving is just uneven`, a superseding full snapshot `owed and armed` that, per F-8, has since failed invisibly).
- F-6 A AND B GIVE THE STRANGER NO WAY TO TELL WHICH COPY IS REAL: `diff` of #2's `SNAPSHOT.md` across A and B returns IDENTICAL (same for #1), the texts never name the other location or explain why the duplicate exists, and the only orientation offered is the phrase `restore-test: PASS … from Location B` — a morning-after reader cannot tell canonical from copy, or whether deleting one home loses anything.
- F-7 EVEN THE TIMELINE ON THE DOOR IS WRONG ON #2: the folder is named `2026-10-29_2104_manual-post-batch7-pre-art-merge` while its own `SNAPSHOT.md` says `taken: 2026-10-29 21:45 IST` and its copy ran `21:44–21:45` — the name a stranger sorts and cites by is the failed attempt's start time (per its own copy-history paragraph, the `2102` attempt), not the preserved state's time; #1 by contrast matches (`1148` / `11:48`), so the two certificates do not even follow one naming honesty.
- F-8 THE MORNING AFTER SNAPSHOT #3, BOTH LOCATIONS LIE BY OMISSION: neither A nor B contains any #3 folder, any FAILED marker, or any note that a run happened — the entire trace lives outside the archive, in `~/workspace/preservation/snapshot3-run.log` (`FAIL-STOP: payload copy did not converge after 12 attempts`, 2026-10-30_0137) and in the hidden leftover `.staging-snapshot-3/2026-10-30_0137_manual-pre-truth3-merge/` containing exactly 1 file, `swarm/MAXIMPL.md`, 0 bytes; a browse of A/B the next morning shows a calm, complete-looking 2 PASS + 1 PARTIAL and no reason to ask any question.
- F-9 FAILURE HISTORY IS SELF-REFERENTIAL, WHICH IS HOW IT SAT UNNOTICED: the failed `2102` attempt exists only as a prose paragraph inside its replacement's `SNAPSHOT.md` (#2, copy-history bullet) and the registry narrative lives inside snapshot #2's own payload (`swarm/plan/WORK-REGISTRY.md`) — to learn a snapshot failed you must already be inside a snapshot; honesty note on the brief's other premise: the surviving manifests are NOT truncated (`wc -l` = 3,325 and 5,999 lines, exactly the file counts their `SNAPSHOT.md`s claim, in both homes), so that flaw, if real, belonged to a superseded attempt whose only trace is the prose above — an external one-page register (F-1) is precisely the surface that would have caught either failure the same night.
- F-10 DAILY AND MANUAL READ AS TWO STRANGERS SHARING A DISK, NOT ONE POLICY: in both locations there are zero `*_daily` folders and no `.staging-daily` exists, while the manual line's #2 certificate admits the daily duty was absorbed ad hoc (`the two owed snapshots discharged as one current-state snapshot: … and the 2026-10-29 daily`) — the daily line has never left a single artifact where a reader of the archive could see it.
- F-11 THE TWO RUNNERS DIVERGE IN THEIR OWN WORDS: `run_daily.py`'s docstring lists its deltas from `run_snapshot3.py` (different staging parent `.staging-daily`, widened prune set adding `guh2, tmpdir`, generic-daily source-state text replacing the manual runner's specific merge narrative, symlink count recorded only in daily), and its `SNAPSHOT.md` template hard-codes `- copy history: daily run 2026-10-30, memory-gated per Anker's letter (single check >= ~2,500 MB …)` while its code gates once at `MemAvailable < 350` — a future daily certificate would misstate its own date context and its own gate, in a format no manual certificate shares.

## Verdict

Individually, #1 and #2 are well-certificated; as a place a person returns
to, the archive fails the morning-after test: no index, no plain verdict,
no restore instructions, an unmarked 1.5 GB duplicate inside the user's
own files, a PARTIAL shelved as an equal, and a failed #3 that left no
mark in either location it was meant to fill. The ceremony is meticulous
inside the run and sloppy at the door — and the door is all the morning
after ever sees.

— FEEL-5
