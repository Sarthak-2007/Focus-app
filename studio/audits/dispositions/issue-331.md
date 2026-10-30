# Issue #331 — Flipd Head-to-head losses (6 findings) — WELLE-7 cluster J disposition

Lead: Welle (cluster J, replacement — first lead died at bootstrap; verified
0 prior comments and no branch for #331 at claim time).
Verified against origin/main @ b7a52cc.

Seats (12, weighted design/purpose): Brutal critic · Purpose-questioner ·
Designer · Feel · UX designer · Verifier · Builder ·
Implementation-quality · Day simulator · Human · Fresh-eyes · Keeper of
Record. Purpose-questioner + Brutal critic + Verifier run as separate
passes; the rest LEAD-COVERED.

- F1 Bounce, not a wall — VERIFIED-FALSE (Monolith win). Monolith meets a
  reach with a dedicated intercept surface — `AirlockInterceptOverlay`
  (`AirlockAndDisarmOverlays.kt:550`) presenting the session's intention
  and the priced peek/emergency economy — and the un-exitable launcher
  block surface (`LauncherBlockScreen.kt`). Nothing relaunches on a
  1-second tick; the blocked app is never "one Recents tap away".
- F2 Single polling service = single point of failure — VERIFIED-FALSE
  (Monolith win). Detection and session-keeping are separate components:
  `MonolithAccessibilityService.kt:282` detects from the accessibility
  event stream while `MonolithShieldService.kt:33` independently owns
  the session, with RoutineHeartbeat + WatchStore as the watchdog story
  Flipd lacks entirely.
- F3 Session born social (group code / live-room id / head-count) —
  STRUCK-BY-PURPOSE. This demands a social shape. Sarthak's settled
  purpose: Monolith is a BLOCKER for study blocks — not a social or
  tracker product. The absence is architectural by design: the manifest
  declares no INTERNET permission and `allowBackup="false"`
  (`AndroidManifest.xml:24-30`); `FocusSessionEntity`
  (`MonolithModels.kt:356`) represents exactly one human, deliberately.
- F4 Finished session published to a feed — STRUCK-BY-PURPOSE. Same
  ruling as F3: a Monolith session ends in a local receipt
  (`ReceiptSheet.kt`) filed in the on-device drawer. Circulation, comments
  and reactions are tracker-social machinery, out of scope; the local,
  witness-free record is the feature, not the gap.
- F5 Device admin requesting zero policies, Toast-only callbacks —
  VERIFIED-FALSE as "empty theatre" for Monolith. Monolith's receiver
  does real work: `onDisableRequested` returns a session-aware warning
  (`MonolithDeviceAdminReceiver.kt:25`), and `onEnabled`/`onDisabled`
  write WatchStore grant state (:33-41). Its policy file is also empty —
  deliberately and disclosed in-code (`device_admin_receiver.xml`,
  issue #44): the admin is friction + warning, never pretend enforcement.
  Flipd's is a Toast; Monolith's is bookkeeping plus an honest warning.
- F6 Checkable public identities; Monolith has no user object —
  STRUCK-BY-PURPOSE. A public identity is the precondition for the
  social layer struck in F3/F4. Monolith is local-first by construction
  (no INTERNET, no account, no backend); the app does not need to greet
  or address its user for a block to hold. Out of scope by settled
  purpose, not an oversight.
