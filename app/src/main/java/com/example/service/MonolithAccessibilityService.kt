package com.example.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.SystemClock
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.MainActivity
import com.example.data.AppBlockTier
import com.example.data.BlockedBrowserDomains
import com.example.data.MonolithDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Enforces real-time platform-level protections during an active focus session:
 * 1. Active Phone & VoIP Call Bypass: Checks AudioManager.mode (MODE_RINGTONE, MODE_IN_CALL,
 *    MODE_IN_COMMUNICATION) BEFORE any block check so incoming WhatsApp/Telegram/cellular calls are never kicked.
 * 2. Settings & Package Installer Blocker: Blocks attempts to Force stop, Uninstall, Clear storage,
 *    or Deactivate Monolith in Android Settings or system package installers.
 * 3. YouTube Shorts & Instagram Reels Sniper: Checks BOTH internal View IDs AND selected bottom-navigation
 *    tabs (`node.isSelected` with text/description "Shorts" or "Reels"). Remains strictly active even during Safe Breaks.
 * 4. In-Browser URL Bar Blocker: Ignores the address bar while the user is actively typing (`node.isFocused == true`),
 *    and blocks distracting domains once committed/loaded—even during Safe Breaks.
 * 5. 3-Tier Per-App & Safe Break Enforcement:
 *    - During a 10-minute Safe Break (`activeSession.isInSafeBreak`), `2m Peek` apps (e.g., WhatsApp) unlock freely
 *      while `Locked` apps, Shorts, Reels, and blocked browser domains stay strictly locked.
 *    - During study blocks, `2m Peek` apps are allowed only while `SystemClock.elapsedRealtime() < appRule.activePeekUntilElapsedRealtime`.
 */
