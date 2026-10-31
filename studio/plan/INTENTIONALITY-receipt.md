# INTENTIONALITY AUDIT — THE FINISH RECEIPT
## Every printed element must state its reason, or go.
### Intentionality Designer (Anchor seat I-4, issue #10)

**Scope.** The finish receipt slip as it stands on `agent/issue-10`
(HEAD `838af23` + NACHDRUCK's uncommitted rounds 1–5 in the working
tree): `ui/components/ReceiptSheet.kt`, `data/ReceiptState.kt`, and
the assembly in `data/MonolithRepository.kt`
(`assemblePendingReceipt`, `receiptOutcomeWord`,
`routineWindowLabelAt`).

**Relation to the existing audits.** Zettel's audit
(`AUDIT-receipt.md`) and Hale's lie-register (`RECEIPT-REVIEW.md`)
ask: *is it true?* This audit asks the next question: *granted it is
true, why is it here?* A line can pass every truth test and still be
an accident — inherited from the digest sheet, carried because the
state happened to hold it, printed because nobody asked it to
justify itself. Sarthak's design charter (Oct 29) is explicit:
EVERYTHING INTENTIONAL, no defaults, no accidents. The receipt is
also the one surface he has explicitly ❤️'d — so this audit protects
its character ferociously and charges rent to everything riding on
it.

**The test.** Every element — printed line, glyph, rule, ink,
motion, and every field the contract carries — must answer three
questions:

1. **Job** — what does the reader learn or feel from this that no
   other element on the slip gives him?
2. **Cost** — what does it displace, dilute, or imply falsely by
   being here?
3. **Removal** — if it vanished tonight, what exactly breaks?

An element that cannot answer Q1, or whose Q3 answer is "nothing",
is flagged REMOVE. An element with a real job done in the wrong
form is REDESIGN. Everything else is KEEP, with its earned reason
written down so a future seat cannot re-litigate it from scratch —
and cannot keep it from habit either.

Verdict counts: **13 KEEP · 5 REDESIGN · 4 REMOVE · 2 CONDITIONAL.**

---

## THE HEAD

### 1. Title — his intention (Atkinson Bold 15sp) — **KEEP**
*Reason it earns its ink:* it is the only line on the slip in his
own words and the only one set in the human typeface. A receipt
that opened with a machine category ("SESSION COMPLETE") would be
a report; this opens with what he declared he was doing, which is
what makes the slip *his* document and the rest of it testimony
about that declaration. Removal test: without it the slip is
anonymous telemetry. The faulted branch's suppression (F-W1) is a
law question already parked at B3 — no new charge here; the
collapsed faulted row (A9, built) correctly stops the № floating
over a title-height void.

### 2. № (zero-padded, `№ 048`) — **KEEP, FLAGGED (CONDITIONAL)**
*Reason it earns its ink:* ritual and continuity — a numbered slip
says finishes are a series, not incidents, and the number is part
of the printed character he loved. That is a real reason and this
audit does not pretend otherwise.
*The flag, in the open:* the № currently **addresses nothing**. The
store is one slot (№ + pointer + outcome word, ReceiptState.kt);
no surface can summon slip № N; yesterday's № is unrecoverable by
design (Hale O-4). A number's ordinary promise is retrievability,
and every slip silently makes that promise and breaks it. Two
honest futures exist and only two: (a) the Slip Book (parked, B2)
gives the № its address and the promise becomes true; (b) if the
book is never ruled in, the № stays — but as admitted ritual, and
no copy anywhere may imply it can be looked up. What may not
happen is the present third state: reference-shaped decoration
nobody has admitted to. **Condition: this flag clears the day B2
is ruled, either way.**

### 3. ARMED HH:mm (+ date on cross-midnight spans) — **KEEP**
*Reason:* the witnessed start — the seal instant the HMAC itself
verifies (Hale O-5), not the tap. It is the slip's opening fact and
the anchor ENDED and RAN are measured against. The A11 date cure
is intentionality-correct in form: the date prints only when the
span crosses midnight, i.e. only when its absence would mislead —
information appearing exactly when it starts earning. Same-day
slips stay clean.

