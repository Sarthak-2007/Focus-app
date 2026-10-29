# TRUTH AUDIT, PART 2 — Vault corpse · overlay copy · Tasks claims · night pass

**Seats:** Nox (night) + Lind (typography), Critics, one audit hand. **Coordinator:** Hale.
**Scope read, not skimmed:** `CurioVaultScreen.kt`, `CryptoIntegrityManager.kt`,
`MonolithRepository.kt` (sign / verify / `computeVerifiedSessionElapsed` / `disarmActiveSession` /
`compute7DayHistory` / `exportEncryptedBackup`), `MonolithViewModel.kt` (totals, token, disarm),
`AirlockAndDisarmOverlays.kt`, `Emergency60sValveView`, `DayFadersView`, `TowerStreakView`,
`TileNumberCounterView`, `TaskHorizonBarView`, `FocusSequencerScreen.kt`, `Type.kt`, `Color.kt`,
the shipped font files (weight classes measured from the TTFs), and the current night renders
`shots/night/n01–n05`.
**Law applied:** Concept = NONE · ARM ≤10s · HOLD absolute · TELL THE TRUTH once, in human
language · hold is **BANKED** (60s total, progress banks at 15/30/45, a slip drains, one use/week) ·
Ledger tab was killed by his word, then **reopened by him** for a cohort verdict (see final section).

Verdicts: **TRUE** = the code/render does what the words say · **LIE** = it does not, and a user
would believe it · **STALE** = was true / is a dead second source · **DEAD-BUT-RENDERED** = the
thing was killed or never lived, and it still shows on screen.

---

## A. The Vault / Ledger corpse (`CurioVaultScreen.kt`)

Still fully wired: `MainActivity` routes destination `VAULT` (index 3) to this screen, the bottom
nav still renders a **LEDGER** tab for it (render n04), and navigating to it recomputes the 7-day
history. Nothing about it knows it died.

