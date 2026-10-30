#!/usr/bin/env bash
# test-offline.sh — deterministic proof of watch.sh logic with a stubbed `gh`.
# No network. Proves: seed comment surfaces, agent filings are skipped, a
# second run is silent (caught exactly once), broken auth alarms (exit 3),
# corrupt state refuses (exit 5), watermark advances.
set -uo pipefail
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
T="$(mktemp -d)"
trap 'rm -rf "$T"' EXIT
mkdir -p "$T/bin" "$T/state"
PASS=0; FAILN=0
ok()   { PASS=$((PASS+1)); echo "ok   - $1"; }
bad()  { FAILN=$((FAILN+1)); echo "FAIL - $1"; }

# ---------- canned API payloads ----------
cat > "$T/issues.json" <<'JSON'
[
 {"id":6509136030,"created_at":"2026-10-29T21:50:11Z","user":{"login":"Sarthak-2007"},
  "issue_url":"https://api.github.com/repos/Sarthak-2007/Focus-app/issues/3",
  "html_url":"https://github.com/Sarthak-2007/Focus-app/issues/3#issuecomment-6509136030",
  "body":"What is the problem guya, does this repo needs to be public?- Sarthak"},
 {"id":6509999999,"created_at":"2026-10-29T22:00:00Z","user":{"login":"Sarthak-2007"},
  "issue_url":"https://api.github.com/repos/Sarthak-2007/Focus-app/issues/22",
  "html_url":"https://github.com/Sarthak-2007/Focus-app/issues/22#issuecomment-6509999999",
  "body":"## EFFICIENCY ACCOUNT — Builders (5 seats)\nFiled under Ren."},
 {"id":6510000001,"created_at":"2026-10-29T22:05:00Z","user":{"login":"Sarthak-2007"},
  "issue_url":"https://api.github.com/repos/Sarthak-2007/Focus-app/issues/22",
  "html_url":"https://github.com/Sarthak-2007/Focus-app/issues/22#issuecomment-6510000001",
  "body":"and also check the pill thing"}
]
JSON
echo '[]' > "$T/pulls.json"

