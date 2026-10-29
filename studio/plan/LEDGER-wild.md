# LEDGER — the WILD cohort verdict

*Filed by Marlowe, coordinator, WILD cohort — Plan Wave, Oct 29, 2026.*
*Question put by Sarthak: "run ledger through agents, im not sure if we should keep or not."*
*Status of this document: a recommendation. The decision is Sarthak's.*

---

## The verdict

**Option (b) — demote.** Kill the Ledger as a destination. Keep the record
itself — as machine data, surfaced only at the three moments a decision
needs it: the session-end receipt, the Shield loadout/routine editing
surfaces, and the emergency valve. No tab, no tower, no totals of minutes
anywhere in the app, ever.

The old Ledger deserved to die and stays dead: hero minutes printed twice
(dot digits + mirror numeral), a streak tower celebrating single digits,
7-day focus columns duplicating YPT, HMAC theatre on a homework receipt.
None of that is the question. The question the reopening actually turns on:

> YPT records the man's study. Nothing records the machine's work —
> sessions sealed, deflections absorbed, holds spent, routine attendance.
> That record exists only inside Monolith. Is it worth keeping, and if so,
> where does it live?

WILD's answer: the record is worth keeping because three real jobs run on
it — and worth demoting because none of those jobs is "go and look at it."

---

## What the record actually is (defined tightly, or it lies)

Four event classes, all Monolith-native, none visible to YPT or GMC:

1. **Seals.** A session was armed, sealed, and ended — planned duration,
   held duration, end state (completed / ended early). Counted by Uma's
   persisted sequence counter: incremented only when a completed session's
   receipt renders, so the sequence has no gaps and needs no excuses.
2. **Deflections.** Blocked-launch events logged by the shield service
   during a sealed interval: package/site, timestamp. Events inside a
   banked-break window are **not** deflections — he was allowed in.
   Repeated OS firings for the same package within 30 s collapse to one
   event. The record claims *what the shield logged*, never omniscience
   about his intentions.
3. **Hold spends.** The weekly emergency minute: banked progress
   (15/30/45 s checkpoints), seconds actually spent, the week it belongs
   to. This one is not optional under any option — the banked-hold law
   (60 s total, banks at quarters, 1/week) cannot be enforced truthfully
   without a persisted hold state. Even "kill entirely" keeps this
   counter; (c) only kills its visibility.
4. **Routine attendance.** A routine fired; a session was sealed inside
   its window, or none was. A routine must never record a session he
   wasn't in — but "fired, nobody came" is itself a fact, and it is the
   only honest feedback the rested user gets when editing his routines.

Explicitly **not** in the record: study minutes as a Monolith total (YPT
owns the day; a Monolith day total is an undercount dressed as a fact,
since he also studies outside the app), streaks, scores, any grade.

---

## Exactly what lives where, under (b)

### 1. The session-end receipt — Deck, ephemeral slip (Emil / Uma form)

Unchanged from the cohort's plan: one slip, issued once at session end,
dismissed in one tap, replaced by the next arm. It prints session-local
facts only — task or `NO TASK SET`, planned vs held (`HELD IN FULL` /
`ENDED 12 MIN EARLY`, same ink, no verdict colour), wall-clock window,
this session's deflection count (`DEFLECTED 7` / `NOTHING DEFLECTED`),
and, only if used, the hold spend with the banked remainder stated
(`BREAK USED · 38 SEC OF THE WEEKLY MINUTE SPENT`). Sequence `№` small
in the eyebrow (Uma). No orange. No day total, no cumulative count.

One change from Emil's draft, forced by honesty: his footer
`MONOLITH KEEPS NO COPY` becomes false the moment the event log persists.
Under (b) the footer reads:

> `THE MACHINE KEEPS ITS OWN LOG · YOUR STUDY RECORD LIVES IN YPT`

Anonymous Pro caps, lowest text tier, same position. The slip states what
is kept, in human language, at the exact moment he might wonder. That is
TRUTH-once applied to the record itself.

