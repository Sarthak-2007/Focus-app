package com.example

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #272 (Build quality): the boot wiring for the StrictMode
 * debug policy. StrictModePolicyTest pins that the policies build
 * and that install() never throws; what it cannot see is the
 * wiring — that MonolithApp.onCreate actually calls install() at
 * every process start. Delete that one line and every behavioural
 * test still passes while no debug process ever installs the
 * policy. These pins guard the wiring itself, in the fleet's
 * source-pin idiom: the call must sit inside the brace-matched
 * onCreate body, and the release gate must stand at the top of
 * install() so release builds install nothing.
 */
class MonolithAppTest {

    private fun appSource(): String = listOf(
        File("src/main/java/com/example/MonolithApp.kt"),
        File("app/src/main/java/com/example/MonolithApp.kt"),
        File("../app/src/main/java/com/example/MonolithApp.kt")
    ).firstOrNull { it.exists() }?.readText()
        ?: error("MonolithApp.kt not found from ${File(".").absolutePath}")

    /** The brace-matched body of the first function whose
     *  declaration contains [decl] — braces inside the body's one
     *  string template are balanced, so plain depth counting lands
     *  on the body's own closing brace (the PR #689 pin shape). */
    private fun functionBody(source: String, decl: String): String {
        val fnIdx = source.indexOf(decl)
        assertTrue("declaration missing: $decl", fnIdx >= 0)
        val openBrace = source.indexOf('{', fnIdx)
        assertTrue("no opening brace after: $decl", openBrace >= 0)
        var depth = 0
        var end = openBrace
        while (end < source.length) {
            when (source[end]) {
                '{' -> depth++
                '}' -> {
                    depth--
                    if (depth == 0) break
                }
            }
            end++
        }
        assertTrue("unbalanced braces in body of: $decl", depth == 0)
        return source.substring(openBrace, minOf(end + 1, source.length))
    }

    @Test
    fun onCreateInstallsTheDebugStrictModePolicy() {
        val body = functionBody(appSource(), "fun onCreate(")
        assertTrue(
            "onCreate must install the StrictMode debug policy",
            body.contains("DebugStrictMode.install()")
        )
    }

    @Test
    fun installIsReleaseGated() {
        val body = functionBody(appSource(), "fun install()")
        assertTrue(
            "install() must return before touching StrictMode when not DEBUG",
            body.contains("if (!BuildConfig.DEBUG) return")
        )
    }
}
