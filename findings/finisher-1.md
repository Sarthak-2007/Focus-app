# Issue #17 — Snapshot #3: THE HEALTHY-WINDOW GATE (gallery-standard gate checklist)

Prepared by: FINISHER-1, Anker cell. **Preparation only — no snapshot was run, nothing was copied.**
Scope: this replaces §5 (Preconditions) of `planner.md` with the version an operator runs at the
machine. It gates the *window*, not the script — the planner's §7 script patches (D1–D3 blockers)
must also be applied before launch; a green gate does not excuse a red script.

Read the gates **in order, top to bottom, immediately before launch**. Every gate is binary:
the PASS reading shown, or STOP. There is no "close enough" column. Five failed attempts are
already on record — a sixth bought by skipping a gate is the one outcome this checklist exists
to prevent.

---

## The numbers — settled first, so nobody re-litigates them at 3 AM

| Number | Source | Verdict |
|---|---|---|
| **350 MB** | `run_snapshot3.py` code floor (`if mem_available_mb() < 350`) — same constant in `run_daily.py` | **REJECTED as a gate.** The 01:37 attempt (`snapshot3-run.log`) started at **2,251 MB — 6.4× the floor — and died 12/12 times** with `Errno 1`. The floor guards a failure mode that is not the binding one (planner D6). It stays in the code only as a last-ditch abort; it never authorises a launch. |
| **2,251 MB → 1,363 MB** | `snapshot3-run.log`, 01:37 attempt | Not a threshold — a death certificate. Memory *fell ~900 MB during the attempts*: a falling reading is a storm in progress, not a window. |
| **~2.9 GB** | Script docstring: attempts 0014 and 0018 flickered/failed "even at ~2.9 GB MemAvailable" | Proof that **no memory number, passed, opens the gate by itself.** |
| **967 MB** | `snapshot1-run.log`: Snapshot #1 started at 967 MB and completed PASS end-to-end | Proof from the other side: memory is non-predictive across generations (that run lived in a healthier generation). It does **not** lower tonight's floor — the current regime's failures all sit in the 1.3–2.25 GB band under daemon load, and a conservative floor costs only waiting. |
| **2,500 MB** | Planner §5.3 (Snapshot #2's successful window measured **2,551 MB**); also the daily runner's own stated rule ("memory-gated per Anker's letter, single check >= ~2,500 MB") — which, note, its code does not actually enforce | **KEPT as the screening floor: necessary, never sufficient.** |
| **≥ 10 GB disk free** | Planner §5.1 | **KEPT.** Worst-case peak transient use ≈ 4× payload ≈ 2.1 GB, so 10 GB is ~5× headroom. The plan-time reading (115 GB free) is stale the moment it is read — re-measure at gate time. |
| **~1 hour** | Planner §5.4 / AGENTS.md: write health decays over ~an hour of runtime generation age | **KEPT** as the generation-age limit in Gate 2. |

**The one-sentence version:** memory ≥ 2,500 MB lets you *keep checking*; only the sustained
write probe (Gate 6) lets you *launch*.

---

## GATE 1 — Quiescence: nothing else is writing, nothing else is building

Check:
```bash
ps aux | grep -i '[g]radle'          # builds / daemons
pgrep -af 'assemble|test|lint'       # any live build task
uptime                               # record the load average in the run log
```
- ☐ **PASS:** no build, test, or lane/cell job is running or writing into
  `~/workspace/clone-inspect/builds/`; issue cells are paused for the copy, or Ren has
  explicitly accepted "lane set as it stood at the copy minute" (the #1/#2 convention,
  to be stated in SNAPSHOT.md).
- **FAIL →** wait for the job to finish, or get Ren's explicit copy-minute acceptance.
  Do not start a copy into a tree that is still moving and call the result a snapshot.

## GATE 2 — Fresh generation: the wall decays with runtime age

Check: note the time of the last fresh runtime start (this generation's start).
- ☐ **PASS:** launch will happen **within ~60 minutes** of that fresh start.
- **FAIL →** (generation older than ~60 min, or age unknown): **do not launch.**
  Defer to the next fresh generation. Tonight's plan-time state — `MemAvailable`
  ~1.3–1.7 GB and *falling between probes*, load average ~10 — was measured in an old,
  storm-loaded generation and is the textbook FAIL picture for Gates 2, 4 and 5 at once.

## GATE 3 — Disk headroom

Check:
```bash
df -m --output=avail ~/workspace | tail -1
```
- ☐ **PASS:** the number printed is **≥ 10240** (10 GB available on `/home/hatch`).
  Covers the worst-case transient peak (staging + A + B + restore scratch ≈ 2.1 GB)
  with ~5× margin. Remember A and B are the **same disk** (planner D7) — this gate
  cannot fix that; it only guarantees the copy itself will not starve.
- **FAIL →** free space first; never "start and hope" — a copy that dies at location B
  leaves a verified A and a torn B, the worst debris shape there is.

## GATE 4 — Gradle daemons: the storm's residue

Check:
```bash
ps aux | grep -i '[g]radle' | wc -l
grep -E 'org.gradle.jvmargs|org.gradle.workers.max' ~/workspace/clone-inspect/Focus-app/gradle.properties
```
- ☐ **PASS:** daemon count is **0** — OR exactly **1**, and it is the known warm daemon
  deliberately kept from *this* fresh generation (record its PID next to the run log).
  `gradle.properties` shows the fixed regime: **`-Xmx2g` / `workers.max=2`**.
- **FAIL →** (2+ daemons, any unknown daemon, or pre-fix `-Xmx4g / workers.max=4`
  settings): the Oct 29 root cause was exactly this — 6 resident daemons, 542 MB free,
  writes dying disguised as a permission wall. Kill the daemons / fix the properties,
  then restart this checklist from Gate 1. **For the daily retry (below) the warm-daemon
  exception does not apply: the count must be 0.**

## GATE 5 — Memory: ≥ 2,500 MB, three times, not falling off a cliff

Check — run three times, 30 seconds apart, and write all three numbers down:
```bash
awk '/MemAvailable/ {print int($2/1024) " MB"}' /proc/meminfo
```
- ☐ **PASS:** **every** reading is **≥ 2500 MB**, and the three readings do not fall by
  more than ~250 MB end-to-end. (The 01:37 death run fell 2,251 → 1,363 MB; a steep
  fall *is* the storm signature, whatever the absolute numbers say.)
- **FAIL →** any reading < 2500 MB, or a steep fall: STOP. Do not substitute the
  script's 350 MB floor, do not average the readings, do not re-run until one reading
  happens to pass — three-for-three is the gate.

## GATE 6 — THE GATE: sustained write probe (decisive, runs last-but-one)

A single successful write proves nothing; the failure mode arrives in waves. Probe in the
preservation tree itself, **twice, back-to-back**, and re-check memory between passes.

```bash
cd ~/workspace/preservation
P=.gate-probe-$(date +%H%M); mkdir -p "$P/small"
# (a) 100 small files — write, then read every one back
for i in $(seq -w 1 100); do printf 'gate-probe %s\n' "$i" > "$P/small/f$i.txt"; done
for i in $(seq -w 1 100); do grep -qx "gate-probe $i" "$P/small/f$i.txt"; done
# (b) one 100 MB file — write with fsync, then read it fully back
dd if=/dev/zero of="$P/big.bin" bs=1M count=100 conv=fsync status=none
stat -c %s "$P/big.bin"        # must print exactly 104857600
sha256sum "$P/big.bin"         # must run to completion and print a hash
```
- ☐ **PASS:** pass 1 completes with **zero errors** (no `Errno 1`, no `EPERM`, no short
  file, no failed read-back) → re-run the Gate 5 memory reading (still ≥ 2500 MB) →
  pass 2, identical procedure in a fresh probe dir, also zero errors → delete both
  probe dirs. Only now is the window healthy.
- ☐ **Conditional (c):** if a build is planned in the same window, add one JVM-shaped
  burst (e.g. a short Gradle invocation in a lane tree) to each pass. If no build is
  planned, record (c) as **N/A** — do not silently skip it.
- **FAIL →** any single error in either pass: STOP, delete the probe dirs, register the
  window as failed, and **do not retry inside this window/generation** — the runner's
  in-run 12× backoff is the retry mechanism, and it already rode the flicker five times.

## GATE 7 — Name and staging collision (last, because the name is minute-stamped)

Check — use the NAME the *patched* script will actually compute (planner D3 renames the
suffix; after the §7 patch, read it from the script rather than assuming):
```bash
cd ~/workspace/preservation
python3 -c "import run_snapshot3; print(run_snapshot3.NAME)"
NAME=<the name printed above>
test ! -e ~/workspace/preservation/monolith-snapshots/"$NAME"
test ! -e ~/workspace/your_files/monolith-archive/snapshots/"$NAME"
```
- ☐ **PASS:** both `test`s succeed (no output, exit 0) — neither destination exists.
  This matters because the script `rmtree`s a same-named destination **without asking**.
- ☐ **Known destruction, acknowledged:** launch wipes the whole
  `.staging-snapshot-3/` parent, including the leftover `swarm/MAXIMPL.md` from the
  registered-FAILED 01:37 attempt. That is debris, not live data — but the operator
  must know it is about to happen, not discover it afterwards.
- **FAIL →** either destination exists: STOP. Pick a later minute / resolve the
  collision by hand. Never let the script's silent `rmtree` make the decision.

## Decision rule and gate expiry

- **All 7 gates PASS, in order → launch** per planner §6. **Any gate FAIL → no launch.**
  "Try anyway" is how the record got to five failed attempts.
- **The gate expires in 10 minutes.** If more than ~10 minutes pass between Gate 6
  passing and launch — or any build starts, any daemon appears, or a cell resumes
  writing — the window is unproven again: restart from Gate 1.
- Log the gate readings (daemon count/PID, three memory numbers, disk avail, both probe
  results) at the top of the run log. A PASS nobody recorded is a PASS nobody can audit.

---

## Retry preparation — last night's failed 02:38 daily snapshot

What happened — the full record is in `studio/plan/WORK-REGISTRY.md` (issue-17 cell
clone), entry `2026-10-30_0238_daily`: the run **died at step 1, the MEMORY CHECK**.
The gate (single check, MemAvailable ≥ ~2,500 MB, per Anker's letter 2148) never
cleared in ~20 minutes of short polls, 02:38–02:58 IST. Readings: **923 MB at start,
then 1475 / 1092 / 843 / 662 / 505 / 1527 / 1145 / 1098 / 1000 MB — peak 1,527 MB,
never within 1,000 MB of the gate.** The copy was never started; no snapshot dir,
staging payload, or location write exists from this attempt; nothing to clean.
The box was genuinely hot, not metric noise: free RAM 143–262 MB across the window,
**2 Gradle daemons resident, one a 1.5 GB RSS blockit-mod build in flight from
02:28 IST.** Two checks *did* pass that night and must not be misread as partial
success: DISK CHECK (111 GB free, against the daily spec's ≥ 15 GB rule) and the
write-health probe in both destinations. The memory gate alone stopped it — and per
the registry's own note, the overnight snapshot-3 seat had already closed the memory
gate programme as falsified (deaths at every memory level); the 02:38 seat obeyed the
gate as its explicit order anyway. Whether that falsification retires the 2,500 MB
floor is Ren's ruling, not this checklist's — this checklist keeps it as a screening
floor (§ numbers table) with the write probe decisive, and flags the tension here
rather than burying it. Facts the operator must hold before touching the retry:

1. **The daily runner enforces almost nothing itself.** `run_daily.py`'s code floor is
   the same rejected **350 MB**; the "~2,500 MB" rule exists only in its SNAPSHOT.md
   text ("memory-gated per Anker's letter"). The 02:38 gate that stopped it was an
   *operator* gate, not a code gate — so the retry is gated by **this checklist,
   applied by hand**, exactly like Snapshot #3. (There is no daily-run log in
   `~/workspace/preservation/`; the registry entry above is the record.)
2. **A retry is a new run, not a resume.** Both runners wipe their staging parent at
   start and resumability is in-run only. The retry gets a **new minute-stamped
   `_daily` name**; whatever debris sits in `.staging-daily/` will be destroyed at
   retry start — expected, but say it before, not after.

**Must all be true before the retry is even attempted:**

- ☐ The build storm is **verifiably over**: no build/test job running, and Gate 4's
  count is **0 daemons — no warm-daemon exception** (the exception exists for a daemon
  deliberately kept warm in a fresh generation; storm residue is the opposite of that).
  At 02:38 there were exactly 2 daemons, one being a 1.5 GB RSS build in flight from
  02:28 — the retry's first job is proving that picture is gone, by name, not by feel.
- ☐ The 02:38 probe pass does **not** carry over. The write-health probe passed in
  both destinations that night and the run still correctly stopped — a probe pass is
  a statement about a minute, not a night. Gate 6 runs fresh for the retry.
- ☐ Gates 1–7 above all PASS, in order, in a **fresh generation** — never in the
  generation/window that starved at 02:38.
- ☐ **Sequencing:** the retry and Snapshot #3 never run at the same time, and neither
  runs into the next scheduled daily. One copy at a time on this box — two concurrent
  copies are themselves a memory/write storm, i.e. they recreate the 02:38 conditions.
  If both are pending, Ren picks the order; the gate is re-run from Gate 1 for each.

**How the retry's gate differs from Snapshot #3's gate:**

| Point | Snapshot #3 | Daily retry |
|---|---|---|
| Memory floor | ≥ 2,500 MB, 3 readings (Gate 5) | **Identical** — same floor, same 3-reading rule |
| Write probe | Gate 6, two passes, decisive | **Identical** — the probe, not memory, opens both gates |
| Disk | ≥ 10,240 MB avail (planner §5.1) | **Stricter: ≥ 15 GB avail** — the daily cadence spec's own rule (Anker letter 2148, as applied at 02:38: "111 GB free, ≥ 15 GB required"). The #3 figure does not carry over. |
| Daemons | 0, or 1 known warm daemon w/ PID recorded | **Stricter: 0, no exception** (storm residue is the failure being retried) |
| Name / staging check | Patched #3 NAME; `.staging-snapshot-3/` wipe acknowledged | `_daily` NAME at the retry minute; `.staging-daily/` wipe acknowledged; A/B collision tests against the `_daily` name |
| Extra precondition | §7 script patches applied (planner) | Storm-over proof + one-copy-at-a-time sequencing (above) |

Net: **same memory floor, same decisive probe — the retry adds proof that the storm is
gone, a stricter daemon rule, and a stricter disk floor (15 GB, its own spec's rule);
it relaxes nothing.**

---

— FINISHER-1
