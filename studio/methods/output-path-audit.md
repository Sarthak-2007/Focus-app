# OUTPUT-PATH AUDIT — lane → merge → ship, traced end to end

*Durchlauf, Methods (artifact flow), Sweep 2 / WORK-QUEUE item 9.
Filed Oct 29, 2026, ~10:25 IST. Baseline for the Keepers of Record —
this audit is a snapshot, not a registry. Method: disk only. Every
claim below names the file that proves it. "Shipped" is used ONLY in
Hale's adopted sense (WASTE-LEDGER W-11): in Sarthak's hands, proven
by a delivery record. A BUILD-CHANGES "SHIPPED" label is not one.*

*Clock note: file mtimes on this VM print UTC; BUILD-CHANGES and
COMMONS print IST (UTC+5:30). Times below are IST as logged.*

---

## 1. THE HEADLINE — the stranded list

Built, real, and going nowhere on the record as of 10:25 IST:

| # | Stranded artifact | State | Evidence |
|---|---|---|---|
| S-1 | **routines rebuild** (`builds/routines/`) | Built, assemble GREEN, **unmerged, tests FAILED, unannounced** | Lane's own letter-draft.md is titled "built, unmerged"; `full-test.log` + `derive-test.log` both BUILD FAILED; `RoutineDerive.kt` + `RoutineHeartbeat.kt` exist ONLY in the lane (diff vs trunk, 28 files differ) |
| S-2 | **drawer build** (`builds/drawer/`) | Built (lane APK exists), **unmerged, no delivery line anywhere** | `DrawerScreen.kt`, `DrawerDay.kt`, `DayWordStore.kt`, `WallAccrual.kt` + 5 drawer tests exist ONLY in the lane (22 files differ vs trunk); 17 renders in `shots-own/` filed nowhere else |
| S-3 | **slide / Slice D** (`builds/slide/`) | **Not even built**: both assembles FAILED, no APK in the lane, unmerged | `build-slide-1.log` BUILD FAILED; `proof-assemble.log` BUILD FAILED in 6m 2s; zero `.apk` under the lane; work survives as `letter-draft.md` + three `scratch-*.kt` files at lane root |
| S-4 | **combined build 1** (trunk itself) | Merged (09:12) but **final build FAILED 10:21 IST — no current installable trunk artifact exists** | `build-batch1-final.log`: BUILD FAILED (`dexBuilderDebug`, 10m 16s); `test-batch1-final.log` is **0 bytes**; newest trunk APK (`Focus-app/app/build/outputs/apk/debug/app-debug.apk`) predates the AOD span fix and the 10:05 fix list; no `monolith-batch1*.apk` was ever cut at root |
| S-5 | **10 root APKs with no BUILD-CHANGES entry at all** | Exist on the VM; never merged-into-record, never delivered on record | §3 table: clean-debug, final-debug, iteration, shield, tasks, round0, round1a, round1c, banked (+ debug is recorded as a build result, never as shipped) |
| S-6 | **3 "SHIPPED"-labelled APKs with no delivery line** | Labelled shipped, never reached him on any record | beyond-a, beyond-b (B1), beyond-c — §3 |
| S-7 | **shieldpick knob variant** | Stranded **by ruling** — the one healthy stranding on this list | BUILD-CHANGES 10:17: "NOT merged; it stays in builds/shieldpick as a record." Named, ruled, kept. This is what every stranding should look like |

Counterweight, for honesty: token and counterfoil slice 1 DID merge
(BUILD-CHANGES 09:12–09:52 entry, hand-merge by Ren; trunk carries
`EmergencyBreakLedgerTest.kt`, `TokenMigrationTest.kt` untracked),
and shieldpick's renders DID ship (he tapped the cap, 10:17).

---

## 2. LANE TRACE — every lane under `builds/`

Lane-vs-trunk deltas measured by direct tree diff of `app/src`
(`diff -rq` trunk vs lane), not git — every tree carries the same
huge uncommitted revamp against the original commit, so git
diffstats measure the revamp, not the lane. That fact is itself
Leak L-5.

