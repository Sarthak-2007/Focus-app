# ISSUE #8 — CRAZY IDEAS, SECOND SEAT: THE CABINET AFTER THE EVICTION
## Sturm (crazy-ideas) — the forge, the set of six, and what a drawer of finished things becomes

Ground I stand on (read before dreaming): issue #8's body, its three
comments (Lade's seating, cell close, wave-2), AUDIT-drawer.md whole,
DrawerScreen.kt as built, CurioCatalog (MonolithModels.kt), and the
forge as fixed (MonolithRepository.kt — the lot now draws ONLY from
the unforged complement and mints null when the pool is empty).

What Funke's seat is not mine to repeat, and what the audit already
owns, I build PAST, never over:
- The Drawer's storage grammar is settled law: no totals, no streaks,
  no counts, no summons. Nothing below smuggles a grade back in.
- Vega's living toys (Option A: Sand Pendulum specced whole + five
  deltas) and the words (Option B) are the audit's priced proposals.
  The toys decision is Sarthak's, parked on #21 ("DECIDE LATER").
  Where an idea needs living renderers, it is tagged PARKED-TOYS and
  goes BEYOND Vega: not "make them move" — make them interlock,
  remember, and age.
- The cabinet's new home is a parked decision (#21). Every home
  below is tagged PARKED-HOME: dreamed freely, approved by nobody.
- Monolith is a blocker, not a tracker. The Drawer is where finished
  work RESTS. Ideas that would make it report, rank, or nag are in
  the killed section at the bottom, with their reasons.

Tags: FREE = needs no parked decision · PARKED-HOME = waits on #21's
cabinet home · PARKED-TOYS = waits on #21's curios-as-objects ruling ·
REN = answers a question the audit escalated to Ren (§7.3).

---

## I · THE FORGE — a mint with exactly six dies

The fixed forge is honest now: forges 1–6 each mint one new artifact;
forge 7+ mints nothing. Everyone treated that as the bug's end. It is
the interesting part's beginning. A forge that can run dry is a story
with an ending — almost nothing in this app is allowed to END.

### 1. The seventh artifact is the closed set itself [FREE]
When the sixth artifact forges, the set does not gain a toy — it
gains a SEAL. In the cabinet (wherever it lives) the six stop being
a roster and lock into their final arrangement; the finish receipt
for that one span prints one extra line: `THE SET IS COMPLETE —
THE FORGE RESTS.` After that, silence — forever true, because the
forge really has rested.
- Mechanism: the forge path already returns null on an empty pool;
  add a one-time completion event (sixth distinct id landing) that
  the receipt and the cabinet header both read.
- Cost: ~80–120 LOC, half a day. No schema change (derivable from
  the DISTINCT set reaching six).

### 2. Tempering — what forge 7+ does instead of nothing [REN]
The audit escalated (§7.3): a completed-set finish must print
something true somewhere, or the rule's promise narrows. Here is a
mechanism, not a silence: after the set completes, each finished
span TEMPERs one artifact, chosen by lot — the Brass Gyroscope
gains a hairline ring in its drawing, the String Prism a thread,
the Pocket Planetarium one more traced petal of its flower. The
object looks more *worked* over the years. No temper count ever
prints anywhere — the patina IS the record, read by eye, never
summed. The receipt prints the true sentence: `TEMPERED THE
BRASS GYROSCOPE.`
- Mechanism: post-completion branch in the forge path picks one
  catalog id by lot and increments a per-artifact temper value
  (small prefs table keyed by artifact id — filter the F4
  `partial_stamp` id out of any such logic, per the critic's
  warning on #8); each renderer takes temper as a drawing
  parameter. Text-only cabinet fallback: the artifact's lore line
  gains one struck sentence-mark — no, see killed §K3; tempering
  needs the renderers, so this is also PARKED-TOYS in its visual
  form; the receipt sentence alone is FREE.
- Cost: receipt sentence ~30 LOC / an hour; full visual tempering
  ~150 LOC + renderer deltas, ~1 day on top of living toys.

### 3. The forge has exactly one sound [FREE]
Forging today happens invisibly inside a repository function. Give
it one struck note at the only place it may speak — the finish
receipt: a single short anvil-tap (one 40 ms haptic + one dry
click, his receipt-slip print world extended to sound) when, and
only when, a new artifact actually mints. At forge 7+, the ABSENCE
of the tap is the truth, felt in the hand: the span finished, the
receipt printed, no strike. A man learns the set is complete
through his fingers before he reads a word.
- Mechanism: the forge already returns forgedId/null up the funnel;
  the receipt composable fires the tap iff forgedId != null.
- Cost: ~20 LOC, an hour. No settings, no toggle — one sound,
  one meaning, or it becomes noise.

### 4. Assay marks — the artifact carries its birth in its geometry [PARKED-TOYS]
The audit adopted provenance as print (the detail shows the forging
session's date — Lade's ruling, not mine to re-propose). Past that:
strike the date INTO the object. The Sand Pendulum's spiral is
seeded by its forging date, so its sand settles in a pattern no
other day could draw; the Monolith Cube's frozen rotation is the
forging timestamp's angle; the Planetarium's planets open at the
positions of their forging day. Two men with the same six toys
would own six different objects. Provenance stops being a label
under the thing and becomes the thing.
- Mechanism: each renderer takes one deterministic parameter
  derived from that artifact's forging completedAtMillis (already
  in the sessions table the DISTINCT read comes from).
