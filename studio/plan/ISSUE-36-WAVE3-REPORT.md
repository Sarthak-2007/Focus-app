# Issue #36 — crypto-integrity · Wave-3 report (cluster C3 / HERZ)

Branch `agent/issue-36`, base `origin/main @ e29acdf`. All findings re-verified
first-hand against `CryptoIntegrityManager.kt` (94 lines pre-fix) on this base.

**Governing constraint:** the integrity/HMAC design is parked decision **#21** —
Sarthak's to rule. This bundle fixed ONLY the decision-independent finding,
documented the decision-dependent ones with full evidence, and changed nothing
about what the signature covers, key provenance, or blank-signature semantics.
The issue is NOT closed and its body is NOT edited: 4 of 5 findings await the
#21 ruling.

Commits:
- `ccde4f1` — finding 5 fix + `CryptoIntegrityManagerTest` (8 tests)
- `3dc2902` — finding 4 doc-word cure only (separate commit, zero behaviour change)
- this report

---

## Finding 1 [CRITICAL] Blank signature accepted as valid — DECISION-DEPENDENT-#21 (left unchecked)

Evidence, re-verified:
- `CryptoIntegrityManager.kt:84` (pre-fix numbering): `if (session.hmacSignature.isBlank()) return true`.
- Entity default: `MonolithModels.kt:279` — `val hmacSignature: String = ""`.

**Which writers leave `hmacSignature` blank?** Grep over `app/src/main`:
- There is exactly ONE `FocusSessionEntity(...)` construction site in main code:
  `MonolithRepository.kt:1347` (`startSession`), and it always signs first
  (`signSessionState` at :1339, `hmacSignature = hmac` at :1357) before
  `dao.insertSession` at :1387.
- Every other session write is a `.copy()` of a row loaded from Room
  (`updateSession` sites at Repository :941, :1008, :1036, :1066, :1435, :1924,
  :1982, :2009, :2017, :2033, :2100) — `copy()` carries the stored signature
  forward; none re-blanks it. Re-signing writes: :1440 (session re-anchor),
  :1884 (`advancePastBreak`), :1977 (reboot re-anchor inside
  `updateSessionProgress`; the non-reboot path at :1965 explicitly carries
  `session.hmacSignature`), :2081 (ending write).
- Conclusion: **no app write path persists a fresh blank signature.** Blank
  rows arise only from (a) the entity default `""` for any row never passed
  through `startSession`, and (b) any pre-signing-era/legacy row. The live
  exposure is the one the audit named: an attacker with local DB write access
  clears the column and the tamper check opts itself out.

**What verify's callers do with the verdict:**
- `MonolithRepository.kt:741` (`computeVerifiedSessionElapsed`): `hmacValid`
  feeds `tamper` in BOTH branches — non-reboot: `tamper = drift > 90L || !hmacValid`
  (:749); reboot: `Pair(priorElapsed + sinceRebootSec, !hmacValid)` (:756).
  `tamper` drives the penalty in `updateSessionProgress` (:1947):
  `if (tamper && session.penaltySecondsAdded == 0) HMAC_TAMPER_PENALTY_SECONDS`
  (900 s = 15 min, const at :111) and persists `tamperDetected = tamper`.
  So blank ⇒ verify `true` ⇒ no penalty, no tamper flag.
- `MonolithRepository.kt:773` (`getSystemGuardStatus`): verdict surfaces as
  `SystemGuardStatus.isHmacSignatureValid` (:784; field default `true` at :52) —
  the guard-status truth surface. Blank ⇒ the surface reports the signature
  "valid" for a row that has none.

Why decision-dependent: whether unsigned = tamper, and what happens to legacy
unsigned rows, IS the design. Not touched; the finding-5 fix and its test suite
explicitly preserve and pin the current semantics.

## Finding 2 [CRITICAL] Hardcoded publicly-known fallback HMAC key, silent downgrade — DECISION-DEPENDENT-#21 (left unchecked)

Evidence, re-verified:
- `CryptoIntegrityManager.kt:28-31`: `fallbackHmacKey = SecretKeySpec("Monolith_Hardware_HMAC_Fallback_32B!"…)` —
  a literal shipped in the APK. (The comment at :27 says "256-bit"; the literal
  is 36 bytes, as the audit noted — left as-is, it is part of this finding.)
