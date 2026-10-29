# TOKEN-SPEC — Freya's review (overlay-surface lens)

*Phase 3. Reviewer: Freya, Maximum-Implementable, session/overlay seat —
owner of the valve × overlay buildspec (mail/ren/0527__from-freya__valve-overlay-buildspec.md,
converged with Ines). Charged on TOKEN-SPEC §§4.5/5/7 against that buildspec,
MASTER-PLAN §0/§0.1, and the shipped code (AirlockAndDisarmOverlays.kt,
MonolithViewModel.kt, BlockitSuitcaseAndSwitchesViews.kt — all re-read for
this review). Four charges; the fourth is mine, found in the valve view.*

---

## Charge 1 — The copy conflict

**RULING: BLOCKED (TOKEN-SPEC §7 as drafted). The buildspec point-2 copy
stands verbatim, every word. §7 is rewritten as the rule underneath it —
amendment text below, exact.**

### Why the buildspec sentences carry it, not §7's

1. **Provenance.** Point 2 is not my draft — it is Ines's text (her letter
   to me, thread disarm-overlay-host; her Element 4), adopted by the host
   *exactly*, converged between the valve's author and the overlay's
   author. §7 is a single-author coordinator draft that silently revises
   converged text. A spec whose §0 promises "every rule below is decided"
   does not get to un-decide another seat's converged surface in a
   subordinate clause ("Freya verifies the overlay against this section" —
   verified; it fails).
2. **The buildspec copy is a layout, §7's is a paragraph.** Point 2 assigns
   one job per element, in fixed positions: title `EMERGENCY BREAK` ·
   body (consequence) · valve (the count, valve-drawn) · rule line *under*
   the valve (mechanics) · chip `KEEP FOCUSING`. §7 drafts one body
   sentence that tries to do consequence + count + mechanics at once.
   Taken literally by a builder, §7 preserves the shipped card's shape —
   eyebrow + one paragraph + valve (AirlockAndDisarmOverlays.kt, the
   current `DisarmChallengeOverlay`) — and the title element and the
   under-valve rule line never get built. The conflict is structural,
   not stylistic.
3. **§7's at-1 sentence is less true than the text it replaces.** "Hold
   the valve for 60 seconds in total — progress banks every 15 — to spend
   it" omits the slip entirely. Banking without drain reads as *release
   is free*; the rule line's "If your hand slips, what isn't banked
   drains away. The banked part stays." is the half of the mechanic that
   makes banking honest, and it is the half §7 drops. §7 also spends the
   ledger's verb on the user — "to spend it" is spec language; the
   surface speaks human (MASTER-PLAN §0, standard 3). The buildspec body
   says what it *does to him*: "This ends the session early."
4. **§7's at-0 sentence amputates the HOLD sentence.** "This week's
   emergency break is spent." — full stop — leaves the refused man with
   an error state and an unanswered next question. The buildspec's second
   sentence answers it in the same breath: "Sessions end when their time
   does." That sentence is not warmth; it is the lock restating itself at
   the exact moment it is tested. It stays.
5. **What §7 is *for* is real, and none of it needs new sentences.** Its
   two true claims — (a) every rendering of the count traces to
   `remaining()`, (b) the shipped "hold continuously" copy is false and
   dies — are rules about provenance and deletion. Rules belong in §7 in
   rule language, pointing at the copy's home, not competing with it.

### Amendment — replace TOKEN-SPEC §7 in full with:

> ## 7. Surface contract (what the screens may say)
>
> - Every rendering of the count traces to `remaining()` — the Deck chip
>   (`$n/1 WEEK`), the disarm overlay, the valve. One number, one source.
>   The count is a rendering of repository truth; no surface holds,
>   decrements, or refunds it.
> - The overlay's sentences are Freya's buildspec point 2, verbatim, and
>   are incorporated by reference, not restated: title `EMERGENCY BREAK` ·
>   body at 1 remaining: "This ends the session early. It is the only
>   break this week." · body at 0: "This week's break is already spent.
>   Sessions end when their time does." · rule line under the valve:
>   "Hold to fill 60 seconds. Every 15 seconds banks. If your hand slips,
>   what isn't banked drains away. The banked part stays." · chip
>   `KEEP FOCUSING`. This spec changes no word of them, and no builder
>   copy is sourced from this section.
> - The shipped body sentence ("Sessions are unbreakable by tap. You have
>   N weekly emergency break token remaining. Hold the physical valve
>   continuously for 60 seconds to abort.") is false twice over —
>   continuity died with the banked verdict, and it presents the token as
>   a thing the UI holds — and is deleted wherever it appears. The words
>   "continuously" / "continuous" may not appear in any valve or overlay
>   copy. The count leaves the body with it: the body's job is
>   consequence, the valve's job is the count (valve-drawn, per buildspec
>   points 2 and 8), the rule line's job is mechanics. One job per
>   element.
> - The Ledger's rendering of a spend is specified in §4.5 as amended by
>   Freya's Charge-2 ruling (record in the tab; no finish receipt for a
>   broken session). A week in the rebuilt Ledger shows the spend as a
>   fact of that week (or its absence as silence — UNSPENT is a word for
>   the DAY CLOSED frame per Humans, not a row of its own).

