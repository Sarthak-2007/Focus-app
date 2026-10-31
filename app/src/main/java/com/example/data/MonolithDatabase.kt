package com.example.data

import android.content.Context
import android.os.Build
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        BlockedAppEntity::class,
        FocusProfileEntity::class,
        FocusSessionEntity::class,
        InterceptLogEntity::class,
        FocusTaskEntity::class,
        BlockedSiteEntity::class,
        GuardSettingsEntity::class,
        RoutineEntity::class,
        EmergencyBreakSpendEntity::class,
        DayNoteEntity::class,
        TaskAccrualSegmentEntity::class
    ],
    version = 19,
    exportSchema = false
)
abstract class MonolithDatabase : RoomDatabase() {
    abstract fun monolithDao(): MonolithDao

    companion object {
        @Volatile
        private var INSTANCE: MonolithDatabase? = null

        /**
         * ROUTINES REBUILD (8→9): schema only — 15 ADD COLUMNs and
         * ONE new table (day_notes, F-SCH-1 — every Drawer-train
         * document promised it this window; Ren ruled the census
         * amended before 8→9 lands), zero data writes (the reseed
         * is a data transform in seed-sync under its flag, NOT a
         * migration callback; the day-word drain likewise runs in
         * seed-sync, never here).
         * Sten's 13 resolved across three tables + Nadia's
         * startedAtSessionElapsedSec + Rurik's taskId + Otis's
         * lastRoutineTickMillis (coordinator arbitration A1).
         *
         * NOT created, by name: autoPausedAtMillis (auto-pause is
         * struck), deleteAfterWindowEnd (R5 makes pending-delete
         * unnecessary), the routine_attendance table (attendance is
         * a derivation over session rows), and testDateEpochDay —
         * the test-date predicate is STRUCK entirely (Ren, 0915):
         * the app must not know a date is a test day, so no column,
         * no state, no suppression exists for it anywhere.
         */
        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Sten — routines: pause (plan §3.5) + eligibility horizon (Inv. §3.5).
                db.execSQL("ALTER TABLE routines ADD COLUMN pausedUntilMillis INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE routines ADD COLUMN scheduleEditedAtMillis INTEGER NOT NULL DEFAULT 0")

                // Sten — focus_sessions: routine identity, frozen window, evidence, outcome.
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN routineId INTEGER")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN windowStartMillis INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN windowEndMillis INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN routineOutcome TEXT")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN evidenceMask INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN firstEvidenceMillis INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN lastEvidenceMillis INTEGER NOT NULL DEFAULT 0")

                // Sten — guard_settings: reseed flags (plan §4) + boot witness (Q3).
                // Otis A1 — the tick stamp rides this same batch.
                db.execSQL("ALTER TABLE guard_settings ADD COLUMN routineReseedV1Done INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE guard_settings ADD COLUMN routineReseedNoteDismissed INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE guard_settings ADD COLUMN lastBootAtMillis INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE guard_settings ADD COLUMN lastRoutineTickMillis INTEGER NOT NULL DEFAULT 0")

                // Nadia — focus_tasks: session-elapsed offset at promotion (Inv. 08, Mech. 3).
                // Nullable, no DEFAULT: existing rows stay NULL = no offset ever recorded.
                db.execSQL("ALTER TABLE focus_tasks ADD COLUMN startedAtSessionElapsedSec INTEGER")

                // Rurik — focus_sessions: the Arm Ticket's task bond (Inv. 07, Inv. 3).
                // Nullable, no DEFAULT, no backfill: NULL = no bond was recorded.
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN taskId INTEGER")

                // F-SCH-1 — day_notes: the Drawer's declared day
                // words (dateIso → REST / ILL / FAMILY, never TEST).
                // Two columns only; the SQL is Room's own generated
                // form for DayNoteEntity so the migrated schema and
                // the entity schema are one shape.
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `day_notes` (" +
                    "`dateIso` TEXT NOT NULL, " +
                    "`word` TEXT NOT NULL, " +
                    "PRIMARY KEY(`dateIso`))"
                )

