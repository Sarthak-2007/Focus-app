# ISSUE #9 — FINISHER SPEC: THE BOARD'S PRINT AS IT RE-STAMPS

*Finisher 1 (board print), issue-9 cell (for Hof). Ground:
`AUDIT-park.md` (Hof's rulings), `issue-9-designer.md` (the CHECK
verb — built on here, never contradicted), and the cell tree's
current `ParkDrawer.kt` (909 lines, with Hof's CHECK key,
`lastCheckLackName` verdict line, PARK-open re-sweep, and the
`GuardLine.kt` `GUARD — NOT CHECKED` print already in). Paper
only — nobody compiles.*

*Scope, exactly: the nine board rows — and the NOT PROVED line
that borrows their grammar — at the one moment they have never
been finished for: a CHECK in flight, when each row re-stamps
independently as its own query lands. Ink hierarchy, hairline
discipline, and the animation law for a word change. NOT in
scope: the CHECK key itself (Designer's §1, settled), tiers and
the age column (§4.7 / §4.4 — neighbours; where they touch the
re-stamp, this spec states the constraint and stops), the FIX
staircase (§4.6), the warrant line (§4.2).*

*House law applied throughout: Sora display / Atkinson
Hyperlegible body / Anonymous Pro telemetry, off the existing
ladder only (22/15/10); never green, never the alert orange on
any board face; state by form first — ink weight is the form,
the word is the caption; no icons, no spinners, no imagery.*

*Every item is marked **SPEC-ONLY** (a ruling/verification the
builder obeys, no code delta) or **PATCH-READY** (the exact
`ParkDrawer.kt` change is given, ready to apply verbatim).*

---

## §0 · WHAT BINDS THIS SPEC — THE ACQUITTALS, RESTATED AS INVARIANTS

Audit §7 acquittals are not redesigned here; they are the frame
every item below is checked against:

- The nine ruled lines, their fixed order, the **49dp pitch**
  (48dp row + 1dp hairline, `ParkBoardRow` as built), and the
  flush right word column are acquitted. A re-stamp changes a
  row's *word and its ink* — never its height, its position,
  its name, or its order. Landing order is not board order:
  rows re-print in query-completion order *in place*, and the
  board never re-sorts, re-flows, or holds early landings back
  to paint the set together. Partial truth stands (Designer
  §1.5) — a half-re-stamped board mid-CHECK is the honest face.
- **Name-only unmeasured prints are acquitted.** An UNMEASURED
  row prints its name and no word, before CHECK and during it,
  until its own first landing. Nothing in this spec puts a
  word, a placeholder dash, or a dimmed guess-word on that row.
- **MISSING never stales** (Vega, acquitted). MISSING keeps its
  dress (F-B1) from measurement until a re-measurement replaces
  it — it does not fade with age, ever. The 5-second `nowMono`
  re-derivation beat may move GRANTED → STALE; it may never
  move MISSING anywhere.
- The complete-set rule for LAST PROVED stands: row re-stamps
  advance nothing above or below the board until the set
  lands. The foot/colophon prints are not this spec's surface.

---

## §1 · THE INK HIERARCHY — THREE DRESSES, FORM FIRST

### F-B1 · The hierarchy itself — **SPEC-ONLY**

The board word today wears a Boolean (`wordIsLack`), so it can
say only two things. A re-stamping board needs three, because
during a CHECK the reader's whole question is *which kind of
not-yes is this row right now* — and that answer must read at
glance distance, before the word itself is parsed:

| Word | Ink | Why this weight |
|---|---|---|
| `GRANTED` | **Quiet** — `onSurfaceVariant` (secondary ink) | A proved grant is the board at rest. It recedes; the eye is not spent on what is fine. (Today's GRANTED dress — kept.) |
| `MISSING` | **Full** — `onBackground` at alpha 1.00 (primary ink) | A measured lack is a fact, and the only board state that asks for a trip to Settings. It alone shouts. (Today's lack dress — kept, now reserved to MISSING alone among permission words.) |
| `STALE` | **Half** — `onBackground` at alpha **0.60** | A grant last seen too long ago (audit P-4, Linea F-1's priced cure, merge list §4.4): neither fine nor gone. Visibly dimmer than MISSING at a glance, visibly darker than GRANTED — Atkinson Bold 15sp stays legible at 0.60 in both schemes. |
| *(no word)* — UNMEASURED | name only, name in its constant dress | Acquitted (§0). Absence is its own dress; no ink tier applies. |

Rules that ride with the table:

- **The name never changes ink.** Every row's name plate stays
  `PillBadgeTextStyle` (Anonymous Pro Bold 10sp, +2.0 tracking)
  in `onSurfaceVariant` through every re-stamp — GRANTED,
  MISSING, STALE, mid-CHECK, idle decay. The name is the row's
  identity; only the word is its state. A reader tracking one
  row during a CHECK watches one fixed point and one changing
  word, never two moving parts.
- **The word never changes type.** Atkinson Hyperlegible Bold
  15sp, +0.5 tracking, in all three dresses — today, and in
  this patch. Ink alpha is the *only* variable. No size step,
  no weight step (Atkinson ships 400/700 only — there is no
  honest middle weight to fake the tier with), no tracking
  change. Sora appears nowhere on a board row (display is the
  header's and the keys'); Anonymous Pro appears only as the
  name plate and timed prints, as today.
- **No fourth colour enters.** STALE's half-ink is the primary
  ink at alpha — never `TokenAlert`, never the accent
  (`primaryContainer`), never a grey token swapped in (a token
  swap would read as a different ink in night, where
  `onBackground` flips cream and the variant greys do not
  track it). Alpha on `onBackground` is scheme-proof by
  construction: day #2C2A29 and night #D2C6BB both halve
  toward their own ground. Green does not exist in this file.
- **Interceptor words keep their mapping, unchanged by this
  spec's patch:** `RUNNING` quiet, `OFF` full. `PAUSED` is
  treated in F-B5 — separately, because its dress is not yet
  ruled. The SOCIAL SITES word follows the same two dresses
  as today, beside its sled (F-B3).

### F-B2 · STALE takes the half-ink — **PATCH-READY**

The one code defect this spec exists to close: as built
(`ParkDrawer.kt` L401–L413 and L779–L805), both the permission
call site and `ParkBoardRow` collapse STALE into the lack
Boolean, so a stale grant and a measured lack print in
identical full ink — P-4's charge, still live in the cell
tree. The patch replaces the Boolean with a three-tier ink
enum everywhere the board prints a word. Six call sites and
both row composables; no other file is touched.

**(a) Add the enum** (beside `ParkBoardRow`, replacing nothing):

```kotlin
/** The board word's ink tier — form first: the weight of the
 *  ink says which kind of state it is before the word is read.
 *  QUIET = proved/at rest, HALF = granted but stale (audit P-4,
 *  Linea F-1: 0.60 of primary ink), FULL = a measured lack. */
private enum class ParkWordInk { QUIET, HALF, FULL }
```

**(b) `ParkBoardRow`** — signature and word colour (L779–L805).
Replace `wordIsLack: Boolean` with `wordInk: ParkWordInk`, and
the colour expression with:

```kotlin
                    color = when (wordInk) {
                        ParkWordInk.QUIET -> scheme.onSurfaceVariant
                        ParkWordInk.HALF -> scheme.onBackground.copy(alpha = 0.60f)
                        ParkWordInk.FULL -> scheme.onBackground
                    }
```

**(c) The permission rows** (L400–L414) — this also hoists the
print derivation to **one call per row per frame** (today the
word and the Boolean each call `permPrintAt` separately; two
calls can straddle the 45s boundary and tear — word computed
GRANTED, ink computed STALE, for one frame). Word and ink must
be read off the same derivation or the re-stamp lies for a
frame. Replace the whole `forEach` body with:

```kotlin
                    WatchItemId.PERMISSIONS.forEach { item ->
                        val print = WatchStore.permPrintAt(snapshot, item, nowMono)
                        ParkBoardRow(
                            name = item.boardName,
                            word = when (print) {
                                PermPrint.GRANTED -> "GRANTED"
                                PermPrint.MISSING -> "MISSING"
                                PermPrint.STALE -> "STALE"
                                PermPrint.UNMEASURED -> null
                            },
                            wordInk = when (print) {
                                PermPrint.GRANTED,
                                PermPrint.UNMEASURED -> ParkWordInk.QUIET
                                PermPrint.STALE -> ParkWordInk.HALF
                                PermPrint.MISSING -> ParkWordInk.FULL
                            }
                        )
                    }
```

(UNMEASURED's tier is inert — the word is null and nothing
renders — QUIET is named only so the mapping is total.)

**(d) The interceptor rows** (else-branch, L441–L447) — the
existing two-dress mapping, re-expressed in the enum:

```kotlin
                            ParkBoardRow(
                                name = item.boardName,
                                word = word,
                                wordInk = if (m?.interceptor == InterceptorState.RUNNING) {
                                    ParkWordInk.QUIET
                                } else {
                                    ParkWordInk.FULL
                                }
                            )
```

**(e) `ParkSocialSitesRow`** (L826 ff.) — same signature swap
(`wordIsLack: Boolean` → `wordInk: ParkWordInk`), same `when`
colour expression as (b) for its word `Text`, and its two call
sites (measured, L425–L436; never-measured, L455–L466) pass
the same expressions as (d) and (f) respectively. The sled is
untouched: the word re-derives during a CHECK, the sled does
not move — the setting is a persisted fact, the word is a
measurement, and a re-stamp must never make one impersonate
the other (the row's own standing law).

**(f) The never-measured board** (L469) and **the NOT PROVED
line** (L581–L589) — parameter rename only, dress unchanged:

```kotlin
                            ParkBoardRow(name = item.boardName, word = null, wordInk = ParkWordInk.FULL)
```

```kotlin
                        ParkBoardRow(
                            name = "NOT PROVED",
                            word = lackName,
                            wordInk = ParkWordInk.FULL
                        )
```

The never-measured call's tier is inert (null word), as (c).
The NOT PROVED word — a *name*, printed in the word slot —
keeps FULL: it is a measured lack the completed CHECK just
proved, the freshest fact on the board, and half-ink there
would under-say the one verdict the verb exists to print.

### F-B3 · One derivation per row, per frame — **PATCH-READY**

Given in full at F-B2(c) for the permission rows and stated
here as the standing rule it establishes: **a row's word, ink,
and (when §4.4 lands) its age print are all read from a single
`permPrintAt` value captured once per composition.** No call
site may derive the same row twice in one frame. The
interceptor rows already comply (single `snapshot.measurement`
read); the social row's word and sled already read different
facts and are exempt by their own law (F-B2e). Acceptance is
by inspection: after the patch, `permPrintAt` appears exactly
once in the board's permission block.

---

## §2 · HAIRLINE DISCIPLINE — THE RULING IS NOT A STATUS

### F-B4 · The hairline never re-stamps — **SPEC-ONLY**

`ParkHairline()` as built is correct and is not patched: 1dp,
`onBackground` at alpha 0.18, full content width, under every
board row — print ruling, not a depth line, and **not a state
surface**. During and after a CHECK:

- A row's hairline does **not** take its word's ink tier. A
  STALE row's line stays 0.18 — if the ruling halved with the
  word, the board would look *unprinted* under exactly the
  rows that are merely old, and MISSING's full-strength line
  would read as emphasis furniture Hof never ruled. The tier
  lives in the word and nowhere else on the row.
- A hairline never thickens, brightens, appears, or disappears
  as its row lands. The set of hairlines on the board is
  identical before, during, and after any CHECK: nine, in
  fixed positions, plus the NOT PROVED line's own when that
  line is printed (F-B6).
- The family break stays exactly as built: the sixth row's
  hairline, then the 16dp whitespace gap with **no** hairline
  and **no** eyebrow in it (audit §1.5 killed the break only
  as the board's *only* structure; as a device it is
  acquitted, §7 — tiers are §4.7's lane and this spec does
  not pre-build them). A re-stamp may not use the gap: no
  progress print, no "checking" marker, nothing seats there
  mid-flight.
- Pitch is a consequence, not a target to re-derive per row:
  48dp row + its hairline = the acquitted 49dp metronome, for
  every row, in every state, including the wordless
  UNMEASURED rows and the social row with its sled. No state
  in this spec adds or removes a single dp inside a row.

Verification only: builder confirms no `ParkHairline` call
site gains a parameter, an alpha branch, or a conditional as
part of this issue. If a future lane wants a state-bearing
ruling, it files its own charge — it does not grow here.

### F-B5 · PAUSED — flagged, not dressed — **SPEC-ONLY**

P-4 named the conflation in one breath — *"STALE and PAUSED
wear full lack-ink: gone, unproven, and his own Safe Break
pause in one face"* — but its priced cure (§4.4) dresses
STALE only, and the Designer does not rule PAUSED. This spec
therefore does **not** patch it: `PAUSED` keeps FULL ink
under F-B2(d), on the record that the case for HALF is the
same shape as STALE's (a deliberate, self-imposed state is
not a lack) and the decision is **Hof's to rule**, not a
finisher's to smuggle into an enum. Until ruled: FULL stands,
and no builder improvises the change inside this patch. If
Hof rules HALF, it is a one-line change at F-B2(d)'s mapping
— priced here so the ruling, if it comes, costs nothing.

---

## §3 · THE ANIMATION LAW — WHAT MOVES WHEN A WORD CHANGES

### F-B6 · The re-stamp is a print act: atomic, instant, unadorned — **SPEC-ONLY**

The board's rows are print (§8.10's one-object law, acquitted:
everything is print except the keys). A word change during a
CHECK is therefore governed like a reprint, not a transition.
As built, the rows comply — there is no animation on any board
row today — and this item exists to keep it that way against
the obvious temptations a "checking" moment invites.

**A word change MAY:**

- Swap **atomically in the landing frame**: the new word and
  its new ink tier appear together, in the same composition,
  from the same derivation (F-B3). GRANTED→STALE on idle decay
  obeys the same law: word and HALF ink in one frame, on the
  5-second beat's frame, never ink-first or word-first.
- Be accompanied only by the motion already owned elsewhere:
  the CHECK key's own dip and 160ms THROW release, and the
  firm click on a whole-set landing (Designer's §1.5/§1.6 —
  the key's law, not the board's). The sound is the verb's;
  no row gets a sound, ever.

**A word change MAY NOT:**

- **Fade, crossfade, or tween.** No `AnimatedContent`, no
  `animateColorAsState`, no alpha ramp on the word or its
  ink — in either direction, at any duration. A crossfading
  STALE would pass through inks that mean nothing; a tweened
  1.00 → 0.60 would print every lie between FULL and HALF on
  its way. Ink tiers are discrete dresses; the swap is a step.
- **Move.** No slide, no offset, no scale, no baseline shift.
  The word's right edge is flush to the same column before
  and after (the social row's word keeps its sled-relative
  position, F-B2e); a longer word (`GRANTED` → `MISSING`)
  grows leftward from the flush edge, as print does.
- **Pulse, blink, or breathe while CHECKING.** Rows carry no
  in-progress dress at all: a row awaiting its landing keeps
  exactly the word and ink it had before the press — old
  GRANTED stays quiet, old STALE stays HALF, old MISSING
  stays FULL. The CHECKING state lives on the key alone
  (Designer §1.5: *the key alone carries the in-progress
  state; the board alone carries the result*). No board-wide
  dim, no per-row shimmer, no sequential highlight chasing
  the landings down the board.
- **Stagger or wait.** No artificial per-row delay, no
  hold-to-complete-set batching (§0). A landing prints in the
  frame the store emits it. The only clock on the board is
  the existing 5-second staleness beat, and it is a
  derivation clock, not an animation.
- **Pass through blank.** A measured row's word never renders
  null between its old and new prints during a CHECK. The
  store is never cleared at press time (as built — the press
  records `checkPressedMono` and dispatches; the snapshot
  stands until landings replace it item by item). Any future
  change that empties the board to "start fresh" breaks this
  law and P-6's spirit with it: blank reads as a different,
  worse claim than stale.
- **Announce itself.** No per-row accessibility live-region
  chatter as nine rows re-stamp inside a second; the board
  remains print for the screen reader exactly as it is for
  the eye — read on demand, not narrated. (The CHECK key's
  own `contentDescription` is the Designer's §1.3, unchanged.)

**The NOT PROVED line's appearance** obeys the same law: it
prints atomically under the key when the completed CHECK
leaves a lack (as built, L581–L589) — no `AnimatedVisibility`,
no fade, no slide, even though its arrival pushes the
surfaces below it down by its 49dp. A print landing moves
paper; it does not apologise for it. Symmetrically, its
clearing on the next press or on a whole board is instant.

**What legitimately animates in the drawer stays untouched
by this spec:** the sheet's KIN-DRAWER travel (280 SEAT /
160 THROW), the grip's growth, the FIX key's existing
`AnimatedVisibility` and dip, the CHECK key's dip, and the
sled thumbs' own shipped travel when *toggled*. None of them
is a word change, and none may borrow the board's re-stamp
moment to perform.

---

## §4 · ACCEPTANCE, BY EYE (builder + Hof, on paper until Ren compiles)

1. Stale board, PARK open: every STALE row reads visibly
   lighter than every MISSING row and visibly heavier than
   every GRANTED row, at arm's length, day and night — no
   two of the three dresses confusable in either scheme.
2. Press CHECK on that board: rows land one by one, in
   landing order; each landing is a single-frame word+ink
   swap; unlanded rows keep their exact pre-press print
   throughout, STALE rows at HALF the whole time.
3. Watch a fresh board idle past 45s: GRANTED → STALE flips
   word and ink in one frame per row on the slow beat;
   names, hairlines, pitch, and order do not move; MISSING
   rows do not change at all.
4. Complete a CHECK with a lack: NOT PROVED prints in FULL
   under the key, ruled like a board line, in the same frame
   the key releases; a whole-set CHECK prints no new line
   and sounds the one firm click from the key's law.
5. Never-measured board: CHECK pressed, rows stay name-only
   until each first landing; no word ever flickers, blanks,
   or pre-prints on a row that has not landed.
6. Diff check on the patch: `ParkHairline` and the row
   heights untouched; `permPrintAt` once in the permission
   block; no animation API imported or referenced by any
   board row.

— Finisher 1 (board print), issue-9 cell (for Hof)
