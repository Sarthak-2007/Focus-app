# LIE REGISTER 3 — FALK (Critics seat 21, provenance)

Ground: the merged trunk as a whole — `Focus-app/app/src/main/java/com/example/` — after Ren's hand merge (token ledger, counterfoil tear, Live Wire substrate, desk-clock span fix) plus the re-applied AOD revert. Charges file only with file+line evidence quoted from the code. No code changed.

---

## CHARGES

### F-1 (S1) — Database v8 has exactly one migration, and it covers exactly one table; three tables and two dropped columns have no DDL anywhere in the tree

`data/MonolithDatabase.kt:24` — `version = 8,` with `exportSchema = false` at :25 (no schema JSON exists to audit against).

The only migration registered, `data/MonolithDatabase.kt:79`: `.addMigrations(MIGRATION_7_8)`, and that migration's entire DDL, :43-50, is one `CREATE TABLE IF NOT EXISTS emergency_break_spends …`. Nothing else in the tree creates or alters any table (tree-wide grep: `Migration(` occurs only at MonolithDatabase.kt:41).

But the v8 entity set includes three whole tables that migration never creates — `data/MonolithModels.kt:413-416` `@Entity(tableName = "blocked_sites") data class BlockedSiteEntity`, :420-428 `@Entity(tableName = "routines") data class RoutineEntity`, :504-508 `@Entity(tableName = "guard_settings") data class GuardSettingsEntity` — and two columns present in the pre-merge tree are simply gone from the entities with no recreate-table DDL: `FocusProfileEntity` and `FocusSessionEntity` no longer carry `frictionTier` (current Models.kt:233-246 and :249-273; the column exists in the git-HEAD versions of both). The fallback that used to absorb all of this was deleted in the same merge (see A-1) — correctly, per TOKEN-SPEC §6.2 — which means any installed database whose schema differs from v8 on any of those tables/columns now meets a loud `IllegalStateException` on open instead of a migration. That is the ruling working as designed; what is missing is the migrations it demands.

The one test that claims to cover this cannot see it. `app/src/test/java/com/example/TokenMigrationTest.kt:36-52` builds its "v7-shaped database" as a single hand-made table — `CREATE TABLE IF NOT EXISTS focus_sessions (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, intention TEXT NOT NULL)` — and its `onUpgrade` (:72-76) calls `MonolithDatabase.MIGRATION_7_8.migrate(db)` directly through a raw `SupportSQLiteOpenHelper.Callback`, never opening `MonolithDatabase` itself against a real v7 file, so Room's TableInfo/identity validation over all ten entities never runs. It proves the spends table can be created. It proves nothing about a real v7 → v8 upgrade.

### F-2 (S1) — The Live Wire verdict is merged, documented as consumed, and consumed by nothing; the arm path still reads the Settings string the substrate was built to replace

`data/MonolithRepository.kt:578` defines `fun liveWarrant(): Warrant {`. Tree-wide grep for `liveWarrant` returns that definition and nothing else — zero callers. Its KDoc, :566-570, claims the opposite:

> "THE LIVE WIRE verdict (Ines): one synchronous read of the living services … The slide reads it at DOWN and again at the gate crossing; the verdict is never older than the gesture."

No slide reads it. The actual arm gate, `ui/MonolithViewModel.kt:896-902`:

```kotlin
fun requestArmWithPreFlightCheck() {
    if (!repository.isAccessibilityEnabled()) {
```

— a Settings-string read (`MonolithRepository.kt:606-616`, `Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES … contains(context.packageName)`), i.e. precisely the inference `service/LiveWire.kt:9-14` says readiness is not: "readiness is not polled, inferred from a Settings string, or waited on".

Same disease in the snapshot flow. `service/LiveWire.kt:55-58` KDoc: "The strip and the chrome Guard line render this" — of `LiveWire.snapshots` (:91-92). Tree-wide grep: `snapshots` is produced at LiveWire.kt:91-96 and collected by no file. What IS wired is the residency note (`MainActivity.kt:150-154`) and the service pokes — the substrate ticks, faithfully, into a flow nobody reads, while the Guard line renders the Boolean readiness count instead (F-6/F-14).