### 4. PLANNED {longSpan} — **KEEP (timed/routine); omission on FLOW — KEEP**
*Reason:* PLANNED is the contract; RAN is the performance. The
pair is the slip's whole narrative tension in two figures, and it
is the only place the machine states what was *asked* alongside
what *happened*. On FLOW slips the segment is omitted (RC-02 cure)
— correct, because a stopwatch has no contract and printing the
floor as one was the one dishonest figure the old slip carried.
*Residual note, no charge:* assembly still sources PLANNED from
`row.spanTargetSeconds` at assembly time (Repository, plannedSeconds
assignment); the audit's item-4 seal-time source
(`sealedSpanTargetSeconds`, dependency C3) remains the priced cure
so slip and Deck can never disagree after a tamper penalty. The
intentionality verdict rides on that dependency landing.

---

## THE BODY

### 5. ENDED HH:mm — **KEEP**
*Reason:* the witnessed end, stamped at the ending write
(`endWallMillis`, RC-01 cure) — the slip's most trustworthy line
(Zettel: "the slip's best line"). It is also the line the outcome
segment hangs from, which is why the tautology kill (next item)
matters: ENDED must never be made to repeat itself.

### 6. The outcome segment — **split verdict**
The slot's law after wave-2: it prints only when it adds a fact.
Audited word by word against that law:

- **(plain ENDED — suppressed) — REMOVE confirmed.** It repeated
  the line it stood on. Correctly dead at the sheet.
- **`ENDED · SHIELD GAP` — suppressed — REMOVE confirmed.** The
  gap's one carrier is item 12 below. Two dialects for one fact
  is how a ledger starts lying by emphasis.
- **`STOOD DOWN` (was SLEPT THROUGH) — KEEP.** It adds the one
  fact ENDED cannot say: the machine ended this, by its own
  stand-down, not the man failing anything. The old word blamed
  him in plain English for the machine's act; the new word is the
  machine owning its act. That is a reason.
- **`ATTENDED` — KEEP.** On a routine slip it is the judgement
  itself — the window was attended — a fact neither ENDED nor RAN
  carries.
- **`PARTIAL` — REDESIGN (charge stands, cure parked at B5).**
  A grade with its evidence redacted fails the purpose test even
  where it passes the truth test: the reader is told *that* he
  partially attended and is refused the *how much* the machine
  plainly computed at judgement and discarded. A verdict without
  its figure is the one rhetorical shape a receipt must never
  take — it asks to be trusted. It prints as
  `PARTIAL · {attended} OF {window}` (long form) or it does not
  print. Until B5 lands, the interim state is a known, named
  debt — not an accident.
- **`FLOW ENDED` — REMOVE (new flag).** Apply the slot's own law:
  what fact does it add? ENDED already states the ending; the
  absent PLANNED already marks this as a FLOW slip; RAN now
  prints the duration, which is FLOW's whole point. The segment
  survives only as a kind-label, and kind is not a fact the reader
  needs at the ending line — he knows he ran a stopwatch. Removal
  test: nothing breaks; the ENDED line gets quieter and RAN gets
  louder, both of which serve the slip. If a future seat wants the
  kind named, the head is where kinds belong, not the outcome slot.

### 7. RAN {longSpan} — the total — **KEEP the line; REDESIGN its arithmetic (headline flag)**
*Reason the line earns its ink:* it is the answer to the only
question every finish asks — *how long did I actually go?* — and
for two waves it was computed, carried through every joint of the
state, and printed nowhere (audit item 5). Its placement (under
ENDED, over a rule, struck Bold 11sp per Finisher 2 §4.3 /
NACHDRUCK round 1) is right: a total must look like a total and
must not borrow the interruptions' 13sp.
*The redesign:* `longSpan` **ceilings** — `(seconds + 59) / 60`.
On every other line this is inert, because every other input is
minute-quantised at its writers (dial minutes, window minutes,
the 1200s floor). `sealedElapsedSeconds` is the exception: it is
real, unquantised elapsed seconds. So the slip's one total is the
one figure that can overstate the lived span by up to 59 seconds
(61 seconds lived prints `RAN 2 MIN`). A total's entire reason to
exist is exactness; a total that rounds *up* is flattery in the
machine's own hand, on the surface whose law is truth before
decoration. **Cure: the total floors, or prints its true span —
it never ceilings.** (This resolves the receipt cell's Ruling-2
open question: whatever dialect the *plan* figures speak, a
*total* is not a dialect choice.) Price: one formatter call at
one call site; `longSpan` elsewhere is untouched.

