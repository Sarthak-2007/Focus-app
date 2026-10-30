#!/usr/bin/env bash
# queue.sh — Law 3 compile-queue keeper (Queue Protocol v2)
# Owner: Stellwerk, Methods (queue keepership). Seated Sweep 2, Oct 29 2026.
#
# WHY: v1 flags were empty files claimed by `touch` after a racy
# `ls compile-*.flag | wc -l` check. On Oct 29, 10:13 IST, three lanes
# held flags at once under a two-flag law (audit: methods/queue-waits.md).
# v2 makes the flag a record and the claim atomic.
#
# FLAG FORMAT (builds/compile-<lane>.flag, one key=value per line):
#   lane=<lane>            owner lane
#   pid=<pid>              PID of the lane's WRAPPER shell — the process
#                          that invoked queue.sh and will run gradle.
#                          queue.sh itself exits; the wrapper is the
#                          life of the run, so the wrapper is tracked.
#   started=<epoch>        acquire time (seconds since epoch)
#   started_ist=<string>   human time, for the wall
#   host=<hostname>
#
# SUBCOMMANDS:
#   queue.sh acquire <lane> [--timeout N]   atomic claim; polls 15s up
#                                           to N sec (default 1800),
#                                           then exits 3. Call from the
#                                           wrapper that runs gradle.
#   queue.sh release <lane>                 remove own flag (idempotent)
#   queue.sh status                         table of flags: age, PID
#                                           life, verdict.
#   queue.sh reap-stale [--dry-run]         remove provably stale flags:
#                                           v2 flag, PID dead, age>60s;
#                                           legacy empty flag, age>1200s.
#                                           Never touches a live PID.
#
# STALE CONSTANTS (priced from the Oct 29 audit, 92 timed runs):
#   median run 133s, p90 264s, p95 588s, max 960s.
#   DEAD_PID_GRACE=60    a dead PID + 60s grace cannot be a live run.
#   LEGACY_STALE=1200    20 min > max observed run (960s) + 25% margin.
#   HARD_CAP=1800        live-PID flag older than this is REPORTED as
#                        HUNG by status; reap never kills a live PID.
#
# ENV: QUEUE_DIR overrides the flag directory (default: studio builds/).
# Used by tests; lanes should never set it.
#
# EXIT CODES: 0 ok · 2 usage · 3 acquire timeout

set -u
QUEUE_DIR="${QUEUE_DIR:-$HOME/workspace/clone-inspect/builds}"
MAX_FLAGS=2
DEAD_PID_GRACE=60
LEGACY_STALE=1200
HARD_CAP=1800
POLL=15

now() { date +%s; }
flagpath() { echo "$QUEUE_DIR/compile-$1.flag"; }
flag_field() { [ -f "$1" ] || return 0; grep -m1 "^$2=" "$1" 2>/dev/null | cut -d= -f2-; }
flag_age() { local m; m=$(stat -c %Y "$1" 2>/dev/null) || { echo -1; return; }; echo $(( $(now) - m )); }
pid_alive() { [ -n "$1" ] && kill -0 "$1" 2>/dev/null; }

verdict() {
  local f="$1" pid age
  pid=$(flag_field "$f" pid); age=$(flag_age "$f")
  if [ -z "$pid" ]; then
    if [ "$age" -gt "$LEGACY_STALE" ]; then echo "STALE-LEGACY"; else echo "LEGACY(age<${LEGACY_STALE}s,unverifiable)"; fi
    return
  fi
  if pid_alive "$pid"; then
    if [ "$age" -gt "$HARD_CAP" ]; then echo "HUNG?(pid alive, age>${HARD_CAP}s)"; else echo "LIVE"; fi
  else
    if [ "$age" -gt "$DEAD_PID_GRACE" ]; then echo "STALE-DEADPID"; else echo "DYING?(pid dead, in 60s grace)"; fi
  fi
}

count_flags() { ls "$QUEUE_DIR"/compile-*.flag 2>/dev/null | wc -l; }

