# Focus-app — Round 3b Audit: 50 More User Perspectives

Date: 2026-10-30 · Scope: 50 personas, micro-journeys and situations · Deduped against the 447 findings of rounds 1–3 (one near-duplicate flagged by title-similarity sweep and discounted below)

**Totals: 238 findings reported (237 net new) — 24 critical, 125 high, 85 medium, 4 low** (running total across all rounds: 684)

## The criticals

- **[one-hand-commute]** Pocket-arming: a 90ms pocket/bag press on the slide cap is a full order to seal a session
- **[urgent-contact-mid-session]** No person can break through: notification suppression is package-only, sender/priority-blind — urgent text is treated exactly like spam
- **[real-emergency-exit]** Second emergency in the same week has no exit at all: one token per week, and at zero the valve is not even rendered
- **[real-emergency-exit]** The emergency exit is not where the emergency happens: the Airlock for the blocked app you need has no valve, so the lawful path is a 65s+ multi-screen detour ending in a 60-second continuous finger hold
- **[otp-banking-mid-session]** OTP and UPI-approval notifications get no exemption — the original notification and its Approve action are cancelled, only title/text survive
- **[otp-banking-mid-session]** Sealed digest is a frozen snapshot: an OTP arriving after you open HELD is not shown at all until the session ends
- **[armed-wrong]** Second-11 cliff: a mistaken arm stops being free and becomes the week's sole emergency token - or unexitable if it is spent
- **[unlisted-distraction]** Unrecognized real distraction is born OPEN by a keyword guess — silently, with no review prompt
- **[unlisted-distraction]** No catalogue row means no law at all: an app installed after the last sync is invisible to both enforcement doors for the whole session
- **[browser-as-loophole]** A 2-minute peek on the browser itself switches off the entire site blocker — one knock unlocks every blocked site at once
- **[browser-as-loophole]** The biggest feed is not on the list: youtube.com home/watch feed is fully open in the browser — only the /shorts path is blocked
- **[shorts-reels-lived]** The sanctioned YouTube peek is a 2-minute Shorts pass: a live peek lease silences the sniper for exactly the window the user is inside the app
- **[sick-day-word]** Declaring ILL changes nothing: routines still auto-start blocking sessions on the sick day and the window is still judged MISSED
- **[digest-too-late]** Post-session digest rows are dead text: tapping a held item goes nowhere, despite the code's own 'tapped to open' promise
- **[custom-profile-lived]** Custom profile's time window is write-only: saved as 'schedule enabled' but no engine ever reads it, so 21:00 comes and goes with nothing applied or lifted
- **[daily-limit-ratchet-lived]** Hitting the daily limit mid-day does literally nothing: no code path ever compares usage to the limit
- **[break-away-from-phone]** Next block auto-starts at break expiry and burns unattended, still receipted ELAPSED_CLEAN
- **[stopwatch-vs-timed-confusion]** Stopwatch mode is entered by silently crossing dial 10, never chosen or explained — the '20m Min Floor' badge exists in the enum and is rendered nowhere
- **[false-start-grace]** Grace chip is a false promise for routine sessions: tapping it books MISSED, not a free cancel
- **[friend-holds-the-phone]** Friend can unblock websites mid-session: the site list has no ratchet, no seal gate, and no identity check
- **[change-of-plans-day]** Switching today's routine OFF mid-window does not free the day: the already-running block runs to its original stored end
- **[receipt-dismissed-forever]** Tapping the tab actually named 'Receipts' (VAULT) is itself a dismissal — and the Vault holds no receipt to land on
- **[two-phones-tablet]** ELAPSED_CLEAN certifies the person, but only ever inspected this one phone
- **[update-day]** Wiped/new-phone open is reclassified as a fresh install and the only reset notice is pre-dismissed

---

## Angle: low-tech-user (5 findings)

**Verdict:** For a user who doesn't speak Android or Monolith, the app is a machine labelled in a private dialect — settings hide behind a blank strip, the only manual is a riddle, the status line contradicts itself, and success is called a forgery — so they don't mis-tap occasionally, they never build a working model of the app at all.

### [HIGH] Settings live behind a deliberately blank, wordless strip of the nav bar — no label, no icon, nothing to discover
*Location:* `app/src/main/java/com/example/ui/views/MonolithRailView.kt:30,34,80; app/src/main/java/com/example/ui/components/ParkDrawer.kt:479,610`

The bottom rail prints exactly four words — RailGeometry.WORDS = listOf("DECK", "SHIELD", "TASKS", "LEDGER") (MonolithRailView.kt:34) — and reserves a 48dp PARK_ZONE_DP strip (line 30) that by design 'prints NO word ever, because PARK is where the machine's own state lives, not a place he goes' (line 80). That blank strip is the only door to everything a normal user calls Settings: the GUARD permission board, 'SOUND EFFECTS' (ParkDrawer.kt:479) and 'THEME' (ParkDrawer.kt:610) all live inside PARK. There is no gear icon, no 'Settings' word, nothing to tap on purpose. A low-tech user who wants to turn sound off or check a permission must accidentally swipe/tap an unlabelled empty stretch of the nav bar — most will conclude the app simply has no settings and give up.

### [HIGH] The app's only in-app 'manual' is a 10sp riddle plate — and it sends users to an undefined place called 'YPT'
*Location:* `app/src/main/java/com/example/ui/components/ParkDrawer.kt:109-123,637; app/src/main/java/com/example/ui/components/ReceiptSheet.kt:176`

The app's only attempt at explaining itself is the 'spec plate' printed at the foot of PARK in 10sp type (ParkDrawer.kt:637 renders PLATE_BODY): 'COLOUR IS A VERB: ORANGE = COMMIT · INK = SHUT · GREY = PEEK · PORCELAIN = OPEN' (line 113), 'WHEEL SETS TIME · SLIDE ARMS · THE HOLD BREAKS', 'STUDY HOURS: IN YPT' (line 122), 'SOUND: FLOAT-PCM, 4 PARTIALS · POWER: YOUR PHONE' (line 123). A repo-wide grep finds zero help, tooltip or explanation UI anywhere in the app. 'YPT' is never defined or expanded anywhere in the codebase, yet the receipt also tells the user 'THE MACHINE KEEPS ITS OWN LOG · YOUR STUDY RECORD LIVES IN YPT' (ReceiptSheet.kt:176) — directing them to a place that does not exist under any name they can find. A user who goes looking for help finds riddles instead.

### [HIGH] The main screen answers 'is blocking on?' with contradictory code pairs: 'BIND HELD · UNSEEN' and 'BIND NOT HELD' next to 'PROOF: POSITIVE'
*Location:* `app/src/main/java/com/example/ui/components/MonolithCustomComponents.kt:380-421`

seamMarksFor() prints paired status marks on the main screen (MonolithCustomComponents.kt:383-400): a healthy state reads 'BIND HELD · THIS VISIT' / 'PROOF: LIVE BIND'; a degraded one reads 'BIND HELD · UNSEEN' / 'PROOF: BIND ONLY' (line 395) — held, but unseen, but proof, but only bind; and a state where accessibility is NOT bound prints 'BIND NOT HELD' alongside 'PROOF: POSITIVE' (line 398) — 'not held' and 'positive proof' in the same breath. None of BIND, PROOF, WARRANT or SEAM is ever defined in the UI. The one plain sentence the app owns ('BLOCKING IS OFF — TAP TO FIX', line 505) only appears for the fully-off case, so in every partial state the low-tech user's only answer to 'is it working?' is a self-contradicting code pair they cannot act on.

### [MEDIUM] Same screens, three different names: rail says DECK and LEDGER, the code says Focus and Receipts — the user's map never settles
*Location:* `app/src/main/java/com/example/ui/views/MonolithRailView.kt:34; app/src/main/java/com/example/ui/MonolithViewModel.kt:49-52; app/src/main/res/values/strings.xml`

The same four destinations carry different names in different layers: the rail prints DECK / SHIELD / TASKS / LEDGER (MonolithRailView.kt:34), the ViewModel labels them 'Focus' / 'Tasks' / 'Shield' / 'Receipts' (MonolithViewModel.kt:49-52, VAULT route), and strings.xml offers a third set — tab_deck 'Focus', tab_vault 'Receipts', tab_reliability 'Guard', tab_web_studio 'Web Deck'. So the screen a user reaches by tapping LEDGER is the Receipts screen, and the home screen is called both DECK and Focus. A low-tech user builds their mental map from words; here every word changes depending on where they read it, so instructions like 'check your receipts' match nothing on the bar in front of them.

### [MEDIUM] 'FORGED — IT STANDS IN THE CABINET': earned records are labelled with the ordinary-English word for 'fake', filed under 'CURIOS'
*Location:* `app/src/main/java/com/example/ui/screens/DrawerScreen.kt:348,486-488,738`

The history/drawer screen is headed 'CABINET' (DrawerScreen.kt:348), its collectibles section is entered via a row labelled 'CURIOS' (lines 738, 776), and each earned artifact's status line prints either 'FORGED — IT STANDS IN THE CABINET' (line 486) or 'NOT YET FORGED' (line 488). In ordinary English 'forged' means fake/counterfeit and 'curios' means trinkets in a shop — nothing tells the user these are their earned records of completed focus work. The plain reading of the success state is 'your achievement is a fake standing in a cupboard'; a low-tech user is more likely to think something went wrong, or that the app is mocking them, than to feel rewarded.

---

## Angle: night-half-asleep (5 findings)

**Verdict:** Half-asleep in a dark room, the app is at its brightest, loudest and most theatrical exactly when the user is weakest: night colours are hostage to a blocking routine, ending a session cuts instantly to light, nothing ever dims the screen, and arming adds a vertigo-blur shot with full-gain sound.

### [HIGH] Night mode does not follow night: half-asleep user outside a session/SLEEP window gets the full light theme, and there is no manual override
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:70-95,212-214; app/src/main/java/com/example/ui/theme/Color.kt:11-15`

MonolithViewModel.kt:70-95 evaluateTheme() returns night only if 'sessionActive' or an enabled, unpaused SLEEP-loadout routine window is OPEN_*; otherwise 'AUTO · DAY — NO ROUTINE OWNS IT' regardless of clock time or system dark mode (no isSystemInDarkTheme anywhere). MonolithViewModel.kt:212-214: 'The manual theme is DEAD... _darkTheme and toggleTheme() are deleted with the DAY/NIGHT pill.' Seeded SLEEP is 20:00-03:30 (MonolithRepository.kt:849-850 routineTargets startMinutes=1200, endMinutes=210). So at 04:00, or any night the user paused/disabled/edited SLEEP to stop it blocking, opening the app to arm sleep protection hits the light scheme: Color.kt:11 TokenColorBackground 0xFFE4DFDA and Color.kt:15 SurfaceBright 0xFFFAF6F0 (250,246,240). Darkness is coupled to accepting a blocking routine; you cannot have a dark UI without it, or stop its blocking without losing the dark UI.

### [HIGH] Session/SLEEP ending at night floodlights the user: deliberate 0ms cut from night ground to light ground, no fade, no hold-till-morning
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:453-468,954; app/src/main/java/com/example/ui/theme/Theme.kt:108`

MonolithViewModel.kt:453-456 comment states the design: theme is 'Re-derived on a 1-second stamp so a routine boundary swaps the scheme on its frame — a 0ms cut, never a fade.' startTickerLoop re-stamps _themeNow every 1000ms (MonolithViewModel.kt:954). When a timed session finishes at e.g. 01:00 with no SLEEP window owning that moment, or the SLEEP window ends at 03:30, evaluateTheme (MonolithViewModel.kt:70-95) flips isNight false within a second and Theme.kt:108 swaps SealedVaultNightColorScheme (background 0xFF171412, Color.kt:32) for WarmTactileStudioColorScheme (background 0xFFE4DFDA, raised card 0xFFFAF6F0, Color.kt:11,15) instantaneously, in a dark room, at the exact moment the user is dozing off or being released to sleep.

### [HIGH] No brightness is ever lowered anywhere — the always-on nightstand clock runs at full daytime brightness in full-brightness cream
*Location:* `app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:84-98,138-192; app/src/main/java/com/example/ui/theme/Color.kt:37`

Repo-wide grep for screenBrightness/BRIGHTNESS_OVERRIDE finds zero hits; the only window flag in the app is FLAG_KEEP_SCREEN_ON in the desk clock (AirlockAndDisarmOverlays.kt:96-98). That clock keeps the screen on all night on a true-black ground (AirlockAndDisarmOverlays.kt:138-141 background Color.Black) but renders its 96sp time in NightColorPrimary at full alpha (AirlockAndDisarmOverlays.kt:165-172, color NightColorPrimary = 0xFFFFE4CF cream, Color.kt:37) plus a full-width cream progress bar (AirlockAndDisarmOverlays.kt:175-192 background NightColorPrimary) — all at whatever daytime system brightness the phone was left on. 'Nightstand' means kept-on, not dimmed: it lights the bedside and can keep the user/partner awake instead of receding.

### [MEDIUM] Arming in bed triggers a 1.05s vertigo-and-blur 'seal shot' — the room recedes and blurs while the theme flips underneath it
*Location:* `app/src/main/java/com/example/ui/screens/TactileDeckScreen.kt:240-262,584-597; app/src/main/java/com/example/MainActivity.kt:107-115`

TactileDeckScreen.kt:240-262: on every live arm the Deck runs a 1050ms shot driving vertigoT and blurT every frame; TactileDeckScreen.kt:584-597 applies it to the hero card: scale 1-0.06*vt, translationY -10dp*vt, and Modifier.blur up to (6f*4f*blurT*(1-blurT)) = 6dp peaking mid-shot, while MainActivity.kt:107-115/256-261 steps the ground/scheme to night at t=400ms mid-animation. For a drowsy user in bed this is a deliberate disorientation effect plus a colour flip applied to blurred text they are trying to read, ending in the seal sound — spectacle and motion at the lowest-willpower, most motion-sensitive moment, with no reduced-motion or night exemption in the code.

### [MEDIUM] No night quieting for sound: every bedtime interaction plays full-gain clicks/knocks/seal at default-ON, ignoring hour and DND
*Location:* `app/src/main/java/com/example/service/SoundEffectsSetting.kt:34-38; app/src/main/java/com/example/service/TactileSoundEngine.kt:629-638,707-724,817-842; app/src/main/java/com/example/ui/MonolithViewModel.kt:1799,2010`

SoundEffectsSetting.kt:34-38: sound defaults ON on fresh install ('Default ON — the shipped behaviour'). TactileSoundEngine.kt:387-390,424-427 uses USAGE_ASSISTANCE_SONIFICATION with fixed gains (playHarmonicSeal gain 0.62 at :707-724, playCompletionPulse/playTearPulse gain 0.68 at :817-842, playRotaryDetent gain 0.38 at :629-638); no code checks hour, themeEvaluation.isNight, interruption filter/DND or ringer mode (grep for interruptionFilter/doNotDisturb finds none). MonolithViewModel.kt fires these on the bedtime path itself — rotary detents while dragging the wheel (:2010, :2088), seat knocks and playHarmonicSeal on arm (:1799, :1968, :2002). The only off switch is the single global PARK toggle, so half-asleep arming in a shared bed audibly clicks, knocks and seals at daytime volume unless the user pre-meditatedly muted the whole app.

---

## Angle: one-hand-commute (5 findings)

**Verdict:** One-handed on a moving bus this app is unusable as designed and unsafe in a pocket: the dial sits a re-grip away from the seal, a jolt can rewrite the duration or seat the rail in PARK, and a 90ms fabric press can arm a session — or, once running, abort it or burn the weekly emergency break.

### [CRITICAL] Pocket-arming: a 90ms pocket/bag press on the slide cap is a full order to seal a session
*Location:* `app/src/main/java/com/example/ui/views/BlockitCoreViews.kt:1327,1889,2047,2132,2338,2407`

SlideToStartView's own words are 'SLIDE OR TAP TO START' (BlockitCoreViews.kt:2407). The only accidental-contact guard is SlideGestureMath.isBrush: a press under 90ms is a brush (BlockitCoreViews.kt:1327,1356). endDrag classifies any on-cap press >=90ms with <8dp movement as a tap (BlockitCoreViews.kt:2047-2059) and a tap starts startMesh to 0.92, which hands to startSeat and latchFrame fires onSlideCommitted on a LIVE read (BlockitCoreViews.kt:2132-2142,2338-2358). beginAttempt's hit test is the cap inflated 8dp in x at full track height (BlockitCoreViews.kt:1889-1935). No proximity, pocket, lock-screen or screen-state check exists anywhere in the app (grep for pocket/proximity/WAKE_LOCK across app/src/main returns nothing). A thigh or bag-lining press lasting a tenth of a second — the normal duration of fabric contact — therefore seals a blocking session the user never chose, and after the 10s grace window the only exit is the weekly emergency valve.

### [HIGH] Dial is absolute-jump plus fling physics: one jolted touch rewrites the session length
*Location:* `app/src/main/java/com/example/ui/views/BlockitCoreViews.kt:1051,1114,1131,1194,1270`

RotaryWheelView calls updateMinutesFromTouch(dx,dy) on ACTION_DOWN itself, before any drag is confirmed (BlockitCoreViews.kt:1051), and updateMinutesFromTouch sets wheelAngleDeg from the touched point's absolute atan2 angle and immediately fires onMinutesChanged via applyValueFromAngle (BlockitCoreViews.kt:1131-1177) — first contact on the rim's far side teleports the dial (e.g. 50 to 180, or into the FLOW clamp at 108/432, BlockitCoreViews.kt:1138-1144). On release the VelocityTracker omega is accepted up to ±1200 deg/s (BlockitCoreViews.kt:1114-1118) and startPhysics/stepPhysics coast it for up to 1,400ms before a well captures it (BlockitCoreViews.kt:1194-1210,1270-1285). A bus lurch that jerks the thumb at release is indistinguishable from a deliberate fling: the wheel spins, lands somewhere else, and the next slide seals that wrong duration with no confirmation or undo of the dial value.

### [HIGH] Dial at the top, seal at the bottom: arming one-handed demands two full re-grips on a moving vehicle
*Location:* `app/src/main/java/com/example/ui/screens/TactileDeckScreen.kt:683,960; app/src/main/java/com/example/ui/views/BlockitCoreViews.kt:1336`

The Deck puts its two halves of one action at opposite ends of the phone: the 288dp RotaryWheelView sits at the top of the scroll column inside the first hero card, below the header, eyebrow and 72sp hero readout (TactileDeckScreen.kt:683-705), while the SlideToStartView is pinned in a separate column under the scroll area at the bottom (TactileDeckScreen.kt:960-1025, height 72dp), with the 52dp rail below that again (MainActivity.kt:699-735). There is no stepper, preset row or second dial near the thumb — the only preset is the FLOW pill directly under the wheel (TactileDeckScreen.kt:707-735). Even the slide's drag path is sized for a free hand: SlideGestureMath.gear puts the commit catch (displayed 0.84) at finger fraction p=0.92 of a travel that is ~290dp on a 390dp-wide screen (BlockitCoreViews.kt:1336-1340,1733-1742) — a ~265dp single thumb sweep. Setting a time then sealing it one-handed means a full-height re-grip of a phone the other hand cannot steady, twice per arm.

### [MEDIUM] Rail converts a jolted tap into a drag at 8dp and can seat it in PARK, opening a sheet the user never asked for
*Location:* `app/src/main/java/com/example/ui/views/MonolithRailView.kt:65,323,347,380`

MonolithRailView turns a tap into a drag at abs(dx) > dp(8f) of horizontal movement (MonolithRailView.kt:323) — one firm bus jolt through the thumb. Once dragging, ACTION_UP commits RailGeometry.nearestStop(capPos) rather than the stop the finger is over (MonolithRailView.kt:347-351), and nearestStop includes PARK_INDEX (MonolithRailView.kt:70), so a jolted tap aimed at LEDGER (segment ~73.5dp at the reference 390dp width, RailGeometry at MonolithRailView.kt:18-19,28-36) can seat into the 48dp unlabelled PARK zone at the track's right end and fire onParkInvoked, opening the PARK sheet (MonolithRailView.kt:380-395). PARK is also the farthest point on the band from a left-side grip and prints no word ever (MonolithRailView.kt:78-84), so the easiest stop to hit by accident is the hardest to aim for deliberately one-handed.

### [MEDIUM] While running, the pocket zone becomes a one-tap abort/valve opener, and the 60s valve hold is position-blind
*Location:* `app/src/main/java/com/example/ui/screens/TactileDeckScreen.kt:1032,1047; app/src/main/java/com/example/ui/views/BlockitSuitcaseAndSwitchesViews.kt:605,718`

The pinned bottom zone changes tenant when a session starts: for the first 10 seconds it is a single-tap 'GRACE WINDOW ABORT' chip wired straight to onCancelWithinGraceWindow, and after that a single-tap 'EMERGENCY BREAK VALVE (60S HOLD)' chip that opens the disarm overlay (TactileDeckScreen.kt:1025-1058) — the exact spot a pocket or a steadying thumb already rests on from arming. The valve itself never verifies the finger stays on it: Emergency60sValveView.onTouchEvent handles only ACTION_DOWN (starts the hold timer) and ACTION_UP/CANCEL (stops it); there is no MOVE/position branch at all (BlockitSuitcaseAndSwitchesViews.kt:718-758), and holdTickRunnable fires onEmergencyBreakCompleted purely when heldMs reaches totalMs (BlockitSuitcaseAndSwitchesViews.kt:605-635). A sustained pocket press therefore runs the full 60s and spends the week's only emergency break, while the mirror-image failure also holds on a bus: any jolt that lifts the thumb fires ACTION_UP and the drainRunnable erases everything unbanked after an 800ms grace at up to 2x speed (BlockitSuitcaseAndSwitchesViews.kt:640-665,770-774).

---

## Angle: urgent-contact-mid-session (5 findings)

**Verdict:** Mid-session, real life gets no identity: a live voice call rings through, but every urgent text is suppressed by package alone with no per-person breakthrough, cannot even be triaged from the frozen digest, and answering it via a LOCKED app (all messaging under SLEEP) costs a 60-second hold, the week's only emergency token, and the entire session.

### [CRITICAL] No person can break through: notification suppression is package-only, sender/priority-blind — urgent text is treated exactly like spam
*Location:* `app/src/main/java/com/example/service/MonolithNotificationListener.kt:58-95`

MonolithNotificationListener.kt:58-63 exempts only 'notification.category == Notification.CATEGORY_CALL || CATEGORY_ALARM'; every other notification is decided purely by package at :76-95 — 'if (appRule != null && appRule.resolvedTier != AppBlockTier.OPEN) { insertCapturedNotification(...); cancelNotification(sbn.key) }'. senderTitle is extracted at :68-69 and stored only as a display field (:81), never consulted. The tier system itself is per-app only (MonolithModels.kt:15-38: LOCKED / PEEK_2M '2-minute check-in for urgent messages' / OPEN), and no contact, sender, priority or category-MESSAGE check exists anywhere in the codebase. A 'family emergency' WhatsApp/SMS text and a group-chat meme from the same app are cancelled and buried identically.

### [HIGH] Urgent contact via a LOCKED app costs the whole session + the week's only emergency token + a 60s hold — and SLEEP loadout locks all messaging
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:142-152; app/src/main/java/com/example/data/MonolithRepository.kt:1151-1160,2225-2250`

peekOfferFor requires the tier: MonolithViewModel.kt:142-152 'entity.resolvedTier == AppBlockTier.PEEK_2M && cooldownLeftSeconds == 0 && !pillActive', and for a LOCKED app the withheld reason is suppressed entirely (:2338 'appEntity?.resolvedTier != AppBlockTier.PEEK_2M -> null'), so the airlock does not even say no peek exists. The only remaining door is the valve, whose own overlay states the price: AirlockAndDisarmOverlays.kt:769-775 'This ends the session early. It is the only break this week.' completeEmergencyBreak (MonolithRepository.kt:2225-2250) inserts the week's one spend — remaining is '(1 - spendCount)' (MonolithWeek.kt:41-42) — and disarms the session as VALVE_BROKE, after a 60-second hold. Worse, the SLEEP loadout re-tiers everything except dialer/phone/clock/maps to LOCKED (MonolithRepository.kt:1151-1160), so at night WhatsApp/messaging lose the peek they normally have and an urgent night message costs the whole session plus the week's only token.

### [HIGH] Mid-session triage is impossible: the held-notification digest is a frozen snapshot — anything newer is invisible until the session ends
*Location:* `app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:391-398; app/src/main/java/com/example/MainActivity.kt:752-767`

The digest's frozen face states it in the app's own words: AirlockAndDisarmOverlays.kt:391-398 'Everything here is as of the time above. Nothing here opens. Anything newer waits until the session ends.' MainActivity.kt:752-767 passes the frozen snapshot (or an empty list) whenever 'frozenDigest != null || sealed', never the live flow, and MonolithViewModel.kt:100-110 documents the FrozenDigest as 'the held notifications as they stood at one instant... never the live flow'. Rows carry no open/tap action in either mode (AirlockAndDisarmOverlays.kt:405-445: only DEL in live mode, nothing in frozen mode). The user cannot glance at sender/body to judge urgency without ending the session — the one triage surface is deliberately blind to anything that arrived after the freeze.

### [MEDIUM] One peek per app per ~22 minutes: the 20m cooldown is armed at peek start, so follow-up messages in the same unfolding emergency cannot be checked
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:106-108,1194-1203`

Constants: MonolithRepository.kt:106-108 'PEEK_DURATION_SECONDS = 120', 'PEEK_COOLDOWN_MILLIS = 20 * 60 * 1000L'. startPeekLease (:1194-1203) sets both activeUntil and peekEnd to lease end, i.e. the 20-minute cooldown is pre-armed the moment the peek starts, and getPeekCooldownRemainingSeconds (:1177-1183) anchors on that end. The peek is only offered after the airlock's 5s countdown reaches zero (AirlockAndDisarmOverlays.kt:663-685, with the printed price 'TAKING THE PEEK STARTS A 20 MINUTES COOLDOWN ON THIS APP'). So a first peek to read 'come to hospital' burns the app for ~20 minutes; the follow-up with the address/room, arriving minutes later, hits 'PEEK COOLDOWN — AGAIN IN X MIN' (:691-698) and the only remaining door is the session-killing valve.

### [MEDIUM] No on-demand pause for real life: Safe Break (the one state where messaging unlocks) is timer-scheduled at block boundaries and can never be summoned
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:2020-2040; app/src/main/java/com/example/ui/MonolithViewModel.kt:1451-1455`

Safe Break entry exists only inside updateSessionProgress at a block boundary: MonolithRepository.kt:2020-2040 'if (blockFinished && session.currentBlockIndex < session.totalStudyBlocks) { session.copy(isInSafeBreak = true, safeBreakSecondsRemaining = SAFE_BREAK_DURATION_SECONDS...) }' — a session on its last/single block never enters one at all. The ViewModel exposes only the exit verb, MonolithViewModel.kt:1451-1455 'fun endSafeBreakEarly()', and no start/summon verb exists in the UI layer. During a Safe Break PEEK apps unlock freely (MonolithAccessibilityService.kt:278-280, MonolithShieldService.kt:170), which would be the proportionate answer to a delivery at the door — but the user cannot ask for it when real life arrives; they must wait for the timer or kill the session via the valve.

---

## Angle: real-emergency-exit (5 findings)

**Verdict:** In a real mid-session emergency there is effectively no emergency lane: at best a 65-second-plus, Deck-only, one-finger 60s hold that burns the week's single token and kills the session — and if that token is already spent, or the needed app is Camera/Waze under SLEEP, or the dialler itself was locked, the lawful exit count is zero, so a panicking user will abandon the app's rules entirely (force-stop/reboot) or simply not get the help, photo or directions they needed in time.

### [CRITICAL] Second emergency in the same week has no exit at all: one token per week, and at zero the valve is not even rendered
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:2225-2234; app/src/main/java/com/example/data/MonolithWeek.kt:41-42; app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:772-806`

MonolithWeek.kt:41-42: remaining = (1 - spendCount).coerceAtLeast(0), week anchored Monday 00:00 local (MonolithWeek.kt weekAnchorMillis). MonolithRepository.kt:2225-2234 completeEmergencyBreak() returns BreakResult.RefusedNoToken when spends >= 1. AirlockAndDisarmOverlays.kt:772-806: at tokensRemaining == 0 the valve is 'simply not composed', body reads "This week's break is already spent." and the only chip rendered is KEEP FOCUSING. No override, PIN, emergency-services carve-out or second lane exists anywhere in the overlay or repository. A token spent Monday on anything leaves a Friday-night emergency with literally zero lawful exits until the session ends on its own or Monday arrives.

### [CRITICAL] The emergency exit is not where the emergency happens: the Airlock for the blocked app you need has no valve, so the lawful path is a 65s+ multi-screen detour ending in a 60-second continuous finger hold
*Location:* `app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:647-715; app/src/main/java/com/example/ui/screens/TactileDeckScreen.kt:1045-1055; app/src/main/java/com/example/ui/views/BlockitSuitcaseAndSwitchesViews.kt:626-650`

AirlockAndDisarmOverlays.kt:647-715: the intercept offers only RETURN TO FOCUS and, conditionally, EMERGENCY PEEK — no valve, no disarm, no dialler shortcut. MonolithViewModel.kt:2445-2460 transmuteImpulseAndReturn() only hides the airlock. The valve chip exists only on the Deck's pinned stack (TactileDeckScreen.kt:1045-1055 'EMERGENCY BREAK VALVE (60S HOLD)'), opening DisarmChallengeOverlay, whose valve then requires a full 60s hold (MonolithRepository.kt:121 EMERGENCY_BREAK_HELD_SECONDS = 60; BlockitSuitcaseAndSwitchesViews.kt:532-618). A slip drains the un-banked remainder after 800ms grace at 1x then 2x (BlockitSuitcaseAndSwitchesViews.kt:626-650, 716-755). Best case from the moment of need: dismiss airlock, reach Deck, tap chip, hold one finger perfectly for 60s — 65s+ of calm, two-handed-phone operation during the exact minute the user is panicking, injured, holding a child, or trying to direct help.

### [HIGH] No temporary emergency suspend exists: the valve destroys the whole session, and the only short lane (2-minute peek) is denied outright to LOCKED apps
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:2225-2250; app/src/main/java/com/example/ui/MonolithViewModel.kt:141-151,2335-2338`

completeEmergencyBreak() (MonolithRepository.kt:2225-2250) spends the token and disarms/ends the session in the same transaction — there is no pause/suspend-and-resume verb. The only temporary lane is the peek: peekOfferFor (MonolithViewModel.kt:141-151) requires resolvedTier == PEEK_2M, no cooldown, no active pill; duration 120s and 20-minute cooldown (MonolithRepository.kt:106-108). AirlockAndDisarmOverlays.kt:663-670 shows the peek chip only after the 5s countdown hits 0. For a LOCKED-tier app the withheld reason is forced to null when tier != PEEK_2M (MonolithViewModel.kt:2335-2338), so the door prints nothing at all. Needing Maps for a 15-minute evacuation or Camera for 5 minutes costs either the whole session plus the week's only token, or nothing is offered at all.

### [HIGH] SLEEP 'Full Lockdown' whitelist is five substrings, so Camera and Waze are locked in the very loadout used overnight
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:1150-1160; app/src/main/java/com/example/data/MonolithModels.kt:54-58`

MonolithRepository.kt:1150-1160 applySmartLoadout(SLEEP): OPEN only if the lowercased 'packageName + appName' contains dialer/phone/clock/alarm/maps, else LOCKED. MonolithModels.kt:54-58 sells SLEEP as 'Blocks every distracting app except Phone, Clock, and Maps'. A stock Camera ('Camera', com.android.camera2 / com.google.android.GoogleCamera) contains none of the five substrings and is LOCKED — no photos of damage/injuries, no document/QR scanning. Waze ('Waze', com.waze) likewise contains no 'maps' and is LOCKED despite being a navigation app. In the overnight loadout — when house fires, break-ins and medical runs actually happen — two of the four tools named in this audit's brief are shut by a substring accident, reachable only via the 60s weekly valve.

### [HIGH] Help can reach you but you cannot reach help: call bypass only fires once a call is already ringing, and no guard stops the dialler itself being set to LOCKED
*Location:* `app/src/main/java/com/example/service/MonolithAccessibilityService.kt:189-190,297-305; app/src/main/java/com/example/data/MonolithRepository.kt:1105-1119`

Every call protection is inbound-only: MonolithAccessibilityService.kt:189-190 returns early only if isCallActive() (AudioManager MODE_RINGTONE/MODE_IN_CALL/MODE_IN_COMMUNICATION, :297-305) — i.e. a call already ringing or live; MonolithNotificationListener.kt:59 exempts CATEGORY_CALL notifications — again, someone calling you. Placing a call requires first opening the dialler through the tier system, and nothing protects the dialler's tier: setAppBlockTier (MonolithRepository.kt:1116-1119) and cycleAppBlockTier (:1105-1113) accept any package at any tier with no essential-app floor or warning, and the Shield matrix lets Phone/Camera/Maps be tapped to LOCKED like any game. A user who locked Phone in a strict moment — or whose loadout did — must clear the Airlock/valve gauntlet before they can even dial for help.

---

## Angle: otp-banking-mid-session (5 findings)

**Verdict:** Mid-session, a time-limited OTP or UPI approval is a race the user usually loses: the notification is cancelled or frozen out of view, its approval action is destroyed, and a LOCKED bank app offers no peek — only ending the session.

### [CRITICAL] OTP and UPI-approval notifications get no exemption — the original notification and its Approve action are cancelled, only title/text survive
*Location:* `MonolithNotificationListener.kt:59-94; MonolithModels.kt:463-471`

