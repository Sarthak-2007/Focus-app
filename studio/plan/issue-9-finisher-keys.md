# ISSUE #9 — FINISHER 2 (KEYS AS OBJECTS): THE FIX KEY + THE CHECK KEY

*Finisher 2 (keys), issue-9 cell (for Hof). Ground: `AUDIT-park.md`
(Hof's rulings stand), `issue-9-designer.md` (the Designer's CHECK
spec — built on, and departed from only where named below), and the
cell tree's built `ParkDrawer.kt` (FIX key ~L445–500, CHECK key
~L505–550, CHECKING derivation ~L200–230). Paper only — nobody
compiles. Every item is tagged **SPEC-ONLY** (law, no code change)
or **PATCH-READY** (exact code, anchored to the built file).*

*The one idea of this finish: the two keys are ONE physical object
wearing two verbs. Same body, same dip, same voice on the way
down. They differ only in where the press goes and what ends it:
FIX leaves the room to repair a named lack; CHECK stays in the
room to prove the whole board. If a reader can tell which key he
is touching before he reads the word, the finish has failed.*

---

## §1 · THE SHARED BODY — geometry (both keys, no exceptions)

**SPEC-ONLY** (already true in the built file; protect all of it):

| Property | Value |
|---|---|
| Width | full content width (sheet padding 20dp each side is the only inset) |
| Height | **48dp**, exactly — the touch target is the whole 48dp, no extra padding |
| Radius | **8dp** `RoundedCornerShape(8.dp)` — the key radius; the panel's 16dp never touches a key |
| Fill (ink dress) | `inkControlFill()` — `onBackground` by day, `#4A423C` by night |
| Word colour (ink dress) | `onInkControlFill()` — `#FBF1EC` day / `#FFE4CF` night |
| Elevation / shadow / ripple | none, ever — `pointerInput` + `detectTapGestures`, no `clickable` indication |
| testTags | `park_fix_key` / `park_check_key` — do not rename |

Both dresses in §4 are token-driven in day and night. Neither key
branches on `isNight` for its dress — `isNight` exists in this
drawer only inside the knock's amplitude (§2), exactly as built.

## §2 · THE DIP — one press language, two verbs

**The shared mechanics — SPEC-ONLY:** a press is a **1dp downward
offset and nothing else**. Fill does not darken, radius does not
change, the word does not move within the key, no scale, no shadow
appears. The dip is the whole press sign, plus the voice:

```
soundEngine?.playSeatKnock(durationMs = 10,
    amplitude01 = if (isNight) 0.06f else 0.1f, pitchHz = 300f)
```

— fired on DOWN, identical call in both keys, already built in
both places. Do not retune one without the other.

**The dip's motion — PATCH-READY** (today both offsets snap;
a gallery key travels): one animator per key, same spec for both,
declared in `ParkDrawer`'s body beside the existing press states.
Import `androidx.compose.animation.core.animateDpAsState`.

```kotlin
val fixDip by animateDpAsState(
    targetValue = if (fixPressed) 1.dp else 0.dp,
    animationSpec = tween(
        durationMillis = if (fixPressed) MonolithKin.PRESS_MS
                         else MonolithKin.DRAWER_CLOSE_MS,
        easing = if (fixPressed) MonolithKin.EasingSeat
                 else MonolithKin.EasingThrow
    ),
    label = "park_fix_dip"
)
val checkDip by animateDpAsState(
    targetValue = if (checking) 1.dp else 0.dp,
    animationSpec = tween(
        durationMillis = if (checking) MonolithKin.PRESS_MS
                         else MonolithKin.DRAWER_CLOSE_MS,
        easing = if (checking) MonolithKin.EasingSeat
                   else MonolithKin.EasingThrow
    ),
    label = "park_check_dip"
)
```

Then in the FIX key's Box, `.offset(y = if (fixPressed) 1.dp else 0.dp)`
becomes `.offset(y = fixDip)`; in the CHECK key's Box,
`.offset(y = if (checking) 1.dp else 0.dp)` becomes
`.offset(y = checkDip)`. Press-in: 90ms SEAT (KIN PRESS). Release:
160ms THROW (PRESS-BACK) — the release the Designer ruled in
§1.6, now true of both keys, not just CHECK.

