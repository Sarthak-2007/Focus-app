package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.SystemGuardStatus
import com.example.service.TactileSoundEngine
import com.example.ui.components.BlockitActionChip
import com.example.ui.components.FullBleedSectionHeader
import com.example.ui.views.SledSwitchView

/**
 * SYSTEM GUARD & HARDWARE INTERCEPTOR CONSOLE (DEVELOPER-GUIDE.pdf §4.2 #6 SledSwitch)
 */
@Composable
fun SystemGuardScreen(
    status: SystemGuardStatus,
    soundEnabled: Boolean,
    shortsReelsSniperEnabled: Boolean = true,
    browserUrlBlockerEnabled: Boolean = true,
    soundEngine: TactileSoundEngine? = null,
    isDarkTheme: Boolean = false,
    onRefreshStatus: () -> Unit,
    onToggleSound: () -> Unit,
    onToggleShortsReelsSniper: () -> Unit = {},
    onToggleBrowserUrlBlocker: () -> Unit = {},
    onOpenAccessibilitySettings: () -> Unit,
    onOpenUsageAccessSettings: () -> Unit,
    onOpenOverlaySettings: () -> Unit,
    onOpenBatteryOptimizationSettings: () -> Unit,
    onOpenNotificationListenerSettings: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(scheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("system_guard_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FullBleedSectionHeader(
            titleWord = "FEED & BROWSER INTERCEPTORS",
            rightWordPrefix = "SLED ",
            rightMonoNumber = "03",
            rightWordSuffix = " SWITCHES",
            useAccentOnNumber = true
        )

        AndroidView(
            factory = { ctx ->
                SledSwitchView(ctx).apply {
                    this.soundEngine = soundEngine
                }
            },
            update = { view ->
                view.isNightMode = isDarkTheme
                view.soundEngine = soundEngine
                view.titleText = "SHORTS & REELS SNIPER"
                view.subtitleText = "Intercepts YouTube Shorts, Reels & TikTok feeds"
                view.isChecked = shortsReelsSniperEnabled
                view.onToggleRequested = onToggleShortsReelsSniper
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        )

        AndroidView(
            factory = { ctx ->
                SledSwitchView(ctx).apply {
                    this.soundEngine = soundEngine
                }
            },
            update = { view ->
                view.isNightMode = isDarkTheme
                view.soundEngine = soundEngine
                view.titleText = "BROWSER SOCIAL URL SHIELD"
                view.subtitleText = "Blocks distracting social domains in Chrome & browsers"
                view.isChecked = browserUrlBlockerEnabled
                view.onToggleRequested = onToggleBrowserUrlBlocker
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        )

        AndroidView(
            factory = { ctx ->
                SledSwitchView(ctx).apply {
                    this.soundEngine = soundEngine
                }
            },
            update = { view ->
                view.isNightMode = isDarkTheme
                view.soundEngine = soundEngine
                view.titleText = "FLOAT-PCM HARDWARE SYNTH"
                view.subtitleText = "4-partial harmonic clicks (0.6, 0.28, 0.37, 2.2) + 1/4/8/10ms haptics"
                view.isChecked = soundEnabled
                view.onToggleRequested = onToggleSound
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))
        FullBleedSectionHeader(
            titleWord = "ANDROID SYSTEM PERMISSIONS",
            rightWordPrefix = "STATUS ",
            rightMonoNumber = "LIVE"
        )

        AndroidView(
            factory = { ctx -> SledSwitchView(ctx) },
            update = { view ->
                view.isNightMode = isDarkTheme
                view.titleText = "ACCESSIBILITY INTERCEPTOR"
                view.subtitleText = "Required for instant foreground package & URL airlock"
                view.isChecked = status.isAccessibilityServiceEnabled
                view.onToggleRequested = onOpenAccessibilitySettings
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        )

        AndroidView(
            factory = { ctx -> SledSwitchView(ctx) },
            update = { view ->
                view.isNightMode = isDarkTheme
                view.titleText = "USAGE STATS TELEMETRY"
                view.subtitleText = "Verifies foreground app usage & anti-tamper timers"
                view.isChecked = status.hasUsageStatsPermission
                view.onToggleRequested = onOpenUsageAccessSettings
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        )

        AndroidView(
            factory = { ctx -> SledSwitchView(ctx) },
            update = { view ->
                view.isNightMode = isDarkTheme
                view.titleText = "NOTIFICATION SILENCER"
                view.subtitleText = "Captures distracting pings into the Held Digest"
                view.isChecked = status.isNotificationListenerEnabled
                view.onToggleRequested = onOpenNotificationListenerSettings
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        )

        AndroidView(
            factory = { ctx -> SledSwitchView(ctx) },
            update = { view ->
                view.isNightMode = isDarkTheme
                view.titleText = "UNRESTRICTED BATTERY & ADMIN"
                view.subtitleText = "Prevents OS from killing active lockdown timers"
                view.isChecked = status.isDeviceAdminEnabled
                view.onToggleRequested = onOpenBatteryOptimizationSettings
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        )

        BlockitActionChip(
            wordLabel = "REFRESH SYSTEM PERMISSIONS",
            isPrimaryInk = true,
            onClick = onRefreshStatus,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
