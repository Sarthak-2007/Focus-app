# PLAN AUDIT — Blok (Critics, vs Blockit: the shipped-app truth checker)

Filed: Thu Oct 29, 2026, ~09:18 IST. Target: `plan/MASTER-PLAN.md`'s claims
ABOUT THE SHIPPED APP vs the tree at
`~/workspace/clone-inspect/Focus-app/app/src/main/java/com/example/`.
Read-only on the tree. No builds. Every charge carries plan line + code line.
Inbox `mail/blok/` was empty on arrival (no letters; nothing to move to `read/`).

**Headline:** the plan's specs are mostly honest about what is *not* built
(§§3, 5.9, 7 say so plainly — acquitted below). Its failures run the other
way: in the token/migration complex it describes a *past* app — an in-memory
token, a v7 database, a live destructive fallback — that the shipped tree
has already replaced. Handed to Sarthak whole, §0.1 + §5.3.8 + §9 B-15 tell
him his weekly break evaporates on process death. It does not. That is the
worst charge (BLOK-1, S1).

---

## Claim-by-claim table (the 8 assigned spot-checks + 2 found on the way)

| # | Plan claim (plan line) | Code (code line) | Verdict |
|---|---|---|---|
| 1 | DB/token baseline: token persistence is "build debt" (§0.1-9, MASTER-PLAN.md:152-153); VM holds "the bare in-memory `MutableStateFlow(1)`… today any process death silently refills the week's break" (:2047-2049); Migration 7→8 + fallback deletion still OPEN (§9 B-15, :5079-5083); permanence test blocked until fallback removal lands (§7.10, :3658-3663) | DB is **version 8** (`data/MonolithDatabase.kt:24`); `MIGRATION_7_8` exists and is registered (`MonolithDatabase.kt:41-52, 79`); `fallbackToDestructiveMigration` has **zero functional hits** in the tree — its only occurrence is the comment recording it is "GONE" (`MonolithDatabase.kt:59-64`); VM token is a `StateFlow` derived from the persisted ledger (`ui/MonolithViewModel.kt:150-151` → `data/MonolithRepository.kt:1202-1209` → `data/MonolithDao.kt:86-93`) | **CHARGE BLOK-1 (S1)** |
| 2 | Session entity: "the entity's penalty-blind `remainingSeconds`… [is] dead by this paragraph" (:1698-1700); acceptance grep: "the penalty-blind remaining getter appear[s] nowhere" (:2707-2708) | `effectiveTargetSeconds = targetDurationSeconds + penaltySecondsAdded` and `remainingSeconds = (effectiveTargetSeconds − elapsedSeconds).coerceAtLeast(0)` (`data/MonolithModels.kt:281-285`); the entity's own comment records the penalty-blind version as the *old* lie (`MonolithModels.kt:275-280`) | **CHARGE BLOK-2 (S2)** |
| 3 | Shield service session handling: §2.1 — during a Safe Break "the Shield notification already computes the honest string" (:190-192); §8.3 — the monitor loop "already wakes every 2s, already computes the enforced package set, already runs the foreground query — and today throws all of it away" (:4059-4063) | Notification prints exact `Safe Break %02d:%02d` (`service/MonolithShieldService.kt:89-94`); loop `delay(2000L)` (`MonolithShieldService.kt:148`), package set computed `:123-134`, foreground query `:136`, no publish of any of it. **But** its remaining-time is an inline duplicate, not the entity getter: `targetDurationSeconds + penaltySecondsAdded` recomputed at `MonolithShieldService.kt:101-102` | **AGREE** on both plan claims; the duplicate-formula fact feeds BLOK-7 (LR2 wording) |
| 4 | Routine alarms: "a routine is a WINDOW, not a firing… Nothing seals, cues demandingly, or starts counting at window-open" (:727-733; repeated :524-530); reseed "runs in `ensureSeededAndSyncPackages`… guarded by `routineReseedV1Done`" (:2090-2092) | START boundary **fires a session**: applies the loadout, `startSession(...)`, `startShield(...)` when no session is active (`service/RoutineAlarmReceiver.kt:38-52`); END disarms the routine's own session (`RoutineAlarmReceiver.kt:54-65`); alarms are `setAndAllowWhileIdle`, re-armed on every fire (`service/RoutineScheduler.kt:34-47`; `RoutineAlarmReceiver.kt:67`). `routineReseedV1Done`: **zero hits in the tree**. Shipped seeds are 09:00 "Morning Deep Work" Mon–Fri + 22:30 "Night Wind-Down" daily (`MonolithRepository.kt:122-143`), not the §3.4 trio (:700-702, which is labelled a rebuild reseed — that part is fairly filed as future) | **CHARGE BLOK-3 (S2)** — the definitional present tense only |
| 5 | Deck hero/FINISHES: "Hero and FINISHES both use the span formula, everywhere" — true span n × block + (n−1) × 10 (:229-240); build status: shipped deck build has "FINISHES from the total" (:593-595); acceptance: formula holds "in every state (idle, primed, sealed, mid-window arrival)" (:573-575) | Pre-arm: **exact** — `totalSpanMinutes = dialMinutes * studyBlocksCount + (studyBlocksCount - 1) * 10` (`ui/screens/TactileDeckScreen.kt:142`), hero renders it (:238-247), FINISHES = now + span (:159-161). Running: `finishPair` is still `now + totalSpanMinutes`, re-`remember`ed on `displaySeconds` (`TactileDeckScreen.kt:155-163`) — every tick pushes the printed finish a tick further away; it is neither start + span nor now + remaining | **CHARGE BLOK-4 (S2)** — pre-arm acquitted, sealed state charged |
| 6 | Slide commit mechanics: §2.6 specs 0.84-under-contact commit, release-commit "LOSES", tap mechanism merged (:388-447); §2.7 status: "Slide recompose round still queued (W4 mechanism + badge death)" (:598-599) | Shipped slide commits **only on release**: `ACTION_UP, ACTION_CANCEL` branch, `if (dragFraction >= 0.82f)` → seal sound + `onSlideCommitted` (`ui/views/BlockitCoreViews.kt:1174-1185`); gate haptic at 0.82 crossing (:1200-1205); badge still prints per-block minutes, not the total (`TactileDeckScreen.kt:498` vs hero :142) | **AGREE** — the plan never claims the W4 mechanism or tap-commit is built; its status line is honest. (The CANCEL-commits fact is LR2-S2-20's, still live in code, covered by the §2.6 spec — see LR2 flags.) |
| 7 | Digest clear path: "CLEAR ALL does not exist while sealed — repository-gated OFF" as Frozen Digest spec (:2267-2270); build status lists "the Frozen Digest (snapshot + repository-gated CLEAR ALL)" as **not yet built** (:2806-2810) | Ungated end to end, exactly as LR2-S1-1 filed: whole-table `DELETE FROM captured_notifications` (`MonolithDao.kt:112-113`) ← pass-through with no sealed check (`MonolithRepository.kt:1162-1164`) ← VM plays the latch sound *before* the delete and checks nothing (`MonolithViewModel.kt:495-500`) | **AGREE** — plan absorbed LR2-S1-1 correctly and labels it unbuilt. The app-side S1 stands against the code, not the plan |
| 8 | Task completion storage: §4.5 completion-events are future work riding "migration 9→10" (:1221-1234); §7.2 Drawer reads completed **rows**, "never the session's `completedTasksSummary` string" (:3332-3334); §7 status: "nothing in §7 is built yet" (:3954-3958) | Today completion is a status flip on the task row — `status = COMPLETED, completedAtMillis = nowWall, completedInSessionId = session?.id ?: 0` (`MonolithRepository.kt:1456-1461`) — **plus** the summary string appended to the session (`MonolithRepository.kt:1463-1476`); un-complete toggles back (`:1441-1450`); a live delete chain for completed rows exists: `MonolithDao.kt:137-138` ← `MonolithRepository.kt:1604-1606` ← `MonolithViewModel.kt:681` | **AGREE** — the plan's future-tense framing matches the tree; §7.10's grep list (:3645-3649) correctly names the chain that must reach zero hits |
| 9 | (§5.3.9, found) "The shipped body sentence ('Sessions are unbreakable by tap. You have N weekly emergency break token remaining. Hold the physical valve continuously for 60 seconds to abort.') is false twice over and is **deleted wherever it appears**" (:2129-2133) | That sentence has **zero hits** in the tree. The overlay already carries Freya's §5.3.9 sentences verbatim (`ui/components/AirlockAndDisarmOverlays.kt:622-627, 652`) and is already Ledger-Direct — valve composed only at `tokensRemaining > 0`, SPENT face at 0 (`AirlockAndDisarmOverlays.kt:580-582, 633-649`) — while §5.9 lists "the token's Ledger-Direct surface" as not yet built (:2808-2810) | **CHARGE BLOK-5 (S2)** |
| 10 | (§2.4, found) "Engine change, filed with the Deck build: the shipped engine coerces blocks to 1..4; cap 8 lands with the tetrads" (:299-301) | `totalStudyBlocks = studyBlocksCount.coerceIn(1, 8)` (`MonolithRepository.kt:986`); VM `blocks.coerceIn(1, 8)` (`MonolithViewModel.kt:401-403`); hub already wraps 8→1 (`TactileDeckScreen.kt:287-290`). The "1 to 4" the plan priced from survives only in a stale KDoc comment (`MonolithRepository.kt:1005`) | **CHARGE BLOK-6 (S2)** |

---

## Charges

### BLOK-1 — S1 — The plan's token/migration baseline describes an app that no longer exists
Plan: :152-153 ("adopted as build debt"), :2047-2049 ("today any process death
silently refills the week's break"), :5079-5083 (B-15 OPEN until 7→8 lands and
the fallback is deleted), :3658-3663 (§7's permanence test "cannot pass until
that lands"), :3357-3359 (same dependency repeated).
Code: `MonolithDatabase.kt:24` (version = 8), `:41-52` (MIGRATION_7_8),
`:79` (`.addMigrations(MIGRATION_7_8)`), `:59-64` (fallback recorded GONE);
`MonolithViewModel.kt:150-151`; `MonolithRepository.kt:1176-1178, 1202-1209,
1224-1249` (ledger-derived remaining + the one transactional write, matching
§5.3.8's own spec almost line for line); `MonolithWeek.kt:28-32, 41-42`;
`MonolithDao.kt:83-93`; entity `MonolithModels.kt:298-305`.
The plan also contradicts itself: §5.9's status (:2800-2804) says the banked
valve "is built and shipped… to TOKEN-SPEC's enforcement" and the truth fixes
are built — B-15 and §0.1-9 say the opposite. Only the 8→9 / 9→10 legs of
B-15 are genuinely open (DB is v8; no 8→9 exists). A reader who trusts the
plan will re-build what is built, and — worse — Sarthak will read that his
one weekly break is currently held in RAM. It is held in Room.

### BLOK-2 — S2 — "Penalty-blind remainingSeconds" is a dead lie filed as a live one
Plan :1698-1700, :2707-2708. Code `MonolithModels.kt:281-285`. The Now Frame
may still be worth building, but not on this evidence: the entity getter the
plan says must die already includes the penalty. §5.9 check 2's grep, run
today, "finds" the getter everywhere and would fail a truthful build.

### BLOK-3 — S2 — Routines: the plan's definitional sentence is the opposite of the shipped behaviour
Plan :727-733 and :524-530 state what a routine *is* — a window, nothing
seals at window-open. Code `RoutineAlarmReceiver.kt:38-52` seals at
window-open (loadout + session + shield), guarded only against steamrolling
a session he started himself; `:54-65` ends it. The rebuild may change this,
but the sentence is written as a definition, not a delta — and §2.5's
conflict sentence (:351-354) is priced on the window reading. The invented
guard name `routineReseedV1Done` (:2090-2092) has zero hits in the tree;
the actual reseed is a plain emptiness check (`MonolithRepository.kt:122`).

### BLOK-4 — S2 — Running FINISHES recedes forever; the plan claims the span formula "everywhere"
Plan :235-240, :573-575, :593-595. Code `TactileDeckScreen.kt:155-163`:
`targetDate = now + totalSpanMinutes`, re-remembered whenever
`displaySeconds` changes — i.e. every tick of the running session. The
sealed Deck's FINISHES is always "a full span from now." Pre-arm is exact
(`:142, 159-161`) and the old dial × blocks lie is genuinely dead — that
half is acquitted. The sealed half is not.

### BLOK-5 — S2 — §5.3.9 orders the deletion of a sentence that is already gone, and §5.9 un-builds a built surface
Plan :2129-2133, :2808-2810. Code: zero hits for the quoted sentence;
`AirlockAndDisarmOverlays.kt:622-627, 652` already prints Freya's replacement
sentences verbatim; `:633-649` already withholds the valve at 0. A builder
following the plan will hunt a corpse and may "rebuild" Ledger-Direct over
the working one.

### BLOK-6 — S2 — Block cap: plan priced from a stale comment, not the code
Plan :299-301. Code `MonolithRepository.kt:986` (1..8), `MonolithViewModel.kt:403`
(1..8), `TactileDeckScreen.kt:288` (wraps 8→1). The tetrads' "engine change"
is already landed; filing it as Deck-build work double-books it.

### BLOK-7 — S3 — FLOW: real trap, invented quote, overstated permanence
Plan :545-548 — "the readout says NO LIMIT… no exit short of killing the app."
`NO LIMIT`: zero hits in the tree. The hero prints `FLOW`
(`TactileDeckScreen.kt:237`), the finish line `OPEN FLOW • 20M FLOOR` (:157),
the badge `FLOW` (:498). The divergence underneath is real and acquitted:
Deck stopwatch = `preset == STOPWATCH_FLOW || dialMinutes < 10` (:125) vs
repository `durationMinutes < 10` only (`MonolithRepository.kt:957`), and the
FLOW key is the only UI caller of `selectStudyPreset` (`TactileDeckScreen.kt:311`;
`MonolithViewModel.kt:394-399` sets dial 5 / blocks 1 — from defaults of 50 / **2**,
`MonolithViewModel.kt:89, 92`, not the plan's "50→5 and blocks 3→1"). But the
preset is an in-memory `StateFlow` (`MonolithViewModel.kt:95`) — nothing is
persisted, and raising the dial arms an honest timed session underneath the
FLOW label. Label-deep trap, session-shallow; the plan's wording makes it
sound disk-deep.

### BLOK-8 — S3 — §8.3: "this codebase leaves every hook empty" — half the hooks are already wired
Plan :4050-4058. Accessibility `onServiceConnected` / `onUnbind` / `onDestroy`
set the instance and poke the Live Wire (`service/MonolithAccessibilityService.kt:55-67,
300-307`); a process-wide Live Wire already exists and is already read for a
warrant (`service/LiveWire.kt:65`; `MonolithRepository.kt:578-596`). True
halves: the notification listener has no connect/disconnect overrides at all
(`service/MonolithNotificationListener.kt:23-69`) and the device-admin
receiver has no `onEnabled`/`onDisabled` (`service/MonolithDeviceAdminReceiver.kt:12-16`).
The Heartbeat Tap is an extension of a shipped mechanism, not a greenfield —
§8.3's costing (≈230 lines, :4095-4097) is priced on the empty-hooks claim.

---

## LR2 flags — what the plan has absorbed, contradicted, or dropped

- **LR2-S1-1 (digest CLEAR ALL, ungated) — ABSORBED, honestly.** Plan §5.5
  (:2267-2270) specs the repository gate; §5.9 status (:2806-2810) lists it
  not-yet-built. Code unchanged (Dao :112-113 ← Repo :1162-1164 ← VM :495-500).
  No charge against the plan.
- **LR2-S1-2 (seeded fabricated history — 3 sessions, 150 min, + intercept
  logs) — NOT ABSORBED. Flag.** The seed block is live
  (`MonolithRepository.kt:271-355`: sessions at :278-334, intercept logs
  :336-355, seeded captures from :357) and no section of MASTER-PLAN and no
  §9 charge orders its deletion. Only the seeded demo *task* is carried
  (B-30, :5110-5115; drill note :3671-3674). §7's Drawer will inherit these
  rows exactly as LR2 warned ("The Drawer build must not inherit a single
  one of these rows") and the plan is silent. Plan-level S2 omission riding
  an app-level S1.
- **LR2-S1-16 (seal sounds before the pre-flight gate, then again) —
  PARTIALLY absorbed.** §2.6's pre-flight paragraph (:502-506) states the
  law ("the seal sounds once, only when the session row exists"), but no §9
  charge carries it, and the code still does it: slide seals at
  `BlockitCoreViews.kt:1184`, then `onSlideCommitted` →
  `MainActivity.kt:330` → `MonolithViewModel.kt:896-903` (gate) /
  `:911` (second seal). S3 flag.
- **LR2-S2-20 (slide commits on ACTION_CANCEL) — spec-absorbed, code-live.**
  §2.6's under-contact commit supersedes it; plan never claims the fix is
  built. Code still commits on CANCEL at ≥0.82 (`BlockitCoreViews.kt:1174-1177`).
  No charge against the plan.
- **LR2 acquittal A-i (:276 — "Shield's remaining-time derivation is the
  entity getter applied") — CONTRADICTED by the code, and LR2 contradicts
  itself.** Shield recomputes inline (`MonolithShieldService.kt:101-102`);
  LR2's own §5 summary (:235) calls it "ShieldService :94-95's duplicated
  formula." Numerically identical today, so no user-facing lie — but the
  register's acquittal wording is false, and MASTER-PLAN §5.2's Now Frame
  case silently inherits the confusion (see BLOK-2). S3 flag on the register.
- **LR2-S3-3 family (repository enforces a different effective target than
  the getter prints, one-tick skew) — overtaken by code.** Repository now
  derives `effectiveTarget = targetDurationSeconds + penaltyAdd`
  (`MonolithRepository.kt:1011-1012`), the same formula as the entity
  (`MonolithModels.kt:281-282`). No plan claim found repeating the skew.

---

## Acquittals

1. **Pre-arm span arithmetic** — plan :229-240 vs `TactileDeckScreen.kt:142,
   159-161` and engine `MonolithRepository.kt:1083-1095` (Safe Break entered
   only when `currentBlockIndex < totalStudyBlocks`, 600s at :95/:1088):
   the formula, the between-blocks-only law, and the death of the old
   dial × blocks product all check out.
2. **AOD fabricated constant** — plan :2800-2804 ("the `0.65f` fabrication
   is dead in code"): the AOD progress path now derives from
   `remainingSeconds / effectiveTargetSeconds`
   (`AirlockAndDisarmOverlays.kt:108-120`); the only `0.65f` left in the tree
   is a scrim alpha (`AirlockAndDisarmOverlays.kt:224`), not a progress value.
3. **Shield monitor facts** — plan :4059-4063 (2s loop, package set,
   foreground query, all discarded): exact (`MonolithShieldService.kt:123-148`).
4. **§3, §5.9, §7 build-status honesty** — "Nothing in §3 is built yet"
   (:996-1000), §5.9's not-yet-built list (:2805-2810, excepting the
   Ledger-Direct item charged in BLOK-5), "nothing in §7 is built yet"
   (:3954-3958): the plan does not claim its specs are shipped. Its lies
   are about the *old* app, not about its own new work.
5. **Token spec vs token code (the spec itself)** — §5.3.8's ledger shape,
   DAO names, formula, Monday anchor, completion-instant booking, and
   attempts-write-nothing all match the shipped implementation
   (`MonolithDao.kt:83-93`; `MonolithRepository.kt:1176-1249`;
   `MonolithWeek.kt:28-42`; `MonolithModels.kt:298-305`). The spec is right;
   only its tense is wrong (BLOK-1).
6. **§7.10's permanence grep list** — correctly identifies the live
   `clearCompletedTasks` chain as a present danger to the Drawer
   (`MonolithDao.kt:137-138` ← `MonolithRepository.kt:1604-1606` ←
   `MonolithViewModel.kt:681`); it does not pretend the chain is gone.

**Totals: 8 charges (1 S1 · 5 S2 · 2 S3) · 3 LR2 flags (1 not-absorbed S1,
1 partial, 1 register self-contradiction) · 6 acquittals.**

— Blok, Critics (vs Blockit)