### F-3 (S1) — The fabricated seed history still lives in the merged trunk: three invented sessions, forged artifacts, invented intercept logs and notifications

`data/MonolithRepository.kt:278-295` (first of three, all inside `if (existingSessions.isEmpty())` at :272):

```kotlin
dao.insertSession(
    FocusSessionEntity(
        intention = "Organic Chemistry Problem Set",
        …
        elapsedSeconds = 50 * 60,
        …
        completedSuccessfully = true,
        phantomReachesBlocked = 4,
        forgedArtifactId = "artifact_lissajous",
        completedTasksSummary = "Mechanism synthesis drills (25m / 24m) • Stereochemistry review cards (25m / 26m)",
```

Followed by "Algorithms & Data Structures Lab" (75 min, `forgedArtifactId = "artifact_gyro"`, :297-315) and "Read Constitutional Law Ch. 6" (25 min, `"artifact_loom"`, :316-334) — 150 minutes of history he never ran — plus two fabricated intercept logs (:336-355, TikTok "Stayed Focused", YouTube Shorts "Shorts Blocked") and two fabricated captured notifications (:357-374, WhatsApp "Study Group • Physics 202", Discord "CS Study Server"). This is LR2-S1-2, the seed Blok charged the plan for never ordering deleted and Ruth closed by scrub in the Drawer lane at 09:38 ("the three fabricated demo sessions … are deleted from the seed block"). The scrub did not survive — or never entered — this merge. A fresh install's record still opens on fiction.

Compounding detail: the seed rows carry the default `hmacSignature: String = ""` (`data/MonolithModels.kt:260`), yet the Ledger renders every receipt as verified — `ui/screens/CurioVaultScreen.kt:264`: `text = "HMAC•${session.hmacSignature.take(8).uppercase()}"` — which for these rows prints a bare `HMAC•` badge over a signature that does not exist.

### F-4 (S2) — The Safe Break still drains one second per CALL in the trunk, and two tickers call it: a 600-second break drains in ~400 wall seconds

`data/MonolithRepository.kt:1052-1053`:

```kotlin
if (session.isInSafeBreak) {
    val nextBreakRemaining = (session.safeBreakSecondsRemaining - 1).coerceAtLeast(0)
```

`updateSessionProgress` has two live callers on different clocks: `ui/MonolithViewModel.kt:342` inside a `delay(1000L)` loop (:364), and `service/MonolithShieldService.kt:81` inside a `delay(2000L)` loop (:148). While both run, the break decrements ~1.5×/wall-second. There is no wall-derived break function in the trunk — tree-wide grep for `breakRemainingAfter` returns zero hits. Ruth's wall post (09:38) records this exact disease as fixed ("Now wall-derived from the persisted (remaining, lastTick) pair via breakRemainingAfter()") — in her lane. In the merged trunk the tick-count drain is intact, and the span derivations of F-15 consume the corrupted `safeBreakSecondsRemaining` it produces.

### F-5 (S2) — A token COUNT reaches the UI on the Deck, in numerals, on the very chip that opens the break

`ui/screens/TactileDeckScreen.kt:520-522`:

```kotlin
BlockitActionChip(
    wordLabel = "EMERGENCY BREAK VALVE (60S HOLD)",
    monoBadge = "$weeklyEmergencyTokensRemaining/1 WEEK",
```

The rest of the merged system was built so the count never lands as a numeral: the valve receives none — `ui/views/BlockitSuitcaseAndSwitchesViews.kt:504-506`: "Ledger-Direct (Freya, invention 1 …): the valve receives NO token count" — and the Disarm overlay words it instead of numbering it (`ui/components/AirlockAndDisarmOverlays.kt:627-631`: "It is the only break this week." / "This week's break is already spent."). The flow feeding the badge is the honest ledger flow (`MainActivity.kt:195`, passed at :344) — the number is true. It is also exactly the surface the Ledger-Direct design kept number-free, resurrected one screen earlier in the same tap path.

