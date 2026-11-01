# ISSUE #97 — RECOMPOSITION OF PR #224 (K1-W97, train side, under FINISH-LEAD K1)

Ruling executed (Review Ren, Oct 31 01:27 IST): **#132's rulings STAND —
MUSIC-PEEK_2M and LOCKED-birth. A stranded cure never silently repeals a
ruled design.** PR #224's cure is recomposed to conform: non-conflicting
parts land; the conflicting part is struck with evidence.

Base of this recomposition: main `dff48c8bd8937dc10eaa746ec122ecb00f9045ef`.
Stamp: `VERIFIED vs main dff48c8bd8937dc10eaa746ec122ecb00f9045ef` —
files/findings covered: AppCatalogReconcile.kt (birth ladder +
AppCategorizer), MonolithRepository.kt (classifier extraction),
GuardLine.kt, WatchStore.kt, MainActivity.kt (PARK intro spend),
ParkDrawer.kt (plate), ReceiptSheet.kt (footer), MonolithRailView.kt
(PARK word), DrawerScreen.kt + strings.xml (Drawer heads),
ShieldMatrixScreen.kt + strings.xml (slip header),
MonolithViewModel.kt (destination label), MonolithCustomComponents.kt
(Guard-line tap target). Every #97 hunk was read against this main.

## STRUCK — conflicts with #132's rulings (does NOT land)

**S1. The OPEN-birth law** — branch `57360275`,
`AppCatalogReconcile.kt` (branch lines 88–95):
`val tier = preset?.tier ?: AppBlockTier.OPEN`, with the comment
"ONLY a curated per-package factory preset may birth a non-OPEN tier…
guessed categories birth no rules". This replaces ALL category-default
births with OPEN. It repeals both #132 rulings:

- **MUSIC-PEEK_2M.** #132 body + closure (fixed `799e7be`, PR #184):
  "players are born PEEK_2M", "birth tier MUSIC → PEEK_2M". On main
  this is live law — AppCatalogReconcile.kt:448
  `AppCategory.MESSAGING, AppCategory.MUSIC, AppCategory.FINANCE -> AppBlockTier.PEEK_2M`.
  Under the branch's law a guessed MUSIC player is born OPEN instead:
  the ruled 2-minute lawful start window is silently replaced by
  no blocking at all.
- **LOCKED-birth.** On main, AppCatalogReconcile.kt:436/459:
  SHORT_VIDEO/GAMES are born LOCKED, and every other unlisted
  category is born LOCKED — "Unlisted is now born LOCKED: shut until
  HE lists it otherwise in the catalogue" (the fail-closed birth,
  #129, standing on main). The branch births every one of those
  guessed apps OPEN — a blocker defaulting unknown apps to open.

**S2. The pinning test** — branch `AppCatalogReconcileTest.kt`:
asserts a guessed MESSAGING app (`com.fresh.chat`, no preset) is born
OPEN. On main MESSAGING → PEEK_2M; the assertion exists only to pin
S1 and is struck with it.

**S3. The stating comment** — branch `MonolithRepository.kt`
categorizePackage hunk: "(see AppCatalogReconcile: guesses birth
OPEN)". The hunk's substance (removing the "play" GAMES token) is
already on main in stronger form — #107's token-based AppCategorizer
(AppCatalogReconcile.kt:132–207, "play" survives only in "play
games") — but its comment states the struck law, so the hunk does
not land as written.

## NOT LANDED — superseded by other standing rulings (recorded, not silent)

- **F1 PARK-intro spend** (MainActivity closePark/openFixPane hunks):
  struck by the HERD settlement on #97 — fixed-elsewhere by #156;
  main's live mechanism spends on delivery (MainActivity.kt:578–589);
  the branch's mechanism is a third one and must not be ported.
- **F2 GUARD 2/2** (WatchStore.REQUIRED, countedWarranted, GuardLine,
  ParkDrawer FIX-queue OPTIONAL labelling, ChromeWaveUnitTest,
  ScreenshotHarnessTest comment): superseded by main's GUARD 6/6 law
  (Ren's Option C; GuardLine.kt:26 on main).
- **F4 plate STUDY HOURS line + receipt footer**: superseded on main
  by #115/W-43 plain-words versions — plate "STUDY HOURS: IN
  YEOLPUMTA, THE STUDY TIMER" (ParkDrawer.kt:141), footer "THIS APP
  KEEPS ITS OWN LOG · STUDY TIME IS IN THE STUDY TIMER"
  (ReceiptSheet.kt:~460). The branch's versions would regress both.
- **F4 plate ROUTINES line** ("FACTORY STARTERS — EDIT ON SHIELD"):
  main's #115 plate composition kept "ROUTINES: SET BY HAND ON
  SHIELD", pinned by TruthCar3SweepTest on main; routines semantics
  are under the rethink freeze. Not landed.
- **F5 CURIOS rename** (DrawerScreen cabinet head): struck by
  DURCH-C's ruling on #97 — CABINET wins; main prints CABINET
  (strings.xml drawer_cabinet). Not landed.
- **Rail PARK word** (MonolithRailView): already landed on main via
  #115 (the zone prints PARK; MonolithRailView.kt:~105 header).
- **Destination label Ledger** (MonolithViewModel VAULT label):
  already on main (MonolithViewModel.kt:83, "Ledger").

## LANDED — the non-conflicting remainder of #97

1. **F5 LEDGER naming in the Drawer** (agreed by both lanes per
   DURCH-C): strings.xml — `drawer_stored` STORED → LEDGER (both
   Drawer heads, DrawerScreen.kt:463/945), `drawer_nothing_under`
   → "NOTHING IN THE LEDGER UNDER …", `drawer_nothing_yet` →
   "NOTHING IN THE LEDGER YET".
2. **F3 slip honesty** (the wording half; the birth mechanism is the
   struck part above): strings.xml `shield_your_rules` YOUR RULES →
   RULES IN FORCE, with the section comment in
   ShieldMatrixScreen.kt updated to say why. Main's #107 hint
   ("TAP A LINE TO FLIP IT SHUT ↔ PEEK") already covers the branch's
   extra explanatory line, so that duplicate line is not added.
3. **F1's spoken label** (WEG map: "can ride the train"; never
   struck): MonolithCustomComponents.kt — the Guard-line tap target
   now carries contentDescription "Park — setup, permissions and
   settings".
4. This record + the branch's own wave-3 report
   (studio/plan/ISSUE-97-WAVE3-REPORT.md), preserved as history.

— K1-W97 (recomposition seat, FINISH-LEAD K1)
