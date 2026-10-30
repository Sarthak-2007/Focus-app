# ISSUE #6 — FUNKE (ENTHUSIASTIC DESIGNER) — THE DELIGHT PASS, DECK
## Seat: Funke, enthusiastic designer, dedicated-100 programme · Primary issue #6
## Ground read whole before writing: the issue + all 8 comments, AUDIT-deck.md,
## BEYOND-deck.md, ISSUE-6-designer.md (Rahmen's lane, respected), ISSUE-6-feel-1.md,
## ISSUE-6-WAVE2-REPORT.md. Paper only — DECLARED, nobody compiles.
## Opened live on the issue: comment 6512077382. This file is the full pass.

## §0 · THE THESIS AND THE WALLS I WORK INSIDE

**Delight lives in the verbs, not the furniture.** The Deck's idle face
stays perfectly still. Every spark below is attached to an *event* —
the seal, a block boundary, a break, a finish, a stop caught in time —
because an event is a moment he caused, and furniture is a moment the
app performs at him. A delight that runs at idle is a notification
wearing better clothes.

Walls honoured, named so the cell can check me against them:
- Spectacle budget (BEYOND S2): the ledger stands at four freezes.
  **I spend zero freezes and propose no fifth.** Zero idle motion.
- One-fact-one-rendering: every item below is checked against the
  rendering that already owns its fact; two of my own ideas died on
  this law and are buried in §8 with their reasons.
- Blocker, not a tracker: no scores, no streaks, no tallies, no
  history-as-judgement. A reward below is a *feeling at an event*,
  never a number that accumulates.
- Sound: the engine's four existing voices only (detent ladder,
  latch, firm click, seat knock). No new assets anywhere in this file.
