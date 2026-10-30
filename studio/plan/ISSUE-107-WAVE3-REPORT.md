# ISSUE #107 — WAVE 3 REPORT (Audit R3 journey: shield-setup-lived)

Cell: `builds/cells/issue-107`, branch `agent/issue-107`, base origin/main `0a00e95`.
Cluster lead: REISE. Worker bundle seats worn by one agent (see sign-offs).

## Per-finding dispositions

### 1. [HIGH] Loadout tap silently rewrites every tier + secret side-effects; subtitle dead text — **FIXED** (`75e6271` core, `9e07fd4` screen, `1195552` tests)
Verified standing in current code before fixing: `ShieldMatrixScreen.kt` fired
`onApplySmartLoadout` directly on cap tap; `SmartLoadout.subtitle` was read by no
UI code; `MonolithViewModel.applySmartLoadout` force-enabled Shorts/Reels sniper +
browser URL blocker for STUDY with no disclosure.
Cure:
- The tier rule was extracted to the pure `LoadoutRules.tierFor` / `LoadoutRules.preview`
  (`data/LoadoutRules.kt`); `MonolithRepository.applySmartLoadout` now commits through
  the same function the preview counts with — preview and commit cannot diverge.
- A cap tap now only STAGES a loadout. `LoadoutPreviewPanel` under the cap prints the
  loadout's own subtitle (rendered for the first time), for STUDY the line
  "ALSO SWITCHES ON: SHORTS & REELS SNIPER + BROWSER URL BLOCKER", and the cost line
  "REPLACES N HAND-SET RULING(S) — LANDS ON X SHUT · Y PEEK · Z OPEN".
  Only "APPLY LOADOUT" commits; "KEEP MY RULES" drops the staging and changes nothing.

### 2. [HIGH] PEEK sold with zero terms — **FIXED** (`9e07fd4`)
Verified: no duration/cooldown text anywhere on the Shield screen; the contract lived
only in `MonolithRepository` constants (PEEK_DURATION_SECONDS=120,
PEEK_COOLDOWN_MILLIS=20 min).
Cure: `PEEK_TERMS_LINE` is DERIVED from those constants —
"PEEK = 2 MINUTES, ONCE — THEN 20 MINUTES SHUT BEFORE THE NEXT PEEK" — and printed
twice on the setup screen: under the YOUR RULES tally (`shield_peek_terms`) and
directly above the catalogue sleds (`shield_peek_terms_catalogue`). Printed terms
are the enforced terms by construction.

### 3. [HIGH] Invisible, uncorrectable substring category guess — **FIXED** (`75e6271`, `9e07fd4`)
Verified: `categorizePackage` was a private repository method; the catalogue row
printed no category; no correction control existed. Category is an existing TEXT
column on `blocked_apps` — correction needs NO schema change (none made).
Cure:
- `categorizePackage` extracted to a pure top-level function in
  `AppCatalogReconcile.kt`, testable and auditable in one place. The bare
  `contains("play")` token is gone (it filed the Play Store itself, and any label
  containing "display", under GAMES); game tokens are now specific
  (game/clash/roblox/minecraft/pubg/candycrush) and "musically" was added so
  TikTok's real package lands SHORT_VIDEO.
- Every catalogue row now prints "FILED AS: <CATEGORY> · TAP TO FIX" under the sled
  (tag `app_category_<package>`); tapping cycles the category, committed via
  `MonolithViewModel.setAppCategory` → `MonolithRepository.setAppCategory`
  (plain row update). The corrected category drives filters/loadouts/birth tiers
  like any other fact. Wired in `MainActivity`.

