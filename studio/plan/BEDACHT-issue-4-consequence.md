# BEDACHT — Issue #4, THE BOLT: consequence & care pass

**Seat:** BEDACHT (thoughtful), dedicated-100 programme, flat under Ren.
**Lens, only lens:** for every choice — who it affects, what it costs them, and
what happens on their **worst day**, not their best one.
**Read before writing:** issue #4 + all comments (incl. FALZ's live wave-3
intentionality flags, the UX persona walk, RAUSCH's crazy-ideas), and
`DESIGN-issue-4-the-bolt.md` in full. Nothing compiled; nothing run.

---

## 0 · Who holds this phone, on the day that decides whether the Bolt works

One person: Sarthak, 19, NEET drop year, AIR-1 filter on everything, up at
03:30, study blocks all day. The Bolt's decisive readings do not happen in a
review session. They happen:

- at **03:30–05:00**, half-asleep, arming before the first block;
- at **peak craving**, mid-session, when he is actively looking for a loophole
  and will take any ambiguity as permission;
- at **session end**, willpower spent, when ENDING_SOON fires;
- on the **sick / broken-sleep / exam-eve day**, when everything is read at
  10% brightness, one-handed, through blur — BLICK's 1 AM simulation is not a
  test condition, it is the median condition.

Every charge below is priced for that reader. A state he misreads is not a UX
defect; the cost lands on his study day, and — the part nobody else in this
cell is charged with saying — on his trust in the one tool he asked to hold
him. A blocker that cries "held" when it isn't doesn't just fail open; it
teaches him the app is theatre, and theatre gets uninstalled.

## 1 · Charges (care gates — merge should not pass without an answer)

### B-1 · STALE_WHILE_RUNNING can be read as RUNNING — pose is the loud channel, and pose doesn't change
Spec §3: on mid-session proof loss the bolt stays THROWN + pin DROPPED; only
the jewel dies and the window shutters. The design's reasoning is sound
(pose = the seal act, a fact; lamp = proof, which fell). But consequence
runs the other way: **position is read before light, and at blur it is read
instead of light.** A tired glance that catches "bolt home, pin down" and
misses a 9dp dark lens has received the exact message RUNNING sends.
- **Who pays:** Sarthak, mid-session, on the day the OS killed the shield —
  i.e. precisely the day he most needs the truth.
- **Cost:** he sets the phone down believing he is protected, in the one
  state where he is not. Worse than OFF, because OFF at least accuses.
- **Ask (not a redesign):** add STALE_WHILE_RUNNING vs RUNNING to BLICK's
  pairwise battery under the same law CAUGHT already obeys — ≥95% of cold
  reads must sort it "not protected" (blur, greyscale, 10% brightness). If it
  fails, the fix must come from a channel as loud as pose (the shuttered
  window is the candidate — it is already a different-shaped mass), never
  from shrinking the claim. Until that test exists and passes, §6's gate
  certifies the two states that were already easy (OFF/RUNNING) and leaves
  the dangerous one unmeasured.

### B-2 · Font-scale clipping: "honest overflow" is honest to the instrument, not to the person
Spec §4: at font scale ≥1.3 the window's numerals clip — ruled "an honest
overflow, never a shrunk digit." Agreed that a shrunk digit lies about the
object. But a **clipped digit lies about the time**: `07:28` clipped can
present as a different, complete-looking reading, and the end time is one of
only two things the issue's acceptance says must stay readable at a glance.
- **Who pays:** the user on the day he (or a future user) runs large system
  type — an accessibility setting, i.e. the user who already pays more to
  read everything.
- **Cost:** a wrong end-time believed. He plans around a freedom that isn't
  coming, or thinks a session ended that hasn't.
