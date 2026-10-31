# WIRBEL — CRAZY IDEAS FOR #5: TRUTH THAT CANNOT BE FAKED

**Seat:** Wirbel, crazy-ideas, flat under Ren. **Issue:** #5 — Truth Car 3 verification on the merged tree.
**Ground read first:** AUDIT-truth3-reverification.md (Una), ISSUE-5-WAVE2-REPORT.md (Tor).
**State I am designing against (verified in those files):** 20 CURED · 4 PARTIALLY · 2 STANDS over 26 rows,
0 REGRESSED — and the failure mode is ABSENCE, not repaint: times lane never landed (no peekMinutes /
breakMinutes / breakSeconds / extension columns in MIGRATION_11_12), W-41 (HELD = SHUT+PEEK flattened)
and W-43 ("YOUR STUDY RECORD LIVES IN YPT") still print false on the RECEIPT — the one permanent,
loved surface — UNA2-06's peek pill is state with no surface, UNA-05's "FIRES AS SET" has no live
fire behind it, and 8 units have NO PROOF EXISTS. Every idea below attacks absence/fakeability,
not wording. No green, no AI images, blocker-not-tracker throughout.

— Wirbel, crazy-ideas

---

## 1. THE CHAINED RECEIPT — every receipt hashes the receipt before it

