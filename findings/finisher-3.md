# Issue #17 — Snapshot #3: THE RESTORE-TEST PROCEDURE (FINISHER-3, Anker cell)

**Procedure only — no restore test was run in the preparation of this document.**
Sources: `findings/planner.md` §8 (the independent restore-test sketch this document
replaces and hardens), `~/workspace/preservation/run_snapshot3.py` (the runner, whose
`sha256_of` / `verify` convention is the authoritative hash semantics), the WORK-REGISTRY
snapshot block (as preserved in `studio/plan/WORK-REGISTRY.md`), `snapshot1-run.log`,
and the surviving sets on disk, read-only, Fri 2026-10-30 ~03:20 IST.

---

## 0. Why this procedure exists — the #1 disease, named

"Snapshot #1" names **two different sets**, and every confusion in the record flows
from the ordinal:

| Identity (full name — the only legal name) | Registry status | Manifest sha256 | Files / bytes |
|---|---|---|---|
| `monolith-snapshot-001-2026-10-29` (attempted ~10:33 IST, tar-era/runner-v1) | **FAILED** — A and B **NOT WRITTEN**, debris cleaned | none (no archive exists) | 0 / 0 |
| `2026-10-29_1148_manual-first` (the surviving set, "Snapshot #1 re-test") | COUNTED | `11ada2f4fa16e947b28c1911f7c1ce8bc953dbd952e68a375c9dce3fe4e05fcf` | 3,325 / 568,973,290 |
| `2026-10-29_2104_manual-post-batch7-pre-art-merge` ("Snapshot #2") | COUNTED, restore PASS independently proved | `0de9adf208f7e963ecbc50357944fac4e09b2000194397e5cd90e740a174866c` | 5,999 / 974,007,921 |

A PASS stamped against the ordinal — or against a name without its manifest hash —
can drift from the set that earned it to a set that didn't. That is what happened to
#1: the FAIL belongs to `monolith-snapshot-001-2026-10-29`; the PASS was earned by
`2026-10-29_1148_manual-first`; read by ordinal, they swap.

**The rule this whole procedure hangs on:**

> **A verdict is a triple, or it is nothing: (full snapshot name, manifest sha256,
> verdict).** No step, stamp, log line, registry entry, or spoken report in this
> procedure may carry a verdict without all three. An ordinal ("#1", "#3") may appear
> only as a comment next to a full name, never as the identifier.

---

## 1. Inputs — frozen before anything is copied

The tester writes these down in the test log (§9) **before** touching the set.
Two of them must come from records *independent of the set itself* — a set may not
be its own only witness.

| Input | Symbol | Source (independence rule) |
|---|---|---|
| Full snapshot name | `NAME` | The directory name in Location A and Location B — must be byte-identical in both |
| Expected manifest sha256 | `EXPECT_SHA` | The run log's `SUMMARY` / `MANIFEST_SHA256` line **and** the WORK-REGISTRY entry. The two must agree with each other first; `SNAPSHOT.md` inside the set is a third check, never the source |
| Expected file count | `EXPECT_N` | Run log `FILES` / registry `files:` field |
| Expected total bytes | `EXPECT_BYTES` | Run log `BYTES` / registry `bytes:` field |
| Tester + start time | — | Who is running this, `YYYY-MM-DD HH:MM IST` |

If no independent record of `EXPECT_SHA` exists for a set, the test may still run,
but its ceiling is **UNVERIFIABLE-PROVENANCE** (§8) — it can never stamp PASS.
(This is the exact trap of a set whose only PASS evidence is its own SNAPSHOT.md.)

Locations, fixed for every test:

- **Location A:** `~/workspace/preservation/monolith-snapshots/<NAME>`
- **Location B:** `~/workspace/your_files/monolith-archive/snapshots/<NAME>`
- **Scratch (restore copy):** `~/workspace/preservation/restore-test/<NAME>-retest-<HHMM>` —
  outside both locations, never inside A or B, never the runner's own
  `restore-test/<NAME>` path (that path belongs to the run's self-test; an
  independent test that reuses it can be mistaken for it).

