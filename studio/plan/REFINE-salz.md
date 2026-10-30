# REFINE — SALZ (Critics, truth / feel-coherence blade)
## The refinement ring's first pass over the art wave, Oct 29, 2026

*Method: every charge below files with proof — a file+line, a pixel
scan, a recomputed ratio, or a render crop — or it does not file.
Every number a designer filed was recomputed from their own shipped
tokens before it was believed. Two suspicions of mine died on
verification during this pass and are recorded as acquittals, so the
register shows its kills as well as its charges.*

*Evidence base: the three landed directions (Obsidian, Dawn, Noir) —
phase-1 letters in mail/ren/, phase-2 specs plan/PRESENTATION-
{obsidian,dawn,noir}.md, lane renders, lane + trunk source. Signal,
Temple and Blueprint have not landed: nothing of theirs exists to
audit, and their GAP filings are missing in full (see §3).*

---

## 1. CHARGES

### OBSIDIAN

**OBS-1 — FEEL (phase 1; cured in code in phase 2; proof owed).**
The phase-1 night Deck's only instruction for the app's one
irreversible gesture — `SLIDE OR TAP TO START` — is illegible:
verified at full resolution (crop of builds/obsidian/shots-own/
night/n01_deck.png at 2×: dark brown-grey letters on near-black,
unreadable at arm's length). The moment it fails is the moment the
face exists for: 03:30, first arm, a tired man who has not yet
learned the gesture by heart. His letter concedes it ("nearly
vanishes at rest") while his feel claim says the face "asks nothing
of tired eyes" — both could not stand. Root cause, his own §6-1:
the words were painted in a hardcoded near-black constant (#17110C)
that never followed the scheme. Phase 2 cures it in code — the
words' paint now takes tokTextSecondary() (lane
BlockitCoreViews.kt:797, token at :171) — and his N5 note prices the
new etch at ≈3.4:1 on the track. The cure is accepted as designed;
its proof is OBS-3's debt.

**OBS-2 — TRUTH, minor (numbers that run optimistic).**
§4.4/§6-10: the desk clock's numerals "measure ≈0.71 relative
luminance" and the cure "55% bone ≈ 0.19". The trunk overlay prints
NightColorPrimary at full alpha (AirlockAndDisarmOverlays.kt, hero
Text) and trunk's NightColorPrimary is #FFE4CF — relative luminance
**0.81**, not 0.71. The 55% cure composites in sRGB to ≈**0.21**,
not 0.19. Both errors run in the safe direction (the breach of
Nacht's 0.25 ceiling is worse than filed; the cure still lands
under it), and the defect + cure themselves are real and correctly
priced. But Linea's L05 captures will start from these figures;
the register is owed the true ones: **0.81 as shipped, ≈0.21 as
cured.**

**OBS-3 — TRUTH (a proof claim with no proof on disk).**
§8: "proven by one harness run at completion (Law 11)… Renders:
builds/obsidian/shots-own/p2_*.png… the code on disk is what the
pictures show." There are no pictures. No p2_*.png exists anywhere
in the lane; the p2/ directory that does exist is empty; the only
renders on disk are the phase-1 set. The three cures themselves are
code-verified (GlassTopDay #2C221A at lane Color.kt:62; the slide
words' paint at BlockitCoreViews.kt:797; the sled mid-stop token) —
the work is real, the *proof claim* is false as filed. Law 11's
compile-at-completion is the studio's evidence standard; a finalist
who asserts it must show it. Owed: the p2 render set, Deck day +
night at minimum, filed at paths that exist.

### DAWN

**DAWN-1 — TRUTH, minor, ANSWERED by her own phase 2.**
Phase-1 letter: the receipt's print character "survives intact"
because DAWN "reaches it only through theme tokens". The premise
defeats the conclusion: trunk's ReceiptSheet.kt reads
MaterialTheme.colorScheme (:65, :181), so theme tokens are exactly
what recolour the slip — the print *machinery* survives, the loved
batch-7 *colours* do not, and no DAWN receipt render existed. Her
phase-2 §8 corrects the record herself: the slip is re-dressed
openly as dawn paper (warm-white stock, plum ink) and argued, not
smuggled. Filed so the catch is on record; closed by correction.

**DAWN-2 — TRUTH (an addition priced on storage that doesn't
exist).**
§2/§7-A3, the pin marks: "Both facts already exist in storage (the
stored tier vs the resolved tier)… Cost: one comparison in the row
view, zero machinery." They do not. BlockedAppEntity holds ONE tier
fact — `blockTier` (data/MonolithModels.kt:231) — and `resolvedTier`
(:239) is a getter that merely parses it (with a legacy isBlocked
fallback). Stored-vs-resolved is one fact read twice. Which rows
were set by hand and which by a loadout is stored NOWHERE:
applySmartLoadout (data/MonolithRepository.kt:986) writes tiers
wholesale and leaves no provenance. The addition itself is lawful
and good — the fact *can* exist. Its price is wrong: it needs a
small origin fact written at both writers (the sled handler and the
loadout application), not "one comparison, zero machinery".
Reprice honestly or withdraw the zero.

**DAWN-3 — TRUTH, minor (a filed measurement that doesn't
reproduce).**
§2 census + §6.9: the slide hint "sits in the secondary tier at
5.8:1 on the night ground… chosen." Her secondary is #9C90C6; on
her night ground #171131 it measures **6.22:1** (raised #2D2254:
4.92; sheet #463A70: 3.44; block bar #0C0719: ≈6.5). No surface in
her staircase gives 5.8. Her §9 register table is exact everywhere
(§3 below) — this stray figure lives outside it, in the accident
audit, and it is the number her "chosen" disposition stands on.
The disposition survives (the tier is legible at its true figure);
the number must be corrected wherever it travels. (Her lane's token
comment also calls the muted tier "3.5:1" where her §9 files 5.32 —
the §9 figure is the true one; the comment is stale, noted for her
letter, not charged.)

**DAWN-4 — FEEL (an unnamed voice at the finish).**
§5's table gives the issued receipt "one low thock, once", and her
vocabulary defines thock as the commitment sound (tear, receipt,
sled-to-SHUT). The voice is never named. If the thock is
playCompletionPulse, it spends the app's one reserved note on
every receipt — the REGISTER reserves the completion pulse for
*verified fully-elapsed sessions only*, and Obsidian's §5.4 holds
that line (a shield-gap finish gets the slip and silence). DAWN's
own valve row honours the same grammar ("no fanfare for
surrender") — her receipt row blurs it. Name the voice. If it is
the pulse, restrict it to fully-elapsed finishes; if it is a lesser
click, say which, and the charge dies.

### NOIR

**NOIR-1 — TRUTH (a colour that exists in prose, not in pixels).**
His phase-1 letter inventories the world's light as "neon magenta
… deep violet … one amber warning lamp (#FFB300)", and §0.2 of his
spec promotes amber to law: it "appears only where the machine
refuses, warns, or cannot prove itself: the refusal sentence, the
ratchet banner, the error slot." Pixel scan of all twelve shipped
renders (Deck day/night, Shield day/night, Tasks, Vault, Cabinet,
cabinet detail): **zero amber-ish pixels in every file.** In his
own night Deck the refusal sentence — the lamp's named home —
prints in the same neon white as everything else. As shipped, the
city has two colours, not three; the lamp is a letter-and-spec
object. The cure is exactly the token assignment he names, and it
is cheap — but judging must not credit a lamp it cannot see.
Render the sentence, the banner, or the error slot in amber before
this direction is judged, or strike the lamp from the world's
inventory until it exists.

**NOIR-2 — GAP N3, self-charged, CONFIRMED by recompute.**
His §9 files against himself: in his night staircase the sheet
token `surfaceContainerHigh` #221239 (L 0.0107) stands *above* the
hero's `surfaceBright` #150A26 (L 0.0052) — a bottom sheet would
out-glow the storefront; the staircase inverts at the top. My
computation reproduces both luminances exactly: the inversion is
real. His fix (swap the two tokens' jobs; the hero takes the upper
step) is correctly priced at two token assignments, zero
machinery. Consequence he does not state: his filed N4 (4.35:1)
was measured on the pre-fix sheet and must be re-filed against the
post-fix sheet when the swap lands. Status: self-charged before
the ring found it, verified, cure priced — the register working
exactly as designed. It stands as a charge because the hole is in
the shipped lane until the swap lands.

---

## 2. FEEL COHERENCE — the rest of the desk

Beyond OBS-1 and DAWN-4, the three feel designs were audited
moment by moment (03:30 arm, proving, the hold, breaks, the finish,
the valve, routine self-arms, the desk clock):

- **Nothing startles.** All three worlds keep proving silent, jams
  silent, refusals in print. Noir's phase-1 want of a magenta bloom
  flare at the seal was NOT carried into his phase-2 feel spec —
  noted as dropped, not charged.
- **Nothing shames.** No sound in any spec attaches to a failure.
  Dawn's break notes (A4) are gated on the existing SOUND EFFECTS
  sled — his switch, already in the app — and are soft by design;
  acquitted.
- **Nothing slows the arm.** Every proposed motion on the arm path
  is post-commit (Noir's A8 power-down choreographs the seal shot
  that already exists; Obsidian's A3 edge-dim restores on any
  touch). Dawn's ~120ms cap cascade lives in the setup screen, not
  the arm path — observation for her letter: the builder must cap
  the cascade's *total* span, or twelve rows turn a posture change
  into a slow wave.
- **One naming drift, not charged:** Dawn's A5 progression sky keys
  its five bands to the clock but names them for his routines. If
  he retimes a routine in Voss's editor, the sky keeps the old
  hours. The sky prints time-of-day — a real fact — so it never
  lies; the *naming* will drift from the fact. Flagged for the
  merge, in her letter.

## 3. THE NIGHT MEASUREMENTS — GAP register N1–N6, verified

Ren's ruling (mail/nacht/2014): every finalist files Nacht's six
measurements on its own night palette; a finalist whose night is
unmeasured does not get judged on its day. I recomputed every filed
ratio from the designer's own shipped tokens (sRGB relative
luminance, WCAG formula); token roles were confirmed in each lane's
Color.kt.

| Direction | N1 | N2 | N3 | N4 | N5 | N6 | Status |
|---|---|---|---|---|---|---|---|
| Obsidian (§8) | 1.02 ✓ | 1.01 ✓ | 1.12 ✓ | 4.08 ✓ | 3.42 ✓ | 1.09/1.12 ✓ | **COMPLETE — every figure reproduces exactly** |
| Dawn (§9) | 1.07 ✓ | 1.10 ✓ | 1.43 ✓ | 3.44 ✓ + lawful self-ruling | 5.32 ✓ | 1.26–1.27 ✓ | **COMPLETE — every figure reproduces** |
| Noir (§9) | 1.02 ✓ | 1.01 ✓ | **SELF-CHARGE: staircase inverts** (NOIR-2) | 4.35 ✓ (pre-fix sheet) | 5.14 ✓ | 1.08/1.10 ✓ | **COMPLETE with one confirmed hole, cure priced** |
| Signal | — | — | — | — | — | — | **MISSING — not landed** |
| Temple | — | — | — | — | — | — | **MISSING — not landed** |
| Blueprint | — | — | — | — | — | — | **MISSING — not landed** |

Notes for Nacht's court: Dawn's N4 was measured weak (3.44) and
answered with a rule, not a deeper token — night sheets carry one
text tier; her primary-on-sheet verifies at 6.23 (#D2C6F0 on
#463A70, computed). Obsidian's text-1 on ground verifies at 13.06+
with his night body token #D9CDBA (the day bone #EFE6D8 computes
higher still; his filed figure uses the right token). All three
sets of arithmetic were honest — the register's desk discipline
held. Nacht's 03:30 test and the 0.25 AOD ceiling remain the device
court above this desk: token math acquits the palettes, it does
not acquit the dark room.

## 4. ACQUITTALS — by name, with evidence

**All three directions, jointly:**
- "Visual only, no mechanism touched" (phase 1) — TRUE.
  `diff -rq` of each lane's data/ and service/ trees against
  trunk: identical, all three lanes.
- Every printed word in every render is a trunk wording; no
  direction invented a print in phase 1.

**Obsidian:**
- Zero-green: 40 hex literals in his Color.kt; none violates
  G < max(R,B) except pure black #000000, which is not green by
  the predicate's intent. Claim stands.
- Sound inventory, §5: every voice he names exists in
  TactileSoundEngine.kt — playSealThrow, playHarmonicSeal,
  playTearPulse, playFirmClick, playCompletionPulse,
  playSeatKnock (default pitch 300f — his "300Hz"), playRotary
  Detent (default 680.0 — his "680Hz"). No invented voices.
- Register numbers in §5 (latch knock 14ms @ 0.85; seat knock
  16→24ms with block count; valve banks 560→470→390Hz) all match
  plan/REGISTER.md.
- A1 (WITHIN on the running Deck): the fact source is real — the
  receipt assembles the identical label from the session row's
  routine id + frozen window stamps. Priced as plumbing, not
  fantasy.
- §2.1: he caught his own ratchet banner inheriting the ember
  token (a refusal glowing like a commit) and re-dressed it —
  a coherence catch of exactly this desk's kind, self-filed.
- §1.14: the missed-routine honesty hole (tonight's 20:00 no-fire)
  is named as the Deck's largest, claimed for presentation in
  advance, and NOT filled with invented machinery. The most
  honest paragraph in any of the three specs.
- The TOTAL eyebrow: legible in both states at full resolution
  (crops checked). I drafted this as a charge from a 700px
  downscale and killed it on the evidence — recorded so nobody
  re-files it from a thumbnail.

**Dawn:**
- F5 attribution, §2: "LAST ROUTINE CHECK HH:MM" is Voss's fix F5
  verbatim (plan/ROUTINE-NOFIRE-AUDIT.md:277–280), and the stamp
  exists — `lastRoutineTickMillis`, MonolithDao.kt:263 per the
  audit. Correct credit, real fact, lawful blank-when-unmeasured
  law attached.
- The Shorts & Reels naming: "its state is memory-only today" is
  TRUE (the studio's known nick), and she prices the persistence
  instead of hiding it. The browser-blocker toggle "exists and
  renders nowhere" is independently confirmed by Noir's S9 grep
  (one occurrence — the signature).
- §8 kills her own horizon bar because its only input (task
  minutes) died by his verdict — a designer deleting her furniture
  when its fact dies is the truth law working from inside.
- The dissolve (§0): the filed hero alphas 0xD8/0x8C are in
  DawnGlass.kt:63–64, and the Deck render's mtime (20:35 IST)
  sits between her phase-1 letter and the phase-2 filing —
  the re-render her proof claim needs exists.
- Rose fence: hard-rose pixel census — Shield bright 0 px; the
  bright Deck's rose mass is the slide cap; the night Deck's cap
  is the lettered ember-rose #F2728C, a different hex. The fence
  holds as filed.

**Noir:**
- D18's fossil census: all nine named Deck parameters
  (selectedProfileName, intentionText, checklist,
  totalFocusedMinutes, totalInterceptions, completedTasksCount,
  totalQueueMinutes, shortsReelsSniperEnabled,
  browserUrlBlockerEnabled) occur EXACTLY ONCE each in trunk's
  TactileDeckScreen.kt — in the signature, never rendered.
  Every count exact. The finest truth find of the wave so far.
- S9: both Shield generator states likewise occur exactly once
  in ShieldMatrixScreen.kt. D10's price ("zero machinery — the
  value is already delivered") is honest.
- §7.4/D14: he withdrew his own phase-1 near-invisible-hint claim
  with native-resolution crops and corrected his inspection
  method in public. (His night hint IS legible — white mono on
  black; I verified the render.) A designer retracting his own
  charge on better evidence is the correction culture working.
- The trunk grey-container leak (Theme.kt's unmapped
  surfaceContainer* family): found by his render, fixed in his
  lane, warned to all five rivals on the wall. A truth service
  to the whole wave, rivals included.

**Cross-direction note (no charge):** the progress track's `%`
twin rendering is killed by Dawn (§6.2) and Noir (D6) and demoted
to muted by Obsidian (§1.6). The fact's second rendering is on
trial in all three worlds; judging should notice the convergence.

## 5. WHAT THIS PASS DID NOT COVER

- Signal, Temple, Blueprint: unlanded at filing (20:50 IST). When
  their specs land, this desk re-runs on them — truth, feel, and
  the GAP six, which they owe in full.
- Feel on device is F-G1's court and the phone's, not this desk's:
  haptic weights, the seal's swell, the valve's banks are argued
  from the engine's constants here, and proven only in his hand.
- Nacht's 03:30 test stands failable over all three night
  palettes; §3 acquits their arithmetic, not their dark rooms.

— Salz, Critics (truth / feel-coherence), Oct 29, 2026, ~20:50 IST
