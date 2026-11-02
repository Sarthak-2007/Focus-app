package com.example

import com.example.ui.strongroom.CompositionDraft
import com.example.ui.strongroom.SigningAppOption
import com.example.ui.strongroom.SigningState
import com.example.ui.strongroom.SigningStep
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.lang.reflect.Modifier

/**
 * C-4 — THE SIGNING SEQUENCE ADMITS ZERO LAWFUL INSERTIONS,
 * asserted exhaustively (ledger C-4: insertion figure 0,
 * adjacency-tested; Stage 4). Landing: faces tree, with P4
 * (ui/strongroom/SigningModel.kt).
 *
 * The lane's own StrongroomSigningModelTest proves the sequence
 * by example. This test proves it by exhaustion — the form C-4
 * demands, because C-4 is a law about what CANNOT happen:
 *
 *  1. API CENSUS — SigningState's whole public act surface is
 *     exactly the seven ruled acts. An eighth public way to move
 *     the state IS an insertion surface, whatever it is named.
 *  2. FIELD CENSUS — the state carries exactly its eight fields
 *     and none of them can hold a combination, a seal, or a
 *     swing (A-2: the combination is written nowhere; the swing
 *     is the seal act's, and no field here stands for it).
 *  3. ADJACENCY BY SEARCH — every act, applied in every order
 *     to depth 7, produces step-edges inside the ruled adjacency
 *     alone: COMPOSITION→HOUR→SENTENCE→COMBINATION→READY, one
 *     step at a time, never back, never skipping. A result
 *     exists only at READY, and never in fewer than the five
 *     acts the chain minimally takes.
 *  4. THE STANDSTILL MATRIX — at every step, every act that does
 *     not belong to that step either returns the state untouched
 *     or refuses (throws). Nothing foreign moves the sequence.
 *
 * If any of this fails, the build is wrong — not the test.
 */
class SigningModelAdjacencyC4Test {

    private fun draft() = CompositionDraft(
        dayShapeName = "ordinary",
        appOptions = listOf(
            SigningAppOption("com.social", "Social"),
            SigningAppOption("com.video", "Video"),
            SigningAppOption("com.notes", "Notes")
        ),
        composedLocked = setOf("com.social", "com.video"),
        composedHour = "06:30",
        composedSentence = null,
        spentDay = false
    )

    /** Every public act the flow exposes, by name — the complete
     *  vocabulary an insertion could be attempted in. A null
     *  result means the act refused (threw): a refusal is not a
     *  transition. */
    private val acts: List<Pair<String, (SigningState) -> SigningState?>> = listOf(
        "advance" to { s -> s.advance() },
        "beginSignature" to { s -> s.beginSignature() },
        "completeSignature" to { s ->
            try {
                s.completeSignature()
            } catch (refused: IllegalStateException) {
                null
            }
        },
        "toggleKnown" to { s -> s.toggleApp("com.notes") },
        "toggleUnknown" to { s -> s.toggleApp("com.unknown") },
        "setHour" to { s -> s.setHour("07:00") },
        "setSentence" to { s -> s.setSentence("his words") },
        "setTaskLine" to { s -> s.setTaskLine("the block's reason") }
    )

    // ---------------------------------------------------------------
    // 1 · API census — the act surface is exactly the ruled seven
    // ---------------------------------------------------------------

    @Test
    fun apiSurface_isExactlyTheSevenRuledActs() {
        val actMethods = SigningState::class.java.declaredMethods
            .filter { m ->
                !m.isSynthetic &&
                    Modifier.isPublic(m.modifiers) &&
                    m.returnType == SigningState::class.java &&
                    m.name != "copy"
            }
            .map { it.name }
            .toSet()
        assertEquals(
            setOf(
                "toggleApp", "setHour", "setSentence", "setTaskLine",
                "advance", "beginSignature", "completeSignature"
            ),
            actMethods
        )
    }

    // ---------------------------------------------------------------
    // 2 · Field census — no field can hold a combination or a swing
    // ---------------------------------------------------------------

    @Test
    fun stateFields_areExactlyTheEight_andNoneHoldsACombinationOrSwing() {
        val fields = SigningState::class.java.declaredFields
            .filter {
                !it.isSynthetic &&
                    !Modifier.isStatic(it.modifiers) &&
                    !it.name.startsWith("$")
            }
            .map { it.name }
            .sorted()
        assertEquals(
            listOf(
                "draft", "liftingHour", "lockedPackages", "result",
                "sentence", "signatureBegun", "step", "taskLine"
            ),
            fields
        )
        val forbidden = listOf("digit", "pin", "seal", "swing", "bolt", "clunk", "code")
        fields.forEach { name ->
            forbidden.forEach { token ->
                assertFalse(
                    "field '$name' must never hold '$token' substance (A-2)",
                    name.contains(token, ignoreCase = true)
                )
            }
        }
    }

    // ---------------------------------------------------------------
    // 3 · Adjacency by search — no edge skips, reverses, or inserts
    // ---------------------------------------------------------------

