# Issue #330 — Flipd Platform execution (4 findings) — WELLE-7 cluster J disposition

Lead: Welle (cluster J, replacement — first lead died at bootstrap; verified
0 prior comments and no branch for #330 at claim time).
Verified against origin/main @ b7a52cc. All four findings describe Flipd's
platform footprint; Monolith is clean on each — VERIFIED-FALSE as losses.

Seats (11, weighted platform): Technical execution · Diagnostician ·
Verifier · Builder · Implementation-quality · Brutal critic ·
Purpose-questioner · Fresh-eyes · Consumer · Methods · Keeper of Record.
Technical execution + Diagnostician + Verifier run as separate manifest /
build-file / grep passes; the rest LEAD-COVERED.

- F1 Special accesses only inside Focus Lock setup — VERIFIED-FALSE as a
  Monolith defect. Flipd can gate grants per-feature because most of
  Flipd (timers, social, stats) is not a blocker. Monolith has no
  grant-free second product: its one product IS the block, and its
  grants are proven at the point of use — arm time — via
  ArmProvingController / the guard board, while Deck/Shield remain
  browsable without them. Just-in-time exists here; the "time" is the
  seal, which is the only moment the grants do work.
- F2 Typo permission (QUERY_ALL_PACKAGES without prefix) —
  VERIFIED-FALSE. Every Monolith permission is a real
  `android.permission.*` name (`AndroidManifest.xml:6-14`); package
  visibility rides on the `<queries>` LAUNCHER intent (:17-23). No
  `QUERY_ALL_PACKAGES` string exists in the manifest.
- F3 2,079 of 19,261 classes are Flipd's; permissions serving SDKs —
  VERIFIED-FALSE (Monolith win). Monolith ships zero third-party SDKs:
  `app/build.gradle.kts:77-110` dependencies are AndroidX / Room /
  coroutines only, with Firebase, Camera, Coil, Moshi and credentials
  all commented out; every component in the manifest is the app's own.
  Its permission list carries no ad/location/phone-state rent.
- F4 Release build ships scaffolding (debugger wait, exported preview
  activity, test link hosts, debug logging) — VERIFIED-FALSE.
  `grep -rn "waitForDebugger|PreviewActivity|test-app.link" app/src/main`
  = 0 hits. The only `exported="true"` component is MainActivity (the
  launcher entry, required); all services and receivers are
  `exported="false"` (`AndroidManifest.xml`).