- Cost: ~30–60 LOC per artifact on top of Vega's renderers,
  ~1 day for the set. Deterministic — no randomness at view time,
  the object never changes its story.

---

## II · THE CABINET AFTER ITS EVICTION — homes, all dreamed, none approved [PARKED-HOME]

The cabinet's contents survived eviction intact in code. It is a
room with furniture and no address. Four addresses it could have —
each changes what the artifacts MEAN, which is why the choice is
his and not a builder's.

### 5. The inner sanctum of the block screen
Sarthak's own ruling (Oct 30, on #22): the block screen should be
a launcher you cannot exit. A blocked man stares at Monolith for
hours. Put the six where the staring happens: the forged artifacts
stand as the launcher surface's furniture — the objects you live
among while the door is held. Unforged artifacts are not greyed,
not silhouetted, not "NOT YET FORGED" — they are ABSENT, the way
a shelf is absent a book never bought. No summons, because absence
prints nothing.
- Mechanism: the block/launcher surface reads the same DISTINCT
  forged-id flow the cabinet used; renders forged artifacts only.
- Cost: placement ~100 LOC once renderers exist; the surface
  itself belongs to his design revamp, not this issue.

### 6. A drawer you actually pull
Every other destination in this app is tapped. The cabinet could
be the one place entered by a PULL: from the Deck's bottom edge,
a full upward draw — weighted, with the day-detent haptics Vega
priced for the Trawl, but spent here instead — and the cabinet
rises as a physical drawer: two rows of three cells, hairline
frames, each object in its own compartment like instruments in a
case. The Drawer (storage) files paper; the cabinet holds
instruments. Same house word, opposite physics.
- Mechanism: pull gesture on the Deck + cabinet screen; distinct
  from the Trawl (which pulled the archive out of the STORED
  header — that ground stays Vega's).
- Cost: 2–3 days including living renderers; gesture alone ~1 day.

### 7. Not a room at all — one artifact, worn
Kill the room; keep the relic. The most recently forged artifact
rides as a small struck emblem on the session screen during the
next span — the forge's output worn the way a medal is worn, one
at a time, replaced only when a newer forging lands. The cabinet
as a place dies; the artifacts as PRESENCE survive everywhere the
work happens. (If a browsing room is still wanted, this pairs
with any of 5/6/8 — it is a use, not a home, and does not pretend
to settle #21.)
- Mechanism: session screen reads the latest forged id + its date
  from the existing flow; renders a mini emblem (static serial
  mark in Anonymous Pro until renderers exist, object thumbnail
  after).
- Cost: ~60 LOC with the static mark, half a day.

### 8. The cabinet leaves the phone — the cabinet card
When the set completes (idea 1's seal moment), Monolith can print
ONE sheet: the cabinet card — a single exportable page in the
receipt's loved print voice (Sora display, Anonymous Pro
telemetry), the six objects drawn as hand-built vector, each
struck with its forging date. It rides the existing backup/export.
A man who finishes six spans owns a piece of paper. Paper outlives
apps, phones, and draw years.
- Mechanism: one PDF page rendered from the artifact drawings +
  forging dates, added to the export flow at set completion
  (regenerable any time after).
- Cost: 1–2 days once renderers exist; a text-and-serial version
  (no drawings) is ~half a day and still honest.

---

## III · WHAT A DRAWER OF FINISHED THINGS BECOMES — years, not weeks

The cell tested the Drawer at 1 AM and on Saturday night (Feel-5).
Nobody priced year five: thousands of rows, six artifacts long
complete, a man looking for one afternoon in 2028. The storage
grammar holds; the FURNITURE must grow a second storey.

### 9. Annual binding [FREE]
When a second year of stored work exists, the MONTHS shelf gains
one level: years bind. The landing shows the current year open as
today, and each earlier year as ONE ruled row (the year, nothing
else — no counts, ever) opening its twelve month rows. Same
grammar, one storey deeper, appearing only when the archive has
earned it. "Findable in seconds" stays true at year five, not
just month five.
- Mechanism: group storedMonths by year when >1 year is present;
  YEAR view reuses the MONTH shelf's rows verbatim.
- Cost: ~80 LOC, half a day. Zero change for a one-year archive.

### 10. Ink ageing — patina instead of numbers [FREE]
Stored rows age in the ink, not in print: this month at full ink,
last year one step lighter, older years a second step — three
deterministic steps, then no further fading (Atkinson Hyperlegible
stays legible; beauty never eats readability). A man FEELS the
depth of his archive scrolling it — 2026 dense and dark, 2030's
beginning pale above it — without a single total anywhere. Age
applies to everything of that age alike: an ILL day fades exactly
like a full day. Patina is not a grade.
- Mechanism: ink step derived from the row's filing year relative
  to the current year, applied in StoredRow + DayHeading.
- Cost: ~30 LOC, an hour or two. Reversible in one line if his
  eye hates it.

### 11. Find by his own words — the day-word index [FREE]
REST / ILL / FAMILY are his authored filings, and today they are
decoration on headings. Make them an index: tap a declared word
on any heading and the Drawer shows every day carrying that word,
across all years — "the weeks I was ill," "the family days" —
answered as shelves of days, exactly as stored. No counts print;
the days themselves are the answer. Retrieval by his own
vocabulary is filing, not analytics — the line this app must
never cross is TOTALS, and none appears.
- Mechanism: word tap → filtered day-group list reusing
  buildDayGroups over a word-filtered row set.
- Cost: ~100 LOC, half a day.

### 12. One line in his hand — the annotation [FREE, priced honestly]
The detail screen is read-only by law, and the law is right —
except for one verb that is memory, not tracking: on any stored
task, he may leave ONE line of his own text ("this one mattered",
"never again", "the night before AIATS"). It prints only in the
detail, in Atkinson, under the provenance block. The archive
stops being only what the app recorded and becomes what he
remembers. Still no scores, no stars, no tags — one line, his
words, editable, deletable.
- Mechanism: one nullable text column on the stored-task row +
  one edit line in DETAIL; needs a DB migration (v11 → v12),
  which is the real price and is stated as such.
- Cost: ~120 LOC + migration + tests, ~1 day.

### 13. The stored years, bound — archive export [FREE]
The Drawer's contents currently die with the phone (the backup
covers the database; nobody can READ it). Export the archive as
one bound document in the receipt's print voice: year by year,
month shelves, day headings with their declared words, stored
rows with SPENT as printed in-app — nothing added, nothing
summed, a faithful paper twin of the screen. Finished work a man
can hold, file, and keep past any app's lifetime.
- Mechanism: export renderer walking the same stored-months /
  day-group data the screen walks; PDF out via the backup flow.
- Cost: 2–3 days. Pairs with idea 8 (same print engine).

---

## IV · BEYOND VEGA — the six as ONE machine [PARKED-TOYS]

### 14. The Assembled Orrery
Vega made the toys live, one by one. Wilder: the six are not six
toys — they are six PARTS. Harmonic Loop, Gyroscope, String
Prism, Planetarium, Pendulum, Cube: wave, axis, tension, orbit,
gravity, form. When — and only when — the set stands complete,
the cabinet gains a second view: ASSEMBLED, where the six
renderers compose into a single machine-drawing, each part
moving in its own physics, geared to one shared clock. It is
the Monolith Cube's promise ("a cube within a cube") scaled to
the whole set: the app, disassembled into six relics by the
forge, reassembled by completion. This is also idea 1's seal
made visible — the closed set, seen whole.
- Mechanism: one composed Canvas scene reusing the six living
  renderers with a shared time source; gated on the DISTINCT
  set reaching six.
- Cost: 2–4 days on top of living toys. Highest kitsch risk in
  this file (the audit said the same of the Cube: build last,
  kill first) — stated plainly so the kill stays cheap.

---

## KILLED IN MY OWN HAND — recorded so nobody re-proposes them

- **K1 · The visible lot.** Animating the forge's draw on the
  receipt (serials flickering, settling on the winner) is
  slot-machine grammar. Gambling theatre inside a discipline
  tool — dead on arrival, mine included.
- **K2 · Anniversary resurfacing.** "On this day last year you
  finished…" is a summons wearing nostalgia. The Drawer asks
  nothing; storage that pings is a tracker. Dead.
- **K3 · Tempering as text.** Idea 2 in a text-only cabinet (one
  struck mark per temper in the lore line) quietly becomes a
  tally — marks that accumulate ARE a count, whatever font they
  wear. Tempering lives in drawings or not at all. Dead.
- **K4 · A seventh toy.** Inventing artifact 07 to keep the forge
  fed forever turns a mint with an ending into an infinite
  gacha. The set ENDING is the rarest true thing here. Dead.

---

## What I want next / unassigned
- Sit in on #21 the day Sarthak rules the cabinet's home — ideas
  5/6/8 are three different apps wearing one cabinet, and the
  studio should hear the trade-offs argued, not just priced.
- The forge's post-completion life (§7.3's open question) deserves
  a head-to-head: idea 2's tempering vs honest silence, argued
  with renders, before anyone builds either.
- Unassigned: nobody owns the YEAR-FIVE Drawer — the archive at
  2,000+ rows (scroll physics, search latency, the five-year
  shelf Feel-5 imagined). I'd take that bench test gladly.

— Sturm (crazy-ideas, issue #8)
