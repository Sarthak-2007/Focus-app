# DESIGN — Issue #4: THE BOLT (Deck top strip, killed as text, rebuilt as an instrument)

**Order:** Sarthak's screenshot, Oct 30 02:49 IST — the Deck's pinned proof strip
(`BLOCK OFF · APPS 89 · TIME ENDS 07:28` over `BIND NOT HELD · PROOF: POSITIVE`)
ordered rebuilt as an object, not a text dump. His measuring question — *does Blockit
ever write like this?* — is answered by this design: no. Blockit lets the object carry
the state. So does this.

**Cell:** ANTLITZ (lead) · FORM (designer) · FUNKE (innovator) · SALZ (critic) ·
WERK (builder) · FEILE (finisher, geometry) · LETTER (finisher, typography &
language) · TON (finisher, colour & material) · KANTE (finisher, states & edge
cases) · MASSSTAB (finisher, mock fidelity) · HAUT (feel, touch) · KLANG (feel,
sound) · GANG (feel, motion) · BLICK (feel, the glance) · DUNKEL (feel, atmosphere
& night; re-seated once after the first seat never woke).

**Status:** design complete; v0 implementation DECLARED in this clone (WERK,
uncompiled — Ren compiles only, on Sarthak's word). The amendment backlog in §8
brings the built code to this spec at the merge pass.

---

## 0 · The verdict on the old strip

It is a *report about* a machine, printed next to the machine — five derivations of
one wire in five tenses stitched into one band (SALZ C1, with file+line evidence):
the BLOCK cell judged on the freshness-checked print state, the running cell on the
frozen seal verdict, the seam stamps on the raw warrant, the refusal sentence
suppressed on staleness, the imprint on a sixth mixture. One frame could legally
print three verdicts of one wire. Its count summed LOCKED + PEEK under a shut verb
(the aggregation lie already convicted as UNA2-02 — and fixed on the imprint, never
on this strip). Its ENDS slid at idle, printed a hypothetical in the grammar of a
fact. Its confidence vocabulary (BIND / PROOF / POSITIVE-for-bad-news) was the studio
talking to itself in public, at 10sp, first in line for clipping.

The strip is deleted. What replaces it is one instrument with one derivation.

## 1 · The instrument

**THE BOLT** — a thrown deadbolt in a housing, at the strip's own address: first
element of the Deck's pinned stack, directly above the 72dp slide register.

- A **bolt** (90×34dp) slides in a **channel** (180×36dp at the 360dp reference
  width; the channel is the one flexible element — all width growth goes to it,
  the bolt never grows). Detents are fractions of travel: **OPEN 0 · CAUGHT ⅓ ·
  THROWN 1** — travel at reference = exactly one bolt width (90dp), so the thrown
  bolt occupies precisely the half the open bolt vacated.
- The channel floor at the shut end carries the **warning bed**: flat matte
  `#D63E06` paint (mode-invariant), sized to the bolt's home footprint. The bolt
  covers it exactly when home. *The orange is uncovered exactly when the bolt is
  not home* — OFF's interruptiveness is area of paint, never light (§6).
- A **keeper socket** at the channel's right end (the bolt visibly arrives
  somewhere; exposed at OPEN/CAUGHT, covered when thrown, its interior painted
  `#D63E06` in OFF only) and a **keeper collar** the thrown bolt overlaps by 4dp.
- A **locking pin** above the bolt's thrown position, with a real notch machined
  in the bolt (12dp wide, 2dp clearance per side, tip stopping 1dp above the notch
  floor). Geometry enforces law: the notch is only under the pin when THROWN, so
  the pin *cannot* drop in any other pose.
- A **jewel in a bezel disc** (bezel ⌀15–20dp class; jewel **≤9dp, hard cap** —
  the glance budget, §6): dark lens / brass / magenta by state (§3).
- A **time window** (102×36dp, glass both modes) holding Anonymous Pro Bold 20sp
  `HH:mm` and nothing else; the word `END` engraved once, on the bezel below the
  glass (LETTER §1.3). Numerals print only for a live timed end (§4).
- The housing is a **band, not a card**: radius 0, 1dp hairlines top and bottom,
  height **60dp** — on the Deck's ladder (header 48 / Bolt 60 / slide 72) and 5dp
  under the 65dp the strip + seam used to rent (§9 disposition). Housing height
  never changes by 1dp in any state; all pose changes are offsets inside it.

The bolt is **not a handle**: no grip, no drag, no slide gesture. It moves by
itself or not at all. Interaction is specified as an amendment (§7.3), not v1
behaviour — as built, the instrument is honestly non-interactive.

## 2 · The one-derivation law (SALZ C1/C3, KANTE §0)

Per frame, the host computes **exactly one** folded verdict from **exactly one**
warrant read; the pure function `boltStateFor` receives only that verdict and
never touches `WatchStore`, `seamMarksFor`, or the warrant itself (grep gate in
KANTE §7.3). The fold, host-side, running precedence first-match-wins:

`liveRaw == OFF → OFF` (UNA2-04, frozen never consulted) · `read == null →
NO_READ` · `stale ∧ raw LIVE → STALE_LIVE` · `print UNPROVEN → UNPROVEN` ·
`print LIVE ∧ frozen ≠ LIVE → FROZEN_UNPROVEN` · `print LIVE ∧ frozen LIVE → LIVE`.

**Pose is geometry** (what physically happened: the session, the seal act).
**Lamp is proof** (what is proven *now*, always from the live fold — the frozen
verdict attests the seal act only, because it carries no read instant and can
never be freshness-checked, KANTE B-C4). **Window is strength** (live end / dim
end / frozen end / blank / shuttered). A mid-session degradation therefore drops
the lamp the same frame while the pose stays seated — proof falls, the session
fact doesn't, and nothing on the face claims otherwise.

WERK's lie register ships with this design, unhidden: the frozen verdict's limits,
the re-derived end's dependence on session-row emissions, and the two blocking
input charges in §8 (B-C1 shield-gap, B-C2 finish input). The instrument's claim
is liveness + pipeline, never "an intercept has fired" — the warrant excludes
shield-protecting by design, and the face must never out-claim it (SALZ test 2).

## 3 · States — the faces (KANTE matrices, TON lamp law, FEILE geometry)

Detents and lamps (position × light; numerals per §4; charge-IDLE is a shape, not
a state — §3.1):

| State | Bolt | Pin | Jewel | Reads in one look as |
|---|---|---|---|---|
| **OFF** | OPEN, open stop; warning bed + painted socket fully exposed | UP | Dark lens (dead) | *open, and it shows* |
| **UNPROVEN / NO_READ / FROZEN_UNPROVEN / IDLE-AUDIT** | CAUGHT at ⅓ (audited-but-not-held, read as "not protected" ≥95%) | UP (geometry forbids drop) | Dark lens | *caught mid-travel — not locked* |
| **JAMMED** (revoked mid-session) | CAUGHT + refusal sentence below (the open door) | UP | Dark lens | *it broke — and says why* |
| **ARMED / BLOCKING** (proven idle) | THROWN, seated | DROPPED | **BRASS** steady | *locked, and I can see it holds* |
| **RUNNING** (proven) | THROWN, seated | DROPPED | **MAGENTA** steady | *a session is locked in* |
| **RUNNING_UNPROVEN / FLOW-UNPROVEN** | THROWN, seated (the act is a fact) | DROPPED | **Dark lens** | *seated hardware, no claim lit* |
| **STALE_WHILE_RUNNING** | THROWN, seated | DROPPED | **Dark lens + window shuttered** | *it was holding; proof gone dark* |
| **ENDING_SOON** (≤300s to live end) | THROWN, seated — pose never moves | DROPPED | steps **down** to BRASS | *still holds; the window says the end is near* |
| **DETENTION FAULT** (denied release retry) | THROWN, seated | DROPPED | **MAGENTA** (KANTE exception) | *it refused to open — same family as running* |

