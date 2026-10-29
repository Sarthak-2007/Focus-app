# THE CHECKABLE BUDGET — Law 3, audited in gigabytes

**Seat:** Vorrat, Methods — WORK-QUEUE item 8, seated by Ren
Oct 29, 2026, 23:18 IST on Sarthak's direct order ("finish that
queue"). Item 8 stood open since its first owner (Speicher) was
refused at seating; it was the queue's last ownerless item.
**Item, verbatim (plan/WORK-QUEUE.md #8):** "Law 3 counts builds,
not gigabytes; draft per-process budgets (daemons, KSP, test
JVMs) the cap can be checked against. Evidence: Ruth's drawer-
lane daemon died twice in a lawful two-flag state (7 GB RAM, 0
available, Oct 29 10:14)."
**Companion, read first:** `methods/memory-budget.md` (Waage,
item 14) — the budget NUMBERS live there. This page does not
re-derive them; it builds the half item 14 never claimed: the
CHECKS. Where the two pages disagree on a constant, §6 names
the discrepancy instead of papering over it.

---

## 0 · RECONCILIATION — what item 14 delivered, what item 8 still owed

Item 14's page delivered the budget table (B1–B7: per-process
RSS figures with provenance), the lawful-state arithmetic, the
founding incident priced, and a PRE-FLAG gate (§7 there): before
claiming a flag, a lane counts resident daemons, multiplies by
B1, adds its peak and the reserve, and compares to 7,935 MB.

Four things item 8 asked for that the gate is not:

1. **A reading, not a multiplication.** The gate counts
   processes and charges each the budget. It never measures one.
   A daemon can pass the count and breach its budget in fact —
   item 14's own §9 sighting caught one at 1,899 MB against a
   1,800 MB line. A checkable budget must read live processes.
2. **Mid-flight, not just pre-flight.** The gate runs once,
   before a claim. Daemons are born, grow, and linger for the
   whole build; Ruth died inside a state the gate had passed.
   The audit must be runnable at any moment, by anyone,
   against the state as it stands.
3. **KSP, named.** Item 8's text names KSP as a class to
   budget. Item 14 correctly gives it no process (in-process
   strategy; it spends from the daemon's heap) — but a named
   class with no check at all is a hole in the audit. §2 gives
   KSP its check at the only address it has: inside the daemon.
4. **Thresholds that mean stop.** The gate says "must fit."
   The audit needs a verdict ladder — inside / margin / breach
   — with the response each rung orders, so the check ends in
   an act, not an opinion.

Everything below is those four things, and only those.

## 1 · THE BUDGET LINES THE CHECKS TEST AGAINST

Figures inherited from `memory-budget.md` §3 (provenance there),
except B1, repriced per that page's §9 verdict 1. MB = MiB as
`ps`/`/proc` report (RSS kB ÷ 1024). The box: **7,935 MB, 0
swap, 2 cores** (item 14 §1, MEASURED).

| # | Process class | Check figure | Status |
|---|---|---|---|
| B1 | Gradle daemon (per resident, building or idle) | **2,000 MB** | PROVISIONAL — repriced from 1,800 on the §9 sighting (a loaded resident sat at ~1,899 under a true -Xmx2g cap; budget = max sighted, rounded up). Hardens to MEASURED at the solo run (§5). |
| B2 | KSP / Kotlin compile | **0 MB standalone — inside B1** | SET (in-process strategy). Checked via the daemon, §2 C-KSP. Never a separate line in any sum. |
| B3 | Test JVM — logic/Robolectric fork (1 per testing lane) | **750 MB** | DERIVED (512 heap cap + native allowance). Never measured on this box. |
| B4 | Screenshot-harness JVM (replaces B3 for that lane, same fork) | **1,000 MB** | DERIVED. A 641 MB still-climbing lower bound was sighted under contention (item 14 §9). |
| B5 | Gradle client JVM (per running build) | **300 MB** | DERIVED. Sighted tonight at 97–104 MB per client (§4) — comfortable, unmeasured as a peak. |
| B6 | Worker processes (D8 etc., ≤2 per building lane) | **512 MB each** | PROVISIONAL placeholder — unmeasured, unconfigured anywhere on disk. |
| B7 | Reserve — OS + agent runtime | **1,000 MB** | RATIFIED (Ren, Oct 29 ~20:18 IST). Policy, not measurement. |

**Caps on disk, current state:** all 48 `gradle.properties`
files (project + GUH + proxy standby, 24 homes) read `-Xmx2g` /
`workers.max=2` after Bausatz's alignment sweep (~21:09 IST,
`methods/guh-census.md`). But a daemon keeps its BIRTH cap
until natural exit — which is why the checks below read each
daemon's own RSS and never trust a cap to predict it.

