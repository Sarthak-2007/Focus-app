# Focus-app — Round 3 Audit: The User Perspective

Date: 2026-10-30 · Scope: 18 lived journeys walked through the code, claims vs experience · Deduped against the 358 findings of rounds 1–2

**Totals: 89 findings — 11 critical, 47 high, 30 medium, 1 low** (running total across all rounds: 447)

## The worst of round 3

- **[notifications-lived]** 'Held' notifications still buzz, flash heads-up and sit tappable in the shade — cancellation only happens after delivery, behind two async DB reads
- **[escaping-like-a-user]** Mid-session Loadout/Profile tap bulk-rewrites every app tier to OPEN, bypassing the one-way ratchet — and the session still receipts ELAPSED_CLEAN
- **[peek-lived]** The promised floating countdown pill never exists: no screen renders it and the tap-✕ early end has no UI caller
- **[peek-lived]** At 2:00 nothing cuts you off: expiry has no enforcer, so a still screen (video/photo/reading) lingers indefinitely
- **[tasks-lived]** Two tasks with the same title: the seed-sync silently deletes one, with no undo
- **[routines-lived]** A routine firing silently rewrites the user's entire hand-set app-rule catalogue — and nothing ever restores it
- **[routines-lived]** Perfect compliance is judged MISSED: 'attendance' counts only intercepts and taps, so a phone left untouched all window reads as a no-show
- **[park-guard-lived]** Board prints NOTIFICATIONS RUNNING with no session running — the word is derived from listener binding alone, while the listener holds nothing outside an active session
- **[things-go-wrong-lived]** Held notifications become unreachable the moment the session ends — the only reading door is a chip that exists only while a session runs
- **[things-go-wrong-lived]** Revoke a permission mid-session and the header still prints GUARD 4/4 for the rest of the session — and the running Deck's explanation/FIX sentence is deliberately suppressed
- **[moment-of-temptation]** The peek's countdown pill is never drawn: the user spends a 2-minute lease with no clock, no warning and no way to hand it back

---

## Journey: first-launch-setup (5 findings)

**Verdict:** First launch drops the user onto a jargon Deck with a one-shot PARK drawer that any stray tap permanently dismisses, contradictory ready/not-ready verdicts, and a Shield full of factory guesses already printed as his own rules — he can reach a first session, but nothing in the journey honestly tells him what state he is in or what he actually chose.

### [HIGH] First-run PARK invitation is permanently consumed by any dismissal, and its reopening paths are unlabelled
*Location:* `app/src/main/java/com/example/MainActivity.kt:278-317`

MainActivity.kt:313-317 auto-opens PARK only when parkIntroPending() is true, and closePark() at MainActivity.kt:278-287 calls markParkIntroDone() on EVERY dismissal — scrim tap, Back (BackHandler at MainActivity.kt:330-332), rail stop press, or PARK re-tap all burn the persisted bit (MonolithRepository.kt:454,494). Nothing tells the user how to get it back: the rail's PARK zone 'prints NO word ever' (MonolithRailView.kt:78-80), and the other reopening path is the header Guard line — a bare, unlabelled Text that is not even rendered until the Watch Store has measured (MonolithCustomComponents.kt:279-294; GuardLine.kt:34-38 returns null while any counted item is UNMEASURED). One reflexive tap in the first second and the only guided setup moment is gone for good.

### [HIGH] Setup gives two contradictory readiness verdicts at once: GUARD x/4 and the FIX queue demand grants the arm gate never requires
*Location:* `app/src/main/java/com/example/service/WatchStore.kt:296; app/src/main/java/com/example/ui/components/GuardLine.kt:34-45`

The slide commits only on a LIVE warrant, whose conjuncts are just accessibility bound+stamped and usage ALLOWED (WatchStore.kt:296 deriveWarrant; BlockitCoreViews.kt latchFrame dispatches only when verdict == LIVE). But the header simultaneously prints 'GUARD {n}/4 — {NAME}' counting FOUR items including DEVICE ADMIN and NOTIFICATION_LISTENER (GuardLine.kt:36-45; WatchStore.kt:93-102 COUNTED), and PARK's FIX key keeps naming the first missing of all SIX permissions as the thing to fix next (ParkDrawer.kt firstMissing over WatchItemId.PERMISSIONS). So after granting exactly the two grants a first session needs, the user is told he is at 2/4 with a FIX — DEVICE ADMIN button, while the Deck's imprint already offers 'WILL SHUT' and the slide will arm. He cannot tell from anything on screen whether he is ready or not.

### [HIGH] First catalogue is invented by substring guessing, then presented on Shield as 'YOUR RULES' — the user's own decisions
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:514-529; app/src/main/java/com/example/data/AppCatalogReconcile.kt:86-100; app/src/main/java/com/example/ui/screens/ShieldMatrixScreen.kt:882`

On the first catalogue sync every installed app is born with a guessed category and tier before the user rules on anything: categorizePackage (MonolithRepository.kt:514-529) substring-matches the package+label — anything containing 'play' is GAMES, anything unmatched falls through to SHOPPING — and AppCatalogReconcile.kt:86-100 births SHORT_VIDEO/GAMES apps as LOCKED and MESSAGING as PEEK_2M. So e.g. an app labelled 'Google Play Store' is born SHUT as a 'game', and a banking or camera app is filed as SHOPPING. Shield then presents these factory guesses under the header 'YOUR RULES' (ShieldMatrixScreen.kt:882), in a section whose own comment calls the tally counts 'of his decisions' (ShieldMatrixScreen.kt:342-352) — decisions the first-time user never made.

### [MEDIUM] PARK's spec plate, read during first setup, claims routines were 'SET BY HAND' and sends study hours to a 'YPT' that exists nowhere
*Location:* `app/src/main/java/com/example/ui/components/ParkDrawer.kt:120-122`

The plate at the foot of PARK — the drawer a first-run user is auto-shown — prints 'ROUTINES: SET BY HAND ON SHIELD' (ParkDrawer.kt:120), but at that moment no hand set them: ensureSeededAndSyncPackages inserts the three factory routines, all enabled=true, on first seed (MonolithRepository.kt:204-205 and routineTargets()). The same plate prints 'STUDY HOURS: IN YPT' (ParkDrawer.kt:122); 'YPT' is defined nowhere — a repo-wide search finds it only in this plate line and one receipt line (ReceiptSheet.kt:176, 'YOUR STUDY RECORD LIVES IN YPT'), with no screen, section or explanation anywhere in the app. The setup surface directs the new user to a place that does not exist in the product he is holding.

### [MEDIUM] Fourth rail stop has three names (LEDGER / Receipts / VAULT) and opens a 'CABINET' of curios — a labelled dead end for a new user
*Location:* `app/src/main/java/com/example/ui/views/MonolithRailView.kt:34; app/src/main/java/com/example/ui/screens/DrawerScreen.kt:348`

The bottom rail's fourth stop is printed 'LEDGER' (MonolithRailView.kt:34 WORDS = DECK, SHIELD, TASKS, LEDGER), the destination it opens is MonolithDestination.VAULT whose label is 'Receipts' (MonolithViewModel.kt:48-52), and the screen the user actually lands on heads itself 'STORED'/'CABINET' and, on a fresh install, shows 'NOTHING STORED YET' above a cabinet of Curio artifacts marked 'NOT YET FORGED' (DrawerScreen.kt:348,486-488,712). Three names for one stop, none of them describing what is there; a new user exploring during setup taps LEDGER expecting a record of his sessions and gets an unexplained curios museum — a dead end with no route back to setup except the rail he came in on.

---

## Journey: arming-a-session (5 findings)

**Verdict:** Arming looks like a deliberate, verifiable seal but is lived as guesswork: the true length is set by an unreadable dot-code multiplier, the printed undo lasts 140ms, the grace clock starts before its abort button exists, and for up to 1.5s the phone is already blocking while the Deck still claims nothing has happened.

### [HIGH] The session-length multiplier is invisible: block count exists only as ambiguous hub dots, so the dial does not mean what the user thinks it means
*Location:* `TactileDeckScreen.kt / BlockitCoreViews.kt (RotaryWheelView hub)`

MonolithViewModel.kt:217-221 defaults dial=50, blocks=2. TactileDeckScreen.kt:324 totalSpanMinutes = dialMinutes*studyBlocksCount + (studyBlocksCount-1)*10, and the hero (TactileDeckScreen.kt:~625-650) prints only that total (e.g. '1H 50M') — never '2 × 50', never the word BLOCKS. The count itself is rendered nowhere as a number: BlockitCoreViews.kt:916-924 'the count lives only as hub geometry - no numeral, no word... the object count by count runs 1,2,3,1,2,3,4,2 - never above four' (5 blocks and 1 block look alike; 8 looks like 2 bars). TactileDeckScreen.kt:~838: tapping the wheel's centre — the natural grab point — cycles the count (8 wraps to 1), silently adding/removing a whole block plus a 10-minute break (~60 min at the default dial) with no label, no confirmation, and no way to tell what changed except the total jumping.

### [HIGH] 'TAP AGAIN TO UNDO' is printed for only 140ms — the undo window closes before a human can physically use it
*Location:* `BlockitCoreViews.kt (SlideToStartView)`

BlockitCoreViews.kt:2407-2408 WORDS_START='SLIDE OR TAP TO START', WORDS_UNDO='TAP AGAIN TO UNDO'; the UNDO words render only while mode==MESH (BlockitCoreViews.kt:1787). MESH is timed by MonolithCurves.DURATION_140 (MonolithCurves.kt:23; stepMachine MESH branch, BlockitCoreViews.kt:~2225-2250), after which the machine enters SEAT and drives itself to the latch. In SEAT, onTouchEvent's ACTION_DOWN falls into 'if (mode != Mode.REST) return true' — the tap is swallowed, not an undo; only a second touch landing inside the 140ms mesh window (startAbortReturn) aborts. No human reacts, re-aims and taps within 140ms of their own first tap releasing, so the printed escape hatch effectively does not exist; once the seat starts, the arm is uncancellable from the slide.

### [MEDIUM] The advertised 10-second grace window burns ~1 second during the seal animation, while the abort chip is not even on screen
*Location:* `TactileDeckScreen.kt (pinned stack / seal shot)`

The session clock is re-anchored to t0 in MonolithRepository.kt:1435-1442 (sealSession), and t0 is the start of the Deck's 1050ms seal shot (TactileDeckScreen.kt:~262-290, driver runs to e>=1050). During the shot the pinned zone still shows the slide, not the chips: TactileDeckScreen.kt:~1028 'if (!isSessionRunning || shotActive || heldFinish || receiptState != null)' renders SlideToStartView, scroll is disabled and task-verb handlers are removed ('Input death', TactileDeckScreen.kt:~570,~790). The grace chip only exists afterwards: TactileDeckScreen.kt:~1040-1055 withinGraceWindow = elapsedSeconds<=10, badge '${10-elapsedSeconds}s'. So the user first sees 'GRACE WINDOW ABORT' with roughly 9s — never 10 — left, and had no abort surface at all during the first second of the window.

### [MEDIUM] Proving is a silent black box: up to 1.5s of real blocking with the Deck still showing the idle, unsealed form and zero feedback
*Location:* `ArmProvingController.kt / TactileDeckScreen.kt (ArmPhase.Proving rendering)`

ArmProvingController.kt:27-33: the row 'is born active at insert' and shield enforcement runs during proving, with sealSession re-anchoring t0 so 'those milliseconds are never counted as session time' — i.e. apps are already being blocked while the session timer has not started. On screen the user gets nothing: TactileDeckScreen.kt:205-215 provingThisSession forces the Deck to keep rendering its idle form (TOTAL, dial, idle strip), and the sentence slot is explicitly null for Proving (TactileDeckScreen.kt:~1074-1082 'armPhase is ArmPhase.Proving -> null'). The attempt can sit in this silent state up to the 1500ms annulment deadline (ArmProvingController.kt ANNUL_MS=1500), distinguishable from a hang only by its ending: either a 1050ms vertigo shot or the cap snapping home with a past-tense 'NO PROOF. NOTHING WAS SEALED.' sentence. There is no 'proving/checking' word, spinner, or cancel offered at any point.

### [MEDIUM] Idle proof strip contradicts itself: the APPS count prints at PASS density even when BLOCK reads OFF or UNKNOWN
*Location:* `TactileDeckScreen.kt (ProofStripBand derivations)`

TactileDeckScreen.kt:~519-528, the idle appsCell: 'appsCellCount != null -> appsCellCount.toString() to StripDensity.PASS' — unconditional on the warrant. In the same strip the BLOCK cell is driven by the freshness-checked warrant (blockCellFor(warrantPrintState): LIVE->ON/PASS, OFF->OFF/FAULT, else UNKNOWN/UNKNOWN), and the imprint correctly refuses to print 'WILL SHUT' on a dead wire (MonolithCustomComponents.kt:450-467, tenant is null unless LIVE). So one frame can simultaneously print BLOCK OFF/UNKNOWN, a PASS-toned app count, and an empty imprint — the strip's own cells disagree about whether those apps will be blocked, and the count's PASS is the one a user reads as the promise.

---

## Journey: opening-blocked-app (5 findings)

**Verdict:** From the user's chair the intercept is a countdown that gates nothing for locked apps, and its one real reward — the 2-minute peek — starts burning before the user reaches the app, shows no clock while it runs, and never actually evicts them when it ends, while the Home-button escape route costs nothing and is not even recorded.

### [HIGH] Peek expiry never evicts: the 2-minute lease just lapses on paper while the user stays in the app, invisible to the shield door
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:986-1005,2418-2426; app/src/main/java/com/example/data/MonolithRepository.kt:564-578`