| Lane | What it is | Lane artifact | Merged? (evidence) | Shipped? | Verdict |
|---|---|---|---|---|---|
| **token** | Emergency-break ledger: append-only spend rows, token derived not counted, Migration 7→8 verbatim, destructive fallback deleted (TOKEN-SPEC) | Lane APK `app-debug.apk` sha `d1a10044…`, 18,167,673 B (in-lane only, never copied to root) | **YES** — BUILD-CHANGES 09:12 merge entry; `merge-review/` holds 8 token-vs-trunk diffs (699 lines); trunk has the token test files | Only as part of combined build 1 — which never shipped (S-4) | Merged, unshipped |
| **counterfoil** | Linea's Counterfoil slice 1: tear-off task stub; tear pulse sole sound owner at completion | Lane APK sha `c0770b09…`, 18,184,057 B; 10 renders in `shots-own/`; scratch build/test logs (test1 SUCCESSFUL 11m13s, test2 FAILED then SUCCESSFUL 51s) | **YES (slice 1)** — same 09:12 BUILD-CHANGES entry ("counterfoil slice 1 … VM seal sound removed"). BUT no counterfoil diff exists in `merge-review/` — the merge left no diff artifact; 20 files still differ lane-vs-trunk (lane base predates token merge + Live Wire; most of that delta is base drift, indistinguishable from unmerged work) | No | Merged on a sentence, not a diff |
| **routines** | Routines rebuild: Migration 8→9 (15 ADD COLUMNs), pure `derive()`, heartbeat funnel, chain alarm, reseed (Voss lane) | Lane APK sha `14a84806…`, 18,216,889 B; 5+ renders + night set in `shots-own/`; closing letter still a **draft** (`letter-draft.md`, never sent) | **NO** — letter-draft says so; `RoutineDerive.kt`/`RoutineHeartbeat.kt` lane-only. TRAP: trunk has files NAMED `RoutineScheduler.kt`/`RoutineAlarmReceiver.kt` (older Oct-28 scaffolding) that DIFFER from the lane's — same names, different code | No | **STRANDED (S-1)**; tests red in-lane |
| **drawer** | The Drawer (kept Ledger tab rebuilt): permanent finished-task store, month index, frozen HELD digest | Lane APK sha `0960741a…`, 18,216,825 B; 17 renders day+night in `shots-own/` | **NO** — `DrawerScreen.kt` + 3 data files + 5 tests lane-only (22 files differ) | No — no BUILD-CHANGES entry mentions a drawer delivery; the lane has no letter file at all | **STRANDED (S-2)**, and silent: no letter, no log line |
| **slide** | Slice D steps 2+3 (Marlowe/Wild): Live Wire slide, 0.84 catch, OFF-stop, strip | **No APK.** Two failed assembles; three loose `scratch-*.kt` (15–30 KB) at lane root; 14 KB `letter-draft.md` | **NO** — `SliceDGestureAndStripTest.kt` lane-only; slide hunks lane-only (21 files differ) | No | **STRANDED (S-3)** — the centre gesture of the app exists only as draft + scratch |
| **shieldpick** | Linea: rotary knob vs sliding cap selector, both REAL working selectors (WORK-QUEUE item 4, renders-only) | No APK (by design — renders lane); 16 captures in `shots-own/` (8 variants × png+jpg); test3 SUCCESSFUL after 2 failures | Knob: **NO, by ruling** (kept as record). Cap: **not yet** — Linea re-seated 10:17 to build the 84dp merge-ready version; merges "with the chrome train after combined build 1" | **YES — the renders shipped**: BUILD-CHANGES 10:20 "RENDERS DELIVERED" + 10:17 records Sarthak's tap picking the cap. The only lane whose output reached him today | Shipped (renders); cap merge pending behind S-4 |

## 3. ROOT APK TRACE — every `monolith-*.apk` at `clone-inspect/`

17 files. "Record" = a BUILD-CHANGES entry naming the file.
"Delivered" = delivery evidence per PLAN-AUDIT PA-S1-19 (Mutter),
the only delivery audit on disk.

| APK | sha (16) | BUILD-CHANGES record | Delivered? | Verdict |
|---|---|---|---|---|
| monolith-debug.apk | 3e614abb | Yes — build result, zero source changes (Oct 28) | No | Baseline artifact, recorded, never claimed shipped |
| monolith-clean-debug.apk | df47555d | **None** | No | Unrecorded |
| monolith-final-debug.apk | c33c8107 | **None** | No (on this record) | Unrecorded — and see next row |
| monolith-shield.apk | **c33c8107** | **None** | No | **Byte-identical to final-debug.** One artifact, two names, zero records |
| monolith-iteration.apk | 0ff96644 | **None** | No | Unrecorded; same size as final-debug, different bytes — indistinguishable by eye |
| monolith-tasks.apk | 963218fd | **None** | No | Unrecorded |
| monolith-round0.apk | f208233a | **None** | No | Unrecorded |
| monolith-round1a.apk | 50413aa0 | **None** | No | Unrecorded |
| monolith-round1c.apk | f6158c8c | **None** | No | Unrecorded |
| monolith-banked.apk | 0a08a4a8 | **None** (`build-banked.log` exists; no entry) | No | Unrecorded |
| monolith-deck.apk | 258e4af2 | Yes — "VERIFIED + SHIPPED", "delivered in clone chat" | Weak: clone chat ≠ his phone; PA-S1-19 does not count it | Label + chat-delivery claim; phone-unverified |
| monolith-beyond-a.apk | e4989c3d | Yes — "SHIPPED" + sha | **No delivery line** | Label-shipped only (S-6) |
| monolith-beyond-b.apk | 50961b7a | Yes — "SHIPPED" (Slice B1) + sha | **No delivery line** | Label-shipped only (S-6) |
| monolith-beyond-c.apk | 44c622a0 | Yes — "SHIPPED" (Slice C, via COMMONS) | **No delivery line** | Label-shipped only (S-6) |
| monolith-beyond-b2.apk | 87de91f4 | Yes — "SHIPPED" (Slice B2) | **YES** — PA-S1-19: reached Sarthak 08:52, phone-unverified | Shipped |
| monolith-truth.apk | 9bb80cf2 | Yes — "SHIPPED" (truth fixes) | **YES** — PA-S1-19: reached Sarthak 07:56, phone-unverified | Shipped |

