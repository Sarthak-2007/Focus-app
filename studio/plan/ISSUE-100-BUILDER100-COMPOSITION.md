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

Gate cycle 1 (run 41202228237 on f56230e4): test-compile failed on
EndingEarlyTruthRenderTest — a main-side Deck test still passing the
deleted HELD-door parameters (capturedNotificationsCount /
onOpenNotificationDigest) to TactileDeckScreen. Its assertions
(grace/end buttons) are untouched; the two dead arguments were
removed. The targeted-tests job's gate-honesty step also named
MonolithNotificationListener.kt as unmapped: Issue100NotificationsTest
now references the listener class directly and pins its in-memory
snoozedCount, making the covering test findable by content.

Gate cycle 2 (run 41202736695 on a3599dc8): Compile job SUCCESS.
Targeted tests: 579 executed, 4 failed. Two were MERGE-INTRODUCED by
this composition: SessionCoveringDaoTest's covering tests — the copy
carried from the CUT branch predated main's #478 revival fix (a
@Before clearing the shared singleton's session rows; without it,
leftover ACTIVE rows with startWallTimeMillis=0 cover every instant
and break the assertNull checks). The @Before session-clear is now
carried into SessionCoveringDaoTest (the captured-clear half is moot:
the table is gone). The other two failures are judged against the
floor in the landing filing: ForestComparativeWinsTest
w2_noCurrencyOrBillingSurfaceInMainCode (main's own VM comments —
"coin flip", "the only coin" — trip its scan; this diff adds no such
line) and SoundAndSocialTruthTest SocialToggleRenderedAndWritesThrough
(a named member of the Oct 30 floor census's SoundAndSocial set).

Addendum 2 (same run): main moved again — #324's lane landed its own
MIGRATION_18_19 (intercept_logs typed columns), so the CUT drop
renumbered a final time to MIGRATION_19_20, DB v20; chain tests
extended. ShieldService composed with main's #278 stopwatch
chronometer (kept) + the snoozedCount badge (CUT). MainActivity kept
main's new shoppingWarningVisible state; its re-added frozenDigest
collection was removed with the digest.

Addendum 3 (same run): the gate PASSED on head 09c581a6 (run
41203850237: Compile SUCCESS; targeted 605 tests, sole failure
ForestComparativeWinsTest W2 — a floor failure, main's own VM
"coin flip"/"only coin" comments trip its scan, this diff adds no
such line; the two MERGE-INTRODUCED SessionCovering failures were
cured by the #478 @Before carry). Before the merge call, main moved
again: PR #736 (issue #138) landed a metadata-only
captured_notifications rebuild as MIGRATION_19_20 / DB v20 — content
columns removed, storage kept. That is #138's measure, not #100's
ruling: #100's body rules removal, and no filing on #100 supersedes
it. Third composition: #138's 19→20 kept as shipped history, the
#100 drop renumbered to MIGRATION_20_21 / DB v21, conflicts resolved
CUT-side (Deck keeps #90's DeckArmedUtilities extraction minus the
HELD chips; listener capture path superseded by the snooze-hold).

Gate cycle 4 (run 41204979419 on fdc45b7a): Compile SUCCESS;
targeted 665 tests, 2 failed — Forest W2 (floor, as before) and
RoutinesMigrationChainTest, MERGE-INTRODUCED in the third
composition's terms: #138's v20 pin asserts captured_notifications
SURVIVES metadata-only at the latest schema, which #100's v21 drop
supersedes. The pin is removed and replaced by a supersession note
in place; the absence assertion (same test, above it) is the
latest-schema truth. Named here for K8/#166 per the test orders.

Addendum 4 (same run): main landed TWO MIGRATION_20_21 declarations
in one wave (#525 rebootGapSeconds + #253 profile bundle, PR #748) —
a duplicate-declaration compile break on main itself (verified on
main dc933100). As composing lane I repaired it inside this PR:
#525 keeps 20→21, the #253 bundle renumbers to MIGRATION_21_22
(contents unchanged), and the #100 drop takes MIGRATION_22_23,
DB v23. This failure is classified MERGE-INTRODUCED (by the wave,
not by this PR and not the floor), owned here as the composing lane.
