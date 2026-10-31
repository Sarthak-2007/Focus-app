# Issue #7 — Shield (audit fixes) — Finisher: WORDS & EDGES

**Seat:** SAUM, FINISHER 3, in VOGT's issue #7 cell — "Main-app pass — Shield audit fixes"
**Slice:** words and edges only — every printed word the R-5/R-6 fixes touch, checked for
one-voice consistency, truncation/overflow with long routine and profile names, narrow
screens, and large font scale. Final copy table + max-length behaviour per line (§3),
exact patches for MONTEUR (§4). No code edited by me — MONTEUR is sole code editor.
**Status:** DECLARED — paper only. Nothing compiled, no tests run, nothing committed/pushed.
**Sources:** `studio/plan/AUDIT-shield.md` (R-5, R-6, R-12 items 1/7/8 in full);
`app/src/main/java/com/example/ui/ShieldBlockingStatus.kt` (the builder derivation, 178 lines);
`app/src/main/java/com/example/ui/screens/ShieldMatrixScreen.kt` (current working tree,
1,067 lines); `studio/cells/issue-7/designer-form.md` (FORM §§1–3, the designer of record
for this sentence); `data/MonolithModels.kt` (`RoutineEntity.name` — an unbounded `String`,
no length cap anywhere in the tree); `data/MonolithRepository.kt` (peek constants,
seed names).

**State snapshot (what this spec is written against).** Already landed in the tree:
the `ARMED` header suffix (ShieldMatrixScreen, ROUTINES header — done, correct);
`ShieldBlockingStatus.kt` with the proof table, `routineCapWord()`, and
`routineRunningNowSuffix()`. **Not yet landed:** the status line's render law (today it is
one bare `Text`, Anonymous Pro 13 sp, no `maxLines`, no overflow — see F-1); `RoutineRow`
still prints binary `ON`/`OFF` in a fixed 48 dp cap and never calls `routineCapWord()` or
`routineRunningNowSuffix()` (both sit as unused imports); the reset strip still prints the
old wording; the search placeholder still prints `Filter apps…`. My patches (§4) are
written for that gap, not for a tree that already has them.

---

## 1. One voice — consistency rulings

The fixes introduce five time-words. They are all true, and they are **not synonyms** —
the table is the contract; nobody "harmonises" them later.

| Word | Claims | Time it speaks of | Home |
|---|---|---|---|
| `BLOCKING NOW` | enforcement is proved, this instant | now | status line only |
| `NOT BLOCKING` | the same claim, negated — never softened | now | status line only |
| `ARMED` | set, scheduled, will fire on its days | future | ROUTINES header count |
| `RUNNING NOW` | the routine's *window* contains now (schedule fact — **not** a blocking claim) | now | routine row meta |
| `WON'T FIRE` | switched on, `daysMask == 0`, can never fire | never | routine row cap |
| `ON` / `OFF` | the switch's own state (`ON` only when it can fire) | — | routine row cap |

A row can read `· RUNNING NOW` while the headline reads `NOT BLOCKING` — that visible
contradiction is a missed fire surfacing (FORM §3.2). Neither line yields to the other.

**Findings inside the status derivation itself:**

- **V-1 · STALE drops the session, UNPROVEN keeps it.** Today:
  `NOT BLOCKING — $label RUNNING, ENFORCEMENT NOT PROVEN` vs
  `NOT BLOCKING — SHIELD PROOF STALE`. Both mean "a session exists, proof failed" —
  they must carry the same detail. **Ruled:** STALE gains the label, same grammar:
  `NOT BLOCKING — <LABEL> RUNNING · SHIELD PROOF STALE`. (Patch P-2.)
- **V-2 · The screen fallback is the derivation's generic form — lock it.**
  The no-status fallback (`NOT BLOCKING — SESSION RUNNING, ENFORCEMENT NOT PROVEN`)
  is character-identical to the UNPROVEN headline with the generic label `SESSION`.
  That is correct and must stay exact: one wording per fact, whether the ViewModel
  supplied the derivation or the screen fell back. Any future edit to one is an edit
  to both. (No patch — a lock, recorded here.)
