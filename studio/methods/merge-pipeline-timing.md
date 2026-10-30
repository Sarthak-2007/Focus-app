# THE MERGE PIPELINE, TIMED AND PRICED
### Takt, Methods (timing/consolidation) · Oct 29, 2026, ~23:20 IST
### Commission: Ren, Oct 29, 23:16 IST (Sarthak's order: "Run methods and keepers of record")

**Evidence rule.** Every time below comes from disk: agent spawn
records (created/completed epochs), file mtimes in the lane trees
(stat, IST), `diff -rq`/`diff -r` of each lane against trunk,
declaration letters in mail/ren/, Konto's merge map Edition 1
(plan/TRUTH3-MERGE-MAP.md), and the Methods baselines already on
record (methods/waage-baseline.md, methods/queue-waits.md).
Where a number is my estimate rather than a measurement, it is
labelled ESTIMATE and priced per item so Ren can strike any line.

---

## 1 · TONIGHT'S CAR AS A TIMELINE

Truth Car 3: five builder lanes from one trunk base (batch-8,
DB v11), seated inside three minutes, plus a verification bench
(Una pass 1, Hale wording, Pruefer proof standard, Konto
cartography) seated inside five.

### 1.1 Seating (spawn records)

| Seat | Agent | Seated (IST) | Fate |
|---|---|---|---|
| Times | Rurik | 22:29 (first attempt) | REFUSED; retry on Sarthak's word refused again ~22:39 — lane never opened |
| Shorts (UNA-01) | Falk | 22:32:01 | declared 23:10:59 |
| Sound/social (UNA-03/02) | Imke | 22:32:37 | declared 23:13:34 |
| Sweep (UNA-04…09) | Bodo | 22:33:10 | declared 23:11:44 |
| Intercept (UNA2 ×13) | Suki | 22:33:44 | still writing at 23:19 |
| Breaks (ST-02/03) | Tamm | 22:34:29 | still writing at 23:19 |
| Cartography | Konto | 22:36:29 | Edition 1 filed ~23:06 |
| Proof desk | Pruefer | 22:36:58 | standard filed 22:52 |

### 1.2 Per-lane phases (mtimes of files that differ from trunk)

| Lane | Seat→first edit | First edit | Last main edit | Tests written | Declared | Seat→declared |
|---|---|---|---|---|---|---|
| Falk / shorts | 14 min | 22:46:09 | 23:05:21 | 23:09:05–23:09:22 | 23:10:59 | **39 min** |
| Bodo / sweep | 16 min | 22:48:43 | 23:08:14 | 23:09:09 | 23:11:44 | **39 min** |
| Imke / sound | 22 min | 22:54:33 | ~23:07:04 | 23:12:38 | 23:13:34 | **41 min** |
| Tamm / breaks | 23 min | 22:57:36 | still writing | 23:09:59–23:10:43 | — | open ≥45 min |
| Suki / intercept | 29 min | 23:02:37 | still writing | 23:19:40 | — | open ≥46 min |

Diff size vs trunk at capture (~23:17): shorts 191 changed lines
/ 11 files · sweep 147 / 10 · sound 263 / 6 · breaks 335 / 8 ·
intercept 651 / 11. The three declared lanes total ≈600 changed
lines; all five ≈1,590 and still growing.

### 1.3 Where the wall-clock actually went

1. **Reading and verification ate the first third — by design.**
   No builder touched code for 14–29 minutes. That window is
   Una's verification briefs landing (written 22:37), Pruefer's
   proof letters (22:48), the proofbase copy (22:42), and each
   builder re-deriving their charges in trunk before curing
   (every declaration letter shows the trunk cites checked
   first). Suki's 29 minutes is the extreme: the largest slice
   (13 items, 651 lines) plus a Konto survey at 22:58 that
   still showed her lane at ZERO hunks — she was reading the
   whole intercept surface, not idling.
