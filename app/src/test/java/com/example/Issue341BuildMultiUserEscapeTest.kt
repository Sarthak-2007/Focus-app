package com.example

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Audit R6 #341 BUILD — F01 [CRITICAL]: multi-user is an escape
 * from a blocker that only watches the owner profile — a second
 * user/profile runs unpoliced, and when the owner returns,
 * Monolith used to resume as if no time had passed.
 *
 * Named limit (in the open, per the desk draft): the in-profile
 * half is NOT buildable from this app — Monolith's process does
 * not run in the other profile, and no API lets it police a
 * profile it isn't installed in. What this PR builds is the
 * RETURN side: a manifest receiver for USER_SWITCHED /
 * USER_BACKGROUND that stamps the gap onto the active session
 * (a USER_GAP session-event), so the return is never silent.
 * These cases pin the manifest declaration + the receiver's
 * contract; OEM device verification of the switch itself stays
 * a named blocker, not a claimed pass.
 *
 * PIN scope: PLUMBING.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class Issue341BuildMultiUserEscapeTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    @Test
    fun manifest_declaresUserSwitchReceiver() {
        val info = context.packageManager.getPackageInfo(
            context.packageName, PackageManager.GET_RECEIVERS
        )
        val receiver = info.receivers?.firstOrNull {
            it.name == "com.example.service.MonolithUserReceiver"
        }
        assertNotNull("MonolithUserReceiver must be manifest-declared", receiver)
    }

    @Test
    fun userSwitchReceiver_isResolvableForBothSwitchActions() {
        // USER_SWITCHED has no public Intent constant at this SDK
        // (USER_BACKGROUND does); both are the manifest's literal
        // action strings, which is what resolution actually keys on.
        for (action in listOf(
            "android.intent.action.USER_SWITCHED",
            Intent.ACTION_USER_BACKGROUND
        )) {
            val matches = context.packageManager.queryBroadcastReceivers(
                Intent(action), 0
            )
            assertTrue(
                "$action must resolve to the user receiver",
                matches.any { it.activityInfo.name == "com.example.service.MonolithUserReceiver" }
            )
        }
    }

    @Test
    fun returnGap_vocabularyExistsOnTheSessionEventLog() {
        // The stamp the receiver writes (via the repository) is a
        // USER_GAP event — the return is a recorded fact on the
        // session's own log, never a silent resume.
        assertEquals("USER_GAP", com.example.data.SessionEventKind.USER_GAP)
    }
}
