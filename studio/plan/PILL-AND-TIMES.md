# PILL-AND-TIMES — the machinery spec for Sarthak's small things

**Author:** Freya (MaxImpl — desk clock), Oct 29, 2026, ~21:23 IST.
**Commission:** Ren, flat. Sarthak's order tonight: *a pill that comes when
you go on other apps and tells the time (session / break / peek), and break
time + peek time editable.* Six designed worlds proposed neither. This spec
is the machinery that stops them being wishes. Spec only — no code.
**Register home:** plan/SMALL-THINGS.md (Small Things team, Tief) — this
document is the machinery answer to the pill and editable-times items.
**Evidence standard:** every claim about today's code carries file+line.
Trunk = `~/workspace/clone-inspect/Focus-app`, app/src/main/java/com/example/…

---

## 1. TODAY, VERIFIED

### 1.1 The peek lease — 120 seconds, written once, enforced by clock

- The numbers are compile-time constants:
  `PEEK_DURATION_SECONDS = 120`, `PEEK_DURATION_MILLIS = 120 * 1000L`,
  `PEEK_COOLDOWN_MILLIS = 20 * 60 * 1000L`
  (data/MonolithRepository.kt:93–95).
- **Grant:** `startPeekLease` (data/MonolithRepository.kt:1091–1099) writes
  one absolute monotonic end — `leaseEndMono = nowMono + PEEK_DURATION_MILLIS`
  (:1093) — onto the app's Room row, and pre-schedules the cooldown anchor to
  the same instant, so the 20-minute cooldown is armed at grant, not at end.
- **Storage:** `BlockedAppEntity.activePeekUntilElapsedRealtime` and
  `lastPeekEndElapsedRealtime` (data/MonolithModels.kt:235–236); the write is
  `MonolithDao.updatePeekLeaseAndCooldown` (data/MonolithDao.kt:32).
- **Early end:** `recordPeekEnded` (data/MonolithRepository.kt:1105–1112)
  zeroes the lease and re-anchors the cooldown to *now*. Natural expiry
  needs no writer: both enforcers compare clocks.
- **Enforcement point 1 — the accessibility sniper:**
  `tier == PEEK_2M && nowMono < appRule.activePeekUntilElapsedRealtime`
  admits the app (service/MonolithAccessibilityService.kt:209–213).
- **Enforcement point 2 — the shield loop:** the same predicate removes a
  PEEK_2M app from the blocked set it polls every 2 s
  (service/MonolithShieldService.kt:170–171).
- **Cooldown derivation:** anchor = max(lastPeekEnd, activePeekUntil);
  remaining = anchor + 20 min − now (data/MonolithRepository.kt:1073–1079).
- **The offer is tier-blind in the way Una charged (UNA2-01, S1):**
  `canPeek` is computed from the app's tier + cooldown + a ViewModel flag
  only (ui/MonolithViewModel.kt:2100–2103); only the per-app path above
  ever reads a lease (plan/AUDIT-deck-intercept-claims.md:32–69).
- **The countdown is state without a surface (UNA2-06, S3 — verified in
  this tree tonight):** `start2MinutePeek`'s contract promises the lease
  *"with the Draggable Edge-Snapping Countdown Pill"*
  (ui/MonolithViewModel.kt:2125–2128). The state exists —
  `FloatingPeekPillState` (:110–118), its flow (:191–192), a 1 Hz ticker
  that decrements it and fires a detent every 15 s (:833–848), an early-end
  verb (:2150–2160) — and a full-tree search finds **zero composable
  consumers**: nothing renders it, so `end2MinutePeekEarly` is unreachable
  from any surface and a taken peek runs invisibly on a silent 120-second
  fuse (plan/AUDIT-deck-intercept-claims.md:174–186). Two further defects
  in the state itself: it **counts ticks instead of deriving from the
  lease end** (the tick-count disease this codebase has already burned
  itself on — data/WallAccrual.kt:24–38), and it lives only in the
  ViewModel's process while the lease it mirrors lives in Room.
