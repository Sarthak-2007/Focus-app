# VERDICT — LAPSE CHECK, TRUTH CAR 3 ON THE MERGED BATCH-9 TREE (issue #5)

**Seat:** Hale, lapse-check session, Beweis's cell. **Method:** static evidence only — no compilation, no gradle, no tests run. Every charge carries file+line in the merged tree (`builds/merge-batch9/Focus-app`) unless a lane or baseline path is named. **Baseline note:** all trunk contrasts below use the true pristine baseline, `builds/truth-3-proofbase/Focus-app` (git f1f24ea, DB v11). The path `~/workspace/clone-inspect/Focus-app` was NOT used as a baseline — see Lapse 0.

---

## LAPSE 0 — THE TRUNK ITSELF LAPSED (verification integrity) — S1 (process)

**Claim:** `~/workspace/clone-inspect/Focus-app` is the studio's pristine trunk (batch-8, DB v11) — the ground every verification contrasts against.
**Fact:** that path's git HEAD is `3ea05bd` ("Batch 9 tests…") on top of `b792727` on top of `fff9edd` ("batch 9: Truth Car 3 + honest app list") — the batch-9 lineage overwrote the canonical trunk path after pass 1. Its `app/src/main` is byte-identical to the merged tree (full-tree diff: only CI/wrapper files differ).
**Evidence:** contaminated path — `AndroidManifest.xml:30` `allowBackup="false"` and `MonolithViewModel.kt:1484` a persisting `toggleShortsReelsSniper`; proofbase — `AndroidManifest.xml:25` `allowBackup="true"`, `MonolithDatabase.kt:25` `version = 11`, `MonolithViewModel.kt:1304–1307` the memory-only toggle.
**Consequence:** any verification run against that path between the overwrite and today would have certified the merge against itself — every "landed" check trivially green, every diff empty. Beleg's calibration caught it (0/3 pass-1 cites landed); my own pass caught it independently (the "pristine" path reads DB v12). The shipped code is unaffected; the *record* of what trunk is was false, silently. The studio needs a ruling on which path is trunk now, and the overwrite needs a name and a date in the log — currently neither exists.

---

## CATEGORY 1 — LANE CLAIMS vs MERGED REALITY

### LAPSE 1 — The entire times lane (Rurik, 6 of the car's 30 items) is absent from the merged tree — S1 (delivery claim)