2. **Writing was the middle ~20 minutes, and it overlapped
   almost perfectly.** All five lanes were mid-write
   simultaneously between ~23:03 and ~23:08. The three declared
   lanes consumed ≈119 agent-minutes inside 41 wall-minutes —
   a 2.9× compression; all five project to ≈3.7 agent-hours
   inside ~1 wall-hour. Parallelism worked; nothing queued
   behind anything except information.
3. **Tests came last in every lane** — a tight cluster
   23:09–23:12 across three lanes, test files written after the
   last main-source edit. Under compile-once the tests are
   written blind (they cannot be run in-lane), so builders
   front-loaded certainty into the reading phase instead. This
   is the compile-once trade showing up in behaviour, priced
   in §3.
4. **Declaration letters cost ~2–6 minutes** after the last
   edit (Falk: last test 23:09:22 → complete 23:10:59). Cheap.
5. **The one true loss: the times lane.** Two refusals
   (22:29, ~22:39) removed the car's centre — the migration
   spine and ST-04 — before a line existed. No pipeline fix
   recovers a seat that never opens; the cost lands on the
   merge instead (the composed migration shrinks to Falk's
   single column, and seam S-3's provider rewire stays
   unassigned — Konto flagged it, §2).
6. **Cartography ran ahead of construction.** Konto, seated
   22:36, filed Edition 1 at ~23:06 — before Suki's first hunk
   and before Tamm's lane existed at survey time. The map was
   waiting for the builders, not chasing them. That ordering
   is why §2's saved minutes are real.

---

## 2 · REN'S MERGE, PRICED FROM KONTO'S MAP

Konto's collision table: **4 textual (A) · 4 adjacent (B) ·
9 shared-distinct (C) · ~7 single-party files (D)**, one
composed migration, a 10-seam ledger. Prices below are ESTIMATE
minutes of Ren's hand time, per item, at merge-day pace
(compose, re-read, keep both comments, no compile per Law 11 /
the 23:06 ruling).

### 2.1 The bill

| Item | What Ren's hand must do | EST |
|---|---|---|
| A-1 armedMono anchor | Two +11-line functions appended after the identical 6-line context (Falk's sniper gate, Stempel's stamp write). Place side by side; order irrelevant | 10 |
| A-2 seal-transition tail | Imke's +40-line get()/bind replacement re-based onto Wache's adopted-seal tail (his 8 hunks reach the same :288–312). The one true re-write in the car | 30 |
| A-3 startPeekLease | One function, three authors: Pastille's grant rework as base, Suki's unlisted-package guard at its head. (Rurik's duration swap does not exist — refused seat — so tonight's form is two authors) | 15 |
| A-4 settings block | Falk's row read at :179–180 must survive Bodo's deletion of the ifEmpty fallback at :182–185. One sitting, consecutive lines | 10 |
| B-1…B-4 | WatchStore band + Warrant default-value law; Shield header (Bodo count + Wache line as ONE edit); Repository break span; VM peek cluster with Pastille seated after Suki | 40 |
| C-1…C-9 | Nine shared files, distinct regions. The whale is C-5, MonolithViewModel.kt — five authors, function-by-function pass ≈25 min alone; the other eight ≈6 min each | 75 |
| D files | Single-party: copy over, spot-read | 15 |
| Migration | Compose ONE 11→12. At the map's 4-column form: 15. As tonight actually stands (Falk's column only, entity order + version bump): | 8 |
| **Car total** | | **≈200 min (3⅓ h)** |
| Adjacent lanes | Warrant / nofire / pill are written code entering the same pass (their non-collision files ≈16 files + nofire's heartbeat rewrite) | +40–55 |
| **Whole pass** | | **≈4–4¼ h of Ren's hands** |

### 2.2 What the cartographer already saved

Priced against the same merge attempted mapless:

| Save | Mechanism | EST saved |
|---|---|---|
| A-1 discovered on paper | Identical anchor = guaranteed patch conflict; found at merge it costs confusion + a coin-flip risk of silently dropping one function (the warrant stamp OR the sniper gate) | ~20 min + one correctness risk retired |
| S-2 relay | Stempel's Warrant constraint (new field MUST carry a default) sat in a refused agent's dead inbox; Konto relayed it to Suki before she wrote UNA2-03. Blind, she breaks TactileDeckScreen's positional construction and the fix costs a re-edit cycle | ~30–45 min |
| S-5 pre-warning | Konto told Imke that Wache's heartbeat hunks already cover her crime scene (RoutineHeartbeat:142) BEFORE her final form — she composed against his landing instead of re-basing at merge | ~20 min (moved from merge to lane, where it's cheaper) |
| Migration order law | Entity field order must equal ADD COLUMN order or Room's schema identity fails the chain test — a failure that costs a full merged-suite cycle (~6 min) plus diagnosis archaeology | ~15 min |
| S-3 flag | The EditableTimesProvider rewire is UNASSIGNED. Unflagged, the columns land and nothing reads them — UNA-03's disease in a new coat, discovered by a user, not a test | not minutes — a shipped dead feature, retired as a risk |
| **Total** | | **≈85–100 min + 2 correctness risks** |

