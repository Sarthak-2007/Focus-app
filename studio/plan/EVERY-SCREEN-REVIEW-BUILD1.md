# EVERY-SCREEN REVIEW — COMBINED BUILD 1 (Deck + Tasks)

WORK-QUEUE item 6 · Critics (Hale, coordinator) · Oct 29, 2026
Benches: Moss (Deck claims), Una (Tasks claims), Quaere
(pixels/finish, PIL-measured), Rank (ship record).
Commissioned in Ren's dispositions mail/hale/1006; scope =
the shipped tree only.

SURFACES: monolith-batch1.apk (sha256 e6d3a1493f28a40e,
byte-verified by Rank), renders 01_deck / 03_tasks day +
night. PROVENANCE (Rank, verified): the delivered renders
are shots/01_deck_ship.jpg + 03_tasks_ship.jpg (10:53 IST);
the shots/*.png on disk are an 11:08 IST same-source
regeneration from the baseline run (pixel diff mean-abs
0.86–1.09, only substantive delta = the live FINISHES clock
text). Both were read; every render claim below holds in
both states unless a line says otherwise.

METHOD: every printed claim on both screens checked against
the shipped code that produces it (lie-register method);
finish items measured in pixels, not eyeballed; anything
already ruled-and-scheduled is NOTED, not re-charged.
Per amendment A5, only S1 findings hold a merge.

## TALLY

- CHARGES: 23 — **S1 ×3 · S2 ×13 · S3 ×7**
- ACQUITTALS: 24 distinct (listed at the end)
- NOTED (visible in B1, already charged/scheduled): 13
  finish items + 1 open unruled proposal (P2-M6)
- S1 vs the merge train: none of the three S1s sits inside
  the routines diff, so none holds the routines merge.
  ESR-03 (S1, train: none) + ESR-05/07/08 (S2, train: none)
  are Deck-local and priced in minutes-to-lines — the
  bench recommends Ren take them as a small truth patch
  on the next build rather than let them wait for a car
  that does not exist.

---

## S1 — LIES TO HIS FACE / BREAKS THE RECORD

### ESR-01 — Tasks prints "DONE 1": a completion he never made (Una)
The header's DONE tally is `completedTasks.size`
(FocusSequencerScreen.kt:258–265), fed by a SEEDED row:
MonolithRepository.kt:348–360 inserts "Review Spaced-
Repetition Deck (30 Cards)" as COMPLETED, completedAtMillis
= now − 1h, actualSecondsSpent = 900, on any fresh install.
The completed list itself is never rendered — only its
size — and the coincidental struck substep on leaf #01
(also exactly 1) hands the reader a second, wrong
explanation for the same numeral. MASTER-PLAN §4.6: "no
done count prints on this screen."
FIX: strike the seeded COMPLETED row (−12 lines) +
seed-sync cleanup deleting that exact row from existing
DBs (~15 lines) + kill the DONE tally (2 lines). ≈1 h.
TRAIN: drawer. (Ledger twin: ESR-16.)

### ESR-02 — While sealed, every verb except the tear stays live — and CUE rewrites the running session's record (Una)
The tear alone is sealed-aware (FocusSequencerScreen.kt:
497, :377). Substep rows (:463), START/CUE (:519), DEL
(:530) and the composer are unconditional, and there is no
second wall below: MonolithViewModel.cueTaskAndNavigateTo
Focus (:779) has no session check, and MonolithRepository.
promoteTaskToActive executes dao.updateSession(active
Session.copy(intention = promoted.title)) (:1775) — a
mid-seal CUE silently changes what the live session's
record says it was for. completeTaskAndAdvance and
deleteTaskById likewise take no session guard.
MASTER-PLAN §4.1 sealed law: no verb, no composer;
repository is the boundary.
FIX: verbs + composer absent (not greyed) while sealed
(~15 lines, screen) AND repository refusals for complete/
promote/delete/toggle while a session is active (~25
lines + tests). ≈half day. TRAIN: Slice E.

### ESR-03 — Running-state FINISHES recedes forever and omits the penalty (Moss)
Idle, the line is arithmetically true (see acquittals).
Running, TactileDeckScreen.kt:354 + :155-162 recompute
`now + totalSpanMinutes` on every tick — 30 minutes into
a 60-minute session the Deck prints now+60, advancing 1:1
with the clock, never arriving. The span also prices
dial×blocks+gaps only (:142) and ignores
penaltySecondsAdded, which the engine's own span
(MonolithModels.kt:326-330, spanTargetSeconds) prices
into every block. In the sealed state — the moment truth
matters most — the Deck and the desk clock (which reads
the span derivations) state different ends for the same
session.
FIX: running branch prints now + activeSession.
spanRemainingSeconds (~3 lines, TactileDeckScreen.kt).
TRAIN: none.

---

## S2 — MATERIALLY WRONG OR MISLEADING

### ESR-04 — "WILL SHUT 7 APPS" undercounts what the shield shuts (Moss)
The 7 is honestly derived (TactileDeckScreen.kt:120,
count of LOCKED; the seed holds exactly 7) but the verb
outruns the count: the enforcing service blocks everything
not OPEN (MonolithShieldService.kt:140-151) = 7 LOCKED +
3 PEEK_2M (YouTube, WhatsApp, Discord) = 10 from t=0.
He reads "7 will shut" and concludes WhatsApp stays open;
it is intercepted from the start. The bottom-nav SYSTEM 7
badge (MainActivity.kt:228-230) carries the same narrow
count. The line's removal is already ruled (slide+chrome)
— this charge is that its derivation was false WHILE IT
LIVED, and the LOCKED-only count must not migrate into
the chrome replacement (the chrome Guard line counts the
board, a different fact — keep them separate).
FIX: count `resolvedTier != OPEN` (1 line) or reword to
the LOCKED fact. TRAIN: slide+chrome (interim: none).

### ESR-05 — Idle FINISHES is frozen at first composition (Moss)
finishPair is remember(...) over keys that never change
while idle (TactileDeckScreen.kt:155-162) — the printed
finish is compose-instant + span, forever. Open the Deck,
deliberate 20 minutes, slide: the screen still promises
the finish computed when it opened. FINISH-LIST P2-M9's
capture of the same state reading a different clock time
("FINISHES 10:23 AM") corroborates: the value is a
photograph of whenever the screen composed. No "as of"
qualifier is printed.
FIX: derive from a ticking clock key, recompute per
minute while idle (~4 lines). TRAIN: none.

### ESR-06 — The slide badge "25M" contradicts the hero at the commit moment (Moss + Rank)
subMonoBadge = "${dialMinutes}M" (TactileDeckScreen.kt:
498; drawn BlockitCoreViews.kt:1125-1131). 25M is true —
it is the per-block length — but the slide carries no
context for it: on the one control he acts on, two numbers
both present as the session's length and only one is.
MASTER-PLAN §2.3 + §2.6 already rule this badge DEAD;
it ships in B1 anyway, and the ship entry never records
the mismatch (Rank: record omission, folded in here).
FIX: delete the badge (2 lines). Interim: "BLOCK 25M"
(1 line). TRAIN: slide.

### ESR-07 — NOW strip: "DONE" is a button wearing a status word's clothes (Moss)
The ACTIVE task's strip reads "…Problem Set (#1–6)  DONE
QUEUE 2". DONE is a tap target (TactileDeckScreen.kt:
400-413, onCompleteActiveTask) rendered in the same mono
register as the genuinely static QUEUE 2 beside it — no
chip, fill, or affordance separates verb from fact. Read
as print, the Deck declares an unfinished task done.
FIX: give the verb an action's body (chip/fill as the
armed-state chips already have) or move completion off
the strip (~10 lines). TRAIN: none.

### ESR-08 — FLOW latches: the wheel can leave the Deck selling flow the engine won't run (Moss)
Chip handler sets STOPWATCH_FLOW + dial 5 (TactileDeck
Screen.kt:311-312); isStopwatchMode = preset == FLOW ||
dial < 10 (:122-126); but setDialMinutes (MonolithView
Model.kt:438-445) never clears the preset. Tap FLOW, drag
the wheel to 90: pointer at 90, slide badge "90M", hero
still "FLOW", finish line still "OPEN FLOW • 20M FLOOR" —
while armMonolithSession passes durationMinutes = 90 and
startSession's isStopwatch = durationMinutes < 10
(MonolithRepository.kt:1160-1165) is false: the engine
runs a timed 90-minute session the Deck sold as open
flow. Three printed surfaces disagree with each other
and with the engine in one reachable state.
FIX: setDialMinutes clears STOPWATCH_FLOW at minutes
>= 10, or isStopwatchMode keys off dial alone (~3 lines,
VM). TRAIN: none.

### ESR-09 — "START 25m" starts nothing; "CUE" doesn't cue; the cue tap spends the arming seal's sound (Una)
Both words are the same clickable (FocusSequencerScreen
.kt:519-528 → onCueTaskToFocus + onNavigateToDeck). The
VM path (:779-790) only promotes and navigates —
armMonolithSession is never called; he lands on the Deck
unarmed. Worse, :780 fires playHarmonicSeal(arming =
true): the seal claims an arm that didn't happen. And
CUE's tap promotes the queued task to ACTIVE on the spot,
demoting the current active (MonolithRepository.kt:
1753ff) — the opposite of "line it up next."
FIX: rename to what they do (TO DECK / MAKE ACTIVE) or
make START arm (~10 lines + label); delete the seal call
from the cue path (1 line). ≈1 h once the word is picked.
TRAIN: slide.

### ESR-10 — The Tasks horizon bar is a dead control with a second, different maths (Una)
TaskHorizonBarView (BlockitTowerFadersAndCatalogViews
.kt:343-414) has NO touch handler at all (its soundEngine
field is assigned, never used) yet renders as the twin of
the tappable duration row directly below it. Its segment
widths are proportional to estimatedMinutes.coerceAtLeast
(10) (:375, :379) while the header sums raw minutes
(FocusSequencerScreen.kt:108) — for any task under 10m,
bar and header describe different queues (MASTER-PLAN
§4.2 kills exactly this denominator).
FIX: the plan's own cure — delete the bar in every form
(−~70 lines view, −~15 screen). Interim: flatten to an
unmistakable read-only track with raw-minute weights
(~20 lines). TRAIN: chrome.

### ESR-11 — The tear books last, in a cancellable effect — and its pulse sounds 260 ms before the record exists (Una)
LaunchedEffect(task.id) { delay(260); onCompleteTask }
(FocusSequencerScreen.kt:368-372). `tearing` is local
remember state: dispose the item, leave the tab, or kill
the process inside the window and the leaf resurrects
un-torn, un-booked — while tear() already fired
playCompletionPulse() at t=0 (:380): completion heard
before it is true, and in the cancellation case heard
and never true. MASTER-PLAN §4.5: kill the app mid-drop
and on return the task is done — shipped code cannot say
that. This is Lie Register 3's U-6, confirmed in the
shipped tree and extended: the record half is already
routed to the Drawer batch (Ren's 1006 disposition);
the sound half is new.
FIX: book in viewModelScope keyed by task id (survives
navigation) or record a pending completion at tear start
(~1–2 h); move the pulse to the booking instant.
TRAIN: drawer (record), sound timing with it.

### ESR-12 — DEL hard-deletes instantly; the only undo is a 5-second in-memory copy (Una)
DEL (FocusSequencerScreen.kt:530) → deleteTaskWithUndo
(MonolithViewModel.kt:673) → dao.deleteTaskById executes
immediately (MonolithRepository.kt:1827). The "undo" is a
VM StateFlow cleared by delay(5000L) (:684): process
death inside the window, or 5 s of not noticing, and the
row is gone with everything recorded on it (actual
SecondsSpent, struck substeps, status — MonolithModels
.kt:94-107). The shipped ACTIVE seed itself carries
420 s of recorded work one muted 12sp tap from erasure.
(Tief's permanence verification stands for COMPLETED
rows — the repository refuses those; this charge is
every other row.)
FIX: soft-delete — a DELETED status filtered from all
flows, restored by undo, purged later (~30 lines, no
migration; status column exists). ≈half day.
TRAIN: drawer.

### ESR-13 — Leaf paper: a claimed fix that is not in the render (Quaere + Una)
The pre-ship fix is real in code: .background(scheme.
surface) at FocusSequencerScreen.kt:400, under a comment
claiming "The leaf HAS paper … verified in code". The
tokens kill it: Theme.kt maps scheme.surface to
Color.kt's surface = the ground itself — day #E4DFDA,
night #171412. PIL census (Quaere): leaf interior column
sampled every 20 px, y 560–1680 = ground hex at every
row, both modes; Δ = 0, contrast 1.00:1. FINISH-LIST
P2-W1 measured the same 1.00:1 pre-ship and is STILL
correct in B1 — the fix must not be booked as W1 landed.
(This is the false-close class: code-true, render-false.
Una graded S3 on finish grounds; the bench files S2
because a fix that exists only in code falsifies the
fix list itself, not just the screen.)
ROOT-CAUSE WARNING (Quaere, for Ren): the Deck card
spends the same token (ESR-NOTE-1) — scheme.surface IS
ground by definition, spent by two screens that both
believe they bought paper. Fix call sites per W1/S1, or
redefine the token and re-audit EVERY scheme.surface
consumer before the next Tasks/Deck car merges.
FIX: ACTIVE (head) leaf → scheme.surfaceBright (day
#FAF6F0 1.23:1 / night #362C27 1.35:1 — Weiss's spec
exactly, already in the theme) + that leaf's perforation
dashes in ground @100%. ~2 lines. Queued leaves stay
surface per the resting-slip law.
TRAIN: drawer (earliest Tasks-touching car still to
merge; chrome is the fallback).

### ESR-14 — Night Tasks: the horizon's selected segment floods full cream — a fourth selection language (Quaere)
Night seg-0 fill = #FFE4CF full marker cream, 12,774 px
— the brightest filled object on the night screen,
brighter than the NIGHT pill. The F-03 night cap (38%
blend #A79587) occurs 0 times in either night frame:
the cap exists nowhere in B1. Root: TaskHorizonBarView
consumes the flipping primary token (night primary =
cream), while the duration row 10 cm below already uses
inkControlFill() correctly (night #4A423C). Selection
census across all four renders: band pill ✓ (F-05
language), duration row ✓, horizon day ✗ (inverted
staircase, ESR-NOTE via P2-W3), horizon night ✗ (this
charge). The theme's own comment claims the night
floodlight was already fixed — it moved; it didn't die.
FIX: seg-0 fill → inkControlFill() (one call-site swap),
glyph stays #FFE4CF, add P2-W2's 1dp top marker #FFE4CF
@90%. ≤3 lines + marker. TRAIN: chrome.

### ESR-15 — The ship entry's clock is false: stamped 12:00 for a 10:57 delivery, filed out of order (Rank)
BUILD-CHANGES.md L383 stamps the ship 12:00 IST; the
delivery was 10:57 (63 minutes earlier). The ledger's own
mtime is 11:08:36 IST — the entry was written before its
own stamp — and the entry sits in the file BEFORE entries
stamped ~11:05, 11:02 and 11:08. Nothing load-bearing
inside the entry is false (hashes, sizes, disclosures all
verify — see acquittals), which is why this is S2 and not
S1: what breaks is the ledger's reliability AS A TIMELINE.
(Pattern, not one-off: the 10:20 entry at L256 also
precedes the 10:17 entry at L271.)
FIX (one text edit): restamp to [10:57 IST], add an
erratum line ("originally stamped 12:00 in error"), move
the entry to chronological position. TRAIN: none.

### ESR-16 — The ship entry overclaims: "fabricated seed history STRUCK" (Rank)
True in the register's narrow sense (sessions / intercept
logs / notifications — verified struck, see acquittals).
As a standalone IN-list line it reads "no fabricated
history ships" — but the same seed function (Monolith
Repository.kt:300-372) still inserts 4 invented tasks,
including ESR-01's pre-completed one with 900 s booked
and the ACTIVE seed carrying actualSecondsSpent = 420,
all visible in the delivered renders. Nothing was hidden
from his eyes (the renders shipped beside the entry),
which keeps this at S2 — the overstatement lives in the
ledger's wording.
FIX (text): amend the line to "fabricated seed SESSION
history STRUCK (sessions / intercept logs / notifications);
seeded starter tasks remain — 4, incl. 1 pre-completed
with 900 s booked — named for the train that removes or
replaces them." TRAIN: none (record); the seeds
themselves are ESR-01's fix (drawer).

---

## S3 — FINISH, LATENT, AND RECORD-KEEPING

### ESR-17 — GUARD chips can flash a fabricated "0/4" on first paint (Moss)
_systemGuardStatus initialises to an all-false literal
(MonolithViewModel.kt:240-254); the header prints from
that flow (MainActivity.kt:221-226) until refreshSystem
Guard() lands. The settled 1/4 is real (acquitted); the
first-paint 0/4 is a placeholder wearing a measurement's
typeface. Same class for HELD's empty-list first paint.
FIX: gate chips on first refresh or render "—/4" until
then (~5 lines). TRAIN: chrome.

### ESR-18 — Status-by-position: NOW and ACTIVE can crown a merely-queued task (Moss + Una)
Deck: activeTask = first ACTIVE ?: first QUEUED
(MonolithViewModel.kt:182-185) — a queue-only state prints
that task under NOW with a DONE verb. Tasks: isCued =
index == 0 (FocusSequencerScreen.kt:305) keys the ACTIVE
label, bold title and START off list position, never
resolvedStatus; the VM's fallback (MonolithViewModel.kt:
171-173) feeds the same risk. MASTER-PLAN §4.3 kills this
by name (head renders NEXT). Latent — the seed head IS
status-ACTIVE, so the shipped renders don't show it;
code-proven on both surfaces.
FIX: key off resolvedStatus == ACTIVE; fallback head
labelled NEXT (~3 lines per surface). TRAIN: none.

### ESR-19 — The accent law contradicts itself in the record, and the code overspends in unseen states (Una)
RULINGS.md:124 (chair law): "orange is commit fill only;
record words print in ink." FINISH-LIST F-01/F-15 take
the other side and sanction START-as-text as Tasks' one
accent. The shipped RENDER is clean (Quaere's census:
exactly one accent element per screen per mode — Deck's
commit fill, Tasks' START glyphs; F-01 NOT VISIBLE on
Deck/Tasks). But RULINGS U3 (:380-384) records the chair
law's own source as UNTRACED, and the code overspends
even under the Finishers' budget in states these renders
don't show: composer caret = SolidColor(primaryContainer)
(FocusSequencerScreen.kt:195) and the undo banner's UNDO
chip at full accent fill (MonolithCustomComponents.kt:
154). The comment at :488 ("The screen's one orange lives
on the cued task's START") is false in its own file.
FIX: Ren settles fill-only vs F-15 once (and the U3
provenance with it); caret → ink (1 line), UNDO chip →
ink (1 line) today regardless. TRAIN: chrome.

### ESR-20 — SPLIT doesn't split; it pastes three canned steps (Una)
SPLIT (FocusSequencerScreen.kt:505-515) → autoSplitTask
IntoMicroSteps (MonolithRepository.kt:1905-1912) →
defaultSubStepsForCategory(title, categoryTag) — but
MonolithModels.kt:162ff branches on category alone; the
title argument is never used. Every "Study" task gets the
identical three generic steps whatever the task is. The
verb promises derivation; it delivers a template.
FIX: cheapest truth is the rename — STEPS (1 line).
Real derivation is a bigger build nobody has priced.
TRAIN: none.

### ESR-21 — The tear spends the completion pulse F-N2 already replaced (Una)
MASTER-PLAN §4.8: "the completion pulse NEVER fires on
Tasks." RULINGS F-N2: a distinct playTearPulse — "a tear
is not a session completion." In the shipped tree
playTearPulse has ZERO hits; the tear calls
playCompletionPulse() (FocusSequencerScreen.kt:380).
An unlanded ruling, audible in B1. (Which sound — this
charge; when it sounds — ESR-11.)
FIX: the sound-engine sweep's priced playTearPulse
(~20 lines engine, 1 call-site swap). TRAIN: none
(sound sweep).

### ESR-22 — Stale KDoc over the latch claims the behaviour the latch killed (Una)
MonolithRepository.kt:1682, over completeTaskAndAdvance:
"Marks the target task as COMPLETED (or toggles back to
QUEUED if already COMPLETED)" — the body directly below
refuses to toggle (acquitted at A-3). The next builder
reading the doc reintroduces MO-1.
FIX: rewrite the KDoc to the latch (2 lines). TRAIN: none.

### ESR-23 — The proof doesn't keep: render provenance + the 24-tests claim (Rank)
(a) Delivered renders survive only as the *_ship.jpg
downscales; shots/*.png were overwritten by the 11:08
baseline regeneration and the ship entry never says so —
a later reader audits the wrong file. (b) "24 tests,
0 failures, 0 errors" (10:53): the XMLs were deleted by
the 11:08 run's results-dir cleanup; the surviving log
proves the suite ran green but carries no count. The
count is corroborated by the baseline's surviving 11:08
HTML report (24/0/0, same suite) — graded WAS-VERIFIED-
NOW-UNVERIFIABLE, not contradicted.
FIX: annotate the ship entry with both facts (text);
standing rule priced at zero build cost — future ship
entries paste the XML totals line into the entry itself.
TRAIN: none.

---

## NOTED — VISIBLE IN B1, ALREADY CHARGED / SCHEDULED (not re-charged)

| # | Item | B1 state (measured where pixels) | Home |
|---|---|---|---|
| N-1 | Deck card = ground, both modes (P2-S1 / P2-M3; F-13 closed FAILED) | Card interior day #E4DFDA / night #171412 = ground (75.0–75.1% of frame); hairline only. Same token root as ESR-13 | chrome |
| N-2 | Band guillotine (F-04; P2-A1/P2-M8) | Worse than filed: Tasks leaf #03's whole action row occupies 0 rows (band top row 1687; the row belongs at ~1668–1688, ground instead). Remedy verified ABSENT in shipped code: LazyColumn has no contentPadding; no 100dp, no fade in either screen file | chrome |
| N-3 | Deck void (P2-S7 flag) | 185.5dp of bare ground, NOW bottom → slide top (Schein ~179dp confirmed). Quaere attaches a priced proposal: slide in-flow under NOW at 24dp gap, or NOW bottom-anchored (~1–10 lines); verify in B2: gap 24dp, void 0dp | chrome (flag + proposal) |
| N-4 | Wheel specular centre 300° vs REGISTER lamp 245.2° (P2-S2 / F-16) | CONFIRMED to 0.5°: lit span 250.5–350.0°, centre 300.25°, both modes; at 245.2° the ring is tick over plain ground; the finger's quadrant (200–250°) is the unlit one. Day tint #F6F2EC ≠ registered #FFF6EC | chrome (already booked) |
| N-5 | Wheel pointer is a lollipop (F-02 / P2-S4) | Day dot #FBF1EC ≈Ø7.5dp at the rim, brightest object on the wheel; night disc #EDE3D8, centre dot #1B110D; the 4dp tail sub-fix landed | chrome |
| N-6 | DAY/NIGHT pill steals first glance (F-11 / P2-M1) | Night pill = full cream fill, 5,608 px, both screens | chrome |
| N-7 | Night tick ring faint (F-12) | Recess channel #100E0C confirmed; minors ≈2.15:1 per Pass 2 census | chrome |
| N-8 | Perforation = pad-edge ink; pad edge 1dp @ 2dp pitch (P2-W4/W5, P2-A4) | Identical colours both modes (day #A8A39F, night #4E4847); night dashes 330px/330px ground at the sampled row | chrome (Pass 2) |
| N-9 | START type ladder not landed (F-15 / P2-W7) | START still Atkinson; DONE — TEAR letterSpacing still 1.sp (Anonymous Pro 700 / 0.5sp owed) | chrome (Pass 2) |
| N-10 | Hub wells full cream (P2-S5) | 2 dots #FFE4CF, 546 px total | chrome |
| N-11 | Slide stripe (P2-S8) | 3dp wide, #FBF1EC both modes (not #FFE4CF @90%); marker-vs-groove naming still owed by the builder | slide (verify-first) |
| N-12 | FINISHES faint at night (P2-M9) | Night row #776A60; day #5F5856 | chrome |
| N-13 | Old band + top strip; tab names FOCUS/SYSTEM/HORIZON vs ruled DECK/SHIELD/TASKS/LEDGER | As the ship entry's STILL OLD list states; counts on the band verified TRUE (A-10) | chrome |
| — | Night slide cap full saturation (P2-M6) | Cap #F47442, 14,750 px, largest accent area at night — LAWFUL under F-01 as it stands; M6 is an UNRULED proposal, not a defect | Ren to rule |

Night staircase census (Quaere, Deck night): ground
#171412 HIT (75.01% of frame), recess #100E0C HIT (tick
channel), raised #362C27 HIT on hub disc + band body but
MISSED on the card (= N-1), sheet/cap #4A423C HIT, AOD
#000000 = 0 px in the frame set — no AOD render exists;
P2-M10's capture debt stands (Linea's commission).

---

## ACQUITTALS — 24 (the killing evidence, compressed)

DECK — Hero "1H 00M" under TOTAL is TRUE: TactileDeck
Screen.kt:142 span = 25×2 + 10 = 60, identical to the
engine's per-block targets + 10-min Safe Breaks and the
model span (MonolithModels.kt:329-330) — MASTER-PLAN
§2.3's formula verbatim. · Wheel pointer at 25 and the
30–180 numerals are TRUE (minutesToAngle(25) = 153°;
numerals are block-length scale print). · Tetrad hub 2/8
filled is TRUE to state (BlockitCoreViews.kt:742; both
renders match well for well). · HELD 00 is derived, not
hardcoded (capturedNotifications.size; fresh seed inserts
none). · GUARD 1/4 is derived live from real system reads,
and the counted set is exactly the ruled four —
accessibility, usage access, device admin, notification
listener; overlay correctly excluded (MainActivity.kt:
221-226; MonolithRepository.kt:57-63). NO hardcoded 1
exists in the chain. · QUEUE 2 is TRUE (queuedTasks.size;
the COMPLETED seed correctly excluded). · NOW title is
the seed ACTIVE task verbatim. · FLOW chip's RENDERING is
honest (unfilled because stopwatch is not live; the lie
is the latch behind it, ESR-08). · FINISHES arithmetic at
the compose instant is TRUE, breaks included (its sins
are temporal: ESR-03/05). · Band counts SYSTEM 7 /
HORIZON 3 are derived and true (the 7's narrowness is
ESR-04, the names are N-13). · DAY/NIGHT word + brand dot
are true to state. · Missing-content suspicion for the
Deck void DIES: Section 3 furniture is armed-only by
construction; the removed furniture was removed BY
RULING; the reserve is empty BY LAW — only the void's
placement survives (N-3).

TASKS — Header "3 TASKS • 95m" is TRUE as printed (raw
sum of active+queued estimatedMinutes, completed excluded;
25+25+45 = 95). · The tear books at 260 ms at the settle's
end (FocusSequencerScreen.kt:363, :371-372; DURATION_260
= 260) — the old 180 ms amputation is gone. · complete
TaskAndAdvance IS completion-only in the shipped tree
(MonolithRepository.kt:1690-1701) — the MO-1/MO-2 latch
Ren fixed pre-ship is present; a double fire cannot
double-count. · The struck substep is persisted state,
not local (toggleTaskSubStep re-serialises + dao.update
Task). · The tear itself honours HOLD (absent, not
greyed, while sealed; perforation overprints solid). ·
Leaf furniture is honest (#0i, per-leaf minutes, substep
numerals all read straight from the row). · Accent
census: exactly ONE accent element per screen per mode
(Deck slide fill; Tasks START glyphs) — FINISH-LIST F-01
is NOT VISIBLE on Deck/Tasks in B1. · F-06's inside-card
desert is NOT VISIBLE (max gap 38dp ≤ 40dp law; the
desert moved outside the card → N-3). · The PROTECTED
hero readout is untouched and correct in both modes.

RECORD — APK size 18,738,330 B and full sha256 verified.
· Assemble GREEN 10:41 supported by log (mtime 10:41:57,
BUILD SUCCESSFUL). · Migration 7→8 exists and NO
fallbackToDestructiveMigration call exists anywhere (the
single grep hit is the comment recording its removal). ·
Seed session/intercept/notification history is struck in
the shipped tree (the narrow claim — its over-broad
wording is ESR-16). · The STILL OLD list and both ship
disclosures match the delivered state faithfully; no
softening between delivery note and ledger.

---

## BENCH NOTES FOR REN

1. The three S1s do not intersect the routines diff —
   the merge train is not held by this review. ESR-03 +
   ESR-05 + ESR-07 + ESR-08 (~20 lines total, all
   Deck/VM, all train: none) would make a clean batch-2
   truth patch; the bench recommends it by name.
2. ESR-13 + N-1 are one token spent by two screens. If
   the fix redefines scheme.surface instead of the call
   sites, every consumer of that token needs re-audit
   before the car merges — Quaere's warning, endorsed.
3. ESR-11's routing: Ren's 1006 disposition already
   sends U-6 (the record half) to the Drawer batch.
   Una proposed Slice E; the bench files drawer per the
   standing disposition and flags the sound half as new
   either way.
4. ESR-19 needs one ruling from Ren, not a build: the
   accent law's two filed versions conflict, and the
   chair version's provenance is U3-untraced. Until it
   is settled, builders will keep acquitting themselves
   under whichever law they prefer.
5. P2-M6 (night slide-cap saturation) remains an open
   proposal — lawful today, unruled either way.

— Hale, Critics coordinator, filing for Moss, Una,
Quaere, and Rank (40 seats; four on this commission).
