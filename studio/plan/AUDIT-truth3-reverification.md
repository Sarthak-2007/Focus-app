# AUDIT — TRUTH CAR 3 RE-VERIFICATION (Una, Critics — consistency)

**Commission:** Truth Car 3's verification seat (Ren, Oct 29, 22:29 IST).
**Method:** pass 1 = every charge re-derived from trunk as it stands
(batch-8 state, DB v11, HB-2 fix in MainActivity) — not carried over
from the batch-7 audits' line numbers. Pass 2 = per-lane diff rulings
as the six lanes land. A REPAINTED ruling stops that lane's merge
until answered. Disputes go to Ren with both evidences.

**Lane map:** truth-3-times (Rurik) · truth-3-shorts (Falk) ·
truth-3-sound (Imke) · truth-3-sweep (Bodo) · truth-3-intercept
(Suki) · truth-3-breaks (Tamm). Verification briefs sent to all six
inboxes at 22:37 IST (mail/<name>/2237__from-una__truth3-
verification-brief.md).

---

## PASS 1 — RE-DERIVATION (Oct 29, ~22:30 IST)

**Result: every charge stands in batch-8 trunk. None false, none
already cured.** Truth Car 2's merge (minutes die, HB-2, Drawer and
receipt cures) touched none of these surfaces. Line numbers below
are the CURRENT trunk cites; the original audits' numbers are
superseded where they differ.

### PARK charges (AUDIT-park-claims.md)

- **UNA-01 (S1) — stands, all three layers.** Toggle:
  `toggleShortsReelsSniper` flips memory only
  (ui/MonolithViewModel.kt:1304–1307; flow :165–166; loadout
  forces true :1915). No shorts field on `GuardSettingsEntity`
  (data/MonolithModels.kt:705–732). Board word rides the
  @Volatile `WatchStore.shortsSettingOn` mirror
  (service/WatchStore.kt:162, :261–266, :415–417). Sniper fires
  with no enabled check (service/MonolithAccessibilityService.kt,
  block 3 ~:165–178). Toggle rendered nowhere
  (ShieldMatrixScreen.kt:71, :84; TactileDeckScreen.kt:112 —
  parameters only).
- **UNA-02 (S2) — stands.** Chain honest (row
  MonolithModels.kt:707; VM init :658; toggle writes Room
  :1309–1314; service honours the stored value). Switch rendered
  nowhere (ShieldMatrixScreen.kt:72, :85; TactileDeckScreen.kt:113
  — parameters only); only the loadout can force it (VM:1916).
- **UNA-03 (S1) — stands, both halves.** `toggleSound` flips the
  engine var only (ui/MonolithViewModel.kt:1271–1277; engine
  default true, service/TactileSoundEngine.kt:254); no persisted
  sound write exists in the tree. First-caller-wins singleton
  (service/SessionSealTransition.kt:285–303); heartbeat binds
  engineless (service/RoutineHeartbeat.kt:142); VM binds with its
  engine (ui/MonolithViewModel.kt:575). PARK prints at
  ui/components/ParkDrawer.kt:432.
- **UNA-04 (S2) — stands.** `allowBackup="true"`
  (app/src/main/AndroidManifest.xml:25); backup_rules.xml still
  the commented Studio sample; data_extraction_rules.xml
  cloud-backup section empty. Plate at ParkDrawer.kt:110.
- **UNA-05 (S2) — stands.** Sentence at ParkDrawer.kt:109; chain
  alarm inexact (`setAndAllowWhileIdle`,
  service/RoutineScheduler.kt:44; no exact-alarm surface in
  service/). Machinery cure is Wache's F1–F5 (parallel lane);
  the words cure is compositional — see Bodo's brief.
- **UNA-06 (S2) — stands.** `userDomains.ifEmpty {
  BlockedBrowserDomains.defaultDomains }`
  (service/MonolithAccessibilityService.kt:184); editor's
  contract printed at ShieldMatrixScreen.kt:308–322.
- **UNA-07 (S3) — stands.** Cap rests on `remember { 0 }`
  (ShieldMatrixScreen.kt:103, bound :155–158) while the screen
  receives `selectedProfileName` (~:74; VM state
  ui/MonolithViewModel.kt:301) and never seats from it.
- **UNA-08 (S3) — stands.** "STUDY RECORD: IN YPT" at
  ParkDrawer.kt:111; the app's own stored record is real
  (DrawerDay / day_notes). True sentence: STUDY HOURS.
- **UNA-09 (S3) — stands.** Header counts `count { it.enabled }`
  (ShieldMatrixScreen.kt, ROUTINES header ~:173–177); scheduler
  refuses mask 0 (data/RoutineDerive.kt:277;
  data/MonolithModels.kt:577, :591, :616, :657).

