# FINISH — TYPE & PRINT DISCIPLINE, THE RECEPTION SHEET

Seat: **Fein (finisher, seat 2)**, Pforte's cell, GitHub issue #14.
Scope: gallery standard for type and print discipline ONLY —
`ReceptionSheet.kt` whole + the styles it imports from
`ui/theme/Type.kt`. No app code edited. Nothing compiled.

Ground: AUDIT-setup-sheet.md §1, §4 (F-2, F-9, F-11), §5 Blank 6,
S-12; house font law — **Sora** display, **Atkinson Hyperlegible**
interface/body, **Anonymous Pro** telemetry/numbers; the receipt's
grammar — count in mono, sentence in prose — is the sheet's grammar.

**Census state note:** the builder lane was editing this file
during the census (S-12 gloss + CHECK-for-UNMEASURED landed
mid-read). Line numbers below are the final read; they may drift
±a few lines as the lane moves. Every charge is tied to a style
value, not only a line, so it survives drift.

**The ladder (Type.kt, binding):** six sizes only — 104 / 72 / 50 /
22 / 15 / 10, no two tiers within 1.4×. Any size outside the ladder
is a violation by definition. Named styles on the sheet:
`PillBadgeTextStyle` = Anonymous Pro Bold 10 / 12, ls 2.0 ·
`DrawerHeaderTextStyle` = Sora Bold 22 / 26 · `BodyTextStyle` =
Atkinson 400 15 / 22 · `AppTitleTextStyle` = Atkinson Bold 15 / 20 ·
`PrimaryButtonTextStyle` = Sora Bold 15 / 20, ls 0.5.

---

## THE CENSUS — every Text/print site, in sheet order

