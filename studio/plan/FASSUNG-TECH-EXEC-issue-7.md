# FASSUNG — TECHNICAL EXECUTION — Issue #7 (Shield audit fixes)

**Role:** technical-execution specialist, Deck + Shield surfaces. (This is a
different seat from my wave-1 finisher spec in `studio/cells/issue-7/
finisher-fassung.md` — that was semantics; this is the mechanism under the
whole Shield pass. Where the two overlap I say so and do not re-sell it.)
**Status:** DECLARED — spec only, no `app/src/` edits, nothing compiled.
**Read before writing:** issue #7 + all 17 comments; `AUDIT-shield.md`;
`ISSUE-7-WAVE2-REPORT.md`; `ShieldBlockingStatus.kt` (192 lines);
`ShieldMatrixScreen.kt` (1,112 lines, wave-2 state); the VM plumbing in
`MonolithViewModel.kt`; both services; `WatchStore.deriveWarrant`.

Prices: **S** = a sitting's edit · **M** = a lane (~½–1 builder-day) ·
**L** = a restructure.

---

## 1 · The headline derivation — audited in code, and it holds

`computeShieldBlockingStatus` is what FORM/FUNKE/SALZ converged on, built
correctly:

- **Pure** — every fact arrives as a parameter; the truth table is
  unit-tested (`ShieldBlockingStatusTest`). No surface invents a variant.
- **Conjuncts:** session row active ∧ shield service running ∧
  `protecting` flipped **for this session id** ∧ accessibility bound ∧
  `armedSessionId == session.id`. Never `isActive` alone. ✓
- **Beat law:** `now − max(shieldLastBeatWallMillis, startWallTimeMillis)
  > SHIELD_GAP_THRESHOLD_MILLIS` (**10 s** wall) → STALE. The start-as-
  reference choice matches `latchShieldGapIfStale`'s own reference. ✓
