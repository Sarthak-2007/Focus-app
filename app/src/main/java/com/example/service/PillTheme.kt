package com.example.service

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.example.R

/**
 * The pill's skin, kept as DATA so a look can reach it.
 *
 * The seven look-worlds will redress this face; the machinery (the
 * derivation, the window, the hand-back) must never need to know
 * which world is wearing it. A look lane sets [PillTheme.palette]
 * (and may swap the figures' typeface through the same holder)
 * before the shield service starts — everything the renderer
 * touches comes from this one object, so the redress is a palette
 * write, not a surgery.
 */
data class PillPalette(
    /** Warm near-black glass, slight alpha so the app beneath
     *  ghosts through at the edges. */
    val backgroundArgb: Int,
    /** One hairline of separation from the app beneath. */
    val borderArgb: Int,
    /** The figures — the app's night ink (cream #FFE4CF). */
    val textArgb: Int,
    /** The face's label tier (SESSION / BREAK / the peeked name). */
    val mutedTextArgb: Int,
    /** The hand-back ✕ — the app's one locked accent (#F47442). */
    val accentArgb: Int
)

object PillPalettes {
    /** This lane's honest default: the trunk night tokens, no
     *  invention. Never green — the studio law. */
    val Default = PillPalette(
        backgroundArgb = 0xE61F1A16.toInt(),
        borderArgb = 0xFF4A4038.toInt(),
        textArgb = 0xFFFFE4CF.toInt(),
        mutedTextArgb = 0xFFB9ACA0.toInt(),
        accentArgb = 0xFFF47442.toInt()
    )
}

object PillTheme {
    @Volatile
    var palette: PillPalette = PillPalettes.Default

    @Volatile
    private var figuresTypeface: Typeface? = null

    /** Anonymous Pro — the standing telemetry typeface, loaded from
     *  the bundled TTF (res/font/anonymous_pro). Falls back to the
     *  system monospace if the resource ever fails to load; the
     *  figures must never wait on a font. */
    fun figures(context: Context): Typeface {
        figuresTypeface?.let { return it }
        val loaded = runCatching {
            ResourcesCompat.getFont(context, R.font.anonymous_pro)
        }.getOrNull() ?: Typeface.MONOSPACE
        figuresTypeface = loaded
        return loaded
    }
}
