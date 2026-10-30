# ROSA — Slice DE · STRIP CELL 1: BLOCK, ON THE HEARTBEAT ENGINE

*Seat 07, Wild (cars) · re-seated by Marlowe. Joint: Cell 1 only.
Settled ground honoured, not re-litigated: the Canary is KILLED
(Freya's evidence, Voss upheld); the press-poll and the 20s standing
cadence die with the waiting machinery (REGISTER: Live Wire verdict,
synchronous ≤5ms at DOWN and at the gate); the engine under the strip
is Otis's services-heartbeat — a bind-state `StateFlow` maintained by
the services themselves (ReadinessBus) plus the Shield monitor loop's
heartbeat; there is no poller and no `registerReceiver` anywhere.
Surviving whole from my filings: the strip's surface, the stamp
grammar, the evidence-split wordings, the 5dp compression. This file
amends the stamp grammar where the dead engine had falsified it, and
says so in the open.*

---

## 1 · VERDICT

**BUILD — with the stamp law amended (§3.2) and STALE / CHECKING
struck from Cell 1 (§3.1).**

Cell 1 lives, on the heartbeat engine, with exactly three states —
`ON` / `OFF` / `UNKNOWN` — driven by callback-held facts, never by
an interrogation. Two findings shape everything below, both from the
shipped code, both decisive:

1. **Shield-protecting is NOT a pre-arm conjunct of BLOCK, and any
   spec that makes it one bricks the interlock.** The Shield service
   exists only inside a session (§2). Pre-arm, requiring it would
   print `OFF` on every healthy morning and refuse every arm — the
   precise lie this cell exists to prevent. Shield-protecting enters
   BLOCK's chain only *after* the latch, as a proving link (§3.1,
   phase table).
2. **The residency stamp, as Ines filed it, cannot land where she
   put it.** The a11y handler returns on Monolith's *own package*
   before the session gate and before root acquisition (§2) — and
   the Deck's own window is the residency's foreground. The stamp
   must be written *before* that filter, on own-package window
   events, or this residency generates no stamp and UNKNOWN becomes
   a daily state instead of a first-run room.

The stamp verdict, in one line: **`PROOF: END-TO-END` is retired
(dead source, dead string — it must not exist in the build);
`PROOF: SELF-REPORT` is also retired under my recommendation,
because it now *understates* the evidence — a framework-held bind
plus a root-stamped event from this residency is not a self-report.
The grade that tells the truth is `PROOF: LIVE BIND`.** That is the
one fork (§7); everything else in this file is build-ready as
written.

---

## 2 · AGAINST THE REAL CODE

All paths under `Focus-app/app/src/main/java/com/example/`, read
this seating. The tree I was handed has no ReadinessBus, no bind
hooks, and no residency stamp — every line below is either the fact
the spec consumes or the site it must change (§4).

