# ISSUE #11 — TECHNICAL EXECUTION (Riegel)

**Seat:** Riegel, technical-execution specialist, dedicated-100 programme.
**Scope:** the machinery UNDER the Held Door — how the intercept that raises
this door actually works on Android, what it costs, and what silently breaks
it. Paper evidence only (file + line, issue-11 branch tip); nobody compiled.
**Posture:** synchronous — this file lands while the cell works, not after.

---

## 1. THE DOOR'S RAISE PATH — there are two doors, and they are not equal

The Held Door is raised by two independent mechanisms, with different
latencies, different failure modes, and different truth values:

| Door | Mechanism | Detection latency | Source |
|---|---|---|---|
| Accessibility | `MonolithAccessibilityService.onAccessibilityEvent` → per-event coroutine → Room snapshots → `startActivity(MainActivity, SINGLE_TOP)` | event-driven, ~50–500 ms typical (DB round-trips dominate) | `service/MonolithAccessibilityService.kt` |
| Shield (UsageStats) | `MonolithShieldService` FGS, 2 s polling loop → `queryForegroundBlockedApp` → same intent | 2 s loop grain + UsageStats staleness (see §3) — worst case ~10 s | `service/MonolithShieldService.kt:88–201`, `data/MonolithRepository.kt:575–591` |

Consequence the door's design must keep facing: **the door is never
instant.** A fast thumb sees the blocked app for a visible beat before the
door lands. Any print on the door that implies the block was instantaneous
is false; the door's honesty is that it arrives, not that it pre-empts.

## 2. ACCESSIBILITY SERVICE — technique, price, silent breaks

**Technique (as built).** Config: `typeWindowStateChanged |
typeWindowContentChanged`, `notificationTimeout=80`,
`flagReportViewIds | flagIncludeNotImportantViews`,
`canRetrieveWindowContent=true` (`res/xml/accessibility_service_config.xml`).
Every event first acquires `rootInActiveWindow` for the Live Wire stamp
(stamp-before-return), then — for any non-self, non-SystemUI package —
launches an IO coroutine that reads, per event: active-session snapshot,
guard-settings snapshot, the full apps snapshot (twice on some paths:
lease row + tier row). Intercept order inside the coroutine: call bypass
(`AudioManager.mode`) → tamper-settings/installer guard → Shorts/Reels
sniper (view-id probe + unbounded recursive selected-tab walk) → browser
URL-bar probe (5 hardcoded view ids, focused-typing exempt) → per-app tier
(rule, Safe-Break rule, lease rule).

**Price.**
- `TYPE_WINDOW_CONTENT_CHANGED` fires at UI-mutation rate. During scroll
  or animation in a target app that is dozens of events/sec; each one that
  passes the package filter costs a coroutine + 3–4 Room reads + tree
  walks. `notificationTimeout=80` throttles per-package delivery but does
  not make the per-event work cheap. This is a standing CPU/battery tax
  for the whole session, proportional to how alive the foreground app is.
- The sniper's tab walk (`isSelectedBottomTabShortsOrReels`) recurses with
  **no depth cap** (the settings-text walk caps at 14; this one doesn't).
  Pathological trees (webview-heavy Instagram surfaces) make single events
  expensive. One-line cure, not taken here (not my cell's code to change
  silently): cap depth at ~14 like its sibling.

**What silently breaks it.**
- **OEM process management.** MIUI/One UI/ColorOS battery savers kill or
  freeze the app process; the accessibility *binding* usually survives,
  but event delivery stalls until the system rebinds. The Live Wire
  (`lastRootMono`) detects a dead pipeline — it does not revive it.
- **Force-stop / some OEM updates** disable the accessibility grant
  outright; Android will not re-enable it programmatically. Until the
  user re-toggles it by hand, this door does not exist and only §3's
  slower door remains. The app's truth surfaces must say so (Watch Store
  does; the door itself never gets the chance).
- **Root acquisition races.** `rootInActiveWindow` is null during window
  transitions — exactly the moment apps open. Sniper and site doors skip
  silently on a null root (acquitted-by-absence); the tier door still
  fires because it needs only the package name. Shorts can therefore be
  reachable for the first beats of a session inside YouTube until a
  content-changed event lands with a live root.
