package com.example.service

/**
 * Issue #275 (Audit R5 supplement — Blockit head-to-head, the
 * keyguard line): explicit classification of the surfaces the
 * accessibility door meets.
 *
 * The audit's charge, verified at the time: Monolith had no
 * keyguard awareness at all — lockscreen / PIN / biometric windows
 * were avoided only by a blanket `com.android.systemui` package
 * skip, repeated ad hoc at every door site, with the lockscreen
 * nowhere named as a class of its own.
 *
 * This classifier is the named home for that law. It changes no
 * behaviour: the family test below is the historical prefix test,
 * byte-identical, and every door site that skipped a system-UI
 * surface still skips exactly the same packages. What is new is
 * that the surfaces are CLASSIFIED — the keyguard (the lockscreen
 * and its PIN entry, up while [keyguardLocked]) is a first-class
 * outcome distinct from generic system chrome (shade, recents,
 * and the biometric prompt's host window while the device is
 * unlocked), and the classification is unit-tested instead of
 * being re-derived from a prefix at each site.
 *
 * Pure — the caller reads the platform's keyguard state and
 * passes it in — so the whole table is JVM-testable.
 */
enum class WindowSurface {
    /** A real app window — the only surface the door processes. */
    APP,

    /** Monolith's own windows — exempt however they arrive. */
    OWN,

    /** The lockscreen / PIN surface: system UI while the
     *  keyguard is locked. Named, and avoided as itself. */
    KEYGUARD,

    /** Other system surfaces that are never an app the user
     *  opened: the shade, recents, the permission controller,
     *  the android system surface, and the biometric prompt's
     *  host while the device is unlocked. */
    SYSTEM
}

object WindowSurfaceClassifier {

    /**
     * The system-UI family test, kept byte-identical to the
     * prefix test the door has always used (`com.android.systemui`
     * and anything under it). One named home for it now.
     */
    fun isSystemUiFamily(packageName: String): Boolean =
        packageName.startsWith("com.android.systemui")

    fun classify(
        packageName: String,
        ownPackage: String,
        keyguardLocked: Boolean
    ): WindowSurface = when {
        packageName == ownPackage -> WindowSurface.OWN
        isSystemUiFamily(packageName) && keyguardLocked -> WindowSurface.KEYGUARD
        isSystemUiFamily(packageName) -> WindowSurface.SYSTEM
        packageName == "android" ||
            packageName == "com.android.permissioncontroller" ||
            packageName == "com.google.android.permissioncontroller" ->
            WindowSurface.SYSTEM
        else -> WindowSurface.APP
    }

    /** True only for a real app window — the door's process gate. */
    fun isAppSurface(
        packageName: String,
        ownPackage: String,
        keyguardLocked: Boolean
    ): Boolean = classify(packageName, ownPackage, keyguardLocked) == WindowSurface.APP
}
