# ISSUE #70 — HUMANS / DAY-FIT + FRESH-EYES (Wave-3 Cluster C10, for PRISMA)

**Seat:** Humans / day-fit + fresh-eyes. I judge as the man living the day: up 3:30 AM, bath, an hour of meditation, first fasted study block ~05:00, gym 8 AM, 12 study hours to protect, sleep 8 PM. The phone is a study tool he arms half-asleep in the dark — not a toy he explores at noon. I have not read the studio's internal reasoning; I judge what a first-week user actually meets.

**Ground truth:** `w3c10-base`, `origin/main @ e29acdf`, read-only, plus local branch reads of `agent/issue-9` and `agent/issue-14`.

**⚠ Scope note on the design-revamp:** the brief says `agent/design-revamp` has since merged to main. That branch is **not present in this local clone** and network fetch timed out during this seat, so I could not verify post-revamp surfaces. Every verdict below is grounded on e29acdf (the bundle's own base). The lived conclusions that must be **re-grepped on post-revamp main before building**: the OVERLAY finding (§2 — a launcher-style block screen might finally use it, or might be a plain activity that still doesn't), and the file/line addresses in §5. The human conclusions (two-door warrant, staging, verdict print, thumb geometry) do not depend on the skin.

---

## 1. Zero screen-reader semantics

**Lived verdict: bundle RIGHT on the core, INCOMPLETE on the edges.**

Walk it as the person who is actually hurt. It is not a hypothetical auditor. It is a blind or low-vision NEET aspirant doing the same 12-hour grind — and for him this is not "some screens are hard." Every surface that *does* anything is Canvas: the rail (all navigation), the dial (session length), the slide (start), the sleds (every shield toggle), the emergency valve. Text surfaces (PARK board, lists) are Compose Text and TalkBack reads those fine. So the precise lived shape is: **he can read the app, and he can operate none of it.** 100% of the primary loop — navigate → set time → start → break out in an emergency — is silent. Voice Access is hurt the same way: you cannot say "tap Slide to start" to a control with no label. Switch Access cannot land on an unfocusable view at all.

There is also a cruelty specific to this app, worth one line: Monolith's first demand is "go into Accessibility Settings and turn on a service" — the exact neighbourhood where a TalkBack user lives — and then gives that user's reader literally zero nodes in return.

And a smaller, closer-to-home hurt: Sarthak himself at 3:30 AM, eyes barely open, screen at minimum brightness, is *functionally* low-vision for the first minutes of every day. That is §5's territory, but it is why semantics + big targets are day-fit features, not charity features.

