# AUDIT — Issue #4: Intentionality audit of the Deck top strip

**Seat:** Intentionality Designer (Anchor seat I-1, issue #4) — dedicated-100 programme.
**Date:** Oct 30, 2026.
**Status:** filed live on issue #4 as the audit was written; this file is the record of record.
**Nobody compiles.** This audit is DECLARED, like everything else on this branch.

## The law

Every element intentional. Words only where the object can't speak. No telemetry or
spec-speak on the face. Fonts Sora / Atkinson Hyperlegible / Anonymous Pro. Never green.
Never AI-generated images. Sarthak's order on this surface, verbatim in spirit: *be
creative — buttons, icons, objects — not dumbly written text*, and his measuring question:
*does Blockit ever write like this?* (It does not. Blockit lets the object carry the state.)

## The interrogation

Every element on the strip was put through the same five questions, in order. An element
that fails any question is flagged; an element that survives must say *why*, in one
sentence a holder of the phone — not an engineer — would accept.

1. What question in the holder's head does this answer?
2. What decision or action changes because it is here?
3. Can the object carry it without words?
4. Is it true in every state and tense it prints in?
5. If it vanished, would the holder notice — or worry?

## Evidence base

- The issue body: the strip as screenshotted Oct 30, 02:49 IST —
  `BLOCK OFF · APPS 89 · TIME ENDS 07:28` over `BIND NOT HELD · PROOF: POSITIVE`.
- `app/src/main/java/com/example/ui/components/MonolithCustomComponents.kt` (Slice D):
  the strip is three cells (BLOCK / APPS / TIME) plus the seam stamp pair printed by
  `seamMarksFor(warrant)` — left mark a BIND sentence, right mark a `PROOF:` grade —
  and `endsAtText(endMillis)` = `"ENDS " + HH:mm`.
- `app/src/main/java/com/example/ui/screens/TactileDeckScreen.kt` (Slice D host):
  `blockCellFor` prints `ON` / `OFF` / `UNKNOWN` with `StripDensity` PASS / FAULT /
  UNKNOWN; the idle TIME cell prints `NO END`, `INVALID`, or a hypothetical
  `ENDS HH:mm` computed as `nowFrame + totalSpanMinutes` from the dial — a plan,
  printed in the grammar of a fact.
- The cell's redesign of record: `studio/plan/DESIGN-issue-4-the-bolt.md` (THE BOLT)
  and `app/src/main/java/com/example/ui/components/SealBar.kt` on this branch, which
  prints exactly two text items: `HH:mm` numerals and one engraved `END`.

## Verdict table

| # | Element (as shipped) | Verdict | Surviving form |
|---|---|---|---|
| 1 | BLOCK state (`BLOCK` + `ON`/`OFF`/`UNKNOWN`) | **REDESIGN** — the fact keeps, the text dies | Bolt position + jewel light; OFF carried by exposed warning bed |
| 2 | APPS count (`APPS 89`) | **REMOVE** from the face | Lives on the Shield header + the split imprint (LOCKED/PEEK), nowhere on the Deck face |
| 3 | TIME / ENDS readout (`TIME` + `ENDS 07:28`) | **REDESIGN, conditional keep** | Numerals only, only for a sealed live end, in a glass window; `END` engraved once; blank otherwise |
| 4 | BIND line (`BIND NOT HELD` and family) | **REMOVE** from the face | Stays inside the machine: warrant, `seamMarksFor` table, unit tests, logs |
| 5 | PROOF line (`PROOF: POSITIVE` and family) | **REMOVE** from the face — headline flag | Proof is embodied in the BLOCK claim's own degradation (dark jewel / caught bolt), never footnoted |

Sub-elements (§6): the `BLOCK`/`APPS`/`TIME` header labels — **REMOVE**. The `·`
separators — **REMOVE**. `StripDensity` tone-stepping as a meaning channel —
**REDESIGN** (state moves to position / light / area). The seam hairline — **REDESIGN**
(it survives only as the instrument's housing edge). The 53dp rent — **PASSES**, and
the Bolt's 60dp housing passes with it (60dp total against the 65dp the strip + seam
used to rent; every added dp is bought by a named function, §7).

---

## §1 · BLOCK state — REDESIGN (the fact keeps, the text dies)

**What it is.** Cell one of three: the label `BLOCK` over/before the word `ON`, `OFF`
or `UNKNOWN` (`blockCellFor` in the Deck host), toned PASS / FAULT / UNKNOWN. In the
screenshot's state it prints `BLOCK OFF`.

**Interrogation.**
1. *What question does it answer?* "Am I protected right now?" — the single question
   the Deck exists to answer. This is the strip's reason for existing at all.
2. *What changes?* Everything: whether the holder starts studying, fixes a grant, or
   distrusts the app. The only element on the strip that passes Q2 outright.
3. *Can the object carry it?* Yes — position is the oldest state language there is.
   A bolt home vs a bolt open needs no caption; that is the Bolt's whole thesis, and
   it is correct.
4. *Is it true in every tense it prints in?* The word, no — not reliably. The host
   itself keeps two tenses of this one fact (the freshness-checked print state for
   idle, the frozen seal verdict for running, the live-OFF override on top), and the
   design record (SALZ C1) shows one frame could legally print three verdicts of one
   wire across strip, seam and sentence. `UNKNOWN` is the honest word in the set and
   it is still a word doing a shrug. The Bolt's one-derivation law (one folded
   verdict per frame, pose = geometry, lamp = proof) is the fix, and this audit
   ratifies it as the condition of BLOCK's survival.
