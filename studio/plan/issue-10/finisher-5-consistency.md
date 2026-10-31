# Issue #10 — Receipt (audit fixes) · FINISHER 5 (whole-slip consistency)

**Seat:** Finisher 5, Zettel's cell for GitHub issue #10, "Main-app pass — Receipt (audit fixes)".
**Scope:** the slip as ONE document — `ui/components/ReceiptSheet.kt` (print) +
`data/ReceiptState.kt` (contract) + assembly in `data/MonolithRepository.kt` /
`ui/MonolithViewModel.kt`. Paper only; nothing compiled, built, or tested.
**Source of truth:** `studio/plan/AUDIT-receipt.md` (Zettel, ruled Oct 30 01:15 IST; read at
`~/workspace/clone-inspect/builds/wt-ci/studio/plan/AUDIT-receipt.md`). The ACQUITTALS section
was read first and is treated as binding throughout: nothing acquitted is flagged below.
**Baseline:** this clone (`agent/issue-10`, HEAD `1852181`, batch-9 tree) carries no issue-#10
code fixes yet — the only cell work product present is `work/designer.md`. Every verdict below
is therefore a verdict on the slip **as it currently prints**, checked law by law so the other
seats' cures can be verified against one list when they land.

**Headline: all five laws VIOLATED in the current print.** The loved character itself is intact
(acquitted traits verified holding in §6) — the failures are exactly where the audit pointed:
figures the machine cannot back still print, the one true total still doesn't, the list still
prints in a second ink, the machine still borrows the human voice, one line still speaks a
different duration dialect, and the object is still a Material card.

---

## LAW 1 — OMISSION: a line vanishes rather than prints an unbacked figure; −1 never prints

**VERDICT: VIOLATED** (the sentinel mechanism holds; the substance does not)

- **HOLDS — the −1 mechanism itself.** Seal columns default to `-1` = uncaptured
  (`data/MonolithModels.kt:332–333`), and the sheet gates HELD on both figures:
  `if (receipt.heldAppsAtSeal >= 0 && receipt.heldSitesAtSeal >= 0)`
  (`ReceiptSheet.kt:129–133`). A literal `−1` can never reach the paper. FLOW's PLANNED
  omission is the same law working correctly and is **acquitted**: `plannedSeconds = null`
  for `FLOW_FINISH` (`MonolithRepository.kt:1639`, contract notes at `ReceiptState.kt:144`,
  `:174`), sheet prints ARMED alone (`ReceiptSheet.kt:115–123`).
- **VIOLATED — HELD prints figures the machine cannot back (audit item 9, W-41).** The gate
  above only proves the figures were *captured*, not that they are *true*:
  - Apps: `countHeldApps` counts `resolvedTier != AppBlockTier.OPEN`
    (`MonolithRepository.kt:1318–1319`) — PEEK_2M apps, which open on a lease and freely in
    every Safe Break, are booked as HELD. Captured at `startSession`
    (`MonolithRepository.kt:1384`).
  - Sites: `heldSites = dao.getAllSitesSnapshot().size` (`MonolithRepository.kt:1385`) — a
    blind list size: browser toggle OFF still books every listed site; an emptied list prints
    `0 SITES` while the enforcer holds the factory domains. Wrong in both directions.
  - Since `startSession` always writes real values (`>= 0`), the omission gate never fires on
    a current row — the line prints on effectively every slip. Per the audit + designer's
    interim law (D4.1/D4.2) the whole print branch must die until C1's split columns land;
    re-gating is not a cure.
- **VIOLATED — deflection rows print as blocks regardless of outcome (audit item 11 TRUTH,
  Neu C-03).** Assembly filters on intention, system package, break windows, and 30s dedupe
  (`MonolithRepository.kt:1608–1623`) but never reads `log.outcome`, although
  `InterceptLogEntity` carries it (`MonolithModels.kt:458`). The contract then drops the
  outcome entirely — `ReceiptDeflection(appName, packageName, atWallMillis)`
  (`ReceiptState.kt:124–128`) — so the sheet (`ReceiptSheet.kt:139–144`) physically cannot
  distinguish a turn-away from a `"Used 2m Peek"` let-in. A visit he was let into prints
  identically to a block: an unbacked claim by form.
