# ISSUE #9 — DESIGNER SPEC: THE CHECK VERB + THE NOT CHECKED LINE

*Designer, issue-9 cell (for Hof). Ground: `AUDIT-park.md` §1/§2/§4
(Hof's rulings stand — nothing here re-opens them), `AUDIT-park-claims.md`
(Una), `TRUTH3-WORDING.md` W-08/W-11, and the cell tree's own code:
`ParkDrawer.kt`, `GuardLine.kt`, `WatchStore.kt`, `Type.kt`, `Color.kt`,
`MonolithCurves.kt`. Paper only — nobody compiles.*

*Scope, exactly: (1) the CHECK verb on PARK's board (audit P-1, merge
list §4.1); (2) the cold-open Guard line printing `GUARD — NOT CHECKED`
(P-6, §4.5); (3) the plate's struck "FIRED AS SET" clause (P-7, W-11) —
its print treatment only; (4) the INPUTS for Ren's W-08 dynamic plate
line, batch 10 — inputs only, the plate's design is Ren's lane and is
not duplicated here. NOT in scope: the Live-Fire Strip (parked,
issue #21 — CHECK must never borrow its language or its promise),
the warrant line (§4.2), tiers/ages (§4.4/§4.7), the FIX staircase
(§4.6). Where those neighbours exist, CHECK composes with them;
it does not redesign them.*

*House law applied throughout: Sora display / Atkinson Hyperlegible
body / Anonymous Pro numbers, off the existing type ladder only
(22/15/10 — no new sizes); zero green and zero orange on PARK's faces
(§8.10 — the alert token does not enter this drawer, not even for
failure); no icons, no spinners, no imagery; state shown by the
object's form first, words only where the object cannot speak;
no telemetry or spec-speak printed on any face.*

---

## §1 · THE CHECK VERB

### 1.1 What it is, in one sentence

CHECK is the board's own verb: one press re-runs the grant-check
sweep (`WatchStore.runSweep` — the same six parallel OS queries the
Deck entry and FIX return already fire) and the board re-prints from
the landings. It is a *re-measurement*, nothing more: the evidence
grade stays SELF-REPORT, and no copy anywhere may let CHECK imply
it witnessed blocking. That claim belongs to the parked Live-Fire
Strip (issue #21) and is not designed, hinted, or pre-sold here.

### 1.2 Placement

- The key sits **directly under the board**: below the interceptor
  family's last hairline, above the foot verdict line
  (`GUARD ···· {n}/4 · {HH:mm}`, §4.3) and the LAST PROVED colophon
  (§1.3 of the audit). The board, its verb, and its signature read
  as one instrument.
- Never in the header zone, never docked to the sheet's foot, never
  floating over the scrim. It belongs to the board the way FIX
  belongs to the family it repairs (audit §1.8's placement law).
- Vertical rhythm on the audit's spacing module {8, 16, 24} only:
  **16dp** board → key, **16dp** key → foot block.
- When FIX is also present, the stack order is **FIX first, CHECK
  second, 8dp between them**. FIX names a measured lack — the
  stronger, more specific verb leads. This is safe even while other
  rows are STALE: MISSING never stales (acquitted, §7), so FIX's
  naming is never the stale part.
- CHECK is **always present** while PARK is open and unsealed —
  unlike FIX's conditional presence. P-1's charge was that the verb
  existed *nowhere*; a verb that vanishes when the board happens to
  look fresh teaches him it isn't his to call. Its dress (§1.4),
  not its presence, carries whether it is needed.
- The drawer's existing auto-sweep on PARK open (§4.1's other half)
  and CHECK are the same act: opening PARK fires the sweep, and the
  key simply shows that act (§1.5). A press while the open-sweep is
  still in flight is absorbed — no second sweep is dispatched.

### 1.3 Anatomy — one key grammar, shared with FIX

CHECK is built from FIX's exact object, so the drawer keeps a
single key grammar:

| Property | Value (source) |
|---|---|
| Width / height | full content width / **48dp** (`park_fix_key`) |
| Corner radius | **8dp** (the key radius; panel stays 16dp) |
| Word | `CHECK` — Sora Bold 15sp, +0.5 tracking (`PrimaryButtonTextStyle`) |
| Press dip | 1dp downward offset, held for the act's duration (FIX's dip law) |
| Press sound | the seat knock, FIX's own settings: `playSeatKnock(10ms, 0.10 day / 0.06 night, 300Hz)` |
| Touch target | the full 48dp key; rows stay inoperable mid-rise, per KIN-DRAWER as today |
| testTag | `park_check_key` |

No icon, no magnifier glyph, no circular arrow — the word on the
object is the whole face. `contentDescription`: **"Check guard
state"** (plain words for the screen reader; the visible face
stays the one word).

