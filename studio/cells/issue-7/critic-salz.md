# Issue #7 — Shield (audit fixes) — Critic Charges: SALZ

**Seat:** SALZ, CRITIC, in VOGT's cell for issue #7, "Main-app pass — Shield (audit fixes)."
**Scope:** unrestricted — code, words, audit assumptions, merge pricing, and process.
Every charge carries file + line evidence or a reproducible state path. No app code
edited, nothing compiled, no tests run. This cell closes **DECLARED**.
**Base:** branch `agent/issue-7` at `8438f5a` (plus in-flight cell work noted where seen).
**Sources read:** `studio/plan/AUDIT-shield.md` in full (R-1…R-13);
`ui/screens/ShieldMatrixScreen.kt` (1,086 lines); `MainActivity.kt` (Shield call site,
`sealed`); `service/MonolithAccessibilityService.kt`; `service/MonolithShieldService.kt`;
`service/WatchStore.kt`; `service/RoutineScheduler.kt`; `service/RoutineHeartbeat.kt`;
`data/MonolithModels.kt` (`RoutineSchedule`, `FocusSessionEntity`);
`data/RoutineDerive.kt`; `data/MonolithRepository.kt` (`standingWarrant()`,
`queryForegroundBlockedApp`, peek constants); `data/AppRules.kt`;
`ui/MonolithViewModel.kt` (routine editors, warrant refresh); cell filings
`designer-form.md` and `innovator-funke.md` (concurred with where noted — the charges
below are filed against the audit and the tree, not against my cellmates).

**Tags:** `[AUDIT]` = the audit itself filed this; I verify and sharpen it.
`[NEW]` = not in the audit. `[DELEGATED]` = owned elsewhere; interim wording only.

---

## CRITICAL

### C-1 `[NEW — sharpens Fremd C1 / R-5]` R-5's recipe cannot produce R-5's own example sentence

R-5 orders the sentence *"derived from the session snapshot the screen already
receives"* and prices it *"small — the facts are already in hand"* (AUDIT-shield.md,
R-5 and merge item 1). In this tree the screen receives no session snapshot at all:

- `ShieldMatrixScreen.kt:99` — the entire session input is `isSessionActive: Boolean = false`.
- `MainActivity.kt:243` — that bit is `val sealed = activeSession?.isActive == true`,
  passed at `:561`. There is no session id, no intention/profile name, no end instant
  anywhere in Shield's contract.

So the example sentence — `BLOCKING NOW — SLEEP SESSION · ENDS 03:30` — has **no lawful
source for either of its two variable parts** in the data R-5 says to derive it from.
This is not a wording quibble: merge item 1 is the audit's headline, it is priced the
cheapest item on the list, and as specified it is unbuildable without the contract
widening R-5 never names. Any builder who "just implements R-5" will either invent the
missing inputs (see C-6) or print the sentence off the boolean (see C-2). The audit's
price and its recipe are both wrong; the finding underneath them is right.

### C-2 `[NEW]` A session row's `isActive` flag is not enforcement — and there is no single "enforcement" to print

The boolean behind `sealed` is a persisted Room flag (`FocusSessionEntity.isActive`,
MonolithModels.kt). Both enforcers treat it as an *input*, never as evidence, and each
door has a different second gate:

- **Accessibility door** (per-app tiers, sniper, site blocker): returns without
  intercepting unless the snapshot is active (`MonolithAccessibilityService.kt`,
  `onAccessibilityEvent` → `dao.getActiveSessionSnapshot() ?: return@launch`;
  `if (!activeSession.isActive) return@launch`) — **and** the code only runs at all if
  the service is bound (`instance`, set in `onServiceConnected`, cleared in
  `onUnbind`/`onDestroy`). State path: session live + accessibility unbound in system
  settings → every door in this service is dead while SHUT still prints as a state.
- **Shield-service door** (foreground polling): `MonolithShieldService.protecting`
  flips true after one full monitor iteration — but its actual intercept calls
  `repository.queryForegroundBlockedApp(...)`, which **returns null unconditionally
  when usage access is missing** (`MonolithRepository.kt`: `if
  (!hasUsageStatsPermission() || blockedPackages.isEmpty()) return null`). State path:
  revoke usage access mid-session → the shield loop keeps "protecting," its
  notification keeps updating, and its door intercepts nothing.
