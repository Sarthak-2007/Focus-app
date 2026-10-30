# KOMET — CRAZY IDEAS FOR THE DECK (ISSUE #6)
## Seat: Komet, crazy-ideas, dedicated-100 programme · Fri Oct 30, 2026
## Status: DECLARED, paper only. Nothing compiled. Posted live as 4 comment waves on issue #6 while written.

Read first: `AUDIT-deck.md` whole (22 items, Rost's verdicts stand —
nothing here re-opens a ruling), `BEYOND-deck.md`, and
`ISSUE-6-innovator.md` (P1–P7). The brief: go PAST the dial-as-its-own
clock (Fusion, F18 — already on record). Nothing below re-files Fusion,
Vega B (slide carries proof), Muede's 05:00 door, Edge-Triggered Truth,
the Truth Token, the Mode-Typed Contract, Earned Silence, Felt Truth,
Dark Deck, or Seal Echo. Those are the floor; this file is past them.

**Walls, honoured in every idea:** Monolith is a blocker, not a
tracker — no scores, no streaks, no reward economy, no
history-as-judgement. The STOP valve's price never rises. Truth laws
stand (ARM fast · HOLD absolute · TELL THE TRUTH once). No new colour
is proposed anywhere in this file; every visual note rides existing
scheme tokens (F17). No generated imagery — anything printed is
deterministic type/SVG in the standing fonts (Sora / Atkinson
Hyperlegible / Anonymous Pro).

Each idea: mechanism in plain words · what it replaces · spec sketch
(states / inputs / outputs) · price (S = a sitting · M = a lane ·
L = a restructure, per the audit's scale) · kill gate (the condition
under which the idea itself dies, agreed in advance).

---

## K1 · THE DECK EATS THE HOME SCREEN — during a seal, the Deck IS the launcher

**Mechanism.** Every focus app ships the same hole: the block screen
is a room you are thrown into when you touch a forbidden door, and the
real home screen still exists underneath, still calling. Kill the
underneath. At the seal, Monolith registers as the device HOME. Waking
the phone during a session lands on the dial, which is now the running
clock (Fusion inherited). Allowed and peek apps are not a list in
another screen — they are the only other objects physically present in
this home, laid out like instruments on the same bench. There is no
block *screen* because there is no second place. Session end / STOP
hands HOME back to the stock launcher in one proved handover, carried
on Release Proof's HELD FRAME.

**Replaces.** The block-screen-as-interrupt model for the session's
whole life; the sealed Deck stops being a dashboard inside an app and
becomes the phone's ground state.

**Spec sketch.**
- *Inputs:* seal event (t=0, Opal's proof-seal frame, unchanged);
  session end / valve resolution; HOME role grant state.
- *States:* `NORMAL-HOME` (stock launcher owns HOME) ⇄ `DECK-HOME`
  (entered at t=0, exited at receipt + sled-home beat). Role requested
  at first seal only, never at install.
- *Outputs:* Deck gains a launcher mode = sealed-only composition:
  Fusion hub readout + STOP chair + allowed/peek shelves in the
  reserve; nothing else composes. Boot recovery: if the app finds
  DECK-HOME persisted across a reboot with no live session, it
  releases the role before composing anything.
- *DB:* none — a role flag + the session record that already exists.

**Price: L.** **Risk: high,** honestly — launcher-role behaviour
varies by OEM. **Kill gate:** if the role grab adds >1s to wake, or
any frame of the stock launcher leaks on wake during a session, the
idea dies; a leaked old home is worse than no idea.

## K2 · ROTATE-TO-SEAL — the safe-combination seal

**Mechanism.** A safe does not slide — it dials: past the number,
back, settle. An alternative seal grammar: seal BY the wheel itself —
turn to your span, overshoot by a deliberate margin, come back and let
it seat. The combination is not a password; the combination IS the
session (span–blocks–span read off the turns). It takes ~2s longer
than the slide, and that is the point: sealing becomes a two-handed
act on the instrument that cannot happen by accident or on autopilot —
the 03:30 hand has to mean it. The slide stays the default; this is a
per-user seal-grammar choice, a ritual upgrade argued as an option,
not a replacement.

**Replaces.** Nothing by default. Offered beside the slide in the
Deck's own settings slot.

**Spec sketch.**
- *Inputs:* wheel turns (detent stream the view already emits);
  `sealGrammar = SLIDE | DIAL` per-user flag.
- *States:* DIAL grammar arms a 3-phase turn detector: pass span+10 →
  reverse → seat within ±2 for 300ms → onLatch → proving exactly as
  the slide's path (Opal's dispatch law untouched; t=0 unchanged).
