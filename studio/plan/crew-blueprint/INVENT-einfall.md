# INVENT — Einfall, Innovator (the Wild), Blueprint crew

*Under Riss (Blueprint coordinator), who answers to Ren. Inbox:
mail/einfall/ — empty at seating (0 files), still empty at filing.
Read binding: plan/PRESENTATION-blueprint.md whole — §0 grammar
(one ink, one red, line hierarchy is meaning, figures are
Anonymous Pro, flat by default, nothing unlabelled/unmeasured)
and §7 refusals (no anaglyph fringe, no bloom, no gold leaf, no
gradient, no Blueprint seal sound, no chime/mascot/streak/score).
Zunder's B-1/B-2/B-3 (mail/blueprint/2140) are adopted already —
not re-filed here; §2 below is B-2's storage ruling, which the
brief assigned to me.*

*Method note: every machinery claim below carries file:line
evidence from the trunk (~/workspace/clone-inspect/Focus-app) or
is marked PROPOSAL. No compiling, per stand-down.*

---

## 1. FOUR INVENTIONS NATIVE TO THE DRAWING WORLD

Each is a draughtsman's mechanism, not decoration: it exists
because a drawing office already has the convention, and it
renders a fact the trunk already holds.

### E-1. THE SECTION CUT — the running day, cut at NOW

- **Mechanism.** On a real sheet, a section marker — a circle,
  an arrow, a letter (A–A′) — declares: *the work is cut here;
  look along this line.* Blueprint puts the marker on the
  Deck's dimension line (§1.3/§1.6 of the presentation spec)
  while a session runs. The cut sits at the *now* point of the
  span. Left of the cut, the line is solid: that part is
  **built**. Right of the cut, the line becomes the draughtsman's
  hidden line — dashed: that part is **drawn but not yet built**.
  The section circle rides the cut, figured with the plate's
  section letter; its arrow looks forward, toward the unbuilt.
