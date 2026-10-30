#!/usr/bin/env bash
# watch.sh — Monolith issue-watcher.
#
# Polls Sarthak-2007/Focus-app issue + PR comments via `gh api`, and surfaces
# the comments that are SARTHAK'S OWN (see "whose comment?" in README.md —
# every agent also posts under his login, so authorship alone is not enough;
# agent comments carry studio signatures, his don't).
#
# Contract:
#   exit 0, no output      -> nothing new (QUIET — cron stays silent)
#   exit 0, output         -> >=1 new comment from Sarthak; first line is a
#                             one-line summary, then one block per comment
#   exit 2, output         -> bad arguments; nothing seeded or changed
#   exit 3, output         -> WATCHER FAILURE (gh auth / API / timeout /
#                             local processing). First line carries the whole
#                             alarm. Pages already committed stay committed;
#                             the un-fetched remainder is retried next poll —
#                             nothing is skipped. Repeat failures of the same
#                             class go silent (logged only) and re-alarm
#                             every 6h. A recovery prints one RECOVERED line.
#   exit 5, output         -> STATE CORRUPT — state file unreadable; the
#                             watcher refuses to guess a watermark.
#
# Catch-up law: fetching is paged (100/page) and STATE COMMITS PER PAGE, so
# a huge backlog window (e.g. after a long gh outage) drains page by page
# across polls instead of dying whole at a fetch cap (livelock cure).
#
# State lives OUTSIDE the repo (see README): $WATCHER_STATE_DIR/state.json
# Env: WATCHER_STATE_DIR, WATCHER_REPO, WATCHER_AUTHOR, WATCHER_SURFACE_ALL=1
set -uo pipefail

REPO="${WATCHER_REPO:-Sarthak-2007/Focus-app}"
AUTHOR="${WATCHER_AUTHOR:-Sarthak-2007}"
STATE_DIR="${WATCHER_STATE_DIR:-$HOME/workspace/goals/monolith-focus-app-revamp/hidden_files/issue-watcher}"
STATE="$STATE_DIR/state.json"
SURFACED_LOG="$STATE_DIR/surfaced.log"
DROPPED_LOG="$STATE_DIR/dropped.log"
RUNS_LOG="$STATE_DIR/runs.log"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROUTING_FILE="$SCRIPT_DIR/routing.tsv"
OVERLAP_SECONDS=120
SEEN_CAP=1000
PAGE_TIMEOUT=120

mkdir -p "$STATE_DIR" || { echo "ISSUE-WATCHER FAILURE — cannot create state dir $STATE_DIR"; exit 3; }

