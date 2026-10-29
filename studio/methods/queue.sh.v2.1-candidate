#!/usr/bin/env bash
# queue.sh — Law 3 compile-queue keeper (Queue Protocol v2)
# Owner: Stellwerk, Methods (queue keepership). Seated Sweep 2, Oct 29 2026.
#
# WHY: v1 flags were empty files claimed by `touch` after a racy
# `ls compile-*.flag | wc -l` check. On Oct 29, 10:13 IST, three lanes
# held flags at once under a two-flag law (audit: methods/queue-waits.md).
# v2 makes the flag a record and the claim atomic.
#
# v2.1 (candidate, Oct 29 — Stellwerk, WORK-QUEUE item 15; NOT YET
# SWAPPED IN): the v2 claim still wrote the record INTO the flag
# name — the noclobber redirect created the file first and the
# record second, and a write refused between the two minted a
# 0-byte phantom that blocked its own lane for LEGACY_STALE
# seconds (Auslese's depcull claim, Oct 29 21:14:32, free RAM
# 171 MB; anatomy + proof: methods/queue-claim-fix.md). v2.1
# claims WRITE-THEN-RENAME: the record is written to a temp name
# and hardlinked into place only once complete, so the flag name
# only ever exists carrying its record. Same revision: the slot
# check no longer errors on a flag that vanishes mid-check
# (warrant, Oct 29 23:07:23); reap-stale leaves a
# reaped-<lane>.note for the daemon sweep (mail/wartung/1028)
# and clears dead claimants' temp litter; status counts live
# records and record-less corpses separately. Constants,
# subcommands, exit codes and the youngest-yields rule are
# UNCHANGED from v2. Legacy (v1 touch / homebrew) flags remain
# readable and reapable; a record-less flag is a corpse-in-
# waiting, never a claim.
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
#                                           v2.1: a reaped flag leaves
#                                           reaped-<lane>.note beside
#                                           the flags; dead claimants'
#                                           .claim-*.tmp litter older
#                                           than 60s is cleared too.
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
  local f records=0 corpses=0
  for f in "$QUEUE_DIR"/compile-*.flag; do
    [ -e "$f" ] || continue
    local lane pid started age v
    lane=$(basename "$f" .flag); lane=${lane#compile-}
    pid=$(flag_field "$f" pid); started=$(flag_field "$f" started_ist)
    age=$(flag_age "$f"); v=$(verdict "$f")
    printf "  %-14s pid=%-8s started=%-14s age=%ss  %s\n" "$lane" "${pid:-none}" "${started:-?}" "$age" "$v"
    if [ -n "$pid" ]; then records=$((records+1)); else corpses=$((corpses+1)); fi
  done
  local debris=0 t
  for t in "$QUEUE_DIR"/.claim-*.tmp; do [ -e "$t" ] && debris=$((debris+1)); done
  [ "$debris" -gt 0 ] && echo "claim temp debris: $debris (dead claimants' litter — reap-stale clears it past ${DEAD_PID_GRACE}s)"
  if [ "$records" -gt "$MAX_FLAGS" ]; then
    echo "VERDICT: LAW 3 BREACH IN PROGRESS — $records live claims over a cap of $MAX_FLAGS."
  elif [ "$n" -gt "$MAX_FLAGS" ]; then
    echo "VERDICT: over cap on paper only — $records live claim(s) within cap + $corpses record-less legacy flag(s). A corpse is a lie, not a slot."
  elif [ "$n" -eq 0 ]; then echo "VERDICT: queue empty."
  else echo "VERDICT: within cap (but see per-flag verdicts — a STALE flag is a lie, not a slot)."
  fi
}

