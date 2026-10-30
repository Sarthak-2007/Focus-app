# TECH-1 — Platform truth + hostile diff review of the cold-start cure (issue #7)

**Seat:** TECH-1, TECHNICAL EXECUTION, BINDUNG's cold-start cure cell.
Report to BINDUNG only. Third seat for this job; started clean, inherited
nothing.
**Scope:** READ-ONLY on code. Judged: `1b641aa` (BUILD-1, self-probe),
`55bcae4` (BUILD-2, cured refusal table), `b13ca1a` (BUILD-3, tests),
on branch `agent/issue-7-coldstart`.
**Method:** full diffs read line by line; callers/callees grepped;
platform claims grounded in the official API reference / AOSP source
behaviour as cited per claim. Anything I could not ground is marked
**UNVERIFIED** — no invented numbers anywhere below.

---

## PART A — PLATFORM TRUTH

### A1. `rootInActiveWindow` outside an event callback

**Verdict: legal, designed-for, and the cure calls it correctly
(off the UI thread). Two probes per residency are battery- and
binder-safe by any honest measure.**

- **Availability.** The API reference places no event-callback or
  thread precondition on `AccessibilityService.getRootInActiveWindow()`:
  it "gets the root node in the currently active window if this service
  can retrieve window content", retrievable at any time via
  `getRootInActiveWindow()` / `getWindows()` / `findFocus()` once the
  service declares `canRetrieveWindowContent` (Monolith does:
  `accessibility_service_config.xml`, `canRetrieveWindowContent="true"`).
  Before `onServiceConnected` / after unbind the connection id is
  undefined and the call returns **null, it does not throw** — so a
  probe fired into a half-bound service fails safe by construction.
- **Cost (binder).** AOSP `AccessibilityService.getRootInActiveWindow()`
  delegates to the `AccessibilityInteractionClient` singleton. Its own
  class doc (AOSP source) states the model: *synchronous from the
  client's perspective, asynchronous internally* — the client thread
  binder-calls the system, the system delegates to the target window's
  hierarchy, and the client **waits up to a timeout
  (`TIMEOUT_INTERACTION_MILLIS = 5000` in AOSP) for the result**. So
  one probe = one binder round-trip to system_server + one delegation
  to the active window's process + a parceled root-node snapshot.
  That is exactly the cost the event path already pays *per event*
  (`MonolithAccessibilityService.kt:516`); two extra calls per Deck
  residency are noise next to it. Battery: no wakelock, no polling
  loop, no timer left running — negligible. Binder-safe: yes, this is
  the platform's designed query path at trivial rate.
- **Failure modes.** Null root (no active window, window transitioning,
  service not connected, interaction timeout), or a thrown query. The
  probe handles both exactly as the event path does: no stamp, failure
  counted via `noteDetectionFailure` only on the throw branch
  (`MonolithAccessibilityService.kt:377–391`). One asymmetry worth
  naming: a **timeout also surfaces as null**, not a throw, so a wedged
  window is indistinguishable from an empty one in the counter —
  inherited from the event path's contract, not created by the probe.
- **Thread rules — the one real hazard, avoided.** Because the client
  thread *blocks waiting*, calling this on the **main thread while a
  foreign window is active can block the UI for up to the 5 s timeout**
  (ANR territory). AOSP special-cases the same-process/same-main-thread
  situation (the interaction client detects it and executes the
  request inline instead of deadlocking), but that rescue covers only
  the own-window case. The cure never relies on it:
  `requestPipelineProbe()` launches on the service's
  `CoroutineScope(SupervisorJob() + Dispatchers.IO)`
  (`MonolithAccessibilityService.kt:283, 397–402`), and
  `noteResidencyStart()` on the UI thread only does a volatile write,
  a `poke()`, and two `launch`es. **No UI-thread blocking anywhere in
  the cure. Acquitted.**
