# Scout Finds 8 — Android Blocking Technique

**Best serves: GitHub Issue #7 (Shield)** — every find below is about whether the shield is actually up, fires on time, and survives tampering. Two finds (exact alarms, OEM survival) secondarily feed routine firing, but the centre of gravity is the Shield.

Scout: Mauer — dedicated ground: accessibility-service blocking craft, UsageStats, anti-uninstall / anti-bypass, Device Owner, exact-alarm reliability, boot persistence, OEM killing.

---

## 1. Accessibility is the ONLY real-time detector — UsageStats is a 10-minute-late witness

**Technique:** Hybrid detection: `AccessibilityService` `TYPE_WINDOW_STATE_CHANGED` as the trigger, `UsageStatsManager.queryEvents()` as the corroborator / fallback — never the other way round.

**Sources:**
- https://dev.to/weavrikx/getting-the-current-foreground-app-on-android-without-root-accessibilityservice-22jj
- https://pub.dev/packages/app_blocker/versions/1.0.0 (working cross-platform blocker: foreground service + UsageStats detection + `TYPE_APPLICATION_OVERLAY` + AlarmManager + `RECEIVE_BOOT_COMPLETED`)

**What makes it elite:** The comparison table is brutal and measured: `getRunningTasks()` returns only your own task since Android 5, `getRunningAppProcesses()` returns only your own process since Android 8, and UsageStats latency is **up to ~10 minutes** — useless for catching an app launch. Only `TYPE_WINDOW_STATE_CHANGED` fires the instant the window changes. The write-up's four field-tested pitfalls are the real gold:
1. Listen to `TYPE_WINDOW_STATE_CHANGED`, **not** `TYPE_WINDOW_CONTENT_CHANGED` (the latter fires on every content update — battery graph "looks like a ski slope").
2. Filter out your own package name, or your own block overlay triggers an infinite loop.
3. Throttle / cooldown after each intervention, or you spam the user into uninstalling.
4. Use `TYPE_ACCESSIBILITY_OVERLAY` for the block window where possible — it comes with the accessibility grant, no separate `SYSTEM_ALERT_WINDOW` permission flow to lose.

**Applies to Monolith:** Monolith already has the right two pieces (accessibility + UsageStats ShieldService) — the find is about *roles*. Audit the shield so accessibility is the sole launch trigger and UsageStats is only (a) fallback when accessibility is dead, and (b) the source for usage totals. If any blocking decision currently waits on a UsageStats poll, a fast user gets seconds-to-minutes inside a blocked app before the shield lands. Also check the self-package filter and the event-type mask in the accessibility config XML — both are one-line bugs that produce either a loop or a dead shield.

---

## 2. The 11-layer OEM survival stack — one foreground service is nowhere near enough

**Technique/product:** "How Are You?!" (126k lines of Kotlin, 125+ versions, solo dev, 24/7 safety monitoring) — an 11-layer recovery architecture built because stock Android guidance failed on every real OEM.

**Source:** https://dev.to/stoyan_minchev/what-android-oems-do-to-background-apps-and-the-11-layers-i-built-to-survive-it-28bb

**What makes it elite:** This is the most concrete anti-kill engineering write-up in the wild, with named OEM behaviours most developers never learn until users churn:
- **Samsung:** "Sleeping Apps" list kills you after ~3 days with no foreground activity; **OTA updates silently reset your battery-optimisation exemption**.
- **Xiaomi/MIUI/HyperOS:** aggressively kills services and resets autostart permission after OTAs.
- **Honor/Huawei PowerGenie:** flags you as "frequently wakes your system" if you call `setAlarmClock()` more than ~3×/day; HwPFWService kills apps holding wakelocks >60 min with a non-whitelisted tag (his fix: use the whitelisted tag `LocationManagerService` on Huawei/Honor, real tag elsewhere).
- **OPPO/Vivo:** sleep-standby / AI sleep mode freezes apps exactly during sleeping hours — i.e. exactly when Monolith's SLEEP routine matters.

His layers that Monolith does not obviously have: (2) schedule recovery **inside `onDestroy()`** (a ~50 ms window to re-arm everything); (3) a **self-chaining `setExactAndAllowWhileIdle()` watchdog** whose interval adapts to power state (15 min active / 30 idle / 60 deep sleep — frequent alarms get OEM-flagged); (4) a WorkManager periodic watchdog — with the trap that `ExistingPeriodicWorkPolicy.KEEP` silently discards re-schedules and `REPLACE` resets the countdown, so you must query `getWorkInfosForUniqueWork()` first; (5) boot recovery on `BOOT_COMPLETED` **+ `LOCKED_BOOT_COMPLETED` + `QUICKBOOT_POWERON` (HTC) + `MY_PACKAGE_REPLACED`**; (7) an 8-hour `setAlarmClock()` safety net (~3/day, under PowerGenie's radar, highest delivery priority in Android); (8) a receiver for `ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED` (see Find 4); (11) **user-facing gap detection** — when all layers fail, detect the silence and show OEM-specific instructions ("Settings > Battery > [your OEM's path]") instead of pretending to be armed. Also: notification channel at `IMPORTANCE_MIN` so the foreground-service notification stays silent while keeping process priority.