- **Notification door**: suppression lives in `MonolithNotificationListener`, gated
  on its own binding — a third, independent life.
- **Device admin** gates only the uninstall guard (`MonolithDeviceAdminReceiver`) —
  it has *no* part in app blocking. Any derivation that gates "BLOCKING NOW" on the
  Watch Store's counted four (accessibility, usage, device admin, listener) will
  print NOT BLOCKING over a phone whose apps are in fact blocked, and vice versa.

"BLOCKING NOW" is therefore not one fact. A sentence that does not say *which door*
it attests — or that silently attests all of them from one leg — is false assurance
in both directions. The audit's headline fix, taken literally, manufactures the
exact lie the audit was convened to kill, in larger type at the top of the screen.

### C-3 `[NEW]` R-5's closed binary has no lawful print for the unproven state

R-5 specifies exactly two sentences: `BLOCKING NOW — …` / `NOT BLOCKING — NO SESSION
RUNNING`. The app already owns a third state and knows it needs one:
`WarrantState { LIVE, OFF, UNPROVEN }` (WatchStore.kt), `WatchStore.printState`
degrades a stale LIVE to UNPROVEN, and the Deck already prints
`CAN'T CHECK BLOCKING — TAP TO RETRY`. R-5's binary deletes that state from Shield.
Forced into the binary, every unproven instant must lie one way or the other:

- **Cold start / first composition:** the active-session flow's pre-emission value is
  null, so `sealed` reads false before Room has answered — the binary prints
  `NOT BLOCKING — NO SESSION RUNNING` during the unmeasured instant, as a fact.
- **Session active, witnesses not yet landed** (rebind, process restart, shield
  started but no iteration yet — `protecting` is reset to false on *every*
  `onStartCommand` in MonolithShieldService): the binary's only positive form is
  `BLOCKING NOW`, printed on the row alone.
- **Session ended on disk, enforcers not yet stood down** (or the reverse): one of
  the two forms is printed; neither is the truth.

A two-word vocabulary for a three-state world is the defect. This is an audit-design
charge, not a builder charge: R-5 must be amended to carry the UNPROVEN form before
any builder touches it.

### C-4 `[NEW]` The obvious proof sources are traps: the warrant's freshness is minted, not measured; its residency is the Deck's

If the builder reaches for the existing warrant to cure C-2, three traps are loaded:

1. `MonolithRepository.standingWarrant()` calls `WatchStore.deriveWarrant(...)`,
   whose `readMono` **defaults to `SystemClock.elapsedRealtime()` at call time**.
   A warrant read now is stamped now even when the pipeline proof underneath it —
   `lastRootMono` — is hours old. `warrantStaleForPrint` ages the *read*, never the
   underlying event stamp. Fresh-looking ≠ fresh.
2. The residency the warrant demands is **Deck-scoped**: `noteResidencyStart()` is
   called only in `MainActivity.kt`'s `LaunchedEffect(currentDestination)` when the
   destination is DECK. A user who opens the app straight onto Shield rides a
   residency started for another room, or none (`stampedThisResidency()` is false
   when `residencyStartMono == 0`). The warrant's LIVE was designed as a pre-arm
   verdict for the Deck slide, not as Shield's standing status.
3. `deriveWarrant` **explicitly excludes** shield-protecting from its conjuncts
   (WatchStore.kt comment: the shield starts with the session, so requiring it
   "would be circular"). A LIVE warrant therefore says nothing about the shield
   service at all — half of C-2's doors are outside its testimony.

And the freshness thresholds in-tree disagree: WatchStore's `STALE_AFTER_MS` is
45,000 ms (calibrated for permission boards), the shield loop beats every 2 s
(`delay(2000L)`), and the finish machine's shield-gap law is 10 s (the session row's
`shieldLastBeatWallMillis`, latched by the VM ticker's `latchShieldGapIfStale`).
R-5 names **no threshold at all**. A derivation that borrows the 45 s board law lets
a dead shield loop print BLOCKING NOW for up to ~45 s after it stopped turning.
Note also the clock trap: the row's beat is a **wall** stamp; every print-age
derivation in WatchStore is **monotonic**. A derivation that subtracts one from the
other is wrong by construction, and nothing in R-5 warns the builder.

