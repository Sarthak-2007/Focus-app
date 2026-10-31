# IDEAS REGISTER — every suggestion filed tonight, with a disposition path

As of: **2026-10-30 06:44 IST** · Kept by REGISTRAR (Keepers of Record).
Rule: nothing enters without a disposition; nothing leaves without a reason.
Dispositions: **BUILT** (landed) · **ADOPTED** (decided / in an active lane's spec) · **PANEL-QUEUED** (goes to the Panel chair) · **PARKED-WITH-REASON** (frozen or ruled out, reason stated).

## Feel, haptics & the Held Door (Scout-1, interaction/feel elite)

| Idea | Seat | Issue | Disposition |
|---|---|---|---|
| SealHaptics system: compose all haptics from Android VibrationEffect primitives (ticks/clicks/thuds) instead of raw buzzes | Scout-1 | #11/#13 | PANEL-QUEUED (top feel find) |
| Haptic law: clear > rich > buzzy, synchronised, rare | Scout-1 | all | PANEL-QUEUED (as law) |
| Breath-before-grant on the peek door (one sec principle, PNAS evidence) | Scout-1 | #11 | PANEL-QUEUED |
| "Away Key": ending a session early requires returning to an armed place/NFC tag | Scout-1 | #7 | PANEL-QUEUED |
| DIY NFC sticker as the cheap Away Key (tag ID only, nothing else stored) | Scout-1 | #13/#15 | PANEL-QUEUED |
| Per-app intervention tiers inside a seal: SHUT / PEEK-ONLY / SLOWED instead of binary | Scout-1 | #7 | PANEL-QUEUED |
| Escalating un-parkable Held Door: peek resistance grows per request; leaving restarts the wait | Scout-1 | #11 | PANEL-QUEUED |
| Peek as a burst with a visible end; re-seal mid-scroll when it lapses | Scout-1 | #11 | PANEL-QUEUED |
| Declare-how-long before entry; declared minutes printed on the receipt | Scout-1 | #11/#10 | PANEL-QUEUED |
| Densifying hold: 12-tick escalating haptic schedule across the 60s emergency hold | Scout-1 | #11/#13 | PANEL-QUEUED |
| Invincible Mode: settings changeable only inside a pre-chosen 10-min window | Scout-1 | #7/#14 | PANEL-QUEUED |
| One growing object IS the whole session screen (Forest principle) | Scout-1 | #4/#13 | PANEL-QUEUED (feeds revamp) |
| Two house springs (heavy seal spring, snappy chip spring) + morph-on-press, no M3 skin | Scout-1 | #13/#7 | PANEL-QUEUED |
| Rotating proof-kinds at the door (breath / maths / physical action) from own machine vocabulary | Scout-1 | #11 | PANEL-QUEUED |
| Accessibility overlay honesty checklist as a gate | Scout-1 | #11 | PANEL-QUEUED |
| Four-state depth grammar: raised / travel / seated-inset / flat-disabled | Scout-1 | #4/#7/#13 | PANEL-QUEUED (feeds revamp) |
| Standing review test: "would a user wait this friction out by day 3?" (habituation warning) | Scout-1 | #11 | PANEL-QUEUED |
| Opal gems / Focus-Score gamification | Scout-1 | — | PARKED-WITH-REASON (scout SKIP; scores already excluded by his law) |
| Brick $59 hardware puck | Scout-1 | — | PARKED-WITH-REASON (scout SKIP; NFC-sticker steal covers the principle) |

## Reliability, alarms & blocking (Scout-2, technical elite)

| Idea | Seat | Issue | Disposition |
|---|---|---|---|
| setAlarmClock() for routine starts (Doze-exempt, visible alarm icon = armed proof) | Scout-2 | #46/#39 | PARKED-WITH-REASON (routines frozen; first evidence item for the rethink decision) |
| OemPolicy object: battery/autostart exemption asked only on phones that need it | Scout-2 | #14/#9 | PANEL-QUEUED (setup-sheet half is not routine-frozen) |
| Do the work inside onReceive(); no service hop in the firing chain | Scout-2 | #46 | PARKED-WITH-REASON (routines frozen; rethink evidence) |
| Exact-alarm contract: canScheduleExactAlarms() checks, re-derive after every restore | Scout-2 | #14/#9 | PANEL-QUEUED |
| Split signing: AGP injected signing for release, zero secrets for CI proof | Scout-2 | #3/#25 | PANEL-QUEUED (interacts with install-over law — see Open Loops) |
| Sign the unsigned artifact as a separate CI step (r0adkll) | Scout-2 | #3 | PARKED-WITH-REASON (fallback only if injected signing fails on this AGP) |
| Graceful signing fallback: debug-sign + loud fingerprint banner in proof jobs | Scout-2 | #3 | PANEL-QUEUED |
| gradle/actions/setup-gradle replaces hand-rolled CI caching | Scout-2 + Methods 1.1 | #3/#24 | PANEL-QUEUED (duplicate merged; both sources credited) |
| Configuration Cache warn-first + RAM law (-Xmx2g, workers 2) in repo gradle.properties | Scout-2 + Methods §3 | #24 | PANEL-QUEUED (duplicate merged) |
| Foreground-service heartbeat owns "blocking is alive"; Shield prints from it, never infers | Scout-2 (Mindful steal) | #7/#48 | PANEL-QUEUED |
| Android 17 Advanced Protection degradation design (never falsely declare isAccessibilityTool) | Scout-2 | #7/#14 | PANEL-QUEUED |
| Roborazzi JVM screenshot tests replace the hand-rolled harness | Scout-2 + Methods 6.5 | #65/#66 | PANEL-QUEUED (duplicate merged) |
| Restic check discipline: snapshot verified only on restore + data-read | Scout-2 | #17 | ADOPTED (Lotse's #17 run + independent verifier follow it) |
| bmgr-style backup/restore bench protocol for the in-app backup | Scout-2 | #5/#17 | PANEL-QUEUED |
| OEM autostart intent table stolen into OemPolicy (no library dependency) | Scout-2 | #14/#9 | PANEL-QUEUED |
| gradle-profiler scenario file: measure speed claims before/after | Scout-2 + Methods | #15 | PANEL-QUEUED |
| Least-privilege accessibility split: detection service with canRetrieveWindowContent=false | Scout-2 | #42/#7 | PANEL-QUEUED |
| Google Compose Preview Screenshot Testing as primary tool | Scout-2 | #65 | PARKED-WITH-REASON (scout verdict SKIP for now; previews kept as the test inventory) |

## Design language (Scout-3, design elite)

| Idea | Seat | Issue | Disposition |
|---|---|---|---|
| TP-7 redundant-state hero dial: one reel object carries the whole session state (#13/#4 ancestor) | Scout-3 | #13/#4 | ADOPTED (fed to Umbau revamp lane as design ancestor) |
| Colour-as-verb law: colour means an action/state, never branding | Scout-3 | all | PANEL-QUEUED (as law) |
| Solari split-flap: the update sound IS the signal | Scout-3 | #13 | PANEL-QUEUED |
| Braun ET66: colour-coded function, one hero key | Scout-3 | #6/#4 | PANEL-QUEUED |
| EP-133 fixed glyph set, lit-or-not, replacing the Deck text dump | Scout-3 | #4/#6 | ADOPTED (direct cure for #4; in Umbau/Antlitz scope) |
| Playdate principle: 1-bit discipline, one unforgettable input | Scout-3 | — | PANEL-QUEUED |
| Nothing Glyph: light pattern as interface, with an overreach ceiling as law | Scout-3 | #4/#13 | PANEL-QUEUED |
| AlwaysOn AOD burn-in defence as reference implementation | Scout-3 | #13 | PANEL-QUEUED |
| UI sound from real recordings, never the same twice (Klack/Kiiwi) | Scout-3 | #49 | PANEL-QUEUED |
| Thermal-receipt two-size truth typography (Font A / Font B) | Scout-3 | #10 | PANEL-QUEUED (receipt character is a keeper — print fixes only) |
| Ambient mode: don't double-shift (verification gate) | Scout-3 | #13 | PANEL-QUEUED |
| Atkinson distinction engineering (confusion pairs checked before drawings) | Scout-3 | #63 | PANEL-QUEUED |
| Night mode is a separate desk-clock face, not a dimmed day face | Scout-3 | #13 | PANEL-QUEUED |
| Rams/ET66 judging rubric for screens | Scout-3 | panel | PANEL-QUEUED |

## Pipeline & process (Methods ×10, WAVE2-METHODS.md)

| Idea | Seat | Issue | Disposition |
|---|---|---|---|
| Freeze-gate script before any merge train departs | Methods (Takt) | #15 | PANEL-QUEUED (draft exists in wave2-methods-files/) |
| Rescue-build v2 workflow (tests as parallel job; no APK upload before tests) | Methods | #3/#24 | PANEL-QUEUED (draft exists; parts already live via Fuge's #3 fix — BUILT in part) |
| Concurrency cancellation + trigger scoping on CI | Methods | #24 | PANEL-QUEUED |
| actionlint pre-flight + explicit timeouts | Methods | #24 | PANEL-QUEUED (timeout-minutes already BUILT on #3 branch) |
| Pin actions by full SHA + Dependabot | Methods | #24/#25 | PANEL-QUEUED (dependabot draft exists) |
| Keytool-generate standard debug keystore in CI | Methods + Fuge | #3 | BUILT (landed on agent/issue-3, run-verified) — superseded in part by the install-over keystore question, see Open Loops |
| Delete custom debugConfig, restore AGP default debug signing (root-cause signing fix) | Methods | #25 | PANEL-QUEUED |
| Commit the existing debug keystore (public-by-design) | Methods | #25 | ADOPTED (Ren ratification 4 — after main proof run; also the install-over cure candidate) |
| Release signing via GitHub Secrets + base64 decode | Methods | #25 | PANEL-QUEUED |
| Sequential merge train onto integration branch; octopus merges rejected | Methods | #15 | ADOPTED (Konto's batch-10 train runs this way) |
| git rerere + range-diff as the hand-merge review loop | Methods | #15 | PANEL-QUEUED |
| Git Town stacked-branch discipline for Wave 3+ (not retrofitted) | Methods | — | PANEL-QUEUED |
| CODEOWNERS-by-hot-file + delta-inventory PR template | Methods | #22 | PANEL-QUEUED (PR template draft exists) |
| Sub-issues + Projects v2 board replace the hand-maintained #22 index | Methods (Register seat) | #22 | PANEL-QUEUED |
| YAML Issue Forms so new orders are born structured | Methods | #22 | PANEL-QUEUED |
| PR closing keywords: merges close issues, not comments | Methods | #22 | PANEL-QUEUED |
| Label taxonomy split type/state; reject actions/stale for this repo | Methods | #22 | PANEL-QUEUED |
| JUnit XML publishing instead of log-diving | Methods | #24 | PANEL-QUEUED |
| One test-only ParkDrawer content wrapper so composable growth stops breaking 5 files | Methods | #65/#66 | PANEL-QUEUED |
| Robolectric fleet tuning: one properties file, fork discipline | Methods | #65/#66 | PANEL-QUEUED (Boden squad territory) |
| Flaky-test detection via Gradle Test Retry, fail-on-flake mode | Methods | #65 | PANEL-QUEUED |
| Tag-push releases (softprops/action-gh-release); kill the 3-day artifact cliff | Methods | #15 | PANEL-QUEUED |
| Monotonic versionCode from git; SemVer versionName from tag | Methods | #15 | PANEL-QUEUED (interacts with install-over law) |
| Release Drafter notes from commits/PRs | Methods | #15 | PANEL-QUEUED |
| Published sha256 + build provenance + immutable releases | Methods | #15 | PANEL-QUEUED |
| Signed issue/PR comments + studio/plan in repo as the collaboration surface | Methods (Postamt) | — | BUILT (programme already works this way) |
| PR template with evidence fields | Methods | #22 | PANEL-QUEUED (draft exists in wave2-methods-files/) |

## Decisions & deliberations

| Idea | Seat | Issue | Disposition |
|---|---|---|---|
| Q6 slide sound: keep today's quiet gate knocks (option B) | Panel (6–0) | #19 | ADOPTED (ratified by Ren; closed decision-only) |
| No urge/feeling question on the Held Door; feeling may only live on the receipt after return | Panel (4–2 advisory) | #20 | ADOPTED (ratified by Ren; closed decision-only) |
| W-29 overturn: the words "EMERGENCY PEEK" die; #11's wording ships | Wave-2 leads → Ren | #11 | ADOPTED (ratified; batch-10 carries it) |
| Routines option A: rebuild routines right | Deliberation | #39/#46 | PANEL-QUEUED (7/50 seats; two conditional — minority, priced ~1 week + OEM bench) |
| Routines option B: dissolve into plain recurring "standing seals" | Deliberation | #39/#46 | PANEL-QUEUED (chair's recommendation; machinery of the winning model — Ren decides per his 05:09 order) |
| Routines option C: remove routines; the hand-arming ritual is the feature | Deliberation | #39/#46 | PANEL-QUEUED (minority; cheapest at 2.0 builder-days) |
| Routines option D: "the summons" — routines prepare, the man seals | Deliberation (5 seats independently) | #39/#46 | PANEL-QUEUED (did not survive cross-examination intact; two zero-cost contributions absorbed into B) |
| Routines option D′: "invert it — the phone lives sealed, routines are the openings" | Crazy-ideas seat | #39/#46 | PANEL-QUEUED |
| Delete the routine object + its grading tribunal entirely | Deliberation (43–7) | #39/#46 | PANEL-QUEUED (the deliberation's strongest consensus; rides with the rethink decision) |
| Diagnosticians' probation: one routine (SLEEP), ARMED/WOKE/REFUSED stamps, proof-gate before trust | Diagnosticians | #46 | PANEL-QUEUED (stamps + proof-gate absorbed into the chair's recommendation) |

## Seat ideas from issue reports (Wave-2/3 cells)

| Idea | Seat | Issue | Disposition |
|---|---|---|---|
| Receipt feed arrival: whole-slip slide at the house motion constant (280ms), no bounce, no line stagger | Enthusiastic designer / Interaction-detail | #10 | PANEL-QUEUED |
| Receipt print half: one ink for deflections, section rule, hanging indent (reads as print, not UI rows) | Wow-level designer | #10 | PANEL-QUEUED |
| Receipt silence-span rules | Emil (via Lade) | #10 | PANEL-QUEUED (named remainder on #10) |
| Snapshot as signed restic repo to R2+B2 (~492 MB unique for #1+#2+#3, ₹0/mo) | Crazy-ideas (Fabel line) | #17 | PANEL-QUEUED (durable form of the off-box leg) |
| Snapshot sets as single zstd volumes on a private release (interim off-box, same-day) | Wow-level designer | #17 | PANEL-QUEUED (interim form of the off-box leg; needs no new accounts) |
| FAILED.md in both stores on every refused/failed snapshot attempt | Improver / Feel-2 | #17 | PANEL-QUEUED |
| INDEX.md storefront in both snapshot roots (name, manifest sha256, verdict triples) | Designer / Feel-5 | #17 | PANEL-QUEUED |
| Test call-site census script (stdlib, prints PASS, catches seeded regressions) | Fuge | #2 | BUILT (studio/tools/check_test_callsites.py, on main via PR #165) |
| Launcher block screen: un-exitable launcher surface + separate allowed/peek screens | Sarthak's ruling 04:29 (not a seat idea — his order) | #22/#18 | BUILT-IN-PROGRESS (Umbau lane; first slice merged via PR #167) |

## The OPEN face — the glance off-duty (DESIGNER-W-4, #155)

| Idea | Seat | Issue | Disposition |
|---|---|---|---|
| The OPEN plate: porcelain, ink, no accent — `OPEN` as headline in the receipt's printed character (ParkDrawer.kt:124: PORCELAIN = OPEN), near-silence as the designed default | DESIGNER-W-4 | #155 | PANEL-QUEUED (filed on #155, comment 6537408254; OPEN state of W-5's GLANCE PLATE) |
| OPEN-2 gated NEXT SEAL: prints only from the excuse-aware derivation (G7 cure), live + unpaused, within a 12h modesty horizon, static clock time + day word — never a countdown | DESIGNER-W-4 | #155 | PANEL-QUEUED (build M, 2–3 builder-days with B2, after PR #540) |
| Blank-widget withdrawal RULED REJECTED at every rung: blank is indistinguishable from failure and is a claim by omission; withdrawal of content, never of witness | DESIGNER-W-4 | #155 | ADOPTED (as ruling in the #155 design record; binds E-5's collapse ladder) |
| Empty-chair rule: OPEN plate keeps the SEALED plate's anatomy with the intention line absent, never prompted — no intention slot, no interrogative/imperative copy between blocks | DESIGNER-W-4 | #155 | PANEL-QUEUED |
| Conditional AS OF: staleness foot line prints only when the standing read is older than one 30-min cadence (refines MAXIMPL-3 R5 / W-2 for the OPEN state) | DESIGNER-W-4 | #155 | PANEL-QUEUED |
| No history on the OPEN face: W-2's yesterday-trace line ruled against on this face (sides with W-5 GLANCE-3; the receipt lives in the Ledger) | DESIGNER-W-4 | #155 | PANEL-QUEUED (named disagreement on #155 for the Panel) |

Register note: Wave-3 cell reports are still landing; their seat ideas enter on the next pass. Duplicates are merged with all sources credited; a seat whose idea was merged keeps its credit line.
