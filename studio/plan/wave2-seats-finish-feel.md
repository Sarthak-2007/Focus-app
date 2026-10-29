# Issue #7 — Shield — WAVE-2 SEATS: FINISHERS ×5 + FEEL ×5

**Cell:** issue-7, branch `agent/issue-7`, under lead **VOGT**.
**Run:** Wave-2 seat-runner (one runner, ten seats, one signed section per seat).
**Status:** DECLARED — paper only. No app code written or edited, nothing
compiled, no tests run, nothing committed. This file is the only output.
**Sources read in full:** `studio/plan/AUDIT-shield.md` (Vogt, R-1…R-13),
`studio/cells/issue-7/lead-vogt.md` (lead synthesis + the named finish/feel
follow-up), and the **current** tree:
`app/src/main/java/com/example/ui/screens/ShieldMatrixScreen.kt` (cited
**SM**) and `app/src/main/java/com/example/ui/ShieldBlockingStatus.kt`
(cited **SBS**). Supporting lines cited where a finding leaves those two
files: `MonolithViewModel.kt` (**VM**), `MonolithRepository.kt` (**Repo**),
`ui/views/BlockitSuitcaseAndSwitchesViews.kt` (**Sled**),
`ui/theme/Color.kt` / `Theme.kt`.

**Seat mapping for this run (declared, not inherited):** Finishers —
F1 Surface (PANZER), F2 Motion (GLANZ), F3 Light (POLITUR),
F4 Sound/Haptics + Semantics (FASSUNG's operability bench), F5 Typography/Print
(SAUM). Feel — L1 Weight (HAUT), L2 Light (LUFT), L3 Timing (SCHWUNG),
L4 Touch+Sound (TAST), L5 Instant-Read (the 1 AM glance seat).

## Common baseline — the truth core PASSES, and no seat re-opens it

`BLOCKING NOW` is **witness-proved, never a row flag**, in the current code:

- SBS:116–119 — a null/inactive session row prints only
  `NOT BLOCKING — NO SESSION RUNNING`; the row's `isActive` can never
  promote itself to BLOCKING.
- SBS:123–126 — BLOCKING requires **both** cascade witnesses **for this
  session id**: shield service running + `protecting` flipped for
  `session.id`, **and** accessibility bound + armed for `session.id`.
- SBS:146–149 + Repo:161 — proof also dies on the repository's own 10 s
  beat-gap law (`SHIELD_GAP_THRESHOLD_MILLIS = 10_000L`).
- SBS:156–158 — the LIVE headline is built only after both witnesses +
  fresh beat; SM:186–187 — with no derivation supplied the screen prints
  `ENFORCEMENT NOT PROVEN — NO WITNESS READ YET`, never BLOCKING.
- RUNNING NOW is session-keyed, not window math: SBS:191–193
  (`routineRunningNowSuffix` on `activeRoutineId`), fed at SM:324.

Every verdict below is about the **finish and feel of that truth** — how it
is printed, lit, timed, sounded and read — plus the four audit items the
lead left open on this screen (websites, cap, PEEK line, refusal point).
Where the current tree already fixed what an earlier filing charged, the
seat says PASS and names the line; stale charges are not re-filed.

| Seat | Verdict |
|---|---|
| F1 Surface (PANZER) | **FIX-FIRST** |
| F2 Motion (GLANZ) | **FIX-FIRST** |
| F3 Light (POLITUR) | **FIX-FIRST** (KILL on error-as-sole-channel) |
| F4 Sound/Haptics + Semantics (FASSUNG) | **FIX-FIRST** |
| F5 Typography/Print (SAUM) | **FIX-FIRST** |
| L1 Weight (HAUT) | **PASS on restraint, FIX-FIRST on the slot** |
| L2 Light (LUFT) | **FIX-FIRST** |
| L3 Timing (SCHWUNG) | **FIX-FIRST** |
| L4 Touch+Sound (TAST) | **FIX-FIRST** (strongest feel defect in this run) |
| L5 Instant-Read | **FIX-FIRST** |

---

## F1 — FINISHER 1 · SURFACE (PANZER)

**Verdict: FIX-FIRST.** The surface order is now right; the status slot,
the cap's surface honesty, and the websites block are not finished.

**PASS, on record:** item 0 is the status sentence (SM:211–233), above
every ruling it qualifies; the catalogue is the one enumeration, grouped
PEEK → SHUT → OPEN (SM:463–475); the tally is counts-only (SM:346–358).

**Findings:**

1. **The status sentence has no slot.** SM:211–233 is one bare `Text`:
   no fixed height, no `maxLines`, no glyph, no segment law. The longest
   real headline — UNPROVEN with a routine label, SBS:135
   (`NOT BLOCKING — $label RUNNING, ENFORCEMENT NOT PROVEN`, ≈60 chars of
   13 sp mono) — wraps to 2–3 lines inside the `LazyColumn` and shoves
   the LOADOUT cap down; when proof flips, everything below jumps. A
   sentence whose height depends on how bad the news is, is not a slot.
   **Price:** fixed 40 dp slot + 10 dp solidity glyph + UNPROVEN split at
   its comma (PANZER PZ-1/PZ-2, already specced in
   `studio/cells/issue-7/finisher-panzer.md` §6): **~60 lines, ≈½ builder-day.**
2. **The ratchet banner still leaves a hole and still sits at the wrong
   address.** It is its own `LazyColumn` item (SM:237–255) wrapping an
   `AnimatedVisibility`; collapsed, it still collects the column's 12 dp
   `spacedBy` gaps (SM:199) — a dead gap under the sentence every normal
   day. Worse, it is the *only* place a refusal prints, and it sits at
   item ~1 while the refused sled may be 70 rows below (see F4/L4).
   **Price:** fold the banner into the status item's `Column` (~0 net
   lines, PZ-3) **and** print the refusal at the touched row (see MUST-5).
