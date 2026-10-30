# LEDGER VERDICT — Designers cohort, IA seat (Plan, lead)
Filed: 29 Oct 2026, 05:17 IST · Coordinator: Sana · For: Ren → Sarthak decides

## THE OPTION — (b) DEMOTE

**Kill the Ledger tab. The record survives in exactly two demoted forms, neither a destination:**

1. **THE RECEIPT (primary)** — the completion sheet, shown once at the moment a session ends, carrying that session's own facts and nothing else. Ephemeral. Dismissed in one tap. It is not a place he goes; it is the truth told at the only moment it is news.
2. **RECORD (secondary)** — one hairline section at the **foot of Shield**, below the apps list and the verb-code legend, titled `RECORD`. It carries the Monolith-only aggregates (spec in §3). It is reached by scrolling a screen he already opens daily to tend routines and apps — never by a nav stop of its own.

No fourth destination. Nav is **three stops: DECK · SHIELD · TASKS** (plus the PARK end-detent for the Guard drawer, which is a position, not a destination).

---

## 1. THE IA REASONING

### 1.1 What the record actually is — and is not
YPT owns the study record: hours, syllabus progress. That record answers his real question ("am I on pace for AIR 1?") and it lives outside this app. Monolith alone holds four facts YPT can never know:

- **Sessions** — what was armed, sat, completed, broken.
- **Deflections** — blocked attempts absorbed during a session.
- **Holds** — emergency-break usage (1/week budget).
- **Routine attendance** — fired vs sat vs ghost.

These are real, and they are exclusively Monolith's. That is the entire case for keeping anything. Note what they are: facts about the *instrument*, not about the *study*. A record of the tool's behaviour, not of the man's progress.

### 1.2 The cost of a fourth destination
- **Nav weight.** The band switch goes from 3 stops to 4. Every stop is not just a tap target — it is travel distance, a detent, printed scale length, and a permanent tax on every single navigation he ever makes, paid daily, to serve a room he opens rarely. Three stops is a switch; four is a menu.
- **Attention economics.** Ask the only question that earns a destination: *he opens it how often, learns what, and changes which decision?* A past-facing record room fails all three. He opens it at most weekly, out of curiosity. What he learns (deflections, streaks, totals) changes no decision he makes that day — his decisions are made on Deck (arm), Shield (tend), Tasks (queue). No action originates in a Ledger. A destination where no action originates is a museum, and a museum inside a daily instrument is dead weight with rent.
- **Centre of gravity.** Monolith is a forward instrument: it points at the next block, the next firing, the day not yet spent. A full past-facing room pulls that balance backward — a quarter of the app's navigation mass devoted to looking behind. The demoted forms keep the past in its proportion: a receipt at the moment of completion (present tense), and a footnote section on the tending screen (past as context for a forward decision — is this routine earning its firing?).

### 1.3 Why demote beats both neighbours
- **Against keep-as-tab:** the tab's only defence is "the facts exist only here." Existence is not a destination argument. The post office holds facts about him too; he does not give it a room in his house. Every fact survives the demotion (§1.4) — what dies is only the *room*, and the room is the cost.
- **Against kill outright:** two of the four facts are load-bearing and would die homeless. **Hold usage** is a budget fact — if the app does not say the week's break is spent, the HOLD-absolute standard has no ledger and the tired user must remember a rule at his weakest moment. **Routine attendance** is the only evidence that can kill a ghost routine: 3 misses → auto-pause is a decision that needs its data somewhere he can audit. Kill the record entirely and both facts either vanish (untruthful — TRUTH once means the app must be able to say them) or leak into chrome and Deck as badges (worse — chrome zero is law).

### 1.4 Where every Monolith-only fact goes
| Fact | Lives | Form |
|---|---|---|
| This session: armed / sat / completed / broken | **The Receipt** | One line on the completion sheet, at completion only, never re-rendered as history |
| Deflections in this session | **The Receipt** | Counted on the receipt line for that session only. No running deflection total is kept anywhere — a lifetime deflection counter is a vanity score and is **killed outright** |
| Hold spent this week (yes/no, when) | **RECORD, Shield foot** | One hairline row: `HOLD — SPENT TUE 14:20` or absence rendered as absence (see §3) |
| Routine attendance (fired / sat / ghost, per routine, trailing 7 days) | **RECORD, Shield foot** | One hairline row per routine, directly serving the auto-pause audit |
| Sessions held this week (count) | **RECORD, Shield foot** | The hero number (§3) |
| Study hours, syllabus, pace | **YPT — never Monolith** | Not duplicated, not approximated, not "today so far." One fact, one rendering, and its home is not here |