5. *If it vanished?* The holder would worry — rightly. The *fact* is load-bearing.

**Verdict.** The BLOCK fact **keeps its place** — it is the strip. The BLOCK *text*
is removed: no label, no `ON`/`OFF`/`UNKNOWN` word on the face. It survives as form:
bolt THROWN + pin dropped + jewel lit (proven), bolt CAUGHT at ⅓ (unproven — never
½, half reads as a resting state), bolt OPEN with the warning bed exposed (off).
One condition, from the issue's own acceptance: **OFF must stay unmistakable.**
The Bolt carries that by area, not light — exposed `#D63E06` bed ≈125.7 luminous
proxy units against the lit jewel's ≤41.9 budget (the §6 glance gate's ≥3× read at
10% brightness). That asymmetry is the reason the bed earns its area and the jewel
keeps its ≤9dp cap (§7). For the ears, the spoken set must keep OFF just as
unmistakable — LETTER's closed 11-sentence set does; any future edit to those
sentences reopens this verdict.

## §2 · APPS count (`APPS 89`) — REMOVE from the face

**What it is.** Cell two: the label `APPS` and a count that the ViewModel derives as
LOCKED + PEEK_2M summed (`MonolithViewModel` Slice D note; the seal-freeze count in
the Deck host).

**Interrogation.**
1. *What question does it answer?* None the holder is asking on the Deck. Nobody
   wakes up wondering "is it 89?" The number's meaning lives where the list lives —
   on the Shield, where each app can be seen, added or removed.
2. *What changes?* Nothing. No Deck action takes the count as input. It is a readout
   in search of a decision.
3. *Can the object carry it?* The question doesn't arise — there is nothing to carry.
4. *Is it true?* Worse than untrue-by-accident: it is an aggregation lie by
   construction. 89 sums apps that *shut* (LOCKED) with apps that merely *peek*
   (PEEK_2M, a 2-minute airlock lease) under one bare number next to BLOCK — the
   exact offence already convicted on the imprint as UNA2-02 ("the number was real,
   the noun was the lie") and fixed there by splitting the count. This strip never
   got the fix. The count also moves without the holder acting (catalog reconcile
   prunes dead entries), so it fails as an identity anchor too.
5. *If it vanished?* Nobody would notice. That is the definition of an element
   without a reason.

**Verdict.** **REMOVE** from the Deck face, in all tenses — idle, sealed, running.
Not redesigned, not engraved, not shrunk to a corner (FUNKE's engraved `89` was
considered by the cell and rejected; this audit concurs and makes it a standing
condition, §8). The information is not destroyed: it keeps its two honest homes —
the Shield header, and the imprint's split tenant (`{n} HELD · {m} PEEK` /
`{n} WILL SHUT · {m} PEEK`, zero segments never printed). On the Deck it was
telemetry wearing a label.

