# ISSUE #9 — FINISHER 5 (WHOLE SHEET): PARK AS ONE COMPOSED SHEET

*Finisher 5 (sheet), issue-9 cell (for Hof). Ground: `ParkDrawer.kt`
whole (cell tree, branch `agent/issue-9`), `AUDIT-park.md` §1–§4,
`issue-9-plan.md` (the cell's scope + dispositions — Hof's built
form wins wherever the designer draft differs), `issue-9-designer.md`.
Paper only — nobody in this cell compiles.*

*What this pass is: the surfaces issue #9 touched, read in context
as a single sheet — header to plate, in the order a man's eye and
thumb actually travel them. What issue #9 changed on the sheet:
three things only — the CHECK key + its `NOT PROVED` verdict line
(P-1), the re-sweep fired on PARK open (P-1's other half), and the
header band's `GUARD — NOT CHECKED` print (P-6). The plate's
struck clause (P-7) was verified, not changed. Everything else on
the sheet is the old sheet, and §3 below keeps it that way.*

---

## §1 · THE VERTICAL RHYTHM — HEADER TO PLATE, AS CHECK LEAVES IT

Stacked sums from the code's own constants (board pitch 48dp row
+ 1dp hairline = 49dp, acquitted §7; plate lines at 16dp). Type-
intrinsic heights are marked ≈. The scroll column's content top
is 0; the 48dp grab strip sits above it as sheet chrome.

| # | Segment | Height | Cum. | Joint before it |
|---|---|---|---|---|
| 1 | Header block — `GUARD` (Sora 22/26) + `LAST PROVED` row | ≈52dp | ≈52 | — |
| 2 | Board — 6 permission rows | 294dp | ≈360 | 14dp |
| 3 | Board — 3 interceptor rows (family break inside) | 16 + 147dp | ≈523 | 16dp break |
| 4 | Keys zone — state-dependent (§1.1) | 0–187dp | 523–710 | 14dp per key |
| 5 | Sled block — SOUND 53 + SHORTS+REELS 53 + THEME 49 | 155dp | +20 joint | 20dp |
| 6 | Plate approach — spacer, hairline, spacer | 45dp | — | 32 + 12dp |
| 7 | Spec plate — head 32 + body 128 (10 lines × 16) | 160dp | — | — |
| 8 | Bottom padding | **100dp** | foot | — |

Scroll extent, end to end: **≈1,003dp** sealed (no keys) ·
**≈1,065dp** CHECK only · **≈1,127dp** FIX + CHECK ·
**≈1,190dp** FIX + CHECK + verdict line. Before this issue the
keys zone collapsed to 0 or 62dp; the sheet's extent breathed
with the board's health and, when whole, the board fell straight
onto the sleds across one 20dp joint. That collapse is gone.

### 1.1 The keys zone — the new fixed paragraph

- **CHECK alone** (board whole, or never measured): 14 + 48 =
  **62dp**. CHECK is present in every unsealed state, so the zone
  now has a floor: the board always ends in a verb.
- **FIX + CHECK** (a measured lack): 14 + 48 + 14 + 48 =
  **124dp**. FIX leads (it names the lack — the stronger verb),
  CHECK second, one shared grammar: 48dp, radius 8, ink fill,
  Sora Bold 15. Two ink masses, one stack, read as one paragraph
  — not two buttons that happen to be neighbours.
- **+ verdict line** (a manual CHECK landed unwhole): +14 + 49 =
  **187dp** total. The line is a `ParkBoardRow` — name `NOT PROVED`,
  word = first unproved counted door, full-ink lack dress, ruled
  under — so the zone's foot is a board line, not a banner. The
  paragraph closes by returning to print.