- Print: Sora display / Atkinson Hyperlegible body / Anonymous Pro
  telemetry, off the existing ladder. Colours are existing scheme
  tokens named by rank (D13's hierarchy), never a new hex. Zero green
  or near-green. No imagery.
- Truth: nothing below prints a state the machine has not verified,
  and every wording item carries a truth kill gate.

## FD-1 · THE STOP CHAIR'S GRACE WORDS (inside F6, M) — POSTED, PRICE S

F6 rules one chair, one verb, whole session, price in its own words.
Its priced-era form to beat stands: `STOP — USES YOUR 1 BREAK THIS
WEEK`. The grace era (≤10s) currently has no words of its own — the
chair would print the price form or nothing during the only seconds
where stopping is free, which teaches exactly the wrong feeling:
that every stop costs.

**Spec — the chair's words are a pure function of the same era
boundary that divides Feel-1 §6's haptics, evaluated at DOWN:**

| Era | Chair words | Haptic (Feel-1 §6, stands) |
|---|---|---|
| Grace, elapsed ≤10s | `STOP — FREE, NO BREAK USED` | firm click, 8ms class — light, free |
| Priced, elapsed >10s | `STOP — USES YOUR 1 BREAK THIS WEEK` | weighted knock 14ms @ 0.85, ~230Hz |

Dress: the chair's existing word style, one line, full 72dp chair.
The grace countdown itself stays in the hub (Designer §2) — the
chair never counts.

**Why this is the Deck's one verbal reward:** it pays the man who
catches himself *fast*. Every other surface in this app prices
failure honestly; this line is where the machine says the cheap,
true, encouraging thing at the exact moment it is true — in the
first ten seconds, stopping costs nothing, and the chair should
sound like relief, not like a till.

**Kill gate (truth):** the words are a claim about the token ledger.
If the charged token count ever disagrees with the era the words
printed — any build, any edge (e.g. a token granted mid-session) —
the pair dies back to bare `STOP` until the derivation is one
function feeding both the charge and the print. Print follows the
ledger, never the reverse.

## FD-2 · THE BREAK SOUND PAIR (new, inside F5's break hero) — PRICE S

The break is the Deck's only reward *state*, and F5 + Designer §2
(BREAK) rightly make the hub the break countdown. But the two
transitions that bound it — block → break, break → block — have no
sound specced anywhere: Feel-1's map covers the wheel, hub, slide,
and chair; the session's own rhythm is silent. A man studying with
the phone face-down currently learns his break started only by
looking. The break should *arrive*.

**Spec — two call sites, existing voices, at the engine's own
block-boundary emission, the same frame the hub re-strikes:**

- **Block → break:** `playFirmClick()` once — the app's one
  "whole landing" voice, here meaning *a whole block landed*. The
  hub glyph re-strikes to the break countdown under Aldo's RE-SET
  grammar (class change: session-remaining → break-remaining) —
  already specced machinery, no new motion.
- **Break → block:** `playSeatKnock(10ms, 0.55, 300Hz)` — the
  brush-weight seat voice: *sit back down*. Deliberately lighter
  than the break's opening click; returning is a smaller event
  than earning the rest.
- **FLOW:** no blocks, no breaks, no pair. Silence is correct.
- **Session end from inside a block** is NOT this item: the finish
  owns its cadence (HELD FRAME, E700 pulse) and nothing is added.

**Kill gate (device):** if the break click ever reads as a
*notification* — he glances expecting a message — it dies. A break
sound must be felt as the machine's own voice, never the world's.
Komet's K3 bells, if they land (FD-6), subsume this pair: the
break bell becomes the break-start voice and FD-2's click is
struck to avoid two voices for one boundary. One voice per
boundary, whichever survives.

## FD-3 · DONE'S CLICK (running task line) — PRICE S

DONE is the most-pressed verb of a running session and the only
one whose press is pure gain: a task finished, by his hand, on
purpose. Today its press feel is unspecified in the Deck's specs —
the 40ms DONE-landing freeze already sits in the spectacle ledger
(one of the four), but the hand gets no voice.

**Spec:** DONE commits on DOWN with `playFirmClick()` — the same
voice as FD-2's block landing, deliberately: *a thing finished*
has one voice on this Deck, whether the session engine or the man
declares it. The ghost title then re-strikes to the next task
under Aldo's RE-SET (changed glyphs only, 120ms strike). The
existing 40ms landing freeze is the visual pair; I spend no new
freeze.

**The fence that makes it delight and not a score:** no tally may
ever attach. No `3 DONE`, no count, no sound that changes with
how many came before (no rising pitch, no combo anything — the
click is identical the first time and the ninth). If the queue
empties, the ghost line goes *absent* (Designer D7: absent when
empty) — no congrats print, no `ALL DONE` banner. The press is
the whole reward; the next title, or honest emptiness, is the
whole report.

**Kill gate:** if any later lane adds a tally print to DONE, this
click dies in the same commit — a score must not have a sound.

## FD-4 · THE 03:30 SQUINT TEST (acceptance instrument) — PRICE 0

Feel-Licht's escalation stands on the record (wave-2 §6): idle at
03:30 outside SLEEP evaluates to DAY and blazes porcelain — priced
M, Ren-level. This seat adds *why it is delight-critical* and the
test that proves the cure: the first face of the day is currently
the loudest face the app owns, at the exact hour its reader is
least awake. BEYOND D1 (night hero steps up to glyph tier
`#EDE3D8`, the numeral the brightest object in the room) is the
finish half of the same cure.

**The test, exact:** dark room, phone at arm's length (~40cm),
Deck in night dress, idle, unprimed. Four pass conditions, in
order:
1. The first object the eye finds is the hero/hub numeral.
2. The second is the slide cap — the face's only accent mass.
3. No third object reaches the eye within 2 seconds.
4. The recipe line is *not* readable at arm's length and *is*
   readable at a lean-in — quiet print should cost a lean, not
   announce itself.

Any failure is a luminance-order failure in the frame (Lamp
Test's staircase, applied by a tired eye instead of a meter),
never a failure of the test. Until the 03:30 evaluation cure
lands, the test runs in session/SLEEP night dress and the
porcelain frame is recorded as the known failing case — the
escalation's evidence, not a new charge.

## FD-5 · THE WHEEL'S PLAY ARC (endorsement + fence) — PRICE 0

The wheel is the one playful object in the app, and its play is
already built: the detent ladder rising 340→880Hz as the value
climbs, the %15 stops stepping up to latch class, the strait's
9° of haptic silence, FLOW's warm landing knock (14ms @ 0.8,
300Hz — the wheel's home sound), the hub's dip-and-click under
timed counts. Read together they describe the Deck's emotional
shape, which this seat names so no lane flattens it by accident:

**Play, then commit.** For the seconds before the seal, the
instrument is a toy — it sings under the hand, its count has
weight, its resting place sounds like home. At the seal the play
*ends*: the locked wheel consumes touches in total silence
(Feel-1 §1, kept), and that silence is the second half of the
delight — the machine stops being fun exactly when it starts
being serious, and the contrast is the ritual Sarthak kept
Blockit for.

**Fences:** (a) no reward sounds on the wheel, ever — no chime
at 180, no jingle on FLOW landing; the pleasure is the physics,
and a jingle turns an instrument into a slot machine. (b) Feel-1's
H1 (FLOW-hub dull release knock, S) is endorsed from this seat:
a surface that answers *dull* reads as a machine at rest; a
surface that answers *nothing* reads as a frozen screen, and a
frozen screen at 03:30 costs a re-check the whole design exists
to prevent.

## FD-6 · SHIP'S BELLS (Komet K3) — SUPPORT WITH ONE AMENDMENT

Komet's K3 is the strongest delight idea filed on this issue:
time as weather, no screen, no print, nothing to watch. This
seat seconds it and files the delight conditions:

- **Pattern (the shipped set):** block-end / break-start = two
  soft strikes, 180ms apart, Laid-Seal placement (body −14dB,
  attack 12ms). Session-end = the long pattern: three pairs,
  gaps widening 180 / 260 / 340ms — a shape the body learns as
  *done* within a week. **No half-block strike in the shipped
  pattern** (amendment to Komet's sketch): a half-block bell is
  a progress report, and a progress report in sound is a clock
  you cannot look away from. Boundaries only — bells mark
  *changes of state*, never the passage itself.
- Silent in SLEEP (Komet's clause, stands). Volume capped under
  media (stands). One switch, hub long-press, no settings page
  (stands — a delight with a preferences screen is a feature,
  and this is a seasoning).
- If FD-2 and K3 both survive to the merge, K3's break pattern
  replaces FD-2's break click (one voice per boundary, FD-2's
  own gate).

**Kill gate (added):** if he is ever caught *waiting* for the
break bell — phone checked at its strike to confirm what it
meant — the break bell alone dies. Weather must never become an
appointment. Komet's quiet-context gate also stands whole.

## FD-7 · K1 / K2 — DELIGHT POSITIONS (argued on the issue, recorded here)

- **K1 (the Deck eats HOME):** in favour, on delight grounds —
  waking inside a session to your own instrument is a bench;
  waking to a block screen is a cell. The reserve fight, priced:
  Teo's F-DAY verdict (reserve stays empty) governs the *idle*
  Deck under his reopening test — a candidate must be (a) true
  at 04:58, (b) unowned, (c) actable. K1's allowed/peek shelf
  fails that test at idle and **passes it in the sealed state**:
  at the seal the loadout is a set fact (a), Shield owns editing
  the list but nothing owns *reaching* it mid-session (b), and
  a shelf tap is a real act — peek or open (c). State changed
  what actable means; the shelf is lawful *because* K1 is a
  sealed-state proposal. The shelf must therefore never leak
  into the idle composition — Rahmen's IDLE layout stands
  shelf-free, and K1 composes only into RUNNING.
- **K2 (rotate-to-seal):** support as filed — an *optional*
  grammar, never the default. The slide's one-gesture mercy is
  the 1 AM door; the dial ritual is play for a man who wants to
  mean it twice, and play you can choose is delight while play
  you must perform at 05:00 half-asleep is a tax. Komet's Muede
  kill gate (two failed tired-hand trials = buried) is the
  right judge and this seat will accept its verdict either way.

## §8 · KILLED BY THIS SEAT, WITH REASONS (the fence, working)

- **Rim ink draining across the break** (progress in reverse as
  the break's spectacle): a second rendering of break progress —
  the hub countdown already owns that fact. One-fact-one-rendering.
- **A warmth line on the finish receipt** (`DONE. EAT.` and
  friends): the receipt's printed-slip character is the one
  surface with a proven love verdict behind it, and it is loved
  *as a factual slip*. Decorating the one loved surface with
  motivational print risks the proven thing for an unproven one.
  The receipt stays a receipt.
- **`SEALED — GO` as a running entry banner:** standing print for
  a settled fact, and a second rendering of the seal the imprint
  already strikes. The settle beat (Designer §2, PROVING) ends in
  silence and the running face — that restraint *is* the reward's
  shape on this Deck.

## §9 · ACCEPTANCE — THE DELIGHT WALK (merge renders + one device pass)

1. Seal at night: play arc ends, silence begins; the only sounds
   after t=0 are the session's own boundaries.
2. Let a block end with the phone face-down: one firm click, no
   glance needed; break ends: one light seat knock, back to work.
3. Press DONE three times in a session: identical click each
   time; no count appears anywhere; empty queue = absent line.
4. STOP inside 10s: chair reads `STOP — FREE, NO BREAK USED`,
   click is light; STOP after: price words, knock is heavy. The
   hand learns the era before the eye reads it.
5. 03:30 squint test (FD-4), all four conditions, in night dress.

## §10 · WHAT I WANT NEXT (unassigned capacity)

The running Deck's HELD chip is the one mid-session surface
nobody has given a delight read: a count of held notifications
is a small pile of proof that the seal is working *for* him.
Next pass from this seat: whether HELD's arrival (a notification
held mid-session) deserves exactly one quiet felt event — and
the argument for why it probably deserves none. Filed as a
question, not a proposal.

— Funke, enthusiastic designer, issue #6. DECLARED, uncompiled.
