# LIMIT CENSUS, ONE LAYER UP — UI + services, trunk, Oct 29, 2026 (~23:25 IST)

Filed by MARKO (MaxImpl — loadout), the caps lane's approved NEXT (b),
seated on Sarthak's order ("finish that queue, don't half-ass it").
Companion to plan/LIMIT-CENSUS.md, which cleared the data layer
(MonolithDao + MonolithRepository). This pass takes the same
instrument to everything above it: `ui/`, `service/`, `MainActivity.kt`,
plus the four small store files census 1 never touched
(`data/WallAccrual.kt`, `data/UndoDeleteStore.kt`, `data/DayWordStore.kt`,
`data/ReceiptState.kt` — stores, not the DAO; named here so the
boundary is explicit, not smuggled).

Method: grep passes over the whole tree for `.take(` / `takeLast` /
`drop(` / `subList(` / `chunked(` / `windowed(`, `maxLines` /
`TextOverflow.Ellipsis`, MAX/CAP/LIMIT/WINDOW/COOLDOWN constants,
bounded queues and size guards, retry/attempt counters, sampling
operators — then every hit read in context, and the files where caps
concentrate read whole (WatchStore, RoutineHeartbeat,
MonolithShieldService, MonolithNotificationListener,
ReleaseRetryScheduler, UndoDeleteStore, DayWordStore, WallAccrual).

Classes (this pass's law, per the brief):
- **HONEST** — a real bound, printed or provable from the surface
  that lives under it.
- **SILENT** — a bound the user can hit without ever being told.
- **FALSE** — a number that claims to be one thing and is another.

Batch 8's lesson binds: a cap is not a crime; a cap that lies about
the record is.

Headline fact, verified by grep: **there is no `.take(`, `drop(`,
`subList(`, `chunked(` or `windowed(` anywhere in `ui/`, `service/`
or `MainActivity.kt`** — the only Kotlin-side truncation in the tree
remains census 1's Repository name trim. No list the UI renders is
sliced in the UI layer; every list below renders whole or through
the Drawer's adjudicated paging.

---

## Charges — SILENT (3)

1. **service/RoutineHeartbeat.kt:46** — `DERIVE_LOOKBACK_MILLIS`,
   48 hours. The reconcile funnel reads its derive inputs through
   this window (L107–108: routine sessions + sessions started
   since `now − 48h`). The active session is read separately and
   uncapped, so a live session is never dropped — but a routine
   window that ended unfinalised more than 48 hours ago (phone
   off or dead across two days) falls out of derive()'s sight
   permanently: the row persists in the table, never finalised,
   never surfaced. The bound forgets to *act*, not to *store* —
   and no surface prints the window. The strongest SILENT row in
   this pass; the cure is Ren's ruling (widen, or derive orphans
   by an uncapped orphan-only read).
2. **service/MonolithAccessibilityService.kt:332** —
   `collectWindowTextLowercase` depth cap: `depth > 14` returns
   "". The Settings tamper check ("monolith" + a tamper word)
   reads the window text through this walk, so tamper text nested
   deeper than 14 levels is invisible to it. A detection bound,
   not a record bound — it can cause a *miss*, never a false
   print — and it is nowhere stated. (Its sibling walk,
   `isSelectedBottomTabShortsOrReels`, recurses uncapped.)
3. **service/TactileSoundEngine.kt:256** — the transient-sound
   command queue is bounded at 64 (`LinkedBlockingQueue(64)`),
   and the enqueue at L568 is `offer()` with the return value
   ignored: past 64 pending commands, UI sounds are dropped
   silently. Sound is not the record, and the seal's Throw never
   rides this queue (its own lane, L287's 1-deep prime signal —
   see HONEST 7) — but a man tapping fast hears the machine go
   quiet and is told nothing. Lowest of the three charges.

## Charges — FALSE (0)

None. Every number these layers print was traced to the fact it
names: the Guard line's first-lack (HONEST 9), the counts on
Shield/PARK (their truth is Truth Car 3's docket, and nothing in
this sweep found a *cap* distorting them), the shield
notification's "urges stopped" (an accrual counter, not a cap),
the receipt's lines (rendered whole — see Swept-clean).


---

## HONEST — real bounds, printed or provable (10)

1. **ui/views/BlockitCoreViews.kt:530** — the Deck wheel's
   duration range: 10–180 minutes in 5-minute steps
   (`coerceIn(10, 180)`). A control's hard stop, felt by the hand
   that turns it; it bounds what can be *set*, never what is
   remembered.
