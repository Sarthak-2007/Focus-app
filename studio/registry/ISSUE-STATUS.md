# ISSUE STATUS — every issue, worked-on or not

As of: **2026-10-30 06:44 IST** · Source: live gh (issue list, branch list, run list, PR list) + git ancestry vs origin/main (0a00e95).
Totals: **165 issues · 16 closed · 149 open** · branches on GitHub: 56 · PRs merged: 6 · PRs open: 3.

**Why closed (16) ≠ PR merges (6):** closures happen on branch-level CI proof (close-when-fixed law); merging is a separate act. Of 16 closed: **6 code-merged-to-main** (#2, #3, #35, #47, #61, #62 — only 4 via PR), **7 code-on-branch-awaiting-train** (#12, #13, #26, #56, #59, #66, #71), **1 verdict-or-strike** (#5), **2 decision-only** (#19, #20). The other 2 merged PRs are not closures: #167 design-revamp, #173 issue-17 evidence (#17 itself still open).
Merged = branch tip is an ancestor of main, or a merged PR exists for the branch (noted). "PR merged, branch active" = work landed, branch has since advanced.

| # | Issue | State | Owner / lead | Branch (tip) | Last CI verdict | Merged to main | How closed |
|---|---|---|---|---|---|---|---|
| 1 | How this repo runs — the charter | open | — (charter) | — | — | — | — |
| 2 | Fix the batch-9 test call-sites so the unit tests compile and ru | CLOSED | Fuge · finisher Siegel | agent/issue-2 (f789cef) | run 40970308776: failure | PR #165 merged · branch active | code-merged-to-main (PR #165) |
| 3 | CI: prove the automatic build circuit end to end | CLOSED | Fuge | agent/issue-3 (60be649) | run 40980138310: failure | no | code-merged-to-main (direct push of workflow to main) |
| 4 | Deck top strip: kill the text dump, make it an object | open | Antlitz · finisher Kappe | agent/issue-4 (a4ebd5a) | run 40980254336: failure | no | — |
| 5 | Verification: Truth Car 3 on the merged batch-9 tree | CLOSED | Tor · finisher Druck | agent/issue-5 (6f96512) | run 40973993598: failure | no | verdict-or-strike (verification sweep, no new code) |
| 6 | Main-app pass — Deck (audit fixes) | open | Antlitz · finisher Kappe | agent/issue-6 (fccbe64) | run 40978833192: failure | no | — |
| 7 | Main-app pass — Shield (audit fixes) | open | Vogt · finisher Bolzen | agent/issue-7 (a2066d9) | run 40978138639: failure | no | — |
| 8 | Main-app pass — Drawer (audit fixes) | open | Lade · finisher Klappe | agent/issue-8 (9157f77) | run 40980587117: failure | no | — |
| 9 | Main-app pass — Park (audit fixes) | open | Vogt · finisher Bolzen | agent/issue-9 (cce41f0) | run 40972632274: failure | no | — |
| 10 | Main-app pass — Receipt (audit fixes) | open | Lade · finisher Klappe | agent/issue-10 (bc6b25f) | run 40980632493: failure | no | — |
| 11 | Main-app pass — Held Door (audit fixes) | open | Tor · finisher Druck | agent/issue-11 (e67e7e9) | run 40980004844: failure | no | — |
| 12 | Main-app pass — Tasks + F-1: routine sessions must read the task | CLOSED | Amt (Cluster F) | agent/issue-12 (efcb236) | run 40979686336: failure | no | code-on-branch-awaiting-train |
| 13 | Main-app pass — Session Room merge (Klinge 19 + Wacht A-1…A-12) | CLOSED | Amt · finisher Hebel (assist) | agent/issue-13 (3fadf72) | run 40980581351: failure | no | code-on-branch-awaiting-train |
| 14 | Main-app pass — Setup Sheet + onboarding merge (Empfang + Pforte | open | Pforte · finisher Zange | agent/issue-14 (25a5c52) | run 40979848771: failure | no | — |
| 15 | Batch 10 — the everything-merge (epic) | open | Konto — batch-10 merge train | agent/issue-15 (e216481) | run 40967367243: failure | no | — |
| 16 | Pill lane — finish verification, then merge | open | Pforte · finisher Presse | agent/issue-16 (c727dde) | run 40978202034: failure | no | — |
| 17 | Preservation — snapshot #3 | open | Konto · finisher Lotse | agent/issue-17 (390f1e0) | run 40976286152: failure | PR #173 merged · branch active | — |
| 18 | LOOKS programme — frozen by Sarthak's order (record only) | open | — FROZEN (looks record) | — | — | — | — |
| 19 | Decision needed: Q6 — the slide sound | CLOSED | Panel verdict → Riegel closed | — | — | — | decision-only |
| 20 | Decision needed: an urge/feeling question on the block screen? | CLOSED | Panel advisory → Riegel closed | — | — | — | decision-only |
| 21 | Parked decisions — do not work until Sarthak rules | open | — PARKED (his decisions) | — | — | — | — |
| 22 | Master index — every pending order | open | — (master index) | — | — | — | — |
| 23 | Audit R1 · build-gradle — 5 findings (worst: CRITICAL) | open | W3 C1 Grund | — | — | — | — |
| 24 | Audit R1 · ci-work ow — 5 findings (worst: CRITICAL) | open | W3 C1 Grund | — | — | — | — |
| 25 | Audit R1 · signing-secrets — 5 findings (worst: CRITICAL) | open | W3 C1 Grund | agent/issue-25 (12a8d38) | run 40980708500: failure | no | — |
| 26 | Audit R1 · manifest-permissions — 5 findings (worst: HIGH) | CLOSED | W3 C1 Grund | agent/issue-26 (efe1b26) | run 40972442258: failure | no | code-on-branch-awaiting-train |
| 27 | Audit R1 · main-activity — 5 findings (worst: CRITICAL) | open | W3 C2 Motor | agent/issue-27 (1d4d50c) | run 40976230105: failure | no | — |
| 28 | Audit R1 · viewmodel — 5 findings (worst: CRITICAL) | open | W3 C2 Motor | agent/issue-28 (eafdce0) | run 40976362141: failure | no | — |
| 29 | Audit R1 · arm-proving — 5 findings (worst: HIGH) | open | W3 C2 Motor | — | — | — | — |
| 30 | Audit R1 · dao — 5 findings (worst: HIGH) | open | W3 C2 Motor | — | — | — | — |
| 31 | Audit R1 · database — 5 findings (worst: CRITICAL) | open | W3 C2 Motor | — | — | — | — |
| 32 | Audit R1 · models — 5 findings (worst: CRITICAL) | open | W3 C3 Herz | agent/issue-32 (7301510) | run 40974266802: failure | no | — |
| 33 | Audit R1 · repository — 5 findings (worst: HIGH) | open | W3 C3 Herz | — | — | — | — |
| 34 | Audit R1 · app-catalog — 5 findings (worst: CRITICAL) | open | W3 C3 Herz | agent/issue-34 (2575cd6) | run 40973179561: failure | no | — |
| 35 | Audit R1 · app-rules — 5 findings (worst: MEDIUM) | CLOSED | W3 C3 Herz | agent/issue-35 (3091cfb) | run 40972933211: failure | YES | code-merged-to-main (branch ancestor of main) |
| 36 | Audit R1 · crypto-integrity — 5 findings (worst: CRITICAL) | open | W3 C3 Herz | agent/issue-36 (69eef88) | run 40973771849: failure | no | — |
| 37 | Audit R1 · dayword-drawerday — 5 findings (worst: HIGH) | open | W3 C4 Blatt | agent/issue-37 (c20a237) | run 40976232430: failure | no | — |
| 38 | Audit R1 · loadout-store — 5 findings (worst: HIGH) | open | W3 C4 Blatt | agent/issue-38 (16c8db7) | run 40978211115: failure | no | — |
| 39 | Audit R1 · week-routine-derive — 5 findings (worst: CRITICAL) | open | W3 C4 Blatt | — | — | — | — |
| 40 | Audit R1 · receipt-state — 5 findings (worst: CRITICAL) | open | W3 C4 Blatt | — | — | — | — |
| 41 | Audit R1 · undo-wall — 5 findings (worst: CRITICAL) | open | W3 C4 Blatt | — | — | — | — |
| 42 | Audit R1 · accessibility-service — 5 findings (worst: CRITICAL) | open | W3 C5 Posten | agent/issue-42 (6d41807) | run 40976003517: failure | no | — |
| 43 | Audit R1 · shield-service — 5 findings (worst: CRITICAL) | open | W3 C5 Posten | agent/issue-43 (9640f75) | run 40976284341: failure | no | — |
| 44 | Audit R1 · boot-deviceadmin — 5 findings (worst: HIGH) | open | W3 C5 Posten | agent/issue-44 (21961b2) | run 40976242621: failure | no | — |
| 45 | Audit R1 · noti cation-listener — 5 findings (worst: HIGH) | open | W3 C5 Posten | agent/issue-45 (44ea681) | run 40976000829: failure | no | — |
| 46 | Audit R1 · routine-scheduler — 5 findings (worst: CRITICAL) | open | W3 C5 Posten | agent/issue-46 (2ec4e39) | run 40976286444: failure | no | — |
| 47 | Audit R1 · heartbeat-retry — 5 findings (worst: CRITICAL) | CLOSED | W3 C6 Puls | agent/issue-47 (b2b9498) | run 40975912176: failure | YES | code-merged-to-main (PR #172) |
| 48 | Audit R1 · seal-watchstore — 5 findings (worst: HIGH) | open | W3 C6 Puls | agent/issue-48 (323e2c7) | run 40977839731: failure | no | — |
| 49 | Audit R1 · sound-engine — 5 findings (worst: CRITICAL) | open | W3 C6 Puls | agent/issue-49 (2d6f866) | run 40974971942: failure | no | — |
| 50 | Audit R1 · deck-screen — 5 findings (worst: HIGH) | open | W3 C7 Tafel | agent/issue-50 (2cfb9c5) | run 40975244578: failure | no | — |
| 51 | Audit R1 · shield-matrix-screen — 5 findings (worst: HIGH) | open | W3 C7 Tafel | agent/issue-51 (ab80f4c) | run 40973796384: failure | no | — |
| 52 | Audit R1 · drawer-screen — 5 findings (worst: HIGH) | open | W3 C7 Tafel | agent/issue-52 (43c2887) | run 40975963122: failure | no | — |
| 53 | Audit R1 · sequencer-screen — 5 findings (worst: HIGH) | open | W3 C7 Tafel | agent/issue-53 (6ca1a1a) | run 40980305822: failure | no | — |
| 54 | Audit R1 · custom-components — 5 findings (worst: HIGH) | open | W3 C7 Tafel | — | — | — | — |
| 55 | Audit R1 · airlock-overlays — 5 findings (worst: CRITICAL) | open | W3 C8 Rampe | agent/issue-55 (cfa1096) | run 40974687791: failure | no | — |
| 56 | Audit R1 · park-drawer — 5 findings (worst: HIGH) | CLOSED | W3 C8 Rampe | agent/issue-56 (b9245c9) | run 40979226816: failure | no | code-on-branch-awaiting-train |
| 57 | Audit R1 · receipt-sheet — 5 findings (worst: HIGH) | open | W3 C8 Rampe | agent/issue-57 (f316eca) | run 40974267409: failure | no | — |
| 58 | Audit R1 · shield-cap-guardline — 6 findings (worst: HIGH) | open | W3 C8 Rampe | agent/issue-58 (a91f327) | run 40975880848: failure | no | — |
| 59 | Audit R1 · blockit-core-views — 5 findings (worst: CRITICAL) | CLOSED | W3 C8 Rampe | agent/issue-59 (568eed2) | run 40979823158: failure | no · PR #174 OPEN | code-on-branch-awaiting-train (PR #174 open) |
| 60 | Audit R1 · blockit-suitcase-views — 5 findings (worst: CRITICAL) | open | W3 C9 Nachlass | agent/issue-60 (5e584a0) | run 40974004876: failure | no | — |
| 61 | Audit R1 · blockit-tower-views — 5 findings (worst: CRITICAL) | CLOSED | W3 C9 Nachlass | agent/issue-61 (0254539) | run 40974335104: failure | YES | code-merged-to-main (PR #168) |
| 62 | Audit R1 · rail-view — 5 findings (worst: CRITICAL) | CLOSED | W3 C6 Puls | agent/issue-62 (97221a7) | run 40976285082: failure | YES | code-merged-to-main (PR #170) |
| 63 | Audit R1 · theme — 5 findings (worst: HIGH) | open | W3 C6 Puls | agent/issue-63 (443c823) | run 40977840731: failure | no | — |
| 64 | Audit R1 · resources — 5 findings (worst: CRITICAL) | open | W3 C1 Grund | agent/issue-64 (1413286) | run 40977847516: failure | no | — |
| 65 | Audit R1 · tests-1 — 5 findings (worst: HIGH) | open | W3 C9 Nachlass | agent/issue-65 (1d1e03a) | run 40975903947: failure | no | — |
| 66 | Audit R1 · tests-2 — 5 findings (worst: CRITICAL) | CLOSED | W3 C9 Nachlass | agent/issue-66 (202a469) | run 40973836782: failure | no · PR #169 OPEN | code-on-branch-awaiting-train (PR #169 open) |
| 67 | Audit R1 · architecture — 5 findings (worst: CRITICAL) | open | W3 C9 Nachlass | agent/issue-67 (4c08995) | run 40978204767: failure | no | — |
| 68 | Audit R1 · security-privacy — 5 findings (worst: CRITICAL) | open | W3 C10 Prisma | — | — | — | — |
| 69 | Audit R1 · performance-battery — 5 findings (worst: CRITICAL) | open | W3 C10 Prisma | — | — | — | — |
| 70 | Audit R1 · usability-accessibility — 5 findings (worst: CRITICAL | open | W3 C10 Prisma | — | — | — | — |
| 71 | Audit R1 · docs-vs-code — 5 findings (worst: CRITICAL) | CLOSED | W3 C10 Prisma | agent/issue-71 (99188d6) | run 40974334747: failure | no · PR #171 OPEN | code-on-branch-awaiting-train (docs-only; PR #171 open) |
| 72 | Audit R1 · release-issue-map — 5 findings (worst: CRITICAL) | open | W3 C10 Prisma | — | — | — | — |
| 73 | Audit R2 · dep-vulnerabilities — 4 findings (worst: HIGH) | open | W3B Funke | — | — | — | — |
| 74 | Audit R2 · dead-code — 5 findings (worst: HIGH) | open | W3B Funke | — | — | — | — |
| 75 | Audit R2 · logging-debug — 4 findings (worst: HIGH) | open | W3B Funke | — | — | — | — |
| 76 | Audit R2 · error-swallowing — 5 findings (worst: CRITICAL) | open | W3B Funke | — | — | — | — |
| 77 | Audit R2 · concurrency-census — 4 findings (worst: HIGH) | open | W3B Funke | — | — | — | — |
| 78 | Audit R2 · ipc-intents — 3 findings (worst: MEDIUM) | open | W3B Welle | — | — | — | — |
| 79 | Audit R2 · url-blocker-bypasses — 4 findings (worst: HIGH) | open | W3B Welle | — | — | — | — |
| 80 | Audit R2 · blocking-threat-model — 5 findings (worst: HIGH) | open | W3B Welle | — | — | — | — |
| 81 | Audit R2 · tamper-routes — 5 findings (worst: CRITICAL) | open | W3B Welle | — | — | — | — |
| 82 | Audit R2 · clock-attacks — 5 findings (worst: CRITICAL) | open | W3B Welle | — | — | — | — |
| 83 | Audit R2 · storage-hygiene — 4 findings (worst: HIGH) | open | W3B Strom | — | — | — | — |
| 84 | Audit R2 · git-history-secrets-bloat — 5 findings (worst: HIGH) | open | W3B Strom | — | — | — | — |
| 85 | Audit R2 · supply-chain — 5 findings (worst: HIGH) | open | W3B Strom | — | — | — | — |
| 86 | Audit R2 · play-targetsdk-compliance — 4 findings (worst: HIGH) | open | W3B Strom | — | — | — | — |
| 87 | Audit R2 · process-death — 5 findings (worst: CRITICAL) | open | W3B Strom | — | — | — | — |
| 88 | Audit R2 · l10n-rtl-fontscale — 5 findings (worst: HIGH) | open | W3B Flut | — | — | — | — |
| 89 | Audit R2 · asset-bloat — 4 findings (worst: HIGH) | open | W3B Flut | — | — | — | — |
| 90 | Audit R2 · complexity-hotspots — 5 findings (worst: CRITICAL) | open | W3B Flut | — | — | — | — |
| 91 | Audit R2 · test-flakiness — 5 findings (worst: HIGH) | open | W3B Flut | — | — | — | — |
| 92 | Audit R2 · accessibility-config-deep — 5 findings (worst: MEDIUM | open | W3B Flut | — | — | — | — |
| 93 | Audit R2 · backup-rules-deep — 4 findings (worst: HIGH) | open | W3B Brand | — | — | — | — |
| 94 | Audit R2 · oem-battery-reality — 4 findings (worst: CRITICAL) | open | W3B Brand | — | — | — | — |
| 95 | Audit R2 · downgrade-update — 4 findings (worst: CRITICAL) | open | W3B Brand | — | — | — | — |
| 96 | Audit R2 · fresh-install-failures — 4 findings (worst: CRITICAL) | open | W3B Brand | — | — | — | — |
| 97 | Audit R3 · first-launch-setup — 5 findings (worst: HIGH) | open | W3B Weg | — | — | — | — |
| 98 | Audit R3 · arming-a-session — 5 findings (worst: HIGH) | open | W3B Weg | — | — | — | — |
| 99 | Audit R3 · opening-blocked-app — 5 findings (worst: HIGH) | open | W3B Weg | — | — | — | — |
| 100 | Audit R3 · notifications-lived — 5 findings (worst: CRITICAL) | open | W3B Weg | — | — | — | — |
| 101 | Audit R3 · escaping-like-a-user — 4 findings (worst: CRITICAL) | open | W3B Weg | — | — | — | — |
| 102 | Audit R3 · peek-lived — 5 findings (worst: CRITICAL) | open | W3B Pfad | — | — | — | — |
| 103 | Audit R3 · ending-early-lived — 5 findings (worst: HIGH) | open | W3B Pfad | — | — | — | — |
| 104 | Audit R3 · safe-break-lived — 5 findings (worst: HIGH) | open | W3B Pfad | — | — | — | — |
| 105 | Audit R3 · session-end-receipt-lived — 5 findings (worst: HIGH) | open | W3B Pfad | — | — | — | — |
| 106 | Audit R3 · tasks-lived — 5 findings (worst: CRITICAL) | open | W3B Pfad | — | — | — | — |
| 107 | Audit R3 · shield-setup-lived — 5 findings (worst: HIGH) | open | W3B Reise | — | — | — | — |
| 108 | Audit R3 · routines-lived — 5 findings (worst: CRITICAL) | open | W3B Reise | — | — | — | — |
| 109 | Audit R3 · park-guard-lived — 5 findings (worst: CRITICAL) | open | W3B Reise | — | — | — | — |
| 110 | Audit R3 · vault-history-lived — 5 findings (worst: HIGH) | open | W3B Reise | — | — | — | — |
| 111 | Audit R3 · desk-clock-lived — 5 findings (worst: HIGH) | open | W3B Karte | — | — | — | — |
| 112 | Audit R3 · sound-haptics-lived — 5 findings (worst: HIGH) | open | W3B Karte | — | — | — | — |
| 113 | Audit R3 · things-go-wrong-lived — 5 findings (worst: CRITICAL) | open | W3B Karte | — | — | — | — |
| 114 | Audit R3 · moment-of-temptation — 5 findings (worst: CRITICAL) | open | W3B Karte | — | — | — | — |
| 115 | Audit R3b · low-tech-user — 5 findings (worst: HIGH) | open | W3B Larve | — | — | — | — |
| 116 | Audit R3b · night-half-asleep — 5 findings (worst: HIGH) | open | W3B Larve | — | — | — | — |
| 117 | Audit R3b · one-hand-commute — 5 findings (worst: CRITICAL) | open | W3B Larve | — | — | — | — |
| 118 | Audit R3b · urgent-contact-mid-session — 5 findings (worst: CRIT | open | W3B Larve | — | — | — | — |
| 119 | Audit R3b · real-emergency-exit — 5 findings (worst: CRITICAL) | open | W3B Larve | — | — | — | — |
| 120 | Audit R3b · otp-banking-mid-session — 5 findings (worst: CRITICA | open | W3B Huelle | — | — | — | — |
| 121 | Audit R3b · alarm-fires-mid-session — 3 findings (worst: HIGH) | open | W3B Huelle | — | — | — | — |
| 122 | Audit R3b · low-battery-mid-session — 4 findings (worst: HIGH) | open | W3B Huelle | — | — | — | — |
| 123 | Audit R3b · dial-precision — 5 findings (worst: HIGH) | open | W3B Huelle | — | — | — | — |
| 124 | Audit R3b · typing-intention — 4 findings (worst: HIGH) | open | W3B Huelle | — | — | — | — |
| 125 | Audit R3b · armed-wrong — 5 findings (worst: CRITICAL) | open | W3B Miene | — | — | — | — |
| 126 | Audit R3b · seal-wait-confusion — 5 findings (worst: HIGH) | open | W3B Miene | — | — | — | — |
| 127 | Audit R3b · glance-time-remaining — 5 findings (worst: HIGH) | open | W3B Miene | — | — | — | — |
| 128 | Audit R3b · accidental-rail-park — 5 findings (worst: HIGH) | open | W3B Miene | — | — | — | — |
| 129 | Audit R3b · unlisted-distraction — 5 findings (worst: CRITICAL) | open | W3B Miene | — | — | — | — |
| 130 | Audit R3b · browser-as-loophole — 5 findings (worst: CRITICAL) | open | W3B Gestalt | — | — | — | — |
| 131 | Audit R3b · shorts-reels-lived — 4 findings (worst: CRITICAL) | open | W3B Gestalt | — | — | — | — |
| 132 | Audit R3b · music-during-focus — 4 findings (worst: HIGH) | open | W3B Gestalt | — | — | — | — |
| 133 | Audit R3b · study-on-phone-itself — 5 findings (worst: HIGH) | open | W3B Gestalt | — | — | — | — |
| 134 | Audit R3b · phone-call-mid-session — 4 findings (worst: HIGH) | open | W3B Gestalt | — | — | — | — |
| 135 | Audit R3b · back-to-back-sessions — 4 findings (worst: HIGH) | open | W3B Abbild | — | — | — | — |
| 136 | Audit R3b · sunday-review — 5 findings (worst: HIGH) | open | W3B Abbild | — | — | — | — |
| 137 | Audit R3b · sick-day-word — 5 findings (worst: CRITICAL) | open | W3B Abbild | — | — | — | — |
| 138 | Audit R3b · digest-too-late — 5 findings (worst: CRITICAL) | open | W3B Abbild | — | — | — | — |
| 139 | Audit R3b · custom-profile-lived — 5 findings (worst: CRITICAL) | open | W3B Abbild | — | — | — | — |
| 140 | Audit R3b · daily-limit-ratchet-lived — 4 findings (worst: CRITI | open | W3B Echo | — | — | — | — |
| 141 | Audit R3b · rocker-mistaps — 4 findings (worst: HIGH) | open | W3B Echo | — | — | — | — |
| 142 | Audit R3b · finding-an-app-in-shield — 5 findings (worst: HIGH) | open | W3B Echo | — | — | — | — |
| 143 | Audit R3b · night-flip-mid-session — 6 findings (worst: HIGH) | open | W3B Echo | — | — | — | — |
| 144 | Audit R3b · tower-as-motivation — 4 findings (worst: HIGH) | open | W3B Echo | — | — | — | — |
| 145 | Audit R3b · curio-reward-moment — 5 findings (worst: HIGH) | open | W3B Umriss | — | — | — | — |
| 146 | Audit R3b · break-away-from-phone — 5 findings (worst: CRITICAL) | open | W3B Umriss | — | — | — | — |
| 147 | Audit R3b · stopwatch-vs-timed-confusion — 5 findings (worst: CR | open | W3B Umriss | — | — | — | — |
| 148 | Audit R3b · multi-block-surprises — 5 findings (worst: HIGH) | open | W3B Umriss | — | — | — | — |
| 149 | Audit R3b · false-start-grace — 5 findings (worst: CRITICAL) | open | W3B Umriss | — | — | — | — |
| 150 | Audit R3b · sound-in-quiet-places — 5 findings (worst: HIGH) | open | W3B Profil | — | — | — | — |
| 151 | Audit R3b · friend-holds-the-phone — 5 findings (worst: CRITICAL | open | W3B Profil | — | — | — | — |
| 152 | Audit R3b · honest-travel-day — 4 findings (worst: HIGH) | open | W3B Profil | — | — | — | — |
| 153 | Audit R3b · change-of-plans-day — 5 findings (worst: CRITICAL) | open | W3B Profil | — | — | — | — |
| 154 | Audit R3b · need-one-app-legit — 5 findings (worst: HIGH) | open | W3B Profil | — | — | — | — |
| 155 | Audit R3b · no-widget-glance-cost — 5 findings (worst: HIGH) | open | W3B Wesen | — | — | — | — |
| 156 | Audit R3b · park-first-run-autopen — 5 findings (worst: HIGH) | open | W3B Wesen | — | — | — | — |
| 157 | Audit R3b · receipt-dismissed-forever — 5 findings (worst: CRITI | open | W3B Wesen | — | — | — | — |
| 158 | Audit R3b · month-in-review — 5 findings (worst: HIGH) | open | W3B Wesen | — | — | — | — |
| 159 | Audit R3b · routine-fires-in-class — 4 findings (worst: HIGH) | open | W3B Wesen | — | — | — | — |
| 160 | Audit R3b · two-phones-tablet — 5 findings (worst: CRITICAL) | open | W3B Figur | — | — | — | — |
| 161 | Audit R3b · update-day — 5 findings (worst: CRITICAL) | open | W3B Figur | — | — | — | — |
| 162 | Audit R3b · deleting-in-anger — 5 findings (worst: HIGH) | open | W3B Figur | — | — | — | — |
| 163 | Audit R3b · morning-after-a-failed-day — 5 findings (worst: HIGH | open | W3B Figur | — | — | — | — |
| 164 | Audit R3b · reading-the-words — 5 findings (worst: HIGH) | open | W3B Figur | — | — | — | — |
| 166 | CI verdict follow-up: 37 real test failures now that the suite r | open | — unassigned | — | — | — | — |

## Non-issue branches
- `agent/design-revamp` (f56ddfe) — last CI: run 40977867656: failure — in main: no · PR #167 merged · branch active
- `agent/scouts` (f19c2d1) — last CI: run 40979040272: failure — in main: no
- `batch-10` (c61859b) — last CI: run 40977857349: failure — in main: no
- `main` (0a00e95) — last CI: run 40978334853: failure — in main: YES

Register kept by REGISTRAR (Keepers of Record). Refreshed from gh on every pass; state fields are gh-sourced, never agent-reported.
