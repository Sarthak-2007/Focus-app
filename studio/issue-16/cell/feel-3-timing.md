# Issue #16 — Pill Timing Feel Specification

**Seat:** Feel-3 · Timing (issue-16 cell)
**Scope:** The Peek face only — the floating 2-minute countdown. Session / Break faces are out of scope except where the Peek face hands over to them at zero.
**Verdict in one line:** The derivation is honest (absolute end, ceiling, never counted) but the rendering clock is not — an unaligned 1 s ticker lets `00:01` survive up to ~1 s past the real end while the app is already re-blocked, so today a glance is true at tick instants and up to a second stale between them; the finished behaviour below fixes phase, not arithmetic.

---

## 1. How the countdown works today — evidence

### 1.1 The lease end is absolute and written once at grant ✅

- `MonolithRepository.kt:99-100` — `PEEK_DURATION_SECONDS = 120`, `PEEK_DURATION_MILLIS = 120_000`.
- `MonolithRepository.kt` `startPeekLease()` (~L1184-1196) — `leaseEndMono = SystemClock.elapsedRealtime() + durationMillis`, stored in `BlockedAppEntity.activePeekUntilElapsedRealtime`; every other row's lease is zeroed in the same write round (one live lease).
- Correct clock choice: `elapsedRealtime()` is monotonic and includes deep sleep, so neither wall-clock changes nor sleep can stretch or shrink the lease. This is right and must not change.

### 1.2 The derivation recomputes from the stored end — it never counts ✅

- `PillFace.kt:114-125` (`PillDerivation.derive`) — live lease = row with `activePeekUntilElapsedRealtime > nowMono` (max end wins as a belt); `remainingMs = end − nowMono`; `remainingSeconds = ((remainingMs + 999) / 1000)` — **ceiling**.
- `PillFace.kt:11-19` header states the design law explicitly: pure function of stored ends + caller's `nowMono`, replacing the dead ViewModel state that counted 1 Hz decrements in a different process.
- `PillDerivationTest.kt` proves it: `faceEqualsStoredEndsAtEverySampledInstant` (samples at +0 / 999 / 1 000 / 45 500 / 90 000 / 119 999 ms all equal `ceil(end − instant)`), `peekFaceNamesTheRowAndDiesExactlyAtItsEnd` (at +119 999 ms → `Peek(…, 1)`; at +120 000 ms → Session face, never `Peek(…, 0)`).
- **Consequence: there is no cumulative drift.** Over the full 120 s the value cannot walk away from the end. Any dishonesty in the shipped pill is a *staleness/phase* problem in the ticker, never an arithmetic problem in the derivation. Do not let a builder "fix" the formula — it is the one part that is already finished.

### 1.3 The ticker is an unaligned sequential 1 s loop ⚠️

- `PillController.kt:88-97` `start()`:

  ```kotlin
  while (isActive) { runCatching { tick() }; delay(1000L) }
  ```

  `delay(1000)` starts **after** `tick()` returns, so the real period is `1000 ms + tick work` (two DAO snapshot reads + a foreground usage-stats query + a main-thread render hop, `PillController.kt:110-131`). The phase relative to the lease's second boundaries is arbitrary — set by whenever the shield service happened to start — and it slowly precesses as tick work varies.
- `PillController.kt:144` — render is gated on `face.renderKey`; the window is touched only when the printed string changes. Good for battery, but it means there is no sub-second surface at all: the digits are the whole display.
- `PillController.kt:383-386` `formatClock` — plain `MM:SS`, clamped at 0. Peek therefore prints `02:00 → 00:01` in 120 discrete steps.
- No countdown animator exists. The only `ValueAnimator` in the controller is the 140 ms drag snap-to-edge (`PillController.kt` `snapToEdge`). There is no progress ring, bar, or sweep anywhere in the Peek rendering (`render()`, `PillController.kt:168-172`: label = app name, time = `formatClock(remainingSeconds)`, ✕ visible — nothing else).

### 1.4 Route B (shield notification) runs at a coarser grain ⚠️

