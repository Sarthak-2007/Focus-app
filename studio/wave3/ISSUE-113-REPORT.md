# Issue #113 — Audit R3 · things-go-wrong-lived — Wave-3B report (KARTE)

Branch `agent/issue-113`, base = origin/main `0a00e95`.
All five findings verified against CURRENT code before any fix was
written (audit line numbers had drifted; current locations cited per
finding). Every finding: **FIXED-IN-CODE** on this branch. No finding
was struck false; none is blocked; no security posture was lowered —
the two anti-tamper mechanisms the journey touches (wall/mono drift,
HMAC) are proven intact by test below.

## The seats (20 named lenses + roster)

Bundled honestly into four working seats; every lens named, thin
coverage marked LEAD-COVERED, none silently dropped.

- **Human / Day-fit / User lead** — covers: User, Consumer, Student,
  User-experience, Purpose-questioning, Thoughtful. Walked each
  journey as the man living it: drawer-finish, Settings-revoke,
  late return, honest reboot.
- **Diagnosticians / Verifiers lead (forensic verification)** —
  covers: Deep-thinker, Implementation-quality, Technical execution
  specialist, Brutal critic (evidence standard: a charge files with
  file+line or not at all). Re-derived every mechanism from the row
  fields up; caught that sealSession/advancePastBreak already obey
  the both-anchors law the reboot path broke.
- **Builders / MaxImpl / Technical fix** — covers: Maximum-
  implementation-potential, Crazy-ideas, Synchronous collaboration,
  Interaction-detail specialist. Wrote the fixes + tests.
