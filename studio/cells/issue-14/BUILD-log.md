# BUILD LOG — issue #14 cell, builder seat

Empfang's onboarding lane merged under Pforte's setup-sheet
audit (`studio/plan/AUDIT-setup-sheet.md`, §6 order). DECLARED,
uncompiled — nobody built or ran anything; Ren compiles on
Sarthak's word. Files touched:
`ui/components/ReceptionSheet.kt`, `MainActivity.kt`,
`test/ReceptionSheetTest.kt`, `test/ScreenshotHarnessTest.kt`.
ParkDrawer.kt and SetupStore.kt untouched.

## 1 · S-1 — total branches on print KIND — DONE (flagged, see F-a)

`receptionTotal` (ReceptionSheet.kt:188) no longer derives
from `!= GRANTED`. It partitions prints by kind:
- any MISSING → one acquitted absence sentence per missing
  door, board order, space-joined (`receptionAbsenceSentence`,
  ReceptionSheet.kt:169). One-lack prints exactly as before.
- no MISSING, any STALE → one aged-proof sentence per stale
  door (`receptionAgedProofSentence`, ReceptionSheet.kt:185).
- none lacking → S-9 branch. UNMEASURED still returns the
  unread-door total (count null) before any branching.
Count unchanged: fresh GRANTED proofs only (audit §1.20 keeps
the count; the sentence was the lie).

Tests (ReceptionSheetTest.kt): the codifying test flipped and
renamed — `staleGrantReadsAsAgedProofNotLackInTheTotal` (:242)
asserts one stale door = count 3 + the exact aged-proof
sentence, and all-four-stale = count 0 with no "WITHOUT"
anywhere. Added `totalPricesEveryMissingDoorOneSentenceEach`
(:275, Blank item 6) and `totalMissingBranchWinsOverStale`
(:299, kind-precedence). New fixture `snapshotWithMonos`
per-item measurement instants.

## 2 · S-9 — all-set branch — DONE

"ALL FOUR DOORS SET." full stop (ReceptionSheet.kt:204).
Test expectation updated (ReceptionSheetTest.kt:171).

## 3 · FREE wording cures (§2 verbatim) — DONE

- S-4 boundary: "YOUR STUDY HOURS LIVE IN YPT." — seat kept,
  no-score clause kept (ReceptionSheet.kt:359).
- S-5 admin prepare: "YOU'LL SEE A LIST OF DEVICE ADMINS.
  FIND MONOLITH, ACTIVATE, COME BACK." (ReceptionSheet.kt:145).
- S-6 anchor job: "THE ANCHOR. While this stands, Android
  refuses to uninstall Monolith." (ReceptionSheet.kt:121);
  rank third clause "THE THIRD GUARDS THE MACHINE ITSELF."
  (ReceptionSheet.kt:397); total admin branch drops
  "MID-SESSION" → "WITHOUT DEVICE ADMIN, MONOLITH CAN BE
  UNINSTALLED." (ReceptionSheet.kt:169ff; test :208).
- S-7 identity: "While a session runs, Monolith shuts the
  apps you ruled shut and the sites on your list — and backs
  you out of Shorts and Reels." (ReceptionSheet.kt:347);
  SEAL beat: "From that instant, the shut-tier apps and the
  listed sites are shut." (ReceptionSheet.kt:436) — see F-b.
- S-10 mail-room job: "THE MAIL ROOM. Holds the shut apps'
  messages while a session runs — you read them in the
  digest." (ReceptionSheet.kt:126).
- S-11 leave line: "LEAVE WHEN YOU LIKE — THE HEADER KEEPS
  NAMING THE FIRST THING MISSING." (ReceptionSheet.kt:512).
- S-13 nameplate: "MONOLITH ML-01" always; "· FIRST OPEN"
  dropped (ReceptionSheet.kt:321).
- S-14 rules line: "…CHANGE THEM ANY TIME BETWEEN SESSIONS."
  (ReceptionSheet.kt:478).
- S-15 FINISH beat: "A session that runs its course prints
  its receipt: what was held, and when it ended."
  (ReceptionSheet.kt:459).

## 4 · S-2 — "IT FIRES AS SET" dead — DONE (note N-a)