- `MonolithShieldService.kt:176-195` — the peek face in the shade recomputes with the identical ceiling formula (`(end − nowMono + 999) / 1000`, L181), so the two mouths can never disagree *at their own sample instants*.
- But the shield loop is `delay(2000L)` (`MonolithShieldService.kt:274`), again sequential after its work. The shade's peek time is therefore up to ~2 s + iteration work stale — it can still be printing time after the lease has died. The in-code comment calls this "the loop's honest 2-second grain"; for the session face that is fine, for a 120 s fuse it is not the same promise as the pill's.

### 1.5 Enforcement dies at the end instant — the display does not

- `MonolithAccessibilityService.kt:209` — a `PEEK_2M` app is allowed only while `nowMono < activePeekUntilElapsedRealtime` (strict `<`).
- So at the true end the door shuts immediately, but the pill keeps printing `00:01` until its next tick — **0 to ~1 000 ms + tick work later**. In that window the pill says "you still have time" about a door that is already closed. That is the single worst timing lie in the current build, and it is a feel failure as much as a truth failure: the last thing he sees before being cut off is a countdown claiming a second he does not have.

---

## 2. Every place the display can drift or round dishonestly

| # | Place | Mechanism | Worst-case error | Direction |
|---|-------|-----------|------------------|-----------|
| D1 | Ceiling rounding, `PillFace.kt:124` | `ceil` prints `1` for any remainder in (0, 1 000] ms, `120` for (119 000, 120 000] ms | < 1 s overstatement of the *fractional* remainder; **0 error against the "whole seconds you still own" reading** | Overstates fraction, never understates. **Keep** — floor would kill the lease a beat early on screen and is the dishonest direction for a fuse. Ceiling is the truthful convention: the printed number is the count of seconds not yet fully spent. |
| D2 | Ticker phase, `PillController.kt:95` | Sequential `delay(1000)` after tick work; ticks unaligned to the lease's ceil boundaries | Each digit flips 0–1 000 ms + tick work **after** the true boundary it represents | Always late, never early; bounded, non-accumulating |
| D3 | Between-tick staleness, `PillController.kt:88-97` + render-key gate L144 | A glance lands between ticks and reads the previous tick's value | Printed integer can be **1 higher** than `ceil(true remaining now)`; combined with D1, up to < 2 s above the exact fractional remainder just before a tick | Overstates |
| D4 | Expiry overhang, derivation vs `MonolithAccessibilityService.kt:209` | Peek face survives until the first tick at/after the end; enforcement is instant | `00:01` displayed for up to ~1 s **after the lease is already dead** | Outright lie window (see §1.5) |
| D5 | Swallowed tick failure, `PillController.kt:93` | `runCatching { tick() }` — a failed read is "silence for one second" by design | One frozen value for ~2 s; repeated failures freeze it indefinitely with no visual sign of staleness | Overstates, unbounded if failures repeat |
| D6 | Grant-to-first-frame latency | The pill appears only when the ticker next runs after `startPeekLease` writes the end | Up to ~1 s with no pill at all after he has been granted and switched apps; first printed value is still correct (recomputed), so this is a missing second, not a wrong one | Absence, not falsehood |
| D7 | Route B grain, `MonolithShieldService.kt:274` | 2 s sequential loop | Shade peek text up to ~2 s stale; can print a live fuse up to ~2 s past the end | Overstates |

**What is NOT a problem:** cumulative/counting drift (none — §1.2), wall-clock jumps (monotonic clock), reboot (monotonic resets, lease reads dead — pill goes Absent rather than resurrecting a fuse), two competing leases (grant zeroes the others; derivation takes max end regardless), and the ✕ hand-back (next tick re-derives from stored rows, `PillController.kt` `onHandBack`).

**Glance-truth verdict for the current build:** at a tick instant, exact. At an arbitrary glance, the printed seconds equal `ceil` of the truth as it was **0–~1.1 s ago** — so NO, today a glance does not guarantee the true remaining time within one displayed second at all moments; it guarantees it within one second *plus one tick of staleness*, and at expiry it can be wrong in kind (alive vs dead), not just in degree.

---

## 3. The finished timing behaviour — specification

Numbers below are requirements, not suggestions. The derivation formula (`PillFace.kt:120-124`) and the absolute monotonic end **do not change**.

### 3.1 Cadence: stepped numerals, boundary-aligned ticks

