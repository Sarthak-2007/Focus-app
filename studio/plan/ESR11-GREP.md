# THE ESR-11-CLASS TREE-WIDE GREP — bookings behind delays

**Reviewer:** Hale, Critics lead — seated directly by Ren (flat
studio), 2026-10-29. This is the tree-wide grep Ren approved in
mail/hale/1230 ("approved, it is ESR-11's class closure and I want
its result before the slide merge"). The slide merge and Slice E
have since landed. The result is not the one the schedule wanted.

**The class, as charged in ESR-11:** a booking or state-write
that happens only after a delay, or inside a cancellable effect,
such that process death — or mere cancellation — inside the
window loses the write, most sharply where a sound or an
animation has already told the user the act happened.

**The disposition test (Ren, mail/hale/1230, on ESR-11):**
"booking precedes sound, or the booking is made death-proof —
the drawer fix answers both halves or neither."

**Result: the class is NOT closed. Its type specimen is still
live in trunk, both halves unanswered — plus one narrow second
site and one adjacent site. Everything else in the tree was
hunted pattern by pattern and is acquitted below, by name.**

Ground: `~/workspace/clone-inspect/Focus-app/app/src/main`
(whole tree, all of ui/, data/, service/, views/, components/,
screens/).

---

## UNSAFE

### G-01 — ESR-11 itself, live: the tear books 260ms after it sounds

`ui/screens/FocusSequencerScreen.kt:450-458`:

```kotlin
if (tearing) {
    LaunchedEffect(task.id) {
        delay(MonolithCurves.DURATION_260.toLong())
        onCompleteTask(task.id)
    }
}
```

and the tear itself, ibid. :460-468: `tearing = true`, then
`soundEngine?.playTearPulse()` — at the tap, 260ms before the
booking exists. The booking is
`onCompleteTask → MonolithViewModel.completeTaskAndAdvance →
MonolithRepository.completeTaskAndAdvance` — the DB write that
moves the task to COMPLETED and into the Drawer.

Two windows, both fatal to the record:

1. **Process death inside the 260ms** — the original ESR-11,
   verbatim: the task resurrects un-booked on relaunch. The user
   heard the tear; the store never did.
2. **Mere cancellation, no death required** — the effect is a
   `LaunchedEffect` inside the leaf's composition and `tearing`
   is `remember(task.id)` state. Navigate away, or scroll the
   leaf out of composition, inside the window and the effect is
   cancelled: the booking never fires at all, while the sound
   already played and the leaf was seen to tear. On return the
   task stands in the queue as if untouched.

What the drawer merge DID fix, for the record: the double-booking
half of the family. completeTaskAndAdvance is now a latch, not a
toggle (MonolithRepository.kt:2231-2242), so a second fire books
nothing twice. But the window itself was not closed — it was
RETIMED: the inline comment at :452-454 records the delay being
set to the dash settle's 260ms (from the fade's 180ms) on
animation grounds ("Critics' MO-2; Finishers' P2-A2"). The death
window got 80ms WIDER in the fix that was supposed to answer it.
Against Ren's disposition test: booking does not precede sound
(the pulse is 260ms early), and the booking is not death-proof
(a plain cancellable effect). Neither half is answered.

Compare the house standard, one package over:
`service/SessionSealTransition.kt:117-134` — executeSeal runs
the row write FIRST, then commits the seal marker synchronously
(`.commit()`, :104-111), then publishes, and only then sounds
the Throw. That ordering exists in this codebase. The tear does
not use it.

### G-02 — UNSAFE (narrow): the day word's durability is an async flush

