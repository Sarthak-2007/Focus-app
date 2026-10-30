// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.kotlin.compose) apply false
  alias(libs.plugins.google.devtools.ksp) apply false
  alias(libs.plugins.secrets) apply false
  alias(libs.plugins.google.services) apply false
}

// Issue #73 (audit R2, dep-vulnerabilities): dependency locking is switched
// on in LENIENT mode — a committed gradle.lockfile is honoured wherever one
// exists, and no build fails while lockfiles are still being generated.
// Strict lock-state generation (`--write-locks`) is the priced follow-up.
subprojects {
  dependencyLocking {
    lockAllConfigurations()
    lockMode.set(LockMode.LENIENT)
  }
}
