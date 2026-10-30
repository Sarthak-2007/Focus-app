# LIE-HUNT — Maas, Critics seat 22 (measure)

Ground: merged trunk ONLY — `Focus-app/app/src/main/java/com/example/`.
Files read in full or at the cited lines: `data/MonolithModels.kt`,
`data/MonolithRepository.kt`, `ui/components/AirlockAndDisarmOverlays.kt`,
plus the two `updateSessionProgress` callers for tick rates.
Lane reports were not used as evidence. No code changed.

## The derivations under test (quoted)

`MonolithModels.kt:281-285`:

```kotlin
val effectiveTargetSeconds: Int
    get() = targetDurationSeconds + penaltySecondsAdded

val remainingSeconds: Int
    get() = (effectiveTargetSeconds - elapsedSeconds).coerceAtLeast(0)
```

`MonolithModels.kt:296-312`:

```kotlin
val spanTargetSeconds: Int
    get() = totalStudyBlocks * targetDurationSeconds + penaltySecondsAdded +
        (totalStudyBlocks - 1).coerceAtLeast(0) * 600

val spanElapsedSeconds: Int
    get() {
        val cycle = targetDurationSeconds + 600
        return if (isInSafeBreak) {
            (currentBlockIndex - 1) * cycle + effectiveTargetSeconds +
                (600 - safeBreakSecondsRemaining)
        } else {
            (currentBlockIndex - 1) * cycle + elapsedSeconds
        }
    }

val spanRemainingSeconds: Int
    get() = (spanTargetSeconds - spanElapsedSeconds).coerceAtLeast(0)
```

Notation for all worked cases: `T` = per-block `targetDurationSeconds`,
`n` = `totalStudyBlocks`, `k` = `currentBlockIndex`, `P` = stored
`penaltySecondsAdded`. The only penalty this code can produce is the
tamper penalty, `MonolithRepository.kt:95-96`:

```kotlin
const val SAFE_BREAK_DURATION_SECONDS = 10 * 60 // 10-Minute Safe Break
const val HMAC_TAMPER_PENALTY_SECONDS = 15 * 60 // 15-Minute Penalty Lock
```

so the concrete penalty case below uses **P = 900**, with
**n = 3, T = 1500** (three 25-minute blocks). `cycle = T + 600 = 2100`.
`spanTargetSeconds = 3·1500 + 900 + 2·600 = 6600`.

Repository transitions under test, `MonolithRepository.kt`:

- penalty resolution, L1011: `penaltyAdd = if (tamper && session.penaltySecondsAdded == 0) HMAC_TAMPER_PENALTY_SECONDS else session.penaltySecondsAdded` — set once, never accumulated, never cleared.
- block finish → break, L1083-1094: stores `elapsedSeconds = effectiveTarget`, `isInSafeBreak = true`, `safeBreakSecondsRemaining = SAFE_BREAK_DURATION_SECONDS`, `penaltySecondsAdded = penaltyAdd`; `currentBlockIndex` unchanged (still k).
- break tick / next block, L1052-1081: see M-3 / M-2.
- ordinary tick, L1102-1103: stores `elapsedSeconds = verifiedElapsed.coerceAtMost(effectiveTarget)`, `penaltySecondsAdded = penaltyAdd`.

---

## CHARGES

### M-1 — spanTarget counts the penalty ONCE; the repository enforces it PER BLOCK. spanRemaining understates true remaining time by (n−k)·P throughout block/break k. [Q1, Q2]

`spanTargetSeconds` (Models L297) adds `penaltySecondsAdded` exactly
once for the whole session. But the block-finish test runs per block
against the per-block target (Repository L1012, L1083):

```kotlin
val effectiveTarget = session.targetDurationSeconds + penaltyAdd
...
val blockFinished = verifiedElapsed >= effectiveTarget
```

and the penalty **carries into every later block** (see M-2's quoted
next-block copy: it never writes `penaltySecondsAdded`, so the stored
P survives). True session length is therefore
`n·(T+P) + (n−1)·600 = 3·2400 + 1200 = 8400`, while the span claims
`spanTarget = 6600` — short by `(n−1)·P = 1800` from the first tick.

Worked, P = 900 present from block 1:

| Moment | spanElapsed | spanRemaining (span) | True remaining | Error |
|---|---|---|---|---|
| Break 1 starts (k=1, rem=600) | 0·2100 + 2400 + 0 = **2400** | 6600−2400 = **4200** | 600 + 2400 + 600 + 2400 = **6000** | **−1800 = (n−k)·P** |
| Block 2 finishes (k=2, e=2400) | 2100 + 2400 = **4500** | **2100** | 600 + 2400 = **3000** | **−900** |
| Block 3 finishes (k=3, e=2400) | 4200 + 2400 = **6600** | **0** | **0** | 0 |