- **VIOLATED — `PARTIAL` prints with its evidence redacted (audit item 7).**
  `receiptOutcomeWord` returns the bare word (`MonolithRepository.kt:1683–1689`, PARTIAL at
  `:1685`), and neither contract carries an attended-seconds field (`ReceiptState.kt:135–154`
  `PendingReceipt`, `:166–185` `ReceiptState` — no such field exists). The word spans 16
  attended minutes to window-minus-16 and prints anyway. Under this law it must either print
  as `PARTIAL · {attended} OF {window}` (B5) or vanish.
- **VIOLATED — outcome slot prints non-facts (audit item 7).** Same function
  (`MonolithRepository.kt:1688`): plain `"ENDED"` is a tautology — the ENDED line already
  says ENDED (`ReceiptSheet.kt:124`) — and `"ENDED · SHIELD GAP"` double-books the
  shield-gap fact, which item 12's line prints again lower on the same slip
  (`ReceiptSheet.kt:146–154`). One fact, two dialects, one slip: a consistency failure
  inside a single document, not just a wording nit.
- **VIOLATED — `SLEPT THROUGH` misattributes the act (audit item 7).**
  `MonolithRepository.kt:1686`: the machine's own stand-down prints in words that read as
  *his* failure. Ruled cure word is `STOOD DOWN`; the current print is a false statement
  about who acted.
- **VIOLATED — the inversion: the one fully-backed figure is withheld (audit item 5, A1).**
  `sealedElapsedSeconds` is computed and carried through every joint —
  `MonolithRepository.kt:1640` (assembly), `:1725` (publish), `:1777` (rehydrate);
  `ReceiptState.kt:147` + `:176` — and read **zero times** by the sheet (grep of
  `ReceiptSheet.kt`: no occurrence). The slip omits its total while printing the unbacked
  HELD and PARTIAL figures above. Omission applied exactly backwards.
- **VIOLATED — footer clause 2 is a false claim on the paper (audit item 13, W-43).**
  `ReceiptSheet.kt:176` prints `YOUR STUDY RECORD LIVES IN YPT` — false as exclusivity
  (Monolith itself keeps a study record; no YPT integration exists in the tree). Clause 1 is
  **acquitted** and stands. The cure (`…YOUR STUDY HOURS LIVE IN YPT`) is A3, in Ren's merge
  custody — noted here because a consistency pass cannot pass a slip whose footer contradicts
  the app that printed it. The one-line guard is also absent: the footer `Text`
  (`ReceiptSheet.kt:174–181`) carries no `maxLines`/clause-split discipline.
- **VIOLATED — cross-midnight slips print no date anywhere (audit item 3, A11).** The
  contract carries only wall-millis instants (`ReceiptState.kt:166–185` — no date field) and
  the sheet's only formatter is `hhmm` (`ReceiptSheet.kt:198–199`). A span crossing midnight
  prints two `HH:mm` stamps with no day attribution on either.