MonolithNotificationListener.kt:59-63 returns early only for CATEGORY_CALL/CATEGORY_ALARM — no CATEGORY_MESSAGE, OTP, or finance exemption. MonolithNotificationListener.kt:75-94: any non-OPEN sender is copied then cancelNotification(sbn.key) destroys the original. MonolithModels.kt:463-471 CapturedNotificationEntity stores only packageName/appName/senderTitle/messageBody — no PendingIntent/action — so a GPay/PhonePe/bank Approve/Reject action is gone, not held. WhatsApp is born PEEK_2M (AppCatalogReconcile.kt:59), so a WhatsApp-delivered OTP is in the held set.

### [CRITICAL] Sealed digest is a frozen snapshot: an OTP arriving after you open HELD is not shown at all until the session ends
*Location:* `MainActivity.kt:755-764; MonolithViewModel.kt:901-934; AirlockAndDisarmOverlays.kt:297-390`

MainActivity.kt:755 digestFrozen = frozenDigest != null || sealed (sealed = session active), and :757-764 passes frozenDigest items or emptyList(), never the live list while sealed. AirlockAndDisarmOverlays.kt CapturedNotificationsDigestSheet frozen text: 'Everything here is as of the time above. Nothing here opens. Anything newer waits until the session ends.' MonolithViewModel.kt:901-934 freeze is minted only when the sheet is visible for that session id. An OTP arriving after the user checks the digest is therefore invisible until a 25-90m session ends, long past a 30s-5m code window.

### [HIGH] A LOCKED banking/UPI app has no peek at all — the only way in is ending the session via the once-a-week 60-second emergency valve
*Location:* `MonolithViewModel.kt:145-152; MonolithRepository.kt:1153-1160; AirlockAndDisarmOverlays.kt:660-685`

MonolithViewModel.kt peekOfferFor (~:145-152): canUse requires resolvedTier == PEEK_2M. Banking/UPI apps fall to SHOPPING (MonolithRepository.kt:514-530 else -> SHOPPING) and Sleep loadout locks them: MonolithRepository.kt:1153-1160 only dialer/phone/clock/alarm/maps stay OPEN, else LOCKED; Sleep profile CSV includes SHOPPING (MonolithRepository.kt:~310). AirlockAndDisarmOverlays.kt:660-685 offers EMERGENCY PEEK only if canUse2MinutePeek, otherwise no peek line. The only other door is DisarmChallengeOverlay: 'This ends the session early. It is the only break this week.' plus a 60s hold valve — slower than the OTP window and burns the weekly break to approve one payment.

### [HIGH] 30-second TOTP/second-OTP loses to the 5s airlock wall plus 20-minute per-app peek cooldown
*Location:* `MonolithRepository.kt:106-108; AirlockAndDisarmOverlays.kt:550-575,660-705`

MonolithRepository.kt:106-108 PEEK_DURATION_SECONDS=120, PEEK_COOLDOWN_MILLIS=20*60*1000. AirlockAndDisarmOverlays.kt:550-575,660: peek chip appears only when countdownSecs==0 after a fixed 5s wall. AirlockAndDisarmOverlays.kt:233-235 peekCooldownPrint: 'PEEK COOLDOWN — AGAIN IN $minutes MIN'. A TOTP rotates every 30s and a second login/payment OTP in the same session re-hits the same Messages/Authenticator/WhatsApp package already on cooldown — the app designed the peek for 'urgent messages' (MonolithModels.kt:28) but rations it to once per 20 minutes per app.

### [MEDIUM] No finance category exists: banks/UPI/authenticators are misfiled as SHOPPING and bulk-locked by the Sleep profile
*Location:* `MonolithModels.kt:222-229; MonolithRepository.kt:514-530,1247-1256`

MonolithModels.kt:222-229 AppCategory has only SHORT_VIDEO, SOCIAL, GAMES, MESSAGING, BROWSERS, SHOPPING — no FINANCE/ESSENTIALS category exists to keep-open as a class. MonolithRepository.kt:514-530 categorizePackage: no bank/authenticator/UPI keyword, so they fall through else -> SHOPPING ('Other Apps'). MonolithRepository.kt:1247-1256 applyProfile: any blocked category except MESSAGING becomes LOCKED, so applying Sleep Loadout (blockedCategoriesCsv includes SHOPPING) silently bulk-locks every bank, UPI and authenticator app the user never individually reviewed.

---

## Angle: alarm-fires-mid-session (3 findings)

**Verdict:** A stock Clock alarm under default tiers breaks through and the session survives it intact, but the protection is accidental, not designed: the alarm exemption lives only in the notification listener, neither blocking door can even detect a ringing alarm, the SLEEP routines' substring test decides overnight whether your alarm app exists at all, and calendar reminders — the other time-critical alert — are silently held and cancelled.

### [HIGH] Alarm is exempt in only one of three doors: its notification is spared, its ringing screen is intercepted and covered by the Airlock
*Location:* `app/src/main/java/com/example/service/MonolithNotificationListener.kt:59-62; app/src/main/java/com/example/service/MonolithAccessibilityService.kt:189-190,273-303; app/src/main/java/com/example/service/MonolithShieldService.kt:180-205; app/src/main/java/com/example/data/MonolithRepository.kt:549-567`

The exemption exists in exactly one of three doors. MonolithNotificationListener.kt:59-62 spares the alarm's notification: 'if (notification.category == Notification.CATEGORY_CALL || notification.category == Notification.CATEGORY_ALARM) return'. But neither enforcer has any alarm bypass: MonolithAccessibilityService.kt:189-190 checks only isCallActive(), defined at :297-303 as AudioManager modes MODE_RINGTONE/MODE_IN_CALL/MODE_IN_COMMUNICATION — a ringing alarm runs on the alarm stream and sets none of those modes, so the bypass structurally cannot fire for it. It then falls through to the per-app tier branch (:273-295) and launchInterceptScreen()s over the alarm screen. MonolithShieldService.kt:180-205 does the same via its 2s polling: the Clock package stays in blockedPackages and its alarm activity is intercepted via UsageStats. Repository.kt:549-567 duplicates the same call-only bypass for the shield door. A repo-wide grep finds zero deskclock/clock-package/calendar package exemptions in either service. Lived: if the Clock app is LOCKED (or PEEK_2M with no live lease), the alarm screen is covered by the Airlock's 5-second wall (AirlockAndDisarmOverlays.kt, countdown remember at intercept) with only RETURN TO FOCUS / peek offers — no snooze, no dismiss — while the alarm keeps sounding, because the one door that spared it (the listener) is the one door that doesn't control the screen. Each retry to reach snooze re-intercepts.

### [HIGH] SLEEP loadout's only alarm safeguard is a name substring: an alarm app branded without 'clock'/'alarm' is auto-LOCKED all night by the seeded sleep routines
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:849-850,887-893,1083-1089,1116-1119,1152-1160`

SLEEP is the loadout of the seeded, enabled overnight routines (MonolithRepository.kt:849-850 'SLEEP', 20:00-03:30; :887-893 'Night Wind-Down' 22:30-07:00), and routine sessions apply their loadout automatically with no user tap: materialiseRoutineSession() at :1083-1089 calls applySmartLoadout(loadout) before startSession. That loadout's entire protection for the morning alarm is a substring test at :1152-1160: OPEN only if package+label contains 'dialer', 'phone', 'clock', 'alarm' or 'maps', else LOCKED. Google/Samsung Clock survive only because their branding happens to contain 'clock'. Any alarm app whose package and label contain none of the five substrings (e.g. sleep-cycle-style alarm apps branded around 'sleep') is silently LOCKED for the whole overnight window — precisely when its alarm must fire — and per the accessibility/shield doors above, its firing screen is then intercepted. Nothing in Shield Matrix or the routine editor warns that the SLEEP loadout has reclassified the user's alarm app; setAppBlockTier (Repository.kt:1116-1119) and the loadout writer apply any tier to any package with no system-critical guard.

### [MEDIUM] Calendar reminders get no alarm-style exemption: under the auto-applied SLEEP loadout they are captured, cancelled and unopenable until the session ends
*Location:* `app/src/main/java/com/example/service/MonolithNotificationListener.kt:59-62,72-100; app/src/main/java/com/example/data/MonolithRepository.kt:1083-1089,1152-1160; app/src/main/java/com/example/MainActivity.kt:756-770; app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:353-360`

MonolithNotificationListener.kt:59-62 spares only CATEGORY_CALL and CATEGORY_ALARM; every other notification from a non-OPEN app during an active session is stored and then cancelled at :72-100 ('dao.insertCapturedNotification(...)' followed by 'cancelNotification(sbn.key)'). Android calendar reminders do not carry CATEGORY_ALARM, so they get no exemption. Calendar is LOCKED automatically overnight: under the SLEEP loadout (Repository.kt:1152-1160) 'calendar' matches none of dialer/phone/clock/alarm/maps, so it falls to LOCKED, and SLEEP routines materialise on their own (:1083-1089). The held reminder is also unreachable mid-session: MainActivity.kt:756-770 forces the digest frozen whenever sealed (frozen snapshot or empty list), and the frozen sheet itself says at AirlockAndDisarmOverlays.kt:353-360 'Everything here is as of the time above. Nothing here opens. Anything newer waits until the session ends.' Lived: a 06:45 'leave for the airport' reminder during the 22:30-07:00 Night Wind-Down session is silently cancelled on arrival and cannot be seen or opened until the session ends — the exact class of time-critical alert the alarm exemption was built to protect, denied protection because of its category.

---

## Angle: low-battery-mid-session (4 findings)

**Verdict:** At 8% the app is battery-blind: it never senses level, saver state or charging, its one BATTERY verdict measures a setup exemption instead, its desk clock can hasten the death, and when the phone dies the session simply freezes and re-locks the user on recharge — protection ends with the phone, silently.

### [HIGH] No low-battery or power-save sensing at all: at 8% with saver on, the shield keeps promising protection with zero warning that it dies with the phone
*Location:* `app/src/main/AndroidManifest.xml:100-105; app/src/main/java/com/example/service/MonolithShieldService.kt:62-65,158-161; app/src/main/java/com/example/data/MonolithRepository.kt:659-663`

A grep of all main sources finds zero occurrences of isPowerSaveMode, ACTION_BATTERY_LOW, ACTION_POWER_SAVE_MODE_CHANGED, BATTERY_CHANGED or any registerReceiver call. The manifest's only receiver intent-filters are LOCKED_BOOT_COMPLETED/BOOT_COMPLETED/MY_PACKAGE_REPLACED/TIME_SET/TIMEZONE_CHANGED (app/src/main/AndroidManifest.xml:100-105). The only PowerManager read in the app is the setup-time exemption check (MonolithRepository.kt:659-663). So when Android's saver engages the app changes nothing, says nothing, and its shield notification goes on printing 'Monolith Locked' / 'Protecting your focus session...' (MonolithShieldService.kt:62-65) and then only a session countdown plus 'urges stopped' (MonolithShieldService.kt:158-161) — a timer for the session, never for the battery that session depends on. The user is never told 'plug in now or this ends when the phone does', and plugging in is likewise never noticed.

### [HIGH] The only battery verdict the user can check (PARK's BATTERY line) measures setup exemption, not level or saver state — it can read satisfied at 8% with saver on
*Location:* `app/src/main/java/com/example/service/WatchStore.kt:86,95; app/src/main/java/com/example/data/MonolithRepository.kt:652,659-663; app/src/main/java/com/example/MainActivity.kt:392-403`

The board's BATTERY item (WatchStore.kt:86, listed in the permissions family at WatchStore.kt:95) is answered solely by watchGrantQuery: WatchItemId.BATTERY -> isBatteryOptimizationIgnored() (MonolithRepository.kt:652), i.e. pm.isIgnoringBatteryOptimizations(packageName) (MonolithRepository.kt:663). That is a Doze/standby exemption granted at setup; it reads neither battery level nor PowerManager.isPowerSaveMode (never called anywhere). Its FIX door likewise only opens the battery-optimization settings (MainActivity.kt:392-403). A user who did setup correctly therefore sees BATTERY satisfied while sitting at 8% with Battery Saver actively on — the one battery line the app gives them answers a question nobody at 8% is asking, and gives no hint saver state was never checked.

### [MEDIUM] Mid-session desk clock holds the screen on with no battery cutoff: at 8% the app's own always-on display accelerates the death it never warns about
*Location:* `app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:83-99`

The desk-clock overlay's DisposableEffect unconditionally does window?.addFlags(FLAG_KEEP_SCREEN_ON) when shown and clears it only on dispose (AirlockAndDisarmOverlays.kt:83-99, flag at :96-98); the overlay contains no battery, level or saver read of any kind, and no timeout. It is offered from the running session's utilities row, so the exact user at 8% who props the phone up as their session clock gets an OLED held on indefinitely — the fastest available route from 8% to a dead phone — with no dimming, no auto-exit at a low threshold, and no warning, from the app whose protection ends the moment the phone does.

### [MEDIUM] Battery death is not an ending: the session clock freezes while the phone is dead and the shield re-arms on recharge, re-locking the user hours later
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:750-756,1951-1955; app/src/main/java/com/example/service/MonolithBootReceiver.kt:37-47`

computeVerifiedSessionElapsed's reboot branch returns priorElapsed + (nowMono/1000) — accumulated seconds plus time since this boot only, discarding the wall-clock hours the phone sat dead (MonolithRepository.kt:750-756, doc at :731-737). updateSessionProgress re-anchors on that basis (MonolithRepository.kt:1951-1955). The session row is never expired by downtime, and on the next boot MonolithBootReceiver runs a forced heartbeat and, 'raise the shield only if one remains active afterwards', calls MonolithShieldService.startShield for the still-active row (MonolithBootReceiver.kt:37-47). So a user who lets the phone die at minute 20 of 50, charges it hours later expecting the session to be long over, is instead re-locked for the remaining ~30 minutes on first boot — death bought them no time at all, and this is never explained anywhere.

---

## Angle: dial-precision (5 findings)

**Verdict:** The dial does not respect a precise intention: it forces 5-minute round numbers, hides that its number is per-block while the real total can be more than double, overshoots twice on release, and double-sounds every detent — so setting an exact duration by feel is guesswork the user only discovers from the hero total.

### [HIGH] 47 minutes is impossible: the dial only stops on multiples of 5, with no precise alternative
*Location:* `BlockitCoreViews.kt:524-530,742-749; TactileDeckScreen.kt:685-723`

valueForAngle() quantises every angle: '(10 + ((angle - 126f) / 9f).roundToInt() * 5).coerceIn(10, 180)' (BlockitCoreViews.kt:524-530), and the drawn scale agrees: 'ticks exist ONLY where the wheel can actually stop — every 5 minutes from 10 to 180, 35 stops' (BlockitCoreViews.kt:742-749). No other dial input exists — no stepper, typed entry or fine mode anywhere in TactileDeckScreen.kt; the only other setter is the FLOW pill forcing 5 (TactileDeckScreen.kt:723). A user who wants 47, 52 or 1h07 gets 45, 50 or 65/70 and must accept the app's number, not theirs.

### [HIGH] Dial reads per-block, hero reads total, and an accidental hub tap silently adds ~60 minutes via invisible study-blocks
*Location:* `MonolithViewModel.kt:220; TactileDeckScreen.kt:325,632-649,698-699; BlockitCoreViews.kt:794,915-927`

Default is already 2 blocks (MonolithViewModel.kt:220), totalSpanMinutes = dialMinutes * studyBlocksCount + (studyBlocksCount - 1) * 10 (TactileDeckScreen.kt:325), so the default 50 on the rim is really 110 minutes. The code admits the split read: rim numerals state 'the block length; the hero states the total' (BlockitCoreViews.kt:794). The block count itself has no numeral or word anywhere: 'the count lives only as hub geometry - no numeral, no word... the object count by count runs 1,2,3,1,2,3,4,2 - never above four' (BlockitCoreViews.kt:915-927). A stray centre-hub tap/release cycles 2->3 (TactileDeckScreen.kt:698-699, BlockitCoreViews.kt:1085-1097) and silently adds a full block plus a 10-minute break (+60 min at 50) to the session, signalled only by one extra well lighting up.

### [MEDIUM] Fumbling past a stop is punished: no boundary hysteresis, and release triggers a two-overshoot coast that re-seats elsewhere
*Location:* `BlockitCoreViews.kt:1135-1148,1194-1204,1215-1289`

Drag is raw 1:1 with no hysteresis: updateMinutesFromTouch() sets wheelAngleDeg directly from atan2 and applyValueFromAngle() flips the value the instant roundToInt() crosses the 4.5-degree midpoint between stops (BlockitCoreViews.kt:1135-1148,524-530) — a finger trembling at the 45/50 boundary toggles the value and re-fires a tick on every crossing. On release the wheel is deliberately under-damped: the seat spring holds 'zeta 0.38 (exactly two overshoots)' (BlockitCoreViews.kt:1223-1229), coasts up to the 1,400ms fling deadline before springing to nearestStopAngle (BlockitCoreViews.kt:1278-1289), and applyValueFromAngle(fromDrag=false) keeps changing/reporting values throughout the coast (BlockitCoreViews.kt:1289). Where you let go is not where you land, and nothing shows the final seat until it arrives.

### [MEDIUM] Every detent sounds twice at two different pitches, and the 'haptic' is not a haptic when the sound engine is attached
*Location:* `BlockitCoreViews.kt:224-249,1161-1173; MonolithViewModel.kt:1471-1477; MainActivity.kt:702-709`

One crossing produces two engine sounds from the same TactileSoundEngine (the wheel receives viewModel.soundEngine: MainActivity.kt:702-709). The view fires fireFeel() in applyValueFromAngle — a pitched playRotaryDetent at '340f + ((newValue - 5) / 175f) * 540f', or a pitch-less playMechanicalLatch on multiples of 15 (BlockitCoreViews.kt:1161-1173,224-243) — then onMinutesChanged invokes ViewModel.setDialMinutes, which unconditionally plays a second playRotaryDetent at a different pitch, '440.0 + (clamped * 2.8)' (MonolithViewModel.kt:1471-1477, via TactileDeckScreen.kt:696). At 45/60/75 the user hears a latch plus a detent at an unrelated pitch; the pitch ladder that is supposed to tell them where they are is doubled and self-contradicting, and with the engine attached fireFeel never vibrates at all (it returns after the engine call, BlockitCoreViews.kt:233-249), so the promised haptic-per-detent is sound-only.

### [MEDIUM] A 47-minute value set by a task is silently rounded to 45 the first time the user touches the wheel
*Location:* `BlockitCoreViews.kt:516-530,1179; MonolithViewModel.kt:1640-1644,1814-1823,2008-2015`

Other paths legally put non-multiples of 5 on the dial: seedDialFromTask copies any estimatedMinutes coerceIn(5,180) (MonolithViewModel.kt:1640-1644) and updateTaskDuration/extendActiveTaskOvertime do the same (MonolithViewModel.kt:1814-1823,2008-2015). But the wheel cannot represent them: minutesToAngle(47) places the dot at 126 + 37*1.8 degrees while valueForAngle() of that same angle rounds back to 45 (BlockitCoreViews.kt:516-530), and nearestStopAngle() = minutesToAngle(valueForAngle(angle)) (BlockitCoreViews.kt:1179). The first drag or physics seat therefore silently deletes 2 minutes (47->45, or rounds 48->50) with no notice — precision the app itself created is destroyed by merely touching its own dial.

---

## Angle: typing-intention (4 findings)

**Verdict:** There is no moment of writing an intention in this app: the user never types one, and the sentence later waved back at them in the airlock, the notification, and the receipt is a factory default, a stock phrase substituted for a blank, or whatever task title the queue machinery last copied in — and it can be silently rewritten mid-session.

### [HIGH] The 'intention' is never authored by the user: it is a live mirror of the active task's title, silently overwritten by every task operation
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:784-789, 1480-1482`

The only writer a user could ever reach, setIntentionText, is a raw assignment with exactly one caller — the Deck callback that is never rendered (MonolithViewModel.kt:1480-1482; MainActivity.kt:510). Everything else that writes _intentionText is task machinery: a collector overwrites it on every activeTask emission (MonolithViewModel.kt:784-789, '_intentionText.value = task.title'), and task create/complete/defer/promote/restore paths overwrite it again at MonolithViewModel.kt:1669, 1694, 1708, 1725, 1747, 1765, 1783, 1803, 1973, 2024. So the words later labelled the user's intention change whenever the queue moves, without the user typing, editing, or confirming anything.

### [HIGH] Mid-session task promotion rewrites the running session's stored intention, so the receipt and later airlock lines no longer show what the session was armed with
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:2581-2584, 2688-2699`

completeTaskAndAdvance rewrites the persisted row: 'currentSession = currentSession.copy(intention = promoted.title)' followed by dao.updateSession (MonolithRepository.kt:2581-2584). The manual promote path does the same (MonolithRepository.kt:~2697). The code's own ESR-02 comment admits the stakes — a sealed session's intention 'is the receipt's title source — rewriting it under the seal would falsify the slip' — yet the guard only refuses the write for the sealed row; every ordinary in-flight (unsealed) session is rewritten freely. The receipt title is built from that same row (MonolithRepository.kt:1629, title = row.intention; ReceiptSheet.kt:92 prints receipt.title).

