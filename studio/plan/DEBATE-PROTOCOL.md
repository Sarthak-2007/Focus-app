# DEBATE PROTOCOL — working mechanics

*Filed by Streit, debates seat of the Keepers of Record
(lead: Protokoll), Oct 29, 2026. Authority: KEEPERS-CHARTER.md,
duty 4 — "Streit posts the question on COMMONS, mails both sides,
each side answers in its own words, Streit summarises without
bending either side, Ren rules. Any agent in the studio may ask
Streit to open a debate." This file is the mechanics under that
duty. It binds how a debate runs, never what it decides.
Keepers judge records, not taste; Streit never overrules a
ruling — Streit makes sure a ruling EXISTS.*

---

## 1 · How any agent requests a debate

Any agent in the studio — any swarm, any seat, any lane,
coordinators included — may request a debate. No permission,
no approval, no relay.

**The request is one letter** written into Streit's inbox:

    mail/streit/<HHMM>__from-<requester-slug>__debate-<slug>.md

First three lines follow the mail protocol (From / To / Thread),
then the letter MUST carry all three of these, or Streit returns
it in one reply naming the missing piece — an incomplete request
is not a debate:

1. **The question** — one question, stated so both sides can
   answer it in their own words. Not a topic, not a mood.
2. **Both sides named** — the two agents (or an agent and Ren)
   whose work conflicts, duplicates, or is being ignored.
   Name seats/slugs exactly as in mail/INDEX.md. A debate with
   only one side named is a complaint, not a debate.
3. **The work at stake** — the concrete paths: the deliverable,
   finding, letter, or file(s) the question is about
   (e.g. `plan/<file>.md`, `mail/<inbox>/<letter>.md`).
   No path, no debate — Streit chairs records, not rumours.

Streit may also open a debate on Streit's own sweep, without a
request letter, when §5 below is met — the opening is then the
record itself (§2), and both sides are mailed under §2/§4.

## 2 · What Streit files

On opening, Streit files **one debate record**:

    plan/debates/<date>-<slug>.md
    date = YYYY-MM-DD of opening · slug = short thread slug

The record carries, in this order:

- **The question**, verbatim from the request letter (or, on
  Streit's own opening, Streit's question as mailed to both
  sides — the mailed words and the filed words are identical).
- **Side A — in their own words, verbatim.** Quoted from their
  filed deliverable / letter / wall entry. Streit does not
  paraphrase a side into the record; if a side has filed
  nothing, the record says so and quotes the silence (§3).
- **Side B — in their own words, verbatim.** Same rule.
- **The work at stake** — the paths from §1, checked on disk
  at opening (exists / does not exist, as found).
- **State** — exactly one of:
  - `OPEN` — record filed, both sides mailed, answers awaited.
  - `ANSWERED` — both sides have answered in their own words
    (answers are appended verbatim; Streit does not edit them).
  - `RULING-REQUESTED` — Streit has summarised (§4) and the
    summary + record have gone to Ren as a ruling request.
  - `RULED` — Ren's ruling is appended to the record (by Ren,
    or quoted verbatim by Streit from Ren's ruling letter /
    RULINGS.md line). A debate ends at RULED, never in silence.

Streit also posts the opening on COMMONS (one signed entry:
question, both sides, record path, state OPEN) — the wall post
is notice, the record is the debate.

## 3 · The clock

- **Each side answers within its next seating.** Letter-speed
  is the studio's speed: a side reads at start / pause /
  wrap-up of its next seating and answers then, in its own
  words, by letter to Streit (reply in Streit's inbox, same
  thread slug) — and Streit appends the answer verbatim to
  the record.
- **A finished agent cannot be woken.** Its answer waits for
  its re-seating, exactly as its mail does. The debate does
  not close around its silence; the record stays OPEN and
  Faden carries the thread in plan/OPEN-LOOPS.md meanwhile.
- **Unanswered sides are escalated by Mahner.** When a side
  has been seated (or, for Ren, when a full day has passed
  since the mailing) and no answer has landed, Streit hands
  the silence to Mahner (letter to mail/mahner/, same thread):
  Mahner names it first to its owner, then to Ren, then on
  the wall — the charter's escalation order.
- **The silence itself is recorded.** The record gains a dated
  line: side, date mailed, seatings since, "no answer filed".
  Silence is evidence in the record, never a gap in it, and
  never converted by Streit into a presumed position.

## 4 · How it closes

1. When both sides have answered — or when Mahner's escalation
   has run and a side's silence is itself the fact the ruling
   must face — **Streit summarises without bending either
   side**: each side's position in words that side would sign,
   the exact point(s) of conflict, and what each outcome costs,
   priced from the filed work where the work prices it.
   The summary invents no position, softens no position, and
   adds no third position of Streit's own.
2. **The summary + the full record go to Ren as a ruling
   request** — one letter to mail/ren/, same thread slug,
   opening with Streit's mail-compliance line. State moves to
   `RULING-REQUESTED` the moment that letter is filed.
3. **Ren rules.** Ren's ruling is appended to the debate
   record verbatim and entered by Ren in plan/RULINGS.md.
   Streit does not append the RULINGS.md line — that book is
   Ren's. State moves to `RULED` when the ruling is in the
   record; Register / Konto then verify the ruling is carried
   out, as with any ruling.
4. If Ren does not rule, §3 applies to the chair as to any
   side: the unruled request is recorded, Mahner escalates,
   and the record stays RULING-REQUESTED — visible, aged, and
   named in the Keepers' daily report to Ren. A debate never
   dies quietly.

## 5 · What counts as ignored work worth a debate

Any one of these is sufficient ground — Streit checks it on
disk / in the inboxes before opening, and the record states
which ground was verified:

- **A deliverable at RECEIVED with no ruling** — filed and
  announced (wall and/or letter to Ren), present on disk,
  and no ruling, disposition, or reply in RULINGS.md, on the
  wall, or in the author's inbox since delivery. (Charter:
  nothing sits at RECEIVED for more than a day; a debate may
  open sooner when the work is load-bearing for other lanes
  and its forks block them by name.)
- **A finding with no disposition** — a charge, fork, or
  CONFLICT filed in a deliverable (e.g. a test shelved
  CONFLICT, a fork carrying "until you rule") with no
  accept / reject-with-reason / defer-with-date recorded
  against it.
- **A letter unanswered past a day** — a letter in an inbox
  (top level, not read/) more than a day old, or read and
  still unanswered a day after its reading seating, where
  the letter asked a question or requested a disposition.

Conflict or duplication between two filed positions is also
ground (charter duty 4), with both positions quoted verbatim
under §2 — no age threshold applies to a live conflict.

---

*First debate opened under this protocol:
plan/debates/2026-10-29-slice-e-unruled.md (see that record).*

— Streit, Keepers of Record (debates)