When the 120s lease ends, the VM ticker only calls end2MinutePeekEarly() (MonolithViewModel.kt:986-1005), which clears an in-memory pill state and writes the lease row closed (MonolithViewModel.kt:2418-2426) — no intent, no GLOBAL_ACTION, no re-intercept is fired, so the user sitting inside the blocked app is not moved at all. Re-blocking depends on a later incidental accessibility event, and the only periodic checker is structurally blind to a stay-put user: MonolithShieldService polls queryForegroundBlockedApp, which only matches apps whose lastTimeUsed is within the last 8 seconds (MonolithRepository.kt:574-575) — a timestamp from when the peek app was opened, minutes stale by expiry. The '2m' badge and 'TAKING THE PEEK STARTS A 20 MINUTE COOLDOWN' price printed on the offer (AirlockAndDisarmOverlays.kt:663-683) therefore buy an eviction that never happens on time.

### [HIGH] The peek countdown pill does not exist on screen: no UI anywhere renders floatingPeekPill, so the peek runs with no visible clock and no early-end tap
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:113-118,2381-2404; app/src/main/java/com/example/data/MonolithRepository.kt:1205-1213`

start2MinutePeek's KDoc promises it 'Grants a 2-Minute Peek lease with the Draggable Edge-Snapping Countdown Pill' (MonolithViewModel.kt:2381) and recordPeekEnded's comment says it ends 'when the user taps ✕' (MonolithRepository.kt:1205). In reality floatingPeekPill is written and ticked only inside MonolithViewModel.kt (lines 255-256, 986-1005, 2400-2404, 2419-2422) and grep finds zero consumers in MainActivity.kt, ui/screens/ or ui/components/ — no composable ever collects the flow. From the user's chair, the moment they tap EMERGENCY PEEK the airlock vanishes and no countdown, app name, remaining-seconds readout or early-end control ever appears for the whole peek. Distinct from the known process-death rehydration title: the pill is not lost on death, it is never drawn in the first place.

### [HIGH] Home/Recents escape from the Airlock is free, repeatable and completely unlogged — only users who tap RETURN TO FOCUS get recorded
*Location:* `app/src/main/java/com/example/MainActivity.kt:348-350; app/src/main/java/com/example/ui/MonolithViewModel.kt:2315-2373`

The Airlock is ordinary Compose content inside MainActivity, not a lock-task or separate window: MainActivity registers BackHandler only for PARK and for non-DECK destinations (MainActivity.kt:348-350), nothing guards the Airlock, and no lock-task API exists in the app. Bookkeeping is coupled to the honourable exits only: triggerAirlockIntercept (MonolithViewModel.kt:2315-2373) plays a sound and sets state but writes no intercept log and increments nothing; recordIntercept is called solely from transmuteImpulseAndReturn ('Stayed Focused', MonolithViewModel.kt:2428-2441) and start2MinutePeek (MonolithViewModel.kt:2408-2415). A user who presses Home at the Airlock, reopens the blocked app from Recents/launcher and repeats gets a fresh detection gap and a fresh Airlock each cycle, with zero entries in the intercept log, zero movement in the session's 'urges stopped' counter, no cooldown consumed and no escalation — the dishonest exit is the only exit that leaves no trace.

### [MEDIUM] Taking the peek does not take you to the app: the 120-second lease starts at the tap and burns while the user navigates home and relaunches by hand
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:2384-2416; app/src/main/java/com/example/data/MonolithRepository.kt:1190-1202`

start2MinutePeek (MonolithViewModel.kt:2398-2416) does exactly three things: hides the Airlock, sets the (unrendered) pill state to 120s, and launches a coroutine that anchors the lease at grant time via repository.startPeekLease(pkg) (MonolithRepository.kt:1196-1202: leaseEnd = nowMono + 120_000). It never builds a launch intent for the peeked package. The user is left standing on the Monolith Deck and must press Home, find the app and relaunch it themselves while the lease — the full '2m' the badge advertised (AirlockAndDisarmOverlays.kt:669) — is already burning; whatever navigation takes comes straight out of the 120 seconds, with no visible clock (see pill finding) to even watch it go.

### [MEDIUM] The 5-second wall counts down to nothing for LOCKED apps: RETURN TO FOCUS was live from second 5, and at 00 no new option ever appears
*Location:* `app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:547,656-663,690-712`

The overlay's header comment sells 'Decision #16: 5s Breathing Delay Wall' (AirlockAndDisarmOverlays.kt:547), but the only element gated by the countdown is the peek offer/withheld line (AirlockAndDisarmOverlays.kt:663,690). RETURN TO FOCUS is composed unconditionally and tappable at '05' (AirlockAndDisarmOverlays.kt:656-661). For a LOCKED-tier app — the majority case — peekWithheld is null because the tier is not PEEK_2M (MonolithViewModel.kt:2338), and the overlay's when(null) branch renders Unit (AirlockAndDisarmOverlays.kt:691-712): at '00' literally nothing appears, unlocks or changes. The lived experience is watching a large numeral count 05→00 down to the identical screen with the identical single button that was live the whole time — a wall whose timer opens nothing.

---

## Journey: notifications-lived (5 findings)

**Verdict:** As lived, 'held' is a post-hoc transcript service: the notification buzzes, flashes and sits tappable in the shade before an async cancel deletes it forever, and the digest afterwards mixes other sessions' leftovers under a count that caps at 100 — the promise of quiet, recoverable holding is kept at neither end of the journey.

### [CRITICAL] 'Held' notifications still buzz, flash heads-up and sit tappable in the shade — cancellation only happens after delivery, behind two async DB reads
*Location:* `app/src/main/java/com/example/service/MonolithNotificationListener.kt:50-95`

The class doc claims it 'Intercepts and suppresses notifications from blocked apps during active sessions' (MonolithNotificationListener.kt:15-22), but onNotificationPosted (line 50) only fires after the system has already posted the notification — sound, vibration and heads-up are delivered at post time and cannot be un-rung. Suppression then waits behind an async hop: scope.launch (line 72) performs two DB reads — getActiveSessionSnapshot() and a full getAllAppsSnapshot() scan (lines 74-75) — before cancelNotification(sbn.key) is even attempted (line 93). For that whole window the notification sits in the shade fully readable with its contentIntent intact, so the user hears it, sees the heads-up, reads it in the shade, and can tap it straight into the blocked app. This is exactly Sarthak's field report, and it is architectural, not a race fix: even an instant cancel happens after delivery.

### [HIGH] 'Held' actually means deleted: the original notification is destroyed and never restored — the digest is a dead transcript with no way to open, reply or act
*Location:* `app/src/main/java/com/example/service/MonolithNotificationListener.kt:20-21,93; app/src/main/java/com/example/data/MonolithModels.kt:463-471; app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:297-458`

cancelNotification(sbn.key) (MonolithNotificationListener.kt:93) permanently dismisses the original notification. All that survives is two strings: CapturedNotificationEntity stores only packageName, appName, senderTitle, messageBody and a timestamp (MonolithModels.kt:463-471) — no PendingIntent, no notification key, no actions. Nothing anywhere re-posts captures when a session ends, and the digest offers no way back in: live rows expose only a DEL affordance and the frozen sheet itself prints 'Nothing here opens' (AirlockAndDisarmOverlays.kt:297-458). Yet the listener's own doc promises captures 'can be read, cleared, or tapped to open after studying' (MonolithNotificationListener.kt:20-21) — there is no tap-to-open code path in the ViewModel or repository at all. A user who trusts 'held' loses quick-reply, OTP actions, call-back buttons and the thread link permanently; what returns after the session is a read-only transcript of a notification that no longer exists.

### [HIGH] The digest and HELD badge are not this session's: captures carry no session key, so old uncleared notifications are presented as freshly held
*Location:* `app/src/main/java/com/example/data/MonolithDao.kt:148-159; app/src/main/java/com/example/data/MonolithModels.kt:463-471; app/src/main/java/com/example/MainActivity.kt:488`

CapturedNotificationEntity has no sessionId field (MonolithModels.kt:463-471) and captures are never cleared at seal or finish. Both digest reads are whole-table: the flow is 'SELECT * FROM captured_notifications ORDER BY timestampMillis DESC LIMIT 100' (MonolithDao.kt:148) and the frozen snapshot is the same table with no filter and no limit (MonolithDao.kt:159). The Deck's HELD badge during a live session is fed the whole list size (MainActivity.kt:488; TactileDeckScreen.kt:914-915). So a user who never pressed CLEAR ALL in a previous session opens today's digest and is shown last week's messages under a header reading 'HELD · AS OF {HH:mm}' — the sheet code even normalises this, noting 'yesterday's held rows legitimately sit in this morning's snapshot' (AirlockAndDisarmOverlays.kt, captureTimeLabel comment). The user cannot tell what this session actually held, and the count certifies captures from sessions that ended days ago.

### [MEDIUM] Notifications whose body lives outside EXTRA_TEXT walk straight through — never captured, never cancelled
*Location:* `app/src/main/java/com/example/service/MonolithNotificationListener.kt:64-68`