3. **LOADOUT is still a state-costume, not an act.** The header the audit
   ordered killed (R-4c) still prints (SM:266 `FullBleedSectionHeader(
   titleWord = "LOADOUT")`, in the 84 dp slot); the cap rests on the
   **last hand pick** — SM:94–97 and SM:140–144 say so in their own
   comments — with **no source print** (`BY HAND` / `BY SLEEP · UNTIL …`
   nowhere on this screen); and a tap commits instantly, no preview
   (SM:271–278 → `onApplySmartLoadout`). When a routine's loadout is the
   one actually running, this cap displays history while wearing a
   state's clothes — R-4's lie, still live. **Price:** Ghost Preview
   ≈120 lines (Vega V-3, audit R-4a) + source print ≈15 lines + header
   deletion ≈8 lines saved.
4. **Websites: wrong floor, no ceiling.** BLOCKED WEBSITES is the last
   section of the column, *after* the whole catalogue (SM:500–507,
   following SM:463–475) — the audit measured ~6,100 dp deep (R-3) and
   nothing in the current order changes that. And it is uncapped:
   SM:509 `blockedSites.forEach` prints every row; there is no ≤5 +
   SHOW ALL fold. **Price:** move above ALL APPS + cap/fold ≈25 lines
   (Dens F4, audit merge item 4).
5. **Dead wires still in the contract (R-9, unlanded).**
   SM:90–93 + SM:115–116: `shortsReelsSniperEnabled`,
   `browserUrlBlockerEnabled`, `profiles`, `selectedProfileName`,
   `onToggleShortsReelsSniper`, `onToggleBrowserUrlBlocker` — declared,
   never used in the body (grep-verified this run). PARK owns both
   switches. **Price:** deletions only.

Signed — **PANZER, Finisher 1 · Surface**, Wave-2, under VOGT. DECLARED.

---

## F2 — FINISHER 2 · MOTION (GLANZ)

**Verdict: FIX-FIRST.** The tally has house manners; the sentence above
it has none, and the sentence is the one that changes at the worst times.

**Findings:**

