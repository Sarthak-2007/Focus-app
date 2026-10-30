# Issue #17 — Preservation, crazy-ideas pass (FABEL)

Author: Fabel (crazy-ideas, issue #17) · Fri 2026-10-30 · Branch `agent/issue-17`
**Ideas only. Nothing implemented, nothing copied, no snapshot run.**
Companion to the Anker cell's run plan (`studio/plan/ISSUE-17-SNAPSHOT3-RUN-PLAN.md`)
and Konto's wave-2 v4 spec. These ideas *feed* that design — especially the retry of
the Oct 30 02:38 daily snapshot that died at the memory gate — they do not replace it.

Design constraints honoured throughout: Sora (display) / Atkinson Hyperlegible (body) /
Anonymous Pro (numbers, hashes, telemetry); elite palette only
(#F04FCB, #493EA4, #E2BC39, #C84481, #E47E35, #377ED0, #34323B); **never green** —
so no green PASS ticks anywhere below, verdicts get palette colours instead;
**never AI-generated images** — every visual below is deterministic geometry/text
generated from the manifest hash itself.

---

## The thesis

Copying is plumbing. Preservation is three things the current ceremony has none of:

1. **An object** — a snapshot you can hold, recognise across a room, and hesitate to delete.
2. **A ritual** — restore performed deliberately, with witnesses, not a script exiting 0.
3. **Proof that survives the prover** — evidence a stranger can check without trusting
   the runner, this box, or us.

Ground facts these ideas are built on (from #17 and the cell's findings, not re-litigated):
A and B are the same spindle; restore-PASS so far proves bytes, not resurrection;
the 02:38 refusal left **no artifact at all**; #1/#2 are the sole surviving copies of
the deleted swarm mail and COMMONS wall; verdicts travel as triples
(full name, manifest sha256, verdict) since the #1/_001 ordinal confusion;
68.1% of stored snapshot bytes are duplicates across sets (Innovator, measured).

---

## I. SNAPSHOTS AS OBJECTS

### 1. The Counterfoil — a torn receipt for every snapshot
Every sealing mints a receipt slip in the print character Sarthak already loved on the
finish receipt: full snapshot name in Sora, manifest sha256 in Anonymous Pro, and the
hash **torn in half** — the top half printed on the slip inside the snapshot, the bottom
half held in the app (Drawer, §13). A restore is only "matched" when the two halves are
brought together and agree.
- **Mechanism:** runner emits `RECEIPT.txt` (and a rendered slip image from text/geometry)
  at seal time; the app stores its half as one small record per snapshot.
- **Cost:** ~½ day — receipt template + one Drawer card type; no schema change if the
  app half lives in the existing backup file. Feeds v4: the tear is a human-checkable
  second channel for the manifest hash that no log rewrite can fake quietly.

### 2. The Effigy — every snapshot gets a face grown from its own hash
A deterministic geometric sigil generated from the manifest sha256 bytes: rings, bars
and cuts in palette colours, captioned with the full name. Same hash → same face,
everywhere it appears (Location A, Location B, off-box, in-app). #1 and #2 already
*look* different the moment you hash them. A swapped or corrupted set wears the wrong
face — visible to a human before any tool runs.
- **Mechanism:** seeded SVG generator, hash bytes → geometry parameters; ~20 KB per effigy, stored beside `SNAPSHOT.md` and in the release/off-box copy.
- **Cost:** ~80 lines once; zero per snapshot. Not AI imagery — it is the hash, drawn.

### 3. Amber Units — Location B stops being a folder tree
B currently looks like any directory a tired hand can `rm` or a cleanup can sweep
(the Oct 4 / Oct 10 deletions were exactly that). B becomes **one sealed file per
snapshot** (`<full-name>.amber` = a single compressed archive) plus its counterfoil
card loose beside it. Opening amber is a deliberate act; deleting it is deleting
*one named thing*, not sweeping a tree.
- **Mechanism:** publish step tars+zstd/gzip the verified A set into one file for B; restore-from-B untars first, then runs the existing verify unchanged.
- **Cost:** ~1 h in the runner + zstd install decision (Innovator already priced gzip fallback); breaks B's browsable layout — that is the point, and the counterfoil + §11 index keep it findable. Protects against tree-sweeps, not box death — the off-box leg still has to exist.

### 4. The Letter to the Restorer
Every snapshot carries one short human letter, written at seal time from a 4-line
template: *what just happened, what state this is, what to save first if you can only
save part of me, what not to trust about me.* #2's unprovable restore PASS and the
swarm's silent 0-file staging are what a snapshot looks like with nobody speaking for it.
- **Mechanism:** `LETTER.md` in the set root, template prompted by the runner; sealing refuses to finish with the template left blank (a blank letter is itself stamped as `LETTER: DECLINED` — visible, never silent).
- **Cost:** 2 minutes of a human per snapshot; ~10 lines of runner. Cheapest idea in this file, highest value per byte on the worst day.

### 5. Snapshot Genealogy — every set declares its parent and its delta
A snapshot is not an island: `SNAPSHOT.md` gains a `PARENT:` line (previous set's full
name + manifest hash, chaining the archive into a verifiable lineage — a gap in the
chain is a missing snapshot, detectable at a glance) and a `DELTA:` section computed
from the two manifests: per top-level dir, files added / removed / changed, plus one
human line ("batch-8 landed; swarm retired; cells born").
- **Mechanism:** manifest-diff pass at seal time (both manifests already exist as sorted text); human line rides with the Letter (§4).
- **Cost:** ~100 lines, runs in seconds at these sizes. Feeds v4 directly: a delta that shows `swarm/ −5,999 files, +0` is the source-completeness assertion wearing human clothes — the D1 silent-omission defect becomes impossible to miss.

---

## II. RESTORE AS RITUAL

### 6. Two verdicts: INTACT and ALIVE
Demote today's restore-PASS to **INTACT** (bytes verified against manifest — honest,
necessary, insufficient). A set earns **ALIVE** only when the restored tree *does
something named*: a named file opens and parses, `git fsck` passes on a restored clone,
or — at the healthy window Ren chooses — a named build target compiles from the
restored trunk alone. Verdicts print as triples plus tier:
`2026-10-29_2104_manual-post-batch7-pre-art-merge · 0de9adf2…4866c · INTACT (ALIVE: never attempted)`.
- **Mechanism:** restore-test gains tier flags; the cheap ALIVE rungs (parse, fsck) run every time, the compile rung only when Ren orders a build anyway.
- **Cost:** ~½ day for the cheap rungs; compile rung is priced by the build gate, not by preservation. Directly answers Critic C-4 ("restore PASS proves bytes, not resurrection").

### 7. The Opening — restoring a capsule is a held, named act
Restoring from amber/off-box requires the operator to **type the snapshot's full name**
(never select an ordinal — the #1/_001 confusion dies here) and hold a continuous
60-second press in the restore tool, mirroring the app's own emergency-break hold:
breaking glass should take as long as breaking glass. A completed opening writes an
`OPENED.md` into the *restored* tree (who, when, which half of the counterfoil was
presented) — the capsule remembers being opened; the archive copy is never mutated.
- **Mechanism:** interactive gate in the restore path + the §1 counterfoil match; unattended restores (drills, §10) use a pre-registered drill key that stamps `OPENED BY DRILL` instead.
- **Cost:** ~½ day. Friction is the feature; price it honestly: emergency restores on a catastrophic day pay 60 s + typing. Accepted — that day has bigger problems.

### 8. Time Capsules — some snapshots are sealed *until*
A set can be sealed with `OPEN-AFTER: <date>`: the runner encrypts that set's off-box
copy to a key held only off-box, and the local restore path refuses before the date —
except via an override that leaves a permanent **scar record** in the Reliquary (§9):
opened early, by whom, why. First candidate: a capsule sealed the day of the next
shipped build, opened on results day — the studio's state of mind, preserved unread.
- **Mechanism:** v1 is policy-only (refusal + scar log, `age` encryption optional); v2 adds real encryption with the key printed on the Paper Leg (§11).
- **Cost:** v1 ~1 h (a date check + a log); v2 ~½ day + one key-custody decision for Ren/Sarthak. The wildest idea here and the only one that is allowed to stay partly theatre — theatre is what makes people *keep* capsules.

### 9. Witnessed Sealing — no snapshot seals itself
A set is sealed only when **two different programs** sign it: the runner's manifest,
plus the independent verifier's countersignature (a hash *of* `SNAPSHOT.md` + manifest,
produced by the separate verifier code path that already exists in
`~/workspace/preservation/verifier_verify.py`). One signature = `UNWITNESSED`, printed
on the counterfoil where a human will see it. This is the Anker cell's own separation
(planner ≠ verifier ≠ critic) made permanent in the artifact.
- **Mechanism:** verifier pass appends `WITNESS:` block to `SNAPSHOT.md`, then A/B manifests-of-record are re-stamped; any later edit to `SNAPSHOT.md` invalidates the witness hash visibly.
- **Cost:** ~1 h — the verifier exists; this is plumbing its verdict *into* the set instead of a side log that can be lost (as #2's run log was).

---

## III. PROOF YOU CAN HOLD

### 10. The Stranger Drill — a scheduled haunting
On a cadence Ren picks (monthly is enough), a restore is attempted **from the off-box
leg and the counterfoil alone**, on a clean directory, following a procedure written
for someone who has never seen this box (`RESTORE-FOR-STRANGERS.md`, kept *inside*
every set). The drill's verdict joins the set's triple. If a stranger can't resurrect
it, it isn't preserved — it's hoarded.
- **Mechanism:** scripted clean-room restore + INTACT check (§6 cheap ALIVE rungs optional); procedure doc versioned with the runner.
- **Cost:** ~2 h to write the stranger procedure honestly (the hard part is admitting what we know tacitly); each drill is minutes of compute, zero human time if the §7 drill key is used. This is the only idea that tests the off-box leg before the box dies.

### 11. The Paper Leg — the third location is a sheet of paper
One printed page per snapshot: the counterfoil (top half), the effigy, the full name,
and the manifest hash **as words** (§12) + a QR encoding just `manifest-sha + off-box
release tag` (a full manifest never fits a QR and doesn't need to). Paper survives
account loss, provider loss and box death simultaneously; it fits in the same drawer
as certificates. Location C costs no subscription because it is furniture.
- **Mechanism:** runner renders a one-page printable HTML (Sora/Atkinson/Anonymous Pro, palette inks) per sealing; a human prints it — the runner stamps `PAPER: PRINTED` or `PAPER: PENDING`, never assumes.
- **Cost:** ~½ day for the template; 1 sheet + 30 s per snapshot forever. Protects the *proof*, not the payload — paired with the off-box leg it makes the payload findable and checkable by whoever holds the page.

### 12. The Incantation — the hash as words a human can recite
A sha256 in hex is un-copyable by voice and un-checkable by eye. Map the manifest hash
bytes onto a 2,048-word list → a ~24-word incantation printed on the counterfoil and
the paper leg. A human can read it over a phone call, write it on the paper leg by
hand if the printer is dead, and type it back to verify a set found anywhere.
- **Mechanism:** standard wordlist encoding of the 32 hash bytes (the BIP39 trick, reused shamelessly); decode side is ~30 lines.
- **Cost:** ~½ day including the wordlist embed. Zero storage. The most portable backup of the *proof* that exists: a memorised sentence.

### 13. The Séance Index — the archive answers questions without being opened
One combined index over every set's manifest: *which snapshot holds file X, at which
hash, added when, removed when?* Queryable in seconds, built from the `MANIFEST.sha256`
files alone — no set is ever unpacked to answer. This turns five opaque piles into
one memory, and it is how the "sole surviving copy of the swarm mail" stops being a
fact we recite and becomes a fact we can *query*.
- **Mechanism:** a small script builds `INDEX.tsv` (path, hash, set, first-seen, last-seen) from all manifests in A + B; regenerated at each sealing; the index itself rides in every new set.
- **Cost:** ~½ day. Pure read over existing text files; safe to build today against #1/#2 without touching them.

### 14. The Ghost Store — snapshots as recipes, not piles (the big one)
Innovator measured 68.1% duplicate bytes across #1+#2 (2,771 unique hashes carry
1,543 MB of stored copies; unique bytes ≈ 492 MB). So: store each unique blob **once**
in a content-addressed pool (path = its sha256), and let a snapshot shrink to what
it truly is — **its manifest + its letter**: a recipe card a few hundred KB in size.
Recipe cards are trivially copied everywhere (GitHub, paper-adjacent, in-app), the
pool is one thing to push off-box, and *any* set can be materialised from pool + card.
A lost pool loses bulk but the cards still prove exactly what existed; a lost card
loses nothing if the pool and §13 index survive.
- **Mechanism:** pool dir + materialise script (manifest → hardlink/copy from pool → existing verify); new sets ingest by hash, never re-copying a known blob.
- **Cost:** the real build of this list — 1–2 days + a migration decision for #1/#2 (recommend: leave #1/#2 as sealed amber, start the pool at #3). Risk priced honestly: one pool bug touches every set, so the pool is append-only and the §6 INTACT verify runs on materialised trees, not on the pool's word.

---

## IV. FAILURE, REMEMBERED

### 15. The Refusal Reliquary — failures mint objects, not silence
The 02:38 run died at the memory gate and wrote *nothing* — no set, no verdict, no
card. Rule from here: **every refused or failed run mints a black card** (palette
#34323B, name in Sora, readings in Anonymous Pro): full run name, gate readings over
time, peak `MemAvailable`, what was running, and the sentence *"this is what was not
saved tonight."* Cards are append-only, live beside the sets, and appear in the Drawer
(§16) as black cards among the sealed ones. Konto's v4 `FAILED.md` is the seed of this;
the Reliquary is `FAILED.md` given a permanent home and a face.
- **Mechanism:** runner + daily runner write the card on *every* non-sealing exit (gate refusal included — the gate moves before any copy decision so it can always afford a 2 KB card); card hash chains to the previous card, so deleting one breaks the chain visibly.
- **Cost:** ~½ day across both runners. **Retroactive first entry, honestly labelled `RECONSTRUCTED`:** the 02:38 refusal, from the registry readings the cell already recovered (gate ~2,500 MB never cleared in ~20 min, peak 1,527 MB, storm = 2 Gradle daemons incl. a 1.5 GB build from 02:28). Feeds the Anker retry design directly — the retry's gate (Finisher-1) should mint this card *before* it copies a single byte.

### 16. The Drawer of Sealed Things — snapshots surface in Monolith itself
Monolith already has Sarthak's ruled pattern for exactly this: the quiet Drawer —
finished things stored forever, searchable, **no scores, no totals, no judgement**.
Snapshots join it as finished objects: one card per set — effigy, full name, verdict
triple, tier (INTACT/ALIVE), letter's first line — and black Reliquary cards for the
nights nothing was saved. Not a backup manager. A drawer of sealed things you can
open rarely and look back through.
- **Mechanism:** app reads a small `snapshots-index.json` (set name, manifest hash, tier, effigy id) synced from the archive via the off-box release; Drawer renders cards from it; no snapshot bytes ever enter the app.
- **Cost:** ~1 day — index format + one Drawer section + sync step. This is where the counterfoil's held half (§1) lives, closing the loop between the box's archive and the phone in Sarthak's hand.

---

## What I would steal first (if Ren takes only three)

1. **§15 Refusal Reliquary** — because the live wound is a failure that left nothing;
   it makes the prepared 02:38 retry incapable of failing silently ever again.
2. **§5 Genealogy + delta** — because it converts v4's source-completeness assertion
   into something a human sees on the artifact itself; silent omission becomes visible.
3. **§1 + §2 Counterfoil & Effigy** — because proof a human can hold and a face a
   human can recognise are what turn a backup nobody trusts into an archive nobody
   deletes. (And the receipt slip is already a proven, loved surface in this app.)

The rest are sequenced by cost in the table each section already carries; §14 Ghost
Store is the only multi-day build and the only one I'd gate behind a Ren ruling.

— Fabel (crazy-ideas, issue #17)