| # | Site (line) | Prints | Current style | Lawful style | Verdict |
|---|---|---|---|---|---|
| 1 | Nameplate (318) | `MONOLITH ML-01` | PillBadge, onSurfaceVariant | PillBadge — a plate IS telemetry dress | **KEEP** |
| 2 | Title (326) | `SETUP + INTRODUCTION` | DrawerHeader (Sora Bold 22) | DrawerHeader | **KEEP** |
| 3 | Eyebrow (338) | `WHAT IT IS` | PillBadge | PillBadge | **KEEP** |
| 4 | One-liner (346) | "While a session runs, Monolith shuts…" (S-7 words) | BodyTextStyle (Atkinson 400) | **AppTitleTextStyle** (Atkinson Bold 15/20) — audit F-2, §4: "one-liner takes AppTitle weight — SHIPS" | **CHANGE** |
| 5 | Boundary line (358) | `YOUR STUDY HOURS LIVE IN YPT. MONOLITH KEEPS NO SCORE AND GRADES NOTHING.` | PillBadge | PillBadge (all-caps statement line; the sheet's standing dress for such lines — cf. #14, #22) | **KEEP** |
| 6 | Eyebrow (367) | `THE FOUR DOORS` | PillBadge | PillBadge | **KEEP** |
| 7 | Door name, ×4 (614) | `item.boardName` | PillBadge | PillBadge — byte-identical to PARK board name (ParkDrawer ParkBoardRow) | **KEEP** |
| 8 | Door word, ×4 (620) | `SET / NOT SET / UNPROVED / NOT CHECKED` | inline Atkinson Bold 15, ls 0.5 | Same values — byte-identical to PARK's board word (ParkDrawer.kt ParkBoardRow: Atkinson Bold 15, ls 0.5, same colour logic). One fact, one print. Should be *named* (see Charge C-3), values do not move | **KEEP** |
| 9 | Job line, ×4 (634) | `receptionDoorJob(item)` — interceptor / witness / anchor / mail room sentences incl. §5 privacy + way-back clauses | BodyTextStyle | BodyTextStyle — prose sentence in prose family; codename lead-in in the same voice, no internal split | **KEEP** |
| 10 | STALE gloss, ×4 when STALE (643) | `THE LAST PROOF AGED OUT — CHECK RE-PROVES IT` (S-12 gloss) | inline Atkinson 400, **12.sp / 17.sp**, onSurfaceVariant | BodyTextStyle values (Atkinson 400 **15/22**), colour onSurfaceVariant — see Violation V-2 | **VIOLATION** |
| 11 | Prepare line, ×4 when MISSING (654) | `receptionDoorPrepare(item)` escort sentences | inline Atkinson 400, **12.sp / 17.sp**, onSurfaceVariant | BodyTextStyle values (Atkinson 400 **15/22**), colour onSurfaceVariant — see Violation V-2 | **VIOLATION** |
| 12 | FIX key, ×4 when MISSING (694) | `FIX — <boardName>` | PrimaryButtonTextStyle, onInkControlFill | PrimaryButtonTextStyle (its ink *dress* is F-4, another seat's charge — the type is lawful) | **KEEP** |
| 13 | CHECK key, ×4 when STALE/UNMEASURED (724) | `CHECK — <boardName>` | PrimaryButtonTextStyle, onBackground | PrimaryButtonTextStyle | **KEEP** |
| 14 | Rank line (393) | `THE FIRST TWO MAKE BLOCKING REAL. THE THIRD GUARDS THE MACHINE ITSELF. THE FOURTH MAKES IT QUIET.` | PillBadge | PillBadge (F-8 kill struck, §4 — the line and its dress stand) | **KEEP** |
| 15 | Guard foot (407) | `guard.text` = `GUARD n/4` / `GUARD n/4 — <NAME>` | inline Atkinson Bold 15, ls 0.5 | **Anonymous Pro Bold 15, ls 0.5** — same spec as the cured total (#23). See Violation V-3 | **VIOLATION** |
| 16 | Eyebrow (422) | `HOW A SESSION RUNS` | PillBadge | PillBadge | **KEEP** |
| 17 | Beat label, ×6 (744, via ReceptionBeatLine) | `ARM / SEAL / THE ROOM / PEEK / BREAK / FINISH` | PillBadge, stacked ABOVE its line in a Column | PillBadge values (Anonymous Pro — family already lawful) as a **fixed label column** in a ruled row — F-9 form, spec below | **CHANGE** |
| 18 | Beat line, ×6 (749) | the six beat sentences (S-3/S-7/S-8/S-15 words) | BodyTextStyle, full width under its label | BodyTextStyle as the **second column** of the same ruled row — F-9 form, spec below | **CHANGE** |
| 19 | Routine line (464) | `NEXT ROUTINE — <NAME> · HH:MM · SET ON SHIELD` (S-2 cured derivation) | PillBadge | PillBadge — the time datum rides the mono family, which is its lawful family; F-10's move above the total has landed (464 < 523) | **KEEP** |
| 20 | Rules line (475) | `YOUR RULES LIVE ON SHIELD — … CHANGE THEM ANY TIME BETWEEN SESSIONS.` (S-14 words) | BodyTextStyle | BodyTextStyle — a prose sentence in the prose family | **KEEP** |
| 21 | OPEN THE LIST key (502) | `OPEN THE LIST` | PrimaryButtonTextStyle | PrimaryButtonTextStyle | **KEEP** |
| 22 | Leave line (509) | `LEAVE WHEN YOU LIKE — THE HEADER KEEPS NAMING THE FIRST THING MISSING.` (S-11 words) | PillBadge | PillBadge | **KEEP** |
| 23 | **Total count (523)** | `GUARD ${count}/4` | **AppTitleTextStyle (Atkinson Bold 15/20)** | **Anonymous Pro Bold 15** — exact spec in V-1 below. Audit F-11, §4: "the sheet currently spends the telemetry family only on labels and never on a datum; the receipt's grammar divides count (mono) from sentence (prose). The sheet now does the same." | **VIOLATION** |
| 24 | Total sentence (530) | `total.sentence` — per-lack composition, see §A below | BodyTextStyle | BodyTextStyle — prose under a mono count, the receipt's exact division | **KEEP** |
| 25 | Stub note (540) | `THIS SHEET WAITS IN PARK — SETUP + INTRODUCTION.` | PillBadge | PillBadge; F-12's move above the tear has landed in this tree | **KEEP** |
| 26 | DONE key (573) | `TO THE DECK →` | PrimaryButtonTextStyle, onInkControlFill | PrimaryButtonTextStyle — the one ink key; Sora button voice is the house key voice | **KEEP** |
| 27 | DONE act line (582) | `THIS FINISHES THE SETUP. IT STARTS NOTHING, SEALS NOTHING, AND MAY BE PRESSED WITH DOORS UNSET.` (Blank (a)3) | PillBadge | PillBadge | **KEEP** |

**Counts: 27 sites — KEEP 20 · CHANGE 3 · VIOLATION 4.**
(Door-card and beat sites are one composable each, ×4 / ×6 printings;
counted once per call site, as printed code.)

---

## THE VIOLATIONS — charges with line evidence

**V-1 · THE TOTAL COUNT IS A DATUM IN PROSE DRESS (F-11, headline).**
ReceptionSheet.kt:523–527 prints `GUARD ${total.count}/4` in
`AppTitleTextStyle` = Atkinson Hyperlegible Bold. The count is the
sheet's one hard datum and the telemetry family never carries it:
every Anonymous Pro appearance on the sheet is a *label*
(PillBadge at 318, 338, 358, 367, 393, 422, 464, 509, 540, 582,
614, 744) — the family is spent on labels, never on a datum,
exactly as §4 F-11 charges. **Exact lawful spec** — replace
`style = AppTitleTextStyle` with:

```
TextStyle(
    fontFamily = AnonymousProFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 15.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.5.sp,
    platformStyle = TightNumericPlatformStyle
)
```

(Anonymous Pro Bold 15 — F-11's words; ls 0.5 and the 20.sp line
match PARK's own datum print, the LAST PROVED instant at
ParkDrawer.kt:311–315, same family/weight/size/tracking.) Add
`AnonymousProFamily` + `TightNumericPlatformStyle` to the imports;
`AppTitleTextStyle` then leaves the file's import list.

**V-2 · THE ESCORT AND THE GLOSS ARE OFF-LADDER (12.sp).**
Prepare (654–660) and the S-12 STALE gloss (643–649) both print in
an inline Atkinson 400 at **12.sp / 17.sp lineHeight**. 12 is not
on the ladder (104/72/50/22/15/10, Type.kt's own table) — it is a
seventh size invented inline, ~1.25× from body 15, inside the
1.4× exclusion the ladder exists to enforce. Both are *sentences*;
sentences take the prose tier. **Exact lawful spec:** both sites
take `style = BodyTextStyle` with `color = scheme.onSurfaceVariant`
retained (the dimming is the meaning — escort/gloss are second
voice, not second size). Price, stated plainly: cards grow ~3.sp
per wrapped line at 15/22 against 12/17. The alternative — demote
both to PillBadge 10 — is rejected: a multi-clause escort at
10.sp tracked 2.0 is unreadable at 04:00, and the gloss is a
sentence, not a label.

**V-3 · ONE DATUM, THREE DRESSES — THE GUARD PRINT COMPETES
WITH ITSELF.** The single fact `GUARD n/4 …` is printed:
(a) at the doors' foot, ReceptionSheet.kt:407–413 — inline
Atkinson Bold 15, ls 0.5; (b) at the total, :523 — AppTitleTextStyle
(Atkinson Bold 15/20, ls 0); (c) in PARK's stub row, ParkDrawer.kt
(park_reception_row) — AppTitleTextStyle. Three call sites, two
named/inline styles, two trackings, zero mono — for the sheet's
headline number. V-1 cures (b). **Cure for (a):** the Guard foot
takes the identical Anonymous Pro Bold 15 / ls 0.5 spec as the
cured total — foot and total are the same derivation
(`guardLineFor` / `receptionTotal` over the same snapshot) and
must wear the same type. (c) is outside this sheet: referred to
Pforte — if the foot/total go mono, PARK's stub row should be
ruled on in the same pass or the fork survives one screen away.

**V-4 (systemic, recorded once, not per-row) · THE GRAMMAR IS
INVERTED.** As built, the sheet spends Anonymous Pro on
*sentences* (boundary #5, rank #14, leave #22, act #27 — all
full prose in PillBadge) and Atkinson on its *count* (#23).
Individually those rows are acquitted above — the all-caps
statement line in badge dress is this sheet's established voice
and the audit acquits the lines — but the inversion is why
F-11 matters: curing #23 (and #15 with it) is what makes the
division true — **count mono, sentence prose** — everywhere a
*datum* appears. No other row moves on this charge.

---

## THE CHANGES — exact specs

**C-F2 · One-liner weight (row 4).** `style = BodyTextStyle` →
`style = AppTitleTextStyle` at :346–352. Nothing else moves:
colour onBackground, same words. (AppTitleTextStyle stays
imported for this site after V-1 removes it from the total.)

**C-F9 · Beats as a ruled table (rows 17–18).** ReceptionBeatLine
(:741 ff.) is currently a Column — label stacked over line, no
rule, no column. **Exact lawful form:** each beat is a `Row`,
`ReceptionHairline()` beneath it (the sheet's own 1.dp @ 0.18f
rule — the table is ruled with the sheet's ruling, no new rule
spec):

- Row: `fillMaxWidth`, `padding(vertical = 8.dp)`, top-aligned.
- Label column: fixed width **84.dp** (fits `THE ROOM`, the
  longest label, at PillBadge 10.sp ls 2.0 on one line — my
  measurement call, Pforte to confirm at render), `Text(label,
  style = PillBadgeTextStyle, color = onSurfaceVariant)` —
  the label column *in Anonymous Pro*, per F-9; the family
  does not change, the geometry does.
- Line column: `Text(line, style = BodyTextStyle, color =
  onBackground, modifier = Modifier.weight(1f))`.
- The trailing `Spacer(8.dp)` inside the composable dies;
  row padding + hairline carry the rhythm. Six rows, six
  hairlines; the block then reads as one table under the
  `HOW A SESSION RUNS` eyebrow, not six stacked couplets.

---

## §A · THE PER-LACK TOTAL SENTENCES (audit §5, Blank 6)

`receptionTotal` composes **one sentence per lacking door**
(absence sentences verbatim A-2; aged-proof sentences for STALE),
joined into the single `total.sentence` string printed at :530.
Typography ruling:

- **One paragraph, one voice:** all per-lack sentences print in
  the single BodyTextStyle Text at :530–534, under the mono
  count. No per-sentence styling, no bolded door names inside
  the prose, no colour split between the absence and aged-proof
  families — the count above carries the number; the paragraph
  carries only sentences. Current code already does exactly
  this: **KEEP, and do not "improve" it** into styled spans.
- The one-lack case (the common case) prints exactly one
  sentence — identical in dress to today's single-sentence
  total. The four-lack case is one long prose block at 15/22;
  that length is the price Blank 6 accepted, and prose size
  is what makes it payable.
- The aged-proof sentence (`<NAME> IS SET — THE LAST PROOF
  AGED OUT. CHECK RE-PROVES IT.`) and the card gloss (#10) are
  the same words in two seats — card gloss in second voice
  (onSurfaceVariant), total sentence in full voice
  (onBackground). That is one fact at two volumes, **not** a
  competing-style charge: the dresses differ by *seat*, the
  families agree once V-2 lands (both Atkinson prose).

## §B · THE STALE GLOSS (S-12)

Present in this tree at :640–651, gated `print == STALE`,
words exact: `THE LAST PROOF AGED OUT — CHECK RE-PROVES IT`.
UNPROVED keeps its word at #8; the gloss is the cure, and its
*seat* is right (under the job line, where the prepare line
sits for MISSING — the two second-voice lines never co-print).
Its only defect is V-2's size. Once V-2 lands, gloss and
prepare share one spec — BodyTextStyle values, onSurfaceVariant —
which is correct: they are the same grammatical thing, the
door's second voice, in its two states.

## §C · INLINE STYLES — a naming debt (no row charged)

Four sites bypass the named ladder with inline parameters:
door word (:620), Guard foot (:407), gloss (:643), prepare
(:654). Door word and Guard foot carry *identical* values
(Atkinson Bold 15, ls 0.5) and the door word duplicates
ParkBoardRow's inline values a third time. After V-1/V-3 land,
the mono datum spec exists twice inline (foot + total).
Recommendation, priced FREE: promote two named styles in
Type.kt at the merge — the mono datum (Anonymous Pro Bold 15,
ls 0.5) and, if PARK agrees, the state word — and point every
site at them. Inline duplicates are how V-3 happened.

— Fein (finisher), issue #14 cell
