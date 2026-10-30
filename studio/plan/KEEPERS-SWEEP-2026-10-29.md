# KEEPERS SWEEP — 2026-10-29 (Protokoll, lead, Keepers of Record)

Sarthak's order, Oct 29, 23:16 IST: "Run methods and keepers of
record." This pass runs flat under Ren. It reconciles everything
produced tonight against plan/WORK-REGISTRY.md and
plan/OPEN-LOOPS.md, verified against disk — mail/, plan/,
builds/, COMMONS.md — not against any summary.

**Sweep window:** the prior Protokoll pass closed 23:22 IST
(registry §"Post-batch-8 seats", OPEN-LOOPS L34–L51). This sweep
covers 23:22–23:52 IST, and re-verifies the board the brief named.
**Live-movement warning:** the studio was still moving while this
sweep ran — a merge tree and a new lane were born at 23:37–23:38
and compile flags changed hands at 23:38 and 23:40. Every state
below is stamped to its observation minute; a state that was true
at 23:40 may already have moved. That is recorded, not smoothed.

**Status vocabulary:** RECEIVED → READ → RULED, per charter.
Lane states: LANDED-DECLARED / RUNNING / REFUSED / CLOSED /
QUEUED / UNACCOUNTED.

---

## 1. TRUTH CAR 3 — the merge-readiness count (the exact set)

Ten seats as seated ~22:29 IST, flat. Count at 23:52 IST:

### LANDED-DECLARED — 5 of 5 builder lanes (all uncompiled, by the 23:06 compile-once ruling)

| Seat | Lane tree | Declaration | Changes since the 23:22 pass |
|---|---|---|---|
| Falk — UNA-01 shorts made real | builds/truth-3-shorts/Focus-app | mail/ren/2314__from-falk__una01-lane-declared.md | none; Register spot-check (plan/TRUTH3-SPOT-CHECK.md) confirms 13 paths item-for-item, 7 @Test, DB v12 + MIGRATION_11_12 + shortsReelsSniperEnabled |
| Imke — UNA-03 sound fact + UNA-02 social | builds/truth-3-sound/Focus-app | mail/ren/2315__from-imke__truth3-sound-social-declared.md | Register confirms 8/8 files, 8 @Test, DB v11, no column; one extra path (gradle.properties, comment-only, kit template) copied to her inbox — see U7 |
| Bodo — five-charge sweep UNA-04…09 | builds/truth-3-sweep/Focus-app | mail/ren/2312__from-bodo__truth3-sweep-declared.md | Register confirms exactly 12 paths, 14 @Test, DB v11, no column |
| Suki — UNA2 intercept slice | builds/truth-3-intercept/Focus-app | mail/ren/2322__from-suki__truth3-intercept-lane-declared.md | **was RUNNING at 23:22; LANDED-DECLARED this window.** UNA2-01/02/03 cured (her halves), UNA2-06 in-app half cured; inbox cleared to zero at declaration |
| Tamm — ST-02/ST-03 break verbs | builds/truth-3-breaks/Focus-app | mail/ren/2328__from-tamm__truth3-breaks-declared.md | **was RUNNING at 23:22; LANDED-DECLARED this window.** Carries the second MIGRATION_11_12 (breakTakenSeconds, DB v12); composition per Ren's 23:23 ruling: the shipped 11→12 carries both ALTERs (Falk's + Tamm's) — a HAND composition at merge, not two migrations |

### CLOSED — the 4 verification / cartography / proof seats

