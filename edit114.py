#!/usr/bin/env python3
"""STURM3-11 edit-bot for agent/issue-114 (run on a GitHub runner).
Removes the merge-created DUPLICATE peek launch in start2MinutePeek:
main's Issue #99 F4 synchronous launch block (fires BEFORE the lease
row exists) is dropped; issue #114's post-lease launch is kept, with
Issue #75 F1's MLog failure wiring grafted onto it. Both edits are
scoped to the start2MinutePeek function slice and assert exact-once
matches; anything unexpected aborts without writing."""
import pathlib, subprocess, sys

VM = pathlib.Path("app/src/main/java/com/example/ui/MonolithViewModel.kt")
src = VM.read_text()
start = src.index("    fun start2MinutePeek() {")
end = src.index("    fun end2MinutePeekEarly() {", start)
fn = src[start:end]

MAIN_BLOCK = """        // Issue #99 F4 (HAND-MERGE: Konto batch-10 flag on this
        // function — keep this block minimal): taking the peek takes
        // you to the app. The lease no longer burns while the user
        // navigates home and relaunches by hand.
        runCatching {
            appContext.packageManager.getLaunchIntentForPackage(pkg)?.let { launch ->
                launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                appContext.startActivity(launch)
            }
        }.onFailure {
            // Issue #75 F1 wiring: the peek was granted and the
            // pill armed, but the app launch itself failed —
            // silently until now.
            MLog.w("peek launch intent failed for $pkg", it)
        }
"""
if fn.count(MAIN_BLOCK) != 1:
    print(f"ABORT: main launch block count={fn.count(MAIN_BLOCK)}")
    sys.exit(1)
fn = fn.replace(MAIN_BLOCK, """        // Issue #99 F4 + Issue #114 F1 (merge de-duplication by
        // STURM3-11): the launch itself lives below, after
        // startPeekLease writes the lease row — see the comment
        // there. Main's synchronous copy of the same launch is
        // removed so the app is launched exactly once, and never
        // before the lease exists.
""")

BRANCH_BLOCK = """            runCatching {
                val launch = appContext.packageManager.getLaunchIntentForPackage(pkg)
                if (launch != null) {
                    launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    appContext.startActivity(launch)
                }
            }
"""
if fn.count(BRANCH_BLOCK) != 1:
    print(f"ABORT: branch launch block count={fn.count(BRANCH_BLOCK)}")
    sys.exit(1)
fn = fn.replace(BRANCH_BLOCK, """            runCatching {
                val launch = appContext.packageManager.getLaunchIntentForPackage(pkg)
                if (launch != null) {
                    launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    appContext.startActivity(launch)
                }
            }.onFailure {
                // Issue #75 F1 wiring (preserved from main's removed
                // duplicate block): a granted peek whose launch
                // throws must not pass silently.
                MLog.w("peek launch intent failed for $pkg", it)
            }
""")

if fn.count("getLaunchIntentForPackage") != 1:
    print(f"ABORT: launch sites in function after edit = {fn.count('getLaunchIntentForPackage')}")
    sys.exit(1)

VM.write_text(src[:start] + fn + src[end:])
print("EDIT_OK")