- **V-3 · Separator grammar is consistent — keep it.** `—` after the verb phrase,
  `·` between facts, `,` before a subordinate clause (`…, ENFORCEMENT NOT PROVEN`),
  `—` inside the break qualifier (`SAFE BREAK — PEEK APPS OPEN`). No variant invents
  a fourth mark.
- **V-4 · Safe-break suffix: the code's wording wins over FORM's draft.**
  Code prints `· SAFE BREAK — PEEK APPS OPEN`; FORM §1.3 drafted `· SAFE BREAK — PEEK OPEN`.
  `PEEK APPS OPEN` keeps the screen's noun (*apps*) and costs 5 chars, absorbed by the
  label budget (§2). **Ruled: code stands.**
- **V-5 · `ENDS HH:MM` is 24-hour, zero-padded** — the same grammar as the routine
  time column (`fmtRoutineTime`, `%02d:%02d`). One clock voice on the screen. ✓ no change.
- **V-6 · All fix-words are caps, no punctuation beyond the grammar marks, no
  exclamation, no "please".** The reset strip and search placeholder join this voice
  in §3 (rows 8–9) — the old `Filter apps…` (sentence case, ellipsis, wrong verb)
  is the one remaining foreign accent on the screen.

**Render deviations from FORM §2.2 found while checking edges (for VOGT — one line each):**
F-1 the status `Text` has no `maxLines`/overflow/fixed slot, so at 320 dp or fontScale 1.3
it wraps mid-sentence and pushes the cap down by a variable height — §3 row 1 + P-3 fix it.
F-2 it colours `NOT BLOCKING` with `scheme.error`; FORM ruled full ink for both verbs,
state carried by glyph + verb. Colour is outside my slice — flagged, not patched here.

---

## 2. Measurement basis (every budget below derives from this)

- **Content width:** LazyColumn horizontal padding 12 dp → **336 dp @ 360 dp screen,
  296 dp @ 320 dp screen** (the narrow case: small handsets, split-screen).
- **Font scales:** 1.0 (default) and **1.3** (Android "Large" — the large-font case).
- **Char widths (advance, bold):** Anonymous Pro is monospace, 0.60 em → 13 sp = 7.8 dp,
  12 sp = 7.2 dp, 11 sp = 6.6 dp (×1.3 at large scale). Atkinson Bold ≈ 0.55 em →
  14 sp ≈ 7.7 dp, 15 sp ≈ 8.3 dp, 11.5 sp ≈ 6.3 dp. Sora Bold 16 sp ≈ 8.5 dp.
- **Chars per status line** (Anonymous 13 sp, as currently rendered): 43 @ 360/1.0,
  37 @ 320/1.0, 33 @ 360/1.3, **29 @ 320/1.3**. Every status variant except
  `NO SESSION RUNNING` exceeds at least one of these — an explicit overflow law is
  therefore not polish, it is the line's only honest form (§3 row 1).
