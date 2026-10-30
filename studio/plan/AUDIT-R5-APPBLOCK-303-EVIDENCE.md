# Issue #303 — Audit R5 supplement, AppBlock Platform execution (3 findings)
## WELLE-7 cluster E verification record — Ersatz (lead, replacement)

Verified against origin/main at branch base.

Seats (≥10, platform-weighted): Technical execution ×2,
Diagnostician, Platform specialist, Verifier ×2, Brutal critic,
Purpose-questioner, Implementation-quality, Builder (record),
Consumer, Keeper of Record. Thin seats LEAD-COVERED; none dropped.

## F1 [HIGH] Process isolation — STANDS-BLOCKED
Evidence it stands: `AndroidManifest.xml` declares
MonolithShieldService, MonolithAccessibilityService and
MonolithNotificationListener with NO `android:process` on any —
all enforcement shares the main process with the UI.
Exact blocker: the three services and the UI share ONE in-process
liveness/state singleton (WatchStore — bind/poke/heartbeat stamps
written by accessibility + listener, read by shield + Guard UI)
and one Room instance per process. Moving the shield to its own
process without an IPC/persisted-state redesign silently splits
WatchStore: the Guard board would attest liveness from a different
process's memory than the shield writes — a TELL-THE-TRUTH
failure worse than the shared-process risk it cures.
Price: persisted liveness stamps (prefs/DB) for every WatchStore
fact + per-process database invalidation audit + soak testing on
device. Estimated 2–4 focused days, HOLD-risk until proven on the
phone. Not a surgical cell fix; named here, not faked.

## F2 [HIGH] Accessibility subscription scoping — STANDS-BLOCKED
Evidence it stands: `res/xml/accessibility_service_config.xml`
declares typeWindowStateChanged|typeWindowContentChanged|
typeWindowsChanged with NO packageNames restriction.
Progress since the audit baseline, on record: the config already
dropped flagIncludeNotImportantViews (now
flagDefault|flagReportViewIds|flagRetrieveInteractiveWindows)
and notificationTimeout moved 80 → 100.
Exact blocker: a static XML packageNames list cannot express
Monolith's policed set — it is (a) the user's own arbitrary
blocked-app catalogue, and (b) OEM call surfaces matched by
SUFFIX in code (`isCallSurfacePackage`: any package ending
".dialer" / containing ".incallui." — MonolithAccessibilityService.kt)
— neither enumerable in a manifest. Scoping statically would
silently kill call handling and per-user app coverage.
Price: runtime scoping — build the policed set from the catalogue
(the nine browsers in isSupportedBrowser,
MonolithAccessibilityService.kt; installers, markets, settings,
sniper targets, catalogue packages, known dialer packages) and push it via
AccessibilityService.setServiceInfo on catalogue change, with
regression tests per branch. Estimated 1–2 days + on-device proof.

## F3 [MEDIUM] QUERY_ALL_PACKAGES / launcher-only catalogue — VERIFIED-FALSE as demanded
The audit offers two cures; the first contradicts Sarthak's own
ruling, the second is already done in code:
- His words (Oct 29, 22:40 IST, recorded in the reconcile KDoc):
  "the list of apps is too long, it contains apps that I haven't
  installed" — the catalogue was deliberately rebuilt around the
  true, launchable installed set.
- `AppCatalogReconcile.kt` header documents the blind spot and
  its safeguards explicitly: the table holds the launchable
  installed set (PackageManager launcher query, visibility via
  the manifest `<queries>` MAIN/LAUNCHER block); prune needs two
  consecutive corroborated absences; pruned rulings are
  tombstoned and restored on rediscovery.
- Declaring QUERY_ALL_PACKAGES would flood the catalogue with
  non-launcher system packages — the exact "too long" list he
  rejected — and Play policy restricts the permission besides.
Disposition: struck — launcher-only is a ruled design property
with the blind spot documented at the reconcile's head, not a
silent omission.

Signed: Ersatz, WELLE-7 cluster E lead (replacement).
