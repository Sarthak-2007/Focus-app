# STURM — CRAZY IDEAS for #12 (Tasks + F-1)
*Seat: Sturm, crazy-ideas, dedicated-100 programme. Filed on branch agent/issue-12 for the Amt cell. Every idea is concrete enough to build; machinery prices are named where they exist. Bindings honoured: no green, no AI images, Tasks never asks duration, Monolith is a blocker not a tracker (nothing here scores, totals, or stores history).*

The queue is not a list. It is a PAD OF LEAVES that tomorrow eats from. F-1 already made the queue feed routine-born sessions (head task title seals as intention, snapshot at seal instant, DEFERRED never headlines, SLEEP takes the routine name). These ideas make that feeding VISIBLE, PHYSICAL, and CUEING — the user should be able to *see* the queue working before it works.

---

## S12-1 · THE GHOST LEAVES — tomorrow's routines queue behind the pad
**What:** On the Tasks screen, behind the head leaf, render faint ghost leaves — one per routine that will fire before the next queue edit horizon (e.g. tomorrow's routines), in firing order, each printed with the routine's name in outline type. The pad visibly has a second queue leaning on it.
**The crazy part:** Tapping a ghost leaf shows a live projection: *"If FASTED BLOCK sealed right now, it would be called: <head task title>"* — F-1 rendered as a preview, computed by running the derive projection at render time with the current head. The user watches the queue feed the routines *before it happens*, and understands instantly why tonight's ordering matters.
**Build:** Read-only. Reuse the ACTIVE-else-first-QUEUED projection (MonolithViewModel:317 / MonolithDao:171 ordering) + routine schedule read. No writes, no schema. One new composable (ghost leaf) + a tap-to-project line.
**Why it earns its place:** F-1 is currently invisible machinery — its failure mode (1 AM queue, 05:00 session titled "FASTED BLOCK") was only found by a human walking it. Ghost leaves make the mechanism self-explaining forever.

## S12-2 · THE WAX SEAL — sealing gets a physical mark
**What:** At the instant a routine session seals carrying a task's name, the head leaf receives a wax stamp: a #C84481 disc pressing in with a 180 ms squash, the leaf's top corner folding under it. While that session runs, the leaf wears the seal. When the session ends, the seal cracks (two-frame split) and fades.
**The crazy part:** The seal is the SAME glyph the Session Room banner carries next to the intention (see #13 companion filing) — one mark, two surfaces, so the user learns: stamped leaf ↔ named room. The queue and the room share a physical object.
**Build:** Seal state is derivable live (a running session whose intention equals the head task's title) — zero schema for the basic version. Optional phase-2: persist `sealedAt` for the crack animation after process death. Animation: one Canvas stamp + corner fold path.
**Binding check:** A seal is a state, not a count — it never accumulates, never scores. Blocker, not tracker. ✓

## S12-3 · THE HEAD LEAF LEANS OUT — morning cueing
**What:** If the queue was edited after 20:00 and no session has run yet today, the first time the Tasks screen (or the idle Desk face, via the banner fallback chain) is seen in the morning, the head leaf is already HALF-TORN: translated down 40% along its perforation, oversized by one type step, everything below it dimmed to 0.6.
**The crazy part:** The pad greets you mid-gesture — the tear you started last night by ordering the queue is already in progress; finishing it (the tear-to-start drag, S12-4) takes one short pull instead of a decision. Cueing as *interrupted physical motion*, not a notification.
**Build:** Condition = (lastQueueEdit > yesterday 20:00) && (no session sealed today) — both facts exist. One initial-state offset on the head leaf composable, cleared on first interaction.

## S12-4 · TEAR-TO-START — the leaf tears into the room
**What:** Starting work from the head leaf is a tear, not a tap: drag the head leaf down past its perforation line (the tear rule's notches are the perforation). Haptic zip (staggered 8 ms pulses), the leaf separates, and the torn leaf IS the transition — it full-screens into the Session Room carrying its title as the banner.
**The crazy part:** The task does NOT leave the queue (blocker, not tracker — nothing is consumed). Instead the leaf in the pad now shows a TORN EDGE (jagged top border) for as long as its session runs. End the session, the edge re-perforates smooth. The pad shows its wound while you work.
**Build:** Drag gesture + perforation threshold on existing leaf; torn edge = one jagged path border, state bound to "session running with my title". No schema, no consumption semantics — sealing still never dequeues (F-1 guarantee untouched).

## S12-5 · THE ENVELOPE — DEFERRED made physical
**What:** At the foot of the pad sits an envelope. Deferring a task slides the leaf INTO the envelope (it visibly fattening by one leaf-thickness per defer). Un-deferring slides it back out onto the pad's tail.
**The crazy part:** This is why DEFERRED never headlines, made physical: the leaf is not *in* the pad, it's in the envelope — of course it can't name a session. The F-1 exclusion rule stops being a spec line and becomes a thing you can point at.
**Build:** Envelope = one composable at list foot; defer/undefer animations are layout slides the list already almost has. Envelope thickness = deferredCount × 2 dp, capped.

## S12-6 · SOLID vs HOLLOW — the room tells you who named it
**What:** Session intentions print in two typographic voices: a session named by the user's own task prints SOLID; a session that fell back to the routine's name (empty queue, DEFERRED-only queue) prints HOLLOW (outlined type). One glance across the room: *I named this* vs *the machine named this*.
**The crazy part:** Truth-telling (TELL-THE-TRUTH standard) as typography. It also creates the gentlest possible nudge: a hollow morning is visible feedback that last night's queue was empty — no notification, no guilt copy, just outline letters.
**Build:** Needs provenance at seal: an `intentionSource` (TASK / ROUTINE) carried on DesiredSession through materialiseRoutineSession. One enum, no user-facing schema semantics change; flagging honestly: this is a small machinery ask beyond F-1's no-schema pass — priced here for Ren's ruling.

## S12-7 · THE HERALD — the queue knocks 30 minutes early
**What:** Thirty minutes before a routine window opens, if a head task exists, the Desk/idle face banner gains a herald line under the routine name: `NEXT · <head task title>`. At seal, the herald line is replaced by the sealed intention itself — same position, same type, the promise becoming the seal in place.
**The crazy part:** The queue gets a voice exactly once per routine, at the moment of anticipation, in the room the routine is about to become — then never speaks again. Cueing with a curfew.
**Build:** Herald state = (next routine within 30 min) && (head exists). Pure projection over data the heartbeat already assembles. Banner slot exists (fallback chain task → intention → routine name → absent; herald inserts as a pre-seal rung, flagged for Wacht's §C/banner discipline on #13).

## S12-8 · TICKET-PUNCH COMPOSER — adding a task prints it
**What:** The composer keeps asking nothing (no duration, ever). But ADD is a print event: the new leaf feeds from the top of the pad like a ticket from a slot, its title landing letter-by-letter in Anonymous Pro (12 ms/char teletype), then the whole leaf settles into Atkinson Hyperlegible as it drops to the tail with a paper flutter (2-frame skew).
**The crazy part:** Two typefaces in one leaf for 600 ms — the machine voice handing over to the reading voice. Adding tomorrow's work feels like punching a ticket you'll tear tomorrow.
**Build:** Staggered char reveal + typeface crossfade on the leaf's entry animation only. No data change.

## S12-9 · THE TYPE-WEIGHT ECHO — position is the only number
**What:** The queue prints in a descending type-weight ramp: head leaf Sora SemiBold, next Medium, next Regular, tail Light — with a matching rule-line weight ramp. No counts, no totals, no minutes: the queue's *shape* is readable from across the room, and reordering visibly re-weights the pad.
**The crazy part:** Combined with the killed horizon bar (AUDIT-tasks item 1 — the chart of a dead quantity), this is its honest replacement: proportion without a single fabricated number. The pad tapers like a held breath.
**Build:** Weight/alpha indexed by position in the existing list item. Zero machinery.

## S12-10 · THE STITCH — one binary mark, never a count
**What:** A leaf whose task headlined a routine session today carries one small stitch mark (#E47E35, 6 dp dash) at its perforation. Binary: sealed-today or not. It clears at midnight. It is never counted, never summed, never shown anywhere else.
**The crazy part:** It's the anti-streak. The pad remembers *that* the queue fed the day — physically, like a thread through the leaf that did the work — and deliberately refuses to remember how often. Built to be un-gameable: there is nothing to optimise.
**Build:** Needs last-sealed date per task — the one small persistence ask in this set (one nullable date column or a daily-reset in-memory + boot re-derive from today's sessions). Priced openly; if Ren refuses schema, S12-2's live-derived seal already covers the running case and this idea dies cleanly.

---
*Filed by Sturm. Take any, kill any — but the ghost leaves (S12-1) are the one I'd fight for: F-1 deserves to be seen working.
— Sturm, crazy-ideas*