# ---------- fake gh ----------
cat > "$T/bin/gh" <<'SHIM'
#!/usr/bin/env bash
# understands: gh auth status | gh api user --jq .login | gh api --paginate <endpoint> --jq <prog>
[ "${FAKE_GH_MODE:-ok}" = "authfail" ] && { echo "HTTP 401" >&2; exit 1; }
prog=""; endpoint=""
while [ $# -gt 0 ]; do
  case "$1" in
    --jq) prog="$2"; shift 2;;
    --paginate) shift;;
    api) shift;;
    user) echo "Sarthak-2007"; exit 0;;
    repos/*) endpoint="$1"; shift;;
    *) shift;;
  esac
done
pnum=1; [[ "$endpoint" =~ [\?\&]page=([0-9]+) ]] && pnum="${BASH_REMATCH[1]}"   # anchor: "per_page=" also contains "page="
if { [ "${FAKE_GH_MODE:-ok}" = "multipage" ] || [ "${FAKE_GH_MODE:-ok}" = "page2fail" ]; } \
   && [[ "$endpoint" == *issues/comments* ]]; then
  if [ "$pnum" -eq 1 ]; then f="$FAKE_GH_DIR/page1.json";
  elif [ "$pnum" -eq 2 ]; then
    [ "${FAKE_GH_MODE}" = "page2fail" ] && { echo "HTTP 502 page2" >&2; exit 1; }
    f="$FAKE_GH_DIR/page2.json";
  else echo '[]' > "$FAKE_GH_DIR/empty.json"; f="$FAKE_GH_DIR/empty.json"; fi
else
  case "$endpoint" in
    *issues/comments*) f="$FAKE_GH_DIR/issues.json";;
    *pulls/comments*)  f="$FAKE_GH_DIR/pulls.json";;
    *) echo "shim: unknown endpoint $endpoint" >&2; exit 1;;
  esac
fi
if [ -n "$prog" ]; then jq -c "$prog" "$f"; else cat "$f"; fi
SHIM
chmod +x "$T/bin/gh"

export PATH="$T/bin:$PATH"
export FAKE_GH_DIR="$T"
export WATCHER_STATE_DIR="$T/state"
W="$HERE/watch.sh"

# ---------- T1: seed run surfaces his 2, skips the agent filing ----------
OUT="$("$W" --seed-at 2026-10-29T21:49:11Z 2>/dev/null)"; RC=$?
[ $RC -eq 0 ] && ok "T1 exit 0" || bad "T1 exit=$RC"
grep -q "6509136030" <<<"$OUT" && ok "T1 seed comment surfaced" || bad "T1 seed missing"
grep -q "SARTHAK ON #3" <<<"$OUT" && ok "T1 seed on issue #3" || bad "T1 wrong issue"
grep -q "does this repo needs to be public" <<<"$OUT" && ok "T1 full text present" || bad "T1 body missing"
grep -q "6510000001" <<<"$OUT" && ok "T1 second his-comment surfaced" || bad "T1 second missing"
grep -q "6509999999" <<<"$OUT" && bad "T1 agent filing leaked" || ok "T1 agent filing skipped"
WM="$(jq -r .streams.issues.watermark "$T/state/state.json")"
[ "$WM" = "2026-10-29T22:05:00Z" ] && ok "T1 watermark advanced to newest" || bad "T1 watermark=$WM"

# ---------- T2: second run silent (exactly once) ----------
OUT2="$("$W" 2>/dev/null)"; RC2=$?
[ $RC2 -eq 0 ] && [ -z "$OUT2" ] && ok "T2 second run silent" || bad "T2 rc=$RC2 out=${OUT2:0:80}"

# ---------- T3: broken auth alarms ----------
OUT3="$(FAKE_GH_MODE=authfail "$W" 2>/dev/null)"; RC3=$?
[ $RC3 -eq 3 ] && grep -q "ISSUE-WATCHER FAILURE" <<<"$OUT3" && ok "T3 auth break: exit 3 + alarm line" || bad "T3 rc=$RC3"

grep -q "6509999999" "$T/state/dropped.log" 2>/dev/null && ok "T1b dropped agent filing is audit-logged" || bad "T1b dropped.log missing the agent id"

# ---------- T4: corrupt state refuses ----------
echo "not json" > "$T/state/state.json"
OUT4="$("$W" 2>/dev/null)"; RC4=$?
[ $RC4 -eq 5 ] && grep -q "STATE CORRUPT" <<<"$OUT4" && ok "T4 corrupt state: exit 5 + alarm" || bad "T4 rc=$RC4"

# ---------- T5: bad seed values are refused on a fresh state ----------
rm -f "$T/state/state.json"
"$W" --seed-at "not-a-date" >/dev/null 2>&1; RC5=$?
[ $RC5 -eq 2 ] && ok "T5 garbage seed: exit 2" || bad "T5 rc=$RC5"
"$W" --seed-at "2999-01-01T00:00:00Z" >/dev/null 2>&1; RC6=$?
[ $RC6 -eq 2 ] && [ ! -f "$T/state/state.json" ] && ok "T5 future seed: exit 2, nothing seeded" || bad "T5 future rc=$RC6"

# ---------- T6/T7: multipage drain + resume after mid-drain failure ----------
# page1 = seed comment + 99 agent fillers (full page -> forces page 2)
jq -n '[{id:6509136030,created_at:"2026-10-29T21:50:11Z",user:{login:"Sarthak-2007"},
  issue_url:"https://api.github.com/repos/Sarthak-2007/Focus-app/issues/3",
  html_url:"https://github.com/Sarthak-2007/Focus-app/issues/3#issuecomment-6509136030",
  body:"What is the problem guya, does this repo needs to be public?- Sarthak"}]
  + [range(0;99) | {id:(6511000000+.), created_at:"2026-10-29T22:00:00Z",
     user:{login:"Sarthak-2007"},
     issue_url:"https://api.github.com/repos/Sarthak-2007/Focus-app/issues/22",
     html_url:"https://github.com/Sarthak-2007/Focus-app/issues/22#issuecomment-x",
     body:"## EFFICIENCY ACCOUNT — filler"}]' > "$T/page1.json"
jq -n '[{id:6512000001,created_at:"2026-10-29T23:00:00Z",user:{login:"Sarthak-2007"},
  issue_url:"https://api.github.com/repos/Sarthak-2007/Focus-app/issues/16",
  html_url:"https://github.com/Sarthak-2007/Focus-app/issues/16#issuecomment-6512000001",
  body:"the pill still shows 2 minutes"}]' > "$T/page2.json"
mkdir -p "$T/state2"
OUT6="$(FAKE_GH_MODE=page2fail WATCHER_STATE_DIR="$T/state2" "$W" --seed-at 2026-10-29T21:49:11Z 2>/dev/null)"; RC6=$?
[ $RC6 -eq 3 ] && grep -q "ISSUE-WATCHER FAILURE" <<<"$OUT6" && ok "T6 mid-drain failure alarms (exit 3)" || bad "T6 rc=$RC6"
grep -q "6509136030" <<<"$OUT6" && ok "T6 page-1 comments printed under the alarm" || bad "T6 committed page-1 text lost"
WM6="$(jq -r .streams.issues.watermark "$T/state2/state.json")"
[ "$WM6" = "2026-10-29T22:00:00Z" ] && ok "T6 watermark committed at page 1" || bad "T6 watermark=$WM6"
OUT7="$(FAKE_GH_MODE=multipage WATCHER_STATE_DIR="$T/state2" "$W" 2>/dev/null)"; RC7=$?
[ $RC7 -eq 0 ] && grep -q "6512000001" <<<"$OUT7" && ok "T7 resume surfaces page-2 comment" || bad "T7 rc=$RC7"
grep -q "6509136030" <<<"$OUT7" && bad "T7 seed re-surfaced (dupe)" || ok "T7 no duplicate of page-1 comments"
OUT8="$(FAKE_GH_MODE=multipage WATCHER_STATE_DIR="$T/state2" "$W" 2>/dev/null)"; RC8=$?
[ $RC8 -eq 0 ] && [ -z "$OUT8" ] && ok "T7 drained window then silent" || bad "T7 post-drain rc=$RC8 out=${OUT8:0:60}"

echo "---"
echo "PASS=$PASS FAIL=$FAILN"
[ "$FAILN" -eq 0 ]
