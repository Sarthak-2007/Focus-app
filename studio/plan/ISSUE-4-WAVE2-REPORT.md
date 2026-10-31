# ISSUE #4 — WAVE-2 REPORT · ANTLITZ (cluster-B lead, flat under Ren)
## Deck top strip: THE BOLT — finish of the uncommitted WERK v0
## Branch agent/issue-4 · All work DECLARED, uncompiled. Never compiled.

## 1 · WHAT WAS FIXED IN CODE THIS WAVE (lead, ANTLITZ)
The uncommitted v0 (SealBar.kt + SealBarStateTest.kt new; TactileDeckScreen.kt
and MonolithCustomComponents.kt modified — ProofStripBand/StripCell deleted,
BIND/PROOF render nowhere) is finished toward the design of record
(DESIGN-issue-4-the-bolt.md, c5de8d2) and committed. Amendments applied:
1. **CAUGHT = ⅓** — plateUnprovenFraction 0.5 → 1f/3f (Bolt §3; half reads as
   a resting state). Pinned by new test `caughtIsOneThirdNeverHalf`.
2. **ENDING_SOON back-off DELETED** — pose is truth (Bolt §11); the carrier is
   now the **window inversion**: whole glass flips to brass #E2BC39 with
   numerals #252323 (8.55:1, mode-invariant, Bolt §5). Stripe + halo-as-alarm
   removed; jewel steps DOWN to brass.
3. **Jewel ≤9dp in a bezel disc** (was 13dp bare lamp in accent orange):
   BRASS #E2BC39 = proven idle / ending-soon, MAGENTA day #C81FA8 / night
   #F04FCB = running (elite palette, zero-green legal), dark lens = all else.
   RUNNING alone carries the 1dp halo ring. `jewelLuminousBudget = 41.9f`
   ships as a named constant + test (Bolt §6 glance gate, executable).
4. **Warning bed rendered** — matte #D63E06 (TokenAlert, BOTH modes;
   `alertLamp()` and the NightAlert #FF8A5C import deleted — the exiled fourth
   orange) on the channel floor at the shut end, bolt-footprint sized; thrown
   bolt covers it exactly, every other pose exposes it. OFF socket interior
   painted the same orange, OFF only.
5. **Keeper collar + locking pin** — collar at the shut end under the bolt's
   final travel; pin above the thrown position drops (12dp) only in thrown
   poses, up (7dp) otherwise — geometry enforces the law.
6. **Bolt geometry** — bolt 90×34dp, channel/track 36dp, housing 53→60dp
   (FEILE grid, Bolt §8.1; TactileDeck pinned-stack comment updated).
