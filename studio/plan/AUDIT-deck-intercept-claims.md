# AUDIT — THE DECK'S WARRANT & IMPRINT, THE INTERCEPT & AIRLOCK SENTENCES

**Auditor:** Una — Critics, consistency seat. Working directly under Ren.
**Date:** Oct 29, 2026 (~21:13 IST). **Method:** reading audit only, no compilation.
**Ground:** every printed claim on the Deck that asserts a fact about the
app's state — the proof strip (BLOCK/APPS/TIME cells, the seam stamp),
the sentence slot, the imprint — and every sentence on the intercept /
airlock / disarm faces, verified against the enforcing code (accessibility
service, shield service, peek lease). Trunk =
`~/workspace/clone-inspect/Focus-app` (batch-7 state, DB v10).
**Standard:** a charge files with file+line on both sides — where the
words print, and where the code keeps or breaks them — or not at all.
This file is also the truth baseline the Held Door redesign inherits
(Wetter's law): the intercept's current sentences, sorted into facts,
wishes, and lies, plus its silences.

**Result: 7 charges (1 S1, 3 S2, 3 S3) · 3 named silences · 12 acquittals.**

Cross-references, not duplicates: the header Guard line was acquitted in
my PARK audit (A-03 there) and is not re-tried. Band's HB-1/HB-2 own the
Guard DOOR's mechanics; Salz owns light; Hale owns Drawer/receipt.
Truth Car 2 (WORK-QUEUE item 17) holds no Deck-strip or intercept item —
no overlap. The site path's emptied-list fallback
(`MonolithAccessibilityService.kt:182–185`) is the same line already
charged from the Shield side as UNA-06 (PARK audit); its cure covers
both faces and it is not re-filed here.

---

## CHARGES

### UNA2-01 (S1) — EMERGENCY PEEK: offered on three doors that cannot honour it

The airlock's peek chip is computed from the intercepted app's tier
alone, blind to which intercept path fired:

```
val canPeek = (appEntity == null || appEntity.resolvedTier == AppBlockTier.PEEK_2M) &&
    cooldownLeft == 0 && !_floatingPeekPill.value.isActive
```
(`ui/MonolithViewModel.kt:2091–2112`, `triggerAirlockIntercept`.)

Four paths launch the airlock (`service/MonolithAccessibilityService.kt`):
the tamper/settings blocker (:155–163, badge "Uninstall Guard"), the
Shorts/Reels sniper (:166–176), the browser site blocker (:182–197),
and the per-app tier path (:201–219). **Only the fourth ever reads a
peek lease** (the lease rule at :209–213). The other three intercept
unconditionally — no lease check exists in their code.

Consequences, door by door:

- **Uninstall Guard.** Settings and the package installers are not in
  the apps table, so `appEntity == null` and the null defaults the
  offer ON: five seconds after the tamper door stops him, it offers
  him an "EMERGENCY PEEK" at the settings he was just barred from.
  `startPeekLease` writes by package name
  (`data/MonolithRepository.kt:1091–1099`); no row matches, the write
  lands nowhere, no cooldown is armed — the offer is repeatable and
  the lease protects nothing. The guard door is the one face in the
  app that must never make an offer it cannot keep.
- **Site Blocked.** The chip leases the *browser*; the site path
  (:182–197) never consults a lease, so the blocked domain re-triggers
  BACK + airlock inside the "peek" it just sold him.
- **Shorts/Reels.** When YouTube or Instagram sits at the 2m tier, the
  sniper's airlock offers the peek; the lease (path 5) admits the app,
  but the sniper (:166–176) has no lease exception and re-fires on the
  feed itself, mid-lease, while the peek clock runs.

The one honest door is the per-app PEEK path the chip was designed
for. Cure directions (builders price, not me): derive the offer from
the intercept path as well as the tier — no chip on the guard, site,
or sniper doors — and kill the null-entity default-true: an unlisted
package is a reason to withhold the offer, not to make it.

### UNA2-02 (S2) — "n APPS WILL SHUT" / "n APPS HELD": the count is LOCKED+PEEK

The imprint's tenant prints the count as the seal's whole story:
"idle prints WILL SHUT, sealed prints HELD"
(`ui/components/MonolithCustomComponents.kt:408–431`,
`imprintTenantText`; rendered from
`ui/screens/TactileDeckScreen.kt:480–485`). The count is
`appsCellCount`:

```
repository.allAppsFlow.map { all -> all.count { it.resolvedTier != AppBlockTier.OPEN } }
```
(`ui/MonolithViewModel.kt:451–454` — SHUT and PEEK tiers together.)

A PEEK-tier app does not shut and is not held: opened mid-session it
meets the airlock and a 2-minute lease. The codebase knows the count's
true name — the Deck's own eviction comment calls it "one number,
LOCKED+PEEK" (`ui/screens/TactileDeckScreen.kt:567–571`) — and the
Shield's header prints the honest split, "n SHUT • m PEEK" (PARK
audit A-08). The imprint flattens the split into the stronger verb.
The number is real; the noun is the lie, in both tenses, and the same
count freezes into the sealed print at the seal
(`ui/screens/TactileDeckScreen.kt:460–467`). Cure directions: print
the split in the imprint as Shield does, or let the tenant's count be
the SHUT tier alone and let the APPS cell carry LOCKED+PEEK under its
own name.

