# SLICE E — HORIZ (Improvers, the horizon bar): E5b THE ORANGE EXIT, build-ready

*Seated by Ilya (Improvers coordinator) for the SLICE E build-ready wave.
My joint is only E5b — the orange exit inside Wren's Departure (E3).
Source part: `plan/slice-de-parts/mara.md` joint (b). Forks F22/F23 are
ruled ADOPT by Ren; this file does not re-argue them, it makes them
buildable. Every line number below was re-verified by my own grep
against TODAY's trunk (`~/workspace/clone-inspect/Focus-app`, working
tree as of Oct 29 09:46 IST) — not copied from Mara. Where today's
number differs from the filed one, I say so. No code edited, no
gradle run.*

---

## 1. THE TABLE — unchanged, arithmetic re-verified

Ren's granted §1.4 shading law, no interpolation, steps held between
boundaries, frame-exact at 60Hz and 120Hz (50ms = 3 / 6 frames):

| Step | t | × | DAY | NIGHT |
|---|---|---|---|---|
| 1 | 0ms | ×1.00 | `#F55314` | `#F47442` |
| 2 | 50ms | ×0.75 | `#B83E0F` | `#B75732` |
| 3 | 100ms | ×0.50 | `#7B2A0A` | `#7A3A21` |
| 4 | 150ms | — | terminal token (§2) | terminal token (§2) |
| — | ≥200ms | — | **zero orange** | **zero orange** |

I recomputed steps 2–3 channel-by-channel (multiply, round half-up):
day 245·.75=183.75→184=`B8`, 83·.75=62.25→62=`3E`, 20·.75=15=`0F`;
night 244·.75=183=`B7`, 116·.75=87=`57`, 66·.75=49.5→50=`32`; the
×0.50 row likewise reproduces exactly. Mara's hexes are correct.
The shading op must implement round-half-up explicitly — a
truncating `(c * m).toInt()` yields `#B73E0F` day step 2 and fails
test T1. This is the one place the table can silently rot; pin it.

---

## 2. CARRIER TABLE — verified in today's trunk

All six carriers exist exactly as Mara filed them. Five of six sit
at her exact lines; the theme citation moves by one line (§3).

