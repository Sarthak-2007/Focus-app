# FINISH — THE PARK STUB AND THE DAY-FORTY REPRINT (Setup Sheet, second life)

Seat: **Glatt (finisher, seat 5)**, Pforte's cell, GitHub issue
#14, `Sarthak-2007/Focus-app`, branch `agent/issue-14`.
Scope: ONLY the sheet's second life — audit
`studio/plan/AUDIT-setup-sheet.md` §1 items 24–25, §4 F-12/F-13,
§5 Blank item 4. Paper spec only: no app code edited, nothing
compiled, no tests run (Ren compiles, only when Sarthak says).

Sources read whole or in the cited spans: `ReceptionSheet.kt`
(784 lines in this tree), `ParkDrawer.kt` (612),
`GuardLine.kt`, `WatchStore.kt`, `SetupStore.kt`,
`MainActivity.kt` (host wiring), `MonolithRailView.kt`,
`MonolithCustomComponents.kt` (header), `FocusSequencerScreen.kt`
(`PerforationRow`), `Type.kt`.

**State of the tree, stated before the spec so nobody merges a
ghost:** F-12's move is ALREADY seated in this tree
(`ReceptionSheet.kt:535–547`, comment cites F-12) — §1 below
therefore LOCKS the exact seat instead of re-inventing it. What
is NOT yet done: the direction wording (§1.3, still the plain
note at :541), and the PARK dashed rule (§2, `ParkDrawer.kt:506`
is still a solid `ParkHairline()`). Two divergences found while
verifying are filed as charges in §4 — they are part of this
deliverable, not asides.

---

## §1 · F-12 — THE STUB NOTE ABOVE THE TEAR, EXACT SEAT

### 1.1 The column order at the foot (the whole tail, in order)

Inside `ReceptionSheet()`'s scrolling column, from the total to
the end of the document — this order is the spec; nothing may
be interleaved, added, or left below DONE:

1. `GUARD ${total.count}/4` — only when `total.count != null`
   (unchanged, `ReceptionSheet.kt:521–528`).
2. `Spacer(4.dp)` — only inside that same count branch.
3. Total sentence `total.sentence`, `BodyTextStyle`,
   `onBackground` (unchanged, :529–533).
4. **The stub block — first open only** (`if (isFirstOpen)`,
   :535), in this exact internal order:
   - a. `Spacer(10.dp)`
   - b. Stub note Text (§1.2, verbatim string)
   - c. `Spacer(4.dp)`
   - d. Direction Text (§1.3, verbatim string)
5. `Spacer(12.dp)` — unconditional (:546).
6. `ReceptionTearRule()` (:547).
7. `Spacer(14.dp)`.
8. DONE key box, 52dp, ink, `TO THE DECK →`,
   testTag `reception_done` (:551 ff.).
9. `Spacer(10.dp)`.
10. Act line: `THIS FINISHES THE SETUP. IT STARTS NOTHING,
    SEALS NOTHING, AND MAY BE PRESSED WITH DOORS UNSET.`

Items 1–7 print on the part of the sheet that STAYS; items
8–10 are the torn-off part (below the tear at item 6). The
note describes the part that stays, so it can never again
print below the tear or below DONE (the audit's defect,
§1.24). DONE + act line are the document's last two elements
— nothing follows item 10.

**When `isFirstOpen == false`, item 4 is absent ENTIRE** —
no empty Text, no residue: items 4a/4c's spacers die with the
block (they are inside the `if`, as built). The reprint's
foot is then exactly: sentence → `Spacer(12.dp)` → tear →
`Spacer(14.dp)` → DONE → `Spacer(10.dp)` → act line.

### 1.2 The stub note — string and dress, unchanged

```kotlin
Text(
    text = "THIS SHEET WAITS IN PARK — SETUP + INTRODUCTION.",
    style = PillBadgeTextStyle,
    color = scheme.onSurfaceVariant
)
```

Verbatim as built (:540–544). `PillBadgeTextStyle` = Anonymous
Pro Bold 10sp / lineHeight 12sp / tracking 2sp (`Type.kt:85`).
The tail `— SETUP + INTRODUCTION` is load-bearing: it is the
PARK row's left plate word-for-word (`ParkDrawer.kt:530`),
so the note names the row he will tap. Do not shorten it.