### Deck / intercept charges (AUDIT-deck-intercept-claims.md)

- **UNA2-01 (S1) — stands.** `canPeek` at
  ui/MonolithViewModel.kt:2128 (in `triggerAirlockIntercept`,
  :2118) — tier alone, null defaults ON. Four doors in
  MonolithAccessibilityService.kt: tamper ~:155–163, sniper
  ~:165–178 (no lease exception), site :179–197 (no lease
  consulted), per-app :201–219 (the only lease read, ~:207–213).
  `startPeekLease` (data/MonolithRepository.kt:1131–1140) writes
  by package; unlisted packages match no row.
- **UNA2-02 (S2) — stands.** `imprintTenantText`
  (ui/components/MonolithCustomComponents.kt:415–431) over
  `appsCellCount = count { resolvedTier != OPEN }`
  (ui/MonolithViewModel.kt:449–451); frozen identically
  (ui/screens/TactileDeckScreen.kt:460–467, :480–485).
- **UNA2-03 (S2) — stands.** `Warrant` has five fields, no read
  instant (service/WatchStore.kt:62–68). Event-driven refresh
  only: ui/MonolithViewModel.kt:468–469, called :676;
  MainActivity.kt:326 (ON_RESUME), :536 (RETRY). Deck's 60 s
  ticker (TactileDeckScreen.kt:354) never touches the warrant.
  Seam noted to Suki: Stempel's warrant lane owns the stamp
  mechanism in parallel.
- **UNA2-04 (S2) — stands.** Frozen at TactileDeckScreen.kt:
  425–430 (state), :458 (BLOCK), :482 (tenant) vs live seam
  :479 and sentence ~:990.
- **UNA2-05 (S3) — stands.** AirlockAndDisarmOverlays.kt:633.
- **UNA2-06 (S3) — stands in trunk.** `FloatingPeekPillState`
  (VM:110–118), `start2MinutePeek` (:2154), `end2MinutePeekEarly`
  (:2178); tree-wide grep: zero render sites for
  `floatingPeekPill` outside the ViewModel. Two halves per Freya
  §4.2: in-app half = Suki; overlay half = Pastille's pill lane
  + phone bench. Closure must name both halves.
- **UNA2-07 (S3) — stands.** Unconditional print at
  AirlockAndDisarmOverlays.kt:549.
- **SIL-2 / SIL-3 — stand.** Cooldown constant
  (MonolithRepository.kt:95) named on no surface; withheld peek
  silent though `peekCooldownRemainingSeconds` is carried
  (VM:126, fed :2141). Values post-cure come from Rurik's store
  (Freya §4.3) — no surface may hardcode 2 or 20.
- **Appendix — all four sightings stand.** Desk-clock banner
  default (AirlockAndDisarmOverlays.kt:197); listener's invented
  body (service/MonolithNotificationListener.kt:82); primer
  modal's dead sentences (:437, :444; binding removed,
  MainActivity.kt:764); `onSimulateIntercept` fake wire
  (TactileDeckScreen.kt:134; MainActivity.kt:503–507).

### Times + break verbs (PILL-AND-TIMES §§3–4; SMALL-THINGS ST-02/03/04)

- **Editable times — absent as specced.** No peekMinutes /
  breakMinutes columns; no breakSeconds on `FocusSessionEntity`
  (data/MonolithModels.kt:268…). Peek lease end from the
  constant (MonolithRepository.kt:1131–1140). Break's four
  sources alive: Repository:97 constant; span literals
  (MonolithModels.kt:391, :395, :398); entry stamp of the
  constant (Repository:1844–1845). `StudyModePreset.breakMinutes`
  (Models:64): one occurrence tree-wide — no reader. Migration
  chain ends at 10→11 (data/MonolithDatabase.kt:160, :212).
  Joint migration law (Freya §4.1): ONE 11→12 carrying five
  columns (peekMinutes, breakMinutes, breakSeconds, UNA-01
  shorts flag, UNA-03 sound flag).
- **ST-02 — stands.** No end/skip/resume verb for the safe break
  anywhere in ui/, data/, service/ (grep empty). Expiry-only
  exit in the Repository accrual span (~:1802–1850).
- **ST-03 — stands.** No sound call observes either break
  boundary; engine voices are seal / finish / detents only.
- **ST-04 — stands.** The running block's target grows by one
  writer only (the tamper penalty). Rurik's slice, with the
  honesty condition: the extension must print on receipt +
  Drawer, or the cure is a repaint.

---

## PASS 2 — PER-LANE RULINGS

*(Appended as each lane lands. Nothing ruled yet — no lane has
landed at filing time.)*

— Una, Critics (consistency), Oct 29, 2026.