- **Every downgrade path** in `signSessionState` (pre-fix lines):
  1. `getHardwareHmacKeyOrNull()` returns null → `mac.init(fallbackHmacKey)` (:71-73);
  2. `mac.init(hwKey)` throws → `mac.init(fallbackHmacKey)` (:67-69);
  3. outer catch-all (any exception, incl. `Mac.getInstance`) → fresh Mac on
     `fallbackHmacKey` (:76-80).
  `getHardwareHmacKeyOrNull()` itself swallows EVERY exception to null (:33-50),
  so any KeyStore failure funnels into path 1. There is **no logging anywhere in
  the file** (grep: no `Log` import/call) and **no marker in the signature**
  distinguishing fallback-signed from KeyStore-signed rows. Pre-fix, verify
  inherited all three paths by delegating to `signSessionState`.
- **Test dependence:** the only test in the repo touching
  `CryptoIntegrityManager` is `ExampleRobolectricTest.kt:84-119`
  (`verify monotonic clock anti-tamper and HMAC signature verification` — signs
  at :89, asserts verify true at :112, tampered-copy false at :118, via
  `computeVerifiedSessionElapsed`). In any runtime where AndroidKeyStore HMAC
  is unavailable, sign and verify both silently use the same fallback key, so
  the test passes without ever exercising a KeyStore key — it cannot
  distinguish hardware signing from fallback signing. (The migration-chain
  tests — `RoutinesMigrationChainTest.kt:285`, `ShortsSniperTruthTest.kt:167`,
  `SliceEMigrationChainTest.kt:213` — only carry the column in schema SQL; they
  do not sign or verify.)

Why decision-dependent: the fallback exists for JVM tests + keystore-less
devices; removing it, logging downgrades, or marking fallback signatures is
the design call under #21. Not touched (the finding-5 fix mirrors the fallback
only in the one keystore-less case documented there — see Finding 5).

## Finding 3 [HIGH] Signature covers only 5 fields — DECISION-DEPENDENT-#21 (left unchecked)

The payload (`CryptoIntegrityManager.kt:60` pre-fix) is exactly:
`startWallTimeMillis | startElapsedRealtimeMillis | bootCount | targetDurationSeconds | isStopwatchMode`
— 5 signed fields. `FocusSessionEntity` (`MonolithModels.kt:268-371`) carries
42 fields. Full census of the **36 unsigned fields** (line refs to
MonolithModels.kt):

| Field | Line | Trusted by |
|---|---|---|
| id | 269 | row identity (PK; listed for completeness) |
| intention | 270 | display |
| profileName | 271 | display |
| elapsedSeconds | 273 | **verify consumer** — reboot branch books it (Repository :754) |
| accumulatedBeforeRebootSeconds | 277 | **verify consumer** — both branches add/trust it (Repository :750, :754) |
| lastTickElapsedRealtimeMillis | 278 | **verify consumer** — reboot detection (Repository :743-744) |
| totalStudyBlocks | 281 | block progression (Repository :2043) |
| currentBlockIndex | 282 | block progression |
| isInSafeBreak | 283 | break state machine |
| safeBreakSecondsRemaining | 284 | break countdown |
| breakTakenSeconds | 294 | span/receipt accounting |
| penaltySecondsAdded | 295 | **penalty gate** — `updateSessionProgress` applies the 900 s tamper penalty only when this is 0 (Repository :1947): setting it non-zero in the DB suppresses the penalty entirely |
| isActive | 296 | which row is "the" active session |
| completedSuccessfully | 297 | outcome/receipt |
| phantomReachesBlocked | 298 | receipt counters |
| tamperDetected | 299 | **the tamper flag itself is unsigned** — an editor can clear the record of tampering |
| forgedArtifactId | 300 | completion artifact |
| completedTasksSummary | 301 | receipt |
| tasksCompletedCount | 302 | receipt |
| routineId | 309 | routine fence (Repository :2050) |
| windowStartMillis | 310 | routine judgement yardstick |
| windowEndMillis | 311 | routine judgement yardstick |
| routineOutcome | 314 | routine judgement record |
| evidenceMask | 317 | attendance evidence E1–E4 |
| firstEvidenceMillis | 318 | attendance evidence |
| lastEvidenceMillis | 319 | attendance evidence |
| taskId | 323 | Arm Ticket bond |
| heldAppsAtSeal | 332 | seal record (receipt omits -1) |
| heldSitesAtSeal | 333 | seal record |
| releaseFaultShield | 341 | release-fault record |
| releaseRetriesDone | 342 | release-fault record |
| releaseFaultedAtWallMillis | 343 | release-fault record |
| outcome | 346 | session ending classification |
| shieldLastBeatWallMillis | 351 | shield-gap classification (Repository :2067) |
| shieldGapSeen | 355 | shield-gap latch |
| endWallMillis | 364 | receipt's witnessed end |
| receiptNumber | 371 | receipt № claim latch |

The sharpest rows are the verify consumers: `computeVerifiedSessionElapsed`'s
reboot branch (Repository :754-756) returns
`max(session.accumulatedBeforeRebootSeconds, session.elapsedSeconds) + (nowMono/1000)`
— both inputs unsigned, so inflating either inflates booked elapsed time with
the HMAC check passing untouched, exactly as the audit charged.