- **Probe at residency start + one at +1500 ms: SAFE.** Both calls are
  fire-and-forget on IO, null/throw-clean, and idempotent (see B,
  double-stamp acquittal). The +1500 ms re-probe's guard (same
  residency, still unstamped) is the correct shape.

### A2. Binding latency; do listeners beat the +1500 ms retry?

**Verdict: no official latency bound exists — any millisecond figure
would be UNVERIFIED. Neither proposed listener signals the thing the
cure actually waits for (bind completion), because
`onServiceConnected` already IS that signal and the cure already
probes on it. Do not adopt either as a re-proof trigger.**

- **How stale can `instance == null` be while the grant is present?**
  The platform gives **no bound**. The lifecycle is "managed
  exclusively by the system" (API reference): the grant lives in
  `Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES`; binding is
  system_server starting the app's process (if dead — the cold-start
  case, so process fork + class loading are *inside* the window) and
  calling `onBind` → `onServiceConnected`. On a loaded or
  OEM-managed device (Samsung sleeping-apps, Xiaomi/HyperOS autostart
  gating and cleaner force-stops are documented OEM behaviour) that
  window stretches arbitrarily — seconds is ordinary, and a
  boot/OEM-blocked bind can be delayed far past any retry constant.
  **Any claim of the form "bind completes within N ms" is UNVERIFIED
  and I make none.** Consequence the cell must hold: `instance == null`
  at residency start is *expected*, not exceptional, and its duration
  is unbounded — which is precisely why the unbound row having **no
  grace** (it prints OFF from frame one) is a truth problem, not a
  timing problem (see B-Major-2 concurrence with DAY-1 D-1).
- **`addAccessibilityStateChangeListener` — wrong signal.** Per the
  API reference it "registers a listener for changes in the **global**
  accessibility state" and its callback delivers one boolean: whether
  accessibility (as a whole) is enabled. If any *other* service keeps
  accessibility enabled, Monolith binding or dying produces **no
  callback at all**. It cannot detect this service's grant, let alone
  its bind. Price of adopting: ~15 lines — for a signal that does not
  answer the question. **Reject.**
- **`AccessibilityServicesStateChangeListener` (API 33+) — closer,
  still wrong event.** Per its API reference it fires "whenever a
  service is enabled or disabled, or its info has been set or
  removed" — i.e. at **grant/info** changes, on the process's main
  handler. Grant ≠ bound: the callback can fire while the process is
  still being forked and `instance` is still null, and it does not
  fire again when the bind lands. As a *re-proof trigger* it would
  still need a poll/retry behind it — it replaces nothing. Price:
  ~25–40 lines (registration site that outlives a residency, API-33
  gate with an API 24–32 fallback since `minSdk = 24`, dedup against
  the `onServiceConnected` probe). **Reject as a re-proof trigger.**
  (It has one honest use — reacting to grant *revocation* mid-session
  faster than the next sweep — but that is a different feature, not
  this cure.)
- **`ContentObserver` on `ENABLED_ACCESSIBILITY_SERVICES` — same
  class of wrong.** It fires when the settings *string is written*
  (user toggles a grant, system rewrites the list, OEM cleaner strips
  an entry), for **any** service in the list, and says nothing about
  binding. It also costs the most discipline: ContentResolver
  registration/unregistration pairing, a Handler, and string parsing
  the `AccessibilityManager` API exists to replace (~30–45 lines).
  **Reject as a re-proof trigger.**
- **Bottom line:** the cure's trigger set — probe at residency start,
  probe at +1500 ms, probe in `onServiceConnected` — already contains
  the only platform event that means "the service is now bound and
  can answer a root query". The +1500 ms retry is not beaten by a
  listener; it is the cheap insurance for the gap *before* that event
  exists. Keep it.

### A3. Force-stop / OEM task-killer semantics — what prints, and is it true?

