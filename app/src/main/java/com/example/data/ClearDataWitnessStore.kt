package com.example.data

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore

/**
 * The off-sandbox witness for Audit R2 · tamper-routes finding 1
 * (issue #81): one small file in the user's Documents
 * (`Documents/Monolith/witness.txt`, a MediaStore contribution owned
 * by this app) naming the session currently alive.
 *
 * Why it survives what the finding attacks: `pm clear` / Settings
 * "Clear storage" wipe the app sandbox — Room, prefs, keystore —
 * but not user-visible MediaStore files the app contributed. So on
 * the next fresh seed, a surviving witness over an empty session
 * table is positive evidence that state was destroyed, where the
 * audit found literally none: the app used to resurrect as a silent
 * fresh install.
 *
 * Limits, stated plainly:
 * - MediaStore relative-path contributions need API 29+; below that
 *   every call here is a no-op and clear-data stays undetectable.
 * - A user can delete the Documents file by hand; the witness raises
 *   clear-data from "invisible" to "leaves evidence unless the file
 *   is also hunted down", it is not a vault.
 * - Every call is best-effort and swallowed: the witness must never
 *   be able to break a session, a seed, or a disarm.
 */
class ClearDataWitnessStore(private val context: Context) {

    private val supported: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    private fun collection(): Uri =
        MediaStore.Files.getContentUri("external")

    private fun queryWitnessUri(): Uri? {
        if (!supported) return null
        return runCatching {
            context.contentResolver.query(
                collection(),
                arrayOf(MediaStore.Files.FileColumns._ID),
                "${MediaStore.Files.FileColumns.DISPLAY_NAME} = ? AND " +
                    "${MediaStore.Files.FileColumns.RELATIVE_PATH} = ?",
                arrayOf(FILE_NAME, RELATIVE_PATH),
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val id = cursor.getLong(0)
                    Uri.withAppendedPath(collection(), id.toString())
                } else {
                    null
                }
            }
        }.getOrNull()
    }

    /** Record [sessionId] as the live session. Replaces any prior
     *  witness — only the newest session's destruction matters. */
    fun write(sessionId: Int, birthWallMillis: Long) {
        if (!supported || sessionId <= 0) return
        runCatching {
            clear()
            val values = ContentValues().apply {
                put(MediaStore.Files.FileColumns.DISPLAY_NAME, FILE_NAME)
                put(MediaStore.Files.FileColumns.RELATIVE_PATH, RELATIVE_PATH)
                put(MediaStore.Files.FileColumns.MIME_TYPE, "text/plain")
            }
            val uri = context.contentResolver.insert(collection(), values)
                ?: return@runCatching
            context.contentResolver.openOutputStream(uri)?.use { out ->
                out.write(
                    ClearDataWitness.encode(sessionId, birthWallMillis)
                        .toByteArray(Charsets.UTF_8)
                )
            }
        }
    }

    /** The witnessed session id, or null when no witness survives. */
    fun readSessionId(): Int? {
        if (!supported) return null
        return runCatching {
            val uri = queryWitnessUri() ?: return@runCatching null
            val payload = context.contentResolver.openInputStream(uri)?.use { input ->
                input.readBytes().toString(Charsets.UTF_8)
            }
            ClearDataWitness.parseSessionId(payload)
        }.getOrNull()
    }

    /** The session ended in the open — its witness has served. */
    fun clear() {
        if (!supported) return
        runCatching {
            queryWitnessUri()?.let { context.contentResolver.delete(it, null, null) }
        }
    }

    private companion object {
        const val FILE_NAME = "witness.txt"
        const val RELATIVE_PATH = "Documents/Monolith/"
    }
}
