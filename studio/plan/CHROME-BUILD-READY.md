# CHROME — BUILD-READY

**Author:** Sana, Designers coordinator, seated by Ren, Oct 29, 2026
(~12:28 IST). Format precedent: Ilya's SLICE-E-BUILD-READY. Every
charge below was ruled before this document existed — this is
build-ready precision, not re-deciding. Where the record settles a
fork, the settlement is cited; the one scope question the record
does not settle is priced-shaped and sits in §13 for Ren, never for
Sarthak (design questions are the studio's job — his standing order).

**Trunk state this spec is written against:** post-slide tree,
batch 4 shipped 12:26 IST (sha a414972de18bc213), suite 112/112
green, DB v9. All file/line citations are to that tree, read
first-hand this seating.

**STALE BASE (Issue #71):** written against sha a414972 / DB v9 /
suite 112/112. Current main is e29acdf / DB v12
(MonolithDatabase.kt:25). All file:line citations below are
unverified against current main until re-based.

**Law read first-hand for this spec:** MASTER-PLAN §2.5, §6
(2930–3410), §8 (4079–4889); DESIGNERS-PLAN §8 (the floor document);
REGISTER (staircase, specular, accent budget, gate values);
RULINGS.md incl. the chair rulings of 08:10 and 08:58 and Ren's
ESR-19 accent ruling (~11:35); GUARD-RULING-AND-DRAWER.md (my own
ruling, ratified); FINISH-LIST-1 (Aurel, binding input);
EVERY-SCREEN-REVIEW-BUILD1 (Hale — ESR-13, ESR-19, N-1, N-2, N-13);
the shieldpick lane record (Linea, merge-ready); Quaere's pixel
census of Oct 29 (Tasks band row 1687; Deck void 185.5dp; wheel
lamp arc 250.5°–350.0° centred 300.25°).

---

## 0. SUPERSESSION — WHAT DIES IN THIS WAVE

Chrome is the wave where the shipped furniture stops lying about
what it is. Named deaths, each priced in §1 and specified in its
charge section:

1. **The capsule band** (`MonolithBottomNavView`,
   BlockitCoreViews.kt:1913–2095, 183 lines) and its printed words
   FOCUS · SYSTEM · HORIZON · LEDGER. Replaced by the §6.4 rail
   (§2 of this spec). Its one truthful part — the session-armed
   filled-centre dot on the FOCUS tab — survives as the rail's
   armed marker, form unchanged, position moved to the DECK
   segment (§6.4).
2. **The header's three pills** (MonolithCustomComponents.kt:
   217–343): the HELD pill, the `GUARD n/4` pill, the DAY/NIGHT
   pill, and the brand row's click (the console toggle). The band
   becomes print: maker's mark left, Guard line right (§3).
3. **The pull-down console** (`UnifiedStatusGuardDrawer`,
   MonolithCustomComponents.kt:346+, wired MainActivity.kt:
   275–303): the whole surface. Its PERMISSIONS chip is superseded
   by PARK; its SHORTS/LINK toggle chips die (the working sleds
   live on Shield and stay there); its telemetry words
   (`BANKED {m} • DEFLECTED {n}`) die — DEFLECTED is receipt +
   DAY CLOSED vocabulary, never console print; its HELD DIGEST
   chip dies (the digest's entry is the Deck's own HELD chip,
   TactileDeckScreen.kt:615, which survives untouched); its
   STREAK TOWER chip dies and takes its target with it (item 5).
4. **The GUARD destination** — `MonolithDestination.GUARD`
   (MonolithViewModel.kt:44), its MainActivity wiring
   (MainActivity.kt:498–524), and `SystemGuardScreen.kt`
   (288 lines) whole. Disposition of its contents, ruled in §5.6:
   the permission rows → PARK's board (as ruled print, not sleds);
   the SOUND EFFECTS sled → PARK (moved whole); the BLOCKED
   WEBSITES editor → Shield (it never existed there; §8.8's plate
   law "SITES: KEPT WITH APPS ON SHIELD" is built by transplant,
   §5.6); the sniper/browser sleds are ALREADY duplicated on
   Shield and simply lose their Guard copy; the conflated
   `BATTERY & ADMIN ACCESS` row dies — the board's lines 3 and 6
   are its lawful successors, each with its own word and pane.
   The screen also held the only device-admin-adjacent tap in the
   app and it pointed at the battery pane; the board's FIX key is
   the first honest path (§5.4).
5. **`StreakTowerMonumentOverlay`** (AirlockAndDisarmOverlays.kt:
   404) and its MainActivity state — ruled OUT as a surface in the
   open: it celebrates totals the Drawer law killed, and the
   console chip was its only door. `TowerStreakView` itself
   SURVIVES (the running-session overlay at
   AirlockAndDisarmOverlays.kt:446 is a live consumer).
6. **The manual theme** — VM `_darkTheme` (MonolithViewModel.kt:
   104–105), `toggleTheme()` (:549–551), and MainActivity's
   `effectiveNight = darkTheme || sessionActive` composition.
   Replaced by Sten's AUTO evaluation (§5.5), which also feeds
   PARK's theme readout — one evaluation, two readers, so the
   readout can never disagree with the app's own face.
7. **`refreshSystemGuard()` as a feed.** The VM's heavy sweep
   (MonolithRepository.kt:639 — HMAC, clock drift, DAO) survives
   ONLY as PARK's on-open diagnostic. Its Deck/resume callers die;
   chrome's feed is the Watch Store (§4). The VM's all-false
   initial `SystemGuardStatus` — the cold-open 0/4 lie §8.2 kills
   — stops feeding any rendering.
8. **CurioVaultScreen.kt** (274 lines) — already unreachable
   since the Drawer took the VAULT destination. Deleted under
   §11's re-home, with its two exclusive views
   (`DayFadersView`, BlockitTowerFadersAndCatalogViews.kt:157;
   `TileNumberCounterView`, :267). The artifact DATA
   (`KineticArtifact`, MonolithModels.kt:403; `CurioCatalog`,
   :674) is untouched and keeps minting.

**Supersessions inside the paper record** (so the builder does not
build the dead variant): DESIGNERS-PLAN §8.3's operative line
"3 stops — DECK · SHIELD · TASKS, segment 98dp" is the V-DEMOTE
variant; the Drawer verdict was KEEP, and MASTER §6.4's four
stops govern. REGISTER's BAND bullet's "(segment 85.5dp, cap
77.5dp), which the filed plan priced for" is the no-PARK
geometry — struck in the Register this seating (§14); §6.4's
73.5/65.5 governs. MASTER §6.8 check 3's example form
`GUARD 3/4 — ACCESSIBILITY OFF` is struck by chair ruling item 6
(08:58): OFF is an interceptor word and never prints for a
permission; a missing permission prints its bare name (§3.2).
MASTER §2.5's "header right = the live state word" is an earlier
stratum: §6.2 + chair ruling 4 put the Guard line at the right
end and §2.5's sentence BELOW the band; chrome builds §6.2 and
moves no Deck sentence (§6.6's operative half for this wave is a
non-goal: no firing time prints on Shield, Tasks, Ledger or PARK —
there is nothing to build, only a census to pass).

---

## 1. THE PRICE (union, deletes counted once)

Production = new + modified lines in `app/src/main`. Tests = new
lines in `app/src/test`. Bands are the honest spread; the union
carries the build at the band's top until the lane proves lower.

| Charge | Production | Tests | Deletions |
|---|---|---|---|
| C1 The band (rail) | 390–480 | 150–210 | −183 (old nav view) |
| C2 Header + Guard line | ~90 | ~80 | (inside C1/C4 rewrites) |
| C3 Watch Store | 480–560 | 180–240 | — |
| C4 PARK (+ deaths, websites move, theme) | 790–910 | 200–260 | −288 Guard screen, ~−200 console, ~−120 monument |
| C5 The cap rides in | ~40 | ~20 | −136 RombiKnobView |
| C6 ESR-13 + N-1 | ~30 | ~40 | — |
| C7 Accent enforcement | ~25 | 90–120 | — |
| C8 One-lamp unity | 120–150 | 80–110 | — |
| C9 Finish fold-ins | 170–220 | 60–90 | — |
| C10 Curios cabinet | 215–255 | 60–80 | −274 CurioVaultScreen, −110 DayFaders, −76 TileCounter |
| C11 P2-M6 census | 0–10 | (inside C7) | — |
| **UNION** | **≈2,350–2,750** | **≈780–1,040** | **≈−1,390** |

