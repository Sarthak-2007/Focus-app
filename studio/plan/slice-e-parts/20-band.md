# 20 — BAND (Improvers, the bottom nav) · SLICE E BUILD-READY
## The nav under Slice E: t=0 → Settle, the HELD FRAME, the E1200 CUT, the ENDED-FAULT sheet

*Seated by Ilya (Improvers coordinator) for the SLICE E BUILD-READY
wave. My joint is the band's **behaviour** under E3/E4 — what a band
tap does, what state the band is left in, and how he gets out of the
faulted finish. The floating band's geometry (the Finishers'
guillotine, FINISH-LIST-1 F-04) is chrome's build, not mine, and is
not priced here. All file:line against TODAY's trunk
(`~/workspace/clone-inspect/Focus-app`), read this run. No code
edited, no gradle run. Inbox at start: no unread letters (seven in
`read/`, all from the plan wave; Otis's end-detent fork in
`0532__from-otis` is superseded for this joint by the chrome ruling
below).*

**Governing chrome facts, taken as settled, not re-argued:**
the band is **DECK · SHIELD · TASKS · LEDGER + PARK** (MASTER-PLAN
§6.4; GUARD-RULING-AND-DRAWER: Guard dissolves, PARK is a utility
end-detent, not a stop and not a destination). The sealed rail's
mechanics are MASTER §6.5 (pin, dim, swallow, unlock on session
end's arrival frame) and my plan-wave Rung 3
(`plan/improvers/15-band-bottom-nav.md`), which §6.5 adopted in
form. E's clocks are Wren's (`slice-de-parts/wren.md` §3–§4, merged
at SLICE-D-E §2 E3/E4): t=0 = the proof-seal frame; E0 = the
repository completion flip on the StateFlow edge; HELD FRAME
E0→E1200; CUT 0ms at E1200; RACK 280 from R0 = max(E1200, P);
receipt t=0 E1480 happy / ≤E1800 all branches; ENDED-FAULT at
E1800 over the still-defocused Deck, rack completing behind the
sheet by ≤E2080.

**One correction to my brief, in the open:** the brief names
`MonolithCustomComponents.kt` as "the band's home." It is not.
The band's view is `MonolithBottomNavView` in
`ui/views/BlockitCoreViews.kt:1237–1419`; its host wiring is
`MainActivity.kt:484–510`. `MonolithCustomComponents.kt` carries
the header (whose session dot at `:227` is Horiz's B5 carrier,
E5b — not mine, not counted below) and the status drawer. Every
pin below is to the real sites.

**Headline verdicts**

1. **Today the band is never gated — not composed-out, not
   swallowed, not refused.** It sits in the root Column in every
   state, its tap path and the VM's `navigateTo` are both
   unconditional, and one tap at any moment from t=0 to E1200
   walks out of the sealed session's screen. HOLD is violated by
   the chrome itself, exactly as filed in the plan wave; E3's
   "input death enumerated — nav included" is a build, and §1
   prices the band's share.
2. **Under E the band dies by swallow at two layers** — the view
   consumes the touch and changes no state anywhere; the VM
   refuses the navigation before its sound. Dead means swallowed,
   never queued: §1.3 proves there is no store, animator, or
   back-stack in which a deferred navigation could live to fire
   after the finish.
3. **At the E1200 CUT the band lands exactly as the chrome ruling
   expects** — DECK selected, PARK empty, armed marker off, no
   animator pending — with one residue path that is mine to close:
   a drawer/PARK state left open at the seal survives it in
   today's architecture. The t=0 clear is priced in §2 (≈8).
4. **ENDED-FAULT: the lawful exits are the sheet's own dismissal,
   any band stop, and PARK — in that order — and the band must be
   live from rack end (≤E2080) even though the pawl stays
   seated.** Two filed texts collide here and one builder's
   misreading would trap him on the fault sheet with its repair
   unreachable; that is fork **F-B1** (§3.3), with my
   recommendation priced.

---

## 1 — Q1: THE BAND TAP — VERTIGO (t=0 → SETTLE) AND HELD FRAME (E0 → E1200)

### 1.1 Today: the band is composed, live, and ungated at every layer

**Composition.** The band is an `AndroidView` in the root
`Column` (`MainActivity.kt:484–510`), a sibling *below* the
`Box(Modifier.weight(1f))` that hosts the `AnimatedContent`
viewport — outside the screen, outside the Deck, outside
`SealShotState`'s reach. There is no session conditional on it
anywhere: it composes identically idle, proving, sealed, running,
and finishing. The Deck-local shot state (E3) cannot gate it by
composition, because the band is not in the Deck's tree.

**The tap path, traced end to end:**

| Hop | Site | What happens |
|---|---|---|
| Touch | `BlockitCoreViews.kt:1403–1417` (`onTouchEvent`) | DOWN is consumed (`return true`). On UP: `idx = ((x − 12dp) / (innerW/4))` — divisor 4, `innerPad` ignored (plan-wave debt, not E's). If `idx != selectedTabIndex`: the view **assigns its own `selectedTabIndex = idx` first** (`:1411`), fires `fireFeel(4L)`, then invokes `onTabSelected` |
| Host | `MainActivity.kt:497–505` | `0→DECK, 1→SHIELD, 2→TASKS, else→VAULT`; calls `viewModel.navigateTo(dest)` — no session check |
| VM | `MonolithViewModel.kt:369–379` (`navigateTo`) | **Unconditional.** Plays `playRotaryDetent(540.0)` (`:370`), assigns `_currentDestination.value = destination`, plus GUARD/VAULT side effects. No `activeSession` read, no finish-state read, no refusal branch |

So today, mid-session: **a band tap navigates, instantly.** The
`AnimatedContent` (`MainActivity.kt:272–278` in Wren's pinning;
the viewport in today's tree) crossfades away from the running
Deck to SHIELD/TASKS/VAULT. The session itself keeps running —
the row stays active, the services keep blocking — but the app's
sealed surface is abandoned by its own chrome, and the seal locks
nothing *in*: HOLD absolute is violated by the band, the header
GUARD pill (`MainActivity.kt:261`, same unconditional
`navigateTo`), and system back (`MainActivity.kt:216–217`,
`BackHandler` → `navigateTo(DECK)` from any non-Deck destination).
The band is my joint; the other two are named here because they
decide where the gate must live (§1.2, Layer 2).

**During the Vertigo specifically (t=0 → t=0+1050):** a tap at,
say, t=0+300 does all of the above *and* amputates the shot. The
Deck leaves composition mid-shot; E3's derive-don't-replay law
means on return the shot is simply over — its remaining 750ms
never existed. One accidental brush on the capsule spends the
seal's only film. Nothing in the shipped tree absorbs this:
Wren §2.2 verified "the code absorbs nothing mid-session today,"
and the band is the largest unabsorbed surface in the root.

**During the held frame:** today there is no held frame. The
finish is discovered by the 1s ticker
(`MonolithViewModel.kt:331–348`: `delay(1000L)` loop; the
completion branch at `:341–347` answers the flip with the *seal*
sound), the Deck recomposes to idle on the spot, and the band —
whose `isSessionArmed` feed (`MainActivity.kt:496`) has already
flipped false with the row — is live throughout, on whatever
destination the user's taps left it. E4 replaces the ticker edge
with the StateFlow edge; that is E4's build, but the band's
held-frame behaviour below depends on it landing.

### 1.2 Under E: dead = swallowed, at two layers

Either layer alone is a lie, for the reason Linea §3 established
on the task line: a view-only gate leaves `BackHandler`, the
header pill, and every in-screen `navigateTo` caller
(`MainActivity.kt:216, 261, 326–327, 408`) open; a VM-only gate
leaves the view's optimistic self-assignment (`BlockitCoreViews
.kt:1411`) free to travel the pill to a stop the app never
navigated to — the cap would print SHIELD for the session's whole
life, then snap back on the first host `update{}` after unlock:
a deferred visual navigation, fired by the finish.

**Layer 1 — the view swallows.** `MonolithBottomNavView` gains a
`isSealed` input (fed in §1.4). In `onTouchEvent`:

- DOWN, sealed: `return true`; exactly **one dull acknowledgement
  — `fireFeel(4L)`**, the latch waveform `[0,11,26,9]ms @
  [0,215,0,150]`, throttled to once per press (per
  `ACTION_DOWN`/`pointerId`; no repeat on hold, no per-MOVE
  re-fire). This is MASTER §6.5's swallow form, which governs
  over total stillness. No ripple exists in this view to
  suppress; no ink changes.
- UP, sealed: `return true` and **nothing else** — no
  `selectedTabIndex` assignment, no `animatePillTo`, no
  `onTabSelected` invocation, no PARK invocation (the PARK zone's
  tap/drag entries check the same flag before any cap travel
  starts — §6.7: while sealed, *no invocation path answers*).
- Semantics: while sealed, `contentDescription = "Navigation
  sealed — session running"` (§6.5), restored at unlock.

**Layer 2 — the VM refuses, before the sound.** `navigateTo`
(`MonolithViewModel.kt:369`) gains, as its first statement:

```kotlin
if (bandLocked.value && destination != _currentDestination.value) return
```

Silent refusal — no banner, no sentence (the held frame and the
cloister offer zero decisions; a refusal surface would be one).
The gate must precede `playRotaryDetent(540.0)` at `:370`: a
swallowed tap that still chirps a navigation detent has spent a
navigation sound on a navigation that did not happen.

**The lock predicate — the part a seal-only gate gets wrong.**
`bandLocked` is a VM `StateFlow<Boolean>` whose source is the
arm/finish controller's phase (Draht's `ArmProvingController`,
`slice-e-parts/05-draht.md` §4 — the same sealed phase type D8
reserves, extended by E4), **never** `activeSession.isActive`
alone:

- **Engages synchronously at the arm call.** The controller's
  first transition (→ PROVING) happens inside
  `armMonolithSession` (`MonolithViewModel.kt:909`) before any
  suspension point. This closes the one-frame race a flow-derived
  flag leaves open: `activeSession` is `repository
  .activeSessionFlow` (`MonolithViewModel.kt:280`), collected
  asynchronously — a tap UP landing between the repository insert
  and the flow's emission to the host would navigate under a
  flow-keyed gate and be refused under a phase-keyed one.
- **Covers PROVING → SEALED-RUNNING** (the Vertigo and the whole
  session). The pin's *rendering* still drops on the t=0 edge
  (§6.5: charged after the seal, never taxing ARM); the swallow
  engaging up to ~210ms earlier, at insert, is invisible and
  correct — he has just committed; keeping him on the Deck while
  proof completes is the gesture's own follow-through. If the
  cascade annuls at L+1500 (fork F7, row deletion), the phase
  returns to IDLE, the lock releases, and no pin ever dropped:
  no seal, no cloister, no residue.
- **Covers RELEASING until rack end** — not merely until E0.
  E0 flips `isActive` false; a session-only predicate would
  re-open the band *during the held frame*, the detention trap
  Linea §3 named for the task line, one surface over. The band's
  phases: E0→E1200 the lock holds and a band tap's one lawful
  effect is the **skip** (§1.3); E1200→rack end the lock holds
  under rack law (§1.3); at rack end (R0+280; E1480 happy,
  ≤E2080 faulted-behind-sheet) the lock releases and §6.4's law
  resumes from the next press. The pin lift at E0 (180ms, scale
  restore 260ms, §6.5/Linea §2.1) is the lock's *visible* half
  ending on the session-end frame; the travel lock outliving it
  by the rack's length is invisible, because during that window
  every lawful tap is already spoken for by the finish.

### 1.3 What a locked tap *does*, phase by phase

| Phase | Band tap's total effect |
|---|---|
| PROVING (latch → t=0) | Swallowed. One 4L ack per press. No skip exists yet — there is nothing to skip to |
| Vertigo (t=0 → +1050) | Swallowed. One 4L ack. The shot does not skip, pause, or notice |
| Sealed-running (rest of session) | Swallowed. One 4L ack. PARK cannot open; the Guard line (header, not band) is print |
| HELD FRAME (E0 → E1200) | Swallowed as navigation; **routed as the skip**: the host's `onBandTapWhileLocked` invokes the finish controller's skip, jumping straight to the E1200 cut (Wren §4.2 skip law: "one tap anywhere"). One tap, one 4L ack, one skip — the ack does not double as a second effect, and the tap is never *also* dispatched to any stop |
| RACK (E1200 → R0+280) | Swallowed. Wren's rack law ("taps during the rack are navigation taps; a reverse tap reverses it; nothing queues") was written for inter-screen racks where a navigation is in flight. The finish rack has **no navigation in flight** — origin and destination are both DECK — so rack law has no object here; swallow is the only non-lying reading. The rack completes on its own clock |
| Rack end → rest (E3130) | Lock released at rack end. Ordinary §6.4 law. Receipt supersession on nav-away is the receipt's joint (Linea §5.4), not the band's |

### 1.4 Proof that no deferred navigation fires after the finish

"Swallowed, never queued" is provable because every place a
navigation could be *stored* in this architecture is enumerable,
and the swallow writes to none of them:

1. **The view's own selection field** (`BlockitCoreViews.kt:1240`)
   and its animator (`:1282–1297`). The field is written by
   exactly two callers: the UP handler (`:1411`, gated by Layer
   1) and the host's `update{}` (`MainActivity.kt:493`, a pure
   derivation — item 3). `pillAnimator` is started only by the
   setter's `animatePillTo` branch (`:1245–1255`). Under the
   swallow neither fires, so at unlock **no animator is pending
   and `animatedPillIndex` still holds its seal-time value**:
   the cap is at DECK, because arming is a Deck act and §6.5
   pins it there ("the cap is at DECK when the seal engages").
2. **The VM's destination** (`MonolithViewModel.kt:83–84, 369`).
   `navigateTo` is a synchronous assignment to a `StateFlow`.
   There is no nav controller, no back-stack entry, no pending
   intent, no queue object anywhere in the tree. A refused call
   leaves the flow untouched; there is nothing to flush at E0,
   at rack end, or ever.
3. **Compose recomposition.** `update{}` (`MainActivity.kt:490–
   506`) re-runs with *current* values only: the derived
   `selectedTabIndex` (item §2.1), the live counts, the sealed
   flag. A swallowed tap wrote nothing, so no future recomposition
   — including the heavy recomposition at the E1200 cut — can
   replay it.
4. **Persistence.** Destinations and taps are never persisted;
   process death mid-session relaunches on DECK (VM init,
   `:83`) with the band at DECK. A killed app cannot deliver a
   queued navigation on relaunch.

Acceptance: test T-B2 (§5) samples all four stores across the
whole E window.

### 1.5 Price — Q1

| Item | Site | Net |
|---|---|---|
| View swallow: sealed branch in `onTouchEvent`, ack throttle, sealed `contentDescription` | `BlockitCoreViews.kt:1403–1417` | ≈18 |
| `isSealed` property + PARK-entry gate (chrome build's invocation sites in this view) | `BlockitCoreViews.kt:1272ff` | ≈7 |
| VM: `bandLocked` StateFlow derived from controller phase + `navigateTo` refusal before the sound | `MonolithViewModel.kt:83ff, 369` | ≈14 |
| Host: pass `isSealed`, route held-frame band tap to skip | `MainActivity.kt:490–506` | ≈10 |
| **Q1 subtotal** | | **≈49 net** |

---

## 2 — Q2: THE BAND AT THE E1200 CUT — SELECTED STATE, PARK, RESIDUE

### 2.1 The landing state, derived not assumed

`selectedTabIndex` is not stored state — it is re-derived every
recomposition from `currentDestination` (`MainActivity.kt:234–
240`: DECK→0, SHIELD→1, TASKS→2, VAULT→3, GUARD→−1). Under §1's
gate, `currentDestination` is DECK at latch, at t=0, at E0, and
at E1200 — it never moved. Therefore at the cut:

- **Selected cell: DECK (index 0)**, cap resting on the DECK
  segment, DECK word at full ink — the chrome ruling's expected
  landing exactly: he arrives on the idle Deck and the band says
  DECK.
- **PARK: unoccupied.** PARK is a position the cap travels to on
  invocation (§6.7), never a selection; nothing in the finish
  path invokes it, and §2.2 clears the one state that could fake
  it. No stop claims anything on PARK's behalf, and PARK claims
  no stop.
- **The rack-end "nav detent jump" is the identity.** Wren §4.4's
  rack token ends with "nav detent jumps (0ms) at rack end" —
  written for racks *between* destinations. Here origin =
  destination, so the jump moves nothing, and the shipped setter
  already guarantees it costs nothing: re-assigning the same
  index is a no-op at `BlockitCoreViews.kt:1244`
  (`if (field != clamped)`), so no 0-distance animator runs
  across the landing. A builder who implements the jump as a
  literal `animatePillTo(0f)` call bypassing the setter has
  invented a 260ms shiver on the landing frame; the test is
  T-B5.
- **Armed marker: off, and off since E0 — correctly.** The band's
  own session carrier is the filled centre on the DECK icon
  (`BlockitCoreViews.kt:1350–1354`: 3dp, `tokTextPrimary()`),
  fed by `isSessionArmed = activeSession?.isActive == true`
  (`MainActivity.kt:496`). MASTER §6.4: the marker "fires off at
  session end's arrival frame." Under E4's StateFlow-edge E0 it
  clears at E0 — during the held frame, while the picture is
  still the sealed world. That is not a contradiction: the
  marker is a *live session fact* rendered in chrome, not part
  of the held frame's frozen picture (which is the Deck's
  composition, Linea §2), and §6.5 lifts the pin on the same
  frame. At E1200 it is absent, as the ruling expects of an
  ended session. (The header dot at `MonolithCustomComponents
  .kt:227` is Horiz's B5 carrier — born-accent, re-toned at
  birth in E5b. It shares the E0 clearing fact; its tone is his
  joint and is not residue of mine.)

### 2.2 Residue audit — everything the finish could leave in the band

| Candidate residue | Verdict |
|---|---|
| Queued / deferred navigation | **None.** §1.4, items 1–4 |
| Pill animator in flight at the cut | **None.** No setter call with a changed value occurs anywhere in E0→E1200; the last travel the cap ever made was the pre-arm arrival at DECK |
| Cap parked off-stop (drag left mid-segment) | **None by construction in E.** The seal engages with the cap at DECK (§6.5); the band takes no drag while locked (§1.2), so no mid-segment rest state can be created during the session to be inherited at the cut |
| Session dot left lit | **None.** §2.1 — clears at E0 by its own feed |
| Badge counts moved under the landing | **Inherited chrome debt, not E residue — named so nobody charges it to E.** Today the band still draws `blockedCountBadge` / `tasksCountBadge` (`MainActivity.kt:494–495`; view `:1257–1270`, drawn `:1385–1400`), and `tasksCountBadge` is live-derived (`MainActivity.kt:230–232`). During the sealed window no mutation path is reachable (every writer is gated or on an unreachable screen), so at E1200 both numbers equal their seal-time values — the finish leaves them *unmoved*. But MASTER §6.4 forbids badges on any stop outright, and the Drawer ruling forbids any badge/count/dot on the LEDGER stop. Their deletion is my Rung 1 / the chrome build. If the chrome build has not landed when E builds, the landing inherits the badges; E neither creates nor worsens them |
| Stop *names* (FOCUS/SYSTEM/HORIZON at `BlockitCoreViews.kt:1239`; host `else→VAULT` at `MainActivity.kt:502`) | **Chrome-build debt, same status.** E's finish is name-blind: index 0 is selected under both vocabularies. The DECK·SHIELD·TASKS·LEDGER rename rides the chrome build, not this slice |
| **Drawer / PARK left open across the seal** | **REAL RESIDUE — mine to close, priced below.** §2.3 |

### 2.3 The one real residue: an open drawer at the seal

Today, `showUnifiedStatusDrawer` is a local `remember` at
`MainActivity.kt:200`, and *nothing* in the seal or finish path
clears it. The status drawer expands inside the Column above the
viewport — it does not cover the slide — so this sequence is
reachable today: open the drawer, arm from the Deck beneath it,
run the session, land at E1200 **with the drawer still open**.
The band's selected state is fine; the *room* it lands in is not
the idle Deck the cut promises.

In the chrome build the same shape returns wearing PARK: PARK's
indication is the cap parked in the zone, derived from
drawer-open state (§6.7: "the cap stays parked in the zone for
as long as the drawer is open — the drawer is visibly the cap's
cargo"). If that open-state survived a seal, the E1200 landing
would show the cap at PARK while the destination derivation says
DECK — two contradictory selections on one rail, the exact lie
§2.1's clean landing exists to avoid.

Why it is closable, and the close: §8 of MASTER (p. "PARK never
has to be closed *by* a seal — a session can only be armed from
the Deck") holds for the PARK *sheet* only if the sheet covers
the slide when open — true of a sheet, false of today's in-flow
drawer. The safe build does not rely on reachability arguments:

- **At the t=0 edge, the host forces every drawer state closed**
  — today's `showUnifiedStatusDrawer = false`, and the chrome
  build's PARK-open state likewise — with PARK's own close law
  (160ms exit, §6.7), concurrent with the shot, changing no
  destination and no band selection. This is part of E3's
  input-death enumeration: a surface that is open is a surface
  that can be tapped, and at t=0 everything tappable dies.
- This does **not** contradict §6.1's "a Drawer already open at
  the seal degrades to read-only furniture": that clause governs
  the *storage Drawer* (the LEDGER stop's destination content,
  §7.1 — a screen he may be *on*, made read-only). PARK is not a
  destination and has no read-only mode; §6.1/§6.7 give it
  exactly two sealed behaviours — cannot open, and no teasing a
  closed state. An open PARK sheet at t=0 therefore **closes**;
  it does not degrade. If Ren reads §6.1 as covering PARK too,
  that is fork F-B2 (§4) — the build differs by one branch, the
  price does not.

**Price — Q2:** t=0 drawer/PARK clear (host `LaunchedEffect` on
the sealed edge + hoisted close call) ≈**8 net**. Everything else
in §2 is derivation and existing guards: ≈0.

**Q2 verdict: YES** — with §1 built and the ≈8 clear, E's finish
leaves the band in precisely the state the chrome ruling expects
at the E1200 landing: DECK selected, PARK an empty position,
marker off, counts (where they still exist) unmoved, no animator
pending, no navigation owed.

---

## 3 — Q3: THE ENDED-FAULT SHEET — IS THE BAND LIVE UNDER IT, AND WHAT IS THE WAY OUT?

### 3.1 The state of the world when the sheet rises

Faulted branch (Wren §4.3): the reverse cascade halts on a link
timeout, or pawl-lift P would land after E1520. At **E1800** the
receipt issues anyway as **ENDED-FAULT** — an aperture sheet over
the still-defocused Deck, carrying the `STILL BLOCKED` lines,
one per faulted link, cause-worded (`INSTAGRAM — STILL BLOCKED
(PERMISSION)` form). The rack completes behind the sheet by
**≤E2080**. The **pawl stays seated**. Silent retry runs at
+30s / +5min / next Deck open, faulted links only, and a healed
link lifts the pawl silently wherever the app is.

Three facts decide the band's answer:

1. **The session is over.** E0 was a *completion* flip — the
   session ran its full course; what faulted is the *release
   proof*, not the session. §6.5's unlock fired at E0. "While
   sealed, PARK cannot open" (GUARD-RULING; MASTER §6.1) binds
   the sealed state, and this is not it.
2. **The pawl is not the band's lock.** The pawl is Release
   Proof's seat (Opal's joint). §1.2's lock predicate is
   controller *phase*, and RELEASING ends for the band at rack
   end. Any builder who keys the band's lock to pawl-seated
   keeps him locked until a silent retry heals the fault —
   ≥30s, possibly never — on the one screen whose whole content
   is "something is still blocked." That reading is killed here
   in print: **the band never waits on the pawl.**
3. **The band is geometrically outside the Deck.** The band is a
   root-Column sibling below the viewport (`MainActivity.kt:484`);
   a sheet hosted in the Deck's viewport does not cover it. A
   sheet hosted at the *root* (the pattern of today's overlays,
   composed after the Column at `MainActivity.kt:513ff` —
   Airlock, Disarm, AOD) covers the whole screen, band included.
   **No filed text states which host the ENDED-FAULT sheet
   gets.** That gap is the fork (§3.3).

### 3.2 The lawful exits — named, in priority order

Under my ruling (§3.3's recommendation), there are three, and he
needs only one:

1. **The sheet's own dismissal — the primary exit.** MASTER
   §6.5's surviving receipt grammar: **one-gesture dismissal**
   (down-drag or tap, 160ms exit) that changes no chrome state.
   Dismissing lands him on the idle Deck — by then the rack has
   completed behind the sheet (≤E2080; a dismissal before that
   lands on a Deck still racking, which finishes under him in
   ≤280ms — lawful, the rack is a focus change, not a lock).
   The fault's truth has been told once, on the sheet; dismissal
   is not a decision and spends nothing.
2. **Any band stop — nav-away.** From rack end (≤E2080) the band
   is live under a viewport-hosted sheet. A stop press is
   Linea §5.4's filed exit for the receipt ("it leaves on
   nav-away; a return re-renders it settled, no replay") applied
   to its faulted form: the sheet is superseded by the
   navigation, once-ever keying (sessionId) prevents any
   re-presentation, and the settled receipt remains reachable by
   returning to the Deck. Zero extra mechanism on the band —
   this is §6.4's ordinary law resuming.
3. **PARK — the repair path, and the one the sheet's content
   points to.** `STILL BLOCKED (PERMISSION)` is a cause whose
   repair lives exactly one place: PARK's board, with its FIX
   keys (GUARD-RULING §1; MASTER §8). From rack end, a PARK-zone
   tap/drag runs §6.7's invocation unchanged, with one ordering
   rule that is mine to state: **the receipt sheet dismisses on
   the same input that invokes PARK** — the 160ms exit runs
   concurrent with the cap's powered leg into the zone, and the
   PARK drawer rises on the cap's seat frame as always. Two
   sheets never stack; "leaving and dismissing are one gesture"
   (§6.7) extends to arriving-with-a-sheet-up. Without this rule
   the natural build produces a PARK drawer rising *over* an
   ENDED-FAULT sheet — the fault's sheet would still be claiming
   the room while he is already repairing it.

Note what is **not** an exit and must not become one: waiting.
The silent retry heals in the background and announces nothing;
the sheet does not auto-dismiss when the pawl lifts (a healed
fault never amends the issued receipt — Wren §4.3). If every
exit above were closed, the sheet would be a trap with a timer
nobody can see. They are not closed; §3.3 makes sure the build
keeps them open.

### 3.3 FORK F-B1 — the sheet's host level, and the seat-before-rise collision

**The contradiction, stated plainly.** Two filed laws collide
on this sheet, and a third filing leaves the deciding question
open:

- **§6.5's receipt grammar is seat-before-rise**: "the
  presenting sheet never races the cap — cap home and chrome
  unlocked before anything rises," with one-gesture dismissal.
- **Wren §4.3 rises the ENDED-FAULT sheet at E1800 while the
  rack completes behind it by ≤E2080** — i.e., the sheet rises
  *before* chrome has finished unlocking/racking, by up to
  280ms. Both texts are filed law; the faulted sheet cannot
  obey both. (The E1800 ceiling is a detention guarantee and
  cannot move to E2080 to satisfy seat-before-rise.)
- **Linea's fork F-L1 (receipt host) is unruled**, and no text
  — Wren, Linea, MASTER §5.7/§6.5 — states whether the faulted
  sheet composes in the Deck viewport or at the root. The
  band's life under the sheet is exactly the difference.

**My recommendation (the band's ruling, priced in §3.4):**

- **Seat-before-rise governs happy receipts; the ENDED-FAULT
  sheet is exempt by its nature.** It is not a presentation over
  a settled room — it *is* the detention exit, and its filed
  grammar is a defocused room. Wren's timing stands as written.
- **The sheet hosts in the Deck viewport, never at the root.**
  The band stays visible below it and goes live at rack end
  (≤E2080) under §3.2's exits 2–3. The 280ms between sheet rise
  (E1800) and rack end is the only window where the sheet is up
  and the band still locked — bounded, invisible as a lock (the
  room is still defocused; nothing has promised him a live band
  yet), and closed by the rack's own clock, not by the pawl.
- **The alternative, named so Ren rules it with eyes open:**
  root-hosted modal sheet → band covered and dead under it;
  exit 1 (dismissal) only; PARK unreachable until dismissed;
  the repair journey becomes dismiss → PARK → FIX (three
  gestures, one of them spent closing a message before he may
  act on it). Lawful, strictly worse, and it hides the one
  surface that must never lie about where he can go behind the
  fault that surface exists to survive.

**Price — Q3:** band-side only (the sheet's own build is E4's /
Linea's F-L1 share): unlock-at-rack-end in the faulted branch is
the same §1.2 predicate, ≈0; PARK-invocation-dismisses-receipt
wiring (host: sheet-state clear on PARK seat, concurrent 160ms
exit) ≈**10 net**.

---

## 4 — FORKS

- **F-B1 — ENDED-FAULT sheet host + seat-before-rise exemption**
  (§3.3). *Recommendation: viewport host; band live from rack
  end; seat-before-rise exempted for the faulted sheet only.*
  Blocks nothing in §§1–2; blocks the faulted sheet's host
  decision inside E4.
- **F-B2 — PARK at the seal: closes, or degrades read-only?**
  (§2.3). §6.1's degrade clause is written for the storage
  Drawer; PARK has no read-only mode in any filing. *My build
  closes PARK at t=0 (≈8, §2).* If Ren extends the degrade
  clause to PARK, the branch changes (cap stays parked, sheet
  read-only, band indication must then print "parked, sealed" —
  a state no filing has specced) and F-B2 needs its own spec
  before the chrome build, not after.

## 5 — ACCEPTANCE TESTS

- **T-B1 (swallow matrix).** Seal a session; at t=0+100,
  t=0+500 (mid-Vertigo), mid-session, E0+100, E0+600,
  E0+1199, and mid-rack, tap each of the four stops and the
  PARK zone: `currentDestination == DECK` at every sample,
  exactly one 4L ack per press, zero `playRotaryDetent(540)`
  calls, zero `onTabSelected` invocations.
- **T-B2 (no deferred navigation).** Across T-B1's whole run,
  sampled at E3130 (rest): `animatedPillIndex == 0f`,
  `pillAnimator == null` or cancelled-and-idle at every sample;
  destination log contains zero entries after the arm.
- **T-B3 (gate race).** Instrumented: dispatch a SHIELD tap UP
  within one frame of the latch. No navigation; pill unmoved.
  Repeat with the tap straddling the repository insert.
- **T-B4 (held-frame skip purity).** Band tap at E0+600: the cut
  executes (defocused Deck composes), destination remains DECK,
  `navigateTo` call count is zero, exactly one skip is recorded
  by the finish controller.
- **T-B5 (landing state).** At E1200 and E1480: derived
  `selectedTabIndex == 0`; cap pixel rests on the DECK segment;
  armed marker absent; PARK zone at rest ink; no animator
  running (frame callbacks idle over 300ms of landing stills).
- **T-B6 (pre-seal drawer).** Open the status drawer (and PARK,
  once built); arm. At t=0+200: drawer closed, cap at DECK,
  destination DECK, shot undisturbed.
- **T-B7 (ENDED-FAULT exits).** Force a link halt. Sheet at
  E1800. Assert: (a) dismissal gesture exits in 160ms to the
  idle Deck; (b) re-enter the state, at E2080+1 a SHIELD press
  navigates and the sheet does not re-present on returning to
  the Deck; (c) re-enter, a PARK invocation dismisses the sheet
  on the same input and the PARK drawer seats with its board —
  **with the pawl still seated throughout** (band state asserted
  independent of pawl state).
- **T-B8 (semantics).** TalkBack: on seal, the band announces
  "Navigation sealed — session running" once; at unlock it
  reads stop names again; the PARK zone while sealed announces
  nothing tappable.

## 6 — NAMED RISKS

- **R-B1 · The recomposition gap.** The host feeds `isSealed`
  through `update{}`; a recomposition can lag the VM's phase
  transition by a frame, leaving the *view* ungated briefly.
  Layer 2 (the VM refusal) is the backstop and is synchronous;
  Layer 1 is the visible truth. Neither layer is optional —
  §1.2 names what each alone permits.
- **R-B2 · Skip-scope creep.** §1.3 routes held-frame band taps
  to the skip because Wren's law says "anywhere." If a later
  ruling narrows the skip to Deck touches, the band tap's
  held-frame effect becomes pure swallow (one ack, nothing
  else) — a one-line change at the host callback, no view work.
  The reverse creep (band taps skipping the *Vertigo*) is
  forbidden by this file: pre-E0 there is no skip.
- **R-B3 · F-L1 dependency.** §3 assumes the faulted sheet *can*
  host in the viewport. Under Linea's F-L1 options A/B (inline
  receipt hosts) the happy receipt is not a sheet at all, but
  the faulted branch remains one in every filing — §3's answers
  stand unchanged. Only a ruling that the faulted sheet is a
  root modal (F-B1's other branch) changes §3.2, and it changes
  it to the worse column, in print, by Ren's hand — not by a
  builder's default.
- **R-B4 · Phase-type ownership.** `bandLocked` rides the
  arm/finish controller's phase (Draht §4). If the builder
  instead derives it from `activeSession` + a finish flag kept
  in the Deck composition, the root-hosted band cannot read the
  Deck-local flag and the gate silently never engages — the
  failure mode is today's behaviour, indistinguishable in
  casual testing. The flag's home (VM, beside the controller) is
  part of this spec, not an implementation detail.

## 7 — PRICE ROLL-UP (my joint only, production net lines)

| Joint | Net |
|---|---|
| Q1 swallow (view + VM gate + host + skip route) | ≈49 |
| Q2 t=0 drawer/PARK clear | ≈8 |
| Q3 PARK-dismisses-receipt wiring | ≈10 |
| **Band under Slice E, total** | **≈67 net + ≈70 tests** |

Excluded by name: the chrome build itself (rail, cap, PARK zone,
rename, badge deletion — my plan-wave ladder and §6.4's build);
the sheet's composition (E4 / F-L1); the finish state machine
(E4 / Draht); the header dot (Horiz, E5b); F-04's scroll padding
(Finishers/chrome).

## NEXT

(a) *What I want next:* the chrome build's band pass — §6.4/§6.5
land in `MonolithBottomNavView` and my §§1–2 become its session
half; I want the render pair (sealed rail, day + night, cap at
DECK with pin) in front of me before anyone calls the rail done.
(b) *What I could do unassigned:* the same swallow audit for the
header band (GUARD pill + drawer toggle, `MainActivity.kt:254–
267`) — it shares `navigateTo` and §1's Layer 2 covers it, but
its *visible* sealed state (print-not-target, §6.1) is unbuilt
and unpriced anywhere.

— Band, Improvers · the bottom nav