Pre-flight: free disk at least 2x `EXPECT_BYTES`; A and B both exist; the set is
quiescent (no snapshot run in flight against it).

---

## 2. Gate 1 — A/B manifest identity (before any restore copy)

Commands (substitute the real paths for `<A>` and `<B>`):

- `sha256sum <A>/MANIFEST.sha256 <B>/MANIFEST.sha256`
- `cmp <A>/MANIFEST.sha256 <B>/MANIFEST.sha256` — must be byte-identical
- In both `<A>/SNAPSHOT.md` and `<B>/SNAPSHOT.md`, read the `manifest sha256:` field.

Pass conditions — **all four**, or stop with verdict **FAIL-IDENTITY** (no restore
copy is made; a set whose two homes disagree about what it is has nothing to test):

1. `sha256(<A>/MANIFEST.sha256)` == `EXPECT_SHA`
2. `sha256(<B>/MANIFEST.sha256)` == `EXPECT_SHA`
3. `cmp` clean — A and B manifests byte-identical, not merely same-hash-by-report.
4. The `manifest sha256:` field inside both SNAPSHOT.md copies == `EXPECT_SHA`.

Record all four readings in the log. "The manifests matched" without the hash
printed is not a record.

---

## 3. Gate 2 — Manifest parse (the truncation gate)

Parse `<B>/MANIFEST.sha256` strictly, before copying anything:

- Every line matches the format: 64 lowercase hex chars, exactly two spaces, then a
  relative path. Zero malformed lines tolerated.
- The file ends with a newline; the final line is complete.
- Lines are sorted by path; no path appears twice.
- Line count == `EXPECT_N`.

Any violation stops the test with verdict **FAIL-MANIFEST-INVALID**, and the log
records the measured facts: total lines, malformed-line count, first malformed line
number and its raw content, whether the final line is terminated.

Why this gate is absolute: a truncated manifest does not merely fail to prove the
files past the cut — it makes them *unmentionable*. Files on disk with no manifest
line cannot be verified, cannot be counted as present-and-good, and cannot be
distinguished from files that were never copied at all. **A set with a truncated
manifest has a verdict ceiling of FAIL, no matter how many of its parseable lines
verify OK.** There is no "PASS (partial)" in this procedure.

---

## 4. Step 3 — The restore copy (from B, links preserved)

Copy **Location B** (never A — B is the copy of the copy; restoring from B is what
proves the archive end of the chain) into scratch, symlinks preserved as links:

- `shutil.copytree(B, SCRATCH, symlinks=True)`, or `cp -a B/. SCRATCH/`.
- Immediately census the scratch copy: `find SCRATCH -type l` — the link count and
  link paths must equal the census of B taken the same hour. A copy tool that
  follows links, drops links, or materialises them as files has already falsified
  the test; stop, verdict **FAIL-COPY**.

---

## 5. Gate 4 — Full sha256 verification of the restored copy

### 5a. The authoritative check (runner convention)

The runner's `sha256_of` is the semantics the manifest was built under, and it is
the only semantics under which every entry is verifiable:

- Regular file: sha256 of its bytes.
- Symlink: sha256 of its **target string** (`os.readlink(path)` encoded UTF-8) —
  a dangling link has no content to hash; the target string is its content.

Run the equivalent of the runner's `verify(SCRATCH, entries)` over every manifest
line: the entry exists at its relative path in scratch, and its hash under the
convention above equals the manifest hash. Output tallies, all printed in the log:

```
VERIFY set=<NAME> manifest=<EXPECT_SHA full>
regular_ok=<R>  regular_fail=<list, or 0>
link_ok=<L>     link_fail=<list, or 0>
total_ok=<R+L>  total_fail=<count>  manifest_lines=<N>
```

Pass: `total_fail == 0` and `total_ok == N == EXPECT_N`.

### 5b. The `sha256sum -c` cross-check — and how the symlink lines are counted

Inside scratch, `sha256sum -c MANIFEST.sha256` is run as a second, independent
implementation — with this accounting, fixed in advance so nobody improvises at
the terminal:

- Every **regular file** must print `: OK`. A regular file printing
  `FAILED` (checksum mismatch) or `No such file or directory` is a failure,
  full stop.
- `sha256sum -c` **cannot** verify a symlink entry under the manifest convention:
  it tries to open the link's target and hash *its* bytes. For a dangling fixture
  it reports the line unreadable (`No such file or directory` / `FAILED open or
  read`). For a link whose target string is an absolute path back into the live
  source tree, it may open the *original* file and report a checksum `FAILED` —
  that reading is about the original, not the restored link, and is equally void.
- Therefore each manifest line that names a symlink is scored **exactly once, in
  5a only**, by this test: `readlink` the restored link, hash the target string,
  compare to the manifest hash. The `sha256sum -c` output for that line is
  transcribed into the log marked `LINK — scored in 5a`, and counted in
  **neither** the OK tally **nor** the failure tally of 5b.
- The reconciliation is arithmetic, and it must close exactly:

```
sha256sum -c OK lines            == R   (regular files)
sha256sum -c non-OK lines        == L   (every one a census symlink, listed by path)
5a link_ok                       == L
R + L                            == N == EXPECT_N
```

A non-OK line in 5b whose path is **not** in the symlink census is a failure
(verdict **FAIL-CHECKSUM**). A census symlink that somehow prints `: OK` in 5b
has been followed and content-hashed — investigate before crediting anything;
it is **not** an extra pass, and 5a still scores it.

### 5c. The symlink census — taken from the set, never assumed

`L` and the link paths are enumerated from the actual set at test time
(`find <B> -type l`), listed by path in the log, and each is classified:

- **Dangling by design** — the queue-sim ghost fixtures
  (`*/queue-sim/*/compile-ghost.flag`, target `/nonexistent-target`). Expected
  hash-of-target-string for that target:
  `fcb6623a103ea8570188ca692e65f00707072812b2a969c7a9099bc6655f0e21`
  (= sha256 of the string `/nonexistent-target` — a worked anchor: if a ghost
  fixture's manifest hash is not this value, something other than the fixture
  changed). Count for Snapshot #3: **3** if the studio tree is included per the
  planner/FINISHER-2 decisions, **+42 more** if the issue cells (each carrying a
  studio copy) are also included, **0** if neither is. The census decides; the
  SNAPSHOT.md note's number must equal the census, not the script's stale text.
- **Absolute probe links** — e.g. `lanes/merge-batch9/guh-test/grund-probe/sl_0..sl_19`
  (20 links, if `guh-test` is kept): targets are absolute paths into the live
  source tree, so they dangle *inside the restore copy* while resolving outside
  it. Same scoring: target string only, in 5a only.
- Any link in the census that is **neither** classified above is named in the log
  and still scored in 5a — classification explains links; it never excuses one
  from verification.

---

## 6. Gate 5 — File-count and byte cross-checks (on the restored copy)

All four must close exactly; any miss is **FAIL-COUNT**:

1. Census: `find SCRATCH -type f -o -type l | wc -l` == `N + 2`
   (the +2 are `MANIFEST.sha256` and `SNAPSHOT.md`, the only two files the
   manifest excludes — by name, at payload root, per spec S4).
2. Break-down agrees: regular files in scratch == `R + 2`; symlinks == `L`.
3. Bytes: sum of `lstat` sizes over the `N` manifest paths in scratch ==
   `EXPECT_BYTES` == the `total bytes:` field in scratch's SNAPSHOT.md.
   (Link sizes count as the `lstat` size of the link itself, exactly as the
   runner's `build_manifest` counted them — mixing conventions manufactures a
   byte mismatch out of thin air.)
4. No extras: every file/link in scratch is either a manifest path or one of
   the +2. An extra file is reported by name even if all hashes pass — the
   runner's `verify` never checks for extras (planner D8); this procedure does.

---

## 7. Gate 6 — Content spot-restores (hashes prove bytes; this proves content)

