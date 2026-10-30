# STAUNEN — THE WOW BAR ON SHIELD (issue #7)

**Seat:** STAUNEN, wow-level designer, dedicated-100 programme.
**Primary issue:** #7 Shield — including the headline item: Shield must say
whether blocking is live.
**Status:** DECLARED — design/judgement only. No app code edited, nothing
compiled, no tests run.
**Read before judging:** `studio/plan/AUDIT-shield.md` (Vogt R-1…R-13);
all fourteen cell filings in `studio/cells/issue-7/`; the landed code —
`ui/ShieldBlockingStatus.kt`, `ui/screens/ShieldMatrixScreen.kt` (status
item, cap wiring, tally, catalogue), `ui/theme/Color.kt`, `Type.kt`;
the wave-2 thread on the issue (Panzer/Saum/Politur/Luft finish specs,
Feile rounds, Blitz, Feder, MIP-04, Intentionality I-3, Nagel's opening).

---

## 0. How this seat judges

Every proposal on this issue is held against the best commercial craft
in the world. The measuring set, named so the cell can argue with it:

| Reference | What it proves | What Shield is measured on |
|---|---|---|
| **Opal** (commercial blocker, the direct rival) | A blocking state can be the calm, beautiful hero of a screen; people pay for that calm | Whether Shield's state reads as designed, not reported |
| **Mullvad** (VPN) | A trust claim as the screen's largest object, evidence printed underneath it, freshness visible | The headline's hierarchy and its proof age |
| **Apple Focus / Live Activities** | State persists, stays glanceable, survives the user leaving and returning | The headline's survival under scroll |
| **Linear / Things 3** | The key fact gets the largest type; a rewrite-everything act always previews or undoes; no footnote ever apologises for a control | The cap, the confession line, the type ladder |
| **1Password / banking apps** | Trust surfaces print when they last checked | Proof freshness |
| **Blockit** | Discipline: nothing spent twice, one glance knows what it is | The house stick — discipline only, never its skin |

Two verdicts are available from this seat: **CLEARS THE BAR** (protect it,
do not redesign it) and **SENT BACK** (specific gap, specific crossing,
priced). "Fine", "consistent", "much better than before" are not
verdicts. A surface can pass every internal ruling in Vogt's audit and
still be sent back here, because the audit asks *is it true and
unduplicated* and this seat asks the prior commercial question: *would a
stranger, from a screenshot, in three seconds, understand it and want
it?*

---

## 1. What already clears the bar — protect these

**1.1 The proof itself. Above commercial.** `computeShieldBlockingStatus`
prints BLOCKING NOW only when both cascade witnesses attest *this*
session id (shield `protecting` flipped for the session + accessibility
armed for the session) and the shield's beat is fresh under the
repository's own 10s gap law. Every commercial blocker in the measuring
set prints its state off *intent* — a toggle, a schedule, a session row.
This cell prints it off *witnesses*. Funke's Standing Conjunction,
landed by Monteur, is the single most above-bar object in the app.
**Ruling: the derivation's logic is not to be redesigned by anyone,
including this seat. All findings below are about its presentation, its
missing third conjunct, and the company it keeps on screen.**

**1.2 The sled.** R-10's acquittal stands at this bar too: 1:1 drag,
per-stop voices, cap colour follows the rocker not the commit. A better
object than the switches in most commercial apps in the set. Panzer's
solidity scale (SHUT solid / PEEK 55% / OPEN frame) is a genuine
material idea — one substance, three densities.

**1.3 The tally's manners.** The 250ms coalesce + whole-line 260ms
Curve-A re-strike, never a digit roll. Slot-machine digits are the
first thing commercial polish removes; this screen never had them.
Glanz's grouped catalogue + jump-words complete R-1 correctly.

**1.4 The words ARMED and WON'T FIRE.** Exact, honest, one read. This
is copy at commercial standard.

---

## 2. SENT BACK — W-1 · The headline is typeset like a log line

**Evidence.** The landed status item (`ShieldMatrixScreen.kt`, Section
0): the whole sentence in Anonymous Pro Bold **13sp**, one glyph
(●/○), fixed 40dp slot. Anonymous Pro is the house *telemetry* face —
the face for numbers nobody reads as sentences. The full sentence
("NOT BLOCKING — SLEEP SESSION RUNNING, ENFORCEMENT NOT PROVEN") is
prose, set in the typeface of a serial number, at a size below the
app-row titles (Atkinson Bold 15sp) it outranks in importance.

**The gap, commercially.** Invert the measuring set: Mullvad's state
is the largest type on its screen; Opal's session state is a designed
object, not a caption. Here the screen's hierarchy says: cap (largest
object) > app names > … > the one fact the screen exists for (13sp
mono). A stranger's screenshot reads "settings page with a cryptic
top line", not "a wall, standing". Form specced Atkinson Bold 14sp
words + mono numerals (her §2.2) and Panzer repeated it; the landed
code set the whole line in mono anyway — the finish spec and the build
disagree, and the build is the blander one. Luft named the same wound
from atmosphere: a whole verdict in Anonymous Pro is "a log line — the
telemetry face reporting on him."

