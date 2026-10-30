# Monolith — Privacy Policy

_Last updated: 2026-10-30. This policy describes the Monolith app
(package `com.example`, distributed as "Monolith") exactly as the
shipped code behaves. Every claim below is traceable to a source
file, cited in `docs/DATA_SAFETY.md`._

**Contact:** [Sarthak to fill: name + email address for privacy
questions. Google Play requires a monitored address here before
submission.]

## The short version

Monolith is a focus blocker. Everything it reads stays on your
phone. The app has **no `INTERNET` permission** and contains **no
networking code** — it is technically incapable of sending your
data anywhere. There are no accounts, no analytics, no ads, no
trackers, and no third-party SDKs that receive data. Android cloud
backup of app data is disabled (`android:allowBackup="false"`).

## What the app reads, why, and where it goes

### 1. Notifications of blocked apps (Notification Listener)
- **What:** When a focus session is active, notifications from apps
  you have chosen to block are captured: the sending app, the
  notification title (sender) and its body text.
  (`service/MonolithNotificationListener.kt` — title/body read from
  `Notification.EXTRA_TITLE` / `EXTRA_TEXT`; call and alarm
  notifications are never captured.)
- **Why:** So a blocked app cannot pull you back in, and so you can
  read what you missed afterwards in the in-app digest instead.
- **Where:** Stored only in the app's private on-device database
  (Room, `data/MonolithModels.kt` `CapturedNotificationEntity`).
  Never transmitted, never shared.
- **Your control:** You can delete individual captured
  notifications or clear the whole digest at any time in the app
  (`MonolithDao.deleteCapturedNotification` /
  `clearAllCapturedNotifications`). Turning off notification access
  for Monolith in Android Settings stops all capture.

### 2. App-usage statistics (Usage Access)
- **What:** Per-app foreground time (which app is in front, and
  daily totals per app), read through Android's `UsageStatsManager`.
  (`data/MonolithRepository.kt` `queryUsageStats`.)
- **Why:** To know which app is on screen so blocking rules apply
  to the right app, and to show you your own usage figures.
- **Where:** Read on demand on the device; totals the app displays
  are computed on the device. Never transmitted, never shared.

### 3. Installed-app inventory (package visibility)
- **What:** The list of launcher apps installed on the phone —
  package name and display label — obtained via
  `PackageManager.queryIntentActivities` for the LAUNCHER category.
  (`data/MonolithRepository.kt`, app-catalog sync.)
- **Why:** So you can see your apps and choose which ones Monolith
  blocks, peeks, or leaves open.
- **Where:** Stored only in the app's private on-device database
  and preferences. Never transmitted, never shared.

### 4. On-screen content via the Accessibility Service
- **What:** The accessibility service observes window changes on
  the device. It reads (a) browser address-bar text in Chrome,
  Brave, Firefox, Edge and Samsung Internet, and (b) on-screen text
  inside Android Settings and inside YouTube/Instagram, solely to
  recognise blocked sites, Shorts/Reels surfaces, and attempts to
  force-stop or uninstall Monolith.
  (`service/MonolithAccessibilityService.kt` —
  `detectBlockedBrowserUrl`, `collectWindowTextLowercase`.)
- **Why:** Blocking happens where blocking is possible: the URL bar
  is the only place a blocked site is visible, and Shorts/Reels are
  surfaces inside other apps.
- **Where:** This text is processed **in memory only, at the moment
  of the event, and is not stored**. The full URL is never saved;
  when a site matches your own blocked-domain list, only that
  domain — which you typed into the list yourself — names the
  block screen. Nothing is transmitted or shared.
- **Honesty note:** Monolith is a blocker, **not** an accessibility
  tool for disability support, and it does not claim to be one
  (`android:isAccessibilityTool="false"` in
  `res/xml/accessibility_service_config.xml`).

### 5. Data you create in the app
Focus sessions, tasks, routines, blocked-site lists, per-app rules
and settings are stored in the app's private on-device database
and `SharedPreferences`. This is your own configuration, created by
you, used only to run the app.

## Data sharing and sale

None. Monolith shares no data with any third party, sells no data,
and contains no advertising or analytics SDKs. Because the app
holds no `INTERNET` permission, no data leaves the device through
the app at all.

## Retention and deletion

- Data lives on the device for as long as the app is installed and
  you keep it.
- Captured notifications can be deleted individually or cleared in
  full inside the app at any time.
- Uninstalling Monolith deletes all of its on-device data.
- There is no server copy to delete: nothing was ever uploaded, and
  cloud backup of app data is switched off in the manifest.

## Children

Monolith is a general-audience productivity tool. It is not
directed at children, and — having no network capability and no
accounts — it collects nothing from anyone off-device.

## Changes to this policy

If a future version changes what the app reads or adds any network
capability, this policy and `docs/DATA_SAFETY.md` must be updated
in the same change, before release.
