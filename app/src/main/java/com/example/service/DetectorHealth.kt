package com.example.service

/**
 * Issue #335 (Audit R5 supplement — Freedom F5): Freedom's detector
 * reports its own health (first/last node, last block, slow-scan
 * warning); Monolith's detector had no health surface at all — a
 * dead detector and a healthy one presented identically, which is a
 * TELL-THE-TRUTH failure on the app's most important promise.
 *
 * This record is the health surface's data core. It is kept pure
 * (monotonic stamps passed in, no Android calls) so the staleness
 * and slow-scan logic is unit-testable on the JVM. The stamps are
 * in-memory only, like the Live Wire registry: persisted health
 * proof would be stale proof.
 */
data class DetectorHealthSnapshot(
    val firstNodeMono: Long = 0L,
    val lastNodeMono: Long = 0L,
    val lastBlockMono: Long = 0L,
    val lastBlockSuccessMono: Long = 0L,
    val nodeCount: Long = 0L,
    val blockCount: Long = 0L,
    val slowScanCount: Long = 0L,
    val lastScanDurationMs: Long = -1L
) {
    /** The detector is stale when it once saw nodes and has now
     *  been silent longer than [staleAfterMs] — the state the guard
     *  line must surface instead of letting a silent detector
     *  impersonate a working one. A detector that has NEVER seen a
     *  node is not "stale", it is "never proved" ([hasProved]). */
    fun isStale(nowMono: Long, staleAfterMs: Long): Boolean =
        lastNodeMono > 0L && nowMono - lastNodeMono > staleAfterMs

    val hasProved: Boolean get() = lastNodeMono > 0L
    val lastScanWasSlow: Boolean get() = lastScanDurationMs > SLOW_SCAN_THRESHOLD_MS

    companion object {
        /** Freedom flags a health failure when node checking
         *  exceeds 100 ms; Monolith adopts the same line. */
        const val SLOW_SCAN_THRESHOLD_MS: Long = 100L

        /** During an armed session the accessibility pipeline sees
         *  window events constantly; 30 s of silence mid-session is
         *  a detector that stopped, not a phone that went quiet. */
        const val STALE_AFTER_MS: Long = 30_000L
    }
}

/** Mutable recorder the services write into; readers take an
 *  immutable [DetectorHealthSnapshot]. */
object DetectorHealth {

    @Volatile
    private var state = DetectorHealthSnapshot()

    @Synchronized
    fun recordNode(nowMono: Long, scanDurationMs: Long) {
        val s = state
        state = s.copy(
            firstNodeMono = if (s.firstNodeMono == 0L) nowMono else s.firstNodeMono,
            lastNodeMono = nowMono,
            nodeCount = s.nodeCount + 1,
            slowScanCount = s.slowScanCount +
                if (scanDurationMs > DetectorHealthSnapshot.SLOW_SCAN_THRESHOLD_MS) 1 else 0,
            lastScanDurationMs = scanDurationMs
        )
    }

    @Synchronized
    fun recordBlock(nowMono: Long, success: Boolean) {
        val s = state
        state = s.copy(
            lastBlockMono = nowMono,
            lastBlockSuccessMono = if (success) nowMono else s.lastBlockSuccessMono,
            blockCount = s.blockCount + 1
        )
    }

    fun snapshot(): DetectorHealthSnapshot = state

    @Synchronized
    fun reset() {
        state = DetectorHealthSnapshot()
    }
}
