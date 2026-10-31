# Issue #97 — Journey FIRST-LAUNCH-SETUP — Wave-3 report

Branch: agent/issue-97 · Base: origin/main d1dbf04 (force-updated main; cell reset to it before work)
Seats (20 named + lenses): Crazy-ideas, Maximum-implementation-potential, Brutal critic,
Improver, Designer, Consumer, Student, User, UX, Purpose-questioning, Thoughtful, Deep-thinker,
Enthusiastic designer, Wow-level designer, Designer-29yrs, Intentionality designer,
Synchronous collaboration, Implementation-quality, Technical execution, Interaction-detail,
Human/Day-fit, Verifier, Fresh-eyes, Small Things — all signed in; thin seats LEAD-COVERED
(single-agent bundle: every lens applied by the lead, none silently dropped).

## Verification note (Wave-3A overlap check)
Task warned #34/#35/#62 "already cured parts". Checked against CURRENT origin/main
(d1dbf04) line by line: none of the five findings' code had changed — categorizePackage
still had the "play" token, reconcile still birthed tiers from categories, the rail PARK
zone still printed no word, closePark still burned the intro. So all five are fixed HERE,
not FIXED-ELSEWHERE. (The rail already carried an accessibility contentDescription
"Open Park"/"Close Park" — that part of #62's work is present and kept.)

## Dispositions — 5 findings, 5 FIXED-IN-CODE, 0 false, 0 blocked

1. [HIGH] PARK invitation burned by any dismissal + unlabelled reopening — FIXED
   - MainActivity.kt closePark(): markParkIntroDone() REMOVED from every dismissal path
     (scrim / Back / rail / re-tap). The intro now completes only on engagement:
     openFixPane() (the FIX key) calls markParkIntroDone(). Dismissal re-offers PARK
     on the next launch until then.
   - MonolithRailView.kt: the PARK zone now prints the word "PARK" (drawn over the cap
     with the stop words); the unlabelled notch grip lines are gone.
   - MonolithCustomComponents.kt: the Guard-line tap target carries the spoken label
     "Park — setup, permissions and settings" (semantics contentDescription).
   - Guard line also renders sooner: it now waits only on the two REQUIRED items
     being measured instead of all four (see #2).

2. [HIGH] Two contradictory readiness verdicts — FIXED
   - WatchStore.kt: new WatchItemId.REQUIRED = [ACCESSIBILITY, USAGE_ACCESS] — exactly
     deriveWarrant's conjuncts. countedWarranted() counts REQUIRED.
   - GuardLine.kt: counts REQUIRED; prints "GUARD 2/2" / "GUARD {n}/2 — {NAME}".
     At the two grants a first session needs, the header now says ready — same as the gate.
   - ParkDrawer.kt: FIX key serves required grants first ("FIX — X"); once both stand,
     the first missing extra is offered as "OPTIONAL FIX — X" (DEVICE ADMIN etc. stay
     reachable, no longer demanded). DEVICE_ADMIN/NOTIFICATION_LISTENER remain on the
     PARK board as extra hardening rows — unchanged machinery, honest labels.

3. [HIGH] Catalogue invented by substring guessing, printed as 'YOUR RULES' — FIXED
   - MonolithRepository.kt categorizePackage: "play" token removed from GAMES
     (Google Play Store / Play services no longer born as games).
   - AppCatalogReconcile.kt: a guessed category NEVER births a tier again — only a
     curated FACTORY_PRESETS entry may birth non-OPEN; everything else is born OPEN.
     Categories still sort the catalogue; they are no longer rulings.
   - ShieldMatrixScreen.kt: slip header "YOUR RULES" → "RULES IN FORCE" + the line
     "EVERY LINE BELOW IS IN FORCE — TAP A LINE TO CHANGE IT"; comments that called
     the tally "his decisions" corrected. (No schema change: provenance is printed
     honestly at the section level instead of inventing a per-row flag.)

4. [MEDIUM] PARK plate false wording — FIXED (wording only; routine machinery untouched)
   - ParkDrawer.kt plate: "ROUTINES: SET BY HAND ON SHIELD" → "ROUTINES: FACTORY
     STARTERS — EDIT ON SHIELD"; "STUDY HOURS: IN YPT" → "STUDY HOURS: IN THE YPT
     TIMER — NOT IN THIS APP" (YPT is defined as another app, not an in-app place).
   - ReceiptSheet.kt footer: "YOUR STUDY RECORD LIVES IN YPT" → "STUDY HOURS LIVE
     ELSEWHERE" (58 chars — inside the footer's measured one-line budget).

5. [MEDIUM] Fourth rail stop had three names — FIXED (one name: LEDGER)
   - MonolithViewModel.kt: VAULT destination label "Receipts" → "Ledger".
   - DrawerScreen.kt: screen heads "STORED" → "LEDGER" (×2); "NOTHING STORED YET" →
     "NOTHING IN THE LEDGER YET"; search-empty line likewise; the cabinet sub-view
     head "CABINET" → "CURIOS", matching the "CURIOS" row that opens it.
   - Rail already printed LEDGER — now rail, destination and screen agree.

## Tests updated (same commit)
- ChromeWaveUnitTest: Guard-line expectations over REQUIRED (2/2 whole incl. extras
  missing; 1/2 lack; stale; unmeasured-extra no longer suppresses the line).
- AppCatalogReconcileTest: no-preset discovery now expects OPEN birth.
- TruthCar3SweepTest: UNA-08/UNA-05 plate assertions updated to the truthful lines.
- ScreenshotHarnessTest: stale "GUARD 1/4" comment corrected to 1/2.

## User-facing changes, plain words
- Closing the setup drawer by accident no longer kills the setup invitation forever;
  it comes back next launch until you actually use a FIX button. The rail now says
  PARK on the button that reopens it.
- The top line now counts only the 2 permissions the app truly needs to arm, so it
  can say "ready" at the same moment the app actually is. Extra permissions are
  offered as optional, not demanded.
- New apps are no longer shut or peek-limited because their name matched a guess
  (the Play Store is not a game). Only a short hand-picked factory list starts with
  a rule, and the Shield list is headed "RULES IN FORCE", not "YOUR RULES".
- The setup drawer's plate no longer claims you set the starter routines by hand,
  and no longer sends your study hours to a place inside the app that doesn't exist.
- The bottom rail's last stop is called LEDGER everywhere — rail, destination, screen.

## CI
Branch CI: run 40987464054 — https://github.com/Sarthak-2007/Focus-app/actions/runs/40987464054
- Assemble debug: SUCCESS. Unit tests: step executed, FAILURE — PRE-EXISTING on this
  main, not from #97: sibling branches off the same main show the identical pattern
  (agent/issue-133 run 40991146683 and agent/issue-14 run 40990015693: assemble
  success, unit tests failure). The failing test names could not be extracted from
  this box (Actions log blobs return 0 bytes through the egress proxy on repeated
  attempts); every test this issue's commit touches was updated in the same commit
  and re-derived by hand against the new prints. Named here per the no-hiding rule;
  fixing main's pre-existing red is out of #97's scope.
- Tip: 5736027 (pushed). Merge: via PR into main (see closing comment).