### 8. WITHIN {routine} · {window} — **KEEP on routine rows; REMOVE on hand-armed rows (new flag)**
*Reason on routine rows:* identity — the slip belongs to that
window; the label is frozen from the row's own stamps and cannot
drift. It earns.
*Charge on hand-armed rows:* the line locates a hand-made act
inside a routine he did not arm under — it answers a question
nobody asked, in the machine's filing language, on a document
whose head is his own intention. Worse, its source
(`routineWindowLabelAt`) reads the **live** routine table at
assembly and again at rehydration: rename or retime a routine and
a standing slip's WITHIN line rewrites itself (RC-03's open half;
the RC-03 freeze is dependency C2 and has not landed for this
branch). A line that is both purposeless *and* unstable has no
defence. Removal test on hand-armed slips: nothing breaks — ARMED
already says when he started, and his intention already says why.
If the freeze lands first, this verdict softens to STATE-ONLY;
it never softens to KEEP-as-printed.

### 9. HELD line — **omitted today: KEEP THE OMISSION; the returning line is CONDITIONAL**
The old summed line (`HELD n APPS · m SITES`) booked PEEK-tier
apps as held (`resolvedTier != OPEN`) and counted sites as a blind
list size — wrong in both directions (W-41, audit item 9). It is
dead as printed and the sheet now prints only
`ReceiptHeldFigures.printLine()` — which nothing fills, so the
line is absent. **The absence is the intentional state, not a
gap:** the slip's omission law (an absent line over a false one)
is itself one of the loved character's load-bearing rules.
*Conditions on its return, stated now so the return cannot smuggle
the old line back:* (i) figures stamped at seal from enforcement
state — shut and peek counted separately, sites from what the
enforcer actually holds, never a list size (the cell's dependency
flag, incl. the birth-vs-seal warning, is adopted whole);
(ii) grammar is the in-tree split dialect —
`{shut} HELD · {peek} PEEK · {sites} SITES`, zero segments omitted
(cell Ruling R4); (iii) the word HELD binds the shut count only,
forever. A HELD line that meets those three has a reason: it is
the enforcement's own testimony. One that meets fewer has none.

### 10. BREAK BANKED — **REMOVE confirmed; and remove its residue**
Killed as printed in wave-2 and correctly: its one live path
booked a 60-second valve-break hold *price* as "banked" minutes —
a semantic falsehood in a banking metaphor the slip never needed.
*New, smaller flag:* `bankedBreakSeconds` is still computed at
every assembly (spend-ledger sum), carried through
PendingReceipt and ReceiptState, and read by no surface. The
repository comment calls it "named truth, not a placeholder" —
but the session row and the spend ledger already *are* the record;
the receipt contract carrying a second, unread copy is weight
without a reader. **When B-tier work next touches the contract,
strike the field or name its reader.** Intentionality applies to
state, not only to print: fields are printed lines that haven't
happened yet. (Same disposition, same reasoning, for the summed
`heldAppsAtSeal` / `heldSitesAtSeal` copies on ReceiptState: the
row holds them; the state should not duplicate them STATE-ONLY
forever — either the W-41 fill point consumes split figures only,
or the copies go.)

---

## THE DEFLECTIONS

### 11. Deflection rows (`HH:mm · {name}`, one ink, 12dp hang) — **KEEP the section; every cure so far earns**
*Reason the section earns its ink:* it is the session's only
narrative — the witnessed list of reaches the machine turned
away. Nothing else on the slip says what the session *cost* in
temptation, and at 1 AM that list, not the total, is what he
re-reads. Listed, never summed: the absence of a count is itself
intentional — a count would turn testimony into a score, and this
app does not grade him. The A4 outcome filter (blocked outcomes
only; peeks never print and never eat a dedupe slot) is the
section's truth floor and is acquitted here in purpose terms too:
a let-in printed as a block would not merely be false, it would
make the section's whole reason — witness — worthless.
The one-ink cure (Druck P-6) earns: dimming the list made the
temptations a lesser class of fact on his own receipt.
The `· SITE` suffix (NACHDRUCK round 4) earns: a reach at a site
and a reach at an app are different acts, distinguished by a word
in the same ink — no colour, no icon, no second class.
*Open meaning, forwarded, not charged:* the Critic's N3 stands
above this section — the list is a list of *resolutions*, not
attempts. If Ren rules on what the list *means*, this section's
heading-less form may need one word of framing. Until then the
rows are true and their reason holds.

