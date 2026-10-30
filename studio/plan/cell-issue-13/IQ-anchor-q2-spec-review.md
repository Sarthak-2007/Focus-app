# ISSUE #13 — SPEC-LEVEL IMPLEMENTATION-QUALITY REVIEW
## Anchor seat Q-2 · Implementation-Quality · Session Room merge (Klinge 19 + Wacht A-1…A-12)

Scope of this seat: NOT design, NOT build. Every merge item is
checked, at spec level, against the real Android sensor/service
stack this app actually runs on, its battery cost, and its
failure behaviour — and marked:

- **IMPLEMENTABLE** — buildable as specced on the real stack.
- **IMPLEMENTABLE-WITH-CHANGES** — the item survives, but the spec
  text must change in the named way before a builder can execute
  it without improvising (improvised specs are where this merge's
  worst charge, Salz C-16, came from).
- **NOT-IMPLEMENTABLE-AS-SPECCED** — the spec as written cannot
  be built, or contradicts itself / the hardware. Named, with the
  smallest change that revives it.

Evidence base: `studio/plan/AUDIT-session-room.md` (§1–§8),
`cell-issue-13/MERGE-PLAN.md` (P-1…P-19 reconstruction),
`cell-issue-13/CHARGES.md` (Salz C-1…C-26, Q-1…Q-9),
`cell-issue-13/MERGE-NOTES.md`, the Wave-2 report
(`ISSUE-13-WAVE2-REPORT.md`), and the merged tree itself
(`DeskClockSettle.kt`, `MonolithViewModel.kt` sampler /
surrender / ceremony sites, `AirlockAndDisarmOverlays.kt`,
`MonolithShieldService.kt`) read at branch `agent/issue-13`.
Nobody compiled anything for this review; all arithmetic below
is checkable from the constants in the tree.

Binding rulings were NOT re-litigated: V1 amended, E-1 veil +
Landing ratified, E-4 confirmed, V6 struck, M-4 keep-awake
amended. Where a ruling's *mechanics* cannot be built as
written, that is filed against the spec text, never the policy.

---

## HEADLINE VERDICTS

1. **The merge as a whole: IMPLEMENTABLE-WITH-CHANGES.**
   27 of 31 items are implementable or implementable with small,
   named spec edits. Four items carry blocking defects (F-1,
   F-2, F-4, F-6 below). None of the four kills an item; all
   four kill the *current spec text* of the item they touch.
2. **P-1 / V1 (the tremor discriminator) is the only item whose
   central empirical claim is currently unproven on hardware.**
   The estimator in the merged tree does not measure the band
   the ruling names (F-1), its verdict can go stale forever
   (F-2), and its noise floor is an uncalibrated constant
   (audit §7 debt 9). Debt 9 is therefore a **ship gate, not a
   tuning nicety** — on this point this seat agrees with Tiefe's
   first-principles filing on this issue, reached independently
   from the constants. Until Vega's bench lands, every sentence
   of the form "V1 is tuned" is false; the spec already says so
   in code (`TREMOR_FLOOR_UNCALIBRATED`) and must say so in the
   merge record too.
3. **A-2's door invariant is NOT-IMPLEMENTABLE-AS-SPECCED at
   the standing point** — its own constants contradict it
   (F-4). One constant must yield; that is Ren's ruling to
   make, and Amt's Wave-2 report already carries the question.
   This seat confirms the arithmetic and refuses to let the
   merge record keep claiming "kept by construction."