Rules that bind the table:
- **Health is the only thing that lights; fault never lights** (TON). OFF and
  unproven are matte paint and dark glass. No state ever shows a lit jewel
  alongside a WARNING-TONE pose (FUNKE L2, structural).
- **Lamp derivation is host-folded and independent of pose derivation** (KANTE
  §1.1) — FLOW keeps magenta (its end is infinity; an unlit lamp would claim
  FLOW is weaker), FLOW-without-freshness keeps the dark lens honestly.
- **CAUGHT = ⅓, never 46%** — 46% (in fact 48.9% by MASSSTAB's arithmetic) reads
  as HALF, and half is a resting state; the instrument has exactly two resting
  positions and one caught pose. The ⅓ pose carries the detent contact cue (the
  bolt's tail touches the open stop's line) and the ink-flip at full shut.
- **The screwdriver test** (FUNKE): no forced-state hack exists. No-lease fails
  toted applications by definition; a wedged arrival is either a named case row
  or a KANTE charge (B-C3: the detent-input list is the closed contract).
- Charge-IDLE: the seal preview law governs (the preview IS ARMED's face; tap
  either of the two Seal buttons and the Deck answers exactly as much as the
  preview face already states). No lamp, pin, or travel may be added for charge.
- **First composition always SNAPs** (KANTE B-C5): no travel is ever animated
  into existence on cold open; a cold open showing THROWN+MAGENTA at frame 0 is
  the instrument telling the truth about a seal made before the Deck existed.
- Quiet swaps (breaks, peek token banking, check-timer fires) fold into the
  pose/lamp functions and trigger **nothing** — no sounds, no glints, no
  sentences (KANTE §3.8: a Bolt that reacts to a parked token lies worse than
  the strip did).

### 3.1 Ending-soon threshold

**300 seconds** from the live end (GANG, FEILE, shipped param). FUNKE's
last-10%-of-span ∨ ≤10-min-any-span and any "75% spent" rule are rejected:
neither is readable (a span fraction is a computation, not a look; a user cannot
verify either without a clock and arithmetic — GANG's verification argument).
The 300s flag is *never rendered solo* on an otherwise-RUNNING face.

## 4 · Words — the face's whole vocabulary (LETTER)

On the face: **`END`** (engraved, Atkinson Hyperlegible Regular 9sp, −0.8dp
tracking compensation, on the lower bezel outside the glass) + **`HH:mm`**
(Anonymous Pro Bold 20sp, 24-hour, verified 54.59dp advance against the 102dp
window with slack to spare; density-only scaling — the instrument does not
auto-shrink; font scale ≥1.3 clips are an honest overflow, never a shrunk digit).

- **Numerals are the absolute clock end in every lit state** (bolt lands t0;
  GANG's remaining-time-on-the-face idea is rejected — a shrinking duration
  reopens "the end slides" and breaks numeral invariance; remaining time lives
  on the hero card's remaining-line where it already exists).
- **Presence law:** the window is **blank** whenever no session has sealed —
  OFF, UNPROVEN, ARMED, FLOW, NO_READ, and every pre-seal row. A planned end
  printed without labels is the old strip's idle-ENDS lie in new metal.
  Placeholders (`--:--`) are banned outright. KANTE's dim-numerals rows for
  idle planned/frozen ends are superseded by this law.
- **Alert numerals are banned** (numerals never take the alert material); the
  one exception-that-isn't: ENDING_SOON inverts the *whole window* (brass plate
  behind, measured pair, §5) — a block change, not a recolour.
- Count is **off the face** (FORM/UNA2-02 beats FUNKE's engraved `89`): its
  homes are the Shield header and the split LOCKED/PEEK imprint. OFF and
  TOTED-OFF are visually identical band faces (spoken/shown distinction lives
  in the refusal sentence + Shield page); the count belongs with its neighbours
  in the order.
- **The spoken set is closed** — 11 `contentDescription` sentences, one merged
  semantic node, numerals and END removed from the semantics tree, no live
  region, no stateDescription (LETTER §3, verbatim in the spec): OFF / OFF-toted
  / OFF-while-something-runs / UNPROVEN / ARMED / RUNNING / RUNNING-UNPROVEN /
  FLOW / FLOW-UNPROVEN / REVOKED-idle / REVOKED-running. Every sentence begins
  with its state claim; none flatters (no "everything is fine").
- **Banned on the face, by family:** every state label; BLOCK/APPS/TIME as
  headers; BIND/PROOF/PRINT/HELD/SEALED/POSITIVE/LEASE/WARRANT/TOTAL vocabulary;
  the count; ENDS in any form; countdowns; AM/PM; day words; placeholders;
  tooltips; brand/serial engraving; track-end letters; alert glyphs; the
  vocabulary of any other screen (no LEAVE, no totals).
- TalkBack merges the housing's node with the refusal sentence's node when the
  sentence shows (REL-TB-1/2) — the hearing user gets the same two-channel
  instrument the seeing user does.

## 5 · Colour & material (TON's table governs; DUNKEL's night anchors)

Binding law, computed WCAG throughout (full state×element×day×night table = TON's
report, adopted verbatim into this spec):

- **The bolt binds `inkControlFill()` in code, never a hardcoded hex.** Night
  thrown fill = cap `#4A423C` (1.96:1 on the recess — the bar's largest matte
  step); **cream `#FFE4CF` is never a fill** (15.8:1 floodlight); cream is grips
  and window ink only.
- **The orange is one:** `#D63E06`, mode-invariant, used as matte stripe + bed +
  socket paint. Night `#FF8A5C` is rejected (the exiled fourth orange; glows
  8.29:1) — and `alertLamp()` as coded re-imports it: deleted at merge (§8).
- **Window:** dark glass both modes — day glass `#2E2A26` / numerals `#FAF6F0`;
  night glass `#161311` / numerals `#FFE4CF`. (DUNKEL's proposal — night glass +
  cream extended to day by the stripe's invariance logic — is recorded as an
  open extension for TON/Ren ratification; the fallback is exactly this table.)
- **ENDING_SOON inversion is a hardcoded mode-invariant pair** — numerals
  `#252323` on brass `#E2BC39` (8.55:1, the table's only alert register); the
  window flips as one measured block. The mode-variant Material alert was the
  trap (day 4.29:1, night 5.80:1, and neither is the brightest thing in its
  face). This inversion is also the glance carrier for ENDING_SOON (§6).
- **The jewel rides in a bezel disc** (ink by day, cap by night), which rescues
  brass from its day camouflage (unset brass vs day glass was 1.21:1). MAGENTA
  follows the house split (day `#C81FA8`, night `#F04FCB`); BRASS is one value
  both modes (`#E2BC39`) — metal does not change at dusk. RUNNING carries a
  1dp-class **halo ring** in jewel tone (MASSSTAB/BLICK: hue alone at a 2.5dp
  dot failed BLUR — ARMED and RUNNING were one read until the ring gave the
  lit states a size difference).
- **Shadow belongs to the bolt, drawn inside the channel** (a contact rib, not
  elevation). TON's reassignment is ratified and DUNKEL completes it: at night
  the shadow is under the crush floor and worth nothing; night weight comes
  from **occlusion (bed covered) → keeper-collar overlap (4dp) → the proven-
  thrown rim step (`#807A7A`, night only, proven states only) → cream grips**,
  in that rank. Day gets the drawn contact shadow (1dp south line, no blur —
  Compose `.shadow()` is banned in this bar).
- **Salience law:** health is the only thing that lights; fault never lights;
  urgency floods exactly once, briefly, in brass (the inversion).
- **Never dim** (DUNKEL R4, hard floor in every mode and phase, including the
  seal shot and detention): the bed, the stripe, OFF socket paint, cream grips,
  window numerals, the proven-thrown rim step, and the jewel — steady, never
  pulsing, breathing, or fading. The XML colour files are not a source of
  truth; the table is.

## 6 · The glance gate (BLICK — the ship gate)

Every number below was measured on real renders (blur at 1.8 arcmin/px,
greyscale twin, 12dp downscale). **The Bolt ships only if the battery passes;
no battery, no swap.**

- **OFF vs RUNNING, pairwise, at 10% brightness in a dark room:** OFF's
  orange-region luminous total ≥ 3× RUNNING's lamp total. As coded (WERK v0,
  no bed), the totals are **0.00 vs 42.6 — a fail by the whole amount.** As
  specced: OFF = bed 644dp² × 0.1775 = 114.3 + painted socket 11.4 = **125.7**;
  RUNNING jewel worst case (brass, 9dp) = 33.3 → **3.77× PASS** (6.16× with an
  orange jewel). The gate is only passable from both sides — OFF gains area,
  RUNNING loses lamp — which is why the jewel cap is a law, not a style:
  **jewel luminous total ≤ 41.9 proxy units; ≤9dp diameter is the enforceable
  proxy.** The budget ships as a named constant in code so the gate is
  executable, not prose (DUNKEL §7).
- **OFF's peripheral read is ONE load-bearing cue** — the exposed orange bed —
  plus foveal confirmations. Protective rule: any future change cutting the
  exposed bed by >20% re-triggers the whole battery.
- **ENDING_SOON as coded FAILS its pairwise test** (hue-only numerals, a 4dp
  stripe, a 5dp backoff, a 1dp halo — none survive blur). The carrier is the
  full window-block inversion (§5): a different-shaped mass entering the lit
  cluster. The 2dp SEATED sink is struck from the cue inventory (1.8 arcmin —
  sub-perceptual; it stays as feel, §7.1, never as evidence).
- **CAUGHT passes only under the binary-bucket rule** (≥95% of cold reads sort
  it "not protected") plus the detent contact cue and the full-shut ink-flip —
  never as an exact-position read.
- **Screenshot law:** the acceptance frame is a side-by-side OFF/RUNNING pair,
  Night, with greyscale twins. A lone RUNNING shot proves nothing — OFF is the
  state that can hurt him, and the contrast is the proof. Battery: T1–T9
  (BLICK §5) — cold reads, pairwise forced choice, 1 AM simulation (10%
  brightness, dark room, no re-aiming), greyscale twin, OFF-grep, the count
  question, the two-channel parity check, paired-screenshot adjacency, and the
  final cold-read-of-record.

## 7 · Feel — motion, sound, touch

### 7.1 Motion (GANG, re-anchored by KLANG)

- **The flip law, scoped:** warrant *verdicts* land in one frame (a verdict is a
  fact; a 100ms fade is 6 frames of lying). Travel >30dp is never one frame —
  the threshold law (≤2dp step / 2–10dp 2 frames / >10dp ≥3 frames + 20ms) is
  tabled for Deck-wide adoption.
- **The seal act is depicted at t0.** The bolt depicts the seal (t0), not the
  ground's arrival (t=400); the throw therefore runs *inside the Throw sound's
  own ≤85ms window*: keyframed travel OPEN→THROWN over **50ms / 3 frames**
  (0 / 24 / 62 / 94dp on the reference, scaled to the responsive channel), the
  pin snapping 1 frame after landing (at the +62ms haptic seat inside KLANG's
  buffer), then a **2dp SEATED sink, stepped rigidly, no spring** — and
  stillness. Loops, breathing, and idle animation are banned outright.
- **The ending-soon flag rises once** (150ms EasingA, on entry only) and falls
  as a snap (retreat is a fact, not an event). The flag never re-fires within a
  session. The window does not tick, pulse, or count down — it holds the
  absolute end, still.
- Mid-session degradation (lamp drop) lands as a 1-frame pose-true STEP or a
  2dp snap — never a fade (a fade reads as *failing*, present tense; the truth
  is *fallen*, past tense).
- Reduced motion collapses the seal sequence to the final pose at the anchor
  frame (the window reading is untouched). `prefersReducedMotion` re-anchors
  per-frame; plate and glow never desync.
- As coded, WERK's 260ms EasingA glide is **retired** by this choreography (§8).

### 7.2 Sound (KLANG — the composable is silent)

- **Zero new sounds, zero sound calls in the composable.** The seal Throw's
  rendered buffer already contains a layer literally named THE BOLT (noise
  rattle +30–85ms, haptic seat +62ms) — bit-identical, hash-guarded
  (`SEAL_BUFFER_GOLDEN_SHA256`), never re-rendered for this design. The Bolt's
  picture is timed to land inside sound that already exists.
- **Silence rulings:** ending-soon flag = silence ( brass inversion is the
  alarm; no instant exists to mark); idle warrant flips = silence both
  directions (no actor acted; a discovery clunk is a jump scare); staleness =
  silence (its discovery is the Deck's knock, once, at the Deck's threshold —
  never from the bar).
- **Press voices live at destinations** (navigateTo's detent 540, the Desk
  Clock toggle's latch). Exactly one new call is specified: the housing's fix
  route (§7.3) speaks PARK's quietest seat knock (10ms, 0.10 day / 0.06 night,
  300Hz class) in the host/VM handler on DOWN. A locked press speaks the house
  refusal knock (6, 0.2, 320), throttled 250ms.
- No reverse Throw may ever be created (the house has no unseal sound; building
  one is how the bar starts lying about unsealing being an event).

### 7.3 Touch (HAUT — specified amendment, not v1 behaviour)

WERK built the instrument non-interactive (honest). If the housing becomes a
door, HAUT's design is the binding spec: **the finger moves the case; proof
moves the bolt** — a 1dp whole-housing dip (PRESS_MS 90 in, ≤150ms return,
EasingA), gated by `navigationLocked()` re-read synchronously at DOWN *and* UP
via a live provider (a stale captured gate is the exact failure mode the Deck
has already shipped elsewhere). A gated press is **totally dead** — no dip, no
tick, no semantics change. Single-voice rule: at most one sound per press, from
the destination or the knock, never both. HAUT's 10-point NEVER list (no
haptic invented for the bar — the house haptic table is closed; no press may
move the bolt, jewel, or window; no long-press; no ripple; …) ships verbatim
in his report.

*Flagged upward, undecided in-cell:* if the housing becomes a door, HAUT argues
the refusal sentence below should be demoted to print — two doors, one lack.
That is a Deck-level decision for Ren, not this cell.

*Dissent recorded:* HAUT holds a locked press should be totally silent (a man
pressing a locked door hears the door, not an alarm); KLANG's refusal-knock
ruling governs (house consistency + the Deck's existing gated-press language),
with HAUT's objection on record.

## 8 · Implementation — what exists, what the merge pass owes

**Built (WERK, DECLARED, uncompiled, this clone's working tree):**
`SealBar.kt` (new — SealBar composable, SealBarState incl. honest
RUNNING_UNPROVEN, pure `sealBarStateFor` / `endsAtNumeralsFor`, `SealBarParams`);
`TactileDeckScreen.kt` (ProofStripBand call replaced; seam computation removed);
`MonolithCustomComponents.kt` (ProofStripBand + StripCell + StripTones DELETED;
`sealStateForBand` left as dead code for the final pass to remove; seam helpers
KEPT unrendered for the sentence slot, imprint, and tests); `SealBarStateTest.kt`
(new). BIND/PROOF render nowhere on the Deck (grep-verified). Built height 53dp —
the strip's old address, honoured deliberately.

**Amendment backlog (merge pass, owner-tagged):**
1. **FEILE §11, all 12 lines** — housing 53→60dp, bolt 90×34, responsive channel
   (channelW = housingW − 156, detents as fractions), CAUGHT 46%→⅓, keeper
   socket + collar, pin/notch machining (pin gated on THROWN), jewel bezel,
   window interior, END per §4, ending-soon backoff DELETED (see dissent below).
2. **TON's table** applied to SealBarParams token-by-token (incl. window pair,
   inversion pair, rim step, shadow rib); `alertLamp()` and the `#FF8A5C` /
   lamp-fill imports deleted; `stripeOrange = #D63E06` invariant.
3. **DUNKEL's bed + socket paint + hairlines** rendered (bed → collar → socket
   → plate → rim, his render order); jewel ≤9dp enforced; the ≤41.9 budget as
   a named constant + token test.
4. **GANG/KLANG choreography** replaces the 260ms glide (throw keyframes,
   pin-at-seat, stepped sink, flag rise/snap).
5. **LETTER's spoken set + semantics law** (11 descriptions, merged node,
   numerals/END out of the tree, REL-TB merge) and banned-words sweep as a
   test.
6. **KANTE's matrices as derivation tests** — every row of §2/§3/§4 of his
   report, read against the fold; the §7 grep gates (boltStateFor purity).
7. **KLANG's one new call** (fix-route knock, host handler only) — only with
   §7.3 if Ren rules the housing a door.

**BLOCKING input charges (merge cannot ship the finished Bolt without them):**
- **B-C1 (KANTE):** the host fold needs a `shieldStale` input — without it,
  there is no honest full-strength running state while the shield gap stands.
- **B-C2 (KANTE):** the Bolt needs the finish/detention input, or it drops to
  idle up to ~1.8s before a release is proven — a lie in the kind direction.
- B-C3/B-C4 stand as the closed input contract and the frozen-verdict limit
  (documented in WERK's lie register; no fix exists — the face's honesty is
  bounded by them, and §2 says so on the record).

## 9 · Audit disposition (SALZ C7 — answered, not dodged)

AUDIT-deck §3 evicted this strip (seam → PARK; sentence to a × destination;
top strip to refuse-with-repair). This design **does not comply silently and
does not ignore it**: issue #4 is Sarthak's direct order for this surface,
issued after the audit, and it supersedes the eviction for the Deck's pinned
band. Where this design *exceeds* §3: the seam stamps are **deleted outright**,
not moved to PARK — posology stamps on a parking page would re-import the
telemetry vocabulary §3 itself wanted off the Deck. Where it agrees with §3:
the refusal sentence stays (it is below the band, in the sentence slot, and is
the JAMMED face's second channel), and nothing §3 protected is weakened.
**Flagged for Ren's countersignature** — a cell does not overrule an audit on
its own authority; it states the disposition and signs it.

HEADER-BAND-AUDIT cross-check: the Bolt carries no header-band vocabulary; the
count's exile (§4) is consistent with UNA2-02's own migration.

## 10 · Acceptance (SALZ's 21, mapped)

SALZ's 21 acceptance tests bind the merge, including: the one-derivation test
(one fold, grep-enforced); the both-directions disagreement test (frozen ON
standing mid-session; live OFF overriding instantly); mid-session revocation
(the JAMMED face + sentence, same frame); the idle-tense test (no hypothetical
end in fact grammar — §4's blank window); the 5-second cold test; the 1 AM
test (§6 battery); TalkBack parity (§4); the rent test (60dp buys a state
change, a threshold, and an evidence channel the 53dp strip never carried);
and the audit-disposition test (§9 above). BLICK's T1–T9 (§6) run alongside;
a failure in either suite is a failure of the design, not of the test.

## 11 · Rulings & dissents of record (lead adjudications)

- **Instrument:** FORM's Bolt semantics on WERK's SealBar chassis, finished to
  FEILE's 60dp geometry. FUNKE's MESH is the documented alternative (racks +
  shear pin + marker tooth) — not built; his hard laws (one-frame verdicts,
  unproven never drawn engaged, live OFF overrides frozen instantly, FLOW
  shows no fake end) are adopted into §2/§3.
- **Threshold:** 300s (§3.1) over FUNKE's fraction rule.
- **Ending-soon pose:** the bolt does NOT back off (FEILE + GANG: pose is
  truth; a backed-off bolt claims the seal is releasing). *Dissent:* DUNKEL
  would keep the 5dp backoff — it cracks the warning bed open (a 12.4-unit
  sliver in his own arithmetic) as the seal approaches release. Noted; the
  hierarchy passes without it (ENDING ≈72 > RUNNING ≤33.3), and pose-truth
  governs.
- **Ending-soon lamp:** jewel steps down to brass while the window inversion
  carries the warning (TON) over FEILE's "lamp unchanged" (dissent recorded —
  her position was consistency; TON's is that a lamp brightening to alert is a
  second alarm, and alarms in this house are singular).
- **Jewel assignment:** BRASS = proven idle, MAGENTA = running (TON/FORM/
  FEILE). *Alternative recorded:* DUNKEL would swap (brass for the lived-in
  running state) — inside budget either way; TON's assignment governs.
- **Seal timing:** the throw is anchored at t0 inside the Throw buffer's
  ≤85ms window (KLANG), with GANG's 50ms keyframed curve; GANG's t=400
  ground-step choreography is superseded on the anchor only. The bar's scheme
  tokens still step at t=400 with the ground (DUNKEL R2 — a bolt thrown in
  daylight, then the room goes dark; that order is correct).
- **Type scaling:** density-only; font scale never rescales the instrument
  (LETTER's ruling, lead-ratified — a bolt is a fixed-size object; the
  alternative, font-scaled geometry, breaks every detent fraction).
- **Press sound:** KLANG's seam (§7.2/§7.3) over HAUT's total silence
  (dissent recorded, §7.3).

## 12 · Mocks

MASSSTAB's seven hand-built SVGs (5 states day + OFF/RUNNING night) were drawn
on FORM's 68dp reference frame to audit the anatomy; the geometry of record is
FEILE's 60dp grid (§1/§3), and his defect register F1–F14 is dispositioned
line-by-line in his report (F1 arithmetic agreed the ⅓ law; F2/F3 pin fixes
adopted; F6 collar overlap adopted; F9 shutter tone adopted; F10/F11 contrast
fixes adopted — END-on-glass resolves via §4's bezel engraving). The RUNNING
exemplar's remaining-style numerals are corrected to a clock end per §4. The
SVG sources live in MASSSTAB's report on this issue; re-rendering them on the
60dp grid is part of the §8 backlog's render tests.

— **ANTLITZ**, cell lead, issue #4. DECLARED, uncompiled, awaiting Ren.


---

<!-- Composition note (K8, #231 train composition): this file was added independently on both main and batch-10; both texts are preserved. Above: the batch-10 text. Below: the main text. -->

# DESIGN — Issue #4: THE BOLT (Deck top strip, killed as text, rebuilt as an instrument)

**Order:** Sarthak's screenshot, Oct 30 02:49 IST — the Deck's pinned proof strip
(`BLOCK OFF · APPS 89 · TIME ENDS 07:28` over `BIND NOT HELD · PROOF: POSITIVE`)
ordered rebuilt as an object, not a text dump. His measuring question — *does Blockit
ever write like this?* — is answered by this design: no. Blockit lets the object carry
the state. So does this.

**Cell:** ANTLITZ (lead) · FORM (designer) · FUNKE (innovator) · SALZ (critic) ·
WERK (builder) · FEILE (finisher, geometry) · LETTER (finisher, typography &
language) · TON (finisher, colour & material) · KANTE (finisher, states & edge
cases) · MASSSTAB (finisher, mock fidelity) · HAUT (feel, touch) · KLANG (feel,
sound) · GANG (feel, motion) · BLICK (feel, the glance) · DUNKEL (feel, atmosphere
& night; re-seated once after the first seat never woke).

**Status:** design complete; v0 implementation DECLARED in this clone (WERK,
uncompiled — Ren compiles only, on Sarthak's word). The amendment backlog in §8
brings the built code to this spec at the merge pass.

---

## 0 · The verdict on the old strip

It is a *report about* a machine, printed next to the machine — five derivations of
one wire in five tenses stitched into one band (SALZ C1, with file+line evidence):
the BLOCK cell judged on the freshness-checked print state, the running cell on the
frozen seal verdict, the seam stamps on the raw warrant, the refusal sentence
suppressed on staleness, the imprint on a sixth mixture. One frame could legally
print three verdicts of one wire. Its count summed LOCKED + PEEK under a shut verb
(the aggregation lie already convicted as UNA2-02 — and fixed on the imprint, never
on this strip). Its ENDS slid at idle, printed a hypothetical in the grammar of a
fact. Its confidence vocabulary (BIND / PROOF / POSITIVE-for-bad-news) was the studio
talking to itself in public, at 10sp, first in line for clipping.

The strip is deleted. What replaces it is one instrument with one derivation.

## 1 · The instrument

**THE BOLT** — a thrown deadbolt in a housing, at the strip's own address: first
element of the Deck's pinned stack, directly above the 72dp slide register.

- A **bolt** (90×34dp) slides in a **channel** (180×36dp at the 360dp reference
  width; the channel is the one flexible element — all width growth goes to it,
  the bolt never grows). Detents are fractions of travel: **OPEN 0 · CAUGHT ⅓ ·
  THROWN 1** — travel at reference = exactly one bolt width (90dp), so the thrown
  bolt occupies precisely the half the open bolt vacated.
- The channel floor at the shut end carries the **warning bed**: flat matte
  `#D63E06` paint (mode-invariant), sized to the bolt's home footprint. The bolt
  covers it exactly when home. *The orange is uncovered exactly when the bolt is
  not home* — OFF's interruptiveness is area of paint, never light (§6).
- A **keeper socket** at the channel's right end (the bolt visibly arrives
  somewhere; exposed at OPEN/CAUGHT, covered when thrown, its interior painted
  `#D63E06` in OFF only) and a **keeper collar** the thrown bolt overlaps by 4dp.
- A **locking pin** above the bolt's thrown position, with a real notch machined
  in the bolt (12dp wide, 2dp clearance per side, tip stopping 1dp above the notch
  floor). Geometry enforces law: the notch is only under the pin when THROWN, so
  the pin *cannot* drop in any other pose.
- A **jewel in a bezel disc** (bezel ⌀15–20dp class; jewel **≤9dp, hard cap** —
  the glance budget, §6): dark lens / brass / magenta by state (§3).
- A **time window** (102×36dp, glass both modes) holding Anonymous Pro Bold 20sp
  `HH:mm` and nothing else; the word `END` engraved once, on the bezel below the
  glass (LETTER §1.3). Numerals print only for a live timed end (§4).
- The housing is a **band, not a card**: radius 0, 1dp hairlines top and bottom,
  height **60dp** — on the Deck's ladder (header 48 / Bolt 60 / slide 72) and 5dp
  under the 65dp the strip + seam used to rent (§9 disposition). Housing height
  never changes by 1dp in any state; all pose changes are offsets inside it.

The bolt is **not a handle**: no grip, no drag, no slide gesture. It moves by
itself or not at all. Interaction is specified as an amendment (§7.3), not v1
behaviour — as built, the instrument is honestly non-interactive.

## 2 · The one-derivation law (SALZ C1/C3, KANTE §0)

Per frame, the host computes **exactly one** folded verdict from **exactly one**
warrant read; the pure function `boltStateFor` receives only that verdict and
never touches `WatchStore`, `seamMarksFor`, or the warrant itself (grep gate in
KANTE §7.3). The fold, host-side, running precedence first-match-wins:

> **LANDED (issue-#4 closer pass, Oct 30):** this law was a promise, not a
> property, from the design's filing until the closer pass (KRALL K-1, VEX §A).
> It is now code: `BoltInputs` + `boltStateFor` in `SealBar.kt`, one fold block
> in `TactileDeckScreen.kt`, and the strip's `blockCellFor`/`blockCell`
> derivations **deleted from the host** — no BLOCK string word is derived
> anywhere on this face. The truth table is pinned row-by-row in
> `SealBarStateTest`. Deviations from the sketch above, on the record: the
> fold's output is a `BoltFace` (state + window), not a bare state; the
> NO_READ row is idle-only (a running no-read folds to RUNNING_UNPROVEN —
> the session's pose is a fact); B-C2's detention input (`heldFinish`) is
> passed and outranks all rows; B-C1's shield-beat input is **not** an input
> (the warrant's 45 s law carries the running STALE row; the shield's 10 s
> beat law is never merged into it and remains owed VM plumbing).

`liveRaw == OFF → OFF` (UNA2-04, frozen never consulted) · `read == null →
NO_READ` · `stale ∧ raw LIVE → STALE_LIVE` · `print UNPROVEN → UNPROVEN` ·
`print LIVE ∧ frozen ≠ LIVE → FROZEN_UNPROVEN` · `print LIVE ∧ frozen LIVE → LIVE`.

**Pose is geometry** (what physically happened: the session, the seal act).
**Lamp is proof** (what is proven *now*, always from the live fold — the frozen
verdict attests the seal act only, because it carries no read instant and can
never be freshness-checked, KANTE B-C4). **Window is strength** (live end / dim
end / frozen end / blank / shuttered). A mid-session degradation therefore drops
the lamp the same frame while the pose stays seated — proof falls, the session
fact doesn't, and nothing on the face claims otherwise.

WERK's lie register ships with this design, unhidden: the frozen verdict's limits,
the re-derived end's dependence on session-row emissions, and the two blocking
input charges in §8 (B-C1 shield-gap, B-C2 finish input). The instrument's claim
is liveness + pipeline, never "an intercept has fired" — the warrant excludes
shield-protecting by design, and the face must never out-claim it (SALZ test 2).

## 3 · States — the faces (KANTE matrices, TON lamp law, FEILE geometry)

Detents and lamps (position × light; numerals per §4; charge-IDLE is a shape, not
a state — §3.1):

| State | Bolt | Pin | Jewel | Reads in one look as |
|---|---|---|---|---|
| **OFF** | OPEN, open stop; warning bed + painted socket fully exposed | UP | Dark lens (dead) | *open, and it shows* |
| **UNPROVEN / NO_READ / FROZEN_UNPROVEN / IDLE-AUDIT** | CAUGHT at ⅓ (audited-but-not-held, read as "not protected" ≥95%) | UP (geometry forbids drop) | Dark lens | *caught mid-travel — not locked* |
| **JAMMED** (revoked mid-session) | CAUGHT + refusal sentence below (the open door) | UP | Dark lens | *it broke — and says why* |
| **ARMED / BLOCKING** (proven idle) | THROWN, seated | DROPPED | **BRASS** steady | *locked, and I can see it holds* |
| **RUNNING** (proven) | THROWN, seated | DROPPED | **MAGENTA** steady | *a session is locked in* |
| **RUNNING_UNPROVEN / FLOW-UNPROVEN** | THROWN, seated (the act is a fact) | DROPPED | **Dark lens** | *seated hardware, no claim lit* |
| **STALE_WHILE_RUNNING** | THROWN, seated | DROPPED | **Dark lens + window shuttered** | *it was holding; proof gone dark* |
| **ENDING_SOON** (≤300s to live end) | THROWN, seated — pose never moves | DROPPED | steps **down** to BRASS | *still holds; the window says the end is near* |
| **DETENTION FAULT** (denied release retry) | THROWN, seated | DROPPED | **MAGENTA** (KANTE exception) | *it refused to open — same family as running* |

Rules that bind the table:
- **Health is the only thing that lights; fault never lights** (TON). OFF and
  unproven are matte paint and dark glass. No state ever shows a lit jewel
  alongside a WARNING-TONE pose (FUNKE L2, structural).
- **Lamp derivation is host-folded and independent of pose derivation** (KANTE
  §1.1) — FLOW keeps magenta (its end is infinity; an unlit lamp would claim
  FLOW is weaker), FLOW-without-freshness keeps the dark lens honestly.
- **CAUGHT = ⅓, never 46%** — 46% (in fact 48.9% by MASSSTAB's arithmetic) reads
  as HALF, and half is a resting state; the instrument has exactly two resting
  positions and one caught pose. The ⅓ pose carries the detent contact cue (the
  bolt's tail touches the open stop's line) and the ink-flip at full shut.
- **The screwdriver test** (FUNKE): no forced-state hack exists. No-lease fails
  toted applications by definition; a wedged arrival is either a named case row
  or a KANTE charge (B-C3: the detent-input list is the closed contract).
- Charge-IDLE: the seal preview law governs (the preview IS ARMED's face; tap
  either of the two Seal buttons and the Deck answers exactly as much as the
  preview face already states). No lamp, pin, or travel may be added for charge.
- **First composition always SNAPs** (KANTE B-C5): no travel is ever animated
  into existence on cold open; a cold open showing THROWN+MAGENTA at frame 0 is
  the instrument telling the truth about a seal made before the Deck existed.
- Quiet swaps (breaks, peek token banking, check-timer fires) fold into the
  pose/lamp functions and trigger **nothing** — no sounds, no glints, no
  sentences (KANTE §3.8: a Bolt that reacts to a parked token lies worse than
  the strip did).

### 3.1 Ending-soon threshold

**300 seconds** from the live end (GANG, FEILE, shipped param). FUNKE's
last-10%-of-span ∨ ≤10-min-any-span and any "75% spent" rule are rejected:
neither is readable (a span fraction is a computation, not a look; a user cannot
verify either without a clock and arithmetic — GANG's verification argument).
The 300s flag is *never rendered solo* on an otherwise-RUNNING face.

## 4 · Words — the face's whole vocabulary (LETTER)

On the face: **`END`** (engraved, Atkinson Hyperlegible Regular 9sp, −0.8dp
tracking compensation, on the lower bezel outside the glass) + **`HH:mm`**
(Anonymous Pro Bold 20sp, 24-hour, verified 54.59dp advance against the 102dp
window with slack to spare; density-only scaling — the instrument does not
auto-shrink; font scale ≥1.3 clips are an honest overflow, never a shrunk digit).

- **Numerals are the absolute clock end in every lit state** (bolt lands t0;
  GANG's remaining-time-on-the-face idea is rejected — a shrinking duration
  reopens "the end slides" and breaks numeral invariance; remaining time lives
  on the hero card's remaining-line where it already exists).
- **Presence law:** the window is **blank** whenever no session has sealed —
  OFF, UNPROVEN, ARMED, FLOW, NO_READ, and every pre-seal row. A planned end
  printed without labels is the old strip's idle-ENDS lie in new metal.
  Placeholders (`--:--`) are banned outright. KANTE's dim-numerals rows for
  idle planned/frozen ends are superseded by this law.
  > **AMENDED (issue-#4 closer pass, Oct 30 — HOLLIS H-1, flagged for Ren's
  > countersignature, NOT silent):** this law deleted the one datum the
  > issue's acceptance explicitly protects — the session end readable at a
  > glance in the screenshotted **idle** state. The audit's own cure
  > ("label it planned or freeze the derivation") and this section's own
  > grammar ("printed **without labels** is the lie") both permit the
  > labelled form. The closer pass therefore restores KANTE's dim-numerals
  > row for idle: the window carries the dial's planned end at 55% ink
  > under a **PLANNED END** bezel engraving (the fold's `PLANNED_END`
  > window), in OFF / UNPROVEN / NO_READ / ARMED alike. A plan in plan's
  > grammar; the live-end grammar (full ink, END) is reserved for running
  > faces. If Ren strikes this amendment, the window returns to blank and
  > the acceptance line must be re-issued with it — the cell may not hold
  > both.
- **Alert numerals are banned** (numerals never take the alert material); the
  one exception-that-isn't: ENDING_SOON inverts the *whole window* (brass plate
  behind, measured pair, §5) — a block change, not a recolour.
- Count is **off the face** (FORM/UNA2-02 beats FUNKE's engraved `89`): its
  homes are the Shield header and the split LOCKED/PEEK imprint. OFF and
  TOTED-OFF are visually identical band faces (spoken/shown distinction lives
  in the refusal sentence + Shield page); the count belongs with its neighbours
  in the order.
- **The spoken set is closed** — 11 `contentDescription` sentences, one merged
  semantic node, numerals and END removed from the semantics tree, no live
  region, no stateDescription (LETTER §3, verbatim in the spec): OFF / OFF-toted
  / OFF-while-something-runs / UNPROVEN / ARMED / RUNNING / RUNNING-UNPROVEN /
  FLOW / FLOW-UNPROVEN / REVOKED-idle / REVOKED-running. Every sentence begins
  with its state claim; none flatters (no "everything is fine").
- **Banned on the face, by family:** every state label; BLOCK/APPS/TIME as
  headers; BIND/PROOF/PRINT/HELD/SEALED/POSITIVE/LEASE/WARRANT/TOTAL vocabulary;
  the count; ENDS in any form; countdowns; AM/PM; day words; placeholders;
  tooltips; brand/serial engraving; track-end letters; alert glyphs; the
  vocabulary of any other screen (no LEAVE, no totals).
- TalkBack merges the housing's node with the refusal sentence's node when the
  sentence shows (REL-TB-1/2) — the hearing user gets the same two-channel
  instrument the seeing user does.

## 5 · Colour & material (TON's table governs; DUNKEL's night anchors)

Binding law, computed WCAG throughout (full state×element×day×night table = TON's
report, adopted verbatim into this spec):

- **The bolt binds `inkControlFill()` in code, never a hardcoded hex.** Night
  thrown fill = cap `#4A423C` (1.96:1 on the recess — the bar's largest matte
  step); **cream `#FFE4CF` is never a fill** (15.8:1 floodlight); cream is grips
  and window ink only.
- **The orange is one:** `#D63E06`, mode-invariant, used as matte stripe + bed +
  socket paint. Night `#FF8A5C` is rejected (the exiled fourth orange; glows
  8.29:1) — and `alertLamp()` as coded re-imports it: deleted at merge (§8).
- **Window:** dark glass both modes — day glass `#2E2A26` / numerals `#FAF6F0`;
  night glass `#161311` / numerals `#FFE4CF`. (DUNKEL's proposal — night glass +
  cream extended to day by the stripe's invariance logic — is recorded as an
  open extension for TON/Ren ratification; the fallback is exactly this table.)
- **ENDING_SOON inversion is a hardcoded mode-invariant pair** — numerals
  `#252323` on brass `#E2BC39` (8.55:1, the table's only alert register); the
  window flips as one measured block. The mode-variant Material alert was the
  trap (day 4.29:1, night 5.80:1, and neither is the brightest thing in its
  face). This inversion is also the glance carrier for ENDING_SOON (§6).
- **The jewel rides in a bezel disc** (ink by day, cap by night), which rescues
  brass from its day camouflage (unset brass vs day glass was 1.21:1). MAGENTA
  follows the house split (day `#C81FA8`, night `#F04FCB`); BRASS is one value
  both modes (`#E2BC39`) — metal does not change at dusk. RUNNING carries a
  1dp-class **halo ring** in jewel tone (MASSSTAB/BLICK: hue alone at a 2.5dp
  dot failed BLUR — ARMED and RUNNING were one read until the ring gave the
  lit states a size difference).
- **Shadow belongs to the bolt, drawn inside the channel** (a contact rib, not
  elevation). TON's reassignment is ratified and DUNKEL completes it: at night
  the shadow is under the crush floor and worth nothing; night weight comes
  from **occlusion (bed covered) → keeper-collar overlap (4dp) → the proven-
  thrown rim step (`#807A7A`, night only, proven states only) → cream grips**,
  in that rank. Day gets the drawn contact shadow (1dp south line, no blur —
  Compose `.shadow()` is banned in this bar).
- **Salience law:** health is the only thing that lights; fault never lights;
  urgency floods exactly once, briefly, in brass (the inversion).
- **Never dim** (DUNKEL R4, hard floor in every mode and phase, including the
  seal shot and detention): the bed, the stripe, OFF socket paint, cream grips,
  window numerals, the proven-thrown rim step, and the jewel — steady, never
  pulsing, breathing, or fading. The XML colour files are not a source of
  truth; the table is.

## 6 · The glance gate (BLICK — the ship gate)

~~Every number below was measured on real renders~~ **STRUCK (KRALL K-5 /
HOLLIS H-4 / VEX V-9, closer pass Oct 30): no number in this section was ever
measured on a render, and two were not correct arithmetic about this
instrument either (a 644dp² bed that exists in no geometry — the bed is the
bolt's 90×34 = 3,060dp² footprint — and a "worst case" priced as a bare brass
disc, a colour/element combination RUNNING never renders). The figures are
replaced below by model arithmetic computed in code over the elements that
actually render (`SealBarParams.offLuminousTotal` /
`jewelLuminousTotal`, pinned by `SealBarStateTest`) and by render-side pixel
measurement (`BoltRenderGateTest`). The human battery (cold reads, 1 AM
simulation, greyscale twins judged by people) has still never run; it is
dispositioned on the issue as owed, not claimed here.** **The Bolt ships
only if the battery passes; no battery, no swap.**

- **OFF vs RUNNING, pairwise, at 10% brightness in a dark room:** OFF's
  orange-region luminous total ≥ 3× RUNNING's lamp total. As coded (WERK v0,
  no bed), the totals were **0.00 vs 42.6 — a fail by the whole amount.** As
  built now, computed over rendered elements: OFF = bed 3,060dp² ×
  rel-lum(#D63E06) + painted socket 64dp² × the same ≈ **554 units**;
  RUNNING (night, the brighter mode) = magenta disc 63.6dp² ×
  rel-lum(#F04FCB) + halo ring 62.8dp² × the same ≈ **35.9 units** — a
  ≈15× model ratio, and `BoltRenderGateTest` measures the same law in
  pixels on real renders of the composable. The gate is only passable from
  both sides — OFF gains area, RUNNING loses lamp — which is why the jewel
  cap is a law, not a style: **jewel luminous total ≤ 41.9 proxy units;
  ≤9dp diameter is the enforceable proxy.** The budget ships as a named
  constant in code, and the totals are now computed from it and the
  geometry — no longer a constant asserted equal to itself (K-5).
- **OFF's peripheral read is ONE load-bearing cue** — the exposed orange bed —
  plus foveal confirmations. Protective rule: any future change cutting the
  exposed bed by >20% re-triggers the whole battery.
- **ENDING_SOON as coded FAILS its pairwise test** (hue-only numerals, a 4dp
  stripe, a 5dp backoff, a 1dp halo — none survive blur). The carrier is the
  full window-block inversion (§5): a different-shaped mass entering the lit
  cluster. The 2dp SEATED sink is struck from the cue inventory (1.8 arcmin —
  sub-perceptual; it stays as feel, §7.1, never as evidence).
- **CAUGHT passes only under the binary-bucket rule** (≥95% of cold reads sort
  it "not protected") plus the detent contact cue and the full-shut ink-flip —
  never as an exact-position read.
  > **FLAGGED (HOLLIS H-2, closer pass):** this bucket means the glance gate
  > certifies "OFF cannot be told from CAUGHT" while the issue's acceptance
  > says "OFF must stay unmistakable". Those are different claims. The
  > closer does not resolve them: the conflict is escalated on the issue
  > for Ren's ruling (amend the acceptance openly, or give OFF a beat-1
  > carrier CAUGHT does not share). No summary may say "OFF unmistakable"
  > until he rules. FALZ §2's beat 1 is likewise renamed by the facts:
  > it carries **not-protected-ness**, not OFF-ness (KRALL K-6).
- **Screenshot law:** the acceptance frame is a side-by-side OFF/RUNNING pair,
  Night, with greyscale twins. A lone RUNNING shot proves nothing — OFF is the
  state that can hurt him, and the contrast is the proof. Battery: T1–T9
  (BLICK §5) — cold reads, pairwise forced choice, 1 AM simulation (10%
  brightness, dark room, no re-aiming), greyscale twin, OFF-grep, the count
  question, the two-channel parity check, paired-screenshot adjacency, and the
  final cold-read-of-record.

## 7 · Feel — motion, sound, touch

### 7.1 Motion (GANG, re-anchored by KLANG)

- **The flip law, scoped:** warrant *verdicts* land in one frame (a verdict is a
  fact; a 100ms fade is 6 frames of lying). Travel >30dp is never one frame —
  the threshold law (≤2dp step / 2–10dp 2 frames / >10dp ≥3 frames + 20ms) is
  tabled for Deck-wide adoption.
- **The seal act is depicted at t0.** The bolt depicts the seal (t0), not the
  ground's arrival (t=400); the throw therefore runs *inside the Throw sound's
  own ≤85ms window*: keyframed travel OPEN→THROWN over **50ms / 3 frames**
  (0 / 24 / 62 / 94dp on the reference, scaled to the responsive channel), the
  pin snapping 1 frame after landing (at the +62ms haptic seat inside KLANG's
  buffer), then a **2dp SEATED sink, stepped rigidly, no spring** — and
  stillness. Loops, breathing, and idle animation are banned outright.
- **The ending-soon flag rises once** (150ms EasingA, on entry only) and falls
  as a snap (retreat is a fact, not an event). The flag never re-fires within a
  session. The window does not tick, pulse, or count down — it holds the
  absolute end, still.
- Mid-session degradation (lamp drop) lands as a 1-frame pose-true STEP or a
  2dp snap — never a fade (a fade reads as *failing*, present tense; the truth
  is *fallen*, past tense).
- Reduced motion collapses the seal sequence to the final pose at the anchor
  frame (the window reading is untouched). `prefersReducedMotion` re-anchors
  per-frame; plate and glow never desync.
- As coded, WERK's 260ms EasingA glide is **retired** by this choreography (§8).

### 7.2 Sound (KLANG — the composable is silent)

- **Zero new sounds, zero sound calls in the composable.** The seal Throw's
  rendered buffer already contains a layer literally named THE BOLT (noise
  rattle +30–85ms, haptic seat +62ms) — bit-identical, hash-guarded
  (`SEAL_BUFFER_GOLDEN_SHA256`), never re-rendered for this design. The Bolt's
  picture is timed to land inside sound that already exists.
- **Silence rulings:** ending-soon flag = silence ( brass inversion is the
  alarm; no instant exists to mark); idle warrant flips = silence both
  directions (no actor acted; a discovery clunk is a jump scare); staleness =
  silence (its discovery is the Deck's knock, once, at the Deck's threshold —
  never from the bar).
- **Press voices live at destinations** (navigateTo's detent 540, the Desk
  Clock toggle's latch). Exactly one new call is specified: the housing's fix
  route (§7.3) speaks PARK's quietest seat knock (10ms, 0.10 day / 0.06 night,
  300Hz class) in the host/VM handler on DOWN. A locked press speaks the house
  refusal knock (6, 0.2, 320), throttled 250ms.
- No reverse Throw may ever be created (the house has no unseal sound; building
  one is how the bar starts lying about unsealing being an event).

### 7.3 Touch (HAUT — specified amendment, not v1 behaviour)

WERK built the instrument non-interactive (honest). If the housing becomes a
door, HAUT's design is the binding spec: **the finger moves the case; proof
moves the bolt** — a 1dp whole-housing dip (PRESS_MS 90 in, ≤150ms return,
EasingA), gated by `navigationLocked()` re-read synchronously at DOWN *and* UP
via a live provider (a stale captured gate is the exact failure mode the Deck
has already shipped elsewhere). A gated press is **totally dead** — no dip, no
tick, no semantics change. Single-voice rule: at most one sound per press, from
the destination or the knock, never both. HAUT's 10-point NEVER list (no
haptic invented for the bar — the house haptic table is closed; no press may
move the bolt, jewel, or window; no long-press; no ripple; …) ships verbatim
in his report.

*Flagged upward, undecided in-cell:* if the housing becomes a door, HAUT argues
the refusal sentence below should be demoted to print — two doors, one lack.
That is a Deck-level decision for Ren, not this cell.

*Dissent recorded:* HAUT holds a locked press should be totally silent (a man
pressing a locked door hears the door, not an alarm); KLANG's refusal-knock
ruling governs (house consistency + the Deck's existing gated-press language),
with HAUT's objection on record.

## 8 · Implementation — what exists, what the merge pass owes

**Built (WERK, DECLARED, uncompiled, this clone's working tree):**
`SealBar.kt` (new — SealBar composable, SealBarState incl. honest
RUNNING_UNPROVEN, pure `sealBarStateFor` / `endsAtNumeralsFor`, `SealBarParams`);
`TactileDeckScreen.kt` (ProofStripBand call replaced; seam computation removed);
`MonolithCustomComponents.kt` (ProofStripBand + StripCell + StripTones DELETED;
`sealStateForBand` left as dead code for the final pass to remove; seam helpers
KEPT unrendered for the sentence slot, imprint, and tests); `SealBarStateTest.kt`
(new). BIND/PROOF render nowhere on the Deck (grep-verified). Built height 53dp —
the strip's old address, honoured deliberately.

**Amendment backlog (merge pass, owner-tagged):**
1. **FEILE §11, all 12 lines** — housing 53→60dp, bolt 90×34, responsive channel
   (channelW = housingW − 156, detents as fractions), CAUGHT 46%→⅓, keeper
   socket + collar, pin/notch machining (pin gated on THROWN), jewel bezel,
   window interior, END per §4, ending-soon backoff DELETED (see dissent below).
2. **TON's table** applied to SealBarParams token-by-token (incl. window pair,
   inversion pair, rim step, shadow rib); `alertLamp()` and the `#FF8A5C` /
   lamp-fill imports deleted; `stripeOrange = #D63E06` invariant.
3. **DUNKEL's bed + socket paint + hairlines** rendered (bed → collar → socket
   → plate → rim, his render order); jewel ≤9dp enforced; the ≤41.9 budget as
   a named constant + token test.
4. **GANG/KLANG choreography** replaces the 260ms glide (throw keyframes,
   pin-at-seat, stepped sink, flag rise/snap).
5. **LETTER's spoken set + semantics law** (11 descriptions, merged node,
   numerals/END out of the tree, REL-TB merge) and banned-words sweep as a
   test.
6. **KANTE's matrices as derivation tests** — every row of §2/§3/§4 of his
   report, read against the fold; the §7 grep gates (boltStateFor purity).
7. **KLANG's one new call** (fix-route knock, host handler only) — only with
   §7.3 if Ren rules the housing a door.

**BLOCKING input charges (merge cannot ship the finished Bolt without them):**
- **B-C1 (KANTE):** the host fold needs a `shieldStale` input — without it,
  there is no honest full-strength running state while the shield gap stands.
- **B-C2 (KANTE):** the Bolt needs the finish/detention input, or it drops to
  idle up to ~1.8s before a release is proven — a lie in the kind direction.
- B-C3/B-C4 stand as the closed input contract and the frozen-verdict limit
  (documented in WERK's lie register; no fix exists — the face's honesty is
  bounded by them, and §2 says so on the record).

## 9 · Audit disposition (SALZ C7 — answered, not dodged)

AUDIT-deck §3 evicted this strip (seam → PARK; sentence to a × destination;
top strip to refuse-with-repair). This design **does not comply silently and
does not ignore it**: issue #4 is Sarthak's direct order for this surface,
issued after the audit, and it supersedes the eviction for the Deck's pinned
band. Where this design *exceeds* §3: the seam stamps are **de-rendered
from every Deck surface** (corrected by the closer pass, KRALL K-11 — the
original wording here, "deleted outright", was false: `seamMarksFor`
survives in `MonolithCustomComponents.kt`, test-pinned, as the wire's
grade record for the sentence slot and imprint; it renders nowhere on the
Deck). They are not moved to PARK — posology stamps on a parking page
would re-import the telemetry vocabulary §3 itself wanted off the Deck. Where it agrees with §3:
the refusal sentence stays (it is below the band, in the sentence slot, and is
the JAMMED face's second channel), and nothing §3 protected is weakened.
**Flagged for Ren's countersignature** — a cell does not overrule an audit on
its own authority; it states the disposition and signs it.

HEADER-BAND-AUDIT cross-check: the Bolt carries no header-band vocabulary; the
count's exile (§4) is consistent with UNA2-02's own migration.

## 10 · Acceptance (SALZ's 21, mapped)

SALZ's 21 acceptance tests bind the merge, including: the one-derivation test
(one fold, grep-enforced); the both-directions disagreement test (frozen ON
standing mid-session; live OFF overriding instantly); mid-session revocation
(the JAMMED face + sentence, same frame); the idle-tense test (no hypothetical
end in fact grammar — §4 as amended: the planned end in **plan** grammar,
labelled, is not a hypothetical in fact grammar); the 5-second cold test; the
1 AM test (§6 battery); TalkBack parity (§4); the rent test (60dp buys a state
change, a threshold, and an evidence channel the 53dp strip never carried —
**HOLLIS H-7's CONDITIONAL mark is discharged for the evidence channel by
the closer pass: the fold now exists in code**; the rent claim for the
glance channel remains conditional on the human battery); and the
audit-disposition test (§9 above). BLICK's T1–T9 (§6) run alongside;
a failure in either suite is a failure of the design, not of the test.

## 11 · Rulings & dissents of record (lead adjudications)

- **Instrument:** FORM's Bolt semantics on WERK's SealBar chassis, finished to
  FEILE's 60dp geometry. FUNKE's MESH is the documented alternative (racks +
  shear pin + marker tooth) — not built; his hard laws (one-frame verdicts,
  unproven never drawn engaged, live OFF overrides frozen instantly, FLOW
  shows no fake end) are adopted into §2/§3.
- **Threshold:** 300s (§3.1) over FUNKE's fraction rule.
- **Ending-soon pose:** the bolt does NOT back off (FEILE + GANG: pose is
  truth; a backed-off bolt claims the seal is releasing). *Dissent:* DUNKEL
  would keep the 5dp backoff — it cracks the warning bed open (a 12.4-unit
  sliver in his own arithmetic) as the seal approaches release. Noted; the
  hierarchy passes without it (ENDING ≈72 > RUNNING ≤33.3), and pose-truth
  governs.
- **Ending-soon lamp:** jewel steps down to brass while the window inversion
  carries the warning (TON) over FEILE's "lamp unchanged" (dissent recorded —
  her position was consistency; TON's is that a lamp brightening to alert is a
  second alarm, and alarms in this house are singular).
- **Jewel assignment:** BRASS = proven idle, MAGENTA = running (TON/FORM/
  FEILE). *Alternative recorded:* DUNKEL would swap (brass for the lived-in
  running state) — inside budget either way; TON's assignment governs.
- **Seal timing:** the throw is anchored at t0 inside the Throw buffer's
  ≤85ms window (KLANG), with GANG's 50ms keyframed curve; GANG's t=400
  ground-step choreography is superseded on the anchor only. The bar's scheme
  tokens still step at t=400 with the ground (DUNKEL R2 — a bolt thrown in
  daylight, then the room goes dark; that order is correct).
- **Type scaling:** density-only; font scale never rescales the instrument
  (LETTER's ruling, lead-ratified — a bolt is a fixed-size object; the
  alternative, font-scaled geometry, breaks every detent fraction).
- **Press sound:** KLANG's seam (§7.2/§7.3) over HAUT's total silence
  (dissent recorded, §7.3).

## 12 · Mocks

MASSSTAB's seven hand-built SVGs (5 states day + OFF/RUNNING night) were drawn
on FORM's 68dp reference frame to audit the anatomy; the geometry of record is
FEILE's 60dp grid (§1/§3), and his defect register F1–F14 is dispositioned
line-by-line in his report (F1 arithmetic agreed the ⅓ law; F2/F3 pin fixes
adopted; F6 collar overlap adopted; F9 shutter tone adopted; F10/F11 contrast
fixes adopted — END-on-glass resolves via §4's bezel engraving). The RUNNING
exemplar's remaining-style numerals are corrected to a clock end per §4. The
SVG sources live in MASSSTAB's report on this issue; re-rendering them on the
60dp grid is part of the §8 backlog's render tests.

— **ANTLITZ**, cell lead, issue #4. DECLARED, uncompiled, awaiting Ren.
