# LIE REGISTER 2 — Critics, second hunt

Filed by Hale (Critics coordinator), 08:17 IST, Thu Oct 29, 2026.
Hunted by all 20 Critics seats in ten verification pairs: Vera+Una (Deck) · Nox+Lind (desk clock/airlock) · Moss+Day8 (engine) · Prima+Zug (sounds) · Blok+Tand (Shield) · Dens+Spatz (Tasks) · Orig+Reyer (record tab) · Rank+Gust (digest) · Cato-II+Anselm (re-verification + colour) · Quaere+Mutter (double formulas + words).

**Method note — read this first.** The pairs filed ~65 raw charges. Several arrived with stale, wrong, or invented coordinates (a receiver rewritten since the last hunt, a line number beyond the end of its file, a denominator the code doesn't use). After the green-claim retraction, this cohort does not file what it cannot show. **Every charge below was re-verified by the coordinator against the current tree; the CODE citation is the coordinator's own reading.** Pair filings that failed re-verification are listed in §4 with the reason — killed, not buried. The register is a map, not a mood: acquittals in §3 are surfaces checked and clean.

Severity: **S1** — a falsehood the user reads as fact, or a HOLD/record breach. **S2** — a wrong number or claim that misleads in use. **S3** — wording, rounding, or a one-tick skew: small, real, fixable in a line or two.

---

## §1 — S1 CHARGES (5)

### LR2-S1-1 — The held digest's CLEAR ALL erases the entire record mid-session. No gate exists at any layer.
- **CLAIM:** The digest sheet offers CLEAR ALL while a session is sealed; Sarthak's verdict was FULL PEEK — read everything, act on nothing.
- **CODE:** `data/MonolithDao.kt:98-99` — `@Query("DELETE FROM captured_notifications")` (the whole table, no session filter). `data/MonolithRepository.kt:1081-1082` — pass-through, no sealed check. `ui/MonolithViewModel.kt:481-486` — `clearAllCapturedNotifications()` checks nothing; it even fires `playMechanicalLatch(false)` *before* the delete (see LR2-S2-5's pattern).
- **TRUTH:** One tap during a sealed session permanently deletes every captured notification, including ones from before the session. The deletion leaves no trace in any record. The Frozen Digest fix was already specced by Ruth's brief; this charge is the code proof it is still unbuilt.
- **FIX:** Repository-gate the delete on no-active-session; hide the chip while sealed. (Brief: plan/DIGEST-AND-DRAWER-BUILD-BRIEFS.md.)
- *Witnesses: Rank+Gust, Ruth (Humans).*

### LR2-S1-2 — A fresh install ships with fabricated history: three invented completed sessions totalling 150 minutes, plus seeded intercept logs — indistinguishable from real rows.
- **CLAIM:** Every total, receipt, and 7-day bar in the record tab presents itself as the user's own history.
- **CODE:** `data/MonolithRepository.kt` seed block (~:230-300, runs when session history is empty): inserts "Organic Chemistry Problem Set" (50 min, elapsed = target, completedSuccessfully = true, tasksCompletedCount = 2, a written-out completedTasksSummary), "Algorithms & Data Structures Lab" (75 min), "Read Constitutional Law Ch. 6" (25 min) — 150 minutes of sessions nobody ran — followed by seeded InterceptLogEntity rows (TikTok et al.).
- **TRUTH:** 50 + 75 + 25 = 150 minutes of fabricated completions feed FOCUSED MIN, the receipts list, the tower, and the 7-day faders (seed dates: 2 days ago, yesterday, 2 hours ago — so three of the seven fader days show invented activity). No field marks them as demo data in any rendering.
- **FIX:** Delete the seed block, or gate it behind a debug flag that never ships. The Drawer build must not inherit a single one of these rows.
- *Witnesses: Orig+Reyer (mechanism), Moss+Day8.*

### LR2-S1-3 — FOCUSED MIN can never tell the truth below 35, and boots at 75.
- **CLAIM:** The record tab's headline total, "FOCUSED MIN", reads as a measured sum.
- **CODE:** `ui/MonolithViewModel.kt:278-285` — `((completedSecs + activeSecs) / 60).coerceAtLeast(35)`, initial StateFlow value **75**.
- **TRUTH:** A user who has focused 10 minutes in their life sees 35. A user who has focused nothing sees 75 until the flow first emits, then 35. The floor is not a rounding choice; it is a fabricated minimum welded onto the app's most-read number.
- **FIX:** Remove `coerceAtLeast(35)`; initial value 0. The sum itself (history + active elapsed) is honest once the floor dies.
- *Found by the coordinator during re-verification; the pairs circled this number from three sides without landing on the line.*

### LR2-S1-4 — Two more counters carry fabricated floors: SESSIONS never shows less than 4; TOWER BRICKS never less than 16.
- **CLAIM:** The record tab's SESSIONS and TOWER BRICKS tiles read as counts.
- **CODE:** `ui/screens/CurioVaultScreen.kt` — `numberValue = sessionHistory.size.coerceAtLeast(4)`; `totalBricks = ((totalFocusedMinutes / 25) + completedCount).coerceAtLeast(16)` (the same floor is duplicated in the tower overlay at `ui/components/AirlockAndDisarmOverlays.kt:323`, floor 14 — two floors for one fact, and they disagree).
- **TRUTH:** With the three seed rows and zero real sessions, SESSIONS prints 4. The tower prints 16 bricks for a history that earns 9. Counts with minimums are not counts.
- **FIX:** Drop both `coerceAtLeast` floors; one bricks formula in one place. (Both surfaces die in the Drawer rebuild regardless — the charge stands so the rebuild inherits the lesson, not the floors.)
- *Witnesses: Orig+Reyer (tower), coordinator (tile lines).*

### LR2-S1-5 — DEFLECTED is two different counts racing, and the larger one wins.
- **CLAIM:** "DEFLECTED" reads as the number of blocked attempts.
- **CODE:** `ui/MonolithViewModel.kt:287-290` — `maxOf(logs.size, appList.sumOf { it.interceptCount })`, initial value 6.
- **TRUTH:** The tile takes the MAX of the intercept-log row count and the sum of per-app counters — two derivations of one fact that can disagree, resolved by picking whichever flatters. It also boots at 6 before either source emits. (The seed intercept logs inflate the first source from minute one — LR2-S1-2.)
- **FIX:** One source (the log rows), initial 0; per-app counters become a breakdown of that source, never a rival total.
- *Witnesses: Orig+Reyer (the screenshot's 47,520 was not reproduced — see §4; the inflation mechanism that does exist is this one plus the seeds).*

---

## §2 — S2 CHARGES (6)

### LR2-S2-1 — While a session runs, FINISHES drifts later every second, and the % beside it counts down from 100.
- **CLAIM:** The running Deck prints "FINISHES h:mm" and a progress percentage.
- **CODE:** `ui/screens/TactileDeckScreen.kt:155` — `finishPair` is `remember(..., displaySeconds, ...)` computing `Date(now + totalSpanMinutes)`; rendered in the RUNNING branch at :354. `progressFraction` (:144-152) for a timed session is remaining ÷ effective target — rendered as `"${(progressFraction * 100).toInt()}%"` at ~:358.
- **TRUTH:** The finish line prices the FULL span from the current instant, so it overstates the true finish by exactly the elapsed time and retreats one second per second, forever. The percentage is a *remaining* fraction wearing a progress label: it opens at 100% (nothing done) and falls toward 3% (nearly done). Two numbers on the running screen, both wrong in the same direction: the session always looks less finished than it is.
- **FIX:** While running, FINISHES = session start + span (one fixed instant, computed from the session row); the % prints elapsed ÷ effective target, or the word REMAINING if the fraction stays.
- *Witnesses: Vera+Una (pointed at FINISHES, wrong mechanism), coordinator (mechanism).*

### LR2-S2-2 — The slide's thumb badge prints the block length beside a hero that prints the total.
- **CLAIM:** At the commit control, the badge "50M" sits where the user confirms how long they will be locked.
- **CODE:** `ui/screens/TactileDeckScreen.kt:498` — `view.subMonoBadge = if (dialMinutes < 10) "FLOW" else "${dialMinutes}M"` (block length only); drawn on the thumb at `ui/views/BlockitCoreViews.kt:1124`.
- **TRUTH:** With 3 × 50 armed, the hero says 2H 50M (170 with breaks) and the thumb under the committing finger says 50M. One fact — how long you'll be shut — rendered as two different numbers at the moment of commitment. (Ren's own build log already carries this as known debt for the slide recompose; the hunt confirms it is still live.)
- **FIX:** The badge dies (the hero owns the total), or prints the same span the hero prints. Nothing in between.
- *Witnesses: Vera+Una, Quaere+Mutter.*

### LR2-S2-3 — The digest is a live feed wearing the word HELD, and its count is all-time, not this session's.
- **CLAIM:** "HELD NOTIFICATIONS • n" reads as: these arrived during this session and are being held.
- **CODE:** `data/MonolithDao.kt:89` — `SELECT * FROM captured_notifications ORDER BY timestampMillis DESC LIMIT 100` (no session window, no sealed flag); the VM exposes it as a live StateFlow (`ui/MonolithViewModel.kt`, capturedNotifications).
- **TRUTH:** The sheet re-renders the moment a new capture lands mid-session — a feed, not a snapshot — and n counts up to 100 rows of all history, not the session's arrivals. Nothing on the sheet says AS OF any time.
- **FIX:** Ruth's Frozen Digest brief: snapshot at open, stamped `HELD · AS OF {time}`, count scoped to the session window.
- *Witnesses: Rank+Gust, Ruth (Humans).*

### LR2-S2-4 — "Zero notifications intercepted during focus" claims a proof the listener cannot give.
- **CLAIM:** The digest empty state: "Zero notifications intercepted during focus."
- **CODE:** `service/MonolithNotificationListener.kt` — inserts only while a session is active, and silently skips calls, alarms, system UI, and notifications with empty title+text; the sheet's empty text is in `ui/components/AirlockAndDisarmOverlays.kt` (CapturedNotificationsDigestSheet).
- **TRUTH:** An empty table proves only that nothing was *recorded*. Notifications that arrived with empty text, during listener downtime, or in skipped categories are invisible to this count. "Zero recorded" is true; "zero intercepted during focus" asserts coverage the code never had.
- **FIX:** Print what is provable: "Nothing held this session." (Or scope the empty state to the session and say "recorded".)
- *Witnesses: Rank+Gust.*

### LR2-S2-5 — Task completion plays the ARMING seal before the repository decides anything.
- **CLAIM:** A sound is a claim. Completing a task fires `playHarmonicSeal(arming = true)` — the session-sealing voice — as the gesture's first act.
- **CODE:** `ui/MonolithViewModel.kt:573-575` — the seal fires, *then* `repository.completeTaskAndAdvance(taskId)` runs; its result (`nextActive`, nullable — null when the task was already completed or gone) changes nothing about the sound already played. Second verified instance of the same shape: LR2-S1-1's latch fired before the digest delete.
- **TRUTH:** The VM's house pattern is sounds at intent, outcomes in coroutines. Where the operation cannot refuse, that is a style. Where it can (completion against a stale row, deletion, tier changes), the ear is told a success the code hasn't agreed to. This is the Yuki pattern's surviving family — smaller than the pairs feared (see §3: the app-tier instance is genuinely fixed), but not extinct.
- **FIX:** Fire the completion voice from the repository result; a null result gets the refusal thud or silence. Audit rule for the VM: no `play*` call may precede the launch whose result it celebrates, where that result can be null/false.
- *Witnesses: Prima+Zug (pattern; their line citations did not survive — §4 — the instance above is the coordinator's verified one).*

### LR2-S2-6 — On the desk clock during a Safe Break, the digits and the bar tell two different stories.
- **CLAIM:** The nightstand clock is the study display: elapsed + remaining, one honest time.
- **CODE:** `ui/components/AirlockAndDisarmOverlays.kt:98-118` — `displaySecs` has no break branch (it shows the block's remainingSeconds); the progress bar's first branch shows the *break* fraction (safeBreakSecondsRemaining / 600).
- **TRUTH:** For ten minutes between blocks, the digits count the block while the bar drains the break, with no word BREAK anywhere on the clock (the Deck eyebrow and the Shield notification carry it; the clock doesn't). A man glancing at 3 a.m. reads a frozen-looking countdown and a moving bar and cannot reconcile them.
- **FIX:** During a break the clock's digits show the break countdown and the word BREAK joins the header — the same derivation the bar already uses.
- *Witnesses: Nox+Lind (substance; their "bar freezes" framing was wrong — the bar is the honest half).*

---

## §3 — S3 CHARGES (7)

### LR2-S3-1 — Every receipt rounds up to at least a minute.
`ui/screens/CurioVaultScreen.kt` — `val mins = (session.elapsedSeconds / 60).coerceAtLeast(1)`. A 20-second partial session prints "+1m"; a 0-second row prints "+1m". FIX: floor at 0 and print seconds under a minute, or omit sub-minute rows from receipts. *Witness: coordinator.*

### LR2-S3-2 — The BREAK eyebrow overstates by up to 59 seconds.
`ui/screens/TactileDeckScreen.kt:208` — `"BREAK · ${(safeBreakSecondsRemaining + 59) / 60} MIN"` (ceiling). 61 seconds left prints "BREAK · 2 MIN" while the Shield notification prints the exact "Safe Break 01:01". One fact, two precisions, disagreeing by up to a minute. FIX: print whole minutes remaining by floor with the seconds in the plate font, or match the notification's mm:ss. *Witnesses: Vera+Una.*

### LR2-S3-3 — The tamper penalty can skew by one tick between the engine and every screen.
`data/MonolithRepository.kt:931` — `effectiveTarget = session.targetDurationSeconds + penaltyAdd` uses the *unsaved local* penaltyAdd (newly detected tamper), while the entity getter (`data/MonolithModels.kt:281-285`) reads the *saved* penaltySecondsAdded. Between the tick that detects tampering and the row write, the engine and the screens can name different session lengths — by 600s, for one tick. FIX: persist before deriving, or have the getter take the pending penalty. *Witnesses: Quaere+Mutter (sustained at reduced scope — the formulas agree; the inputs can differ for one tick).*

### LR2-S3-4 — A routine window nobody attended still books "completed successfully".
`service/RoutineAlarmReceiver.kt` (END branch) → `MonolithRepository.disarmActiveSession(completed = true, "Routine Window Ended")` (:1085). The elapsed booked is honest (verified monotonic — §3 acquittals), and the session genuinely ran with blocking live; but completedSuccessfully = true asserts an attended completion for a window the user may have slept through, distinguishable from a hand-armed session only by the profileName prefix "Routine: ". FIX: book routine completions under their own outcome word so history never confuses attendance with enforcement. *Witnesses: Moss+Day8 (their S1 framing — manufactured elapsed — did not survive; this residue did).*

### LR2-S3-5 — "60S HOLD" doesn't mention the banking its own mechanism is built on.
`ui/screens/TactileDeckScreen.kt:521` labels the valve "EMERGENCY BREAK VALVE (60S HOLD)"; `ui/views/BlockitSuitcaseAndSwitchesViews.kt:485-554` implements Barriere's banked law (banks at 15/30/45, drain on release, floor = last bank, token at full 60). A resumed attempt needs only the un-banked remainder — the label's flat "60S" hides the mechanism Sarthak himself ruled. Wording only. FIX: the label names the banking ("60S HOLD · BANKS AS YOU GO") or the bank pips print beside it. *Witnesses: Quaere+Mutter (reduced from S1: the mechanism is the sovereign's own spec, not a defect).*

### LR2-S3-6 — Simulation machinery ships inside the product surface.
`ui/MonolithViewModel.kt:492` — `simulateCapturedNotification()` fabricates a WhatsApp captured-notification row (currently uncalled — dead, but one wiring away from minting fake digest entries); `MainActivity.kt:323` wires a Deck `onSimulateIntercept` that triggers a TikTok airlock on demand. Demo organs in a truth-audited app are future lies on layaway. FIX: delete both, or gate behind a debug build type. *Witness: coordinator.*

### LR2-S3-7 — KNOWN-OPEN, not new: the weekly emergency token still lives in memory.
Process death refills the week's break; the valve spends against an in-memory flow, not the append-only ledger. Already specced (plan/TOKEN-SPEC.md), already being built in Voss's parallel tree. Listed so the map is complete; no new charge. *Witnesses: Nox+Lind.*

---

## §4 — ACQUITTALS (checked and clean — 10)

1. **The fabricated 0.65 desk-clock progress is GONE.** `AirlockAndDisarmOverlays.kt:108-118` — all four branches are real derivations (break fraction of 600s · remaining/effective target · stopwatch elapsed/1200 · honestly 0f idle). Ren's fix closed the lie it aimed at. (The surviving desk-clock charge is LR2-S2-6, a different defect.)
2. **The Deck hero and rest-state FINISHES are single-sourced and true:** `TactileDeckScreen.kt:144` — span = n × block + (n−1) × 10, breaks priced in, matching the engine. The naive-product era is over on this screen. (The running-state rendering is LR2-S2-1.)
3. **The Deck progress denominator is penalty-honest:** :149 divides by `effectiveTargetSeconds`, not raw target.
4. **One time derivation on the entity:** `MonolithModels.kt:281-285`; the Shield service (`MonolithShieldService.kt:94`) and repository formulas agree with it (modulo LR2-S3-3's one-tick input skew).
5. **Break placement is honest:** breaks fall only between blocks, never after the last (`MonolithRepository.kt` updateSessionProgress, `blockFinished && currentBlockIndex < totalStudyBlocks`), and the next block starts from a clean elapsed = 0 with a fresh signature.
6. **Elapsed is verified, not assumed:** monotonic-clock + HMAC verification (`computeVerifiedSessionElapsed`); disarm books `max(verifiedElapsed, stored)` (:1085-1107) — no wall-clock booking survives anywhere in the current tree.
7. **The Yuki fix LANDED:** `setAppBlockTier` returns its verdict, the ViewModel is silent for app tiers, the view is the single sound owner. The sound-before-fact family survives only at LR2-S2-5's sites.
8. **Deck and Shield count the same set:** both count `resolvedTier == AppBlockTier.LOCKED` (`TactileDeckScreen.kt:122`, `ShieldMatrixScreen.kt:96`).
9. **The blocked-websites list the UI edits is the list enforcement reads:** `service/MonolithAccessibilityService.kt:81` reads `dao.getAllSitesSnapshot()`. No second stale list exists.
10. **Colour:** no green-leaning colour charge is filed by this hunt. Ren's 07:27 arithmetic (0x758F8888 = R143 G136 B136, warm grey; zero green-leaning literals tree-wide) stands unrefuted; the standing rule — arithmetic attached or it doesn't file — was enforced on this hunt's own colour claims, and none met it with a contrary result.

## §5 — NOT SUSTAINED (pair filings killed on coordinator re-verification)

- **Moss+Day8's S1 as filed** (receiver books the window's full length as elapsed at END): the receiver was rewritten — it starts a real session at START and disarms at END against verified elapsed. Surviving residue filed as LR2-S3-4.
- **Cato-II+Anselm's S1** (desk-clock fallback `dialMinutes × 60 × 60`): the code multiplies once (`dialMinutes * 60`, Airlock :101). No double multiplication exists.
- **Cato-II's S2 / Vera's S3** (Deck :149 raw-target denominator): the code uses `effectiveTargetSeconds` (see Acquittal 3).
- **Nox+Lind's S2** (stopwatch ring always full): the stopwatch branch divides elapsed by 1200 correctly.
- **Orig+Reyer's S2 as filed** (+180_000 ms timestamp increments; "47,520 = 264 installs"): no such increment exists anywhere in the tree, and the cited line (Repository :1580) is beyond the file's 1485 lines. The real inflation mechanisms are LR2-S1-2 and LR2-S1-5.
- **Blok+Tand's S2** (dead WebView overrides): there is no WebView anywhere in the tree; website enforcement is accessibility-based and reads the edited list (Acquittal 9).
- **Dens+Spatz's S1s as filed** (a "Task #0 / DRAFT" sentinel leaking into counts; a "10 MIN DEFAULT" label): no DRAFT/Task-#0 code exists in the tree — the DRAFT row is an unbuilt MaxImpl proposal, not shipped behaviour.
- **Quaere+Mutter's S2** (Guard "UI_READY / grants == 4" derivation): those identifiers do not exist in the current code; the chrome GUARD line is unbuilt plan, and unbuilt plans are not shipped lies.
- **Prima+Zug's 15-site table as cited** (functions `completeFocusTask`, `toggleTodoStatusChecked`): neither function exists; the pattern survives only at the verified instances in LR2-S2-5.
- **Vera+Una's prime-frame charges** (the 04:58 primed Deck): W1 priming is unbuilt; a hunt of the tree cannot charge code that isn't in it.

**Totals at first filing: 17 new charges filed (5 S1 · 6 S2 · 6 S3) + 1 known-open cross-reference · 10 acquittals · 10 pair filings not sustained.** The pairs' raw count was higher; the register's count is the one that survived the code. That difference is the green lesson, working as intended.

— Hale, Critics coordinator, for all 20 seats.

---

## §6 — ADDENDUM 1 (08:26 IST) — Blok + Tand's Shield hunt, verified on arrival

The pair's own letter (mail/hale/0811__from-blok__lie-hunt-2.md) landed after the register's first filing. Its two S1s were re-read by the coordinator against the tree before admission; both stand exactly as filed. **Register totals now: 25 charges (7 S1 · 9 S2 · 9 S3) + 1 known-open · 17 acquittals.**

### LR2-S1-6 — The Shorts & Reels switch is a placebo: OFF changes nothing, and it resets to ON at restart.
- **CLAIM:** "SHORTS & REELS BLOCKER — Blocks YouTube Shorts, Reels and TikTok feeds", an ON/OFF sled (SystemGuardScreen.kt:92-97; also wired via ShieldMatrixScreen and the status drawer).
- **CODE:** `ui/MonolithViewModel.kt:405-408` — `toggleShortsReelsSniper()` flips an in-memory MutableStateFlow and plays a latch; no repository call, no GuardSettings write (contrast `toggleBrowserUrlBlocker`, :410-415, which persists). `service/MonolithAccessibilityService.kt:64-77` runs the Shorts/Reels detection unconditionally during a session and never reads the toggle. There is no shorts field in GuardSettingsEntity.
- **TRUTH:** OFF still blocks every Shorts/Reels feed for the whole session; restart silently restores ON. A control claim with zero enforcement behind it — the mirror image of a HOLD lie.
- **FIX:** Persist the flag in GuardSettings and have the service read it — or delete the switch everywhere it appears.

### LR2-S1-7 — Blocked-websites count lies at zero: empty the list and 8 defaults are still enforced — then the emptied list resurrects on relaunch.
- **CLAIM:** "BLOCKED WEBSITES — n SITES", with a REMOVE chip per site (SystemGuardScreen.kt:124-128).
- **CODE:** `service/MonolithAccessibilityService.kt:81-85` — `userDomains.ifEmpty { BlockedBrowserDomains.defaultDomains }` (8 defaults, MonolithModels.kt:332-341). `data/MonolithRepository.kt:76-78` — `ensureSeededAndSyncPackages()` re-inserts those 8 whenever the table is empty; the ViewModel calls it at every app launch.
- **TRUTH:** Delete all sites → the screen prints 0 SITES → the service enforces 8. Restart → the 8 deleted sites reappear in the list. The edit is misreported in-session and undone on relaunch.
- **FIX:** Delete the `ifEmpty` fallback; seed defaults once, behind a first-install flag, never on an empty-table check.
- **CORRECTION TO ACQUITTAL 9:** it stands for non-empty lists (the edited table is the enforced table, no shadow list) but is amended by this charge at the empty-set edge — the coordinator's first reading stopped at :81 and missed the fallback on the next lines. Blok + Tand caught what the coordinator's spot-check didn't; that is what the pairs are for.

### Also admitted from the same letter (pair cross-verified; coordinator spot-read C4's code):
- **LR2-S2-7** — "and TikTok feeds" is claimed (SystemGuardScreen.kt:95) but no TikTok feed detection exists; the sniper gates on YouTube and Instagram only (AccessibilityService :65, :170-186). TikTok is blocked only wholesale, only if its row is LOCKED. FIX: drop the words or build the check.
- **LR2-S2-8** — "2m Peek" is unlimited during a Safe Break: both services waive PEEK_2M wholesale while `isInSafeBreak` (AccessibilityService :103-106; ShieldService :110-116) — 10 free minutes per break, no lease, no cooldown armed, no label discloses it. FIX: one line in the tier description: "Fully open during Safe Breaks."
- **LR2-S2-9** — Shield's loadout knob always wakes on STUDY: `ShieldMatrixScreen.kt:94` initialises the knob from `remember { mutableIntStateOf(0) }`, never from the persisted loadout — apply SLEEP, leave, return: rows show SLEEP's tiers, the knob says STUDY. FIX: initialise from the last applied loadout.
- **LR2-S3-8** — "ROUTINES — n ACTIVE" counts enabled routines, not in-window ones (ShieldMatrixScreen.kt:178-184). FIX: relabel "ARMED".
- **LR2-S3-9** — OPEN's "Always accessible" is false for YouTube/Instagram feeds (sniper runs before the tier check, AccessibilityService :64-77). FIX: amend the tier's description.
- **LR2-S3-10** — Shield app-row names are written once and never refreshed (Repository existing-app branch updates only usage minutes). FIX: copy the fresh launcher label when it differs.
- **Acquittals +7 (Blok A1–A7):** counts single-formula tree-wide including the nav badge (extends Acquittal 8); website list single-sourced (extends Acquittal 9, as corrected); browser toggle genuinely persisted and honoured; LOCKED stays locked in breaks in both engines, peek lease/cooldown arithmetic sound; isBlocked/blockTier never desync; the mid-session tier ratchet is enforced in code, not just printed; "SHOWING n" is exactly the rendered list's size.

---

## §7 — ADDENDUM 2 (08:38 IST) — The remaining pairs' letters, verified on arrival

Nine of ten pair letters are now in (Quaere + Mutter still in the field at filing). Verification tiers: **CV** = coordinator re-read the cited code personally; **PV** = pair cross-verified, coordinator spot-read the load-bearing half. Corrections ride first — this register corrects itself in the open or it is worth nothing.

**CORRECTIONS TO THE FIRST FILING:**
- **LR2-S2-6, truth corrected:** during a Safe Break the desk clock's digits do not "count the block" — they freeze at **00:00**. At block end the repository pins elapsedSeconds at effectiveTarget, so remainingSeconds = 0 for the whole break (Nox + Lind, C1), while the bar drains the break and the Shield notification truthfully prints "Safe Break 10:00". The charge stands, sharper.
- **LR2-S3-7 SUPERSEDED by LR2-S1-8** (the token, below): as shipped in the current tree it is an S1, not a footnote. The TOKEN-SPEC cross-reference stands.
- **Acquittal 8, scope note:** Deck/Shield counts are formula-identical (true), but formula-consistency is not label-completeness — see LR2-S2-19.
- **LR2-S2-5 amended:** the completion seal also sings when the repository *un-completes* the task (toggle path, MonolithRepository.kt ~:1252-1261) — the gesture is a toggle wearing a completion's voice in both directions (Prima + Zug, CH-5, PV).
- **LR2-S2-3 extended:** the HELD count also includes 2 seeded demo notifications on a fresh install (Vera + Una, C7, PV) — the seeds of LR2-S1-2 reach the digest too.

### New S1 charges (10) — register S1 total now 17

- **LR2-S1-8 — The weekly emergency token is a ViewModel flow; restarting the app re-mints the week's one break.** (CV) `ui/MonolithViewModel.kt:137` — `_weeklyEmergencyTokensRemaining = MutableStateFlow(1)`; spend checked against the flow at :938; **zero** token code exists in MonolithRepository.kt (grep count 0). Breaks per week = 1 + number of restarts. FIX: TOKEN-SPEC's append-only ledger (already being built). *Nox + Lind, C2.*
- **LR2-S1-9 — A mid-session reboot brands itself a clock-tamper and adds a false 900-second penalty.** (CV) updateSessionProgress re-anchors the monotonic start on reboot (Repository :936-949) but never the wall start; the next tick's drift test (computeVerifiedSessionElapsed, :584-593) compares wall-elapsed-from-origin against mono-elapsed-from-re-anchor, reads drift ≈ the whole pre-reboot elapsed (> 90s), sets tamper, and the penalty lands (worked example in Moss's letter: a 50-minute session 20 minutes in demands ~2,639s more instead of 1,740s). FIX: re-anchor the wall start with the mono start, or exclude the accumulated segment from the drift comparison. *Moss + Day8.*
- **LR2-S1-10 — The Safe Break counts calls, not seconds.** (CV) Repository :972 — `safeBreakSecondsRemaining - 1` per updateSessionProgress *call*. Two callers tick at different rates: Moss measured the 600-count break at ~400s with both tickers alive and ~1,200s Shield-only. The break's length — and the PEEK exemption riding on it (LR2-S2-8) — is however long the callers say it is. FIX: derive break remaining from timestamps like every other duration in the engine. *Moss + Day8.*
- **LR2-S1-11 — A stopwatch session's tamper penalty is booked and enforced nowhere.** (PV) The penalty inflates effectiveTargetSeconds (2,100s in Moss's worked case) but stopwatch completion is governed by the bare 1,200s floor constant; no surface references penalty or tamper for stopwatch at all. The record claims a punishment the app never serves. FIX: enforce the penalised floor, or don't book the penalty. *Moss + Day8.*
- **LR2-S1-12 — FLOW is sticky: the Deck can print FLOW while the repository arms a hard countdown.** (PV) The STOPWATCH_FLOW preset (MonolithViewModel) is never cleared by `setDialMinutes` (:392) or the wheel; the Deck's mode test honours the preset, but Repository.startSession decides stopwatch by dial < 10 only. One tap on the FLOW chip, then dial 50: the screen says "FLOW / OPEN FLOW • 20M FLOOR" and the session that seals is a 50-minute countdown. The user is held by a different session than the one printed. FIX: one mode derivation, shared by screen and repository. *Vera + Una, C1.*
- **LR2-S1-13 — The cued row's "START 25m" button starts nothing — and plays the arming seal while not starting it.** (CV) `cueTaskAndNavigateToFocus` (MonolithViewModel.kt:722-734) promotes the task, sets the dial, navigates to the Deck — and never calls any arm/start function; the identical call backs "CUE" on every other row. Its first act is `playHarmonicSeal(arming = true)` (:723): the seal claims an arm the function structurally cannot perform. FIX: rename the action CUE everywhere, or make START arm. The seal goes either way. *Dens + Spatz, C1.*
- **LR2-S1-14 — The ratchet's REFUSAL speaks in the completion voice.** (CV) `triggerRatchetWarning` (MonolithViewModel.kt:144-145) opens with `playCompletionPulse()` — the reward sound — for every denial (tier weaken, unlock, quota, schedule). Only the text banner tells the truth; the ear is told success. FIX: a refusal thud, or silence. The completion pulse is for completions. *Prima + Zug, CH-1.*
- **LR2-S1-15 — The app-row sled clicks BEFORE the ratchet decides.** (PV) `AppRowSledToggleView` fires its firm click at tap (BlockitSuitcaseAndSwitchesViews.kt ~:475) before `onTierSelected` reaches the ViewModel ratchet; on a sealed-session refusal the user hears click → completion pulse (LR2-S1-14) → no change. The Yuki pattern, alive at the view layer — the ViewModel half of that fix landed (Prima verified the ratchet decides at VM :775-789 before its detent), the view half didn't. FIX: the sled stays silent until the verdict returns, per the landed sled law. *Prima + Zug, CH-2.*
- **LR2-S1-16 — The arm seal fires before the pre-flight gate — and then fires again.** (PV) The slide seals at BlockitCoreViews.kt ~:1184 before the ViewModel's pre-flight check (:879-885) that can divert to a modal instead of arming; on the success path the seal sounds twice (:1184 + VM :894), and `MonolithShieldService.startShield` swallows all failures (:195-207) so the second seal can never learn the shield didn't start. FIX: seal once, from the arm outcome. *Prima + Zug, CH-3.*
- **LR2-S1-17 — The history dedupe key silently deletes genuine repeat sessions.** (CV) `data/MonolithRepository.kt:415` — dedupe on `intention|targetDurationSeconds|completedTasksSummary`: run the same session twice (same intention, same length, same tasks) and the second completion is dropped as a duplicate. A record that invents sessions at seed (LR2-S1-2) also erases real ones for resembling themselves. FIX: dedupe on a session id, never on content. *Orig + Reyer.*

### New S2 charges (14) — register S2 total now 23

- **LR2-S2-10 — Multi-block history erases every block but the last.** (CV) At block rollover the session copy resets `elapsedSeconds = 0` (Repository, break-exit branch ~:982); the final row books only the last block's elapsed. A lived 3 × 50 books as 50 minutes (−67%). FIX: accumulate block elapsed into the row at each rollover. *Moss + Day8.*
- **LR2-S2-11 — The tamper penalty is re-charged per block — and dropped if first detected mid-break.** (PV; connects LR2-S3-3 and LR2-S2-13.) Per-block re-derivation re-applies the 900s (Moss: +1,800s over a 3-block session beyond the stated penalty). FIX: charge once per session, persist the fact. *Moss + Day8.*
- **LR2-S2-12 — The same fact wears three different progress floors.** (PV) Deck clamps its fraction at 0.03/0.05, the desk clock at 0.04 (Airlock :113-117): at 60 seconds left the clock renders 120 seconds' worth of bar. FIX: one floor, or none — a bar may be empty. *Nox + Lind, C4.*
- **LR2-S2-13 — The Safe Break launders a tamper penalty out of the record.** (PV) The break branch (Repository :971-999) discards the penaltyAdd/tamper computed at :930-931, and break exit re-signs a fresh HMAC over the raw target (:975-976): an HMAC-only tamper detected mid-break exits the break clean. FIX: carry penaltyAdd through the break branch; sign the penalised target. *Cato-II + Anselm.*
- **LR2-S2-14 — The session signature attests a duration the session no longer has.** (CV) `CryptoIntegrityManager.kt:57-60, :84-92` — the HMAC payload signs raw `targetDurationSeconds` only; `penaltySecondsAdded` is unsigned, so after the +900s penalty the signature verifies a shorter session than the real one, and the penalty can be edited to zero in the database without tripping verification. FIX: include penaltySecondsAdded in the payload. *Cato-II + Anselm.*
- **LR2-S2-15 — The desk clock shows elapsed XOR remaining, never both.** (PV) Its stated job (kept as a study display: elapsed + remaining) is served by a single `displaySecs` that picks one per mode (Airlock :98-102). FIX: print both, small — or reword the job. *Nox + Lind, C5.*
- **LR2-S2-16 — A just-ended session renders as an idle dial.** (PV) On completion the clock falls to the `else` branch — dial time over an empty bar, no completed state (Airlock :98-120). The finish the user looks up at is rendered as if nothing happened. FIX: a completed rendering for the just-ended session. *Nox + Lind, C6.*
- **LR2-S2-17 — The running % also resets at every block boundary.** (PV; extends LR2-S2-1.) The fraction is per-block (remaining ÷ this block's target), collapsing through every Safe Break and snapping back at each new block — across a 3-block morning it tells the block's story three times, never the session's. FIX: session-span fraction, one derivation (the hero's). *Vera + Una, C3/C4.*
- **LR2-S2-18 — The queue total is computed twice, with two formulas.** (PV) The Tasks screen sums raw estimates; the ViewModel floors the same total through the dial's minimum — one 25-minute task reads 25 on one surface and 50 in the other's arithmetic (Dens's worked example). FIX: one total, one formula. *Dens + Spatz.*
- **LR2-S2-19 — "WILL SHUT n APPS" understates enforcement: PEEK apps are shut too.** (PV; scope-note to Acquittal 8.) The eyebrow counts LOCKED only; both services also intercept PEEK_2M apps outside their lease/break windows. The number is honestly derived and materially incomplete. FIX: count what is actually shut, or word the eyebrow to the LOCKED set it counts. *Vera + Una, C5.*
- **LR2-S2-20 — The slide can commit on ACTION_CANCEL.** (PV) Tap-to-commit is Sarthak's own ruling and is acquitted; but the commit path also fires when the gesture is *cancelled* (Vera + Una, C8: commit at right-edge tap, at 82% travel, or on ACTION_CANCEL). A cancelled gesture must commit nothing. FIX: cancel means cancel. *Vera + Una.*
- **LR2-S2-21 — The session-seal voice is spent on at least eight non-seal actions.** (PV) Undo, splits, loadout/profile apply and save all play the arming seal; opening the disarm challenge plays a release latch for merely *opening* it (VM :923). When everything seals, nothing does. FIX: the seal is for the seal; the rest get their own voices or silence. *Prima + Zug, CH-6/CH-7.*
- **LR2-S2-22 — Disarm banks uncapped elapsed: break minutes and late-END overrun book as study.** (PV) `disarmActiveSession` banks `max(verifiedElapsed, stored)` with no cap at the session's target (Repository :1085-1107; coordinator read confirms the max() and the absence of a cap). FIX: cap banked elapsed at the effective target. *Moss + Day8.*
- **LR2-S2-23 — Row #1 is labelled ACTIVE by position, not by state.** (PV) The Tasks queue's first row wears ACTIVE even when its stored status is QUEUED and no session runs (FocusSequencerScreen, per Dens + Spatz). FIX: label from stored status. *Dens + Spatz.*

### New S3 charges (8) — register S3 total now 17

- **LR2-S3-11 — Colour NAMES lie; the pixels don't.** (CV-discipline: arithmetic attached by the pair, Anselm re-computed.) Color.kt aliases (BauhausBlue, SolarYellow, ElectricViolet…) name warm greys (e.g. R97 G91 B90) and the accent orange (R245 G83 B20); ElectricIris and SoftPeriwinkle are live in Theme.kt's tertiary slots. FIX: rename aliases to what they render, or delete the dead ones. *Cato-II + Anselm — whose full 181-literal table (mail/hale/0807__from-cato-ii__colour-table.md) CONFIRMS Ren's zero-green verdict: G > R and G > B nowhere in the tree.*
- **LR2-S3-12 — Overnight sessions book their minutes to the wrong day.** (PV) Day attribution by start date: Moss's arithmetic puts 420 of the Night routine's 510 minutes on the wrong day of the 7-day chart. FIX: attribute by elapsed span, or split at midnight. *Moss + Day8.*
- **LR2-S3-13 — A blank HMAC verifies as valid.** (CV) `CryptoIntegrityManager.verifySessionState` — `if (session.hmacSignature.isBlank()) return true`. This is the hinge that lets the seed rows of LR2-S1-2 wear the "VERIFIED SESSION RECEIPTS" header with literally empty signatures. FIX: blank verifies as *unverifiable*, and prints as such. *Moss + Day8; Orig + Reyer.*
- **LR2-S3-14 — The Tasks horizon bar floors durations for width but labels them raw.** (PV) A 5m + 45m queue draws 18%/82% against true 10%/90% (Dens's arithmetic). FIX: widths from raw minutes. *Dens + Spatz.*
- **LR2-S3-15 — DEFERRED tasks are counted and shown nowhere.** (PV) The status exists in storage and in no count, total, or list on the Tasks surface. FIX: show them, or fold the status honestly. *Dens + Spatz.*
- **LR2-S3-16 — `fireFeel`'s else-branch IS the completion pulse.** (PV) Any haptic-ms value outside the named branches falls through to `playCompletionPulse` (Prima + Zug, CH-9) — an API whose default answer to "what happened?" is "success". Live minor victims in their table (valve ticks speaking latch; a tower latch no screen wires). FIX: the default feel is silence. *Prima + Zug.*
- **LR2-S3-17 — The grace chip prints "0s" while abort still works.** (PV) Display and capability disagree for the grace window's remainder (Vera + Una, C10). FIX: print the true remaining grace, or drop the countdown. *Vera + Una.*
- **LR2-S3-18 — Sub-step 10 renders "010".** (PV) Zero-padding applied past its width (Vera + Una, C11). FIX: pad to two, never three. *Vera + Una.*

### Acquittals from the pair letters (120, filed in the letters)
Prima + Zug acquitted 53 of 86 sound sites individually (their table is the acquittal evidence); Nox + Lind 9 (including valve banking/drain/spend-at-60.0 arithmetic and the 0-token lockout — the lockout is real, only its durability fails, LR2-S1-8); Vera + Una 11 (hero arithmetic exact against the engine, hub, idle FINISHES, the 20M FLOOR wording); Orig + Reyer 7 (the Models :281-285 derivation itself — no record-tab figure derives from raw target); Cato-II + Anselm 20 (including: no division by raw targetDurationSeconds anywhere in the tree; ShieldService :94-95's duplicated formula is identical to the entity's); Dens + Spatz 9; Rank + Gust 4; Moss + Day8 7 (including the main countdown timer — one honest monotonic source, no double-count from dual callers).

### Not admitted from this round
- **Orig + Reyer's DayFaders "515-minute fallback week"** — no `515` literal exists in the UI tree as cited. The pair may re-file with the line; until then it does not file.
- Rank + Gust's remaining S1s corroborate LR2-S1-1/LR2-S2-3 rather than adding new charges (their letter's framing of CLEAR ALL — "the only surviving copy" — is adopted into LR2-S1-1's truth: the listener already cancelled the originals, so the digest's rows are the sole record the delete destroys).

**REGISTER TOTALS AFTER ADDENDUM 2: 57 charges (17 S1 · 23 S2 · 17 S3) · 17 headline acquittals + 120 pair-filed acquittals · 10 first-round filings not sustained · Quaere + Mutter outstanding, Addendum 3 if their letter brings verified charges.**

---

## §8 — ADDENDUM 3 (08:44 IST) — Quaere + Mutter close the hunt (10 of 10 pairs in)

The final letter (mail/hale/0820__from-quaere__lie-hunt-2.md) is the strongest single filing of the hunt: 22 charges, every one cross-read, and it independently lands on this register's own S1s from the opposite direction (the placebo switch, the zero-list, the per-call break) — two pairs per lie, no coordination. Admitted below: 9 new charges + extensions. **Final register totals: 66 charges (19 S1 · 28 S2 · 19 S3) · 148 acquittals.**

### New charges

- **LR2-S1-18 — "WILL SHUT n APPS" is promised unconditionally — with accessibility dead, it shuts 0 and arms anyway.** (PV) `TactileDeckScreen.kt:226` prints the promise regardless of enforcement state; the pre-flight primer, after merely *describing* the missing permissions (AirlockAndDisarmOverlays.kt:434), offers "COMMIT & ARM SESSION NOW" (:457). Worked: accessibility off → Deck prints WILL SHUT 5 → 0 are shut. A blocker that arms without blocking, under a printed count, is the HOLD standard's worst case. FIX: when accessibility is off, the eyebrow prints "BLOCKING OFF — NO APPS WILL SHUT" and arm requires the permission or an explicit, labelled override. *Quaere + Mutter, B6 (second limb).*
- **LR2-S1-19 — "BATTERY & ADMIN ACCESS": one row, two permissions, each half of the promise unkept.** (PV) The switch state reads device-admin only (SystemGuardScreen.kt:273, via Repository :553-562); the tap opens battery-optimisation settings (MainActivity.kt:453-455); nothing in the tree checks `isIgnoringBatteryOptimizations`, and no intent anywhere opens device-admin settings. Worked: admin ON + battery not exempt → row shows ON while Android remains free to kill the session the subtitle says is protected; battery exempt + admin OFF → row shows OFF and tapping can never grant what it measures. FIX: two rows, each checked and opened honestly. *Quaere + Mutter, B2.*
- **LR2-S2-24 — Task "actual seconds" are counted per call, like the break.** (PV) `incrementActiveTaskSecond()` runs on every non-break progress call (Repository :952; the counter itself :1158-1168): ≈90 credited seconds per 60 wall-seconds with both tickers alive, ≈30 Shield-only. This is the exact field the Drawer will read back as "actual seconds spent" — the permanent record inherits a driver-ratio error. FIX: stamp task seconds from the verified monotonic elapsed. *Quaere + Mutter, A2 (Moss + Day8 concur).*
- **LR2-S2-25 — TOTAL and HELD counts are silently capped by query LIMITs.** (PV) DAO caps: history LIMIT 60, intercept logs LIMIT 80, digest LIMIT 100 (MonolithDao.kt:63-67, :86, :90 — the 100-cap personally seen by the coordinator). The receipts header prints "TOTAL ${sessionHistory.size}": 75 lifetime sessions print TOTAL 60, and the focused sum drops the oldest 15 with it; 120 held notifications print HELD 100 while 20 sit invisible in the table. FIX: COUNT(*) aggregates for anything printed beside TOTAL or HELD. *Quaere + Mutter, A8; Orig + Reyer concur on the 60 cap.*
- **LR2-S2-26 — The 7-day faders draw a fabricated week whenever real stats haven't loaded.** (PV) `BlockitTowerFadersAndCatalogViews.kt:182-190` — with fewer than 7 stat rows the view substitutes a hard-coded week (45, 75, 60, 90, 50, 110, 85 minutes, dated Oct 22–28) under the header "7-DAY FOCUS FADERS"; the ViewModel's initial stats value is emptyList(). Every cold start shows a man who focused 0 a Saturday of 110m. **This also resolves the refused claim from Addendum 2: Orig + Reyer's "515" was this week's SUM (45+75+60+90+50+110+85 = 515), mis-cited as a single literal. The charge was right; the citation form was wrong. It is admitted here in substance, with Quaere's lines.** FIX: draw the real partial list or nothing; delete the hard-coded week. *Quaere + Mutter, A11; Orig + Reyer, in substance.*
- **LR2-S2-27 — "STATUS LIVE" is a snapshot, and "accessibility enabled" is a string, not a service.** (PV) Guard's header promises LIVE (SystemGuardScreen.kt:220-222); the status recomputes only at init, on ON_RESUME, on navigating to Guard, or on the manual refresh button — never while the screen sits open. Worse, the accessibility check (Repository :527-538) tests whether the ENABLED_ACCESSIBILITY_SERVICES settings string *contains the package name*: a crashed or unbound service still reads ON. Same pattern for the notification listener. FIX: label it "STATUS AS OF LAST CHECK", or poll while visible; check the bound service, not the string. *Quaere + Mutter, B1.*
- **LR2-S2-28 — The "(60S HOLD)" chip ends a post-floor stopwatch session on a plain tap.** (PV) The chip is a tap (Deck :520-522); `requestDisarmChallenge` (ViewModel :908-921) disarms immediately — no overlay, no valve, no hold, no token — when the session is stopwatch and the 20-minute floor has passed. The label describes a different session type's exit. FIX: in that state the chip reads "END SESSION". *Quaere + Mutter, B9.*
- **LR2-S3-19 — A fifth permission is computed, counted nowhere, and its settings path is dead.** (PV) `getSystemGuardStatus()` computes hasOverlayPermission (Repository :517-524, :622); SystemGuardScreen receives `onOpenOverlaySettings` and never calls it; no overlay row exists among its switches; no window in the tree uses an overlay type. GUARD 4/4 (acquitted as arithmetic, A-iii) can print while a computed permission stands OFF and undisplayable. FIX: give it a row and count 5, or stop computing it and delete the callback. *Quaere + Mutter, B3.*
- **LR2-S3-20 — USAGE ACCESS "keeps timers honest" — it doesn't touch the timers.** (PV) SystemGuardScreen.kt:243-247. Usage stats find the foreground app (Repository :475-491); session honesty comes from elapsedRealtime + boot count + HMAC (:571-600), which never consult usage access. Revoke it and every timer stays exactly as honest; only foreground detection degrades. FIX: subtitle → "Checks which app is open so blocked apps can be caught." *Quaere + Mutter, B11.*

### Extensions (sharper evidence for charges already filed — no new numbers)
- **LR2-S1-10** gains its caller arithmetic: ViewModel ticker `delay(1000)` (:328) + Shield loop `delay(2000)` (:74) → ≈90 decrements per 60 wall-seconds; the "10-minute" break runs ≈6m40s app-alive, ≈20m Shield-only. (A1.)
- **LR2-S1-4** gains a third formula: the drawer badge computes bricks as `(total/25).coerceAtLeast(1)` with no completed term (MonolithCustomComponents.kt:411) — at the seeded 150/3 the tower prints **16** (Vault), **14** (overlay), **6** (badge) simultaneously. (A4.)
- **LR2-S1-5** gains the three-store arithmetic: one interception event is written to three stores (Repository :1045-1057); on a fresh install the app simultaneously holds **2** (logs), **0** (app rows), **9** (session phantom counts) — and DEFLECTED prints max(2, 0) = 2, a number true of no store. (A7.)
- **LR2-S1-2** gains its crown: the receipts header reads "**VERIFIED** SESSION RECEIPTS" (CurioVaultScreen.kt:210-214) over a list no verification ever runs on, printing "HMAC•" + an empty string for the seeds' blank signatures (coordinator-verified print line). (B10.)
- **LR2-S2-18** gains the third queue formula (horizon denominator floors each task at 10m, CatalogViews :375) and the fact that the ViewModel's own total is passed to two screens and read by neither — a dead second total. (A6.)
- **LR2-S2-19** gains the wrong-set worked example: 5 LOCKED + 3 PEEK prints WILL SHUT 5; 8 are shut. (B6, first limb.)
- **LR2-S3-3** gains its worked tick: target 3,000, tamper first detected → getter says 3,000 while the repository already enforces 3,900. (A10.)
- **LR2-S3-5** gains its second site: the airlock overlay still orders a "**continuous**" hold (AirlockAndDisarmOverlays.kt:608) that the banked valve abolished. (B7.)
- **LR2-S1-8** gains the other direction: no week or reset logic exists anywhere — a long-lived process would never renew the token either. "Weekly" is false both ways. (B8.)

### Dissent, recorded
Quaere + Mutter **acquit** the BREAK eyebrow's ceiling rounding (their A-vi: whole minutes remaining, never understated). Vera + Una's LR2-S3-2 stands on the disagreement with the notification's exact mm:ss — but the dissent is real, and the register records it: two pairs read the same line and split. The charge stays at S3, flagged contested.

### Acquittals +11 (Quaere + Mutter)
Shield's remaining-time derivation is the entity getter applied to the persisted row (A-i); the pre-arm span formula is the engine's (A-ii, already Acquittal 2); both readiness counts are single-predicate (A-iii, extends Acquittal 8); total focused does NOT double-count the active session (A-iv — the suspicion was tested and died); sub-step progress is single-sourced (A-v); the browser switch keeps its word (B-i); **the valve genuinely requires 60.0 held seconds and spends the token only at completion** (B-ii); UNDO restores and no "TAP AGAIN TO UNDO" string exists anywhere (B-iii); the slide's 82% gate breaks no printed word (B-iv); NOTIFICATION HOLD holds within its real scope (B-v).

**FINAL TOTALS — LIE-HUNT 2: 66 charges (19 S1 · 28 S2 · 19 S3) · 148 acquittals · 10 first-round filings not sustained · 1 claim admitted late in substance (the 515 week). All 20 seats reported; all ten letters are in mail/hale/. The hunt is closed.**

— Hale, Critics coordinator.