`data/DayWordStore.kt:31-40`: `put()` writes with
`editor.apply()`. apply() is immediate in memory and asynchronous
to disk. The read path (Repository.allDayWords,
MonolithRepository.kt:2521-2524) reads the same in-memory prefs,
so the Drawer shows the word as declared the instant he taps —
but a hard process death before the background flush lands loses
it silently, and after relaunch the word is simply absent, with
no trace it was ever declared. The window is short (the flush is
usually milliseconds) but unbounded in principle, and the
victim is the Drawer's one authored record — his declaration
for a sick day, exactly the class of fact this app exists not to
lose. Filed as DRW cross-reference in plan/DRAWER-REVIEW.md.
The fix pattern already exists in-tree: the seal marker commits
synchronously for precisely this reason
(SessionSealTransition.kt:104-111).
Same pattern, settings-grade only: MonolithRepository.kt:442
and :457 (park-intro flag, guard prefs) — no booking rides on
them; noted, not charged.

### G-03 — CLASS-ADJACENT: the undo window's restore copy is memory-only

`ui/MonolithViewModel.kt:1565-1583`: deleteTaskWithUndo books
the delete immediately (correct), then holds the deleted row
solely in `_deletedTaskForUndo` (a StateFlow) behind a 5-second
dismiss timer. Process death inside the window destroys the only
copy of the row: a delete the UI presented as reversible for five
seconds becomes permanent. Not a lost booking — a lost reversal.
Bounded exposure: the repository refuses to delete COMPLETED
rows (MonolithRepository.kt:2439-2445), so stored Drawer records
cannot be eaten this way; queued and active tasks can.

---

## SAFE — every other site, by name

The finish machine (the largest delay cluster in the tree) is
acquitted in detail because it LOOKS like the class and is not:
the finish's booking is the outcome stamp on the session row,
written at the ending write BEFORE E0 ever fires. What the
detention delays (MonolithViewModel.kt:1078-1165 — E700/E880
sound scheduling, the 25ms pawl poll, issue at E1480-E1800, the
1050ms sled expiry) is only the receipt's NUMBER, and that claim
is a single `withTransaction` on the guard_settings singleton
(MonolithRepository.kt:1380, :1565). Death mid-detention loses
nothing booked: at relaunch the finish collector re-derives the
finish from the row, and within the 10-minute discovery window
claims and issues immediately with no detention (branch U,
MonolithViewModel.kt:1042-1049). Past that window the receipt is
deliberately never minted — a designed boundary: no № is claimed,
so no gap opens in the sequence, and the finish itself stands
booked in the row and in the Drawer.