### F-6 (S2) — The old Boolean `hasUsageStatsPermission` semantics survive intact and drive the Guard UI; UNSET is still collapsed into DENIED on every surface that renders it

The tri-state exists and is correct at the source (see A-10). But the merged status surface never carries it. `data/MonolithRepository.kt:42-43`: `data class SystemGuardStatus( val hasUsageStatsPermission: Boolean = false,` — populated at :702 `hasUsageStatsPermission = hasUsageStatsPermission(),` where :564 defines `fun hasUsageStatsPermission(): Boolean = usageAccess() == UsageAccess.ALLOWED` — UNSET and DENIED both become `false`. Consumers render that Boolean as the fact:

- `ui/screens/SystemGuardScreen.kt:245`: `view.isChecked = status.hasUsageStatsPermission` — the USAGE ACCESS switch shows a never-asked fresh device identically to a positively denied one.
- `MainActivity.kt:219-224`: the readiness count folds the same Boolean in as one of four equal legs.
- `ui/MonolithViewModel.kt:241-243`: the initial state hard-codes `hasUsageStatsPermission = false`.

`service/LiveWire.kt:41-43` states the charge against its own tree: "AppOps for GET_USAGE_STATS is three-valued in truth; the shipped Boolean collapsed UNSET into DENIED, which lied on fresh devices." Past tense in the comment; present tense in the Guard screen. (The two gating callers, Repository :483 and :516, need only ALLOWED and are acquitted under A-10.)

### F-7 (S2) — "One derivation of session time" is claimed in the models and violated by two of the three surfaces; the Shield service re-derives remaining inline, and Deck and desk clock run opposite-moving bars

`data/MonolithModels.kt:275-280`:

> "ONE derivation of session time (the Now Frame fix, Nadia x Freya): every surface - Deck, desk clock, airlock, the enforcing service - reads these and only these, so the app can only ever tell one time."

The enforcing service does not read them. `service/MonolithShieldService.kt:100-102`:

```kotlin
val effectiveTarget = updated.targetDurationSeconds + updated.penaltySecondsAdded
val remainingSec = (effectiveTarget - updated.elapsedSeconds).coerceAtLeast(0)
```

— a second, inline copy of `effectiveTargetSeconds`/`remainingSeconds` (Models.kt:281-285), and `MonolithRepository.kt:1012` carries a third (`val effectiveTarget = session.targetDurationSeconds + penaltyAdd`).

Worse, the two progress bars derive from different quantities in opposite directions. Desk clock, `AirlockAndDisarmOverlays.kt:123-124`: `(activeSession.spanElapsedSeconds.toFloat() / activeSession.spanTargetSeconds…)` — fills 0→1 across the whole span. Deck, `TactileDeckScreen.kt:148-149`: `(activeSession.remainingSeconds.toFloat() / target.toFloat()).coerceIn(0.03f, 1f)` — per-block remaining, draining 1→0. During a Safe Break `remainingSeconds` is 0 for the whole break (Models.kt:290-293 says so itself), so the Deck bar sits pinned at its 0.03 floor for ten minutes while the desk clock's bar keeps counting. One session, two bars, contradictory stories — the exact lie the Models KDoc says "dies here" (:280).

### F-8 (S2) — The break duration is law three times in three forms: the named constant, three bare 600s in the same package, and a bare 10 in the Deck

Named law: `data/MonolithRepository.kt:95`: `const val SAFE_BREAK_DURATION_SECONDS = 10 * 60 // 10-Minute Safe Break`.

The span math — in the same package, `com.example.data`, where the constant was directly reachable — hard-codes it three times, `data/MonolithModels.kt:296-305`:

```kotlin
val spanTargetSeconds: Int
    get() = totalStudyBlocks * targetDurationSeconds + penaltySecondsAdded +
        (totalStudyBlocks - 1).coerceAtLeast(0) * 600
…
        val cycle = targetDurationSeconds + 600
        …
                (600 - safeBreakSecondsRemaining)
```