- **The tamper guard is text matching.** Lowercased English strings
  ("force stop", "uninstall", "clear storage"…) + "monolith" in the
  Settings window tree. It silently stops working under: a non-English
  system locale, an OEM Settings redesign that paginates app info
  differently, or split-screen (root = the other window). It is friction,
  not a wall — and its own badge ("Uninstall Guard") should never be
  read as more than that. Device admin (§4) is the same grade of friction.
- **Event flood → duplicate doors.** Two events in flight launch the
  intercept intent twice; `SINGLE_TOP` absorbs the second at the Activity,
  but the door's arrival seam (FIX-7, 180 ms specA entry, offer-arming
  nonce) depends on `onNewIntent` discipline (FIX-4) holding under that
  flood. That is the seam to re-prove on the phone, not on paper.

## 3. SHIELD SERVICE + USAGE STATS — the slow door is the honest backstop

**Technique.** FGS (`FOREGROUND_SERVICE_TYPE_SPECIAL_USE` on API 34+),
`START_STICKY`, a 2 s loop that each iteration: reconciles routines every
15th pass, snapshots the session, **writes session progress to Room every
iteration** (`updateSessionProgress(..., stampShieldBeat = true)`),
rebuilds the blocked set from the full apps snapshot (lease- and
break-aware), then asks UsageStats: `queryUsageStats(INTERVAL_DAILY,
now−15 s, now)`, filtered to blocked packages with `lastTimeUsed ≥
now−8 s`, max wins.

**Price — the most expensive machinery in the app.**
- A Room write every 2 s for the session's whole life (a 12 h study day
  = ~21,600 writes) plus a UsageStats query every 2 s. The FGS keeps the
  process hot and defeats doze batching for itself. Battery cost is real
  and **UNMEASURED on-device** — no `batterystats` bench exists in this
  repo. Until one does, any percentage anyone quotes for it is invented.
  Bench owed: `adb shell dumpsys batterystats` over one full SLEEP +
  one full DAY span, shield on vs off.
- Detection is *retrospective*: UsageStats `lastTimeUsed` at
  `INTERVAL_DAILY` granularity is bucketed and can lag real foreground
  changes by seconds. Combined worst case (loop phase + stats lag +
  the 8 s recency filter) means a sub-10-second dip into a blocked app
  can begin and end between proofs. The accessibility door covers most
  of that gap; where both are degraded (a11y killed by OEM + stats lag),
  short dips are simply not caught. Name it, don't paper it.

**Silent breaks.**
- **Usage-access revoked / never granted:** `queryForegroundBlockedApp`
  returns null forever (`hasUsageStatsPermission()` gate) — the shield
  loop still runs, still stamps its beat, still *looks* alive. The beat
  stamp proves the loop ran, not that it could see. (Watch Store's
  usage-access item is the truth surface for this; the shield itself
  cannot distinguish "nothing blocked" from "I am blind".)
- **The 8 s recency window cuts both ways:** after the door intercepts,
  the blocked app's `lastTimeUsed` stays inside the window for several
  more iterations. If the door activity is dismissed by any path other
  than its own verbs, the shield can re-raise the door against an app
  the user already left — a door for nobody. Verify on phone: intercept
  → BACK/home → watch for a phantom re-raise within ~8 s.
- **FGS start restrictions:** `startForegroundService` from the boot
  receiver is on Android 12+'s exemption list, but OEM builds have
  shipped that ignore the exemption; a failed start throws inside
  `startShield`'s try/catch and is **swallowed silently** ("Safe ignore
  in test environments"). On a real phone that catch is where protection
  goes to die without a sound. The cascade witness
  (`protecting`/`protectingMono`) is the detection; nothing currently
  *acts* on its absence at the door layer.

## 4. DEVICE ADMIN — what it is, and what it is not

