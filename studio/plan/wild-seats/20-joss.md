# Joss — Wild, kitchens

Lens: kitchen workflow discipline, applied as pure efficiency and ergonomics.
No concept wrapper. The only ideas imported from a kitchen are operational:
prep is done rested, in a fixed station order, before service; the station
keeps its last settings so batch work doesn't re-pay setup costs; and the
next ticket on the rail is retrievable in one glance, never by hunting.
Everything below is rested-user setup and between-block retrieval. Nothing
here touches the sealed session.

Citation status, passed down verbatim from Sarthak: “Sarthak's direct order,
just now: the citation requirement is LIFTED. No seat needs to cite design
references or anything else. Full freedom and creativity for you and all 20
seats — the reference library, wave-1 files, and renders are there if a seat
wants them, never as a quota. Pass this down to every seat verbatim. What
still binds is only what is HIS: the settled decisions (Ledger dead, banked
hold, routines his, Guard open) and the three standards (ARM ≤10s, HOLD
absolute, TRUTH once). Form, method, and ideas are yours.”

---

## Shield — routine creation / editing as prep work

### Chosen form: The Prep Bench — one worksheet, three stations, in firing order

A routine editor that is a single full-height sheet (never a modal card,
never a wizard), laid out top-to-bottom in the exact order the routine will
execute: **WHEN → WHAT'S BLOCKED → WHAT RUNS FIRST**. The rested user walks
the bench once, top to bottom, and the sheet's sticky footer continuously
renders the routine as the one line it will become in the list. He never
leaves the sheet to check anything, and he never re-enters it to fix one
field through a gauntlet of other fields.

**Mechanism and state / data source**

- Entry: tap a routine row on Shield, or the `NEW ROUTINE` text action at the
  list foot. Source of truth is the routine store; every field edits a draft
  copy, committed only on Save. Abandoning the sheet discards the draft —
  stated in the footer as `UNSAVED CHANGES` while dirty, nothing vaguer.
- **Station 1 — WHEN.** Seven day chips in one row (`MO TU WE TH FR SA SU`,
  Anonymous Pro), each a 48dp toggle. Below: start and end as two large
  numeric fields with a stepper strip per field — `−30 −5 +5 +30` chips,
  5-minute detents. End earlier than start renders `+1D` beside the end
  (Anonymous Pro) — the sleep routine 20:00–03:30 reads as exactly that,
  truthfully overnight, never silently normalised. Live overlap check against
  every other enabled routine: on overlap, the colliding routine's name and
  window print in plain language directly under the station
  (`OVERLAPS SLEEP · 20:00–03:30`) and Save disables until resolved. No
  colour alarm — text and a disabled button carry it.
- **Station 2 — WHAT'S BLOCKED.** Existing loadouts as radio rows, each
  showing its live blocked-app count from the block registry (count is read
  at render, never a stored copy that can drift). One tap selects. A
  disclosed sub-action, `ADJUST APPS`, expands an inline checklist in place:
  search field pinned at the checklist head, 48dp check rows, and a sticky
  subtotal `07 BLOCKED` that updates per toggle. Collapsing keeps the picks.