**Where the verbs part — SPEC-ONLY:**

| | FIX (repair) | CHECK (prove) |
|---|---|---|
| Press commits | him OUT — to the system's door for the named lack | nothing leaves; the sweep fires in the store |
| Dip held for | the whole absence, however long Settings takes | the sweep's landings (~300ms class) |
| Dip released by | the return read: host tick + 800ms stability beat, store read LIVE at that instant (acquitted, audit §7) | the stamp derivation of §5 — never a beat, never a timer |
| Firm click sounds iff | that one named item reads GRANTED at the return read | the counted four all read GRANTED at completion |
| Silence otherwise | yes — no word prints from the key (the printed FIXED / STILL OPEN verdict is §4.6's staircase lane, NOT this finish; the key's face must never pre-print it) | yes — a completed set with lacks gets no click; the board's re-print and FIX's appearance are the verdict |

## §3 · THE FIX KEY — finish items

1. **Entry tempo — PATCH-READY.** The audit (§1.8) ruled 280 SEAT
   for the key's entry; the built `AnimatedVisibility` enters on
   `DRAWER_CLOSE_MS` (160) with a SEAT easing — a dismissal's
   clock on an arrival. In the FIX key's `AnimatedVisibility`:

   ```kotlin
   enter = expandVertically(
               tween(MonolithKin.DRAWER_OPEN_MS, easing = MonolithKin.EasingSeat)) +
           fadeIn(tween(MonolithKin.DRAWER_OPEN_MS)),
   ```

   Exit stays exactly as built: `DRAWER_CLOSE_MS` + `EasingThrow`
   on both halves. A key that arrives at drawer tempo and leaves
   at throw tempo — arrivals land, departures are thrown.

2. **Face — SPEC-ONLY.** `FIX — ${item.boardName}`, first MISSING
   in board order, one lack at a time (acquitted). The em dash is
   part of the face; the board name is the board's own token,
   never re-cased or shortened on the key.

3. **Presence — SPEC-ONLY.** Conditional and only conditional:
   `firstMissing != null && !isSealed`. A FIX key with nothing to
   fix is a lie of affordance; it absents, it does not disable.

## §4 · THE CHECK KEY — finish items

1. **The dress ladder — PATCH-READY** (Designer §1.4, unbuilt:
   the shipped key wears ink permanently, so at rest on a fresh
   board it shouts at the same volume as when it is the answer).
   Derive beside the existing `checking` derivation:

   ```kotlin
   // Ink exactly when pressing CHECK is the answer: a row STALE
   // or UNMEASURED, or a sweep in flight. MISSING does not
   // promote it — a lack's answer is the FIX key above it.
   val checkNeeded = if (snapshot == null) true else
       WatchItemId.PERMISSIONS.any { item ->
           val p = WatchStore.permPrintAt(snapshot, item, nowMono)
           p == PermPrint.STALE || p == PermPrint.UNMEASURED
       }
   val checkInk = checking || checkNeeded
   ```

   In the CHECK key's Box, replace `.background(inkControlFill())`
   with (import `androidx.compose.foundation.border`):

   ```kotlin
   .then(
       if (checkInk) Modifier.background(inkControlFill())
       else Modifier.border(1.dp, scheme.outline, RoundedCornerShape(8.dp))
   )
   ```

   and the face Text's colour becomes
   `color = if (checkInk) onInkControlFill() else scheme.onBackground`.
   Print dress = transparent ground (the sheet shows through),
   1dp `outline` — the Designer's strong-hairline token — word in
   primary ink. Dress swaps are instant at the state boundary;
   fills do not crossfade in this app and the keys do not start.

