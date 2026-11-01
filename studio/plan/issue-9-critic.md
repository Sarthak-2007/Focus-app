# ISSUE #9 — CRITIC: EVERY PROOF PARK ASSERTS AND DOES NOT HAVE

*Critic, issue-9 cell (for Hof). Scope: UNRESTRICTED — code, words,
plans, process. Every charge carries file + line or quoted text,
or it is not a charge. Ground: the cell clone on branch
`agent/issue-9`, commit `7387f21` ("CHECK verb + re-sweep on open,
GUARD — NOT CHECKED, FIRED AS SET verified struck") plus the
working tree as it stood while this was written (~03:20 IST,
Oct 30) — the cell was building live under this audit, and
charges are marked where a fix was mid-landing. Audits read:
`AUDIT-park.md` (Hof, P-1…P-8), `AUDIT-park-claims.md` (Una,
UNA-01…09 / A-01…A-14), `issue-9-designer.md`,
`issue-9-innovator.md`, `issue-9-plan.md` (Hof),
`issue-9-finisher-guardline.md`, `issue-9-finisher-keys.md`.
Paper only — nobody compiles.*

*Severity: S1 = a printed or sounded claim that is false or
unearned in a reachable state. S2 = an unearned implication, a
stale-proof path, or a cell record that overstates what landed.
S3 = a wording/process fault that will become S1/S2 if nobody
touches it.*

---

## CHARGES

### C-1 (S1) — CHECKING completes on stamps the press never caused: the shield loop and the previous sweep can "answer" CHECK

The completion derivation is stamp-time only:

> "Any writer's stamp counts (the sweep's queries, a lifecycle
> hook, the shield loop): the claim is 're-measured since you
> asked', whoever measured."
> — `ui/components/ParkDrawer.kt:188–191`

```kotlin
val checking = checkPressedMono > 0L && !WatchItemId.PERMISSIONS.all { item ->
    (snapshot?.items?.get(item)?.measuredMono ?: 0L) >= checkPressedMono
}   // ParkDrawer.kt:192–194
```

Three laundering paths, all reachable:

1. **The shield loop answers two of the six for free.** While a
   session runs, `MonolithShieldService` iterates every 2 s
   (`service/MonolithShieldService.kt:193`, `delay(2000L)`) and
   each iteration re-stamps ACCESSIBILITY and
   NOTIFICATION_LISTENER from `instance != null` alone
   (`service/WatchStore.kt:233,240`, via `onShieldIteration`).
   Within 2 s of any press, those two items carry post-press
   stamps whether or not the sweep's queries ever landed. The
   "re-measurement" for the board's two BIND doors is a presence
   re-read the loop was already doing — the press bought nothing.
2. **The PARK-open sweep answers a later press.** Opening PARK
   now dispatches a sweep (`MainActivity.kt`, parkOpen effect →
   `runWatchSweep()`). A press 100 ms later is "answered" by
   queries dispatched *before* the press that happen to *land*
   after it, because stamps record landing time, not dispatch
   time. The measurement exists that ties a stamp to its sweep —
   `WatchMeasurement.sweepGen` (`service/WatchStore.kt`, the
   measurement record; `runSweep` increments and passes it,
   `:254–262`) — and the CHECKING derivation never reads it.
   The plan ratifies the hole as a feature: "CHECKING until
   every permission item carries a measurement stamped at or
   after the press, whoever stamped it" (`issue-9-plan.md`,
   item 1).
3. **The verdict can be pronounced on the wrong sweep, then
   contradicted silently.** The completion effect
   (`ParkDrawer.kt:204–224`) fires the firm click / sets the
   NOT PROVED name from the snapshot that ended CHECKING. If
   that was the open-sweep's landings and the press's own sweep
   lands a beat later with a different result, the board flips
   (a row goes MISSING, FIX appears) *after* the click already
   sounded "whole" — and no second verdict ever prints, because
   `checking` is already false and the effect cannot re-fire.

The Designer's own standard was stricter: CHECKING runs "from
dispatch until the set completes" (`issue-9-designer.md` §1.5) —
the *dispatched* set. Cure: completion must require stamps of
the press's own sweep generation (or a generation ≥ the one the
press dispatched), for all six items, the way LAST PROVED's
era logic at least attempts (see C-5 for why that attempt also
leaks).

