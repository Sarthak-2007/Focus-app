# FINISH — SCHLIFF (Finisher 3, Wacht's cell, issue #13)

Slice: **BREAK + THE BANNER SLOT + THE TEAR RULE** — audit items
A-5, A-1, A-7, and the §C-3 break sentence.
Gallery-standard pass over the merged code, read fresh.

## STATE OF THE GROUND (read this first)

- Subject file: `app/src/main/java/com/example/ui/components/`
  `AirlockAndDisarmOverlays.kt` in the issue-13 clone, audited at
  md5 `a2da335dd4c4a6119d5bcc063b607860` (1,197 lines; md5 stable
  across three checks during this pass).
- The merge was IN FLIGHT during this audit: the file changed
  twice under the first read (819-line trunk face → the cured
  1,197-line merge), and `MainActivity.kt` /
  `MonolithViewModel.kt` / `MonolithRepository.kt` were still
  being written at the last check. **No surgical edits were made
  by this seat** — every cure below is exact and small, but
  editing a file its builder is actively rewriting would race
  him. All seven defects are filed for Wecker to land.
- Lane original compared throughout:
  `~/workspace/clone-inspect/builds/flow-clock/Focus-app` (same
  file + `DeskClockFlowTest.kt`). The lane is the PRE-cure face:
  its break line is `BREAK — DOORS OPEN · n MIN LEFT`, its
  progress strip is the capsule on the `NightColorSurfaceVariant`
  bed. Neither survives in the merged file except where F-1 says.

## VERIFIED CLEAN (checked, not assumed)

- **A-7 clamp + hair** — `tearFraction` at
  AirlockAndDisarmOverlays.kt:155–157 is
  `coerceIn(TEAR_HAIR_FRACTION = 0.01f, 1f)`, a named hair and
  the ruled upper clamp; a delayed-finish overrun cannot print
  past the span.
