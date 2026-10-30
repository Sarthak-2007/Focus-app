# FEEL-4 — The Long Middle: what the operator actually watches while a snapshot runs

Beat: ANKER's cell, GitHub issue #17 (Preservation — snapshot #3).
Evidence only: `~/workspace/preservation/run_snapshot3.py` (console surface =
its `print` calls), `snapshot1-run.log` (the one successful run on record:
16 lines, 1,254 bytes, for 3,325 files / 568,973,290 bytes), and
`snapshot3-run.log` (29 lines, 12 failed staging attempts). No vibes below
without a quoted artifact. Every proposed fix is print-only — no change to
what is copied, hashed, verified, or retried.

## Finding 1 — The payload copy is one unbroken silence, start to COUNTS

`copy_tree()` and `copy_file()` in run_snapshot3.py contain **zero** `print`
calls. The only prints inside `stage_payload()` are one line, midway:

```python
print(f"LANES {lane_names}")
```

and the counts are only emitted after *all five* sections finish, in `main()`:

```python
print(f"COUNTS {counts}")
```

So the successful run's screen (snapshot1-run.log, verbatim, in order) is:

```
NAME 2026-10-29_1148_manual-first
MemAvailable at start: 967 MB
LANES ['counterfoil', 'drawer', 'kitproof-token', 'probe-heizung', 'routines', 'shieldpick', 'slide', 'token']
COUNTS {'swarm': 1484, 'trunk': 136, 'lanes': 1575, 'records': 111, 'apks': 19}
```

Dead air, named:

- **Before `LANES`:** swarm (1,484 files) + trunk (136 files) copy with no
  output at all — the operator's first signal that anything is happening is a
  lane *name list*, which reads as configuration, not progress.
- **After `LANES`:** lanes (1,575 files — the largest section), records (111)
  and APKs (19, the largest bytes) copy with no output until `COUNTS`. There
  is no per-section start/done, no file counter, no current filename, no bytes
  counter anywhere in the run.

This is exactly the shape that has already burned this studio once: a frozen
log is indistinguishable from a hung process, because the log freezes *by
design* for the entire multi-minute copy. An operator who kills a "hung"
snapshot mid-copy would be killing a healthy ceremony.

Smallest fix (print-only): in `copy_tree()`, print a section-start line and a
progress line every 250 files (`STAGE swarm 250/… files`), and print each
section's count as it completes instead of only in the final `COUNTS` dict.
Integrity untouched: the manifest/verify chain still decides truth; the
counter is narration.

## Finding 2 — Retries are invisible while they run; only their deaths print

The staging retry loop prints only in the `except` branch:

```python
except OSError as e:
    last_exc = e
    print(f"STAGE-ATTEMPT {attempt} died: {type(e).__name__}: {e}")
    print(f"MemAvailable: {mem_available_mb()} MB; backoff 25 s, resuming")
    time.sleep(25)
```

There is **no attempt-start line**. Consequences, all visible in
snapshot3-run.log:

- During attempt N+1's entire copy, the newest line on screen is attempt N's
  *death*. The operator cannot tell "in the 25 s backoff" from "back in the
  copy" from "hung in the copy" — all three are the same frozen two lines.
