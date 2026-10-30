package com.example

import android.util.Log

/**
 * The app's one logging voice (issue #75, audit R2 · logging-debug).
 *
 * Until now the shipped app contained zero log statements across ~24.6k
 * lines: every swallowed failure in the audit was permanently
 * undiagnosable from any shipped build — no logcat line, no crash
 * report, no field repro. This object is the facility that was missing:
 *
 * - `d` speaks only in DEBUG builds (BuildConfig.DEBUG finally read).
 * - `i` marks lifecycle facts worth seeing in any build's logcat.
 * - `w` / `e` always speak: a warning or an error that cannot be seen
 *   does not exist. New failure handling must route through here (or
 *   through [CrashCapture] for the uncaught path) — never through a
 *   bare, silent catch.
 */
object MLog {
    private const val TAG = "Monolith"

    fun d(message: String) {
        if (BuildConfig.DEBUG) Log.d(TAG, message)
    }

    fun i(message: String) {
        Log.i(TAG, message)
    }

    fun w(message: String, throwable: Throwable? = null) {
        if (throwable != null) Log.w(TAG, message, throwable) else Log.w(TAG, message)
    }

    fun e(message: String, throwable: Throwable? = null) {
        if (throwable != null) Log.e(TAG, message, throwable) else Log.e(TAG, message)
    }
}