---

## Charge 2 — The receipt conflict

**RULING: BLOCKED (§4.5's and §7's "finish receipt" clause). Exit-form and
record are separable; the record stands in full, the receipt clause falls.
Buildspec point 6 stands: no receipt for a broken session.**

### The separation, stated precisely

Two different objects are being conflated under one word, "receipt":

- **The record** — durable, re-readable, at rest. A row (the spend, §2)
  joined to its session row, rendered in the rebuilt Ledger tab, the
  record's home (MASTER-PLAN §0.1-6: "the tab is the record's home, the
  finish receipt is a moment"). §4.5's core demand is this, and it is
  *right*: the break must be renderable from the spend row by
  `sessionId`, never from string-matching `outcomeReason`. I endorse
  every word of that pipeline.
- **The exit form** — transient, once, on the Deck at the session's end.
  My §4 machine (maximpl-parts/10-freya-session.md): stillness 400ms →
  one pulse (`playCompletionPulse`) → a full-screen face that *stays
  until tapped*, triggered only by the state transition to
  `completedSuccessfully`. Point 6 of the buildspec: a valve break exits
  `fadeOut(specA)` straight to the idle Deck, seal(false) its only sound.
  Completion and emergency never share this form — sound, stillness,
  face, dismissal, all of it.

A pipeline is not a rendering. §4.5 can have its join, its constant, its
one definition — and the broken session can still exit with no ceremony.
Nothing in the pipeline *requires* the moment.

### Why Nox C-07 does not carry the receipt anymore

C-07 was ruled under the demote-era verdict, where the record had exactly
two legal forms — the finish receipt and the weekly line — so a
valve-ended session excluded from the receipt was indeed rendered
*nowhere*, and Nox's charge ("the one whose truth most needs stating
once, rendered nowhere") was correct *on those premises*. The premises
are dead. Sarthak kept the tab (05:41, MASTER-PLAN §0), §0.1-6
superseded the two-form condition in the open, and the tab is now a
standing home where the partial can be — must be — stated. TOKEN-SPEC
§4.5/§7 as drafted renders the break in the receipt *and* the Ledger:
that is the fact in two homes, the exact violation C-07's own table
exists to kill. The spec cannot cite one-fact-one-rendering as its
authority for creating a second rendering.

### The strongest case, plainly

1. **A receipt is a reward shape, whatever its ink.** DONE word, hero
   numeral, stillness, a face that stays until tapped so it can be
   kept — §4 rations that ceremony to completion *because* it is the
   session's only one. Issue it for a break and the app teaches, at the
   tiredest moment of the week, that breaking and completing are the
   same kind of ending with different footnotes. "Same ink" is not the
   defence of C-07's wording (`BROKEN AT 31 MIN · HOLD SPENT`); it is
   the mechanism of the harm. The partial deserves a *record*, in the
   Ledger's ink, at rest, on Sunday's terms — not a *keepsake* on the
   Deck at 31 minutes.
2. **Precedent already in the system: grace cancel.** A grace-cancelled
   session is recorded (session row, its one line, real elapsed — the
   Zug V13 settlement) and receives no receipt, and nobody has ever
   called that record a lie. Record-without-receipt is the established
   form for a session that ended by an exception path. The valve break
   is the second exception path; §4.4 of TOKEN-SPEC already keeps it out
   of the token — keep it out of the ceremony on the same logic.
3. **The trigger is a state, not a path.** My §4.3 keys the receipt on
   `completedSuccessfully` precisely so no disarm path can wander in.
   §4.1 of TOKEN-SPEC books the break with `completed = false`. The
   spec's own transaction already classifies the ending as not-a-
   completion; its §4.5 then asks the completion ceremony to print it.
   The spec disagrees with itself; the transaction is right.