    @Test
    fun search_everyActInEveryOrder_staysInsideTheRuledAdjacency() {
        val ruledEdges = setOf(
            SigningStep.COMPOSITION to SigningStep.HOUR,
            SigningStep.HOUR to SigningStep.SENTENCE,
            SigningStep.SENTENCE to SigningStep.COMBINATION,
            SigningStep.COMBINATION to SigningStep.READY
        )
        // Level-order search over the flow's whole reachable
        // state space (it is small and finite: every amendment
        // toggles between filed values). Every act is applied to
        // every reached state — every interleaving the flow can
        // be subjected to is walked, not sampled.
        val start = SigningState(draft())
        val visited = mutableSetOf(start)
        val seenEdges = mutableSetOf<Pair<SigningStep, SigningStep>>()
        var frontier = listOf(start)
        var level = 0
        var firstResultLevel = -1
        while (frontier.isNotEmpty()) {
            val nextFrontier = mutableListOf<SigningState>()
            frontier.forEach { state ->
                if (state.result != null) {
                    assertEquals(
                        "a result exists only at READY",
                        SigningStep.READY, state.step
                    )
                    assertTrue(
                        "a result implies the signature was begun",
                        state.signatureBegun
                    )
                    if (firstResultLevel < 0) firstResultLevel = level
                }
                acts.forEach { (_, act) ->
                    val next = act(state) ?: return@forEach
                    if (next.step != state.step) {
                        seenEdges += state.step to next.step
                        // Every step edge climbs by exactly one
                        // rank — none skips, none reverses.
                        assertEquals(
                            "step edges climb by one: ${state.step} -> ${next.step}",
                            state.step.ordinal + 1, next.step.ordinal
                        )
                    }
                    if (visited.add(next)) nextFrontier += next
                }
            }
            frontier = nextFrontier
            level++
        }

        assertTrue("the chain must be completable", firstResultLevel >= 0)
        assertEquals(
            "every ruled edge is reachable — and no other edge exists",
            ruledEdges, seenEdges
        )
        assertEquals(
            "the shortest signing is the five acts of the chain itself " +
                "(3 advances + begin + complete) — nothing shorter signs",
            5, firstResultLevel
        )
    }

    // ---------------------------------------------------------------
    // 4 · The standstill matrix — foreign acts never move a step
    // ---------------------------------------------------------------

    /** Canonical states, one per reachable step/posture. */
    private fun canonicalStates(): Map<String, SigningState> {
        val s0 = SigningState(draft())
        val s1 = s0.advance()
        val s2 = s1.advance()
        val s3 = s2.advance()
        val s3Begun = s3.beginSignature()
        val ready = s3Begun.completeSignature()
        return mapOf(
            "COMPOSITION" to s0,
            "HOUR" to s1,
            "SENTENCE" to s2,
            "COMBINATION" to s3,
            "COMBINATION_BEGUN" to s3Begun,
            "READY" to ready
        )
    }

    /** The acts that BELONG to each posture (advance belongs to
     *  the three advancing steps; at COMBINATION it stands still
     *  by the model's own law and at READY everything does). */
    private val belonging: Map<String, Set<String>> = mapOf(
        "COMPOSITION" to setOf("toggleKnown", "toggleUnknown", "advance"),
        "HOUR" to setOf("setHour", "advance"),
        "SENTENCE" to setOf("setSentence", "setTaskLine", "advance"),
        "COMBINATION" to setOf("beginSignature", "completeSignature"),
        "COMBINATION_BEGUN" to setOf("completeSignature"),
        "READY" to emptySet()
    )

    @Test
    fun standstill_everyForeignActAtEveryStep_movesNothing() {
        canonicalStates().forEach { (posture, state) ->
            acts.forEach { (actName, act) ->
                if (actName in belonging.getValue(posture)) return@forEach
                val outcome = try {
                    act(state)
                } catch (refused: IllegalStateException) {
                    null // a refusal is lawful stillness
                } catch (refused: IllegalArgumentException) {
                    null
                }
                if (outcome != null) {
                    assertEquals(
                        "$actName at $posture is a foreign act: it must " +
                            "stand still — a state change here IS an insertion",
                        state, outcome
                    )
                }
            }
        }
    }

    @Test
    fun ready_isTerminal_underEveryAct() {
        val ready = canonicalStates().getValue("READY")
        assertEquals(SigningStep.READY, ready.step)
        assertFalse(ready.canLeave)
        acts.forEach { (actName, act) ->
            val outcome = try {
                act(ready)
            } catch (refused: IllegalStateException) {
                null
            }
            if (outcome != null) {
                assertEquals("READY admits no further act ($actName)", ready, outcome)
            }
        }
        // And READY cannot be fabricated directly into a signed
        // result by construction tricks the census would miss:
        // a state built at READY by hand carries no result.
        assertEquals(
            null,
            SigningState(draft(), step = SigningStep.READY).result
        )
        try {
            SigningState(draft(), step = SigningStep.READY).completeSignature()
            fail("completion away from a begun combination must refuse")
        } catch (expected: IllegalStateException) {
            // refused — the sequence holds
        }
    }
}