Why decision-dependent: extending the payload invalidates every live
signature on upgrade — what the signature covers is the design call under
#21. Census delivered as evidence; payload NOT changed.

## Finding 4 [HIGH] 'Hardware/TEE' claim never verified — DECISION-DEPENDENT-#21 in substance; doc claim PARTIALLY cured (left unchecked)

Evidence, re-verified:
- Key creation (`CryptoIntegrityManager.kt:33-50` pre-fix) builds
  `KeyGenParameterSpec.Builder(alias, PURPOSE_SIGN or PURPOSE_VERIFY).build()`
  and nothing else: no `setIsStrongBoxBacked(true)`, no user-auth or
  unlocked-device requirement. Grep over `app/src/main/java` finds zero uses
  of `KeyInfo` / `securityLevel` / attestation. Every exception in key lookup
  is swallowed to null → fallback (see Finding 2). The repository doc at
  `MonolithRepository.kt:726` repeats (and :733) the "Hardware Keystore HMAC" claim.
- **Cure applied (commit `3dc2902`, words only, zero behaviour change):** the
  class header no longer claims hardware backing. It now states keys are
  REQUESTED from the AndroidKeyStore, TEE backing is unverified (no StrongBox
  request, no KeyInfo security-level check), a software fallback exists, and a
  valid signature proves consistency with whatever key signed — not hardware
  custody. This un-claims a falsehood without prejudging the ruling: whichever
  way #21 lands (require TEE, or accept software keys), the doc stays true.
- NOT cured: requiring StrongBox/TEE or a security-level check changes device
  support — that is the #21 substance and stays untouched. (The same
  "Hardware" wording in `MonolithRepository.kt:726` was left alone to keep
  this cure scoped to the class the finding names; flagging it here for the
  ruling pass.)

## Finding 5 [MEDIUM] Non-constant-time verify + key-generation side effect — FIXED-IN-CODE (commit `ccde4f1`)

Pre-fix evidence: `verifySessionState` delegated to `signSessionState`
(:85-92), whose key lookup GENERATED and persisted a KeyStore key when absent
(:39-46), and compared with short-circuiting String equality
`expected == session.hmacSignature` (:92).

The fix, in `CryptoIntegrityManager.kt`:
- **(a) Constant-time comparison:** verify decodes the stored Base64 signature
  (`Base64.decode(..., NO_WRAP)`, malformed input caught → `false`, empty →
  `false`) and compares raw MAC bytes with
  `java.security.MessageDigest.isEqual`. No String equality anywhere in verify.
- **(b) No key creation on the verify path:** the old single lookup is split —
  `getOrCreateHardwareHmacKeyOrNull()` (the original body, sign path ONLY) and
  `getExistingHardwareHmacKeyOrNull()` (verify, lookup-only, never generates).
  Verify no longer calls `signSessionState` at all; it computes the expected
  MAC directly over the identical payload (shared `payloadBytesFor`, same
  template string). Where a keystore exists but holds no key
  (`canHoldHardwareKey()` probe: KeyStore loads + HMAC KeyGenerator exists),
  a non-blank signature returns `false` — key deletion/loss now surfaces as
  tamper instead of being silently healed by minting a fresh key.
- **One deliberate mirror, flagged for HERZ:** where NO keystore exists at all
  (plain JVM / keystore-less runtime — `canHoldHardwareKey()` false), verify
  computes the expected MAC with the same fallback key the sign path itself
  would have used there. Rationale: there is no key storage in such a runtime
  to mutate or whose deletion could be masked, and refusing the fallback there
  would make every fallback-signed row unverifiable (including the repo's own
  Robolectric HMAC test). This does NOT extend the fallback anywhere it did not
  already reach via sign, and the fallback's existence stays Finding 2 /
  decision #21, untouched.
- **Unchanged, bit-for-bit on the happy path:** payload string and field set,
  key alias, fallback key value, sign-path downgrade behaviour, and the
  blank-signature rule (line-84 semantics preserved verbatim, per the brief).

Tests — `app/src/test/java/com/example/CryptoIntegrityManagerTest.kt` (new,
Robolectric, sdk 36), 8 tests:
1. valid signature verifies true;
2. tampered signed field (targetDurationSeconds) fails;
3. malformed Base64 (`!!!not-base64!!!`, `====`) fails without throwing;
4. well-formed Base64 of wrong MAC bytes fails;
5. truncated MAC (16 of 32 bytes) fails;
6. no existing key: verify returns false AND the keystore still holds no key
   afterwards (no creation);