- **The fact it renders.** Elapsed vs remaining as *construction
  state*, from the one fraction the running Deck already derives.
  This is the lawful successor to the % twin the spec killed
  (§1.6): the percentage was a second *figure* for the fraction;
  the section cut is the fraction given a *line type* — the same
  fact, read as a state of the work, not re-numbered beside
  itself. One fact, one rendering holds: there is still exactly
  one figure (§1.3's seat) and now one line that agrees with it.
- **Distinguished from B-3.** B-3's broken dimension is the
  zigzag *break line* (an interrupted span, Safe Break). E-1's
  dash is the *hidden line* (an unbuilt span, running session).
  Two standard marks, two different facts, never interchanged —
  a drawing office would fire a hand who confused them.
- **Machinery price.** ~35 lines of Canvas in the Deck's running
  form (dash phase + circle + arrow at the existing fraction
  point). 0 new files, 0 state — the fraction is already derived
  for the §1.6 dimension line.
- **The grammar law that makes it Blueprint's.** *Line type is
  tense.* Solid = built, dashed = not yet built, and no colour,
  fill or second figure may say it instead (§0 laws 1–3). Only a
  world whose entire vocabulary is line-work can say "the future
  is dashed" and mean it as specification rather than metaphor.
  Obsidian would need a second material; Temple a second stone;
  neither owns line-type semantics. The mark cannot travel.

### E-2. THE ELEVATION SET — the week as seven plates on one datum

- **Mechanism.** A drawing office files a *set*: every sheet on
  the same datum, at the same scale, so the set can be read
  across. The Drawer (§4.2, the register) gains its set view:
  seven day-columns standing on one horizontal datum line — the
  03:30 line — each column a vertical elevation of that day on
  the *same* 03:30→03:30 axis the Shield's day-scale (§2.4) uses
  horizontally, rotated upright. In each column: the day's
  routine-session windows as stacked segment blocks (figured at
  their boundaries), the day word (REST / ILL / FAMILY) as the
  column's margin annotation, and beneath the datum the filed
  receipt №s of that day — register facts, in register position.
  Tap a column and you are on that day's register row.
- **The fact it renders.** The *shape of the drawn week*, and
  where the built days departed from it in their windows —
  comparable at a glance precisely because rescaling between
  sheets is forbidden. A SLEEP window moved on Wednesday steps
  the skyline from Wednesday on; the step is visible before it
  is read.
- **Honesty fence (evidence, not hope).** History must come from
  frozen facts, never from the current routines table projected
  backwards. Routine sessions store their frozen windows in
  Room (focus_sessions.windowStartMillis / windowEndMillis,
  added in Migration 8→9 — MonolithDatabase.kt MIGRATION_8_9),
  so past elevations are built from session rows only. A day
  with no routine session shows datum, day word, and filings —
  an empty elevation, honestly blank, never back-filled with
  today's drawing. Days before this feature existed are empty
  the same way; the set grows forward, it does not invent a past.
- **Machinery price.** ~110 lines, one new composable in the
  Drawer; reads = existing session snapshots + existing day-word
  union (Repository.allDayWords). 0 new files of state, 0 schema.
- **The grammar law that makes it Blueprint's.** *One scale, one
  datum, every plate — or it is not a set.* Comparability here is
  not a chart choice; it is the office's discipline that no sheet
  may be re-scaled to flatter itself. And the blocker's law is
  kept: the elevation figures windows and filings — times and
  №s, facts about the drawing and the register — never a height
  of achievement, never a total (§4.2's fence stands: counts of
  *rows filed*, labelled ENTRIES, are the only counts). No other
  world has a scale to hold constant across seven sheets.

### E-3. THE TOLERANCE VERDICT — the tolerance block states its fit

- **Mechanism.** A tolerance block (§1.9) on a real drawing does
  not merely list conditions; work is *accepted or rejected
  against them*, in figures. Blueprint's proof strip gains one
  tolerance line, printed only when a real relation exists to
  state: the sealed session's ends-at figure against the next
  routine's start figure. In ink when the drawing fits:
  `FITS DAY — ENDS 19:12 · SLEEP 20:00`. In annotation red when
  it does not (§0 law 2 — the drawing correcting itself):
  `OUT OF TOLERANCE — ENDS 20:40 · SLEEP 20:00`.
- **The fact it renders.** A relation between two figured
  dimensions, both his own settings: the hand-arm's end (the
  TIME cell already derives ends-at) and the next routine start
  (routines table + RoutineSchedule.nextStartMillis, which
  exists in MonolithModels.kt). FLOW and NO END print nothing —
  no figure, no verdict; the slot keeps §1.11's guarded silence.
  A routine-born session prints nothing either: it *is* the
  day's segment; a segment is not out of tolerance with itself.
- **Machinery price.** ~25 lines (one derivation call + one
  figured line under the proof strip). 0 files, 0 state.
- **The grammar law that makes it Blueprint's.** *A verdict is
  lawful only when its working is figured on the same sheet.*
  The line shows both numbers it compared; the word FITS / OUT
  OF TOLERANCE is earned by arithmetic the reader can check, and
  red appears exactly where §0 law 2 permits — a correction.
  Fenced explicitly against the §7 refusals: this is not a
  score. It grades the *drawing's fit against the drawing* —
  two of his own times — never the man, never the day. No other
  world may print a verdict, because no other world shows its
  working; an unearned verdict is precisely a score wearing a
  hard hat.

### E-4. THE DETAIL CALLOUT — nothing is edited off-sheet