### 1.3 Blank item 4 — the note gains PARK's direction

Immediately under the note (item 4d above), same style, same
colour, one new Text:

```kotlin
Text(
    text = "PARK OPENS BY TAKING THE RAIL CAP TO THE UNMARKED " +
        "END OF THE RAIL, OR BY TAPPING THE GUARD LINE AT " +
        "THE TOP RIGHT.",
    style = PillBadgeTextStyle,
    color = scheme.onSurfaceVariant
)
```

**Every clause is a host fact; nothing is invented:**

| Clause | Host evidence |
|---|---|
| "TAKING THE RAIL CAP TO THE … END OF THE RAIL" | The rail (`MonolithRailView.kt`) has four printed stops (`RailGeometry.WORDS` = DECK·SHIELD·TASKS·LEDGER, :35) and a 48dp PARK zone at the track's end (`PARK_ZONE_DP`, :30; `PARK_INDEX = 4`, :32). A tap on the zone animates the cap to PARK and fires `onParkInvoked` (`tapStop`, :369–370); a drag seated there does the same (`commitStop`, :381–382). Host: `MainActivity.kt:749–755` wires `onParkInvoked` → `openPark()`. |
| "UNMARKED END" | The PARK zone "prints NO word ever" (`MonolithRailView.kt` header, :79–81). The direction must say so, or he will hunt for a PARK label that does not exist. |
| "TAPPING THE GUARD LINE AT THE TOP RIGHT" | The header band (`MonolithCustomComponents.kt:279–296`): brand left (`padding(start = 20.dp)`), Guard line right (`padding(end = 20.dp)`), `clickable` when unsealed. Host: `MainActivity.kt:461–463` `onGuardLineTap` → `openPark()`; the header's own doc calls this tap "the third door into PARK" (:239–244). |

**Truth bounds on the direction (spec notes, NOT extra print):**

- Both named openings run through the one host guard,
  `openPark()` (`MainActivity.kt:305`): PARK opens only when
  `!sealed && !viewModel.navigationLocked()`. The string
  claims no "always" and needs none.
- The Guard-line door exists only while the line prints:
  `guardLineFor` returns null — and the header prints
  nothing — while any counted door is UNMEASURED
  (`GuardLine.kt:36–37`). The rail door is the unconditional
  one, which is why it is named FIRST.
- Deliberately NOT named: PARK's first-run self-invitation
  (`MainActivity.kt:336`, `parkIntroPending() && setupStamped`)
  — the machine opening itself is not a direction for his hand.
- Height budget: at the sheet's 320dp-class column the note
  runs 2 badge lines (24dp) and the direction 3 badge lines
  (36dp); item 4 totals 10 + 24 + 4 + 36 = **74dp above the
  tear**. If a narrower render pushes the direction to 4
  lines, the block grows by exactly 12dp — the words never
  shrink, split, or drop a clause to fit.

---

## §2 · F-13, SECOND HALF — THE DASHED RULE ABOVE THE PARK STUB ROW

### 2.1 The exact edit site

`ParkDrawer.kt:506` — the `ParkHairline()` call that closes
the THEME row and directly precedes the (5b) reception stub
row (:508–541). Replace THAT call, and only that call, with
the dashed rule in §2.2.

Every other hairline in PARK stays solid, by name:
:486 (under SOUND), :541 (under the stub row itself), :545
(above the spec plate), and the board rows' internal ruling
(`ParkBoardRow`, :599). PARK therefore carries exactly ONE
dashed rule — the tear's mark, over the torn stub. A second
dashed rule anywhere in the drawer is a defect against this
spec.

### 2.2 Geometry — the tear's spec, exactly

New private composable beside `ParkHairline` (:605), drawn on
`PerforationRow`'s geometry (`FocusSequencerScreen.kt:772–822`)
at the tear rule's acquitted alpha:

