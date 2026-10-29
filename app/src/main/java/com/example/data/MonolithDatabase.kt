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
        CapturedNotificationEntity::class,
        FocusTaskEntity::class,
        BlockedSiteEntity::class,
        GuardSettingsEntity::class,
        RoutineEntity::class,
        EmergencyBreakSpendEntity::class,
        DayNoteEntity::class
    ],
    version = 12,
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
        val MIGRATION_11_12 = object : Migration(11, 12) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE guard_settings ADD COLUMN shortsReelsSniperEnabled INTEGER NOT NULL DEFAULT 1")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN breakTakenSeconds INTEGER NOT NULL DEFAULT 0")
            }
        }

        /**
         * TOKEN-SPEC §6: the spend ledger arrives as an explicit
         * migration. There is NO backfill — today's in-memory spends
         * were never recorded, so v8 truthfully starts with an empty
         * ledger and a full token; inventing a past spend would be a
         * fabricated record.
         */
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
        fun getInstance(context: Context): MonolithDatabase {
            return INSTANCE ?: synchronized(this) {
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
                    .addMigrations(MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10, MIGRATION_10_11, MIGRATION_11_12)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
