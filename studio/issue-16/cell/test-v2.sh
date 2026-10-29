#!/usr/bin/env bash
# === CHANGE NOTE vs builds/pill/test.sh (10 lines) ===
# 1 Queue gone: swarm queue.sh deleted 2026-10-30 (GitHub move); hard acquire removed.
# 2 Queue now optional if it reappears; PILL_REQUIRE_QUEUE=1 restores hard fail (exit 3).
# 3 Paths moved: pill lane -> this cell (repo/, guh/, logs under the cell, no collisions).
# 4 Verdict pre-written state=RUNNING before Gradle starts, so a kill leaves evidence.
# 5 Log tee-streamed incrementally to test-v2.log instead of one end-of-run redirect.
# 6 Watcher snapshots completed test names from the JUnit XML dir every 2s to a file.
# 7 Traps (INT/TERM/HUP) write INTERRUPTED verdicts; SIGKILL is covered by 4+5+6.
# 8 Kept verbatim: env21.sh/JDK21, own-daemon gradle --stop + sleep 4, same Gradle task.
# 9 Kept contract: last stdout line PILL_TEST_DONE test=<code>; exit now mirrors it.
#
# ============================================================================
# test-v2.sh - Pastille (issue-16) pill lane: unit-test RE-RUN script.
# Finisher-2 (issue-16 cell)
#
# WHAT IT DOES - exactly, in order:
#   1. Resolves every path from its own location (the issue-16 cell dir),
#      so it works no matter what directory Ren invokes it from.
#   2. Truncates/creates three evidence files in the cell dir (below) and
#      writes the verdict file IMMEDIATELY with state=RUNNING - before any
#      Gradle process exists. This pre-write is the kill-survival mechanism.
#   3. If the retired swarm queue ever reappears at its old path, acquires
#      the "pill" slot from it (timeout 3600s) and releases it on exit.
#      Absent (the normal case since 2026-10-30) it logs one line and runs
#      without it. It never blocks forever on a missing file.
#   4. Sources ~/workspace/clone-inspect/env21.sh (JDK21 discipline, kept
#      verbatim from the old script), exports this cell's own
#      GRADLE_USER_HOME=<cell>/guh, stops THIS cell's Gradle daemon
#      (gradle --stop) and sleeps 4s - the JDK-switch discipline: never let
#      a daemon started under another JDK serve the JDK21 test run.
#   5. Runs exactly one Gradle task in <cell>/repo:
#          gradle :app:testDebugUnitTest --console=plain
#      streaming output through tee into the log, line-by-line as it comes.
#   6. While Gradle runs, a background watcher polls the JUnit XML dir
#      every 2s and rewrites test-v2.completed.txt with the sorted, unique
#      "classname.name" of every <testcase> finished so far.
#   7. On normal completion: final XML scan, verdict rewritten with
#      state=DONE and the real test exit code, queue released if held.
#
# WHAT REN SHOULD EXPECT WHEN HE FIRES IT:
#   - Fire it with ONE command, no arguments, from anywhere:
#         bash ~/workspace/clone-inspect/builds/cells/issue-16/test-v2.sh
#     (It is also chmod +x, so direct execution works too.)
#   - Nobody has compiled or run this script before him: it was written
#     and syntax-checked only, per the cell's no-compile rule.
#   - It runs the FULL debug unit-test suite, foreground, until Gradle
#     itself finishes. No timeout is imposed by this script (other than
#     the optional queue's 3600s acquire timeout).
#   - stdout mirrors the log. THE LAST LINE OF STDOUT IS ALWAYS the
#     machine-readable verdict, in exactly this shape:
#         PILL_TEST_DONE test=<code>
#     where <code> is Gradle's own exit code (0 = suite green).
#   - The process exit code mirrors <code>: 0 green, non-zero = that code.
#     Special codes: 9 = repo dir missing (nothing ran),
#     3 = queue acquire failed AND PILL_REQUIRE_QUEUE=1 was set,
#     130/143 = interrupted by SIGINT/SIGTERM (verdict says INTERRUPTED).
#   - Files left in the cell dir after ANY run, finished or killed:
#         test-v2.log           full Gradle output, streamed incrementally
#         test-v2.verdict       key=value state file (see below)
#         test-v2.completed.txt test names finished before the end/death
#   - IF THE ENVIRONMENT DIES MID-RUN (SIGKILL, VM death, power): no
#     process can run cleanup - that is expected, not a script failure.
#     Recovery, in order: (a) test-v2.verdict still reads state=RUNNING
#     with the start time and PID - that alone proves the run never
#     finished; (b) test-v2.log holds every line tee flushed up to the
#     death; (c) test-v2.completed.txt holds the tests known complete,
#     and the authoritative source remains the JUnit XML dir itself:
#         repo/app/build/test-results/testDebugUnitTest/*.xml
#     (present only if Gradle got far enough to write results; if it is
#     absent, the log + completed list are the whole recoverable record).
#   - Verdict file fields: state (RUNNING|DONE|INTERRUPTED|FAILED),
#     test (Gradle code, empty while running), started/finished
#     (ISO-8601 local), pid, log, completed, xml_dir, queue
#     (held|skipped-absent|skipped-failed|not-attempted).
# ============================================================================
set -u

