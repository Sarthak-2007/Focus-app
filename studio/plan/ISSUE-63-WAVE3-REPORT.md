# ISSUE #63 — Wave 3 report (theme) · lead: bundle lead #63 under PULS, cluster C6

Base: origin/main @ e29acdf. Branch: agent/issue-63.
Note on brief vs body: the dispatch brief paraphrased the 5 findings (fontScale /
porcelain-dark / dynamic takeover / same-red error / statusBarColor). The ISSUE
BODY + AUDIT-R1.txt slice "theme" are the law and were worked verbatim; where the
brief's paraphrase overlaps (dynamic colour, night error) it is cured inside the
body's findings (F5/F1). Current Theme.kt contains NO window/statusBarColor code
at all (124 lines, grep-verified) and no raw-sp dial cap issue in the theme files —
those brief items do not exist in current code and are noted here, not invented.

## Verdicts (all verified against current origin/main first)

1. **HIGH schemes half-wired + dark tertiary light token — FIXED-IN-CODE.**
   Verified: neither scheme assigned surfaceBright/surfaceDim/surfaceContainer*/
   inverse*/scrim/surfaceTint, yet surfaceBright is rendered at
   AirlockAndDisarmOverlays.kt:324,498, ReceiptSheet.kt:78, FocusSequencerScreen.kt:541,
   TactileDeckScreen.kt:580. Both schemes now wire every consumed slot from the
   existing 19+19 tokens only. Dark tertiary: was ElectricIris (= light
   TokenColorSecondaryContainer #615B5A) → now NightColorSecondaryContainer.
2. **HIGH dead divergent XML palette — FIXED-IN-CODE.** Verified: zero `@color/`
   refs in res, zero R.color in Kotlin; values-night contradicted Kotlin
   (#FFFFB38F vs #FF4A423C, #FFFAE4D3 vs #FF776A60, #FF201D1A vs #FF171412,
   #FFF8F5F5 vs #FFD2C6BB; light #FF787170 vs #FF5F5856). Both colors.xml files
   DELETED — Kotlin tokens are the single source of truth. themes.xml kept
   (manifest references @style/Theme.MyApplication).
3. **HIGH hard-coded colours outside theme — FIXED-IN-CODE.** Verified 19+
   literals. All replaced: copies → the token they copied (TokenColorOnPrimary,
   NightColorSurfaceSheet, NightColorPrimary, NightTextColorSecondary,
   NightColorOnSurface, TokenColorSecondaryContainer); untokenised tones →
   new honest component tokens in Color.kt (SequencerEdgeDay/Night 1–3,
   SequencerHairlineNight, TokenTrueBlack); alpha ghosts → token.copy(alpha);
   scrims → scheme.scrim (now wired); AOD true-black → TokenTrueBlack.
   Post-fix grep: ZERO `Color(0x` outside ui/theme, ZERO `Color.Black` anywhere.
4. **MEDIUM Color.kt alias landfill — FIXED-IN-CODE.** Verified: alias block was
   lines 110–209 (97 vals by count, audit said 87 — either way a landfill);
   72 had zero call sites, the rest only Theme.kt + one census test. ALL aliases
   deleted; Theme.kt names real tokens; the Robolectric census now iterates
   DaylightZeroGreenTokens + NightZeroGreenTokens. Wrong RGB comment fixed
   (TokenColorOnSurfaceVariant is 95/88/86, not 120/113/112). NightAlert is no
   longer a dead token counted in the 19: it is WIRED as the night error (see
   computed contrast below), so the count is honest. Three lying scheme aliases
   in Theme.kt (zero call sites) also deleted.
5. **MEDIUM Typography holes + Theme API lies — FIXED-IN-CODE.** Verified:
   Typography assigned 12/15 slots; header claimed a 50 tier that existed only as
   an inline SpanStyle in TactileDeckScreen.kt; bodyLarge was the BOLD
   AppTitleTextStyle; dynamicColor param accepted and never read; darkTheme
   defaulted false. Cures: DisplayUnitTextStyle (Sora Bold 50sp) created and
   assigned to displaySmall; headlineSmall/titleSmall assigned; Deck hero unit
   spans now reference DisplayUnitTextStyle instead of the inline hack;
   bodyLarge = honest 400 BodyTextStyle; bodySmall = dense-leading tier;
   labelLarge = button-word tier. Theme API: dynamicColor parameter REMOVED
   (brand palette is law; wallpaper colour can never take over or import green;
   MainActivity's hard-coded `dynamicColor = false` arg removed with it) and
   darkTheme is now a REQUIRED parameter — the caller (VM theme evaluation,
   routines+session per chrome C4, deliberately not the OS flag) owns the call.

## Computed contrast (relative-luminance ratios, WCAG formula, computed not eyeballed)

- Night onError NightColorOnPrimary #1B110D on NightAlert #FF8A5C: **7.98:1**
- NightAlert #FF8A5C on night background #171412: **7.90:1**
- Day onError #FBF1EC on TokenAlert #D63E06: 4.15:1 (passes 3:1 UI/large; day tone kept per audit scope)
- TokenAlert on day background #E4DFDA: 3.49:1 (UI-component tier)
- Night onTertiary #FFE4CF on NightColorSecondaryContainer #4A423C: **8.08:1**
- Night onTertiaryContainer #EDE3D8 on NightColorSurfaceBright #362C27: **10.72:1**
- Light onTertiary #F4EFEE on #615B5A: **5.85:1**; onTertiaryContainer #252323 on #D8D1CB: **10.35:1**
- Inverse pairs: light cream on block-bar #2E2A26 **12.81:1**; night dark ink on cream **15.23:1**
- surfaceBright text: day #2C2A29 on #FAF6F0 **13.27:1**; night #D2C6BB on #362C27 **8.11:1**

Fonts law held: only Sora / Atkinson Hyperlegible / Anonymous Pro styles added or
assigned. No green introduced (all new component tokens satisfy G < max(R,B);
TokenTrueBlack is achromatic 0,0,0, kept out of the census lists). No AI images.

Tests: new `ThemeWiringTest` (4 tests: surfaceBright tokens, staircase/inverse/scrim,
dark tertiary+error night tokens, all-slot typography + 50 tier + body honesty);
Robolectric zero-green census re-pointed at the real token lists.

## 20 seats (internal lens passes, signed)

- Crazy-ideas — proposed deleting the XML palette outright rather than re-syncing a third copy; adopted (single source of truth beats a synced lie). ✓
- Maximum-implementation-potential — pushed for full slot wiring in BOTH schemes in one pass, not just surfaceBright; adopted. ✓
- Brutal critic — "error == accent is a semantic lie worse than a dead token"; drove the NightAlert revival. ✓
- Improver — RGB comment correction + ladder comment now true; both landed. ✓
- Designer — container staircase steps only across existing tokens; no new palette colours invented. ✓
- Consumer — raised surfaces (Receipt/Airlock) finally show the intended raised tone instead of library grey. ✓
- Student — required-parameter darkTheme teaches callers the theme is evaluated, not defaulted. ✓
- User — night error now reads as error, not as another orange action button. ✓
- User-experience — scrims now come from the scheme, so day/night scrim behaviour is one rule. ✓
- Purpose-questioning — asked why dynamicColor existed at all; answer: it didn't do anything — removed, not defaulted. ✓
- Thoughtful — AOD true-black kept (OLED-off is a feature) but tokenised and named honestly. ✓
- Deep-thinker — traced the old 1.95:1 claim: it measured CREAM on NightAlert; with dark ink the pair is 7.98:1 — revival is sound. ✓
- Enthusiastic designer — DisplayUnitTextStyle gives the 50 tier a real home; hero spans reference it. ✓
- Wow-level designer — weighed heaviest: the staircase + inverse wiring makes M3 components (menus, dialogs) stop clashing with the house surfaces. ✓
- Designer-29-years — weighed heaviest: "aliases that lie are worse than no aliases" — full prune, census re-pointed, no compatibility shim kept. ✓
- Intentionality designer — weighed heaviest: every remaining hex in Color.kt has a name that says what it IS; component tones section is explicitly the only hex home. ✓
- Synchronous collaboration — diff kept checkbox-scoped so cluster siblings (screens issues) rebase cheaply; literal replacements are one-line each. ✓
- Implementation-quality — grep proofs recorded (zero Color(0x outside theme, zero Color.Black, zero @color consumers before deletion). ✓
- Technical execution — new ThemeWiringTest pins the wiring so a future scheme edit can't silently unwire a slot. ✓
- Interaction-detail — hero ghost shadows preserved at identical effective alpha via token.copy(alpha) (0.5/0.6/0.35/0.7 ≈ 0x80/0x99/0x59/0xB3). ✓

## IMPACT ON THE APP (plain words, for the person holding the phone)

- Raised cards and sheets (the receipt slip, the airlock panels, the Deck's raised
  card) now actually use the warm porcelain day tone / warm raised night tone the
  palette always defined for them — before, those surfaces silently fell back to
  stock Material grey-ish defaults that were never chosen. Day: brighter, cleaner
  porcelain. Night: a warm dark step instead of a default dark grey.
- Night error messages/alerts now wear the warm night alert orange (#FF8A5C)
  instead of the same orange as the main action accent — an error no longer looks
  like a button. Same orange family, slightly lighter, visibly distinct role.
- Night "tertiary" accents shift from a day grey (#615B5A) to the night cap tone
  (#4A423C) — subtly darker and warmer at night, as intended.
- Menu/dialog-style surfaces that read the container/inverse slots now follow the
  house staircase instead of library defaults (inverse = the dark block bar with
  cream text by day; cream with dark text by night).
- Scrims (dimming layers behind sheets) are now the scheme's true black at the
  same alphas as before — no visible change expected.
- NOTHING else changes visually: the XML palette deletion changes zero rendered
  pixels (proof below), alias deletion changes zero rendered pixels (every
  surviving call site names the same colour value via its real token), the
  Typography slot fills have zero current consumers in main code (grep-verified),
  and removing the dynamicColor parameter changes nothing because it was never read.

## NO-DOWNGRADE LEDGER (Sarthak's 04:58 rule)

- XML colors.xml deletion — NOT a downgrade: zero `@color/` references in res,
  zero R.color in Kotlin (grep-verified before deletion); nothing rendered from
  it, so zero pixels change. It was a rotting third copy, not a surface.
- Alias deletion — NOT a downgrade: 72/97 aliases had zero call sites anywhere;
  the rest were consumed only by Theme.kt (now names the identical token values)
  and one census test (now iterates the token lists). Same values, same pixels.
- Slot wiring — a change, judged EQUAL-OR-BETTER: the previous values were
  un-chosen library defaults; the new values are the tokens Color.kt defines
  precisely for those roles (TokenColorSurfaceBright is literally commented
  "The ONE raised card"). Restoring intent, not flattening.
- Night error NightAlert revival — same hue family, higher computed contrast
  (7.90:1 on ground vs accent-as-error's role confusion); not plainer.
- No finding was dispositioned DECISION-NEEDED: no cure required making any
  screen look worse, plainer, or less premium.

## Added seats (Sarthak's 05:08 + 05:16 orders — genuine passes, signed)

- FEEL SPECIALIST 1 (day/night atmosphere) — At 03:30 the night scheme reads as
  one warm dark room: ground #171412, text cream at 11:1-tier (not shouting),
  raised step #362C27 a quiet 1.35:1 stair — the wiring makes library surfaces
  join that room instead of importing stock dark-grey furniture. At noon the day
  scheme is warm stone + one porcelain highlight + one orange spent rarely; the
  staircase keeps sheets/cards in the same material family. Verdict: cures raise
  atmosphere coherence, no surface feels cheaper. ✓
- FEEL SPECIALIST 2 (surface texture) — The premium feel risk was the container
  slots: stock defaults would have made dialogs/menus feel like a different app
  pasted in. Assigning them the house staircase (bright→variant day, recessed→
  sheet night) keeps every M3 surface in the app's own material. Scrims moving
  to scheme.scrim at identical alphas: texture unchanged. Verdict: no downgrade;
  the texture is now uniformly the app's. ✓
- FRESH-EYES 1 (cold read, no audit text) — Read Color/Theme/Type cold: "19 day
  tokens, 19 night tokens, one accent, three fonts with assigned jobs (display /
  body / numbers). It reads as one intentional system. The raised-card tone being
  the brightest day surface makes hierarchy obvious." No cure read as a downgrade. ✓
- FRESH-EYES 2 (cold read, no audit text) — "The strange part was never the
  palette — it was the leftovers: names like canary for orange, XML files nobody
  uses, a settings knob (dynamicColor) that does nothing. Removing leftovers
  doesn't make an app plainer; it makes it believable." ✓
- KEEPER OF RECORD — Ledger reconciled below; every verdict carries its evidence
  (grep counts, computed ratios, CI run ids). DECISION-NEEDED items: none.
  Remainders: pre-existing main-baseline test failures (named in CI section),
  not #63's. ✓

## CI

- Run 40973555570 (first push, base e29acdf): Assemble green path reached Unit
  tests; test-compile failed — TruthCar3SweepTest stale ParkDrawer params
  (PRE-EXISTING on main) + my required-darkTheme fallout (fixed same pass).
- Run 40974360115: same, darkTheme fallout gone.
- Rebase onto origin/main 2d2683b (post design-revamp #167 + issue-61 #168):
  clean, 3 overlap files, all cures verified intact post-rebase.
- Run 40977840731 (rebased tip): **Assemble debug = SUCCESS**; Unit tests failed
  compile only on revamp-era tests passing the removed dynamicColor arg (8 sites,
  5 files — fixed) + the pre-existing ParkDrawer staleness is gone on new main.
- Run 40979891959: **Assemble debug = SUCCESS; tests EXECUTE — 296 tests, 39
  failed.** Diffed name-by-name against main's own run 40978007987 (issue-62
  merge): main's baseline is the SAME 39 failures (render/screenshot/DB-migration
  suites, pre-existing, outside #63). My branch's only unique failure was my own
  ThemeWiringTest assertion bug (titleSmall family) — fixed in df59977.
  ExampleRobolectric zero-green census (re-pointed) PASSES.
- Run 40983714858 (tip 788db5d): **Assemble debug = SUCCESS; 296 tests executed,
  37 failed — all in the pre-existing baseline set** (count fell 39→38→37 across
  my runs as flaky/baseline items passed; zero #63 failures, ThemeWiringTest and
  the re-pointed zero-green census pass).
- Post-report rebase onto origin/main 72d2440 (merges #179/#180 landed meanwhile):
  one conflict, ParkDrawer scrim line — resolved as scheme.scrim over main's new
  effectiveFraction. Final verdict run on the rebased tip is the run cited in the
  closing evidence comment on issue #63.
