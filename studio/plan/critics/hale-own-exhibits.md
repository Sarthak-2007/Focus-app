# HALE — coordinator's own exhibits (verified firsthand against current code)

## A. TRUTH — the data layer (all in data/MonolithRepository.kt + ui/MonolithViewModel.kt unless noted)

| # | Exhibit | Evidence | Verdict |
|---|---------|----------|---------|
| A1 | **The app seeds three fake completed sessions into the real history table on first run** — "Organic Chemistry Problem Set" 50m, "Algorithms & Data Structures Lab" 75m, "Read Constitutional Law Ch. 6" 25m (= 150 min), plus a fake TikTok intercept log. No seed flag; indistinguishable from real records forever. | Repository ensureSeeded (~L231–300): `dao.insertSession(...)` ×3 with `completedSuccessfully = true`, forged artifact IDs | LIE — structural. The Vault's wave-1 hero "150 focused minutes" was 100% fiction. Every aggregate (totals, 7-day, receipts, bricks) inherits the pollution. Plan must carry: seeds die; demo data never shares a table with the record. |
| A2 | **totalFocusedMinutes is floored at 35 and initialised at 75.** | ViewModel L280–287: `((completedSecs + activeSecs) / 60).coerceAtLeast(35)`, StateFlow initial `75` | LIE. With zero real seconds the app claims ≥35 focused minutes; first frames claim 75. |
| A3 | **totalInterceptions is initialised at 6 and computed as max(two rival ledgers).** | ViewModel L289–293: initial `6`; `maxOf(logs.size, appList.sumOf { interceptCount })` | LIE + one-fact violation. Two accounting systems, print the larger. |
| A4 | **Early quits are written into the deflections ledger.** disarmActiveSession inserts an InterceptLogEntity for every non-grace early end ("Partial Stamp (Xm)" ≥60s, "Unlatched Early" <60s). | Repository L1101–1112 | LIE by category: DEFLECTED counts his own exits as blocked-app deflections (via the logs.size branch of A3). |
| A5 | **Disarm banks the kinder number.** `bankedElapsed = max(verifiedElapsed, active.elapsedSeconds)`. | Repository L1089 | TRUTH violation in the generous direction: when verification and the ticker disagree, the record takes the larger. |
| A6 | **7-day blocks inflate.** `blocks25 = (totalMins / 25).coerceAtLeast(if (totalMins >= 15) 1 else 0)` — 15 minutes renders as a full 25-minute block. | Repository L1138 | LIE (small, systematic). |
| A7 | **Routine windows mint completed sessions unattended.** Receiver START calls `repository.startSession(...)` with zero gestures; END disarms with `completed = true`. Elapsed accrues by wall-clock verification whether or not he was present. | service/RoutineAlarmReceiver.kt (whole file) | GHOST RECORDS — the exact fraud settled law bans. A 05:00–08:00 window records a completed 3-hour session for a man who may be sick, travelling, or awake elsewhere. |
| A8 | **Vault tiles fabricate.** SESSIONS shows `sessionHistory.size.coerceAtLeast(4)`; TOWER BRICKS = `(totalFocusedMinutes / 25) + completedCount`, floored at 16; receipts print `(elapsedSeconds/60).coerceAtLeast(1)` minutes, "+" included for failed sessions; receipt list silently truncates to 8 under a header that counts all. | ui/screens/CurioVaultScreen.kt L59–61, L121, L195, L190 | LIES, plural, in the tile arithmetic itself. Full table in Nox+Lind's file; these four are mine, read firsthand. |

## B. HOLD — the seal's soft underlayer (coordinator's exhibits)

| # | Exhibit | Evidence | Verdict |
|---|---------|----------|---------|
| B1 | **Safe Break: the seal opens itself on schedule.** Multi-block sessions auto-enter a 10-min break at each block boundary; PEEK_2M apps unlock freely during it; break minutes bank as session elapsed with no distinction in the record. | Repository L971–1013; MonolithAccessibilityService L102 | HOLD violation by design + TRUTH violation in the record. Mailed to Zug (exit verdict) and Quaere (visibility verdict). |
| B2 | **The airlock is a questionnaire at the moment of temptation.** Every blocked launch opens an overlay demanding: pick a distraction reason (rotary-detent picker), optionally tap particles (`incrementAirlockParticlePulse` — a tap counter with rising pitch), then choose peek vs transmute. | ViewModel L966–1048 | HOLD violation: at least three decisions, priced at his weakest second. Quaere prices it fully. |
| B3 | **Stopwatch sessions exit by choice after a 20-min floor — no valve, no token.** | ViewModel requestDisarmChallenge L927–941 | An exit class the "unbreakable" claim never mentions. Zug's verdict to make. |

## C. ATTACK ON THE EMERGING PLAN (SYNTHESIS.md, measured against settled law)

The synthesis was merged BEFORE Sarthak settled the law (concept = none; routines his; hold banked; Ledger now OPEN again). These items in it are dead or unlawful and the master plan must not carry them:

| # | Synthesis item | Why it falls |
|---|----------------|--------------|
| C1 | R2.17 NEXT ribbon printing "FIRES ITSELF" + mise en place + slide demotion inside routine windows | Advertises the exact machine-agency he killed ("it will just collide with actual life"). A NEXT line printing a fact (time · loadout) is lawful; the firing semantics beneath it are the open wound (A7). The plan must specify routine firing as a RECORDING question, not a copy question. |
| C2 | Part 2, D-D Consent Beat presented as the routines answer | Superseded by his settlement: routines are HIS tools + no ghost records. Consent Beat is at most one candidate mechanism; the master plan must re-derive routine behaviour from the standards, not inherit D-D. |
| C3 | R3.24 "3 misses → auto-pause with a note" | The machine edits his routine. Violates "rested user decides" — auto-pause is a decision taken at his tiredest, by the app. Lawful form: a printed fact on the row (`MISSED ×3`) that HE acts on. |
| C4 | R3.24 "attendance logged" | Attendance currently means "the window elapsed" (A7). Any plan line about attendance must define presence evidence or be struck. |
| C5 | R6.34 chrome war line `TODAY 2H30 / 12H` | Monolith cannot know his day — YPT owns study. A chrome total dressed as his day-total is a lie with a denominator. Lawful forms only: app-scoped and labelled (`SEALED 2H30`), or silence. Same for `186D TO NEET`: the exam date is unannounced (~May 2 2027 estimate); a countdown printed as fact must carry its basis or die. |
| C6 | R2.19 First Light dawn ceremony; Phase-2 Program Tape; R5 accession stamp ceremony | Narrative machinery. The concept verdict killed story as a class ("a gesture lives in the hand, a concept lives in a paragraph"). The session-end RECEIPT survives only as a truthful statement (see LEDGER verdict file), stripped of ceremony-as-retention theatre. |
| C7 | R1.12 accent budget "Ledger 1 (accession №)" | Suspended — Ledger is OPEN. No budget line may presume the verdict. |
| C8 | R2.16 readout/caption/badge duration trio | One-fact law: exactly ONE duration figure may be hero. Current build still renders readout (block) + "MIN TOTAL" caption + slide badge "25M" + FINISHES — Vera+Una have the live count. The plan must name the survivor (total, per D5) and kill the rest by name. |
| C9 | R4.28 Tasks sound family | Survives, with wave-1 sound law attached: fixed pitch SETS, silence map (no navigation/settings chirps), routine firing gets a sound or the silence is a lie about the headline feature. |
| C10 | Guard-dependent items (R3.26 grouping, R6 drawer) | Guard is HIS OPEN PICK. Every plan element that presumes dissolve OR destination must be written in both branches or flagged. Flagged here as a global dependency. |

— Hale

## D. Coordinator's spot-verification of seat headlines (Prima, Moss — checked firsthand)

- Prima's pre-flight charge CONFIRMED: MainActivity wires the slide's commit to
  `viewModel::requestArmWithPreFlightCheck` — the permission check runs AFTER
  the committing gesture. And the celebration precedes the fact: the slide view
  plays `playHarmonicSeal` on commit, then `armMonolithSession()` plays it
  AGAIN before `repository.startSession` is even launched. Two seals, zero
  sessions yet.
- Moss's FLOW trap CONFIRMED: `selectStudyPreset` (ViewModel L382–387) writes
  dial := preset.workMinutes (5) AND blocks := preset.defaultBlocks in one
  call; the Deck's FLOW chip is the only caller of `onSelectStudyPreset` in
  the app — no surface ever selects another preset back. One stray tap is
  permanent until restart, and the Deck then advertises stopwatch/"NO LIMIT"
  while the repository arms timed sessions.
- ARM totals so far (Prima + Moss): happy path 2 gestures, ~2–3.5s — PASS.
  The budget dies only where the permission is dead (Prima: 9–12 gestures,
  12–35s+) and where a stray FLOW/hub tap re-prices the session silently.

## E. Corrections + consolidation notes (from seat returns)

- **CORRECTION to my B2:** Quaere's render-level audit shows the airlock's
  reason picker and particle counter DO NOT EXIST in the rendered overlay —
  the ViewModel callbacks are dead (`setAirlockReason`, `incrementAirlockParticlePulse`,
  `splitActiveTaskAndReturn` never called from UI). The rendered airlock is a
  RETURN/PEEK binary — lawful. The real exhibits are: (1) dead machinery that
  the plan must delete, not build; (2) every intercept is still logged with a
  fabricated cause "Habit" — a false fact written into the record (TRUTH).
  My B2 stands corrected accordingly.
- **HOLD's worst hole (Quaere):** the pull-down drawer is reachable in one tap
  mid-session and its Shorts-sniper + browser-link-blocker toggles have NO
  session check in code — one tap each disables real blocking while sealed.
  HOLD absolute currently has a one-tap back door in the top bar.
- **HOLD scarcity is fake (Day8):** the weekly emergency token is in-memory
  ViewModel state — restarting the app refills it. The 1/week cap does not
  survive a reboot.
- **Routine manufacture (Day8):** left alone, the two undeletable seed routines
  mint ≈750 min/day of "completed" focus (≈87 h in an untouched week);
  CRUD = 1 of 4 (pause only; DAO has no routine delete). Seeds contradict his
  day on every line (09:00 start vs 05:00 fasted block; SLEEP until 07:00 vs
  03:30 wake).
- **TRUTH headline count (Vera+Una):** session duration is rendered 5 ways that
  mutually disagree, incl. FINISHES an hour early at default state and a %
  that is percent-remaining, unlabeled. Drawer prints "DEFLECTED" with NO
  number. Stopwatch defined two ways (Deck vs Repository) — NO LIMIT on
  screen, timed session in the engine.
- **Ledger leans:** Quaere (b) demote — receipt as event, not place; Day8 (b)
  demote — kill tab, completion receipt + one Sunday week-line. Awaiting
  Nox+Lind (rendering evidence).