**Applies to Monolith:** This is a direct checklist for the Shield's TELL-THE-TRUTH standard. Monolith should (a) adopt the adaptive watchdog chain + WorkManager watchdog as two independent resurrection paths, (b) add `MY_PACKAGE_REPLACED` and `LOCKED_BOOT_COMPLETED` to the boot receiver — an app update currently risks a silently disarmed shield, (c) implement gap detection: if the shield service was supposed to be alive and no heartbeat landed for X minutes, the app must *say so* on next open with the device-specific fix path, never show a green "armed" state. That last layer is the difference between a truthful blocker and a decorative one.

---

## 3. "CAN'T CHECK BLOCKING" on cold reopen — it's a binding race, and the check itself is the wrong question

**Technique:** Three-state shield status (GRANTED-in-Settings / BOUND-alive / UNKNOWN-yet) instead of a single boolean check at `onCreate`.

**Sources:**
- https://copyprogramming.com/howto/how-to-detect-accessibility-settings-on-android-is-enabled-disabled
- https://llcloud.app/guides/stop-xiaomi-killing-apps/ (Xiaomi section: "Why does my Xiaomi keep switching off an app's accessibility? The app was force-stopped, and Android turns accessibility off on a force-stop.")

**What makes it elite:** The standard check everyone copies — parse `Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES` or call `AccessibilityManager.getEnabledAccessibilityServiceList(FEEDBACK_ALL_MASK)` and match package/class — answers *"did the user grant it?"*, **not** *"is the service bound and receiving events right now?"*. On a cold start those two diverge: the Settings string is populated before the system has re-bound the service to your (just-restarted) process, OEM skins delay binding further, and after a force-stop Android actively *removes* the grant. A check fired synchronously on the first frame of a cold reopen therefore legitimately returns "not enabled / can't verify" for a shield that comes alive seconds later — exactly Monolith's symptom. The robust pattern from the field:
1. The accessibility service itself writes a **heartbeat** (timestamp + `onServiceConnected()` flag) to persistent storage when it binds and periodically while alive.
2. The UI reads three states: grant present in Settings **and** heartbeat fresh = ARMED; grant present but heartbeat stale/absent = "starting / verifying" (retry with a grace window, ~a few seconds, re-checking on `addAccessibilityStateChangeListener` / a `ContentObserver` on `ENABLED_ACCESSIBILITY_SERVICES`); grant absent = genuinely OFF.
3. Only the third state may ever show a warning, and never on the first cold frame.

**Applies to Monolith:** This is the most likely root-cause family for the known "CAN'T CHECK BLOCKING — TAP TO RETRY" pain on cold reopen, and the fix is architectural, not a retry button: move the verdict off the cold-start synchronous check, give the service a persisted `onServiceConnected` heartbeat, and make the banner a three-state readout with a silent grace period. It also satisfies TELL-THE-TRUTH better than either extreme (false alarm now, or a false green): "verifying…" is a truthful state. Pair it with Find 2's gap detection so a *persistently* stale heartbeat escalates to the OEM-specific instructions instead of an eternal spinner.

---

## 4. Exact alarms die silently three ways — routines need the full permission lifecycle

**Technique:** Self-rescheduling exact-alarm chains + permission-state receiver + boot re-arm, per Google's own current docs.

**Sources:**
- https://developer.android.com/develop/background-work/services/alarms
- https://developer.android.com/about/versions/14/changes/schedule-exact-alarms
- https://dev.to/stoyan_minchev/what-android-oems-do-to-background-apps-and-the-11-layers-i-built-to-survive-it-28bb (Layer 8)

