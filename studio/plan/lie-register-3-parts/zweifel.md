# LIE-HUNT 3 — ZWEIFEL, Critics seat 24 (doubt)

Ground: merged trunk ONLY — `Focus-app/app/src/main/java/com/example/`.
Files read in full: `service/LiveWire.kt` (98 lines),
`service/MonolithAccessibilityService.kt` (308),
`service/MonolithShieldService.kt` (253), `MainActivity.kt` at the
residency site, `data/MonolithRepository.kt` at `usageAccess()` /
`liveWarrant()` (L530–596). Every cross-reference below was re-grepped
against the whole `com/example/` tree, not the ground files alone.
Lane reports were not used as evidence. No code changed.
Inbox checked: `mail/zweifel/` — empty (only an empty `read/`).

Notation: `start` = `LiveWire.residencyStartMono`,
`stamp` = `MonolithAccessibilityService.lastRootMono`,
`bound` = `MonolithAccessibilityService.instance != null`.

---

## CHARGES

### Z-1 (S1) — The stamp is never cleared: a rebound accessibility service inherits its dead predecessor's proof and can read LIVE before processing a single event

`stamp` has exactly one writer in the whole trunk
(`grep -rn "lastRootMono\s*="` → one hit):

```kotlin
// MonolithAccessibilityService.kt:79
lastRootMono = SystemClock.elapsedRealtime()
```

No lifecycle method resets it. `onServiceConnected` (L55–59) sets only
the instance:

```kotlin
override fun onServiceConnected() {
    super.onServiceConnected()
    instance = this
    LiveWire.poke()
}
```

`onUnbind` (L61–67) and `onDestroy` (L300–307) both clear `instance`
and poke — neither touches `lastRootMono`:

```kotlin
override fun onDestroy() {
    if (instance === this) {
        instance = null
        LiveWire.poke()
    }
    scope.cancel()
    super.onDestroy()
}
```

And the residency test is a pure comparison against the surviving
value, `LiveWire.kt:80-83`:

```kotlin
fun stampedThisResidency(): Boolean {
    val stamp = MonolithAccessibilityService.lastRootMono
    return stamp > 0L && stamp >= residencyStartMono
}
```

The kill path, step by step: (1) user sits on the Deck, service stamps
(stamp ≥ start, wire LIVE-eligible). (2) The service unbinds and
rebinds — Settings toggle, system restart of the service, crash-rebind
— while the user never leaves the Deck, so no new `noteResidencyStart`
fires (its only caller is keyed on destination change; see Z-5).
(3) The new instance's `onServiceConnected` pokes; `stampedThisResidency()`
reads the *predecessor's* stamp against the *unchanged* start and
returns true instantly. (4) `liveWarrant()` can therefore return
`LIVE` for a binding that has processed zero events. This is a direct
breach of the wire's own definition, `LiveWire.kt:17-20`:

> "it has STAMPED an event from this Deck residency (the event
> pipeline processed this foreground, this visit - not last
> Tuesday's toggle)"

The stamp proves the *pipeline once ran in this process*, not that
the *currently bound* service ran. Process death does **not** save or
damn this either way — it zeroes both statics together (acquitted at
A-9); the leak is specifically across **rebind / instance replacement
inside one process**, which is the common failure mode of an
accessibility service. Fix shape (for Hale, not applied): reset
`lastRootMono = 0L` in `onServiceConnected` before/alongside
`instance = this`, and in `onUnbind`/`onDestroy` alongside the
instance clear, each followed by the existing `poke()`.

### Z-2 (S1) — In the merged trunk the verdict has no reader: `liveWarrant()` is never called and `snapshots` is never collected, while the comments name readers that do not exist

Whole-tree grep, merged trunk:

- `liveWarrant` → one hit: its definition,
  `data/MonolithRepository.kt:578: fun liveWarrant(): Warrant {`.
  No caller anywhere in `com/example/`.
- `LiveWire.snapshots` / `.snapshot()` outside `LiveWire.kt` → zero
  hits. The flow's only references are its own declaration and `poke()`:

```kotlin
// LiveWire.kt:91-97
private val _snapshots = MutableStateFlow(snapshot())
val snapshots: StateFlow<WireSnapshot> = _snapshots.asStateFlow()
fun poke() {
    _snapshots.value = snapshot()
}
```

