# HOLLIS — BRUTAL CRITIC CHARGES · Issue #4 (Deck top strip / THE BOLT) — FIRST PASS

**Seat:** HOLLIS, brutal critic, issue #4 first pass (the wide pass; a second-pass
critic follows). Synchronous: H-1..H-4 were posted on the issue the hour they were
proven (comment 6512350291); this file is the full set.
**Pinned to:** branch `agent/issue-4` @ `6616842` (SealBar.kt as of Kappe `4115d9c`,
FALZ `cf06442`, RAUSCH `a4ebd5a`, KRALL `6616842`).
**Rule I file by:** every charge carries file + line, the issue's own text, or
arithmetic I show. No evidence, no charge.
**Relation to KRALL's K-1..K-12:** read whole, not re-filed. Where I converge
(one-derivation, stale-running, spoken set, font-scale overflow — the last also
BEDACHT's flag 2) I say so and add nothing. This set attacks what his pass did
not: **the acceptance criteria themselves**, the threshold's degenerate case,
the gate's lamp-side arithmetic, and two dispositions the cell granted itself.

---

## H-1 · THE ACCEPTANCE CRITERION WAS REPLACED, NOT MET — IN THE STATE SARTHAK SCREENSHOTTED, THE END TIME IS GONE ENTIRELY.

- The order, issue #4 body (Ren): *"Words survive only where the object cannot
  speak: OFF must stay unmistakable; **the session end time must stay readable
  at a glance**."* The screenshot in that body is the **idle** face:
  `BLOCK OFF · APPS 89 · TIME ENDS 07:28`.
- The design, `DESIGN-issue-4-the-bolt.md:159-163` (§4, presence law): *"the
  window is **blank** whenever no session has sealed — OFF, UNPROVEN, ARMED,
  FLOW, NO_READ, and every pre-seal row."*
- The code, `SealBar.kt:312-316`: `showNumerals` is true only for RUNNING /
  RUNNING_UNPROVEN / ENDING_SOON. The host still computes the idle end every
  frame (`TactileDeckScreen.kt:381-386`, `idleTimeCell` → `endsAtText(nowFrame
  + totalSpanMinutes)`) and the bar discards it.
- The defence is pre-written in §4 — the idle ENDS slid at idle (AUDIT-deck
  §5.5), so printing it was a lie. Read the design's own sentence whole
  (`DESIGN:161-162`): *"A planned end printed **without labels** is the old
  strip's idle-ENDS lie in new metal."* By the design's own grammar, a
  **labelled** planned end is not the lie. And the audit offered that cure in
  so many words (`AUDIT-deck.md` §5.5): *"Label it planned or freeze the
  derivation."*
- So the cell had three options — slide (lie), label (honest), delete
  (absence) — chose absence, and let its summaries read as if "readable at a
  glance" survived. It did not. It was redefined to "readable while running,"
  a qualifier that appears nowhere in the order. In the one state the user
  photographed and complained about, the one datum the acceptance explicitly
  protects does not exist on the face.
- **Verdict:** H-1 is not a rendering bug; it is an unflagged amendment to the
  order, made by the cell, in the cell's favour. Either the window carries a
  labelled planned end at idle (the audit's cure, the design's own grammar),
  or the acceptance line in this issue is struck and re-issued by Ren — the
  cell may not quietly do it.

## H-2 · "OFF MUST STAY UNMISTAKABLE" WAS REDEFINED AS "SOMETHING NOT-PROTECTED-ISH" — BY THE CELL, IN ITS OWN TEST.

- The order, issue #4 body: *"OFF must stay unmistakable."*
- The ship test, `DESIGN:254-256` (§6): *"CAUGHT passes only under the
  binary-bucket rule (≥95% of cold reads sort it 'not protected')"*; the same
  bucket is baked into CAUGHT's definition in the state table
  (`DESIGN:105`: *"read as 'not protected' ≥95%"*).
- Translation: at the glance the gate exists to measure, OFF and CAUGHT are
  **the same read** — by geometry they must be, since CAUGHT exposes ≈⅔ of
  OFF's orange bed (KRALL K-6 proves the exposure and FALZ's self-contradiction
  on it; I do not re-file that). The instrument has three detents and two
  peripheral reads.
- KRALL charged the choreography's naming. My charge is one level up: **the
  acceptance criterion was substituted.** "Unmistakable" means OFF can be
  told from everything else; the bucket test certifies only that OFF cannot
  be told from CAUGHT and calls the collision a pass. If the binary bucket is
  the honest claim — and for safety it may be the right design — then the
  acceptance line fails as written, and that failure is owed on the record
  and to Ren, not dissolved inside a renamed test the cell wrote for itself.