### 2. Shield — the record at the point of editing (rested-user surface)

Two placements, both inside surfaces that already exist, both serving the
person who is *changing* something:

- **The Verdict Line (Uma).** One fixed-height line at the top of Shield,
  under the header, printed on the ground — no card, no chip. One human
  sentence about the last completed session's deflection truth,
  overwritten wholesale by the next session. The Shield composable
  receives only a verdict enum and an end date, never the event list, so
  no total can leak onto this surface by a future well-meaning edit.
- **Loadout pressure, per source.** Inside Shield's blocked apps/sites
  editing area, each source may carry one small mono figure: deflections
  logged against it in the last 7 days. This is a *different fact* from
  the receipt's per-session count — a week's pattern per source is the
  evidence a loadout decision runs on ("Instagram tried 14 times, always
  early in the session" is a tuning fact; "deflected 7" is a session
  fact). One rendering each: the session count lives only on the slip;
  the 7-day per-source figure lives only here. No app-wide deflection
  total is computed for display anywhere.
- **Routine attendance, on the routine's own row.** Each routine row
  carries its recent truth in small marks: fired / joined / missed over
  its last firings (e.g. last 7 scheduled days). This is the feedback
  Joss's editing workflow needs and the antidote to ghost records — a
  routine that keeps firing into an empty room says so, on its own row,
  where the rested user can pause, retime, or delete it. Kato's 24-hour
  elevation keeps no past (his law stands for the elevation drawing);
  attendance lives on the rows in the list, not on the elevation.

### 3. The emergency valve — hold state at the point of spend (Rosa)

The valve UI states the week's hold truth where the spend is decided:
banked progress, seconds remaining, and — once spent — the fact and date
of the spend, in human language, until the week resets. This is
operational state, not history: no list of past weeks, no hold archive.

### 4. Chrome / AOD — nothing

No historical figure earns a place in the header, nav, or AOD. Emil's
imprint line stands as planned (state word, next routine, live shut
count — all present-tense operational facts). The record never appears on
a surface he glances at mid-session: HOLD absolute means the past cannot
tap him on the shoulder while he works.

### Data retention, stated plainly

The event log (seals, deflections, hold spends, routine fires) persists
on-device, in Monolith's own store. It is small, it is his, and it is
never rendered as a study record. If he ever wants it gone, "clear the
machine's log" is a Shield-level act that resets the sequence counter and
says what it destroyed before confirming — planned as a data act, not a
design feature of this wave.

---

## Why not (a) — the rebuilt tab, at its strongest

The strongest (a) is genuinely good: a single chronological machine tape
— one mono line per event, seals, deflections, hold spends, routine
fires — no minutes, no streaks, no charts, the flight-recorder of the
app. It would be the most honest screen Monolith has ever had, and the
only one about the machine rather than the man.

It still loses, for three reasons:

1. **Nothing starts there.** Every decision the record serves is taken
   somewhere else — dismiss the receipt (Deck), tune the loadout (Shield),
   edit a routine (Shield), spend the hold (valve). A destination whose
   only verb is *look* becomes one of two things within a month: a shrine
   he visits for reassurance, or a guilt wall of missed routines. Both
   are feelings about the past; the app's job is the next sealed session.
2. **A tab is a standing invitation to dashboard creep.** The old Ledger
   did not start as a tower and four duplicate totals; it grew there,
   one reasonable stat at a time, because a stats screen exists to be
   filled. A section inside Shield has a size budget enforced by its
   neighbours; a tab has a whole screen of empty space arguing for
   content.
3. **Cost on the nav.** A fifth destination re-opens the chrome plan
   (band-switch balance, badge pressure, header facts) to house a room
   he would open rarely. The record's jobs are episodic; its rent under
   (b) is three small placements. Under (a) it pays destination rent for
   episodic use.

## Why not (c) — kill entirely, at its strongest