**What makes it elite:** Three separate silent-death modes, all documented, all plausible causes of a routine that "just didn't fire":
1. **Denied by default:** `SCHEDULE_EXACT_ALARM` is *not* pre-granted on fresh installs targeting API 33+, and is **stripped on backup-and-restore to a new device / after an Android 14 upgrade path** — you must call `canScheduleExactAlarms()` before *every* scheduling attempt and route the user via `ACTION_REQUEST_SCHEDULE_EXACT_ALARM` if false.
2. **Revocation kills everything with no callback:** when the user (or an OTA / permission reset) revokes exact-alarm access, *all* pending AlarmManager chains die silently — no exception, no broadcast to the chains themselves. The only recovery is listening for `ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED` and re-establishing every chain on re-grant.
3. **Reboot wipes AlarmManager entirely:** pending alarms do not survive shutdown; only a manifest-declared, `exported` `BOOT_COMPLETED` receiver re-arming them does. And `setRepeating()` has been **inexact since API 19** — a "daily routine" built on it drifts and batches under Doze. The correct pattern is a self-rescheduling chain: each firing schedules the next with `setExactAndAllowWhileIdle()` (noting its Doze quota: roughly one fire per 9 minutes per app while idle — fine for routines, fatal for a naive 1-minute watchdog). `setAlarmClock()` is the highest-priority escape hatch but is OEM-metered (Find 2).

**Applies to Monolith:** Given a live bench failure of a routine not firing, audit in this order: (a) is `canScheduleExactAlarms()` checked at arm time and at boot, with the result persisted and surfaced truthfully if false? (b) Is there a receiver for the permission-state-changed broadcast re-arming all routines? (c) Are routines a self-rescheduling `setExactAndAllowWhileIdle` chain rather than `setRepeating`? (d) Does the boot receiver re-arm *routines* as well as the shield service? Any one "no" fully explains a silent no-fire, and none of them throws an error you'd ever see in a log.

---

## 5. Monotonic-clock anchoring + asymmetric cooldowns — killing the clock-change and midnight-relax bypasses

**Technique/product:** Ripple (free, local-only Android focus blocker, v0.1.1–v0.1.5) — the sharpest small-blocker engineering writing currently publishing.

**Sources:**
- https://dev.to/disconfirmai/why-most-android-screen-time-blockers-fail-at-midnight-and-how-we-fixed-the-clock-uninstall-1e44
- https://dev.to/disconfirmai/building-a-cross-platform-screen-time-blocker-ios-familycontrols-android-device-owner-that-kids-24dn

**What makes it elite:** Two ideas, both simple, both missing from most blockers:
- **Monotonic anchoring:** every timer/cooldown is measured on `SystemClock.elapsedRealtime()`, never `System.currentTimeMillis()`. Changing the date/time in Settings — the cheapest bypass there is — does nothing. State is anchored to the monotonic clock and **re-anchored across reboots** (persist the wall-clock + elapsed pair at anchor time; on boot, recompute elapsed continuity instead of trusting the wall clock alone).
- **Asymmetric friction:** tightening a rule (add an app, lower a limit, extend a sleep window) applies *instantly*; relaxing a rule (pause, raise a limit, unlock uninstall) requires a cooldown — 15 min default, configurable to 2 h, **doubled for short-video apps** — with an optional friend-generated one-time code for real emergencies. It designs for the actual adversary: morning-you vs midnight-you.
- Bonus craft from v0.1.5: block only the *surfaces* (Shorts / Reels / Channels feeds) inside whitelisted messaging apps, with per-session caps (~30 min) on the whitelist itself, so "messaging stays reachable" stops being a two-hour scroll loophole.

**Applies to Monolith:** (a) Audit every lock, peek-pass, emergency-break and routine-duration calculation for `currentTimeMillis()` — any wall-clock-based lock duration is bypassable in seconds via Settings, which directly breaks HOLD-absolutely. The 60-second emergency-break hold and the 2-minute peek pass especially must be monotonic. (b) The asymmetric-cooldown pattern maps cleanly onto Monolith's existing "exactly one emergency break per week": de-arming / shortening a routine should carry a delay that arming never does. (c) Surface-level blocking inside allowed apps is the known Shorts/Reels problem — Ripple proves it's shippable in a small local-only app.

---

## 6. The uninstall-guard truth table — and the one genuine tier, reachable without a PC

**Technique:** Layered uninstall resistance, honestly graded; Device Owner via on-device Wireless Debugging / Shizuku as the top tier.

**Sources:**
- https://applass.com/blog/app-blocker-cannot-be-uninstalled-android
- https://dev.to/disconfirmai/why-most-android-screen-time-blockers-fail-at-midnight-and-how-we-fixed-the-clock-uninstall-1e44 (shell-daemon route)
- https://learn.microsoft.com/en-us/dotnet/api/android.app.admin.devicepolicymanager.setuninstallblocked?view=net-android-35.0 (`DevicePolicyManager.setUninstallBlocked` — callable by a device owner / profile owner / `DELEGATION_BLOCK_UNINSTALL` delegate)