**The crossing (specific).** Two tiers, both already in the house
grammar, no new faces, no colour:

- **Tier 1 — the verb, as display.** `BLOCKING` / `NOT BLOCKING` /
  `CAN'T CHECK` in **Sora Bold 22sp** (the house display face, the
  DrawerHeader tier) with the solidity glyph at 14dp on the same
  baseline axis. This is the three-second read.
- **Tier 2 — the evidence, as telemetry.** Directly beneath, Anonymous
  Pro 11sp, variant ink: `<SESSION LABEL> · ENDS 03:30 · PROOF 3S OLD`
  (see W-3) — the facts, at the size facts deserve, in the face facts
  already wear everywhere else in this app.

Slot: 40dp → ~62dp. **Paid for, per R-13, by W-6's removals** (the
cap-source confession line ~22dp + the cap's dead ticks) — the screen
does not grow; the apology funds the headline. Truncation law unchanged
from Saum's table, applied per tier: Tier 1 never truncates; Tier 2
sheds segments in her order (label first, ENDS never before the break
qualifier). Motion unchanged: one whole-headline 260ms Curve-A
re-strike, coalesced — Schwung's spec, applied to both tiers as one
object. Semantics: one contentDescription, the sentence read plainly
(Form §2.2), exactly as now.

**Price:** ~35 lines in the status item + Saum table update. No new
derivation, no machinery.

---

## 3. SENT BACK — W-2 · The missing usage conjunct is launch-blocking

**Evidence.** Nagel's opening charge, Salz C-2 before it:
`MonolithRepository.queryForegroundBlockedApp` returns null
unconditionally when usage access is missing; the shield loop keeps
beating, `protecting` stays flipped, and `computeShieldBlockingStatus`
— which never asks about usage access — prints BLOCKING NOW over a
door that intercepts nothing.

**The gap, commercially.** Vogt's lead filing carries this as a
"residual … for Ren's merge call" and the issue's acceptance items
were worked anyway. At the commercial bar there is no such category
for a *known false statement in the product's headline claim*. Opal
with a proven "says it's blocking, isn't" screenshot is a refund
thread and a dead rating. Trust is not a feature of this app; it is
the product. One false LIVE outweighs every other finding in this
file combined.

**The crossing.** The third conjunct joins the derivation —
usage-access attested for this residency, judged by the same read the
warrant already makes (Form's evidence model had it in the
conjunction from the start; the landed pair dropped it). Until it
lands: the UNPROVEN form is the only honest print for that state, and
**this seat's signature is withheld from the headline item's
acceptance**. Price: the cell has already scoped it (Salz C-2);
Funke's root-stamp strengthening remains the named successor, not a
blocker.

---

## 4. SENT BACK — W-3 · The proof has no age

**Evidence.** The derivation knows the beat instant
(`shieldLastBeatWallMillis`) and judges staleness at 10s, but the
printed line carries no freshness. Between "proved 1 second ago" and
"proved 9 seconds ago, one beat from stale", the screen is identical.

