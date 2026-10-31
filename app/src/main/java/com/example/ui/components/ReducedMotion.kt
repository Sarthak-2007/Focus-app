package com.example.ui.components

import android.content.Context
import android.provider.Settings

/**
 * Issue #256: the app-wide reduced-motion read.
 *
 * Before this file, exactly one minor effect (the Strike settle in
 * MonolithCustomComponents) ever consulted the system animator
 * scale, while the 1050ms vertigo-blur seal shot — the largest
 * motion in the app — was forced on everyone. This is the single
 * read every motion site should share: reduced motion is ON when
 * the system animator duration scale is 0 (the user has turned
 * animations off / enabled the accessibility remove-animations
 * setting). An unreadable setting reads as NOT reduced — motion
 * stays the default, never the other way round.
 */
fun isReducedMotion(context: Context): Boolean = runCatching {
    Settings.Global.getFloat(
        context.contentResolver,
        Settings.Global.ANIMATOR_DURATION_SCALE,
        1f
    )
}.getOrDefault(1f) == 0f
