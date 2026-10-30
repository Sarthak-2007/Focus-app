# PLAN AUDIT — Dens (Critics, density/numbers)

*Target: every load-bearing constant in `plan/MASTER-PLAN.md` (5,229 lines)
against `plan/REGISTER.md` (417 lines), which claims (REGISTER.md L5–13) to be
"the single home of every load-bearing constant" whose value "governs" wherever
a plan file differs. Method: every occurrence grepped and read in place;
arithmetic recomputed, never asserted. Severity: **S1** = a builder following
the printed text builds or fences wrong · **S2** = two authorities in force at
once, or a status the file contradicts itself on · **S3** = arithmetic opacity
or a dead clause that misleads without changing the build.*

---

## Table of constants

| Constant | Register value (line) | MASTER-PLAN occurrences (line) | Verdict |
|---|---|---|---|
| ARM wall | 10.0s (L19) | §2.6 L532–538 "inside the 10s wall"; §5.8 L2573–2575 | **AGREE** |
| ARM worst case (binding) | **8.0s** (L21–26) | §0.1-2 L135–136 prints **≈6.1s "stands in print"**; §9 C-4 L4853–4858; A-13 L5014; B-17 L5085–5087 | **DISAGREE — Charge ARM-1 (S2), ARM-2 (S2)** |
| ARM Deck path | ≈2.0–2.3s typ / ≈3.3s worst (L24–25) | §2.6 L532–534, identical figures | Numbers agree; **derivation DISAGREE — ARM-3 (S2), ARM-4 (S3)** |
| ARM plate (AOD) path | ≈3.1–3.5s typ / ≈7.2s worst (L25–26) | §2.6 L535–536; §5.8 L2573–2575 | **AGREE** (but see ARM-2: 7.2 is the body's max, 8.0 is never derived) |
| Slide GATE | 0.68 (L36) | §2.6 L388–389, L395; §8 L4314; C-1 L4810 | **AGREE — acquitted** |
| Slide CATCH / COMMIT | 0.84 (L38–40) | §2.6 L388–390, L398–400 (D4 notches at 0.84); §8 L4316 | **AGREE — acquitted** |
| Slide OFF-STOP | 0.65 (L41) | §2.6 L435; §8 L4317, L4665 | **AGREE — acquitted** (arithmetic below) |
| Slide buried coords 0.80 / 0.82 | superseded only (L49–58) | **zero occurrences in MASTER-PLAN** (grep `0\.80\|0\.82`: no hits) | **AGREE — acquitted** |
| Slide verdict deadline | 1500ms / Park / UNKNOWN **DEAD**; survives only in Warrant fallback (L61–65) | §2.6 L423–471 specs Park + 1500ms deadline + UNKNOWN + Canary in present tense; §8 L4106–4108, L4334–4337 and C-1 L4805–4820 kill them | **DISAGREE inside MASTER — ARM-3 (S2)** |
| Band segment / cap (operative) | draft body: **85.5 / 77.5** (L150–156); amendment: **73.5 / 65.5** (L193–198) | §6.4 L2968–2974 prints 73.5 / 65.5 with the correction stated; check L3250–3251 | MASTER agrees with the amendment; **Register disagrees with itself — BAND-1 (S2)** |
| Band demote geometry 98 / 90 | buried by amendment (Register L199–201) | §6.4 L2950–2952, named dead | **AGREE — acquitted** |
| Room fixed sum | **796dp** (L162); amendment L405–411: headline stands, stack does not produce it, centre struck | §2.2 L201–206 prints 796 + centre (206, 404); B-51 L5164–5173 admits the stack fails | Both docs print a sum **their own arithmetic refutes — ROOM-1 (S1)** |
| Room reserve / fence | reserve = H − 796, floor 20dp, return under H=820 (L162) | §2.2 L203–204, same | Agree with each other; **floor clause is dead — ROOM-2 (S3)**; fence superseded in fact by 841 — ROOM-1 |
| Wheel scale | 9° = 5 min · FLOW + 10–180 · travel 324° (L72–74; amendment L246–249) | §2.4 L255 prints Uhr's rival scale (36 detents, 10.000° steps, values 5–180, 0→+350°); §2.4 L272 prints Vega's; C-10 L4900–4907 corrects | **DISAGREE inside MASTER — WHEEL-1 (S2)** |
| Valve bar / banks / hold | bar full-width × 112dp, banks 15/30/45, 60.0 held s (L117–119) | §0 L51–53; §5.3.1 L1722–1725; §5.3.2 L1736–1737; bank table L1800–1806 (560/470/390Hz) | **AGREE — acquitted** |
| Drain law | 800ms grace → 1.0× × 3.0s → 2.0×, floor = last bank, worst tail ≈9.7s (L120–124) | §5.3.4 L1822–1831; acceptance L2748–2749, L2762–2764 | **AGREE — acquitted** (arithmetic verified below) |
| Token | exactly 1/week, Monday 00:00, spend only at 60.0 (L126–131) | §5.3.7–8 L1968–1972, L1988; A-12 L5013 | **AGREE — acquitted** |
| Spectacle freezes | exactly 4: slide 40 · final sub-step 50 · DONE 40 · bank catch 40 (L111–113) | ledger restated §2.6 L541–544; spends at L1167, L1238, L1611, L1797–1798; fifth refused §8 L4637–4638 — **but §4 L1162–1163 spends a 5th** | **DISAGREE inside MASTER — SPEC-1 (S2)** |
| Build prices (lines / days) | **none — Register carries no price anywhere** (grep `price\|lines\|engineer`: no price entries) | scattered per-item only: L163 (~140 lines), L863 (~30), L1645–1649 (≈105 / ≈205 / ≈190 / ≈200 / ≈30), L2667 (~1 engineer-day), L3432–3435 (≈+25 / ≈5), L3695 (≈+31), L4095 (≈230) | **DISAGREE by omission — PRICE-1 (S2)** |

---

## Charges

### ROOM-1 — S1 — The room sum is false in both documents, and the adopted correction is in neither
- Register L162 fixes **796dp**; MASTER §2.2 L202–204 prints "Fixed sum 796."
- MASTER §2.2's own printed stack, summed as printed: header 48 + S20 20 +
  readout 88 + S8 8 + NEXT 20 + wheel 288 + task 20 + S20 20 + slide 72 +
  S20 20 = **604**; adding the only nav height the plan builds, the §6.4 rail
  at 52dp (MASTER L2956), gives **656 ≠ 796**. Shortfall: **140dp** of the
  headline is carried by no element in the section that asserts it.
- Both documents confess this: Register amendment L405–411 ("the filed §2.2
  stack does not arithmetically produce 796 on any single reading") and
  MASTER B-51 L5164–5173 (same admission, charge OPEN).
- The decomposition that does close is filed outside both documents:
  filed debt 744 + 64 − 12 = 796 verifies for the strip, but the reserved
  sentence slot (+30 = 22dp slot + 8dp beat) was never booked → 826; Rosa's
  accepted 5dp strip compression → **821** (`plan/SLICE-D-E-BUILD-READY.md`
  L350–356, L563–567). Ren **adopted** 821 with lawful minimum room
  **H = 841** (= 821 + 20 floor; arithmetic checks) in his fork rulings
  (`mail/marlowe/read/0905__from-ren__slice-de-fork-rulings.md`, F19+F20).
  Neither REGISTER.md nor MASTER-PLAN.md carries 821 or 841 anywhere
  (grep: no hits in either file).
- Consequence, priced: MASTER L204 and Register L162 fence the build at
  "under 820 returns to the coordinators." Under the adopted correction the
  lawful minimum is 841. Every device with 820 ≤ H < 841 passes the printed
  fence and fails the adopted room. The Register's founding claim — one
  value per constant, register governs — is false for the plan's largest
  constant: three sums are in circulation (796 printed, 821 adopted, 841
  lawful minimum), and 821 itself carries an open +16 contingency to **837**
  if Emil's imprint allocation falls outside the 744 base
  (SLICE-D-E-BUILD-READY.md L367).
- Collateral: the wheel centre **(206, 404)** is struck as a constant by
  Register L407–410 yet still printed as fact in MASTER §2.2 L201. The
  strike is arithmetically right: centre y = 228 (NEXT end) + gap + 144
  (R144); at the S32 gap §2.2's coordinates require, 228 + 32 + 144 = 404;
  at the S20 charge §2.2 itself asserts, 228 + 20 + 144 = **392**. MASTER
  prints the centre and the charge that refutes it on adjacent lines.
- Feeder: MASTER C-3 L4843–4851 — the Counterfoil blank leaf at **56dp**
  stands against §2.2's **20dp** task-line slot, a further **36dp** unbooked,
  "not corrected by inventing a number," carried to B-38/B-51.

### ARM-1 — S2 — Two binding ARM worst cases stand in one file, and §9 contradicts itself on whether that is closed
- Register L21: **8.0s** is "the binding priced bound."
- MASTER §0.1-2 L135–136 (Ren's adopted text, the first authority a builder
  meets): "worst path **≈6.1s stands in print**; the ≈2.5s claim is dead."
- MASTER C-4 L4853–4858 admits the state plainly: the 8.0s figure
  "supersedes §0.1-2's ≈6.1s… This entry corrects nothing above §0.1's own
  line… **Until he strikes it, both figures stand in the file**."
- §9 then files the same collision twice with opposite statuses: table A-13
  L5014 declares it "**closed as a collision** by the Register's one
  figure — 8.0s binding," while charge B-17 L5085–5087 holds it "**HELD** ·
  closes when: Ren strikes it (C-4)." A charge cannot be closed in the
  A-table and held in the B-list of the same section. Difference at stake:
  8.0 − 6.1 = **1.9s**, 19% of the 10.0s wall.

### ARM-2 — S2 — The binding 8.0s is asserted, never derived; the body's own maximum is 7.2s
- Every per-path figure in MASTER's body tops out at **7.2s** (plate worst,
  L535–536, repeated L2573–2575), and that figure already "includes one
  full failed drag." Register L21–23 attributes 8.0s to "Barriere's worst
  hand, one failed slide included (HUMANS-PLAN, HPA-23)" — a source in
  neither audited document, and no line in either document shows the
  addition that produces 8.0 from 7.2. The unexplained residue is
  8.0 − 7.2 = **0.8s**. A-13 (L5014) closes the collision "by the
  Register's one figure" — closure by citation of the number whose
  derivation is the thing missing.

### ARM-3 — S2 — The Deck worst case (3.3s) is derived from machinery both documents have killed
- Register L61–65: the verdict is synchronous ≤5ms; "The 1500ms deadline,
  the Park, and the UNKNOWN class… are **DEAD**"; 1500ms survives only in
  the Warrant fallback. MASTER agrees in its correction layer: C-1
  L4805–4820 and §8 L4334–4337 ("The 1500ms deadline, the Park, and the
  waiting machinery of §2.6-as-filed are dead").
- But MASTER §2.6's body still specs that machinery in the present tense —
  the tap's Park (L429–433), UNKNOWN at the 1500ms deadline (L437), the
  Canary's 300/800/1500ms ladder (L464–471) — and still **derives** the
  Deck worst case from it: L533–534, "worst = **1500ms Park deadline +
  1.8s cascade ceiling**" = 3.3s.
- The derivation also does not sum on its own terms: the drag to intent
  (≈1.2s, same sentence) precedes the finger-up that enters the Park; a
  sequential reading gives 1.2 + 1.5 + 1.8 = **4.5s ≠ 3.3s**. Either the
  deadline runs concurrent with the drag (nowhere stated) or the printed
  worst understates its own path by 1.2s. The number 3.3 matches the
  Register (L25); its proof rests on a dead deadline and a missing term.

### ARM-4 — S3 — Deck typical lower bound does not sum
- MASTER L533: typical ≈2.0–2.3s from "drag ≈1.2s to intent + proving
  0.6–1.1s." 1.2 + 0.6 = **1.8**, not 2.0. The upper bound sums
  (1.2 + 1.1 = 2.3 ✓); the lower bound carries an unexplained **0.2s**
  (presumably Deck-open time, nowhere booked).

### SPEC-1 — S2 — §4 spends a fifth freeze in its operative spec and convicts it in its own verification
- The registered ledger is exactly four (Register L111–113; MASTER
  L541–544, identical list).
- MASTER §4's operative strike spec, L1162–1163: an **ordinary** sub-step
  strike carries "Hit-stop at ~70% for **40ms** (Zeile Rung 2)."
- MASTER §4's freeze verification, L1450–1455: "this screen spends exactly
  two of the Register's four freezes… ordinary sub-step hit-stops
  (**Zeile 40ms**, Luz 60ms) **spend 0ms**."
- If the operative spec governs, Tasks spends three freezes (ordinary 40 +
  final-step 50 + DONE 40) and the app spends **five** distinct freeze
  events — one over the Register's cap, with none of the four named to
  die, which Register L112–113 requires. If the verification governs,
  L1162–1163 is dead text sitting in the build spec. Both cannot ship.

### BAND-1 — S2 — The Register's main body still prints the wrong band as "operative"
- Register L150–156 (Chrome and room, the register proper): "Operative
  geometry = Designers' V-TAB figures (**segment 85.5dp, cap 77.5dp**),
  which the filed plan priced for exactly this branch."
- Register amendment L193–198 corrects it: that pair is the **no-PARK**
  geometry; the with-PARK pair is 73.5 / 65.5. MASTER §6.4 L2968–2974
  prints the corrected pair and the correction.
- The amendment governs by the Register's own law, but the main-body line
  was never struck: a builder reading the Register top-to-bottom builds an
  85.5dp segment band — over-wide by 85.5 − 73.5 = **12dp per segment,
  48dp across four stops** (exactly PARK's flat cost, spent twice).

### WHEEL-1 — S2 — §2.4 carries two rival wheel scales in one section
- MASTER §2.4 L255: "Scale: **36 detents**, 5-minute steps, values
  **5–180, 10.000° steps**, clockwise 0→+350°" (Uhr's frame; span check:
  35 steps × 10° = 350° — internally consistent, and wrong).
- Register L72–74: **9° = 5 min · FLOW + 10–180 · travel 324°**
  (check: (180 − 10) ÷ 5 = 34 steps × 9° = 306°, + 18° Strait = 324° ✓).
  MASTER §2.4's own Strait paragraph L272 already builds on 9°.
- The correction exists only at C-10 L4900–4907, ~4,650 lines after the
  paragraph it kills, which still opens the wheel spec in the present
  tense. C-10's own warning is the charge: "no builder may resurrect the
  first paragraph because it prints first" — in the merged file, it does
  print first.

### PRICE-1 — S2 — Prices are load-bearing and unregistered; MASTER's are fragments with no total
- The Register's claim (L5–13) covers "every load-bearing constant"; it
  contains **no build price at all** — no lines, no days, for any item.
- MASTER's prices, complete inventory: export-kill **~140 lines** / 5 files
  (L162–163) · icon pipeline **~30 lines** (L863) · Task #0 **≈105 lines**
  (L1645) · steps-as-rows **≈205** + completion-events **≈190 lines**
  (L1647–1648) · Vernier **≈200 lines** + ≈30 semantics (L1648–1649) ·
  Drawer search **≈+25 lines** (L3432) + ≈5 lines escape (L3435) ·
  Drawer item **≈+31 lines** total (L3695: +6 DAO, +15 ViewModel, +10
  screen — sums ✓) · Watch Store composed price **≈230 lines** (L4095).
  No section sums them; no slice or build total exists in either document.
- The only **day** price in MASTER, **~1 engineer-day** (L2667), prices the
  Extinguished Clock — a carried proposal its own section refuses to enter
  (§5.8.11; open charge B-55, L5185–5192). The plan's sole day-denominated
  price is attached to the one item that is not a build.
- The build totals a builder actually plans against — Slice D ≈1,550 net,
  Slice E ≈1,700 net (SLICE-D-E-BUILD-READY.md L617–620), noted by Ren in
  his fork-rulings letter — have no home in the plan of record or its
  Register.

### ROOM-2 — S3 — The reserve floor of 20dp is unreachable under the printed fence
- Register L162 / MASTER L203–204: reserve = H − 796, floor 20dp, build
  returns under H=820. At H = 820 the reserve is 820 − 796 = **24dp**;
  the 20dp floor would first bind at H = 816, which the same sentence
  already returns to the coordinators. The floor clause governs an empty
  interval (816–819 is returned, 820+ never reaches it). Dead text in
  both documents. (Under the adopted 821 sum the same shape recurs:
  841 = 821 + 20 exactly, so there the floor and the fence coincide —
  one more sign 821/841 is the coherent pair and 796/820 is not.)

---

## Acquittals — constants that check out everywhere, arithmetic shown

**Slide coordinates (the plan's cleanest numbers).** GATE 0.68
(Register L36) at MASTER L388–389 (zones FREE 0–0.68 · DAMPED 0.68–0.84 ·
TAKEN 0.84–1.00), L395, §8 L4314, C-1 L4810. CATCH 0.84 (Register L38) at
L389–390 ("displayed progress crossing 0.84 under contact"), L398–400
(D4 notches re-coordinated to 0.84 × cap-travel), §8 L4316, A-20 L5021.
OFF-STOP 0.65 (Register L41) at L435 and §8 L4317, L4665 — and the
derivation sums: gate − 0.03 = 0.68 − 0.03 = **0.65** ✓. TAP MESH 140ms
to 0.92 + 110ms assist spring (Register L44–47) at L423–425, identical.
The buried rivals **0.80 / 0.82 occur nowhere in MASTER-PLAN** (grep, zero
hits); they live only in the Register's superseded list (L49–58) and in
shipped code (the dead 0.82 release-commit the Slice D build deletes —
code, not plan). One coordinate, one name, everywhere. **Acquitted.**

**Band operative geometry (as amended).** (342 − 48) ÷ 4 = 294 ÷ 4 =
**73.5** ✓; cap = 73.5 − 8 (4dp inset × 2) = **65.5** ✓; DECK→LEDGER
travel = 3 steps × 73.5 = **220.5dp** ✓ (MASTER L2968–2969). The dead
pair also sums as labelled: 342 ÷ 4 = **85.5**, cap 77.5 ✓ — correctly
named no-PARK in MASTER L2971–2974 and tested against in the §6.8 check
L3250–3251. Demote pair 98/90 buried in both (Register L199–201; MASTER
L2950–2952). **Acquitted** (the Register's unstruck main-body line is
charged separately as BAND-1).

**Valve and token (Barriere's numbers, verbatim — condition 8 holds).**
Bar 112dp vs ≥88dp floor: 112 − 88 = **24dp** excess, as MASTER L1736–1737
states ✓. Banks 15/30/45 and pitches 560→470→390Hz: Register L117–119 =
MASTER §0 L51–53, §5.3.1 L1722–1725, bank table L1800–1806, acceptance
L2768–2769 ✓. Drain arithmetic verified: worst unbanked tail 14.9s →
0.8 grace + 3.0s @ 1.0× (drains 3.0) + (14.9 − 3.0) ÷ 2.0 = 5.95s @ 2.0× →
total 0.8 + 3.0 + 5.95 = **9.75 ≈ 9.7s** ✓ (Register L120–124; MASTER
L1828–1829 prints the same breakdown). Re-grip check: hand back 3.0s
post-slip has drained for 3.0 − 0.8 = 2.2s @ 1.0× = **2.2s lost** ✓
(MASTER L1827, L2763–2764). Token: 1/week, Monday 00:00 anchor, spend
written only at 60.0 held seconds — Register L126–131 = MASTER L1968–1972,
L1988 (`completeEmergencyBreak()`), A-12 L5013 ✓. **Acquitted, entire
family.**

**Spectacle ledger (the four, as a list).** Register L111–113 and MASTER
L541–544 are item-identical: slide commit 40 · final sub-step 50 · DONE
landing 40 · bank catch 40. Spends traced: final sub-step L1167 and
L1611; DONE L1238 ("Freeze 40ms — row-local (ledger #3)"); bank catch
L1797–1798 ("Register spectacle ledger #4"); a fifth candidate refused
by name at §8 L4637–4638. The list is honest; the single violation is
§4's operative hit-stop, charged as SPEC-1. **The ledger acquitted; §4's
spec text is not.**

**ARM wall and per-path figures (as numbers).** 10.0s wall and the
3.3 / 7.2 per-path worsts match between Register L19–26 and MASTER
L532–538 / L2573–2575 exactly. What is charged (ARM-1/2/3/4) is the
rival binding figure in §0.1-2, the missing derivation of 8.0, and the
dead machinery under 3.3 — not the figures' agreement. **Acquitted as
transcription.**

**1500ms Warrant fallback.** Register L61–65 ("survives in exactly one
place") = MASTER §8 L4106–4108 ("its 1500ms exists exactly once, there,
and nowhere else"). The §8 text honours the Register; §2.6's body does
not (charged in ARM-3). **Acquitted for §8.**

---

*— Dens, Critics (density/numbers), 29 Oct 2026. No code, no builds, no
plan edits: charges and acquittals only, each with file + line.*