| Claim (exact string / figure + where) | Source | Verdict | Truthful replacement |
|---|---|---|---|
| `FOCUSED MIN` tile value — `totalFocusedMinutes`, floored: `((completed+active secs)/60).coerceAtLeast(35)`, initial state **75** | `MonolithViewModel.totalFocusedMinutes` | **LIE** | On a fresh install, with zero sessions ever, this tile opens at 35–75 minutes of focus that never happened. Replacement: the real sum, 0 included. A record that cannot say 0 is not a record. |
| `SESSIONS` tile — `sessionHistory.size.coerceAtLeast(4)` | `CurioVaultScreen` §1 | **LIE** | Same disease, second organ: session count floored at 4. Replacement: the real count. |
| `VERIFIED SESSION RECEIPTS` header — `TOTAL {sessionHistory.size}` (unfloored) | `CurioVaultScreen` §4 | **LIE (by contradiction, on one screen)** | Render n04 shows SESSIONS **4** in the tile and TOTAL **3** in the receipts header, ~600dp apart. One fact, two numbers, both on screen at once. Replacement: one count, once — or no count at all (see verdict section). |
| `TOWER BRICKS` — `(totalFocusedMinutes/25 + completedCount).coerceAtLeast(16)`; tower overlay copy of the same formula uses `coerceAtLeast(14)` | `CurioVaultScreen`; `StreakTowerMonumentOverlay` | **LIE** | Bricks are not a fact; they are focused-minutes re-divided, then floored twice at two different floors (16 vs 14) in two places. In n04: 150 min and 3 completed sessions are really 9 bricks; the screen renders **16**. Replacement: none. Delete the derived unit. |
| Dot-matrix + crisp numeral in every tile (`TileNumberCounterView`: 3-digit 4dp dot grid on the left, plain numeral right-aligned, same value) | `BlockitTowerFadersAndCatalogViews.kt` | **TRUE but double-encoded** | Both encodings agree by construction (same `numberValue`) — so the dot grid carries zero information and costs decoding time on every glance. Two divergences hide in it: the dot grid is capped `%03d`, `coerceIn(0,999)` — past 999 focused minutes the dots freeze at 999 while the numeral keeps counting; and the dots render leading zeros (`004`) where the numeral renders `4`. Keep exactly one encoding: the numeral. |
| `7-DAY FOCUS FADERS` — absent day renders `0m` **and** a stub bar: fill fraction `coerceIn(0.12f, 1f)` | `DayFadersView.onDraw`; data from `compute7DayHistory` | **LIE (rendering)** | The numbers are honest (a day with no sessions sums to 0), the picture is not: in n04, FRI–MON each show `0m` over a visible cream bar ~12% of track height. Absence is drawn as a small presence. Replacement: 0 minutes = no bar, full stop. |
| 7-day fallback — if `statsList.size < 7`, a hardcoded week renders: MON 45m … SAT 110m, dated 2026-10-22…28 | `DayFadersView.onDraw` | **DEAD-BUT-RENDERED** | Invented history, shipped. It only appears in the first frames before the ViewModel loads (initial state is `emptyList()`), but a truth surface may not open with fiction even briefly. Replacement: render nothing until the real list exists. |
| `compute7DayHistory` — `blocks25 = (mins/25).coerceAtLeast(if (mins>=15) 1 else 0)`; `interceptionsCount` per day | `MonolithRepository.compute7DayHistory` | **STALE** | Both fields are computed every time and rendered nowhere (the faders draw minutes only). The ≥15m→1-block bump is also a small inflation that never even gets shown. Dead computation feeding a dead screen. |
| Multi-block sessions: at each block boundary `elapsedSeconds = 0`, `startWallTimeMillis` re-anchored, HMAC re-signed — one session row survives | `MonolithRepository.updateSessionProgress` | **LIE (upstream of every total)** | A 2×25m session's stored elapsed is the *last block only*. Every total, fader and receipt downstream undercounts multi-block work — the totals lie downward here while the floors lie upward. Replacement (data, if any record survives): accumulate elapsed across blocks; never reset it. |
| Receipt row: `+{mins}m` — `(elapsedSeconds/60).coerceAtLeast(1)` | `CurioVaultScreen` §4 | **LIE** | A 20-second aborted session prints `+1m`. Combined with the row above, a receipt's minutes are wrong in both directions depending on how the session ran. Replacement: real minutes, and `0m` / "under a minute" when that's the truth. |
| Receipt row: intention + `+Xm` coloured `primaryContainer` (night orange `#F47442`) when `completedSuccessfully` | `CurioVaultScreen` §4; render n04 (`+25m` in orange) | **TRUE value, accent violation** | The one number on the row is spent in the commit colour. Orange is COMMIT fill only, chrome zero. Replacement: ink/cream like every other readout. |
| `VERIFIED SESSION RECEIPTS` (the word VERIFIED) + per-row stamp `HMAC•{hmacSignature.take(8).uppercase()}` | Header in `CurioVaultScreen` §4; `CryptoIntegrityManager` | **LIE — the worst on this screen** | What the stamp actually proves: the HMAC covers exactly five fields — start wall time, start monotonic time, boot count, target duration, stopwatch flag. It does **not** cover `elapsedSeconds`, the intention, `tasksCompletedCount`, or `completedSuccessfully` — i.e. it does not cover *a single field the receipt displays*. Verification is never run for display; a session flagged `tamperDetected` still lists as a VERIFIED receipt with no mark. A blank signature verifies as **true** (`if (hmacSignature.isBlank()) return true`) — and in render n04 the stamp renders literally as `HMAC•` with nothing after it. On reboot the signature is *re-signed* over the new anchor, so even the start-time proof is a current statement, not an original one. Replacement copy, if a stamp survives at all: none — remove the word VERIFIED and the fragment. The honest sentence is "this app wrote this down," which needs no stamp. |
| HMAC / AES fallback keys — hardcoded strings in the APK: `"Monolith_Hardware_HMAC_Fallback_32B!"`, `"Monolith_AES256_Backup_Key_32By!"` | `CryptoIntegrityManager` | **LIE (conditional)** | When AndroidKeyStore is unavailable, signing/encryption fall back to keys printed in the shipped binary — forgeable/decryptable by anyone who reads the APK. The hardware path is real; the fallback silently downgrades "hardware-backed" to obfuscation with no surface saying so. Replacement: fail closed and say "backup unavailable on this device," or drop the claim. |
| `EXPORT AES-256-GCM LEDGER BACKUP` chip; badge `READY` when payload non-empty; `onImportBackupJson` wired to… `exportEncryptedStateBackup()` again | `CurioVaultScreen` §4; `MainActivity` wiring; `MonolithRepository.exportEncryptedBackup` | **LIE** | Three lies in one chip: (1) clicking "EXPORT" writes nothing anywhere — no file, no share sheet, no clipboard; it regenerates a string in memory and lights READY. (2) The payload contains app rules + two counts (`completedSessionsCount`, `totalFocusedMinutes`) — **no sessions, no receipts, no 7-day history** — so the "LEDGER BACKUP" cannot restore the ledger. (3) There is no import; the import callback re-exports. The AES-256-GCM itself (256-bit keystore key, 128-bit tag, random IV) is genuinely implemented — for a payload that goes nowhere and omits the thing it names. Replacement: either a real share/export of the actual session records, labelled "Backup of block rules and totals," or delete the chip. |
| Curio artifacts / harmonic synth params (`selectedArtifact`, `harmonicSynthUnlocked`, `onSelectArtifact`, `onPlayHarmonicChord`) still plumbed into the screen | `CurioVaultScreen` signature | **DEAD-BUT-RENDERED (plumbing)** | The "Curio" half of the Vault is already gone from the body; its wires still run through the corpse. Whatever survives, this doesn't. |
| Screen identity: file/destination `CurioVault` / `VAULT`, nav label `LEDGER`, header `TAB 3: LEDGER & STREAK TOWER` | `MainActivity`, bottom nav (n04), file doc comment | **STALE** | Three names for one corpse. Naming drift is how dead things keep getting rebuilt by accident. |

