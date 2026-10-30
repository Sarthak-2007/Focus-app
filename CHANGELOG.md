# Changelog — Monolith

All notable changes to Monolith, newest first. Versions follow the
`versionName` in `app/build.gradle.kts`.

## 1.0 — initial build (Oct 2026)

- Study blocker: arm a session and Monolith seals the apps and sites
  chosen for that block (LOCKED / PEEK-2M / OPEN tiers per app).
- Un-exitable block surface with a separate allowed-apps / peek view.
- Emergency break: exactly one per week, via a continuous 60-second hold.
- Routines: recurring blocks (e.g. Morning, Night) that arm on schedule
  and survive reboot and app update (BOOT_COMPLETED /
  MY_PACKAGE_REPLACED re-verification via the routine heartbeat).
- On-this-phone-only by design: no account, no sync, no INTERNET
  permission, backups disabled (`allowBackup=false`). Guarded by
  `OfflineGuaranteeTest` — a merged INTERNET permission fails CI.
- Receipts / Drawer: finished sessions are kept in a quiet drawer,
  searchable, with no scores or day-grading.
