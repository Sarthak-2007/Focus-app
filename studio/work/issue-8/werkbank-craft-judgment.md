# WERKBANK — CRAFT JUDGMENT, ISSUE #8 (THE DRAWER)
## Designer, 29 years. My lens: what survives decades of shipped products.
## Ground: branch `agent/issue-8` at fd15a06; AUDIT-drawer.md whole;
## DRAWER-REVIEW.md (Hale) whole; ISSUE-8-WAVE2-REPORT.md whole;
## MonolithRepository.kt:1934–2113 and MonolithDao.kt:68–75 read line by line.
## Nothing compiled. Nobody compiles.

I judge a joint by four things, in this order: which way the grain
runs, where the load actually travels, what wears first, and whether
the next craftsman can repair it without guessing. A joint that
photographs well and fails one of those is furniture, not structure.
The forge fix gets that test here — not a style pass.

---

## 1 · THE FORGE FIX — THE GRAIN RUNS THE RIGHT WAY (SHIP THE SHAPE)

The old joint (pre-f0e1461, Repository:1861 in the audit's numbering):

```kotlin
CurioCatalog.artifacts.shuffled().first().id
```

Writer drew from ALL SIX. Reader — the cabinet — read the DISTINCT
set (`MonolithDao.kt:75`: `SELECT DISTINCT forgedArtifactId …`).
Two different sets pretending to be one fact. That is a butt joint
screwed from one side: it held until the second forge, then every
repeat was invisible labour and, with the set complete, every later
finish forged into a void while the printed rule kept promising.
Hale's DRW-01 and the audit's §2A "silent death" are the same charge
in two voices and both are true of the old code.

The fix as landed (f0e1461, now Repository:2062–2066):

```kotlin
val alreadyForged = dao.getForgedArtifactIds().toSet()
CurioCatalog.artifacts
    .filter { it.id !in alreadyForged }
    .shuffled()
    .firstOrNull()?.id
```

Why this ages well, specifically:

1. **One source, both sides.** The writer now reads the reader's own
   query. There is no second list to drift, no cached set to go
   stale, no "keep these in sync" comment owed to the future. In 29
   years, every divergence bug I have buried was two copies of one
   fact; this fix deletes the second copy instead of policing it.
   That is the whole craft in one move.
2. **`firstOrNull()`, not `first()`.** The empty pool is a lawful
   state (set complete), and the code treats it as one: null, no
   crash, no invented artifact. The old `.first()` on a full set
   could never be empty; on the complement it would have been a
   crash wearing a fix. Whoever wrote this thought about year six,
   not demo day. Noted, by name.
3. **No new furniture.** No schema, no new table, no counter, no
   migration. The fix is a filter on an existing read. Repairs in
   ten years start from "the forge draws the complement of the
   DISTINCT set" — one sentence, fully true. Fixes that add
   machinery to cure a set disagreement always rot; this one adds
   none.
4. **The set can grow.** If the catalog ever goes to seven, the
   complement grows with it and nothing here changes. If it shrinks,
   dead ids in the forged set are inert. The joint is not fitted to
   today's six; it is fitted to the shape of the problem.

Verdict on the shape: **SHIP.** This is a dovetail, not a photograph
of one.

---

## 2 · CONDITION ONE (BLOCKING) — THE DRAW IS NOT IDEMPOTENT, AND THIS FILE HAS THE SCARS TO KNOW BETTER

Read the landing line (Repository:2102):

```kotlin
forgedArtifactId = if (isRoutineSession) null else forgedId ?: session.forgedArtifactId,
```

`forgedId ?: session.forgedArtifactId` preserves an existing stamp
only when the draw came back null. When the draw succeeds it
**overwrites whatever the session already carried**. And the draw
runs whenever the incoming `session` copy says all blocks completed
— with no check that this session has not already forged.

Why that is reachable, not theoretical: `updateSessionProgress`
takes its session as a caller-supplied copy (Repository:1934–1937).
It never re-reads the row, never checks `isActive`. This codebase
is driven by **two tickers** — its own comments say so twice, both
times as post-mortems: task accrual (≈90 credited per 60 wall
seconds, Repository:~2400s comment block) and the Safe Break
countdown (a 600s break drained in ~400s). Both burns were the same
fire: two callers, each holding a copy, each believing it was the
only writer.

Walk the seam: ticker A completes the span, draws artifact X from
the complement, writes X, sets `isActive = false`. Ticker B —
holding its pre-completion copy, `forgedArtifactId = null`,
elapsed still ≥ target — ticks a breath later. It re-derives
`allBlocksCompleted = true`, re-reads the DISTINCT set (now
including X), draws Y from what remains, and **overwrites the row:
X is un-forged after the fact, Y stands in its place.** No repeat,
no crash, nothing prints false at any single instant — and the
record of what a finished span forged changes after it was made.
In a room whose entire law is "what you filed stays filed," a
stamp that can be silently re-struck is the one defect class this
screen cannot carry.

The cure is one line of joinery, priced in minutes:

```kotlin
val forgedId = if (allBlocksCompleted && !isRoutineSession &&
    session.forgedArtifactId == null) { /* the complement draw */ }
    else session.forgedArtifactId
```

— or equivalently, skip the draw when the session already carries
a catalog id. Draw once per session, ever; after that the stamp is
read, never re-rolled. I do not care which spelling the builder
takes. I care that the forge becomes idempotent, because every
decades-lived system I have maintained survived on exactly that
property at its write seams and died without it.

This is a condition on my sign-off, not a redesign. The shape in
§1 stands untouched.

## 3 · CONDITION TWO (BLOCKING FOR CLOSURE) — AN UNTESTED JOINT IS A PROMISE, NOT A JOINT

I grepped the test tree (`app/src/test`, "forge", case-insensitive).
What exists: routine fences asserting routine sessions forge null
(RoutinesFinaliseTest, TokenBreakRoutineSeamTest), a partial-stamp
assertion, migration DDL. What does **not** exist: a single test of
the complement behaviour that is this fix's entire claim. Nothing
forges five and asserts the sixth is the one remaining; nothing
forges six and asserts the seventh completes with null; nothing
asserts an already-stamped session keeps its stamp (§2's case).

The regression this invites is the worst kind: reverting
2062–2066 to `shuffled().first()` is a one-line change that no
screen would betray for months — the cabinet is evicted, its new
home is #21, and repeats are invisible precisely as the original
bug was invisible. The original bug lived undetected until an
audit read the repository by hand. A test is the audit that runs
every time. Three tests, declared, in the neighbour style of the
existing finalise tests:

1. Seed 5 distinct forged ids → complete a span → the forged id is
   the one remaining catalog id.
2. Seed all 6 → complete a span → session completes
   (`completedSuccessfully`), `forgedArtifactId` stays null.
3. Session already carrying a catalog id reaches the ending write
   again (stale-copy re-tick) → stamp unchanged. (This test fails
   against the code as landed today; it passes with §2's guard.
   That is what the test is for.)

Until those exist I sign the *design* of the fix and withhold
sign-off on its *closure*. Paper without a pin through it is how
this exact bug already lived once.

## 4 · NON-BLOCKING — RECORDED SO THE NEXT CRAFTSMAN DOESN'T REDISCOVER THEM

- **`partial_stamp` rides the DISTINCT read.** `disarmActiveSession`
  writes `forgedArtifactId = "partial_stamp"` for early ends ≥60s,
  so `getForgedArtifactIds()` returns it alongside the six. Today it
  is harmless *by accident of construction*: the complement filter
  only tests catalog ids, and the cabinet only asked "is this
  catalog id in the set." In the cabinet's new home (#21), whoever
  builds the reader must intersect with the catalog explicitly
  (`CurioCatalog.artifacts.filter { it.id in forged }`) and never
  print a count derived from the raw DISTINCT list — a raw count
  would count the stamp as a seventh artifact. One sentence in
  #21's brief saves a false print. (A count in the cabinet is
  itself against the Drawer's no-count law — Dens's charge — but
  that hearing belongs to #21.)
- **Completed-set silence is honest only while nothing promises.**
  With the cabinet evicted and its standing rule line killed, a
  seventh finish forging null prints nothing false — silence, not
  a lie. The audit's §7.3 question (does a completed-set finish
  print anything on the receipt?) is still Sarthak's/Ren's. My
  craft note for that decision: in the new home, the completed set
  must be *sayable* in one plain line when he looks at it, or the
  invisible-forge returns in new clothes — not as a bug this time,
  but as a room that cannot answer "am I done collecting?" from
  what it shows. Six rows all reading FORGED does answer it. Hold
  that property in #21 and the silence is safe.
- **Provenance stays deferred, and must not be claimed.** The audit
  adopted (truth grounds) that the cabinet detail print the
  forging session's date/task; the data exists on the session row.
  The eviction preserved the data; no surface prints it. Correct
  for #8's scope — but no report should list provenance among #8's
  cures. It is #21's first debt.
- **The comment at 2049–2061 is good stock.** It states the law,
  the reason, and the escalation boundary in the place the next
  reader will stand. Keep it verbatim through any §2 edit; only
  extend it with the idempotence sentence.

## 5 · THE REST OF #8, JUDGED WITH THE SAME RULE

Briefly, so this filing is the whole room and not one corner:

- **Filing zone (wave-2): HOLDS.** Files through the existing
  DayWordStore commit, generalised by date, no new store. Reuse of
  the proven write path over a new one — that is the choice that
  survives. The 03:30 seam test the Improver/Student seats asked
  for (tomorrow in next month) is the right test; millis arithmetic
  for "tomorrow" would have been the splinter and the technical
  seat already killed it (filingDate arithmetic only).
- **DayHeading made print-only: HOLDS.** A question that retires
  when answered is a hinge that stops creaking. One ask, one place.
- **Search under seal: HOLDS.** A read that mutates nothing was
  gated by a rule written for writes; separating the two is
  exactly the grain test — load path examined, not costume.
- **Cabinet eviction preserving data/code: HOLDS.** Nothing was
  deleted to make a report cleaner. In 29 years, the deletions
  done for tidiness are the ones that get re-bought at full price.

## SIGN-OFF

The forge fix's **shape ships** — complement of the reader's own
DISTINCT set, `firstOrNull`, no new machinery. My signature on
**closure** waits on two pins: the idempotence guard (§2, minutes)
and the three tests (§3). Neither changes the design; both are
what separate a joint that holds for decades from one that merely
photographs well today — and this bug, of all bugs, has already
proven it can live invisibly inside a good photograph.

— Werkbank, 29-years designer
