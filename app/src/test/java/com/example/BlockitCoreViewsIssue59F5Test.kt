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
 *
 * ONE DISCLOSED ADJUSTMENT to the taken test (BUENDEL-BAU-3B):
 * the stranded original's first segment cancelled the animator
 * DIRECTLY (springBackAnimatorForTest!!.cancel()). No production
 * code can do that — the animator is private, exposed only by
 * this test hook — and Android's Cancel->End dispatch then
 * legitimately consumes the still-pending callback through the
 * gated onAnimationEnd: the verbatim test FAILED at that assert
 * on PR #550 run 41087939123 attempt 2 (933 tests executed;
 * AssertionError at the first assertEquals). The gate — the
 * stranded form, ported unchanged — guards the view's OWN cancel
 * paths, which clear the callback before cancelling. Segment 1
 * therefore exercises the finding's headline real cancel path
 * instead: detach (evidence note on #59: detach at the old
 * :2851), via the same reflective detach() helper the stranded
 * file used for its F1 test. Segments 2 (resetToRest silent
 * cancel) and 3 (natural end fires exactly once) are verbatim.
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

    private fun detach(v: View) {
        val m = View::class.java.getDeclaredMethod("onDetachedFromWindow")
        m.isAccessible = true
        m.invoke(v)
    }

    // Finding 5: spring-back onEnd fires exactly once on natural end, never on cancel.
    @Test
    fun slideSpringBackCancelIsSilentAndEndFiresOnce() {
        val v = slide()
        var ends = 0
        v.animateSpringBackForTest { ends++ }
        detach(v) // the view's own detach cancel path — silent
        assertEquals(0, ends)
        v.animateSpringBackForTest { ends++ }
        v.resetToRest() // cancels silently
        assertEquals(0, ends)
        v.animateSpringBackForTest { ends++ }
        v.springBackAnimatorForTest!!.end()
        assertEquals(1, ends)
    }
}