1. **Numerals stay per-second steps.** `MM:SS` changing once per second is the right primary cadence for 120 s: a continuously rolling digit field is unreadable at a glance and makes the number feel negotiable. No tenths digit on the main figure, ever.
2. **Ticks are aligned to the lease, not to a free-running loop.** While a Peek face is live, the next wake is scheduled for the next ceiling boundary of *this* lease:
   `delayMs = ((remainingMs − 1) % 1000) + 1 + 40`
   i.e. wake **40 ms after** the instant the printed second should flip (40 ms covers the render hop with margin; hard ceiling: the flip must land **≤ 80 ms** after the true boundary, every second, including the flip into `00:01` and the death at zero). Between peeks (Session/Break faces), the existing 1 s free-running tick may stay — those faces have minute-scale tolerance.
3. **Expiry gets its own wake.** The same scheduling already yields a wake at the end instant + 40 ms; that wake is mandatory, not best-effort — it is the zero-frame deadline in §3.4.
4. **Grant is eventful.** `startPeekLease` (or the airlock verb that calls it) pokes the controller to tick immediately: first Peek frame on screen **≤ 300 ms** after the grant write, printing `02:00`. He must never spend his first peek second with no pill.
5. **A failed tick must not freeze time silently (kills D5).** If a Peek tick fails, retry once after **150 ms**; if that also fails, the next scheduled boundary wake still fires from the stored end (scheduling is computed from the end, not chained off the failed tick). The derivation being stateless makes this free — use it.

### 3.2 The smooth element: one linear fuse, anchored to the end

6. Add **one** sub-second element and only one: a thin fuse line along the pill's bottom edge (or a ring segment if a look demands it) whose filled fraction is `remainingMs / 120 000` exactly.
7. It is driven by an animator whose **duration is set from the absolute end at every tick** (`duration = remainingMs at tick`, linear interpolator, target = empty) and re-anchored on every aligned tick. It never counts, never accumulates, and a missed tick cannot make it drift — the next tick re-anchors it to the stored end. This is the same truth law as the derivation, applied to motion.
8. Update cost is bounded by construction: the animator runs only while the Peek face is live (≤ 120 s), and the render-key gate for the *window layout* is untouched — only the fuse's fill changes per frame.
9. The fuse is what makes the last second legible (§3.3) and what makes a mid-second glance informative: digits give the whole second, fuse position gives the fraction. Together a glance carries the truth to roughly **±100 ms of feel**, without printing a single extra digit.

### 3.3 The last 10 seconds: urgency without panic

The principle: **the information accelerates, the pill does not.** No faster digit changes (impossible — seconds are seconds), no strobe, no shake, no sound-per-second, no size growth. Urgency comes from colour commitment, one pulse per second, and two haptics total.

10. **T−10 s (printed `00:10` appears): enter the urgent state, once.**
    - Time figures switch from the default text colour (`PillTheme` `textArgb`, cream `#FFE4CF`) to the accent (`accentArgb`, `#F47442`). Label (app name) does **not** change colour — only the fuse figures burn.
    - The fuse line thickens from its resting weight to **2 dp** and takes the accent colour for its remaining length.
    - **One** haptic: single 25 ms pulse, medium amplitude. This is the "wrap up" tap — felt, not heard. No sound.