- **The price is never printed (SIL-2)** and **a withheld peek gives no
  reason (SIL-3)**: the cooldown state exists
  (ui/MonolithViewModel.kt:125, :2112) and renders nowhere
  (plan/AUDIT-deck-intercept-claims.md:212–221).

### 1.2 The safe break — 600 seconds, three literal copies, wall-derived

- The named constant: `SAFE_BREAK_DURATION_SECONDS = 10 * 60`
  (data/MonolithRepository.kt:97). Session state:
  `FocusSessionEntity.isInSafeBreak` + `safeBreakSecondsRemaining`
  (data/MonolithModels.kt:277–278).
- **Entry:** when a block finishes and it is not the last block, the
  progress machine writes `isInSafeBreak = true,
  safeBreakSecondsRemaining = SAFE_BREAK_DURATION_SECONDS`
  (data/MonolithRepository.kt:1760–1773). Single-block sessions and
  stopwatch (FLOW) sessions never break. There is no skip/end-early verb.
- **Countdown:** wall-derived from the stored (remaining, lastTick) pair
  by `breakRemainingAfter` (data/WallAccrual.kt:40–49) inside the progress
  updater (data/MonolithRepository.kt:1724–1758); at 0 the next block
  starts (:1730–1750). Never decremented per call — two tickers read the
  same stored pair and agree.
- **The span formula counts (n−1) breaks — in literal 600s, three times:**
  `spanTargetSeconds = n · effectiveTargetSeconds + (n−1) · 600`
  (data/MonolithModels.kt:369), the cycle `effectiveTargetSeconds + 600`
  (:373), and in-break elapsed `(600 − safeBreakSecondsRemaining)` (:376).
  These literals are **independent of the constant at Repository.kt:97** —
  today the break has four sources of truth that merely agree.
- **Where break state prints today:** the Deck eyebrow
  `BREAK · ⌈remaining/60⌉ MIN` (ui/screens/TactileDeckScreen.kt:557); the
  shield notification's `Safe Break %02d:%02d • Block %d/%d`
  (service/MonolithShieldService.kt:123–127); the desk clock reads the
  *span* (ui/components/AirlockAndDisarmOverlays.kt:104–110) and by
  Nacht's law gives the break no rendering of its own there (:118–124);
  WatchStore mirrors `safeBreakActive` for the warrant
  (service/WatchStore.kt:164, :263–268, :429).
- **Enforcement during a break:** PEEK_2M apps unlock freely, lease or not
  (service/MonolithAccessibilityService.kt:202–205); the shield's blocked
  set excludes them likewise (service/MonolithShieldService.kt:170).
- **A dead promise on the shelf:** `StudyModePreset.breakMinutes`
  (data/MonolithModels.kt:60–69 — 5, 10, 15 across presets) has **no
  reader anywhere in the tree** (grep-verified tonight). No surface can
  honour it; the Deck's own comment records the preset is never read
  (ui/screens/TactileDeckScreen.kt:296). "Break length" has never had a
  real home. §3 gives it one.

### 1.3 The only surface in another app today: the shield notification

The shield foreground service (which runs exactly while a session is
active — it stops itself when none is, service/MonolithShieldService.kt:
100–105) rebuilds and re-posts its notification **every 2 s loop
iteration** (:145–148, delay at :193; `notify` on the same id, :233–236).
Verbatim, by state:

- **At start:** title `Monolith Locked`, text
  `Protecting your focus session...` (:61–66).
- **Timed session:** title `Monolith • <intention>` (:146); text
  `%02d:%02d left • Block <i>/<n> • <k> urges stopped` (:133–139, :147).
- **Safe break:** text `Safe Break %02d:%02d • Block <i>/<n> • <k> urges
  stopped` (:123–127, :147).
- **Stopwatch:** text `Stopwatch +%02d:%02d (20m floor) • <k> urges
  stopped` (:128–132, :147).
