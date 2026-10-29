# AUDIT — DEEPENING-ROUND + SEVENTH-WORLD MACHINERY CLAIMS

*Una — Critics, consistency. Seated directly under Ren. Oct 29, 2026,
~21:50 IST. Method identical to AUDIT-spec-machinery-claims.md: every
machinery claim in the deepening round and the seventh world, verified
against the tree, file+line on both sides, verdicts TRUE /
FALSE-with-the-honest-price / UNVERIFIABLE-AS-STATED. Design taste is
not this desk's ground. No compilation; reading audit.*

*Files read whole: DEEPEN-noir.md, DEEPEN-signal.md, DEEPEN-temple.md,
DEEPEN-obsidian.md (landed 21:40 IST mid-seating; joined the desk the
hour it landed, per brief), PRESENTATION-maschine.md (M1–M7 prices
read as claims). Freya's PILL-AND-TIMES.md (restored whole, 623 lines)
is cited as shared ground truth where the deepenings cite her — her
§1.2, §2.1, §2.4, §3.1/§3.2/§3.5/§3.6 were re-read, not re-derived.*

**One global note before the verdicts.** Every line citation in these
five files was written against batch-7 trunk. The gate merge (Truth
Car 2 + record caps, DB v11) landed mid-round and shifted lines in
MonolithRepository.kt, MonolithModels.kt, MonolithViewModel.kt and
MainActivity.kt (Repository/Models/VM citations now land ~+40 lines
below the cited numbers). I re-checked every shifted citation at its
new address: **no citation failed on content.** The drift is recorded
once here, not charged per claim.

## TALLY

| World | Claims checked | TRUE | FALSE | Notes |
|---|---|---|---|---|
| NOIR | 9 | 8 | 1 (minor) | |
| SIGNAL | 8 | 7 (2 conditioned) | 1 (minor) | |
| TEMPLE | 6 | 6 | 0 | 1 self-filed estimate, not charged |
| MASCHINE | 7 prices + 11 spots | premises all TRUE | 3 — all in §7 | the weight of this audit |
| OBSIDIAN | 16 | 16 | 0 | the cleanest file of the round |
| DAWN | — | — | — | no deepening landed; nothing to read |

---

## NOIR — DEEPEN-noir.md

**TRUE:**

- **§1.5 — HB-1/HB-2's predicate "exactly as the truth car wrote
  it."** Lane builds/truth-2, MainActivity.kt:241:
  `val doorLocked = sealed || viewModel.navigationLocked()`, feeding
  the header's isSealed (:411) and the openPark() tap guard (:280).
  The same lines now stand in trunk (gate merge) at the same
  addresses. TRUE in the lane as claimed, and in trunk since.
- **§1.3 — TIME keeps ENDS HH:mm, derived, never stored.** Prior
  audit A-05 stands: ENDS = now + remainingSeconds
  (MonolithModels.kt span derivations). Not re-charged.
- **§7.2 — "the VM carries the cooldown."** Prior audit SIL-3 stands:
  peekCooldownRemainingSeconds (MonolithViewModel.kt:125, :2112,
  pre-merge addresses).
- **§7.4 — the frame-gauge "maps the existing span fraction."** The
  span derivations exist and a fraction is directly derivable:
  spanTargetSeconds / spanElapsedSeconds / spanRemainingSeconds
  (MonolithModels.kt:391–405, post-merge).
- **§5.1 / §5.4 / §7.1 / §7.3 — the Freya §2.1 citations.** Freya
  §2.1 (PILL-AND-TIMES.md:178–220) says what he cites: derivation
  pure from stored ends, never a counted tick; BREAK outranks PEEK;
  the pill's ✕ calls `recordPeekEnded`. TRUE as citations.
- **§5.3 / §3 — Freya §3 (GuardSettings columns + breakSeconds
  stamped at creation; joint migration 11→12).** Freya §3.1 (two
  columns, :362), §3.2 (the stamp, :395–396), §4.1 (migration
  11→12). TRUE as citations; the store does not exist in trunk and
  his text does not claim it does — conditioned claims, dependency
  named. TRUE-as-conditioned.
- **§1.2 — the unlit tier "measures 5.14:1; §9 N5."** The figure
  stands at PRESENTATION-noir.md:603 (`#8A76AD` on `#06030B`) and
  Salz verified the GAP set exact. TRUE by citation; contrast
  arithmetic is Salz's desk.
