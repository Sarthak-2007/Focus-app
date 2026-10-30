# Issue #99 — Journey OPENING-BLOCKED-APP — Wave-3 Report

Lead: #99 bundle lead under WEG. Base: origin/main @ 169f688.
Branch: agent/issue-99.

## Seats (20 named + lenses, all signed)
Crazy-ideas ✓ · Maximum-implementation-potential ✓ · Brutal critic ✓ ·
Improver ✓ · Designer ✓ · Consumer ✓ · Student ✓ · User ✓ ·
User-experience ✓ · Purpose-questioning ✓ · Thoughtful ✓ ·
Deep-thinker ✓ · Enthusiastic designer ✓ · Wow-level designer ✓ ·
Designer-29-years ✓ · Intentionality-focused designer ✓ ·
Synchronous collaboration ✓ · Implementation-quality ✓ ·
Technical execution specialist ✓ · Interaction-detail specialist ✓ ·
Human/Day-fit (LEAD-COVERED) · User lens ✓ · UX lens ✓ ·
Verifier ✓ · Diagnostician ✓ · Technical-execution lens ✓.
Thin seats are LEAD-COVERED — none silently dropped.

## Dispositions (5 findings: 4 FIXED-IN-CODE, 1 FIXED + remainder STANDS-BLOCKED)

### F1 [HIGH] Peek expiry never evicts — FIXED-IN-CODE
Verified on current main: MonolithRepository.kt:595 still filtered
`lastTimeUsed >= end-8_000`; VM ticker expiry only called
end2MinutePeekEarly. Cure, three layers:
- `expiredPeekPackages(apps, nowMono)` (pure, MonolithRepository.kt):
  lease end recorded, past, within a 15-min eviction window.
- `queryForegroundBlockedApp(blocked, expiredPeekPackages)` overload:
  for an expired-peek package the 8s freshness filter is waived —
  treated as foreground-blocked regardless of lastTimeUsed age.
- MonolithShieldService passes the expired set every poll (2s), so
  the stay-put user is re-intercepted on the first poll after expiry.
- VM ticker expiry now runs `expirePeekAndEvict`: closes the lease,
  re-checks foreground with the blindness waived, and if the user is
  still inside, brings Monolith to front + re-fires the Airlock.

### F2 [HIGH] Peek countdown pill never rendered — FIXED-IN-CODE (canonical; cures #101-F3 by cross-ref)
Verified: zero consumers of floatingPeekPill outside the VM on main.
Cure: `PeekCountdownPill` composable (AirlockAndDisarmOverlays.kt) —
app name + lease-derived countdown (`peekPillCountdownText`, pure) +
✕ wired to `end2MinutePeekEarly`; collected and rendered in
MainActivity from `viewModel.floatingPeekPill`.

### F3 [HIGH] Home/Recents escape free + unlogged — FIXED-IN-CODE (trace) + STANDS-BLOCKED (lock-task remainder)
Verified: recordIntercept was called only from RETURN/peek paths;
no abandonment trace existed (Back is now swallowed per #55, but
Home/Recents were still free). Cure: MainActivity's lifecycle
observer calls `viewModel.noteAirlockAbandonedIfStanding()` on
ON_STOP; if an Airlock stands unresolved (RETURN/peek hide it before
any stop, so standing-at-stop = dishonest exit), the VM writes one
intercept-log entry per intercept instance with outcome
"Left Airlock Without Choosing" — intercept count, log and session
reaches counter all move.
STANDS-BLOCKED remainder: a true lock-task / device-owner kiosk
that makes Home physically impossible. Price: device-owner
provisioning + a dedicated lock-task mode + phone verification —
out of this issue's surgical scope; named here, not faked.

### F4 [MEDIUM] Peek doesn't take you to the app — FIXED-IN-CODE
Verified: start2MinutePeek never built a launch intent. Cure: it now
fires `PackageManager.getLaunchIntentForPackage(pkg)` (NEW_TASK,
runCatching) as part of the grant.
**HAND-MERGE FLAG**: start2MinutePeek is a Konto batch-10 hand-merge
surface — the added block is 10 lines, self-contained, flagged here.

### F5 [MEDIUM] 5s wall counts down to nothing for LOCKED — FIXED-IN-CODE
Verified: RETURN TO FOCUS composed unconditionally; LOCKED tier
peekWithheld==null → when(null) rendered Unit. Chosen cure (stated
plainly): **for LOCKED-tier apps, RETURN TO FOCUS becomes live only
at 00** — the delay is the point of the wall, so the wall now gates
the return itself (chip dimmed + click-gated by the pure
`returnToFocusLive(isLockedTier, countdownSecs)`). PEEK tier is
unchanged: its RETURN stays live and its wall still gates the peek
offer. HOLD strengthened, never weakened.
Plumbing: `AirlockInterceptState.isLockedTier`, set in
triggerAirlockIntercept from the row's resolvedTier.

## Tests
New `Issue99PeekJourneyTest` (6 tests): expired-peek detection
(inside window / live lease / never leased / ancient lease), pill
countdown formatting, LOCKED return-gate, PEEK return-live.

## User-facing changes, in plain words
- When your 2-minute peek runs out while you're still in the app,
  you get pulled back to the block screen — the peek really ends.
- While a peek runs in Monolith you now see a pill with the app name
  and the seconds left, and a ✕ to end it early.
- Sneaking out of the block screen with Home is now written into
  the intercept log — it counts, it is no longer invisible.
- Tapping EMERGENCY PEEK opens the app for you — no more burning
  peek seconds finding it yourself.
- For fully locked apps, the RETURN TO FOCUS button only works
  after the 5-second wall finishes — the wait is real now.

## CI
(to be filled after push — see closing comment on #99)

## WEG recovery addendum (cluster lead, Oct 30)

The bundle's commits were lost to a scratch-clone mixup; the complete
change set survived as working-tree edits in this cell and was
verified hunk-by-hunk against this report, then committed by WEG as
**b83255a** on agent/issue-99 — based on CURRENT origin/main (newer
than the bundle's 169f688 base) — and pushed. The test file carries
6 @Test methods (the bundle handoff's "9" counted sub-cases).
F1 honesty note from the lead: the eviction cure is real code (poll
waiver + expirePeekAndEvict), AND the Diagnosticians' audit stands
in the record — Android gives no certain foreground truth for a
motionless user, so the poll treats an expired-peek package as
foreground within a 15-minute window. If the user had already left
to an allowed app, the worst case is an Airlock shown on returning
to Monolith inside that window — bounded, and the lease record is
truthful either way. The certain-foreground source remains priced
at ~1 builder-day + a Sarthak ruling (persistent-notification feel
change) as a follow-up, not a blocker for this disposition.
CI verdict on b83255a: read by WEG before closure (see #99 closing
comment).
