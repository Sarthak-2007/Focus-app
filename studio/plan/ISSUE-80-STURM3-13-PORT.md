# Issue #80 — STURM3-13 port note (inherits STURM3-02 / WELLE cell)

Audit of the inherited branch `agent/issue-80` (STURM3-13):

- **Diagnostic residue on #80: PRESENT — do not merge that branch.**
  Its tip commit `87f0e83` is the temp diagnostic itself ("upload
  unit-test result XML — REVERT before merge"), never reverted:
  `.github/workflows/rescue-build.yml` lines 76–84 carry the
  TEMP DIAGNOSTIC upload-artifact step. This port branch was built
  fresh from `origin/main` and never contains it.
- The branch was also based on stale main (`0a00e95`); its
  per-finding verification no longer holds against current main.
  Re-verified by STURM3-13 against current `origin/main`:

## Verdicts vs CURRENT main

- **F2 [HIGH] PiP / split-screen unpoliced — FIXED ON MAIN, struck.**
  Issue #92 landed `sweepOtherWindows` +
  `flagRetrieveInteractiveWindows` (MonolithAccessibilityService.kt,
  config xml): every other application window is judged through the
  same snapshot pipeline and doors. STURM3-02's F2 code is NOT
  ported — it would duplicate #92. The #92 sweep is also the
  vehicle this port's F3/F5 checks ride on (each swept window
  arrives as its own snapshot under its own package).
- **F1 [HIGH] cross-profile installs — partial ported, residue
  stands-blocked (platform), claim UPHELD.** Discovery via
  LauncherApps.getProfiles/getActivityList ported
  (MonolithRepository + AppCatalogReconcile.mergeProfiledInstalls /
  profilesByPackage), adapted to main's #34 dedupe: calling-profile
  entries keep their isMainActivity flags; only profile-only
  packages are appended. Residue: no enforcement door carries a
  UserHandle (AccessibilityEvent = packageName only, UsageStats is
  per-calling-user, StatusBarNotification has no profile field) and
  a service runs only in the profile where the app is installed and
  granted — per-profile rules from this one install are impossible
  for a non-device-admin/non-profile-owner app. Product decision
  for Sarthak, not faked.
- **F3 [HIGH] Custom Tabs / in-app WebViews — LIVE on main, ported.**
  Main's site door still gates on isSupportedBrowser (step 4).
  Ported as step 4b on main's pipeline: bounded window texts
  (depth 12 / 200, focused nodes skipped) captured for every
  non-browser package; a text fires only when URL-shaped
  (ThreatModelGuards.looksLikeLoadedUrl) and host-matched — prose
  mentions never fire. Guard toggle + peek lease honoured as step 4.
  Residue (stated): a WebView exposing no text nodes for its page
  gives no signal; no normal-app API reads another app's WebView URL.
- **F4 [HIGH] pre-posted notifications — LIVE on main, ported.**
  Main's onListenerConnected only stamped WatchStore. Ported:
  onNotificationPosted's body is now the shared handleNotification;
  sweepActiveNotifications() runs the shade's activeNotifications
  through it on listener connect and on the shield's protecting
  flip. All of main's #45/#132 gates (post-time session rule,
  capture policy, media exemption, dedupe) apply unchanged because
  the sweep uses the identical path. Residue (platform): Android
  Notification History, if user-enabled, retains pre-listener text;
  no API purges it.
- **F5 [MEDIUM] launcher / Assistant surfaces — LIVE on main,
  ported.** Step 4b judges launcher/Assistant packages by content:
  a ≥20-char node naming a non-OPEN app fires (UNKNOWN path, no
  peek offer — this door cannot honour a lease). Bare icon labels
  can never fire (anti-loop guard, tested). Residue: content that
  nowhere names its source app carries no attributable signal;
  policing it means blocking the launcher wholesale — a downgrade,
  not done, would need Sarthak's approval.

Tests: ThreatModelGuardsTest, 5 pure tests, ported unchanged.

— STURM3-13, STURM-3 cell
