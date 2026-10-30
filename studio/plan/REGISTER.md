# REGISTER — every load-bearing constant, one value each

*Filed by Sana (Designers, coordinator) with §§4–9 of the master plan,
Oct 29, 2026. Una's fix, applied: MASTER-PLAN.md cited sections and
numbers that existed in several rival forms across the cohort files.
This register is the single home of every load-bearing constant and
coordinate in the plan. **No section of the plan is authority until
this register lands; where any plan file — including a filed section
of MASTER-PLAN.md itself — carries a different value for a registered
constant, this register's value governs and the other is a superseded
interim, named as such below.** A builder who finds a number in a
cohort file that is not in this register does not build from it until
it is registered.*

---

## ARM (standard 1)

- **ARM WALL: 10.0s** — cold open to sealed, every path, failures
  included. (The standard itself, §0.)
- **ARM WORST CASE: 8.0s** — the binding priced bound: Barriere's
  worst hand, one failed slide included (HUMANS-PLAN, HPA-23).
  Per-path pricing stands beneath it and does not rival it: Deck
  open → sealed ≈2.0–2.3s typical / ≈3.3s worst (BEYOND W5); plate
  (AOD) → sealed ≈3.1–3.5s typical / ≈7.2s worst (W5, one full failed
  drag included); tap, healthy: inside the press (W5).
- **Superseded:** ~~≈2.5s~~ (WILD-PLAN §10 — priced the strip as if it
  were the seal; dead in §0.1-2 already) · ~~≈6.1s~~ (Critics' merge
  condition 2, adopted in §0.1-2 — it priced the cold Deck path
  before W5's per-path accounting existed; §0.1-2's "stands in
  print" is corrected by this register, flagged to Ren, since §0.1
  is his adopted text).

## The slide (one event, one number, one name — §0 gate vocabulary)

- **GATE: 0.68** — zone boundary (FREE → DAMPED), haptic only. No
  colour changes at the gate, ever (F-ORANGE).
- **CATCH / COMMIT: 0.84** — displayed progress crossing 0.84 under
  contact commits. The D4 notches sit at this coordinate. There is
  exactly one commit coordinate in the app.
- **OFF-STOP: 0.65** — gate −0.03; the interlock's refusal position
  on positive evidence of death (BLOCK returns OFF). A refusal
  position, never a commit.
- **GATE RELEASE: 0.66** — the retreat's release coordinate: a
  retreat falling back below 0.66 releases to FREE (Marlowe's
  step-3 build, merged batch 4; Ren's ruling, Oct 29, via
  mail/takt/1150). Entered Oct 29 (Sana) — the checker warned
  for its absence until this entry (W-07).
- **DAMPED ZONE: 0.68–0.84** · **TRAVEL: 180dp** · **BRUSH FLOOR:
  90ms** (a press under 90ms commits nothing) · **TAP MESH: 140ms
  to 0.92, then the shared 110ms assist spring to the seat** ·
  **SEAT SPRING: Cato's (400/16), 110ms** · **LATCH KNOCK: 14ms @
  0.85 at 0.97 of travel**.
- **Superseded coordinates, with provenance:** ~~0.80~~ — Sana's D4
  notch coordinate, registered premature; F-GATE rules it follows
  the winner to 0.84 (BEYOND-deck, F-GATE) · ~~0.82~~ — Designers'
  §11-R5 commit (warn 0.68 · catch ~~0.80~~ · commit ~~0.82~~), superseded
  by W4/F-GATE's single catch · ~~0.65-as-commit~~ — Humans' HPA-24
  ("one gate: ~~0.65~~, magnet-complete from 0.40"), superseded by
  F-GATE; its intent (one number the hand learns) is honoured
  structurally — the hand learns 0.84; 0.68 is felt, never a
  decision; 0.65 is a refusal, never a commit. Zug-II's ~~0.80 load
  zone / 0.82 felt gate~~ (Improvers wave) are the same dead
  coordinates in a second file.