- **HOLDS — ENDED's backing.** Assembly reads the row's stamped end first
  (`MonolithRepository.kt:1567`: `endWallMillis > 0` wins over the caller's instant) — the
  slip's best line, **acquitted**, stays acquitted. The unstamped-row fallback is the audit's
  accepted edge (item 6), not flagged.
- **HOLDS — no sum is ever taken.** No count/total field exists in the deflection contract
  (`ReceiptState.kt:124–128` and its comment), and the sheet lists rows without aggregating
  (`ReceiptSheet.kt:139–144`). The never-sum acquittal holds in code.

---

## LAW 2 — INK & VOICE: one ink for the list; machine statements in Anonymous Pro Bold; his intention in Atkinson

**VERDICT: VIOLATED** (three of five sub-checks fail)

- **VIOLATED — the deflection list prints in a second ink (audit item 11 PRINT, Druck P-6).**
  Deflections are the only ledger lines rendered `dim = true` (`ReceiptSheet.kt:139–144`),
  and `ReceiptMonoLine` maps dim to `onSurfaceVariant` against the ledger's `onBackground`
  (`ReceiptSheet.kt:188–195`). Druck's one-ink ruling was made *over* Aug's dim acquittal —
  the dim is not protected. Separation must come from the section rule + hanging indent
  (A10), neither of which exists in the file yet (no divider composable anywhere in
  `ReceiptSheet.kt`).
- **VIOLATED — both machine statements borrow the human voice (audit item 16).**
  `THE SHIELD WAS OFF FOR PART OF THAT SESSION` prints in Atkinson Hyperlegible Bold 12sp
  (`ReceiptSheet.kt:146–154`), and the faulted line `SHIELD — STILL BLOCKED (SERVICE DIDN'T
  STOP)` does the same (`ReceiptSheet.kt:156–166`). The law: machine interruptions speak
  machine — Anonymous Pro Bold, 13sp, 1sp tracking. Note the precision: the SHIELD-WAS-OFF
  *sentence* is binding-acquitted (plainest line on the slip) — only its typeface is charged.
  The faulted line's wording is separately charged at audit item 14 (engineer-speak; B4).
- **VIOLATED — the № prints greyer than the title (audit item 2, Druck P-4).**
  `№` renders Anonymous Pro Bold 13sp in `onSurfaceVariant` (`ReceiptSheet.kt:103–109`)
  while the title beside it is `onBackground` (`ReceiptSheet.kt:96`). A document's number is
  its head-mark: same ink, never greyer.
- **HOLDS — his intention owns Atkinson.** The title — and, the two violations above aside,
  only the title — is Atkinson Hyperlegible Bold (`ReceiptSheet.kt:91–98`, 15sp,
  `onBackground`). Sora appears nowhere on the slip. On the faulted branch the title is
  suppressed whole (`ReceiptSheet.kt:99–102`, F-W1); whether faulted slips should print
  `FOR: <intention>` is B3, **pending Ren's ruling — held, not flagged**, per the audit's own
  disposition.
- **HOLDS — the ledger voice itself.** `ReceiptMonoLine` is Anonymous Pro 11sp at 1sp
  tracking (`ReceiptSheet.kt:188–195`) — the acquitted mono-ledger character, intact. The
  footer is the same family at 9sp/0.25sp in the variant ink (`ReceiptSheet.kt:174–181`);
  the audit charges it only on fit/night-legibility (Law 1 above), not on voice.

---

## LAW 3 — LONG-FORM DURATIONS EVERYWHERE: one dialect, no mixed forms

**VERDICT: VIOLATED** (one rogue dialect line; the true total speaks no dialect at all — it is absent)

- **HOLDS — the house dialect exists and PLANNED speaks it.** `longSpan`
  (`ReceiptSheet.kt:204–211`) is the ceiling-to-minute long form (`50 MIN`, `1H 00M`), and
  PLANNED routes through it (`ReceiptSheet.kt:118`). Clock stamps (`ARMED`/`ENDED`/`WITHIN`,
  deflections) are instants, not durations — `HH:mm` there is correct and not a dialect mix.
- **VIOLATED — `BREAK BANKED` speaks floor-division in a ceiling house (audit item 10).**
  `ReceiptSheet.kt:134–138`: `"BREAK BANKED ${receipt.bankedBreakSeconds / 60} MIN"` —
  integer floor, never routed through `longSpan`. The same slip therefore prints two
  different truths for the same span class: 90 seconds would print `PLANNED`-dialect
  `2 MIN` via `longSpan` but `BREAK BANKED 1 MIN` here; a 30-second value passes the
  `> 0` gate and prints `BREAK BANKED 0 MIN`. The line is also a semantic falsehood, as
  charged: assembly sums spend-ledger `heldSeconds` (`MonolithRepository.kt:1593–1594`),
  whose only writer books the valve's 60-second hold *price*
  (`EMERGENCY_BREAK_HELD_SECONDS = 60`, `MonolithRepository.kt:121`, written at `:2240`) —
  a price re-named as a banked break. Audit disposition is KILL as named and as
  arithmeticked; any lawful successor is re-earned as `BREAK HELD {longSpan}`. Still
  printing today.
- **VIOLATED — the lived duration prints in no form (cross-ref Law 1, item 5/A1).** With
  RAN absent, the slip's only printed durations are PLANNED (a plan, not a life) and the
  rogue BREAK BANKED above. On FLOW slips — PLANNED lawfully absent — the rogue line is
  potentially the *only* duration on the paper, and it is the false one. Consistency of
  dialect is meaningless while the headline duration is missing.

---

## LAW 4 — SHAPE: 3dp radius, no border, shadow lift (audit item 15; anchor item 17)

**VERDICT: VIOLATED** (all three shape counts, plus the anchor)

- **VIOLATED — radius.** The slip clips at `RoundedCornerShape(24.dp)` — twice:
  `.clip(...)` and the border's shape (`ReceiptSheet.kt:77`, `:79`). Law: 3dp. Thermal
  paper is cut square; 24dp is the digest sheet's inherited card geometry.
- **VIOLATED — border present.** `.border(1.dp, scheme.outlineVariant, …)`
  (`ReceiptSheet.kt:79`; import at `:7`). Law: no border.
- **VIOLATED — no shadow lift.** No `shadow`/`elevation` modifier exists anywhere in
  `ReceiptSheet.kt` (grep: zero hits). The lift the law assigns to shadow is currently
  faked by the border + `surfaceBright` background (`ReceiptSheet.kt:78`) — remove the
  border without adding the shadow and the slip loses its separation from the Deck.
- **VIOLATED — anchor floats (audit item 17).** The host `Box` pads 12dp on all sides
  (`ReceiptSheet.kt:69–72`), so the slip floats and the nav pill peeks under its foot.
  Law: anchor means contact — 0 at bottom and sides.
- **HOLDS — scrim 0.12.** `Color.Black.copy(alpha = 0.12f)` (`ReceiptSheet.kt:69`) —
  binding acquittal, verified in place.
- **HOLDS — brightest object.** The slip's `surfaceBright` ground (`ReceiptSheet.kt:78`)
  keeps it the brightest object in the frame, per the acquittal. This survives the shape
  cure only if the shadow lift lands with it (above).

---

## LAW 5 — CROSS-SURFACE AGREEMENT: the slip must not disagree with the Deck or with itself

**VERDICT: VIOLATED — with two dependencies FLAGGED, NOT FIXED (C3, C2), as instructed**

- **FLAGGED DEPENDENCY (C3) — PLANNED's source is a live recompute, not a seal (audit
  item 4). DO NOT FIX IN THIS CELL.** Assembly sets
  `plannedSeconds = … row.spanTargetSeconds` (`MonolithRepository.kt:1639`).
  `spanTargetSeconds` is a *computed getter* on the final row
  (`MonolithModels.kt:399–402`) built from `effectiveTargetSeconds =
  targetDurationSeconds + penaltySecondsAdded` (`MonolithModels.kt:379–380`) — so a
  mid-session tamper penalty (written at `MonolithRepository.kt:1947`, `:1978`, `:2029`,
  `:2082`) prints *inside the plan*. No `sealedSpanTargetSeconds` column exists anywhere
  in the tree (grep: zero hits) — Frist's seal-time source is not in this build. The VM
  freezes the finish display from the same getter at E0
  (`MonolithViewModel.kt:1232–1235`), so slip and Deck agree today only by coincidence of
  read timing, not by a shared sealed source; and rehydration re-derives through the same
  assembly (`MonolithRepository.kt:1767`), so a restored slip is not guaranteed to match
  its first printing. **Flag for the batch-10 merge: slip and Deck must take
  `sealedSpanTargetSeconds` in the same merge, or item 4 stays open.**