### [HIGH] A user who writes nothing gets words put in their mouth twice: a factory default, and a blank-to-stock-phrase substitution, both read back as their declaration
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:226; app/src/main/java/com/example/data/MonolithRepository.kt:1348`

_intentionText is born pre-filled with someone else's homework: MutableStateFlow("Finish Chapter 4 Problem Set") (MonolithViewModel.kt:226), and armMonolithSession passes it to the arm flow unconditionally with no required/blank check (MonolithViewModel.kt:2203). If the value ever is blank, startSession forges a replacement: intention = intention.ifBlank { "Deep Uninterrupted Study" } (MonolithRepository.kt:1348). Those invented words are then persisted and read back at the user as theirs: the persistent shield notification title 'Monolith • ${updated.intention}' (MonolithShieldService.kt:143) and the airlock's 'ACTIVE INTENTION: ${state.sessionIntention}' line (AirlockAndDisarmOverlays.kt:646-653, fed from active?.intention at MonolithViewModel.kt:2354).

### [LOW] The only keyboard that can ever feed the intention (the task-title box) has no length, trim, or capitalisation discipline, and its text is rendered unbounded at the moment of temptation
*Location:* `app/src/main/java/com/example/ui/screens/FocusSequencerScreen.kt:230-247; app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:646-653`

setIntentionText stores text verbatim — no trim, no cap (MonolithViewModel.kt:1480-1482). The proxy input, the Sequencer's task field, is singleLine with only an IME Done action, no maxLength or keyboard customisation (FocusSequencerScreen.kt:230-247). That uncapped string is then printed with no maxLines limit in the 12.5sp airlock intention line (AirlockAndDisarmOverlays.kt:646-653) and used raw as the shield notification's title (MonolithShieldService.kt:143), so a long pasted task title floods the two surfaces meant to deliver a short, sharp reminder.

---

## Angle: armed-wrong (5 findings)

**Verdict:** Arming the wrong thing is effectively irreversible in practice: the gesture commits before a human can react, the free 10s abort is unadvertised, hidden for its first second and Deck-only, and one second late the mistake costs the week's only emergency token - or cannot be exited at all.

### [CRITICAL] Second-11 cliff: a mistaken arm stops being free and becomes the week's sole emergency token - or unexitable if it is spent
*Location:* `app/src/main/java/com/example/ui/screens/TactileDeckScreen.kt:1029-1055; app/src/main/java/com/example/data/MonolithRepository.kt:2233; app/src/main/java/com/example/ui/MonolithViewModel.kt:completeEmergencyBreakFromValve`

TactileDeckScreen.kt:1029: withinGraceWindow = elapsedSeconds <= 10; at 11s the same slot renders only 'EMERGENCY BREAK VALVE (60S HOLD)' with badge '$weeklyEmergencyTokensRemaining/1 WEEK' (lines 1045-1055). That path is MonolithViewModel.requestDisarmChallenge() -> 60s DisarmChallenge -> completeEmergencyBreakFromValve(), and MonolithRepository.completeEmergencyBreak() returns BreakResult.RefusedNoToken when the week's one spend exists (line 2233: 'if (dao.getSpendsForWeek(anchor).size >= 1) return RefusedNoToken'), which the ViewModel handles as 'Session stays sealed'. There is no in-place fix: once running, TactileDeckScreen replaces the RotaryWheelView with a 6dp progress track (running else-branch), so dial, blocks and profile cannot be corrected at all. A typo discovered at second 11 - wrong 180 minutes, wrong 8 blocks (span = dial*blocks + (blocks-1)*10 breaks) - costs the week's only token or the whole session.

### [HIGH] Point of no return arrives mid-gesture: tap commits in 140ms, drag commits at 0.84 and cannot be dragged back
*Location:* `app/src/main/java/com/example/ui/views/BlockitCoreViews.kt:1318,1534,1787,1844,2215,2318,2408`

A plain tap counts as an order: endDrag's tap path calls startMesh to 0.92 (BlockitCoreViews.kt tap branch in endDrag), the mesh runs DURATION_140 (MonolithCurves.kt:23 'const val DURATION_140 = 140'; BlockitCoreViews.kt:2215), then seats and latchFrame() fires onSlideCommitted. The only undo is WORDS_UNDO='TAP AGAIN TO UNDO' (BlockitCoreViews.kt:2408), rendered only in Mode.MESH (line 1787), and the abort needs 'a second touch during the MESH' (lines 1844-1847) - a 140ms window no human who has just realised a mistake can hit. On the drag path it is worse: SlideGestureMath.commits() fires at displayed>=0.84 under contact (lines 1318, 1352-1353), the code's own comment says 'the commit crossing ends finger ownership' (line 1534), and stepSeat clamps 'if (displayed < seatFloor) displayed = seatFloor' (line 2318) - dragging back toward rest does literally nothing.

### [HIGH] The 10s grace window is undiscoverable and partly unusable: never advertised before arming, no verb during proving, chip hidden for the 1050ms seal shot
*Location:* `app/src/main/java/com/example/ui/screens/TactileDeckScreen.kt:973,1029-1043; app/src/main/java/com/example/ui/ArmProvingController.kt:onLatch/runAttempt; app/src/main/java/com/example/ui/MonolithViewModel.kt:startTickerLoop`

Nothing anywhere before the seal mentions the window: the string 'GRACE WINDOW ABORT' occurs only at TactileDeckScreen.kt:1032, post-seal. After sliding, the session row is born active and the shield starts during proving (ArmProvingController class doc: row 'born active at insert', enforcement before t=0), yet the controller offers no user cancel in Proving/Jammed - onLatch() just returns if already proving, and its jam comment says the retry 'is the machine's, never his - no verb is offered'. Once sealed, TactileDeckScreen.kt:973 keeps the slide ghost in the grace chip's slot whenever 'shotActive' is true, and the shot only lands at e>=1050ms (lines 258-259), so the chip is physically absent for the first ~1.05s of the 10s. Its countdown badge '${10 - elapsedSeconds}s' (line 1033) is driven by the DB elapsedSeconds that the ViewModel ticker only persists once per second (MonolithViewModel startTickerLoop delay(1000L) -> updateSessionProgress), so even the printed remainder is stale.

### [MEDIUM] Even inside grace there is no 'fix it' - abort is all-or-nothing, no edit of duration, blocks or loadout on the live session
*Location:* `app/src/main/java/com/example/MainActivity.kt:514-515; app/src/main/java/com/example/ui/MonolithViewModel.kt:2197-2210,2267-2283; app/src/main/java/com/example/data/MonolithRepository.kt:2311-2325`

MainActivity.kt:514-515 wires the grace chip to exactly one call: confirmDisarmSession('Accidental Start Grace Cancel'), which disarms the whole session (MonolithViewModel.kt:2267-2283); MonolithRepository.disarmActiveSession() treats it as outcome GRACE_CANCEL with bankedElapsed=0 (lines 2311-2325). Nothing calls setDialMinutes/selectAndApplyProfile/applySmartLoadout on the live row, and startSession's target/blocks are fixed at insert (MonolithRepository.kt:1351ff, targetDurationSeconds from durationMinutes, totalStudyBlocks coerced 1..8). So even a one-field mistake - 25 vs 50 minutes, STUDY vs SLEEP loadout name latched in armMonolithSession() (MonolithViewModel.kt:2197-2210 reads _selectedProfileName/_dialMinutes/_studyBlocksCount once) - requires killing the session, re-setting everything by hand, and sliding again, with the re-arm re-proving from zero.

### [MEDIUM] Grace abort lives only on the Deck: leave the Deck to check what you armed wrong and the window burns invisibly
*Location:* `app/src/main/java/com/example/ui/screens/TactileDeckScreen.kt:1029-1043; app/src/main/java/com/example/ui/MonolithViewModel.kt:navigationLocked`

'grace_abort_button' / onCancelWithinGraceWindow exists in exactly one composable: TactileDeckScreen (lines 1029-1043); no other screen, overlay, Airlock, desk clock or notification surface renders it (repo-wide grep for GRACE WINDOW ABORT returns only that site). Navigation unlocks at t0+1050ms (MonolithViewModel.navigationLocked()), i.e. with ~9s of grace left, so the natural wrong-arm reflexes - open SHIELD to check/fix the wrong block, open HELD or DESK CLOCK, tap a blocked app to test the loadout and get the Airlock - all burn the window with no countdown shown anywhere. Returning to the Deck after 10s presents the valve chip instead, with no hint the free exit existed and expired while they were elsewhere.

---

## Angle: seal-wait-confusion (5 findings)

**Verdict:** The seal wait is a 0.2-1.5s (plus 1.05s seal-shot) stretch of total on-screen silence on a Deck that still looks idle and tappable: re-slides are swallowed, setting changes don't apply, the one jam sentence can name the wrong link and offers no verb or countdown, killing the app mid-wait resurrects the unproven session as running, and a user blocked during the wait watches that session be annulled as if it never existed.

### [HIGH] Kill the app during the wait and the unproven session resurrects as fully running — proof is bypassed by relaunch
*Location:* `app/src/main/java/com/example/ui/ArmProvingController.kt:37-44; app/src/main/java/com/example/ui/screens/TactileDeckScreen.kt:195-207`

Controller header: 'PROVING lives here and nowhere else: a controller state in the ViewModel layer. Not a DB state, not a session column' (ArmProvingController.kt:37-39) and _phase starts at Idle in a plain MutableStateFlow (ArmProvingController.kt:~270). The row is 'born active at insert' (ArmProvingController.kt:40-44). On relaunch the phase is therefore Idle, and TactileDeckScreen.kt:195-207 states sessions 'born outside the cascade (routines, restores) find the phase Idle and run as they always have' — provingThisSession is false for Idle, so isSessionRunning becomes true with no t=0, no re-anchor, no seal, and no 'NO PROOF' sentence. The proof the UI promised ('only when every link has answered does t=0 exist', ArmProvingController.kt:23-28) is simply skipped by killing the app at the one moment it is being checked.

### [HIGH] The proving wait is deliberately silent and the Deck looks idle and fully interactive — nothing on screen says a seal is being proven
*Location:* `app/src/main/java/com/example/ui/components/MonolithCustomComponents.kt:537-549; app/src/main/java/com/example/ui/screens/TactileDeckScreen.kt:202-208,1062-1071`

jamSentenceFor: 'Non-jammed phases print nothing here (PROVING's silence is the machine working)' (MonolithCustomComponents.kt:537-540), and the Deck's slot is explicitly 'armPhase is ArmPhase.Proving -> null' (TactileDeckScreen.kt:1070-1071). provingThisSession forces isSessionRunning=false (TactileDeckScreen.kt:202-208), so the hero still shows the idle dial total, the eyebrow still reads 'TOTAL' not 'SESSION RUNNING', and there is no spinner, countdown, count of links, or the word PROVING anywhere. Worse, the Deck is not frozen: the wheel's lock is 'view.isLockedSession = shotActive' and shotActive requires isSessionRunning, so the wheel, the FLOW pill (TactileDeckScreen.kt:~695), task DONE ('clickable(enabled = !shotActive)') and scroll ('enabled = !shotActive') all stay live — yet onLatch already captured durationMinutes/studyBlocksCount as plain parameters (ArmProvingController.kt onLatch; MonolithViewModel.kt:2202-2207), so anything the user changes while waiting silently does not apply to the attempt in flight. Healthy wait is at least DWELL_MS=210ms and up to DEADLINE_MS=500ms before jam (ArmProvingController.kt companion), followed by a further 1050ms seal-shot detention after success (TactileDeckScreen.kt:~243-262) — ~1.3-2.5s that reads as a frozen idle screen.

### [MEDIUM] Tapping or re-sliding during the wait is swallowed whole — no feedback that the attempt exists or that the retry did nothing
*Location:* `app/src/main/java/com/example/ui/views/BlockitCoreViews.kt:1848-1852,2343-2348; app/src/main/java/com/example/ui/ArmProvingController.kt:onLatch`

After latchFrame() fires onSlideCommitted the slide sits in Mode.SEATED (BlockitCoreViews.kt:2343-2348). Its touch handler: 'if (mode != Mode.REST) return true' on ACTION_DOWN (BlockitCoreViews.kt:1848-1852) — the tap is consumed and dropped, no knock, no shake, no print. Behind it, onLatch itself early-returns: 'if (current is ArmPhase.Proving || current is ArmPhase.Jammed) return' (ArmProvingController.kt onLatch). The only evidence the first slide registered is the cap parked at the end of the track; a user who slides again, taps the cap, or hammers it gets identical silence for the whole 210-1500ms window and cannot distinguish 'proving' from 'my slide never registered'.

### [MEDIUM] The jam message is frozen, verb-less and countdown-less: it keeps naming a link that may already have answered, then the attempt vanishes at 1.5s
*Location:* `app/src/main/java/com/example/ui/components/MonolithCustomComponents.kt:542-549; app/src/main/java/com/example/ui/ArmProvingController.kt:runAttempt/annul, companion ANNUL_MS/SENTENCE_HOLD_MS`

At the 500ms deadline the phase becomes Jammed with a frozen jamSet (ArmProvingController.kt runAttempt: jamSet = sampled.pendingLinks()). applyMark() on a Jammed phase updates only 'links', never jamSet, and jamSentenceFor prints from jamSet: '"${first.displayName} DIDN'T ANSWER — STILL PROVING."' with 'action = null' (MonolithCustomComponents.kt:542-549) — commented 'It carries no action: the retry fires on the machine's own clock and offers him no verb.' So if SHIELD recovers at 700ms but ACCESS is still pending, the slot keeps accusing SHIELD for the remaining ~800ms, shows no countdown to the L+1500ms annulment (ANNUL_MS=1500), and the only other signal is jam haptics — three 20ms seat-knocks at 90ms spacing wired in MonolithViewModel.kt:~700 — invisible on a muted/DND phone lying face-up. Then annul() snaps the slide home via resetToRest (displayed=0 instantly, BlockitCoreViews.kt:1482-1500) and swaps in a different sentence, '… — NO PROOF. NOTHING WAS SEALED.', held 10s (SENTENCE_HOLD_MS=10_000).

### [MEDIUM] Blocking is already live during the wait, but an annulled attempt is deleted as if it never existed — the user was blocked by a session the app denies
*Location:* `app/src/main/java/com/example/ui/ArmProvingController.kt:40-44,onLatch; app/src/main/java/com/example/data/MonolithRepository.kt:1448-1457`

The batch at latch starts the shield in the same frame as the insert (ArmProvingController.kt onLatch: 'port.startShield()' alongside the startSession launch), and the header concedes 'the ≤500ms of enforcement before t=0 is lawful because no surface claims the seal before t=0' (ArmProvingController.kt:40-44). A user who slides, immediately switches to a blocked app to 'check something', is therefore intercepted by a session that has not been proven. If proving then fails, annul() deletes the row outright — annulProvingSession is 'dao.deleteSessionById' and its comment insists the session 'un-exists… in law, never was' (MonolithRepository.kt:1448-1457) — and stops the shield. No history, receipt or explanation survives for the block the user just experienced; back in the app they only get the 10-second 'NOTHING WAS SEALED' sentence for an enforcement that demonstrably happened to them.

---

## Angle: glance-time-remaining (5 findings)

**Verdict:** Mid-session 'how long is left?' has no honest one-glance answer: the user must open the app and pick a surface, and the surfaces disagree - Deck/notification count to the end of the current block (00:00 through every break), desk clock and Deck strip count to the end of the session, and the strip's ENDS time itself drifts a minute early - so the number they get depends on where they happened to look, not on how long is actually left.

### [HIGH] Multi-block glance gives two different 'time left' answers: Deck/notification count to end of this block, desk clock and Deck strip count to end of session - unlabelled
*Location:* `MonolithModels.kt:373-429; TactileDeckScreen.kt:312,525; MonolithShieldService.kt:134-139; AirlockAndDisarmOverlays.kt:107`

Model claims the opposite: MonolithModels.kt:373-378 'every surface - Deck, desk clock, airlock, the enforcing service - reads these and only these, so the app can only ever tell one time.' Reality: Deck hero reads per-BLOCK remaining - TactileDeckScreen.kt:312 'if (activeSession.isStopwatchMode) activeSession.elapsedSeconds else activeSession.remainingSeconds' (MonolithModels.kt:382-383 remainingSeconds = effectiveTargetSeconds - elapsedSeconds, one block only). Shield notification does the same per-block math inline at MonolithShieldService.kt:134-139 ('%02d:%02d left • Block %d/%d'). Desk clock reads the whole SPAN at AirlockAndDisarmOverlays.kt:107 'activeSession.spanRemainingSeconds' (MonolithModels.kt:400-429 spanTarget = totalStudyBlocks*effectiveTarget + (blocks-1)*600). Deck's own strip also uses span (TactileDeckScreen.kt:525). Worked example, 3x50min session at start: Deck hero and notification say 50:00, desk clock says 170:00, Deck strip says ENDS in 170 min - on the same Deck screen, hero vs strip disagree by 2 hours, and neither hero nor desk clock labels its number BLOCK vs SESSION (only the notification's small trailing 'Block 1/3' hints).

### [HIGH] During every Safe Break the Deck hero reads 00:00 for 10 minutes - a glance says the session is finished while notification counts the break and desk clock counts the session
*Location:* `TactileDeckScreen.kt:312,621-625; MonolithRepository.kt:2020-2043; MonolithShieldService.kt:126-129; AirlockAndDisarmOverlays.kt:102-107`

At a block boundary MonolithRepository.kt:2020-2043 sets elapsedSeconds = effectiveTarget and isInSafeBreak=true, so remainingSeconds (MonolithModels.kt:382-383) is exactly 0 for the whole break. Deck hero is that value (TactileDeckScreen.kt:312) formatted as primaryReadout (TactileDeckScreen.kt:316-318), i.e. '00:00' for 10 minutes; the only correction is the 10sp eyebrow 'BREAK · X MIN' at TactileDeckScreen.kt:621-625. Same moment, other surfaces: notification counts the BREAK down ('Safe Break %02d:%02d', MonolithShieldService.kt:126-129) and desk clock counts the SESSION down (spanRemainingSeconds, AirlockAndDisarmOverlays.kt:102-107 - its own comment admits per-block 'would read 00:00 (FINISH) through every break'). A user glancing at the Deck - the default surface - sees the finished-state number while two blocks may still remain.

### [MEDIUM] Deck strip 'ENDS HH:mm' mixes a minute-stale wall clock with a per-second countdown, so the end time drifts up to a minute early each minute and disagrees with the Airlock's end line
*Location:* `TactileDeckScreen.kt:352-360,525; MonolithViewModel.kt:2363-2370; MonolithCustomComponents.kt:427-428`

Deck strip end is endsAtText(nowFrame + spanRemainingSeconds*1000) (TactileDeckScreen.kt:525), but its two inputs tick at different rates: nowFrame is only re-read on a minute-boundary loop (TactileDeckScreen.kt:352-360: delay(60_000 - (now % 60_000) + 50)), while spanRemainingSeconds falls every second via the 1s VM ticker (MonolithViewModel.kt:949-1020). So after each boundary the printed end = real end minus seconds-elapsed-in-the-minute: it drifts up to 59s early, reads one minute early in HH:mm (MonolithCustomComponents.kt:427-428 truncates to HH:mm) for part of every minute, then snaps a minute later at the next boundary. The Airlock's end line is computed fresh in one shot (MonolithViewModel.kt:2363-2370: System.currentTimeMillis() + spanRemainingSeconds*1000, stored as an absolute instant), so Airlock and Deck strip can show ENDS times a minute apart at the same moment, and the Deck's own end time visibly moves backwards/forwards while the user just watches it.

### [MEDIUM] Desk clock prints count-up (stopwatch) and count-down (timed) in identical unlabelled MM:SS - a glance cannot tell elapsed from remaining
*Location:* `AirlockAndDisarmOverlays.kt:104-112,154; MonolithShieldService.kt:130-133`

AirlockAndDisarmOverlays.kt:104-112: displaySecs = if stopwatch 'activeSession.elapsedSeconds' else 'activeSession.spanRemainingSeconds', then one unlabelled timeText = '%02d:%02d' of mm:ss. The overlay's only other text is 'NIGHTSTAND DESK CLOCK • TAP ANYWHERE TO EXIT' (line 154), the progress bar (lines 116-126) and a task/intention banner (line 196) - the words LEFT, ELAPSED, ENDS or FLOW appear nowhere. So in FLOW/stopwatch mode the big number counts UP from zero and in timed mode it counts DOWN, in identical 96sp type; a half-asleep glance cannot tell whether 12:34 is time spent or time remaining. The same format also never converts to hours: spanRemaining of a 170-minute session prints as '170:00'. The notification, by contrast, does label its stopwatch ('Stopwatch +%02d:%02d (20m floor)', MonolithShieldService.kt:130-133) - proof the label was considered necessary there and omitted here.

### [MEDIUM] No zero-tap glance exists: rail and non-Deck tabs show no time at all, and the desk clock needs Deck navigation plus a tap, then any stray tap dismisses it
*Location:* `MainActivity.kt:700-749; TactileDeckScreen.kt:897-910; MonolithViewModel.kt:249-250; AirlockAndDisarmOverlays.kt:140-154`

No surface answers without opening the app and navigating: the always-visible rail (MainActivity.kt:700-741, 52dp MonolithRailView) carries no session time at all, and grep shows no remaining/elapsed rendering anywhere in MainActivity outside the Deck, and none in FocusSequencerScreen/ShieldMatrixScreen/DrawerScreen - leave the Deck tab and the answer is gone. The desk clock, the surface designed for glancing, is buried behind the Deck's session-utilities row that only exists while running (TactileDeckScreen.kt:897-910, 'DESK CLOCK' chip -> onOpenAlwaysOnDeskClock) and its visibility is a plain in-memory StateFlow (MonolithViewModel.kt:249-250,1563-1565), so it must be re-summoned every time. Once up, the whole screen is one dismiss target - AirlockAndDisarmOverlays.kt:140-147 Box '.clickable(onClick = onDismiss)' with the header 'TAP ANYWHERE TO EXIT' (line 154) - so the accidental tap that a reaching, half-asleep user makes to steady the phone destroys the very clock they lifted it to read. The shield notification is thus the only true glance surface, and per the first finding it answers with the per-block number, not the session's.

---

## Angle: accidental-rail-park (5 findings)

**Verdict:** Mid-session the seal locks the apps but not the furniture: the rail stays fully navigable after the first second, and a stray tap or two from there — or one graze on the Deck's DONE cap — can silently unblock a site, rewrite a routine's schedule, or falsify the task record, with no warning, confirmation, or undo anywhere along the path.

### [HIGH] Sealed session, live rail: one stray tap navigates away mid-session because navigationLocked() never checks the seal
*Location:* `app/src/main/java/com/example/ui/views/MonolithRailView.kt:308; app/src/main/java/com/example/ui/MonolithViewModel.kt:1388-1429; app/src/main/java/com/example/MainActivity.kt:715-724`

MonolithRailView has no sealed flag at all: onTouchEvent (ui/views/MonolithRailView.kt:308) processes every DOWN/MOVE/UP and tapStop/commitStop (:365/:377) fire onTabSelected for any stop. The host wires that straight to navigation (MainActivity.kt:715-724). The only lock, MonolithViewModel.navigationLocked() (ui/MonolithViewModel.kt:1388-1401), checks Proving/Jammed, the first 1050ms of SealedRunning, and finish detention — never activeSession.isActive. So after the seal shot's first second, a child's tap or a palm graze across the 52dp band leaves the Deck for SHIELD/TASKS/VAULT mid-session. The user is told nothing: navigateTo (:1403-1429) plays only a rotary detent (silent if sound is off), prints no warning, and as a side effect voids the receipt's sled-return gate for any non-DECK destination (:1415-1418). The sealed Deck — the one screen that looks locked — is one stray tap away from screens full of live controls.

### [HIGH] Blocked-site REMOVE is a one-tap, unratcheted hole in the live seal
*Location:* `app/src/main/java/com/example/ui/screens/ShieldMatrixScreen.kt:557-564; app/src/main/java/com/example/ui/MonolithViewModel.kt:1558-1561; app/src/main/java/com/example/data/MonolithRepository.kt:800-802`

Each blocked-site row carries a one-tap REMOVE chip (ui/screens/ShieldMatrixScreen.kt:557-564, onClick = { onRemoveSite(domain) }). MonolithViewModel.removeBlockedSite (ui/MonolithViewModel.kt:1558-1561) has no active-session check at all — unlike its neighbours setAppBlockTier (:2064-2096, ratchet refusal + warning), updateAppDailyLimit and toggleProfileSchedule, which all refuse weakening mid-session. MonolithRepository.removeBlockedSite (data/MonolithRepository.kt:800-802) is a bare dao.deleteSite(domain), and the accessibility matcher enforces exactly 'the user's stored sites unchanged' (service/MonolithAccessibilityService.kt:62-68), so the deletion weakens the live session immediately. No ratchet warning, no confirmation, no undo: the row just vanishes and the count drops by one. A pocket tap after stray rail navigation (previous finding) silently opens a site the user believes is shut for this session.

### [HIGH] One stray tap on a routine's ON/OFF cap rewrites the schedule mid-session — no ratchet, no confirmation, fresh edit stamp
*Location:* `app/src/main/java/com/example/ui/screens/ShieldMatrixScreen.kt:708-725; app/src/main/java/com/example/ui/MonolithViewModel.kt:1501-1519`

RoutineRow receives no isSessionActive parameter and its 48x32dp ON/OFF cap is an unconditional clickable { onToggle() } (ui/screens/ShieldMatrixScreen.kt:708-725), wired straight through at :331. MonolithViewModel.toggleRoutine (:1515-1519) performs no session check, and persistRoutine (:1501-1513) re-stamps scheduleEditedAtMillis = System.currentTimeMillis(), dismisses the routine reset note, and forces a RoutineHeartbeat reconcile on every flip. One stray tap therefore disables tonight's self-arming window (or arms one that was off) and manufactures a fresh edit stamp that the MISSED-judgement edit-horizon trusts — while the app-tier, daily-limit and profile-schedule controls around it all carry one-way-ratchet guards for exactly this situation. The user is told nothing: no warning banner, no confirmation; if the tap was in a pocket, the only trace is an ON/OFF word nobody is looking at.

### [MEDIUM] Refused PARK graze strands the rail cap in the PARK zone — the band lies about where you are
*Location:* `app/src/main/java/com/example/ui/views/MonolithRailView.kt:365-388; app/src/main/java/com/example/MainActivity.kt:289-301,712-714`

A tap or drag-seat on the PARK zone always animates the cap into the zone first and only then asks the host: tapStop/commitStop call animateCapTo(PARK_INDEX) { onParkInvoked?.invoke() } (ui/views/MonolithRailView.kt:369-370, :381-382). While sealed (or in finish detention) the host correctly refuses — openPark() at MainActivity.kt:289-301 requires !sealed && !navigationLocked() and simply does nothing. But nothing animates the cap back: the host's update only re-seats it 'if (view.selectedTabIndex != selectedTabIndex)' (MainActivity.kt:712-714), and the committed stop never changed, so the cap stays parked over the blank, wordless zone with PARK shut. The band — the app's primary navigation furniture — now prints a selection that does not exist, contradicting its own grammar ('While PARK is open no word is selected'), and the user gets no sound, word, or motion explaining that their graze was refused or where they actually are.

### [MEDIUM] Deck's DONE cap stays live for the whole sealed session — one stray tap completes the task and advances the queue
*Location:* `app/src/main/java/com/example/ui/screens/TactileDeckScreen.kt:831; app/src/main/java/com/example/ui/MonolithViewModel.kt:1700-1712`

On the sealed Deck, the NOW row's DONE key-cap is gated only by the 1050ms seal shot: .clickable(enabled = !shotActive) { onCompleteActiveTask(activeTask.id) } (ui/screens/TactileDeckScreen.kt:831) — isSessionRunning never disables it (the title tap and substep rows at :811/:858 use the same shot-only gate). MonolithViewModel.completeTaskAndAdvance (:1700-1712) has no midSeal()/sealed guard either, unlike promoteTaskToActive and autoSplitTaskIntoMicroSteps which explicitly refuse mid-seal (:1735-1745, :1998); it completes the task in the repository and rewrites _intentionText to the next task's title. A child tapping the one prominent verb on the sealed screen permanently marks the task done and advances the queue — no confirmation, no undo on this screen (REOPEN lives over on the Tasks screen), and no print saying the session's task record just changed.

---

## Angle: unlisted-distraction (5 findings)

**Verdict:** As lived, the user's real distraction — unlisted, mis-guessed, or installed after the last sync — opens freely all session, nothing in the app ever notices the catalogue gap, and the receipt still stamps the session ELAPSED_CLEAN with a HELD count that never included it.

### [CRITICAL] Unrecognized real distraction is born OPEN by a keyword guess — silently, with no review prompt
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:514-528; app/src/main/java/com/example/data/AppCatalogReconcile.kt:88-92`

categorizePackage (MonolithRepository.kt:514-528) guesses category from substrings of package+label only: GAMES requires 'game|clash|roblox|play', SHORT_VIDEO 'tiktok|youtube|instagram|reel', SOCIAL 'twitter|reddit|snapchat|facebook', BROWSERS 'chrome|browser|firefox|brave', else SHOPPING ('Other Apps', MonolithModels.kt AppCategory). AppCatalogReconcile.kt:88-92 then births only SHORT_VIDEO/GAMES as LOCKED and MESSAGING as PEEK_2M — 'else -> AppBlockTier.OPEN' — so even a correctly-guessed SOCIAL or BROWSERS app, and every unrecognized game (e.g. one whose name/package contains none of the magic words), news app, or second browser, is born OPEN. No code marks it unreviewed or asks the user to rule on it; it silently joins the Open majority.

### [CRITICAL] No catalogue row means no law at all: an app installed after the last sync is invisible to both enforcement doors for the whole session
*Location:* `app/src/main/java/com/example/service/MonolithAccessibilityService.kt:273-275; app/src/main/java/com/example/service/MonolithShieldService.kt:169; app/src/main/java/com/example/ui/MonolithViewModel.kt:768,2451`

Both doors only enforce rows that exist: MonolithAccessibilityService.kt:273 'val appRule = dao.getAllAppsSnapshot().find { it.packageName == pkgName } ?: return@launch' and :275 OPEN returns; MonolithShieldService.kt:169 builds blockedPackages solely from DB rows with 'tier == AppBlockTier.OPEN -> false'. The catalogue sync ensureSeededAndSyncPackages (MonolithRepository.kt:192) is called only from MonolithViewModel.kt:768 (init) and :2451 (refreshSystemGuard) — the shield's 2s loop never re-syncs, and the manifest declares no PACKAGE_ADDED receiver. Install the distracting app after the last Monolith launch — even a factory-preset one that would be born LOCKED — and it has no row, no tier, and no enforcement for the entire session.

### [HIGH] Afterwards the record still calls it clean: ELAPSED_CLEAN is decided only by the shield's heartbeat, never by what was actually used
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:2063-2072,1608,531; app/src/main/java/com/example/ui/components/ReceiptSheet.kt:131`

The ending write in MonolithRepository.kt:2063-2072 stamps ELAPSED_CLEAN vs ELAPSED_SHIELD_GAP purely from 'session.shieldGapSeen || nowWall - beatRef > SHIELD_GAP_THRESHOLD_MILLIS' — whether the shield process beat, never what the user opened. Receipt deflections (MonolithRepository.kt:1608) are built only from intercept_logs, which OPEN/no-row apps never generate (they are never intercepted), and the one usage figure, queryRealTodayUsageMinutes (MonolithRepository.kt:531), is a whole-day total refreshed only at sync and never compared against the session window. The session therefore completes with completedSuccessfully/forged artifact and ReceiptSheet.kt:131 prints 'HELD n APPS · n SITES' — a clean receipt for time freely spent in the unlisted app.

### [HIGH] The one-tap fix fails too: STUDY/WORK_SPRINT loadouts re-derive from the same wrong category, so the unlisted app stays OPEN while the seal counts 'held' apps
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:1125-1150,1318,1384; app/src/main/java/com/example/data/MonolithModels.kt (SmartLoadout.STUDY)`

SmartLoadout.STUDY's own subtitle promises 'Blocks Social & Games' (MonolithModels.kt, SmartLoadout), but applySmartLoadout (MonolithRepository.kt:1125-1150) re-derives tiers from the same guessed category: STUDY locks only SHORT_VIDEO/SOCIAL/GAMES and WORK_SPRINT only SHORT_VIDEO/GAMES, both 'else -> AppBlockTier.OPEN' (:1139, :1149). A news/game app filed as SHOPPING or BROWSERS stays OPEN under both; only SLEEP locks it. At seal, startSession captures heldAppsAtSeal = countHeldApps = rows 'resolvedTier != OPEN' (MonolithRepository.kt:1318,1384), so the receipt's HELD figure certifies the loadout while the user's actual distraction was never in the count and no arm-time surface lists what remains open.

### [MEDIUM] The audit surfaces hide the gap by design: standing orders exclude OPEN apps entirely and the tally prints 61 OPEN as neutral bookkeeping, never as exposure
*Location:* `app/src/main/java/com/example/data/AppRules.kt:3-22; app/src/main/java/com/example/ui/screens/ShieldMatrixScreen.kt:841-843`

AppRules.kt:3-10 states the standing-orders derivation plainly: 'OPEN apps carry no ruling and never appear here' — groupedByRule returns only peek and shut lists. The Shield tally itself is designed as e.g. '9 SHUT · 4 PEEK · 61 OPEN' (ShieldMatrixScreen.kt:841-843 comment) presented as bookkeeping completeness ('the book provably covers every app on it'), not as a warning. Nowhere — catalogue, standing orders, Deck, or arm flow — is an OPEN app framed as an unguarded door or the user nudged to review the Open pile before sealing, so the gap is structurally easy to never notice.

---

## Angle: browser-as-loophole (5 findings)

**Verdict:** As lived, the browser is not a door with a lock but a corridor with a short list taped to it: Chrome is born Open, the main YouTube feed and any second browser are outside the fence entirely, a single peek on Chrome suspends the whole site list, and the sites that are blocked admit no peek at all — so 'one search' can end in an unblocked feed while a genuinely needed page stays absolutely shut.

### [CRITICAL] A 2-minute peek on the browser itself switches off the entire site blocker — one knock unlocks every blocked site at once
*Location:* `app/src/main/java/com/example/service/MonolithAccessibilityService.kt:218-254; app/src/main/java/com/example/data/MonolithRepository.kt:1133-1134`

The site door is gated on `!leaseLive` (MonolithAccessibilityService.kt:254), and leaseLive is computed purely per-package: lease row is PEEK_2M and its 2-minute lease is running (MonolithAccessibilityService.kt:226-228). The code's own comment admits the design: 'a live peek lease on THIS package silences the sniper and the site blocker too' (MonolithAccessibilityService.kt:218-224). The STUDY loadout — the default study posture — puts Chrome itself on PEEK_2M: `lower.contains("youtube") || lower.contains("chrome") ... -> AppBlockTier.PEEK_2M` (MonolithRepository.kt:1133-1134). So the user who knocks for a 2-minute peek to do 'one search' in Chrome gets, for the full lease, unintercepted access to every blocked site in that same browser — reddit.com, instagram.com, tiktok.com — because the lease honours the package, and a browser package hosts the whole web. A peek meant for checking one app becomes a blanket amnesty for all 8 blocked domains at once.

### [CRITICAL] The biggest feed is not on the list: youtube.com home/watch feed is fully open in the browser — only the /shorts path is blocked
*Location:* `app/src/main/java/com/example/data/AppCatalogReconcile.kt:62; app/src/main/java/com/example/data/MonolithModels.kt:508-517; app/src/main/java/com/example/service/MonolithAccessibilityService.kt:112-115`

Chrome is born OPEN: factory preset `"com.android.chrome" to AppRulePreset(AppBlockTier.OPEN, 60)` (AppCatalogReconcile.kt:62), so in a hand-armed session the browser app itself has no time budget at all — the per-app door returns early for OPEN tier (MonolithAccessibilityService.kt:276-277 in step 5). The only fence is the site list, and the factory list is exactly 8 entries (MonolithModels.kt:508-517): instagram.com, tiktok.com, reddit.com, x.com, twitter.com, facebook.com, snapchat.com, and only 'youtube.com/shorts' for YouTube. Plain youtube.com — home feed, subscriptions, recommendations, /watch — matches nothing, and the Shorts/Reels sniper that guards the app cannot fire in a browser because it is package-gated to the YouTube and Instagram apps only (MonolithAccessibilityService.kt:112-115). The user opens Chrome for one search, taps a video, and lands in the full algorithmic YouTube feed for the rest of the session: no app block, no sniper, no site block, no timer.

### [HIGH] A second browser is catalogued as SHOPPING, born OPEN, and invisible to the site blocker — an unpoliced corridor already on the phone
*Location:* `app/src/main/java/com/example/service/MonolithAccessibilityService.kt:368-376; app/src/main/java/com/example/data/MonolithRepository.kt:525-527; app/src/main/java/com/example/data/AppCatalogReconcile.kt:88-92`

The site blocker only runs for 5 hard-coded packages — Chrome, Brave, Firefox, Edge, Samsung Internet (MonolithAccessibilityService.kt:368-376). A second browser the user already has installed (Opera, DuckDuckGo, Kiwi, Via) is also invisible to the catalogue's browser detection: categorizePackage only recognises names containing chrome/browser/firefox/brave (MonolithRepository.kt:525-526) and everything unrecognised falls through to `else -> AppCategory.SHOPPING` (MonolithRepository.kt:527). In reconcile, a SHOPPING-category app with no preset is born OPEN (AppCatalogReconcile.kt:88-92). Lived result: the user switches to the other browser mid-session and gets the entire web — blocked domains included — with zero intercepts, zero site checks, and a Shield list that files that browser under Shopping, Open. Nothing anywhere tells the user this browser is unpoliced.

### [HIGH] Blocked sites have no peek: the web is unlimited-or-nothing — allowed sites run all session, a needed blocked page can never be knocked for 2 minutes
*Location:* `app/src/main/java/com/example/service/MonolithAccessibilityService.kt:56-57; app/src/main/java/com/example/ui/MonolithViewModel.kt:142-152`

Apps get three tiers (LOCKED / PEEK_2M / OPEN, MonolithModels.kt:19-33), but sites get no tier at all. A site intercept fires with InterceptPath.SITE (MonolithAccessibilityService.kt:260-268), and `canHonourPeekLease` is true only for APP_TIER and SHIELD_APP (MonolithAccessibilityService.kt:56-57). peekOfferFor therefore can never offer a peek on the site door (MonolithViewModel.kt:142-152), and the withheld-reason logic prints nothing at all when the door cannot honour a lease (MonolithViewModel.kt:2337) — the airlock does not even explain that no peek exists. So the lived web is binary and inverted: every allowed site is unlimited for the whole session, while one blocked page the user genuinely needs (e.g. a Reddit answer surfaced by their study search) is absolutely unreachable without ending the session — no 2-minute knock, no cooldown, no exception path.

### [MEDIUM] Receipt claims 'HELD N SITES' even when the browser blocker was switched off for the entire session
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:1385-1390; app/src/main/java/com/example/service/MonolithAccessibilityService.kt:254; app/src/main/java/com/example/ui/components/ReceiptSheet.kt:129-131`

At session birth the receipt's site count is captured as the raw list size with no reference to the enforcement toggle: `val heldSites = dao.getAllSitesSnapshot().size` inside the seal transaction (MonolithRepository.kt:1385-1390). Enforcement, however, checks the toggle per event: `(guardSettings?.browserBlockerEnabled ?: true)` (MonolithAccessibilityService.kt:254). A user who switched 'SOCIAL SITES' off in PARK therefore browses the whole session with the site blocker doing nothing, yet the receipt still prints `HELD ${heldAppsAtSeal} APPS · ${heldSitesAtSeal} SITES` (ReceiptSheet.kt:129-131) — claiming 8 sites were held that were never once checked.

---

## Angle: shorts-reels-lived (4 findings)

**Verdict:** As lived, the sniper is mostly absent where it claims to stand guard: it is redundant under the default whole-app locks, deliberately switched off during the one sanctioned YouTube peek (turning it into a 2-minute Shorts pass), blind to everything in YouTube/Instagram except the vertical feed itself, and when it does fire it fires on the mere presence of a shelf or tab — not on a short actually playing.

### [CRITICAL] The sanctioned YouTube peek is a 2-minute Shorts pass: a live peek lease silences the sniper for exactly the window the user is inside the app
*Location:* `app/src/main/java/com/example/service/MonolithAccessibilityService.kt:218-241`

MonolithAccessibilityService.kt:218-231 computes leaseLive (row tier == PEEK_2M and elapsedRealtime < activePeekUntilElapsedRealtime) and gates the sniper with 'if (!leaseLive && shortsSniperArmed(guardSettings, pkgName))' — the code's own comment (lines 218-224, UNA2-01) says 'a live peek lease on THIS package silences the sniper and the site blocker too'. YouTube's factory tier is exactly PEEK_2M (AppCatalogReconcile.kt:55, and the STUDY loadout also sets YouTube to PEEK_2M at MonolithRepository.kt:1133), so the one sanctioned way into YouTube mid-session is a peek lease — during which the user can tap the Shorts tab and scroll Shorts freely until the 120-second lease expires (MonolithRepository.kt:106). This directly contradicts the service's own header claim at MonolithAccessibilityService.kt:26-28 and line 230 that the sniper 'Remains strictly active even during Safe Breaks' / is 'Strictly active during both Work Blocks and Safe Breaks'. PARK still prints SHORTS + REELS ON (ParkDrawer.kt:547-562) while it is off.

### [HIGH] In the default configuration the PARK sniper toggle is a lived no-op in both directions: Instagram is locked whole-app before the sniper can matter, YouTube is tier-gated, and OFF changes nothing the user feels
*Location:* `app/src/main/java/com/example/service/MonolithAccessibilityService.kt:230-243,281-310`

Sniper branch (MonolithAccessibilityService.kt:230-243) runs before the tier branch (lines 281+), but detection requires the user to already be on a Shorts/Reels feed. Instagram's factory preset is LOCKED whole-app (AppCatalogReconcile.kt:54) and the STUDY loadout locks SHORT_VIDEO/SOCIAL categories (MonolithRepository.kt:1136-1139), so the tier door intercepts Instagram at launch on any screen — the user meets 'Locked', never 'Reels Blocked', except the edge case of Instagram restoring directly onto its Reels tab. YouTube is PEEK_2M-gated by the tier door, and once the peek is taken the sniper is silenced (see lease finding). Symmetrically, flipping the PARK sled OFF (ParkDrawer.kt:534-583 -> MonolithViewModel.kt:1484-1492 -> MonolithRepository.kt:821-829) changes nothing the user experiences under the default loadout: Instagram stays locked whole-app and YouTube stays peek-gated by tiers that never consult the sniper switch (shortsSniperArmed is only called at MonolithAccessibilityService.kt:231).

### [HIGH] Everything except the vertical feed is unpoliced: with YouTube/Instagram set OPEN, unlimited long-form video, home feed, Stories and DMs run all session under a SHORTS + REELS ON board
*Location:* `app/src/main/java/com/example/service/MonolithAccessibilityService.kt:106-115,333-367`

shortsSniperArmed (MonolithAccessibilityService.kt:112-115) only ever arms for com.google.android.youtube and com.instagram.android, and detectShortsOrReelsFeed (lines 333-355) fires only on 5 named view IDs or a selected bottom tab whose text/description is exactly 'shorts'/'reels'. Nothing in the sniper path checks duration, usage, or any other surface: an OPEN-tier YouTube can play unlimited long-form video, search and subscriptions, and an OPEN-tier Instagram can scroll its home feed, Stories, DMs and Explore grid for the entire session with zero intercepts, because none of those screens contain a listed ID or a selected Shorts/Reels tab. The sniper is the only feed-specific guard — the tier door below it (lines 281+) returns immediately for OPEN apps ('if (tier == AppBlockTier.OPEN) return@launch').

### [MEDIUM] The sniper punishes proximity, not watching: any listed view ID present anywhere in the window tree, or a merely-selected 'Shorts' tab, triggers BACK plus the full airlock
*Location:* `app/src/main/java/com/example/service/MonolithAccessibilityService.kt:333-367`

detectShortsOrReelsFeed (MonolithAccessibilityService.kt:336-345) returns true if findAccessibilityNodeInfosByViewId finds any listed ID anywhere in the root tree — including 'com.google.android.youtube:id/reel_recycler', the Shorts shelf container — with no isVisibleToUser, playback or foreground check anywhere in the function. Strategy B (lines 357-367, isSelectedBottomTabShortsOrReels) fires on any node in the whole tree with isSelected and text/description 'shorts' or 'reels', e.g. merely opening a channel's Shorts tab grid or a selected 'Shorts' search filter chip. Either trigger runs the full punishment at lines 232-241: performGlobalAction(GLOBAL_ACTION_BACK) plus the Monolith airlock intercept (InterceptPath.SNIPER, which per MonolithViewModel.kt:147 / InterceptPath.canHonourPeekLease offers no peek) — identical treatment for scrolling past a shelf or landing on a tab grid as for actually watching a Short.

---

## Angle: music-during-focus (4 findings)

**Verdict:** Studying with music works only by accident: Spotify survives because a substring miss leaves it OPEN, while YouTube Music and 'Player' apps are locked, lose their notification controls, and cannot lawfully be started mid-session at all.

### [HIGH] Media playback notification is treated as a held message: controls cancelled for any non-OPEN player, song stored in the digest
*Location:* `app/src/main/java/com/example/service/MonolithNotificationListener.kt:59-93`

MonolithNotificationListener.kt:59-61 exempts only Notification.CATEGORY_CALL and CATEGORY_ALARM ('Never suppress incoming cellular or VoIP call notifications'); there is no CATEGORY_TRANSPORT/media exemption and no peek-lease or Safe-Break check anywhere in the file. At :76-93 any non-OPEN app's notification with a title/text is inserted into captured_notifications and cancelNotification(sbn.key) is called — a media notification's song title/artist passes the :66 title/text gate, so for a LOCKED/PEEK player every track post/update strips the shade/lock-screen pause/skip controls and files the song as a 'held' message in the digest. An OPEN player's controls survive untouched — same listener, opposite experience, decided only by tier.

### [HIGH] Forgot to press play before sealing = no lawful way to start music: LOCKED players get no peek and Safe Break does not free them, despite background playback never being blocked
*Location:* `app/src/main/java/com/example/service/MonolithShieldService.kt:167-173; app/src/main/java/com/example/service/MonolithAccessibilityService.kt:275-284`

Both doors police foreground only: MonolithShieldService.kt:167-173 builds the blocked set as OPEN=false, PEEK=false only on Safe Break or live lease, else true; MonolithAccessibilityService.kt:278-284 repeats it ('Safe Break Rule: 2m Peek apps unlock freely... while Locked apps stay locked'). No code pauses/stops another app's background audio, so music started before sealing keeps playing even for a LOCKED app — but opening that app to press play, change playlist or pick a podcast fires the Airlock, LOCKED gets no peek offer (MonolithViewModel triggerAirlock: peek withheld unless resolvedTier==PEEK_2M), and a Safe Break does not free it either. The only exits are ending the session or the weekly emergency valve. An OPEN-tier user opens their player freely mid-session. No UI string anywhere mentions music/podcasts or warns 'start playback before you seal' (zero music references in the UI code).

### [HIGH] No music category at all: your player's tier is a substring lottery — YouTube Music born LOCKED, '…Player' apps classified as GAMES, Spotify accidentally OPEN
*Location:* `app/src/main/java/com/example/data/MonolithModels.kt:222-229; app/src/main/java/com/example/data/MonolithRepository.kt:514-527,1132-1149; app/src/main/java/com/example/data/AppCatalogReconcile.kt:53-64,89-91`

MonolithModels.kt:222-229: AppCategory has only SHORT_VIDEO, SOCIAL, GAMES, MESSAGING, BROWSERS, SHOPPING('Other Apps') — no audio/music category exists. MonolithRepository.kt:514-527 categorizePackage is substring roulette: contains('youtube')->SHORT_VIDEO, contains('play')->GAMES (so any app labelled 'Music Player'/'Podcast Player' is GAMES), else SHOPPING. Consequences: com.google.android.apps.youtube.music (contains 'youtube') is SHORT_VIDEO with no factory preset (AppCatalogReconcile.kt:53-64 presets cover com.google.android.youtube=PEEK_2M but not YouTube Music), so AppCatalogReconcile.kt:89-91 births it LOCKED; Spotify (com.spotify.music) falls through to SHOPPING and is born OPEN. applySmartLoadout (MonolithRepository.kt:1132-1149) then disagrees with itself: STUDY gives the 'youtube' string PEEK_2M, WORK_SPRINT gives SHORT_VIDEO LOCKED — YouTube Music flips PEEK vs LOCKED by loadout, while 'player'-labelled apps are LOCKED in both.

### [MEDIUM] SLEEP routine silently re-tiers your music player to LOCKED, overwriting your hand-set OPEN — no audio exception exists
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:849-850,1086-1089,1152-1161`