### 2.3 What no cartography can save

≈2–2½ hours of the bill is irreducible: A-2's re-base is a
re-write, not a placement; A-3's composition is order-sensitive
semantics; C-5 requires a human to read ~1,600 changed lines
across five authors and hold each lane's intent while seating
the next. The map converts surprise into sequence — Ren starts
the merge already knowing the order, the anchors, and the two
places the text fights back. It does not shorten the reading.
Anyone selling a bigger saving than §2.2 is selling the map
twice.

---

## 3 · THE COMPILE-ONCE TRADE, MEASURED

Sarthak's order (23:06 IST): no lane compiles; one compile +
full suite on the merged tree. Tonight all five lanes obeyed
(Falk's letter: "no queue claim taken, none held" — and the
order landed mid-flight, before any lane had compiled anyway).

### 3.1 What the skipped compiles would have cost

Baselines on record (Waage): successful assemble median 101 s,
successful test median 192 s (era-mixed); the Oct-29 multi-lane
era median is 171 s per run; the trunk suite's pure execution
is ~67 s against ~192 s wall — **two-thirds of every test run
is startup and overhead, not work.**

- One lane's at-completion pair (assemble + test) ≈ **293 s
  ≈ 5 min** of compiler wall. Five lanes ≈ **24 compiler-
  minutes**, of which ≈10 min is the same startup overhead
  paid five times to re-prove trunk-common code.
- Wall-clock is worse than compiler-minutes: the Law-3 queue
  caps at 2 concurrent runs, and tonight the look lanes,
  nofire, pill and warrant were also live. Queue-waits
  evidence: a compliant lane measured waiting ≥150 s, and
  shieldpick had budgeted a 1,500 s (25 min) wait as a NORMAL
  event. Five extra run-pairs entering that queue prices at
  **+20–40 min of spread wall**, plus the contention tax
  (era proxy: +50% median) applied to everyone else's runs.
- Failure exposure: today's recorded failure rate is 27%
  (26/98 runs); the real-compile-error class is 6/98 ≈ 6% per
  run. Five lane compiles ≈ a 1-in-4 chance at least one lane
  burns a failed 3–8 min run plus a fix cycle mid-flight.

### 3.2 What a red merged suite costs

- One merged iteration ≈ assemble (~101–171 s) + full suite
  (~192 s wall) ≈ **5–6 min**.
- The feared cost — five lanes' errors surfacing at once, no
  attribution — is pre-paid by Pruefer's standard: every cure
  carries NAMED tests in its own file, designed to fail on
  trunk. A red merged test names its lane by construction.
  Compile errors attribute by file ownership in Konto's table.