- **VERDICT (Live Wire): synchronous, ≤5ms, at DOWN and at the
  gate** (Ines, MaxImpl invention wave — adopted). The 1500ms
  deadline, the Park, and the UNKNOWN class of W4-as-merged are
  DEAD with the waiting machinery; the 1500ms figure survives in
  exactly one place: the Warrant fallback, if Live Wire's
  residency facts fail on device, where it exists exactly once.
- **t=0:** the first vsync frame the controller executes PROVING →
  SEALED-RUNNING: `t0 = max(last link CONFIRMED-SHUT, latch +
  210ms, pawl-seated frame)` (§2.6; merge condition 1, closed).

## The wheel and hub

- **WHEEL: Ø288dp (R144)** · **SCALE: 9° = 5 min** · **STOPS:
  FLOW + 10–180 in 5-min steps** · **STRAIT: 18°** (FLOW landing
  step + phantom step; no tick, tooth or numeral inside it) ·
  **DEAD SECTOR: 36°** · **TRAVEL FLOW→180: 324°**.
- **STRAIT GATE (coast): <180°/s falls back to its shore;
  ≥180°/s crosses paying Δω = −90°/s** at the centreline; a held
  finger always crosses 1:1 · **FLOW WELL: ±6° capture, seat
  ≤520ms**.
- **LOADED WHEEL: I_rel(n) = 1 + 0.14·(n−1)** (n=1 → 1.00, n=8 →
  1.98) · **VISCOUS τ: 0.34s → 0.48s** (τ(n) = 0.34·√I_rel) ·
  **SEAT: exactly two overshoots, ≤520ms, every count** · **FLING
  DEADLINE: ≤1,400ms, every count** · **SEAT KNOCK: 16ms @ 0.85
  (n=1) → 24ms @ 1.0 (n=8), linear; the only feel that scales** ·
  **FLOW LOAD: zero (I_rel = 1)**.
- **HUB: Ø128** · **WELLS: 8, Ø10dp, on R40** · **COUNT CAP: 8**
  (engine coercion sites only; no migration) · **CLOSURE BAR:
  R40, 10dp wide, 160ms ink-settle from 88%** · **SPECULAR: swept
  form** (`specularSwept(θ₀, θ₁)`, 720-entry LUTs + prefix sums,
  sinc attenuation |sin(πΔ/p)/(πΔ/p)|, **p = 3°**; Dario's two
  amendments bind: Δ over *drawn* frames, sinc replaces dual
  sub-draws) · **BROKEN FLARE: per-tooth variance 0.84–1.16,
  compile-time only, teeth 17/83 hand-set 0.62/0.68, no runtime
  writer, ever**.

## The session

- **TRUE SPAN: n × block + (n−1) × 10** — the only span formula in
  the app (hero, FINISHES, receipt, Now Frame).
- **SAFE BREAK: 600s, between blocks only** — never after the last
  block.
- **NOW FRAME: one pure `derive()`** — verified elapsed,
  penalty-aware remaining, block/break state, finishes-at, window
  state, weekId; every surface consumes it; the publisher sleeps
  to the next change instant (the Deadline, not the Ticker).