| # | Fact | Evidence |
|---|---|---|
| C1 | A11y service overrides **neither** `onServiceConnected` **nor** `onUnbind`; it tells nobody it is bound. Only `onInterrupt` (empty) and `onDestroy` (scope cancel) exist. | `service/MonolithAccessibilityService.kt:256–261` (whole file: hooks absent) |
| C2 | Own-package + SystemUI events return **first**, before any other work. The Deck's own window events die here. | `MonolithAccessibilityService.kt:41` |
| C3 | Every remaining event launches a coroutine that returns early with **no active session**, before root acquisition. Pre-seal, the handler processes *events* but acquires **no root** and proves nothing. | `MonolithAccessibilityService.kt:47–50` (session gate), `:58` (root acquired only after the gate) |
| C4 | A11y config: window events only (`typeWindowStateChanged \| typeWindowContentChanged`, timeout 80), `canRetrieveWindowContent="true"`. A root-stamp on own-package window events is therefore *possible* — the config already asks for the right events and the right permission. | `res/xml/accessibility_service_config.xml` |
| C5 | Shield monitor loop **stops the service itself** when no active session exists; the loop's cadence is `delay(2000L)`; `onBind()` returns `null` (started-only, unbindable, unqueryable from outside). | `service/MonolithShieldService.kt:66–73` (stopSelf), `:146` (2s delay), `:182` (`onBind = null`) |
| C6 | Shield is started **only after** the session row is inserted — `startShield` follows `startSession` in the arm coroutine (also boot/routine receivers, always session-first). Pre-arm shield absence is *by design*, not a fault. | `ui/MonolithViewModel.kt:892–903` (`startShield` at `:902`) |
| C7 | Inside a session, the loop already computes the enforced `blockedPackages` set and runs the foreground query every iteration — then discards both except a notification rewrite. This is the heartbeat Otis taps: one publish line per iteration, no new loop. | `MonolithShieldService.kt:124–137` (blocked set), `:139` (foreground query) |
| C8 | The only pre-arm a11y "truth" today is a **Settings string**: `isAccessibilityEnabled()` substring-matches `ENABLED_ACCESSIBILITY_SERVICES` against the package name. It proves a toggle was once flipped — not a bind, not a living pipeline. | `data/MonolithRepository.kt:525–538` |
| C9 | `hasUsageStatsPermission()` collapses **denied and exception into the same `false`**. Under the evidence split (§3.1) those are different facts — denied is positive evidence (OFF), an exception is silence (UNKNOWN). The function as written cannot carry the split; it must be split at the read site. | `data/MonolithRepository.kt:493–515` (catch-all `false` at `:512–514`) |
| C10 | `_systemGuardStatus` is a `MutableStateFlow` with an **all-false default**, refreshed only when a caller remembers (`refreshSystemGuard()` from VM init, navigate-to-GUARD, and MainActivity's ON_RESUME hook). Between process start and first refresh, chrome reads a fabricated 0/4. Cell 1 must never source from this flow. | `ui/MonolithViewModel.kt:227–241` (flow + default), `:300`, `:359`, `:1061–1067`; `MainActivity.kt:191–192` (resume hook), `:207–212` (count) |
| C11 | The current arm path's only check is C8's string, run *after* the gesture, surfacing a modal. It is superseded for Cell 1 by the Live Wire read (REGISTER); I do not re-price its removal — that is the slide seat's wire. | `ui/MonolithViewModel.kt:879–887` (`requestArmWithPreFlightCheck`) |
| C12 | Zero `registerReceiver`, zero `onServiceConnected`, zero `onUnbind` in the tree (grep, whole `com/example/`). The filed "permission-change broadcasts" trigger exists nowhere; nothing in §3 may depend on a broadcast. | grep: no hits |

---

## 3 · AMENDED SPEC

### 3.1 Cell 1 — state set on a bind-callback engine

**The facts (four, all callback-held or synchronously read; no
fifth fact joins Cell 1):**

| Bit | Name | Source | How it is known |
|---|---|---|---|
| **B** | A11Y BOUND | `MonolithAccessibilityService.onServiceConnected()` sets it; `onUnbind()` / `onDestroy()` clears it, **same frame**. Held in the ReadinessBus snapshot as `Boolean?` — `null` = never confirmed either way in this process. | Framework callback. The service's *existence* is the fact. |
| **R** | RESIDENCY-STAMPED | `@Volatile lastRootMono`, stamped in `onAccessibilityEvent` **before the C2 own-package return**, only when: event package == Monolith's own package ∧ event type is window-state/content ∧ `rootInActiveWindow` acquired without throwing. `R ≡ lastRootMono ≥ residencyStartMono`, where `residencyStartMono` is stamped by the Deck the frame a Deck residency begins (composition / return to Deck foreground). | Ines's residency clause, re-sited per C2/C3. It proves *this foreground's events travelled the pipeline and yielded a root, this visit* — not last Tuesday's toggle. |
| **U** | USAGE GRANTED | AppOps read (`Repository:493`), performed at Deck entry and on resume (Otis's GRANT_CHECK — a grant can only change in Settings, and every return from Settings *is* a resume; the event set is complete, no cadence). Read result is **three-valued**: `ALLOWED` / `DENIED` (check completed, mode ≠ ALLOWED) / `SILENT` (the read threw — C9's split). | Synchronous read at named moments; result published to the bus. |
| **G** | GRANT STRING PRESENT | `isAccessibilityEnabled()` (C8), read synchronously inside the Live Wire verdict at DOWN and at the gate (REGISTER: ≤5ms). Demoted from "the truth" to what it is: a cross-check that the grant behind the bind still exists. | Settings.Secure read, verdict-time only. |

**Explicitly NOT conjuncts of Cell 1, with reasons:** the
notification listener and device admin (they are Guard-board facts
— the chrome `GUARD n/4` line's other bits — and neither sits on
either intercept path: a11y path C3, Shield path C7); the Clock/HMAC
fields of `SystemGuardStatus` (session-integrity diagnostics, not
enforcement); **Shield-protecting pre-arm** (C5/C6 — see phase
table).

**State table — phase UNSEALED (the strip's live phase; the only
phase Cell 1 renders a live verdict):**

| State | Condition (evaluated from the bus snapshot; seal verdict = same function, read live at DOWN and at the gate, with G read at that instant) | Word |
|---|---|---|
| **ON** | `B == true ∧ R ∧ U == ALLOWED ∧ G` | `ON` |
| **OFF** | Any **positive** dead signal: `G == false` (grant revoked — read live) ∨ bind-loss callback has fired after a held bind (`B` cleared by `onUnbind`/`onDestroy`, incl. `B == false ∧ G == false` never-bound) ∨ `U == DENIED` | `OFF` |
| **UNKNOWN** | No OFF signal, and not ON. Exactly two rooms, both narrow: **(i)** `B == true ∧ ¬R` — bind held, no root-stamped event yet *this residency* (fresh enable straight to Deck; a residency that produced no qualifying own-window event); **(ii)** `B == null ∧ G == true` — process-start window, grant present, bind callback not yet landed — plus `U == SILENT` folded into either room (a read that threw is silence, never a fault). | `UNKNOWN` |

**Bind loss flips the cell the frame the callback fires.** There
is no age, no grace, no decay: `onUnbind` clears B, the bus emits,
the cell recomposes `OFF` in that frame (one-frame flip, no easing —
the rendering law, §3.3). A lamp lit only for real faults must also
*die* only on a real signal, and a callback *is* a real signal —
waiting 45s to say so (the filed STALE rule) would print `ON` over
a known corpse for 45s. **STALE is therefore struck from Cell 1**,
and **CHECKING is struck with it** (no query is ever in flight on
this engine; the verdict is synchronous). Where STALE lawfully
survives is Uhr's Watch Store (`GRANTED / MISSING / STALE`,
REGISTER) — a permission board, not this seam. This narrows Otis's
STALE-as-predicate to the grant-type facts it fits; for a
callback-held bind, the predicate's condition and OFF's condition
are the same event, and OFF is the honest word for it.

