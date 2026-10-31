# WUNDER — CRAZY IDEAS FOR #14: THE ESCORT IS A JOURNEY, THE SHEET IS ALIVE

**Seat:** Wunder, crazy-ideas (second crazy seat on this issue), flat under Ren.
**Issue:** #14 — Setup Sheet + onboarding (Empfang + Pforte), repo
`Sarthak-2007/Focus-app`, branch `agent/issue-14`.
**Ground read first:** the issue + its comments (Pforte's seating, expansion,
closing, Wave-2, Wave-2 addendum; Wirbel's crazy wave; and Rausch's two
comments — first pass + sharpened update — read in full before this file
was finalised, with collisions checked and renamed away, see below),
`AUDIT-setup-sheet.md`, `ReceptionSheet.kt` whole, Funke's
`INNOVATION-mechanisms.md`, Fibel's `DESIGN-composition.md`, Glatt's
`FINISH-park-stub.md`, Wirbel's crazy file
(`studio/plan/WIRBEL-CRAZY-IDEAS-RECEPTION.md`), and Rausch's angle as
posted on the issue.

**My angle — the two journeys nobody designed:**

Wirbel took the sheet's *face* (first-session seal, live bounce, confession
line, stub-as-Receipt-Zero, chit-as-header-ticket, per-door knocks — all his,
none repeated here). Rausch took the *ceremony* (first open as house
handover, prove-first, doors-as-rites, DONE as a tear he pulls — read in
his two comments + `studio/crazy/issue-14-rausch.md`; built past, never
repeated here — two collisions actively avoided: his THRESHOLD is the
*arrival* at first open, so my return scene is named the DOORSTEP; and
he killed auto-chaining Settings, a kill my Convoy E-4 honours — every
leg is his own tap). The cell took the sheet's *truth* (S-1, the chit,
the ledger, the handover). What is still designed by Android, not by us:

1. **THE ESCORT** — a FIX tap today is: read one line → thrown alone into a
   foreign system list at 04:00 → find Monolith → survive a warning →
   find your own way back → hope the sheet judged it. The chit *judges*
   the return. Nothing *accompanies* the going. A journey has a departure,
   a foreign country, and a homecoming. We designed one verdict and called
   the rest Android's problem.
2. **PARK'S REPRINT** — the sheet's second life is a photocopy: same
   document, live numbers. A living object *changes between visits* in
   ways derived from real facts — it remembers where he left off, reports
   what moved while he was away, and its doors carry biographies. PARK
   re-opens this sheet *forever*; forever deserves more than a reprint.

**Laws I keep:** fonts Sora / Atkinson Hyperlegible / Anonymous Pro · never
green · no AI-generated images · DONE live at any door state, no idea gates
any exit · blocker, not tracker (no scores, no visit counts, no grades) ·
**an escort line may never lie about its destination** — every direction
below names the real list, the real tap, the real way back, and where a
fact varies by phone, the line *says it varies* instead of pretending.

---

## PART ONE — THE ESCORTS, AS A DESIGNED JOURNEY

### E-1 · THE REHEARSAL — he makes the Settings tap once, here, before he leaves

**Crazy:** Every escort line *describes* the foreign country. Let him
*rehearse* it. On FIX press-and-hold (or a `REHEARSE` word on the card),
the card flips to a three-beat strip drawn in the sheet's own hand —
schematic, ruled, print-styled, NOT a fake screenshot:

```
BEAT 1 — A LIST OF APPS          [ ruled schematic: rows, one row inked ]
BEAT 2 — YOU TAP MONOLITH        [ the inked row, the switch beside it ]
BEAT 3 — ANDROID WARNS. THE WARNING IS THE DOOR WORKING.  [ Accessibility only ]
THEN: BACK, UNTIL YOU ARE HERE AGAIN.
```