- **SEAL CADENCE: 210ms** (Orson's two entries, 90 + 120) ·
  **HELD FRAME: 1200ms** · **RECEIPT DETENTION: ≤1.8s** (Release
  Proof) · **SLED GOES HOME: receipt +1000ms, powered return over
  650ms** · **COMPLETION PULSE: reserved for verified
  fully-elapsed sessions only**.
- **SPECTACLE: exactly 4 freezes** — slide commit 40ms · final
  sub-step 50ms · DONE landing 40ms · bank catch 40ms. A fifth
  requires naming which dies.

## The valve and the token (Barriere's numbers, verbatim — condition 8)

- **BAR: full-width × 112dp** (touch floor for the valve: ≥88dp —
  the bar exceeds it) · **BANKS: 15 / 30 / 45** (pitch falls 560 →
  470 → 390Hz) · **FULL HOLD: 60.0 held seconds**.
- **DRAIN: 800ms grace (per-slip, uncapped — grace never accrues)
  → 1.0× for the first 3.0s of drain → 2.0× thereafter, easing at
  the floor** · **FLOOR: the last bank; banked seconds never drain
  in-session** · worst tail (≈14.9s unbanked) reaches the floor in
  ≈9.7s. (Designers' §7.2 flat 2:1 and Critics' reversed citation
  are both superseded — Barriere's file is the source.)
- **TOKEN: exactly 1 per week** · **WEEK ANCHOR: Monday 00:00
  local** (`MonolithWeek` — one definition, consumed by the token
  and any weekly rendering) · **SPEND: written only at 60.0 held
  seconds, in the same transaction as the disarm** · attempts,
  banks, slips and abandons write nothing · **ENFORCEMENT:
  repository `completeEmergencyBreak()`**; the valve view receives
  no count (Ledger-Direct); at 0 the valve is not composed — a
  SPENT plate renders instead.

## Data (migration arbitration — Voss, binding)

- **7→8: TOKEN-SPEC's alone, CLOSED** — `emergency_break_spends`
  table + `fallbackToDestructiveMigration(true)` DELETED in the
  same change. Nothing else rides.
- **8→9: routines rebuild** — Sten's 13 ADD COLUMNs +
  `startedAtSessionElapsedSec` (focus_tasks) + `taskId`
  (focus_sessions). `autoPausedAtMillis` is formally NOT created.
- **9→10: plumbing batch** — Rurik's step-rows table +
  completion-events table + Otis's readiness log. Append/child
  tables only.
- **Task #0 (the DRAFT row): no migration** (String status column).

## Chrome and room

- **BAND: DECK · SHIELD · TASKS · LEDGER + PARK** — four stops +
  the utility end-detent. **PARK: 44dp zone + 4dp notch; flat 48dp
  cost under every variant; arrival IS invocation; two rest
  heights.** ~~Operative geometry = Designers' V-TAB figures
  (segment 85.5dp, cap 77.5dp), which the filed plan priced for
  exactly this branch.~~ **[STRUCK Oct 29 (Sana, per Massarbeit's
  checker correction): those are the no-PARK figures. Operative
  geometry = MASTER §6.4 as amended — segment 73.5dp, cap
  65.5dp, PARK flat 48dp; see the amendment at the foot of this
  Register.]**
- **GUARD LINE: `GUARD 4/4`** — words, in chrome; a lack names the
  first lack (~~`GUARD 3/4 — ACCESSIBILITY OFF`~~ — **[STRUCK
  Oct 29 (Sana): OFF is an interceptor word and never prints for
  a permission (chair ruling, Oct 29, 08:58 #6). A MISSING
  permission prints its bare name — `GUARD 3/4 — ACCESSIBILITY`;
  a STALE one appends STALE.]**). Denominator = 4,
  board-defined (the n/4 charge closes with the Guard ruling).
- **WATCH STORE states: GRANTED / MISSING / STALE** (permissions)
  · **RUNNING / PAUSED / OFF** (interceptors). One source; no
  surface runs its own check.
- **ROOM: fixed sum ~~796dp~~; reserve = ~~H − 796~~, floor 20dp; under
  ~~H=820~~ the build returns to the coordinators** — the wheel never
  shrinks, the strip never folds. **[AMENDED Oct 29 (Sana, per
  Massarbeit's checker correction): the fixed sum is 821dp and
  reserve = H − 821 govern — the amendment sits at L431–436 of
  this Register (H = 841); the 796 figures above are struck in
  effect and kept as the record of the superseded sum.]**
- **RADIUS: 8dp keys / 16dp panels** (frozen) · **MARGIN: 20dp**
  (frozen) · **TOUCH FLOORS: 48dp everywhere; valve ≥88dp**.

## Type and print

- **TYPE LADDER: 104 / 72 / 50 / 22 / 15 / 10** — Sora display,
  Atkinson Hyperlegible body, Anonymous Pro plate (10sp +2.0
  tracking). Off-ladder sizes are build errors.
- **DRAWER PAGING: 50** (`EARLIER` loads the preceding 50) ·
  **DRAWER SEARCH: all stored tasks, all years** · **AOD:
  REMAINING `H:MM` at 104sp; patrol 4×3 anchors; dimming luminance
  0.215 → 0.174 → 0.130, ceiling 0.25**.

*— Sana, Designers coordinator. The register is part of the plan of
record from this filing; amendments enter by a dated line here,
never by editing a number in place in a cohort file.*

---

## AMENDMENTS — 29 Oct 2026, at the §§4–9 merge (Sana)

*Every line below amends the register above by dated line, per this
file's own law: numbers are never edited in place silently. Each
amendment names what it supersedes. Where an audit disagreed with
the register and lost, the loser is recorded, not erased.*

### Band geometry — CORRECTED

- **Segment 73.5dp · cap 65.5dp** governs the band (§6.4). The draft
  above's "segment 85.5dp, cap 77.5dp … priced for exactly this
  branch" was wrong: Razor and Raster independently proved that pair
  is the **no-PARK** V-TAB geometry (342 ÷ 4); with PARK's flat 48dp
  spent first, the with-PARK pair is (342 − 48) ÷ 4 = **73.5 / 65.5**.
  Step travel 73.5dp; DECK→LEDGER 220.5dp; PARK flat 48dp beyond.
- **Buried by name:** the demote-branch geometry **segment 98dp /
  cap 90dp** (DESIGNERS §8.3) is superseded entire and carries into
  no variant.
- **Rail height: 52dp** is registered (DESIGNERS §8.3 + §11-R17),
  with Raster/Kato's **64dp** contest on record, tied to the room
  decomposition — §9, charge B-51. §6 builds at 52 until this line
  is amended again.
- **Touch floor, restated as a touch-AREA law:** every control's
  effective touch area is ≥48dp in both dimensions; a visual
  rendering smaller than 48dp is lawful only under a touch area that
  meets the floor (the band cap's 44dp visual under its segment×52dp
  target is the type case, §11-R17's split). "48dp everywhere" in
  the draft above means touch area, never visual size.

### Guard — the board's member set, and STALE's print

- **Counted four (the denominator): ACCESSIBILITY · USAGE ACCESS ·
  DEVICE ADMIN · NOTIFICATION LISTENER** — verified against the
  shipped readiness count (`SystemGuardStatus`), Teen's invention-
  wave self-correction E6 ("BATTERY was wrong"), and Uhr's Watch
  Store file. Ruled at this merge (§8.5); closes Dieter-II B-3 on
  ratification.
- **Uncounted board lines:** `OVERLAY` (queried in shipped code;
  the airlock windows need it; the seal does not depend on it) and
  `BATTERY` (the optimisation-exemption grant; not separately
  counted in the shipped status; its failure mode is already Watch
  Store evidence under the death law). Both take board lines and
  FIX eligibility; neither moves the numerator; chrome's first-lack
  naming runs over the counted four only.
- **Interceptor lines:** `SHORTS + REELS · SOCIAL SITES ·
  NOTIFICATIONS`, state words RUNNING / PAUSED / OFF with §8.5's
  effect semantics. The Shield service's own state feeds the strip's
  BLOCK cell and never enters the denominator.
- **STALE's chrome form:** the state word rides the name —
  `GUARD 3/4 — ACCESSIBILITY STALE` (§6.2). This amends the draft
  above's example form (`… — ACCESSIBILITY OFF`): `OFF` is an
  interceptor word and never prints for a permission. Closes
  Dieter-II B-32 on ratification.
- **STALE's predicate (registered):** no PASS row younger than
  **45s** → STALE, derived at render from timestamps, one threshold
  on every surface; printed ages ceiling-rounded to the next 5s,
  never flattering (§8.4). Vau's 15,000ms board warrant and 5,000ms
  foreground sweep cadence are superseded — the poll engine is dead
  (§8.3).

### Wheel — frame, regimes, and the two 540s

- **Vega's W2 frame governs §2 entire** (9° = 5 min · FLOW + 10–180
  · dead sector 36° · FLOW→180 travel 324°). Uhr's first-paragraph
  scale (10° = 5 min · stops 5–180 · 10° dead wedge) is superseded;
  the pair dies together (§9, C-10).
- **Two seat regimes, named so they stop reading as rivals:** the
  **coast/fling capture** seat is Vega's spring — exactly two
  overshoots, ≤520ms (the LOADED WHEEL entry above) · the
  **drag/programmatic** seat is Kin's **SNAP-WHEEL (280 · 0.78,
  overshoot ≤2°, one pass, settle ≤380ms)**. Each governs its own
  regime; neither is a rival value for the other.
- **Fling decay: 2.4/s**, ω₀ clamped **±540°/s** — a *different* 540
  from Vega's well-capture velocity; both are registered, labelled,
  and may not be conflated.
- **Hub count-commit: on release** (§2.4 / DESIGNERS §3.2). Kin
  M-04's commit-at-the-bottom-of-the-dip is superseded; his grammar
  records the dissent (§9, C-12).
- **Closure bar: 160ms ink-settle from 88% STANDS**, source now
  cited in full: `plan/beyond-deck-parts/vega.md`, line 90 — "bar
  lands at 88% ink and settles to 100% over 160ms" (Kin's audit
  found the Deck source Raster and Iris missed). The earlier
  suspicion that 88% lacked a source is withdrawn.

### Specular — Dario's Amendment 2 governs

- **Dual sub-draws are DEAD.** The sinc is a **frame-global
  multiplier** (Dario Amendment 2); Δ is measured over *drawn*
  frames. The LUTs are **two 720-entry tables** (t³ broad, t⁸
  machined, 0.5° steps) with **721-entry prefix sums** — the draft
  above's "720-entry LUTs + prefix sums" is amended to match.
- **The t⁸ machined peak attenuates to 0.968 at Δ = 18°** (Dario's
  precision; 0.988 is superseded).
- **Base/peak forms (Mara):** face `0.16 + 0.66·t³`; machined rim
  `base + peak·t⁸`, peak +15%. **Caps:** peak lift ≤14% day / ≤8%
  night over cap fills; non-accent fills never lighter than
  `#4A423C` at night; specular never renders over text; ≤35%
  saturation at peak. Light vector θ_L ≈ 245.2°.
- **The OTHER day peak, registered separately:** the hub-cap core —
  azimuth 315°, core ±9°, cos² falloff to zero at ±34°, lift **10%
  day / 8% night**. This is a *different constant* from the 14%
  tooth peak above; both are registered so no builder spends one on
  both surfaces.
- **Mara's bounded linear form:** `bandOffset = clamp(−0.35 ×
  localTravelPx, −8dp, +8dp)`, zero at every seat.
- **MechanicalOvershoot STANDS** for Shield sled seats (§3 as filed
  builds three controls on it). Kin's by-name deletion of it is
  recorded as his grammar's dissent, not adopted.

### Colour — the register now carries hex (Iris's audit, H1–H15)

*The draft above carried zero hex values; under its own rule no
colour could be built from it. The locked tokens are registered
here, from §1.3 and the cohort files Iris audited.*

- **Night staircase:** ground `#171412` · recess `#100E0C` ·
  raised `#362C27` · cap `#4A423C` · text `#D2C6BB` · text-2
  `#9C8E84` · muted `#776A60` · glyph `#EDE3D8` · cream `#FFE4CF`
  (text/glyph only, never a fill).
- **Day staircase:** recess `#D8D1CB` · ground `#E4DFDA` · raised
  `#FAF6F0` · ink `#252323` · text `#2C2A29` · muted `#5F5856`.
- **Accents:** ACCENT day `#F55314` / night `#F47442`; ON-ACCENT
  `#471602` both modes. **Accent budget per screen:** Deck 1 ·
  Tasks 1 · Shield 0 · running 0 · AOD 0 · overlays 0 · chrome 0.
  Orange is a COMMIT fill only.
- **`#FF8A5C` is DEAD everywhere — denied, not dormant.** It was the
  filed BREAK SPENT exception tone; Ren's R13 review refused the
  orange-exception family ("no orange in chrome, no exceptions"),
  Iris (the exception's own author) records the HOLD SPENT exception
  as denied and staying denied, and the tone independently breaches
  the AOD luminance ceiling at **lum 0.402** (ceiling 0.25). BREAK
  SPENT prints word-only in glyph ink, both modes, ≤2% area, never
  on AOD (§5.3.7). Registered here so no builder reading §1.3
  resurrects it in good faith.
- **Cap marker-line colour: cream `#FFE4CF` @ 90%** (Mara's filed
  form, "nothing else") — **provisional, flagged for ratification**
  (Vau's day `#FAF6F0` @90% / night ink `#252323` @90% and Joss's
  glyph tone are the losing candidates, on record).
- **Knurl A/B tooth alternation:** day `#FAF6F0` / `#CFC7C0` ·
  night `#4A423C` / `#241E1A`.
- **AOD stage tones** are registered as filed in §5.8.5, source
  cited as **DESIGNERS §6.2 / nacht.md §1.3** (provenance corrected:
  not MASTER §§2–3).
- **Deck print alphas:** D4 notches `#000000` @ 35% · D8 skirt 25%
  day / 35% night · `SLIDE TO ARM` @ 55% · hero RE-SET 55% register,
  strike density 86% → 100% over 120ms.
- **Shield/chrome:** sled fill staircase per §3.7 · loadout marker
  `#FBF1EC` both modes · search-field line pair at α **0x2E ≈
  18.0%** · BLOCKBAR day `#2E2A26` / night `#161311` · Nacht's night
  ratio laws as filed.
- **Hairline notation trap, filed:** the byte **0x75 ≈ 46%**, not
  75%. Any hairline registered by percentage alone is ambiguous;
  file the byte or do not file it.
- **Scrim: GROUND at ~80% alpha, both modes.**
- **`#758F88` — the record, whole:** shipped code's night
  outlineVariant. Channels R117 **G143** B136 (G−R = +26 — the only
  G-dominant hex in scope; Cato-II's arithmetic, Iris's audit).
  **Ren verified the token personally (29 Oct 2026, 07:27 IST) and
  ruled it a warm grey — no swap.** Cato-II's replacement nomination
  `#8F8375` (warm-ordered 143/131/117) stands as filed dissent. The
  ruling is not re-opened by this register; the build charge is
  §9's B-20.

### Motion — the registries (Kin's audit, H-01…H-34, folded in)

- **Curve registry:** THROW (0.20, 0, 0, 1.00) · SEAT (0.22, 0.90,
  0.28, 1.00) · PRESS (0.20, 0, 0.30, 1.00) · LINEAR-MEASURED,
  plus the one-off beziers as filed per element.
- **Spring registry:** SNAP-WHEEL 280 · 0.78 · THROW-SPRING 520 ·
  0.90 · RETURN-CRITICAL 300 · 1.00 · PRESS-BACK 800 · 0.65 ·
  BANK-RETURN 110 · 0.85.
- **ARRIVAL, defined:** the first frame within max(1% of travel,
  0.5dp). **DOWN-response ≤16ms.** Overshoot budget ≤3% of travel
  or 2dp, whichever binds. Scale floor 0.94.
- **Gate-to-session ≤280ms cap**, with the dwell note: the 40ms
  commit freeze and the ≥120ms dwell are two moments, not one —
  both are registered and neither absorbs the other.
- **Panel Travel Law** as filed (Kin): panels travel on SEAT/THROW
  only; no panel invents a third curve.
- **AOD timing set:** stage boundaries 20 / 50 min · 2s crossfade ·
  finish = one 60s step · task-line transients 60s / 10s · 2.5s
  wake brightness ramp.
- **Band formula: 180 + 60 × stops** (ms). Kin's ≤340ms three-stop
  cap is superseded by the formula at four stops.
- **Sled settle family, key timings (PRESS dip 80ms house, legal
  70–90), drawer timings (280 SEAT open / 160 THROW close), overlay
  speed classes, hero RE-SET timings** — as filed in Kin's audit
  and the merged sections that own them (§§3–8); the audit's
  numbers govern where a section prints none.
- **The second 650ms, disambiguated:** Shield's BANK-RETURN return
  (650ms) and the Deck's SLED GOES HOME (receipt +1000ms, 650ms
  powered return, registered above) are two different constants
  that share a duration. Neither cites the other.

### Geometry — the missed constants (Raster's audit, M1–M20)

- **Content grid:** 372dp wide · 4 columns × 84dp on S12 · axes and
  the start beat at **y = 112** as filed.
- **Slide:** track 72dp · cap 84 × 56dp · D4 notches at the catch
  coordinate · refusal slot as filed (§2.6).
- **The strip's room cost: 64dp** (Raster's accounting; carried into
  B-51's decomposition audit).
- **Operative sled geometry (§3.7, Yuki):** track 168 × 36dp ·
  radius **12dp** · pad 3dp · slot 54dp · cap 54 × 30dp. The radius
  12dp is **slot geometry** and is the radius freeze's one named
  exception: the freeze (8 keys / 16 panels) governs keys and
  panels; a sled track's radius is neither.
- **Wheel print geometry** as filed (Raster M-series): tick/numeral
  placement at full opacity — the DESIGNERS §3.4 percentage form
  (ticks/numerals at 35/85/55%) is **named dead** (Iris D9).
- **Knurl:** 120 teeth · 3.000° pitch · tooth 8.0 × 2.4dp.
- **Hub press geometry, AOD patrol offsets** (4×3 anchors, offsets
  as filed in §5.8) — as filed.
- **Running arc:** Ø280dp · 270° sweep · 10dp stroke · 90° gap
  (§5.4). **Valve rest slot: 200 × 48dp**, nested in the arc's gap,
  with a 12dp drift tolerance (§5.4; the Barriere full-width bar
  governs the valve's *working* form, §5.3 — the slot is its rest
  rendering only).

### Room — the centre is struck

- The **796dp headline stands** (two sources file it) and the
  reserve law stands. But the filed §2.2 stack does not
  arithmetically produce 796 on any single reading, and the printed
  wheel centre (206, 404) contradicts the stack's own S20 charge.
  **The centre is struck as a constant** — it is derived from the
  stack, never fixed. The decomposition and the rail height close
  together at the merge audit (§9, charge B-51).

*— Amendments entered by Sana, Designers coordinator, 29 Oct 2026,
from the register audits of Raster (geometry/room), Kin
(motion/time) and Iris (colour/light), and the §§4–9 merge. The
register, as amended, is the plan's authority for every constant it
carries.*

---

## SETTLEMENT AMENDMENTS — 29 Oct 2026, the plan-audit sweep (Sana)

*Entered on the Critics' plan audit (plan/PLAN-AUDIT.md, PA-S1-03 /
PA-S1-04) and the rulings cited per line. The audit's finding:
settlements were announced faster than the documents were amended.
Each line below strikes an earlier line — main body or amendment —
that still printed the losing value as law. Per this register's own
law, the strike enters by dated line; the struck text stays above
as the record of what lost.*

