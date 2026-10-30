# Issue #88 — Wave-3 Report (Audit R2 · l10n-rtl-fontscale)

**Cell:** FLUT, Wave-3B cluster lead, branch `agent/issue-88`, base origin/main @ 0a00e95.
**Crew note (honest):** the roster seats could not be spawned (fleet saturation, DB
pool timeouts at seating time — 60+ agents already live). The bundle's verify+fix work
is LEAD-COVERED: every finding re-verified by the lead against current main with
file+line evidence, every fix lead-written and script-checked. Seats: bundled,
marked covered-by-lead per the programme's own precedent.

## User-facing changes (plain words)
- **Every clock in the app now follows the phone's 12/24-hour setting.** A user on
  12-hour time sees "ENDS 5:30 PM" instead of "ENDS 17:30" — on the Deck, the
  seal drawer, the receipt, the launcher block screen, the Drawer's finish times,
  the sequencer, the airlock. Users on 24-hour time see no change.
- **Drawer day/month headings now print in the device language** (they were pinned
  to US English).
- **Hindi/Arabic app names and task titles no longer render as boxes** on the
  Canvas-drawn surfaces (lock dial, switches, suitcase cards): the bundled fonts
  gain a system-font fallback for glyphs they don't contain (Android 10+).
- **In right-to-left locales the shield cap selector's moving cap now sits over the
  correct word** (before, it covered the wrong stop while the tap zones mirrored).
- Nothing else changes. No feature removed, no look changed for the default user.

## Findings

1. **[HIGH] Hardcoded `HH:mm` (18 sites) + `Locale.US` day labels — FIXED.**
   New `com.example.data.ClockText` reads the system setting
   (`DateFormat.getTimeFormat` / `DateFormat.is24HourFormat`). Display sites
   converted: `MonolithRepository.hhmm` (within-lines), `ReceiptSheet.hhmm` (4
   callers), `ParkDrawer` seal clock, `endsAtText` + `airlockSessionLine` +
   `captureTimeLabel` + the frozen-capture stamp (Airlock), `LauncherBlockScreen`
   ENDS, `DrawerScreen.clockTime` (3 callers), `FocusSequencerScreen` FINISHED.
   Pure print helpers take the `is24Hour: Boolean` (not a Context) so their JVM
   tests pin BOTH branches — `SliceDGestureAndStripTest` and
   `TruthCar3InterceptTest` updated accordingly. Machine formats deliberately
   untouched: `yyyy-MM-dd`/ISO instants are data, not display. `DrawerScreen`
   `headingLabel`/`monthLabel` moved `Locale.US` → `Locale.getDefault()`.

2. **[HIGH] Default-locale `.lowercase()` in matching paths — FIXED (12 pins).**
   `Locale.ROOT` added where code MATCHES, SORTS or KEYS (the audit's own Turkish
   failures): `MonolithModels` frozen-category `when`, `normalizeDomain`;
   `MonolithRepository` package categoriser, smart-loadout key, dedupe key,
   synthetic `com.custom.` package name; `AppRules` + `ShieldMatrixScreen` name
   sort comparators; `MonolithAccessibilityService` 4 keyword scans; `DrawerScreen`
   declare-word testTag. Display-only case transforms (ViewModel warning strings,
   ShieldMatrix printed labels, Drawer day-key) verified NOT matching-critical and
   left by design — listed here so the strike is explicit, not silent.

3. **[MEDIUM] RTL — PART FIXED, PART STANDS-BLOCKED (priced).**
   FIXED: `ShieldCapSelector` — `Modifier.offset(x)` is physical and did not
   mirror while its Row/zones did; travel is now mirrored under
   `LocalLayoutDirection` (LTR formula byte-identical).
   STANDS-BLOCKED: `RailView` (~40 absolute-x draws + touch dx in
   `BlockitRailView.kt`) and Canvas `Align.LEFT` sites in `BlockitCoreViews` —
   mirroring the app's primary navigation rail blind, with no RTL render proof
   available, risks the proven LTR rail. Price: ~0.5 day with an RTL screenshot
   harness (compose `createAndroidComposeRule` under an `ar` locale config) +
   phone check. Owner: rail lane.
   `supportsRtl` deliberately NOT touched (flipping it app-wide un-mirrored would
   be a downgrade).

4. **[MEDIUM] Canvas sp() vs fixed dp boxes at 1.3× font scale — STANDS-BLOCKED
   (priced).** Verified: `BlockitCoreViews.sp()` scales by `scaledDensity` while
   boxes sized by `dp()` don't; 13 fixed two-line draw sites across the Blockit
   view files collide at 1.3×. The true cure is fontScale-aware baseline layout
   in three keeper-surface view files — visual surgery on the app's most beloved
   surfaces with no render-diff proof available in CI. Price: ~0.5–1 day
   including a 1.3× screenshot sweep; owner: Blockit-views lane (with #59/#60).

5. **[MEDIUM] Bundled fonts: no Devanagari/Arabic — CANVAS HALF FIXED, COMPOSE
   HALF STANDS-BLOCKED (priced).** Verified by codepoint census: Atkinson covers
   U+0900–097F in 0 of its glyphs (0 Devanagari codepoints), Anonymous Pro
   likewise; both are Latin/Greek/Cyrillic only. Canvas: `BlockitCoreViews`' four
   typefaces now rebuild via `Typeface.CustomFallbackBuilder` on API 29+ (bundled
   face for its glyphs, system fallback for the rest; Latin rendering unchanged;
   below 29 the plain face, exactly as before). Compose: whether Minikin already
   falls back for `Type.kt`'s resource families is a device render question, not
   a code question — price: one Hindi/Arabic render check on device; if broken,
   per-weight fallback families in `Type.kt`, ~0.5 day. Owner: first phone pass.

## Counts
5 findings: **2 fixed outright, 2 part-fixed with the remainder priced-blocked,
1 priced-blocked.** Where a remainder stands open it is named above with owner
and price — nothing silent.

— FLUT, Wave-3B cluster lead, for Ren.
