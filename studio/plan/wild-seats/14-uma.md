# Uma — Wild, museums

My wave-1 seat built a museum: vitrines, an accession registry, a permanent
collection. Sarthak has since killed the Ledger/Vault outright — there is no
collection, no archive, no room to walk back into. So this seat's job inverts:
**take everything the museum knew about provenance and permanence and compress
it into the two surfaces that survive — the one session-end receipt, and one
sentence in Shield.** Provenance must be complete at the moment it is printed,
because it will never be printed again. Permanence must be honest about what
actually persists, because there is no registry pretending otherwise.

The compression, in one line: **the app keeps a number and a verdict. The slip
keeps everything else — once.**

---

## Deck, session end — the receipt: provenance and permanence in one slip

### Chosen form — THE NUMBERED SLIP (the only copy, and it says so)

A single flat slip rendered inline on the Deck's end state, replacing the
readout where the session just ran. It is not an overlay, not a card that
rises, not a screen he navigates to. It carries the session's full provenance
chain and its deflection record, bears a permanent sequence number, and
states plainly — on its own face — that it is not kept. When the next session
completes, the next slip replaces it. That is the entire afterlife a session
gets, and the slip never lies about it.

**Layout (top to bottom, one column, no grid, no tiles):**

1. **Header baseline.** Left: `SESSION RECEIPT` — Anonymous Pro caps 10sp,
   tertiary tier (~3.4:1), letterspaced. Right, same baseline: the number,
   `№ 047`, Anonymous Pro Bold 12sp, primary tier (~11:1). The number is the
   most permanent thing on the slip, so it gets the strongest ink. No orange
   anywhere on this surface — orange is a Commit fill (Deck slide, Tasks
   START) and a receipt commits nothing.
2. **Title.** The task title exactly as it stood at arm time — Sora SemiBold
   20sp, primary tier. If the session had no task, the line reads
   `UNTITLED SESSION`. The app never invents a chapter name to make the slip
   look better.
3. **Medium line.** Anonymous Pro caps 10.5sp, secondary tier (~5.6:1):
   `FOCUS · 50 MIN PLANNED · 50 MIN SEALED`. If banked break time was spent,
   it is stated separately, never folded into the held figure:
   `50 MIN SEALED · 30S ON BANKED BREAK`. Sealed time and break time are two
   facts; the slip prints both or neither.
4. **Provenance block.** Atkinson Hyperlegible 13sp, secondary tier, one fact
   per line, hairline rules (1px, fill cap `#4A423C`) between lines:
   - `Armed by hand at 05:02 · sealed until 05:52` — always present.
   - `Within his routine — Fasted block, 05:00–08:00` — present **only** when
     the arm timestamp falls inside an enabled routine window. The wording is
     *within*, never *by*: the routine did not start this session, and the
     slip must not say it did. A routine never records a session he was not
     in; this line records a session he *was* in, and says only where it sat
     in his day.
   - `Loadout as sealed — 14 apps · 3 sites` — counts from the arm-time
     snapshot (below), never from the live list.
5. **Deflection record.** Headed by one micro-label, Anonymous Pro caps 9.5sp,
   tertiary tier: `TURNED AWAY, AS LOGGED BY THE SHIELD`. Below it, one row
   per app, Anonymous Pro 10.5sp, secondary tier:
   `INSTAGRAM — 05:17, 05:31` · `YOUTUBE — 05:44`. Every recorded event's
   time is printed; rows group by app so the slip stays compact at any
   volume, but no event is dropped, rounded, or summarised away. **No total
   is printed.** Not here, not anywhere (see the Shield element — the
   aggregate count is a rendered fact this design refuses to create).
   If nothing was recorded, the block is one line: `Nothing tried to get in.`
6. **Footer — the permanence statement.** Atkinson 12sp, tertiary-to-secondary
   tier, the last line on the slip:
   `This slip is the only copy. The app keeps the number, not the slip.`
   Beneath it, one borderless text action, Atkinson 13sp, primary tier,
   no fill, no chip: `Save slip as image`. It renders the slip to PNG via
   graphics-layer capture and hands it to the system share/save sheet. If he
   saves it, permanence lives in his gallery, by his act. The app retains
   nothing extra either way, and never prompts him about it again.

**Mechanism and data sources — where every line's truth comes from:**