- *Outputs:* same seal, same cascade, same cadence.
- *DB:* none.

**Price: M.** **Kill gate:** Muede's 1 AM test — if a tired hand
fails the combination twice in trials, it is theatre; bury it.

## K3 · SHIP'S BELLS — the sealed Deck tells time without a screen

**Mechanism.** Ships underway never watched clocks; the watch struck
bells — a learnable chime pattern, until you *know* it is the
half-hour without counting. Sealed Deck, same: at each block boundary
and half-block, the phone in a pocket or face-down on the desk sounds
a small bell pattern (twin soft strikes, in Orson's Laid-Seal voice
family, never the Throw). Two strikes = break coming; the long
pattern = session done. No glance, no screen-on, no remaining-time
anxiety; time arrives as weather. It extends the AOD desk clock's job
off the desk entirely.

**Replaces.** The need to wake the phone to learn where the session
stands — mid-session screen checks that exist only to read the clock.

**Spec sketch.**
- *Inputs:* session frame clock (same elapsedRealtime family as the
  seal shot); block index; break flag.
- *States:* patterns for HALF / BLOCK-END / BREAK-START / END only.
  Silent inside SLEEP routines; off by default until turned on once.
- *Outputs:* `BellPattern(kind)` → TactileSoundEngine schedule,
  volume capped under media volume. One switch (hub long-press menu),
  no settings page of toggles, no print anywhere.
- *DB:* none.

**Price: S–M.** **Kill gate:** if it ever sounds in a context that
should be quiet, the whole bell dies — not a patch, the bell.

## K4 · THE NO-NUMBERS DECK — commit first, count later

**Mechanism.** Every focus app lets you bargain: 50 feels hard, shave
to 40, to 35 — the dial is a negotiation held at the moment you are
weakest. Inversion: a Deck mode with NO rim numerals and NO hero total
while setting. Turn the wheel by feel — detents knock, arc fills — and
the machine seals whatever you turned to. Only AFTER t=0 does the Deck
confess, once, in the hub: `YOU SEALED 2H 10M`. You cannot shave a
number you never saw. Setting stops being shopping and becomes a vow
made by the hand. TELL-THE-TRUTH is honoured at the only moment truth
cannot be used to bargain: after commitment. Pre-seal it prints one
honest thing — the arc's proportion of the full sweep (geometry, not
digits). A pre-seal long-press peeks the number for 1s; peeking is
allowed, just *felt* — recorded nowhere, judged nowhere.

**Replaces.** The bargaining loop at the setting gesture; the recipe
line's job for spans set blind (the arithmetic is confessed whole,
post-seal, instead of explained pre-seal).

**Spec sketch.**
- *Inputs:* `blindSet` flag on the Innovator-P3 `DeckSet` payload —
  a field, not a hack.
- *States:* setting = ticks + sweep only; post-seal confession =
  Fusion hub readout, unchanged.
- *Outputs:* composition suppression of numerals/hero in idle only.
- *DB:* none.

**Price: M** (mostly deletion + one flag). **Kill gate:** Fremd's
5-second stranger test — if a stranger cannot guess what the wheel
does with no numbers, it ships as a buried opt-in, never a default.

## K5 · THE TWO-SELVES RATCHET — tonight-you sets the floor, morning-you can only raise it