- **Refresh:** the VM recomputes on WatchStore emissions, on session
  emissions, **and on a per-second ticker loop** (`MonolithViewModel.kt`
  ~:1057, `delay(1000L)`). This matters: STALE is a *crossing*, and
  crossings happen when nothing emits. Because the ticker is VM-side
  (not shield-loop-side), a wedged shield loop still goes STALE within
  ~1 s of the 10 s line — the beat stops advancing (its only writer is
  the loop's `updateSessionProgress(stampShieldBeat = true)`), the
  ticker's `now` keeps advancing. **The derivation cannot be stuck
  printing LIVE by silence. Acquitted, with the mechanism named.**

**What BLOCKING NOW honestly means, stated for the record:** both
services attest this session + the shield's beat is fresh. It does **not**
mean "an intercept has fired" — no witness observes an intercept, and
FUNKE's Mechanism B (proof by last real block) was correctly rejected as
the power source (it starves exactly when the wall works). The sentence
is a witness claim, and it is the strongest one this machinery can make
without the deferred Mechanism C probe (§5).

## 2 · The two residual holes in the proof — priced, with the decision owners named

### 2.1 The usage-access blind door (Salz C-2; wave-2's "R-5 third conjunct")

The shield's foreground door is UsageStats. If usage access is **revoked
mid-session**: the shield loop keeps iterating (its beat keeps stamping —
the beat proves the *loop* lives, not that its *door* sees), `protecting`
stays true, the a11y service still intercepts what it can — and the
sentence prints BLOCKING NOW while one enforcement door is blind. The
warrant knows this fact (`Warrant.usage`); the Shield derivation never
reads it.

**Build:** one more parameter — `usageAllowed: Boolean` — fed by the VM
from the same `standingWarrant()` read it already performs
(`repository.standingWarrant().usage == UsageAccess.ALLOWED`); UNPROVEN
gains the missing-proof word `USAGE`. Truth-table tests gain one column.
**Price: S.** The **strictness call is not mine and not the builder's** —
wave-2 already routed it up (more NOT BLOCKING prints when a permission
lapses). Machinery is ready when Ren/Sarthak rule it; until then the
sentence's claim should be understood as *two-witness*, and nobody
should describe it as three.

### 2.2 The accessibility witness has no liveness beat — named, not cured

`armedSessionId` is stamped by the service's session collector at arm
time. There is no a11y heartbeat: a bound-but-wedged service keeps its
stamp forever, and no age law can be invented for it — at idle (Shield
open, no navigation) a healthy a11y service produces **no events for
long stretches**, so any "armed stamp older than X" rule would print
false NOT BLOCKINGs in exactly the calm case. **Verdict: leave it, and
say what the witness is** — bind + arm, no liveness. The shield beat is
the only liveness clock on this screen, by design. Anyone proposing an
a11y age law owes a false-negative analysis first.

## 3 · The status line's rendering — three wave-2 remainders, mechanised

Wave-2 landed full ink + fixed 40 dp slot + `AnimatedContent` re-strike.
Three specced pieces did not land; here is each one's build:

1. **The solidity glyph is two-valued; the proof is three-valued.**
   Code: `Text(if (proof == LIVE) "●" else "○")` — UNPROVEN and STALE
   wear the same hollow as NO_SESSION, but PANZER/FORM ruled a 55%
   middle (unproven ≠ proven-absent). A text glyph cannot do 55%.
   **Build:** replace the `Text` glyph with a 10 dp `Canvas` (or Box +
   `drawBehind`): solid circle / stroked circle / circle at 55% alpha
   fill — one `when(proof)` inside `drawCircle`. Semantics already carry
   the sentence, so the glyph stays decorative to TalkBack.
   **Price: S.**
2. **Whole line in Anonymous Pro 13 sp — the mono face LUFT charged.**
   FORM/PANZER ruled Atkinson Bold 14 for words, Anonymous Pro only for
   the `ENDS` numerals. A single `Text` cannot split typefaces; the
   headline must stop being one `String` at the render site.
   **Build:** add structured fields to `ShieldBlockingStatus`
   (`label: String`, `endsAtText: String?`, keep `headline` for tests /
   semantics) and render `buildAnnotatedString` with a `SpanStyle`
   (Anonymous Pro Bold) around the numerals segment only. Deriving the
   split by parsing the headline at the composable is forbidden — same
   rule as my wave-1 §1.1 for the spoken string: structure is built in
   the derivation, never recovered by string surgery.
   **Price: S–M** (data-class fields + renderer + truth-table tests keep
   `headline` as the canonical concatenation, so the existing tests
   arbitrate that the split can never disagree with the line).
3. **No anti-flap coalesce.** The tally below coalesces (250 ms,
   `LaunchedEffect(tallyNow) { delay(250); … }`); the status line
   re-strikes on **every** headline change, and at cold start / rebind
   the witnesses settle in bursts — the exact burst SCHWUNG's spec
   governed. **Build:** the tally's own pattern, copied: hold
   `statusShown` state, `LaunchedEffect(statusHeadline) { delay(250);
   statusShown = statusHeadline }`, `AnimatedContent` on `statusShown`.
   One exception carved out, honestly: the **first** frame is a cut
   (initial state = first headline, no delay) — coalesce governs
   *changes*, not arrival. **Price: S.**
4. **Spoken form still owed (from my wave-1 spec §1.1).** Wave-2's report
   says "a semantics description carrying the whole sentence (R-11
   core)" — the code sets `contentDescription = statusHeadline`, the
   raw caps-and-em-dash string. That is the headline *carried*, not the
   spoken form *derived* (sentence case, punctuation TalkBack survives).
   **Build:** a `spoken` field constructed in the same `when` that
   builds `headline` (strings tabled in finisher-fassung §1.1), the Row
   semantics read `spoken`, plus `liveRegion = Polite` — still absent
   from the screen (grep: zero `liveRegion` hits in
   `ShieldMatrixScreen.kt`). Spam-safety is already proven by
   construction: the spoken string changes only when the proof state
   does (the `ENDS` HH:mm is stable per session — §1.1 of the finisher
   spec shows the arithmetic). **Price: S.**

