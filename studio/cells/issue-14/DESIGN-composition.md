# DESIGN — COMPOSITION SPEC FOR MERGE ITEMS 11–15 (Setup Sheet)

Seat: **Fibel (designer, composition as print)**, Pforte's cell,
GitHub issue #14, `Sarthak-2007/Focus-app`, branch `agent/issue-14`.
Source of law: `studio/plan/AUDIT-setup-sheet.md` §1, §4, §6.
Scope: merge-list items **11, 12, 13, 15** (§6) — composition +
finish only. Truth wording (items 1–6), mechanism (7–10) and the
stranger lines (14) belong to other seats; where my items touch
their seats I spec the *seat and the form* and name the interface,
never their words. Paper work only — nothing here is compiled.

Conventions for the builder:

- All line numbers are the **pre-merge tree** (`ReceptionSheet.kt`
  692 lines, `ParkDrawer.kt` as read Oct 30). Apply items in §6
  order; earlier items shift later line numbers — anchor on the
  quoted code, not the number alone.
- Type references are the real ladder (`Type.kt`): `PillBadgeTextStyle`
  = Anonymous Pro Bold 10/12, tracking 2sp. `BodyTextStyle` = Atkinson
  400 15/22. `AppTitleTextStyle` = Atkinson Bold 15/20. No new sizes
  are introduced anywhere in this spec — the ladder has six and
  every change below is a *reassignment* to an existing rung.
- Height budgets are paper estimates calibrated to the audit's own
  measured totals (all-SET ≈1,715dp, all-MISSING ≈2,190dp,
  untouched ≈1,950dp, viewport ≈760dp). My per-element rebuild
  lands within ~3% of those totals; treat every dp figure as ±5%.
- Inventions are marked **INV-n** with a price. Everything unmarked
  is the audit's ruling, executed.

---

## ITEM 11 — the FREE six (audit §6.11)

### 11a · F-1 — KILL the `THE DOOR` eyebrow

- **Location:** `ReceptionSheet.kt`, `ReceptionSheet()` Movement 1,
  lines 283–289 (the `Text("THE DOOR", …)` and the `Spacer(8.dp)`
  under it).
- **Change:** delete both, entire. Movement 1 opens directly on the
  one-liner after the title hairline's `Spacer(16.dp)` (line 281).
  Update the movement comment to `// ---- MOVEMENT 1 · THE IDENTITY ----`.
- **No replacement eyebrow.** **INV-1:** §1.4 permits re-eyebrowing
  `WHAT IT IS` or none; I rule **none**. `WHAT IT IS` is a label
  that says nothing the one-liner does not say in its first three
  words, and Movements 2/3 keep their eyebrows because theirs
  *count and name* (`THE FOUR DOORS`, `HOW A SESSION RUNS`).
  Movement 1 is framed by the nameplate + title + hairline already;
  the asymmetry is deliberate print discipline, not a gap.
  Price: 0dp, one less label to read at 04:00.