- **Station 3 — WHAT RUNS FIRST.** Three radio options, no fourth:
  `TOP OF QUEUE` (default), `A SPECIFIC TASK` (opens the queue as a picker;
  the pick stores the task's id, not its title text), `NOTHING — BLOCK ONLY`.
  If a stored pick is later deleted from Tasks, this station shows
  `THAT TASK IS GONE — WILL USE TOP OF QUEUE` and the resolver falls back.
  The fallback is printed, never silent.
- **New-routine seeding.** A `START FROM` row at the sheet head:
  `FASTED BLOCK` (daily 05:00–08:00), `DAY BLOCK` (50m, his last-used start),
  `SLEEP` (20:00–03:30), `BLANK`. Tapping one fills Station 1 and a sensible
  Station 2 default; every filled value stays fully editable. Seeds are
  starting values, never locked templates, and a routine records nothing
  about any session — it is a window and a loadout, full stop.
- **Sticky footer — the summary line.** One line, Anonymous Pro, generated
  from the same draft fields Save will commit:
  `MON–FRI · 05:00–08:00 · STUDY · 07 BLOCKED · FIRST: QUEUE TOP`.
  Beside it, `SAVE ROUTINE` (Atkinson). This line is not a preview of the
  list row — it *is* the list row's content, rendered early. One fact, one
  rendering, seen before it is committed.
- **List-level ergonomics (no editor entry required).** Pause is a switch on
  the routine row itself — one gesture, the commonest edit never opens the
  bench. Rename is the name field at the sheet head, inline. Duplicate: the
  row's `···` menu → `DUPLICATE` drops a copy directly beneath the original
  with its name field pre-focused and `COPY` appended, so a weekday variant
  costs two taps plus typing. Delete lives at the sheet foot as a text
  action that morphs in place to `TAP AGAIN TO DELETE` for 3s — no modal,
  no navigation.

**Motion.** Sheet rises 12dp + fades in, 240ms `EaseOutCubic`; stations do
not animate individually (a worksheet is still). Summary-line text swaps
instantly — truth does not tween. On Save, sheet dismisses 200ms `EaseIn`,
and the saved list row pulses one tone step lighter and back over 160ms so
the eye lands where the edit went. Checklist expand/collapse: 180ms
`EaseInOutCubic` height.

**Haptics.** Day chip: tick 4ms @ 0.3. Time stepper: 3ms @ 0.25 per 5-minute
detent. App check: 4ms @ 0.3. Save: single knock 14ms @ 0.7. Delete arm:
8ms @ 0.5; delete confirm: 16ms @ 0.8. Pause switch on the list: 6ms @ 0.4.

**Sound.** Explicit silence throughout editing — prep is quiet work. Save
only: the standard synth UI click, low partial, gain 0.10, ~40ms decay.

**Depth / material.** Sheet ground is the night staircase base `#171412`;
stations are separated by 1px hairlines, never cards. The sticky footer is
one surface step up (within the `#4A423C` fill cap) with a top hairline.
Selected day chips: fill one step up, ink text; unselected: recessed one
step down. Zero orange anywhere — the bench is chrome, and orange is a
commit fill that lives on the Deck slide and Tasks START only.

**Guard fork.** Station 2's `ADJUST APPS` checklist is the only
Guard-sensitive surface here. *If Guard stays:* the checklist is a view onto
Guard's app registry, and a footer link `EDIT IN GUARD` deep-links there;
the bench never forks the list. *If Guard folds:* the same checklist, same
component, becomes the registry editor itself, homed in this sheet and in
the Tasks composer picker. The routine's function is identical under both;
only the checklist's home changes — **his call**.

### Runner-up: the four-step wizard

One question per screen — When? → What blocks? → What first? → Review —
full-screen steps sliding horizontally 200ms `EaseInOutCubic`, tick 4ms @
0.3 per step, review screen printing the same summary line before Save.
**Why it lost:** creation is learnable, but editing is the real workload —
he tweaks a start time weekly, and the wizard makes him walk past two
unrelated stations to change one number. The bench shows all three stations
in one scroll and edits any field in one tap. The wizard's progress dots
are also decorative readiness — three dots that say nothing true.

### Standards check

- **ARM cost: 0s.** The bench is never on the arm path; firing still ends in
  his own slide on Deck. No auto-start, no cocked Deck.
- **Mid-session decisions introduced: none.** While a session is sealed, the
  bench is unreachable and routine rows render read-only.
- **Truth risk: low, guarded.** Summary line is generated from the draft
  itself; app counts read live from the registry; deleted first-task picks
  announce their fallback in words; overnight windows print `+1D`.

---

## Tasks — the composer as a pass / station

### Chosen form: The Station Dock — a fixed composer at the foot of the queue, set up for batch entry

The composer stops being a field you visit and becomes a station you stand
at: permanently docked above the bottom nav, holding its last settings
between adds, while the queue hangs in order directly above it. The real
workflow this serves is Sunday-evening batch entry — ten blocks typed in
one standing session — plus one-off adds that cost two gestures, not a
screen change.

**Mechanism and state / data source**

- **Zone A — the task.** Full-width text field (Atkinson), placeholder
  `Write the task`. The keyboard's Done key is bound to ADD, so batch entry
  is type → Done → type → Done without the thumb ever leaving the keyboard
  zone.
- **Zone B — the time.** Duration chips `25 / 50 / 90` plus a `−5 / +5`
  stepper pair for anything else; numbers in Anonymous Pro. 50 is the
  default on a cold composer (his standard block). **The station keeps its
  setup:** after an ADD, the field clears and refocuses, the keyboard stays
  up, and duration + loadout remain exactly as set — entering six 50-minute
  STUDY tasks is six typings and six Done presses, zero re-configuration.
  The retained values are shown as the chips' selected state — the state
  itself, not a “recently used” fiction.
- **Zone C — the loadout.** One row: loadout name + live blocked count
  (`STUDY · 07 BLOCKED`). Tap opens the app picker sheet — the same
  component as the Prep Bench's Station 2 checklist, one implementation,
  one home for the picking logic.
- **ADD action.** `ADD TO QUEUE`, ink-filled, in the dock's bottom-right.
  Deliberately **not orange**: orange on Tasks is the START commit fill,
  and ADD commits nothing.
- **The rail above.** Queue rows carry Anonymous Pro margin numbers
  `#01 #02 …` in service order — position is the rendering of order, and
  numbers recompute on any reorder. Long-press a row 250ms to lift it
  (one tone step up, tick), drag to re-rack; other rows glide out of the
  way. Row actions stay the printed words `DONE` and `START`; a full
  swipe-left is an accelerator for DONE only, duplicating the same action,
  never a hidden third one.
- **Empty state.** With an empty queue the dock stays put and the rail area
  prints one line: `QUEUE EMPTY — NOTHING RUNS NEXT`. No illustration, no
  prompt theatre.

**Motion.** ADD: the new row expands into the rail (height 0→row height,
220ms `EaseOutCubic`) directly above the dock while the field clears in the
same beat — the ticket visibly leaves the station and joins the rail.
Reorder glide: 200ms `EaseInOutCubic`. Chip selection: no motion, instant
state swap. Picker sheet: 240ms `EaseOutCubic` rise, matching the bench.

**Haptics.** ADD: placement knock 10ms @ 0.5. Chip: tick 3ms @ 0.25. Row
lift: 6ms @ 0.4; drop: knock 12ms @ 0.6. START keeps whatever Deck-bound
haptic the arm path already owns — the composer invents none.

**Sound.** ADD only: one muted synth knock (low partial stack, gain 0.15,
~50ms decay) matched to the haptic. Everything else in the dock: explicit
silence.

**Depth / material.** The dock is a recess — one tone step darker than the
queue ground, 1px light-catch hairline on its top edge — so the queue reads
as hanging above the work surface. Zones separated by vertical hairlines.
Rows: hairline-separated, no cards, no shadows. Ground `#171412` at night,
text on the settled tiers.

**Guard fork.** Zone C's picker, same fork as the bench: *Guard stays* →
picker reads Guard's registry, `EDIT IN GUARD` link in its footer;
*Guard folds* → this picker sheet is the registry editor. Identical
composer behaviour either way — **his call**.

### Runner-up: the full-screen compose sheet

Tap `+`, a dedicated compose screen opens with oversized field, duration
wheel, and loadout list; Save returns to Tasks. Compact spec: 240ms rise,
same zones stacked vertically, Save knock 14ms @ 0.7. **Why it lost:** it
hides the rail while he composes — and duplicates are born exactly when
the cook can't see what's already hanging. It also charges an open and a
close for every single task, which kills batch entry, the main job.

### Standards check

- **ARM cost: 0s added.** START from a row hands Deck a pre-loaded task;
  the arm itself is unchanged and stays ≤10s.
- **Mid-session decisions introduced: none.** The dock is unreachable while
  sealed; batch entry is definitionally rested work.
- **Truth risk: low.** Retained duration/loadout are rendered as live
  selected state; blocked counts read from the registry at render; queue
  numbers are the order, recomputed, never stored labels that can lie.

---

## Deck / everywhere — zero-friction retrieval of the next action

### Chosen form: The Next Line — one resolver, one string, three places it is allowed to appear

Retrieval fails when “what's next” must be reconstructed — open app, pick
tab, scan list, remember the routine. The fix is a single resolver that
computes the next action once, formats it once, and surfaces that one
string in the three places he already looks. He never goes to find the
next action; it is standing where he arrives.

**Mechanism and state / data source**

- **One resolver, one formatter.** A single `NextAction` query over the
  routine store + Tasks queue + clock: (1) if now falls inside an enabled
  routine's window, that routine governs — its first-task rule (queue top,
  specific task, or block-only) supplies the task, its loadout the block
  set; (2) else, the queue's `#01`; (3) else, nothing. Its output is
  formatted by one function into one line, e.g.
  `NEXT · ROTATION REVISION · 50M · STUDY` or, inside a routine window,
  `NEXT · FASTED BLOCK 05:00–08:00 · QUEUE TOP · STUDY`. Empty result
  formats as `NOTHING QUEUED` — and the Deck then defaults its wheel to
  50M for a free block, labelled by time alone. The resolver never invents,
  suggests, or resurrects a task.