Third form, `ui/screens/TactileDeckScreen.kt:142`: `val totalSpanMinutes = dialMinutes * studyBlocksCount + (studyBlocksCount - 1) * 10`. Change the constant in the Repository and the desk clock's span target, span elapsed, and the Deck's finish-time pricing all silently keep the old break. (Week anchor, by contrast, is single-sited — A-3.)

### F-9 (S3) — Fabricated/literal progress survives outside the desk clock: the Deck's idle dial invents a fraction, and both stopwatch bars divide by a hard-coded 1200f

The AOD constant itself is dead (A-2). Its family is not.

- Idle Deck, `ui/screens/TactileDeckScreen.kt:151-152`: `((dialMinutes - 5).toFloat() / 175f).coerceIn(0.04f, 1f)` — a progress fraction for a session that does not exist, from a denominator (175) that corresponds to nothing in the system.
- Stopwatch, twice: `TactileDeckScreen.kt:146` `(activeSession.elapsedSeconds.toFloat() / 1200f)` and `AirlockAndDisarmOverlays.kt:125-126` `(activeSession.elapsedSeconds.toFloat() / 1200f).coerceIn(0.04f, 1f)`. A named constant for exactly this value exists and is used elsewhere — `MonolithRepository.kt:94` `STOPWATCH_MIN_FLOOR_SECONDS = 20 * 60`, consumed properly at `MonolithViewModel.kt:926`. Both bars instead hard-code it, and the desk clock's own KDoc (`AirlockAndDisarmOverlays.kt:113-116`) claims "Every branch below is a real derivation from the session, or honestly empty" — a stopwatch has no target; dividing elapsed by the 20-minute floor makes the bar read full at 20:00 and stay clamped for the rest of an open-ended session. That is a made-up progress with a plausible denominator.

### F-10 (S3) — The killed AES-GCM export is dead as code and alive as commentary in the Ledger screen

`ui/screens/CurioVaultScreen.kt:52`: "- Verified Session Receipts ledger & AES-256-GCM encrypted backup export." And :208: `// SECTION 4: VERIFIED SESSION RECEIPT LEDGER & AES-GCM BACKUP`. Under that header sits receipts only — no export control, no export call, nothing (tree-wide grep for export/backup code returns only these two comments and the kill note). The code itself is honestly dead (A-5): `data/CryptoIntegrityManager.kt:17-19` records "(The AES-GCM backup export that used to live here was killed Oct 29, 2026: its payload had no egress and no restore - theatre, not a backup.)" The Curio comments were never told. A reader of the Ledger screen's own docs believes the app exports an AES-GCM backup. It does not.

### F-11 (S3) — Comment-vs-code in the AOD file: both hero numerals are documented as Space Mono at sizes the code does not use