- **Critics / Fresh-eyes / Keepers of Record** — covers: Improver,
  Designer, Enthusiastic designer, Wow-level designer, Designer with
  29 years of experience, Intentionality-focused designer. Guarded
  the truth standard (no silent security downgrade, no fabricated
  credit), the fonts/palette law (no new surfaces invent colours;
  the one new line reuses the receipt's existing bold register),
  and this record.

## Finding 1 — CRITICAL — Held notifications unreachable after the session ends

**Verdict: FIXED-IN-CODE.**

Verified live in current code: the HELD chip is composed only inside
`if (isSessionRunning && !shotActive)` (TactileDeckScreen.kt §3) and
`toggleNotificationDigestSheet(true)` has exactly one caller in the
app (MainActivity's Deck wiring). The digest machinery itself was
never the problem — an unsealed open is live mode and the repository's
sealed gate lifts when no session is active.

Fix (TactileDeckScreen.kt): on the idle Deck, when
`capturedNotificationsCount > 0`, the same HELD chip (same label,
same count badge, same `onOpenNotificationDigest`) stands full-width
below the running utilities row. Count-gated: an empty capture seats
no door to nothing. User-facing change: after any session — watched
or not — the Deck shows `HELD · n`; the tap opens the digest with
read + clear + delete live again.

Proof: `HeldDoorRenderTest` (Robolectric compose) — idle Deck with
3 captured seats the HELD node; with 0 it seats none.

## Finding 2 — CRITICAL — Revoked permission still prints GUARD 4/4; Deck sentence suppressed

**Verdict: FIXED-IN-CODE (both halves).**

Verified: `guardLinePrint` substituted the seal freeze whenever a
session was active (MonolithViewModel), and the Deck's sentence slot
ran `isSessionRunning -> null` (TactileDeckScreen).

Fix (a) — header: new pure law `resolveGuardLinePrint` in
GuardLine.kt — a MEASURED live print outranks the seal freeze in
both directions (revocation drops the band to `GUARD 3/4 — …` at
once; restoration lifts it back); the freeze survives only as the
UNMEASURED fallback, the one honest job for a past measurement.
`guardLinePrint` now always computes the live print and resolves.
Fix (b) — sentence: the running branch of the sentence slot now
reads `refusalSentenceFor(deckWarrant, nowMono)` instead of forced
null. That function returns null while the wire is whole (so a
healthy session stays silent), names a dead accessibility/usage
grant with its FIX verb the moment the live warrant shows it, and
keeps UNA2-03's stale guard (a stale read prints nothing). The
warrant is re-read by the VM's per-second ticker whenever stale and
by the sweep on Deck entry/resume/FIX return, so a Settings trip
and return lands both halves.

Scope stated plainly: the sentence covers the two grants that own
FIX doors (accessibility, usage); device-admin and notification-
listener revocations are named by the header line (fix a) — they
have no in-app FIX verb to offer, and inventing one was out of
scope. Freshness bound, also plainly: both halves read the Watch
Store / warrant, whose sweep runs on entry/resume/FIX-return and
whose prints go STALE rather than lie when unmeasured — a
revocation is surfaced on return to the app, not while he is still
standing in Settings. That is the store's existing law on every
surface, not a new gap.

Proof: `GuardLineTruthTest` (pure) — frozen-whole vs live-lack,
frozen-lack vs live-whole, freeze fallback on unmeasured, idle =
live. `guardLineFor` itself is unchanged and already table-tested
(ChromeWaveUnitTest).

## Finding 3 — HIGH — Return >10 min after the end discards the finish forever

**Verdict: FIXED-IN-CODE.**

Verified: branch U's `else -> handledFinishIds += row.id`
(MonolithViewModel startFinishController) discarded silently, and
the same handled mark then barred the `publishReceipt(sessionId)`
facade (`if (sessionId in handledFinishIds) return`) — permanence
by construction. sessionHistoryFlow is unbounded (all rows), so the
controller sees every finish the DB holds; the discard was the only
gate.

Fix: branch U now issues at discovery at ANY age —
`ageMs <= 1_800L` witnessed; everything older takes the existing
unwitnessed path (claim + issue, no held frame, no pulse, no
detention). Nothing is fabricated: the slip's facts are the row's
own (endWallMillis stamp per RC-01, sealed elapsed), and the № is
minted at discovery, when it truthfully came into being. The old
"never fabricated into a late №" rationale is answered in the
KDoc at the site. User-facing change: come back an hour later and
the receipt stands on the Deck, numbered, instead of the session
vanishing into silent history minutes.

Proof: code-level (the when's third arm is deleted; the unwitnessed
path it now reaches is the already-tested ≤10-min path —
SledAndDetentionTest / ReceiptClaimRepositoryTest cover the
publish machinery). No VM-harness unit test was practical for the
collector itself; stated, not hidden.

## Finding 4 — HIGH — Honest reboot brands a tamperer, +15 min per block, never mentioned

**Verdict: FIXED-IN-CODE (root + naming). No DECISION-NEEDED: the
cure restores the codebase's own anchor law and weakens nothing.**

Mechanism re-derived from the row: updateSessionProgress's reboot
re-anchor moved mono/boot/accumulated but left
`startWallTimeMillis` at the session's start. Every later tick
therefore compared wall-since-start against mono-since-reboot —
drift permanently > 90s for any session older than ~90 seconds —
tamper stuck, and `penaltyAdd` priced 900s into
`effectiveTargetSeconds`, which `spanTargetSeconds` multiplies by
EVERY block (MonolithModels: span = blocks × effectiveTarget + breaks).

Root fix (MonolithRepository.updateSessionProgress): the re-anchor
now moves BOTH anchors to the re-anchor frame — `startWallTimeMillis`
joins `startElapsedRealtimeMillis` — and the re-sign covers the NEW
pair. This is not a new invention: it is sealSession's stated law
("Both anchors move to the same frame so the mono/wall
cross-check stays consistent") and advancePastBreak's practice;
the reboot path was the one re-anchor in the app that broke it.
The two anchor-carrying writes in the function (stopwatch, final)
carry the new wall anchor; the copies that never carried anchors
(break entry/countdown) are untouched, as before. Drift is now
segment-local: a wall clock dragged inside one boot segment still
reads as tamper (proven by test), an HMAC failure still reads as
tamper (proven by test) — the penalty still exists, still 15
minutes, still once. What died is only the false positive.

Naming fix: the penalty is now a fact on the session's own record.
`penaltySecondsAdded` threads row → PendingReceipt →
ReceiptState (assemble / publish / rehydrate all carry it), and
ReceiptSheet prints, in the shield-gap line's bold register:
`A 15 MIN TAMPER PENALTY WAS ADDED TO THIS SESSION` (minutes from
the field). The PLANNED figure already included the penalty
silently; the slip now says so.

Known consequence, stated not hidden: `startWallTimeMillis` is
also read as the receipt's ARMED instant and the deflection
window's start; a mid-session reboot moves it to the re-anchor
frame. That is the pre-existing anchor semantics — the seal
(≤500ms) and every block advance already move it — extended by
one more re-anchor of the same kind, not a new class of change.

Rows already carrying a false penalty: not rewritten — a stored
900 cannot be distinguished retroactively from a genuine one, and
rewriting records on suspicion would be its own lie. The fix is
prospective; stated here for the record.

Proof: `RebootTruthRepositoryTest.honestRebootBanksOnlyWitnessedTimeAndDrawsNoPenalty`
— re-anchor tick and the (formerly false-tamper) next tick both
clean, HMAC verifies over the new anchors; plus the two
security-intact tests under Finding 5.

## Finding 5 — HIGH — Post-reboot clock credits the phone's entire uptime as focus

**Verdict: FIXED-IN-CODE.**

Verified: the reboot branch returned
`priorElapsed + (nowMono / 1000)` — the device's whole uptime at
the first post-reboot tick, unguarded or not (MonolithRepository
computeVerifiedSessionElapsed; the only uptime-credit site in the
app, grep-verified).

Fix: the branch returns the banked prior elapsed and nothing else;
tamper there is the signature's verdict alone. Downtime and
pre-first-tick uptime are unobserved time, and unobserved time is
not focus time — the session simply runs its remaining witnessed
seconds on the new boot. The function's KDoc point 2 now states
this law where it used to boast the opposite. This also removes
the old receipt oddity the audit flagged (hours credited under a
line that said the shield was off for "part" of the session): with
no uptime credited, ELAPSED_SHIELD_GAP remains a statement about
beats, not a fig leaf over booked uptime.

Proof: `RebootTruthRepositoryTest` — at the pure seam,
computeVerifiedSessionElapsed on a rebooted row returns exactly
the banked 3,490 with ~7,200s of uptime on the clock; the
end-to-end test asserts the same through updateSessionProgress.
Security neighbours in the same file: wall-jump-in-segment draws
the full penalty; forged HMAC reads tamper; plain honest tick
verifies clean at the exact mono figure.

## Files

- `app/src/main/java/com/example/data/MonolithRepository.kt` —
  F5 branch + KDoc; F4 both-anchor re-anchor + re-sign; receipt
  penalty threading (assemble/publish/rehydrate).
- `app/src/main/java/com/example/data/ReceiptState.kt` —
  `penaltySecondsAdded` on PendingReceipt + ReceiptState
  (appended, defaulted — no existing construction breaks).
- `app/src/main/java/com/example/ui/components/ReceiptSheet.kt` —
  the penalty line.
- `app/src/main/java/com/example/ui/components/GuardLine.kt` —
  `resolveGuardLinePrint` + the freeze's-limit KDoc.
- `app/src/main/java/com/example/ui/MonolithViewModel.kt` —
  guardLinePrint resolves live-over-freeze; branch U discards
  nothing.
- `app/src/main/java/com/example/ui/screens/TactileDeckScreen.kt` —
  idle HELD door; running sentence reads the live warrant.
- Tests (new): `RebootTruthRepositoryTest` (4),
  `GuardLineTruthTest` (5), `HeldDoorRenderTest` (2).

No schema change (DB stays v12), no migration, no new colours, no
green, fonts untouched.