## §3 · TIME / ENDS readout (`TIME ENDS 07:28`) — REDESIGN, conditional keep

**What it is.** Cell three: the label `TIME` and `endsAtText(...)` = `ENDS HH:mm`,
a pure function of a stored end-milli while running — and, at idle, of
`nowFrame + totalSpanMinutes` from the dial (Deck host, idle TIME cell), where it
also prints `NO END` (stopwatch) and `INVALID` (dial out of range) as sibling states.

**Interrogation.**
1. *What question does it answer?* While a session runs: "when am I free?" — a real
   question, asked at a glance, mid-study. At idle: "when *would* I be free if I
   started now for the dialled span?" — a planning hypothetical.
2. *What changes?* Running: the holder plans around the end. Idle: nothing that the
   dial and the hero card don't already say, in their own places.
3. *Can the object carry it?* The *fact of an end* can (a window, lit or blank); the
   *time itself* cannot — a clock reading is the textbook case of words/numbers the
   object cannot speak. This is one of exactly two places on the face where print
   earns its keep (the issue's acceptance says the same: the end time must stay
   readable at a glance).
4. *Is it true in every tense it prints in?* Running: yes. Idle: **no** — the old
   strip printed the hypothetical end in the same grammar, tone and cell as the real
   one (the "ever-sliding finish line": the printed end moved as the clock moved,
   because it was `now + span`). A planned end printed without a label is the old
   lie in new metal, whoever prints it. `NO END` and `INVALID` are also confessions
   the face should never make in telemetry grammar: stopwatch's lack of an end is
   shown by a blank window; an invalid dial is the dial's own state to carry, at
   the dial.
5. *If it vanished?* Running: the holder would notice and worry — keep. Idle: nobody
   would notice — remove.

**Verdict.** **REDESIGN with a presence law.** What survives: `HH:mm` numerals only
(Anonymous Pro Bold 20sp, 24-hour, absolute clock end — never a countdown, never a
remaining-time figure; remaining time already lives on the hero card), inside the
glass time window, **only while a session with a real end is sealed and running**
(including the ENDING_SOON inversion, §7). What dies: the `TIME` label, the word
`ENDS` (the word `END`, engraved once on the bezel below the glass, is the entire
vocabulary the window needs), the idle hypothetical, placeholders (`--:--` banned),
`NO END`, `INVALID`. When there is no sealed end, the window is **blank** — an empty
window is the object saying "nothing is running," and it says it without a word.
ENDING_SOON (≤300s) never moves the bolt; it inverts the whole window to the brass
plate pair. Pose is truth; the window is the clock. They do not impersonate each other.

## §4 · BIND line (`BIND NOT HELD` and family) — REMOVE from the face

**What it is.** The seam line's left mark, from `seamMarksFor`: `BIND HELD · THIS
VISIT`, `BIND HELD · UNSEEN`, `BIND NOT HELD`, `BIND UNCONFIRMED` — the live state of
the accessibility service's system bind and whether its pipeline root-stamped this
residency, per `WatchStore`'s BIND registry.

**Interrogation.**
1. *What question does it answer?* An engineer's question: "is the AccessibilityService
   currently bound, and did it stamp this process residency?" The holder's actual
   question — "can I trust that blocking works?" — is §1's question, already answered
   by the BLOCK fact itself. BIND answers it a second time, in the mechanism's own
   vocabulary.
2. *What changes?* Nothing the holder can act on. The distinctions the line draws
   (HELD·THIS VISIT vs HELD·UNSEEN vs UNCONFIRMED) map to no holder action: every
   actionable derivative already exists one slot below the strip, in plain words,
   with a tap target — the refusal sentences (`BLOCKING IS OFF — TAP TO FIX`,
   `CAN'T CHECK BLOCKING — TAP TO RETRY`, `USAGE ACCESS IS OFF — TAP TO FIX`).
   A diagnostic that duplicates its own remedy, in a harder language, directly
   above the remedy, has no reason to be on the face.