## 2 · THE CHECKS — command, reading, threshold

One-shot form below (thirty seconds of shell, no gradle).
Continuous form: `methods/run-baseline.sh`'s PID-keyed sampler
(item 14, addendum) records the same classes every 2 s — the
one-shot checks are its manual twin, and a dispute between them
is settled by the sampler's log, not by re-running `ps` until
it agrees with you. Take every RSS reading **twice, ~10 s
apart**: a live daemon's RSS breathes (tonight's largest moved
~40 MB between two reads a minute apart), and a single reading
is a sample, not a fact. The peak reading is `/proc`'s VmHWM —
it does not breathe backwards.

**C-DAEMON — residency and per-daemon RSS (tests B1)**

```bash
ps -eo pid,rss,cmd | grep [G]radleDaemon      # every resident
grep -E 'VmRSS|VmHWM' /proc/<pid>/status      # per daemon: now + peak
```

Reading: one line per resident daemon, **whatever tree spawned
it** — residency charges the box, not the project (tonight two
of three residents belong to a sibling tree; they spend the
same 7,935). Thresholds, per daemon: RSS or VmHWM **> 2,000 =
BREACH** (name the PID; attribute by JDK path, birth time and
the flag table — where attribution fails, the box totals in
C-SUM still rule). VmHWM within **100 MB of the line = MARGIN**
for that daemon even if current RSS fell back: it has already
spent the money once and will again under the same load.

**C-KSP — the compile inside the daemon (tests B2, inside B1)**

KSP has no PID; its check is the daemon's behaviour across the
compile phase. Reading: the building daemon's VmHWM minus its
pre-build RSS baseline (its RSS in the minute before the flag
was claimed, or its birth RSS if born for this build). That
growth **is** the Kotlin/KSP spend — item 14 §2: daemon RSS
peaks during compile, not during dex or packaging, precisely
because the compiler lives in this heap. Threshold: the sum
(baseline + growth) is judged by C-DAEMON's B1 line — there is
no second allowance. A daemon that idles at 500 and peaks at
1,950 during compile is INSIDE; one that idles at 1,700 and
peaks at 2,050 has breached *by compiling*, and the honest
report says so. Companion read, when a breach is suspected:
the daemon's own `-Xmx` on its cmdline (`ps` shows it) — a
pre-sweep resident may lawfully carry `-Xmx3g`; the RSS check
doesn't care, but the report should name the cap it grew under.

**C-FORK — test and harness JVMs (tests B3 / B4)**

```bash
ps -eo pid,rss,cmd | grep '[G]radle Test Executor'
```

Reading: one fork per testing lane (`forkEvery=0`,
`maxParallelForks=1` — item 14 §2). Thresholds: **> 750 =
BREACH** for a logic/Robolectric run; **> 1,000 = BREACH**
when ScreenshotHarnessTest is in the run (B4 replaces B3 for
that lane — same fork, never added). Both figures DERIVED
until the solo run (§5): a fork breach tonight is reported
as "over a derived line," never as a measured fact.

**C-WORKER — worker processes (tests B6)**

```bash
ps -eo pid,rss,cmd | grep '[G]radleWorkerMain'
```

