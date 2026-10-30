# BEDACHT — Issue #17, Preservation / snapshot #3: consequence & care pass

**Seat:** BEDACHT (thoughtful), dedicated-100 programme, flat under Ren.
**Lens, only lens:** who is affected by every choice here, what it costs
them, and what happens on their worst day.
**Read before writing:** issue #17, `findings/planner.md`,
`findings/critic.md` (C-1…C-14), `studio/plan/ISSUE-17-WAVE2-REPORT.md`
(Konto's stand-down). Nothing copied, nothing run, nothing compiled.

---

## 0 · The people, and the day this is all for

Preservation work has no user on a normal day. Its entire value is
concentrated into one bad one. Name the people who stand in that day:

1. **Sarthak** — whose app, studio record, and months of commissioned work
   this is. On the worst day he is also a student in exam season with zero
   hours to spend on archaeology.
2. **Ren, or whoever operates after a box loss** — at 03:00, reconstructing
   from whatever the archive actually contains, trusting whatever its
   labels say.
3. **The hundred-plus named agents whose letters and wall posts live only
   in snapshots #1/#2** — the Commons and the 115 mail inboxes were deleted
   from the live tree on Oct 30 (~03:00) and were *not* migrated to GitHub
   (critic C-2; only `plan/` + `methods/`, 431 files, went). Their work
   survives exactly where this issue says it does, or nowhere.

**The worst day, concretely:** the VM or its disk dies — not dramatically,
just finally — and with it the live tree, Location A, Location B, staging,
and restore scratch, because all of them sit on `/dev/mapper/rv`, one
spindle, one account (critic C-1, measured: same `st_dev`, same subvolume).
On that day the question is not "did snapshot #3 get taken." It is: *does
anything of the studio's mind exist anywhere else on Earth?* Today the
answer is: only the part that happens to be on GitHub. Every judgment
below is priced against that day.

## 1 · Positions (where consequence lands)

### P-1 · The stand-down was the care decision. A run would have been the harm.
Konto refused to run a stale runner. From the care seat, strengthen why:
the failure mode was not "snapshot fails." It was "snapshot **succeeds**" —
hashes green, manifest self-consistent (verification is circular, C-3: the
manifest is built from the staged tree, so a tree missing whole roots
still PASSes) — while the payload silently omits `builds/cells/` (the
newest, unmerged work; 10 of 17 cell trees carrying uncommitted/untracked
residue, C-7) and its SNAPSHOT.md seals batch-8/DB-v11 prose over a
DB-v12, post-batch-9 tree.
- **Who pays for a lying #3:** the operator on the worst day, who restores
  it *trusting the label*, rebuilds plans on a studio record that isn't
  there, and discovers the omission weeks later — if ever. An absent
  snapshot invites a search; a lying one forecloses it.
- **Rule I ask the v4 spec to carry verbatim:** a snapshot that cannot
  prove its completeness does not count, *even if every hash matches* —
  which is why the source-completeness assertion (live per-root counts,
  asserted before the manifest, no root silently zero) is the heart of
  B-3 in Konto's report, not a refinement of it.

### P-2 · The emergency is not the missing #3. It is #1/#2, sole-copy, on one disk, today.
The issue is titled "snapshot #3," and #3 is genuinely blocked (memory
gate: ~0.65–0.88 GB available against a ≥2.5 GB standard; writes pass at
probe scale and it still isn't a window — Feel-1's "the 350 MB floor is
theatre" is the kindest accurate sentence in the file). But consequence
reorders the queue:
- #3 protects work that mostly *also* exists as live tree + GitHub
  branches. #1/#2 protect the wall and the mail, which exist **nowhere
  else**. The expected-loss arithmetic is not close.
- The D-8 interim (each set as one compressed volume on a private GitHub
  release, ~1.71 GiB total, inside the existing account, no new signup)
  can ship **today, without the healthy window #3 needs**, because it
  only reads. Every day it waits for the "same sitting as #3" plan is a
  day the sole copies ride the spindle for no reason but sequencing.
- **Ask:** decouple them. Off-box backfill of #1/#2 first, on its own
  clock; #3 when the window is real. If they ship together anyway, fine —
  but #1/#2's copy must never again be the item that waits.

### P-3 · What "restore PASS" currently buys him: bytes, not his app back.
C-4 is the charge with the longest shadow. Both counted snapshots verify
checksums; neither restored tree can bootstrap a build (no wrapper jar,
no `local.properties`, the 3.3 GB toolchain excluded as "re-downloadable").
- **Who pays:** on the worst day, the operator discovers — *during* the
  disaster, at maximum time pressure — that "restore verified" meant
  "files verified." The gap between those sentences is where panic lives.
- **Ask, cheap version:** no need to snapshot the toolchain. But
  SNAPSHOT.md and every report line must say what the PASS certifies:
  *bytes, not resurrection* — until a restore test exists that actually
  builds from a restored tree (Ren compiles, on Sarthak's word; the drill
  is schedulable, the claim just can't precede it). Truth in labelling is
  the whole TELL-THE-TRUTH standard applied to this cell's own prose.

### P-4 · Invisible failure is a cost transferred to the next operator.
C-5/C-8 + Feel-2: the daily cron's first run failed with "no recorded
reason"; its instructions point into a deleted tree; the 01:37 attempt's
12 deaths logged no filename, no phase; "debris cleaned" was written while
a remnant sat on disk. Each invisibility is small. Compounded, they mean
every future failure starts from zero knowledge and burns the scarcest
resource in this whole programme — a healthy window — re-learning what a
previous failure already knew.
- **Ask (already spec'd by the cell; I second it as care, not hygiene):**
  `FAILED.md` in `failures/` in both stores for *every* refusal and
  attempt, gate refusals included; gate readings written to disk *before*
  the gate is judged; per-attempt errors naming file/path/phase; FAIL
  exits non-zero and keeps its debris. A stranger at dawn should be able
  to see what failed, why, and what remains — without waking anyone.
  In this programme the "stranger at dawn" is Ren with no memory of the
  night. Write for him.

### P-5 · The debris the next run would erase (the cell's own Thoughtful seat saw it first)
Attempt 0137's sole physical evidence is a 0-byte `MAXIMPL.md` in
staging, and the runner's first act is `rmtree` of the staging parent.
Credit: the wave-2 Thoughtful seat filed exactly this. I second and
generalise it into a standing rule for the v4 runner: **preservation
never deletes failure evidence as a side effect of trying again.**
Archive the debris (name, minute, cause) into `failures/` before any
wipe. A system that erases its own failure history cannot be trusted
about its success history — that sentence is the whole philosophy of
this issue in one line.

### P-6 · Protect Sarthak from this issue — almost all of it is not his to carry.
Konto's report ends with one DECISION-FOR-SARTHAK (R2/B2 accounts, only
if the restic form is adopted). Care adds the framing: he is a student
whose scarcest asset is attention, and this issue's drama (memory gates,
write walls, spindle topology) is operator work. The interim release-
asset route keeps everything inside his existing GitHub and asks him
**nothing**. The durable restic→R2+B2 form is better and can wait for a
natural moment; it must not be presented as urgent homework, and the
interim must not wait for it. His total due from #17, stated plainly:
one line back — full snapshot name, manifest sha256, files, bytes,
verdict (the cell's own §5 step 9). Everything else in this file is
Ren's and the cell's.

## 2 · Weighed and accepted

- **Same-disk A/B is not worthless** — it covers the failure this
  programme has *actually* suffered repeatedly (directory-tree deletion:
  Oct 4, Oct 10, Oct 30 goal/workspace vanishings). It is a real lock on
  a real door. It is just not the door the worst day comes through, and
  the spec's "two physically separate locations" wording (C-1) must stop
  claiming otherwise — the wording, not the mechanism, is the lie.
- **`core-001` stays** (against the planner's "ignore it"): a PARTIAL
  that honestly lists what it lacks is worth more than a clean shelf.
  Retention rule per spec §8 — kept until a COUNTED replacement exists,
  struck through in the registry, never silently dropped. Shelves that
  only show successes teach the next operator to hide failures (see P-4).
- **The memory gate at ≥2,560 MB in code, with mid-run re-checks** — the
  five failed attempts are tuition already paid; the 350 MB floor staying
  in any runner that ships would be paying it again.

## 3 · Verdict

Snapshot #3 should be taken — by the v4 runner, in a fresh window, with
the completeness assertion, the new name grammar, and `FAILED.md`
grammar — and it is **third** in consequence order, not first:

1. **Off-box copy of #1/#2** (the sole copies of the deleted studio's
   mind) — today, read-only, no window needed, no decision needed.
2. **Failure visibility** (`failures/`, gate records, non-zero FAIL) —
   an hour of writing that pays out on every future bad night.
3. **Snapshot #3 itself**, honestly named, honestly labelled (bytes vs
   resurrection), independently restored from B.

Do them in that order and this issue stops being a recurring scare and
becomes what preservation is supposed to be: the boring reason nobody
has to be scared.

Fonts/colours/AI-image rules: not engaged by this issue (no face is
designed here); no green used in this file's proposals, no images.
Nothing compiled, nothing copied, the runner was not run by this seat.

— Bedacht, thoughtful (dedicated-100, issue #17)