Clause killed on all three surfaces. The derivation is the
full cure, not the interim: `receptionFirstRoutineLine`
(ReceptionSheet.kt:219) now takes `nowMillis`, filters to
LIVE rows via `RoutineSchedule.isLive` (enabled &&
daysMask != 0 — the scheduler's own predicate), picks the
soonest `RoutineSchedule.nextStartMillis`, prints
"NEXT ROUTINE — <NAME> · <HH:MM> · SET ON SHIELD". The word
FIRST died with the clause (it was smallest-start, not next).
Call site updated: MainActivity.kt:823 passes
`System.currentTimeMillis()`. Fabricated harness line cured:
ScreenshotHarnessTest.kt:392. Tests rewritten:
`routineLineNamesTheNextLiveRoutineToStart`
(ReceptionSheetTest.kt:325) — fixed Mon/Tue clocks prove
soonest-next-start beats smallest startMinutes, daysMask==0
and disabled rows return null.

## 5 · S-12 — card family — DONE (rider excluded, see F-c)

- Prepare line prints under MISSING only — removed from
  UNMEASURED (ReceptionSheet.kt:652).
- UNMEASURED gains the CHECK key: shared `PermPrint.STALE,
  PermPrint.UNMEASURED` branch, same `reception_check_<item>`
  tag (ReceptionSheet.kt:703).
- STALE cards gain the gloss "THE LAST PROOF AGED OUT —
  CHECK RE-PROVES IT" under the job line (ReceptionSheet.kt:644).

## 6 · Stranger lines (§5) — DONE

Seated in `receptionDoorJob` so they print at every door
state, not only while a door lacks:
- Door 1: "NOTHING LEAVES THIS PHONE — MONOLITH HOLDS NO
  NETWORK PERMISSION." (ReceptionSheet.kt:116).
- Door 4: "HELD ON THIS PHONE, IN THE DIGEST — NOTHING
  LEAVES THIS PHONE." (ReceptionSheet.kt:128).
- Door 3 way-back: "TO TAKE IT BACK: SWITCH MONOLITH OFF IN
  THAT SAME LIST — THEN IT UNINSTALLS LIKE ANY APP."
  (ReceptionSheet.kt:122).
- Under DONE, unconditional: "THIS FINISHES THE SETUP. IT
  STARTS NOTHING, SEALS NOTHING, AND MAY BE PRESSED WITH
  DOORS UNSET." (ReceptionSheet.kt:583).

## 7 · Composition FREE items — DONE

- F-1: "THE DOOR" eyebrow killed, re-eyebrowed "WHAT IT IS"
  (ReceptionSheet.kt:339).
- F-3: second sentence ("You seal a session…") removed
  (ReceptionSheet.kt:~353).
- F-12: stub note moved above the tear rule, still gated on
  first open (ReceptionSheet.kt:541).

## Not implemented — per brief, by name

S-3 value slots (fact check pending) · S-8 ROOM beat
(merge-gated on issue #13; beat left exactly as built) ·
mechanisms §6.7–6.10 · F-6 collapse · tear choreography ·
F-2/F-4/F-5/F-7/F-9/F-10/F-11 (not in this pass's list).

## Flags for Pforte

- F-a · S-1 wording: the audit says the STALE total takes
  "the aged-proof sentence in Stachel's words" but nowhere
  quotes that sentence. I composed it from the audit's own
  S-12 gloss words: "<DOOR> IS SET — THE LAST PROOF AGED
  OUT. CHECK RE-PROVES IT." Needs Stachel's/Pforte's
  confirmation or replacement wording; one function to change.
- F-b · S-7 SEAL: the beat line is now exactly the cured
  sentence; the old prefix "Slide, and the seal proves
  itself." was dropped to keep the cure verbatim. If the
  slide instruction must stay on the beat, it's a one-line
  restore.
- F-c · S-12 rider NOT done: the enabled-but-unbound
  accessibility prepare words ("the service is stuck") were
  not in my assigned bullet and need the sweep to call the
  distinguishing Settings query — second-pass work.
- N-a · S-2 nuance: "next boundary" implemented as next
  START. A routine whose window currently contains now is
  named at its next start, not its window end — printing a
  containing window's end as the line's time would misname
  the routine. Flagged, not blocking.
- Out-of-scope sighting: ReceiptSheet.kt:176 still prints
  "YOUR STUDY RECORD LIVES IN YPT" — the S-4 family on a
  surface outside this merge. Not touched; Pforte's call.

— Bauer (builder), issue #14 cell
