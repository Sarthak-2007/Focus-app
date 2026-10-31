# FINISH — THE TEAR AND THE EXIT (Setup Sheet, merge item 13)

Seat: **Letzt (finisher)**, Pforte's cell, GitHub issue #14,
`Sarthak-2007/Focus-app`, branch `agent/issue-14`.
Scope: merge-list item **13 only** (AUDIT-setup-sheet §6.13,
§3 "Letzt F-1/F-2/F-3 SHIP") — the DONE tear choreography,
the tear-rule geometry, and the FIX/CHECK key visibility pair.
Paper spec only: nothing compiled, nothing tested, no app
code edited by this seat. The lead applies this verbatim;
every value below is decided — no judgment calls remain.

Sources read whole / in part, this tree, pre-change:
`ReceptionSheet.kt` (784 lines), `MainActivity.kt`
(stamp host + Reception binding), `ParkDrawer.kt` (the
visibility pair, lines 369–375), `FocusSequencerScreen.kt`
(`PerforationRow` lines 772–818, the counterfoil tear
`onTearLeaf` + its two animatables), `MonolithCurves.kt`
(`MonolithCurves` / `MonolithKin`), `SetupStore.kt`,
`TactileSoundEngine.kt` (`playTearPulse`, `playFirmClick`).
Line numbers are pre-change anchors — anchor on the quoted
code when they have shifted.

Naming warning: this document's **F-3 is Letzt F-3**
(AUDIT §3: the key visibility pair). Fibel F-3 in AUDIT §4
(the killed second sentence) is a different item, already
in the code, and is not touched here.

---

## §0 · FLAGS FIRST — what is impossible as built

**FLAG A (blocking, cured by §1.2).** The tear cannot be
built inside `ReceptionSheet.kt` alone. Today DONE's
`onPress` calls `onDone()` synchronously, and the host's
`stampSetupAndEnter()` (MainActivity) does three things in
one call: `setupStore.markCompleted()` (the stamp),
`receptionOpen = false` (the dismissal), `navigateTo(DECK)`
(the handover). The instant `receptionOpen` flips, the
sheet's `LaunchedEffect(visible)` starts the 160 THROW —
there is no interval in which a tear could run, and no way
to book the stamp at t=0 (G-01) while deferring the throw
to t=260. §1.2 unbundles the host callback into a stamp
callback and an enter callback. That host edit is part of
this item; without it, do not attempt the tear.

**FLAG B (blocking, cured by §1.3).** The moment the stamp
books, the host's `setupStamped` flips to `true`, so the
sheet recomposes with `isFirstOpen = false` mid-tear. The
only in-sheet use of `isFirstOpen` is the stub note above
the tear (line 535) — unlatched, it would vanish at the
first recomposition of the tear, i.e. the stub would move
during the one phase where everything above the rule must
be motionless. §1.3 latches it (`tearLatchedFirstOpen`).

**FLAG C (test, cured by §1.8).** The existing test
`sheetDoneFiresTheStampAndEnterCallback`
(`ReceptionSheetTest.kt`) clicks DONE on a first-open sheet
(`isFirstOpen = true`) and asserts `done == true`
immediately after `performClick()`. Under this spec `onDone`
fires at t=260, so that assertion fails as written. §1.8
rewrites it. This is a spec consequence, not a defect.

**FLAG D (wording, resolved to source).** The brief's
"PerforationRow geometry (centred …)" — in the source,
"centred" is only vertical: `val y = size.height / 2f`
inside the 1dp row. Horizontally the dashes tile from
`x = 0f`; there is no centring offset anywhere in
`PerforationRow`. §2 copies the source verbatim. If a
horizontally-centred pattern was meant, that is a new
ruling for Pforte — it is not in the source and this spec
does not invent it.