- **Proposal, priced in the design's own currency:** the channel is already
  the one flexible element (§1: "all width growth goes to it, the bolt never
  grows"). Extend the law one step: at font scale ≥1.3 the **window widens
  by stealing channel width** (detents are fractions — travel geometry
  survives), and if width still runs out, drop the engraved `END` before a
  single digit clips. Digits are the cargo; the engraving is the label.

### B-3 · B-C1 / B-C2 are care gates, not merge debt
§8 lists the blocking input charges (no `shieldStale` input → no honest
full-strength RUNNING; no finish input → the Bolt drops to idle up to ~1.8s
before a release is proven, "a lie in the kind direction"). Filed as
implementation charges. Refiled here as consequence:
- The old strip was *distrusted text* — nobody believed `PROOF: POSITIVE`
  in their bones. The Bolt is built to be believed at a glance, in the dark,
  without reading. **A trusted object that lies costs more than a printout
  that lies**, because the glance that replaces reading also replaces
  checking. Ship the Bolt without B-C1/B-C2 and we have not removed the
  strip's lies; we have promoted them into a more believable medium.
- **Ask:** B-C1/B-C2 close before the Bolt replaces the strip on any build
  Sarthak carries — or the swap waits. The interim 50ms tween and the
  6-of-11 spoken descriptions (wave-2 report) are approximations and are
  named as such; these two are not approximations, they are wrong answers.

## 2 · Weighed and accepted (consequence priced, design stands)

- **CAUGHT at ⅓ + the ≥95% binary-bucket law.** The correct care posture:
  the instrument has two resting states and one accusation, and "sort of
  locked" is the single most expensive misread available to a blocker.
  The gate being a *ship* gate (no battery, no swap) is what care looks
  like when it's enforced instead of felt. Stands, with B-1 extending the
  same law to STALE.
- **OFF paid for in area, never light** (exposed `#D63E06` bed ≈125.7 vs
  jewel ≤41.9 luminous budget, ≥3×). On the worst day — dark room, 10%
  brightness — OFF is the state that can hurt him, and it is the state the
  design spends the most surface on. Correct priority, correctly inverted
  from every dashboard instinct. Note for the record: no green anywhere in
  this design, per his standing rule; the one orange `#D63E06` is
  mode-invariant matte paint. Compliant.
- **ENDING_SOON: pose never backs off; the brass window inversion carries
  it; the jewel steps *down*.** Priced: at minute −5:00 his willpower is at
  its daily minimum and a bolt that visibly *retreats* would read as the
  app itself letting go first. Pose-truth here is not pedantry, it is the
  app holding its nerve so he can hold his. DUNKEL's 5dp backoff dissent
  is respectfully opposed on care grounds, not just FEILE/GANG geometry.
  One residual (test, not redesign): BLICK should include a jewel-only crop
  of ENDING_SOON — brass is also ARMED's jewel, and a glance that catches
  only the lamp must not sort "idle, nothing running" while a session is
  live. The thrown pose + inverted window should carry it; prove it.
- **Blank window at idle; placeholders banned.** The old strip's idle
  `TIME ENDS 07:28` was a hypothetical in fact's grammar (FALZ flag 3,
  agreed in full). On a worst morning, a printed end he never armed invites
  him to plan a day around a session that does not exist. Blank is not
  missing information; blank is the truth. Stands.
- **Silence rulings (KLANG).** No sound for staleness, none for
  ending-soon, no reverse Throw ever. On the worst day, an alarm he can't
  act on is just shame with a waveform; the brass inversion is the one
  alarm and it is singular. Stands — with §3 below as the one open edge.
- **Non-interactive v1.** A bolt that does nothing when poked will, once,
  feel broken. Priced and accepted: a fake affordance on a trust instrument
  costs more than one moment of "is it frozen?", and HAUT's gated-door spec
  exists for if/when Ren rules it a door. What I ask is only that the first
  shipped frame not *invite* the poke — no grip, no handle affordance —
  which §1 already rules ("not a handle"). Stands.
