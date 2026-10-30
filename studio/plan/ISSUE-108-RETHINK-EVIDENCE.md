# ISSUE #108 — RETHINK EVIDENCE (Audit R3 journey: routines-lived)

**Status: RETHINK-PENDING.** Routines are frozen by Sarthak's order (Oct 30,
05:07 IST: "Routines as a whole should be rethought"). This file is evidence
for the deliberation — NO routine code was changed, nothing here is a fix.
Verified by REISE (cluster lead, wearing Verifier + Diagnostician seats)
against origin/main @ 0a00e95 in cell builds/cells/issue-108.

## Per-finding verdicts

### F1 [CRITICAL] Routine firing silently rewrites the hand-set catalogue, never restores — STANDS
- `data/MonolithRepository.kt:1086-1104` `materialiseRoutineSession()` still
  calls `applySmartLoadout(loadout)` then `startSession(...)` — every firing
  recomputes and persists EVERY catalogue row's tier from category heuristics
  (`applySmartLoadout`, Repository :1126+).
- Tier snapshot/restore: grep across app/src/main for snapshot/restore of
  tiers = **zero hits**. Nothing restores the user's hand-set tiers, ever.
- The display/enforcement split is now *documented in code*:
  `ui/MonolithViewModel.kt:375-379` (UNA-07 comment): the LOADOUT cap "is
  written only by applySmartLoadout below (the hand path — a routine applying
  its own loadout does not move his selector)". The screen shows his pick;
  enforcement is the routine's. STANDS exactly as audited.
- **Shared machinery with #107:** `applySmartLoadout` is ONE function used by
  both the hand cap tap (VM :2098-2117 → Repository :1126) and the routine
  path (Repository :1089). What #107 fixes (no preview/confirmation/subtitle
  on the hand tap) does NOT touch the routine path — a routine fires with no
  tap at all. What is routine-only here: the absence of snapshot/restore and
  the cap-not-moving rule. A cure for F1 that lives in the shared function
  (e.g. snapshot-before-apply) would serve both paths; a cure in the Shield
  UI serves only the hand path.

### F2 [CRITICAL] Perfect compliance judged MISSED — STANDS
- `data/MonolithRepository.kt:139` `EVIDENCE_COVERAGE_MILLIS = 300_000`;
  `:142` `ATTENDANCE_FLOOR_SECONDS = 900`.
- `computeAttendedSeconds` (:959) builds "attended" purely from evidence
  instants (intercepts, task completions, first/last stamps), each covering
  5 minutes. An untouched phone in a drawer generates ~no evidence.
- Judgement (:1024-1037): `attended < 900 → MISSED` (or LATE_FIRE if the
  session started >120 s late); `ATTENDED` requires
  `attended >= yardstick − 900`. The audit's claim is exact: compliance that
  produces no incidents is graded as absence.
- One nuance the audit predates: the LATE_FIRE word now exists, so a late
  firing is blamed on the firing, not the user. The on-time-fire MISSED path
  is unchanged.

### F3 [HIGH] Pause/postpone engine exists, nothing can ever invoke it — STANDS
- `pausedUntilMillis` occurrences in current code: migration default
  (`data/MonolithDatabase.kt:58`), model declaration (`data/MonolithModels.kt:561`),
  and derive-side READS ONLY (`data/RoutineDerive.kt:133,137,186,204,283`;
  `data/MonolithModels.kt:705-709`). **No writer exists anywhere** — no UI,
  no ViewModel function, no repository function sets it.
- The pause semantics are fully implemented downstream (WindowState.PAUSED,
  J6 amnesty in RoutineDerive) and permanently unreachable. No pre-fire
  warning surface exists; RoutineHeartbeat touches no UI/notification.

### F4 [HIGH] Editor cannot express intent — STANDS
- Only time controls: `−30` / `+30` chips for START and END
  (`ui/screens/ShieldMatrixScreen.kt:746-755`). No time picker, no minute
  precision — 21:15 is unsettable.