- **At completion:** title `Session Complete`, text
  `Unlocked desk toy ready in your Break tab.` — then the service detaches
  and stops (:116–121).
- **During a peek: nothing changes.** No state of the notification names
  a peek, a lease, or a cooldown.

The builder uses title + text only — no chronometer, no actions, no
big-text (:224–231) — on an IMPORTANCE_LOW, badge-less channel
(:196–208), `setOnlyAlertOnce(true)`, tap opens MainActivity. So today,
inside another app, his time lives behind the notification shade, at
2-second freshness, and his peek's time lives nowhere at all. That is
the whole of it.

### 1.4 The settings precedents (where §3's home comes from)

- **`GuardSettingsEntity`** — a single-row Room table whose own doc line
  reads *"Single-row guard settings the accessibility service must
  honour"* (data/MonolithModels.kt:669–695). Seeded on first use
  (data/MonolithRepository.kt:172–183, :755–756), read directly by the
  accessibility service with no UI in the loop
  (service/MonolithAccessibilityService.kt:179), exposed as a flow
  (:769) and via `guardSettingsRow()` (:1366–1367). The same row already
  carries the receipt № counter — the house home for small global facts
  that machinery must honour.
- **DayWordStore** — SharedPreferences-backed (data/DayWordStore.kt).
  UI-layer only; no enforcer reads it. Wrong home for a number two
  services must obey.
- **WatchStore** — process-memory `@Volatile` mirrors fed by the shield
  loop (service/WatchStore.kt:162–170). Dies with the process; a mirror,
  never a truth. Wrong home.
- **The DB is version 10 with a real migration chain** — 7→8, 8→9, 9→10
  registered (data/MonolithDatabase.kt:25, :189) and
  `fallbackToDestructiveMigration` is *gone* (:169). Any schema change
  is a real migration plus a chain test (the Slice E migration-chain
  test is the standing precedent).

### 1.5 The overlay permission — already declared, already watched, never used

- `SYSTEM_ALERT_WINDOW` is **already in the manifest**
  (app/src/main/AndroidManifest.xml:11).
- It is already a watched Guard item — `WatchItemId.OVERLAY`
  (service/WatchStore.kt:77, `counted = false`; in the PERMISSIONS sweep
  list, :85–87), measured fresh by `Settings.canDrawOverlays`
  (data/MonolithRepository.kt:597, :620–626), and the Guard FIX key
  already deep-links its grant screen
  (app/src/main/java/com/example/MainActivity.kt:351–356).
- It is **not** one of the counted four (ACCESSIBILITY · USAGE ACCESS ·
  DEVICE ADMIN · NOTIFICATION LISTENER) and must not become a fifth:
  the app blocks completely without it.
- And **nothing draws with it**: a full-tree search finds zero
  `TYPE_APPLICATION_OVERLAY` windows and zero `addView` calls. The
  grant is a watched, grantable, completely unused capability. §2
  prices the pill against exactly this fact.

---

## 2. THE PILL

### 2.1 What it is

A small floating pill, present only while a session is active and only
while another app is in front, whose single job is to print the live
number of the state he is in. Its faces, in priority order — a pure
derivation from stored ends, **never a counted tick**:

```
derive(session, appRows, nowMono, foregroundPkg):
  if session == null or !session.isActive        → ABSENT
  if foregroundPkg == own package                → ABSENT   (§2.4 Q1)
  if session.isInSafeBreak                       → BREAK
        remaining = breakRemainingAfter(session pair, nowMono)
  else if any row.activePeekUntil > nowMono      → PEEK
        that row's app name;
        remaining = row.activePeekUntil − nowMono
  else                                           → SESSION
        stopwatch → elapsedSeconds
        timed     → remainingSeconds (the Now Frame derivation,
                    data/MonolithModels.kt:353–355) + Block i/n
```

- **BREAK outranks PEEK:** during a break the PEEK_2M doors stand open
  without any lease (§1.2), so a lease's countdown would print a fuse
  on an open door — a false urgency. The break's clock is the true one.
