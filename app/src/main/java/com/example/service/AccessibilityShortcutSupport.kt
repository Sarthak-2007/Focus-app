package com.example.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings

/**
 * Issue #337 (Audit R5 supplement — Freedom, Platform F1):
 * Freedom offers the accessibility button/shortcut as a second
 * activation path for its service; Monolith had no shortcut
 * integration of any kind — 'shortcut' appeared nowhere in the
 * tree, and the service could only ever be reached through the
 * standard Settings toggle.
 *
 * Android's model: ANY accessibility service can be bound to the
 * system accessibility shortcut/button, but the binding is the
 * USER's secure setting (`accessibility_shortcut_target_service`
 * / `accessibility_button_targets`). What the app owns is:
 *  - knowing whether Monolith is the current shortcut target
 *    (this file's reads — so setup can say so truthfully), and
 *  - deep-linking to the exact system screen where the binding is
 *    made (this file's intent), instead of leaving the user to
 *    hunt through Settings.
 */
object AccessibilityShortcutSupport {

    /** Secure-settings keys, as literals so the reads work on
     *  every API level Monolith ships (minSdk 24); the constants
     *  only exist on newer APIs. */
    const val KEY_SHORTCUT_TARGET = "accessibility_shortcut_target_service"
    const val KEY_BUTTON_TARGETS = "accessibility_button_targets"
    const val ACTION_SHORTCUT_SETTINGS = "android.settings.ACCESSIBILITY_SHORTCUT_SETTINGS"

    /** This app's accessibility-service component, flattened the
     *  way the secure settings store it (`package/class`). */
    fun serviceComponent(context: Context): String =
        ComponentName(context, MonolithAccessibilityService::class.java)
            .flattenToString()

    /** Parse a secure-settings target list. Values arrive either
     *  as one component or several separated by ':' (button
     *  targets) — both shapes are handled. */
    fun parseTargets(raw: String?): List<String> =
        raw?.split(':')?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()

    /** True when [component] (flattened `package/class`) is among
     *  [targets]; targets stored in the short form
     *  (`package/.Class`) are expanded before comparison. */
    fun isTargeted(targets: List<String>, component: String): Boolean {
        val pkg = component.substringBefore('/')
        val cls = component.substringAfter('/')
        return targets.any { target ->
            val targetPkg = target.substringBefore('/')
            val targetCls = target.substringAfter('/', "")
            val expanded = if (targetCls.startsWith(".")) targetPkg + targetCls else targetCls
            targetPkg == pkg && expanded == cls
        }
    }

    /** Is Monolith currently bound to the accessibility shortcut
     *  or the accessibility button on this phone? */
    fun isMonolithShortcutTarget(context: Context): Boolean {
        val component = serviceComponent(context)
        val resolver = context.contentResolver
        val shortcut = parseTargets(
            Settings.Secure.getString(resolver, KEY_SHORTCUT_TARGET)
        )
        val button = parseTargets(
            Settings.Secure.getString(resolver, KEY_BUTTON_TARGETS)
        )
        return isTargeted(shortcut, component) || isTargeted(button, component)
    }

    /** The deep link to the system's accessibility-shortcut
     *  screen, where the user binds Monolith to the shortcut /
     *  button. Falls back to the accessibility settings root on
     *  devices whose ROM lacks the dedicated screen. */
    fun shortcutSettingsIntent(): Intent =
        Intent(ACTION_SHORTCUT_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

    fun accessibilitySettingsIntent(): Intent =
        Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
}