| Site | Pattern | Why it is safe |
|---|---|---|
| data/MonolithRepository.kt:1920 | delay(60_000) | Week-anchor tick flow; emits reads only. The spend write it neighbours is a separate single transaction (ibid. :1945+), never behind this delay. |
| ui/MonolithViewModel.kt:236 | delay(3600) | Ratchet banner auto-clear; in-memory print only; a newer warning cancels the pending clear by design. |
| ui/MonolithViewModel.kt:590 | delay(90) | Jam haptic strikes; sound only, no write follows. |
| ui/MonolithViewModel.kt:847 | delay(1000) | The ticker loop. Session truth is anchored in the row's wall/mono stamps; per-tick persistence is continuous, death loses ≤1s of display, recovery re-derives (computeVerifiedSessionElapsed). |
| ui/MonolithViewModel.kt:896 | delay(remaining) | Shot-ground step at t0+400; visual state only. |
| ui/MonolithViewModel.kt:1576 | delay(5000) | G-03's dismiss timer — see above; the delete itself precedes it. |
| service/MonolithShieldService.kt:190 | delay(2000) | Foreground poll interval; intercepts fire on detection, the delay books nothing. |
| service/SessionSealTransition.kt:251 | delay(SAMPLE_MS=25) | Witness polling INSIDE the proving procedure; the seal's writes are ordered row → marker commit() → publish → sound (ibid. :117-134), and death mid-proving is F-K1 recovery's named case (young window 600_000, ibid. :276-278). The anti-ESR-11 pattern. |
| ui/ArmProvingController.kt:535 | delay(sampleMs) | Same witness poll, controller side; reads only. |
| ui/ArmProvingController.kt:633, 677 | delay(SENTENCE_HOLD_MS) | Clears the printed jam/annul sentence only. Annulment's writes (port.annulProvingSession, stopShield) run synchronously in annul() BEFORE the hold (ibid. :645-690). |
| ui/components/AirlockAndDisarmOverlays.kt:500 | delay(1000) | Airlock 5-second countdown print; every act in that overlay is a tap, not a timer expiry. |
| ui/components/MonolithCustomComponents.kt:669, 672 | delay(500), delay(120) | Refusal-sentence fade-out and the hand-back tick; print only, nothing persists. |
| ui/components/MonolithCustomComponents.kt:734 | LaunchedEffect(strikeKey) | The CORRECT pattern, filed as evidence the standard exists in UI too: onStruck(key) fires BEFORE the settle animations run; the record itself is held at VM scope (E-12). |
| ui/components/MonolithCustomComponents.kt:770 | LaunchedEffect(isSealed) | The imprint lip's fade; visual only. |
| ui/components/ParkDrawer.kt:156 | delay(5_000) loop | Staleness re-render beat; every PARK print re-derives live from the Watch Store. |
| ui/components/ParkDrawer.kt:182 | delay(800) | FIX verdict: reads the store LIVE after the sweep window, plays a click, releases in-memory dip state. No write. |
| ui/components/ParkDrawer.kt:439 | delay(260) | SOUND ON/OFF label echo timed to the sled's travel; the toggle's own write is immediate via onToggleSound. |
| ui/screens/TactileDeckScreen.kt:218, 236 | LaunchedEffect(session/shot) | Seal-shot view state (ghosts, exit phases on the slide view). The seal booking is SessionSealTransition's, above. |
| ui/screens/TactileDeckScreen.kt:282 | delay(remaining) | Sled return: the witness callback fires BEFORE the delay (ibid. :279-281); what follows is an in-memory gate. |
| ui/screens/TactileDeckScreen.kt:354 | delay(to minute) | A print-refresh clock for the Deck's time line; display only. |
| ui/screens/TactileDeckScreen.kt:772 | (no delay) | The Deck's own completion door: plain clickable → ViewModel → repository write in the same tap. The Deck books immediately; only the Tasks leaf (G-01) waits. |
| ui/views/BlockitSuitcaseAndSwitchesViews.kt:461, 482 | postDelayed(32) | Switch animation redraw scheduling inside the view; no state write. |
| ui/views/BlockitCoreViews.kt:2080 | postDelayed(150) | One redraw after a knock; view-internal. |
| data/DayWordStore.kt / Repository prefs | apply() | G-02 — the one unsafe prefs site, filed above. |
| service/SessionSealTransition.kt:110 | commit() | Synchronous marker write; the standard G-01 and G-02 should meet. |

## PATTERNS SEARCHED (the basis of the negative)

Searched across the whole of `app/src/main`:
`delay(` (24 hits, every one tabled above) · `LaunchedEffect`
(19 sites, all inspected) · `rememberCoroutineScope` (1 —
ParkDrawer, animation scope only) · `postDelayed` (3, tabled) ·
`Handler(` / `java.util.Timer` / `scheduleAtFixedRate` (0) ·
`withTimeout` (0) · `GlobalScope` (0) · `lifecycleScope` (0) ·
`finishedListener` (0 — no animation in the tree books on
completion; the tear's booking rides a naked effect instead,
which is G-01) · `addUpdateListener` (8, all inside custom
views, driving gesture-internal redraw and readout — the value
reports DURING the gesture, nothing waits for the end) ·
`.apply()` / `.commit()` on SharedPreferences (4 sites, G-02) ·
Choreographer frame loops in views (slide/wheel internals;
view state only — the slide's latch dispatches the cascade
synchronously).

A clean negative is claimed ONLY for the patterns above, and
only as far as they reach: a write can hide behind indirection
(a callback stored and fired later) that grep cannot see. Within
what the patterns cover, G-01 and G-02 are the tree.

---

*Filed by Hale. ESR-11 was charged against build 1, dispositioned
to the drawer car, and reported around for two merges. It is
still open. The grep is its closure or its escalation — Ren
rules which.*