| # | Carrier | Site TODAY | At t=0⁻ | Terminal / born tone |
|---|---|---|---|---|
| B1 | Slide cap fill | `BlockitCoreViews.kt:1133` — `paintFill.color = if (isArmedState) tokSecondaryContainer() else tokAccent()`; resolver `tokAccent()` at `:139` (`LIGHT_PRIMARY_CONTAINER 0xFFF55314` :69 / `NIGHT_PRIMARY_CONTAINER 0xFFF47442` :90) | accent — guaranteed, not assumed: the host forces the accent branch, `TactileDeckScreen.kt:496` sets `view.isArmedState = false` on every update | `tokControlFill()` `:133` — day `#252323` (`LIGHT_PRIMARY` :67) / night `#4A423C` |
| B2 | Slide progressive trail | `BlockitCoreViews.kt:1104–1106` — `paintAccent.color = tokAccent()`, `alpha = 55`, restored to 255 at `:1107`; paint declared `:175` | accent @ α55 over the track | same `tokControlFill()` @ **α55 unchanged** |
| B3 | Done sub-step numerals (`01`, `02`…) | `TactileDeckScreen.kt:436` — `color = if (step.isDone) scheme.primaryContainer else scheme.onSurfaceVariant` | accent, **conditional**: steps expanded ∧ ≥1 done | `scheme.onSurfaceVariant` — day `#5F5856` (`Color.kt:23`) / night `#776A60` (`Color.kt:44`) — i.e. the row's own undone tone, already the else-branch at the same line |
| B4 | Running progress fill — **born at seal** | `TactileDeckScreen.kt:346` — `.background(scheme.primaryContainer)` (6dp track, running branch `:337–365`) | born accent | `inkControlFill()` — `MonolithCustomComponents.kt:439–442`, already imported at Deck `:53`; day `onBackground #2C2A29` / night `#4A423C` |
| B5 | Chrome session dot — **born at seal** | `MonolithCustomComponents.kt:227` — `.background(if (isSessionActive) scheme.primaryContainer else scheme.outline)`, inside `MonolithPullDownHeader` (`:192`), composed at `MainActivity.kt:254` — **in the root, outside the Deck** (this decides §3's shape) | born accent | `scheme.onBackground` — day `#2C2A29` / night `#D2C6BB`; precedent: the Canvas nav dot at `BlockitCoreViews.kt:1352–1354` |
| B6 | GRACE WINDOW ABORT chip + badge rule — **born at seal** | callsite `TactileDeckScreen.kt:512` (`isAccentArmed = true`, chip bg from `MonolithCustomComponents.kt:154`); badge rule `:180` — `monoColor = if (isPrimaryInk && !isAccentArmed) scheme.primaryContainer else fg` | born accent (chip ≤10s window, on screen through Settle) | callsite becomes `isPrimaryInk = true` (bg → `inkControlFill()` via `:155`, fg → `onInkControlFill()` via `:160`); badge rule at `:180` loses its accent branch — badge takes `fg` |

**Base-shift warning — B1/B2 flagged RE-PIN AFTER THE SLIDE MERGE.**
Both sit inside Marlowe's in-flight slide rewrite
(`builds/slide/Focus-app/`). In that tree today the same code lives
at `tokAccent()` `:141`, trail `:1284–1287`, cap `:1316` — a drift
of +180/+183 lines, names and structure stable. The E5b build must
not start on the pins above if the slide merge lands first; test T7
is the gate. The re-pin checklist is three items: (1) trail site,
(2) cap site, (3) the host still forces `isArmedState = false` at
the seal frame — if the rewrite ever lets the armed branch
(`tokSecondaryContainer()`, day `#615B5A` at `:71`) reach the seal,
B1 is not orange at t=0⁻ and the exit has no subject; that is a
build-stopper, not a tone question.

**Sequencing risk carried from Mara (R2), re-verified:** the
shipped slide springs the cap home on commit —
`animateSpringBack(0f)` at `BlockitCoreViews.kt:1186/:1188`,
function at `:1214`. The exit presumes a seated cap through t=200
(E3's seat law). If E5b builds before the seat law lands inside the
slide rewrite, B1/B2's steps land on a travelling cap. Build order
§5.5 of the build-ready file (E3+E5 in one pass) already covers
this; I record it so the price in §5 is read as piggyback-only.

---

## 3. F23 — THE SCHEME PIN (the hold mechanism, named and priced)

**The fault, at today's lines.** The scheme is sampled at exactly
two points, and both flip on the session-active edge:

1. **Compose scheme (root):** `MainActivity.kt:103–106` calls
   `MyApplicationTheme(darkTheme = darkTheme, isFocusSessionActive =
   activeSessionForTheme?.isActive == true)`; `Theme.kt:103–107`
   (Mara filed :104–108; today the `if` is at :103, night scheme at
   :104, day at :106) selects `SealedVaultNightColorScheme` when
   `darkTheme || isFocusSessionActive`. This one flip re-tones
   **every** Compose carrier — B3 (:436), B4 (:346), B5 (:227, root
   header), B6 (:154/:180) — in a day seal it brightens the accent
   `#F55314 → #F47442` at the exit's first frame: step 1 runs
   backwards.
2. **Canvas night flag (Deck):** `TactileDeckScreen.kt:119` —
   `effectiveNight = isDarkTheme || isSessionRunning` — pushed into
   the views at `:281` (wheel) and `:494` (slide), where it selects
   inside `tokAccent()` (`BlockitCoreViews.kt:139`) for B1/B2.

**The mechanism — one boolean hold at the root, because B5 lives
at the root.** A Deck-local wrapper cannot pin the header dot, so
the hold sits where the scheme is sampled:

- **State:** a VM-owned `sealSchemeHold: StateFlow<Boolean>`
  (default `false`, in-memory only). It is set `true` on the same
  session-active edge that would flip the theme, and only in the
  one case that can flip: the flip is one-directional (an OR at
  `Theme.kt:103` can only add night), so a boolean is exact — no
  scheme snapshot needs storing. Night users (`darkTheme = true`)
  never set it; for them the hold is a no-op and the table's night
  column is true from t=0.
- **Root sample:** `MainActivity.kt:106` becomes
  `isFocusSessionActive = activeSessionForTheme?.isActive == true &&
  !sealSchemeHold` (2 lines changed, `Theme.kt` untouched — the
  theme's API and its other callers do not move).
- **Deck sample:** `TactileDeckScreen.kt:119` splits in two, which
  is exactly Wren's A3 made concrete: a **glyph flag**
  (`isDarkTheme || isSessionRunning`, unpinned — hero glyph at :250
  lands at t=0 with the content cut, per A3) and a **pinned flag**
  (`isDarkTheme || (isSessionRunning && !sealSchemeHold)`) feeding
  the two `isNightMode` pushes at `:281/:494` and every token read
  downstream of `effectiveNight`. E3 already prices an
  `effectiveNight` deferral (≈20 lines, wren.md §5a); the pin is
  that deferral extended to the root input — §5 prices only the
  delta, no double-count.
- **Duration: pin from the session-active edge through t=400,
  release AT t=400 in the ground-step frame.** Two rejected
  durations, with reasons:
  - *Through t=200 (exit end) — rejected.* The terminal tones are
    scheme-paired: cap `tokControlFill()` day `#252323` ≠ night
    `#4A423C`; numerals `onSurfaceVariant` day `#5F5856` ≠ night
    `#776A60`. A flip at t=200 would step the seated cap and the
    numerals a fifth, unfiled time, mid-Recession, on a still
    picture — a lie of exactly the kind A3 exists to prevent.
  - *Through Settle (t=1050) — rejected.* Wren's shot table steps
    the ground once at t=400 inside Recession and prices Settle's
    values on the stepped ground. A second flip at 1050 doubles
    the step and invalidates the settle frame.
  - At t=400 the release **is** A3's ground step: hold clears,
    scheme flips, ground steps, terminal tones take their night
    values (`#4A423C` / `#776A60`), and the born carriers B4–B6
    take their night ink values — one frame, one cut, every
    surface moving together. Nothing steps twice; nothing steps
    early.
- **Release driver — at the root, not in the Deck.** The trigger
  lives in a `LaunchedEffect` beside `MainActivity.kt:103`, keyed
  on the hold, comparing frame time against the VM's `t0Mono`
  (already plumbed by E3, wren.md §5a ≈25 lines) and clearing the
  hold at the first frame with elapsed ≥ 400ms. Root placement is
  load-bearing: if the user leaves the Deck mid-shot, a
  Deck-composition effect dies with the composition and the app
  would sit in a stuck day scheme for the session's life.
- **Safety releases (any one clears the hold immediately):**
  session ends or is annulled (F7's row deletion — an unproven
  seal must not leave the theme pinned); process death (the hold
  is in-memory, defaults off — Wren's derive-don't-replay already
  declares the shot dead with the process); reduced motion (Wren
  collapses the shot's sync points — the exit's tone steps are
  not motion and still run, so the hold releases at **t=200**,
  the exit's own end, instead of 400).
- **What the hold does NOT do:** it does not delay the session,
  the seal, input death, or any StateFlow the VM publishes. It
  delays one theme input and one Deck flag. The session is real
  from its first frame; only its colour scheme waits 400ms.

---

## 4. FINISHERS CROSS-CHECK (FINISH-LIST-1.md) — no collision, no new fork

Checked E5b's six tones against every Finisher number that touches
the Deck, the Ledger night fills, and the accent budget:

- **F-01 (accent budget: one surface per screen, Deck = slide
  cap).** Aligned, not colliding. F-01 kills the accent on the
  HELD numeral, SYSTEM/HORIZON badges, and all Ledger numerals —
  none of those is a B-carrier. B3's terminal `onSurfaceVariant`
  (`#5F5856`/`#776A60`) is **not** F-01's glyph tone
  (`#252323`/`#EDE3D8`), and that is not a collision: F-01's tone
  is the *permanent* fate of numerals that should never have been
  orange (the Tasks/Finishers lane owns it); E5b borrows the
  sub-step row's own existing undone tone — the else-branch
  already at `TactileDeckScreen.kt:436` — for the 200ms the exit
  owns the row. Two lanes, two timescales, no shared frame in
  which both numbers claim the same pixel.
- **F-03 (Ledger night fills):** night fill = cream blended 38%
  toward night ground `#171412` → `#A79587`, current brick exempt
  at `#F47442`. E5b has no Ledger surface: B4's night ink
  `#4A423C` is the Deck's running progress fill, not a brick or
  fader fill, and no E5b carrier ever takes `#A79587`. No overlap.
- **F-05 (one selection language)** corroborates E5b outright:
  its selected fill is ink — day `#252323` / night cap `#4A423C`
  — the exact pair B1/B2 terminate in. The cap lands where the
  app's selection language already lives.
- **REGISTER-wins clause:** no E5b tone collides with a Finisher
  number, so the clause never fires and **no collision fork is
  filed**. If a later Finisher pass re-tones the Deck's ink pair,
  B1/B2's terminal follows `tokControlFill()` by reference, not
  by copied hex — the exit inherits, it cannot drift.

---

## 5. RE-PRICE — E5b = ≈61 net production lines (filed ≈50)

Net lines, production only, against today's trunk, piggybacked on
E3's `SealShotState`/`vertigoT` (build order §5.5 lands E3+E5 in
one pass). Tests priced separately below.

| Item | Site | Net |
|---|---|---|
| Shaded-accent resolver (multiply + round-half-up op, one place beside `tokAccent()`) | `BlockitCoreViews.kt:~139` | ≈10 |
| B1/B2 consume resolver; `exitPhase` property + invalidate on `SlideToStartView` | `BlockitCoreViews.kt:1104–1106, :1133` | ≈6 |
| Phase plumbing: phase derived from `SealShotState` elapsed in Deck; `update{}` wiring; B3 colour by phase → terminal | `TactileDeckScreen.kt:436, :493–500` | ≈13 |
| Born carriers B4+B5+B6 (three one-line re-tones + one one-line rule change) | Deck `:346, :512`; Components `:180, :227` | ≈4 |
| **F23 scheme pin (the delta nobody priced):** VM hold field + set/clear-on-session-end (≈10), root sample + release effect in MainActivity (≈6), Deck :119 glyph/pinned split (≈6) | VM; `MainActivity.kt:103–106`; Deck `:119` | ≈22 |
| **Subtotal** | | **≈55** |
| Contingency on the slide-merge re-pin (T7 may move B1/B2 wiring) | | ≈6 |
| **E5b total** | | **≈61 net** |

**Delta against the filed ≈50, stated plainly:** Mara's ≈50 was
core ≈40 (piggyback) + born ≈4, with F23 carried as an unpriced
ruling dependency (her R1/F3 — "needs one ruling in the merge",
zero lines). The ruling is now ADOPT and the mechanism is real
work: +22. Core comes in ≈5 under her figure because E3's shot
state and VM `t0Mono` plumbing already exist in E3's price.
**Standalone condition:** if E5b is ever built before E3, add
Mara's Choreographer phase driver ≈15 → ≈76. Do not build it
standalone; the condition is recorded so nobody quotes 61
without its piggyback.
**Tests:** ≈55 lines (T1–T6 below; T7 is a grep gate, no lines).

For the roll-up: E5 total (D6 ≈80 + E5b ≈61) ≈ **141 net**,
against the build-ready file's ≈130. The +11 is the F23 pin,
entirely.

---

## 6. ACCEPTANCE TESTS

- **T1 — shading table (unit).** The resolver, both modes, phases
  0–3, equals §1's table hex-for-hex, including the round-half-up
  edge (night green 66·.75=49.5→50). A truncating implementation
  must fail this test.
- **T2 — step frames (Robolectric render, existing
  `TetradRenderTest`/`ScreenshotHarnessTest` harness).** Day seal,
  **steps expanded with ≥1 step done** (Mara's R3 — a collapsed-
  steps test never sees B3 and ships it unshaded): cap pixel at
  t=49/50/100/150 reads `#F55314 → #B83E0F → #7B2A0A → #252323`;
  trail pixel carries the same sequence at α55; numeral pixel
  ends at `#5F5856`. Night seal: the night column, ending
  `#4A423C` / `#776A60`.
- **T3 — zero-orange scan.** Full Deck + header frame at t=200
  and at t=399: no pixel equals any accent or shaded-accent value
  from §1. The scan must include the born carriers (progress
  fill, header dot, grace chip + its badge) — they are in frame
  precisely when a slide-only scan would pass.
- **T4 — F23 pin.** Day seal: at t=100 the live scheme is still
  the day scheme — asserted on device-visible facts, not internals:
  header dot reads `#2C2A29` (not night `#D2C6BB`) and the cap's
  step-2 pixel is day `#B83E0F` (not night `#B75732`). Scheme
  identity changes **exactly once**, at the first frame ≥ t=400.
  Night-user seal: hold never sets; steps follow the night column
  from t=0.
- **T5 — born at ink.** First running frame (t=0⁺ composition):
  B4 fill = `inkControlFill()` (day `#2C2A29`), B5 =
  `onBackground`, B6 chip bg = `inkControlFill()` with badge in
  chip fg (cream). The EMERGENCY BREAK chip's badge is asserted
  non-accent in the same test — see R4: that consequence is
  global and must be a checked decision, not a surprise.
- **T6 — hold safety.** Annulled seal (F7) and process-death
  simulation before t=400: hold cleared, theme follows live
  session state on next composition, no stuck day scheme.
- **T7 — re-pin gate (no code).** If `builds/slide` merges before
  E5b builds: re-grep B1/B2 sites and the `isArmedState` host
  forcing in the merged tree and amend §2's pins before the
  first E5b line is written.

---

## 7. NAMED RISKS

- **R1 — scheme flip at t=0** (Mara's R1, blocking): closed by
  §3's pin, gated by T4. Without the pin the day exit's first
  step brightens — the exit lies before it starts.
- **R2 — spring-back cap** (`BlockitCoreViews.kt:1186/:1188`):
  exit steps presume a seated cap; sequencing owned by the
  slide rewrite + E3 seat law (§2). If E5b lands first, T2's
  frame samples are taken on a moving cap and will flake —
  that flake is the signal, not a test bug.
- **R3 — B3 conditionality:** covered by T2's mandated fixture
  (steps expanded, one done). Any test suite that seals with
  steps collapsed is blind to one-third of the at-frame carriers.
- **R4 — the `:180` badge rule is app-wide.** `BlockitActionChip`
  is shared: the same one-line change de-oranges the badges of
  every primary-ink chip, including Shield's at
  `MonolithCustomComponents.kt:383/:390/:420`, not only the
  Deck's two. F-01's accent budget supports the direction
  (badges → glyph/fg everywhere), and Mara already claimed the
  EMERGENCY BREAK badge on this reasoning — but the builder and
  Ren should see the true blast radius in print: **one line,
  every screen with a primary-ink chip.** Priced as filed (1
  line); if Ren wants it Deck-scoped instead, that is a
  parameter on the chip (+≈4 lines) — a ruling, not a fork I
  need: the default stands unless he says otherwise.
- **R5 — slide-merge drift:** B1/B2 pins rot the day
  `builds/slide` merges (already +180/+183). T7 is the gate;
  §5 carries ≈6 lines of contingency for it.
- **R6 — hold outlives its shot:** mitigated by design (root
  release driver + three safety releases, §3) and gated by T6.
  The failure mode if all three releases were bypassed is a
  day scheme held for one session's life — visible, recoverable
  at session end, never persistent across process death.

## 8. FORKS

**None.** F22/F23 arrive ADOPT and are specced, not re-opened;
the Finisher cross-check (§4) produces no collision fork; R4's
scoping question carries a stated default and needs no ruling
to build.

## NEXT

(a) *What I want next:* the E3/E5b seam audit — Wren's
`SealShotState` is the one object my phase plumbing, my pin
release, and her A3 deferral all hang off; I want the pass that
reads her built (not filed) shot state against §3's contract
before the Deck build starts, because three joints sharing one
holder is exactly where horizon failures hide.
(b) *What I could do unassigned:* a trunk-wide sweep for other
one-directional OR-flips of the `Theme.kt:103` shape — any place
a session-active edge silently re-tones a surface nobody has
enumerated as a carrier (the B5 pattern, generalised).

— Horiz, Improvers · the horizon bar