He performs the shape once with his thumb in the safe house; Settings
becomes *recognition*, not search. The last beat is the return — the
escort is the first surface in the app that rehearses *coming back*,
not just going.
**Mechanism:** pure Compose schematic in `ReceptionDoorCard` (hand-built
rows/hairlines, existing type ladder — no images at all), content =
a pure function `escortRehearsal(item)` beside `receptionDoorPrepare`,
its beats derived from the SAME facts the escort line prints (list kind,
verb, warning-whole). Beat 3 exists only where the full escort carries a
warning. The strip prints one honesty clause in badge type:
`YOUR PHONE'S LIST MAY BE WORDED DIFFERENTLY. THE SHAPE IS THIS.`
— because OEM lists differ and pretending otherwise is a destination lie.
**Cost:** SMALL–MEDIUM (one composable + one pure function + tests for
warning-wholeness parity with the full escort).

### E-2 · THE TRAVELLING STRIP — the excursion has a witness while he is gone

**Crazy:** Funke's N-3 prints `· OUT: <DOOR>` in the Guard line — inside
the app, where he isn't. Take the witness *with* him: the moment a chit
is written DEPARTED, Monolith posts ONE silent, ongoing notification:

```
MONOLITH — OUT: ACCESSIBILITY · LEFT IT NOT SET · TAP TO COME BACK
```

It claims only the departure fact (door + print at departure — both
already on the chit). It never claims the door's current state, because
while he is in Settings Monolith has not lawfully measured it — a live
claim there would be a lie. Tapping it returns him straight into
Monolith (the return he otherwise has to find himself). The chit's
resolution (or spending, or the 24 h DARK law) cancels it. If the phone
denies notification permission, it silently never exists — no nag, no
substitute claim.
**Mechanism:** host-side: `writeDepartureChit` → post ongoing
notification (existing notification machinery/channels, no sound, no
heads-up); `resolveChit`/`spendChit`/expiry → cancel by fixed id.
PendingIntent = the app's own launch intent. One id, one excursion —
the latest departure overwrites, exactly like the chit itself.
**Cost:** SMALL. This turns "come back" from an instruction he must
remember into a door that travels with him.

### E-3 · THE DOORSTEP — the homecoming is a scene, not a scroll position

**Crazy:** Today a returning man lands back on a ~1,900 dp sheet and the
sheet politely scrolls his card into view. Invert it: on a judged
return, the sheet does not open at all at first — a **doorstep** does.
One screen, almost empty: the visited door's name in the nameplate
position, its verdict in ink, its chit line, and — if a next door
stands — that door's FIX key, plus one key: `TO THE SHEET →`.
The homecoming gets its own room, the size of the moment: he left for
one door; one door meets him. The full sheet is one tap behind the
doorstep, never lost, never forced.
**Mechanism:** host state: when a chit resolves to a RETURNED_* state
and the sheet is next shown, show `ReceptionDoorstep` (one composable
reusing `chitLineFor`, the door word, and the existing FIX path for
the next door) before the sheet column; `TO THE SHEET` (and DONE's
spend law) dismiss it for that chit. SPENT chits never raise it.
Unjudged/DARK returns print their own honest doorstep lines
(`RETURN NOT READ` — the doorstep says it couldn't see, in the open,
instead of burying it in a card).
**Cost:** SMALL–MEDIUM (one composable + host gating; all facts already
exist on the chit). Distinct from Wirbel's header ticket: his is a line
*on* the sheet; this is a room *before* it.

### E-4 · THE CONVOY — four errands become one expedition with legs

**Crazy:** Nobody walks to the same foreign country four times if the
road connects. On the doorstep (E-3), a repaired door's
`NEXT — <DOOR>` stops being a suffix and becomes the next leg's FIX
key: tap it and he departs again *from the doorstep*, new chit, new
escort line printed on the doorstep itself before he leaves (full
escort, warning whole — the doorstep is a departure lounge, it owes
him the whole briefing every leg). A man with four unset doors can
walk all four in one continuous expedition — depart, return, doorstep,
depart — and only meet the full sheet when he chooses `TO THE SHEET`.
The sheet stops being a queue of errands and becomes a base camp.
**Mechanism:** doorstep's next-door key calls the existing
`onDeparture(next, print)` + `onFixClick(next)` pair (the exact FIX
commit path, chit first, intent second); the doorstep renders
`receptionDoorPrepare(next)` while the chit stands DEPARTED-for-next
(i.e., during the breath before the pane takes him — and if the intent
fails to fire, the doorstep is where he still stands, briefed).
Legs are never chained automatically — each departure is his tap.
**Cost:** FREE–SMALL on top of E-3 (no new state; the chit's
`nextDoor` already computes the leg).