**Where the bundle is incomplete:** its F1 spec covers rail (via #62), rotary, slide, sled, app-row sled, valve — and stops. Two of the Canvas views get no line at all:

- `BlockitTowerFadersAndCatalogViews.kt:24 TowerStreakView` — the streak tower. Information-bearing (it *is* the progress proof) and, so far as I could find, not duplicated as text anywhere. A TalkBack user gets no streak at all.
- `BlockitTowerFadersAndCatalogViews.kt:162 TaskHorizonBarView` — same question, never asked by the bundle.

**Fix-spec amendments (builder numbers):**

1. Adopt the bundle's F1 spec as written (base-class `performClick` + `announce()` helper in `BlockitCoreViews.kt:60`, rotary description + `ACTION_INCREMENT/DECREMENT` ±5 min, sled labels `"<label>, on/off"`, valve announce-only). Add to the rotary: `ViewCompat.setStateDescription(view, "$minutes minutes")` so the value is announced on focus without re-reading the whole label (~2 lines).
2. **Tower + Horizon (+~6 lines total):** each gets either a summary `contentDescription` refreshed when its data changes (e.g. tower: `"Study tower: <n> days this week"` — builder takes the exact figures the view already draws), or, if a view is proven purely decorative/duplicated, `importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO` **with the proof cited in the PR**. Silence by default is the one unacceptable outcome; silence by argued decision is fine.
3. Sleds: in `onInitializeAccessibilityNodeInfo`, set `className = "android.widget.Switch"`, `isCheckable = true`, `isChecked = isChecked` — so TalkBack announces "On, switch, double-tap to toggle," the pattern every Android user already knows, instead of a bare custom label (~4 lines in `SledSwitchView` + `AppRowSledToggleView`).
4. Emergency valve: announce-only is right (no click shortcut — the 60-second hold is the law), but a blind user holding for 60 s in silence cannot tell a registering hold from a dead screen. Add `announceForAccessibility` at two points only: at 30 s held — `"30 seconds. Keep holding."` — and at 10 s remaining — `"10 seconds."` (~6 lines in `Emergency60sValveView`). No other chatter.
5. **Minimum-set acceptance test (the whole F1 bar, one script, on device with TalkBack on):** from a cold Deck — (a) land on and hear all 5 rail stops in order and activate SHIELD and PARK; (b) set the dial to 50 minutes using only Increment actions; (c) toggle one sled and hear its new state; (d) activate Slide and hear either the session start or the honest reason it cannot arm; (e) open PARK and read all 9 board lines; (f) find the emergency valve and hear what it is. If all six pass, F1's minimum is met. Anything beyond this (full dial drag equivalence, tower exploration) is gold-plating — do not build it in this pass.

**DECISION-NEEDED #1 — the slide.** The bundle's spec makes `SlideToStartView.performClick()` trigger "the same completion path a full slide triggers." That trades one user-good against another: the slide is deliberate friction — the one-way ratchet is the product's promise that starting is a decision, not a twitch. A TalkBack double-tap is also a deliberate act (focus, hear the label, double-tap), so the friction argument is weaker than it looks — but it is Sarthak's call, not ours:
- **A. Full equivalent (bundle's spec):** performClick = completed slide, warrant checks unchanged. *Recommended — the alternative locks blind users out of the app's core act entirely.*
- **B. Accessible substitute friction:** performClick opens a "hold to start, 2 seconds" confirm. Keeps a deliberateness beat; costs a second interaction model to build and test (~30 lines).
- **C. Announce-only:** the slide is labelled but cannot be activated without the gesture. This is exclusion dressed as purity — listed so the rejection is on record.

---

## 2. Six permission Settings trips before any value

**Lived verdict: bundle PARTIAL and, in one place, WRONG. This is the finding where the bundle most needed a human.**

First, live the first morning as shipped on main. It is 3:30 AM (or, kinder, the night before). He opens the app. Before Monolith has blocked a single app — before it has proved it can do the one thing it exists for — it wants six separate excursions into Android Settings: six different panes, list-hunting in at least four of them, and the two scariest warnings Android owns (accessibility, device admin) served back-to-back to a man who has received nothing yet. At a realistic 40–90 seconds per trip, that is 5–10 minutes of pure toll before the first second of value. The bundle's Consumer seat said it plainly: most people uninstall before the first block fires. I add the day-fit half: even Sarthak, who is committed, pays this toll *at the exact hour his willpower budget for the day is being spent on waking up at 3:30* — and the seeded FASTED routine wants its first block at 05:00.

Now the fact the bundle's own evidence contains but its fix spec does not act on. Read `WatchStore.deriveWarrant` (`WatchStore.kt:296-304` on the bundle's numbering): the warrant goes LIVE on exactly **two** grants — accessibility bound **and** usage allowed (plus the residency stamp). DEVICE_ADMIN and NOTIFICATION_LISTENER are "counted" for PARK's n/4 print, but they are **not warrant conjuncts** — i14's own absence sentences admit it: without admin, "MONOLITH CAN BE UNINSTALLED"; without the listener, "MESSAGES ARRIVE AS THEY COME." Both are degradations, not blockages. **A session can seal on two trips.** The bundle noticed ("only two gate the warrant" — Purpose-questioning seat) and then spec'd a Reception that still presents four doors on day one. Explanation is not sequencing. A first-run sheet listing four doors reads as "four required," whatever the DONE key's fine print says — that is how a groggy human reads a checklist at 3:30 AM.

### The six, priced by when the day actually needs them

| Grant | Needed before FIRST session? | The lived moment it becomes real |
|---|---|---|
| ACCESSIBILITY | **YES** — warrant conjunct; it is the interceptor | Minute one |
| USAGE ACCESS | **YES** — warrant conjunct; it is the witness | Minute one |
| NOTIFICATION LISTENER | **NO** — only does work *during* a session (holds shut apps' messages, `MonolithNotificationListener.kt`) | The moment the first session starts |
| DEVICE ADMIN | **NO** — its whole job is refusing uninstall (`MonolithDeviceAdminReceiver.kt`) | After the app has proved itself — the first sealed session |
| BATTERY (ignore optimisations) | **NO** — keeps the shield/routines alive across a long day | The first routine, or the first full day — see Decision #2 |
| OVERLAY | **NO — and on this code, never.** See below | Nothing on e29acdf draws with it |

**The OVERLAY problem — the bundle's spec would print a falsehood.** I grepped the whole base: `TYPE_APPLICATION_OVERLAY` / any overlay window — **zero hits**. `Settings.canDrawOverlays` is *checked* (`MonolithRepository.kt:676`) and nothing else in the app ever uses the grant. Interception is done by `startActivity(MainActivity)` from the accessibility service (`MonolithAccessibilityService.kt:422-429`) and the shield service (`MonolithShieldService.kt:181-190`) — no overlay window exists. Yet the bundle's F2 remainder spec instructs Reception to explain OVERLAY with the job line *"draws the block screen over a shut app."* **On this code that sentence is not true.** Printing it violates the app's own TELL-THE-TRUTH law on the exact surface built to earn trust. (Caveat per my scope note: if the merged design-revamp's launcher block screen genuinely draws an overlay, the job line becomes true *there* — re-grep before reusing. If the launcher is an activity, it stays false.)

**One more the bundle never mentions:** `POST_NOTIFICATIONS` sits in the manifest (`AndroidManifest.xml`) and is **never requested anywhere in code** (grep: 0 hits). It is not a Settings trip — it is a one-tap system dialog — and on Android 13+ without it the shield's foreground-service notification simply never shows. Cheapest grant in the whole set, currently unasked. Add it to the sequence below as a dialog, not a trip.

**Fix-spec amendments — staging by trigger, in builder numbers:**

1. **First open (Reception, after the i14 merge): show TWO doors, not four.** Reception iterates a new `WatchItemId.FIRST_RUN = listOf(ACCESSIBILITY, USAGE_ACCESS)` instead of `COUNTED` for the first-run presentation; the total line reads against /2 ("2 OF 2 — YOU CAN START A SESSION NOW"). PARK's board and the n/4 count stay exactly as they are — PARK is the full ledger; Reception day-one is the morning's errand. (~15 lines changed in `ReceptionSheet.kt` post-merge: the list it maps, and the total's denominator.)
2. **NOTIFICATION_LISTENER — trigger: first session start.** When he slides to start and the listener is missing, do **not** block the slide. Print one line on the Deck for that session only: `"MESSAGES FROM SHUT APPS WILL ARRIVE AS THEY COME THIS SESSION — TURN ON THE MAIL ROOM IN PARK TO HOLD THEM."` (Atkinson, 13sp, `onBackground`.) The cost is stated, the session starts, the fix is one FIX trip whenever he chooses. (~10 lines: one conditional line on the Deck's pre-start state.)
3. **DEVICE ADMIN — trigger: first sealed session's receipt.** On the first receipt only, one added line + the existing PARK route: `"MONOLITH EAR
...[truncated 14101 chars]