1. **No re-strike on the status line.** The tally re-strikes whole:
   `AnimatedContent`, 260 ms, `MonolithCurves.EasingA`, fade in/out
   together (SM:863–879). The status sentence — a graver fact — swaps in
   a single frame as a plain `Text` (SM:211–213). Same screen, two
   manners, and the lesser fact got the better ones. **Price:** one
   `AnimatedContent` keyed on the headline, tally's exact spec: **~20 lines.**
2. **No anti-flap coalesce on proof.** The tally coalesces bursts behind
   a 250 ms stability delay (SM:171–176). The status derivation has no
   equivalent in the screen: a shield beat hovering at the 10 s line
   (SBS:146) can flip LIVE ↔ STALE on successive witness reads and the
   print strobes with it — print may follow a flickering fact, but it
   must not strobe at ticker rate. **Price:** 250 ms coalesce mirroring
   the tally's `LaunchedEffect` pattern: **~15 lines.**
3. **The banner's motion is unspecified.** SM:238 `AnimatedVisibility`
   with defaults — not the house curves R-7 required, and (per F1) it
   animates inside an item that should not exist separately. Rides
   F1 finding 2 / MUST-5 at **no extra price**.
4. **Acquitted:** no overshoot, pulse, roll or blink exists anywhere in
   the status zone today; the sled's throw/settle physics stand acquitted
   by the audit (R-10) and this seat does not re-open them. The fix is
   addition of the one governed motion, not a redesign.

Signed — **GLANZ, Finisher 2 · Motion**, Wave-2, under VOGT. DECLARED.

---

## F3 — FINISHER 3 · LIGHT (POLITUR)

**Verdict: FIX-FIRST — and KILL on `scheme.error` as the status line's
sole alert channel.**

**PASS, credited precisely:** the current tone map is *not* the
lead-snapshot defect. SM:222–228 already splits the states: LIVE = full
ink `onBackground` (SM:223–224), NO_SESSION = quiet `onSurfaceVariant`
(SM:225–226) — a phone with no session is no longer painted as an error.
That part of HAUT's tone map has landed in effect. What remains:

1. **UNPROVEN / STALE still wear `scheme.error` alone** (SM:227).
   `scheme.error` is `TokenAlert` `#D63E06` (`Color.kt:29`, wired at
   `Theme.kt:36`); PANZER's token measurement (finisher-panzer.md §2.4)
   puts it at **3.49:1 by day** — the least legible ink on the screen,
   spent on the two states where the print *is* the alarm. By night the
   same slot is the accent's family, spending the one orange on print.
   And colour is the **only** differentiator: there is no glyph, no
   density step (F1 finding 1), so a colour-blind or glare-washed glance
   gets the verb and nothing else.
   **Price:** full ink in **all** states + state carried by the 10 dp
   solidity glyph (solid = LIVE, hollow = NO_SESSION, 55 % =
   UNPROVEN/STALE) + verb: **~25 lines**, shared with F1's slot patch —
   not additive.
2. **The banner's text colour is still a hardcode.** SM:251
   `Color(0xFFFBF1EC)` on `scheme.primaryContainer` (SM:243) — R-7
   ordered it tokenised (`onPrimaryContainer`); a hardcoded cream is
   unprovable in the day theme and will silently rot at the next theme
   change. **Price: 1 line.**