```kotlin
/** The stub's tear mark: the Reception tear rule's spec —
 *  1dp tall, ink at 35%, dash 3dp / gap 3dp in dp (never raw
 *  px), stroke vertically centred in the row. Static: the
 *  stub is already torn; no dashScale motion lives in PARK. */
@Composable
private fun ParkDashedRule() {
    val ink = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f)
    Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
        val y = size.height / 2f
        val dash = 3.dp.toPx()
        val gap = 3.dp.toPx()
        var x = 0f
        while (x < size.width) {
            drawLine(
                color = ink,
                start = Offset(x, y),
                end = Offset(minOf(x + dash, size.width), y),
                strokeWidth = size.height
            )
            x += dash + gap
        }
    }
}
```

- **Dash 3dp / gap 3dp, converted with `dp.toPx()` at draw
  time** — the PerforationRow family geometry (audit §3,
  Letzt F-2: "centred, dash 3dp / gap 3dp in dp"). NOT the
  floats in the as-built `ReceptionTearRule` — see Charge
  G-1, §4.
- **Alpha 0.35f of `onBackground`** — the tear rule's own
  tone (audit §3: "alpha 0.35 acquitted"), distinct from the
  hairline's 0.18f and from PerforationRow's queued-leaf
  0.30 `outlineVariant`: this rule is the SHEET's tear
  reprinted in PARK, so it wears the sheet tear's ink.
- **dashScale is fixed at 1f — do not wire a parameter.**
  The halving dashes (260 specB) belong to Letzt F-1's
  first-open DONE choreography on the sheet; PARK's stub
  is the settled, already-torn edge and never animates.
- **Zero layout delta:** the rule occupies the hairline's
  exact seat — full column width (inside PARK's existing
  20dp horizontal padding), 1dp tall, no spacer added or
  removed on either side. THEME row stays 48dp, stub row
  stays 48dp, PARK's total height does not move by 1dp.

### 2.3 The row's right word — the full Guard line, kept whole

**The shorten is STRUCK and is not specced** (audit §4,
F-13 first half). The row keeps, byte-for-byte:

```kotlin
text = guardLineFor(snapshot, nowMono)?.text ?: "OPEN"   // ParkDrawer.kt:536
```

- When a Guard line exists it prints in FULL — `GUARD 4/4`,
  or the lack form `GUARD {n}/4 — {NAME}` / `… {NAME} STALE`
  (`GuardLine.kt:40–46`) — never a count-only `GUARD {n}/4`,
  never a bare board name. One fact, one print: this is the
  same `GuardLinePrint.text` the header prints
  (`MonolithCustomComponents.kt:281`) and the sheet reprints
  at its doors' foot (`ReceptionSheet.kt:404–415`).
- Dress unchanged: `AppTitleTextStyle` (Atkinson Bold
  15/20), `onBackground`; left plate unchanged
  (`SETUP + INTRODUCTION`, `PillBadgeTextStyle`,
  `onSurfaceVariant`, :529–533); row height 48dp; tap
  unchanged (interactive — visible, settled, unsealed —
  → `onOpenReception()`, testTag `park_reception_row`).
- **Render-check clause (audit §4 tail, §7):** worst case
  is `GUARD 2/4 — NOTIFICATION LISTENER STALE` beside the
  `SETUP + INTRODUCTION` plate. If that overflows at the
  merge render check, the ROW grows a second line — the
  word never shrinks, truncates, or ellipsises.
- The `?: "OPEN"` fallback is NOT endorsed by this section
  — it is a divergence from the one-print law in the null
  state and is filed as Charge G-2 (§4) for Pforte's ruling.
  The F-13 edit does not touch line 536 either way.

---

## §3 · THE REPRINT CONTRACT — THE SAME DOCUMENT AT DAY FORTY

Claim (the sheet's own law, `ReceptionSheet.kt:84–89` and
`ParkDrawer.kt` (5b) comment :508–513): one document at two
moments; day forty's printing is day one's, reprinted with
live states. Verified line by line against the host:

### 3.1 What differs — exhaustively