- **DETENTION FAULT keeps magenta.** The refusal-to-open moment is the
  highest-frustration second in the app's life. The face stays in the
  running family and the *sentence below* carries the why — the object
  holds, the words explain. That division is exactly right for a man who
  must not be flattered or alarmed by the instrument itself. Stands.

## 3 · One open question, filed upward (not a charge)

**The spoken set has no live region (LETTER §3/§4).** For the seeing user
the two-channel parity is real. For the ears-only user — in bed, screen
dark, TalkBack on — a mid-session **revocation** (JAMMED) is never
announced; he discovers the loss only when he next lands on the node.
Silence was ruled to stop the bar chattering about quiet swaps, and that
ruling is right for swaps. A revocation is not a swap; it is the one event
whose whole content is "you are not protected anymore." **Question for
LETTER/Ren:** does JAMMED alone earn an assertive announcement, once, while
every other transition stays silent? I file it as a question because the
cost runs both ways — a blocker that speaks unprompted at 1 AM also has a
price — but the asymmetry belongs on the record: today, the user least
able to glance is the user last to know.

## 4 · On RAUSCH's wave-3 objects (wax seal, iris, muselet) — the novelty price

Filed because these are live on the issue as I write. Any replacement
object re-pays, in full: the BLICK battery, the binary-bucket cold reads,
the spoken set, the worst-day pricing in §0. That price is paid by
Sarthak at 03:30, not by the cell at the desk. Two consequence notes the
cell should carry into any judging:

- **The wax seal's OFF accuses.** Cracked wax is tamper-evidence: it says
  *someone broke this*. But the commonest OFF is innocent — he simply
  never armed, or a session ended cleanly. An instrument that greets an
  ordinary morning with the iconography of violation spends guilt he
  didn't earn, daily. The Bolt's OPEN bed is loud without accusing; that
  neutrality is a care property, not a lack of poetry.
- **The muselet's OFF celebrates** (cork out, pressure released) — the
  wrong register on the morning after a failed arm, and it makes *release*
  the delightful state in an app whose whole job is holding.

Novelty is not free and beauty is not the only axis: the Bolt's metaphor
(deadbolt, keeper, pin) is one he already owns from every door he has ever
locked. Whatever ships, its OFF must be unmistakable, unaccusing, and
uncelebrated.

## 5 · Who else pays (the second circle)

- **Ren / future maintainers:** the one-derivation law is a care decision
  for them too — one fold, one warrant read, grep-enforced purity means a
  future 2 AM fix cannot quietly reintroduce the strip's five-tenses-one-
  wire bug. The lie register shipping *with* the design (B-C3/B-C4's
  documented limits) is the part I most want preserved at merge: an
  instrument that publishes what it cannot prove can be trusted with what
  it claims.
- **The merge itself:** §8's backlog is long (geometry, TON's table, the
  keyframed throw, 11 sentences). Consequence of a partial merge is a
  Bolt that *looks* finished and isn't — the worst artifact this cell can
  produce. The ship gate is BLICK T1–T9 on real renders or nothing; I add
  B-1's stale-pairwise test to that gate and B-3's inputs to its entry
  conditions, and I ask the lead to keep the wave-2 report's honesty
  ("interim approximation, named as such") attached to anything DECLARED.

**Verdict:** the Bolt is the right object, designed by people who priced
most of the right days. My gates are narrow: prove STALE can't be misread
as RUNNING (B-1), never let a digit clip (B-2), and don't ship the trusted
object on unproven inputs (B-3). Answer those three and this design has
my signature, not just my silence.

Fonts throughout per his law (Sora / Atkinson Hyperlegible / Anonymous Pro
— the window's Anonymous Pro Bold numerals and the Atkinson engraving are
compliant). No green used or proposed. No AI-generated images — MASSSTAB's
mocks are hand-built SVG. Nothing compiled.

— Bedacht, thoughtful (dedicated-100, issue #4)