- **Arm-time snapshot.** At the instant of sealing, the app freezes a
  `ProvenanceSnapshot`: arm timestamp, planned minutes, the task's title
  *string copied at that moment* (the task may be renamed or deleted later;
  the slip reads as the session was, not as the list looks now), and the
  loadout as enforced — the package/site set the ShieldService actually
  sealed with, counted at that instant. A receipt built from live settings
  at session end would let a mid-week Guard edit rewrite a finished
  session's provenance. The snapshot is what makes the slip a record instead
  of a re-render.
- **End-time assembly.** At natural completion, the slip is assembled from
  the snapshot plus: sealed elapsed seconds (wall-clock, from the session
  state machine), banked-break seconds actually spent (from the break bank's
  own ledger of that session), end timestamp, and the deflection event log
  for the sealed interval only.
- **Deflection events, defined tightly.** Source: blocked-launch events
  logged by the accessibility/usage-stats shield during the sealed interval.
  Events inside a banked-break window are **not** deflections — he was
  allowed in; they belong to the break figure, never to this list. Repeated
  OS firings for the same package within 30 seconds collapse to one event at
  the first timestamp (the log's noise must not inflate the record). The
  header's `AS LOGGED BY THE SHIELD` is load-bearing: the slip claims what
  the shield recorded, not omniscience about his intentions.
- **The number.** A single persisted integer counter, incremented atomically
  at — and only at — the moment a completed session's slip is rendered.
  Abandoned, killed, or never-sealed sessions consume nothing, so the
  sequence has no gaps and needs no excuses. The counter is the *only*
  permanent artefact of any session: `№ 048` arriving next time is proof
  `№ 047` existed, without a single byte of 047 being stored. Permanence by
  sequence, not by storage.
- **Single-slot retention.** The rendered slip is held in one `lastReceipt`
  slot. It remains the Deck's resting state until the next completed session
  overwrites the slot with its own slip. There is no list, no index, no
  second slot. Overwrite is the design, stated on the slip's own footer.

**Motion:** No rise, no stamp, no stagger. On completion the readout
crossfades to the slip: content alpha 180ms EaseOut, slip surface tone
arriving over 240ms `FastOutSlowIn`. All lines appear together — a printed
slip does not read itself aloud line by line, and delaying the number for
theatre (wave-1's 500ms stamp beat) is ceremony tax on the exact moment he
may want to arm again.

**Haptics:** One soft settle at slip appearance, `12ms @ 0.4` — and nothing
else. No knock for the number: weight is conveyed by sequence and ink, not
by a thump pretending a registry exists behind it. Save-as-image: silence.
The arm that replaces the slip: the existing arm haptics, unchanged.

**Sound:** Explicit silence. No printer whir, no stamp thock, no completion
jingle. Those sounds promise an archive. There isn't one.

**Depth / material:** Strictly the locked vocabulary. Night ground `#171412`;
the slip is one surface step up (≥1.35:1 against ground), flat, with 1px
hairline rules at the `#4A423C` fill cap between provenance lines; micro-labels
letterpressed at the 3.4:1 tertiary tier, body at 5.6:1, title and number at
~11:1. Real font weights only (Sora SemiBold, Atkinson regular, Anonymous Pro
Bold are actual files). No shadows, no glass lines, no gradient — wave-1's
vitrine reflection is dead with the Vault that housed it.

**Guard fork:** The loadout line reads its counts from the enforcement set
the ShieldService seals with, which is the same set under both Guard
outcomes — if Guard survives as a screen, that screen edits the set; if
Guard folds into Shield, Shield edits it. The slip's source, wording, and
placement do not change either way. (His call, untouched.)

### Runner-up — THE ACCESSION STAMP (wave-1, full ceremony card)

A catalogue card rises over the Deck (24dp, 380ms `FastOutSlowIn`), holds a
beat of stillness, then the accession number stamps down at t≈500ms (scale
1.6→1.0, 90ms hard decel), with the app's heaviest haptic (`22ms @ 1.0` plus
a 40ms-later paper settle `10ms @ 0.3`) and a dry 120ms thock if sound is on.

**Why it lost:** it is a ceremony wrapper, and ceremony is settled-dead — but
the deeper failure is truth. A stamp, a registry number delivered like a
verdict, and a card that sinks away toward "the Vault" all *imply
retrievability*: that this record now lives somewhere permanent and can be
consulted. It cannot. Ledger is dead; dressing the one surviving slip in
registry theatre manufactures exactly the false permanence TRUTH-once
forbids. It also taxes the ending: an overlay to dismiss (a post-session
decision the chosen form never asks for), 500ms of withheld information, and
the loudest haptic in the app spent honouring a record nobody keeps.

### Standards check — receipt

- **ARM ≤10s:** Zero cost. The snapshot is taken inside the arm action he
  already performs; the slip lives on the end state and the next arm gesture
  (the existing slide, in its existing place below the slip) replaces it
  without a dismissal step. 0 added gestures, 0 added waits.
- **HOLD absolute:** Zero mid-session anything. Nothing is shown, asked, or
  collected interactively during the seal; the deflection log is written by
  the shield service, never surfaced until the session is over.
- **TRUTH once:** Provenance is rendered exactly once, from frozen sources,
  with its three classic lies engineered out — live-loadout rewrite (killed
  by the arm-time snapshot), routine false attribution (killed by *within*,
  not *by*, plus timestamp-in-window check), held-time inflation (break
  seconds printed separately, never inside the sealed figure). The
  permanence claim is printed on the slip itself and understates rather than
  oversells: number kept, slip not. The deflection total is never computed
  for display anywhere in the app (see below), so no duplicate or rival
  rendering of it can exist.

---

## Shield / chrome — deflection truth without a stats shrine

### Chosen form — THE VERDICT LINE (one sentence, overwritten, never counted)

One fixed-height line slot at the top of Shield, directly under the header,
printed on the ground — no card, no fill, no chip. It states, in one human
sentence, what the *last completed session* proved about the shield, and it
is overwritten wholesale by the next completed session. It never shows a
count, a per-app breakdown, a streak, a trend, or a history — and it is
engineered so it *cannot*: **the Shield composable receives only a verdict
enum and an end date, never the event list.** A total cannot leak onto this
surface by accident or by a future well-meaning edit, because the data to
build one never arrives here.

**The sentence (prefix in Anonymous Pro caps 9.5sp, tertiary tier; sentence
in Atkinson Hyperlegible 13sp, secondary tier):**

- No session has ever completed:
  `LAST SESSION — ` *(absent; instead, whole line at tertiary tier:)*
  `No session has tested the Shield yet.`
- Last session, zero recorded attempts:
  `LAST SESSION · ENDED 29 OCT, 05:52 — Nothing tried to get in.`
- Last session, attempts recorded, all turned away:
  `LAST SESSION · ENDED 29 OCT, 05:52 — Everything that tried was turned away.`
- Last session, shield enforcement was down for part of the seal
  (accessibility permission lost, detected by service heartbeat):
  `LAST SESSION · ENDED 29 OCT, 05:52 — The Shield was off for part of that
  session.` — the only state in which the line is tappable, taking him to
  the enforcement/permission surface. A verdict that can only ever be good
  news is not a verdict; the negative state is what makes the positive ones
  worth reading.

The date is part of the sentence, always. A verdict without its date would
let a three-week-old clean session masquerade as current truth; with the
date printed, staleness is visible at a glance and the sentence ages
honestly in place — no expiry logic, no "this week" framing, no refresh.

**Mechanism and data source:** At session completion, the same event log
that feeds the receipt is reduced — once, at write time — to a single
rolling struct `{ verdict: UNTESTED | CLEAN | HELD | COMPROMISED, endedAt }`,
stored in one slot and overwritten by the next completion. Break-window
openings cannot produce COMPROMISED or count as attempts (he was allowed
in); COMPROMISED is set only by an enforcement-permission gap *outside*
break windows during the sealed interval. Nothing accumulates: there is no
field anywhere in this design that sums deflections across sessions.

**Where it does NOT appear:** Not on the Deck, not in the bottom nav, not
as a badge or dot. One fact, one home: Shield. During a running session the
line still shows the *previous* session's verdict, dated — it makes no
live claims, so HOLD is never interrupted by a deflection counter ticking
up in chrome while he is supposed to be working.

**Motion:** None. The line swaps instantly at session end (between sessions,
when nobody is watching it change). A label is replaced, not performed; any
animation here would be the shrine clearing its throat.

**Haptics:** Silence. The line is not interactive in its normal states.
In the COMPROMISED state only, tapping it gives the standard navigation
tick, `6ms @ 0.3`.

**Sound:** Explicit silence, all states.

**Depth / material:** Type printed directly on Shield's ground, separated
from the content below by a single 1px hairline at the fill cap `#4A423C`.
Fixed slot height (20dp) so verdict changes — including the longer
COMPROMISED sentence wrapping to two lines at large text sizes, accommodated
by a reserved two-line max with the hairline below it — never shift the
layout beneath. No tone step, no recess: the flattest surface in this
entire plan, deliberately. Shrines get plinths; a label gets print.

**Guard fork:** The line's home is Shield under both outcomes, unchanged in
wording, placement, and data. The fork touches only the COMPROMISED tap
target: if Guard survives as its own screen, the tap lands on Guard's
permission surface; if Guard folds into Shield, it lands on the enforcement
section of the same screen he's already on. (His call; the label argues
nothing either way.)