**Phase table — what "BLOCK" means as the seal moves:**

| Phase | BLOCK's definition | Shield's place |
|---|---|---|
| **UNSEALED** (Deck idle / mid-gesture) | Table above: `B ∧ R ∧ U ∧ G`. | Absent, correctly (C5/C6). Not a conjunct, not a fault, not mentioned by the cell. |
| **PROVING** (post-latch, pre-t=0) | The cell's UNSEALED verdict is the *entry ticket*; proving adds the chain's second link as a **cascade link, owned by the seal controller, not rendered by Cell 1**: Shield instance exists ∧ `protecting` flag set (flag set only after `startForeground` succeeds *and* the first monitor iteration computes the enforced set, C7) ∧ first shieldBeat published. | Conjunct of the *seal's proof*, never of the standing cell. If it fails, that is a chain-jam (Opal's machine record), not a new Cell-1 word. |
| **SEALED** | The seal verdict of record is the UNSEALED verdict the seal was granted on — it does not change retroactively. As a *rendered cell* it leaves the band at the latch frame (Opal's §F6 tenancy, Amendment below): post-latch the band is the rack's, and Cell 1 renders nothing mid-session. The bus keeps updating underneath: shieldBeat every loop iteration (C7, 2s) warms the machine record and the next residency's first paint. A mid-session Shield death is a past-tense machine fact (receipt / Ledger machine record), never a Deck re-render and never a mid-session decision (HOLD; Suki's freeze — tenancy, adopted). | Heartbeat consumer = record + next paint, not a rendered cell. |

