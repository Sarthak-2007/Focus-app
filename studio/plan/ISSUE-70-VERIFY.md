# ISSUE #70 — VERIFY (Audit R1 · usability-accessibility) — Wave-3 Cluster C10, for PRISMA (lead)

**Base:** `~/workspace/clone-inspect/builds/cells/w3c10-base`, `origin/main @ e29acdf1e4a8166ca4efe328c7728e61f2861176` (read-only; nothing edited/committed).
**Audit read:** `8438f5a` — all lines re-checked against current code.
**Branches checked (fetched, read-only):** `agent/issue-14 @ 9ef5dfd` (Pforte), `agent/issue-9 @ 255ae52` (Vogt), `agent/issue-7 @ e6a11e9` (Shield). `agent/issue-56/62/63` do **not** exist as branches — #56/#62/#63 are R1 slice *issues*, checked via `gh issue view` (read-only).
**Verdict counts: HOLDS 1 · PARTIAL 4 · FALSE 0 · pure FIXED-ELSEWHERE 0 · pure OWNED-BY 0** (ownership/fixed-elsewhere sit *inside* the PARTIALs — see table).

---

## (a) Per-finding verdicts

| # | Sev | Verdict | Current evidence (main @ e29acdf) |
|---|---|---|---|
| 1 | CRITICAL | **PARTIAL — HOLDS (core) / OWNED-BY #62 (rail only) / FIXED-ELSEWHERE fragments only** | Zero UI semantics on main, exactly as audited. `grep -rn contentDescription app/src/main/java` = 2 hits, both *reading other apps' nodes*: `MonolithAccessibilityService.kt:352,409`. `grep semantics` in UI = **0 hits**; no `performClick`, no `onInitializeAccessibilityNodeInfo` anywhere in UI. Canvas views unchanged: `MonolithRailView.kt:98 class MonolithRailView`, `:204 onDraw`, `:307-308 @SuppressLint("ClickableViewAccessibility") override fun onTouchEvent`; `BlockitCoreViews.kt:371 class RotaryWheelView`, `:986-987` same SuppressLint/onTouchEvent, `:1444 SlideToStartView`, `:1826-1827` same; plus `BlockitSuitcaseAndSwitchesViews.kt:32 SledSwitchView, :190 AppRowSledToggleView, :532 Emergency60sValveView` (SuppressLint at :167,431,715) and `BlockitTowerFadersAndCatalogViews.kt:24,162` — **9 Canvas View classes, none accessible**. Fragments elsewhere do **not** cure this: i9 adds Compose `.semantics{contentDescription=…}` only on Park FIX/CHECK keys (`ParkDrawer.kt:644,759` on that branch); i7 adds 3 in `ShieldMatrixScreen.kt:246,1038,1101`. Neither touches any Canvas view. Rail sub-part is owned: #62 body lists *“[HIGH] Primary navigation is completely inaccessible: no semantics, no performClick, no focus — MonolithRailView.kt:282,307-360”*. |
| 2 | CRITICAL | **PARTIAL — HOLDS on main / FIXED-ELSEWHERE in part on `agent/issue-14`** | Six trips unchanged: `WatchStore.kt:91-96 val PERMISSIONS = listOf(ACCESSIBILITY, USAGE_ACCESS, DEVICE_ADMIN, NOTIFICATION_LISTENER, OVERLAY, BATTERY)`; `AndroidManifest.xml:5-13` VIBRATE / FOREGROUND_SERVICE / FOREGROUND_SERVICE_SPECIAL_USE / POST_NOTIFICATIONS / RECEIVE_BOOT_COMPLETED / SYSTEM_ALERT_WINDOW / PACKAGE_USAGE_STATS; `MainActivity.kt:362-410 openFixPane` — six separate `when` branches, six different Settings panes, incl. hardcoded `ComponentName("com.android.settings","com.android.settings.DeviceAdminSettings")` with SECURITY fallback. `WatchStore.deriveWarrant:296-304`: `!a11yBound -> WarrantState.OFF`. **On i14:** ReceptionSheet explains + escorts the **counted four** in plain words and `DONE is live at any door state` (no trap/nag) — value-before-all-grants is mitigated for the four; OVERLAY + BATTERY are **not** in Reception (`ReceptionSheet.kt` iterates `WatchItemId.COUNTED` only, :223,588) and the six separate panes remain — Android gives no bulk special-access grant, so the *trip count itself* cannot be cured in-app. |
| 3 | HIGH | **PARTIAL — FIXED-ELSEWHERE `agent/issue-14` (core cure) / HOLDS residual (PARK + strings jargon)** | On main: `MainActivity.kt:787-791` *“The ShieldPermissionPrimerModal binding stays REMOVED (Slice D)…”*; first run only auto-opens PARK: `MainActivity.kt:~313-317 LaunchedEffect(Unit){ if (viewModel.parkIntroPending()) parkOpen = true }` (repo: `MonolithRepository.kt:454,494`); `ParkDrawer.kt:305-355` prints `GUARD / LAST PROVED · SELF-REPORT` + nine bare board names, grep why/because in ParkDrawer = 0 explanatory lines; `strings.xml:16-25` ritual strings verbatim (`SLIDE TO SEAL VAULT`, `ONE-WAY RATCHET ENGAGED`, `INTENTION AIRLOCK CHAMBER`). **On i14 (unmerged):** new `ReceptionSheet.kt` (1,292 lines) + `SetupStore.kt` (257 lines) — first-open sheet, stamped once, re-openable from PARK; plain jobs: `receptionDoorJob` — ACCESSIBILITY *“THE INTERCEPTOR. Shuts the apps you named, the moment they open. MONOLITH HOLDS NO NETWORK PERMISSION.”*, USAGE *“THE WITNESS. Tells Monolith which app is in front…”*, DEVICE_ADMIN *“THE ANCHOR. While this stands, Android refuses to uninstall Monolith. TO TAKE IT BACK: …”*, NOTIFICATION_LISTENER *“THE MAIL ROOM. Holds the shut apps' messages…”*; escorts `receptionDoorPrepare` name the Android warning *before* the jump. Fonts Atkinson/Anonymous, keys 48dp (:735,1120,1179). Residual: PARK board itself and `strings.xml` ritual vocabulary are unchanged even on i14. |
| 4 | HIGH | **PARTIAL — FIXED-ELSEWHERE `agent/issue-9` (silence + escort + count) / HOLDS (serial walk itself)** | On main, verbatim: `ParkDrawer.kt:175-179 val firstMissing = … PERMISSIONS.firstOrNull { … == PermPrint.MISSING }`; `:195-215` return effect — *“The repaired line alone gets the firm click; the hand goes back in silence — no word prints, ever.”* (only `playFirmClick()` on repair, nothing on failure); `:416-462` single FIX key, `.height(48.dp)`, `text = "FIX — ${item.boardName}"` naming only that one item → up to six sequential round-trips, no error/instruction on a wrong toggle. **On i9 (unmerged):** escort printed above the key (`ParkDrawer.kt:619-629` *“FIX OPENS SETTINGS — FIND MONOLITH IN THE LIST, TURN IT ON, THEN COME BACK HERE”* / per-door variant), staircase label `:698-702 "FIX 1 OF ${missingItems.size} — ${item.boardName}"` (`missingItems` :199-206), **printed verdict** `:712-719 FIXED / STILL OPEN` naming the door (`fixVerdict`, set :326-360), plus a CHECK key re-proving the whole board. Serial one-pane-at-a-time remains — i9's own comment concedes it: *“The walk itself is sequential by construction”* (:199-201) — because each grant lives in a different Android pane. #56 does **not** own this finding: its five lines are sled gate/scroll, FIX-timer binding and scrim — different defects in the same file. |
| 5 | MEDIUM | **HOLDS** (not owned by #63 or #56; untouched on i7/i9/i14) | Sleds: `ParkDrawer.kt:513-514, 582-583, 751-752 .width(76.dp).height(34.dp)` (+ sealed-overlay duplicates :522-523,591-592,760-761) — 34dp < 48dp. Identical `76×34` on **both** i9 (:866-867,935-936,1178-1179) and i14 (:542-543,611-612,847-848). FLOW key: `TactileDeckScreen.kt:714-735` — 11sp text, `.padding(horizontal = 10.dp, vertical = 5.dp)` ⇒ ≈31dp tall, no `heightIn(min=…)`. Labels: `Type.kt:85-92 PillBadgeTextStyle fontSize = 10.sp, lineHeight = 12.sp`. Night contrast: `Color.kt:44 val NightColorOnSurfaceVariant = Color(0xFF776A60) // 13. Night muted tier — 3.5:1` on ground `#171412` (`NightColorOnSurface`/bg tokens, Color.kt night staircase) — self-documented **3.5:1 < WCAG AA 4.5:1**. Duplicated token: `BlockitCoreViews.kt` companion `NIGHT_ON_SURFACE_VARIANT = 0xFF776A60` — a colour fix must change **both**. #63 (theme) is cleared: its lines are half-wired schemes / dead XML palette / hard-coded colours / alias count / typography holes — none cites #776A60, 3.5:1 or PillBadge 10sp. #56 is cleared for size: it cites sled *behaviour*, never sled *geometry*. |

---

## (b) Fix specs

### Prisma cures herself

**F5 — touch + contrast (small, do all three together). Price: ~30 lines, 3 files (+1 duplicate token), no DB, no behaviour change. Risk: low — sled drawing must be re-centred (see note).**
1. `ParkDrawer.kt` — every sled `AndroidView` + its sealed overlay: `.height(34.dp)` → `.height(48.dp)` at :513-514, 522-523, 582-583, 591-592, 751-752, 760-761 (width stays 76.dp — width already >48). In `BlockitSuitcaseAndSwitchesViews.kt SledSwitchView.onDraw`, centre the 34dp track vertically in the new 48dp bounds (draw at `(height-trackH)/2`); if it already centres on `height/2`, no change — verify before claiming done.
2. `TactileDeckScreen.kt:714-735` FLOW key — add `.heightIn(min = 48.dp)` to the Box and change `vertical = 5.dp` → `vertical = 13.dp`; keep 11sp (size is cured by the box, not the glyphs).
3. `Color.kt:44` — exact token change: `NightColorOnSurfaceVariant = Color(0xFF776A60) // 3.5:1` → `Color(0xFF9C8E84)` — the already-shipped `NightTextColorSecondary` value (Color.kt:49), documented **5.8:1** on the same ground, warm grey, no green, no new hue. Update the comment to `5.8:1`. **And** `BlockitCoreViews.kt` companion: `NIGHT_ON_SURFACE_VARIANT = 0xFF776A60.toInt()` → `0xFF9C8E84.toInt()` (else Canvas surfaces keep the failing colour). `Type.kt:85-92` — `PillBadgeTextStyle fontSize 10.sp → 11.sp, lineHeight 12.sp → 13.sp` (letterSpacing 2.0.sp unchanged); at 5.8:1 the 10sp labels would already pass AA contrast — the +1sp is the 3:30 AM legibility margin, check Deck/PARK renders for wrap before merging.

**F1 (non-rail Canvas core) — minimal semantics, one base-class helper + per-view labels. Price: ~80 lines, 3 files. Risk: medium — TalkBack swipe order must be tested on device; do NOT try to make the rotary dial fully TalkBack-operable in this pass, make it *announced + adjustable*.**
1. `BlockitCoreViews.kt:60 BlockitCanvasView` — add `init { isFocusable = true }`, override `performClick(): Boolean { super.performClick(); return true }`, and a protected `announce(label: String, state: String? = null)` helper setting `contentDescription`. Remove nothing else; keep the `@SuppressLint` only where a view genuinely handles its own click dispatch, with performClick now present the suppression is honest.
2. `RotaryWheelView` (:371) — `contentDescription = "Session length, $minutes minutes"` refreshed wherever minutes change; expose `AccessibilityNodeInfo` action `ACTION_INCREMENT/DECREMENT` (±5 min) via `onInitializeAccessibilityNodeInfo` / `ViewCompat` delegate so a TalkBack user can set time without the drag.
3. `SlideToStartView` (:1444) — `contentDescription = "Slide to start session"`, `performClick()` triggers the same completion path a full slide triggers *only if* the host's warrant allows (never bypass the warrant — TELL-THE-TRUTH: if it cannot arm, announce why via the existing slide sentence, do not fake-arm).
4. `BlockitSuitcaseAndSwitchesViews.kt` — `SledSwitchView` (:32) + `AppRowSledToggleView` (:190): host passes a plain label; view sets `contentDescription = "<label>, on/off"`, `isClickable = true`, `performClick()` → existing `onToggleRequested`. `Emergency60sValveView` (:532): label `"Emergency break, hold 60 seconds"` — announce-only, no performClick shortcut (the 60s hold is the law).
5. Compose side (2 lines, already proven on i9/i7 — adopt their pattern, don't reinvent): any remaining icon-only Compose controls get `.semantics { contentDescription = "…" }` in Atkinson plain words.

**F2/F3/F4 residuals — adoption, not new invention. Price: merge cost only.**
- Merge `agent/issue-14` (Reception) and `agent/issue-9` (Park escort/verdict/CHECK) — they touch overlapping ParkDrawer regions; merge i9 first, then i14, resolve ParkDrawer by hand, CI-prove. That closes F3's core, F4's silence, and F2's explanation half.
- F2 remainder (Prisma, ~20 lines in ReceptionSheet after merge): add a second, clearly-optional movement *“LATER, FOR FULL PROTECTION”* with the same job+escort treatment for OVERLAY (*draws the block screen over a shut app*) and BATTERY (*stops Android putting Monolith to sleep mid-day*) — so all six are explained in one place even though only four gate the warrant. Do **not** touch `deriveWarrant` — OFF-unless-a11y+usage is a truth, not a bug.
- F3 remainder: leave `strings.xml` ritual strings (decorative, on already-explained surfaces) and PARK codenames (they are now *reprints* of Reception's explained doors, re-openable via PARK's stub) — strike those sub-points with this reasoning cited, don't churn copy.
- F4 remainder: serial walk is Android-forced; after the i9 merge the walk is counted (`FIX 1 OF 3`), escorted and verdicted — that is the maximum honest cure. Strike “serial” as *platform constraint, mitigated*, keep the box unchecked until i9 is merged + CI-green.

### OWNED-BY — exact cure to cite in the issue body

**#62 (rail-view) owns F1's rail portion. Citable cure:** *“MonolithRailView.kt must stop being an unlabelled blob: (1) `isFocusable = true` + `performClick()` override dispatching the pressed stop through the existing `onTabSelected`/`onParkInvoked` path; (2) per-stop accessibility — either `contentDescription` updated to `'<DEST>, tab, selected/not selected, n of 5'` on every `selectedTabIndex`/`parkOpen` change, or (preferred) an `ExploreByTouchHelper` with 5 virtual views (DECK / SHIELD / TASKS / LEDGER / PARK) each with bounds from RailGeometry, text, selected state and ACTION_CLICK; (3) `onInitializeAccessibilityNodeInfo` sets className/role as a tab list. Acceptance: TalkBack can land on, hear and activate every stop incl. PARK, in order, at night and day. Until #62 lands this, #70 finding 1 stays PARTIAL, not closed.”*
**#56 / #63 own nothing in #70** — cleared above with evidence; do not bounce F4/F5 to them.

---

## (c) Signed seats (20)

- **Crazy-ideas** — Virtual-view rail + a spoken “where am I” on long-press would leapfrog the minimum; spec'd as #62's preferred route, not gold-plating now. — *Crazy-ideas*
- **Maximum-implementation-potential** — Biggest lever is merging i9+i14, not new code: three of five findings shrink in one merge train. — *Maximum-implementation-potential*
- **Brutal critic** — An app that demands an accessibility service while giving TalkBack literally zero nodes is the worst line in this audit, and it still holds word-for-word. — *Brutal critic*
- **Improver** — F5 is a 30-line embarrassment fix (34→48, one hex duplicated in two places); no excuse for it to survive another wave. — *Improver*
- **Designer** — #9C8E84 keeps the muted night hierarchy (it's the existing 5.8:1 tier) — contrast cured without a new colour or a floodlight. — *Designer*
- **Consumer** — At the shop shelf this reads: install, six unexplained Settings trips, jargon board — most people uninstall before the first block ever fires. i14's Reception is the first version I'd hand a stranger. — *Consumer*
- **Student** — Sarthak at 3:30 AM, half-asleep, needs “FIND MONOLITH, TURN IT ON, COME BACK” — i9/i14 finally say exactly that; main says nothing. — *Student*
- **User** — I tried the mental TalkBack walk: rail = silence, dial = silence, slide = silence. Finding 1 is not theoretical. — *User*
- **User-experience** — Silent success/failure on FIX return (main :202-205) is the single most demoralising pattern here; i9's printed FIXED/STILL OPEN is the correct cure. — *User-experience*
- **Purpose-questioning** — Why six grants? Only two gate the warrant; OVERLAY/BATTERY were never explained anywhere — Reception must price all six or the count itself looks like greed. — *Purpose-questioning*
- **Thoughtful** — Serial trips are Android's architecture, not malice — verdicts should punish the silence and the jargon, not the platform constraint. — *Thoughtful*
- **Deep-thinker** — The duplicated #776A60 token in BlockitCoreViews is the trap: a Theme-only fix would “pass” review while Canvas surfaces stayed at 3.5:1. — *Deep-thinker*
- **Enthusiastic designer** — ReceptionSheet is genuinely beautiful thinking — one printed slip, plain jobs, warnings named before the jump. Merge it, don't redesign it. — *Enthusiastic designer*
- **Wow-level designer** — The door metaphors (Interceptor/Witness/Anchor/Mail Room) do in one line what the PARK board fails to do in nine — that's the wow: clarity as craft. — *Wow-level designer*
- **Designer, 29 years** — 48dp, 4.5:1, labelled controls: these were settled standards long before this app. Meeting them is not innovation, it's admission. — *Designer, 29 years*
- **Intentionality-focused designer** — Every remaining jargon string after Reception lands is a *choice* — decorative ritual on explained surfaces is intentional; unexplained codenames on a first-run board were not. — *Intentionality-focused designer*
- **Synchronous collaboration** — Merge order matters: i9 before i14 on ParkDrawer, one hand-resolve, one CI run — parallel merges here would collide and waste the lead's close. — *Synchronous collaboration*
- **Implementation-quality** — All fix specs above name exact files/lines/tokens and a testable acceptance; F1's dial gets announce+increment, not a fake full TalkBack reimplementation. — *Implementation-quality*
- **Technical execution specialist** — Verified by grep, not memory: semantics=0 in UI on main, 2 semantics on i9, 3 on i7, COUNTED-only on i14, 76×34 sleds identical on all three branches. — *Technical execution specialist*
- **Interaction-detail specialist** — FLOW at ≈31dp with 5dp vertical padding is the miss your thumb finds at 3:30 AM; `heightIn(min=48.dp)` + 13dp padding fixes the feel, not just the audit line. — *Interaction-detail specialist*

*No seat was dropped; no seat needed LEAD-COVERED — all 20 contributed a distinct, evidence-tied line.*

---

## REBASE ADDENDUM — STURM-2 closer, 2026-10-30 (~13:00Z)

Branch `agent/issue-70` was 792 commits behind main (merge-base 3ea05bd).
Rebased onto main @ e929c85; conflicts resolved semantically, NOT by
taking either side wholesale:

- **F1 sled/rocker (Suitcase views): ALREADY CURED ON MAIN by issue-60** —
  `SledSwitchView` carries `contentDescription = titleText` +
  `stateDescription` On/Off + a gateCheck-ed `performClick`;
  the app rocker carries `syncA11yDescription()` ("<app>, Shut/Peek/Open"
  + stateDescription) and a `performClick` that cycles through the same
  `commitTo` path a tap uses. The original #70 versions were weaker
  (combined string, ungated, bypassed commitTo) and were NOT carried over.
- **F1 slide: ALREADY CURED ON MAIN by issue #59** —
  `SlideToStartView` (BlockitCoreViews.kt) carries
  `contentDescription = "Slide or tap to start"` and a `performClick`
  under the latch-frame law (OFF → refusal, UNPROVEN → catch only,
  LIVE → mesh; no commit from activation). The original #70
  performClick fired `onSlideCommitted` directly on LIVE — it would
  bypass that law — NOT carried over (duplicate override removed).
- **F1 rotary: PARTIALLY on main (#59 label) + #70 scroll actions kept** —
  main's `refreshAccessibilityLabel()` speaks minutes/blocks/locked and
  refreshes in the same setters; #70's `describeAs("Session length…")`
  calls clobbered it and were dropped. #70's unique cure — SeekBar
  node class + ACTION_SCROLL_FORWARD/BACK ±5 min through the same
  setter/callback, with announcement — IS carried over.
- **F1 emergency valve: #70 cure carried over** (focusable, announce-only
  "Emergency break. Hold for 60 seconds to open.", no activation shortcut).
- **F1 base machinery carried over**: `BlockitCanvasView.performClick` +
  `describeAs` helper. PARK merged-semantics rows carried over.
  PARK sled callbacks: main's #56 gate (`interactive = visible &&
  settled && !isSealed`, ParkDrawer.kt) strictly subsumes #70's
  seal-nulling; HEAD form kept, equivalence noted in code.
- **F3 Deck primer carried over** verbatim (Repository bit + VM +
  MainActivity flip-on-first-session + Deck three lines).
- **F5 carried over**: sled hosts 34→48dp (remaining sites), FLOW column
  13dp vertical padding (HEAD grew a #147 badge line; padding wraps the
  column, ≈54dp target), night muted tier #776A60→#9C8E84 in BOTH token
  homes (Color.kt + BlockitCoreViews companion).
- **F2/F4 unchanged**: owned by #14/#9 respectively, still unmerged;
  platform-forced serial-trip strike stands with evidence in issue body.
- **F1 rail remainder unchanged**: owned by #62.
- **F5 PillBadge 10sp→11sp unchanged**: DECISION-NEEDED, not shipped
  (measured geometry evidence in ISSUE-70-HUMANS.md §7).
