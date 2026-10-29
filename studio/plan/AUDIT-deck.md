# FULL-APP BRUTAL AUDIT — THE DECK (home screen)
## Coordinator verdict · ROST · Fri Oct 30, 2026, ~01:45 IST

Commissioned by Sarthak (Oct 30, 01:15 IST) after he had to point out
himself that the home page feels cluttered and no critic had ever
filed it. Crew, all seated flat under Rost, every filing read whole
by the coordinator: DENS (Critics, density — census lead) · PLAN
(Designers, information architecture — verdicts lead) · VEGA (Wild,
physics — premise attack) · MUEDE (Humans, tired 1 AM — tests lead) ·
SCHEIN (Finishers, Deck bench — the finish) · FREMD (fresh eyes —
cold read, code first). Addition from Ren (01:21): verdicts alone do
not discharge the audit — a complete alternative presentation is
owed. It is filed (§8) and adopted (§9).

Sources: TactileDeckScreen.kt whole (1,069 lines), MainActivity.kt
header band + host call, MonolithCustomComponents.kt (header, strip,
sentence, imprint, chips), GuardLine.kt, BlockitCoreViews.kt (wheel,
slide), theme files. Calibration: Blockit's home/start layouts.
Paper only. Nothing compiled — Ren compiles, only on Sarthak's word.

## §1 · THE JOB — RULED