3. *Can the object carry it?* The part of BIND that matters to trust — "is the proof
   live *now*" — is carried by the jewel and its freshness law (a stale read darkens
   the claim; see §5). The rest is plumbing.
4. *Is it true?* Yes — the seam table is scrupulously truthful (its total-function
   discipline caught a real `BIND HELD`-for-unbound bug in its own history). Truth
   is not the charge. **Placement** is: a true sentence can still be telemetry.
5. *If it vanished?* From the face: nobody would notice. From the *system*: the
   studio would go blind — which is why removal here means removal from the face,
   never from the machine.

**Verdict.** **REMOVE from the face, entirely, in all states.** `BIND` is Android
bind-lifecycle spec-speak — the studio talking to itself in public (the design
record's phrase, and it is exact). What stays, deliberately: the warrant,
`seamMarksFor` as the wire's internal grade record, its unit tests
(`SliceDGestureAndStripTest`, `TruthCar3InterceptTest`), and the logs. The Bolt
prints no BIND descendant: no sub-state of the jewel, no pin behaviour, no engraving
may re-encode `THIS VISIT` / `UNSEEN` / `UNCONFIRMED` for those who know the code —
a code only engineers can read is BIND with the serial numbers filed off (§8,
condition 3).

## §5 · PROOF line (`PROOF: POSITIVE` and family) — REMOVE from the face (headline flag)

**What it is.** The seam line's right mark, from `seamMarksFor` and its staleness
overload: `PROOF: LIVE BIND`, `PROOF: BIND ONLY`, `PROOF: POSITIVE`, `PROOF: NONE`,
`PROOF: STALE` — Rosa's grade of how the wire's legs support the BLOCK claim.

**Interrogation.**
1. *What question does it answer?* "How good is the evidence for the thing printed
   next to it?" — a meta-question. It is a label about the other labels: the strip
   grading its own homework, on its own face.
2. *What changes?* Nothing. No holder action is keyed to the difference between
   LIVE BIND and BIND ONLY; the one actionable consequence of weak proof — don't
   trust the seal — is a change to the BLOCK claim itself, not a footnote to it.
3. *Can the object carry it?* Yes, and this is the crux: **proof belongs inside the
   claim, not beside it.** When proof is live, the jewel lights. When proof degrades,
   the jewel goes dark and (mid-session) the bolt's pose keeps the session fact while
   the lamp drops the claim — the Bolt's pose/lamp split is exactly PROOF embodied.
   A face that needs a PROOF line is confessing its headline can't be trusted; the
   cure is to make the headline honest, not to append a disclaimer.
4. *Is it true?* The grades are honestly derived — and one of them is a glance-level
   lie anyway: `PROOF: POSITIVE` prints beside `BIND NOT HELD` in the screenshot
   state, because POSITIVE means "the OFF rests on a directly observed dead signal."
   That is a defensible sentence in a log and an indefensible one on a face: at
   arm's length, at 1 AM, POSITIVE reads as *good news* on the exact frame where
   there is none. Vocabulary that requires its definition to not mislead has failed
   Q4 on the face, whatever it does in the table.
5. *If it vanished?* The holder loses nothing; the strip loses its most
   studio-serving element. The studio loses nothing either — the grades continue
   to exist where grades belong (tests, logs, this audit's §4 note).

**Verdict.** **REMOVE from the face, entirely, in all states — this audit's headline
flag.** No `PROOF:` prefix, no grade word, no staleness word on the face. STALE in
particular is fully absorbed by behaviour the holder can already see: on a stale
read the jewel is dark and the refusal slot stays silent until a fresh read lands
(the host's existing staleness law). If it isn't proven *now*, the face shows an
unproven instrument — it does not print the word STALE and call that honesty.

---

## §6 · Sub-elements — the printout's connective tissue

These were not named in the brief's five, but intentionality is total or it is
nothing: they stood on the same strip and they get verdicts too.

- **The header labels `BLOCK` / `APPS` / `TIME` — REMOVE.** Category headers are
  printout furniture: they exist to tell a reader which column of a report they are
  in. An instrument has no columns. (The one surviving engraved word, `END`, is not
  a header — it is part of the window, disambiguating the numerals as *end*, not
  *now*; it is the smallest word that does that job, which is why it alone survives.)
- **The `·` separators — REMOVE.** Typographic stitching for sentences that no
  longer exist. An object's parts are separated by space and material, not middots.
- **`StripDensity` (PASS / FAULT / UNKNOWN as tone steps on text) — REDESIGN.**
  Ink-density as a meaning channel asks the holder to read how *dark* a word is and
  decode a verdict from it — a code, again, and the first to clip (10sp, per the
  design record). State moves to channels that survive blur and peripheral vision:
  position (bolt), light (jewel), area (warning bed). Tones remain as *material*
  (the bolt's fills, the glass) — never as the message.
- **The seam hairline under the cells — REDESIGN.** It existed to underline a stamp.
  With the stamp gone, a hairline survives only where it bounds the instrument:
  the Bolt housing's top and bottom 1dp edges. A line that underlines nothing is
  decoration; a line that edges a housing is structure.
- **The 53dp pinned height — KEEP, and the Bolt's 60dp passes the rent test.** The
  pinned address itself earns its place: the zone under the slide never changes
  height across the seal (Δy = 0), and that stability is a promise the Deck makes
  with the holder's thumb. The Bolt's 60dp is *less* than the 65dp the strip + seam
  rented, and each dp of it is bought by a named function (§7) — that is what rent
  paid in full looks like.

## §7 · The replacement, audited — the Bolt's parts must earn their places too

Killing the text dump does not exempt its successor. Each part of THE BOLT /
`SealBar.kt` was interrogated with the same five questions. Verdicts:

- **Bolt + channel (90×34dp bolt, travel = one bolt width, detents 0 / ⅓ / 1) —
  KEEP.** It *is* §1's surviving form: position answers "am I protected?" before a
  single word could. The ⅓ CAUGHT pose earns its fraction: half would read as a
  resting state, and this instrument has exactly two resting states and one caught
  pose. The bolt is not a handle (no drag, no grip) — correctly: a state display
  that invites a gesture it doesn't honour would be a lie of affordance.
- **Warning bed (`#D63E06`, bolt-footprint, covered exactly when home) — KEEP.**
  The hardest-working surface on the new face: it makes OFF *interruptive by area*
  while fault never lights (health is the only thing that lights — TON's law, and
  the right one). Its size is not taste; it is the glance gate's arithmetic
  (≈125.7 vs the jewel's ≤41.9). Any future change cutting the exposed bed by >20%
  re-triggers the whole glance battery (the cell's own protective rule — ratified).
- **Keeper socket + collar (socket exposed at OPEN/CAUGHT, collar overlapped 4dp
  when thrown) — KEEP, one watch.** The socket earns its place: a bolt must visibly
  arrive *somewhere*, and the exposed, orange-painted socket is OFF's second,
  foveal confirmation. The collar's 4dp overlap is the night occlusion cue (rank 2
  in DUNKEL's night-weight order) — structure, not ornament. The watch: the socket
  interior's orange must stay *paint* (matte, mode-invariant), never a glow.
- **Locking pin + notch — KEEP, with the audit's sharpest condition.** The pin is
  the element closest to smuggling BIND back onto the face: a dropped pin "means"
  the seal is locked-and-held. It survives for one reason only — it is not a code,
  it is a *mechanical consequence*: the notch is only under the pin when the bolt is
  THROWN, so the pin *cannot* drop in any other pose; geometry enforces the law
  instead of a label asserting it. Condition: the pin must never gain states. No
  half-drop, no pin colour, no pin behaviour keyed to warrant sub-states — the day
  the pin encodes anything beyond *thrown / not thrown*, it becomes BIND in metal
  and this verdict flips to REMOVE.
- **Jewel in bezel (≤9dp jewel, bezel disc, brass = proven idle, magenta =
  running, dark = everything else) — KEEP.** The jewel is PROOF embodied (§5): the
  one light on the face, and health is the only thing that lights it. The ≤9dp cap
  and the named luminous budget constant (41.9) earn their severity — without them
  the lamp out-shouts OFF's bed and the glance gate fails by the whole amount. The
  1dp halo ring on RUNNING earns its place on BLICK's blur evidence (hue alone at
  2.5dp-equivalent failed; the ring gives the lit states a *size* difference).
  Brass/magenta both sit in the house palette; no green anywhere near this face —
  verified against the design's token table.
- **Time window (102×36dp glass + `HH:mm` + engraved `END`) — KEEP** under §3's
  presence law, which is part of this verdict, not a footnote: blank when no sealed
  end exists, numerals never alert-coloured (ENDING_SOON inverts the whole window
  as one measured block instead), density-only scaling (an honest clip at font
  scale ≥1.3 beats a silently shrunk digit).
- **Housing (60dp band, radius 0, hairlines top/bottom) — KEEP.** A band, not a
  card: it is the Deck's pinned furniture, and its height never changes by 1dp in
  any state. All pose changes are offsets *inside* it. That invariance is the
  element's reason.
- **What the Bolt correctly does NOT contain:** no count, no state label, no brand
  or serial engraving, no track-end letters, no countdown, no tooltip, no
  interactivity theatre. Absences are design decisions; these ones are right, and
  §8 makes several of them standing conditions so they stay absent.

## §8 · Standing conditions handed to the ANTLITZ cell

Inputs to the design, filed live — not a postscript. The cell's Bolt already meets
most of these; they are recorded so a future merge pass cannot quietly unmeet them.

1. **No count on the Deck face, in any form** — printed, engraved, notched or
   encoded. The count's homes are the Shield header and the split imprint. (§2)
2. **No grade vocabulary on the face, by family:** BIND / PROOF / POSITIVE / STALE /
   HELD / SEALED / LEASE / WARRANT and kin stay in code, tests and logs. (§4, §5)
3. **No re-encoding:** no visual channel on the face (pin, jewel sub-states, bed
   patterning, window behaviour) may encode warrant sub-states beyond the folded
   verdict the Bolt already shows. If only an engineer can read it, it is telemetry
   and it goes. (§4, §7-pin)
4. **OFF unmistakable, twice over:** by area on the face (exposed bed ≥3× the lit
   jewel's luminous total at the glance gate) and by word in the spoken set. A
   change to either reopens §1's verdict.
5. **The window's presence law is load-bearing:** numerals only for a sealed live
   end; blank otherwise; no placeholders, no idle hypothetical, no exceptions for
   "helpful" plan previews on this surface. (§3)
6. **Proof degrades the claim, never footnotes it:** any future state whose proof
   weakens mid-session must move the lamp (and only the lamp) — the sentence slot
   below the strip, not the strip, is where words about faults live. (§5)
7. **Fonts and palette, verified on this surface:** Sora / Atkinson Hyperlegible /
   Anonymous Pro only (numerals Anonymous Pro Bold, `END` Atkinson Hyperlegible);
   never green; the one orange is `#D63E06`, mode-invariant; brass `#E2BC39`;
   magenta follows the house day/night split. No AI-generated imagery anywhere on
   the instrument.

## What removal does not mean

Nothing in this audit touches the truth machinery *inside* the app. The warrant,
WatchStore's BIND registry, `seamMarksFor`'s grade table and its tests, and the
refusal sentences all stay — they are how the machine knows, and how it speaks when
something is actually wrong and actionable. The audit's claim is narrower and
harder: **knowing is the machine's job; the face's job is to be an object whose
state can be read at a glance, in the dark, at 1 AM, by the person holding the
phone — not a printout of the machine's diary.**

Five elements stood on the strip. One fact (BLOCK) survives as an object, one
reading (the end time) survives under a presence law, and three (the count, BIND,
PROOF) are removed from the face with their reasons stated above. That is the
audit.

— Intentionality Designer (Anchor seat I-1, issue #4)