`MonolithDeviceAdminReceiver` declares exactly one policy:
`<limit-password/>`. `onDisableRequested` returns a warning string.
That is the entire mechanism. It does **not** prevent uninstall (the
user reads the warning, confirms, admin drops, uninstall proceeds —
three taps), it is not "kernel-level" (the class doc says so; the doc
is wrong and should be corrected wherever the claim is repeated), and
Android has deprecated device-admin for this use class. Its honest
value: (a) friction at the exact moment of impulse, (b) the
Watch Store grant stamps in `onEnabled`/`onDisabled`. Price: near zero
at runtime. Silent break: none needed — it breaks *loudly* by design
(the disable flow is a system dialog); what would be silent is anyone
believing it holds. It doesn't. The session's real uninstall defence
is the accessibility tamper guard (§2) — which is text matching. Both
layers are friction. The truthful sentence is: **a determined user can
uninstall Monolith mid-session in under a minute, and no mechanism in
this app can lawfully prevent that.** The design already knows this
(the lock's strength is the 60 s emergency-break hold and the token
ledger, not the uninstall path) — the paperwork should stop implying
otherwise.

## 5. THE TWO-MINUTE PEEK LEASE — monotonic, persisted, and one reboot wide open

**Technique.** Grant (VM `start2MinutePeek`, re-derived through
`peekOfferFor` — path, tier, cooldown, pill, session liveness) →
`MonolithRepository.startPeekLease`: Room row gets
`activePeekUntilElapsedRealtime = elapsedRealtime() + 120_000` and
`lastPeekEndElapsedRealtime` pre-armed to the same instant, so the
20 min cooldown (`PEEK_COOLDOWN_MILLIS`) is anchored even if the lease
is never observed ending. Enforcement reads the same stamp in three
places: accessibility tier door, accessibility lease-silencer for the
sniper/site doors (UNA2-01), and the shield's blocked-set filter.
Expiry enforcement is *passive*: nobody pushes the user out at T+120 s;
the shield's next iteration (≤2 s + stats lag) re-intercepts, and the
accessibility door fires on the next window event. A user sitting
perfectly still in a leased app, reading, is re-intercepted by the
shield path only.