The strongest (c) is Emil's purity: print the slip, keep no copy, let the
sequence number be the only permanence. It is clean, it is one decision
made once, and it makes the receipt's footer the truest sentence in the
app. It also has real force on deflections: the events are logged by the
shield anyway during the session, and there is something suspect about a
blocking app quietly building a permanent dossier of every temptation.

It loses on the jobs:

1. **Routine editing goes blind.** Without attendance, the rested user
   editing his 05:00 block has no idea whether it has fired into an
   empty room for nine straight days. He would be tuning the machine
   that runs his mornings from vibes. The routine law — never record a
   session he wasn't in — is about not *faking* attendance; (c) goes
   further and forbids *knowing* it.
2. **Loadout tuning loses its only evidence.** Per-source pressure is
   the difference between a blocklist built on what actually attacks
   him and one built on what he remembers attacking him. Memory is the
   worse instrument, and it is the tired user's memory at that.
3. **(c) is partly theatre anyway.** The hold counter must persist under
   every option or the banked-hold law breaks; the shield must log
   deflections during the session or the receipt can't print them.
   (c) therefore doesn't stop the machine recording — it only deletes
   the records at midnight and forbids him from ever seeing them. A
   purity rule that governs his access to his own machine's facts, not
   the facts' existence, is ideology priced in utility.

## The strongest case against this verdict — (b) itself

Stated as hard as the cohort can state it:

**(b) is the mushy middle, and it knows it.** It keeps the data, so
Emil's clean line — *the machine keeps no copy* — dies, and the receipt
must carry a longer, weaker footer in its place. It spreads one record
across three surfaces, so three surfaces must be kept consistent
forever, and every future debate about the record reopens in three
places instead of one. Worst: the Shield "pressure" figures are the old
Ledger's seed crystal. Today it is one mono number per blocked source;
the argument for a 7-day column next to it writes itself, then a total,
then a tab "for convenience" — and the tower regrows from a section,
exactly as it grew the first time. (b)'s defence against that future is
not architecture, it is discipline: the verdict-enum-only Verdict Line,
the refusal to compute any app-wide total, the per-fact single home.
Discipline has to be re-chosen by every future editor of this app;
(c)'s deletion only had to be chosen once. And on the strictest reading
of one-fact-one-rendering, the per-source 7-day figure is a second
rendering of deflection events the receipt already rendered — if Sarthak
reads that law strictly, (b) collapses honestly to receipt + valve state
+ attendance marks, with the Shield pressure figures cut.

WILD files (b) anyway: the jobs are real, the placements are small, and
the regrowth risk is named here, in the open, where a future editor has
to argue with it in writing.

---

## Where the main WILD plan depends on this answer

Flagged for the WILD-PLAN synthesis (Ledger treated as OPEN there):

- **Nav / chrome.** (a) adds a fifth destination and reopens the
  band-switch and badge plan. (b)/(c): nav as planned. Emil's imprint
  line is unaffected under all three.
- **Receipt.** Exists under all three options (even (c) keeps the
  ephemeral slip; (c) kills retention, not the slip). Its **footer**
  depends on the answer: (c) → `MONOLITH KEEPS NO COPY`; (a)/(b) → the
  machine-log line above. Uma's **sequence number** survives all three.
- **Uma's single-slot retention** (`lastReceipt` overwritten by the next
  slip) survives under all three for the *rendered slip*. Under (a)/(b)
  the *event log beneath it* persists; her "permanence by sequence, not
  by storage" becomes "sequence on the slip, storage underneath."
- **Shield scroll budget.** (b) spends: one verdict line at the top,
  small per-source figures inside the loadout area, attendance marks on
  routine rows. (a) spends nothing on Shield (the record moves to the
  tab) but pays the nav cost instead. (c) spends nothing anywhere.
- **Kato's elevation.** "Keeps no past" holds for the elevation drawing
  under all three; under (b) attendance marks live on the list rows
  below it, not on the drawing.