                // routine_attendance is never created: attendance is
                // a derivation over session rows, not a table.
            }
        }

        /**
         * SLICE E (9→10): Saum's unified manifest (F-N1 + F-SCH-2,
         * Ren's rulings §12) — schema only, zero data writes, no
         * backfill anywhere: every default IS the truthful pre-E
         * value (-1 = uncaptured held count; '' = no outcome
         * classified; 0 = no beat ever recorded).
         *
         * focus_sessions +8: the persisted seal record
         * (heldAppsAtSeal / heldSitesAtSeal — Emil E-2's permanent
         * form of the in-memory freeze), Draht's release-fault
         * triple, Nocke's outcome discriminator, and the persisted
         * shield beat + gap latch the finish machine reads.
         * guard_settings +4: the receipt's home (Zettel Z1) — the
         * № counter, the slot pointer, the outcome word, and
         * Nocke's composite latch.
         *
         * NOT created, by name: testDateEpochDay — the test-date
         * predicate is STRUCK (Ren); a PRAGMA-scan test asserts no
         * column of that name exists at v10, as at v9.
         */
        val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Fader/Zettel — the held counts, captured at birth.
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN heldAppsAtSeal INTEGER NOT NULL DEFAULT -1")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN heldSitesAtSeal INTEGER NOT NULL DEFAULT -1")
                // Draht — the release-fault record.
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN releaseFaultShield INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN releaseRetriesDone INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN releaseFaultedAtWallMillis INTEGER NOT NULL DEFAULT 0")
                // Nocke — the outcome discriminator.
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN outcome TEXT NOT NULL DEFAULT ''")
                // Nocke — the persisted shield beat + the gap latch.
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN shieldLastBeatWallMillis INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN shieldGapSeen INTEGER NOT NULL DEFAULT 0")
                // Zettel Z1 / Nocke / Reihe — the receipt's home.
                db.execSQL("ALTER TABLE guard_settings ADD COLUMN lastCompositeSessionId INTEGER NOT NULL DEFAULT -1")
                db.execSQL("ALTER TABLE guard_settings ADD COLUMN lastReceiptOutcome TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE guard_settings ADD COLUMN receiptSequence INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE guard_settings ADD COLUMN lastReceiptSessionId INTEGER NOT NULL DEFAULT 0")
            }
        }

        /**
         * TRUTH CAR 2 (10→11): schema only, zero data writes, no
         * backfill — both defaults ARE the truthful pre-v11 value
         * (0 = the fact was never recorded).
         *
         * focus_sessions +2, both Hale receipt-pass charges:
         * endWallMillis (RC-01 — the true end wall-time, stamped
         * at the ending write so a recovered finish stops printing
         * a reconstruction as fact) and receiptNumber (RC-04 — the
         * claimed № stamped on the row inside the claim
         * transaction, so the one-session-one-№ guarantee rests
         * on the row, not on single-slot arithmetic).
         *
         * NOT created, by name: testDateEpochDay — the test-date
         * predicate stays STRUCK (Ren), as at v9 and v10.
         */
        val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN endWallMillis INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN receiptNumber INTEGER NOT NULL DEFAULT 0")
            }
        }

        /**
         * TRUTH CAR 3 (11→12, the car's single shipped migration,
         * composed by Ren at merge from the slices' declared adds;
         * Konto's map holds the composition): schema only, zero
         * data writes, no backfill.
         *
         * guard_settings +1 (Falk, UNA-01): the Shorts + Reels
         * sniper's persisted switch. DEFAULT 1 IS the truthful
         * pre-v12 value: until this column the sniper fired
         * unconditionally during active sessions, so every
         * existing row was, in effect, ON.
         *
         * focus_sessions +1 (Tamm, ST-02): breakTakenSeconds —
         * the accumulated Safe Break seconds actually taken,
         * booked at every break exit, read by spanElapsedSeconds
         * in place of the assumed full length. A session in
         * flight across this upgrade carries 0, so its
         * pre-upgrade breaks understate its span by the assumed
         * length each — narrow, stated here, not hidden.
         *
         * NOT created, by name: testDateEpochDay — the test-date
         * predicate stays STRUCK (Ren), as at v9, v10 and v11.
         */
        /**
         * ISSUE #287 (12→13): schema only, zero data writes, no
         * backfill — every default IS the truthful pre-v13 value
         * ('' = no culprit recorded, '' = no reflection stored).
         *
         * focus_sessions +3 (Forest head-to-head F5/F8): the
         * failure-cause pair (package + kind) stamped at the
         * ending write by the disarm doors, and the one
         * user-authored reflection line stored against the
         * finished session.
         *
         * NOT created, by name: testDateEpochDay — the test-date
         * predicate stays STRUCK (Ren), as at v9–v12.
         */
        val MIGRATION_12_13 = object : Migration(12, 13) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN failureCausePackage TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN failureCauseKind TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN reflectionNote TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_11_12 = object : Migration(11, 12) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE guard_settings ADD COLUMN shortsReelsSniperEnabled INTEGER NOT NULL DEFAULT 1")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN breakTakenSeconds INTEGER NOT NULL DEFAULT 0")
            }
        }

        /**
         * Issue #10 (W-41): the seal's held count, split by tier.
         * heldShutAtSeal / heldPeekAtSeal record how many of the
         * apps held at a session's birth were SHUT and how many
         * were on the 2-minute PEEK tier, so the receipt can print
         * the two facts separately instead of one merged figure.
         * (13→14, not 12→13: v13 was taken by the failure-cause
         * columns above while this cure was in flight — the split
         * chains after it, and both land.)
         *
         * There is NO backfill — a pre-v14 row's split was never
         * recorded, and inventing one from today's tiers would
         * fabricate history. Old rows keep -1 ("uncaptured"); the
         * receipt prints their stored total and omits the split,
         * the same omission law heldAppsAtSeal has always obeyed.
         */
        val MIGRATION_13_14 = object : Migration(13, 14) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Defensive form (main-merge ruling on M-A1 OPEN-1):
                // a device that ever installed a train-v13 build
                // already carries these two columns at version 13;
                // an unguarded ADD COLUMN would crash its upgrade
                // with a duplicate-column error. The guard makes the
                // step idempotent — identical result on every
                // install history, no main-line behaviour change.
                addColumnIfAbsent(db, "focus_sessions", "heldShutAtSeal",
                    "ALTER TABLE focus_sessions ADD COLUMN heldShutAtSeal INTEGER NOT NULL DEFAULT -1")
                addColumnIfAbsent(db, "focus_sessions", "heldPeekAtSeal",
                    "ALTER TABLE focus_sessions ADD COLUMN heldPeekAtSeal INTEGER NOT NULL DEFAULT -1")
            }
        }

        /**
         * W3 #134, DURCH-D closure recomposition (14→15; renumbered
         * again at landing — main took 13→14 itself for issue #10's
         * held-split while this lane was in flight, exactly as it
         * had taken 12→13 for #287 before that): schema only, zero
         * data writes, no backfill — the default IS the truthful
         * pre-v15 value (0 = no call seconds were ever excluded;
         * before this column the progress machine never looked, so
         * no past row can claim any).
         *
         * focus_sessions +1: callExcludedSeconds — the per-block
         * seconds the call bypass was active, excluded from the
         * booked elapsed by updateSessionProgress so call time
         * is never certified as focus time.
         */
        val MIGRATION_14_15 = object : Migration(14, 15) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN callExcludedSeconds INTEGER NOT NULL DEFAULT 0")
            }
        }

        /**
         * Issue #261 (15→16): schema only, zero data writes, no
         * backfill — both defaults ARE the truthful pre-v16 values
         * (0 / false: before this door, no Safe Break could start
         * mid-block, so no past row entered one or booked its
         * planned length).
         *
         * focus_sessions +2, both for the mid-block Safe Break
         * door (the transferred W9 DAYLIGHT DS3 finding):
         * midBlockSafeBreak — the CURRENT break's origin marker,
         * read by the break exits to resume the same block instead
         * of advancing past it; extraSafeBreakSeconds — the
         * planned length of every mid-block break entered, priced
         * by spanTargetSeconds.
         *
         * NOT created, by name: testDateEpochDay — the test-date
         * predicate stays STRUCK (Ren), as at v9–v15.
         */
        val MIGRATION_15_16 = object : Migration(15, 16) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN midBlockSafeBreak INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN extraSafeBreakSeconds INTEGER NOT NULL DEFAULT 0")
            }
        }

        /**
         * AUDIT R6 #340 (16→17): schema only, zero data writes, no
         * backfill — every default IS the truthful pre-v17 value.
         * (Renumbered at the FIX-R3 landing: this cure was authored
         * against v12, renumbered through v13/v14/v15 in flight and
         * carried as 15→16 on the branch — but main took 15→16
         * itself for issue #261's Safe Break columns above while
         * this landing was blocked, so the seal chains after it as
         * 16→17. Both column sets land; neither migration is lost.)
         *
         * focus_sessions +2 (F5/F48): the finish seal and its chain
         * link. '' = never sealed — every pre-v17 receipt stands
         * exactly as provable as it was the day it printed, and no
         * seal is ever minted retroactively over old claims.
         * blocked_sites +1 (F10): the per-site redirect target.
         * '' = no destination; BACK + Airlock stands.
         * guard_settings +1 (F68): notification-body consent, kept
         * apart from the listener grant. 'UNSET' = bodies are NOT
         * kept — the private posture is the default posture.
         */
        val MIGRATION_16_17 = object : Migration(16, 17) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN finishSignature TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN prevFinishSignature TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE blocked_sites ADD COLUMN redirectTarget TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE guard_settings ADD COLUMN notificationBodyConsent TEXT NOT NULL DEFAULT 'UNSET'")
            }
        }

        /**
         * Batch 10 (17→18 at the #231 composition sitting,
         * K8/floor lane): the train’s composed 12→13 content,
         * landed as a NEW link — main’s 12_13 (#287) is shipped
         * history and is never redefined, and main’s own 16→17
         * (#340, above) is likewise shipped: this content was
         * authored as the train’s 16→17, but main took that link
         * while the train was frozen, so it chains after it. Carries: Frist’s five
         * (peek/break settings + the break/extension stamps) and
         * Voss’s one (withinLabel), plus #110’s accrual-segment
         * table (REISE cure) and #30 F1’s four secondary indices.
         * The W-41 held pair is NOT here: main’s 13_14 already
         * adds exactly those columns (identical type/default);
         * running them twice is a duplicate-column crash. ADD
         * COLUMNs run through the same defensive guard as
         * 13_14, so a train-v13 install (which already has
         * these columns at v13) upgrades cleanly too.
         */
        val MIGRATION_17_18 = object : Migration(17, 18) {
            override fun migrate(db: SupportSQLiteDatabase) {
                addColumnIfAbsent(db, "guard_settings", "peekMinutes",
                    "ALTER TABLE guard_settings ADD COLUMN peekMinutes INTEGER NOT NULL DEFAULT 2")
                addColumnIfAbsent(db, "guard_settings", "breakMinutes",
                    "ALTER TABLE guard_settings ADD COLUMN breakMinutes INTEGER NOT NULL DEFAULT 10")
                addColumnIfAbsent(db, "focus_sessions", "breakSeconds",
                    "ALTER TABLE focus_sessions ADD COLUMN breakSeconds INTEGER NOT NULL DEFAULT 600")
                addColumnIfAbsent(db, "focus_sessions", "extensionSecondsAdded",
                    "ALTER TABLE focus_sessions ADD COLUMN extensionSecondsAdded INTEGER NOT NULL DEFAULT 0")
                addColumnIfAbsent(db, "focus_sessions", "extensionLog",
                    "ALTER TABLE focus_sessions ADD COLUMN extensionLog TEXT NOT NULL DEFAULT ''")
                addColumnIfAbsent(db, "focus_sessions", "withinLabel",
                    "ALTER TABLE focus_sessions ADD COLUMN withinLabel TEXT")
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `task_accrual_segments` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`taskId` INTEGER NOT NULL, " +
                    "`sessionId` INTEGER NOT NULL, " +
                    "`dateIso` TEXT NOT NULL, " +
                    "`seconds` INTEGER NOT NULL)"
                )
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS `index_task_accrual_segments_taskId_sessionId_dateIso` " +
                    "ON `task_accrual_segments` (`taskId`, `sessionId`, `dateIso`)"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_focus_tasks_completedAtMillis` ON `focus_tasks` (`completedAtMillis`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_focus_sessions_routineId` ON `focus_sessions` (`routineId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_focus_sessions_windowEndMillis` ON `focus_sessions` (`windowEndMillis`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_intercept_logs_timestampMillis` ON `intercept_logs` (`timestampMillis`)")
            }
        }

        /**
         * Issue #100 (notifications-lived) — Sarthak's ruling:
         * notification-content storage is CUT. The captured
         * notifications table (plaintext message bodies, stored
         * since v1-era) is dropped outright. Held notifications
         * are now snoozes of the originals held by the system;
         * Monolith keeps no copy, so there is nothing to migrate
         * the rows into — dropping is the whole migration.
         *
         * BUILDER-100 composition note (Oct 31): authored as
         * 14→15 on the CUT branch, renumbered 15→16 by DURCH-C,
         * renumbered again to 18→19 at this landing — main took
         * 15→16 itself for #261's Safe Break columns, 16→17 for
         * #340 and 17→18 for batch-10 while this lane was in
         * flight. Same drop, same ruling; only the number moved.
         */
        val MIGRATION_18_19 = object : Migration(18, 19) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS captured_notifications")
            }
        }

        /**
         * TOKEN-SPEC §6: the spend ledger arrives as an explicit
         * migration. There is NO backfill — today's in-memory spends
         * were never recorded, so v8 truthfully starts with an empty
         * ledger and a full token; inventing a past spend would be a
         * fabricated record.
         */
        /**
         * Idempotent ADD COLUMN (main-merge defensive form, M-A1
         * OPEN-1 ruling): runs [ddl] only when [column] is absent
         * from [table], so a migration step survives an install
         * whose earlier lineage already added the same column.
         */
        private fun addColumnIfAbsent(db: SupportSQLiteDatabase, table: String, column: String, ddl: String) {
            var present = false
            db.query("PRAGMA table_info($table)").use { cursor ->
                val nameIdx = cursor.getColumnIndex("name")
                while (cursor.moveToNext()) {
                    if (cursor.getString(nameIdx) == column) { present = true; break }
                }
            }
            if (!present) db.execSQL(ddl)
        }

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `emergency_break_spends` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`sessionId` INTEGER NOT NULL, " +
                    "`spentAtWallMillis` INTEGER NOT NULL, " +
                    "`weekAnchorMillis` INTEGER NOT NULL, " +
                    "`heldSeconds` INTEGER NOT NULL)"
                )
            }
        }

        /**
         * Uses Device-Protected Storage Context (`createDeviceProtectedStorageContext()`)
         * on Android N+ so Direct-Boot (`LOCKED_BOOT_COMPLETED`) can enforce active locks
         * immediately upon device reboot before the user even unlocks their lock screen.
         *
         * TOKEN-SPEC §6.2: `fallbackToDestructiveMigration` is GONE, in
         * the same change that adds this migration — permanently. With
         * it, any future un-migrated bump would silently wipe sessions,
         * routines and spends. From v8 on, a missing migration is a loud
         * IllegalStateException in testing, never a silent wipe on his
         * phone.
         */
        /**
         * Issue #83 (storage-hygiene F4): space reclamation. Every
         * erase in this app was a bare DELETE, so deleted rows' pages
         * stayed in the file forever and monolith_tactile_db grew
         * monotonically for the life of the install. This callback
         * puts the file on INCREMENTAL auto-vacuum: the first open
         * that finds the file not already incremental performs the
         * one full VACUUM the mode change requires, and every open
         * hands a bounded slice of free pages back to the device.
         * Best-effort by construction — a pragma failure must never
         * cost the app its database open.
         */
        private val RECLAIM_CALLBACK = object : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                try {
                    val cursor = db.query("PRAGMA auto_vacuum")
                    val mode = try {
                        if (cursor.moveToFirst()) cursor.getInt(0) else 0
                    } finally {
                        cursor.close()
                    }
                    // auto_vacuum: 0 = NONE, 1 = FULL, 2 = INCREMENTAL.
                    if (mode != 2) {
                        db.execSQL("PRAGMA auto_vacuum = INCREMENTAL")
                        db.execSQL("VACUUM")
                    }
                    db.execSQL("PRAGMA incremental_vacuum(512)")
                } catch (_: Exception) {
                    // Contained: reclamation is hygiene, not function.
                }
            }
        }

        fun getInstance(context: Context): MonolithDatabase {
            return INSTANCE ?: synchronized(this) {
                // The second check INSIDE the lock (issue #31
                // finding 1): the pre-fix shape re-checked nothing
                // after acquiring it, so two racing callers (boot
                // receivers + the activity's first read are the
                // realistic pair) could each build a database —
                // the second overwriting INSTANCE and orphaning
                // the first's open instance, its DAOs, and any
                // transaction in flight on it.
                INSTANCE?.let { return@synchronized it }
                val appCtx = context.applicationContext
                val storageContext = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    appCtx.createDeviceProtectedStorageContext() ?: appCtx
                } else {
                    appCtx
                }
                val instance = Room.databaseBuilder(
                    storageContext,
                    MonolithDatabase::class.java,
                    "monolith_tactile_db"
                )
                    .addMigrations(MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10, MIGRATION_10_11, MIGRATION_11_12, MIGRATION_12_13, MIGRATION_13_14, MIGRATION_14_15, MIGRATION_15_16, MIGRATION_16_17, MIGRATION_17_18, MIGRATION_18_19)
                    .addCallback(RECLAIM_CALLBACK)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * Test-only escape hatch (issue #31 finding 4): closes
         * the singleton and clears it under the same lock
         * [getInstance] builds under, so a test can start from a
         * cold database instead of inheriting whatever a previous
         * test built. Production paths never call this — in
         * production the singleton's immortality is deliberate
         * (Direct-Boot enforcement reads it from boot receivers
         * before any unlock exists).
         */
        fun closeAndResetForTests() {
            synchronized(this) {
                INSTANCE?.close()
                INSTANCE = null
            }
        }
    }
}
