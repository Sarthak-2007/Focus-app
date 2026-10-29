# TRUTH CAR 3 — MERGE MAP

**Keeper:** Konto, Keepers of Record (merge cartography seat).
**Commission:** Ren, Oct 29, 22:29 IST — the map that makes Ren's
hand-merge of Truth Car 3 one pass instead of a week.
**Edition 1 — Oct 29, ~22:58 IST.** Surveyed from the lanes' trees,
the mailroom, the wall, and trunk — never from summaries. Read-only
over every lane; this desk edits nothing but this file.
**Re-survey triggers:** each builder's declaration letter (proof log
opens a run), each lane's wall note, Pruefer's countersigns. The map
is re-issued at every trigger; an edition's timestamp is its truth
boundary.

**Ground:** Una's two audits (plan/AUDIT-park-claims.md,
plan/AUDIT-deck-intercept-claims.md) · Freya's machinery spec
(plan/PILL-AND-TIMES.md §§3–4, restored whole) · Tief's register
(plan/SMALL-THINGS.md) · Pruefer's proof standard
(plan/TRUTH3-PROOF-STANDARD.md) + log (plan/TRUTH3-PROOF-LOG.md).
All lanes base on trunk = batch-8 state, DB v11 (Focus-app working
tree, incl. Ren's HB-2 fix). Trunk line numbers below are pre-car
unless a lane's own coordinates are named.

---

## 0 · LANE CENSUS (at Edition 1)

| Lane | Builder | State at survey | Tree evidence |
|---|---|---|---|
| truth-3-times | Rurik | **NOT YET OPEN** — no lane dir | — |
| truth-3-shorts | Falk | WRITING | 8 files differ (see §1); column + local migration + sniper gate in tree |
| truth-3-sound | Imke | WRITING (service side) | 3 files: SessionSealTransition, TactileSoundEngine, NEW SoundEffectsSetting.kt; no UI surface yet |
| truth-3-sweep | Bodo | WRITING | 10 files differ; UNA-04/06/07/08/09 all have tree presence; UNA-05 = letter only |
| truth-3-intercept | Suki | OPEN, NO HUNKS YET | lane dir exists; app/src identical to trunk |
| truth-3-breaks | Tamm | **NOT YET OPEN** — no lane dir | — |
| truth-3-proofbase | Pruefer | OPEN | pristine trunk copy (22:42); no lane declared written; all countersigns PENDING |

**Adjacent parties to the same build** (not the car; their hunks
share the car's files, so the merge meets them in the same pass):

| Lane | Owner | State | Relevance |
|---|---|---|---|
| builds/warrant | Stempel | WRITTEN (code) | 2 production files + WarrantStampResidencyTest; §1 A-1, C-1 |
| builds/nofire | Wache | WRITTEN (code) | 10 files + NEW CompletionSignal.kt; item 18 / F1–F5 + ST-05/06; §1 A-2, B-2, seams S-1/S-5/S-8 |
| builds/pill | Pastille | WRITTEN (code) | 4 NEW files (PillController/PillFace/PillTheme/EditableTimes) + 4 changed + 2 tests; seams S-3/S-6 |
| builds/routine-edit | Voss | OPEN since ~19:04 | **BASED ON PRE-CAR-2 TRUNK (DB v10)** — lacks UndoDeleteStore.kt / RecordCapsTest.kt that trunk carries; its hunks transplant, never overlay. Touches Repository, ViewModel, Shield, Deck, Drawer, ReceiptState, Dao, Database, Models — Ren sequences it against the whole car (§1 C-9, seam S-10) |

---

## 1 · FILE COLLISION TABLE

Classes: **A — TEXTUAL** (same lines/anchor; hand-compose, no
automated patch survives) · **B — ADJACENT** (hunks interleave in
one region; one pass, in the order named) · **C — SHARED-DISTINCT**
(same file, separate regions; one pass, any order) · **D — ONE
PARTY** (no collision; listed where a second party reads the file).

### A — textual overlaps (4)

**A-1 · service/MonolithAccessibilityService.kt — the companion
anchor (trunk :67–73).** Falk and Stempel each append a NEW
function immediately after `armedMono`'s `internal set`, after the
identical 6-line context block, each +11 lines:
- Stempel (warrant, WRITTEN): `notePipelineProof()` — the one stamp
  write; plus hunks at :75–115 (provePipelineNow + connect path)
  and :125–133 (event path routed through the write).
- Falk (shorts, WRITING): `shortsSniperArmed(settings, packageName)`
  — the UNA-01 gate; plus hunks at :10/:25 (imports), :150–163,
  :161–188 (sniper enabled check in the fire path :164–177's
  neighbourhood), :176–195 (settings read :179–180 + fallback zone).
Both insertions coexist cleanly by hand (order irrelevant); any
patch tool conflicts on the shared anchor. Compose Stempel's block
and Falk's block side by side at the anchor; keep both comments.

**A-2 · service/SessionSealTransition.kt — the tail.**
- Wache (nofire, WRITTEN): 8 hunks spanning the file (:19, :50,
  :62–85, :88, :134, :232, :263, :276–312) — the seal/adoption
  rewrite reaches the tail.
- Imke (sound, WRITING): hunks @@ -95 (engine seam) and
  @@ -289,19 → +302,40 — the get()/bind block itself
  (trunk :288–303), where every get() learns to read the persisted
  sound fact.
Their tail hunks overlap in trunk coordinates :288–312. Merge
order: **nofire first (it is written), Imke's binding cure composed
onto Wache's tail text** — her +40-line replacement must be
re-based on his adopted-seal flow, not pasted over it.

**A-3 · data/MonolithRepository.kt — startPeekLease
(trunk :1091–1099).** THREE parties write the lease:
- Pastille (pill, WRITTEN): reworked the grant (duration via
  EditableTimesProvider, `dao.clearOtherPeekLeases(packageName)`,
  hunks @@ -1124 in the lease bookkeeping + @@ -529 block).
- Rurik (times): changes the duration SOURCE inside the grant —
  `peekMinutes` off the settings row instead of the constant
  (FT-3), via the provider rewire (seam S-3).
- Suki (intercept): UNA2-01 — a grant against an unlisted package
  must arm NOTHING (no lease state, no cooldown); the guard sits
  at the same function's head.
Compose in that order: pill's text as the base, Rurik's source
swap inside it, Suki's guard at its head. One function, three
authors, zero shared lines if composed in this sequence.

**A-4 · service/MonolithAccessibilityService.kt — the settings
block (trunk :164–195).** Falk's sniper gate reads the settings
row at :179–180; Bodo's UNA-06 cure deletes the `ifEmpty`
fallback at :182–185 — the same read block, consecutive lines.
Falk's read must survive with the fallback gone (an empty stored
list stays empty AND the row — with Falk's column — still reads).
Compose together, in one sitting, not in two passes.

### B — adjacent / interleaved (4)

**B-1 · service/WatchStore.kt — the :123–:221 band.** Three
parties, no shared lines, one band:
- Wache @@ -123,7 → +123,14 (F3 denied-start stamp region) and
  @@ -201,6 → +208,20.
- Stempel @@ -165,12 → +165,26 (`noteResidencyStart()` only — he
  does NOT touch the Warrant class, deriveWarrant, standingWarrant,
  or refusalSentenceFor; his letter, mail/wagen/2220).
- Suki: UNA2-03 adds a read instant to the **Warrant data class
  (:62–67)** — outside the band, but the same file; **the field
  MUST carry a default value**: TactileDeckScreen.kt (~:143–152)
  constructs `Warrant(...)` positionally with five args, and a
  no-default sixth field breaks that call site and every other
  whole construction. (Stempel's warning; relayed to Suki by this
  desk, §3 S-2.)
- Imke reads :163 + :409–418 (UNA-02's board word follows the
  toggle) — below the band, distinct lines.

**B-2 · ui/screens/ShieldMatrixScreen.kt — the header block.**
- Bodo: UNA-09's count derivation at :173–177 (header counts
  `routines.count { it.enabled }` today; cure reads the shared
  `isLive` he added in MonolithModels :684 — see C-2) and UNA-07's
  cap rest at :103 (now resting on his LoadoutStore).
- Wache: F5's liveness line, hunks @@ -85 and @@ -175,6 → +176,30
  — his print lands IMMEDIATELY under Bodo's count lines in the
  same header block.
- Falk cites :71–74/:103/:173–177/:308–313 from UNA-01's audit
  section (render-site discussion); his toggle itself renders in
  ParkDrawer — no Shield hunk of his at Edition 1.
- Voss's routine editor is this screen's heaviest writer (v10
  base) — Ren sequences him last on this file.
Compose Bodo's count + Wache's line as one header edit.

**B-3 · data/MonolithRepository.kt — the break span
(:1680–1759).** Rurik's ST-04 write (add time by his hand — a NEW
writer beside the tamper penalty at :1680; the penalty ledger
must stay untouched by it) and Tamm's ST-02 verb (zeroes the
break's remaining inside the flip span :1724–1759) + ST-03's
knock call sites on the same flips. Adjacent writers, one span;
compose Rurik's write first (it defines the target the flip
reads), then Tamm's verb.

