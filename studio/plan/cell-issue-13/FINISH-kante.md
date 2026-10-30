# FINISH — KANTE (Finisher 4, Wacht's cell, issue #13)

Slice: **SLEEP ON CHARGER, SLEEP ON BATTERY, and the POST-YIELD
DARK** — audit `studio/plan/AUDIT-session-room.md` §1 (SLEEP
states), §3 A-2 + A-10(v). Gallery-standard pass over the
CURRENT merged code (merge + wave-2 cure pass, read fresh):
`ui/DeskClockSettle.kt`, `ui/MonolithViewModel.kt` (keep-awake /
surrender paths), `ui/components/AirlockAndDisarmOverlays.kt`
(descent), `MainActivity.kt` (flag application, post-yield
composition). Cross-read: `FEEL-samt.md` §2/§3 (S-1/S-2/S-3,
D-1…D-5 touch this slice) — cured items are verified below and
NOT re-filed; what remains is filed as K-1…K-9.

**Surgical edits: THREE landed by this seat**, all in
`AirlockAndDisarmOverlays.kt`, all logged at their sites and in
§3 below (K-4, K-5, K-6). No other code touched. Nothing
compiled, nothing committed, nothing pushed, nothing posted.

---

## §1 · VERIFIED CURED — checked in the current code, not assumed

- **Salz C-16 (CRITICAL) — the charger-disconnect stamp is now
  sampled EVERY beat. CURED, do not re-file.** The ticker folds
  the plug state through the pure stamp before either branch
  consumes it — visible or hidden (`MonolithViewModel.kt:1261–
  1268`, stamp call at :1262; `lastChargerPluggedSeen` updated
  :1268). The pure law (`DeskClockSettle.kt:333–345`,
  `chargerDisconnectStamp`): plugged→unplugged edge stamps this
  beat; a plugged beat KEEPS the stamp, never zeroes it; first
  unplugged observation stamps the first beat. Samt S-2's 03:00
  scenario now yields at 03:10. Proof in tree:
  `DeskClockFlowTest.chargerDisconnectStamp_theC16Scenario`.
- **The deadline formula is the ruled one.**
  `deskClockSurrenderDeadlineMono` (`DeskClockSettle.kt:304–
  309`) = max(settle, last interaction, last charger
  disconnect) + `SURRENDER_STILL_MILLIS` = 600 000 (:312).
  Plug-in before the deadline cancels (the plugged branch at
  VM:1301–1303 never evaluates a deadline and clears the fade);
  plug-in after it does not resurrect (the session is decided
  in the settle machine and `deskClockYieldedSessionId`
  stands until the wake path). Keep-awake ownership is in the
  VM (VM:307–310); `MainActivity.kt:259–264` only applies the
  flag it is handed. On charger the face holds all night.
- **Descent scope — Samt D-1 + D-2 CURED, do not re-file.**
  The VM derives `deskClockDescentApplies` from the stand-
  down's own `isSleepLoadout` path (VM:1258–1259; the
  derivation at VM:1475–1479); the overlay's span fraction
  exists only under it, timed, non-stopwatch
  (Overlay:~331–337). A 25-minute afternoon span and the
  stopwatch no longer descend.
- **A-2's floors hold in the pure function.**
  `deskClockPrintAlphas` (Overlay:~147–150): hero 0.55 → 0.36,
  number lines 0.55 → 0.50 and never below; the tear rule
  takes the hero ink. Label, sub-line, block line and banner
  text all print at `alphas.numberLine` in the composition.
- **No block line, no notches on routine rows — by
  construction.** Routine rows materialise with
  `studyBlocksCount = 1` (`MonolithRepository.kt:1105`);
  `deskClockBlockLine` returns null at n ≤ 1; the notch loop
  is gated `blockCount > 1` (Overlay:~505). Moench MA-1's
  ground holds.
- **Banner = "SLEEP" in the bare case.** A routine SLEEP row's
  `intention` is the routine's name ("SLEEP",
  `RoutineDerive.kt` DesiredSession) and its `profileName` is
  "Routine: SLEEP", so the fallback chain reaches "SLEEP"
  when no task stands in front of it. The pre-emption case is
  K-4 below.
- **Wake re-raise + V1 re-arm — as ruled.** A wake interaction
  while yielded re-raises SEEDED at once, stamps settle +
  interaction, and calls `noteWakeReRaise` (VM:362–373),
  which clears the decided bit and the held signature in the
  settle machine — the next auto-raise is sensor-built, never
  time-only. Proof in tree:
  `DeskClockFlowTest.wakeReRaise_reArmsTheSampler`.
- **Samt S-1, first half only:** the fade flag now leads the
  deadline by a full ticker grain (VM:1292–1294) instead of
  testing a 520 ms window — the coin flip is mostly cured.
  The residue is K-3; the clear-beat choreography is K-2.

---

## §2 · DEFECTS — 9 filed (3 cured in place by this seat)

### K-1 · HIGH — THE POST-YIELD DARK DOES NOT EXIST: the yield clears into the lit Deck (Samt S-3, UNCURED — the worst defect in this slice)

At the deadline the VM sets visible = false and keep-awake =
false (`MonolithViewModel.kt:1296–1299`). Nothing then stands
over the Deck: `deskClockYieldedSessionId` is a private var
(VM:322) with **no StateFlow**, so no composition can even see
the yielded state, and `MainActivity.kt` composes nothing for
it (the overlay call at :795–806 is the only desk-clock
surface; the veil/Landing boxes at :813–828 belong to the
routine-finish return, not the yield). What a sleeping man
faces after the surrender is the full-light Deck for the whole
OS screen timeout — 30 s on many devices, minutes if he ever
set it long for study. A-10(v)'s invariant — the flag clears
into OLED black, where cut and off are indistinguishable — is
met by the print and broken by the room.
CURE (exact, Samt S-3's, still the right one):
1. VM: expose the yielded state beside the private var —
   `private val _deskClockYielded = MutableStateFlow(false)`,
   set true in the yield beat (VM:1296), cleared in
   `noteUserInteraction`'s wake branch (VM:366) and wherever
   the yielded id is cleared (VM:374).
2. `MainActivity.kt`, immediately AFTER the
   `AlwaysOnDeskClockOverlay(...)` call (:806) so it composes
   above the Deck but below nothing the wake needs: while
   `deskClockYielded` is true, a plain
   `Box(Modifier.fillMaxSize().background(Color.Black)
   .testTag("desk_clock_yielded_black"))` — no print, no verb,
   no clickable (the Activity's `onUserInteraction` already
   receives the wake touch; the box must not consume it
   first — a non-clickable Box doesn't).
3. Proof: Samt §5.3's frame at +1 s after the clear must be
   true black, not Deck.

### K-2 · HIGH — THE YIELD BEAT UN-FADE
...[truncated 8412 chars]