Expected suite at close: 112 → ~150 tests. Compile-at-completion
per Law 11: the lane writes whole, compiles once per charge
batch, merges compile once. The rail's geometry constants live in
ONE object (`RailGeometry`) so the open C5 contest (52dp build vs
64dp construction) — §6.4 rules the build at 52dp — is a
one-object swap if the Register is ever amended; nothing else in
the lane may hardcode a rail dimension.

**Build order inside the lane** (dependencies, §12 expands):
Watch Store (C3) → header (C2) → PARK (C4) → band (C1) → cap
(C5) → smalls (C6, C7 sites, C11 census) → wheel light (C8) →
finish fold-ins (C9) → curios (C10) → census + acceptance sweep
(§15). PARK and the header consume the store; the band's PARK
invocation needs the sheet to exist; every census runs last,
over the finished tree.

---

## 2. C1 — THE BAND: THE RAIL

Law: MASTER §6.4 entire; DESIGNERS-PLAN §8.3 (mechanics);
REGISTER band entry as amended (§14 of this spec).

### 2.1 What prints

Four stops, printed ON the recess in plate style (Anonymous Pro
700, 10sp, +2.0 tracking), centred per segment:

**DECK · SHIELD · TASKS · LEDGER** — then the PARK zone, which
prints NO word, ever.

The word-to-screen mapping is the ruling this charge exists to
state: the destinations do not change. `MonolithDestination`
(MonolithViewModel.kt:44) keeps DECK / SHIELD / TASKS / VAULT;
the band prints **LEDGER** over the VAULT destination because the
screen IS the Drawer and Ledger is its name in every ruling since
the verdict. The shipped words FOCUS · SYSTEM · HORIZON die with
the old view (N-13 closes here). **No icons, no badges, no
counts on any stop** — the old view's blockedCountBadge /
tasksCountBadge die with it. (Hale acquitted those two counts
TRUE; the rail law is stricter than truth — a stop is a word,
not a readout. The counts' facts remain available on their own
screens.) The Ledger stop carries no badge, count, dot or
marker of any kind (§6.4, absolute).

### 2.2 Geometry (390dp reference, `RailGeometry` object)

