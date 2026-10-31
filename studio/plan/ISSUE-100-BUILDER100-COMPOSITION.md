# Issue #100 — BUILDER-100 composition of PR #552 onto main 96f83650 (Oct 31)

Vehicle settlement filed on #100 (comment 6543372346): PR #552 drives
(CUT ruling), PR #347 closed-superseded. #552's head 650829c4 was
unsound: compile-red on its own CI (run 41119019705, build job
124280619641 — MonolithNotificationListener.kt:179 public function
exposes internal CachedAppRule), CONFLICTING vs current main, and its
drop migration collided with main's MIGRATION_15_16 (#261).

Repairs in this composition:
1. Compile fix: NotificationHoldCache.ruleCovering is now `internal`
   (the cache's consumers are this file + the same-module test).
2. Migration renumber: the captured_notifications drop is now
   MIGRATION_18_19, database version 19 (main took 15→16 #261,
   16→17 #340, 17→18 batch-10 in flight). Migration-chain tests
   (Routines / SliceE / ShortsSniper) extended to MIGRATION_18_19;
   Routines still asserts captured_notifications ABSENT at latest.
3. Conflicts resolved CUT-side, main's newer work kept by name:
   Repository bookInterceptArrival/resolveIntercept (live VM callers)
   kept; ShieldService badge uses the in-memory snoozedCount inside
   main's #155 IQ-2 M-8 labelled bigText form; VM sevenDayStats kept;
   MainActivity deskClockYielded surface kept, digest sheet deleted,
   one closePark call re-signed to the CUT signature.
4. K4 closure conditions (6538393455) re-verified on the composed
   listener: zero contentFingerprint / SeenCapture / dedupe store /
   OTP content copy — OTP survives only as the transient exemption.
5. TAGWERK peek cure (6524365581, priced S) added: a live peek lease
   (BlockedAppEntity.activePeekUntilElapsedRealtime, the enforcement
   doors' own read) exempts that package from holds on both the
   cached fast path and the async path; pinned in
   Issue100NotificationsTest.peekLeaseLive_onlyWhileLeaseEndIsInTheFuture.

Tests deleted with the deleted surface (the vehicle's recorded
disposition, named here for K8/#166): FrozenDigestGateTest,
HeldDoorRenderTest, NotificationListenerCaptureTest,
NotificationRetentionDaoTest, TruthCar3CaptureBodyTest (files), plus
digest-only tests inside DrawerRenderTest / CascadeDeckRenderTest /
ExampleRobolectricTest / SeedAndDayWordDrainTest. Every one pinned
the storage/digest surface the ruling deletes; no test guarding a
surviving behaviour was cut, skipped or weakened.

— BUILDER-100, BUG-B tree

Addendum (same run): main moved again during the landing (batch of
builder PRs incl. #713 for issue #534). Second composition onto
b2f9a364: #534's new closePark signature carried a digest-dismiss
branch calling the deleted toggleNotificationDigestSheet — composed
out (parameters + branch), #534's parkIntro spend law kept verbatim
(definition + all call sites re-signed to
closePark(parkOpenState, viewModel, spendParkIntro = …)).
