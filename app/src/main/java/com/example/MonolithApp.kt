package com.example

import android.app.Application
import android.content.Context
import java.io.File

/**
 * Application entry (issue #75, audit R2 · logging-debug).
 *
 * Exists so process-level truth machinery is installed before any
 * activity or service runs: the uncaught-exception capture below and
 * the [MLog] boot line. Registered as android:name=".MonolithApp".
 */
class MonolithApp : Application() {
    override fun onCreate() {
        super.onCreate()
        CrashCapture.install(this)
        DebugStrictMode.install()
        // Audit R6 #341 F56: every SharedPreferences file carries
        // its schema version from this process start on.
        com.example.data.PrefsSchema.stampAll(this)
        MLog.i("process start (debug=${BuildConfig.DEBUG})")
    }
}

/**
 * Issue #272 (Build quality): StrictMode, debug builds only.
 *
 * The audit's charge: no StrictMode policy existed anywhere, so
 * main-thread disk/network work and leaked closeables in
 * development were invisible — the exact class of jank a blocker
 * cannot afford on the arm path. The policy is LOG-ONLY
 * (penaltyLog, never penaltyDeath): violations surface in the
 * log for the developer, they never crash the user's — or a
 * test's — process, and release builds install nothing at all.
 */
object DebugStrictMode {

    fun install() {
        if (!BuildConfig.DEBUG) return
        runCatching {
            android.os.StrictMode.setThreadPolicy(threadPolicy())
            android.os.StrictMode.setVmPolicy(vmPolicy())
        }
    }

    internal fun threadPolicy(): android.os.StrictMode.ThreadPolicy =
        android.os.StrictMode.ThreadPolicy.Builder()
            .detectAll()
            .penaltyLog()
            .build()

    internal fun vmPolicy(): android.os.StrictMode.VmPolicy =
        android.os.StrictMode.VmPolicy.Builder()
            .detectAll()
            .penaltyLog()
            .build()
}

/**
 * The uncaught path, made visible (issue #75).
 *
 * Before this, the app had no setDefaultUncaughtExceptionHandler
 * anywhere: a crash left no trace a user or developer could ever read.
 * Now an uncaught failure is (1) logged through [MLog], (2) written
 * synchronously to filesDir/last-crash.txt so the NEXT launch can see
 * that the last process died and why, and (3) handed to the previous
 * handler untouched — capture never swallows, never replaces the
 * platform's own crash behaviour.
 */
object CrashCapture {
    private var installed = false

    fun install(context: Context) {
        if (installed) return
        installed = true
        val appContext = context.applicationContext
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                MLog.e("UNCAUGHT on ${thread.name}", throwable)
                File(appContext.filesDir, "last-crash.txt").writeText(
                    "wallMillis=${System.currentTimeMillis()} thread=${thread.name}\n" +
                        throwable.stackTraceToString()
                )
            } catch (_: Exception) {
                // The crash path must never throw: fall through to the
                // previous handler exactly as if we were not here.
            }
            previous?.uncaughtException(thread, throwable)
        }
    }
}
