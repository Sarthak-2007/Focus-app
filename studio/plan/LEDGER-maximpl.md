# LEDGER — the Maximum-Implementable verdict (build-cost lens)

**Question reopened by Sarthak:** keep the Ledger, demote it, or kill it?
**This file prices only.** What the record *means* — ritual, pride, the screenshot
surface — is other cohorts' lenses (HUMANS, DESIGNERS). Here: what each option
costs to build, what it costs to keep, what code and data die or survive.
**Verdict up front: (b) demote — a sheet over the Deck, sharing its rows with the
session-end receipt.** Reasoning and the strongest case against it below.

All line counts from the tree as of Oct 29, 05:20 IST.

---

## 1. Ground truth: what exists regardless of the verdict

These survive **every** option, including kill. Any pricing that counts them as
savings is lying:

- **The tables.** `FocusSessionEntity` and `InterceptLogEntity` (Room) are the
  app's core record — session recovery, the shield's own log. Not Ledger property.
- **The aggregates.** `totalFocusedMinutes` and `totalInterceptions` are
  ViewModel StateFlows (MonolithViewModel.kt:280, 289) already consumed by the
  status drawer and the chrome. The Ledger is one rendering among several, not
  the data's owner.
- **The HMAC — correction of the brief's framing.** `CryptoIntegrityManager`
  (194 lines) is not receipt decoration. It signs and verifies session state in
  `MonolithRepository` — tamper detection, the 15-minute penalty lock, reboot
  recovery. It survives all three options untouched. The only HMAC that can die
  is the printed `HMAC•XXXXXXXX` stamp on receipt rows: eight characters of
  theatre over a real system that keeps working in the dark.
- **The accession number is free.** `FocusSessionEntity.id` is
  `@PrimaryKey(autoGenerate = true)`. A sequential provenance № is the id,
  formatted. Zero DB migration, zero new fields, under any option that wants it.
- **The 7-day stats** (`compute7DayHistory`, Repository) are computed lazily,
  today only when navigating to VAULT (MonolithViewModel.kt:362). The compute is
  small and movable; the renderings are the cost, not the math.

## 2. What the Ledger is today, priced as-is

Not a free status quo — the current tab breaks the TRUTH standard daily:

- `CurioVaultScreen.kt` — 288 lines, one destination, five bespoke surfaces.
- **Live lies in the shipped build:** the SESSIONS tile prints
  `sessionHistory.size.coerceAtLeast(4)` — at least 4 sessions even when there
  are none. Tower bricks are `coerceAtLeast(16)` — a monument floored at 16
  bricks regardless of fact. The receipts list renders `take(8)` under a header
  that prints the full TOTAL. Total focused minutes render three-plus times on
  one screen (tile, bricks, tower) — one fact, three renderings, two of them
  falsified by floors.
- **Bespoke view surface:** `TileNumberCounterView`, `DayFadersView`,
  `TowerStreakView` — three of the four classes in
  `BlockitTowerFadersAndCatalogViews.kt` (416 lines, shared with the horizon
  bar). **TowerStreakView has a second tenant:** the streak-monument overlay
  summoned from the drawer (`AirlockAndDisarmOverlays.kt:344`). The tower
  cannot die with the tab unless that overlay dies or is rehomed with it.
- **Dead wiring already on the tab:** the KineticArtifact / curios machinery
  (Models + ViewModel + screen props) is Vault-scoped and inert. It dies under
  every option including rebuild — nobody's plan revives it.
- **A squatter:** the AES-GCM backup EXPORT button lives on this screen. The
  export itself (`MonolithRepository.exportEncryptedBackup` + ViewModel flow)
  is option-independent; the *button* needs a home under (b) and (c) or the
  feature is stranded — alive in the repository, unreachable in the UI.

## 3. Option (a) — keep, rebuilt as a tab (SYNTHESIS item 31 scope)