- **A-7 notches, shape** — :437–444: width `3.dp.toPx()`,
  full-height (`size = Size(notchWidth, h)`), drawn LAST over
  both the solid and the dashed runs, opaque `Color.Black` —
  no alpha of their own; the ink they interrupt carries the
  descent (`alphas.hero`, :409), the gaps inherit it as absence.
  Gate `blockCount > 1` (:436) matches the models: routine SLEEP
  rows carry `totalStudyBlocks = 1`, so no notches are owed
  there (Moench MA-1's ground holds).
- **A-7 ground — VERIFIED, merged tree** — the rule is a bare
  `Canvas` (:410) composed directly over the face's true-black
  ground. The dead capsule and its bed are GONE from the merged
  file; `NightColorSurfaceVariant` (#100E0C, Color.kt:35) occurs
  in the file only as the import at :67 and a comment at :403
  (see F-6). Tree-wide grep: no `NightColorSurfaceVariant` bed
  sits under the rule anywhere — its remaining uses are other
  surfaces (digest sheet, Deck), none behind this Canvas. The
  bed survives ONLY in the unmerged lane original (its 16 dp
  capsule strip), which the merge has already superseded.
- **A-1 suppression + ownership** — `deskClockBlockLine`
  returns null on `isInSafeBreak` (:560; fun at :558), and
  the slot's derivation tries the break line FIRST
  (:459–462): during a break the break line owns the slot and
  the block line is suppressed. Both halves of A-1's gate hold.
- **A-1 figure** — k < n prints `BLOCK k OF n · UNTIL HH:MM`
  from `now + remainingSeconds` (:565–566); `remainingSeconds`
  is the per-block remainder (MonolithModels.kt:382–383), so
  the instant is the block's true end. Final block prints
  `BLOCK n OF n` alone (:564) — no instant prints twice.
  Single-block and stopwatch spans print no block line at all.
- **A-5 core law** — slot composed only `if (running)` (:458):
  idle wears no slot (and A-8's gate holds with it — the banner
  cannot name a queued session on an idle face). While running
  the height is fixed (`height(62.dp)`, :466 — but see F-4),
  absent banner leaves the same box standing with a transparent
  background (:468–470), text is top-aligned
  (`Alignment.TopStart`, :472). Absent ↔ 1-line ↔ 2-line and
  banner ↔ break swaps no longer move the column. Banner and
  block lines print at `alphas.numberLine` — A-2's 0.50 floor
  reaches the break line too.
- **Break hero** — during a break the hero still reads the span
  (`deskClockHeroText` → `spanRemainingSeconds`); the break
  never impersonates a finish.

## DEFECTS — 7

### F-1 · HIGH — the break sentence is still the ceiling-minute lie §C-3 killed; its own KDoc claims the cure

`deskClockBreakLine`, AirlockAndDisarmOverlays.kt:576–580:

```kotlin
val minutes = (safeBreakSecondsRemaining.coerceAtLeast(0) + 59) / 60
return "BREAK — PEEK APPS OPEN · $minutes MIN LEFT"
```

§C-3's sentence (audit §5: "PEEK APPS OPEN" STANDS; A-1(a) +
A-11(c): one break sentence, room/glass/shade, ending
`… · UNTIL HH:MM` from the break-end) is not what this prints.
It prints a CEILING minute count — at 61 s remaining it says
"2 MIN LEFT", the exact derivation A-1 convicted on the block
line one seat above ("a pattern cannot acquit a false number").
Worse, the KDoc directly above it (:569–575) describes the
cured function — "the banner says the doors are open and until
when — the stored break-end in wall time" — that the code does
not contain. Comment and code tell two truths; that is a
lie-register entry, not a wording slip.
CURE (exact, two sites):
1. Change the derivation to take the wall clock the call site
   already holds and print the break's end:

```kotlin
internal fun deskClockBreakLine(
    isInSafeBreak: Boolean,
    safeBreakSecondsRemaining: Int,
    nowWallMillis: Long
): String? {
    if (!isInSafeBreak) return null
    val breakEndWall =
        nowWallMillis + safeBreakSecondsRemaining.coerceAtLeast(0) * 1000L
    return "BREAK — PEEK APPS OPEN · UNTIL " + wallClockTimeText(breakEndWall)
}
```

   (Same now + remaining derivation the house already trusts
   for ENDS on hand rows and for the block line's UNTIL at
   :566 — one derivation, three mouths, per A-11(c).)
2. Call site :459–462: pass `nowWallMillis` (already in scope
   in the composition).
3. The KDoc then becomes true; keep it.

### F-2 · MEDIUM-HIGH — notches are cut at k/n, which is not where the blocks end

AirlockAndDisarmOverlays.kt:439: `val x = w * (k.toFloat() / blockCount)`.
A-7(ii): notches are "cut at block-boundary fractions **of the
span**". The span's own geometry (MonolithModels.kt:399–401)
is `spanTarget = n × effectiveTargetSeconds + (n−1) × 600` —
breaks are span time, so equal fractions are not boundary
fractions. Worked example, 4 × 25 min blocks: span = 7,800 s;
the code cuts at 25.0 / 50.0 / 75.0 %; the span-priced
boundaries are 19.2 / 46.2 / 73.1 %. The first notch stands
≈5.8 % of the face width (≈21 dp on a 360 dp face) away from
the boundary it claims to mark — on the one element whose
whole job is marking boundaries truthfully.
CURE (exact): cut at the span's own fractions, derived from
the same numbers `spanTargetSeconds` uses:

```kotlin
val blockSecs = activeSession.effectiveTargetSeconds
val spanSecs = activeSession.spanTargetSeconds.coerceAtLeast(1)
for (k in 1 until blockCount) {
    val boundarySecs = k * blockSecs +
        (k - 1) * MonolithRepository.SAFE_BREAK_DURATION_SECONDS
    val x = w * (boundarySecs.toFloat() / spanSecs)
    …
}
```

(Uses the repository constant, never a bare 600; early-ended
breaks shift real boundaries by their untaken remainder —
`breakTakenSeconds` is not boundary-addressable from the row,
and the span-priced fraction is the honest figure the span
itself promises. Note that limit in the comment when landing.)

### F-3 · MEDIUM — A-1's suppression re-opens A-5's jump one seat up: the block line's seat is not reserved

The block line is composed inside the hero cell
(:384–395) only when `deskClockBlockLine` returns non-null.
At every break start it vanishes and at every break end it
returns, moving the tear rule and the banner slot ≈21 dp
(13 sp line + its leading + the cell's 4 dp gap) in 0 ms —
twice per break, all night. A-5 fixed exactly this class of
jump for the banner (the burn-in law spends 520 ms moving
this column 3 dp); composed with A-1, the cure is spent one
line higher. The audit's BREAK state keeps "hero and rule"
counting — it nowhere licenses the rule changing its seat.
CURE (exact): reserve the seat the way A-5 reserves the
slot — whenever the face is running a timed multi-block
span, compose a fixed-height line box (the block line's own
measure: set the Text `lineHeight = 17.sp` and the box
height to it) and print the text only when the derivation
returns non-null:

```kotlin
if (running && !activeSession!!.isStopwatchMode &&
    activeSession!!.totalStudyBlocks > 1) {
    Box(Modifier.height(17.dp)) {
        deskClockBlockLine(activeSession!!, nowWallMillis)?.let { …Text… }
    }
}
```

Single-block, stopwatch and idle faces compose no seat, as now.

### F-4 · MEDIUM — the slot's "two-line measure" is a magic number, and the text inside it is unmeasured

AirlockAndDisarmOverlays.kt:466 + :475–481. Three faults, one
seat:
(a) `height(62.dp)` is asserted, not derived — the comment at
:455–456 quotes the ruled measure (15 sp × 2 + padding) but
nothing in the code computes it; 62 dp matches neither that
formula (30 sp + 24 dp padding) nor a derivation from the
text actually set (14 sp, :479).
(b) The banner `Text` sets no `lineHeight`, so a "two-line
measure" is whatever the font's default leading yields —
the measure the slot claims to fix is not controlled
anywhere.
(c) No `maxLines` / overflow: a task title that wraps to
three lines overflows the fixed box and is clipped mid-glyph
by the slot's own rounded clip (:467) — the slot stays still
by amputating the sentence. And because the text is sp and
the box is dp, any fontScale above 1.0 grows two lines past
62 dp into the same clip.
CURE (exact): give the text a measure and derive the box
from it —

```kotlin
private val BANNER_LINE_HEIGHT = 19.sp
// in the slot:
.height(with(density) { (BANNER_LINE_HEIGHT * 2).toDp() } + 24.dp)
// on the Text:
lineHeight = BANNER_LINE_HEIGHT, maxLines = 2,
overflow = TextOverflow.Ellipsis
```

(any equivalent derivation is fine — what is not fine is an
unrelated constant). Ellipsis, never clip, is how a fixed
slot ends a long sentence.

### F-5 · LOW — the dash pattern is measured in raw pixels; the notch beside it is measured in dp

AirlockAndDisarmOverlays.kt:430–432:
`PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)` — Canvas
units are px, so the dashes are 8 px on / 6 px off: 8 dp on an
mdpi device, ≈2.7 dp on xxhdpi. The notch three lines later
converts properly (`3.dp.toPx()`, :437). One rule, two unit
systems; the perforation's texture changes with density.
CURE: `floatArrayOf(8.dp.toPx(), 6.dp.toPx())` (the dp values
are the designer's to confirm — what must die is the unit
split, not the numbers). Phase stays 0f — Mond J3's
no-doubling condition holds and must keep holding.

### F-6 · LOW — the bed is dead but its token still imports

AirlockAndDisarmOverlays.kt:67 imports
`NightColorSurfaceVariant`; after the capsule's death nothing
in the file consumes it (only the comment at :403 names it).
A dead import in a finished file is lint noise today and a
re-attachment point tomorrow — the next hand that reaches for
a bed under the rule finds the token already in scope.
CURE: delete the import when F-1…F-4 land in this file.

### F-7 · LOW — proof gap: no test in the merged tree pins this slice, and the lane's test pins the dead sentence

The clone's test tree has no `DeskClockFlowTest` — nothing
asserts `deskClockBreakLine`, `deskClockBlockLine`,
`tearFraction`, or the slot's behaviour (§7 debt 10's slice
share is unpaid on this face). The lane's test exists but
certifies the killed sentence
(flow-clock `DeskClockFlowTest.kt:249–252` expects
`BREAK — DOORS OPEN · n MIN LEFT`); ported as-is it would
fail the cured code — or worse, be "fixed" by reverting F-1.
CURE: port the lane test with §C-3 expectations — break line
at 61 s / 600 s / 1 s remaining against a fixed `nowWallMillis`
asserting `UNTIL HH:MM` strings, block-line suppression in a
break, final-block figurelessness, `tearFraction` at −0.5 /
0 / overrun, and notch fractions from F-2's worked example
(4 × 1500 s blocks → 0.192 / 0.462 / 0.731).

## EDITS MADE BY THIS SEAT

None — logged deliberately: the subject file was rewritten
twice during this audit and three neighbouring files were
still in motion at the last check (see STATE OF THE GROUND).
Every cure above is copy-exact and lands in minutes once
Wecker's merge settles; a finisher does not race his builder.

## CLOSE

**Defect count: 7** (1 HIGH, 1 MEDIUM-HIGH, 2 MEDIUM, 3 LOW).
**Worst: F-1** — the break line still prints the ceiling-minute
lie (`… · n MIN LEFT`) that §C-3, A-1 and A-11 each killed,
under a KDoc that claims it prints `UNTIL HH:MM`. Every break,
every night, the room's one sentence about the break is the
number the audit convicted — and the comment swears otherwise.

What I want next: the §7 debt-8 census frame for BREAK read
against F-1's cured string before this merge is called done.
What I could do unassigned: the same slot-measure pass over
the Deck's BREAK eyebrow, whose ceiling-minute derivation
(A-11's cross-note) is this defect's parent.

— Schliff, Finisher 3, Wacht's cell, Oct 30, 2026