3. **WON'T FIRE's word is quieter than its verdict.** The routine cap
   (SM:686–703): for `WON'T FIRE`, `capFilled` is false (SM:684–685) and
   the word falls to `scheme.onSurfaceVariant` (SM:702) — the same quiet
   tone as OFF. PANZER's grammar (one body, word density separates them:
   WON'T FIRE = full ink, OFF = quiet) is not what the code prints; at
   night that variant measures ≈3.5:1 (finisher-panzer.md §4 table).
   **Price: 1 line** (`onBackground` for the WON'T FIRE branch).

Signed — **POLITUR, Finisher 3 · Light**, Wave-2, under VOGT. DECLARED.

---

## F4 — FINISHER 4 · SOUND/HAPTICS + SEMANTICS (FASSUNG's bench)

**Verdict: FIX-FIRST.** Two channels lie outright: the ear is told a
refused act committed, and TalkBack is told nothing at all.

**Findings:**

1. **A refused sled throw sounds like a commit.** Order of operations in
   the sled: `commitTo` fires the commit feel **first** —
   Sled:295–297 (`fireCommitFeel(targetTier)` at :296, *then*
   `onTierSelected?.invoke` at :297) — and only afterwards does the VM
   get to refuse (VM:2118, the ratchet warning). So during a session, a
   weakening throw plays the LOCKED/PEEK/OPEN commit voice for a change
   that never happened. This is TAST T-1 / KLANG's defect, verified in
   the current tree, not quoted from the lead file. A sound is a claim;
   this one is false. **Price:** gate the feel on acceptance — ack
   return on the tier callback, feel fires on ack: **~40 lines across
   Sled + SM + VM**. (Interim, uglier: suppress commit feel for
   weakenings while a session is active, ~15 lines; the ack is the true
   fix and this seat prices the true fix.)
2. **The LOADOUT cap borrows the seal's voice.** A cap tap runs
   SM:271–278 → VM:2137–2138 `applySmartLoadout` →
   `soundEngine.playHarmonicSeal(true)` — the harmonic **seal** voice,
   for an act that seals nothing and (per F1 finding 3) silently rewrites
   every app's tier, STUDY additionally flipping the Shorts sniper +
   browser blocker (VM:2144–2156) with none of it printed at the cap.
   KLANG F-1 stands. **Price:** swap to the cap's own latch voice at
   commit (and, with Ghost Preview, silence on the preview tap):
   **~5 lines.**
3. **Semantics: zero.** Grep this run: `contentDescription`,
   `semantics`, `liveRegion`, `stateDescription` return **0 hits** in
   SM, in Sled (`BlockitSuitcaseAndSwitchesViews.kt`), and in
   `ShieldCapSelector.kt`. The status sentence is not a live region
   (SM:230–232 carries only a `testTag`); the Canvas sleds are not
   nodes; the cap announces no state. R-11 called this disqualifying,
   not polish; the wave-1 pass landed none of it. **Price:** FASSUNG's
   semantics pack (status live-region + plain-language
   `contentDescription`, sled self-description + custom actions, cap /
   routine-cap state descriptions): **≈½ builder-day**, specced in
   `finisher-fassung.md` §8.
4. **Refusal has no sound — and that part is right.** TAST's print-only
   rule for row refusals stands and this seat defends it: the cure for
   finding 1 is *silence until acceptance*, never a new refusal buzz.

Signed — **FASSUNG bench, Finisher 4 · Sound/Haptics + Semantics**,
Wave-2, under VOGT. DECLARED.

---

## F5 — FINISHER 5 · TYPOGRAPHY / PRINT (SAUM)

**Verdict: FIX-FIRST.** The words are mostly true now; the print grammar
still has a guest face, a wrong placeholder, and one unreworded banner.

**PASS, on record:** ARMED in the ROUTINES header (SM:298–299, derivation
acquitted — `liveCount`); WON'T FIRE wired and payload-widened
(SM:680, SM:688 `widthIn(min = 48.dp)`); RUNNING NOW on the meta line
from the session's own routineId (SM:665–667); the tally's mixed grammar
(Anonymous numerals, Atkinson words).

**Findings:**

1. **The Sora guest still sits in the routine name.** SM:654–659:
   `routine.name.uppercase()`, `SoraFamily`, Bold, 16 sp — in a row whose
   every other word, and the header above it, is Atkinson/Anonymous.
   R-12 item 7 evicted Sora from routine rows; it is still there.
   **Price: 2 lines** (Atkinson Bold 15 sp / `AppTitleTextStyle`,
   `maxLines = 1` + ellipsis — the meta line at SM:661–671 has no
   `maxLines` either, same patch).
2. **SEARCH placeholder still lies about what the field is.**
   SM:401–407 prints `Filter apps…`. R-6 ruled `SEARCH APPS` — it *is* a
   search; "filter" is the chips' job below it. **Price: 1 line.**
3. **The reset banner still prints the pre-R-6 wording.** SM:305:
   `SCHEDULE RESET TO YOUR DAY — TAP ANY ROW TO CHANGE IT`. R-6's ruled
   text — `YOUR DAY, SEEDED — TAP A ROW TO OPEN IT · TIMES YOU SET ARE
   NEVER CHANGED` — and the 48 h residency (vs 14 days) are both
   unlanded. **Price: 1 line** in SM; residency is a caller-side
   constant, minutes.
4. **The status sentence breaks the screen's own type grammar.**
   SM:214–216 sets the *whole* headline — words and `ENDS HH:MM` alike —
   in Anonymous Pro Bold 13 sp. Every other mixed line on this screen
   (tally, section headers) sets words in Atkinson, numerals in
   Anonymous. The loudest line on the screen is the only one shouting in
   mono. **Price:** annotated-string build splitting digit runs to
   Anonymous, words to Atkinson Bold 14 sp: **~35 lines**, shared with
   F1's slot patch (PZ-2) — not additive.
5. **The ratchet words still say VAULT.** The banner text arrives from
   VM:2118 (`…CANNOT WEAKEN '…' DURING ACTIVE VAULT`) and VM:2092
   (`…UNTIL 00:00` for the unlock path) — R-7 ordered the word gone
   (`CANNOT WEAKEN 'X' DURING THIS SESSION`). Print truth includes the
   VM's strings, not just this file's. **Price: 2 lines in VM.**

Signed — **SAUM, Finisher 5 · Typography/Print**, Wave-2, under VOGT. DECLARED.

---

## L1 — FEEL 1 · WEIGHT (HAUT)

**Verdict: PASS on restraint, FIX-FIRST on the slot (shares F1/F3).**

The choreography laws hold in the current code by absence, and absence
is the correct default here: Shield adds **no** sound, **no** haptic,
**no** pulse or loop at any proof change — the seal's Throw remains the
engine's alone, and a re-proof is stated, never re-sounded. That is the
SETTLE / RELEASE / SLIP score's silence clauses, landed by doing nothing,
and this seat acquits them.

What lacks weight is the sentence's body. LIVE and NO_SESSION differ
only in ink tone (SM:222–228); there is no glyph, no density, no fixed
mass on the page (F1 finding 1) — so "the wall is standing" does not
*feel* heavier than "it is not", it merely reads differently. Weight on
this screen is solidity, and solidity is exactly the missing 10 dp
glyph. One more keying note, recorded not charged: the room's night is
claim-keyed (`effectiveNight = isDarkTheme || isSessionActive`,
SM:139) while the sentence is proof-keyed — HAUT's standing ruling
accepts this split (night honours the intention, print carries the
proof) **provided** the palette never dawns back on a SLIP; the current
boolean cannot dawn mid-session, so the proviso holds today.
**Price:** carried by F1/F3 (≈60 + ~25 lines, shared). No separate
choreography build is priced or wanted in wave-2.

Signed — **HAUT, Feel 1 · Weight**, Wave-2, under VOGT. DECLARED.

---

## L2 — FEEL 2 · LIGHT (LUFT)

**Verdict: FIX-FIRST.** The 3:30 AM room test fails on one line and
passes on the rest of the room.

The room itself is calm and honest: night falls for a session, the
tally and catalogue print in full ink, nothing glows, nothing performs.
But the line a tired man reads first is, in its two alarm states, the
dimmest print in the room (F3 finding 1: `scheme.error`, SM:227, 3.49:1
by day), and the quiet state is not far behind at night:
`onSurfaceVariant` (SM:226) is the weakest token in the night scheme
(≈3.5:1 per finisher-panzer.md §4) — acceptable for a *scheduled* down
wall, since the verb carries it and nothing is being claimed, but it
means the sentence's whole light budget is spent inversely: loudest
fact, weakest light, in both directions. LUFT's A-1/A-3 from the lead
file (session-night keyed on proven LIVE; a stepped night for sleds)
remain the named atmosphere strengthening — recorded here as a
**remainder**, not a wave-2 MUST: the room does not currently *lie*
about the session, it merely does not change when proof arrives.
**Price:** the MUST part is F3's (~25 lines, shared). Stepped/proof-keyed
night: unpriced in this run, lead's follow-up.

Signed — **LUFT, Feel 2 · Light**, Wave-2, under VOGT. DECLARED.

---

## L3 — FEEL 3 · TIMING (SCHWUNG)

**Verdict: FIX-FIRST.** The clocks underneath are honest; the print's
timing is ungoverned.

**PASS:** the staleness clock is one law everywhere: SBS:146 judges the
beat by `MonolithRepository.SHIELD_GAP_THRESHOLD_MILLIS` (Repo:161,
10,000 ms), the same constant the repository's own latch uses — no
second, softer threshold invented for the screen. The tally's timing is
governed (250 ms coalesce, SM:173–175; 260 ms Curve A re-strike,
SM:865–870).

**Findings:**

1. The status sentence inherits **none** of that governance: no
   coalesce, no re-strike (F2 findings 1–2, same evidence SM:211–213).
   Its timing defect is worst exactly at the boundary it exists for —
   beat age crossing 10 s (SBS:146–149) while a session stands.
2. `ENDS HH:MM` never ticks and changes only by re-strike of the whole
   headline (SBS:152–158 derives it from the session's own numbers) —
   acquitted, and any future per-second anything on this line is
   banned by this seat.
3. The ratchet banner self-erases on the VM's timer while the man may
   still be scrolling to it — timing as a truth problem, cured by
   MUST-5's in-place print (which must **not** self-erase mid-read;
   it clears on the next accepted commit or on leaving the row's group).

**Price:** carried by F2 (~35 lines total). No new durations, no new
curves are priced — everything is paid from the house vocabulary.

Signed — **SCHWUNG, Feel 3 · Timing**, Wave-2, under VOGT. DECLARED.

---

## L4 — FEEL 4 · TOUCH + SOUND (TAST)

**Verdict: FIX-FIRST — the strongest feel defect in this run.**

The hand is lied to at the exact point of refusal. The sled's commit
feel fires before the app has decided (Sled:296 before Sled:297; the
refusal is only computed later, VM:2118), so a ratchet-refused weakening
*feels* — and sounds — like it landed, while the top banner (SM:237–255)
prints the refusal somewhere the man's eyes are not. Touch, sound and
print give three different answers to one gesture. R-7's whole point
was one answer, at the point of refusal.

Second: the LOADOUT cap's tap is answered by the seal's voice
(VM:2138) for a silent mass-rewrite (F1 finding 3, F4 finding 2) — the
heaviest voice on the screen spent on its least honest act. The voice
scale is inverted: a one-app sled commit and a whole-phone rewrite
should not be neighbours, and neither should outrank the seal.

**What is right:** refused flips never fake-*commit* in print — the
sled re-seats and the tier does not change (audit acquittal, R-10);
detent/commit grading inside an accepted throw is acquitted. The lie
is in the feel channel only, which is why it survived a print audit.
**Price:** carried by F4 (ack-gated feel ~40 lines; cap voice ~5
lines). This seat adds one law for the patch: a refusal is print,
only print, and print **first** — no voice, no knock, no buzz may be
added to "help" the refusal land.

Signed — **TAST, Feel 4 · Touch+Sound**, Wave-2, under VOGT. DECLARED.

...[truncated 6732 chars]
---

## L5 — FEEL 5 · INSTANT-READ (the 1 AM glance seat)

**Verdict: FIX-FIRST.** Two of the audit's three one-glance questions
now answer instantly; the third — *what does PEEK actually mean, and
where do I change a website* — still requires reading the whole screen
or already knowing.

**PASS, the glance that works:** status sentence first (SM:211),
counts that sum to the phone directly under the routines (SM:351),
rulings grouped with their rows (SM:463–475), `RUNNING NOW` readable
off the meta line (SM:665–667), `WON'T FIRE` readable off the cap
(SM:680). A man can now prove *whether* the wall stands and *how* it is
set in under two seconds. That was merge item #1 and it landed.

**Findings — where the glance still fails:**

1. **PEEK's contract prints nowhere on this screen.** The group header
   is the bare word (SM:888–899 `CatalogueGroupHeader` prints only
   `tierWord(tier)`). R-6 ruled the contract line into the PEEK header,
   derived from the constants — and the constants are sitting there:
   Repo:106 `PEEK_DURATION_SECONDS = 120`, Repo:108
   `PEEK_COOLDOWN_MILLIS = 20 * 60 * 1000L`. A first-time or 1 AM reader
   sees a word, not a deal: *"PEEK — 2 MINUTES, THEN SHUT AGAIN ·
   20 MIN BETWEEN PEEKS"*. **Price: ~10 lines** (format from the two
   constants, never hardcoded).
2. **Websites fail the glance twice:** position (last section, after
   the catalogue — F1 finding 4) and honesty of the ADD: the click
   handler clears the field unconditionally after calling the add
   (SM:571–575 `onAddSite(newSiteText); newSiteText = ""`), so a
   rejected domain vanishes exactly like an accepted one — print that
   claims success (R-3's rider). **Price:** move + cap ≈25 lines
   (shared with F1); rejected-ADD prints why and keeps the text
   **~15 lines** (needs the add's result surfaced to this composable).
3. **SEARCH's placeholder mislabels the tool at first read**
   (`Filter apps…`, SM:403) — a tired glance reads "filter", reaches
   for the chips, and the search goes unused. Rides F5 finding 2
   (1 line).
4. **The cap fails instant-read by design, not by dress:** its position
   can mean "what I last picked by hand" while a routine's loadout is
   live (F1 finding 3) — no glance, however fast, can read a fact the
   surface does not print. Rides MUST-4.

Signed — **Instant-Read seat, Feel 5**, Wave-2, under VOGT. DECLARED.

---

## MUST — fixes this run recommends as wave-2 code (ordered)

1. **MUST-1 · Status slot + light (F1/F2/F3/L1/L2/L3, one patch).**
   Fixed 40 dp slot; full ink in **all** states (`scheme.error` off this
   line entirely); 10 dp solidity glyph carries state (solid / hollow /
   55 %); mixed type grammar (Atkinson words, Anonymous numerals,
   14 sp); UNPROVEN splits at its comma inside the slot, never
   ellipsised; whole-line 260 ms Curve A re-strike + 250 ms anti-flap
   coalesce; banner folded into the same item (kills the dead 24 dp
   gap) with its text colour tokenised to `onPrimaryContainer`.
   **≈½–1 builder-day, ~120 lines in SM.** This is the lead file's
   named follow-up item #1, confirmed by five seats in this run.
2. **MUST-2 · Semantics pack (F4).** Status live-region + plain-language
   descriptions; sled self-description + custom actions; cap and
   routine-cap state descriptions. R-11: disqualifying if absent.
   **≈½ builder-day.**
3. **MUST-3 · No false commit feel (F4/L4).** Sled commit feel gated on
   VM acceptance (ack on the tier callback); LOADOUT cap loses the
   harmonic-seal voice (~5 lines of this item). **~45 lines.**
4. **MUST-4 · LOADOUT cap honesty (F1/L5).** Ghost Preview (first tap
   previews, second commits, STUDY's sniper/blocker side-effects named
   in the preview line) + cap derives from the **current** applied
   loadout and prints its source (`BY HAND` / `BY <ROUTINE> · UNTIL
   HH:MM`; where unprovable, prints LAST APPLIED as a fact) + LOADOUT
   header killed. **~135 lines net.**
5. **MUST-5 · Refusal at the point of refusal (F1/F4/L3/L4).**
   A ratchet-refused weakening strikes its word **at the sled row
   touched** (in-place print, no self-erase mid-read); the top banner
   remains only for Deck-origin refusals; banner strings lose VAULT
   (VM:2092, VM:2118 → `…DURING THIS SESSION`). **Small: ~40 lines
   across SM + VM + the row's refusal state.**
6. **MUST-6 · Websites (F1/L5).** Move BLOCKED WEBSITES above ALL APPS,
   cap at ≤5 rows + SHOW ALL; a rejected ADD prints why and keeps the
   typed text. **~40 lines.**
7. **MUST-7 · PEEK contract line (L5).** In the PEEK group header only,
   derived from Repo:106/Repo:108. **~10 lines.**
8. **MUST-8 · One-line print cures (F3/F5).** `SEARCH APPS` placeholder
   (SM:403); Sora evicted from routine names + meta `maxLines = 1`
   (SM:654–671); WON'T FIRE word to full ink (SM:702); reset banner to
   the R-6 wording (SM:305) + 48 h residency. **~10 lines total.**
9. **MUST-9 · Dead wires struck (F1).** SM:90–93, SM:115–116 params
   and the unused import leave Shield's contract (R-9). **Deletions.**

## Remainders — recorded, not wave-2 MUSTs

- **Third witness (usage access).** Lead file's residual stands: the
  shield's polling door also needs usage access (SALZ C-2); BLOCKING
  NOW's two-witness proof does not include it. Named strengthening
  (FUNKE Mechanism A / root-stamp) unbuilt.
- **Websites-empty re-arm (Una's flag, via the audit).** Emptying the
  sites list silently re-arms factory domains; untouched by MUST-6's
  move — a machinery item for Ren, named in the audit's merge list.
- **Sticky status sentence (SALZ C-7 vs LUFT).** Not in this run's
  MUSTs: LUFT's standing alternative (the session-night ground as the
  travelling proof) is adopted until the phone proves otherwise;
  GLANZ's sticky-header construction is already specced if it does.
- **Stepped / proof-keyed night (LUFT A-1/A-3)** and the **Day Rail**
  routine editor (audit R-8; ±30 chips already delegated to Voss's
  batch-10 wheels by the lead) — named next builds, not wave-2 code.
- **Sled drag/draw truths (audit R-10: S-1 56-vs-54 dp, S-3 mid-drag
  re-tier, V-2 provisional cap)** — acquitted mechanism, small truths
  still owing; they ride the finish pass, not this run's MUST list.
- **Category filter words vs taxonomy (Muede T7)** — filed to Ren as a
  naming charge in the audit; not fixed silently here.
- Everything above is paper-verified only: the wave-1 pass's 17 truth
  tests have never run, and no seat in this run compiled or ran
  anything. Contrast figures are token computations from the prior
  finish filings, not device measurements.

## DECISION-FOR-SARTHAK — items only he can settle

1. **Cap interaction (MUST-4).** Ghost Preview makes the LOADOUT cap a
   two-tap act (preview → commit) where today it is one tap. The audit
   ruled it; but the cap was his own Oct 29 pick, and the extra tap is
   a feel change to *his* chosen control — confirm the two-tap preview,
   or rule one-tap with a post-commit undo line instead.
2. **Third witness strictness.** If usage access is added as a third
   conjunct, BLOCKING NOW becomes harder to earn (more honest, more
   NOT BLOCKING prints when a permission lapses). His call on how
   strict the sentence should be; the machinery price is FUNKE's
   Mechanism A, unpriced in this run.
3. **Nothing else.** All other items above are ruled by the audit /
   lead synthesis and need no decision from him; per his presentation
   rule, items 1–2 are the only questions this file raises to him,
   routed through VOGT/Ren — no seat messages Sarthak directly.

— Wave-2 seat-runner, for VOGT (lead, issue #7 cell), Oct 30 2026.
DECLARED: one file written (`studio/plan/wave2-seats-finish-feel.md`),
no app code, no commits, no compile.
