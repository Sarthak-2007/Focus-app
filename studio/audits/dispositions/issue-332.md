# Issue #332 — Flipd Second-pass findings (7 findings) — WELLE-7 cluster J disposition

Lead: Welle (cluster J, replacement — first lead died at bootstrap; verified
0 prior comments and no branch for #332 at claim time).
Verified against origin/main @ b7a52cc. This is the one Flipd issue of the
five where Monolith genuinely loses lines: 3 fixed in code, 1 verified as
deliberate, 2 struck by settled purpose, 1 stands blocked (priced).

Seats (12, weighted platform/technical): Technical execution ·
Diagnostician · Builder · Implementation-quality · Verifier · Brutal
critic · Purpose-questioner · Designer · UX designer · Day simulator ·
Consumer · Fresh-eyes. Technical execution + Diagnostician + Builder run
as the code passes; the rest LEAD-COVERED.

- F1 Fallback door reads daily-aggregate lastTimeUsed, 8s cutoff —
  FIXED-IN-CODE (commit 77d5093). The shield door now reads the real
  UsageEvents stream on a 20-second window through the pure
  `ForegroundDoor` decision (`data/SafetyFloor.kt`), and the shield
  poll cadence drops 2000ms → 1000ms (`MonolithShieldService.kt`).
  The #99 expired-peek waiver is kept on the aggregate path, where a
  stay-put user has no fresh event to read. Tests:
  `SafetyFloorForegroundDoorTest` (settled app stays visible; allowed
  app in front clears an earlier blocked one).
- F2 Lock suspended for the whole call, uninstall guard included —
  FIXED-IN-CODE (commit 77d5093). The accessibility door was already
  cured per-package in Wave-3 #42 (`shouldBypassForCall`,
  `MonolithAccessibilityService.kt:180-193`). The shield door still had
  the global holiday (`queryForegroundBlockedApp` returned null for any
  call state); it is removed — call surfaces are exempt per-package via
  the safety floor, and every other blocked app stays blocked mid-call,
  Flipd's own construction.
- F3 Timer and lock as separate per-session choices — STRUCK-BY-PURPOSE.
  Sarthak's settled definition: Monolith is a BLOCKER; a session that
  blocks nothing is a timer/tracker product, which Monolith is not.
  Every seal starting the shield unconditionally
  (`SessionSealTransition` → `MonolithShieldService.startShield`) is the
  product working as defined. (The open-flow stopwatch mode,
  `MonolithModels.kt:368`, covers untimed study without inventing a
  non-blocking timer tier.)
- F4 Saved user-authored session recipes — STANDS-BLOCKED, priced.
  Verified: no recipe object exists — `StudyModePreset` is a 4-entry
  compile-time enum (`MonolithModels.kt:61-72`) and `FocusProfileEntity`
  (:325) has no goal/tag/reminder/lock-variant fields. Cure price: a new
  Room entity + DB migration + Deck editor UI + routine/profile
  binding — a schema-and-surface programme that belongs to the Deck /
  routines rethink lanes, not a surgical audit cell (a half-built
  recipe table with no editor would be exactly the mediocre work the
  quality law forbids). Partial coverage today: routines + profiles +
  smart loadouts already replay duration + loadout + window.
- F5 Compiled-in ~90-package floor; Monolith has no floor at all —
  FIXED-IN-CODE (commit 77d5093), with the Flipd shape deliberately NOT
  copied. New `SafetyFloor`: dialer / telecom / incallui / IME /
  emergency packages can never hold a blocking tier — capped in
  `LoadoutRules.tierFor`, `applyProfile`, and both manual tier setters,
  and refused by both enforcement doors. Payments, ride-hailing and
  music are NOT floored: locking those is the user's sovereignty
  (#329 F4), and exempting them by vendor decree is Flipd's defect,
  not its virtue.
- F6 Multitasking as a declared, counted session mode —
  STRUCK-BY-PURPOSE. Declared work-modes and rotation counts are
  tracking; Monolith is not a tracker (settled purpose). Rotation
  between OPEN/PEEK apps is already free and unrecorded; the intercept
  log records only reaches for BLOCKED apps — an enforcement record
  (HOLD / tell-the-truth), not productivity analytics wearing a
  friendlier name.
- F7 Reboot footprint: six actions, pre-unlock, clock-change reconcile —
  VERIFIED-FALSE as a defect (deliberate anti-tamper). Each action in
  `MonolithBootReceiver.kt:48-80` earns its wake: LOCKED_BOOT_COMPLETED
  restarts the shield pre-unlock (HOLD-absolutely); TIME_SET /
  TIMEZONE_CHANGED reconcile closes the clock-change evasion (move the
  clock, end the session early / skip a routine); MY_PACKAGE_REPLACED
  re-arms after an update. Groups are individually guarded and the
  shield restarts before the slow reconcile. Flipd's one-condition
  receiver is precisely why its lock dies silently mid-run — the
  failure #331 F2 already faults it for.


---

**Rebase addendum (same run, after main moved):** while this branch waited,
issue #119 landed `EssentialApps` on main — its `floorTier` gives every tier
WRITER (loadout, profile, cycle, set) a dialler floor (LOCKED → PEEK_2M),
and SLEEP essentials are package-based. F5 is therefore FIXED-ELSEWHERE for
the writer paths and the dialler; this branch's rebased cure keeps only what
#119 does not cover: (1) F1/F2 in full — the UsageEvents fallback door and
the end of the whole-call holiday at BOTH layers (repository query and the
ShieldService selection wrapper); (2) the DOOR floor — neither door ever
intercepts a floor package whatever a stored row holds; (3) the IME /
emergency classes, which have no floor anywhere in #119. The original
LoadoutRules/setter caps were dropped in the rebase as duplicates of
EssentialApps; SafetyFloor now delegates dialler detection to
EssentialApps.isDialerPackage so the studio keeps one definition.
