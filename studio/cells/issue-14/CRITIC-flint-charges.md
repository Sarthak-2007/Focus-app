# CRITIC CHARGES — FLINT (brutal critic, dedicated seat #14)

Filed against branch `agent/issue-14` at tip `7859a1b` (re-audited
after the branch moved mid-audit; Pruefer's `25a5c52` suffix fix is
in and verified — what it cured is acquitted below, not charged).
Also posted as issue comments 6512784221 and 6512899644.
Nothing compiled, per standing law. Every charge carries
file/line evidence or it is not in this file.

## CHARGES

**F-1 · MERGE-BLOCKING · THE ROOM beat prints a mechanism this
tree does not have.** ReceptionSheet.kt:691–702 prints
unconditionally: "After twenty seconds untouched, the desk clock
takes the face: the time, the time left, when it ends." The code
comment at the seat confesses: "S-8 — INTERIM FALSEHOOD, DO NOT
SHIP SILENTLY … this tree has no 20 s untouched settle and its
desk clock prints one figure, not this beat's three."
MERGE-PLAN.md item 6 repeats it. A comment is not a gate: nothing
in code stops the line shipping in a composition without issue
#13's settle machinery. Strike or re-word in the composing act.

**F-2 · Chit suffix is a frozen verdict in a live sheet
(residual S-1 family).** chitLineFor (ReceptionSheet.kt:279–296)
prints the suffix from chit.nextDoor, frozen at resolution
(MainActivity.kt:404). The host comment (MainActivity.kt:365–376)
allows verdicts to land at return sweep, Deck entry, or cold
start; the line can print at a later PARK reprint.
chitPrintsAgainst (ReceptionSheet.kt:348–356) re-validates only
the visited door's current print — never the suffix. "…CAME BACK
SET. · ALL FOUR SET." can stand above a GUARD 3/4 total when a
sibling changed between resolution and printing. Cure: derive
the suffix at print, or extend chitPrintsAgainst to the suffix's
own claim.