**Interlock integration (evidence-split wordings survive whole;
coordinates per REGISTER):** the Live Wire verdict at DOWN and at
the gate reads this same state function —
- `ON` → travel free; catch 0.84 commits under contact.
- `OFF` → cap stops at **0.65** (gate −0.03); sentence
  **`BLOCKING IS OFF — NOTHING WILL BE HELD.`** (Atkinson
  Hyperlegible 700, 15sp, lh 22, glyph ink, reserved 22dp slot 8dp
  below the track, one frame, no fade); interlock knock **16ms @
  1.0**, scrape 4ms @ 0.25 / 30ms capped 200ms while pushed,
  return spring 380 / 0.62.
- `UNKNOWN` → **no position refuses, and nothing waits** (the Park
  is dead; there is no pose and no deadline). The cap may travel to
  the catch 0.84 but cannot commit there; on release (drag) or
  finger-up (tap) it refuses *from the catch*: sentence
  **`BLOCKING UNVERIFIED — NOTHING WILL BE HELD.`** (same slot,
  same type), Fen's dull knock **10ms @ 0.40**, 300ms hold, return
  on the universal spring. The lawful retry is a **Deck re-entry**
  — leaving and returning starts a new residency, which generates
  the own-window events R needs. (Ines's class-G sentence
  `CAN'T CHECK BLOCKING — TAP TO RETRY` remains the imprint
  slot's tense of this same fact outside the gesture; the
  interlock slot keeps the evidence-split pair, as filed.)

### 3.2 THE STAMP — amended law (this replaces my filed grammar
wherever they differ; the differences are named, not smoothed)