MonolithModels.kt (SmartLoadout.SLEEP, 'Full Lockdown'): 'Blocks every distracting app except Phone, Clock, and Maps'. MonolithRepository.kt:1152-1161 implements exactly that: only names containing dialer/phone/clock/alarm/maps stay OPEN, everything else LOCKED — no audio exception and BlockedAppEntity carries no allow-in-background/audio flag. The seeded SLEEP routine (MonolithRepository.kt:849-850, 20:00-03:30, ALL_DAYS, enabled) auto-applies it: materialiseRoutineSession at :1086-1089 calls applySmartLoadout(loadout) before startSession, bulk-rewriting every row via dao.insertApps — so a hand-set OPEN on Spotify/a podcast app is silently overwritten to LOCKED when the night window materialises, killing sleep-podcast and night-study music control, and flipped back by the next STUDY loadout.

---

## Angle: study-on-phone-itself (5 findings)

**Verdict:** For a student whose material lives on the phone, Monolith cannot express 'this app is my study': classification is name-substring luck with no education category and no correction path, the Deep Study loadout rations YouTube/Chrome lectures to 2 minutes per 22, the one hand-fix is ratchet-locked mid-session and silently revoked by the next routine, and the advertised allowed-study-sites list is consumed by nothing.

### [HIGH] The 'Deep Study' loadout puts the study surfaces themselves on a 2-minute ration: YouTube lectures and Chrome-hosted classes are unusable in the session built for them
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:1133`

MonolithRepository.kt:1133 — STUDY branch: `lower.contains("youtube") || lower.contains("chrome") || app.category == AppCategory.MESSAGING.name -> AppBlockTier.PEEK_2M`, unconditionally, by name substring. A peek is 120s with a mandatory 20-minute cooldown (MonolithRepository.kt:107-108: PEEK_DURATION_MILLIS = 120*1000, PEEK_COOLDOWN_MILLIS = 20*60*1000). So a 50-minute recorded lecture, Google Classroom, or a PDF open in Chrome gets 2 minutes per 22. It even demotes Chrome below its own factory preset of OPEN/60m (AppCatalogReconcile.kt:63). The loadout's own subtitle (MonolithModels.kt:46-50) sells STUDY as the study configuration; for a student whose material lives in YouTube/Chrome it blocks the material, not the distraction.

### [HIGH] There is no education category and no way to correct one: study apps are filed by name-substring luck, permanently
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:514-529`

AppCategory has exactly six values and no study/education entry; the catch-all is literally SHOPPING("Other Apps","Tools") (MonolithModels.kt:222-228). categorizePackage (MonolithRepository.kt:514-529) guesses from substrings of package+label: contains "play" -> GAMES (line 521), so Google Play Books is a 'game' and is born LOCKED (AppCatalogReconcile.kt:89: GAMES -> LOCKED); contains "reel" -> SHORT_VIDEO (line 517); Anki (com.ichi2.anki), YPT and every PDF reader match nothing and fall to SHOPPING (line 527). No production code lets the user change an existing app's category — grep finds no category write outside discovery/addCustomApp; the only per-app verb is tier cycling (cycleAppBlockTier). The mis-filing is therefore forever, and every category-driven mechanism (loadouts, profiles, Shield filter chips) inherits the wrong answer.

### [HIGH] A mis-tiered study app discovered mid-session cannot be rescued, and a rescue made between sessions is silently revoked by the next routine
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:1126`

Two locks on the same door. (1) Mid-session: MonolithViewModel.setAppBlockTier refuses any weakening while a session is active — 'ONE-WAY RATCHET ENGAGED • CANNOT WEAKEN ... DURING ACTIVE VAULT' (MonolithViewModel.kt, setAppBlockTier, isLockedSession branch) — so if the STUDY loadout shut your PDF reader, you learn it only by hitting the airlock and cannot fix it until the session ends. (2) Between sessions: applySmartLoadout rewrites EVERY app row from category/name rules with no per-app exception or pin (MonolithRepository.kt:1126-1178, dao.insertApps(updated) over allApps), and routines re-apply it automatically at every window materialisation (MonolithRepository.kt:1089, materialiseRoutineSession). The OPEN you hand-set on Anki/YouTube survives exactly until the next loadout tap or routine window, with no notice.

### [MEDIUM] The shipped 'allowed study sites' list is dead decoration: no study-site allowlist exists anywhere in enforcement
*Location:* `app/src/main/java/com/example/data/MonolithModels.kt:518`

BlockedBrowserDomains.allowedStudyExamples (MonolithModels.kt:518-524) names scholar.google.com, arxiv.org, wikipedia.org, canvas.instructure.com, leetcode.com — the exact sites a phone-student needs. Grep across app/src/main shows zero production consumers; the only other reference is a test asserting the list contains scholar.google.com (ExampleRobolectricTest.kt:190). The browser blocker is blocklist-only (addBlockedSite/removeBlockedSite, MonolithRepository.kt), so there is no way to declare 'this site is my study and always passes' — and per the finding above, the browser that would carry those sites is itself peek-capped under STUDY anyway.

### [MEDIUM] Under SLEEP, the study kit is locked with the games: only telephony/clock/maps name-matches survive, so nightly Anki revision is impossible
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:310`

applySmartLoadout's SLEEP branch (MonolithRepository.kt, SmartLoadout.SLEEP) opens only apps whose name/package contains dialer/phone/clock/alarm/maps and LOCKs everything else — Anki, YPT, PDF readers and Play Books alike, because as 'Other Apps'/GAMES they have no exemption class. The seeded 'Sleep Loadout' profile does the same via category: blockedCategoriesCsv = "SHORT_VIDEO,SOCIAL,GAMES,SHOPPING,BROWSERS" (MonolithRepository.kt:310), and applyProfile LOCKs every SHOPPING app. A student doing spaced-repetition revision before bed — the canonical Anki use — must either abandon the session or abandon the revision; the app offers no 'this one is study, let it live at night too' expression.

---

## Angle: phone-call-mid-session (4 findings)

**Verdict:** An ordinary mid-session call is invisible to the session's clock and record: the call's minutes count as clean focus, the session (and its Safe Break) can end and unseal the phone mid-conversation, and the much-advertised call bypass only helps with calls other people start — the user cannot dial out past a locked Phone app.

### [HIGH] Every minute on the phone call is booked as focused time and certified ELAPSED_CLEAN
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:1950,2063-2072`

updateSessionProgress accrues verifiedElapsed purely from the monotonic clock (MonolithRepository.kt:1939-1950 computeVerifiedSessionElapsed) and stamps the outcome at MonolithRepository.kt:2063-2072: ELAPSED_CLEAN if only the shield beat is fresh. The call-state check exists in exactly two places in the whole app — MonolithRepository.kt:566 (blocking query) and MonolithAccessibilityService.kt:190 (blocking door) — and neither feeds accrual, finish classification, or routine judgement. A user who spends 50 of a 60-minute session talking on the phone gets a clean receipt (and a routine window judged HELD) for work they never did; the record cannot distinguish a call from focus because it never looked.

### [HIGH] Session finishes and unseals the phone underneath a live call — user finds out only at hang-up
*Location:* `app/src/main/java/com/example/service/MonolithShieldService.kt:108-122`

The shield loop calls updateSessionProgress every 2s with no call-state gate (MonolithShieldService.kt:108); when the session completes mid-call it posts 'Session Complete' / 'Unlocked desk toy ready in your Break tab' and stopSelf()s (MonolithShieldService.kt:109-122) — a notification sitting behind the in-call UI the user is staring at. Blocking, the seal, and (for routines) the SESSION_EDGE finalisation (MonolithShieldService.kt:112-115) all proceed underneath the conversation. The user hangs up to discover the session they thought they were still in ended twenty minutes ago and the phone has been wide open since.

### [MEDIUM] The 10-minute Safe Break drains in real time during the call and can be fully spent before hang-up
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:1992-2018,2040-2056`

When a block's target is reached, updateSessionProgress flips the row to isInSafeBreak=true with SAFE_BREAK_DURATION_SECONDS regardless of any call in progress (MonolithRepository.kt:2040-2056), and the break balance is wall-derived from (remaining, lastTick) so it drains in real time on every subsequent tick (MonolithRepository.kt:1992-2018). A call that straddles the block boundary consumes the break the user earned: hang up after a 12-minute call and the 10-minute break is already gone and the next block has started without them.

### [MEDIUM] Bypass only covers calls that already exist — dialling out to a LOCKED Phone app is blocked like any distraction
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:548-562,1153-1159`

The bypass keys on AudioManager modes MODE_RINGTONE / MODE_IN_CALL / MODE_IN_COMMUNICATION (MonolithRepository.kt:552-562; MonolithAccessibilityService.kt:190,297) — states that exist only once a call is already ringing or connected. To place a call the user must first open the Phone app while the mode is still MODE_NORMAL, so a Phone app set to LOCKED is intercepted by the per-app door (MonolithAccessibilityService.kt branch 5) exactly like a social app. The only standing dialer exemption is the SLEEP loadout's name-substring heuristic (MonolithRepository.kt:1153-1159); STUDY and WORK_SPRINT contain no dialer rule at all, so the phone survives only if the user happened to leave it OPEN. The documented promise 'incoming cellular, WhatsApp, or Telegram calls are never blocked' (MonolithRepository.kt:548-550) silently excludes every call the user initiates.

---

## Angle: back-to-back-sessions (4 findings)

**Verdict:** The app treats every session as a lonely one: the seam between sessions is a mandatory receipt dismissal plus seconds of locked-slide ceremony, arming the next session erases the previous receipt forever, and if the process dies during the break the next session silently re-arms with factory dial/loadout/block settings instead of the ones the day was built on.

### [HIGH] Arm setup (dial, loadout, blocks) is process-memory only: a process death during the break silently re-arms session 2 with different parameters
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:217-227,369,745-791,1640-1644`

The whole arm configuration lives only in ViewModel memory with hardcoded initializers: MonolithViewModel.kt:217 `_dialMinutes = MutableStateFlow(50)`, :220 `_studyBlocksCount = MutableStateFlow(2)`, :226 `_intentionText = MutableStateFlow("Finish Chapter 4 Problem Set")`, :369 `_selectedProfileName = MutableStateFlow("Study Loadout")`. The init block (MonolithViewModel.kt:745-791) restores none of them from the database, even though the finished session's row persists its profileName, duration and totalStudyBlocks. The only restore path is the activeTask collector (:786-791) re-seeding intention and — via seedDialFromTask (:1640-1644), only when the task has an estimate > 0 — the dial; the loadout/profile and block count are restored by nothing, anywhere. The break between sessions is precisely when the user uses other apps and Android/OEM reclaimers kill Monolith's process, so session 2 can silently arm at 50 minutes under 'Study Loadout' when session 1 was 90 minutes under a custom loadout, with no word that the setup changed.

### [MEDIUM] Starting the next session permanently destroys the previous session's receipt — no surface can ever re-issue it
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:2197-2202; app/src/main/java/com/example/data/ReceiptState.kt:10-16`

armMonolithSession's first act is to destroy the standing receipt: MonolithViewModel.kt:2197-2202 — 'the standing receipt and its sled gate clear at the next arm — the slip stood until now', `_receiptState.value = null`. The store is single-slot by contract (ReceiptState.kt:10-16: 'slot pointer = lastReceiptSessionId... What persists is exactly a number, a pointer, an outcome word'). The one facade that could re-issue a past session's slip, publishReceipt(sessionId) (MonolithViewModel.kt:1349), has zero UI callers — grep finds no call site outside the repository/finish path — and no history surface re-opens a receipt. So the user who slides straight into the next session (the habit the receipt's tap-anywhere dismissal trains) permanently loses the previous slip: its deflection list and held counts exist nowhere else and can never be re-assembled for a claimed session.

### [MEDIUM] The receipt is a zero-action full-screen gate and the slide stays physically locked for seconds after every finish — no 'next session' door exists
*Location:* `app/src/main/java/com/example/ui/components/ReceiptSheet.kt:71-76; app/src/main/java/com/example/MainActivity.kt:776-786; app/src/main/java/com/example/ui/views/BlockitCoreViews.kt:1632`

ReceiptSheet.kt contains exactly one interaction: the scrim's onDismiss (lines 71-76); there is no button of any kind — no 'again', no next-session door. It composes full-screen over the Deck at the root (MainActivity.kt:776-786), so the slide underneath is unreachable until it is dismissed. Before it even appears, detention freezes the Deck — TactileDeckScreen.kt:273-276: 'the Deck renders the session's last frame — frozen readout, frozen progress, the slide still seated — and nothing else moves' — until E+1480-1800ms (ReceiptState.kt Detention RECEIPT_FLOOR_MS/RECEIPT_CEILING_MS). After dismissal the slide is still locked: the sled return only starts at receipt-publication + 1050ms (TactileDeckScreen sled LaunchedEffect) and runs 650ms (BlockitCoreViews.kt:1632, anim.duration = 650L), with sealedGate clearing only at the animator's end. Every boundary in a study day costs a mandatory dismissal tap plus roughly 2.5-3.5 seconds of dead ceremony, repeated identically, and nothing anywhere acknowledges that another session might follow.

### [LOW] A finished session never advances the task it was for — session N+1 silently defaults to re-studying the same task
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:1159-1315,1700-1710; app/src/main/java/com/example/MainActivity.kt:501,609`

Task completion is a manual act wired only to explicit taps (MainActivity.kt:501 onCompleteActiveTask, :609 onCompleteTask). The finish controller (MonolithViewModel.kt:1159-1315) publishes the receipt and never touches the task queue, while the activeTask collector (:786-791) keeps _intentionText pinned to the still-active task's title and re-seeds the dial from it. So after a session in which the work was actually finished, the Deck quietly prepares to study the same task again; advancing requires leaving the Deck, finding the task in the Tasks room, completing it there, and returning to re-arm. The receipt — whose title IS the session's intention (ReceiptState.kt PendingReceipt.title = row.intention) and which is the natural seam moment — offers no 'done with this' action.

---

## Angle: sunday-review (5 findings)

**Verdict:** On Sunday evening the app cannot judge the week with the user: the verdicts on failed days are written to the database and read by no screen, the Drawer shows only completed tasks in deliberate silence, and the one weekly aggregate uses different day boundaries and counts failures as focus.

### [HIGH] The failed day's verdict is recorded in the DB but no review surface ever reads it — Sunday review cannot show MISSED, PARTIAL or shield-gap at all
*Location:* `app/src/main/java/com/example/data/MonolithModels.kt:313-355; app/src/main/java/com/example/ui/components/ReceiptSheet.kt:124`

Every session row carries an immutable judgement: routineOutcome 'ATTENDED / PARTIAL / MISSED / LATE_FIRE' (MonolithModels.kt:313-314), a classified outcome (MonolithModels.kt:344-346), shieldGapSeen and an evidence mask (MonolithModels.kt:316-318, 352-355). No screen reads any of it: grep of app/src/main/java/com/example/ui and MainActivity.kt finds zero references to routineOutcome, and the only outcome print in the app is ReceiptSheet.kt:124 ('ENDED ${hhmm} · ${receipt.outcomeWord}') for the single current receipt. On Sunday, last Tuesday's MISSED verdict exists in the database and nowhere on any screen the user can open.

### [HIGH] Drawer stores only COMPLETED tasks, so a failed day leaves no trace — its absence looks identical to a day off
*Location:* `app/src/main/java/com/example/data/MonolithDao.kt:207-210; app/src/main/java/com/example/ui/screens/DrawerScreen.kt:54-60`

Drawer reads are task-only and success-only: getStoredPage and getStoredMonths filter "status = 'COMPLETED'" on focus_tasks (MonolithDao.kt:207-210), never focus_sessions. The screen's own header declares the intent: 'It is not a dashboard, report card or attendance book: no totals, no streaks, no percentages, no counts, no summons' (DrawerScreen.kt:54-60). A day of hard focus that completed no task, or a day that failed outright, produces no heading and no row, and the landing deliberately renders an empty current month 'in SILENCE' (DrawerScreen.kt, LANDING branch comment). On Sunday that silence is indistinguishable from a rest day, a data loss, or a week the app was never used.

### [MEDIUM] The only way to explain a bad day — the day word — can only be declared on that day itself, never retroactively at Sunday review
*Location:* `app/src/main/java/com/example/ui/screens/DrawerScreen.kt DayHeading (isToday gate); app/src/main/java/com/example/data/DrawerDay.kt:20-29`

The one explanation mechanism the Drawer offers — a declared day word (REST / ILL / FAMILY) beside the date heading — is rendered only on today's heading and only while unsealed: DayHeading gates the tappable words with 'if (isToday && !isSealed)' (DrawerScreen.kt, DayHeading). Past days show a stored word as print but offer no way to add or change one. A user reviewing on Sunday evening cannot file 'ILL' against Thursday; the explanation window for that day closed at the next 03:30 boundary (DrawerDay.kt:20-29).

### [MEDIUM] The week surfaces cannot agree: 7-day stats use rolling calendar-midnight/start-day bucketing, the Drawer uses the 03:30 filing rule, and neither uses MonolithWeek's Monday anchor
*Location:* `app/src/main/java/com/example/data/MonolithWeek.kt:8-17; app/src/main/java/com/example/data/MonolithRepository.kt:2379-2390; app/src/main/java/com/example/data/DrawerDay.kt:27-29`

MonolithWeek.kt claims the week is 'defined once... one fact, one definition' for 'any future weekly surface', anchored Monday 00:00. compute7DayHistory ignores it: it takes a rolling 6-days-ago-to-today window, buckets sessions by calendar midnight with a fixed endOfDay = startOfDay + 86_400_000L, and credits a session entirely to its start day (MonolithRepository.kt:2379-2390) — a 23:00-01:00 session is wholly day one. The Drawer files the same lived day by a different rule (03:30 filing, DrawerDay.kt:27-29) and by task completion instant, and its minutes are task actualSecondsSpent, not session elapsedSeconds. Three surfaces, three different 'days', two different clocks: totals cannot be reconciled against each other.

### [MEDIUM] The 7-day aggregation counts failed sessions as full focus minutes and inflates 15 minutes into a '25-minute block'
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:2365-2392`

compute7DayHistory's only filter drops unfinalised routine rows (MonolithRepository.kt:2365-2372); outcome is never consulted, so a shield-gap or early-broken session contributes its full elapsedSeconds to that day's totalMinutes exactly like a clean session. The block count then rounds up: blocks25 = (totalMins / 25).coerceAtLeast(if (totalMins >= 15) 1 else 0) (MonolithRepository.kt:2390) — a 15-minute day counts as a full 25-minute focus block. Even if this data were displayed, the week's numbers would paint the failed day as ordinary focused time.

---

## Angle: sick-day-word (5 findings)

**Verdict:** As lived, the day word is a caption, not an excuse: hidden in the history archive, locked out by the very routine session illness triggers, limited to three permissible reasons, and powerless to stop that day's routines from firing, blocking, and permanently recording MISSED.

### [CRITICAL] Declaring ILL changes nothing: routines still auto-start blocking sessions on the sick day and the window is still judged MISSED
*Location:* `app/src/main/java/com/example/data/DayWordStore.kt:5-8; app/src/main/java/com/example/data/RoutineDerive.kt:82-90,199-233; app/src/main/java/com/example/service/RoutineHeartbeat.kt:139-155`

DayWordStore.kt:5-8 defines the feature as pure print: 'his authored note for a date, printed beside that date's heading in the Drawer. It is a word, never a number, and it is never folded into any total.' Nothing else consumes it: grep shows dayWords/allDayWords flow only into DrawerScreen (MainActivity.kt:633) and storedMonths. RoutineDerive.derive() (RoutineDerive.kt:82-90) takes only routines, now, activeSession, sessions, settings — no day-word input exists to give. judgeLatestClosed (RoutineDerive.kt:199-233) grants amnesty for a paused window (J6, line 208: 'a day he paused in advance cannot be a miss') but has no ILL/REST/FAMILY case, so an unclaimed window on a declared sick day falls to J4 MISSED (line 224-227). RoutineHeartbeat.kt:139-155 then materialises the desired blocking session for the window anyway. The pause feature excuses a window; the sick-day word — the feature that looks like the excuse — excuses nothing.

### [HIGH] The declaration is locked out precisely when he is sick: any active session seals the Drawer, and yesterday can never be declared after the fact
*Location:* `app/src/main/java/com/example/MainActivity.kt:243,634; app/src/main/java/com/example/ui/screens/DrawerScreen.kt:836-839,863; app/src/main/java/com/example/ui/MonolithViewModel.kt:1951-1968; app/src/main/java/com/example/data/DrawerDay.kt (filingDate)`

MainActivity.kt:243: 'val sealed = activeSession?.isActive == true', passed to the Drawer as isSealed (MainActivity.kt:634). DayHeading renders the tappable words only 'if (isToday && !isSealed)' (DrawerScreen.kt:863); its own doc (DrawerScreen.kt:836-839) says 'While sealed the words are print and answer nothing.' So the routine session that fires because he is ill (see heartbeat materialisation) is exactly what locks the declaration out — he must first end/escape the session to earn the right to say he was sick. There is no catch-up path: declareTodayWord (MonolithViewModel.kt:1951-1968) files only today's filing date — 'only today's filing date is declarable from here — the Drawer files words, it does not rewrite past days.' Worse, filing uses the 03:30 rule (DrawerDay.filingDate subtracts 210 minutes), so a word declared at 02:00 while up sick in the night is filed under yesterday, leaving the actual sick day blank.

### [MEDIUM] The feature is buried in the history archive: VAULT tab, under today's date heading, signposted nowhere a sick user would look
*Location:* `app/src/main/java/com/example/ui/screens/DrawerScreen.kt:61-68,865; app/src/main/java/com/example/MainActivity.kt:357,624-640`

The only place the words exist as controls is inside DayHeading in the Drawer (DrawerScreen.kt:865: 'DayWordStore.WORDS.forEach'), and the Drawer is the VAULT destination — rail stop 3 of 4 (MainActivity.kt:357, 624-640). dayWords is passed to no other screen (MainActivity.kt:633 is the sole UI consumer). Nothing on the Deck — the screen a resting user actually sees — mentions REST/ILL/FAMILY, offers a 'not today' action, or hints the feature exists; the Drawer's own header (DrawerScreen.kt:61-68) describes storage, not a sick-day mechanism. A first-time ill user has no discoverable path: he would have to already know to open his history archive and look under today's date heading.

### [MEDIUM] Guilt by closed vocabulary: REST/ILL/FAMILY are the only permissible excuses — every other legitimate lost day must be mislabelled or left blank
*Location:* `app/src/main/java/com/example/data/DayWordStore.kt:20-21,46-49,55-56`

DayWordStore.kt:55-56: 'val WORDS: List<String> = listOf("REST", "ILL", "FAMILY")' and put() refuses anything else silently (DayWordStore.kt:46-49: 'else -> return'). The class doc (DayWordStore.kt:20-21) is proud of the closure: 'never TEST. This app does not know test days.' A day lost to grief, a work emergency, travel, a mental-health crash, or caring for a parent in hospital has no honest word: FAMILY must stretch to cover both a wedding and a funeral, or the day stays blank — and a blank day is indistinguishable in the Drawer from a day he simply didn't show up. The vocabulary forces either mislabelling or an unexplained gap, which is the guilt outcome the feature claims to soften.

### [MEDIUM] Tomorrow remembers the excuse as decoration and the miss as fact: ILL prints beside the date while the session row is permanently stamped MISSED, completedSuccessfully=false
*Location:* `app/src/main/java/com/example/ui/screens/DrawerScreen.kt:851-857; app/src/main/java/com/example/data/MonolithRepository.kt:1030-1041`

The word's entire afterlife is typographic: DayHeading prints '"${headingLabel(...)} · ${group.word}"' beside the date (DrawerScreen.kt:851-857), permanently, next to a day with no stored rows. Meanwhile the sick day's routine session is finalised independently of the word: finaliseRoutineSession (MonolithRepository.kt:1030-1041) stamps routineOutcome = MISSED and completedSuccessfully = false on the session row when attendance is under the floor, with no reference to any day note. So tomorrow (and every later month view) shows both records side by side, unreconciled: his word 'ILL' as decoration in the Drawer, and the machine's verdict MISSED / not completed as the durable fact — the excuse is remembered, but it acquits nothing.

---

## Angle: digest-too-late (5 findings)

**Verdict:** After the session the 'digest' is a timestamp-less, tap-dead, recency-only slit of text capped at 100 items, where an expired OTP and group-chat noise weigh the same and one unconfirmed CLEAR ALL tap can erase the only copies of all of it.

### [CRITICAL] Post-session digest rows are dead text: tapping a held item goes nowhere, despite the code's own 'tapped to open' promise
*Location:* `app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:410-447; app/src/main/java/com/example/service/MonolithNotificationListener.kt:20-22,92-94; app/src/main/java/com/example/data/MonolithModels.kt:463-471`

Listener class doc promises notifications 'can be read, cleared, or tapped to open after studying' (MonolithNotificationListener.kt:20-22), but CapturedNotificationEntity stores only id/timestamp/package/appName/senderTitle/messageBody/isRead (MonolithModels.kt:463-471) — no PendingIntent/contentIntent is ever saved, the original is destroyed via cancelNotification(sbn.key) (MonolithNotificationListener.kt:92-94), and the digest row Column in CapturedNotificationsDigestSheet has no clickable at all (AirlockAndDisarmOverlays.kt:410-447). The only row/ sheet actions are DEL, CLEAR ALL and CLOSE DIGEST. To reply to the friend, the user must close the digest, open the source app manually, and hunt for the message themselves.

### [HIGH] The post-session (live) digest strips every timestamp, so expired OTPs and hour-old pleas are indistinguishable from fresh ones
*Location:* `app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:425-433; app/src/main/java/com/example/MainActivity.kt:756-768`

In frozen (in-session) mode the row header is "${item.appName} • ${item.senderTitle} • ${captureTimeLabel(item.timestampMillis)}" (AirlockAndDisarmOverlays.kt:425-429), but in live mode — the mode MainActivity.kt:756-768 selects after the session ends (frozenDigest==null, sealed==false -> capturedNotifications) — the header is only "${item.appName} • ${item.senderTitle}" with no time at all. No relative age ('2h ago') exists anywhere in the sheet. The user cannot tell a 2-minute-old OTP from a 3-hour-expired one, or that a friend's message waited an hour, without leaving and reconstructing it elsewhere.

### [HIGH] CLEAR ALL is one unconfirmed tap that irreversibly deletes every held notification ever captured, with no undo
*Location:* `app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:365-368,434-443; app/src/main/java/com/example/ui/MonolithViewModel.kt:1595-1611; app/src/main/java/com/example/data/MonolithDao.kt:167-168`

CLEAR ALL is a plain BlockitActionChip whose onClick is viewModel::clearAllCapturedNotifications (MainActivity.kt:765; MonolithCustomComponents.kt:170-204 chip is a single clickable Box, no confirm state). ViewModel (MonolithViewModel.kt:1595-1603) immediately calls repository.clearAllCapturedNotifications() -> 'DELETE FROM captured_notifications' (MonolithDao.kt:167-168) — the whole table, all apps, all sessions, not just what was viewed. No confirmation dialog, no undo path anywhere in the digest chain, and the per-row DEL is the same pattern: an 11sp bare Text clickable firing onDeleteItem instantly (AirlockAndDisarmOverlays.kt:434-443). One tired mis-tap at the end of a long session permanently destroys the only copies, because the originals were cancelled from the shade during the session.

### [HIGH] No triage at all: flat recency-only list gives group-chat noise exactly the same weight as an OTP or a direct plea
*Location:* `app/src/main/java/com/example/data/MonolithDao.kt:147; app/src/main/java/com/example/data/MonolithModels.kt:463-471; app/src/main/java/com/example/service/MonolithNotificationListener.kt:62-69; app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:403-447`

The DAO's only ordering is 'ORDER BY timestampMillis DESC' (MonolithDao.kt:147). CapturedNotificationEntity has no priority, category, conversation or importance field (MonolithModels.kt:463-471); the listener reads notification.category only to early-return calls/alarms and then discards it (MonolithNotificationListener.kt:62-66), and stores only EXTRA_TITLE/EXTRA_TEXT (lines 68-69). The sheet renders one flat LazyColumn of identical 12sp rows (AirlockAndDisarmOverlays.kt:403-447) with no grouping by app/conversation, no filter, no search, no pinning. A one-time password, a direct message needing a reply, and forty group-chat reactions are interleaved purely by arrival second at identical visual weight.

### [MEDIUM] Post-session digest silently caps at the newest 100 and reports that cap as the total held count
*Location:* `app/src/main/java/com/example/data/MonolithDao.kt:147-159; app/src/main/java/com/example/ui/MonolithViewModel.kt:483; app/src/main/java/com/example/MainActivity.kt:756-768; app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:360-366,404-406`

The live flow is 'SELECT * FROM captured_notifications ORDER BY timestampMillis DESC LIMIT 100' (MonolithDao.kt:147-148) and the post-session digest is fed exactly that flow (MonolithViewModel.kt:483; MainActivity.kt:761 'else -> capturedNotifications'), while the header prints 'HELD NOTIFICATIONS • {notifications.size}' as if it were the total (AirlockAndDisarmOverlays.kt:360-366). The frozen snapshot deliberately has NO LIMIT because 'a held item past any cap is still held' (MonolithDao.kt:150-159) — proving the authors knew the cap lies — yet the unsealed post-session view keeps it. After a long, noisy session the oldest held items (often the earliest, most time-sensitive ones) are silently invisible and uncountable, but CLEAR ALL still deletes them unseen. The list viewport is also hard-capped at 220.dp height (AirlockAndDisarmOverlays.kt:404-406), so even the visible 100 are triaged through a slit.

---

## Angle: custom-profile-lived (5 findings)

**Verdict:** As lived, 'creating a custom profile' is building a labelled promise the app never keeps: the schedule never fires, the categories cannot express the apps the user pictured, and applying it contradicts its own description while erasing the setup they already had.

### [CRITICAL] Custom profile's time window is write-only: saved as 'schedule enabled' but no engine ever reads it, so 21:00 comes and goes with nothing applied or lifted
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:1282-1307`