- **§2 — "the slide gesture his tap chose."** TRUE (Sarthak's tap
  picked the sliding cap; batch 5 shipped it).

**FALSE (minor) — N-F1, §1.1/§1.2: "a hub that displays nothing is
furniture."** The cut of the 8-dot hub is a lawful design choice.
The premise is not the trunk fact. The hub's eight wells display the
block count: wells fill with the count, joining into a solid bar at
count 4 and again at 8 (BlockitCoreViews.kt:916–919), and the code
says so in terms — "wells are print" (:967). Fremd's confusion was
legibility (he could not decode the wells), not nonexistence — the
same species as O-F3: a true design instinct carrying a false fact
about the shipped machine. **Honest price: none.** Strike "displays
nothing"; the sentence the tree supports is "a hub whose count a
tired man cannot read is furniture" — which is Fremd's actual charge,
and needs no invention.

**No verdict sought:** §5.5's idle clock is flagged for Ren, not
smuggled (ST-01, already ruled IN).

---

## SIGNAL — DEEPEN-signal.md

**TRUE:**

- **§2 — wheel physics "one (θ,ω) integrator, shared, shipped."**
  Settled in the spec audit (SlideGestureMath,
  BlockitCoreViews.kt:1309 ff.). TRUE.
- **§4 — the break sub-headline "derived from the same stored
  (remaining, lastTick) pair the machine already keeps (Freya
  §1.2)."** Freya §1.2 (PILL-AND-TIMES.md:64–103): countdown
  wall-derived from the stored pair by `breakRemainingAfter`
  (WallAccrual.kt:40–49 — verified at those exact lines tonight).
  TRUE.
- **§8 — disarm "three BANK notches at 15/30/45 (the shipped
  banking law)."** TRUE (60s hold, chambers bank at 15/30/45, token
  spent only at full 60.0 — his verdict, shipped).
- **§10 — "the VM ticker and the heartbeat already compute these
  boundaries."** Both halves verified: the Deck's minute ticker
  re-derives TIME every tick (prior audit UNA2-03); the heartbeat
  computes nextBoundaryMillis every reconcile pass and re-arms the
  chain from it (RoutineHeartbeat.kt, step 5 of the tick). TRUE.
- **§11 — the bounds (peek 0–30, 0 = OFF; break 0–60; defaults
  2/10; cooldown not editable).** Freya §3.5 (:444, :455) and §3.6
  (:471) say exactly this. TRUE as citation.