---

## HIGH

### C-5 `[NEW]` "RUNNING NOW" from `RoutineSchedule.isActiveNow` would print window math as a running session

R-5: *"Routine rows gain the RUNNING NOW marker from RoutineSchedule.isActiveNow,
which exists and is never called by the row."* True — and the reason it is never
called may be that it does not mean what R-5 spends it as. `isActiveNow`
(MonolithModels.kt:727) checks only `isLive` + "window contains now." It is blind to
everything `RoutineDerive.deriveOne` knows:

- **PAUSED** — `pausedUntilMillis` is never consulted by `isActiveNow`; derive
  returns `WindowState.PAUSED` for a window whose start the pause covers.
- **OPEN_HELD** — a different (manual) session occupies the window; derive refuses
  to materialise, `isActiveNow` still says true.
- **Already claimed / CLOSED_DUE** — the window's session ended early and was
  claimed; no second session is running or will run; `isActiveNow` still says true.
- **Never materialised** — derive emits no `DesiredSession` when the remaining
  window is under `MIN_REMAINDER_MINUTES = 5` (RoutineDerive.kt), when the seal was
  tombstoned (RoutineHeartbeat checks `isTombstoned` before materialising), or when
  no trigger has run yet: the chain alarm is `setAndAllowWhileIdle`
  (RoutineScheduler.kt:44) — inexact by design — and every other trigger
  (boot, resume, shield loop, opportunistic service events) requires the phone to
  wake or be used. State path, 1 AM, phone idle in Doze past a routine's start:
  window contains now, no session exists, marker prints RUNNING NOW.

The truthful source for "running" is the *session row's* `routineId` +
`windowStartMillis` against the live routine — the frozen snapshot law the rest of
the routines rebuild already obeys — or derive's `OPEN_RUNNING` state. R-5 points
the builder at the one function in the file that cannot tell the difference.

### C-6 `[NEW]` "ENDS 03:30" and the session label: R-5 names no derivation, and the tree offers three wrong ones

- An active row carries **no end stamp**: `endWallMillis` is written once, at the
  ending write (MonolithModels.kt, RC-01 comment). There is nothing on the row that
  says when it ends.
- The obvious formula — start + `targetDurationSeconds` — is the **per-block**
  target: `remainingSeconds` hits 0 at the *first block boundary* of a multi-block
  session (the `spanTargetSeconds` comment in MonolithModels.kt names exactly this
  lie on the desk clock). The honest figure needs the span derivation, plus
  penalties, plus — for a routine session — the frozen `windowEndMillis`, which is
  the only end a routine session actually keeps.
- A stopwatch session **has no end**: its stored target is a 20-minute floor
  (`isStopwatchMode`). Any `ENDS HH:MM` printed for it is invented.
- The label has the same hole. Session-name candidates disagree: the routine's
  frozen snapshot (`intention = routine.name`, `profileName = "Routine: <name>"`),
  the live routine row (forbidden by the frozen-snapshot law — a renamed routine
  would relabel a running session), and the LOADOUT cap (R-4 itself proves the cap
  shows his last *hand* pick, not the running loadout). A manual session's
  `intention` is free user text (default "Deep Uninterrupted Study") — printing
  `<intention> SESSION` verbatim is not a sentence, it's a text field wearing one.

The one correct derivation in-tree, `derivedEndWall`, is private to the ViewModel's
finish controller and is not part of Shield's contract. R-5's example is achievable;
R-5's silence about its derivation is how it won't be.

### C-7 `[NEW]` The qualifying sentence scrolls away from everything it qualifies

