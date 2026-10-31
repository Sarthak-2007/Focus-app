# Issue #10 — Receipt (audit fixes) · DESIGNER (print side)

**Seat:** Designer, Zettel's cell for GitHub issue #10, "Main-app pass — Receipt (audit fixes)".
**Source of truth:** `studio/plan/AUDIT-receipt.md` (Zettel, ruled Oct 30 01:15 IST; read in the
`wt-ci` clone at `~/workspace/clone-inspect/builds/wt-ci/studio/plan/AUDIT-receipt.md`).
**Code read:** the canonical batch-9 tree
`~/workspace/clone-inspect/builds/merge-batch9/Focus-app` — verified byte-identical to `wt-ci`
for both receipt files (`ReceiptSheet.kt`, `ReceiptState.kt` diff clean). All line citations
below are to that tree.
**Standing constraint honoured throughout:** the slip's printed character is a proven keeper
and is *protected*, not redesigned — mono ledger (Anonymous Pro 11sp / 1sp tracking), long-form
durations, zero buttons or CTA on the paper, brightest object in the frame, omission law
(absent line over false line). Every decision below is a truth fix inside that character.
Nothing here compiles, builds, or tests — paper only.

## Scope

**In (this document):** audit A4 (deflection outcome filter, item 11 truth half), A1 (the
`RAN` total, item 5), A9-interim + item 20 interim law (dismissed-№ persistence), C1 print
side (HELD line, item 9).
**Out (parked, issue #21 — not designed here, not even sketched):** TEAR, SLIP BOOK.
**Out (other seats / other group-A items):** shape/anchor (A5), rhythm/section rules as a
whole (A6 — touched only where §2's rule placement depends on it), arrival feed (A7),
dismissal *gesture* mechanics (A8 — §3 defines which events count as a dismissal so
persistence can attach to them; it does not re-spec the gesture), outcome words (A2),
footer noun (A3), date line (A11), faulted-sheet wording (item 14 except §3.5).

**Type & colour law for everything below:** ledger lines stay Anonymous Pro; the title stays
Atkinson Hyperlegible Bold; Sora appears nowhere on the slip today and gains no foothold
here (it is the app's display face, not the paper's). No new colour anywhere — all ink from
the existing scheme (`onBackground` / `onSurfaceVariant`); never green, no accent, no image
of any kind. This is a text-only document about a text-only surface.

---

## 1. Deflection lines — outcome-filtered (audit item 11 TRUTH, cure A4)

### 1.1 What is wrong, in evidence

Assembly never looks at the outcome it already has. In
`data/MonolithRepository.kt`, `assemblePendingReceipt` builds the deflection list at
**L1608–1635**: the filters are intention match, `packageName != "com.monolith.system"`,
valve-break-window exclusion, and 30s-per-package dedupe — `log.outcome` is never read,
even though `InterceptLogEntity` carries it (`data/MonolithModels.kt:452–460`, field at
L458). The rows are then reduced to `ReceiptDeflection(appName, packageName, atWallMillis)`
(`data/ReceiptState.kt:124–128`) — the outcome is *dropped at the contract*, so the sheet
(`ui/components/ReceiptSheet.kt:139–144`) physically cannot tell a block from a let-in:
every surviving row prints identically as `HH:mm · {appName}`, dim. Neu C-03, verified in
the audit: a visit he was **let into** on a peek prints as if the machine had blocked it.

### 1.2 The outcome vocabulary — enumerated, not assumed (audit's own condition)

I enumerated every writer of `intercept_logs.outcome` in the production tree. There are
exactly three in `ui/MonolithViewModel.kt`, plus system rows and one test-harness dialect:

| Stored `outcome` string | Writer (evidence) | What actually happened | Class |
|---|---|---|---|
| `"Stayed Focused"` | `MonolithViewModel.kt:2437`, `transmuteImpulseAndReturn()` | Airlock fired, he turned back, app never opened | **SHUT** |
| `"Split Into 10m Micro-Step"` | `MonolithViewModel.kt:1978`, `splitActiveTaskAndReturn()` | Airlock fired, he redirected to a micro-step, app never opened | **SHUT** |
| `"Used 2m Peek"` | `MonolithViewModel.kt:2412`, `start2MinutePeek()` | A 2-minute lease was granted and he was **let in** | **PEEK** |
| `"BLOCKED"` | No production writer in this tree; seeded by `ScreenshotHarnessTest.kt:484,493` and the dialect older rows may carry | A plain block stamp | **SHUT** |
| Free-text `outcomeReason` strings (e.g. `"Ended Early"`, grace-cancel and valve strings) | `MonolithRepository.kt:2347–2353`, `disarmActiveSession()` | Session-ending bookkeeping, always on package `com.monolith.system` | **NEITHER** — already excluded by the existing system-package filter; must stay excluded |
| Anything else / unrecognised | — | Unproven | **NEITHER** (see D1.4) |

### 1.3 Design decisions

- **D1.1 — Filter at assembly, never at the sheet.** The outcome predicate joins the
  existing filter chain in `assemblePendingReceipt` (`MonolithRepository.kt:1614–1619`).
  `ReceiptDeflection` and `ReceiptSheet.kt` do not change: by the time a row reaches the
  contract it *is* a block, so the print code stays exactly as loved. Bonus truth property:
  rehydration rebuilds through the same assembly (`rehydrateReceipt` →
  `assemblePendingReceipt`), so a restored slip prints the same list as the first printing
  — the filter cannot drift between the two.
- **D1.2 — Allowlist, not denylist.** A row prints **iff** its `outcome` is in the SHUT
  set: `{"Stayed Focused", "Split Into 10m Micro-Step", "BLOCKED"}`. Everything else —
  the PEEK string, system outcome-reasons, and any string nobody has enumerated — does
  not print. A denylist (`!= "Used 2m Peek"`) would silently promote the next invented
  outcome string into a printed "block"; the allowlist fails closed, which is the slip's
  omission law applied to rows.
- **D1.3 — The outcome filter runs FIRST in the chain, before dedupe.** Today the 30s
  dedupe key is per-package over *all* rows (L1620–1628). If a peek row entered the dedupe
  map, a genuine block of the same package 20s later would be swallowed as a "duplicate"
  of a visit that was let in. Order is therefore part of the spec: outcome → intention →
  system-package → break-window → dedupe. A peek must leave no trace in any later filter.
- **D1.4 — What prints for each outcome (exact):**
  - **SHUT:** exactly what prints today, unchanged in form — one ledger line per kept row,
    `"{hhmm} · {appName}"` (e.g. `14:12 · Instagram`), chronological, listed and **never
    summed** (binding acquittal: no count, no header, no "×3"). Print treatment (one ink,
    hanging indent, silence rules) is A10's seat; this filter composes with it untouched.
  - **PEEK (`"Used 2m Peek"`):** **nothing.** No line, no count, no marker, no
    placeholder, no gap where it "would have been". The slip does not say he peeked, and
    it does not say he didn't — the deflection list is a list of *turn-aways*, and a
    let-in is simply not an entry in it, the way a shop receipt doesn't list the aisles
    you walked down. Inventing a `PEEKED …` line was considered and rejected: it is not
    in cure A4, it would sum/narrate what the acquittals protect as list-only, and it
    would re-book the peek on the slip under a kinder word — the same W-41 sin in
    miniature.
  - **NEITHER / unrecognised:** nothing, same as PEEK.
- **D1.5 — Empty result is a normal slip.** A session whose only intercepts were peeks
  prints **zero** deflection lines and no substitute text (no "NO DEFLECTIONS" line —
  the paper never prints the absence of a section). The section simply isn't there, and
  the slip's rhythm closes up.

### 1.4 Acceptance checks for the builder's proof (paper checks, not runs by me)

1. Seed the harness's two `"BLOCKED"` rows (TikTok, Instagram) **plus** one
   `"Used 2m Peek"` row for a third package, intention-matched, inside the window:
   slip lists TikTok and Instagram only.
2. A `"Used 2m Peek"` row for package X at *t*, a `"Stayed Focused"` row for X at
   *t+20s*: the shut row **prints** (proves D1.3 ordering).
3. A slip assembled, dismissed, and rehydrated shows the identical list (proves D1.1).

---

## 2. The total line — `RAN {longSpan}` (audit item 5, cure A1)

### 2.1 What is wrong, in evidence

The lived span is computed, carried, and printed nowhere. `sealedElapsedSeconds` exists
on `PendingReceipt` (`data/ReceiptState.kt:147`) and on `ReceiptState` (L176); assembly
fills it from the row's own derivation, `row.spanElapsedSeconds` (`MonolithRepository.kt:
1640`); publish and rehydrate both carry it through (L1725, L1777). A grep of
`ReceiptSheet.kt` returns **zero** reads of it. The slip prints PLANNED and withholds the
one figure that says what actually happened — worst on FLOW slips, where PLANNED is
lawfully absent (RC-02) and duration is the whole point. Six audit seats, unanimous.

### 2.2 Design decisions

- **D2.1 — Exact string.** One new ledger line:
  `RAN {longSpan(receipt.sealedElapsedSeconds)}` — the sheet's *existing* formatter
  (`ReceiptSheet.kt:204–211`, ceiling-to-minute dialect), reused verbatim, never
  reimplemented. Prints as `RAN 50 MIN`, `RAN 1H 00M`, `RAN 3H 50M`. Short forms
  (`50M`, `1:00`) are forbidden — they are not the slip's dialect anywhere.
- **D2.2 — Placement: directly under the ENDED line, over a rule.** Per audit item 5
  ("set as the total line **under ENDED over a rule**", Druck's sectioning) the printed
  order of the time ledger becomes:

  ```
  ARMED 09:00 · PLANNED 50 MIN
  ENDED 09:50 · {outcome word, where A2 leaves one}
  ──────────────────────────────────   ← section rule (item 16's rule, head│total)
  RAN 50 MIN
  ```

  Rationale, stated once: the total belongs to the three stamps it totals. ARMED /
  PLANNED / ENDED / RAN read as one four-line ledger at the head — plan against ran is
  the comparison the slip exists to make, and it must be makeable in one glance without
  travelling past the deflection list. A foot-of-slip total (supermarket style) was
  considered and rejected: deflections are a narrative list of variable length, and a
  total buried under it would sit at a different height on every slip. **The RAN line
  never moves** — same slot on manual, FLOW, routine, and faulted slips alike.
- **D2.3 — Type.** Anonymous Pro, **Bold**, 11sp, 1sp letter-spacing, `onBackground` —
  the ledger's own size and tracking, struck harder. The total's authority comes from
  its rule and its slot, not from a size jump: 13sp was considered (it is the size
  item 16 gives the machine's two interruptive statements) and rejected — RAN is not an
  interruption, and a bigger total would compete with the Atkinson title, which is the
  one human voice on the paper.
- **D2.4 — Verbatim figure, never recomputed at render.** The sheet prints the carried
  field. It must **not** derive a span from `endedAtWallMs − armWallMs` or from any
  wall arithmetic: the sealed field is the tick-derived span (breaks and penalties
  already priced by the model's one derivation), and a render-time recompute is the
  same class of lie as item 4's PLANNED recompute. If RAN and the wall stamps ever
  disagree by a minute, the field wins on paper and the disagreement is a data bug to
  file, not a print bug to paper over.
- **D2.5 — FLOW slips.** Same line, same slot, same type. With PLANNED absent, RAN is
  the headline figure *by being the only duration on the slip* — it is not promoted,
  enlarged, or moved up for FLOW. Consistency of slot is what makes it findable.
- **D2.6 — Faulted slips.** RAN prints. The session ran for a real span whatever the
  shield did afterwards; the fault has its own line (item 14's seat) and does not
  confiscate the total.
- **D2.7 — Zero and small values print.** The field has no uncaptured sentinel (it is
  plain seconds, always carried), so there is no omission case: `RAN 1 MIN` (the
  ceiling dialect's smallest nonzero) and even `RAN 0 MIN` print as the machine's own
  figure rather than the line vanishing. **One flagged exception to verify before
  merge** — see Dependency 2 (§6): ROUTINE_SLEEP_STANDDOWN rows.

### 2.3 Resulting head of a clean manual slip (after §1, §2, §4-interim)

```
Finish Chapter 4 Problem Set (#1–6)          № 048
ARMED 09:00 · PLANNED 50 MIN
ENDED 09:50
──────────────────────────────────
RAN 50 MIN
WITHIN DAY BLOCKS · 09:00–13:00
14:12 · Instagram
THE MACHINE KEEPS ITS OWN LOG · YOUR STUDY RECORD LIVES IN YPT
```

(Deflection time above is illustrative of form only. HELD absent per §4; footer noun
shown as currently printed — A3's cure is another seat's line to change.)

---

## 3. Dismissal must persist (audit item 20 interim law, cure A9)

### 3.1 What is wrong, in evidence

Dismissal is memory-only at **both** layers, and rehydration is unconditional:

- Host: `MainActivity.kt:268` — `var dismissedReceiptSessionId by remember {
  mutableStateOf(-1) }`. `remember` dies with the process (and with any activity
  recreation). Every dismissal door writes only this value: the sheet's own
  `onDismiss` (L783), the navigation/PARK `LaunchedEffect` (L303–308), rail/closePark
  paths. The render gate is L776–785: slip shows iff `receiptState != null &&
  dismissedReceiptSessionId != standingReceipt.sessionId && destination == DECK`.
- VM: `MonolithViewModel.kt:1096–1103` — at every process birth,
  `runSliceERecovery()` calls `repository.rehydrateReceipt()` and installs the result
  whenever `_receiptState` is null, with no dismissed check anywhere. The standing
  receipt itself is genuinely persisted — as № + slot pointer + outcome word on the
  `guard_settings` singleton (`ReceiptState.kt:10–18`; `GuardSettingsEntity` in
  `MonolithModels.kt`, receipt columns at L761–773) plus the row-latched № (RC-04) —
  which is exactly why the resurrection is reliable: the data survives, the dismissal
  doesn't. Audit item 20: "a dismissed slip re-ambushes at cold start."

### 3.2 User-visible behaviour (the contract)

- **B3.1 — Dismissed means dismissed, forever, for that №.** Once a slip is dismissed,
  that receipt never renders again: not on cold start, not on relaunch, not after
  force-stop, not on return to the Deck, not the next day. No re-feed, no re-arrival —
  there is nothing to animate, because there is no slip.
- **B3.2 — Dismissal is per-receipt, never a mute.** The persisted fact names **one**
  session/№. The *next* session's slip (new №) arrives, detains, and stands exactly as
  before. A user who dismisses every slip still sees every new slip once.
- **B3.3 — Every existing dismissal door has the same persisted effect.** The events
  that count as "dismissed" are precisely the ones that set the host state today:
  (a) the lawful dismissal gesture on the sheet/scrim (its DOWN-gesture + 2s-dwell
  mechanics are A8's spec — persistence attaches to that event, whatever A8's final
  gesture is); (b) navigating away from the Deck; (c) invoking PARK; (d) a rail stop
  press that closes PARK/sheets. No door is second-class: dismiss-by-navigation must
  survive restart exactly as dismiss-by-tap does, or the ambush survives through the
  side door.
- **B3.4 — Faulted slips: same persistence, higher gate.** A faulted slip dismisses
  only on its explicit act (audit item 19). Once that act happens, B3.1 applies in
  full. Until it happens, the faulted slip **must** still re-present on cold start
  (settled, no re-performed arrival, per Z6) — its news (the shield did not stop) is
  the one receipt fact too urgent to swallow. Persistence must therefore record
  *dismissed* receipts only; it must never be implemented as "don't rehydrate".
- **B3.5 — No new surface, no undo, no summon.** Dismissal stays a one-way, silent
  act: no button appears on the paper (zero-CTA acquittal), no "receipt dismissed"
  text prints anywhere, and there is deliberately **no way back** in this design.
  Re-summoning an old slip is the SLIP BOOK — parked (issue #21) and not designed
  here. Until Ren rules it in, a dismissed slip is gone from view and kept only in
  the machine's log (№ + pointer + outcome, as today).
- **B3.6 — The standing slot is not erased.** Dismissal is a *viewing* fact, not a
  deletion: `receiptSequence`, `lastReceiptSessionId`, `lastReceiptOutcome`, and the
  row's `receiptNumber` all stay exactly as the claim left them (the footer's "THE
  MACHINE KEEPS ITS OWN LOG" remains true of the slip's remains). The next arm's
  existing clearing of `_receiptState` (VM `armMonolithSession`) is unaffected.

### 3.3 State that must survive restart (data requirement for the builder)

1. **What:** the dismissed receipt's identity — `sessionId` **and** receipt № as a
   pair. `sessionId` is the key both gates already compare; the № is the slip's
   printed identity and its RC-04 row latch. Storing the pair costs nothing and
   makes the gate immune to any future slot/sequence drift: a restored receipt is
   suppressed iff **both** match the dismissed pair.
2. **Where (recommended):** one new column on the `guard_settings` singleton, beside
   the receipt slot it qualifies (e.g. `dismissedReceiptSessionId: Int = 0` +
   `dismissedReceiptNumber: Int = 0`, 0 = none dismissed), written by a **targeted
   single-purpose UPDATE** in the DAO's established §7.5 discipline — the same
   pattern as `writeReceiptClaim` and the stamp writes (`data/MonolithDao.kt:
   255–280` region): no whole-row REPLACE may ever carry this value. An equally
   lawful alternative is a dismissed stamp on the session row beside `receiptNumber`
   (RC-04's home); the behaviour contract above is what binds, the builder picks
   the column — but it must live in Room with the rest of the receipt's facts, not
   in a side file or preferences store that can drift from the slot.
3. **When written:** at the dismissal event itself (B3.3), in the same act that
   clears the visible slip — VM-mediated (`dismissReceipt(sessionId, number)` on
   the repository), not left to a later flush. The host's `remember` state still
   updates instantly for the visual exit; the persisted write is launched from the
   same event. Acceptance is judged at ordinary-restart granularity (B3.4's test
   below); a process kill racing the gesture mid-flight is out of scope.
4. **Two gates, either sufficient (defence in depth):**
   - *VM gate (primary):* `runSliceERecovery()` consults the dismissed pair before
     installing a rehydrated receipt — dismissed standing receipt ⇒ `_receiptState`
     stays null. This kills the ambush at the source and also keeps the sled gate
     from ever arming on a dismissed slip.
   - *Host gate (backstop):* `dismissedReceiptSessionId` in `MainActivity.kt:268`
     is initialised from the persisted value (via the VM), not from `-1`, so even
     if a restored state slips through, the existing render gate at L778 suppresses
     it. Neither gate alone is load-bearing for correctness of the other.
5. **Shape:** a single scalar pair, overwritten by each new dismissal — **not** a
   list or set. Only the standing slip can be on screen, and `standingReceipt()`
   only ever returns the latest claim, so one dismissed identity is provably
   sufficient; older dismissed slips can never be rehydrated anyway. No storage
   growth, no bookkeeping table.

### 3.4 Acceptance cases (paper checks)

| # | Sequence | Required result |
|---|---|---|
| 1 | Slip stands → dismiss by tap → force-stop → relaunch | Deck opens clean; no slip, ever, for that № |
| 2 | Slip stands → navigate to Shield → return → restart | Same — navigation-dismissal persisted too |
| 3 | Slip stands → invoke PARK → restart | Same |
| 4 | Faulted slip stands → restart **without** dismissing | Faulted slip re-presents, settled; no arrival re-performance |
| 5 | Faulted slip → explicit dismissal → restart | Never renders again |
| 6 | Dismiss № 048 → run a new session → its finish issues № 049 | № 049's slip arrives and stands normally |
| 7 | Dismiss → rotate / activity recreation (same process) | No flicker-resurrection of the slip |
| 8 | Fresh install / no receipt stands → any dismissal door fires | No-op; nothing written that can suppress a future first slip (guard the `-1`/`0` sentinel) |

### 3.5 Note on sequencing with A8

If A8's dwell lands first, a tap inside the 2s dwell is *not* a dismissal and must
write nothing — persistence hooks the dismissal **event** as A8 defines it, not the
raw tap. Builders of A8 and A9 must share that one event definition.

---

## 4. The HELD line — killed as printed; return path via SHUT/PEEK split (audit item 9, W-41, dependency C1)

### 4.1 What is wrong, in evidence

The line as printed (`ReceiptSheet.kt:129–133`, gated on both seal counts `>= 0`):

```
HELD {heldAppsAtSeal} APPS · {heldSitesAtSeal} SITES
```

Both figures are false, in opposite directions (audit item 9, verified wider than filed):

- **Apps:** `countHeldApps` (`MonolithRepository.kt:1318–1319`) counts
  `resolvedTier != OPEN` — LOCKED **and** PEEK_2M together. PEEK apps open on a
  lease and freely in every Safe Break; booking them as HELD is the W-41 lie. The
  count is captured at `startSession` (L1384) from that same derivation.
- **Sites:** `heldSites = dao.getAllSitesSnapshot().size` (L1385) — a blind list
  size. Browser toggle OFF ⇒ the slip still books every listed site; list emptied
  ⇒ it prints `0 SITES` while the enforcer silently holds the 8 factory domains
  (`BlockedBrowserDomains.defaultDomains`, `MonolithModels.kt`).

### 4.2 Interim design — until the split columns land (ships with this issue)

- **D4.1 — The HELD line prints NOTHING.** Not a corrected figure, not a partial
  figure, not `HELD —`, not zeroes: the whole line is absent from every slip, and
  the slip's rhythm closes over the gap (no blank ledger row left behind). This is
  the audit's ruling verbatim ("Until then it is OMITTED, under the slip's own
  omission law: an absent line over a false one") and it is unconditional — it
  applies to manual, FLOW, routine, and faulted slips alike.
- **D4.2 — Print-side kill, data untouched.** The existing seal columns keep being
  written at `startSession` exactly as today (other readers and the W-41 data work
  are not this issue's to disturb). What dies is only the print branch in
  `ReceiptSheet.kt` — the `>= 0` sentinel gate is *not* sufficient on its own,
  because the columns carry real captured values (`>= 0`) on every current row;
  the branch must be removed/disabled outright, not re-gated.
- **D4.3 — No substitute line.** Nothing replaces HELD in its slot: no deflection
  count (never-sum acquittal), no "shield held" prose, no SENTENCE (B7, unruled).
  The slot stays empty until §4.3's conditions are met.

### 4.3 Return design — consuming the persisted split (post-W-41)

The data work — persisted `heldShutAtSeal` / `heldPeekAtSeal` seal columns (Suki's
Car-3 offer) plus an **enforcer-derived** sites figure — is Ren's batch-10 hand-work
(W-41) and is **not** designed here. This section specifies only the print side that
consumes it, so the line can return the moment the data is certified, with no
further design pass.

- **D4.4 — Exact format on return:**

  ```
  HELD {heldShutAtSeal} SHUT · {heldPeekAtSeal} PEEK · {heldSitesEnforced} SITES
  ```

  e.g. `HELD 8 SHUT · 3 PEEK · 5 SITES`. Word law: **SHUT** and **PEEK** are the
  app's own tier words — the Shield screen's section headers print exactly `SHUT` /
  `PEEK` (`ui/screens/ShieldMatrixScreen.kt`, tier→word mapping at L833–834:
  LOCKED→"SHUT", PEEK_2M→"PEEK") — and they stand alone there over app lists, so
  they stand alone here; repeating "APPS" after each segment (`8 SHUT APPS · 3 PEEK
  APPS`) was rejected as noise. Stored tokens (`LOCKED`, `PEEK_2M`) never print.
  The lead word `HELD` is kept from the loved slip: with the split printed, it no
  longer lies — SHUT apps were held outright, PEEK apps were held behind a
  lease-door, and the segment label says which. What W-41 killed was the *lumping*,
  not the word.
- **D4.5 — Position and type unchanged.** The returned line occupies the dead line's
  exact slot — after the WITHIN line, before the deflection list (today's L129–133
  position in the column) — in the plain ledger voice: Anonymous Pro 11sp / 1sp,
  `onBackground`, not dim, not bold. It is a fact line, not a statement.
- **D4.6 — All-or-nothing gate (the omission law, extended).** The line prints iff
  **all three** inputs are present and truthful: `heldShutAtSeal >= 0 &&
  heldPeekAtSeal >= 0 && heldSitesEnforced >= 0`, where the new columns carry the
  seal columns' existing sentinel law (`-1` = uncaptured, `0` is a real count —
  `MonolithModels.kt` seal-column comment at L325–333). Any one missing ⇒ the
  **whole** line is omitted. A partial line (`HELD 8 SHUT · 3 PEEK` with sites
  dropped) was rejected: it reads as a complete account and silently forgives the
  missing third — precisely the blind-sites failure in a new shape. Likewise, the
  apps half may not return ahead of the sites half: audit item 9 conditions the
  line on **both** figures deriving from enforcement truth.
- **D4.7 — Zeroes print.** `HELD 0 SHUT · 2 PEEK · 0 SITES` is a lawful, truthful
  line and prints in full. Zero is a count, not an absence (same law as the
  existing columns).
- **D4.8 — No arithmetic rescue, ever.** The sheet/assembly must never derive the
  split from the legacy `heldAppsAtSeal` (e.g. peek = held − shut): that number
  mixes the tiers inseparably, and any difference computed from it inherits the
  lie. Rows sealed before the split columns exist carry `-1` there (migration
  default) ⇒ their slips — including a standing slip rehydrated across the
  upgrade — omit HELD under D4.6. Correct, and final for those rows.
- **D4.9 — Fit.** Worst realistic print, `HELD 12 SHUT · 10 PEEK · 18 SITES`
  (33 chars), sits comfortably inside the slip's inner width at 11sp/1sp (the
  62-char footer at 9sp/0.25sp measures ~326dp against ~348dp — see the standing
  comment at `ReceiptSheet.kt:160–170`). The line must never wrap mid-figure and
  never truncate: a truncated count is a false count. If a future fontScale ever
  threatens the fit, the remedy is the footer's clause-split discipline, not a
  smaller total — flagged, not expected.

### 4.4 Acceptance checks (paper)

1. **Interim build:** a slip whose row carries `heldAppsAtSeal = 10,
   heldSitesAtSeal = 3` (the harness seed values) prints **no** HELD line and no
   blank row where it stood.
2. **Post-W-41:** row with shut 8 / peek 3 / sites-enforced 5 prints exactly
   `HELD 8 SHUT · 3 PEEK · 5 SITES`, once, in the §D4.5 slot.
3. Row with any one of the three at `-1` prints no HELD line.
4. Pre-split row (split columns `-1`, legacy `heldAppsAtSeal = 10`) rehydrated
   after upgrade prints no HELD line — and no derived split anywhere.

---

## 5. What this design deliberately does not touch

- Deflection print treatment (dim→one ink, hanging indent, silence rules, site-entry
  distinction) — A10 / Druck P-6's seat; §1 composes with whatever it rules.
- Outcome words (`ENDED`, `SLEPT THROUGH`→`STOOD DOWN`, PARTIAL evidence) — A2 / B5.
- Footer noun cure and one-line guard — A3 (W-43, Ren's merge custody).
- Shape, anchor, rhythm beyond §2's single rule, arrival, faulted-sheet wording —
  A5/A6/A7/item 14 seats. §2's rule assumes item 16's section-rule system exists;
  if A6 slips a batch, RAN still ships with a single hairline rule of its own —
  the total must not wait on the full re-rhythm.
- TEAR and SLIP BOOK — parked, issue #21. Nothing in §3 pre-builds them: the
  dismissed-№ persistence is the audit's *interim* law, valuable whether or not
  the book is ever ruled in.

## 6. Dependencies found (for Zettel / Ren)

1. **W-41 data work gates §4.3 only.** The SHUT/PEEK split columns
   (`heldShutAtSeal`/`heldPeekAtSeal`) and an enforcer-derived sites figure do
   not exist in this tree (DB at v12, `MonolithDatabase.kt:25`; the only seal
   counts are the mixed `heldAppsAtSeal`/`heldSitesAtSeal`). §4.2's omission is
   fully shippable without them; §4.3 is written to consume whatever column
   names the data work certifies — the sites input name above
   (`heldSitesEnforced`) is a placeholder for that certification, and the print
   side must be pointed at the enforcer-derived column, **not** at a redefined
   `heldSitesAtSeal`, unless the data work explicitly re-certifies that column's
   derivation.
2. **§2 needs one verification on ROUTINE_SLEEP_STANDDOWN rows.**
   `sealedElapsedSeconds` comes from `row.spanElapsedSeconds` for every kind. On
   a stand-down row (a SLEEP window that closed with no attendance), that
   derivation may be structurally `0` while the window itself ran for hours —
   in which case `RAN 0 MIN` would mint a fresh falsehood on exactly the slips
   Muede already charges. Recommendation: the builder verifies the field's value
   on a stand-down row before merge; if it is not a truthful lived span for
   that kind, stand-down slips omit RAN under the omission law until the field
   is truthful there. All other kinds (manual, FLOW, ATTENDED, PARTIAL) are
   unaffected — their rows tick the span derivation directly.
3. **§3 needs a schema addition, priced small.** Any Room home for the dismissed
   pair (guard_settings column or session-row stamp) is a DB migration —
   v12 → v13 — with the targeted-UPDATE DAO write to match. The audit books
   A9 as batch-10-mergeable ("small, no new surface"); the migration is the
   whole of its cost, and it should ride in the same batch as W-41's split
   columns if that work also migrates, so the receipt pays for one version
   bump, not two.
4. **§1's vocabulary is a living list.** The SHUT allowlist in D1.2 is complete
   for every writer in this tree today (verified by enumeration, §1.2). Any
   future intercept outcome string lands as NEITHER (silent) until it is
   enumerated into the table — that fail-closed behaviour is intended, but
   whoever adds an outcome string must be pointed at this table, or true
   blocks will silently stop printing.

— Designer, Zettel's cell (issue #10). Paper only; nothing compiled.