**The gap, commercially.** Every trust surface in the measuring set
prints its freshness — 1Password's unlock state, banking sessions,
Mullvad's connection time. Freshness is what turns a claim into a
*checkable* claim, and it costs the reader nothing: it sits in the
evidence tier.

**The crossing.** Tier 2 (W-1) ends with the proof age, derived from
the beat the derivation already reads: `· PROOF 3S OLD`, re-struck on
the existing per-second refresh the ViewModel already runs for this
StateFlow. It never rolls digit-by-digit (the age is a fact printed
once per second, whole — the tally's manners govern it as they govern
every re-strike here; if the cell judges a ticking age too busy, the
lawful alternative is bands: `PROOF FRESH` under 5s, the number above
it). Zero new machinery: the timestamp is in the row today.

**Price:** ~10 lines in the derivation's headline assembly + tests in
the existing truth table.

---

## 5. SENT BACK — W-4 · The verdict vanishes under scroll

**Evidence.** The sentence is item 0 of a `LazyColumn` whose
`listState` is saveable (M6): a man returning to Shield mid-catalogue —
exactly where a 1 AM tier change happens — has no status anywhere on
screen until he scrolls to the top. Form flagged this as a "priced
follow-up"; Glanz then specced sticky *group headers* for the
catalogue. The screen will shortly have sticky furniture for its
least important repetition and none for its headline fact.

**The gap, commercially.** Apple Focus state lives on the Lock Screen
and in Control Center — it follows the user. A VPN's state is
reachable from everywhere in its app. A status that exists only at
one scroll position is a status that is absent at the moment of use.

**The crossing.** Form's sticky variant, upgraded from follow-up to
requirement, in its smallest lawful form: when item 0 scrolls off, a
**28dp sticky mini-line** takes the top — solidity glyph + Tier-1 verb
only (`● BLOCKING` / `○ NOT BLOCKING`), same derivation, same
re-strike, no second sentence, no evidence tier. It is a continuation
of the one line (the way a sticky group header continues its group),
not a second home for the fact. Luft's warning is honoured: it does
not *follow* him as a floating watcher; it sits where the line
scrolled away from, in flow's own grammar, and disappears the moment
he scrolls back to the full headline.

**Price:** ~30 lines (sticky header slot + scroll-off condition).

---

## 6. SENT BACK — W-5 · The unproven line is a dead end in a third voice