4. **A-11(c) names a field that does not exist** (F-6). The
   "same stored break-end" is not stored anywhere in the
   schema. The item is cross-lane (#16) and currently has no
   consumer in this tree at all — `PillDerivation` has zero
   call sites and zero definitions here, and
   `MonolithShieldService` still re-derives inline
   (`MonolithShieldService.kt:126,138`). Do not mark A-11
   merged on #13's evidence; it is a spec for #16, and its
   break-end clause must be re-specced first.

---

## BLOCKING FINDINGS (spec text must change)

### F-1 · V1's band is a function of an unmeasured delivery rate — the coded poles do not cut 7–14 Hz
The ruling says: sample ~50 Hz (`SENSOR_DELAY_GAME`), band
energy 7–14 Hz. Two facts of the real stack break the naive
reading:

- `SENSOR_DELAY_GAME` is a **hint** (nominal 20,000 µs), not a
  rate contract. Delivered rates vary by device, sensor hub,
  and batching state. Any fixed-coefficient filter's band
  therefore floats with a rate nobody measures.
- The merged estimator's one-pole coefficients (VM:
  slow `0.36`, fast `0.58`) at a true 50 Hz give one-pole
  cutoffs of **≈3.6 Hz and ≈6.9 Hz** (fc = −Fs/2π · ln(1−α)).
  The difference of the two poles is thus a ≈3.6–6.9 Hz band —
  postural sway and slow drift, sitting *below* the 7–14 Hz
  tremor band the ruling names, and largely missing it.
  Coefficients that would cut 7/14 Hz at 50 Hz are ≈0.59/0.83.
  At a delivered 100 Hz the same constants would land at
  ≈7.1/13.8 Hz — right by accident. A band that is correct
  only on devices that happen to deliver double the requested
  rate is not a spec; it is a lottery.
  (MIP-07's spec, landed on this branch at `3fadf72`,
  convicts the same poles at the same numbers — two seats,
  one arithmetic. This seat adopts its timestamp-true
  direction as the cure's shape.)

**Required spec change (P-1):** the estimator must derive its
coefficients from the **measured** sample interval
(`event.timestamp` deltas, median over the registration), or
resample to a fixed grid. Constants in the spec are then
cutoff frequencies, never bare pole alphas. Add a bench line
to debt 9: record delivered Fs on the target phone alongside
the traces; a trace without its Fs is not evidence.

### F-2 · The verdict has no TTL — a stalled sensor freezes the last verdict forever
`tick()` consumes `tremorVerdict` as a timeless fact. If
delivery stalls mid-registration (hub fault, device state
change, sensor shared/revoked), the last verdict persists
indefinitely: a stale HELD suppresses **both** raise paths
with no signature actually persisting — the starvation
direction Salz named under C-17, now at the state-machine
level. The ruling's own words bound the suppression: "while
the signature persists." No samples = no signature.

**Required spec change (P-1):** the verdict carries the mono
stamp of its last sample; a verdict older than a named TTL
(suggest 2 s — two ticker grains, ≫ the estimator's ~0.4 s
EMA memory) degrades to UNKNOWN and the backstop governs.
Likewise `onAccuracyChanged` is currently a no-op:
`SENSOR_STATUS_UNRELIABLE` must force UNKNOWN for the
duration — an unreliable sensor is sensor-absent with extra
steps, and the ruling already prices sensor-absent.

### F-3 · The duty cycle's off-window semantics are unspecified — and the natural implementation re-arms the backstop against a held phone
The ruling: once HELD persists 30 s, sample 6 s in every 30 s.
The spec never says what happens to the *verdict* and the
*held-since stamp* during the 24 s off-window. The merged
implementation unregisters (good) but also **resets the
verdict to UNKNOWN and clears the held signature on the next
tick** (the tick's signature-lifted branch re-arms the settle
from that beat). Consequences a spec must price: the backstop
deadline re-arms from the clear, HELD must re-prove itself
from a cold estimator at every on-window (seed beat + EMA
warm-up ≈ 0.5–1 s of the 6 s window), and `heldSinceMono`
restarts, so the duty law's own 30 s precondition is
re-earned in fragments. It happens to mostly work because
HELD re-asserts fast; "mostly, by accident" is not a spec.

**Required spec change (P-1):** across a duty off-window the
verdict and `heldSinceMono` are **latched**, not cleared.
The off-window is a sampling pause, not an acquittal. Only a
sampled verdict change inside an on-window, or the F-2 TTL
expiring *inside* an on-window with no samples, may lift
HELD. Spec must also state the estimator warm-up budget
inside the 6 s window (seeded poles make this ~1 s; name it).

### F-4 · A-2's door invariant contradicts its own constants at fraction 0
A-2 pins: exit luminance ≥ 1.10 × hero-print luminance at
every span fraction, with standing constants hero 0.55 /
exit ceiling 0.58. At fraction 0 the best achievable ratio
is 0.58/0.55 = **1.055 < 1.10**. The invariant is satisfiable
at the descended end (0.40 / 0.36 = 1.111 ✓ — Salz Q-6) and
unsatisfiable at the standing end. Wave-2 honestly restated
this and changed neither number; MERGE-NOTES' "kept by
construction, capped at the standing 0.58" is the claim
that must not survive into the record.

**Verdict: NOT-IMPLEMENTABLE-AS-SPECCED** (one point of one
item). Smallest revival, Ren's choice, already queued by
Amt: (a) exit ceiling rises to ≥0.605 at standing, or
(b) standing hero drops to ≤0.527, or (c) the invariant is
re-scoped to the descended range where it was proven.
Until Ren rules, builders must not "fix" this by quietly
moving a ruled constant — Wave-2 was right to refuse.

### F-5 · Sub-second visual laws evaluated on a 1 Hz ticker — the spec must name the driver of every timed effect
The surrender fade (A-10(v)) is specified as "at true-off
− 520 ms the print fades 520 Curve C." The machinery that
evaluates it beats at 1 s (`delay(1000L)` ticker). A 520 ms
window on a 1 Hz grain is a coin-flip — Wave-2's cure (lead
the flag by one grain; the frame-driven overlay fade runs
inside the final second) is the honest form, and it changes
the effective window to deadline−1000…deadline ms.
**Required spec change (applies to A-3, A-4, A-10(v)):** every
timed effect in the merge names its driver — *frame clock*
(animations: A-4's table, the dawn ramp's per-frame lerp —
implementable as written) vs *ticker grain* (any deadline,
fade trigger, or suspension evaluated by the VM — honest
grain 1 s, and the spec's numbers must be written at that
grain). A-3's ramp survives because 60 s at 1 s recomposition
is 60 visible steps; D-3's descent-resume remainder (Wave-2)
is the same law read from the other side and this seat
upholds it as correctly priced, not as a defect.