**Claim:** batch-9 is "Truth Car 3" (commit `fff9edd`; merge map §2 composes an 11→12 migration of FOUR columns; §4 counts Rurik's FT-1…FT-6 + ST-04 among the car's 30 items).
**Merged evidence against it:**
- The composed migration carries **two** columns, not four — `MonolithDatabase.kt`, `MIGRATION_11_12`: `shortsReelsSniperEnabled` (Falk) and `breakTakenSeconds` (Tamm) only. No `peekMinutes`, no `breakMinutes`, no `breakSeconds`.
- `grep peekMinutes|breakMinutes` across merged `app/src/main`: zero hits in any entity, setter, or surface (the only `breakMinutes` in the tree is the unread `StudyModePreset` field, `MonolithModels.kt:64`, present since trunk). `data/EditableTimes.kt` — the lane's read-point, written in both Rurik's and Pastille's lanes — does not exist in the merged tree.
- `startPeekLease` still reads the constant: `MonolithRepository.kt:107` (`PEEK_DURATION_MILLIS = 120 * 1000L`), used at `:1196`. FT-3 (lease reads the setting; 0 = no grant) is unlanded — there is no setting to read.
- FT-2 (span derivations read the row's `breakSeconds` stamp; "the three literal 600s die") is unlanded: `MonolithModels.kt:401` still computes `(totalStudyBlocks - 1) * 600`, and `spanElapsedSeconds` reads `MonolithRepository.SAFE_BREAK_DURATION_SECONDS`. The merged code says so itself, honestly, in its own comments — `MonolithModels.kt:406–413` and `MonolithRepository.kt:1837–1845` both describe Rurik's stamp as a future event ("when the car's breakSeconds stamp lands… this reader re-points"). The code does not lie; the delivery claim does.
- ST-04 (add time to a running block by his hand, printed on receipt/Drawer) is absent whole: no verb, no EXTENDED line anywhere in `ui/` or `data/` (the only `extend*` in the tree is the pre-existing task-estimate overtime, `MonolithViewModel.kt:1814` — a different act).
- There is no `patch-truth-3-times.log` beside the other five lanes' patch logs in `builds/merge-batch9/` — the lane was never even patch-applied in the merge directory. Pruefer's log (`TRUTH3-PROOF-LOG.md:15`) still shows the lane PENDING, never declared. No test in the merged suite asserts the missing columns, so the absence is silent in the suite as well.

**Mitigation, stated plainly:** no printed number is falsified by this lapse — peek is still 2 minutes and breaks still 600 s everywhere, so every surface that prices them prices them truly. What is false is the completeness claim: a sixth of the car, including the only user-facing new verb it promised (ST-04), did not ship, and nothing in the delivery said so.

### LAPSE 2 — UNA2-06's render halves exist in no tree, merged included — S2

**Claim (pass 1, carried into the car):** the peek gains in-app furniture — a countdown derived from the stored end, and a reachable early-end (merge map §4, UNA2-06; seam S-6: closure requires both halves).
**Merged evidence:** the state machinery landed and is honest — `MonolithViewModel.kt:255–256` holds `FloatingPeekPillState`, and the ticker at `:986–1007` derives remaining from the Room row's `activePeekUntilElapsedRealtime`, never a tick count. But **no composable anywhere reads `floatingPeekPill`** (grep across `ui/` and `MainActivity.kt`: zero render sites — the pass-1 charge, verbatim, still true), and `end2MinutePeekEarly` (`:2418`) has exactly one caller: the VM's own ticker retiring an expired lease (`:994`). The overlay half is absent too: Pastille's `service/PillController.kt`, `PillFace.kt`, `PillTheme.kt` (pill lane) are not in the merged tree, and the merged `MonolithShieldService.kt` holds no pill controller. A man who takes a peek in batch-9 gets no countdown on any face and no ✕ to end it early by hand — precisely the defect UNA2-06 was charged with in trunk.

### LAPSE 3 — The no-fire lane (Wache, F1–F5) did not land — S3 (honestly printed)

The merge map seated nofire as an adjacent party to the same build. Merged evidence: no `service/CompletionSignal.kt`; `AndroidManifest.xml` carries no `USE_EXACT_ALARM` (the nofire lane's manifest has it at its `:15`); `RoutineScheduler.kt:44` still arms with inexact `setAndAllowWhileIdle`. **No false print results:** the plate prints the struck, weaker sentence — `ParkDrawer.kt:120` "ROUTINES: SET BY HAND ON SHIELD" — exactly as W-11 ordered while F1–F5 are unlanded. Filed as an unlanded adjacent build, not a lie; but any record that lists batch-9 as containing the no-fire cure is wrong.

### LAPSE 4 — The car's own verification gate never ran — S2 (process)

Merge map §5.1: "no lane enters the merge without Pruefer's PROVEN/HALF-PROVEN marks + Una's re-verification." `TRUTH3-PROOF-LOG.md` shows **all six lanes PENDING, no lane ever declared written, zero runs**; Una's `AUDIT-truth3-reverification.md` pass 2 reads, in full, "Nothing ruled yet." The merge shipped anyway. This session is, as far as the record shows, the first verification pass of any kind over the merged tree — after delivery.

**Clean in this category (verified landed, whole):** shorts/UNA-01 (column, gate, rendered toggle — see Cat. 3); sound/UNA-03 + social/UNA-02 (prefs store with `commit()` writes, `SoundEffectsSetting.kt`; every engine born from the fact, `TactileSoundEngine.kt:291`; seal `get()` re-reads at bind, `SessionSealTransition.kt:105`, `:300–320`; the heartbeat's engine-less bind at `RoutineHeartbeat.kt:142` now passes through that `get()`); sweep UNA-04 (manifest + both rules files exclude every domain — Cat. 3), UNA-06 (fallback dead, a11y `:267–285`), UNA-07 (LoadoutStore via VM `:377`, `:2100–2104`), UNA-08 (plate, `ParkDrawer.kt:122`), UNA-09 (`ShieldMatrixScreen.kt:309` counts `RoutineSchedule.liveCount`); intercept UNA2-01…05, UNA2-07, SIL-2, SIL-3, APP-1…APP-4 (details in Cat. 2/3; spent face is the single clause, `AirlockAndDisarmOverlays.kt:774`; intention line renders only when non-blank, `:646`; banner default "Deep Focus Session" and listener's invented body survive only as comments — listener stores the capture's own text, `MonolithNotificationListener.kt:89`; `onSimulateIntercept` and "hardware-level" have zero hits tree-wide); breaks ST-02/ST-03 (Cat. 3).

---

## CATEGORY 2 — REN'S MERGE-TIME FIXES, SEMANTIC NEIGHBOURS CHECKED

- **Restored lease block + sniper gating `!leaseLive` — CLEAN.** `MonolithAccessibilityService.kt:236–241` computes `leaseRow`/`leaseLive` (PEEK tier + live monotonic end, this package only); sniper gated at `:243`, site door gated identically at `:267` — gating one door did not leave the other inconsistent. The sniper still fires whenever no lease is live and the persisted switch is on (`shortsSniperArmed`, `:124–128`, missing row reads ON = pre-v12 truth). Per-package scoping is correct: a YouTube lease cannot silence Instagram. The tamper door (`:224`) fires before the lease check, but no lease can ever exist for it — the airlock sells peeks only on `APP_TIER`/`SHIELD_APP` paths (`peekOfferFor`, `MonolithViewModel.kt:141–149`), so the ordering is unreachable, not inconsistent. The shield door's set already excludes a leased PEEK app (`MonolithShieldService.kt:171`).
- **`sessionEndsAtMillis` span clock (Tor FIX-2) — CLEAN, one neighbour noted.** The airlock's line now reads `spanRemainingSeconds` (`MonolithViewModel.kt:2363–2370`), as does the Deck strip's TIME cell (`TactileDeckScreen.kt:525`) and the desk clock (`AirlockAndDisarmOverlays.kt:107`). Other TIME readers audited: receipt/freeze derivations already span-based (`MonolithViewModel.kt:1142`, `:1230–1235`). **Neighbour, filed as observation, not a lapse:** the Deck hero's `primaryReadout` still reads per-block `remainingSeconds` (`TactileDeckScreen.kt:312`), so during a Safe Break it shows 00:00 beside the "BREAK · n MIN" eyebrow (`:615`). This predates the merge (identical in proofbase), the span comment itself scopes its law to surfaces "with no block context" (`MonolithModels.kt:388–398`), and the Deck carries that context — but it is the same shape as the desk-clock charge Ren upheld, and nobody has ruled on it for the Deck. It wants a ruling, not silence.
- **`launchInterceptScreen` path args (02:27 cure) — CLEAN.** All three a11y launch sites pass their `InterceptPath`; the shield launcher passes `SHIELD_APP` (`MonolithShieldService.kt:186–188`); `MainActivity.kt:138–140` parses the extra with `UNKNOWN` as the conservative fallback — an unproven door sells no peek.
- **`breakBoundaryKnock` positional calls (02:27 cure) — CLEAN.** Both call sites are the flip points and only the flip points: entry at `MonolithRepository.kt:2039` (`false`, once — the written row routes later ticks to the countdown branch), exit inside the shared `advancePastBreak` at `:1872` (`true`), so expiry and hand-ended breaks sound identically, once. The engine behind it is born from the persisted sound fact (`:1827–1834`), so a knock on a sound-OFF install stays silent.
- **Tween easing fixes (02:27 cure) — CLEAN.** `MonolithCurves.kt` is identical to proofbase; the cure was call-site argument naming. No truth-bearing neighbour exists for an easing.

---

## CATEGORY 3 — THE WORDS BATCH-9 WAS DELIVERED WITH, ONE BY ONE

- **Honest app list — TRUE.** `AppCatalogReconcile.kt` prunes stored rows for uninstalled packages, discovers installed ones, seeds no invented usage (intercepts start at zero; usage follows the real query); the manifest's `<queries>` launcher declaration (`AndroidManifest.xml:17–22`) makes the installed set actually visible to the reconcile.
- **Shorts/Reels switch works — TRUE.** Toggle persists (`MonolithViewModel.kt:1484–1491` → `repository.setShortsReelsSniperEnabled`), renders in PARK (`ParkDrawer.kt:553+`), the sniper honours it per event (a11y `:243`), and the board word is fed from the same flow (`MonolithViewModel.kt:890–899` → `WatchStore.updateInterceptorInputs`). The STUDY loadout's force-on now writes through to Room too (`:2105–2119`) instead of moving memory only.
- **Sound + social-site settings persist — TRUE.** Sound: above (Cat. 1). Social: the PARK sled renders in both board states (`ParkDrawer.kt:373`, `:401`) and is wired to the persisted `browserBlockerEnabled` fact (`MainActivity.kt:669`), which the a11y site door reads per event.
- **Truthful block screen with SHUT/PEEK split — TRUE ON THE DECK; FALSE ON THE RECEIPT** (Lapse 6). Deck: `appsSplit` (`MonolithViewModel.kt:554+`) feeds `imprintTenantText`'s split print with the register's omission law (`MonolithCustomComponents.kt:450–477`), and the "· AS SEALED" tense of W-24 landed (`:461`).
- **End-break-early + true break record — TRUE.** Chip renders for exactly the break's life (`TactileDeckScreen.kt:930–938`, word "END BREAK" per W-26); VM verb guards on an active in-break session (`endSafeBreakEarly`); repository performs the expiry transition itself and books actually-taken seconds on both exits (`MonolithRepository.kt:1907–1933`, `:1998–2011`), and `spanElapsedSeconds` reads that ledger (`MonolithModels.kt:414–426`).
- **Lease honoured by every door — TRUE.** See Cat. 2, first item; the offer side matches the enforcement side (`peekOfferFor` + the second gate in `start2MinutePeek`, `MonolithViewModel.kt:2384–2396` — a stale airlock can no longer sell a lease, and an unlisted package arms nothing).
- **Span-true SESSION ENDS — TRUE.** See Cat. 2, second item; the airlock prints it via `airlockSessionLine` (`AirlockAndDisarmOverlays.kt:625`), stopwatch sessions print "SESSION RUNNING · NO SET END" and never an invented end.

---

## CATEGORY 4 — FALSE PRINTS THE DELIVERED BUILD STILL MAKES

### LAPSE 5 — The receipt footer still says the study RECORD lives in YPT — S2

**Claim charged in this car (W-43):** "THE MACHINE KEEPS ITS OWN LOG · YOUR STUDY RECORD LIVES IN YPT" — false; YPT holds his study *hours*, the app keeps its own record (Una UNA-08). Replacement ordered: "…YOUR STUDY HOURS LIVE IN YPT", with the law "W-13 and W-43 cure together or the charge reopens on whichever survives."
**Merged evidence:** W-13 cured — `ParkDrawer.kt:122` "STUDY HOURS: IN YPT". W-43 did not — `ReceiptSheet.kt:176` still prints "THE MACHINE KEEPS ITS OWN LOG · YOUR STUDY RECORD LIVES IN YPT" on every receipt. The charge reopens on the survivor, per the register's own terms. (The cure rode Rurik's appendix list — it died with Lapse 1's lane.)

### LAPSE 6 — The receipt's HELD line still flattens SHUT+PEEK into one noun — S2

**Claim charged in this car (W-41):** "HELD {n} APPS · {k} SITES", where n counts PEEK-tier apps as held — the imprint's lie (W-23) written into the permanent record; a PEEK app is not held, it meets the airlock and a lease, and unlocks freely in every Safe Break. Ordered cure: store the split at birth and print "HELD {n} SHUT · {m} PEEK · {k} SITES"; ordered fallback if the column were refused: "UNDER THE SEAL: {n} APPS · {k} SITES".
**Merged evidence:** neither landed. `MonolithRepository.kt:1318–1319` — `countHeldApps` is still `count { resolvedTier != OPEN }`; the birth capture stores that single merged number (`:1384–1390`); `ReceiptSheet.kt:129–131` prints "HELD ${heldAppsAtSeal} APPS · ${heldSitesAtSeal} SITES". The Deck was cured and the record was not — the false noun now ships in the one surface that is kept forever.

### LAPSE 7 — The plate's PEEK line and the peek badge are true by accident, not by cure — S3 (conditional)

W-08 ordered the plate's static "PEEK: 2 MINUTES, THEN SHUT AGAIN" replaced by the set value, because editable times would make it false. Merged `ParkDrawer.kt:112` still prints the static line, and the airlock badge derives from the constant (`AirlockAndDisarmOverlays.kt:669`, `peekBadgeText(MonolithRepository.PEEK_DURATION_SECONDS)` — its own comment concedes the source is the constant "the lease machinery reads"). Both prints are true today for exactly one reason: Lapse 1. Two failures cancel into an accident. The day anyone lands editable times without revisiting these two prints, both become standing lies — the register's file should keep this charge open against that day, not close it as cured.

**Closed here, for the record:** BUILD-CHANGES' "GENUINE failure 2 (OPEN)" — `TruthCar3SweepTest.una06_factoryDefaults_biteOnlyWhenStored` — is a **test-fixture defect, not merged logic**. `BlockedBrowserDomains.defaultDomains` is identical in proofbase, the sweep lane, and the merged tree (`MonolithModels.kt:508–517`) and contains `"youtube.com/shorts"`, never `"youtube.com"`; the test expects a plain watch URL to match `"youtube.com"` out of that list, which no tree can satisfy — it would fail in Bodo's own lane. Enforcement semantics (the stored list, as stored, is the enforced list) are exactly as UNA-06 ordered. Recommend Beweis close it as fixture and have the test's expectation corrected to the shorts URL it evidently meant.

---

## WHAT I WANT TO DO NEXT / UNASSIGNED, NOTICED

1. **Una's pass 2 was never written.** The five landed lanes have still had no per-lane re-verification against the merged tree (as opposed to their lane trees). I want that pass run — or this verdict explicitly accepted in its place — before batch-9 is called verified anywhere.
2. **Lapse 1 needs a ruling, not a repair by stealth:** either Rurik's lane gets a real merge slot (its tree is complete and self-consistent — columns, provider, setters, tests) or the batch-9 delivery record is amended to name what the car actually carried. The current record says "Truth Car 3" whole; six items say otherwise.
3. **Lapses 5 and 6 are small and priced:** the footer is one word; the HELD line needs either the split stored at birth (a column — it can ride whatever migration opens next) or Ren's ruling for the fallback line. Neither should wait for a car.
4. **UNA2-06's in-app render was written by nobody** — not Suki's lane, not Pastille's, not the merge. If a seat is assigned, the state and derivation are already in the VM waiting for a face; the overlay half additionally needs the pill lane merged or formally struck from the car's scope.
5. **Unassigned, noticed:** the Deck-hero-during-break question (Cat. 2) wants a ruling; the studio's trunk path (Lapse 0) wants a named owner and a log entry for the overwrite; and Wache's F1–F5 (Lapse 3) still has no build — the plate's struck sentence is honest, but it is also now the longest-standing interim print in the app.

— HALE, Truth Car 3 verification cell (issue #5)
