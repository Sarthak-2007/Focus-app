# ISSUE #109 — WAVE 3 REPORT (Audit R3 journey: park-guard-lived)

Cell: `builds/cells/issue-109`, branch `agent/issue-109`, base `0a00e95` (origin/main as seeded).
Cluster lead: REISE. Every finding verified against the CURRENT code in the cell before any fix.

## Per-finding disposition

### F1 [CRITICAL] NOTIFICATIONS RUNNING with no session running — FIXED `c7c4c24`
Verified standing: `WatchStore.deriveInterceptor` derived NOTIFICATIONS from the listener
binding alone (setting hardcoded `true`, suspension always `false`), while
`MonolithNotificationListener.onNotificationPosted` returns early unless the active-session
snapshot `isActive`. Cure: WatchStore gains a `sessionActive` interceptor input, pushed by the
ViewModel's active-session collector next to the Safe Break input (`session?.isActive == true` —
the same predicate MainActivity calls `sealed`). NOTIFICATIONS now derives OFF when the listener
is bound but no session is capturing, RUNNING only while a session captures. Safe Break still
does not pause it (the listener does not suspend — verified in the listener code).
Evidence: `WatchStoreRobolectricTest.interceptors_deriveFromSettingsParentsAndSuspension`
updated to the new truth and extended — bound + no session → OFF; session active → RUNNING;
Safe Break → still RUNNING; session ends → OFF on the same binding.

### F2 [HIGH] Guard board unreachable / silently read-only mid-session — FIXED `bb470e2` (drawer half) + FIXED-IN-MAIN (tap half)
Verified in two halves:
- Tap half — FIXED-IN-MAIN: the audit's dead tap no longer exists. `MainActivity.kt`
  `onGuardLineTap` routes a sealed Guard-line tap to the launcher block surface (Sarthak's
  Oct 30 04:29 ruling — the sealed phone's home), and `openPark()` itself is guarded by the
  one `doorLocked` predicate (HB-1/HB-2). Not re-litigated here.
- Drawer half — stood exactly as filed: a seal landing under an open drawer hid the FIX key
  (`ParkDrawer` AnimatedVisibility `!isSealed`) and froze the sleds behind invisible overlays
  with no print anywhere. Cure: while sealed, the drawer prints at its head
  "SEALED — READ-ONLY. A SESSION IS RUNNING: THE BOARD CAN BE READ, NOT CHANGED, UNTIL IT ENDS."
  (testTag `park_sealed_note`). The lock itself is untouched — Park stays read-only while
  sealed (HOLD-absolutely); only the silence was the defect.

### F3 [HIGH] STALE is a dead end — FIXED `b51175d`
Verified standing: `firstMissing` keyed on `PermPrint.MISSING` only; opening PARK fired only
`refreshSystemGuard()` (whose own comment admits its result "renders NOWHERE"); the Watch sweep
ran only on ON_RESUME / Deck entry; the open board re-derives every 5s against
`STALE_AFTER_MS = 45_000`, ageing its own GRANTED lines into STALE in front of the reader.
Cure (two parts): (1) opening PARK now also fires the real Watch sweep (`runWatchSweep`), so the
board opens on fresh readings; (2) when nothing is MISSING but a line is STALE, the board offers
a RE-CHECK key (`park_recheck_key`) naming the first stale line; a press fires the same sweep
from the host and the lines re-measure in place. A missing grant still outranks a stale reading
— FIX keeps priority. Sealed, RE-CHECK is absent like FIX.

### F4 [MEDIUM] Header prints GUARD 4/4 over a board showing OVERLAY/BATTERY MISSING — FIXED `df53529`
Verified standing: `GuardLine.kt` counted only `WatchItemId.COUNTED` (4 of the board's 6
permission lines) and printed `GUARD 4/4` at wholeness. Cure: the header counts all six
PERMISSIONS — `GUARD 6/6` only when every board line is warranted GRANTED; a lack names the
first non-whole line across all six (bare, or with the ` STALE` suffix); any UNMEASURED of the
six silences the line (no partial denominator printed as a whole). `WatchStore.countedWarranted`
aligned to the same six. Evidence: `ChromeWaveUnitTest` guard-line cases moved to /6 plus the
F4 regression case itself — counted four whole + OVERLAY missing now prints
`GUARD 5/6 — OVERLAY` (was `GUARD 4/4`).

### F5 [MEDIUM] Two contradictory 'SHORTS + REELS' lines — FIXED `5668806`
Verified standing: the board interceptor line and the sled row (section 5) carried the same bare
name with different vocabularies (RUNNING/PAUSED/OFF vs ON/OFF) and no printed link. Cure:
the board line prints "SHORTS + REELS — MEASURED" (measured and never-measured branches) and
the sled row prints "SHORTS + REELS — SETTING". No behaviour change — the qualifiers are the
missing link between two true facts.

No finding was VERIFIED-FALSE. No schema change, no migration, no downgrade: settings were not
unlocked and the seal was not weakened anywhere in this lane.

## CI
- Run: https://github.com/Sarthak-2007/Focus-app/actions/runs/40985292769 (code tip `b51175d`;
  queued ~85 minutes behind the whole Wave-3 push, then ran)