### F-6 · A-11(c)'s "same stored break-end" does not exist; A-10(v)'s "readable system screen timeout" overclaims
- **A-11(c):** no break-end field exists on the session row
  (Salz C-18, confirmed: the row carries remaining-seconds
  only). Three surfaces cannot consume "the same stored
  break-end" until one exists. Two buildable forms, #16's
  pick, priced in the item: (i) derive at the reading
  instant (`now + remaining`) — what Wave-2 landed for the
  room — accepting that two surfaces reading a second apart
  can print adjacent minutes at a boundary; or (ii) stamp a
  real break-end (wall, mono) on the row at break start —
  a schema-touching change that must be priced as one, not
  smuggled under "no DB change" (see F-7). As specced today:
  **NOT-IMPLEMENTABLE-AS-SPECCED** in this clause.
- **A-10(v) "true-off":** `Settings.System.SCREEN_OFF_TIMEOUT`
  is readable without permission — that half is true. It is
  NOT the actual screen-off instant under keep-awake, device
  admin, or OEM power policy, and the merged tree in fact
  keys the fade to the designed surrender deadline instead
  (MERGE-NOTES remainder 4, honestly named). Spec change:
  strike "derived from the readable system screen timeout";
  the deadline *is* true-off by construction on the yield
  path, because the flag clear is what darkens the face.

### F-7 · Store decisions must be named in the spec, or priced both ways (A-10(ii), general law)
Salz C-25 stands and this seat generalises it: A-10 priced
"one persisted filed-marker beside the standing pointer"
with "No DB change," but the pointer lives in a Room entity
— beside it *is* a schema change. The merge escaped via a
separate prefs store, importing the two-store race Salz
C-21 convicted (Wave-2 reversed the write order — filed
before claimed — which makes the failure direction safe;
the cure is accepted). **Spec law going forward, priced
into A-10(ii) retroactively:** an item that persists
anything names its store in the spec text. "Beside X" is a
store decision. Unnamed-store items are returned by this
seat as IMPLEMENTABLE-WITH-CHANGES on sight.

### F-8 · Battery: the sampler's real cost is AP wakefulness, and HELD's exposure is unbounded — both must be budgeted in the spec
- The accelerometer at GAME delay is cheap silicon; the cost
  is delivery: registered with no batching latency, the
  listener keeps the application processor waking at the
  delivery rate for every sampled second. P-1's exposure is
  bounded in the REST/UNKNOWN cases (raise or backstop
  within ~20 s + dwell) but **unbounded under HELD**: a
  session armed while he reads in-hand for hours duty-cycles
  at 20% indefinitely. That may be the right policy — it is
  the ruling — but the spec carries no budget and no
  measurement debt for it.