fail() { # fail <class> <detail>
  local cls="$1" detail="$2" now now_s prev_cls last_alarm alarm=0
  now="$(date -u +%Y-%m-%dT%H:%M:%SZ)"
  now_s="$(date -u +%s)"
  echo "[$now] FAILURE class=$cls detail=$detail" >> "$RUNS_LOG"
  if [ -f "$STATE" ]; then
    prev_cls="$(jq -r '.failure.class // ""' "$STATE" 2>/dev/null)"
    last_alarm="$(jq -r '.failure.last_alarm // ""' "$STATE" 2>/dev/null)"
    if [ "$prev_cls" != "$cls" ] || [ -z "$last_alarm" ]; then alarm=1;
    elif [ $(( now_s - $(date -u -d "$last_alarm" +%s 2>/dev/null || echo 0) )) -ge 21600 ]; then alarm=1; fi
    FTMP="$(mktemp "$STATE_DIR/.fail.XXXXXX")"
    jq --arg c "$cls" --arg t "$now" --arg la "${last_alarm:-}" --argjson al "$alarm" '
      .failure={class:$c, since:(if $c == (.failure.class // "") then (.failure.since // $t) else $t end),
                last:$t, count:((.failure.count // 0)+1),
                last_alarm:(if $al == 1 then $t else $la end)}' "$STATE" > "$FTMP" && mv "$FTMP" "$STATE"
    rm -f "$FTMP"
  else
    alarm=1
  fi
  if [ "$alarm" -eq 1 ]; then
    echo "ISSUE-WATCHER FAILURE — Sarthak's GitHub comments are NOT being watched. class=$cls detail=\"$detail\" at=$now repo=$REPO. Action: run 'gh auth status'; if the token is dead, re-login gh. The watcher resumes from its last watermark — nothing is skipped."
    # pages committed before this failure surfaced real comments; their text
    # must reach Ren in THIS message, not die with the buffered output.
    if [ "${TOTAL_HIS:-0}" -gt 0 ] && [ -n "${OUTFILE:-}" ] && [ -f "$OUTFILE" ]; then
      echo "NOTE: $TOTAL_HIS new comment(s) were committed before the failure and follow below; the un-fetched remainder retries next poll."
      echo
      cat "$OUTFILE"
    fi
    exit 3
  fi
  echo "[$now] FAILURE-SUPPRESSED (repeat of class=$cls, next re-alarm >=6h)" >> "$RUNS_LOG"
  if [ "${TOTAL_HIS:-0}" -gt 0 ] && [ -n "${OUTFILE:-}" ] && [ -f "$OUTFILE" ]; then
    ISSUES="$(echo ${HIS_ISSUES:-} | tr ' ' '\n' | sort -u | paste -sd' ' -)"
    echo "SARTHAK COMMENTED — $TOTAL_HIS new comment(s) on $REPO: $ISSUES"
    echo
    cat "$OUTFILE"
  fi
  exit 0
}

# ---------- lock (no overlapping polls -> no double surfacing) ----------
exec 9>"$STATE_DIR/.lock"
if command -v flock >/dev/null 2>&1; then
  flock -n 9 || { echo "[$(date -u +%Y-%m-%dT%H:%M:%SZ)] LOCK-SKIP another poll is running" >> "$RUNS_LOG"; exit 0; }
else
  echo "[$(date -u +%Y-%m-%dT%H:%M:%SZ)] WARN no flock on this box — running UNLOCKED" >> "$RUNS_LOG"
fi

# ---------- state init (only when no state exists) ----------
SEEDED_NOW=""
if [ ! -f "$STATE" ]; then
  SEED="$(date -u +%Y-%m-%dT%H:%M:%SZ)"
  if [ $# -gt 0 ]; then
    if [ "${1:-}" = "--seed-at" ] && [ -n "${2:-}" ]; then
      SEED="$2"
      if ! [[ "$SEED" =~ ^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}Z$ ]]; then
        echo "ISSUE-WATCHER FAILURE — bad --seed-at value '$SEED' (want ISO8601 UTC, e.g. 2026-10-29T21:49:11Z). Nothing seeded."; exit 2
      fi
      if [ "$(date -u -d "$SEED" +%s)" -gt $(( $(date -u +%s) + 300 )) ]; then
        echo "ISSUE-WATCHER FAILURE — --seed-at '$SEED' is in the future; that would skip everything until then. Nothing seeded."; exit 2
      fi
    else
      echo "usage: watch.sh [--seed-at ISO8601Z]   (the flag only applies when no state exists)" >&2; exit 2
    fi
  fi
  ITMP="$(mktemp "$STATE_DIR/.init.XXXXXX")"
  jq -n --arg s "$SEED" '{version:1, failure:null,
    streams:{issues:{watermark:$s, seen_ids:[]}, pulls:{watermark:$s, seen_ids:[]}}}' > "$ITMP" \
    && mv "$ITMP" "$STATE"
  rm -f "$ITMP"
  echo "[$(date -u +%Y-%m-%dT%H:%M:%SZ)] INIT watermark seeded at $SEED" >> "$RUNS_LOG"
  SEEDED_NOW="$SEED"
elif [ $# -gt 0 ]; then
  echo "usage: watch.sh [--seed-at ISO8601Z]   (state already exists — --seed-at ignored; delete state.json to reseed)" >&2; exit 2
fi

# ---------- state sanity (refuse to guess a watermark) ----------
if ! jq -e '.version == 1 and .streams.issues.watermark and .streams.pulls.watermark' "$STATE" >/dev/null 2>&1; then
  echo "ISSUE-WATCHER FAILURE — STATE CORRUPT: $STATE is unreadable or missing watermarks; the watcher refuses to guess (guessing = a flood or a silent skip). Action: inspect the file; reseed by deleting it and running once with --seed-at <ISO>."
  echo "[$(date -u +%Y-%m-%dT%H:%M:%SZ)] STATE-CORRUPT" >> "$RUNS_LOG"
  exit 5
fi
WAS_FAIL_CLS="$(jq -r '.failure.class // ""' "$STATE")"
WAS_FAIL_SINCE="$(jq -r '.failure.since // ""' "$STATE")"

# ---------- auth preflight (fail loudly, never silently) ----------
command -v gh  >/dev/null 2>&1 || fail "NO_GH_BINARY" "gh not on PATH"
command -v jq  >/dev/null 2>&1 || fail "NO_JQ_BINARY" "jq not on PATH"
PRE_ERR="$(mktemp "$STATE_DIR/.pre.XXXXXX")"
LOGIN="$(timeout 60 gh api user --jq .login 2>"$PRE_ERR")"
RC=$?
PRE_ERR1="$(head -1 "$PRE_ERR" | cut -c1-160)"; rm -f "$PRE_ERR"
[ $RC -eq 124 ] && fail "TIMEOUT" "gh api user exceeded 60s (egress hang?)"
[ $RC -ne 0 ]   && fail "AUTH" "gh api user failed rc=$RC gh said: $PRE_ERR1"
[ "$LOGIN" != "$AUTHOR" ] && fail "AUTH" "gh logged in as '$LOGIN', expected '$AUTHOR'"

# ---------- helpers ----------
route_for() { # route_for <issue_number> -> lane string
  local n="$1" lane=""
  if [ -f "$ROUTING_FILE" ]; then
    lane="$(awk -F '\t' -v n="$n" '$1==n {print $2; exit}' "$ROUTING_FILE")"
    if [ -z "$lane" ]; then
      lane="$(awk -F '\t' -v n="$n" '$1 ~ /-/ {split($1,r,"-"); if (n>=r[1] && n<=r[2]) {print $2; exit}}' "$ROUTING_FILE")"
    fi
  fi
  [ -n "$lane" ] && echo "$lane" || echo "Ren — unrouted, triage by hand"
}

iso_minus() { # iso_minus <ISO8601Z> <seconds> -> ISO8601Z
  local epoch
  epoch="$(date -u -d "$1" +%s 2>/dev/null)" || { echo "$1"; return; }
  date -u -d "@$((epoch - $2))" +%Y-%m-%dT%H:%M:%SZ
}
ist_of() { TZ=Asia/Kolkata date -d "$1" '+%d %b %H:%M IST' 2>/dev/null || echo "$1"; }

# Agent-vs-Sarthak classification, in jq (fast on big windows): a comment is
# a studio (agent) filing if its first non-empty line is a heading or bold
# header, or its body carries studio structural markers.
JQ_CLASSIFY='
  def firstline: (split("\n") | map(select(test("\\S"))) | .[0] // "");
  def isagent: (.body // "") as $b | ($b | firstline) as $f
    | (($f | test("^#")) or ($f | test("^\\*\\*"))
       or ($b | test("efficiency account|inquisition|closure evidence|ci run [0-9]|agent/issue-|under ren|signed[,:]|carrier bundle|disposition:"; "i")));
  {id, created_at, user: .user.login,
   issue_url: (.issue_url // .pull_request_url // ""), html_url,
   body: (.body // ""), agent: isagent}'

OUTFILE="$(mktemp "$STATE_DIR/.out.XXXXXX")"
PENDING_LOG="$STATE_DIR/.surfaced.pending"
PENDING_DROPPED="$STATE_DIR/.dropped.pending"
: > "$PENDING_LOG"; : > "$PENDING_DROPPED"
trap 'rm -f "$OUTFILE" "$PENDING_LOG" "$PENDING_DROPPED"' EXIT
TOTAL_HIS=0
HIS_ISSUES=""

commit_stream() { # commit_stream <stream> <new_watermark> <ids-json>
  local stream="$1" nwm="$2" idjson="$3" tmp
  [ -s "$PENDING_LOG" ] && cat "$PENDING_LOG" >> "$SURFACED_LOG"
  [ -s "$PENDING_DROPPED" ] && cat "$PENDING_DROPPED" >> "$DROPPED_LOG"
  : > "$PENDING_LOG"; : > "$PENDING_DROPPED"
  tmp="$(mktemp "$STATE_DIR/.state.XXXXXX")"
  jq --arg s "$stream" --arg wm "$nwm" --argjson new "$idjson" --argjson cap "$SEEN_CAP" '
    .streams[$s].watermark = $wm
    | .streams[$s].seen_ids = (((.streams[$s].seen_ids + $new) | unique) | .[-$cap:])
  ' "$STATE" > "$tmp" && mv "$tmp" "$STATE"
  rm -f "$tmp"
}

# ---------- poll one stream, page by page ----------
poll_stream() {
  local stream="$1" endpoint="$2"
  local wm since page=1
  wm="$(jq -r --arg s "$stream" '.streams[$s].watermark' "$STATE")"
  since="$(iso_minus "$wm" $OVERLAP_SECONDS)"
  while :; do
    local rawfile errfile rc count bmax idjson
    rawfile="$(mktemp "$STATE_DIR/.raw.XXXXXX")"; errfile="$(mktemp "$STATE_DIR/.err.XXXXXX")"
    timeout $PAGE_TIMEOUT gh api "repos/$REPO/$endpoint?since=$since&per_page=100&page=$page" \
        --jq ".[] | $JQ_CLASSIFY" > "$rawfile" 2> "$errfile"
    rc=$?
    [ $rc -eq 124 ] && { rm -f "$rawfile" "$errfile"; fail "TIMEOUT" "$stream page $page exceeded ${PAGE_TIMEOUT}s (pages before it are committed; the rest retries next poll)"; }
    [ $rc -ne 0 ]   && { ERR1="$(head -1 "$errfile" | cut -c1-160)"; rm -f "$rawfile" "$errfile"; fail "API" "$stream page $page failed rc=$rc gh said: $ERR1"; }
    rm -f "$errfile"
    count="$(wc -l < "$rawfile")"
    [ "$count" -eq 0 ] && { rm -f "$rawfile"; break; }
    # batch watermark + ids in one pass; a garbled pass is a LOUD local failure
    bmax="$(jq -s -r 'max_by(.created_at).created_at' "$rawfile")" \
      || { rm -f "$rawfile"; fail "LOCAL" "$stream page $page: jq aggregation failed"; }
    [[ "$bmax" =~ ^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}Z$ ]] \
      || { rm -f "$rawfile"; fail "LOCAL" "$stream page $page: bad watermark candidate '$bmax'"; }
    [ "$bmax" \< "$wm" ] && bmax="$wm"   # watermark never moves backwards
    idjson="$(jq -s 'map(.id)' "$rawfile")"
    # audit: his-login comments judged to be agent filings (recoverable)
    local drec
    while IFS= read -r drec; do
      local did diurl dcreated
      did="$(jq -r .id <<<"$drec")"; diurl="$(jq -r .issue_url <<<"$drec")"; dcreated="$(jq -r .created_at <<<"$drec")"
      if ! jq -e --arg s "$stream" --argjson i "$did" '.streams[$s].seen_ids | index($i)' "$STATE" >/dev/null; then
        echo "$did #${diurl##*/} $dcreated dropped-as-agent" >> "$PENDING_DROPPED"
      fi
    done < <(jq -c --arg u "$AUTHOR" 'select(.user == $u and .agent == true)' "$rawfile")
    # his comments: right author, not studio-signed, not already seen
    local rec
    while IFS= read -r rec; do
      local id created iurl hurl body num
      id="$(jq -r .id <<<"$rec")"; created="$(jq -r .created_at <<<"$rec")"
      iurl="$(jq -r .issue_url <<<"$rec")"; hurl="$(jq -r .html_url <<<"$rec")"
      body="$(jq -r .body <<<"$rec")"; num="${iurl##*/}"
      if jq -e --arg s "$stream" --argjson i "$id" '.streams[$s].seen_ids | index($i)' "$STATE" >/dev/null; then
        continue
      fi
      TOTAL_HIS=$((TOTAL_HIS + 1))
      HIS_ISSUES="$HIS_ISSUES #$num"
      {
        echo "=== SARTHAK ON #$num · comment $id · $created UTC ($(ist_of "$created")) ==="
        echo "$hurl"
        echo "ROUTE (draft): $(route_for "$num")"
        echo "---"
        printf '%s\n' "$body"
        echo
      } >> "$OUTFILE"
      echo "$id #$num $created $hurl" >> "$PENDING_LOG"
    done < <(if [ "${WATCHER_SURFACE_ALL:-0}" = "1" ]; then
               jq -s -c --arg u "$AUTHOR" 'unique_by(.id) | sort_by(.created_at) | .[] | select(.user == $u)' "$rawfile"
             else
               jq -s -c --arg u "$AUTHOR" 'unique_by(.id) | sort_by(.created_at) | .[] | select(.user == $u and .agent == false)' "$rawfile"
             fi)
    rm -f "$rawfile"
    commit_stream "$stream" "$bmax" "$idjson"
    wm="$bmax"
    [ "$count" -lt 100 ] && break
    page=$((page + 1))
  done
}

poll_stream issues "issues/comments"
poll_stream pulls  "pulls/comments"

# ---------- full success: clear failure record, log, output ----------
if [ -n "$WAS_FAIL_CLS" ]; then
  FTMP="$(mktemp "$STATE_DIR/.state.XXXXXX")"
  jq '.failure = null' "$STATE" > "$FTMP" && mv "$FTMP" "$STATE"
  rm -f "$FTMP"
fi
NOW="$(date -u +%Y-%m-%dT%H:%M:%SZ)"
echo "[$NOW] POLL ok his_new=$TOTAL_HIS" >> "$RUNS_LOG"

if [ -n "$SEEDED_NOW" ]; then
  echo "ISSUE-WATCHER INIT — state seeded at $SEEDED_NOW; comments before that instant are not watched."
fi
if [ -n "$WAS_FAIL_CLS" ] && [ "$TOTAL_HIS" -eq 0 ]; then
  echo "ISSUE-WATCHER RECOVERED — polling works again (was down: class=$WAS_FAIL_CLS since $WAS_FAIL_SINCE). Watermark held the whole outage; nothing was skipped."
fi
if [ "$TOTAL_HIS" -gt 0 ]; then
  ISSUES="$(echo $HIS_ISSUES | tr ' ' '\n' | sort -u | paste -sd' ' -)"
  REC=""
  [ -n "$WAS_FAIL_CLS" ] && REC=" (watcher recovered from a $WAS_FAIL_CLS outage — watermark held, nothing skipped)"
  echo "SARTHAK COMMENTED — $TOTAL_HIS new comment(s) on $REPO: $ISSUES$REC"
  echo
  cat "$OUTFILE"
fi
exit 0
