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

## System surfaces — glance doors (INNOVATOR-2, WING-A ideation, #155)

Full deep-dives: #155 comment 6537513966 · Discussion (INNOVATOR-2 system surfaces, #155). Verification VERIFIED vs main `235a382a`: TileService, ShortcutManager, requestPinAppWidget and WidgetSealTruth all 0 hits in `app/src` (code search + local grep + manifest read); widget present via PR #312; witness gate exists only on PR #540.

| Idea | Seat | Issue | Disposition |
|---|---|---|---|
| Witness Tile: Quick Settings TileService that is never a toggle — three states (SEALED only when minted in onStartListening against witness + <30s beat; UNPROVEN greyed; OPEN), click only escorts to SHIELD, flip-push via requestListeningState on #540's refresh funnel | INNOVATOR-2 | #155 | PANEL-QUEUED (M, 2–3 days; after #540 merges — needs the real WidgetSealTruth gate) |
| Pocket Receipt shortcuts, static half: shortcuts.xml verbs only ("Seal a session" → DECK arm flow, "Open Shield" → SHIELD) — static content is fixed at install, so any state word in it is a lie by construction | INNOVATOR-2 | #155 | PANEL-QUEUED (S, 0.5–1 day; independent of #540) |
| Pocket Receipt shortcuts, dynamic half: exactly ONE dynamic ShortcutManager shortcut, label-only truth ("SEALED — OPEN MONOLITH"), pushed/removed only at seal flips via the refresh funnel, rate-limit guard, no UNTIL/intention in the cached label, icon constant (honours DESIGNER-E-4's icon-flip kill) | INNOVATOR-2 | #155 | PANEL-QUEUED (S–M, +1–2 days; after #540 merges) |
| Ask Once, At The Sealing: requestPinAppWidget door for the existing widget — isRequestPinAppWidgetSupported gate, already-placed check, once-ever flag, offered only at first successful seal (or first completion card, never both), honest fallback card on unsupported launchers | INNOVATOR-2 | #155 | PANEL-QUEUED (S, 0.5–1 day; independent of #540) |
| Assistant / voice arming ("Hey Google, seal…") | INNOVATOR-2 | #155 | PARKED-WITH-REASON (killed in filing §4: voice arm = pocket-arming with a microphone; fails blocker identity) |
| Lock-screen widgets / glanceable hub as a glance surface | INNOVATOR-2 | #155 | PARKED-WITH-REASON (judged NOT REAL today in filing §4: no general third-party API on the user's device class; revisit only if a stable API ships) |
| Tile-as-toggle / tap-to-arm shortcut (naive forms) | INNOVATOR-2 | #155 | PARKED-WITH-REASON (killed by name in filing §4 so no later lane "simplifies" into them: toggle = 60s-hold bypass; tap-arm = ritual bypass) |

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
| The Counterfoil: the widget as the receipt slip's torn-off stub — one opaque slip-paper ground in EVERY state (beauty is state-independent by construction), a dashed perforation as the signature, ink-only state variation; rendered-text Tier-B ruled OUT (soul carrier is composition, not typeface); blind soul test T1/T2/T3 as the acceptance method | DESIGNER-W-1 (Wow, WING-A) | #155 | PANEL-QUEUED (filed on #155 comment 6537400092 + Discussion #639; delta S +0.5–1d on DESIGNER-E-1's stamped-plate M, standalone M 3d; rides #540 successor / #18 revamp) |
| Three Plates glyph family (SEALED ring-closed/sector-filled · OPEN sector-empty · UNPROVEN ring-broken/no-gnomon) — one hand-built silhouette set for every future glance surface | DESIGNER-E-4 (Enthusiastic designer, WING-A) | #155 | PANEL-QUEUED (design filed on #155; derived only from shipped monolith_mark/monogram geometry + Color.kt tokens) |
| D1 QS tile faces: PLATE icons, state in silhouette + system tint (colour carries nothing), subtitle ≤23 chars in Anonymous-Pro register, fail = UNAVAILABLE/UNPROVEN face (M, 2d) | DESIGNER-E-4 | #155 | PANEL-QUEUED (design done; builds after PR #540 merges) |
| D2 launcher-icon face flip (INNOVATOR-1 P2 visual form: stamp-bar SEALED face) | DESIGNER-E-4 | #155 | KILLED-BY-DESIGNER (icon cannot carry freshness; silent shield death leaves an unbounded false SEALED in the dock — artwork salvaged into the Three Plates) |
| D3 Night Seal dream visual form: true-black ground, cream night tokens, Sora 50 intention + Anonymous Pro SEALED—UNTIL line, UNPROVEN stamped in night alert ~3s before self-erasure; OPEN face = designed absence (L, 3–4d) | DESIGNER-E-4 | #155 | PANEL-QUEUED (design done; builds after PR #540 merges) |
| D4 Lock-screen widget twin reduced form: one 16sp line, no intention / no routine name on the lock face, dedicated 2×1 lock layout, silent where unsupported (S–M, 1–2d) | DESIGNER-E-4 | #155 | PANEL-QUEUED (design done; natural first build with D1 after #540) |
| D5 Held Glance label behaviour: single shortcut, label self-dates (SEALED • UNTIL HH:mm), UNPROVEN is the boot/package-replaced default, flip-fresh never beat-fresh wording (S, 1d) | DESIGNER-E-4 | #155 | PANEL-QUEUED (design done; ranked last of the surviving four) |

| The Seal Answers Back: on a reach this session, the shield card's title promotes the session's own hold record for 10 min, countdown demoted to BigText (temptation-moment glance) | INNOVATOR-5 (WING-A) | #155 | PANEL-QUEUED (filed #155 comment 6536557343 + Discussion; S–M ~1 day; identity PASS, session-scoped) |
| The Arming Verdict: one-shot summons per arm — "SEALED • SHIELD PROVED HH:MM:SS" on the protecting flip within ~15 s, else "SHIELD DID NOT PROVE — NOT PROTECTED" with repair actions; 45-min expiry | INNOVATOR-5 (WING-A) | #155 | PANEL-QUEUED (filed #155 comment 6536557343 + Discussion; M ~1.5 days; identity PASS) |
| The Dawn Line: widget shows the just-ended session's finish verdict (receipt words from persisted outcome) only when the ending was unwitnessed; burns on first tap/open or noon — one session, no list/count/streak | INNOVATOR-5 (WING-A) | #155 | PANEL-QUEUED (filed #155 comment 6536557343 + Discussion; M ~2 days; identity CONDITIONAL PASS — dies if panel rules any next-morning surface a tracker) |
| The Seal That Expires on Its Own Face: every SEALED widget render arms a one-shot AlarmManager self-update at +90 s and at printed end, bounding a dead shield's stale SEALED print to ~90 s (mechanism answering CRITIC-5 charge 6536252752; DURCH-D's lane decides) | INNOVATOR-5 (WING-A) | #155 | PANEL-QUEUED (filed #155 comment 6536557343 + Discussion; S–M ~1 day; consult to DURCH-D) |

