# MERGE-LOG FORMAT — one line per merge (Waage spec, Kraftarm builds)

Commission 2. Purpose: Ren's merge time and the compile-queue depth
stop being feelings. Every train car that merges produces exactly ONE
line in `methods/merge-log.csv`; Restwert pastes the weekly roll-up
into `plan/WASTE-LEDGER.md`. If a merge has no line, it didn't happen
— same law as the rest of Sweep 2.

## The line (CSV, append-only, header written once)

```
car,merger,queue_depth_at_start,merge_start,merge_end,wall_s,compile_s,test_ran,tests_n,result,flags_held_by_others,notes
```

| Field | Format | Meaning |
|---|---|---|
| `car` | text, no commas | train car / lane name as merged (e.g. `slide`, `shieldpick-cap`, `batch1`) |
| `merger` | text | who merged — today always `ren`; field exists so a second integrator is visible the day one appears |
| `queue_depth_at_start` | int | number of `builds/compile-*.flag` files present (excluding own) at merge start — the contention the merge was born into |
| `merge_start` / `merge_end` | ISO local `YYYY-MM-DDTHH:MM:SS` | wall clock, from the helper's own timestamps — never reconstructed afterwards |
| `wall_s` | int | end − start, whole merge operation: checkout/apply + compile + test + commit |
| `compile_s` | int or empty | the gradle BUILD-line seconds inside the merge, if a compile ran (Law 11: once per car) |
| `test_ran` | `full` / `subset` / `none` | what test scope the merge ran |
| `tests_n` | int or empty | tests executed, from test XML count when available |
| `result` | `merged` / `failed-compile` / `failed-test` / `failed-io` / `abandoned` | first failure class wins; vocabulary fixed — new classes need a Waage ruling, not improvisation |
| `flags_held_by_others` | int | compile flags held by other lanes *during* the merge compile (peak, sampled by helper) — separates "slow merge" from "contended merge" |
| `notes` | text, no commas (use `;`) | one clause max: the waste, if any (`waited 240s for queue slot`; `dex io fail; rerun`) |

## Rules

1. **One line per merge attempt**, including failed ones — a failed
   car that is retried gets a second line; the pair is the rework cost.
2. **Queue wait is part of the merge.** If the integrator waits for a
   Law-3 slot before starting, that wait goes in `notes` as
   `waited Ns` and is also counted: Stellwerk's queue stats derive
   from these lines, not from a second log.
3. **Timestamps are captured, never remembered.** The helper wraps the
   merge commands; a line typed from memory an hour later is a
   fabrication and Restwert should strike it.
4. **No line, no merge.** If the helper wasn't used, the merger writes
   the line by hand in the same minute, marking `notes` with
   `manual`. Manual lines are audited against git log by Restwert.

## For Kraftarm (the helper)

`merge-car.sh <car> -- <merge command…>`:
1. Count `builds/compile-*.flag` → `queue_depth_at_start`; stamp start.
2. Run the command; while it runs, sample flag count every 10 s →
   peak = `flags_held_by_others` (excluding a `compile-methods.flag`
   and the merger's own `compile-trunk.flag` if held by this process).
3. On exit: stamp end, append the CSV line (flock the file — two
   merges must never interleave a line), print the line back.
4. Derive `compile_s` by grepping the merge's gradle log for the last
   `BUILD … in Ns` line; `tests_n` from the newest test-results XML.
5. Exit code passes through unchanged. The helper never retries,
   never stops a daemon, never touches another lane's flag.

## Roll-up (Restwert → WASTE-LEDGER, weekly or per train)

From the CSV, four numbers only: merges count · median `wall_s` ·
total queue-wait seconds (from `waited Ns` notes) · failed-merge
count by `result` class. Pasted as one ledger block:

```
MERGES <period>: n=<k> median_wall=<s>s queue_wait_total=<s>s failures=<class:count …> (source: methods/merge-log.csv)
```

— Waage, Methods · measurement
