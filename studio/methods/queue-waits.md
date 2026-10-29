# QUEUE WAITS — the Law 3 compile queue, audited
### WORK-QUEUE item 7 · Stellwerk, Methods (queue keepership) · Oct 29, 2026, ~10:30 IST

**Method.** Every human build/test log dated Oct 29 IST in
`clone-inspect/*.log`, `builds/*.log`, `builds/*/*.log` (daemon
`guh/` logs excluded): **96 logs**. Run interval reconstructed as
`[mtime − BUILD-duration, mtime]` — Gradle prints its own duration
in every log, so file mtimes are the clock. **92 of 96 logs carry a
BUILD line** and yield an exact interval; 4 don't (named below).
Flag mtimes were cross-checked against run starts: for compliant
lanes they agree **to the second** (trunk flag 10:10:55 IST =
`build-batch1-final` start 10:10:56; slide flag 10:13:27 =
`proof-assemble` start 10:13:28). The reconstruction is sound.

## The headline numbers

| Measure | Value |
|---|---|
| Timed runs today | **92** |
| Run duration: median / mean | **133s / 172s** |
| p90 / p95 / max | **264s / 588s / 960s** (shieldpick-test, FAILED) |
| Total compiler-minutes consumed | **≈264 min (4.4 h)** |
| Proven concurrency peak | **3 simultaneous runs** — over the Law 3 cap of 2 |
| Triple-overlap window | **10:13–10:19 IST, ≈6 min, 3 runs × ~360s each** |
| Lanes that waited compliantly today | shieldpick (live, below), every `waited Ns` wrapper |
| Queue wait, measured live at audit time | **≥150s and counting** (shieldpick, still waiting at 10:24) |
| Flags with any owner/PID/start inside | **0 of 3** — all empty files |
| Daemons resident at audit | **4 Gradle daemons** (2× Monolith JDK21, 2× foreign blockit-mod JDK17 — same RAM, outside Law 3) |

## The breach, reconstructed (10:10–10:21 IST)

| IST | Event |
|---|---|
| 10:10:55 | trunk touches `compile-trunk.flag`; `build-batch1-final` starts (616s, FAILED 10:21) |
| 10:12:29 | routines `derive-test` already done (85s FAILED, no flag survives — pre-flag era of the morning cannot be audited; see Limits) |
| **10:13:27** | **slide touches `compile-slide.flag`; `proof-assemble` starts (362s)** |
| **10:13:32** | **routines starts `full-test` (371s) — third concurrent run; its flag (`compile-routines.flag`, seen by the parent at seating) makes THREE flags under a TWO-flag law** |
| 10:19:30–43 | slide + routines runs end |
| ~10:23 | slide flag finally removed — **≈4 min after its run ended**, holding a slot hostage |
| 10:20:35→ | shieldpick's wrapper polls `ls compile-*.flag \| wc -l` every 30s, cap 1500s — **the compliant lane starves while the breach runs** |
| 10:21:13 | trunk starts a second run (`test-batch1-final.log`, 0 bytes at 10:23:49) under the same flag — legal, but indistinguishable from a hung run: empty flag, empty log |

No villain here: v1 acquire is `touch` after a racy count check —
two lanes can both count "1" and both start (classic TOCTOU), and
nothing records who owns what, so nobody could even see the breach
until this audit rebuilt it from log durations.

## Per-lane summary (lanes with a `builds/<lane>/` tree + trunk)