- **ROOM: 821 governs.** The main body's "ROOM: fixed sum 796dp;
  reserve = H − 796 … under H=820 the build returns" is STRUCK.
  Corrected sum **821dp**; reserve = H − 821, floor 20dp; lawful
  minimum room **H = 841** (Kato's recount, SLICE-D-E §3-R4;
  adopted by Ren, F19+F20, Oct 29 09:05). B-51a CLOSED on Spatz's
  arithmetic: 796 is dead. §2.2 is amended to match.
- **TRAVEL: fractions of actual travel.** The slide entry's
  "TRAVEL: 180dp" as the travel constant is STRUCK. Travel is
  fractions of the view's actual travel (maxTravel = width − 88dp
  ≈ 248dp); **180dp stands as the FLOOR only** (F1, ruled by Ren,
  Oct 29 09:05).
- **SPECULAR — shipped status corrected.** The wheel entry's
  "SPECULAR: swept form (`specularSwept(θ₀, θ₁)`, 720-entry LUTs +
  prefix sums…)" is STRUCK as a shipped claim. What the tree
  carries is Mara's REST-STATE form (0.16 + 0.66·t³, fixed lamp,
  Broken Flare table); `specularSwept` and the LUTs are unbuilt
  spec, landing only if the rim itself rotates (Ren's correction,
  Oct 29 09:05, via Marlowe §3-R9). The Dario Amendment 2 entry
  above still governs the swept form's spec — dual sub-draws dead,
  sinc frame-global, Δ over drawn frames — for if it is ever built.