cmd_status() {
  echo "queue dir: $QUEUE_DIR   cap: $MAX_FLAGS   now: $(TZ=Asia/Kolkata date '+%H:%M:%S IST')"
  local n; n=$(count_flags)
  echo "flags present: $n"
  local f
  for f in "$QUEUE_DIR"/compile-*.flag; do
    [ -e "$f" ] || continue
    local lane pid started age v
    lane=$(basename "$f" .flag); lane=${lane#compile-}
    pid=$(flag_field "$f" pid); started=$(flag_field "$f" started_ist)
    age=$(flag_age "$f"); v=$(verdict "$f")
    printf "  %-14s pid=%-8s started=%-14s age=%ss  %s\n" "$lane" "${pid:-none}" "${started:-?}" "$age" "$v"
  done
  if [ "$n" -gt "$MAX_FLAGS" ]; then
    echo "VERDICT: LAW 3 BREACH IN PROGRESS — $n flags over a cap of $MAX_FLAGS."
  elif [ "$n" -eq 0 ]; then echo "VERDICT: queue empty."
  else echo "VERDICT: within cap (but see per-flag verdicts — a STALE flag is a lie, not a slot)."
  fi
}

cmd_reap() {
  local dry=0; [ "${1:-}" = "--dry-run" ] && dry=1
  local f reaped=0
  for f in "$QUEUE_DIR"/compile-*.flag; do
    [ -e "$f" ] || continue
    local v; v=$(verdict "$f")
    case "$v" in
      STALE-DEADPID|STALE-LEGACY)
        if [ "$dry" -eq 1 ]; then echo "would reap: $f ($v, age $(flag_age "$f")s)"
        else echo "reaped: $f ($v, age $(flag_age "$f")s)"; rm -f "$f"; fi
        reaped=$((reaped+1));;
    esac
  done
  echo "reap-stale: $reaped stale flag(s)${dry:+ [dry-run]}"
}

cmd_acquire() {
  local lane="${1:-}"; [ -n "$lane" ] || { echo "usage: queue.sh acquire <lane> [--timeout N]" >&2; exit 2; }
  local timeout=1800
  [ "${2:-}" = "--timeout" ] && timeout="${3:-1800}"
  local f; f=$(flagpath "$lane")
  # Re-entrant: my wrapper already holds this lane's live flag.
  if [ -f "$f" ] && [ "$(flag_field "$f" pid)" = "$PPID" ]; then echo "already holding $f"; return 0; fi
  local waited=0
  while :; do
    # Atomic claim: noclobber create fails if the file exists — no race.
    if ( set -o noclobber; {
        echo "lane=$lane"; echo "pid=$PPID"; echo "started=$(now)"
        echo "started_ist=$(TZ=Asia/Kolkata date '+%Y-%m-%d %H:%M:%S IST')"
        echo "host=$(hostname)"
      } > "$f" ) 2>/dev/null; then
      # Slot check AFTER claim: over cap, the YOUNGEST flag yields
      # (deterministic — no honour required).
      local others=0 of
      for of in "$QUEUE_DIR"/compile-*.flag; do
        [ "$of" = "$f" ] && continue
        [ "$(stat -c %Y "$of")" -le "$(stat -c %Y "$f")" ] && others=$((others+1))
      done
      if [ "$others" -lt "$MAX_FLAGS" ]; then
        echo "acquired: $f (wrapper pid $PPID, waited ${waited}s)"; return 0
      fi
      rm -f "$f"
    fi
    if [ "$waited" -ge "$timeout" ]; then
      echo "acquire TIMEOUT after ${waited}s for lane $lane — queue state:" >&2
      cmd_status >&2; exit 3
    fi
    cmd_reap >/dev/null 2>&1 || true
    local nap=$POLL remaining=$((timeout-waited))
    [ "$remaining" -lt "$nap" ] && nap=$remaining
    [ "$nap" -lt 1 ] && nap=1
    sleep "$nap"; waited=$((waited+nap))
  done
}

cmd_release() {
  local lane="${1:-}"; [ -n "$lane" ] || { echo "usage: queue.sh release <lane>" >&2; exit 2; }
  local f; f=$(flagpath "$lane")
  [ -f "$f" ] || { echo "no flag for $lane (already released)"; return 0; }
  rm -f "$f"; echo "released: $f"
}

case "${1:-}" in
  acquire) shift; cmd_acquire "$@";;
  release) shift; cmd_release "$@";;
  status)  cmd_status;;
  reap-stale) shift; cmd_reap "$@";;
  *) echo "usage: queue.sh {acquire <lane> [--timeout N]|release <lane>|status|reap-stale [--dry-run]}" >&2; exit 2;;
esac
