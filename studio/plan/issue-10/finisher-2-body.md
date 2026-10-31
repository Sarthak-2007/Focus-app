# Issue #10 — Receipt (audit fixes) · FINISHER 2 (body & total)

**Seat:** Finisher 2 — body & total, Zettel's cell for GitHub issue #10, "Main-app pass —
Receipt (audit fixes)". The slip is a LOVED surface: this document is finishing, not
redesign. Every call below preserves the protected character (mono ledger, long-form
durations, zero buttons/CTA, omission law) and lands only the audited cures in my slice.
**Authority:** `studio/plan/AUDIT-receipt.md` (Zettel, ruled Oct 30 01:15 IST; read at
`~/workspace/clone-inspect/builds/wt-ci/studio/plan/AUDIT-receipt.md`) items **5, 7, 16**;
cures **A1, A2, A6**. Composes with `work/designer.md` in this cell (§2 total, §4 HELD) —
where the designer already ruled (placement, RAN type), this spec finishes that ruling
with exact spacing/rule/type values and does not reopen it.
**Code grounded in this clone** (`agent/issue-10`): all line citations are to
`app/src/main/java/com/example/ui/components/ReceiptSheet.kt`,
`app/src/main/java/com/example/data/ReceiptState.kt`, and
`app/src/main/java/com/example/data/MonolithRepository.kt` in this tree.
**Hard rules honoured:** paper only — nothing compiled, nothing tested, by me or asked
of the builder as my proof. No new colour, no new font family, no image, never green.

---

## 0. Verification — the total's field is already carried (audit item 5)

Verified in this tree, end to end:

| Hop | Evidence |
|---|---|
| Row derivation | `MonolithModels.kt:414–430` — `spanElapsedSeconds` getter on the session row |
| Assembly | `MonolithRepository.kt:1640` — `sealedElapsedSeconds = row.spanElapsedSeconds` |
| Pending contract | `ReceiptState.kt:147` — `val sealedElapsedSeconds: Int` on `PendingReceipt` |
| Publish | `MonolithRepository.kt:1725` — carried into `ReceiptState` |
| Rehydrate | `MonolithRepository.kt:1777` — carried again; a restored slip prints the same total |
| Issued contract | `ReceiptState.kt:176` — `val sealedElapsedSeconds: Int` on `ReceiptState` |
| Sheet reads | **0** — `grep -c sealedElapsedSeconds ReceiptSheet.kt` returns 0. The field arrives and is printed nowhere: exactly the audit's charge |

Consequences for the builder: **no state addition, no migration, no new formatter.**
`longSpan(seconds)` already exists at `ReceiptSheet.kt:204–211` (ceiling-to-minute
dialect: `50 MIN`, `1H 00M`). A1 is a print cure only, ~4 lines as priced.

The same carriage check for my other lines: `plannedSeconds` (`ReceiptState.kt:146,175`;
sheet `ReceiptSheet.kt:115–122`), `outcomeWord` (`ReceiptState.kt:138,170`; minted at
`MonolithRepository.kt:1683–1689`), `routineLabelWithin` (`ReceiptState.kt:150,179`;
sheet `ReceiptSheet.kt:126–128`; string built frozen for routine rows at
`MonolithRepository.kt:1578–1579`, hand-armed at `:1673–1674`).

---

## 1. THE FINAL LINE ORDER — my region, exact

This is the order the finished slip prints, top to bottom. Lines owned by other seats
are shown in ⟦brackets⟧ only to fix my lines' positions; their internals are not
specced here. Conditional lines print only under the gate stated in §2–§5; an absent
line leaves **no slot, no spacer, no rule** behind it (§6.3 closure law).