**FLAG E (accepted edge, no machinery).** BACK during the
260 ms tear: the host's `BackHandler { receptionOpen =
false }` is live throughout (the host cannot see the
sheet-internal tear), so a BACK inside the window throws
the sheet early and truncates the tear. Both outcomes are
safe — the stamp already committed at t=0 (Flag A's order
is what makes this harmless), `parkIntro` is already
discharged, and whether the deferred `onDone()` still
fires before the sheet leaves composition, it only
re-sets `receptionOpen = false` and navigates to the Deck.
No double stamp is possible (host guard, §1.2). Recorded,
not cured: adding a tearing callback to the host is more
machinery than a 260 ms edge earns.

**FLAG F (inherited, §3).** An `AnimatedVisibility` exit
keeps its content composed — and therefore hit-testable —
for the exit's 160 ms. PARK's FIX key has exactly this
property today. §3 mirrors PARK verbatim and does not cure
it; see §3.4.

---

## §1 · THE TEAR AT DONE — Letzt F-1

### 1.1 The predicate — when a press tears

A DONE press **stamps** iff `isFirstOpen == true` at the
instant of the press. `isFirstOpen` is the host's
`!setupStamped` snapshot (MainActivity binding), i.e.
`!SetupStore.isCompleted()` as last read by the host.

- The predicate is the **state transition**, never the
  entry route. A sheet re-opened from PARK's stub row with
  the setup somehow still unstamped has `isFirstOpen ==
  true`, and its DONE press stamps and tears. A first-open
  sheet is the normal case, not the definition.
- The sheet never reads `SetupStore` itself and gains no
  new parameter for this beyond `onStamp` (§1.2).
- Only the stamping press tears. Every other DONE press —
  PARK reprints, any re-open while stamped — is §1.7,
  byte-for-byte today's behaviour.

### 1.2 Host unbundling — `MainActivity.kt` (Flag A cure)

Replace the body of `stampSetupAndEnter()` (currently the
three booking lines + the two exit lines) with two
functions, exactly:

```kotlin
    /** Book the setup stamp ONLY (G-01/G-02): DONE's press
     *  dispatches this FIRST, before the tear's sound or
     *  motion — the write, the commit, the publish, in the
     *  house order. The sheet is still standing when this
     *  returns; dismissal is stampSetupAndEnter(), called
     *  by the sheet only after the tear has run (§ FINISH
     *  item 13). Idempotent: the guard makes a second call
     *  a no-op, so the stamp can never book twice. */
    fun stampSetupOnly() {
        if (setupStamped) return
        setupStore.markCompleted()
        setupStamped = true
        if (viewModel.parkIntroPending()) {
            viewModel.markParkIntroDone()
        }
    }

    fun stampSetupAndEnter() {
        receptionOpen = false
        viewModel.navigateTo(MonolithDestination.DECK)
    }
