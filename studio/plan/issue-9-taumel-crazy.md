# ISSUE #9 — TAUMEL, CRAZY-IDEAS: PARK, THE QUIETEST SCREEN, OUT LOUD

*Seat: Taumel, crazy-ideas (dedicated-100 programme). Ground: AUDIT-park.md
(P-1…P-8, §4 merge list, §5 escalations), the cell innovator's proof ladder
(L0–L3), ParkDrawer.kt as the cell found it. Everything below is BEYOND the
§4 merge list and the innovator's M-mechanisms — wilder, each one concrete
enough to build, each one priced. Laws held: no green, no AI images,
blocker not tracker, PARK changes nothing destructive, CHECK is read-only.
Paper only — nothing compiled.*

---

## T-1 · CHECK IS A KEY TURN, NOT A TAP

**Idea.** CHECK is press-and-HOLD, like turning a key in a lock. While held,
the board re-proves row by row in tier order; each row *strikes* as its fresh
measurement lands — a typewriter strike in Anonymous Pro, a soft mechanical
tick per tier. Release early and the check stops where it stopped, printing
exactly the partial truth: `CHECKED 3/6 — RELEASED EARLY`. A full hold
(~the sweep budget, 2.5s) ends with the board signing itself at the foot
(audit §1.3's signature line, earned by the hold, not printed free).

**Why crazy / why true.** Every other verb in this app is a tap; the one verb
whose whole meaning is *certainty* should cost a gesture. The hold also
solves the honest-partial problem physically: an interrupted proof *feels*
interrupted because his finger left the key.

**Build.** ParkDrawer: CHECK key gets press-state tracking; sweep dispatch
stays the innovator's completing sweep (M1) — only the trigger and the
row-strike rendering are new. Rows animate ink-in on `measuredWall >=
checkStartWall` per item (state already exists). Sound: existing tick
vocabulary, one per tier. **Price: S–M. No DB, no permission, no machinery.**

## T-2 · THE DOUBT KEY — CHECK TWICE, PRINT THE DIFFERENCE

**Idea.** For 10 seconds after any CHECK completes, the key relabels:
`DOUBT`. Pressing it re-runs the sweep *in reverse tier order* and diffs the
two passes. Any row whose answer changed between passes prints a third
dress — `UNSTABLE` — beside its word. Two agreeing passes print one line:
`TWICE MEASURED · SAME ANSWER`.

**Why crazy / why true.** One measurement is a claim; two independent
measurements that agree are the closest L1 ever gets to proof — and a grant
that flickers (Android does revoke/re-grant around service restarts) is
currently invisible, averaged away. DOUBT makes flicker a printed fact.
It also gives the 1 AM man something real to do with his doubt instead of
re-opening the drawer five times.

**Build.** Second sweep pass with reversed item order; diff on the store's
existing per-item measurement structs; UNSTABLE is a print dress, not a new
state (decays at next sweep). **Price: S. Rides M1 entirely.**

## T-3 · THE GHOST BOARD — WHAT IT BELIEVED WHEN YOU WALKED IN

**Idea.** When PARK opens, freeze the board exactly as first rendered —
the *ghost* — in half-ink behind/beside the live board. After a CHECK, rows
whose word changed since the ghost get a margin tick (`»`) and the ghost
word prints struck-through in miniature under the live word:
`GRANTED` over ~~STALE~~. Dismissing the drawer burns the ghost.

**Why crazy / why true.** The audit's spine (P-1/P-4) is that PARK answers
doubt with stale belief. The ghost makes the *gap between belief and proof*
the actual exhibit. He doesn't just learn the grant is fine — he sees how
wrong the drawer was when he opened it, which is the thing that teaches
him whether to trust it next time.

**Build.** Snapshot the rendered row words at drawer-open (composition
state, no persistence); render delta after sweep completion. **Price: S.**
Print-only; the store is untouched.

## T-4 · WHEN THE MACHINE IS HEALTHY, THE SCREEN GOES DARK

**Idea.** Inverse alarm design: if all four counted doors are GRANTED and
fresh at open, PARK renders *almost nothing* — near-black sheet, one line
`GUARD 4/4`, the time, the CHECK key. Any lack, staleness, or unmeasured
item brings the full board up in full light. **Health is boring; only
trouble gets a lit room.**

**Why crazy / why true.** Every settings screen in every app is uniformly
lit, so the eye must read to know. PARK's whole job is one bit — *is the
machine alright* — and light can carry that bit in 200ms from across the
room. It also finally makes PARK the quietest screen by making quietness
its reward state.

**Build.** Conditional composition branch on the existing guard summary;
full board remains one tap away (`BOARD` key un-dims). Careful: NOT
CHECKED (P-6) counts as trouble — dark is earned only by a measured 4/4.
**Price: S. Palette: existing ink/paper tokens only — dark, never green.**

## T-5 · THE USUAL SUSPECT — THE BOARD NAMES ITS OWN WEAKEST DOOR

**Idea.** From the readiness/death history (the Death Certificate data,
audit §5), PARK computes which counted door has died most often *on this
phone* and marks its row, permanently and quietly: `· USUAL SUSPECT`.
Ties print nothing. The mark moves only when the record changes.

**Why crazy / why true.** Machines have character; this phone's character
is that one door always goes first (on most OEMs it's battery/usage).
Naming the suspect turns a generic board into *his* board, and pre-loads
the FIX trip: when something breaks at 1 AM, his eye starts at the right
row before he's read a word.

**Build.** Count of MISSING transitions per item from the readiness log
(the migration audit §5 already owes); max-wins label in the row renderer.
**Price: S once the log exists; blocked on it, say so on the row's behalf
in the plan, don't fake it.**

## T-6 · CHECK YOU CAN HEAR — EACH TIER ANSWERS IN ITS OWN VOICE

**Idea.** CHECK runs audibly: Tier 1 answers with a low thunk, Tier 2 a
mid tick, Tier 3 a high tick, interceptors a dry click — sequenced as the
measurements land (pairs with T-1's strikes). A missing door is *a silence
where its sound should be*. Eyes closed, 1 AM, phone face-down: he hears
4/4, or he hears the hole.

**Why crazy / why true.** PARK is the screen he opens when he least wants
to read — half asleep, doubting. Sound is the only channel that works with
eyes shut, and a *missing* sound is more legible than a missing word.
Silence as a data type.

**Build.** Map sweep-item completion → existing sound engine slots; four
short samples max, all from the app's current mechanical vocabulary (no
new assets beyond variants). Mute follows the sound sled wherever it
lives after audit §1.9. **Price: S.**

## T-7 · PARK PRINTS RECEIPTS TOO — THE CHECK STUB

**Idea.** Borrow the loved object: every completed CHECK prints a 3-line
stub at the board's foot, set exactly like the finish receipt (Anonymous
Pro, ruled, numbered by sweepGen): `№ 041 · CHECKED 02:47:13 · 6/6 ·
SELF-REPORT`. The drawer keeps the last five stubs stacked as a mini
slip-book; tapping one expands its diff (which rows changed vs the stub
before it).

**Why crazy / why true.** The receipt is the one surface Sarthak ❤️'d.
PARK is the one surface nobody loves. Marrying them gives proof a *kept
form*: a CHECK stops being a refresh and becomes an entry in a ledger he
can scroll — the drawer's own testimony, in the app's best typography.

**Build.** Stub = pure rendering of data the completing sweep already
produces; keep last-5 in the WatchStore (in-memory + the foot signature
line is audit §4.3 anyway). Expansion diff = T-3's ghost logic between
consecutive stubs. **Price: M (S if T-3 lands first).**

## T-8 · ONE LINE OF PROPHECY — THE NEXT WINDOW, JUDGED NOW

**Idea.** Vega's Tomorrow Dry-Run (audit §5, filed as a surface) shrunk to
ONE printed line at the board's foot: `NEXT WINDOW — SLEEP 20:00 ·
WOULD ARM` / `WOULD FAIL · USAGE ACCESS`. Computed only from *current*
measurements, labelled projection, re-derived at every sweep. Never WILL.
WOULD is the whole honesty of the line.

**Why crazy / why true.** The failure that hurts is discovering at 20:00
that a door died at 15:00. A forecast line turns PARK from a post-mortem
room into an early-warning one — for the price of a sentence, not a
surface.

**Build.** Evaluate the next routine window's arm conjuncts (the warrant
derivation already exists) against the standing warrant; print under the
plate. No new store. **Price: S.**

## T-9 · FIX LEAVES A STAMP — REPAIR HISTORY ON THE BOARD

**Idea.** When a FIX trip returns FIXED (audit P-5's printed verdict), the
row gains a rotated mono stamp: `RESTORED 02:47` — set like a passport
stamp, slight angle, palette pink (#C84481) ink at partial alpha. Stamps
fade over 24h and vanish; max three visible. A row stamped twice in a
week keeps both stamps, overlapping.

**Why crazy / why true.** Today a repaired row is indistinguishable from
one that never broke — the board forgets its own drama instantly. Stamps
make repair an *event with a residue*, and a row collecting stamps is
T-5's Usual Suspect announcing itself visually before any statistic does.

**Build.** Stamp = last-FIXED wall time per item (one field the FIX
verdict path already knows); renderer draws rotated text, alpha by age.
**Price: S. No image assets — type only, per the no-AI-images law.**

## T-10 · HOLD A ROW, READ ITS LIFE — THE BIOGRAPHY PRESS

**Idea.** Long-press any row and the sheet's foot becomes that door's
one-paragraph biography, three lines max, all from the readiness log:
`GRANTED 14 OCT · DIED TWICE · LAST DEATH 02:47 (BATTERY) · MEAN LIFE
6 DAYS`. Release and it's gone. The quiet screen keeps its secrets until
touched.

**Why crazy / why true.** PARK's board is a snapshot pretending to be the
whole story. The biography is the story — and hiding it behind a hold
keeps the board clean for the 95% of opens that only need the snapshot.

**Build.** Aggregation queries over the readiness log (same dependency as
T-5); foot-area swap in composition. Cause-of-death prints only where the
transition reason is actually recorded — otherwise the line omits it
(omission law, same as the receipt's). **Price: M, log-dependent.**

## T-11 · CHECK IS THE ONE VERB THE SEAL CANNOT LOCK

**Idea.** During the SLEEP seal PARK is read-only (acquitted, binding).
Rule CHECK the single exception that needs no exception: it *is*
read-only — it changes nothing, grants nothing, opens nothing. So during
a seal, the sealed drawer still offers CHECK (and only CHECK), and a
3 AM doubt can be *answered* without unsealing, arming, or waking the
machine. The stub it prints (T-7) waits in the drawer for morning.

**Why crazy / why true.** The seal's law is "nothing changes at night."
A measurement changes nothing — refusing it doesn't protect the night,
it just leaves the doubt alive until morning, which is how seals get
broken for stupid reasons. Let the one harmless verb through.

**Build.** Gate composition: sealed branch renders board + CHECK, all
other verbs absent (not disabled — absent). Sweep machinery is already
seal-safe (queries only). **Price: S. Needs Ren's nod as a seal-law
reading, flagged as such.**

## T-12 · THE MISSING CHAIR — A NO-DOOR ROW SHOWS ITS OWN EMPTY SEAT

**Idea.** When an interceptor's parent service is missing (audit §1.6's
NO DOOR), the row doesn't collapse or grey out — it renders as a *dotted
outline of the row that should be there*, same 49dp pitch, holding the
empty place in the metronome: `THE MISSING CHAIR — {service}` with the
bind/install path as its verb. The board's rhythm never breaks; the hole
has a shape.

**Why crazy / why true.** Absence is usually rendered as nothing, and
nothing is unreadable. A shaped hole keeps the board's ruled metronome
(Linea's acquitted 49dp pitch) honest and makes the missing thing the
most visible object on the screen — which, when it's the thing that
catches notifications, it deserves to be.

**Build.** Renderer branch on the NO DOOR state: dashed border row
(Compose drawBehind, no assets), verb routes to the existing bind pane.
**Price: S.**

---

*Filed as paper. The cell keeps what it can use; Ren rules what needs
ruling (T-11's seal reading above all). More waves follow as the cell
works — Taumel is synchronous, not final.*

— Taumel, crazy-ideas