R-5: *"One line qualifies every SHUT beneath it."* Shield is one `LazyColumn`
(ShieldMatrixScreen.kt:262). The status sentence, placed as its top item per R-5,
scrolls off with the first swipe — while the SHUT/PEEK catalogue it qualifies sits,
by the audit's own measurement, thousands of dp deep (BLOCKED WEBSITES ≈6,100dp
pre-fix). A man standing at the catalogue reads unqualified SHUT states again, with
the qualifier a minute of scrolling away. Merge item 3 makes the *catalogue* header
sticky; nothing in R-1…R-13 pins the **status** line. A qualifier that does not
travel with the qualified is a footnote, and the audit's 1 AM test ("all three in
the first screen") only passes for a man who never scrolls.

### C-8 `[NEW]` Safe Break: during a break, "BLOCKING NOW" is false for exactly the tier the tally counts beside it

Both enforcers carve PEEK out during a Safe Break: the accessibility service
returns for `tier == PEEK_2M` when `activeSession.isInSafeBreak`
(MonolithAccessibilityService.kt), and the shield service's blocked-set filter
drops PEEK apps in a break (MonolithShieldService.kt). So for the whole of every
break, the wall is standing for SHUT and *open by design* for PEEK — while R-5's
sentence has no break segment and the tally directly beneath it counts PEEK apps in
the same breath. An unqualified BLOCKING NOW over a PEEK count is a false statement
about the PEEK door for ten minutes of every multi-block session. (Routine sessions
are single-block and escape this; manual sessions — the ones he arms by hand — do
not.)

### C-9 `[AUDIT R-6 — sharpened]` ACTIVE → ARMED trades one overloaded word for another, and the count still isn't "armed" in fact

R-6 renames the ROUTINES header word to ARMED. But ARMED is already spent, twice,
on *live-session* facts: the receipt prints `ARMED ${hhmm(receipt.armWallMs)}` (the
instant a session was armed — ReceiptSheet.kt), and the Deck's sealed-life section
is headed ARMED (TactileDeckScreen.kt, "SECTION 3: ARMED — session utilities").
On Shield it would mean "enabled and has at least one day." Same word, three
truths, one app — the precise offence R-6 prosecutes against ACTIVE.

The number under it is also weaker than the new word claims. `liveCount` =
`enabled && daysMask != 0` (acquitted as a *count derivation* — Dens A8 — and I do
not reopen the arithmetic). But it is blind to: the pause (`pausedUntilMillis`),
whether the chain alarm is actually armed (`RoutineScheduler.isChainArmed` exists
and no print reads it), and whether the heartbeat funnel is alive
(`lastRoutineTickMillis` is stamped every tick and no Shield print reads it). The
header would print "2 ARMED" over a chain that is, in fact, disarmed and
unreconciled. ARMED should be a claim about machinery, or the word should be one
that only claims schedulability.

### C-10 `[AUDIT R-6 / Fremd C10 / Muede F4 — sharpened]` daysMask = 0: one row, three contradictory prints — and R-6 doesn't say which one dies

Today a `daysMask == 0, enabled == true` routine simultaneously prints:

- header: **excluded** from the count (`liveCount`, ShieldMatrixScreen.kt:310 area);
- row meta: `NO DAYS SET` (`routineDaysLabel(0)`, ShieldMatrixScreen.kt);
- row cap: **`ON`** — the RoutineRow toggle prints `if (routine.enabled) "ON"`
  with no mask term.

R-6's cure — *"A daysMask=0 + enabled row prints WON'T FIRE, not ON"* — never names
the surface. Read one way it replaces the cap; read another it adds a fourth word
to the row and ON survives. A ruling a builder can implement two ways, one of which
keeps the lie, is not yet a ruling.
Entry path, for the record: `toggleRoutineDay` XORs the mask bit with no guard
(MonolithViewModel.kt) — one tap on the last lit day chip manufactures this dead
routine silently, mid-edit, with no print that the routine just stopped existing
for the scheduler (`nextBoundary` skips non-live routines entirely, so the chain
rearms as if the routine were never there).

---

## MEDIUM

### C-11 `[AUDIT R-1 / R-13 — sharpened]` Standing Orders: the duplication cure keeps three duplicates and one unstable jump

R-1's direction is right and I do not reopen it. But as specified:

1. **Duplicate counts survive.** The tally prints SHUT n · PEEK n · OPEN n; the new
   group headers print SHUT n / PEEK n / OPEN n again, one screen apart. R-13 —
   adopted in this same audit — forbids spending a surface twice; the numbers get
   two homes by the cure's own design.
2. **The jump targets are unstable indices.** The tally's words-as-jumps scroll a
   `LazyListState` to a group position — but everything above the catalogue is
   conditional: the ratchet banner (`AnimatedVisibility`), the reset note
   (`showResetNote`), the prune note (`pruneNoteText != null`), the slip fold
   under search, and R-3 itself inserting BLOCKED WEBSITES above ALL APPS. Every
   insertion re-indexes the jump. A jump that lands one section off is a silent
   misdirection — worse than no jump, because it arrives with confidence.
3. **Empty-group suppression + the wrong list = a vanished ruling.**
   `groupedForCatalogue()` already exists in-tree (AppRules.kt, builder pass):
   it drops empty groups, and its comment says the absence is "printed once, by
   the tally's zero." But the tally counts `allApps` while the catalogue is built
   from `filteredApps`. Under a category filter, a ruling with apps on the phone
   but none in the filter contributes no group and no zero anywhere near it — it
   reads as "no such ruling," when the truth is "filtered out." The builder must
   pin which list the groups derive from before this ships; the audit doesn't.
4. **The fallback has no expiry.** Dens's F1 fold is "the named fallback if Ren
   cuts merge scope." Nothing states when the fallback is revisited — so the
   probable end-state of a scope cut is the duplicate book, folded, permanent,
   with the audit's headline finding #2 closed on paper. Fallbacks in this
   programme need a named re-open trigger or they are destinations.

### C-12 `[AUDIT R-3 / Fremd C9b — sharpened]` The websites ADD false-success is structural: the field clears before the verdict exists

R-3 files "a rejected ADD prints why and does NOT clear the field." In this tree
that fix cannot be bolted on at the wording layer:

- ShieldMatrixScreen's ADD handler calls `onAddSite(newSiteText)` and then
  **unconditionally** `newSiteText = ""` in the same click — synchronously, before
  any result can exist.
- The ViewModel's `addBlockedSite` launches a coroutine and uses the repository's
  Boolean only to decide a sound; no result ever returns to the composable.
- The repository's `addBlockedSite` returns false when
  `BlockedBrowserDomains.normalizeDomain` rejects the input (no dot, contains a
  space) — a rejection no surface ever prints.

So "localhost," a typo'd domain, or a duplicate all vanish identically to a
success. Implementing R-3 requires changing the add contract to carry a result
back to the field — a contract change the audit's pricing ("fold ≈25 lines") does
not mention. Two adjacent prints ride along: the stored form is *rewritten*
(scheme/`www.` stripped, path kept) and never shown, so he cannot verify what will
actually be matched; and the section header `BLOCKED WEBSITES · n SITES` prints in
the enforced present tense even when the PARK browser-blocker toggle is off or no
session is running — the list is an *armed* list, not a *blocking* one, most of
the time it is read.

### C-13 `[DELEGATED — interim wording only]` The ±30 chips: delegated, but the interim must stop describing itself as an editor that merely lacks finesse

Per the cell's standing instruction, the ±30-minute chip item is **delegated to
Voss's batch-10 time wheels** (audit R-8); I propose no local cure and file nothing
against the wheels. What I file is against the *interim truth* while the chips
remain the shipped editor:

- **Silent refusal at the bounds.** `adjustRoutineTime` (MonolithViewModel.kt):
  if the resulting `rawSpan !in 30..1080`, the function simply `return`s — no
  sound, no print, no refusal. A tap at the 18-hour ceiling does nothing, at the
  control, with no word — the "invisible refusal" family the audit prosecutes as
  headline #5 for the ratchet, unfiled here for routines.
- **The label names a time; the effect is a span.** START +30 moves only the
  start (end fixed): the window doesn't shift, it *shortens*. Nothing on the row
  discloses that the two START chips and the two END chips are duration edits in
  one direction and position edits in neither.