### UNA2-03 (S2) — The Deck's warrant cannot age

The strip, the sentence slot, and the imprint all print from one
`Warrant` value (`ui/screens/TactileDeckScreen.kt:479–485`,
`seamMarksFor(deckWarrant)`). The value carries **no read instant**:
the class is four fields — state, a11yBound, a11yStampedThisResidency,
shieldProtecting (`service/WatchStore.kt:62–67`) — and nothing in the
print path can ask how old it is.

Refresh is event-driven only: Watch Store emissions collected at
`ui/MonolithViewModel.kt:655–660` (sweeps land on Deck entry, resume,
FIX return), app ON_RESUME (`MainActivity.kt:300–308`), and the
sentence slot's own RETRY tap. There is no time-based re-read anywhere
in the warrant's path; the Deck's minute ticker
(`ui/screens/TactileDeckScreen.kt:349–356`) re-derives TIME and never
touches the warrant. So while he sits on the Deck — the normal case
at 05:00, deciding whether to arm — an accessibility service killed
silently underneath (the OEM case) leaves the strip printing
"ON · BIND HELD · THIS VISIT · PROOF: LIVE BIND" and the imprint
promising "n APPS WILL SHUT" over a warrant of unbounded age. PARK's
board reads the same store and *derives* staleness at render, printing
STALE past a threshold (`service/WatchStore.kt:142, 313–322`, PARK
A-01/A-03); the Deck's prints have no such derivation. The slide
itself is protected — it re-reads synchronously at DOWN
(`armWarrantNow`, `ui/MonolithViewModel.kt:476`) — so the lie lives
only in the print, never in the arm. That is still the print the
register's own law guards most: a future-tense promise over a wire
whose death nobody has checked for. Rider: the idle TIME cell's
"ENDS HH:mm" derives from the dial alone
(`ui/screens/TactileDeckScreen.kt:365–369`) and prints PASS-toned in
the same frame where BLOCK reads OFF — an unconditional end for a
session the strip itself says cannot currently be armed. Cure
directions: carry the warrant's read instant out of the store (it
exists — the sweep era) and let the seam and the idle tenant degrade
past PARK's own threshold, exactly as the board does.

### UNA2-04 (S2) — Frozen present tense: the strip contradicts itself in one frame