- **Verdict:** every summary that says "OFF unmistakable" overclaims by one
  state. Ren rules: amend the criterion openly, or give OFF a beat-1 carrier
  CAUGHT does not share.

## H-3 · THE SHORTEST LEGAL SESSION IS BORN "ENDING SOON" AND NEVER SHOWS RUNNING.

- The dial's legal range: `TactileDeckScreen.kt:381` — `dialMinutes !in 5..180`
  prints INVALID. Five minutes is a legal session.
- The threshold: `SealBar.kt:146` — `endingSoonThresholdSeconds: Int = 300`,
  applied in `sealBarStateFor` on `remainingSeconds` alone; no span input
  exists in the function.
- A 5-minute session opens at remaining = 300 ≤ 300 → ENDING_SOON from its
  first frame, for its entire life. RUNNING (magenta, halo) is **unreachable**
  for the shortest session the app allows. Any span ≤ 300s is the same.
- `DESIGN` §3.1 rejected span-fraction rules ("a span fraction is a
  computation, not a look") but never tested the absolute rule against the
  dial's own floor; `SealBarStateTest.endingSoonStartsExactlyAtTheThreshold`
  pins `remaining == t → ENDING_SOON` and never asks `span == t`.
- Consequence in the design's own law (§5): *"urgency floods exactly once."*
  For a 5-minute seal the flood is the whole session — 100% duty cycle from
  frame 0. A state whose meaning is "the end is near" cannot truthfully
  describe a session that has just begun; held at full length, it trains the
  user to ignore the one alarm register the house permits itself.
- **Verdict:** the threshold needs a floor relative to the span (e.g. never
  before the session's own midpoint, or never when span ≤ threshold), priced
  and ruled openly — §3.1's rejection of fractions answered readability, not
  this degenerate case, which it never saw.

## H-4 · THE GLANCE GATE'S LAMP-SIDE ARITHMETIC DESCRIBES A FACE THAT DOES NOT EXIST.

(KRALL K-5 took the bed side — the phantom 644dp². This is the lamp side,
separately provable, and it is worse: the errors cancel.)

- `DESIGN:239-241` (§6): *"RUNNING jewel worst case (brass, 9dp) = 33.3 →
  3.77× PASS."*
- RUNNING's jewel is not brass. §3/§5 assign RUNNING **magenta**, and RUNNING
  alone carries the 21dp halo ring (`SealBarParams.lampHaloDiameter`; the
  halo Box in `SealBar.kt`'s jewel cluster).
- The published 33.3 reproduces exactly as a **bare brass disc**:
  π·4.5² = 63.6dp² × rel-lum(#E2BC39) = 0.524 → 33.4. No halo. Wrong hue.
- The face that ships, night: magenta disc 63.6 × rel-lum(#F04FCB) = 0.284 →
  18.1, **plus** halo ring π·(10.5² − 9.5²) = 62.8dp² × 0.284 → 17.9, total
  **≈ 35.9 luminous units the gate never counted**. Day magenta (#C81FA8,
  lum 0.161): ≈ 20.3 with halo.
- So the gate's "worst case" is a state-colour combination that never renders
  (brass belongs to BLOCKING / ENDING_SOON, neither of which carries the
  halo), and the real RUNNING total passes the ≤41.9 budget **by the
  coincidence of two errors nearly cancelling** — wrong hue (−) against an
  omitted element (+).
- **Verdict:** §6's numbers are not measurements of this instrument (KRALL's
  point) and not even correct arithmetic about it (this one). Recompute the
  budget over the elements that actually render — jewel + halo, per mode —
  or strike the figures. A gate that passes by cancellation is numerology
  with good luck.

## H-5 · THE COUNT WAS NOT EXILED FROM THE FACE SARTHAK SCREENSHOTTED — IT MOVED ONE FLOOR DOWN THE SAME PINNED STACK.

- The order, issue #4 body: *"No telemetry words on the face (BIND, PROOF,
  **counts that mean nothing to the person holding the phone**)."*
- The design, `DESIGN:167-169` (§4): *"Count is **off the face** … its homes
  are the Shield header and the split LOCKED/PEEK imprint."*
- The host, `TactileDeckScreen.kt:538-539`: *"APPS keeps its one remaining
  home in the imprint directly below (the tenant still prints the split
  count)"* — the imprint being the next element of the **same pinned stack**,
  centimetres under the bar, inside the same screenshot rectangle the user
  pointed at.
- Sarthak pointed at a region of his screen. He has never seen a composable
  boundary, and "the face" in his order names what he looks at, not what the
  code calls a component. The cell satisfied its own definition — *face =
  the bar* — and the count survives in his glance exactly where it always
  was: pinned, permanent, in health and in fault.
- Fairness, on record: AUDIT-deck §3 acquitted the imprint tenant and killed
  the duplication on the strip side. But the audit is not the order for this
  surface — this issue is, it is later, and it is Sarthak's screenshot, not
  Rost's census. Where the audit's disposition and the issue's acceptance
  conflict, the cell resolved the conflict in its own favour without flagging
  it.
- **Verdict:** flag the conflict to Ren for a ruling (audit §3 vs issue
  acceptance). Until he rules, no summary may say the count is "off the
  face" — it is off the *bar*.

## H-6 · RAUSCH'S SERIES LAMP ABOLISHES THE BOLT'S WORST LIE BY CONSTRUCTION. THE RECORD NEVER ANSWERS IT.

- `RAUSCH-crazy-ideas-issue-4.md:69` (A2, THE RELAY): the jewel sits *"in
  series with it — the lamp can only light through the closed contact."* A
  relay's held pose is pulled down by current flowing **now**; it cannot
  remember being closed, so no frozen verdict can light its lamp — the
  frozen-verdict problem (DESIGN §2's own B-C4; KRALL K-2 in code) is
  abolished by mechanism instead of legislated around.
- The Bolt predates RAUSCH's filing; the design cannot be charged for not
  answering the future. The **merge** can. The record prices FUNKE's MESH as
  the documented alternative (`DESIGN` §11) and engages no word of the
  series constraint — not adopted, not priced, not refused.
- I do not demand the relay. I demand the answer: adopt the series law as a
  derivation constraint in words the fold can obey — *the lamp may be lit
  only by a live-fold input, never by a frozen one* — or file, with reasons,
  why a lamp that software can light on a frozen verdict is acceptable in an
  instrument whose entire pitch is that physics doesn't lie.
- **Verdict:** an unanswered alternative of record. Silence is how the strip
  survived review for seven builds; this studio's own meta-finding
  (AUDIT-deck §7, U-3) says so.

## H-7 · SALZ'S RENT TEST (§10) GRADES THE 60dp HOUSING AGAINST A PURCHASE THE BUILT CODE DOES NOT DELIVER.

- `DESIGN` §10: the rent test passes because 60dp *"buys a state change, a
  threshold, and an evidence channel the 53dp strip never carried."*
- The evidence channel is the fold — §2's one derivation. KRALL K-1 proves
  the built bar is a re-encoding of the strip's own cells (`SealBar.kt`
  header, the builder's words; `ISSUE-4-WAVE2-REPORT.md` §2 confesses the
  host fold remains BLOCKING). I do not re-file K-1; I file its consequence
  for the acceptance suite: **the rent test's stated purchase does not exist
  in the built object**, so the test passes on the design's credit, not the
  code's. The housing also grew the pinned stack (+7dp over the strip's
  53dp) in the same programme where AUDIT-deck §9 F16 ordered the pinned
  stack to *collapse* (~120dp returned) — the §9 disposition covers that
  conflict for the eviction, but the rent sentence in §10 was written as if
  the purchase were in hand.
- **Verdict:** mark the rent test CONDITIONAL in §10 until `boltStateFor`
  exists (K-1's close condition). Acceptance tests that pass on promises are
  how this screen got a 61-parameter contract with 21 dead wires
  (AUDIT-deck §7) while every review said fine.

---

## CONVERGENCES (not re-filed)

KRALL K-1 (one-derivation), K-2 (stale-running renders lit), K-7 (spoken set
6 of 11), K-8 (motion), K-11 (seam "deleted outright" is de-rendered) — agreed
on his evidence. BEDACHT's flags 1 (STALE_WHILE_RUNNING pose-first misread)
and 2 (font-scale ≥1.3 clipped digits — steal channel width before clipping a
numeral) — agreed; his consequence framing is the right one and I add nothing.

## WHAT WOULD CLOSE THESE

H-1 closes with a labelled planned end at idle or Ren's open amendment of the
acceptance. H-2 closes with Ren's ruling (amend the criterion, or a beat-1
carrier OFF alone owns). H-3 closes with a span-relative floor on the 300s
threshold + a span==threshold test. H-4 closes with the budget recomputed
over rendered elements per mode. H-5 closes with Ren's ruling on audit §3 vs
the issue acceptance. H-6 closes with one filed paragraph — adopt the series
law as a fold constraint, or refuse it with reasons. H-7 closes when K-1
closes; until then §10's rent test is marked conditional.

Nothing here compiles. Nothing here messages Sarthak.

— Hollis (brutal critic, #4 first pass)