### E-5 · THE GUIDE DOG COMES HOME BY ANY DOOR — cold-start returns land on the errand

**Crazy:** He does not always come back through Back. He comes back
through the launcher, through recents, after the process died in
Settings — and cold start today opens the sheet at its *top*, a man
re-entering his own house through the front door when he left by the
side gate for one specific room. Law: a cold start (or any sheet
showing) with an unspent, unresolved-or-just-judged chit opens the
sheet *already standing at the visited card* — his errand is the
sheet's first sight, the identity movement above it folded to its
hairline (he has read it; it is day-one furniture now), unfolding on
scroll-up. Process death stops being an amnesia the design apologises
for and becomes a door the design *expects*.
**Mechanism:** the sheet already owns `BringIntoViewRequester`s and a
chit auto-scroll effect keyed on `settled`; extend its trigger: on
first composition with a chit whose door != null and state != SPENT,
request bring-into-view for that door before the first frame settles,
and initialise `nowMono`/scroll from the chit door instead of 0.
The chit is already persisted in SetupStore precisely so death cannot
kill it — this just lets the *sheet* honour what the store remembered.
**Cost:** FREE–SMALL (one effect condition + initial-scroll wiring;
tests: cold-start-with-DEPARTED-chit lands on the card).

### E-6 · THE WARNING, REHEARSED AT FULL SIZE — inoculation, not description

**Crazy:** The Accessibility warning is the moment escorts lose people:
Android's strongest words, at 04:00, alone. The escort names it in one
clause. Go all the way: the Accessibility card's prepare grows a
second, full-width block — set in the sheet's ink, the size of a key —
that prints the warning's *shape* before Android prints its words:

```
ANDROID WILL NOW SPEAK IN ITS STRONGEST WORDS ABOUT LETTING AN APP
CONTROL THIS PHONE. EVERY BLOCKING APP HEARS THERE. MONOLITH ASKS
FOR NOTHING THERE EXCEPT THE SWITCH BESIDE ITS OWN NAME.
```

Every clause is a checkable fact about that Settings screen (it is
the standard accessibility grant warning; the only act available there
is the toggle). Where a phone inserts a timed wait on that screen,
the block adds, in badge type: `SOME PHONES MAKE YOU WAIT ON THAT
SCREEN. THE WAIT IS ANDROID'S. IT ENDS.` — hedged because it varies,
printed because the wait is the second place people abandon the door
believing something broke.
**Mechanism:** one more conditional block in the Accessibility card
(print == MISSING only), words beside `receptionDoorPrepare` so Stachel's
census owns them like every other line; nothing else moves.
**Cost:** FREE (words + one block) — priced with an on-device check
across two OEMs before ship, because the honesty law prices
unverified Settings claims as debts (the S-5 precedent).

---

## PART TWO — PARK FOREVER: THE SHEET AS A LIVING OBJECT

### P-1 · DOOR BIOGRAPHIES — the reprint's cards remember their own lives

**Crazy:** Day forty's sheet currently differs from day one's only in
its live words. Give every door a **biography word**, derived whole
from the Setup Ledger's three masks + the current print — four honest
lives a door can have lived:

| Life | Derivation (stamp bit / ever-set bit / current print) | Prints as |
|---|---|---|
| SET AT SETUP, NEVER LOST | stamp=1, current=GRANTED | `SET AT SETUP — STEADY` |
| SET AT SETUP, LOST, REGAINED | stamp=1, current=GRANTED, and a PARK `WAS SET — SINCE LOST` interval is impossible to prove from masks alone → see kill note | — |
| LEFT AT SETUP, SET SINCE | stamp=0, everSet=1, current=GRANTED | `LEFT AT SETUP — SET SINCE` |
| SET AT SETUP, NOW LOST | stamp=1, current=MISSING | (PARK already prints this: `WAS SET AT SETUP — SINCE LOST`) |
| NEVER SET | stamp=0, everSet=0, current=MISSING | `NOT YET SET` |

I kill my own second row in the table: masks cannot prove a
lost-and-regained interval, so the sheet must not print it. What
survives is a reprint whose granted doors are no longer identical
lines — one is *steady*, one *arrived late* — and both claims are
mask-arithmetic, reprovable by any test. The sheet ages truthfully
or not at all.
**Mechanism:** one pure function `doorBiography(stampMask, everSetMask,
item, print): String?` beside `doorsMaskAt`; printed in badge type
under the collapsed granted line (F-6's 37 dp grows by one 12 dp line
for stamped sheets only) and on PARK's board rows where the ledger
sub-line seat already exists.
**Cost:** FREE–SMALL (pure function + two print seats + truth-table
test over the mask combinations).

### P-2 · SINCE YOU LAST LOOKED — PARK keeps the sheet warm between visits

**Crazy:** A living object notices when you've been away. When the
sheet closes (any exit — DONE, LEAVE, the Shield excursion), it
leaves a **bookmark snapshot**: the four prints as they stood at
closing, in SetupStore. Next time PARK opens, its stub row compares
bookmark vs now and — only if a door actually moved — the stub
prints one diff line above the row:

```
SINCE YOU LAST LOOKED — WITNESS: SET · ANCHOR: NOT SET
```

Nothing moved → nothing prints (silence is the truthful diff).
This is not a visit count and carries no dates: it is the sheet
answering the only question a returning man actually has — *what
changed while I wasn't looking?* — from two real measurements.
PARK's stub stops being a label for a document and becomes the
document's pulse: most days still, alarming exactly when it should be.
**Mechanism:** `SetupStore` gains `bookmark_mask` + `bookmark_prints`
(one TEXT of four PermPrint names), written in ONE commit at sheet
close (host already owns every close path); PARK computes
`bookmarkDiff(bookmark, snapshot, nowMono)` pure, prints in the
stub's existing sub-line seat. STALE transitions print as
`PROOF AGED` — a state change, honestly named, never as loss.
**Cost:** SMALL (one store write + one pure function + one print seat).

### P-3 · THE DOG-EAR — the sheet re-opens where his hand left it

**Crazy:** Books remember. At sheet close, SetupStore keeps the door
he last touched (last FIX/CHECK tap, or the chit door if one stands)
as the sheet's **dog-ear**. Every PARK reprint opens already standing
at that door (bring-into-view on first settle, the E-5 machinery),
wearing one badge line: `YOU LEFT OFF HERE.` A reprint is then a
*resumption*, not a restart — the difference between a form and a
workbench. The dog-ear moves only on his hand's acts, never on
sweeps; a spent chit clears it (the errand is done; the next opening
owes him the top of the sheet again).
**Mechanism:** `SetupStore.lastSheetDoor` written on FIX/CHECK press
(existing press callbacks) and cleared by `spendChit`; reprint's
initial bring-into-view reads it exactly like E-5's chit path —
E-5 and P-3 are one mechanism with two triggers (an open errand,
or a marked place).
**Cost:** FREE–SMALL on top of E-5.

### P-4 · THE STUB IS A HANDLE — pull the sheet out of PARK with your thumb