### 1.4 The dress ladder — the form speaks first

The key has two dresses, and which one it wears is itself the
board's headline state:

- **Print dress (at rest, board fresh):** transparent ground, 1dp
  outline in the strong-hairline token (`outline`), word in primary
  ink. Present, obviously a key, making no claim on his attention.
- **Ink dress (CHECK is the needed verb):** the FIX fill —
  `inkControlFill()` with `onInkControlFill()` text. The object
  promotes itself to full ink exactly when pressing it is the
  answer: any counted row STALE, any row UNMEASURED, or the last
  act FAILED (§1.5).

No third colour exists. Failure never wears the alert orange;
promotion and failure are both said in ink and in words.

### 1.5 States

Four states. The board's rows are never re-dressed to show
progress — a row re-prints atomically when its own query lands,
exactly as sweep landings already behave. **The key alone carries
the in-progress state; the board alone carries the result.**

**IDLE** — PARK settled open, no sweep in flight.
- Board fresh (every row measured, none stale): print dress, word `CHECK`.
- Board stale or unmeasured: ink dress, word `CHECK`.
- A press dispatches the sweep and enters CHECKING.

**CHECKING** — from dispatch until the set completes or the
window closes.
- Ink dress, held at the 1dp dip for the whole act. Word: `CHECKING`.
  (One honest word, in the key's own Sora — not a spinner, not an
  animated ellipsis, not a percentage. No telemetry on faces.)
- The key takes no second press. Rows land one by one behind it;
  LAST PROVED does not move yet — the complete-set rule is
  acquitted law and CHECK does not bend it: partial landings print
  on their rows and advance nothing.
- Interceptor rows and the warrant line re-derive from the same
  landings for free (derivation at every emission) — CHECK fires
  no second machinery for them.
- Expected duration is the sweep's own (~300ms class). The
  completion window is **2,500ms** from dispatch: any item not
  landed by then counts the act as failed *for that item*. Landed
  items keep their new stamps — partial truth stands, as it does
  everywhere in this store.

**PROVED** — the complete six-item set landed inside the window.
- The key releases its dip (PRESS-BACK, as FIX), returns to print
  dress, word back to `CHECK`.
- The proof itself is printed where it already lives: the foot
  verdict line takes the new `{HH:mm}`, the LAST PROVED colophon
  advances. CHECK adds no "success" line of its own — success is
  visible in the object (a fresh board), so it gets no word.
- Sound: if the completed set is whole (counted 4/4 GRANTED),
  one firm click (`playFirmClick()`) at the completion instant —
  the same voice, and the same condition, as FIX's repaired click.
  If the set completed *with lacks*, **no click**: the board's
  re-print and FIX's appearance are the verdict, and a click there
  would sound like good news it isn't.
- PROVED is a settle, not a banner: it lasts exactly as long as
  the board stays fresh. The first row crossing the 45s stale
  line returns the key to IDLE in ink dress. Nothing expires,
  fades, or needs dismissing.

**FAILED** — the window closed with items unlanded (a query threw,
hung, or never returned).
- The key releases its dip, **stays in ink dress** (it is still
  the needed verb), word back to `CHECK`.
- And here — only here — a word is added, because failure is the
  one outcome the board cannot show by itself (unlanded rows look
  exactly as they did before the press; P-5's lesson: silence
  reads identical for fixed and still-broken). Directly under the
  key, 8dp gap, one line, left-aligned, Atkinson Hyperlegible
  Bold 15sp in full primary ink:

  `NOT PROVED — {NAME} DID NOT ANSWER`

  `{NAME}` is the first unlanded item in board order (the board's
  own names: `ACCESSIBILITY`, `USAGE ACCESS`, …). One name only,
  however many failed — the count-and-first-lack grammar the
  Guard line already speaks.
- The line **persists** — until the next CHECK press or the
  drawer closing. It never auto-expires; the man reading at 1 AM
   must not be able to miss it by looking up late.
- No click, no knock, no other sound on failure.

### 1.6 Transitions (existing KIN vocabulary only — no new curves)

