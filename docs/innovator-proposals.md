# INNOVATOR PROPOSALS — THE HELD DOOR (issue #11)

Seat: Innovator, Tor's cell · For: Tor (lead) · Ground: the merged
tree at `builds/wt-ci` (batch-9 lineage — the fresh issue-11 clone
was still an empty `.git` at filing time; every line reference
below is from that tree and should be re-checked against the
issue-11 branch before build).

Files read whole or in the relevant part:
`ui/components/AirlockAndDisarmOverlays.kt` (the door at :550, the
Disarm face at :732), `ui/MonolithViewModel.kt` (`peekOfferFor`
:141, `AirlockInterceptState` :153, `triggerAirlockIntercept`
:2315, `start2MinutePeek` :2385, `requestDisarmChallenge` :2236,
`completeEmergencyBreakFromValve` :2292), `MainActivity.kt` :793–
808 (both overlays composed, Disarm after Airlock = on top),
`data/MonolithRepository.kt` :1176–1225 (cooldown + lease),
`ui/views/BlockitSuitcaseAndSwitchesViews.kt` :532
(`Emergency60sValveView`), `service/TactileSoundEngine.kt`
(the existing voices), `studio/plan/AUDIT-held-door.md` whole.

Standing position: FIX-1…FIX-8 are the floor. Nothing below
re-litigates an acquittal (the offer matrix, RETURN never gated,
price before act, Night palette, the card shell pending the Leaf
call). Every proposal is a mechanism the fix list does not
already contain, each with spec + price. House rules observed:
never green anywhere in any proposal; no AI images; nobody
compiles — all prices are paper prices in the audit's own units
(SMALL / SMALL–MED / MED).

---

## P-1 — THE BORN-OCCUPIED SLOT, AND A STATIONARITY PROOF
### Attacks (a): the 55dp RETURN jump — and every future face that grows under a thumb

FIX-6 specifies the skeleton (reserved slot, bottom-anchored
RETURN). A specification is a hope until it is a test. This
proposal adds the two mechanisms that make the skeleton
*structural* instead of aspirational:

**1a. The slot is born occupied.** In the shipped door
(`AirlockAndDisarmOverlays.kt` :550), the peek zone is empty
during the wall and the chip *inserts* at countdown 0. Under the
fix, the reserved slot must never render as emptiness: from
frame one it is occupied by the gate line itself — FIX-6's
"PEEK OPENS IN 0:0X" over Schwelle's draining 2dp rule, laid out
in the slot's full height, vertically centred where the chip
will sit. At wall 0 the gate line *crossfades in place* (180ms,
FIX-7's entry spec) into the chip + price. The eye reads a
transformation of one object, not the arrival of a second one.
A thumb already travelling toward RETURN's rect passes through
a slot whose contents changed state, never position — and the
slot is not RETURN's rect, ever, in any state.

**1b. Slot height is measured worst-case, not estimated.** The
audit prices the offer branch at 110dp with a "verify on device"
rider. Replace the estimate with a derivation: slot height =
chip 48dp (F-4) + 6dp gap + price text at its worst lawful wrap.
Worst case is computable today: FIX-8's re-timed price ("WHEN
THE PEEK ENDS, A {n} MINUTES COOLDOWN STARTS ON THIS APP") with
the largest editable-store cooldown, at 12.5sp Atkinson, on the
narrowest supported width (320dp minus card padding 48dp =
272dp measure). The builder measures that string once, in a
unit test over the pure price function, and the slot constant is
*that test's output*, pinned. If a future editable-store value
lengthens the price past the pinned height, the test fails
before the face can reflow. The withheld branch (34dp) and the
no-peek-owed branch (0dp) take the same treatment: the slot
height is a pure function of the door's class
(`path.canHonourPeekLease && tier == PEEK_2M` → offer-slot
height; else 0), computed at `triggerAirlockIntercept` time and
carried in state — never re-derived mid-visit.

**1c. The stationarity proof (the actual invention).** A JVM
test — no device, no compile beyond Ren's normal proof build —
that renders `AirlockInterceptOverlay` (or, cheaper, the pure
layout derivation beneath it) across the door's full state
matrix — wall running / wall 0 + offer / wall 0 + withheld
COOLDOWN / withheld ALREADY_ACTIVE / withheld PEEK_OFF / no
peek owed / countdown expired mid-cooldown (FIX-5c) — and
asserts the RETURN slab's bounds are **identical in every
state**, and the card's total height is identical within a
visit class. The audit caught F-2 by a finisher's eye; eyes
don't scale to every future face. This converts FIX-6 from a
one-time repair into a standing law with a tripwire, and the
same harness is reusable by every other overlay audit (Tor's
own NEXT (b) wants FIX-6's skeleton law carried to the other
five faces — this is the carrying mechanism).

**Spec delta:** overlay restructure local to
`AirlockInterceptOverlay` (slot Box with state-carried height,
gate line composed inside it from frame one, crossfade at arm);
one pure `doorSlotHeightFor(path, tier)` beside `peekOfferFor`;
one test class.
**Price: SMALL–MED** (the layout work is inside FIX-6's price;
the increment is the test harness + the measured-height pin).
**Risk:** the card stands taller on peek doors than the shipped
minimal card — accepted by FIX-6 already; the 0dp branch keeps
locked doors exactly as lean as today. Compose test
infrastructure for bounds assertions is the one unknown; if
Robolectric-style rendering is absent from the tree, the
fallback is asserting over the pure layout derivation (heights
and order), which still catches every reflow that originates in
state — the only kind F-2 was.

---

## P-2 — THE DOOR TURNS: THE HONEST EXIT AS A SECOND FACE, NOT A SECOND PLACE
### Attacks (b): "END THE SESSION INSTEAD" must feel like a door

FIX-8 ships the line as a route to `DisarmChallengeOverlay`.
A text line that teleports him to a different card over a
different screen is a form with a link in it. The machinery for
something better is already in the tree — this proposal is
mostly wiring, plus two truth guards the route exposes:

**2a. The door never leaves; the exit face stands in front of
it.** `MainActivity` :793–808 already composes the Disarm
overlay *after* the Airlock overlay — later composition, higher
z. So: the footer line's tap calls `requestDisarmChallenge()`
and **does not hide the airlock**. The Disarm card opens over
the still-standing door; its KEEP FOCUSING
(`dismissDisarmChallenge`, VM :2263) peels it away and the man
is back at the door he was facing, countdown and peek state
untouched, RETURN still live. Nothing was booked by the round
trip — no intercept outcome is written by either function
today, and the spec forbids adding one: looking at the exit is
not an outcome. The psychological shape is the point: he is
never "taken somewhere to fill in an ending"; he turns the
door's other face toward himself, reads the price, and turns
it back.

**2b. There is never a frame with no door standing.** The one
forbidden transition is airlock-hidden-then-disarm-shown (or
the reverse) with a gap frame between them — a gap shows the
blocked app, reward delivered, the exact sin FIX-7a executes
the missing GLOBAL_ACTION_BACK for. Because 2a never hides the
airlock, the invariant holds by construction. Spec it as a
law anyway, for whoever touches the wiring later: any future
change that hides one face before showing the other is a
regression by definition, named here so a reviewer can cite it.

**2c. The stopwatch shortcut must not fire from the door.**
`requestDisarmChallenge` (VM :2236) has a trap for this route:
a stopwatch session past its 20-minute floor **disarms
immediately and silently** — no face, no valve, booking
"Stopwatch Completed". From the Deck that is arguably a
feature. From the Held Door it is a disaster: a quiet footer
tap ends the session with zero gravity, and an urge-tap on the
smallest text on the face becomes the easiest exit in the app
— easier than RETURN is honest. Spec: a door-originated entry
point, `requestDisarmChallenge(fromDoor = true)`, that
*always presents the face*. When clean completion is lawfully
available (stopwatch past floor), the face says so in its body
— derived words, e.g. "This session has run its floor. Ending
it now is a completion, not a break." — and its verb is a
full-width slab, END THE SESSION, a deliberate slab-tap
standing between the footer line and the act. When it is not
available, the face is the existing one: consequence line,
`Emergency60sValveView`, KEEP FOCUSING. One face, two lawful
bodies, both derived from the same session facts the VM
already reads at :2236–2240.

**2d. Completion must clear the door.** Today
`completeEmergencyBreakFromValve` (VM :2292) and
`confirmDisarmSession` hide the Disarm state and stop the
shield — and leave `_airlockState.isVisible == true` standing
over a session that no longer exists, its session line still
printing an end instant for a dead session. From the Deck
entry path this was unreachable; from the door it is the main
path. Spec: both disarm completions also clear the airlock
state in the same emission. One line each; without it P-2
ships a door that outlives its session, a Baseline-1 lie in
standing form.

**2e. The exit line is quiet but not small.** Footer tier in
ink (never accent, never a slab — the audit's "a route, not a
spend" stands), but its touch row is 48dp tall, full width,
centred text — the F-4 discipline applied to the exit. Under
urge pressure a 12sp text tap target is a precision instrument
aimed at a shaking hand; quiet is a property of the ink, not
of the target size.

**Spec delta:** one VM entry-point flag + one face body branch;
two one-line state clears; the footer line in the overlay
(48dp row) wired to the new entry point; no new composable, no
new sound (the latch voice `requestDisarmChallenge` already
plays is the turn's voice).
**Price: SMALL** (2c's face branch is the only new rendering).
**Risk:** the Disarm card over the airlock card is two cards
stacked visually. Mitigation already in the tree: the Disarm
overlay's ground is `NightColorBackground.copy(alpha = 0.94f)`
(:745) — the door beneath is dimmed to a silhouette, which
reads as depth, not clutter. If Schliff judges the stack
muddy on device, the fallback is a 0ms cut (no fade) between
faces inside the same full-screen ground — same wiring, the
airlock's exit fade suppressed only for this transition.

---

## P-3 — TURN THE KEY: THE PEEK IS TAKEN BY A HOLD, AND THE LEASE SHOWS ITS TEETH
### Attacks (c): make the 2-minute peek physical and unambiguous

FIX-8 kills the dead words and re-times the price. What
remains abstract is the *act*: a chip appears and a tap spends
a lease. A tap is also what accidents are made of — the same
travelling thumb P-1 protects RETURN from can land on the chip
in the slot above it. The lease deserves a verb with a body:

**3a. Hold-to-take, 600ms, filled by the hand.** The peek chip
becomes a door-local composable (not a fork of the shared
`BlockitActionChip`, :170 — the shared chip stays tap-only for
every other face): press-and-hold fills the chip left-to-right
over 600ms; release before full drains the fill in 120ms and
books nothing; reaching full fires `start2MinutePeek()` — whose
grant-time re-derivation (acquitted, VM :2393) remains the
final word, so a hold completed into a changed world still
cannot sell a dishonoured lease. The fill is the statement of
intent rendered in time: at every instant the man can see
exactly how much of the act he has committed and that letting
go un-commits all of it. Detents at the fill's quarters reuse
`playRotaryDetent` (the engine's existing voice — the pill
already detents at 760Hz on its 15-second marks, VM :991); the
completed turn lands on `playMechanicalLatch(true)`, the sound
`start2MinutePeek` already plays. No new audio machinery, no
new sound design. Microcopy under the chip, replacing nothing
in FIX-8's price line: "HOLD TO TAKE THE PEEK" — an
instruction, present only while the offer stands.
TalkBack: the hold carries a custom accessibility action
("Take the peek") so the gesture is never the only door.

Why 600ms: long enough that no travelling thumb, pocket press,
or F-2-class accident completes it; short enough that a man
who means it never experiences it as a second wall — the wall
already happened; this is the *signature*, not another queue.
It also gives the door a grammar of escalating physical
commitment that matches the machinery's own prices: RETURN =
a tap (free, encouraged), PEEK = a 600ms hold (a small lease,
a real price), END THE SESSION = a 60-second valve (the
week's only break). Three acts, three weights, each weight
proportional to what the act spends. That proportionality is
the physical unambiguity the brief asks for — a man can feel
which door he is opening by how it opens.

**3b. The lease prints its end, not its length, wherever it is
carried.** The badge fix (FIX-8, m+s honestly) governs the
chip. Extend the same law to the lease's own life: the pill
state (`FloatingPeekPillState`, VM :113) is derived from the
row's `activePeekUntilElapsedRealtime` by the VM ticker — but
in this tree **no composable consumes `floatingPeekPill`**
(grep over `MainActivity`, screens, and components returns
only the VM). Flagging for Tor rather than asserting: if
Pastille's pill face is the renderer and lives outside this
tree, 3b is a spec handed to that lane; if nothing renders it,
a taken peek currently runs *faceless* — two minutes inside
the blocked app with no visible clock and no early-end verb
on screen — and the minimal pill becomes part of this
proposal's price. The pill spec, either way: Anonymous Pro
countdown derived by `peekSecondsRemaining` from the stored
lease end (never counted — the doctrine at VM :180–188), the
app's name, and one verb, END PEEK NOW → `end2MinutePeekEarly`
(VM :2410). One truth the pill must print, because the
machinery makes it true and a man deciding whether to end
early deserves to know it: `recordPeekEnded` (REPO :1217)
re-anchors the cooldown to the *actual* end instant — ending
early starts the same 20 minutes sooner; it does not shorten
them, and it does not refund the lease's unused seconds. The
pill's footer line, derived from `PEEK_COOLDOWN_MILLIS`:
"ENDING NOW STARTS THE SAME {n} MINUTES" — wait, precisely:
the cooldown is {n} minutes from whenever the peek ends, so
the honest line is "THE {n} MINUTES START WHEN THE PEEK
ENDS" — the same sentence FIX-8 puts on the offer, carried
into the room where the decision is actually made.

**3c. The break appointment gets a body too.** FIX-8 ships the
Errand's kernel: on PEEK-tier doors the next break's instant
may print. An instant alone is a timetable entry. Give it the
door's physical grammar: it prints in the transom slot's
family (same slot discipline as P-1, withheld-branch height),
prefixed by the break's own fact — that at that instant this
app *opens by the session's own law*, no lease, no cooldown:
derived words along the line of "AT {HH:mm} THE BREAK OPENS
THIS APP — NO PEEK SPENT". Every clause is checkable against
the machinery (Safe Break tier law: PEEK apps open in breaks;
a break opening is not a peek and books no cooldown). It
converts the craving's false binary (peek now or nothing)
into a true ternary the man can weigh: return, lease, or
wait for the door the session already built.

**Spec delta:** one door-local hold chip (pointerInput
press handling + fill rendering + a11y action); pill spec as
above; one derived line for the appointment.
**Price: SMALL–MED** for 3a + 3c; **+MED if the pill face must
be built** (3b's flag resolves which).
**Risk:** hold-to-take is the one proposal here that changes
a ruled surface's interaction (the chip was a tap in every
filing). It does not touch the offer's *existence*, timing, or
price — only the taking — and RETURN's tap is untouched. If
Tor judges added friction on the peek as scope beyond the
audit, 3a detaches cleanly: 3b and 3c stand without it.

---

## P-4 — EVERY PRINT A PURE FUNCTION: THE DOOR'S TRUTH, MADE TESTABLE
### Attacks (d): unmistakable truthfulness as structure, not review

The audit's FIX-5 anchors the cooldown to its end-mono and
FIX-8's taper adds a count. Both are still *procedures a
reviewer must trust*. The tree already contains the stronger
pattern in three places — `peekOfferFor` (pure, unit-tested
door by door), `peekSecondsRemaining` (derived from the stored
end, VM :186), `seamMarksFor` (a total pure function whose
branch a merge pass once deleted and a test caught). Extend
that pattern until the whole face is covered:

**4a. `doorPrints(state, nowMono)` — one pure function returns
every string the door prints.** Session line, gate line,
withheld line, price, badge, taper count line, appointment
line. The state carries *ends*, not counters:
`wallEndsAtMono`, `cooldownEndsAtMono` (FIX-5b's cure, applied
to the wall too per FIX-5's own F-1 twin note), session end
millis as today. The overlay becomes a dumb renderer of the
function's output — it owns layout, ink, and motion, and no
arithmetic. Then the Held Door Truth Baseline becomes a JUnit
matrix, not a prose law: for every combination of path × tier
× cooldown × pill × token/session state, assert no print
states a remaining duration, no withheld door prints an offer,
no offer prints without its price, "AGAIN IN 0 MIN" and its
cousins are unrepresentable, and the badge equals the lease
seconds the repository will grant. A future merge that breaks
a Baseline law fails a test before it fails the man.

**4b. The taper's count, derived from the only honest
source.** FIX-8's wall is `5 + this session's booked
intercepts of this package`. The obvious implementation is a
trap: `BlockedAppEntity.interceptCount` is a **lifetime**
counter (`recordIntercept` increments it on every booking,
REPO :2111) — printing "THIRD TIME TONIGHT" from it would be
a lifetime total wearing tonight's clothes. The truthful
derivation already has a precedent in the tree: the receipt
assembly counts deflections from `getInterceptLogsBetween`
over the session's wall window (REPO, `assemblePendingReceipt`).
Spec: the door's count = intercept logs for this package with
`timestampMillis >= activeSession.startWallTimeMillis`,
read at `triggerAirlockIntercept`; the same number drives
both the wall length and the printed count — one derivation,
two prints, incapable of disagreeing. (The 30s-per-package
dedupe the receipt applies is the receipt's law, not the
door's: the door counts arrivals, and says so by printing the
count beside the gate, where its meaning is the visit's
order tonight.)

**4c. A nonce per arrival.** Every remember and LaunchedEffect
in the overlay keys on `(interceptedPackageName, isVisible)`
(:562, :577). A re-fire for the same package while the door
already stands — or a second intercept landing through FIX-4's
new onNewIntent route — can inherit the previous visit's
countdown at 0: a fresh arrival facing no wall, its taper
never applied, because Compose sees unchanged keys. Spec:
`triggerAirlockIntercept` stamps `interceptNonce =
SystemClock.elapsedRealtime()` into the state; all keys
include it. Every arrival pays its own (tapered) wall —
which is precisely the taper's promise, and without the nonce
the promise has a hole exactly where repeat craving lives.

**4d. The door shows its warrant only when it cannot show a
clean one.** The Deck's doctrine (imprint tenant, UNA2-04):
when live and frozen agree, no qualifier prints — a standing
qualifier teaches the eye to skip it. Apply it here: at
trigger time the VM already can read
`repository.standingWarrant()`. When the warrant is LIVE, the
door prints nothing about itself — silence is the held bind's
signature. When it is not (UNPROVEN / OFF / stale per
`WatchStore.warrantStaleForPrint`), the door's footer tier
carries the seam's left mark for that warrant
(`seamMarksFor(warrant, nowMono)`, the existing pure
function): a door standing on an unproven bind says so on its
own face instead of printing "SESSION ENDS {HH:mm}" in the
voice of a machine that will certainly hold. This is the one
place the door speaks about *itself*, and it speaks only to
confess. Price is one state field + one conditional Text.

**Spec delta:** state gains three fields (two end-monos, one
nonce) + warrant snapshot; overlay's two LaunchedEffect
counters deleted in favour of derivation at recomposition
(the ticker demoted to a redraw trigger, per FIX-5b); one
pure function + its matrix test; one DAO call site for the
session-window count (the query exists).
**Price: SMALL–MED** — mostly movement of existing arithmetic
into one audited place.
**Risk:** recomposition-derived prints need a per-second
redraw trigger while the door stands; the VM's existing
1-second ticker loop (VM :958) is the natural metronome and
already drives the pill by the same doctrine. No new clocks
are introduced — that is the point.

---

## P-5 — THE HOMECOMING: THE DOOR REMEMBERS THE LEASE IT GRANTED
### Attacks (d), and the seam between (c) and the next visit

When a peek lease expires while the man is inside the app,
the machinery re-intercepts him: the a11y service sees a
PEEK_2M package with `nowMono >= activePeekUntilElapsedRealtime`
(REPO / service :228, :284) and fires the door again. Today
that arrival is indistinguishable from a fresh craving — same
badge, same wall, same accusation. It is not the same event,
and the door *knows* it: the row still carries the lease's
end. A door that cannot tell "caught reaching" from "the
lease I granted just ended" mis-describes the man to himself
at the exact moment the machinery worked perfectly.

**Mechanism:** at `triggerAirlockIntercept`, derive
`justExpiredLease = entity.activePeekUntilElapsedRealtime > 0
&& nowMono - entity.activePeekUntilElapsedRealtime <= 15_000`
(the field's semantics support this: `startPeekLease`, REPO
:1194, pre-schedules `lastPeekEnd = leaseEnd`, and after
natural expiry both fields stand at the lease's end until the
next lease or `recordPeekEnded` rewrites them; an early-ended
peek zeroes `activePeekUntil`, so the early-end path never
masquerades as an expiry). On a homecoming arrival:

- The badge line's fact changes: the door prints, in the
  withheld zone's slot and grammar, "THE PEEK ENDED" — past
  tense, a report, not a refusal. The cooldown line follows
  it in the same visit (the cooldown is now running; FIX-5's
  derived print states it) — the two lines together are the
  whole truth of the moment: the lease ran, it ended, the
  price is now counting.
- The wall for *this* arrival is the taper's floor with no
  count increment: the expiry echo is not a new reach, so
  4b's count must exclude it, or the taper punishes a man for
  the lease's own clock. Spec: the homecoming intercept books
  outcome "Peek Expired" (a new outcome string — the ledger
  gains a true word; downstream, Lade's Drawer crew should
  note it never counts as an urge stopped, answering part of
  rider C-13's family) and 4b's count query excludes that
  outcome.
- RETURN is untouched, live from frame one, as always.

**Price: SMALL** (one derivation in the VM, one print branch,
one outcome string, one exclusion in 4b's query).
**Risk:** the 15s window is a judgement call — wide enough to
cover launch latency (FIX-7a's BACK + launch path), narrow
enough that a genuine re-reach a minute later reads as what
it is. If the window mis-fires, the cost is a kind badge on
one visit, never a granted lease: `peekOfferFor` is untouched
and the cooldown still governs the offer.

---

## WHAT I AM NOT PROPOSING, AND WHY

- **No negotiation at the door** (the audit's P-6 strike is
  right): nothing here lets the craving second bargain with
  the seal's length. The hold in P-3 spends nothing but the
  man's own 600ms.
- **No dictation, no chips, no survey** (P-4-slip and FIX-3's
  struck chip row stand): every proposal above works with
  zero input beyond the three acts the door already has.
- **No new sounds, no new colours, no green, no imagery.**
  Every voice named already exists in `TactileSoundEngine`;
  every surface named is an existing Night token.

## PRICE SUMMARY

| Proposal | Price | Depends on |
|---|---|---|
| P-1 Born-occupied slot + stationarity proof | SMALL–MED | FIX-6 |
| P-2 The door turns (honest exit) | SMALL | FIX-8's exit line |
| P-3 Turn the key (hold-to-take + lease prints) | SMALL–MED (+MED if pill face must be built) | FIX-8; 3b pending Tor's pill check |
| P-4 Every print a pure function | SMALL–MED | FIX-5 |
| P-5 The homecoming | SMALL | P-4b's count |

---

## NEXT (Law 10)

(a) **What I want to do next:** spec P-2's door-originated
Disarm face to buildable detail — the two lawful bodies
(valve / past-floor completion) as exact derived wordings in
Hale's register for Tor's ruling, since 2c is the proposal
most likely to ship and its words are the only part of it
that can still lie. In the same pass, resolve P-3b's flag:
hunt the merged tree once more for any pill renderer outside
the paths I grepped, and report to Tor whether the peek
currently runs faceless or Pastille's face carries it.

(b) **What I could do, unassigned:** carry P-1c's
stationarity-proof harness to the Disarm face and the desk
clock (the other two full-screen faces whose content arms
mid-visit), and draft the `doorPrints` matrix (P-4a) as a
standalone test spec the builder can lift without reading
this file — the Baseline as an executable document, ready
for whichever cell builds FIX-5.

— Innovator, Tor's cell, issue #11. Report to Tor; nobody
compiled; nothing here messages the user.