2. **ui/views/BlockitCoreViews.kt:388, :544** — the block count
   on the same wheel: 1–8 blocks (`coerceIn(1, 8)`). Same class:
   a range with a stop, printed by the dial itself.
3. **service/ReleaseRetryScheduler.kt:36** — the release-retry
   cadence is exactly two retries (+30 s, +5 min), then the
   Deck-open trigger is the only door. A retry *cap*, and an
   honest one: the cadence is stated in the file's law, its state
   persists on the faulted row itself (`releaseRetriesDone`), and
   the fault it serves is printed on the receipt at its instant.
   Named nuance for Ren's ruling, not a charge: no surface prints
   "the retries are spent" — the record carries the stamps for
   anyone who reads the row, but the app never says it aloud.
4. **data/UndoDeleteStore.kt:99** — the undo window, 5 seconds
   (`WINDOW_MILLIS = 5_000`), and the store's single slot: exactly
   one restore copy, because the undo banner is a single slot —
   store and presented affordance agree, and the ViewModel's
   rehydration (MonolithViewModel.kt:638) honours the same
   window after process death. A second delete inside the window
   displaces the first's banner and copy together — the promise
   and its backing stay the same size.
5. **service/WatchStore.kt:142** — `STALE_AFTER_MS`, 45 s: a PASS
   measurement older than 45 s of monotonic time prints as STALE.
   A staleness window that *announces itself* in the print —
   the opposite of a silent cap. (The store keeps only the latest
   measurement per item by design, in memory, with the no-op
   persistence named in its own header — a current-readings
   store, not a log; the append-only Readiness Log is the data
   layer's, Slice E's.)
6. **service/RoutineHeartbeat.kt:45** — the opportunistic
   throttle, 30 s (`OPPORTUNISTIC_THROTTLE_MILLIS`), with the
   shield loop's heartbeat cadence the same ~30 s
   (MonolithShieldService.kt:86 — every 15th iteration of the
   2 s loop). Sampling, honestly: the reconcile derives from
   *state*, never from event counts, and its coalescing flag
   loops once more rather than dropping a trigger — a throttled
   wake loses nothing.
7. **service/TactileSoundEngine.kt:287** — the seal prime signal
   is 1-deep: a second prime offered while one is pending is
   coalesced away. Honest by construction — a prime is
   idempotent; the pending one renders the same buffer.
8. **service/SessionSealTransition.kt:274** —
   `RECOVERY_YOUNG_WINDOW_MS`, 10 minutes: a marker-absent row
   older than this is classified sealed-under-an-older-build
   (L177–181). A classification window, argued in the file's own
   KDoc — the number says what it is. (Its sibling,
   `ANNUL_MS = 1500` at :267 — the proving-witness window whose
   annulment deletes the proving row — is the one data-eating
   mechanism above the DAO; it is NOT a new find: it stands
   charged in the no-fire audit and is being cured in Wache's
   lane, WORK-QUEUE 18 / F2 adopt-don't-delete. Cross-referenced
   here so this census cannot be read as having missed it.)
9. **ui/components/GuardLine.kt:40** — the header Guard line
   prints the FIRST lack only (`prints.first { … }`). A ruled
   print, per Sarthak's ratified design ("GUARD n/4 — <first
   lack>"); the full board lives in PARK. One lack named, the
   count honest beside it.
10. **The Drawer paging, consumed honestly** — the data layer's
    50-row pages (census 1, rows 5–6) are accumulated whole by
    the ViewModel (MonolithViewModel.kt:1707–1728, :1743–1757)
    with `hasEarlier` printed as an EARLIER row on screen —
    paging the user can see and pull, never a silent ceiling.

## Product numbers, ruled — not caps (grouped)