While a session runs, the strip mixes tenses without marking any of
them. BLOCK and the imprint print the seal-time freeze
(`ui/screens/TactileDeckScreen.kt:459–467, 480–485`: BLOCK from
`frozenState`, the tenant from `frozenCount`); the seam directly
beneath them prints the *live* warrant
(`seamMarksFor(deckWarrant)`, :479), and the sentence slot reads the
live warrant too (:990). Kill the bind mid-session — revoke it in
Settings, return — and one screenful reads: BLOCK **ON**, "n APPS
HELD", and directly under them "BIND NOT HELD · PROOF: POSITIVE" and
"BLOCKING IS OFF — TAP TO FIX". The live prints are true; the frozen
ones were true at the seal and are printed in the present tense with
no "at seal" qualifier, for as long as the session stands — the
session's own record books the gap only at finish. The freeze itself
is designed (Emil's law: sealed prints the press-frozen verdict), and
the design's intent is acquitted in A-06; the charge is the tense:
two of the strip's four prints cannot be brought current by anything
the wire does, and nothing on the glass says so. Cure directions:
let the frozen cell and tenant follow the seam when the live state
falls to OFF (the fact changed kind, not degree), or print the
freeze's tense on its face.

### UNA2-05 (S3) — The spent face: "Sessions end when their time does."

Printed on the disarm overlay when the week's break is spent
(`ui/components/AirlockAndDisarmOverlays.kt:630–634`). On its plain
reading it is false twice over: a session ends early every time he
finishes it from the Deck — finishing is the app's core verb — and an
accidental start can be cancelled inside the grace window without
spending anything (`MainActivity.kt:471–473`,
`onCancelWithinGraceWindow` → `confirmDisarmSession`). The sentence's
true content is one clause shorter: "This week's break is already
spent." The rest is rhetoric standing where a fact is expected —
the same standard as PARK UNA-08.

### UNA2-06 (S3) — The peek runs furniture-less: the countdown pill is state without a surface

`start2MinutePeek`'s own contract promises the lease "with the
Draggable Edge-Snapping Countdown Pill"
(`ui/MonolithViewModel.kt:2125–2150`), and the state exists
(`FloatingPeekPillState`, :113–118). No composable anywhere in the
tree consumes it — a full-tree search finds zero render sites outside
the ViewModel. So a taken peek runs invisibly: no countdown on the
glass, and `end2MinutePeekEarly` (:2152–2161) is unreachable from any
surface, so a peek cannot be handed back early either. The lease
itself is enforced (PARK A-12) — what is missing is every word and
number of its life. For the Held Door baseline: today, taking the
peek is stepping into the app with a silent 120-second fuse.

### UNA2-07 (S3) — "ACTIVE INTENTION: " — a label that can print an empty fact

The airlock prints `sessionIntention` verbatim
(`ui/components/AirlockAndDisarmOverlays.kt:548–554`), sourced as
`active?.intention ?: _intentionText.value`
(`ui/MonolithViewModel.kt:2104–2110`). Both can be empty — sessions
arm with no declared intention — and the line then prints its label
over a blank: not a false claim, a hollow one, on the face whose
whole job is to remind him what he declared. Cure direction: the
line prints only when there is an intention to print (the imprint's
empty-box discipline, A-06, is the house pattern).

---

## SILENCES — the Held Door baseline's named gaps

Absences, not false prints — filed so the redesign inherits them as
decisions, not discoveries.

- **SIL-1 — No remaining time.** The intercept never states how long
  the session still runs. The one span derivation every surface is
  sworn to share exists (`data/MonolithModels.kt:341–375`, the Now
  Frame) and the airlock never reads it. A man meeting the door at
  minute 40 of 50 and a man meeting it at minute 2 read the same face.
- **SIL-2 — The peek's price is never printed.** A taken peek arms a
  mandatory 20-minute per-app cooldown
  (`data/MonolithRepository.kt:95`; derivation :1073–1079). No
  sentence on the airlock, before or after, names it.
- **SIL-3 — A withheld peek gives no reason.** When the cooldown is
  running, the chip simply never appears. The state carries the
  remaining cooldown (`peekCooldownRemainingSeconds`,
  `ui/MonolithViewModel.kt:125, 2112`) and the overlay renders it
  nowhere — the offer vanishes silently, indistinguishable from the
  app simply not being peekable.

---

## ACQUITTALS — claims the code keeps exactly, by name

**A-01 · The seam marks, all five prints.** Each branch's words match
`deriveWarrant`'s legs exactly (`service/WatchStore.kt:288–306`;
inputs gathered live at `data/MonolithRepository.kt:570–580`):
a11yBound is the service instance's existence — the system
instantiates the service only when enabled and bound, so existence is
the grant read directly; the residency stamp is a monotonic compare
against this Deck residency's start, so "THIS VISIT" cannot leak
across visits (`service/WatchStore.kt:166–180`); usage is a live
AppOps read (`data/MonolithRepository.kt:540–558`). "PROOF: POSITIVE"
keeps Rosa's definition: OFF rests on a directly observed dead signal
(instance absent, AppOps DENIED — both live reads, never a timeout),
and "BIND HELD · UNSEEN" honestly separates held from seen-working.
A null warrant prints a blank stamp, never a guess
(`ui/components/MonolithCustomComponents.kt:380–401`).