```
⟦HEAD — title (his intention) ····· № 048⟧
ARMED 09:00 · PLANNED 50 MIN                  ← §2.1  (PLANNED segment omitted whole on FLOW)
ENDED 09:50                                   ← §2.2 + §3 (suffix only when a word survives §3)
────────────────────────────────────────────  ← §4.2  total rule (TOTAL section's first element)
RAN 50 MIN                                    ← §4.1  TOTAL — bold mono
WITHIN DAY BLOCKS · 09:00–13:00               ← §2.3  (only when routineLabelWithin ≠ null)
⟦HELD — OMITTED whole until C1 lands (designer §4.2); BREAK BANKED — KILLED, item 10.
  Neither occupies a slot or a gap.⟧
⟦deflection lines, HH:mm · app — A10's seat; position unchanged⟧
THE SHIELD WAS OFF FOR PART OF THAT SESSION   ← §5.1  (only when shieldWasOff)
SHIELD — STILL BLOCKED (SERVICE DIDN'T STOP)  ← §5.2  (only when faulted; wording is B4's seat)
⟦FOOTER — A3's seat; position and one-line law unchanged⟧
```

Worked variants (form only):

- **Clean manual:** `ARMED 09:00 · PLANNED 50 MIN` / `ENDED 09:50` / rule / `RAN 50 MIN` — no outcome suffix, no trailing separator (§3.1).
- **FLOW:** `ARMED 09:00` / `ENDED 12:50 · FLOW ENDED` / rule / `RAN 3H 50M` — RAN is the only duration on the slip and is **not** promoted, enlarged, or moved (designer D2.5).
- **Shield-gap manual:** `ENDED 09:50` bare; the gap fact prints exactly once, as the §5.1 statement.
- **Sleep stand-down:** `ENDED 07:00 · STOOD DOWN` / rule / `RAN …` subject to the §4.4 verification gate.
- **Faulted:** title row collapses per A9's seat; my lines are unchanged — RAN still prints (designer D2.6), fault statement at §5.2 type.

---

## 2. The body lines, one by one

