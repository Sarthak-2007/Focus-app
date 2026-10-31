# PRUEFER — Verifier report to Zange, issue #14

**VERDICT: S-1 PARTIALLY CURED** — signed PRUEFER

The headline S-1 lie (the total converting UNPROVED into WITHOUT) is genuinely cured in code and pinned by tests. One residual S-1-class contradiction survives in the Return Chit suffix — the exact path commit `9ef5dfd` claimed to cure — for the `RETURNED_UNCHANGED` state. Evidence below, all at tip `dee277f8b1aacf9075ee914cc552263d7b2b86c8` on `agent/issue-14`, worktree read-only (no commit, no push, no edits, no gradle run).

## 1. What S-1 claimed vs what the cure commits changed

- Issue #14 (gh, verbatim sense): "the sheet total contradicts its cards — a granted door aged >45 s prints UNPROVED on-card while the total prints `WITHOUT ACCESSIBILITY — SESSIONS WILL NOT SEAL`." Audit `studio/plan/AUDIT-setup-sheet.md` §2 S-1 confirms the mechanism: old `receptionTotal` branched on `!= GRANTED`, and the old test `staleGrantReadsAsLackInTheTotal` codified the lie.
- `e8567fd` changed **tests + marking only** (3 files): renamed/corrected `totalMissingBranchWinsOverStale` → `totalPricesStaleAndMissingEachInItsOwnKindsWords`, added the 256-combo guard, added the S-8 comment in `ReceptionSheet.kt`. It did **not** touch `receptionTotal` itself — the production cure predates it in the Wave-1 graft, which matters for attribution.
- `9ef5dfd` changed: `DoorJobTextStyle` defined in `Type.kt` + import (compile-blocker, unrelated to S-1 wording), `SetupStore` holes, the `receptionTotal` doc comment, and the chit host search in `MainActivity.kt` (MISSING-only → `!= PermPrint.GRANTED`).
- Finisher commits touch nothing relevant: `86872c9` merges main (CI/audit files only), `fea53ac` is import/opt-in only, `dee277f` is `advanceTimeBy` in one test.

## 2. Core S-1 — CURED in current code

- `WatchStore.kt:355-364` `permPrintAt`: GRANTED + age > `STALE_AFTER_MS` (`WatchStore.kt:150`, 45_000) → `PermPrint.STALE`, never MISSING. Card word is `UNPROVED` for STALE (`ReceptionSheet.kt`, `receptionDoorWord`).
- `ReceptionSheet.kt:221-251` `receptionTotal`: prints derived solely via `permPrintAt` (:225); any UNMEASURED → `ReceptionTotal(null, "THE DOORS ARE STILL BEING READ.")` (:226-228); count = fresh-GRANTED only (:231); lacks priced per-kind in COUNTED order (:238-248) — MISSING → absence sentence (`ReceptionSheet.kt:201-210`), STALE → aged-proof sentence `ReceptionSheet.kt:218-219`: "THE LAST PROOF OF … AGED OUT — CHECK RE-PROVES IT." — asserts age only, never that the grant stands. All-fresh → "ALL FOUR DOORS SET." (:240), reached only when `lacks.isEmpty()`.
- Render gate: `GUARD ${total.count}/4` prints only when `count != null` (`ReceptionSheet.kt:804-810`).
- Sibling derivations are equally clean: `doorsMaskAt` sets a bit only on fresh GRANTED (`ReceptionSheet.kt:257-266`); `guardLineFor` counts fresh only, suffixes STALE by name, returns null on UNMEASURED (`GuardLine.kt:34-45`); `SetupStore.kt:43-45` `markCompleted()` now delegates to `stampSetup(0)` (no ledger-less stamp); `SetupStore.kt:95-107` `markCalledIn` checks COUNTED membership before shifting.
- Struck sentences: grep of `app/src/main` finds "FIRES AS SET" / "MID-SESSION" / "UNBREAKABLE" **only inside comments recording their striking** (`ReceptionSheet.kt:200`, `:313`) — no printed string carries them.

## 3. The 256-combo guard — enumeration verified, scope narrower than its name