**A-02 · The refusal sentences and their verbs.** Causal priority is
correct: accessibility first (enforcement dies at the first leg),
then the unstamped check, then usage DENIED, then usage UNSET, then
the OFF backstop — a usage-caused OFF can never be misrouted to the
accessibility sentence, because the usage branch precedes it
(`ui/components/MonolithCustomComponents.kt:443–461`). The verbs do
what they say: FIX_ACCESSIBILITY and FIX_USAGE open their own panes
(`ui/screens/TactileDeckScreen.kt:994–995`), RETRY re-reads the wire
synchronously (`MainActivity.kt:508` → `refreshDeckWarrant`), and a
healthy wire prints nothing — the slot keeps its reserved emptiness.

**A-03 · The jam sentence: "… DIDN'T ANSWER — STILL PROVING."**
True for the whole of its life. During the jam window (declared at
L+500, annulment at L+1500) sampling resumes whenever a witness goes
stale and the seal can still fire on a late confirmation
(`ui/ArmProvingController.kt:560–620`; deadlines :692–699). The
machine is still proving; at the deadline the sentence is replaced
by the past tense, never left standing (A-04).

**A-04 · The annulment sentences: "… — NO PROOF. NOTHING WAS
SEALED."** The annulment deletes the row (not deactivates), stops
the shield, leaves no tombstone and no history
(`ui/ArmProvingController.kt:648–690`); the seal-refusal variant
names the session-row link and claims the same two facts, both true
(:621–641). The named link is the first unanswering one, by ordinal —
names, never a count.

**A-05 · TIME, running.** `ENDS HH:mm` is `now + remainingSeconds`,
and `remainingSeconds` is the one Now Frame derivation —
effective target (penalties included) minus elapsed
(`data/MonolithModels.kt:348–351`) — the same number the enforcing
service reads. The idle branches are equally honest: "NO END" for
the stopwatch, "INVALID" for an out-of-range dial
(`ui/screens/TactileDeckScreen.kt:365–369, 468–478`). (The idle
ENDS's warrant-blindness is charged as UNA2-03's rider, not held
against the derivation.)

**A-06 · The imprint's discipline.** Idle, the future prints ONLY on
a LIVE wire — over a dead or unproven wire the box is empty, space
held (`ui/components/MonolithCustomComponents.kt:415–431`). Sealed,
it prints the press-frozen count, never a live recount. The
discipline is the register at its best; what the count *names* is
charged separately (UNA2-02).

**A-07 · The airlock's badges and names.** Path badges name the
mechanism that actually fired — "Uninstall Guard", "Shorts Blocked",
"Reels Blocked", "Site Blocked"
(`service/MonolithAccessibilityService.kt:159, 171, 191`) — and the
tier path prints the intercepted app's own tier badge ("Blocked" /
"2m Peek", `data/MonolithModels.kt:21, 27`). The big name is the
rule's own app name, or, on the site path, the matched domain — the
thing that was actually stopped.