```

`stampSetupAndEnter()` loses its three booking lines and
keeps its last two, unchanged. Verified: its only caller
in the tree is the Reception binding below, so no other
route depended on its booking side-effect. Do not retain
a "safety" `markCompleted()` inside it — `onDone` must
mean *exit only*, or §1.1's predicate stops being
auditable.

At the `ReceptionSheet(` call site, add one argument
immediately before the `onDone` line:

```kotlin
                onStamp = { stampSetupOnly() },
                onDone = { stampSetupAndEnter() }
```

### 1.3 Sheet state, signature, locks — `ReceptionSheet.kt`

**Signature.** Insert one parameter immediately before
`onDone: () -> Unit,`:

```kotlin
    onStamp: () -> Unit = {},
    onDone: () -> Unit,
```

The default exists solely so the two test call sites
compile unchanged (§1.8); the production binding in §1.2
passes it explicitly. The default is not permission for
any host to omit it.

**Imports — add:**

```kotlin
import androidx.compose.ui.graphics.graphicsLayer
import com.example.ui.theme.MonolithCurves
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
```

(`Animatable`, `tween`, `MonolithKin`, `delay` are already
imported. `PathEffect` is removed in §2.3.)

**State.** Directly after the existing
`var sheetHeightPx by remember { mutableStateOf(0) }` line,
add:

```kotlin
    // THE TEAR (Letzt F-1): two animatables, the counterfoil's
    // own pair — the torn block drops + fades on specA 180,
    // the rule's dashes halve on specB 260; the tear phase is
    // the longer of the two (260), then the host's 160 THROW
    // runs as today. Exit total 420 = DURATION_420.
    val tearDrop = remember { Animatable(0f) }
    val tearDash = remember { Animatable(1f) }
    var tearing by remember { mutableStateOf(false) }
    // Latched at the stamping press BEFORE onStamp() flips
    // the host's setupStamped: the stub note above the tear
    // is gated on isFirstOpen, and the booking would
    // un-print it mid-tear (Flag B).
    var tearLatchedFirstOpen by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
```

**Reset + rise effect.** Replace the existing
`LaunchedEffect(visible)` block (rise/leave) with:

```kotlin
    LaunchedEffect(visible) {
        if (visible) {
            // Every showing starts untorn — a PARK reprint
            // never inherits a spent tear.
            tearDrop.snapTo(0f)
            tearDash.snapTo(1f)
            tearing = false
            tearLatchedFirstOpen = false
            onShown()
            fraction.animateTo(0f, tween(MonolithKin.DRAWER_OPEN_MS, easing = MonolithKin.EasingSeat))
        } else if (fraction.value < 1f) {
            fraction.animateTo(1f, tween(MonolithKin.DRAWER_CLOSE_MS, easing = MonolithKin.EasingThrow))
        }
    }
```

Reset happens only on the `visible` entry — deliberately
**not** in the exit branch: through the 160 THROW the torn
values hold (block faded + dropped, dashes halved), so
nothing pops back mid-throw.

**Lock.** Change the `interactive` line to:

```kotlin
    val interactive = visible && settled && !tearing
```

This one change locks DONE, FIX, CHECK and OPEN THE LIST
for the tear's whole duration (all four gate on
`interactive`). A second DONE press inside the window has
no pointer input at all — there is no re-entry path, and
no second stamp, sound, or `onDone` can be produced.

**Scroll lock.** In the scrolling inner `Column`, replace
`.verticalScroll(rememberScrollState())` with:

```kotlin
                .verticalScroll(scrollState, enabled = !tearing)
```

"Everything above the rule motionless" must hold against
his hand too, not only against the animation: a flick
inside the 260 ms window must not move the stub.

**Stub-note latch.** The stub note's condition (line 535)
changes from `if (isFirstOpen) {` to:

```kotlin
            if (isFirstOpen || tearLatchedFirstOpen) {
```

Nothing else in that block changes (its 10dp spacer, its
words, its seat above the tear).

### 1.4 The DONE handler — exact replacement

In the DONE `Box` (testTag `reception_done`), replace the
`onPress` body (today: `playFirmClick(); onDone();
tryAwaitRelease()`) with:

```kotlin
                                detectTapGestures(onPress = {
                                    if (isFirstOpen) {
                                        // G-01 — book AT THE TAP, in the
                                        // house order (the counterfoil's
                                        // and the seal's own: write,
                                        // commit, publish — then sound /
                                        // motion). The stamp dispatches
                                        // FIRST, synchronously, committed
                                        // (SetupStore G-02); the tear
                                        // pulse replaces the firm click;
                                        // the motion state arms last and
                                        // the driver (§1.5) runs it. A
                                        // death anywhere after this line
                                        // leaves a stamped setup.
                                        onStamp()
                                        soundEngine?.playTearPulse()
                                        tearLatchedFirstOpen = true
                                        tearing = true
                                    } else {
                                        // A reprint's DONE does not stamp:
                                        // firm click + immediate exit,
                                        // exactly as built — no tear, no
                                        // onStamp, exit is the 160 THROW
                                        // alone.
                                        soundEngine?.playFirmClick()
                                        onDone()
                                    }
                                    tryAwaitRelease()
                                })
```

Notes that bind the implementer:

- `tryAwaitRelease()` stays last in both branches and
  nothing follows it. Setting `tearing` recomposes the
  `Box` without its `pointerInput`; that can cancel the
  await — harmless, because booking and sound already ran
  synchronously at DOWN, before the await.
- The firm click is **replaced**, not layered: the
  stamping branch contains no `playFirmClick()` call.
  `playTearPulse()` is the counterfoil's existing voice
  (`TactileSoundEngine`, no parameters) — no new sound.
- `onDone()` is not called anywhere in the stamping
  branch. It arrives only from the driver, §1.5.

### 1.5 The tear driver

Immediately after the `LaunchedEffect(visible)` block of
§1.3, add:

```kotlin
    // The tear driver: both animatables run concurrently;
    // coroutineScope returns when the LONGER (dash, 260ms)
    // lands — the drop finishes at 180 and holds its last
    // value for the remaining 80ms. Only then is the exit
    // handed to the host, whose visible flip runs the 160
    // THROW exactly as today. Fires once: tearing flips
    // true once per showing (§1.3 resets it on entry).
    LaunchedEffect(tearing) {
        if (!tearing) return@LaunchedEffect
        coroutineScope {
            launch {
                tearDrop.animateTo(
                    1f,
                    MonolithCurves.specA(MonolithCurves.DURATION_180)
                )
            }
            launch {
                tearDash.animateTo(
                    0.5f,
                    MonolithCurves.specB(MonolithCurves.DURATION_260)
                )
            }
        }
        onDone()
    }
```

Values, decided: drop target `1f` (drives §1.6's
alpha/translation), dash target `0.5f` (dash 3dp → 1.5dp,
the counterfoil's settle), `specA` = EasingA,
`specB` = EasingB. **The durations must be passed
explicitly** — `specB`'s default is 420; omitting
`DURATION_260` silently stretches the tear phase to 420
and the exit total to 580, outside the ruling.

### 1.6 The torn block — one wrapper, nothing else moves

Everything below the tear rule — and only that — is
wrapped in a single `Column` carrying the drop/fade.
Current seat (after the §2 call-site change):

```kotlin
            Spacer(modifier = Modifier.height(12.dp))
            ReceptionTearRule(dashScale = tearDash.value)
            Spacer(modifier = Modifier.height(14.dp))
            // DONE Box …
            Spacer(modifier = Modifier.height(10.dp))
            // act-line Text …
```

Becomes:

```kotlin
            Spacer(modifier = Modifier.height(12.dp))
            ReceptionTearRule(dashScale = tearDash.value)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = 1f - tearDrop.value
                        translationY = tearDrop.value * 10.dp.toPx()
                    }
            ) {
                Spacer(modifier = Modifier.height(14.dp))
                // DONE Box … (unchanged, except the §1.4 onPress body)
                Spacer(modifier = Modifier.height(10.dp))
                // act-line Text … (unchanged)
            }
```

- The 14dp spacer moves **inside** the wrapper: it is
  part of the torn leaf.
- The DONE Box and the act line move inside **verbatim** —
  heights (52dp), dress (ink), testTag, words all
  unchanged. The act line is below the rule, so it is torn
  with the key; the stub note is above the rule (§1.3) and
  is not in this wrapper.
- The formula is the counterfoil's own
  (`FocusSequencerScreen`: `alpha = 1f - tearProgress`,
  `translationY = tearProgress * 10.dp.toPx()`): at rest
  `tearDrop = 0` → alpha 1, offset 0 — a reprint renders
  pixel-identical to today.
- No `graphicsLayer`, `offset`, or alpha driven by
  `tearDrop`/`tearDash` may be applied to anything above
  the rule, to the rule's own container, or to `fraction`.
  Above the rule, the only tear-driven value anywhere is
  the rule's dash scale (§2). "Motionless" is defined as:
  no tear-driven transform or alpha above the rule. Live
  re-prints (the 5-second staleness beat, sweep landings)
  may still change words above the rule during the window
  — that is the sheet's standing liveness, not tear motion.

### 1.7 Timeline and the non-stamping press

Stamping press, from DOWN:

| t (ms) | Event |
|---|---|
| 0 | `onStamp()` — `SetupStore.markCompleted()` `commit()` returns; `setupStamped = true`; PARK intro discharged. |
| 0 | `playTearPulse()` (its own ~40 ms voice runs concurrently; it gates nothing). |
| 0 | `tearing = true`; all keys + scroll lock (§1.3). |
| 0–180 | Torn block: `translationY` 0 → 10dp, alpha 1 → 0, `specA(180)`. Holds at final value 180–260. |
| 0–260 | Rule dashes: scale 1 → 0.5 (dash 3dp → 1.5dp, gap constant 3dp), `specB(260)`. Above the rule: still. |
| 260 | Driver returns; `onDone()` → `receptionOpen = false`. |
| 260–420 | Whole sheet throws: `fraction` 0 → 1, `tween(160, EasingThrow)` — the existing exit, unmodified. Torn values hold through it. |
| 420 | `fraction >= 1f`; the sheet's early return uncomposes it. |

**Total 420 ms = `MonolithCurves.DURATION_420`** — inside
the house vocabulary {180, 260, 280, 420, 520}; every
component (180 specA, 260 specB, 160 = `DRAWER_CLOSE_MS`)
is also a vocabulary value. No new duration, curve, or
sound is introduced anywhere in this item.

Non-stamping DONE press (PARK reprint / already stamped),
complete behaviour: `playFirmClick()` then `onDone()`,
same lambda, same order as today; `onStamp()` is never
called and `SetupStore` is untouched; `tearing` never
flips, `tearDrop` stays 0f, `tearDash` stays 1f, the
driver never runs; exit is the 160 THROW alone — **total
160 ms, exactly today's exit.** BACK exits likewise
(stamps nothing, tears nothing) and are unchanged.

### 1.8 Test call sites — the only test edits in this item

- `ScreenshotHarnessTest.kt` `setReception(...)`: **do not
  edit.** The `onStamp` default (§1.3) covers it; it never
  clicks DONE.
- `ReceptionSheetTest.kt`:
  1. `setSheet(...)` gains two parameters with defaults —
     `isFirstOpen: Boolean = true` and
     `onStamp: () -> Unit = {}` — passed through to the
     `ReceptionSheet(...)` call (replacing the hard-coded
     `isFirstOpen = true`). All existing callers compile
     unchanged.
  2. Rewrite `sheetDoneFiresTheStampAndEnterCallback`
     (Flag C) to assert the G-01 order and the deferral:

     ```kotlin
         var stamped = false
         var done = false
         setSheet(onStamp = { stamped = true }, onDone = { done = true })
         composeRule.onNodeWithTag("reception_done")
             .performScrollTo()
             .performClick()
         // G-01: the stamp books at the press; the enter
         // waits for the tear (260ms).
         assertTrue(stamped)
         assertFalse(done)
         composeRule.mainClock.advanceBy(300)
         assertTrue("DONE hands the exit to the host after the tear", done)
     ```
  3. Add the reprint case:

     ```kotlin
     @Test
     fun sheetDoneOnAReprintDoesNotStampAndExitsAtOnce() {
         var stamped = false
         var done = false
         setSheet(
             isFirstOpen = false,
             onStamp = { stamped = true },
             onDone = { done = true }
         )
         composeRule.onNodeWithTag("reception_done")
             .performScrollTo()
             .performClick()
         assertFalse(stamped)
         assertTrue(done)
     }
     ```

### 1.9 Comment upkeep (same edit, no other text changes)

- The file header's motion line ("Motion is KIN-DRAWER's:
  rise 280 SEAT, leave 160 THROW.") becomes: rise 280
  SEAT; a stamping DONE leaves 420 (260 tear + 160 THROW);
  every other leave 160 THROW.
- The DONE block's comment gains one sentence: the press
  books first (onStamp), tears, and only then hands the
  host the exit (onDone) — reprints skip the tear. Wording
  is the lead's, the two facts are not optional.

---

## §2 · THE TEAR RULE ON PERFORATION GEOMETRY — Letzt F-2

### 2.1 What changes and what stands

Changes: dash/gap construction only. Stands, untouched:
the colour line — `onBackground.copy(alpha = 0.35f)` (the
0.35 alpha is acquitted; do **not** import PerforationRow's
colour logic — `outlineVariant` at 0.30, or `background`
when raised — the rule borrows geometry, not tone), the
1dp height, the `fillMaxWidth`, the seat, and the 12dp
spacer above it.

The defect this cures, for the record: as built, the rule
is one `drawLine` with
`PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)` —
raw **pixels**, so the pattern shrinks on denser screens,
and it can never halve. The new geometry is in dp and
takes the settle parameter, exactly as `PerforationRow`
builds it.

### 2.2 Exact replacement — `ReceptionTearRule`

Replace the whole composable (currently at the file's
foot) with:

```kotlin
/** The tear rule: where the sheet separates when DONE is
 *  pressed and the stub stays in PARK. Built on
 *  PerforationRow's geometry (FocusSequencerScreen): dash
 *  3dp / gap 3dp, in dp, the line centred in its 1dp
 *  height, tiled from the left edge. [dashScale] is the
 *  tear's settle — during a stamping DONE the dashes halve
 *  (3dp → 1.5dp) over specB 260 while the gap holds; at
 *  rest it is 1f and the rule prints as it always has.
 *  Ink at 35% stands (acquitted) — geometry is borrowed,
 *  tone is not. */