2. **States — SPEC-ONLY** (as built + §4.1's dresses):
   `CHECK` at rest (print dress fresh / ink dress needed);
   `CHECKING` in ink, dipped, taking no second press
   (`interactive && !checking` gate, as built). The word swap is
   instant, same frame as the dip — no spinner, no ellipsis, no
   percentage, no "3 OF 6" (telemetry never prints on a face).
   Rows land one by one behind the dipped key; a row re-prints
   atomically on its own landing and nothing else moves.

3. **Presence — SPEC-ONLY.** Always present while PARK is open
   and unsealed (§7 for the seal) — present even when fresh, in
   print dress. A verb that vanishes when the board looks fresh
   teaches him it isn't his to call (Designer §1.2, adopted).

## §5 · CHECKING'S HONESTY — the protected derivation

**SPEC-ONLY — this section is a fence, not a feature.** The built
derivation is the most honest code in the drawer:

```kotlin
var checkPressedMono by remember { mutableStateOf(0L) }
val checking = checkPressedMono > 0L && !WatchItemId.PERMISSIONS.all { item ->
    (snapshot?.items?.get(item)?.measuredMono ?: 0L) >= checkPressedMono
}
```

The law it embodies, itemised so no future patch crosses it
by accident:

1. **CHECKING is a derivation from store stamps, never a timer.**
   It begins at the press instant and ends at the exact emission
   where the sixth permission stamp reaches that instant. No
   `delay()`, `withTimeout`, frame counter, or elapsed check may
   set, clear, extend, or expire `checking` or `checkPressedMono`.
   The only two lawful exits are (a) the stamps landing, and
   (b) the drawer closing (the built `LaunchedEffect(visible)`
   reset) — and (b) abandons the *key's* claim, not the sweep's:
   the sweep completes in the store regardless, and the next
   opening prints its landings.
2. **The Designer's 2,500ms FAILED window (§1.5 of her spec) is
   NOT adopted.** A clock declaring `NOT PROVED` would be a
   timer wearing a verdict — the same dishonesty as a timed
   CHECKING, one seat over. This drawer has no store-stamped
   failure to print: `runSweep` stamps every query it runs
   (a throw stamps MISSING), so the only incompletion is a
   query that never returns — and the honest face for that is
   the key staying dipped at `CHECKING` for the drawer's life,
   not a word a clock invented. If a sweep-abandoned stamp ever
   lands in the store, the FAILED line returns as a derivation
   off that stamp — first unlanded name in board order, Atkinson
   Hyperlegible Bold 15sp, 8dp under the key, per Designer §1.5 —
   and not one build before.
3. **Parity with LAST PROVED.** The same six landings that
   release this key advance the store's LAST PROVED instant.
   Key release and colophon advance are one event seen from two
   seats; any patch that changes one predicate must change the
   other in the same commit, or neither.
4. **Partial truth stands.** Landed items keep their stamps and
   re-print; unlanded items keep their old stamps and their old
   words. CHECKING never blanks, dims, or re-dresses a row.
5. **The FIX key's 800ms return beat is not a precedent.** It is
   a stability beat before a *verdict read*, acquitted for FIX
   alone (§2's table). It does not cross to CHECK, whose verdict
   is the stamps themselves.

## §6 · LABEL TYPOGRAPHY — both faces

**SPEC-ONLY + one PATCH-READY lock:**

- Both faces: `PrimaryButtonTextStyle` and nothing else — Sora
  Bold 15sp, lineHeight 20sp, +0.5 tracking. The type ladder has
  no second button size and the keys invent none.
- FIX's longest possible face, `FIX — NOTIFICATION LISTENER`,
  fits the full-width key at this size with room to spare. If a
  future board name ever overflows, the answer is never a
  smaller size, never tracking compression, never ellipsis —
  the name is wrong, not the type.
- **PATCH-READY:** add `maxLines = 1` to both keys' face `Text`
  calls, so no future string can wrap a key into two lines and
  quietly change its height.
- **PATCH-READY (semantics):** the visible faces stay one word /
  one name; the reader gets plain words. Import
  `androidx.compose.ui.semantics.contentDescription` and
  `androidx.compose.ui.semantics.semantics`; on the CHECK Box add
  `.semantics { contentDescription = "Check guard state" }`
  (Designer's words), on the FIX Box
  `.semantics { contentDescription = "Fix ${item.boardName}" }`.

## §7 · SPACING — keys among themselves and to the board

**PATCH-READY** (built file uses 14 / 14 / 20 — none of them in
the audit's module {8, 16, 24}, §1.13). The finished rhythm:

```
board's last hairline
  16dp
[FIX key 48dp]          (only when a lack exists)
   8dp                  (only between the two keys)
[CHECK key 48dp]
  16dp
foot block / next section
```

Stack totals: both keys present **136dp**; CHECK alone **80dp**;
sealed **0dp** (§8). Exact edits:

1. FIX key: the `Spacer(Modifier.height(14.dp))` inside its
   `AnimatedVisibility` Column → **16.dp**. (It lives inside the
   visibility block, so a vanishing FIX takes its gap with it.)
2. CHECK key: its `Spacer(Modifier.height(14.dp))` →
   `Spacer(Modifier.height(if (firstMissing != null) 8.dp else 16.dp))`.
   When FIX's exit collapse and this spacer's 8→16 settle run
   together, the pair reads as one settle — that is correct,
   do not sequence them.
3. The trailing `Spacer(Modifier.height(20.dp))` after the CHECK
   block → **16.dp**. It sits outside the seal gate, so under
   the seal the board keeps a lawful 16dp joint to what follows.

No other spacer in the key zone changes. The board's internal
16dp family break (interceptors) is the board's own finish, not
this one.

## §8 · SEALED ABSENCE

**SPEC-ONLY** (as built; one named departure). Under the seal,
**both keys are absent — object, dip, voice, and spacers, all of
it.** FIX is gated by its `AnimatedVisibility` (`!isSealed` in
its visible condition), CHECK by its `if (!isSealed)` block with
its spacer inside the gate, so no orphan gap survives either key.
If the seal lands while PARK stands open, FIX leaves by its
normal exit (shrink + fade, 160 THROW, §3.1) and CHECK leaves
with the recomposition; the sheet's panel geometry — radius,
rest heights, grab strip — never moves for either.

The sealed drawer is print: it may be read, never operated. A
key-shaped print that swallows touch would be an object lying
about being one, at the exact hour the drawer's whole job is
not lying. *Departure, stated plainly for Hof: Designer §1.7's
print-dress persistence for CHECK under the seal is NOT adopted
— Hof's seated law (both absent) stands over it.*

## §9 · PATCH INDEX + ACCEPTANCE

PATCH-READY, in application order: §2 dip animators (both keys)
· §3.1 FIX entry tempo · §4.1 CHECK dress ladder · §6 maxLines +
semantics · §7 three spacer values. Everything else in this
document is SPEC-ONLY law around code that already honours it.
No DB change, no new permission, no new colour, no new curve,
no new size. Zero orange, zero green, on both keys in both themes.

Acceptance, by eye:

1. Fresh board, PARK open: CHECK sits in print dress under the
   board; no FIX. Let it age past 45s: a row goes STALE and
   CHECK promotes itself to ink, unaided, no press.
2. Press CHECK: knock on DOWN, dip travels in at 90 SEAT, word
   reads CHECKING; rows land one by one; on the sixth stamp the
   dip throws back over 160, word re-prints CHECK, LAST PROVED
   takes the new time in the same instant. Whole set → one firm
   click. Set with a lack → silence, FIX seated 8dp above CHECK.
3. Press FIX: dip + knock, he leaves; return with the lack
   repaired → one firm click as the dip releases; return still
   broken → silence, dip still releases, key names the next lack
   or absents at 160 THROW after having arrived at 280 SEAT.
4. Kill a query so it never returns: CHECK stays dipped at
   CHECKING — no word appears at 2,500ms or any other mark.
   Close and reopen: the landings are printed, the key at rest.
5. Seal a session with PARK open: both keys and both gaps gone;
   the board reads as pure print; unseal and the keys return at
   their §7 spacing, CHECK dressed for the board's actual state.

— Finisher 2 (keys), issue-9 cell (for Hof)
