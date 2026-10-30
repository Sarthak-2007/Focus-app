# Issue #304 — Audit R5 supplement, AppBlock Head-to-head losses (6 findings)
## WELLE-7 cluster E verification record — Ersatz (lead, replacement)

Verified against origin/main at branch base. Code cure for F1 on
this branch (SessionTierGate); F2/F3 partially cured by earlier
waves (evidence below); F4/F5/F6 STANDS-BLOCKED, priced.

Seats (≥10, enforcement-weighted): Builder ×2, Verifier ×2,
Brutal critic, Purpose-questioner, Implementation-quality,
Technical execution, Diagnostician, UX designer, Feel, Finisher,
Consumer, Keeper of Record. Thin seats LEAD-COVERED; none dropped.

## F1 [HIGH] Tightening-only edits while active — FIXED-IN-CODE (this branch)
Was: hand tier taps + site removal were already ratcheted in the
ViewModel (#141: MonolithViewModel.cycleAppBlockTier /
setAppBlockTier one-way ratchet + undo refusal; #151:
removeBlockedSite ratchet), but the loadout tap was the named
hole — MonolithViewModel.applySmartLoadout had no session check
and MonolithRepository.applySmartLoadout rewrote every tier from
the preset, loosening LOCKED apps mid-session. The repository
verbs themselves knew no direction at all.
Cure (commit on agent/issue-304): pure `SessionTierGate`
(LoadoutRules.kt — strictness rank, loosens, clampWhileSealed);
repository applySmartLoadout lands tightening-only per app while
a session is sealed (routine materialisation applies its loadout
BEFORE its session starts, so it still lands whole);
cycleAppBlockTier / setAppBlockTier / removeBlockedSite refuse
loosening at the write boundary for every caller; the ViewModel
loadout tap surfaces the ratchet warning when a loosening was
clamped. Tests: SessionTierGateTest (total order + sealed clamp
matrix + unsealed passthrough).

## F2 [HIGH] Recents/Settings policing — HALF FIXED-IN-CODE, HALF STANDS-BLOCKED
Fixed already (Wave-3 #42, verified on main):
MonolithAccessibilityService.kt — INSTALLER_PACKAGES now 8 OEM
installers (audit baseline: 4), MARKET_PACKAGES 7 markets,
SETTINGS_PACKAGES (Settings + MIUI SecurityCenter), and tamper
detection is locale-independent: `isTamperActionViewId` matches
app-info action-button resource IDs (uninstall/force-stop/
disable/clear) instead of the English text search the audit
cited.
Stands-blocked: the Recents/overview surface. The accessibility
service deliberately SKIPS SystemUI packages
(onAccessibilityEvent + window scan, MonolithAccessibilityService.kt).
Blocker: intercepting Recents fights the settled block-screen
design (un-exitable launcher surface — Sarthak's Oct 30 ruling)
and OEM overview implementations vary per vendor; a wrong
intercept here can trap the user outside the launcher block
screen. Price: per-OEM overview behaviour study + a SystemUI
watch limited to the overview window class + on-device proof on
his phone. Estimated 1–2 days, design-panel review first.

## F3 [HIGH] One accessibility death kills sites/Shorts/Settings guard — SURFACING FIXED-IN-CODE, FALLBACK STANDS-BLOCKED
App blocking already survives accessibility loss (shield polls
UsageStats independently — MonolithShieldService loop,
queryForegroundBlockedApp). The "undifferentiated guard state"
half is cured on main: WatchStore maps enforcement branches to
their doors (shorts → ACCESSIBILITY; sites → ACCESSIBILITY +
USAGE_ACCESS — WatchStore.kt branch mapping), per-item
PermPrint includes STALE states, and the Guard line renders
per-permission prints (GuardLine.kt).
Stands-blocked: reassigning the Settings/uninstall guard to the
polling door on accessibility death. Blocker: the guard's
evidence is window-content (view IDs in the Settings window) —
UsageStats polling cannot see it; a fallback would need the
shield to launch checks it has no window access for. Price:
accessibility-rebind watchdog + expedited re-grant flow (overlaps
#309 F1's supervision build). Estimated 1 day on top of #309 F1.

## F4 [HIGH] Pooled budgets — STANDS-BLOCKED
Stands: MonolithModels.kt carries only per-app
BlockedAppEntity.dailyLimitMinutes; no group/pool entity exists.
Blocker/price: a pooled budget is a Room schema change (new
entity + DAO + migration) in a codebase whose schema is
deliberately frozen at v12 with new state routed to prefs (see
MonolithRepository catalog-memory KDoc), plus shield enforcement
of a shared counter and a group editor UI. It is also an
allowance/tracker shape, not the blocker's core (his settled
purpose: seal apps for study blocks; sessions, not budgets, are
the mechanism — daily per-app limits already exist as the
allowance surface). Estimated 3–5 days incl. migration tests.
Named, priced — not built in this cell.

## F5 [HIGH] Precedence algebra for overlapping routines — STANDS-BLOCKED
Stands: RoutineDerive.deriveOne judges each routine's windows
against sessions; overlapsAny (RoutineDerive.kt) judges
attendance only. The de-facto precedence on main is implicit and
undocumented: a session binds ONE routineId, and the start
guards refuse a second routine session while one is active
(MonolithRepository start guards) — first window to fire wins.
Blocker/price: a strictest-wins merged-loadout semantics must be
defined (his panel's call — routines as a whole are under his
Oct 30 rethink order: "im not sure if it even is needed"),
then implemented in the derive layer with editor surfacing.
Estimated 2–3 days after the routines rethink verdict. Building
an algebra on a feature whose existence is under rethink would
be waste.

## F6 [MEDIUM] Pause state machine — STANDS-BLOCKED
Stands: pause is still the single RoutineEntity.pausedUntilMillis
column (MonolithModels.kt), consumed by RoutineDerive; no
presets, no history, no pre-resume notification.
Price: presets are UI-only (cheap, half a day) but history needs
persisted pause records (prefs-side table, schema-frozen
convention) and a pre-resume notification needs a dedicated
alarm in the scheduler chain + notification surface — together
1–2 days, and it belongs with the routines rethink (F5) rather
than ahead of it. Named, priced — not built in this cell.

Signed: Ersatz, WELLE-7 cluster E lead (replacement).
