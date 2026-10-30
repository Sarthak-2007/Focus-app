# Issue #302 — Audit R5 supplement, AppBlock Comparative audit (3 findings)
## WELLE-7 cluster E verification record — Ersatz (lead, replacement)

Verified against origin/main at branch base. All three lines are
comparative WINS the audit itself records for Monolith — the
"loss" framing does not hold on current code; each is struck in
the issue body as VERIFIED-FALSE (as a loss) with the evidence
below. No code change is owed; the deliverable is this proof.

Seats (≥10, weighted to a verification-shaped issue): Verifier ×2
(lead + second pass), Diagnostician (SDK/permission census),
Brutal critic, Purpose-questioner, Consumer, Technical execution,
Implementation-quality, Fresh-eyes, Keeper of Record, Builder
(record). Thin seats LEAD-COVERED by Ersatz; none dropped.

## F1 [HIGH] "AppBlock ships three ad networks + backend sync; Monolith zero SDKs, no backend" — VERIFIED-FALSE as a loss (WIN holds)
- `app/build.gradle.kts`: every network/analytics-adjacent
  dependency line is commented out (firebase BOM/ai/firestore/auth,
  okhttp, retrofit, moshi — lines ~78–113); the live dependency
  list is Compose, Room, coroutines, lifecycle only.
- `app/src/main/AndroidManifest.xml`: declares NO
  `android.permission.INTERNET` at all (grep count 0) — a backend
  sync is not merely absent, it is impossible without a manifest
  change. No AD_ID / ACCESS_ADSERVICES permissions.
- Manifest pins `android:allowBackup="false"` with
  dataExtractionRules/fullBackupContent exclusions (UNA-04), so
  the "DATA: ON THIS PHONE ONLY" plate is mechanically true.
Disposition: struck — win stands, protect it (any future SDK
addition must be judged against this edge, per the audit's own
recommendation).

## F2 [MEDIUM] "AppBlock's block is a binary wall; Monolith's Airlock has a graduated peek tier" — VERIFIED-FALSE as a loss (WIN holds)
- `MonolithModels.kt:25` — `AppBlockTier.PEEK_2M` (2-minute
  check-in, mandatory 20-minute cooldown) sits between LOCKED and
  OPEN in the three-state model (MonolithModels.kt:10-31).
- Lease machinery live: `MonolithRepository.startPeekLease()` /
  `recordPeekEnded()` write peek lease + cooldown stamps via DAO.
Disposition: struck — win stands; peek tier is a differentiator
to present as designed, per the audit's recommendation.

## F3 [MEDIUM] "AppBlock ships developer tooling in its release binary; Monolith ships none" — VERIFIED-FALSE as a loss (WIN holds)
- Grep of `app/src/main/` for DeveloperSettings / DevEvents /
  mock-premium surfaces: zero hits.
- `AndroidManifest.xml` declares exactly one activity
  (.MainActivity), three services, four receivers — no developer
  activity, menu, or override surface exists to ship.
Disposition: struck — win stands; standing rule recorded: keep
diagnostic/override surfaces out of the release manifest.

Signed: Ersatz, WELLE-7 cluster E lead (replacement).