| # | Element | Day one | Day forty | Mechanism |
|---|---|---|---|---|
| 1 | How it opens | Opens ITSELF at cold start while unstamped | Never self-opens; opened only by tapping the PARK stub row | `receptionInitiallyOpen(setupCompleted) = !setupCompleted` (:108) seeds `receptionOpen` (`MainActivity.kt:279–283`); the stub tap runs `closePark(...); receptionOpen = true` (:691–694) |
| 2 | Stub note + direction (§1, item 4) | Printed above the tear | ABSENT, no residue spacing | The sheet's ONLY `isFirstOpen` branch: parameter :236, sole use :535 (verified by grep — no other branch exists) |
| 3 | DONE's effect | Stamps the setup | Re-runs the identical host function; the stamp is already true, so the visible effect is the same exit | Both printings wire `onDone = { stampSetupAndEnter() }` (`MainActivity.kt`, Reception host call); it calls `SetupStore.markCompleted()` (committed write of `true`, idempotent — `SetupStore.kt:29–31`), sets `setupStamped = true`, discharges the PARK intro bit, closes, navigates to DECK (:285–293) |

### 3.2 What does NOT differ — including the nameplate

- **Nameplate: `MONOLITH ML-01` on BOTH printings.** The
  one line that could have forked the document is cured by
  S-13 the only lawful way — the suffix `· FIRST OPEN` is
  deleted from the document entirely, not switched by
  state. The plate is an unconditional `Text("MONOLITH
  ML-01")` (`ReceptionSheet.kt:317–324`, comment cites
  S-13: "it names the stamp, not the opening"). Any builder
  who re-adds a conditional suffix — in either direction —
  breaks the contract.
- Title `SETUP + INTRODUCTION` (:325–329), identical — and
  identical to the PARK row's plate (C-1, one document,
  one name).
- Every other printed string in Movements 1–3 — identity,
  boundary line, door jobs and escorts, rank line, the six
  beats, rules line, leave line, total sentence branches,
  tear, DONE words, act line — is the same source string
  in both printings. There is no second template, no
  "returning user" copy, anywhere in the file.
- Exit choreography: a PARK reprint's DONE keeps today's
  behaviour — the tear phase (Letzt F-1) fires only when
  the press actually stamps, i.e. first open (audit §3).

### 3.3 What MAY differ — live values, not wording

Derived at render on both printings, from the instant's
snapshot: each door's word (`SET` / `NOT SET` / `UNPROVED`
/ `NOT CHECKED`) and its FIX/CHECK key, the doors'-foot
Guard line, the total's count and sentence branch, the
first-routine line (next live routine at that instant,
`receptionFirstRoutineLine(routines, nowMillis)`), and the
night dress (`isNight`). A door repaired in forty days
prints `SET` on the reprint and `NOT SET` in PARK's memory
of day one — that is the reprint's purpose, not a fork.

### 3.4 Proof the two printings can never disagree

1. **One snapshot.** The host collects ONE flow —
   `val watchSnapshot by viewModel.watchSnapshots…`
   (`MainActivity.kt:173`) — and passes the same value to
   `ParkDrawer(snapshot = watchSnapshot)` (:681) and to
   `ReceptionSheet(snapshot = watchSnapshot)` (:818).
   Neither surface reads any other store for a watch fact.