- **Name sources are unbounded:** `RoutineEntity.name: String` has no cap; a routine
  session's label is that name upper-cased + ` SESSION`; profile names are likewise
  uncapped (seeds: `FASTED BLOCK` 12, `DAY BLOCKS` 10, profiles ≤ 14 — seeds fit,
  renames don't). The cap must live at the print layer (P-1), because the data layer
  has none. Fixtures used below: routine name `MORNING FASTED STUDY BLOCK` (26 chars),
  profile name 22 chars — renames, labelled as fixtures, not seeds.

---

## 3. FINAL COPY TABLE — exact strings + max-length behaviour per line

Priority inside every line is the same: **the verb/fact segment never truncates;
the name segment yields first; fixed short segments (`ENDS`, `RUNNING NOW`,
the break qualifier) are never ellipsised — they move or the name shrinks.**
Every ellipsised line carries its full untruncated string as its TalkBack
`contentDescription` (R-11 ride-along, P-10).

| # | Line (exact template) | Worst case | Behaviour at the edge |
|---|---|---|---|
| 1a | `BLOCKING NOW — <LABEL> · ENDS HH:MM` | label capped 20 (§P-1) → **48 chars** | Rendered as segments (P-3), never one wrapping Text. fs ≤ 1.15: one line — `<LABEL>` segment is `weight(1f)`, `maxLines=1`, Ellipsis; verb, `· ENDS HH:MM` fixed, never cut. fs > 1.15 **or** 320 dp with break suffix: two-line stack — L1 verb + label (label ellipsises), L2 the fixed segments in order. At 360/1.0 the label shows ≈14 chars + `…`; at 320/1.0 ≈9 + `…`. `ENDS` never shows a past time (FORM §1.3: drop the segment, derivation-side). |
| 1b | `BLOCKING NOW — <LABEL>` (stopwatch, no end exists) | **35 chars** | Same segment law; fits one line everywhere except 320/1.3 (29-char budget) → label ellipsises to ≈13 chars. Never invent an end (no `ENDS —`, no `NO SET END` — absence of the segment *is* the print). |
| 1c | `BLOCKING NOW — <LABEL> · ENDS HH:MM · SAFE BREAK — PEEK APPS OPEN` | **78 chars** | The break segment is **never dropped and never ellipsised** (it is what keeps the sentence true while PEEK apps stand open). Two-line stack at every width/scale: L1 = verb + label + `· ENDS HH:MM` (label absorbs all pressure), L2 = `· SAFE BREAK — PEEK APPS OPEN` whole (30 chars — fits 320/1.0; at 320/1.3 it is the one segment allowed to shrink *type*, not words: it stays 13 sp while L1's label ellipsises further — words are never cut). |
| 1d | `NOT BLOCKING — NO SESSION RUNNING` | **33 chars** | The shortest variant and the audit's verbatim string (R-5). One line @ 360 & 320 at fs 1.0; at fs 1.3 it wraps after `—` as a whole-line wrap (no ellipsis — nothing in it may be cut; it is two facts, and the wrap point is the em-dash boundary). Slot height must tolerate 2 lines at fs 1.3 (see P-3: no fixed 28 dp slot at large scale). |
| 1e | `NOT BLOCKING — <LABEL> RUNNING, ENFORCEMENT NOT PROVEN` | label 20 → **67 chars** | Segment law as 1a: label ellipsises first; the tail `RUNNING, ENFORCEMENT NOT PROVEN` (32 chars) is fixed and moves whole to L2 in the stacked form. Never shorten the tail to `NOT PROVEN` alone — *enforcement* is the word that names what failed. |
| 1f | `NOT BLOCKING — <LABEL> RUNNING · SHIELD PROOF STALE` (**new form, V-1/P-2**) | label 20 → **64 chars** | As 1e. Replaces today's label-less `NOT BLOCKING — SHIELD PROOF STALE` (33 chars). The old short form is retired, not kept as a "compact variant" — one fact, one wording. |
| 1g | Fallback (no status supplied, session active): `NOT BLOCKING — SESSION RUNNING, ENFORCEMENT NOT PROVEN` | **54 chars** | Locked identical to 1e with generic label `SESSION` (V-2). Same segment law. The no-status + no-session fallback is exactly 1d. |
| 2 | ROUTINES header right: `<n> ARMED` | `99 ARMED` = 8 glyphs ≈ 50 dp @ 11.5 sp | Never truncates in any reachable state: title `ROUTINES` (≈50 dp) + right side fit 296 dp even at fs 1.3 and a 3-digit count (`999 ARMED` ≈ 56 dp). `0 ARMED` prints as-is — an absence stated as a fact, the screen's acquitted grammar. No patch; `FullBleedSectionHeader` is shared and is **not** to be modified for this line. |
| 3 | Routine cap: `ON` / `OFF` / `WON'T FIRE` (precedence: `OFF` if disabled; `WON'T FIRE` if enabled ∧ daysMask = 0; else `ON`) | `WON'T FIRE` = 10 glyphs ≈ 66 dp @ Anonymous Bold 11 sp (86 dp @ fs 1.3) | Cap **sizes to payload** (P-4): fixed 48 dp width dies; `widthIn(min = 48.dp)` + 10 dp horizontal padding → `WON'T FIRE` cap ≈ 86 dp @ fs 1.0, ≈106 dp @ 1.3; height stays 32 dp (text 14.3 sp at fs 1.3 still fits). The word never wraps, never shrinks, never abbreviates to `NO FIRE`/`WON'T` — a cut cap word is a wrong state. Middle density per FORM §3.3: surfaceVariant fill + outline + 55%-ink word (neither ON's solid ink nor OFF's recess). Name/meta column absorbs the growth (§2: 196 dp left @ 360, 150 dp @ 320/1.3 worst pairing). |
| 4 | Routine name: `<NAME>` upper-cased | unbounded (fixture 26) | `maxLines=1`, Ellipsis, in the `weight(1f)` column (P-5). Shows ≈28 chars @ 360 with an `ON` cap, ≈23 with a `WON'T FIRE` cap, ≈17 @ 320/1.3 with `WON'T FIRE`. Full name in `contentDescription`. (Type: R-12 items 7–8 already rule Atkinson Bold 15 sp, evicting the Sora 16 sp guest — that swap is Panzer/MONTEUR's; the maxLines law here holds under either face.) |
| 5 | Routine meta: `<DAYS> · <LOADOUT>` + ` · RUNNING NOW` | full worst: `MON TUE WED THU FRI SAT SUN · STUDY · RUNNING NOW` = **49 chars ≈ 353 dp** — fits *nowhere* | One line, `maxLines=1` (R-12). Shortening is **derivation-side, whole segments only** (P-6, 30-char budget ≈ 216 dp @ 12 sp — the @ 360 / `WON'T FIRE`-cap width; render ellipsis is backstop only): **(1)** `RUNNING NOW` never drops, never shortens; **(2)** the loadout segment drops first, whole, with its `·` (it has a second home: the expanded row's loadout chip); **(3)** if days + suffix still exceed budget, days print as `<n> DAYS` (exact days live in the expanded day chips — summary vs record, each in its home). Worked worst cases: the 49-char line above drops its loadout (rule 2) → 43 chars, still over → days compress (rule 3) → `7 DAYS · RUNNING NOW` (20 ✓). Standard cases fit whole or after rule 2 only: `MON–FRI · STUDY · RUNNING NOW` (29 ✓), `NO DAYS SET · STUDY` (19 ✓ — and a `WON'T FIRE` row can never carry `RUNNING NOW`: `isActiveNow` is false for mask 0 by construction). Ellipsis must never eat half a day code: the helper returns an already-fitting string, so the render ellipsis fires only at fs 1.3, mid-*loadout* at worst. |
| 6 | `NO DAYS SET` (meta, mask = 0) | 11 chars | The printed *cause*; row 3's cap is the *consequence*. Both print, once each — not duplication (FORM §3.3). No behaviour change. |
| 7a | Catalogue group headers: `PEEK — <n>` / `SHUT — <n>` / `OPEN — <n>` (R-1 end state) | ≤ 10 glyphs | `maxLines=1`, never truncates. Groups with no members print no header — the tally's `0 PEEK` above is the absence print (one home per fact; FORM's in-catalogue `NONE` is **not** adopted — recorded here so it isn't re-litigated). |
| 7b | PEEK contract, PEEK group header only: `PEEK — 2 MINUTES, THEN SHUT AGAIN · 20 MIN BETWEEN PEEKS` (R-6, derived — never hardcoded) | **56 chars** ≈ 353 dp @ 11.5 sp — overflows even 360 dp on one line | Prints as its **own line** under `PEEK — <n>`, Anonymous Pro 11 sp, variant colour. Wrap law: the only permitted break is at the `·` — render as two segments in a wrapping row, or `maxLines=2` with the break falling at the `·` (the string's longest pre-`·` segment is 33 chars ≈ 218 dp, fits 296 dp at fs 1.0; at fs 1.3 segment 1 is 283 dp — fits 296, breaks exactly at `·` on 320, on 360 it fits L1 whole at fs 1.0 only). Numbers derive from `PEEK_DURATION_SECONDS` (120 → `2 MINUTES`) and `PEEK_COOLDOWN_MILLIS` (20 min → `20 MIN`) with singular forms `1 MINUTE` / `1 MIN BETWEEN PEEKS` if a constant ever becomes 1 (P-9). Prints **once** on this screen; PARK's plate stays the settings home. |
| 8 | Reset strip (R-6 verbatim): `YOUR DAY, SEEDED — TAP A ROW TO OPEN IT · TIMES YOU SET ARE NEVER CHANGED` | **73 chars** ≈ 482 dp @ Anonymous 11 sp | Replaces `SCHEDULE RESET TO YOUR DAY — TAP ANY ROW TO CHANGE IT` wholesale — do not blend the two. **Never ellipsised, never truncated** (it is a truth claim about his times; a cut claim is a false one). Wraps whole: 2 lines @ 360 & 320 fs 1.0, 2 lines @ 360 fs 1.3, 3 lines @ 320 fs 1.3 — strip height grows, `maxLines` unset (or 3 as backstop). Residency (48 h / first edit, R-6) is machinery, not copy — noted, not specced here. |
| 9 | Search placeholder: `SEARCH APPS` (R-6) | 11 chars | Replaces `Filter apps…`. Placeholder dress unchanged (Atkinson 13 sp, variant colour — a placeholder is furniture, the *words* join the screen's voice). Truncation impossible at any width/scale. Typed text unchanged. |

**Out of my slice, untouched:** the tally line's wording (acquitted by the audit),
filter words, website rows, the ratchet banner's text (R-7 owns it), ±30 chips
(DELEGATED to Voss's batch-10 wheels — no copy touched in the time area).

---

## 4. Exact patches for MONTEUR (specifications — I edit no code)

All in `app/src/main/java/com/example/ui/ShieldBlockingStatus.kt` (SBS) or
`app/src/main/java/com/example/ui/screens/ShieldMatrixScreen.kt` (SMS), as marked.

- **P-1 (SBS) — cap the label in `shieldSessionLabel`.** After the label is built,
  cap the *name segment* so the whole label ≤ 20 chars and the ` SESSION` suffix is
  never cut: if `label.length > 20`, rebuild routine labels as
  `name.take(11) + "… SESSION"` and non-routine labels as `label.take(19) + "…"`.
  Add a pure-function test case: 26-char routine name → 20-char label ending `… SESSION`.
- **P-2 (SBS) — STALE gains the label (V-1).** In `computeShieldBlockingStatus`,
  the STALE branch headline becomes `"NOT BLOCKING — $label RUNNING · SHIELD PROOF STALE"`.
  Update the function's truth-table comment and any test asserting the old string.
- **P-3 (SMS) — status render law.** Replace the single status `Text` (Anonymous 13 sp,
  no limits) with a segment composition of `statusHeadline`: split on `" · "` —
  segment 0 (verb + label [+ `RUNNING, …` tail for UNPROVEN forms]) renders
  `Modifier.weight(1f, fill = false)`, `maxLines = 1`, `overflow = Ellipsis`;
  remaining segments render fixed, `maxLines = 1`, no wrap. Container: `Row` when
  `LocalDensity.current.fontScale <= 1.15f`, else `Column` (segments stack in order).
  Exception per table 1c: when the headline contains `SAFE BREAK`, always `Column`,
  break segment alone on L2. Keep testTag `shield_blocking_status` on the container.
  (Type/colour per FORM §2.2 are F-2/VOGT's call — if adopted in the same pass:
  Atkinson Bold 14 sp words, Anonymous Bold numerals, full ink for both verbs.)
- **P-4 (SMS, `RoutineRow`) — the cap tells the truth and sizes to it.** Word becomes
  `routineCapWord(routine.enabled, routine.daysMask)` (already in SBS — wire the
  existing import). Box: delete `.width(48.dp)`, use `.widthIn(min = 48.dp)` +
  `.padding(horizontal = 10.dp)`, radius 8 → 12 dp (R-12 item 7–8). Fill/foreground
  three-way: `ON` → ink fill / on-ink word; `WON'T FIRE` → `surfaceVariant` fill +
  `outlineVariant` border + word at 55% ink; `OFF` → as today.
- **P-5 (SMS, `RoutineRow`) — name and meta get their laws.** Name `Text`: add
  `maxLines = 1, overflow = TextOverflow.Ellipsis`. Meta `Text`: text becomes
  `routineMetaLabel(...)` (P-6) **without** the running suffix; the suffix renders as
  a second `Text` in a `Row` beside it — `routineRunningNowSuffix(routine, nowMillis)`
  as its own segment, Atkinson/Anonymous Bold, full ink, `maxLines = 1`, never
  ellipsised; the meta segment takes `weight(1f, fill = false)` + Ellipsis as backstop.
  `RoutineRow` therefore needs a `nowMillis: Long` parameter — pass the screen's
  existing `nowMillis` through at the call site.
- **P-6 (SBS) — add the pure meta helper.**
  `fun routineMetaLabel(daysLabel: String, loadoutLabel: String, runningNow: Boolean, budget: Int = 30): String`
  applying §3 row 5 in order: full `"$daysLabel · $loadoutLabel"`; if appending the
  suffix (14 chars incl. ` · `) or the string itself exceeds `budget`, drop the
  loadout segment; if days + suffix still exceeds, replace days with
  `"${dayCount} DAYS"` (pass `dayCount: Int` = mask bit count). `routineDaysLabel` /
  `routineLoadoutLabel` stay in SMS and are passed in as strings — no data-layer move.
  Unit-test the three worst cases from §3 row 5.
- **P-7 (SMS) — reset strip.** Replace the strip's string with §3 row 8 verbatim.
  No `maxLines` below 3, no overflow setting — wrapping is its law.
- **P-8 (SMS) — search placeholder.** `"Filter apps…"` → `"SEARCH APPS"`. One line.
- **P-9 (SMS, when the R-1 grouped catalogue lands) — PEEK contract header.**
  Under the `PEEK — <n>` group header, one line built as
  `"PEEK — ${mins} MINUTE${s}, THEN SHUT AGAIN · ${cd} MIN BETWEEN PEEKS"` where
  `mins = MonolithRepository.PEEK_DURATION_SECONDS / 60`,
  `cd = MonolithRepository.PEEK_COOLDOWN_MILLIS / 60_000`, plural `S` omitted when
  the value is 1. Segment/wrap law per §3 row 7b. Nowhere else on the screen.
- **P-10 (SMS) — semantics for ellipsised lines (R-11 ride-along).** Status container,
  routine name, and routine meta row each get
  `Modifier.semantics { contentDescription = <full untruncated string> }`.

---

## 5. Paper verification checklist (for MONTEUR / Ren on device — I run nothing)

1. 320 dp width + fontScale 1.3, session = routine fixture `MORNING FASTED STUDY BLOCK`,
   timed, in safe break → status shows two stacked lines, label ellipsised,
   `· SAFE BREAK — PEEK APPS OPEN` whole on L2, `ENDS` present and not past.
2. Same device, no session → `NOT BLOCKING — NO SESSION RUNNING`, wraps only at the
   em dash, nothing cut.
3. Routine fixture: enabled, daysMask = 0 → cap reads `WON'T FIRE` whole, header
   count does **not** include it in `ARMED`; meta reads `NO DAYS SET · STUDY`.
4. Routine fixture: custom 6-day mask, window live → meta fits one line, ends with
   `· RUNNING NOW` whole; expand the row — day chips show the exact six days.
5. Reset strip at 320/1.3 → three wrapped lines, final word `CHANGED` visible.
6. TalkBack over rows 1/4/5 → full untruncated strings read out (P-10).

---

## 6. Draft GitHub comment (for VOGT to post)

> — SAUM (FINISHER), for VOGT's issue #7 cell.
>
> Words & edges delivered: `studio/cells/issue-7/finisher-saum.md` (DECLARED — paper only, uncompiled, no code edited; MONTEUR is sole code editor).
>
> Final copy table for every word the R-5/R-6 fixes touch, each with a measured max-length law (budgets derived at 360/320 dp × fontScale 1.0/1.3, Anonymous Pro at 0.60 em): the status sentence in all seven forms (BLOCKING NOW ± ENDS ± SAFE BREAK, NOT BLOCKING — NO SESSION RUNNING, UNPROVEN, STALE, and the screen fallback), `ARMED`, `ON`/`OFF`/`WON'T FIRE`, `RUNNING NOW`, routine name + meta, the PEEK contract line, the R-6 reset strip, and `SEARCH APPS`.
>
> Decisions VOGT should note: (1) one-voice table locking the five time-words as non-synonyms — `RUNNING NOW` is a schedule fact, `BLOCKING NOW` a proved enforcement claim, and a row showing one under a headline showing the other is a missed fire surfacing, not an inconsistency to hide. (2) STALE was the one inconsistent variant — it dropped the session label UNPROVEN keeps; ruled to `NOT BLOCKING — <LABEL> RUNNING · SHIELD PROOF STALE` (P-2). (3) The screen fallback is character-identical to UNPROVEN with the generic label `SESSION` — locked as law (V-2). (4) Safe-break suffix: the code's `PEEK APPS OPEN` stands over FORM's shorter draft (V-4).
>
> The edge findings that bite: routine/profile names are unbounded in the data layer, so the session label is capped at the print layer (≤20 chars, name segment only, `… SESSION` never cut — P-1); the status Text as currently rendered has no maxLines/overflow and wraps mid-sentence at 320 dp / fontScale 1.3, so it gets a segment law — verb and fixed segments (`ENDS`, `RUNNING NOW`, the break qualifier) never ellipsise, the label absorbs all pressure, stacking to two lines at large scale (P-3); `WON'T FIRE` (10 glyphs ≈ 66 dp) cannot fit the row's fixed 48 dp cap, so the cap sizes to payload with the middle-density treatment (P-4); the worst meta line is 49 chars ≈ 353 dp, so shortening is derivation-side and whole-segment only — loadout drops first, days compress to `<n> DAYS`, `RUNNING NOW` never drops (P-5/P-6). Remaining R-6 words specced verbatim: reset strip (never truncated, wraps to ≤3 lines), `SEARCH APPS`, and the PEEK contract on its own header line with the only permitted break at its `·` (P-7/P-8/P-9). Flagged outside my slice for VOGT: the status line's `error` colour vs FORM §2.2's full-ink ruling (F-2).
>
> Ten exact patches (P-1…P-10) and a six-point on-device checklist are in the file. Close: DECLARED.
>
> — SAUM

---

*SAUM, FINISHER 3, VOGT's issue #7 cell — Oct 30, 2026. DECLARED: words-and-edges
spec only; no app code edited, nothing compiled, no tests run, no git operations.*
