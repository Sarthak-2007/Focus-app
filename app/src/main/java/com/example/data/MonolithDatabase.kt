package com.example.data

import android.content.Context
import android.os.Build
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        BlockedAppEntity::class,
        FocusProfileEntity::class,
        FocusSessionEntity::class,
        InterceptLogEntity::class,
        CapturedNotificationEntity::class,
        FocusTaskEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class MonolithDatabase : RoomDatabase() {
    abstract fun monolithDao(): MonolithDao

    companion object {
        @Volatile
        private var INSTANCE: MonolithDatabase? = null

        /**
         * Uses Device-Protected Storage Context (`createDeviceProtectedStorageContext()`)
         * on Android N+ so Direct-Boot (`LOCKED_BOOT_COMPLETED`) can enforce active locks
         * immediately upon device reboot before the user even unlocks their lock screen.
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
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