**Evidence.** The screen's no-witness fallback prints
`ENFORCEMENT NOT PROVEN — NO WITNESS READ YET` — a verb-less,
engineer-English sentence that matches neither the derivation's forms
nor the Deck's `CAN'T CHECK BLOCKING — TAP TO RETRY`, the app's one
ruled uncertainty sentence (Form §1.3C). Feile's Round 1 already
locked the cure (fallback joins the family, character-identical
forms). Separately: even the derivation's UNPROVEN form offers no
action. Where the dead leg is accessibility or usage access, the fix
is a known settings pane — the Deck already owns both destinations
(`RefusalAction.FIX_ACCESSIBILITY` / `FIX_USAGE`) — and Shield's line
carries semantics only, no tap.

**The gap, commercially.** Two failures stacked. (a) *Voice:*
commercial products say their uncertainty in one sentence everywhere;
a third grammar on the flagship screen is how an app starts sounding
assembled. (b) *Remedy:* a status that names a fixable death and
cannot take you to the fix is a dead end. Apple's "No Connection"
carries its remedy; a wall that says CAN'T CHECK and shrugs will be
read at 1 AM as "broken", not "honest".

**The crossing.** (a) Adopt Feile's lock verbatim — no new words from
this seat. (b) The unproven/stale line becomes tappable **exactly
when the missing proof names a fixable leg**: UNPROVEN with
missingProof = ACCESSIBILITY → the accessibility pane; the usage
conjunct (W-2) missing → the usage pane. The line gains the Deck's
door grammar (ink + bar, the FilterWord treatment) in those states
only, and its TalkBack custom action names the destination (Form
§2.2's semantics, completed). NO SESSION and SHIELD PROOF STALE stay
untappable — they state facts with no user verb, and a fake door is
worse than none.

**Price:** ~25 lines (tap routing exists in the Deck's vocabulary).

---

## 7. SENT BACK — W-6 · Three objects whose reasons have expired fund the headline

Intentionality I-3 filed all three; this seat confirms them at the
bar and assigns their budget to W-1 (R-13 arithmetic):

1. **The cap's stop ticks** — ruled dead in R-4(d), still drawn in
   `ShieldCapSelector.kt`, still described in its own header comment.
   An element that survives its death sentence is the definition of
   an accident. **Remove.**
2. **The cap-source confession line** — "THIS CAP SHOWS YOUR LAST
   HAND PICK" / "A ROUTINE SESSION IS RUNNING — ITS LOADOUT GOVERNS,
   NOT THIS CAP". No commercial product ships a permanent footnote
   apologising for the control above it — and worse, under a routine
   the cap stays *live*, rewriting tiers it does not govern.
   **The cure is the cap, not the caption:** while a routine session
   governs, the cap rests on the *governing* loadout and is inert to
   taps (a tap prints, at the cap, `SLEEP GOVERNS UNTIL 03:30` — R-7's
   refusal-at-the-control law). The line then dies because the lie it
   confessed is gone. Price: the cap gains a governed pose; the
   derivation already knows the governing session and its end.
3. **The ±30 chips** — ruled dead by R-8, still the only time editor
   until Voss's wheels land. I-3 is right: they must not survive the
   wheels merge as a "quick adjust". Recorded here so the survival
   attempt, if it comes, meets a filed objection.

---

## 8. SENT BACK — W-7 · The cap still commits on first tap (the screen's floor)

**Evidence.** In the landed screen, `ShieldCapSelector.onSelect`
calls `onApplySmartLoadout(...)` directly: one tap rewrites every
app's tier by category — the audit's finding 4, the "silent rewrite"
— and Klang filed that the act also spends the harmonic *seal* voice,
the Throw's own body, on an act that seals nothing. R-4's Ghost
Preview (first tap previews hollow ghost caps + consequence line,
second tap commits) was ruled, specced by Vega, voiced by Tast
(preview = a press that does not seat; commit = one firm detent) —
and is not in the code.

**The gap, commercially.** This is the floor, not the ceiling: Linear,
Things, Gmail — every product in the set — previews or undoes a
rewrite-everything act. A settings screen whose most destructive
control is its fastest control does not reach commercial, whatever
its headline says.

**The crossing.** Ghost Preview as already specced — this seat adds
nothing to the mechanism and one thing to its ambition: **Blitz's
Counterfoil (B-8) is the wow form of the same cure** — the commit
tears a numbered foil (old tier → new tier, app by app, side-effects
printed or they don't happen) and the foil *is* the undo. If the
merge can carry only one, Ghost Preview is the floor and ships;
the Counterfoil is the named next build and this seat will hold the
screen at "commercial" — not "above" — until it lands.

---

## 9. The bar, stated for this screen

After Vogt's merge as landed: Shield is *truthful, disciplined, and
set in type that undersells it* — a B+ internal product with an A+
proof engine. After W-1…W-7: the state is the screen's hero, its
proof is complete (three conjuncts) and visibly fresh, it survives
scroll, its uncertainty speaks the app's one voice and carries its
remedy, and its most destructive control previews before it commits.
That is the first version of this screen a stranger could screenshot
and understand. That is the bar.

**Sign-off state from this seat:** the headline item's *derivation*
is signed (§1.1). Its *acceptance* is withheld pending W-2, and the
screen's wow sign-off is withheld pending W-1, W-5, W-7 (W-3, W-4,
W-6 ride the same merge or are filed as its named remainder — they
are not optional extras).

— Staunen, wow-level designer, issue #7 cell — Oct 30, 2026.
DECLARED: judgement and design only; no code edited, nothing compiled.