@Composable
private fun ReceptionTearRule(dashScale: Float = 1f) {
    val ink = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f)
    Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
        val y = size.height / 2f
        val dash = 3.dp.toPx() * dashScale
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

This is `PerforationRow`'s loop verbatim with three
substitutions, all decided: `color` = the rule's own ink
(above); the `sealed` overprint block is **omitted** (the
sheet is never sealed while it stands — the host gates
`visible` on `!sealed` — so a sealed branch would be dead
print); no `raised` parameter (that is the counterfoil
leaf's tone case, not this rule's).

Consequences to expect, not to "fix": as `dashScale`
halves, the pitch goes 6dp → 4.5dp (gap constant), so the
dash count across the width grows during the settle —
that *is* PerforationRow's settle behaviour. The final
dash is clamped to the right edge by `minOf`, as in the
source.

### 2.3 Call site and import

- The single call site becomes
  `ReceptionTearRule(dashScale = tearDash.value)`
  (§1.6 shows its seat). The parameter default keeps the
  signature honest for any other caller; verified, there
  is one call site.
- Delete `import androidx.compose.ui.graphics.PathEffect`
  — its only use in the file was the old rule (verified:
  import + one use). No other import changes for this
  section (`Canvas`, `Offset`, `dp` are already in).

---

## §3 · THE KEY VISIBILITY PAIR — Letzt F-3

