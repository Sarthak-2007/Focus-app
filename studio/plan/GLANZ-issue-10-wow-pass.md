# GLANZ — Issue #10 (Receipt): the wow pass

**Seat:** Glanz, wow-level designer (dedicated-100). **Primary issue.**
**Lens:** the bar is *better than anything — commercial level and above*. The slip is
compared against the best printed artefacts in commercial software and against real
thermal print. Anything merely fine is sent back with the gap named and a priced way
across it. Truth work (waves 1–2, the cell's four items) is acquitted and not re-tried
here; this pass is only about the distance from *a correct slip* to *an untouchable one*.
**Grounding:** `ReceiptSheet.kt` / `ReceiptState.kt` on `agent/issue-10` (committed HEAD
04e25a1 + Nachdruck's in-flight working tree, re-checked during the pass), the cell specs
in `studio/plan/issue-10/` (Finisher 2, Finisher 3, Feel 5, Finisher 5), and the issue
thread incl. Wirbel's and the Intentionality Designer's seats.
**Laws honoured:** nothing compiled; fonts Sora / Atkinson Hyperlegible / Anonymous Pro;
never green; no AI images; Blockit is a measuring stick for discipline, never a skin.

---

## Verdict in one paragraph

The slip's *truth* is gallery-grade; its *print craft* is not yet. The paper is a leaked
Material default, the rules are UI grey on a one-ribbon document, the total is body-size
and rounds itself up, the column has no section rhythm, and the document never closes.
Five priced fixes below, in merge priority. Nachdruck's in-flight rounds (hanging indent,
QUIET spans, SITE distinction) are credited — they closed two of this pass's draft charges
while it was being written.

## 1 · THE PAPER (merge priority 1)

`ReceiptSheet.kt:146` paints `.background(scheme.surfaceBright)`; the theme maps no
member of that slot, so the stock is Material 3 baseline: `#FEF7FF` (purple-tinted) by
day, `#3B383E` grey at night (Feel 5 §1.1, re-verified this pass — `surfaceBright`
appears nowhere in the theme files).

Commercial comparison: no serious product ships a surface in a framework default. A
receipt's stock is a brand constant — Monzo's, Things', and real thermal paper are all
recognisable before a word is read.

**Fix (Feel 5 S2.1, already specced, still unbuilt):** paint the constant
`TokenColorSurfaceBright #FAF6F0` (`Color.kt:15`, alias `WarmIvoryPaper`),
frame-invariant, ledger ink `TokenTextColorPrimary` in both frames. One line.

## 2 · ONE RIBBON (merge priority 1, with §1)

The one-ink law was enforced for glyphs (the `dim` deletion, structural — kept and
praised) and quietly exempted for everything else:

| Element | Built ink | Ruling |
|---|---|---|
| Ledger glyphs | `onBackground`, α 1.0 | ✔ the ink |
| Footer | `onBackground.copy(alpha = 0.72f)` | ✘ a second ink by alpha |
| Total / deflection / footer rules | `HorizontalDivider(color = scheme.outlineVariant)` | ✘ a third ink — a UI grey |
| QUIET span hairlines (in-flight) | `scheme.outlineVariant` | ✘ same grey, newly spread |

Finisher 3 §2.1 ruled the section rule **solid `onBackground`** — "a printed line, not a
UI divider." Finisher 2 §4.2 prescribed `outlineVariant` for the total rule. The code
took the grey for all of them; Nachdruck's span-rule comment now defends grey as "one
rule ink" — unification in the wrong direction.

**Fix:** every rule on the slip prints solid `onBackground` hairline (1dp). The total
rule's distinction is its slot and the struck total beneath it (§3), never paleness.
Footer prints full ink at 9sp (smallness is its hierarchy); any future mute must be a
measured, named ink tier per Feel 5, never an unexamined 0.72.

## 3 · THE TOTAL (merge priority 2)

Two defects in the slip's most trusted line, both against Finisher 2 §4.3 / designer
D2.3, which this pass sends back:

**(a) Presence.** RAN is Anonymous Pro Bold at **11sp** — body size. On all commercial
print the total is the largest type on the paper (thermal double-height exists for
exactly this). The refusal of 13sp+ ("reserved for interruptions") rations size by
category; interruptions are sentences, the total is a figure — they cannot compete.
On FLOW slips RAN is the only duration and is still deliberately unpromoted (§1 of
Finisher 2's own order). **Fix:** RAN at **15sp Bold / 1sp tracking** (the title's size,
the ledger's face), `maxLines = 1`; floor 14sp if a render fails. One constant.

**(b) Exactness.** `longSpan` ceilings: 61s → `RAN 2 MIN` (+98%). The ceiling dialect is
lawful for PLANNED (a promise) and QUIET (approximate silence); a *total* that overstates
is a falsehood in the position of highest trust (the cell's Critic Attack 3, recorded and
shrugged at close). **Fix:** RAN alone gets an exact formatter extending the dialect one
rung down — seconds print only when non-zero: `RAN 61 SEC`→ in dialect: `RAN 1 MIN 1 SEC`,
`RAN 3H 50M`, `RAN 1H 01M 11 SEC`. ~6 lines; tests at 61s / 3600s / 3671s.

## 4 · RHYTHM (merge priority 3)

Built column: one uniform `Arrangement.spacedBy(8.dp)` plus ad-hoc spacers. Finisher 2
§6 specced the section system — 16dp between sections, 8dp within, rules as section
elements, closure law (absent lines leave no slot, no orphan rule, no double gap) — and
it remains unbuilt in HEAD and in the in-flight tree. Uniform spacing is spreadsheet
rhythm: no line is more related to any other, so the eye gets no map of the document.
**Fix:** §6 as specced, whole column (§6.2 rule 3 forbids the head-only half-measure).

## 5 · THE ENDING (merge priority 5)

After the footer the column stops: 20dp padding, square cut. Every real printed artefact
closes — end mark, tear line, colophon. The slip's last impression is an administrative
sentence, then void. (TEAR-the-gesture stays parked, #21; an end *mark* is print.)
**Fix:** below the footer rule, 16dp of air, one centred Anonymous Pro 9sp full-ink line:
the № repeated as colophon — `· № {numberPrint} ·`. It closes the document and gives the
№ (flagged by the Intentionality Designer as addressing nothing) a second, ritual job:
head-mark and colophon, the serial you cite. One `Text`.

## Credited, not charged

- **Hanging indent, QUIET span rules, `· SITE`** — Finisher 3 §§2.2/3/4, implemented by
  Nachdruck in-flight during this pass; my draft charges withdrawn on the issue thread.
- **Wirbel's QUIET CHAPTERS / denomination** — independent convergence on Finisher 3 §3
  and on this pass's §3(a) respectively; the denomination's "exact, never rounded" is §3(b).
- **Omission law, listed-never-summed, the killed HELD line, dismissal dwell** — acquitted;
  this pass touches none of them.

## Acceptance for this pass (paper/render checks, for the merge)

1. Slip stock measures `#FAF6F0` in day and night renders; no `surfaceBright` read in the sheet.
2. Grep: no `outlineVariant`, no `copy(alpha` in `ReceiptSheet.kt`; all rules 1dp `onBackground`.
3. RAN renders 15sp bold; 61-second slip prints `RAN 1 MIN 1 SEC`; 3600s prints `RAN 1H 00M`.
4. Section gaps measure 16/8 per Finisher 2 §6 on clean, FLOW, and faulted exemplars; no orphan rules.
5. Footer is the last *sentence*; the colophon is the last *mark*, centred, on every slip kind.

— Glanz, wow-level designer, issue #10. DECLARED, uncompiled.
