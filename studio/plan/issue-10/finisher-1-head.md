# Issue #10 — Receipt (audit fixes) · FINISHER 1 (head & number)

**Seat:** Finisher 1, Zettel's cell for GitHub issue #10, "Main-app pass — Receipt (audit fixes)".
**Slice:** the slip's HEAD only — title (his intention), the №, the ARMED line.
**Rulings finished here:** audit item 2 / Druck P-4 (№ as head-mark), item 3 / cure A11
(date on cross-midnight slips), item 14's collapse half / cure A9 (faulted head row).
**Sources:** `studio/plan/AUDIT-receipt.md` (Zettel, ruled Oct 30 01:15 IST; read in the
`wt-ci` clone — the audit file is not present in this clone) and the code in *this* clone,
`app/src/main/java/com/example/ui/components/ReceiptSheet.kt`. Every line citation below
is to this clone's file unless a path says otherwise.
**Binding character, untouched:** Anonymous Pro mono ledger (11sp / 1sp tracking,
`ReceiptSheet.kt:187–197`), Atkinson title, long-form durations, zero buttons / zero CTA
on the paper, omission law (absent over false). Sora gains no foothold in the head.
Nothing here compiles, builds, or tests — paper only.

---

## 0. Current head, in evidence (the three faults this slice closes)

| Element | Today (this clone) | Fault |
|---|---|---|
| Title | Atkinson Hyperlegible Bold, 15sp, `onBackground`, `weight(1f)` — L90–98 | None in itself; it is the ink/size reference the № must meet. No `lineHeight`, no `letterSpacing`, no wrap law is set. |
| № | Anonymous Pro Bold, **13sp**, **`onSurfaceVariant`** — L103–109 | Item 2 exactly: 2sp smaller and one ink greyer than the title it heads. No `maxLines` / `softWrap` guard: nothing stops it wrapping or being squeezed. |
| Head Row | `SpaceBetween` + `CenterVertically`, no inter-element spacing — L85–89 | With a 2-line title the № centres against *both* lines (app-bar behaviour, Druck P-4). Title can touch the №: the only gap is whatever `SpaceBetween` leaves after `weight(1f)` takes its share — no floor. |
| Faulted branch | Empty `Box(Modifier.weight(1f))` spacer inside the same Row — L99–102 | Item 14: the Row, its alignment, and a title-width slot (~300dp of the ~348dp inner width) all survive with nothing in them; the № floats over the void. |
| ARMED line | `ReceiptMonoLine`, 11sp / 1sp / `onBackground`, string built L115–122 from `hhmm()` (L199–200, pattern `"HH:mm"` only) | Item 3: no date exists anywhere in the file. `hhmm(armWallMs)` and `hhmm(endedAtWallMs)` are indistinguishable across midnight — `ARMED 23:40` / `ENDED 00:10` reads as backwards time. |

Reference geometry used throughout: Column padding 20dp (L81), host Box padding 12dp
(L71), Column `spacedBy(8.dp)` (L83); standard-card inner width ≈348dp (the standing
measurement in the footer comment, L166–174). Inner width at any device width W is
`W − 24dp (host) − 40dp (column)`.

---

## 1. F1 — The № is the head-mark: same size, same ink, same baseline (item 2)

One shared head-mark style for both elements. The title keeps its face and voice;
the № keeps its mono face (it is a ledger mark, not a second title) and is raised to
the title on every axis the eye compares.

### 1.1 Exact values