**The silent break nobody had named — REBOOT.** Both lease stamps are
`SystemClock.elapsedRealtime()` values (monotonic, zeroed at boot)
**persisted in Room**. Nothing at boot, session start, or anywhere else
resets them (grep: no writer zeroes `activePeekUntilElapsedRealtime`
outside grant/end paths; the boot receiver stamps wall-clock
`lastBootAtMillis` but never touches the peek stamps). After a
mid-session reboot, uptime restarts near 0 while the stored end stamp
still holds pre-reboot uptime — if the phone had been up 6 h when the
lease was granted, `nowMono < activeUntil` stays true for up to ~6 h
of new uptime: **PEEK apps are effectively OPEN for hours inside a live
session**, and symmetrically a pre-reboot cooldown anchor can suppress
legitimate offers for just as long. Probability: low (needs a reboot
inside the lease/cooldown window). Impact: total for that window, and
*silent* — every surface derives from the same poisoned stamp, so
every surface agrees on the lie. Cure (priced, not implemented — this
is a machinery change for Ren's merge): at boot-receiver tick and at
session arm, if `elapsedRealtime() < stored stamp` sanity fails
(stamp > current uptime + lease max), zero both peek stamps for all
rows — a lease never survives a reboot; that is also the *honest
product* behaviour (a reboot ends a peek, full stop). ~15 LOC in the
repository + one pure-function test. Filed as R-11-1 below.

**Other lease failure modes.**
- **Grant is VM-gated, repository-naked:** `startPeekLease` itself
  performs no offer re-derivation; any future caller that skips
  `peekOfferFor` sells leases the doors can't honour. The law currently
  holds by there being exactly one caller (VM:2818). Keep it that way
  or move the derivation into the repository.
- **Process death mid-lease is safe** (stamps persisted) — the one
  death this machinery survives cleanly; say so.
- **Deep sleep does not pause the lease:** `elapsedRealtime` includes
  sleep. A peek granted, phone pocketed, is a peek spent. Intended —
  but the pill's printed countdown deriving from the same anchor is
  what makes it honest.

## 6. THE #11 ITEMS THEMSELVES — mechanism under the fixes

**RETURN's ~55 dp jump at countdown 0 (the headline defect).** Root
mechanism: conditional composition — the peek zone's height varied by
frame state and `spacedBy` collapsed around absent children, so
RETURN's address was a *consequence* of countdown state. The cure in
tree is the right shape and I verify its mechanics: fixed skeleton
(Z = 48 + 6 + 34 + 4 + 18 = 110 dp in every frame; card bottom-anchored;
RETURN centre fixed 136 dp above the card's bottom edge; no `spacedBy`
in the card column — `AirlockAndDisarmOverlays.kt:674–692, 911–921`).
Tenants crossfade *inside* fixed bounds, so countdown state can change
contents, never geometry. Runtime price: zero. Residual risks, priced:
(a) any future tenant whose intrinsic content exceeds its fixed slot
clips silently instead of pushing — the failure mode moves from
"jumps" to "truncates", and only the stationarity tests
(`HeldDoorStationarityTest`, wave-2's variable-content frame) stand
guard; keep them non-negotiable in CI. (b) The 110 dp zone is
*absent* (0 dp) on non-peek-owing doors — lawful per the skeleton law
as filed (RETURN's address is defined relative to the card's bottom
edge, and the card grows upward), but any future surface that anchors
to the card's *top* reintroduces the jump on that surface.

**Countdown-at-0 seam (wave-2 remainder — my seat flagged it; I close
the technical half here).** Printed seconds are derived per frame from
anchored monotonic ends (`derivedSecondsUntil`, ceiling form,
`AirlockAndDisarmOverlays.kt:363`). At derived-0 the tenant must swap
(counting → offer/RESOLVING) via an overlay→VM refresh contract: the
overlay derives 0, reports, the VM re-derives the offer and re-emits.
The seam's real latency is therefore: redraw tick (50 ms after the
wave-2 cure) + VM emission + recomposition. On paper the contract is
correct; the untested part is that no test renders the derived-0 frame
and asserts the swap *happens* rather than the door sitting on a dead
"0" tenant if the VM emission is conflated away by StateFlow equality
(an emission with an equal state value is dropped — if the refresh
produces a state equal to the previous one, the door never learns).
That — not the arithmetic — is the failure mode to test. Price of the
test as filed stands (~1 frame + pure-derivation extraction); I add:
the test must drive the VM, not just the composable, or it proves the
wrong half.

**Gate drain at 50 ms.** The overlay redraw loop now ticks `delay(50L)`
(20 fps recomposition of one overlay while the door stands). Cost:
negligible next to §3's loop; bounded by the door's visibility. No
machinery concern. The printed seconds remain anchor-derived, so the
finer grain changed smoothness only — correct separation.

**Emission-bound session liveness (FIX-5d bounded residual — priced).**
The offer's liveness conjunct reads stored row state refreshed by the
shield heartbeat. If the shield is dead (§3's blind case) and the
session row is stale, the door can offer a peek on a session that has
ended in wall-clock truth but not in row truth. Worst case is bounded
by the grant's own re-derivation — which reads the *same* rows. The
residual is real and correctly recorded by Tor's cell; the technical
floor under it: liveness ultimately rests on the shield loop or the
ticker emitting. A wall-clock cross-check at grant time
(`session.endWallMillis` vs `System.currentTimeMillis()`, where the
row carries one) would close most of it for ~5 LOC — offered as
R-11-2, Ren's call.

## 7. FINDINGS FILED

- **R-11-1 (NEW, silent break):** peek lease + cooldown monotonic
  stamps survive reboot unreset → PEEK tier effectively open (or
  offers suppressed) for up to pre-reboot-uptime after a mid-session
  reboot. Cure ~15 LOC + 1 test. §5.
- **R-11-2 (offer):** wall-clock cross-check at grant time to bound
  the FIX-5d emission-bound liveness residual. ~5 LOC. §6.
- **R-11-3 (hygiene):** sniper tab-walk recursion is uncapped while
  its sibling walk caps at 14. One-line parity fix. §2.
- **R-11-4 (truth-in-docs):** "kernel-level uninstall guard" claim in
  `MonolithDeviceAdminReceiver`'s doc is false; the mechanism is a
  warning dialog. Correct the doc wherever repeated. §4.
- **Bench owed (no owner yet):** shield-loop battery cost,
  `batterystats`, one SLEEP + one DAY span. §3.

## 8. WHAT I DID NOT VERIFY

Everything above is code-reading. Intercept latency under OEM battery
management, UsageStats lag on Sarthak's actual device, the phantom
re-raise (§3), and the reboot hole's live behaviour (R-11-1) are
phone facts; they are stated as mechanisms with the test that would
settle each, not as observations. Nobody compiled; Ren compiles on
Sarthak's word.

— Riegel, technical execution
