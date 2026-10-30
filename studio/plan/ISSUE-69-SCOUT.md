# Issue #69 — performance-battery — SCOUT (Wave-3 Cluster C10, PRISMA)

**Seat:** SCOUT (performance/battery practice) · **Base:** `w3c10-base @ origin/main e29acdf` (per ISSUE-69-VERIFY.md)
**Input read first:** `ISSUE-69-VERIFY.md` — verdicts F1 HOLDS, F2 HOLDS (owned #42), F3 HOLDS, F4 HOLDS (owned #28), F5 PARTIAL (TactileDeck sub-claim FALSE). All adjustments below are against the verify bundle's §b-1…§b-5 fix specs, not against the raw audit.
**Method:** live web/GitHub sweep, 2026-10-30. Every source URL is copied verbatim from search-result URL lists. No code edits.
**Latency rule applied throughout:** anything that makes a block land slower is flagged **DECISION-NEEDED** and is never silently recommended (04:58 rule).

---

## Scout's cure adjustments — one line per finding

- **F1 (shield loop):** ADJUST §b-1 — keep the 2 s cadence (production blockers poll *faster*, 900 ms–1 s), but swap `queryUsageStats(INTERVAL_DAILY, 15 s)` for a rolling `queryEvents(lastQueryTime, now]` window with a retained last-foreground field, and let the OS render the notification clock via chronometer so `notify()` fires only on state change, not even once a minute.
- **F2 (accessibility pipeline):** ADJUST §b-2 — replace drop-if-busy coalescing with **cancel-and-replace** (`collectLatest`-style: the newest window always wins), and move the package/type/session early-exit **before** the synchronous `rootInActiveWindow` touch at `:173`; optionally restrict delivery system-side with a dynamically updated `packageNames` list.
- **F3 (duplicate tickers):** ADOPT §b-3, strengthened — the shield service is the SSOT writer because it is session-scoped and survives the UI; the VM should not merely stop *persisting*, it should derive its displayed elapsed purely from wall-clock anchors locally, and the fallback guard should key on shield-beat *freshness* (the existing gap-latch signal), not on a service `instance` reference.
- **F4 (catalog syncs):** ADJUST §b-4 — replace the pure 5-minute TTL with **event-invalidated caching** (`ACTION_PACKAGE_ADDED/REMOVED/CHANGED` receiver clears the stamp; TTL stays only as a backstop) plus a disk-cached label map, so a newly installed app appears instantly instead of up to 5 min late, at zero extra scans.
- **F5 (UI tickers):** ADOPT §b-5 for ShieldMatrix/TactileDeck; ADJUST the VM half — fixing the `_themeNow` stamp timing alone leaves `themeEvaluation` subscribed `Eagerly` for the VM's whole life; switch it to `SharingStarted.WhileSubscribed(5_000)` and collect UI state with `collectAsStateWithLifecycle()` so nothing ticks while no screen is visible.

---

## Ranked finds (best first)

### 1. Cancel-and-replace beats drop-if-busy in the accessibility pipeline — F2 · ADJUSTS §b-2(b)
- **Source:** https://ricardocosteira.dev/posts/2026/coroutines-need-your-cooperation/
- **Practice (one line):** Feed events into one collector using `collectLatest` (with `distinctUntilChanged()` in front) so a new event *cancels* the in-flight block and starts on the newest value, instead of launching an inner coroutine per event that cancellation can never reach.
- **Mapping:** **Adjusts** §b-2(b). Verify's drop-if-busy `pendingJob` can spend its one slot on a *stale* window while the genuinely hostile launch — the newest event — is the one dropped. Cancel-and-replace inverts that: stale work dies, newest window is always the one policed. Hard bound stays 1 in-flight pipeline.
- **Cost/risk:** Low–medium. Requires the pipeline body to be cancellation-cooperative (suspending Room reads are; the recursive tree walks need an `ensureActive()`/node-budget check per level — see find 6). **No latency cost — blocking responsiveness improves** (no queueing behind a stale job).

### 2. Early-exit before touching the window tree, and extract primitives on the callback thread — F2 · ADJUSTS §b-2(a)
- **Sources:** http://dev.to/weavrikx/getting-the-current-foreground-app-on-android-without-root-accessibilityservice-22jj and https://copyprogramming.com/howto/event-getsource-returns-null-accessibility-in-android
- **Practice (one line):** A production warn/block service's entire callback is: check event type → read `packageName` → drop own package → act; event/node objects are never handed to a background thread (their data goes stale) — primitives are extracted synchronously first.
- **Mapping:** **Adjusts** §b-2(a). Verify gates on `armedSessionId` before `scope.launch`, but the current code touches `rootInActiveWindow` synchronously at `:173` *before* any session check and then *re-reads* it inside the async job at `:197` (a race verify itself notes). Scout order: type check → package check → armed check → only then acquire root, once, inside the single-flight job; pass extracted primitives, never the event.
- **Cost/risk:** Low. Removes 1 of the 2 per-event root acquisitions and all no-session tree touches. **No latency cost** for blocked packages — `TYPE_WINDOW_STATE_CHANGED` is effectively instant.

### 3. Event-invalidated package cache beats a pure TTL — F4 · ADJUSTS §b-4
- **Source:** https://copyprogramming.com/howto/how-to-get-a-list-of-all-installed-apps-on-a-android-device
- **Practice (one line):** Cache the installed-app list and invalidate it with a `BroadcastReceiver` on `Intent.ACTION_PACKAGE_ADDED` / `ACTION_PACKAGE_REMOVED` (the published pattern pairs this with a short TTL backstop), instead of re-querying PackageManager on a schedule or per screen open.
- **Mapping:** **Adjusts** §b-4. Verify's 5-min TTL kills the ×2 cold-start and per-PARK rescans, but buys it with "new app appears ≤5 min late." A package-change receiver clears the sync stamp the moment an install/uninstall actually happens — same scan count in steady state, **zero staleness window**. TTL (longer, e.g. 30–60 min) remains only as a missed-broadcast backstop.
- **Cost/risk:** Low (~15 lines: one dynamically registered receiver in app/repository scope, `package` scheme data on the filter). Must handle `ACTION_PACKAGE_CHANGED` too (enable/disable state). No blocking-latency effect — catalog freshness only improves.

### 4. Let the OS render the notification clock; notify only on state change — F1 · ADJUSTS §b-1(2)
- **Sources:** https://dev.to/tryreclaim/racing-alarmmanager-with-a-coroutine-app-reminder-timers-on-android-3i08 and https://deepwiki.com/d4rken-org/capod/2.5-notification-system
- **Practice (one line):** `setUsesChronometer(true)` + `setChronometerCountDown(true)` + `setWhen(expiresAt)` makes SystemUI render the running countdown with *nothing ticking in your process*; a mature monitor app additionally pipes updates through `.throttleLatest(1000)` + `.distinctUntilChanged()` so notifications post only when the underlying state actually changed.
- **Mapping:** **Adjusts** §b-1(2). Verify's notify-on-subtitle-change already cuts 1,800 rebuilds/h to ~60/h (minute changes). Chronometer rendering cuts it to ~state-changes only (block events, break flips, session end) — the per-minute `notify()` disappears too.
- **Cost/risk:** Low–medium. The chronometer occupies the notification's `when` field, not the custom subtitle string — if the shield subtitle's exact text format (urges count + custom time format) is a design surface, keep verify's string-keyed notify-on-change for the subtitle and use chronometer only if the design accepts the system format. **Flagged as a design check for PRISMA, not a latency issue** — no blocking path is touched.

### 5. Rolling `queryEvents` window + retained last-foreground, at the cadence real blockers actually use — F1 · ADJUSTS §b-1 (detection half)
- **Sources:** https://dev.to/rexa/how-to-block-apps-on-android-without-an-accessibilityservice-5gog and https://dev.to/cajdata/yet-another-productivity-app-4opf
- **Practice (one line):** Production UsageStats blockers poll a foreground service every **900 ms–1 s** using `queryEvents(lastQueryTime, now]` — a rolling window advanced only after a successful read — and keep the last known foreground package in a field, overwriting it only when a `MOVE_TO_FOREGROUND` event actually arrives (an empty result means "nothing changed," never "no foreground app").
- **Mapping:** **Adjusts** §b-1's detection half, **adopts** its cadence conservatism. (a) Monolith's current `queryUsageStats(INTERVAL_DAILY, end−15_000)` + `lastTimeUsed ≥ end−8_000` filter is the fixed-window pattern the production write-up calls the single most common bug source in this approach; the rolling `queryEvents` window is cheaper per call and cannot drop late-landing events. (b) Cadence evidence cuts *against* slowing down: 900 ms–1 s is what shipped blockers accept paying; Monolith's 2 s is already the frugal end.
- **Cost/risk:** Low. Same 2 s period, one cheaper query, no extra wakeups. **⚠️ DECISION-NEEDED if anyone proposes the other direction:** some guides suggest polling UsageStats every 5–10 s (https://copyprogramming.com/howto/how-to-monitor-one-application-using-another-application-in-android-operating-system) — that is a 5–10 s slower block on the shield path and is **not** recommended here; verify's "do not slow this loop" stands, now with market evidence behind it.

### 6. Restrict accessibility delivery system-side (`packageNames`) + budget the tree walks — F2 · ADJUSTS §b-2 (new sub-item)
- **Sources:** https://deepwiki.com/duartebarbosadev/Scrolless/3-accessibility-service and https://docs.huihoo.com/android/4.4/training/accessibility/service.html and https://www.appbrain.com/app/blockit-%7C-stop-phone-addiction/com.hypenet.focused
- **Practice (one line):** Scrolless (open-source Shorts/Reels blocker) statically sets `packageNames` to only the 3 apps it polices and `notificationTimeout="100"` "to minimize battery impact"; Blockit's published accessibility disclosure states the pattern Monolith should match: package-name only, no screen content, **"the service does nothing when no session is running."**
- **Mapping:** **Adjusts/adds to** §b-2. Monolith's blocked set is dynamic, so the static-XML version doesn't fit — but `AccessibilityServiceInfo.packageNames` is settable at runtime via `setServiceInfo()` (per the platform training guide). On session arm / blocked-set change, set `packageNames` = blocked ∪ browsers ∪ Settings ∪ own/SystemUI exclusions; clear it (all packages) only if a flow genuinely needs global events. Events from every other app then never cross IPC into the process at all — stronger than any in-process gate. Independently: give both recursive walks (`collectWindowTextLowercase` depth-14, `isSelectedBottomTabShortsOrReels` *unbounded*) a shared node-count budget (e.g. 500 nodes) + early-exit-on-match, and prefer `findAccessibilityNodeInfosByViewId/Text` over manual recursion where the target is known.
- **Cost/risk:** Medium. Dynamic `setServiceInfo` reconfiguration must be tested for an event gap during re-registration; the fallback is verify's in-process gate alone (finds 1–2), which already captures most of the win. **⚠️ DECISION-NEEDED — do NOT drop `TYPE_WINDOW_CONTENT_CHANGED` from the config** (one published warn-service recommends window-state-only because content-changed "fires on every content update — your battery graph will look like a ski slope"): Monolith needs content events for browser-site and Shorts/Reels detection, so dropping the type would silently kill those blocks. The lawful version is in-code: content-changed events from packages outside the content-inspection set return after the cheap package check, before any root acquisition (find 2).

### 7. SSOT: the service owns the persisted clock; the ViewModel computes display time locally — F3 · ADOPTS + STRENGTHENS §b-3
- **Sources:** https://guides.codepath.org/android/Architecture-of-Android-Apps and https://kompas.wasmer.app/https_developer_android_com/topic/architecture
- **Practice (one line):** Single source of truth: each data type has exactly one authoritative owner and *only that owner may mutate it*; everyone else observes it or derives read-only values from it.
- **Mapping:** **Adopts** §b-3 and answers the ownership question directly: **the shield service owns the clock.** It is the session-scoped component (starts/stops with the session, survives Activity/ViewModel death); a ViewModel is UI-scoped and is the wrong owner for persisted session time. Strengthening: the VM's 1 s tick shouldn't call the repository *at all* for progress — displayed elapsed is a pure function of stored anchors (start, accumulated, break state) and `now`, computable locally per tick at zero DB cost; persistence happens only in the shield. Also key verify's fallback guard on **shield-beat freshness** (`latchShieldGapIfStale` already maintains exactly that signal) rather than `MonolithShieldService.instance == null`, which can lie across process death.
- **Cost/risk:** Low. No new mechanism; formalises the shield header's own "only lawful writer" comment verify cites. No latency effect.

### 8. `WhileSubscribed` + lifecycle-aware collection, not just a slower stamp — F5 · ADJUSTS §b-5 (VM half)
- **Sources:** https://projectech.in/guides/kotlin-flow-stateflow-android-guide/ and https://www.irurueta.com/apps/android/compose-state-and-side-effects.html and https://copyprogramming.com/howto/kotlin-timer-task-in-android-kotlin
- **Practice (one line):** Collect UI flows with `repeatOnLifecycle(STARTED)` / `collectAsStateWithLifecycle()` and share upstream with `SharingStarted.WhileSubscribed(5_000)` so collection *and* upstream work stop when the screen isn't visible; a lifecycle-scoped timer (`repeatOnLifecycle(RESUMED) { while(isActive){ …; delay(1000) } }`) runs only while the UI is actually on screen.
- **Mapping:** **Adjusts** §b-5's VM citation for #28. Verify targets the unconditional 1 s `_themeNow` stamp; but `themeEvaluation` is `stateIn(…, Eagerly, …)` — even a boundary-aligned stamp leaves the combine subscribed and re-deriving for the VM's entire life whenever anything upstream emits. `WhileSubscribed(5_000)` + lifecycle-aware collection is the complete cure: no visible screen ⇒ no upstream, no tick, no recomposition.
- **Cost/risk:** Low. 5 s resubscribe grace absorbs rotations. One check for #28: confirm no *non-UI* consumer (shield, widget) collects `themeEvaluation` — if one does, it needs its own subscription, not an Eagerly UI flow. No blocking-latency effect (pure UI theming).

### 9. Disk-cached launcher catalog for instant cold start; decouple the usage-minutes query — F4 · ADJUSTS §b-4 (second half)
- **Sources:** https://github.com/C-P-S-P/focus-launcher and https://deepwiki.com/takke/cpustats/3.4-inter-process-communication
- **Practice (one line):** The open-source Focus launcher's `AppRepository` is documented as "LauncherApps scan, labels, categories, **disk cache for instant cold start**"; separately, mature monitors broadcast updates *only when `isUpdated()` says the data changed*, skipping IPC/UI work entirely on identical snapshots.
- **Mapping:** **Adjusts** §b-4. Verify splits seed vs package halves and TTLs the expensive half; scout adds: persist the reconciled label/icon-light catalog to disk so cold start renders from cache and reconciles in the background (change-detected, find 3's receiver decides *whether*, the disk cache decides *how fast*), and split `queryRealTodayUsageMinutes()` (12 h UsageStats window) out of the catalog sync entirely — usage minutes are needed by usage surfaces, not by catalog reconciliation, and currently ride along on every sync.
- **Cost/risk:** Low–medium (one small serialized cache + invalidation hook shared with find 3). Stale-label risk is bounded by the same receiver invalidation. No latency effect.

### 10. Screen-off is free battery — but pausing the shield loop is a beat-law decision — F1/F5 · DECISION-NEEDED, NOT RECOMMENDED AS-IS
- **Sources:** https://deepwiki.com/dubrowgn/wattz/3.2-notification-and-status-bar-updates and https://copyprogramming.com/howto/listening-for-action-screen-off
- **Practice (one line):** Wattz's status service stops its periodic task on `ACTION_SCREEN_OFF` and resumes on `ACTION_SCREEN_ON` "to conserve battery when the screen is off"; the general platform guidance is the same — halt unnecessary periodic work while non-interactive, since no app can be used on a dark screen.
- **Mapping:** **Conflicts with** §b-1's preserved invariant, flagged honestly: the shield loop's 2 s iteration also stamps the shield beat that the 10 s shield-gap law (`SHIELD_GAP_THRESHOLD_MILLIS`) reads. Pausing the whole loop screen-off ⇒ beat goes stale ⇒ gap latch fires on every screen-off — a false "shield died" record. A lawful variant exists (split the loop: beat-only iteration screen-off, full detection/notification work screen-on), but it restructures the loop verify deliberately kept intact.
- **Cost/risk:** **⚠️ DECISION-NEEDED.** Saving is real (all detection/DB/UsageStats work during screen-off session hours) but it touches the gap-evidence law; blocking responsiveness on wake also depends on `SCREEN_ON` delivery preceding the user's first app launch (in practice it does, broadcast is synchronous with wake — but that claim needs a device test, not scout's word). Do not fold into §b-1 silently; PRISMA/Sarthak call.

### 11. Notification-update throttling as a pattern library (supporting evidence) — F1 · ADOPTS §b-1(2)
- **Source:** https://deepwiki.com/d4rken-org/capod/2.5-notification-system
- **Practice (one line):** Capod's monitor worker applies `.throttleLatest(1000)` + `.distinctUntilChanged()` to its notification flow — deduplication first (identical state never posts), throttle second (bursts collapse to the latest).
- **Mapping:** **Adopts/supports** §b-1(2)'s `lastNotifiedSubtitle` string-key design with a shipped-app precedent, and gives PRISMA the idiomatic Flow-shaped implementation if the loop is ever refactored onto a state flow. Find 4 (chronometer) supersedes it where the design allows.
- **Cost/risk:** Negligible. A 1 s throttleLatest on *block-event* notifications would delay a "blocked" subtitle by ≤1 s — cosmetic only (the block itself is the accessibility/shield overlay, not the notification), but keep block-count changes on the distinct-change fast path, unthrottled, as verify's full-string key already does.

### 12. What NOT to import (negative finds — rejected practices)
- **Debounce the accessibility pipeline by 300 ms** (a hardware-key guide's pattern, https://copyprogramming.com/howto/is-it-possible-to-create-an-android-service-that-listens-for-hardware-key-presses): **⚠️ DECISION-NEEDED / rejected by scout** — a 300 ms debounce is a 300 ms later block on every hostile launch; Monolith's flood problem is *duplicate work*, solved by single-flight (find 1), not by waiting. Debounce is for search boxes and key repeats, not for a wall.
- **Raise `notificationTimeout` well past 100 ms to thin events:** Scrolless and the platform training guide both sit at 100 ms; Monolith's 80 ms is already in family. Raising it delays the first event of a burst — **⚠️ DECISION-NEEDED**, no recommendation; the cure is pipeline-side (finds 1–2, 6), exactly as verify ruled.
- **WorkManager/JobScheduler for any of these loops:** minimum periodic interval is 15 min and delivery is Doze-deferred — lawful for catalog hygiene, absurd for session progress or blocking. No finding maps to it; listed only so nobody "modernises" a 2 s shield loop into a 15-min job.

---

## DECISION-NEEDED register (slower-block / law-touching items — none silently recommended)

| # | Proposal | Trade | Scout position |
|---|---|---|---|
| D1 | Slow shield UsageStats poll to 5–10 s (generic-guide advice) | Battery ↓, block up to 10 s slower | Reject; production blockers run 0.9–1 s, Monolith already at 2 s |
| D2 | Pause shield loop while screen off | Battery ↓↓, touches 10 s shield-gap beat law + wake-latency needs device proof | PRISMA/Sarthak call; beat-only split variant exists (find 10) |
| D3 | Drop `TYPE_WINDOW_CONTENT_CHANGED` from a11y config | Event flood ↓↓, browser-site + Shorts/Reels blocks die | Reject as stated; in-code package gate instead (find 6) |
| D4 | Debounce a11y pipeline ~300 ms / raise `notificationTimeout` ≫100 ms | Fewer runs, every block lands later | Reject; single-flight instead (finds 1, 12) |
| D5 | Chronometer-rendered notification clock | `notify()` → ~zero, custom subtitle time format lost | Design call for PRISMA (find 4), no latency stake |

*Scout complete: 12 finds (11 positive + 1 negative-find entry), every URL verbatim from search results, every latency trade flagged DECISION-NEEDED. Read-only: no code edits, no issue writes, no messages to Sarthak.*
