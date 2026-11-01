# CLOSER DISPOSITION — Issue #4 (THE BOLT, Deck top strip)

**Seat:** Closer under STURM-1 (surge closure captain). **Date:** Oct 30, 2026.
**Base:** branch `agent/issue-4` @ `33bd355` (remote tip at seating; last
commit 01:59 UTC — outside the 15-minute skip window; no PR existed).
**Method:** every charge re-derived from the code at the tip before any
verdict. Verdicts: **FIXED** (code + test on this branch) · **CORRECTED**
(record text amended, quoted) · **ESCALATED** (needs a ruling only Ren /
Sarthak can give — named, with the evidence) · **OWED** (cannot be
completed inside CI by any agent — named, with what would complete it).

The fresh-eyes + feel critic seat announced by STURM-1 is invited to
attack every FIXED line below; each names its file so the attack is cheap.

---

## The headline work

**The fold exists now.** `SealBar.kt`: `BoltInputs` (all fields derived
host-side from ONE warrant read), `BoltFace` (state + window), and the
pure `boltStateFor` — the bar's only derivation. Host
(`TactileDeckScreen.kt`): `blockCellFor`, `blockCell`, `idleBlockCell`
**deleted**; one fold block computes `BoltInputs` once per frame
(raw state, `WatchStore.printState`, `warrantStaleForPrint` — all over
the same `deckWarrant` object) and passes the `BoltFace` to `SealBar`.
The strip's string word reaches this face nowhere. Truth table pinned
row-by-row in `SealBarStateTest` (precedence order). Render evidence in
`BoltRenderGateTest` (pixel measurements over cold compositions of the
real composable, day + night).

**The two stale laws were NOT merged** (binding warning): the fold
reads the warrant's 45 s monotonic print law only. The shield's 10 s
wall beat law is not an input to this face (see B-C1 below).

**#6 coordination:** the fold *replaces* the C5(b) host derivation
block (which had landed on this branch via the main merge) — it does
not coexist with it. C5(b)'s law survives as fold rows 2–4. The #6 lane
was not live on this host block at seating (no commit in the skip
window); this note is the coordination record Fassung priced.

---

## KRALL K-1…K-12

- **K-1 (one-derivation not in code) — FIXED.** See headline. The grep
  gate, run by hand at the closer pass: `SealBar.kt` contains no
  `WatchStore` import or call and no `seamMarksFor` call (the names
  survive only in comments stating the law); `TactileDeckScreen.kt`
  contains no `blockCell` derivation at all.
- **K-2 (STALE_WHILE_RUNNING unrenderable; stale renders lit) —
  FIXED.** New `SealBarState.STALE`: thrown, pin dropped, lamp dark,
  window **shuttered** (diagonal hatch drawn over the glass, no
  numerals). Fold row 3 fires on `readStale ∧ raw LIVE` *before* the
  unproven row — `SealBarStateTest.runningStaleLiveIsStaleNeverRunning`.
  Pixel proof: `BoltRenderGateTest.staleIsDistinctInPixels` (zero
  magenta; frame differs from RUNNING and from RUNNING_UNPROVEN;
  hatch crosses the window's left slice where no numeral reaches).
  Also cured in the same rows: OFF-while-running is now its own state
  (`OFF_WHILE_RUNNING`, own spoken sentence) instead of plain OFF.
- **K-3 (pin drops into nothing; bolt paints over pin) — FIXED.**
  A real notch is machined into the bolt's top edge (12dp recess,
  track-coloured, riding with the bolt, under the pin only when
  thrown), and the pin now renders **after** the bolt, so the drop is
  visible into the recess.