- IDLE → CHECKING: dip + word swap at the press instant; the seat
  knock sounds on DOWN, as FIX's does.
- CHECKING → PROVED / FAILED: dip release over **160 THROW**
  (`DRAWER_CLOSE_MS` + `EasingThrow` — the PRESS-BACK the FIX key
  already uses); the word re-prints at the same frame. No fades
  longer than 160ms anywhere in this verb.
- Dress changes (print ↔ ink) are instant swaps at the state
  boundary — fills do not crossfade in this app, and CHECK does
  not start.
- Drawer closing mid-CHECKING: the sweep is the store's, not the
  drawer's — it completes in the store regardless, and the next
  opening shows its landings. The key keeps no memory across
  openings except what the store holds.

### 1.7 Under the seal

Sealed, PARK is print (audit §7 acquittal — read-only degradation
stands). CHECK renders in **print dress permanently**, same 48dp
footprint so the sheet's geometry never jumps at the seal, with
the sleds' own treatment: a touch-swallowing overlay, no press,
no dip, no sound. It does not absent itself — a missing verb
would re-teach P-1's lie in the one state where he can only look.

### 1.8 Copy register — the whole vocabulary of this verb

Printed: `CHECK` · `CHECKING` · `NOT PROVED — {NAME} DID NOT ANSWER`.
Nothing else. Never `REFRESH` (browser-speak), never `RE-PROVE`
or `VERIFY` (spec-speak claiming a grade the sweep doesn't hold),
never `TEST BLOCKING` (the parked strip's promise), never a
countdown, percentage, or generation number on the face.

---

## §2 · THE NOT CHECKED GUARD LINE (P-6 — "never blank")

### 2.1 The change, stated for the builder

`guardLineFor` (GuardLine.kt) today returns `null` in two cases —
snapshot null, or any counted item UNMEASURED — and the header
band prints nothing. Both cases now return a print:

`GUARD — NOT CHECKED`

- **No count, ever.** Not `GUARD 0/4`: a count would be the
  day-one lie this state exists to prevent (A-03 acquits the
  line's words precisely because it never prints an unmeasured
  number). The absence of the count *is* the form saying
  "unmeasured" before the words are read.
- The trigger is **any** of the counted four UNMEASURED — one is
  enough, at cold open or at any later instant (e.g. a fresh
  residency before the first sweep lands). Measured MISSING is
  measured: the line leaves NOT CHECKED for the normal lack form
  `GUARD {n}/4 — {NAME}` the instant the set is fully measured,
  lacks and all.

### 2.2 Dress — the band does not change for this line

The header band is a nameplate: 47dp row over a 1dp hairline,
invariant. NOT CHECKED wears the Guard line's existing dress
verbatim — Anonymous Pro Bold 15sp, +0.5 tracking, primary ink
(`onBackground`), right-aligned, 20dp end padding, same
`header_guard_line` testTag, baseline-identical with `GUARD 4/4`
and every lack print. No alert colour, no outline box, no pulse,
no icon. The band's form says *a line is printed here*; the words
say what the line knows. On the cold-open face the difference
between blank and printed is the entire cure — decoration would
only argue with it.

### 2.3 Behaviour

- **Cold open:** the line prints NOT CHECKED from the **first
  frame** — no blank frame, no `0/4` flash before it. It holds
  until the counted set is fully measured, then swaps in place
  to the measured print. Text swap only: same slot, same type,
  no animation (the band never animates).
- **Implementation contract:** the print must be *unblankable* —
  wherever the band renders unsealed or sealed, one of the three
  prints (whole / lack / NOT CHECKED) is on the glass. Builder:
  making `guardLineFor` total (non-null return) and dropping the
  band's null gate is the clean way to guarantee it; the two
  ViewModel capture sites and the seal freeze consume the same
  function and inherit the guarantee.
- **Tap:** unsealed, the line remains the third door into PARK —
  in NOT CHECKED state above all, since the drawer is where the
  verb lives. Sealed: print only, tap gated, as today.
- **Seal freeze:** if the seal captures while unmeasured, the
  frozen line reads NOT CHECKED for the session's whole life.
  That is honest, not a defect: the seal froze an unchecked line,
  and §2.3's contract is that the frozen print is the same print
  the live band would have made at that instant. Do not "fix"
  this by re-measuring under the seal.

### 2.4 The board's twin — explicitly NOT changed

PARK's board rows keep their acquitted name-only print while
unmeasured (audit §7, Dens/Fremd/Vega). Do not print UNMEASURED
(or any word) on board rows as part of this item. The summary
line lied by silence; the rows never did. This cure touches the
header line only.

---

## §3 · THE PLATE: "FIRED AS SET" STAYS STRUCK (P-7 / W-11)

Design treatment, for the record (the words half already stands
in this tree — `ParkDrawer.kt` PLATE_BODY prints
`ROUTINES: SET BY HAND ON SHIELD`, with the W-11 comment at the
site; the builder's job on this item is verify-only):

- The struck clause leaves **no trace on the face**: no gap line,
  no placeholder, no asterisk, no footnote. The routines line
  stands as one whole true sentence at the plate's normal dress
  (Anonymous Pro 10sp, secondary ink, in PLATE_BODY's order).
- No other PARK surface may imply the struck claim in the
  meantime — not the foot verdict line, not CHECK's vocabulary
  (§1.8), not a routines count anywhere in the drawer.
- **Restoration, pre-designed so it is a print act, not a design
  question, when the day comes:** when Wache's F1–F5 are in the
  *same build* AND a live routine fire has been re-verified on
  the phone, the line returns in exactly its old form —
  `ROUTINES: SET BY HAND ON SHIELD · FIRED AS SET` — same line,
  same dress, no badge, no emphasis, no announcement furniture.
  Both conditions, or the struck form stands.

---

## §4 · W-08 DYNAMIC PLATE — INPUTS ONLY (batch 10)

Ren's batch-10 lane owns the plate's design. What it will need
from this cell is one input, prepared here: the peek length, so
plate line 3 can print the set value (TRUTH3-WORDING W-08:
`PEEK: {n} MINUTES, THEN SHUT AGAIN`; `PEEK: OFF` at zero) instead
of a static number the editable-times setting would falsify.

- **Input:** `peekMinutes: Int?`, delivered to `ParkDrawer`
  alongside its existing inputs, in the same manner as
  `themeReadout` — a plain value, no store access inside the
  drawer.
- **Source of truth:** the same persisted value the peek lease
  reads at grant time — when the editable-times lane lands its
  `guard_settings` column, that column, read through the
  `guardSettingsFlow` PARK already consumes for its sleds (so a
  change made on Shield re-prints on the flow's landing, PARK
  open or not; a setting needs no sweep and carries no
  SELF-REPORT grade). **In this tree today** the lease is still
  the constant `MonolithRepository.PEEK_DURATION_SECONDS = 120`:
  until the column exists, the only lawful input is that constant
  (= 2). Never a composable-side default, never a `remember`,
  never a second copy of the number.
- **Print rules the input must support** (for the plate lane's
  use): `null` (settings row not yet loaded) → print no number —
  the filed fallback line `PEEK: A SHORT LEASE, THEN SHUT AGAIN`,
  never blank, never a guessed `2`; `0` → the OFF form; `1` →
  singular `1 MINUTE`; `n ≥ 2` → `{n} MINUTES`. Expected bounds
  0–30 (PILL-AND-TIMES §3); an out-of-range value prints as read —
  validation belongs to the editor, the plate only reports.
- Nothing else is prepared: no plate layout, line order, or
  wording beyond W-08's own ruling. One input, honestly sourced.

---

## §5 · BUILDER'S CHECKLIST (acceptance, by eye)

1. Open PARK unmeasured: key present in ink dress; board name-only;
   header band (behind the sheet) read NOT CHECKED before opening.
2. Open PARK: open-sweep fires; key shows CHECKING dipped; rows
   land individually; on completion the foot line + colophon take
   the new time; whole set → one firm click; set with a lack →
   no click, FIX seated above CHECK.
3. Let the board age past 45s with PARK open: rows go STALE at
   half-ink (their own cure), key promotes to ink dress unaided.
4. Press CHECK on a stale board → PROVED settle, key back to print
   dress. Force a query to hang past 2,500ms → FAILED line prints
   the first unlanded name, persists across a scroll, dies on the
   next press.
5. Cold-start the app: header prints NOT CHECKED from frame one;
   no blank, no `0/4`, ever; tap (unsealed) opens PARK.
6. Seal a session: CHECK is print at the same footprint; frozen
   Guard line holds whatever the seal-frame print was.
7. Plate: routines line reads `ROUTINES: SET BY HAND ON SHIELD`
   — full stop, no clause, no mark where it was.

— Designer, issue-9 cell (for Hof)