- **§13 item 15 — "Noir's desk verified my cure in code."** TRUE
  (plan/CURE-VERIFY-signal-temple.md: Theme.kt container mapping in
  both schemes; hero #121214 over ground #0E0E10).
- **§5 / §8 — the DOOR and PEEK plate "read the setting live from
  the machine's store (Freya §3)."** The store does not exist in
  trunk; it rides Freya's commissioned columns + the 11→12 joint
  migration. His text names the dependency and claims nothing about
  trunk. TRUE-as-conditioned.

**FALSE (minor) — S-F2, §2: the printed scale's cost — "the same
duration setter A2's keypad already uses — presentation only,
small."** A2 exists nowhere as machinery. PRESENTATION-signal.md:165
defines A2 as an unbuilt addition ("tap the headline and the
duration becomes typed digits on a square keypad sheet") and prices
A2 itself **MEDIUM — "one sheet composable + the existing duration
setter; ~150 lines + tests."** The signal lane contains no keypad
(grep over builds/signal: no keypad composable); trunk has none.
"Already uses" is false tense over an unbuilt sibling: the scale's
tap-to-set is presentation-only-small **only after A2's MEDIUM is
spent**; standing alone it carries the same setter work A2 prices.
**Honest price: small on top of A2; MEDIUM standalone — the same
MEDIUM, counted once, in whichever item lands first.** Nothing else
in §2 moves: the dial's death and the scale are design, and the
wheel physics he keeps are shipped as claimed.

---

## TEMPLE — DEEPEN-temple.md

**TRUE — a clean file:**

- **§7 — END BREAK: "the verb itself (zeroing the remaining break)
  does not exist in the tree; it is new machinery, named: one VM
  verb on the existing flip."** Verified both halves. No end/skip
  verb exists (Freya §1.2: "There is no skip/end-early verb"; the
  register's ST-02 concurs). The flip it would trigger does: the
  progress machine exits the break on its own when the stored pair
  runs out — break entry writes `isInSafeBreak = true`
  (MonolithRepository.kt:1844, post-merge; pre-merge entry
  :1760–1773) and the updater's break branch flips it back to false
  and starts the next block (:1803–1816; Freya §1.2 cites the flip
  at :1730–1750 pre-merge). TRUE, priced as he prices it.
- **§10 — ST-04: "the writer such a verb needs already exists in
  the tree wearing the tamper penalty's clothes."** The penalty
  writer stands in the progress machine: `penaltySecondsAdded =
  penaltyAdd` written on the session row (MonolithRepository.kt:1790
  post-merge; cited :1680 pre-merge — same write, merge drift).
  remainingSeconds honours it (MonolithModels.kt,
  effectiveTargetSeconds). TRUE.
- **§9.2 — "the heartbeat already computes the boundary every
  tick."** Same verification as Signal §10. TRUE.
- **§9.1 — "PILL-AND-TIMES §1.2 named the dead breakMinutes
  field."** Freya §1.2, final bullet: `StudyModePreset.breakMinutes`
  (MonolithModels.kt:60–69 — the enum stands at those lines tonight,
  breakMinutes field and all) "has no reader anywhere in the tree."
  TRUE.
- **§3 / §13 — the day cure "stands in code (Noir-verified,
  CURE-VERIFY §2)."** TRUE (Noir verified the dial tiers and slab
  #E3C184 + 7dp shadow at TactileDeckScreen.kt:521 in code; renders
  still owed, and his §1.4/§13 say so — a claim that carries its own
  limit is not a false one).

**Not charged:** §8's pill tablet contrast "≈ 9.6:1" is filed in his
own text as "a claim to be proven, not a proof." An estimate that
labels itself an estimate is honest filing; Salz's desk proves or
kills it at renders. §9.3/§5's idle desk clock is ST-01 dress,
ruled, no machinery claim made.

---

## MASCHINE — PRESENTATION-maschine.md (the seventh world)

**The M-prices — premises verified, prices stand as his judgment:**

- **M1 (one-shaft motion law, MEDIUM) — premise TRUE.** "One shared
  phase derivation (the Now Frame's sibling); no new machinery."
  The Now Frame exists: the session entity's single derivation set
  (effectiveTargetSeconds, remainingSeconds, the span derivations,
  MonolithModels.kt:335–405 post-merge) that every surface reads.
  A sibling phase derivation is presentation work over real ground.
- **M2 (drum counter, MEDIUM, presentation only) — premise TRUE.**
  Every reuse point he names exists: the Deck readout, the resting
  dial, the COUNT drums' source figures, the receipt № (stamped on
  the session row in the claim transaction — RC-04, verified in
  MonolithRepository.kt tonight).
- **M3 (works clock + cam window, SMALL) — TRUE.** "The facts
  exist, the tickers run": the cam window's fact is
  nextBoundaryMillis, computed every heartbeat tick
  (RoutineHeartbeat.kt, tick step 5); the works clock is device
  time. Nothing to build underneath. SMALL stands.
- **M4 (cam-drum editor, MEDIUM) — dependency TRUE.** Voss's
  routine-edit mechanism had not landed when he wrote (lane
  builds/routine-edit still open tonight; trunk's editor remains
  the ±30-minute steppers). He names the dependency in §10 instead
  of spending it. TRUE as stated.
- **M5 (stamping press, SMALL–MEDIUM) — TRUE-as-hedged.** "The
  setters exist or ride Freya's §3 columns" — the hedge is the
  claim, and it is accurate: what exists is the stepper setting in
  Voss's lane's keeping; the direct setters ride Freya §3 / the
  routine-edit car. The sentence survives because it says "or."
- **M6 (winding key / ST-04, machinery SMALL) — TRUE.** "The
  penalty write path exists; this is the same write by his hand" —
  the writer verified at MonolithRepository.kt:1790 (see Temple
  §10). Same write, new hand. SMALL stands.
- **M7 (works bell / ST-05, machinery SMALL) — TRUE-as-priced.**
  "The engine and the moments exist": TactileSoundEngine and its
  completion pulse exist (playCompletionPulse,
  TactileSoundEngine.kt:792); the heartbeat/shield path reaches
  endings (the finalise funnel, RoutineHeartbeat.kt tick steps
  1–4). What does not exist is the service-layer sound call — the
  SLEEP stand-down at RoutineHeartbeat.kt:124–131 is, in its own
  comment's words, "a plain deactivation," and the completion voice
  today lives in the UI layer (register ST-05). He prices exactly
  that call as the SMALL addition and names ST-05 as its car.
  TRUE as priced.

**Spot claims — TRUE:**

- **§2 D1 — "`GUARD 1/4 — ACCESSIBILITY`, the shipped sentence."**
  GuardLine format `GUARD {n}/4 — {NAME}` (GuardLine.kt:13–14);
  the lack's board name is ACCESSIBILITY (WatchStore.kt:73,
  counted = true). TRUE.