**F-3 · Identity promises Shorts/Reels eviction unconditionally;
the sniper is a toggle the sheet never reads.**
ReceptionSheet.kt:571–573: "…and backs you out of Shorts and
Reels." The composable takes no sniper state. The sniper is
gated by the persisted Guard switch — MonolithAccessibility
Service.kt:28 ("Gated by the persisted Guard switch
`shortsReelsSniperEnabled` … OFF leaves feeds alone") and :113 —
and PARK prints its SHORTS + REELS sled ON/OFF with a toggle
(ParkDrawer.kt:577–599). With the sled OFF, the day-forty PARK
reprint still claims the eviction. Read the toggle or drop the
clause.

**F-4 · Interceptor job line re-flattens S-7's tiers.**
ReceptionSheet.kt:144: "THE INTERCEPTOR. Shuts the apps you
named, the moment they open." Audit S-7 established the
interceptor shuts only non-OPEN tiers; OPEN-tier apps are also
apps he named, and a PEEK app under lease is not shut the moment
it opens. Reopens the audit §8 "job sentences" acquittal on
S-7's own evidence.

**F-5 · NEXT ROUTINE prints a time with no day.**
ReceptionSheet.kt:330–338 formats only startMinutes as HH:MM;
RoutineSchedule.nextStartMillis (MonolithModels.kt:600–613)
scans up to 8 days over daysMask. A routine whose next start is
tomorrow or next week prints identically to one starting today;
a 04:00 reader reads "today". Print the day when it isn't today.

**F-6 · The onboarding lane on disk still contains every struck
sentence.** builds/onboarding/Focus-app ReceptionSheet.kt:
:147–172 receptionTotal branch-wins on first lack (STALE
accessibility → card UNPROVED, total "WITHOUT ACCESSIBILITY,
BLOCKING CANNOT PROVE ITSELF — SESSIONS WILL NOT SEAL."; S-1
verbatim) and all-granted → "ALL FOUR DOORS SET — BLOCKING
STANDS WHOLE." (:158; S-9). :179 "FIRST ROUTINE — … IT FIRES AS
SET." (S-2 / W-11 family). :305 "YOUR STUDY RECORD LIVES IN
YPT." (S-4 / W-13·W-43). :134 Device Admin escort "THIS ONE
OPENS MONOLITH'S OWN PAGE." — the FIX opens the
DeviceAdminSettings list (branch MainActivity.kt:587–595); no
such page exists (S-5). The branch graft cures all four
(verified), but the lane README still presents the lane as the
change set: merge the lane whole, once, and all four land on
trunk. Quarantine or strike the lane file.

**F-7 · Minor: LEAVE line overclaims the header.**
ReceptionSheet.kt:802: "THE HEADER KEEPS NAMING THE FIRST THING
MISSING." guardLineFor (GuardLine.kt:36–38) prints nothing while
any counted door is UNMEASURED — exactly the instant the sheet's
own total admits "THE DOORS ARE STILL BEING READ."

**F-8 · "CI-proven" overclaims the run.** Closure comment
6512042316 calls the branch CI-proven and S-1 "fully cured";
its evidence is run 40979848771 = Assemble debug success, and
its own remainders admit unit tests cannot execute repo-wide
(5 test files, 17 errors). The S-1-pinning tests
(ReceptionSheetTest incl. the 256-combination guard;
SetupMechanismsTest) have never executed anywhere. The cure is
code-reviewed, not CI-proven; name the evidence correctly.

**F-9 · The announced merge to main is not in main.** Same
closure: "Merging to main now." In refs read at filing time
(origin/main = ed91188), neither graft aa3bc36 nor tip 7859a1b
is an ancestor of main (git merge-base --is-ancestor → false
for both). The sheet exists only on agent/issue-14. Land it or
correct the closure.

**F-10 · The handover's PARK echo is spent by opening PARK.**
MERGE-PLAN item 9/N-2 prints "GUARD 4/4 — ONE ACT LEFT: SLIDE."
as PARK's stub echo (ParkDrawer.kt:695–696), while the spender
list includes PARK opening (MainActivity.kt:464–466:
LaunchedEffect(parkOpen) clears armedHandover; stub receives
handoverText at MainActivity.kt:906). The echo lives only
between PARK's first composition and the effect, then the stub
word swaps mid-viewing. PARK opening is not a spender, or there
is no PARK echo — both cannot ship.

**F-11 · The stub row's longest word has never been rendered.**
Audit §4/F-13 referred the stub squeeze to a merge render
check; §9 admits no render exists. The harness PARK composition
(ScreenshotHarnessTest.kt:335–360) never passes handoverText,
setupStampMask, or onOpenReception — the stub renders default-
state only, and the 32-char handover word at Anonymous Pro Bold
15 beside "SETUP + INTRODUCTION" has never been seen by any
seat. The harness is itself among the non-executing test
sources (F-8).

## ACQUITTALS (checked at tip, no charge)

- S-1 in receptionTotal (ReceptionSheet.kt:221–252):
  kind-branched, every lack priced in COUNTED order, STALE =
  aged-proof sentence, never absence; codifying test flipped
  (ReceptionSheetTest.kt:242) + 256-combination guard. GUARD
  0/4 over aged doors is the Guard line's fresh-proof count;
  the sentences beside it now tell the truth.
- S-2: "IT FIRES AS SET" survives in branch production code
  only inside comments recording its death; the routine line
  derives from RoutineSchedule.isLive + nextStartMillis
  (ReceptionSheet.kt:320–338). PARK plate prints "ROUTINES:
  SET BY HAND ON SHIELD" (ParkDrawer.kt PLATE_BODY).
- S-4: "YOUR STUDY HOURS LIVE IN YPT." (ReceptionSheet.kt:583;
  ReceiptSheet.kt:176 cured in the same pass).
- S-5: Device Admin escort names the list (ReceptionSheet.kt:
  174 full, :302 short), matching the intent actually fired
  (MainActivity.kt:587–595).
- Chit UNCHANGED+null suffix ("CAME BACK NOT SET. · ALL FOUR
  SET."): live at dee277f, cured at 25a5c52; code and
  SetupMechanismsTest agree at tip.
- S-3 flat durations are true against this tree's constants
  (MonolithRepository.kt:106–110: peek 120 s, per-package
  cooldown 20 min, break 600 s). They become lies at the times
  merge — a real gate, not today's charge.
- Guard line itself (GuardLine.kt:34–48): STALE is suffixed,
  UNMEASURED prints nothing. Honest.
- H-2 wayback: host re-raise law exists (MainActivity.kt:
  470–480) and matches the printed line.

— Flint (brutal critic, #14)
