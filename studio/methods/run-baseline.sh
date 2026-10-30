#!/bin/bash
# Waage — Sweep-2 official baseline instrument (commission 1, part 2).
# ONE timed solo full run on the TRUNK tree: assembleDebug + testDebugUnitTest.
# Law 3: never runs while >=1 other gradle run holds a flag slot unless the
# caller passes --force-solo-ok after verifying zero daemons are busy.
# Law 11: this IS the completion compile for measurement; nothing else.
#
# Upgrade (Waage, Oct 29 2026 — total-sweep follow-up, Ren-approved):
# the sampler is PID-keyed. Per-process-class peaks (daemon / test /
# worker / client) are recorded per PID and printed in the summary
# against memory-budget.md §3's B-rows — B3/B4/B6 stop being DERIVED
# once a solo slot runs this instrument. The screenshot harness is a
# test class inside the SAME single fork as the logic tests (B4's own
# page entry), so the B4 line re-uses the test PID set when
# ScreenshotHarnessTest appears in the run's results: it replaces B3
# for that fork, it never adds a second JVM. Raw samples are a
# transient tick file; the per-PID peaks in the summary are the record.
#
# Usage: run-baseline.sh [--max-wait-min N] [--label NAME]
# Waits until builds/compile-*.flag count == 0 (clean solo slot), claims the
# slot as compile-methods.flag, runs, releases. Refuses after max-wait.
set -u
CI=$HOME/workspace/clone-inspect
BUILDS=$CI/builds
MAXWAIT=60; LABEL=baseline
while [ $# -gt 0 ]; do case "$1" in
  --max-wait-min) MAXWAIT=$2; shift 2;;
  --label) LABEL=$2; shift 2;;
  *) echo "unknown arg: $1" >&2; exit 2;;
esac; done

waited=0
while :; do
  n=$(ls "$BUILDS"/compile-*.flag 2>/dev/null | wc -l)
  busy=$(pgrep -fc 'GradleDaemon.*busy\|org.gradle.launcher.daemon' 2>/dev/null || echo 0)
  if [ "$n" -eq 0 ]; then break; fi
  if [ "$waited" -ge $((MAXWAIT*60)) ]; then
    echo "NO SLOT: $n flag(s) still held after ${MAXWAIT}m — baseline NOT run." >&2; exit 3
  fi
  sleep 30; waited=$((waited+30))
done

FLAG=$BUILDS/compile-methods.flag
touch "$FLAG"
trap 'rm -f "$FLAG"' EXIT
TS=$(date +%Y%m%d-%H%M%S)
OUT=$CI/design-refs/swarm/methods/baseline-runs
mkdir -p "$OUT"
LOG=$OUT/$TS-$LABEL.log
SUM=$OUT/$TS-$LABEL.md

# PID-keyed RSS sampler (upgraded Oct 29 2026 — see header): every 2s,
# append one line per tracked process (epoch, pid, class, RSS KB) to a
# tick file. Classification is by cmdline, checked in this order —
# daemon, test executor, worker, client — because a test executor's
# cmdline also carries worker.org.gradle strings. The sampler's own
# cmdline is "python3 -" (the program arrives on stdin), so it can
# never match its own patterns; the transient `ps` child matches none.
SAMPLES=$OUT/.samples-$TS.tsv
: > "$SAMPLES"
python3 - "$SAMPLES" <<'PY' &
import subprocess, sys, time
out = open(sys.argv[1], "a", buffering=1)
def classify(args):
    if "GradleDaemon" in args: return "daemon"
    if "Gradle Test Executor" in args: return "test"
    if "Gradle Worker Daemon" in args or "GradleWorkerMain" in args: return "worker"
    if "GradleMain" in args: return "client"
    return None
while True:
    try:
        ps = subprocess.run(["ps", "-eo", "pid=,rss=,args="],
                            capture_output=True, text=True, timeout=10).stdout
    except Exception:
        ps = ""
    now = int(time.time())
    for line in ps.splitlines():
        parts = line.split(None, 2)
        if len(parts) < 3: continue
        pid, rss, args = parts
        cls = classify(args)
        if cls: out.write(f"{now}\t{pid}\t{cls}\t{rss}\n")
    time.sleep(2)
PY
SAMPLER=$!

cd "$CI/Focus-app" || exit 2
source "$CI/env21.sh"
XMLDIR=$CI/Focus-app/app/build/test-results/testDebugUnitTest
# Defect-2 fix (Ren's 1220 letter; applied by Ren at the total sweep,
# Oct 29): the stale-XML trap. Delete the results dir BEFORE the run —
# any XML on disk afterwards is this run's by construction, and a
# failed run leaves none to misread.
rm -rf "$XMLDIR"
START=$(date +%s)
# Defect-1 fix: /usr/bin/time does not exist on this box (the 11:04 run
# died at exit 127 and measured nothing). The shell times the run
# (START/END) and the sampler above owns peak RSS; no external timer.
gradle :app:assembleDebug :app:testDebugUnitTest --rerun-tasks \
  > "$LOG" 2>&1
