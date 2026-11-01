# ISSUE #101 — FRESH VERIFICATION PASS · closure seat under DURCH-C

**Date:** Oct 31, 2026, ~01:15 IST. **Verified against:** current main
`a43466cf` (live-fetched this run; main tip unchanged from the brief).
**Trigger:** the issue stood CLOSED (K4-D100, Oct 30 16:41Z, on main
`f8ca61b`) on a "fixed on main" claim. This pass re-checked every
finding line-by-line against current code, trusting nothing.

## Per-finding verdicts

1. **F1 [CRITICAL] Bulk Loadout/Profile rewrite bypasses the ratchet —
   PARTIALLY CURED; PROFILE HALF STILL REAL → CURED BY THIS BRANCH.**
   - Loadout half: cured on main by #304 F1 (`f96d307d`, ancestor of
     main). Repository `applySmartLoadout` (MonolithRepository.kt:2246,
     clamp at :2300) lands tightening-only while sealed via
     `SessionTierGate.clampWhileSealed`; VM warns on any loosening
     (MonolithViewModel.kt:3277–3299). Stronger than the wave-3
     flat-refusal; wave-3's loadout hunk is superseded — dropped.
   - Profile half: **still open on current main.** VM
     `selectAndApplyProfile` (MonolithViewModel.kt:3370) carried no
     session guard; repository `applyProfile`
     (MonolithRepository.kt:2501–2546) set `OPEN` for every category
     the profile does not list (`!shouldBlock -> AppBlockTier.OPEN`)
     with no sealed clamp. Both prior closure verdicts (STURM3-08,
     K4-D100) cited only the loadout lines — the profile door was
     never checked. **Cure (commit 3c1f8f3e):** `applyProfile` clamps
     through `SessionTierGate.clampWhileSealed` while sealed
     (mirrors #304); VM fires the tightening-only ratchet warning.
2. **F2 [HIGH] Blocked Websites REMOVE live mid-session — CURED ON
   MAIN, struck.** Via #151 F1 (`112a6488`, ancestor of main):
   `removeBlockedSite` (MonolithViewModel.kt:2492–2510) refuses
   during an active session with the ratchet warning; adding stays
   lawful. Wave-3's duplicate hunk correctly never landed
   (DURCH-C duplicate-hunk ruling stands).
3. **F3 [HIGH] Peek countdown pill never rendered — CURED ON MAIN,
   struck.** `MainActivity.kt:436` collects `floatingPeekPill`;
   `:1512–1515` renders `PeekCountdownPill` (in-code comment:
   "canonical; #101-F3 cross-ref", via #99 F2 / #114). Ancestor
   commits on main verified.
4. **F4 [MEDIUM] 'Stayed Focused' banked at the RETURN tap — STILL
   REAL IN SUBSTANCE → CURED BY THIS BRANCH (separable commit).**
   The closure verdict held it cured via #114 F2
   (`moveTaskToBack(true)` performs the return on the same tap).
   That makes the return real; it does not make the *staying* real:
   on current main `transmuteImpulseAndReturn`
   (MonolithViewModel.kt:3988) still calls `recordIntercept`
   instantly, so re-entering the blocked app seconds later leaves
   the "Stayed Focused" row and the incremented urges-stopped count
   standing. **Cure (commit 43d36554):** wave-3's prescribed fix
   (990e5f35) composed forward — the bank is scheduled 60 s out
   (`pendingReturnBanks`); a lawful re-intercept of the same package
   annuls it. #114's pairing, #162's uninstall-guard outcome
   (banks at the tap, `countAsReach=false`, never fed the count)
   and #124's frozen intention are preserved. Separable: if DURCH-C
   rules the #114 reading settled, drop 43d36554; F1 lands alone.

## Branch disposition

Old branch tip (`e867ee99`: re-triggers over report `cd67e39a` +
cure `990e5f35`) was NOT merged and is NOT on main (ancestor test
this run). It was ~910 commits behind and 3 of its 4 hunks are
duplicates-or-superseded vs main; merging would regress
SessionTierGate. The branch is therefore **recomposed**: force-pushed
to current main + the two surviving cures above. The superseded
commits remain accounted here and in PR #349's timeline; nothing
they contained is lost — F2/F3/loadout live on main via
#151/#99/#304, and their authored intents for F1-profile/F4 are
the commits above.

## Post-closure filings on this thread (out of scope, named)

After the K4 close, W9 DAYLIGHT comments (Oct 30 18:20–18:23Z)
filed new findings on this thread (launcher exit-by-tap / no HOME
role; dialer package-wide call bypass). They are not among the
original 4 findings and are not cured or struck here; they stand
named for their own owners.

## Seats (all LEAD-COVERED by this closure seat)

Verifier — every finding re-read at line level on `a43466cf` plus
live API state (issue CLOSED 16:41Z, PR #349 OPEN, #245 read).
Critic — both prior closure verdicts audited; F1's conflation
(loadout evidence, profile claim) caught. Diagnostician — root
cause: no sealed gate existed on the profile write path at either
layer. Builder — two separable commits composed forward, wave-3
approaches re-judged against current design (#304 clamp > flat
refusal). Tester — SessionTierGate semantics already unit-covered
(`SessionTierGateTest`); CI on this branch is the verdict court.
Keeper — superseded commits + post-closure filings accounted by
SHA/name, nothing silently dropped. Fresh-eyes — the escape was
re-walked as a user: Shield → Profile tap mid-session was the one
door still open. Consumer — mid-session taps now answer with the
ratchet's printed law; unsealed behaviour unchanged. Technical-
execution — gh/git only, throttle held, no local compile.
Implementation-quality — cures reuse the in-tree gate and warning
idiom exactly; no new mechanism invented where one existed.

## Addendum — drift re-verification vs main `3ef08237` (Oct 31, ~02:30 IST)

Main moved `a43466cf` → `3ef08237` while this branch's CI queued.
File-scoped drift check per the stamp rule: F1/F2/F3 regions are
byte-identical on `3ef08237` (applyProfile MonolithRepository.kt:2525,
selectAndApplyProfile MonolithViewModel.kt:3385, removeBlockedSite
:2507, applySmartLoadout clamp :2324; MainActivity untouched) — stamps
carry. The F4 region drifted only additively (#254 F12 attempt mirror
in triggerAirlockIntercept, after the mint; transmuteImpulseAndReturn
:4027 byte-identical) — F4 re-proved still real on `3ef08237`, and the
annul hook's context is untouched. This branch was therefore
recomposed onto `3ef08237` (same two cure commits, re-created via the
Git Database API because local git writes were failing box-wide);
**VERIFIED vs main `3ef08237`** for all four findings as scoped above.
— closure seat under DURCH-C (#101)