**Build price: the highest of the three.**
Full screen rewrite (the 288 lines are replaced, not edited): one 64sp hero,
one mono stats line, hairline receipt rows with accession №s, a 7-day column
chart with printed scale (DayFaders rebuilt or replaced), absence rendered as
absence, DAY CLOSED state. Cross-screen work: the finish ceremony (accession
card at completion) lives half on this screen, half on the session-end surface.
Deletions inside the option: dot-matrix tiles, the HMAC print, the curios
wiring, the truth-floors. The tower either dies (then its overlay tenant is
rehomed — extra work) or survives amputated. Nav keeps the stop forever.

**Keep price: a fifth surface under permanent truth-audit.** Every future
change to sessions, routines, attendance, or the receipt must be mirrored here;
every rendering on this screen is a standing TRUTH liability (this screen is
where the current build's worst lies live). The data it uniquely holds —
deflections, holds, attendance — is a minority of its content; the majority
re-renders what YPT and the drawer already say.

## 4. Option (b) — demote

The deletions are ≈ kill's deletions (screen, tiles, faders, curios, tower —
subject to the overlay tenant; nav stop removed). The difference is the
retained surface. Three forms, priced:

**(b1) Sheet over the Deck — the recommended form.**
New build: one sheet composable + open-state hoisting in the Deck host. Its
rows are the session-end receipt's rows (Freya's spec) — one row implementation,
two presentations, shared upkeep. Contents: today's sessions + receipts list +
attendance/MISSED entries + the 7-day truth in one mono line or a minimal
column strip (not the fader rebuild). The compute trigger moves from
navigate-to-VAULT to sheet-open (one-line change, MonolithViewModel.kt:362).
Backup button: one chip inside the sheet or in the drawer — a move, not a build.

**(b2) A section inside Shield.**
Same renderings, worse rent. Shield is already the longest, most-edited screen
(routines hero + Section strip + apps list, furniture budgeted ≤35% of first
viewport). A RECORD section pays that screen's complexity tax on every future
Shield change and mixes the perimeter with the archive in one scroll.
Build ≈ (b1), keep-price strictly worse. Rejected on price.

**(b3) Chrome corner (the status drawer).**
Cheapest build of any option — the drawer already receives both totals today;
receipts become drawer items. But the drawer is also the leading candidate home
for Guard's residue if D-B dissolves it: two demotions landing in one drawer
make it the everything-drawer, and a glance surface is the worst legibility
home for a list. Cheap to build, expensive to live in. Rejected on keep-price.

## 5. Option (c) — kill

**Build price: negative — it pays back.** Deletions: screen (288), Vault-only
views (tiles + faders; tower partial — see the overlay tenant), curios wiring,
the VAULT destination + MainActivity wiring (L227/401/496), the Deck's
`onOpenCurioVault` entry point, the navigate-triggered stats compute. Net
several hundred lines removed, zero migration (tables stay; §1).

**The hidden bill — attendance becomes write-only.** Sten's settled no-ghost
rule (main plan, 05-sten-routines) writes an attendance record: attended
minutes, MISSED entries, 3-miss auto-pause notes. That record is the TRUTH
standard's evidence. Under kill there is **no surface anywhere** where its
owner can inspect it. The first time a routine books a MISSED he disputes,
there is no courtroom — the app asserts, he cannot check. Kill does not delete
this need; it exports it, unpaid, to whichever screen later grows a surface
for it — which is a demote performed later, in a worse place, at a worse time.
Also stranded: the backup button (§2) and DAY CLOSED's one loved fact (day
total against target) — the latter survives only as a line on the session-end
receipt, nothing at day scale.

## 6. Dependency flags (main plan proceeds with Ledger OPEN)

- **Otis (09, chrome):** nav stop count branches on this verdict AND on D-B.
  His two-branch spec stands; this file does not collapse it.
- **Freya (10, session surfaces):** the session-end receipt is specced
  regardless; under (b1) its rows are shared with the sheet — one
  implementation. Under (a) its archive home is the tab; under (c), the DB.
- **Sten (05, routines):** the attendance record's minimum truthful surface is
  specced on the routine row itself (last-firing outcome word) so the truth
  survives even (c); its inspection home is this verdict's question.