### C-2 (S1) — A query that never returns bricks the key at CHECKING forever, and the cell has enshrined this as acceptance

There is no timeout, deadline, or escape anywhere in the CHECK
path (grep: no `2500` / `2_500` / `withTimeout` in
`ParkDrawer.kt`). While `checking` is true the key takes no
second press (`ParkDrawer.kt`, CHECK key: `if (interactive &&
!checking)` gates the `pointerInput`). One hung AppOps/binder
query — the exact failure the Designer designed her FAILED
state for (`issue-9-designer.md` §1.5: 2,500 ms window, then
`NOT PROVED — {NAME} DID NOT ANSWER`, persistent) — leaves the
drawer saying CHECKING for the rest of its life. A CHECKING
that never ends is itself a claim — "the machine is still
answering" — and after a minute it is a false one.

The cell has reasoned its way into this and written it down as
virtue: "the honest face for that is the key staying dipped at
CHECKING for the drawer's life, not a word a clock invented"
(`issue-9-finisher-keys.md` §5.2), and its acceptance list
makes it a test: "Kill a query so it never returns: CHECK stays
dipped at CHECKING — no word appears at 2,500ms or any other
mark" (ibid., acceptance 4). The reasoning proves too much. A
timed *verdict* would indeed be invented; a timed *confession*
is not: the key may not say NOT PROVED on a clock, but it must
stop saying CHECKING on one. Minimum honest faces, any of
which cures this without inventing a verdict: print the press's
age beside the word once it passes the sweep's own ~300 ms
class; or let a second press re-dispatch (a fresh generation
replaces the abandoned one); or on drawer close-and-reopen,
seat the key at rest with the stragglers' rows still UNMEASURED
— which is what happens today only by accident of `remember`
dying, and is nowhere stated. And note what the plan did with
the Designer's FAILED state: it was "adopted with one
correction" (`issue-9-plan.md`, item 1) — the correction
renamed failure to *completed-with-a-lack* (the built
`NOT PROVED` row, `ParkDrawer.kt:581–589`), deleting the only
state that covered a query that never answers, while keeping
the word "adopted" on the tin.

### C-3 (S1) — The header Guard line's warm lie survives the cold-open cure: `GUARD 4/4` has no clock

P-6 cured the blank. The opposite stale-proof path is untouched.
`guardLinePrint` is a `combine` of exactly three flows —
snapshots, sealFreeze, activeSession
(`ui/MonolithViewModel.kt:667–683`) — evaluated with
`SystemClock.elapsedRealtime()` at combine time. Nothing in
that chain ticks. Staleness is a function of *now*
(`WatchStore.permPrintAt`: GRANTED older than
`STALE_AFTER_MS = 45_000` becomes STALE, `WatchStore.kt:142,
permPrintAt`), so the print can only change when a snapshot
happens to emit. Idle on the Deck with no session — shield loop
not running, lifecycle hooks silent, the Deck-entry sweep long
landed — no emission ever comes, and the band prints
`GUARD 4/4` for as long as he cares to look at it: four grants
last measured minutes or hours ago, wearing the present tense.
PARK solved this exact problem locally with a 5 s re-derivation
beat (`ParkDrawer.kt`, the `nowMono` loop); the header — the
line he sees on *every* screen — has no equivalent. The cure
that makes the line honest when it knows nothing must also
make it honest when what it knows has rotted: add a ticker to
the combine (or derive the header print inside a composable
with a beat, as PARK does), so 4/4 degrades to
`GUARD {n}/4 — {NAME} STALE` in front of him. Until then "never
blank" is only half of P-6: never *stale-silently* is the other
half, and it is unbuilt and unfiled in the cell plan.

### C-4 (S2) — The seal freeze can now freeze an ignorance and hold it for a session's whole life — and CHECK is locked out of the one state where doors die

Two consequences of the landed cures, compounding:

1. The freeze captures whatever `guardLineFor` prints at the
   seal frame (`MonolithViewModel.kt:840, 881` capture sites;
   the frozen branch at `:672–673`). Post-cure, an unmeasured
   seal frame freezes `GUARD — NOT CHECKED`, and the band holds
   it for the session's whole life even after every grant is
   measured and live. The Designer pre-acquitted this ("That is
   honest, not a defect", `issue-9-designer.md` §2.3) — but the
   symmetric case was already the standing risk and is now
   sharper: a seal frame at 4/4 freezes *that* too, and no
   in-session event can correct either print in either
   direction.
2. CHECK cannot help: the key is absent under the seal
   (built: `if (!isSealed)`, `ParkDrawer.kt:538`), and PARK
   itself cannot even be invoked while sealed
   (`MainActivity.kt` `openPark()`: `if (!sealed &&
   !viewModel.navigationLocked()) parkOpen = true`). So during
   a session — the exact window P-8 was filed about ("a door
   dying after the 20:00 SLEEP seal cannot even be viewed
   until morning") — there is no re-measurement verb anywhere,
   and the only Guard print is the frozen one. The cell's own
   papers contradict each other on whether this is lawful:
   the Designer seats CHECK under the seal as inert print
   (`issue-9-designer.md` §1.7); Finisher 2 rules that a
   print-dress key under the seal would be "an object lying
   about being one" and keeps both keys absent
   (`issue-9-finisher-keys.md` §8); the Innovator proposes the
   opposite of both — CHECK *stays live* while sealed, because
   "a sealed drawer that cannot even re-measure is P-8 again"
   (`issue-9-innovator.md` §7). Three seats, three rulings,
   one built. Hof must rule on the record; right now the merge
   would inherit whichever paper a builder reads last.

### C-5 (S1) — LAST PROVED can advance on a set that never coexisted, and its print hides the date

The complete-set rule in `publish()`:

```kotlin
val sweepStart = pendingSweepStartWall
if (sweepStart > 0L) {
    val perms = WatchItemId.PERMISSIONS.mapNotNull { next.items[it] }
    if (perms.size == WatchItemId.PERMISSIONS.size &&
        perms.all { it.measuredWall >= sweepStart }) {
        val completedAt = perms.maxOf { it.measuredWall }
        ... next.copy(lastProvedWallMillis = completedAt)
        pendingSweepStartWall = 0L
    }
}   // service/WatchStore.kt:421–433 (publish internals)
```

The era test is `>= sweepStart` with no upper bound and no
freshness window, and `pendingSweepStartWall` is never cleared
except by completion. So: a sweep lands five items; the sixth
query hangs. Hours later the sixth item is stamped by any
other road — a lifecycle hook, a FIX return, the shield loop —
and the "complete set" completes retroactively out of five
hours-old measurements and one fresh one. LAST PROVED advances
to the fresh stamp while five board rows on the same face
print STALE. The telltale and the rows contradict each other
in the same eyeful. The doc comment at `runSweep` even claims
the stricter rule — "When all six carry this sweep's
generation, LAST PROVED advances" (`WatchStore.kt:251–253`) —
but the implementation never compares `sweepGen` at all. The
comment is the acquitted law (A-02, complete-set rule); the
code is an era smear.

Second, independent fault in the same print: PARK renders the
instant as `SimpleDateFormat("HH:mm", …)` and nothing else
(`ParkDrawer.kt:385–386`). A LAST PROVED from yesterday at
21:14 prints `21:14` — indistinguishable from today at 21:14.
The store is in-memory, so the instant cannot predate the
process — but a process lives for days, and the print carries
no date and no age. A proof print that cannot say *which day
it proved* is a stale-proof path by typography. (The
Innovator's historical prints already know this law:
"prints … with the date when not today",
`issue-9-innovator.md` §8 #17. The board's own telltale should
obey it first.)

### C-6 (S2) — RUNNING has no stale form: interceptor words are stored derivations, printed without an age test

Permission rows degrade at render (`permPrintAt`, age vs
45 s). Interceptor rows do not: PARK prints the *stored*
`m.interceptor` verbatim (`ParkDrawer.kt:393–407`), and
`InterceptorState` has no STALE case. The derivation re-runs
only inside `publish()` (`WatchStore.kt:405–415`) — and
`poke()`, the store's "nudge with no data", bypasses `publish`
entirely (`_snapshots.value = _snapshots.value.copy()`), so a
poke re-emits the snapshot with interceptor words exactly as
stale as before while claiming in its comment to force
"derived readers to re-read the living facts". Worse, every
`publish` re-stamps each interceptor measurement with the
publish instant (`measuredMono = mono`, `:410`), so even the
record's own age field lies: an interceptor "measured" a
second ago may be a re-print of a derivation whose parent
grants were measured an hour ago. If emissions stop — shield
dead, no sweeps — SHORTS + REELS / SOCIAL SITES /
NOTIFICATIONS print RUNNING forever. RUNNING is the strongest
word on the board (it claims work is happening, P-2), and it
is the only word with no decay law at all.

### C-7 (S2) — Two counted doors are "proved" by presence, and NOTIFICATIONS RUNNING by a binding that may never have captured

- `watchGrantQuery` answers ACCESSIBILITY and
  NOTIFICATION_LISTENER from `instance != null`
  (`data/MonolithRepository.kt:646, 649–650`); the shield
  loop re-stamps the same test every 2 s (C-1). A bound but
  wedged service — enabled, instantiated, processing nothing
  — passes every sweep, every CHECK, and advances LAST PROVED.
  The audit's residue already named this ("BIND-row GRANTED =
  `instance != null`, laundered fresh every 2s by the shield
  loop", `AUDIT-park.md` §2 RESIDUE); issue #9's CHECK now
  puts a verb and a firm click on top of the laundering
  without touching it.
- NOTIFICATIONS derives RUNNING from the listener parent
  alone (`WatchStore.deriveInterceptor`: NOTIFICATIONS
  parents = `[NOTIFICATION_LISTENER]`, setting hard-wired
  `true`). Nothing in the derivation asks whether the
  listener has ever captured a notification in this process
  or on this phone. A listener the OS bound and then starved
  prints RUNNING beside a digest that has never received a
  thing. The honest historical fact exists in Room
  (`CapturedNotificationEntity.timestampMillis` — the
  Innovator's "LAST CAPTURE", `issue-9-innovator.md` §3);
  the board does not quote it.

### C-8 (S2) — The warrant's one activity witness can be satisfied by Monolith's own windows

`stampedThisResidency()` — the sole conjunct that upgrades
the warrant from grant-report to activity proof
(`WatchStore.deriveWarrant`: LIVE requires `stamped`) — reads
`lastRootMono`, which is stamped in `onAccessibilityEvent`
*before* the own-package early return:

```kotlin
if (rootInActiveWindow != null) {
    lastRootMono = SystemClock.elapsedRealtime()
    WatchStore.poke()
}   // service/MonolithAccessibilityService.kt:173–176
...
if (pkgName == applicationContext.packageName ||
    pkgName.startsWith("com.android.systemui")) return
    // :184–185 — the filter the stamp precedes
```

Any event — Monolith's own UI churning, a SystemUI shade
twitch — with a non-null root satisfies the residency. So the
Deck's LIVE today, and any warrant line PARK prints tomorrow
(§4.2), can rest on the pipeline having processed *Monolith
itself*, never a foreign foreground. The stamp proves the
event bus is warm; the prints that ride it ("BIND HELD · THIS
VISIT" / "PROOF: LIVE BIND", `MonolithCustomComponents.kt:
383`) say it proves this visit's pipeline. Those are
different claims, and the gap is exactly the size of P-2.

This also pre-charges the Innovator's M2
(`issue-9-innovator.md` §3): `provePipelineNow()` would have
the service acquire its root and stamp `lastRootMono`
*itself*, then CHECK would read that same field as the
witness — a witness that testifies because it was told to.
And because the field is shared, a PARK pull would launder
straight into the Deck's residency test; the sketch's "do
not reuse the Deck residency" does not help while both
residencies read one stamp. M2 is buildable honestly only
with a *separate* stamp field for the pull, and with the
print admitting the pull ran against Monolith's own
foreground window (PARK open = Monolith in front), which
proves the query machinery turns — not that it would catch
YouTube's tree. The sketch's own "Cannot prove" paragraph
concedes the second half; the first half (self-stamping,
shared field) it does not mention.

### C-9 (S2) — The sweep launders a crashed query into a measured denial, and CHECK inherits it

```kotlin
val granted = runCatching { query(item) }.getOrDefault(false)
stampPermission(item, granted, gen)   // service/WatchStore.kt:259–260
```

A query that *throws* — AppOps binder failure, a SecurityException
on an OEM build — is stamped MISSING, indistinguishable from a
query that answered "no". Finisher 2 states this as settled
fact ("a throw stamps MISSING", `issue-9-finisher-keys.md`
§5.2) and builds CHECK's philosophy on it ("Every door
answers"). Every door does not answer; some doors' *questions
crash*, and the store prints the crash as a verdict in full
lack-ink, names it on the FIX key, and sends the man to
Settings to repair a grant that may be perfectly present.
CHECK then "re-proves" the crash and can play its verdicts
over it. Minimum cure: a thrown query stamps nothing (the row
keeps its old word and its old age — partial truth, the
house's own law), and completion predicates count it as
unlanded, which is what makes C-2's escape necessary rather
than theoretical.

### C-10 (S2) — "FIRED AS SET": struck in the app, alive in the master spec, and the cell plan closes the item on a grep that buries the lede

Verified in the app: PLATE_BODY prints `ROUTINES: SET BY
HAND ON SHIELD` (`ParkDrawer.kt`, PLATE_BODY, with the W-11
comment at `:115`-area recording the strike and its return
conditions), and a test pins the absence
(`app/src/test/java/com/example/TruthCar3SweepTest.kt:332,
353`). The restoration conditions are verifiably unmet in
this tree: the routine chain alarm is still inexact —
`setAndAllowWhileIdle` (`service/RoutineScheduler.kt:44`),
no `setExact*` / `canScheduleExactAlarms` anywhere in `app/
src/main` — so Wache's F1–F5 are not in this build and no
live fire has been re-verified. So far, the plan is right.

But the plan's closure sentence — "A repo-wide grep finds
the sentence only in that comment and in paper"
(`issue-9-plan.md`, item 3) — buries what "in paper" contains.
The sentence stands unmarked, as current spec text, in the
documents a future builder is most likely to build from:

- `studio/plan/MASTER-PLAN.md:4677` —
  `ROUTINES: SET BY HAND ON SHIELD · FIRED AS SET`
- `studio/plan/CHROME-BUILD-READY.md:658` — the plate text in
  a document whose name is a build instruction.
- `studio/plan/designers-parts/teen.md:411`,
  `studio/plan/seat-drafts/s8-stoff.md:223`,
  `studio/plan/seat-drafts/s8-vau.md:426` — the same plate,
  uncorrected.

Other papers do carry the strike (`TRUTH3-WORDING.md` W-11;
`TRUTH3-MERGE-MAP.md:335`), which makes the unmarked copies
worse, not better: the repo now disagrees with itself about
what the plate says, and the disagreeing copies are the
master ones. Item 3 is not closed until the master spec
plate is struck through with the W-11 conditions written at
the site — the same verify-only discipline the cell applied
to the code. Kin, same disease, smaller dose: the plate's
neighbouring absolutes stand in this tree only because their
machinery happens to hold today — `DATA: ON THIS PHONE ONLY`
now backed by `android:allowBackup="false"`
(`AndroidManifest.xml:30`, UNA-04 cured — acquitted, §A);
`PEEK: 2 MINUTES` true only while the lease is the constant
120_000 (the W-08 input problem the plan itself flags).
Every static plate line is a claim with an unmarked expiry
date; the plate needs the Innovator's §8 freshness rules
applied as a standing census, not a one-off grep.

### C-11 (S3) — Three different definitions of "whole" share one surface

CHECKING completes on all **six** permissions
(`ParkDrawer.kt:192–194`). The completion click and the NOT
PROVED name are computed on the counted **four** only
(`ParkDrawer.kt:210–221`). The board prints **nine** lines.
So a CHECK whose only lacks are OVERLAY and BATTERY
completes, plays the firm click — the voice the house
reserved for "repaired / whole" — and prints no NOT PROVED
line, while two rows sit in full lack-ink on the same face.
The Designer sanctioned the counted-4 click
(`issue-9-designer.md` §1.5), so this is filed S3, as a
ruling the cell must make *explicitly* rather than inherit
by collision: either the click's condition is the board's
condition (all printed lacks count), or the click is
re-voiced so it cannot be heard as "the board is whole".
Inherited-by-collision is how the next audit gets its P-1.

Also on process, briefly: the cell plan declares item 1 and
item 2 "FIXED" (`issue-9-plan.md`) while, at the time of
writing, Finisher 4's totality patch (F-G4) and Finisher 2's
CHECK dress ladder (§4.1 — the built key is always ink fill,
`ParkDrawer.kt:546`, where the Designer ruled print dress at
rest on a fresh board, §1.4) were PATCH-READY papers over
the committed code, with the totality edits only appearing
in the working tree during this audit. "FIXED" in a cell
plan should mean *in the branch*, not *specced beside it*.
Ren's merge should re-verify both against the branch tip,
not the plan.

---

## THE CHECK HONESTY STANDARD — what a check must do before the UI may say proved

A CHECK (or any future verb of proof on this board) earns the
right to print, sound, or imply "proved" only when **all** of
the following hold. Anything less must print the weaker true
sentence, in the house's existing words.

1. **Generation-bound completion.** The set that releases the
   verb is the set the press dispatched: every counted item
   carries a stamp from that sweep's generation (or a later
   one the press can name). Stamps from earlier sweeps,
   lifecycle hooks, and periodic loops may update rows; they
   may never *complete* a check. (C-1, C-5)
2. **Answered ≠ crashed ≠ silent — three outcomes, three
   faces.** A query that throws stamps nothing and counts as
   unlanded. A query that never returns must eventually stop
   the verb claiming it is still checking: no timed verdict,
   but a timed confession (age of the press, or a re-press
   that re-dispatches a fresh generation). Silence is never a
   fourth state that looks like patience. (C-2, C-9)
3. **Presence is not activity.** `instance != null` is L1
   SELF-REPORT (the Innovator's ladder, `issue-9-innovator.md`
   §1, is adopted here as the naming law). No word, click, or
   advance of LAST PROVED may present a presence read as a
   pipeline witness. Activity claims require an activity
   stamp the checker did not write itself, from a field no
   other proof consumes. (C-7, C-8)
4. **Every proof print carries its instant — and its date
   when the instant is not today.** A proof without a
   readable age is a claim of *now*. LAST PROVED, CHECKED,
   frozen Guard lines: all obey or none may print in the
   present tense. (C-3, C-5)
5. **Freshness is derived at render, for every family.**
   Any word that can rot must be able to print its rot:
   permissions have STALE; interceptors and the header line
   must gain the same derivation or stop printing present-
   tense words when their derivation is older than the
   board's own 45 s law. A stored word is a historical
   document, and must be dressed as one. (C-3, C-6)
6. **One definition of whole per surface.** The set that
   completes the verb, the set that earns the success sound,
   and the set the verdict line names are the same set, or
   each print names its own set on its face. (C-11)
7. **The grade is printed with the verdict.** A sweep-only
   CHECK is SELF-REPORT and must travel with that qualifier
   the way LAST PROVED already does — on the verdict line,
   not in a comment. `BLOCKING PROVED`, an upgraded RUNNING,
   or any end-to-end word is issue #21's property and may
   not be borrowed, hinted, or pre-sold by this verb.
   (Innovator §1/§7; Designer §1.1/§1.8 — both already rule
   this; the built verb's comment calling itself "the
   drawer's verb of proof", `ParkDrawer.kt` (3b) header,
   should be re-worded to the grade it holds.)
8. **A check must be callable in the state where proof
   decays.** A verb that absents itself under the seal while
   the header freezes its last print leaves the longest
   stretch of the machine's life — the session — with no
   lawful way to re-measure. If the seal forbids the verb,
   the frozen print must carry its seal-instant on its face,
   so the reader can price its age himself. (C-4)

## ACQUITTALS — what this audit checked and the code keeps

- **A-C1 · USAGE UNSET.** The warrant still collapses UNSET
  to UNPROVEN, never OFF (`WatchStore.deriveWarrant`), and
  `usageAccess()` is honestly three-valued
  (`MonolithRepository.kt:585–610`). The board's Boolean
  collapse (C-7's neighbour, audit RESIDUE) stands charged
  in Hof's residue already; the warrant side is clean.
- **A-C2 · UNA-01/02/03/04 in this tree.** Shorts setting
  persisted and honoured (`shortsSniperArmed`, a11y service
  `:112–115`, gate at `:231`); social-sites sled writes the
  persisted flag the service reads; sound persisted via
  `SoundEffectsSetting` (ViewModel `toggleSound`); backup
  off (`AndroidManifest.xml:30`). Una's S1s are dead in this
  tree and this critic does not re-file corpses.
- **A-C3 · The NOT CHECKED print itself.** No count, no
  fabricated number, both unmeasured paths covered, tests
  declared for all four prints (`GuardLinePrintTest.kt`).
  The charge is what surrounds it (C-3, C-4, C-11's process
  note), not the string.
- **A-C4 · The plate strike in code.** See C-10: the app is
  clean; the paper is not.

— Critic, issue-9 cell (for Hof)
