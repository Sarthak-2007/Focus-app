package com.example.service

import android.app.StatusBarManager
import android.content.ComponentName
import android.content.Context
import android.graphics.drawable.Icon
import android.os.Build
import com.example.R
import java.util.concurrent.Executor
import java.util.function.Consumer

/**
 * Issue #274 (Audit R5 — Platform execution), the standing half
 * of the tile-placement line: "no tile-placement prompt … for
 * any quick-access surface". PR #492 built the Quick Settings
 * tile itself; nothing ever offered to place it, so the tile
 * existed only for a user who went hunting through the panel's
 * edit screen.
 *
 * The placement fact has exactly one honest source: the tile
 * service's own lifecycle. [MonolithTileService.onTileAdded] /
 * `onTileRemoved` record it here as they fire, and the system
 * prompt's own answer (added / already added) records it too.
 * The PARK board's TILE line prints ADDED only on that testimony
 * — testimony, not proof, in the AUTOSTART oath row's sense: if
 * the platform ever removes the tile without telling the
 * service, the word stands until the next lifecycle event, and
 * tapping the line re-runs the prompt, whose ALREADY_ADDED /
 * fresh-add answer re-derives the fact.
 *
 * The prompt itself is the system's (Android 13+, API 33):
 * [StatusBarManager.requestAddTileService] shows the platform's
 * own add-tile sheet; the user decides there, never in an
 * in-app imitation. Below API 33 no prompt exists — the line
 * then names the panel's edit screen as the route, and says so.
 */
object TilePlacement {

    private const val PREFS_NAME = "monolith_tile_placement"
    private const val KEY_ADDED = "tile_added"

    /** The board's word for the tile line: ADDED on the recorded
     *  testimony, nothing (the line stands as a lack) otherwise. */
    fun word(added: Boolean): String? = if (added) "ADDED" else null

    /** The system add-tile prompt exists from API 33. */
    fun systemPromptAvailable(sdkInt: Int): Boolean = sdkInt >= 33

    /** Whether the system prompt's answer records the tile as
     *  placed: it was just added, or it was already there. */
    fun resultRecordsAdded(result: Int): Boolean =
        result == StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ADDED ||
            result == StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ALREADY_ADDED

    fun isAdded(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_ADDED, false)

    fun recordAdded(context: Context) = record(context, true)

    fun recordRemoved(context: Context) = record(context, false)

    private fun record(context: Context, added: Boolean) {
        try {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_ADDED, added).apply()
        } catch (_: Exception) {
            // A lost write costs the board word, nothing else.
        }
    }

    /**
     * Fire the system's add-tile prompt. No-op below API 33
     * (callers print the edit-screen route instead). The answer
     * arrives on [executor]; an added / already-added answer is
     * recorded before [onResult] runs, so any surface reading
     * the fact afterwards reads it fresh.
     */
    fun requestAdd(
        context: Context,
        executor: Executor,
        onResult: (Int) -> Unit = {}
    ) {
        if (!systemPromptAvailable(Build.VERSION.SDK_INT)) return
        try {
            val manager = context.getSystemService(StatusBarManager::class.java)
                ?: return
            val appContext = context.applicationContext
            manager.requestAddTileService(
                ComponentName(appContext, MonolithTileService::class.java),
                appContext.getString(R.string.tile_label),
                Icon.createWithResource(appContext, R.mipmap.ic_launcher),
                executor,
                Consumer { result: Int ->
                    if (resultRecordsAdded(result)) recordAdded(appContext)
                    onResult(result)
                }
            )
        } catch (_: Exception) {
            // No status bar service (or a refused call): the
            // line stands as it was; the user keeps the panel's
            // edit screen as the manual route either way.
        }
    }
}
