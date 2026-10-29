# INTEGRATION — Riss's running log, Blueprint lane

Opened Oct 29, ~23:41 IST, on Monteur's + Ausbau's closes.
Verified on disk by Riss unless marked (claim).

## Verified clean
- VM census: exactly ONE DrawingRevisionStore field
  (MonolithViewModel.kt:219), ONE bump() (:1340, inside
  persistRoutine beside the stamp), ONE R6 gate (:1272–1277,
  LookStore read at the call site, detent spent only when the
  look is not BLUEPRINT). Monteur's and Ausbau's accounts agree
  with the tree. The parallel-hand question is closed: Ausbau
  placed the touches (my mis-addressed doorbells), Monteur
  verified single-instance and removed his own duplicate field.
- DUP-1 CONFIRMED DEAD: TitleBlockRevisionCell
  (MonolithCustomComponents.kt:942) + drawingCloudLive (:905)
  have zero call sites anywhere in the lane — Monteur's
  TitleBlock carries the mounted REV cell. Ausbau fix round:
  delete both.
- LEAK-1 PARTLY DISCHARGED BY AUSBAU HIMSELF: inkControlFill()
  no longer hardcodes the warm stock tones — night resolves to
  #26356E (the Bp night secondary-container step), day to
  scheme.onBackground; onInkControlFill() scheme-derived too.
  Two notes, neither a blocker: (a) the night branch keys on
  scheme.background luminance, so STOCK night's filled controls
  also take the indigo step — a stock-night tint delta for the
  merge desk to accept or gate; (b) the hex duplicates a scheme
  value it could read (scheme.secondaryContainer) — polish.

## Open items for fix rounds / render judgment
- PITCH-1 (reconcile against Politur R1's exact text before
  touching): VM sites as found — :1310 dial minutes
  440 + min×2.8 (R1 slope ✓); :1846 480 + clamped×2.5 (slope
  ✗ if the site is minute-valued; function context to read);
  :1303 + :1821 flat 640.0 detents (scope to check). Ausbau
  (claim) unified his view call sites to 480 + value×2.5 —
  against R1's 2.8 if those sites are minute-valued. One fix
  round, one owner, after R1's text is re-read.
- HOST-1 (Monteur fix round): ST-07 NEXT line + E-3 tolerance
  verdict are built Deck-side (Ausbau) with null defaults —
  MainActivity must pass nextRoutineLabel /
  nextRoutineBoundaryMillis (exact param names from Ausbau's
  Deck signature) from the routines flow + heartbeat facts
  RootApp already collects. Silent until wired = a missing
  K-9 line and a missing verdict, both owed by the spec.
- TIERB (Monteur fix round, ruled in mail/monteur/2345):
  stacked identity cell, wordmark 96dp, revision 112dp,
  ISSUED 104dp stands.
- WHEEL-1 (render judgment): Zirkel's ±144dp extension lines
  vs the drawn rim at 129.6dp radius — 14.4dp standoff per
  side. Drafting-correct IF the arrows land on the rim; the
  Deck render decides.
- SPEC-1: HubSpecularTest — specular drawing removed by
  Ausbau; press-depth machinery + CORE_TRAVEL_GATE stand.
  Check its assertions at the test run; retire/adjust only
  what pins the killed specular, name it in the manifest.
- Queue at 23:41: cap 2, both flags held (pill LIVE;
  routine-edit LEGACY age ~285s). The integration compile
  waits for a free flag — Einbau is still building anyway.

## Fix rounds — Ausbau (2355, complete, verified against his letter)
- DUP-1 DONE: drawingCloudLive + TitleBlockRevisionCell deleted
  (−77 lines, zero references lane-wide). KEPT + flagged by him:
  the pure helper `titleBlockRevisionText` — census at
  integration; delete if it has no call sites, keep if
  Monteur's TitleBlock calls it.
- PITCH-1 CLOSED: updateTaskDuration now 440 + m×2.8 (R1);
  adjustRoutineTime now 520 + resultingMinutesOfDay×14/60 (R3);
  end2MinutePeekEarly now playFirmClick (R5). The two flat-640
  sites censused: setStudyBlocksCount (voices the count 1–8)
  and toggleTaskSubStep (a toggle detent) — neither
  minute-valued, both stand by R1's own terms.
- Held Door facts DONE (VM half): 6 nullable AirlockInterceptState
  fields name-for-name with Einbau's overlay params, filled in
  triggerAirlockIntercept; honest withholding named (last-peek
  label suppressed when the stamp is a pre-scheduled future
  lease end; split null until the apps flow emits). Call-site
  half is Monteur's round, item 6.
- endedDerived DONE (data half): field on PendingReceipt +
  ReceiptState (defaulted false — harness construction still
  compiles), set row.endWallMillis == 0L at assembly, carried
  through publish + rehydrate.
- StoredMonth counts DONE (data half): StoredMonthCount
  projection + DAO COUNT(*) on the same 03:30 filing-day
  projection + repository map + VM monthEntryCounts StateFlow.
  Call-site half is Monteur's round, item 5.

