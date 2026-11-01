# PARK, UNHINGED — crazy ideas for the quietest screen

*Agent: Komet (crazy-ideas) · Issue #9 (Park) · Repo: Sarthak-2007/Focus-app · Branch: `agent/issue-9`*
*One job only: ideas. Nothing here is implemented, audited, or merged. DECLARED paper.*
*Grounding: issue #9 + its 5 comments, `studio/plan/AUDIT-park.md`, the wave-1 builder
dossier, and `studio/plan/ISSUE-9-WAVE2-REPORT.md` — read before writing a word.*

---

## 0 · Where Park stands (so nobody mistakes these for re-proposals)

The sane work is DONE and I am not re-selling it: the CHECK verb exists (P-1,
generation-tied CHECKING, persistent `NOT PROVED` line), the cold-open Guard line
prints `GUARD — NOT CHECKED` and is unblankable in type (P-6), `FIRED AS SET`
stays struck (P-7 / W-11), and wave-2 landed the warrant line, `GUARD {n}/4`
headline + signed foot line, the age column, tier eyebrows, FIX's escort +
staircase label + printed verdict, the plate cut, and the `doorLocked` gate.

What's left is not correctness. It's that Park is still a screen that
**only tells the truth while you are looking at it, in the tense you ask about,
and goes silent in every wait.** Four territories, per my brief:

1. **Re-proving** — truth that renews itself without being asked.
2. **The CHECK verb** — a verb this central deserves more grammars than a tap.
3. **Cold-open honesty** — the first frame could confess a biography, not a state.
4. **Waiting without deadness** — every wait in Park (sweep, FIX trip, seal)
   currently carries silence or a tick. Each can carry live, computed signal.

Rules I held myself to: fonts Sora / Atkinson Hyperlegible / Anonymous Pro only;
never green; no AI-generated images (everything below is type, ink, and timing);
DB v11 compatibility — anything needing persistence says so and prices it;
the three standards (ARM-fast · HOLD-absolutely · TELL-THE-TRUTH) are the test
every idea had to pass. Where an idea strains an existing ruling (sound law,
motion law, the one-object law), the strain is printed in the idea, not hidden.

---

## 1 · RE-PROVING — Park proves in the dark

### 1.1 The Night Watchman Ledger
**Idea:** Park keeps a diary while closed. The shield loop already re-stamps
Accessibility + Notification Listener every ~2s during a session
(`onShieldIteration`); today a *changed* verdict evaporates into the next
snapshot. Record only the transitions — verdict flips, with wall time — into a
small append-only ledger. Opening Park then leads with one line:
`WHILE YOU WERE GONE: NOTHING CHANGED` — or `WHILE YOU WERE GONE: USAGE DIED
02:47 · CAME BACK 02:49`. This is Vega's Death Certificate grown a second
column: deaths *and* resurrections, with durations, so a door that flickers
at night is caught flickering, not just found dead in the morning.
**Mechanism:** transition detector inside `WatchStore.publish()` (compare
old vs new print per item, append on change) + a flat-file/DataStore ring of
the last ~50 entries + one print line in ParkDrawer above the board.
**Cost:** M — ~40 lines detector, ~60 persistence, ~30 print. No DB schema
change if the ring lives in a plain file beside the store. *Strain: none —
it only writes what the store already knows.*

### 1.2 The Rotating Proof (STALE becomes nearly unprintable)
**Idea:** Don't widen the 45s `STALE_AFTER_MS`. Make it unreachable while the
shield runs. The shield loop already wakes every 2s; give each iteration ONE
permission to re-query, rotating through all six — a complete fresh set every
~12s, harvested from a heartbeat that already exists. Park stops being a
snapshot camera and becomes a slow radar. A grant revoked mid-session shows
up within seconds, not at the next Deck entry or resume.
**Mechanism:** in `onShieldIteration`, index `WatchItemId.PERMISSIONS` by
iteration count and call the existing `watchGrantQuery(item)` for that one
item, stamping with the current sweep generation rules untouched.
**Cost:** S — ~30 lines, zero new triggers, zero new permissions; the six
queries are cheap state reads (instance checks, Settings reads, PowerManager).
Battery cost is one extra state read per 2s the shield already spends awake.
*Caveat printed honestly: this covers session time only — overnight, unsealed
and shieldless, Park still ages. That's what 1.1's ledger is for.*