7. key deleted after signing: the once-valid signature stops verifying AND
   verify does not resurrect the key (skips via `assumeTrue` only in a
   keystore-less runtime, where the case cannot arise);
8. blank signature keeps current accepted semantics (pins Finding 1 as
   unchanged pending #21 — documentation, not endorsement).

---

## Seat lines — the 20 named types (internal lens passes)

- **Crazy-ideas:** Sign the verdict, not the session — a countersigned "tamper-so-far" digest would make findings 1+3 moot; priced for #21, not built. — *Crazy-ideas*
- **Maximum-implementation-potential:** The lookup/create split is the max cure available without touching #21 territory; anything further spends Sarthak's ruling, not code. — *Maximum-implementation-potential*
- **Brutal critic:** Finding 5's fix is real, but let's not dress it up: the check still waves blank rows through and trusts unsigned elapsed fields — the theatre critique stands until #21 is ruled. Classifications reviewed: findings 1–4 are correctly decision-dependent; none was misfiled to dodge work. — *Brutal critic*
- **Improver:** `payloadBytesFor` shared by sign+verify kills a whole class of future payload drift between the two paths. — *Improver*
- **Designer:** No surface touched; the only user-visible artefact is a doc comment that finally tells the truth. — *Designer*
- **Consumer:** As the phone's owner: if my keystore key dies, sessions now flag tamper instead of quietly healing — that's the behaviour I'd want, and it's the one the brief ordered. — *Consumer*
- **Student:** The census table is the teachable artefact: 36 unsigned fields, three of which the verifier itself eats. — *Student*
- **User:** Nothing about my timer changes today; the fix is invisible unless someone tampers — correct shape for a security fix. — *User*
- **User-experience:** Zero UX delta by design; a false "tamper" on keystore-less runtimes would have been the UX bug, and the mirror avoids it. — *User-experience*
- **Purpose-questioning:** Reviewed the classifications as ordered: is finding 5 truly decision-independent? Yes — constant-time compare and no-mutation-on-read are hygiene under ANY design #21 could pick, including abolishing HMAC entirely. Is the doc cure prejudging? No — it removes a claim; every ruling stays open. No conviction. — *Purpose-questioning*
- **Thoughtful:** The `canHoldHardwareKey` probe is the one judgment call in the fix; it is named, reasoned, and reversible in one line if HERZ disagrees. — *Thoughtful*
- **Deep-thinker:** Verify-mints-key was worse than a side effect: it converted evidence of key loss into a fresh key and a clean verdict — destroying the signal. The fix restores the signal. — *Deep-thinker*
- **Enthusiastic designer:** The report's census table should ride along into the #21 ruling pack verbatim — it IS the decision surface. — *Enthusiastic designer*
- **Wow-level designer:** Nothing wow shipped, correctly: wow here would have been redesigning the scheme, which is exactly what was forbidden. — *Wow-level designer*
- **Designer-29-years:** Twenty-nine years says: never let a read path write. This fix is that rule, enforced. — *Designer-29-years*
- **Intentionality designer:** Every changed line traces to finding 5 or the permitted doc cure; zero scope creep into coverage, provenance, or blank semantics. — *Intentionality designer*
- **Synchronous collaboration:** Evidence slices, census, and downgrade paths are written so the #21 ruling session needs no re-verification pass. — *Synchronous collaboration*
- **Implementation-quality:** Sign path is byte-identical in behaviour (same lookup body, renamed); verify's happy path returns the same verdicts for every honestly-signed row. — *Implementation-quality*
- **Technical execution:** `MessageDigest.isEqual` on decoded bytes; malformed/empty/truncated inputs all funnel to `false` before any key work. — *Technical execution*
- **Interaction-detail:** The deleted-key test asserts the post-condition (alias still absent), not just the verdict — the side effect was the bug, so the test hunts the side effect. — *Interaction-detail*

(Thin lenses above are LEAD-COVERED by the bundle lead where a seat's pass
was a confirmation rather than new evidence; no seat dropped.)

## Disposition summary

| # | Severity | Disposition |
|---|---|---|
| 1 | CRITICAL | DECISION-DEPENDENT-#21 — evidence above, unchecked |
| 2 | CRITICAL | DECISION-DEPENDENT-#21 — evidence above, unchecked |
| 3 | HIGH | DECISION-DEPENDENT-#21 — census above, unchecked |
| 4 | HIGH | DECISION-DEPENDENT-#21 in substance; doc claim PARTIAL cure (`3dc2902`), unchecked |
| 5 | MEDIUM | FIXED-IN-CODE (`ccde4f1`) + 8 unit tests |

Issue stays OPEN for the #21 ruling. CI verdict for this branch is recorded
in the bundle's delivery note to HERZ (run list pulled after push).
