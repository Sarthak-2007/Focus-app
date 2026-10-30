# Issue #100 — storage CUT, executed (DURCH-C closure seat)

Sarthak's ruling (Oct 30): notification-content storage is CUT.
For every finding about storing captured notification content, the
fix is REMOVAL of the storage, not repair. PR #347's wave-3 branch
implemented the opposite direction (repair: fuller capture,
session-scoped digest, uncapped stored count) and was NOT merged;
this branch replaces it wholesale.

## What the held experience becomes
Held = SUPPRESSED, not transcribed. A blocked app's notification
during a session is cancelled out of the shade and nothing about
it is stored anywhere — no title, no body, no row, no digest.
There is no sheet to open afterwards because there is nothing to
show. The only retained fact is an in-memory, session-scoped
count of suppressions (HeldSuppressionCounter), which the shield
notification's badge prints; it dies with the process.

## Findings
1. CRITICAL (post-delivery buzz / async cancel): platform delivery
   before any listener callback is architectural and cannot be
   un-rung; the app's own latency is removed — the cancel decision
   now runs on an in-memory cache (covering session + app rules,
   10s TTL, background refresh) and fires inside onNotificationPosted
   itself on a warm cache. Cold/stale cache = the old DB slow path,
   which warms the cache. Honest limit stated in the listener doc.
2. HIGH (held = deleted, digest a dead transcript): CURED BY CUT.
   CapturedNotificationEntity, the captured_notifications table
   (dropped by MIGRATION_14_15, DB v15), all DAO capture queries,
   repository capture API, the digest sheet, the frozen digest,
   and the Deck HELD door are all deleted.
3. HIGH (digest/badge not session-scoped): the digest is gone;
   the surviving badge count is the suppression counter, keyed by
   session id — a new session resets it, other sessions read 0.
4. MEDIUM (body outside EXTRA_TEXT walks through): body/title are
   now read from every carrier (EXTRA_TEXT, EXTRA_BIG_TEXT,
   EXTRA_TEXT_LINES, MessagingStyle EXTRA_MESSAGES, EXTRA_SUB_TEXT,
   EXTRA_TITLE_BIG) before the suppression judgement.
5. MEDIUM (HELD count capped at 100): no list exists to cap — the
   badge reads an uncapped counter.

Never-suppress classes preserved: calls/alarms (policy), media
playback (#132), FINANCE apps and one-time codes (#120 — the code
now simply stays in the shade; nothing is copied either).

Tests: NotificationListenerCaptureTest rewritten as suppression
tests (counted-not-stored, big-text body, OTP never suppressed);
storage-only tests deleted (FrozenDigestGate, HeldDoorRender,
NotificationRetentionDao, TruthCar3CaptureBody); migration chains
extended to v15 with a captured-table-absence assertion.