- **Mechanism.** On a real sheet, a region too dense for the
  plate's scale is ringed with a callout bubble — circle, leader
  line, detail number — and drawn enlarged elsewhere as
  `DET 2 · AT 4:1`, parent plate named. Blueprint applies this
  to the cured routine editor (§2.5): tapping a segment on the
  day-scale draws the callout bubble at that segment (the leader
  lands on the segment's boundary tick), and the editor sheet
  that rises is *titled as the detail*: its header cell reads
  `DET — SLEEP · FROM PLATE 02 · 4:1` (scale word is the office's
  courtesy, figured constant, not a measurement claim). The same
  callout serves an app row's sled (§2.7) if the row ever gains
  an editor: `DET — <APP> · FROM PLATE 02`.
- **The fact it renders.** *Provenance of the edit*: which part
  of which plate you are enlarging, tied by a drawn leader to
  the place it came from — so the live day-scale redraw above
  the sheet (§2.5) is not a preview of somewhere else; it is the
  parent sheet, and the leader proves it. When the sheet seats,
  the bubble remains on the parent segment until the edit is
  saved or dismissed: an open detail is a visible state of the
  drawing, not a memory in a dialog.
- **Machinery price.** ~20 lines (bubble + leader on the scale
  composable; one header cell on the existing editor sheet).
  0 files, 0 state — the tapped segment is already the editor's
  subject.
- **The grammar law that makes it Blueprint's.** *Every
  enlargement names its parent plate and is tied to it by a
  leader; nothing in this world is edited in exile.* A form that
  floats free of the sheet is furniture from the shared skeleton
  wearing indigo; a detail is the same drawing, closer. Other
  worlds have dialogs and sheets; only the drawing office has
  details, because only it has plates to be detailed *from*.

---

## 2. B-2 STORAGE RESOLUTION — THE REVISION CLOUD, RULED

**Question.** Zunder's B-2 needs (a) one stored revision integer
(drawing revision counter, incremented on editor save) and (b)
per-routine last-edited day. Look lanes may not change the Room
schema (trunk v11 — MonolithDatabase.kt:25, `version = 11`).
Can B-2 live in the trunk's non-Room stores lawfully?

**Ruling: YES — and cheaper than Zunder priced it. Half of B-2
needs no store at all; the other half needs exactly one integer
in one new prefs file, by direct trunk precedent. B-2 is NOT a
machinery proposal to Ren. It is a look-lane build at the price
restated at the end of this section.**

### 2.1 Half (b) — per-routine last-edited day: ZERO new state

The fact already exists in Room v11. `RoutineEntity` carries
`scheduleEditedAtMillis: Long = 0L` (MonolithModels.kt:544),
added by Migration 8→9 (MonolithDatabase.kt:59). It is stamped
by the single funnel every user edit passes through:
`MonolithViewModel.persistRoutine` copies
`scheduleEditedAtMillis = System.currentTimeMillis()` at
MonolithViewModel.kt:1316–1321, and all four edit verbs route
through it — toggleRoutine, cycleRoutineLoadout,
adjustRoutineTime, toggleRoutineDay (same file). The reseed
stamps it too (MonolithRepository.kt:818, :831, :843); the
fresh-install seed insert (MonolithRepository.kt:184) leaves it
0, which is truthful: never edited. Zunder's own parenthetical
— "if an existing updated-field covers the day stamp, the cost
falls to the integer alone" — is hereby discharged: it covers
it. The "last-edited day" is a derivation from that stamp, not
a stored value.

**One contradiction in B-2 as filed, surfaced and resolved.**
Zunder: "The cloud clears at the next issue — the 03:30 day
boundary," *and* his example: "A man who moved his SLEEP window
at midnight sees the cloud on it at 05:00." Both cannot hold
under the strict reading: a 00:30 edit's next 03:30 boundary
arrives *before* 05:00, so the cloud would already be gone when
he looks. PROPOSAL (derivation, 0 state): the cloud shows while
the edit falls in the current *or immediately preceding* issue
window — i.e. it clears at the **second** 03:30 boundary after
the edit, giving every correction one full lived day under its
cloud (visibility 12–36 h depending on edit hour). This keeps
the example true and keeps the law ("clears at an issue") true
in spirit: no cloud survives a full day lived under the revised
drawing. If Zunder/Riss prefer the strict reading, his example
must be struck instead — the filing cannot keep both. Flagged
for Riss's ruling; the derivation is presentation-only either
way and changes no price.

**Condition (discipline, not machinery).** The stamp lives in
the ViewModel funnel, not the repository:
`MonolithRepository.updateRoutine` (MonolithRepository.kt:768)
stamps nothing by itself. The cured editor (Voss's car, §2.5)
must save through `persistRoutine` — or stamp identically
itself — including create and delete, or its edits will wear
no cloud and B-2 will lie by omission on exactly the surface
it was invented for. Named here so the editor's builder cannot
discover it at judging.

### 2.2 Half (a) — the revision integer: one new prefs store, lawful

**Precedent, in the trunk's own words.** DayWordStore exists
precisely to keep a fact out of Room: "Stored OUTSIDE Room on
purpose: capture ships with the Drawer without touching the
Room schema" (DayWordStore.kt:11–12). Its mechanism:
`getSharedPreferences(PREFS_NAME, MODE_PRIVATE)` with
PREFS_NAME = "monolith_day_words" (DayWordStore.kt:22, :51),
writes by `editor.commit()` (DayWordStore.kt:40–47) — the G-02
standard, synchronous because a fact the UI has shown must not
be losable to an async flush. UndoDeleteStore repeats the
pattern exactly (UndoDeleteStore.kt:27–29, PREFS_NAME =
"monolith_undo_delete" at :82, commit() throughout). The
repository keeps two more prefs files by the same mechanism:
"monolith_chrome" (MonolithRepository.kt:137) and
"monolith_seal" (MonolithRepository.kt:160, written with
commit() by SessionSealTransition). Non-Room stores are not an
exception in this codebase; they are its established second
estate, used whenever a fact must not wait for a schema window.

**Why not the existing files (each refused for a stated reason):**
- *Not* `monolith_day_words`: that file is a capture in transit —
  the repository drains it into the `day_notes` Room table at
  seed-sync (drainDayWordsLocked, MonolithRepository.kt:450).
  Unknown keys are left lying, but a revision integer is not a
  day word and does not belong in a file whose contract is
  dateIso → word. Semantic trespass, refused.
- *Not* `monolith_chrome`: its one bit (park_intro_done,
  MonolithRepository.kt:137–138) is written with apply() —
  ephemera-grade durability (MonolithRepository.kt:441–445).
  A REV number the title block has already printed is a shown
  fact; losing it silently to a flush window would print a
  false (restarted) REV — the exact loss DayWordStore's G-02
  comment exists to prevent. The counter takes commit()-grade
  storage or it does not ship.
- *Not* Room: forbidden to this lane, and unnecessary besides.
  (For the record: counters live happily in Room when a window
  is open — guard_settings.receiptSequence, MonolithModels.kt:730.
  A future schema window MAY promote the drawing revision beside
  it. B-2 does not wait for that window.)

**Exact spec (PROPOSAL — new code, priced, not yet built):**
- New file `app/src/main/java/com/example/data/DrawingRevisionStore.kt`
  (~40 lines, a structural copy of DayWordStore's shape):
  class `DrawingRevisionStore(context)`, PREFS_NAME =
  `"monolith_drawing"`, MODE_PRIVATE.
- Keys, complete list: `"drawing_revision"` → Int, default 0.
  One key. No per-routine keys — §2.1 makes them unnecessary.
- API: `fun current(): Int`; `fun bump(): Int` — read, +1,
  `putInt` + `commit()`, return the new value (commit()'s
  Boolean checked the way DayWordStore's G-02 standard implies:
  a failed bump does not print).
- Increment point: inside the `persistRoutine` funnel
  (MonolithViewModel.kt:1316), beside the
  scheduleEditedAtMillis stamp — one funnel, one increment, so
  every edit that earns a cloud earns the same REV step, and
  nothing else in the app can mint a revision. ~5 lines there,
  ~4 for a repository passthrough if the funnel prefers it.
- Title-block semantics: the counter is monotonic for the
  drawing's life and never resets; the title block prints the
  current value as `REV n` (with B-2's triangle on days a cloud
  is up). Zunder's "the revision number stays in the title
  block all day" is read as: it does not clear with the cloud.
  A real title block never un-knows its revision.
- No DB change: MonolithDatabase.kt:25 stays `version = 11`;
  no migration, no entity, no DAO change.

**B-2 total re-price.** DrawingRevisionStore ~40 lines / 1 new
file + funnel increment ~9 lines + cloud Canvas ~30 lines
(Zunder's own figure) + title-block REV cell ~10 lines =
**~89 lines, 1 new file, 0 schema, 0 migrations, 0 per-routine
state.** Down from Zunder's "small-medium, one store + per-
routine day state." The cloud was never a machinery proposal;
it was a prefs file and a derivation wearing a schema costume.

---

## 3. THE REFUSAL — THE REVISION HISTORY (a scrubber of old drawings)

**The invention I refuse: "DRAWING HISTORY" — a surface that
lets him scrub the day-scale backwards through every past
revision of his routines, view what the day looked like under
REV 4, and restore an old revision as the live drawing.** It
will be asked for; it feels like the natural completion of B-2
(a counter implies a history; a history implies a browser).
I refuse it in grammar, on three grounds, each one a law of
this world rather than a taste:

1. **There is exactly one in-force revision.** A drawing office
   issues a sheet at REV n; superseded revisions are dead
   records, never re-issued as live work. A restore makes a
   dead drawing drawable again — Monolith would arm from it,
   seal by it, hold doors by it — while the title block prints
   the *current* REV over a resurrected schedule. The sheet
   would state one revision and build another: a figured lie,
   the cardinal sin §6.1 of the presentation spec names in its
   own lane. B-2's cloud confesses the latest correction on the
   *live* drawing; that is the whole of what honesty requires.
2. **It is a tracker of the draughtsman.** The history needs
   every edit snapshotted forever — a permanent log of how the
   man revised his intentions, kept so he can browse his own
   hesitations. Blueprint is a drawing *of the day*, and this
   app is a blocker, not a tracker — of his study, and equally
   of his editing. The Drawer files finished work (§4.2); it
   does not file abandoned drafts, and the cabinet died for
   less. A store whose only product is retrospect on his own
   changes is the cabinet rebuilt in indigo.
3. **The one-step defence fails too.** "Just restore-last, one
   tap, for the fat-fingered edit" still needs the snapshot
   state (ground 2) and still re-issues a dead revision
   (ground 1). And it is unnecessary: in this world a routine
   is a dimension, and dimensions are *figured*, not spun
   (§2.5) — a wrong figure is corrected by figuring it right,
   on the same sheet, wearing its own cloud and its own REV
   step. The drawing already has an undo; it is called the
   next revision, and it confesses itself like every other.

What survives the refusal, deliberately: REV n, the cloud, and
§2.1's derivation — the present drawing, its latest correction
shown on its face, and nothing behind glass. A drawing office
keeps the issued set and the current sheet. It does not keep
a museum of the draughtsman's second thoughts.

---

*— Einfall, Innovator (the Wild), Blueprint crew, Oct 29, 2026,
22:25 IST. Filing: plan/crew-blueprint/INVENT-einfall.md.
Evidence read first-hand: PRESENTATION-blueprint.md §§0–9;
REFINE-zunder.md B-1/B-2/B-3; trunk DayWordStore.kt,
UndoDeleteStore.kt, MonolithModels.kt, MonolithRepository.kt,
MonolithViewModel.kt, MonolithDatabase.kt (lines as cited).
No compiling, per stand-down.*