class MonolithAccessibilityService : AccessibilityService() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val pkgName = event?.packageName?.toString() ?: return
        if (pkgName == applicationContext.packageName || pkgName.startsWith("com.android.systemui")) return

        // 1. Active Phone & VoIP Call Bypass (WhatsApp, Telegram, Cellular calls never blocked mid-ring)
        if (isCallActive()) return

        scope.launch {
            val dao = MonolithDatabase.getInstance(applicationContext).monolithDao()
            val activeSession = dao.getActiveSessionSnapshot() ?: return@launch
            if (!activeSession.isActive) return@launch

            val rootNode = try { rootInActiveWindow } catch (_: Exception) { null }

            // 2. Settings & Package Installer Blocker
            if (isPackageInstallerOrTamperSettings(pkgName, rootNode)) {
                performGlobalAction(GLOBAL_ACTION_BACK)
                launchInterceptScreen(
                    packageName = pkgName,
                    appName = "System Settings Guard",
                    reasonBadge = "Uninstall Guard"
                )
                return@launch
            }

            // 3. YouTube Shorts & Instagram Reels Sniper (Strictly active during both Work Blocks and Safe Breaks)
            if (pkgName == "com.google.android.youtube" || pkgName == "com.instagram.android") {
                if (detectShortsOrReelsFeed(rootNode)) {
                    performGlobalAction(GLOBAL_ACTION_BACK)
                    launchInterceptScreen(
                        packageName = pkgName,
                        appName = if (pkgName.contains("youtube")) "YouTube Shorts" else "Instagram Reels",
                        reasonBadge = if (pkgName.contains("youtube")) "Shorts Blocked" else "Reels Blocked"
                    )
                    return@launch
                }
            }

            // 4. In-Browser URL Bar Blocker (Strictly active during both Work Blocks and Safe Breaks; ignores focused typing)
            if (isSupportedBrowser(pkgName) && rootNode != null) {
                val matchedDomain = detectBlockedBrowserUrl(rootNode)
                if (matchedDomain != null) {
                    performGlobalAction(GLOBAL_ACTION_BACK)
                    launchInterceptScreen(
                        packageName = pkgName,
                        appName = matchedDomain,
                        reasonBadge = "Site Blocked"
                    )
                    return@launch
                }
            }

            // 5. 3-Tier Per-App & Safe Break Blocking
            val appRule = dao.getAllAppsSnapshot().find { it.packageName == pkgName } ?: return@launch
            val tier = appRule.resolvedTier
            if (tier == AppBlockTier.OPEN) return@launch

            // Safe Break Rule: 2m Peek apps unlock freely during the 10-minute Safe Break, while Locked apps stay locked
            if (activeSession.isInSafeBreak && tier == AppBlockTier.PEEK_2M) {
                return@launch
            }

            // Active 2-Minute Peek Lease Rule: allow access while monotonic clock is within the 2m lease window
            val nowMono = SystemClock.elapsedRealtime()
            if (tier == AppBlockTier.PEEK_2M && nowMono < appRule.activePeekUntilElapsedRealtime) {
                return@launch
            }

            launchInterceptScreen(
                packageName = appRule.packageName,
                appName = appRule.appName,
                reasonBadge = tier.badge
            )
        }
    }

    private fun isCallActive(): Boolean {
        return try {
            val am = getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return false
            val mode = am.mode
            mode == AudioManager.MODE_RINGTONE ||
                mode == AudioManager.MODE_IN_CALL ||
                mode == AudioManager.MODE_IN_COMMUNICATION
        } catch (_: Exception) {
            false
        }
    }

    private fun isPackageInstallerOrTamperSettings(pkgName: String, rootNode: AccessibilityNodeInfo?): Boolean {
        val installerPackages = setOf(
            "com.google.android.packageinstaller",
            "com.android.packageinstaller",
            "com.samsung.android.packageinstaller",
            "com.miui.packageinstaller"
        )
        if (installerPackages.contains(pkgName)) return true

        if (pkgName == "com.android.settings" && rootNode != null) {
            val dump = collectWindowTextLowercase(rootNode)
            val mentionsMonolith = dump.contains("monolith")
            val mentionsTamperAction = dump.contains("force stop") ||
                dump.contains("deactivate") ||
                dump.contains("uninstall") ||
                dump.contains("clear storage") ||
                dump.contains("clear data") ||
                dump.contains("device admin")
            return mentionsMonolith && mentionsTamperAction
        }
        return false
    }

    private fun detectShortsOrReelsFeed(rootNode: AccessibilityNodeInfo?): Boolean {
        if (rootNode == null) return false
        // Strategy A: Known internal View IDs
        val shortsViewIds = listOf(
            "com.google.android.youtube:id/reel_recycler",
            "com.google.android.youtube:id/reel_player_page_container",
            "com.google.android.youtube:id/shorts_player",
            "com.instagram.android:id/clips_viewer_view_pager",
            "com.instagram.android:id/reel_viewer_fragment"
        )
        for (id in shortsViewIds) {
            val nodes = try { rootNode.findAccessibilityNodeInfosByViewId(id) } catch (_: Exception) { null }
            if (!nodes.isNullOrEmpty()) return true
        }
        // Strategy B: Resilient selected bottom-navigation tab inspection (`node.isSelected`)
        return isSelectedBottomTabShortsOrReels(rootNode)
    }

    private fun isSelectedBottomTabShortsOrReels(node: AccessibilityNodeInfo?): Boolean {
        if (node == null) return false
        val desc = node.contentDescription?.toString()?.trim()?.lowercase() ?: ""
        val text = node.text?.toString()?.trim()?.lowercase() ?: ""
        if (node.isSelected && (
                desc == "shorts" || desc == "reels" ||
                    text == "shorts" || text == "reels" ||
                    desc.startsWith("shorts,") || desc.startsWith("reels,")
            )
        ) {
            return true
        }
        for (i in 0 until node.childCount) {
            if (isSelectedBottomTabShortsOrReels(node.getChild(i))) return true
        }
        return false
    }

    private fun isSupportedBrowser(pkgName: String): Boolean {
        return pkgName in setOf(
            "com.android.chrome",
            "com.brave.browser",
            "org.mozilla.firefox",
            "com.microsoft.emmx",
            "com.sec.android.app.sbrowser"
        )
    }

    /**
     * Reads the browser URL bar across Chrome, Brave, Firefox, Edge, and Samsung Internet.
     * Ignores the URL bar when `node.isFocused == true` so it never falsely triggers while the user is typing.
     */
    private fun detectBlockedBrowserUrl(rootNode: AccessibilityNodeInfo): String? {
        val urlBarViewIds = listOf(
            "com.android.chrome:id/url_bar",
            "com.brave.browser:id/url_bar",
            "org.mozilla.firefox:id/mozac_browser_toolbar_url_view",
            "com.microsoft.emmx:id/url_bar",
            "com.sec.android.app.sbrowser:id/location_bar_edit_text"
        )
        for (viewId in urlBarViewIds) {
            val nodes = try { rootNode.findAccessibilityNodeInfosByViewId(viewId) } catch (_: Exception) { null }
            if (!nodes.isNullOrEmpty()) {
                for (node in nodes) {
                    // Do NOT trigger while the address bar is focused and the user is actively typing
                    if (node.isFocused) continue
                    val urlText = node.text?.toString()?.lowercase() ?: continue
                    val matched = BlockedBrowserDomains.defaultDomains.firstOrNull { domain ->
                        urlText.contains(domain)
                    }
                    if (matched != null) return matched
                }
            }
        }
        return null
    }

    private fun collectWindowTextLowercase(node: AccessibilityNodeInfo?, depth: Int = 0): String {
        if (node == null || depth > 14) return ""
        val sb = StringBuilder()
        node.text?.let { sb.append(it).append(' ') }
        node.contentDescription?.let { sb.append(it).append(' ') }
        for (i in 0 until node.childCount) {
            sb.append(collectWindowTextLowercase(node.getChild(i), depth + 1))
        }
        return sb.toString().lowercase()
    }

    private fun launchInterceptScreen(packageName: String, appName: String, reasonBadge: String) {
        val interceptIntent = Intent(applicationContext, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra(MonolithShieldService.EXTRA_INTERCEPTED_PACKAGE, packageName)
            putExtra(MonolithShieldService.EXTRA_INTERCEPTED_APP_NAME, appName)
            putExtra(MonolithShieldService.EXTRA_INTERCEPT_BADGE, reasonBadge)
        }
        startActivity(interceptIntent)
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
