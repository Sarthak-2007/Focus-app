# BATCH 10 — THE EVERYTHING-MERGE · MERGE PLAN — EDITION 2

**Keeper:** Konto (cell lead, issue #15). **Cell (14 seats, all
reported):** Merge Planner · Innovator · Critic · Verifier ·
GLANZ (§doc) · FASSUNG (§1) · SCHLIFF (§2) · KANTE (§3+§5) ·
REGISTER (§4) · PULS (lifecycle) · KLANG (sound/touch) ·
WORT (printed word) · GANG (motion) · WIDERSTAND (the "no").
**Ground:** branch `agent/issue-15` sits on main @ 8438f5a
(studio-records commit only); the app tree is main @ 817e8c7 —
the batch-9 tree. DB **v12** (MIGRATION_11_12 carries only
`shortsReelsSniperEnabled` + `breakTakenSeconds`).
**What Edition 2 is:** Edition 1 re-verified line-by-line
against the lane trees by fourteen seats. Every material
defect in Edition 1 has ≥2 independent source-checked
confirmations; no seat contradicts another on a material
fact. Where seats supplied drop-in replacement text, this
edition adopts it and says so. **This plan composes; Ren
performs the merge and the single compile, only on
Sarthak's word. Nobody else compiles or merges.**
**Prior cartography:** studio/plan/TRUTH3-MERGE-MAP.md was
the batch-9 car's map (11→12). Its seam ledger still binds
where named below; its §2 migration is history — batch 9's
actual 11→12 took only two of its four listed columns (plus
`breakTakenSeconds`, which §2 never listed), so Frist's
columns arrive HERE.

---

## 0 · LANE CENSUS + THE BASE-WINS LAW (corrected)

**THE BASE-WINS LAW (Verifier, generalized by WORT/KLANG —
the single most important sentence in this edition):** NO
lane is based on batch 9. All seven branched from pre-batch-9
trunk (v11, or their own private v12) and every lane lacks
batch 9's landed work — AppCatalogReconcile.kt, AppRules.kt,
LoadoutStore.kt, SoundEffectsSetting.kt, the Truth-Car-3
tests, WatchStore.readMono, the InterceptPath/sniper changes,
and the wording cures (W-13/W-23/W-33/W-39/W-46, the FIRED AS
SET strike). **Base = the merge-batch9 tree. Every lane
contributes named deltas only, ported hunk-by-hunk onto base
text. No lane file ever overwrites a shared face wholesale** —
every lane branched before those cures, and any wholesale win
resurrects struck sentences. Files in 3+ versions across the
trees: WatchStore.kt (4), MonolithAccessibilityService.kt (3),
MainActivity.kt (6); MonolithViewModel.kt differs in every
lane and is the worst conflict surface.

