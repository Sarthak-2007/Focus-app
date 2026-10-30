package com.example

import android.view.View
import com.example.ui.views.SlideToStartView
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.GraphicsMode

/**
 * Issue #59 (AUDIT-R1 blockit-core-views), finding 5 ONLY — the
 * spring-back double-fire remainder.
 *
 * Ported from the stranded branch commit 25773fbc
 * (BlockitCoreViewsIssue59Test.kt, STURM-2's net-merge): of that
 * file, only the F5 test is taken here — F1–F4 are confirmed cured
 * on main by other PRs and their tests/hooks are not re-landed.
 * Taken: slideSpringBackCancelIsSilentAndEndFiresOnce (+ its
 * slide() helper). Left: the pure-math tests, the F1 rotary-detach
 * test, the F4 measure/SavedState/accessibility tests, and the
 * draw-census tests.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class Issue59F5ViewTest {

    private fun slide(): SlideToStartView {
        val ctx = RuntimeEnvironment.getApplication()
        ctx.resources.displayMetrics.density = 3.0f
        val v = SlideToStartView(ctx)
        v.measure(
            View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(216, View.MeasureSpec.EXACTLY)
        )
        v.layout(0, 0, 1080, 216)
        return v
    }

    // Finding 5: spring-back onEnd fires exactly once on natural end, never on cancel.
    @Test
    fun slideSpringBackCancelIsSilentAndEndFiresOnce() {
        val v = slide()
        var ends = 0
        v.animateSpringBackForTest { ends++ }
        v.springBackAnimatorForTest!!.cancel()
        assertEquals(0, ends)
        v.animateSpringBackForTest { ends++ }
        v.resetToRest() // cancels silently
        assertEquals(0, ends)
        v.animateSpringBackForTest { ends++ }
        v.springBackAnimatorForTest!!.end()
        assertEquals(1, ends)
    }
}