**The Deck's one job: let him set the session he means, see that the
machine can actually hold it, and seal it — in that order, and
nothing else on the page.** (Plan's sentence, adopted as the law of
this audit; Dens's compression — "set a span, see it is true, slide
once" — is its print form.) A fact earns home tenancy only if it
changes the set, the proof, or the seal. Everything else is a guest.

## §2 · INVENTORY — THE SCREEN AS IT STANDS

Frame (host): header band 48dp — MONOLITH brand 10sp + Guard line
15sp bold (tap = PARK's door, unsigned) — over a 1dp hairline; rail
52dp below (four named stops + a 48dp PARK zone that prints no word).

Scroll column: [1] hero card (the Deck's only raised surface, lifted
by tone alone — its shadow import is dead) — eyebrow (TOTAL /
SESSION RUNNING / BREAK · n MIN, 10sp) · hero glyph 72sp printed
THREE times stacked (±1dp ghosts) · rotary wheel 288dp (numerals
from 30; hub secretly cycles block count) · FLOW key-cap (idle) —
or, running: 6dp progress track + % text. [2] Task line: NOW +
title (secret tap = sub-steps) + DONE key-cap + QUEUE n (secret tap
= door to Tasks). [3] Utilities, settled-running only: DESK CLOCK
chip + HELD chip with count badge.

Pinned stack (never scrolls, 216.5dp by Dens's measure, ~198dp by
Plan's — the difference is the imprint's lip accounting; either way
≈ a quarter of the phone): proof strip 53dp (BLOCK / APPS / TIME
cells + two seam stamps on its foot hairline) · 72dp register
(slide; or GRACE WINDOW ABORT chip ≤10s; or EMERGENCY BREAK VALVE
chip) · refusal sentence slot 22dp (reserved even when empty) ·
imprint ~39.5dp (double rule + tenant or emptiness).

The contract behind it: the composable's signature carries 60+
parameters (61 named at the host seam). **21 are never used in the
body — verified by three independent greps, list identical in
substance across Dens, Plan, Vega, Fremd:** studyPreset, checklist,
completedTasksCount, totalQueueMinutes, selectedProfileName,
intentionText, totalFocusedMinutes, totalInterceptions, profiles,
apps, shortsReelsSniperEnabled, browserUrlBlockerEnabled,
onToggleChecklistItem, onAddChecklistItem, onDeferActiveTask,
onExtendActiveTaskOvertime, onAddQuickTaskToQueue, onOpenCurioVault,
onIntentionChanged, onProfileSelected, onSimulateIntercept.
MainActivity wires several to live VM calls the screen can never
invoke. A third of the home page's declared world does not exist
on it. Sections are numbered 1, 2, 3, 5. There is no Section 4.

## §3 · VERDICTS PER ELEMENT — RULED

- **Wheel** — KEEP. Unanimous; the best object in the app. Its fit
  problem is cured by eviction (§9 F16), not by shrinking it —
  Fremd's 288→230 shrink is held as the next lever if renders
  still clip after the evictions land.
- **Hero glyph** — KEEP as the page's single numeral. The triple
  print is ruled SINGLE-PRESS (Schein §8.1): the catch ghost dies;
  one shadow ghost survives, tokenised per scheme, one direction;
  on FLOW and on the ticking MM:SS the ghosts are OFF entirely —
  a press does not strike a moving target (Schein), tired eyes read
  ghosts as blur (Muede), three copies of one string is rent (Dens).
  Dens's kill-both stands as the fallback if it still reads soft.
- **FLOW key-cap** — KILLED as a control. It writes a preset the
  engine never reads (the file's own ESR-08 comment convicts it);
  a control wired to a ghost is theatre. FLOW becomes wheel-only,
  and the mode is PRINTED, never hidden in a number range: the
  idle eyebrow carries the recipe (§3, eyebrow). Muede's keep and
  Fremd's promote are overruled on the wiring, honoured on the
  visibility: the mode gets more print, not a second door.
- **Eyebrow** — KEEP, demoted to a label that must earn its line.
  Standing TOTAL / SESSION RUNNING are wallpaper and die. At idle
  it prints the recipe — `3 × 50 + BREAKS` (Fremd's and Muede's
  cures converge) — so the hero's arithmetic is explained on the
  face that charges it. In a break it prints `BREAK — BACK AT
  HH:MM`. A routine-born session prints its name: `SLEEP ·
  ROUTINE` (Muede). The fake day emboss shadow dies (Schein T1).
- **Progress** — the % text is KILLED (unanimous). The track in
  current form is convicted on three counts (Vega T-1/T-2/T-3):
  floored fraction (prints 3–5% that isn't true), FLOW measured
  against an invented 1,200 seconds the mode itself denies, and
  a timed bar that is remaining/target — born full, draining,
  printing 100% at the seal. Ruled: timed progress = elapsed/
  target, filling, no floors; FLOW gets NO bar (NO END means no
  bar). Under the alternative (§8) progress becomes Vega's rim
  ink and the whole derivation dies.
- **Task line** — DEMOTED to a ghost. Idle: the active task's
  title, read-only, one line (it answers "what am I sealing
  myself into?" — part of the set). Running: title + DONE.
  Sub-steps and the QUEUE door MOVE to Tasks in all states;
  QUEUE's count print dies with them (a count whose door is
  elsewhere is telemetry; the rail is the door). DONE keeps its
  key-cap body — verb in a verb's body was right.
- **Utilities row** — KEEP, state-gating acquitted by every seat.
  HELD learns its noun (the count is of notifications; print the
  word once). Its ink steps down at night (§9 F12).
- **Proof strip** — EVICTED from permanent tenancy. The band dies
  as a standing object; its facts split by owner: TIME/ENDS stays
  on the Deck (his fact); the app count's standing home is the
  imprint; BLOCK becomes state-only — it prints on the Deck when
  the wire is not LIVE and during proving, and is silent in
  health. Form: Plan's proof drawer (strip + sentence exist when
  proof is live business: proving, jammed, faulted, one settle
  beat) obeying Dens's law — fault gets more room than health.
  Dens's Two-Height Strip is the priced floor if the drawer is
  refused at merge; it is not the ruling.
- **Seam stamps** — MOVE to PARK. Five of six seats concur; the
  sixth (Vega) keeps them only as a mid-press sub-print under
  Proposal B (phase 2). `BIND HELD · THIS VISIT / PROOF: BIND
  ONLY` is the studio talking to itself in public (Muede).
- **Slide** — KEEP, promoted: the Deck's one verb. Its words
  learn state (Vega T-7: at 90% travel it still instructs him to
  start sliding — print that cannot see the hand is wallpaper).
- **Grace/valve chair** — the swap is KILLED; Muede's verdict is
  adopted whole, the strongest single-element ruling in this
  audit. One chair, one verb, whole session: **STOP** — free and
  instant inside 10 seconds; after, the same tap opens the valve
  challenge and the chair's own words carry the price (form to
  beat: `STOP — USES YOUR 1 BREAK THIS WEEK`). The valve label
  `(60S HOLD)` describes an overlay's hold, not the chip's tap —
  a label lying about its own gesture is the worst print on the
  screen. The grace countdown moves into the hero while it runs.
  The chips fill their full 72dp zone in every state (Schein
  §8.8). Price M.
- **Refusal sentence** — KEEP, and in fault it is PROMOTED: first
  element of the pinned stack, above the slide (Muede). Its words
  are rewritten plain (form to beat: `NOT SURE BLOCKING IS ON —
  TAP TO CHECK AGAIN`). Its reserved 22dp is acquitted — the
  reservation is mercy (no jump when it fires).
- **Imprint** — KEEP, tenant and all. Four seats acquit its
  tenant; Dens's kill is overruled — the duplication dies on the
  strip side, and the imprint is the seal's fixed record (Fremd:
  the one telemetry element that behaves like print). Tenant ink
  to full alpha when struck (Schein).
- **Guard line** — KEEP as host chrome and PARK's door (Plan,
  Fremd); Vega's kill-from-the-sky is overruled — it is the only
  standing door to the board that owns the warrant's detail. It
  is not counted as Deck furniture; it IS counted in §4's pile-up,
  because the eye cannot tell host from home. Its tap is unsigned
  — charged, S (§9 F20's family). Frozen-at-seal values must be
  labelled or stop printing (Muede, truth §5).
- **Header band / brand** — KEEP, acquitted (Schein: the one
  surface already at gallery standard). Brand/Guard volume
  inversion (10sp vs 15sp bold) noted; the Guard line is a door
  label and an alarm in one — its volume is ruled acceptable
  while it is the only health word standing at idle-fault.

## §4 · CLUTTER — RULED

The count, ruled: **one fact — can this phone block? — is printed
six times on one face** (Dens's census stands): header Guard line,
strip BLOCK cell, seam stamp left, seam stamp right, refusal
sentence, imprint tenant. In vocabularies it is Fremd's count: four
dialects (GUARD / BLOCK / BIND·PROOF / BLOCKING) for one wire. The
apps count prints twice ~100dp apart despite the file's own
comment celebrating its eviction to "one rendering per fact" —
the eviction moved it from three homes to two and declared
victory. Duration prints three ways (hero span, wheel numeral,
strip ENDS). FLOW prints three times with two doors. Five prints
speak at identical 15sp-bold volume (Dens); the strip speaks at
full volume in health and fault alike, training the eye to ignore
the one moment it matters (Vega D-5).

The fixed tax: 316.5dp of permanent chrome (header 48 + pinned
216.5 + rail 52) — 43% of a 740dp phone spent before the hero
exists; 61.5dp of it is reserved emptiness printing nothing. The
idle home is ~1.23 scroll-screens of content; the instrument card
does not fit its own window whole (Fremd C9).

**The clutter sentence, ruled:** the clutter is not feature count.
It is registers (Fremd) — produced by no state ever retiring
(Vega) and a pinned stack that never sleeps (Plan/Dens). The Deck
is five machines bolted to one chassis (setup desk, launch pad,
running dashboard, task line, status board), and the status board
shouts in all states at full volume.

## §5 · TRUTH — CHARGES THAT STAND

1. **Preset theatre.** The FLOW cap writes STOPWATCH_FLOW; the
   engine arms on dialMinutes alone and never reads the preset
   (the file confesses it, ESR-08). studyPreset is a corpse
   parameter pretending to be a control's wire. (Dens T1/T2,
   Plan T1, Vega T-5, Fremd C5 — four seats, one charge.)
2. **The floored, inverted, invented fraction** (§3, progress).
   Three lies in two glyphs (Vega).
3. **The valve label lies about its own gesture** (§3, chair).
4. **The misrouted FIX** (Fremd C3): a refusal branch prints
   "BLOCKING IS OFF — TAP TO FIX" wired to the Accessibility
   pane for a grant he already has. The sentence names the wrong
   lack and opens the wrong door. S.
5. **Idle ENDS slides** (Fremd C4): the comments swear the finish
   line "never slides"; at idle it is fed a now-frame that
   advances every minute, so ENDS 14:30 becomes 14:31. Running
   is honest; the idle word is not. Label it planned or freeze
   the derivation; the comment is struck until true. S.
6. **The hero's unexplained arithmetic.** Span = blocks × block
   + (blocks−1) × 10 breaks — true, and nowhere on the face.
   Wheel says 50, hero says 2H 30M; a true number that reads as
   a bug is a presentation lie (Vega T-4). Cured by §3's recipe.
7. **Frozen Guard, unlabelled** (Muede): the header's Guard line
   can hold its seal-frame value for a session's whole life,
   labelled nowhere as frozen.
8. **Comment rot** (Fremd C6, Vega D-6): the file header promises
   "12dp universal spacing" over a 24dp-radius inset card at
   16/18 padding; sections run 1, 2, 3, 5. Comments are this
   studio's memory; a false map is how the clutter survived
   review.

ACQUITTALS — binding, recorded once for all seats: the imprint
refuses WILL SHUT over a non-LIVE wire; the Guard line prints
nothing rather than a fake 0/4; the seam table is total and never
prints BIND HELD unbound; TIME/ENDS running is a pure function of
a stored end; the hero prices the breaks in; the seal freeze is
truthful; Δy=0 across the seal holds in code; sub-steps already
sleep behind a tap; the slide refuses in the hand when the
warrant is OFF — truth you can feel (Fremd). The Deck's words are,
line by line, unusually honest. Its dishonesty is architectural:
dead wires, duplicate renderings, labels lying about gestures.

## §6 · THE TESTS — RULED

**1 AM (Muede's walk stands as the record):** arming block 1 at
05:00 costs 1 gesture that matters buried under 5 decisions, 4
separate reads, 1 hidden control (block count inside the wheel
hub), and 1 dead second (the seal shot's input detention). The
break's one asked question — how long is my break? — is the
smallest print on the card (10sp eyebrow) while 72sp answers the
session's remaining. The routine-born SLEEP session is
indistinguishable from a hand-armed one. The grace window gives
a panicking man ~8 usable seconds to find an unannounced chip
whose pixels then become a different verb. Verdict: **operable
at 1 AM, unverifiable at 1 AM — FAIL as composed.**

**5 seconds (Fremd):** a stranger learns "timer + slide" — the
instrument passes. Then four dialects of machine self-report.
The page needs three sentences; a commercial home survives on
one. **Instrument PASS, page FAIL.**

**Finish (Schein):** brightness hierarchy inverts importance at
sealed night — ink fills #4A423C outshine the hero card #362C27;
three dark masses shout at rest where Blockit runs one; the hero
numeral at 72sp is a caption beside Blockit's 148dp architecture;
raw hex lives in three files against Blockit's 19 named slots,
so a look that recolours the schemes recolours ~60% of this
screen. The vertigo shot has gallery bones and a gimmick aim:
it blurs the instrument being sealed. The static triple-printed
MM:SS at 72sp for hours is an OLED burn-in risk the AOD clock
already knows to dodge and the Deck does not.

## §7 · THE BAR — RULED PLACEMENT

The instrument (hero + wheel + slide) is at Sarthak's bar —
original, truthful, one-job. The machinery is more honest than
the commercial standard. **The page is not commercial:** no
commercial home ships a 61-parameter contract with 21 dead
wires, a status board pinned through every state, its alarm
quieter than its decoration, and zero accessibility semantics
on its three primary controls (Fremd C10 — wheel, slide, rail
are Canvas views with no name, no value, no action; the home
screen is unusable eyes-free; priced L and flagged
bar-disqualifying until done). Dens scores density 4/10. Plan:
an A-grade instrument in a C-grade room. Both stand.

And the meta-finding, charged to the chair as much as any lane
(Vega U-3): the one-fact-one-rendering law was ruled, celebrated
in a comment at its third home, and never enforced at the
second. A law enforced by comment is a wish. The next merge
that adds a rendering of an existing fact should fail review
on that ground alone.

## §8 · THE ALTERNATIVE — REN'S ADDENDUM, DISCHARGED AND RULED

Two complete alternatives were filed. They are the same
alternative at two depths, and the verdict adopts both:

**PLAN §7 — "ONE QUESTION PER STATE" (the presentation of
record).** The Deck asks exactly one question per state; every
fact lives in the room that owns its answer.
- IDLE — "How long, and into what?" Hero, wheel, ghost task
  title, slide; beneath the slide ONE line — the imprint,
  carrying the seal's promise when LIVE, the refusal sentence
  when not. The proof drawer is shut; a healthy wire is silence.
- PROVING/JAMMED — "Is it holding?" The proof drawer opens
  where the ghost line stood: strip's three cells + seam, jam
  sentence inside it, hero frozen. Proof gets the whole stage
  for exactly the seconds it is the question.
- RUNNING/BREAK — "How long left, and what's in the way?"
  Hero, honest progress, ghost title, utilities; the valve at
  the slide's address; the imprint's HELD line as the foot.
- FINISH-DETAINED / RECEIPT — as now (both acquitted).
Evictions: warrant detail → PARK; task verbs → TASKS; totals
and history → DRAWER (the dead 21 prove the page never wanted
them); profiles/loadout → SHIELD, already their home.

**VEGA A — "THE FUSION" (the instrument mechanism of record).**
The hero moves INTO the wheel: the hub prints the true span
("1H 50M", "+10 BREAK" as sub-line when blocks > 1 — the
arithmetic explained at the hand that sets it). At the seal the
wheel does not vanish — it locks at its seat and the hub becomes
the running readout, same glyph, same address; progress is ink
filling the tick ring, FLOW's ring never completes (no
denominator, no floor, no inversion). Dies: hero panel, eyebrow
as standing label, progress track, %, the whole
progressFraction derivation.

**Ruled plainly, as Ren's addendum demands: the alternative
beats the keep-list's home.** The keep-list is five machines;
this is one machine in three states. The fix list (§9) is
written as the alternative, not as patches on the current room.

**Phase 2, named not buried:** VEGA B — "the slide carries the
proof": the strip's facts migrate into the slide track as three
gated segments stamped by travel; an unproven leg is felt as
rising spring resistance and a dropping knock pitch before it
is read. It converts a status board he must read into a
resistance he cannot miss. Priced L. It is NOT in the first fix
pass — it re-opens the slide's machinery and needs its own lane
and Ren's build word. MUEDE's "05:00 door" (proof flashed inside
the slide's trail when nothing changed since last seal, M) is
filed on record for the same reason — an arm-path machinery
change, Ren's separate call. SCHEIN's Lamp Test (three-frame
pure-luminance staircase renders — idle / sealed / fault — making
brightness hierarchy mechanically checkable) is endorsed to Ren
as a standing instrument; tonight's Deck fails it at the sealed
frame.

## §9 · THE FIX LIST — MERGE-READY, ORDERED, PRICED

S = a sitting's edit · M = a lane · L = a restructure.

F1  Cut the 21 dead parameters + host wiring. **S**
F2  Kill the FLOW cap as a control; the mode is printed
    (recipe line), never a second door. **S**
F3  Kill the % text; progress made true — timed fills
    elapsed/target, no floors; FLOW carries no bar. **S**
F4  Eyebrow: idle prints the recipe (`3 × 50 + BREAKS`);
    standing TOTAL / SESSION RUNNING die; routine-born
    sessions print their name. **S**
F5  Break hero: during a Safe Break the hero IS the break
    (countdown), eyebrow `BREAK — BACK AT HH:MM`. **S**
F6  The chair: one verb STOP, whole session; free inside
    10s, priced in its own words after; countdown in the
    hero; chips fill the 72dp zone. **M**
F7  Task row → ghost title (idle read-only; running title +
    DONE); sub-steps + QUEUE move to Tasks. **M**
F8  Seam stamps → PARK. **S**
F9  Sentence words rewritten plain; the misrouted FIX branch
    corrected to the pane that owns the lack; in fault the
    sentence stands above the slide. **S**
F10 Hero single-press (one tokenised shadow ghost; ghosts off
    on FLOW and the ticking readout). **S**
F11 The card gets its shadow (2dp day / tone-only night) or
    stops claiming lift. **S**
F12 Unlight the utilities: night ink fill steps down
    (#2A2320); brightness order glyph > card > controls >
    ground restored. **S**
F13 Key-cap radii registered: 8dp → 12dp (FLOW's successor
    print, DONE). Two radii app-wide. **S**
F14 Group by silence (spacing re-priced: tighter inside the
    object, 20dp between objects) + fade the scroll's bottom
    edge — content retires, never guillotined. **S**
F15 Vertigo moves to the ground: the page recedes (0.98,
    blur peak 3dp, same curves, same 1050ms); the instrument
    seals in focus. **M**
F16 The pinned stack collapses: strip + sentence become the
    state-gated proof drawer (§8); pinned zone = slide +
    imprint (+ sentence in fault). ~120dp of permanent
    screen returned. **L**
F17 Tokenise the light: every raw hex in Deck / components /
    views swept into scheme tokens (the looks cannot inherit
    porcelain ghosts). **M**
F18 The Fusion (Vega A): hero into the hub; the dial seals in
    place as its own clock; rim-ink progress. **M**
F19 Receipt dismissal: slip-swipe or PARK/rail only; a stray
    scrim tap shakes the slip, never kills it. **S**
F20 Print PARK on the rail's unnamed zone. **S**
F21 Comment truth pass: sections renumbered or unnumbered;
    the header's spacing promise corrected; idle ENDS
    labelled planned (§5.5). **S**

**Totals: 15 S · 5 M · 1 L.** Plus bar debt F22 below.
Phase 2 (separate lanes, Ren's word): Vega B (L) · Muede's
05:00 door (M) · Schein's Lamp Test spec (S).
F22 Accessibility semantics on wheel, slide, rail. **L** —
    not presentation; a standing bar debt, named so no merge
    calls this screen finished while it stands.

Sequencing note for the merger: F1–F5, F8–F14, F19–F21 are
independent of the restructure and can land on the current
room; F16 + F18 are the alternative's skeleton and should land
together; F17 precedes any look touching this screen.

## §10 · THE CREW, ANSWERED BY NAME

**DENS** — census adopted as the audit's numbers (60+ params,
21 dead, 316.5dp tax, six renderings). Two-Height Strip: not
the ruling — the band dies rather than collapses — but your
law (fault gets more room than health) governs the drawer, and
your mechanism is the priced floor if the drawer is refused.
Imprint-tenant kill overruled (four seats acquit it; the
duplication dies strip-side). Ghost kill-both held as fallback
behind Schein's single-press.
**PLAN** — job sentence adopted as §1's law; §7 adopted as the
presentation of record; state map and pinned-stack ruling
adopted nearly whole (F16). One amendment: your strip drawer
keeps TIME/ENDS on the Deck in all states per Fremd/Muede —
his fact does not visit the drawer.
**VEGA** — premise adopted (§4's sentence is yours and
Fremd's together). Proposal A adopted (F18). Proposal B
ruled phase 2 on price and machinery, not on merit — your own
fallback logic conceded the sequencing. Ghosts: your acquittal
is honoured in form (technique, not duplication) and
overruled in count (Schein's single-press governs a finish
question). U-3 lands in §7 against the chair, as filed.
**MUEDE** — the grace-chair kill adopted whole (the audit's
strongest single verdict); break hero, routine naming, plain
sentence words, receipt dismissal all adopted. FLOW-cap keep
overruled on the wiring (a key to a dead preset is theatre);
blocks key answered by the printed recipe — the count is on
the face, the hub gesture stays. The 05:00 door is filed for
Ren's separate call, answered not buried.
**SCHEIN** — single-press, shadow, unlighting, radii,
grouped silence, fade, vertigo-to-ground, token sweep:
adopted as F10–F15 + F17 — your §8 is the finish half of this
fix list. Strip-as-seam (§8.4) is superseded by the strip's
eviction; the fade (your §8.5) takes the seam's job. Lamp
Test endorsed to Ren by name.
**FREMD** — "cut dialects, not features" is §4's ruling in
your words. Strip-to-PARK adopted in part: BLOCK and the
stamps go; TIME stays (Muede/Plan carry it). Misroute (C3),
idle-ENDS (C4), comment rot (C6), PARK's missing name, and
the accessibility debt (C10) all stand as filed. Wheel shrink
held as the next lever, answered by eviction first. Your cold
acquittals are in §5 — they are the strongest evidence in this
document, and they are why the verdict cuts arrangement, not
machinery.

## §11 · WHAT THIS AUDIT DID NOT DO

It did not re-audit the machinery (warrant, cascade, seal —
Truth Cars' ground, and acquitted here where the Deck prints
it). It did not touch the other four screens; the same diseases
are alleged there by three of my crew (reserved emptiness —
Dens; state-squatting — Plan; dialect pile-up — Fremd), alleged
only. Nothing here is built. Every fix waits for Ren's merge
and Sarthak's build word, under the new law.

— ROST, coordinator, full-app brutal audit (the Deck).
Crew filings: mail/rost/0122 (Schein), 0123 (Dens), 0123
(Muede), 0124 (Plan), 0125 (Fremd), 0131 (Vega) — all read,
all answered above.