| Lane | Owner | Verdict (Verifier) | Merge character |
|---|---|---|---|
| truth-3-times | Frist (Rurik) | **PARTIAL** | Admitted on confirmed delta inventory ONLY (§4A1). Private 11→12; columns re-based into 12→13 (§1); provider is a FILE SELECTION (§2). Repository↔Models inseparable (C13). |
| routine-edit | Voss | **PARTIAL** | A v10-era CODE base wearing a v12 Database (Critic C2: Dao paging can't express DRW-04's page+1 law, ReceiptState `plannedSeconds` reverted non-null, Models carries Truth-Car-2-deleted CabinetArtifact fields, tree lacks UndoDeleteStore.kt/RecordCapsTest.kt). Deltas RE-BASED, not transplanted; only `withinLabel` enters 12→13. Dead voiced surface (selectArtifact/tapSerialPlateEasterEgg, VM:2233-2245 + MainActivity:605-606) NEVER transplants. |
| flow-clock | Wecker | **PARTIAL** | Real delta = 8 files (DeskClockSettleMachine 20 s settle + desk-clock print cures + DeskClockFlowTest, 14 tests). V1 ~50 Hz sampling, HELD suppression, Klinge's 19, Wacht A-1…A-12, the M-4 amendment, Landing have NO code in this lane — they live in the issue-13 cell tree (§3 step 7, §4C7). Its Airlock file is stale-base (C14): hunks transplant, file never replaces. Its `deskClockBannerText` duplicates batch 9's — keep ONE copy. |
| nofire | Wache | **CONFIRMED** | F1 exact alarm + USE_EXACT_ALARM · F2 ADOPT · F3 stamps WatchStore · F4 reseed · F5 LAST ROUTINE CHECK · ST-05 CompletionSignal · ST-06 knock · SILENT-1 uncapped DAO read; 6 tests. Caveat on record: its assemble FAILED on the write wall — no passing build log exists. |
| warrant | Stempel | **CONFIRMED as declared** | provePipelineNow (a11y :93/:104), notePipelineProof (:77), WatchStore.kt:187. Assemble PASS in its completion-build.log; test-pass NOT documented. **This fix exists ONLY in this lane.** Lands as written. |
| pill | Pastille | **CONFIRMED** (issue #16) | Ships Route A overlay AND Route B notification incl. ACTION_END_PEEK; exactly 18 tests (PillDerivationTest 15 + PillLeaseRepositoryTest 3). Assemble PASS; its test.log ends in daemon death — no verdict from the log. The merge input is the #16 FLAGS PACKAGE, not the verdict alone (§4F1). |
| onboarding | Empfang | **CONFIRMED as declared** | SetupStore prefs, ReceptionSheet, DONE = "TO THE DECK →" (:501), PARK stub re-opens. 13 tests unrun, no build logs. Lands LAST (§2 truth dependency); blocked by #14's S-1 regardless (§4C8). Its ParkDrawer is stale-base and reprints struck lies (C7) — its SETUP stub-row hunk transplants INTO batch 9's ParkDrawer. |

**The two private 11→12s remain the first trap.** Frist's
lane carries a private MIGRATION_11_12 (his 5 columns);
Voss's carries another (withinLabel only). Neither text may
be copied — only column deltas re-based into the one
MIGRATION_12_13. **Neither lane's MonolithModels.kt may be
overlaid**: both lack v12's `breakTakenSeconds` +
`shortsReelsSniperEnabled` — an overlay silently deletes two
live columns. `breakTakenSeconds` (Tamm, DEFAULT 0) and
`breakSeconds` (Frist) are distinct columns and coexist.

**Admission rule (Konto's disposition, ruling R3 in §6):**
CONFIRMED lanes land as declared. PARTIAL lanes are admitted
**on their confirmed delta inventory only** — the delta list
in §4A is the admission artifact; files outside it are named
never-port. (KANTE's stricter CONFIRMED-only gate would bar
the Frist/Voss/Wecker deltas the epic exists to land; their
deltas are confirmed even where their bases are not.)

---

## 1 · THE MIGRATION — 12→13, ONE STEP (FASSUNG + Planner)

One composed `MIGRATION_12_13`, house style, placed after
MIGRATION_11_12 in data/MonolithDatabase.kt. Drop-in text
(Planner; rows 1–6 pinned verbatim from lane source by
FASSUNG/Planner/Critic-A2/A3; rows 7–8 see the warning):

```sql
ALTER TABLE guard_settings ADD COLUMN peekMinutes INTEGER NOT NULL DEFAULT 2;
ALTER TABLE guard_settings ADD COLUMN breakMinutes INTEGER NOT NULL DEFAULT 10;
ALTER TABLE focus_sessions ADD COLUMN breakSeconds INTEGER NOT NULL DEFAULT 600;
ALTER TABLE focus_sessions ADD COLUMN extensionSecondsAdded INTEGER NOT NULL DEFAULT 0;
ALTER TABLE focus_sessions ADD COLUMN extensionLog TEXT NOT NULL DEFAULT '';
ALTER TABLE focus_sessions ADD COLUMN withinLabel TEXT;
ALTER TABLE focus_sessions ADD COLUMN heldShutAtSeal INTEGER NOT NULL DEFAULT -1;
ALTER TABLE focus_sessions ADD COLUMN heldPeekAtSeal INTEGER NOT NULL DEFAULT -1;
```

| # | Column | Table | Type / Default | Source |
|---|---|---|---|---|
| 1 | `peekMinutes` | guard_settings | INTEGER NOT NULL DEFAULT 2 | Frist (times MonolithDatabase.kt:197) |
| 2 | `breakMinutes` | guard_settings | INTEGER NOT NULL DEFAULT 10 | Frist (:198) |
| 3 | `breakSeconds` | focus_sessions | INTEGER NOT NULL DEFAULT 600 | Frist (:199) |
| 4 | `extensionSecondsAdded` | focus_sessions | INTEGER NOT NULL DEFAULT 0 | Frist (:200) |
| 5 | `extensionLog` | focus_sessions | TEXT NOT NULL DEFAULT '' | Frist (:201) |
| 6 | `withinLabel` | focus_sessions | TEXT — **nullable, NO default clause** | Voss (routine-edit MonolithDatabase.kt:184; entity `String? = null`, MonolithModels.kt:370; freeze writer MonolithDao.kt:108) |
| 7 | `heldShutAtSeal` | focus_sessions | INTEGER NOT NULL DEFAULT **-1 (INFERRED)** | **NO CODE SOURCE — see warning** |
| 8 | `heldPeekAtSeal` | focus_sessions | INTEGER NOT NULL DEFAULT **-1 (INFERRED)** | **NO CODE SOURCE — see warning** |

**⚠ Rows 7–8 (Critic C1, four-seat confirmed):** these names
exist in NO tree — a full sweep of every lane, batch 9, and
the proofbase returns zero hits outside prose
(AUDIT-receipt.md:87,259; TRUTH3-WORDING.md W-41;
MAIL-AUDIT-2.md:87). They are Suki's Car-3 OFFER, assigned to
Rurik, never built. The -1 default is Konto's provisional
adoption by analogy to `heldAppsAtSeal`/`heldSitesAtSeal`
(main MonolithModels.kt:332-333) and the issue-10 designer's
D4.6/D4.8 — any other default makes pre-split rows print a
false HELD line. **Ruling R1 (§6) is Ren's:** confirm -1, set
0, or ship 6 columns and hold 2 (Planner's fallback). The
columns alone cure nothing: W-41's full package — seal-time
WRITER + enforcer-derived sites figure + restore reader +
print — is step 9 (§3, §4B2–B4). Without the writer the
columns sit at default forever and W-41 is recorded cured
while the imprint still prints a live recount
(batch-9 VM:837-838 contradicts its own doc at :622-625
today).

**Composed order (Critic C12 — the anchor collision):**
Frist appends his focus_sessions fields after `receiptNumber`;
Voss appends `withinLabel` at the SAME anchor. The composed
order is fixed here and the entity fields must mirror it
exactly, per table:
- guard_settings (append after `shortsReelsSniperEnabled`,
  batch 9's last): `peekMinutes`, `breakMinutes`.
- focus_sessions (append after `receiptNumber`):
  `breakSeconds`, `extensionSecondsAdded`, `extensionLog`,
  `withinLabel`, `heldShutAtSeal`, `heldPeekAtSeal`.

**Bump checklist:** version 12→13 (MonolithDatabase.kt:25);
append MIGRATION_12_13 to addMigrations (:242); GuardSettings
entity appends after `shortsReelsSniperEnabled`;
FocusSession entity appends the six after `receiptNumber`,
in the order above. exportSchema=false — no schema files.
**A new 12→13 chain test (fresh ≡ upgraded) is owed** —
assigned at step 11 (§3), Planner's spec; three mutually
exclusive MIGRATION_11_12 symbols exist across the trees and
none of their chain tests discharges this one (C12).
`heldAppsAtSeal` STAYS (additive, no DROP) — no source says
drop it. Bounds live in the setters, not the schema (peek
0–30, break 0–60, clamped on write; Critic A2 verified
entity defaults = migration defaults and both clamps agree
for Frist's five).


---

## 2 · COLLISION SURFACE + SEAMS (SCHLIFF rewrite; Critic C3–C14 folded)

**Hot and seamed files** (lane counts measured against each
lane's own v11 base — the proofbase — never against the
batch-9 tree; Edition 1's ≥3-lane claim was false for 9 of
its 14): touched by ≥3 batch-10 lanes —
data/MonolithRepository.kt (3) · data/MonolithDao.kt (4) ·
ui/MonolithViewModel.kt (5) · service/MonolithShieldService.kt (3)
· MainActivity.kt (5). Touched by 2 lanes but composed, never
overlaid — data/MonolithModels.kt · data/MonolithDatabase.kt ·
service/WatchStore.kt · ui/screens/ShieldMatrixScreen.kt ·
ui/screens/TactileDeckScreen.kt · data/ReceiptState.kt ·
ui/components/ReceiptSheet.kt. Single-lane files carrying a
named seam — data/EditableTimes.kt (two lanes, one path —
see the provider seam) · service/SessionSealTransition.kt ·
service/MonolithAccessibilityService.kt ·
ui/components/ParkDrawer.kt · AndroidManifest.xml. Every one
is composed function-by-function in one pass, never overlaid.
**Five files are composed LINE-BY-LINE (Critic's bottom
line): SessionSealTransition.kt · MonolithShieldService.kt ·
WatchStore.kt · ShieldMatrixScreen.kt · ParkDrawer.kt.**

**BASE-wins seams (named here so no lane text can win them
by accident):**
- **TactileSoundEngine.kt + SoundEffectsSetting.kt (KLANG
  S2, CRITICAL).** Every lane's engine is the pre-fact
  version (sha256 9e768f0233ab…, 32,906 B, `soundEnabled =
  true` literal) and no lane carries the store file at all.
  Batch 9's engine (fd724d5ac54c…, 34,989 B) + store +
  SessionSealTransition's `resyncSoundFromPersistedFact`
  are BASE text. Edition 1's §2 never named these files.
- **The Repository break block (KLANG S1, CRITICAL).**
  `breakBoundaryKnock` / `advancePastBreak` /
  `endSafeBreakEarly` exist ONLY in batch 9 (Repository
  advancePastBreak :1856, knocks :1872/:2039,
  endSafeBreakEarly :1906) — ST-02/ST-03. All seven lanes
  carry the silent inline flips. Batch 9's
  BreakVerbsRepositoryTest is the tripwire and the proof.
- **The span getters (Critic C6 + PULS).** Neither lane's
  text is the composed getter. Frist's own comment
  (times MonolithModels.kt:446-453) prescribes the hybrid —
  completed-target + `breakTakenSeconds` + (`breakSeconds`
  − remaining) — which exists in NO lane. Batch 9's
  spanElapsed prices breaks via Tamm's actually-taken
  ledger; times' reverts to full-length cycle arithmetic
  (the ST-02 lie). The composed getter is hand-written at
  merge; likewise `effectiveTargetSeconds` takes times'
  version WITH the `+ extensionSecondsAdded` term, or
  ST-04 extensions never move the enforced target (C6/A10).
- **ReceiptState (SCHLIFF M6 trap).** BASE's nullable
  `plannedSeconds` (post-RC-02) wins. Routine's ReceiptState
  diff is mostly v10 negative space (`Int?` reverted to
  `Int`, DrawerDay.nextBoundaryMillis absent) — only Voss's
  authored hunks transplant; an overlay silently regresses
  RC-02/DRW-05.

**The named seams, as amended (SCHLIFF's drop-in texts,
adopted):**

- **startPeekLease (Repository)** — two lane texts exist
  and they differ by one line: take pill's grant text
  wholesale (provider duration, `durationMillis <= 0` → no
  grant, `dao.clearOtherPeekLeases(packageName)` before the
  lease write — the one-live-lease law). Times' text is the
  same function minus `clearOtherPeekLeases`; nothing of
  Frist's is added to THIS function — his duration source
  is the provider's body (below). **There is NO
  Repository-head guard from Suki in any tree** (Edition 1
  said otherwise — false; Critic C9: batch-9 Repository
  :1194-1202 writes the lease unconditionally). Her
  unlisted-package guard stands in the merged tree at the
  ViewModel (`peekOfferFor` + the grant re-derivation in
  `start2MinutePeek`) and is preserved by THAT composition.
  The Repository-head guard is a separate OWED input,
  written at merge (§4B6).
- **The provider rewire (S-3)** — a FILE SELECTION, not a
  rewrite: `data/EditableTimes.kt` exists in two lane
  versions at one path. Times' version IS the rewired
  provider (settings-row reads, peekMinutes × 60_000 clamp
  0–30, breakMinutes × 60 clamp 0–60, peekMinutes = 0 →
  duration 0 → no grant via the Repository's `<= 0`
  return). It lands wholesale at step 4. Pill's
  constants-reading version must NEVER land — in particular
  not at step 5, after times (GLANZ D2 / SCHLIFF B2 /
  Critic C5: pill's provider + Frist's Repository is an
  outright compile break — Frist references companion
  bounds pill's file doesn't declare; pill's provider +
  anything else compiles and silently never reads the
  columns). The merge verifies exactly one
  EditableTimesProvider survives and it is times' text.
- **start2MinutePeek (ViewModel)** — THREE authors, base
  text is the merged tree's (Suki's): her pre-grant
  `peekOfferFor` re-derivation and her no-fallback package
  line (`val pkg = state.interceptedPackageName` — the
  `ifBlank { "com.whatsapp" }` in BOTH lanes' texts must
  NOT return; Critic C4). Onto that base: Frist's seed
  (`secondsRemaining = peekMinutes.value * 60`, replacing
  the constant — PULS: the seed is Frist's or the metronome
  dies; a 10-min peek otherwise gets cut at 2:00 while the
  pill shows ~8:00) and Pastille's ST-08 launch block —
  the pill lane's only ViewModel hunk — transplanted after
  `repository.startPeekLease(pkg)` inside the grant's
  launch block, so the lease is written before the launch
  fires. Neither lane's whole function overwrites. (The
  #16 flags package, §4F1, governs the grant gate + Boolean
  patch; its note that the countdown seed stays constant is
  SUPERSEDED here by the PULS/SCHLIFF evidence — Konto's
  composition call, flagged in §6.)
- **SessionSealTransition** — Edition 1's bullet was DEAD
  AND INVERTED (SCHLIFF B4 / Critic C3 / WIDERSTAND, three
  seats): Wache's F2 lands AS WRITTEN — ADOPT-never-delete,
  `SealVerdict.ADOPTED` added, and `isTombstoned` DELETED
  with its tombstone store and its Heartbeat caller
  (nofire's RoutineHeartbeat re-proves instead). Preserving
  `isTombstoned` per Edition 1 would resurrect a dead API
  and break F2. What must not be lost is the batch-9 sound
  text in the merged file — `resyncSoundFromPersistedFact()`,
  the bind-time re-read in `get()`, `boundEngineSoundEnabledForTest()`
  / `resetForTest()` (called by batch 9's
  SoundAndSocialTruthTest) — composed onto Wache's tail,
  never the reverse. The `isTombstoned` drop lands in the
  SAME sitting everywhere it is called (incl. batch 9's
  ArmProvingControllerTest fake override, which becomes an
  overrides-nothing compile error the moment the interface
  lands). The Critic verifies against BOTH sources (nofire
  for F2, the merged tree for the sound hunks) BEFORE this
  file is touched (KANTE step-3 entry condition).
- **Shield header (S-8)** — the merged tree's header is the
  base and never moves: UNA-09's
  `RoutineSchedule.liveCount(routines)` count (batch-9
  Shield :308-310) and UNA-07's `loadoutCapIndex` already
  stand there. Onto it, in §3's order: Wache's F5 first
  (the `lastRoutineTickMillis` parameter + the LAST
  ROUTINE CHECK block under the header, step 3), then
  Voss's editor transplants hunk-by-hunk around both
  (step 6). Voss's own header count
  (`routines.count { it.enabled }`) never enters — it is
  the pre-UNA-09 print UNA-09 falsified. Beyond the header
  (Critic C8): Voss renames `onAdjustRoutineTime` →
  `onSetRoutineTime` (batch-9 MainActivity:581 passes the
  old name), lacks the `loadoutCapIndex`/`pruneNoteText`
  parameters batch-9's call site passes, and reverts
  UNA-07 inside his file (`mutableIntStateOf(0)` vs
  `remember(loadoutCapIndex)`) — his hunks are re-based
  onto BASE signatures, never transplanted raw. **Konto's
  reconciliation of the KANTE/SCHLIFF tension (§6 R2):**
  steps 3–5 make NO change to ShieldMatrixScreen.kt other
  than each lane's own named delta (F5 is nofire's);
  Voss settles the editor at step 6 around BASE + F5.
- **Onboarding truth dependency — KEEP AS WRITTEN in
  Edition 1** (SCHLIFF B6, verified line-by-line): the
  ROOM beat (ReceptionSheet.kt:381-385) is true only with
  flow-clock's 20 s settle law (`SETTLE_MILLIS = 20_000L`,
  DeskClockSettle.kt:109) merged; onboarding lands after
  flow-clock, and Pforte's S-1 is merge-blocking for issue
  #14's half (§4C8). **Residue (Critic C7):** even WITH
  flow-clock merged, the sentence overclaims two edges —
  cold-open raises the clock AT ONCE
  (`sessionStartWallMillis < processBirthWallMillis →
  return true`) and a hand-dismissed session never raises.
  The sentence is cured or struck at step 7/8, not merely
  ordered (§6, owed).

**Missing seams, now named (SCHLIFF M1–M8 + Critic):**

- **M1 · FIRED AS SET (S-1 successor).** Batch 9 struck
  the clause; its own ParkDrawer comment (:115-119) makes
  restoration a MERGE-TIME ACT on re-verified live fire.
  Batch 10 lands F1–F5 but live-fire re-verification is a
  phone bench, not source — the condition is only half-met,
  so: the clause STAYS STRUCK in ParkDrawer; onboarding's
  stale print of it (`"ROUTINES: SET BY HAND ON SHIELD ·
  FIRED AS SET"`, onboarding ParkDrawer :109) and its
  second struck lie (`"STUDY RECORD: IN YPT"` — UNA-08
  corrected to `"STUDY HOURS: IN YPT"`) never enter;
  ReceptionSheet's `receptionFirstRoutineLine` ("IT FIRES
  AS SET", derived from `enabled` only — WORT/S-2, §4C8)
  is cured (derive from `enabled && daysMask != 0`, next
  boundary) or struck in the same sitting.
- **M2 · The sheet's flat durations.** ReceptionSheet's
  "two minutes" / "Ten minutes" hardcode what Frist's
  columns make settings — false at every non-default
  value. Cure ships AT THE TIMES MERGE (step 4, not 8):
  value slots from the GuardSettings row; peek 0 prints
  PEEK IS OFF; break 0 drops the beat (§4C8 S-3).
- **M3 · S-7 peek values inside the merged Suki text —
  three faces, no lane owns any (SCHLIFF M3 / §4B7):**
  (a) the withheld-reason PEEK_OFF branch rekeys from the
  constant `PEEK_DURATION_SECONDS <= 0` to
  `peekMinutes == 0`; (b) the composed `canPeek` =
  `peekOfferFor(...) && peekMinutes.value > 0` — an
  expression that exists in NO tree; (c) the airlock badge
  (`peekBadgeText(PEEK_DURATION_SECONDS)`, Airlock :669)
  rewires to the set value. Assign all three at merge or
  the values land half-read — UNA-03's disease on the
  peek's face. Also the pill's intercept gate lacks the
  `peekMinutes > 0` term (PULS) — the offer must not
  outlive the grant at peek = 0.
- **M4 · ShieldService hunk geography (C-9 successor).**
  Pill's hunks span :119–:408; nofire's denial stamp
  (:293–:307) sits INSIDE pill's hunk field; times adds
  :131 inside pill's :119–:157 region. Wache's stamp
  composes INTO Pastille's text there, not beside it;
  Frist's :131 likewise. **The monitoring loop itself has
  three authors (Critic C11):** pill's rewritten loop
  lacks batch 9's `EXTRA_INTERCEPT_PATH` extra and
  nofire's `CompletionSignal.soundIfNoUi` (ST-05) — compose
  the loop from any single lane and at least one landed
  cure silently un-lands.
- **M5 · The break span (B-3/S-4 successor).** Times'
  `extendActiveSession` (ST-04) inserts at the exact
  anchor above `updateSessionProgress` where batch 9
  inserted Tamm's 131-line break block. Both new functions
  seat above it; neither overwrites. The flips read the
  row's `breakSeconds` stamp while ST-04's writer leaves
  the tamper-penalty ledger untouched — composition must
  PROVE that, not assume it (its header states the law).
- **M6 · The receipt assembly (S-10 successor).** Times
  adds `extensions = row.extensionEvents` +
  `plannedSeconds = row.sealedSpanTargetSeconds` at the
  assembly site (+ ReceiptExtension fields on both
  assembly classes + the slip print); routine stamps
  `withinLabel` inside `publishReceipt`. Frist's
  Repository and Models are INSEPARABLE (Critic C13):
  `sealedSpanTargetSeconds` exists only in times' Models
  (:440) — a Repository-only transplant breaks compile, a
  Models-only one leaves PLANNED on the old source
  (AUDIT-receipt C3: slip and Deck take the same source in
  the same merge). Times' Dao lacks batch 9's
  `deleteAppsByPackage` and `updateShortsReelsSniperEnabled`
  — additive-only, BASE Dao text stands.
- **M7 · WatchStore + the occupied a11y anchor (B-1
  successor).** WatchStore is FOUR-way (Critic C10):
  nofire's file lacks batch 9's `Warrant.readMono`,
  `warrantAgeMs`/`warrantStaleForPrint` (called from
  MonolithCustomComponents :415,:527 and VM :1013) — land
  it whole and the Deck's warrant prints break compile.
  Warrant's residency hunk and nofire's F3 hunks are
  distinct regions of one band — one pass, either order.
  In the AccessibilityService, warrant's `notePipelineProof`
  anchor is occupied in batch 9 by Falk's `shortsSniperArmed`
  (:102-117) — the two compose side by side; no automated
  patch applies it (Critic A1). AndroidManifest: ONLY
  nofire's head hunk (`USE_EXACT_ALARM`) enters; its
  `allowBackup="true"` never overwrites batch 9's `"false"`
  (UNA-04).
- **M8 · The Deck chip anchor.** Times' ADD TIME chip and
  batch 9's END BREAK chip insert at the IDENTICAL anchor
  (TactileDeckScreen.kt:858) under mutually exclusive
  conditions — textual collision, semantic coexistence:
  compose both, in that order. (Same class in Dao: pill's
  `clearOtherPeekLeases` inserts where batch 9 inserted
  `deleteAppsByPackage` — coexist by hand.) Also (PULS):
  the Deck ENDS composer — truth's text uses breakMinutes,
  all other trees hardcode ×10; the wrong survivor prints a
  false end time BEFORE arming.
- **Flow-clock Airlock (Critic C14).** The lane's Airlock
  file lacks batch 9's `PeekWithheld`/`MonolithRepository`
  imports its own body needs — hunks transplant into BASE's
  file; the lane file never replaces it. One
  `deskClockBannerText` survives (the lane defines it where
  batch 9 already does — duplicate declaration otherwise).

**Feel/wording seams with merge teeth (the five feel seats,
only where they change a merge act):**
- **The session room exists in TWO editions (GANG S1).**
  The flow-clock LANE is settle-only (20 s; no sensor, no
  verdict; one flat fadeIn 420 ms / fadeOut 260 ms). The
  ~50 Hz sampling, HELD duty-cycle, SURRENDER_FADE 520 ms,
  veil + Landing live in the ISSUE-13 CELL TREE
  (cells/issue-13 DeskClockSettle carries the 7-param tick
  with sensorVerdict). Step 7 names its source FILE BY
  FILE from #13's merge list (§4C7) — composing the lane
  believing it is the room ships a lesser room under the
  room's name.
- **Extension multiplication (PULS S2 — ruling R6).**
  `extensionSecondsAdded` is never cleared at a block flip
  and the span prices it per block: one +10 tap on a
  3-block session = +30 span, blocks 2–3 silently longer,
  receipt prints one event. Per-block by design (then the
  chip + slip wording lie) or current-block-only (then
  Frist's span math is wrong) — Ren rules before step 4.
- **Sound (KLANG S3–S5).** The pill's hand-back is MUTE
  (PillController has no engine import; ACTION_END_PEEK
  also silent) vs the in-app harmonic seal — ruling R5.
  ADD TIME is the only silent chip in its row (times VM
  extendActiveSession :1358-1360, no sound call) — voice
  it at composition or record the silence as chosen.
  CompletionSignal + the ST-06 knock construct a FRESH
  engine per call, never released, born `soundEnabled =
  true` in-lane (correctness inherited entirely from the
  BASE-wins engine seam above) — and NO tests exist for
  either voice. Exactly one `playSealThrow` call site per
  tree and the SealThrowIdentityTest (md5 3c4a7f28…) are
  byte-stable across all eight trees — keep them so.
- **Wording-blocking four (WORT).** (1) The ReceptionSheet
  total branch (S-1, §4C8). (2) "IT FIRES AS SET" (M1).
  (3) The receipt HELD split — or the lawful fallback
  `UNDER THE SEAL: {n} APPS · {k} SITES`; never the
  flattened noun. (4) Voss's `plannedSeconds` type — BASE
  nullable wins (M6). Cure text for W-13/W-43 is copied
  VERBATIM from TRUTH3-WORDING.md ("THE MACHINE KEEPS ITS
  OWN LOG · YOUR STUDY HOURS LIVE IN YPT" — code today
  reads "YOUR STUDY RECORD LIVES IN YPT"); W-13 and W-43
  cure TOGETHER or the charge reopens (§4B5). Frist's
  EXTENDED line is correct on the slip but absent from the
  Drawer — W-44's second half extends to the Drawer day
  entry in the same form, same sitting. Separator law:
  `·` on faces, `•` only in the notification shade (lanes'
  Shield tally still uses `•`; BASE uses `·`). The
  WITHIN label grammar is identical in BASE and VOSS —
  freeze only, no seam. BIND/PROOF seam marks are
  identical in every tree — the kill under issue #4 lands
  once, on the merged tree.
- **The machine's "no" (WIDERSTAND).** Onboarding
  ParkDrawer's stub fallback prints **"OPEN"** for an
  unmeasured guard line (lane ParkDrawer.kt:536) — false
  confidence; it dies at composition (the ReceptionSheet
  total's cure text lives in cells/issue-14, NOT the
  onboarding lane). F3's denied-start stamp
  (`shieldStartDeniedWallMillis/Count`) has NO consumer
  anywhere — ruling R4: compose a Park consumer or land
  it voiceless, knowingly. Pill's ViewModel contributes
  NOTHING to the peek gates (its canPeek defaults ON for
  null/unlisted entity, blank package defaults to
  "com.whatsapp", no withheld print — a regression set).
  SIL-3's cooldown print (reason + ceiling minutes,
  ticking) is the house standard for a voiced "no".
  Batch-9 VM's start2MinutePeek second gate silently
  returns on a stale offer (dead tap, pre-existing) —
  noted, not charged to any lane. CompletionSignal's
  no-UI predicate (process IMPORTANCE_FOREGROUND) vs the
  VM's "UI alive" is an arbitration race at the boundary
  (PULS); SLEEP stand-down (ST-05) borrows the completion
  pulse at 03:30.
- **Motion notes that bind composition (GANG).** The pill:
  instant birth, one-frame teleport, instant death, 140 ms
  snap on the Android default interpolator; its ✕ can
  linger ~1 s after tap. The veil→Landing path has a 0.80
  black cut → full-bright flash → 520 ms re-dim. The slide
  cap carries five physics for one event family.
  `MonolithCurves.kt` is byte-identical across all five
  trees — the vocabulary is already unified; the accents
  are not. None of these block the merge; all are recorded
  so the composed tree's feel is a choice, not an accident.

**Carried seam ledger S-1…S-10 — disposition (SCHLIFF D):**
S-1 binds, transformed → M1 · S-2 CLOSED (`Warrant.readMono`
stands in batch 9; no lane adds a Warrant field) · S-3 binds
→ provider seam · S-4 binds → M5 · S-5 binds only as the
SessionSealTransition tail composition · S-6 covered (in-app
half in batch 9; overlay half is the pill lane; its binding
edge sits inside the startPeekLease seam) · S-7 binds,
unassigned in Edition 1 → M3 · S-8 binds → Shield seam ·
S-9 binds as §1 · S-10 binds → M6.


---

## 3 · MERGE ORDER (KANTE-sharpened; every step carries entry + exit proof)

Edition 1 named what happens at each step and proved
nothing. The ORDER survives; the proofs are new. **Step 0
is new and non-negotiable** — the pre-merge state is
destroyed by composition, so its evidence must be captured
first or it can never exist.

0. **Baseline freeze + proof capture (NEW).** (a) Capture
   the cure-proof FAIL baselines against main @ 817e8c7 —
   BEFORE composition (§5 G2: a PASS-on-composed claim is
   worthless without the pre-merge FAIL). (b) Lane freeze
   by tree hash: any later change to a lane tree voids its
   admission. (c) Lane-contribution inventory (every lane's
   confirmed delta, per §4A) — the M1 dropped-block gate
   (§5) audits the composed tree against THIS inventory.
1. **Admission gate.** Entry: the admission table is closed
   — lane × issue × verdict × report path, one row per
   input in §4 — and issue #5's Pass-2 per-lane rulings
   (§4D3) are recorded per lane (a REPAINTED ruling stops
   that lane here). PARTIAL lanes enter on their §4A delta
   inventory only. Exit: the signed table; any input not in
   it does not enter the merge.
2. **MIGRATION_12_13 composed FIRST** (§1). Entry: §6 E1
   closed — Planner's text + withinLabel pin + R1 ruled or
   the split columns held by name. Exit: diff shows
   Database + entities ONLY; the order-check passes
   (entity append order = ADD COLUMN order, per table);
   negative grep finds NEITHER lane's private 11→12 text
   anywhere in the tree. Every lane's data-layer hunks then
   reduce to non-migration text.
3. **warrant → nofire.** Warrant lands as written; exit =
   file-identity check against the lane (its two files +
   test, nothing else moved). Nofire entry: the Critic's
   `isTombstoned` pre-verification against BOTH sources is
   recorded BEFORE SessionSealTransition is touched (§2).
   F5's header block composes onto the BASE Shield header
   here (its own lane's named delta — §2 S-8); no other
   Shield change at this step.
4. **truth-3-times + the provider, same sitting.** The
   EditableTimes FILE SELECTION lands (times' text
   wholesale; pill's never enters). Exit = provider grep
   proof: the provider READS the columns (no hardcoded
   durations survive); arithmetic verified — peekMinutes
   × 60_000 clamp 0–30, breakMinutes × 60 clamp 0–60,
   peekMinutes = 0 → no grant. In the same sitting: M2's
   sheet-duration cure (value slots; step 4, not 8 — §4C8
   S-3), the composed span getters (§2 C6 hybrid), the
   Deck ENDS composer on breakMinutes, and Frist's
   Repository+Models together (M6/C13). R6 (extension
   semantics) must be ruled before this step.
5. **pill — the #16 flags package, in its own order**
   (§4F1: guard → latch → grant → gate → launch → record):
   Flag A composed start2MinutePeek (with Frist's seed,
   §2) · the Boolean grant gate · pill's startPeekLease
   wholesale WITH its Dao companion `clearOtherPeekLeases`
   (without it the take does not compile) · the OWED
   Repository-head guard composed at the lease's head
   (§4B6). Exit = seam-survival diff naming, by function:
   the guard, the launch, the duration source, the gate —
   plus the M3 composed `canPeek` and the pill intercept
   gate's `peekMinutes > 0` term.
6. **routine-edit transplants, hunk by hunk — with a HUNK
   LEDGER.** Every hunk recorded landed/excluded with its
   reason; drift files named in §4A2 never port; the dead
   voiced surface never enters. Shield precondition: Voss
   composes onto BASE + F5 + UNA-07/09 (§2 S-8), his
   signatures re-based to BASE's call sites (C8). The
   withinLabel freeze (writer + assembly read) lands with
   its anchor verified against batch 9 (Critic A3).
7. **flow-clock / session room — issue #13's merge list
   copied VERBATIM into the merge record** (§4C7: Klinge
   P-1…P-19 + A-1…A-12 dispositions; DeskClockSettle
   ported from the ISSUE-13 TREE, not the lane — §2 GANG
   S1), signed list-vs-tree check by Wecker's cell.
   Riders: ReceiptFiledStore lands as a store (a column
   instead = a new §1 item, Ren's call); the ROOM sentence
   edge-cure (§2 C7 residue) lands or the sentence is
   struck here; A-11 is NOT consumed here — it binds the
   pill merge (step 5, §4C7).
8. **onboarding, LAST.** Entry: flow-clock in (step 7) AND
   Pforte S-1 cured or the sheet does not land (§4C8);
   S-2's clause cured or struck (M1). Its ParkDrawer
   contributes ONLY the SETUP stub-row hunk, transplanted
   into batch 9's ParkDrawer; the "OPEN" stub fallback
   dies here (§2 WIDERSTAND). Pforte S-1's disposition is
   recorded by name — cured-with-test-flip or struck —
   never silent.
9. **Ren's hand-work, per-item sign-offs (three separate
   lines, each citing its defining source):** W-08 dynamic
   PARK plate (§4B1) · W-41 FULL package — writer storing
   the split + the enforcer-derived sites figure + the
   restore reader in the VM freeze + the split print, with
   the all-or-nothing gate: HELD prints only when shut,
   peek AND sites are all ≥ 0, else the line is OMITTED
   (GLANZ D5; §4B2–B4) · W-43 + W-13 twin cure, verbatim
   text (§4B5) · Receipt Group A beyond the two feeds
   (§4F2, A1–A11) · Frist-side prints (§4F3: PLANNED's
   seal-time source feeding slip AND Deck in this same
   merge; W-44 extension lines on slip AND Drawer).
   **The M1 dropped-block gate (§5) runs immediately
   after this step**, while reconstruction is still cheap.
10. **Test-call-site reconciliation.** A reconciliation
    table over EVERY test file in the composed tree (count
    stated): each call-site checked against the composed
    signatures BEFORE the suite is asked to run (issue #2's
    lesson, §4D2 — including the ArmProvingControllerTest
    fake-override death at step 3 and the three trees'
    mutually exclusive chain tests, C12). Exit: the table
    + a composition freeze line — after this step the tree
    changes only by a named defect fix.
11. **The single build.** Entry condition: **Sarthak's
    explicit word, quoted + dated in the merge record.**
    Exit = FOUR separate lines, never one "build passed":
    (a) suite counts by test-class name; (b) the migration
    chain verdict per column (12→13, fresh ≡ upgraded);
    (c) the cure-proof register — every cure FAIL→PASS with
    both hashes (§5 G2); (d) the artifact line — path +
    size + sha256 + DB version 13.

## 4 · INPUTS TRACKED FROM OTHER CELLS — THE LEDGER (REGISTER's replacement, adopted whole)

Every input the merge consumes is entered here with:
identity · verdict/state ON EVIDENCE · the §3 step that
consumes it · what breaks or falsifies if it is absent or
taken on faith. An input not in this ledger does not enter
the merge. The epic's checklist on issue #15 is updated as
each lands — an item is checked only when its work is IN
the merge tree, never when it is merely declared. States:
CONFIRMED (evidence in tree/test) · PARTIAL (admit ONLY
the confirmed delta inventory named) · DECLARED (code/text
written, uncompiled, no independent proof) · OWED (does
not exist yet; named owner must produce it).

### 4A · THE SEVEN LANES (§3 steps 3–8)

A1. **truth-3-times — Frist (Rurik). PARTIAL.** Confirmed
delta inventory ONLY: the five columns re-based into
12→13 (§1 rows 1–5; times MonolithDatabase.kt:197–201) +
EditableTimes.kt provider (times' text whole, §2) +
sealedSpanTargetSeconds derivation (MonolithModels.kt:440).
Consumed: step 2 (columns), step 4 (lane + provider, same
sitting). Absent/on faith: columns land unread (the S-3
disease), or unconfirmed hunks ride in and PLANNED/
extension prints have no seal-time source (receipt C3, F3).
A2. **routine-edit — Voss. PARTIAL.** Confirmed delta
inventory ONLY: the RC-03 freeze — withinLabel (§1 row 6;
routine MonolithDatabase.kt:184; Dao stamp :106–108;
Repository freeze :1597–1602). Its private 11→12 text
NEVER enters. Consumed: step 2 (column), step 6 (re-based
hunks, Shield last). Absent: the receipt WITHIN line keeps
re-deriving live and a restored slip can disagree with its
first printing. On faith beyond the inventory: a v10-base
overlay overwrites v12 work (Critic C2).
A3. **flow-clock — Wecker. PARTIAL.** Confirmed delta
inventory ONLY, as enumerated verbatim by issue #13's
merge list (C7). Consumed: step 7. Absent: onboarding's
ROOM beat is false and the session room stays trunk's.
On faith beyond the inventory: unlisted hunks enter
un-audited against AUDIT-session-room.
A4. **nofire — Wache. CONFIRMED.** F1–F5 + ST-05/06;
service/CompletionSignal.kt new. Consumed: step 3, after
warrant. Absent: ADOPT-never-delete is lost at the
SessionSealTransition composition (§2), routines keep
firing inexactly, PARK's "FIRED AS SET" stays struck, and
onboarding S-2's clause can never be lawfully restored.
A5. **warrant — Stempel. CONFIRMED AS DECLARED** (closed
lane, lands as written). provePipelineNow +
notePipelineProof (warrant MonolithAccessibilityService.kt
:77, :93, :104, :159; WatchStore.kt:187). Consumed:
step 3, first. Absent: the "CAN'T CHECK BLOCKING"
residency stamp never lands on cold start; PARK cannot
say LIVE (C4 rider). On faith: declared, uncompiled —
the cure-proof gate (D1) is its first independent test.
A6. **pill — Pastille. CONFIRMED** (C9 holds the full
#16 input; the lane alone is not the whole input).
Consumed: step 5, per the flags package (F1). Absent: no
pill, no ST-08 launch, and #13's A-11 single-derivation
law has nothing to consume. On faith: the seam's
breakDurationSeconds() has NO caller in pill or batch-9
(#16 finisher-4 §B4) — Flag B alone does NOT make
editable breaks work.
A7. **onboarding — Empfang. CONFIRMED AS DECLARED.**
SetupStore.kt, ReceptionSheet.kt (receptionTotal :147,
"THE ROOM" card :381). Consumed: step 8, LAST, after
flow-clock. Blocked regardless by #14's S-1 (C8): lands
only with S-1 cured or the sheet lies at its own total.

### 4B · REN'S HAND-WORK — NO LANE CARRIES THESE (§3 step 9, expanded)

B1. **W-08 dynamic PARK plate. OWED — Ren at merge.**
"PEEK: 2 MINUTES, THEN SHUT AGAIN" → "PEEK: {peekMinutes}
MINUTES, THEN SHUT AGAIN" / "PEEK: OFF" at 0, value from
the same GuardSettings row the lease reads
(TRUTH3-WORDING.md W-08). Fed by #9. Consumed: step 9,
after A1's columns exist. Absent: the plate prints a
false standing fact at every non-default setting.
B2. **W-41 seal split — columns. OWED — written at
merge.** heldShutAtSeal / heldPeekAtSeal (§1 rows 7–8).
They exist in NO lane tree; they are Suki's Car-3 OFFER
(AUDIT-receipt.md:87, :259–260). Consumed: step 2 (into
MIGRATION_12_13) + step 9 (print). Absent: W-41's full
cure cannot land; the HELD line stays STATE-ONLY/OMITTED
under the receipt's omission law.
B3. **W-41 seal-time writer. OWED — Ren at merge.** The
birth writer must store the split beside the merged
count — "the split cannot be reconstructed at print
time" (TRUTH3-WORDING.md W-41). With it: the
enforcer-derived sites figure (heldSites today is a
blind list-size, AUDIT-receipt item 9). Consumed:
step 9. Absent: columns land permanently at default and
the split print is fabricated from nothing.
B4. **W-41 split print. OWED — Ren at merge.** "HELD
{n} APPS · {k} SITES" → "HELD {n} SHUT · {m} PEEK ·
{k} SITES", zero segments omitted, all-or-nothing gate
(§3 step 9). Fed by #10 + #11. Consumed: step 9.
Absent: the slip books PEEK apps as HELD in the
permanent record.
B5. **W-43 receipt footer + W-13 twin. OWED — Ren at
merge** (custody on record: MAIL-AUDIT-2.md:83). "YOUR
STUDY RECORD LIVES IN YPT" → "YOUR STUDY HOURS LIVE IN
YPT" (TRUTH3-WORDING W-43). Fed by #10. Consumed:
step 9. Twin law: W-13 cures together with W-43 or the
charge reopens on whichever survives — W-13 is tracked
as part of THIS input, not separately.
B6. **Repository-head unlisted-package guard. OWED —
written at merge; exists in NO lane.** At the head of
startPeekLease: a grant against an unlisted package arms
NOTHING — no lease state, no cooldown (TRUTH3-MERGE-MAP
A-3; batch-9's startPeekLease :1194–1202 has no such
guard). The ViewModel-side peekOfferFor already in
batch 9 is NOT this input: it guards the offer, not the
Repository grant. Consumed: step 5, composed into the
pill startPeekLease (F1). Absent: an unlisted package
can still arm a lease + cooldown through any Repository
caller that bypasses the ViewModel.
B7. **S-7 peek-value rewires (three). OWED — Ren at
merge** (TRUTH3-MERGE-MAP S-7: "no surface may hardcode
the peek's 2 or the cooldown's 20"). Consumed: steps
4/5, with A1's columns live. (a) Withheld-reason branch
rekeyed to peekMinutes == 0 (batch 9 keys it on the
constant, comment: "the branch arms the day Rurik's
editable store can set it to 0" — that day is this
merge). Absent: "PEEK IS OFF" can never print. (b)
Composed canPeek expression (§2 M3). Absent: a stale
offer launches an app no lease protects. (c) Airlock
badge value → "{peekMinutes}m", same for the app rows'
tier badge and the price line (W-29). Absent: badge and
price lie at every setting but 2.

### 4C · THE OTHER CELLS (§3 steps 1, 6–9)

C1. **#6 Deck — Rost audit. DECLARED** (paper verdict;
"nothing here is built — every fix waits for Ren's
merge"). Consumed: step 1 gate; its merge-ready fixes
compose where files collide (§2). Absent/on faith: Deck
fixes enter with no cell behind them, or the audit is
mistaken for built work.
C2. **#7 Shield — Vogt audit. DECLARED.** FEEDS A2:
Voss's editor composes onto BASE + F5 at step 6 per the
amended S-8 (§2 — Edition 1's "Voss settles first" is
struck by SCHLIFF B5/Critic C8). Consumed: steps 1, 6.
Absent: the header composes in the wrong order and
Bodo/Wache header work (already in batch 9) is
overwritten by a v10-base transplant.
C3. **#8 Drawer — Lade audit. DECLARED.** Consumed:
step 1 gate; Drawer cures at steps 6/9. Absent: ST-04's
extension print (F3) has no audited Drawer form to land
in. Its §7 escalations (cabinet home, toys, forge) are
Ren/Sarthak decisions, NOT merge inputs — they block no
step unless Ren rules one in.
C4. **#9 Park — Hof audit. DECLARED.** FEEDS B1 (the
plate is PARK's furniture). Rider (its §6): the warrant
fix changes NO printed string in PARK and makes
Deck-reads-LIVE vs PARK-cannot-say-LIVE MORE visible —
its prints ride the same merge window as A5. Consumed:
steps 1, 3, 9.
C5. **#10 Receipt — Zettel audit. DECLARED.** FEEDS B4
(item 9/C1) and B5 (item 13/A3). Its full merge input
is larger — F2. Groups B (rulings) and C (dependencies)
are NOT step-9 inputs unless ruled. Consumed: steps 1, 9.
Absent: step 9 does the two word cures and silently
drops the other nine Group A cures.
C6. **#11 Held Door — Tor audit. DECLARED.** FEEDS
B4/B7: the door is where the split count, the withheld
reason (W-30) and the price line (W-29/SIL-2) print.
Consumed: steps 1, 5 (airlock/peek cluster). Absent:
B7's rewires land on an unaudited face.
C7. **#13 Session Room — Wacht cell. DECLARED** ("lane
closes DECLARED: nothing compiled"). Its merge list is
consumed VERBATIM at step 7 (Klinge P-1…P-19 +
A-1…A-12). Riders §4 must not lose: (i) NEW
data/ReceiptFiledStore.kt — a new store, NOT a column;
if Ren wants the column instead it becomes a §1 item;
(ii) proof debts 1–9 of its audit §7 still OWED on the
merged build (debt 10 discharged in DeskClockFlowTest);
(iii) A-11 is NOT consumed here — it is a cross-lane
obligation ON C9 (one PillDerivation consumed by the
shield service; inline re-derivation dies; one break
sentence across room/glass/shade). Absent/on faith:
step 7 improvises its own list, or the stand-down
ceremony ships unproven.
C8. **#14 Setup Sheet + onboarding — Pforte audit.
DECLARED, with a MERGE BLOCK.** S-1 STANDS, merge-
blocking for onboarding's half (step 8): receptionTotal
treats any print ≠ GRANTED as absence — a
granted-but-stale door prints UNPROVED on its card and
"WITHOUT ACCESSIBILITY" in the total, same screen, same
instant; the lane's own test codifies the lie. Cure
(SMALL): branch the total on print KIND + flip the
codifying test. Two further charges ride this cell:
S-2 "IT FIRES AS SET" (M1 — full cure derives from
enabled && daysMask != 0 next boundary; interim clause
dies FREE; depends on A4 + live-fire re-verification
before any firing claim may print) and S-3 flat
durations (M2 — cure ships AT THE TIMES MERGE, step 4).
Consumed: steps 1, 4 (S-3), 8.
C9. **#16 Pill — Pastille's cell. CONFIRMED as an
inventory; the merge input is the flags package (F1),
NOT the verdict alone.** The 18 tests are inventoried,
not run; flags on three of them (F1 circular oracle, F2
formula-mirroring, F6 Robolectric/shared-DB — a PASS
proves less than the names claim). Consumed: step 5.
Absent: step 5 takes the raw pill lane and re-introduces
`ifBlank { "com.whatsapp" }`, which the guard exists to
kill.
C10. **#12 Tasks + F-1 — Amt audit. DECLARED, with one
OWED ruling.** The merge input is fix list F-2…F-9
(≈2½ builder-days incl. recomposition). F-1 (routine
sessions take their intention from the head task) is
"machinery, not furniture", ESCALATED to Ren — no F-1
code enters any step until Ren rules + prices it.
Consumed: step 1 gate. Absent/on faith: F-1 is silently
built on a coordinator's say-so, or the F-list is
dropped because the title named only "F-1 machinery".

### 4D · STANDING PROOF INPUTS (§3 steps 1, 10, 11; §5)

D1. **Pruefer cure-proof standard. BINDING GATE** (§5
G2; TRUTH3-PROOF-STANDARD.md). Includes the composed-
migration chain re-run — no in-lane pass substitutes.
Consumed: steps 1, 11. Absent: every lane's in-lane
green is taken as proof of the composed tree — the
failure batch 9's map existed to prevent.
D2. **Batch-9 test-call-site lesson — issue #2. BINDING
GATE.** The app built and delivered while its unit
tests did not compile (stale Shield params at 5 call
sites + dead assert imports). Consumed: step 10.
Absent: the everything-merge repeats it at larger scale.
D3. **Issue #5 — Truth-Car-3 verification. STILL OWED.**
Una's Pass 1 is complete (every charge re-derived, all
stand); Pass 2 — per-lane diff rulings as the lanes
land — is still owed; a REPAINTED ruling stops that
lane's merge until answered. Consumed: step 1 gate, per
lane A1–A7. Absent: lanes enter on this cell's Verifier
alone and a repainted charge surfaces after composition.

### 4E · THIS CELL'S OWN INPUTS (§6 — they gate step 1)

E1. Merge Planner: drop-in MIGRATION_12_13 text +
per-column evidence — DELIVERED into §1, except the
split columns' defaults (B2: names on record; defaults
were NOT on record — Konto's -1 is provisional, R1).
E2. Verifier: per-lane verdicts — DELIVERED, entered in
§4A/§0. E3. Critic: charge list C1–C14 + acquittals —
DELIVERED, folded into §0/§2/§3. E4. Innovator:
mechanisms, spec + price — DELIVERED; dispositions in
§5; an unruled innovation is NOT an input and enters no
step.

### 4F · COMPOUND INPUTS — WHERE ONE CELL'S OUTPUT IS ANOTHER'S PART

F1. **#16 flags package** (cells/issue-16 finisher-4,
FINAL; builder-merge-flags concurs). Consumed at
step 5, IN THIS ORDER: (i) Flag A composed
start2MinutePeek — batch-9 guard + ST-08 launch
inserted between grant and record + `import
android.content.Intent`; order is law: guard → latch →
grant → gate → launch → record. (ii) Flag B provider
swap: times' EditableTimes.kt taken WHOLE — in the
batch-9 lineage this is an ADD, not a swap (no
EditableTimes.kt exists in batch 9), and it lands ONLY
after MIGRATION_12_13 + entity fields (step 2);
reversed, the tree does not compile. (iii) Pill's
startPeekLease wholesale WITH (iv) its two companions:
Dao `clearOtherPeekLeases` (absent from batch 9 —
without it the take does not compile) and the Boolean
patch (false at refusal), which the composed
ViewModel's grant gate gates launch + record on.
Absent any part: an unleased app launches at
peekMinutes=0 and a peek that never happened is
recorded; or no compile; or the gate has nothing
truthful to gate on. ALSO TRACKED from this package:
the pill countdown-seed divergence — the flags keep
the constant seed in both ViewModels; §2's
start2MinutePeek seam supersedes it with Frist's seed
(Konto's call, §6).
F2. **#10 Receipt Group A beyond W-41/W-43**
(AUDIT-receipt :225ff). All DECLARED, all mergeable
now, all consumed at step 9 unless Ren rules
otherwise: A1 total line · A2 outcome-word kills · A4
deflection outcome filter · A5 shape · A6 rhythm +
section rules · A7 arrival feed-not-fade · A8 dismissal
gesture + dwell · A9 faulted-row collapse + dismissed-№
persistence · A10 deflection print · A11 date on
cross-midnight slips. Absent: step 9 ships a slip that
still withholds its total, prints peek visits as
blocks, and can be swatted away by any tap.
F3. **Frist-side receipt/Drawer prints riding A1** (not
Ren's hand, not Voss's): (i) PLANNED's seal-time
source — sealedSpanTargetSeconds feeds BOTH slip and
Deck in the same merge (AUDIT-receipt C3; §2 M6);
(ii) W-44 extension print — per-extension "EXTENDED
+{n} MIN · {HH:MM}" lines on slip AND Drawer, list-
never-sum, PLANNED keeps its seal-time meaning;
(iii) ST-04's add-time writer itself — composes
Rurik-first in the break span (M5); Tamm's ST-02/ST-03
(already in batch 9) read its stamp.

**Checked and correctly EXCLUDED: issue #17** (snapshot/
preservation — its scope is the snapshot system's A/B
homes, not the app merge; it consumes no §3 step and no
step consumes it). Named here so the exclusion is a
decision, not a silence. — REGISTER


---

## 5 · GATES (KANTE-sharpened + M1–M4)

- **G1 · Build authority, checkable.** Only Ren compiles;
  only on Sarthak's word. The word is not a vibe: step 11's
  entry condition is the quoted, dated instruction in the
  merge record. Any compile without it is out of authority
  and its results are void.
- **G2 · Cure-proof register (Pruefer standard).** A cure
  counts only as FAIL on the pre-merge tree → PASS on the
  composed tree, both runs recorded WITH HASHES in the
  register. Pre-merge FAIL baselines are captured at
  step 0 — before composition destroys the "pre" state.
  No in-lane pass substitutes for a composed-tree pass.
- **G3 · Presentation (routing rule, reclassified).**
  Nothing from this cell reaches Sarthak except through
  Ren. (Edition 1 listed this beside the proof gates; it
  is a routing rule, not a proof.)
- **M1 · Dropped-block gate (NEW, after step 9).** The
  composed tree's landed inventory is diffed against
  step 0's lane-contribution inventory. Every delta in the
  inventory is either IN the tree or named excluded with
  its reason. (Converges with Innovator mechanism #2 —
  the symbol survival census — the only proposed mechanism
  that catches ABSENCE: batch 9's dropped lease block is
  the standing example of what presence-checks miss.)
- **M2 · Seam-ledger sign-off (NEW).** The six §2 named
  seams + M1–M8 are signed off one by one by the Critic
  against the composed tree before step 10 opens. A seam
  without a signature is an open seam; step 10 does not
  start over an open seam.
- **M3 · Abort disposition (NEW).** If step 11 fails: the
  composed tree is PRESERVED as-is, the failing lines are
  reported by name, and any further compile is a NEW build
  requiring Sarthak's fresh word. No silent retry loop.
- **M4 · Open facts bind (NEW).** §6's items are
  preconditions, not footnotes: each is closed-with-evidence
  or the step that depends on it is recorded BLOCKED, by
  name, in the merge record. Nothing is dropped by silence.

**Innovator's mechanics — dispositions (spec + price in his
report; adoptions are Ren's ruling, R7):** #1 rehearsal tree
with per-lane git commits + zero-reject gate · #2 symbol
survival census (adopted into M1 above as a gate) · #3
static test call-site reconciliation (its substance is
step 10) · #4 executable seam contracts (grep assertions
per seam — the M2 sign-off's instrument) · #5
migration-first + schema-identity diff (its substance is
step 2's exit) · #6 diff-back attribution audit.
**Innovator marked #1 and #2 FORCED.** Evidence base:
batch 9's own patch logs — failed hunks, .rej files, fuzz
offsets up to +200 lines, a "reversed patch detected" on
MonolithDatabase. Konto carries #1 + #2 as the plan's
method; Ren rules (R7).


---

## 6 · RULINGS FOR REN + OPEN FACTS (binding preconditions per §5 M4)

**Rulings — Ren's, surfaced not decided:**
- **R1 · The W-41 split package.** Confirm DEFAULT -1 for
  `heldShutAtSeal`/`heldPeekAtSeal`, set 0, or ship 6
  columns and hold 2. Konto's provisional: **-1**, on the
  triple-sourced analogy (§1) — and whichever way it goes,
  step 9 carries the FULL package (writer + enforcer-derived
  sites figure + restore reader + print), or W-41 ships as
  a false cure (Critic C1). Also confirmed, no ruling
  needed: `heldAppsAtSeal` stays (additive).
- **R2 · Shield sequencing.** Konto's reconciliation (§2
  S-8): BASE header never moves; F5 (nofire's own delta)
  composes onto it at step 3; steps 3–5 otherwise touch
  ShieldMatrixScreen.kt not at all; Voss's editor transplants
  settle at step 6 around BASE + F5. (KANTE's stricter
  zero-touch and Edition 1's "Voss settles first" are both
  superseded by the tree evidence.)
- **R3 · PARTIAL-lane admission.** Konto proposes: PARTIAL
  lanes enter on their confirmed delta inventory only
  (§0/§4A) — the delta list is the admission artifact —
  over KANTE's CONFIRMED-only gate, which would bar the
  deltas the epic exists to land.
- **R4 · F3's denied-start stamp** has no consumer anywhere.
  Compose a Park consumer at merge, or land the stamp
  voiceless, knowingly (WIDERSTAND).
- **R5 · Pill hand-back voicing.** The pill's door is mute
  where the in-app door seals with the harmonic throw
  (KLANG S3). Voice it, or accept the mute door — decided
  in the plan, not discovered on the phone.
- **R6 · Extension semantics** (PULS S2): per-block by
  design or current-block-only. Must be ruled before
  step 4 — Frist's span math and the chip/slip wording
  cannot both be right until it is.
- **R7 · Innovator adoptions.** #1 rehearsal tree + #2
  symbol survival census are carried in §5 as the plan's
  method (Innovator marked both FORCED; #2 is gate M1).
  #3–#6's substance is partly inside steps 2/10 and gate
  M2; formal adoption of the remainder is Ren's.

**Open facts still owed (each blocks its named step):**
- The 12→13 chain test — owed, assigned to step 11's
  proof set (Planner's spec, §1). Blocks step 11.
- Issue #5 Pass 2 per-lane rulings — owed per lane at
  step 1 (§4D3).
- The ROOM sentence edge-cure (cold-open + hand-dismissed,
  §2 C7 residue) — owed at steps 7/8: cured text or the
  sentence is struck.
- #13's proof debts 1–9 — owed ON THE MERGED BUILD
  (bench/captures; they do not block composition, they
  block calling the room proven).
- F-1 (issue #12) — no code until Ren rules + prices it
  (§4C10). Not a merge input until then.

**Cell record:** 14 seats, 14 reports, all folded above —
nothing dropped by silence; every exclusion named (§4,
REGISTER's #17 check). The epic's checklist on issue #15
stays UNCHECKED: nothing in this edition is merged, and an
item is checked only when its work is IN the merge tree.

— Konto, Oct 30, 2026 (Batch-10 Edition 2)