- Silent dead taps confirmed: `ui/MonolithViewModel.kt:1529-1548`
  `adjustRoutineTime` — `if (rawSpan !in 30..1080) return` with NO feedback
  (the rotary detent sound plays only on the success path, so a clamped tap
  is literally silent and motionless).
- Create/delete/rename: grep for addRoutine/deleteRoutine/createRoutine/
  renameRoutine across app/src/main = **zero hits**. The only RoutineEntity
  constructions are the 3 seeds (`data/MonolithRepository.kt:841-850`:
  FASTED BLOCK 05:00–08:00, DAY BLOCKS 09:00–19:30, SLEEP 20:00–03:30).
- SLEEP crosses midnight (1200→210 min); the row prints start → end with no
  overnight indication (audit cite MonolithModels windowContaining — day-mask
  logic now at Models :705+; substance unchanged).

### F5 [HIGH] Occupied/overlapping windows never fire, yet count as ACTIVE — STANDS
- `data/RoutineDerive.kt:157,160`: deriveOne returns `WindowState.OPEN_HELD`
  (no desired session) whenever any other session occupies the window.
- `service/RoutineHeartbeat.kt:133` (own comment): "Materialise at most one
  desired session" per pass, first in list order (:140-151).
- `data/MonolithModels.kt:720-725`: `isLive = enabled && daysMask != 0`;
  `liveCount` counts exactly that — fireability is not part of the count.
  Printed as the ACTIVE number at `ui/screens/ShieldMatrixScreen.kt:314-317`.
  A routine that can never fire is counted ACTIVE. STANDS.

## Complexity census (current code)
Dedicated routine machinery: RoutineDerive.kt 290 lines · RoutineHeartbeat.kt
189 · RoutineScheduler.kt 91 (now legacy-alarm cancelling only) ·
RoutineAlarmReceiver.kt 32 = **602 lines**, plus LoadoutStore.kt 54 (shared
with the hand path). Routine references spread wide: MonolithRepository 61 ·
MonolithViewModel 33 · MonolithModels 22 · ShieldMatrixScreen 18 — **17 files**
mention Routine. Plus: a DB table + migration column (pausedUntilMillis),
window snapshots on every session row, and the finalise/judgement funnel.

## What each candidate model does to these findings
- **A — Rebuilt right** (minimal machinery that cannot silently not-fire):
  F1 needs snapshot/restore of tiers around any routine apply (new state,
  new failure modes). F2 needs attendance redefined as time-under-protection,
  not evidence — a judgement redesign. F3 needs the pause verb actually wired
  (the engine half-exists). F4 needs a real editor (picker, create/delete).
  F5 needs overlap arbitration + an honest ACTIVE definition. Every finding
  is curable under A, at the price of keeping — and growing — the 602-line
  machine that already failed silently once on his phone.
- **B — Dissolved into plain recurring seals** (no routine object; recurring
  windows set once, visible at a glance, nothing derived): F1 dies (no
  loadout apply on fire — a seal uses the rules as they stand). F2 dies (no
  attendance grading — blockers don't grade). F3 dies as stated (a seal is
  edited or skipped directly; nothing to postpone). F4 mostly dies (the
  editor IS the seal list; precision is a UI choice, not a model limit).
  F5 dies (no derivation, no ACTIVE count to lie — a window either seals or
  the surface says why). Machinery deleted: Derive + Heartbeat + judgement
  funnel; the alarm/scheduler remnant shrinks to firing the seal.
- **C — Removed, manual arming only**: all five findings die with the
  feature. Cost: zero automation — the 20:00 SLEEP seal becomes his nightly
  hand ritual (the ritual he already keeps with Blockit). Machinery deleted:
  everything in the census, plus the routines DB table goes dormant.

— REISE, cluster lead, Wave-3B. Evidence only; the decision belongs to the
deliberation and Sarthak.