- **Effect:** −20dp (12 badge line + 8 spacer). The sheet then
  counts doors in exactly one eyebrow (§1.4's charge cured).

### 11b · F-2 — the one-liner takes AppTitle weight

- **Location:** lines 290–294, the `Text("Monolith shuts …")`.
- **Change:** `style = BodyTextStyle` → `style = AppTitleTextStyle`
  (Atkinson Bold 15, lineHeight 20sp). Colour unchanged
  (`scheme.onBackground`). Words unchanged *by this item* — S-7's
  cured wording (merge item 3) drops into the same call and takes
  this weight with it.
- **Effect:** the identity line becomes the heaviest prose on the
  sheet below the title — first-read hierarchy fixed. Height
  44dp → 40dp (2 lines × 20sp lineHeight): −4dp.
- **Import note:** `AppTitleTextStyle` is already imported (line 48)
  and its only other use is the total, which item 11e removes —
  the import stays live through this item. Do not clean it up.

### 11c · F-3 — the second sentence is killed

- **Location:** lines 295–301 (the `Spacer(6.dp)` at 295 and the
  `Text("You seal a session. The phone becomes the room. …")`).
- **Change:** delete both, entire. The boundary line (lines 304–309)
  keeps its existing `Spacer(8.dp)` (line 302) as its gap from the
  one-liner. No spacer is added back.
- **Effect:** −50dp (6 spacer + 2 lines × 22). The miniature of
  Movement 3 dies; the beats print the thing in full, once (§1.6).

### 11d · F-10 — the routine line moves above the total

- **Location:** the `if (firstRoutineLine != null) { … }` block,
  lines 402–409 (currently inside Movement 3, after the beats),
  and the total block at lines 459–472.
- **Change:** cut the whole block from 402–409 (including its
  leading `Spacer(4.dp)`). Re-seat it in the foot, immediately
  before `val total = receptionTotal(…)` (line 459), i.e. after
  the `Spacer(20.dp)` at line 454, in this exact form:

  ```kotlin
  if (firstRoutineLine != null) {
      Text(
          text = firstRoutineLine,
          style = PillBadgeTextStyle,
          color = scheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(10.dp))
  }
  ```

- **Seat only.** The string's derivation and wording are S-2's
  (merge item 2); this item moves whatever string arrives. When
  `firstRoutineLine == null` the LEAVE→total distance is exactly
  as built (20dp) — the move is height-neutral in every state.
- **Why here:** the routine line is a fact *about his standing
  setup*, grammatically kin to the total, not to the beats. It now
  reads as the total's first line: routine, then count, then
  sentence.

### 11e · F-11 — the total in Anonymous Pro Bold 15

- **Location:** lines 460–467, the `Text("GUARD ${total.count}/4", …)`.
- **Change:** replace `style = AppTitleTextStyle` with the explicit
  telemetry print — byte-for-byte the PARK datum print
  (`ParkDrawer.kt`, the LAST PROVED value) so the sheet's one datum
  and the board's datums are one voice:

  ```kotlin
  Text(
      text = "GUARD ${total.count}/4",
      fontFamily = AnonymousProFamily,
      fontWeight = FontWeight.Bold,
      fontSize = 15.sp,
      letterSpacing = 0.5.sp,
      color = scheme.onBackground
  )
  ```

- The total *sentence* (lines 468–472) keeps `BodyTextStyle` —
  that split is the ruling's whole point (§4 F-11): count in mono,
  sentence in prose, the receipt's grammar.
- **Imports:** add `com.example.ui.theme.AnonymousProFamily`
  (ParkDrawer already imports it from the same package path).
  `FontWeight`, `sp` already imported. Height unchanged (15sp
  datum, ~20dp line, + the existing `Spacer(4.dp)`).

### 11f · F-12 — the stub note moves above the tear

- **Location:** the `if (isFirstOpen) { … }` stub block, lines
  506–513 (currently *below* DONE), and the tear seat at lines
  473–475.
- **Change:** delete the block from below DONE entire. Re-seat it
  between the total sentence and the tear rule:

  ```kotlin
  Text(text = total.sentence, style = BodyTextStyle, …)   // unchanged
  if (isFirstOpen) {
      Spacer(modifier = Modifier.height(12.dp))
      Text(
          text = "THIS SHEET WAITS IN PARK — SETUP + INTRODUCTION.",
          style = PillBadgeTextStyle,
          color = scheme.onSurfaceVariant
      )
  }
  Spacer(modifier = Modifier.height(12.dp))
  ReceptionTearRule(dashScale = tearDashScale)            // item 13 form
  Spacer(modifier = Modifier.height(14.dp))
  // DONE box follows — now the sheet's last element
  ```

  (The existing `Spacer(12.dp)` at line 473 is consumed as the
  spacer above: when `isFirstOpen` is false it separates sentence
  from tear exactly as today; when true, the note sits inside the
  same 12+note+12 envelope. Do not stack both spacers.)
- **Why:** as built the note prints on the torn-off part — it
  describes where the sheet waits from *below* the separation it
  survives. Above the tear it prints on the stub that stays (§1.24).
  DONE becomes the final element; the sheet ends with an act.
- **Interface (item 14):** the note's wording gains PARK's real
  direction from the stranger-lines seat. Budget the note at
  **2 badge lines (24dp)** in all height maths below — if the
  direction pushes it to 3 lines the K-2 figures in §K grow by 12dp.
- **Effect on DONE's position:** +36dp of tail above the tear
  (12 spacer + 24 note) — counted honestly in §K. Net sheet
  length is unchanged only if you ignore that the old seat's
  10dp spacer + 12dp line below DONE are deleted: net **+14dp**
  of total length, all of it above the tear where it belongs.

---

## ITEM 12 — the card family (audit §6.12)

All four changes live in `ReceptionDoorCard` (lines 522–644) plus
one restructure in the parent loop (lines 319–336). Do them
together: they share one signature change.

**Parent restructure (do first).** Replace the `forEach` at
319–336 with a precomputed print list so F-5 can know which card
is first:

```kotlin
val prints = WatchItemId.COUNTED.map { item ->
    item to (snapshot?.let { WatchStore.permPrintAt(it, item, nowMono) }
        ?: PermPrint.UNMEASURED)
}
val firstMissingIdx = prints.indexOfFirst { it.second == PermPrint.MISSING }
prints.forEachIndexed { idx, (item, print) ->
    ReceptionDoorCard(
        item = item, print = print,
        escortFull = idx == firstMissingIdx,
        /* isNight, interactive, fixPressed, soundEngine, onFix, onCheck as today */
    )
}
```

`ReceptionDoorCard` gains one parameter: `escortFull: Boolean`.

### 12a · F-4 — ink discipline: FIX takes surfaceVariant

- **Location:** the `PermPrint.MISSING` branch, lines 574–610;
  specifically `.background(inkControlFill())` (line 582) and the
  key text's `color = onInkControlFill()` (line 607).
- **Change:**
  - `.background(inkControlFill())` → `.background(scheme.surfaceVariant)`
  - `color = onInkControlFill()` → `color = scheme.onBackground`
- That is all. Height 48dp, radius 8dp, the 1dp press dip
  (line 580), the seat knock (lines 590–594), the testTag — all
  untouched. The result is byte-identical dress to the CHECK key
  (lines 618/635) and to `OPEN THE LIST` (lines 427/444): three
  keys, one dress, and **ink remains on exactly one key in the
  sheet — DONE** (line 485, untouched by this item). The sheet
  header's "one ink key at the foot" claim becomes true.
- **Out of scope, stated so nobody "fixes" it:** PARK's own FIX
  key (`ParkDrawer.kt` line 385) keeps its ink. PARK is the board,
  its material law (§8.10: print except the FIX key) is its own;
  F-4 rules this sheet only.

### 12b · F-5 — escort short forms

- **Location:** `receptionDoorPrepare` (lines 129–143) stays the
  full escort, word-for-word (its sentences are cured by S-5/S-6
  at merge item 3 — this item does not touch a word of it). The
  prepare `Text` in the card is lines 562–572.
- **Change:** add a second pure function beside it:

  ```kotlin
  /** The short list form (F-5): the shared frame — a list, find
   *  Monolith, come back — is printed ONCE, on the first unset
   *  card. Later cards print only their door-specific clause,
   *  plus their Android warning WHOLE where one exists. */
  fun receptionDoorPrepareShort(item: WatchItemId): String = when (item) {
      WatchItemId.ACCESSIBILITY ->
          "FIND MONOLITH IN THE LIST, SWITCH IT ON. " +
              "ANDROID WARNS IN STRONG WORDS — THAT WARNING IS STANDARD " +
              "FOR EVERY BLOCKING APP. IT IS THE DOOR WORKING."
      WatchItemId.USAGE_ACCESS ->
          "FIND MONOLITH IN THE LIST, ALLOW USAGE ACCESS."
      WatchItemId.DEVICE_ADMIN ->
          "FIND MONOLITH IN THE DEVICE ADMIN LIST, ACTIVATE."
      WatchItemId.NOTIFICATION_LISTENER ->
          "FIND MONOLITH IN THE LIST, ALLOW. " +
              "ANDROID MAY WARN — THAT WARNING IS STANDARD. " +
              "IT IS THE DOOR WORKING."
      else -> ""
  }
  ```

  **INV-2 — these strings are my composition, priced here.**
  The form rule they execute: short form = [find + the door's own
  verb] + [the warning sentences, verbatim from the full escort,
  if the door has one]. Nothing else survives the cut — no
  "YOU'LL SEE", no "COME BACK" (the return is the sheet's standing
  law by the second card; the first card already taught it).
  Warnings are kept *whole*, per the brief and A-6: accessibility
  keeps both warning sentences, listener keeps both of its.
  **Sync law for the builder:** the DEVICE_ADMIN short form is
  written against S-5's *cured* full escort ("LIST OF DEVICE
  ADMINS") — if item 3 lands different admin words, the short
  form's noun follows the cured full form, never the old
  "MONOLITH'S OWN PAGE" (that page does not exist, S-5).
  Price: −17dp per later unset card (one 12/17 escort line each:
  full escorts run 2–4 lines at ~55 chars/line in the 320dp
  column; shorts run exactly one line less in every door's case).
- **Card change (lines 562–572):** the prepare text becomes
  `text = if (escortFull) receptionDoorPrepare(item) else receptionDoorPrepareShort(item)`.
  Style unchanged (Atkinson 400 12/17, `onSurfaceVariant`).
- **Who is "first":** first **MISSING** card in COUNTED order,
  recomputed every recomposition from the precomputed list above.
  STALE cards print no escort (they gain S-12's gloss — item 4's
  seat, in the same `if` region; do not collide: gloss replaces
  nothing here). UNMEASURED cards print no escort at all (S-12)
  and therefore never consume the "first" slot — a sheet that
  measures late still gives its first real lack the full escort.
- **Tests:** the existing 13 tests touch neither function. The
  builder adds one pure test for the short form (door order +
  warning-wholeness) — flagged, not specced; Pforte prices it
  with item 12's SMALL.

### 12c · F-6 — granted doors collapse to one ruled line (~37dp)

- **Location:** top of `ReceptionDoorCard` (after line 533,
  before the card `Column`'s first spacer).
- **Change:** early return for the granted state:

  ```kotlin
  if (print == PermPrint.GRANTED) {
      Column(modifier = Modifier.testTag("reception_door_${item.name}")) {
          Spacer(modifier = Modifier.height(8.dp))
          Row(
              modifier = Modifier.height(20.dp),
              verticalAlignment = Alignment.CenterVertically
          ) {
              Text(text = item.boardName, style = PillBadgeTextStyle,
                  color = scheme.onSurfaceVariant)
              Spacer(modifier = Modifier.weight(1f))
              Text(
                  text = "SET",
                  fontFamily = AtkinsonHyperlegibleFamily,
                  fontWeight = FontWeight.Bold,
                  fontSize = 15.sp, letterSpacing = 0.5.sp,
                  color = scheme.onSurfaceVariant
              )
          }
          Spacer(modifier = Modifier.height(8.dp))
          ReceptionHairline()
      }
      return
  }
  ```

  Geometry: 8 + 20 + 8 + 1 = **37dp**, against the expanded
  granted card's ~89dp — **−52dp per granted door, −208dp at
  4/4**. The SET word keeps its standing granted dress
  (`onSurfaceVariant`, Atkinson Bold 15 — the dim SET is acquitted,
  Letzt A-3): a finished line recedes; the eye lands on what is
  still open. Name, word, hairline — the board row's grammar,
  nothing else. No job, no escort, no key, no animation of its
  own (the shrink *into* this form rides the key-exit pair,
  item 13b: the FIX key exits over 160ms inside the card, then
  the card recomposes collapsed — sequence them key-first so the
  card never snaps in one frame).
- **Interface (Return Chit, item 7):** the chit prints one line
  *in the visited card* and bolds its SET. A collapsed card has
  no second line to give. Ruling for the mechanism seat: on the
  chit's printing only, the collapsed line grows the chit line
  beneath the SET row (card 37 + 17 = 54dp for that printing,
  hairline still last). My collapse yields to the chit for
  exactly one printing; it does not permanently re-expand.
  Flagged for Pforte/Funke — not built by this item.

### 12d · F-7 — the key band is reserved while UNMEASURED

- **Location:** the `when (print)` at lines 573–640. Today
  UNMEASURED falls to `else -> Unit`: no escort (already),
  no key, no height — so the sweep's arrival *grows* the card
  and shoves everything below it down. That jump is the defect.
- **Change:** give UNMEASURED an explicit branch that reserves
  the key band at the exact height the landed key will occupy —
  **58dp** (10 spacer + 48 key), the same envelope FIX and CHECK
  already use:

  ```kotlin
  PermPrint.UNMEASURED -> {
      if (snapshotIsLive) {
          // S-12's CHECK key (item 4) renders here, in this slot.
      } else {
          Spacer(modifier = Modifier.height(10.d
...[truncated 15761 chars]