- **Required spec change (P-1):** add a power proof debt
  beside §7 debt 9: one SLEEP night, sampler on vs off,
  battery %/h from `dumpsys batterystats` or the phone's own
  ledger, on the target device. Label all current drain
  numbers (including any in MIP-07's budget) as estimates
  until that night exists. Also spec the registration call
  explicitly: sampling period in µs (20,000), report latency
  0 while undecided (batching would smear the band), and
  registration owned by exactly one lifecycle (VM
  `onCleared` unregister is in the tree — Wave-2 cured the
  leak Salz's VM-Review found; keep it as a named invariant).

---

## NON-BLOCKING FINDINGS (implementable, with the failure behaviour written down)

- **N-1 · Magnitude-only band energy is orientation-anisotropic.**
  The estimator reads |a|; tremor perpendicular to gravity
  modulates |a| only at second order, tremor parallel at
  first order. Sensitivity therefore depends on how the phone
  is held — exactly what Fen's four added traces (braced
  two-hand, chest-rest, lap-rest…) vary. Per-axis band energy
  (sum of three band variances) removes the anisotropy at
  trivial cost. Not blocking — the bench traces will show
  whether magnitude suffices — but debt 9's traces MUST be
  run per-orientation and reported per-orientation, or a
  magnitude-only pass on one grip will be read as a pass for
  all grips. (MIP-07's classification work should consume
  the same per-axis data.)
- **N-2 · Process death mid-armed-undecided.** The settle's
  decided bit is in-memory by design (FLOW §2.6 prices the
  one cold re-raise over a dismissed session). Accepted as
  specced — but note the asymmetry the spec should state
  plainly: V2's dismissal bit is persisted (P-2) while the
  settle's decided bit is not; a process death between a
  hand dismissal of the *face* and session end re-raises
  once. Priced, lawful, write it in the item so no future
  panel re-files it as a bug.
- **N-3 · Charger state sampling.** The sticky-intent read
  (`ACTION_BATTERY_CHANGED`, null-register) at 1 Hz is the
  right mechanism — no receiver registration, no leak — and
  the merged fail-safe (unreadable ⇒ treat as plugged ⇒
  never surrender on a guess) is the correct failure
  direction. Spec should name that fail-safe as part of
  M-4's amended form; today it lives only in a code comment.
  Salz C-16's cure (every-beat edge stamping, sticky stamp)
  is verified in the tree and this seat closes C-16's
  spec-side: the deadline function was never the problem;
  the stamp's provenance was, and provenance is now specced
  by `chargerDisconnectStamp`'s contract. Keep that contract
  in the spec text, not only in the KDoc.
- **N-4 · A-4 SEEDED window background.** As written ("the
  window background *on the seeded path* is #000000") this
  is not literally buildable: the pre-content window of a
  cold start wears the **theme's** background — there is no
  per-path window before the first frame exists. The
  buildable form is a global black `windowBackground` on
  the app theme (the form Glanz's uncommitted change on
  this branch takes). IMPLEMENTABLE-WITH-CHANGES: re-scope
  the clause to the theme, and record that the no-flash
  proof (debt 6, capture from process start) is the only
  evidence that can acquit it.
- **N-5 · A-4 finish ground hold.** The fixed 1,200 ms hold
  against a detention that can run 1,800 ms (Salz C-23,
  Wave-2 remainder) leaves up to ~340 ms of lit Deck inside
  the seam on the faulted branch. IMPLEMENTABLE-WITH-
  CHANGES: key the ground fade to the VM's issued flag —
  the flag exists; the constant is an approximation the
  spec should not carry into batch-10 as if it were the
  design.
- **N-6 · E-1 veil trigger.** As first merged, the veil fired
  for every routine-born kind at any hour (Salz C-22);
  Wave-2 narrowed it to `ROUTINE_SLEEP_STANDDOWN`, which is
  the ratified grammar (night machine-return). Closed.
  Failure behaviour to keep in the spec: veil state is
  in-memory; a process death between stand-down and first
  touch loses the veil and lands on a lit Deck — acceptable
  (the return is then a cold open, SEEDED's law), but say so.
- **N-7 · A-10(i) ceremony gate.** The audit's cure sentence
  understated the surgery (Salz C-26): in the real engine,
  haptics and audio are gated by *different* flags
  (`hapticsEnabled` vs `soundEnabled`), and three calls, not
  one predicate, made the ceremony. Wave-2 gated the whole
  launch on the kind — the correct form. Spec law: any
  future "silence X" item must name *calls*, not predicates,
  and state which of audio/haptic/visual it kills. With
  that wording, A-10(i) is IMPLEMENTABLE and, as merged,
  correct.
- **N-8 · Doze / CPU suspend.** The 1 s ticker and the
  sampler both stall under deep sleep; every deadline in
  the merge is a monotonic comparison evaluated on the next
  beat, so late evaluation degrades in the safe direction
  (face yields late, never early; backstop fires late,
  never into a hand). One exception to record: under F-2's
  TTL, a suspend that freezes delivery mid-HELD degrades
  the verdict to UNKNOWN on wake — correct, and the reason
  the TTL must compare against sample stamps, not ticker
  beats.

---

## ITEM-BY-ITEM VERDICTS

### Klinge 19 (as reconstructed, MERGE-PLAN P-1…P-19)

| Item | Verdict | Reason (one line) |
|---|---|---|
| P-1 V1 discriminator | **WITH-CHANGES** | F-1 (Fs-derived band), F-2 (verdict TTL + accuracy gate), F-3 (duty latching), F-8 (power debt). Policy untouched. |
| P-2 V2 persisted dismissal | IMPLEMENTABLE | One persisted bit, rehydration-tested (§7 debt: process-death frame). |
| P-3 V3 notches | IMPLEMENTABLE | As finished by A-7; boundary fractions must use span arithmetic (Salz C-19's cure — breaks count in the span). |
| P-4 V4 descent | **WITH-CHANGES** | Inherits A-2's F-4 conflict + F-5's driver naming. |
| P-5 V5 block line | IMPLEMENTABLE | As cured by A-1; derivation is pure text from row state. |
| P-6 §C-1 ENDS sub-line | IMPLEMENTABLE | Pure derivation; DAE-1 follow-on keys hero+dawn to the same frozen window end — one derivation, as A-12/C-1 answered. |
| P-7 §C-2 exit line constant | IMPLEMENTABLE | E-4 confirmed; a constant cannot be unbuildable. |
| P-8 §C-3 break sentence | **WITH-CHANGES** | Inherits F-6's break-end clause (derive-at-reading form is buildable; "stored" form is not). |
| P-9 L1 hero grammar | IMPLEMENTABLE | H:MM / MM:SS pure formatter; C-8's cure verified in tests. |
| P-10 L2 hero labels | IMPLEMENTABLE | State-keyed constants. |
| P-11 L4 invented numbers killed | IMPLEMENTABLE | Deletions; idle hero is wall time (Salz Q-2). |
| P-12 L5 fake buttons killed | IMPLEMENTABLE | Deletions; one verb remains. |
| P-13 L7 banner chain | IMPLEMENTABLE | As answered by A-12/C-2 (profileName floor wired at the call site — Wave-2 caught the unwired first pass; spec should require call-site evidence, not derivation-only tests). |
| P-14 M-1 arrival | **WITH-CHANGES** | As composed by A-4; inherits F-5 (name the animation driver — frame clock — in the item). |
| P-15 M-4 keep-awake | **WITH-CHANGES** | Ownership move is right; inherits F-5/F-6 (fade grain, true-off wording) + N-3 (name the charger fail-safe). |
| P-16 M-6 stand-down question | IMPLEMENTABLE | Answered from code (A-10); a question item — its answer's items carry their own verdicts. |
| P-17 Item 5 idle/stopwatch seats | IMPLEMENTABLE | Print-layer, completed by A-6/A-8. |
| P-18 Item 17 exit dismissal | IMPLEMENTABLE | One 180 ms frame-clock animation, ground+print in parallel (Salz C-15's cure — series `animateTo` was the unbuildable-in-effect form; spec should say "parallel" in the item). |
| P-19 Item 19 light pin | **WITH-CHANGES** | Inherits N-5 (detention-keyed ground hold, not the 1,200 ms constant). |

### Wacht A-1…A-12

| Item | Verdict | Reason (one line) |
|---|---|---|
| A-1 block line cured | IMPLEMENTABLE | State gate + formatter; UNTIL for k<n derives from per-block remaining at reading instant. |
| A-2 descent completed | **NOT-AS-SPECCED** (one point) | F-4: invariant unsatisfiable at fraction 0 under its own constants; rest of the item implementable. Ren rules which constant yields. |
| A-3 dawn ramp | IMPLEMENTABLE | Frame/ticker grain is honest at 60 s (F-5); keyed to window end after DAE-1. |
| A-4 arrival/exit by cause | **WITH-CHANGES** | Cause discriminator is free (interaction stamp exists); SEEDED clause re-scopes to theme (N-4); finish row inherits N-5. |
| A-5 banner slot one height | IMPLEMENTABLE | Fixed slot; C-24's cure (measure from the real 14 sp text + maxLines/ellipsis law) is the form the spec should carry. |
| A-6 seats settled | IMPLEMENTABLE | A deletion + a seat assignment; proof = §7 debt 2's frame. |
| A-7 rule bounded/grounded | IMPLEMENTABLE | Clamp + Canvas rule + construction check (no lit bed under the rule — verify by grep for the surface variant at the draw site, zero price as specced). |
| A-8 idle banner gated | IMPLEMENTABLE | One gate at the derivation's call site. |
| A-9 room never clicks | IMPLEMENTABLE | One call deleted on one path; N-7's wording law applies to future sound items. |
| A-10 stand-down act | **WITH-CHANGES** | (i) N-7 wording; (ii) F-7 store naming (prefs route accepted only with C-21's write-order + single-slot invariant in the spec); (iii) inherits (ii); (iv) pure branch logic, implementable; (v) F-5/F-6/N-3. |
| A-11 emissaries (cross-lane #16) | **NOT-AS-SPECCED** (clause c) | F-6: no stored break-end exists; (a) no `PillDerivation` exists in this tree to consume — #16 must create it, so "route B consumes" is a creation, priced as one; (b) span-number print is implementable once (a) exists. |
| A-12 conditionals | IMPLEMENTABLE | Both answered from code (MERGE-NOTES); C-1's answer is now load-bearing for P-6/DAE-1 — keep the answer attached to the item, not only in notes. |

### Rulings' mechanics (not the policies)

| Ruling | Verdict | Note |
|---|---|---|
| V1 amended | WITH-CHANGES | F-1/F-2/F-3/F-8 are mechanics the ruling's text assumed, not decisions it made. |
| E-1 veil + Landing | IMPLEMENTABLE | Trigger narrowed to the stand-down kind (N-6); Landing bloom is frame-clock, controls live from first touch — no stack obstacle. lockNow correctly absent. |
| E-4 confirmed | IMPLEMENTABLE | Teaching lives on #14's surface; no code surface in this merge — the merge must not be marked blocked on it, nor complete-by-association with it. |
| M-4 amended | WITH-CHANGES | See P-15. The deadline's *inputs* are all readable on the real stack (sticky battery intent, mono stamps) — Salz C-16 was a wiring miss, not a stack limit. |

---

## WHAT THIS SEAT ASKS OF THE MERGE RECORD

1. Strike "kept by construction" for the door invariant
   until Ren rules on F-4; print the ratio's honest range
   (1.055 at standing → 1.111 descended) wherever the
   invariant is claimed.
2. Re-issue P-1's spec text with F-1/F-2/F-3/F-8 inside it,
   so the next builder executes a spec instead of
   reconstructing one. MIP-07's timestamp-true direction
   (`3fadf72`) is the natural vehicle; this review is its
   gate, not its rival.
3. Promote §7 debt 9 to a named ship gate for V1: bench
   traces with delivered-Fs recorded, per-orientation
   verdicts, and the power night (F-8) — all on the target
   phone — before any build notes call the sampler tuned.
4. A-11 stays OPEN as a cross-lane spec for #16 with F-6
   attached; it is not evidence-closed by #13's merge.
5. Adopt F-7 as standing spec law for every future merge
   item in this programme: persistence names its store;
   timed effects name their driver; silence items name
   their calls.

## NEXT (Law 10)

(a) WHAT I WANT: the P-1 spec re-issue (ask 2) routed
through this seat before batch-10's builder touches the
sampler again — a half-day of spec text is cheaper than a
second C-16-class cure; and Ren's F-4 ruling on the record
before A-2 is quoted anywhere as merged-clean.
(b) WHAT I COULD DO UNASSIGNED: extend this same gate to
issue #16's pill merge the day its plan forms (A-11's
three clauses are already half-reviewed above), and run
the identical constant-arithmetic check (F-1/F-4 method:
compute every named band, ratio, and floor from the
tree's own constants) over the Deck audit's numbers,
where Salz already found the ceiling-minute derivation
still living.

— Implementation-Quality (Anchor seat Q-2, issue #13)
