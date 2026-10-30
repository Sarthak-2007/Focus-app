# Issue #309 — Audit R5 supplement, AppBlock Second-pass findings (7 findings)
## WELLE-7 cluster E verification record — Ersatz (lead, replacement)

Verified against origin/main at branch base. Code cures on this
branch for F2 (exact alarms), F5 (board order), F7 (package
receiver). F3 and the F4 soft-cap demand are struck against
Sarthak's settled lock law; the rest STANDS-BLOCKED, priced.

Seats (≥10, platform/design mixed): Builder ×2, Technical
execution, Diagnostician, Verifier ×2, Brutal critic,
Purpose-questioner, UX designer, Implementation-quality,
Day simulator, Consumer, Keeper of Record. Thin seats
LEAD-COVERED; none dropped.

## F1 [HIGH] No out-of-process watchdog — STANDS-BLOCKED
Stands: manifest declares the three enforcement services and no
monitor; WatchStore is in-process/in-memory; shield returns
START_STICKY. Mitigations on main, on record: MonolithBootReceiver
re-raises the shield before AND after a forced heartbeat
reconcile on boot/package-replaced; clock changes force the same
reconcile; RoutineHeartbeat re-derives from stored state on many
triggers (resume, shield loop, session edges), so a process
death is recovered at the next trigger rather than never.
Exact blocker: a true dead-man check needs persisted liveness
stamps (WatchStore facts are memory-only) plus a scheduler that
survives the process (AlarmManager/WorkManager — WorkManager is
not in the dependency list) in a separate process — the same
architecture build as #303 F1. Price: shared with #303 F1,
2–4 days + on-device soak. Not faked here.

## F2 [HIGH] Clock re-arm + exact alarms — FIXED-IN-CODE (this branch, main half)
Already on main: clock changes route through MonolithBootReceiver
(TIME_SET / TIMEZONE_CHANGED in the manifest) into a FORCED
RoutineHeartbeat reconcile — the whole app re-derives and
re-arms on a moved clock.
Cured here: the chain alarm was inexact-only
(RoutineScheduler.rearmChain, setAndAllowWhileIdle). Now:
SCHEDULE_EXACT_ALARM declared; the chain arms
setExactAndAllowWhileIdle when the grant is held (pure
exactAlarmPermitted decision — API <31 always permitted),
inexact fallback otherwise. Tests: Issue309SecondPassTest.
Stands-blocked remainder: per-service individual clock receivers
(AppBlock ships one per process). With one process and one
forced central reconcile, per-door receivers add no re-arm the
central path lacks; they only matter after the #303 F1 process
split. Priced there; not duplicated here.

## F3 [HIGH] Temporary unlock / auto-relock — VERIFIED-FALSE (struck, clashes with his settled law)
Sarthak's settled lock design (standing, Oct 29/30): the lock is
final — unbreakable — with exactly ONE emergency break per week
via a continuous 60-second hold. A temporary-unlock state that
re-locks afterwards, priced separately or instead, is a second,
softer exit shape he has already ruled out; building it would
override his verdict with a rival's feature list. Struck with
that evidence, never silently skipped. The current exit matches
his law: MonolithRepository.completeEmergencyBreak spends the
weekly token (EmergencyBreakSpendEntity, MonolithModels.kt) and
refuses at zero tokens.

## F4 [HIGH] Emergency exit soft cap + reason survey — SOFT CAP STRUCK, REASON CAPTURE STANDS-BLOCKED
The metered-but-never-removed hatch contradicts the same settled
law as F3 (hard 1-per-week cutoff is his design, not an
oversight) — struck.
Reason capture is compatible with his law and stands:
EmergencyBreakSpendEntity stores sessionId / spentAtWallMillis /
weekAnchorMillis / heldSeconds only — no reason column, nothing
learned from a break. Blocker/price: a reason column is a Room
schema change (v12 is deliberately frozen; new state goes to
prefs — a prefs-side reason log keyed by spend timestamp avoids
the migration) plus a reason step in the disarm flow UI without
weakening the 60-second hold. Estimated half a day
(prefs variant) — queued behind the disarm surface's design
pass, not bolted on here.

## F5 [HIGH] Cheapest-first permission ask — FIXED-IN-CODE (board half, this branch); instant payoff STANDS-BLOCKED
Cured here: WatchStore PERMISSIONS/COUNTED board order led with
ACCESSIBILITY + DEVICE_ADMIN; it is now cheapest-first —
USAGE_ACCESS (read-only) leads, OVERLAY and NOTIFICATION_LISTENER
follow, ACCESSIBILITY and DEVICE_ADMIN close. Every surface that
reads the list (Guard line prints, PARK board, first-missing
prompts) now meets the cheap asks first. Test:
Issue309SecondPassTest.boardLeadsCheapestFirst.
Stands-blocked: the instant payoff (a personalised usage report
the moment Usage Access lands). The data path exists
(MonolithRepository.queryRealTodayUsageMinutes feeds catalogue
seeding only) but no first-run report surface exists; building
one is a first-run experience build (design panel territory —
his Oct 30 order puts design judgement on the panel).
Estimated 1–2 days after a panel-ruled design.

## F6 [HIGH] Strict Mode schedulable / routine lock binding — STANDS-BLOCKED
Stands: RoutineEntity (MonolithModels.kt) has no lock/strict
field; protection exists only inside an armed session.
Blocker: (a) a lock flag is a Room schema change against the
frozen-v12 convention; (b) routines themselves are under
Sarthak's Oct 30 rethink order ("The routine thing clearly
needs more work, im not sure if it even is needed") — binding a
new enforcement object to a feature whose existence he is
reconsidering is building on sand. Price: half a day of schema +
derive wiring AFTER the routines rethink verdict lands. Named,
priced — not built ahead of his verdict.

## F7 [MEDIUM] No package-change receiver — FIXED-IN-CODE (this branch)
Cured here: MonolithPackageReceiver (manifest-registered,
PACKAGE_ADDED / REMOVED / REPLACED / CHANGED, package scheme)
forces the existing catalogue reconcile at install time —
ensureSeededAndSyncPackages(forceCatalogSync = true) — with
BootReceiver-grade containment (supervised scope, goAsync
finished exactly once). The reconcile's own law is unchanged: a
new package is discovered with its remembered/default ruling;
a removal is only noted (prune still needs a second
corroborated absence). Enforcement's shield loop re-reads the
DAO every 2s, so a reconciled catalogue is live immediately.

Signed: Ersatz, WELLE-7 cluster E lead (replacement).