Also noted, not charged: the null-status fallback prints
`ENFORCEMENT NOT PROVEN — NO WITNESS READ YET` — a form that appears in
**no** ruled copy table (FORM's three states, SAUM's seven forms, the
landed four proofs). It is honest, but it is a fifth dialect at the
screen's most important address. Either SAUM tables it or it adopts the
UNPROVEN form with the generic label. **Price: S** (a wording ruling +
one string).

## 4 · Session-night keyed on a row claim (LUFT A-1) — the cheap half and the real half

`ShieldMatrixScreen.kt:143`: `effectiveNight = isDarkTheme ||
isSessionActive`, and `isSessionActive` arrives from `MainActivity` as
`sealed` — a **session-row claim**, the same class of fact §1 forbids the
sentence to use. The sleds' `isNightMode` therefore darkens on a claim.

- **Cheap half (this screen):** the composable already holds
  `blockingStatus`. Sled night should read
  `isDarkTheme || blockingStatus?.isBlocking == true`. One derivation
  line + the sleds' existing `effectiveNight` parameter. **Price: S.**
- **Real half (host):** the *scheme itself* steps at the host —
  `Theme.kt:108` `darkTheme || isFocusSessionActive`, and
  `MainActivity`'s `effectiveNight = darkTheme || sealed` feeds every
  screen. Keying the whole room on proven LIVE crosses MainActivity +
  Theme, i.e. **app-wide**: every screen's night grammar re-verifies,
  and a new failure mode appears — proof flicker at the stale boundary
  would strobe the room. It needs hysteresis: night *enters* on LIVE,
  *exits* only on NO_SESSION or a proven OFF (an UNPROVEN/STALE
  mid-session keeps the room — the wall is doubted, not declared
  fallen). That state machine is a `remember`ed latch in the host keyed
  by session id. **Price: M, Ren's call** — my recommendation is the
  cheap half now, the host half only with the latch designed, never as
  a bare `|| isBlocking` swap at the theme.

## 5 · The priced remainders — mechanisms

