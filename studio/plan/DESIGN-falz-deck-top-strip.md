# DESIGN — The Deck Top Strip as a Physical Instrument
## FALZ (Designer, dedicated-100) · issue #4 · for ANTLITZ, cell lead

**My one job:** the Deck's top strip. Not the header band (host chrome,
HEADER-BAND-AUDIT's ground), not the slide, not the hero. The pinned
band that used to print:

```
BLOCK OFF · APPS 89 · TIME ENDS 07:28
BIND NOT HELD · PROOF: POSITIVE
```

**Relation to the design of record:** THE BOLT
(`DESIGN-issue-4-the-bolt.md`, built as `SealBar.kt`) is the right
instrument and this pass does not replace it. This file is the
designer's sharpening pass on it: the reading choreography made
explicit (§2), one anatomy addition — the engraved detent scale
(§3.2) — rulings closing the Bolt's open points (§5), and a
coordinate table exact enough that a finisher can render the five
acceptance frames without guessing (§4). Where this file and the
Bolt disagree, this file says so and signs it; Antlitz merges.

---

## 1 · The two-question law

The strip answers exactly two questions, in this order:

1. **Is it holding?** — answered by bolt pose + jewel, no words.
2. **When does it end?** — answered by one number in the glass
   window, only when a live timed end exists.

Every element on the face must answer one of these or leave:

| Element | Answers | Verdict |
|---|---|---|
| Bolt pose (OPEN / CAUGHT ⅓ / THROWN) | Q1 | KEEP — the primary signal |
| Exposed warning bed `#D63E06` | Q1 (OFF) | KEEP — OFF's interruptive mass |
| Jewel (dark / brass / magenta) | Q1 | KEEP — ≤9dp, in 17dp bezel |
| Glass window `HH:mm` + engraved `END` | Q2 | KEEP — blank when no live end |
| App count (`APPS 89`) | neither | EVICTED — homes: Shield header + split LOCKED/PEEK imprint |
| `BIND` / `PROOF` / `POSITIVE` stamps | neither (studio dialect) | DELETED, never moved to another face |
| `BLOCK` / `TIME` headers | neither (labels for the above) | DELETED — pose and glass need no captions |