## Coordinator edits owed at integration (Riss's own hand)
- RECEIPT-BRACKET: ReceiptSheet.kt still brackets ENDED on the
  `restored` proxy (Einbau's disclosed over-marking). Swap the
  bracket condition (and its legend gate) to the new
  `endedDerived` flag. Check first whether `restored` carries
  any other print in that file — swap only the bracket logic.
- titleBlockRevisionText census (above).

## Compile attempts (integration, Oct 29–30)
- Attempt 1: flag acquired after 435s; died on `./gradlew`
  (no wrapper script in this tree — kit lanes use the shared
  gradle 9.3.1 on PATH). Mechanical, zero code signal.
- Attempt 2: acquire TIMED OUT at 1500s (look-dawn LIVE
  1690s + look-temple LIVE) — queue saturation, no run.
- Attempt 3: acquired after 510s; BUILD FAILED in 2m4s with
  `java.io.IOException: Operation not permitted` —
  "Failed to notify root build lifecycle listener / Could not
  update guh/caches/9.3.1/file-changes/last-build.bin" +
  "Could not receive a message from the daemon". Both write
  targets are lane-private. The Oct 29 signature (Methods
  Sweep 2: write failures = RAM death under daemon pile-up,
  not a path guard). Diagnosis at retry time: 3.1 GB
  available, 4 daemons resident (~1.9 GB RSS across three
  older ones), two sibling builds live — the failure window
  coincided with peak contention (dawn's marathon + temple).
  NO code errors were reported before the infrastructure
  failure; the Kotlin compile never got to speak.
- Attempt 4: acquire TIMED OUT at 3300s. Queue state at
  timeout: SIX flags over the cap of 2, all LEGACY pid=none —
  on disk they are 0-byte, root-owned files timestamped
  19:31/19:39 (bare-touch relics from before queue v2), and
  `queue.sh reap-stale --dry-run` judges 0 of them stale yet
  (the tool's own age measure for the cohort was ~700s at
  01:12 IST, crossing its 1200s legacy threshold shortly
  after). NO gradle CLI was running anywhere on the box at
  diagnosis — the cap is consumed by corpses, not claims.
- RAM at diagnosis (01:14 IST): 7187 MB used / 290 MB free /
  747 MB available, swap 0. Visible top RSS: three Gradle
  daemons (~1.4 GB combined, ages 50m–2h06), the runtime's own
  hatch process 583 MB — the visible processes sum to ~2 GB;
  the rest is not attributable from this seat's ps view.
  The VM itself booted ~23:05 IST (pid 1 elapsed 2h08).
- ESCALATED to Ren (attempt 5 re-armed in background: a
  3300s acquire → assemble → release, so the lane builds the
  moment a flag + ~2 GB exist). What the lane needs from the
  studio desk: (a) a reap-stale once the cohort crosses the
  tool's threshold, or a ruling on the root-owned relics;
  (b) box memory hygiene — the Oct 29 fix was a runtime
  restart + daemon cull; both are above coordinator
  authority, and killing other lanes' daemons from this seat
  is exactly what the queue law forbids. The lane's code,
  harness and manifest are complete and staged; nothing in
  any attempt has reported a code error.

## Attempt 5 + the authority change + STAND DOWN (final entry)
- Attempt 5: acquired after 2190s (the relic cohort aged out
  or was reaped — the queue cleared), fresh daemon, BUILD
  FAILED in 17s at Gradle VFS creation:
  `BuildLifecycleAwareVirtualFileSystem … java.io.IOException:
  Operation not permitted`. Suspect on record for whoever
  builds next: this lane's derived state — attempt 3's daemon
  died mid-write to `guh/caches/9.3.1/file-changes/
  last-build.bin`; the GUH's file-changes/VFS metadata and
  `Focus-app/.gradle/9.3.1/fileChanges` are derived state,
  safe to purge-and-rebuild. Left untouched (see below).
  RAM at the time still ~137 MB free / 1137 available.
- BUILD AUTHORITY (Sarthak, Oct 30 01:15 IST, via MEMORY.md):
  only Ren compiles, only on Sarthak's explicit word; lanes
  close DECLARED (code + tests written, uncompiled); earlier
  lane proof-compiles are grandfathered, never precedent.
  Attempts 1–4 predate the order; attempt 5 was armed at
  ~01:15, simultaneous with it, and is the lane's last —
  on the order reaching this seat, all compiling stopped.
- STAND DOWN (Ren, Oct 30 ~01:33 IST, mail/riss/0133):
  Sarthak stopped the 7 designs — build the main app
  properly first. Lane state stays on disk exactly as it
  stands; nothing merges now. THIS LANE CLOSES DECLARED.
- Superseded by the stand-down, unexecuted and noted (not
  silently dropped): Ren's soul-test gate (mail/riss/2338,
  23:38) — the colour-stripped structure proof owed before
  declaring; and the render-verdict seats (Scharf round 2,
  Tageslauf RT conversions, Politur gallery, Leitsatz soul
  test), which needed frames only a compile can produce.
  The composition case stands in the manifest §§1–3 if the
  programme ever resumes. The bar (mail/riss/2340, his words:
  "better than anything, commercial level, even above that")
  is recorded as the programme's standard at its stop.
- FEEL VERDICT (Haut, mail/riss/2338, 23:38 — before the
  stand-down): Blueprint feel KEPT, no amendment required —
  "the strongest feel build in the wave." Pitch family
  verified in code by her reading, incl. the wheel's view-side
  `fireFeel(1L, 480 + target×2.5)` texture site alongside the
  VM detents — monotone f(value) intact, the 440+2.8×
  task-duration recalibration a calibration inside the
  family, not a breach; peek-end firm click in place; Held
  Door stamp on the 140ms Curve A; R6's world-specific
  silence endorsed on record. PITCH-1's remainder is closed
  by the feel desk; the two-site fact stands here for the
  merge desk's information.

— Riss, Blueprint coordinator.
