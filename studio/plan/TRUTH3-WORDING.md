# TRUTH CAR 3 — THE WORDING REGISTER

**Seat:** Hale — Critics, the lie register. Working directly under Ren.
**Date:** Oct 29, 2026 (~22:36 IST). **Method:** reading audit only, no
compilation. **Ground:** trunk `~/workspace/clone-inspect/Focus-app` at
batch-8 state (DB v11, HB-2 fix in). Every entry verified in batch-8
code by my own read, except where an entry cites Una's audits
(`plan/AUDIT-park-claims.md`, `plan/AUDIT-deck-intercept-claims.md`,
batch-7 state) — those cites are marked (Una) and the prints were
spot-verified unchanged in batch 8.

**The rule, same as the receipt register's:** a sentence is a promise.
Each entry names the claim, the code fact that must hold for the claim
to be true, and a verdict:

- **KEEP** — the words are true once the cure this car is already
  building lands (the cure is named; if it doesn't land, the KEEP
  lapses and the sentence stands charged).
- **REPLACE** — the current words outrun any fact; replacement words
  are supplied and say exactly the true thing, no vaguer. Sent to the
  owning builder's inbox the same hour.
- **FLAG** — no cure in this car makes it true; the car or build that
  could is named, and the print rule is supplied so it lands true.

**The value-slot law (Freya, PILL-AND-TIMES §4.3, adopted as wording
law):** after this car, no printed word may hardcode the peek's
minutes or the cooldown's minutes. Where a ruling below shows a
number, it is a VALUE SLOT — the surface reads it from the same store
or derivation the machinery reads, never a literal in the copy.

---

## 1 · PARK — the board, the telltale, the key, the sled, the plate

**W-01 · The six permission words (GRANTED / MISSING / STALE) and the
six names.** Claim: each line's grant state, measured by its own
query. Fact: Una A-01 — every word measured, never proxied; staleness
derived at render (`WatchStore.kt:313–322`); a never-measured line
prints its name only (`ParkDrawer.kt`, board block). **KEEP** —
the surface the rest of the app is judged against; no word changes.

**W-02 · The three interceptor words (RUNNING / PAUSED / OFF).**
Claim: the interceptor chain's state — NOT the setting's state. Fact:
the derivation is a chain word by construction
(`WatchStore.kt:409–451`: setting on + every parent GRANTED + no
suspension → RUNNING; setting off OR any parent not GRANTED → OFF).
One word, OFF, therefore covers two facts (he turned it off / a
parent is missing) — the same construction the NOTIFICATIONS line
already lives under honestly (Una A-10: no setting exists there and
none is faked). **KEEP, with the condition that makes it true:**
after Falk's and Imke's cures, the setting's own state prints at the
setting's own home (the toggle's ON/OFF, W-09/W-10); the board word
stays a chain word and never becomes the setting's only print. If a
lane leaves the board word as the setting's sole rendering, this
KEEP lapses.

**W-03 · "LAST PROVED {HH:MM} · SELF-REPORT".** Fact: Una A-02 —
the instant advances only on a complete six-item set from one sweep
era; PARK prints time + the qualifier. SELF-REPORT is the honest
word. **KEEP.**

**W-04 · The FIX key, "FIX — {first lack}".** Fact: Una A-04 — it
names the first missing item in board order and opens that grant's
own pane. **KEEP.**