saveCustomProfile hard-codes isScheduleEnabled=true and a printed label '"%02d:00 – %02d:00 • Custom"' (MonolithRepository.kt:1291-1304), yet grep across service/, RoutineDerive.kt and RoutineHeartbeat.kt finds zero references to FocusProfile/allProfiles — the only profile-schedule code anywhere is toggleProfileSchedule/updateProfileTimeWindow writing the row back (MonolithRepository.kt:1263-1280). Auto-start/stop exists only for the separate RoutineEntity system. The user who builds 'Evenings 19:00-21:00' and trusts the enabled schedule gets nothing at 19:00 and nothing stopping at 21:00; the window is a label, not a mechanism.

### [HIGH] Applying a profile contradicts the profile's own description: Sleep leaves messaging open and locks Phone/Clock/Maps; Study locks the YouTube it promised to peek
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:1243-1261`

Seeded Sleep Loadout promises 'Locks every app except Phone, Clock, and Maps' (MonolithRepository.kt:308) but its CSV omits MESSAGING (line 310), so WhatsApp/Discord stay OPEN under applyProfile's 'app.category in activeCategories' test (lines 1252-1256), while Phone/Clock/Maps match no keyword in categorizePackage and fall into SHOPPING (lines 514-528), which IS in the CSV — so the three named exceptions are LOCKED. Seeded Study Loadout promises 'sets YouTube & Messaging to 2m Peek' (line 282), but applyProfile grants PEEK_2M only to MESSAGING (line 1255); YouTube is categorised SHORT_VIDEO (line 517) and gets LOCKED. The user discovers at 21:00 that the profile did roughly the inverse of its own description on the apps they cared about.

### [HIGH] Creator thinks in six crude categories, not apps: YouTube and Instagram are the same switch, and every unrecognised app hides inside 'Other Apps'
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:2168-2180`

createCustomProfile accepts only List<AppCategory> — no package list, no per-app exceptions (MonolithViewModel.kt:2168-2174). The category an app lands in comes from categorizePackage's substring guessing: tiktok/youtube/instagram/reel are all SHORT_VIDEO (MonolithRepository.kt:517-518), so 'block Instagram, keep YouTube lectures' is inexpressible — one switch moves both. Everything unrecognised falls to AppCategory.SHOPPING, whose user-facing label is literally 'Other Apps' (MonolithModels.kt:222-229): ticking that one category to catch a single stray app silently LOCKs every unclassified app on the phone, and unticking it leaves the stray app open.

### [HIGH] One profile tap silently rewrites the session dial and bulk-overwrites every hand-set per-app tier, with no preview, snapshot or undo
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:2141-2148`

selectAndApplyProfile does three things in one tap with no confirmation or preview: sets the profile name, overwrites _dialMinutes with profile.defaultMinutes, and launches applyProfile (MonolithViewModel.kt:2141-2148), which rewrites EVERY app row — categories not in the CSV are forced to OPEN, erasing hand-tuned per-app tiers via dao.insertApps(updated) (MonolithRepository.kt:1250-1260). There is no snapshot/restore, and the creator cannot capture the user's current per-app setup at all since its only inputs are name/minutes/categories/hours (MonolithViewModel.kt:2168-2174). Trying a profile 'to see what it does' permanently destroys the setup the user built by hand and changes the session length they had dialled.

### [MEDIUM] Windows are whole-hours only with no days: a 20:30 or weekday-only profile cannot be expressed, and editing Sleep's 22:30 start silently rounds it to 22:00
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:1267-1307`

Both the creator and the editor take startHour/endHour as bare Ints (MonolithRepository.kt:1282-1288, 1267-1269) and force startMinute=0/endMinute=0 (lines 1302-1304, 1274-1276); FocusProfileEntity has no days-of-week field at all, unlike routines' daysMask. A pictured '20:30 wind-down, weekdays only' is inexpressible. Worse, editing the seeded Sleep Loadout — the one profile that carries real minutes, 22:30 (line 311-316) — through updateProfileTimeWindow silently rewrites it to 22:00 and replaces its '22:30 – 07:00 • Nightly' label with '• Active Window' (lines 1270-1278).

---

## Angle: daily-limit-ratchet-lived (4 findings)

**Verdict:** As lived, the daily limit is a phantom — preset invisibly, displayed nowhere, and never enforced when hit — while the one real mid-day teacher, the ratchet banner, teaches a false deadline ('UNTIL 00:00') for 3.6 silent seconds and offers no lawful way out.

### [CRITICAL] Hitting the daily limit mid-day does literally nothing: no code path ever compares usage to the limit
*Location:* `app/src/main/java/com/example/service/MonolithShieldService.kt:167-176; app/src/main/java/com/example/service/MonolithAccessibilityService.kt:274-287; app/src/main/java/com/example/data/MonolithModels.kt:238`

Every app carries a dailyLimitMinutes (MonolithModels.kt:238, default 30; presets assign TikTok 15, Instagram 20, YouTube 30 in AppCatalogReconcile.kt:53-64), and real usage is tracked in todayUsageMinutes — but no enforcement path ever compares them. MonolithShieldService.kt:167-176 builds its blocked set purely from resolvedTier, safe-break and peek-lease; MonolithAccessibilityService.kt:274-287 likewise returns early only on tier/peek/safe-break. A repo-wide grep for dailyLimitMinutes finds only the model, the DAO setter, the catalogue presets and one ViewModel guard — zero comparisons against usage. The user who burns through Instagram's 20 minutes at 14:00 gets no block, no intercept, no warning: the app just keeps opening, and the 'limit' is only discovered to be fiction by the failure never arriving.

### [HIGH] The daily limit is invisible and unsettable: silently preset per app, shown nowhere, editable nowhere
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:2121; app/src/main/java/com/example/data/AppCatalogReconcile.kt:53-99`

MonolithViewModel.updateAppDailyLimit (MonolithViewModel.kt:2121-2131) has zero call sites in any screen — grep finds only its own definition plus the repository/DAO plumbing. todayUsageMinutes is never rendered by any composable (no reference under ui/). Meanwhile AppCatalogReconcile.kt:53-64 silently births apps with limits the user never chose (TikTok 15, Twitter 15, WhatsApp 45, Chrome 60) and :99 defaults every other app to 30. The user cannot discover what their limit for an app is, cannot see how much of it they have used, and cannot change it — the entire daily-limit feature exists only as invisible database columns.

### [HIGH] 'CANNOT UNLOCK UNTIL 00:00' is false in both directions — the real gate is session-end, and the four ratchet messages contradict each other
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:2047-2053, 2079, 2124, 2152`

Tapping a LOCKED app mid-session prints 'ONE-WAY RATCHET ENGAGED • CANNOT UNLOCK '<APP>' UNTIL 00:00' (MonolithViewModel.kt:2053). But the actual gate at MonolithViewModel.kt:2047 is only activeSession.value?.isActive == true — there is no midnight check or midnight reset anywhere. So the sentence lies both ways: if the session ends at 18:00 the user can unlock at 18:00, six hours before the promised 00:00; if a session is still active past midnight the app stays locked after the promised release time. The sibling refusals can't even agree on the falsehood: tier-weakening says 'DURING ACTIVE VAULT' (:2079), the quota refusal says 'DURING LOCKDOWN' (:2124), the schedule refusal says 'DURING VAULT LOCKDOWN' (:2152) — three different stated durations for the same session-active gate, so the user cannot learn the real rule from any of them.

### [MEDIUM] The only explanation of a ratchet refusal is a silent 3.6-second banner that names no remedy, no numbers and no end time
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:300-312; app/src/main/java/com/example/ui/screens/ShieldMatrixScreen.kt:252-272`

triggerRatchetWarning (MonolithViewModel.kt:300-312) sets the message and unconditionally clears it after delay(3600L); its own comment admits 'The warning is a print; it sounds nothing.' The banner is 12sp bold text rendered only inside ShieldMatrixScreen (ShieldMatrixScreen.kt:252-272; wired only at MainActivity.kt:562), and the refusal itself changes nothing on screen — the tapped row stays exactly as it was. None of the four messages names a remedy or a fact the user could act on: the quota refusal ('DAILY QUOTA CAN ONLY BE TIGHTENED DURING LOCKDOWN', :2124) does not state the current quota, the value just refused, or when the session ends; no message mentions ending the session, a safe break, or any appeal. A user who looks up a second too late, or blinks, gets a tap that silently did nothing and no persistent record anywhere of why.

---

## Angle: rocker-mistaps (4 findings)

**Verdict:** On Shield, the natural scrolling thumb sits on a 168dp hair-trigger rocker where a flick or an unabsorbed scroll silently and permanently opens an app the user believes is locked — and the app's only way of saying so is an absent glyph and a number, so the user finds out days later, mid-craving, when the 'locked' app simply opens.

### [HIGH] Flick-throw amplifies a 7dp scroll nudge into a two-stop LOCKED-to-OPEN jump
*Location:* `app/src/main/java/com/example/ui/views/BlockitSuitcaseAndSwitchesViews.kt:457,475-482,524-528`

onTouchEvent ACTION_MOVE sets dragging=true once horizontal drift exceeds just dp(6f) (line 457); ACTION_UP then ignores where the thumb actually is if velocity is high: 'velocityDpPerSec > FLICK_DP_PER_SEC -> 2' where FLICK_DP_PER_SEC = 800f and index 2 = AppBlockTier.OPEN (lines 475-482, 233-243, 524-528). The header comment admits it: 'a release faster than ~800 dp/s carries to the far stop.' A quick diagonal scroll-flick that starts on a row's rocker and drifts 7dp rightward before lifting therefore throws a LOCKED app straight to OPEN, skipping PEEK — the maximum possible weakening from the minimum possible gesture. commitTo() fires onTierSelected immediately (lines 287-299); there is no distance-proportional safeguard at all.

### [HIGH] Scroll gestures the list cannot absorb commit a tier: UP is judged on x alone, vertical travel never checked
*Location:* `app/src/main/java/com/example/ui/views/BlockitSuitcaseAndSwitchesViews.kt:431-503; app/src/main/java/com/example/ui/screens/ShieldMatrixScreen.kt:1067-1087`

The view never calls parent.requestDisallowInterceptTouchEvent (other views in the same codebase do, e.g. BlockitSuitcaseAndSwitchesViews.kt:720, BlockitCoreViews.kt:1003), so its only scroll protection is the parent delivering ACTION_CANCEL (lines 492-503). When the LazyColumn cannot take the gesture — a category/search filter has shrunk the catalogue below one screen, or the user pulls down at the top / up at the bottom of the list — no CANCEL ever arrives, yet the view consumed ACTION_DOWN and returns true for every event. The ACTION_UP tap path then computes the stop purely from event.x ('val idx = ((event.x - rLeft) / (rockerW / 3f)).toInt()', line 487) and the drag path from x-velocity/rounded x (lines 475-482): event.y and total vertical travel are never read anywhere in onTouchEvent. A purely vertical scroll attempt ending over the OPEN third of a rocker commits OPEN. The rocker is 168dp wide (line 361) on a 54dp row (ShieldMatrixScreen.kt:1084) — the right half of every row in a long list is this live control, exactly where a right thumb scrolls.

### [HIGH] A mistap is persisted instantly with no confirmation, no undo, and no change record anywhere
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:2066-2092; app/src/main/java/com/example/data/MonolithRepository.kt:1115-1119; app/src/main/java/com/example/data/MonolithModels.kt:232-244`

commitTo() invokes onTierSelected the instant the finger lifts (BlockitSuitcaseAndSwitchesViews.kt:294-299); CatalogueAppRow forwards it to onSetAppTier (ShieldMatrixScreen.kt:1077-1079); MonolithViewModel.setAppBlockTier writes it via repository.setAppBlockTier -> dao.setAppTier with no dialog, no undo and no record (MonolithViewModel.kt:2066-2092, MonolithRepository.kt:1115-1119). The one-way ratchet only refuses weakening while a session is active (MonolithViewModel.kt:2070-2083) — mistaps while browsing Shield outside a session, the normal setup moment, land unopposed and govern every future session and routine. The app's only undo machinery is for deleted tasks (MonolithViewModel.kt:275-282, 1770). The sole feedback on commit is a sound/haptic pitch (fireCommitFeel, lines 275-285), and even the mid-travel crossing tick (noteTravel, lines 246-250) fires during ordinary drags, so the user's ear cannot distinguish 'I scrolled past a row' from 'I just opened Instagram forever.' BlockedAppEntity has no changed-at field (MonolithModels.kt:232-244), so the change leaves no trace to reconstruct later.

### [MEDIUM] Discovery is by absence: OPEN draws nothing, the slip omits OPEN apps, and no surface names what changed
*Location:* `app/src/main/java/com/example/ui/views/BlockitSuitcaseAndSwitchesViews.kt:325-356; app/src/main/java/com/example/ui/screens/ShieldMatrixScreen.kt:170-176,226-238`

onDraw renders OPEN as literal absence: the margin glyph branch is 'AppBlockTier.OPEN -> Unit' (lines 336-338) and the app name is demoted to regular weight in the secondary tone — 'an OPEN app is quiet' (lines 342-345). The standing-orders slip at the top of Shield lists only rules.shut and rules.peek lines (slipLines, ShieldMatrixScreen.kt:226-238), so a flipped app vanishes from the summary entirely; the only aggregate trace is openCount, a bare number inside the tally Triple (ShieldMatrixScreen.kt:170-176) with no names attached. The catalogue is alphabetical and the row does not move when its tier changes, so there is no positional cue either. Days later the user has exactly one way to learn an app they believed SHUT is OPEN: open that app during a session and discover nothing stops them — or audit all ~100 rows by eye, hunting for a missing 8dp square.

---

## Angle: finding-an-app-in-shield (5 findings)

**Verdict:** Finding one app in Shield is a scroll through a non-alphabetical, shifting list using a name-only search that lies about absence, past look-alike rows with no icons to tell them apart — a user who cannot spot their app concludes it is not there or cannot be blocked, and gives up on ruling it.

### [HIGH] Catalogue is sorted by enforcement priority, not alphabet — and an app's place moves as it gets intercepted
*Location:* `app/src/main/java/com/example/data/MonolithDao.kt:13; app/src/main/java/com/example/ui/MonolithViewModel.kt:427-440`

MonolithDao.kt:13 orders the catalogue 'ORDER BY isBlocked DESC, interceptCount DESC, appName ASC', and MonolithViewModel.kt:427-440 filteredApps applies only filter{} — no re-sort. So every SHUT/PEEK app (ordered by how often it has been intercepted) sits above every OPEN app regardless of name, alphabet is only a third-key tiebreak, and an app's position literally migrates upward as its interceptCount grows during use. A user scanning A-to-Z for an OPEN app starting with 'A' must first pass the entire blocked block; a user who memorised where an app 'lives' loses that anchor after the next intercept.

### [HIGH] Search matches only the stored display name, and its no-match line falsely declares installed apps 'not on this phone'
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:433-436; app/src/main/java/com/example/ui/screens/ShieldMatrixScreen.kt:517-522`

MonolithViewModel.kt:433-436: matchesQuery is only app.appName.contains(query, ignoreCase=true) ANDed with the category match. There is no package-name match, no category-word match, no alias ('YT', 'WA'), no fuzzy/prefix tolerance. Worse, ShieldMatrixScreen.kt:517-522 prints 'NO APP BY THAT NAME ON THIS PHONE' whenever filteredApps is empty and the query is non-blank — it never checks selectedCategory, so an installed app hidden only by a still-active category chip (tapped earlier, now scrolled off-screen) is declared not on the phone. Searching a category word like 'messaging' or 'video' likewise finds nothing and delivers the same false verdict.

### [HIGH] Rows carry no icon and no package name — similar-named apps are indistinguishable and long names truncate to identical prefixes
*Location:* `app/src/main/java/com/example/ui/views/BlockitSuitcaseAndSwitchesViews.kt:351-359; app/src/main/java/com/example/ui/screens/ShieldMatrixScreen.kt:1060-1085`

grep finds zero getApplicationIcon/loadIcon/applicationIcon calls anywhere in app/src/main. CatalogueAppRow (ShieldMatrixScreen.kt:1060+) passes only appName and resolvedTier to AppRowSledToggleView, whose onDraw renders nothing but ellipsizeToWidth(appName, namePaint, nameMaxWidth) at 14sp, where nameMaxWidth = width − 168dp rocker − 28dp − 10dp (BlockitSuitcaseAndSwitchesViews.kt:351-359) — roughly 150dp on a typical phone. Distinct packages sharing a label (OEM + Google 'Clock'/'Calculator'/'Camera') render as pixel-identical rows, and long shared prefixes truncate identically ('Google Play servi…' vs 'Google Play Store'). The package name exists only in the invisible testTag. The user sleds a tier onto a twin they cannot tell apart from the target.

### [MEDIUM] Category chips cannot narrow the long tail: keyword collisions misfile apps and everything else dumps into 'Other Apps'
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:514-530; app/src/main/java/com/example/data/MonolithModels.kt:222-229`

MonolithRepository.kt:514-530 categorizePackage is a ~20-keyword substring scan over 'package + label': anything unmatched falls to AppCategory.SHOPPING, whose label prints as 'Other Apps' (MonolithModels.kt:222-229) — banking, maps, camera, gallery, music, food, health, education, i.e. most of a real phone, all in one un-narrowable bucket. The 'play' substring files Google Play Store, Play Books, any 'Player' app, and any label containing 'Display' as GAMES; BROWSERS prints as 'Web & News' yet only matches chrome/browser/firefox/brave, so actual news apps land in Other Apps. On day one, before any staleness, the chips promise six useful slices and deliver two small ones plus a junk drawer.

### [MEDIUM] The search box is buried screens-deep in the page and scrolls away — finding starts with scrolling, and refining means scrolling all the way back
*Location:* `app/src/main/java/com/example/ui/screens/ShieldMatrixScreen.kt:240,435-475,507`

The search field is an ordinary item inside the single LazyColumn (ShieldMatrixScreen.kt:240), placed after the ratchet banner, the LOADOUT selector, the ROUTINES section, and the full YOUR RULES slip — which prints every PEEK and SHUT app again as its own 40dp line — before the 'ALL APPS' header at line 435 and the input at line 453. With a few dozen ruled apps the finder starts multiple screens down. Catalogue rows are 54dp plus 12dp spacing, so a few hundred apps is on the order of twenty screens of scrolling; the input is not sticky/pinned, there is no clear button, no scrollbar, fast-scroll or alphabet index — so once deep in the list, refining the query means scrolling the whole way back up past the slip and routines again.

---

## Angle: night-flip-mid-session (6 findings)

**Verdict:** As lived, the choreographed night flip does not exist: the app snaps to night at row birth before the seal is even proved, snaps back to full day the instant the session ends, and hands the user a near-white receipt and unreadable dark status icons in the dark room the session was meant to protect.

### [HIGH] The t=400 scheme pin is dead code: darkTheme already contains the session, so the flip fires at row birth during proving, not at seal+400ms
*Location:* `app/src/main/java/com/example/MainActivity.kt:103-115; app/src/main/java/com/example/ui/theme/Theme.kt:108-112; app/src/main/java/com/example/ui/MonolithViewModel.kt:70-76,474`

Claim in MainActivity.kt:103-114: session-night 'applies only once the ground has STEPPED (t=0 + 400ms), never at the row's birth and never during proving', via isFocusSessionActive = active && shotGround==GROUNDED. Defeated upstream: MonolithViewModel.kt:70-76 evaluateTheme() returns ThemeEvaluation(true,"AUTO · NIGHT — SESSION ACTIVE") for ANY active session, and MonolithViewModel.kt:474-476 darkTheme is just that evaluation. Theme.kt:108-112 picks the night scheme if (darkTheme || isFocusSessionActive) — the first disjunct is already true. The ViewModel itself notes (sealFreeze comment) the row 'is born active at insert… and t=0 can land up to 1500ms later'. Lived: the whole app goes black the instant the row is inserted, while the seal is still proving and may still jam/annul — on annul the row is deleted and it snaps back to day, a day→night→day strobe before any session exists.

### [HIGH] No surface actually splits at t=0 vs t=400 — every groundNight/effectiveNight also ORs darkTheme, so wheel, slide, rail and other screens flip together at birth
*Location:* `app/src/main/java/com/example/MainActivity.kt:256-262,492-495,564,604,635,706-708; app/src/main/java/com/example/ui/screens/TactileDeckScreen.kt:260-270`