| AS-OF stamp on the glance: widget line carries its render instant (`· AS OF HH:mm`), so a stale frame is self-evidently stale even when every refresh trigger fails (process death, standby-bucket deferral, OEM launcher frame caching) — IDEA-G1, Discussion #640 | INNOVATOR-4 (adversary seat, WING-A) | #155 | PANEL-QUEUED (priced S ~0.25 builder-day; consult filed on #155, comment 6536827668) |
| Until+1 appointment: M4's re-render appointments gain a third firing at printed-until + 1 min, so a SEALED-until sitting in the past is re-examined within a minute of becoming false — IDEA-G2 (delta on THOUGHTFUL-5/IMPROVER-2's M4, credited there), Discussion #640 | INNOVATOR-4 (adversary seat, WING-A) | #155 | PANEL-QUEUED (priced inside M4's ~1 builder-day) |

## The widget as a stamped plate (DESIGNER-E-1, WING-A ideation)

| Idea | Seat | Issue | Disposition |
|---|---|---|---|
| Stamped-plate widget: one machined plate (rivets, hairline frame, maker's-mark wordmark) whose six states differ only by stamp treatment — struck box SEALED / plain print OPEN / dashed box OPEN-next / alert box UNPROVEN / unboxed ARMING / double-border void box NO READ; full spec on #155 (comment 6536917315), build home = #18 revamp per the END-PHASE park (6525111973) | DESIGNER-E-1 | #155/#18 | PANEL-QUEUED (design filed; builder-complete; price M, 3–4 builder-days) |
| PRINTED HH:mm honesty line on the tall (3×2) plate: the plate admits when its state was last read — staleness as a printed fact, not a hidden lie (UNPROVEN tall prints LAST PROOF instead) | DESIGNER-E-1 | #155/#18 | PANEL-QUEUED (rides the plate build; alone S, 0.5d) |
| Glance-tap routing by state: SEALED/UNPROVEN/NO READ → Shield surface, OPEN → Deck (answers park item 3 on #155; notification F5 routing is the precedent) | DESIGNER-E-1 | #155/#18 | PANEL-QUEUED (design ruling for the revamp pass to ratify) |
| Tier-B rendered-text plate (park's original "rendered-text" form) only if Tier-A @font verification fails on a target launcher; accessibility-gated on the spoken sentence | DESIGNER-E-1 | #155/#18 | PARKED-WITH-REASON (contingent on the Tier-A launcher check; L, 5–8 builder-days if triggered) |

| GlanceInvalidator: one shared invalidation bus for every PUSHED glance surface — flip events + a beat-anchored one-shot self-invalidation alarm (fire at beatWall + 31s, re-arm only on a proven render, stop on first UNPROVEN render) + boot/package-replaced/clock-change refresh — built once for the widget (Step 1 of MAXIMPL-1's #155 ship order, comment 6537078341) and inherited by the dynamic-shortcut label and launcher-icon face instead of each inventing freshness; pull surfaces (QS tile) explicitly excluded, they read WidgetSealTruth at listen time. Generalises IMPROVER-4's widget-only alarm follow-up (6536071074) and INNOVATOR-5's P4 self-recheck (6536557343), with the fire time beat-anchored rather than render-anchored (strictly tighter bound) | MAXIMPL-1 (WING-A) | #155 | PANEL-QUEUED (Step 1 of the filed ship order; phase is dispute D5, captain's call) |

## OEM reality of the glance (MAXIMPL-5, WING-A IDEATION, #155)

Filed on #155 comment 6536721775 (design) + the reseat completion filing; `VERIFIED vs main 30ce1b41423ac656f3493d6963f42af613fbaad2` (widget provider, widget layout/info, boot receiver byte-identical a15d1e56 → 30ce1b4; shield service widget triggers unchanged in substance — 4 `requestUpdate` sites). Full design + bench matrix on #155 and in Discussions. All items are spec for the owning lane, sequenced strictly after PR #540 merges — no parallel build.

| Idea | Seat | Issue | Disposition |
|---|---|---|---|
| G1 law — no unbounded, undated widget claim: every frame print carries (i) the bound at which the claim stops applying and (ii) its render time, so a cached frame falsifies itself from the frame alone under force-stop / deep-sleep / autostart-denied regimes where no wake can ever correct it | MAXIMPL-5 | #155 | PANEL-QUEUED (design filed; cure priced S–M for owning lane) |
| V1 — stopwatch SEALED print gains its seal start: `SEALED • {intention} • STOPWATCH SINCE {HH:mm}` (invents no end; kills the one unbounded print on main, provider :82 at a15d1e56) | MAXIMPL-5 | #155 | PANEL-QUEUED (S, ~0.5 builder-day) |
| V2 — third widget line `CHECKED {HH:mm}`: absolute render stamp, read against the status-bar clock; NOT a beat-age in seconds. Convergent with Discussion #640's AS-OF stamp (IDEA-G1/G2) — both sources credited, one implementation to be chosen by the owning lane | MAXIMPL-5 + IDEA-G1/G2 (#640) | #155 | PANEL-QUEUED (S, ~0.25 builder-day) |
| V3 — `initialLayout` static default `OPEN` → `OPEN THE APP TO CHECK` (the provider's own read-failure words): a rebind frame must not assert an unmeasured state (mirror-image lie, R7) | MAXIMPL-5 | #155 | PANEL-QUEUED (S, ~0.25 builder-day) |
| V4 — boot / package-replaced refreshes the widget: one `MonolithWidgetProvider.requestUpdate` on MonolithBootReceiver's existing tick path (zero widget references on main, grep-verified) | MAXIMPL-5 | #155 | PANEL-QUEUED (S, ~0.25 builder-day) |
| G3 — standby self-report: PARK/board prints the app's own `UsageStatsManager.getAppStandbyBucket()` in plain words (e.g. `GLANCE MAY FREEZE — PHONE RESTRICTS MONOLITH` when RESTRICTED); zero references in app/src/main on main | MAXIMPL-5 | #155 | PANEL-QUEUED (M, 1 builder-day) |
| G4 — OEM doors from the glance's side: Samsung Never-sleeping deeplink + Xiaomi Autostart / No-restrictions path, linked from the G3 board line (the exemption *ask* itself stays Scout-2's OemPolicy, #14/#9 — cross-reference, not duplication) | MAXIMPL-5 | #155/#14 | PANEL-QUEUED (M, 1–2 builder-days) |
| OEM bench matrix as ship-gate for every glance surface: 10 regimes (Doze, RESTRICTED bucket, Battery Saver, process kill, force-stop, OEM sleep lists, Xiaomi autostart-off, launcher rebind, reboot, session-ends-while-frozen) × Pixel/Samsung/Xiaomi, verdicts REPLACED / FALSIFIABLE / LIE, zero LIE cells in tier-1; emulators sign the AOSP row only | MAXIMPL-5 | #155 | PANEL-QUEUED (L, 3–4 days tier-1; tier-2 Oppo/vivo/Realme +2 days) |
| Killed on the record: widget Chronometer/TextClock for stopwatch seals (ticking manufactures false life beside a dead shield); WorkManager periodic widget refresh (second freshness clock, strictly worse); Glance-API rewrite (same RemoteViews host, zero truth gain) | MAXIMPL-5 | #155 | PARKED-WITH-REASON (reasons stated on #155, comment 6536721775) |

## Zero-screen glances — sound, touch, badge (INNOVATOR-3, WING-A ideation, #155)

| Idea | Seat | Issue | Disposition |
|---|---|---|---|
| The Pocket Knock: haptic pull-glance — double-knock / volume long-press (a11y key events) / earbud hook asks the shield; it answers SEALED (Seal Throw waveform) / OPEN (single tick) / UNPROVEN (broken descending strikes) from the live witness at query instant; dead shield = silence = not proven | INNOVATOR-3 | #155 | PANEL-QUEUED (M–L, 3–4d; after PR #540; full filing on #155 + Discussion #609) |
| The Unfinished Chord: earcon twin of the Pocket Knock — UNPROVEN rendered as the seal motif with its final seating strike missing | INNOVATOR-3 | #155 | PANEL-QUEUED (S–M, 1.5–2d on top of the Pocket Knock; never ships alone) |
| Dot, Not Digits: kill setNumber (DESIGNER-E-5's ruling), move the 45-min completion card to a badge-off channel so the dot tracks the standing card only; badge specced as a pointer, never a claim (launcher-owned rendering stated) | INNOVATOR-3 (builds on DESIGNER-E-5) | #155 | PANEL-QUEUED (S, 0.5–1d; consult to K5/DURCH-D) |
| The Fallen-Shield Knock: one UNPROVEN-pattern strike on the protecting flip true→false for a live row (witness loss while process lives); process death expressly out of scope | INNOVATOR-3 | #155 | PANEL-QUEUED (M, ~2d; strictly after PR #540's flip machinery) |
| The Wrist Answer: Wear OS pull-glance in the same three haptic patterns | INNOVATOR-3 | #155 | PARKED-WITH-REASON (pushed watch face re-imports icon-flip staleness; no wear module/bench; unpark only after the pull grammar exists; L, 5d+ if ever) |
| The Dead-Man's Tick: periodic reassurance buzz while sealed | INNOVATOR-3 | #155 | PARKED-WITH-REASON (killed by the filing seat on both laws: pushed claim can tick over a dead shield; ambient-companion drift + room-law fight; the pull-glance covers the need at zero idle cost) |

## The OPEN face — the glance off-duty (DESIGNER-W-4, #155)

| Idea | Seat | Issue | Disposition |
|---|---|---|---|
| The OPEN plate: porcelain, ink, no accent — `OPEN` as headline in the receipt's printed character (ParkDrawer.kt:124: PORCELAIN = OPEN), near-silence as the designed default | DESIGNER-W-4 | #155 | PANEL-QUEUED (filed on #155, comment 6537408254; OPEN state of W-5's GLANCE PLATE) |
| OPEN-2 gated NEXT SEAL: prints only from the excuse-aware derivation (G7 cure), live + unpaused, within a 12h modesty horizon, static clock time + day word — never a countdown | DESIGNER-W-4 | #155 | PANEL-QUEUED (build M, 2–3 builder-days with B2, after PR #540) |
| Blank-widget withdrawal RULED REJECTED at every rung: blank is indistinguishable from failure and is a claim by omission; withdrawal of content, never of witness | DESIGNER-W-4 | #155 | ADOPTED (as ruling in the #155 design record; binds E-5's collapse ladder) |
| Empty-chair rule: OPEN plate keeps the SEALED plate's anatomy with the intention line absent, never prompted — no intention slot, no interrogative/imperative copy between blocks | DESIGNER-W-4 | #155 | PANEL-QUEUED |
| Conditional AS OF: staleness foot line prints only when the standing read is older than one 30-min cadence (refines MAXIMPL-3 R5 / W-2 for the OPEN state) | DESIGNER-W-4 | #155 | PANEL-QUEUED |
| No history on the OPEN face: W-2's yesterday-trace line ruled against on this face (sides with W-5 GLANCE-3; the receipt lives in the Ledger) | DESIGNER-W-4 | #155 | PANEL-QUEUED (named disagreement on #155 for the Panel) |

| The Plate: the glance rebuilt as the receipt slip's nameplate — fixed-geometry porcelain/ink plate, stamp-word state (SEALED/OPEN/UNPROVEN identical in silhouette), registration-mark accent in every state, typeset in the house fonts | DESIGNER-W-1 (Wow-level designer, WING-A) | #155 | PANEL-QUEUED |

Register note: Wave-3 cell reports are still landing; their seat ideas enter on the next pass. Duplicates are merged with all sources credited; a seat whose idea was merged keeps its credit line.
| One-time widget pin offer at the first receipt (system pin prompt, dismissible forever, never re-asked) — the #155 glance only helps users who place widgets; the offer rides on trust earned by a held block | CONSUMER-1 (Consumer seat, #155) | #155 · Discussion #615 | PANEL-QUEUED |

## Pill × receipt relation (WING-C DESIGN, Wave reseat)

| Idea | Seat | Issue | Disposition |
|---|---|---|---|
| The pill is the receipt's stub — one object, two tenses: claim the receipt № at ARM, pill Session face prints the same № + intention frozen at the claim; identity unifies (№, intention, row, derivation, type), tense never crosses (no outcome/sealed facts on the pill; no verbs/ephemerality on the receipt); valve/grace endings burn the claimed № | INTENTIONALITY-4 (origin: T01) | #16 | PANEL-QUEUED (verdict: https://github.com/Sarthak-2007/Focus-app/issues/16#issuecomment-6537576045 · Discussion: https://github.com/Sarthak-2007/Focus-app/discussions/671 · priced ~125 lines + 6 tests, ≈1 builder-day, after pill merge + C13 ghost deletion) |

## Glance truth source (WING-A IDEATION, #155)

| Idea | Seat | Issue | Disposition |
|---|---|---|---|
| SealTruth — ONE shared truth source for every glance surface: pure law (SealTruthLaw) + persisted shield warrant record (the protecting flip, written at the flip) + throttled persisted a11y pipeline record, so widget / shield card (incl. lock-screen public version) / Deck warrant / watchdog / QS tile / DreamService / shortcuts all RENDER one SealVerdict instead of re-deriving; kills lie classes L1–L8 (row-truth SEALED, warm-process-only truth, grant-as-binding, clock-travel, threshold drift, static public version, future-surface re-derivation, friendly-writer beat forgery); programme ≈5–7 builder-days, core M–L 2–3d | MAXIMPL-2 | #155 | PANEL-QUEUED (design + per-surface prices + consult asks filed on #155: https://github.com/Sarthak-2007/Focus-app/issues/155#issuecomment-6538324133 · VERIFIED vs main 9e7b38f68b3694146a95e4dba02dacf49539686b · offered to FINISH-LEAD K5 / PR #540 driver for the fresh re-implementation; no code built by this seat) |

## Glance-truth test floor (WING-A, #155)

| Idea | Seat | Issue | Disposition |
|---|---|---|---|
| PIN-scope labels as a floor law: every test carries its scope (PLUMBING / POST / RENDER / SOURCE) in its name or header, so coverage can never be misread at a glance — a test named as a behaviour pin that only exercises plumbing is how F3's ghost survived PR #540's first test file | MAXIMPL-4 | #155 | PANEL-QUEUED (full test matrix + audit: https://github.com/Sarthak-2007/Focus-app/issues/155#issuecomment-6538317578) |
| Mutation acceptance as the definition of "pinned" for every truth surface: six named mutations (call-site flag flip, funnel deletion, gate removal, timeout zeroing, badge flip, truth-boundary shift) must each fail at least one test, or the coverage is decoration | MAXIMPL-4 | #155 | PANEL-QUEUED |
| The widget line carries its own death: a SEALED render schedules its own un-render at beat + 30 s, making staleness impossible by construction rather than by the 30-minute platform cadence | MAXIMPL-4 | #155 | PANEL-QUEUED (feeds MAXIMPL-3's refresh-architecture seat) |

## Ambient glances (INNOVATOR-1, Crazy-ideas/Wild/Innovator, WING-A — #155)

Filed on #155 (comment 6535813528) and Discussion #609. All five consume PR #540's WidgetSealTruth (protecting witness + persisted beat <30 s) and land only after #540 merges; none prints SEALED from the session row alone.

| Idea | Seat | Issue | Disposition |
|---|---|---|---|
| P1 "Seal in the Shade": Quick Settings TileService glance — state computed at onStartListening, UNAVAILABLE = unproven, tap routes to SHIELD only (M, ~2 days) | INNOVATOR-1 | #155 | PANEL-QUEUED |
| P2 "The Icon Changes Its Face": activity-alias launcher icon flips to a stamped SEALED plate on the protecting flip; boot default OPEN; watchdog re-asserts from the truth beat (M, ~2–3 days + OEM bench) | INNOVATOR-1 | #155 | PANEL-QUEUED (panel must rule the ≤1-watchdog-interval stale window first) |
| P3 "Night Seal": DreamService glance that exists only while sealed and finishes immediately when OPEN/UNPROVEN — no seal, no surface (L, ~3–4 days) | INNOVATOR-1 | #155 | PANEL-QUEUED |
| P4 Lock-screen widget twin: add lock-screen widgetCategory to the existing #312 widget, same truth line (S–M, ~1–2 days) | INNOVATOR-1 | #155 | PANEL-QUEUED (cheapest; natural first build after #540) |
| P5 "The Held Glance": dynamic long-press shortcut whose label is the state, refreshed on flip events (S, ~1 day) | INNOVATOR-1 | #155 | PANEL-QUEUED (weakest freshness; ranked last) |
| Streak/score complications, progress-ring live wallpaper, persistent AOD stats face, icon badge counts | INNOVATOR-1 | #155 | PARKED-WITH-REASON (killed by the seat before filing: fail blocker-identity law — 24/7 tracker/dashboard; badge counts also fail truth law, cached = stale) |
