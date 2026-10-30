# ISSUE #13 — FINISH, THE SEAMS (arrivals · exits · the finish seam)
Glanz, Finisher 5, Wacht's cell — Oct 30, 2026.

SCOPE: A-4's cause table as built, A-10's stand-down proof
(audit §7 debts 6–7), Salz's C-15, Daemmer's DAE-8. Nothing
else. JUDGED ON CURRENT CODE: branch `agent/issue-13` @
e155802 — the wave-1 merge (3d0f30a) + DAE-1 follow-on
(a8956eb) + CI-fix commits, CI green per the lead. Every line
below was re-read fresh in this seat; wave-1 line numbers in
CHARGES.md / FEEL-daemmer.md do NOT carry over and are not
used. No compile run (by law — CI verdicts arrive via the
lead). One surgical edit applied, logged in §6.

Files read whole or in seam-relevant part:
`ui/components/AirlockAndDisarmOverlays.kt` (the overlay),
`ui/MonolithViewModel.kt` (raise / yield / finish paths),
`MainActivity.kt` (composition order, flag application),
`data/ReceiptState.kt` (Detention, finishPulses, filed kinds),
`ui/components/ReceiptSheet.kt`, `ui/theme/MonolithCurves.kt`,
`res/values/themes.xml`, `DeskClockFlowTest.kt` (coverage).

VERDICT IN ONE BREATH: the arrival table is now exact —
C-15's cure is real and complete. The exits are not finished:
the finish seam still releases its ground on a clock instead
of on the receipt (DAE-8 stands), an unwitnessed finish runs
the seam backwards, the stand-down's veil snaps over the
clock and cuts its ruled fade — and the worst seam in the
merge is an exit A-4 never tabled: the battery surrender,
whose farewell print, having faded to nothing in its own
curve, is resurrected at full alpha and thrown off the
screen, leaving the lit Deck standing in the dark.

---

## §1 · SALZ C-15 — VERIFIED CURED, row by row

C-15 convicted the wave-1 overlay of running ground and
print in series (`animateTo` suspends). The current overlay
launches both in parallel child coroutines from one t=0 on
every row, both exits included
(`AirlockAndDisarmOverlays.kt:228–258, 276–297`). Totals
against A-4's table, recomputed from the code:

| Row | Code | Total | Table | Verdict |
|---|---|---|---|---|
| ENTRY UNWATCHED | ground `tween(260, A)`; print `tween(520, delay 140, C)` (:228–234) | print complete 660 ms | 260 A / 520 C at +140 | ✓ exact |
| ENTRY WATCHED | ground `tween(180, A)`; print `tween(420, delay 180, B)` (:241–247) | 600 ms | 180 A / 420 B at +180 | ✓ exact |
| ENTRY HAND | ground `tween(180, A)`; print `tween(260, delay 180, A)` (:252–258) | 440 ms | 180 A / 260 A at +180, 440 total | ✓ exact |
| EXIT DISMISSAL | both `tween(180, EasingThrow)`, parallel (:288–297) | 180 ms together | 180 throw together | ✓ literally together now |
| EXIT FINISH (shape) | print `tween(260, A)` (:276); ground hold+fade (:278–282) | shape ✓, timing ✗ — §2 | print 260 A, ground holds, then fades as receipt fades in | shape cured, simultaneity not |

Two sub-points checked and acquitted:
- WATCHED / HAND "print only on completed black": the
  print's delay (180) equals the ground's duration (180), and
  a tween holds its start value through its delay — the
  print's first non-zero frame lands on completed black. ✓
- UNWATCHED's print at +140, before the ground completes at
  260, is A-4's own ruled row (Mikro M-1, acquitted for the
  unwatched witness state). Not a blend defect; the table is
  the law and the code matches the table. ✓

C-15 is closed in this seat. It does not reopen.

---

## §2 · DAEMMER DAE-8 — JUDGED ON CURRENT CODE: **STANDS**

Daemmer's charge reproduces exactly; wave-2 named it C-23
and left it a remainder. The current numbers:

- Ground release is a fixed clock: `tween(260,
  delayMillis = 1200, EasingA)` — the ground is GONE at
  E0+1,460 ms (`AirlockAndDisarmOverlays.kt:278–282`). The
  code comment at :269–275 admits it in writing.
- Issuance is an event with a formula, not a clock:
  `Detention.receiptAtMs` = min(max(1480, R0+280), 1800),
  R0 = max(1200, pawlLift) (`ReceiptState.kt:230–242`);
  the faulted branch issues at exactly 1,800
  (:252–254). The VM's detention loop additionally polls
  at 25 ms grain and pays a publish transaction before
  `emitReceipt` (MonolithViewModel.kt, finish loop).
- The receipt's own fade-in is 280 Curve C
  (`ReceiptSheet.kt:60–63`; `MonolithCurves.kt:72–73`,
  specC default 280) — it can only START at issuance.

So for a witnessed manual finish the uncovered window is
1,460 → issuance ∈ [1,480, 1,800]: **20 ms on the cleanest
pawl, 340 ms faulted** — Daemmer's arithmetic, unchanged.
What stands in the window is the Deck's held frame at Deck
light. Item 19's pin, as A-4 extended it, is a simultaneity
("the ground fades AS the receipt fades in") and a light
law ("no surface brighter than the clock between clock and
receipt"); a fixed 1,200 ms hold honours neither — it is a
guess that happens to land near the clean case and misses
the faulted case by a third of a second, on exactly the
nights that already went wrong.

Extension Daemmer did not file (same defect, wider):
for routine-born kinds the receipt NEVER stands
(`emitReceipt` refuses them, VM finish path; they are born
filed) and the veil gates on ROUTINE_SLEEP_STANDDOWN alone
(VM:1572–1573) — so a witnessed ROUTINE_ATTENDED /
ROUTINE_PARTIAL finish gets the hold, the release at 1,460,
and then nothing: the seam's second half does not exist for
those kinds. The ground should release on the issuance
EVENT for every kind; what fades in opposite it is the slip
where a slip stands, the veiled or plain Deck where none
does — at the same instant either way.

CURE (Daemmer's, endorsed verbatim, still exact): the VM
raises a ground-release fact when the finish issues
(`emitReceipt`, and at filed-issuance for routine kinds);
the overlay animates the ground out (260 Curve A) when it
flips, retaining a 2,200 ms failsafe delay so a halted chain
can never trap the face under black. SMALL. Severity in
this seat: MEDIUM — Daemmer filed LOW for the blink; the
pin it breaks is a named ruling, and the faulted branch
(the longest gap) is the case the pin exists for.

---

## §3 · A-10's PROOF (§7 debt 7) — code-level verdict

A 03:30 stand-down, VM alive, both plugs — the record must
show NO ceremony and NO standing slip.

- NO CEREMONY — PASS, at two independent gates.
  `finishPulses(ROUTINE_SLEEP_STANDDOWN) = false`
  (`ReceiptState.kt:120–127`), and the VM gates the WHOLE
  ceremony launch on the kind — `ceremonySounds = kind !=
  ROUTINE_SLEEP_STANDDOWN` (VM:1620–1621) — so the E0 knock
  and the E880 pair, haptics included, are never called.
  The gate sits on the calls, as ruled. ✓
- NO STANDING SLIP — PASS, at three doors. The filed
  marker is written BEFORE the № claim in publish
  (Repository, C-21 order); `emitReceipt` refuses
  routine-born kinds outright; rehydration refuses a filed
  session. The record (№, "SLEPT THROUGH" row) exists; the
  morning's first surface is not it. ✓
- MOTION SILENCE — CONDITIONAL PASS. If the face had
  already yielded on battery, `onFinishE0`'s hide is a
  no-op (visible already false; the effect does not
  re-fire) — nothing moves. ✓ If the face STANDS at 03:30,
  the finish exit runs — correctly — but under a veil that
  snaps on over it: see G-4. The exit's own motion is
  lawful; its lighting is overridden.
- Debt 7 itself remains a HARDWARE debt: CI green proves
  `DeskClockFlowTest` (incl. `standDownIsSilent_andClassified`),
  not a 03:29:30–03:31:00 room recording. Nothing in this
  seat discharges it; Daemmer's timer-wins variant of the
  recording (his §5(a)) is endorsed and added in §7.

---

## §4 · DEFECTS

### G-1 · HIGH — ★ WORST. The surrender yield exits through the dismissal throw, and the faded print resurrects at full alpha on its way out.
A-4 tables exactly two exits; A-10(v) designs a third
ending — print fades 520 Curve C to 0 over the black
ground, the flag clears INTO OLED black, "cut and off
indistinguishable." The current code gives the yield no
exit mode of its own:
- At the deadline the VM sets, in one beat,
  `_isAlwaysOnDeskClockVisible = false` AND
  `_deskClockSurrenderFade = false` (VM:1295–1299).
  `exitIsFinish` is whatever the raise left — false — so
  the overlay takes the DISMISSAL branch: ground and print
  throw to 0 together over 180 EasingThrow
  (Overlay:284–297).
- Worse, the surrender effect answers `surrenderFade =
  false` with `surrenderAlpha.snapTo(1f)`
  (Overlay:303–310). The print the farewell had spent
  520 ms fading to nothing (VM:1292–1294 leads the flag by
  one ticker grain so the fade completes) SNAPS BACK to
  full effective alpha — `printAlpha` was never animated
  out — and is then thrown off over 180 ms while the
  ground throws off beneath it.
Net visible act, in a dark bedroom: a face that had gone
to sleep in its own curve flashes back whole for ~180 ms,
then the lit Deck stands revealed until the OS screen
timeout — Samt's S-3, still uncured in the current tree
(no yielded black surface exists anywhere in
MainActivity), compounded by a resurrection neither Samt
nor Daemmer filed.
CURE (exact, three parts): (i) a third exit mode —
the VM's yield beat sets a `deskClockExitIsYield` fact
(or reuses a seam-state, G-8) instead of only flipping
visible; the overlay's yield branch animates NOTHING:
ground stays snapped at 1f, print stays at 0. (ii) The
surrender effect must not snap back on the yield beat:
when the hide is a yield, `surrenderAlpha` holds 0 (the
snapTo(1f) arm fires only on a raise). (iii) Samt's S-3
composition: while the VM's yielded session is live,
MainActivity composes a plain true-black full-screen box
above the Deck, so the flag clears into black and stays
black until his wake tap re-raises SEEDED (VM's existing
wake path). SMALL–MEDIUM. This is the defect the merged
build must not ship with: it fires on EVERY battery
surrender, at the deepest point of the night, in the
slice's own grammar — a farewell that undoes itself.

### G-2 · MEDIUM — DAE-8 stands: the finish ground releases on a fixed 1,200 ms clock, not on issuance.
Full judgment in §2. Ground gone at E0+1,460
(Overlay:278–282); issuance ∈ [1,480, 1,800]
(ReceiptState:230–254) plus poll/publish latency; 20–340 ms
of brighter held-frame Deck inside the seam, and for
routine-born kinds no receipt ever arrives opposite the
release. Cure: §2 (release on the issuance event, 2,200 ms
failsafe). Wave-2's C-23 remainder — confirmed open.

### G-3 · MEDIUM — The unwitnessed (branch U) finish runs the finish seam backwards.
`onFinishE0` sets the FINISH exit unconditionally
(VM:1558–1564) before the witnessed split. On the
unwitnessed path the receipt is then published
immediately at discovery (VM:1582–1588) — its own comment
says "no held frame, no detention." The overlay does not
know: a standing manual slip fades in (280 C) UNDER /
beside a ground that holds a further 1,200 ms and fades
1,200–1,460 (the sheet composes after the overlay in
MainActivity, so the slip draws over black, floating on
nothing, while the Deck stays covered for over a second
after the paper has arrived). For a routine-born
unwitnessed finish the same 1.46 s of held black precedes
a receipt that will never stand. The seam's simultaneity
is inverted, and the code contradicts its own comment.
CURE (exact): move the clock hide inside the branches —
witnessed finishes hide at E0 with `exitIsFinish = true`
as now; the unwitnessed branch publishes FIRST and hides
in the same launch after `emitReceipt` returns, with an
instant (snap) exit, since its receipt is already
standing / filed. SMALL.

### G-4 · MEDIUM — At the stand-down, the veil snaps on ABOVE the standing clock and cuts its ruled fade.
`onFinishE0` sets `_nightVeil = true` in the same breath
as the finish hide (VM:1563–1573). In MainActivity the
veil composes AFTER the overlay (overlay :795; landing
:813; veil :820–826) — i.e. on top of it. A 0.80 black
scrim with no animation therefore lands on the clock face
at E0: the print's ruled 260 Curve A fade (Overlay:276)
plays underneath at an effective 20% — the farewell's
first act is an instantaneous cut, not the fade — and the
ground's hold/fade is spent invisible. Daemmer acquitted
the gap-coverage for this kind; the coverage is real, but
it is bought by overriding the exit it covers.
CURE (exact): compose the E-1 pair UNDER the desk-clock
overlay — move the landing-dim and veil boxes before the
`AlwaysOnDeskClockOverlay` call in MainActivity. The
clock then exits on true black over the veiled Deck, the
print fade is seen as ruled, and the ground's fade reveals
the veiled Deck (0.80) instead of bare Deck. While the
clock is hidden the overlay returns early and the veil's
clickable is reachable again, so no behaviour is lost.
SMALL (source order only).

### G-5 · MEDIUM-LOW — The unveil still flashes: veil 0.80 snaps off, the Landing dim animates up from 0. (DAE-6, still open.)
`unveilNightReturn` clears the veil and sets the Landing
in one call (VM:380–383). The veil is an unanimated 0.80
box (MainActivity:820–826); the Landing is
`animateFloatAsState(target 0.45, tween(520, C))`
(MainActivity:273–283) whose value at the tap is 0. So
the tap's first frames show the Deck at FULL light, easing
down to the Landing's 55% over 520 ms — a flash at the
exact touch E-1 exists to make gentle. Wave-2 listed
DAE-6 as a remainder; it stands.
CURE (exact): on unveil, seed the dim's animated value at
0.80 before targeting 0.45 (hold the scrim's own alpha as
the animation's start — one state write in the host, or
drive both scrims from one alpha in the VM), so the
sequence is 0.80 → 0.45 over 520 Curve C with no frame
brighter than the veil it replaces. SMALL.

### G-6 · LOW — SEEDED's #000000 window background was nowhere in the tree. **CURED IN THIS SEAT (edit logged, §6).**
A-4's SEEDED row carries a construction clause: "the
window background on the seeded path is #000000."
Pre-edit, `res/values/themes.xml` held a bare
`Theme.DeviceDefault.NoActionBar` style, and no
`windowBackground` / window-background write existed
anywhere in res or in `MainActivity.onCreate` — frame
zero of a cold open wore the platform default, the one
unruled surface in the arrival table. Cure applied: one
item, `android:windowBackground = @android:color/black`,
in the app theme. A CI verdict on the edited tree is the
lead's to collect.

### G-7 · LOW — SEEDED's "frame one at full lawful alpha" rests on a post-composition snap, not on construction.
Both animatables initialise at 0f for every cause
(Overlay:203–204); SEEDED reaches 1f only when
`LaunchedEffect` runs its `snapTo` (Overlay:208–215).
In practice the snap lands before the first drawn frame —
but the table's strongest row ("NO fade — frame one at
full lawful alpha") should hold by construction, the way
the rest of the table now does, not by effect scheduling.
The same snap path serves the battery-yield wake re-raise
(VM:368–371), where the overlay had fully exited.
CURE (exact): initialise the animatables from the cause
at first composition for the raise —
`remember(visible) { Animatable(if (visible && entryCause == SEEDED) 1f else 0f) }`
shape, or drive the SEEDED frame from a `graphicsLayer`
floor of 1f while the cause is SEEDED and no exit has
run. ZERO–SMALL.

### G-8 · LOW — The seam state is three loose flows; one raise site omits the exit-flag reset.
`visible`, `entryCause`, `exitIsFinish` are three
independent StateFlows (VM:249–274), written in sequence
at each raise (VM:1223–1231 tick; :1967–1972 hand) and
consumed by an effect keyed on two of them
(Overlay:205). A recomposition landing between the cause
write and the visible write starts the previous cause's
row, then cancels and restarts when the true cause lands
— a mid-alpha freeze inside the new row's delay. Rare
(same-beat writes usually coalesce), real, and unprovable
either way by the current tests, which is itself the
point: the seam's atomicity is folklore.
Compounding: the wake re-raise (VM:360–374) is the one
raise site that never resets `_deskClockExitIsFinish` —
today unreachable-to-harm (the finish path clears the
yielded id), but it is the reset's absence at exactly the
site a future path will copy.
CURE (exact): one `StateFlow<DeskClockSeam>` carrying
(visible, entryCause, exitKind) written in a single
assignment at every raise/hide site, wake included.
SMALL.

---

## §5 · ACQUITTALS (verified in current code, binding on this seat)

- ENTRY WATCHED's black slab is gone (§1) — the watched
  phone no longer dies and resurrects.
- EXIT DISMISSAL is 180 EasingThrow, ground and print
  together, from every hand path (chip dismiss, disarm,
  valve — VM hide sites all leave `exitIsFinish` false
  from their raise). Item 17 stands as built.
- EXIT FINISH's first half is exact: print fades 260
  Curve A over a ground that HOLDS at full black — no
  brighter surface appears during the fade or the hold;
  the pin fails only in the release's timing (G-2).
- The receipt's own shape is right: 280 Curve C fade-in
  (ReceiptSheet:60–63) — DAE-8 was always about its
  timing, never its curve.
- A-10(i)/(ii)/(iii) at the stand-down: silence on the
  calls, born-filed slips, dismissal writes through —
  §3. The 03:30 act exists now; G-4 is its lighting,
  not its existence.
- The overlay's exit survivorship is sound: the early
  return (Overlay:311) requires BOTH alphas at 0, so no
  exit in the table can be uncomposed mid-flight.

## §6 · EDIT LOG
One surgical edit, applied by this seat, uncompiled by
law (CI via the lead):
- `app/src/main/res/values/themes.xml` — G-6's cure:
  `android:windowBackground = @android:color/black` added
  to `Theme.MyApplication`, with the A-4 clause cited in
  a comment at the site. Resource-only; no code touched.
No other edits. G-1…G-5, G-7, G-8 are specified, not
applied: each spans the VM ↔ host seam and must land as
one CI-verified change under the lead, not as an
uncompiled drive-by in a finisher's slice.

## §7 · PROOF DEBTS THIS SEAT ADDS (to audit §7.6 / §7.7)
(a) The surrender, recorded (extends debt 7's family):
battery SLEEP span to the yield deadline — frames at
fade −0 / +260 / +520 ms and at deadline +0 / +90 /
+180 ms / +5 s: after G-1's cure the record must show
print gone by +520, NO frame of resurrected print, and
true black (not Deck) at every later frame.
(b) The finish seam, timed (debt 7's sibling): a faulted
manual finish, frames at issuance −20 / +0 / +140 ms —
after G-2's cure, ground and receipt cross-fade in the
same window; no frame of bare held-frame Deck.
(c) The stand-down with the face STANDING (debt 7 as
written covers VM-alive; make the clock's presence
explicit): 03:29:30–03:31:00, the print's 260 fade
visible as a fade — not cut by the veil (G-4's proof).
(d) Cold open from process start (debt 6, SEEDED arm):
first three frames — window black, face at full lawful
alpha on frame one of the face (G-6/G-7's proof).

## NEXT (Law 10)
(a) WHAT I WANT: G-1's cure landed before any build
carries this merge to a phone — it is the only defect
here that fires every single battery night.
(b) WHAT I COULD DO UNASSIGNED: sit the seam-timing
bench when hardware exists — debts (a)–(d) above are
four recordings, and this seat will read them frame by
frame against the table it has now memorised.

— Glanz, Finisher 5 (the seams), Wacht's cell, Oct 30, 2026