**B-4 · ui/MonolithViewModel.kt — the peek cluster
(:2091–2161).** Suki: canPeek (:2091–2112, UNA2-01) and
end2MinutePeekEarly (:2150–2161, UNA2-06 reachability) are one
neighbourhood; Pastille's pill state lands immediately AFTER
(:2166+, her @@ -2165 hunk) and her hand-back INVOKES
end2MinutePeekEarly — the verb Suki makes reachable is the verb
the pill's ✕ calls. Compose Suki's cluster first, then seat
Pastille's block after it.

### C — shared file, distinct regions (9)

**C-1 · service/MonolithAccessibilityService.kt (whole file).**
Beyond A-1/A-4: Suki reads the per-app lease path (:209–213) for
UNA2-01 — read-side, no hunk expected; if she writes there it
joins A-4's sitting.

**C-2 · data/MonolithModels.kt.**
- GuardSettingsEntity (:705–~740): Rurik (peekMinutes,
  breakMinutes) + Falk (shortsReelsSniperEnabled — IN TREE,
  appended after `lastReceiptSessionId`, Models @@ -728) append
  fields to the same constructor: independent lines, composed by
  appending all three — ** in the same order the composed
  migration adds them** (schema identity, §2). Imke adds NOTHING
  here (her fact is a prefs store, §2 deviation).