Open real content in the restored copy — all five, all must pass
(**FAIL-CONTENT** otherwise). "Open" means read and eyeball the head of the
file; a zero-byte or binary-garbage result fails even with a matching hash
(a hash can faithfully preserve an empty shell — the 2102 attempt produced
exactly those).

1. **Trunk source:** the first `.kt` under `trunk/app/src` (walk order, as the
   runner picks it) — readable Kotlin, head non-trivial (more than 20
   non-whitespace characters, the runner's own spot threshold).
2. **A record:** `records/BUILD-CHANGES.md` — readable, real changelog head.
3. **A lane file unique to this set's era** — for Snapshot #3, a file under
   `lanes/merge-batch9/` that exists in no earlier set. (Spot files must be
   chosen from *this* set's manifest. The runner's hardcoded spots —
   `swarm/plan/SNAPSHOT-SPEC.md`, `swarm/mail/ren/*.md` — are dead paths after
   the Oct 30 cleanup and would FAIL a perfect copy; planner defect D2. If the
   studio tree is included under a new root, the equivalent studio file from
   *that* root is spot 3b.)
4. **This set's own SNAPSHOT.md** in the restored copy: header name == `NAME`,
   manifest field == `EXPECT_SHA`, restore line still reads `PENDING` at test
   time (proof the copy predates the stamp — a restored copy that already
   claims PASS was stamped before it was tested, or is not the copy under test).
5. **The APK byte-compare (the one binary proof):**
   `sha256sum SCRATCH/apks/monolith-batch9.apk` must equal **both**
   (a) the manifest line for `apks/monolith-batch9.apk`, and
   (b) an independent anchor: `sha256sum ~/workspace/clone-inspect/monolith-batch9.apk`
   (the live shipped file) while it exists. If the live source no longer
   exists, the anchor is Location A's copy of the same path — and the log
   records **which anchor was used**. All three readings are printed in the
   log; "APK matched" without the hashes is not a record.

---

## 8. Verdicts — the complete grammar

PASS requires every gate green: identity (Gate 1), parse (Gate 2), copy
fidelity (§4), full verification (§5, both implementations reconciled),
counts/bytes (§6), content (§7). Anything else is one of:

| Verdict | Meaning |
|---|---|
| `PASS` | All gates green for `(NAME, EXPECT_SHA)`. |
| `FAIL-IDENTITY` | A/B manifests disagree with each other or with `EXPECT_SHA`. |
| `FAIL-MANIFEST-INVALID` | Parse gate: malformed/truncated/unsorted/duplicate lines, or line count differs from `EXPECT_N`. |
| `FAIL-COPY` | The B-to-scratch copy altered the tree (links followed/dropped, census differs). |
| `FAIL-CHECKSUM` | A manifest entry missing or hash-mismatched in the restored copy under §5a semantics. |
| `FAIL-COUNT` | Census, breakdown, bytes, or extras check (§6) does not close. |
| `FAIL-CONTENT` | A spot file unreadable/trivial, or the APK byte-compare misses either anchor. |
| `UNVERIFIABLE-PROVENANCE` | No independent `EXPECT_SHA` record exists; test ran, no PASS may be stamped. |

No other wording is a verdict. In particular: a runner's own `RESTORE-TEST PASS`
line in its run log is the run's *self-test* result — it enters this procedure
only as the provenance record for `EXPECT_SHA`/`EXPECT_N`/`EXPECT_BYTES`, never
as a substitute for an independent test.

---

## 9. Where the result gets stamped — both locations, the registry, and the log

A result exists only when it is stamped in **all four** places, with the same
triple in each. Stamping order matters: log first (it is the evidence), then
both SNAPSHOT.md copies, then the registry.

### 9a. The test log (evidence, written first, never edited after)

`~/workspace/preservation/restore-test-<NAME>.log` — contains the frozen inputs
(§1), every gate's measured readings (hashes, tallies, census list, spot
results, APK hashes, anchor used), and the final verdict line:

```
VERDICT set=<NAME> manifest_sha256=<EXPECT_SHA> result=<PASS or FAIL-class> tester=<name> at=<YYYY-MM-DD HH:MM IST>
```

### 9b. SNAPSHOT.md — in Location A **and** in Location B (identical stamp in both)

The runner leaves `- restore-test: PENDING` in both copies. The independent
test replaces that line — in both copies, byte-identical replacement — with
exactly this wording for a pass:

```
- restore-test: PASS <YYYY-MM-DD HH:MM IST> | set: <NAME> | manifest sha256: <full 64-hex> | independent re-test from Location B, <N> files (<R> regular + <L> symlinks by target-string hash), 0 checksum failures, <EXPECT_BYTES> bytes | spots: trunk-kt, records/BUILD-CHANGES.md, lane-era file, SNAPSHOT.md all read OK; apks/monolith-batch9.apk byte-identical to <anchor: live source | Location A> | tester: <name> | log: restore-test-<NAME>.log
```

For a failure, the same skeleton, verdict word and failure class swapped in,
with the measured defect named (counts, paths, line numbers) — never a bare
"FAIL":

```
- restore-test: FAIL-<class> <YYYY-MM-DD HH:MM IST> | set: <NAME> | manifest sha256: <full 64-hex or "none readable"> | <measured defect, e.g. "manifest parse: 3,104 lines, line 3,105 malformed/truncated, final line unterminated; 221 on-disk files have no manifest entry and are unverifiable"> | tester: <name> | log: restore-test-<NAME>.log
```

**Validity rule, readable by any later reader at a glance:** a restore line is
valid only if the `set:` name equals the SNAPSHOT.md header name in the same
file **and** the `manifest sha256:` in the restore line equals both the
`manifest sha256:` field above it in the same file **and** the actual
`sha256sum` of the `MANIFEST.sha256` sitting in the same directory. A PASS
line failing any of those three equalities is void on its face — it is a PASS
*of some other set*, which is precisely the #1 accident, made detectable in
ten seconds.

**Re-tests append, never overwrite.** If a set already carries a stamped
(non-PENDING) restore line, a later test adds a second line,
`- restore-test-2: ...`, with its own date/tester/log. History is never
rewritten; a verdict that changes leaves both readings on the page.

### 9c. The WORK-REGISTRY snapshot entry

Append/update the entry **under the full name** (never under an ordinal),
carrying: name, `manifest sha256: <full>`, files, bytes, and the restore line
in the same triple form:

```
restore-test: <PASS or FAIL-class> <date/time IST> — independent re-test of set <NAME>, manifest <full 64-hex>, from Location B, <N> files, <failures> checksum failures | status: <COUNTED or FAILED>
```

### 9d. Cleanup and report

After stamping: delete the scratch copy; leave A and B otherwise untouched.
The report to Sarthak, per his presentation rule, is one line and carries the
triple: name, manifest sha256 (short form acceptable *in chat only*, full
form in all four records), file count, bytes, verdict.

---

## 10. Repair path for #1 — the honest re-test of the surviving set

This section is a procedure and an expectation. The re-test itself is Ren's to
run; nothing in this section was executed as a restore test.

### 10a. Step zero — split the ordinal (do this before any command)

The registry holds two entries that both answer to "#1". The re-test names
its subject in full, in its log's first line, and refuses the other:

- `monolith-snapshot-001-2026-10-29` — **not the subject.** Registry: FAILED,
  A/B NOT WRITTEN, manifest none, 0 files, debris cleaned. There is nothing
  on disk to restore-test; its honest verdict is already written and this
  procedure cannot improve it. Any PASS sentence anywhere that traces to
  this identity, or to the tar-era attempt it replaced, is void.
- `2026-10-29_1148_manual-first` — **the subject.** The surviving set in both
  locations.

### 10b. The re-test, gate by gate (what "honest" looks like for this set)