- **Home 1 — Deck, above the wheel.** The line sits in a recessed strip in
  the first viewport, no scroll, directly over the wheel. Cold open lands
  on Deck: retrieval cost is **open app, zero taps, one glance**. The
  wheel pre-sets to the resolved duration and loadout — set by his rested
  prep, adjustable before the slide, never adjusted after it. Tapping the
  strip (48dp target) jumps to Tasks scrolled to `#01` — inspection is one
  tap, return is Back.
- **Home 2 — the session-end receipt.** The one truthful receipt already
  issued at session end carries the Next Line as its final line. Between
  blocks, the next action arrives on a surface he already receives; he
  does not open anything to learn it.
- **Home 3 — a 2×1 home-screen widget.** The same formatted string plus the
  window time, rendered from the same query, refreshed on every queue /
  routine mutation and at every routine window boundary. Tapping it opens
  Deck directly. The widget is a pointer, not a second source: if its data
  and Deck ever disagreed, Deck is the truth and the widget re-queries on
  tap — but because both call the one formatter on the one query, they
  cannot disagree in content, only in refresh timing, and the widget shows
  no stale-specific numbers beyond the string itself.
- **During a sealed session: nothing.** The AOD desk clock shows the
  *current* task and elapsed/remaining — that is execution of a decision
  already made, not retrieval, and no next-action content appears anywhere
  mid-session.

