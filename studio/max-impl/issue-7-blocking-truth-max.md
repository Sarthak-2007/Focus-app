# MAX-IMPL — Issue #7 · The Blocking-Truth System (Shield)

**Author:** MIP-04 (Maximum-Implementation-Potential, issue #7)
**Assignment:** Shield must say whether blocking is live. Maximise it: the app
continuously proves its own blocking pipeline and prints only what it has
proven, when it proved it.
**Status:** Spec only. Nobody compiles. This file prices the fullest
buildable version; §11 slices it so every slice ships honest words and no
slice ever pre-claims proof it does not run.
**Background read:** `studio/plan/AUDIT-shield.md` (headline finding 1, the
Missing Sentence; ruling R-5), the shipped issue-#7 cell
(`ShieldBlockingStatus.kt`, MONTEUR), and the warrant lane
(`WatchStore.kt`, `MonolithAccessibilityService.provePipelineNow()`,
`MonolithRepository.standingWarrant()`), treated throughout as prior art
to exceed, not copy.

---

## 0. The maximised idea, in one paragraph

Today Shield proves a *session exists* with two witnesses; the Deck's
warrant proves *accessibility once read a window during this visit* with
three facts. Neither proves blocking. The maximum buildable system is a
**Blocking Truth Engine (BTE)**: a single pure derivation fed by
**exercised proofs** — for every door the app claims, the app periodically
makes that door *do its real work on a synthetic canary input* and records
that it did, with a monotonic stamp and a measured latency. A door is not
"on" because a setting says so, not "live" because a service object
exists, and not "proven" because it passed once at arm time. It is proven
only while its last successful exercise is younger than its staleness TTL,
and it is revoked *instantly* — not at TTL expiry — the moment any
lifecycle callback, settings observer, or AppOps listener reports the
door closing. The headline `BLOCKING NOW` is printed only when every
door in the session's explicit **claim set** is at its required proof
tier, the shield engine itself has iterated inside the last 4 seconds,
and the youngest required proof is printed next to the verdict as its
age — so the sentence carries its own expiry. Everything else prints
`NOT BLOCKING — <named door, named failure, last-proved time>`. Every
proof, loss, and recovery lands in an append-only proof log, so "was it
blocking at 14:03?" becomes an answerable question and a session that
spent six minutes unguarded says so on its own receipt.

---

## 1. Where the prior art stops (measured, not rhetorical)

### 1.1 The shipped Shield sentence (`ShieldBlockingStatus.kt`, issue-#7 cell)

- Two witnesses only: `MonolithShieldService.protecting` (flipped after
  one full monitor iteration, session-id matched) and
  `MonolithAccessibilityService` bound + `armedSessionId` matched.
- Four proof states: `LIVE / NO_SESSION / UNPROVEN / STALE`. Staleness is
  one clock only: the shield loop's persisted beat vs.
  `SHIELD_GAP_THRESHOLD_MILLIS = 10_000` (five missed 2 s beats).
- **None of the four counted doors is a conjunct.** Usage access, device
  admin, and the notification listener do not appear at all.
  Accessibility appears as *bound + session-observed*, never as
  *exercised*: a bound service whose event pipeline has wedged still
  satisfies it, provided its session collector once armed.
- The witness mono stamps are explicitly *not aged* in that derivation
  ("they are not aged out here; the beat is the staleness clock"). A
  service bound at 09:00 and silent since proves a 15:00 headline if the
  shield beat is fresh.
- Refresh is pull-based (`refreshShieldBlockingStatus()` at screen
  events). Nothing invalidates the printed line between refreshes.

### 1.2 The warrant (`WatchStore` + `provePipelineNow()`, warrant lane)

Stronger store, weaker verdict:

- `WatchStore` keeps nine watch items with mono+wall stamps; the counted
  four are ACCESSIBILITY · USAGE ACCESS · DEVICE ADMIN · NOTIFICATION
  LISTENER. A GRANTED measurement goes `STALE` after `STALE_AFTER_MS =
  45_000`. A grant-check sweep stamps six permission items in parallel;
  `lastProvedWallMillis` advances only on a complete set.
- But `deriveWarrant()` — the only verdict — uses exactly three inputs:
  a11y bound, `stampedThisResidency()` (`lastRootMono >=
  residencyStartMono`), and usage `ALLOWED`. **Device admin and the
  notification listener are counted on the Guard line and gate nothing.**
  Shield-protecting "rides along as a fact" and is explicitly *not* a
  conjunct.
- `provePipelineNow()` acquires `rootInActiveWindow` and recycles it.
  That proves: bound, window-content retrieval enabled, *a* window tree
  exists. It does not prove the tree belonged to another app, that node
  traversal/text reading (what Shorts/Reels and URL detection actually
  do) works, that the decision logic can read its rules, or that an
  intercept would fire. When the user is standing in Monolith, the root
  it reads is Monolith's own.
- Every door's check is an *existence or permission read*: `instance !=
  null`, AppOps mode, `DevicePolicyManager.isAdminActive`,
  `Settings.Secure` string containment. No door is ever exercised.
- Persistence is explicitly NONE ("nothing in this file touches Room");
  the Readiness Log was deferred to a later slice. There is no history,
  therefore no gap accounting and no after-the-fact truth.
- The known cold-start failure — "CAN'T CHECK BLOCKING — TAP TO RETRY"
  on every reopen — is a *manual-retry* design: the residency pull fixed
  the stamp, but recovery still waits for the user to ask.

### 1.3 The gap this spec closes

| Fact | Shield sentence today | Warrant today | BTE (this spec) |
|---|---|---|---|
| Session row active | required | n/a | required, never sufficient |
| Shield loop iterated recently | beat ≤ 10 s (wall) | not a conjunct | iteration counter advanced ≤ 4 s (mono) + beat ≤ 10 s |
| Accessibility exercised | no (bound only) | root read only | canary node readback + decision dry-run (§3.1) |
| Usage access exercised | not checked | AppOps read only | own-package foreground event observed (§3.2) |
| Device admin | not checked | counted, not gating | guard tier, claim-set gating, ceiling named (§3.3) |
| Notification listener exercised | not checked | counted, not gating | self-notification canary observed + cancelled (§3.4) |
| Per-door staleness TTL | none | 45 s (perm prints only) | per door, mono-aged (§5) |
| Instant invalidation on door loss | no | bind hooks stamp store, verdict not re-derived on Shield | yes — every surface re-derives (§5.3) |
| Proof history / gap accounting | no | no (by design) | append-only proof log (§8) |
| Recovery | tap to retry | tap to retry | automatic re-probe with backoff (§7.3) |

---

## 2. First principles — the truth model

### 2.1 Three tiers of proof. Never conflate them.

For every door, BTE distinguishes:

- **T1 · GRANT** — the platform says the permission exists.
  (AppOps mode, `isAdminActive`, secure-settings membership,
  `canRetrieveWindowContent`.) Cheapest, weakest: OEM skins are known
  to report grants for pipelines that deliver nothing.
- **T2 · BIND / LIVENESS** — the service object exists *now* and its
  lifecycle is current. (`instance != null`, connected stamps,
  heartbeat/iteration counters advancing.)
- **T3 · EXERCISE** — the door *did its real work* on a canary input,
  end-to-end, inside a deadline, and BTE observed the output. This is
  the only tier the word PROVEN may describe.

A door's printed word is its highest *currently fresh* tier:
`PROVEN` (T3) · `GUARD SET` (admin's T2 ceiling, §3.3) · `BOUND ONLY`
(T2) · `GRANTED ONLY` (T1) · `MISSING` (T1 affirmatively absent) ·
`UNSET` (usage access never decided — kept distinct from DENIED, as the
repository's three-valued `UsageAccess` already established) ·
`STALE` (was at a tier, aged out) · `SILENT` (T1/T2 present, T3 exercise
produced nothing inside its deadline — the most dangerous state and
therefore a named one, not folded into UNPROVEN) · `ERROR` (exercise
threw; detail code logged).

### 2.2 The claim set — what "blocking" promises, per session

Doors are not interchangeable; they protect different things, and the
maximum honest system says which promise is whose:

- **Intercept doors** (a SHUT app cannot stay open): ACCESSIBILITY,
  USAGE ACCESS, plus DOOR 0, the shield engine itself (§3.0).
- **Integrity door** (Monolith cannot be quietly disarmed/uninstalled
  mid-session): DEVICE ADMIN.
- **Capture door** (a SHUT app's notifications are swallowed into the
  digest, not shown): NOTIFICATION LISTENER.

A session's **claim set** is derived from its own configuration at arm
time and frozen onto the session row: intercept doors always claimed;
capture claimed whenever any ruled app is non-OPEN (the digest promise
is live); integrity claimed unless setup recorded an explicit user
waiver for admin (waiver is a row, with a timestamp — never a silent
default). `BLOCKING NOW` requires *every claimed door at its required
tier* (§2.3). An unclaimed door still prints in the detail sheet
(§6.2) with its real state; it simply does not gate, and the headline
says so once, in the detail sheet: `NOT CLAIMED THIS SESSION — WAIVED
AT SETUP <date>`. No door is ever silently dropped from the claim set
mid-session; losing a claimed door is a verdict change, not a
redefinition.

### 2.3 Required tiers

| Door | Required for `BLOCKING NOW` | Why not less / not more |
|---|---|---|
| 0 · Shield engine | T3-equivalent: iteration advanced ≤ 4 s ago for *this* session id, last iteration outcome = completed foreground query | `protecting` flipping once at start (today) proves an iteration ran once, not that the loop is alive now |
| 1 · Accessibility | T3: canary readback + decision dry-run (§3.1), fresh ≤ 30 s | T2 bound-only is today's lie surface |
| 2 · Usage access | T3: own-package foreground event observed (§3.2), fresh ≤ 90 s | AppOps ALLOWED-but-silent is a real OEM failure mode; the shield's foreground door is blind without delivered events, however the grant reads |
| 3 · Device admin | T2 ceiling: `GUARD SET` (§3.3) | T3 does not exist on stock Android — see the ceiling, §3.3. Requiring an impossible tier would make LIVE unreachable; calling T2 "proven" would be a lie. The grammar keeps them visibly different |
| 4 · Notification listener | T3: canary observed + cancelled (§3.4), fresh ≤ 5 min, *plus* T2 bind fresh at headline time | Canary is intrusive enough to run rarely; between canaries, bind-loss invalidates instantly (§5.3), so the 5 min window is backstopped |

### 2.4 Clocks

- All ageing on `SystemClock.elapsedRealtime()` (monotonic). Wall clock
  is for *display* ("LAST PROVED 14:02:11") and never for a verdict.
- Every proof records `(mono, wall, bootCount)` where bootCount is
  `Settings.Global.BOOT_COUNT`. A proof whose bootCount ≠ current boot
  is void — a reboot kills every stamp, including persisted log-derived
  state. (The log in §8 is history, never a source of live proof —
  the warrant lane's rule "persisted proof is stale proof" is kept and
  sharpened: persisted *events* are history; verdicts are always
  re-derived from in-memory, this-boot proofs.)
- Session scoping is kept from both prior arts: a door proof is global,
  a headline is not. BTE requires `armedSessionId == session.id`
  (accessibility), `protectingSessionId == session.id` (engine), and
  the canary exercises run *for* that session id (stamped with it), so
  yesterday's session can never lend proof to today's — the shipped
  test `witnessesForAnotherSession_doNotProveThisOne` becomes a BTE
  invariant across all doors.

---

## 3. Proof mechanism per door

### 3.0 DOOR 0 — the shield engine (not counted in the four; gates anyway)

Existing machinery: `MonolithShieldService` 2 s loop;
`updateSessionProgress(stampShieldBeat = true)` persists
`shieldLastBeatWallMillis`; repository gap law at 10 s.

BTE adds, in the service's companion (in-memory only):

- `iterationCounter: Long` — incremented at the *end* of each full
  iteration (after the foreground query), with `lastIterationMono`,
  `lastIterationSessionId`, `lastIterationOutcome`
  (`COMPLETED / QUERY_FAILED / NO_SESSION`).
- Headline requirement: counter advanced within 4 s (2 loop periods —
  one missed beat is weather, two is death), `lastIterationOutcome ==
  COMPLETED`, session id matches, *and* the persisted beat ≤ 10 s
  (existing law, kept as the cross-process witness: the UI process can
  see the beat even if it somehow holds a stale in-memory view).
- The foreground-service notification is updated on every verdict
  change (§7.2) — `NotificationManager` presence of the shield
  notification is *not* used as proof (the app posts it itself; a
  self-assertion is not a witness — the MonolithCustomComponents
  comment already states this law for another leg, BTE generalises it).

**Cost:** ~40 lines in the service, ~15 in the derivation. No new APIs.

### 3.1 DOOR 1 — ACCESSIBILITY (intercept: tamper guard, Shorts/Reels sniper, browser URL blocker)

**T1 · GRANT.** `Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES`
contains the service **and** the service's `ServiceInfo` resolves with
`canRetrieveWindowContent == true`. (Existing `isAccessibilityEnabled()`
does the first half.)

**T2 · BIND.** `instance != null`, `onServiceConnected` stamp this
boot, `armedSessionId == session.id` with `armedMono` this boot
(existing collector, kept verbatim).

**T3 · EXERCISE — two sub-proofs, both required:**

**(a) Canary readback — proves retrieval + traversal + text reading.**
BTE renders a *canary node*: a view inside Monolith's own UI (Shield
detail sheet and Deck both host it; when neither is visible, the
foreground-service's overlay-free fallback is the block screen itself
during a session) carrying a rotating content-description token,
e.g. `monolith-canary-<probeId>`, 1 dp, full ink — present in the
accessibility tree, not a hidden test hack: it is labelled in the
detail sheet as the proof target. The probe calls a new service method
`provePipelineExercised(probeId, token): ProveResult`:

1. `rootInActiveWindow` acquired (today's `provePipelineNow()`, kept
   as step 1 of 3, not the whole proof);
2. `windows` enumerated; the window list contains a non-SystemUI
   window — proving the interactive-windows path, which needs one
   config demand (§10): add `flagRetrieveInteractiveWindows` to
   `accessibility_service_config.xml` (flags today: `flagDefault |
   flagIncludeNotImportantViews | flagReportViewIds`;
   `canRetrieveWindowContent` is already true). Changing the config
   can force a re-bind on update — priced in §9;
3. node tree traversed (`findAccessibilityNodeInfosByText(token)` or
   recursive walk, the same traversal helpers the URL blocker uses)
   until the canary token is read back verbatim.

Deadline 750 ms. Outcomes: `READBACK_OK(latencyMs)` / `NO_ROOT` /
`NO_WINDOWS` / `TOKEN_NOT_FOUND` / `THREW(code)`. A root over Monolith's
own window alone no longer passes: step 2+3 are the exercise.

**(b) Decision dry-run — proves the rules path can decide.**
Today the three detectors live inline in `onAccessibilityEvent`,
reading live nodes and DAOs inside a coroutine. BTE extracts their
*pure cores* (package + extracted-text + rules → decision) — the
single largest refactor in this spec, priced honestly in §9 — and the
probe runs them against synthetic inputs built from the *live* rules:

- take one actually-SHUT package from `dao.getAllAppsSnapshot()`
  (if none is SHUT, this sub-proof is `VACUOUS` and the detail sheet
  says so — a session ruling nothing has a smaller claim, printed);
- assert the tamper-settings predicate fires on a synthetic Settings
  package fixture, the URL predicate fires on the first blocked domain
  from the persisted site list, and the foreground decision for the
  SHUT package is INTERCEPT.

This proves DB readability, guard-settings readability, and decision
code health at probe time. It does **not** prove a live intercept —
and is never printed as one; the detail sheet's wording is
`DECISION DRY-RUN PASSED`, distinct from the readback line.

**Continuous passive proof (between probes).** Every event that
acquires a root stamps `lastRootMono` (existing `notePipelineProof()`,
kept as the *only* stamp door). BTE also records
`lastForeignRootMono` + the event's package *class* (own / system /
other — never content, never the package name in the log) when the
root's package ≠ Monolith's. A foreign root stamp inside the TTL
refreshes T3(a) without a canary run — real traffic is better proof
than synthetic traffic, and during an active session foreign events
are frequent, so canaries mostly matter at arm time and in quiet
stretches. This is the mechanism that keeps the 30 s TTL (§5) cheap.

**Failure words:** `MISSING` (T1 absent) · `UNBOUND` (T1 present, T2
absent) · `SILENT` (T2 present, no root/canary inside deadline — the
wedged-service case) · `DECISION FAILED` (dry-run mismatch — a code/
data fault, surfaces as ERROR with the failing predicate named).

### 3.2 DOOR 2 — USAGE ACCESS (the shield loop's foreground eye)

The shield's second door: `queryForegroundBlockedApp()` reads
`UsageStatsManager.queryUsageStats(INTERVAL_DAILY, …)` for a blocked
package with `lastTimeUsed` inside 8 s. If usage data stops flowing,
this door sees nothing and the accessibility door carries app
blocking alone — degraded in fact, invisible today.

**T1 · GRANT.** Existing three-valued AppOps read
(`unsafeCheckOpNoThrow(OPSTR_GET_USAGE_STATS)` → ALLOWED / UNSET /
DENIED), kept exactly; UNSET and DENIED print differently and deep-link
differently (§7.1).

**T2 · SERVICE.** `UsageStatsManager` obtainable; a stats query returns
non-null (an ALLOWED grant returning null is already a SILENT smell).

**T3 · EXERCISE — the foreground canary.** The only data the door
exists to deliver is "package X came foreground at time T". BTE makes
it deliver exactly that, for a package it controls:

1. Probe records `t0 = wall now`, generates `probeId`.
2. BTE starts `ProofCanaryActivity` — a transparent, no-history,
   `excludeFromRecents` activity in Monolith's own package — and
   finishes it immediately. Monolith's own package has just genuinely
   foregrounded a *new activity*, generating real usage events.
3. Probe polls `UsageEvents` (`queryEvents(t0 - 5s, now)`) every
   250 ms up to a 4 s deadline for `MOVE_TO_FOREGROUND` /
   `ACTIVITY_RESUMED` naming `ProofCanaryActivity`, and separately
   requires `queryUsageStats` to report Monolith's own package with
   `lastTimeUsed >= t0 - 2s` (the same query shape the shield loop
   runs — if the production query can't see us, it can't see a SHUT
   app either).

Outcomes: `EVENT_SEEN(latencyMs)` / `STATS_ONLY` (stats updated,
events API silent — partial: prints `PROVEN (STATS PATH)` because the
shield's production path is the stats query; the events gap is logged)
 / `SILENT` (neither moved inside 4 s despite ALLOWED — the OEM-spoof
case, and the single most valuable catch in this whole spec) /
`THREW(code)`.

**Cadence honesty:** usage data latency varies wildly by OEM (sub-second
to tens of seconds for stats buckets). The 4 s deadline governs the
*events* path; the *stats* path gets a 15 s observation window recorded
passively: every shield iteration already queries stats — BTE stamps
`lastStatsFreshMono` whenever the loop's own query returns the current
foreground package correctly (it knows the foreground from the
accessibility door's last foreign package when available). In
practice: T3 is earned at arm by the canary (events path), and
*maintained* by the loop's own production queries succeeding — the
exercise is the work itself. TTL 90 s (§5) reflects real stats latency;
pretending 10 s precision here would manufacture STALE flapping.

**Cost:** one tiny activity + manifest entry, ~120 lines probe, no new
permission (PACKAGE_USAGE_STATS already held).

### 3.3 DOOR 3 — DEVICE ADMIN (integrity) — and its named ceiling

**T1 · GRANT.** `DevicePolicyManager.isAdminActive(ComponentName)`
(existing `isDeviceAdminActive()`).
**T2 · GUARD SET.** T1 **plus** the receiver component is enabled
(`PackageManager.getComponentEnabledSetting`), **plus** a
`onDisableRequested` / `onDisabled` stamp check: if a disable was
*requested* during this session, that is a **TAMPER ATTEMPT** event
(§8) — the guard held (the user backed out) or fell (onDisabled →
instant verdict loss, §5.3); either way it is printed in the detail
sheet and logged, never swallowed.

**The ceiling, stated plainly:** on stock Android there is **no T3 for
device admin**. An app cannot simulate its own deactivation, cannot
invoke `onDisableRequested`, and cannot observe the Settings admin
page. Any design claiming to have "proven" admin end-to-end is lying.
BTE therefore: (i) requires only T2 for this door, (ii) prints a
*different word* — `GUARD SET`, with a ◆ glyph vs. the ● of exercised
doors — so the headline's `4/4` count never implies four equal proofs,
and the detail sheet footnote says why, in one line: `ADMIN CAN BE
CHECKED, NOT EXERCISED — ANDROID GIVES NO APP A WAY TO TEST ITS OWN
REMOVAL GUARD.` (iii) compensates with *watchfulness*: DPM re-read on
every light probe + `onDisabled` instant invalidation + a
BOOT_COMPLETED re-check (admin state survives reboot; a revocation
while the phone was off is caught at boot, before any session can arm
on a stale claim).

### 3.4 DOOR 4 — NOTIFICATION LISTENER (capture/suppression)

Production path today: `onNotificationPosted` → session + rule check →
insert into digest DB → `cancelNotification(key)`. Own-package and
SystemUI posts return early; call/alarm categories never suppressed.

**T1 · GRANT.** Upgrade the string check: prefer
`NotificationManager.isNotificationListenerAccessGranted(ComponentName)`
where available, fall back to the `enabled_notification_listeners`
secure string (existing `isNotificationListenerEnabled()`).
**T2 · BIND.** `instance != null` + `onListenerConnected` stamp this
boot (existing WatchStore writer, kept) **plus** a bridge exercise-lite:
`getActiveNotifications()` returns (array, possibly empty) without
throwing — a bound listener whose system bridge is dead throws or
hangs here.
**T3 · EXERCISE — the self-notification canary:**

1. Probe generates `token`, registers it in an in-memory
   `ListenerCanaryRegistry` (token → probeId, deadline) *before*
   posting. The listener consults this registry **first** in
   `onNotificationPosted`, before the own-package early return — the
   one deliberate change to the production handler, ~10 lines.
2. BTE posts a notification on a dedicated channel
   `monolith_proof_canary` (IMPORTANCE_MIN, silent, no sound/vibration,
   `setLocalOnly`, group-collapsed) whose title carries the token.
3. Listener observes the token → stamps `canarySeenMono/Wall` → calls
   `cancelNotification(key)` itself (exercising the suppression half
   of the production path) → BTE verifies within 2 s that the key is
   absent from `getActiveNotifications()`.
4. The canary is **never** inserted into the digest DB and never counted
   as a captured notification (registry branch returns before any DAO
   call — asserted by test, §9.3).

Outcomes: `OBSERVED_AND_CANCELLED(latencyMs)` / `SEEN_NOT_CANCELLED`
(DEGRADED — capture works, suppression doesn't; if capture is the
claim, headline may stay LIVE only with the degraded clause §6.1 —
default: DEGRADED prints as NOT BLOCKING for the capture claim,
because a notification that is seen but shown has leaked) /
`SILENT` (not observed in 2 s) / `UNAVAILABLE` (POST_NOTIFICATIONS
denied for Monolith's own posts — then own-post canary cannot run;
fallback: T2 + passive maintenance, door prints `BOUND ONLY —
CANARY UNAVAILABLE: APP NOTIFICATIONS OFF`, and if this door is
claimed, headline is NOT BLOCKING with that reason; turning Monolith's
notifications back on is the FIX — honest, because without them the
FGS truth line in §7.2 is also dead).

**Visibility cost, stated:** a MIN-importance notification cancelled
inside ~200 ms can still flicker on some OEM launchers. Mitigations:
post only at arm, at surface entry, and every 5 min during a session —
never on the 30 s light cadence; post+cancel are back-to-back from the
listener's callback, not from a timer. Residual flicker risk is a real
cost of T3 here and is listed in §9 as accepted-with-mitigation, with
the fallback that a build may ship this door at "T3 at arm + T2
maintained" (canary TTL = session, bind-loss still instant) if phone
testing shows flicker — the tier words already support saying exactly
that.

**Passive maintenance:** every real capture (a genuine SHUT-app
notification swallowed) stamps the same T3 freshness as a canary —
real work outranks synthetic work, same principle as §3.1.

---

## 4. The engine — `BlockingTruthEngine`

One new module; both prior derivations retire into it.

```
service/BlockingTruth.kt      — pure types + derivation (no Android)
service/BlockingTruthEngine.kt— collectors, probes, observers, log writer
service/ProofCanaryActivity.kt
service/ListenerCanaryRegistry.kt ( folds into Engine if preferred )
data/TruthProofEvent.kt       — Room entity + DAO additions (§8)
ui/BlockingTruthLine.kt       — headline + detail sheet composables
```

### 4.1 Types (pure, unit-testable — the `ShieldBlockingStatus` discipline, extended)

```kotlin
enum class DoorId { ENGINE, ACCESSIBILITY, USAGE_ACCESS, DEVICE_ADMIN, NOTIFICATION_LISTENER }
enum class DoorTier { PROVEN, GUARD_SET, BOUND_ONLY, GRANTED_ONLY, MISSING, UNSET, STALE, SILENT, ERROR, VACUOUS, NOT_CLAIMED }
data class DoorProof(
  val door: DoorId, val tier: DoorTier,
  val provedMono: Long, val provedWall: Long, val bootCount: Int,
  val latencyMs: Long?, val detailCode: String, val sessionId: Int)
data class ClaimSet(val intercept: Boolean = true, val capture: Boolean,
                    val integrity: Boolean, val integrityWaivedWall: Long?)
fun computeBlockingTruth(session, claimSet, proofs: Map<DoorId, DoorProof>,
                         nowMono, currentBoot): BlockingTruth
```

`BlockingTruth` = verdict enum + the exact headline string + per-door
print rows + missing/degraded door list + youngest-proof age. Same
contract as today: **no surface may invent its own version** — Shield,
Deck, block screen, FGS notification, and receipt all render this one
object's strings.

Verdicts: `LIVE` · `NO_SESSION` · `DOOR_MISSING` · `DOOR_UNPROVEN` ·
`DOOR_STALE` · `DOOR_SILENT` · `ENGINE_DEAD` · `CHECKING` (transient
only, §6.3). `isBlocking == (verdict == LIVE)` — one boolean, as today.

### 4.2 Probe scheduler — who proves, when

All probes are driven from surfaces and the shield loop. **No new
background scheduler, no WorkManager, no extra wake locks.** When no
session is running and no surface is visible, BTE does nothing — truth
nobody is reading is not worth battery, and the next surface entry
probes fresh (that *is* the residency idea, completed).

| Trigger | Probe |
|---|---|
| Arm attempt (extends `ArmProvingController`) | Full heavy probe: all doors T3 (admin T2), deadline-bound into the existing 500 ms proving window's successor — see §9.2 note: canary latencies (usage events ≤ 4 s) exceed the cascade's 500 ms deadline, so arm runs heavy probes *concurrently from finger-down on the slide's first movement*, not from the latch; by seal time they have had the whole gesture. A door still pending at seal = UNPROVEN refusal, named (§7.1) |
| Shield or Deck entry / `onResume` | Light probe (T1+T2 all doors, a11y root, engine beat) immediately + heavy probe if any claimed door's T3 is stale or absent |
| Session active, shield loop | Light re-read piggybacked every iteration via `WatchStore.onShieldIteration` successor (2 s, already running); heavy canaries on their TTL cadences: a11y on TTL expiry if no foreign-root stamp refreshed it, usage maintained by production queries (§3.2), listener canary every 5 min |
| Any invalidation event (§5.3) | Verdict drops *first*, re-probe second ( debounced 500 ms, backoff 1 s → 5 s → 30 s while the door stays down ) |
| User taps headline / `RE-PROVE` | Full heavy probe, now. The tap is an accelerator, never a requirement — the fix for the TAP-TO-RETRY trap |
| Boot completed | All proofs void (bootCount); T1/T2 sweep only; no session can exist yet |

Probe concurrency: one heavy probe per door at a time (single-flight);
light probes are reads of already-stamped state plus cheap platform
queries, callable synchronously from the ViewModel exactly as
`standingWarrant()` is today.

### 4.3 WatchStore's fate

BTE *absorbs* it: the nine-item store, its writers, its 45 s perm-print
staleness, and the Guard-line count all survive, but (i) the counted
four's count becomes **proven count** (§7.4), (ii) `deriveWarrant()` is
deleted and the Deck's warrant becomes a *view* of `BlockingTruth`
(Deck cares about the intercept doors pre-session: with no session,
BTE derives against the *standing claim set* — the claim the next arm
would make — and prints `WOULD BLOCK — 4/4 PROVEN` / `WOULD NOT BLOCK —
…` grammar, §6.4), and (iii) the store gains the BTE proof map as its
source. One store, one derivation, two prior arts retired — no third
truth is added alongside two living ones.

---

## 5. Staleness rules

### 5.1 TTLs (monotonic age of the door's last qualifying proof)

| Door | TTL | Basis |
|---|---|---|
| Engine iteration | 4 s | 2× the 2 s loop; plus persisted beat ≤ 10 s (existing gap law) |
| Accessibility T3 | 30 s | Foreign-root stamps refresh continuously in real use; 30 s of total a11y silence during an active session *is* the suspicious fact. (WatchStore's 45 s perm-print TTL remains for T1 prints, a different, weaker claim — the two numbers coexist because they age different tiers, and the detail sheet labels which is which) |
| Usage T3 | 90 s | Stats latency reality (§3.2); events-canary at arm earns it, production queries maintain it |
| Device admin T2 | 60 s | Cheap DPM re-read on light cadence; revocation callbacks invalidate instantly regardless |
| Listener T3 | 5 min | Canary cost (§3.4); bind-loss invalidates instantly regardless |
| Any T1/T2 fallback print | 45 s | WatchStore law, inherited |

### 5.2 Ageing laws

1. Age is computed at derivation time from mono stamps, never stored
   (WatchStore's rule, kept).
2. A `MISSING` reading never goes stale (WatchStore's rule, kept): an
   absent grant stays absent until re-measured *present*.
3. Crossing a TTL flips that door to `STALE` and the verdict with it —
   no grace period, no hysteresis on the *print*. (Recovery uses
   backoff, §4.2, so a flapping door flaps the headline truthfully
   rather than being smoothed into a lie; the proof log preserves the
   flap count for the receipt, §8.)
4. Wall-clock jumps (user changes time, timezone travel) cannot affect
   any verdict — mono only. Boot changes void everything (§2.4).
5. `CHECKING` is not a hiding place: a probe in flight older than its
   deadline resolves to its failure word automatically (§6.3).

### 5.3 Instant invalidation (TTL is the backstop, never the mechanism)

Registered at engine start, unregistered never (process-lifetime):

- Accessibility `onUnbind` / `onDestroy` → door void *in the callback*,
  before any rebind can inherit the stamp (Register-3 Z-1 discipline,
  already in the code).
- Listener `onListenerDisconnected` / `onDestroy` → same.
- Device-admin receiver `onDisabled` → same; `onDisableRequested` →
  TAMPER ATTEMPT event + immediate T1 re-read.
- `AppOpsManager.startWatchingMode(OPSTR_GET_USAGE_STATS, …)` callback
  → usage T1 void + re-probe.
- `ContentObserver`s on `ENABLED_ACCESSIBILITY_SERVICES` and
  `enabled_notification_listeners` secure settings → respective T1
  void + re-probe (catches revocation paths that skip the service
  callbacks — e.g. force-stop and OEM service killers, where
  `onDestroy` may never run: the observer + the next light probe's
  `instance` check catch what the callback didn't).
- Shield service `onDestroy` / stop action → engine door void.

Every invalidation pokes the snapshot flow (existing `WatchStore.poke()`
pattern), so every visible surface and the FGS notification re-derive
in the same frame budget as a data change — the headline cannot sit
stale on screen while the store already knows.

---

## 6. The exact live lines

Grammar, fixed for every surface: `●` = standing, `○` = not standing
(existing Shield finish law — state carried by verb + glyph, never
colour alone; full ink in every state). Anonymous Pro for the line
(existing Shield telemetry face). One line, ≤ the existing 40 dp slot
on Shield; the FGS notification and Deck use the same string.

### 6.1 Headlines (verbatim templates; `<…>` are the only variables)

```
● BLOCKING NOW — <LABEL> · ENDS <HH:MM> · PROVED <AGE> · <N>/<M> DOORS
● BLOCKING NOW — <LABEL> · PROVED <AGE> · <N>/<M> DOORS                      (stopwatch: no ENDS)
● BLOCKING NOW — <LABEL> · ENDS <HH:MM> · SAFE BREAK — PEEK APPS OPEN · PROVED <AGE> · <N>/<M> DOORS
○ NOT BLOCKING — NO SESSION RUNNING
○ NOT BLOCKING — <LABEL> RUNNING · ACCESSIBILITY SILENT — NO INTERCEPT WILL FIRE · LAST PROVED <HH:MM:SS>
○ NOT BLOCKING — <LABEL> RUNNING · USAGE ACCESS SILENT — FOREGROUND EYE BLIND · LAST PROVED <HH:MM:SS>
○ NOT BLOCKING — <LABEL> RUNNING · USAGE ACCESS NOT SET — NEVER GRANTED
○ NOT BLOCKING — <LABEL> RUNNING · NOTIFICATION CAPTURE SILENT · LAST PROVED <HH:MM:SS>
○ NOT BLOCKING — <LABEL> RUNNING · UNINSTALL GUARD OFF — DEVICE ADMIN MISSING
○ NOT BLOCKING — <LABEL> RUNNING · SHIELD ENGINE DEAD — LOOP STOPPED <AGE> AGO
○ NOT BLOCKING — <LABEL> RUNNING · <DOOR> PROOF STALE — LAST PROVED <HH:MM:SS>
○ NOT BLOCKING — <LABEL> RUNNING · ENFORCEMENT NOT PROVEN — <DOOR> <BOUND ONLY|GRANTED ONLY>
○ CHECKING BLOCKING — PROVING <M> DOORS…                                     (transient, §6.3)
```

- `<AGE>` is the age of the *youngest required proof* at print time,
  rendered `4s` / `2m` — the headline admits how old its own evidence
  is, every time. It is recomputed by a 1 s ticker while the surface
  lives (ViewModel, §10), so a line left on screen ages visibly and
  flips itself at TTL without any user action — the direct cure for
  the shipped pull-only refresh.
- `<N>/<M>`: N = claimed doors at required tier, M = claimed doors.
  Admin at GUARD SET counts as at-tier (its ceiling), and the detail
  sheet — never the headline — carries the ◆ distinction. A waived
  door is not in M and its waiver is printed in the detail sheet.
- Multiple simultaneous failures: headline names the *highest-priority*
  failure (order: ENGINE → ACCESSIBILITY → USAGE → LISTENER → ADMIN —
  intercept before capture before integrity, i.e. "what leaks first"),
  and the detail sheet lists all. The headline never concatenates
  failures into an unreadable string.
- The existing shipped lines are preserved where they were true:
  `NOT BLOCKING — NO SESSION RUNNING` is unchanged; the old generic
  `ENFORCEMENT NOT PROVEN` survives only in its door-named form above.

### 6.2 The detail sheet (tap the headline) — per-door rows, verbatim shape

```
ACCESSIBILITY          ● PROVEN      readback 182ms · proved 14:02:11 (12s ago) · decision dry-run passed
USAGE ACCESS           ● PROVEN      foreground event 640ms · proved 14:01:48 (35s ago)
DEVICE ADMIN           ◆ GUARD SET   checked 14:02:02 (21s ago) · can be checked, not exercised
NOTIFICATION LISTENER  ● PROVEN      canary seen + cancelled 210ms · proved 13:58:20 (4m ago)
SHIELD ENGINE          ● RUNNING     iteration 1,204 · beat 1s ago · for this session
LAST FULL PROOF        14:02:11 · NEXT CANARY ≤ 5m · [ RE-PROVE NOW ] · [ PROOF LOG ]
```

Each row's right side is a FIX deep-link *only when the door is not at
tier*: ACCESSIBILITY → `ACTION_ACCESSIBILITY_SETTINGS`; USAGE →
`ACTION_USAGE_ACCESS_SETTINGS`; ADMIN → the admin-activation intent
for the receiver; LISTENER → `ACTION_NOTIFICATION_LISTENER_SETTINGS`.
A row at tier shows no link — nothing to fix is nothing to tap.

### 6.3 CHECKING — a state with a deadline, not a resting place

`CHECKING` may print only while a heavy probe is genuinely in flight
and never for longer than the longest door deadline (4 s, usage). It
then resolves — to LIVE or to a named NOT BLOCKING — without user
input. This is the structural end of "CAN'T CHECK BLOCKING — TAP TO
RETRY": retry is the engine's job, on backoff, forever, while a
session or surface cares; the tap merely hurries it.

### 6.4 Pre-session grammar (Deck)

No session → no BLOCKING claim is possible, and the Deck must not
borrow the session grammar. It prints the standing claim's readiness:

```
● READY — WOULD BLOCK · 4/4 DOORS PROVEN <AGE>
○ NOT READY — ACCESSIBILITY SILENT · ARM WILL REFUSE
```

The arm cascade consumes the same object (§7.1): readiness *is* the
pre-arm verdict, so the slide can never be told LIVE by one derivation
and refused by another — the warrant/cascade split disappears.

---

## 7. Failure surfaces

### 7.1 At arm

`ArmProvingController` gains the BTE heavy probe as a fourth link
family beside its existing three witnessed links, under the same laws:
concurrent from the latch, per-door pending sets named at refusal,
annulment on failure. Refusal sentences extend the existing form:

```
ACCESSIBILITY — NO PROOF. NOTHING WAS SEALED.          (existing shape)
USAGE ACCESS — SILENT. NOTHING WAS SEALED. [ OPEN USAGE ACCESS SETTINGS ]
NOTIFICATION LISTENER — UNBOUND. NOTHING WAS SEALED.
```

The seal sound / t=0 still fire only on full proof (existing law);
BTE changes *what counts as proof*, not the ceremony.

### 7.2 Mid-session loss — the surfaces, in order of reach

1. **Headline flips** on Shield/Deck in the invalidation frame (§5.3).
2. **FGS notification** (already always present during a session): its
   subtitle becomes the headline string on every verdict change. The
   user learns blocking died *without opening the app* — today the
   notification says "Monolith Locked / Protecting your focus
   session…" unconditionally, which under this spec is a prohibited
   sentence whenever the verdict isn't LIVE.
3. **Truth alarm:** if a session that *was* LIVE loses an intercept
   door for > 10 continuous seconds (one shield-gap law's worth —
   transient OEM stalls don't cry wolf), BTE posts one
   high-importance notification: `BLOCKING LOST — ACCESSIBILITY OFF ·
   TAP TO FIX`, deep-linking the door's settings. Once per loss
   episode; recovery posts nothing (the headline restoring is enough)
   but logs (§8). No sound during SLEEP sessions' windows except via
   the session's own alert policy — priced as a settings question for
   the builder, default: vibrate only.
4. **Session truth:** the loss opens a **TruthGap** (§8). The session
   is not silently "completed" over a hole: its outcome gains
   `ELAPSED_WITH_TRUTH_GAP` beside the existing `ELAPSED_SHIELD_GAP`
   machinery (which BTE generalises: a shield gap *is* a truth gap of
   the engine door), and the finish receipt prints
   `UNGUARDED 6 MIN — ACCESSIBILITY OFF 14:03–14:09` in the receipt's
   existing truthful style. A session with no gaps prints nothing —
   absence of the line is itself the clean record.
5. **Block screen:** if the user opens Monolith mid-session (the
   launcher-style block surface of the current design ruling), its
   first line is the headline — a NOT BLOCKING block screen must say
   so on its face, with the FIX link, not imitate a standing wall.

### 7.3 Recovery

Door returns (observer/callback/light probe) → heavy re-probe
automatically → verdict restores → gap closes with its duration →
all surfaces re-print. At no point does recovery require the user to
tap, retry, or re-arm. If the door returns *degraded* (e.g. listener
SEEN_NOT_CANCELLED), the verdict restores only to the degraded truth,
printed as such.

### 7.4 The Guard line / counts

Everywhere a count prints today (header Guard line `n/4`, PARK board),
the numerator becomes **doors at required tier**, denominator the
claim set size — never grants. `GUARD 2/4 PROVEN` replaces a count that
today can read `4/4` on grants alone while the warrant is UNPROVEN.

---

## 8. The proof log (the deferred Readiness Log, built)

Append-only Room table — **DB v12 → v13, explicit `Migration(12,13)`**
(destructive fallback is live in this codebase; an un-migrated version
bump would wipe the sessions the log exists to describe — the studio
has been bitten by exactly this precondition before).

```kotlin
@Entity TruthProofEvent(
  id, sessionId: Int?, door: String, kind: String,   // PROBE_OK, PROBE_FAIL, INVALIDATED, RECOVERED, TAMPER_ATTEMPT, GAP_OPEN, GAP_CLOSE
  tier: String, detailCode: String, latencyMs: Long?,
  mono: Long, wall: Long, bootCount: Int)
```

- Writers: BTE only. Readers: proof-log sheet (from §6.2), receipt
  gap accounting, Drawer (quiet drawer law: stored forever, no scores,
  no day-grading — the log is a record, not a judgement; gaps print as
  facts on their own session's receipt only).
- Retention: session-linked events live with their session; unlinked
  (pre-session) probe events prune at 7 days / 2,000 rows, pruned by
  the shield loop's existing periodic tick — no new worker.
- Gaps are *derived* from GAP_OPEN/GAP_CLOSE pairs at receipt time,
  not stored as durations — durations computed from events can't drift
  from the events.
- The log is never a verdict source (§2.4). Killing the app, clearing
  the log, or rolling the DB back can only ever make the headline
  *less* proven, never more.

---

## 9. Cost

### 9.1 Build cost (declared, uncompiled — nobody compiles)

| Piece | Files | Est. LOC |
|---|---|---|
| Pure types + derivation + headline grammar (§4.1, §6) | 1 new (`BlockingTruth.kt`), retire `ShieldBlockingStatus.kt` derivation into it, `deriveWarrant` deleted | ~450 prod, ~600 test |
| Engine: collectors, scheduler, observers, single-flight probes (§4.2, §5.3) | 1 new, `WatchStore` absorbed/renamed | ~500 |
| Door 0: iteration counter + outcome stamps | `MonolithShieldService` edit | ~55 |
| Door 1: `provePipelineExercised`, canary host view, decision-core extraction from `onAccessibilityEvent` (**the expensive line** — three detectors untangled from live-node coroutine code into pure cores + adapters) | `MonolithAccessibilityService` + 1 new pure file + config XML flag | ~700 |
| Door 2: `ProofCanaryActivity` + events/stats probe | 2 new (activity ~40, probe ~120) + manifest | ~160 |
| Door 3: component check, tamper stamps, boot re-check | receiver + repository edits | ~80 |
| Door 4: canary channel, registry hook in listener, verify-cancel | listener + engine edits | ~220 |
| Proof log: entity, DAO, `Migration(12,13)`, gap derivation, receipt line | DB + repository + receipt edits | ~350 |
| Surfaces: headline ticker (1 s re-derive while visible), detail sheet, FGS verdict updates, Guard-line proven count, Deck readiness grammar, arm link family | ViewModel + Shield + Deck + components edits | ~600 |
| Settings deep-links per door + truth alarm | engine/UI edits | ~120 |
| **Total** | **~14 files touched/created, 1 migration, 1 manifest entry, 1 XML flag** | **~3,200 prod + ~1,400 test LOC** |

In studio terms: one builder-cell programme of roughly the warrant
lane's size again — call it **3–4 builder-weeks of lane time**, with
the accessibility decision-extraction as the critical path and the
only piece that touches live blocking behaviour (everything else is
additive observation). No server, no account, no new permission:
PACKAGE_USAGE_STATS, accessibility, listener, admin, and notification
posting are all already held; the canary activity and channel ride
existing grants.

### 9.2 Runtime cost (the user's phone pays this — priced, not waved)

- Light probe: platform reads only; piggybacked on the existing 2 s
  shield loop and surface entries. Negligible.
- A11y canary: one root+windows+traversal, ≤ 750 ms budget, at arm /
  surface entry / TTL expiry without foreign traffic. Millisecond-
  scale CPU; no wake lock beyond the already-running FGS.
- Usage canary: one activity start/finish + ≤ 4 s of 250 ms polls, at
  arm and on stale only. The activity is transparent and
  `excludeFromRecents`; it *will* appear in the user's own usage stats
  as Monolith time — truthfully, as Monolith.
- Listener canary: one MIN-importance post/cancel per 5 min of session
  (§3.4 flicker caveat).
- 1 s headline ticker: only while Shield/Deck is composed; a mono-age
  recompute of an in-memory map. Negligible.
- Proof log: ≤ a few KB/day at these cadences; pruned (§8).
- Net battery claim, honestly bounded: **< 0.5% per 3 h session** is
  the design budget, dominated by nothing — every cadence rides work
  the app already does. This is a budget to be *measured on the phone
  bench*, not a promise; the log's probe timestamps make the real
  cadence auditable after the fact.

### 9.3 Test cost

- Pure truth-table suite extending `ShieldBlockingStatusTest` +
  `Wave2ShieldPrintTest` patterns: ~40 cases (per-door tier × claim ×
  staleness × session-match, headline-string equality for every §6.1
  template, CHECKING deadline resolution, boot-void).
- Fake-probe unit tests per door (registry, canary result mapping,
  gap open/close derivation, receipt gap line).
- One instrumented/Robolectric risk flagged: `UsageEvents` and
  `windows` behaviour under Robolectric are partial — the probes are
  therefore written against narrow interfaces (`UsageProbePort`,
  `A11yProbePort`) with fakes in tests, real adapters on device. The
  phone bench (arm with each door killed in turn; kill a11y mid-
  session; revoke usage mid-session; disable admin mid-session) is
  the acceptance test — listed in §12, because unit tests cannot
  prove an OEM delivers usage events.

---

## 10. Demands on the app (what BTE requires the rest of Monolith to give)

1. **One derivation law, enforced:** Shield, Deck, block screen, FGS
   notification, PARK board, and receipt consume `BlockingTruth` and
   nothing else. Any future surface printing a blocking claim from a
   local boolean is a lie-register offence by construction.
2. **`accessibility_service_config.xml`:** add
   `flagRetrieveInteractiveWindows`. Consequence priced: config change
   may require users to re-toggle the service after update; the
   migration/update path must detect `BOUND ONLY — RE-TOGGLE
   ACCESSIBILITY TO COMPLETE UPDATE` as a named state rather than a
   mysterious SILENT.
3. **Accessibility handler refactor:** the three detectors gain pure
   cores (§3.1b). This touches the live intercept path — it must land
   with its existing behaviour tests green *before* BTE probes consume
   the cores, and it is the one demand that can regress blocking
   itself if done carelessly. Sequenced first in §11 for that reason.
4. **Listener handler:** registry-first branch (§3.4) + a dedicated
   canary notification channel created at first engine start.
5. **Manifest:** `ProofCanaryActivity` (exported=false, transparent
   theme, noHistory, excludeFromRecents).
6. **DB v13** with explicit migration; claim-set columns on the
   session row (capture claimed, integrity claimed/waived + waiver
   wall time) frozen at arm.
7. **ViewModel:** a `StateFlow<BlockingTruth>` + 1 s ticker while any
   truth surface is visible; `refreshDeckWarrant()` /
   `refreshShieldBlockingStatus()` become thin calls into BTE.
8. **Receipt + Drawer:** gap derivation + the one gap line (§7.2.4);
   proof-log sheet reachable from the detail sheet.
9. **Copy discipline:** the words PROVEN / GUARD SET / SILENT / STALE
   mean §2.1's tiers everywhere, including PARK and setup screens;
   setup's grant checklist may say GRANTED — it may not say "blocking
   ready" until BTE readiness (§6.4) says so.
10. **No new permissions, no analytics, no network.** The proof log
    never leaves the device (it is inside the existing AES-GCM backup
    only if the user exports one — same posture as sessions today).

---

## 11. Slicing — every slice ships honest words

- **P0 · One truth, four doors, tiered prints (≈ 30% of cost).**
  BTE derivation + claim set + T1/T2 for all four doors + engine
  iteration proof + per-door TTLs + instant invalidation + 1 s ticker
  + FGS mirroring + proven Guard count. Headline grammar ships with
  tier words capped at reality: doors print `BOUND ONLY / GRANTED
  ONLY / GUARD SET`, and `BLOCKING NOW` in P0 requires T2-fresh on
  intercept doors — *and the detail sheet says P0's standard out
  loud*: `PROOF STANDARD: BOUND + GRANTED, NOT YET EXERCISED.` The
  headline lie (session ⇒ blocking) dies in P0; the word PROVEN does
  not yet appear. This slice alone exceeds both prior arts.
- **P1 · Exercise (≈ 45%).** Decision-core extraction, then a11y
  readback, usage canary, listener canary. As each door's T3 lands,
  its required tier rises to PROVEN and its detail line gains
  latencies. Doors land independently; a door without T3 keeps P0
  words. Arm's heavy probe arrives with this slice.
- **P2 · Memory (≈ 25%).** Proof log + migration, truth gaps, receipt
  line, proof-log sheet, truth alarm. Before P2, gaps are known live
  but not remembered; P2's absence is printed nowhere because nothing
  claims history before it exists.

Reordering law: P1's extraction (§10.3) may precede P0 if the builder
prefers the risky refactor first — the slices' *printing* order may
not invert: no exercised word before its exercise runs.

---

## 12. Acceptance (phone bench — the only court for §3)

1. Arm with all doors healthy → `BLOCKING NOW … PROVED <age> · 4/4`,
   age visibly ticking; detail sheet shows three latencies + ◆ admin.
2. Revoke accessibility in Settings mid-session → headline flips
   within one frame of the callback; FGS notification flips; truth
   alarm at +10 s; FIX link lands on the accessibility page; re-grant
   → automatic re-probe → LIVE restores, gap on receipt.
3. Toggle usage access off mid-session → `USAGE ACCESS …` NOT
   BLOCKING; AppOps listener fires without waiting for TTL.
4. Set Monolith notifications off → listener door prints its
   UNAVAILABLE line at next probe; no crash, no false PROVEN.
5. Post a real notification from a SHUT app → captured to digest,
   listener T3 refreshes from real work (no canary needed).
6. Deactivate admin mid-session → instant NOT BLOCKING (integrity);
   request-disable-and-cancel during a session → TAMPER ATTEMPT in
   log + detail sheet.
7. Kill the shield service (force-stop path excluded — that kills
   everything) / let the loop stall → ENGINE DEAD inside 4 s + beat
   law, not at the next manual refresh.
8. Reboot mid-session-day → all proofs void at boot; no surface prints
   a resurrected LIVE from the log.
9. Cold-open the app ten times → `CHECKING` resolves every time
   without a tap; the words TAP TO RETRY appear nowhere in the app.
10. Airplane-mode / time-change / timezone-change mid-session → no
    verdict changes (mono law).

---

## 13. What this spec deliberately does not claim

- It does not claim proof *between* probes. It prints the age of its
  evidence so the gap between "proved" and "now" is always visible —
  continuous proof on a phone is sampled proof, honestly labelled.
- It does not claim an exercised canary equals a real intercept of a
  real SHUT app. It claims the pipeline's every link — retrieval,
  traversal, decision, foreground delivery, capture, suppression —
  was observed working, for this session, within its TTL. A design
  that wants the stronger claim must actually launch a blocked app
  at the user, which is a product decision (a "test the wall" button
  that launches a SHUT app and lets the user watch it bounce is
  buildable from these same parts — noted as an optional P1.5, priced
  at ~150 LOC, *not* folded into the headline's standard).
- Device admin's ceiling (§3.3) is Android's, not effort's.
- OEM usage-event latency beyond the events API's delivery is
  measured and printed, not assumed away (§3.2).

— MIP-04 (Maximum-Implementation-Potential, issue #7)