The error is `(n−k)·P` in block/break k and only reaches zero in the
final block — i.e. the desk clock under-reports the time left for the
entire session except its last block, whenever a penalty is in force.
Q2's identity `spanElapsed + spanRemaining = spanTarget` holds, but
only definitionally: `spanRemaining` is *defined* as the difference
(Models L311-312). Both sides of the identity are wrong against the
enforced session by the same `(n−k)·P`; the identity acquits the
arithmetic and convicts the target.

### M-2 — spanElapsed is DISCONTINUOUS at the break → next-block transition: Δ = 1 − P seconds. With P = 900 it jumps BACKWARD 899 s. [Q1(d)]

Last observable break row (`safeBreakSecondsRemaining = 1`, block k
just finished, P in force):

```
spanElapsed = (k−1)·2100 + (1500+900) + (600−1) = (k−1)·2100 + 2999
```

The next call takes Repository L1053-1071 (quoted in M-3): remaining
hits 0 and the stored row becomes block k+1, `elapsedSeconds = 0`,
not in break:

```
spanElapsed = k·2100 + 0
Δ = k·2100 − [(k−1)·2100 + 2400 + 599] = 2100 − 2999 = −899 s
```

In general Δ = `(T+600) − (T+P) − 599 = 1 − P`:

- **P = 0:** Δ = **+1 s** — spanElapsed leaps one second forward; the
  break's final second is never displayed (stored break rows run
  rem = 600…1; the rem = 0 row is never persisted, it is replaced by
  the next-block row in the same call).
- **P = 900:** Δ = **−899 s** — spanElapsed falls backward by almost
  15 minutes the instant block k+1 starts, spanRemaining jumps UP by
  899, and the AOD progress bar (Airlock L123-124,
  `spanElapsed/spanTarget`) regresses by 899/6600 ≈ **13.6 points** at
  the very moment a new block begins.