- **Rosa's valve.** Hold-state persistence is required under all three;
  a *visible* statement of the spend at the valve is planned under
  (a)/(b) and is the minimum truthful form under (c) as well (the valve
  must still say the minute is spent — that is present-tense state, not
  history).
- **Data layer.** (a)/(b) require the four-class event log as planned
  data. (c) requires only the hold counter, the sequence counter, and
  session-local deflection counting that is discarded at session end.

*Process note: the reopening reached Rosa, Opal, Aldo, and Suki while
their seat work was in flight, and the shared brief was amended for any
future seat. The finished seats' record material — Emil's receipt, Uma's
verdict line and event definitions, Kato's elevation law — was written
under the kill and is used above as the atoms of this verdict.*

## Addendum — the in-flight seats have now filed (05:52 IST)

The verdict sections arrived, and they converge on (b) independently:

- **Opal — (b), THE SERVICE LOG.** One sheet (Shield footer row +
  nameplate long-press; both doors inert mid-session) fed by one
  append-only event store: three tally drums (SESSIONS SEALED /
  DEFLECTIONS ABSORBED / HOLDS SPENT — derived from the store, so drum
  and roll can never disagree) over an event roll of exactly nine event
  types, sequential event numbers as provenance, no crypto theatre.
  Her case against her own (b) is the sharpest filed: *a sheet is where
  facts go to be forgotten; Monolith has no technician, only a tired
  student at 03:30 — if the record will genuinely be used, the honest
  vote is (a).* Her (b) stands only on rare, specific use: diagnosis,
  hold accounting, attendance truth. **Coordinator note:** the Service
  Log is adopted as the fullest form of this verdict's Shield placement
  — if (b) is chosen, the record's kept form is Opal's sheet; the
  Verdict Line (§2) remains the at-a-glance surface, the sheet is what
  it opens onto. Her tally drums are the one point of friction with
  §2's "no app-wide total computed for display anywhere": the drums are
  totals, kept behind a door, derived from the store. The tension is
  recorded, not resolved — it is Sarthak's to settle with the option.
- **Aldo — (b), THE SLIP.** A typeset sheet with two doors (auto-issued
  at session end; a `RECORD` text action at Shield's foot, inert while
  sealed). His governing rule improves this verdict and is adopted:
  **custody transfer** — facts move from live surfaces to the record at
  session end, so no fact ever sits in two places at once. Rows: seals
  with running `№`, deflections attached to their session's seal number,
  `HOLD SPENT` only in weeks it happened, routine attendance as `FIRED`
  / `MISSED — NO SEAL`. His case against: *a sheet nobody opens is the
  Ledger on life support; if the cohort's use-cases are all
  retrospective curiosity rather than decisions, (c) beats (b) and I
  said I'd sign the kill.*
- **Suki — leans (b), against (c) on audit grounds.** Killing the record
  makes every coverage failure unprovable after the fact, so READY
  becomes an unauditable assertion; against (a) because a tab invites
  daily visiting and a record becomes a score. Her floor is adopted in
  the main plan: the intercept log, gap record, and timestamps are
  written during the session under **every** outcome — "kill" can only
  mean killing the kept, browsable form. And her chrome ruling stands in
  both documents: no historical total earns a chrome corner under any
  outcome — a cumulative count in chrome is the streak tower rebuilt at
  10 sp.
- **Rosa — flags, consistent with (b).** The spent-hold record is
  written to the store at the completion frame under all three options;
  only its display home varies. Named truth risk: kill-the-Ledger with
  no receipt surface = a hold enforced next week with no visible
  provenance — the one dishonest configuration. (c) stays honest only
  if the receipt survives as a surface. It does, in every plan on the
  table.

The verdict stands as filed: **(b)** — now the position of the
coordinator and every seat that examined the question, with the
strongest dissent (Opal's "honest vote would be (a)") preserved in
writing above. The decision remains Sarthak's.

— Marlowe, WILD coordinator (verdict filed 05:21 IST; addendum 05:52 IST)