---

## B. The overlays' copy (`AirlockAndDisarmOverlays.kt` + valve + Deck badges)

Settled law for this section: the hold is **BANKED** — 60 seconds total, progress banks at
15/30/45s, a slip drains banked progress rather than zeroing it, one use per week.

| Claim (exact string / figure + where) | Source | Verdict | Truthful replacement |
|---|---|---|---|
| "Sessions are unbreakable by tap." | `DisarmChallengeOverlay` body text | **STALE / overbroad** | In the first 10 seconds a session *is* ended by one tap (GRACE WINDOW ABORT, Deck); a stopwatch session past its 20-minute floor ends on request with no valve at all (`requestDisarmChallenge` completes it directly). Replacement: "This session can't be ended by a tap. The one way out is below." — said only when it's true, i.e. outside the grace window. |
| "Hold the physical valve **continuously** for 60 seconds to abort." + valve label `HOLD 60S FOR WEEKLY EMERGENCY BREAK` | `DisarmChallengeOverlay`; `Emergency60sValveView` | **LIE — direct contradiction of settled law** | His law is BANKED. The shipped valve is the opposite: `ACTION_UP / ACTION_CANCEL → holdProgress = 0f`. A slip at 59s costs the whole minute; nothing banks at 15/30/45; nothing drains — it zeroes. The copy describes the mechanism accurately and the mechanism breaks his decision. Replacement copy (and mechanism): "Hold the valve. Your hold banks every 15 seconds — 60 seconds in total ends the session. Letting go drains what hasn't banked, it doesn't erase what's banked." |
| "You have {n} **weekly** emergency break token remaining." (also Deck badge `{n}/1 WEEK`, valve badge `{n}/1 LEFT`) | `DisarmChallengeOverlay`; `TactileDeckScreen` chip `EMERGENCY BREAK VALVE (60S HOLD)` badge `"$weeklyEmergencyTokensRemaining/1 WEEK"`; `Emergency60sValveView` | **LIE** | There is no week anywhere in the code. The token is a `MutableStateFlow(1)` **in memory**: never persisted, never reset on any calendar boundary. Kill the app and the "weekly" token is back. It also decrements on *any* non-grace `confirmDisarmSession`, not only a completed valve hold. Replacement: until the token is persisted with a real week boundary, the honest badge is `1 BREAK LEFT` with no time word — or persist it and earn the word WEEK. (Grammar, Lind's note: "1 … token remaining" / "0 … token" — the string never pluralises.) |
| Valve at 0 tokens: label `NO WEEKLY EMERGENCY TOKENS REMAINING`, touch returns false | `Emergency60sValveView` | **TRUE mechanism, STALE word** | It does refuse. But per the row above it means "none remaining until the app process restarts," which is not what it says. |
| Desk clock header: `NIGHTSTAND DESK CLOCK • TAP ANYWHERE TO EXIT` | `AlwaysOnDeskClockOverlay` | **TRUE** | The whole surface is clickable → dismiss; Round-0 restored `FLAG_KEEP_SCREEN_ON`, so "always-on" is now mechanically true, and the ground is true black (`Color.Black`) per the locked AOD rule. One of the few clean claims in this file. |
| Desk clock progress bar when **no active session** (or stopwatch): `progress = 0.65f` hardcoded | `AlwaysOnDeskClockOverlay` | **LIE** | With nothing running, the clock draws a bar filled to 65% — a progress figure invented on the spot, rendered in the same cream, same track, as a real session's remaining fraction. Replacement: no bar when there is no session; in stopwatch, show elapsed fraction of the floor or no bar. |
| Desk clock hero time: unlabeled — remaining seconds in countdown mode, elapsed in stopwatch, `dialMinutes × 60` when idle | `AlwaysOnDeskClockOverlay` (`displaySecs` chain) | **STALE / ambiguous** | One numeral, three different meanings depending on hidden state, no word saying which. TELL THE TRUTH once, in human language: label it — `REMAINING` / `ELAPSED` — or, idle, don't dress the dial setting up as a clock reading. (Code-comment rot, Lind: the comment says 112sp, the text is 96sp; comments elsewhere in this app still say "Space Mono" for what is Anonymous Pro — see §D.) |
| Desk clock task banner fallback: `activeTask?.title ?: activeSession?.intention ?: "Deep Focus Session"` | `AlwaysOnDeskClockOverlay` | **STALE** | With neither task nor session, the clock announces a "Deep Focus Session" that does not exist. Replacement: blank, or the dial's actual intention text. |
| Airlock: countdown `05 → 00`, peek chip appears only at 0 and only if `canUse2MinutePeek` | `AirlockInterceptOverlay` | **TRUE** | The 5-second wall is real and the gating is honest. |
| Airlock chip: `EMERGENCY PEEK` badge `2m` | `AirlockInterceptOverlay` | **STALE naming** | This is the 2-minute peek lease (20-minute cooldown, per-app tier) — it is *not* the emergency break and consumes no weekly token. Calling two different doors "emergency" teaches him the wrong price for both. Replacement: `PEEK — 2 MINUTES`, badge `2m`. |
| Airlock chip: `RETURN TO FOCUS` with `isAccentArmed = true` (accent fill) | `AirlockInterceptOverlay` | **Accent violation (borderline)** | Returning to focus is the safe act, not the ARM commit; spending the one orange fill on it dilutes the only meaning orange is allowed to have. Replacement: ink fill. (The disarm valve's hold-progress fill in accent is the one defensible exception in this file — it *is* the break committing, filling only while he holds it. Keep that one, say so in the plan so it isn't "fixed" by mistake.) |
| Deck chip: `GRACE WINDOW ABORT {10 − elapsed}s`, first 10s only, free | `TactileDeckScreen`; `confirmDisarmSession("Accidental Start Grace Cancel")` | **TRUE** | Real window, real countdown, token untouched, elapsed zeroed (≤10s — nothing real is erased). The honest exception that makes "unbreakable by tap" a lie as written. |
| Primer modal: "Grant Accessibility & Usage Access for **hardware-level** app interception, or arm session immediately." + chip `COMMIT & ARM SESSION NOW` | `ShieldPermissionPrimerModal` | **STALE wording** | Interception is OS-service-level, not hardware-level; and the modal is honest that arming works without the grants — which quietly admits the session can be "armed" with no interception behind it. Replacement: "Without these, Monolith can't stop other apps — a session would be a timer, not a lock." Then let him choose with true information. |
| Digest sheet empty state: "Zero notifications intercepted during focus." | `CapturedNotificationsDigestSheet` | **TRUE (when empty)** | Fine. Noted only so the sweep is complete. |

---

## C. Tasks screen claims (`FocusSequencerScreen.kt` vs its ViewModel)

| Claim (exact string / figure + where) | Source | Verdict | Truthful replacement |
|---|---|---|---|
| Header: `HORIZON` — `{orderedQueue.size} TASKS • {computedTotalMins}m` | Screen recomputes from `orderedQueue` (active + queued) | **TRUE (rendered figure)** | Render n03: 3 tasks, 25+25+45 = **95m** — checks out against the rows and the bar. Keep the figure; kill its twin (next row). |
| `totalQueueMinutes` (ViewModel): sums ACTIVE+QUEUED then `.coerceAtLeast(_dialMinutes.value)`, initial value **50** | `MonolithViewModel.totalQueueMinutes`; passed toward the screen, never used by it | **STALE — dead second source** | A second, divergent definition of the same total exists, floored at the dial setting (an empty queue would "total" 50 minutes). The screen ignores it and computes honestly — this time. One fact, one source: delete the ViewModel flow or make the screen use it unfloored. |
| Horizon bar segment widths: weight = `estimatedMinutes.coerceAtLeast(10)`; segment labels dropped when `segW < 38dp` | `TaskHorizonBarView` | **LIE (proportion, small tasks)** | The header sums raw estimates; the bar inflates any task under 10m to 10 for width. A 5m task draws at double its share of the strip the header just totalled. Replacement: width from the same raw minutes as the header; if a segment is too narrow for its label, the label goes — the width doesn't change. |
| Queue header: `DONE {completedTasks.size}`; rows `#01 · 25m`, `ACTIVE` / `QUEUE` | `FocusSequencerScreen` §3 | **TRUE** | Counts and per-row estimates match the entities. (n03's `DONE 1` + 3 open rows are consistent.) |
| Cued row action: `START {estimatedMinutes}m` — in night orange (`primaryContainer`) as **text** | `FocusSequencerScreen` §3 + its own comment: "The screen's one orange lives on the cued task's START — as text." | **LIE + accent violation** | It does not start anything: the tap cues the task and navigates to the Deck (`onCueTaskToFocus` + `onNavigateToDeck`); the session still has to be armed there. And orange-as-text in chrome is exactly what the accent budget forbids — orange is a COMMIT *fill*. Replacement: label it what it does — `CUE` like the other rows, in ink — and let the Deck's slide own the word START and the colour. |
| Composer placeholder: "Add single-line focus task..." + duration keycaps 15/25/45/90m + `ADD` | `FocusSequencerScreen` §2 | **TRUE** | Does what it says; estimates clamp 5–180 in the repository (keycaps stay inside that). |
| Undo banner: `REMOVED: {title}` + `UNDO` | `FocusSequencerScreen` | **TRUE** | Real 5-second undo window in the ViewModel, restore is real. |

---

## D. Night verification pass — renders vs the locked staircase, fonts, orange census

**Locked:** ground `#171412` · steps ≥1.35:1 · text ≈11 / 5.6 / 3.4 · cap `#4A423C` · AOD black ·
orange = COMMIT fill only, chrome zero · real font weights only.

**Staircase — measured from `Color.kt` (WCAG ratios computed from the shipped hex values):**

| Step | Token | Ratio vs ground | Locked | Verdict |
|---|---|---|---|---|
| Ground | `NightColorBackground #171412` | — | `#171412` | **TRUE** |
| Recessed | `NightColorSurfaceVariant #100E0C` | 1.05:1 *below* ground | (recessed = below ground) | TRUE in direction, whisper-quiet in the renders — tiles in n04 read almost flat against the ground |
| Raised | `NightColorSurfaceBright #362C27` | **1.35:1** | ≥1.35:1 | **TRUE, exactly at the floor** |
| Cap | `NightColorSurfaceSheet / SecondaryContainer #4A423C` | **1.86:1** (1.38:1 over raised) | cap `#4A423C` | **TRUE** |
| Body text | `NightTextColorPrimary #D2C6BB` | **10.95:1** | ≈11 | **TRUE** |
| Secondary text | `NightTextColorSecondary #9C8E84` | **5.78:1** | ≈5.6 | **TRUE** (within ≈) |
| Muted tier | `NightColorOnSurfaceVariant #776A60` | **3.51:1** | ≈3.4 | **TRUE** (within ≈) |
| AOD | `Color.Black` in the desk-clock overlay | — | black | **TRUE** |
| `onError` | cream on `NightAlert` was 1.95:1 → now `NightColorOnPrimary`, 8.1:1 | — | — | **TRUE (already fixed, verified in Theme.kt)** |

The staircase **holds** — in code and, screen by screen, in the renders: n02 (Shield) and n01
(Deck) sit cleanly on it, recessed wells read below ground, the nav capsule reads as the cap.
No step in any of the five night renders breaks the 1.35 rule.

**Fonts (Lind) — verified from the files, not the registrations:** measured OS/2 weight classes
of the shipped TTFs: Sora 400 / **500 / 600 / 700** (four real files), Atkinson Hyperlegible
400 / 700 (only weights that exist), Anonymous Pro 400 / 700. `Type.kt` registrations now match
the files exactly — the Round-0 fraud stays dead. Two residues: (1) `SubtitleExplanationTextStyle`
and many call sites still *request* `FontWeight.Medium` from Atkinson, which silently resolves
to Regular — no fake weight is drawn, but the request is a lie the system politely covers for;
cap requests at the real weights. (2) The *names* lie in comments only: `PlusJakartaSansFamily`
is Atkinson, `JetBrainsMonoFamily` is Anonymous Pro, "Space Mono" in view comments is Anonymous
Pro — no user sees these, but the next agent who trusts a comment will spec the wrong font.

**Orange census — every orange surface in the night renders (accent = `#F47442` night / `#F55314` day):**

| Where | What | Verdict |
|---|---|---|
| n01 Deck — slide-to-arm thumb | Full accent **fill** on the commit control | **LEGAL** — the one correct spend in the set |
| n03 Tasks — `START 25m` | Accent **text**, cued row | **VIOLATION** (and the word lies, §C) |
| n04 Vault — FOCUSED MIN tile: dot grid + numeral both accent (`useAccentColor = true`) | Accent text/dots, chrome | **VIOLATION ×2 encodings** |
| n04 Vault — tower crown brick | Accent **fill** on the newest brick | **VIOLATION** — a brick is not a commit |
| n04 Vault — tower header `16 BRICKS` mono number in accent (`tokAccent()`) | Accent text, chrome | **VIOLATION** |
| n04 Vault — fader header `DAY 07/07` (`useAccentOnNumber = true`) | Accent text, chrome | **VIOLATION** |
| n04 Vault — selected day (THU): `25m` label + fader fill in accent | Accent text + fill, chrome | **VIOLATION ×2** — selection is not a commit |
| n04 Vault — receipt `+25m` in `primaryContainer` | Accent text, chrome | **VIOLATION** |
| n05 Guard — header `03 SWITCHES` number in accent | Accent text, chrome | **VIOLATION** (small, same disease) |
| n02 Shield | — | **CLEAN** |

**Count: 1 legal spend, 9 violating surfaces — and 7 of the 9 are on the Ledger screen.**
Orange-as-text is concentrated exactly where the dead tab lives; Deck and Shield prove the
budget is achievable everywhere else. (Pixel census agrees in shape: orange-ish pixels ≈0.97%
of n01 — nearly all of it the one legal thumb — vs ≈0.66% of n04 smeared across seven small
chrome surfaces. It is not the amount, it is the addresses.)

---

## LEDGER VERDICT EVIDENCE

*Added on Hale's addendum: Sarthak reopened the Ledger decision — keep rebuilt (a), demote (b),
or kill (c). This section is evidence from the night/typography/rendering lenses only.*

### 1. The count — every number the Ledger shows, totalled

Facts and renderings **on the Vault screen alone** (render n04 is the specimen):

| Distinct fact | Renderings on this one screen | Count |
|---|---|---|
| All-time focused minutes | dot-matrix digits · crisp numeral (same tile) · re-derived as tower bricks · (4th: `BANKED {n}m` in the System Console drawer) | 3–4 |
| Session count | SESSIONS tile (floored, says 4) · receipts header TOTAL (unfloored, says 3) | 2 — *disagreeing* |
| Completed-session count | never shown directly; smuggled inside the bricks formula | 0 (hidden input) |
| Deflections | DEFLECTED tile (dots + numeral = 2 encodings) · (again as a chip in the tower overlay) | 2 |
| "Bricks" (derived, not a fact) | TOWER BRICKS tile (dots + numeral) · tower grid · tower header `FL 03` · tower header `16 BRICKS` | 4 |
| Per-day minutes ×7 | fader bar height · `Nm` label over each column | 14 renderings of 7 facts |
| Per-session record ×≤8 rows | intention · `+Xm` · profile · tasks count · HMAC fragment | 5 fields/row |
| Backup state | `READY` badge (payload non-empty) | 1 — a state, not a record |

**Total: roughly 30 visible number-renderings of about 6 distinct facts** (focused minutes,
deflections, session count, completed count, per-day minutes, per-session rows) — a rendering
ratio of ~5:1 against the workshop law *one fact, one rendering*, before counting the two
places where the renderings contradict each other (4 vs 3 sessions; 16 vs the real 9 bricks).

**Which facts exist ONLY here:** Monolith-sealed minutes (time with the lock actually on),
deflections / phantom reaches during sessions, session attendance (completed vs ended-early vs
grace-cancelled), and tasks completed inside a session. These are real and they are recorded
nowhere else.
**Which duplicate his other record:** per-day and all-time *study* minutes as a category. His
study time already has a record — the YPT timer pipeline — which counts all study, sealed or
not. Monolith's minutes are a strict subset (sealed time only) wearing the same label,
"FOCUSED MIN." At any glance the two systems will give him two different answers to "how much
did I study," and this screen never says its minutes are the sealed subset. That is not
duplication of data; it is duplication of *meaning*, which is worse.

### 2. One fact, one rendering — what survives if anything does

| Fact | Renderings today | The single rendering to keep |
|---|---|---|
| Minutes held in the session that just ended | tiles, tower, faders, receipts, drawer | **The session-end receipt line on the Deck** (see below) — once, at the moment it becomes true |
| Sealed minutes today | fader column for today · inside all-time tile | The receipt line accumulates nothing; if a today figure must exist, it belongs on the Deck's idle face as one plain number — never in a tab |
| Deflections in a session | DEFLECTED tile (all-time) · per-day field (unrendered) · overlay chip | One count **inside the session-end receipt**, for that session only. All-time deflection totals are trivia |
| Session count / completed count | tile vs header (disagreeing) | Neither. A count of sessions is not a truth he acts on; the receipt already says "held" or "ended early" per session |
| Per-day minutes (7 days) | faders (bar + label ×7) | **None in Monolith.** The week's shape belongs to the study record (YPT); Monolith keeping a second, subset week invites the two-answer problem daily |
| Per-session history | receipt rows ×8 with HMAC fragments | Collapses into the single end-of-session receipt, shown once at the end. History beyond that is the study log's job |
| Bricks / floors | 4 renderings | **None. Ever.** A derived unit whose only function is to make a small number look bigger (and which is floored upward to do it) |
| HMAC fragment | receipt rows | **None.** It verifies nothing the row shows (§A) |

**The session-end receipt — the one survivor, specified:** at the moment a session ends
(completed, ended early, or valve break), the Deck shows **one line, in human language, once**,
then it's gone on next arm. Contents, all mechanically true, no floors, no stamps:
`{intention} — held {H}h {M}m of {target}` · or `Ended early at {M}m` · plus, only if non-zero,
`{n} task(s) finished` and `{n} reach(es) turned away`. Multi-block elapsed must be summed
across blocks for this line to be true (fix in §A). That is the whole record Monolith needs to
*show*. The database can keep whatever it keeps; showing is the decision.

### 3. The checking habit — what this screen asks of him

**At his desk, rested:** the screen asks for *decoding labour*. Its hero numbers are written
twice, once in a dot-matrix that must be read digit by digit; its week must be tapped day by
day (selection state exists for its own sake — tapping THU recolours it orange and changes
nothing else); its history must be cross-checked against stamps he cannot verify. Rested, he
can do all of that. None of it produces a decision: no number on this screen changes what he
arms next, eats, or studies. It is a museum of himself, and a museum with falsified labels
(floors, stubs, an empty HMAC) at that.

**At 22:00, tired:** the same screen asks for the same labour from the version of him least
able to do it — and pays him in the two currencies that hook checking: a monument that only
grows (tower, floors that guarantee it never looks empty) and a near-miss to inspect (which
day was short, which receipt is missing). A surface that rewards opening, decoding and
comparing, and whose every visit produces *nothing actionable*, is a checking habit with a
progress bar. Its cruellest detail at 22:00 is the fader row: four days of `0m` — each one
still drawn with a little cream stub, as if the screen cannot even let a zero be zero.

**Glanceable truth test:** cover the screen for one second — what can he take away? From the
receipt line in §2: everything, in one read. From this screen: a dot pattern, a tower, and two
session counts that disagree. The current form — hero tiles, tower, faders, receipts — is five
surfaces deep where the standard allows one statement, once.

### 4. Preliminary lean (Nox + Lind, evidence not verdict)

**Lean: (b) demote — with (c) as the acceptable floor, and (a) rejected.** The tab, the tiles,
the tower, the faders and the receipts list should die without replacement: they fail the
truth standard more often than any other surface in the app (floored totals, a "VERIFIED"
stamp that verifies nothing it labels, a backup that backs up nothing it names), they hold
seven of the app's nine accent violations, and they duplicate the meaning of a study record
he already trusts. But the *facts* underneath are not all worthless — sealed minutes,
deflections and attendance exist only here, and at exactly one moment they are worth saying:
when a session ends, to the man who just held it, once, in one human sentence on the Deck.
That is demotion, not resurrection: no tab, no history, no monument, nothing to check at
22:00 — a record that surfaces at the moment it is earned and then gets out of the way. If
the cohort judges even that line to be one rendering too many, kill it too and we will not
object: a silent database plus his real study record loses him nothing he can act on. What
we will defend against is (a): rebuilding this screen, however honestly, rebuilds the
checking habit with better labels.

---

**Worst finding, one sentence:** On a fresh install — before he has studied a single minute —
Monolith's record opens by lying to him in four places at once (≥35 focused minutes, ≥4 sessions,
≥16 tower bricks, and a "VERIFIED" stamp whose signature covers none of the fields it labels and
verifies as true even when it's blank), which means the app's truth-telling surface is the one
place in it that cannot be trusted.