### Runner-up — THE QUIET TALLY (count in chrome, kept "tasteful")

Shield header carries `TURNED AWAY — 4` in Anonymous Pro caps, with a hairline
notch added under the Deck's chrome for each live deflection as it happens
(notch fade-in 160ms EaseOut, haptic and sound explicitly none — the
restraint is the pitch: "a counter, but a quiet one").

**Why it lost:** restraint does not save it; a count is a shrine at any
volume. Four specific failures: (1) the aggregate it renders duplicates the
receipt's provenance as a rival rendering of the same events — one fact,
two homes; (2) a running total invites comparison across days, which is the
first step to streaks, which is the whole shrine; (3) the live notch is a
mid-session reward signal arriving inside HOLD — each deflection would
visibly *pay out* while he works, a decision-adjacent distraction the
standards exist to kill; (4) any cap or compression of the notch row (and
there must be one) truncates the record and lies by omission. The chosen
form's refusal is structural, not tonal: pass Shield an enum, not a list.

### Standards check — verdict line

- **ARM ≤10s:** Zero cost. The line is read between sessions; arming never
  touches it, and it adds no step to any path.
- **HOLD absolute:** Zero mid-session decisions, zero mid-session signals.
  It renders the previous session's dated verdict during a run and updates
  only at completion, after the seal is already over.