| Property | Title (L91–98, amended) | № (L103–109, amended) |
|---|---|---|
| `fontFamily` | `AtkinsonHyperlegibleFamily` (unchanged) | `AnonymousProFamily` (unchanged) |
| `fontWeight` | `FontWeight.Bold` (unchanged) | `FontWeight.Bold` (unchanged) |
| `fontSize` | **15.sp** (unchanged) | **15.sp** (was 13.sp) |
| `lineHeight` | **20.sp** (new, explicit) | **20.sp** (new, explicit) |
| `letterSpacing` | **0.sp** (new, explicit) | **0.sp** (new, explicit — do *not* give it the ledger's 1sp; at 15sp that adds ~5dp across `№ 048` and pushes the mark off the flush right edge) |
| `color` | `scheme.onBackground` (unchanged) | **`scheme.onBackground`** (was `scheme.onSurfaceVariant`) |
| Alpha / tint | none | none — the same `Color` object, alpha 1.0, in day and night schemes alike (both resolve through `MaterialTheme.colorScheme`; no hex is introduced anywhere in this slice) |

### 1.2 Alignment and spacing (the Row, L85–89, amended)

- `verticalAlignment`: **`Alignment.Top`** (was `CenterVertically`), and both Texts
  carry `Modifier.alignByBaseline()` (the № additionally keeps no `weight`).
  Net law: the № sits on the title's **first-line baseline**, always. A 1-line title
  and a 3-line title put the № in the identical position — it never sinks to the
  middle of a wrapped title block.
- Minimum title↔№ gap: **12dp**, guaranteed, not emergent. Implement as
  `Modifier.padding(start = 12.dp)` on the № (composed with `alignByBaseline()`),
  keeping `horizontalArrangement = Arrangement.SpaceBetween` and the title's
  `weight(1f)`. 12dp is the floor at every title length, including a title that
  fills its full weighted share.
- Edges: title left edge and ARMED left edge both flush at the Column's 20dp inset;
  № right edge flush at the inner width's right edge — no extra inset, no trailing
  tracking (see `letterSpacing` above). Head row width = full inner width.
- Single-line head height is now deterministic: **20dp** (the shared `lineHeight`),
  followed by exactly one 8dp Column gap before ARMED (§3.2).

### 1.3 The № never degrades

- `maxLines = 1`, `softWrap = false`, no ellipsis, no auto-size, no shrink.
  `numberPrint` is `№ ` + zero-padded-to-3, never truncated
  (`data/ReceiptState.kt:189` — `№ 048` … `№ 1048`); at 15sp Anonymous Pro Bold
  (advance ≈0.6em ≈ 9.0dp/glyph) the widest lawful print, `№ 1048` (6 glyphs),
  measures ≈54dp — it fits its slot at every № the machine can mint, so no
  degradation path is ever needed.
- If the title is so long that title + 12dp + № exceed the inner width, the
  **title wraps** (into its weighted share) — the № never yields width, size,
  or ink. This priority is part of the spec, not an accident of layout order:
  the № is composed outside any shrinking container.

### 1.4 Title wrap law (finishing the acquitted title, not redesigning it)

- No `maxLines`, no ellipsis: paper does not cut his words. Wrapped lines are
  flush-left, `lineHeight` 20sp, 0dp extra leading between wrapped lines.
- The head block may therefore be 20dp, 40dp, or 60dp tall for 1/2/3 title lines;
  ARMED follows after the same single 8dp gap in every case (§3.2).

**Acceptance (paper checks):** (a) a 1-line-title slip and a 3-line-title slip
place the №'s baseline on the title's first baseline, right edge flush, at
15sp/`onBackground`; (b) source diff shows `onSurfaceVariant` and `13.sp` gone
from the № and no other element in the head using `onSurfaceVariant`;
(c) `№ 1048` prints whole on one line.

---

## 2. F2 — Cross-midnight slips carry the date once, on the ARMED line (item 3 / A11)

Placement ruled here, out of the audit's two lawful options ("the ARMED line
carries its day, *or* the head carries the date once"): **the ARMED line carries
it.** The date qualifies the ARMED stamp, so it stands with that stamp; the head
row (§1) gains no second line, same-day slips stay byte-identical, and "once" is
satisfied structurally — no other line is permitted a date.

### 2.1 The trigger — calendar days, not durations

- Cross-midnight ⇔ the **local calendar date** of `receipt.armWallMs` differs
  from the local calendar date of `receipt.endedAtWallMs`, both taken in the
  device default timezone — the same zone the existing `hhmm()`'s
  `SimpleDateFormat` resolves in (L199–200). Not "ended < armed as clock
  times", not "span > N hours": a 23:55→00:05 slip triggers; a 00:05→23:55
  same-day slip does not; a 25-hour slip inside one calendar date is impossible,
  and a multi-day span triggers exactly the same single date.
- Compute both dates from the carried fields only (`armWallMs`,
  `endedAtWallMs` on `ReceiptState`) — never `System.currentTimeMillis()` at
  render: a restored slip must print the same date its first printing did.

### 2.2 Exact print

- Same-day: **unchanged, byte for byte** —
  `ARMED 09:00 · PLANNED 50 MIN` / `ARMED 09:00` (FLOW).
- Cross-midnight, ordinary case — date segment inserted before the time,
  joined by the line's existing separator (`" · "`, space + U+00B7 + space):

  ```
  ARMED 30 OCT · 23:40 · PLANNED 50 MIN
  ARMED 30 OCT · 23:40                  ← FLOW (no PLANNED, RC-02 unchanged)
  ENDED 00:10 · {outcome word}          ← ENDED never gains a date
  ```

- Date format, exact: pattern **`dd MMM`**, `Locale.US`, rendered **uppercase**
  (`30 OCT`, `05 NOV`) — zero-padded 2-digit day, 3-letter month, one space
  between. It takes the ARMED line's own type unchanged: the whole line,
  date included, is one `ReceiptMonoLine` string — Anonymous Pro 11sp / 1sp
  tracking / `onBackground` (L187–197). No second size, ink, or face for the date.
- Year boundary exception: if the two dates also differ in **year**, the format
  is **`dd MMM yyyy`** (`ARMED 31 DEC 2026 · 23:40 · …`). Still one date, still
  on ARMED only. No other case prints a year.
- **Once means once:** no date in the head row, no date on ENDED, no date on
  any deflection line (they keep `HH:mm` only, L139–144), no date in the
  footer. A deflection that itself fell after midnight is still dated by the
  ARMED date + its clock time, by position in the session — the slip does not
  re-date its list.

### 2.3 Implementation shape (for the builder)

- Two new private helpers beside `hhmm()` at L199–200, same file, same
  `SimpleDateFormat`/`Locale.US` discipline:
  `sameLocalDay(aMs: Long, bMs: Long): Boolean` and
  `dayLabel(wallMs: Long, withYear: Boolean): String` (patterns above).
  The ARMED string construction at L116–122 gains exactly one conditional
  segment: `if (crossMidnight) "${dayLabel(armWallMs, yearDiffers)} · " else ""`
  prefixed to the existing `hhmm(armWallMs)` segment, ahead of the unchanged
  PLANNED logic. No state addition: both inputs already ride `ReceiptState`.
- Fit, measured in the ledger's own arithmetic (11sp, advance ≈6.6dp + 1sp
  tracking ≈ 7.6dp/char): worst ordinary print
  `ARMED 30 OCT · 23:40 · PLANNED 1H 00M` = 37 chars ≈ **281dp**; worst year
  print = 42 chars ≈ **319dp** — both inside the ≈348dp inner width on one
  line at fontScale 1.0, with the year case the tighter one. If a user's
  fontScale ever forces a wrap, ARMED wraps only at a `" · "` boundary,
  continuation flush-left (no hanging indent — that device belongs to the
  deflection list, A10's seat), and no time, date, or PLANNED figure is ever
  truncated or ellipsised: a truncated stamp is a false stamp.

**Acceptance (paper checks):** (a) arm 23:40 / end 00:10 next day prints the
§2.2 block exactly; (b) arm 00:10 / end 23:55 same day prints no date anywhere;
(c) arm Dec 31 / end Jan 1 prints `31 DEC {arm year}` once; (d) a restored
cross-midnight slip reprints the identical date; (e) grep: exactly one
date-pattern formatter exists in the file, called only from the ARMED branch.

---

## 3. F3 — Faulted slips: the title row collapses whole (item 14 / A9, collapse half)

### 3.1 What dies

The conditional-spacer construction at L90–102 dies entirely. On the faulted
branch there is **no Row, no `Box`, no `Spacer`, no zero-alpha or empty Text**:
the head Row composable is simply not composed. A `height(0.dp)` placeholder,
a kept Row with an invisible title, or a semantics-bearing empty node are all
non-compliant — collapse means the slot does not exist, in layout *and* in the
accessibility tree (an empty title node is what made the fault read as a
rendering crash, Neu Q5).

### 3.2 What the faulted head is, exactly

- One line only: the №, printed with §1's finished values unchanged
  (Anonymous Pro Bold, 15sp, `lineHeight` 20sp, `letterSpacing` 0sp,
  `onBackground`, `maxLines = 1`, `softWrap = false`), composed as a single
  `Text` with `Modifier.fillMaxWidth()` and `textAlign = TextAlign.End` —
  right edge flush at the same inner-width edge it occupies on a healthy slip,
  so the №'s position is identical whether or not a title stands beside it.
- Faulted head block height: **20dp** (the №'s line), then exactly **one 8dp**
  Column gap (`spacedBy(8.dp)`, L83 — which spaces only composed children, so
  no phantom gap accrues), then ARMED. Total reserved for the absent title:
  **0dp** in every axis — versus today's surviving slot of ≈300dp wide
  (`weight(1f)` of the inner width minus the №) inside a live Row.
- Branch shape for the builder: `if (!receipt.faulted) { Row(…) { Title; № } }
  else { №-only Text }` — one № `Text` spec used by both branches (same
  values, factored once), so the two heads can never drift apart in ink or size.
- **Scope fence:** this spec prints nothing in the collapsed space. Emil's
  `FOR: <intention>` charge is group B3, unruled; F-W1 stands whole until Ren
  ratifies it, so no `FOR:` line, no fault headline, and no title text of any
  kind appears in the head on a faulted slip. The fault's own line further
  down the slip (L154–165) is another seat's wording to finish — untouched here.
  The ARMED line on a faulted slip follows §2 unchanged, date rule included.

**Acceptance (paper checks):** (a) faulted slip's first printed line is the №,
right-flush, at 15sp/`onBackground`; (b) layout/semantics inspection of the
faulted branch finds no Row/Box/Spacer/empty Text above ARMED; (c) healthy
and faulted slips place the № at the identical right-edge x and the ARMED
line starts exactly 28dp (20dp head + 8dp gap) below the head block's top
on a faulted slip.

---

## 4. What this slice deliberately does not touch

- ENDED line, outcome words (A2), RAN total and its rule (designer's §2 / A1,
  A6), WITHIN, HELD omission, deflections and their print treatment (A10),
  the two machine statements' voice (item 16), footer (A3), shape/anchor
  (A5), rhythm beyond the head's own joints (A6) — the head contributes
  **0dp** of extra spacing toward any future section rhythm: its two joints
  are fixed at 12dp (title↔№, horizontal) and 8dp (head→ARMED, vertical),
  and section spacing lands below ENDED, never inside the head.
- No dismissal, arrival, or persistence behaviour (A7–A9's other halves).
- No new colour, face, image, button, or CTA anywhere — the head remains
  two voices only: his intention in Atkinson, the machine's marks in mono.

— Finisher 1 (head & number), Zettel's cell (issue #10). Paper only; nothing compiled.