The listener reads only Notification.EXTRA_TITLE and EXTRA_TEXT (MonolithNotificationListener.kt:64-66) and returns early — no capture, no cancel — when both are empty (line 68). It never looks at EXTRA_BIG_TEXT, EXTRA_TEXT_LINES or MessagingStyle extras. Notifications whose visible content lives in those extras (long messages expanded as big text, inbox-style stacks, some messaging apps' styles) therefore arrive during a sealed session completely untouched: not held, not logged, not in the digest — the user just gets them. The mirror failure hits captured ones: only the short EXTRA_TEXT is stored, so the digest can show a truncated preview of a message the user never actually receives in full anywhere, since the original was cancelled.

### [MEDIUM] The live HELD count silently stops at 100 — the badge and digest header under-report while the frozen sheet counts the same set in full
*Location:* `app/src/main/java/com/example/data/MonolithDao.kt:148-159; app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:362-363`

The live digest flow is capped: 'SELECT * FROM captured_notifications ORDER BY timestampMillis DESC LIMIT 100' (MonolithDao.kt:148), and both the Deck HELD badge (MainActivity.kt:488) and the live sheet header 'HELD NOTIFICATIONS • N' (AirlockAndDisarmOverlays.kt:362-363) print that capped list's size. The frozen snapshot deliberately removes the cap — its comment admits a capped read 'would lie by omission' (MonolithDao.kt:152-159) — but the live surfaces the user actually watches during a session keep the cap. In a notification-heavy session the badge freezes at 100 while captures keep being taken and cancelled, and the live and frozen counts for the same held set disagree, so the number the user triages by is silently wrong exactly when holding matters most.

---

## Journey: escaping-like-a-user (4 findings)

**Verdict:** A tempted user never needs to defeat the shield: in the first minute they can walk into Monolith's own Shield screen and weaken the rules wholesale (loadout/profile rewrite, site deletion) through doors the one-way ratchet never guarded, take a peek whose promised countdown pill doesn't exist, and have the session receipted ELAPSED_CLEAN with 'urges stopped' credited at a tap — the escape is in-app, ordinary, and the record calls it a clean session.

### [CRITICAL] Mid-session Loadout/Profile tap bulk-rewrites every app tier to OPEN, bypassing the one-way ratchet — and the session still receipts ELAPSED_CLEAN
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:2098; app/src/main/java/com/example/data/MonolithRepository.kt:1126-1169`

Navigation is open mid-session: MonolithViewModel.navigationLocked() (MonolithViewModel.kt:1388-1403) locks only during arm-proving, the 1050ms seal shot, and finish detention — never for a running session — so the rail takes the sealed user to SHIELD. There, ShieldCapSelector's onSelect fires onApplySmartLoadout unconditionally (ShieldMatrixScreen.kt:290-292), MonolithViewModel.applySmartLoadout (MonolithViewModel.kt:2098) has no session check, and MonolithRepository.applySmartLoadout (MonolithRepository.kt:1126-1169) rewrites EVERY app's tier from category alone: STUDY sets everything that is not short-video/social/games/messaging/YouTube/Chrome to OPEN, WORK_SPRINT sets everything except short-video/games to OPEN (social to PEEK). selectAndApplyProfile/applyProfile (MonolithViewModel.kt:2141; MonolithRepository.kt:1243-1261) does the same, OPENing every category the profile doesn't list. This walks straight around the app's own printed law in setAppBlockTier (MonolithViewModel.kt:2079): 'ONE-WAY RATCHET ENGAGED • CANNOT WEAKEN ... DURING ACTIVE VAULT' — the ratchet guards only the single-app door, not the bulk doors. Afterwards the record pretends nothing happened: the finish outcome is derived solely from the shield-beat gap (MonolithRepository.kt:2068-2071, ELAPSED_CLEAN), no intercept or weakening is logged, so a session spent in freshly-OPENed apps receipts as clean.

### [HIGH] Blocked Websites REMOVE is live mid-session with no ratchet at all — delete the rule, then browse the site in Chrome
*Location:* `app/src/main/java/com/example/ui/screens/ShieldMatrixScreen.kt:562; app/src/main/java/com/example/data/MonolithRepository.kt:800-802`

Same open navigation (navigationLocked, MonolithViewModel.kt:1388-1403) reaches ShieldMatrixScreen's BLOCKED WEBSITES section, where each row's REMOVE chip calls onRemoveSite with no session gate (ShieldMatrixScreen.kt:562). MonolithViewModel.removeBlockedSite (MonolithViewModel.kt:1558-1562) has no active-session check and MonolithRepository.removeBlockedSite (MonolithRepository.kt:800-802) is a bare dao.deleteSite(domain). Enforcement reads the stored list live on every event (MonolithAccessibilityService.kt, detectBlockedBrowserUrl path: userDomains = dao.getAllSitesSnapshot()), so the deletion bites immediately: the user removes e.g. youtube.com, opens Chrome, and browses it for the rest of the session. App tiers, daily limits and profile schedules all got ratchet guards (MonolithViewModel.kt:2079, updateAppDailyLimit, toggleProfileSchedule); the website list — half the blocking product — got none. The finish record is again ELAPSED_CLEAN by shield-beat only (MonolithRepository.kt:2068-2071), with no trace the rule was deleted mid-session.

### [HIGH] The peek's countdown pill is never rendered in any life — taking a peek leaves the user with no clock and no way to end it early
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:2384-2409, 986-1009`

start2MinutePeek's own contract promises a 'Draggable Edge-Snapping Countdown Pill' (MonolithViewModel.kt:2384-2387 doc comment: 'Grants a 2-Minute Peek lease with the Draggable Edge-Snapping Countdown Pill'). The VM maintains FloatingPeekPillState and ticks it every second, even playing detents as it counts (MonolithViewModel.kt:986-1009). But no surface ever draws it: grep for floatingPeekPill and end2MinutePeekEarly across MainActivity.kt, ui/screens/* and ui/components/* returns 0 hits — the flow has no collector outside the ViewModel, and the only mention of the pill's face ('Pastille's pill face', MonolithViewModel.kt:979) is a comment naming a component that does not exist in the tree. So the user who takes the sanctioned peek is inside the blocked app with no visible countdown and no early-end verb anywhere; the first sign the 2 minutes are over is the next sudden Airlock. This is distinct from the known process-death title, which presupposes a pill that renders in normal life and only dies with the process — here it is never rendered at all.

### [MEDIUM] 'Stayed Focused' and the 'urges stopped' count are banked at the RETURN tap, before the user has returned to anything
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:2428-2442; app/src/main/java/com/example/service/MonolithShieldService.kt:171-174`

transmuteImpulseAndReturn (MonolithViewModel.kt:2428-2442) fires the instant the chip is tapped: it hides the overlay and immediately calls repository.recordIntercept(outcome = 'Stayed Focused'), and recordIntercept (MonolithRepository.kt:2104-2125) both writes that outcome to the intercept log and increments the session's phantomReachesBlocked, which the shield's own notification then prints back at the user as fact: 'N urges stopped' (MonolithShieldService.kt:171-174). Nothing has been stayed or stopped yet — the tap only dismisses a dialog inside Monolith itself; the user can re-open the blocked app seconds later, get intercepted again, tap RETURN again, and each tap banks another 'urge stopped' and another 'Stayed Focused' log line. The session's headline self-report is written at the moment of intention, not after the behaviour it claims to record.

---

## Journey: peek-lived (5 findings)

**Verdict:** As lived, the 2-minute peek is a lease with no clock face, no hand-off to the app, and no one on duty at expiry: the user pays the full price up front, navigates to the app on their own burning time, scrolls unwatched by any countdown, and — on a still screen — is never actually cut off at 2:00.

### [CRITICAL] The promised floating countdown pill never exists: no screen renders it and the tap-✕ early end has no UI caller
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:2380-2424; app/src/main/java/com/example/MainActivity.kt:161-241`

Claim: MonolithViewModel.kt:2380-2382 doc says start2MinutePeek 'Grants a 2-Minute Peek lease with the Draggable Edge-Snapping Countdown Pill'; MonolithModels.kt:218 coaching tip promises 'the floating timer will keep you anchored'; MonolithRepository.kt:1205-1206 says recordPeekEnded fires 'when the user taps ✕'. Reality: grep over app/src/main shows floatingPeekPill (MonolithViewModel.kt:255-256) has zero collectors — MainActivity.kt collects ~45 flows (lines 161-241) but never this one, and no composable anywhere renders it; end2MinutePeekEarly (MonolithViewModel.kt:2418) has exactly one caller, the VM's own expiry ticker (line 994). So during the peek the user, sitting in another app, sees no countdown, no warning, and has no ✕/early hand-back at all — the only trace is an unexplained rotary-detent click every 15s from the VM ticker (MonolithViewModel.kt:996). Distinct from the known process-death pill title: the pill is not lost on death, it is never drawn in the first place.

### [CRITICAL] At 2:00 nothing cuts you off: expiry has no enforcer, so a still screen (video/photo/reading) lingers indefinitely
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:564-576; app/src/main/java/com/example/service/MonolithAccessibilityService.kt:282-295`

Nothing fires at lease expiry. The VM ticker (MonolithViewModel.kt:986-1001) only retires its internal pill state via end2MinutePeekEarly() and launches no intercept. Cut-off depends entirely on the two doors re-noticing the app: (a) the accessibility service re-checks the lease only inside onAccessibilityEvent (MonolithAccessibilityService.kt:282-295), and its config subscribes only to typeWindowStateChanged|typeWindowContentChanged (res/xml/accessibility_service_config.xml) — a user watching a video, viewing a photo, or reading a static screen generates neither and is never re-checked; (b) the shield's 2s poll asks queryForegroundBlockedApp, which only returns apps whose lastTimeUsed is within the last 8 seconds (MonolithRepository.kt:564-576, filter at line 574) — a user who entered at peek start has a ~120s-old stamp at expiry, so this door is arithmetically blind to a continuous peek. Worse, the ticker still calls recordPeekEnded at expiry (MonolithViewModel.kt:994,2418-2425), so the 20-minute cooldown burns down while the user lingers free.

### [HIGH] Tapping the peek strands you inside Monolith with the 120 seconds already burning — no hand-off to the blocked app
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:2384-2416; app/src/main/java/com/example/data/MonolithRepository.kt:1194-1202`

start2MinutePeek (MonolithViewModel.kt:2384-2416) hides the airlock (line 2399), sets the pill state, launches repository.startPeekLease(pkg) and immediately logs outcome 'Used 2m Peek' (lines 2406-2415) — and stops. There is no getLaunchIntentForPackage / launch of the intercepted package anywhere in the UI layer (grep over ui/ and MainActivity.kt returns only Settings intents). The user is left standing on Monolith's own screens and must find the blocked app again via Home/recents/app icon while the lease — written with its end already set to now+120_000ms in MonolithRepository.kt:1194-1202 — burns. The offer's price line (AirlockAndDisarmOverlays.kt:678, peekPriceText) discloses only the cooldown, never that travel time comes out of the 2 minutes. If the user never reaches the app, startPeekLease has pre-scheduled lastPeekEnd=leaseEnd, so the full peek and the 20-minute cooldown are spent on a peek never seen — already recorded as 'Used 2m Peek'.

### [MEDIUM] Return from a peek is unexplained for 5 seconds: the cooldown reason is hidden behind a second full countdown wall
*Location:* `app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:560-571,663-714`

In AirlockInterceptOverlay both the offer and the explanation are gated on the 5-second wall finishing: the EMERGENCY PEEK chip and its price render only 'if (countdownSecs == 0 && state.canUse2MinutePeek)' (AirlockAndDisarmOverlays.kt:663-683), and the withheld-reason lines — including PeekWithheld.COOLDOWN's 'PEEK COOLDOWN — AGAIN IN $minutes MIN' — render only 'if (countdownSecs == 0 && !state.canUse2MinutePeek)' (lines 690-714). countdownSecs is re-seeded to 5 on every intercept (remember/LaunchedEffect, lines 560-571). So a user yanked back at peek expiry stares at a fresh 05→00 wall whose only labelled action is RETURN TO FOCUS, with no on-screen statement that their peek ended or that a cooldown started, for the wall's full five seconds; a first-time user likewise gets no hint a peek exists until the wall ends.

### [LOW] 'EMERGENCY PEEK' borrows the weekly token's name: the offer never says it won't spend the one 'EMERGENCY BREAK' or end the session
*Location:* `app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:260-269,663-683`

The peek chip is labelled 'EMERGENCY PEEK' (AirlockAndDisarmOverlays.kt:665) while the genuinely token-spending action elsewhere is 'EMERGENCY BREAK', whose overlay warns 'This ends the session early. It is the only break this week.' (AirlockAndDisarmOverlays.kt, DisarmChallengeOverlay body). The peek's only printed term is the cooldown line, 'TAKING THE PEEK STARTS A $minutes $unit COOLDOWN ON THIS APP' (peekPriceText, lines 260-269). Nothing on the offer states that the peek does not spend the weekly emergency token and does not end the session — a user in craving, choosing between two near-identically named 'EMERGENCY' doors, cannot tell from the app's own words whether tapping this one burns their one weekly break.

---

## Journey: ending-early-lived (5 findings)

**Verdict:** Ending early is theatre at its decision points: the same chips and holds silently record opposite outcomes depending on invisible session kind, a stored tick counter, and a 20-minute floor the user is never shown.

### [HIGH] Grace Abort on a routine session is not a grace cancel at all — it is silently judged MISSED/PARTIAL by the routine funnel
*Location:* `TactileDeckScreen.kt:1029-1044; MonolithRepository.kt:2299-2311,1031-1034`

Deck offers the identical chip to every active session with no kind check: TactileDeckScreen.kt:1029-1044 renders 'GRACE WINDOW ABORT' with a 10s countdown whenever elapsedSeconds<=10, wired in MainActivity.kt:514-516 to confirmDisarmSession("Accidental Start Grace Cancel"). But MonolithRepository.kt:2299-2306 disarmActiveSession diverts any non-SLEEP routine row to finaliseRoutineSession() and returns BEFORE the isGraceCancel check at :2311 is ever computed. The funnel at MonolithRepository.kt:1031-1034 stamps MISSED (or LATE_FIRE) when attended seconds < 900. Claim vs recorded: the user taps a free, unrecorded abort; a routine window opened seconds earlier is permanently judged as a missed window instead of GRACE_CANCEL with elapsed wiped to 0.

### [HIGH] The 10-second grace deadline is enforced nowhere in the ending write — it is only a UI rendering of a tick-maintained counter, and a late grace cancel zeroes real elapsed history
*Location:* `MonolithRepository.kt:2309-2313,2325,2346; TactileDeckScreen.kt:1029-1032`

The countdown is Deck rendering only: TactileDeckScreen.kt:1029-1032 derives withinGraceWindow from the stored activeSession.elapsedSeconds counter (maintained by the 1s VM ticker at MonolithViewModel.kt:973,1020 and the 2s shield ticker). The ending write itself has no time check: MonolithRepository.kt:2311 sets isGraceCancel purely from outcomeReason.contains("Grace Cancel"), and :2312 then sets bankedElapsed=0, discarding the verifiedElapsed the same function just computed at :2309, stamping GRACE_CANCEL at :2325 and skipping the intercept log at :2346. Any stale/frozen counter or late-delivered tap therefore gets a free cancel that erases however long the session actually ran — the printed '10s' is a promise no enforcement code keeps.

### [HIGH] The 'EMERGENCY BREAK VALVE (60S HOLD)' chip secretly flips to a free instant successful finish after the stopwatch 20-minute floor — same label, opposite cost
*Location:* `TactileDeckScreen.kt:1046-1055; MonolithViewModel.kt:2236-2255; MonolithRepository.kt:2322-2324`

The chip never changes its words or badge: TactileDeckScreen.kt:1046-1055 always prints 'EMERGENCY BREAK VALVE (60S HOLD)' with monoBadge "$weeklyEmergencyTokensRemaining/1 WEEK". But MonolithViewModel.kt:2236-2255 requestDisarmChallenge() checks isStopwatchMode && elapsedSeconds >= STOPWATCH_MIN_FLOOR_SECONDS (1200s, MonolithRepository.kt:109) and, past the floor, never opens the overlay at all — it immediately calls disarmActiveSession(completed=true, "Stopwatch Completed"), which stamps STOPWATCH_ENDED (MonolithRepository.kt:2322-2324) with completedSuccessfully=true. The user tapping what is labelled as the week's only token-spending valve gets a no-hold, no-token, recorded-as-success completion, and is never told the button's meaning changed at 20:00.

### [HIGH] Abandoning the valve is not consequence-free: a single momentary grip permanently stamps E4 attendance evidence worth up to 10 phantom minutes toward a routine judgement
*Location:* `BlockitSuitcaseAndSwitchesViews.kt:725-743; MonolithViewModel.kt:2226-2234; MonolithRepository.kt:932-952,972-976`

Overlay presents KEEP FOCUSING as the clean exit (AirlockAndDisarmOverlays.kt:809-815) and the token spec comment claims 'Banks, slips and abandoned attempts never reach this function and write nothing' (MonolithRepository.kt:2220-2223). But every ACTION_DOWN grip — even a 1-second accidental touch before any bank — fires onHoldStarted (BlockitSuitcaseAndSwitchesViews.kt:725-743), wired via MainActivity.kt:807 to MonolithViewModel.kt:2226-2234 recordValveHoldStarted(), which persistently writes the E4 bit plus first/last evidence stamps (MonolithRepository.kt:932-952). At finalise, each first/last instant earns a 300-second coverage arc (MonolithRepository.kt:141,972-976) against the 900s attendance floor (:143,1031-1034): grips spread across a window can manufacture up to 600s of 'attendance' for a user who held nothing, spent nothing, and was told nothing was recorded.

### [MEDIUM] A completed 60s hold that is refused resets to zero in silence — no sound, no text, no state change tells the user the hold did not count
*Location:* `BlockitSuitcaseAndSwitchesViews.kt:608-614; MonolithViewModel.kt:2294-2319; AirlockAndDisarmOverlays.kt:781-798`

The valve fires completion and erases itself before any verdict exists: BlockitSuitcaseAndSwitchesViews.kt:608-614 invokes onEmergencyBreakCompleted then immediately resetHold() and invalidate(), so the user's 60 seconds of progress visually vanishes at the moment of completion. MonolithViewModel.kt:2294-2319 completeEmergencyBreakFromValve() only acts on BreakResult.Completed; on RefusedNoToken or NoActiveSession it does literally nothing ('Nothing sounds', overlay stays open). Since the overlay composes the valve purely on tokensRemaining>0 (AirlockAndDisarmOverlays.kt:781-798) — e.g. a session that finished naturally during the hold — the user is left staring at a fresh untouched 60s valve with no explanation, indistinguishable from a glitch, invited to hold another 60 seconds that will also silently do nothing.

---

## Journey: safe-break-lived (5 findings)

**Verdict:** As lived, the Safe Break is a half-break announced at neither end: PEEK apps unlock, but their notifications stay held, Shorts and blocked sites keep intercepting despite the guard board printing PAUSED, the countdown starts silently and can be missed entirely, and it ends in a hard cut that lands mid-use with no warning.

### [HIGH] Guard board prints SHORTS + REELS and SOCIAL SITES as PAUSED during a Safe Break while both keep intercepting
*Location:* `app/src/main/java/com/example/service/WatchStore.kt:469-474 vs app/src/main/java/com/example/service/MonolithAccessibilityService.kt:230-268`

WatchStore.deriveInterceptor returns PAUSED for every non-NOTIFICATIONS interceptor when safeBreakActive is set (app/src/main/java/com/example/service/WatchStore.kt:469-474), and the PARK board prints that word verbatim next to 'SHORTS + REELS' and 'SOCIAL SITES' (app/src/main/java/com/example/ui/components/ParkDrawer.kt:358-383). But the accessibility service enforces both anyway: the sniper is headed 'Strictly active during both Work Blocks and Safe Breaks' (app/src/main/java/com/example/service/MonolithAccessibilityService.kt:230) and the URL blocker likewise (same file:244-268); only a live peek lease gates them, never the break. The user reads PAUSED on the app's own guard board, opens Shorts or a blocked site mid-break, and is Airlocked.

### [HIGH] Held notifications stay held through the whole Safe Break — including for the PEEK apps the break just unlocked
*Location:* `app/src/main/java/com/example/service/MonolithNotificationListener.kt:82-108`

MonolithNotificationListener.onNotificationPosted checks only that a session isActive and the sender's tier is not OPEN before inserting the notification into the digest and calling cancelNotification (app/src/main/java/com/example/service/MonolithNotificationListener.kt:82-108); isInSafeBreak is never read anywhere in the file. WatchStore's own comment admits notification capture 'has no suspension input... its PAUSED is unreachable by construction' (app/src/main/java/com/example/service/WatchStore.kt:166-172) and deriveInterceptor hardcodes NOTIFICATIONS suspended=false (same file:470). So during the break a user freely chatting in an unlocked PEEK app (e.g. WhatsApp) gets no shade/banner notification for its incoming messages; they silently accumulate in the HELD digest to be found later.

### [HIGH] The break ends as an unannounced hard cut: no warning, no usable final countdown, intercept lands mid-use
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:1998-2016; app/src/main/java/com/example/ui/screens/TactileDeckScreen.kt:615-616`

The expiry flip in updateSessionProgress just advances the block (app/src/main/java/com/example/data/MonolithRepository.kt:1998-2016); the only boundary signal anywhere is breakBoundaryKnock, a mechanical-latch sound (same file:1827-1829, 1872) — no vibration, no heads-up: the shield notification is IMPORTANCE_LOW with setOnlyAlertOnce (app/src/main/java/com/example/service/MonolithShieldService.kt:203-213). Inside the unlocked app there is no break countdown surface at all, and within one ~2s shield poll of expiry the PEEK app re-enters the blocked set and the intercept fires mid-use (same file:163-190). Even on the Deck the break clock rounds UP to whole minutes — 'BREAK · ${(safeBreakSecondsRemaining + 59) / 60} MIN' (app/src/main/java/com/example/ui/screens/TactileDeckScreen.kt:615-616) — so the entire final minute reads '1 MIN' and then the next block has already started.

### [MEDIUM] The break also starts in silence, and its clock runs whether or not the user ever learns it began
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:2022-2040; app/src/main/java/com/example/data/WallAccrual.kt:40-52`

The entry flip fires only breakBoundaryKnock(false) — the same latch sound (app/src/main/java/com/example/data/MonolithRepository.kt:2022-2040) — and the countdown is purely wall-derived from the stored (remaining, lastTick) pair, with even process-death gaps counting as elapsed break (app/src/main/java/com/example/data/WallAccrual.kt:23-52). A user who is in an OPEN app or has the screen off when block 1 ends gets no alert that their 10 minutes started (only the silent LOW-importance notification text changes); the first sign of the break can be its expiry intercept. The break is spent whether or not the user ever knew they were on one.

### [MEDIUM] The desk clock offered during the break renders the break invisible — no word, no countdown, no change at its end
*Location:* `app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:99-125`

The DESK CLOCK chip is offered in the running-session utilities row, break included (app/src/main/java/com/example/ui/screens/TactileDeckScreen.kt:903-925), but the desk clock deliberately gives the break nothing: 'a Safe Break earns no special rendering (Nacht's law - the break's one rendering is the Deck's BREAK eyebrow)' (app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:118-125). It displays span remaining, which counts straight through the break (app/src/main/java/com/example/data/MonolithModels.kt:387-429): no BREAK word, no break countdown, and nothing on that screen changes when the break ends. A user who props the phone up as their break clock cannot tell break from work, or spot its end, without abandoning the clock.

---

## Journey: session-end-receipt-lived (5 findings)

**Verdict:** As lived, the natural finish detains the user 1.48–1.8s behind a frozen frame still labelled SESSION RUNNING, then hands them a slip that omits the tasks they finished and the breaks they took, retitles itself after the last promoted task, erases the deflections logged before that promotion, lists the peeks they surrendered to as if they were turned away, and then offers no way to keep, reopen or export the one record it claims lives elsewhere.

### [HIGH] Tasks finished in the session are booked as its 'receipt summary' — and the receipt never prints them
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:2552; app/src/main/java/com/example/ui/components/ReceiptSheet.kt`

MonolithRepository.kt:2522 documents completeTaskAndAdvance as recording completion 'in the active FocusSessionEntity receipt summary', and :2552-2562 appends each torn-off task to completedTasksSummary / tasksCompletedCount; reopenStoredTask's doc (:2622) even repairs 'the finish receipt' entry. But assemblePendingReceipt (MonolithRepository.kt:1625-1646) never reads either column, PendingReceipt/ReceiptState (ReceiptState.kt:132-189) have no task field at all, and ReceiptSheet.kt renders title, times, HELD, BREAK BANKED, deflections and footer only — grep for completedTasksSummary in ReceiptState.kt/ReceiptSheet.kt is empty. Lived: the user tears off three tasks in a 50-minute session and the slip attesting that session names none of them and no count.

### [HIGH] Finishing a task rewrites the session's intention mid-flight — retitling the receipt and silently deleting its earlier deflections
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:2583`

When a completion promotes the next queued task, MonolithRepository.kt:2583 does currentSession.copy(intention = promoted.title) with no seal guard. The sibling path knows this is falsification: promoteTaskToActive refuses the same write because 'a SEALED session's intention is the receipt's title source — rewriting it under the seal would falsify the slip' (:2690-2697). Assembly then takes the slip's title from that mutated row (title = row.intention, :1629) and filters deflections by exact match log.sessionIntention == row.intention (:1611-1614), while airlock logs are stamped with the intention live at intercept time (MonolithViewModel.kt:2354). Lived: a queue-worker's receipt is titled after their last task, and every blocked attempt from before that promotion vanishes from the slip.

### [HIGH] The slip's deflection list cannot tell surrender from resistance: 2-minute peeks print identically to turned-away attempts
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:1623; app/src/main/java/com/example/ui/components/ReceiptSheet.kt:139`

Intercept logs carry an outcome: the airlock's return verb logs 'Stayed Focused' (MonolithViewModel.kt:2437) and the peek verb logs 'Used 2m Peek' (:2412) — the user actually opened the blocked app for two minutes. Assembly discards it: MonolithRepository.kt:1623 maps each log to ReceiptDeflection(it.appName, it.packageName, it.timestampMillis), and ReceiptDeflection (ReceiptState.kt:124-128) has no outcome field, despite its contract calling each entry 'a blocked attempt the machine turned away' (:119-120). ReceiptSheet.kt:139-144 prints only 'HH:mm · AppName' per line. A session where the user peeked Instagram three times and a session where they never got in produce indistinguishable slips.

### [MEDIUM] The breaks the user actually lived never appear: BREAK BANKED reads the valve ledger (admitted 0 on every publishable receipt) while Safe Breaks are booked elsewhere and dropped
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:1587; app/src/main/java/com/example/ui/components/ReceiptSheet.kt:134`

A natural multi-block finish includes 10-minute Safe Breaks, carefully booked into breakTakenSeconds at every exit (MonolithRepository.kt:1922 early end, :2006 expiry) and priced into PLANNED via spanTargetSeconds. Assembly never reads breakTakenSeconds — no reference exists in ReceiptState.kt or ReceiptSheet.kt. The slip's only break line, 'BREAK BANKED … MIN' (ReceiptSheet.kt:134-138, shown only when > 0), is fed from the emergency-valve spend ledger (MonolithRepository.kt:1593-1594), and the code's own comment admits: 'On every publishable receipt today the honest figure is 0 — a session the valve broke publishes nothing' (:1587-1592). Lived: the user sits through two 10-minute breaks, the slip shows no break at all, and the ARMED→ENDED wall span cannot be reconciled against PLANNED from anything printed.

### [MEDIUM] The receipt is a dead end: it orders the record into YPT, offers no action of its own, and the tab named 'Receipts' contains no receipts
*Location:* `app/src/main/java/com/example/ui/components/ReceiptSheet.kt:176; app/src/main/java/com/example/ui/MonolithViewModel.kt:52`

The slip's footer claims 'THE MACHINE KEEPS ITS OWN LOG · YOUR STUDY RECORD LIVES IN YPT' (ReceiptSheet.kt:176), yet ReceiptSheet.kt contains no share, save, copy or export affordance anywhere — its only callback is onDismiss, fired by tapping the scrim. Retrieval is equally absent: MonolithViewModel.kt:52 labels the VAULT destination 'Receipts', but DrawerScreen.kt contains zero receipt rendering (grep 'Receipt' is empty) — it shows stored tasks and Curio artifacts. The standing slip is a single slot (guard_settings lastReceiptSessionId, ReceiptState.kt Z1 header), so once dismissed — or once the next finish claims the slot — no surface in the app can show that receipt again; the user is told their record lives in another app and given no way to put it there except transcribing a slip by hand.

---

## Journey: tasks-lived (5 findings)

**Verdict:** A real study day in Tasks is mined with silent losses: duplicate-titled tasks vanish at launch without undo, a low delete's undo expires off-screen in 5 seconds, advancing the queue from the Deck rewrites the sealed session's own receipt, merely jotting a task down crowns it ACTIVE, and SPLIT pastes canned problem-set steps onto whatever you actually wrote.

### [CRITICAL] Two tasks with the same title: the seed-sync silently deletes one, with no undo
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:394-406`

MonolithRepository.kt:394-406: inside ensureSeededAndSyncPackages, non-COMPLETED tasks are keyed by "${task.title.lowercase()}|${task.status}" and any repeat is removed with a direct dao.deleteTaskById(task.id) — no UndoDeleteStore copy, no banner, no notice. That sync runs at every process start (MonolithViewModel.kt:768, init) and on every PARK open via refreshSystemGuard (MonolithViewModel.kt:2449-2451). A study day legitimately contains repeated titles — 'Revision', 'Read Chapter 4' for two subjects, both QUEUED — and the later one is simply gone next launch. The app's only sanctioned delete (DEL) comes with an undo; this invisible one does not, and the COMPLETED-only permanence guard in the same block proves the authors knew repeat titles are normal for finished tasks while still deleting them for queued ones.

### [HIGH] Advancing the queue from the Deck rewrites the sealed session's receipt title; advancing from Tasks refuses to
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:2580-2590`

Deck DONE during a session calls completeTaskAndAdvance, which on promoting the next task rewrites the live session row: MonolithRepository.kt:2580-2590 sets currentSession.copy(intention = promoted.title) with no seal check. The receipt is built from that same field — assemblePendingReceipt sets title = row.intention and filters deflections by log.sessionIntention == row.intention (MonolithRepository.kt:1600-1625) — so finishing 'Maths' mid-session retitles the session's slip to the next task and drops every intercept logged under 'Maths'. The other advance door does the opposite on purpose: promoteTaskToActive (MonolithRepository.kt:2683-2700) refuses the identical intention write under the seal, commenting that a sealed session's intention 'is the receipt's title source — rewriting it under the seal would falsify the slip'. Same user act, two contradictory records, and the DONE door is the one that falsifies.

### [HIGH] Delete a task low in the queue and the UNDO banner is born off-screen with a 5-second life
*Location:* `app/src/main/java/com/example/ui/screens/FocusSequencerScreen.kt:276-310`

The undo banner is a LazyColumn item inserted near the TOP of the Tasks list — after the Horizon bar and composer, before the QUEUE header and every leaf (FocusSequencerScreen.kt, 'Undo deleted task banner' item, ~lines 276-310). Nothing scrolls it into view. Its life is UndoDeleteStore.WINDOW_MILLIS = 5_000L (UndoDeleteStore.kt:99), enforced by the dismiss delay in MonolithViewModel.kt:1786. A user who scrolls down a real day's queue and taps DEL on leaf #8 never sees the banner: it appeared off-screen above them and expired in five seconds, taking the only recovery for that delete with it.

### [MEDIUM] Adding the only task force-promotes it to ACTIVE — a mid-session jot-down hijacks NOW and starts billing time
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:2458-2460`

submitTask passes promoteToActive = orderedQueue.isEmpty() (FocusSequencerScreen.kt:~145, onAddTask(clean, 0, "Study", "", orderedQueue.isEmpty())), and the repository independently force-promotes whenever no ACTIVE row exists: shouldBeActive = promoteToActive || !hasActive (MonolithRepository.kt:2458-2460). So the first task a user types is instantly crowned ACTIVE/#01 and shown as NOW on the Deck before they have started anything; worse, if they finish their last task mid-session and jot a new one down for later, it becomes ACTIVE on the spot, the ViewModel's activeTask collector pushes its title into the intention (MonolithViewModel.kt:~784-790), and the running session's ticks start crediting seconds to it via accrueActiveTaskSeconds (MonolithRepository.kt:2423-2444). There is no way to merely write a task down.

### [MEDIUM] SPLIT does not split your task — it stamps the same three problem-set steps on every task you wrote
*Location:* `app/src/main/java/com/example/data/MonolithModels.kt:190-215`

The leaf's SPLIT verb (FocusSequencerScreen.kt, rendered only while subSteps.isEmpty()) calls autoSplitTaskIntoMicroSteps, which stamps FocusTaskEntity.defaultSubStepsForCategory(target.title, target.categoryTag) onto the task (MonolithRepository.kt:2912-2921). That generator never reads the title — it switches only on category (MonolithModels.kt, defaultSubStepsForCategory) — and every task the composer creates is hardcoded category "Study" (FocusSequencerScreen.kt submitTask), which falls into the else branch: 'Sketch diagram & list known variables', 'Solve core problem set step-by-step', 'Check units & boundary conditions'. SPLIT on 'Revise History dates' or 'Learn French verbs' decorates the leaf with the same three problem-set steps, presented as that task's own breakdown on both the Tasks leaf and the Deck.

---

## Journey: shield-setup-lived (5 findings)

**Verdict:** Setting up Shield is configuring blind: the screen never explains what its three tier words or three loadout words actually do, one unexplained tap can silently rewrite every decision the user just made by hand, and the categories secretly driving those decisions are invisible, guessed, and uncorrectable.

### [HIGH] One tap on the LOADOUT cap silently rewrites every hand-set app tier (and secretly flips two global shields on) — no preview, no confirmation, no undo, and the loadouts' own explanations are never rendered
*Location:* `ShieldMatrixScreen.kt:285-295; ShieldCapSelector.kt:64; MonolithViewModel.kt:2098-2117; MonolithRepository.kt:1126-1169; MonolithModels.kt:44-61`

ShieldCapSelector.kt:64 prints only the bare words STUDY/WORK/SLEEP; ShieldMatrixScreen.kt:285-295 fires onApplySmartLoadout immediately on tap with no dialog or preview anywhere in the screen. MonolithRepository.kt:1126-1169 then overwrites the tier of EVERY app row from category/name rules. MonolithViewModel.kt:2105-2116: picking STUDY additionally force-enables the Shorts/Reels sniper and browser URL blocker — side effects the tap never mentions. The explanations exist but are dead text: SmartLoadout.subtitle strings (MonolithModels.kt:44-61, e.g. STUDY 'Blocks Social & Games, sets YouTube & Messaging to 2m Peek, enables Shorts & Web shields.') are read by no UI code — only loadout.title is consumed (MonolithViewModel.kt:2100). A user who spent ten minutes hand-tuning sleds loses all of it to one curious tap, with no way back except redoing it.

### [HIGH] PEEK is sold with zero terms: the sled stop was renamed from '2m' to 'PEEK' and nothing on the setup screen ever says it means 2 minutes, once, then a mandatory 20-minute lockout
*Location:* `BlockitSuitcaseAndSwitchesViews.kt:407-409; MonolithRepository.kt:106-108; ShieldMatrixScreen.kt (whole file, no peek terms printed)`

BlockitSuitcaseAndSwitchesViews.kt:407-409: stops are arrayOf("SHUT","PEEK","OPEN") with the comment 'The middle stop said "2m" until tonight; the word is PEEK.' The actual contract lives only in code constants the user never sees: MonolithRepository.kt:106-108 PEEK_DURATION_SECONDS=120 and PEEK_COOLDOWN_MILLIS=20*60*1000 ('20-Minute Mandatory Cooldown'). Grep of ShieldMatrixScreen.kt finds no '2 MIN', duration or cooldown text anywhere — the tally and slip print only the word PEEK. A new user choosing the middle stop cannot know how long a peek lasts, that it is rationed, or why a second peek will later be refused.

### [HIGH] Apps are auto-decided by an invisible, uncorrectable substring guess of their category — the guess is never printed on the row, and it drives birth tiers, category filters and every loadout
*Location:* `MonolithRepository.kt:514-532; AppCatalogReconcile.kt:85-103; ShieldMatrixScreen.kt:1060`

MonolithRepository.kt:514-532 categorizePackage(): lowercased 'package + label' substring matching — contains('play') makes an app GAMES (catches the Play Store itself and any label containing 'display'), contains('reel') makes it SHORT_VIDEO, and the fallback for everything unmatched (banking, mail, maps, notes, camera) is SHOPPING. AppCatalogReconcile.kt:85-103: a newly discovered app is born LOCKED if the guess says SHORT_VIDEO/GAMES and PEEK_2M if MESSAGING, before the user has decided anything. Yet the catalogue row (ShieldMatrixScreen.kt:1060 CatalogueAppRow / AppRowSledToggleView) shows only the app name and the sled — the category is printed nowhere and no control anywhere lets the user view or fix it, while the filter chips and loadouts keep acting on it.

### [MEDIUM] The YOUR RULES slip looks like a printed ledger but every line is a hidden toggle: tapping a line silently flips its ruling SHUT<->PEEK and the line then disappears
*Location:* `ShieldMatrixScreen.kt:187-217; ShieldMatrixScreen.kt:882; ShieldMatrixScreen.kt:~965-975`

ShieldMatrixScreen.kt:187-217 slipTap: a tap on a slip line calls onSetAppTier flipping LOCKED<->PEEK_2M and starts the rest/exit choreography. SlipLineRow's own comment (ShieldMatrixScreen.kt:~965) states 'The whole 40dp line is the tap target; a tap flips the ruling'. The header (ShieldMatrixScreen.kt:882) prints only 'YOUR RULES' — no hint text, no button styling, nothing telling the user the list is tappable, that a tap changes enforcement, or that OPEN cannot be reached from here (revoking is only on the catalogue sled). A user tapping a line to inspect it has silently weakened or strengthened a block and watches the entry vanish 300ms later.

### [MEDIUM] Search only matches the display name, but its no-match verdict falsely tells the user the app is not on their phone
*Location:* `MonolithViewModel.kt:427-437; ShieldMatrixScreen.kt:518-530`

MonolithViewModel.kt:433-436: matchesQuery is only app.appName.contains(query, ignoreCase=true) — package name, category and former names are never searched. ShieldMatrixScreen.kt:522 then prints 'NO APP BY THAT NAME ON THIS PHONE' for any zero result. Searching 'musically' for TikTok, 'vending' for the Play Store, or 'chat' for messengers produces a printed claim that the app does not exist on the device, when the app is installed and merely not named that — a new user concludes Shield cannot see/control that app at all.

---

## Journey: routines-lived (5 findings)

**Verdict:** As lived, a routine is a silent ambush the user cannot rehearse, postpone or even configure precisely: it fires with no warning, rewrites his hand-set app rules without restoring them, counts overlapping windows as ACTIVE while never running them, and grades a perfectly compliant phone-in-pocket window as MISSED.

### [CRITICAL] A routine firing silently rewrites the user's entire hand-set app-rule catalogue — and nothing ever restores it
*Location:* `MonolithRepository.kt:1086-1170; MonolithViewModel.kt:375-378`

materialiseRoutineSession() calls applySmartLoadout(loadout) then startSession (MonolithRepository.kt:1086-1104). applySmartLoadout rewrites EVERY catalogue row by category and persists the whole table: dao.insertApps(updated) with tiers recomputed from package-name/category heuristics (MonolithRepository.kt:1126-1170). No snapshot or restore of prior tiers exists anywhere (grep for tier snapshot/restore: zero hits), and finaliseRoutineSession (1002-1058) restores nothing. Meanwhile the Shield loadout cap deliberately does NOT move for a routine firing — 'a routine applying its own loadout does not move his selector' (MonolithViewModel.kt:375-378) — so the screen keeps displaying the user's hand pick while the enforced rules are the routine's. Any per-app exception the user set by hand (a messaging app left OPEN, a game set to PEEK) is silently erased up to three times a day, including at 20:00 by SLEEP while he may not even be looking at the phone.

### [CRITICAL] Perfect compliance is judged MISSED: 'attendance' counts only intercepts and taps, so a phone left untouched all window reads as a no-show
*Location:* `MonolithRepository.kt:139-142, 959-989, 1024-1040`

finaliseRoutineSession judges the window from computeAttendedSeconds (MonolithRepository.kt:1024-1040): attended < 900s floor → MISSED, ATTENDED requires attended >= window seconds − 900. But 'attended' is not time under protection — it is evidence coverage: instants from intercept logs, in-session task completions and first/last evidence stamps, each instant covering only EVIDENCE_COVERAGE_MILLIS = 300_000 (5 minutes) (MonolithRepository.kt:139-142, 959-989). A user who does exactly what the app wants — leaves the phone in a drawer through the 09:00–19:30 DAY BLOCKS window, never touching a blocked app, generating zero intercepts — accrues ~0 attended seconds and is stamped MISSED, printed back to him as 'MISSED' via receiptOutcomeWord. Compliance that produces no incidents is indistinguishable from absence, and the app grades it as absence.

### [HIGH] The moment arrives with no warning, and the postpone/skip feature exists in the engine but has no button — it can never be used
*Location:* `RoutineHeartbeat.kt:34-38; MonolithModels.kt:550-565; RoutineDerive.kt:130-140; ShieldMatrixScreen.kt:643-777`

RoutineHeartbeat's own contract: 'this file... touches no UI or notification surface' (RoutineHeartbeat.kt:34-38) — a window materialises a session and raises the shield with zero advance warning (no pre-fire notice exists anywhere in the codebase) and no at-the-moment 'skip tonight' or 'postpone' verb. The engine fully implements pausing — RoutineEntity.pausedUntilMillis (MonolithModels.kt:550-565), WindowState.PAUSED and the J6 pause amnesty in RoutineDerive.kt:130-140, 196-205 — but nothing ever writes that field: grep shows its only occurrences are the migration default 0, the model declaration and derive-side reads. The RoutineRow UI offers only ON/OFF, ±30 chips, loadout cycle and day toggles (ShieldMatrixScreen.kt:643-777). Mid-conversation, mid-drive, mid-anything, the user's only choices are to have set OFF in advance or to endure / kill the session and forfeit the window.

### [HIGH] The Shield editor cannot say what the user means: ±30-minute steps only, silent dead taps at the clamps, and no way to create, delete or rename a routine
*Location:* `ShieldMatrixScreen.kt:730-762; MonolithViewModel.kt:1529-1544; MonolithRepository.kt:840-855`

The expanded editor (ShieldMatrixScreen.kt:730-762) offers only '−30' / '+30' chips for START and END and a blind loadout cycle — no time picker, no minute precision, so 21:15 or 21:45 can never be set. adjustRoutineTime silently returns with no feedback when the resulting span leaves 30..1080 minutes (MonolithViewModel.kt:1529-1544): the tap appears dead and the user cannot tell whether it registered. There is no create, delete or rename path at all — grep for addRoutine/deleteRoutine across the app finds nothing; the only RoutineEntity constructions are the three seeded targets FASTED BLOCK / DAY BLOCKS / SLEEP (MonolithRepository.kt:840-855). The collapsed row (ShieldMatrixScreen.kt:667-707) shows only start → end and days: no duration, no 'starts in Xh', and for the overnight SLEEP row '20:00 → 03:30' nothing indicates it crosses midnight or that the after-midnight hours are governed by the previous day's mask bit (RoutineSchedule.windowContaining checks yesterday's day, MonolithModels.kt:639-665). What 21:00 will do cannot be read off what the editor shows.

### [HIGH] Overlapping or manually-occupied windows silently never fire — while Shield still counts them 'ACTIVE'
*Location:* `RoutineDerive.kt:146-163; RoutineHeartbeat.kt:153-158; ShieldMatrixScreen.kt:299-310`

deriveOne returns OPEN_HELD — no desired session — whenever any other session occupies the window, including a manual session the user started for another purpose and including another routine's overlapping window (RoutineDerive.kt:146-163). The heartbeat then 'materialises at most one desired session' per pass, first in list order (RoutineHeartbeat.kt:153-158). The editor places no constraint and shows no warning against overlapping windows (adjustRoutineTime checks only the 30..1080 span, MonolithViewModel.kt:1529-1544), and the Shield header counts every enabled, day-carrying routine as 'ACTIVE' (ShieldMatrixScreen.kt:299-310, RoutineSchedule.liveCount) regardless of whether it can ever fire. The swallowed window is later judged HELD in the receipt dialect with no in-the-moment explanation: at 21:00 the user simply watches nothing happen for a routine the app calls ACTIVE.

---

## Journey: park-guard-lived (5 findings)

**Verdict:** As lived, the Guard board is a diagnostic the user cannot use when it matters: it certifies NOTIFICATIONS RUNNING while nothing is held, seals itself shut silently during the sessions where failures appear, decays into unactionable STALE while being read, and contradicts its own header and its own duplicated sled lines.

### [CRITICAL] Board prints NOTIFICATIONS RUNNING with no session running — the word is derived from listener binding alone, while the listener holds nothing outside an active session
*Location:* `app/src/main/java/com/example/service/WatchStore.kt:451; app/src/main/java/com/example/service/MonolithNotificationListener.kt:72`

WatchStore.deriveInterceptor (service/WatchStore.kt:451-475) derives NOTIFICATIONS with setting hardcoded true and only the listener binding as parent, and explicitly makes its suspension always false — no active-session conjunct anywhere. The actual listener does nothing without one: MonolithNotificationListener.kt:72-73 returns early unless dao.getActiveSessionSnapshot() isActive, and :76 only captures apps whose tier is not OPEN. A user at rest, or in a session with the leaking app set OPEN, reads 'NOTIFICATIONS — RUNNING' on the board (ParkDrawer.kt interceptor rows) while zero notifications are being held — the exact claim-vs-experience gap Sarthak reported from his own phone.

### [HIGH] Guard board is unreachable exactly when something breaks mid-session: sealed taps silently do nothing, and a seal landing under an open drawer silently kills FIX and the sleds
*Location:* `app/src/main/java/com/example/MainActivity.kt:300; app/src/main/java/com/example/ui/components/ParkDrawer.kt:416`

MainActivity.kt:289-300: openPark() is `if (!sealed && !viewModel.navigationLocked()) parkOpen = true` — a Guard-line tap during a session falls through with no message, animation or explanation. If a routine seals while the drawer is already open, ParkDrawer.kt:416 hides the FIX key (`visible = firstMissing != null && !isSealed`) and the sound/shorts/social sleds get invisible clickable overlays that swallow every tap (ParkDrawer.kt ParkSocialSitesRow and sled rows) — nothing in the drawer ever prints that it has gone read-only or why. The user is locked out of diagnosis precisely when a leak is visible.

### [HIGH] STALE is a dead end: board and header treat it as a lack, but no FIX key and no re-measure exist for it — and the open board ages its own GRANTED lines into STALE every 45s
*Location:* `app/src/main/java/com/example/ui/components/ParkDrawer.kt:175; app/src/main/java/com/example/MainActivity.kt:327; app/src/main/java/com/example/service/WatchStore.kt:150`

ParkDrawer.kt:175-179 computes firstMissing from PermPrint.MISSING only, and the FIX key's visibility (ParkDrawer.kt:416) keys off firstMissing — a STALE line is styled as a lack (wordIsLack=true for STALE) and the header Guard line names it as the lack (GuardLine.kt:39-44), yet no FIX and no re-check control exists for it. Worse, opening PARK fires no Watch sweep: MainActivity.kt:325-329 only calls refreshSystemGuard(), whose own comment admits 'Its result renders NOWHERE'; sweeps run only on ON_RESUME and Deck entry. Meanwhile ParkDrawer.kt:168-173 re-derives staleness every 5s and WatchStore.kt:150 sets STALE_AFTER_MS=45_000, so USAGE ACCESS / DEVICE ADMIN / OVERLAY / BATTERY lines decay from GRANTED to STALE in front of a user who simply reads the board for 45 seconds — manufactured alarm with no offered remedy except leaving the app and coming back.

### [MEDIUM] Header can print GUARD 4/4 (whole) while the board it opens shows OVERLAY/BATTERY MISSING in lack styling and offers a FIX key for them
*Location:* `app/src/main/java/com/example/ui/components/GuardLine.kt:39; app/src/main/java/com/example/service/WatchStore.kt:98`

GuardLine.kt:36-39 counts only WatchItemId.COUNTED (WatchStore.kt:98-100: accessibility, usage, device admin, listener) and prints 'GUARD 4/4' when those four are granted. The board's permission family has six lines (WatchStore.kt PERMISSIONS), and OVERLAY and BATTERY print in the same bold lack styling when MISSING — and ParkDrawer.kt:175 includes them in firstMissing, so the FIX key can be sitting under a header that simultaneously claims wholeness. The user taps a line that says everything is whole and is handed a repair job it never mentioned.

### [MEDIUM] One drawer, two 'SHORTS + REELS' lines that can contradict each other at the same moment: board says OFF, sled below says ON, with no link printed between them
*Location:* `app/src/main/java/com/example/ui/components/ParkDrawer.kt:366; app/src/main/java/com/example/service/WatchStore.kt:459`

The board's interceptor family prints a 'SHORTS + REELS' line with the measured vocabulary RUNNING/PAUSED/OFF (ParkDrawer.kt interceptor rows, derived in WatchStore.kt:451-475 from setting + accessibility parent + safe-break), and further down the same drawer a second row also named 'SHORTS + REELS' carries the sled with the vocabulary ON/OFF (ParkDrawer.kt section (5) sled row, its word delayed 260ms behind the fact). With accessibility missing the user sees, on one screen, SHORTS + REELS: OFF above and SHORTS + REELS: ON below — both true in their private senses, contradictory in the user's, and nothing on the drawer explains that one is the setting and the other is the measured outcome, or which missing line above is causing the OFF.

---

## Journey: vault-history-lived (5 findings)

**Verdict:** Coming back tomorrow, the Drawer cannot answer 'did I actually focus this week?' — it files only ticked-off tasks (with lifetime minutes dumped on completion day, including zero-second completions made outside any session), sessions themselves leave no trace, yesterday's day word is unfillable, and the Curios cabinet records nothing about any real day.

### [HIGH] Drawer stores task completions, not sessions — a focused day with no ticked-off task leaves no trace in history
*Location:* `DrawerScreen.kt:51-65; MonolithDao.kt:206-212; MonolithViewModel.kt:486-504`

DrawerScreen.kt:51-65 header defines the Drawer as 'Permanent storage for finished tasks... A stored task is a COMPLETED row of focus_tasks'; MonolithDao.kt:206 getStoredPage filters WHERE status='COMPLETED' and :209 getStoredMonths likewise — focus_sessions are never queried by any Drawer path. Meanwhile the Deck/receipt world counts sessions: MonolithViewModel.kt:486-504 totalFocusedMinutes sums session elapsedSeconds, and MonolithRepository.kt:2361 compute7DayHistory is session-based. A user who sits a full 90-minute session (manual or routine) but never ticks a task COMPLETED returns tomorrow to a blank day or 'NOTHING STORED YET' — the session they lived, and that the Deck counted, has no existence in the only history surface.

### [HIGH] A task's SPENT is its lifetime accrual dumped entirely onto its completion day
*Location:* `MonolithRepository.kt accrueActiveTaskSeconds; DrawerScreen.kt:92-105, StoredRow`

MonolithRepository.accrueActiveTaskSeconds (~L2425-2445) keeps adding wall time into one task's actualSecondsSpent across every session until it is completed, crediting only the single ACTIVE (or first QUEUED) task and pausing during Safe Breaks. DrawerScreen.kt:92 buildDayGroups groups solely by DrawerDay.filingDate(completedAtMillis), and StoredRow prints that lifetime total as that day's 'SPENT'. Work a task Mon+Tue, finish it Wed: Mon/Tue headings show nothing for it and Wed claims all its minutes; other tasks worked in the same sessions accrue 0. The per-day shelves therefore never reconstruct what any given day actually contained.

### [HIGH] Tasks ticked done with no session ever run become permanent 'stored' history (SPENT 0 SEC)
*Location:* `MonolithViewModel.kt:1700-1712; MonolithRepository.kt completeTaskAndAdvance, :2602`

completeTaskAndAdvance has no session guard at either layer: MonolithViewModel.kt:1700 launches it unconditionally, MonolithRepository.completeTaskAndAdvance stamps completedAtMillis=now with completedInSessionId = currentSession?.id ?: 0 and writes the COMPLETED row even when no session exists. That row is then permanent Drawer history, printed by StoredRow as a stored record with 'SPENT 0 SEC', indistinguishable in kind from genuinely focused completions — while the session receipt path (completedTaskSummaryEntry, MonolithRepository.kt:2602) inflates the same task to '(actualMins).coerceAtLeast(1)' = '1m actual'. Ticking boxes at the desk, with the app never sealed, manufactures a focus history.

### [MEDIUM] Day words can only be declared today — yesterday's ILL/REST day can never be explained after the fact
*Location:* `MonolithViewModel.kt declareTodayWord; DrawerScreen.kt DayHeading`

MonolithViewModel.kt declareTodayWord (~L1951-1965) hardcodes the filing date to DrawerDay.filingDate(System.currentTimeMillis()) with the comment 'only today's filing date is declarable from here — the Drawer files words, it does not rewrite past days', and DrawerScreen DayHeading renders the tappable REST/ILL/FAMILY words only when isToday. The user who was ill yesterday and never opened the app — precisely the person the word exists for — comes back to a permanently blank yesterday, indistinguishable from an idle or skipped day, and no screen offers a way to file the word late.

### [MEDIUM] Cabinet/Curios is amnesiac decoration: random duplicate forges collapse to a binary stamp with no when, which session, or how many
*Location:* `DrawerScreen.kt CABINET view; MonolithRepository.kt:2049-2051; MonolithDao.kt:74`

The cabinet prints 'A FINISHED SPAN FORGES ONE — THE FORGE CHOOSES WHICH.' (DrawerScreen.kt cabinet list). The forge at MonolithRepository.kt:2049-2051 fires only when allBlocksCompleted && !isRoutineSession and picks CurioCatalog.artifacts.shuffled().first() — random, so repeat finishes re-forge duplicates; routine finishes and stopwatch/early finishes forge nothing in the cabinet at all (an early end instead stamps forgedArtifactId='partial_stamp' in disarmActiveSession, an id no catalog row matches). MonolithDao.kt:74 reads DISTINCT forgedArtifactId, collapsing all repeats, and the cabinet detail (DrawerScreen.kt CABINET) prints only lore plus 'FORGED — IT STANDS IN THE CABINET' / 'NOT YET FORGED' — no date, no session, no count. After enough finishes the cabinet stops changing entirely and tells the returning user nothing about any actual day.

---

## Journey: desk-clock-lived (5 findings)

**Verdict:** As lived, the desk clock is a fragile in-app poster — it vanishes behind the lock screen at the first interruption, barely moves its pixels despite the burn-in claim, hides the user's breaks, and exits to full controls on any stray touch.

### [HIGH] 'Always-on' is only until the first interruption: clock is an in-app overlay with no lock-screen presence
*Location:* `app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:83-101; app/src/main/java/com/example/MainActivity.kt (no showWhenLocked/turnScreenOn anywhere)`

Overlay premise comment claims 'the screen stays on while the clock shows' via FLAG_KEEP_SCREEN_ON (AlwaysOnDeskClockOverlay.kt:83-99 in AirlockAndDisarmOverlays.kt), but that flag only works while MainActivity's window is the visible foreground window. Grep of all main sources finds no setShowWhenLocked, setTurnScreenOn, wake lock, or lock-screen visibility anywhere, and MainActivity.kt declares no such flags. So one power-button press, one incoming-call/alarm interruption that ends on the keyguard, or any task switch permanently puts the 'always-on' clock behind the lock screen with no way back on its own — the user returns to a locked black phone, not a desk clock.

### [HIGH] Burn-in 'figure-8' is a ±6-pixel 5x5 raster: the giant numerals sit on effectively the same OLED pixels all session
*Location:* `app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:140-171; app/src/main/java/com/example/ui/MonolithViewModel.kt:955-957`

VM names it _burnInFigure8MinuteStep and comments 'Shift Always-On Desk Clock figure-8 coordinates every 60 seconds to prevent OLED burn-in' (MonolithViewModel.kt:252-253, 955-957), stepping +1 per 60 ticks. The overlay converts it as dx=((burnInStep%5)-2)*3, dy=(((burnInStep/5)%5)-2)*3 into IntOffset(dx,dy) (AirlockAndDisarmOverlays.kt:140-147) — IntOffset is raw pixels, so the 96sp cream numerals (line 164-171, NightColorPrimary 0xFFFFE4CF) wander a 5x5 raster of only ±6 pixels (~2dp), repeating every 25 minutes. That is neither a figure-8 nor a meaningful shift: a ~55px-wide glyph moved 6px keeps almost every lit pixel lit in the same place for the entire session, all night, every night.

### [HIGH] Desk clock erases Safe Breaks: user on the clock never learns their break started, so the break is silently spent at the desk
*Location:* `app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:102-131,196-215; app/src/main/java/com/example/data/MonolithModels.kt:385-398`

The desk clock deliberately has no block context: displaySecs always reads spanRemainingSeconds, and the in-code law states 'During a break the span simply keeps counting - the break is not rendered AS a break' (MonolithModels.kt span-derivation comment ~L385-398; AirlockAndDisarmOverlays.kt:102-131). The overlay file never reads isInSafeBreak, shows no BREAK word, and keeps printing the active task's banner (L196-215). The Deck's BREAK eyebrow — named in the comment as the break's 'one rendering' — is covered by this full-screen overlay. A user who puts the phone on the desk clock to take their Safe Break sees the countdown and their task title roll on unchanged and gets no signal their break started or is ending; they can sit out the whole break still 'working'.

### [MEDIUM] Glance tells you a number and nothing else: no remaining/elapsed label, no time of day, no end time — and the stopwatch bar pins at full after 20 minutes
*Location:* `app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:103-131,154-171`

The only hero is a bare String.format('%02d:%02d', mm, ss) (AirlockAndDisarmOverlays.kt:103-115) with no REMAINING/ELAPSED label anywhere in the overlay, yet the same face counts DOWN spanRemainingSeconds for timed sessions and UP elapsedSeconds for stopwatch sessions (L103-109) — from across a desk the two are indistinguishable. It also never shows the wall-clock time of day or the session end time, despite calling itself a 'DESK CLOCK'. Worse, in stopwatch mode the progress bar is elapsed/1200f coerced to max 1f (L130-131): after 20 minutes the bar sits completely full for the rest of an hours-long session, reading as 'finished' at a glance while the digits keep climbing.

### [MEDIUM] Tap-anywhere-to-exit means anyone's stray touch dismisses the clock and lands on the unlocked, fully operative Deck
*Location:* `app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:143-156; app/src/main/java/com/example/ui/MonolithViewModel.kt:1563-1566; app/src/main/java/com/example/MainActivity.kt:743-750`

The header prints 'NIGHTSTAND DESK CLOCK • TAP ANYWHERE TO EXIT' and the entire full-screen Box carries .clickable(onClick = onDismiss) (AirlockAndDisarmOverlays.kt:143-156); onDismiss just flips _isAlwaysOnDeskClockVisible to false (MonolithViewModel.kt:1563-1566) and MainActivity.kt:743-750 returns the user to the fully operative Deck. There is no hold, no confirmation, no passcode. One stray touch — a desk bump, a sleeve, a child or coworker tapping the interesting glowing phone — both kills the always-on display (clearing FLAG_KEEP_SCREEN_ON on dispose, L95-99, so the phone then times out and locks mid-session) and hands whoever touched it the live session controls, including the break/disarm doors, with zero friction from an app whose whole pitch elsewhere is friction.

---

## Journey: sound-haptics-lived (5 findings)

**Verdict:** As lived, the sound system is a private language the user cannot trust: ON can mean silence on a muted phone, one sled flip speaks in two voices (louder when muting than unmuting), the seal clunk mostly means housekeeping while the real seal and finish sounds can be silently skipped or fired from off-screen.

### [HIGH] SOUND ON is indistinguishable from OFF on a muted phone: no volume, ringer, DND or output-routing check exists anywhere
*Location:* `app/src/main/java/com/example/service/TactileSoundEngine.kt:389,426,521-538; app/src/main/java/com/example/service/SessionSealTransition.kt:113-117`

Every AudioTrack and the focus request are built with fixed USAGE_ASSISTANCE_SONIFICATION attributes and fixed PCM gains (0.38f-0.68f) at TactileSoundEngine.kt:389,426,529; a grep of app/src/main finds zero reads of getRingerMode, getStreamVolume, currentInterruptionFilter, audio-device/routing or setPreferredDevice anywhere. PARK prints only ON/OFF (ParkDrawer.kt:479-510) and the plate claims 'SOUND: FLOAT-PCM, 4 PARTIALS · POWER: YOUR PHONE' (ParkDrawer.kt:123). Worse, SessionSealTransition.kt:116 treats a played Throw as 'a seal already heard' and never re-sounds it — on a muted phone it was heard by nobody, yet the marker stands.

### [HIGH] Every PARK sled flip sounds twice, and the sound sled is asymmetric: muting clicks loudly, unmuting's click is swallowed
*Location:* `app/src/main/java/com/example/ui/views/BlockitSuitcaseAndSwitchesViews.kt:168-176; app/src/main/java/com/example/ui/MonolithViewModel.kt:1435-1439,1484-1499`

SledSwitchView.onTouchEvent fires fireFeel(8L) on every ACTION_UP (BlockitSuitcaseAndSwitchesViews.kt:171), which BlockitCoreViews.kt:238 maps to playFirmClick (sound+haptic). Only then does onToggleRequested run the VM handler, which fires a second voice: toggleSound plays playRotaryDetent(720) when turning ON (MonolithViewModel.kt:1439), toggleShortsReelsSniper/toggleBrowserUrlBlocker play playMechanicalLatch (MonolithViewModel.kt:1489,1496). Because the view fires before the fact flips, turning sound OFF audibly clicks at full volume (gate still ON), while turning ON the click's sound is gated out and only its haptic plus the detent survive — the same gesture sounds different in each direction, and never like the single calibrated throw either layer documents.

### [MEDIUM] The 'session seal clunk' is spent on undo, cueing a task and picking a loadout — the real seal sounds completely different
*Location:* `app/src/main/java/com/example/service/TactileSoundEngine.kt:705-727; app/src/main/java/com/example/ui/MonolithViewModel.kt:1799,1968,2002,2020,2099,2135,2142,2421`

TactileSoundEngine.kt:705-707 documents playHarmonicSeal as 'Session seal clunk: full-force thunk... then the lock seating', but MonolithViewModel calls playHarmonicSeal(arming=true) for undoDeleteTask (1799), splitActiveTaskAndReturn (1968), autoSplitTaskIntoMicroSteps (2002), cueTaskAndNavigateToFocus (2020), applySmartLoadout (2099), addCustomApp (2135), createCustomProfile (2142) and end2MinutePeekEarly (2421). The actual seal never uses it: SessionSealTransition.kt:142 fires the unrelated 1-second playSealThrow, and armMonolithSession's own comment boasts the pre-proof seal sound was deleted because 'a seal sound before proof is intent wearing proof's clothes'. The seal vocabulary is spent on housekeeping, so the one sound that means 'you are locked in' teaches the user nothing.

### [MEDIUM] Finish the session a moment too slowly and the completion pulse is silently deleted — only a 900ms window, no late play
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:1241-1262`

MonolithViewModel.kt:1241-1262: the finish sequence plays the E0 seat knock, then the completion pulse only 'if (SystemClock.elapsedRealtime() - e0Mono < 900L)' after delaying to E700; the code's own comment admits 'Death-booked: the E700 slot stands EMPTY'. There is no late-fire, no substitution: the user hears the settle knock and the E880 knock pair with a hole where the winding-down voice — described at TactileSoundEngine.kt:833-837 as the completion signal and elsewhere as 'the completion voice's only lawful spend' — should be. On a slow or loaded phone the most important sound of the session is the one that silently doesn't happen.

### [MEDIUM] During a peek the phone buzzes every 15 seconds from an invisible app, then 'seals' when the peek expires
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:984-999,2421-2430`

The VM's 1-second ticker, while a peek lease is active, fires soundEngine.playRotaryDetent(760.0) whenever nextSec % 15 == 0 (MonolithViewModel.kt:996-997) — i.e. at 105/90/75/60/45/30/15s remaining, while the user is by definition inside the peeked blocked app and Monolith's own UI (including the pill that would explain the buzz) is not on screen; each detent is also a haptic waveform via TactileSoundEngine.kt:630-640. At expiry the same ticker calls end2MinutePeekEarly(), which plays playHarmonicSeal(arming=true) (MonolithViewModel.kt:2421-2430) — a seal clunk sounding from an app the user isn't looking at, announcing an event ('peek over') with the voice for the opposite one ('session arming').

---

## Journey: things-go-wrong-lived (5 findings)

**Verdict:** On every failure return — revocation, reboot, or simply coming back late — the app either keeps printing the seal-day lie (GUARD 4/4), silently rewrites the session's length and record, or withholds the very things it promised to hold for the user, and in no case does it tell him what happened or give him a way back.

### [CRITICAL] Held notifications become unreachable the moment the session ends — the only reading door is a chip that exists only while a session runs
*Location:* `app/src/main/java/com/example/ui/screens/TactileDeckScreen.kt:901; app/src/main/java/com/example/MainActivity.kt:518`

The digest's only door is the Deck's HELD chip, and that chip is composed only inside `if (isSessionRunning && !shotActive)` (ui/screens/TactileDeckScreen.kt:901-920, HELD chip at :914-919). The sole wiring of onOpenNotificationDigest in the whole app is MainActivity.kt:518, and toggleNotificationDigestSheet(true) has no other caller (grep over app/src/main). So the instant a session ends — watched or unwatched, e.g. it finished while the phone was in a drawer or the app was killed — the chip vanishes and there is no screen, menu or receipt link that opens CapturedNotificationsDigestSheet (MainActivity.kt:756) again. The captured rows stay in Room, and even the delete/clear verbs live only inside that unreachable sheet (MainActivity.kt:765-766). The user returns to an idle Deck with a dial: no count, no 'held while you were away', no way to read what the app intercepted on his behalf.

### [CRITICAL] Revoke a permission mid-session and the header still prints GUARD 4/4 for the rest of the session — and the running Deck's explanation/FIX sentence is deliberately suppressed
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:663; app/src/main/java/com/example/ui/components/GuardLine.kt:39; app/src/main/java/com/example/ui/screens/TactileDeckScreen.kt:1072`

guardLinePrint explicitly substitutes the seal-frame freeze for the live measurement whenever a session is active: 'if (session?.isActive == true && freeze?.guardLine != null) freeze.guardLine' (ui/MonolithViewModel.kt:663-677), and GuardLine.kt:39 prints that as a present-tense fact, 'GUARD 4/4'. The live sweep does run on Deck entry/resume/FIX return (MonolithViewModel.kt:587-595) — its result is simply discarded for the header, which sits over every screen (MainActivity.kt:422). Meanwhile the Deck's one sentence slot that names a dead permission and offers a FIX action is forced silent for exactly this case: 'isSessionRunning -> null' (ui/screens/TactileDeckScreen.kt:1069-1075). The only honest signal is the strip's BLOCK cell flipping to OFF (TactileDeckScreen.kt:508) — unexplained, unactionable, and directly contradicted by the GUARD 4/4 printed above it. Unlike the imprint tenant, the guard line carries no 'AS SEALED' tense mark.

### [HIGH] Come back more than 10 minutes after a session ended and its finish is deliberately thrown away — no receipt, no notice, and no way to ever get one
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:1173`

startFinishController's branch U (ui/MonolithViewModel.kt:1150-1157, 1173-1180): a finish first seen within 1.8s gets the witnessed path, within 10 minutes a quietly-issued receipt, but 'older ⇒ the moment is gone — marked handled, never fabricated into a late №' — the else branch just does handledFinishIds += row.id and emits nothing: no receipt, no banner, no 'your session ended while you were away' line anywhere. The user opens the app to a plain idle Deck; his completed session exists only as silent minutes inside history/stats. There is no recovery verb either: the VM's publishReceipt(sessionId) facade (MonolithViewModel.kt:1349-1368) has zero call sites in any screen, and it refuses outright when the id is in handledFinishIds (:1351) — which branch U just added. The discard is permanent and user-invisible by design.

### [HIGH] An honest mid-session reboot brands the user a tamperer and silently stretches the session by 15 minutes per block — and no screen ever mentions it
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:749; app/src/main/java/com/example/data/MonolithRepository.kt:1947`

computeVerifiedSessionElapsed (data/MonolithRepository.kt:740-749) flags tamper when wall-vs-mono drift exceeds 90s. After a reboot, updateSessionProgress re-anchors only the mono/boot fields (data/MonolithRepository.kt:1951-1955) while startWallTimeMillis stays at the original session start — so on every later tick wall elapsed (start-to-now, including pre-reboot time and downtime) is compared against mono elapsed since the re-anchor, drift is permanently >90s for any session that had run over ~90 seconds, and tamper=true sticks. That applies the 15-minute penalty (MonolithRepository.kt:111, 1947: penaltyAdd = HMAC_TAMPER_PENALTY_SECONDS), and because spanTargetSeconds prices EVERY block at effectiveTargetSeconds (data/MonolithModels.kt:414-429), a 4-block session silently grows by 60 minutes. No surface ever tells him: tamperDetected/penaltySecondsAdded are rendered by no composable in the app (grep: the field exists only in MonolithModels.kt:299 and repository writes) — the end time on the Deck just moves, unexplained, and the receipt carries no penalty line.

### [HIGH] After a reboot the session clock credits the phone's entire uptime as focus time, however long the phone sat unguarded before anything ticked
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:755`

In computeVerifiedSessionElapsed's reboot branch (data/MonolithRepository.kt:751-756): 'val sinceRebootSec = (nowMono / 1000L).toInt(); Pair(priorElapsed + sinceRebootSec, !hmacValid)' — elapsed after a reboot is prior elapsed plus the device's ENTIRE uptime at the first post-reboot tick, with no reference to whether the shield, the app, or anything was actually guarding during that uptime. If the shield failed to restart (the OEM/kill cases earlier rounds documented) and the user first opens the app two hours after booting, those two hours book as focus time and can complete blocks on the spot. The only admission the user ever gets is the receipt's single line 'THE SHIELD WAS OFF FOR PART OF THAT SESSION' (ui/components/ReceiptSheet.kt:145-153) — 'part' doing heavy lifting for time that may have been wholly unguarded yet fully credited.

---

## Journey: moment-of-temptation (5 findings)

**Verdict:** At its one moment of truth the app spends its friction on the user's good intentions — the honest button strands them in the blocker, a mistake costs the weekly token — while the craving itself is priced at 5 seconds of patience, paid out through a peek whose promised countdown pill was never even drawn.

### [CRITICAL] The peek's countdown pill is never drawn: the user spends a 2-minute lease with no clock, no warning and no way to hand it back
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:2381-2426, 986-1003; app/src/main/java/com/example/data/MonolithRepository.kt:1194-1197`

MonolithViewModel.kt:2381-2383 KDoc promises the peek is granted 'with the Draggable Edge-Snapping Countdown Pill', and start2MinutePeek (MonolithViewModel.kt:2400) plus the per-second ticker (MonolithViewModel.kt:986-1003) maintain FloatingPeekPillState — but grep of the entire app/src tree shows floatingPeekPill has ZERO collectors outside the ViewModel: no composable in MainActivity.kt, screens/ or components/ ever renders it, and no WindowManager overlay view exists anywhere in the app. The lease also starts at tap-time inside Monolith (MonolithRepository.kt:1194-1197 sets activePeekUntil = now + 120_000 ms when startPeekLease runs), so the user burns lease seconds navigating back to the craved app, then gets no countdown, no expiry warning and no early-end control — end2MinutePeekEarly (MonolithViewModel.kt:2418) has no UI caller, only the internal expiry path at line 994. The VM even plays a rotary-detent click every 15 s of the peek (MonolithViewModel.kt:996-998) for a pill nobody can see. Distinct from the known process-death pill title, which presumes the pill renders in a healthy process — it never renders at all.

### [HIGH] 'RETURN TO FOCUS' returns nowhere — it only closes the sheet and strands the user inside the blocker app
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:2428-2444; app/src/main/java/com/example/MainActivity.kt (no finish/moveTaskToBack anywhere); app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:655-661`

The chip is labelled 'RETURN TO FOCUS' (AirlockAndDisarmOverlays.kt:655-661), but its handler transmuteImpulseAndReturn (MonolithViewModel.kt:2428-2444) does exactly one visible thing: _airlockState.value = state.copy(isVisible = false). MainActivity.kt contains no finish() or moveTaskToBack() call anywhere. The user was yanked out of whatever they were doing into Monolith's activity by the intercept; choosing the honest door leaves them standing on Monolith's Deck and they must find their way back to their actual work app by hand. The app's friction lands on the good intention: the virtuous tap costs a manual re-navigation, while the screen's other exits cost nothing.

### [HIGH] The whole 'breathing delay wall' is 5 seconds, after which one tap buys 2 minutes in the craved app — and the cooldown is per-app, so the craving just rotates
*Location:* `app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:558-568, 663-676; app/src/main/java/com/example/data/MonolithRepository.kt:106-108; app/src/main/java/com/example/ui/MonolithViewModel.kt:2325`

The airlock's entire resistance is a countdown hardcoded from 5 (AirlockAndDisarmOverlays.kt:560-568). When it reaches 0 the screen does not send the user back or escalate — it simply adds the EMERGENCY PEEK chip (lines 663-676), and one tap grants 120 s inside the craved app (MonolithRepository.kt:106-108: PEEK_DURATION_SECONDS = 120). The wait-to-reward ratio is 5 s of patience for 24x that in the app. The 20-minute cooldown that supposedly rations this is per app row (MonolithViewModel.kt:2325 reads getPeekCooldownRemainingSeconds for that app's entity only), so the moment one app's peek is spent, the next blocked app offers a fresh 5-second wall and a fresh 2 minutes — the craving just rotates icons.

### [HIGH] An accidental session start has a 10-second undo window; at second 11 the only exit costs the week's one emergency token
*Location:* `app/src/main/java/com/example/ui/screens/TactileDeckScreen.kt:1029-1058; app/src/main/java/com/example/MainActivity.kt:514-515; app/src/main/java/com/example/ui/MonolithViewModel.kt:2236-2262; app/src/main/java/com/example/data/MonolithRepository.kt:109`

The Deck renders the free 'GRACE WINDOW ABORT' chip only while activeSession.elapsedSeconds <= 10 (TactileDeckScreen.kt:1029-1045); its handler is the sole caller of the token-free confirmDisarmSession path (MainActivity.kt:514-515). After second 10 the chip is replaced by 'EMERGENCY BREAK VALVE (60S HOLD)', and requestDisarmChallenge (MonolithViewModel.kt:2236-2262) routes every timed session — and any stopwatch session under the 20-minute floor (MonolithRepository.kt:109: STOPWATCH_MIN_FLOOR_SECONDS = 20*60) — into the valve that spends the week's single emergency token. A session started by a stray tap and noticed a minute later can only be undone by burning the one break reserved for a genuine emergency, or by serving the session out.

### [MEDIUM] Two opposite doors are both named EMERGENCY, and each one's real price is printed only in small, dim type
*Location:* `app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:663-685, 796-822, 615-639; app/src/main/java/com/example/ui/screens/TactileDeckScreen.kt:1047-1050`

The airlock's cheap, repeatable door is labelled 'EMERGENCY PEEK' (AirlockAndDisarmOverlays.kt:663-676) while the Deck's session-ending, once-a-week door is labelled 'EMERGENCY BREAK VALVE (60S HOLD)' with its scarcity shown only as a mono badge '$weeklyEmergencyTokensRemaining/1 WEEK' (TactileDeckScreen.kt:1047-1050). Nothing in either label tells a depleted user that one costs a 20-minute per-app cooldown and the other costs the whole session plus the week's only break. The peek's actual price is a single 12.5sp sentence at 80% alpha under the chip (AirlockAndDisarmOverlays.kt:678-685), and the break's mechanics — banking and draining — are explained only in 11sp text at 75% alpha (lines 817-822: 'Hold to fill 60 seconds. Every 15 seconds banks. If your hand slips, what isn't banked drains away. The banked part stays.') that must be parsed before starting a 60-second continuous hold on a 68dp strip (line 796). At the moment of least capacity, the consequential information is the smallest, dimmest text on both screens, while the craved app's name (24sp) and countdown (88sp) get the hero treatment (lines 615-639).

---