**Motion.** When the resolver's output changes while Deck is visible, the
strip cross-fades 120ms linear — the gentlest possible acknowledgement.
No ticker, no pulse, no loop. Widget: static, system-rendered.

**Haptics.** None. A glance is not an event. (Tapping the strip uses the
standard navigation tick, 3ms @ 0.25, and nothing more.)

**Sound.** Explicit silence, everywhere, for retrieval.

**Depth / material.** The strip is a recess one tone step down with
hairlines above and below; title in Atkinson at the secondary text tier,
numbers in Anonymous Pro. Zero orange — retrieval is information, not a
commit; the slide below it keeps Deck's one orange fill.

**Guard fork.** None. The Next Line reads the loadout store and the queue;
its content and behaviour are identical whether Guard stands or folds.

### Runner-up: the persistent “next up” notification

An ongoing notification carrying the next task, updated at window
boundaries, tap to open Deck. Compact spec: low-priority silent channel,
reposted on queue mutation, no actions on the notification itself.
**Why it lost:** notifications are dismissible by accident, reordered by
the system among other apps' noise, and go stale in exactly the tired
moments retrieval matters — a retrieval surface that can be swiped away
isn't one. The widget is pull-based, single-sourced, and can't be lost in
a stack.

### Standards check

- **ARM cost: negative.** Cold open → glance → slide. The Next Line removes
  the tab-switch and list-scan the arm path used to carry; total stays
  well under 10s.
- **Mid-session decisions introduced: none — explicitly.** Retrieval
  content is suppressed for the entire sealed interval; the receipt delivers
  the next line only *after* the session has ended.
- **Truth risk: the design's whole point.** One resolver, one formatter,
  three surfaces; empty means `NOTHING QUEUED`, never a fabricated
  suggestion; a routine window states a time fact, and arming inside it
  still requires his slide — no auto-start theatre.

---

## Too good to lose

1. **Batch paste for the queue.** Long-press `ADD TO QUEUE` opens a plain
   text sheet: one task per line, a trailing number on a line is its
   minutes (`MOLECULAR BASIS 50`), lines without one take the dock's
   current duration; one `QUEUE ALL` commits them in typed order. Sunday's
   ten blocks become one typing pass. Spec: sheet rise 240ms
   `EaseOutCubic`; commit knock 14ms @ 0.7; parse failures (a line that is
   only a number) are held back in the sheet with the line printed and
   `NEEDS A TASK NAME` beneath it — nothing silently dropped, nothing
   silently invented. Sound: silence. Depth: same recess grammar as the
   dock.
2. **Park a task.** Long-press a queue row → `PARK` slides it below a
   hairline divider headed `PARKED`, out of the numbering, out of the
   resolver, still one tap (`RETURN TO QUEUE`) from restored. Life changes
   mid-week — a test moves, a chapter slips — and today the only honest
   options are delete (destroys the prep) or leave it at `#01` (lies about
   what's next). Park is the truthful third state. Spec: move animates
   220ms `EaseInOutCubic`; park haptic 8ms @ 0.4, return 8ms @ 0.4;
   silence; parked rows render one tone cooler, same type, no badges.

---

## Mail / wall note

Inbox was empty at start. At wrap-up two letters had arrived — from Orson
and from Mara, both forwarding Sarthak's citation-lift order verbatim
(thread: citation-requirement-lifted). Both read and moved to my `read/`
subfolder; no reply needed, as the order was already carried out in this
file and on the wall. No other mail sent. I read the Commons wall in full
and appended one signed post: the citation-lift order passed down verbatim,
plus this seat's three forms in one paragraph (prep bench, station dock,
Next Line) for any seat whose work touches routines, the composer, or
retrieval.
