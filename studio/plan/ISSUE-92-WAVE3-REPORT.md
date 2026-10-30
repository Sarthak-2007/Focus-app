# Issue #92 — Wave-3 Report (Audit R2 · accessibility-config-deep)

**Cell:** FLUT, Wave-3B cluster lead, branch `agent/issue-92`, base origin/main @ 0a00e95.
**Crew note (honest):** the bundle's seat-spawn for this issue's window sweep hit a
safety refusal under another coordinator earlier in the programme (recorded in the
chair's progress notes; not retried, not routed around). The implementation here is
the LEAD's own code, written from the audit text and the service sources directly,
and is marked LEAD-COVERED. Remaining seats could not be spawned (fleet saturation);
verify+verify work lead-covered likewise.

## User-facing change (plain words — FLAGGED per the 04:58 rule)
- **The shield now also watches split-screen and picture-in-picture windows.** Before
  this fix, the blocking service only inspected the one foreground app: a blocked
  app running in a split-screen pane or a floating PiP window was never checked.
  Now every visible app window is passed through the same checks (tamper words,
  Shorts/Reels snipers, blocked sites) — each under that app's own peek lease and
  the Guard settings, so a legitimately peeked app in a side pane is NOT blocked.
  The service's listing in Android's Accessibility settings also gains a proper
  summary line and opens the app when tapped (it was a dead entry before).
- Nothing else changes: no screen, no flow, no setting behaves differently.

## Findings (all 5 verified against the 8-line shipped config + service source)

1. **[CRITICAL] Interactive-window blind spot — FIXED.** Config gains
   `flagRetrieveInteractiveWindows` + `typeWindowsChanged`. The service's event
   entry gains a bounded sweep (step 4b, after the site branch): every other
   `TYPE_APPLICATION` window root from a new `interactiveWindowRoots()` helper is
   run through the EXISTING detectors (tamper / Shorts / Reels / site — all already
   take a root node), each detector gated on its own package's peek lease and the
   Guard switches. Own package, the event's own package and SystemUI are skipped.
   Cost: one window-list read per accessibility event, same order as the existing
   per-event root read; no timers, no polling.
2. **[MEDIUM] notificationTimeout 80ms — FIXED** → 100 (the platform default the
   audit names; 80 bought nothing and sat under every platform example).
3. **[LOW] `isAccessibilityTool` undeclared — FIXED** by declaring it, truthfully,
   as `false` (Monolith is a user-armed focus shield, not an assistive tool; the
   attribute is declared so the posture is a decision, not an inheritance).
4. **[LOW] `flagIncludeNotImportantViews` — FIXED** (removed; it pulled decorative
   nodes into every traversal's hot path for no check the service runs).
5. **[LOW] No settingsActivity / summary — FIXED.**
   `settingsActivity="com.example.MainActivity"` (the app's real launcher
   activity) + a `summary` string describing the shield in the settings listing.

## Verification notes
- No test in the tree pins the config or the service event flow (grep-proven);
  the change is config + one additive sweep step, compile-proven by CI.
- The sweep reuses the detectors' existing null/lease discipline; a window whose
  root is unavailable is skipped, never guessed at (TELL-THE-TRUTH).

— FLUT, Wave-3B cluster lead, for Ren.