| Item | Mechanism (concrete) | Price / risk |
|---|---|---|
| **R-4(a) Ghost Preview** | The blocker is that `applySmartLoadout` *writes* — there is no pure projection to preview from. Refactor first: extract `planLoadout(loadout, apps): Map<pkg, tier>` as a pure function in the repository/AppRules layer; commit becomes `apply(plan)`, preview becomes `count(plan)` computed in the VM as a derived `StateFlow` — **same function, so the preview cannot lie about the commit**. UI: cap selector gains a local `previewIndex` (first tap previews, second tap on the same segment commits via the existing `onApplySmartLoadout`; tap elsewhere reverts). ~120 lines as the audit guessed — **confirmed**, but ~40 of them are the refactor, not the UI. | M · risk: any second copy of the loadout rules = a lying preview; the refactor is the price of honesty. Two-tap vs undo is Sarthak's call (wave-2 routed it) — technique supports either; undo needs a pre-commit tier snapshot (one DAO read + write-back, +S). |
| **R-7 in-place refusal** | Today `ratchetWarningMessage: StateFlow<String?>` is one global string — it cannot name a row. Change to `RatchetRefusal(packageName, message, nonce)` (nonce so a repeat refusal on the same row re-strikes instead of being an equal-state no-op). The sled **row wrapper in Compose** (not the Canvas View) renders it under the touched row: `AnimatedVisibility` 280 A in / 160 Throw out, dwell ruled by R-7, cleared by a VM `delay` job. The top banner stays for non-row refusals (routine toggles). | ~½ builder-day — wave-2's price **confirmed** · risk: LazyColumn item identity — the refusal must key to the package, not the item index, or filtering mid-dwell prints it under the wrong app. |
| **R-8 wheels** | Delegated to the routine-edit lane (#15) — no local price. Integration note only: the editor rows are Compose; the wheel (that lane's custom View) arrives via `AndroidView`, and my wave-1 spec §3.5 already files the semantics it must inherit (one adjustable node per wheel). The ±30 chips' *labels* change, not their behaviour, until then. | — (lane #15) |
| **R-10 S-1** (56 vs 54 dp) | One measure constant inside `AppRowSledToggleView`'s layout maths. | S |
| **R-10 S-3** (mid-drag re-tier) | Guard the View's `update{}`-driven `currentTier` assignment with its existing dragging flag; re-seat applies at drag end. | S |
| **R-10 V-2** (provisional cap) | Cap selector local state vs committed index split — same pattern as Ghost Preview's `previewIndex`; land together if Ghost Preview lands. | S |
| **R-11 remainder** | Sled semantics landed core (wave-2); what remains is cap/chips custom actions — the pattern is finisher-fassung §5–§7, unbuilt parts. | M (with the finish pass) |
| **R-12 finish pass** | Panzer's ~24 h stands as filed; nothing in the mechanism reprices it. | as filed |
| **Reset-note residency** (dies on first edit / 48 h) | Residency is a **persisted instant**, not a VM flag: one `guard_settings`-style key (seeded-at wall millis) + "edited since seed" derived from any routine write's timestamp; the note's visibility is a pure function of those two facts, computed where `showResetNote` is derived. No new table. | S–M · risk: a VM-only flag resurrects the note every process death — that's the bug the residency rule exists to kill. |
| **Turned-Away Ticker** (wave-2 seat proposal) | Technically trivial — `intercept_logs` exists, one DAO query + one line of print. **I price it S and recommend against building it on this screen:** LUFT's ruling (no catch-memory on Shield; "a door does not brag") stands uncountermanded, and a ticker is a *log on a face* — the exact species issue #4 was ordered to kill on the Deck. If Ren wants it, its home is the Drawer/Ledger, not Shield. | S · build only on Ren's countermand |
| **Wall-Test Canary / FUNKE Mechanism C** (challenge–response probe) | There is no honest synthetic foreground event to inject: UsageStats events cannot be fabricated, and an accessibility self-probe measures the probe path, not the intercept path. Any build here risks a *false LIVE* — the worst print this screen can make. **Price: L, risk: unacceptable without a device-proven design. Defer — FUNKE's own deferral stands.** | L · deferred |

## 6 · Data truth table — the sentence's facts

| Fact | Source | Tense / law |
|---|---|---|
| Session active + id + label + end | Room session row (`FocusSessionEntity`), label from its frozen `profileName`, end from frozen `windowEndMillis` (routine) or `now + spanRemainingSeconds` (hand) | row = claim; witnesses below promote it |
| Shield protecting this session | `MonolithShieldService.protecting / protectingSessionId` companion vars, flipped at first full loop iteration | flip instant; liveness via beat |
| Shield liveness | row's `shieldLastBeatWallMillis`, written only by the loop | 10 s wall gap law |
| A11y armed this session | `MonolithAccessibilityService.armedSessionId`, stamped by its session collector | bind + arm; **no liveness beat exists** (§2.2) |
| Usage allowed (not yet a conjunct) | `WatchStore.deriveWarrant` via `repository.standingWarrant()` | §2.1 — S to add, decision routed up |
| RUNNING NOW on a row | the active session's `routineId` (`routineRunningNowSuffix`) — never `isActiveNow` window math | session fact, not schedule arithmetic ✓ landed |

## 7 · What I want next (unassigned capacity)

- A witness-staleness diagram in the repo (which stamps exist, which
  age, which never can) so the next audit doesn't re-derive §2.2 from
  scratch.
- The `headline`/`spoken`/`label` structured-status refactor priced in
  §3.2–§3.4 done as **one** small pass — three S items that share one
  data class and should never land separately.

— Fassung, technical execution