CELL_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO="$CELL_DIR/repo"
LOG="$CELL_DIR/test-v2.log"
VERDICT="$CELL_DIR/test-v2.verdict"
COMPLETED="$CELL_DIR/test-v2.completed.txt"
XML_DIR="$REPO/app/build/test-results/testDebugUnitTest"
QUEUE_SH="$HOME/workspace/clone-inspect/design-refs/swarm/tools/queue.sh"
QUEUE_HELD=0
QUEUE_STATE="not-attempted"
STARTED="$(date -Is)"
WATCHER=""

write_verdict() {
  # write_verdict <state> <test-code-or-empty> <finished-or-empty>
  # Written via tmp+mv so a reader (or a post-mortem) never sees a torn file.
  {
    printf 'state=%s\n' "$1"
    printf 'test=%s\n' "$2"
    printf 'started=%s\n' "$STARTED"
    printf 'finished=%s\n' "$3"
    printf 'pid=%s\n' "$$"
    printf 'log=%s\n' "$LOG"
    printf 'completed=%s\n' "$COMPLETED"
    printf 'xml_dir=%s\n' "$XML_DIR"
    printf 'queue=%s\n' "$QUEUE_STATE"
  } > "$VERDICT.tmp"
  mv -f "$VERDICT.tmp" "$VERDICT"
}

scan_completed() {
  # Extract finished test names from whatever JUnit XML exists right now.
  # No XML yet is a normal state, not an error: the list is just empty.
  if [ -d "$XML_DIR" ]; then
    grep -h '<testcase ' "$XML_DIR"/*.xml 2>/dev/null \
      | sed -n 's/.*classname="\([^"]*\)".* name="\([^"]*\)".*/\1.\2/p; s/.*name="\([^"]*\)".* classname="\([^"]*\)".*/\2.\1/p' \
      | sort -u > "$COMPLETED.tmp" 2>/dev/null || true
    mv -f "$COMPLETED.tmp" "$COMPLETED"
  fi
}

release_queue() {
  if [ "$QUEUE_HELD" = "1" ]; then
    bash "$QUEUE_SH" release pill > /dev/null 2>&1 || true
    QUEUE_HELD=0
  fi
}

finish() {
  # finish <state> <code> - single exit door: final scan, verdict, last line.
  local state="$1" code="$2"
  [ -n "$WATCHER" ] && kill "$WATCHER" > /dev/null 2>&1
  scan_completed
  release_queue
  write_verdict "$state" "$code" "$(date -Is)"
  echo "PILL_TEST_DONE test=$code"
  exit "$code"
}

trap 'echo "INTERRUPTED sigint" >> "$LOG" 2>/dev/null; finish INTERRUPTED 130' INT
trap 'echo "INTERRUPTED sigterm" >> "$LOG" 2>/dev/null; finish INTERRUPTED 143' TERM
trap 'echo "INTERRUPTED sighup" >> "$LOG" 2>/dev/null; finish INTERRUPTED 129' HUP

# --- Evidence files first: from this line on, a kill leaves a record. ---
: > "$LOG"
: > "$COMPLETED"
write_verdict RUNNING "" ""

log_line() { printf '%s\n' "$*" | tee -a "$LOG"; }

if [ ! -d "$REPO" ]; then
  log_line "FATAL repo dir missing: $REPO"
  finish FAILED 9
fi
cd "$REPO" || finish FAILED 9

# --- Optional queue: honoured if it ever returns, never required. ---
if [ -f "$QUEUE_SH" ]; then
  if bash "$QUEUE_SH" acquire pill --timeout 3600 >> "$LOG" 2>&1; then
    QUEUE_HELD=1
    QUEUE_STATE="held"
    log_line "queue: acquired pill slot via $QUEUE_SH"
  elif [ "${PILL_REQUIRE_QUEUE:-0}" = "1" ]; then
    QUEUE_STATE="required-failed"
    log_line "ACQUIRE FAILED (PILL_REQUIRE_QUEUE=1)"
    finish FAILED 3
  else
    QUEUE_STATE="skipped-failed"
    log_line "queue: acquire failed, continuing without it (set PILL_REQUIRE_QUEUE=1 to make this fatal)"
  fi
else
  QUEUE_STATE="skipped-absent"
  log_line "queue: $QUEUE_SH absent (retired 2026-10-30) - running without it"
fi
write_verdict RUNNING "" ""

# --- JDK21 discipline (kept from pill/test.sh): env, own GUH, stop own daemon. ---
# shellcheck disable=SC1091
source "$HOME/workspace/clone-inspect/env21.sh"
export GRADLE_USER_HOME="$CELL_DIR/guh"
mkdir -p "$GRADLE_USER_HOME"
gradle --stop > /dev/null 2>&1 || ./gradlew --stop > /dev/null 2>&1 || true
sleep 4

# --- Watcher: snapshot completed test names from JUnit XML every 2s. ---
( while :; do scan_completed; sleep 2; done ) &
WATCHER=$!

log_line "== testDebugUnitTest (JDK21, full suite) =="
gradle :app:testDebugUnitTest --console=plain 2>&1 | tee -a "$LOG"
T=${PIPESTATUS[0]}
echo "TEST_EXIT=$T" >> "$LOG"

finish DONE "$T"
