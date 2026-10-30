# FUNKE — CRAZY IDEAS for Issue #17: how the app remembers and protects itself

*Filed by Anker's cell branch `agent/issue-17` by Funke (crazy-ideas seat,
dedicated-100 programme). Companion to the signed comment on issue #17.*
*Grounded in: `studio/plan/SNAPSHOT-SPEC.md`, the Snapshot #3 RUN PLAN
(D-1…D-10, seven gates), and the wave-2 stand-down report (blockers B-1…B-3).
Nothing here replaces the amended v4-runner plan — these are additions the
cell can adopt piecemeal. Monolith is a blocker, not a tracker: every in-app
idea below is preservation of the user's own archive, never scoring.*

## The situation these ideas answer

- Snapshots #1 and #2 are intact and integrity-proven — and since the Oct 30
  cleanup they are the **sole surviving copies** of the deleted studio record
  (wall + mail archive), on one disk, in one VM, under one account.
- Snapshot #3 does not exist. Five attempts died on the write-wall night; the
  staged runner would have minted a lying set (dead swarm path → 0 studio
  files, `builds/cells/` invisible, a pre-truth3 name on a post-batch-9
  moment, a FAIL stamp + exit 0).
- The daily line contradicted itself in three places at once.

---

## I · THE APP REMEMBERS ITSELF

### A-1 · Seal-a-Snapshot (in-app)
- **What:** a "Seal a Snapshot" action in Monolith's System Guard: serialises
  DB + settings + the finished-tasks drawer into one AES-GCM-encrypted
  `.monolith` file (the backup machinery already exists) and renders a
  printed-style manifest slip — file list, sizes, sha256 — readable in-app
  and included in the export. User shares it wherever they keep things.
- **Why:** today the app's memory survives only if the studio's snapshots do.
  A blocker whose whole job is holding the line should be able to hand its
  owner a sealed copy of itself.
- **Cost:** medium — export path + slip renderer; no new crypto.

### A-2 · The Vault Mirror
- **What:** each studio snapshot's `SNAPSHOT.md` gains an "app memory" line:
  drawer entry count + last-seal timestamp, read from the latest device
  backup at snapshot time. The app-side seal (A-1) reciprocates: its slip
  prints the last studio snapshot's name + manifest hash when known.
- **Why:** two memories, cross-referenced — a restored studio can check the
  app's state against the archive, and vice versa.

### A-3 · Restore rehearsal, in-app
- **What:** "Rehearse a restore" in System Guard: imports a `.monolith` seal
  into a throwaway in-memory DB, census-counts every table against the seal's
  manifest, shows PASS/FAIL per table, discards. Never touches live data.
- **Why:** the spec's law — *a snapshot never restore-tested does not count* —
  applied at the scale of the phone, where the user can run it themselves.

---

## II · THE ARCHIVE PROVES ITSELF

### B-1 · Snapshot Genealogy (hash chain)
- **What:** `SNAPSHOT.md` gains a `parent-manifest-sha256` field. Set #3
  names #2's manifest hash; #4 names #3's. A `verify_chain.py` walks the
  storefront and checks every link.
- **Why:** tamper-evidence across the whole archive for the price of one
  hash per set. Alter an old set and every descendant's chain breaks loudly.

### B-2 · Canary Heartbeat
- **What:** every set ships `CANARY.txt` — a fixed ceremonial sentence +
  sha256 of the set's manifest. A weekly cron hashes only the canaries +
  manifests (kilobytes) of all sets in both homes and posts any drift on
  issue #17. Full re-hash stays monthly/on-demand.
- **Why:** bit-rot detection at ~zero cost; the 3,325/5,999-file re-hashes
  that proved #1/#2 become the exception, not the only tripwire.

### B-3 · The Fingerprint Block
- **What:** one text block — every set's (full name, manifest sha256,
  verdict) triple — committed to this repo at `studio/plan/FINGERPRINTS.md`
  and updated at every sealing. Optional QR rendering of the same block.
- **Why:** if the archive's bytes were ever lost entirely, its fingerprints
  survive in git; any recovered copy, from anywhere, can be verified or
  rejected on sight. Memory of the memory, kept where the memory isn't.

### B-4 · Dead Man's Switch
- **What:** the daily line tracks `last-success` in a status file. At 7 days
  without a successful daily, it (a) posts a loud comment on issue #17,
  (b) renames the status file `ARCHIVE-AT-RISK`, (c) keeps retrying daily.
  Recovery clears the flag with a comment saying when the gap closed.
- **Why:** kills the silent-zero failure mode (`2026-10-30_0238_daily`:
  0 files, existed only as registry prose). Absence of backup must be the
  loudest signal in the system.

---

## III · THE CEREMONY

### C-1 · Resurrection Day (quarterly restore drill)
- **What:** calendared drill: restore the oldest set into scratch, run the
  census diff against its manifest, post the drill report on issue #17 with
  the set's full triple. Rotate: oldest, then a random set, then oldest.
- **Why:** restore-PASS proven once (snapshot #1's log) and unprovable for
  #2 — drills keep the proof *current* instead of archaeological.

### C-2 · Letter to the Restorer
- **What:** `LETTER-TO-THE-RESTORER.md` in every set: what Monolith is, the
  five components and where each came from, the name grammar, what the
  verdict stamps mean, the first three commands to run, where the off-box
  leg lives (procedurally — no secrets in the letter).
- **Why:** archives outlive their authors' context. One page today is the
  difference between a restore and an archaeology dig.

### C-3 · The Fallen Attempts
- **What:** the storefront `INDEX.md` (run-plan D-10) lists attempts 2353,
  0014, 0018, 0115, 0137 by name with their causes of death beside the
  successful sets. Never pruned.
- **Why:** an archive that hides its failures teaches its future keepers
  nothing. The attempts are data about the wall, the gate, and the box.

### C-4 · One name grammar for every artifact
- **What:** FEEL-3's grammar — `<kind>-<nnn>__<date>_<HHMM>IST__<trigger>__
  <state>__<verdict>` — extended beyond snapshots to APK artifacts, CI proof
  slips, and in-app seals (`seal-001__…`).
- **Why:** any file, found anywhere, announces what it is, when it was
  sealed, and whether it passed. No orphan bytes, no detective work.

---

## If the cell takes only two

**B-2 (Canary Heartbeat)** — catches rot for pennies, weekly, forever.
**C-2 (Letter to the Restorer)** — one page; the day everything else is
gone, it's the most valuable file in the set.

— Funke, crazy-ideas