RC=$?
END=$(date +%s)
kill $SAMPLER 2>/dev/null; wait $SAMPLER 2>/dev/null
# Post-process the tick file: stdout = the simultaneous-total peak KB
# (the CSV's definition, unchanged from the old sampler); the class
# lines file carries the per-PID peaks mapped onto the B-rows.
CLASSLINES=$OUT/.classlines-$TS
PEAKKB=$(python3 - "$SAMPLES" "$XMLDIR" "$CLASSLINES" <<'PY'
import glob, os, sys
samples, xmldir, outpath = sys.argv[1], sys.argv[2], sys.argv[3]
per_pid = {}   # pid -> [class, peak KB]
tick = {}      # epoch -> summed RSS KB across tracked PIDs
try:
    with open(samples) as f:
        for line in f:
            p = line.rstrip("\n").split("\t")
            if len(p) != 4: continue
            epoch, pid, cls, rss = int(p[0]), p[1], p[2], int(p[3])
            tick[epoch] = tick.get(epoch, 0) + rss
            cur = per_pid.get(pid)
            if cur is None or rss > cur[1]: per_pid[pid] = [cls, rss]
except FileNotFoundError:
    pass
total_peak = max(tick.values()) if tick else 0
def mb(kb): return f"{kb/1024:.0f}"
def pid_list(cls):
    items = sorted(((pid, v[1]) for pid, v in per_pid.items() if v[0] == cls),
                   key=lambda x: -x[1])
    if not items: return "none observed"
    return " · ".join(f"PID {pid} peak {mb(kb)} MB" for pid, kb in items)
harness = any("ScreenshotHarnessTest" in os.path.basename(fn)
              for fn in glob.glob(xmldir + "/*.xml"))
lines = []
lines.append("- per-class RSS peaks (PID-keyed sampler, 2s ticks; budgets: methods/memory-budget.md §3):")
lines.append(f"  - B1 daemon (budget 1,800 MB per resident daemon): {pid_list('daemon')}")
lines.append(f"  - B3 test JVM (budget 750 MB): {pid_list('test')}")
if harness and any(v[0] == "test" for v in per_pid.values()):
    lines.append(f"  - B4 harness JVM (budget 1,000 MB — SAME fork as B3, replaces it, never adds): {pid_list('test')} (whole-fork peak; phases inside one JVM are not separable from RSS)")
elif harness:
    lines.append("  - B4 harness JVM (budget 1,000 MB): ScreenshotHarnessTest in results but no test PID was sampled — B4 not measured")
else:
    lines.append("  - B4 harness JVM (budget 1,000 MB): ScreenshotHarnessTest not in this run's results — B4 not measured")
lines.append(f"  - B5 client (budget 300 MB): {pid_list('client')}")
lines.append(f"  - B6 workers (budget 512 MB each, provisional): {pid_list('worker')}")
with open(outpath, "w") as f: f.write("\n".join(lines) + "\n")
print(total_peak)
PY
)
WALL=$((END-START))
GRADLE_LINE=$(grep -E 'BUILD (SUCCESSFUL|FAILED) in' "$LOG" | tail -1)
read -r TCOUNT TSUM TFAIL <<<$(python3 - "$XMLDIR" <<'PY'
import glob,sys,xml.etree.ElementTree as ET
t=s=f=0
for fn in glob.glob(sys.argv[1]+"/*.xml"):
    r=ET.parse(fn).getroot(); t+=int(r.get('tests',0)); s+=float(r.get('time',0)); f+=int(r.get('failures',0))+int(r.get('errors',0))
print(t, round(s,1), f)
PY
)
# A run with no gradle verdict is an INSTRUMENT FAILURE, never a
# measurement: the header says so, no test numbers are printed, and
# the CSV takes a comment line, not a data row. (The 11:04 run's
# summary printed stale build-1 XMLs under a normal header — that is
# the failure this block exists to make impossible.)
if [ -z "$GRADLE_LINE" ]; then
  {
    echo "# Baseline run — INSTRUMENT FAILURE — $TS ($LABEL)"
    echo "- tree: trunk ($CI/Focus-app), env21.sh (JDK 21), gradle 9.3.1"
    echo "- exit: $RC · gradle verdict: none — the run produced no measurement; no test numbers are reported."
    echo "- log: $LOG"
  } > "$SUM"
  echo "# INSTRUMENT FAILURE $TS,$LABEL,rc=$RC — no data row (see $TS-$LABEL.md)" >> "$OUT/baseline-runs.csv"
  echo "INSTRUMENT FAILURE: gradle produced no verdict (exit $RC) — baseline NOT measured. See $SUM" >&2
  rm -f "$SAMPLES" "$CLASSLINES"
  cat "$SUM"
  exit 4
fi
{
  echo "# Baseline run — $TS ($LABEL)"
  echo "- tree: trunk ($CI/Focus-app), env21.sh (JDK 21), gradle 9.3.1"
  echo "- command: gradle :app:assembleDebug :app:testDebugUnitTest --rerun-tasks"
  echo "- contention: solo (0 compile flags held at claim; flag compile-methods.flag held throughout)"
  echo "- exit: $RC · gradle verdict: ${GRADLE_LINE:-none}"
  echo "- WALL CLOCK: ${WALL}s"
  echo "- peak RSS (all gradle/test JVMs, 2s sampling): $((PEAKKB/1024)) MB"
  cat "$CLASSLINES"
  echo "- test XML: tests=$TCOUNT sum_time=${TSUM}s failures+errors=$TFAIL"
  echo "- log: $LOG"
} > "$SUM"
echo "$TS,$LABEL,$WALL,$RC,$TCOUNT,$TSUM,$TFAIL,$((PEAKKB/1024))" >> "$OUT/baseline-runs.csv"
rm -f "$SAMPLES" "$CLASSLINES"
cat "$SUM"