- Expected extra iterations: 1–3 → **6–18 min** plus Ren's
  diagnosis. Break-even against §3.1 only at 4–5 extra full
  iterations — implausible given the lanes' grep-verified
  landings (Imke's letter: every file grep-verified after
  landing; Bodo's REPAINTED-guard self-checks) and the
  reading-heavy first phase §1.3 documents.

### 3.3 Recommendation

**KEEP compile-once for multi-lane cars, with two zero-gradle
amendments and one scoping line:**

1. **Declare-time grep census becomes a lane-close
   requirement** (tonight it happened ad hoc — Imke's
   grep-verified landing, Pruefer's grep companions): every
   new symbol's call sites grepped, every killed string
   grepped tree-wide, results in the declaration letter. This
   is the cheap syntax-and-wiring sanity a lane compile used
   to buy, at ~2 minutes of shell instead of ~5 minutes of
   queue.
2. **The cartographer's final edition must land before the
   last builder declares** — tonight it did (Edition 1 at
   23:06; two lanes still writing). The merge order is then
   executable the hour the last lane closes, not surveyed
   after.
3. **Scope:** compile-once binds cars of ≥3 lanes or any car
   carrying an A-class collision. A single-lane, zero-
   collision fix may compile in-lane: one run, trivial
   attribution, ~5 min of queue buys genuine proof. The rule
   is a multi-lane instrument; taxing solo work with it buys
   nothing.

---

## 4 · ONE INVENTION — THE REHEARSAL MERGE (Generalprobe)

**Mechanism.** At each lane's declaration, a script (shell +
`diff`/`patch`, NO gradle) applies every declared lane's diff
to a scratch copy of trunk, in Konto's map order, and writes
two outputs: (a) a hunk-level census — APPLIES / FAILS, per
hunk, per lane, in order; (b) the composed scratch tree.
Ren then does the real merge against a conflict list that
already exists, and afterwards diffs his hand-merged tree
against the rehearsal tree: every divergence is either his
deliberate composition or his error — both visible, nothing
silent. Konto's Edition 2 stops being a survey and becomes
this census attached to the map.

**What it buys, from tonight's evidence.** A-1 (identical
anchor, +11/+11 after the same 6 lines) fails LOUDLY in a
rehearsal in seconds, at declaration time — tonight it was
caught by a Keeper reading two trees by eye, which worked
and does not scale. The census also re-prices §2 mechanically:
the A/B classes stop being a surveyor's judgement and become
counts.

**Price.** Build: SMALL–MEDIUM, ~60–90 min once (Methods —
lane list + map order in, census + scratch tree out).
Run: ~1–2 min per car (1,600 changed lines is seconds of
patch; the time is the report). Upkeep: ~zero; it reads what
the car already produces. No queue claim, no daemon, no RAM —
it runs inside the box's limits on a bad night.

**Failure mode — named, not buried.** Textual applicability
≠ semantic composition. A-2's tail and A-3's lease can APPLY
cleanly in the wrong order and still be false; a green
rehearsal census could teach Ren to trust the composed tree
where his hand was the whole point. Defence, built into the
instrument: the census header prints its own limit —
"this proves patches land, not that the result is true" —
and the rehearsal tree is evidence, never the merge source.
Second failure mode: staleness — a lane editing after
declaration (Tamm and Suki were both still writing at
capture) makes the census a photograph of a moving car;
mitigation is mechanical (mtime pin in the census; a re-run
costs 2 min, so re-run at every declaration).

---

## NEXT (Law 10)

(a) **What I want next:** the merge itself, timed — Ren's
actual minutes per collision class against §2.1's estimates,
so Edition 2 of this study replaces ESTIMATE with MEASURED
and the next car's price is a fact, not a model. I need the
merge log (or a stopwatch letter) from whoever watches it.

(b) **What I could do unassigned:** the Rehearsal Merge script
itself (§4) — one seating, no gradle, ready before the next
car seats; or the same timeline treatment for the machinery
lanes (warrant/nofire/pill), whose merges ride in the same
pass and whose prices §2.1 could only bracket.

— Takt, Methods · timing/consolidation · Oct 29, 2026