**First, a correction to the brief's premise.** "The grant is removed
after force-stop" is **not stock-AOSP truth**. What AOSP force-stop
does (documented platform behaviour since API 12's stopped state):
kills all the package's processes and puts the package in the
**stopped state** — no component of it may run again until the user
manually launches it. The `ENABLED_ACCESSIBILITY_SERVICES` entry is
a settings string; stock force-stop does not rewrite it, which is why
a still-listed service rebinds after a manual relaunch. What *does*
remove the entry in the field is the OEM layer: Xiaomi's Security
cleaner / HyperOS force-stop path is documented to switch
accessibility off, and the app "cannot switch it back on itself".
So there are two sub-cases, and the cure prints the same in both:

After force-stop + manual relaunch (the only way Monolith runs again):

| Surface | Prints | Truth? |
|---|---|---|
| Warrant (`deriveWarrant`) | `OFF` — `!a11yBound` (`instance == null`) is the first conjunct (`WatchStore.kt:443`) | True at that instant: nothing is bound, nothing can block via a11y |
| Sentence (`refusalSentenceFor`, timed overload) | `BLOCKING IS OFF — TAP TO FIX` / `FIX_ACCESSIBILITY` — the not-bound row fires **immediately, no grace** (grace covers only the bound-UNPROVEN-unstamped row) | True in the OEM-removed case, permanently until re-grant. In the grant-survived case: true only transiently — it self-corrects the moment the system rebinds and the `onServiceConnected` probe stamps — but at frame one it is **indistinguishable from a dead grant**, and the slide gate reads the same OFF |
| Seam (`seamMarksFor`) | `BIND NOT HELD` · `PROOF: POSITIVE` | Same status as the sentence: an observed absence (no instance) printed in the seam's strongest grade |

**Is it the truth?** In the OEM-killer case — the case Sarthak will
actually meet — **yes, fully**: grant gone, guard dead, OFF is the
fact and FIX opens the pane that owns it. In the stock case it is a
*transiently-true* print with no "binding…" state to distinguish it —
the same first-frame wound as A2's cold bind, arriving through a
second door. The cure neither created nor worsened this; it also did
not close it. It closes only if the unbound row gains a *bounded*
bind-pending silence keyed on "grant present in
`ENABLED_ACCESSIBILITY_SERVICES` but instance null" — a read the cure
does not currently take. Priced in B-Major-2.

OEM task-killer *without* force-stop (process kill, grant intact):
process death zeroes all in-memory state (`instance`, `lastRootMono`,
`residencyStartMono` are process-scoped by design); on relaunch this
is exactly the cold-start case the cure was built for, and it behaves
as designed.

### A4. Event drought — is "a quiet Deck generates no events" platform-true?

**Verdict: true in its narrow platform form, FALSE as a blanket claim
about this Deck — and the cure is right not to depend on either.**

- **Narrow form — TRUE.** Accessibility events are change reports, not
  a heartbeat. Per the `AccessibilityEvent` reference, an event is
  fired by a view when "something notable happens in the user
  interface" (a click, a focus change, a state transition). The
  platform defines no periodic/keepalive event. Monolith additionally
  subscribes to exactly two types —
  `typeWindowStateChanged|typeWindowContentChanged`
  (`accessibility_service_config.xml`, `notificationTimeout="80"`) —
  so on a genuinely unchanged window, with the user not touching
  anything, **nothing is dispatched and nothing can stamp**. The root
  cause's core claim survives.
- **Blanket form — FALSE for this Deck.** The Deck is not a static
  surface: `TactileDeckScreen` runs a 5 s `nowMonoTick`
  (`TactileDeckScreen.kt:369–376`), a minute-aligned clock effect,
  and state-driven recompositions. Any recomposition that changes a
  semantics-visible node (a clock digit, the sentence/seam text
  itself flipping, an animated value landing in the semantics tree)
  is exactly a `TYPE_WINDOW_CONTENT_CHANGED` candidate from
  Monolith's **own package** — and the event path stamps *before* its
  own-package early return (`MonolithAccessibilityService.kt:~512–525`),
  so a self-generated content change stamps the residency just as a
  foreign one does. Whether a given quiet minute actually produces
  such an event depends on which visible text changed — **UNVERIFIED**
  per-device, and deliberately so: it is nondeterministic (first tick
  that changes nothing visible may emit nothing; the minute flip
  likely does). That nondeterminism is the point: the pre-cure design
  rested LIVE on an event that was *likely eventually* but *never
  owed*, and the fog was the price on the mornings it didn't come.
  The probe converts an owed-nothing into an asked-now. Correct cure,
  correctly motivated — but the cell's record should stop saying
  "a quiet Deck generates no events" without the qualifier
  "no event is *owed*".

---

## PART B — HOSTILE DIFF REVIEW

Findings ranked. File:line references are to the branch tree at
`b13ca1a` unless a commit is named.

### BLOCKER

**B-Blocker-1 — SliceD's no-clock rows will fail CI. The known suspect
is CONFIRMED, and CI gates the APK on it.**
BUILD-2 made the single-arg `refusalSentenceFor(warrant)` delegate via
`SystemClock.elapsedRealtime()`
(`MonolithComponentLogic.kt:230–231`). `SystemClock` in a plain JVM
unit test is the mockable-android.jar stub: with no
`isReturnDefaultValues` anywhere in the Gradle config (verified:
`app/build.gradle.kts` sets only `isIncludeAndroidResources = true`),
the stub method **throws `RuntimeException("Stub!")`** — and it is
evaluated as the delegation *argument*, so it throws before the timed
overload is entered, even for `refusalSentenceFor(null)`.
`SliceDGestureAndStripTest.kt` has **no `@RunWith`** — both classes in
the file (`SlideGestureMathTest`, `StripDerivationsTest`) are plain
JUnit — and `StripDerivationsTest.causeTablePicksOneSentenceByCausalPriority`
still calls the no-clock overload **six times** (the not-bound row,
both usage rows, the UNSET-unstamped row, LIVE, and null;
`SliceDGestureAndStripTest.kt:185, 207, 212, 218, 222–223`).
BUILD-3 converted exactly **one** call in that test — the
UNPROVEN-unstamped row — to the timed overload and left the other six
exposed, while BUILD-3's own header in `ColdStartWarrantTest.kt`
states the reason this is fatal: *"off-device that clock is a stub,
so every sentence assertion here goes through the timed overload."*
The author knew the rule and applied it to one row of seven.
**CI impact is total, not cosmetic:** `.github/workflows/rescue-build.yml`
runs `./gradlew testDebugUnitTest` (step `tests`, ~line 90) and only
assembles/uploads the APK **after** its unit tests pass — one Stub!
throw in `StripDerivationsTest` fails the test task, fails the job,
and no APK ships from that run. The other two test files are safe:
`ColdStartWarrantTest`'s plain class uses the timed overload
exclusively (verified call by call), and its store half is
Robolectric (`ShadowSystemClock` present); `TruthCar3InterceptTest`
uses the timed overload for every sentence assertion.
**Priced fix (~10 minutes, test file only):** convert the six
remaining SliceD calls to the timed overload with an explicit fresh
`readMono`/`nowMono` pair, exactly as BUILD-3 did for the seventh —
or delete the no-clock overload's production existence and make the
tests the timed table's only callers. Do not "fix" it with
`isReturnDefaultValues = true`: that would silently turn every stub
in every plain test into a default value and mask real calls.

### MAJOR

**B-Major-1 — A probe in flight across `onUnbind` can resurrect the
stamp the unbind just killed (proof laundering across bindings).**
`probePipelineNow()` performs the binder query *first* and writes
`lastRootMono` *after* it returns (`MonolithAccessibilityService.kt:377–391`).
The query can take arbitrarily long (A1: up to the 5 s interaction
timeout). `onUnbind` zeroes `lastRootMono` precisely so "a rebound
service must never inherit its predecessor's proof"
(`MonolithAccessibilityService.kt:493–503`) — but **nothing cancels
the old instance's scope at unbind**: `scope.cancel()` happens only
in `onDestroy` (`:1013–1024`). So: probe launches on instance A →
system unbinds A (stamp zeroed) → A's still-running IO coroutine
returns a root and stamps `lastRootMono = now` *after* the zeroing →
system binds instance B, whose `onServiceConnected` sees a
`lastRootMono` it never earned. `deriveWarrant`'s `!a11yBound` leg
masks the zombie stamp while nothing is bound, so the warrant cannot
print LIVE off a dead service — that much holds — but the instant B
binds, `stampedThisResidency()` can read true off A's corpse proof,
defeating the anti-inheritance invariant the codebase states in
three places. The event path has the same write-after-query shape
but synchronously on the event thread, a far narrower window; the
probe's IO dispatch + up-to-5 s wait widens it by orders of
magnitude. **Priced fix:** capture a binding generation (or the
instance identity) at probe launch and write the stamp only if it
still matches at write time; and/or cancel the scope in `onUnbind`.
~10 lines. Until then, CRIT-1's C-1 acquires a mechanical edge: the
probe's stamp is not merely self-asserted, it is not even guaranteed
to belong to the binding it certifies.

**B-Major-2 — (Concurrence, independently verified) The unbound row
has no grace, so the cure's headline case still prints OFF at frame
one.** DAY-1's D-1 / FEEL-1's F-1 / CRIT-1's C-5 name it; my platform
half (A2/A3) confirms the mechanism from the other side: bind latency
is unbounded and `instance == null` at residency start is expected,
yet the not-bound row (`MonolithComponentLogic.kt:246–247`) prints
`BLOCKING IS OFF` with no silence window, and `deriveWarrant` maps
the same instant to `OFF` for the slide gate. The cure graced the
bound-unstamped row and left the *earlier* race — service not yet
bound at all — printing the strongest word in the table. **Priced
fix:** a bounded bind-pending silence for the unbound row *only when
the grant is present* (one `AccessibilityManager`
`getEnabledAccessibilityServiceList` / secure-settings read at
residency start, cached for the residency): grant-present-but-unbound
inside ~grace → silence; grant-absent → OFF immediately (that print
is observed truth and must never be graced). ~30–40 lines + tests.
Without it, "the fog cannot rest" remains false in its first-frame
form even after B-Blocker-1 is fixed.

### MINOR

**B-Minor-1 — Delayed-probe guard: correct, with two hairline races,
both fail-safe.** `noteResidencyStart` reads `scheduledStartMono`
*after* writing `residencyStartMono` (`WatchStore.kt:288`): two rapid
Deck entries can make the first entry's delayed probe carry the
second entry's stamp — it then fires (or not) on the second
residency's schedule; harmless duplicate/suppression, never a stamp
for a dead residency, because the guard re-checks equality at fire
time. Second: the fire-time check
(`residencyStartMono == scheduledStartMono && !stampedThisResidency()`)
and the subsequent `instance?.requestPipelineProbe()` are a
check-then-act pair — an unbind landing between them launches a probe
on a dying instance whose scope dies at `onDestroy`; the probe is
lost silently. It fails toward UNPROVEN (safe direction) and the
rebind probe in `onServiceConnected` covers the case. No fix required;
recorded so nobody "discovers" it later.

**B-Minor-2 — Residency never ends, so two cure behaviours are
process-lifetime, not residency-lifetime.** Nothing in production
clears `residencyStartMono` (only `resetForTest` does). Therefore
(a) `onServiceConnected`'s re-probe fires on *any* later rebind, even
with the user nowhere near the Deck — one wasted binder call, benign;
(b) every later `deriveWarrant` (including `standingWarrant()` for
non-Deck surfaces) carries the *first* Deck visit's start, so the
grace arithmetic for a hand-off warrant ages from a stale origin —
always in the conservative direction (older ⇒ less grace ⇒ OFF
sooner), never toward a false silence. Benign, but the comments say
"residency" where the code means "since first Deck entry this
process". One-line comment correction when B-Major-2 is touched.

**B-Minor-3 — BUILD-2 changed the no-clock overload's contract beyond
the fog row, silently.** Before `55bcae4`, the stale-print guard lived
only in the timed overload; the no-clock overload bypassed it. Now the
no-clock overload inherits the guard via delegation. No production
caller uses the no-clock overload (verified: the only production call
site, `TactileDeckScreen.kt:1175`, uses the timed overload with the
5 s ticker's `nowMono`), so the blast radius is tests and previews —
but it is a behaviour change in a shared function that the commit
message does not mention, and it is the same change that produced
B-Blocker-1. Record it; the fix for B-Blocker-1 should state the
contract deliberately.

**B-Minor-4 — Probe (and event path) never recycle the acquired
root node.** Each successful `rootInActiveWindow` hands the caller an
`AccessibilityNodeInfo` it owns; neither the probe nor the event
stamp recycles it. The probe adds at most ~3 nodes per residency —
immaterial next to the event path's per-event leak, which is
pre-existing and out of this cure's ownership. Noted for the record;
no action in this lane.

**B-Minor-5 — `probePipelineNow()` is a public synchronous blocking
call standing next to its safe wrapper.** Only `requestPipelineProbe()`
calls it today (verified by grep), and the wrapper is IO-dispatched.
But the function itself will block any future caller for up to the
interaction timeout (A1), and nothing in its signature or name says
so — its KDoc says what it proves, not what it costs. One-line KDoc
addition ("blocking binder call — call only via
`requestPipelineProbe()` or off the main thread") prices the trap
shut. (Same trap, pre-existing: the event path calls the getter
inline, but on the system's event thread, where the platform expects
it.)

### ACQUITTALS — clean, per file, where clean is true

- **`WatchStore.kt` (BUILD-1 parts) — concurrency-clean.**
  `residencyStartMono` and `lastRootMono` are both `@Volatile`;
  `stampedThisResidency()` is a pure read of two volatiles; the
  delayed probe re-validates under current state at fire time.
  **Double-stamp race with the event path: BENIGN, acquitted with
  reasoning** — both writers write `SystemClock.elapsedRealtime()`
  *at write time* (neither carries a pre-query timestamp across its
  async gap), so concurrent stamps are monotonic and idempotent; the
  last writer's value is by construction the freshest. The only
  cross-binding hole is B-Major-1's, and it is about *which binding*
  the proof belongs to, not about racing values.
- **`MonolithAccessibilityService.kt` (BUILD-1 parts) — threading
  clean, one leak-shaped exception.** Probe dispatch is IO, stamp
  write is a volatile store, `poke()` routes through the store's
  synchronized publish (no StateFlow clobber). Exception: the
  unbind-scope survival behind B-Major-1. Separately noted, not
  charged to BUILD-1: `onServiceConnected` restarts the event worker
  and session collector on the *same* scope every bind, and the scope
  is cancelled only at `onDestroy` — if the system ever rebinds the
  *same* instance (permitted by the Service lifecycle), workers
  accumulate. Pre-existing pattern, untouched by the cure; filing it
  here so it is on a record, not as a charge against this lane.
- **`MonolithComponentLogic.kt` (BUILD-2 parts) — table logic clean.**
  Row order does what the KDoc says: not-bound → DENIED → UNSET →
  UNPROVEN-unstamped (grace-aged from the warrant's *own* carried
  `residencyStartMono`, clamped at zero, `0` = age-unknown = no
  grace) → OFF → LIVE-silence. I do not repeat CRIT-1's C-3/C-6
  (seam contradiction, FIX-door semantics): they stand as filed by
  CRIT-1 and my read concurs, but they are truth-of-print charges,
  not mechanism defects, and duplicating them under my name would
  waste the cell's tokens. The defects that are mine to file about
  this file are B-Blocker-1's delegation and B-Minor-3's contract
  change.
- **`ColdStartWarrantTest.kt` / `TruthCar3InterceptTest.kt`
  (BUILD-3) — clean.** Timed overload used exclusively where the
  clock is a stub; Robolectric half properly separated
  (`@RunWith(RobolectricTestRunner)`, `sdk = [36]`), resets the
  singleton in `@Before`, and its header honestly names what is
  device-only. The grace boundary is pinned at T+4000/T+4001.
  BUILD-3's failure is not in its own files — it is the one SliceD
  row it touched and the six it didn't (B-Blocker-1).

### Bottom line for BINDUNG

The mechanism is sound and the platform use is correct: the probe is
the right call, on the right thread, at the right triggers, and no
listener the brief proposed would improve it. The lane nevertheless
cannot ship as-is: **B-Blocker-1 fails CI and with it the APK**,
B-Major-1 lets a dead binding's probe mint the next binding's proof,
and B-Major-2 leaves the first frame — the original wound's first
frame — printing OFF on a healthy phone. Fix order: Blocker-1
(minutes), Major-1 (~10 lines), Major-2 (~30–40 lines). After those,
my A-section stands as the platform record the cure can cite.

---

## Efficiency account — TECH-1

- **Artefacts:** this file (`studio/cells/issue-7-coldstart/tech-1.md`,
  the only file committed by this seat) + the signed summary comment
  on issue #7. Read-only on code honoured; no compiles run (verdicts
  above rest on diff/grep/CI-config evidence, and B-Blocker-1 is
  stated as a code-path certainty, not a run result — if VER-1 runs
  `testDebugUnitTest` and SliceD passes, I will stand corrected in
  public on #7, and the stub analysis says it will not pass).
- **Read in full:** SARTHAK-WORDS.md; all three build diffs
  (`1b641aa`, `55bcae4`, `b13ca1a`); `WatchStore.kt` residency +
  derivation + stale machinery; `MonolithAccessibilityService.kt`
  probe/event/unbind/destroy sections + service config XML;
  `MonolithComponentLogic.kt` cured table; Deck ticker + sentence
  call site (`TactileDeckScreen.kt`); CI workflow's test/assemble
  gating; `SliceD`/`ColdStart`/`TruthCar3` test call sites;
  RECORD.md + CRIT-1/DAY-1/FEEL-1 charges (concurrences marked as
  concurrences, never re-filed as discoveries).
- **Platform grounding:** official API reference
  (`AccessibilityService`, `AccessibilityManager`,
  `AccessibilityStateChangeListener`,
  `AccessibilityServicesStateChangeListener`, `AccessibilityEvent`)
  and AOSP source behaviour (`AccessibilityInteractionClient`'s
  synchronous-client/async-internal model + 5 s interaction timeout;
  stopped-state semantics). Where the platform gives no number
  (bind latency), I said UNVERIFIED instead of inventing one.
- **Waste admitted:** two broad web searches returned SEO noise
  before the queries were narrowed to the API reference and AOSP
  class docs; ~4 searches of budget. No agents spawned (depth cap,
  and none needed). No idle time: the SliceD/CI verification and the
  platform searches ran in parallel batches.
- **What I would do next, unassigned:** re-review the B-Major-1
  generation-guard fix and the B-Major-2 grant-present grace when
  they land — both are small enough to verify in one read — and
  device-verify the A4 qualifier (whether a truly untouched Deck
  minute produces a self-stamp) on Sarthak's phone class, since that
  single observation would let the cell replace "no event is owed"
  with a measured event rate.

— TECH-1