### 12. QUIET span rules (`{span} QUIET` between entries ≥20 min apart) — **CONDITIONAL KEEP (new flag)**
NACHDRUCK round 3 added the slip's first *derived* line: it prints
no witnessed event; it interprets the gap between two printed
ones. Its reason, stated fairly: a quiet half-hour and a siege
should not read identically on paper, and the rule gives the list
a breath shaped like the session's actual rhythm. That is a
typographic reason, and typography is a legitimate currency on
this surface.
*Conditions:* (i) it must never be mistaken for testimony — it
carries no timestamp and is counted as nothing, and it must stay
that way; the day a QUIET figure is summed, compared, or carried
into any judgement, it becomes a score wearing a rule's clothes
and this acquittal dies. (ii) Its figure speaks the total's new
dialect: gaps floor, never ceiling (it currently reuses
`longSpan` — same cure site as item 7). Under those two, it
earns its hairlines.

### 13. THE SHIELD WAS OFF FOR PART OF THAT SESSION — **KEEP, binding**
*Reason:* the slip's one interruption in the ledger's own voice —
the machine breaking its mono ledger to confess, in Anonymous Pro
Bold 13sp (the double-strike), that its guard was down. It is the
plainest line on the slip (all six audit seats) and the reason
the outcome slot no longer needs to say SHIELD GAP. Its size is
not decoration: at ledger size it would read as one more entry;
at 13sp it is an event. Removal test: without it, a gap finish is
indistinguishable from a clean one except by a suffix — the
confession is the difference between a record and a report.

---

## THE FAULTED BRANCH