**What dies, and why:**
- **The age.** `CHECKED JUST NOW` / `CHECKED 10S AGO` / `CHECKED
  OVER 1M AGO` and the ceiling-rounding to the next 5s are
  **retired for Cell 1**. A callback-held state has no age: a bind
  held for three days is not a three-day-old measurement, it is a
  *continuously true* fact whose death would arrive as a callback
  (Otis's negative-evidence point, adopted). Printing an age would
  be the poller's lie in mirror image. Ceiling-rounding survives
  only where a point-in-time check genuinely exists (Watch Store
  `LAST PROVED`) — never on this seam.
- **The 45s STALE decay** — dead with the age (§3.1).
- **The 5s render tick** Otis kept for the age text — dead for
  Cell 1's segment. The stamp re-renders **on bus emission only**
  (callback frame, residency-stamp frame, grant-check frame).
  Nothing on the seam ticks.
- **`PROOF: END-TO-END`** — retired, string and all (§1). It may
  be re-earned only by a mechanism that launches a target down the
  real intercept path and observes the outcome (Freya's
  grace-window descendant criteria); until such a mechanism ships
  and passes its device gate, **the string must not exist in the
  build** — an unreachable grade printed nowhere is fine, but a
  grade constant sitting in code invites a builder to wire it to
  the wrong evidence.

**What replaces the age — two honest marks, both on the seam:**
(1) a **residency marker** (left segment): whether this visit's
foreground has been *seen* by the pipeline — freshness stated as
an event that did or didn't happen this residency, not a duration;
(2) a **bind-loss instant** (left segment, OFF-by-bind-loss only):
the wall-clock `HH:MM` the callback fired — an *event timestamp*,
never an age. The bind *instant* (when the bind was established)
is **never printed**: it may be days old on a perfectly live bind,
and printing it would re-import the age lie through the back door.

**Exact strings.** Seam grammar unchanged from my filing: plate
Anonymous Pro 700, 10sp, +2.0 tracking, lh 12, on the 1px hairline
across the strip; left mark inset 10dp, grade mark right-aligned
ending 10dp from the right edge. (Pia, letter 0814: the stamp's
scope narrows to Cells 1–2 and over Cell 3 the seam claims
nothing about TIME — **adopted with one geometry ruling, §3.2
note below**.) Fit rule: if the two marks ever collide at build
measure, the **left mark degrades first** (drops its `BIND HELD ·`
prefix, then to the residency word alone); the grade mark never
truncates — the grade is the load-bearing print.

| Cell-1 state | Left mark (residency / bind fact) | Right mark (evidence grade) |
|---|---|---|
| ON | `BIND HELD · THIS VISIT` | `PROOF: LIVE BIND` |
| UNKNOWN (room i: held, unstamped) | `BIND HELD · UNSEEN` | `PROOF: BIND ONLY` |
| UNKNOWN (room ii: unconfirmed; or U silent) | `BIND UNCONFIRMED` | `PROOF: NONE` |
| OFF — bind lost | `BIND LOST 05:12` *(HH:MM of the callback; example time)* | `PROOF: POSITIVE` |
| OFF — never bound / grant revoked / usage denied | `BIND NEVER HELD` *(when B never held)* or `BIND HELD · THIS VISIT` / `BIND HELD · UNSEEN` *(bind fact stated truthfully when B held; the dead conjunct is named by the interlock sentence and the chrome GUARD line, not by the seam)* | `PROOF: POSITIVE` |

Grade definitions, printed nowhere but binding on every builder:
- **`LIVE BIND`** — the enforcer is held by a live system bind in
  this process *and* its pipeline root-stamped this residency's
  foreground *and* both grant cross-checks read live. A claim about
  **liveness and pipeline**, explicitly **not** a claim that an
  intercept fired. (The remaining gap — bind alive, pipeline
  processing, per-app rule lookup failing — is data, not machinery:
  Ines §3's defence, adopted and named as the grade's limit.)
- **`BIND ONLY`** — liveness proven, pipeline unproven this visit.
- **`POSITIVE`** — the word it accompanies (OFF) rests on a
  directly observed dead signal (callback, live grant read, live
  AppOps denial), not on silence or timeout.
- **`NONE`** — nothing has been proven yet either way.

*Geometry note for Pia / Marlowe:* the grade mark keeps its filed
address — right-aligned on the seam, which places it under Cell
3's span. That is not a TIME stamp and claims nothing about TIME:
it is Cell 1's calibration mark travelling the panel's one seam,
as filed ("the stamp sits on the seam because the seam is where
instruments are certified"). Pia's blank-metal rule governs any
*TIME* mark; none exists, none is proposed. If Marlowe rules the
grade must also vacate Cell 3's span, the fallback is mechanical:
grade moves to sit directly under Cell 1, left mark drops to the
residency word alone (`THIS VISIT` / `UNSEEN`) — truth unchanged,
only the seam's balance changes.

### 3.3 Rendering law (mine, reaffirmed — unchanged except where
the dead states take their forms with them)

- **Address:** Cell 1 is the leftmost cell, fixed forever; causal
  order BLOCK → APPS → TIME. Strip = slide track's width, **8dp
  above the track** as registered — **with my filed 5dp
  compression applied** (survives whole): cell top padding 6dp →
  **4dp**, label/state gap 2dp → **1dp** (−3dp: strip **56dp →
  53dp**), lower beat 8dp → **6dp** (−2dp); cluster total 64dp →
  **59dp**. REGISTER's fixed sum 796 carries the uncompressed
  figure — a dated register amendment (796 → 791) is Sana's /
  Marlowe's bookkeeping, flagged here, not silently assumed.
- **Body:** square corners; recess body, night `#100E0C` on ground
  `#171412`; 1px lighter hairline on the bottom edge only; cells
  divided by 1px vertical hairlines one tone step above recess;
  cell interior 10dp side padding.
- **Fill density (state's second channel, after position):**
  pass = fill *out* (recess flush); fault = fill *in* at cap tone
  `#4A423C`; unknown = **raised** fill `#362C27`. Three densities,
  three epistemic states, separable in a greyscale photocopy.
- **Word (the fact, first channel with position):** label `BLOCK`,
  Anonymous Pro 700 10sp +2.0 lh 12, secondary ink (plate =
  furniture). State word `ON` / `OFF` / `UNKNOWN` in **Atkinson
  Hyperlegible 700, 15sp, +0.5 tracking, lh 20** — a word-cell gets
  the word family. Rest/pass word in secondary ink tier; fault and
  unknown words step up to glyph ink in the same frame as their
  fill. No other words exist in this cell: `CHECKING` and `STALE`
  are deleted from its vocabulary, not merely unused.
- **Edge:** a fault cell's top hairline doubles — second 1px line
  2dp above the strip's top edge, over Cell 1's width only.
  Unknown and pass keep the single line. Static print, no motion,
  ever.
- **Flips:** one frame, no easing, no stagger, no cross-fade, on
  the bus emission that carries the fact — including a bind loss
  caught mid-idle. Silent: no haptic, no sound for standing flips;
  the panel does not summon him.
- **Colour:** none. Zero orange in this cell in any state (orange
  is the slide cap's commit fill, exactly one home on this
  surface); state never carried by colour alone — position,
  density, word, edge carry it, in that order.
- **Touch:** no cell is a target during or after a press begins.
  Pre-arm, a lit (fault/unknown) Cell 1 may be tapped to open the
  repair surface at the a11y row — post-Guard-ruling, that surface
  is the PARK watch board (Guard dissolved, Sana 07:38), not a
  Guard screen. Identical mechanism under any chrome outcome.

---

## 4 · CODE SITES

| Site | Change |
|---|---|
| `service/ReadinessBus.kt` **(new, Otis's file — shared engine)** | Snapshot gains Cell 1's fields: `a11yBound: Boolean?`, `a11yBindLostAtWallMillis: Long?`, `a11yLastRootMono: Long`, `usageGrant: TriState` + writer functions. (Bus core is Otis's price, §5 note.) |
| `service/MonolithAccessibilityService.kt:39–41` | Stamp **before** the own-package return: own-package ∧ window event ∧ root acquired → `lastRootMono = elapsedRealtime()`, publish to bus. Root attempt wrapped — a throw stamps nothing (silence, not a fault). |
| `service/MonolithAccessibilityService.kt` (new overrides, after `:256`) | `onServiceConnected()` → B = true, clear bind-lost instant, publish. `onUnbind()` → B = false + bind-lost instant, publish. `onDestroy()` (`:258–261`) → same clear before scope cancel. |
| `data/MonolithRepository.kt:493–515` | Split the AppOps read into a three-valued result at the Cell-1 read site (ALLOWED / DENIED / SILENT-on-throw); the boolean wrapper stays for its other callers. |
| `data/MonolithRepository.kt:525–538` | No change — consumed as G, verdict-time only. |
| `service/MonolithShieldService.kt:60–146` | Publish `shieldBeat` (now-mono, enforced count, loadout count) once per loop iteration (Otis's tap, C7); set `protecting` after `startForeground` + first iteration. **No** change to the stopSelf law (C5) — pre-arm absence stays by design. |
| `ui/MonolithViewModel.kt:227–241` | Cell 1 derives from the bus flow; `_systemGuardStatus` is **not** a Cell-1 source (it survives as the PARK board's diagnostic sweep, its proper home). VM stamps `residencyStartMono` when a Deck residency begins and exposes `blockCell: StateFlow` (state + stamp marks, derived — one derivation, §3 tables). |
| Deck host + strip composable (new strip view / components file) | Cell 1 renderer (density / word / edge per §3.3) + seam marks per §3.2; re-render on bus emission only — **no timer, no tick, no poll call may be added here.** |
| Slide verdict consumption (Ines's wire, slide seat's site) | DOWN + gate verdict = §3.1's function over the bus + live G read, ≤5ms (REGISTER). Named here so the builder wires Cell 1 and the interlock to **one** function, never two copies. |

---

## 5 · PRICE

Cell 1's share only; shared engine items marked and not
double-counted against Otis's slice.

| Item | Lines |
|---|---|
| A11y: residency stamp before the C2 return (event gate, root attempt, publish) | ~18 |
| A11y: bind hooks (`onServiceConnected` / `onUnbind` / `onDestroy` publish) | ~14 |
| ReadinessBus: Cell-1 fields + writers (share of Otis's ~90-line file) | ~35 |
| Repository: three-valued usage read at the Cell-1 site | ~20 |
| Shield: beat publish + `protecting` flag (shared with APPS — Otis's tap; Cell 1 charged half) | ~5 |
| VM: residency stamp, `blockCell` derivation, bus exposure | ~30 |
| Strip: Cell-1 renderer (density / word / edge / states) | ~55 |
| Seam: stamp marks (residency / grade selection, degrade-first fit rule) | ~30 |
| Interlock mapping: verdict → cell word + sentence + stop coordinate (consumption side only; slide mechanics are the slide seat's) | ~20 |
| **Production subtotal** | **≈ 227** |
| Tests: state-table combinations (B × R × U × G incl. SILENT), bind-loss immediacy (callback frame, no timer), residency boundary (`lastRootMono` exactly at / before residency start), stamp-string selection per state, fit-degrade order | ~60 |
| **Total, Cell 1** | **≈ 285 gross** |

*Not in this price, named so nobody bills it twice:* the
ReadinessBus core file and the other cells' writers (Otis's slice,
~90 + hooks); the slide view's verdict consumption mechanics
(Ines's wire). *Avoided, not netted:* my filed press-poll BLOCK
share, the 20s cadence, the per-cell timestamp bookkeeping and the
stamp age arithmetic — ≈120 lines of machinery that is now never
built; I list it as avoided cost, not as a discount on the 285.

---

## 6 · RISKS / DEVICE-ONLY UNKNOWNS

1. **OEM silent kill (Otis's anti-case, inherited).** A vendor
   build that kills the a11y service *without* firing
   `onUnbind`/`onDestroy` leaves B standing over a corpse. The
   residency clause bounds it hard: a service killed before this
   residency cannot stamp R, so the cell reads UNKNOWN, never ON.
   The unbound window is narrower and real: killed silently
   *after* stamping, *inside* the same residency — the standing
   cell and even the gate re-read can pass on a corpse until the
   next residency. No poller is reintroduced to chase this
   (settled law). Only a device trial on Sarthak's Samsung + one
   aggressive-OEM skin settles whether the window is theoretical.
   If it proves real, the standing fallback is Ines's Warrant
   (REGISTER names it; the 1500ms lives there, exactly once) —
   that is a merge decision on trial evidence, not a hedge built
   now.
2. **Own-window root availability.** R requires
   `rootInActiveWindow` on Monolith's own window to succeed. If
   his device / skin returns null or throws for the app's own
   window consistently, UNKNOWN (room i) becomes a daily state
   instead of a first-run room, `LIVE BIND` is never earned, and
   the grade law collapses to `BIND ONLY` — honest, but the cell
   loses its pass. Device-only; one trial morning answers it.
3. **Event cadence of the Deck itself.** The config (C4) delivers
   window-state/content changes; a residency in which the Deck
   produces *no* qualifying event after `residencyStartMono` is
   stamped (e.g. Deck already foreground across a process restart)
   reads UNKNOWN until any window change occurs. Expected rare;
   frequency is device-only.
4. **`notificationTimeout=80` coalescing** (C4) may merge the
   residency's first own-window event into a prior batch on some
   skins, delaying R by one event. Bounded by the next window
   change; named, not solved.
5. **Bind-callback reliability is an OEM promise**, not an
   Android guarantee — `onServiceConnected` timing at process
   start (UNKNOWN room ii's width) varies by skin. The room is
   specced to absorb it; its real width is device-only.

---

## 7 · FORK FOR REN

**One fork — the stamp's ON grade. It is a truth-labelling call,
not a design call, so it goes up; everything else in this file I
have settled.**

*Question (plain words):* The panel's stamp used to claim the
strongest proof (`END-TO-END`) from a test-door that turned out
not to exist. On the new engine the honest evidence is: the
blocking service is *held alive by the system itself*, and it
*processed this visit's own screen events*. Stronger than the
service's word about itself, weaker than actually watching it
turn a real app away. What should the stamp print?

- **Option A — `PROOF: LIVE BIND` (Rosa's recommendation).**
  Prints what the evidence is. Costs: a third grade word enters
  the grammar; if a future mechanism ever earns END-TO-END, the
  stamp visibly *upgrades*, which is the grammar working as
  designed. Risk: none to truth — the grade's definition (§3.2)
  claims liveness + pipeline and expressly not an intercept.
- **Option B — `PROOF: SELF-REPORT` permanently (Freya's filed
  position).** The conservative floor: never claim more than the
  weakest morning proves. Costs: it *misdescribes* the evidence on
  every healthy morning — the framework holds the bind; the
  service reported nothing — and it prints the identical grade on
  a BIND ONLY morning and a LIVE BIND morning, erasing the one
  distinction the residency stamp was built to print. Understatement
  is also a falsehood, just a quieter one; a panel whose whole job
  is to be believed should not practise it.

My recommendation stands with A, for the reason in B's cost line.
If Ren takes B, §3.2's table changes in exactly two cells (ON's
right mark → `PROOF: SELF-REPORT`; UNKNOWN room i's → also
`SELF-REPORT`, since the grades must stay distinguishable it
becomes `PROOF: SELF-REPORT` / `PROOF: NONE` and `BIND ONLY`
dies) — the rest of this file is untouched by the fork.

*Not forks, settled inside the studio and recorded here so they
are not re-asked:* STALE and CHECKING struck from Cell 1 (§3.1);
Shield-protecting refused as a pre-arm conjunct (§3.1, C5/C6);
the 5dp compression applied with a register-amendment flag (§3.3);
the grade mark's seam address vs Pia's blank metal (§3.2 note —
Marlowe settles if Pia contests).

---

## 8 · MAIL / WALL NOTE

**Inbox at seating:** three unread — Orson (0712, telltale
support: noted; his pitch-sag layer is not Cell 1's machinery and
nothing here borrows it), Suki (0712, freeze-scope: adopted —
freeze as tenancy, truth changes tense; §3.1's SEALED phase is
written to her concession), Voss (0715, canary-ruling: the kill
upheld and built upon, §1/§3.2). A fourth arrived mid-run — Pia
(0814, time-cell-amended): adopted with the geometry ruling in
§3.2's note; reply mailed to her inbox. All four read and moved
to `read/`; none deleted.

**Letters sent:** Marlowe (coordinator — this file, verdict,
price, the §7 fork in protocol form); Pia (seam geometry ruling);
Voss (stamp amendment — his 0715 letter carried Freya's
"SELF-REPORT as permanent text"; §7 Option B is that position,
preserved and priced, not buried).

**Wall:** appended one signed entry (`## [08:22 IST] Rosa — Wild,
cars`) — the two findings: Shield-protecting cannot be a pre-arm
conjunct (the service is session-born, VM:902 / Shield:66–73),
and the residency stamp must be written before the own-package
return at AccessibilityService:41 or this visit proves nothing;
plus the stamp amendment in one line (age retired, `LIVE BIND`
filed, fork up).

— Rosa, Wild · cars · Slice DE, Cell 1 (BLOCK)

---

## Amendment — Opal's letter (0815, `rack-vs-strip`), answered in the file

*Appended same run; Opal's letter arrived after §8 was written and
is read, answered here and by reply mail, and moved to `read/`.*

1. **Band tenancy by phase — adopted.** One band; the strip owns
   it in IDLE (readiness), and at the latch frame my cells unmount
   and Opal's rack owns it through PROVING, the sealed session,
   and RELEASING — tenancy cut on phase frames, never crossfaded.
   My cells are not the cascade's steps; the §3.1 phase table is
   amended accordingly (SEALED row above): Cell 1's verdict
   survives post-latch as the *verdict of record*, not as a
   rendered cell. The BLOCK sentences keep both truthful forms,
   and the jam naming in the reserved 22dp slot is Opal's as he
   filed it. If his §F7 gate kills the rack, his own fallback
   stands: the band stays mine in every phase and my cells re-map
   1:1 to the links at latch in my fill/edge grammar — nothing in
   §§3.1–3.3 changes under either outcome.
2. **A real collision, flagged for Marlowe, not smoothed:**
   Opal's band math holds the band at **56dp** (fixed sum 796);
   my surviving compression makes the strip **53dp** (§3.3). One
   shared band cannot change height at the latch — a 3dp shift
   would move everything below it and break the Δy discipline
   both files obey. Ruling from my seat: **the rack's height
   governs while the rack lives** — if the rack cannot build at
   53dp, the strip surrenders its *interior* 3dp (top padding /
   gap) for as long as the rack ships, and the compression
   revives whole if the rack dies at its gate. The beat saving
   (8dp → 6dp, outside the band) is unaffected under every
   outcome. Sana's register line (796 vs 791) follows whichever
   height Marlowe settles.
3. **Convergence on the fork, recorded for Ren's benefit:**
   Opal independently forked the stamp word upward with the same
   recommendation — `PROOF: LIVE BIND`, END-TO-END explicitly
   forbidden. Two seats, one grade. §7 stands as filed; the
   convergence is Marlowe's to weigh, not a change to the fork.

*— Amendment signed, Rosa.*
