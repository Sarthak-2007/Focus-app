# Issue #58 — Shield Cap + GuardLine — Wave-3 Report (RAMPE / C8 Schwelle)

Branch `agent/issue-58`, base origin/main `e29acdf`. Verified against current
main AND `origin/agent/issue-7` (no GuardLine/ShieldCap/WatchStore changes —
empty diff on all three files) AND `origin/agent/issue-9` (GuardLine change is
only the UNMEASURED print `GUARD — NOT CHECKED` + WatchStore sweep-gen work;
it does not fix any #58 finding — its GuardLine still hardcodes `4/4`).
Conclusion: nothing was FIXED-ELSEWHERE; all six findings stood on main and
are FIXED-IN-CODE here.

## Dispositions (6/6)

| # | Sev | Finding | Disposition | Evidence |
|---|-----|---------|-------------|----------|
| F1 | HIGH | MISSING never goes stale; ancient denial prints as current forever | **FIXED-IN-CODE** | Was: `WatchStore.kt` permPrintAt "MISSING never stales" (old test even asserted MISSING at nowMono=9,999,999). Fix: new `PermPrint.MISSING_STALE` — any measurement past `STALE_AFTER_MS` (45s) is stale; a stale denial returns MISSING_STALE, GuardLine prints `NAME STALE`. Distinct enum value (not a collapse into STALE) so PARK's FIX key still knows the item is *missing*: `ParkDrawer.kt` firstMissing accepts MISSING \|\| MISSING_STALE, its board prints MISSING_STALE as `STALE`. Tests: boundary at exactly +45_000 (still MISSING) / +45_001 (MISSING_STALE), stale-missing Guard line, fresh-missing bare line. |
| F2 | HIGH | Selector stops accessibility-dead: bare clickable Boxes, no label/role/selected | **FIXED-IN-CODE** | `ShieldCapSelector.kt` tap zones: `.clickable` → `.selectable(selected = i == clampedIndex, role = Role.RadioButton)` (publishes role + selected state) + `.semantics { contentDescription = "${words[i]} loadout" }`. TalkBack now gets three named radio buttons with selection, not three bare targets. testTags `shield_cap_stop_$i` preserved. |
| F3 | MED | Tap targets 36dp < 48dp minimum | **FIXED-IN-CODE** | Design-consistent cure, internal geometry AND position identical — arithmetic corrected after the C8 acceleration verifier caught a 3dp error in the first version of this row: old content 36+6+8 = 50dp centred in the 84dp slot → track top 17dp, ticks top 59dp. A *centred* 48dp wrapper (new content 48+8 = 56dp) would put the track at 20dp (+3dp shift — the first commit's flaw). Cure as landed: Column is top-aligned with an 11dp offset — wrapper top 11dp, track top 11+6 = **17dp**, ticks top 11+48 = **59dp**, both exactly old. Ticks drop their 6dp top padding because the wrapper's lower overhang *is* that 6dp. Cap 30dp, radii 12/9, words untouched; the 84dp slot is fixed so nothing downstream moves either. |
| F4 | MED | Re-tapping selected stop plays firm click + refires onSelect, re-applying loadout | **FIXED-IN-CODE** | Pure guard `ShieldCap.shouldHandleTap(current, tapped)` in the selector file; the tap handler fires sound + onSelect only when it returns true. Unit-tested for every stop (re-tap never fires, every real move fires). |
| F5 | MED | GuardLine hardcodes denominator '4' while comparing COUNTED.size | **FIXED-IN-CODE** | `GuardLine.kt`: `val total = WatchItemId.COUNTED.size`; whole prints `GUARD $total/$total`, lack prints `GUARD $warranted/$total — $name`. Test builds its snapshots from COUNTED itself and asserts the printed fraction's denominator equals COUNTED.size in both states. |
| F6 | LOW | Unbounded selectedIndex + zero tests for duplicated index mapping | **FIXED-IN-CODE** | (a) Clamp: `ShieldCap.clampIndex` (coerceIn 0..STOP_COUNT-1) applied before *every* use — word lookup, cap offset, ticks, semantics — Int.MIN/MAX tested. (b) One mapping: `LoadoutStore.CAP_ORDER` is now the single index↔loadout source; `capIndexOf` = indexOf, new `loadoutAtCapIndex` = getOrNull (null for garbage, never SLEEP-by-else). Selector's STOP_COUNT derives from CAP_ORDER.size; `ShieldMatrixScreen` resolves taps through loadoutAtCapIndex and applies nothing for null — its hand-written `when/else->SLEEP` is deleted. Tests: full round-trip both directions for every stop/loadout, out-of-range → null, word/mapping counts equal. |

Counts: VERIFIED-FALSE 0 · FIXED-ELSEWHERE 0 · **FIXED-IN-CODE 6** · STANDS-BLOCKED 0.

## Files changed
- `service/WatchStore.kt` — PermPrint.MISSING_STALE + symmetric staleness in permPrintAt.
- `ui/components/GuardLine.kt` — STALE suffix for MISSING_STALE; denominator from COUNTED.size.
- `ui/components/ParkDrawer.kt` — board prints MISSING_STALE as STALE; FIX key still targets stale denials.
- `ui/components/ShieldCapSelector.kt` — ShieldCap pure object (clamp/guard/stop words), selectable+semantics stops, 48dp hit wrapper around the untouched 36dp track.
- `data/LoadoutStore.kt` — CAP_ORDER single source + loadoutAtCapIndex.
- `ui/screens/ShieldMatrixScreen.kt` — caller resolves via the one mapping; else->SLEEP deleted.
- Tests: new `ShieldCapGuardLineWave3Test.kt` (8 tests); `ChromeWaveUnitTest.kt` permPrint test updated — its old assertion *encoded* the F1 bug (MISSING at 9,999,999ms), renamed `permPrint_staleForAnyMeasurementPast45s` with the 45s boundary asserted for denials too.

## The 20 seats (each a distinct lens, signed)

1. **Crazy-ideas** — Proposed printing the denial's *age* on the Guard line ("ACCESSIBILITY — 3H OLD"). Rejected by the panel: the line's grammar is name + STALE suffix; inventing a second grammar for one state breaks the print law. The MISSING_STALE enum, however, came from this seat's insistence that staleness is a property of the *measurement*, not the value.
2. **Maximum-implementation-potential** — Pushed the fix to the root (permPrintAt) instead of patching GuardLine's renderer: every present and future consumer inherits honest staleness. Also drove CAP_ORDER as a list both mapping directions derive from, not two mirrored `when`s.
3. **Brutal critic** — "The old ChromeWave test didn't miss the bug, it *blessed* it: asserting MISSING at t=9,999,999 is a test defending a lie." Correct — the test is renamed and its assertion reversed, noted in the disposition so nobody reads the change as test-tampering. Also: F3's first draft (just height(48) on the track) would have silently grown the cap — killed.
4. **Improver** — ParkDrawer follow-through: fixing permPrintAt without touching PARK would have made the FIX key vanish for stale denials and crashed its exhaustive `when`. Both spots cured in the same commit; that cross-file sweep is the improvement over a GuardLine-only patch.
5. **Designer** — Defended the rendered object Sarthak picked: 36dp track / 30dp cap / radii / ticks are pixel-law. The 48dp wrapper achieves the platform minimum with the visible object bit-identical (see the 17dp arithmetic in F3).
6. **Consumer** — As the man buying the phone's promise: "GUARD 3/4" with a three-hour-old denial underneath is a false label. After the fix the same screen says STALE — the label now expires like the fact does.
7. **Student** — NEET-morning test: at 3:30 AM, half awake, a bare missing name reads as "fix this one thing"; STALE reads as "re-check" — which is the true next action, because the sweep, not the permission, may be what's broken.
8. **User** — Re-tap rage: tapping the resting STUDY cap used to click and re-apply the whole loadout. A control that punishes an accidental touch with a state reload is hostile. Now it's inert, as a resting control should be.
9. **User-experience** — TalkBack walk-through, before: three unlabeled click targets in a row, selection unknowable. After: "STUDY loadout, selected, radio button" / "WORK loadout, radio button" / "SLEEP loadout, radio button" — the selector is finally operable eyes-free.
10. **Purpose-questioning** — Asked why staleness existed only for GRANTED at all. Answer found in code: none — it was an accident of the `when` ordering, documented as if deliberate ("MISSING never stales"). A purpose that can't be stated isn't a purpose; the asymmetry died.
11. **Thoughtful** — Considered the harm of the opposite error: if a *fresh* denial printed STALE, he'd distrust real lacks. The boundary tests (fresh denial bare at 10s age, stale only past the same 45s a grant gets) keep both errors out.
12. **Deep-thinker** — MISSING_STALE vs collapsing into STALE: collapsing would lose the one bit PARK's FIX key acts on (missing-ness). Truth here is two-dimensional (value × age); the enum now carries both instead of letting one axis eat the other.
13. **Enthusiastic designer** — The hit-wrapper trick is the kind of solution worth celebrating: the platform minimum and the picked artwork stop being enemies. The invisible 6dp overhang is load-bearing and commented as such so no future tidy-up "fixes" it away.
14. **Wow-level designer** — Audited the result against the wow bar: nothing visual changed — and that *is* the wow here. An accessibility + target-size overhaul the eye cannot detect is craft, not compromise.
15. **Designer with 29 years of experience** — RadioButton, not Tab and not plain Button: three mutually exclusive loadouts, exactly one resting — that is a radio group, and has been the correct pattern for three decades. Also insisted selected state ride the semantics, not a contentDescription sentence, so switch/voice access get it structurally.
16. **Intentionality-focused designer** — Every dp now has a stated reason in code comments: 48 (platform minimum), 36 (picked artwork), 6 (overhang = old tick gap), 84 (inherited slot). No accidental numbers left in the selector.
17. **Synchronous collaboration** — Cross-checked #7 (empty on these files) and #9 (UNMEASURED print only — will rebase cleanly: its GuardLine hunk touches the null/UNMEASURED path, this branch's hunks touch the fraction/name path; WatchStore hunks are in different functions). Flagged the one true overlap — #9's "GUARD n/4 + foot signature" PARK work vs F5's denominator: #9 kept the literal in PARK prints, which is #9/#57 scope, not this slice; merge train should note it.
18. **Implementation-quality** — Pure logic extracted (ShieldCap object) precisely so F4/F6 are unit-testable without Compose; the composable keeps zero decision logic beyond calling it. No new dependencies, no schema, no settings.
19. **Technical execution specialist** — Compile-risk sweep: exhaustive `when` over PermPrint occurs only in ParkDrawer (updated); all other consumers compare `== GRANTED` / `!= GRANTED` (MISSING_STALE behaves correctly under both — it is not warranted). `selectable` needs no extra import beyond foundation.selection; the removed `clickable` import has no remaining use in the file. SmartLoadout import in ShieldMatrixScreen still used by its signature.
20. **Interaction-detail specialist** — Sound discipline: the firm click is *change* feedback; it now sounds only on an actual change (F4). The re-tap is fully silent and inert — no half-feedback (sound without action or action without sound), which is the detail that made the old behaviour feel like a lie.

## CI — exact status (verifier catch 2, accepted)
Run 40973604438 on 2761476: **assembleDebug GREEN** (BUILD SUCCESSFUL —
all #58 main code compiles), but `compileDebugUnitTestKotlin` FAILED on
pre-existing stale test call-sites in 5 unrelated files
(ScreenshotHarnessTest, ShortsToggleRenderTest, SoundAndSocialTruthTest,
TruthCar3CaptureBodyTest, TruthCar3SweepTest) — byte-identical to main
@ e29acdf's own run 40970102642. No #58 file is in the error list, but
the 9 new/updated #58 tests therefore have **not executed in CI**, so
this run is NOT a closure run and #58 is **not closed on it**. The known
cure is agent/issue-7 @ 082440e (repairs exactly those 5 files); per the
verifier it must land via the merge flow — #58 does not absorb another
issue's branch. Closure path: rebase agent/issue-58 onto the repaired
mainline when it lands → fresh CI run with tests executing → close +
merge in that pass. Local gradle not run, per dispatch.