- Verdict: **RED — one step only.** Step ledger (via the jobs API): checkout, setup-java,
  SDK install, keystore, **Assemble debug — SUCCESS** (all main and test code in this lane
  compiles, and the signed debug APK artifact `monolith-debug` was uploaded), upload-artifact,
  **Unit tests — FAILURE**, verdict summary, job complete.
- Named precisely, as far as the evidence reaches: the only red is the shared
  `testDebugUnitTest` step — the shared CI test floor the dispatch warned is being killed
  centrally (sibling Wave-3 branches completed failure in the same window). The individual
  failing test names could NOT be extracted from this cell: GitHub serves job logs from an
  Azure blob host whose transfers return empty/abort through this egress (tried
  `gh run view --log-failed` ×3, the run-logs zip, the job-logs API, and the signed blob URL
  ×3 with long timeouts). No claim is made that the failing tests are or are not this lane's.
- Mitigating evidence gathered instead: the two test classes this lane touched were
  re-derived by hand against the new code line by line (GuardLine cases: 6/6 whole, 4/6 lack,
  the F4 regression 5/6 — OVERLAY, stale 0/6, unmeasured-silence incl. the OVERLAY case;
  Robolectric interceptor sequence incl. the new session-gating transitions), and a repo-wide
  grep confirms no other test asserts the changed Guard texts, the MEASURED/SETTING labels,
  or NOTIFICATIONS derivation outcomes. REISE: if the central floor verdict names specific
  tests on the re-run, the two classes above are this lane's exposure surface.

## Plain-words user-facing changes
- The Guard board no longer claims notifications are being held when no session is running.
  NOTIFICATIONS reads RUNNING only while a session is actually capturing; at rest it reads OFF.
- The header's guard count now covers all six permissions the board checks (GUARD x/6). It can
  no longer say everything is whole while the board shows OVERLAY or BATTERY missing.
- If a session starts while the PARK drawer is open, the drawer now says at the top that it is
  sealed and read-only until the session ends — instead of its buttons silently going dead.
- Opening PARK re-measures all six permissions on the spot. If a line goes STALE while the board
  is open, a RE-CHECK button appears and re-measures without leaving the app.
- The two Shorts + Reels lines are labelled: the board line is the MEASURED state, the switch
  below is the SETTING.

## Seat sign-offs (bundle worn by the issue-worker; rest LEAD-COVERED by REISE)
- Human / Day-fit + User (journey walk in code): signed — walked the lived journey end to end:
  at rest the board lied RUNNING; mid-session the drawer died silently; a 45-second read rotted
  GRANTED into STALE with no remedy; the header blessed a board that disagreed with it; two
  same-named lines contradicted. All five reproduced in code before any fix.
- Verifier: signed — every finding re-checked against current code (file + line) after the
  Wave-2 Park merge landed in main; F2's tap half found already cured in main and left alone.
- Diagnostician: signed — root causes were derivation gaps, not the lock: a missing conjunct
  (session) in one derivation, a denominator drawn from the wrong set, a sweep wired to the
  wrong trigger, and two unlabelled facts sharing one name.
- Brutal critic: signed — the fixes do not touch the seal, do not unlock a single setting, and
  print more truth in exactly the places the user was previously left guessing. Residual noted:
  during a finish's detention window (navigationLocked, unsealed) a Guard tap still no-ops
  silently via openPark() — transient (seconds, mid-finish), no print surface exists on that
  path today; flagged for REISE rather than papered over here.
- Builder: signed — five surgical commits, one per finding, each scoped to its checkbox;
  existing tests that encoded the false prints were updated to the new truth, none deleted.
- Feel specialist: signed — RE-CHECK inherits the FIX key's body exactly (same 48dp ink key,
  same dip, same quiet seat knock at its floor), so the remedy feels like the drawer's own
  furniture, not a patch; the sealed note prints in the drawer's badge voice at the head,
  where the change is lived; no new colours, no green, fonts unchanged (Sora / Atkinson
  Hyperlegible / Anonymous Pro only).
- Interaction-detail: signed — FIX outranks RE-CHECK (a missing grant is never hidden behind a
  re-measure), the RE-CHECK press releases its dip on release like FIX, and the MEASURED/SETTING
  qualifiers ride the existing name plates — no new gestures, no new surfaces.
- Remaining seats: LEAD-COVERED by REISE.