cmd_reap() {
  local dry=0; [ "${1:-}" = "--dry-run" ] && dry=1
  # v2.1 print fix: v2.0's summary used ${dry:+ [dry-run]}, which
  # expands for dry=0 too (the string "0" is non-empty) — the label
  # printed on real reaps. Suffix is set only for a true dry run.
  local drysfx=""; [ "$dry" -eq 1 ] && drysfx=" [dry-run]"
  local f reaped=0
  for f in "$QUEUE_DIR"/compile-*.flag; do
    [ -e "$f" ] || continue
    local v; v=$(verdict "$f")
    case "$v" in
      STALE-DEADPID|STALE-LEGACY)
        if [ "$dry" -eq 1 ]; then echo "would reap: $f ($v, age $(flag_age "$f")s)"
        else
          echo "reaped: $f ($v, age $(flag_age "$f")s)"
          # Item 15 note (Wartung seam, mail/wartung/1028): a reaped
          # flag leaves a note naming lane + wrapper PID + age, so
          # the daemon sweep can hunt the run's Gradle daemon —
          # reaping frees the slot, only the sweep frees the RAM.
          local rlane; rlane=$(basename "$f" .flag); rlane=${rlane#compile-}
          { echo "lane=$rlane"; echo "class=$v"; echo "pid=$(flag_field "$f" pid)"
            echo "age=$(flag_age "$f")"; echo "reaped_ist=$(TZ=Asia/Kolkata date '+%Y-%m-%d %H:%M:%S IST')"
          } > "$QUEUE_DIR/reaped-$rlane.note" 2>/dev/null || true
          rm -f "$f"; fi
        reaped=$((reaped+1));;
    esac
  done
  # v2.1: claim temp litter. A .claim-*.tmp older than DEAD_PID_GRACE
  # is a dead claimant's remains (write+link is a single breath; a
  # live claim is never 60s old). Younger temps are in-flight claims
  # and are never touched.
  local t reaped_t=0
  for t in "$QUEUE_DIR"/.claim-*.tmp; do
    [ -e "$t" ] || continue
    if [ "$(flag_age "$t")" -gt "$DEAD_PID_GRACE" ]; then
      if [ "$dry" -eq 1 ]; then echo "would reap claim temp: $t (age $(flag_age "$t")s)"
      else echo "reaped claim temp: $t (age $(flag_age "$t")s)"; rm -f "$t"; fi
      reaped_t=$((reaped_t+1))
    fi
  done
  echo "reap-stale: $reaped stale flag(s)$drysfx"
  [ "$reaped_t" -gt 0 ] && echo "reap-stale: $reaped_t claim temp(s)$drysfx"
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
    # v2.1 claim: WRITE-THEN-RENAME (see header). The record is
    # written to a temp name in the same directory; only a complete
    # record is hardlinked onto the flag name — link(2) fails if
    # the flag already exists, so the claim stays atomic and a
    # refused or killed write mints nothing at the flag name.
    local tmp="$QUEUE_DIR/.claim-$lane.$PPID.$$.tmp"
    if { echo "lane=$lane"; echo "pid=$PPID"; echo "started=$(now)"
         echo "started_ist=$(TZ=Asia/Kolkata date '+%Y-%m-%d %H:%M:%S IST')"
         echo "host=$(hostname)"
       } > "$tmp" 2>/dev/null && [ -s "$tmp" ] && ln "$tmp" "$f" 2>/dev/null; then
      rm -f "$tmp"
      # Slot check AFTER claim: over cap, the YOUNGEST flag yields
      # (deterministic — no honour required). A flag that vanishes
      # mid-check was leaving: skip it silently (v2.0 statted it
      # blind and errored — warrant, Oct 29 23:07:23).
      local others=0 of om
      for of in "$QUEUE_DIR"/compile-*.flag; do
        [ "$of" = "$f" ] && continue
        om=$(stat -c %Y "$of" 2>/dev/null) || continue
        [ "$om" -le "$(stat -c %Y "$f")" ] && others=$((others+1))
      done
      if [ "$others" -lt "$MAX_FLAGS" ]; then
        echo "acquired: $f (wrapper pid $PPID, waited ${waited}s)"; return 0
      fi
      rm -f "$f"
    fi
    rm -f "$tmp"
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