Root cause, in the formulas: the break branch of `spanElapsedSeconds`
(Models L304-305) credits the finished block at `effectiveTargetSeconds`
(T+P), while the non-break branch (L307) builds every prior block's
base from `cycle = T + 600` — penalty-free. The penalty is counted in
the finished block exactly while its break runs, then silently dropped
from the elapsed base forever after (it lives on only inside the
*current* block's `elapsedSeconds` growth toward T+P).

**Does `penaltySecondsAdded` carry into the next block's stored row?
YES.** Repository L1059-1071, the next-block write:

```kotlin
val nextBlockSession = session.copy(
    currentBlockIndex = (session.currentBlockIndex + 1).coerceAtMost(session.totalStudyBlocks),
    isInSafeBreak = false,
    safeBreakSecondsRemaining = 0,
    elapsedSeconds = 0,
    startWallTimeMillis = wallNow,
    startElapsedRealtimeMillis = nowMono,
    bootCount = currentBoot,
    accumulatedBeforeRebootSeconds = 0,
    lastTickElapsedRealtimeMillis = nowMono,
    hmacSignature = freshHmac
)
```

No `penaltySecondsAdded` argument — `copy()` retains the break row's
stored P. That carry is correct for enforcement (each block really is
T+P long) and is precisely what makes M-1's once-only spanTarget a lie.

### M-3 — In the TRUNK, the Safe Break drains PER CALL, not per wall second — and two live tickers call it. A 600 s break ends in ~400 wall seconds; stalled tickers freeze the break entirely. [Q5]

Repository L1052-1053:

```kotlin
if (session.isInSafeBreak) {
    val nextBreakRemaining = (session.safeBreakSecondsRemaining - 1).coerceAtLeast(0)
```

One decrement per *invocation* of `updateSessionProgress`, regardless
of how much wall/monotonic time passed since the last one. There is
**no `breakRemainingAfter()` anywhere in the trunk** (grep over
`com/example/` returns zero hits) — the wall-derived fix Ruth's lane
claimed exists only in her tree, not in what merged.

Both callers run concurrently against the same session row:

- `MonolithViewModel.kt:342` calls it in a loop with `delay(1000L)` (L364) — 1 call/s.
- `MonolithShieldService.kt:81` calls it in a loop with `delay(2000L)` (L148) — 0.5 calls/s.

Combined drain ≈ **1.5 decrements per wall second** whenever both
loops are live: the nominal 600 s Safe Break is exhausted after
≈ 600 / 1.5 = **400 wall seconds**. Consequences for the span, Q1(c):
mid-break `spanElapsed` advances at call rate, not at time rate — it
runs ~1.5× fast under both tickers, and at 0× under none: if the
process dies or both loops stall, `safeBreakSecondsRemaining` never
moves, while block time (derived from wall/monotonic clocks in
`computeVerifiedSessionElapsed`, Repository L659-681) is immune to
exactly that failure. Break time and block time are kept by two
different clocks in the trunk; only one of them is a clock.

### M-4 — A tamper penalty first detected DURING a break is computed, then thrown away — and the next-block write re-signs the session, laundering the evidence. [Q5, stored fields]

`penaltyAdd` is computed at L1011 for every call. But neither break
write persists it:

- break tick, L1074-1077: `session.copy(safeBreakSecondsRemaining = nextBreakRemaining, lastTickElapsedRealtimeMillis = nowMono)` — nothing else.
- next block, L1059-1071 (quoted in M-2): no `penaltySecondsAdded`, no `tamperDetected`.

Worse, the next-block write re-signs from scratch (L1055-1058):

```kotlin
val freshHmac = CryptoIntegrityManager.signSessionState(
    wallNow, nowMono, currentBoot, session.targetDurationSeconds, false
)
```

So a tamper that occurs while `isInSafeBreak` is true produces
`penaltyAdd = 900` in memory, is discarded by both break branches,
and the fresh HMAC at block start makes the post-break session verify
clean. The 15-minute Penalty Lock has a break-shaped hole in it.

### M-5 — The AOD progress clamp fabricates motion at both ends: 4% before a second has passed, and a permanently full bar for any stopwatch past 20 minutes. [Q6]

AirlockAndDisarmOverlays.kt L123-126:

```kotlin
activeSession != null && activeSession.isActive && !activeSession.isStopwatchMode ->
    (activeSession.spanElapsedSeconds.toFloat() / activeSession.spanTargetSeconds.coerceAtLeast(1)).coerceIn(0.04f, 1f)
activeSession != null && activeSession.isActive ->
    (activeSession.elapsedSeconds.toFloat() / 1200f).coerceIn(0.04f, 1f)
```

- At `spanElapsedSeconds = 0` (the session's first frame) the bar
  renders **0.04 full** — progress that has not happened. Small, but
  it is the same species as the fabricated 0.65 constant the file's
  own comment (L112-116) boasts of killing.
- The stopwatch bar divides by a fixed 1200 s and clamps at 1f:
  from 20:00 onward, an open-ended count-up session — one whose whole
  point is running past the floor — displays a *complete* bar for the
  rest of its life while its own numerals keep counting up. The bar
  and the digits tell different stories on the same screen.
- The clamp also conceals M-2: the raw ratio never *exceeds* 1 in any
  traced stored state, so the 1f cap itself is not masking an
  overflow — but the 0.04f floor masks the −899 s regression's true
  size whenever it would cross below it, and in all cases the rendered
  bar is a clamped rendering, not the derivation.

### M-6 — The break length is hardcoded as literal `600` in the span formulas while the repository owns it as a named constant: two sources for one fact. [Q1/Q5, latent]

Models L298, L302, L305 use the literal `600` three times
(spanTarget's break term, `cycle`, the break-elapsed term).
Repository L95 owns the same fact as
`SAFE_BREAK_DURATION_SECONDS = 10 * 60`, and L1088 writes the break
from the constant. Today both are 600 and every number in this file
holds. The day anyone retunes the constant, the repository will run
one break length and the span will silently keep accounting another —
no compile error, no test in these files couples them. A latent lie,
charged as such, not a live one.

---

## ACQUITTALS

### A-1 — Block → break transition is continuous, and Ren's stored-fields claim is TRUE. [Q1(b), Q5]

Repository L1085-1090 stores, at the finish tick:
`elapsedSeconds = effectiveTarget`, `isInSafeBreak = true`,
`safeBreakSecondsRemaining = 600`, `currentBlockIndex` still k.
Hence per-block `remainingSeconds = effectiveTarget − elapsedSeconds
= 0` exactly, as claimed. And the span meets itself at that instant:
last non-break value `(k−1)·cycle + effectiveTarget` (ordinary ticks
clamp `elapsedSeconds` at `effectiveTarget`, L1103) equals the first
break value `(k−1)·cycle + effectiveTarget + (600−600)`. No jump at
this seam, for any P, provided P does not change on that very tick.

### A-2 — Ren's desk-clock claim holds exactly when P = 0: during a break, spanRemaining = break remainder + all remaining blocks and breaks. [Q5]

n=3, T=1500, P=0: `spanTarget = 5700`. Break 1 at rem=600:
spanElapsed = 1500, spanRemaining = **4200** = 600 (this break) +
1500 + 600 + 1500 ✓. Mid-break, rem=300: spanElapsed = 1800,
spanRemaining = **3900** = 300 + 1500 + 600 + 1500 ✓. The desk clock
(Airlock L105) reads `spanRemainingSeconds` for timed sessions, so it
counts the break down *as session time*, never sits at 00:00 through
a break between blocks, and gives the break no rendering of its own.
The claim fails only through M-1 (P > 0), not in its own terms.

### A-3 — Single-block sessions degenerate exactly. [Q3]

n=1: `spanTarget = T + P + 0·600 = effectiveTargetSeconds`;
`spanElapsed = 0·cycle + elapsedSeconds = elapsedSeconds`;
`spanRemaining ≡ remainingSeconds`. The `(n−1).coerceAtLeast(0)`
term contributes no phantom break, and no break can occur
(Repository L1084 requires `currentBlockIndex < totalStudyBlocks`).
The span *is* the per-block derivation. Clean.

### A-4 — Stopwatch sessions: the span is never read on any AOD branch. [Q4]

Every derivation in `AlwaysOnDeskClockOverlay`:

- display, L99-107: `if (activeSession.isStopwatchMode) activeSession.elapsedSeconds else activeSession.spanRemainingSeconds` — span reached only on the timed arm.
- progress, L117-127: the span arm (L123-124) is gated on `!activeSession.isStopwatchMode`; the stopwatch arm (L125-126) reads only `elapsedSeconds`; the idle arm is `0f`.
- repository side: `updateSessionProgress` returns stopwatch sessions at L1036-1049, *before* the break branch — a stopwatch row can never enter the break state whose span branch exists.

No other span reader exists in the AOD file (grep: L105 and L124 are
the only two span references outside MonolithModels.kt). Acquitted —
with M-5's note that the stopwatch *bar* is dishonest on its own
terms, which is a separate charge, not a span leak.

### A-5 — Bounds: spanRemaining cannot go negative, spanElapsed never exceeds spanTarget in any reachable stored state, and the final block lands exactly on target. [Q2, Q6]

`spanRemainingSeconds` is coerced at Models L312, so no negative
rendering is possible. Tracing every stored state the repository can
write (ordinary tick clamped at L1103; break rows with rem ∈ [1,600]
written only from 600 downward at L1053/L1088; next-block rows with
elapsed = 0): spanElapsed ≤ spanTarget everywhere, with equality
exactly at final-block completion — `(n−1)·cycle + (T+P) =
(n−1)(T+600) + T + P = nT + P + (n−1)·600 = spanTarget` ✓. The
coercion at L312 is therefore a guard that never has to fire, not a
concealment — the dishonesty in this system lives in M-1/M-2's
*middle* of the session, not at its edges.

### A-6 — The mid-break span *formula* is shaped correctly. [Q1(c)]

In break, spanElapsed = finished blocks at full value + `(600 −
safeBreakSecondsRemaining)`: it advances exactly 1 s per second of
break consumed and includes the break inside session time rather
than freezing or skipping it. The formula is acquitted; its *input*
is charged in M-3 — `safeBreakSecondsRemaining` itself does not
measure seconds in the trunk.

---

## Question map

1. (a) mid-block k: `(k−1)(T+600) + e` — correct. (b) break start:
   continuous, = `(k−1)(T+600) + T+P` (A-1). (c) mid-break: formula
   correct, input drains per call (A-6, M-3). (d) block k+1 start:
   **jump of 1 − P** — +1 s if P=0, **−899 s if P=900** (M-2). Penalty
   **does** carry into the next block's stored row (M-2).
2. spanTarget drops `(n−1)·P` against what is enforced (M-1);
   elapsed+remaining=target holds only by definition.
3. Single-block: exact degeneration (A-3).
4. Stopwatch: span never read on any AOD branch (A-4).
5. Ren's claim: stored fields and P=0 arithmetic verified (A-1, A-2);
   the trunk's break is per-call, dual-ticker — Ruth's wall-derived
   fix is **not** in the trunk (M-3); tamper during a break is
   discarded (M-4).
6. No negative spanRemaining and no spanElapsed overflow (A-5); the
   live dishonesties at the bounds are the 0.04 progress floor and
   the saturated stopwatch bar (M-5).

## Worst finding

When the one penalty this code can impose — the 900-second tamper lock — is in force, the desk clock under-reports the time remaining by up to (n−1)·900 seconds for the whole session and its elapsed time jumps backward 899 seconds the instant the block after the penalty begins, so the span lies most precisely in the one situation where the user is already being punished.

— Maas, Critics · seat 22 (measure)
