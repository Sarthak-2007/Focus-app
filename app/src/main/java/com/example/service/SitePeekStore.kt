package com.example.service

/**
 * W3 #130 (browser-as-loophole, findings 1 + 4): the site peek's
 * domain tag. A peek taken on the SITE door is a lease on ONE
 * domain in ONE browser package — never a blanket amnesty for the
 * browser. The lease's time and cooldown live on the browser app's
 * Room row exactly like an app peek (MonolithRepository.startPeekLease;
 * the floating pill derives its countdown from that row), so this
 * store carries only the scope the row cannot: which domain the
 * lease was granted for.
 *
 * In-memory only, by design: if the process dies the tag dies with
 * it and the site blocker fails CLOSED (every blocked domain is
 * intercepted again), never open.
 */
object SitePeekStore {

    @Volatile
    var domain: String? = null
        private set

    @Volatile
    var packageName: String? = null
        private set

    fun grant(domain: String, packageName: String) {
        this.domain = domain
        this.packageName = packageName
    }

    /** Clears the tag when the lease for [packageName] ends. A tag
     *  for a different package is not this lease's to clear. */
    fun clearIf(packageName: String) {
        if (this.packageName == packageName) {
            domain = null
            this.packageName = null
        }
    }

    fun resetForTest() {
        domain = null
        packageName = null
    }
}

/**
 * The site blocker's exemption test, pure: a live lease exempts
 * ONLY the exact (package, domain) pair it was granted for, and
 * only while the Room lease end is still ahead of [nowMono]. An
 * app-tier peek on the browser grants no tag, so it exempts
 * nothing — the site rules stay live through it.
 */
internal fun sitePeekExempts(
    peekedDomain: String?,
    peekedPackage: String?,
    domain: String,
    packageName: String,
    leaseUntilMono: Long,
    nowMono: Long
): Boolean =
    peekedDomain != null &&
        peekedDomain == domain &&
        peekedPackage == packageName &&
        leaseUntilMono > 0L &&
        nowMono < leaseUntilMono