- `Warrant` / `WireSnapshot` are constructed only at
  `MonolithRepository.kt:589` (inside the uncalled `liveWarrant()`)
  and `LiveWire.kt:85` (inside `snapshot()`/`poke()`).

Against that, the comments assert live consumers.
`LiveWire.kt:55-58`:

> "The strip and the chrome Guard line render this; nothing is
> cached, so there is no age to stamp and no STALE decay to compute."

`data/MonolithRepository.kt:567-570`:

> "The slide reads it at DOWN and again at the gate crossing; the
> verdict is never older than the gesture."

Neither reader exists in the merged code. Every `poke()` in both
services updates a `StateFlow` nobody collects; the one synchronous
verdict function is dead code. This also bounds Z-1's blast radius
today — the false LIVE is computed for nobody — but the register
records the trunk as it is: the Live Wire, as merged, is a registry
plus a proof ritual wired to nothing, described in its own comments
as load-bearing UI truth. Any future reader merged on the strength
of those comments inherits Z-1, Z-3 and Z-7 unpriced.

### Z-3 (S2) — The snapshot's stamped leg is unmasked: the strip fact can read `stamped = true` for a service that is not bound

The warrant and the snapshot compute "stamped" differently.
`liveWarrant()`, `MonolithRepository.kt:579-580`:

```kotlin
val a11yBound = MonolithAccessibilityService.instance != null
val stamped = a11yBound && LiveWire.stampedThisResidency()
```

`LiveWire.snapshot()`, `LiveWire.kt:85-89`:

```kotlin
fun snapshot(): WireSnapshot = WireSnapshot(
    a11yBound = MonolithAccessibilityService.instance != null,
    a11yStampedThisResidency = stampedThisResidency(),
    shieldProtecting = MonolithShieldService.protecting
)
```

No `bound &&` in the snapshot. Combined with Z-1 (stamp never
cleared), the moment the service dies its own teardown pokes a
snapshot of `a11yBound = false, a11yStampedThisResidency = true`
(provided stamp ≥ start), and that pair persists indefinitely —
until the next Deck entry re-notes the residency or a new stamp
arrives. Two surfaces computing the same named fact two ways, one of
them able to print "stamped this residency" over a dead service,
while the warrant over the same fields says OFF. The definitions
must be one: the snapshot should carry the masked leg the warrant
uses, or be charged as carrying a different fact under the same name.

### Z-4 (S2) — Before the first residency note there is no residency, yet any stamp in the process reads as "this residency" (start = 0 hole)

`LiveWire.kt:71-82`:

```kotlin
@Volatile
var residencyStartMono: Long = 0L
    private set
...
return stamp > 0L && stamp >= residencyStartMono
```

With `start = 0L`, the test degenerates to `stamp > 0L`: any event
the service stamped at any earlier point in this process — before
MainActivity's first composition, before the user ever saw the
Deck — satisfies the residency clause. The window is narrowed but
not closed by the defaults (ViewModel's destination starts at DECK,
`ui/MonolithViewModel.kt:83`, and the `LaunchedEffect` at
`MainActivity.kt:150-154` notes the start on first composition): the
accessibility service is already bound and stamping before the app
process composes anything, and any verdict read in that interval —
or in any future flow that reads the warrant before a Deck entry —
is LIVE-eligible on a stamp that belongs to no residency at all.
The `stamp > 0L` guard proves only "not the initial value"; it is
not a residency. Honest form: an un-noted residency (start == 0)
must read unstamped.

### Z-5 (S2) — The residency doc claims a note "on app resume"; no such call exists, so pre-background stamps survive a return unchanged

`LiveWire.kt:67-69`:

> "Monotonic start of the current Deck residency. Noted on app
> resume and on every Deck entry; the accessibility service must
> stamp at or after this instant…"

Whole-tree grep for callers of `noteResidencyStart` → exactly one,
`MainActivity.kt:152`, inside the destination effect:

```kotlin
// MainActivity.kt:150-154
LaunchedEffect(currentDestination) {
    if (currentDestination == MonolithDestination.DECK) {
        LiveWire.noteResidencyStart()
    }
}
```

The app's actual resume hook notes nothing. `MainActivity.kt:201-209`:

```kotlin
DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_RESUME) {
            viewModel.refreshSystemGuard()
        }
    }
    …
}
```

Consequence: background the app for hours, return to a Deck that was
never destroyed (destination unchanged → the `LaunchedEffect`, keyed
on `currentDestination`, does not re-run). `start` is still the
hours-old value, every stamp from before backgrounding still
satisfies `stamp >= start`, and the wire reads stamped/LIVE the
instant the app returns — before the pipeline has processed a single
event of *this* return. That is precisely the "processed this
foreground, this visit" proof the header (`LiveWire.kt:11-14`)
claims the design exists to provide, failing in the resume direction
while Z-6 shows it failing in the re-entry direction. Either the
doc's "on app resume" is struck, or the resume observer gains the
note; as merged, the sentence is false and the behaviour matches
the false sentence's absence.

### Z-6 (S2) — Deck re-entry does invalidate prior stamps (by design), but the note can also invalidate stamps from the visit it opens, and nothing guarantees a re-stamp

That re-entry invalidates is the stated design, and is acquitted as
intent. What is charged is the mechanism's two unpriced edges.

(a) **Retro-invalidation of this visit's own first stamps.** The
note runs in a Compose effect after composition
(`MainActivity.kt:150-154`); the Deck's window becomes the active
window — and therefore stampable — independently of effect
scheduling, on the accessibility service's thread. A stamp taken
between window activation and the effect's execution has
`stamp < start` the moment `noteResidencyStart()` lands
(`LiveWire.kt:75-77`):

```kotlin
fun noteResidencyStart() {
    residencyStartMono = SystemClock.elapsedRealtime()
    poke()
}
```