Score: 17 APKs → 2 proven in his hands, 1 chat-delivered, 3
label-only, 10 with no record at all, 1 baseline. That is W-11
measured, not quoted.

## 4. TRUNK STATE

- Trunk = `clone-inspect/Focus-app`, **entirely uncommitted**: 34
  files changed vs the original commit (+3,195/−3,312) plus
  untracked Live Wire, token tests, fonts. There is no commit, tag,
  or named base after the AI Studio original — "trunk state" can
  only be cited as a timestamp, never a revision. Every lane base
  is therefore unverifiable after the fact (routines' letter-draft
  has to *narrate* its base: "TOKEN tree stamp (DB v8)").
- Merged and in trunk: token, counterfoil slice 1, Live Wire
  substrate, truth fixes, B2 wheel, AOD span fix (09:12–09:52
  BUILD-CHANGES entries; first merged run 24/24 tests GREEN 09:23).
- Not in trunk: routines rebuild, drawer, slide/Slice D, shieldpick
  cap (pending), shieldpick knob (ruled out).
- Current build state: **RED** — see S-4. The last green trunk
  evidence is test-batch1c.log (10:36 IST run) predating the 10:05
  fix list; the final build failed and its test log is empty.

## 5. THE LEAK POINTS — where artifacts fall out of the record

- **L-1 · "SHIPPED" is a label a builder gives himself.** Six
  BUILD-CHANGES entries say SHIPPED; the word is written in the
  same edit that finishes the build, with no delivery field to
  fill. Example: Slice A — "VERIFIED 13/13 + SHIPPED", sha given,
  no recipient, no channel, no time of delivery. (W-11's root.)
- **L-2 · Lane APKs are born anonymous.** Lane builds land at the
  default `app/build/outputs/apk/debug/app-debug.apk` inside the
  lane tree — same filename in all 4 lanes that built one, never
  copied to root, never named, never sha-logged. Token's APK
  (d1a10044…) is discoverable only by hashing it yourself.
- **L-3 · Diffs live only in lane trees — and mostly not even
  there.** `merge-review/` contains diffs for exactly ONE lane
  (token, 8 files). Counterfoil merged with no diff filed.
  Routines/drawer/slide diffs were never extracted; their delta
  exists only as the difference between two living trees, and it
  *rots* as trunk moves (counterfoil's 20-file delta is already
  mostly base drift). A diff not extracted at merge time is a diff
  lost.
- **L-4 · Renders nobody filed.** Drawer: 17 renders in-lane, no
  letter, no wall post, no review. Routines: day+night sets in-lane
  only. Counterfoil filed 10 in-lane. Only shieldpick's renders
  travelled — because its brief made renders the deliverable.
  Renders that aren't the deliverable never move.
- **L-5 · No revision identity anywhere.** Nothing is committed;
  bases are narrated ("as of ~09:15", "TOKEN tree stamp") instead
  of named. Provenance questions ("which base did slide fork
  from?") are unanswerable from disk a day later.
- **L-6 · Letters that never left the lane.** Routines' and slide's
  closing letters sit as `letter-draft.md` INSIDE the lane dir —
  not in any inbox. The record of the work is co-located with the
  work, so when the lane tree is ignored, its own report is ignored
  with it. (Slide's draft at least self-reports honestly; drawer's
  lane has no letter at all.)
- **L-7 · Filename reuse hides identity.** final-debug ≡ shield
  (identical sha); iteration is a same-size different-bytes twin;
  banked/round0/round1a/round1c carry no entry saying what
  distinguishes them. A name is currently a claim nobody checks.
- **L-8 · The registry that should catch all this is empty.**
  `plan/WORK-REGISTRY.md` is **0 bytes** as of this audit. The
  Keepers inherit a charter, a format need, and no rows. §6 of the
  companion file (delivery-record-format.md) is the fill.

## 6. WHAT "GOOD" LOOKED LIKE, once

Shieldpick: brief named the deliverable (renders), lane produced
exactly it, BUILD-CHANGES logged delivery AND the decision AND the
loser's disposition ("stays as a record") in two dated entries.
Every field the format in `methods/delivery-record-format.md`
requires already exists in that one episode — the format just makes
it non-optional for everyone else.

— Durchlauf, Methods · disk-verified Oct 29, 2026
