# AUDIT — THE HELD DOOR (the intercept / airlock face)
Coordinator: Tor · Crew: Scharte (critic), Schwelle (designer), Funke
(inventor), Halbwach (human, 1 AM), Schliff (finisher), Blank (fresh eyes)
Filed: Oct 30, 2026, ~01:35 IST · Paper only — nobody compiled (Ren's law).
Ground: merged tree builds/merge-batch9/Focus-app (shipping truth),
trunk where it differs. Every filing read whole by the coordinator;
every charge below carries its seat's number. Rulings are Tor's;
overturns of the wording book are flagged for Ren's ratification.

## HEADLINE

The door's machinery is the most honest in the app — the peek-offer
matrix (peekOfferFor + re-derivation at grant) was acquitted by all
six seats without exception. The face standing on that machinery is
not: its central sentence reads the wrong clock, its ledger books a
reason nobody gave, its biggest glyph is an uncaptioned number that
gates only surrender, and as merged the door has never run at all —
the service that fires four of its five doors does not compile.
Blank's cold answer to "door, wall, lecture, or help?" stands as
the audit's: **a wall with good manners** — a help for the man who
didn't mean it, a waiting room for the man who did. The fix list
below is the distance from wall to door.

## BASELINE SCORECARD (Held Door Truth Baseline, five laws)

1. No remaining duration; end instant only — OBEYED in form,
   VIOLATED in fact (the instant is the wrong instant: B-1/C-2).
2. Withheld peek prints state AND reason — structure OBEYED;
   timing (5 s late) and ticker (self-ticking, floored seed)
   VIOLATED (C-6/C-7/B-3/F-7).
3. Peek offered only where a lease can be honoured — OBEYED,
   fully, unanimously acquitted.
4. Doors that never owed a peek print no peek line — OBEYED.
5. Derived values, never literals — badge/price OBEYED; the
   5-second wall itself is a bare literal derived from nothing
   (Blank §4) — cured in spirit by FIX-8's taper.

## THE FIX LIST (severity order; prices are the crew's, ratified)

**FIX-1 (S1 · SMALL) — The door does not compile.** [Tor, C-1×3 seats]
MonolithAccessibilityService.kt:239 reads `leaseLive`, declared
nowhere in the tree; launchInterceptScreen (:401) requires `path`
and the guard (:208) and sniper (:220) callers pass none. Cure,
specced not typo'd: `leaseLive` is derived at the hoisted settings
read — the browser package's own app row carries a live peek lease
(activePeekUntilElapsedRealtime > now); no row → false. Guard call
passes UNINSTALL_GUARD, sniper passes SNIPER. Until Ren's compile
proves this face, every sentence of the form "the door does X"
carries the rider: as written, on paper (Halbwach H-11).

**FIX-2 (S1 · SMALL) — SESSION ENDS reads the wrong clock.**
[B-1/C-2, found independently by critic and fresh eyes]
triggerAirlockIntercept derives the end from per-BLOCK
remainingSeconds (VM :2364–2366) against the model's own span law
(MonolithModels :386–395). Multi-block: the door announces the
session over at the first block boundary. Safe Break: per-block
remaining is 0, so the sniper/site doors — which fire inside
breaks by design — print "SESSION ENDS {now}" for ten minutes
while the session runs. W-28 ordered the span end; the desk clock
in the same file got the span cure; the door did not. Cure:
spanRemainingSeconds at VM :2366 — and the Deck's TIME cell
(TactileDeckScreen :525) shares the derivation and takes the
same cure in the same pass, or the two faces disagree the day
one is fixed alone.

**FIX-3 (S1 · SMALL) — The ledger pack: stop booking what nobody
said.** [C-3/B-4/B-5/H-4, four seats, unanimous]
(a) The MUSCLE_MEMORY default dies: intercepts book reason
UNSTATED unless a man actually states one (Funke P-2's value;
Scharte/Blank's "NOT ASKED" is the same cure). REPO's own "Habit"
default (:2109) dies with it. (b) The dead wires die:
onSelectReason / onTransmuteParticleTap, the state fields behind
them, and the DistractionTriggerReason coachingTip strings —
counsel written for a picker that was never composed. The
optional reason-chip row (P-2's second half) is STRUCK for this
face: a survey at the craving second is the wrong instrument,
and the honest exit line (FIX-8) answers the man who meant it
better than a chip can. (c) RETURN's outcome books "RETURNED AT
DOOR" (Scharte's string), not "Stayed Focused" — the tap is
observed; the sequel is not. Whether he stayed focused is
bookable at session end, not at the door (referred downstream,
see Riders). (d) The blank-package fallback booking TikTok's
package (B-10) dies: blank books blank. (e) The intention
fallback to the composer's draft (C-12/B-6) dies: no armed
session in state → blank → no line (W-31's discipline).

**FIX-4 (S2 · SMALL) — Repeat intercepts can miss the door.**
[B-9, Blank alone, verified against MainActivity]
singleTop activity; onNewIntent only calls setIntent; intercept
extras are consumed once at first composition. A second intercept
against a live activity lands him on plain Monolith or on the
previous app's stale door — wrong name, wrong badge, wrong peek
state. Cure: consume the intercept extras in onNewIntent (route
through handleIncomingInterceptIntent there).

**FIX-5 (S2 · SMALL) — The cooldown truth pack.** [C-7/B-3/F-7]
Three stacked approximations, one cure family: (a) the
repository's derivation FLOORS (REPO :1180, remainingMs/1000)
while the print CEILINGS — floor-then-ceiling understates, the
one direction this face swears it never goes; repository returns
the ceiling form, matching peekSecondsRemaining. (b) The overlay
re-ticks a local counter instead of deriving from the stored
anchor (the VM's own doctrine, :180–188, obeyed by the pill,
broken by the door): the state carries the cooldown's end-mono;
the print derives at each recomposition, the ticker reduced to
a redraw trigger. (c) Expiry resolves: at derived 0 the face
re-runs the offer derivation — the peek stands there, or nothing
prints. "AGAIN IN 0 MIN" becomes unrepresentable. F-1's twin
cure applies to the 5-second countdown itself: anchor it to a
stored end-mono, never a free-running delay loop.

**FIX-6 (S2 · SMALL–MED) — The composition pack.** [Schwelle,
Schliff F-2/F-3/F-4/F-5/F-9/F-10/F-11, Halbwach H-1/H-3/H-9]
- FIXED SKELETON: nothing re-flows during a visit. The peek /
  withheld zone is composed from frame one at its full height
  (Schliff: 110dp offer branch / 34dp withheld / 0dp where
  neither is owed — verify the 110 on device against the price's
  real line count); content fades in at 180ms when it arms.
  RETURN's centre is stationary from frame one. This kills the
  audit's worst interaction defect: at countdown 0 the card grew
  ≈110dp and RETURN jumped 55dp — more than its own height —
  under a travelling thumb, onto EMERGENCY PEEK (F-2).
- SECOND 0: the withheld lines and the peek's price print from
  the moment the door opens (C-6/H-7/I-2). Only the offer chip
  waits for the wall. A known refusal is never withheld.
- THE NUMERAL DIES AS HERO (see Conflicts 1). The wall prints as
  a captioned gate line in the peek zone: "PEEK OPENS IN 0:0X"
  (Scharte's words; Anonymous Pro, statement rank) over
  Schwelle's draining 2dp rule. It names what it gates; it does
  not pretend to gate the stay (B-8).
- INTENTION: prefix "ACTIVE INTENTION:" killed; his words print
  bare, directly under the app name (Halbwach's slot), 16sp
  (Schwelle's size — one hero per face, and the name is it).
- EYEBROW: "AIRLOCK INTERCEPTION •" killed (three seats); the
  badge word alone survives, footer tier. Fallback badge word
  "Locked" → "Blocked" at all three sites (C-10).
- NAME SLOT (C-8/B-7): guard door prints what he opened
  (Settings / the installer) — "System Settings Guard" is a
  fiction in the slot that everywhere else holds a fact; the
  badge already names the guard. Site door: browser name as the
  name, matched domain as sub-line. Sniper names KEEP (the
  feed is the fact of what was stopped).
- TOUCH + INK: RETURN becomes a 56dp slab (Schwelle), its label
  in the scheme's own on-container #471602 → 5.37:1 (F-3; today
  cream on vermilion is 2.55:1, the lowest contrast on the
  face, on the one word he must hit). PEEK chip 48dp (F-4).
  Border alpha 0.2 → 0.38 (F-5). Body lines alpha 0.8 → 0.92
  (F-10). Centre alignment on wrapped lines (F-11). Name capped
  at one line with ellipsis (F-9).

**FIX-7 (S2 · SMALL) — The arrival seam.** [F-6 + Blank §4]
(a) BACK parity: the app-tier path performs GLOBAL_ACTION_BACK
before launching, like its three siblings — the blocked app
must not stand visible, reward delivered, through launch
latency. The shield path takes the same line (Blank: W-32's
acquittal premise "BACK is performed before launch" is false
on both main doors; the wording book's A-08 fact is corrected
by this fix, and Hale's book should note it). (b) Entry:
fadeIn specA at 180ms, not specB's 420ms ghost (Schwelle D-5's
substance at Schliff's number). Exit keeps specA 260ms.
(c) Arrival voice: the seal's recipe at door pitch — 300Hz,
44ms, gain 0.60, chordRatio 1.5, latch haptics kept (F-12);
today a 16ms switch tick announces the door while the goodbye
carries the visit's heaviest sound.

**FIX-8 (S3 · SMALL) — Names, prices, and the door's memory.**
- "EMERGENCY PEEK" DIES (Conflicts 4). The chip prints "PEEK"
  + its derived badge. One string in the tree (face :665).
- Price sentence re-timed (C-11/B-2): "WHEN THE PEEK ENDS, A
  {n} MINUTES COOLDOWN STARTS ON THIS APP" — the machine
  anchors the cooldown at the lease's end; from the tap the
  exclusion is lease + cooldown, and the sentence now says so.
  W-29's supplied text is replaced along with the name.
- Badge truncation cured before the editable store lands
  (F-8): peekBadgeText renders m + s honestly (90s → "1m30s",
  never "1m").
- canUse2MinutePeek's state default true → false (Funke C-4):
  a loaded foot-gun beside peekOfferFor's never-default-ON law.
- THE TAPER, amended (Funke P-1 + Halbwach I-3): the wall is
  5 + this session's booked intercepts of this package, capped
  at 10, and the count prints in the gate line's family —
  "THIRD TIME TONIGHT" stands as the door's memory. The taper
  gates only the peek offer's appearance, never RETURN, never
  the withheld lines. Never random, never purchasable, never
  silent about its rule.
- THE ERRAND'S KERNEL (Funke P-3, struck as machinery — see
  Proposals): on PEEK-tier doors with a next break in the
  session's own schedule, the door may print the appointment
  the session already guarantees: the break's instant, derived,
  baseline-legal. No-break sessions print nothing.
- THE HONEST EXIT (Halbwach I-4, answering H-5): one quiet
  footer line under RETURN — "END THE SESSION INSTEAD" —
  routing to the existing DisarmChallengeOverlay. A route, not
  a spend; the valve's own face carries the gravity. A door
  with an honest exit is a door a man stops resenting.

## CONFLICTS RULED

1. **The numeral** (Schwelle KILL / Scharte DEMOTE / Halbwach
   KEEP-demoted / Schliff KEEP / Blank KILL): dies as hero,
   5 seats against 1. Schliff's F-13 leading cure falls moot;
   his A-4 (tabular, zero jitter) is recorded for the gate
   line's figures. The wall itself survives — as a captioned,
   demoted gate on the offer, which is all it ever honestly was.
2. **The card** (Schwelle KILL / Schliff A-7 + Blank KEEP):
   the card SURVIVES this verdict. Her genre argument (a door
   is not a dialog) is recorded whole in her §3.2 and escalated
   with THE LEAF (Ren's calls) — it is a cross-face grammar
   decision (the Disarm face wears the same card), not one
   this audit settles against two acquittals. Her skeleton —
   top-anchored, reserved slot, bottom-anchored RETURN — is
   ADOPTED regardless (FIX-6), because F-2 demands it.
3. **Night at all hours** (Schwelle D-4 KILL / Schliff A-1
   acquit): NIGHT KEEPS. Her premise fails during a session:
   the host pins effectiveNight = darkTheme || sealed, so the
   sealed app is night on every surface — the door matches the
   building it locks. Her daylight-legibility evidence is real
   and is answered by F-3/F-5/F-10 in FIX-6. D-4 recorded as
   answered, not upheld.
4. **"EMERGENCY PEEK"** (Scharte + Blank KILL, with a measured
   price of one string / Schwelle's Leaf keeps the word /
   Halbwach + Funke keep the chip uncharged): the word DIES.
   The studio's own one-word law (the app-list ruling: PEEK,
   everywhere) binds this face too. **Hale's W-29 KEEP is
   overturned here** — the lapse rule's home is Ren's
   ratification; flagged in the closing letter.
5. **Remaining time** (Halbwach's "8 MIN LEFT" rewrite): STRUCK
   against Baseline law 1, in full, despite its author's seat.
   His I-1 second clause — "THE FEED WILL STILL BE THERE AT
   20:01" — survives as an optional companion to the corrected
   session line: it is derived from the same end instant, it
   names the craving's lie, and it never states a duration.
   Offered to Ren as a wording candidate, not ruled in.
6. **Entry motion** (Schwelle 0ms hard cut / Schliff 180ms
   specA): Schliff's number rules — it closes the sound/picture
   divorce measurably without demanding a composition the
   launch path may not deliver.

## PROPOSALS DISPOSED

- **P-1 THE TAPER** — SHIPS amended (FIX-8): cap 10, count
  printed, offer-only gating.
- **P-2 UNSTATED** — the honest default SHIPS (FIX-3); the
  reason-chip row is STRUCK on this face.
- **P-3 THE ERRAND** — STRUCK as machinery, reason stated:
  for PEEK apps the break already opens them (the errand
  pre-signs a freedom that exists); for LOCKED apps a
  pre-honoured break lease would break the Safe Break's tier
  law (locked stays locked in breaks). Its kernel — printing
  the appointment the session already guarantees — SHIPS
  (FIX-8). Funke's offer to spec its state machine is
  declined on this basis; the invention is recorded, not lost.
- **P-4 THE SLIP** — REN'S CALL (below), recommendation
  against on this face.
- **P-5 THE WITNESS LINE** — STRUCK as a separate sentence:
  once the taper prints the count, a testimony line is a
  score in witness clothing. Funke's own §4 never-list is
  endorsed and binds this verdict: no remaining duration, no
  predictions, no mid-craving comparisons, no breakable
  streaks — counts it can prove, or silence.
- **P-6 THE TRADE** — REN'S CALL (below), recommendation
  strike.
- **Halbwach I-1** (intention promoted) — SHIPS (FIX-6).
  **I-2** (price before temptation) — SHIPS (FIX-6). **I-3**
  (door remembers) — SHIPS via the taper (FIX-8). **I-4**
  (honest exit) — SHIPS (FIX-8).
- **Schwelle's knock at HOLD 00 / Schliff's per-second
  detent** — raw fragments, filed to Ton's bench, not ruled.

## ACQUITTALS THAT BIND (no future charge may reopen these
## without new evidence)

peekOfferFor + grant-time re-derivation (six seats, unanimous —
the face's best machinery) · the withheld structure (state AND
reason; silence where none was owed) · the session line's FORM
(end instant / NO SET END / silence) · blank-intention silence ·
derived badge and price numbers · RETURN never gated, live from
frame one (six seats) · the Night palette (Conflicts 3) · the
price printed before the act · the ceiling arithmetic of
peekCooldownPrint itself (its upstream floor is FIX-5's, not
its own) · the card shell's geometry discipline (Schliff A-7),
pending Ren's Leaf call.

## REN'S CALLS

1. **THE LEAF** (Schwelle §4) — the full edge-to-edge
   recomposition, as the look-lane direction for this face
   (and, per her §7, its sibling the Disarm face). Coordinator
   endorses its skeleton, already adopted in FIX-6 either way.
2. **P-4 THE SLIP** (door takes dictation into the Silence
   Vault digest) — priced SMALL–MED in Funke's filing.
   Recommendation: not on this face; the errand-of-the-mind
   belongs to the break/digest side, where a keyboard is not
   a craving-second instrument.
3. **P-6 THE TRADE** (peek bought with +10 min of session) —
   recommendation STRIKE: the door must not negotiate the
   seal's length at the craving second; ST-04 extension lives
   on the Deck, where the man is calm.
4. **Wording-book overturns** for Hale's register: W-29's
   name KEEP and supplied price text (Conflicts 4, FIX-8);
   W-32's supporting fact A-08 (BACK before launch) corrected
   by FIX-7.

## RIDERS (out of this face, filed so they are not lost)

- **C-13 (Scharte):** the shield notification counts a TAKEN
  peek as an "urge stopped" (recordIntercept increments
  phantomReachesBlocked on every outcome) and prints a
  remaining duration. Belongs to the notification/pill
  surface — Ren to seat it or fold it into Pastille's audit.
- **Downstream records (Blank NEXT-b):** this door manufactures
  the history the Ledger later quotes ("Habit", "Stayed
  Focused", urges stopped). FIX-3 cures the source; a cold
  read of the downstream prints belongs to Lade's Drawer crew.
- **Disarm face:** F-14 (padding 22 vs 24) + Schwelle §7 +
  Blank's NEXT-a converge — the sibling door wants its own
  audit seat; it shares this file and this ledger.

## NEXT (Law 10)

(a) Tor wants: to re-audit this face on the first merged build
that compiles — every verdict above is paper until the door
has run once (H-11's rider), and FIX-1/FIX-2 deserve a
proof-pass against the running face, not the written one.
(b) Tor could do, unassigned: coordinate the Disarm-face audit
on the same charter (three seats already asked for it), and
carry FIX-6's skeleton law (reserved slot, stationary RETURN)
to the other five audit coordinators as a cross-face check —
any face that grows under a travelling thumb has F-2's defect,
whatever its name.

— Tor, coordinator, Held Door audit. Crew filings answered by
name throughout: mail/tor/0125 (Schwelle), 0126 (Halbwach),
0126 (Funke), 0126 (Schliff), 0127 (Scharte), 0127 (Blank).