- **BAND: 73.5 / 65.5 operative in the main body's place.** The
  main body's "Operative geometry = Designers' V-TAB figures
  (segment 85.5dp, cap 77.5dp)" is STRUCK: **segment 73.5dp ·
  cap 65.5dp** (§6.4; the Band-geometry amendment above) is the
  operative geometry; 85.5 / 77.5 is the no-PARK pair and governs
  nothing.
- **MARKER CREAM: RATIFIED.** The colour amendment's "Cap
  marker-line colour: cream `#FFE4CF` @ 90% … provisional, flagged
  for ratification" is settled: **`#FFE4CF` @ 90% is RATIFIED**
  (Ren, Oct 29 08:58, item 10 of the merge ratifications). Vau's
  and Joss's candidates stay on record as losers. The colour now
  also appears in MASTER-PLAN §0's verdicts and §9 (0858 #10).
- **B-12: CLOSED.** Death-booked sessions take no completion
  pulse — closed-by-merge and RATIFIED (Ren, Oct 29 08:58,
  item 5). §9's B-12 entry and §5.7's wording are amended to match.
- **F-2: THE MAIN BODY CARRIES WINNERS ONLY.** The main body's
  remaining unstruck printings of dead values are struck in place
  (registry merge, Oct 29, on Ren's approval; Fundus registration
  F-2, OPEN-LOOPS L30 — W-07's life inside this register ends):
  the ARM superseded figures (~~≈2.5s~~ · ~~≈6.1s~~); the slide's
  superseded coordinates (~~0.80~~ · ~~0.82~~ · ~~0.65-as-commit~~,
  including Zug-II's restatement); the Chrome-and-room section's
  superseded room sum (~~796dp~~ / ~~H=820~~ — its [AMENDED] note
  already named 821 as governing). The BAND and GUARD entries
  already carried their losers struck in place (Sana's strikes,
  main body) and are unchanged. Per this register's own law the
  struck text stays above as the record of what lost; nothing
  unstruck in the main body is now a loser.

*— Settlement amendments entered by Sana, Designers coordinator,
29 Oct 2026, on Hale's plan audit and Ren's ratifications.*
