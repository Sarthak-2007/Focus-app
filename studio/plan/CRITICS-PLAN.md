# CRITICS-PLAN — the audit of everything, measured

**Cohort:** Critics (Hale, coordinator). Seats filed: Prima, Moss, Day8, Zug, Quaere,
Vera+Una, Nox+Lind. Full row-level tables live in `plan/critics/` (seat files are
part of this deliverable); coordinator exhibits in `plan/critics/hale-own-exhibits.md`.

**Measuring instruments (his, settled):** ARM ≤10s cold-open-to-sealed · HOLD
absolute, zero mid-session decisions · TELL THE TRUTH once, in human language.
One fact, one rendering. Beauty is retention. The rested user decides, the
tired user executes. Concept: none.

**Method note (his order, 05:1x):** citation requirements lifted — seats audited
by any method that finds the truth. Every number below was measured against the
current code or the shipped renders.

---

## 1. ARM AUDIT — every path from cold open to sealed

Budget: **10 seconds, cold.** Happy path passes. The budget dies in exactly
three places: a permission modal that spends his one committing gesture before
checking the permission, a resting state that is never his, and traps that
silently re-price the session.

| Path | Gestures | Seconds | Verdict |
|---|---|---|---|
| Cold open → slide, duration unchanged, permission alive (Prima+Moss) | 2 (launcher tap, slide: 248dp travel, gate at 0.82 ≈ 203dp) | ~2–3.5s | **PASS** — start destination is DECK, slide painted on frame one, zero scrolls/taps before it |
| Duration change 25→50 (Moss) | 2 (wheel detent drag ~69dp rim arc + slide) | ~1.5–2.2s incl. 280ms settle | PASS |
| Worst wheel case: FLOW left dial at 5 → 180 (Moss) | 3–4 (26 detents, 223°, ~358dp arc, re-grip, hub tap, slide) | ~3.5–4.5s | PASS on the clock — but see FLOW trap below |
| **Ordinary re-arm, mid-day (Day8)** | slide + re-spec: dial 25→50 (~3s) + blocks 2→1 via 3 wrap-around hub taps (~3s) + coupled total-verify (~2s) | **~8s of the 10s budget** | **FAIL in practice** — dial/blocks/intention/profile are unpersisted ViewModel state; the resting state (dial 50 vs preset POMODORO_25_5=25) is self-contradictory, and the `activeTask` collector re-imposes the queue head's 25-min estimate on the dial at every task advance. Tax ≈ 4.0 hours over ~1,800 arms |
| **Accessibility dead at slide (Prima)** | 9–12 (slide spent → modal → Settings trip → grant → return → second tap/slide) | **~12–35s+** | **FAIL — 2–3× budget.** The check runs *after* the committing gesture (`requestArmWithPreFlightCheck` is wired to the slide's commit). Granting costs 7–10 gestures inside Android Settings, as a decision at 05:00, fasted |
| Routine fire (Day8) | 0 | 0 | Passes ARM trivially — and fails TRUTH (§3): zero gestures, zero presence |

### ARM frictions the plan must carry fixes for

| # | Friction | Fix the plan must carry |
|---|---|---|
| A-1 | Pre-flight check runs after the slide; the seal sound fires twice (view + `armMonolithSession`) and before the session row exists — celebration precedes the fact | Permission health is a standing Deck fact (a dead guard shows on the idle Deck, before any slide is offered); the slide is never spent on a modal. One seal, sounded only when the session exists |
| A-2 | Painted slide lies during cold open: dial 50 against a 25-min preset, "SHUT 0 APPS", totals initialised 75/6 — because seeding + a whole-phone package scan (`ensureSeededAndSyncPackages`, run up to 3×, mutex-serialised) gates the truth, and the scan landing can re-price the dial mid-gesture | Truth before paint or honest placeholders; the scan never runs on the arm path more than once; the dial is never rewritten by a collector after first paint |
| A-3 | FLOW key is a one-way trap: one tap sets dial:=5 AND blocks:=1 permanently (`selectStudyPreset`'s only caller in the app — no surface ever selects back), while the repository arms timed sessions under a stopwatch banner | FLOW dies as a stray-tap hazard: preset selection becomes an explicit arm-time choice with a way back, or the key goes. Screen stopwatch definition and repository definition must be one definition (§3) |
| A-4 | Slide tap-to-commit hole: no thumb hit-test on ACTION_DOWN — tap at ≥82% of track + release commits with zero drag; release at 0.81 loses everything to a 280ms spring | The centre gesture must require the drag: DOWN only counts on the thumb; near-gate release behaviour decided by the rested design, not by float comparison |
| A-5 | Doubled sounds on the two gestures that must feel single: every wheel detent sounds twice (view + VM `setDialMinutes`), the seal sounds twice | One sound per event, owned by one layer (sound law: the VM speaks for value changes, the view for travel) |
| A-6 | Blocks hub: one-way wrap 1→2→3→4→1 (resting 2→1 costs 3 taps), no press feedback, touch radius (~49dp) smaller than the drawn hub (~56dp) — edge taps silently become wheel drags and change duration | Bidirectional or direct-set hub; touch ≥ drawn size everywhere on the Deck |
| A-7 | Wheel dot freezes between detents (drawn angle moves only on snap) — stiction feel on the primary control | Drawn angle follows the drag continuously; the value snaps, the picture doesn't jump |

## 2. HOLD AUDIT — every mid-session decision surface

Budget: **zero.** The cohort counted **14+ decision surfaces (Quaere) and 23
exit/escape surfaces (Zug)** in a sealed session. HOLD today is a claim, not a
property: the one place it is genuinely implemented — the per-app tier ratchet
(VM L755–860: tightening free, loosening refused with plain words) — is the
model the plan extends. Everything else routes around it.

### 2.1 The three holes that unmake the seal outright — kill first

| Surface | Cost today | Verdict |
|---|---|---|
| **Smart Loadout applied mid-session** (Shield → VM L810–820 → Repository L681–725) | **1 tap** rewrites EVERY app's tier from a template (WORK_SPRINT un-LOCKs social apps to PEEK, everything else OPEN). Ratchet never consulted. The record keeps claiming the seal held | **KILL during session.** Loadouts/profiles join the ratchet family at the repository boundary: loosening refused while sealed, tightening free |
| **Profile applied mid-session** (VM L842–849) | 1 tap sets every out-of-profile app OPEN — same hole, second door | **KILL during session** (same shared gate) |
| **Pull-down drawer toggles** (Quaere): Shorts-sniper + browser-link-blocker switches, reachable in 1 tap from the always-visible header | 1 tap each disables real blocking; `toggleShortsReelsSniper`/`toggleBrowserUrlBlocker` have **no session check** (unlike tiers/schedules/limits) | **KILL during session** — drawer goes read-only while sealed. (Zug adds: the Shorts toggle is also a fake control — in-memory, nothing reads it, the service snipes unconditionally. Off still blocks. A lying switch dies either way) |

### 2.2 The exit machinery, measured against the banked law he settled

| Surface | Finding | Verdict |
|---|---|---|
| Emergency valve | Banking **does not exist**: progress is this-press-only; release snaps `holdProgress = 0f` (the file header falsely calls it "decays"). Flat 4/8ms ticks — 15/30/45 checkpoints unfeelable. Target **68dp vs his ≥88dp**. No ACTION_MOVE handling (slide off, timer runs; second finger restarts it). Zero accessibility semantics — invisible to TalkBack/Switch Access | **GATE — rebuild as his law:** bank at 15/30/45 into persisted state; slip drains toward the last bank, never below; distinct checkpoint ticks; ≥88dp; MOVE bounds + secondary-pointer ignore; checkpoint announcements. (Motor spec already exists: HUMANS/Barriere, humans-plan/barriere-valve.md §1.3 — 800ms no-drain grace → drain at 1.0× for the first 3.0s → 2.0× thereafter, easing near the floor, floor = last bank, banked never drains; 112dp bar. Adopt verbatim, don't re-derive. CORRECTION 06:3x: an earlier citation in this file reversed the drain order as "2.0×→1.0×" — charged against Critics by Ruth (HPA-31) and Nox+Lind (C-20) and corrected here; Barriere's file is the source, not any cohort's citation of it) |
| Overlay copy | *"Hold the physical valve continuously for 60 seconds"* — three untruths in one sentence: not physical, "continuously" contradicts his banked law, and the weekly token it cites doesn't exist | Rewrite with the rebuild: banked, on-screen, token stated only when the token is real |
| **Weekly token** | **Does not exist.** `MutableStateFlow(1)`, in-memory: no persistence, no week anchor, no reset logic anywhere. Restart/force-stop/reboot/reinstall refills it. `confirmDisarmSession` doesn't even enforce it at 0 — the only "enforcement" is the valve view refusing touches | **KILL this shape.** Persist token + week-anchor in Room with the session record; spend recorded in the session's truthful line; check enforced in the repository, never in a UI prop |
| Grace-window abort (≤10s) | Legitimate ARM-time undo — but the repository **erases the record** (elapsed zeroed, no log line), and the ≤10s gate is a magic string + UI-only check the VM never verifies | **JUSTIFY the window, KILL the erasure.** It leaves one truthful line ("Cancelled in the grace window, Ns in, no token spent"); VM verifies elapsed itself; reason becomes an enum |
| Stopwatch tap-finish (after 20-min floor) | One tap completes — on a chip still labelled "EMERGENCY BREAK VALVE (60S HOLD) · 1/1 WEEK". Record says `completed = true, "Stopwatch Completed"` for a choice | **JUSTIFY the mode** (rested choice at arm time), **KILL the lying surface**: chip renames "FINISH STOPWATCH" past the floor; record states "Ended by choice after the 20-minute floor" |
| Force-stop / recents kill | No `onTaskRemoved`; enforcement dies silently, session row stays active, relaunch **resumes as if held throughout** — unheld time written as held | Cannot be prevented; must be **confessed**: stamp "enforcement interrupted" with last-tick time; record shows held vs gap separately |
| Reboot | Session survives, shield re-forms, clock-change correctly distrusted (+15m tamper penalty) — but the powered-off interval is invisible in the record | **JUSTIFY** + record the restart as a line in the session's story (boot-count change is already detected) |
| Guard's own Settings launchers | Monolith offers buttons into Accessibility Settings mid-session; that page contains none of the tamper words the blocker listens for, so the service can be killed unopposed, after which force-stop/uninstall are unguarded (device-admin is a nag mislabelled "Kernel-Level Uninstall Guard") | Launchers inert while sealed (Guard's fate is his pick — this is only about in-session reachability). Kill the "kernel-level" overclaim in copy |
| Safe Break (10m per block boundary) | Automatic, arm-time structure — not a decision. Two faults: break time driven by tick counts from two callers (VM 1s + Shield 2s) so "10 minutes" can end early; the Deck never declares it (readout shows 00:00 while PEEK apps unlock) and the finished record carries no break line | **JUSTIFY** + drive from verified elapsed; declare the break state on the Deck; the session's line names its breaks |
| Airlock | Rendered overlay is a lawful RETURN/PEEK binary after a 5s countdown — **the reason picker and particle-tapper in the briefs do NOT exist in the render** (dead ViewModel callbacks, never called). But every intercept is logged with a fabricated cause **"Habit"**. The peek's countdown pill also doesn't exist — the 120s lease is blind, ends by re-interception; unknown packages are offered a peek by default ("EMERGENCY PEEK" though it costs no token) | **KILL the dead machinery and the false cause-logging; do NOT build the picker.** Peek: rename "PEEK · 2M", unknown packages default no-peek, render the lease countdown or drop the lease's pretence of visibility |
| Bottom nav + header GUARD pill while sealed | Nav fully live mid-session: Shield loadouts, the full Task Sequencer (auto-split/promote/delete), Vault. GUARD pill opens full Guard incl. blocked-site editing + Settings exits | While sealed, navigation is read-only or inert: his exits are the valve, grace, and completion — not the tab bar |
| HELD digest mid-session | Three doors from the Deck into intercepted **message bodies** + a destructive CLEAR ALL, inside the session | Digest is post-session reading; mid-session it shows count only, no bodies, no destructive action |
| Desk-clock tap | Whole screen dismisses the AOD into the full live Deck UI | Deliberate gesture to exit (long-press or the valve's little sibling), not any tap |
| Theme toggle mid-session | Theme is forced during session; the tap silently flips post-session state | Toggle inert while sealed (a control that pretends is a lie) |
| Task DONE tap / sub-steps / QUEUE | DONE marks + advances mid-session; QUEUE navigates away; sub-step toggles edit against his list | DONE/sub-steps: **execution, justified** (toggle only, never edit text). The *advance* is banked to block-end so the queue can't be browsed forward at his weakest. QUEUE navigation: kill while sealed |
| Touch targets on exits | KEEP FOCUSING — the safe button — is **42dp** (BlockitActionChip hard-coded height), as are Airlock RETURN/PEEK and Guard REMOVE. Deck/valve chips 58dp ✓; valve 68dp ✗88 | One component sweep: chip floor ≥48dp everywhere; valve ≥88dp; no call site may pass below the floor |
| Call bypass | While any call state is active, ALL blocking pauses for EVERY package | **JUSTIFY**, narrowed: telephony + the call's own package only |


## 3. TRUTH AUDIT — the lie list (exhaustive; replacements are the plan's copy)

Sources: Vera+Una (30 claims, Deck/chrome/Shield), Nox+Lind (Vault/overlays/Tasks/
night), Hale exhibits (data layer). Full tables in the seat files; this is the
merged list, worst first.

### 3.1 The record is falsified at the source — before any screen renders

| # | Lie | Evidence | Truthful replacement |
|---|---|---|---|
| T-1 | **Three fake completed sessions seeded into the real history table** on first run (50m + 75m + 25m = 150 min, plus a fake TikTok intercept), unflagged, indistinguishable forever. The Vault's famous "150" was 100% fiction | Repository seed block | Seeds die. Demo data never shares a table with the record; existing installs get the three seed rows identified (they carry known intentions/artifact IDs) and removed |
| T-2 | `totalFocusedMinutes` **floored at 35**, initialised **75** | VM L280–287 | The number is the number: sum of real elapsed, no floor, no pre-data initial |
| T-3 | Deflections = `max(logs.size, Σ per-app interceptCount)`, initialised **6** — two rival ledgers, print the larger | VM L289–293 | One ledger (the intercept log), one count |
| T-4 | **His own early quits are written into the deflections ledger** ("Partial Stamp", "Unlatched Early" intercept rows) — DEFLECTED counts his exits as blocked apps | Repository L1101–1112 | Exits are session outcomes, recorded on the session — never as intercepts |
| T-5 | Disarm banks `max(verifiedElapsed, tickerElapsed)` — when verification and ticker disagree, the kinder number wins | Repository L1089 | The verified number wins; disagreement is itself recorded (tamper flag already exists) |
| T-6 | 7-day blocks inflate: 15 min renders as a full 25-min block | Repository L1138 | Blocks are floor(minutes/25) or the rendering drops blocks entirely |
| T-7 | **Routine ghosts:** receiver arms with zero gestures/presence; END stamps `completed = true` as the caller's word, never derived. Two undeletable seed routines mint **≈750 manufactured minutes/day (≈87 h in an untouched week)** | RoutineAlarmReceiver; Day8 truth table (9 scenarios) | A routine may only record what presence evidence supports: no verified presence, no completion — the window records as *fired, unattended*, or does not record. END never asserts completion |
| T-8 | Manual arm during a routine session orphans the routine's row (`deactivateAllSessions`) — and history still counts it: double-counting possible | Day8 | One active-session invariant enforced in the repository, not by deactivation sweeps |
| T-9 | Every intercept logged with fabricated cause **"Habit"** | Quaere (dead reason machinery) | Cause is recorded only when a cause exists in the data; otherwise the log says what happened: app, time, outcome |
| T-10 | Intercepts during force-stop gaps / powered-off intervals are written as held time on resume | Zug | Held and unheld intervals are separate lines in the record (see §2.2) |

### 3.2 The Deck and chrome — one fact, five renderings, several false

| # | Claim as rendered | Truth | Replacement |
|---|---|---|---|
| T-11 | Duration rendered **5 ways that disagree**: readout `50:00` (per-block), caption `100 MIN TOTAL` (still omits the 10-min safe breaks — true span 110), slide badge `50M` (per-block again), `FINISHES` (current block only — an hour early at default), running % = percent *remaining*, unlabelled, counting down | Vera+Una | ONE hero: the **total** (blocks + breaks, the honest wall-clock span). Block length lives on the wheel's printed scale and nowhere else. FINISHES = the real end. % labelled "LEFT" or dropped |
| T-12 | Stopwatch defined twice: Deck = FLOW preset OR dial<10; Repository = dial<10 only. Pick FLOW, wheel to 50: screen says `NO LIMIT / OPEN FLOW`, engine arms a hard 50-min timed session | Vera+Una | One definition in one place; the screen can only say what the engine will do |
| T-13 | Safe break: Deck shows `00:00` for 10 minutes, never says "break" (the Shield notification already computes the honest string) | Vera+Una | Deck declares `BREAK · 9:41` — the string exists; use it on the big surface too |
| T-14 | `SHUT n APPS` counts LOCKED only, present tense, while nothing is shut outside a session; armed PEEK apps are also intercepted | Vera+Una | Idle: "n APPS WILL SHUT". Sealed: count everything the seal actually stops |
| T-15 | Drawer prints `BANKED 150m • DEFLECTED ` — **"DEFLECTED" with no number** (the value is passed in and never used); BANKED includes the live session, the 35-min floor, and the 150 seeded minutes | Vera+Una | Both numbers real or the line dies |
| T-16 | `HELD 02` on fresh install — seeded fake notifications | Vera+Una | HELD counts real captures only; seeds die (T-1) |
| T-17 | Nav still reads **LEDGER**; tabs misnamed SYSTEM/HORIZON for Shield/Tasks | Vera+Una | Names say what the screens are (Ledger's fate per the verdict file) |
| T-18 | DAY/NIGHT pill says NIGHT during any session regardless of his choice | Vera+Una | The pill reports his setting, not the session's forcing |
| T-19 | Loadout band's middle stop says **DEEP** but applies WORK_SPRINT; cap position is unpersisted local state (STALE on return) | Vera+Una | Label = what it applies; selection persists |
| T-20 | Routines header counts *enabled* as "ACTIVE"; rows never print duration or LIVE/NEXT though `RoutineSchedule` computes both | Vera+Una | Header counts what it says; rows print the computed facts or the computation dies |

### 3.3 The Vault / Ledger screen — audited in full in the verdict file

Summary (detail + verdict: `plan/LEDGER-critics.md`): on a fresh install the
record lies in four places at once — focused minutes floored (T-2), SESSIONS
floored at 4, TOWER BRICKS floored at 16 (14 in the overlay) from a fabricated
formula `(minutes/25 + completions)`, and a **"VERIFIED" HMAC stamp that covers
none of the fields it labels** (not elapsed, intention, tasks, or completion —
it verifies as true even when blank; one render shows literally `HMAC•` and
nothing). SESSIONS tile says 4 while the receipts header on the same screen
says TOTAL 3. Receipt minutes round every session up to ≥1m and print "+"
for failures. The "EXPORT AES-256-GCM LEDGER BACKUP" button exports nothing
anywhere; its payload omits the sessions it claims to back up; the import
callback re-exports. (AES itself is real; fallback keys are hardcoded strings
in the APK.) A **hardcoded fake week (45–110m)** renders if stats load late;
0-minute fader days draw a 12%-height stub — absence rendered as presence.
The desk clock draws a **hardcoded 65% progress bar** with no session.
~30 visible renderings of ~6 distinct facts.

### 3.4 Cleared as TRUE (say so plainly)

Night staircase measured from shipped hex — ground #171412, raised 1.35:1,
cap #4A423C 1.86:1, text 10.95/5.78/3.51, AOD true black: **clean, locked.**
Fonts real (Sora 400–700; Atkinson/Anonymous 400/700) from the TTFs. Grace
countdown, routine row times/days/loadout strings, `SHOWING n` count, filter
words, app sled stops, drawer toggle chips' labels, `MONOLITH • ARMED`, Tasks
header (3 tasks / 95m checks out), natural completion, clock-change distrust.
Orange census: **1 legal spend (Deck slide thumb) vs 9 violating chrome
surfaces — 7 of them on the Ledger screen.** Shield clean; Guard has 1.

## 4. THE KILL LIST — the master plan must honour every line

1. **Seed data dies** — fake sessions, fake intercepts, fake notifications, the hardcoded fake week, the desk clock's 65% bar. Demo data never shares storage with the record.
2. **Every floor dies** — `coerceAtLeast` on focused minutes (35), sessions (4), bricks (16/14), receipt minutes (1). A number is the number or it is not printed.
3. **The weekly token is rebuilt as a persisted fact** (Room, week-anchored, spend recorded, enforced in the repository) — or every string mentioning it dies. No third option.
4. **The valve is rebuilt as his banked law** (bank 15/30/45, drain-to-floor, ≥88dp, checkpoint ticks, accessibility semantics) and its copy rewritten. "Continuously" and "physical" die.
5. **Mid-session loosening dies as a class** — loadouts, profiles, drawer toggles, browser shield, site removal all join the existing tier ratchet at the repository boundary. While sealed: tightening free, loosening refused in plain words.
6. **The Ledger tab dies as a destination** — verdict and surviving forms in `plan/LEDGER-critics.md` (cohort: demote, unanimous). The backup export is relocated, not lost.
7. **The HMAC stamp dies as display.** Crypto stays inside, guarding elapsed time, where it is honest. "VERIFIED" is never printed about fields the signature doesn't cover.
8. **Routine ghosts die** — no completion without presence evidence; routine CRUD is built whole (create/rename/delete/pause — today 1 of 4, DAO has no routine delete); seeds reseed to his real day (05:00 fasted block, sleep 20:00–03:30) and become deletable like any of his tools.
9. **Resting state becomes his** — dial/blocks/intention persist across restarts and task advances; the Deck opens as he left it. The re-spec tax (~8s/arm) is the ARM budget's biggest everyday thief.
10. **FLOW as a one-way trap dies** (A-3); stopwatch gets one definition (T-12).
11. **The airlock's dead machinery dies unbuilt** — reason picker, particle tapper, split-and-return (T-9's fabricated causes with them). Do not resurrect as a feature.
12. **Silent resumes die** — force-stop gaps, reboots, and guard-off intervals are confessed in the record, never absorbed as held time.
13. **Grace erasure dies** — every exit leaves its one truthful line, including the free one.
14. **42dp targets die** — component floor ≥48dp; the valve ≥88dp.
15. **Duration gets one hero** (T-11) and the Deck's safe-break silence ends (T-13).

## 5. ATTACK ON THE EMERGING PLAN (SYNTHESIS + wall, vs settled law)

Filed in full in `hale-own-exhibits.md` §C; the charges the master plan must answer:

- **C1** R2.17's "FIRES ITSELF" ribbon advertises the machine-agency he killed. NEXT may print facts (time · loadout); firing semantics are a recording question (T-7), not copy.
- **C2** D-D Consent Beat is not the routines answer — his settlement (routines his + no ghosts) supersedes it. Re-derive, don't inherit.
- **C3** R3.24's "3 misses → auto-pause" is the machine editing his routine — a decision taken at his tiredest. Lawful form: a printed fact (`MISSED ×3`) he acts on. (Note: attendance data must exist for even the fact — see verdict file dependencies.)
- **C4** "Attendance logged" must define presence evidence or be struck.
- **C5** Chrome's `TODAY 2H30 / 12H` is a lie with a denominator — YPT owns his day; Monolith may print app-scoped `SEALED 2H30`, labelled, or nothing. `186D TO NEET` prints an unannounced exam date as fact or dies.
- **C6** First Light ceremony, Program Tape, accession-stamp ceremony: narrative machinery, dead with the concept. The session-end receipt survives only as truthful statement.
- **C7** The accent budget's "Ledger 1" line was suspended — now resolved by verdict (demote): the budget closes with zero Ledger spend.
- **C8** R2.16's duration trio resolves per T-11: total is hero, badge dies, readout demotes to block scale.
- **C9** Tasks sound family survives with wave-1 sound law (fixed pitch sets, silence map).
- **C10 — global dependency:** Guard is HIS OPEN PICK. Every plan element presuming dissolve or destination must be written in both branches or flagged. Critics audited Guard's in-session reachability only (§2.2), not its fate.
- **Convergence note:** on the Ledger, four cohorts (Critics, Designers, MaxImpl, Wild) plus Improvers' exam all landed on demote independently, on different lenses. The master plan should treat the tab's death as settled-by-evidence and spend its argument on the surviving forms.

## 6. Dependencies flagged

- **Banked-hold persistence needs the record store** — even a "kill everything" Ledger outcome keeps persisted hold state + session rows (Wild/MaxImpl concur). Storage is not the Ledger; the tab was.
- **Attendance data feeds C3's lawful form** — if routines print `MISSED ×3`, the underlying presence record must exist and be honest (T-7).
- **Backup export relocates** with the Ledger verdict (to Shield or Guard-drawer, per Guard's fate — C10).
- **Valve motor numbers** are settled in HUMANS (Barriere spec) — the build wave adopts them; Critics' requirement is only that banking exist, drain, announce, and measure ≥88dp.
- **Round 1b/1d and the TokenAlert death** remain build-wave items; nothing in this audit blocks them, and T-11/T-19 presume the type/spacing passes land.

— Hale, coordinator, Critics. Seats: Prima · Moss · Day8 · Zug · Quaere · Vera · Una · Nox · Lind.