**Mechanism.** The Deck serves one person: whoever holds the phone
now. The app exists because there are two — the clear head planning at
night, and the 05:00 self who would sell the session for ten minutes.
Give the clear-headed self a mechanism, not a reminder: a ratchet. At
night, on the same Deck, turn the wheel to tomorrow's minimum and
ratchet it — the hub prints `FLOOR SET BY LAST NIGHT`. At 05:00 the
wheel will not turn below that floor: detents below it are dead, their
knock drops to the REFUSED class (Felt Truth's family). Turning UP is
free — morning ambition is welcome — never down. The floor expires at
session start or noon, whichever first; an unused floor dissolves,
unrecorded, unjudged. Vega's Strait generalised: the wheel already
knows resistance-as-meaning; the ratchet is resistance with an
*author*, and the author is him, yesterday.

**Replaces.** Willpower-by-notification; the routine prime's monopoly
on "a session prepared by a better self" — a floor can be set by hand,
any night, for any morning.

**Spec sketch.**
- *Inputs:* floor-set gesture (set + hub press-hold 600ms); clock.
- *States:* floor = one record (span, blocks, expiry) in a small
  standalone store (DayWordStore pattern — no Room change, DB version
  untouched). Wheel clamps at floor while live.
- *Outputs:* `DeckSet` payload carries `floor`; idle eyebrow prints it
  in F4's routine-naming slot family: `FLOOR · SET LAST NIGHT`.
- *DB:* none (standalone store).

**Price: M.** **Kill gate:** the ratchet binds the *setting*, never
the *exit* — STOP keeps its exact ruled price. Any draft that lets a
floor raise the exit price is the thing that dies.

## K6 · THE BLIND DECK — the whole Deck, playable eyes-closed

**Mechanism.** F22 buys semantics so a screen reader can name the
wheel. Go past compliance: design the Deck as an instrument that needs
NO eyes at all — not as an accessibility feature, as a feature for a
man arming a session in a dark room or a corridor, phone half out of a
pocket. Detent pitch rises with span (a learnable scale: the 50 knock
sounds higher than the 30 knock), block count is a chord (1/2/3 low
notes stacked), the catch gets a tone, the refusal its dull knock
(already specced in BEYOND W4), the seal its cadence. A practised user
arms a full session — set, prove by ear/hand, seal — without the
screen ever lighting. The screen stays fully composed for eyes-open
use; this is a second, complete sensory rendering of the same state
machine, not a mode. No focus app is an instrument in the musical
sense; this one would be.

**Relation to P5 (Felt Truth):** P5 puts the *wire* in the hand. K6
puts the *whole session* in the ear. They compose; neither is the
other.

**Spec sketch.**
- *Inputs:* the same state the Canvas draws — zero new state.
- *States:* pitch classes per detent band (5 max — learnable, not a
  piano); chord-on-hub-press for blocks; catch/refusal/seal tones from
  the existing sound families.
- *Outputs:* TactileSoundEngine mappings driven from Deck state.
- *DB:* none.

**Price: M–L** (the cost is sound design, not code). **Kill gate:**
the blindfold arm-test — set 3×50 and seal, eyes shut, in <15s after
5 practice tries; if a tester cannot, simplify the scale before
anything ships.

## K7 · SCENE SEAL — one slide re-stages the entire phone

**Mechanism.** The seal blocks apps; the rest of the phone stays as
distracting as it was — bright wallpaper, badges, ringtone personality.
The tower-fader idea at full size: the Deck is a master fader for the
whole device. The slide cross-fades the phone into study-scene over
the same ~1s the Vertigo ground-recede takes: wallpaper → the Deck's
own ground tone (a plain dark field, hand-set), brightness ceiling
capped, notification sounds → the seal's sound family only, DND on,
badges off where the OS allows. At receipt the scene cross-fades back,
carried on Release Proof's HELD FRAME: silence, then the phone visibly
becomes his again.

**Truth discipline.** Every scene change is listed on the Deck
pre-seal in one plain line — `SEAL ALSO: DARKENS · CAPS BRIGHTNESS ·
SILENCES`. Anything the OS will not actually do is struck from the
line, never faked. A scene he cannot read is a scene that lies.

**Spec sketch.**
- *Inputs:* pre-seal scene capability probe (per item, proved like
  the warrant's links); seal; receipt.
- *States:* `SCENE-HOME` ⇄ `SCENE-STUDY`, transitions bound to t=0
  and to the HELD FRAME respectively.
- *Outputs:* per-item appliers with per-item reversal; boot recovery
  restores the pre-seal scene before anything composes if a crash
  stranded the study scene.
- *DB:* the pre-seal scene snapshot lives beside the session record.

**Price: M–L**, permission-dependent per item — ship the subset that
proves on device, in the line's own order. **Kill gate:** any item
that cannot be *provably reversed* at receipt dies individually.

## K8 · THE WITNESS KEY — a seal a second person co-signs

**Mechanism.** Two-key launch, for the biggest seals only (spans over
a threshold he sets): sealing requires a hold on a second paired
device — a spare phone, or a trusted person's phone running a tiny
companion surface showing only `HE ASKS TO SEAL 3H — TURN YOUR KEY?`
and a hold button. No chat, no feed, no history on the witness side;
the witness learns a span and a moment, nothing else. Their hold + his
slide inside the same 10s window = t=0. Nobody available? The normal
single-key path still works and the imprint prints plainly
`SEALED ALONE`. Truth, not shame — the record says which kind of seal
it was, and nothing is scored, shared onward, or kept beyond the
receipt the session already issues.

**Replaces.** Nothing in the default path. An optional heavier lock
for the sessions he himself flags as the ones that matter most.

**Spec sketch.**
- *Inputs:* pairing record; witness hold signal; 10s co-sign window
  bound to the slide's press (queries-at-touch-down law holds — the
  co-sign is a query until latch).
- *States:* `AWAITING-WITNESS` inside proving only; timeout returns
  the Deck to idle with one plain sentence, no penalty recorded.
- *Outputs:* session record gains one bit: witnessed or alone.
- *DB:* one bit on the existing record family — flag for the cell to
  price against the standing DB version.

**Price: L** (pairing + companion surface + window sync) — the most
expensive idea in this set, priced so it can be refused cleanly.
**Kill gate:** pairing that takes more than one sitting to set up
kills it — a ritual needing IT support is not a ritual.

## K9 · THE CAPSULE LINE — one sentence thrown across the session

**Mechanism.** At the seal — typed *before* sliding, inside the
detention beat the audit already owns, costing t=0 nothing — the Deck
offers ONE empty line. He may throw one sentence across the session to
the only person guaranteed to read it: himself, at the receipt.
`Physics is shaky, be kind.` It is sealed with the session, shown
nowhere during it (not the running Deck, not the AOD — a message you
can peek at is just a note), and printed at the receipt's foot in the
receipt's own print voice, under the double rule where the imprint
tenant lives. Then it lives only if he keeps the receipt — the Drawer
already keeps receipts forever, unjudged, which is exactly the right
drawer for it. Empty is a complete answer: slide without writing and
nothing nags, ever. The slide never waits for typing.

**Replaces.** Nothing. It occupies a beat that already exists and a
foot that already prints.

**Spec sketch.**
- *Inputs:* one nullable string, captured pre-slide.
- *States:* none of its own; written at seal, read at receipt, nowhere
  else — enforce by giving the running/AOD surfaces no parameter that
  can carry it (P3's mode-typed payloads make this structural).
- *Outputs:* one line on the receipt.
- *DB:* one nullable field on the session record — flagged for the
  cell to price against the standing DB version.

**Price: S.** The cheapest idea in the set. **Kill gate:** if the
line ever renders anywhere before the receipt, the feature is cut,
not fixed — its whole value is the seal on it.

## K10 · THE POPULATED RIM — the rim is a room, and he is not the only one in it

**Mechanism.** His study groups already exist (YPT groups are his real
world). While setting, the tick ring carries a handful of soft marks:
other group members who are *sealed right now*. No names, no counts,
no durations, no leaderboard, nothing that survives the moment — a
mark exists while someone is inside a session and dissolves when they
finish. The library reading room, on a dial: at 05:00, in the dark,
three other dials on your rim are already turning. Sealing becomes
taking your seat, not entering solitary confinement.

**Wall-check, plainly, because this idea rots fastest:** PRESENCE
ONLY — binary, anonymous, ephemeral, unrecorded on his device beyond
the live frame. The moment anyone proposes counts, comparisons, or
history for it, that proposal is a different feature wearing this
one's coat and should be refused by name.

**Spec sketch.**
- *Inputs:* a presence backend (the only idea here needing a server):
  group membership + a live sealed/not-sealed bit per member.
- *States:* marks derived per frame from the live bit set; nothing
  persisted locally.
- *Outputs:* rim marks in existing tick ink, one weight up (F17
  tokens; no new colour).
- *DB:* none locally, by law of the idea.

**Price: L.** **Kill gate:** no backend, no feature — and a
simulated rim (marks that are not real people, live) would be the
worst lie this app could print. It ships real or never.

## K11 · THE PAPER DECK — a printed dial on the desk arms the phone

**Mechanism.** Print a physical Deck: a heavy card, the dial printed
on it, preset positions marked (his repertoire: MORNING / DEEP /
SLEEP), an NFC tag under each position. The morning ritual: phone
face-down ON the card, on the position. The tag arms that preset; he
picks the phone up already looking at a set Deck (priming, but the
prime lives in furniture, not software). The card IS the settings
page, and it cannot receive notifications. Printable from the app as a
deterministic vector sheet in the standing palette and fonts — no
generated imagery — or hand-made: any NFC tag + a handwritten label;
the app learns `tag → preset` in one teaching tap.

**Replaces.** Software priming's setup burden for the 2–3 presets that
make up nearly every real morning.

**Spec sketch.**
- *Inputs:* NFC tag reads (foreground dispatch); teaching tap flow.
- *States:* tag→preset map in a small standalone store.
- *Outputs:* a primed `DeckSet` payload, identical to a routine prime.
- *DB:* none (standalone store).

**Price: M.** **Kill gate:** NFC read must land in <500ms on his
device, or the ritual feels broken at the exact moment it must feel
instant.

## K12 · SIDE A / SIDE B — the session is a record, the break is flipping it

**Mechanism.** Aesthetic madness with a true mechanism inside: the
Fusion hub rendered as a GROOVE — progress (rim-ink's heir) as a
needle travelling a spiral; the width of groove left is the session
left, the oldest progress bar in music. A 2-block session is a record
with a side break: block 1 = Side A; the Safe Break's one job becomes
FLIP THE RECORD — during the break the Deck shows the disc's other
face (same truths, break print family: back-at time, nothing else),
and block 2 cannot start until the flip gesture (a two-finger turn of
the disc, big and satisfying) is made. Breaks die by drift; a record
you must flip cannot drift. The break gains a physical task that ends
it crisply.

**Replaces.** The break's passive countdown as the break's whole
interface; the elapsed/target bar's last abstract remnant.

**Spec sketch.**
- *Inputs:* session progress (elapsed/target, the audit's true
  formula — no floors, no invented denominators); break state.
- *States:* `SIDE-A` / `FLIP` (break) / `SIDE-B` (…C, D for more
  blocks — sides letter by block index).
- *Outputs:* spiral groove rendering on the existing ring geometry;
  flip gesture gate on break-end (the break's ruled length is
  untouched — the flip arms the next block, it does not shorten or
  extend the break).
- *DB:* none.

**Price: M.** **Kill gate:** Vega's truth charges apply whole —
needle position = elapsed/target exactly; FLOW has no groove (no
side, no end). If the spiral cannot be made exactly true, it is
decoration lying about time, and it dies. Scope note: a Deck
mechanism, not an app re-skin; the other looks' lanes are untouched.

## K13 · THE ENTROPY DIAL — an unsealed setting slowly forgets itself

**Mechanism.** Every Deck in every app holds your setting forever,
patiently, while you "decide" for twenty minutes with the phone in
your hand — the setting screen itself becomes the distraction.
Entropy: a span set on the wheel and left unsealed *decays* — after
90 idle seconds the wheel begins, almost imperceptibly, to unwind
toward zero (a detent every few seconds, knock pitch falling — K6's
scale running backwards). Touch the wheel and it stops, holds, yours
again. The message arrives by physics, not print: a session you will
not seal is a session you are not having. It manufactures the one
thing the idle Deck lacks — a gentle, honest reason to seal now, or
put the phone down and let it reach zero and go dark.

**Truth discipline.** Decay is never hidden: while it runs the
eyebrow prints `UNSEALED — LETTING GO` (F4's slot, plain words).
Nothing decays while a K5 floor is live, and nothing about a decayed
setting is recorded anywhere.

**Spec sketch.**
- *Inputs:* last-touch timestamp on the wheel; idle timer.
- *States:* `HELD` (touched within the window) → `DECAYING` →
  `ZERO-DARK` (hands off to the idle/dark rest state).
- *Outputs:* wheel position driven by the decay clock; any touch
  returns to `HELD` at the current position.
- *DB:* none.

**Price: S–M.** **Kill gate:** one device pass tunes the 90s; if it
ever decays mid-setting (hands returning inside the window), or no
number feels kind, the idea dies kind.

---

## RANKING — if only three are ever built

| Rank | Idea | Why first |
|---|---|---|
| 1 | **K9 Capsule Line** | S-priced, zero machinery risk; the only idea that makes a session feel addressed to someone — him, at the receipt. |
| 2 | **K1 Deck Eats The Home Screen** | The biggest structural leap available to a blocker: deletes the block-screen category instead of improving it, and rhymes with the launcher direction already ruled for the block surface. |
| 3 | **K5 Two-Selves Ratchet** | Solves the founding problem (two selves, one phone) with a mechanism, not a message. |

Next tier, unranked: K3 Ship's Bells (S–M, pure feel, no surface
cost) · K13 Entropy Dial (S–M, fixes the idle Deck's one dishonesty:
infinite patience) · K4 No-Numbers Deck (the strongest single-gesture
idea; needs the stranger test).

## DELIBERATELY NOT PROPOSED — refusals on record

- Streaks / XP / any reward economy — tracker rot, against the wall.
- Generated session art or motivational lines — banned materials, and
  slop besides.
- Names, feeds, or chat on the rim — K10 is presence-only or nothing.
- Anything that raises the STOP valve's price — the exit law is
  untouchable, in every idea above, by construction.
- A chatbot / assistant voice on the Deck — the Deck asks one question
  per state; a chat asks fifty.
- A second clock anywhere — the dial is the clock (Fusion); K3's bells
  are the same clock in another sense, not another time.

— Komet, crazy-ideas · Waves 1–4 posted as comments on issue #6
(6511647638 · 6511690080 · 6511719563 · 6511930739). DECLARED,
uncompiled, unbuilt — the cell prices, Rost rules, Ren merges.