### 3.1 The pair, copied from PARK verbatim

PARK's FIX block (`ParkDrawer.kt` lines 369–375) is the
only template. Both sheet key blocks take:

```kotlin
                    enter = expandVertically(tween(MonolithKin.DRAWER_CLOSE_MS, easing = MonolithKin.EasingSeat)) +
                        fadeIn(tween(MonolithKin.DRAWER_CLOSE_MS)),
                    exit = shrinkVertically(tween(MonolithKin.DRAWER_CLOSE_MS, easing = MonolithKin.EasingThrow)) +
                        fadeOut(tween(MonolithKin.DRAWER_CLOSE_MS))
```

i.e. **160 / 160, Seat in / Throw out**, durations via the
constant `MonolithKin.DRAWER_CLOSE_MS` (= 160), no literals,
no alignment arguments (PARK passes none — its defaults
are this pair's defaults), no delay. This is the same
160 that closes the sheet; the vocabulary gains nothing.

Imports — add:

```kotlin
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
```

### 3.2 Exact restructure — `ReceptionDoorCard`'s `when`

Replace the entire `when (print) { … }` block (MISSING
branch, STALE/UNMEASURED branch, `else -> Unit`) with two
`AnimatedVisibility` blocks. The two `Box`es move inside
**byte-identical** — dress, dip, sounds, testTags, heights
all as built; this item is animation only and does not
touch F-4's ink question or the CHECK voice question:

```kotlin
        // Letzt F-3: PARK's visibility pair on both key blocks —
        // a key never appears or vanishes in one frame; a
        // repaired door's key throws out over 160 while the
        // card settles. The 10dp spacer rides inside, so the
        // animated envelope is the whole 58dp the key band
        // occupies standing (10 + 48); a keyless card carries
        // no reserved gap from this pair.
        AnimatedVisibility(
            visible = print == PermPrint.MISSING,
            enter = expandVertically(tween(MonolithKin.DRAWER_CLOSE_MS, easing = MonolithKin.EasingSeat)) +
                fadeIn(tween(MonolithKin.DRAWER_CLOSE_MS)),
            exit = shrinkVertically(tween(MonolithKin.DRAWER_CLOSE_MS, easing = MonolithKin.EasingThrow)) +
                fadeOut(tween(MonolithKin.DRAWER_CLOSE_MS))
        ) {
            Column {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    // … the FIX Box, byte-identical to today:
                    // 48dp, fixPressed 1dp dip, inkControlFill(),
                    // pointerInput(item) seat-knock + onFix,
                    // testTag "reception_fix_${item.name}",
                    // text "FIX — ${item.boardName}" …
                ) { /* … unchanged … */ }
            }
        }
        AnimatedVisibility(
            visible = print == PermPrint.STALE || print == PermPrint.UNMEASURED,
            enter = expandVertically(tween(MonolithKin.DRAWER_CLOSE_MS, easing = MonolithKin.EasingSeat)) +
                fadeIn(tween(MonolithKin.DRAWER_CLOSE_MS)),
            exit = shrinkVertically(tween(MonolithKin.DRAWER_CLOSE_MS, easing = MonolithKin.EasingThrow)) +
                fadeOut(tween(MonolithKin.DRAWER_CLOSE_MS))
        ) {
            Column {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    // … the CHECK Box, byte-identical to today:
                    // 48dp, surfaceVariant, pointerInput(item)
                    // onCheck, testTag
                    // "reception_check_${item.name}", text
                    // "CHECK — ${item.boardName}" …
                ) { /* … unchanged … */ }
            }
        }
```

(The lead pastes the real Box bodies from the current
branches into the marked slots, character for character —
the `// …` lines above are placement markers, not code.)

Structural rules that are part of the spec:

- The `Spacer(10.dp)` that today precedes each Box moves
  **inside** its visibility block. Left outside, a keyless
  card would keep a permanent orphan 10dp gap.
- The trailing `Spacer(10.dp)` + `ReceptionHairline()`
  after the old `when` stay outside both blocks,
  unchanged — as a key shrinks out, the hairline rises to
  meet the card; that rise is the settle.
- The `else -> Unit` branch is deleted. GRANTED is now
  expressed as both blocks invisible (zero height), which
  is the same print with an exit path.
- Both blocks are always in the composition. That is what
  makes the exit animatable at all: the old `when` removed
  the key from the tree in the same recomposition that
  reported the repair — the one-frame pop this item kills.

### 3.3 Choreography by transition (all decided)

| Transition | Result |
|---|---|
| First composition, key standing | No enter animation — `AnimatedVisibility` starts entered when first composed visible. Mirrors PARK. The standing sheet opens with its keys already printed. |
| UNMEASURED → MISSING (first sweep lands a lack) | CHECK exits 160 Throw while FIX enters 160 Seat, concurrently, in the same column: combined envelope ≈ constant 58dp, a cross-settle, no sequencing, no delay. |
| MISSING → STALE (proof ages / re-read) | Same swap, reversed roles. |
| MISSING → GRANTED (the repair return — the case F-3 names) | FIX block shrinks + fades over 160 Throw. Nothing enters. The job/escort prints above and the hairline below do not move except the hairline's rise as the 58dp leaves. |
| STALE / UNMEASURED → GRANTED | CHECK block, same 160 Throw exit. |
| Any → GRANTED via the return verdict | Independent clocks, no coupling: the verdict's 800 ms beat and its firm click (existing effect in this file) are untouched; the key's exit starts at the recomposition that first prints GRANTED, whenever the sweep lands it — not at the verdict. The FIX dip (`fixPressed`, cleared by the verdict effect) rides inside the exiting Box and dies with it; no interaction. |

### 3.4 Flag F, stated for the record

During its 160 ms exit a key remains composed and its
`pointerInput` live: a tap on the shrinking key can still
fire `onFix`/`onCheck`. This is PARK's behaviour with the
same construction, inherited deliberately — this item's
law is *mirror PARK*, and gating the exiting content
(e.g. on the visibility state) would fork the pair. If
Pforte wants the gate, it is a separate ruling that must
land on PARK and the sheet together.

---

## §4 · WHAT THIS ITEM DOES NOT TOUCH

- No other key's dress, sound, size, or words (F-4 ink,
  CHECK's knock, and the 40 → 24dp bottom padding are the
  §3 F-4/F-5 micro-pass — a different item; the scrolling
  column's `bottom = 40.dp` stays 40 here).
- The rise (280 SEAT), the throw mechanics, the scrim —
  there is none on this sheet — and the host's
  `visible = receptionOpen && !sealed` gate.
- The door cards' prints, escorts, glosses, collapse
  behaviour, and the total — except the stub-note latch
  condition (§1.3), which changes no print, only its
  survival through the tear.
- `SetupStore` itself: `commit()` semantics (G-02) are
  relied on, not modified.
- No new durations, curves, sounds, colours, or sizes
  anywhere. Tear total 420 is composed of 260 + 160, both
  pre-existing constants.

**Acceptance, on paper (Ren compiles when Sarthak says):**
a first-open DONE press books the stamp before any sound,
tears for 260 (block −10dp/fade 180 specA, dashes halved
260 specB, stub still), throws 160 — 420 out; a PARK
reprint's DONE clicks firm and is gone in 160, unstamped
and untorn; the rule at rest is PerforationRow's pattern
in the sheet's own 0.35 ink; and no FIX or CHECK key
anywhere in the sheet ever again appears or leaves in a
single frame.

— Letzt (finisher), issue #14 cell