- **TRUTH once:** The verdict is the *only* aggregate rendering of
  deflection in the app, and the receipt holds the only per-event rendering
  — two different facts (what happened, event by event / what it proved, in
  one sentence), each with exactly one home. The sentence is derived from
  the same single log at the same single moment, carries its date so it
  cannot impersonate fresh truth, and has a real negative state, so it is a
  measurement and not a compliment.

---

## Too good to lose

**1. The fresh-slip light.** For the first stretch after completion, the
slip sits at its full surface step; once it is no longer the just-finished
session — concretely, when the app next cold-opens or the next session arms
— it settles one step down toward the ground and its ink drops from primary
to secondary tier (tone lerp 800ms `FastOutSlowIn` if the change happens
on-screen; silent, no haptic, no sound). No `NEW` tag, no badge: a slip
under fresh light is today's, a dimmed slip is simply the last one, and its
date line already says which. It gives the Deck's resting state an honest
sense of *when* without adding a single rendered fact — age shown as light,
not as data.

**2. Loadout names behind the slip's own long-press.** The receipt's face
shows loadout counts (complete provenance would name names, and 14 app
names would bury the slip). One long-press on the loadout line expands it
in place — height ease 220ms `FastOutSlowIn`, single haptic `8ms @ 0.35` on
expand, silence otherwise — into the full arm-time snapshot list, Atkinson
12sp, apps then sites, exactly as sealed. Post-session only, on the slip
itself, so the names' one home stays the receipt; the face stays readable
and the full record is one press away when he wants to check what he
actually sealed against.

---

## Mail / wall note

Inbox held two letters on arrival — from Orson (05:14) and Mara (05:17),
both relaying Sarthak's citation-lift order verbatim; read in full and
moved to `read/`. The same order arrived in my assignment directly and is
posted on the Commons wall by Ren (05:14); I am passing it down once more
via a signed wall append alongside this file, verbatim, so no seat can miss
it. No other mail sent. My wall append also carries this seat's thesis in
one line — *the app keeps a number and a verdict; the slip keeps everything
else, once* — for the Shield and receipt seats building nearby.

*— Uma, Wild (museums)*