11. **T−10 → T−4:** each aligned second flip carries **one** pulse on the figures only: scale 1.00 → 1.06 → 1.00 over **180 ms**, ease-out, exactly once per second, synchronised to the boundary wake (so pulse, digit change, and fuse step all land in the same frame). Nothing flashes faster than 1 Hz; opacity never drops below 1.0 (no blinking — a blink hides the number at the moment he most needs to read it).
12. **T−5 (printed `00:05` appears): one more haptic**, 40 ms, firmer than the T−10 tap. This is the last interruption. No haptic at T−4…T−1, none at zero — zero is handled by the exit (§3.4), not by a buzz competing with it.
13. **T−1 (the final second):** the digits read `00:01` for its full true duration — never shortened, never held. The fuse visibly empties across this second, so 900 ms left and 90 ms left *look* different even though both print `1`. No new colour, no pulse larger than the §11 pulse: the final second must feel like the fuse burning out, not like an alarm going off.
14. **Sound: none, at any point in the 120 s.** The peek happens inside another app, often with its own audio; the pill is a visual/haptic surface. (If a look ever proposes a closing tick sound, it is capped at the single T−5 moment and off by default — not part of this spec's shipped behaviour.)

### 3.4 Zero: the exact frame the pill leaves

15. **There is no `00:00` Peek frame. Ever.** The derivation already guarantees this (`PillDerivationTest`: at the end instant the face is Session, not `Peek(0)`) — the renderer must not invent one during an exit animation.
16. **Deadline:** the Peek face is gone **≤ 150 ms after the true end instant** (the §3.1 boundary wake fires at end + 40 ms; the exit below must complete inside the remaining budget). The current ~1 s overhang (D4) is the defect this clause exists to kill.
17. **The exit frames, in order:**
    - **Frame 0 (end + ≤ 80 ms):** digits and fuse vanish **in the same frame** — the first exit frame contains no time figure at all. A shrinking pill still printing `00:01` would extend the lie through the animation; the number dies before the container moves.
    - **Frames 1…N (≤ 70 ms more, 140 ms total maximum, matching the existing snap-animation duration already in `PillController.kt`):** the pill body (label + ✕) collapses/fades in place — scale to 0.9 + alpha to 0 is sufficient; no slide, no bounce, nothing that reads as celebration or punishment.
    - **After the exit:** whatever `PillDerivation` says at that instant owns the window — in practice the peeked app is being re-blocked/intercepted at the same instant, so the pill normally goes fully Absent (or shows the Session face if he has already landed on an admitted surface). The exit must not delay, replace, or soften the block itself; the pill is cosmetic by construction and stays that way.
18. **Early hand-back (✕) uses the same exit**, triggered by the re-derived face after `recordPeekEnded` zeroes the lease — same digit-first death, same ≤ 150 ms from the tap's write. A peek that ends by his hand and a peek that ends by the clock must be indistinguishable in their last frames.

### 3.5 Route B parity

19. While a lease is live, the shield loop's peek branch wakes on the **same aligned 1 s schedule** as Route A (not the 2 s grain at `MonolithShieldService.kt:274`); the 2 s grain may resume for non-peek faces. The shade and the glass must flip in the same second — two mouths, one derivation, and now one cadence.
20. Route B has no fuse and no pulses; its urgent state is text-only: at ≤ 10 s the subtitle's time segment is unchanged in wording (`MM:SS left`) — the shade does not editorialize. Its zero deadline is the same **≤ 150 ms after end** for the *peek wording and the END PEEK action* to disappear; a notification that still offers END PEEK for a dead lease is a lie with a button on it.

### 3.6 The glance guarantee (acceptance test)

21. After this spec: **at any glance, at any moment of the 120 s,**
    - printed seconds `= ceil(end − t)` for some `t` no more than **80 ms** before the glance, i.e. the printed number is the true ceiling at the glance instant except inside an 80 ms sliver after each boundary;
    - equivalently: the display **never understates** remaining time, and overstates the exact fractional remainder by **< 1.08 s** worst case (≤ 1 s ceiling + ≤ 80 ms staleness), down from today's < ~2.1 s;
    - the displayed state (Peek vs not-Peek) is never wrong by more than **150 ms** at either end of the lease (grant frame §3.1.4, death frame §3.4.16).
22. Test it the way `PillDerivationTest.faceEqualsStoredEndsAtEverySampledInstant` tests the derivation: sample the *rendered* face at adversarial instants (boundary − 1 ms, boundary + 79 ms, boundary + 81 ms, end − 1 ms, end + 149 ms, end + 151 ms) and assert the printed value / presence against the stored end. The derivation table stays untouched and must keep passing as-is.

---

## 4. What must not change

- The ceiling formula and the absolute monotonic end (§1.1-1.2) — they are the truth the feel is built on.
- `formatClock` `MM:SS` as the only numeric surface; no tenths, no bare-seconds switch at 59 s, no percentage readout.
- One live lease, pill dies with the shield service, blocking never consults the pill.
- Never green, existing `PillTheme` tokens only — the urgent state spends the accent that already exists (`#F47442`), it does not invent a red.

— **Feel-3 · Timing (issue-16 cell)**