- The 25 s backoff itself (`time.sleep(25)`, ×12 attempts = 5 minutes of pure
  sleep in snapshot #3, plus copy time) has no countdown, no wake time, no
  heartbeat. Twelve times, the screen promises `backoff 25 s, resuming` and
  then says nothing about whether resuming happened.
- Worst: **convergence is invisible, so "riding the flicker" and "dying
  slowly" print identically.** The runner's whole integrity story is
  resumability — `copy_file()` skips a destination that is already identical
  (size + sha256) — but no print ever reports how many files were skipped vs
  copied. Snapshot3-run.log's twelve attempts are verbatim-identical except
  one number, and that number trends the wrong way:

```
STAGE-ATTEMPT 1 died: PermissionError: [Errno 1] Operation not permitted
MemAvailable: 2251 MB; backoff 25 s, resuming
STAGE-ATTEMPT 2 died: PermissionError: [Errno 1] Operation not permitted
MemAvailable: 2146 MB; backoff 25 s, resuming
...
STAGE-ATTEMPT 12 died: PermissionError: [Errno 1] Operation not permitted
MemAvailable: 1396 MB; backoff 25 s, resuming
FAIL-STOP: payload copy did not converge after 12 attempts: [Errno 1] Operation not permitted
```

  An operator reading this live sees: same error, memory bleeding
  2,251 → 1,396 MB, zero evidence any attempt got further than the last.
  That *reads* as dying slowly even if every attempt was in fact converging.
  Note also what is absent: no `LANES` line appears anywhere in
  snapshot3-run.log, which means every attempt died inside the swarm or
  trunk copy (sections 1a/1b, before the `LANES` print) — and the death line
  cannot even say that, because the `PermissionError` string carries no
  filename and no print records the file/section in flight or the count
  staged so far. Twelve attempts, and the log cannot answer "how far did
  the best attempt get?"
- The resume path makes the silence *longer* on each retry, silently: to
  skip an already-staged file, `copy_file()` runs `sha256_of()` on both the
  destination and the source. A retry therefore re-walks and re-hashes
  everything already staged before copying anything new — minutes of work
  that produce no output and look exactly like a stall.

Same defect in the location writes: `shutil.copytree()` of the full tree to
A, then to B, is monolithic and print-free; the loop prints only on failure
(`WRITE {dst} attempt {attempt} died: …; backoff 25 s`) and once on success
(`WROTE {dst}`). A whole-tree copy is either a long silence or a death line,
nothing between.

Smallest fix (print-only): print at attempt *start* —
`STAGE-ATTEMPT {n} starting (resuming)` — and have `copy_file()` count
skipped-vs-copied, printed per section per attempt
(`attempt 7: swarm skipped=1402 copied=31`). That one counter is the entire
difference between the two readings: a rising skipped count *is* "riding the
flicker," proven on screen; a flat one is an honest early warning of dying.
For the backoff, print the wake time (`backoff 25 s, resuming at HH:MM:SS`)
or a single heartbeat line when the sleep ends. For location writes, print
`WRITE {dst} attempt {n} starting` before `copytree`.

## Finding 3 — No line in any run carries a timestamp, so the log has no clock

Every `print` in run_snapshot3.py is timestamp-free; the script captures
`NOW` once (for the snapshot *name*) and never prints elapsed time. In a
night of wall-clock retries, the log itself cannot answer the operator's
first question — "how long ago was that last line?" — except via file mtime
outside the log. The only thing that changes across snapshot3-run.log's
retries is `MemAvailable`, i.e. the closest thing to a clock on screen is a
memory gauge falling 855 MB. That is a progress substitute that points
backwards.

Smallest fix (print-only): prefix each print with IST time (the script
already defines `IST`) or at minimum print elapsed seconds on the
attempt/verify/summary lines. One helper, no logic change.

## Finding 4 — The back half is four more silent full-tree hash passes

After `COUNTS`, the ceremony does, in order, with no progress output inside
any of them (`build_manifest()` and `verify()` contain no prints):

1. `build_manifest(STAGE)` — hashes all 3,325 files → then one line:
   `FILES 3325 BYTES 568973290 MANIFEST_SHA256 11ada2f4…`
2. `verify(STAGE, entries)` — hashes all 3,325 again → `STAGE-VERIFY ok=3325 fail=0`
3. `verify()` on A, then on B → `VERIFY-A ok=3325 fail=0 manifest_matches=True`,
   `VERIFY-B ok=3325 fail=0 manifest_matches=True`
4. Restore `copytree` from B (silent) + `verify(SCRATCH, entries)` (silent) →
   `RESTORE ok=3325 fail=0 files=3325 bytes=568973290`

Five full passes over ~569 MB, each announced only by its verdict after it
finishes. The verdicts are the integrity core and are good — terse, exact,
grep-able. But between `WROTE …B` and `RESTORE-TEST PASS` the operator again
watches a frozen log through the longest sustained work of the run, with no
way to tell pass 2 of 5 from pass 5 of 5.

Smallest fix (print-only): a start line per pass (`VERIFY-B starting,
3325 files`) and a counter every 500 files inside `verify()` /
`build_manifest()`. Verdict lines unchanged.

## Finding 5 — SUMMARY arrives as one more log line, not as the earned moment

The successful run's last three lines (snapshot1-run.log, verbatim):

```
RESTORE-TEST PASS
MemAvailable at end: 1391 MB
SUMMARY name=2026-10-29_1148_manual-first files=3325 bytes=568973290 manifest_sha256=11ada2f4fa16e947b28c1911f7c1ce8bc953dbd952e68a375c9dce3fe4e05fcf restore=PASS spots=plan-md:SNAPSHOT-SPEC.md:True trunk-kt:ExampleInstrumentedTest.kt:True mail-letter:1158__from-hale__every-screen-review-build1.md:True
```

Why it lands flat, on the evidence:

- **No visual break.** No blank line, no rule/separator, no heading — SUMMARY
  is the same ALL-CAPS key=value format as the 15 lines above it, differing
  only in length. In the log file it is literally the longest line, which
  makes it the *hardest* to read, not the most prominent.
- **It repeats, it doesn't conclude.** Every value in SUMMARY already
  appeared verbatim earlier (`FILES … BYTES … MANIFEST_SHA256 …`,
  `RESTORE ok=…`, `SPOT-OPEN …`, `RESTORE-TEST PASS`). It adds no new fact —
  critically, **no duration and no attempt count**: the two numbers that
  would make the ending feel earned ("converged on attempt 7, 43 minutes")
  are the two numbers the runner never records on screen. A ceremony that
  survived a flickering night and a ceremony that sailed through in one
  attempt produce the *same-shaped* ending.
- **The real climax is spent a line early and in passing:** `RESTORE-TEST
  PASS` — the moment the snapshot proves it can come back — is a 17-character
  line sandwiched between `SPOT-OPEN …` and `MemAvailable at end:`, formatted
  identically to a debug line.

Correction to the brief's premise, on the evidence: in the *successful* run
SUMMARY is not buried among hundreds of lines — the whole log is 16 lines.
The flatness is not burial, it is *sameness*: 16 lines in one register, so
the last one has no register of its own. (In a retry-heavy success the
burial version would also occur: snapshot3's shape shows attempt noise
scaling the log while SUMMARY's format stays fixed.)

Smallest fix (print-only): before SUMMARY, print a blank line + a rule
(`———`), and add the two missing facts the runner already knows or can
trivially track: `attempts={n}` and `elapsed={s}` (start time captured next
to `NOW`). Keep the SUMMARY key=value payload byte-identical after the rule
so anything grepping it still works.

## Summary of dead-air moments (one line each)

1. Swarm+trunk copy (1,620 files) before the first progress-adjacent line —
   fix: per-section start + every-250-files counter in `copy_tree()`.
2. Lanes+records+APKs copy (1,705 files) after `LANES` until `COUNTS` —
   fix: same counter; print each section count as it completes.
3. Every retry attempt while it runs (only deaths print) — fix:
   attempt-start line.
4. Every 25 s backoff (×12 in snapshot #3) — fix: print wake time / heartbeat
   on resume.
5. Resume re-hash walk on each retry (skip-check hashes both copies,
   silently) — fix: report skipped-vs-copied counts per attempt; this is
   also the only on-screen proof of convergence vs slow death.
6. Location writes A and B (monolithic `copytree`) — fix: attempt-start line
   per write.
7. Manifest build + stage/A/B/restore verifies (5 silent full-tree hash
   passes over 569 MB) — fix: pass-start line + every-500-files counter in
   `verify()`/`build_manifest()`.
8. Whole run: no timestamps, no elapsed time anywhere — fix: IST prefix or
   elapsed seconds on status lines.
9. SUMMARY: same format as every debug line, repeats earlier values, omits
   attempts and duration — fix: blank line + rule before it, add
   `attempts=` and `elapsed=`; payload unchanged.

None of the nine fixes touches a hash, a copy rule, a retry count, a backoff
duration, or a verdict. The ceremony's integrity design is sound on the
evidence of snapshot1-run.log (3325/3325 at every verify, restore PASS);
what is missing is solely that the operator watching it can *see* it being
sound, in the middle, while there is still time to lose trust.

— FEEL-4