4. **What the man is owed, he still gets.** The Ledger session row for a
   valve-ended session prints its break line from the spend row —
   elapsed attended time (already booked by `disarmActiveSession`'s
   Partial Stamp, §4.3's own note) + the spend fact. C-07's sentence
   survives with its home corrected: `BROKEN AT {elapsed} · HOLD SPENT`,
   in the tab, from the join. Truth stated once, in its one home.

### Amendment — in §4.5, strike and replace

Strike: "The finish receipt and the rebuilt Ledger render the break from
this table" and §7's "The receipt for a valve-ended session prints its
break line from the spend row (§4.5)."

Replace §4.5's rendering sentences with:

> The spend row **is** the record — `sessionId` is the join. The rebuilt
> Ledger renders the break from this table, never from string-matching
> `outcomeReason`: a valve-ended session's row in the tab carries its
> break line (`BROKEN AT {elapsed} · HOLD SPENT`, elapsed from the
> session's booked attended time) sourced from the spend row. This
> satisfies the outstanding cross-file charge (Nox C-07) as re-homed by
> the KEEP-THE-TAB verdict (MASTER-PLAN §0.1-6): the partial is stated
> once, in the record's home. The finish receipt does not render it —
> the receipt's trigger is `completedSuccessfully` only (Freya §4.3),
> a valve break is booked `completed = false` (§4.1), and completion
> and emergency never share an exit form (buildspec point 6). The
> outcome-reason constant for this path is exactly `"Emergency Break"`
> (`EMERGENCY_BREAK_REASON`), replacing the free-text reason the overlay
> currently passes for valve completions.

**If you overrule me, Voss, price it honestly in the fold-in:** you are
not adding a line, you are merging two exit forms — and the merged form
must then surrender the receipt's stays-until-tapped dismissal, its
pulse, and its DONE word for broken sessions, at which point what remains
is a Ledger line shown early, i.e. this ruling with worse timing. I ask
for the ruling above, not the compromise.

---

## Charge 3 — Owner-split integrity

**RULING: STANDS-WITH-AMENDMENT. The split is clean in principle —
bank = attempt state in the ViewModel (`DisarmChallengeState.bankedSeconds`),
token = ledger fact in the repository, grip = view-local — and §5's
deletions are the right ones (the VM flow dies as a source of truth;
`confirmDisarmSession` never touches the token again). But §5 as written
leaves three holes exactly on my surface, each verified against the
shipped code. Amendments A–C are required lines, exact text below.**

### Where the split leaks today (the baseline §5 must close)

Shipped `MonolithViewModel`: `_weeklyEmergencyTokensRemaining` is read
*once*, at `showDisarmChallenge`, and copied into
`DisarmChallengeState` (a snapshot); `confirmDisarmSession` then
decrements the flow itself. §5 deletes the flow and says "the VM exposes
the repository flow as state" — but never says the *state object's* field
stops being a snapshot. If the builder keeps
`DisarmChallengeState.weeklyEmergencyTokensRemaining` written only at
open time, the token lives in two places again — repository (law) and a
frozen Int in the visit (rendering that can lie for the visit's whole
life). Every edge below is that leak, wearing a clock.

### Amendment A — derivation, not snapshot (add to §5)

> `DisarmChallengeState.weeklyEmergencyTokensRemaining` is never written
> at open time. The overlay's state is derived from
> `observeEmergencyBreaksRemaining()` for as long as the overlay is
> visible (the VM combines the repository flow into the disarm-challenge
> state; it does not copy it). A count rendered in an open overlay is
> the repository's current truth or it is not rendered at all.

### Edge 1 — opened at 1, truth is 0; and the Refused landing

This is not hypothetical: a stale emission, a re-collected flow, or a
spend landed between open and completion all produce it, and §4.1 already
decides the law (`RefusedNoToken`, session stays sealed). What §5 does
not decide is the *surface*: it says the overlay "shows the valve's
existing zero-token state (no new UI)". There is a specified state for
this — buildspec point 8, SPENT — but no *transition into it from inside
a live visit*, and my state machine has none: COMPLETED assumes the
overlay closes to the Deck. On Refused it must not close, must not sound,
and must not leave a now-disabled valve sitting under tokens=1 copy.

### Amendment B — the Refused transition (add to §5)

> On `RefusedNoToken` returned to an open overlay, the overlay
> transitions in place to the SPENT state (buildspec point 8): body
> swaps to the tokens=0 copy, the valve renders its spent state and
> refuses touch, the chip remains the only action, and the overlay does
> **not** close. No sound plays — in particular the seal
> (`playHarmonicSeal(arming = false)`) fires only on `Completed`, after
> the transaction commits; a refused break is silent. The attempt's
> bank is neither spent nor cleared by the refusal: it remains the
> session's bank in the ViewModel, frozen behind a disabled valve,
> and dies with the session as buildspec law already provides.
> ("Existing zero-token state" in this spec means the buildspec point-8
> state, not the shipped valve's legacy label.)

### Edge 2 — Monday 00:00 rolls over mid-visit

§4.2 rules the spend books against the *completion* week's anchor — a
ruling that presupposes a visit (and a hold) can straddle the boundary.
But §5's flow sketch ("anchor refreshed when the flow is collected and
whenever the shield/VM re-reads state") guarantees no emission *during*
a long open visit: a man sitting in SPENT from Sunday 23:58 to Monday
00:02 would be shown a spent week that no longer exists — a false
surface, held open by the spec's own refresh rule. And a count change
arriving mid-grip must not yank the grip: the grip is the view's, the
attempt is alive, and the repository transaction — not the rendering —
is what decides the completion (§4.1/§4.2). My states handle a count
that changes mid-visit only if the spec says which changes are visual
and which are deferred to the boundary.

### Amendment C — rollover during an open visit (add to §5)

> `observeEmergencyBreaksRemaining()` must emit on a week-anchor change
> while collected (a midnight tick, not only on collection or re-read);
> an overlay left open across Monday 00:00 must never render last
> week's count. Transitions inside an open visit: if the count changes
> while the valve is not gripped (OPEN_IDLE or SPENT), the overlay
> transitions in place — 1→0 enters SPENT (Amendment B's rendering),
> 0→1 leaves SPENT for OPEN_IDLE with the tokens=1 copy, the valve
> enabled, and the session's bank unchanged (the bank belongs to the
> session, not the week). If the count changes during HOLDING, GRACE,
> or DRAINING, nothing on the surface changes mid-attempt: the attempt
> runs to its own boundary (completion call, abandon, drain to floor),
> and `completeEmergencyBreak()` decides under §4.2. The rendering
> never interrupts a grip.

With A–C, the split verifies: the VM holds attempt state and nothing
durable; the repository holds the ledger fact and nothing about the
attempt; the view holds the grip and nothing else. No state in two
places — including across midnight.

---

## Charge 4 — found collision: §5's "unchanged" valve contract is not unchanged

**RULING: BLOCKED on §5's sentence "The valve view's contract is
unchanged — it still receives a plain `tokensRemaining: Int`." The Int
is not plain. Amendment D required.**

In the shipped valve (`Emergency60sValveView`,
BlockitSuitcaseAndSwitchesViews.kt), the `tokensRemaining` setter is:

```kotlin
var tokensRemaining: Int = 1
    set(value) {
        if (field != value) { field = value; resetHold() }
        invalidate()
    }
```

and `resetHold()` zeroes `heldMs` **and `bankedMs`**, kills holding and
draining, and removes the tick callbacks. Today this side effect is
invisible: the overlay feeds the setter a snapshot once per visit, so it
fires at most once, before any grip. Under §5's live repository flow —
the change Charges 3's amendments make real — *every distinct emission
mid-visit becomes a bank-erasing event*: an anchor tick, a stale-then-
corrected pair of emissions (1→0→1), or Amendment C's lawful 0→1
rollover would wipe the view's banked/held time without a slip, a
dismissal, or any act of his. That breaks converged law three ways at
once: the bank never decreases inside an attempt (Ines); settling on
dismiss goes to the bank floor, never to zero (buildspec machine); and
an attempt must write nothing *and lose nothing* silently (§4.3's
spirit — the record of the valve is binary, and so is its feel). A
rendering input that destroys attempt state is enforcement hiding in a
setter — the same disease §5 cures in the ViewModel, one layer down.

### Amendment D — add to §5, replacing the "contract unchanged" sentence

> The valve view's `tokensRemaining: Int` input survives, but its setter
> loses its side effect: assigning a new count gates touch and rendering
> only, and must never call `resetHold()` or otherwise touch held,
> banked, or drain state. Hold reset is owned by the view's own attempt
> lifecycle (completion, and the session-identity change at a new arm) —
> never by a count rendering. Until this line lands, §5's live flow
> must not be wired into an open overlay: the snapshot-at-open wiring
> it replaces is, on this one point, safer than the flow.

---

## Summary for the fold-in

| Charge | Ruling | What folds |
|---|---|---|
| 1 · Copy | **Blocked** (§7 as drafted) | Buildspec point-2 copy stands verbatim; §7 replaced with the amendment text above |
| 2 · Receipt | **Blocked** (§4.5/§7 receipt clause) | Record stands (spend row → Ledger line, `BROKEN AT {elapsed} · HOLD SPENT`); finish receipt struck for broken sessions |
| 3 · Owner split | **Stands-with-amendment** | §5 + Amendments A (derivation), B (Refused→SPENT in place, silent), C (anchor emission + mid-visit transition rules) |
| 4 · Valve setter | **Blocked** (§5 "contract unchanged") | §5 + Amendment D (count setter rendered pure; reset owned by attempt lifecycle) |

The ledger, the anchor, the transaction, the migration — Voss, that
machinery is right, and none of my charges touches it. My charges are
all on the last centimetre: what the surface says, what ceremony it
performs, and what a number is allowed to do to a hand already on the
valve.

— Freya, Maximum-Implementable (session/overlay)
