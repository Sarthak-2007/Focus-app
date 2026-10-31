# Monolith — Google Play Data Safety mapping

_One row per Play Data Safety form question, each answered from a
verified code fact (re-grepped 2026-10-30 against `main` @
`2531c84` during the issue-#86 closure re-verification). If code
changes, re-verify the cited line before re-submitting the form._

## Global answers

| Form question | Answer | Code fact |
|---|---|---|
| Does your app collect or share any user data? | **Collects, on-device only; shares none** | Collection happens into the app's private Room DB / SharedPreferences; sharing is impossible — see next rows. |
| Does your app use the INTERNET permission? | **No** | `grep INTERNET app/src/main/AndroidManifest.xml` → 0 hits. |
| Networking / upload code present? | **None** | Grep for `HttpURLConnection`, `OkHttp`, `Retrofit`, sockets, `openConnection` across `app/src/main/java` → 0 hits. |
| Is data encrypted in transit? | **N/A — no transit exists** | No network code (above). Play form: answer "data is not sent off device" wherever offered. |
| Data shared with third parties? | **No** | No network code, no ads/analytics SDKs in `app/build.gradle.kts` dependencies. |
| Can users request data deletion? | **Yes — in-app + uninstall** | Captured notifications: per-item delete and clear-all (`MonolithDao.kt:286-289` `deleteCapturedNotification` / `clearAllCapturedNotifications`). Uninstall removes all app data. No server copy exists. |
| Cloud backup of app data? | **Disabled** | `AndroidManifest.xml:41`: `android:allowBackup="false"` with `dataExtractionRules`/`fullBackupContent` excluding every domain. |
| Independent security review / labels | None claimed | — |

## Per data type

| Play data type | Collected? | Shared? | Ephemeral? | Required/optional | Purpose (Play wording) | Code fact |
|---|---|---|---|---|---|---|
| **Messages — other in-app messages** (notification title + body of blocked apps) | Yes | No | No | Optional (only while a session runs and the app is blocked) | App functionality | `MonolithNotificationListener.kt:278-279` (and :423-424): reads `EXTRA_TITLE`/`EXTRA_TEXT`, persists `senderTitle`/`messageBody` (`MonolithModels.kt:631-637` `CapturedNotificationEntity`) into the on-device Room DB for the post-session digest. Call/alarm categories excluded. |
| **App activity — app interactions / usage** (per-app foreground time) | Yes | No | Read on demand | Required for blocking | App functionality | `MonolithRepository.kt:1138,1251`: `UsageStatsManager.queryUsageStats(INTERVAL_DAILY, …)` + foreground polling for enforcement. |
| **Installed apps inventory** | Yes | No | No | Required for blocking setup | App functionality | `MonolithRepository.kt:572`: `PackageManager.queryIntentActivities(ACTION_MAIN + CATEGORY_LAUNCHER)` → package name + label persisted on-device as the blockable-app catalog. Manifest `<queries>` is scoped to the LAUNCHER intent only. |
| **Web browsing history** | **No — not collected** | No | **Yes (in-memory only)** | — | — | `MonolithAccessibilityService.kt:1532` `extractBrowserUrlTexts` / `:1557` `collectUrlBarTexts` read the browser URL-bar text in memory; `:117` `matchBlockedDomain` compares it against the user's own blocked-domain list. The URL text is **never stored, logged, or transmitted**; only a match/no-match result is used, and the matched value is a domain from the user's own list. Declare as *not collected*; if Play's review asks, this is ephemeral processing for app functionality. |
| **On-screen text via accessibility** (Settings window text; YouTube/Instagram surface detection) | No (transient processing) | No | Yes | Required for blocking | App functionality | `MonolithAccessibilityService.kt:1577` `collectWindowTextLowercase` builds an in-memory string to detect Shorts/Reels surfaces and Settings tamper attempts; result is a boolean trigger, text is discarded. `accessibility_service_config.xml` declares `android:isAccessibilityTool="false"` (pinned false on purpose — Monolith is a blocker, not an accessibility tool). |
| **Personal info** (name, email, address, IDs) | No | No | — | — | — | No account system, no sign-in, no such fields anywhere in the data model (`MonolithModels.kt`). |
| **Financial info** | No | No | — | — | — | No payments code. |
| **Health / fitness** | No | No | — | — | — | — |
| **Location** | No | No | — | — | — | No location permission in the manifest; no location APIs used. |
| **Photos / videos / audio / files** | No | No | — | — | — | No storage/media permissions; no media APIs. (The app plays its own bundled UI sounds only.) |
| **Calendar / contacts** | No | No | — | — | — | No such permissions or APIs. |
| **Device or other IDs** | No | No | — | — | — | No advertising ID, no `ANDROID_ID` read for identification, no fingerprinting code. |
| **App-generated user content** (sessions, tasks, routines, rules, blocked-site list) | Yes — created by the user, on-device | No | No | Required | App functionality | Room entities + `SharedPreferences` stores (`MonolithDatabase.kt`, `DayWordStore.kt`, `LoadoutStore.kt`). This is the user's own configuration, not behaviour tracking. |

## Permissions cross-reference (manifest, as on `main` @ `2531c84`)

| Permission | Why | Data Safety consequence |
|---|---|---|
| `POST_NOTIFICATIONS` | Show the focus session's foreground-service notification. Runtime-requested on `main` since issue #280: `MainActivity.kt` launches the system prompt via `ActivityResultContracts.RequestPermission` when the first-run board is delivered (API 33+ gated, grant-checked first). | No data type — posting, not reading. |
| `SYSTEM_ALERT_WINDOW` | **Live permission — retained.** Consumer: the peek countdown pill (`PeekPillOverlay.kt`, issue #114) — `WindowManager.addView` with `TYPE_APPLICATION_OVERLAY` (:134, :152), gated on `Settings.canDrawOverlays` (:56), driven by `MonolithViewModel` during a peek; draws over the peeked app, reads no data. Issue #86's original audit premise ("no overlay window is ever created") no longer holds on current `main`, so the removal originally drafted in PR #249 was **not** applied; the finding was struck with this evidence at #86 closure (2026-10-30). | Display only — no data read. Declare the permission with the peek-pill justification in the Play submission. |
| `FOREGROUND_SERVICE` / `FOREGROUND_SERVICE_SPECIAL_USE` | Keep the shield running during a session | None. |
| `PACKAGE_USAGE_STATS` | Usage rows above | Declared above. |
| `RECEIVE_BOOT_COMPLETED`, `VIBRATE` | Restart routines after reboot; haptics | None. |
| Notification Listener binding | Messages row above | Declared above. |
| Accessibility Service binding | Ephemeral on-screen processing above | Declared above; `isAccessibilityTool="false"`. |
| Device Admin binding | Uninstall protection during sessions | No data read. |