1. **Inputs (§1):** `NAME = 2026-10-29_1148_manual-first`;
   `EXPECT_SHA = 11ada2f4fa16e947b28c1911f7c1ce8bc953dbd952e68a375c9dce3fe4e05fcf`,
   taken from `snapshot1-run.log`'s SUMMARY line and the registry entry —
   which agree — *not* from the set's own SNAPSHOT.md (that is checked third).
   `EXPECT_N = 3325`; `EXPECT_BYTES = 568973290`.
2. **Gate 1 (§2):** sha256 of both MANIFEST.sha256 copies, `cmp`, both
   SNAPSHOT.md manifest fields — four readings, all against `EXPECT_SHA`.
3. **Gate 2 (§3) — the decisive gate for this set:** strict-parse B's
   manifest and count. This is where the reported truncation, if real in the
   set presented, ends the test (see 10c).
4. **Copy + census (§4, §5c):** restore from B. Expected census for this set:
   **0 symlinks** (the v2-era runner copied by content; no links survive in
   either location — measured read-only today). `L = 0` makes §5b trivially
   strict: *every* `sha256sum -c` line must print `: OK`; a single non-OK
   line of any kind is a failure, with no symlink accounting to hide in.
5. **Gate 4 (§5):** all 3,325 entries under §5a semantics.
6. **Gate 5 (§6):** census == 3,327 (3,325 + 2); payload `lstat` bytes ==
   568,973,290; no extras.
7. **Gate 6 (§7):** spots per the runner's originals, which for this set
   still exist: `swarm/plan/SNAPSHOT-SPEC.md`, first `.kt` under
   `trunk/app/src`, a `swarm/mail/ren/*.md` letter — plus the APK
   byte-compare. Anchor caveat, recorded in the log: the live source root no
   longer holds the batch 1–3 APKs (only batch 8/9 remain at
   `~/workspace/clone-inspect/`), so the anchor for e.g.
   `apks/monolith-batch3.apk` is **Location A's copy**, and the log says so.
   Triple agreement required: restored hash == manifest line == A-copy hash.
8. **Stamp (§9):** append-only — the existing line
   `- restore-test: PASS 2026-10-29 11:49 IST from Location B, 3325 files, 0 checksum failures`
   is **never edited or deleted**. It is, note, exactly the kind of stamp this
   procedure exists to supersede: name-bound only via the file it sits in, no
   manifest hash, no tester, no log reference. The re-test adds
   `- restore-test-2: ...` in the §9b wording (full triple) in **both**
   locations, writes `restore-test-2026-10-29_1148_manual-first.log`, and
   updates the registry entry under the full name.

### 10c. Expected verdict — given the truncated manifest

The cell's verifier reports the surviving #1 set as registered FAILED with a
**truncated manifest**. Taking that finding at face value, the expectation
under this procedure is determined, not a matter of judgment:

> **Expected verdict: `FAIL-MANIFEST-INVALID` for the set as registered.**
> Gate 2 stops the test before any restore copy is made: a truncated manifest
> yields malformed/unterminated tail lines and a line count short of the
> claimed 3,325, so Gate 5's count check would fail by the same margin, and
> every on-disk file past the cut is unverifiable by construction. Even if
> all parseable lines were to verify OK in §5a, the verdict cannot rise above
> FAIL — §3's ceiling is absolute, and stamping anything else would repeat
> the original accident in a new form. The correct stamps are the §9b FAIL
> wording (with the measured line counts in it), the registry entry left
> FAILED, and the 11:49 PASS line permanently re-read as belonging to the
> run whose SUMMARY (`snapshot1-run.log`: name `2026-10-29_1148_manual-first`,
> manifest `11ada2f4...e05fcf`) it matches — a PASS of *that* tested set at
> *that* minute, never a transferable property of the ordinal "#1".