### 4. [MEDIUM] YOUR RULES slip lines are hidden toggles — **FIXED** (`9e07fd4`)
Verified: slip tap flipped LOCKED↔PEEK_2M and the line exited; the header printed
only "YOUR RULES".
Cure: the slip header now prints the hint "TAP A LINE TO FLIP IT SHUT ↔ PEEK"
(tag `slip_hint`) directly under "YOUR RULES". The flip behaviour itself is
unchanged (it is the book's amendment path by design); it is no longer hidden.

### 5. [MEDIUM] Search name-only; no-match line falsely claims absence — **FIXED** (`75e6271`, `9e07fd4`)
Verified: `matchesQuery` tested only `appName`; the zero-result line printed
"NO APP BY THAT NAME ON THIS PHONE".
Cure: pure `appMatchesQuery` (MonolithViewModel.kt) now matches display name,
package name, and category (enum name + printed label); `filteredApps` uses it.
The no-match line now reads "NO MATCH FOR THAT SEARCH — TRY THE APP'S NAME OR ITS
PACKAGE NAME" — it reports a search result, never an absence. Search hint text
updated to "Filter apps by name or package…".

No finding was VERIFIED-FALSE or FIXED-IN-MAIN: all five stood in the cell's
current code (file/line evidence in the audit matched the code read this run).
Nothing is STANDS-BLOCKED; no schema change was needed for any cure.

## Evidence
- New tests `app/src/test/java/com/example/ShieldSetupTruthTest.kt` (12 tests):
  9 pure (categorize ×3 incl. Play-Store-not-GAMES and display-not-GAMES,
  loadout tiers ×2, preview counts, subtitles present, PEEK terms line, search
  match ×1) + 3 Robolectric render tests (PEEK terms + slip hint printed;
  cap tap stages preview with subtitle + side-effects and applies NOTHING until
  APPLY is tapped; no-match line never claims absence).
- CI run for the code tip `1195552`:
  https://github.com/Sarthak-2007/Focus-app/actions/runs/40985318601
  - Assemble debug: **SUCCESS** (the code compiles; APK uploaded).
  - Unit tests: 318 tests, 35 failed — **zero failures in ShieldSetupTruth**
    (all 12 new tests passed) and zero in any Shield/loadout/search class.
  - The 35 reds are exactly the shared CI test floor being killed centrally by
    another lane, in pre-existing classes untouched by this branch:
    ScreenshotHarnessTest 13, DrawerRenderTest 7, CascadeDeckRenderTest 5,
    SoundAndSocialTruthTest 3, TruthCar3Sweep(Robo)Test 3, BreakMigrationTest 1,
    ChromeWaveUnitTest 1, ShortsSniperTruthTest 1, TetradRenderTest 1.

## Seat sign-offs
- **Human/Day-fit + User (journey lead)** — walked the Shield setup journey in code
  end-to-end as Sarthak lives it: cap → routines → YOUR RULES slip → search →
  catalogue sleds → websites. Every place the journey asked him to decide blind
  (loadout cost, PEEK terms, category, slip taps, search verdict) now prints its
  terms at the point of decision. Signed.
- **Verifier** — each finding re-checked against the CURRENT cell code before any
  fix; all five stood (locations matched the audit). Post-fix, each cure is bound
  to a test or a derived-from-constants string. Signed.
- **Diagnostician** — root causes: commit-without-preview on the cap; a contract
  that lived only in constants; a private, untestable guess function; a ledger
  styled as print but wired as toggles; a verdict sentence stronger than the
  search behind it. Each cure removes the cause, not the symptom. Signed.
- **Brutal critic** — the preview panel adds one tap to applying a loadout; that
  is the correct price for not silently trashing ten minutes of hand-set rules.
  Category cycling is a blunt correction tool (six taps worst case) — accepted:
  it is truthful, immediate, and schema-free; a picker sheet is polish for a
  later lane, not a blocker. The PEEK line appears twice on one screen —
  deliberate: once where rules are read, once where they are set. Signed.
- **MaxImpl** — preview counts come from the committing function itself
  (`LoadoutRules`), the strongest available guarantee; search widened to package
  + category; categorize's worst false positive ("play") killed. Signed.
- **Builder** — 3 code commits, surgical, no schema change, no design/feature
  downgrade, fonts only Sora/Atkinson Hyperlegible/Anonymous Pro, no green
  added, no AI images. Assemble green in CI. Signed.
- **Interaction-detail specialist** — staging keeps the cap's instant response
  (tap → preview in place), APPLY/KEEP MY RULES use the house chip grammar,
  the slip hint sits in the slip's own mono grammar, and the category line is
  quiet print under the sled until needed. Signed.
- Remaining named seat types: **LEAD-COVERED by REISE** (per dispatch).

## PLAIN-WORDS: what Sarthak will see/do differently
- Tapping STUDY / WORK / SLEEP on the Shield screen no longer changes anything
  by itself. A panel appears under the cap explaining what that loadout does,
  warning (for Study) that it also turns on the Shorts & Reels sniper and the
  browser blocker, and saying exactly how many of his hand-set app rules it
  would replace and what the final SHUT/PEEK/OPEN counts would be. He taps
  "APPLY LOADOUT" to go ahead or "KEEP MY RULES" to walk away with nothing changed.
- The Shield screen now prints what PEEK actually means — "2 minutes, once,
  then 20 minutes shut before the next peek" — both near his rules list and
  right above the app list.
- Every app row now shows the category Shield has filed it under
  ("FILED AS: … · TAP TO FIX"). If the guess is wrong, he taps that line to
  change it; the new category is saved and used everywhere.
  Also fixed silently: the Play Store can no longer be guessed as a "Games" app.
- Under "YOUR RULES" it now says the lines can be tapped to flip them
  between SHUT and PEEK — no more hidden toggles.
- App search now also finds apps by their package name and category, and when
  a search finds nothing the screen says "NO MATCH FOR THAT SEARCH" instead of
  wrongly claiming the app is not on his phone.