`ReceptionSheetTest.kt:354` `totalNeverContradictsItselfAcrossAllDoorStateCombinations`: `for (code in 0 until 256)` (:366) = 256 iterations; inner loop over `WatchItemId.COUNTED` (:372), which has exactly 4 entries (`WatchStore.kt:98-100`); each door consumes one base-4 digit (`rest % 4`, `rest /= 4`, :373-380) — a bijection onto 4⁴ = **256 distinct combos, count confirmed**. Digits: 0 fresh-GRANTED, 1 stale-GRANTED (mono `now - 60_000`, past the 45 s bound), 2 MISSING, 3 absent = UNMEASURED.
Caveat Zange should carry: the guard pins **only** the count/unread contract (:385-397 — null count ⟺ reading sentence; printed count = fresh-GRANTED count). It never inspects sentence wording per kind. A STALE→WITHOUT wording regression with a correct count would **pass** this guard; it is caught only by the sibling tests in §5.

## 4. S-8 marking — honest

`ReceptionSheet.kt:684-692`: comment "S-8 — INTERIM FALSEHOOD, DO NOT SHIP SILENTLY" sits directly on THE ROOM beat, stating this tree has no 20 s settle and its desk clock prints one figure, not three. The printed line itself is unchanged (`ReceptionSheet.kt:693-694`). Marked in code, not silently fixed, not silently dropped — exactly what the Wave-2 report claims.

## 5. Tests — would they FAIL if the S-1 lie were reintroduced?

Yes for the total, by named function (`ReceptionSheetTest.kt`):
- STALE priced as WITHOUT → `staleGrantReadsAsAgedProofNotLackInTheTotal` (:242) fails (exact aged sentence, count 3, all-stale case asserts no "WITHOUT", count 0); also :275 and :323 fail.
- Branch-wins erasure, either direction → `totalPricesMixedLacksEachInItsOwnKindsWords` (:275, MISSING counted-first + STALE) and `totalPricesStaleAndMissingEachInItsOwnKindsWords` (:323, STALE counted-first + MISSING — the `e8567fd` correction) each fail; together they pin both orders.
- Count including STALE, or total printed over an unread door → `totalNeverPrintsOverAnUnreadDoor` (:227) and the 256 guard (:354) fail.
- Ledger upgrading aged proof → `SetupMechanismsTest.kt:215` `doorsMaskSetsFreshGrantsOnly` fails.

## 6. RESIDUAL — where S-1 still stands (exact path)

The chit suffix. Reachable chain, all current code:
1. `SetupStore.kt:255` `chitVerdictFor`: post-departure measurement still MISSING → `RETURNED_UNCHANGED`.
2. `MainActivity.kt:397-404`: the next-door search **excludes the visited door itself** (`other != c.door`) and scans only the other three for `!= PermPrint.GRANTED`. If those three are fresh-GRANTED, `next == null` and `resolveChit(RETURNED_UNCHANGED, null)` is stored.
3. `ReceptionSheet.kt:275-276` + `:281`: `chitLineFor` maps `nextDoor == null` → `" · ALL FOUR SET."` for **both** suffix-bearing states, so the printed line is: **"WENT OUT NOT SET · CAME BACK NOT SET. · ALL FOUR SET."** — it asserts all four set in the same line that proves the visited door NOT SET.
4. It does print: `chitPrintsAgainst` (`ReceptionSheet.kt:340-347`) lets an UNCHANGED chit print precisely while the door's current print is MISSING (:343), on the expanded card at `ReceptionSheet.kt:1064`.

`9ef5dfd`'s cure fixed the *sibling* half (STALE/UNMEASURED other doors now block the suffix) but not the visited door's own verdict. The REPAIRED + null case is sound (REPAIRED requires the visited door fresh-GRANTED). UNREAD/DARK are safe (no suffix appended, `ReceptionSheet.kt:282-283`), so the host's other null-resolutions (`MainActivity.kt:377-378`, `:423-430`) do not leak.

**No test catches this:** `SetupMechanismsTest.kt:147` `chitLinesAtMissingDeparture` tests UNCHANGED only with `nextDoor = DEVICE_ADMIN` (:156-159) and the null-suffix case only under REPAIRED (:152-155); there is no UNCHANGED + null case and no host-level test of the next-door computation. One-line fix direction for Zange (not applied — read-only seat): suppress the ALL-FOUR suffix for `RETURNED_UNCHANGED`, or include the visited door's verdict in the host's null condition, plus the missing test case.

**Bottom line:** receptionTotal S-1 — CURED, evidence above. Chit-suffix S-1 — STANDS for `RETURNED_UNCHANGED` at `ReceptionSheet.kt:276,281` via `MainActivity.kt:397-402`. Overall: **S-1 PARTIALLY CURED**.

— PRUEFER