### 1.3 The Open-Drawer Fountain
**Idea:** An *open* Park should never rot in front of its reader — the exact
P-1 tableau (GRANTED decaying to STALE at 45s while he watches) survives
today for anyone who lingers. While the drawer is visible, quietly re-sweep
every 40s — just under the stale horizon — tagged in the foot line as
`AUTO · 21:15` so an automatic proof is never dressed as a demanded one.
CHECK stays the verb *he* fires; the fountain is the floor under it.
**Mechanism:** a `LaunchedEffect(visible)` loop beside Park's existing 5s
`nowMono` beat, calling the same `runWatchSweep()`; the generation-tied
CHECKING state already knows how to ignore a sweep it didn't dispatch.
**Cost:** S — ~20 lines. *Strain: a purist could say auto-proof makes CHECK
redundant. It doesn't — CHECK is how he interrogates; the fountain is how
the room stays honest while he reads. Both print their provenance.*

### 1.4 Proof Fossils
**Idea:** Every completed sweep leaves a fossil: one tick in a foot strip,
in Anonymous Pro, one per sweep, spaced by real time — a barcode of the
day's proving. A dense morning, a six-hour gap while the phone sat unproved,
the 1 AM cluster. No numbers to interpret, no score, no grade — Monolith is
a blocker, not a tracker — just the visible sediment of *when truth was
last renewed*, sitting quietly under the plate where the colophon lives.
**Mechanism:** persist completed-sweep wall times (append one `Long` per
complete set — `lastProvedWallMillis` already marks exactly that instant) +
render ticks in the colophon row, today's window only.
**Cost:** S — ~50 lines persistence + ~20 render. No DB change (flat list).

### 1.5 Cross-Examination (CHECK the warrant, not just the grants)
**Idea:** When a CHECK completes WHOLE, Park does one more thing before it
rests: it re-derives the *warrant* at that same instant and prints whether
the two stories agree. Today the board can say 6/6 while the warrant says
UNPROVEN — because residency stamping (`noteResidencyStart`, Deck-entry
only) is a separate clock, and C-8 already flagged that the residency stamp
precedes the own-package filter. Instead of hiding that seam, Park prints
it: `BOARD WHOLE — WARRANT AGREES: LIVE` or `BOARD WHOLE — WARRANT SAYS
UNPROVEN (RESIDENCY NOT STAMPED THIS VISIT)`. The drawer's two truths,
forced to face each other on the record.
**Mechanism:** at sweep completion, call the existing
`MonolithRepository.standingWarrant()` once and print the comparison beside
the foot verdict line. CHECK must NOT call `noteResidencyStart()` (wave-1
dossier §1.5 — it would un-prove LIVE); it only *reads* the warrant.
**Cost:** S — ~40 lines, all read-only. *This is the cheapest honesty in
the file: the disagreement already exists; only the print is new.*

---

## 2 · THE CHECK VERB — more grammars for one verb

### 2.1 Deep CHECK (long-press)
**Idea:** A tap on CHECK re-measures grants. A *hold* on CHECK runs the
deep sequence: sweep → warrant cross-examination (1.5) → interceptor
re-derivation — and prints a longer verdict block, receipt-style. The hold
is not a timer and declares nothing (Feel 5's timeout ruling stands
untouched); it is a *selector* — the same way FIX's confirmed release
selects the trip. One verb, two weights: CHECK and CHECK, HELD.
**Mechanism:** long-press handler on the existing CHECK key dispatching the
extended read-only sequence; normal tap path byte-identical to today.
**Cost:** S/M — ~60 lines. *Strain: adds a second grammar to a key whose
law is one-object clarity; the deep verdict prints under its own eyebrow
(`DEEP CHECK`) so the two can never be confused.*