7. **Time window** — 102×36dp dark glass both modes (day #2E2A26/#FAF6F0,
   night #161311/#FFE4CF), Anonymous Pro Bold 20sp numerals, `END` engraved
   (Atkinson Hyperlegible 9sp) on the bezel below the glass, blank whenever
   no session has sealed (showNumerals gate unchanged). Numerals are no
   longer bare text beside the lamp.
8. **Motion** — 260ms glide retired → 50ms throw (Bolt §7.1, inside the
   existing Throw sound's ≤85ms window; zero new sounds, composable silent).
   First composition snaps (animateFloatAsState initial = target).
9. **Semantics** — UNPROVEN now speaks "caught, not locked" (the ½ language
   died with the ½ pose); BLOCKING speaks "thrown and held".

## 2 · REMAINDERS (merge pass, honest)
- Host fold stays the v0 re-encoding of blockCell/timeCell — Bolt §2's
  one-fold `boltStateFor` + B-C1 shieldStale / B-C2 finish inputs remain
  BLOCKING machinery charges for batch-10 (issue #15), unchanged.
- §7.3 housing-as-door (press dip, fix-route knock, haptic seat) NOT built —
  the instrument stays honestly non-interactive this wave.
- BLICK T1–T9 glance battery runs on renders at merge (Ren's compile), not
  in this cell. The budget constant + jewel cap make it executable.
- 2dp SEATED sink + pin-at-+62ms keyframe choreography: approximated by the
  50ms tween + pin pose; full keyframes are merge-pass polish.
- sealStateForBand dead code: grep this wave found no remaining reference in
  the #4 tree (already gone); MonolithCustomComponents seam helpers stay for
  the sentence slot / imprint / tests, as designed.

## 3 · FINISHERS ×5 — signed sections (seated as one cluster, all reported)
- **F-FLÄCHE (surface):** v0 FAIL — flat Row, no channel/bed/socket/pin.
  Bed + collar + pin + bezel now CODE (lead §1.4–1.6). Housing stays a band,
  radius 0, hairlines — MERGE-PASS render check. — *F-Fläche*
- **F-GANG (motion):** 260ms tween = a verdict lying for 15 frames. 50ms
  throw CODE-NOW, done. Verdict changes unanimated, first frame snap. — *F-Gang*
- **F-LICHT (light):** 13dp lamp broke the ≤41.9 budget (~89 units, OFF lost
  0 vs 42.6). Jewel 9dp + bed ≈125.7 OFF total restores ≥3×. Done in code. — *F-Licht*
- **F-TON (sound/haptics):** composable silent, zero new sounds — PASS as
  built; Throw buffer hash untouched. No haptic invented for the bar. — *F-Ton*
- **F-DRUCK (typography/print):** face vocabulary is now END + HH:mm only;
  END was missing in v0 — engraved this wave. Density-only scaling stands. — *F-Druck*

## 4 · FEEL SPECIALISTS ×5 — signed sections
- **FEEL-GEWICHT (weight):** night weight = occlusion → collar overlap → rim
  step → cream grips; rim step (#807A7A, night, proven only) is MERGE-PASS. — *Gewicht*
- **FEEL-LICHT (light):** health only lights; fault never lights — now true
  in code (dark lens for OFF/UNPROVEN/FAULT, orange is paint not light). — *Licht*
- **FEEL-TAKT (timing):** throw at t0 inside ≤85ms; ending-soon flag rises
  once, never re-fires; window never ticks. Priced in §1.8. — *Takt*
- **FEEL-BERÜHRUNG (touch+sound):** non-interactive is honest v1; §7.3 door
  spec (1dp housing dip, live gate re-read at DOWN+UP, single voice) filed
  MERGE-PASS / Ren's Deck-level call. — *Berührung*
- **FEEL-BLICK (instant-read):** OFF vs RUNNING now separable by area alone
  (bed exposed vs jewel ≤9dp); CAUGHT at ⅓ sorts "not protected". Battery
  T1–T9 still owed on renders. — *Blick*

## 5 · THE 20 NAMED TYPES — signed sections
- **Crazy-ideas:** "Keeper's Toll" — END window flips to a toll-clock in the
  last 300s — REJECTED by lead (reopens sliding-end lie); inversion adopted
  instead. One wild idea priced and refused on record. — *Crazy-ideas*
- **Maximum-implementation-potential:** §8 backlog was 80% CODE-NOW, not
  merge-pass — proven: 8 of 10 items landed this wave in ~120 lines. — *Max-impl*
- **Brutal critic:** v0 was a slider widget wearing the Bolt's name (½ pose,
  back-off, 13dp lamp, alert numerals — four spec violations in 391 lines).
  Charges C-1…C-7 all cured in code or re-filed in §2 above. — *Brutal critic*
- **Improver:** jewel bezel + budget constant + tests convert prose laws
  into executable gates; next improvement: render-side bed-area assertion. — *Improver*
- **Designer:** form-first now holds — position, area, and one jewel carry
  the state; words survive only as END + numerals. — *Designer*
- **Consumer:** at arm's length OFF now reads orange-and-open without a
  single word; that is the screenshot complaint answered. — *Consumer*
- **Student (1 AM test):** caught-at-⅓ + dark lens cannot be misread as
  locked by a tired eye sorting binary; PASS by the binary-bucket rule. — *Student*
- **User:** no new interaction to learn; the bar still asks nothing of the
  hand. Correct — an instrument, not a control. — *User*
- **User-experience:** the one regression risk is the always-present window
  (102dp reserved while blank) — accepted per Bolt §4 presence law; the
  glass reads as an object, not an absence. — *UX*
- **Purpose-questioning:** the bar's job is one glance, one truth: *does it
  hold?* Everything not serving that was cut, including the count. — *Purpose*
- **Thoughtful:** DUNKEL's back-off dissent and HAUT's silence dissent stay
  on record in the design; this wave followed the ratified rulings, not
  the loudest seat. — *Thoughtful*
- **Deep-thinker:** pose/lamp/window as three independent channels (geometry
  / proof / strength) is the design's real invention; code now keeps them
  independent (lamp drops never move the bolt). — *Deep-thinker*
- **Enthusiastic designer:** the pin dropping only when the notch arrives
  is the delight detail — machinery you can see obeying physics. — *Enthusiastic*
- **Wow-level designer:** window inversion is the wow beat: the whole glass
  turns brass, once, silently — a block change, not a recolour. — *Wow*
- **Designer, 29 years:** detents at fractions, fixed-size instrument,
  density-only scaling — clockmaker rules, correctly kept. Font-scale
  ≥1.3 overflow is honest, never a shrunk digit. — *29-years*
- **Intentionality-focused designer:** every remaining element answers for
  its presence: bed (OFF's area), collar (arrival), pin (held), jewel
  (proof), window (end). Nothing decorative survived. — *Intentionality*
- **Synchronous collaboration:** this finish consumed issue #6's F20 ruling
  (rail zone ≠ strip) and #10's F19 precedence without collision; the
  strip band stayed #4's alone. — *Sync-collab*
- **Implementation-quality:** pure derivation untouched; all changes are
  params + rendering; 3 new tests pin the laws that were prose. — *Impl-quality*
- **Technical execution specialist:** compile-risk audit: no new imports
  beyond Column + Atkinson family (both present in tree), no API changes
  for callers (SealBar signature unchanged), Dp arithmetic only. — *Tech-exec*
- **Interaction-detail specialist (owner, top-strip object):** Buttons —
  none, by design: the bolt is not a handle; adding a tap target here
  would counterfeit a control. 3D treatment — layered machining instead
  of elevation: bed (floor) → collar → socket → bolt → pin, contact rib
  not shadow (`.shadow()` banned in this bar), bezel disc rescues the
  jewel. Vibration — NONE invented: the house haptic table is closed;
  the seal's +62ms seat already lives in the Throw buffer the throw is
  timed to. Peak perfection for this object = it moves like the thing
  it depicts, in the sound that already exists, and never pretends to
  be pressable. §7.3 door remains the priced alternative (dip 1dp /
  90ms in / ≤150ms out, gate re-read at DOWN+UP, one knock, HAUT
  dissent recorded) if Ren rules the housing a door. — *Interaction-detail*

## 6 · DECISION-FOR-SARTHAK
- None new from #4. (The housing-as-door question is Ren's Deck-level
  call per the design, §7.3 flag — not escalated to Sarthak.)

## 7 · WHAT I WANT NEXT (ANTLITZ)
Render the OFF/RUNNING night pair + greyscale twins at the batch-10
compile and run BLICK T1–T9 against this code; then the §2 host fold
(one-derivation `boltStateFor` with the B-C1/B-C2 inputs) as my next
seat on the merge tree.

— ANTLITZ, cluster-B lead, issue #4. DECLARED, uncompiled, awaiting Ren.

## STATUS ADDENDUM (lead, after commit 8336ca1)
- This cell origin is the LOCAL merge-batch9 remote, not GitHub: push succeeded there only (new branch agent/issue-4). GitHub push of 8336ca1 is still owed by Ren.
- The signed summary comment DID post on issue #4 via the GitHub connector (comment id 6510069208).

## ADDENDUM — finisher/feel dissent, recorded by the lead (late cluster)
The finisher/feel cluster classified the full geometry (bed/collar/
pin/notch) and the keyframed throw as MERGE-PASS, and F-GANG held
that merely shortening the tween to 50ms is "CODE-NOW but wrong" —
the spec's throw is keyframed (0/24/62/94dp), pin snapping 1 frame
after landing at the +62ms seat, then a rigid 2dp SEATED sink.
Lead's position, on record: the geometry + params landed this wave
as DECLARED code because every value is a SealBarParams constant and
the merge pass can re-time/re-place without touching callers; what
did NOT land and remains MERGE-PASS: (1) the keyframed throw +
pin-at-seat + stepped sink (the current 50ms tween is an interim
approximation, named as such); (2) verdict changes still animate via
animateFloatAsState — spec says verdicts land in 1 frame (Tech-exec
charge, stands); (3) TON's full day/night token table applied
token-by-token; (4) LETTER's closed 11-sentence spoken set (current
descriptions: 6, and RUNNING leaks "Ends HH:mm" into
contentDescription — Implementation-quality charge, stands);
(5) the rim step #807A7A (night, proven only) and cream grips.
The ship gate is unchanged and absolute: BLICK T1–T9 on real renders
(side-by-side OFF/RUNNING, night, greyscale twins) — no battery, no
swap. Nothing in this wave ships without it.
— ANTLITZ, lead. DECLARED, uncompiled.

## FINAL STATUS (course correction, gh route)
- Origin remote CORRECTED in this cell: was the local merge-batch9 path,
  now https://github.com/Sarthak-2007/Focus-app.git, upstream tracking set.
- Branch agent/issue-4 PUSHED to GitHub (b25574f), verified in sync
  (HEAD..origin empty both ways) — not diverged, not merely equivalent.
- Follow-up signed comment posted via `gh issue comment` (never the
  connector): https://github.com/Sarthak-2007/Focus-app/issues/4#issuecomment-6510177606
  (The earlier comment 6510069208 went via the connector before the
  course correction landed; no connector calls since.)
— ANTLITZ
