# ISSUE #10 — TAUMEL, CRAZY-IDEAS: THE SLIP AS A KEPT OBJECT

*Seat: Taumel, crazy-ideas (dedicated-100 programme). Ground: AUDIT-receipt.md
(Zettel's ruling — inventory, Groups A/B/C, binding acquittals), the cell's
wave-2 report. Everything below is BEYOND the audit: the Tear (B1), the Slip
Book (B2) and the Sentence (B7) are already proposed — these are the next
ring out. Each idea concrete, priced, and inside the laws: no green, no
AI images, type-and-rule graphics only, deflections listed never summed,
the slip prints only what the machine can back. Blocker, not tracker —
where an idea walks near that line, the guard is written into the idea.
Paper only — nothing compiled.*

---

## T-1 · THE SLIP PRINTS ITS OWN SHELF ADDRESS

**Idea.** The footer's last line gains the slip's filing address, printed
like a library stamp: `FILED — DRAWER › OCT 2026 › № 041`. If the Slip
Book (B2) lands, the address is literally true: that section, that month,
that №. If it doesn't land yet, the line waits — the slip never prints an
address that doesn't exist (omission law).

**Why crazy / why true.** A kept object should say where it is kept. The
№ stops being a number-to-nowhere (Neu's charge) and becomes a *place* —
he can hold the paper and know the shelf it sleeps on.

**Build.** One footer line from data the slip already carries (№, end
date) + the Drawer section key. **Price: S, sequenced after B2.**

## T-2 · CARBON COPIES — A SHARED SLIP IS ALWAYS A DUPLICATE

**Idea.** The original slip exists once, in the app, in full ink. Any
export/share/long-press-copy renders a **DUPLICATE**: same body, but the
head gains `DUPLICATE — PRINTED {HH:mm}` and the whole copy prints in the
palette's violet (#493EA4) at reduced ink — carbon-paper dress. Copies of
copies print `DUPLICATE №2`, counted. The original can never leave; only
its carbons travel.

**Why crazy / why true.** Real receipt culture already knows an original
is an original. This kills the screenshot-of-a-slip grey zone, gives
sharing a ritual instead of a leak, and the violet carbon dress is
unmistakable across a room: *that's not the slip, that's a copy of it.*

**Build.** A render flag on the receipt composable (ink token swap + head
line + copy counter persisted per № — one small field). Share intent
exports the duplicate render only. **Price: M. No schema change beyond
the counter.**

## T-3 · THE SLIP AGES IN THE BOOK

**Idea.** Kept slips *age on paper*: in the Slip Book, a slip's stock
tints by its age — crisp white today, a deterministic warm fade at a
month, deeper at a season (palette browns/ambers from #E47E35 family at
low alpha, computed from age, rendered as a plain colour wash — no image
assets). Two slips side by side, you can see which winter each is from
before reading a digit.

**Why crazy / why true.** Digital archives are ageless, which is why
nothing in them feels kept. Paper yellows; that yellowing *is* the proof
of keeping. Age-as-tint makes the Book feel like a drawer of real slips
and gives old sessions dignity instead of staleness.

**Build.** Tint = `f(now − endWallMillis)` bucketed (0d / 7d / 30d / 90d+),
one background colour per bucket in the Book's item renderer. The live
slip (today's) never tints. **Price: S. Book-dependent (B2).**

## T-4 · THE MARGIN IS HIS — ONE LINE IN HIS OWN WORDS

**Idea.** Every slip carries one blank ruled line at the foot, headed
`IN YOUR WORDS —`. Tapping it (on the kept copy, in the Book) opens the
keyboard; he may write ONE line, ~48 characters, saved onto that slip
only, thereafter printed in Atkinson italic — the one place on the paper
that isn't the machine's mono voice. The live slip's line stays blank;
only the kept copy can be written on, like signing a receipt after the
shop has printed it.

**Why crazy / why true.** The audit protects the machine's testimony
fiercely — and that's exactly why one human line matters: it's the only
ink on the slip the machine didn't make. A month later, the deflections
are data; *"couldn't sleep, read physics instead"* is the session.
Blocker-not-tracker holds: the line is his handwriting-slot, never
parsed, never scored, never summed.

**Build.** One nullable text column on the receipt row (DB v12 rides
with B1/B4's schema work), one-line input with hard cap, italic render
branch. **Price: M (+schema, batch with B-group).**

## T-5 · PINK SLIPS — FAULTS PRINT ON DIFFERENT STOCK

**Idea.** A faulted slip (audit item 14) prints on *pink stock*: the
whole paper takes the palette pink (#C84481) at low alpha instead of the
standard stock. In the Book, fault slips are findable by colour alone,
spine-out. The head still prints `FOR: <intention>` per B3 — the pink
says *the machine failed*, the words say *what it failed at*.

**Why crazy / why true.** "Getting a pink slip" is already the culture's
phrase for a failure notice — here it's literal, and it's the *machine*
receiving it. Stock colour as a data channel: zero extra words, instant
sorting, and the faulted sheet stops looking like a rendering crash
(Neu's Q5) because crashed renders don't choose their paper.

**Build.** Background token branch on the fault flag the sheet already
receives. **Price: S. Pairs with B3/B4.**

## T-6 · THE SLIP HAS A BACK

**Idea.** The kept slip can be *turned over*: a two-finger twist (or the
Book's flip key) rotates the paper to its back — blank stock carrying
only the spec plate (HOLD / PEEK lines, audit §1.11's surviving four),
his margin line (T-4) if written, and centred, small: `THIS SIDE KEPT
BLANK BY THE MACHINE`. Nothing else ever prints there.

**Why crazy / why true.** Cards have fronts; *paper has two sides*, and
every digital imitation forgets it. The back is where kept objects hide
their private side — his one line lives there, not under the machine's
testimony. It also gives the spec plate a home that isn't a screen.

**Build.** Book-side flip animation (rotateY on the slip composable,
two render faces); live slip never flips (it hasn't been kept yet).
**Price: M. Book-dependent.**

## T-7 · THE ADDING-MACHINE TAPE — THE BOOK AS ONE LONG TAPE

**Idea.** The Slip Book gets a second view: **THE TAPE**. All kept slips
render as one continuous roll — no cards, no gaps, perforation joins
between sessions, and down the right margin a running column adds each
slip's `RAN` as the tape descends, exactly like an adding machine's
margin column. Scrolling the Book becomes unrolling the month.

**The guard (blocker, not tracker — written into the mechanism).** The
margin column exists ONLY inside this view, is never totalled into any
score, grade, streak or Deck number, and the tape's foot prints the
receipt footer's own law: `THE MACHINE KEEPS ITS OWN LOG`. It's a kept
object's shape, not an analytics surface — the Drawer precedent (kept,
quiet, unjudged) covers it.

**Why crazy / why true.** This is the difference between a folder of
receipts and *the roll from the till*. One continuous tape is the most
receipt-true way a set of receipts can exist, and the margin column is
what every real tape does — refusing it would be imitating paper with
its hands tied.

**Build.** Alternate Book layout: single lazy column, slips rendered
borderless with perforation separators, margin figure per slip + a
cumulative print computed at render (no stored totals — derived, like
everything honest in this app). **Price: M. Book-dependent, Ren rules
the guard.**

## T-8 · THE SLIP POINTS FORWARD — ONE LINE FOR TOMORROW

**Idea.** If a routine window follows this session, the foot gains one
line above the footer law: `NEXT — SLEEP 20:00` (name + time only; no
span, no promise). Hand-armed one-off sessions print nothing — there is
no next, and the slip doesn't invent one.

**Why crazy / why true.** Receipts from real machines end with *"thank
you, visit again"* — a pointer at the future. Ours can't thank (praise
vocabulary is banned by B7's law and the house voice), so it does the
colder, truer thing: it names the next appointment the machine already
knows. The slip becomes a hand-off, not just a record.

**Build.** Next-window lookup at assembly (routine store already
answers it); print branch only when non-null. **Price: S.**

## T-9 · THE FINGERPRINT STRIP — EVERY SLIP'S BARS ARE ITS OWN

**Idea.** Above the footer, the slip prints a strip of vertical bars —
widths derived deterministically from the slip's own digits (№, ARMED,
ENDED, RAN seconds) by a fixed digit→width table, drawn with Compose
rects. It scans as nothing; no two slips' strips match; it is the
session's fingerprint in the exact visual language of a till receipt.

**Why crazy / why true.** Real receipts carry a barcode nobody reads —
it just certifies *this one, not another*. A deterministic strip gives
every slip a visible identity stronger than its №, costs no assets,
no images, and — the honest part — it claims nothing: it encodes the
printed figures and nothing hidden, so it can never lie beyond them.

**Build.** Pure composable: digit string → width pattern → rect row,
~30 lines, unit-testable against a golden slip. **Price: S.**

## T-10 · FOLD FOR ROUTINE, TEAR FOR HAND — TWO WAYS A SLIP CLOSES

**Idea.** The Tear (B1) is ruled for closing a slip. Add its sibling for
*routine* finishes only: the **Fold** — dismissal folds the slip in
thirds, two crease lines striking as it folds (top third down, bottom
third up, like a letter for an envelope), and the folded slip is what's
filed. Hand-armed slips tear; routine slips fold. In the Book, folded
slips show their creases; torn slips show their teeth.

**Why crazy / why true.** Two kinds of session end two kinds of way in
real paper life — the till receipt you tear off, the letter you fold
away. The distinction costs the user nothing to learn (the gesture is
shown by the paper's own perforation vs crease marks) and doubles the
kept objects' texture: a Book of mixed tears and folds reads like a
real drawer.

**Build.** Second dismissal animation path + a `closedBy` stamp (rides
B1's v12 torn-stamp work as an enum: TORN / FOLDED). Book renderer draws
crease rules for FOLDED. **Price: M, sequenced with B1.**

## T-11 · THE FIRST SLIP OF THE MONTH IS THE COVER

**Idea.** In the Book, each month opens with its first slip rendered
full-bleed as the month's *cover sheet* — slightly larger, its head
carrying the month name in Sora (the one display-type moment in the
whole receipt world) above the usual mono. The rest of the month files
behind it at standard size.

**Why crazy / why true.** Archives have covers; piles don't. One
ceremonial slip per month turns scrolling the Book into opening volumes
— and it spends the app's display typeface exactly where ceremony
belongs: at thresholds, never in testimony.

**Build.** Book section header = first item of month rendered with the
cover treatment (layout branch, no new data). **Price: S. Book-
dependent.**

---

*Filed as paper. Sequence note for the cell: S-priced loners (T-5, T-8,
T-9) can ride any batch; T-1/T-3/T-6/T-7/T-11 wait on the Book (B2);
T-4/T-10 batch with the v12 schema work. Ren rules T-7's guard and
anything the cell finds touching a standing law.*

— Taumel, crazy-ideas