Every 'delayed' consumer ORs the same poisoned flag: MainActivity.kt:256-262 effectiveNight = darkTheme || sealed and groundNight = darkTheme || shotGround==GROUNDED (comment: 'the header band's room and the rail step at t=400'); MainActivity.kt:706-708 rail view.isNightMode = groundNight with comment 'steps with the GROUND (t=400), not at the seal'; TactileDeckScreen.kt groundNight = isDarkTheme || (isSessionRunning && (!shotActive || shot.groundStepped)) where the isDarkTheme passed in (MainActivity.kt:492-495) is the same evaluation-based darkTheme, despite the comment calling it 'the RAW system theme'. Since darkTheme is true from row birth, wheel (RotaryWheelView.isNightMode), slide, rail, Shield/Tasks/Vault (isDarkTheme=effectiveNight, MainActivity.kt:564,604,635) can never wait. There is no moment where hero is night and ground is day, or vice versa, by design — the documented split exists only in comments.

### [HIGH] End flip has no choreography at all: night snaps to full day instantly at session end, inverting even the finish's supposedly held frozen frame
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:70-95,1060-1087; app/src/main/java/com/example/ui/screens/TactileDeckScreen.kt:270-280; app/src/main/java/com/example/data/ReceiptState.kt:201-205`

There is no reverse shot: MonolithViewModel.kt startShotGroundDriver (~1060-1087) sets _shotGround=NOT_SEALED and cancels the job the moment activeSession==null, with no 400ms hold, and evaluateTheme (MonolithViewModel.kt:70-95) returns DAY as soon as session?.isActive is false. TactileDeckScreen.kt: heldFinish = finishState != null && !finishState.issued keeps the readout frozen, but the Deck's effectiveNight = isDarkTheme || isSessionRunning is already false, so the 'held frame' is repainted in day colours while its numbers are frozen. Background jumps NightColorBackground 0xFF171412 (Color.kt:32) to TokenColorBackground 0xFFE4DFDA (Color.kt:11) in one frame, for the whole detention (Detention.RECEIPT_CEILING_MS=1800, ReceiptState.kt:201-205) before the receipt even appears.

### [HIGH] The receipt is always read in day: brightest card in the app (surfaceBright #FAF6F0, 0.12 scrim) presented to night-adapted eyes right after a dark session
*Location:* `app/src/main/java/com/example/ui/components/ReceiptSheet.kt:64-80; app/src/main/java/com/example/ui/theme/Color.kt:15,36`

ReceiptSheet.kt:64-80 renders over a scrim of only Color.Black.copy(alpha=0.12f) on scheme.surfaceBright — in the day scheme that is TokenColorSurfaceBright 0xFFFAF6F0 (Color.kt:15), the brightest token in the app, versus the night hero card's 0xFF362C27 (Color.kt:36) the user was reading seconds earlier. Because the scheme already flipped to day at E0 (see end-flip finding) and the receipt only issues on Detention's formula (up to 1800ms later, MonolithViewModel.kt onFinishE0), there is no path where the slip is read in session-night: a user who sealed in a dark room finishes, gets blasted by the day Deck, then must read ARMED/ENDED/HELD lines on a near-white slip. Rehydrated receipts (runSliceERecovery) likewise always render in whatever the current — normally day — scheme is.

### [MEDIUM] System bars never flip: status bar is hard-coded light-style in onCreate, leaving dark status icons invisible against the night ground all session
*Location:* `app/src/main/java/com/example/MainActivity.kt:73-81`

MainActivity.kt:73-81 onCreate calls enableEdgeToEdge(statusBarStyle=SystemBarStyle.light(TRANSPARENT,TRANSPARENT), navigationBarStyle=SystemBarStyle.dark(TRANSPARENT)) once; grep finds no other enableEdgeToEdge/SystemBarStyle call and no re-invocation on darkTheme/shotGround change anywhere. Light status-bar style means dark system icons/scrim designed for a light ground. During session-night the ground behind the transparent status bar is 0xFF171412, so clock/battery/signal glyphs sit dark-on-near-black for the entire session — the one strip the user glances at to check the time in a dark room is the one surface that never flips. Conversely the dark navigation-bar style is wrong-footed in day.

### [MEDIUM] Brightness shock is by design: no fade, no crossfade and no window-brightness reduction anywhere — every flip is a 0ms palette cut
*Location:* `app/src/main/java/com/example/ui/theme/Theme.kt:100-120; app/src/main/java/com/example/ui/MonolithViewModel.kt:450-476`

Grep over app/src/main/java finds zero animateColorAsState/Crossfade for the scheme, zero screenBrightness/window-brightness code anywhere. The theme evaluation's own comment (MonolithViewModel.kt:~454) boasts re-derivation 'as a cut' / 'a 0ms cut, never a fade', and Theme.kt:108-115 swaps the whole ColorScheme object atomically in MaterialTheme. Palette is the only lever: on an LCD or at high manual brightness the backlight the user set for daytime does not drop one notch when the session seals at night, and the palette step itself (#E4DFDA→#171412 at seal, reverse at end) lands as a single-frame flash in both directions instead of a dimming step the eye can follow.

---

## Angle: tower-as-motivation (4 findings)

**Verdict:** As lived, there is no tower: the daily ritual of checking it ends in a storage drawer that by charter shows no streaks, a missed day can leave no mark because no brick ever carried a date, and the brick count was never counted in days under any of its three definitions — so the number could never have matched the history the user remembers living.

### [HIGH] The daily tower check dead-ends in a screen that forbids streaks: the tower's old home now opens the Drawer, whose own charter bans the number the user came for
*Location:* `app/src/main/java/com/example/MainActivity.kt:624; app/src/main/java/com/example/ui/screens/DrawerScreen.kt:59-62; app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:62`

MonolithViewModel.kt:52 names the fourth rail stop VAULT with label "Receipts"; MainActivity.kt:624-646 routes VAULT to DrawerScreen; DrawerScreen.kt:59-62 states its charter in its own words: "It is not a dashboard, report card or attendance book: no totals, no streaks, no percentages, no counts, no summons." TowerStreakView has zero instantiation sites in the app (the only hit for "TowerStreakView(" is its own class declaration at BlockitTowerFadersAndCatalogViews.kt:24); its sole remaining trace is an unused import at AirlockAndDisarmOverlays.kt:62. strings.xml:21 still names a ritual "FOCUS ACCUMULATION TOWER" that no code references. The in-repo build record studio/plan/CHROME-BUILD-READY.md:74-76 asserts "TowerStreakView itself SURVIVES (the running-session overlay at AirlockAndDisarmOverlays.kt:446 is a live consumer)" — on this commit line 446 sits inside CapturedNotificationsDigestSheet and no consumer exists.

### [HIGH] A missed day is structurally invisible: no brick carries a date and no consecutive-day streak is computed anywhere, so the tower can never show the hole the user knows is there
*Location:* `app/src/main/java/com/example/ui/views/BlockitTowerFadersAndCatalogViews.kt:106-140; app/src/main/java/com/example/data/RoutineDerive.kt:50-53; app/src/main/java/com/example/data/MonolithRepository.kt:2361-2405`

The tower's drawing loop assigns bricks purely by position — BlockitTowerFadersAndCatalogViews.kt:113 `val brickNumber = fl * 7 + col + 1` — filling 1..bricks contiguously with ghost slots only after the count; no brick stores or renders a date, so there is no visual form a gap could take. No consecutive-day computation exists anywhere: grep for "streak" in main code hits only DrawerScreen's ban and RoutineDerive.kt:50-53, where the app's one live "streak walk" is an internal routine-window judgement that "prints nothing, books nothing, and breaks a streak walk" on UNKNOWN — invisible to the user. The only day-bucketed history, compute7DayHistory (MonolithRepository.kt:2361-2405), produces DailyFocusStat rows that no screen renders as a tower or streak. A user who skips a week returns to exactly the monument they left; the missed days leave no mark because no surface can represent an absence.

### [MEDIUM] "One brick" has three incompatible definitions in the codebase — per block, per day, and minutes/25 plus session count — and none of them counts days
*Location:* `app/src/main/java/com/example/ui/views/BlockitTowerFadersAndCatalogViews.kt:20; app/src/main/java/com/example/data/MonolithRepository.kt:2392; studio/plan/LIE-REGISTER-2.md:37`

The view's own header cannot decide what a brick is: BlockitTowerFadersAndCatalogViews.kt:20 "one brick per focused block/day." The surviving per-day block count is computed at MonolithRepository.kt:2392 as `val blocks25 = (totalMins / 25).coerceAtLeast(if (totalMins >= 15) 1 else 0)` — 15 focused minutes earn a full "25m block", and 49 minutes earn exactly what 25 earn. The in-repo lie register records the tower formula that actually shipped to the Vault as a third definition: studio/plan/LIE-REGISTER-2.md:37 "`totalBricks = ((totalFocusedMinutes / 25) + completedCount).coerceAtLeast(16)`" — lifetime minutes divided by 25 plus a session count, two different units added into one brick count. Under every definition the number is derived from minutes or sessions, never from the calendar days the user remembers, so it cannot match their lived history even when the arithmetic runs honestly.

### [LOW] Even an empty history gets a monument: the brick count is floored at 1 and the tower is always drawn at least 4 floors tall with a lit crown brick
*Location:* `app/src/main/java/com/example/ui/views/BlockitTowerFadersAndCatalogViews.kt:29,53-54,116-120`

BlockitTowerFadersAndCatalogViews.kt:29 clamps the count with `value.coerceIn(1, 70)` so zero bricks is unrepresentable, and lines 53-54 force the scaffold: `val totalFloors = ((bricks + 6) / 7).coerceIn(1, 8)` then `val displayFloors = max(totalFloors, 4)`. Lines 116-120 then light the single brick as the accent "crown" brick. A user who has completed nothing is still shown a standing four-floor tower with a lit crown — the monument's minimum size is a stage set, not a record of anything they did.

---

## Angle: curio-reward-moment (5 findings)

**Verdict:** As lived, the curio moment barely exists: the app silently rolls dice in a database write, tells the finisher nothing, unlocks nothing they could not already read as text, and cannot distinguish clean, repeated, routine, FLOW, or shield-gap effort.

### [HIGH] Completion moment is silent: a curio is forged in the ending write but the receipt that follows never mentions it
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:2049; app/src/main/java/com/example/data/ReceiptState.kt; app/src/main/java/com/example/ui/components/ReceiptSheet.kt`

The forge happens silently in the ending write: MonolithRepository.kt:2049-2050 `val forgedId = if (allBlocksCompleted && !isRoutineSession) { CurioCatalog.artifacts.shuffled().first().id }`. But ReceiptState.kt's ReceiptState/PendingReceipt carry no forgedArtifactId field at all, MonolithRepository.kt:1625-1644 assemblePendingReceipt() never reads row.forgedArtifactId, and ReceiptSheet.kt prints only ARMED/PLANNED, ENDED/outcome, HELD, BREAK BANKED, deflections and the footer 'THE MACHINE KEEPS ITS OWN LOG · YOUR STUDY RECORD LIVES IN YPT'. At the one moment the user finishes a hard session, nothing names, shows, or points to what they just forged.

### [HIGH] Earning unlocks nothing: the full curio catalogue — names, subtitles and lore — is readable before any session, and the 'artifact' is only text
*Location:* `app/src/main/java/com/example/data/MonolithModels.kt:492; app/src/main/java/com/example/ui/screens/DrawerScreen.kt:154,374,485`

KineticArtifact (MonolithModels.kt:492-498) is only id/serialCode/name/subtitle/lore. DrawerScreen.kt:154-157 admits 'the kinetic toy VISUALS were never built as views in the shipped app... the cabinet is the catalog and the record — not the toys.' The cabinet list (DrawerScreen.kt:374-379) lets any artifact be tapped regardless of forged state, and the detail (DrawerScreen.kt:450-495) prints the full serial, name, subtitle and lore for forged and unforged alike — the only difference is the closing line 'FORGED — IT STANDS IN THE CABINET' vs 'NOT YET FORGED'. A user can read the entire 'reward' collection, e.g. the Brass Gyroscope's 'Four brass rings nested one inside the next', on day one before any session, and earning it changes no content and shows no object.

### [HIGH] Reward is an unweighted lottery and duplicates vanish: no link to effort, no count, date or session provenance in the cabinet
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:2050; app/src/main/java/com/example/data/MonolithDao.kt:74; app/src/main/java/com/example/ui/screens/DrawerScreen.kt:485`

Choice is pure lot: MonolithRepository.kt:2050 `CurioCatalog.artifacts.shuffled().first().id`, and MonolithModels.kt:482-486 admits 'forging is one artifact chosen by lot per completed session' after cutting the focus/interception requirement fields 'no code ever read'. Duration, blocks, deflections and cleanliness do not weight it. Repetition is then erased: MonolithDao.kt:74-75 `SELECT DISTINCT forgedArtifactId ... WHERE forgedArtifactId IS NOT NULL`, and the cabinet (DrawerScreen.kt:375,485-489) renders only a boolean FORGED / 'FORGED — IT STANDS IN THE CABINET' with no count, date, session intention or receipt number. Forging the same toy five times looks identical to forging it once, so many hard sessions can leave the cabinet visibly unchanged and the user cannot tell effort from bad luck.

### [HIGH] Printed rule 'A FINISHED SPAN FORGES ONE' is false for FLOW and routine finishes, which forge nothing silently
*Location:* `app/src/main/java/com/example/ui/screens/DrawerScreen.kt:433; app/src/main/java/com/example/data/MonolithRepository.kt:1970,2046,1040`

DrawerScreen.kt:433 prints the standing user-facing rule with no exception: 'A FINISHED SPAN FORGES ONE — THE FORGE CHOOSES WHICH.' In code, stopwatch/FLOW sessions return early in MonolithRepository.kt:1970-1983 before the forge logic, and their lawful completion in disarmActiveSession (MonolithRepository.kt:2322-2334) keeps `forgedArtifactId = partialStampId` = the creation-time null. Routine sessions are fenced out at MonolithRepository.kt:2046-2050 ('may NEVER declare success or mint an artifact') and finaliseRoutineSession at MonolithRepository.kt:1040 merely preserves `session.forgedArtifactId` — always null from creation (MonolithRepository.kt:1368) — even on ATTENDED. A user who finishes by FLOW or attends a daily routine, then reads the cabinet rule, was promised one and gets none, with no explanation on either receipt.

### [MEDIUM] A shield-gap session forges the same indistinguishable curio as a clean session
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:2049,2068,2086; app/src/main/java/com/example/ui/screens/DrawerScreen.kt:485`

MonolithRepository.kt:2049-2050 computes forgedId solely from allBlocksCompleted; the shield-gap classification is computed independently afterwards at MonolithRepository.kt:2068-2076 (ELAPSED_SHIELD_GAP vs ELAPSED_CLEAN) and the same write at MonolithRepository.kt:2083-2086 sets completedSuccessfully=allBlocksCompleted and stores the forgedId regardless. The cabinet then marks that artifact simply FORGED (DrawerScreen.kt:485-489) with no clean/gap distinction. The session the receipt itself indicts with 'THE SHIELD WAS OFF FOR PART OF THAT SESSION' (ReceiptSheet.kt) earns the identical, indistinguishable curio as a clean session — the reward does not certify the discipline it is meant to honour.

---

## Angle: break-away-from-phone (5 findings)

**Verdict:** The user who obeys the Safe Break and walks away is summoned back by a single 46ms buzz that fires once and never again, while the next block starts, burns, and can be receipted ELAPSED_CLEAN without him — and nothing on his return ever tells him what he missed.

### [CRITICAL] Next block auto-starts at break expiry and burns unattended, still receipted ELAPSED_CLEAN
*Location:* `MonolithRepository.kt:1874-1887, outcome branch in updateSessionProgress`

advancePastBreak resets the session at the expiry tick: 'currentBlockIndex = (session.currentBlockIndex + 1)... elapsedSeconds = 0, startWallTimeMillis = wallNow' (MonolithRepository.kt:1874-1887), so the new block's clock starts at break expiry, not at the user's return. updateSessionProgress then keeps ticking it via the shield loop and, at completion, stamps ELAPSED_CLEAN whenever only the shield beat is fresh: 'if (session.shieldGapSeen || nowWall - beatRef > SHIELD_GAP_THRESHOLD_MILLIS) ELAPSED_SHIELD_GAP else ELAPSED_CLEAN' (MonolithRepository.kt:~2070 outcome branch). No code anywhere checks screen-on, app foreground, or any user interaction before crediting the block. A 25-minute walk back can burn a whole block — or end the session — and the receipt still certifies clean study time for a desk nobody sat at.

### [HIGH] The entire break-end summons is one 46ms buzz and one 16ms click, fired exactly once
*Location:* `MonolithRepository.kt:1866-1872; TactileSoundEngine.kt:683-705`

The code's own comment brags about the design: 'the exit speaks here, at the flip — the one point every break exit crosses exactly once, so the timer's end and the hand's end sound identically, once' (MonolithRepository.kt:1866-1872). That one voice is playMechanicalLatch(true): a haptic waveform of 0,11,26,9 ms (~46ms total) plus a single 16ms transient at gain 0.54 (TactileSoundEngine.kt:683-705), played as USAGE_ASSISTANCE_SONIFICATION (TactileSoundEngine.kt:387-390). No repetition, no escalation, no second knock if the first is missed — after advancePastBreak flips isInSafeBreak=false, no later tick ever re-fires it. In a pocket, mid-walk, outdoors, a 46ms buzz and a 16ms click are functionally silence.

### [HIGH] No alarm is ever scheduled for break end and no pre-expiry warning exists — expiry is only found by polling, on a silent LOW channel
*Location:* `MonolithRepository.kt:1991-2010, 1872, 2039; MonolithShieldService.kt createNotificationChannel/buildNotification`

grep shows AlarmManager exists only in RoutineScheduler.kt and ReleaseRetryScheduler.kt — nothing schedules a wake-up for break end. Expiry is merely discovered when a polling loop next calls updateSessionProgress and breakRemainingAfter hits 0 (MonolithRepository.kt:1991-2010; shield loop delay(2000L), MonolithShieldService.kt:~195). The shield notification that carries the countdown is built on a channel with IMPORTANCE_LOW (MonolithShieldService.kt createNotificationChannel) and setOnlyAlertOnce(true) (buildNotification), so its per-tick text change to 'Safe Break MM:SS' never makes a sound, buzz, or heads-up. And there is no T-minus warning anywhere: breakBoundaryKnock is invoked only at the two flips (MonolithRepository.kt:1872, 2039) — nothing fires at 60s or 10s remaining. The walking user gets no countdown they can feel and no alarm-grade deadline.

### [MEDIUM] Returning late greets the user with nothing: no 'break ended X ago', no 'you are N minutes into Block 2'
*Location:* `TactileDeckScreen.kt:613-618; MonolithRepository.kt:1880-1882`

The Deck's only session-state eyebrow renders 'BREAK · N MIN' during a break and plain 'SESSION RUNNING' otherwise (TactileDeckScreen.kt:613-618); the hero just shows the already-running total/elapsed. A grep for late/missed/return/away surfaces in the Deck and ViewModel finds no such state — the session row carries no 'break ended at HH:mm' marker the UI reads, and advancePastBreak overwrites startWallTimeMillis/startElapsedRealtimeMillis with the expiry instant (MonolithRepository.kt:1880-1882), erasing the very fact needed to tell him how late he is. He cannot tell whether the block started 10 seconds or 18 minutes ago, or that a break even happened.

### [MEDIUM] If the whole session ends while the user is still away, the only notice is the same silent LOW notification, detached
*Location:* `MonolithShieldService.kt:110-119; MonolithViewModel.kt:1253`

In the shield loop, session end does only: updateNotification("Session Complete", "Unlocked desk toy ready in your Break tab.") then stopForeground(STOP_FOREGROUND_DETACH) and stopSelf() (MonolithShieldService.kt:110-119) — the same IMPORTANCE_LOW, onlyAlertOnce notification, so it arrives silently and just sits detached. The actual completion voice, playCompletionPulse(), is fired only from the ViewModel's finish controller (MonolithViewModel.kt:1253), i.e. it depends on the app's UI-side machinery witnessing the finish. The user who did the right thing and stayed away longest gets the quietest possible ending: no sound, no buzz, no persistent alert distinguishing 'session over' from the ordinary silent countdown updates.

---

## Angle: stopwatch-vs-timed-confusion (5 findings)

**Verdict:** The app never teaches the stopwatch/timed difference before it costs the user: mode is an invisible dial threshold, its 20-minute floor is disclosed nowhere at choice time and not even at the stop attempt, and every running surface (Deck, desk clock, airlock) renders the two modes in identical or misleading language.

### [CRITICAL] Stopwatch mode is entered by silently crossing dial 10, never chosen or explained — the '20m Min Floor' badge exists in the enum and is rendered nowhere
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:1330; app/src/main/java/com/example/ui/views/BlockitCoreViews.kt:526; app/src/main/java/com/example/ui/screens/TactileDeckScreen.kt:714`

MonolithRepository.kt:1330-1333 `val isStopwatch = durationMinutes < 10` with target replaced by STOPWATCH_MIN_FLOOR_SECONDS (109: 20*60); no confirmation or naming step exists in the arm path (MonolithViewModel.kt:2198-2206 passes only _dialMinutes). On the wheel the boundary is invisible: BlockitCoreViews.kt:526-535 minutesToAngle maps every value <10 to the single 108° FLOW seat and valueForAngle returns 5 for any angle <117°, so 3-9 are not durations at all. The Deck derives the mode the same silent way (TactileDeckScreen.kt:292-303 `dialMinutes < 10`) and the hero just swaps digits for the word 'FLOW' (640-641). The one string that teaches the cost, StudyModePreset's badge '20m Min Floor' (MonolithModels.kt:68), is never rendered: the FLOW pill at TactileDeckScreen.kt:714-735 prints only 'FLOW', and the studyPreset parameter is never read in the screen body. A user who drags one detent below 10 to get a short 5-minute session has in fact bought an open-ended session with a 20-minute lock, and nothing at the moment of choice says so.

### [HIGH] The 20-minute floor is first revealed at the stop attempt — and even then never stated: remainingFloor is computed and discarded, the valve blames the weekly token instead
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:2236; app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:772`

MonolithViewModel.kt:2236-2260 requestDisarmChallenge computes isBelowFloor and remainingFloor (2238-2240) and then throws remainingFloor away: below floor it opens the generic valve, at/after floor it silently disarmActiveSession(completed=true, 'Stopwatch Completed') with no valve at all. DisarmChallengeState carries only isVisible + requiredHoldSeconds (MonolithViewModel.kt:~195), and the overlay it opens says only 'This ends the session early. It is the only break this week.' plus 'Hold to fill 60 seconds...' (AirlockAndDisarmOverlays.kt:763-801) — no mention of stopwatch, of the 20-minute floor, or of how many minutes remain until stopping is free. So the user learns the floor exists only by being routed into a weekly-token valve, and is never told that waiting out the remaining minutes would have made the same tap free — the one fact that would change their decision is computed in code and printed nowhere.

### [HIGH] The sole stop affordance for a stopwatch is labelled as a weekly emergency-token valve — even after the floor, when the same tap is free and instant
*Location:* `app/src/main/java/com/example/ui/screens/TactileDeckScreen.kt:1045; app/src/main/java/com/example/ui/MonolithViewModel.kt:2243`

A running session's only exit control is labelled 'EMERGENCY BREAK VALVE (60S HOLD)' with badge '$weeklyEmergencyTokensRemaining/1 WEEK' for every mode and every age of session (TactileDeckScreen.kt:1045-1055) — a stopwatch has no STOP/FINISH button anywhere. Yet for a stopwatch past the floor that same tap does not open the valve and does not spend anything: MonolithViewModel.kt:2242-2255 completes the session cleanly and instantly. Before the floor the label is accurate, after it the identical label, on the identical button, lies about both the mechanism and the price, so the user cannot predict what tapping 'stop' will cost until after they have tapped it.

### [MEDIUM] Running readouts are direction-blind: identical MM:SS and percent bar count up in FLOW and down in timed mode, and the stopwatch bar pins at 100% from minute 20 while the session runs on
*Location:* `app/src/main/java/com/example/ui/screens/TactileDeckScreen.kt:327; app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:103`

Deck running hero prints the bare primaryReadout MM:SS for both modes (TactileDeckScreen.kt:640 showRunningForm branch), while progressFraction runs in opposite directions behind the same bar and '%': stopwatch = elapsed/1200 growing (336), timed = remaining/target shrinking (339) — after 20:00 a stopwatch sits at 100% forever while still running. The desk clock is worse because it is read at a glance at night: AirlockAndDisarmOverlays.kt:103-131 formats both modes as the identical '%02d:%02d' with no count-up/count-down marker, and stopwatch progress is elapsed/1200 capped at 1f, so the bar reads permanently full under a clock that keeps climbing. '42:17' on the nightstand is uninterpretable: elapsed in FLOW, remaining in a timed session, with no word on either surface saying which.

### [MEDIUM] Airlock tells a stopwatch user 'NO SET END' and hides the one end that matters — the floor moment when clean exit unlocks
*Location:* `app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:244; app/src/main/java/com/example/ui/MonolithViewModel.kt:2367`

The airlock states the end for timed sessions to the minute ('SESSION ' + endsAtText, AirlockAndDisarmOverlays.kt:241-248) but for a stopwatch prints only 'SESSION RUNNING · NO SET END' — because MonolithViewModel.kt:2367-2372 deliberately sets sessionEndsAtMillis = active?.takeIf { !it.isStopwatchMode }, withholding even the floor-derived earliest clean exit (start + 20m) that the app itself enforces. The one surface that does name the floor is the shield's notification, 'Stopwatch +%02d:%02d (20m floor)' (MonolithShieldService.kt:128-131), which still does not say how long is left until it. A user intercepted at minute 3, mid-temptation, is told the session has no end — the lived reading is 'trapped indefinitely / must burn the weekly break' — when in fact a clean exit unlocks at a specific, computable time the app refuses to state at the door.

---

## Angle: multi-block-surprises (5 findings)

**Verdict:** At 50 minutes the session stops under a paper-working user with no warning, no grace and only a 16ms click, the Deck then reads 00:00 as if it ended, and Block 2 restarts on wall-clock whether or not they ever took — or noticed — the break.

### [HIGH] Block boundary is an ambush: instant flip at 50:00, no warning and no finish-your-paragraph grace or delay verb
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:2022-2040; app/src/main/java/com/example/ui/screens/TactileDeckScreen.kt:930-938`

MonolithRepository.kt:2022-2040 flips the instant 'verifiedElapsed >= effectiveTarget': the row is rewritten isInSafeBreak=true, safeBreakSecondsRemaining=600, and breakBoundaryKnock(false) fires only AT/AFTER the flip. No pre-boundary warning, countdown-to-break, snooze or '+2 MIN TO FINISH' verb exists anywhere in the progress machine; the only verb the break itself offers is forward — TactileDeckScreen.kt:930-938 'END BREAK'. A user mid-paragraph at 49:58 gets zero seconds of notice and no lawful way to finish the sentence inside the block.

### [HIGH] Ignored break does not wait: Block 2 auto-starts on wall-clock expiry with the user still on paper
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:1991-2018,1862-1887`

MonolithRepository.kt:1991-2018: the break countdown is purely wall-derived and at nextBreakRemaining==0 advancePastBreak runs unconditionally — no presence, readiness or confirmation check. advancePastBreak (MonolithRepository.kt:1862-1887) resets elapsedSeconds=0 and startElapsedRealtimeMillis=nowMono, so Block 2's clock is already running from the break's expiry. A reader who keeps working on paper for 18 minutes returns to find their 'break' long over and ~8 minutes of Block 2 silently burned; in their lived model Block 2 starts when they sit back down — in the code it started at minute 60 whether they were there or not.

### [HIGH] Deck reads as session-finished for the entire break: hero 00:00 and 3% progress while Block 2 is still coming
*Location:* `app/src/main/java/com/example/ui/screens/TactileDeckScreen.kt:313-342,610-617; app/src/main/java/com/example/data/MonolithModels.kt:382-393`

Entry writes elapsedSeconds=effectiveTarget (MonolithRepository.kt:2024-2028), so MonolithModels.kt:382-383 remainingSeconds=(target-elapsed)=0 for the whole break — the model's own comment (MonolithModels.kt:387-393) admits 'Per-block remainingSeconds reads 0 for the whole of a break'. The Deck renders exactly that: TactileDeckScreen.kt:313-319 displaySeconds=remainingSeconds -> hero '00:00', and :336-342 progressFraction coerces 0 to 0.03 -> '3%'. The only counter-signal is a 10sp eyebrow 'BREAK · X MIN' (TactileDeckScreen.kt:610-617, minutes ceiling-rounded). The desk clock was explicitly fixed to avoid reading FINISH through a break (AirlockAndDisarmOverlays.kt:104-110); the Deck — the screen a user actually opens to ask 'what just happened?' — was not. It contradicts the shield notification's honest 'Safe Break 09:xx • Block 1/2' (MonolithShieldService.kt:123-126).

### [MEDIUM] For an off-phone paper worker the only boundary signal is a 16ms latch click and a silent low-importance notification
*Location:* `app/src/main/java/com/example/service/TactileSoundEngine.kt:601-607,683-702; app/src/main/java/com/example/service/MonolithShieldService.kt:199-232; app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:120-125`

The boundary's entire voice is breakBoundaryKnock -> playMechanicalLatch: a 16ms transient at gain 0.54 plus a ~41ms vibration waveform (TactileSoundEngine.kt:683-702), and its audio half is dropped entirely when sound is off (enqueueTransient returns early if !soundEnabled, TactileSoundEngine.kt:601-607). The repository comment concedes the proof standard is 'call level, never an acoustic claim' (MonolithRepository.kt:1814-1822). The persistent surface is no louder: the shield channel is IMPORTANCE_LOW with setShowBadge(false) (MonolithShieldService.kt:199-210) and the notification is setOnlyAlertOnce(true) with a content intent but no actions (MonolithShieldService.kt:216-232) — no heads-up, no END BREAK action from the shade. The desk-clock surface a paper worker would prop open deliberately renders nothing about the break at all: 'a Safe Break earns no special rendering' (AirlockAndDisarmOverlays.kt:120-125). Phone face-down or in the next room = the boundary simply happens to them, discovered minutes late or after expiry.

### [MEDIUM] Paper work finished during the uninvited break is erased from the task record by design
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:2415-2435`

MonolithRepository.kt:2415-2435 accrueActiveTaskSeconds passes accruingTaskId=null whenever session.isInSafeBreak, with the stated law 'During a Safe Break the anchor advances without crediting — break minutes never land in a task's stored seconds'. There is no override, no 'I kept working' claim path, and END BREAK only helps if the user notices the break and opens the Deck mid-break. The student who spends the first four minutes of the uninvited break finishing the problem set does real, on-task work that the session record permanently counts as zero — their task total under-reports exactly the minutes the boundary stole.

---

## Angle: false-start-grace (5 findings)

**Verdict:** The accidental-start grace is a 9-usable-second, Deck-only, tick-counted chip whose promise is false for routine sessions and which, one second late, mutates into a 60-second hold that spends the week's only emergency break.

### [CRITICAL] Grace chip is a false promise for routine sessions: tapping it books MISSED, not a free cancel
*Location:* `TactileDeckScreen.kt:1029; MonolithRepository.kt:2300-2312,1031-1038`

TactileDeckScreen.kt:1029 shows the chip for ANY activeSession with elapsedSeconds<=10, with no routine check. But MonolithRepository.kt:2300-2306 disarmActiveSession diverts any routineId!=null (non-SLEEP) session to finaliseRoutineSession() BEFORE the isGraceCancel test at :2311, discarding the reason string entirely. finaliseRoutineSession (MonolithRepository.kt:1031-1038) then judges attended<900s as MISSED/LATE_FIRE and books elapsedSeconds=attended. A routine session the user never deliberately armed, cancelled at 5s from the chip labelled 'GRACE WINDOW ABORT', is therefore recorded as a missed routine window, not GRACE_CANCEL with 0s banked.

### [HIGH] 9s vs 11s cliff: same button becomes a 60s hold that spends the sole weekly token
*Location:* `TactileDeckScreen.kt:1029-1058; MonolithRepository.kt:2311-2346`

TactileDeckScreen.kt:1029-1058: the identical 58dp full-width slot swaps at elapsedSeconds 10->11 from 'GRACE WINDOW ABORT' (onCancelWithinGraceWindow) to 'EMERGENCY BREAK VALVE (60S HOLD)' with badge '$weeklyEmergencyTokensRemaining/1 WEEK' (onRequestDisarm). The valve overlay (AirlockAndDisarmOverlays.kt, DisarmChallengeOverlay) states 'This ends the session early. It is the only break this week.' and demands a 60s hold. Repository consequence: at <=10s MonolithRepository.kt:2311-2313 forces bankedElapsed=0, outcome GRACE_CANCEL (:2325) and :2346 writes no intercept log; at 11s via the valve the token spend is inserted (completeEmergencyBreak, :2222-2255), outcome is VALVE_BROKE, elapsed is banked, and an 'Unlatched Early' intercept log is written. One second of hesitation, distraction, or a friend arriving converts a free, unrecorded abort into a 60-second hold plus the week's only emergency break.

### [HIGH] Grace abort lives only on the Deck: pocket the phone, lock it, or get intercepted and the window burns unseen
*Location:* `MainActivity.kt:470-515; MonolithShieldService.kt:214-232; TactileDeckScreen.kt:1029-1043`

The chip exists only inside TactileDeckScreen, which MainActivity.kt:470 renders only when destination==DECK inside AnimatedContent. The shield's ongoing notification (MonolithShieldService.kt:214-232 buildNotification) has only a content PendingIntent to MainActivity and no addAction/abort action, and the Airlock overlay offers no grace abort. Meanwhile the grace clock is monotonic from the seal anchor (MonolithRepository.kt:1429-1438 sealSession re-anchors startWall/startElapsedRealtime to t0), so time spent looking up at a visitor, unlocking the phone, returning from a blocked app that intercepted you, or reopening the app counts fully. Put the phone down for 11 seconds and the free door is gone before you ever saw it.

### [MEDIUM] First ~1.05s of the 10s window is unusable: the seal-shot animation occupies the chip's slot
*Location:* `TactileDeckScreen.kt:248-270,973-1033`

TactileDeckScreen.kt:973-1029: while shotActive the slot still renders the SlideToStartView, not the chips; the seal-shot loop (TactileDeckScreen.kt:248-270) only sets heroLanded at e>=1050ms from t0. The session clock already runs from that same t0 (sealSession re-anchor, MonolithRepository.kt:1429-1438). So the first grace chip a user can physically tap already shows at most '9s', never the advertised 10 — over a tenth of the window is spent watching the arming animation finish, before any human reaction, fumble recovery, or reading the label.

### [MEDIUM] The 10-second law is a stale tick count plus a string-sniff, not a real deadline
*Location:* `TactileDeckScreen.kt:1029-1033; MonolithViewModel.kt:948-1020,2269-2286; MonolithRepository.kt:115-117,2311`

The UI gate reads the stored row's elapsedSeconds (TactileDeckScreen.kt:1029), a value only rewritten by the VM's delay(1000L) loop (MonolithViewModel.kt:948-1020) and the shield's ~2s loop — so the badge can linger past, or jump over, real seconds. Worse, enforcement downstream is none: MonolithViewModel.kt:2269-2286 confirmDisarmSession passes the reason string unchecked, and MonolithRepository.kt:2311 decides grace purely by outcomeReason.contains("Grace Cancel") — its own companion comment (MonolithRepository.kt:115-117) calls this the 'interim Grace Cancel string-sniff'. No elapsed re-check exists in the repository. As lived, 10s is not a deadline but the luck of the last tick; as recorded, a 'grace' booking proves nothing about when the tap actually happened.

---

## Angle: sound-in-quiet-places (5 findings)

**Verdict:** In a quiet place this app is untrustworthy both ways: it ignores silent mode/DND and speaks on timers nobody is watching, yet the one sound the user actually depends on — the break ending — is a single 16ms click that is trivially missed and never repeated.

### [HIGH] Timer-driven sounds speak out loud with nobody touching the phone: break latches, routine seal Throw and the finish cascade all fire unattended
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:1827,1872,2039; app/src/main/java/com/example/service/RoutineHeartbeat.kt:150-162; app/src/main/java/com/example/ui/MonolithViewModel.kt:1241-1260`

Break flips are deliberately fired from the background machinery, not the screen: MonolithRepository.kt:1805-1828 comment says a UI-hung voice 'would be silent-by-construction whenever the app is not alive mid-break' so breakBoundaryKnock (playMechanicalLatch) is called in advancePastBreak (MonolithRepository.kt:1872) and at break entry (MonolithRepository.kt:2039), driven by MonolithShieldService.kt:108's tick. Routines are worse: RoutineHeartbeat.kt:150-162 materialises a session nobody tapped and calls proveAndSeal, ending in SessionSealTransition.kt:142 engine.playSealThrow() — a 44,100-sample, 1-second rendered clunk with room reverb (TactileSoundEngine.kt:100 SEAL_SAMPLES). Session finish is explicit about it: MonolithViewModel.kt:1241-1260 'a Deck that isn't composed doesn't mute the room' fires seat knock + completion pulse + a knock pair on a timer. In a library/bedroom the phone on the desk announces block-end, break-end, routine-start and session-end to everyone nearby, minutes after the user's last touch.

### [HIGH] Silent mode and DND are not inputs to this app at all: no ringer-mode, interruption-filter or stream-volume check exists anywhere
*Location:* `app/src/main/java/com/example/service/TactileSoundEngine.kt:389,426,529; app/src/main/java/com/example/service/SoundEffectsSetting.kt:41-45`

Zero hits in app/src/main for ringerMode, RINGER_MODE, currentInterruptionFilter, getStreamVolume or any Zen/DND API. Every AudioTrack and the focus request use USAGE_ASSISTANCE_SONIFICATION / CONTENT_TYPE_SONIFICATION (TactileSoundEngine.kt:389, 426, 529), played via raw AudioTrack — not a notification, so DND's interruption filter never sees it, and the code adds no check of its own. The only gate any playback path reads is the app's own soundEnabled, whose persisted default is ON (SoundEffectsSetting.kt isEnabled: getBoolean(KEY_ENABLED, true)). A user who does the universal quiet-place gestures — silent, vibrate, DND before a meeting — has changed nothing this app consults; whether they get detents and knocks is left to OEM stream mapping, not to their choice.

### [HIGH] The break-end cue a user relies on is a single 16ms latch click — same voice as a settings toggle, no alarm usage, no repeat, fired exactly once
*Location:* `app/src/main/java/com/example/service/TactileSoundEngine.kt:685-702; app/src/main/java/com/example/data/MonolithRepository.kt:1863-1872`

The break-exit 'voice' is playMechanicalLatch: one 16ms transient at gain 0.54, 520Hz (TactileSoundEngine.kt:685-702) — the identical sound used for ordinary toggle flips all over MonolithViewModel.kt (e.g. :1489, :1496, :1559). It is not on USAGE_ALARM (all tracks are USAGE_ASSISTANCE_SONIFICATION, TactileSoundEngine.kt:389/426), there is no repeat, no escalation, no volume sanity check, and the repository comment at MonolithRepository.kt:1863-1872 stresses it 'fires exactly once' while 'a countdown tick stays silent'. advancePastBreak starts the next block's clock in the same write, so a user in the kitchen/corridor who misses that single click is already losing block time while believing they are still on a break — nothing ever tells them they missed it.

### [MEDIUM] Turning SOUND EFFECTS off does not quiet the phone: every effect still buzzes at up to full amplitude, by design
*Location:* `app/src/main/java/com/example/service/TactileSoundEngine.kt:767-775,139-141; app/src/main/java/com/example/ui/components/ParkDrawer.kt:468-490`

Every play* method calls triggerWaveform() before any sound gate is consulted: playSealThrow (TactileSoundEngine.kt:767-775) runs the waveform, then 'if (!soundEnabled) return'. Its own doc states the design: 'With sound off: haptic only (S0 — muting loses timbre, never the event)'. The seal waveform is full-force: SEAL_THROW_TIMINGS/AMPLITUDES (TactileSoundEngine.kt:139-141) peak at amplitude 255; detents, latches and knocks use amplitudes 135-255 throughout (:630-850). The PARK control the user reaches for is labelled only 'SOUND EFFECTS' ON/OFF (ParkDrawer.kt:468-490) — nothing tells them OFF leaves the motor live, and on a hard library desk or office table that buzz is the loudest thing the app makes.

### [MEDIUM] One fixed loudness for every room and every output: no volume level, no quiet mode, no Bluetooth/headset awareness
*Location:* `app/src/main/java/com/example/service/TactileSoundEngine.kt:638,653,700,724,826,842; app/src/main/java/com/example/ui/components/ParkDrawer.kt:479-490`

Gains are compile-time constants with no room/output scaling: detent 0.38 (TactileSoundEngine.kt:638), firm click 0.5 (:653), latch 0.54 (:700), seal 0.62 (:724), tear/completion 0.68 (:826, :842), seal buffer clamped to ±0.95. The only user control is the binary PARK sled (ParkDrawer.kt:479-490). Zero hits for AudioDevice, setPreferredDevice, Bluetooth or headset anywhere in app/src/main: the same fixed-gain transient goes to whatever route is live — in-ear at full gain while scrolling the dial with earbuds in, and, because the break/finish/routine sounds are timer-fired (see unattended finding), out of the speaker at that same gain if the earbuds came out during the session. The user's real choice in a quiet place is 'full voice or lose the break-end cue entirely'.

---

## Angle: friend-holds-the-phone (5 findings)

**Verdict:** The seal only guards the owner's future self: anyone physically holding the phone can read his held messages and stored history, unblock websites and rewrite tomorrow's routines mid-session with no identity check anywhere, and can erase the held notifications outright the moment the session ends.

### [CRITICAL] Friend can unblock websites mid-session: the site list has no ratchet, no seal gate, and no identity check
*Location:* `app/src/main/java/com/example/ui/screens/ShieldMatrixScreen.kt:561; app/src/main/java/com/example/ui/MonolithViewModel.kt:1388,1552`

Rail navigation is free mid-session: navigationLocked() (MonolithViewModel.kt:1388-1401) only refuses during arm-proving, the 1050ms seal shot, or finish detention, and navigateTo (1403) has no sealed check. On Shield, every blocked site row carries a REMOVE chip (ShieldMatrixScreen.kt:561-562) and an ADD field (612), wired straight to viewModel::removeBlockedSite/addBlockedSite (MainActivity.kt Shield binding). Neither VM verb (MonolithViewModel.kt:1552-1562 approx: addBlockedSite/removeBlockedSite) nor the repository (MonolithRepository.kt:794-802, plain insertSite/deleteSite) checks for an active session — unlike app tiers and daily limits, which are ratcheted at MonolithViewModel.kt:2047 and 2123 with 'CANNOT WEAKEN … DURING ACTIVE VAULT'. The accessibility service re-reads the site list per event, so a removed domain stops matching immediately, inside the session that was supposed to be sealed.

### [HIGH] Friend can silently rewrite every future routine mid-session — disable, retime, re-day, re-loadout — and it persists and reconciles on the tap
*Location:* `app/src/main/java/com/example/ui/screens/ShieldMatrixScreen.kt:328; app/src/main/java/com/example/ui/MonolithViewModel.kt:1500-1550`

Same free rail trip lands on Shield's ROUTINES section, rendered unconditionally with live callbacks (ShieldMatrixScreen.kt:328-336: onToggle, onCycleLoadout, onAdjustStart/End ±30, onToggleDay). The VM verbs toggleRoutine/cycleRoutineLoadout/adjustRoutineTime/toggleRoutineDay (MonolithViewModel.kt:1515-1550) contain no activeSession check at all, and persistRoutine (1500-1513) immediately writes the row, re-stamps scheduleEditedAtMillis=now, dismisses the reset note, and forces a RoutineHeartbeat CRUD reconcile. Nothing on the Deck announces the change; the owner finds out when tomorrow's SLEEP/DAY BLOCKS window never arms, arms at a moved time, or carries a different loadout — or never finds out and reads the gap as his own failure.

### [HIGH] Anyone holding the phone mid-session can read the full text of the owner's held notifications from the Deck's HELD chip
*Location:* `app/src/main/java/com/example/ui/screens/TactileDeckScreen.kt:914; app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:419,444`

During a running session the Deck itself offers the digest: 'HELD' chip with the count, onOpenNotificationDigest (TactileDeckScreen.kt:897-917). Sealed mode is a frozen snapshot, not a redaction: the sheet's own contract says 'Reading is free; acting stays sealed' (AirlockAndDisarmOverlays.kt:284-296), and each frozen row prints appName • senderTitle • time (419) plus the full messageBody when non-blank (444-446). CapturedNotificationEntity stores senderTitle and messageBody verbatim (MonolithModels.kt:463-470). No PIN, biometric, or any identity check exists anywhere in the app (zero BiometricPrompt/PIN code in app/src/main). Owner shows off his Deck for ten seconds; the friend walks away having read the private messages the session was holding back from the owner himself.

### [HIGH] The second the session ends, the same holder can CLEAR ALL / DEL the held notifications — the app's only copies — before the owner sees them
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:2155; app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:370`

The digest's protections are keyed to 'sealed', not to 'owner'. Live (unsealed) mode renders a CLEAR ALL chip (AirlockAndDisarmOverlays.kt:370) and a per-row DEL (approx :430), and the repository's only guard is timing: deleteCapturedNotification and clearAllCapturedNotifications refuse solely 'if (dao.getActiveSessionSnapshot()?.isActive == true)' (MonolithRepository.kt:2155-2165); the VM verbs (MonolithViewModel.kt:1595-1611) add no check. The originals were already cancelled from the shade by the listener (MonolithNotificationListener.kt:93 cancelNotification), so the digest is the only copy. A friend who picks the phone up after the session ends — while the owner is still looking at his receipt — can erase every held message before the owner ever learns they existed.

### [MEDIUM] Mid-session, a friend can browse the owner's entire Vault history — every stored task, substep, time and day word — because the Drawer locks typing, not reading
*Location:* `app/src/main/java/com/example/ui/screens/DrawerScreen.kt:540,190`

navigateTo(VAULT) works while sealed (see finding 1), and DrawerScreen gates only edits, never reading: search input is 'enabled = !isSealed' with the comment 'reading is free, typing is an edit' (DrawerScreen.kt:540-556) and day-word declaration is suppressed while sealed (DayHeading, approx :845-870), but the LANDING/INDEX/MONTH lists, ALL MONTHS browsing, EARLIER paging and every StoredRow remain live, and tapping a row opens a full detail — title, category, finish date/time, estimate, spent, and every substep (DrawerScreen.kt:190-330). Day words print beside their dates in the month/landing groups. Showing the app off, one rail tap turns a demo into a read of months of what the owner worked on, when, and how his days were labelled.

---

## Angle: honest-travel-day (4 findings)

**Verdict:** On an honest travel day the app silently re-files, re-times and re-writes the user's record in the destination zone — receipts print hours never lived, today's word can overwrite yesterday's, and routines relocate without a word of explanation — while no screen ever acknowledges that the timezone changed.

### [HIGH] Travel breaks the 'never rewrite past days' law: after a westbound landing, declaring today's word silently overwrites yesterday's word
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:1951-1958; app/src/main/java/com/example/data/DrawerDay.kt:29-31; app/src/main/java/com/example/ui/screens/DrawerScreen.kt:863`

MonolithViewModel.kt:1951-1958 comment states the law — 'only today's filing date is declarable from here — the Drawer files words, it does not rewrite past days' — then declareTodayWord() writes DrawerDay.dateIso(DrawerDay.filingDate(System.currentTimeMillis())) with filingDate defaulting to ZoneId.systemDefault() (DrawerDay.kt:29). After a westbound landing (e.g. Delhi→London), filingDate(now) is once again the departure calendar date, so the word the user declares for their landing 'today' silently overwrites the departure day's already-declared word; after an eastbound date-line crossing, one calendar date is never filingDate(now) at any moment and can never receive a REST/ILL word at all. DrawerScreen.kt:863 gates the declaration UI on isToday, so the user has no way to see which date their tap will actually stamp.

### [HIGH] Receipt rewrites lived times into the destination timezone: ARMED/ENDED and WITHIN print hours the user never lived, with no zone label
*Location:* `app/src/main/java/com/example/ui/components/ReceiptSheet.kt:118-124,199-200; app/src/main/java/com/example/data/MonolithRepository.kt:1536-1538,1584-1586; app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:276-279`

ReceiptSheet.kt:118-124 prints 'ARMED ${hhmm(receipt.armWallMs)}' and 'ENDED ${hhmm(receipt.endedAtWallMs)}' via hhmm() at ReceiptSheet.kt:199-200 — SimpleDateFormat("HH:mm", Locale.US) with the implicit device zone at render time. The routine WITHIN line does the same: MonolithRepository.kt:1536-1538 comment admits 'HH:mm in the device's zone' and :1584-1586 formats the session's frozen windowStartMillis/windowEndMillis with it. A session lived 09:00–11:00 IST reprints as 07:30–09:30 after landing in Dubai — times the user never saw on any screen while living them — and no zone name or offset is printed anywhere on the slip. AirlockAndDisarmOverlays.kt:276-279 has the same flaw for held notifications: captureDate is recomputed in the current zone, so a message captured that morning in the departure zone flips to the 'd MMM · HH:mm' (not-today) format after landing. Distinct from the prior cross-midnight date-ambiguity title: this is retroactive zone conversion of already-lived times.

### [MEDIUM] No routine can be pinned to home time and nothing says the schedule moved: every routine silently relocates to destination wall-clock on landing
*Location:* `app/src/main/java/com/example/data/MonolithModels.kt:550-569; app/src/main/java/com/example/ui/screens/ShieldMatrixScreen.kt:624-627,675-700; app/src/main/java/com/example/service/MonolithBootReceiver.kt:59-71`

RoutineEntity (MonolithModels.kt:550-569) stores only startMinutes, endMinutes and daysMask — no timezone field exists anywhere in the schema. ShieldMatrixScreen.kt:624-627,675,682,700 renders the routine as bare '07:00 → … · MON–FRI' text with no zone. On landing, every routine silently re-anchors to the new local wall time: a 09:00 routine created to coincide with a home-team standup now fires at 09:00 destination time (hours off in absolute terms), and there is no setting to pin a routine to home time, no banner, and no note that anything moved — MonolithBootReceiver.kt:59-71 handles ACTION_TIMEZONE_CHANGED by silently force-ticking RoutineHeartbeat with no user-facing surface. The user cannot tell from any screen whether a printed time means home time or local time.

### [MEDIUM] App left open across landing never learns the zone changed: Drawer 'today' and current month stay on departure time for hours
*Location:* `app/src/main/java/com/example/ui/screens/DrawerScreen.kt:175-183; app/src/main/java/com/example/ui/MonolithViewModel.kt:1843-1845,1877; app/src/main/java/com/example/service/MonolithBootReceiver.kt:59-71`

The only ACTION_TIMEZONE_CHANGED handler in the app is MonolithBootReceiver.kt:59-71, which reconciles routines only. The Drawer keys 'today' to a LaunchedEffect loop (DrawerScreen.kt:175-182) that computes DrawerDay.nextBoundaryMillis() under the zone in force at composition and delay()s that whole duration — a zone change mid-flight does not interrupt or recompute the sleep, so the heading and isToday declaration row stay on the departure filing day until the old zone's 03:30-equivalent instant passes, while declareTodayWord stamps at tap time in the new zone, so heading and write target disagree. Similarly MonolithViewModel.kt:1843-1845 initialises _drawerMonth once at ViewModel creation and :1877 refreshes it only when refreshDrawer() runs on VAULT open — an app left open across landing keeps showing the departure month as 'current' indefinitely.

---

## Angle: change-of-plans-day (5 findings)

**Verdict:** When the day's plan collapses, the app offers no way to change just today: the pause built to forgive exactly this is unreachable, OFF does not stop the block already running, and every available edit permanently rewrites the weekly schedule — in 30-minute taps that take effect mid-edit.

### [CRITICAL] Switching today's routine OFF mid-window does not free the day: the already-running block runs to its original stored end
*Location:* `app/src/main/java/com/example/service/RoutineHeartbeat.kt:167-172; app/src/main/java/com/example/data/RoutineDerive.kt:117-128`

RoutineHeartbeat.kt:167-172: 'if a finalise ended the active session and nothing replaced it, the shield stands down. A live session (manual or routine) is never stopped here.' RoutineDerive.kt:117-123: an active session is finalise-due only when 's.windowEndMillis <= nowMillis' — its STORED window end. deriveOne returns DISABLED for a switched-off routine (RoutineDerive.kt:126-128) but nothing acts on that to end the live session; only SLEEP sessions get a stand-down path (RoutineHeartbeat.kt:125-131). The OFF cap in the editor (ShieldMatrixScreen.kt:719 'ON'/'OFF') therefore changes only future windows while the phone stays blocked for hours under a routine the screen now shows as off.

### [HIGH] The pause that would forgive a collapsed day exists in the engine and the judgement law but has no button: pausedUntilMillis is written only by tests
*Location:* `app/src/main/java/com/example/data/MonolithModels.kt:556-561; app/src/main/java/com/example/ui/MonolithViewModel.kt:1515-1550`

RoutineEntity carries pausedUntilMillis (MonolithModels.kt:556-561: 'pause-until (plan §3.5). 0 = not paused'), RoutineDerive implements a PAUSED state (RoutineDerive.kt:133-139) and J6 amnesty (RoutineDerive.kt:189-204: 'paused/disabled windows do not exist for judgement — a day he paused in advance cannot be a miss'). But grep of app/src/main shows no production writer: the only assignments of pausedUntilMillis are in tests (RoutineDeriveTest.kt:52, ChromeWaveUnitTest.kt:201). The complete ViewModel verb set for routines is toggleRoutine, cycleRoutineLoadout, adjustRoutineTime, toggleRoutineDay (MonolithViewModel.kt:1515-1550) — no pause verb, and ShieldMatrixScreen.kt has no pause control. The one mechanism built to forgive a changed day cannot be reached by the user whose day changed.

### [HIGH] No 'just today' exists: skipping today's routine means editing the weekly mask, silently cancelling every future same-weekday too
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:1546-1550; app/src/main/java/com/example/data/MonolithModels.kt:551-574`

toggleRoutineDay does 'routine.daysMask xor (1 shl dayIndex)' (MonolithViewModel.kt:1546-1550) — a bit in a weekly recurrence mask (bit 0 = Monday … bit 6 = Sunday, MonolithModels.kt:574). RoutineSchedule computes every window purely from daysMask + start/end minutes (MonolithModels.kt:628-668); the RoutineEntity has no date, no one-off exception, no 'skip next occurrence' field (MonolithModels.kt:551-569). There is no today-only scope anywhere: un-ticking Tuesday kills every Tuesday until the user remembers to re-tick it, and nothing in the app reminds them or restores it.

### [HIGH] A collapsed day that needs a different block cannot get one: routines cannot be added, deleted or renamed — only the 3 seeded rows, repurposed under their old names
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:846-859; app/src/main/java/com/example/data/RoutineDerive.kt:39-45`

insertRoutines is called only by the seed path with the three hardcoded routineTargets() (MonolithRepository.kt:205, 846-859: 'FASTED BLOCK', 'DAY BLOCKS', 'SLEEP') and by the reseed transform (MonolithRepository.kt:906). No add/delete/rename verb exists in the ViewModel (MonolithViewModel.kt:1515-1550) or the editor (ShieldMatrixScreen.kt:643-770 renders the name as static Text). DesiredSession.intention is '= routine.name' (RoutineDerive.kt:39-45), so a repurposed SLEEP row runs sessions whose stated intention is literally 'SLEEP'.

### [MEDIUM] Rescheduling is live-fire in 30-minute taps: no picker, no draft/apply — each tap persists and reconciles instantly, and out-of-clamp taps are silently ignored
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:1501-1511,1538-1544; app/src/main/java/com/example/ui/screens/ShieldMatrixScreen.kt:736-747`

The expanded editor offers only '−30'/'+30' chips for START and END (ShieldMatrixScreen.kt:736-747) — no time picker. Every tap goes through persistRoutine, whose comment reads 'Every edit re-stamps the schedule... then reconciles immediately (CRUD trigger)' with a forced RoutineHeartbeat tick (MonolithViewModel.kt:1501-1511); derive materialises a session for any unclaimed window containing now with remainder >= 5 (RoutineDerive.kt:163-178) and the heartbeat materialises it in the same pass (RoutineHeartbeat.kt:139-151). So nudging a start time earlier across the current time starts blocking while the user is still mid-edit on the END chips. Taps that would breach the span clamp are silently dropped: 'if (rawSpan !in 30..1080) return' runs before any sound or persist (MonolithViewModel.kt:1538-1544) — no message, no feedback, the chip just does nothing.

---

## Angle: need-one-app-legit (5 findings)

**Verdict:** Mid-session, a legitimate need for one blocked app has no proportionate lawful path: if the app was not pre-tiered PEEK before arming you get silence at the airlock, a ratchet that refuses to correct the tier, and a choice between abandoning the task or ending the whole session and spending the week's only emergency token — a design that trains users to pre-open everything next time.

### [HIGH] A LOCKED app legitimately needed mid-session has no scoped lawful path at all — and the airlock does not even say so
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:141-154,2337-2341,2045-2096`

peekOfferFor requires the app's tier to already be PEEK_2M: 'entity.resolvedTier == AppBlockTier.PEEK_2M' (MonolithViewModel.kt:141-154). For a LOCKED app the withheld-reason is explicitly null — 'appEntity?.resolvedTier != AppBlockTier.PEEK_2M -> null' (MonolithViewModel.kt:2337-2341) — and AirlockAndDisarmOverlays.kt:690-715 prints a peek line only for a non-null withheld reason, so the teacher's Instagram (SOCIAL, tiered LOCKED by the STUDY loadout at MonolithRepository.kt:1131-1140) gets only RETURN TO FOCUS, with no word that a peek exists or why it is denied. Correcting the tier in the moment is also refused: cycleAppBlockTier during an active session only tightens, and for LOCKED it fires 'ONE-WAY RATCHET ENGAGED • CANNOT UNLOCK … UNTIL 00:00' (MonolithViewModel.kt:2045-2067); setAppBlockTier likewise returns early on any loosening (MonolithViewModel.kt:2069-2096). The 'UNTIL 00:00' is itself false — the ratchet checks only activeSession.isActive, so ending the session, not midnight, frees the tier.

### [HIGH] The sole fallback for a two-minute legitimate lookup is burning the week's only emergency token and ending the whole session
*Location:* `app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:772,801; app/src/main/java/com/example/data/MonolithRepository.kt:2225-2262`

The only remaining door is the Emergency Break valve on the Deck (TactileDeckScreen.kt:1046-1057, badge '1/1 WEEK'). Its overlay states the price in its own words: 'This ends the session early. It is the only break this week.' (AirlockAndDisarmOverlays.kt:772), above a 60-second hold (DisarmChallengeState requiredHoldSeconds=60, MonolithViewModel.kt:2257-2260; 'Hold to fill 60 seconds', AirlockAndDisarmOverlays.kt:801). The ledger confirms the price: remaining = '(1 - spendCount).coerceAtLeast(0)' per week (MonolithWeek.kt:41-42), and completeEmergencyBreak inserts the spend and disarms the whole session in one transaction (MonolithRepository.kt:2225-2262). A 30-second dictionary check therefore costs the entire remaining session plus the week's only token, leaving nothing for a real emergency later that week.

### [HIGH] Waiting for a Safe Break is useless for this need: breaks are automatic-only, between-blocks-only, and LOCKED apps stay locked through them
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:2020-2040; app/src/main/java/com/example/service/MonolithAccessibilityService.kt:277-279`

There is no on-demand break verb: a Safe Break is created only inside updateSessionProgress when 'blockFinished && session.currentBlockIndex < session.totalStudyBlocks' (MonolithRepository.kt:2020-2040), so a single-block session never gets one and no session can call one early. Even when it arrives it does not help this user: 'Safe Break Rule: 2m Peek apps unlock freely during the 10-minute Safe Break, while Locked apps stay locked' (MonolithAccessibilityService.kt:277-279), mirrored in the shield's blocked-set filter, which excludes only 'updated.isInSafeBreak && tier == AppBlockTier.PEEK_2M' (MonolithShieldService.kt:170). Waiting politely for the break changes nothing for the mis-tiered LOCKED app.

### [MEDIUM] Even where a peek exists, its fixed 120 seconds plus a 20-minute cooldown is sized for an urge, not for a task — so users learn to pre-open everything
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:106-108,1194-1202; app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:663`

The lease is a fixed constant with no sizing to the task: 'PEEK_DURATION_SECONDS = 120', 'PEEK_COOLDOWN_MILLIS = 20 * 60 * 1000L' (MonolithRepository.kt:106-108). The offer appears only after the airlock's 5-second countdown finishes (AirlockAndDisarmOverlays.kt:663), and startPeekLease pre-arms the cooldown in the same write that grants the lease (MonolithRepository.kt:1194-1202). If the file has not downloaded, the DM thread needs scrolling, or the copied text must be checked back in the study app, there is no extension and no second peek: the next intercept prints only 'PEEK COOLDOWN — AGAIN IN $minutes MIN' (AirlockAndDisarmOverlays.kt:233-235). The rational adaptation the design teaches is to pre-set every plausibly-needed app to PEEK or OPEN before arming — i.e. to pre-open everything and gut the shield in advance.

### [MEDIUM] A peek is not scoped to the needed content: one lease opens the whole app and even silences its Reels/Shorts sniper and site blocker
*Location:* `app/src/main/java/com/example/service/MonolithAccessibilityService.kt:217-254; app/src/main/java/com/example/service/MonolithShieldService.kt:164-175`

The lease is package-level, not content-level, in both enforcers: the shield simply removes the package from blockedPackages while 'nowMono < app.activePeekUntilElapsedRealtime' (MonolithShieldService.kt:164-175), and the accessibility service computes leaseLive per package (MonolithAccessibilityService.kt:226-229). Worse, a live lease 'silences the sniper and the site blocker too — a lease is honoured by every door or it is honoured by none' (MonolithAccessibilityService.kt:217-224), and both the Shorts/Reels sniper (line 231) and the URL blocker (line 254) are skipped while leaseLive. So peeking into Instagram to read a teacher's DM also suspends the Reels sniper inside that same app for two minutes: the legitimate errand is bundled with exactly the feed exposure the session exists to prevent, with no DM-only or file-only scope anywhere in the code.

---

## Angle: no-widget-glance-cost (5 findings)

**Verdict:** Between sessions Monolith is glance-blind: no widget, tile, shortcut or badge exists, the shield notification removes itself when idle, asserts 'Locked/Protecting' before protection has run a single iteration, and after a natural finish leaves a never-cancelled 'Session Complete' ghost in the shade — every 2-second status question costs a full cold open of the app.

### [HIGH] Between sessions there is no glance surface at all: no widget, no QS tile, no launcher shortcut, no idle notification — the shield notification deletes itself when the session ends
*Location:* `app/src/main/AndroidManifest.xml; app/src/main/java/com/example/service/MonolithShieldService.kt:98-104`

AndroidManifest.xml declares exactly one activity (MainActivity, LAUNCHER intent-filter only) plus services/receivers — grep for appwidget/TileService/shortcuts in the manifest returns nothing (exit 1), res/xml/ contains only accessibility_service_config, backup_rules, data_extraction_rules and device_admin_receiver (no appwidget-provider or shortcuts file), and a repo-wide search for AppWidgetProvider/TileService/ShortcutManager finds zero hits. The only external surface, the shield notification, is removed and the service self-stops the moment no session is active: MonolithShieldService.kt:98-104 'if (activeSession == null || !activeSession.isActive) { stopForeground(STOP_FOREGROUND_REMOVE); stopSelf() }'. Yet the data a glance needs exists — RoutineSchedule.nextStartMillis (MonolithModels.kt:599-600) computes the next routine window — it is simply never surfaced outside the app. Between sessions the user's 2-second questions (next routine when? shield idle or broken? anything held?) have no surface at all; every check is a full cold open of the app, landing on the arming Deck (MonolithViewModel.kt:209 defaults currentDestination to DECK).

### [HIGH] The de-facto widget lies at the moment of the glance: 'Monolith Locked / Protecting your focus session...' is posted before a single protection iteration has run
*Location:* `app/src/main/java/com/example/service/MonolithShieldService.kt:61-67,152-158,258-262; app/src/main/res/values/strings.xml:6`

MonolithShieldService.kt:61-67 calls startForeground with buildNotification("Monolith Locked", "Protecting your focus session...") before startMonitoringLoop() is even launched and before any session row is read. The code's own witness contradicts the text: protecting only flips true after a full monitor iteration under a live session (MonolithShieldService.kt:152-158), and the companion doc states it plainly (lines 258-262): 'True only after startForeground succeeded AND a full monitor iteration ran... started is not protecting.' The channel description doubles down on the claim, promising a 'real-time monotonic focus timer' (res/values/strings.xml:6), while the timer is a text snapshot rebuilt on a 2-second polling loop (MonolithShieldService.kt:137-146, delay(2000L) at :195) with no chronometer set in the builder (:213-232). A user who arms and immediately glances at the shade is told protection is a fact during the exact window where it is still only a hope.

### [HIGH] Ghost glance after natural completion: the 'Session Complete' notification is DETACHed, ongoing, never auto-cancels and is never cancelled — it poses as current status forever
*Location:* `app/src/main/java/com/example/service/MonolithShieldService.kt:114-119,223-232`

On timer completion the loop posts updateNotification("Session Complete", "Unlocked desk toy ready in your Break tab.") and then calls stopForeground(STOP_FOREGROUND_DETACH) (MonolithShieldService.kt:114-119) — DETACH leaves the notification in the shade, unlike the STOP and no-session paths which use STOP_FOREGROUND_REMOVE (:42, :98). The builder sets .setOngoing(true) and never sets setAutoCancel (MonolithShieldService.kt:223-232), and a repo-wide search finds no NotificationManager.cancel() for NOTIFICATION_ID 4096 anywhere. So the completion card survives indefinitely — hours or a day later the shade still announces 'Session Complete • desk toy ready' as if it just happened, and tapping it (no auto-cancel) leaves it there. Only the next session overwriting the same notification ID ever clears it.

### [MEDIUM] Launcher badge is deliberately switched off on the only channel — the icon never carries session, urge or held-notification state
*Location:* `app/src/main/java/com/example/service/MonolithShieldService.kt:201-208; app/src/main/java/com/example/ui/screens/TactileDeckScreen.kt:915`

The app's only notification channel explicitly disables badges: MonolithShieldService.kt:201-208 creates the channel with setShowBadge(false). It is the only NotificationChannel and the only NotificationCompat builder in the codebase, so no Monolith state — active session, urges blocked, held notifications — can ever appear on the launcher icon. The held-notification count demonstrably exists and is considered worth printing inside the app (TactileDeckScreen.kt:915 renders capturedNotificationsCount as a chip badge), but a user who wants that one number between sessions must open the full app and reach the Deck chip; the home screen shows a bare icon indistinguishable from the app never having been installed-configured at all.

### [MEDIUM] The one glance that exists is a single truncated line whose tap dumps the user into the full app on the arming Deck — no expanded text, no actions, no routing to the surface asked about
*Location:* `app/src/main/java/com/example/service/MonolithShieldService.kt:213-232; app/src/main/java/com/example/MainActivity.kt:132-141`

buildNotification (MonolithShieldService.kt:213-232) sets only a title and a single-line content text — no BigTextStyle, no setUsesChronometer, no addAction anywhere in the codebase — so intention, countdown, block progress and urge count are crammed into one truncating line ('Monolith • ${intention}' / 'MM:SS left • Block x/y • N urges stopped', :142-146). The content PendingIntent (:214-221) is a bare MainActivity intent with no routing extra, and MainActivity only special-cases intercept extras (MainActivity.kt:132-141); everything else lands on the default DECK destination (MonolithViewModel.kt:209). Even a mid-session glance therefore costs a full app open on the arming dial, not the status, break or digest surface the user was asking about — and for the detached 'Session Complete' ghost, the tap doesn't even dismiss the card it came from.

---

## Angle: park-first-run-autopen (5 findings)

**Verdict:** The first PARK auto-open is a blank, actionless checklist that rises by itself, can rise read-only over a live session, shows features ON that were never chosen, and is permanently spent by the first accidental tap — as a first impression it teaches the user nothing except that the app interrupts them.

### [HIGH] First-run auto-open bypasses the seal guard every other invocation obeys — it can rise, read-only and unactionable, over a live session
*Location:* `app/src/main/java/com/example/MainActivity.kt:311-318`

Host comment promises 'While sealed it cannot be invoked at all' (MainActivity.kt:271-274) and the tap path enforces it — openPark() checks '!sealed && !viewModel.navigationLocked()' before setting parkOpen (MainActivity.kt:289-301). The first-run path ignores both: LaunchedEffect(Unit) does 'if (viewModel.parkIntroPending()) { parkOpen = true }' with no seal/lock check (MainActivity.kt:311-318). On a fresh install a routine session can already be live, so the brand-new user is greeted by a drawer ParkDrawer renders read-only under the seal: FIX is suppressed ('visible = firstMissing != null && !isSealed', ParkDrawer.kt:415-416) and every sled is covered by a touch-swallowing overlay — a board they cannot act on, auto-raised over a session they never started.

### [HIGH] The auto-raised first board has no FIX key and no state words — its only action appears later, unannounced, when the async sweep lands
*Location:* `app/src/main/java/com/example/ui/components/ParkDrawer.kt:175-179`

The store starts at WatchSnapshot.EMPTY (WatchStore.kt:152, WatchSnapshot.EMPTY at WatchStore.kt:140-142), so at first composition every permPrintAt returns UNMEASURED (WatchStore.kt:355-357), which the board maps to word=null — name plates with blank right sides (ParkDrawer.kt:341-346). firstMissing is 'PERMISSIONS.firstOrNull { permPrintAt == MISSING }' (ParkDrawer.kt:175-179), so with nothing measured it is null and the FIX key — the board's only call to action — is not rendered at all (ParkDrawer.kt:415-416). The auto-open fires in the same first composition (MainActivity.kt:314-318) while the grant sweep is only triggered in parallel by Deck entry (MainActivity.kt:166-171) and lands asynchronously; the 280ms rise (MonolithCurves.kt:90) therefore seats a board with no verdicts and no button, and FIX later pops in mid-reading via expandVertically/fadeIn (ParkDrawer.kt:415-420).

### [HIGH] Any dismissal — including an accidental first-second scrim tap — permanently spends the one-time intro; never closing it makes it re-invite on later, fully-measured runs
*Location:* `app/src/main/java/com/example/MainActivity.kt:279-287`

closePark() — reached by scrim tap, Back, rail re-tap, grip drag past 40%, all with no minimum dwell or read condition — unconditionally runs 'if (viewModel.parkIntroPending()) viewModel.markParkIntroDone()' (MainActivity.kt:279-287), and markParkIntroDone persists the bit with apply() (MonolithRepository.kt:494-498). A reflexive tap on the full-screen scrim (ParkDrawer.kt scrim clickable -> onDismiss) in the first second, before the sweep has even printed a word, permanently spends the app's one guided moment. Inverted corollary: the bit is set on close, never on open or on measurement, so a user who force-stops or is killed while the drawer stands open gets the full auto-rise again on a later launch even though the store has long since measured — contradicting the stated condition 'while the store has never measured' (MainActivity.kt:271-274; MonolithRepository.kt:450-456, where the 'never measured' fact is implemented as an unrelated default-false boolean).

### [MEDIUM] First board presents blocking features as already ON that the user never chose, on the same screen whose measured lines print nothing at all
*Location:* `app/src/main/java/com/example/ui/components/ParkDrawer.kt:387-411`

On the never-measured board the SHORTS + REELS and SOCIAL SITES lines print no state word (their interceptor derivation returns null while parents are UNMEASURED, WatchStore.kt:466-478; ParkDrawer.kt:387-411 prints name only), yet directly below, the Shorts sled row renders a definite 'ON'/'OFF' word and a seated sled from shortsReelsSniperEnabled (ParkDrawer.kt:540-600), the Social sled likewise stands on its board line (ParkDrawer.kt:393-411), and both persisted facts default to true when no guard-settings row exists (MonolithRepository.kt:815-816, 828-829). A user who has granted nothing and chosen nothing is shown two blocking features already ON in the drawer that rose by itself, while the board above them simultaneously claims to know nothing about whether they can run.

### [MEDIUM] The only explanatory text in the auto-opened drawer is buried at the foot of its scroll, below everything, in 10sp spec-plate riddles
*Location:* `app/src/main/java/com/example/ui/components/ParkDrawer.kt:620-640`

The drawer's entire visible vocabulary on first rise is the header 'GUARD', a 'LAST PROVED' label with a blank slot (ParkDrawer.kt:308-330), nine unexplained board names, sleds and 'THEME'. The only sentences that say what the app does — PLATE_HEAD/PLATE_BODY, e.g. 'WHEEL SETS TIME · SLIDE ARMS · THE HOLD BREAKS' (ParkDrawer.kt:105-125) — are rendered last, at the foot of a verticalScroll column after the board, FIX, Sound, Shorts and Theme sections (ParkDrawer.kt:301, 620-640), in 10sp type. A first-time user who does not think to scroll a drawer they never opened never reaches even that riddle; the auto-open's first viewport teaches nothing about what PARK is, why it rose, or that scrolling is expected.

---

## Angle: receipt-dismissed-forever (5 findings)

**Verdict:** As lived: a careless tap, a trip to the tab literally labelled 'Receipts', or opening PARK makes the session's only record vanish with no warning and no way back in the live app — the Vault keeps completed tasks, day words and curios, but of the receipt itself only the latest session's number, pointer and outcome word survive, and even those are orphaned the moment the next session claims its own.

### [CRITICAL] Tapping the tab actually named 'Receipts' (VAULT) is itself a dismissal — and the Vault holds no receipt to land on
*Location:* `MainActivity.kt:303-309; MonolithViewModel.kt:49-52; DrawerScreen.kt:134-160`

The rail tab the user taps is literally labelled Receipts: MonolithDestination.VAULT("vault", "Receipts") (MonolithViewModel.kt:49-52). But MainActivity.kt:303-309 runs LaunchedEffect(currentDestination, parkOpen) { if (currentDestination != DECK || parkOpen) dismissedReceiptSessionId = receiptState.sessionId } — leaving the Deck for VAULT dismisses the slip on that same tap. What VAULT then shows is DrawerScreen, whose entire data model is List<FocusTaskEntity> (DrawerScreen.kt:134-160; MonolithDao.kt:207-213 getStoredPage/searchStoredTasks return FocusTaskEntity only) headed 'STORED' — completed tasks, day words and curios. No receipt, no session, no № is rendered there. The one tap a user would make to keep/see the receipt is a tap that destroys its only display and lands on a screen that never contained it.

### [HIGH] One stray tap, any navigation, or opening PARK kills the slip — and there is no re-open affordance anywhere in the live app
*Location:* `ReceiptSheet.kt:36-48,70-77; MainActivity.kt:262-309,776-784`

Dismissal is a full-screen trap: ReceiptSheet.kt:70-77 makes the whole scrim Box .fillMaxSize().clickable(onClick = onDismiss), and its own header comment states it 'dismisses on the next DOWN anywhere or on navigation'. The host comment at MainActivity.kt:262-268 repeats the law: 'the slip stands until the next DOWN anywhere, a navigation, or PARK's invocation'. Once any of those fire, MainActivity.kt:776-784 only renders the slip when dismissedReceiptSessionId != standingReceipt.sessionId — and nothing anywhere in the codebase ever resets that flag to -1, and no button, menu, Deck control or Vault entry re-opens a receipt (the only ReceiptSheet call site in the app is MainActivity.kt:781-783). PARK is equally silent: openPark() merely sets parkOpen=true (MainActivity.kt:290-301), which triggers the same LaunchedEffect dismissal — a user who opens PARK just to check a permission setting loses the receipt as an unmentioned side effect, with no warning, confirmation or undo.

### [HIGH] The Vault keeps none of the receipt: persistence is one number, one pointer, one word — for the latest session only
*Location:* `ReceiptState.kt:10-16; MonolithRepository.kt:1530-1534,1753-1793; ReceiptSheet.kt:182-190`

ReceiptState.kt:10-16 states the design plainly: 'No receipt_store table exists. What persists is exactly a number, a pointer, an outcome word. The slip itself is re-derived from the session row at issuance and at rehydration — never stored.' Guard settings keep only receiptSequence / lastReceiptSessionId / lastReceiptOutcome (MonolithModels.kt:761-771; MonolithRepository.kt:1530-1534 standingReceipt()). Re-derivation exists on exactly one path — rehydrateReceipt() (MonolithRepository.kt:1753-1793) — and it is called once, at ViewModel birth, in runSliceERecovery (MonolithViewModel.kt:1093-1102), only for the single standing (latest) session. The slip's distinctive content — the per-app deflection lines 'HH:mm · appName', HELD apps/sites, the routine WITHIN line, banked break (ReceiptSheet.kt:118-150) — is assembled transiently and exists in no Vault list, no session-history screen (sessionHistory in MonolithViewModel.kt:446 is consumed only by totals and the finish controller), and no export. The slip's own footer concedes it while misdirecting the user: 'THE MACHINE KEEPS ITS OWN LOG · YOUR STUDY RECORD LIVES IN YPT' (ReceiptSheet.kt:182-190) — the machine's log has no reader in this app, and YPT is not this app's Vault.

### [HIGH] The only warnings that the shield was off — or is STILL blocking — live exclusively on this one-tap-vanishing slip
*Location:* `ReceiptSheet.kt:154-171; MainActivity.kt:303-309`

ReceiptSheet.kt:154-171 prints, only on the slip, 'THE SHIELD WAS OFF FOR PART OF THAT SESSION' and, for a faulted finish, 'SHIELD — STILL BLOCKED (SERVICE DIDN'T STOP)' — with the code comment stating this fault line 'never prints on the imprint, the strip, or anywhere else' and the faulted sheet deliberately carries no title. A faulted receipt therefore means the blocking service may still be running, and the sole surface that tells the user so is the surface designed to vanish on the next tap, navigation or PARK open (MainActivity.kt:303-309). Dismiss it carelessly and the user has neither the warning nor any screen that will repeat it — the Vault (DrawerScreen) contains no session or fault information at all.

