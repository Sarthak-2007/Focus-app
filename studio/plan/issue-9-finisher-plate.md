# ISSUE #9 — FINISHER 3 (PLATE + TELLTALE): THE FOOT OF PARK

*Finisher 3 (plate), issue-9 cell (for Hof). Ground: `AUDIT-park.md`
§1.3 / §1.11 / §2 P-7 (Hof's rulings stand — nothing here re-opens
them), `issue-9-designer.md` §3 (the plate's print treatment) and
§1.2 (the foot block's neighbours), `TRUTH3-WORDING.md` W-11, and
the cell tree's built `ParkDrawer.kt` (909 lines: plate constants
L108–126, plate render L749–772, LAST PROVED telltale L366–395).
Paper only — nobody compiles.*

*Scope, exactly: the foot of PARK — (1) the spec plate as
furniture: its typography and spacing finished to gallery
standard; (2) the LAST PROVED telltale: its placement at the foot
per audit §1.3, options weighed and one recommended; (3) the
verification census proving the struck sentence "FIRED AS SET"
appears NOWHERE in the app. NOT in scope: restoring the clause
(not this cell's call — §5.4 states the gate and stops), the
§1.11 plate cut itself (§4.9's lane — this spec finishes the
plate so the cut lands cleanly, it does not perform it), the
W-08 dynamic plate (batch 10, Ren's lane — inputs are the
Designer's §4 / Innovator's §8, not duplicated here), the foot
verdict line `GUARD ···· {n}/4 · {HH:mm}` (§4.3 — a neighbour;
this spec composes spacing with it and does not design it).*

*House law applied throughout: Anonymous Pro is the plate's and
the telltale's family (telemetry/engraving); no new sizes off the
ladder (22/15/10); zero green, zero orange on any foot face —
the plate may *name* colours, it never wears them; furniture is
print, never an object: no press, no dip, no sound, no sled.*

*Every item is tagged **SPEC-ONLY** (law / verification, no code
delta) or **PATCH-READY** (exact change, anchored to the built
file).*

---

## §1 · THE PLATE AS BUILT — INVENTORY (the object being finished)

Two constants, one render site, nowhere else in the app
(verified §5: `PLATE_HEAD` / `PLATE_BODY` are defined once in
`ParkDrawer.kt` and consumed once, L754/L763):

**PLATE_HEAD** (L108–111) — primary ink (`onBackground`):

```
MONOLITH ML-01
UNIT Nº 01 · BUILT FOR ONE USER · NOT FOR SALE
```

**PLATE_BODY** (L112–126) — secondary ink (`onSurfaceVariant`):

```
WHEEL SETS TIME · SLIDE ARMS · THE HOLD BREAKS
HOLD: 60 SECONDS, BANKED EVERY 15 · ONE BREAK A WEEK
PEEK: 2 MINUTES, THEN SHUT AGAIN
COLOUR IS A VERB: ORANGE = COMMIT · INK = SHUT · GREY = PEEK · PORCELAIN = OPEN
ROUTINES: SET BY HAND ON SHIELD
SITES: KEPT WITH APPS ON SHIELD · DATA: ON THIS PHONE ONLY
STUDY HOURS: IN YPT
SOUND: FLOAT-PCM, 4 PARTIALS · POWER: YOUR PHONE
```

The routines line (BODY line 5, code L123) already prints the
struck form — the weaker true sentence, full stop, no clause.
The UNA-05 comment sits directly above it in the constant
(L117–122): it documents the strike, the falsification
(Oct 29, 20:00 SLEEP no-fire), and the return gate. That comment
is the plate's provenance record and is **kept verbatim** —
§5 judges it, nothing in this finish touches it.

Render (L749–772): the plate is the sheet's last element —
preceding section's hairline, `Spacer(32)`, `ParkHairline()`,
`Spacer(12)`, HEAD `Text`, BODY `Text`, then the column's
`bottom = 100.dp` padding (L364) below it. Both `Text`s carry
identical inline type (see §2); the two blocks are joined with
`"\n"`, so head and body read as one continuous 10-line block.

---

## §2 · PLATE TYPOGRAPHY — THE FINISH

### F-P1 · The plate's type is locked as built — **SPEC-ONLY**

| Property | Value (both HEAD and BODY, as built L754–772) |
|---|---|
| Family | `AnonymousProFamily` — the engraved-plate family |
| Weight | `FontWeight.Normal` (400 — the family's honest regular) |
| Size / line height | **10sp / 16sp** (1.6 rhythm — the loosest line pitch in the drawer; furniture breathes) |
| Letter spacing | **1.2sp** |
| Alignment | left, flush with the board names' left edge (sheet padding 20dp is the only inset) |
| Ink | HEAD `onBackground`; BODY `onSurfaceVariant` — two inks, no third |

Rules that ride with the table:

- **Do not "correct" the plate onto `PillBadgeTextStyle`.**
  That style's comment calls it "the engraved plate" tier, but
  its values are Bold 10sp / 12sp line height / 2.0sp tracking —
  a label's dress, not this furniture's. The plate's inline
  values above are canonical *for the plate*: swapping in the
  badge style would bolden every line, tighten the pitch from
  16 to 12, and change the object's whole temperature. If a
  future cleanup wants the plate's type named, it extracts
  these exact values into a `PlateTextStyle`; it never
  substitutes an existing style. (The board's name plates keep
  `PillBadgeTextStyle` — that is their dress, not the plate's.)
- **Byte-identical day and night** (the file's own law for the
  plate): the *text* never branches on theme, time, seal, or
  state. Only the two ink tokens follow the scheme, exactly as
  every other print surface does. A plate line that needs a
  live value is not a plate line — it is the W-08 lane's
  question, and until that lane lands the constants stay
  constants (the PEEK line's hard-coded `2` is lawful today
  only because the lease still reads the same constant,
  `PEEK_DURATION_SECONDS = 120` — Designer §4; the day those
  diverge, the line is W-08's to fix, not a finisher's patch).
- **No emphasis exists on the plate.** No bold line, no primary
  ink below the head, no larger first line. `MONOLITH ML-01`
  leads by position and ink alone. The struck routines line
  wears the identical dress as its seven body neighbours —
  striking changed its words, never its volume (Designer §3:
  the clause leaves no trace on the face — no gap, no marker,
  no asterisk, and equally no hush).

### F-P2 · Wrapping — furniture never shrinks — **SPEC-ONLY**

The COLOUR line (BODY line 4) is the plate's longest and wraps
mid-clause at ordinary widths (Linea F-7, audit §1.11). The
finish law, until the §1.11 cut removes that line on its own
lane:

- A wrap is a defect of the *line list*, never of the type.
  The cures that are forbidden, by name: reducing the size,
  compressing the 1.2sp tracking, hyphenating, abbreviating a
  word, or letting the plate's block narrow the sheet padding.
  Furniture is engraved at one size or it is not furniture.
- The lawful cure is the already-ruled one: §1.11 cuts the
  plate to the two head lines + HOLD + PEEK. This spec does
  not perform that cut (§4.9 owns it), but §4 below fixes the
  plate's spacing so the cut is a pure deletion of list entries
  — no layout constant in §3 references a line count.
- Acceptance measure for the post-cut plate (for §4.9's lane,
  stated here so it is not re-derived): at the narrowest
  content width the sheet supports, each surviving line is
  checked for a single-line seat. Any line that still wraps
  is flagged to Hof by name — it is never squeezed into fit
  inside the plate patch.

---

## §3 · PLATE SPACING — THE FOOT'S RHYTHM

### F-P3 · The joints — **PATCH-READY**

As built, the foot is the drawer's worst rhythm: the joints
around the plate are **32 / 12**, and the void below it is
**100** — none of them in the audit's spacing module
{8, 16, 24} (§1.13), and the 100dp is ruled pure void (§1.12).
The finished foot, top to bottom (stated relative to "the last
ruled line above the foot" so it survives §4.10's moves of the
Sound sled and Theme row — whatever sits above, the foot's own
joints do not change):

```
last ruled line above the foot (hairline)
  24dp
[foot verdict line — §4.3's line, when that lane lands]
   8dp
[LAST PROVED colophon — §4 of this spec, Option A]
  16dp
plate hairline (ParkHairline, 1dp @ 0.18 — unchanged)
   8dp
PLATE_HEAD (2 lines @ 16sp pitch)
PLATE_BODY (lines @ 16sp pitch — no gap from HEAD; one block)
  24dp   ← sheet bottom padding (§1.12: 100 → 24)
```

Exact edits to the built file:

1. Plate render block: `Spacer(Modifier.height(32.dp))` →
   **24.dp** — but only once the verdict line and colophon
   (§4) are seated above the plate hairline does the full
   stack above apply. In the tree as it stands today (no
   verdict line, telltale still at the header), this spacer
   is the joint between the Theme hairline and the plate
   hairline and takes **24.dp** directly.
2. `Spacer(Modifier.height(12.dp))` under the plate hairline →
   **8.dp**. The hairline-to-text joint tightens to the
   module's smallest step: a ruled furniture block sits close
   under its rule, the way the board's rows sit on theirs.
3. Sheet column padding (L364): `bottom = 100.dp` → **24.dp**
   (audit §1.12, verbatim). The plate becomes the true foot:
   the last thing printed, one module-step above the sheet's
   edge — not a text floating in a 100dp cellar.
4. No gap is inserted between the HEAD and BODY `Text`s, and
   none is removed: the 16sp line height already carries the
   whole block's pitch (10 lines × 16sp = 160sp of furniture).
   HEAD/BODY remain two `Text` calls solely for the ink split.

Interim-state note (so no builder has to guess): edits 1–3 are
lawful **today**, before §4.3/§1.3 land — they change only
off-module numbers, and the telltale move in §4 slots into the
stack above without re-touching them (its 8 + 16 joints are
its own spacers, seated with it).

### F-P4 · The plate's hairline — **SPEC-ONLY**

`ParkHairline()` as built (1dp, `onBackground` @ 0.18) is the
plate's only frame and stays unparameterised (Finisher 1's
F-B4 law applies at the foot too: the ruling is not a status
surface). The double ruling visible today — the Theme row's
trailing hairline, a void, then the plate's hairline — is not
charged: with §3's 24dp joint it reads as the chapter break
before the furniture, which is what a spec plate is. Neither
hairline is deleted, brightened, or doubled further.

---

## §4 · THE TELLTALE AT THE FOOT — PLACEMENT

### The object being placed — **SPEC-ONLY** facts

- **What it is:** `LAST PROVED {HH:mm} · SELF-REPORT` — the
  wall instant (`WatchSnapshot.lastProvedWallMillis`,
  `ParkDrawer.kt` L381) of the last *complete* six-permission
  set. It is not a four-count headline: the counted four are a
  subset of its six (audit §1.3, Fremd F-03). The complete-set
  rule is acquitted law — partial landings, CHECK in flight
  included, never move it (Finisher 2 §5.3: the CHECK key's
  release and this instant's advance are one event, two seats).
- **`· SELF-REPORT` stays, verbatim** (Fremd's acquittal beats
  the strike proposals on the word itself; audit §1.3). The
  mislabel the audit charged was *placement*, not wording —
  a six-set instant wearing headline dress above the four.
  The cure is demotion, not a rename. The one glossary line
  the audit assigns the qualifier lives where the reception
  reprints it — **not** in PARK; no glossary is added at the
  foot. (In this tree PARK is the telltale's only print site:
  `lastProvedWallMillis` is consumed at exactly one composable,
  L381 ff.)
- **As built, at the header (L366–395):** `GUARD` title
  (Sora Bold 22), 6dp below it a row — label in
  `PillBadgeTextStyle` (Anonymous Pro Bold 10sp, secondary
  ink) + 10dp gap + the instant in Anonymous Pro **Bold 15sp,
  primary ink**. A 15sp bold instant directly under the title
  is the board's loudest timed print — headline volume for a
  colophon fact. And while `proved == null` the label prints
  alone over a blank slot: a caption with nothing captioned.

### The options

**Option A — Stacked colophon above the plate (RECOMMENDED).**
Its own line in the foot stack (§3): below the foot verdict
line (8dp), above the plate hairline (16dp), left-flush with
the plate. The telltale becomes the plate's signing line —
the board's evidence, filed with the furniture it belongs to,
read last because it is *about* everything above it. The
plate block and the telltale share family, pitch, and left
edge, so the foot reads as one engraved unit: verdict,
signature, plate.

**Option B — A dynamic last line inside PLATE_BODY.**
Rejected. The plate is byte-static furniture (§8.10; §2
F-P1 above) — Teen's verbatim text, identical in every state.
A wall-clock line inside the constant would corrupt both
objects: the furniture gains a state, the instant gains a
false permanence (it would sit in the list beside claims
with no freshness at all). If a dynamic plate ever prints
this instant, that is W-08's batch-10 design to make — not a
finisher's insertion into the static list.

**Option C — Right-hand companion on the foot verdict line.**
`GUARD ···· {n}/4 · {HH:mm}` left, the telltale right, one
baseline. Rejected as the primary seat (kept as the fallback
if a future sheet-height cut ever prices the foot by the
line): the verdict line's `{HH:mm}` is the board signing its
*current* print (§4.3), the telltale's is the last *complete
set's* instant — two different times that can differ, printed
on one line in one dress, invite exactly the misread §1.3
exists to kill (the six-set instant mistaken for the four's
headline fact). It would also couple this move to §4.3's
unbuilt lane; Option A moves independently, today.

**Option D — Stay at the header, demoted in dress only.**
Rejected outright: audit §1.3 rules MOVED, and §1.2 seats
`GUARD {n}/4` as the header's amended headline. A demoted
telltale under that headline still sits as the four's
eyebrow and still prints its dangling label before the first
proof. Dress cannot cure a placement the audit struck.

### F-P5 · Option A, specified — **PATCH-READY**

Delete the telltale row from the header block (L374–395's
`Spacer(6)` + `Row` — the `GUARD` title itself stays, §1.2's
amendment is its own lane) and seat the colophon in the foot
stack of §3, immediately above the plate hairline:

- **Presence:** the whole line renders **only when
  `proved != null`**. Before the first complete set there is
  no colophon — no label alone, no dash, no `— NOT YET`
  placeholder (a placeholder would print a claim about proof
  in the place reserved for proof). Its spacers live inside
  the same condition, so the unproved foot is plate-only and
  geometry-clean; the line's first appearance adds its
  16sp + joints at the scroll's end, where growth costs
  nothing (the foot is reached by scrolling, never watched
  while it changes — contrast the header, where the blank
  slot sat in the first viewport).
- **Form:** the built row's grammar, re-dressed to furniture
  scale. Label `Text` and instant `Text` in one `Row`,
  `Alignment.CenterVertically`, gap **8dp** (built: 10dp —
  off-module):
  - Label: `"LAST PROVED"` — Anonymous Pro **Regular** 10sp,
    line height 16sp, tracking 1.2sp (the plate's own values,
    §2), `onSurfaceVariant`. At the foot the label is
    furniture text, not a badge — `PillBadgeTextStyle` does
    not travel down with it.
  - Instant: `"{HH:mm} · SELF-REPORT"` — format string and
    `SimpleDateFormat("HH:mm", Locale.US)` **unchanged** —
    Anonymous Pro **Bold** 10sp, line height 16sp, tracking
    1.2sp, `onBackground`. Bold at 10sp keeps the instant the
    darkest thing on its line (findable at a glance) without
    headline volume: the demotion is 15sp → 10sp and a move
    of ~the whole sheet, not a silencing.
  - `testTag`: add `"park_last_proved"` on the row (none
    exists today; the move is the moment to name it, matching
    `park_fix_key` / `park_check_key` grammar).
- **Behaviour:** pure print. No press, no dip, no sound, in
  every state — including **sealed**, where it remains
  visible (it is history, not a control; the sealed drawer is
  read-only, and this line is exactly what reading is for).
  It re-prints atomically when the complete set lands, in the
  same frame the CHECK key releases (Finisher 2 §5.3 parity)
  — no fade, no slide (Finisher 1's F-B6 print-act law covers
  the foot: a signature changing is a reprint).
- **What the header keeps:** nothing of the telltale. After
  the move, the instant lives at the foot and **nowhere else**
  in the drawer — the built header comment's law ("the instant
  lives HERE and nowhere else") travels with it, re-pointed:
  the comment moves to the colophon site and the header block
  comment is struck in the same patch. Two print sites for one
  instant is the one outcome worse than either seat alone.

---

## §5 · THE STRUCK SENTENCE — VERIFICATION CENSUS

**Claim under proof:** the sentence `FIRED AS SET` — in the
plate's full form `ROUTINES: SET BY HAND ON SHIELD · FIRED AS
SET` — appears **NOWHERE in the app**: not rendered, not in a
string resource, not assembled at runtime. Struck under Hale
W-11 after a live falsification (Oct 29, 20:00 SLEEP routine
did not fire; UNA-05, `AUDIT-park-claims.md`). It may return
only when Wache's F1–F5 are in the same build **AND** a live
fire is re-verified (§5.4) — neither condition holds in this
tree, and ruling on them is not this cell's call.

### §5.1 Method (re-runnable, in order)

Scope: the whole repo working tree, branch `agent/issue-9`
(`.git` excluded; no `app/build/` exists in this tree, so no
generated artefact can hide a copy).

```bash
# 1. Exact phrase, case-sensitive and -insensitive (identical results)
grep -rn  "FIRED AS SET" . --exclude-dir=.git
grep -rni "fired as set" . --exclude-dir=.git

# 2. Whitespace-normalised sweep — catches the phrase split
#    across a line break, which (1) cannot see
#    (regex FIRED\s+AS\s+SET over every UTF-8 file; found §5.3-J)

# 3. Evasion passes — fragments and semantic neighbours
grep -rn "SET BY HAND" . --exclude-dir=.git   # every routines line
grep -rn "FIRED" app/src/main/                # every main-source token
grep -rn "MONOLITH ML-01\|PLATE_BODY" app/src/ # second plate site?
# + full read of res/values/strings.xml (the only string table)
```

### §5.2 Census — every hit, judged

**Totals: 26 occurrences — app main source 1, app tests 2,
paper (`studio/`) 23. Rendered-string occurrences: 0.**

**A. The app (`app/src/main/`) — 1 hit, NOT printed:**

| # | Site | Text | Judgement |
|---|---|---|---|
| A1 | `ParkDrawer.kt:118` (comment inside PLATE_BODY's provenance block, L117–122) | `// the "FIRED AS SET" clause is STRUCK in this lane. It was falsified on his phone…` | **LAWFUL — the strike's own record.** A Kotlin comment above the routines line; it compiles to nothing and renders nowhere. It is the reason the next reader does not "restore" the clause, and W-11's gate is written in it. Keep verbatim. Deleting it to make a grep read zero would destroy the proof's provenance — the verdict below is about the *app's faces*, not about erasing the paper trail inside the code. |

The only rendered routines string in the app is the struck
form itself: `ParkDrawer.kt:123` —
`"ROUTINES: SET BY HAND ON SHIELD"` — and §5.1 pass 3 confirms
no second plate, no resource copy, and no concatenated or
split assembly of the clause exists (`PLATE_HEAD`/`PLATE_BODY`
have exactly one definition and one render site).

**B. Non-phrase `FIRED` tokens in main source — judged, cleared:**

| Site | What it is | Judgement |
|---|---|---|
| `MonolithViewModel.kt:2481, 2483, 2488, 2510` | `SledReturnGate.Stage.FIRED` — an internal enum stage of the sled-return state machine | **Not the sentence, not the claim.** A code identifier for a different mechanism (a return gate firing), never printed, never about routines. No action. |
| `BlockitCoreViews.kt:1616` | Comment naming "the gate's FIRED state" | Same mechanism as above, in prose. Not a user-facing claim. No action. |

**C. Tests (`app/src/test/`) — 2 hits, both are the guard:**

| # | Site | Judgement |
|---|---|---|
| C1 | `TruthCar3SweepTest.kt:325` (comment) | Documents UNA-05 at the test that enforces it. Lawful; keep. |
| C2 | `TruthCar3SweepTest.kt:353` | `onNodeWithText("FIRED AS SET", substring = true).assertDoesNotExist()` inside `una05_plateDoesNotClaimFiredAsSet`, paired with `assertExists()` on the struck routines line (L351). **This is the standing tripwire** — the sentence's only lawful appearance in test code is inside a non-existence assertion. (Declared, not run — nobody in this cell compiles.) |

**D. Paper (`studio/plan/`) — 23 hits, none reaches the app.**
Paper quotes the sentence to rule on it, remember it, or
archive what the plate once said. None is compiled, shipped,
or rendered. Judged by kind:

| Kind | Sites | Judgement |
|---|---|---|
| D1 · The ruling itself | `TRUTH3-WORDING.md:127` (W-11; phrase split across a line break — found only by sweep 2) | **The authority for the strike.** Must keep the full old line quoted; a ruling that cannot name what it struck cannot be audited. |
| D2 · Audit trail quoting to strike | `AUDIT-park.md:64, 125, 126` (P-7); `AUDIT-park-claims.md:127` (UNA-05); `plan-audit-parts/una.md:228, 230`; `TRUTH3-MERGE-MAP.md:335, 472`; `TRUTH3-PROOF-STANDARD.md:160`; `WORK-QUEUE.md:491`; `OPEN-LOOPS-MAHNER-ESCALATIONS.md:483` | **Lawful record.** Each quote is inside a strike, a gate, or a restore-condition. Keep. |
| D3 · Pre-strike verbatim archives | `MASTER-PLAN.md:4677`; `CHROME-BUILD-READY.md:658`; `designers-parts/teen.md:411`; `seat-drafts/s8-stoff.md:223`; `seat-drafts/s8-vau.md:426`; `REFINE-salz-aod.md:216` | **Historical paper — the plate as originally specced.** These are dated design records, not app source; editing history to match the strike would falsify the archive. Keep, unedited. Hof/Ren may annotate elsewhere; the finisher does not rewrite them. |
| D4 · This cell's own specs | `issue-9-designer.md:11, 280, 298` (§3: struck treatment + the gated restoration form); `issue-9-innovator.md:391` (W-08 input row 4: STRUCK + gate); `issue-9-plan.md:49` (cell plan §3 heading) | **Lawful and necessary.** Each quote names the gate (F1–F5 in the same build AND live fire re-verified) in the same breath. The designer's L298 quote is the *pre-designed restoration print* (Designer §3) — a conditional future form, fenced by both conditions. Keep. |

### §5.3 Verdict

> **PROVED — for the app.** In the shipped source the struck
> sentence survives only as (i) one comment recording its own
> striking, and (ii) one test asserting its non-existence.
> No face, plate, resource, or runtime assembly prints it.
> The plate's routines line is the weaker true sentence and
> nothing else in the drawer implies the struck claim
> (Designer's §3 bar: not the foot verdict line, not CHECK's
> vocabulary — both checked against their specs in this cell).

### §5.4 The restoration gate (stated, not opened)

Restoration is a print act, pre-designed by the Designer (§3):
the line returns in exactly its old form, same line, same
dress, no badge or announcement — **only** when (1) Wache's
F1–F5 are in the *same build* as the plate, AND (2) a live
routine fire has been re-verified on the phone. Both, or the
struck form stands. This spec adds one duty to that day:
whoever restores it re-runs §5.1 and files the new census —
the clause's return should be as documented as its striking.

### §5.5 Checklist (for the builder / Hof — tick in order)

- [ ] Re-run §5.1 passes 1–3 on the final tree; totals match
      §5.2 (26 / 1 / 2 / 23) or every delta is a new §5.2 row,
      judged in writing, before the branch is called done.
- [ ] Read `PLATE_BODY` (L112–126) once, whole: routines line
      is exactly `ROUTINES: SET BY HAND ON SHIELD`; UNA-05
      comment intact above it.
- [ ] `strings.xml` and `res/` re-grepped at the same pass —
      still zero (the plate is code-side only; keep it so).
- [ ] Confirm `una05_plateDoesNotClaimFiredAsSet`
      (TruthCar3SweepTest) still pairs `assertExists` (struck
      line) with `assertDoesNotExist` (clause) — both halves,
      never one without the other.
- [ ] After §4's telltale move and §3's spacing land, re-grep
      `ParkDrawer.kt` for the phrase: exactly one hit (A1, the
      comment) — the foot patches must neither duplicate the
      plate nor carry the clause in a new comment.
- [ ] No D3 archive file edited by anyone in this cell.

---

## §6 · ACCEPTANCE, BY EYE (builder + Hof, on paper until Ren compiles)

1. Scroll PARK to the foot: the plate sits 24dp above the
   sheet's edge, under its hairline at an 8dp joint, in one
   unbroken 10-line mono block — head bright, body quiet,
   nothing in the foot wearing orange, green, or a third ink.
2. The routines line reads `ROUTINES: SET BY HAND ON SHIELD`
   — full stop. No clause, no mark where it was, no change of
   dress from its neighbours.
3. Before any complete set: the foot is plate-only — no
   dangling LAST PROVED label anywhere on the sheet, header
   or foot.
4. Land a complete set (CHECK to completion): the colophon
   appears at the foot — `LAST PROVED {HH:mm} · SELF-REPORT`
   at 10sp, label quiet, instant bold — in the same frame the
   CHECK key releases; the header shows no instant at all.
5. Partial landings move nothing at the foot; the colophon
   keeps the previous complete set's time, untouched, until
   the next complete set replaces it atomically.
6. Seal a session: the foot reads identically — colophon and
   plate as pure print; unseal, and nothing at the foot has
   moved, resized, or re-dressed.
7. Day ↔ night: the plate's words are byte-identical; only
   its two inks follow the scheme.

— Finisher 3 (plate), issue-9 cell (for Hof)
