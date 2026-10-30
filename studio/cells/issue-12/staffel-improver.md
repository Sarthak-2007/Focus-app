# STAFFEL — IMPROVER SPEC · Issue #12 (Tasks cell, lead Amt)

*Improver: STAFFEL, dedicated-100 programme. One job only: the task
composer's presentation, and how the queue's head presents as a routine
session's intention. This file improves EXISTING output in visible
rounds — it starts from the branch as shipped, never from zero.*

**Base being improved (all on `agent/issue-12` as read Oct 30):**
`AUDIT-tasks.md` (Amt's verdict, items 1–19, F-1…F-9) ·
`PLAN-issue-12-tasks-f1.md` (Amt) · `designer.md` (§1–§8) ·
`feel-5.md` (the queueing ritual) · `feel-2.md` (S-3 night knocks) ·
`builder.md` + branch code: F-1 machinery landed and CI-verified
(RoutineDerive resolver, seal-time re-resolve H1, blank guard H2),
F-3 landed (Horizon bar + HORIZON header deleted, QUEUE header carries
`N TASKS · SEALED` from `displayQueue`), F-6 landed (HAND-SPLIT).
F-2 (composer recomposition) and F-4 (verb truth) were deferred to
batch-10 — the presentation this file improves is exactly that gap.

**Fixed points (not moved, not re-argued):** F-1 is machinery —
routine sessions read the queue; only its surfacing improves here.
The Horizon bar and the SPLIT generator are KILLED — nothing below
revives them in any form: no proportional segment, no minute total,
no generated sub-step, no "stencil" offered back. No green or
near-green anywhere. Type: Sora (display), Atkinson Hyperlegible
(body/leaves), Anonymous Pro (telemetry/numbers) — the app's three
families, no fourth.

**The gap in one paragraph.** On the shipped branch the machinery
keeps the promise and the presentation never tells him. The composer
is still a form bolted *above* the queue it feeds: a recessed field
at the top of the screen (`FocusSequencerScreen.kt`, Section 1),
placeholder "Add single-line focus task..." in 13sp, and a full-width
ink `ADD` KeyCap underneath it — while a leaf it creates lands at the
*bottom* of the list, in a different room from the act of writing.
The head leaf still prints the `ACTIVE` / `QUEUE` plate words the
audit killed (item 7 — `isCued` renders "ACTIVE", every other leaf
"QUEUE", derived from index, never from status), CUE still teleports
to the Deck, and nowhere on the screen does the head leaf state the
one consequence F-1 created: *this leaf's title will name the next
routine session when it seals.* A user who queues perfectly at night
gets no visible confirmation on Tasks that queueing did anything.

---

## ROUND 1 — The composer: the pad's next blank leaf, docked at the foot

*Improves: audit item 2 / F-2, Plan's recomposition, Designer §4.3,
Feel-5 Beats 3–4 + its fights #1–#2, Feel-2 S-3/S-5. What the earlier
output left open — leaf anatomy, the empty-queue collision, the reach
across a long queue, the sealed-state rule, key geometry — is ruled
here, not restated.*

### 1.1 The move, and the reach ruling Feel-5 asked for

The composer leaves Section 1 entirely. It becomes a leaf of the pad
and it is **docked, not listed**: the screen becomes a `Column` —
the queue `LazyColumn` at `weight(1f)` above, the composer leaf fixed
at the foot of the screen below it, above the floating band's 100dp
clearance. It is not the last item of the list.

Why docked beats Plan's list-end leaf (the concrete improvement):
Feel-5's fight #1 stands unanswered in every prior filing — on a long
queue, a list-end composer forces the night writer to scroll the whole
accumulation (the guilt pile) before he can write, so the ceremony is
skipped on exactly the bad weeks it matters. A docked leaf is always
one thumb-travel away, at the desk edge of the pad, whatever the
queue's length or scroll position. The queue itself still opens at
its head (first paint = position 0): he reads #01 first, the blank
waits at his thumb. Spatial honesty is preserved — the leaf he writes
on sits at the foot of the pad, where its task will land — without
making him wade to it.

### 1.2 Leaf anatomy (the composer is drawn as a leaf, not a form)

Top to bottom, reusing the leaf grammar the screen already speaks:

1. **Perforation row** — the same `PerforationRow` as every leaf,
   unsealed state, queued-leaf tone. The blank belongs to the pad;
   the perforation is its membership mark.
2. **Plate row** — left: nothing (no `ACTIVE`/`QUEUE`-class word —
   see Round 2 §2.1). Right: the leaf's future number in Anonymous
   Pro 11sp, `onSurfaceVariant`: `#NN` where NN = `displayQueue.size
   + 1`, two-digit voice. The number is a promise of place: write
   here, and this is the handle your leaf will carry. It is derived,
   never stored; during the tear's ghost window it counts the paper
   in hand, exactly as the QUEUE header count does (F-3's rule).
3. **The ruled field** — the existing recessed field (surfaceVariant,
   12dp radius, outlineVariant hairline) set *inside* the leaf's
   padding, indented 0dp (it is the leaf's title line, not a sub-step
   line). Two changes from the shipped field:
   - **Text size 15sp** Atkinson Hyperlegible Medium — the leaf
     title's own size and family. The shipped 13sp made the writing
     smaller than the thing written; Feel-5 Beat 3 requires the words
     to appear *on the leaf, in the leaf's type, at the leaf's size*.
     On landing, the title takes the leaf's normal weight (Medium,
     or Bold if it lands at the head) — the text does not resize,
     reflow, or change family across the ADD. Cause and effect in
     the same type.
   - **Placeholder: `Write the next task…`** Atkinson 15sp,
     `onSurfaceVariant` (night: the leaf-local plate cure, Round 3
     §3.4). The shipped "Add single-line focus task..." is deleted:
     "single-line" is an implementation constraint leaked into the
     user's sentence, and the sentence described the widget, not the
     work. The new placeholder is an instruction in the pad's voice
     and dies the moment he types, like the old one.
4. **Key row** — two keys, hierarchy ruled (Feel-5 fight #2):
   - **ADD — the end-key.** Right-aligned, a small `KeyCap` in ink
     control fill (the existing cap component, the composer's one
     filled key), Anonymous Pro Bold 12sp, **height 44dp**, width
     hugging its word + 20dp horizontal padding. It is the full-stop
     at the end of the line he just wrote — visually the heaviest
     object on the blank leaf, and nowhere near the shipped
     full-width bar that out-shouted START, the screen's real commit.
     Test tag `add_task_button` moves with it.
   - **FIRST — the quieter key.** Left of ADD on the same row, a bare
     word: Atkinson Hyperlegible Bold 12sp, `onSurfaceVariant`, no
     fill, no border, touch target 44dp tall. FIRST lands the leaf
     at the head (Muede's errand: *this one opens the fasted block*).
     It must be present, legible, unmistakable — and subordinate.
     Two equal keys would make Beat 4 a form's decision ("which
     submit do I mean?") at 1 AM; the weight difference *is* the
     guidance: the ordinary case is loud, the dawn exception is
     quiet. New test tag `first_task_button`.
   - Both keys act on the same trimmed draft; a blank or whitespace
     draft makes both inert and **silent** — no buzz, no shake, no
     refusal knock (Feel-2 S-5: a half-asleep fumble is not punished;
     the leaf just stays blank).
5. **Stack edge** — the blank leaf is the pad's last leaf: no leaves
   behind it, so no pad-edge lines. Its bottom is the pad's bottom.

### 1.3 What each key does (presentation semantics, no new machinery)

- **ADD** appends at the foot: the existing `onAddTask(clean, 0,
  "Study", "", isHead = orderedQueue.isEmpty())` path, unchanged.
  MINUTES DIE stands — 0, no estimate asked, ever. The field clears
  and **keeps focus**, keyboard up, ready for the next line (Designer
  §4.4 rapid-fire entry, now stated as law for this leaf): queueing
  three leaves is three lines written on one pad, not three form
  submissions. IME `Done` = ADD, always — the keyboard's commit is
  the ordinary case; FIRST exists only as the leaf's own key, never
  on the IME, so there is one keyboard behaviour to learn.
- **FIRST** appends then promotes to head in one act — presentation
  requires the *result* be visible without navigation: he stays on
  Tasks, the queue scrolls to the head (the list's own motion, no
  teleport to the Deck), and the new leaf is simply *there*: raised,
  bold, #01, wearing the intention line of Round 2. If the promotion
  funnel refuses (a seal race), the leaf stays at the foot as an
  ordinary ADD and no success sound plays (Feel-2's order law: the
  knock follows the booked fact).
- **Sound — one owner, cured on this leaf.** The shipped ADD speaks
  twice (screen `submitTask()` fires a mechanical latch, the
  ViewModel's add path fires a detent — Feel-2's live defect, and
  Plan's closing notes assign its cure to F-2's composer move, i.e.
  to this round). The recomposed composer deletes the screen's latch
  call with Section 1; the single voice is Feel-2 S-3: ADD = one
  quiet seat-knock at its lightest weight; FIRST = the same knock
  lower and firmer plus the half-weight settle contact. One tap,
  one transient — that is the acceptance line, testable in the
  SoundAndSocialTruthTest shape.

### 1.4 The empty queue: one blank, not two

Shipped collision nobody ruled: the held blank leaf (112dp,
`NOTHING QUEUED`, audit item 6 — KEEP) and a foot composer would
print the pad's emptiness twice, and audit item 1 kills a second
printing of any fact. **Ruling: when the queue is empty, the two
blanks merge.** The docked composer leaf grows to the held blank's
112dp standard; its plate row carries `NOTHING QUEUED` (Atkinson
15sp, `onSurfaceVariant` — the blank leaf's own print, moved, not
duplicated) above the ruled field, and the `#NN` plate reads `#01`.
The list area above shows no blank leaf at all. The moment the first
task lands, the composer leaf settles back to its standard height
and `NOTHING QUEUED` is gone — the queue itself now says it. One
address for "what's next", learned once, kept in both states.

### 1.5 The composer under a seal

Unruled in all prior output; ruled here. A session sealing does not
seal the future: **the composer stays live while a session runs.**
Writing tonight's next leaf during today's block is arranging, not
renegotiating — the sealed leaf's verbs stay absent (F-5, untouched),
but the blank leaf at the foot is not one of the sealed leaf's verbs.
One restriction, from position honesty: **FIRST is absent while
sealed.** The head slot is occupied by a sealed record; a new leaf
cannot land at the head until the seal ends, and a key that promises
the head and delivers the foot would lie. ADD remains, and leaves
added under seal queue behind the sealed head in order. When the
session ends, FIRST returns with the unsealed state — absence, not
greying, the screen's own law for sealed things.

### 1.6 What Round 1 deliberately does not do

No duration bank returns (MINUTES DIE). No category picker, no
priority widget, no date field — the composer asks a name and
nothing else, the acquittal every audit seat signed. No "set as
intention" toggle: queueing *is* setting the intention (Round 2
makes that visible on the head, where it belongs — not as a second
control on the composer). No confirmation dialog for FIRST.

*— STAFFEL, improver · Round 1*

---

## ROUND 2 — The head leaf says what F-1 made it: the intention line

*Improves: audit items 4 + 7 / F-4's head half, Designer §1.2 + §4.3
(overruled in part by Amt — respected, see §2.2), Feel-5 Beats 2 + 6
+ 7, PLAN closing notes (NEXT marker overruled; provenance parked).
The machinery is F-1's and is not touched: snapshot at seal instant,
sealing consumes nothing, DEFERRED never headlines, SLEEP always
keeps its own name, empty queue falls back to the routine name.*

### 2.1 First, finish the audit's kill: ACTIVE / QUEUE plate words die

The shipped leaf still prints them (`if (isCued) "ACTIVE" else
"QUEUE"`, Anonymous Pro Bold 10.5sp, from index alone). Audit item 7
killed them — ACTIVE is false as printed (it means "first in line"
while nothing runs) and both words print as a state what position
already says. In this recomposition they are deleted, not replaced:
the plate row's left side carries only the existing 5dp running
square when the head is cued *and* a session is active (shape and
position, no word), and is otherwise empty. Head-ness keeps the
audit's closed signal list — raised paper (`surfaceBright`), bold
title, `#01`, the accent START — plus the one addition this round
justifies below. The composer leaf's plate row (Round 1 §1.2) is
built to the same law from the start: number only, no word.

### 2.2 The intention line — a sentence, not a marker

Amt overruled the Designer's `NEXT` head-card marker, and that
ruling stands here in full: no NEXT badge, no head "card" treatment,
no new label-word. What the Designer was reaching for — provenance
visible in place — survives in the reduced form Amt's own plan
adopted ("what the Designer got right — provenance should be
visible — is adopted in reduced form"), and this line is that form:
not a status word pinned to the leaf, but **one factual sentence
about tomorrow, printed only where it is true.**

**Placement.** On the head leaf only, directly under the title,
above the sub-steps: the leaf's own consequence, read in the same
glance as its name. Non-head leaves never carry it — the sentence is
false for them, and a line printed everywhere teaches nothing.

**Type.** Anonymous Pro, 10.5sp, Bold, letterSpacing 1sp — the plate
telemetry voice the leaf already uses — in `onSurfaceVariant`
(night: the leaf-local cure, Round 3 §3.4). Never the accent: the
screen's one accent stays START's. Never Sora: this is telemetry
about the title, not a second headline competing with his words.

**States — exact strings.** `<time>` and `<ROUTINE>` are the next
upcoming routine's own start and name, read from routine state the
app already holds; no invented time is ever printed.

| State | Line |
|---|---|
| Unsealed, next routine exists and is not SLEEP | `NAMES THE <time> <ROUTINE> WHEN IT SEALS` — e.g. `NAMES THE 05:00 FASTED BLOCK WHEN IT SEALS` |
| Unsealed, no routine upcoming | *(absent)* — the head still names a hand-armed session as today; no routine, no sentence |
| Unsealed, next routine is SLEEP | *(absent)* — SLEEP never takes the head (F-1's ruled exception); printing the promise here would lie by breakfast |
| Sealed, running session's intention **equals** this leaf's title | `THIS LEAF NAMED THE RUNNING BLOCK · SEALED <time>` |
| Sealed, intention differs (empty-queue seal, SLEEP seal, head torn/replaced after seal) | *(absent)* — the leaf must not claim a session it did not name |
| Ghost leaf (tear's 260ms) | *(absent)* — a booked leaf is a record in motion; the line belongs to the live head that replaces it |

Why these wordings tell the truth (the design work of this round):

- **"WHEN IT SEALS" carries the conditionality.** F-1 reads the
  head at the seal instant (and re-resolves at materialise, H1), so
  the honest claim is not "this *will* be the intention no matter
  what" but "the head *at seal* names it". Editing, reordering or
  deleting before the seal moves the line with the head — it is
  **rendered from position, never stored on the leaf** — so the
  screen teaches live-before-seal / frozen-at-seal by observation:
  he watches the sentence change hands when he CUEs a different
  leaf, and watches it stop moving once sealed. No tooltip, no
  onboarding copy can teach the snapshot rule as cheaply.
- **The sealed state is checked by equality, not by a new column.**
  The running session's stored `intention` string is compared with
  the head leaf's title — data the screen can already reach, zero
  schema, the parked provenance column stays parked. Equality is
  sufficient here because the line claims only what the user can
  verify from the two strings in front of him; the one ambiguity
  (he hand-types a task identical to a routine name) prints a line
  that is still literally true about the words on the session.
- **Two routines, one morning:** the unsealed line always names the
  *next* routine only. After the first seals, the sealed-state line
  governs the running block; the head does not pre-claim the second
  block while the first runs. When the first session ends, the line
  re-points to the next routine on its own. One sentence at a time,
  each one true when read.
- **The tear is the divider, as Feel-5 ruled:** once the head is
  torn, the next leaf rises, takes #01, and takes the line. Nothing
  about the line survives on the torn leaf (ghost state, above).

### 2.3 The empty queue teaches the fallback — once, on the blank

Designer §2's `No task queued` headline was overruled by Amt for the
Shield/receipt/drawer (it would archive a shame label as an
intention), and that ruling stands — **nothing in this round puts
that string on any session surface.** But Tasks itself may state the
fallback as a fact about tomorrow, in the one place Round 1 created
for it: under `NOTHING QUEUED` on the merged empty composer leaf,
one telemetry line in the intention line's type, printed only when
a non-SLEEP routine is upcoming:

`WITH NOTHING QUEUED, THE <time> <ROUTINE> SEALS UNDER ITS OWN NAME`

This is a statement about a routine, on the pad, at night — not a
headline on a session, not a stored intention, not a scolding (no
second sentence, no "add a task" exhortation; the ruled field below
it is the whole invitation). It closes the last silent gap in F-1's
surfacing: he learns the fallback *before* the morning it happens,
from the pad he chose to leave empty. SLEEP-next or no-routine:
line absent, blank stays blank.

### 2.4 Downstream hierarchy: his words hold the highest line

Feel-5 Beat 9 + the Critic's receipt flag, scoped to presentation
only: wherever a routine-born session's record is printed (Shield
headline, receipt headline), the **intention string is the headline**
— Sora, the display voice — verbatim, untruncated in storage,
wrapping to 3 lines on the Shield before any visual ellipsis; the
routine's name demotes to the Anonymous Pro provenance line beneath
it: `<time> <ROUTINE> · SEALED <time>`. That line claims only what is
always true (which routine fired, when it sealed) — it does **not**
print `FROM QUEUE` on stored records, because the provenance column
that could prove it for old rows is parked, and a source claim that
only *usually* holds is a lie with good odds. For sessions sealed
before F-1, headline and provenance coincide (intention *is* the
routine name) and the two lines simply agree — no `SOURCE NOT
RECORDED` apology print, no relabelling of history. The Drawer
entry inherits the same hierarchy and is searchable by the intention
string, as the Designer specced — search is over the stored string
that already exists, no new data.

### 2.5 What Round 2 deliberately does not do

No NEXT marker (Amt's overrule). No provenance column, no taskId
bond, no claim/consume at seal (PLAN rulings 3 + 6 stand). No
Eve Snapshot (killed in the plan; the seal-instant read is the
truthful one, and this line is precisely what makes that visible).
No divergence notice on Tasks during a session (Designer §3.3 puts
`QUEUE MOVED AFTER SEAL` on the receipt only — unchanged; the head
leaf under seal is a record, and records don't annotate themselves).
No line on SLEEP, ever.

*— STAFFEL, improver · Round 2*