`ui/components/AirlockAndDisarmOverlays.kt:159`: `// Hero architectural Space Mono numerals (112sp)` — the Text beneath it, :162-164, is `fontFamily = AnonymousProFamily, … fontSize = 96.sp`. Again at :537: `// Hero Architectural Countdown Numeral (Space Mono)` over `fontFamily = AnonymousProFamily` at :540. The font swap (Space Mono → Anonymous Pro, the user's telemetry face) landed in the revamp; the comments still describe the dead font at a dead size. Small, and exactly the register this hunt exists for: the next agent who trusts the comment ships the wrong face.

### F-12 (S3) — Repository KDoc caps study cycles at "1 to 4 blocks"; the code allows 8

`data/MonolithRepository.kt:1005`: "* 3. Multi-Block Study Cycles (1 to 4 blocks) with automatic 10-minute Safe Breaks in between". The code it documents: :986 `totalStudyBlocks = studyBlocksCount.coerceIn(1, 8),` and the setter agrees, `ui/MonolithViewModel.kt:403`: `_studyBlocksCount.value = blocks.coerceIn(1, 8)`. (This is the stale comment Blok caught the plan pricing from; it survives in the trunk, still priced wrong.)

### F-13 (S3) — Dead imports left by the merge in two of the four audited files

`ui/components/AirlockAndDisarmOverlays.kt` — each name occurs only on its import line in the file: :13 `import androidx.compose.foundation.layout.Spacer`, :52 `import com.example.ui.theme.NightBlockBarSurface`, :55 `import com.example.ui.theme.NightColorPrimaryContainer`. `data/MonolithRepository.kt:33-34`: `import org.json.JSONArray` / `import org.json.JSONObject` — zero uses in the file; the JSON-shaped work they once served is gone, the imports are its tombstone. (The `getValue`/`setValue` imports in MainActivity.kt and AirlockAndDisarmOverlays.kt are NOT charged: both files use `by` delegates throughout, e.g. MainActivity.kt:144, AirlockAndDisarmOverlays.kt:492-494.)

### F-14 (S3) — The readiness count is implemented twice; one copy is dead code

`data/MonolithRepository.kt:54-60` defines `SystemGuardStatus.readinessCount` (the four-Boolean count over accessibility / usage / notification / device-admin). Tree-wide grep finds no consumer of `.readinessCount`. `MainActivity.kt:219-224` recomputes the identical list locally as `guardReadinessCount` and passes that one down (:257, rendered at `MonolithCustomComponents.kt:282` as `"$guardReadinessCount/4"`). Same law, two forms, one of them unreachable — the next editor can fix the dead one and change nothing, or fix the live one and leave the corpse claiming otherwise.

### F-15 (S3) — Span arithmetic seam: penalty is counted once in the span target but excluded from the span cycle, so after a penalised first block the desk clock overstates remaining time by the full penalty

`data/MonolithModels.kt:296-298`: span target = `totalStudyBlocks * targetDurationSeconds + penaltySecondsAdded + (n-1) * 600` — penalty counted once. :300-308: span elapsed prices each completed cycle as `val cycle = targetDurationSeconds + 600` — penalty excluded — while the per-block `elapsedSeconds` it accumulates is capped at the penalty-inclusive target (`MonolithRepository.kt:1103`: `elapsedSeconds = verifiedElapsed.coerceAtMost(effectiveTarget)`, effectiveTarget including penalty per :1012). Once block 1 completes with a tamper penalty, every later span reading understates elapsed by `penaltySecondsAdded` — up to 900 s (`MonolithRepository.kt:96`, `HMAC_TAMPER_PENALTY_SECONDS = 15 * 60`). The desk clock — the surface the span fix was built for — tells him he has up to fifteen minutes he does not have, precisely in the tamper case.

---

## ACQUITTALS

### A-1 — `fallbackToDestructiveMigration`: dead as code, present only as its own obituary
Tree-wide grep returns a single hit, `data/MonolithDatabase.kt:59-64`, inside a KDoc recording the deletion ("`fallbackToDestructiveMigration` is GONE, in the same change that adds this migration — permanently."). The builder at :74-80 is `.addMigrations(MIGRATION_7_8).build()` and nothing else. Acquitted. (Its absence is what makes F-1 a crash-risk rather than a silent wipe — the ruling held; the migrations it demands are what's missing.)

### A-2 — The fabricated AOD constant (0.65f): dead on the desk clock
No progress use of 0.65f exists. The only 0.65f in `AirlockAndDisarmOverlays.kt` is :228, `.background(Color.Black.copy(alpha = 0.65f))` — the digest sheet's scrim, a different surface. The desk-clock progress at :117-128 derives the timed branch from the span (:123-124) and renders idle as honest `else -> 0f` (:127). Acquitted for the AOD constant specifically; the literal-progress family survives elsewhere and is charged at F-9.

### A-3 — Token ledger core and week anchor: derived, single-sited, transactionally spent
The week is defined once, `data/MonolithWeek.kt:28-32` (`weekAnchorMillis`, Monday 00:00 local), and every consumer calls it — Repository :1177, :1191, :1230; the DAO only queries by the stored anchor (`MonolithDao.kt:86-90`). The remaining formula is single-sited, `MonolithWeek.kt:41-42`: `(1 - spendCount).coerceAtLeast(0)`. The only spend write is one transaction, `MonolithRepository.kt:1224-1248` (check-anchor-count → insert row → disarm, `RefusedNoToken` writing nothing). No count is stored in UI state: `MonolithViewModel.kt:67-75` keeps `DisarmChallengeState` count-free by design, and the overlay receives the live repository flow (`MainActivity.kt:195`, :562-564). Acquitted — except the Deck badge, charged at F-5.

### A-4 — "Laid Seal" language / Bit-Identical contradiction: no hits
Tree-wide grep over all `.kt` (and `app/src` generally) for "Laid Seal", "laid seal", "LaidSeal": zero. "identical"/"Identical": zero hits in code — no comment claims bit-identical anything. The seal vocabulary that exists is the sound (`TactileSoundEngine.kt:334-336`, `playHarmonicSeal`) and ordinary "session stays sealed" state language (e.g. `MonolithViewModel.kt:986`) — neither asserts the counterfoil is "laid", neither touches the Bit-Identical ruling. Acquitted.

### A-5 — AES-GCM export code: genuinely dead; HMAC remains, alone
No `Cipher`, AES, or GCM code exists anywhere in the tree. `CryptoIntegrityManager.kt` is HMAC-only: alias/algorithm at :24-25 (`monolith_hmac_timer_guard_v1`, `HmacSHA256`), signing/verify at :62-92, with the export's kill note at :17-19. Session receipts print the HMAC prefix (CurioVaultScreen.kt:264). Acquitted as code; its ghost comments are charged at F-10, and its badge over blank seed signatures at F-3.

### A-6 — The one migration that exists is exactly right
`MIGRATION_7_8`'s DDL (`MonolithDatabase.kt:44-49`) matches `EmergencyBreakSpendEntity` (`MonolithModels.kt:326-332`) column-for-column, name and type: `id` INTEGER PK AUTOINCREMENT, `sessionId`, `spentAtWallMillis`, `weekAnchorMillis`, `heldSeconds` all INTEGER NOT NULL. No backfill, per TOKEN-SPEC §6. Acquitted.

### A-7 — Compile hygiene of the merge, apart from dead imports
In the four audited files (`MonolithRepository.kt`, `MainActivity.kt`, `MonolithViewModel.kt`, `AirlockAndDisarmOverlays.kt`): zero duplicated imports, zero wildcard imports. A tree-wide census of top-level declarations finds every class/object/enum name exactly once — no two classes share a simple name, so no collision is hiding behind an import. No reference to a lane-only symbol was found in the merged files. Acquitted, except F-13.

### A-8 — AOD revert remains: no orphaned break branch, no unused locals in the desk clock
The reverted break-progress branch left no corpse in `AlwaysOnDeskClockOverlay`. The progress `when` (:117-128) has three branches — timed-span, stopwatch, idle-zero — all reachable, none break-specific; `displaySecs`, `progress`, `dx`, `dy` are all consumed (:105-185). The span reading the revert was re-applied over is intact and correct in form (:99-105 reads `spanRemainingSeconds` for timed sessions, with the reasoning recorded at :100-104). Acquitted on dead code; the function's comments are charged at F-11 and its stopwatch denominator at F-9.

### A-9 — `usageAccess()` and `liveWarrant()` themselves: the tri-state is honestly built
`MonolithRepository.kt:536-562`: MODE_ALLOWED → ALLOWED, MODE_DEFAULT → UNSET, else DENIED, exception → UNSET — UNSET is never silently promoted or demoted. `liveWarrant()`'s verdict table (:582-587) maps DENIED → OFF but UNSET → UNPROVEN, preserving the distinction the Boolean destroyed. The construction is clean; the lie is that nothing calls it (F-2) and the Guard surface still eats the Boolean (F-6). Acquitted at the source.

---

## WORST FINDING

The merged trunk ships database v8 guarded by exactly one migration that creates only the spends table, while three other tables and two dropped columns have no DDL anywhere in the tree — so the first real upgrade from a pre-v8 install crashes on open, and the migration test cannot see it because its "v7 database" is a two-column fiction.

— Falk, Critics seat 21 (provenance)