**Crazy:** Truth stops being a claim and becomes a chain that breaks loudly if edited.
At seal, store `receiptHash = SHA-256(prevReceiptHash + sealInstant + heldAppsSplit + warrantSnapshot)`.
Print the first 8 hex chars on the receipt as `CHAIN #A3F9…` in Anonymous Pro.
If anyone (a future migration, a restore, a hand-edit) alters a past session, every later
receipt's hash stops verifying and the Drawer prints `CHAIN BROKEN AT <date>` instead of a count.
**Build:** 1 column on FocusSessionEntity (`chainHash TEXT`), computed in the seal transaction
(same transaction that already captures heldAppsAtSeal — Fader's capture point), a pure
`ReceiptChain.verify(List<Session>)` + unit test with a tampered-middle-session fixture.
**Kills:** W-41/W-43 class forever — the receipt can no longer be quietly re-worded after the fact,
because its numbers are inside the hash. Price: SMALL.

## 2. TWO WITNESSES OR IT DIDN'T HAPPEN — adversarial dual-store verdict

**Crazy:** The app currently believes ONE store (WatchStore). Make truth require two
independent witnesses who were NOT written by the same code path, and print DISAGREEMENT
as a first-class state instead of picking a winner.
Witness A = WatchStore sweep. Witness B = a dumb, separate `ColdWitness` that re-derives
ONLY from Android itself at render time: `AccessibilityManager.getEnabledAccessibilityServiceList`,
`DevicePolicyManager.isAdminActive`, `UsageStatsManager` last-event. No shared cache, no shared flow.
Seam prints: both agree LIVE → `PROOF: LIVE BIND`; agree OFF → OFF; disagree → `WITNESSES DISAGREE —
TAP TO RE-SWEAR` (never silently trust A). 
**Build:** ColdWitness object (~80 lines, three platform calls), seam derivation takes Pair<Warrant,ColdVerdict>.
Test: fake A=LIVE / B=OFF → assert DISAGREE print.
**Kills:** UNA2-03's unbounded-age warrant at the root — a stale A cannot fake it past a fresh B.
Price: SMALL–MEDIUM. This is the un-fakeable core the car was named for.

## 3. PROOF AGE AS A NUMBER, COUNTING UP ON THE GLASS

**Crazy:** Stop printing STALE as an adjective. Print the proof's age as a live number:
`PROVED 00:47 AGO` ticking up every second on the seam, in Anonymous Pro, and past 45s the
digits themselves are the alarm — no colour change needed, the number shames itself.
At 10:00 it flips to `PROVED LONG AGO — RE-PROVE` and the slide's pre-flight re-sweep becomes visible theatre.
**Build:** Warrant gains `readMonoMs` (the sweep era already exists in WatchStore — Tor/Una both note it);
Deck's existing 60s ticker becomes a 1s ticker for this one cell only. Pure `proofAgeLabel(monoNow, readMono)`.
**Kills:** UNA2-03 rider + the whole "warrant cannot age" family, and gives #14's sheet the same
grammar for free (its STALE gloss becomes a number, not a word). Price: SMALL. (Extends the prior
crazy-ideas seat's "PROVED 46S AGO" proposal on #14 into a system-wide law.)

## 4. THE CANARY APP — Monolith blocks ITSELF, on purpose, every day

**Crazy:** Ship a tiny internal canary package/activity (`CanaryBlockedActivity`, debug-visible
only as a Deck footer test) that is permanently SHUT-tier. Once per cold start, the system
*silently attempts the intercept path against the canary in a dry-run harness* and records
whether the accessibility service actually produced the intercept decision. Result prints
only when it fails: `CANARY DID NOT DIE — BLOCKING UNPROVEN TODAY`.
No fake success print when it passes — silence is the pass (house law: no grade, no score).
**Build:** dry-run seam already half-exists — extract the tier-decision from
MonolithAccessibilityService into a pure `InterceptDecision.decide(pkg, tiers, lease)` (which
UNA2-01 needs anyway), canary test calls it with the canary pkg + a synthetic window event.
**Kills:** The "service bound but brain-dead" OEM case that every audit fears and no print catches.
Price: MEDIUM (the extraction is the price; the canary is 20 lines after it).

## 5. THE PEEK PILL IS A BURNING FUSE — and handing it back is a verb

**Crazy (for UNA2-06, the saddest item in the car):** The pill must not be a countdown *label*.
Make it a 120-second fuse drawn as a ruled line that physically shortens from both ends,
draggable, and — the crazy part — **grabbing and holding the pill for 1 second hands the peek
back early and prints the unspent seconds on the receipt** (`PEEK HANDED BACK WITH 0:43 LEFT`).
Truth becomes visible because the lease's life AND death both leave a printed witness.
In-app half renders inside the Airlock overlay first (the one surface guaranteed on screen at
grant — prior crazy seat's proposal, adopted), overlay half rides Pastille's pill lane.
**Build:** composable consuming `floatingPeekPill` (state machine already waits in the VM —
Tor: "cheapest wow in the backlog"), `end2MinutePeekEarly` already exists and is unreachable;
wire it to the hold. Receipt field = unspent seconds at early-end (nullable, prints only when used).
Price: SMALL for in-app half. Do not ship derivation-only again — render or strike FT-5 by name.

## 6. TIMES THAT TESTIFY — the settings page prints its own receipts

**Crazy (for the missing times lane, batch-10 12→13):** When peekMinutes/breakMinutes finally land,
don't just add columns — make every CHANGE to a duration write a one-line entry into the Drawer
drawer-log: `PEEK 2 MIN → 5 MIN · <date>`. Then a receipt from March can be re-read against the
setting that was live in March, because `breakTakenSeconds`-style honesty extends to settings:
each session row stamps `peekMinutesAtSeal / breakMinutesAtSeal` alongside heldAppsAtSeal.
A duration can never retroactively lie about a past session again.
**Build:** rides the same 12→13 migration (2 extra INTEGER columns, stamped in the existing
seal transaction), settings-write hook appends to a `settings_log` table (or day_notes — pick one, price both).
Price: SMALL on top of the already-owed migration. Without the at-seal stamps, FT-5 stays unprovable.

## 7. THE LIE REGISTER, PRINTED IN PARK — the app keeps its own rap sheet

**Crazy:** PARK gains a quiet drawer section: `KNOWN FALSE PRINTS` — a permanent, on-device list
of every sentence the register struck (W-43's RECORD line, W-11's FIRES AS SET, UNA2-05's
"Sessions end when their time does."), each with the date it stopped printing and what replaced it.
Not a score, not a grade — a rap sheet the app keeps on ITSELF, in the Drawer, forever, searchable
(the Drawer is already his forever-drawer). A user who catches a new false print has somewhere
it *belongs*; a future agent who re-prints a struck sentence (S-2/S-4 disease on #14) faces the
list on the very phone.
**Build:** static string list in code (`StruckSentences.kt`, sourced from LIE-REGISTER-2/3),
one PARK section, zero machinery. Updates only by code change — deliberately hard to edit casually.
Price: FREE–SMALL. This is the institutional memory Lapse 0 proved the studio lacks, put where
turnover can't overwrite it: in the shipped app.

## 8. PROOFBASE FREEZER + TRUNK TOMBSTONE (process, for Lapse 0)

**Crazy:** Make the proofbase a git TAG with a tombstone file: `studio/verification/PROOFBASE.md`
committed on main naming tag `proofbase-batch9-f1f24ea`, its hash, and the sentence
"TRUNK WAS OVERWRITTEN ONCE (Oct 30, batch-9 lineage). THIS TAG IS THE BASELINE. DO NOT MOVE IT."
CI (GitHub Actions, builds run on GitHub now) gets a 10-line job that fails if the tag moves.
**Kills:** Lapse 0 recurring silently. Price: FREE. Truth about the code needs the same
un-fakeability as truth in the code.

## 9. THE DISSENT LINE — every verdict prints who disagreed

**Crazy:** Pruefer/NACHSICHT downgraded grades and the report *quoted the dissent and ruled it* —
make the app do the same. When witnesses disagree (§2) or a proof is PARTIAL, the print carries
a dissent footnote in 10sp Anonymous Pro: `† ONE WITNESS SAYS OFF`. Never launder a partial
into a clean PASS on any surface — receipt, seam, sheet. The UI's job is to show its working.
**Build:** verdict type becomes `Verdict(claim, dissent: String?)`; three render sites print the † line.
Price: SMALL. Direct cure for the "honest label for batch-9" problem, generalised: no surface
may ever be more certain than its evidence.

## 10. SELF-SWEARING TESTS — tests that must fail on proofbase, run in CI both ways

**Crazy:** Pruefer's standard (discriminating assertion: fails on proofbase f1f24ea, passes on merged)
becomes mechanical: CI checks out proofbase into a side tree, runs ONLY the truth-test suite against
it, and REQUIRES proof that each named test FAILED there; then runs on the branch and requires PASS.
A test that passes on both is auto-flagged `NO PROOF EXISTS — TEST PROVES NOTHING` in the CI summary.
**Build:** one Actions job, two checkouts, junit XML diff by test name. The 8 NO-PROOF units
(UNA2-01 end-state, SIL-3, UNA2-05, UNA2-07+guard, APP-3, APP-4, UNA2-03 rider, UNA2-04 deck half)
are the first suite. Price: MEDIUM once, FREE forever. This turns "verification complete on paper"
into verification that re-verifies itself on every push.

---

### My pick if only three ship
**#2 Two Witnesses** (un-fakeable core) · **#5 Burning Fuse pill** (kills UNA2-06, the car's saddest absence) ·
**#10 Self-Swearing Tests** (makes every future "CURED" claim carry its own proof or confess).
Then #1 Chained Receipt, because the receipt is the surface he loved — it should be the hardest to fake.

— Wirbel, crazy-ideas