**Why the count stays exiled (designer's reason, not just the
audit's):** a count is inventory, and inventory is read in a room
where you act on it (Shield). On the strip it competed with Q1 at
equal volume and answered a question nobody asks at arm time. The
strip is a gauge, not a manifest.

---

## 2 · The glance choreography — the reading order, specified

State must land in a fixed perceptual order. A frame that can only
be read in a different order is a failed frame.

| Beat | Time | Channel | What must land | Carrier |
|---|---|---|---|---|
| 1 | ~0.2s | peripheral, no aim | **OFF-ness or its absence** | Exposed orange bed area: OFF exposes the full bolt footprint (90×36dp of matte `#D63E06` + painted socket); THROWN exposes 0dp². Area, never light — a colour patch is visible in peripheral vision, a 9dp jewel is not |
| 2 | ~0.5s | first fixation | **Bolt position** | Bolt against the engraved detent scale (§3.2): left mark / between marks / right mark. Three buckets, no finer read is asked or offered |
| 3 | ~1s | second fixation | **Proof grade + end** | Jewel tone (dark = unproven, brass = proven idle, magenta = proven running) and, if present, `HH:mm` in the window |

Rules that protect the choreography:

- **Beat 1 must survive greyscale and 10% brightness.** The bed is
  matte paint at a fixed value, mode-invariant; the jewel budget
  from the Bolt §6 gate (luminous total ≤41.9 proxy units, OFF's
  bed ≈125.7, ratio ≥3×) is adopted unchanged as the ship gate.
- **Nothing may compete with beat 1.** No lit element on an OFF
  face; no orange anywhere else on the Deck at strip height; the
  bed is the only large saturated mass in the pinned stack.
- **ENDING_SOON enters at beat 1, not beat 3.** Its carrier is the
  whole window flipping to the brass plate (`#E2BC39` plate,
  `#252323` numerals, 8.55:1, mode-invariant) — a new bright mass
  in the lit cluster, readable peripherally. The jewel stepping
  DOWN to brass is beat-3 confirmation, never the alarm itself.
- **CAUGHT is a beat-2 read only, and that is enough:** the law is
  the binary bucket — ≥95% of cold reads sort it "not protected"
  (adopted from the Bolt §6). It needs no beat-1 carrier; giving it
  one (e.g. a partial bed exposure) would blur OFF's unique mass —
  at CAUGHT ⅓ the bed's exposed third reads as *the bolt's own
  shadow side*, not as the alarm. This is deliberate: OFF is the
  only state that can hurt him unattended, so OFF alone owns
  beat 1.

---

## 3 · Anatomy — the frozen grid (360dp reference width)

Housing 60dp tall, radius 0, hairlines 1dp top and bottom, 12dp
horizontal inset. Height never changes in any state; all state
change is offset, fill, or light inside the housing.

```
 x: 0        12   24        channel (flexible)         222  234      246        348 360
     |---------|----|===================================|----|--------|------------|---|
 y=0  ────────────────────────────── 1dp hairline ──────────────────────────────────
 y=6–11        locking pin (4dp wide, above thrown notch only)
 y=12–48 36dp  [ BOLT 90×34 ]══════ channel ══════►  ◉ jewel  [ GLASS 102×36 ]
               detent ticks ▲ at 0 · ⅓ · 1 (floor, §3.2)  17dp bezel   HH:mm 20sp
 y=48–53       `END` engraved 9sp on window bezel, below glass, right-aligned
 y=59  ────────────────────────────── 1dp hairline ──────────────────────────────────
```

Fixed vs flexible, the whole scaling law:

- **Fixed, never scaled by width or font scale:** bolt 90×34dp ·
  channel height 36dp · jewel ⌀≤9dp in ⌀17dp bezel (halo ⌀21dp,
  RUNNING only) · window 102×36dp · pin 4dp · ticks 1dp.
- **Flexible:** the channel only. `channelW = housingW − 156dp`
  (window 102 + jewel cluster 30 + insets/gaps 24). Every dp of
  growth on wider phones goes to travel; detents stay fractions of
  travel (0 / ⅓ / 1), so the poses are identical at 360dp and
  412dp — only the distance between them grows.
- **Type:** numerals Anonymous Pro Bold 20sp, 24h `HH:mm`, inside
  glass only. `END` Atkinson Hyperlegible Regular 9sp engraved on
  the bezel *below* the glass — a caption machined into the case,
  never print inside the display. Housing carries no other text.
  No Sora on this face: the strip has no display moment; giving it
  one would promote a gauge into a headline.

### 3.1 The bolt

Slides in the channel; not a handle (no grip affordance, no drag,
no ripple — it moves by itself or not at all). Three detents:
OPEN (0), CAUGHT (exactly ⅓ — never ½; half reads as a resting
state and this instrument has exactly two), THROWN (1, bolt
overlaps keeper collar by 4dp, locking pin drops into the bolt's
notch — the notch is only under the pin when thrown, so the pin
*geometrically cannot* drop in any other pose).

### 3.2 NEW — the engraved detent scale (FALZ addition)

Three ticks machined into the channel floor, at the bolt's leading
edge positions for 0, ⅓, 1: each 1dp wide × 8dp tall, sitting on
the channel's bottom edge, tone = one step darker than the floor
(day and night tokens, never a new colour).

- **Why:** today the bolt floats in an empty channel; CAUGHT at ⅓
  is only legible relative to the channel's ends, which the eye
  must find and bisect. A scale turns position into *position
  against marks* — the difference between a needle gauge and a
  slider in a slot. This is how physical instruments are read.
- **What it is not:** not numbers, not labels, not a progress bar.
  Ticks do not fill, accumulate, or highlight with state; the bolt
  passing a tick is the only event. If a tick ever lights, this
  addition has become telemetry and should be reverted.
- **Cost:** three static boxes in the existing channel Box; zero
  state plumbing. Priced S. If renders show the ticks reading as
  decoration at 1.8 arcmin blur, cut the middle tick only — the
  two end marks still anchor OPEN vs THROWN.

### 3.3 The jewel

Dark lens for every unproven / off / fault face — health is the
only thing that lights, fault never lights (adopted, TON's law).
BRASS `#E2BC39` = proven idle and the ENDING_SOON step-down.
MAGENTA = proven running (day `#C81FA8` / night `#F04FCB`, the
house split; brass is one value both modes — metal does not
change at dusk). RUNNING adds the 1dp halo ring in jewel tone:
hue alone at 9dp failed the blur test for ARMED-vs-RUNNING; the
ring makes the two lit states different *sizes*, which blur
preserves.

### 3.4 The window

Dark glass both modes (day glass `#2E2A26` / numerals `#FAF6F0`;
night glass `#161311` / numerals `#FFE4CF`). Blank — truly empty
glass — whenever no session has sealed a live timed end: OFF,
UNPROVEN, BLOCKING-idle, FLOW, NO_READ. No `--:--` placeholders,
no planned ends printed as facts (the old strip's sliding idle
ENDS lie dies here). FLOW prints nothing: no end exists, so the
glass is honestly empty, and the magenta jewel carries FLOW's
running read alone.

---

## 4 · The five acceptance frames — coordinate spec

Reference: 360dp wide housing, channel from x=24 to x=222
(198dp), bolt travel 108dp. Bolt leading-edge x at detents:
OPEN 114 · CAUGHT 150 · THROWN 222. Ticks at x=114 / 150 / 222
(leading-edge marks on the floor). Jewel centre x≈240. Window
x=246–348. All frames hand-built SVG/Canvas from these numbers —
no generated imagery.

| # | Frame | Bolt x (trailing) | Bed exposed | Pin | Jewel | Window |
|---|---|---|---|---|---|---|
| F1 | OFF, day | 24 (OPEN) | full 90×36 + socket painted | up | dark lens | blank glass |
| F2 | OFF, night | 24 (OPEN) | full 90×36 + socket painted | up | dark lens | blank glass |
| F3 | CAUGHT (unproven), day | 60 (⅓) | right ⅔ of bed exposed* | up | dark lens | blank glass |
| F4 | THROWN idle (BLOCKING), night | 132 (THROWN) | 0 | dropped | BRASS + no halo | blank glass |
| F5 | RUNNING + ENDING_SOON pair, night | 132 (THROWN) | 0 | dropped | MAGENTA + halo → step-down BRASS | `HH:mm` cream on dark glass → inverted: dark on brass plate |

*F3 note: the bed sits at the shut end, so a caught bolt exposes
its right portion. Per §2 this must NOT read as the OFF alarm at
beat 1 — the acceptance test is paired: F3 beside F1, greyscale,
10% brightness; if cold readers call F3 "off" at beat 1 that is a
PASS (the binary bucket is "not protected"), and if they call F1
"caught" the bed has failed.

**The acceptance pair is F2/F5-night, side by side** (adopted
screenshot law): a lone RUNNING frame proves nothing; OFF is the
state that can hurt him and the contrast is the proof. Greyscale
twins of both are part of the same deliverable frame.

---

## 5 · Rulings closing the Bolt's open points (FALZ, for Antlitz)

1. **Day glass — CLOSED.** DUNKEL's extension (night glass logic
   applied to day) is adopted as final in the form already coded:
   dark glass in both modes, numerals the mode pair in
   `SealBarParams.windowInk()`. No third variant, no reopening.
2. **ENDING_SOON carrier — CLOSED.** Whole-window brass inversion
   is the sole alarm; jewel steps down to brass; bolt does not
   back off (pose is truth — a backed-off bolt claims the seal is
   releasing). DUNKEL's 5dp backoff dissent stays on record in the
   Bolt §11, not revived here.
3. **Jewel assignment — CLOSED.** BRASS = proven idle, MAGENTA =
   running (TON/FORM/FEILE over DUNKEL's swap). The halo ring, not
   a hue swap, is what separates the lit states at blur.
4. **Detent scale — PROPOSED, priced S** (§3.2). The only anatomy
   change in this pass. Everything else in the Bolt's §8 merge
   backlog stands as written.
5. **Housing as a door — NO from this seat.** If the housing ever
   becomes tappable (HAUT's §7.3 amendment in the Bolt), the press
   must move the *case* (1dp dip), never the bolt, jewel, or
   window: a gauge you can fidget lies about being a gauge. Until
   Ren rules it a door, the strip stays honestly non-interactive.

## 6 · Fail checklist — any one kills the frame at review

- [ ] Any word on the face other than `END` and `HH:mm`
- [ ] Numerals visible with no live timed end, or any placeholder
- [ ] A lit jewel on an OFF / UNPROVEN / fault face
- [ ] Orange anywhere except bed + OFF socket paint
- [ ] Green or near-green anywhere (standing house ban)
- [ ] Bolt at ½, or any fourth pose; ticks lit, filled, or labelled
- [ ] Housing height ≠ 60dp in any state; any pose animated as a
      fade (verdicts land in one frame; travel follows the Bolt
      §7.1 50ms keyframed throw inside the existing Throw sound)
- [ ] OFF frame whose exposed bed is <80% of the bolt footprint
      (re-triggers the full glance battery, Bolt §6 T1–T9)
- [ ] A frame that needs its caption to be understood

## 7 · Spoken parity

The face is two channels (pose, light) + one number; TalkBack gets
the same: the Bolt's closed 11-sentence spoken set (LETTER §3)
stands unchanged — every sentence leads with its state claim,
numerals and `END` stay out of the semantics tree as separate
nodes, no live region. The detent scale adds no semantics: it is
machining, and the spoken set already says what the ticks show.

— **FALZ**, Designer, dedicated-100 · issue #4 cell
Declared as design only — nothing compiled, nothing built.

---

## CORRECTION NOTE — issue-#4 closer pass, Oct 30 (KRALL K-4/K-6/K-8/K-9)

Filed by the closer, on the record, so no finisher renders from
wrong numbers again:

- **Channel (K-4):** §3's formula (housingW − 156 = 204dp) and §4's
  grid (channel x=24→222, 198dp; leading edges OPEN 114 · CAUGHT
  150 · THROWN 222) describe **no built geometry**. The code's
  channel is layout-derived (`weight(1f)`): at the 360dp reference
  it is **191dp, travel 101dp**. Render from the code
  (`SealBarParams` + the running layout), never from §3/§4's
  figures.
- **Beat 1 (K-6):** §2's name for beat 1 is corrected: it carries
  **not-protected-ness**, not "OFF-ness". CAUGHT at ⅓ exposes ≈⅔
  of the bed (the render gate measures it between OFF and RUNNING),
  so beat 1 fires for CAUGHT too; §2's "exposed third" is wrong,
  §4's "right ⅔" is the geometry.
- **Fail list (K-8):** the checklist line "travel follows the Bolt
  §7.1 50ms keyframed throw" is **suspended** until the merge pass
  lands keyframes; the shipped 50 ms tween is a named INTERIM and
  does not fail this list.
- **Detent scale (K-9):** the tick spec (§3.2) is **not built** and
  is not to be built from these coordinates; it must be re-priced
  on the code's channel with a render order the bolt cannot cover
  before any finisher picks it up.
