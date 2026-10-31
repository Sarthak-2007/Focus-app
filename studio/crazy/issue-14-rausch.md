# RAUSCH — CRAZY IDEAS FOR FIRST OPEN
## Issue #14 · Setup Sheet + onboarding · `Sarthak-2007/Focus-app` · branch `agent/issue-14`

Seat: **Rausch (crazy-ideas, issue #14)** — one job only: crazy, original
ideas for the first-open experience. No implementation, no audit, no merge.
Ground read before dreaming: issue #14 + its comments, Pforte's cell
(`DESIGN-composition`, `INNOVATION-mechanisms`, `FEEL-first-ten-seconds`,
`FINISH-tear-exit`), the cured sheet as it stands — ReceptionSheet,
four counted doors (ACCESSIBILITY / Interceptor, USAGE_ACCESS / Witness,
DEVICE_ADMIN / Anchor, NOTIFICATION_LISTENER / Mail Room), prints
SET / NOT SET / UNPROVED / NOT CHECKED, branched GUARD total (S-1 cured),
Return Chit, Setup Ledger, Armed Handover, DONE tear 420 ms
(260 tear + 160 throw), stub waiting in PARK.

**The law that binds even the wildest line here:** a door that is not
proved cannot be dressed as proved. Every idea below either respects the
four print kinds exactly, or is explicitly marked as a *simulation* and
labelled as one on screen. Fonts: Sora display / Atkinson Hyperlegible
body / Anonymous Pro telemetry. Never green. No AI-generated images —
everything here is type, rules, motion, sound, and paper mechanics the
app already owns.

**My thesis, in one sentence:** first open is not onboarding, it is a
**house handover at 04:00** — a stranger hands him the keys to a room
that will lock him in, and the whole ceremony exists to answer, in
order: *Is this place real? Is it honest? Do I choose this? Can I leave?*
Every idea serves RECEPTION, DOORS AS CEREMONY, THE SHEET THAT CANNOT
LIE, or DONE AS A TEAR.

Cost key: **FREE** = words/print/order only · **SMALL** = one composable
or one pure function + tests · **MEDIUM** = new state in SetupStore or a
new gesture/animation system · **LARGE** = new surface or new machinery.
Nothing here needs a DB version move unless it says so.

---

## A · RECEPTION — the first ten seconds

### A1 · THE THRESHOLD (black before the nameplate)
First open does not start with a sheet. It starts with ~900 ms of quiet
dark, then `MONOLITH ML-01` **stamps** itself in — one firm sound, one
stamp-press dip — and only then does the sheet rise. He meets a place
before he meets a form. The stamp is the same stamp machinery the app
uses everywhere else, so reception and the rest of the house are one
material from second one.
- **Mechanism:** a `thresholdShown` phase in ReceptionSheet gating the
  rise on a 900 ms Animatable + `playFirmClick`-family stamp sound;
  skippable by any tap (tap = "let me in"), never repeated after stamp.
- **Cost:** SMALL — one phase state + existing sound, no new assets.

### A2 · PROVE FIRST, ASK SECOND (the Handshake)
Before a single door is *asked*, the sheet proves itself: a live,
watchable self-read — "WATCH — I AM READING MY OWN DOORS NOW" — the
sweep visibly lands, timestamps in Anonymous Pro, each door flipping
NOT CHECKED → its true print in front of him. The app demonstrates it
can tell the truth about itself *before* it asks him to trust it with
Android's scariest lists. Ask second. Prove first.
- **Mechanism:** on first open only, delay the FIX keys until the first
  live `WatchSnapshot` lands (UNMEASURED cards already reserve the key
  band — F-7), and print the measurement instant per door in telemetry.
- **Cost:** FREE–SMALL — ordering + one telemetry line per card; the
  sweep, prints, and reserved band already exist.

### A3 · THE NIGHT CLERK (reception knows what hour it is)
The sheet greets the hour honestly: at 04:00 it is terse, dim, and slow
("SETUP + INTRODUCTION — TAKE YOUR TIME, NOTHING HERE STARTS ANYTHING");
at 19:00 it is brisk. Not a fake personality — one header line and the
motion/sound tempo keyed to wall-clock hour, printed from the real
clock, never claiming anything about *him*.
- **Mechanism:** pure `receptionHourLine(wallHour)` + tempo factor fed
  to the existing rise/tear durations within the house vocabulary.
- **Cost:** SMALL — one pure function + tests; tempo must stay inside
  {180, 260, 280, 420, 520} or it is cut, not invented.

### A4 · THE GHOST SESSION (walk the room before you buy it)
One beat in Movement 3 is *playable*: a 20-second, clearly-labelled
**SIMULATION** — he turns a miniature dial, the sheet dims, a fake shut
app bounces off, the desk clock takes the face, it ends, a miniature
receipt prints. Labelled `A SHOWING — NOT A SESSION. NOTHING WAS SHUT.`
in badge type, above it, the whole time. The sheet's worst debt
(Blank's census: the wheel, SEAL, the Deck are never cashed) is paid
by *doing it once with his own thumb*, not by more sentences.
- **Mechanism:** a self-contained composable simulation driven by one
  Animatable timeline; zero calls to WatchStore, the warrant, or any
  real blocking path — simulation state lives and dies in the sheet.
- **Cost:** LARGE — the priciest idea in this file; one isolated
  composable + a hard review rule: it may never read a real print.

### A5 · LEAVE IS THE FIFTH DOOR (refusal gets a ceremony too)
Print LEAVE at the **head**, dressed exactly like a door — a card, a
job line ("LEAVES. NOTHING IS STAMPED, NOTHING IS LOST, THIS SHEET
WAITS IN PARK"), a key. A man who can leave at any second is a man who
can choose to stay. This kills the first-ten-seconds trap Blank found
(exit invisible until the foot) without a sticky key: the exit is not
a UI escape hatch, it is a *resident of the house*.
- **Mechanism:** one static card above Movement 2, reusing the door
  card's geometry/dress, wired to the existing leave path (BACK's
  behaviour, stamped nothing).
- **Cost:** SMALL — one card, no new state; DONE's act-line then never
  has to apologise for existing.

---

## B · PERMISSION DOORS AS A CEREMONY

### B1 · DOORS OPEN INWARD (the sheet shortens behind him)
The granted-door collapse (F-6, one ruled line, ~37 dp) goes one step
crazier: a door that lands SET doesn't just shrink — it **recedes**:
its line prints like a doorway seen from inside, hairline below, and
the *whole sheet audibly shortens* (a soft paper settle). By 4/4 the
sheet is a stub of four ruled lines and the total — he can *feel*
completion as the paper getting lighter in his hand. Progress is
weight, not a counter.
- **Mechanism:** extend F-6's collapse with the existing 160 ms
  visibility pair + one settle sound on the MISSING→GRANTED transition
  only (the chit already detects exactly this transition).
- **Cost:** SMALL — animation/sound on a transition that already exists;
  no new print, no new claim.

### B2 · THE PROCESSION (one door on stage at a time)
Radical option: on *first open only*, doors are not a list of four —
they are a procession. Only the current door is full-size; doors not
yet reached wait as one-line silhouettes below ("3 DOORS WAIT"), doors
done recede per B1. He is never confronted with four errands at once;
the sheet asks one question at a time, like a person, not a form.
PARK reprints keep the full list (he knows the house by then).
- **Mechanism:** first-open variant of the door loop: index of first
  non-GRANTED door gets the expanded card; others render the collapsed
  / silhouette line; recomputed from the same prints, nothing stored.
- **Cost:** MEDIUM — a second rendering mode for the loop + tests that
  both modes print identical words per door (see C1: words never fork).

### B3 · KEY-CUTTING (FIX cuts a key, it doesn't open Settings cold)
When he taps FIX, before Android takes him, the sheet *cuts the key*:
the door's card edge notches — a small geometric notch animates in,
Anonymous Pro prints the door's name on the key — and the Return Chit
becomes literally that key coming back. On return, if REPAIRED, the
key **fits**: the notch fills with ink for one printing (the chit's
existing bold-SET printing, made physical). A departure object he can
picture while he's lost in Android's lists.
- **Mechanism:** notch = one `graphicsLayer`/clip shape driven by
  `chit.state == DEPARTED` for that door; fill = the existing unspent-
  REPAIRED chit printing. No new persisted state — the chit *is* the key.
- **Cost:** MEDIUM — one shape + two animation states on the card;
  sound optional and must stay the existing firm click.

### B4 · THE ANCHOR GETS TWO HANDS (the scary door gets the slow rite)
Device Admin is the door strangers fear ("Android refuses to uninstall
Monolith"). Give *only that door* a two-hand ceremony: its FIX key is
a **hold, not a tap** — press-and-hold ~800 ms while the way-back line
("TO TAKE IT BACK: SWITCH MONOLITH OFF IN THAT SAME LIST") prints
itself letter-complete under his thumb. He cannot grant the scariest
door by accident, and the exit instructions are the thing his thumb
physically waits for. Every other door stays a tap.
- **Mechanism:** `detectTapGestures` → hold-progress Animatable on the
  Anchor card only; releasing early cancels cleanly, writes no chit,
  fires no intent. Hold completion = today's FIX path, unchanged.
- **Cost:** SMALL–MEDIUM — one gesture variant on one card + tests;
  accessibility alternative (a plain tap path in the same card) is
  mandatory, not optional, or this idea is cut.

### B5 · THE ESCORT WALKS HIM TO THE DOOR (and waits outside)
Today the escort is text. Make it a **thread**: when FIX fires, a thin
rule draws from the card to the sheet's edge (the path out), and on
return the thread is either reeled in (REPAIRED — thread gone, key
fits, B3) or left hanging with the chit line tied to its end
(UNCHANGED / UNREAD / DARK, each in its own honest words). The
excursion — currently printed nowhere while he's away — becomes the
one visual the sheet keeps holding for him.
- **Mechanism:** thread = one animated line whose existence is exactly
  `chit.state == DEPARTED` for that door; reeling = the chit resolution
  effect that already runs host-side. No new state.
- **Cost:** SMALL — one line composable keyed to existing chit state.

### B6 · DOORS ANSWER IN THEIR OWN VOICE (sound, never speech)
Each door gets one distinct *mechanical* sound from the existing
engine's family — Interceptor: a latch; Witness: a soft click;
Anchor: a bolt (heavier, slower); Mail Room: a paper shuffle. SET
never sounds like success-music; it sounds like the object it is.
Klang's law stands: a voice that performs unproved success is a lie —
so sounds fire on the *measured print change*, never on the tap.
- **Mechanism:** four `TactileSoundEngine` variants selected by door id
  at the same site that today plays the firm click on REPAIRED.
- **Cost:** SMALL — engine variants only; if a variant can't be made
  honestly distinct, the door keeps the shared click (cut, don't fake).

---

## C · THE SETUP SHEET THAT NEVER CONTRADICTS ITSELF

*The S-1 wound, attacked as architecture, not wording. Wording cures
die the next time someone edits a sentence. These make contradiction
structurally impossible — or loudly visible the moment it tries.*

### C1 · ONE INK (the total is *clipped from* the cards, literally)
The total may not be authored. On screen, the total sentence is
visually *clipped out of the cards above it*: same column, and each
per-door sentence in the total prints directly under a hairline that
aligns with its card's hairline — a column of echoes. If a sentence
ever disagreed with its card, the eye would catch it in one glance
because they sit in one ruled column, card-word and total-word in the
same Anonymous Pro voice. Form enforces what the branched-total law
already enforces in code.
- **Mechanism:** layout only — shared column/hairline composable for
  card footers and total lines; `receptionTotal`'s per-kind branching
  is untouched and remains the single source.
- **Cost:** SMALL — composition refactor of existing prints, zero new
  words, zero new logic.

### C2 · THE SHEET READS ITSELF BACK (the Echo Check at DONE)
The craziest honest machine in this file: pressing DONE first makes
the sheet **re-read itself** — for ~600 ms the total rebuilds itself
line by line *from the cards in view order*, each line flashing as it
is re-derived, then the tear fires. If any line cannot be re-derived
from a card (a bug, a stale snapshot), the tear **refuses** and the
sheet prints `THIS SHEET DISAGREES WITH ITSELF — NOTHING WAS STAMPED.`
and stays. DONE becomes a proof ceremony: the stamp only lands on a
sheet that just demonstrated its own consistency, live, in front of him.
- **Mechanism:** DONE handler runs the existing pure `receptionTotal`
  against the same snapshot the cards rendered, compares per-door kind
  with the rendered prints (both already in composition); mismatch =
  no `onStamp()`, print the disagreement line, log the pair. Match =
  today's stamp → tear path, unchanged.
- **Cost:** MEDIUM — one comparison pass + the refusal print + tests
  (the 256-combination guard from Wave-2 becomes its test spine).

### C3 · NO WORD TRAVELS ALONE (every claim carries its evidence)
Every verdict word on the sheet gets a tiny telemetry shadow:
`SET · PROVED 04:00:12` / `NOT SET · READ 04:00:12` / `UNPROVED · LAST
PROOF 03:58:40`. A word that must show its timestamp can never quietly
outlive its truth — staleness becomes *visible age*, not a second
verdict hiding in another word (the M-24 wound: card UNPROVED vs total
IS SET). One fact, one timestamp, everywhere it prints.
- **Mechanism:** print `measurement.measuredWall` (already in the
  snapshot) beside each door word and in the total's per-door lines,
  Anonymous Pro 10sp; no new measurement, no new store.
- **Cost:** SMALL — print sites + formatting; height cost ~+12 dp per
  card must be re-summed against Zoll's K-2 budgets before it ships.

### C4 · THE CONTRADICTION LANTERN (for PARK, forever after)
The setup ledger already remembers doors-at-stamp. Add one ruthless
resident: if PARK's board and the sheet's stub ever disagree about a
door (different prints from different snapshots in the same sitting),
the stub row prints a lantern line — `PARK AND THIS SHEET READ THIS
DOOR DIFFERENTLY — CHECK RE-READS BOTH.` — instead of letting two
surfaces tell two stories in the same house. Contradiction, when it
exists, is *announced*, never smoothed over.
- **Mechanism:** host compares the stub's print source with PARK's
  board print for counted doors at PARK open; one conditional line.
  Both surfaces already read from WatchStore — this only compares.
- **Cost:** SMALL — one comparison + one print; zero new persistence.

### C5 · WORDS NEVER FORK (one string, every surface)
Procession mode (B2), collapsed lines (B1), PARK's stub, the Guard
line, the total: five surfaces, and the rule is brutal — a door's
job/escort/verdict strings exist **once** (`receptionDoorPrepare`,
`receptionDoorWord`, the per-kind total sentences) and every surface
renders *those*, or renders nothing. Any surface that needs a shorter
form uses the ruled short-form function (F-5), never a hand-typed
paraphrase. Paraphrase is where contradictions are born.
- **Mechanism:** discipline + one lint-style unit test asserting the
  five surfaces' strings are drawn from the shared functions (string
  identity, not similarity).
- **Cost:** FREE–SMALL — mostly refusal: the cost is every future
  surface being denied its own wording. That's the point.

---

## D · DONE AS A TEAR

*The tear exists (Letzt F-1: stamp at t=0, dashes halve over 260 ms,
block drops/fades 180 ms, throw 160 ms, total 420 ms). These push it
from choreography to meaning.*

### D1 · HE PULLS THE TEAR (DONE is a drag, not a tap)
The stamping DONE is not pressed — it is **pulled**: thumb on DONE,
drag down; the perforation stretches, dashes thinning as his pull
nears the threshold, and past it — tear, stamp already booked at the
*moment the pull commits* (same G-01 order: write, commit, publish,
then sound/motion). Release early and the leaf settles back, unstamped,
unashamed: `NOT YET.` A tear he performs with his own hand cannot be
an accident, and cannot feel like a form submitting itself.
- **Mechanism:** DONE's `pointerInput` gains a drag-progress state
  driving the existing `tearDash`/`tearDrop` animatables' *targets*;
  commit threshold (~40 dp) triggers today's stamp→tear sequence
  verbatim. Tap-DONE remains for PARK reprints (non-stamping, as ruled).
- **Cost:** MEDIUM — gesture state on one key + tests for commit /
  release-early / BACK-during-pull; the 420 ms timeline is untouched.

### D2 · THE BLANK LEAF (0/4 tears differently, and that's the message)
Tear with doors unset and the torn leaf is **blank where the count
would print** — no `GUARD 0/4` on the departing leaf, just the tear
sound, slower, and the stub above the rule carrying the true total
(as it already does). He keeps the stub with the facts; the leaf he
throws away carries nothing he needs to be scolded by. Emptiness,
printed honestly, says what four warning sentences keep repeating.
- **Mechanism:** the torn block's content branches on the stamped
  mask (ledger, already written at DONE): mask 0 → leaf renders the
  act-line only; mask >0 → as today. Stub/total above the rule never
  branch — the facts stay whole where they stay.
- **Cost:** SMALL — one conditional in the torn block + one test.

### D3 · THE STUB BECOMES HIS KEY TO PARK (the stub grows teeth)
The stub that waits in PARK is not a note — it is the **torn edge
itself**: PARK's stub row renders with the same perforation geometry
along its top, dash-for-dash the rule he tore. When a door is later
lost, one dash in the stub's edge *breaks* (a gap) per lost door —
the stub visibly wears what the house has suffered, and re-proving
the door restores the dash. The setup's scar tissue, kept where he
can see it, saying only what the ledger knows.
- **Mechanism:** PARK stub row draws `PerforationRow` geometry with
  gaps driven by the existing ledger consumer ("WAS SET AT SETUP —
  SINCE LOST" condition, Funke §2.2) — visual twin of a line that
  already prints; words unchanged.
- **Cost:** SMALL — one canvas row in ParkDrawer keyed to an existing
  condition; if the twin ever disagrees with the words, the words win
  and the teeth are cut (C4's lantern covers the disagreement).

### D4 · THE TEAR REMEMBERS ONE TIME ONLY (no re-tear, ever)
A reprint's DONE must never tear, simulate a tear, or "replay" the
ceremony (today: firm click, 160 ms throw — correct). Go further:
the PARK reprint sheet prints, where the tear rule was, a **spent
perforation** — dashes already halved, permanently, with one line:
`THIS SHEET WAS TORN ONCE — THE STUB IS THE ORIGINAL.` Firstness is
a fact (ledger `setup_stamped_wall`); the reprint's paper should look
like what it is: a copy of something that already happened.
- **Mechanism:** tear rule's `dashScale` initialises to 0.5f when
  `isFirstOpen == false`, 1f when true; one conditional badge line
  from `stampedWall()`. No state, no animation change.
- **Cost:** FREE–SMALL — an initial value + one line; the tear driver
  is untouched because a reprint never enters it.

### D5 · AFTER THE TEAR, THE DECK SAYS ONE LINE (the Armed Handover, shouted)
The handover exists (Funke §3: `GUARD 4/4 — ONE ACT LEFT: SLIDE.`,
one-shot, four spenders). Crazy extension: at 4/4, the tear is
**louder and the Deck receives him mid-sentence** — the handover line
is already on the Deck's header *as the sheet throws*, so the throw
lands inside a sentence that began on the sheet: sheet's last print
above the tear is `ONE ACT LEFT —` and the Deck's header completes it
`— SLIDE.` Two surfaces, one sentence, no gap. At <4/4 it degrades
honestly to the standing handover, never faking the completion.
- **Mechanism:** sheet prints the prefix only when its own total
  count == 4 at stamp instant (the same derivation the handover
  setter already uses); header prints the existing handover. One
  derivation, two prints, C5-compliant.
- **Cost:** SMALL — one conditional print on the sheet; spender law
  and handover state are untouched.

---

## E · RAUSCH'S FIVE — if only five ship, ship these

1. **A2 Prove First, Ask Second** — trust is the whole first-open
   problem, and this is trust performed, not promised. FREE–SMALL.
2. **C2 The Echo Check** — the sheet that re-reads itself before it
   may stamp turns S-1 from a cured bug into a permanent ceremony.
   MEDIUM, and worth every line.
3. **D1 Pull The Tear** — DONE becomes an act his hand remembers.
   MEDIUM.
4. **A5 Leave Is The Fifth Door** — a chooser who can leave is the
   only chooser whose staying means anything. SMALL.
5. **B4 The Anchor's Two Hands** — the scariest door gets the slowest
   rite, and its way-back is the thing his thumb waits for. SMALL–MEDIUM.

## F · KILLED IN MY OWN HEAD (recorded so nobody revives them)

- **Confetti / success animation at 4/4** — performs a celebration the
  app hasn't earned; sessions haven't run yet. Killed by Klang's law.
- **A percentage ("SETUP 75% COMPLETE")** — a door is not 25% of a
  truth; percentages would re-introduce exactly the flattening S-1's
  per-kind branching killed. Killed by honesty.
- **Auto-opening the next door's Settings after a return** — chains
  him through errands he didn't choose; the chit's `· NEXT —` suffix
  already *names* the next door without dragging him. Killed by A5's
  principle: he chooses, every time.
- **Naming the doors with his name / personalisation on first open** —
  the sheet knows nothing about him yet; pretending otherwise is the
  M-19 presumption ("MORNING 09:00 — who set this?") wearing a smile.
  Killed by honesty.
- **Hiding unset doors behind a "do it later" fold** — a fold is a
  place where a lack goes to be forgotten; the sheet's whole worth is
  that lacks stay printed. Killed by C5.

---

*Wild, and every wild thing above can be built from machinery this
tree already owns — prints, the chit, the ledger, the tear pair, the
visibility pair, the sound engine. Where an idea needed a thing that
doesn't exist, it says so in its cost. Nothing here dresses an
unproved door as proved, and the two ideas that touch proof itself
(A2, C2) exist only to make proof harder to fake.*

— Rausch (crazy-ideas, issue #14)