- **FLAGGED DEPENDENCY (C2) — WITHIN disagrees with itself across printings (audit
  item 8).** Routine-row slips are frozen at insert and agree (`MonolithRepository.kt:1575–1580`).
  Hand-armed slips are re-derived **live** at assembly *and again at rehydration* via
  `routineWindowLabelAt(row.startWallTimeMillis)` (`MonolithRepository.kt:1584`) — a
  routine edited/disabled between printings changes the restored slip's WITHIN line. The
  cure is the RC-03 freeze from Voss's lane; batch 10 must not close item 8 without that
  merge. Flagged, not fixed.
- **VIOLATED — slip vs Shield vocabulary (cross-ref Law 1).** The Shield's own tier words
  are SHUT / PEEK; the slip's HELD line lumps both tiers into `APPS` (designer D4.4).
  Two surfaces, two accounts of the same shield — resolved only when HELD is omitted
  (interim) and returns in the split form (C1).
- **HOLDS — slip vs itself on the backed facts.** ENDED (Law 1), the № (row-latched claim,
  `MonolithRepository.kt:1496–1525` region; `ReceiptState.kt` header `:10–18`), and the
  deflection window/dedupe are re-derived through the *same* assembly on publish and
  rehydrate (`MonolithRepository.kt:1711–1735`, `:1753–1788`), so first and restored
  printings cannot drift on those lines. The two flagged sources above are the only
  re-derivation hazards found.