**The reconciliation duty (one paragraph, because the record currently
contradicts itself):** the parse gate, not any report — including this one —
is the arbiter. Two read-only parses made today (the critic's strict-regex
parse, findings/critic.md C-13, and this seat's own read-only recount) found
the surviving 1148 manifest in A and B complete: 3,325 lines, 0 malformed,
newline-terminated, hash `11ada2f4...e05fcf` in both homes. If the re-test's
Gate 2 confirms that, the expected verdict flips: the set becomes
PASS-eligible (subject to Gates 4–6), and it is the *flaw record* that must
be repaired — the truncation then belongs to the superseded
`monolith-snapshot-001` / tar-era debris, and the registry must say so under
both full names. Either way the procedure produces one of two honest
outcomes — a FAIL with measured line counts, or a PASS bound to
`(2026-10-29_1148_manual-first, 11ada2f4...e05fcf)` — and no third outcome in
which a verdict floats free of the set that earned it. That is the repair.

---

## 11. Anti-confusion rules (summary — post above the desk)

1. Verdicts travel as triples: **(full name, manifest sha256, verdict)**.
   Ordinals are commentary, never identifiers.
2. `EXPECT_SHA` comes from two independent records that agree; a set is
   never its own only witness.
3. Restore is always **from B**; A is the comparison anchor, never the source.
4. Symlinks are scored **once**, in §5a, by target-string hash. In
   `sha256sum -c` they are transcribed and reconciled arithmetically —
   never counted as failures, never silently skipped, never double-counted
   as passes.
5. A truncated or unparseable manifest caps the verdict at FAIL. Partial
   verification is not a kind of PASS.
6. Stamps are append-only, identical in A and B, mirrored in the registry
   and the test log — four places, one triple.
7. The runner's self-test and an independent re-test are different events
   with different stamp lines (`restore-test:` vs `restore-test-2:`) and
   different scratch paths. Neither may borrow the other's wording.

— FINISHER-3

---

## 12. ADDENDUM — correction after reading the actual verifier report

*Appended on landing of `findings/verifier.md`, which reached this seat only
after §§0–11 were written. The procedure (§§1–9, 11) is unchanged — it was
built to be premise-proof. What changes is §10c's attribution, corrected here,
append-only, per this procedure's own rule that history is never overwritten.*

The verifier's measured findings are the **opposite** of the summary this
seat was briefed with:

- **Claim 1 (#1 = 3,325 files, restore PASS): PROVEN** — full re-hash of A
  and B today (3,325/3,325 OK each), bytes 568,973,290, PASS backed by the
  contemporaneous `snapshot1-run.log` ceremony record.
- **Claim 3 (#1's manifest truncated mid-line): DISPROVEN** — 3,325/3,325
  well-formed lines, trailing newline present, final line complete, manifest
  entry set == on-disk set in both directions, manifest hash unchanged since
  creation. This matches the critic's C-13 and this seat's own read-only
  recount (§10c's reconciliation paragraph).
- **Claim 2 (#2 = 5,999 files, restore PASS): count/integrity PROVEN**
  (5,999/5,999 OK in both homes, 974,007,921 bytes); **the historical 21:46
  restore PASS itself is UNPROVABLE as an event** — no run log or restore
  artifact survives for #2, only the stamped line in its own SNAPSHOT.md.

Consequences for §10, stated plainly:

1. §10c's "expected verdict FAIL-MANIFEST-INVALID, given the truncated
   manifest" was conditional on a premise that does not survive measurement.
   The condition is void. Under the verifier's findings, the §10b re-test of
   `2026-10-29_1148_manual-first` is **PASS-eligible**: Gate 2 is expected to
   pass clean, and the re-test's job is to convert an already-proven set's
   weak old stamp into a §9b stamp (full triple, tester, log) — not to
   rescue it from a truncation it does not have.
2. The live defect the verifier actually exposed sits in **#2**, not #1: a
   PASS stamped only inside the set's own SNAPSHOT.md, with no surviving
   run log, is exactly the self-witness case §1's provenance rule and §9a's
   mandatory test log exist to prevent. For any future claim about #2's
   restore, the honest wording is the verifier's: integrity PROVEN today;
   the 2026-10-29 21:46 event attested only by its own stamp.
3. The §0 identity table stands as written (it is registry fact, not
   premise): two identities answer to "#1", and the ordinal is still banned
   as an identifier. The disease was real; the patient list in the brief
   was wrong.

— FINISHER-3 (addendum)
