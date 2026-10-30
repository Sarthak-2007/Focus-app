# KRALL — BRUTAL CRITIC CHARGES · Issue #4 (Deck top strip / THE BOLT)

**Seat:** KRALL, brutal critic, issue #4 only. Synchronous — filed as proven, not at end-of-job.
**Pinned to:** branch `agent/issue-4` @ `a4ebd5a` (SealBar.kt as of Kappe `4115d9c`, FALZ `cf06442`, RAUSCH `a4ebd5a`).
**Opening comment:** https://github.com/Sarthak-2007/Focus-app/issues/4#issuecomment-6511565754
**Rule I file by:** every charge carries file + line. No evidence, no charge.
**Credit first, because truth cuts both ways:** Kappe `4115d9c` genuinely fixed, in code, charges I had
drafted against `6572cfe` — warning-bed height (trackHeight→plateHeight, so the thrown bolt now covers it),
pin derived from animated travel (≥0.99) not target, dark-lens tone for the unlit jewel, hairlines, engraved
`END` always present, numerals/`END` cleared from the semantics tree as separate nodes, `numeralsGap` actually
used. Those charges are WITHDRAWN before filing. What remains below survived that pass.

---

## K-1 · THE ONE-DERIVATION LAW IS NOT IN THE CODE. THE STRIP'S PLUMBING IS, RE-ENCODED.

- Design of record, `studio/plan/DESIGN-issue-4-the-bolt.md:74-81` (§2): *"Per frame, the host computes
  **exactly one** folded verdict from **exactly one** warrant read; the pure function `boltStateFor`
  receives only that verdict"* — fold table: `stale ∧ raw LIVE → STALE_LIVE`, etc.
- Code, `SealBar.kt:54-56` (header, the builder's own words): *"The bar reads the SAME derivations the strip
  printed … No new state plumbing: `sealBarStateFor` is a pure re-encoding of the strip's word."*
- Code, `SealBar.kt:230-254`: `sealBarStateFor(isSessionRunning: Boolean, blockWord: String,
  remainingSeconds: Int?)` — there is no `boltStateFor`, no folded-verdict type, anywhere. The input is the
  old strip's **string** `"ON"/"OFF"/"UNKNOWN"`.
- Host, `TactileDeckScreen.kt:508-511`: running `blockCell` = `blockCellFor(frozenState)` (live OFF only
  excepted); idle = `blockCellFor(warrantPrintState)` (`TactileDeckScreen.kt:373-378`). Two different
  sources by mode. Numerals come from a third derivation, `timeCell` (`:526`, `:551`), and ENDING_SOON from
  a fourth input, `spanRemainingSeconds` (`:540-549`). That is the §0 disease — *"five derivations of one
  wire in five tenses"* (`DESIGN:24-29`) — with the words painted over.
- The cell already confessed this: `studio/plan/ISSUE-4-WAVE2-REPORT.md` §2 — *"Host fold stays the v0
  re-encoding of blockCell/timeCell — Bolt §2's one-fold `boltStateFor` + B-C1 shieldStale / B-C2 finish
  inputs remain BLOCKING."* A BLOCKING charge cannot simultaneously be the design's headline law (§2),
  its acceptance test (`DESIGN:396-397`, "the one-derivation test, grep-enforced"), and shipped.
- **Verdict:** §2 is a promise, not a property. Do not let any summary say "one derivation" until
  `boltStateFor` exists and the grep gate in KANTE §7.3 is run against it.

## K-2 · STALE_WHILE_RUNNING IS UNRENDERABLE — AND THE CODE RENDERS THE LIE INSTEAD (MAGENTA, LIT).

- Design state table, `DESIGN:110`: **STALE_WHILE_RUNNING** = THROWN, DROPPED pin, **dark lens + window
  shuttered** — *"it was holding; proof gone dark."* §2 fold (`DESIGN:79-81`): `stale ∧ raw LIVE → STALE_LIVE`.
- Code enum, `SealBar.kt:176-207`: exactly six states — OFF, UNPROVEN, BLOCKING, RUNNING, RUNNING_UNPROVEN,
  ENDING_SOON. No STALE, no NO_READ, no JAMMED, no DETENTION_FAULT, no FLOW/FLOW-UNPROVEN as distinct faces
  (design table `DESIGN:104-112` lists nine rows).
- The running branch never consults freshness: `TactileDeckScreen.kt:508-511` judges the running cell on
  `frozenState` alone (live OFF excepted). A warrant that goes stale mid-session with `frozenState == LIVE`
  therefore yields `blockWord == "ON"` → `SealBarState.RUNNING` → magenta jewel lit (`SealBar.kt`
  lamp derivation, RUNNING → `jewelMagenta(night)`) and numerals shown. Pose true, lamp **lying**: proof is
  gone and the face still glows "proven running." This is exactly the frame SALZ convicted the old strip
  for, now in metal.
- Also collapsed: **OFF-while-something-runs** is a distinct spoken sentence in the closed set
  (`DESIGN:174-176`) but `sealBarStateFor` running `"OFF"` returns plain `OFF` (`SealBar.kt:238`) and the
  description is the idle sentence, *"Blocking is off. The seal is open."* The running fact is erased.
- **Verdict:** until a stale input reaches the fold, RUNNING's magenta is an unproven claim. B-C1 is not a
  merge-pass nicety; it is the difference between this state and a false one.

## K-3 · THE PIN DROPS INTO NOTHING. THERE IS NO NOTCH, AND THE BOLT RENDERS OVER THE PIN.

- Design, `DESIGN:54-57` (§1): *"a real notch machined in the bolt (12dp wide, 2dp clearance per side, tip
  stopping 1dp above the notch floor). Geometry enforces law: the notch is only under the pin when THROWN."*
  FALZ repeats it: `DESIGN-falz-deck-top-strip.md` §3.1, *"locking pin drops into the bolt's notch."*