2. **One derivation.** `WatchStore.permPrintAt(snapshot,
   item, nowMono)` (`WatchStore.kt:355`) is the only
   permission-print function, and every surface calls it:
   PARK's board and FIX-key lack test (`ParkDrawer.kt:163,
   327, 333`), the sheet's door cards (:374) and its total
   via `receptionTotal` (:192), and `guardLineFor`
   (`GuardLine.kt:36`) — which itself feeds the header,
   the sheet's doors'-foot print (:404), and the PARK
   stub row's right word (:536). The door words are a pure
   reprint map over that same `PermPrint` (`receptionDoorWord`,
   :100–105: SET ≡ GRANTED, NOT SET ≡ MISSING, UNPROVED ≡
   STALE, NOT CHECKED ≡ UNMEASURED) — a second vocabulary,
   never a second fact.
3. **One instant per surface.** Within each composition
   pass, a single remembered `nowMono` (re-derived on the
   same 5-second beat in both files — `ParkDrawer.kt:154–161`,
   `ReceptionSheet.kt:269–276`) feeds every derivation on
   that surface, so a surface never disagrees with itself
   mid-print.
4. **Never simultaneous.** The stub tap closes PARK in the
   same input that opens the sheet (`MainActivity.kt:691–694`),
   and the host gates the sheet on `!sealed`. The board and
   the sheet are never on screen at two different instants
   to be compared.
5. **The bound of the proof, stated honestly:** equality
   holds at equal (snapshot, nowMono). Between two
   printings the snapshot advances (sweeps land; a GRANTED
   proof ages past `STALE_AFTER_MS = 45_000`,
   `WatchStore.kt:148`, and prints STALE). That drift is
   time passing over one derivation — the definition of a
   live reprint — not two derivations disagreeing. At the
   same snapshot and the same instant, sheet, board, stub
   word, and header print the same fact, by construction.

---

## §4 · CHARGES (divergences found, with line evidence)

**G-1 · The as-built tear rule is not at the tear's spec —
BLOCKS a blind copy for §2.** `ReceptionTearRule`
(`ReceptionSheet.kt:773–782`) draws
`PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)` — raw
PIXELS, not dp. That is ≈2.7dp/2dp at density 3, 4dp/3dp
at density 2, 8dp/6dp at density 1: the tear has no fixed
geometry until Letzt F-2 (audit §3) redraws it on
PerforationRow's 3dp/3dp. §2 is therefore specced from the
3dp spec directly, NOT by copying or calling
`ReceptionTearRule`. When F-2 lands on the sheet's tear,
the merge render check must confirm both rules read
dash 3dp / gap 3dp @ 0.35 — until then the sheet's own
tear, not PARK's new rule, is the off-spec print.

**G-2 · The stub row's `OPEN` fallback forks the Guard
fact in the null state.** `ParkDrawer.kt:536`:
`guardLineFor(snapshot, nowMono)?.text ?: "OPEN"`. When
`guardLineFor` returns null (snapshot null, or any counted
door UNMEASURED — `GuardLine.kt:35–37`), the header prints
NOTHING (`MonolithCustomComponents.kt:279`, `if (guardLine
!= null)`) and the sheet's doors' foot prints NOTHING
(`ReceptionSheet.kt:405`, same guard) — but the stub row
prints `OPEN`, a right word derived from no measurement,
in the exact slot §2.3 reserves for the Guard fact.
Recommendation to Pforte: rule it explicitly — either
(a) acquit `OPEN` by name as the row's INVITATION word
(not a Guard print) and write that into the (5b) comment
at :508–513, or (b) print no right word in the null state,
matching both other surfaces. Glatt's lean: (a) — the
unmeasured state is precisely the first-run state where
the row must still invite — but as built it is an
undocumented fork, and it is not cured by silence.

**G-3 · Note spacer variance, 2dp (minor).** Fibel's
DESIGN-composition §11f seats the moved note with a
`Spacer(12.dp)` above it; the tree as built uses
`Spacer(10.dp)` (`ReceptionSheet.kt:538`). §1.1 pins
**10dp** (the as-built seat, continuous with the note's
original below-DONE spacer). Pforte ratifies one number;
the builder must not stack both.

---

## §5 · MERGE RENDER CHECKS (Glatt's list, for the compile Ren runs)

1. First open: foot reads total → note → direction → tear
   → DONE → act line, in that order, nothing after the act
   line; note + direction visibly ABOVE the dashed tear.
2. Day-forty reprint (stamped store, opened from PARK):
   identical sheet, no note/direction, tear 12dp under the
   total sentence, nameplate reads `MONOLITH ML-01` with
   no suffix.
3. PARK: exactly one dashed rule — 3dp/3dp, visibly the
   tear's family against the solid 0.18 hairlines — sitting
   directly above the `SETUP + INTRODUCTION` row; row word
   in the worst case `GUARD 2/4 — NOTIFICATION LISTENER
   STALE` printed whole (second line if needed, per §2.3).
4. Same-instant agreement: with a fixed snapshot, PARK's
   board, the stub word, the header line, and a reprinted
   sheet's doors'-foot line print the same Guard text —
   expected by construction (§3.4); the capture is the proof.

— Glatt (finisher), issue #14 cell
