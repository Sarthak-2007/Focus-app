# FREMD — the session room, read cold (Oct 30, 2026, ~00:44 IST)

Reader: Fremd, fresh-eyes seat. I read the built thing first
(builds/flow-clock/Focus-app: DeskClockSettle.kt, the desk-clock
face in AirlockAndDisarmOverlays.kt, the VM ticker, MainActivity's
wiring), wrote down what it does, and only then read
plan/FLOW-REGISTER.md §2 and Wecker's declaration (mail/ren/2348).
Nothing here was compiled or run — paper only, like the seat's
other passes. Charges F-1…F-7 are filed to Wecker by name in
mail/wecker/; he answers each at the merge.

## What the room is, cold

You seal a session on the Deck. Nothing happens for twenty
seconds. If your hand has left the phone — no touch anywhere in
the app for 20 s after the seal or after your last touch,
whichever is later — the screen fades to a black face: one huge
number, a small "NOW HH:MM" under it, a thin progress strip, one
sentence at the bottom (your task's name, your declared
intention, a break line, or nothing), and at the top, in small
letters, "NIGHTSTAND DESK CLOCK • TAP ANYWHERE TO EXIT". The
screen stays awake while it stands. It shifts a few pixels every
minute so the pixels don't burn. Tap anywhere and you're back on
the Deck; the room will not return for that session. Open the app
cold into a live session and the room is simply there — no settle,
no wait. When the session finishes or is disarmed, the room
closes itself. If no session runs, the room never raises itself;
opened by hand it shows the real time under a "THE TIME NOW"
label.

That behaviour — a room that arrives by itself, leaves when told,
and dies with its session — is genuinely above the commercial
timer faces I know, which are pages you must remember to open.
The machine is good. The face it wears is not yet at that level:
the biggest number in the room is the least labelled thing in it.

## The charges (summary — full text in mail/wecker/)

**F-1 · The dismissal that forgets. HIGH.** Law 2 says a
dismissal is a decision for the rest of the session. In code the
decision is one in-memory bit (DeskClockSettleMachine.
decidedSessionId); a process death erases it, and the cold-open
rule then re-raises the clock over the very session he dismissed.
The register states the pickup law absolutely in §2.3 ("the face
he left is the face he gets — the Deck if he had dismissed it")
and buries the exception in §2.6. Across a 7½-hour SLEEP span,
a background process death is not an exotic event. As written,
§2.3 promises what the code does not do.

**F-2 · The hero has no label: neither unit nor direction. HIGH.**
While running, the huge number is span-remaining for a timed
session and elapsed for a stopwatch — identical dress, opposite
meanings, no word on the face says which, or that the format is
minutes:seconds at all. Past 99 minutes it prints total minutes
(a 450-minute SLEEP span opens at "449:59" and counts down
through three-digit minutes all night). A tired man at 01:00
facing "268:41" and "NOW 01:00" cannot decode the room's main
number in five seconds; every commercial clock face prints
hours when hours exist. This fails the house test on the
room's largest print.

**F-3 · "BREAK — DOORS OPEN" overclaims. MEDIUM-HIGH.** During
a Safe Break only PEEK-tier apps unlock (MonolithAccessibility-
Service.kt:203; MonolithShieldService.kt:170). SHUT apps, the
Shorts/Reels sniper and blocked sites stay locked — the
service's own header says so. The register authored the sentence
(§2.3) and the lane printed it verbatim. "DOORS OPEN" is false
as stated; the true sentence is in the app's own grammar (the
peek tier opens). In a build whose whole programme is prints
that don't lie about enforcement, this one lies.

**F-4 · The stopwatch strip is invented progress. MEDIUM.** A
stopwatch session has no target, but its strip fills on an
elapsed/1200 s derivation — full at twenty minutes, then full
for hours. The register's "hour three: the strip has filled"
is true of a stopwatch by minute twenty. A progress bar for a
mode with nothing to progress toward is a default wearing a
fact's clothes.

**F-5 · The 4% floor contradicts the file's own honesty claim.
MINOR.** The overlay's comment states "every branch below is a
real derivation from the session, or honestly empty" — and the
timed strip is then coerced to a 0.04 minimum, so at a session's
first second the bar shows 4% of it already gone. Small, visual,
argued in another comment as never-reads-empty — but the claim
sentence and the clamp cannot both stand.

**F-6 · ENDS is withheld until the last minute. MEDIUM-LOW.**
The one number the register itself calls "the one number a
finishing man wants" prints only inside the final 60 seconds;
for every hour before that, no surface on the face says when
the session ends, though the row knows it. Not a lie — a
poverty, and below the bar: a commercial session face shows its
end. Fremd's ask: ENDS HH:MM small and standing, beside NOW,
for every timed session.

**F-7 · The room is named for another hour. MINOR.** The
standing top line reads "NIGHTSTAND DESK CLOCK" on every raise,
including a daytime study block. The name describes furniture
for the night; the room is the session's room at any hour. It
also spends the face's most persistent line on a name instead
of a fact.

## Acquittals — where code and intention agree, and the thing is true

- The settle is as designed: 20 s from max(seal-arm, last
  touch), Activity-level stamps, one flag write in the existing
  ticker. The machine is pure and its 14 tests exist on disk
  (DeskClockFlowTest.kt) — written, unrun; the merged run is
  still the proof, as Wecker himself states.
- Cold open into a live session raises at once (row predates
  process birth). True in code — F-1 is about what else that
  rule resurrects, not this.
- The fabricated banner is dead: task title, else intention,
  else no banner. The idle hero is the real time, labelled
  THE TIME NOW, inseparably above the figure. dialMinutes is
  no longer read.
- NOW HH:MM stands under the running hero, label inside the
  string. The light cure's constants are on disk as specified
  (0.55 / 0.58); the capture acquittal (L05) is still owed and
  Wecker asks for it himself.
- The finish and the disarm close the clock (flag writes on
  those paths verified). The clock is a read face: the only
  touch on it is leaving.
- The room is in-app only, by stated design (§2.7 refuses the
  system overlay; the pill owns the other-apps glance). No
  charge — the boundary is honest.
- The break line replacing the banner during a break: accepted.
  While the doors are (partly) open, the break is the live
  fact; the task name can wait ten minutes.

## Verdict against the bar

"Better than anything — commercial level, even above that."
On behaviour, this room clears it: no commercial timer I know
walks in by itself when your hand leaves, honours a dismissal,
and closes itself at the finish. On print, it does not yet:
F-2 and F-3 are the kind of faults the truth programme exists
to kill, sitting on the newest face in the app. Fix the hero's
label and the break sentence, persist the dismissal, and the
room is what the bar asks. Until then my verdict is: a good
machine, an under-labelled face — NOT YET the best room in
the building.

— Fremd, fresh-eyes seat