- **Sealed:** 0dp. As built (plan §1, overruling the designer
  draft's footprint-preserving print dress), both keys absent and
  the sheet shortens by the whole zone at the seal. Recorded as
  the cell's accepted geometry jump: under the seal the drawer
  is print, and print needs no verb-shaped hole.

Two composition facts about the zone as built, stated plainly:
CHECK wears the ink fill **always** (the plan's grammar), not the
designer draft's print/ink dress ladder — its eye-weight is
constant, in health and in lack alike; what changes with health
is only whether FIX stands above it. And the zone's internal
joints are **14dp**, inherited from FIX's existing gap — see §1.2.

### 1.2 The joints — no module, and correctly untouched here

As-built joint inventory, top to foot: **6** (title→telltale) ·
**14** (header→board, board→FIX, FIX→CHECK, CHECK→verdict) ·
**16** (family break — the board's one ruled joint) · **20**
(keys→sleds) · **32 + 12** (plate approach) · **100** (foot).
The audit's charge stands exactly as filed (§1.13): this set —
{6, 12, 14, 16, 20, 32, 100} — has no module.

This branch changes none of it, on purpose, and the reason is
compositional, not lazy: the audit's module is {8, 16, 24}
(§1.13) and its foot is 24dp (§1.12), and both belong to the
wider pass (§4.11). Normalising only the joints issue #9 touched
— CHECK's 14s to a lawful 16, say — while FIX keeps 14 would
split the keys stack into two rhythms and make the new key look
mis-seated against its sibling. A consistent off-module 14
inside the stack beats a locally lawful 16. The whole joint set
converts in one pass, or it does not convert in this branch.

What the 100dp foot does to the composition until then: at max
scroll the plate's last line comes to rest a full 100dp — two
key-heights — above the sheet's bottom edge. The sheet's final
impression is void, and the plate (furniture, meant to be read
last and rarely) appears to float short of the foot it should
sign. Ruled CUT to 24dp (§1.12); the cut is checklist B-11, and
this branch ships with the void knowingly, not by oversight.

---

## §2 · WHERE THE EYE LANDS — ON OPEN, BEFORE AND AFTER THE SWEEP

Opening PARK now fires the sweep (`MainActivity`'s parkOpen
effect → `runWatchSweep()`), so every open has two frames, not
one: the board he is shown, and the board that lands. The rise
(280 SEAT) and the sweep (~300ms class) run concurrently; the
frames below are the seat instant and the landing instant.

### 2.1 Before the re-sweep lands — the stale frame

1. **The grab strip and leading edge.** A moving sheet captures
   the eye before any print does; the first fixation is motion,
   not content. Rows are inoperable mid-rise (KIN-DRAWER), so
   nothing competes.
2. **`GUARD`, top-left.** Largest type on the sheet (Sora Bold
   22), first static landing. It still prints no count — the
   headline `GUARD {n}/4` is §4.3, wider pass (B-3). The header
   zone's claim is unchanged by this issue.
3. **The board's first full-ink word.** The `LAST PROVED` row
   holds only an old time or a blank slot, so the eye falls
   through it to the right-hand word column — to the first row
   whose word wears full ink (a lack, a stale, a paused). If the
   stale board is whole, every word is variant-grey and the eye
   keeps travelling — correctly: a whole stale board has nothing
   to say until it is re-measured.
4. **CHECK is not the landing.** It sits ≈523dp down the content,
   at or below the fold at the seat instant on either rest
   height. On open it is discovered, not landed on. (The one
   exception is §2.3.)

This frame is honest in a way the old sheet was not: it is
visibly the *previous* measurement — old stamps, possibly stale
words — because a fresh measurement is already in flight and
about to replace it in place. The old sheet showed the same
frame and called it current, indefinitely.

### 2.2 After the re-sweep lands — the print act

Landings are atomic per row: each word re-prints the instant its
own query lands, no fade, no adornment. The eye's second path:

1. **The row that changed.** A word flipping (STALE → GRANTED,
   or a lack appearing) in the column the eye is already reading
   is the landing's first event. Nothing else on the sheet moves
   at that moment — the change owns the fixation.
2. **`LAST PROVED` advances — only on the complete set.** The
   acquitted complete-set rule holds: partial landings print on
   their rows and advance nothing. When the set completes, the
   header-zone numeral swap is the sheet signing its own re-proof.
3. **Down to the keys, if — and only if — the keys zone changes
   shape.** A landing that makes FIX appear or vanish animates
   the zone (expand/shrink, 160ms as built; the 280 SEAT entry
   correction is §4.6, B-6) — the only post-seat motion on the
   sheet, and it pulls the eye down to the verbs. A landing that
   merely renames FIX's lack swaps the word inside the same 48dp
   box: no geometry change, no eye pull. A whole landing is
   quiet by design: numeral swap above, firm click if the counted
   set is 4/4 GRANTED, and otherwise silence — success gets no
   banner because the fresh board *is* the success print.
4. **The verdict line, manual CHECKs only.** An open-sweep landing
   never prints `NOT PROVED` — the line belongs to the man's own
   press (its state clears on close, on the next press, or when
   the board reads whole by any road). When it does print, it
   appears under the key he just pressed, in board grammar, at
   the exact point his eye already rests after the dip releases.
   The unwhole open landing's verdict is the board itself plus
   FIX's appearance — no line, no click.

### 2.3 The cold-open frame — where `NOT CHECKED` does its work

The P-6 print lives off-sheet, on the header band, and its
composition moment is *before* the sheet opens: on a cold start
the band's right side — previously blank, previously read as
fine by a tired man — now carries `GUARD — NOT CHECKED` from the
first frame, in the Guard line's own dress (Anonymous Pro Bold
15, right-aligned, no count, no colour). The eye lands on a
printed ignorance instead of an absence, and the tap that opens
PARK is the natural next move.

Inside the sheet, the twin frame: a never-measured board prints
names only — a left column with no right column at all. The eye
runs the names, finds no words, and arrives at the keys zone,
where CHECK stands as the sheet's **sole ink object and sole
verb**. In this one state CHECK *is* the first true landing —
which is exactly right: on an unmeasured board, re-measuring is
the only honest move the surface can offer.

---

## §3 · SCROLL BEHAVIOUR — WITH BOTH KEYS PRESENT

- **Every open starts at scroll 0.** The sheet leaves composition
  when shut, so its `rememberScrollState()` is born fresh per
  open: header first, always. No open ever resumes mid-sheet —
  he never re-enters staring at the sleds because he left them
  there. The re-sweep's landings therefore always play out in
  the first screenful's grammar (§2), not wherever he left off.
- **The first screenful is now the truth instrument entire.**
  With FIX + CHECK seated, board (≈523) + keys (124) + the 20dp
  joint puts the sled block's first line at ≈667dp of content
  (≈715 from the sheet's top edge). On either rest height that
  is scroll territory: board + verbs own the opening view; the
  sleds, the theme line, and the plate are reached by travelling.
  That ordering is the audit's own hierarchy made spatial —
  state first, its verbs second, controls and furniture after —
  and issue #9 is what makes it true: before CHECK, a whole
  board's first screenful already contained the SOUND sled.
- **Late landings can shift what he is reading — once, bounded.**
  If he has already scrolled to the sleds or plate when a landing
  toggles FIX (±62dp) or a manual verdict prints (+63dp), the
  content above his viewport changes height while the scroll
  offset holds in pixels: what he is reading jumps by that one
  segment, once per landing, and settles. At scroll 0 — the
  normal case, since landings land within ~300ms of the press or
  the open — the same change happens below his fixation and reads
  as the stack growing, not as a jolt. No mitigation is built in
  this branch and none is smuggled: the jump is the price of a
  conditional key above a scroll column, it is bounded at one
  segment, and it cannot repeat without a new press.
- **The verdict line scrolls as a board line, and persists.**
  It is not a toast, a snackbar, or an overlay: it takes its
  49dp in the column, scrolls with the sheet, and is still there
  when he scrolls back — it dies only on the next press, on
  close, or when the board reads whole. A 1 AM reader cannot
  miss it by looking up late.
- **The foot still lies about the end.** The trailing 100dp
  (§1.2) means max-scroll overshoots the plate into pure void:
  the scroll feels unfinished, as if one more segment were
  missing below the furniture. Known, ruled, owned by B-11 —
  recorded here so no one "fixes" it inside this branch and so
  the wider pass inherits the complaint with its address on it.
- **Sealed, the sheet is a shorter scroll of pure print.** No
  keys zone, sleds touch-swallowed, theme and plate unchanged:
  the same column minus its verbs. The scroll extent drops to
  ≈1,003dp and nothing in the remaining travel asks for a hand.

---

## §4 · FINISH CHECKLIST — THE BOUNDARY, NUMBERED

### (a) This merge — issue #9's branch must carry all of these

1. **Re-sweep on PARK open (§4.1, host half).** `MainActivity`'s
   parkOpen effect calls `viewModel.runWatchSweep()` in addition
   to the existing diagnostic; every open path (rail cap, PARK
   re-tap, first-run auto-open) passes through `parkOpen`, so no
   path bypasses the sweep.
2. **The CHECK key (§4.1, board half).** `ParkDrawer` gains
   `onCheckClick` — exactly one host, no default value, so no
   second host can seat the drawer without wiring the verb. Key:
   48dp, radius 8, ink fill, `CHECK` / `CHECKING` in
   PrimaryButtonText, testTag `park_check_key`, seat knock on
   press (10ms, 0.10 day / 0.06 night, 300Hz), 1dp dip held for
   the act. Present in every unsealed state including
   never-measured; absent under the seal.
3. **CHECKING as derivation, never timer.** Press stamp vs every
   PERMISSIONS item's `measuredMono` at/after it; any writer's
   stamp counts. No timeout, no spinner, no percentage on the face.
4. **The verdict line, in its corrected wording.** Whole landing
   → firm click + line cleared. Unwhole landing → silence + a
   persistent `ParkBoardRow(name = "NOT PROVED", word =` first
   unproved COUNTED door`)`, clearing on next press, on close,
   or when the board reads whole by any road. The designer's
   draft wording (`… DID NOT ANSWER`) is **not** carried: every
   door answers; MISSING is an answer; the line names the
   measured lack and claims nothing more.
5. **`GUARD — NOT CHECKED` (§4.5 / P-6).** `GuardLine.kt`: both
   unmeasured paths (null snapshot; any counted item UNMEASURED)
   return the print instead of null — total, unblankable, no
   count ever. `GuardLinePrintTest` pins all four prints
   (NOT CHECKED ×2, 4/4, named lack, named stale lack). The
   board's acquitted name-only unmeasured rows are untouched.
6. **P-7 verification only — no code change.** `PLATE_BODY`
   stands as `ROUTINES: SET BY HAND ON SHIELD`; the struck
   `FIRED AS SET` survives only in the UNA-05 comment and in
   paper. Neither restoration condition is met (no F1–F5 in this
   build, no re-verified live fire), so nothing is earned back.
7. **W-08 inputs prepared, plate NOT built.** The `peekMinutes`
   input contract is documented (the same value the lease reads;
   null → the plate omits the number, 0 → OFF form, 1 → singular)
   for Ren's batch-10 plate. No plate line, layout, or wording
   changes in this branch.
8. **Declared, not compiled.** The cell's state stands as Hof
   left it: code + tests written, uncompiled. Ren compiles on
   Sarthak's word, at the merge — not before, not here.

### (b) The wider audit pass — must NOT be smuggled into this branch

Each item is §4 of `AUDIT-park.md`, owned by the pass that owns
the whole list. If any of these is found in this branch's diff,
it comes out — a half-landed neighbour is worse than an absent
one, because the pass that follows will assume it was never
touched.

9. **§4.2 — Warrant line on the board (P-2 cheap half).** No
   LIVE / OFF / UNPROVEN print joins the board here, however
   close CHECK's landing sits to it.
10. **§4.3 — Headline `GUARD {n}/4` + foot verdict line (P-3).**
    The header stays the bare word `GUARD`; no
    `GUARD ···· {n}/4 · {HH:mm}` line is added above the plate.
11. **§4.4 — Age column + STALE half-ink dress (P-4).** No row
    prints an age; STALE keeps its current full lack-ink. (The
    §2 eye path above describes the sheet *without* the third
    dress — deliberately.)
12. **§4.6 — FIX: escort card, printed FIXED / STILL OPEN
    verdict, Healing Staircase, placement under its family,
    280 SEAT entry (P-5, §1.8).** FIX in this branch keeps its
    place below the board, its silent return (sound half only),
    and its as-built 160ms entry. CHECK was composed to sit
    under it *as it is*; the staircase re-seats both keys and
    belongs to its own pass.
13. **§4.7 — Tier composition (§3).** No tier eyebrows, no
    consequence lines, no OFF / NO DOOR split, no eviction of
    the false citizens from the tier block. The 16dp whitespace
    break remains the board's only internal structure here.
14. **§4.8 — LAST PROVED → foot colophon (§1.3).** The telltale
    stays in the header block, beside `GUARD`, where §1 of this
    sheet measured it. It does not move to the plate in this
    branch — not even "while we're down there."
15. **§4.9 — Plate cut to 4 lines (P-7, §1.11).** The plate
    keeps all 10 lines, COLOUR and `DATA: ON THIS PHONE ONLY`
    included. Item (a)6 verifies one struck clause; it does not
    open the plate.
16. **§4.10 — Sound sled → Shield; theme row → footnote /
    LOOK row (§1.9–10).** Both rows stay in PARK, in their
    current slots, at their current heights — the sled block
    §1 measured is the shipped one.
17. **§4.11 — Padding 100 → 24dp + spacing module {8, 16, 24}
    (§1.12–13).** The void and the joint set ship as-is in this
    branch, per §1.2's reasoning: the conversion is one pass,
    whole-sheet, or it is vandalism.
18. **§4.12 — PARK gate composes `doorLocked`; seal-time
    warning line (P-8).** PARK's gate keeps its current
    composition here; no seal-time print is added to any door.

**Also not this branch, for the record:** the Live-Fire Strip
(§5, parked as issue #21 — CHECK's copy register bars its
language and its promise, and nothing in this sheet pre-sells
it) and the W-08 dynamic plate itself (batch 10, Ren's lane —
item (a)7 prepares its input and nothing more).

---

## §5 · THE SHEET, IN ONE LOOK

Before this issue, PARK was a board that aged in front of the
reader with no verb on it, under a header line that went blank
exactly when it had nothing to report. After it, the sheet reads
top to foot as one instrument: a header that names its ignorance
off-sheet (`NOT CHECKED`) and its last proof on-sheet, a board
that is re-measured the moment it is opened, two verbs in one
grammar seated directly under the board they serve — FIX for the
named lack, CHECK for the whole truth — a verdict that prints
only when print is the only way to say it, and then the controls
and furniture, demoted by scroll distance to their proper rank.
The joints are unmoduled and the foot is void; both are ruled,
priced, and waiting on the pass that owns them. What this branch
owes the merge is a sheet that can re-prove itself on demand —
and nothing it does not owe.

— Finisher 5 (sheet), issue-9 cell (for Hof)
