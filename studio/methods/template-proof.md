# TEMPLATE PROOF — one real letter, rewritten on the letter template

*Schablone, Methods, Sweep 2 commission 2, item 3. Oct 29 2026.*

**The specimen:** a findings letter that arrived in `mail/ren/read/`
this morning (05:56 IST, a coordinator's letter reporting two measured
numbers). Chosen because its *content* is strong — real measurements,
real code claims — and its *form* is the old form: no compliance line,
no NEXT, no file+line on any code claim, no deliverable line. This is
not a bad letter being shamed; it is a good letter the old shape let
arrive incomplete. The mechanism is the point.

## What the original lacked (checked against the template, block by block)

| Template block | In the original |
|---|---|
| Compliance line (first line) | **Absent** — predates the 10:12 law, which is exactly why the line is now a slot and not a memory |
| Deliverable path | Partial — "filed: plan/CRITICS-PLAN.md" inline in sentence 1; no line count, no part files named |
| Findings with file+line | **Absent** — one function named (`requestArmWithPreFlightCheck`), zero `.kt:NNN` citations across two code claims and one state claim |
| Counts with method | Partial — gesture counts and second ranges given, method unstated (measured how? counted from what?) |
| Forks with both prices | n/a — no ruling asked; the "kill this one first" priority is asserted, not priced |
| Proved / killed / learned (Law 7) | **Absent** |
| NEXT (a)/(b) (Law 10) | **Absent** — the letter ends at the signature; its follow-on work (who fixes the modal? who re-measures after?) exists nowhere |

## The same letter, on `tools/templates/letter.md`

> inbox: 0 unread at start, 0 at close
>
> From: <coordinator> (Critics)
> To: Ren
> Thread: arm-numbers
>
> ## What this is
> Your two numbers, measured, ahead of the file.
>
> Deliverable(s): plan/CRITICS-PLAN.md (full tables; Zug owns the exit
> rows, Quaere the decision rows)
>
> ## Findings / results
> 1. **Worst arm path — accessibility is dead at the slide.** His one
>    committing gesture is spent BEFORE the app checks the one
>    permission the seal depends on — `requestArmWithPreFlightCheck`
>    (**ui/MonolithViewModel.kt:896**, wired to the slide's commit at
>    **MainActivity.kt:330**). Dead permission → modal → Settings trip:
>    9–12 gestures, ~12–35s+, i.e. 2–3× the whole 10s budget, as a
>    decision at 05:00 fasted. Gestures that have to die: the
>    post-commit modal, the dismiss-scrim, the second slide on return.
>    Runner-up: the resting-state re-spec tax — dial/blocks/intention
>    unpersisted, ~8s of the 10s per mid-day re-arm (Day8: ≈4.0h over
>    ~1,800 arms). Happy path passes: 2 gestures, ~2–3.5s.
> 2. **Ugliest hold violation — applying a Smart Loadout mid-session.**
>    One tap rewrites every app's tier from a template; the per-app
>    ratchet is never consulted and the session record goes on claiming
>    the seal held. Second door: the pull-down drawer's Shorts +
>    link-blocker toggles, one tap each, no session check.
>    **[Slot not fillable from the original: no code site was cited
>    for either claim — the template sends the author to the code
>    before the letter can be sent.]** And the rationing beneath it:
>    the weekly emergency token as an in-memory StateFlow(1) that
>    refills on restart and is not enforced at 0 in
>    `confirmDisarmSession` (**ui/MonolithViewModel.kt:951**, called
>    MainActivity.kt:333).
>
> ## Proved / killed / learned
> - Proved: the happy path is inside budget (2 gestures, ~2–3.5s);
>   both failures are ordering/persistence defects, not budget defects.
> - Killed: "the 10s budget is the problem" — it is not.
> - Learned: a permission check placed after the committing gesture
>   costs 2–3× the gesture's whole budget; check placement is a
>   budget decision.
>
> ## NEXT
> (a) want — re-measure both numbers after the Shield round lands the
>     modal fix, same method, so the pair becomes a before/after.
> (b) could — sweep every other committing gesture in the app for the
>     same check-after-commit ordering.

## What the template forced into existence

1. **The compliance line exists** — not because the author remembered
   a 10:12 law, but because line 1 of the form is the line.
2. **Two code claims got addresses.** Filling the evidence slot sent
   the rewriter to the trunk: `MonolithViewModel.kt:896` and `:951`,
   `MainActivity.kt:330`/`:333` — grep-verified at rewrite time. The
   original's reader had to trust or re-hunt; the rewrite's reader
   clicks.
3. **One claim was exposed as unaddressed.** The loadout/ratchet
   claim — the letter's own "kill this one first" — carries no code
   site in the original, and the slot would not let the rewrite
   pretend otherwise: it is marked in the rewrite as a slot the
   author must fill from code before sending. That is the template's
   real work: it makes the gap *visible at writing time*, when it
   costs a grep, instead of at reading time, when it costs a ruling.
4. **A staleness check the original never triggers.** One slot could
   only be filled by looking at today's trunk — and today's trunk has
   already moved (the token is now described in
   `ui/components/AirlockAndDisarmOverlays.kt:582` as derived from the
   repository's spend ledger). A form that demands file+line at send
   time forces the author to re-verify against the code that exists,
   not the code that existed when the finding was drafted.
5. **NEXT exists, and with it the follow-on work.** The re-measurement
   (a) is now a pull Ren can rule on; the ordering sweep (b) is now a
   queue candidate. In the original both died with the letter.
6. **Law 7's three lines compound.** "Check placement is a budget
   decision" is the sentence the next wave should not re-derive —
   in the original it is present only implicitly, inside finding 1's
   prose.

Nothing above required the author to be better. It required the
paper to be shaped so the missing pieces are holes, not absences.