Threshold: **> 512 per worker = over the PROVISIONAL line**,
reported as provisional. Count rule: > 2 workers for one
building lane is itself a finding (`workers.max=2` is SET on
disk; a third worker means the setting isn't governing).

**C-CLIENT — the gradle command itself (tests B5)**

The `-Xmx64m` launcher JVMs in `ps` output (one per running
build). Threshold **> 300 = BREACH** (DERIVED line; tonight
sighted at ~100, §4).

**C-SUM — the state audit (tests the whole position)**

Two readings, both must pass; the second is decisive:

```bash
# (a) tracked sum: RSS of every process C-DAEMON..C-CLIENT found
#     + B7 reserve (1,000)  ≤  7,935
# (b) ground truth:
free -m        # 'available'  ≥  1,000  (the ratified reserve)
```

(a) accounts only for Gradle's processes; the box also feeds
the OS, the agent runtime and everything else out of the same
7,935 — the hatch runtime alone sighted at 602 MB tonight.
That is exactly why (b) rules: **available < 1,000 is the
founding incident's signature** (542 MB available at Ruth's
death) and no sum that "fits" can overrule it. A state can
pass (a) and fail (b) — tonight's does (§4). The failure is
the finding.

## 3 · THE VERDICT LADDER — what a reading orders

| Verdict | Condition (any one) | The order |
|---|---|---|
| **INSIDE** | Every class within its line; C-SUM (a) fits; available ≥ 1,500 | Flags may be claimed under Law 3 as usual. |
| **MARGIN** | Any daemon's VmHWM within 100 of B1 · available 1,000–1,500 · any DERIVED-line class within 10% of its line | **No new flag claims.** Running builds finish. Residents drain by their 15-min idle timeout; nobody starts a test/harness run until the state reads INSIDE again. |
| **BREACH** | Any process over its line (B6/fork breaches reported with their PROVISIONAL/DERIVED labels) · C-SUM (a) over 7,935 · **available < 1,000** | Stop-state: no new claims, and the next build in the queue waits even if a flag frees. The breach is **reported to Ren the hour it is read**, with the readings attached — never silently absorbed. Clearance, in order: (1) wait out idle timeouts; (2) `gradle --stop` in your OWN lane's GUH only — never across lanes, never kill another lane's process by PID; (3) re-read the state before any claim. |

The ladder's discipline is the founding incident read
backwards: Ruth's state would have read BREACH on available
(542 < 1,000) and on residency (6 daemons × any B1 + reserve
> 7,935) *while both flags were lawful*. Law 3 stays the law
of claims; this ladder is the law of the box underneath it.

## 4 · WORKED AUDIT — tonight, live (Oct 29, ~23:22 IST)

Read by Vorrat against the running night (art-wave lanes,
Truth Car 3, the pill and routine-edit lanes). Measurement
reads only; nothing disturbed.

**Flags held (Law 3):** 2 — `compile-pill.flag`,
`compile-routine-edit.flag`; a third lane (look-temple) waits
inside `queue.sh acquire`. Lawful.

| Check | Reading | Verdict |
|---|---|---|
| C-DAEMON | 3 residents: PID 2333 RSS 1,880 / **VmHWM 1,916** (jdk21, clone-inspect — a test daemon) · PID 2186 RSS = VmHWM 1,741 (sibling tree) · PID 2082 RSS 464 (sibling tree) | No breach at B1 = 2,000. PID 2333 is **MARGIN** (peak within 100 of the line). At item 14's pre-reprice B1 = 1,800 the box's largest daemon breaches on current RSS *and* peak — tonight the reprice is load-bearing, not academic. |
| C-KSP | No compile-phase baseline pair taken (the audit arrived mid-flight; C-KSP needs the pre-claim baseline or a birth RSS). Named here as the check's one procedural demand: whoever claims a flag notes the daemon's birth/baseline RSS in the flag record's margin — one line, or C-KSP can only be run by the sampler's continuous log. | Not run — stated, not skipped silently. |
| C-FORK / C-WORKER | No test executor, no worker alive at either reading (the pill/routine-edit work was between phases). | No reading — classes absent, not acquitted. |
| C-CLIENT | 2 launcher JVMs: 104 MB, 97 MB | INSIDE (B5 = 300, DERIVED line now sighted twice at ~⅓ of itself). |
| C-SUM (a) | Tracked Gradle total ≈ 4,286 MB (daemons 4,085 + clients 201) + 1,000 reserve = **5,286 ≤ 7,935** | Passes. |
| C-SUM (b) | `free` available: **557 MB, then 639 MB** a minute later | **BREACH** (< 1,000). The untracked ~3.6 GB is the rest of the box: agent runtime 602, cache ~0.8–1.0, shells, tooling, OS. |