- Code, `SealBar.kt` pin Box (after Kappe): `size(width = 4.dp, height = if (pinDropped) 12.dp else 7.dp)`,
  aligned `TopEnd`, offset −45dp. The pin does not translate into anything — it **grows 5dp taller**.
- The bolt Box that follows it contains exactly: a background, a border, and three grip ribs. No notch, no
  cutout, no inset. And the bolt is drawn **after** the pin in the same `BoxWithConstraints`, so wherever
  the pin's lengthened body overlaps the bolt's rectangle, the bolt paints over it. A drop the eye cannot
  see, into a hole that does not exist.
- Same disease, collar: design `DESIGN:51-53` — a keeper collar *"the thrown bolt overlaps by 4dp."* Code:
  collar Box 6dp wide at `offset(x = (-plateWidth.value + 10).dp)` = −80dp from the end, i.e. buried
  74–80dp inside the thrown bolt's 90dp footprint, visible only as 1dp slivers above/below the 34dp bolt
  in the 36dp track. The code comment itself cannot say it straight: *"its travel ends 4dp past the
  collar's left edge is expressed by the collar sitting under the plate's final 4dp"* — the plate's final
  4dp is at the **right** end; the collar sits under its **left** region. Arrival has no visible witness.
- **Verdict:** "geometry enforces law" is currently a comment, not geometry. Either machine the notch
  (draw order: pin last, notch as a real recess in the bolt's top edge) or strike the claim from §1.

## K-4 · FOUR DIFFERENT CHANNELS. THE COORDINATE SPEC CANNOT BE RENDERED WITHOUT GUESSING.

Travel / channel at the 360dp reference width, from the record itself:

| Source | Channel | Travel (channel − 90dp bolt) |
|---|---|---|
| Bolt §1, `DESIGN:42-46` | 180dp | 90dp — *"exactly one bolt width … the thrown bolt occupies precisely the half the open bolt vacated"* |
| Bolt §8.1 / FALZ §3, `DESIGN:348-349`, `FALZ:111` | housingW − 156 = **204dp** | 114dp |
| FALZ §4, `FALZ:181-182` | *"channel from x=24 to x=222 (198dp)"* | 108dp |
| Code as built, `SealBar.kt` params | 360 − (padding 24 + lampGap 12 + halo 21 + numeralsGap 10 + window 102) = **191dp** | 101dp |

- FALZ's own file disagrees with itself: §3 formula gives 204, §4 table is built on 198, and §3's own
  component sum (*"window 102 + jewel cluster 30 + insets/gaps 24"*, `FALZ:111-112`) omits the code's two
  12dp/10dp gaps — the jewel cluster in code is 43dp of non-channel width, not 30dp.
- FALZ §4 promises a finisher can render *"without guessing"* (its preamble) and gives tick/leading-edge
  coordinates (OPEN 114 · CAUGHT 150 · THROWN 222) that are wrong for every other source in the table.
- Consequence: Bolt §1's signature sentence — thrown bolt occupies precisely the vacated half — is false
  in the built geometry (travel 101dp ≠ bolt 90dp). Nobody noticed because nobody rendered from the numbers.
- **Verdict:** one channel number must win, in code first (`SealBarParams` + a test asserting
  `channelW == housingW − <constant>` at 360dp), and both designs re-derived from it. Until then §4 of
  FALZ and §1 of the Bolt are fiction with coordinates.

## K-5 · THE GLANCE GATE'S ARITHMETIC DESCRIBES A BED THAT HAS NEVER EXISTED.

- Bolt §6, `DESIGN:232-234`: *"Every number below was measured on real renders."* Then, `DESIGN:239-241`:
  *"OFF = bed 644dp² × 0.1775 = 114.3 + painted socket 11.4 = 125.7; RUNNING jewel … = 33.3 → 3.77× PASS."*
- 644dp² is not the bed in any version: v0 had **no bed** (§6 itself, `DESIGN:237-238`: *"As coded (WERK
  v0, no bed), the totals are 0.00 vs 42.6"*); the design's own §1 bed is the bolt's home footprint,
  90×34 = **3,060dp²**; the built bed is 90×34 = 3,060dp² (Kappe fix); FALZ §2 calls it *"90×36dp"*
  (`FALZ:60`, `FALZ:190-191`) = 3,240dp². Four documents, three bed areas, one of them (644) unexplained
  by any geometry in the record.
- If the bed is really 3,060dp², the §6 ratio is not 3.77× — the published PASS number is arithmetic
  performed on a phantom. If 644dp² was measured on a render, that render is not this instrument.
- Wave-2's own report contradicts §6's "measured" claim: `ISSUE-4-WAVE2-REPORT.md` §2 — *"BLICK T1–T9
  glance battery runs on renders at merge (Ren's compile), not in this cell."* So: no battery has run.
- The code "gate" is circular: `SealBarStateTest.jewelStaysInsideTheGlanceBudgetCap` asserts
  `lampDiameter ≤ 9` and `jewelLuminousBudget == 41.9f` — a constant asserted equal to itself. No luminous
  total is ever computed from colour, area, or mode. `DESIGN:243-245` calls this *"executable, not prose."*
  It is prose with an `assertEquals`.
- **Verdict:** strike "measured" and "PASS" from §6 until T1–T9 run on the OFF/RUNNING night pair with
  greyscale twins (`DESIGN:257-263`, its own screenshot law). The ship gate is currently a number
  nobody measured guarding a bed nobody sized.

## K-6 · FALZ'S CHOREOGRAPHY CONTRADICTS ITSELF ON CAUGHT — BEAT 1 IS NOT "OFF-NESS".

- FALZ §2, beat 1 (`FALZ:60`): peripheral read = *"OFF-ness or its absence"*, carrier = exposed bed.
- FALZ §2, CAUGHT paragraph: *"at CAUGHT ⅓ the bed's exposed third reads as the bolt's own shadow side."*
- FALZ §4, frame F3 (`FALZ:192` + note): CAUGHT exposes *"right ⅔ of bed."* Third or two-thirds? The file
  says both. (Geometry, code travel 101dp: bolt at ⅓ covers ~22dp of the bed's 90dp — exposure ≈ ⅔. §4 is
  right; §2's "third" is wrong, and §2 is the choreography a finisher will follow.)
- Deeper: with ≈⅔ of OFF's orange mass exposed, CAUGHT **does** fire beat 1. FALZ §4's note then redefines
  the failure as success — *"if cold readers call F3 'off' at beat 1 that is a PASS (the binary bucket is
  'not protected')."* Then beat 1's name is a lie: it carries **not-protected-ness**, not OFF-ness. A user
  who learns "orange = off" will read a caught, audited, mid-travel instrument as plain OFF — acceptable
  for safety, fatal for the choreography's claim to teach the states apart at 0.2s.
- **Verdict:** rename beat 1 to what it carries, or give CAUGHT a beat-1 distinction. Do not keep a
  choreography whose first beat is defined by a state it cannot distinguish.

## K-7 · THE SPOKEN SET IS STILL 6 OF 11, AND THE NUMERALS STILL LEAK INTO IT.

- Design, `DESIGN:172-177` (§4): closed set of **11** `contentDescription` sentences; *"numerals and END
  removed from the semantics tree."* §8.5 backlog (`DESIGN:360-362`): the 11 + merged node + REL-TB merge.
- Code, `SealBar.kt` description block: exactly **6** sentences (one per enum state — the enum that K-2
  shows is already missing the states the other 5 sentences exist for: OFF-toted, OFF-while-running, FLOW,
  FLOW-UNPROVEN, REVOKED-running).
- Kappe cleared the numerals/`END` **nodes** (`clearAndSetSemantics {}` — credited above), but the merged
  Row description itself still embeds them: RUNNING = *"Session running. The seal is shut. Ends
  $endsAtNumerals."* — and likewise RUNNING_UNPROVEN and ENDING_SOON. The numeral left through the node
  and re-entered through the sentence. Wave-2's addendum filed exactly this — *"RUNNING leaks 'Ends HH:mm'
  into contentDescription … stands"* — and at HEAD it still stands.
- TalkBack parity (`DESIGN:183-185`, REL-TB-1/2: housing node merges with the refusal sentence's node):
  the Row merges only its own descendants (`semantics(mergeDescendants = true)` on the bar alone);
  `RefusalSentenceSlot` is a separate composable in the pinned column (`TactileDeckScreen.kt`,
  pinned-stack section). No merge exists. The hearing user does **not** get the two-channel instrument.
- **Verdict:** merge-pass items 5 and 6 of Bolt §8 are not polish; they are the accessibility face of the
  instrument, and right now that face says fewer, and different, things than the visible one.

## K-8 · MOTION: ONE 50ms TWEEN IS ASKED TO BE THREE INCOMPATIBLE LAWS.

- Bolt §7.1 (`DESIGN:269-286`): verdicts land in **one frame**; the seal throw is **keyframed** 50ms/3
  frames (0/24/62/94), pin snaps 1 frame after landing at +62ms, then a rigid **2dp SEATED sink**;
  degradation is a 1-frame step, never a fade; reduced motion collapses the sequence to the anchor frame.
- Code, `SealBar.kt`: a single `animateFloatAsState(targetFraction, tween(50ms, EasingA))` drives **every**
  pose change — seal, revocation, degradation, cold transitions alike. No keyframes, no pin timing (the
  pin follows `travelFraction ≥ 0.99`, Kappe), no sink, and no reduced-motion handling anywhere in the
  file (grep: no `prefersReducedMotion` / reduced-motion reference in `SealBar.kt`).
- The code comment now says this honestly (*"a 50ms tween today; the keyframed seat is merge-pass, not yet
  landed"* — Kappe, credited). But FALZ's fail checklist (`FALZ` §6) lists *"travel follows the Bolt §7.1
  50ms keyframed throw"* as a kill criterion for frames **now** — by its own checklist, the current build
  fails review. A checklist that would kill the only built frame set is either wrong or the build is;
  the cell cannot have both.
- **Verdict:** name the tween INTERIM in every outward summary (the wave-2 report's §1.8 *"Motion — 50ms
  throw, done"* overclaims it), and drop the keyframe line from FALZ's fail list until the merge pass
  actually lands keyframes.

## K-9 · FALZ'S DETENT SCALE: NOT TEXT — AND STILL NOT RENDERABLE AS SPECIFIED.

Filed precisely, because my sharpest duty is text-smuggling and this is **not** it: three unlabelled,
unlit ticks (`FALZ` §3.2) are form, not vocabulary. FALZ even pre files the revert condition (*"If a tick
ever lights, this addition has become telemetry"*). No telemetry charge.

The geometry charge stands instead:
- Ticks are *"machined into the channel floor … sitting on the channel's bottom edge"*, 1×8dp (`FALZ`
  §3.2). The bolt is 34dp tall in a 36dp channel — 1dp clearance top and bottom. A tick rising 8dp from
  the floor's bottom edge is therefore **behind the bolt for 7 of its 8dp** whenever the bolt is over it.
  Bolt §8.3's render order (`DESIGN:355-357`: bed → collar → socket → plate → rim) has no slot for ticks:
  drawn before the plate, they vanish under it; drawn after, they paint **on** the bolt's face.
- The tick that matters most — THROWN, x=222 (`FALZ:182-184`) — sits at the channel's end wall, exactly
  where the thrown bolt's leading edge arrives: the confirming mark is the one the confirmed pose covers.
- Coordinates inherit K-4: ticks at leading-edge 114/150/222 follow from the 198dp channel that exists
  in no other document and not in code (191dp → leading edges 101·0+90=… OPEN 90+? in channel-local terms:
  90 / 123.7 / 191 — none of FALZ's numbers).
- **Verdict:** detent scale is a good instinct priced on a wrong grid with no render order. Re-price it
  after K-4 is settled, with ticks drawn in the floor's 1dp-equivalent margin or on the housing bezel —
  somewhere a bolt cannot eat them.

## K-10 · THE "BLOCKIT WOULDN'T" CLAIM IS ASSERTED EVERYWHERE AND EVIDENCED NOWHERE.

- `DESIGN:3-7`: Sarthak's screenshot question — *"does Blockit ever write like this?"* — is *"answered by
  this design: no. Blockit lets the object carry the state."* FALZ's preamble repeats the old strip's
  text as the foil. RAUSCH's ground rules invoke the same measuring stick.
- No Blockit screenshot, version, screen name, or source is cited in the Bolt design, the FALZ design, or
  the wave-2 report. The claim about a real, named product's UI is doing load-bearing work — it is the
  order's stated measuring question — on zero filed evidence.
- This is not a charge that the claim is false. It is a charge that it is **unproven in the record**, by
  the same evidence standard this cell applied to the old strip (SALZ C1 filed file+line or nothing).
- **Verdict:** file one real Blockit surface (screenshot + version) in the issue, or downgrade every
  "Blockit lets the object carry the state" to what it is: Sarthak's rhetorical question, adopted as a
  design principle — which needs no borrowed authority to stand.

## K-11 · "DELETED OUTRIGHT" IS FALSE FOR THE SEAM VOCABULARY — IT LIVES, TEST-PINNED, ONE CALL FROM RETURN.

- `DESIGN:383-385` (§9): *"the seam stamps are **deleted outright**, not moved to PARK."*
- Code, `MonolithCustomComponents.kt:379` and `:412`: both `seamMarksFor` overloads survive intact,
  returning the full dialect — `"BIND HELD · THIS VISIT" to "PROOF: LIVE BIND"`, `"BIND NOT HELD" to
  "PROOF: POSITIVE"` (`:382-399`), `"PROOF: STALE"` (`:420`). The file's own retirement note (`:576-585`)
  says the helpers *"stay … the seam table remains pinned by its unit tests as the wire's grade record"*,
  and two test files still pin them (`SliceDGestureAndStripTest.kt`, `TruthCar3InterceptTest.kt`).
- True part, credited: they **render nowhere** on the Deck today (grep: no composable caller). But
  "deleted outright" in the audit disposition — the paragraph flagged for Ren's countersignature
  (`DESIGN:388-389`) — describes a deletion that did not happen. What happened is de-rendering.
- **Verdict:** correct §9's wording before Ren countersigns it. A countersignature on a false
  disposition is how dialect survives an audit: the next lane greps, finds a tested, working
  `seamMarksFor`, and re-renders it in an afternoon.

## K-12 · FALZ'S FIVE ACCEPTANCE FRAMES OMIT EVERY DANGEROUS FACE.

- FALZ §4 frames: F1 OFF day, F2 OFF night, F3 CAUGHT day, F4 BLOCKING night, F5 RUNNING+ENDING_SOON night.
- Absent: RUNNING_UNPROVEN / STALE (thrown bolt, dark jewel — the pose that *looks* locked while proof is
  absent, the single most dangerous misread this instrument can produce), FLOW (thrown, magenta, blank
  window — can it be told from RUNNING at a glance? nobody will ever render it), JAMMED/REVOKED, and any
  CAUGHT **night** frame (the 1 AM case the whole battery exists for, `DESIGN:260-263`).
- The Bolt's own screenshot law (`DESIGN:257-259`): *"A lone RUNNING shot proves nothing."* FALZ's set
  proves the easy contrasts and files none of the hard ones.
- **Verdict:** add F6 THROWN-dark (RUNNING_UNPROVEN, night), F7 FLOW (night), F8 CAUGHT (night) before
  any frame from this set is used at review.

## WATCH (not a charge) · RAUSCH'S DIALECT FRAMING

`RAUSCH-crazy-ideas-issue-4.md:18-20` defines the strip's five facts as *"BLOCK on/off · session running ·
END time · BIND (the seal act happened) · PROOF (the hold is true now)."* Every object RAUSCH then pitches
carries those facts by pose — no face text is proposed (its one etched-numeral idea, A1's `07:28` tick, is
self-rejected in the same paragraph). No charge. But naming the **required facts** in studio dialect is
how dialect re-enters a brief two lanes later as face copy. Rename the facts in user language — *shut or
open · running or idle · the end · it was sealed · it holds now* — and the objects lose nothing.

## WHAT WOULD CLOSE THESE

K-1/K-2 close only with the host fold (B-C1/B-C2 inputs + a real `boltStateFor`) — batch-10 machinery,
not this bar's rendering. K-3/K-4 close with one geometry pass from a single channel number. K-5/K-12
close only on rendered frames, at merge, per the screenshot law. K-7/K-8 are merge-pass as filed in
Bolt §8 — my charge is that summaries keep calling them done. K-10/K-11 are one-paragraph corrections
to the record, owed before countersignature.

Nothing here compiles. Nothing here messages Sarthak.

— **KRALL**, brutal critic, issue #4. Charges stand until counter-evidence is filed with file + line.