What dies, named: the Ledger tab, its nav stop, its hero-stat dashboard, dot-matrix anything, streak towers, the lifetime deflection total, every chart of hours (YPT's fact, stolen rendering).

---

## 2. DEPENDENCIES — for Razor & Mikro (chrome variants, building in parallel)

Flagged explicitly because your three variants wait on this shape:

1. **NAV STOPS = 3.** DECK · SHIELD · TASKS. Design the band switch scale, detents, and cap travel for three stops plus the PARK end-detent (Guard drawer). **Do not reserve width, a detent, or a printed numeral for a fourth stop.** If Sarthak overrules to keep-the-tab, the scale re-opens — until then, 3 is the number.
2. **CHROME = ZERO record data.** No held-count badge, no deflection count, no hold-spent dot anywhere in chrome. The hold's spent state appears only in RECORD (Shield foot) and, at the moment it matters, on the hold control itself. Chrome carries no Ledger residue of any kind.
3. **SHIELD FOOT SPACE — reserved.** RECORD occupies the foot of Shield: below the apps list and the verb-code legend, after a 48dp section break. Budget ≈ 180–220dp of scroll content (hero row + ≤5 hairline rows). It is scroll content, never first-viewport furniture — Shield's furniture budget (≤35% of first viewport for the apps-list zone) is unaffected.
4. **DECK SPACE — unchanged.** Nothing from the Ledger lands on Deck. The receipt is a sheet *over* Deck at completion; it owns no permanent Deck real estate, no corner, no chip.
5. **ACCENT — RECORD uses zero.** The one-accent maximum permitted to a record surface is spent as **zero**: accent is COMMIT fill only (Deck slide, Tasks START), and RECORD commits nothing. Its hero number is ink, not orange.

---

## 3. THE SURVIVING SURFACE — RECORD, composition under the locked laws

*(The Receipt is specced briefly at §3.2; RECORD is the standing surface and gets the full spec.)*

### 3.1 RECORD — foot of Shield
- **Position:** Shield scroll foot. Order from top of Shield: routines time column (hero) → Section strip → apps list → verb-code legend → **48dp break** → RECORD → end of scroll. Nothing below it.
- **Section head:** `RECORD` — label tier, 10sp Anonymous Pro Bold, +2.0 tracking, tertiary text tone. No rule above it except the 48dp of air; a hairline (1dp) separates it from the legend zone.
- **THE ONE HERO NUMBER — sessions HELD this week.** A single count, e.g. `9`, at 44sp Sora (bottom of the hero ramp — this is a footnote surface, it does not earn 64–112). Beside/under it, one label line: `HELD THIS WEEK` (10sp label tier). **Defence of the choice:** it is the only Monolith-only aggregate that answers a forward question — *is the instrument working?* Deflections flatter (they grow when temptation grows; a big number can mean a worse week). Hours are YPT's. Holds are binary and live in their own row. Held-sessions is the instrument's truth in one digit, and it cannot be gamed by the app minting ghost minutes — a ghost session is by definition not held.
- **Hairline receipt rows** (1dp hairlines, 12dp vertical padding, no cards, no fills): one row per routine attendance + one hold row. Format, body tier 13sp Atkinson, secondary tone, values in primary tone:
  - `FASTED BLOCK — SAT 4 · GHOST 1 · MISSED 2` (trailing 7 days)
  - `DAY BLOCKS — SAT 5 · GHOST 0 · MISSED 2`
  - `HOLD — SPENT TUE 14:20`
- **Absence rendered as absence, never as 0.** A routine with no firings this week prints `— NO FIRING THIS WEEK` with an em-dash gap where the counts would sit — never `SAT 0`. A hold not spent prints `HOLD — UNSPENT` (a word, not a zero, not an empty row that could be misread as missing data). A week with no sessions prints the hero position as `—` with the label `NOTHING HELD YET` — the space is held, the number is absent. Zero is a measurement; absence is a different fact, and it is drawn differently.
- **Tones — night staircase (locked):** ground `#171412`; hero number in primary text `#D2C6BB`; row values `#D2C6BB`, row labels/keys `#9C8E84`; section head and hairlines in the tertiary register `#776A60` / cap-adjacent `#4A423C` hairline at reduced presence. Day mode maps to the same three-step ladder in day tones. No surface in RECORD rises above ground by more than one staircase step — it is the quietest section in the app by design.
- **Accent:** zero (see §2.5). **Motion:** none. RECORD never animates, counts up, or refreshes theatrically; numbers re-strike in place or not at all. **Interaction:** none — rows are not tappable, there is no "see more," no drill-down, no export. The moment RECORD grows a tap target it is growing back into a destination.

### 3.2 The Receipt — completion sheet (the session's truth, once)
- Appears once, on session completion, as a sheet over Deck. One line of fact: `HELD 50 MIN · STUDY · 3 DEFLECTED` — or for a broken session, the truth in the same grammar: `BROKEN AT 31 MIN · HOLD SPENT` — never softened, never scored.
- Type: 15sp body Atkinson for the line; no hero number, no accession number, no seal, no verification language. Dismissal: one tap anywhere, or it yields to the next arm. It keeps no copy of itself — its facts pass into RECORD's aggregates and the sheet itself is gone. TRUTH once means once.

---

## 4. THE STRONGEST CASE AGAINST THIS VERDICT — argued to win

*The demote is a coward's compromise, and here is the case that should beat it.*

A section at the foot of Shield, below the apps list, below a legend, below a 48dp break, with no interaction and no accent, is not a demotion — it is a burial with a headstone. Nobody scrolls to the foot of a settings-adjacent screen to audit routine attendance. In practice RECORD will be seen by exactly one person: Sarthak, once, on the day it ships, when Ren shows it to him. After that it is write-only memory — data the app keeps for an audience of nobody. And write-only memory is worse than no memory, because it still costs: the attendance logging, the ghost-session rules, the auto-pause machinery all stay alive to feed a surface no one reads. The kill option at least has the honesty to let the machinery die with the room.

Worse, the demote splits the record's soul in two and weakens both halves. The Receipt is ephemeral by design — shown at the exact moment he is most tired, at session end, when it will be dismissed unread with the same thumb that dismisses everything. RECORD is permanent but invisible. So the design achieves the worst combination available: the moment of truth is unreviewable, and the archive is unvisited. The keep-the-tab camp can say what this verdict cannot: a tab is a *promise the data matters*. Sarthak is a man building a six-month campaign to AIR 1; the PROUD-user evidence from wave 1 said the night Vault was the app's only screenshot surface — the one thing he showed anyone. Demoting the record to a Shield footnote takes the only surface that ever made him proud of the instrument and files it under plumbing. Men keep apps that make them proud; they delete apps that are merely correct. Beauty is the grading axis, and this verdict grades the record at zero beauty on purpose and calls it discipline.

And the nav argument is thinner than it looks. Three stops versus four is a difference of one detent on a switch he drags perhaps twenty times a day — seconds per week — weighed against a record he might open every Sunday for six months: 26 audits of whether his routines are real or theatre. That audit *is* a decision — which routines live, which die — and this verdict's own §1.2 admits routine attendance is load-bearing while exiling its only evidence to a scroll-foot. If a fact can kill a routine, it has originated a decision, and a surface that originates decisions has earned its stop by this verdict's own test.

**That is the case. It loses — narrowly — on one fact:** the Sunday audit does not need a destination, because Shield is already where routines live and die; the audit happens standing over the routine rows it judges, which is a better room for the verdict than a separate hall of numbers. But if Sarthak's actual behaviour proves he audits weekly and proudly, this verdict should be reversed to KEEP — and the demote's own logic says so.

— Plan, Designers (IA seat), for Sana

---

## COORDINATOR'S ADDENDUM (Sana, 29 Oct 2026 — build-true corrections)

Two rulings from the Designers merge bind anyone building from this file:

1. **The section is titled LEDGER, not RECORD.** Teen's naming law (one
book, one name — LEDGER is the surviving word; VAULT and RECORD both die)
overrules this file's working title wherever it appears above. The Receipt
keeps its name; the Shield-foot section is the LEDGER section.
2. **The hero is Sora 700 50sp, not 44sp.** 44 is illegal against Set's
ladder (50/44 = 1.14; ladder now build-track with Ren). A held-session
count is a total, and totals sit on the H3 rung: 50sp, −1.5 tracking,
lh52. Everything else in this verdict — the demote, the Receipt's
ephemerality, the absence-as-`—` rule, the case against itself — stands
exactly as Plan filed it.