The pipeline *did* process this visit; the wire reads UNPROVEN
until a *second* event with a non-null root happens to arrive.
Nothing in the ground files schedules, polls or retries that second
event: `poke()` is only ever event/service-driven (see A-8's table),
and an idle, static Deck generates no event this code can count on.
The state is honest (UNPROVEN, not LIVE) but can strand there for a
whole visit.

(b) **Millisecond equality cuts the other way.** The test is
`stamp >= residencyStartMono` (L82) on a millisecond clock
(`SystemClock.elapsedRealtime()`): a stamp written in the same
millisecond *before* a note reads as stamped *at or after* it.
One millisecond of false-positive at every residency boundary —
small, but the comparison's precision is the clock's precision, and
the register notes it because the design's language ("at or after
this instant") claims more resolution than the code has.

Mid-session reachability of the invalidation itself is confirmed,
for the record: any Shield/Guard/Vault visit followed by a return
to DECK re-notes (destination change), e.g. the back path at
`MainActivity.kt:215-217` and the promotion path writing DECK at
`ui/MonolithViewModel.kt:749`. A live session's warrant therefore
drops LIVE → UNPROVEN on every Deck re-entry until the next stamp —
intended by the residency definition, priced here so Hale merges it
knowingly, with (a) as the defect riding on it.

### Z-7 (S2) — The shield's `protecting` flag is set by an unguarded coroutine loop: one exception kills monitoring silently and the flag then lies in whichever position it died in

`protecting` becomes true at exactly one place,
`MonolithShieldService.kt:117-120`, inside the monitor loop:

```kotlin
if (!protecting) {
    protecting = true
    LiveWire.poke()
}
```

The loop carrying it, `MonolithShieldService.kt:72-150`, has **no
try/catch anywhere**: `dao.getActiveSessionSnapshot()` (L74),
`repository.updateSessionProgress(activeSession)` (L81),
`dao.getAllAppsSnapshot()` (L123, L138),
`repository.queryForegroundBlockedApp(...)` (L136) and
`startActivity(...)` (L145) are all bare calls in a
`serviceScope.launch` (L72). Any exception from any of them ends
`monitorJob` — and no path restarts it except a fresh
`onStartCommand` (the loop's only starter, L62). The two *orderly*
exits do call `stopSelf()` (L77-78, L85-86 → `onDestroy` clears the
flag, L199); an exception exit does not: the service stays started
and foreground, monitoring is dead, and:

- if the exception lands after L118, `protecting` stays `true`
  **forever** — its only clearers in the trunk are the STOP action
  (`MonolithShieldService.kt:38`) and `onDestroy` (L199) — and the
  snapshot keeps faithfully reporting a protection that no loop is
  performing;
- if it lands before L118, `protecting` never becomes true although
  a foreground shield service is running.

Related wording charge (S3, folded in): the comment at
`MonolithShieldService.kt:114-116` claims the flag marks that "a
full monitor iteration has run"; the set at L117-120 sits *before*
the same iteration's foreground-block scan (L122-148) and its
`delay` — it marks a *partial* first iteration (session re-checked
and progress updated, notification posted). "Started is not
protecting" (L116) is true; "full iteration" is not.

### Z-8 (S3) — `WarrantState.UNPROVEN`'s own definition omits one of its two real causes, and mislabels the state as first-run-only

`LiveWire.kt:36-38`:

> "The bind is held but nothing has stamped this residency yet -
> a first-run state, not a morning state: CAN'T CHECK."

But `liveWarrant()` also reaches UNPROVEN with the stamp satisfied,
via usage: `MonolithRepository.kt:585-587`:

```kotlin
!stamped -> WarrantState.UNPROVEN
usage != UsageAccess.ALLOWED -> WarrantState.UNPROVEN
else -> WarrantState.LIVE
```

A bound, stamped device whose AppOps reads `MODE_DEFAULT`
(→ UNSET, `MonolithRepository.kt:556`) — or where `usageAccess()`
throws and falls back to UNSET (L559-561), or finds no AppOps
service (L538-539) — sits in UNPROVEN permanently, on a device of
any age. The state reached is honest (acquitted at A-5); its
documented definition is not: a reader trusting L36-38 will hunt a
missing stamp while the actual failed leg is usage. "First-run
state" is likewise wrong for the exception/never-asked paths, which
recur on any run.

### Z-9 (S3) — Every registry field is `@Volatile`, but every composite read is torn: the verdict can be assembled from two different moments

Filed under Q4 alongside acquittal A-6. `snapshot()`
(`LiveWire.kt:85-89`) performs three independent volatile reads
(instance, stamp, protecting); `stampedThisResidency()` (L80-82)
reads `stamp` and `start` as two separate field reads;
`liveWarrant()` (`MonolithRepository.kt:579-581`) reads `instance`,
then re-reads the stamp pair. Interleavings that produce a verdict
true of no single instant:

- stamp read (old, ≥ old start) → another thread notes a new
  residency (start := now) → comparison reads *old* start only if
  scheduled before the write; if it reads the new start the result
  is a false negative (safe direction), but the symmetric schedule
  — comparison's start-read winning the race against the note —
  returns `true` *after* the new residency has begun, on the
  previous residency's stamp. One-interleaving-wide, self-correcting
  on the next poke/read, and worth one line because the warrant's
  claim is synchronicity ("a synchronous read of their registries",
  `LiveWire.kt:11-13`; "never older than the gesture",
  `MonolithRepository.kt:569-570`).
- `liveWarrant()`: service unbinds between the L579 bound-read and
  the L580 stamp-read → a warrant asserting `a11yBound = true` for
  a service already gone (mitigated only by Z-1/Z-3's surviving
  stamp).

No lock, snapshot-token or double-read guards any of the three
composite sites. Severity S3: windows are interleaving-narrow and
every individual field *is* safely published (A-6).

---

## ACQUITTALS

### A-1 (Q1) — Stamp-before-return ordering: ACQUITTED, path by path

The stamp block is the first statement of the method,
`MonolithAccessibilityService.kt:69-84`:

```kotlin
override fun onAccessibilityEvent(event: AccessibilityEvent?) {
    // Live Wire stamp, taken BEFORE any early return below …
    try {
        if (rootInActiveWindow != null) {
            lastRootMono = SystemClock.elapsedRealtime()
            LiveWire.poke()
        }
    } catch (_: Exception) {
        // No stamp.
    }
```

Every return in the method, and the stamp's position relative to it:

| # | Return | Line | Stamp block precedes? |
|---|--------|------|-----------------------|
| 1 | `event?.packageName… ?: return` | L86 | Yes — L77-84 runs first |
| 2 | own-package / systemui `return` | L87 | Yes |
| 3 | `if (isCallActive()) return` | L90 | Yes |
| 4 | `?: return@launch` (no active session) | L94 | Yes — inside `scope.launch` (L92), launched after the block |
| 5 | `if (!activeSession.isActive) return@launch` | L95 | Yes |
| 6 | `return@launch` after Settings/installer intercept | L107 | Yes |
| 7 | `return@launch` after Shorts/Reels intercept | L119 | Yes |
| 8 | `return@launch` after browser-domain intercept | L139 | Yes |
| 9 | `?: return@launch` (no app rule) | L144 | Yes |
| 10 | `if (tier == OPEN) return@launch` | L146 | Yes |
| 11 | Safe-Break peek `return@launch` | L150 | Yes |
| 12 | peek-lease `return@launch` | L156 | Yes |

There is no return, throw-escape or branch entry before L77 in the
method, and the asynchronous body's returns are `return@launch`
inside a coroutine that cannot start before the synchronous stamp
block has executed. Rosa's ordering, as designed, is exactly what
merged. Two qualifications, neither of which breaks the acquittal:
the own-package and systemui events the block logic ignores at L87
*do* stamp (the stamp precedes that check too — intended, and stated
at L70-74); and returns 4–12 execute on the IO scope, so "precedes"
is launch-order, not completion-order — the stamp cannot be overtaken
by them because `stamp` is written before `scope.launch` is reached.

### A-2 (Q1) — The stamp's conditionality is defined, not smuggled: ACQUITTED

The stamp fires only when a root was actually acquired — `if
(rootInActiveWindow != null)` (L78), exceptions swallowed with no
stamp (L82-84) — and that is the field's own definition,
`MonolithAccessibilityService.kt:47-49`: "Monotonic stamp of the
last event whose root window was **successfully acquired**."
`LiveWire.kt:74-76` states the same in place ("A failed root
acquisition stamps nothing: the wire reads dead, which is the
truth."). The one loose wording — L73-74's "it is taken on every
event" — is immediately qualified by those same lines, and an event
with no root is, under the stated definition, an event the pipeline
did not process. No charge: a null-root event honestly leaves the
wire unstamped rather than forging proof from delivery alone.

### A-3 (Q3) — Shield-protecting is genuinely excluded from the pre-arm conjuncts: ACQUITTED

The state computation, `MonolithRepository.kt:582-588`, mentions
`protecting` nowhere:

```kotlin
val state = when {
    !a11yBound -> WarrantState.OFF
    usage == UsageAccess.DENIED -> WarrantState.OFF
    !stamped -> WarrantState.UNPROVEN
    usage != UsageAccess.ALLOWED -> WarrantState.UNPROVEN
    else -> WarrantState.LIVE
}
```

It is read exactly twice in the trunk, both times as a reported
fact *after* any verdict logic: copied into the returned `Warrant`
at `MonolithRepository.kt:593` (`shieldProtecting =
MonolithShieldService.protecting`, state already computed at
L582-588) and into the snapshot at `LiveWire.kt:88`. Whole-tree
grep for `protecting`/`shieldProtecting` returns no third reader —
nothing feeds it back into a gate, so the circularity the merge
ruled out (shield starts *with* the session; `LiveWire.kt:22-25`,
`MonolithRepository.kt:574-577`) is absent in fact, not just in
comment. Its *timing* is likewise as designed in outline:
`onStartCommand` sets the instance after `startForeground` returns
but does **not** set the flag (`MonolithShieldService.kt:51-61`);
the flag is set only inside the loop under a live, still-active
session (L74-87 precede L117-120) — subject to the mechanism charge
at Z-7, not to a circularity charge.

### A-4 (Q3) — OFF is reachable exactly when (no bind) or (usage DENIED): ACQUITTED, with the full table

`when` branches evaluate in order (`MonolithRepository.kt:583-587`),
so OFF ⟺ `!a11yBound ∨ usage == DENIED`, and no other input reaches
it — `stamped` is never consulted before the DENIED branch, and
`protecting` never at all (A-3). Full state table over the three
real legs (bound × stamped × usage; stamped is already masked by
bound at L580):

| bound | stamped | usage | State | Line |
|-------|---------|-------|-------|------|
| no | — | any | OFF | L583 |
| yes | — | DENIED | OFF | L584 |
| yes | no | ALLOWED / UNSET | UNPROVEN | L585 |
| yes | yes | UNSET | UNPROVEN | L586 |
| yes | yes | ALLOWED | LIVE | L587 |

LIVE ⟺ bound ∧ stamped ∧ (usage == ALLOWED) — exactly the conjuncts
in both headers (`LiveWire.kt:16-21`, `MonolithRepository.kt:572-574`).

### A-5 (Q3) — Bind-alive + usage UNSET reads UNPROVEN, and that is the honest verdict: ACQUITTED (outcome; its documentation is charged at Z-8)

`usageAccess()` is truly three-valued, `MonolithRepository.kt:554-558`:

```kotlin
when (mode) {
    AppOpsManager.MODE_ALLOWED -> UsageAccess.ALLOWED
    AppOpsManager.MODE_DEFAULT -> UsageAccess.UNSET
    else -> UsageAccess.DENIED
}
```

UNSET therefore covers never-asked (`MODE_DEFAULT`), a missing
AppOps service (L538-539) and any thrown failure (catch → UNSET,
L559-561). Downstream it can produce neither lie direction: never
OFF (only the positive `MODE_*` denials fall into the `else` at
L557 and reach L584) and never LIVE (L586 requires ALLOWED).
CAN'T CHECK is what an unset or unreadable AppOps *is*. The
conflation inside UNSET (never-asked vs. couldn't-read) is real but
harmless at the verdict level, since both meanings land on the same
honest state. The old Boolean collapse the comment at L533-535
describes is genuinely gone — `hasUsageStatsPermission()` (L564)
is a separate, correctly narrow consumer and does not feed the
warrant.

### A-6 (Q4) — All five registry fields are `@Volatile` and safely published: ACQUITTED (publication; composite atomicity charged separately at Z-9)

| Field | Declaration |
|-------|-------------|
| `LiveWire.residencyStartMono` | `@Volatile`, `LiveWire.kt:71-72` |
| `MonolithAccessibilityService.instance` | `@Volatile`, `MonolithAccessibilityService.kt:43-44` |
| `MonolithAccessibilityService.lastRootMono` | `@Volatile`, `MonolithAccessibilityService.kt:50-51` |
| `MonolithShieldService.instance` | `@Volatile`, `MonolithShieldService.kt:208-209` |
| `MonolithShieldService.protecting` | `@Volatile`, `MonolithShieldService.kt:216-217` |

Writers (accessibility callbacks / service lifecycle, main thread
and IO scopes) and readers (repository's synchronous warrant read,
snapshot reads) therefore see each other's *individual* writes
without stale-cache risk; the `MutableStateFlow` behind `snapshots`
(`LiveWire.kt:91-92`) is itself thread-safe for the cross-thread
`poke()` calls. Supporting detail, also acquitted: both services
guard teardown by identity — `if (instance === this)`
(`MonolithAccessibilityService.kt:62`, `:301`;
`MonolithShieldService.kt:196`) — so a dying predecessor instance
cannot null out its replacement's registration on the way down.

### A-7 (Q5) — Every registry write that exists is followed by a `poke()`: ACQUITTED for the enumerated transitions

| Transition | Write | Poke |
|------------|-------|------|
| A11y connect | `instance = this`, A11y L57 | L58 |
| A11y unbind | `instance = null`, L63 | L64 |
| A11y destroy | `instance = null`, L302 | L303 |
| A11y stamp | `lastRootMono = …`, L79 | L80 |
| Residency note | `residencyStartMono = …`, LiveWire L76 | L77 (after the write — snapshot recomputes against the *new* start) |
| Shield STOP action | `protecting = false`, Shield L38 | L39 |
| Shield start | `instance = this`, L60 | L61 |
| Shield protecting | `protecting = true`, L118 | L119 |
| Shield destroy | `instance = null` L197 / `protecting = false` L199 | L200 (one poke covering both writes) |

The conditional pokes on a11y teardown (inside `if (instance ===
this)`) are correct, not gaps: when the guard fails, the registry
value did not change, so there is nothing new to publish. Usage
access needs no poke: it is not a snapshot field (`WireSnapshot`,
`LiveWire.kt:59-63`, carries bound/stamped/protecting only), and
`liveWarrant()` re-reads AppOps fresh on every call
(`MonolithRepository.kt:581`) — a permission change in Settings
cannot strand the *warrant* (only the un-read flow of Z-2 goes
unnotified, and it carries no usage leg to strand). The orderly
monitor-loop exits (Shield L75-87) clear `protecting` only via the
`stopSelf()` → `onDestroy` path (L199-200) rather than in place —
a transient window between break and destroy, acquitted because the
destroy poke is the designed clearing point and fires under normal
service teardown; the *exception* exit, which never reaches it, is
the charged case at Z-7.

### A-8 (Q2) — Process death and reboot do not forge a stamp: ACQUITTED

The residency machinery is in-memory only — no write of
`residencyStartMono`, `lastRootMono`, either `instance` or
`protecting` to any persisted store exists in the ground files,
matching "Proof is never persisted" (`LiveWire.kt:13-14`). Process
death therefore resets `stamp` and `start` to `0L` together
(`LiveWire.kt:72`, `MonolithAccessibilityService.kt:51`), and the
`stamp > 0L` conjunct (L82) refuses the initial value, so a fresh
process cannot inherit a previous process's proof even though
`SystemClock.elapsedRealtime()` also restarts at reboot. The
in-process rebind case is the live hole and is charged at Z-1;
across process boundaries the design holds.

### A-9 (Q5) — `onInterrupt()` clearing nothing is correct: ACQUITTED

`MonolithAccessibilityService.kt:298: override fun onInterrupt() {}`
— an interrupt is a transient feedback interruption, not an
unbind; the service remains bound and `instance` remains truthfully
non-null through it. (Filed because a doubt-seat checks every empty
lifecycle method against the registry: this one owes no clearing.)
Likewise the shield's `onBind` returning `null` (L193) is a started-
service fact, not a registry gap — `protecting` is driven by
`onStartCommand` and the loop, never by binding.

### A-10 (Q2) — The residency comparison's direction is the safe one where it is merely racy: NOTED, no charge

Where Z-6(a)'s scheduling race and Z-9's interleavings resolve to a
verdict, they resolve to UNPROVEN (false negative — proof discarded)
in every interleaving except the one-millisecond equality and the
single start-read race named inside those charges. A wire whose
races overwhelmingly fail toward "can't check" rather than toward
LIVE is failing in the direction its own law prefers. Recorded so
Hale does not price Z-6/Z-9 as LIVE-forging defects; Z-1 remains
the only stamp charge that forges LIVE, and it is not a race — it
is a missing reset, deterministic on every rebind.

---

## Answers, one line per question

1. **Ordering** — the stamp block precedes every one of the 12
   returns in `onAccessibilityEvent` (A-1); its conditionality is
   the field's stated definition (A-2).
2. **Residency** — a previous *binding's* stamp reads as current on
   every rebind because `lastRootMono` is never reset (Z-1); a
   pre-note stamp reads as current because `start` initialises to 0
   (Z-4); the claimed app-resume note does not exist (Z-5); and
   Deck re-entry invalidation, though by design, can discard this
   visit's own first stamps with no guaranteed re-stamp (Z-6).
3. **liveWarrant** — protecting is truly excluded and read only as a
   post-verdict fact (A-3); OFF ⟺ (no bind) ∨ (usage DENIED),
   exactly (A-4); bind-alive + usage UNSET → UNPROVEN, which is
   honest in outcome (A-5) and misdocumented in definition (Z-8).
4. **Publication** — all five fields are `@Volatile` and the flow is
   safely published (A-6); only composite reads are torn (Z-9).
5. **Stale forever** — three ways: the snapshot's unmasked stamped
   leg after service death (Z-3), `protecting` after a monitor-loop
   exception (Z-7), and, beneath both, a flow and a verdict that
   nothing in the merged trunk ever reads (Z-2). Every *enumerated*
   transition does poke (A-7).

**Worst finding, one sentence:** `lastRootMono` is never cleared — not on unbind, not on destroy, not on reconnect — so a freshly rebound accessibility service inherits its dead predecessor's stamp and the wire can pronounce LIVE over a binding that has not processed a single event.

— Zweifel, Critics seat 24 (doubt), LIE-HUNT on the merged trunk