### 2.2 Landing Order (the sweep, made visible leg by leg)
**Idea:** The six queries land in parallel and each landing already
re-publishes the snapshot — but the board dresses the result as if it
arrived all at once. Print the *order*: as each row's fresh stamp lands
(generation-matched), a tiny Anonymous Pro ordinal appears beside it —
`1`…`6` in landing order — and stays until the drawer closes. Over days he
learns which door answers instantly and which one drags; the night USAGE
takes 900ms to answer, that number is the first symptom, printed before
any verdict exists. A sweep becomes a six-horse race where the slow horse
is the diagnosis.
**Mechanism:** derive per-item landing order from `measuredMono` within the
current `sweepGen` (the store already buries both); render the ordinal as a
superscript that fades to the age column's dress.
**Cost:** S — ~35 lines, pure derivation + print. No new state.

### 2.3 The Wager (CHECK asks him first)
**Idea:** Before CHECK sweeps, it asks *him*: two chips — `WHOLE` /
`SOMETHING'S OPEN` — one tap, then the sweep runs and the verdict line
prints his call beside the truth: `YOU CALLED WHOLE — RIGHT` or
`YOU CALLED WHOLE — WRONG: USAGE`. The app's whole thesis is a machine
that tells the truth; the wager turns that thesis inward and calibrates
the *user's* model of his own phone. A man who is wrong three nights
running stops trusting his gut about his setup — which is precisely when
he starts pressing CHECK unprompted. Skippable: pressing CHECK again
(or waiting) sweeps without a call, and the line prints `NO CALL`.
**Mechanism:** one transient state field for the call + two chips above
the key, shown only while the board is unwhole-or-stale; comparison at
the completing publication. Nothing persisted, nothing scored, no streak —
the moment it keeps score it becomes a game and leaves Park's register.
**Cost:** S/M — ~70 lines. *Crazy, and deliberately fragile: if it ever
feels like a game, kill it. As a calibration instrument, it's the only
idea here that improves the human's proving, not the machine's.*

### 2.4 CHECK's Receipt
**Idea:** Every CHECK prints a tear-off at the foot of the board — the
receipt character Sarthak already loved on the finish slip, brought to
the drawer: `CHECK №47 — 21:14:03 — WHOLE 6/6 — GEN 112`, perforation
dress, torn edge. The count persists (a number, not a history); the slip
itself lives only for this visit. Proof you can hold is believed
differently from proof you merely read — the receipt is Park admitting
that, in print.
**Mechanism:** format one line at sweep completion from data already in
the snapshot (wall clock, counted whole, `sweepGen`) + a persisted
monotonic counter for the №. Reuse the receipt slip's existing dress.
**Cost:** S — ~40 lines + one persisted `Long`.

### 2.5 Two-Eyed CHECK (the checker, checked)
**Idea:** For the counted four, CHECK queries each door by TWO independent
routes and prints disagreement instead of laundering it. Accessibility:
live instance (`instance != null`, today's presence-proof — C-7's named
weakness) *vs* the `Settings.Secure` enabled-services list. Usage: the
existing `usageAccess()` *vs* a fresh `UsageStatsManager` probe window.
Where the eyes agree, the row prints as today. Where they disagree, the
row prints `DISAGREE` in full ink with both readings named underneath —
Park confessing that its own evidence conflicts, instead of picking the
convenient one silently.
**Mechanism:** alternate query implementations beside
`MonolithRepository.watchGrantQuery(item)`, invoked only by CHECK (never
by the ambient sweep — cost stays on the verb, not the heartbeat).
**Cost:** M — ~90 lines + careful verification per route. *This is the
wildest machinery idea in the file and the most honest: C-6/C-7 stand
escalated precisely because presence-proof is weak. Two-eyed CHECK
doesn't fix the derivation law — it makes the weakness visible every
time it matters, which is the Park way of handling an unfixable truth.*