**A-08 · RETURN TO FOCUS.** The service performs GLOBAL_ACTION_BACK
*before* launching the intercept (all four paths), so the blocked
surface is already left behind; the chip hides the airlock and books
the honest outcome "Stayed Focused"
(`ui/MonolithViewModel.kt:2169–2183`). He is never returned into the
app that was stopped.

**A-09 · The disarm face, with a break remaining.** "This ends the
session early. It is the only break this week." — the count is
derived live from the spend ledger for as long as the overlay is
visible, never snapshotted at open (`ui/MonolithViewModel.kt:128–135`
and the overlay's contract comment,
`ui/components/AirlockAndDisarmOverlays.kt:589–598`; the week's
arithmetic is PARK A-11). The rule line "Hold to fill 60 seconds.
Every 15 seconds banks…" matches the valve, whose required seconds
are the constant 60 (`ui/MonolithViewModel.kt:131, 2034`). The spent
face's extra clause is charged at UNA2-05; the face's machinery is
acquitted.

**A-10 · The digest's sentences.** The caller passes the frozen
snapshot's items whenever a freeze exists, an empty list while the
freeze lands, and the live list only when unsealed
(`MainActivity.kt:706–721`) — so "Everything here is as of the time
above. Nothing here opens. Anything newer waits until the session
ends." is kept by construction: frozen rows carry no affordance
(DEL exists only in live mode,
`ui/components/AirlockAndDisarmOverlays.kt:355–376`), newer captures
land in Room, not in the snapshot, and "HELD · AS OF HH:mm" stamps
the freeze's own instant. Live mode's header count is the list's
size, and its empty line ("Zero notifications intercepted during
focus") is true as a class claim — the listener captures only inside
active sessions (`service/MonolithNotificationListener.kt:72–73`).

**A-11 · The eyebrow.** "BREAK · N MIN" prints the safe break's
whole minutes remaining, ceiling-rounded so it never understates
(`ui/screens/TactileDeckScreen.kt:553–562`); "SESSION RUNNING" and
"TOTAL" are labels that claim nothing they don't show.

**A-12 · The desk clock's exit line.** "TAP ANYWHERE TO EXIT" — the
clock's full-screen box is clickable to dismiss
(`ui/components/AirlockAndDisarmOverlays.kt:145–152`). (One sentence
only; the clock's light is Salz's ground and its time is the span
derivation's, A-05's family.)

---

## APPENDIX — out-of-scope sightings, named, not chased

- **The desk clock's banner default.** With no active task and no
  declared intention, the clock prints "Deep Focus Session" as the
  session's name (`ui/components/AirlockAndDisarmOverlays.kt:197`) —
  a fabricated label on the surface he reads at night. The clock
  face is Linea/Salz ground; the word is named here so it is not
  lost.
- **The listener's invented body.** A capture whose text is empty is
  stored with the body "Suppressed notification during study
  session" (`service/MonolithNotificationListener.kt:82`), and the
  digest prints it where the notification's own words belong —
  invented text in a record's clothing. Capture integrity is Hale's
  digest ground; named, not chased.
- **The primer modal's dead sentences.** `ShieldPermissionPrimerModal`
  still prints "SYSTEM GUARD PRE-FLIGHT" and promises "hardware-level
  app interception" (`ui/components/AirlockAndDisarmOverlays.kt:436–447`)
  — accessibility interception is not hardware-level, and the modal's
  binding stays removed (`MainActivity.kt:740–744`), so the sentences
  print nowhere today. Dead print with a false sentence inside: do
  not resurrect without a re-audit (the PARK UNA-01 pattern).
- **The simulate-intercept parameter.** The Deck accepts
  `onSimulateIntercept` (`ui/screens/TactileDeckScreen.kt:134`),
  wired in `MainActivity.kt:475–481` to a hardcoded fake (TikTok,
  badge "Suitcase Locked") — and the screen never invokes it. A fake
  airlock sits one call from live; if any future surface wires it,
  the airlock's header becomes a printed fiction.

— Una, Critics (consistency), Oct 29, 2026.