- **One live lease, enforced at grant:** today nothing stops two rows
  holding future lease-ends (the only guard is the ViewModel's memory
  flag, ui/MonolithViewModel.kt:2102). The grant
  (`startPeekLease`) zeroes every other row's `activePeekUntil` in the
  same DAO write round — one extra update — so storage can never hold
  two live leases and the derivation's "max until" is a formality.
- **The pill is also the cure for UNA2-06's second half:** its PEEK face
  carries a ✕ that calls the existing early-end verb
  (`recordPeekEnded`, data/MonolithRepository.kt:1105–1112) — the first
  surface from which a peek can be handed back early. Its body tap
  returns to Monolith (the intercept's RETURN TO FOCUS semantics). It
  is never a launcher for the peeked app.
- **Draggable, edge-snapping** — the dead contract's own words
  (ui/MonolithViewModel.kt:2125–2128) — via a touch listener on the
  window; snap to the nearest vertical edge on release. Position is
  *not* persisted (residency-scoped delight; a persisted x/y is one
  more settings column bought for nothing — priced and declined).

### 2.2 The driver

`MonolithShieldService` owns a pill controller. Reasons, all verified:
the service already runs exactly while a session is active (§1.3); its
loop already reads the session row and all app rows every iteration
(service/MonolithShieldService.kt:100, :163); it already knows the
foreground package family through its usage-stats intercept query
(:176–188 — the pill needs the same read answered for *own-package?*
instead of *blocked?*, a widening of an existing query, not a new
sense organ).

- **Cadence:** a 1 s ticker coroutine inside the service, deriving from
  stored ends each tick and touching the window only when the printed
  string changes (during a countdown, every second; the window itself
  is created once). The shield's 2 s loop stays as is.
- **Battery, honestly:** the foreground service already holds the
  process awake for the session's whole life; the pill adds no
  wakelock, no new service, no polling of anything new — one small
  window repaints. The honest cost is a 1 Hz derivation over rows the
  service already holds, which is noise against the loop's existing
  Room reads every 2 s.
- **Death behaviour:** the window is removed in `onDestroy` and when
  the derivation first reads ABSENT for session-end. If the service
  dies, the pill dies with it — it can never outlive the shield that
  makes its numbers true. (A frozen pill printing a dead session's
  time would be UNA2-03's warrant-blindness in a new hat.)

### 2.3 The two routes, priced

**Route A — overlay window (WindowManager, `TYPE_APPLICATION_OVERLAY`),
driven as in §2.2.**
- *Permission:* **the marginal cost is near zero, and here is the
  proof, not the pitch:** the permission is already declared
  (§1.5), already watched, and already grantable through the existing
  Guard FIX flow. If he has granted it, the pill costs no ask at all.
  If he hasn't, it is one more toggle on a screen he already knows —
  stated plainly for what it is: *draw over other apps* is a real
  capability, the same class malware reaches for first —
  which is why the system gates it behind its own grant screen, and
  why this app's Guard already treats it as a watched item with a
  named FIX path instead of a silent ask. The pill spends a
  capability the app already holds a grant flow for; it introduces
  no new *kind* of power into the app.
- *Machinery:* one `WindowManager` handle and one small view,
  created, updated and destroyed by the shield service per §2.2; a
  touch listener for drag and the ✕ (§2.1). No new service, no new
  permission, no new dependency — the overlay APIs are framework,
  and the service that would drive them already runs.
- *What it cannot break:* the pill is a reader. It writes nothing
  but the early-end verb the airlock already owns (§2.1). If the
  grant is missing or revoked mid-session, `canDrawOverlays` reads
  false at the next derivation and the window never draws, or is
  removed — Route B, below, carries the time instead. Blocking
  never consults the pill; the pill's death is cosmetic by
  construction.

**Route B — the shield notification, upgraded (the floor).**

- The notification of §1.3 gains three things, all inside its
  existing builder and loop: (i) a **system chronometer** for the
  timed-session face (`setUsesChronometer`, counting down to the
  session's end) — the system's own ticking, not our 2-second text;
  (ii) a **peek face**: while any lease is live, the text names the
  peek and its remaining at the loop's 2-second grain — honest
  grain, printed as seconds; (iii) an **END PEEK action** on that
  face, wired to `recordPeekEnded`
  (data/MonolithRepository.kt:1105–1112) — the same verb Route A's
  ✕ calls.
- *Permission:* none. *Machinery:* builder fields on code that
  already re-posts every 2 s, one action PendingIntent, and a
  lease read the loop's row set already contains.
- *Why it is the floor and not the answer:* reading it costs him
  the shade — a swipe down, i.e. leaving the app he is in
  *mentally* before he can read the fuse he is *inside*. His ask
  was a pill that *comes*: present on the glass, uninvited, no
  gesture. Route B makes the time reachable in every grant state;
  it never makes it *seen*. A spec that shipped B alone would
  answer the letter of the order and miss its life — the same
  shape as today's notification, which already prints the
  session's time behind the shade and has never once been the
  thing he sees.

**Recommendation: Route A, with Route B's upgrades built as the
ungoverned floor beneath it.** A wherever the grant exists — at
near-zero marginal cost, §1.5's proof, not a pitch; B always, so
no grant state leaves him without a reachable clock and a
reachable hand-back. The two are not rivals: A is the glass, B is
the shade, and §2.1 is one derivation with two mouths. If the
ruling takes only one, take B first — it closes the silence
everywhere, including for a man who never grants overlays — and
record A as deferred, not declined, so the register knows which
promise shipped.

### 2.4 The never-list — open questions, each with a recommendation

- **Q1 — over Monolith's own surfaces, the Held Door included?**
  **Never — a hard rule, already in the derivation** (§2.1: own
  package → ABSENT). The app's own faces print their own numbers:
  the Deck has its strip, and the Held Door's baseline
  (plan/AUDIT-deck-intercept-claims.md, the silences SIL-1–3)
  already owes the door its own remaining-time print. A floating
  pill over them would double-print every fact and could sit on
  the door's verbs at the one moment the door must be
  unambiguous. Inside the app, the app speaks; the pill is for
  everywhere else.
- **Q2 — on the lock screen?** **No.** The pill reports a
  session, and the lock screen is the system's face, not the
  session's. The night surface already exists and is lawful
  there: the desk clock, which carries the span (§1.2). Two
  floating time-surfaces over a locked phone is noise wearing a
  uniform. The pill draws over apps, not over the keyguard.
- **Q3 — whenever a routine window is open, session or not?**
  **Never.** The pill reports *sessions*, not schedules. A SLEEP
  window at 19:58 summons nothing; when the routine arms a
  session, the session is active and the SESSION face applies by
  the same derivation — no special routine case exists and none
  is priced. A pill that appears for a *plan* would print a
  promise the machinery has not made yet.
- **Q4 — through a call?** **No.** While the in-call face is
  foreground the derivation reads ABSENT — the telecom package
  family joins own-package in the foreground test. A human voice
  outranks a fuse; the peek and the session keep their own
  clocks underneath, and the pill resumes when the call face
  leaves.
- **Q5 — the PEEK face: only over the peeked app, or wherever
  he wanders?** **Wherever the foreground isn't Monolith** —
  with the face always naming the peeked app (§2.1). During a
  lease the doors mostly hold anyway (§1.1: only the peeked app
  is admitted; the rest re-intercept), so in practice the face
  lives over the peeked app — which is the point: the fuse burns
  *on the glass of the app he is spending it in*. But if he
  steps to an admitted surface, the countdown follows him
  rather than vanishing: a fuse that hides when he looks away
  from it is SIL-3's silence in a new place.

---

## 3. EDITABLE TIMES

### 3.1 The home: two columns on the guard settings row

- `peekMinutes` and `breakMinutes` — two columns on
  `GuardSettingsEntity` (data/MonolithModels.kt:669–695). The
  case rests on §1.4: the row's own doc line is *"settings the
  accessibility service must honour"*; it is seeded on first use
  (data/MonolithRepository.kt:172–183, :755–756); the
  accessibility enforcer reads it directly
  (service/MonolithAccessibilityService.kt:179) with no UI in
  the loop, so the number a settings surface writes is the
  number the machinery obeys — no cache, no mirror, no drift.
  The row already carries the receipt № counter: small global
  facts that machinery must honour live here. That is the house
  pattern, not a new invention.
- **DayWordStore — argued against and priced.** Zero migration,
  zero schema risk — and disqualified by audience: it is
  SharedPreferences read by the UI layer only
  (data/DayWordStore.kt). No enforcer reads it. A peek length
  stored there would be a number the settings screen believes
  and the sniper never hears: UNA-03's disease — a settings
  fact that isn't one — rebuilt on purpose. The cheap home's
  price is the promise itself. Declined.
- **WatchStore — argued against and priced.** Also zero
  migration — and disqualified by lifetime: process-memory
  `@Volatile` mirrors (service/WatchStore.kt:162–170), fed
  *by* the truth and dead with the process. A setting must
  outlive the process that reads it; a mirror cannot be a
  source. Declined.

### 3.2 The stamp: breakSeconds on the session row

- The settings row alone is not enough for the break. The span
  derivations (data/MonolithModels.kt:369, :373, :376) must
  read **the session's own promise**, so `breakSeconds` is
  stamped onto `FocusSessionEntity` **at creation**: the
  creation write reads `breakMinutes` once and writes
  `breakSeconds = breakMinutes × 60` onto the row it is
  already writing.
- The three literal 600s in the span formula are replaced by
  the row's stamp — the four sources of truth (§1.2) collapse
  to one stored fact per session. The named constant at
  Repository.kt:97 survives only as the migration default's
  ancestor (§3.6).
- **The peek needs no stamp — it already has one.** The lease
  end written at grant (data/MonolithRepository.kt:1093) *is*
  an absolute stamp of the setting at grant time;
  `startPeekLease` simply reads `peekMinutes` instead of the
  constant when it computes that end.

### 3.3 Who writes, who reads

- **Writes:** one Repository setter per number — clamp per
  §3.5, then the DAO update on the single row, the write
  pattern the row's other fields already use. The *surface*
  that calls the setters (which screen, which control) is the
  winning face's presentation problem; this spec owns only
  that the write lands on the row and nowhere else.
- **Reads:** exactly two — `startPeekLease` at grant, and the
  session-creation write at stamp time. **No enforcer reads
  the settings.** The sniper and the shield loop keep
  comparing clocks against stored ends and stored pairs
  (§1.1, §1.2), exactly as they do today. That is the whole
  design: an edit can only ever reach a *future* grant or a
  *future* stamp, because no live fact is ever re-derived
  from the setting.

### 3.4 The mid-flight law — prospective only, by construction

Stated as law: **an edit prices the future; it never re-prices
the present.** Not as a guard clause someone must remember to
write, but as a consequence of §3.3. A live lease holds an
absolute end already written. A running break holds a stored
(remaining, lastTick) pair accrued by wall clock
(data/WallAccrual.kt:40–49), plus its row's stamp. A running
session's span was computed from its row's stamp at creation.
There is no code path from the settings row to any of those
facts, so no edit can move them. He can change his peek from 2
to 5 mid-peek and the peek he is *in* still ends on its
written end — the next peek takes 5. The session he finishes
tonight was armed with tonight's numbers, and its receipt
will say so truthfully.

### 3.5 Bounds — argued, not assumed

- **peekMinutes: 0–30.** *0 = peeks off* — a zero-length lease
  is never granted, and the offer derivation reads the same
  setting, so 0 silences the offer at its source: cleaner than
  a separate peek toggle, which would be a second fact that
  could disagree with the first. *30 as the ceiling:* a peek
  is an interruption measured against a block; past half an
  hour the word stops describing the act, and the opening
  becomes a hole the HOLD law pays for silently. The
  20-minute cooldown (§3.7) still anchors to the lease's end,
  so every length in range keeps an honest rhythm: spend,
  then cool.
- **breakMinutes: 0–60.** *0 = no gaps* — and the span stays
  honest at 0: (n−1) × 0 contributes nothing, single- and
  multi-block spans both remain true, and the break state is
  simply never entered (§1.2's entry write stamps the row's
  own number; at 0 the countdown derivation reads an
  already-ended break). *60 as the ceiling:* a gap longer
  than an hour between blocks is not a break in a day's
  chain, it is a new part of the day — and parts of the day
  are routines' job, not this number's.
- The setters clamp; they never reject-and-keep. A write of
  −5 lands as 0; a write of 90 lands as the bound. A settings
  row that silently kept an out-of-range number would be a
  lie the enforcers would then obey.

### 3.6 Defaults — today, bit-identical

`peekMinutes` defaults to **2**, `breakMinutes` to **10** —
the migration's column defaults are today's constants (§1.1,
§1.2), so a man who never opens the setting gets exactly the
app he has. Editability that changes behaviour by existing is
a tax on everyone who didn't ask; this one's existence is
free.

### 3.7 The cooldown — named out of scope

`PEEK_COOLDOWN_MILLIS` (20 minutes, data/MonolithRepository.kt:95)
is **not** made editable here. Editability was asked for the
peek and the break; extending it to every constant by momentum
is how a settings row becomes a junk drawer — and the cooldown
is the peek's *price*, not its length, a different moral
category. What the cooldown is owed is not a dial but its
**words**: SIL-2 (the price printed before the peek is taken)
and SIL-3 (the reason printed when a peek is withheld). Those
sentences ride Truth Car 3's airlock edits (§4.3), with their
values read from this spec's store and the existing cooldown
derivation (data/MonolithRepository.kt:1073–1079), never
hardcoded afresh. Should he ever ask for the cooldown's length
too, the home already exists: a third column, granted under
the same stamp-at-grant law (§3.2) — one line of migration,
priced here so the refusal costs nothing later.

---

## 4. SEAMS

### 4.1 One migration — renumbered 11→12 *(addendum; see the restoration note)*

- As first filed, this section specced one joint migration
  10→11. In the interval, **Truth Car 2 took 10→11** (its
  `endWallMillis` + `receiptNumber`), so the joint migration
  is renumbered **11→12**. Everything else in this section is
  as filed.
- The migration carries, in one step: Truth Car 3's
  GuardSettings columns (UNA-01's SHORTS flag, UNA-03's sound
  flag) **and** this spec's three columns — `peekMinutes`,
  `breakMinutes` (guard settings, §3.1) and `breakSeconds`
  (session row, §3.2). Pre-existing session rows stamp
  `breakSeconds = 600` in the migration: the only break any
  of them could have been created under (§1.2's constant) —
  true by construction, not estimated.
- **Why joint:** the gate's one-build rule means one approved
  build carries everything anyway; two migrations in that
  build would be two chain-verifications for one schema
  step's worth of change. The migration-chain test (§4.4)
  extends once, to 12. And the *order* is the seam stated
  here so the merger doesn't improvise it: if Car 3's columns
  somehow land first under their own number, this spec's
  columns take the next — the joint step is preferred, the
  sequence is the law, the numbers are bookkeeping.

### 4.2 UNA2-06 closes in two halves — not closable by Car 3 alone

The charge — a taken peek runs invisibly: no countdown on any
surface, the early-end verb unreachable — has two halves by
geography. **The in-app half:** the lease's remaining printed
on Monolith's own faces (the airlock at grant, the Deck
during a live lease), riding from Truth Car 3's neighbourhood
of airlock and settings edits. **The overlay half:** the pill
over the other app (§2), which is this spec's. Neither closes
the charge alone: an in-app countdown leaves the peek
invisible exactly where it is spent; the pill alone leaves
Monolith's own faces without the number Q1 forbids the pill
to carry for them (§2.4). Re-verification splits by surface
to match: Una re-verifies the in-app half against Car 3's
landing; the overlay half verifies on the phone bench (§4.5).
The charge's closure line must name both halves, or it does
not close.

### 4.3 SIL-2 / SIL-3 — Car 3's words, this spec's values

The sentences themselves — the price named on the offer; the
reason and the remaining cooldown named when the offer is
withheld — are airlock copy and ride Truth Car 3's edits.
Their *values* come from here: the price's length is
`peekMinutes` (§3.1); the withheld reason's remaining is the
existing derivation (data/MonolithRepository.kt:1073–1079)
over the row's anchors. One source per number: after this
spec, no surface in the tree may hardcode the peek's 2 or
the cooldown's 20 — the constants at Repository.kt:93–95
survive only as the defaults' ancestors (§3.6, §3.7).

### 4.4 Tests — enumerated per piece

All unit-side, in the standing harness families (migration
chain, derivations, setters). The overlay's behaviour is
deliberately absent here; it is §4.5's.

1. **Migration chain, to 12:** a v11 database migrates
   clean; `peekMinutes = 2` and `breakMinutes = 10` land on
   the settings row; pre-existing session rows carry
   `breakSeconds = 600` (§4.1).
2. **Span at non-default stamps:** sessions stamped
   `breakSeconds ∈ {0, 300, 3600}` — span target, cycle
   length and in-break elapsed all read the stamp at each
   value; at 0 the span is the bare block sum. The three
   literal 600s are gone from MonolithModels.kt — the grep
   is the test's companion, run once at landing.
3. **Lease at non-default settings:** `peekMinutes = 5` → a
   grant writes end = now + 300 000 ms; `peekMinutes = 0` →
   no grant, no offer, no row touched.
4. **Mid-flight immovability (§3.4's proof):** grant at 2,
   edit to 30 mid-lease — the stored end does not move.
   Create a session at break 10, edit to 60 mid-break — the
   pair, the stamp and the span do not move. The *next*
   session stamps 60.
5. **The pill derivation, as a table:** §2.1's function over
   its cases — no session → ABSENT; own package → ABSENT;
   break and lease both live → BREAK; stopwatch → elapsed
   face; lease on row B, foreground B → PEEK, B's name, B's
   remaining; grant-zeroing: after any grant, at most one
   row holds a future lease end.
6. **Setter bounds (§3.5):** writes of −5 and of 90 clamp to
   the bounds on both numbers; the row never holds an
   out-of-range value.

### 4.5 The phone bench — named now

The overlay-over-another-app behaviour is **phone bench**,
named here so the gated build's bench list carries it from
the start: the window actually drawing over a third-party
app; the PEEK face live over the peeked app during a real
lease; drag and edge-snap under a real thumb; the ✕ reachable
and obeyed; the window dying with the service and never
outliving its session; Q4's call-face absence during a real
call. The harness cannot prove any of it — Robolectric has
no other apps — and this spec does not pretend otherwise. It
joins the unproven-on-device list beside haptics, actual
blocking, and routine firing.

— Freya (MaxImpl — desk clock), Oct 29, 2026.

---

## Restoration note (Oct 29, ~21:38 IST)

As first filed tonight, this document ended mid-word in
§2.3, at line 258 — a write died in the memory weather and
the tail never landed. My handoff letter (mail/ren/2123) was
true of the spec and false of the file. Restored in full
above: §2.3's remainder, §2.4, §3 and §4, from the filed
handoff and Ren's queue record (WORK-QUEUE item 21), at the
original density; verified whole by a full re-read after the
restore. **One change from the original filing, marked in
place at §4.1:** the joint migration renumbered 11→12,
because Truth Car 2 took 10→11 in the interval. Nothing else
was rewritten — where the record and my recollection of the
filing could differ, the record governed.

— Freya
...[truncated 17971 chars]