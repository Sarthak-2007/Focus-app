# §8 — GUARD, DISSOLVED (seat draft)

*Drafted by Uhr, Designers (time / the Watch Store), for Sana.
Main body for MASTER-PLAN §8. Floor: the Guard ruling
(plan/GUARD-RULING-AND-DRAWER.md §1, ratified), MASTER-PLAN §0,
the REGISTER, Otis's Heartbeat Tap + Readiness Log
(invention/MAXIMPL-INVENTIONS.md; invention/maximpl-parts/09-otis.md)
and Ines's Live Wire (invention/maximpl-parts/02-ines.md). Where the
filed plans and the ruling collide, the ruling and the REGISTER
govern and the fight is named. Constants are the REGISTER's;
disagreements are flagged in place, never forked.*
**One sentence:** Guard is not a place — it is one store that
measures, four surfaces that read from it and nothing else, and a
wire the slide holds in its own hand at the moment of commitment.

## 8. GUARD, DISSOLVED — the Watch Store

**Floor:** GUARD-RULING-AND-DRAWER §1. The band is DECK · SHIELD ·
TASKS · LEDGER + PARK (§6 owns the band's geometry; this section
owns the truth the band's chrome line prints). No fifth stop, no
Guard room, no second permission board anywhere — Shield keeps the
working lists and gains no board (the CHAIN question is settled for
this branch, ruling §1).

### 8.1 Sealed-state law (this section)

While a session is sealed: **the store keeps measuring.** The
heartbeat beats, the bind hooks fire, the log appends — detection is
not a decision, and ignorance is not what HOLD requires. **Nothing
the store learns is announced mid-session** (his verdicts, §0:
Q7 stay silent + Q8 the clock runs — one law). The chrome Guard line
does not change, PARK cannot open, no knock, no word, no screen
change; every Android Settings launcher renders as print and
answers nothing (Hale's clause, carried by the ruling). The session
runs to its scheduled end. At the finish, the receipt reads the log
for the session's window and tells the truth once: a death recorded
inside the window books the session **broken, never as a clean
seal** — and the break is the machine's fault, filed beside the
chain-jams, never against him, and the weekly token is not spent
(he broke nothing). Mid-session renderings of Guard state freeze at
their seal-frame values; the store's *record* never freezes.

### 8.2 The store — one source, no copies

The **Watch Store** is the single source for every permission and
interceptor fact in the app. **No surface runs its own check and no
surface keeps its own copy** — a surface that queries the OS itself,
or caches a verdict past the render that consumed it, is a second
store and a build error (acceptance 1).

**Per item, the store holds exactly three things:**

- **value** — from the REGISTER's vocabularies only. Permissions:
  `GRANTED / MISSING / STALE`. Interceptors: `RUNNING / PAUSED /
  OFF`. No word crosses families; `STALE` is never a synonym for
  `MISSING` (STALE says the warrant expired; MISSING says a
  measurement returned a lack).
- **when measured** — the measurement's instant, kept twice for two
  jobs: monotonic (the age arithmetic reads it; wall clocks lie by
  adjustment) and wall-clock (the print reads it — PARK's
  `LAST PROVED <instant>`, §8.5).
- **evidence class** — how the measurement was made, one of:
  `BIND` (the service's own bind/disconnect hook fired — the
  service's existence is the fact) · `HEARTBEAT` (the Shield
  service's own beat published it, §8.3) · `GRANT CHECK` (a
  synchronous system query — Settings string / AppOps — made at
  Deck entry, at resume, or inside a press; a grant is not a
  process, nothing binds, and a grant can only change in Settings,
  from which every return is a resume, so the event set is
  complete) · `WIRE` (a Live Wire read inside the gesture, §8.6 —
  recorded for the seal's provenance, never reused as standing
  evidence).

**The counted four** (the chrome line's denominator, REGISTER:
4, board-defined) are the four readiness items behind the existing
count: **ACCESSIBILITY · USAGE ACCESS · DEVICE ADMIN ·
NOTIFICATION LISTENER**. **Interceptor items** carry the triad:
the Shield service itself (the blocking engine — `RUNNING` while
its heartbeat beats and it is protecting, `PAUSED` only under a
suspension he chose, `OFF` when it is not running or a parent grant
it depends on is `MISSING`/`STALE`). Interceptor state feeds the
strip's BLOCK cell and PARK's board; it does not enter the
denominator — the count is permissions, the triad is effect, and
the two never blob into one number.

**Never measured renders as absence.** Before an item's first
measurement lands, surfaces print its name and no state word —
blank-until-proven. A word printed before its measurement exists
is the original lie this section exists to kill. There is no
spinner and no fabricated default: the shipped cold-open all-false
0/4 snapshot dies with the poll engine (§8.3).

### 8.3 The engine — the Heartbeat Tap + the Readiness Log

The store is not polled into existence. It is **fed by the
services' own lives, and remembered as events.**

**The Heartbeat Tap (the writers).** Android already polls these
services — it is called binding. The system binds the accessibility
service and the notification listener when they are granted,
re-binds them after death, and calls their lifecycle hooks on every
connect and disconnect; this codebase leaves every hook empty. The
Tap wires them to one process-wide `StateFlow` (Otis's
`ReadinessBus`): accessibility `onServiceConnected` / `onUnbind` /
`onDestroy` write the accessibility item; the listener's
connect/disconnect hooks write its item; the device-admin
receiver's `onEnabled` / `onDisabled` write its item. **The tap
itself:** the Shield service's monitor loop already wakes every
**2s**, already computes the enforced package set, already runs the
foreground query — and today throws all of it away. One publish per
iteration puts it on the flow instead: during a session the
blocking facts are *continuously proven*, not sampled. Grants ride
the `GRANT CHECK` hooks of §8.2 (Deck entry + the existing
`ON_RESUME` observer). TIME is never on the bus and never in the
store: it is a pure derivation from wheel + routines state at
render, un-stale-able by construction (§8.5, the strip).

**The Readiness Log (the memory).** Every writer above also
appends one row to an append-only log on the `intercept_logs`
pattern — `(cell, value, atWallMillis, source, pressId)`, heartbeat
rows throttled to at most one per 10s per cell, pruned to a bounded
ring per cell. **State is a query, never a stored field:** the
latest row per item, derived at the instant a surface renders.
Because the truth lives on disk as timestamps, **process death
cannot strand a stale ON**: kill the app for a week, reopen — the
first frame derives the honest state with its honest age, no
special case, no cold-start default. The log is also the receipt's
witness (§8.1) and the repair path's measurer: a bind-flap
(`disconnected 03:12:07, reconnected 03:12:09`) is a property of
the record, so Suki's stability beat after a FIX is measured from
rows, not timed by hope (§2.6's hand-back stands: re-sweep ≤300ms +
500ms stability, 90ms cross-fade, total ≤890ms, no restored line).
Migration: the log rides **9→10, the plumbing batch** (REGISTER,
data — append/child table only, nothing existing changes meaning).

**What dies, entire:** Rosa's 20s standing poll and the whole
trigger table it rode on (Deck entry / resume / "permission
broadcasts" — a trigger that exists nowhere in the tree / 20s
cadence) as a *data mechanism*; `refreshSystemGuard()` as chrome's
feed (its heavy sweep — HMAC, clock-drift, DAO — survives only as
PARK's on-open diagnostic, a screen, not a feed); every stored
STALE flag; the all-false cold-start snapshot. What survives of
the filed strip is its surface and stamp grammar (§8.5) — the
engine under it is this one, by correction to §2.6. Composed price
(Otis): ≈230 lines, no new timer, no new permission, no DB change
beyond the 9→10 table.

**The honest gap, printed:** the Tap's anti-case stands — an OEM
build that kills a service *without* firing its disconnect hooks
leaves a set bit standing over a corpse on a *standing* surface.
Two defences are structural, not hopeful: the log's freshness ages
in public and crosses into STALE on schedule with no loop running
(§8.4), and the one place ON is load-bearing — the seal — never
reads standing state at all (§8.6). If the device trial shows
silent kills on his phone, the stamp's evidence grade drops and
the Warrant (Ines's A, REGISTER) is the standing fallback; its
1500ms exists exactly once, there, and nowhere else.

### 8.4 STALE — a timestamp query, never a state

`STALE` is **derived at render from timestamps; nobody sets it, no
timer owns it, no loop maintains it.** It cannot fire late, fire
twice, or be left standing by a dead process.

- **The predicate.** An item reads `STALE` when there is **no PASS
  row younger than 45s** for it — its latest measurement is a lack,
  an unknown, or a pass whose warrant has expired. 45s is the one
  threshold, on every surface (the strip's filed 45s, generalised;
  the tighter board warrant in my invention file is superseded —
  one threshold or the word means two things).
- **Freshness never flatters.** Where an age prints, it is
  **ceiling-rounded to the next 5s** — a measurement 41s old prints
  as 45s old, never 40. Grammar (Rosa's, kept): `JUST NOW` under
  5s, `OVER 1M` past 55s. Rounding runs against the app, always:
  the stamp may overstate age, never understate it.
- **Bind silence is evidence, stated exactly.** For a bind-sourced
  item, a set bit with no disconnect event is a *continuously* true
  fact, not a measurement ageing since a connect that may be days
  old — stamping it `OVER 1M` old would be the poller's lie in
  mirror image. So a bind item's measured-instant refreshes at every
  re-validation read (Deck entry, resume, any render after a flow
  change) *on the grounds that the read found the death-event still
  absent* — and the log records that read as its row, so the
  predicate above still governs: if the reads stop (process dead,
  hooks silent), the rows stop, the age grows, and STALE arrives on
  schedule. STALE for a bind item therefore means precisely: *was
  bound; is not confirmed bound now.*
- **Recovery is silent.** One fresh measurement re-warrants the
  item; words re-strike in place on the next render. No "recovered"
  line, no banner, no hand-back ceremony — repair is measured, not
  announced (the PARK exception is the repaired row's own firm
  click, §8.5 — a sound on the row he fixed, not a summons).

### 8.5 What the store feeds — four readers, no fifth

1. **The chrome line (§6 owns its setting; this section owns its
   truth).** One quiet line, words only: `GUARD 4/4` when whole —
   no colour alarm, no motion, no sound, no pip (my invention
   file's chrome pip is superseded by the ruling's line; the count
   *is* the naming in chrome). The numerator counts only
   currently-warranted `GRANTED` items — a STALE item never
   inflates it. When something lacks, the line names the **first
   lack in board order** and nothing else changes:
   `GUARD 3/4 — ACCESSIBILITY OFF`. The instant never prints here;
   chrome is furniture, the instant lives in PARK.
2. **PARK's board (the sole detailed Guard home).** Header `GUARD`,
   beneath it `LAST PROVED <instant>` — the wall-clock instant of
   the latest complete measurement set, printed as an instant,
   never a duration (an instant is true forever and ages in the
   reader's eye; a duration begins lying the second it is struck).
   One row per item: name, its state word from §8.2's vocabularies,
   and a **FIX** key only where action is needed — FIX opens the
   exact Android pane; on return, each row re-strikes only when its
   own fresh query lands, and the repaired row alone gets the firm
   click; no fixed stagger, no restored banner. Below the board,
   in ruling order: Sound, then Theme as an `AUTO` readout naming
   the routine or schedule that owns it; the static spec plate may
   sit at the foot as identity. PARK opens from the band's end
   detent or by tapping the chrome line — **unsealed only** (§8.1);
   first run opens PARK directly and walks the permissions there.
3. **The strip (§2.6's surface, this engine).** Its BLOCK and APPS
   cells render from the store's flow — a subscriber cannot be
   late — with the stamp on the panel's seam in §8.4's grammar and
   evidence classes; its TIME cell is derived, never stored (§8.3).
   The strip is standing furniture, an instrument not a warning:
   silent flips, no haptics, it does not summon him. **A standing
   pass seals nothing** — the strip's job is what he knows before
   he touches the slide; the seal's evidence is §8.6.
4. **The finish receipt (the sealed reader, §8.1).** The only
   surface that reads the store *backwards*: the log rows inside
   the session's window decide broken-vs-clean, once, in human
   language. Nothing else in the app reads the log as history —
   the store is a witness for the receipt, not a record surface;
   the Drawer (§7) stores his tasks, never the machine's health.

**Explicitly not fed:** Shield (no second board — its working
lists are its own facts, not the store's), the Deck body (no
permission fact prints there beyond the strip and the chrome line),
and any notification, badge, or summons anywhere — a permission
death is named on the surfaces he touches next, never pushed at
him, in either the sealed or unsealed case.

### 8.6 The slide's in-path proof — the store and the wire divide the work

Two questions are asked of readiness, and they are not the same
question. The store answers *what is true, standing*; the wire
answers *is it alive in this hand, now*. Neither does the other's
job.

- **The store's half:** everything standing — the chrome line, the
  strip, PARK's board, the cause-words of a refusal (the sentence
  under the slide names the lack the *store* measured:
  `BLOCKING IS OFF — NOTHING WILL BE HELD.` /
  `BLOCKING UNVERIFIED — NOTHING WILL BE HELD.`, §2.6's split,
  unchanged). The store is never consulted *by the gesture as its
  verdict*: a standing pass, however fresh, seals nothing — that
  rule survives from Rosa's press-poll discipline, with its
  mechanism replaced below.
- **The wire's half (Ines's Live Wire, REGISTER-adopted):** the
  living services are held in `@Volatile` registries — the
  accessibility service's instance + its residency root-stamp
  (stamped on every event, after root acquisition, proving the
  pipeline processed events *from this Deck residency*), the
  Shield service's instance + `protecting` flag. The verdict is a
  **synchronous read on the gesture's own thread, ≤5ms, taken at
  DOWN and again at the 0.68 gate crossing** — four volatile/AppOps
  reads, no coroutine, no deadline, nothing to wait for. Wire live
  → full travel, the catch at **0.84** commits under contact. Wire
  dead on positive evidence → travel limit **0.65** (REGISTER's
  OFF-stop), the refusal sentence, the dull knock, 300ms hold,
  spring home; a wire that dies mid-drag collapses the limit on
  the next MOVE frame — the gesture degrades honestly instead of
  sealing on a corpse. Tap commits by the same road (DOWN reads
  the wire; UP at ≥90ms with wire live runs the mesh to the seat).
  `UNKNOWN` survives in exactly one narrow room — bind held but no
  event ever stamped this residency (fresh enable, straight to
  Deck): the class-G sentence, `CAN'T CHECK BLOCKING — TAP TO
  RETRY`, where the retry is a Deck re-entry that generates the
  events. A first-run state, not a morning state.
- **The seam between them, in one line:** the wire's verdict is
  spent by the gesture and written to the log as a `WIRE` row for
  provenance; the store's standing truth is what the verdict is
  checked *against* on every other surface — if wire and store
  ever disagree at a press, the wire governs the seal (it is the
  fresher, stronger evidence) and the disagreement itself is a log
  row the receipt can find. **The 1500ms deadline, the Park, and
  the waiting machinery of §2.6-as-filed are dead** (REGISTER);
  §2.6's strip already runs on §8.3's engine by correction, and
  its press-poll sentences survive as the wire's two wordings.

### 8.7 Acceptance checks (this section)

1. Grep: no status surface queries the OS outside the store; no
   surface caches a verdict past its render. One store or it
   didn't happen.
2. Kill the accessibility service with PARK open: the line's word
   decays to `STALE` by the §8.4 predicate (≤45s from the last
   PASS row), then `MISSING` on the next measurement; `LAST
   PROVED` does not advance on a partial or failed measurement;
   Δy = 0 everywhere.
3. Kill the app for a week, reopen: the first frame derives STALE
   from timestamps — no all-false default, no stranded ON.
4. Age print audit: every printed age is ceiling-rounded to the
   next 5s; no age anywhere rounds down; no duration-form age
   string (`AGO`, `LAST SYNCED`) exists in copy except the filed
   stamp grammar.
5. Seal audit: a standing store pass with a dead wire refuses at
   0.65; a live wire seals with the verdict read inside the press
   (≤5ms, DOWN and gate); the store alone never seals.
6. Sealed audit: kill blocking mid-session — chrome line
   unchanged, PARK unreachable, no knock or word; the session runs
   to its end; the receipt books it broken from the log, token
   unspent.
7. Count audit: the chrome numerator never counts a STALE or
   never-measured item; the named lack is the first in board
   order; interceptor state never enters the denominator.

— Uhr, Watchmaker, Designers. My Q8 verdict is this section's
sealed law (§8.1); my invention file's poll cadences, chrome pip,
stepping telltale and mid-session BROKE cut are superseded where
named above — the store, the decay, and the instant survive them.