All body lines keep the ledger voice exactly as shipped — `ReceiptMonoLine`
(`ReceiptSheet.kt:188–197`): **Anonymous Pro, 11sp, letterSpacing 1sp,
`onBackground`** (deflection dimming is A10's seat; untouched here). No body line
changes size, family, weight, or ink in this spec except RAN (§4.3) and the two
statements (§5).

### 2.1 ARMED / PLANNED — form unchanged

- Exact form, as today (`ReceiptSheet.kt:116–122`):
  `ARMED {hhmm(armWallMs)} · PLANNED {longSpan(plannedSeconds)}`.
- `plannedSeconds == null` (FLOW, RC-02): the PLANNED segment is omitted **whole** —
  the line is `ARMED {hhmm}` with no trailing `·`, no placeholder. Existing behaviour;
  it is restated because §3 applies the same punctuation law to ENDED.
- **Dependency, not a print change:** the PLANNED *figure's source* is audit item 4 /
  cure-group C3 (Frist's seal-time `sealedSpanTargetSeconds`). This spec does not
  re-source it and the builder must not improvise a source here; the print form above
  is final regardless of which sealed figure C3 delivers.

### 2.2 ENDED — the witnessed end, suffix conditional

- Base form: `ENDED {hhmm(endedAtWallMs)}` — `endedAtWallMs` is the RC-01 stamped
  end; the line is the slip's best line (audit item 6) and its base never changes.
- The suffix is built **conditionally** (replaces the unconditional interpolation at
  `ReceiptSheet.kt:123–125`):

  ```
  text = if (receipt.outcomeWord.isBlank())
             "ENDED ${hhmm(receipt.endedAtWallMs)}"
         else
             "ENDED ${hhmm(receipt.endedAtWallMs)} · ${receipt.outcomeWord}"
  ```

  A killed word must kill its punctuation with it: no trailing `·`, no double space,
  no dangling separator ever prints.

### 2.3 WITHIN — verbatim label, new neighbour

- Exact form unchanged: the sheet prints `receipt.routineLabelWithin` verbatim
  (`ReceiptSheet.kt:126–128`); the string itself is pre-built as
  `WITHIN {NAME} · HH:mm–HH:mm` (middle dot after the name, en-dash between the
  times — `MonolithRepository.kt:1578–1579`). The sheet never rebuilds, re-cases, or
  re-punctuates it.
- **Position is the only change:** today it sits directly under ENDED; in the final
  order (§1) it sits directly **under RAN**, because the total inserts between ENDED
  and it. Relative order against everything below it (deflections, statements) is
  unchanged.
- Gate unchanged: prints iff `routineLabelWithin != null`. No WITHIN, no gap (§6.3).
- **Dependency:** the label's freeze truth is audit item 8 / C2 (RC-03). Print side
  is final; if C2 slips a batch, WITHIN still prints in this slot from the carried
  field — the item stays open on the data side, not on the paper.

---

## 3. Outcome words — cure A2 (audit item 7), finished

### 3.1 The kill table — every word the slip can carry today

The vocabulary is minted in exactly one place: `receiptOutcomeWord(row, kind)`,
`MonolithRepository.kt:1683–1689`. The kills land **there**, and the sheet stays
vocabulary-free (it knows only blank vs non-blank, §2.2). Single-sourcing matters:
the same word is what the claim persists as `lastReceiptOutcome`
(`MonolithRepository.kt:1481,1515`; store described at `ReceiptState.kt:10–18`), so
source and print can never disagree, on first printing or on rehydration (which
re-derives through assembly and discards the stored word anyway).

| Kind / row fact (source: `MonolithRepository.kt:1683–1689`) | Prints today | FINISHED ruling |
|---|---|---|
| Manual finish, `ELAPSED_CLEAN` (else-branch) | `ENDED` | **KILLED.** `receiptOutcomeWord` returns `""`. ENDED line is bare (§2.2). Tautology: the line already says ENDED |
| Manual finish, `ELAPSED_SHIELD_GAP` (else-branch) | `ENDED · SHIELD GAP` | **KILLED.** Returns `""`. The gap fact prints exactly once on the slip — the §5.1 statement (audit items 7 + 12) — never twice in two dialects |
| `ROUTINE_SLEEP_STANDDOWN` | `SLEPT THROUGH` | **KILLED, replaced:** returns `"STOOD DOWN"`. Line prints `ENDED {hhmm} · STOOD DOWN`. The machine's act, in the machine's voice — it stood down; he did not fail to wake |
| `ROUTINE_ATTENDED` | `ATTENDED` | **KEEP**, unchanged |
| `FLOW_FINISH` | `FLOW ENDED` | **KEEP**, unchanged (thin but true; its cure is the §4 total, not a new word) |
| `ROUTINE_PARTIAL` | `PARTIAL` | **WAITS — not an A2 kill, do not improvise.** Audit item 7: PARTIAL is STATE-ONLY and prints only with its evidence (`PARTIAL · {attended} OF {window}`), which needs a state addition (B5, Ren's ruling). Until B5 lands, `receiptOutcomeWord` returns `""` for this kind — a bare grade with its evidence redacted never prints. When B5 lands, the word returns through this same function with its evidence string; the sheet's §2.2 conditional needs no change |

### 3.2 Hard edges

- The strings `SLEPT THROUGH`, `ENDED · SHIELD GAP`, and a bare suffix `ENDED`
  must occur **nowhere** in the print path after this cure. (The stored session
  token `SessionOutcome.ELAPSED_SHIELD_GAP`, `ReceiptState.kt:36`, is a data token,
  not a print word — it does not change, and `shieldWasOff` keeps deriving from it.)
- The sheet must **not** grow string comparisons against the killed words
  (no `if (outcomeWord == "ENDED")` in `ReceiptSheet.kt`): vocabulary lives in the
  one minting function, or the next word drifts in two places.
- Test-fixture note for the builder (not my proof to run): `ScreenshotHarnessTest`
  hand-builds a state with `outcomeWord = "ENDED"` — that fixture speaks the old
  dialect and should be updated to `""` for clean-manual frames, or the harness
  will keep photographing a word production can no longer mint.

---

## 4. THE TOTAL — cure A1 (audit item 5), finished

### 4.1 Exact line

```
RAN {longSpan(receipt.sealedElapsedSeconds)}
```

One line, e.g. `RAN 50 MIN`, `RAN 1H 00M`, `RAN 3H 50M`. The formatter is the sheet's
existing `longSpan` (`ReceiptSheet.kt:204–211`), reused verbatim — short forms
(`50M`, `1:00`) are forbidden anywhere on the slip. The figure is the **carried field
printed verbatim**: the sheet must never recompute a span from
`endedAtWallMs − armWallMs` or any wall arithmetic (designer D2.4). If RAN and the
wall stamps ever disagree by a minute, the field wins on paper and the disagreement
is a data bug to file, not a print bug to paper over.

### 4.2 Placement and the rule

- **Slot:** directly under the ENDED line, over its rule, in every slip kind —
  manual, FLOW, routine, faulted. The total never moves (designer D2.2): plan
  against ran must be readable in one glance at the head of the slip, never buried
  under a variable-length deflection list.
- **The rule:** a single full-inner-width hairline, the house idiom —
  `HorizontalDivider(thickness = 1.dp, color = scheme.outlineVariant)` (same
  construction as `AirlockAndDisarmOverlays.kt:383`, `DrawerScreen.kt:421`). No new
  colour, no double rule, no thickened "total" rule: the total's weight comes from
  its slot, its rule, and its bold strike (§4.3), not from decoration.
- **The rule is the TOTAL section's first element** (see §6): 16dp of air above it
  (the inter-section gap, measured ENDED→rule), the 1dp rule, then 8dp to RAN
  (intra-section). The rule belongs to the total, the way a receipt rules *above*
  its total — there is deliberately **no rule under RAN**; a boxed-in total reads
  as a form field, not a sum.

### 4.3 Type — the total's strike

**Anonymous Pro, Bold, 11sp, letterSpacing 1sp, `onBackground`, `maxLines = 1`.**
Implement as a `bold` parameter (default `false`) on the existing `ReceiptMonoLine`
(`ReceiptSheet.kt:188–197`); RAN alone passes `true` in my region.

- **Not 13sp.** The designer considered and rejected it (D2.3), and this spec holds
  that rejection as final: 13sp is item 16's *interruption* size, reserved for the
  two machine statements (§5). RAN is not an interruption; a 13sp total would
  compete with the Atkinson title, the one human voice on the paper.
- **Not Atkinson.** The total is a ledger figure; it speaks mono like every other
  figure on the slip.

### 4.4 Gates and the one flagged exception

- RAN prints on manual, FLOW, routine ATTENDED, routine PARTIAL, and faulted slips.
  The field carries no uncaptured sentinel (plain seconds, always carried), so
  there is no omission case among those kinds: `RAN 1 MIN` and even `RAN 0 MIN`
  print as the machine's own figure (designer D2.7).
- **Flagged exception — `ROUTINE_SLEEP_STANDDOWN` (builder verifies before merge):**
  the field derives from `row.spanElapsedSeconds` (`MonolithModels.kt:414–430`),
  whose terms are tick-accrued (`elapsedSeconds`, block index, break seconds). A
  stand-down row — a SLEEP window that closed with no attendance — may carry a
  structural `0` while its window ran for hours, and `RAN 0 MIN` on that slip would
  mint a fresh falsehood on exactly the slip §3 renames. This is the designer's
  Dependency 2 (`work/designer.md` §6.2), carried here as a gate: **verify the
  field's value on a stand-down row; if it is not a truthful lived span for that
  kind, stand-down slips omit RAN — and its rule with it (§6.3) — under the
  omission law until the field is truthful there.** All other kinds are unaffected.

---

## 5. The two bold machine statements — voice cure (audit item 16)

The two bold statements are the slip's only interruptions, and item 16 moves both
out of the title's Atkinson into the typewriter's double-strike. Both currently sit
at Atkinson Bold 12sp with no tracking (`ReceiptSheet.kt:145–165`). Finished spec
for **both**:

| Property | Finished value |
|---|---|
| `fontFamily` | `AnonymousProFamily` (was `AtkinsonHyperlegibleFamily`) |
| `fontWeight` | `FontWeight.Bold` (unchanged) |
| `fontSize` | **13sp** (was 12sp) |
| `letterSpacing` | **1sp** (was unset/default) |
| `lineHeight` | **18sp** — new, see wrap law below |
| `color` | `scheme.onBackground` (unchanged) |

The title alone keeps Atkinson: the human voice belongs to his intention; the
machine's interruptions speak machine, at a size that interrupts.

### 5.1 Shield-gap statement (position and text unchanged)

`THE SHIELD WAS OFF FOR PART OF THAT SESSION` — exactly as at
`ReceiptSheet.kt:147`, gated on `receipt.shieldWasOff` as today. Text is binding
(acquitted by all six audit seats); only its voice changes. After §3.1 this line is
the **sole** carrier of the shield-gap fact on the slip.

### 5.2 Faulted statement (type only — wording is B4's seat)

`SHIELD — STILL BLOCKED (SERVICE DIDN'T STOP)` — exactly as at
`ReceiptSheet.kt:159`, gated on `receipt.faulted` as today, still the one lawful
receipt-only fault line. This spec changes its type only; its plain-wording and
retry-fact cures are audit item 14 / B4 and are **not** pre-empted here — when B4
lands its wording, it lands in this §5 voice without further type work.

### 5.3 Wrap law for both statements

At 13sp mono + 1sp tracking, both statements exceed the slip's inner width (the
62-char footer at 9sp/0.25sp already measures ~326dp against ~348dp —
`ReceiptSheet.kt:166–174`) and **will wrap to two lines**. That is accepted and
finished, under three rules: **never truncate** (no `maxLines` cap, no ellipsis —
a truncated statement is a false statement), **never shrink** (no font-size
fallback to force one line), and wrapped lines sit on the 18sp line height above
so the double-strike reads as one statement, not two ledger lines. If both
statements ever print on one slip, they are one VOICE section: 16dp into the
section, 8dp between the two statements (§6).

---

## 6. Section rhythm — item 16, finished for my region

### 6.1 What is wrong

The column today is one uniform `Arrangement.spacedBy(8.dp)`
(`ReceiptSheet.kt:83`) under 20dp padding (`:81`), with zero rules anywhere in the
file: a document with no sections. Item 16's law: **8dp within sections, 16dp
between, sections ruled (head / body / total / foot).**

### 6.2 The finished structure (head-of-slip region, mine to spec)

The builder replaces the single uniform arrangement, in my region, with nested
columns — outer arrangement `spacedBy(16.dp)`, each section an inner
`Column(spacedBy(8.dp))`:

| Section | Contents (in order) | Internal gap | Gap before it |
|---|---|---|---|
| ⟦HEAD⟧ | ⟦title · № row⟧ | — | — (other seat) |
| **BODY-1 · TIME LEDGER** | ARMED… line; ENDED… line | 8dp | 16dp |
| **TOTAL** | total rule (§4.2); RAN line | 8dp (rule→RAN) | 16dp (ENDED→rule) |
| **BODY-2 · CONTEXT** | WITHIN line (when present) | — | 16dp |
| ⟦BODY-3 · RECORD⟧ | ⟦deflection lines — A10's seat⟧ | ⟦8dp⟧ | ⟦16dp⟧ |
| **VOICE** (when any statement prints) | §5.1 statement; §5.2 statement | 8dp | 16dp |
| ⟦FOOT⟧ | ⟦footer — A3's seat⟧ | — | ⟦16dp⟧ |

Rules of the structure:

1. **Gaps are measured between printed neighbours, edge to edge**, in the
   `spacedBy` sense the code already uses. "16dp between sections" is air; the
   total rule's 1dp is the TOTAL section's first element and does not consume
   either gap around it (ENDED →16dp→ rule →8dp→ RAN).
2. **Exactly one rule is added by this spec** — the total rule. The remaining
   section boundaries in my region are carried by the 16dp gap alone; the head
   and foot rules of item 16's full system belong to the A6/head and A3 seats.
   If the full re-rhythm slips a batch, RAN still ships with its rule (designer
   §5) — the total never waits on it.
3. **No uniform-8 tail:** the builder must not leave the old flat `spacedBy(8.dp)`
   governing lines below my region while the head is sectioned — the outer 16dp
   arrangement, once introduced for the head, is the column's arrangement; the
   downstream sections' owners fill their inner columns under it.
4. The column's outer 20dp padding (`ReceiptSheet.kt:81`) is unchanged by this
   spec — slip shape, radius, border, and anchor are A5's seat (items 15, 17).

### 6.3 Closure law — absent lines leave nothing behind

Every gap and rule in this spec attaches to a **printed** neighbour. When an
optional line is absent — WITHIN null, a statement's gate false, HELD omitted,
PARTIAL waiting (§3.1) — its section, spacer, and (for RAN under §4.4) its rule
vanish with it, and the 16dp section gap is measured between the two nearest
**printed** lines across the boundary. No blank ledger row, no orphan rule, no
double gap ever marks where a line would have stood. This is the slip's omission
law applied to rhythm, and it is what keeps a FLOW slip (no PLANNED, no WITHIN)
as tight as a full routine slip.

---

## 7. What this spec deliberately does not touch

- **Head** (title/№ print, faulted-row collapse — items 2, 14/A9), **shape &
  anchor** (items 15, 17/A5), **arrival** (item 18/A7), **dismissal** (item 19/A8):
  other seats. My §1 order and §6 gaps are written to compose with them unchanged.
- **Deflection print treatment** (one ink, hanging indent, silence rules — item 11
  print half/A10) and the deflection **outcome filter** (A4, designer §1): other
  seats; positions in §1 are theirs to fill.
- **HELD** (item 9/C1): omitted whole until the SHUT/PEEK split lands (designer
  §4) — it appears in §1 only as an absence.
- **BREAK BANKED** (item 10): killed as named and as arithmeticked; no line in
  §1 replaces it.
- **Footer** (item 13/A3, W-43): noun cure, one-line guard, and night ink are
  A3's seat, Ren's merge custody.
- **Date on cross-midnight slips** (item 3/A11): the ARMED line's form in §2.1 is
  the same-day form; A11's date cure composes on top of it, not through this spec.
- **TEAR / SLIP BOOK** (item 20, group B, parked to issue #21): nothing here
  pre-builds them.

## 8. Dependencies carried forward (for Zettel / Ren)

1. **C3 — PLANNED's seal-time source** (item 4): §2.1's print form is final; the
   figure's source is Frist's lane and must land in the same merge or item 4
   stays open.
2. **C2 — WITHIN freeze** (item 8): §2.3's print side is final; RC-03's merge
   closes the truth side.
3. **B5 — PARTIAL evidence** (item 7): §3.1 holds the word silent until the
   attended/window figures are carried to the slip; the return path is through
   `receiptOutcomeWord` alone, no sheet change needed.
4. **B4 — fault-line wording** (item 14): lands in §5.2's finished voice.
5. **§4.4 standdown verification**: builder confirms `spanElapsedSeconds` on a
   ROUTINE_SLEEP_STANDDOWN row before merge; the omission fallback is specced
   if the field proves structural there.

## 9. Paper acceptance checks (for the builder's proof — not run by me)

1. Clean manual 50 MIN: slip reads ARMED…PLANNED / `ENDED 09:50` (bare) / rule /
   `RAN 50 MIN` bold — grep the print path: no `SLEPT THROUGH`, no
   `SHIELD GAP` suffix, no trailing `·` on any ENDED line.
2. Shield-gap manual: ENDED bare; the gap fact appears **once**, as the §5.1
   statement in Anonymous Pro Bold 13sp/1sp.
3. Sleep stand-down: `ENDED … · STOOD DOWN`; RAN present only per §4.4's verdict.
4. FLOW: `ARMED` alone, `· FLOW ENDED` kept, RAN present in its fixed slot.
5. Routine: WITHIN sits under RAN, verbatim, en-dash and middle dot intact.
6. Seeded field-vs-wall disagreement (sealed field one minute off the wall
   difference): the slip prints the **field**.
7. Rhythm: with WITHIN and both statements absent, no orphan spacer or rule
   survives; section gaps measure 16dp between printed neighbours, 8dp within.
8. Both statements wrap to two lines at 13sp without truncation, on 18sp line
   height; RAN never wraps and never exceeds one line.

— Finisher 2 (body & total), Zettel's cell (issue #10). Paper only; nothing compiled.
