# FASSUNG — TECHNICAL EXECUTION — Issue #4 (THE BOLT, Deck top strip)

**Role:** technical-execution specialist, Deck + Shield surfaces. I own whether
and how each design can be built; the feel seats own how it feels.
**Status:** DECLARED — spec only, no `app/src/` edits, nothing compiled.
**Read before writing:** issue #4 + all 5 comments; `DESIGN-issue-4-the-bolt.md`
in full; `SealBar.kt` (548 lines, this branch); the host derivations in
`TactileDeckScreen.kt` (~340–560); `WatchStore.kt` (warrant derivation);
`MonolithViewModel.kt` (warrant + shield-status plumbing, issue-7 branch —
same trunk machinery).

Prices: **S** = a sitting's edit · **M** = a lane (~½–1 builder-day) ·
**L** = a restructure. Every price names its risk.

---

## 1 · The headline charge, confirmed and priced — the fold does not exist yet

KRALL's opening charge is correct, and I confirm it from the code, not the
design doc:

- `DESIGN-issue-4-the-bolt.md` §2 promises a pure `boltStateFor` folding
  **exactly one warrant read** (precedence: live-OFF → NO_READ → STALE_LIVE →
  UNPROVEN → FROZEN_UNPROVEN → LIVE).
- The shipped `SealBar.kt` header (lines 48–56) says the opposite, honestly:
  the bar "reads the SAME derivations the strip printed" and `sealBarStateFor`
  is "a pure re-encoding of the strip's word". It takes `blockWord: String` —
  the output of `blockCellFor`, which the host builds by **mixing tenses**:
  idle → the freshness-checked `warrantPrintState`; running → the **frozen**
  seal verdict, with the live wire consulted only for the OFF override
  (`TactileDeckScreen.kt:508–512`). The time numerals come from a second
  derivation (`timeCell`), the imprint from a third. Three derivations of one
  wire still reach this face; the Bolt re-encodes the first and inherits the
  disagreement the design was ordered to kill.

**The cure is buildable today, with zero new plumbing.** Every input the §2
fold needs already exists host-side in the same composition:

| Fold input | Host source (exists now) |
|---|---|
| `liveRaw` | `liveWarrantState` (`deckWarrant?.state`) |
| `read == null` | `deckWarrant == null` |
| stale ∧ raw LIVE | `WatchStore.warrantStaleForPrint(deckWarrant, nowMono)` (45 s law) |
| print state | `warrantPrintState` (`WatchStore.printState`) |
| frozen verdict | `frozenState` (sealFreeze / fbFrozen fallback) |
| running? | `isSessionRunning` + `activeSession` |

**Build:** one `BoltInputs` value class + one pure `boltStateFor(inputs)`
returning a `BoltFace` (pose, lamp, window) in `SealBar.kt`; the host computes
it once per frame and passes the face — never a `String` word. `sealBarStateFor`
and `blockCellFor`'s strip vocabulary die on the Deck face (the imprint keeps
its own ruled derivation; it is a different surface with its own tense law —
do not "unify" them by accident).
**Price: M** (≈½ builder-day incl. replacing `SealBarStateTest`'s string-word
tests with the §2 truth table, one test per precedence row, in precedence
order). **Risk:** low — pure function, host call-site ~20 lines; the only
behaviour change is the intended one (mid-session stale/unproven now drops
the lamp the same frame instead of riding the frozen verdict).

**Merge warning for Ren:** the issue-6 branch carries its own running-print
degrade (C5(b): running `blockCell` degrades to UNKNOWN on a live-UNPROVEN /
stale wire). Both branches edited the same host derivation block. The fold
**replaces both** — batch-10 must not try to merge the two textually; land
the fold and strike whichever of the two older forms arrives with it.

## 2 · State coverage — design table vs shipped enum

Shipped `SealBarState` has 6 states. The design table has ~10 rows. The gaps,
each with its mechanism and price:

| Design row | Shipped? | Mechanism to close | Price |
|---|---|---|---|
| OFF / UNPROVEN(caught) / ARMED(brass) / RUNNING(magenta) / RUNNING_UNPROVEN(dark) / ENDING_SOON | Yes | — | — |
| NO_READ | By accident | `deckWarrant == null` → frozen null → `UNKNOWN` → CAUGHT pose. Correct pose, wrong reason; the fold makes it a named row. | in §1 |
| STALE_LIVE (thrown, dark lamp, **window shuttered**) | **No** | Fold row from `warrantStaleForPrint` (idle) — and for *running*, the shield-beat input in §3 below. Window shutter = one extra `Box` overlay branch in the window (diagonal hatch via `drawBehind`, no asset). | S after §1/§3 |
| JAMMED (revoked mid-session, refusal sentence below) | **No** | The cause already exists host-side: `refusalSentenceFor(deckWarrant, nowMono)` computes it, but returns text+action, not a value. Refactor it to return a `RefusalCause` enum first; the sentence renders the enum, the fold consumes it. | M (the refactor touches the sentence slot + its Slice-D tests) |
| DETENTION FAULT (denied release retry; thrown, magenta) | **No — but the input is already at the host** | `finishState: FinishState?` is **already a `TactileDeckScreen` parameter** (line 181) and `heldFinish` is already derived (line 275). KANTE's B-C2 feared a missing input; at the host it exists — it was simply never passed to the bar. Add `finishState != null && !issued` to `BoltInputs`. | S |
| FLOW (stopwatch) | Partially | `isStopwatchMode` is host-known; today FLOW rides RUNNING/RUNNING_UNPROVEN. It needs a distinct row only for the spoken sentence (LETTER's set has FLOW + FLOW-UNPROVEN). Pose/lamp identical by design. | S (one fold parameter) |
| IDLE-AUDIT | Yes in effect | = UNPROVEN pose/lamp; no separate render. | — |

## 3 · B-C1 — the shield input, and the two stale laws that must not merge

The Deck warrant **deliberately excludes** `shieldProtecting`
(`WatchStore.deriveWarrant` KDoc: the shield starts with the session, so
requiring it first would be circular — for the *arm* verdict that reasoning
holds). But the Bolt's **running lamp** is a post-seal claim, and post-seal
the shield witness exists and is already gathered — by the ViewModel, for
Shield: `MonolithShieldService.protecting / protectingSessionId /
protectingMono` (companion vars, flipped at the first full 2 s loop
iteration), the row's `shieldLastBeatWallMillis`, and the repository's
**10 s** gap law (`SHIELD_GAP_THRESHOLD_MILLIS`).

**Build (one machinery fix discharges #4 B-C1 and #6 C5 together):** the VM's
existing per-second loop already calls `refreshShieldBlockingStatus()` with
exactly these inputs. Add one `StateFlow<Boolean>` beside it —
`deckShieldStale` = session active ∧ both shield witnesses attest this
session ∧ `now − max(shieldLastBeat, startWall) > 10_000` — or, cleaner,
expose the already-computed `shieldBlockingStatus` to the Deck host and let
the fold read `proof == STALE` from it. I recommend the second: one
derivation of shield truth for both faces, no third copy of the beat law.
Host gains one parameter; fold gains one input.
**Price: S–M. Risk — the one that matters:** there are **two** stale laws in
this app and they are not interchangeable: warrant print staleness is
**45 s** (monotonic read age, `WatchStore.STALE_AFTER_MS`); shield beat
staleness is **10 s** (wall-clock heartbeat gap). The fold must use 45 s for
the idle print rows and the 10 s beat (via the Shield derivation) for the
running lamp — a builder who "simplifies" to one threshold has changed the
truth, not the code. The truth-table tests must pin both.

## 4 · Motion — what `animateFloatAsState` actually buys, and the keyframe debt

- **First composition:** acquitted. `animateFloatAsState` seeds its
  Animatable at the first target value, so a cold open onto a running
  session composes already THROWN — KANTE B-C5 holds in the shipped code.
- **Travel:** shipped is `tween(50 ms, EasingA)` — a smooth glide, **not**
  the specced keyframed throw (0 / 24 / 62 / 94 dp of travel at 0 / ~17 /
  ~33 / 50 ms). Build: `keyframes { durationMillis = 50; 0f at 0;
  (24f/94f) at 17; (62f/94f) at 33; 1f at 50 }` — fractions of travel, since
  code travel = `maxWidth − plateWidth` varies with width while the design's
  94 dp is the reference-width value. **Price: S.**
- **Pin:** shipped derives `pinDropped = travelFraction >= 0.99f` from the
  animated value — correct instinct (never the target). Under the tween it
  lands in the final frame; the design wants it **1 frame after** seat
  (+62 ms seat inside the Throw buffer). With keyframes, add a second
  `animateFloatAsState` for pin drop keyed on the seated boolean with a
  12 ms delay, or accept final-frame. **Price: S; risk:** at 120 Hz the
  50 ms throw is 6 frames, not 3 — the choreography is ms-true, frame-count
  varies by panel; nobody should test frame counts, test arrival times.
- **Reduced motion:** at `ANIMATOR_DURATION_SCALE == 0f` the tween still
  runs its 50 ms (duration scale scales it to 0 in practice — Animator
  duration scale does apply to Compose tweens via the Android animator
  bridge only for value animators; Compose `tween` is clock-driven, **not**
  scale-driven). Honest build: read the scale (house precedent: the Strike's
  test in `MonolithCustomComponents.kt`) and snap (`tween(0)`) when 0f.
  **Price: S.** Flagged because the design asserts the collapse; the code
  does not yet do it.

## 5 · Rendering technique — acquittals and one honest limit

- The channel is `BoxWithConstraints(Modifier.weight(1f))`; travel =
  `maxWidth − plateWidth`. That **is** FEILE's "all width growth goes to the
  channel" law, achieved by layout instead of the doc's arithmetic
  (housingW − 156). Acquitted — equivalent, and it cannot drift from the
  actual jewel/window widths the way a hard-coded 156 could.
- Render order inside the channel Box (composition order = z-order):
  track → warning bed → collar → socket → pin → plate. Matches DUNKEL's
  bed → collar → socket → plate → rim order; the plate renders last and
  covers the bed exactly at THROWN. The bed is `plateWidth × plateHeight`
  at `CenterEnd` — OFF exposes it fully, CAUGHT (⅓) exposes its shut-end
  portion. Acquitted.
- **The glance gate is only half-executable today.** `SealBarStateTest`
  asserts the enforceable proxies: jewel ≤ 9 dp, budget constant == 41.9,
  orange == `#D63E06`. But `jewelLuminousBudget` is a **named constant
  nothing computes from** — no code sums bed area × luminance vs jewel
  area × luminance. Making BLICK's ≥3× gate truly executable needs a
  render-capture pixel sum (the repo's `ScreenshotHarnessTest` /
  Robolectric graphics mode can host it: render OFF + RUNNING, sum the
  orange-region and lamp-region luminance in the strip's bounds, assert
  ratio). **Price: M. Until it lands, the gate is design arithmetic +
  proxy constants — say so at the merge, don't call it a code gate.**

## 6 · Semantics — 6 sentences shipped, 11 specced, and why the fold pays for the rest

Shipped: one merged node (`semantics(mergeDescendants = true)`), 6
`contentDescription` sentences; numerals + END `clearAndSetSemantics {}`
(correctly out of the tree). LETTER's closed set is 11 — the missing five
(OFF-toted, OFF-while-running, FLOW, FLOW-UNPROVEN, REVOKED-idle/running)
are exactly the fold rows §2 adds. **Sequence law: fold first, sentences
second** — each spoken sentence then maps 1:1 to a fold output and cannot
drift from the pose. **Price: S after §1–§2.** No live region (correct —
the bar is a fact, not an announcement; matches the Shield status ruling).

## 7 · The housing-as-door amendment (§7.3) — technique, if Ren rules it a door

Fully buildable with house patterns: `Modifier.pointerInput` +
`detectTapGestures(onPress)` on the Row; the 1 dp dip via
`graphicsLayer { translationY = … }` animated 90 ms in / ≤150 ms out
(EasingA); the gate re-read **synchronously at DOWN and UP through a
provider lambda** — the host already passes exactly this pattern to the
slide (`warrantProvider = viewModel::armWarrantNow`), so pass
`navigationLockedProvider: () -> Boolean` the same way; a captured Boolean
parameter would repeat the HB-4 class of bug (composition-captured gate
read at tap time). KLANG's one knock call goes in the host handler, never
the composable. **Price: S–M. Blocked on Ren's ruling, not on technique.**

## 8 · Data truth table — every Bolt fact, its source, its tense

| Fact on the face | Source | Tense / law |
|---|---|---|
| Pose (open/caught/thrown) | session existence + seal act (`isSessionRunning`, frozen verdict) | the act is a fact; pose never degrades mid-session |
| Lamp (brass/magenta/dark) | live fold: print state (45 s) at idle; shield beat (10 s, via Shield derivation) while running | proof is *now* or it is dark |
| End numerals | `timeCell` → `endsAtNumeralsFor`: `nowFrame + spanRemainingSeconds` (hand) — **derived, not stored** (see #6 C4: it slides if the ticker stalls; the stored-end stamp is the cure, priced there) | absolute clock end only; blank otherwise; placeholders banned — shipped code honours this (`showNumerals` gate) |
| Count | **not on this face** (Shield header + imprint) | — |

## 9 · What I want next (unassigned capacity)

- The §5 pixel-sum glance test, so BLICK's battery becomes a CI gate
  instead of a render-day ritual.
- A fold-conformance grep gate in CI: `SealBar.kt` must not import
  `WatchStore` / `seamMarksFor` (KANTE §7.3) — five lines of CI script.

— Fassung, technical execution