- **The lattice itself** (20:00 → 20:40 unreachable) is the audit's finding #3 and
  stands as filed; the interim wording in any merge note must not call the chips
  "coarse but working" — at the bounds they are not working, and along START they
  are not doing what their labels say.

### C-14 `[AUDIT R-11 — sharpened]` The new sentences are not in R-11's semantics price

Verified: `grep -c contentDescription` on ShieldMatrixScreen.kt is **0** — the
audit's R-11 charge stands exactly as filed. Sharpening: R-11 prices "sleds, cap,
rows and chips." It does not name the merge's *new* prints — the status sentence,
RUNNING NOW, WON'T FIRE. All three are state that *changes while the screen is
open*; as plain `Text` with no state description or live-region behaviour, a
TalkBack user hears the state as of whenever focus last passed — or never. The
screen's most important new sentence would be its least accessible one.

### C-15 `[NEW — process]` The merge list prices its riskiest item cheapest, and R-13's budget rule is applied to dp but not to proof

Merge item 1 (status sentence) is listed first and priced "small." It is the only
item on the list that requires: a contract widening into Shield (C-1), a
multi-door proof derivation with a named freshness law (C-2, C-4), a third print
state the audit didn't specify (C-3), and an end/label derivation the audit didn't
specify (C-6). Items 2–13 are overwhelmingly in-file. Sequencing the cross-cutting
proof item first, at the smallest price, is how it gets built as specified — off
the boolean — by a builder working to the list. R-13's discipline ("name what it
removes to pay for it") is enforced for scroll depth and waived for epistemic
risk. The list should carry, per item, the *proof* it depends on, not only its
line count.

---

## What survives scrutiny (for the record)

- The services' witness design is sound and is the right foundation: session-id-keyed
  (`protectingSessionId`, `armedSessionId`), in-memory only, reset on every start /
  unbind / destroy — "persisted proof is stale proof" is honoured in the code, which
  is precisely why C-1/C-2 are charges against the *audit's recipe*, not the machinery.
- `liveCount`'s arithmetic (Dens A8) and `groupedByRule()`'s counts: acquitted.
  My charges are against the words and claims hung on them, not the derivations.
- R-1's destination (one enumeration), R-4's cap-as-act, R-7's refusal-at-the-point:
  no charges. C-11 and C-12 attack their specifications' gaps, not their rulings.

## Highest-severity summary for VOGT

**C-1, C-2, C-3, C-4 (CRITICAL) are one charge in four parts: R-5 as written will
print "BLOCKING NOW" from a database flag, in a binary with no unproven state,
using proof sources whose freshness is minted rather than measured — the audit's
headline fix is, as specified, a false-assurance machine.** R-5 must be amended
(third print state; session-id-keyed witnesses for each door the sentence attests;
a named freshness law; a named ENDS/label derivation) before it is built.
**C-5 (HIGH)** is the same defect one row down: `isActiveNow` cannot carry
RUNNING NOW.

---

— SALZ (CRITIC), for VOGT's issue #7 cell. Filed at
`studio/cells/issue-7/critic-salz.md` on branch `agent/issue-7`, DECLARED — no code
edited, nothing compiled, no tests run. Headline: do not build R-5 as written.
The audit found the missing sentence; its recipe for the sentence would print it
off `activeSession.isActive` alone — a persisted flag that both enforcers treat as
an input, not as evidence — in a two-form binary that has no word for the
unproven state the app elsewhere calls UNPROVEN. Four reproducible paths print
BLOCKING NOW over a dead wall: accessibility unbound, usage access revoked
(shield door blind while `protecting` stays true), shield loop dead inside the
45-second staleness law R-5 never names, and the cold-start unmeasured instant
(which the binary instead prints as a proven NOT BLOCKING). The same defect
repeats in miniature in R-5's RUNNING NOW marker: `RoutineSchedule.isActiveNow`
is window arithmetic that cannot distinguish PAUSED, OPEN_HELD, already-claimed,
tombstoned, or not-yet-materialised windows from a running session. Amend R-5
before merge item 1 is priced, sequenced, or built; the remaining charges
(C-6…C-15) are filed in the critic record with evidence and severities.
