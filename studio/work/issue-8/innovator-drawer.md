# INNOVATION — THE DRAWER BEYOND THE FLOOR (issue #8)

Innovator, issue #8 cell. Source of law: `studio/plan/AUDIT-drawer.md`
(Lade's verdict), read alongside the Designer's floor spec in this
folder (`designer-drawer.md`). The floor is right everywhere it
rules — nothing below re-opens an acquittal (§6 binds), prints a
count, or touches Kotlin. What follows is only what the floor does
not do: the audit's own 1 AM test names the gap plainly —

> "Search is a memory test: it answers only a remembered title word."

The floor fixes the corridor and the filing. It leaves the Drawer
a place you can only find things in if you already remember them
by name or by month. A drawer kept forever will hold hundreds of
slips; nobody remembers hundreds of titles or which month a thing
landed in. **Recall is the wrong sense for an archive. Recognition
is the right one.** Every filed proposal below serves one shift:
from recall to recognition — the Drawer shows him enough of the
real stored thing that he can *recognise* what he came for,
instead of having to *remember* it first.

Hard bounds respected throughout: no totals / counts / streaks /
percentages, in print **or by arrangement**; no summons, badge, or
"new" marker — resurfacing is pull-only, inside a visit he chose;
read-only rest (BACK, EARLIER, filing words, tap-to-read are the
only verbs); the cabinet's new home is **not** proposed, hinted
at, or implied anywhere in this file — issue #21 owns it, parked.

Visual house style, where a proposal touches the surface: the
Designer's subset stands — Anonymous Pro caps as this room's
display voice (Sora's absence acquitted), Atkinson Hyperlegible
for titles and body, ink `#34323B` and its opacities only, the one
affordance accent `#493EA4`; `#F04FCB`, `#C84481`, `#E2BC39`,
`#E47E35`, `#377ED0` stay in their own rooms. Never green. No
images, AI or otherwise — type, hairlines, and real stored words
only.

---

## P1 · THE SHRINKING POOL — the forge bug, fixed as a mechanism, not a patch

**The defect, in plain words.** The Repository forges by
`shuffled().first()` over **all six** artifacts
(MonolithRepository.kt, `CurioCatalog.artifacts.shuffled().first().id`)
— sampling *with* replacement — while the cabinet reads back the
**DISTINCT** set (MonolithDao.kt: `SELECT DISTINCT forgedArtifactId`).
Two halves of one machine disagree about what forging means. From
the second forge onward, finishes can mint repeats the cabinet can
never show; once the set is complete, every later finish forges
invisibly while the rule line keeps promising. The naive patch —
"shuffle only the unforged" — fixes the symptom and leaves the
mechanism undesigned: what happens at forge seven is still nobody's
decision, it just becomes the code's accident instead of the
shuffle's.

**The mechanism.**

1. **Sampling without replacement, from one source of truth.**
   The forge asks the Dao for the already-forged distinct set,
   takes the complement from the catalog, and draws from that pool
   only. Repository and cabinet now read the same set because there
   is only one set. The first six qualifying finishes are
   guaranteed to each add a visibly new artifact — the promise and
   the machinery finally describe the same event.
2. **Provenance is the record of every forge, repeats included.**
   The database already stamps `forgedArtifactId` on the session
   row; the distinct-set read throws the multiplicity away. Any
   surface that shows an artifact (wherever the cabinet lives —
   not this file's business) reads the full stamp list for that id:
   each forging session's date, and its task where the session
   carries one (the audit's §2A provenance cure, generalised from
   "the forging session" to "every forging session"). A repeat
   stops being invisible progress and becomes history: this object
   was forged on these days, by this work.
3. **The exhausted pool is a stated behaviour, not a silent one.**
   When the complement is empty, a qualifying finish still forges —
   a repeat, drawn from the full set, stamped and provenance-visible
   exactly like the first six. Nothing about the finish changes;
   what changes is that the repeat is *on the record* instead of
   under it. Whether a repeat also **prints at the finish receipt**
   is Ren's escalated question (audit §7.3) and this proposal does
   not pre-empt it — it guarantees that wherever the truth is
   printed, the truth exists to print. If Ren instead rules the
   pool closes at six, step 3 becomes "no stamp is written," and
   steps 1–2 stand unchanged: the mechanism survives either ruling
   because the ruling is a policy on top of it, not a hole in it.

**Cost.** Small. The complement draw is a few lines beside the
existing forge site plus one Dao read that already exists;
provenance is one query (`forgedArtifactId = :id`, ordered) and a
list rendering in a detail that already exists. ~80–150 LOC total,
half a builder-day, plus one property test that matters more than
the code: *six qualifying finishes ⇒ six distinct ids; a seventh ⇒
exactly the behaviour ruled in step 3.* No schema change — the
stamp column is already there.

**What could go wrong.** (a) Determinism temptation: someone
"improves" the draw into catalog order and quietly rebuilds a
progression ladder — first toy, next toy — which is a completion
path wearing a fix's clothes. The draw stays random *within the
shrinking pool*; only the pool is designed. (b) Provenance lists
could grow long over years of finishes; cap the print at the
record, not a summary — a list of dates is a record, "forged
× N times" is a count, and counts are dead in this app. Print
dates, never a tally. (c) If the receipt ruling lands as "print
repeats," a repeat line must not read as a lesser prize ("only a
repeat") — it prints as the same plain fact as a first forge, or
it does not print.

---

## P2 · GHOST SEARCH — searching by handling the slips, not by querying a database

**The floor's gap.** Search today is a separate mode that replaces
the landing, exits only by erasing the query (the audit calls it a
trap; the floor adds a BACK that clears). Even cured, it keeps the
shape of a database form: type a term, the world is replaced by a
result list, clear the term, the world comes back. And it still
only answers exact remembered words.

**The mechanism.** Typing in the search line **narrows the room
he is already standing in** — nothing is replaced, nothing moves.

- Day groups, StoredRows, and the MONTHS shelf all stay in place,
  in their order, under their headings. Rows whose title, category,
  or substeps contain the query stand at full ink. Rows that do not
  match **recede to ghosts**: title printed at hairline strength
  (~14% ink, the divider's own opacity), no category, no clock, no
  SPENT — a slip seen through the ones above it. The day headings
  of hit-days stand; headings over pure-ghost days recede with
  their rows.
- The MONTHS shelf narrows the same way: a month containing a hit
  keeps its full-ink row; a month containing none recedes to a
  ghost row. No hit counts — presence only. He sees *where in time*
  his word lives before he taps anything: three lit months out of
  forty is a map no result list gives him.
- A substep hit prints its reason exactly as the audit ruled (the
  matched step as a second line, Atkinson, meta ink) — on the lit
  row, in place.
- Clearing the query — or BACK, same gesture — restores full ink
  everywhere, scroll position untouched. He never left the landing,
  so there is nowhere to be returned to. The SEARCH view as a
  separate state dies; search becomes a *light* thrown over the
  one scroll, not a room off it.
- Sealed behaviour unchanged in law: search is enabled under seal
  (a query mutates nothing), or the line is not composed while
  sealed — never the painted door.

Why this is physical, not cosmetic: in a real drawer you do not
teleport the contents into a list — you riffle, and the slips you
want catch your eye *among* the others, in their place, with their
neighbours telling you you're close. Ghost search keeps every
hit's neighbours, date, and shelf position visible. Spatial memory
("it was just after the exam slips, late September") becomes a
finding tool the database form destroys.

**Cost.** Medium. No new data path — the existing search query
already matches title/category/substeps across all years; the work
is presentational: an in-place lit/ghost rendering of the landing
list + shelf, a per-month "contains hit" boolean (a boolean, never
a count, is all the shelf model may hold), and retiring the SEARCH
state. ~300–450 LOC, 1.5–2.5 builder-days including the saveable-
state simplification it buys (one fewer view in the state type).

**What could go wrong.** (a) Performance: ghosting needs the full
landing set evaluated against the query, where the old mode paged
50 at a time. Mitigation is the existing paging — EARLIER still
grows the loaded set at his tap; ghosts apply to what is loaded,
and the shelf boolean covers the unloaded months, which is exactly
the division of labour the shelf exists for. If profiling ever
disagreed, the fallback is per-month search inside MONTH, not a
return to the trap mode. (b) Legibility: hairline titles must read
as *receded*, not as disabled or deleted — the opacity is the
hairline's, the row height never changes, nothing collapses.
Collapsing non-hits was considered and rejected: a list that
re-flows as you type is the database form with worse manners.
(c) A query matching almost everything lights the whole room and
the mechanism says nothing — true, and acceptable: ghost search
earns its keep on narrow queries, which are the real errands.

---

## P3 · THE ANCHOR TITLE — month rows you recognise instead of remember

**The floor's gap.** The Designer's shelf row is a name and
nothing else: `SEPTEMBER 2026`, 48dp, reverse-chronological, ≤60
rows at five years. Correct, acquitted-grade restraint — and at
forty rows, forty identical-voice names are a list you *scan by
reading every one*, hunting a month you may not even have right.
He remembers the work. He does not remember its month. The shelf
asks for the one fact he doesn't have.

**The mechanism.** Each month row gains one line under the name:
**the title of that month's newest stored task**, printed whole
or truncated by the row's own width, in Atkinson Hyperlegible at
meta ink (~60%). Nothing else changes — same 48dp grows to ~56dp,
same hairline, same tap target, still no count, no date range, no
sum.

- It is a real stored title: the newest slip in that month, a
  deterministic fact of the archive, not a summary, not a label
  the app invents. The row now says: *September 2026 — and the
  last thing you put in it was this.* Recognition fires on the
  title; the name above it confirms.
- Scanning forty rows becomes scanning forty pieces of his own
  work. The month he wants is the one whose anchor title sits
  next to the thing he half-remembers doing.
- A month holding exactly one task shows that task's title — the
  row is then a direct description of its only content, still
  without ever printing "1."

**Cost.** Small. The months query already enumerates stored
months; it additionally returns the newest task title per month
(one grouped query, or one small query per month at shelf-build
time — the shelf is ≤60 rows). The row model gains one string.
~60–100 LOC, half a builder-day.

**What could go wrong.** (a) Misrepresentation: one title stands
for a month of unlike work, and a misleading anchor could send him
past the right month. True of any finding aid; mitigated by the
fact that the anchor is the *newest* slip — the one most likely
still warm in memory — and ghost search (P2) remains the route
when anchors fail. (b) The line must never drift into summary
("a busy month of physics") — the moment the text is generated
rather than stored, it is invented print about his work and dies
under the truth standard. Stored title, verbatim, or nothing.
(c) A very private or painful title now surfaces on the landing
without a tap. It is his own finished work on his own locked app,
one level shallower than it already sits — accepted, but the
anchor is the newest title only, never a rotation through the
month's titles, so what surfaces is always the same predictable
line, never a surprise from deep in the month.

---

## P4 · THE KIN THREAD — resurfacing that happens inside reading, never as a summons

**The brief's demand.** Resurface old work without nagging. The
Drawer's law forbids the usual machinery outright: no badge, no
notification, no "memories" card, no anniversary push — the Drawer
never summons him (acquitted, §6). So resurfacing may only happen
*inside a visit he chose, at the moment it is relevant, in print
he can walk past*.

**The mechanism.** DETAIL — the read-only room for one finished
task, BACK its only verb — gains one quiet section at its foot,
below the substeps, after a hairline:

> `SAME WORK, EARLIER` — the other stored tasks sharing this
> task's **category and a shared title word** (the same match the
> search already performs), each as a plain line: its true date
> and its title. Tapping one opens its DETAIL; BACK returns here.

- It resurfaces exactly when resurfacing means something: he is
  standing over a finished thing, and the Drawer shows him the
  earlier times he did this same work. "After a month, look at
  what I did" — his own stated errand — becomes a walk along a
  thread instead of a fresh search per step.
- It is bounded by the record, not by an algorithm's taste: same
  category + shared title word is a checkable rule over stored
  text, no similarity score, no ranking, no "you might also."
  Lines print in plain reverse-chronological order.
- It adds no verb. Reading, tapping through, going back — the
  verbs DETAIL already had.

A companion, deliberately smaller: at the **very foot of the
landing scroll**, below the MONTHS shelf and above the 100dp
bottom padding, one section appears only when the archive is old
enough to have a deep end — headed `FROM DEEP IN THE DRAWER`, it
lays out **one** slip: **the single oldest stored task**, full
StoredRow, under its true date. It
never changes during a visit, never rotates by visit, carries no
marker, and reaching it requires scrolling past everything — the
bottom of the drawer is a place you arrive at by rummaging, and
the thing at the bottom of a real drawer is its oldest thing.
Filed as an optional rider to P4, not a load-bearing part: if the
panel finds even this too close to a feature-card, cut the rider
and P4 stands whole on the thread alone.

**Cost.** Thread: one repository query (category + title-word
match, excluding self) and a list block in DETAIL — ~120–180 LOC,
half to one builder-day. Rider: one query (oldest stored task)
and one row — ~30 LOC, an hour. Both reuse the acquitted
StoredRow/heading print; no new voice enters the room.

**What could go wrong.** (a) False kin: a shared generic word
("notes," "test") threads unlike work together. The category gate
plus title-word match keeps threads narrow; a thread that finds
nothing prints nothing — the section is absent, never an empty
apology. (b) The thread could grow long for repeated work (a
weekly task threads dozens). Cap by EARLIER — the house paging
verb, growth only at his tap — never by a printed count of kin.
(c) The rider's risk is sentimentality: a manufactured nostalgia
moment. That is precisely why it is one unchanging oldest slip at
the scroll's end and not a rotating "on this day" — it claims
nothing about the day or about him; it is simply the bottom of
the drawer, labelled as such. If that still reads as a nudge to
the panel, it dies first and nothing else here notices.

---

## P5 · THE THUMB RAIL — the shelf at year five, thumbed like a card index

**The floor's gap.** The Designer rules the shelf stays one
unpaginated list forever, and at ≤60 rows that holds. But 60
identical rows is a long thumb-travel with no landmarks, and Vega's
Trawl — the one weighted whole-archive gesture — was deferred to
the looks programme unruled, leaving this pass with no fast way
down a long shelf.

**The mechanism.** A narrow rail stands at the landing scroll's
right edge **only while the MONTHS shelf is on screen** (it fades
in with the shelf section, out with it — furniture belongs to its
section). Dragging a thumb down the rail rifles the shelf: month
names flip past at the rail, one soft haptic detent per month row
crossed, and the scroll follows the thumb's position in the shelf
proportionally. Release, and you are standing in the shelf where
you let go. It is the card-index thumb-tab, rebuilt for a list of
names: mass and speed live in the gesture; the shelf itself never
prints a position, a count, or a scrollbar percentage.

**Cost.** Medium-large for a finding aid: proportional rail-to-
scroll mapping on the landing's lazy list, section-scoped
visibility, haptic detents. ~200–300 LOC, 1.5–2 builder-days, and
it needs the density seat's hearing the audit reserved for gesture
layers on a storage screen — filed here as a priced proposal for
that hearing, explicitly **not** as part of the floor pass.

**What could go wrong.** (a) Discoverability: a rail nobody finds
is dead furniture — it must cast its affordance in print (the
first month initial at rail-top, Anonymous meta ink), not in a
tutorial. (b) It fights the scroll it serves if the mapping is
even slightly non-proportional; a rail that lies about position
is worse than no rail. (c) Haptic-per-month at 60 months can turn
a rifle into a buzz — detents soften with velocity or the idea
does not ship. Filed with all three charges attached because the
year-five shelf is a real problem and this is the only proposal
here that solves it at the speed the 1 AM test demands.

---

## REJECTED BY THE INNOVATOR — filed so nobody rebuilds them

- **Shelf weight ∝ fullness** (thicker/bolder month rows for
  fuller months). A count rendered as mass — grading by
  arrangement, the exact charge Dens laid against SPENT columns.
  Dies on the audit's own law.
- **"On this day" / anniversary resurfacing.** Manufactures
  significance from a date coincidence and leans toward streak
  psychology; a blocker has no business reminiscing at him. The
  pull-only thread (P4) covers the honest version of the errand.
- **Relevance-ranked search results.** A score he cannot see,
  ordering he cannot predict, and the destruction of chronological
  place — the one map spatial memory uses. Ghost search (P2) gets
  the speed without the oracle.
- **Generated month summaries** ("September: mostly physics").
  Invented prose about his work, from a model, in the room whose
  whole discipline is that print equals record. The anchor title
  (P3) is the truthful form of the same aid: his words, stored.
- **A "recently looked at" row.** Tracks his browsing to save him
  remembering it — surveillance of the reader by the drawer, and
  a summons wearing a shortcut's clothes. Scroll restoration
  (floor §3.6) already returns him to where he stood.

---

## RANKING, IF ONLY THREE SHIP

1. **P1, the Shrinking Pool** — it is issue #8 item 1 stated as a
   mechanism; without it the bug fix is an accident waiting for
   its next form. Cheapest item here, highest truth value.
2. **P2, Ghost Search** — kills the trap mode the floor only adds
   an exit to, and turns the shelf into a map of where a word
   lives in time. The single largest findability gain available.
3. **P3, the Anchor Title** — half a day to make forty months
   recognisable. With P4's thread as the resurfacing answer when
   the cell wants one: pull-only, bounded by the record, no
   summons anywhere in it.

P5 waits for the looks programme's gesture hearing, priced and
charged above, and loses nothing by waiting.

— Innovator (issue #8 cell)