### 14. SHIELD — STILL BLOCKED (SERVICE DIDN'T STOP) — **REDESIGN (wording; substance KEEP)**
*Reason the line exists:* it is the one receipt-only fact —
the finish happened but the shield did not lift — and suppressing
it would make a faulted slip indistinguishable from a clean one.
That job is real and it may live nowhere else.
*Why the form fails the test:* `(SERVICE DIDN'T STOP)` is
engineer-speak addressed to no one the slip serves — at midnight
it reads as a crash note, not a fact in the man's own situation
(Muede, Neu, audit item 14). And `STILL BLOCKED` is present tense
with no expiry: on a restored slip it re-asserts, hours later, a
state nobody has re-verified (Una). **Cure, already priced in the
audit and re-demanded here in purpose terms:** plain words for
the fault (`THE SHIELD DID NOT LIFT` class — Ren's wording call),
the retry fact printed (a retry is already scheduled at
fault + 30s; the slip that reports a fault owes the reader its
next step), the fault's wall time on restored prints, and the
corrected-duplicate stamp (`RELEASE CONFIRMED LATER`, B4, +DB v12)
when the release later confirms. A fault line that cannot say
what happens next has done half its job.

---

## THE FOOTER

### 15. THE MACHINE KEEPS ITS OWN LOG · YOUR STUDY HOURS LIVE IN YPT — **KEEP the sentence; REDESIGN its ink exception (new flag)**
*Reason the footer earns its ink:* it is the slip's colophon and
its boundary stone. Clause 1 tells him this paper is not the
record — the machine keeps its own log — which is what licenses
the slip to be brief, listed-never-summed, and dismissible without
loss. Clause 2 (as cured, W-43: HOURS, not RECORD — Monolith
itself keeps a study record; hours are what live in YPT) sends
him to the one place the hours he cares about are totalled, and
keeps a blocker from impersonating a tracker on its own paper.
It is the only line not about *this* session, and that is exactly
why it must stay last, smallest, and incapable of growth: the day
the footer gains a third clause it becomes a noticeboard.
*The ink flag:* the footer prints at 0.72 alpha — a deliberate
quiet ink — while the one-ink law killed the `dim` parameter
everywhere else, and the sheet's own comment claims the footer
"follows the slip's ink law (body ink at footer weight)". It does
not; it prints grey, and in night frame n08 the audit found that
grey near-illegible. There *is* an honest reason available —
a colophon is not testimony and may sit quieter than the ledger —
but an exception with a reason must be **named as an exception**,
in the code and on the paper's terms, or it is just the accident
the law was written against. **Cure: either print the footer in
full body ink at 9sp (size already does the quieting), or keep
0.72 and rewrite the comment to own the exception and its
reason.** The clause-boundary split guard (wave-2, fontScale-aware
after NACHDRUCK round 5) is acquitted: a footer that loses its
last words to clipping would be a false footer, and the guard is
the kind of invisible intentionality this surface runs on.

---

## THE OBJECT & THE ENDING (non-print elements, same test)

### 16. Shape, scrim, arrival, stillness — **KEEP, each with its reason on record**
- **3dp square cut, no border, shadow lift (A5):** thermal paper
  is cut, not carded. The 24dp digest-card shape was the audit's
  clearest *accident* — furniture inherited from another room.
- **Scrim 0.12:** the room stays visible behind the slip; the
  session's context is not erased by its own receipt. Continuity
  is the reason; a darker scrim would announce a modal, and this
  is a document.
- **Contact anchor (slip covers the nav band):** a printed slip
  rests on the surface; the floating sheet with the nav pill
  peeking under it (Aug's pixel charge) was neither paper nor UI.
- **Feed arrival; restored slips settled (A7):** the printer's one
  motion, paid for by the detention; a crossfade is a card
  appearing. A restored slip re-performing its arrival would be
  theatre — it renders settled, as a kept document does.
- **Stillness after arrival; no buttons, no CTA, no auto-dismiss:**
  paper does not plead to be tapped. This cluster is the loved
  character's core and is **binding** in this audit too.
- **Detention before issue (E1480–E1800):** the slip arrives only
  when the machine has proven it stopped. The wait is the
  receipt's warranty seal; removing it would make every other
  line's truth a claim instead of a proof.

### 17. Dismissal — DOWN gesture + 2s dwell; faulted slips exempt; dismissal persisted — **KEEP as built**
*Reason:* the old law (any tap swats the ending, tap's own action
swallowed) made the slip's exit an accident by construction. The
built cure gives dismissal a *gesture with intent* (DOWN, not the
UP of a stray tap completing), a dwell that lets the in-flight
tap pass to its own target, an explicit-act rule for the faulted
slip whose news must not be swattable, and persistence of the
dismissed № so a decision survives process death instead of
re-ambushing at cold start. Each clause answers a named accident;
none is decoration. The remaining keeping question — the Slip
Book / TEAR (B1/B2) — is a surface decision parked with Ren; this
audit's № flag (item 2) is the receipt-side stake in that ruling.

---

## SUMMARY — flagged by name, in the open

**REMOVE**
1. `FLOW ENDED` outcome segment — adds no fact beyond ENDED + RAN (item 6).
2. `WITHIN …` on hand-armed slips — unasked, self-rewriting (item 8).
3. Plain `ENDED` segment; `ENDED · SHIELD GAP` segment — already suppressed; removals ratified (item 6).
4. `BREAK BANKED` — already killed as printed; ratified (item 10).

**REDESIGN**
5. **RAN's arithmetic — the total never ceilings** (item 7). *Headline flag.*
6. `PARTIAL` — prints only with its evidence, `PARTIAL · {attended} OF {window}` (item 6; cure parked B5).
7. Fault line wording + retry fact + restored-ageing + corrected-duplicate stamp (item 14; cure parked B4).
8. Footer ink — full ink, or the 0.72 exception named and owned in code (item 15).
9. State residue — `bankedBreakSeconds` and the summed HELD copies on ReceiptState: strike, or name a reader (item 10).

**CONDITIONAL**
10. № — clears when the Slip Book is ruled either way; until then, reference-shaped ritual, admitted (item 2).
11. HELD line — stays omitted until seal-stamped split figures + split grammar land (item 9).
12. QUIET span rules — keep as typography only, floored figures, never summed or judged (item 12).

**KEEP, reasons on record (protected):** title · ARMED (+cross-midnight date) · PLANNED (FLOW omission incl.) · ENDED · STOOD DOWN · ATTENDED · RAN line & placement · WITHIN on routine rows · deflection section (outcome filter, one ink, `· SITE`, listed-never-summed) · THE SHIELD WAS OFF… · footer sentence & split guard · 3dp/contact/shadow · scrim 0.12 · feed arrival / restored settled · stillness, no CTA · detention · dismissal law as built · the omission law itself.

**Not charged, forwarded:** N1 (peek counts as attendance — routine-judgement surface), N2 (peek inflates the shield notification's "urges stopped" — issue #7), N3 (the deflection list's meaning), B5's attended-seconds carriage, C2/C3 dependencies — all already routed; this audit adds no duplicate filings, only the purpose-side stakes noted above.

Nothing compiled. Paper only.

— Intentionality Designer (Anchor seat I-4, issue #10)
