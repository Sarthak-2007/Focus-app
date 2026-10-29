# Issue #47 — Audit R1 · heartbeat-retry — Wave-3 report (bundle lead, under PULS / C6)

Branch: `agent/issue-47` · Base: origin/main @ e29acdf · All 5 findings verified against
CURRENT main code before any fix. NOTE: the dispatch brief's paraphrase of the 5 findings
(RoutineAlarm request-code collision etc.) does NOT match the actual issue body — the body
(read via `gh issue view 47`) is about `ReleaseRetryScheduler.kt` + `RoutineHeartbeat.kt`,
and the body + `studio/audits/AUDIT-R1.txt` slice "heartbeat-retry" governed this work.

## Per-finding verdicts

1. **[CRITICAL] Healed release faults never cleared → stale fault kills a later live
   session's shield — HOLDS, FIXED-IN-CODE.**
   Evidence it held: `MonolithDao.kt` had `stampReleaseFault` (set-only) and no clear
   anywhere (grep: no caller clears `releaseFaultShield`); `ReleaseRetryScheduler.runDueRetry`
   picked the newest faulted row from the full history and, when any shield instance stood
   (e.g. a later session's), fell through to `stopShield()` on a long-past due instant.
   Cure: new `MonolithDao.clearReleaseFault(id)`; `runDueRetry` now decides via the pure
   `retryAction(...)` — `HEALED_NO_SHIELD` and `SUPERSEDED_BY_LIVE_SESSION` (an active
   session other than the faulted row exists) both clear the fault flag + cancel the alarm
   and NEVER command a stop. Fault stamps stay on the row as history.
2. **[CRITICAL] 'Deck-open' third retry is fake — HOLDS, FIXED-IN-CODE.**
   Evidence: exhausted cadence (`due == null`) returned before ever reaching `stopShield()`.
   Cure: `RetryAction.STOP_FINAL` — with both timed retries spent, the next trigger makes
   one final stop attempt, stamps `releaseRetriesDone = CADENCE.size + 1`, and closes the
   cadence (`EXHAUSTED` thereafter). The header promise is now true.
3. **[HIGH] Heartbeat has zero failure recovery — HOLDS, FIXED-IN-CODE.**
   Evidence: `RoutineHeartbeat.tick()` had try/finally only (unlock); `reconcileOnce`
   re-arms the one-shot chain at step 5, after all fallible work; `tickAsync`'s
   SupervisorJob swallowed failures. Cure: per-pass try/catch in `tick` (CancellationException
   rethrown) → `Log.e` (never silent) + `recoverChainAfterFailure()` rearms the chain at the
   pure `recoveryRearmMillis(boundary, now)` (sooner of next boundary / +60 s recovery tick,
   5 s floor so an overdue boundary can't spin), resets `lastArmedBoundaryMillis`, and the
   `pendingAgain` loop is preserved across a failed pass instead of being discarded.
4. **[HIGH] Late triggers collapse the cadence — HOLDS, FIXED-IN-CODE.**
   Evidence: after a stop, the next instant was re-derived from the original fault instant
   and `scheduleNext` set it even in the past (past `setAndAllowWhileIdle` fires instantly).
   Cure: pure `nextRetryAtMillis(fault, done, now)` — nominal instant when still future;
   otherwise `now + (offset[done] − offset[done−1])`, preserving the intended spacing
   (e.g. a day-late first attempt reschedules the second retry +270 s out, not instantly).
5. **[MEDIUM] Unbounded full-history scan per retry + full 48 h reconcile per heartbeat —
   HOLDS, FIXED-IN-CODE.**
   Cure (retry half): new targeted DAO read `getFaultedReleaseSession()` —
   `WHERE releaseFaultShield = 1 AND releaseFaultedAtWallMillis > 0 … LIMIT 1` — replaces
   `getSessionHistorySnapshot().firstOrNull{…}` in `runDueRetry`.
   Cure (heartbeat half): new `countDeriveSessionInputs(since)` COUNT over the exact union
   of the two derive() session reads; when no session is active and the count is zero, both
   reads provably return empty, so the pass derives over `emptyList()` and skips
   materialising the 48 h lists. Semantics identical — the probe is a proof, not a guess
   (proven row-by-row in `ReleaseRetryDaoTest`).

## Tests added

- `app/src/test/java/com/example/ReleaseRetrySchedulerTest.kt` (pure JVM): the full
  `retryAction` matrix (heal, supersede-never-stops, wait/timed boundaries, STOP_FINAL →
  EXHAUSTED), `nextRetryAtMillis` on-time + day-late spacing, `recoveryRearmMillis`
  sooner-boundary / +60 s / overdue-floor cases.
- `app/src/test/java/com/example/ReleaseRetryDaoTest.kt` (Robolectric, real DB): newest-
  faulted-only read, clear-keeps-stamps, and the derive-probe count union (+1/+1/+1/+0
  matrix over started-since / finalised-in-lookback / unfinalised-any-age / old-finalised).

## The 20 seats (internal lens passes, each signed)

- **Crazy-ideas** — Proposed making the final door a "persistent until proven" loop; rejected:
  an unbounded stop loop against a possibly-legit shield violates HOLD. The bounded
  STOP_FINAL + clear-on-supersede design won. — *Crazy-ideas*
- **Maximum-implementation-potential** — Pushed for extracting the whole decision into the
  pure `retryAction` enum so every branch is testable without Android; adopted — it is what
  makes F1/F2 provable in CI. — *Maximum-implementation-potential*
- **Brutal critic** — The brief's finding list didn't match the issue body; flagged and
  worked the body. Also: the old header said "a healed fault files to the record" while no
  code ever cleared the flag — documentation was lying; header now states the clear law. — *Brutal critic*
- **Improver** — `scheduleNext` still accepts a past instant from other callers; the only
  remaining caller (ViewModel fault path) schedules fault+30 s fresh, so the risk is closed
  at the scheduler's own reschedule site via `nextRetryAtMillis`. Noted, not gold-plated. — *Improver*
- **Designer** — No user-facing surface changes in this bundle; nothing to add beyond
  keeping the receipt's printed truth untouched (heals never amend the slip). — *Designer*
- **Consumer** — As the phone's owner: the failure mode that mattered was "new session's
  shield killed by an old ghost" — F1's supersede rule kills exactly that. — *Consumer*
- **Student** — Nothing to add; the cadence (+30 s / +5 min / Deck-open) is now literally
  what the header always claimed it was. — *Student*
- **User** — A user whose heartbeat died silently would just see routines stop firing with
  no trace; F3's log + recovery rearm is the difference between a ghost bug and a hiccup. — *User*
- **User-experience** — Recovery tick at +60 s (not +5 s, not next-boundary-only) balances
  battery against a missed routine start; the 5 s floor only guards the overdue case. — *User-experience*
- **Purpose-questioning** — Asked whether the retry scheduler should exist at all vs.
  trusting the shield's own poll: it must — the fault case is precisely "the shield would
  not stop", where no poll is coming. Kept, bounded, truthful. — *Purpose-questioning*
- **Thoughtful** — Clearing only the flag (keeping faultedAt + retriesDone stamps) preserves
  the record's honesty: the row still confesses it once faulted. — *Thoughtful*
- **Deep-thinker** — The supersede rule's edge: faulted row == active row is impossible by
  construction (publish happens at finish, row inactive), but the guard still compares ids
  (`active.id != faulted.id`) so a future caller can't turn the cure into the bug. — *Deep-thinker*
- **Enthusiastic designer** — Nothing visual to add in this machinery bundle; enthusiasm
  recorded for the STOP_FINAL naming — the third door finally has a name and a test. — *Enthusiastic designer*
- **Wow-level designer** — No surface work in scope; nothing to add. — *Wow-level designer*
- **Designer-29-years** — Reviewed the DAO additions for shape consistency: targeted
  single-column UPDATEs match the file's existing latch/fault style (no whole-row writes). — *Designer-29-years*
- **Intentionality designer** — Every new constant (60 s recovery, 5 s floor) is named and
  documented at its definition, not magic-numbered at call sites. — *Intentionality designer*
- **Synchronous collaboration** — Diff is checkbox-scoped to the two service files + DAO +
  models comment, so the batch-10 rebase stays cheap; no other issue's files touched. — *Synchronous collaboration*
- **Implementation-quality** — The heartbeat early-exit is semantics-preserving by
  construction (COUNT over the exact union of the two reads it replaces) and is proven
  against the real DB in the story test, incl. the unfinalised-any-age branch. — *Implementation-quality*
- **Technical execution** — Verified no other production caller depended on the old
  full-history read inside `runDueRetry` (grep: the other two `getSessionHistorySnapshot`
  callers are repository features, untouched); `tick`'s CancellationException is rethrown,
  never converted into a "failure". — *Technical execution*
- **Interaction-detail** — Deck-open remains a real trigger (`onDeckOpenRetry` untouched)
  and now actually delivers the promised third attempt; VM-init and boot triggers behave
  identically by the hints-not-mechanism law. — *Interaction-detail*

## CI

Pushed early; verdict recorded in the issue evidence comment (run URL + result).
READY-FOR-MERGE pending that CI verdict.