- **§2 D6 — PROOF cites A-01 correctly** (POSITIVE = directly
  observed signal only). TRUE. The `7 SHUT · 3 PEEK` on the plate
  is illustrative composition, not a claim.
- **§3 W3 — valve bounds "1–30, or 0 = blanked (Freya's bounds,
  §3)."** Freya §3.5: peekMinutes 0–30, 0 = peeks off. TRUE.
- **§3 W4 — Freya's "600-seconds-in-four-places."** TRUE: the
  constant (MonolithRepository.kt:97) + three literal 600s in the
  span derivations (MonolithModels.kt:391, :395, :398 post-merge;
  Freya cites :369/:373/:376 pre-merge).
- **§6 — ST-08's plumbing.** TRUE (prior audit A-08: the
  accessibility service performs GLOBAL_ACTION_BACK, then launches
  the intercept — the machine already places him back; his plate
  designs over real plumbing).
- **§1 — stop-wheel banking 15/30/45 + `N/1 WEEK`.** TRUE (shipped
  token law).
- **§11.4 / §11.10 — the desk clock's fabricated banner default;
  the dormant simulate-intercept wired to a hardcoded fake.**
  Both TRUE (prior audits' appendix sightings, re-confirmed as
  filed).

**§7 — the feel claims. Three FALSE, all under one preamble.**
§7 opens (PRESENTATION-maschine.md:396–398): the instrument is
spent "without one priced amendment… All voices below are existing
parameter sets at existing call sites." Against that sentence:

- **M-F1 — JAM: "two strikes 12 ms apart, repeated at the shipped
  jam pattern." FALSE.** The shipped jam is three strikes, not two,
  at 90 ms spacing, not 12: `repeat(3) { playSeatKnock(20, 1.0f,
  320f); if (strike < 2) delay(90) }` (MonolithViewModel.kt,
  the playJamHaptics lambda handed to ArmProvingController). No
  two-strike/12 ms pattern exists anywhere in the tree; the only 12
  near a knock is a 12 ms *duration* argument at an unrelated call
  site (MonolithViewModel.kt:1104) — a duration misread as a
  spacing is the likely birth of this figure. **Honest price:**
  printing the shipped pattern costs nothing. Keeping his pattern
  is a re-patterning of an existing voice — small presentation
  parameter work — but it is an amendment, and under his own
  preamble it must be priced and named as one (and the cascade's
  feel is Haut's ground; the amendment is hers to argue).
- **M-F2 — THROW travel: "segment pattern: strikes at 90 ms,
  70 ms, 50 ms — load gathering." FALSE.** No such segment pattern
  exists in the tree (searched ArmProvingController.kt,
  SessionSealTransition.kt, BlockitCoreViews.kt: no 90/70/50 strike
  sequence; the shipped slide travel and catch sounds are the
  slide's own, settled in batch 4). **Honest price:** as M-F1 — a
  new pattern on existing voices, small, lawful the moment it is
  filed as an amendment instead of a shipped fact.
- **M-F3 — THE PROVING: "one gear-mesh clunk per witnessed link:
  the seat knock re-pitched (200–240 Hz, short)." FALSE — the
  weightiest of the three.** The shipped cascade sounds *nothing*
  at the links: "sound fires at t=0 — its only arm-path call"
  (ArmProvingController.kt:29), and batch 6's law is that the seal
  sound lives only at t=0. There is no call site at a link to
  re-pitch at. This is not a pattern slip like M-F1/M-F2; it spends
  sound where a shipped law forbids spending it. **Honest price:**
  either the gears turn in light only (shipped law; the needles
  jump, the room stays quiet — his own §7 HOLD paragraph already
  argues motion is the machine's idle speech), or the clunks are
  filed as a priced amendment *against the cascade's sound law*
  for Ren and the panel to rule — they cannot ride in as an
  existing parameter set.

**Verified inside §7, for the record:** TURN's ladder — the used
pitched-detent range is attested at both ends (420 Hz in the
Round-0b contract comment, BlockitCoreViews.kt:229; 760 Hz at
MonolithViewModel.kt:858 and :1631; the engine coerces wider,
280–1480, TactileSoundEngine.kt:595 — the *used* ladder is what he
cites). ANNULMENT's "one latch-release voice (520 Hz)" exists:
playMechanicalLatch disengaged = 520f (TactileSoundEngine.kt:657).
BREAK's latch-release and FINISH's completion pulse are existing
voices (TactileSoundEngine.kt:642, :792); their *placement* at the
break boundaries and in the service layer is ST-03/ST-05's priced
machinery, and his text credits those cars — placement claimed as
design, existence claimed as fact, both accurate.

**Palette note:** his "zero-green audited" spot-check passed on the
listed values in my sampling; the full light audit is Salz's desk
and is not duplicated here.

---

## OBSIDIAN — DEEPEN-obsidian.md

**TRUE — sixteen checks, no charge.** The §0 standing corrections
were themselves re-verified first, because a deepening that opens
by correcting its own record earns or loses the desk's trust there:

- **§0 / O-F1:** trunk `LIGHT_ON_PRIMARY = 0xFFFBF1EC` at
  BlockitCoreViews.kt:70; the near-black value is his lane's. TRUE.
- **§0 / O-F3:** shipped sled mid-stop day #615B5A
  (ui/theme/Color.kt:20), night #4A423C (:41); #4A4038 is his
  lane's. TRUE (prior audit; re-cited correctly here).
- **§0 / O-F2 restated with the lease's real addresses:**
  `startPeekLease` / `recordPeekEnded` / the cooldown derivation
  stand at MonolithRepository.kt:1131 / :1145 / :1113 post-merge
  (cited :1091–1099 / :1105–1112 / :1073–1079 pre-merge — content
  exact, merge drift per the global note); enforcement by clock
  comparison at MonolithAccessibilityService.kt:209–213 and
  MonolithShieldService.kt:170–171. TRUE.
- **§2 baseline citations — all exact:** the instrument card
  (TactileDeckScreen.kt:515 clip, :522 border, RoundedCornerShape
  24.dp, surfaceBright); the rail's geometry (MonolithRailView.kt:
  26–28 — HEIGHT_DP 52, RADIUS_DP 16, MARGIN_DP 20); the FLOW chip
  (TactileDeckScreen.kt:659–661, 8.dp); the progress track (3.dp
  clips at :688/:695 inside his cited :683–712); the desk clock's
  slab (16.dp, RoundedCornerShape 12.dp, full NightColorPrimary —
  AirlockAndDisarmOverlays.kt, fill at :184 as cited).
- **§3.2 — the dial cannot lie, and his formula is the tree's:**
  `valueForAngle` (BlockitCoreViews.kt:524–530): `if (angle <
  117f) return 5; return (10 + ((angle − 126f) / 9f).roundToInt()
  * 5).coerceIn(10, 180)` — character-for-character the derivation
  he prints. The hero span formula stands at
  TactileDeckScreen.kt:321 as cited (`dialMinutes × blocks +
  (blocks − 1) × 10`, with the tree's own comment pricing it).
  TRUE.
- **§3.6 / §14 — ST-07's fact: the next routine boundary "computed
  every heartbeat (RoutineHeartbeat.kt:170–180) and printed
  nowhere."** The heartbeat derives nextBoundaryMillis every tick
  and rearms from it (tick step 5, in his cited range); no UI
  prints it. TRUE.
- **§4.4 — the break's precise count "derived from the same stored
  (remaining, lastTick) pair (`breakRemainingAfter`,
  WallAccrual.kt:40–49)."** Exact lines, exact function. The END
  BREAK pricing ("the flip already exists; the verb is the
  machinery, priced small by the register") matches Temple §7 and
  ST-02 — three files, one fact, no drift between them. TRUE.
- **§4.5 — "the tamper penalty's writer is the tree's one
  clock-grower, Repository:1680."** The write stands at :1790
  post-merge (merge drift); it is the only writer that grows a
  running session's clock. TRUE.
- **§4.6 — WITHIN from frozen stamps (MonolithRepository.kt:
  1443–1453 pre-merge).** TRUE (prior audit RC-03 refinement: the
  routine rows' name and window come from the row's frozen
  profileName and stamps; his citation carries the refinement's
  form, as he says it does).
- **§5.1 — counts "re-derived per change, never stored
  (ShieldMatrixScreen.kt:106, :150)."** The counts are computed
  values in the composable; the SHUT • PEEK suffix prints at :150
  as cited. TRUE.
- **§5.2 — the cap: 84dp slot; the throw on DURATION_260
  (MonolithCurves.kt:26).** DURATION_260 stands in the constants
  block at that address. TRUE.
- **§5.3 — the DURATIONS machinery as Freya §3 + Truth Car 3:**
  two columns on GuardSettingsEntity (the entity stands at
  MonolithModels.kt:705 post-merge, cited :669 pre-merge — and it
  has no peek/break columns tonight, as his design assumes);
  breakSeconds stamped at creation; defaults 2/10; bounds 0–30 /
  0–60 — all confirmed against Freya §3.1/§3.2/§3.5/§3.6 line by
  line. The four-sources defect he says the stamp cures is real
  (Repository:97 + MonolithModels.kt:391/:395/:398 post-merge).
  TRUE.
- **§9 — the desk clock's numerals at 96sp
  (AirlockAndDisarmOverlays.kt:164).** TRUE — and worth a line of
  record: his phase 2 never misstated this figure (the 104sp error
  was Signal's S-F1, not his), and §9 cites it clean. Salz's
  corrected luminances (0.81 shipped; ≈0.21 at his 55% cure) are
  carried as Salz's figures, attributed. TRUE as citation.
- **§11 — the pill's machinery, Freya's entire:** Route A's
  permission already declared (AndroidManifest.xml:11) and already
  watched (WatchStore.kt:77, counted = false); the derivation
  (§2.1) and never-list (§2.4) cited at their true addresses;
  BREAK outranks PEEK on the ground he states
  (MonolithAccessibilityService.kt:202–205 — PEEK-tier doors stand
  open lease-free during a break; Freya §1.2 concurs). The ✕'s
  verb: `recordPeekEnded`'s only caller chain is the VM's expiry
  ticker (MonolithViewModel.kt:855 → the verb → Repository, post-
  merge addresses; cited :838 / :2150–2160 pre-merge) — "today no
  hand can reach it" is TRUE. The draggable edge-snapping contract
  is the dead promise's own words in the VM (the start2MinutePeek
  doc comment). The SESSION face's number: the Now Frame's
  remainingSeconds (MonolithModels.kt:404–405 post-merge; Freya
  §2.1 cites :353–355 pre-merge, and he cites her address). TRUE.
- **§3.10 — the plinth's honest pricing:** "geometry constants in
  a unit-tested object; its tests re-derive at the merge. Priced
  as small geometry rework, **not** zero." A designer pricing his
  own change *up* against the easy claim. Recorded as the round's
  model sentence.

---

## DAWN — no deepening landed

No DEEPEN-dawn.md exists on disk at this seating's close. Nothing
new to audit. The standing item from the spec audit is unchanged:
**DAWN-2 (Salz, S-grade)** — the pin marks priced "zero machinery"
on storage trunk doesn't have (BlockedAppEntity stores ONE tier,
MonolithModels.kt, blockTier; resolvedTier is its parse; provenance
stored nowhere) — still awaits Dawn's reprice-or-withdraw when her
deepening lands. It joins this desk the hour it lands, on the same
terms Obsidian's did.

## WHAT THIS MEANS FOR JUDGING

Five files leaned on; four stand as written. The round's machinery
load is carried by Freya's spec and the shipped tree, and Noir,
Temple and Obsidian cite both without a slipped figure — Obsidian's
deepening is the round's cleanest file, and it is the one that
opened by correcting its own record. Signal owes one cost line its
honest tense. Maschine's architecture prices (M1–M7) all stand —
its §7 feel chapter is the exception: three of its "existing"
voices are inventions, one of them against a shipped law, and
judging should read §7 as *proposed* feel (arguable, possibly
worth buying) rather than *shipped* feel (free). The preamble at
:396–398 is the sentence to fix; the world does not need it.

*— Una, Critics (consistency), Oct 29, 2026. Verdict copies filed in
each designer's inbox; closing letter to Ren carries the NEXT.*