- Rail: flush recessed rail at the screen foot, spanning margin
  to margin (20dp each side), **52dp tall**, radius **16dp**,
  recess fill (day #D8D1CB / night #100E0C), 1dp letterpress top
  light-line.
- Track inset 4dp → usable 342dp × 44dp. **Segment 73.5dp; cap
  65.5dp** (cap height 44dp, the track's full inner height).
- Four stops consume 294dp; **PARK is a flat 48dp** at the track's
  end (44dp zone + 4dp notch), drawn as recess fill + a 1dp
  end-wall line + a 1dp score line at its inner edge + the 4dp
  notch grip line at ink-35.
- Travel: DECK→LEDGER = 3 segments = **220.5dp**. Cap rest
  positions are the four segment centres minus cap half-width;
  there is no fifth rest position — PARK is a seat against the
  end-wall, not a stop (§5.1).
- Host change (MainActivity.kt:541–562): the AndroidView host's
  height goes 80dp → **52dp**; the capsule's internal float
  padding dies with the view. The host keeps `selectedTabIndex`
  semantics (−1 = none/GUARD-gone; PARK is never an index).

### 2.3 Mechanics (the grammar is REST · DOWN · TRAVELLING · ARRIVED · REFUSED · ARMED)

- **Answer on DOWN ≤16ms**: the pressed stop's word inks
  ink-55 → ink-100 the same frame. Ink/tone only — never scale,
  never ripple (ripple is already purged tree-wide; keep it so).
- Unselected words at rest: day #5F5856 / night #9C8E84
  (text-2). Selected word: glyph tone. (Floor's dissent stands in
  the Register: one observed mis-tap promotes the words to 15sp;
  that is a Register amendment, not a builder's choice.)
- DOWN on an unselected stop: the word inks; the cap departs on
  release-or-drag, 1:1, offset preserved, no magnet, no
  rubber-band. A drag ends seated at the NEAREST stop.
- DOWN on the selected stop: cap's bottom band steps one tone
  down; null press; nothing navigates.
- **KIN-NAV**: cap travel 180ms + 60ms per stop crossed, zero
  overshoot (adjacent 240ms; DECK→LEDGER 360ms). Words flip on
  arrival, never mid-flight. Content starts on the seat frame.
- **KIN-TAB-IN**: outgoing content fades 120ms linear; incoming
  translates 14dp in stop order + fades, 240ms. The shipped
  instant destination swap (MainActivity's `when(destination)`)
  gains this transition at the viewport host — and only there;
  screens do not animate themselves.
- Haptics: 1ms per detent crossed, 4ms on contact/seat; 8ms
  never appears in chrome.
- Touch target = full segment × 52dp. The cap is visual only.
- **Armed marker**: while a session is armed or running, a
  filled-centre marker in ink/glyph tone sits at the DECK
  segment's position — form never orange, steady (no pulse),
  independent of cap position, ABSENT (not dimmed) when no
  session. Source: the same `isSystemArmed` fact the old dot
  read. §6.5's seal pin is NOT built here (§12).

### 2.4 Form of the build

A new Canvas view in the house pattern (the chrome machines are
all Canvas views; the shipped nav view's touch-zone pattern —
full-view zones, `selectedTabIndex`, `onTabSelectedCallback` —
carries over so MainActivity's contract survives). Suggested
home: `ui/views/MonolithRailView.kt`, ~330–420 lines including
the pure geometry object (segment maths, KIN-NAV duration,
PARK-seat predicate — all unit-testable without a canvas).
`MonolithBottomNavView` is deleted in the same commit-range;
nothing may reference it at close (grep gate, §15).

### 2.5 Verification

§6.8 checks 1, 6, 7, 8 (chrome half) — geometry from renders at
the harness's fixed points; travel by frame-logged unit tests on
the pure maths + a DOWN+16ms screenshot (ink changed, cap home);
armed marker present/absent renders, day + night; pixel audit:
zero orange in the band, both modes (inside the C7 census).

---

## 3. C2 — THE HEADER BAND AND THE GUARD LINE

Law: MASTER §6.2; chair rulings 08:10 #4 and 08:58 #2/#4/#6;
DESIGNERS-PLAN §8.1.

### 3.1 The band

ONE band, **48dp**, top of every screen, hairline at y48. It is
the header on Deck, Shield, Tasks, Ledger and inside PARK's
frame — geometry invariant across sealing, PARK opening, and
every Guard state (nothing in chrome moves a single dp, ever).

- **Left**: the maker's mark `MONOLITH` — Anonymous Pro 700,
  10sp, +2.0 tracking, 20dp margin. Furniture: no click, no
  state, no state-derived form. (The shipped brand row's click —
  the console toggle, `header_drawer_toggle` — dies; §0 item 3.)
- **Right**: the Guard line (§3.2), Anonymous Pro 700 **15sp**,
  right-aligned, 20dp margin.
- **Nothing else prints in the band**: no pill, no badge, no
  theme word, no HELD count, no screen name. The shipped
  header's three pills die (§0 item 2). The digest's entry is
  NOT here — it is the Deck body's HELD chip
  (TactileDeckScreen.kt:615), untouched.
- §2.5's firing sentence sits BELOW this band on the Deck,
  never inside it. Chrome builds no sentence and moves none.

Implementation: `MonolithPullDownHeader`
(MonolithCustomComponents.kt:217–343) is rewritten to a pure
print composable (~70 lines): mark left, Guard line right,
hairline under. Its current inputs (heldCount, guardReadiness,
theme state, three callbacks) collapse to ONE input — the Guard
line's derived print (§3.2) — plus the sealed flag (tap gate).

### 3.2 The Guard line — the derivation, named exactly

The line is a READER of the Watch Store (§4), one of its four
lawful readers (§8.6). It runs no check of its own (§6.8-4).

- **Whole**: prints exactly `GUARD 4/4`.
- **Lack**: prints `GUARD {n}/4 — {NAME}`, n = the count of
  counted-four items whose store value is currently-warranted
  GRANTED (value GRANTED and not STALE at render), NAME = the
  first non-whole counted item **in board order** (ACCESSIBILITY,
  USAGE ACCESS, DEVICE ADMIN, NOTIFICATION LISTENER).
  - A MISSING item prints its **bare name** — `GUARD 3/4 —
    ACCESSIBILITY`. No state word: the name in this slot IS the
    lack (chair ruling 08:58 #6; §6.8-3's OFF example is struck,
    §0).
  - A STALE item prints name + STALE: `GUARD 3/4 —
    ACCESSIBILITY STALE`. STALE rides the name wherever lack is
    printed.
- **Never-measured**: before the store's first counted-four
  measurement lands, the line prints NOTHING (absence, §8.2 —
  blank-until-proven). The counted four are all measured by the
  entry sweep (§4.3) within the §8.9 return budget (≤300ms); the
  blank window is frames, and the all-false `0/4` cold print of
  the shipped app can never recur because there is no default
  value to print.
- **Quiet by law**: no colour alarm, no motion, no sound, no
  badge, no fill, no pip in any state (§8.5's pip form in
  DESIGNERS-PLAN §8.5 is superseded by §6.2's "no pip" — the
  line is text only). Removing colour from a render loses no
  Guard information (§6.8-3).
- **Tap** (unsealed): sends the cap to PARK under KIN-NAV and
  invokes the drawer on the seat frame (§5.1) — it is a second
  invocation path to the same seat, not a screen. While sealed
  the line is inert print.
- **Sealed**: the line FREEZES at its seal-frame value for the
  session's whole life (§8.1): the store keeps measuring, the
  record never freezes, the chrome rendering does. This is a
  rendering freeze — a snapshot of the derivation taken at the
  seal frame, held by the VM — and it is chrome's half of
  §6.8-9; the pin and latches are Slice E's (§12).

Derivation home: a pure function in the VM over the store's
flow — `guardLinePrint(storeState, sealedSnapshot)` returning a
sealed print type (`Whole`, `Lack(name, stale)`, `Unmeasured`).
Unit-tested without Android (§15): whole/lack/stale/blank/frozen
cases, board-order precedence when two items lack at once.

### 3.3 What the header stops being fed

MainActivity.kt:235–241's local count (derived in the Activity
from `systemGuardStatus` booleans) is deleted; the Activity
collects the VM's Guard-line print. `guardReadinessCount`
(MonolithRepository.kt:57) remains only as the PARK diagnostic's
arithmetic (§5.6), never a chrome feed.

### 3.4 Verification

§6.8 checks 1–4; §8.11 checks 5, 7, 8 (count audit). Renders:
whole, lack-missing, lack-stale, cold (blank) — day + night.
Grep: no permission/interceptor check call site under
MainActivity or the header file.

---

## 4. C3 — THE WATCH STORE

Law: MASTER §8.2, §8.3, §8.4, §8.6, §8.7. This is the wave's
load-bearing build: the header, PARK, and the Deck strip all
read it; nothing else in the app may run a standing check.

### 4.1 The item set (fixed; board order never changes)

| # | Item | Family | Evidence class | Counted |
|---|---|---|---|---|
| 1 | ACCESSIBILITY | permission (BIND) | BIND | ✓ |
| 2 | USAGE ACCESS | permission | GRANT CHECK | ✓ |
| 3 | DEVICE ADMIN | permission | GRANT CHECK | ✓ |
| 4 | NOTIFICATION LISTENER | permission (BIND) | BIND | ✓ |
| 5 | OVERLAY | permission | GRANT CHECK | — |
| 6 | BATTERY | permission (the exemption grant itself) | GRANT CHECK | — |
| 7 | SHORTS + REELS | interceptor | derived | — |
| 8 | SOCIAL SITES | interceptor | derived | — |
| 9 | NOTIFICATIONS | interceptor | derived | — |
| — | SHIELD ENGINE | (behind the strip, never a board line) | HEARTBEAT | — |

Per item the store keeps exactly three things (§8.2): the value
(family vocabulary — permissions: GRANTED / MISSING / STALE is
NOT a value, see §4.4; interceptors: RUNNING / PAUSED / OFF — no
word ever crosses families), when it was measured (monotonic
clock for age, wall clock for print), and its evidence class.

**The BATTERY read does not exist in the shipped tree** —
`SystemGuardStatus` (MonolithRepository.kt:43–62) has no
exemption field. The store's GRANT CHECK set adds
`PowerManager.isIgnoringBatteryOptimizations(packageName)`.

### 4.2 Writers (the Heartbeat Tap; no new timer, no new permission)

One process-wide StateFlow (Otis's ReadinessBus form), fed by:

- **Service lifecycle hooks**: `MonolithAccessibilityService`
  onServiceConnected / onUnbind / onDestroy write item 1;
  `MonolithNotificationListener` (:23) onListenerConnected /
  onListenerDisconnected write item 4;
  `MonolithDeviceAdminReceiver` onEnabled / onDisabled write
  item 3. (The Live Wire's existing stamps in the accessibility
  service are untouched — wire and store are separate
  instruments, §8.7; the store's hook is an additional write at
  the same lifecycle points.)
- **The Shield service's existing 2s monitor loop**
  (MonolithShieldService.kt:72–165) publishes ONE snapshot per
  iteration: the engine HEARTBEAT item + re-reads of the two
  BIND items' present-tense state.
- **GRANT CHECK reads**: Deck entry, Deck resume, and the
  FIX-key press dispatch the six grant queries in parallel at
  t=0 (reusing the Repository's existing read bodies:
  hasUsageStatsPermission :519, isAccessibilityEnabled :561,
  isNotificationListenerEnabled :573, isDeviceAdminActive :585,
  hasOverlayPermission :553, plus the new battery read). Each
  item re-stamps only when its own query lands — a fixed stagger
  is banned by name (§8.9).
- **Interceptor derivation**: items 7–9 are computed from their
  setting + parents at every store emission: RUNNING = setting
  on ∧ parents GRANTED-warranted ∧ no suspension; PAUSED =
  setting on ∧ warranted ∧ his chosen suspension; OFF = setting
  off OR a parent MISSING/STALE. No stored interceptor state.

### 4.3 What dies, what the seam is

`refreshSystemGuard()` stops being a feed (§0 item 7); Rosa's
20s poll and trigger table are already dead paper — no code.
The Readiness Log (append-only table) rides **Migration 9→10**,
which is Slice E's plumbing batch (F-N1) — §8.3 settles this, it
is NOT a chrome fork: chrome builds the store in memory (bus +
tap + grant hooks + render-derived STALE) behind a writer
interface whose log implementation is a **no-op in this wave**;
Slice E attaches the table without touching a call site.
Named consequence: §8.11-3's cross-process-death STALE
derivation lands with the log in Slice E; chrome's cold open is
blank-until-measured, which is the truthful half and is built
here.

### 4.4 STALE — derived at render, set by nobody

No field stores STALE. At any render, an item whose latest PASS
measurement is older than **45s** (monotonic age) renders STALE
— one threshold for every surface (§8.4; Vau's 15,000ms is
superseded paper). For BIND items the derivation differs in
kind: their measured-instant refreshes at every re-validation
read (Deck entry / resume / any flow emission), so a bind item
renders STALE exactly when it *was bound and is not confirmed
bound now*. Recovery is silent everywhere. Age prints (where
ages print at all — the seam stamp, PARK) use §8.4's grammar:
ceiling-rounded to the next 5s; `JUST NOW` under 5s; `OVER 1M`
past 55s.

### 4.5 The strip re-source (chrome integration item)

Batch 4 shipped the Deck strip reading `deckWarrant` /
`liveWarrant()`. §8.6 makes the strip the store's third reader:
the VM's STANDING `deckWarrant` derivation is re-based on the
store (a `standingWarrant()` derivation: a11yBound ← item 1's
BIND state, stamped ← engine HEARTBEAT freshness, usage ←
item 2's GRANT CHECK) while the slide's in-gesture synchronous
`liveWarrant()` reads stay exactly as merged (§8.7: the wire
governs the seal). **Prints must not change** — the seam table
is the regression: `StripDerivationsTest` passes unmodified,
and the derivation swap is covered by equivalence unit tests
(same inputs → same seam marks, both derivations, in the lane
before the old one is deleted).

### 4.6 Form and price

New file `service/WatchStore.kt` (~300–360 lines: item model,
bus, tap writers' registration, derivation, STALE query,
standingWarrant). VM rewire + service hooks + battery read
~180–200. Tests 180–240: derivation tables, STALE decay (fake
clock), interceptor effect table, strip equivalence, Guard-line
precedence (shared with C2's tests — counted once, here).

### 4.7 Verification

§8.11 checks 1 (one-store grep), 2 (decay ≤45s), 4 (age-print
audit), 5/6 (seal audits, chrome halves), 7 (count audit);
§6.8 check 4. The one-store grep is a merge gate: at close, no
permission or interceptor read exists outside WatchStore.kt,
the Repository grant bodies it calls, and PARK's on-open
diagnostic.

---

## 5. C4 — PARK

Law: MASTER §6.7 (invocation), §8.5 (board), §8.8 (the drawer),
§8.9 (FIX + PROVING), §8.10 (sound/theme/plate), §8.1 (sealed);
GUARD-RULING-AND-DRAWER.md; chair rulings 08:58 #2/#3/#12;
F-B1 (viewport host; receipt dismissal).

### 5.1 Invocation and dismissal (§6.7 — the contract)

- PARK is hosted in the **viewport host, never root** (F-B1):
  it rises over the current screen's content, under the header
  band and above the rail's frame — chrome geometry never
  moves. The sheet's host is MainActivity's viewport Box.
- **Arrival IS the invocation.** The cap dragged past LEDGER
  into the PARK zone (resistance over the last 12dp; the notch
  inks ink-35 → ink-70 at the zone's inner edge; the detent
  tick pitched BELOW the stop family — Mikro's flag: the exact
  pitch is Ton's open item, the lane takes the engine's lowest
  existing detent voice and names it in the lane log), seats
  against the end-wall (soft detent, 4ms) → the drawer rises
  the same frame. A tap on the PARK zone does the same (PARK
  counts as one step under KIN-NAV; invocation waits for the
  seat). The Guard line's tap sends the cap to PARK under the
  same travel law (§3.2).
- **KIN-DRAWER**: open 280ms SEAT, close 160ms THROW. Exactly
  **two rest heights** — closed and open. Rows are operable
  only from the open seat frame; a row tapped mid-rise receives
  no DOWN answer.
- Grab strip: 32dp visual / 48dp touch; grip line 24dp → 28dp
  on DOWN; drag 1:1; release above 40% of open height seats
  open, below seats closed.
- **Dismissal is one gesture with leaving — exactly three
  inputs**: (a) a stop press or cap drag out — the sheet
  dismisses AND the cap departs on the SAME input; if the
  session receipt sheet is up, this same input dismisses it
  too (F-B1: PARK invocation dismisses the receipt sheet on
  the same input — the receipt never traps him under PARK);
  (b) drag the sheet down past 40% → closed, cap returns to
  its origin stop; (c) scrim/content tap or PARK re-tap →
  160ms exit, cap travels home under KIN-NAV.
- **Sealed**: PARK cannot be invoked (rail locked, §6.1); an
  already-open Drawer degrades read-only at the seal (chair
  ruling 08:58 #3); nothing new opens while sealed.
- **First run**: with the store never measured, PARK opens
  directly at first composition. Leavable mid-walk in one
  gesture; no modal, no summary, no completion screen (§8.9).

### 5.2 The sheet (§8.8 — contents, in ruling order, closed list)

Panel radius 16dp. **Interior is the GROUND** (day #E4DFDA /
night #171412) — raised tones do not appear inside PARK.
Contents, in this order and no other:

1. **Header**: `GUARD` (Sora 700, 22sp) + the telltale line:
   `LAST PROVED <instant>` — Anonymous Pro 700 15sp +0.5,
   label in plate style (10sp +2.0); the instant is the wall
   time of the latest COMPLETE measurement set, followed
   permanently by `· SELF-REPORT`. The slot is BLANK before
   the first complete set; it advances only on complete sets;
   the instant lives HERE only — not in chrome, not on the
   strip seam, not on the plate. No AGO, no LAST SYNCED,
   anywhere, ever.
2. **The board** (§5.3).
3. **The FIX key** (§5.4), directly under the board.
4. **Sound** (§5.5).
5. **Theme** (§5.5).
6. **The spec plate** (§5.5), at the foot, as furniture.

Material law: exactly one tone (ground); everything is print
except the FIX key — the board's only fill, and the only radii
in PARK are the panel's 16dp and the key's 8dp. **Orange: zero.**
Locked tokens only. A re-strike is an instant re-print (no
animation on any board line, ever).

### 5.3 The board (§8.5 — nine lines, fixed order, ruled print)

Pitch **48dp** per line; 1dp hairlines at ink 18% between;
name in plate style left (Anonymous Pro 700, 10sp, +2.0);
state word right in **Atkinson 700, 15sp, +0.5** (truth never
whispered). No fill, no radius, no press state, no ripple, no
chevron, no per-line action, no description text. **No toggles
on the board. No count reprint** (`n/4` lives only in chrome).

1. ACCESSIBILITY — GRANTED / MISSING / STALE
2. USAGE ACCESS — GRANTED / MISSING / STALE
3. DEVICE ADMIN — GRANTED / MISSING / STALE
4. NOTIFICATION LISTENER — GRANTED / MISSING / STALE
5. OVERLAY — GRANTED / MISSING / STALE (uncounted)
6. BATTERY — GRANTED / MISSING / STALE (uncounted; the
   exemption grant itself)
— 16dp doubled gap, no eyebrow —
7. SHORTS + REELS — RUNNING / PAUSED / OFF
8. SOCIAL SITES — RUNNING / PAUSED / OFF
9. NOTIFICATIONS — RUNNING / PAUSED / OFF

Word tones: GRANTED / RUNNING / ON in secondary ink (day
#5F5856 / night #9C8E84); MISSING / STALE / PAUSED / OFF in
primary ink (day #252323 / night glyph #EDE3D8). Same size,
same weight — the tone is the only emphasis. `BLOCKED` appears
nowhere. A never-measured line prints its NAME ONLY (absence,
§8.2) — no state word, no spinner.

### 5.4 The FIX key and the return (§8.9 — PROVING)

- The key exists **only** when a permission line (counted or
  uncounted) reads MISSING — exactly one key. STALE earns no
  key; an interceptor's OFF earns none.
- Form: full content width, **48dp tall, radius 8dp, ink fill**
  (day #252323 with word #FAF6F0; night fill cap #4A423C with
  glyph #EDE3D8). **Never orange.** Text:
  `FIX — <first MISSING in board order>`, Sora 700, 15sp, +0.5.
  Built FLAT (the §8.10 specular exception then has zero
  application in PARK).
- Behaviour: answers on DOWN; dips 1dp (PRESS ≤90ms) and STAYS
  dipped for the whole absence; opens the EXACT settings pane
  for that grant — accessibility / usage-access / device-admin
  / notification-listener / app-overlay / battery-optimisation
  — never Settings home. (The intents partly exist in the dying
  Guard wiring, MainActivity.kt:498–524 — minus device-admin,
  which exists nowhere in the shipped app and is new here.)
  Voice: Ton's Nav Key at its quietest (−16dB; night ×0.6
  sound, ×0.85 haptic). While sealed, the key is print and the
  repository gate holds: `fixEnabled = !isSessionArmed ∧
  missingExists` (§8.1).
- **The return is PROVING**: at t=0 of return all permission
  queries dispatch in parallel; the sweep lands ≤300ms; each
  line re-strikes only when its own query lands (hard cut);
  the repaired line alone gets `playFirmClick` (−8dB; haptic
  in the same 100ms); if nothing was fixed — no sound, the key
  springs back (PRESS-BACK ≤150ms). Stability beat 500ms after
  the last proof. **Hand-back prints no word**: no banner, no
  FIXED / RESTORED / ALL GOOD anywhere (grep gate). The FIX
  key absents itself — 48dp + gap collapse over 160ms THROW,
  sections below rise (the one filed exception to Δy=0; board
  lines never move). Total hand-back ≤890ms. A second MISSING
  inherits the key on the first's proof frame.
- PARK's on-open diagnostic: opening PARK also fires the
  surviving `refreshSystemGuard()` sweep (§0 item 7) — its
  integrity extras (HMAC, clock drift) render nowhere unless
  the lane's diagnostic line is invoked; they are the
  instrument's, not a surface's.

### 5.5 Sound, theme, plate (§8.10)

- **Sound**: the shipped `SOUND EFFECTS` sled
  (SystemGuardScreen.kt:185–201) moves WHOLE — `SledSwitchView`
  unchanged as a control — to a 52dp-pitch row: name plate-style
  left, sled right, margin word ON/OFF (Atkinson 700 15sp)
  flipping on arrival only. It gates
  `TactileSoundEngine.soundEnabled` (engine-level, everywhere)
  exactly as today.
- **Theme**: one ruled line, 48dp pitch, board grammar. Right
  readout is exactly one of: `AUTO · NIGHT — SESSION ACTIVE` ·
  `AUTO · NIGHT — <ROUTINE NAME> <START>` (the routine as
  printed on its Shield row, e.g. `AUTO · NIGHT — SLEEP 20:00`)
  · `AUTO · DAY — NO ROUTINE OWNS IT`. Precedence (Sten):
  (1) session active → NIGHT/session; (2) an enabled, unpaused
  SLEEP window active → NIGHT/that routine; (3) else DAY/no
  owner. **The readout is computed from the same evaluation
  the theme itself uses** — chrome therefore also replaces the
  theme derivation (§0 item 6): VM exposes one
  `themeEvaluation` (session-active ∨ SLEEP-window-active, via
  the existing routines state + RoutineDerive) driving
  `darkTheme` and the readout from a single when; transition
  is a 0ms cut at the boundary; the readout re-strikes the
  same frame. No manual pill is resurrected anywhere.
- **Spec plate**: Teen's text (Anonymous Pro 400, 10sp, +1.2,
  line height 16, uppercase, on bare ground; lines 1–2 primary
  ink — day #252323 / night #D2C6BB — the rest secondary),
  with the LEDGER clause CUT IN PRODUCT (chair ruling 08:58
  #12 — kept in paper, struck in the app). The plate prints:
  MONOLITH ML-01 / UNIT Nº 01 · BUILT FOR ONE USER · NOT FOR
  SALE / WHEEL SETS TIME · SLIDE ARMS · THE HOLD BREAKS /
  HOLD: 60 SECONDS, BANKED EVERY 15 · ONE BREAK A WEEK /
  PEEK: 2 MINUTES, THEN SHUT AGAIN / COLOUR IS A VERB:
  ORANGE = COMMIT · INK = SHUT · GREY = PEEK · PORCELAIN =
  OPEN / ROUTINES: SET BY HAND ON SHIELD · FIRED AS SET /
  SITES: KEPT WITH APPS ON SHIELD · DATA: ON THIS PHONE ONLY /
  STUDY RECORD: IN YPT / SOUND: FLOAT-PCM, 4 PARTIALS ·
  POWER: YOUR PHONE. Byte-identical text is an acceptance
  check (§8.11-16) — the lane copies from §8.10 as amended by
  the chair, not from this summary, if the two ever differ.
  **STUDY RECORD: IN YPT is the plate's last content line** —
  the Drawer is not mentioned (§7.1's law: the plate never
  advertises it).

### 5.6 Disposition of the dying GUARD destination (ruled)

PARK **absorbs** the board, the FIX paths, sound and theme.
It does not host the old screen: `SystemGuardScreen` is
deleted whole, and with it the sled-shaped permission rows,
the REFRESH PERMISSIONS chip, and the conflated battery/admin
row. The **BLOCKED WEBSITES editor moves to Shield** — a new
section transplanted from SystemGuardScreen.kt:124–202
(~78 lines, mostly moved code) placed after Shield's app list,
wired to the VM's existing `blockedSites` flow (:130) and
`addBlockedSite` / `removeBlockedSite` (:650/:656) — MainActivity
re-points the callbacks it already passes. Its caret is ink at
the new home (§8). The sniper/browser sleds keep exactly one
home: Shield's, as today. `MonolithDestination.GUARD` and its
`selectedTabIndex` −1 mapping are deleted; at close, a grep for
the GUARD destination returns only the PARK header word and
the Guard LINE print.

### 5.7 Form, price, verification

New file `ui/components/ParkDrawer.kt` (~520–620 lines: sheet,
board, key, rows, plate) + MainActivity/VM wiring ~180 + theme
evaluation ~90 + websites transplant ~85 gross (~20 net new).
Tests 200–260: board derivation (9 lines, word families,
blank-until-proven), FIX presence + pane mapping (all six),
PROVING frame order (per-line re-strike, no stagger), theme
readout's three states, sealed gates, plate bytes, first-run.
Verification: §6.8 checks 9 (chrome half), 11, 12; §8.11
checks 3 (chrome half), 9–23 mapped in §15.

---

## 6. C5 — THE CAP RIDES IN (shieldpick integration)

Proven in lane (Linea): BUILD SUCCESSFUL, harness 2/2, renders
filed (SLEEP reached by real tap). The merge set is TWO files:

1. NEW `ui/components/ShieldCapSelector.kt` (188 lines):
   recessed track (12dp radius, 3dp pad), ink cap travelling on
   `specA(260)`, selected word over a #FBF1EC marker line,
   ticks under the track, tap tags `shield_cap_stop_0/1/2`,
   `playFirmClick` as its voice, zero orange. Words:
   STUDY / WORK / SLEEP.
2. `ShieldMatrixScreen.kt` section-1 swap ONLY (~+15/−25): the
   `RombiKnobView` AndroidView (:130–166) out, `ShieldCapSelector`
   in, same `onApplySmartLoadout` mapping
   (STUDY / WORK_SPRINT / SLEEP, :154–156), same **84dp
   production slot**.

Merge discipline (from the lane record): the lane diff ALSO
drops `showResetNote` and the routines reseed strip — that is
lane staleness (the lane's base predates the routines reseed
note), NOT part of the cap set. **Chrome keeps showResetNote**;
only the section-1 hunk merges. `ShieldSelectorVariants.kt`
and the knob build stay in the lane as the record, never
merged.

With its only consumer gone, **`RombiKnobView`
(BlockitSuitcaseAndSwitchesViews.kt:172–307) is deleted from
trunk** in this wave.

Per her approved NEXT, **Linea adapts the cap herself at build
time** when chrome touches Shield, and runs the everybody-review
on these renders. Chrome's builder does not fork her file.

Verification: the lane proof re-run on the merged tree (harness
2/2 + the cap renders re-shot in trunk context); F-05 audit —
the cap already speaks the selection language (ink fill, glyph
word, marker line); census: zero orange on Shield, both modes.

---

## 7. C6 — ESR-13 CLOSES HERE, AND N-1 (the paper and the card)

Law: ESR-13 + Hale's fix (build-1 review, ratified in Ren's
dispositions); FINISH-LIST P2-W1; Quaere's census (the close).

### 7.1 The leaf (Tasks)

Root cause, stated once so it cannot recur: in this theme
`scheme.surface` IS the ground token (Color.kt:11 day #E4DFDA =
background; :26 night #171412 = background). Any "paper" filled
with `surface` is ground wearing a name.

- `TaskLeafItem` (FocusSequencerScreen.kt:397), background at
  :465 — today `.background(scheme.surface)` under a comment
  (:460–464) that claims the paper exists. The fix is a branch
  on the leaf's existing `isCued` flag (the head leaf is index
  0, call site :363):
  `.background(if (isCued) scheme.surfaceBright else scheme.surface)`
  — the head (active) leaf takes the raised step (day #FAF6F0,
  1.23:1 / night #362C27, 1.35:1 — tokens already in theme,
  Color.kt:15/:36); queued leaves stay surface (the
  resting-slip law: flush is their truth). The false comment is
  rewritten to state exactly this; a comment claiming unbuilt
  paper is itself a defect class (ESR-13's lesson) and the lane
  log names the rewrite.
- The head leaf's perforation dashes print in the GROUND
  colour at 100%: `PerforationRow` (:635) gains a `raised`
  variant — dash colour `scheme.background` when raised,
  `scheme.outlineVariant` otherwise (the queued leaves' alpha
  re-voice is §10.3).
- The token is NOT redefined. Quaere's warning stands as a
  build rule: redefining `scheme.surface` would force a
  re-audit of every consumer in the tree; chrome fixes call
  sites, never the token.

### 7.2 The Deck card (N-1) and the hub disc

Same root, second site: the Deck hero card
(TactileDeckScreen.kt:337) fills `.background(scheme.surface)`
— ground on ground, 75% of the frame (Quaere). Fix:
`→ scheme.surfaceBright`, both modes (P2-S1). The wheel's hub
disc (BlockitCoreViews.kt:705, `tokSurfaceBright()`) then sits
one step from the raised card at night; per the pre-approved
Finish ruling it steps to the night cap: hub fill becomes
`if (isNightMode) tokSurfaceSheet() else tokSurfaceBright()`
(night #4A423C; day unchanged). **The hero time readout is
PROTECTED — untouchable** (charge 8): nothing in this section
or §9 touches its type, size, position or content.

### 7.3 The close

Quaere's pixel census, not a comment: leaf interior column
samples (her y560–1680 protocol) read #FAF6F0 / #362C27 on the
head leaf and ground on queued leaves; Deck card interior
samples read surfaceBright both modes. ESR-13 and N-1 close on
her numbers or not at all.

Price: ~30 production (branch, variant, two call sites, comment
truth), ~40 tests (render assertions at her sample points).

---

## 8. C7 — ACCENT LAW ENFORCEMENT (ESR-19)

Ren's ruling, binding, restated as the build rule: **orange
appears (i) as the FILL of the one commit control per screen,
and (ii) as TEXT only where the text is itself the commit verb.
Everything else is ink.** Budgets (REGISTER): Deck 1 · Tasks 1 ·
Shield 0 · running 0 · AOD 0 · overlays 0 · chrome 0.

### 8.1 Named sites (found by grep this seating; the census is the proof, this list is the map)

1. **Carets** — every `cursorBrush` in the tree → ink
   (`scheme.onBackground`): Tasks composer
   (FocusSequencerScreen.kt:195, `SolidColor(primaryContainer)`
   today), Shield sites field (ShieldMatrixScreen.kt:245, same
   leak, transplanted home §5.6), Guard's (:183 — dies with the
   screen; no action). The Drawer's search field (:328) already
   uses onSurfaceVariant — compliant, untouched.
2. **UNDO chip** (FocusSequencerScreen.kt:248): today
   `BlockitActionChip(isAccentArmed = true)` = full accent fill
   (MonolithCustomComponents.kt:179). Ruled form: **ink fill,
   accent text**. The chip gains a mode (~6 lines:
   bg = inkControlFill(), fg = primaryContainer); UNDO is its
   only caller this wave. The REMOVED banner behind it (:231)
   fills `scheme.primary` — a full-width CREAM slab at night;
   it re-voices to `inkControlFill()` in the same hunk.
3. **Deck running progress fill** (TactileDeckScreen.kt:485):
   `.background(scheme.primaryContainer)` — an accent FILL on
   the running Deck. Budget for running = 0; the slide cap
   (resting) is the Deck's one. The fill goes ink
   (`scheme.primary`). This is the census's expected catch if
   missed; named here so it is not.
4. **Header brand dot** (`accentBrandDot`, MonolithCustomComponents
   — primaryContainer when armed): dies with the header rewrite
   (§3); the new band's armed marker is the rail's ink marker
   (§2.3). No accent enters the band.
5. **START on the cued row** (FocusSequencerScreen.kt:577–581):
   accent TEXT on the commit verb — LAWFUL under (ii), stays.
   Its type is finished in §10.4.
6. **`#FF8A5C`** (Color.kt NightColorAlert; BlockitCoreViews
   NIGHT_ALERT :103): the REGISTER denies this token everywhere
   (it also breaches the AOD luminance ceiling). Census class:
   must sample ZERO on every screen, both modes. The lane names
   `tokAlert()`'s consumers in the lane log and re-voices them
   to the locked night accent #F47442 or ink per the accent law
   — no new hex enters the tree.

### 8.2 The census method (per screen; Quaere executes the close)

For each screen (Deck rest, Deck running, Tasks, Shield,
Drawer, PARK) × (day, night): harness render → count pixels in
the accent set {#F55314, #F47442, #D63E06, #FF8A5C}. Allowance:
exactly the one commit control's fill area (Deck: the slide
cap; all other screens: zero fill) plus commit-verb TEXT glyph
area (START, UNDO — bounded, anti-aliasing fringe included in
the allowance's tolerance). Any other accent pixel is a charge
with a coordinate. The census harness is a test-side utility
(~90 lines) sampling the harness's PNG output — it joins the
suite permanently; ESR-19 closes on its green run over the
merged tree, and the same run is P2-M6's close (§12 of this
spec).

---

## 9. C8 — ONE-LAMP UNITY (the wheel's light)

Law: REGISTER specular (face form 0.16 + 0.66·t³; θ_L = 245.2°);
FINISH-LIST P2-S2…S6; Finish Pass 2's measurement (Quaere:
specular arc centred 300.25°, lit span 250.5°–350.0°, both
modes — a proven defect). The Deck's light directions unify to
the ONE lamp at **245.2°**. All sites are in `RotaryWheelView`
(BlockitCoreViews.kt:348+). **The hero time readout is
PROTECTED — nothing here touches it.**

**VERIFY FIRST (before any edit):** one mid-drag render settles
whether the specular is screen-fixed or rim-rotating (P2-S2's
named uncertainty). The knurl comment at :554 already records
the rest-state truth ("screen-fixed material"); the render
confirms it in the lane log, then:

1. **The chamfer arc** (:524–537) — the measured defect itself:
   a uniform 2dp arc (azimuth 250→350) in surfaceBright @80% /
   sheet @60%. Rebuilt as the lamp's specular: stroke 2dp at
   the chamfer radius, tint #FFF6EC, alpha per azimuth
   **0.16 + 0.66·cos³(θ − 245.2°)** for |Δ| ≤ 90°, 0 beyond —
   drawn as graded segments (the arc is segmented finely
   enough that adjacent-segment steps are sub-visible; the
   lane names its step). Peak at 245.2°, dying at 155.2° and
   335.2°.
2. **The knurl teeth** (:554–600): the teeth's specular today
   centres on canvas 220° — a second lamp. Re-centred: the
   same S-2 curve (× Mara's `toothFlare` table — the broken
   teeth 17/83 stay broken; the flare is material, not light),
   brightest tooth at 245.2°, darkest at 65.2°, adjacent-tooth
   step ≤ ~2% luminance, total peak ≤14% day / ≤8% night.
3. **The socket shading** (:507–516): two flat half-rings
   (dark top / light bottom — a step due south, the third
   direction). Deleted; replaced by per-tooth-matched graded
   shading on the same curve (P2-S3's bezel law applied to the
   socket ring): toning varies continuously with azimuth,
   peak at 245.2°, no step anywhere on the ring.
4. **The hub core** (absent today): built AS REGISTERED —
   azimuth **315°**, core ±9°, cos² falloff to zero at ±34°,
   lift +10% day / +8% night toward #FFF6EC, on the hub disc
   only, never over the wells. **This is not a fourth lamp and
   not a fork**: the Register books the hub-cap core as a
   separate constant in so many words ("a different constant…
   both are registered so no builder spends one on both
   surfaces"), §8.10 repeats 315° as the rig's fixed azimuth
   for the cap, and P2-S5's instruction is "build the
   registered core as written." Unity governs the FACE's lamp
   (items 1–3, one direction everywhere it appears); the core
   is the machined cap's own glint. A builder who "unifies"
   the core to 245.2° has broken the Register, not obeyed it.
5. **The reader dot + sweep** (P2-S4; :605–695): the active
   sweep arc (today `tokPrimary()` — ink day / full cream
   night) re-voices: day → text-2 #5F5856; night → glyph
   #EDE3D8 @70%. The reader disc (today ink day / glyph night,
   Ø28dp): night fill → cap #4A423C; the inner pip (today
   onPrimary, Ø8dp) → a 6dp dimple, ground @40%, with a 1dp
   leading-edge marker line. Day disc stays ink #252323.
6. **Ticks** (P2-S6; :625–645): majors at night → glyph
   #EDE3D8 @80% (today textSecondary full); minors: night →
   text-2 #9C8E84 @55%, day → muted #5F5856 @55% (today
   surfaceVariant both modes). Mids unchanged.
7. **Wells** (P2-S5's dots; :745–758): filled wells at night
   (today `tokPrimary()` = #FFE4CF full — N-10's flood) →
   #EDE3D8 @80%; well radius 5dp → 4dp. Day unchanged.

Price: 120–150 production; 80–110 tests (census assertions at
Quaere's fixed sample points: arc-centre fit ±3°, tooth peak
azimuth, no-step scan of the socket ring, hub-core presence
inside ±9° and absence outside ±34°).

---

## 10. C9 — FINISH-LIST FOLD-INS

Aurel's list is binding input; what follows is the part of it
this wave owns. (P2-A tear motion already landed in the drawer
merge; P2-R wheel motion is wheel-physics territory; the
sound findings are not chrome.)

### 10.1 F-04 — the seam (every scroll surface)

- Bottom content padding **100dp** (= rail 52 + fade 24 + air
  24 — the Finish figure survives the rail's new height
  unchanged) on: Tasks' LazyColumn (FocusSequencerScreen.kt:
  121 — today modifier padding only, NO contentPadding anywhere
  in the tree), Shield's LazyColumn (ShieldMatrixScreen.kt:
  100), the Drawer's lists (DrawerScreen.kt:165, :276), and
  PARK's own scroll inset. Form: `contentPadding =
  PaddingValues(bottom = 100.dp)`; the existing horizontal
  modifier padding stays. The Deck is ACQUITTED (its pinned
  stack keeps content clear, Quaere P2-M8) — no Deck padding.
- **The 24dp fade**: ONE composable in MainActivity's viewport
  — a bottom-anchored gradient (transparent → background,
  linear) of 24dp directly above the rail host, so last rows
  dissolve into the seam on every screen at once. It carries
  no content and takes no touch.

### 10.2 F-05 — one selection language

The language (settled): selected = ink fill (day #252323 /
night cap #4A423C) + glyph text + 1dp marker line on the
leading edge (cream @90% night / #FAF6F0 @90% day); unselected
= no fill, 1dp outline in text-2 @40%, text-2 text; no
unselected element lighter than the selected, either mode.
Where chrome touches selection: the shield cap already speaks
it (§6); the rail's words are plate print, governed by §2, not
this language; the sleds (`SledSwitchView`,
`AppRowSledToggleView`) audit clean or re-voice — the lane's
F-05 audit is a named step with its result in the lane log.
The substantive build is the **task horizon** (P2-W2/W3 —
ESR-14's flood dies here): `TaskHorizonBarView`
(BlockitTowerFadersAndCatalogViews.kt:343) re-voices —
night: segment 0 → cap #4A423C with glyph #FFE4CF and a 1dp
top marker line #FFE4CF @90%; segments ≥1 recede toward the
F-03 ceiling (**#A79587** — the 38% cream blend over night
ground — is the absolute ceiling for ANY night fill here,
never raw #FFE4CF); day: seg0 #252323, seg1 #615B5A, seg≥2
#D8D1CB (recess), glyph #2C2A29; #FAF6F0 on this screen stays
reserved for the head leaf (§7) and the nav capsule's
successor forms.

### 10.3 The paper family (P2-W4/W5/W6 — Tasks furniture)

- Pad edge: pitch 2dp → 3dp, insets 4dp → 8dp (TaskLeafItem's
  end-padding formula, :455: `leavesBehind * 3 + 8`), edge
  tones depth-stepped — day k0 #A8A39F / k1 #BDB7B4 / k2
  #CCC7C3; night k0 #4E4847 / k1 #3B3735 / k2 #2D2927.
- Queued perforation alpha 46% → **30%** (PerforationRow :640:
  outlineVariant at 0.30 — day blend #BDB7B4, night #3B3735).
  The head leaf's raised perforation is §7.1's ground @100%.
- The ruled blank (empty-queue leaf, :332–356) gains its one
  rule line under the text (day #615B5A @18%, night #8F8888
  @18%), and the blank's height goes **56dp → 112dp** (the
  Finish ruling on the held blank, Ren mail/aurel/1010) —
  proof owed: empty-queue stills, day + night.

### 10.4 P2-W7 — START's type

The cued row's START (§8.1 item 5) → Anonymous Pro 700,
accent, letter-spacing 0.5, on the 15sp step
(FocusSequencerScreen.kt:570–585 adjusted). CUE stays as is.

### 10.5 F-03 — night fills, scoped to what survives

Night fills cap at the 38% cream blend **#A79587** (cream
blended 38% toward night ground #171412), top hairline full
cream @90% kept. Live surfaces in this wave: `TowerStreakView`'s
CURRENT brick (BlockitTowerFadersAndCatalogViews.kt:24 — its
other consumer is the running overlay; the accent exception
stands: the CURRENT brick in accent #F47442 is the Ledger's
single accent and is NOT capped). `DayFadersView`'s F-03 half
dies with the view under §11 — stated here so nobody prices it
twice. PARK builds flat from birth (§5.4), so the cap has no
PARK application.

Price (C9 whole): 170–220 production; 60–90 tests (scroll
extents measured from renders — last row's bottom ≥ rail top
+ 24dp air on Tasks/Shield/Drawer; horizon census points;
selection audit assertions).

---

## 11. C10 — CURIOS RE-HOME (the Drawer consequence, ruled in the open)

**The ruling: the curios live in the Drawer, as a cabinet
page. Not PARK, not killed.**

- Not PARK: §8.8's contents are a closed list by ruling, and
  PARK's material law (one tone, all print, zero decoration)
  admits no curios shelf. A PARK shelf would need a new ruling
  this record does not contain.
- Not killed: Sarthak's recorded feature state (Oct 28) lists
  curios among Monolith's KEPT features, and the artifacts
  still mint and persist on every completed manual session
  (MonolithRepository.kt:1310 → `forgedArtifactId`). The
  WORK-QUEUE addendum's kill door contradicts his recorded
  state; silence is the one forbidden outcome, so the conflict
  is settled here, in the open, for the re-home.
- **Form**: `DrawerView` (DrawerScreen.kt:66) gains **CABINET**.
  The Drawer landing (:466) gains ONE furniture row — `CURIOS`,
  a `DrawerNavRow` (:556) under the ALL MONTHS index row
  (:500). The cabinet lists the six catalog artifacts
  (`CurioCatalog`, MonolithModels.kt:674–748) as ruled rows —
  serial, name, subtitle — with forged state derived from the
  session rows: ONE new DAO query (`SELECT DISTINCT
  forgedArtifactId FROM sessions`, ~6 lines) + repository
  passthrough (~8). Tap → detail (lore, requirements, the
  existing select verb — VM `selectArtifact` :1460 is live
  code with no living caller; the cabinet gives it one); the
  harmonic-synth unlock state prints as a line when unlocked
  (VM :281), and the detail's serial line carries the existing
  `tapSerialPlateEasterEgg` (:1465) — the plate it taps must
  exist, and now it does. Sealed: the cabinet is read-only,
  like the Drawer itself (selection inert).
- **The six kinetic toy VISUALS never existed as views in this
  tree.** Building them is not chrome's charge and is not
  priced here; the cabinet is a reading/selection surface over
  existing data. If the toys are ever built, they hang off the
  detail page.
- **Data consequence, stated**: nothing is deleted from data.
  `forgedArtifactId` values keep minting; the catalog is
  untouched; existing forged artifacts become VISIBLE for the
  first time since the Drawer took the destination.
- **Deletions**: `CurioVaultScreen.kt` whole (−274; its
  telemetry/tower/faders/receipts content is Bound-Book
  machinery the Drawer ruling already killed — totals,
  streaks, 7-day aggregates — and its session-receipts list
  duplicates the Drawer's record law); `DayFadersView` (−110)
  and `TileNumberCounterView` (−76), dead once the screen
  dies (verified: no other consumer); MainActivity's dead
  CurioVaultScreen import (:60). REMOVAL-LOG entries owed for
  each, name by name. The Drawer's two fossil testTags
  `"curio_vault_screen"` (DrawerScreen.kt:170, :281 — a
  copy-paste fossil from the vault file; no test references
  them) are renamed to drawer tags in the same hunk.

Price: 215–255 production (cabinet ~190–240 + data ~25);
60–80 tests (cabinet render, forged derivation, sealed
read-only).

---

## 12. C11 — P2-M6 (the slide cap at night)

The slide is merged; this charge is census, not construction.
Code read this seating: the cap fill (BlockitCoreViews.kt:
1332) is `tokAccent()`, which resolves by `isNightMode` —
night #F47442, the locked night accent. The expectation is
compliance. The C7 census (§8.2) samples the Deck's cap in
both modes: day pixels read #F55314, night pixels read
#F47442, and **day saturation at night is a charge** — chrome
fixes only if the census fails (allowance 0–10 lines: the
only plausible fault is a token path bypassing `isNightMode`,
fixed at its site). The cap's grip line (P2-S8; :1342–1347,
today LIGHT_ON_PRIMARY at 3dp): the builder NAMES it in code —
this spec's recommendation is **marker** (it sits at the
leading edge and reads as position): 1dp, #FFE4CF @90%, both
modes. If the builder names it groove: 3dp stands + a 1dp
shadow line #471602 @35% at its left. Same price either way;
the census closes the chosen form.

---

## 13. FORKS — SETTLED, AND THE ONE QUESTION FOR REN

Settled from the record (each with its source; the builder
does not re-open them): band words + Ledger-over-VAULT (§2.1 —
commission + §6.4); four stops, not V-DEMOTE's three (§0);
missing prints the bare name, OFF never prints for a
permission (§3.2 — chair 08:58 #6 over §6.8-3's example);
hub core at 315° (§9 item 4 — the Register's own separation);
PARK absorbs, websites transplant to Shield (§5.6 — §8.8 +
plate law); curios → Drawer cabinet (§11 — his feature state
over the addendum's kill door); the Readiness Log is Slice
E's, the store's seam is chrome's (§4.3 — §8.3 + F-SCH-1);
the rail builds at 52dp under the open C5 contest (§1 —
§6.4), constants isolated.

**For Ren (one scope question, priced as far as the record
allows):** the Deck's 185.5dp void (Quaere's census: strip
bottom row 1148 → slide top row 1519) sits OUTSIDE this spec —
F-04 acquits the Deck, and the void is interior Deck
composition, not chrome. Quaere priced a cure in her letter
to Hale (unread by this seat; cited in Hale's review
dispositions). **Excluded from chrome; confirm the exclusion
or assign the cure to this lane with her letter attached.**
Nothing else in this spec waits on an answer: if Ren does not
rule, chrome builds as written and the void rides the Deck's
next finish car.

---

## 14. BOOK AMENDMENTS LANDED WITH THIS SPEC (Sana's owed marks)

From Ren's letters 1041 (Massarbeit's checker corrections) and
1210 (Slice E amendment obligations) + the gate_release entry
owed since 1150 — landed this seating, reported in my closing
letter:

1. **REGISTER.md**: `slide.gate_release = 0.66` entered as law
   (accent & gate region). Inline STRUCK marks: the BAND
   bullet's "(segment 85.5dp, cap 77.5dp), which the filed
   plan priced for" → pointer to the amendment (73.5/65.5
   governs, MASTER §6.4); the ROOM bullet's "fixed sum 796dp;
   reserve = H − 796" → pointer (821 governs, H = 841); the
   GUARD LINE bullet's "ACCESSIBILITY OFF" example → struck
   per chair ruling 08:58 #6 (bare name for MISSING).
2. **BEYOND-deck.md**: :282 "reserve = H − 796" and :577
   "Fixed sum is now 796" — struck with pointers to 821.
3. **MASTER-PLAN.md**: §5.7 (:2478) "no overlay" struck from
   the receipt sentence (F-L1 — the receipt host IS an
   aperture sheet; "no archive" stands); the HPA-01
   "Nothing seals… at window-open" sentences (:622, :834)
   carry dated amendment notes to F-S1(i)'s convergence sense
   (a materialising session seals through
   SessionSealTransition after proof — the window alone seals
   nothing BY DECLARATION, and there is no second path); §6.8
   check 3's OFF example struck (see item 1).
4. **SLICE-E-BUILD-READY.md** (:888): "manual-overlap sessions
   get no routine line" struck with a dated note — a
   hand-armed session inside a routine window gets the
   `WITHIN …` line at receipt assembly (F-Z5); the `BY …` ban
   stands. (Ren's letter named "§11"; the sentence's only home
   in the corpus is Ilya's quotation of the plan position —
   struck there, where Slice E's builder will read it.)
5. **Otis-N1** (§3.4's evidence form): verified against Ren's
   correction — §3.4's row form already prints the evidence the
   ruling requires; no text change needed, noted in RULINGS at
   its next pass.

The UNTRACED dispositions (Ren 0958 — U1–U6 relabels, C1–C4
closures, the seeded-history strike entering plan text) are
booked for RULINGS.md's next amendment pass under §0's record;
they change no line of this build spec.

---

## 15. VERIFICATION — THE ACCEPTANCE MAP

Every check in MASTER §6.8 (12) and §8.11 (23), mapped to its
method in this wave. Deferred halves are Slice E's by name —
chrome exposes the hooks and proves its half.

- **§6.8-1/2** (band geometry, mark only): renders, all screens,
  day + night; sealed/unsealed pair — no frame moves.
- **§6.8-3** (Guard line forms): unit tests (C2) + renders:
  whole / bare-name lack / STALE lack / cold blank.
- **§6.8-4** (store-only tracing): the one-store grep (merge
  gate, §4.7) + derivation tests.
- **§6.8-5** (war line): grep `186D` / NEET-count / TODAY-hours
  in chrome surfaces — clean.
- **§6.8-6** (rail geometry): render measurement (segment, cap,
  PARK 48, words fit; Ledger bare; PARK wordless).
- **§6.8-7** (travel): unit tests on the pure travel maths +
  DOWN+16ms screenshot pair + corner-tap test.
- **§6.8-8** (armed marker + zero orange): renders armed/resting
  + the C7 census over the band.
- **§6.8-9** (sealed chrome): chrome half — geometry invariance
  + Guard-line freeze renders at seal; pin/latch half → Slice
  E (rail exposes the seal input).
- **§6.8-10** (firing sentence homes): grep + renders — no
  firing time on Shield/Tasks/Ledger/PARK.
- **§6.8-11** (PARK invocation): gesture tests — one drag
  invokes on the seat frame; two rest heights; the three
  dismissal paths each verified; mid-rise row tap = no answer;
  LAST PROVED in PARK only (grep + renders).
- **§6.8-12** (board's sole home): renders of every screen —
  the board exists in PARK only.
- **§8.11-1** one-store grep (gate). **-2** STALE decay ≤45s:
  fake-clock tests. **-3** cold truth: chrome half (blank until
  measured) by render; log half → Slice E. **-4** age-print
  audit at the seam + PARK. **-5/-6** seal audits: chrome
  halves (frozen rendering, fixEnabled gate) by test; **-7**
  count audit (= §6.8-3). **-8** entry paths: exactly two +
  first run, by test. **-9** instant's sole home: grep.
  **-10** instant steps only on complete sets: store tests.
  **-11** board truth: render census — exactly 9 lines, words
  ∈ families, `BLOCKED` absent. **-12** FIX panes: all six
  intents verified per line. **-13** return frame-log: PROVING
  order test. **-14** sound row: sled identity + gate test.
  **-15** theme: three readout states rendered; the evaluation
  is the theme's own (single-when grep). **-16** plate
  byte-identical: string test against §8.10 as amended.
  **-17** photograph test: board + plate renders. **-18** state
  words Atkinson 700 15sp: render/type audit. **-19** zero
  orange + radii {16, 8} + ground sampling inside PARK: census.
  **-20** specular audit: PARK built flat — vacuous, recorded.
  **-21** sole home: Shield renders contain no board/FIX/LAST
  PROVED. **-22** first run: PARK opens unmeasured, leavable in
  one gesture. **-23** floors: ≥48dp targets; type ∈ the ramp.
- **ESR closes**: ESR-13 + N-1 on Quaere's census (§7.3);
  ESR-19 on the accent census (§8.2); ESR-04 on the strip APPS
  cell + Guard-line count agreement renders; N-2 (band
  guillotine) on the F-04 scroll-extent measurements; N-13 on
  the rail's words (§2.1).
- **Suite**: full run green at merge; `StripDerivationsTest`
  unmodified-green is a hard gate (§4.5); the census utility
  (§8.2) joins the suite permanently.
- **Everybody review** (Law 6 / A5): this spec and the lane's
  renders go to the whole studio; only S1 findings hold the
  merge.

— Sana, Designers coordinator, Oct 29, 2026. Inbox: 3 unread
at start, 0 at close. The letter to Ren carries the price
summary, the §13 question, and my NEXT.