**State verdict: BREACH on headroom with zero processes at
fault.** Two lawful flags, every daemon inside its line, the
tracked sum fitting by 2.6 GB — and the box still sits inside
the founding incident's signature band (557–639 available
against the incident's 542). Under §3 the order tonight is:
no new claims until residents drain and available recovers
past 1,000 (1,500 for INSIDE). Nothing needs killing; the
15-minute idle timeouts are already draining the sibling
residents. This is item 8's premise proven on live data: the
flag count said lawful; the gigabyte audit said stop — before
anything died, which is the only time the audit is worth
anything.

## 5 · WHAT STAYS PROVISIONAL — and the one measurement that closes it

B3, B4, B6 have never been measured on this box; B1's 2,000
is a sighting-derived reprice, not a measurement. **One solo
run closes all four:** Waage's instrumented run, fire-ready
in `memory-budget.md` §10 (`methods/run-baseline.sh --label
solo-b3b4b6`; ~15 min of booking once launched; preconditions
P1–P5 printed there — zero flags, no foreign forks, available
≥ 5,200, ≤ 1 resident daemon, /tmp ≥ 200 MB). Tonight's box
(§4: available ~600, flags held) is nowhere near its window;
the slot is a quiet box after the art wave, and Waage's own
escalation rule stands: if 24 h after the art wave lands the
run still hasn't fired, the fact to escalate is residency,
not the instrument. When it fires, in Waage's seating: B3/B4/
B6 flip DERIVED/PROVISIONAL → MEASURED on item 14's page, B1
is re-sighted under the delta, and this page's §1 figures are
updated from the same summary — the thresholds do not change
shape, only their labels harden.

Until then, every check output that cites B3, B4 or B6
carries its label (DERIVED / PROVISIONAL) in the report
itself. A threshold is not allowed to look firmer than its
number.

## 6 · DISCREPANCIES NAMED (not silently harmonised)

1. **Item 14's gate still spends 1,800.** `memory-budget.md`
   §4's table and §7's pre-flag arithmetic multiply residents
   by 1,800; its own §9 sighting supports 2,000, and §1 here
   checks against 2,000. A lane running the §7 gate as printed
   *under-charges every daemon by 200 MB* and can pass a state
   this page calls MARGIN/BREACH. Owed: a one-constant edit to
   §4/§7 of item 14's page (Waage's page, Waage's or Ren's
   hand) so gate and audit spend the same B1.
2. **OPEN-LOOPS L25 already reads CLOSED** (Registry merge:
   item 8 was reassigned Speicher → Waage and "closed on the
   filed page"), while WORK-QUEUE item 8 itself still stands
   open with "Needs Ren's ruling: reassign or strike." Both
   records can't be the whole story: item 14 closed the
   *budget-numbers* half L25 pointed at, and the queue item —
   whose text asks for budgets *the cap can be checked
   against* — stayed open for the checkable half. This page
   is that half. The registry correction is Ren's/Protokoll's
   to book; it is named here so the two files stop disagreeing
   in silence.

## 7 · CLOSURE

Item 8 asked for per-process budgets (daemons, KSP, test
JVMs) the cap can be checked against. The budgets stand in
item 14's page; the checking stands here: a budget line per
class (§1), a command + reading + threshold per class (§2),
a verdict ladder that ends in orders (§3), and a worked audit
against tonight's live box that caught a lawful state sitting
in the founding incident's signature band (§4). **Item 8 can
be struck from the queue on this delivery.** What remains
owed is not item 8's: the solo measurement (§5 — Waage's slot,
already item 14's named debt) and the one-constant gate edit
(§6.1). Both are named, owned, and small; neither blocks the
strike.

— Vorrat, Methods · Oct 29, 2026, ~23:24 IST
