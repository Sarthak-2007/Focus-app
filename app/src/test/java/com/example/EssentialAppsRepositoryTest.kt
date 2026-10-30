package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppBlockTier
import com.example.data.AppCategory
import com.example.data.BlockedAppEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.data.SmartLoadout
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Issue #119, findings 4 + 5 at the repository — the enforcement
 * layer: the SLEEP loadout keeps Camera/Waze OPEN by package, and
 * no tier writer (set / cycle / loadout) can classify the dialler
 * LOCKED.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class EssentialAppsRepositoryTest {

    private lateinit var context: Context
    private lateinit var db: MonolithDatabase
    private lateinit var repository: MonolithRepository

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = MonolithRepository(context, db.monolithDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    private fun row(pkg: String, name: String) = BlockedAppEntity(
        packageName = pkg,
        appName = name,
        category = AppCategory.SHOPPING.name,
        isBlocked = false,
        blockTier = AppBlockTier.OPEN.name
    )

    private suspend fun tierOf(pkg: String): AppBlockTier =
        db.monolithDao().getAllAppsSnapshot().first { it.packageName == pkg }.resolvedTier

    @Test
    fun sleepLoadoutKeepsCameraAndWazeOpenByPackage() = runBlocking {
        val dao = db.monolithDao()
        dao.insertApps(
            listOf(
                row("com.android.camera2", "Camera"),
                row("com.waze", "Waze"),
                row("com.google.android.dialer", "Phone"),
                row("com.instagram.android", "Instagram")
            )
        )
        repository.applySmartLoadout(SmartLoadout.SLEEP)
        assertEquals(AppBlockTier.OPEN, tierOf("com.android.camera2"))
        assertEquals(AppBlockTier.OPEN, tierOf("com.waze"))
        assertEquals(AppBlockTier.OPEN, tierOf("com.google.android.dialer"))
        assertEquals(AppBlockTier.LOCKED, tierOf("com.instagram.android"))
    }

    @Test
    fun diallerFloorBindsSetAndCycle() = runBlocking {
        val dao = db.monolithDao()
        dao.insertApps(listOf(row("com.google.android.dialer", "Phone")))

        repository.setAppBlockTier("com.google.android.dialer", AppBlockTier.LOCKED)
        assertEquals(AppBlockTier.PEEK_2M, tierOf("com.google.android.dialer"))

        // OPEN -> (cycle would say LOCKED) -> floored to PEEK_2M.
        repository.setAppBlockTier("com.google.android.dialer", AppBlockTier.OPEN)
        val dialerRow = dao.getAllAppsSnapshot().first { it.packageName == "com.google.android.dialer" }
        assertEquals(AppBlockTier.PEEK_2M, repository.cycleAppBlockTier(dialerRow))
        assertEquals(AppBlockTier.PEEK_2M, tierOf("com.google.android.dialer"))
    }
}