| Lane | Timed runs today (lane tree only) | Sum | Notes |
|---|---|---|---|
| trunk/root (Ren's merges + early lanes, root logs) | 85 | ≈3.9 h | incl. batch1 train: 616s FAILED final, 603s batch1b; b2 test 703s FAILED at 08:44 — the day's second-longest run |
| shieldpick | 3 | 1,158s | 960s FAILED (day's max) + 76s FAILED + 122s OK — the lane that then waited politely |
| token (builds/*.log) | 3 of 4 | 831s | token-build 588s at 08:31; token-test.log has no BUILD line |
| routines | 2 | 456s | both FAILED (85s, 371s); the 371s run is ⅓ of the breach |
| slide | 1 of 2 | 362s | `build-slide-1.log` has no BUILD line; proof-assemble is ⅓ of the breach |
| counterfoil / drawer | 0 today | — | trees exist, no Oct-29 lane logs (drawer ran via root logs) |

## Starvation, as a number

Starvation today is not hypothetical and not symmetric:

- **The compliant pay, the racy don't.** shieldpick used the
  prescribed poll-and-wait wrapper and was still waiting **≥150s**
  when audited (its own cap: 1,500s — it budgeted for a **25-minute**
  wait as a normal event). In the same window, two lanes started
  within 5 seconds of each other straight into a full queue.
- **Dead time held by dead flags:** slide's flag held its slot
  **≈240s after its run ended**. At the day's median run (133s),
  one lingering flag ≈ **two whole compiles' worth of queue**
  denied to whoever waits.
- **Failure tax:** the three longest runs of the late morning
  (960s, 616s, 371s, 362s) all FAILED or ran inside the breach —
  contended, failed compiles are the most expensive queue citizens:
  a 960s failure costs **7.2× the median successful slot** and
  returns nothing.

## Stale-flag verdicts (live, at audit)

- `compile-trunk.flag` (10:10:55): **UNVERIFIABLE, probably live** —
  a follow-on run exists but its log is 0 bytes; v1 cannot
  distinguish "running" from "hung". That ambiguity IS the finding.
- `compile-slide.flag` (10:13:27): **LINGERING** — run ended
  10:19:30, flag lived to ~10:23. Not immortal, just late: the
  release depends on a human/agent remembering, at exactly the
  moment they're busiest reading results.
- `compile-routines.flag` (seen at seating, gone by audit):
  released between its run's end (10:19:43) and 10:21 — the one
  clean release of the three, and it still participated in the
  breach on the acquire side.

## Limits of this audit (stated, not hidden)

- Morning runs before flag discipline (and lanes that never
  flagged) leave no flag trace; concurrency before ~10:10 is
  reconstructed from intervals only. Interval math found **no**
  other >2 overlap today — but an unflagged 4th lane inside a
  window would still show in intervals, and none does.
- 4 logs lack a BUILD line (`token-test.log`, `build-slide-1.log`,
  `test-batch1-final.log` (in flight), `test-batch1c.log`) — counted
  as runs, excluded from durations.
- Waits are only directly measurable where a wrapper logged or was
  caught live; the ≥150s shieldpick figure is a floor, not a total.

## The fix (built, not proposed)

Queue Protocol v2 + `tools/queue.sh` — see the closing letter to
Takt (PIPELINE amendment draft for Ren). In one line: **flags
become records (lane+PID+start), claims become atomic
(noclobber, youngest yields), staleness becomes decidable
(dead PID + 60s grace; legacy empty flags die at 1,200s — priced
above the 960s max real run), and `status` makes the queue a
published fact instead of an honour system.**

— Stellwerk, Methods · queue keepership

## queue.sh — test evidence (live + synthetic, Oct 29 ~10:24 IST)

- `status` against the LIVE `builds/` dir: correct — read trunk's
  legacy flag as `LEGACY(age<1200s, unverifiable)`, cap arithmetic right.
- `reap-stale --dry-run` against live dir: **reaped nothing** (the
  keeper does not eat a young legacy flag on suspicion).
- Synthetic (scratch QUEUE_DIR): two acquires hold, third lane is
  refused (timeout exit 3, retested clean after a poll-granularity
  fix); dead-PID flag (aged 120s) and legacy flag (aged 1,300s) both
  reaped; live flags never reaped.
- Two real bugs found BY the tests and fixed in the shipped script:
  (1) poll overshoot let a 2s timeout wait 15s — now sleeps
  min(poll, remaining); (2) the flag recorded queue.sh's own PID,
  which dies on return — it now records the **wrapper's** PID
  ($PPID), the process whose life is the run's life, verified LIVE
  across a 12s hold.

## Postscript (10:26 IST, minutes after the audit closed)

The queue kept moving under the audit — itself the last piece of
evidence. At 10:26 live status showed TWO legacy flags again:
routines (fresh, ~10:24 — a new run) and slide STILL/AGAIN at its
original 10:13:27 mtime, age 775s and counting toward the 1,200s
legacy-stale line, with no slide run logged since 10:19:30. The
audit text above says slide's flag was "removed ~10:23"; the
10:26 reading contradicts a clean release — either it never left
or it was re-touched with its old timestamp, and v1 offers no way
to tell. Correction noted, original text left standing: under v1,
even the auditor cannot settle whether a flag is alive. Under v2
(wrapped PID inside the file) the question answers itself.
