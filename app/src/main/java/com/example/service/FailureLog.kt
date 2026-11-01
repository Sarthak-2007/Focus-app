package com.example.service

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import java.io.File

/**
 * Audit R6 #340 F61: the scrubbed, persistent failure log.
 *
 * The pre-cure state: failures were swallowed at ~75 catch sites
 * or, at best (issues #33/#76), shouted into logcat — a sink that
 * rotates away in hours and that a user can never hand to anyone.
 * Freedom runs the real pipeline (rate-limited file logger,
 * scrubber, capped files) but ships it sloppy: verbose package /
 * URL logging in release builds and a remotely-supplied syslog
 * destination. This log takes the architecture and refuses the
 * sloppiness:
 *
 *  - SCRUB BEFORE WRITE, not before send: a line carries only a
 *    wall timestamp, a component, an event kind, and the
 *    exception's CLASS — never its message (messages carry URLs,
 *    package names, notification text), never any content. The
 *    file on disk is already safe to hand over.
 *  - The file is the sink. In release builds this object emits
 *    NOTHING to logcat; in debug builds it mirrors there.
 *  - Capped (256 KB, oldest lines pruned at write) and resident
 *    in app-private internal storage, which Android backup
 *    excludes per the app's data-extraction rules. It leaves the
 *    device only inside a user-initiated export — there is no
 *    remote destination, configured or otherwise.
 *  - Rate-limited per (component, event): a failure in a hot loop
 *    writes at most one line per minute per kind, with the
 *    suppressed count folded into the next written line.
 */
object FailureLog {

    private const val MAX_BYTES = 256 * 1024L
    private const val RATE_WINDOW_MS = 60_000L
    private const val FILE_NAME = "failure-log.txt"

    @Volatile
    private var appContext: Context? = null

    private val lock = Any()
    private val lastWrittenAt = HashMap<String, Long>()
    private val suppressed = HashMap<String, Int>()

    fun bind(context: Context) {
        if (appContext == null) {
            appContext = context.applicationContext
        }
    }

    /** The log file, for a user-initiated export to read. Null
     *  until [bind] has run in this process. */
    fun file(): File? = appContext?.let { File(it.filesDir, FILE_NAME) }

    fun record(component: String, event: String, error: Throwable?) {
        if (BuildConfig.DEBUG) {
            Log.w("FailureLog", "$component/$event", error)
        }
        val ctx = appContext ?: return
        try {
            synchronized(lock) {
                val now = System.currentTimeMillis()
                val key = "$component/$event"
                val last = lastWrittenAt[key] ?: 0L
                if (now - last < RATE_WINDOW_MS) {
                    suppressed[key] = (suppressed[key] ?: 0) + 1
                    return
                }
                val folded = suppressed.remove(key) ?: 0
                lastWrittenAt[key] = now
                val exName = error?.javaClass?.simpleName ?: "-"
                val line = buildString {
                    append(now).append('\t').append(component).append('\t')
                        .append(event).append('\t').append(exName)
                    if (folded > 0) append("\t(+").append(folded).append(" suppressed)")
                    append('\n')
                }
                val f = File(ctx.filesDir, FILE_NAME)
                f.appendText(line)
                if (f.length() > MAX_BYTES) {
                    val lines = f.readLines()
                    val keep = lines.drop(lines.size / 2)
                    f.writeText(keep.joinToString(separator = "\n", postfix = "\n"))
                }
            }
        } catch (_: Exception) {
            // The failure log must never become a failure source.
        }
    }
}