- FocusSessionEntity (:268): Rurik alone (breakSeconds).
- Span derivations (:369/:373/:376): Rurik alone writes
  (literals → the row's stamp); Tamm reads.
- Routine helpers (:684): Bodo alone — NEW `RoutineEntity.isLive`
  (enabled && daysMask != 0), the one definition the chain
  derivation and Shield's count both read (UNA-09).

**C-3 · data/MonolithDatabase.kt.** Version :25, MIGRATION_11_12
slot beside MIGRATION_10_11 (:160), addMigrations list (:212).
Three lanes carry local 11→12 texts (Rurik's spine, Falk's —
IN TREE, version already 12 in his lane); Ren composes ONE
(§2). Class: single-point composition, not a merge.

**C-4 · data/MonolithDao.kt.** Pastille @@ -31,6 → +31,15
(clearOtherPeekLeases + lease queries, file head); Falk
@@ -248,6 (his settings write path rides the guard block);
Rurik's setters ride the existing guard insert/update
(:235–241) — no new Dao text expected from times; if his letter
adds any, it lands in the same :235 block as Falk's hunk —
re-survey at his declaration.

**C-5 · ui/MonolithViewModel.kt (whole file).** The car's most
shared file; beyond B-4: Falk deletes the memory toggle
(:1287–1289 + state :165) and binds the row; Imke rebinds
toggleSound (:1254–1257) to her store; Suki rewrites
appsCellCount (:451–454, UNA2-02) and the FloatingPeekPillState
ticker (:110–118, :833–848, UNA2-06 — the printed remaining must
derive from the STORED END, not the VM's tick count); Bodo
(VM hunks in tree — loadout/plate derivations); Wache one block
(@@ -411); Pastille header + :2166 block. Regions are distinct
functions throughout — one pass, function by function. The
:1254–1289 band holds Imke's and Falk's toggles 30 lines apart;
compose in one sitting to keep the toggle block coherent.

**C-6 · ui/components/ParkDrawer.kt.** Bodo owns the plate
(PLATE_BODY :104–115 — UNA-05 + UNA-08's lines); Falk's shorts
toggle renders here (in tree); Imke's UNA-02 social toggle +
UNA-03 sound toggle render here (not yet in tree at Edition 1).
Plate vs board rows: distinct regions, one file.

**C-7 · MainActivity.kt.** Suki: UNA2-03's refresh wiring
(:300–308), APP-4's wiring removal (:475–481), UNA2-05's rider
(:740–744 — the primer binding STAYS removed). Wache: one-line
hunks at :192 and :566. Falk: MainActivity differs in his lane
(region unmapped at Edition 1 — re-survey). Distinct lines;
one pass.

**C-8 · AndroidManifest.xml + res/xml.** Wache's F1 permission
(USE_EXACT_ALARM, @@ -8,6 → :8–19, file head) and Bodo's UNA-04
(allowBackup "true"→"false" at :25 — IN TREE — plus BOTH
res/xml/backup_rules.xml and res/xml/data_extraction_rules.xml
rewritten in his lane). Head vs application tag: distinct
lines, same file — compose together and re-check the manifest
as a whole.

**C-9 · service/MonolithShieldService.kt.** Pastille (7 hunks —
the pill controller's driver, :102–:408 spread) × Wache
(startShield denial stamping, hunks at :114 and :293–307).
Regions mostly distinct; Wache's :293 hunk sits inside
Pastille's hunk field (her @@ -207 → :291 and @@ -217 → :306
bracket it) — compose Wache's stamp INTO Pastille's text there,
not beside it.

### D — one party in the car (no collision)

- **ui/components/AirlockAndDisarmOverlays.kt** — Suki alone:
  UNA2-05 (:630–634), UNA2-07 (:548–554), APP-1 (:197), APP-3
  (:436–447).
- **ui/components/MonolithCustomComponents.kt** — Suki alone:
  imprintTenantText (:408–431, UNA2-02). (refusalSentenceFor at
  :451/:455 is Stempel-adjacent ground; he does not touch it.)
- **service/MonolithNotificationListener.kt** — Suki alone:
  APP-2 (:82).
- **data/RoutineDerive.kt** — Bodo reads/writes for UNA-09's
  shared truth (his isLive lands in Models, C-2; Derive itself
  may take a call-site hunk — in tree at Edition 1).
- **data/DrawerDay.kt** — Bodo (UNA-08's on-phone record side);
  Voss's lane also differs here (adjacent, S-10).
- **service/TactileSoundEngine.kt** — Imke (hunks :41, :251–279,
  :288–325: the engine's enabled initializer learns the store).
  Tamm's ST-03 knocks are CALLS into this engine from the flip
  sites — no engine text from breaks.
- **ui/screens/TactileDeckScreen.kt** — Suki (UNA2-02 tenant
  prints :349–571 cluster; UNA2-03's positional Warrant
  construction ~:143–152 — see B-1's default-value law; UNA2-04's
  strip :459–485 + :990) + Tamm (ST-02's Deck chip — a NEW
  element in the break face) + Falk (cites :112–113 only).
  Suki and Tamm write different elements; one pass.

---

## 2 · THE MIGRATION COMPOSITION — 11→12, ONE STEP

Freya §4.1's law: ONE joint migration carries the car's columns;
if any column lands first under its own number the rest take the
next — the joint step is preferred, the sequence is the law.
Registration point (trunk): MonolithDatabase.kt — `version = 11`
(:25) → 12; `MIGRATION_11_12` object beside MIGRATION_10_11
(:160); appended to `addMigrations(...)` (:212). Falk's lane
already carries exactly this shape for his one column.

**The composed migration carries FOUR columns** (not five — see
the deviation below):

| # | Column | Entity → table | Type | Migration default | Owner | State |
|---|---|---|---|---|---|---|
| 1 | `peekMinutes` | GuardSettingsEntity → `guard_settings` | INTEGER NOT NULL | 2 | Rurik (times) | Specced (Freya §3.1/§3.6); lane not yet open |
| 2 | `breakMinutes` | GuardSettingsEntity → `guard_settings` | INTEGER NOT NULL | 10 | Rurik (times) | Specced (§3.1/§3.6); lane not yet open |
| 3 | `breakSeconds` | FocusSessionEntity → session table | INTEGER NOT NULL | 600 for pre-existing rows (the only break they could have been created under, §4.1) | Rurik (times) | Specced (§3.2); lane not yet open |
| 4 | `shortsReelsSniperEnabled` | GuardSettingsEntity → `guard_settings` | INTEGER NOT NULL | 1 (true — the sniper fired unconditionally pre-v12) | Falk (shorts) | **IN TREE** — entity field appended after `lastReceiptSessionId` (Models @@ -728); SQL in his lane verbatim: `ALTER TABLE guard_settings ADD COLUMN shortsReelsSniperEnabled INTEGER NOT NULL DEFAULT 1` |

**Composition rules for Ren's hand:**
- Entity field order in GuardSettingsEntity must equal the
  composed migration's ADD COLUMN order (Room schema identity):
  append peekMinutes, breakMinutes, shortsReelsSniperEnabled
  after `lastReceiptSessionId`, in that sequence; breakSeconds
  appends on FocusSessionEntity independently.
- Fresh-install schema comes from the composed entity; upgraded
  installs from the composed migration; FT-1
  (MigrationChainTo12) verifies both land identical — and
  Pruefer re-runs the chain against the COMPOSED migration at
  merge (his countersign for the migration is separate from
  every lane's in-lane pass).
- Bounds live in the setters, not the schema (FT-6): peek 0–30,
  break 0–60, clamped on write (Freya §3.5).

**DEVIATION ON RECORD — the sound fact is NOT a column.**
Freya §4.1's manifest and Pruefer's standard both assume UNA-03's
sound flag rides the migration. Imke's lane (WRITING) instead
persists it in a dedicated SharedPreferences store —
`service/SoundEffectsSetting.kt` (NEW): prefs file
`monolith_sound_effects`, key `sound_effects_enabled`, default
true, `commit()` writes, every engine's initializer reads it.
Her argued ground (file header): the fact must be readable
SYNCHRONOUSLY at engine construction on any thread, which a Room
read cannot be; the seal marker's prefs are the house precedent.
Cartography, not ruling: the deviation is proof-compatible with
Pruefer's named tests as written (they assert behaviour across
process death, not storage location) — but it contradicts §4.1's
letter, so it must be DECLARED to Pruefer and Ren by Imke (this
desk has written to her, §3 S-9) and ruled at merge. Until ruled,
the composed migration above stands at four columns.
**Second store of the same kind:** Bodo's UNA-07 cure persists
the hand-applied loadout in `data/LoadoutStore.kt` (NEW,
SharedPreferences, commit() writes, hand path only — a routine's
own loadout never moves his selector). No schema business; no
migration impact; recorded here so the build's store census is
complete: the car adds TWO prefs stores and FOUR columns.

---

## 3 · SEAM LEDGER

State words: SETTLED (in writing) · SENT (letter out, answer
owed) · OPEN (named, no letter yet) · DISCOVERED (found by this
survey) · WRITTEN (one side's code exists).

**S-1 · UNA-05's words × item 18's machinery — Bodo × Wache.**
The plate prints "FIRED AS SET"; only Wache's F1–F5 landing
makes it true. Bodo's letter is SENT (mail/wache/2233, 22:33,
unread at survey) asking for the landing state + any residue
where the honest sentence is weaker. Wache's lane is WRITTEN
(code) but he has not yet declared to Bodo. Bodo's fallback, in
his letter: print untouched + conditional wording filed to Ren —
the words cure then slides to the merge. Proof state by
construction: HALF-PROVEN (Pruefer). **Owner of the next move:
Wache (answer) — the merge cannot close UNA-05 without it.**

**S-2 · UNA2-03's read instant × the warrant stamp — Suki ×
Stempel.** SETTLED as a boundary, MISDELIVERED as a letter.
Stempel's note (mail/wagen/2220, 22:20) fixes the split: he
touches MonolithAccessibilityService + WatchStore's
noteResidencyStart ONLY; the Warrant class, deriveWarrant,
standingWarrant, refusalSentenceFor are NOT his — they are
Suki's ground. His one constraint travels with it: **any new
Warrant field carries a default value** (positional construction
at TactileDeckScreen ~:143–152). The letter sits in WAGEN's
inbox — the refused builder — and Suki has never seen it.
**This desk has relayed it to mail/suki/ (Edition 1).** Stempel's
half: WRITTEN. Suki's half: lane open, no hunks yet.

**S-3 · The times provider rewire — Pastille × Rurik.**
Pastille's half is WRITTEN: `data/EditableTimes.kt` (pill lane)
— one read-point for both durations, reading the shipped
constants today, its header documenting the merge contract.
At merge, its two functions switch to Rurik's columns
(peekMinutes × 60_000 clamped 0–30; breakMinutes × 60 clamped
0–60; peekMinutes = 0 → no grant at all), and the pill's
startPeekLease rework (A-3) already calls through it. **The
rewire itself is currently UNASSIGNED** — neither lane's brief
owns the provider's post-merge text. Flagged to Ren in this
desk's closing letter: assign it (Rurik in-lane at declaration,
or Ren's hand at merge), or the columns land and nothing reads
them — UNA-03's disease in a new coat.

**S-4 · The break triangle — Tamm × Rurik × Imke.**
ST-02's verb and ST-03's knocks (Tamm) act on the break flips
in Repository :1724–1759; the span those flips serve reads
Rurik's breakSeconds stamp (C-2) — Tamm's math must read the
stamp, never the retired literals. ST-03's knocks fire through
the engine Imke makes honest: a knock on a sound-OFF engine
must stay silent — her persisted flag governs his voices.
Boundary against Wache's ST-05 (item 18 family): break flips
are Tamm's; endings beyond the open app (and the 03:30
stand-down's sentence) are Wache's. State: OPEN — named in all
three briefs; no letters yet at Edition 1.

**S-5 · UNA-03's birth order × the no-fire heartbeat — Imke ×
Wache.** DISCOVERED at this survey. The charge's crime scene is
RoutineHeartbeat:142 (get() with no engine) — and Wache's
heartbeat hunks (@@ -138,28 → +153,47) already cover :138–166,
the :142 call site's whole neighbourhood; his seal-transition
hunks reach the same tail Imke rewrites (A-2). Her cure may
partly ride his text — and her SoundEngineBirthOrder test binds
whichever get() the composed tree ships. **This desk has
written to Imke with the hunk regions (Edition 1)** so her lane
composes against his landing instead of discovering it at
merge.

**S-6 · UNA2-06's two halves — Suki × Pastille.** The charge
closes only when both halves speak (Freya §4.2): in-app
(Suki: the remaining printed on Monolith's faces, derived from
the stored end — plus the early-end verb made reachable) and
overlay (Pastille: WRITTEN — derivation + hand-back in the pill
lane; proves only on the phone bench, §4.5). Proof state:
HALF-PROVEN by construction until the bench speaks. B-4 is
this seam's textual face.

**S-7 · The peek values — Suki × Rurik.** SIL-2's printed price
and SIL-3's withheld reason (Suki's words) take their VALUES
from Rurik's side: the price is peekMinutes; the remaining
cooldown is the existing derivation (Repository :1073–1079).
After the car, no surface may hardcode the peek's 2 or the
cooldown's 20 (Freya §4.3) — Pruefer's grep companions police
it. State: OPEN (both lanes pre-declaration).

**S-8 · The Shield header — Bodo × Wache × Voss.** B-2's
textual face: Bodo's honest count (:173–177), Wache's F5
liveness line (lands at :176), and Voss's editor (the whole
screen, v10 base) meet in one header block. Compose order
recommended: Voss's transplant settled first by Ren, then
Bodo + Wache composed as one header edit. State: OPEN.

**S-9 · The migration spine — Rurik × Falk × Imke × Pruefer.**
Falk's column is declared BY HIS TREE (§2, verbatim SQL above)
— his letter to Rurik is still owed under his brief, but the
fact no longer waits on it: **this desk has relayed the column
+ Imke's store deviation to mail/rurik/ (Edition 1).** Imke's
declaration to Pruefer + Ren is owed (her store contradicts
his standard's assumption) — requested in this desk's letter
to her. Pruefer verifies each lane's chain in-lane and the
composed chain at merge; no in-lane pass substitutes.

**S-10 · ST-04's print × the Drawer — Rurik × Voss.**
TheExtensionIsPrinted (FT/ST-04) writes where the receipt and
Drawer figures are composed — ReceiptState.kt, DrawerScreen.kt,
DrawerDay.kt all differ in Voss's lane (his E-14(c)/RC-03 work
rides Car 2's shared completedTaskSummaryEntry formatter).
Rurik's print cure must compose against Voss's versions of
those files or be re-based at merge. State: OPEN — flagged
for Ren's merge sequencing; no letter (both briefs name it).

---

## 4 · REGISTRY — THE CAR, COUNTED

**30 countable items** (16 audit charges + 2 silences + 4
appendix sightings + 5 times tests + 3 small-things verbs),
plus 4 GUARDS (regression cover; never counted as cures) and 1
baseline note (SIL-1). States at Edition 1: no lane has
declared itself written (proof log); Pruefer countersigns all
PENDING. "WRITING" = tree presence at survey; "OPEN" = lane
exists, no hunks; "NOT OPEN" = no lane dir yet.

### truth-3-times — Rurik (6) · lane NOT OPEN
| Item | What it is | Proof (Pruefer) |
|---|---|---|
| FT-1 | Migration chain to 12; defaults land (peek 2 / break 10 / old rows' breakSeconds 600) | MigrationChainTo12 — A |
| FT-2 | Span derivations read the session's breakSeconds stamp; the three literal 600s die | SpanReadsTheStamp — B |
| FT-3 | startPeekLease reads peekMinutes; 0 = no grant, no row touched | LeaseReadsTheSetting — B |
| FT-4 | Mid-flight immovability: edits price the future only | MidFlightImmovability — A (weak alone; FT-2/3 carry) |
| FT-6 | Setter bounds clamp (−5, 90 → bounds on both numbers) | SetterBoundsClamp — A (same caveat) |
| ST-04 | Add time to a running block by HIS hand + the extension PRINTED on receipt/Drawer | AddTimeGrowsTheTargetByHisHand — B; TheExtensionIsPrinted — B |
(FT-5, the pill derivation table, is the pill lane's — cross-
referenced in the standard so no lane of this car claims it.)

### truth-3-shorts — Falk (1) · WRITING
| UNA-01 (S1) | Shorts + Reels made real: persisted column (IN TREE), sniper gate (IN TREE), rendered toggle (IN TREE — ParkDrawer) | ShortsSettingPersistsAcrossProcessDeath; SniperHonoursShortsSetting — B; ShortsToggleRenderedAndTrue — A |

### truth-3-sound — Imke (2) · WRITING (service side)
| UNA-03 (S1) | Sound Effects becomes a persisted fact (prefs store, §2 deviation) governing every engine birth | SoundSettingPersistsAcrossProcessDeath; SoundEngineBirthOrder — B; EveryEngineGetReadsTheSetting — B |
| UNA-02 (S2) | Social Sites' toggle renders again (PARK the audit's heir) and writes through | SocialToggleRenderedAndWritesThrough — A; SocialBoardWordFollowsTheToggle — B |

### truth-3-sweep — Bodo (6) · WRITING
| UNA-04 (S2) | "DATA: ON THIS PHONE ONLY" made true: allowBackup=false (IN TREE) + both backup rules files rewritten (IN TREE) | BackupExcludesTheRecord — B (config) |
| UNA-05 (S2) | "FIRED AS SET" — WORDS HALF only; machinery is item 18's (seam S-1) | RoutinePlateWordsAreTrue — B; HALF-PROVEN by construction |
| UNA-06 (S2) | "0 SITES" stays empty: the ifEmpty factory fallback dies (a11y hunk IN TREE) | EmptyWebsiteListStaysEmpty — B; GUARD FirstRunStillSeedsRealRows |
| UNA-07 (S3) | Loadout cap rests on the hand-applied loadout (LoadoutStore, IN TREE) not `remember { 0 }` | LoadoutCapRestsOnTheAppliedLoadout — B |
| UNA-08 (S3) | The plate says STUDY HOURS; the old string dies tree-wide | PlateSaysStudyHours — B (words + grep) |
| UNA-09 (S3) | "n ACTIVE" counts the scheduler's own set (shared `isLive`, IN TREE) | ActiveCountEqualsSchedulersCount — B |

### truth-3-intercept — Suki (13) · OPEN, no hunks yet
| UNA2-01 (S1) | Peek offered only where a lease can be honoured; unlisted package arms nothing | NoPeekOfferOnGuardDoor / OnSiteDoor / OnSniperDoor — B; PeekLeaseForUnlistedPackageArmsNothing — B; GUARD PeekOfferOnPerAppDoorStands |
| UNA2-02 (S2) | The count prints the honest SHUT/PEEK split, both tenses | ImprintCountsWhatItNames — B |
| UNA2-03 (S2) | The Deck's warrant carries a read instant and degrades with age (+ rider: idle ENDS cell is warrant-aware) | DeckWarrantDegradesWithAge — B; IdleEndsIsWarrantAware — B |
| UNA2-04 (S2) | The running strip never contradicts itself in one frame | RunningStripNeverContradictsItself — B |
| UNA2-05 (S3) | The spent face prints only the spent fact; the dead sentence dies | SpentFacePrintsOnlyTheSpentFact — B (words + grep) |
| UNA2-06 (S3) | The peek gains in-app furniture: countdown from the stored end + reachable early-end (IN-APP HALF; seam S-6) | PeekCountdownRendersInApp — A; PeekEarlyEndIsReachableAndReal — A/B; HALF-PROVEN by construction |
| UNA2-07 (S3) | No "ACTIVE INTENTION: " over a blank | IntentionLineAbsentWhenEmpty — B; GUARD IntentionLinePrintsWhenDeclared |
| SIL-2 | The peek's price prints before it is taken — from the setting, never hardcoded | PeekPricePrintedFromTheSetting — B (+ grep) |
| SIL-3 | A withheld peek prints its reason + remaining cooldown | WithheldPeekPrintsReasonAndRemaining — B |
| APP-1 | Desk clock's fabricated "Deep Focus Session" banner dies | ClockBannerNeverFabricates — B (+ grep) |
| APP-2 | The listener's invented capture body dies | CaptureBodyIsNeverInvented — B (+ grep) |
| APP-3 | The primer modal's "hardware-level" sentence dies (resurrection only via Una's fresh audit) | PrimerFalseSentenceIsDead — B (words) |
| APP-4 | onSimulateIntercept + its fake TikTok wiring removed | SimulateInterceptWiringRemoved — B (+ grep) |

### truth-3-breaks — Tamm (2) · lane NOT OPEN
| ST-02 | A safe break can be ended early (Deck chip → VM → flip) | EndBreakEarlyFlipsToNextBlock — A; GUARD BreakStillEndsByTimer |
| ST-03 | Both break boundaries knock, once each, from the existing vocabulary | BreakStartKnockFires + BreakEndKnockFires — B (call-level) |

### Not counted, on record
- **SIL-1** (the intercept never states remaining time) — a
  Held Door DESIGN baseline, carried by the finalist doors,
  not a Car 3 code item; no proof test exists for it in the
  standard. Named here so it is not dropped by silence.
- **4 GUARDS** (named in the tables above): a lane that breaks
  a guard fails on that ground even with its own tests green
  (Pruefer's standard, incl. the audits' acquittals A-01…A-14 /
  A-01…A-12 as regression cover).

**Count check:** 6 + 1 + 2 + 6 + 13 + 2 = **30**. Adjacent
builds in the same merge (not car items): warrant stamp fix
(WRITTEN), no-fire F1–F5 + ST-05 + ST-06 (WRITTEN), the pill
(WRITTEN), routine editing (OPEN, v10 base).

---

## 5 · MERGE ORDER — RECOMMENDATION TO REN (the ruling is his)

1. Countersign gate first: no lane enters the merge without
   Pruefer's PROVEN/HALF-PROVEN marks + Una's re-verification.
2. Compose the migration (§2) into trunk BEFORE any lane's
   data-layer hunks — every lane's local 11→12 then reduces to
   its non-migration text.
3. Adjacent written lanes in dependency order: warrant
   (Stempel) → nofire (Wache) → pill (Pastille) — the car's
   A-2/B-1/C-9 compositions assume their text is already the
   base text.
4. Car lanes: sweep (Bodo) and shorts (Falk) — their shared
   block is A-4, one sitting; sound (Imke) onto Wache's tail
   (A-2); times (Rurik) with the S-3 provider rewire ASSIGNED
   before his hunks land; intercept (Suki) after times (S-7's
   values exist); breaks (Tamm) last among the car (B-3 reads
   Rurik's stamp).
5. Routine-edit (Voss) transplants against the merged tree —
   never the reverse (S-8/S-10).
6. Composed-migration chain re-run + full suite + the phone
   bench owns what no suite can prove (UNA2-06's overlay half,
   the pill over another app).

## 6 · OPEN FACTS THIS DESK STILL OWES THE MAP

- Falk's MainActivity hunk region (C-7) — unmapped at Edition 1.
- Rurik's and Tamm's trees — no lanes exist to survey yet.
- Whether Imke's UNA-02 surface lands in ParkDrawer as the
  audit's heir or argued elsewhere — her tree will say.
- Wache's answer to Bodo (S-1) — the merge's UNA-05 gate.
Each closes at the next survey, on evidence, or stays named
here. Nothing in this car is lost, unread, or dropped without
appearing in this file.

— Konto, Keepers of Record, Oct 29, 2026 (Edition 1)