- **Una** (verification) — plan/AUDIT-truth3-reverification.md, pass 1 CLOSED; pass 2 armed at merge (OPEN-LOOPS L39).
- **Hale** (wording) — plan/TRUTH3-WORDING.md, 619 lines, 47 W-entries (W-01…W-47, Register-verified); CLOSED; re-verification seat armed at merge (L39).
- **Konto** (merge cartography) — plan/TRUTH3-MERGE-MAP.md edition 1 CLOSED, **and now STALE**: Ed.1 predates Suki's and Tamm's declarations, states truth-3-breaks "does not exist" (Discrepancy X3, still uncorrected in a filed edition), and knows no truth-3-times tree. **Edition 2 is owed before the merge compile is planned against it** — see U11.
- **Pruefer** (proof desk) — plan/TRUTH3-PROOF-STANDARD.md (455 lines, Register-verified) + plan/TRUTH3-PROOF-LOG.md + builds/truth-3-proofbase; CLOSED; merged-tree proof session armed at merge (L39).

### REFUSED — 1 seat, recorded, not dropped

- **Rurik** (times + migration spine, ST-04) — REFUSED ×2 (spawn 22:29; re-seat ~22:39 on Sarthak's word). No declaration, no lane at 23:22. His inbox is a dead inbox holding 8 letters (Una 2237, Falk 2233, Imke 2236, Hale 2240, Pruefer 2248, Tamm 2250, Konto 2302, Suki 2320 — the last arrived during Nachweis's audit). Disposition on record: every item has a living route except the times slice's own brief/standard (waits with the feature) and Hale's W-41/W-43 (Ren's at the merge) — per plan/MAIL-AUDIT-2.md.

### THE TIMES LANE MOVED — new, unbooked at birth

- **builds/truth-3-times/** was created **23:37 IST by Bausatz's
  lane kit** (its own README.md states this). Diff vs trunk at
  23:40: exactly 2 source files differ —
  data/MonolithModels.kt and data/ReceiptState.kt
  (+ gradle.properties, kit template). **No builder is named in
  any letter, no declaration exists, no seat row existed before
  this sweep.** State: BORN / WRITING, owner UNACCOUNTED (U2).
  It is **NOT** in the merge set below until it declares.

### The exact merge set Ren merges from

1. truth-3-sweep (Bodo) — declared, uncompiled
2. truth-3-shorts (Falk) — declared, uncompiled
3. truth-3-sound (Imke) — declared, uncompiled
4. truth-3-intercept (Suki) — declared, uncompiled
5. truth-3-breaks (Tamm) — declared, uncompiled
6. Hand composition, named in advance: the single MIGRATION_11_12
   carrying both ALTERs; Suki × Wecker seam on
   AirlockAndDisarmOverlays.kt + MonolithViewModel.kt if
   flow-clock joins the same compile (Wecker's letter names it).
7. NOT in the set: truth-3-times (undeclared), the pill's
   UNA2-06 face half (Pastille, still RUNNING), Stempel's
   UNA2-03 half (still RUNNING).

**Rehearsal evidence already on disk:** Takt's
tools/rehearsal-merge.sh ran sweep/shorts/sound only
(sweep 19/19 clean · shorts 12/12/2 · sound 11/4/4; six failed
hunks named, methods/rehearsal-merge-first-run.md). The full-car
rehearsal — now possible, all five declared — has **not** run.
Takt's NEXT (a) asks to run it; unruled (U14).

**A merge is already moving, unbooked:** builds/merge-batch9/
appeared 23:38 with patch logs for applist, truth-3-breaks,
truth-3-intercept, truth-3-sound, truth-3-shorts, truth-3-sweep.
Its logs carry REJECTS, by name: MonolithDatabase.kt (breaks —
reversed/previously-applied, 3 hunks), RoutinesMigrationChainTest.kt
(1 hunk), SliceEMigrationChainTest.kt (1 of 2), MonolithAccessibilityService.kt
(intercept 2 of 6; sound 2 of 6), MainActivity.kt (sound 1 of 1),
ParkDrawer.kt (sound 2 of 5), ScreenshotHarnessTest.kt (sound 1 of 1).
No letter, no registry row, no named owner in any book before
this sweep (U1). Presumed Ren's hand merge in progress — the
Keepers record the presumption as a presumption.

---

## 2. APP LIST — Ordner + the five-seat creative panel

- **Panel — all five CLOSED, all five proposals READ by Ordner.**
  The five letters sit in mail/ordner/read/, which is the disk
  proof Ordner read them: Vega (physics, 2257__from-vega__applist-physics-panel.md),
  Muede (tired-at-1-AM walk, 2256), Liv (design spec, 2258),
  Mikro (micro-interactions, 2302), Gust (taste verdict, 2312).
  Their closing letters to Ren stand at mail/ren/2256 (Muede),
  2259 (Vega), 2300 (Liv), 2302 (Mikro), 2316 (Gust).
- **Ordner — RUNNING, no declaration.** Lane builds/applist/Focus-app
  (8 files differ per the 23:22 pass, incl. new data/AppCatalogReconcile.kt
  + data/AppRules.kt). A build-completion.log appeared in the lane
  at 23:40 whose entire visible content is queue.sh errors
  (stat failures on compile-warrant.flag, "integer expression
  expected") — the lane is attempting a completion compile and
  the log does not show it succeeding. No closing letter.
- **The panel-answer obligation is OPEN (U3):** the brief required
  Ordner to answer the panel's proposals BY NAME. No letter from
  Ordner exists in any panelist's inbox (vega / liv / mikro /
  muede / gust all checked: zero unread, and no Ordner letter in
  any of them). Reading is done; answering is not. Owner: Ordner,
  at lane close; Ren rules any proposal Ordner declines.

## 3. LOOK LANES

| Lane | State at 23:52 | Evidence / change since 23:22 |
|---|---|---|
| Noir — Tinte (coordinator) | RUNNING | builds/look-noir/ (LOOK-NOIR.md + REGISTRY.md + crew/ + shots-own). **Crew roster letter to Ren still missing** — X4 stands, re-confirmed by Nachweis (MAIL-AUDIT-2 finding 2). Haut's feel verdict: KEPT |
| Signal — Fahne (coordinator) | RUNNING | builds/look-signal/ (REGISTRY.md + crew/ + shots-own + alba logs). **No LOOK-SIGNAL.md at lane root** — X5 stands for this lane. Haut: KEPT (6 ms @ 255 stamp in code). A compile-look-signal.flag stood earlier in the evening and was gone by the 23:40 flag check — queue state is volatile, see §6 |
| Temple — Stein (coordinator) | RUNNING | builds/look-temple/ (LOOK-TEMPLE.md + CREW-REGISTRY.md + compile-setzer.log). **Feel layer NOT YET WRITTEN** (Haut, plan/RECONCILE-haut-feel.md: knocks still at trunk's 320 Hz vs the promised 160–220 Hz, tablet knocks unwired, no motion character — priced SMALL / SMALL / SMALL–MEDIUM, pre-warning letter in Stein's inbox). Maurer (theme/Deck half) + Richtschnur (Methods, TEMPLE-METHODS.md unfilled) REFUSED, flat re-seats still owed (L41) |
| Blueprint — Riss (coordinator) | RUNNING | builds/look-blueprint/ (BASE-MANIFEST.sha256 + README.md; **no LOOK-BLUEPRINT.md at lane root** — X5 stands). Haut: KEPT, "the strongest feel build in the wave" |
| Dawn — flat builder | RUNNING | builds/look-dawn/ now carries **LOOK-DAWN.md** (new since the 23:22 pass, which recorded none) + proof.log + run-proof.sh + shots/. compile-look-dawn.flag stood at 23:38 (pid 28610). No closing letter. Haut open items: sound warmth DILUTED to an engine comment (detent pitch census identical to trunk) — SMALL pitch pass or strike the claim; two-beat swell repair unwritten; Dawn's §6 glass note vs the register's ST-03 seat knock — one ruling queued to Ren (U16) |
| Obsidian — Kern (coordinator) / flat builder | REFUSED / QUEUED | Coordinator REFUSED; flat spawn REFUSED; builds/look-obsidian does not exist (the builds/obsidian/ tree is the older presentation lane, a different thing). Build seats only after the running wave lands, per Sarthak's 22:24 sequencing (L42). Haut pre-warning letter filed to its inbox before the lane exists |
| Maschine — Werk (coordinator) | REFUSED ×2 / QUEUED | Coordinator refused twice incl. Sarthak's-word retry. No build lane; seats after the running wave + a check for Zunder's approved intensify pass first (L42). Haut pre-warning filed; Maschine's §7 preamble carries Una's three FALSE findings as a charge, not an inheritance (Haut) |
| Fassung (theme builder, 22:07 attempt) | REFUSED | Nothing on disk; recorded at 23:22, unchanged |
| Wagen (Truth Car 3, coordinator form) | REFUSED | Superseded by the flat ten; recorded, not dropped |

## 4. MACHINERY LANES

- **Voss — routine editing + E-14(c) rider: RUNNING** (longest
  lane, seated ~19:04; 26 files differ at 23:22). **Four letters
  from Ren stand UNREAD in mail/voss/** — 2029 (nofire rulings),
  2125 (BUILD-STAND-DOWN), 2130 (RC-03 re-aim), 2133 (formatter
  seam) — the two riders unread ~2 hours into the lane
  (Nachweis, MAIL-AUDIT-2 finding 1; re-verified this sweep).
  The lane builds; by the mailroom's method it has not read the
  letters that re-aim it (U4). No closing letter.
- **Wache — no-fire fix F1–F5 + ST-05/ST-06: RUNNING.**
  builds/nofire/ (13 files differ at 23:22, incl. new
  service/CompletionSignal.kt). compile-nofire.flag appeared
  23:40:19 IST (pid 46654) — the lane is at its compile now.
  Unread in mail/wache/: Bodo 2233 (UNA-05 firing words — the
  words half of L44 sits unread in the lane that must carry
  the firing half) (U6). No closing letter.
- **Pastille — the pill: RUNNING.** builds/pill/ (10 files differ
  at 23:22, incl. PillController.kt / PillFace.kt / PillTheme.kt /
  EditableTimes.kt). Unread in mail/pastille/: Suki 1725
  (UNA2-06 halves — the coordination letter for the charge
  L45 says cannot close inside Car 3 alone) and Frist 2342
  (editable-times access path) (U6). Nachweis relayed Wagen's
  UNA2-06 substance to Suki at 2335 because Pastille's copy had
  not been relayed — the relay model worked; the direct letter
  still stands unread. No closing letter.
- **Stempel — warrant stamp fix: RUNNING.** builds/warrant/
  (3 files differ + WarrantStampResidencyTest.kt at 23:22).
  Unread in mail/stempel/: Suki 1724 (UNA2-03 halves) (U6).
  No closing letter. His lane is also the cure for the phone's
  "CAN'T CHECK BLOCKING" cold-start bug (Sarthak, 22:06/22:12) —
  no landing, no verdict on that bug yet in any book.

## 5. FLOW REGISTER + SMALL THINGS REGISTER — standing, and Flow moved

- **Flow Team — Ablauf, founding CLOSED** (plan/FLOW-REGISTER.md
  + plan/FLOW-TEAM-CHARTER.md, letter mail/ren/2218).
- **The desk-clock auto-raise (OPEN-LOOPS L43: designed,
  unassigned at 23:22) is now ASSIGNED AND LANDED-DECLARED:**
  Wecker, builds/flow-clock/Focus-app — 5 files, new
  ui/DeskClockSettle.kt, declared **uncompiled** at
  mail/ren/2348__from-wecker__flow2-lane-declared.md, 14 tests
  written unrun. L43's close condition ("a named lane lands it")
  is met at lane level; the merge is Ren's. Seams named to Suki
  (mail/suki/2345, unread — arrived after her declaration, U5).
  Wecker's own inbox holds Ren's 2340 "the bar" UNREAD at his
  close (U15).
- **Small Things — Tief, founding CLOSED** (plan/SMALL-THINGS.md,
  plan/SMALL-THINGS-CHARTER.md, plan/SMALL-THINGS-SPECS-PASS.md
  by Klar). Register standing; no new closing tonight.
- **Frist letters, new and unread:** mail/suki/2343 (SIL-2 values
  access path) and mail/pastille/2342 (editable-times access
  path) — both filed ~23:42–23:45, both in running/closed lanes'
  inboxes, both wait for re-seating or lane close (U5/U6).

## 6. METHODS — the paired half of the 23:16 order, now VERIFIED

At 23:22 this run was booked UNVERIFIED ("sibling run, invisible
from this seat"). This sweep verifies it on disk. All of the
following are LANDED, none was in either book before this pass:

- **Vorrat** — methods/memory-budget-page.md (WORK-QUEUE item 8
  delivered; his closure ruling: item 8 CAN be struck). Letter
  mail/ren/2326. Also reports a live breach reading in §4.
- **Satz** — methods/chunked-write-protocol.md (140 lines, filed
  as law). Letter mail/ren/2330. Asks O-7 be struck closed with
  this page as its home.
- **Bausatz** — kit touch executed, 4 items: descriptor counts in
  tools/lane-base.sh, methods/birth-census.md (+ tools/birth-census.sh),
  tools/guh-census.sh first run (36 homes: 32 aligned, 2 empty
  skeletons — truth-3-breaks, truth-3-sweep — 2 hand-born with
  content but no gradle.properties: look-signal, nofire),
  methods/kit-false-closure-sweep.md, methods/lane-kit-metadata-fix.md.
  Letter mail/ren/2334. **It was Bausatz's kit that built the
  truth-3-times lane at 23:37** — the kit touch and the lane
  birth are the same hand, which is the only provenance U2 has.
- **Takt** — methods/merge-pipeline-timing.md (letter 2321:
  car merge priced ≈200 min of Ren's hands, ESTIMATE) and the
  Rehearsal Merge (tools/rehearsal-merge.sh +
  methods/rehearsal-merge-first-run.md, letter 2335).
- **Stellwerk** — item 15 BUILT AS A CANDIDATE:
  methods/queue.sh.v2.1-candidate + methods/queue-claim-fix.md
  (proof, sims in methods/queue-sim/). Live tools/queue.sh
  UNTOUCHED. The 22:47 "breach" is closed as bookkeeping: no
  third compile ever ran; a record-less corpse inflated the
  count. **Swap procedure (§4): at the Car 3 merge window, queue
  drained, atomic rename, smoke test, rollback = the byte-identical
  v2.0 baseline in methods/queue-sim/.** Two rulings asked of Ren
  (U12): (a) LEGACY_STALE 1,200 → 1,800 at the swap;
  (b) the reaped-flag note's sufficiency (no daemon GUH field —
  Wartung was never seated to answer mail/wartung/1028; Nachweis
  relayed the note back to Stellwerk at 2335). OPEN-LOOPS L50
  is updated from "fix approved, seats when lanes close" to
  "candidate built and proven; swap + 2 rulings owed."
- **Marko** — plan/LIMIT-CENSUS-UP.md filed (letter 2336):
  3 SILENT · 0 FALSE · 10 HONEST. SILENT-1 has teeth:
  RoutineHeartbeat.kt:46, DERIVE_LOOKBACK_MILLIS 48 h — a routine
  window unfinalised >48 h falls out of derive() permanently,
  never surfaced. Ruling owed (U13).
- **Queue state at sweep time (volatile, stamped):** flags seen
  standing during this pass — compile-look-dawn.flag (23:38,
  pid 28610), compile-nofire.flag (23:40, pid 46654);
  compile-look-blueprint.flag and compile-look-signal.flag stood
  at the pass's first check and were gone by the second;
  compile-pill.flag and compile-routine-edit.flag (the two
  standing at 23:22, X6) were gone by 23:40. The queue is being
  worked live; no breach is booked from a single snapshot —
  Stellwerk's corpse analysis is the governing account.

## 7. KEEPERS' OWN LATE ARRIVALS (this window)

- **Register** — plan/TRUTH3-SPOT-CHECK.md (letter mail/ren/2322):
  Falk/Bodo/Imke confirmed path-for-path (see §1); the one
  mismatch it found — Tamm's second MIGRATION_11_12 absent from
  Konto's Ed.1 — is now declared by Tamm and ruled for
  composition by Ren (23:23), so the mismatch is a map-edition
  problem (U11), not a lane problem.
- **Nachweis** — plan/MAIL-AUDIT-2.md (letter mail/ren/2342):
  window 10:20–23:35; 351 inboxes · 221 unread · 150 read ·
  297 at zero; the cull verifies EXACT (837/837 deleted paths
  absent; 27 ring letters counted from disk). Three relays sent
  2335: Pastille→Suki substance, Ren's idle-clock ruling → Tinte,
  Wartung note → Stellwerk. Findings 1–3 are U4, U8, L38.
- **Mahner** — mail/ren/2324__from-mahner__open-loops-chase-sweep2.md
  + plan/OPEN-LOOPS-MAHNER-ESCALATIONS.md updated this window.
- **Haut** (Finishers, feel) — plan/RECONCILE-haut-feel.md
  (366 lines) + letters to all seven look inboxes; letter
  mail/ren/2342. Law audit clean: no fraud — SessionSealTransition
  byte-identical in all five built lanes, golden hash standing,
  no per-look seal sound, no ambient loop. Verdicts and open
  items are booked in §3 above.
- **Protokoll (prior pass)** — mail/ren/2324__from-protokoll__night-accounting.md:
  the 23:22 accounting this sweep extends. Its NEXT (a) — the
  Registry-Merge pre-pass seat at the look merge (L51) — remains
  unruled.

---

## 8. THE UNACCOUNTED LIST — the sweep's teeth

Each row: what it is · where it lives · what ruling or action it
needs from Ren. Nothing here is dropped; each row is also booked
in plan/OPEN-LOOPS.md (L52–L66) with an owner.

- **U1 · The batch-9 merge tree exists in no book.**
  builds/merge-batch9/ (born 23:38) + its six patch-*.log files,
  carrying named rejects: MonolithDatabase.kt, RoutinesMigrationChainTest.kt,
  SliceEMigrationChainTest.kt, MonolithAccessibilityService.kt (×2 lanes),
  MainActivity.kt, ParkDrawer.kt, ScreenshotHarnessTest.kt.
  Needs: Ren names the merge owner on the record, and each reject
  gets a disposition (hand-composed / lane rework / ruled out)
  before any compile is claimed against this tree.
- **U2 · truth-3-times lane born with no named builder.**
  builds/truth-3-times/ (kit-built 23:37 by Bausatz's make-lane;
  MonolithModels.kt + ReceiptState.kt differ from trunk).
  Needs: Ren names the seat (a re-seated times builder? a new
  name?) or rules the tree a kit artefact to be struck. Until
  then it is NOT in the Car 3 merge set, and S-3's rewire
  (L35) stays ownerless.
- **U3 · Ordner read the panel, answered nobody.**
  Five proposals in mail/ordner/read/; zero Ordner letters in
  mail/vega/, mail/liv/, mail/mikro/, mail/muede/, mail/gust/;
  no declaration in mail/ren/. Needs: at lane close, Ordner
  answers each of Vega / Liv / Mikro / Muede / Gust by name —
  taken, declined, or escalated — in their inboxes and in his
  declaration. Ren rules whatever Ordner declines.
- **U4 · Voss's riders unread ~2 hours into the longest lane.**
  mail/voss/: 2029, 2125, 2130 (RC-03 re-aim), 2133 (formatter
  seam — E-14(c)'s second half, L47). Needs: Ren knocks, or
  rules the lane's output provisional until the riders are read;
  the merge must not carry E-14(c) as "rider absorbed" while
  2133 stands unread.
- **U5 · Letters that arrived in Suki's inbox after her close.**
  mail/suki/: 2335 (Nachweis relay, Pastille UNA2-06 substance),
  2343 (Frist, SIL-2 values access path), 2345 (Wecker, flow2
  seams). She closed at zero; these are post-close arrivals and
  wait for her re-seating by the charter's hard limit — Faden
  holds the thread. Needs: Ren seats Suki's pass-2 (already
  armed, L39) with these three at the top of her brief.
- **U6 · Coordination halves unread in three running machinery lanes.**
  mail/pastille/: Suki 1725 (UNA2-06 halves) + Frist 2342
  (editable-times). mail/stempel/: Suki 1724 (UNA2-03 halves).
  mail/wache/: Bodo 2233 (UNA-05 firing words).
  Needs: each lane reads before it declares; a declaration filed
  with its coordination letter unread is booked UNREAD, not
  LANDED, by this office.
- **U7 · Register's spot-check note stands unread in Imke's inbox.**
  mail/imke/2321__from-register__spot-check-extra-path.md
  (the gradle.properties extra path). Minor, named, open.
  Needs: Imke reads at re-seating (her pass-2 / proof session).
- **U8 · Tinte's crew roster letter does not exist.**
  Required by Ren's 22:15 crew-roster order; Fahne (2216),
  Riss (2220), Stein (2225) filed theirs; Tinte filed none
  (X4; Nachweis finding 2). Whether Noir's crew seats all
  12 types is UNVERIFIED from the record. Needs: Ren demands
  the letter, or rules the roster order discharged by
  builds/look-noir/REGISTRY.md + crew/ on disk.
- **U9 · Two look lanes have no LOOK-<name>.md manifest.**
  builds/look-signal/ and builds/look-blueprint/ lane roots
  (X5). Both lanes RUNNING, so this is a state, not a loss —
  but the merge must not assume manifests that do not exist.
  Needs: each coordinator files the manifest with its closing
  letter (L40), or Ren waives it by name per lane.
- **U10 · Temple's feel layer is unwritten work, not a delay.**
  plan/RECONCILE-haut-feel.md (Haut): knocks at trunk 320 Hz vs
  promised 160–220 Hz; tablet knocks unwired; no motion
  character; priced SMALL / SMALL / SMALL–MEDIUM. Plus L41's
  two refused seats (Maurer, Richtschnur) still unseated.
  Needs: Ren seats the feel pass + the two flat re-seats, or
  rules Temple ships as-is on the feel axis.
- **U11 · Konto's Merge Map edition 1 is stale at the merge's door.**
  plan/TRUTH3-MERGE-MAP.md: predates Suki (2322) and Tamm (2328)
  declarations, carries the X3 breaks error, knows no times
  tree, no flow-clock seams. Needs: Konto Ed.2 — Ren seats it
  before the one compile, attaching Takt's full-car rehearsal
  census (U14) — or Ren merges from this sweep's §1 set +
  the rehearsal census directly and rules Ed.2 unnecessary.
- **U12 · Stellwerk's candidate waits on two rulings + a swap owner.**
  methods/queue-claim-fix.md §3–§4: (a) LEGACY_STALE 1,200→1,800;
  (b) reaped-note sufficiency without a GUH field; (c) the swap
  itself at the Car 3 merge window (queue drained, atomic
  rename, smoke, rollback on record). Stellwerk asks to be
  seated at the swap. Needs: Ren rules (a) and (b), names the
  swap hand.
- **U13 · Marko's SILENT-1 is a data-eating cap with no ruling.**
  plan/LIMIT-CENSUS-UP.md: RoutineHeartbeat.kt:46, 48 h derive
  lookback — orphaned routine windows are never finalised,
  never surfaced. Marko asks for the cure lane beside Wache's
  F2. Needs: Ren kills it (cure lane) or rules it accepted,
  on the record.
- **U14 · NEXT-section offers (Law 10) filed this window, none ruled.**
  By name: Takt (a) full-car rehearsal run under his hand ·
  Takt (b) machinery-lane rehearsal · Tamm (a) Pruefer proofbase
  run + Una pass 2 on his lane (armed, L39) · Tamm (b) END BREAK
  on the shield notification; desk-clock break rendering; receipt-level
  span assertion · Suki (in her wall note + letter) Pruefer
  countersign at the merged compile · Wecker (a) merged-tree
  DeskClockFlowTest run under his name + L05 desk captures ·
  Wecker (b) §2.3 pickup audit; FL-13 landing check · Stellwerk
  (a) seat at the swap + LEGACY_STALE datum from the first merged
  suite · Stellwerk (b) corpse census; `queue.sh why` sketch ·
  Marko (a) SILENT-1 cure lane · Marko (b) census 3 (test tree +
  harness); post-merge census re-run · Vorrat (a) standing
  pre-merge budget read, seated at the first merge train ·
  Vorrat (b) C-KSP procedure (in his letter) · Satz (a) chunked-write
  citation into the brief template + O-7 struck · Satz (b) measured
  threshold study with Waage; phantom-tail spot sweep · Bausatz
  (a) full-tier exclusion audit (already queued in his name) ·
  Bausatz (b) GUH seeding of look-signal + nofire homes ·
  Register (a) merged-trunk spot-check the hour the compile lands ·
  Register (b) machinery-lane claims-vs-disk rotation · Protokoll
  (prior pass) (a) Registry-Merge pre-pass seat at the look merge ·
  Protokoll (b) spot-check rotation over the seat rows; a standing
  refusal ledger · Nachweis findings 1–3 (booked as U4, U8, L38).
  Needs: Ren rules each — seat, fold into a merge, or decline
  on the record. An unruled NEXT is an open loop, not a courtesy.
- **U15 · Wecker closed with Ren's letter unread.**
  mail/wecker/2340__from-ren__the-bar.md stands unread; his
  lane declared at 2348 regardless. Needs: the letter is read
  at his next seating (the merged-tree test run he asks for),
  or Ren withdraws it.
- **U16 · Dawn's §6 glass note vs the register's ST-03 seat knock.**
  One flip, one voice — Haut recorded the conflict and queued
  it to Ren. Plus Dawn's diluted sound-warmth claim (pitch pass
  or strike). Needs: one ruling, both halves.
- **U17 · Landed lanes with no merge owner, by name.**
  Beyond Car 3 (§1, owner: Ren at the merge, armed): Wecker's
  flow-clock (landed-declared, no merge owner named), Ordner's
  applist (merge owner unnamed while its completion compile is
  already attempting), the five look lanes (merge = the gated
  build, owner Ren, gate = Sarthak's explicit go — standing).
  Needs: Ren names himself or a seat per lane on the record;
  "Ren, presumably" is what this office found for merge-batch9
  (U1) and it is not a booking.

## 9. What did NOT move (verified, so it is not silently lost)

- OPEN-LOOPS L34 (Q6, the slide's sound — Sarthak's tap),
  L36 (release-vs-debug), L37 (git commit), L49 (core-001 relic
  at BOTH snapshot locations, verified present at 23:22 and not
  re-ruled since), L38 (daily snapshot cron — cron.list checked
  at 23:22: no snapshot job; Nachweis confirms still uncreated
  at 23:35). All stand exactly as booked.
- Snapshot 2026-10-29_2104 stands COUNTED (manifest 0de9adf2…,
  restore PASS) — the night's preservation is real.
- Protokoll's own inbox: 0 unread at start (Fundus 1024 already
  in read/, discharged at 23:22), 0 at close of this sweep.

— Protokoll, Keepers of Record (lead) · Oct 29, 2026, ~23:52 IST
Read-only over app code and lane trees throughout; the only
writes are this file, the two books, the letter to Ren, and the
signed wall note. Nothing compiled.