**Crazy:** PARK's stub row today is a tap target that teleports him
into a sheet that rises by the drawer's law. Make the row a physical
handle: press and drag it upward and the sheet *follows the finger*
out of PARK — the drawer's own fraction driven by his hand, seating
with KIN physics when released past half, thrown back under half.
On a machine whose whole soul is tactile (slide-to-latch, the tear,
the dip), the most-repeated journey in the app's life — PARK → sheet,
forever — should be the most physical one. A tap still works (access
law: drag is an addition, never the only door).
**Mechanism:** `Modifier.draggable`/pointer-input on the PARK stub
row driving the sheet host's existing `fraction` Animatable
(sheet already exposes exactly two rest heights and a fraction);
release → `animateTo` nearest rest with `MonolithKin` seat/throw.
Host hoists the fraction from sheet-local to host state — the one
real refactor here.
**Cost:** MEDIUM (fraction hoist + gesture wiring + a feel pass —
Haut's seat — to price the throw threshold). The only MEDIUM in
Part Two, and it buys the forever-journey's feel outright.

### P-5 · THE SHEET LEAVES ITS TOTAL ON PARK'S FACE — a voice, not a row

**Crazy:** PARK's board prints door *words*; the sheet's total prints
door *prices*. While any door lacks (MISSING or STALE), PARK's stub
block grows the sheet's own total sentence — `receptionTotal`'s
sentence, verbatim, in Body dress — under the stub row:

```
WITHOUT DEVICE ADMIN, MONOLITH CAN BE UNINSTALLED.
```

The man who never re-opens the sheet still lives under its voice:
PARK stops merely *listing* his doors and starts *pricing* them, in
the introduction's own audited words, derived from the same snapshot
by the same pure function (one fact, one print — the sheet and PARK
can never disagree, because there is only one derivation). At 4/4
fresh the sentence is `ALL FOUR DOORS SET.` for exactly one PARK
opening after the last repair, then silence — good news is printed
once, bad news stands until it's false. (The "once" is the chit's
spend law reused: the stub borrows the unspent-REPAIRED print.)
**Mechanism:** `ParkDrawer` gains the total sentence from the host's
existing snapshot (no new state except the once-print, which rides
the chit); print seat under the stub row, killed the moment prints
change the sentence.
**Cost:** FREE–SMALL. Risk priced honestly: PARK gains a prose block;
if Zoll's geometry says the drawer can't hold it at 4-lack, the
sentence caps at the first lacking door + `· AND <n> MORE, PRICED
ON THE SHEET` — a truncation that names itself, never a silent cut.

---

## KILLED BY MY OWN HAND (priced, rejected, on record)

- **A FLOATING GUIDE OVER SETTINGS** (overlay arrow pointing at the
  Monolith row while he's in the list) — the dream escort, and a lie
  machine: overlay-over-Settings is OEM-fragile, the arrow cannot
  know which row is Monolith on an unscrolled list, and a blocker
  drawing over the system's own grant screen *looks like the attack
  the warning describes*. Killed on the honesty law.
- **COUNTING HIS PARK VISITS / SHEET AGE IN DAYS on the reprint**
  ("DAY 40 OF THE SHEET") — tracker grammar wearing a keepsake's
  coat; Funke's ledger kills (no nostalgia dates, no visit counts)
  bind me too. The sheet ages by *door biographies* (P-1), never by
  a day counter.
- **AUTO-CHAINED EXCURSIONS** (E-4 without his tap per leg) — an app
  that fires Settings intents in sequence is an app performing his
  consent. Every leg is his tap or it doesn't happen.

## MY PICK, IF ONLY THREE SHIP

**E-2 THE TRAVELLING STRIP** (the escort finally accompanies him —
SMALL, and it changes what a FIX *is*) · **P-1 DOOR BIOGRAPHIES**
(the reprint becomes a life, for the price of one pure function) ·
**E-4 THE CONVOY on E-3's DOORSTEP** (four errands become one
expedition; the homecoming becomes a scene). Then P-2, because once
the sheet has a pulse, PARK is never a drawer again.

What I want next, unassigned: the same journey treatment for the
*Shield excursion* (OPEN THE LIST already has H-2's way-back line —
it deserves a chit of its own), and a stranger-walk of the doorstep
at 04:00 before anyone prices E-3 SMALL with a straight face.

— Wunder (crazy-ideas, issue #14)