---

## 6. ACQUITTED CHARACTER — verified holding (not flagged, per binding acquittals)

- Mono ledger: Anonymous Pro 11sp / 1sp tracking (`ReceiptSheet.kt:188–195`). ✔
- Long-form durations as the house dialect (`ReceiptSheet.kt:204–211`). ✔ (one rogue line — Law 3)
- Zero buttons / CTA on the paper: no `Button` composable exists in `ReceiptSheet.kt`;
  the only clickables are the scrim's dismiss and a disabled swallow on the slip body. ✔
- Brightest object in frame: `surfaceBright` (`ReceiptSheet.kt:78`). ✔
- Stillness: no number animation anywhere in the sheet; the slip composes settled. ✔
- Scrim 0.12 (Law 4). ✔ · ENDED from the stamped end (Law 1). ✔ · FLOW's absent PLANNED (Law 1). ✔
- Deflections listed, never summed (Law 1). ✔ · GRACE_CANCEL / VALVE_BROKE silence: neither
  kind reaches `finishKindOf`'s publishable set (`ReceiptState.kt:89–109`), so no slip and
  no № is minted for them. ✔

## 7. SEEN BUT NOT MINE — out-of-scope sightings for their owning seats (not counted above)

- Arrival is still a crossfade with a dead exit fade (`ReceiptSheet.kt:62–66`) — A7's seat (item 18).
- Rhythm is uniform `spacedBy(8.dp)` with zero section rules (`ReceiptSheet.kt:83`) — A6's
  seat (item 16); Law 2's one-ink cure depends on A6's rules existing to separate by.
- Dismissal persistence and the DOWN-gesture/dwell — A8/A9 seats (items 19–20); nothing in
  the three files I own contradicts the designer's §3 contract.

## 8. VERDICT TABLE

| Law | Verdict | Killing evidence (one line) |
|---|---|---|
| 1 · Omission / −1 | **VIOLATED** | HELD + peek-deflections + evidenceless PARTIAL print unbacked; RAN withheld (`ReceiptSheet.kt:129`, `MonolithRepository.kt:1608`, `:1640`) |
| 2 · Ink & voice | **VIOLATED** | List in two inks (`ReceiptSheet.kt:142,195`); machine statements in Atkinson (`:148,160`); № grey (`:108`) |
| 3 · Long-form durations | **VIOLATED** | `BREAK BANKED ${… / 60} MIN` floor dialect + false name (`ReceiptSheet.kt:136`) |
| 4 · Shape (item 15/17) | **VIOLATED** | 24dp radius + 1dp border + no shadow + 12dp float (`ReceiptSheet.kt:71,77,79`) |
| 5 · Cross-surface | **VIOLATED (C3 + C2 flagged, not fixed)** | PLANNED from live `spanTargetSeconds` getter (`MonolithRepository.kt:1639`); WITHIN re-derived live (`:1584`) |

— Finisher 5 (whole-slip consistency), Zettel's cell (issue #10). Paper only; nothing compiled.