- **Nadia (08, horizon):** tower/faders/horizon share one file
  (`BlockitTowerFadersAndCatalogViews.kt`). Ledger deletions and horizon
  deletions interleave in that file and must land as one edit, whoever wins.
- **MainActivity:** VAULT wiring (import, destination branch, Deck entry
  callback) is touched by every option except keep-as-is, which is not on
  the table.

## 7. Verdict

**(b1) — demote to a sheet over the Deck.**

The cost logic, in one paragraph: the expensive layers — tables, aggregates,
integrity, the receipt — survive every option, so kill's headline savings are
mostly deletions that demote makes too. What the tab charges above the sheet
is a permanent destination, a 288-line screen, and two-to-three bespoke Canvas
views under lifelong truth-audit; what that premium buys over the sheet is
*presence*, a meaning-quality, not a capability — every fact on the tab is
reachable in the sheet at the place sessions actually start and end. What
rebuild (a) charges above the sheet is larger still, for the same facts.
And kill, the only cheaper option, fails a functional requirement created by
settled law: the attendance record must be inspectable by its owner. The
sheet is the cheapest surface that satisfies it, and it shares its upkeep
with the receipt he already sees at every session end.

## 8. The strongest case against this verdict

Demote is the option that decides least, and it pays both prices. It keeps a
record surface's upkeep forever — the sheet, its rows, its stats compute, its
truth-audit — *and* it taxes the one surface the ARM standard protects most:
a RECORD affordance now sits on the Deck, competing with the slide for the
eye at 03:30, every day, forever. If he never pulls the sheet — and sheets
are where records go to be forgotten — then (b1) is simply kill with rent:
the honest price was (c), paid once, and this file chose the coward's middle.
Worse: presence is not nothing. A tab is a place you *go*; a sheet is a place
you *fall into*. If he in fact visits his record — weekly, say, the way he
checks YPT — then (a)'s premium amortises to nearly nothing per visit and
this verdict saved a small build cost by demoting the app's memory to a
drawer flap.

**Why the verdict stands anyway:** the sheet's marginal cost over kill is
one bounded composable whose rows already exist for the receipt; its
distinct upkeep rounds to the receipt's. The attendance-inspection
requirement is not a taste that might vanish — it is a consequence of the
settled no-ghost law, and kill meets it at no price. And (b1) is the only
option that degrades gracefully: if a month proves the sheet unopened,
deleting it collapses (b1) into (c) in one small edit, having lost little;
if the record turns out to be visited and loved, the sheet's contents are
exactly the rebuilt tab's contents, promoted back at known cost. Demote is
not the middle because it is timid. It is the middle because it is the only
option that keeps both doors open at a price near the cheaper one.

— **Voss**, Maximum-Implementable coordinator, Oct 29 2026.
*Priced from the code. Nadia's independent cost take lands in
plan/maximpl-parts/08-nadia-horizon.md and may dispute these numbers —
if it does, the dispute is part of the record, not an error.*

---

## Addendum — independent cross-check (Nadia, 08-nadia-horizon.md, Assignment 2)

Nadia priced the same question blind and her numbers agree with §3–§5:
delete = 4–6 files touched, ~0 new code, zero migration; rebuild = 3–4
files plus one new printed-scale column chart (~120–180 lines), still zero
migration (accession from the session id; `compute7DayHistory` already emits
zero days). She independently confirms §1's correction: the HMAC half of
`CryptoIntegrityManager` is session-integrity infrastructure and survives
every option. New facts from her read: `decryptBackupAesGcm` has zero
callers; 6 of CurioVaultScreen's parameters are never read by its own body;
the Deck's `onOpenCurioVault` is wired but never invoked.

**Her one-line lean: delete.** Recorded, not smoothed over. The verdict in
§7 stands on the one argument her lens was instructed not to weigh: the
attendance record created by Sten's settled no-ghost law must be
inspectable by its owner, and delete provides no surface at any price —
the need re-emerges later as an unplanned demote in a worse place. On pure
build price she is right and §5 already says so: kill is the cheapest
option to build. The disagreement is what the price buys, and it is
Sarthak's to settle with both numbers in front of him.