### [MEDIUM] The slip never says it matters — and arming the next session clears it even when that session will publish no receipt at all
*Location:* `ReceiptSheet.kt:56-193; MonolithViewModel.kt:2198-2200; MonolithRepository.kt:1484-1486`

Nothing on the slip says it is the only copy, that tapping dismisses it, or where (nowhere) it goes: ReceiptSheet.kt renders title, №, lines and the YPT footer, with no close label, no 'saved' marker and no dismissal hint. Worse, the natural next act erases even the in-memory standing copy: MonolithViewModel.kt:2198-2200 armMonolithSession() does _receiptState.value = null with the comment 'the standing receipt and its sled gate clear at the next arm — the slip stood until now'. And that next session may publish nothing at all — claimReceiptNumber's contract notes 'Valve / grace / early endings never call this — they publish nothing' (MonolithRepository.kt:1484-1486) — so a user who arms again and then valve-breaks or grace-cancels is left with neither the old receipt in view nor a new one, and when a later session does claim, the single slot (lastReceiptSessionId/receiptSequence) moves to it, orphaning every earlier № permanently.

---

## Angle: month-in-review (5 findings)

**Verdict:** A month in, the Drawer keeps everything but helps you re-read almost none of it: past day words are frozen and unsearchable, month-hopping resets to the current month every time, and search/paging get heavier and more manual the more history you have.

