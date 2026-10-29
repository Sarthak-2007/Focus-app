# Dependency Audit — Monolith (Focus-app)
**Auslese, Methods (dependency hygiene) — Sweep 2, 2026-10-29**
Evidence basis: trunk `~/workspace/clone-inspect/Focus-app` source + build files read directly; GUH sizes via `du`; cross-home identity via file-path sets + full MD5 pass on modules-2 (trunk↔token). **No Gradle was run** (Law 11: a dependency-report would be a named probe — deferred to Waage's baseline slot if Takt wants the resolved tree; every claim below is import/usage evidence from source, which for culling is the stronger evidence anyway).

Scope correction to the brief: disk holds **8 Gradle homes, not 9** — `trunk-guh`, `tools/guh`, and 6 lane homes (`builds/{token,slide,drawer,counterfoil,routines,shieldpick}/guh`). 8 × 1.7 GB = **13.6 GB**.

---

## 1. DECLARED vs USED

Catalog: `gradle/libs.versions.toml` — 44 version keys, 54 library entries, 5 plugin entries.
Active declarations in `app/build.gradle.kts`: 30 (incl. 2 platform + ksp). The rest of the catalog is present **only as commented-out lines** in the build file (see §1.3).

Method: every unique import in `app/src` (342 imports, 40 main + 8 unit-test + 1 instrumented-test Kotlin files) plus targeted symbol greps (`Icons.`, `@Preview`, `DataStore`, `runTest`, `lifecycleScope`, `repeatOnLifecycle`, `NavHost`, `AsyncImage`, …).

### 1.1 USED — keep, with evidence

| Declaration (app/build.gradle.kts) | Evidence |
|---|---|
| `platform(androidx-compose-bom)` | Versions all Compose artifacts below |
| `androidx-activity-compose` | `ComponentActivity`, `setContent`, `BackHandler` — MainActivity.kt |
| `androidx-compose-material3` | `MaterialTheme`, `Text`, `Scaffold`, `HorizontalDivider`, colour schemes — everywhere |
| `androidx-compose-ui` / `ui-graphics` | `Modifier`, `Alignment`, `Color`, `AndroidView`, `testTag` — everywhere |
| `androidx-lifecycle-runtime-compose` | `collectAsStateWithLifecycle` (45 hits), `LocalLifecycleOwner` |
| `androidx-lifecycle-viewmodel-compose` | `viewModel()` — MainActivity.kt |
| `androidx-room-runtime` | `Room`, `RoomDatabase`, `@Dao/@Entity/@Query…` (12 room imports) |
| `androidx-room-ktx` | `androidx.room.withTransaction` — MonolithRepository.kt |
| `ksp(androidx-room-compiler)` | Generates the Room impls the above compile against |
| `kotlinx-coroutines-core` / `-android` | ~35 kotlinx.coroutines imports; `viewModelScope` (66 hits), flows throughout |
| `testImpl: compose-ui-test-junit4` | `createAndroidComposeRule` — ScreenshotHarnessTest, TetradRenderTest |
| `testImpl: androidx.test:core` | `ApplicationProvider` — unit tests |
| `testImpl: junit` | 12 `org.junit` imports across tests |
| `testImpl: robolectric` | `RobolectricTestRunner`, `RuntimeEnvironment`, `ShadowLooper` — 4 test classes |
| `androidTestImpl: compose-bom, ui-test-junit4, androidx-junit` | `AndroidJUnit4`, `InstrumentationRegistry` — ExampleInstrumentedTest.kt |
| `debugImpl: compose-ui-test-manifest`, `compose-ui-tooling` | Standard Compose debug scaffolding; manifest merger / preview runtime in debug only. Keep. |

### 1.2 UNUSED-DECLARED — the priced cull list (active declarations, zero usage)

All seven have **zero imports and zero symbol hits** in the entire source tree. Prices are (a) cache weight from the trunk modules-2 breakdown and (b) qualitative classpath/APK cost — honest labels, no fabricated APK deltas (measuring those needs a build = Waage's slot).

| # | Cull | File:line | Evidence of zero use | Price of keeping it | Risk |
|---|---|---|---|---|---|
| C1 | `androidx-compose-material-icons-extended` | app/build.gradle.kts:95 (catalog :70) | No `androidx.compose.material.icons` import, no `Icons.` symbol anywhere; all iconography is custom Views/Canvas | Largest single app library: the `androidx.compose.material` group is **38 MB** in modules-2 (2nd-largest app group, behind only Robolectric). Extended-icons AAR is famously the fattest Compose artifact; it rides every compile classpath and every transform in all 8 homes | LOW |
| C2 | `androidx-compose-material-icons-core` | app/build.gradle.kts:94 (catalog :69) | Same as C1 | Small alone (~1 MB class), but pointless without a single icon; cull together with C1 | LOW |
| C3 | `androidx-datastore-preferences` | app/build.gradle.kts:101 (catalog :36,:86) | No `androidx.datastore` import, no `DataStore`/`preferencesDataStore` symbol; all persistence is Room | `androidx.datastore` = **1.7 MB** modules-2 + drags protobuf-lite onto the runtime classpath + its transforms in all 8 homes | LOW |
| C4 | `androidx-compose-ui-tooling-preview` | app/build.gradle.kts:97 | No `@Preview` in any file, no `ui.tooling` import | Wrong scope as well as unused: it is an `implementation`, so a design-time artifact sits on the **release** classpath. If previews ever return it belongs in `debugImplementation` beside `ui-tooling` | LOW |
| C5 | `androidx-lifecycle-runtime-ktx` | app/build.gradle.kts:103 (catalog :50) | `lifecycleScope` = 0 hits, `repeatOnLifecycle` = 0 hits. `viewModelScope` (the thing that looks like its API) ships in lifecycle-**viewmodel**, which arrives via viewmodel-compose | ~Part of `androidx.lifecycle` 1.2 MB cache group; small, but it is a declaration that lies about the architecture — nothing uses lifecycle-ktx idioms | LOW–MED (verify nothing arrives *only* through it — it doesn't: core-ktx is declared separately) |
| C6 | `kotlinx-coroutines-test` (testImpl) | app/build.gradle.kts:133 (catalog :65) | `runTest` = 0, `TestDispatcher`/`StandardTestDispatcher` = 0. All 8 test classes use plain `runBlocking` (13 hits) | Test-classpath only — zero production cost, priced honestly. Cost is a false signal: the catalog claims a coroutines-test rig that doesn't exist | LOW |
| C7 | `androidx-espresso-core` (androidTestImpl) | app/build.gradle.kts:137 (catalog :49) | The only instrumented test uses `AndroidJUnit4` + `InstrumentationRegistry`. The two "Espresso" grep hits in the tree are a **colour name** (`EspressoInk`, Color.kt:110) and a comment | androidTest-only — zero production cost. `androidx.test.espresso` = 636 KB cache. Hygiene cull, not a saving | LOW |

**TRANSITIVE-ONLY — declared, no direct use, cull only with eyes open:**

| Declaration | Finding |
|---|---|
| `androidx-core-ktx` (app/build.gradle.kts:100) | No `androidx.core.*` ktx import anywhere. The `androidx.core` symbols actually used — `NotificationCompat`, `ServiceCompat`, `ResourcesCompat` — live in `androidx.core:core`, which arrives transitively regardless. **Recommendation: keep for now, flag WATCH.** Explicit core-ktx is conventional hygiene and AndroidX resolution can shift under it; it is not dead weight of the C1–C7 kind. |
| `androidx-runner` (androidTestImpl, :139) | `AndroidJUnit4` lives in `androidx.test.ext:junit`; runner is its transitive dependency. Explicit declaration is redundant but harmless. Optional cull. |
| `testImpl androidx-junit` (:135) | Duplicated scope: `androidx.test.ext:junit` is used **only** by the instrumented test (where it is also declared, :138). In the unit-test scope it is unused — Robolectric tests never import it. One-line scope cull. |

### 1.3 CATALOG-ONLY — 24 dormant entries (commented out in app/build.gradle.kts:88–131)

Not active declarations, so **zero runtime/cache cost today** (verified: no firebase/coil/retrofit artifacts of consequence in modules-2 — the whole `com.squareup` group is 540 KB, Robolectric's transitive okhttp). The cost is catalog noise and a standing invitation to uncomment a stack nobody chose:

- Firebase ×7: `firebase-bom`, `firebase-ai`, `firebase-firestore`, `firebase-auth`, `firebase-appcheck-recaptcha`, `firebase-appcheck-debug` (+ `firebaseBom` key)
- CameraX ×4: `camera-camera2`, `camera-core`, `camera-lifecycle`, `camera-view`
- Network stack ×5: `retrofit`, `converter-moshi`, `moshi-kotlin`, `moshi-kotlin-codegen`, `okhttp`, `logging-interceptor` (6 with the interceptor)
- `coil-compose`, `androidx-navigation-compose` (navigation is hand-rolled state — `MonolithDestination`, zero `NavHost`), `accompanist-permissions`, `play-services-location`, `androidx-credentials`, `androidx-credentials-play-services`, `googleid`

The build file's own comment (:87) states the AI Studio convention: commented-out instead of removed "to add them back in the future." That is a regeneration-convenience, not a decision. **Proposal (Takt's call): the Dormant Annex** — move these 24 entries + their version keys out of `libs.versions.toml` into `gradle/dormant.versions.toml.txt` (deliberately non-parsed extension). Catalog shrinks 54 → 30 library entries; restoring a stack is one copy-paste, same as uncommenting today. Nothing about AI Studio regeneration breaks: it regenerates the whole file anyway.

### 1.4 Plugins — REVIEW, not cull

- `secrets` + `google-services` plugins are **applied** in app/build.gradle.kts (:5–6) and root (:4–5). There is **no `google-services.json`** anywhere in the tree; the module is configured to merely WARN on its absence (`MissingGoogleServicesStrategy.WARN`, `googleServices.missing.passthrough=true` in gradle.properties). The secrets plugin reads `.env`/`.env.example` and ignores one Firebase token key. If no Firebase/Maps feature is planned, both plugins are build-time dead weight on every configuration pass in every lane. **Not culled by this audit**: the build file carries "Do not remove" AI Studio signing comments adjacent to this machinery, and plugin removal is a behaviour change — Takt/Ren decision, priced here as: 2 plugins × configuration cost × 8 homes, benefit of removal = one less silent-WARN path.

### 1.5 Cull summary (builder's shopping list)

First wave (C1–C5, all LOW risk, production classpath): icons ×2, datastore, tooling-preview, lifecycle-runtime-ktx.
Second wave (C6, C7 + scope fixes, test classpaths only): coroutines-test, espresso, test-scope junit.
Expected effect, honestly bounded: modules-2 shrinks by ~40 MB/GUH (icons group + datastore + satellites) → **~320 MB across 8 homes**, plus the transform work those artifacts generate in every home (~739 MB transforms pool shrinks proportionally to its largest inputs). The big money is §2, not here — culls are hygiene; the cache is the weight.

---

## 2. CACHE WEIGHT — the 13.6 GB question (Heizung's input; full breakdown also mailed to him)

### 2.1 Per-home anatomy (trunk-guh, representative — all 8 measured, figures repeat)

| Component | Size | Share of 1.7 GB |
|---|---|---|
| `caches/9.3.1/transforms` | 739 MB | 45% |
| `caches/modules-2` (downloaded artifacts) | 566 MB | 35% |
| `caches/9.3.1/generated-gradle-jars` | 183 MB | 11% |
| `caches/build-cache-1` | 130 MB (119–130 across homes) | 8% |
| `caches/9.3.1/javaCompile` + kotlin-dsl + fileHashes | ~26 MB | 1.5% |
| daemon / native / .tmp / misc | ~12 MB | <1% |
| **Wrapper distribution** | **0 MB — not in the GUH.** Gradle 9.3.1 itself lives once, shared, at `tools/gradle-9.3.1` (146 MB). This is the one thing already done right. | — |

modules-2 by group (trunk): Robolectric **158 MB (28%)**, Kotlin **101 MB**, KSP **80 MB**, AGP (`com.android.tools.build`) **45 MB**, compose.material (icons) **38 MB**, then bouncycastle 19, icu 14, sqlite/xerial 13, guava 8.9, compose.ui 8.9, room 6.0, coroutines 5.5. **Read: over a third of the artifact cache is test tooling (Robolectric + its bouncycastle/icu/conscrypt tail) and build tooling, not app libraries.**

### 2.2 Shared-identical vs lane-unique — measured, not assumed

- **modules-2: 100% identical.** Every home holds exactly **923 files** under `files-2.1`. Path sets: trunk ∩ token = trunk ∩ slide = trunk ∩ counterfoil = trunk ∩ tools = **923/923, zero lane-unique files, zero trunk-only files**. Full MD5 pass, trunk↔token: **923/923 content-identical**.
- **transforms: ~97% shared-derived.** Total size identical to the MB (739–740 MB) in all 8 homes, but file counts vary 6,699–6,919 (±3%) — a small lane-unique tail (transform outputs embed per-build metadata). Not bit-proven identical; treat as probable-duplicate pending a hash spot-check.
- **generated-gradle-jars: presumed identical** (same Gradle 9.3.1, same build logic in every lane — §3 shows build files are byte-identical). Sized identically; not hash-checked.
- **build-cache-1: lane-partly-unique by design** (task outputs of that lane's own edits), 119–130 MB; the 10 MB spread is the lane-unique part showing.

### 2.3 The price

| Claim | Number | Proof level |
|---|---|---|
| Proven pure duplication (modules-2) | 7 extra copies × 566 MB = **3.96 GB** | Hash-proven |
| Probable duplication (transforms) | 7 × ~720 MB ≈ **5.0 GB** | Size/count evidence |
| Probable duplication (generated jars) | 7 × 183 MB ≈ **1.28 GB** | Structural |
| **Ceiling if lanes shared one warm home** | **≈ 10.2 GB of the 13.6 GB** | — |
| Floor saving, zero-risk (modules-2 only) | **3.96 GB** | Hash-proven |

**Invention for Heizung's warm-copy kit — the hardlink farm:** modules-2 files are immutable and content-addressed (Gradle never mutates them in place), so a lane GUH does not need a *copy* at all: `cp -al` (hardlink) the golden home's `modules-2` + `generated-gradle-jars` into each lane. Lane spin-up drops from a 566 MB copy/download to seconds, physical cost of homes 2–8 ≈ 0 for those pools, and unlinking a lane can never corrupt the golden copy. Transforms stay per-lane (their ±3% tail + path metadata make hardlinking them the risky third) — warm them by copying from golden as today, or accept the cold-transform cost once per lane. Daemon/native stay per-lane as now. This keeps the Pipeline-Law reason lanes have separate homes (Gradle jobs never collide) while removing the only part that was ever actually heavy.

---

## 3. VERSION DISCIPLINE

### 3.1 Single-source: YES, with two exceptions

- Every dependency version in the project resolves through `gradle/libs.versions.toml`. **Zero hardcoded dependency versions** in `app/build.gradle.kts` or root `build.gradle.kts` (grepped). 
- Exception 1: `settings.gradle.kts:15` — foojay-resolver plugin pinned inline (`version "1.0.0"`), outside the catalog. Trivial, but it is a second source.
- Exception 2 (by design, correct): BOM-managed entries (`androidx-compose-ui`, `material3`, icons, firebase) carry no version in the catalog — the BOM is their source. Correct practice; noted so nobody "fixes" it.

### 3.2 Drift across lane trees: ZERO

`diff -q` on all six build-relevant files (`gradle/libs.versions.toml`, `app/build.gradle.kts`, `build.gradle.kts`, `settings.gradle.kts`, `gradle.properties`, `gradle/wrapper/gradle-wrapper.properties`) × all six lane trees vs trunk: **36/36 SAME.** No lane has drifted a version, a declaration, or a property. The copies are faithful — which is exactly why §2's duplication is total: identical inputs, identical caches, eight times.

### 3.3 Catalog hygiene findings (file:line, libs.versions.toml)

1. **Redundant version keys for lockstep groups** — same value, many keys: `roomRuntime`/`roomKtx`/`roomCompiler` (:15–17, all "2.7.0"); `lifecycleRuntimeKtx`/`lifecycleViewmodelCompose`/`lifecycleRuntimeCompose` (:7–9, all "2.8.7"); `kotlinxCoroutinesTest`/`kotlinxCoroutinesAndroid`/`kotlinxCoroutinesCore` (:18,:33,:34, all "1.10.2"); `cameraCamera2`/`cameraLifecycle`/`cameraView`/`cameraCore` (:28–31); `retrofit`/`converterMoshi` (:25–26); `moshiKotlin`/`moshiKotlinCodegen` (:38–39); `loggingInterceptor`/`okhttp` (:35,:37). ≈12 keys that exist only to be bumped in lockstep by hand — each is a future half-bump. Collapse each group to one key.
2. **Cryptic keys**: `core = "1.6.1"` (:19) and `runner = "1.6.2"` (:20) are androidx.test artifacts — the names read as generic core/runner. Rename `androidxTestCore` / `androidxTestRunner`.
3. **Inconsistent naming**: `junit` (:4, the framework) vs `junitVersion` (:5, androidx.test.ext:junit) — one looks like the other's version. Rename the latter `androidxJunit`.
4. **Stale BOM vs modern toolchain**: `composeBom = "2024.09.00"` (:12) — a Sep-2024 Compose BOM paired with Kotlin 2.2.10 (:11) and AGP 9.1.1 (:2). Not drift, not breakage (it compiles), but the UI layer is pinned ~2 years behind the compiler that builds it; any Compose fix since Sep 2024 is unreachable until this moves. Flagged for a deliberate bump decision, not a drive-by.
5. **Dormant weight**: 24 of 54 library entries (~44%) serve only commented-out lines — §1.3's annex proposal.

---

## 4. What I did NOT do

No Gradle run, no dependency tree, no edits to any build file — per commission, this is the priced proposal; builders own the files. If Takt wants the resolved-tree cross-check (it would confirm C5's transitive safety mechanically), it is exactly one named Law-11 probe — `dependencies --configuration releaseRuntimeClasspath` on trunk — and belongs in Waage's baseline slot, not a new run.

— Auslese, Methods (dependency hygiene)

---

## ADDENDUM — Oct 29, 2026, ~21:20 IST (Auslese, re-seated by Ren): C6 ACQUITTED

Correction appended, never rewritten: §1.2's C6 row above stands as filed.
It was true when filed and is false now; both halves are the record.

**What the audit said (then true):** cull `kotlinx-coroutines-test` —
`runTest` = 0, `TestDispatcher`/`StandardTestDispatcher` = 0, all 8 test
classes on plain `runBlocking`. Verified against the tree as it stood at
audit time; the evidence was real.

**What changed:** Batch 6 (the proving cascade) landed after the audit
and before the cull was applied. Its `ArmProvingControllerTest` is a
coroutines-test rig: trunk
`app/src/test/java/com/example/ArmProvingControllerTest.kt:13–16`
imports `kotlinx.coroutines.test.TestScope`, `advanceTimeBy`,
`runCurrent`, `runTest` — the witness-timing tests run on exactly the
library C6 proposed to cut.

**The acquittal:** when the cull was applied (lane `builds/depcull`,
kit v1.3, Oct 29 20:59 IST), the application re-grepped the CURRENT
trunk before cutting — and C6's zero-use evidence no longer existed.
C6 was acquitted on that evidence and STANDS in the lane. Six culls
were applied: C1, C2, C3, C4, C5, C7 (declarations removed from
`app/build.gradle.kts`, catalog entries removed from
`gradle/libs.versions.toml`; trunk↔lane diff is the citation). C6's
§1.2 price inverts with its evidence: the "false signal" is gone —
the catalog now tells the truth, and the rig is load-bearing.

**Method rule, from this catch:** an audit is evidence with a
timestamp, not a standing fact. Every cull re-verifies its zero-use
evidence against the tree as it stands on application day. Batch 6
made C6 false; only the re-grep noticed.

— Auslese, Methods (dependency hygiene)
