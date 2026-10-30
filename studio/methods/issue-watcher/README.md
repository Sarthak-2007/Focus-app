# Issue Watcher — Sarthak's GitHub comments, surfaced to Ren

**Why this exists.** On Oct 30, 2026, Sarthak's own comment on issue #3
(comment 6509136030 — "What is the problem guya, does this repo needs to be
public?- Sarthak") sat unanswered for hours because *nothing watched the
issues*. He complained; this watcher is the cure. It polls
`Sarthak-2007/Focus-app` for his comments and prints each one to Ren —
exactly once, never silently broken.

## What it does

`watch.sh` polls two comment streams via `gh api` (gh CLI + PAT only —
**never the GitHub connector**, per standing law):

- `GET /repos/Sarthak-2007/Focus-app/issues/comments` (issue comments;
  PR *conversation* comments included — PRs are issues in this API)
- `GET /repos/Sarthak-2007/Focus-app/pulls/comments` (PR review comments)

**Whose comment is it?** Every agent in the studio posts under Sarthak's
login too (single PAT identity, signed in the text). Author login alone
would flood Ren with agent filings. So a comment counts as *Sarthak's own*
when it carries **no studio signature**: agent filings are headed documents
(first line a `#` heading or `**bold header**`) or carry structural markers
("efficiency account", "inquisition", "closure evidence", "ci run <n>",
"agent/issue-", "under ren", "signed", "disposition:", …). His comments are
plain prose. The bias is deliberate: anything unclassifiable surfaces —
missing one of his comments is the failure this tool exists to kill; one
extra surfaced filing costs Ren a glance. `WATCHER_SURFACE_ALL=1` disables
the filter (debug).

Each surfaced comment prints as: summary first line (`SARTHAK COMMENTED —
N new comment(s) … : #a #b`), then per comment a block with issue number,
comment id, UTC + IST time, URL, a **draft routing** line (from
`routing.tsv` beside the script — which lane owns that issue), and his
**full text, verbatim**.

## Output contract (this is what cron delivers to Ren)

| Case | Exit | Output |
|---|---|---|
| Nothing new | 0 | **nothing** — total silence |
| New comment(s) | 0 | summary line + blocks (above) |
| Recovery after a failure | 0 | one `ISSUE-WATCHER RECOVERED` line (plus blocks if comments also landed) |
| gh auth / API / timeout failure | 3 | one `ISSUE-WATCHER FAILURE` line: class, detail, the action to take |
| State file corrupt | 5 | one `STATE CORRUPT` line; the watcher refuses to guess a watermark |

Failure alarms **dedupe**: the first failure of a class alarms; repeats go
to the run log only and re-alarm every 6 hours, so a day-long outage is
one alarm + reminders, not a flood. The watermark **never advances on
failure** — when gh comes back, the same window is retried. Nothing is
skipped, ever; the trade is duplicates-are-impossible, delay-is-possible.

## The watermark law

State lives **outside the repo** (a state file in the repo would mean a
commit + push per poll, and merge noise in the studio's home):

```
~/workspace/goals/monolith-focus-app-revamp/hidden_files/issue-watcher/
  state.json     per-stream {watermark, seen_ids[]} + failure record
  surfaced.log   append-only audit: one line per surfaced comment
  dropped.log    append-only audit: his-login comments the classifier
                 judged to be agent filings (id + issue + time) — the
                 recovery path if the classifier ever misjudges HIM
  runs.log       one line per poll / failure / suppression / lock-skip
```

Three silences, all distinguishable in the logs: nothing new (a `POLL ok`
line), a poll skipped because another is running (`LOCK-SKIP` line), and a
comment dropped by the classifier (`dropped.log` line). On the chat
surface all three are correctly silent; in the record, none is invisible.

Per stream: `watermark` = newest `created_at` fetched so far, and it
**never moves backwards**. Fetching is **paged** (100/page, ascending)
and **state commits per page**: each page is classified, its new comments
composed into the output, `surfaced.log`/`dropped.log` appended, and the
new watermark + id ring written (temp-file + atomic rename) — *then* the
next page is fetched. Per page the order is fixed: fetch → surface →
log → commit; any other order is a bug. Why per page: after a long gh
outage the catch-up window can hold thousands of comments, and an
all-or-nothing fetch dies whole at its timeout, forever (the live Oct 30
blackout demonstrated exactly this livelock against the first build).
Paged commits drain the backlog across polls; a failure mid-drain is
loud, keeps every committed page, prints that page's comments under the
alarm line, and retries only the remainder. The overlap (`since =
watermark − 120s`) plus the `seen_ids` ring (last 1000) make re-fetched
pages duplicate-free; the ring only has to cover the overlap, so 1000
ids is orders of headroom. (Design note: AKTE's records design keyed the
watermark on the last *surfaced* comment instead; the built law —
stream-position watermark + id ring, committed per page — gives the same
no-skip/no-dupe guarantee.)

**Edit policy:** comments are keyed on `created_at` + id. An *edit* to an
already-surfaced comment does **not** resurface (decided — STICH's charge,
answered): his questions are new comments; resurfacing edits would spam.
A comment he writes is surfaced as first posted.

**First run / reseeding:** with no state file, `watch.sh` seeds the
watermark at *now* and polls from there — history is never flooded into
the chat. `watch.sh --seed-at <ISO8601Z>` seeds at a chosen instant
(used by the proof below). To reseed by hand: stop the cron, delete
`state.json`, run once. Production was seeded 2026-10-30T05:01:27Z.

## Failure modes (what Ren sees, what to do)

- `class=AUTH` — token revoked/expired or wrong account. Action in the
  alarm line: `gh auth status`, re-login. (The watcher also verifies the
  login is `Sarthak-2007`, not just *a* login.)
- `class=TIMEOUT` — this box's egress proxy hangs in waves; `gh` calls are
  wrapped in `timeout` (60s preflight, 300s fetch) so a hang becomes a
  loud class, never a silent stall.
- `class=API` — non-2xx from the endpoint (incl. 403/429 rate limits):
  hard stop, no tight retry — the cron interval is the retry. The alarm
  carries gh's own first error line; nothing is discarded silently.
- `class=LOCAL` — local processing (jq) failed on fetched data; state
  was NOT advanced. Re-run; if it repeats, the fetched payload is the bug.
- `STATE CORRUPT` (exit 5) — inspect `state.json`; reseed as above.
- Exit 2 — bad arguments (e.g. a malformed or future `--seed-at`, or any
  flag while state exists). Nothing was seeded or changed.
- Known environmental limit: during this box's write-EPERM waves even
  stdout writes can die; a failure line lost that way is visible in
  `runs.log` once writes heal (the failure record in state lags one poll).

## Proof

- `test-offline.sh` (beside the script) stubs `gh` and proves the logic
  deterministically: **19/19** — seed comment surfaces with full text on
  #3, agent filing skipped *and audit-logged in `dropped.log`*, second
  run silent, auth break exits 3 with the alarm line, corrupt state
  exits 5, garbage/future seeds exit 2 seeding nothing, watermark
  advances and never regresses — plus the multipage lifecycle: a full
  100-comment page commits, a page-2 failure alarms *with page 1's
  comments printed under the alarm*, the next poll resumes and surfaces
  only page 2's comment (no duplicates), and the drained window goes
  silent.
- Live seed proof: scratch state seeded at 2026-10-29T21:49:11Z (one
  minute before the seed comment) — first poll must surface comment
  6509136030, second poll silence. Run during the Oct 30 egress blackout,
  every live attempt failed loudly at preflight (class=TIMEOUT, watermark
  unmoved — the failure half of the contract, proven live repeatedly);
  the happy-path live run is re-attempted until the wave turns and its
  result is appended here.

## Schedule wiring (Ren installs in one step)

Cron in this runtime is an **agent task** whose body runs the script
(TAKT's finding — a cron body cannot be a bare script). One `cron.add`
(check `cron.list` first for an existing `monolith-issue-watcher`):

- id `monolith-issue-watcher`, mode `task`, owner
  `goal:monolith-focus-app-revamp`, enabled
- schedule: interval, every `10m`, timezone `Asia/Kolkata`
  (steady-state cost ≈ 2 API calls/poll ≈ 12/hr of the 5,000/hr budget;
  worst-case delay before Sarthak is heard ≈ 10 min. Drop to `20m` via
  `cron.update` only on evidence of waste — no reseed needed.
  Worst-case single run ≈ 11 min (60s preflight + 2 × 300s fetches), so
  a slow poll can overlap the next tick: the flock makes the second one
  a logged no-op (`LOCK-SKIP`), never a double surface.)
- delivery: the clone chat `cfacc00d-a202-4579-9fad-525710c440e4`
- timeout_secs 1200, predicted_connector_permissions `[]` (gh route only)
- body: "Run `~/workspace/clone-inspect/Focus-app/studio/methods/issue-watcher/watch.sh`
  (no arguments). Exit 0 with no output → end with NO message. Output
  beginning `SARTHAK COMMENTED` → your final message is that output,
  verbatim, nothing added. Output beginning `ISSUE-WATCHER FAILURE` or a
  non-zero exit → your final message is that line, verbatim. Never post
  or write anything on GitHub; never reseed the state."

## Design provenance

Built by MELDER with 12 seats (Methods-reader SPUR, Diagnosticians LAUT +
TAKT, Builders ZUG + WEICHE, Critics STICH + RUHE, Verifiers MASS + GRUND,
Keeper AKTE, Fresh-eyes FREMDE, Maximizer MAXIM). Notable inputs: ZUG's
route verification (issues/comments covers PR conversation comments;
pulls/comments priced as an additive stream — included here because the
brief names PRs), STICH's hybrid watermark verdict (adopted), RUHE's
output contract + alarm dedupe (adopted), TAKT's cron shape (adopted),
SPUR's finding that the Methods commission's METHODS-GITHUB.md **does not
exist** anywhere (no design to adopt; its adjacent advice — conditional
polling, hard stop on 403/429 — is honoured by the `since` window and the
fail-hard law). MAXIM's priced extensions (issue/PR bodies, state-change
events, burst digest) are recorded in the build report as deliberate
deferrals, not omissions.