- **Peek lease 120 s + cooldown 20 min** — enforced at
  MonolithAccessibilityService.kt:209 against the stored lease
  end; printed by the airlock's price line (SIL-2). Ruled
  product rules (Freya's PILL-AND-TIMES §§3–4; their
  editability is Truth Car 3's times slice), not truncations.
- **The week** (data/MonolithWeek.kt) — Monday 00:00 anchor, one
  break per week: calendar definitions, Token-Spec §3.
- **Safe Break 600 s / stopwatch 20 m floor** — session-shape
  constants printed on the surfaces that run them
  (MonolithShieldService.kt:126–131).

---

## The ellipsis line — argued (7 sites, 0 charges)

A truncated FACT is a census row; a truncated subtitle is not.
The line I drew: a task's title IS a fact — so every title
ellipsis was checked for a whole-printing surface behind it.
Every one has one. The Tasks queue leaf prints its title
uncapped; the Drawer's stored-task detail prints `detail.title`
with no `maxLines` at all (DrawerScreen.kt:242). What remains is
list-row furniture, one tap from the whole fact:

- **ui/screens/DrawerScreen.kt:902** — stored-task row title,
  2 lines + ellipsis; detail at :242 prints it whole.
- **ui/screens/TactileDeckScreen.kt:746** — the running task's
  title, 1 line; the same task prints whole in Tasks.
- **ui/screens/FocusSequencerScreen.kt:286** — the undo banner's
  "REMOVED: <title>", 1 line, inside a 5-second transient; the
  restore copy holds the title field-for-field.
- **ui/screens/FocusSequencerScreen.kt:339** — last-finished
  title, 1 line, struck through; a pointer, not the record.
- **ui/components/MonolithCustomComponents.kt:223, :622, :629**
  — badge and pill-stamp words, 1 line: labels, not facts.

Verdict on the group: decoration-class. If Sarthak's task titles
grow past two lines in the Drawer *rows*, the row is where he'd
feel it first — flagged here so the choice stays visible, not
buried.

## Swept clean — named absences (so the census is complete)

- **service/MonolithNotificationListener.kt** — no length cap on
  captured title/text (trim only); every suppressed notification
  from a non-OPEN app is inserted. The digest's only cap remains
  the data layer's ruled live-flow 100 (census 1, row 4), and
  the frozen digest reads the uncapped snapshot
  (MonolithViewModel.kt:806).
- **service/MonolithShieldService.kt** — one foreground
  notification (ID 4096), replaced in place each iteration: a
  surface, not a record. No counters are capped; the loop's only
  numbers are its 2 s cadence and the heartbeat's (HONEST 6).
- **service/MonolithAccessibilityService.kt** — no event queue,
  no dedupe window, no intercept throttle: every event is
  handled; relaunch relies on FLAG_ACTIVITY_SINGLE_TOP. Beyond
  SILENT 2, its walks carry no other depth/size bound.
- **data/WallAccrual.kt** — no caps at all; its `coerceAtLeast(0)`
  floors are arithmetic guards on deltas, and its whole purpose
  is that no tick is ever dropped or double-counted.
- **data/DayWordStore.kt** — the declared-words map is uncapped.
  The closed word list (REST / ILL / FAMILY) is a vocabulary law,
  not a cap.
- **service/SessionSealTransition.kt:89–91** — the claim/seal/
  tombstone sets grow one entry per session *for the process's
  life* and die with it — named here as the pass's one uncapped
  growth: process-bounded, entries are single facts, never a
  record. Not a charge; stated so nobody discovers it later and
  calls it one.
- **ui/components/ReceiptSheet.kt** — deflections and summary
  entries render whole (`forEach`, L139); the slip prints what
  the finish booked, unsliced.
- **data/CryptoIntegrityManager.kt** and **MainActivity.kt** —
  no caps, chunks, or count bounds found.

## NOT A LIMIT — grep noise, recorded

- **ui/views/MonolithRailView.kt:31** — `CAP_HEIGHT_DP = 44f`:
  a cap *rail's* height in dp. The word, not the deed.
- The `coerceIn(0, 255)` family across ui/views — alpha/colour
  clamps: rendering arithmetic, not bounds on anything kept.
- **ui/screens/FocusSequencerScreen.kt:422** —
  `leavesBehind.coerceIn(0, 3)`: the stack-edge drawing depth of
  the leaf behind — how many paper edges are *drawn*, not how
  many tasks exist.

---

## Verdict

Above the data layer the app keeps almost nothing silently:
**3 SILENT, 0 FALSE, 10 HONEST**, 7 ellipsis sites all
furniture-class, and the layers' lists render whole — the
truncation disease of the old data layer does not recur up here.
The one row that deserves Ren's early eye is SILENT 1: the
heartbeat's 48-hour derive lookback is the only bound in these
layers that can leave a piece of Sarthak's record permanently
unattended — stored, but never finalised, and never mentioned.
SILENT 2 and 3 are small: a detection depth and a sound queue.
Cures are Ren's ruling; nothing in this pass is actively eating
data tonight (the one eating mechanism, the seal annulment,
already has an owner and a lane — HONEST 8's cross-reference).

— MARKO, Oct 29, 2026