**What makes it elite:** The AppLass teardown states plainly what blocker marketing hides, with the mechanism: **no Play-Store app can truly prevent its own uninstall.** Device admin's self-protection was removed in Android 7 (malware abuse), device admin was deprecated in 2020 and is unavailable on newer GMS devices — today it adds exactly one deactivation step. The accessibility guard is a *race*, not a lock: the service watches for your App Info page / the accessibility-settings page and fires `performGlobalAction(GLOBAL_ACTION_HOME / BACK)` in milliseconds before a finger lands. It works against impulse, and it is defeated by (in order) revoking accessibility, deactivating admin, **Safe Mode** (no third-party services run — unfixable at consumer tier), and ADB uninstall. The *only* genuine tier is **Device Owner**: `setUninstallBlocked()`, suspend/hide apps, block Safe Mode — defeated only by factory reset — but it must be provisioned on a device with no accounts (practically post-factory-reset) via ADB, which is why no consumer blocker offers it as a toggle.
Ripple v0.1.5 shows the modern middle path: on Android 11+, pair with the phone's **own Wireless Debugging** (no PC, no root) to start a small shell daemon via `app_process` as UID 2000 (shell) — Shizuku-compatible — which locks the uninstall switch, restarts the blocker within ~2.5 s if swiped from Recents, and **rebinds the accessibility service every 2.5 s if the system kills it**.

**Applies to Monolith:** (a) Keep the existing device-admin + accessibility race, but label it truthfully in-product as *friction*, never "uninstall-proof" — TELL-THE-TRUTH applies to the guard's own claims. (b) Offer Device Owner as an explicit, opt-in "hard mode" provisioning flow for Sarthak's own device (he is the single user; the factory-reset/ADB cost that kills it for consumers is acceptable for a personal build), with `setUninstallBlocked` on Monolith itself. (c) The cheaper 80%: a Shizuku/Wireless-Debugging companion mode whose only jobs are accessibility rebinding and recents-swipe resurrection directly attacks Monolith's two weakest moments — OEM-killed accessibility (Find 2) and the cold-reopen check failure (Find 3). (d) Accept Safe Mode as a designed, acknowledged escape at consumer tier rather than pretending otherwise; if it must be closed, Device Owner is the only door.

---

## 7. Android 17 Advanced Protection will revoke Monolith's accessibility grant outright — plan the degraded mode now

**Technique/threat:** Android 17 Advanced Protection Mode (AAPM): only apps declaring `isAccessibilityTool="true"` keep AccessibilityService access; everyone else's grant is revoked automatically, with no prompt, no grace period, and no per-app override.

**Sources:**
- https://thehackernews.com/2026/03/android-17-blocks-non-accessibility.html
- https://labs.cosmicbytez.ca/news/2026-03-16-android-17-blocks-non-accessibility-apps-from-accessibility-api-to-prevent-malwa
- https://www.unpanictech.online/2026/10/android-17-advanced-protection-accessibility-services.html

**What makes it elite:** This is an existential platform shift for every blocker, stated in Google's own framing: accessibility services "can read sensitive data, install malware, or block uninstallation" — i.e. Google now classifies *Monolith's exact technique* as the abuse pattern. Under AAPM the exempt categories are narrow (screen readers, switch access, voice input, Braille); automation tools, launchers, antivirus, parental-control/monitoring apps are explicitly blocked — Tasker lost its grant instantly in field tests. Two traps to avoid: declaring `isAccessibilityTool="true"` falsely (it's reserved for genuine assistive tools; misdeclaring invites Play/policy trouble and still may not survive verification), and hard-failing when the grant vanishes. Google does ship an `AdvancedProtectionManager` API so apps can *detect* AAPM and adapt.

**Applies to Monolith:** Monolith is sideloaded/personal, so this only bites if Sarthak ever enables Advanced Protection — but if he does, the shield dies silently today. Build the degraded mode now: detect AAPM / sudden accessibility revocation, fall back to the UsageStats + overlay path (slower — Find 1 — but alive), and surface a truthful banner explaining *why* blocking is degraded and that disabling Advanced Protection is the only restore. That banner is the same three-state honesty machinery as Find 3 — one investment, two failure modes covered. Do **not** set `isAccessibilityTool="true"`.

---

### Mauer's priority order for #7

1. **Find 3** — fixes the live, user-visible "CAN'T CHECK BLOCKING" defect; small, architectural, testable on-device.
2. **Find 4** — explains and prevents silent routine no-fires; all four audit questions are yes/no code checks.
3. **Find 2** — the survival checklist; adopt watchdog chain + `MY_PACKAGE_REPLACED` + gap detection incrementally.
4. **Find 5** — monotonic-clock audit is a grep away and closes the cheapest bypass in the app.
5. Finds 1, 6, 7 — role audit, truthful guard labelling + optional Device Owner hard mode, and the Android 17 degraded mode, in that order.

— Mauer (Scout 8)