- **K-4 (four channels) — CORRECTED on the record.** The code's
  channel is layout-derived (the channel takes `weight(1f)`; all
  width growth goes to it — Fassung §5's acquittal) and at the 360dp
  reference it is **191dp, travel 101dp** (360 − 24 padding − 12
  lampGap − 21 halo − 10 numeralsGap − 102 window). §1's signature
  sentence ("travel = exactly one bolt width; the thrown bolt
  occupies precisely the half the open bolt vacated") is **false in
  the built geometry and is struck as a geometry law**: detents are
  fractions of travel (0 / ⅓ / 1), which is the only channel claim
  the code makes. FALZ §3/§4's 204dp/198dp figures and tick
  coordinates do not describe this instrument and are not to be
  rendered from; a FALZ correction note is appended to its file.
- **K-5 (glance gate arithmetic on a phantom bed; circular test) —
  FIXED as far as code can fix it.** §6's figures struck in the
  design (see the §6 amendment). The budget test no longer asserts a
  constant against itself: `SealBarParams.offLuminousTotal()` /
  `jewelLuminousTotal(state, night)` compute totals from the actual
  geometry and hues (bed 3,060dp²; RUNNING = magenta disc + halo
  ring, per mode), pinned in
  `SealBarStateTest.glanceGateArithmeticOverRenderedElements`, and
  `BoltRenderGateTest.glancePairNightMeasured` measures the same ≥3×
  law in pixels on renders. The **human** battery (cold reads at
  1 AM) remains OWED — see T1–T9 below.
- **K-6 (FALZ beat 1 misnamed) — CORRECTED.** Beat 1 carries
  not-protected-ness, not OFF-ness; flagged in DESIGN §6 and in the
  FALZ correction note. CAUGHT's measured exposure in the render
  gate (orange pixels between OFF and RUNNING, ≈⅔ of OFF) matches
  §4's geometry, not §2's "third".
- **K-7 (spoken set 6 of 11; numerals leak into descriptions) —
  FIXED (bar side).** Twelve exact sentences, one per fold state,
  numerals in **none** of them (the leak is closed by construction
  and asserted in `BoltRenderGateTest.spokenSetIsClosedAndNumeralFree`).
  Mapping to LETTER's 11: OFF ✓ · OFF-while-running ✓ (new) ·
  UNPROVEN ✓ · ARMED ✓ (BLOCKING) · RUNNING ✓ · RUNNING-UNPROVEN ✓ ·
  FLOW ✓ · FLOW-UNPROVEN ✓ · OFF-toted — *not bar-carried, by §4's own
  ruling* (its distinction lives in the refusal sentence + Shield
  page) · REVOKED-idle/running — *carried by the sentence slot*
  (`jamSentenceFor`, TactileDeckScreen) — plus NO_READ, STALE,
  ENDING_SOON, DETENTION_FAULT sentences the fold's new states owe.
  **REL-TB merge (housing node merges with the refusal sentence's
  node) remains OWED**: it is a Deck composition change across two
  pinned-stack elements, not a bar change; named here so it is not
  lost.
- **K-8 (one tween asked to be three laws) — PART-FIXED, remainder
  OWED with the interim named.** Landed: reduced-motion collapse
  (animator scale 0 → 0 ms snap, read explicitly in the composable)
  and the code comment + this record name the 50 ms tween **INTERIM**
  in every outward summary. Owed: the keyframed throw (0/24/62/94),
  pin-at-seat timing, the 2dp SEATED sink. FALZ's fail-list line is
  corrected in the FALZ note so the checklist no longer kills the
  only built frame set for a merge-pass item.
- **K-9 (detent scale not renderable as specified) — STRUCK as
  not-built.** The ticks were never built in code (grep: no tick in
  SealBar.kt); the charge priced a specification on a wrong grid
  (see K-4). No code change; the spec is not to be built until it is
  re-priced on the code's channel with a render order that survives
  the bolt — recorded in the FALZ note.
- **K-10 ("Blockit wouldn't" unevidenced) — ESCALATED.** No Blockit
  screenshot/version exists anywhere in this record (verified by
  grep across the branch docs at the closer pass). The claim is
  downgraded in the disposition record to what it is: Sarthak's
  rhetorical question, adopted as a design principle — it needs no
  borrowed authority. Filing a real Blockit surface is a browser/
  device task no closer can do from CI; it is Ren's to file or to
  strike the comparison from the design's preamble.
- **K-11 ("deleted outright" false for seam vocabulary) —
  CORRECTED.** DESIGN §9 amended (quoted in the design): the stamps
  are de-rendered from every Deck surface; `seamMarksFor` survives,
  test-pinned, as the wire's grade record. Wording fixed *before*
  any countersignature.
- **K-12 (FALZ frames omit every dangerous face) — FIXED in
  executable form.** `BoltRenderGateTest` renders and asserts the
  hard faces: STALE night, RUNNING_UNPROVEN night, FLOW night,
  CAUGHT night, OFF/RUNNING night pair, BLOCKING day+night,
  ENDING_SOON night. The human cold-read use of those frames is the
  OWED battery below.

## HOLLIS H-1…H-7

- **H-1 (acceptance criterion replaced: idle end deleted) — FIXED,
  on the record.** The idle planned end is restored as a **labelled**
  datum: fold window `PLANNED_END` prints the dial's planned end at
  55% ink under a **PLANNED END** bezel engraving, in every idle
  face (pixel-differential proof:
  `BoltRenderGateTest.idlePlannedEndRenders`). DESIGN §4's presence
  law is amended in the open (quoted amendment in the design),
  flagged for **Ren's countersignature**: if he strikes the
  amendment, the window returns to blank *and* the acceptance line
  must be re-issued — the cell holds neither silently.
- **H-2 ("OFF unmistakable" substituted by the binary bucket) —
  ESCALATED.** The conflict is real and is now flagged in DESIGN §6
  in so many words. Only Ren can amend Sarthak's acceptance line or
  order a beat-1 carrier CAUGHT does not share. Until he rules, no
  summary from this lane says "OFF unmistakable".
- **H-3 (shortest legal session born ENDING_SOON) — FIXED.**
  `endingSoonThresholdFor(span) = min(300, span/2)`; a 300 s span
  now opens as RUNNING and turns ENDING_SOON at its midpoint.
  Pinned: `shortestLegalSessionIsNotBornEndingSoon` +
  `endingSoonStartsExactlyAtTheThresholdForLongSpans`.
- **H-4 (lamp-side gate arithmetic describes a face that doesn't
  exist) — FIXED.** Same cure as K-5: the gate now computes over
  the elements that render (magenta disc + halo, per mode ≈35.9
  night units — matching Hollis's own recomputation), and §6's
  published figures are struck.
- **H-5 (count moved one floor down the same pinned stack) —
  ESCALATED.** Verified: the imprint directly below the bar still
  prints the split count (`imprintTenantText` in the same pinned
  stack). Audit §3 acquitted the imprint; the issue acceptance
  names "counts that mean nothing" on the face Sarthak
  screenshotted. The conflict is Ren's to rule; the fold does not
  read the count, and no summary from this lane says the count is
  "off the face" — it is off the *bar*.
- **H-6 (RAUSCH's series lamp unanswered) — FIXED as a derivation
  law.** Adopted, in words the fold obeys, now in `boltStateFor`'s
  KDoc and enforced by its rows: *the lamp may be lit only by
  live-fold inputs (print LIVE ∧ frozen LIVE, freshness-checked);
  the frozen verdict alone can never light it.* Pinned by
  `runningFrozenAloneNeverLightsTheLamp`. The relay mechanism
  itself is not adopted (the Bolt's lamp is software; the law is
  the honest form of the constraint for this instrument).
- **H-7 (rent test passes on a purchase the code didn't deliver) —
  DISCHARGED for the evidence channel.** The purchase (the fold)
  now exists in code; DESIGN §10 amended. The glance channel's rent
  remains conditional on the human battery (OWED below).

## VEX V-1…V-9

- **V-1 (MIP-02 max spec: presence claimed before fact; creep
  contradicts detent law and itself) — RULED, on the record.** The
  presence sequence is history (the file has since landed) and is
  not re-litigated. On substance: **proving creep is struck** — the
  Bolt's detent law stands (exactly two resting positions and one
  caught pose at ⅓; DESIGN §3/§11). MIP-02's two creep fraction
  sets (0.40/0.55/0.70 vs 0.55/0.70/0.85) are unreconciled in its
  file and neither is adopted; `studio/max-impl/issue-4-strip-max.md`
  is a proposal document, not a spec of record for this face.
- **V-2 (intentionality audit bound a merge pass from a comment;
  repeats the phantom bed figure) — CORRECTED by K-5's cure.** The
  "≈125.7" figure is struck wherever this pass controls the text
  (DESIGN §6); the audit file's repetitions (its lines 93, 295)
  are noted here as echoing a struck figure — the audit's seven
  merge conditions are superseded by this disposition where they
  overlap.
- **V-3 (bolt material keyed to target, not position) — FIXED.**
  `plateFill` / `plateGripInk` / night grips now derive from the
  animated `travelFraction` (`seatedNow` / `pinDropped`), the same
  arrival law Kappe landed for the pin.
- **V-4 (END tracking in sp, not dp) — FIXED.** The −0.8
  compensation is divided by the density's fontScale, making it
  the dp-constant LETTER specified at every font scale.
- **V-5 (commit 6572cfe claims a compile-and-execute nobody
  performed) — CORRECTED by this pass's evidence standard.** The
  claim stands in history and cannot be rewritten; it is answered
  by the CI verdict on this branch (see the closing comment),
  which is the first verdict this branch's test suite has actually
  been read against in this record.
- **V-6 (§9 countersignature missing) — ESCALATED.** Verified: no
  countersignature exists in the issue comments, branch docs, or
  commit messages at the closer pass. §9 as amended (K-11) plus
  H-1's amendment both await **Ren's countersignature**; two waves
  proceeded on the unratified override and that fact is now on the
  record in this file.
- **V-7 (Bedacht B-1/B-2/B-3 unanswered; B-2 contradicts LETTER's
  ruling) — ANSWERED.** B-1 (STALE pairwise at the ≥95% bar): the
  STALE face now exists and its pairwise pixel distinctness is
  gated (K-2); the ≥95% human bar is part of the OWED battery.
  B-2 (font-scale digit clipping vs density-only ruling): the
  conflict is real; the closer **does not silently pick a side** —
  LETTER's density-only law stands in code today (fixed-size
  instrument), and Bedacht's care charge is escalated with H-2/H-5
  for a ruling, because stealing channel width at font scale ≥1.3
  changes every detent fraction Bedacht's own B-1 depends on.
  B-3 (B-C1/B-C2 as care gates): B-C2 is discharged (detention
  input passed, fold row 1); B-C1 is answered in part — the
  warrant-stale STALE row landed — and its shield-beat form
  remains OWED (below), named as a care gate, not a nicety.
- **V-8 (grafts/rival objects unadjudicated) — RULED for this
  closure.** RAUSCH G1/G4 presuppose a JAMMED face: the bar now
  carries the revocation family as OFF_WHILE_RUNNING /
  RUNNING_UNPROVEN / STALE faces (the sentence slot carries the
  words); G1/G4 are **not built** and are parked, not adopted.
  The twelve RAUSCH objects, the scout strip, and MIP-02's Canvas
  re-render are **not adopted**; THE BOLT as amended by this
  disposition is the design of record. An unruled bench is closed
  by ruling, which this paragraph is.
- **V-9 (phantom 644 propagated through three documents) —
  CORRECTED.** Struck in DESIGN §6 (K-5). The intentionality
  audit's and MIP-02's repetitions are flagged in V-2/V-1 above;
  neither file's figure is a source for any future gate.

## FASSUNG (tech-exec) — priced cures, disposition

- §1 fold priced M — **LANDED** (headline). Zero new plumbing used:
  every input existed host-side, as priced.
- §2 DETENTION_FAULT priced S — **LANDED** (fold row 1;
  `finishState`/`heldFinish` were already host parameters).
- §2 STALE_LIVE — **LANDED in warrant form** (45 s law, shuttered
  window — the priced S after §1). The running shield-beat input
  (§3, priced S–M) is **OWED**: it needs the VM to expose
  `shieldBlockingStatus` to the Deck host, which touches the
  Shield face's derivation home; merging it into this closure
  would have meant editing another issue's surface unpriced. The
  two stale laws remain unmerged, per the binding warning.
- §2 JAMMED via RefusalCause refactor priced M — **NOT LANDED**;
  the bar-side faces (OFF_WHILE_RUNNING etc.) carry the state and
  the sentence slot carries the cause in words today. The refactor
  is owed if the JAMMED face is ever to be a bar state of its own.
- §4 keyframes / pin-at-seat — **OWED** (K-8). §5 pixel-sum glance
  test priced M — **LANDED** as `BoltRenderGateTest` (the §9
  wish, built). §6 sentences — **LANDED** (K-7). §7 housing-as-door
  — not ruled a door; untouched, correctly.

## Panel verdict + render gate T1–T9

- **Panel (Bar/Truth/Beauty NOT YET at seating; Feel/Day/Soul
  PASS):** a closer cannot seat its own panel. **ESCALATED** to
  STURM-1/Ren with this disposition as the panel's reading packet.
- **T1–T9:** the executable half is **LANDED** (`BoltRenderGateTest`:
  pairwise OFF/RUNNING night measured, greyscale-distinctness via
  pixel diff, exposure ladder, STALE shutter, planned-end
  differential, ending-soon inversion, spoken-set parity). The
  human half (cold reads by people, 1 AM simulation, the
  two-question cold read of record) is **OWED** — it requires
  human readers and a device; no agent in CI can run it and none
  should claim it. Per DESIGN §6's own law ("no battery, no
  swap"), the *swap* claim rests on the human battery; the code
  closure does not.

## Efficiency account (Sarthak's standing order)

Artefacts: this disposition; the fold (2 source files); the truth
table (15 tests); the render gate (5 tests, 20+ captured frames);
4 design-record amendments. Idling: GitHub API timeouts cost ~15
minutes of retries at seating (REST + GraphQL intermittently dead);
no work was duplicated around them — local git/code work proceeded
in parallel. Waste admitted: one abandoned local-clone inspection
pass duplicated in the mip02 clone before the shared clone's
worktree was used; the first CI-failure detail read (run 40993204985)
was never obtained — the API died first — so the pre-existing
failure's exact composition is named from the #166 floor reference
in the closing comment, not invented.

— **Closer, issue #4, under STURM-1.** Charges stand answered as
above until counter-evidence is filed with file + line.