### [HIGH] Past day words are fossilised: after the 03:30 rollover a missing or wrong REST/ILL/FAMILY can never be declared, corrected or cleared
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:1950-1962; app/src/main/java/com/example/ui/screens/DrawerScreen.kt:863-877`

MonolithViewModel.kt:1950-1962 declareTodayWord() hard-codes the date from filingDate(System.currentTimeMillis()) with the comment 'only today's filing date is declarable from here — the Drawer files words, it does not rewrite past days'; DrawerScreen.kt:863 DayHeading renders the REST/ILL/FAMILY chooser only 'if (isToday && !isSealed)'. No other write path for a past date exists. A user who was too ill to tap ILL that day, or who tapped the wrong word, discovers it in review and can never declare, correct or clear it — the gap/error is permanent print.

### [HIGH] Day words are unsearchable, and search results strip them — weeks-old ILL/REST context cannot be found and disappears from found days
*Location:* `app/src/main/java/com/example/data/MonolithDao.kt:213; app/src/main/java/com/example/ui/screens/DrawerScreen.kt:585-587`

MonolithDao.kt:213 searchStoredTasks matches only 'title LIKE … OR categoryTag LIKE … OR notesOrSubsteps LIKE …' — day_notes/day words are not in the query at all, so searching ILL, REST or FAMILY returns DrawerScreen's 'NOTHING STORED UNDER “ILL”' even when that word is printed on a heading in the data. Worse, DrawerScreen.kt:585-587 builds search-result groups with 'dayWords = emptyMap()', so even a successful title search strips the word off its day heading: the reason a week was light vanishes exactly when the user goes looking for it.

### [MEDIUM] Month-by-month review is a loop back to the current month every time: no prev/next month, and BACK from a past month throws away the index and the place
*Location:* `app/src/main/java/com/example/ui/screens/DrawerScreen.kt:620-676; app/src/main/java/com/example/ui/MonolithViewModel.kt:1876-1882`

DrawerScreen.kt:675-676: the MONTH view's BACK does 'onSelectMonth(currentYearMonth.first, currentYearMonth.second); view = DrawerView.LANDING' — it discards the month being read, reloads the current month, and lands on the landing, not on the ALL MONTHS index (DrawerScreen.kt:620-646) the user came from. MonolithViewModel.kt:1876-1882 refreshDrawer() likewise resets _drawerMonth to the current filing month, and MonolithViewModel.kt:1422-1423 calls it on every navigateTo(VAULT). No previous/next-month control exists anywhere in DrawerScreen.kt — the only month entry points are the index rows and the landing. Reviewing three past months is three full Landing → ALL MONTHS → month → BACK-to-current-month loops.

### [MEDIUM] Search fires an un-debounced three-column leading-wildcard LIKE scan on every keystroke, so finding an old task costs a full scan per letter typed
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:1918-1935; app/src/main/java/com/example/data/MonolithDao.kt:213`

MonolithViewModel.kt:1918-1935 setDrawerQuery() cancels the previous job and immediately launches repository.searchStoredTasks(query, 0) on every keystroke — there is no delay/debounce anywhere in the function. MonolithDao.kt:213 runs that as three leading-wildcard LIKEs ('%' || :q || '%') over title, categoryTag and the full notesOrSubsteps blob of every COMPLETED row. Typing a 9-letter query over a month-plus of stored tasks is 9 unindexed full-table scans, each replacing the result list wholesale — search gets slower and flickerier the more history the user has earned.

### [LOW] Accumulated EARLIER pages are regrouped from scratch on every recomposition, and paging a full month is repeated manual scroll-to-bottom taps
*Location:* `app/src/main/java/com/example/ui/screens/DrawerScreen.kt:84-105,585,681,785,941; app/src/main/java/com/example/ui/MonolithViewModel.kt:1904-1913`

DrawerScreen.kt:84-105 buildDayGroups() groupBy's every loaded row through DrawerDay.filingDate() and LocalDate.parse's every day-word key, and it is invoked un-remembered inside the LazyColumn content at DrawerScreen.kt:585 (search), :681 (month) and :785 (landing), so any recomposition regroups the whole accumulated set from scratch. Pages accumulate in memory via MonolithViewModel.kt:1904-1913 loadEarlierDrawer() ('_drawerRows.value = _drawerRows.value + page.rows', 50 rows per tap, MonolithRepository.kt:165 DRAWER_PAGE_SIZE=50), and paging is a manual EARLIER tap at the very bottom (DrawerScreen.kt:941) — no scroll-triggered load. The deeper into a month the user reads, the more every subsequent interaction re-sorts, and each 50 rows costs another scroll-to-the-bottom pilgrimage.

---

## Angle: routine-fires-in-class (4 findings)

**Verdict:** In class the routine betrays the user before it traps them: the phone buzzes at full amplitude and sounds its seal chord with no regard for silent mode, a named 'urges stopped' notification appears face-up, and there is no snooze, no skip-today, and no warning — the pause feature exists only in the derive layer and in tests.

### [HIGH] Unattended routine fire performs the full seal ceremony: max-amplitude desk buzz plus seal sound, with no silent-mode/DND check anywhere
*Location:* `app/src/main/java/com/example/service/RoutineHeartbeat.kt:144-161; app/src/main/java/com/example/service/TactileSoundEngine.kt:143-144,767-777`

RoutineHeartbeat.kt:144-161 materialises the session and launches proveAndSeal off-pass, with the comment boasting 'the SLEEP 20:00 seal sounds, bit-identically' through the Deck-arm transition. SessionSealTransition.kt:142 calls engine.playSealThrow(); TactileSoundEngine.kt:767-777 fires triggerWaveform BEFORE the soundEnabled check, using SEAL_THROW_TIMINGS longArrayOf(0, 24, 38, 16) at amplitudes intArrayOf(0, 255, 0, 175) (lines 143-144) — a max-amplitude buzz a hard desk turns into a rattle — gated only by hapticsEnabled, a var defaulting to true (line 292). If sound is on, the seal chord is then rendered to a raw AudioTrack (USAGE_ASSISTANCE_SONIFICATION, TactileSoundEngine.kt:387-391). A codebase-wide grep finds no RINGER_MODE, interruption-filter, or DND check anywhere: the phone being on silent for class is never consulted. Nobody tapped anything; the phone announces itself.

### [HIGH] The snooze does not exist: the pause machinery is fully built in derive() but pausedUntilMillis has zero production writers
*Location:* `app/src/main/java/com/example/data/MonolithModels.kt:561; app/src/main/java/com/example/data/RoutineDerive.kt:129-140; app/src/main/java/com/example/ui/MonolithViewModel.kt:1501-1521`

RoutineDerive.kt:129-140 implements a complete PAUSED state and judgeLatestClosed grants J6 amnesty ('a day he paused in advance cannot be a miss', lines 199-207), all keyed on RoutineEntity.pausedUntilMillis (MonolithModels.kt:561, default 0L). But a grep of app/src/main shows no production code ever writes that field: the only writers are the migration default (MonolithDatabase.kt:58, 'DEFAULT 0') and unit tests (RoutineDeriveTest.kt, ChromeWaveUnitTest.kt). The routine UI surface offers toggleRoutine (disable outright), loadout cycling and schedule edits via persistRoutine (MonolithViewModel.kt:1501-1521) — no pause, no skip-today, no snooze duration. The one escape hatch the design built for exactly this situation is unreachable from the user's chair.

### [MEDIUM] No advance warning and no controls at the moment of firing: the alarm goes straight to a locked session, and the notification that appears has zero action buttons
*Location:* `app/src/main/java/com/example/service/RoutineAlarmReceiver.kt:12-31; app/src/main/java/com/example/service/MonolithShieldService.kt:223-232`

RoutineAlarmReceiver.kt:12-19 describes itself as a shell carrying 'NO routine id, NO boundary, and NO logic' whose fire goes straight to RoutineHeartbeat.tick (lines 22-31), which materialises immediately — derive() only emits a DesiredSession for a window already containing now (RoutineDerive.kt:164-178), and no pre-boundary notification, countdown, or 'starts in 5 minutes' path exists anywhere in the scheduler or heartbeat. The one surface that does appear, the shield's foreground notification, offers no controls: buildNotification (MonolithShieldService.kt:223-232) sets only a content intent that opens the app — no addAction for Snooze, Skip, or Dismiss. On the desk, the first and only sign of the routine is the lock itself.

### [MEDIUM] What the desk sees: every app is re-tiered instantly with no grace period, and a persistent notification naming the routine and counting 'urges stopped' appears face-up
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:1086-1100,1126-1166; app/src/main/java/com/example/service/MonolithShieldService.kt:142-146`

materialiseRoutineSession (MonolithRepository.kt:1086-1100) calls applySmartLoadout then startSession then startShield in one breath; applySmartLoadout rewrites every installed app's tier and persists the whole table via dao.insertApps(updated) (lines 1126-1166) — there is no grace minute in which the user could finish what they were doing. Simultaneously the shield posts its ongoing notification and the monitor loop retitles it 'Monolith • ${updated.intention}' with subtitle '$timeFormatted • ${updated.phantomReachesBlocked} urges stopped' (MonolithShieldService.kt:142-146), where intention is the routine's own name ('Routine: <name>' / routine.name per RoutineDerive.kt:37-38) — e.g. 'Monolith • DAY BLOCKS … urges stopped', sitting face-up on the desk for anyone to read. The channel sets setShowBadge(false) (line 207) but no lockscreen-visibility restriction is ever set.

---

## Angle: two-phones-tablet (5 findings)

**Verdict:** The app guards exactly one handset and never says so at arm or receipt time: a session spent entirely on the tablet or laptop beside the phone still seals as HELD and receipts as ELAPSED_CLEAN.

### [CRITICAL] ELAPSED_CLEAN certifies the person, but only ever inspected this one phone
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:2061-2071; app/src/main/java/com/example/ui/components/ReceiptSheet.kt:124,131`

The ending write classifies the session purely from this handset's shield heartbeat: MonolithRepository.kt:2061-2071 stamps ELAPSED_CLEAN unless 'session.shieldGapSeen || nowWall - beatRef > SHIELD_GAP_THRESHOLD_MILLIS' on this device — no input exists for any other screen. ReceiptSheet.kt:124 then prints 'ENDED {time} · {outcomeWord}' and :131 'HELD {n} APPS · {m} SITES' with no 'on this phone' qualifier. A user who spends the session on Instagram on a tablet or YouTube on a laptop is handed the same clean receipt as a user who never touched a screen.

### [HIGH] Arming never discloses the one-door limit — the only 'this phone' wording in the app is about data, not protection
*Location:* `app/src/main/java/com/example/ui/components/MonolithCustomComponents.kt:469-483; app/src/main/java/com/example/ui/components/ParkDrawer.kt:107-123`

At the seal the Deck's imprint says '{n} HELD · {m} PEEK' (MonolithCustomComponents.kt:469-483, splitTenantText) — an unqualified hold. A grep of all of app/src/main for tablet, laptop, desktop, second phone/device, other device, cross-device and companion returns zero user-facing hits. The only phone-scoped sentences in the app are about storage and power, in PARK's spec plate: ParkDrawer.kt:121 'DATA: ON THIS PHONE ONLY' and :123 'POWER: YOUR PHONE' — the user is told his data stays on this phone, never that his protection does.

### [HIGH] 'Held' notifications are only held on this handset — the same message arrives live on every other screen
*Location:* `app/src/main/java/com/example/service/MonolithNotificationListener.kt:51-93; app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:362,377`

MonolithNotificationListener.kt:51-52 and :75-93 only ever see and cancelNotification(sbn.key) for StatusBarNotifications posted to this handset, matched by this phone's package rules. The same WhatsApp/Gmail/Instagram message is delivered in parallel to the user's tablet, watch and laptop client, untouched. The digest then claims the opposite of lived experience: AirlockAndDisarmOverlays.kt:377 'Anything newer waits until the session ends' and :362 'HELD NOTIFICATIONS' — the message did not wait; it was read and answered on the next screen, and the digest re-presents it afterwards as newly held catch-up.

### [HIGH] Nothing on the other device can even be named in the Shield — the catalogue is this phone's installed list only
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:220-233; app/src/main/java/com/example/ui/screens/ShieldMatrixScreen.kt:522`

The Shield's universe is built solely from this device's PackageManager: MonolithRepository.kt:220 'pm.queryIntentActivities(mainIntent, 0)' with the comment at :233 'THE CATALOG IS THE PHONE'S TRUTH', and ShieldMatrixScreen.kt:522 answers a miss with 'NO APP BY THAT NAME ON THIS PHONE'. Sites are blocked only by reading browser URL bars on this device (MonolithAccessibilityService.kt browser-package list). An app or site that lives only on the tablet/laptop can never be listed, tiered, peeked or blocked — and the user is never told the catalogue's boundary is the handset, not their attention.

### [MEDIUM] Installing Monolith on the second device doesn't extend the guard — it forks into an independent, unsynced second Monolith
*Location:* `app/build.gradle.kts:104-122; app/src/main/AndroidManifest.xml:25-30; app/src/main/res/xml/data_extraction_rules.xml`

There is no account, server or sync layer: in app/build.gradle.kts:104-122 Firebase, Firestore, Auth, Credentials, OkHttp and Retrofit are all commented out. AndroidManifest.xml:25-30 sets allowBackup=false and both res/xml backup/extraction rules exclude every domain including device-transfer, deliberately (PARK plate, ParkDrawer.kt:121). So installing Monolith on the tablet creates a second, stranger Monolith: separate timer, routines, shield list and receipts; arming the phone does nothing on the tablet, and the user's focus history is split across two devices that never reconcile.

---

## Angle: update-day (5 findings)

**Verdict:** On update day Monolith says almost nothing: a new phone or wiped install is silently reclassified as a fresh install with the reset notice pre-dismissed, the only no-transfer warning is a trivia line in the PARK plate, the backup export was killed with no replacement, and the update event itself leaves no user-visible trace of what survived.

### [CRITICAL] Wiped/new-phone open is reclassified as a fresh install and the only reset notice is pre-dismissed
*Location:* `app/src/main/java/com/example/data/MonolithRepository.kt:200-213; app/src/main/java/com/example/ui/MonolithViewModel.kt:506-510`

An empty database is classified as a fresh install, not a loss: MonolithRepository.kt:200-213 inserts routineTargets() and writes routineReseedV1Done=true AND routineReseedNoteDismissed=true, so the one reset notice the app has is pre-dismissed. MonolithViewModel.kt:506-510 states the suppression plainly: 'Fresh installs never see it (reseed flag and dismiss flag are both set at seed time)'. The same seed path repopulates profiles (MonolithRepository.kt:296+) and starter tasks, so the returning user sees a plausible, populated app — factory routines, loadouts, a task — with no banner saying their sessions, history, day words, held notifications or settings did not carry over, and no way to tell a wipe from a first install.

### [HIGH] The only 'your data never leaves this phone' warning is one buried line in the PARK spec plate
*Location:* `app/src/main/java/com/example/ui/components/ParkDrawer.kt:115-124`

grep over app/src/main/java finds exactly one user-facing occurrence of the warning: ParkDrawer.kt:121, 'SITES: KEPT WITH APPS ON SHIELD · DATA: ON THIS PHONE ONLY', printed as a secondary-ink line in PLATE_BODY (ParkDrawer.kt:115-124) sandwiched between 'STUDY HOURS: IN YPT' and 'SOUND: FLOAT-PCM, 4 PARTIALS · POWER: YOUR PHONE', inside the PARK drawer the user must deliberately open. There is no equivalent disclosure on first run, on Deck, on Shield, in Drawer/history, or at any moment before a phone switch — the user learns data is phone-bound, if ever, only in trivia type after the loss is already irreversible.

### [HIGH] No self-help path exists: the backup export was deliberately killed and never replaced with any export/import
*Location:* `app/src/main/java/com/example/data/CryptoIntegrityManager.kt:15-19`

CryptoIntegrityManager.kt:15-19 records the decision in its own words: 'The AES-GCM backup export that used to live here was killed Oct 29, 2026: its payload had no egress and no restore - theatre, not a backup.' Nothing replaced it: grep for export/import UI (ACTION_CREATE_DOCUMENT, ACTION_SEND, createDocument, export) across app/src/main/java/com/example/ui returns no hits. A user who heeds the plate line and tries to move their history to a new phone by hand has literally no button — no file, no share sheet, no restore flow — so self-help before switching phones is impossible even for a warned, motivated user.

### [MEDIUM] The update itself is invisible: PACKAGE_REPLACED is handled silently and the app never tells the user an update happened or what survived it
*Location:* `app/src/main/java/com/example/service/MonolithBootReceiver.kt:28-52`

MonolithBootReceiver.kt:28-52 handles ACTION_MY_PACKAGE_REPLACED purely internally: it maps it to RoutineHeartbeat.Source.PACKAGE_REPLACED, ticks the heartbeat and restarts the shield if a session remains. No flag, preference or UI state records that an update happened, and grep finds no BuildConfig/packageInfo/version read anywhere in app/src/main/java — the app never stores or compares a previous version. On next open the user gets the identical board whether the update applied cleanly, applied and reset state, or never applied at all: no 'updated' line, no 'your history/routines are intact' confirmation, no post-update permission re-check summary. Silence is the only update receipt.

### [MEDIUM] The one migration notice that does exist understates the event and deletes itself when the user obeys it
*Location:* `app/src/main/java/com/example/ui/screens/ShieldMatrixScreen.kt:313-324; app/src/main/java/com/example/ui/MonolithViewModel.kt:515-524; app/src/main/java/com/example/data/MonolithRepository.kt:918-924`

The sole migration notice, ShieldMatrixScreen.kt:313-324, prints only 'SCHEDULE RESET TO YOUR DAY — TAP ANY ROW TO CHANGE IT' — it never says an update caused the reset, what was changed, or what was preserved. It then self-erases two ways: MonolithViewModel.kt:515-524 hides it once the newest target edit is 14 days old, and MonolithRepository.kt:918-924 (dismissRoutineResetNote) dismisses it permanently on ANY routine edit — i.e. doing exactly what the note instructs ('tap any row to change it') destroys the only record that a reset happened, even though the other reset routines were never reviewed.

---

## Angle: deleting-in-anger (5 findings)

**Verdict:** As lived, the uninstall guard is pure obstruction with inverted timing: it hard-bounces the owner mid-session for any installer window or even for viewing App Info, offers no path to the clean exit its own admin warning prescribes and logs surrender as a focus win — yet it is completely absent after a MISSED judgement and on the Play Store route, the two places an angry owner actually deletes from.

### [HIGH] Guard cannot tell Monolith from any other app: every installer window mid-session is treated as an attack on Monolith
*Location:* `app/src/main/java/com/example/service/MonolithAccessibilityService.kt:206-215,309-316`

MonolithAccessibilityService.kt:310-316 returns true purely on the foreground package being one of 4 installer packages (com.google.android.packageinstaller, com.android.packageinstaller, com.samsung.android.packageinstaller, com.miui.packageinstaller) — the target app of the install/uninstall is never read. The call site (206-215) then fires GLOBAL_ACTION_BACK and an airlock labelled appName="System Settings Guard", reasonBadge="Uninstall Guard". Mid-session the owner cannot delete ANY app — including deleting the distracting app itself in a moment of resolve — or sideload-install/update anything, without being bounced to Monolith as if they were attacking it.

### [HIGH] After a judged MISSED window — one of the two anger triggers — the uninstall guard does not exist at all
*Location:* `app/src/main/java/com/example/service/MonolithAccessibilityService.kt:194-206; app/src/main/java/com/example/service/MonolithDeviceAdminReceiver.kt:13-15`

MonolithAccessibilityService.kt:194-195: the whole event coroutine returns early unless dao.getActiveSessionSnapshot() is an active session, before the tamper check at line 206 ever runs. A MISSED judgement lands when a routine window closes — precisely when no session is active — so at the moment of grievance Settings, Force stop and Uninstall are completely unwatched. The only remaining layer, MonolithDeviceAdminReceiver.kt:13-15, is a dismissible warning string on the deactivate screen that falsely claims it is 'protecting your active study session' when there is none. Protection is maximal mid-craving and zero at the post-judgement moment that actually provokes deletion.

### [HIGH] Merely opening Monolith's App Info page fires the guard — viewing is indistinguishable from tampering
*Location:* `app/src/main/java/com/example/service/MonolithAccessibilityService.kt:206-207,318-327`

MonolithAccessibilityService.kt:318-327: for com.android.settings the check is dump.contains("monolith") && dump contains any of "force stop"/"deactivate"/"uninstall"/"clear storage"/"clear data"/"device admin" over the whole window text. Monolith's own App Info page statically displays those button labels, so the predicate is true on page load, before any tap: line 207 performGlobalAction(GLOBAL_ACTION_BACK) plus the airlock fire on viewing alone. Mid-session the owner cannot open App Info to check Monolith's storage, permissions or battery use — the guard treats reading the page as pressing the buttons.

### [HIGH] The bounce is a dead end that contradicts the admin warning — and surrendering is logged as 'Stayed Focused'
*Location:* `app/src/main/java/com/example/ui/MonolithViewModel.kt:2337,2428-2437; app/src/main/java/com/example/data/MonolithRepository.kt:2110-2123; app/src/main/java/com/example/MainActivity.kt:797`

For InterceptPath.UNINSTALL_GUARD, canHonourPeekLease is false (MonolithAccessibilityService.kt:44-57), so MonolithViewModel.kt:2337 sets peekWithheld=null and the Airlock (AirlockAndDisarmOverlays.kt, AirlockInterceptOverlay) renders only the 5s countdown and RETURN TO FOCUS — no end-session verb, despite MonolithDeviceAdminReceiver.kt:14 instructing the owner to 'End your focus block cleanly before deactivating'. Worse, RETURN TO FOCUS is wired to transmuteImpulseAndReturn (MainActivity.kt:797), which records the intercept with outcome="Stayed Focused" (MonolithViewModel.kt:2428-2437), and recordIntercept increments the session's phantomReachesBlocked (MonolithRepository.kt:2110-2123). Backing down from deleting the app is written into the session record as a blocked distraction reach and a focus victory.

### [MEDIUM] The Play Store uninstall route is entirely unwatched — the guard only patrols Settings and 4 installer packages
*Location:* `app/src/main/java/com/example/service/MonolithAccessibilityService.kt:309-330`

grep for com.android.vending across app/src/main/java returns zero hits. The guard's entire caseload is the 4 installer packages (MonolithAccessibilityService.kt:310-315) plus a text scan that only runs when the foreground package is com.android.settings (line 318). The Play Store surface — where the owner lands from a notification, a search, or the app drawer’s store link and taps Uninstall on Monolith's own listing — is never inspected, so that deletion route meets no Monolith airlock, no warning copy, and no persuasion of any kind while the Settings route is hard-bounced.

---

## Angle: morning-after-a-failed-day (5 findings)

**Verdict:** The morning after a failed day the app offers neither reckoning nor restart: it silently stamps MISSED in a ledger no screen displays, lets the failed day vanish from the Drawer unexplainably (the REST/ILL/FAMILY word is today-only), shows the spent token as a bare '0/1 WEEK' with no return date, and presents yesterday's abandoned task as today's NOW — opening the app is indistinguishable from any other day, except everything he needs to recover is missing.

### [HIGH] Yesterday can never be explained: the day-word (REST/ILL/FAMILY) is today-only, so the morning after is too late
*Location:* `ui/screens/DrawerScreen.kt DayHeading (isToday gate); ui/MonolithViewModel.kt:1955-1964; data/DayWordStore.kt:56`

The only mercy/annotation mechanism for a bad day is the day word (REST / ILL / FAMILY, DayWordStore.kt:5, WORDS at DayWordStore.kt:56), but it can only ever be attached to today: the picker renders only under `if (isToday && !isSealed)` in DayHeading, and MonolithViewModel.declareTodayWord (ui/MonolithViewModel.kt:1955-1964) hardcodes the filing date from System.currentTimeMillis(). The code even names the use case — DrawerScreen.kt:716-719: 'a sick day in the first week… is exactly when a word needs declaring' — but if he was too ill/broken to declare it that day, by the next morning yesterday is permanently unexplainable: no surface lets him say 'yesterday was ILL', so the failure stands in the record as plain failure forever.

### [HIGH] The MISSED verdict is written in permanent ink overnight — and no screen in the app will ever show it to him
*Location:* `service/RoutineHeartbeat.kt:89-122; data/MonolithRepository.kt:1030-1055; data/MonolithModels.kt:313; ui/screens/ShieldMatrixScreen.kt:675-723`

Overnight/on-open, RoutineHeartbeat.reconcileOnce (service/RoutineHeartbeat.kt:89-122) finalises yesterday's routine sessions via finaliseRoutineSession, which stamps routineOutcome MISSED when attended < floor (data/MonolithRepository.kt:1030-1032) — a stamp MonolithModels.kt:313 declares immutable ('Stamps are immutable'). Yet routineOutcome is rendered on no user surface: Shield's routine rows print only name, window times, days, loadout and ON/OFF (ui/screens/ShieldMatrixScreen.kt:675-723), the Drawer stores only COMPLETED tasks (ui/screens/DrawerScreen.kt:61-68), and sessionHistory feeds only the finish/receipt controller and totals (ui/MonolithViewModel.kt:446, 1164). He is convicted in a ledger he cannot open: no acknowledgement, no reason (evidence coverage is never shown), no appeal, no 'start again' cue.

### [MEDIUM] A zero-completion day does not exist in the Drawer: failed days silently vanish from the kept record
*Location:* `ui/screens/DrawerScreen.kt:84-111`

buildDayGroups (ui/screens/DrawerScreen.kt:84-111) materialises a date heading only for dates that have completed-task rows, a declared day word, or are today. A failed day with zero completions and no word gets no heading at all in Landing or Month view — the month reads as an unbroken run of productive days with the worst days simply absent. Combined with the today-only word, the kept record doesn't present the wreckage or forgive it; it deletes the day from history by omission.

### [MEDIUM] Token spent yesterday reads '0/1 WEEK' with no renewal date anywhere — he cannot learn from the app when it comes back
*Location:* `ui/screens/TactileDeckScreen.kt:1047-1048; data/MonolithWeek.kt:9-19`

The valve chip prints only monoBadge = "$weeklyEmergencyTokensRemaining/1 WEEK" (ui/screens/TactileDeckScreen.kt:1047-1048). The renewal fact lives only in code: the week 'turns over Monday 00:00 local civil time' (data/MonolithWeek.kt:9-19). No UI string anywhere names Monday, a reset, or a return date (grep of ui/ for Monday/resets/returns finds nothing). On Saturday morning after a Friday spend, the app tells him 0/1 and nothing about when — or even whether — the token comes back; the one recovery fact he needs is the one fact never printed.

### [MEDIUM] No day boundary in the queue: yesterday's abandoned task greets him as today's 'NOW', indistinguishable from fresh work
*Location:* `data/MonolithModels.kt:101-124; ui/screens/TactileDeckScreen.kt:791-800`

FocusTaskEntity (data/MonolithModels.kt:101-124) has no created-at or date field at all — only status, queueOrder and accumulated actualSecondsSpent. So yesterday's half-finished ACTIVE task is still the Deck's 'NOW' the next morning (ui/screens/TactileDeckScreen.kt:791-800 prints activeTask.title under NOW), visually identical to work started minutes ago, with its stale spent seconds folded in. Nothing marks it as carried over, nothing asks whether yesterday's plan still stands, and there is no fresh-slate moment: the failed day's queue just silently becomes today's, presented as if it were today's intention.

---

## Angle: reading-the-words (5 findings)

**Verdict:** Read cold, the app speaks a private studio dialect in which the same things are renamed on every screen, two opposite breaks share one word, and its two most official documents — the receipt and the completion notification — direct the user to places (YPT, a Break tab with a desk toy) that the app itself never names or defines.

### [HIGH] Receipt footer sends your study record to 'YPT' — an undefined acronym the PARK plate contradicts
*Location:* `app/src/main/java/com/example/ui/components/ReceiptSheet.kt:176; app/src/main/java/com/example/ui/components/ParkDrawer.kt:122`

ReceiptSheet.kt:176 prints the footer 'THE MACHINE KEEPS ITS OWN LOG · YOUR STUDY RECORD LIVES IN YPT' on every receipt, while ParkDrawer.kt:122 (the PARK spec plate) prints 'STUDY HOURS: IN YPT' — the two screens disagree about what YPT holds (RECORD vs HOURS). 'YPT' is never expanded or defined anywhere in the UI: grep of app/src/main/java finds it only at those two prints. Worse, the receipt's claim is false in the app's own words: the app demonstrably keeps a study record itself — DrawerScreen.kt prints 'STORED'/'NOTHING STORED YET' over finished tasks and ReceiptSheet itself is that record — so the slip the user is holding denies the record the user is holding.

### [HIGH] 'Session Complete' notification promises an 'Unlocked desk toy' in a 'Break tab' that does not exist
*Location:* `app/src/main/java/com/example/service/MonolithShieldService.kt:116`

MonolithShieldService.kt:116, on session end: updateNotification("Session Complete", "Unlocked desk toy ready in your Break tab."). No 'Break tab' exists in the app's own vocabulary: the rail prints only DECK · SHIELD · TASKS · LEDGER (MonolithRailView.kt:34) and destinations are labelled Focus/Tasks/Shield/Receipts (MonolithViewModel.kt:49-52). 'Desk toy' appears nowhere else in app/src/main/java (single grep hit, this line) — the collectible-like objects the app does have live under CURIOS/CABINET in DrawerScreen.kt. A user who taps this notification goes hunting for a tab and a toy that no screen names.

### [HIGH] Two opposite things are both called a break: the free automatic pause and the once-a-week session-killer
*Location:* `app/src/main/java/com/example/ui/screens/TactileDeckScreen.kt:616,1047; app/src/main/java/com/example/service/MonolithShieldService.kt:126; app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:763`

Three different events share the word BREAK. (a) The free, automatic 10-minute pause between blocks: Deck eyebrow prints just 'BREAK · {n} MIN' (TactileDeckScreen.kt:616) and the shield notification calls it 'Safe Break %02d:%02d' (MonolithShieldService.kt:126). (b) The once-a-week, session-ending escape: Deck chip 'EMERGENCY BREAK VALVE (60S HOLD)' with badge '{n}/1 WEEK' (TactileDeckScreen.kt:1047-1048), overlay title 'EMERGENCY BREAK' with body 'This ends the session early. It is the only break this week.' (AirlockAndDisarmOverlays.kt:763,774), valve label 'HOLD FOR WEEKLY EMERGENCY BREAK' (BlockitSuitcaseAndSwitchesViews.kt:695), plate 'ONE BREAK A WEEK' (ParkDrawer.kt:111). Nothing in the Deck's bare 'BREAK · 7 MIN' tells a cold user it is not the weekly one, and nothing in 'EMERGENCY BREAK' tells them the other break is free and automatic.

### [MEDIUM] Every screen renames the same rulings and rooms — SHUT/Blocked/Locked/HELD, 2m Knock/Peek, DECK/Focus, LEDGER/VAULT/Receipts/STORED/CABINET
*Location:* `app/src/main/java/com/example/data/MonolithModels.kt:19-30; app/src/main/java/com/example/ui/screens/ShieldMatrixScreen.kt:833; app/src/main/java/com/example/ui/views/MonolithRailView.kt:34; app/src/main/java/com/example/ui/MonolithViewModel.kt:49-52,2079,2152`

The LOCKED tier alone carries four printed names in one enum: label="Shut", badge="Blocked", displayLabel="Blocked" (MonolithModels.kt:19-24), and the Shield tally/sled print it as 'SHUT' ('9 SHUT · 4 PEEK · 61 OPEN', ShieldMatrixScreen.kt:833,843-855) while the receipt prints the same apps as 'HELD {n} APPS' (ReceiptSheet.kt:131), the airlock badge defaults to 'Locked' (MonolithViewModel.kt:157; MonolithShieldService.kt:185 fallback "Locked"), and the PARK plate teaches 'INK = SHUT' (ParkDrawer.kt:113). The peek tier is label="2m Knock" vs badge/displayLabel="2m Peek" in the same enum (MonolithModels.kt:25-30), then 'PEEK' on Shield and 'EMERGENCY PEEK' in the airlock (AirlockAndDisarmOverlays.kt:665). Destinations rename too: rail 'DECK/LEDGER' (MonolithRailView.kt:34) vs destination labels 'Focus/Receipts' (MonolithViewModel.kt:49-52) vs the history screen's own headers 'STORED' (DrawerScreen.kt:522), 'CABINET' (DrawerScreen.kt:348) and button 'CURIOS' (DrawerScreen.kt:738); and 'VAULT' means the receipts destination (MonolithViewModel.kt:52) yet ratchet warnings use it for a live session: 'CANNOT WEAKEN … DURING ACTIVE VAULT' / 'DURING VAULT LOCKDOWN' (MonolithViewModel.kt:2079,2152).

### [MEDIUM] Proof/BIND/SEAL sentences are studio code with no glossary, and 'ARM SESSION NOW' is contradicted by 'NOTHING WAS SEALED'
*Location:* `app/src/main/java/com/example/ui/components/MonolithCustomComponents.kt:383-421,545; app/src/main/java/com/example/ui/ArmProvingController.kt:627; app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt:512,535`

The Deck's proof strip stamps pairs no glossary anywhere defines: 'BIND HELD · THIS VISIT' / 'PROOF: LIVE BIND', 'PROOF: BIND ONLY', 'PROOF: POSITIVE', 'BIND NOT HELD' / 'PROOF: POSITIVE', 'PROOF: STALE' (MonolithCustomComponents.kt:383-421), and PARK's GUARD board prints 'LAST PROVED' with GRANTED/MISSING/STALE (ParkDrawer.kt:308-344). When arming fails the only explanation printed is studio code: 'SESSION-ROW — NO PROOF. NOTHING WAS SEALED.' (ArmProvingController.kt:627) and '{link} DIDN'T ANSWER — STILL PROVING.' (MonolithCustomComponents.kt:545). This directly contradicts the pre-flight sheet's plain promise on the same flow — 'Grant Accessibility & Usage Access so Monolith can intercept distracting apps, or arm the session immediately.' with button 'COMMIT & ARM SESSION NOW' (AirlockAndDisarmOverlays.kt:512,535): the words say NOW/immediately, the machine's own next sentence can say nothing was sealed and name a link the user has never heard of.

---
