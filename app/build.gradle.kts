import com.google.gms.googleservices.GoogleServicesPlugin.MissingGoogleServicesStrategy

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.google.services)
}

// Ren's ruling, issue #25 (DURCH-VC, Oct 30 2026): versionCode is
// EPOCH-MINUTES — minutes since the Unix epoch, computed here in the
// build at build time, NEVER hand-set. It can never go lower than any
// previous build (a later build is always a later minute), which kills
// the L16 class permanently across parallel lanes/trains: no merged
// tree can resurrect a lower hand-set value. Supersedes the hand-set
// vc2 (and batch-9's vc1) upward. Scale: Oct 2026 ≈ 29.9 million;
// Int.MAX_VALUE (2,147,483,647) is not reached for ≈ 4,026 years
// (≈ year 6053), and Play's 2,100,000,000 cap is millennia away too.
// This defaultConfig is the ONE definition point: every build route
// (local assemble, CI debug, CI release proof, screenshots) inherits
// it — no flavor, variant, or workflow override exists.
val epochMinutesVersionCode: Int = (System.currentTimeMillis() / 60_000L).toInt()

android {
  namespace = "com.example"
  // compileSdk/targetSdk 37 (was 36.1/36): compose-bom 2026.09.00
  // (Compose 1.12.1) declares minCompileSdk=37 in its AAR metadata —
  // this raise landed ahead of the BOM bump (PR #213) so the bump
  // compiles. REFIT-DEPS-RAISE, Oct 31 2026; determination on #245.
  compileSdk { version = release(37) }

  defaultConfig {
    applicationId = "com.aistudio.monolith.kxtpzr"
    minSdk = 24
    targetSdk = 37
    versionCode = epochMinutesVersionCode
    versionName = "1.1"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  // Do not remove or modify these signingConfigs. They are necessary for building, installing,
  // and updating Android apps in AI Studio.
  signingConfigs {
    create("release") {
      val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/my-upload-key.jks"
      storeFile = file(keystorePath)
      storePassword = System.getenv("STORE_PASSWORD")
      keyAlias = "upload"
      keyPassword = System.getenv("KEY_PASSWORD")
    }
    create("debugConfig") {
      storeFile = file("${rootDir}/debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      // Do not remove or modify this signingConfig assignment. It is necessary for Android apps in
      // AI Studio.
      signingConfig = signingConfigs.getByName("release")
    }
    debug {
      // Do not remove or modify this signingConfig assignment. It is necessary for Android apps in
      // AI Studio.
      signingConfig = signingConfigs.getByName("debugConfig")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    // Audit R6 #341 F02: java.time (DrawerDay, MonolithWeek, the
    // schedule layer) is API 26+; without desugaring, minSdk 24
    // devices died with NoClassDefFoundError at ViewModel
    // construction. Desugaring backfills java.time on API 24-25.
    isCoreLibraryDesugaringEnabled = true
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions { unitTests { isIncludeAndroidResources = true } }
  dependenciesInfo {
    includeInApk = false
    includeInBundle = true
  }
}

// Finisher (Klappe): JDK 21 strong encapsulation blocks Robolectric's
// AndroidInterceptors reflection (IllegalAccessException at sandbox
// start, every Robolectric test dead). Open the JDK modules Robolectric
// documents for JDK 21 test workers.
tasks.withType<Test>().configureEach {
  jvmArgs(
    "--add-opens=java.base/java.lang=ALL-UNNAMED",
    "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
    "--add-opens=java.base/java.io=ALL-UNNAMED",
    "--add-opens=java.base/java.net=ALL-UNNAMED",
    "--add-opens=java.base/java.nio=ALL-UNNAMED",
    "--add-opens=java.base/java.nio.charset=ALL-UNNAMED",
    "--add-opens=java.base/java.security=ALL-UNNAMED",
    "--add-opens=java.base/java.text=ALL-UNNAMED",
    "--add-opens=java.base/java.time=ALL-UNNAMED",
    "--add-opens=java.base/java.util=ALL-UNNAMED",
    "--add-opens=java.base/java.util.concurrent=ALL-UNNAMED",
    "--add-opens=java.base/java.util.concurrent.atomic=ALL-UNNAMED",
    "--add-opens=java.base/java.util.zip=ALL-UNNAMED",
    "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED",
    "--add-opens=java.base/sun.nio.cs=ALL-UNNAMED",
    "--add-opens=java.base/sun.security.ssl=ALL-UNNAMED",
    "--add-opens=java.base/sun.security.util=ALL-UNNAMED",
    "--add-opens=java.desktop/java.awt.font=ALL-UNNAMED",
    // BUILDER-TRAIN-4 triage (PR #231, run 41105848855): add-opens does
    // not substitute for add-exports - Robolectric's sandbox needs
    // jdk.internal.access EXPORTED (SharedSecrets, at
    // ApplicationSharedMemory.create). Without it every Robolectric
    // suite dies in setup: 211 toolchain failures at head 7a0ba487.
    "--add-exports=java.base/jdk.internal.access=ALL-UNNAMED"
  )
}
googleServices { missingGoogleServicesStrategy = MissingGoogleServicesStrategy.WARN }

// Some unused dependencies are commented out below instead of being removed.
// This makes it easy to add them back in the future if needed.
dependencies {
  implementation(platform(libs.androidx.compose.bom))
  // implementation(platform(libs.firebase.bom))
  implementation(libs.androidx.activity.compose)
  // implementation(libs.androidx.camera.camera2)
  // implementation(libs.androidx.camera.core)
  // implementation(libs.androidx.camera.lifecycle)
  // implementation(libs.androidx.camera.view)
  implementation(libs.androidx.compose.material3)
  // Issue #336: the one icon system (MonolithIcons). Extended set:
  // the current core artifact no longer carries Shield/Schedule/
  // Visibility/VpnKey/Chevrons/DarkMode/FlashOn/Pause (CI-proven).
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  // implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  // Audit R6 #341 F17: the redundant session-boundary wake path
  // (SessionBoundaryWorker) — persisted jobs that survive the
  // process deaths the alarm chain does not.
  implementation(libs.androidx.work.runtime.ktx)
  // implementation(libs.coil.compose)
  // implementation(libs.converter.moshi)
  // implementation(libs.firebase.ai)
  // Uncomment to use Firestore:
  // implementation(libs.firebase.firestore)

  // Uncomment ALL FOUR of the following dependencies together to use Firebase Auth and Google
  // Sign-In via Credential Manager:
  // implementation(libs.firebase.auth)
  // implementation(libs.androidx.credentials)
  // implementation(libs.androidx.credentials.play.services)
  // implementation(libs.googleid)
  // implementation(libs.firebase.appcheck.recaptcha)
  // implementation(libs.firebase.appcheck.debug)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  // implementation(libs.logging.interceptor)
  // implementation(libs.moshi.kotlin)
  // implementation(libs.okhttp)
  // implementation(libs.play.services.location)
  // implementation(libs.retrofit)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  "ksp"(libs.androidx.room.compiler)
  coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")
  // "ksp"(libs.moshi.kotlin.codegen)
}

// MOTOR #31 F3 (train inheritance): Room schema export location —
// pairs with exportSchema = true on MonolithDatabase (DB v13).
ksp {
  arg("room.schemaLocation", "$projectDir/schemas")
}