**W-05 · The THEME readout** ("AUTO · NIGHT — SESSION ACTIVE" /
"AUTO · NIGHT — {ROUTINE} {HH:MM}" / "AUTO · DAY — NO ROUTINE OWNS
IT"). Fact: one evaluation drives scheme and print together
(`MonolithViewModel.kt:66–95`, `evaluateTheme`; Una A-05). The three
sentences are exactly the evaluation's three branches. **KEEP.**

**W-06 · Plate line 1: "WHEEL SETS TIME · SLIDE ARMS · THE HOLD
BREAKS".** Fact: Una A-14 — the falsifiable clause (the hold breaks)
holds in code. **KEEP.**

**W-07 · Plate line 2: "HOLD: 60 SECONDS, BANKED EVERY 15 · ONE
BREAK A WEEK".** Fact: Una A-11 — bank interval is totalMs/4, banks
lock at 15/30/45, completion only at the full 60 held; the weekly
remainder is (1 − spends) on a Monday-anchored week. The hold is
NOT made editable by this car (Sarthak's verdict stands at 60), so
the number stays a fact, not a setting. **KEEP.**

**W-08 · Plate line 3: "PEEK: 2 MINUTES, THEN SHUT AGAIN" →
REPLACE.** The claim dies inside this very car: Rurik's editable
times make the lease length his setting (default 2, bounds 0–30 —
PILL-AND-TIMES §3), and the moment he sets 5, a static plate prints
a false standing fact in furniture's clothing. The lease fact itself
is exact (Una A-12: lease = nowMono + the duration; both enforcers
cut at its end). **Replacement:** the plate line prints the set
value from the same GuardSettings row the lease reads:
"PEEK: {peekMinutes} MINUTES, THEN SHUT AGAIN" — and when
peekMinutes is 0 (peek off), the line prints "PEEK: OFF". The plate
stops being byte-static in exactly one line; furniture does not
outrank fact. → Bodo (plate owner). If the plate's static form is
defended at merge, the fallback true line is "PEEK: A SHORT LEASE,
THEN SHUT AGAIN" — filed here so the choice is Ren's, not silence's.

**W-09 · The SOUND EFFECTS row + its ON/OFF word.** Claim: the
setting's state. Fact today: the word reports a flow that persists
nowhere and does not govern every engine (Una UNA-03:
`toggleSound`, MonolithViewModel.kt — batch-8 read confirms it flips
the one engine var and a flow, nothing else; the seal transition's
first-caller-wins binding can mint a sound-on engine behind it).
**KEEP as cured** — Imke's cure (persist the flag; every engine
reads it; one engine passed at every `get()`) makes the existing
words true; no word changes. The row's designed 260 ms word lag
("flips on arrival, never mid-throw", ParkDrawer.kt sound block) is
choreography, not staleness. If the cure lands without the
persistence half, this KEEP lapses.

**W-10 · The sled view's dormant title: "SHORTS & REELS BLOCKER" /
"Auto-back out of vertical short feeds" → REPLACE (before it ever
prints).** The one titled switch view in the tree
(`BlockitSuitcaseAndSwitchesViews.kt:33, 46` — defaults verified in
batch 8) is seated today only in chrome mode, title suppressed, as
the SOUND sled (Una UNA-01). If Falk's or Imke's cure seats this
view non-chrome for a real toggle, these words wake with it.
"Auto-back out" overclaims: the sniper's true act is BACK + the
airlock, and only while a session runs (Una, deck audit A-08).
**Replacement, if the view is seated for the shorts toggle:**
title "SHORTS + REELS" (its board name, W-02's family) and subtitle
"SHORT FEEDS MEET THE AIRLOCK WHILE A SESSION RUNS". For any other
toggle seated in this view, the title is that line's board name and
the subtitle is struck, not improvised. → Imke (toggles), copy in
the register for Falk.

**W-11 · Plate line 5: "ROUTINES: SET BY HAND ON SHIELD · FIRED AS
SET" → REPLACE (interim), reverting when the no-fire car lands.**
Fact: falsified live once (Sarthak's report, Oct 29 20:21; Una
UNA-05) — the chain alarm is inexact and no exact-alarm surface
exists in `service/`. The cure is Wache's F1–F5 car, NOT this one;
nothing in Truth Car 3 makes the second clause true. The first
clause is kept by the rows' own machinery (Una A-06). Una's ruling
stands: the line waits for the fix or prints the weaker true
sentence. The weaker true sentence is: **"ROUTINES: SET BY HAND ON
SHIELD"** — the clause is struck, not softened. The full line is
restored the day F1–F5 land in a build AND Voss's re-verification
files a live fire; until then the struck form is the only lawful
print. → Bodo.

**W-12 · Plate line 6: "SITES: KEPT WITH APPS ON SHIELD · DATA: ON
THIS PHONE ONLY".** Two claims on one line. SITES: true in every
state except the emptied list (Una UNA-06 — the `ifEmpty` factory
fallback at MonolithAccessibilityService.kt:182–185); this car's
cure drops the fallback, and first-run behaviour is unchanged
because the defaults are real rows. DATA: the app keeps the claim
(no INTERNET permission, no network code); the shipped
configuration breaks it (allowBackup="true", sample backup rules —
Una UNA-04); this car's cure closes the manifest. **KEEP as
cured** — both halves have named cures in this car (Wagen's list);
if either cure is cut at merge, the corresponding half-line must
be struck the same day, not left standing.

**W-13 · Plate line 7: "STUDY RECORD: IN YPT" → REPLACE.** Fact:
Una UNA-08 — YPT holds his study HOURS; the app keeps a study
record of its own on this phone (the Drawer is built from stored
session rows, `DrawerDay.kt`; session rows carry the completed-task
summaries; day words persist). On its plain reading the sentence
is false. **Replacement: "STUDY HOURS: IN YPT"** — one word longer,
exactly true. → Bodo. The same sentence in its second costume is
ruled at W-43 (the receipt footer); the two cure together or the
charge reopens.

**W-14 · Plate line 8: "SOUND: FLOAT-PCM, 4 PARTIALS · POWER: YOUR
PHONE".** Fact: the technical half verifies — 4 harmonic partials,
weights at `TactileSoundEngine.kt:50–51` (Salz's AOD desk confirmed
the same cites). "POWER: YOUR PHONE" asserts nothing falsifiable;
it is colour, and reads as colour. **KEEP**, halves noted so nobody
later cites the line as a measurement.

**W-15 · The plate head** ("MONOLITH ML-01 / UNIT Nº 01 · BUILT FOR
ONE USER · NOT FOR SALE"). Furniture; true of this build; falsifiable
in no particular (Una, non-claims). **KEEP.**

## 2 · SHIELD — the prints this car touches

**W-16 · The ROUTINES header count: "{n} ACTIVE".** Fact: it counts
`enabled` alone (`ShieldMatrixScreen.kt` header block, batch-8
read), including routines with `daysMask == 0` that the scheduler
refuses a boundary (Una UNA-09). This car's cure counts what the
scheduler counts (`enabled && daysMask != 0`, or the header takes
its word from the same derivation). The word ACTIVE then says the
true thing: schedulable. **KEEP as cured** — no word changes;
if the cure is cut, REPLACE the suffix with " ON" is NOT
sufficient (an ON routine with no days is the same lie in a
smaller word) — the count itself is the cure. → Bodo (Shield
prints travel with PARK prints in this car's map).

**W-17 · "SCHEDULE RESET TO YOUR DAY — TAP ANY ROW TO CHANGE
IT".** Fact: Una A-06 — prints only when a real reseed ran on an
existing install; any row edit dismisses; fresh installs never see
it. **KEEP.**

**W-18 · The BLOCKED WEBSITES header: "{n} SITES"** and the section
contract ("the list below is the list the blocker enforces",
section comment at ShieldMatrixScreen.kt:308–313). Fact: print =
his list's size; enforcement = the same list in every state except
the emptied one (W-12's SITES half). With UNA-06 cured, print and
enforcement agree in all states, including 0. **KEEP as cured.**
The REMOVE / ADD verbs do what they say (Una A-09). **KEEP.**

**W-19 · The LOADOUT cap's seated name (STUDY / SLEEP / WORK).**
Fact: the cap rests on a `remember` starting at STUDY whatever is
applied (Una UNA-07), printing one loadout's name over another's
live counts. No word is wrong — the fact under the word is. This
car's cure seats the cap from the applied-loadout state the screen
already receives. **KEEP as cured** (condition named: the cap's
rest position is the applied loadout, or the section prints the
two claims in disagreement, W-02's lesson on another surface).

**W-20 · Routine row words: "NO DAYS SET", the day letters, the
ON/OFF cap.** Facts: Una A-07 (chips, label and firing math share
one bit order) and A-08 (the cap prints stored `enabled`
verbatim). "NO DAYS SET" is the honest row-level truth that the
header count contradicted until W-16's cure. **KEEP.**

## 3 · THE DECK — refusals, seam, imprint, strip, chips

**W-21 · The refusal sentences.** "BLOCKING IS OFF — TAP TO FIX" /
"USAGE ACCESS IS OFF — TAP TO FIX" / "CAN'T CHECK BLOCKING — TAP
TO RETRY" (`MonolithCustomComponents.kt`, `refusalSentenceFor` —
batch-8 read, identical to Una's audit). Fact: Una A-02 — causal
priority is correct (accessibility first, then unstamped, then
usage DENIED, then UNSET, then the OFF backstop); the verbs do
what they say; a healthy wire prints nothing. "CAN'T CHECK" is a
true report of the UNPROVEN-unstamped state; its habit of greeting
every reopen is the stamp MECHANISM's defect — Stempel's lane, in
flight — not a wording defect: the sentence never claims the check
succeeded, only that it hasn't landed and a tap re-reads. **KEEP**
(all three; the words stand whatever Stempel's cure does to their
frequency).

**W-22 · The seam marks.** "BIND HELD · THIS VISIT / BIND HELD ·
UNSEEN / BIND NOT HELD / BIND UNCONFIRMED" × "PROOF: LIVE BIND /
BIND ONLY / POSITIVE / NONE" (`seamMarksFor`, batch-8 read). Fact:
Una A-01 (deck audit) — each branch's words match `deriveWarrant`'s
legs exactly; LIVE BIND's definition is the three legs actually
read, settled at the slide merge; a null warrant prints blank,
never a guess. This car's UNA2-03 cure (the warrant gains a read
instant; the seam and tenant degrade past PARK's threshold) needs
NO new vocabulary — the grades already say proven/unproven in the
house's words. **KEEP** — and a restraint filed with it: no lane
invents a Deck "STALE" sentence; the seam's existing grades are
the degradation's language.

**W-23 · The imprint tenant: "{n} APP(S) WILL SHUT" / "{n} APP(S)
HELD" → REPLACE.** Fact: the count is LOCKED+PEEK — SHUT and PEEK
tiers together (`MonolithViewModel.kt:451–454`, Una UNA2-02) — and
a PEEK-tier app neither shuts nor is held: mid-session it meets
the airlock and a short lease. The number is real; the noun is
the lie, in both tenses, and the same count freezes into the
sealed print. **Replacement — the split, mirroring Shield's nouns
(the face he already reads, "n SHUT • m PEEK"):**
- idle (LIVE wire only — the empty-box discipline is acquitted,
  Una A-06, and does not change): **"{n} WILL SHUT · {m} PEEK"**,
  singulars by the existing rule ("1 WILL SHUT · 1 PEEK"); when
  m == 0 the second segment is omitted whole (a zero segment is a
  printed nothing — the HELD line's omission law), and when n == 0
  the tenant prints "{m} PEEK" alone, never "0 WILL SHUT".
- sealed: **"{n} HELD · {m} PEEK"**, same omission rules.
→ Suki. The alternative cure Una filed (SHUT-only count, words
untouched) is NOT adopted: it would hide the peek tier's existence
from the one print that tells him what the seal does.

**W-24 · The frozen tense (UNA2-04).** Fact: BLOCK and the tenant
print the seal-time freeze; the seam and the sentence slot print
the live warrant, one frame can read "ON · n HELD" over "BIND NOT
HELD · BLOCKING IS OFF". The freeze is designed (the press-frozen
verdict — acquitted as intent, Una A-06); the charge is the
present tense with no qualifier. **Ruling (words + trigger): when
the live warrant reads OFF under a running session, the sealed
tenant gains its tense — the split print of W-23 with the suffix
" · AS SEALED" — and the BLOCK cell follows the seam's live word,
as it already can.** When live and frozen agree, no suffix
prints: a qualifier that always stands teaches the eye to skip
it. → Suki (the trigger is hers to wire; the words are these).

**W-25 · Strip cells: BLOCK (ON / OFF / UNKNOWN), TIME (ENDS
{HH:MM} / NO END / INVALID), APPS (the bare count).** Facts: Una
A-05 — ENDS is the one Now Frame derivation, the same number the
enforcer reads; NO END and INVALID are exact. UNKNOWN is the
honest word for UNPROVEN (W-21's family). The idle ENDS's
warrant-blindness is UNA2-03's rider — its cure is the read
instant, not a new word. The APPS cell's bare number claims
nothing its label doesn't: "APPS" + a count of the non-OPEN set;
the false noun lived in the tenant, ruled at W-23. **KEEP.**

**W-26 · The eyebrow: "BREAK · {n} MIN" / "SESSION RUNNING" /
"TOTAL".** Fact: Una A-11 — the break's minutes are ceiling-
rounded so the print never understates. **KEEP.** ST-02's new
verb (this car: ending a safe break early) takes its word here:
**the chip prints "END BREAK"** — the break family's own verb,
no "emergency" borrowed from the valve's face, no "skip" (he is
not skipping anything; the break ends and the next block starts,
which is the flip the machinery already has). → Rurik (break
machinery; Tamm builds the verb — the word travels with the
register entry).

**W-27 · The running chips: "DESK CLOCK", "HELD" + count badge,
"GRACE WINDOW ABORT" + countdown badge, "EMERGENCY BREAK VALVE
(60S HOLD)" + "{n}/1 WEEK" badge.** Facts: each verb does what it
says — the clock opens the clock; HELD opens the digest and its
badge is the live captured count; the grace chip cancels inside
the window and spends nothing (its badge counts the window down,
truly); the valve chip opens the disarm face and its badge is the
ledger's live remainder (Una A-09/A-11 family, batch-8 read at
TactileDeckScreen.kt:940–975). **KEEP**, all four.

## 4 · THE AIRLOCK — the Held Door's sentences

The Held Door Truth Baseline binds this face (Ren's letter,
Oct 29 ~21:20): remaining time is stated by the door; a withheld
peek prints its state AND its reason; the offer appears only where
a lease can be honoured; counts print the honest split (W-23).
Freya's spec assigns the SIL-2/SIL-3 sentences themselves to this
car (PILL-AND-TIMES §4.3) — they are ruled here, values from the
store, never literals (the value-slot law).

**W-28 · The door's missing line (SIL-1) → ADD.** Today the
intercept never states how long the session still runs (Una
SIL-1): minute 40 and minute 2 read the same face. **The line,
printed on the airlock under the app name:**
- timed session: **"SESSION ENDS {HH:MM}"** — the span end, the
  Now Frame derivation, the same instant the Deck's TIME cell
  prints (one fact, one rendering — `endsAtText` already exists
  for exactly this text);
- stopwatch session: **"SESSION RUNNING · NO SET END"** —
  mirroring the Deck's NO END, so the door never invents an end
  for a session that has none.
→ Suki.

**W-29 · The peek chip: "EMERGENCY PEEK" + badge "2m" → REPLACE
the badge; ADD the price line (SIL-2).** Two defects of wording.
(1) The badge hardcodes the lease's length (`AirlockAndDisarm-
Overlays.kt`, batch-8 read: `monoBadge = "2m"`) — inside this
car the length becomes his setting, and the badge would lie at
every setting but one. **Replacement: the badge prints the set
minutes from the same GuardSettings row the lease reads —
"{peekMinutes}m"** (the app rows' "2m" tier badge at
BlockitSuitcaseAndSwitchesViews.kt:296 takes the same ruling:
the tier's name is its current length, read from the store).
(2) The offer never names its price: a taken peek arms a
20-minute per-app cooldown (Una SIL-2; derivation at
MonolithRepository.kt:1073–1079), and no sentence on the face
says so before he pays. **The price line, under the chip, in the
face's body register: "TAKING THE PEEK STARTS A {cooldown}
COOLDOWN ON THIS APP"** — {cooldown} rendered from the same
derivation ("20 MINUTES" at today's constant), never a literal.
The chip's name itself: "EMERGENCY PEEK" — the peek is not an
emergency and the face already carries a real EMERGENCY BREAK
elsewhere in the app; but the word is the shipped name of the
act on every surface, and renaming the verb mid-car would split
its references. **The name is KEPT, the badge and the price are
the cure.** → Suki.

**W-30 · The withheld peek (SIL-3) → ADD the reason line.** Today,
when the cooldown runs, the chip simply never appears — silence
indistinguishable from an unpeekable app (Una SIL-3). **The
line, standing where the chip would stand, whenever the offer is
withheld for cooldown on this app: "PEEK COOLDOWN — AGAIN IN
{n} MIN"** — state and remaining, from the existing derivation
(`peekCooldownRemainingSeconds`), ceiling-rounded like the
break eyebrow so it never understates the wait. Second state,
if the face distinguishes it: when peekMinutes is 0 the offer is
withheld by his own setting, and the line is **"PEEK IS OFF"** —
no verb promised, none implied. On the doors where no lease can
be honoured at all (UNA2-01's guard/site/sniper doors after
cure), NO peek line prints — an absent offer on a door that
never had one is not a withholding, and printing a reason there
would advertise a door that doesn't exist. → Suki.

**W-31 · "ACTIVE INTENTION: {text}" → REPLACE the condition,
keep the words.** Fact: the line prints its label over a blank
when no intention was declared (Una UNA2-07 — both sources can
be empty). A label over a blank is a hollow claim on the face
whose job is to remind him what he declared. **Ruling: the line
renders only when the intention is non-blank** (the imprint's
empty-box discipline — absence, not a placeholder word). When it
prints, the words are exact. → Suki.

**W-32 · The badges and the big name; "RETURN TO FOCUS".** Facts:
Una A-07 (path badges name the mechanism that fired; the tier
path prints the app's own badge; the big name is the rule's app
or the matched domain) and A-08 (BACK is performed before the
intercept launches; the chip books the honest outcome "Stayed
Focused"). The breathing-delay numeral (05 → 00) is a countdown
of the delay wall and claims nothing beyond it. **KEEP.**

## 5 · THE DISARM FACE

**W-33 · The spent face: "This week's break is already spent.
Sessions end when their time does." → REPLACE.** Fact: Una
UNA2-05 — the second sentence is false on its plain reading
twice over (he finishes sessions early from the Deck as the
core verb; the grace window cancels an accidental start without
spending). **Replacement: "This week's break is already
spent."** — the whole truth of the face, one clause. The rest
was rhetoric standing where a fact is expected. → Suki.

**W-34 · The available face + the valve's rule line.**
"This ends the session early. It is the only break this week."
and "Hold to fill 60 seconds. Every 15 seconds banks. If your
hand slips, what isn't banked drains away. The banked part
stays." Facts: Una A-09 — the count derives live from the spend
ledger for as long as the overlay stands; the required seconds
are the constant 60; the banking description matches the valve's
mechanics clause for clause. The title "EMERGENCY BREAK" and
the chip "KEEP FOCUSING" do what they say. **KEEP.**

## 6 · THE DESK CLOCK

**W-35 · "NIGHTSTAND DESK CLOCK • TAP ANYWHERE TO EXIT".** Fact:
Una A-12 (deck audit) — the full-screen box is clickable to
dismiss; the exit verb is real. **KEEP.**

**W-36 · The banner's default name: "Deep Focus Session" →
REPLACE (kill the default).** The banner prints
`activeTask?.title ?: (activeSession?.intention ?: "Deep Focus
Session")` (`AirlockAndDisarmOverlays.kt`, batch-8 read) — with
no task and no declared intention, the clock invents a session
name and prints it as a fact on the surface he reads at night
(Una, appendix sighting 1; the Flow Register's §2 rules the same
death). **Ruling: the banner renders only when a task title or a
declared intention exists; when neither exists the banner does
not render.** No replacement name is supplied because no true
name exists — a session he didn't name has no name, and the
clock's other prints (the span, the strip) already say what is
running. → Rurik (appendix cures ride his list in this car).
The Flow build inherits a dead default, not a live one.

**W-37 · The idle numerals → FLAG.** With no session, the clock
prints the DIAL SETTING formatted as a time (batch-8 read:
`displaySecs` else-branch = `dialMinutes * 60`) — a setting
wearing a clock's face (Tief's ST-01; Ablauf's audit confirms it
alive). No cure in this car owns the clock face. **The car that
could:** the blocking-start flow build (FLOW-REGISTER §2: the
idle lie dies; current time joins the face). **The print rule,
supplied now so it lands true:** idle, the numerals print the
actual time and nothing else stands on the face to confuse the
reading; running, the numerals are the span's remaining and the
face carries the session's own prints (W-28's ENDS line is the
door's version of the same discipline). One numeral pair, two
facts — the states must never share an unlabelled face.

## 7 · THE DIGEST

**W-38 · The digest's sentences.** "HELD · AS OF {HH:MM}" /
"Everything here is as of the time above. Nothing here opens.
Anything newer waits until the session ends." / "Nothing has
been held." / "Zero notifications intercepted during focus." /
"HELD NOTIFICATIONS • {n}" / CLEAR ALL / CLOSE DIGEST / DEL.
Facts: Una A-10 — kept by construction (frozen rows carry no
affordance, DEL exists only in live mode, newer captures land
in Room not the snapshot, the live count is the list's size, the
listener captures only inside active sessions). Batch-8 read
confirms every sentence verbatim. **KEEP**, all of them.

**W-39 · The listener's invented body: "Suppressed notification
during study session" → REPLACE (store no body).** Fact: a
capture whose text is empty is stored with that sentence as its
body (`MonolithNotificationListener.kt:82`, batch-8 read:
`text.ifEmpty { "Suppressed notification during study
session" }`), and the digest prints it where the notification's
own words belong — invented text in a record's clothing (Una,
appendix sighting 2). The notification said nothing; the record
must say nothing. **Ruling: a textless capture stores an empty
body and the row prints its app, sender and time with an empty
body slot** (W-38's frozen/live rows already stand on those
three). No marker word is supplied — "(NO TEXT)" would still be
a word the notification never said, printed in its place; the
empty slot is the true print. Existing stored rows carrying the
invented sentence are records already written; the cure is
prospective, and back-filling them would be a second invention.
→ Rurik (appendix cures).

## 8 · THE RECEIPT & THE DRAWER — this car's prints

**W-40 · The slip's standing lines (post-Car-2).** "ARMED
{HH:MM} · PLANNED {span}" (FLOW slips omit PLANNED — RC-02),
"ENDED {HH:MM} · {outcome}", "THE SHIELD WAS OFF FOR PART OF
THAT SESSION", "SHIELD — STILL BLOCKED (SERVICE DIDN'T STOP)".
Facts: my own receipt register, re-verified in its cars (RC-01:
the ended instant is the persisted witness; RC-02: no plan is
invented for a stopwatch span; the fault line is the one lawful
receipt-only line). Batch-8 read of ReceiptSheet.kt confirms
the prints and their conditions. **KEEP** — this car's changes
to the slip are W-41, W-43 and W-44, and they are ruled so the
slip's discipline (witnessed vs reconstructed, never dressed
alike) survives them.

**W-41 · The slip's HELD line: "HELD {n} APPS · {k} SITES" →
REPLACE (the imprint's lie, in the record's ink).** Fact: the
apps count is `countHeldApps` = `resolvedTier != OPEN`
(`MonolithRepository.kt:1255–1256`) — the same LOCKED+PEEK
flattening as W-23, written into the session row at birth
(`heldAppsAtSeal`, :1318–1328) and printed on the permanent
record. The sites count is the sites list's size and is exact.
**Replacement: "HELD {n} SHUT · {m} PEEK · {k} SITES"**, same
omission law as W-23 (a zero segment does not print). **The
fact requirement, named for the builder:** the row stores one
merged count today, so the split cannot be reconstructed at
print time — the birth writer must store the peek-tier count
beside the shut count (a column on Rurik's migration spine).
If that column is refused at merge, the fallback lawful line is
"UNDER THE SEAL: {n} APPS · {k} SITES" (presence under the seal
is the only claim both tiers make true) — the choice is Ren's,
made on the record, never the merged noun by default. → Rurik.

**W-42 · "BREAK BANKED {n} MIN" — dormant print, KEEP.** Fact:
the figure is the spend ledger's held seconds FOR THIS SESSION
(`MonolithRepository.kt:1531`), and the code's own note states
the honest figure on every publishable receipt today is 0 — a
valve-broken session publishes no slip, so the line never
prints. When a banked break can lawfully close a session, the
word BANKED is the valve's own taught vocabulary (W-34) and the
figure is session-scoped; the floor division cannot print
0 MIN because a spend books only at the full 60 held (W-07).
**KEEP** — ruled now so the day it wakes, it wakes audited.

**W-43 · The slip's footer: "THE MACHINE KEEPS ITS OWN LOG ·
YOUR STUDY RECORD LIVES IN YPT" → REPLACE.** Fact: the same
sentence as the plate's W-13 in a longer coat. The machine's log
is real (the Drawer is built from it); the study RECORD does
not live in YPT — the study HOURS do (Una UNA-08's ground).
The footer was ruled "exact" once — on its line-fit, not its
truth (ReceiptSheet.kt's own comment is a measurement of
tracking and width). **Replacement: "THE MACHINE KEEPS ITS OWN
LOG · YOUR STUDY HOURS LIVE IN YPT"** — one word, shorter than
the current line, so the measured one-line fit (0.25sp tracking,
9sp) is preserved with room to spare. → Rurik. W-13 and W-43
cure together or the charge reopens on whichever survives.

**W-44 · ST-04's extension print — the ruling Rurik builds to.**
The verb is new in this car (add time to a running block;
ST-04's honesty flag: "the slip's planned-vs-lived figures must
show the extension"). The slip's discipline decides the form —
an extension is a witnessed event in the session's life, like a
deflection, and prints like one:
- **On the slip, one mono line per extension, in the ARMED
  family: "EXTENDED +{n} MIN · {HH:MM}"** — the amount in the
  long-form span dialect (W-40's `longSpan`: "7 MIN", "1H 05M"),
  the instant it was granted. A list, never a sum (the
  deflections' law): two extensions print two lines.
- **PLANNED keeps its meaning: the target as armed at the
  seal.** If the implementation grows the stored target in
  place and the slip re-reads it, PLANNED becomes a
  reconstruction wearing a witness's clothes — the RC-01 sin,
  and the exact failure this register exists to catch. The
  lived target is PLANNED + the EXTENDED lines; ENDED and the
  outcome word carry the rest. The fact requirement: the seal-
  time target must survive somewhere the slip can read (the
  stamp discipline of Rurik's own breakSeconds ruling is the
  pattern: stamp at the event, read only the stamp).
- **In the Drawer**, the stored session's day entry carries the
  same segment in the same form — "EXTENDED +{n} MIN · {HH:MM}"
  per event — because the Drawer prints stored facts verbatim
  everywhere else (its task entries: "({est}m est / {actual}m
  actual)", estimate only where given, `completedTaskSummary-
  Entry` — Car 2's discipline, not to be diluted here).
→ Rurik (his cure; the words are these).

**W-45 · The WITHIN label — cross-reference, not a ruling.**
The slip's `routineLabelWithin` line is RC-03's ground (the
label frozen on the row at issuance) and rides Voss's
routine-edit lane, where my re-verification already stands as a
seat (Ren's ruling, mail/hale 2022). Nothing in this car
touches it; filed here so the register's silence is not read
as an acquittal or an oversight.

## 9 · THE PRIMER MODAL — dead print, ruled before it wakes

**W-46 · "SYSTEM GUARD PRE-FLIGHT" / "Grant Accessibility &
Usage Access for hardware-level app interception, or arm
session immediately." → REPLACE the body sentence; the modal
itself is the appendix's to kill or keep.** Fact: the modal's
binding stays removed (Una, appendix sighting 3), so the
sentences print nowhere today — and "hardware-level" is false
(accessibility interception is not hardware-level). Dead print
with a false sentence inside does not get resurrected unaudited
(the UNA-01 pattern). **Replacement body, if the modal ever
lives again: "Grant Accessibility & Usage Access so Monolith
can stop other apps, or arm the session now."** The title may
stand; the chips ("ACCESSIBILITY" / "USAGE STATS" / "COMMIT &
ARM SESSION NOW") are verbs that do what they say. → Rurik
(appendix cures — kill or cure by name, with these words if it
lives).

## 10 · DORMANT WIRING — one note, no words

**W-47 · `onSimulateIntercept`.** The Deck accepts a simulate
parameter wired to a hardcoded fake airlock (Una, appendix
sighting 4); the screen never invokes it. It prints nothing
today, so there is no sentence to rule — filed so the wording
register's silence is not read as clearance: **if any surface
ever wires it live, every airlock ruling in §4 is void for that
surface until re-audited.** The kill is the appendix's (Rurik).

---

## Summary of dispositions

KEEP (true as cured, cure named): W-01, W-02*, W-03, W-04, W-05,
W-06, W-07, W-09*, W-12*, W-14, W-15, W-16*, W-17, W-18*, W-19*,
W-20, W-21, W-22, W-25, W-26, W-27, W-32, W-34, W-35, W-38,
W-40, W-42. (* = the KEEP lapses if its named cure or condition
fails at merge.)
REPLACE (words supplied, sent to owners): W-08 (Bodo), W-10
(Imke), W-11 (Bodo), W-13 (Bodo), W-23 (Suki), W-24 (Suki),
W-28 (Suki — ADD), W-29 (Suki), W-30 (Suki — ADD), W-31 (Suki),
W-33 (Suki), W-36 (Rurik), W-39 (Rurik), W-41 (Rurik), W-43
(Rurik), W-44 (Rurik — the print his cure must carry), W-46
(Rurik).
FLAG (no cure in this car): W-37 — the desk clock's idle
numerals; the blocking-start flow build owns the face, the
print rule is supplied.
Cross-references, not rulings: W-45 (RC-03, Voss's lane),
W-47 (dormant wiring).

— Hale, Critics (the lie register), Oct 29, 2026.