---

## 3 · COLD-OPEN HONESTY — the first frame confesses a biography

### 3.1 The Three-Tense Confession
**Idea:** A cold open currently answers one tense: NOW (and, until the first
sweep lands, `NOT CHECKED`). Give the header three tenses, in Anonymous Pro,
for exactly one visit-frame sequence:
`WAS: WHOLE AT 21:14 · DIED: USAGE, 02:47 · NOW: NOT CHECKED`
— then the sweep lands and NOW resolves into the live verdict, while WAS and
DIED stay printed in the foot as the visit's epigraph. A man opening Park at
1 AM doesn't just learn the state; he learns the *story*: it was fine when
he slept, something died at 02:47, and the drawer is finding out the rest
with him. That is Muede's 1 AM test answered in one line.
**Mechanism:** WAS = persisted `lastProvedWallMillis` (exists); DIED = the
Night Watchman Ledger's (1.1) last counted transition to MISSING/STALE-death;
NOW = the existing Guard-line derivation. Print assembly only.
**Cost:** S once 1.1 exists (~30 lines); M standalone (~110, building the
transition memory first). *Dependency printed openly: this idea is 1.1's
face. Without the ledger, DIED has no honest source and must not be faked
from the age column.*

### 3.2 Cold Open in Two Acts (Memory, then Verification)
**Idea:** Frame one of a cold open, every row wears *memory dress*: half-ink
(the STALE dress, reused), eyebrow `LAST KNOWN — FROM MEMORY`, ages already
counting. The headline stays exactly `GUARD — NOT CHECKED` — the law is not
bent, memory is never dressed as measurement. Then the auto-sweep lands and
each row *lifts* out of memory dress as its fresh stamp arrives, in landing
order (2.2). The drawer visibly does what it actually does: remember, then
verify. Today the same epistemology happens invisibly and the first frame
just looks empty; the two acts make the honesty *watchable*.
**Mechanism:** render the pre-sweep snapshot (last published state, stamps
intact) in memory dress until the first generation-matched publication of
the visit replaces it row by row. The store already retains the snapshot;
only the dress gate is new.
**Cost:** S/M — ~50 lines. *Strain: none against P-6 — NOT CHECKED still
prints; memory is labelled as memory in the eyebrow, in words.*

### 3.3 The Honest Clock (Park confesses its own absence)
**Idea:** One line, first cold open of a visit, Sora small:
`THIS DRAWER WAS LAST OPENED 6H 12M AGO.`
Not the machine's state — the *surface's* absence, confessed by the surface.
Every other line in Park accuses the machine; this one accuses Park, and it
reframes everything below it: of course the proofs are old — nobody asked.
It is the cheapest possible cure for the false comfort of a fresh-looking
drawer full of ancient stamps.
**Mechanism:** persist Park's last-open wall time (one `Long`, written by
the existing `LaunchedEffect(parkOpen)` hook); print the delta once per
visit above the board.
**Cost:** XS — ~15 lines. *The smallest idea in this file and, per line,
maybe the most Park sentence ever proposed.*

### 3.4 The Stranger's Sentence (process-cold first frame only)
**Idea:** After a *process* cold start — phone rebooted, app swiped away,
the truly blind case P-6 was born from — the first frame carries one
plain Atkinson sentence above the board, written for a tired stranger who
has never seen this app:
`I DON'T KNOW YET WHETHER I CAN BLOCK. CHECKING NOW.`
The first completed sweep replaces it with the verdict line and it never
appears again that process. Not a tutorial, not onboarding copy — a
one-time confession in human grammar, exactly at the moment the technical
grammar (`GUARD — NOT CHECKED`) is most likely to be misread as fine.
**Mechanism:** a process-level flag set at `Application`/Activity create
when no snapshot exists; cleared on the first complete-set publication.
**Cost:** S — ~35 lines. *Gate it hard: drawer-reopens within a warm
process never show it. Said once, to a stranger, then never again.*

---

## 4 · WAITING WITHOUT DEADNESS — every wait carries computed signal

Park has three waits: the sweep (seconds), the FIX trip (minutes, off-screen),
and the seal (all night). The law for all four ideas below: **copy is
generated from stamps and state, never canned** — a waiting line that can
repeat verbatim is a spinner wearing words.

### 4.1 The Narrated Age (the 5s beat learns to speak)
**Idea:** Park already wakes every 5s to re-derive its prints. While a
counted row sits STALE with no sweep running, let the foot line narrate
the wait from real numbers, changing every beat because the numbers move:
`USAGE LAST ANSWERED 3M 05S AGO · NO CHECK SINCE 21:14 · CHECK COSTS ONE TAP`
— every clause computed from `measuredMono`, the last sweep's wall time,
and the row's own state. No animation, no pulse (Feel 3's motion law
stands); the *information* is the motion. Waiting stops being dead the
moment it tells you something true that wasn't true five seconds ago.
**Mechanism:** a pure formatter over the snapshot + visit state, rendered
in the foot line slot when (counted STALE ∧ ¬CHECKING ∧ ¬sealed).
**Cost:** S — ~45 lines. *Risk printed: narration must never editorialize
(\"still waiting…\"). Numbers and one flat fact about the verb. Nothing else.*

### 4.2 The FIX Trip Time Capsule (the off-screen wait, reported)
**Idea:** Wave-2 gave FIX a printed verdict — `FIXED — X` / `STILL OPEN — X`.
But the trip itself is a black box, and the nightmare case is invisible:
he fixes Accessibility in Settings while Usage quietly dies in the same
two minutes, and Park prints `FIXED` and rests. So: at FIX press, Park
seals a copy of the board. On return, *above* the verdict, it prints the
trip's story: `AWAY 1M 48S · ACCESSIBILITY: MISSING → GRANTED · USAGE:
GRANTED → MISSING · NOTHING ELSE MOVED`. The verdict still prints; now
it prints *in context*, and a collateral death can never again hide
behind a successful repair.
**Mechanism:** snapshot copy held in the ViewModel at FIX dispatch
(the press already records `fixPressedItem`); on the return sweep's
completing publication, diff old vs new prints per item and format the
changed rows only. State dies with the visit — nothing persisted.
**Cost:** S — ~60 lines. *This is diff-print (Dens's residue idea) cut
to the one journey where the diff is a verdict, not a decoration.*

### 4.3 The Slowest Door Gets the Floor
**Idea:** When a CHECK leg hasn't landed ~1.5s after dispatch, Park does
not spin and does not sit silent: the pending row *grows* — the drawer
already owns two rest heights and row composition — and states, in
Atkinson, what is actually being waited on:
`ASKING ANDROID ABOUT USAGE ACCESS — NO ANSWER YET (1.5S)`.
When the stamp lands, the row settles and the sentence is replaced by
the age print. A slow leg stops being a hang and becomes a named,
timed fact about Android's own responsiveness — and on a healthy phone
it simply never appears.
**Mechanism:** per-item pending = (item's stamp generation ≠ current
`sweepGen` ∧ sweep in flight ∧ elapsed since `pendingSweepStartWall`
> threshold) — all derivable from fields the store already publishes;
one conditional row expansion in ParkDrawer.
**Cost:** S — ~45 lines. *Threshold is a display fact, not a verdict —
Feel 5's anti-timer ruling governs verdicts; nothing here declares a
sweep failed, it only names a wait while the wait is happening.*

### 4.4 The Seal Vigil (the night-long wait, kept watchable)
**Idea:** P-8's cheap half ships a warning at seal time. The full wait —
arm to morning — stays blind: after the SLEEP seal, a door that dies at
02:47 cannot even be *viewed* until morning. Give sealed Park a
**view-only vigil**: the drawer opens during seal, the board live
(re-stamped by the shield loop / 1.2's rotation), every control replaced
by its sealed print (`SEALED — VIEW ONLY`), FIX and CHECK visibly
inert, and the Night Watchman Ledger (1.1) accumulating at the foot so
the morning open starts with the night's whole story already written.
Nothing can be changed; everything can be *seen*. HOLD-absolutely is
about not breaking the seal — watching through the glass breaks nothing.
**Mechanism:** a `viewOnly` composition of ParkDrawer gated on
`doorLocked` (the gate wave-2 already composed), controls rendered
disabled-with-print; ledger + headline prints reuse existing derivations.
**Cost:** M — ~120 lines. *Strain printed plainly: this re-opens the
sealed-blindness ruling (P-8) in the opposite direction from its cure —
the audit chose disclosure-at-seal over view-during-seal. This idea says:
why not both, with the seal's power untouched. Ren's call, not mine.*

---

## 5 · THE PARKED DREAM — Live-Fire, dreamed about, NOT proposed as work

> **PARKED. Parked. Parked.** The Live-Fire Strip is a parked decision
> (issue #21, Sarthak's call). Nothing in this section is approved work,
> a recommendation to unpark it, or a shadow plan. My brief allows me to
> dream about it, clearly marked — so, one dream, clearly marked:

**The Ghost Rehearsal.** If Live-Fire ever unparks, Park shouldn't run it
on demand like a party trick — it should *haunt*: once, in the FASTED
morning window, after a night where the ledger (1.1) recorded a counted
door flickering, Park opens with a single quiet card where the confession
line sits: `USAGE FLICKERED TWICE LAST NIGHT. REHEARSE THE PIPELINE?`
— one tap runs Vega's strip (sweep → `provePipelineNow()` → sacrificial
decoy → self-posted notification, mono-stamped, receipt-perforation
print), dismissing the card buries it for a week. The rehearsal is
*offered by evidence*, never by schedule and never automatically.
**Mechanism (dream-grade):** ledger flicker detector → offer card →
the strip as already specced in AUDIT-park §5 (~300–400 lines, no DB,
no new permission).
**Cost:** the strip's own price, plus ~40 lines for the evidence gate.
**Status: DREAM ONLY — parked under #21 until Sarthak says otherwise.**

---

## 6 · If Ren keeps only three

1. **1.2 The Rotating Proof** — kills the 45s rot at the root, ~30 lines,
   rides a heartbeat that already exists. The highest truth-per-line
   ratio in this file.
2. **4.2 The FIX Trip Time Capsule** — makes a collateral death during a
   repair *unhideable* for ~60 lines and zero persistence.
3. **3.3 The Honest Clock** — 15 lines, and Park accusing *itself* once
   per visit changes how every other line on the board is read.

And the one I'd fight for despite its price: **2.5 Two-Eyed CHECK** —
the only idea that attacks a *standing escalated charge* (C-7) instead
of decorating around it.

## 7 · What I left alone, on purpose

- The CHECKING state stays generation-tied; no timers declare verdicts
  anywhere above (Feel 5's ruling, Dens's fence — respected, not relitigated).
- Sound: nothing here adds a sound. (An earlier seed of mine — a sweep
  that clicks leg by leg — dies against Feel 2's sound law: CHECK-unwhole
  is silence + print, and a healthy sweep has no sound budget to spend.
  Killed in the open, here, rather than smuggled in.)
- The plate, the tiers, the colophon: wave-2's composition stands. Crazy
  is for the silences, not for re-fighting settled composition.
- No scores, no streaks, no grades anywhere — Monolith is a blocker,
  not a tracker; 2.3's wager is built to forget itself for exactly
  this reason.

## 8 · What Komet wants next

- **Unassigned, offered:** the same four-territory treatment for the
  *Seal* moment itself (the 60-second emergency-break hold is the
  app's other great wait, and it is currently one long silence).
- **If any of §1–§4 is picked:** I want the seat that writes its print
  census — every new line above should be grep-pinned the way wave-1
  pinned `GUARD — NOT CHECKED`, so a future skin can never quietly
  unprint Park's confessions.

— Komet (crazy-ideas, issue #9)
