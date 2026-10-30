# Suki — slice D/E part: THE REFUSAL SENTENCES + THE SLOT + THE HAND-BACK

*Seat: Suki (Wild, space). Coordinator: Marlowe. Joint: the refusal
sentences, Rosa's reserved slot, and the repair hand-back — build-ready.*

*Law as given, and followed: the final refusal sentences are the
Critics' merge pair — `BLOCKING IS OFF — TAP TO FIX` /
`CAN'T CHECK BLOCKING — TAP TO RETRY` — Atkinson 700 15sp, glyph ink,
in Rosa's reserved 22dp slot 8dp below the track, reserved ALWAYS.
Live Wire (Ines §3, REGISTER): the verdict is synchronous, ≤5ms, at
DOWN and at the gate; UNKNOWN survives in exactly one room — bind
held, no event stamped this residency. Hand-back: no restored line,
ever; re-sweep ≤300ms + 500ms stability beat + 90ms cross-fade,
≤890ms total.*

***Superseded wording in the sources, named as ordered:***
*plan/BEYOND-deck.md W4 (and MASTER-PLAN §3, which copies it) carries
two older forms that are DEAD and must not be built: (1) the interlock
pair `BLOCKING IS OFF — NOTHING WILL BE HELD.` /
`BLOCKING UNVERIFIED — NOTHING WILL BE HELD.` in Rosa's slot — the
final pair above replaces both, in the same slot, at the same type;
(2) W4's F-TENANCY paragraph and its FIGHTS summary, which put the
repair-tense sentence (`— TAP TO FIX`) in chrome/the imprint as a
second home — superseded by the one-home ruling
(REN-RULINGS-DOSSIER §4, option B): the sentence lives ONLY in the
reserved slot; chrome carries state, not a sentence (the `GUARD n/4`
line, REGISTER). W4's 1500ms UNKNOWN deadline and Park machinery are
likewise dead under Live Wire (REGISTER, "VERDICT (Live Wire)"). My
own beyond-deck part (beyond-deck-parts/suki.md §2, §5) argued the
sentence tenants the imprint slot at 15sp and evicts its neighbours —
that geography loses to F-TENANCY + the dossier, and §3 below is my
own correction of it. What survives of that file whole: the
cause-worded classes, the priority order, the measured hand-back,
the no-restored-line strike.*

---

## (1) Verdict

**BUILDS, with four amendments — all of them forced by the verdict's
own shape or by the final one-home ruling, none of them taste:**

- **A1 — The table is 4 refusal rows, not 2 and not 7.** The filed
  pair is the floor. Live Wire's verdict (Ines §3) has exactly four
  conjuncts, and exactly two more causes are real, distinct, and
  differently routed: shield-not-protecting and usage-stats-denied.
  They get rows, in my filed Class B / Class C wording. Three of my
  old classes are STRUCK because the wire does not read them:
  overlay (old D), list-empty (old E), list-out-of-sync (old F) —
  a sentence for a cause the verdict cannot distinguish is a
  fabricated cause, and the brief forbids it by name.
- **A2 — Two slots, not one.** The sentence slot and the imprint slot
  are different objects at different addresses (§3). The imprint
  tenant does NOT move into Rosa's slot when healthy, and the
  sentence does NOT tenant the imprint when it fires. One slot with
  tenancy was my beyond-file's geography; it is superseded.
- **A3 — The ≤300ms re-sweep is a bind-wait, not a verdict latency.**
  The verdict read is ≤5ms (REGISTER). What the 300ms buys is the
  OS re-binding the accessibility service (or the shield's
  `protecting` flag landing) after he flips the switch in Settings —
  existence, not measurement. §4 defines the sweep as repeated
  ≤5ms reads inside that window, with the bind callback as a
  second, unbounded-but-truthful completion path.
- **A4 — The healthy tenant of the sentence slot is NOTHING.**
  Hand-back fades the sentence to the ground itself. The imprint's
  count tenant is the healthy tenant of a *different* slot and is
  never evicted, never faded, never involved. No `FIXED`, no
  `BLOCKING ON`, no restored line — enforced by type (§4.4): the
  slot's state enum has no restored value to print.

---

## (2) The class table — cause → sentence → TAP route

The verdict the table is built on — `liveWarrant()` (Ines §3;
**not yet in code**, §5): four conjuncts, all must hold —
(i) `a11y.instance != null` (service bound, live object);
(ii) `a11y.lastRootMono ≥ this residency's first stamp` (the event
pipeline processed events from *this* Deck visit);
(iii) `shield.instance?.protecting == true`;
(iv) `hasUsageStatsPermission()` (AppOps, the one synchronous truth
already in the repository, `MonolithRepository.kt:493`).

**Priority — one sentence, first failing conjunct in causal order
BLOCK → APPS → TIME wins the slot:** a11y → residency-stamp →
shield → usage. Grounds: a11y is upstream of everything (a dead
bind makes the stamp absent by consequence, not by second fact);
the stamp outranks shield/usage because an unproven pipeline may
not assert a measured cause — the retry is free and re-stamps,
then any real cause surfaces; shield (the enforcer, BLOCK) outranks
usage (its foreground sensor, APPS) — fix the blocker before its
eyes. Only the winner prints; the rest are never counted on the
Deck. Partial repair re-runs the same order (§4.5).

| # | Cause (verdict state) | Sentence — verbatim, Atkinson 700 15sp, glyph ink, no full stop | TAP route (the tap is the slot, §3) |
|---|---|---|---|
| 0 | All four conjuncts live | — *slot empty* | — *slot is not a target* |
| 1 | **A11y unbound** — `instance == null` (toggled off, bind released, or service killed; the wire cannot and does not split these — same pane fixes all three) | `BLOCKING IS OFF — TAP TO FIX` | Prima's Settings detour, direct: `Settings.ACTION_ACCESSIBILITY_SETTINGS` — exists today, `MainActivity.kt:433–436` (duplicate for the primer modal `:532–533`). Return runs the hand-back (§4). |
| 2 | **Bound, no residency stamp** — `instance != null` ∧ no stamp this residency (Ines's narrowed UNKNOWN; a first-run state, not a morning state) | `CAN'T CHECK BLOCKING — TAP TO RETRY` | **No Settings.** Retry = a Deck re-entry that generates the events: VM bounces the Deck destination out and back (the exit/entry window transitions are what the service stamps), then re-reads. If the re-entry still stamps nothing, the sentence stands — it is still true — and the same tap is offered again. Never escalates to a fabricated cause. |
| 3 | **Shield not protecting** — a11y live + stamped, `shield.protecting != true` (never started, start failed, or died) | `BLOCKING STOPPED — TAP TO RESTART` | **In-app, no Settings:** VM calls `MonolithShieldService.startShield(appContext)` (companion exists, `MonolithShieldService.kt:195–208`), then the hand-back sweep reads `protecting`. *Stopped*, not *off*: the a11y switch is on; a process died under it. Different cause, different verb, different route — my filed Class B, kept character-for-character. |
| 4 | **Usage-stats denied** — a11y + shield live, AppOps `GET_USAGE_STATS != MODE_ALLOWED` | `USAGE ACCESS IS OFF — TAP TO FIX` | Prima's Settings detour, direct: `Settings.ACTION_USAGE_ACCESS_SETTINGS` — exists today, `MainActivity.kt:438–441` (duplicate `:535–536`). Return runs the hand-back. |

**Combinations, stated once:** a11y-null + anything → row 1.
No-stamp + shield-down/usage-denied → row 2 (the pipeline is
unproven; downstream causes are not yet assertable). Shield-down +
usage-denied → row 3. Usage alone → row 4. There is no fifth
combination state and no "and n more" line — the repair sheet
behind the Guard line lists all lacks for the rested user; the
Deck prints the head of the queue (my beyond-file §10 anti-case
stands as the named cost of this).

**Struck rows, with the reason each dies:**
- *Overlay denied* (old Class D, `OVERLAY IS OFF — TAP TO FIX`):
  the wire does not read `canDrawOverlays` — interception in this
  codebase is an activity launch, not an overlay window
  (`MonolithShieldService.kt:131`, accessibility
  `launchInterceptScreen`). Arming must not refuse on a permission
  the seal does not use. Overlay stays a PARK-board fact only.
  (Note the trap in today's code: `SystemGuardScreen` receives
  `onOpenOverlaySettings` at `:62` and *never renders a row for
  it* — the intent at `MainActivity.kt:443–451` is wired and
  unreachable from that screen. Do not "fix" this by giving
  overlay a Deck sentence; fix it in PARK, another joint.)
- *List empty / list out of sync* (old E/F): not wire conjuncts.
  APPS faults were advisory under Rosa and stay advisory: the
  imprint count tenant prints the truth (`{n} APPS WILL SHUT`,
  n = 0 included) and the seal is not refused for a list fact.
- *Notification listener, device admin, battery*: present in
  today's `SystemGuardStatus` (`MonolithRepository.kt:29–38`),
  absent from the wire. No rows. A sentence needs a conjunct.

**Timing of tenancy:** standing (wire dead at Deck entry / at DOWN)
— the sentence is already in the slot before the gesture, and the
slide is inert (cap recess, accent withdrawn, travel clamped at
0.65 by Vega's joint). Mid-drag wire death (caught at the gate
re-read) — the slot does NOT swap under his thumb (my beyond-file
§4 law); it tenants on the cap's rest frame after the refusal
spring home, so lamp-word and sentence co-exist for zero frames.

---

## (3) The slot geography — ONE answer: TWO slots

**Slot S — the sentence slot (Rosa's).** This joint's object.

- **Address:** top edge = **track bottom + 8dp**; height **22dp**;
  width = the track's width (content width — 372dp at Kato's 20dp
  margins; the strip, the track and this slot share edges, one
  control cluster). Below it, the filed **S20** beat to the nav.
  The below-track stack is therefore: track 72 · **S8 · slot 22** ·
  S20 · nav. The slot is booked room (REGISTER: fixed sum 796,
  reserve = H − 796) — it is *not* carved from the reserve, and the
  reserve stays empty (Teo's NONE).
- **Reservation behaviour:** the 22dp exists in EVERY state —
  healthy, primed, refusal, advisory, sealed (frozen empty),
  hand-back. Nothing below it (S20, nav) and nothing above it
  (track, strip, task line) moves by a single dp in any tenancy
  change. **Δy = 0, forever.** A refusal that arrives fills a
  waiting room; it does not build one.
- **Type:** Atkinson Hyperlegible **700, 15sp, lh 22, tracking 0**,
  glyph ink (night `#EDE3D8`; day ink), left-aligned at the
  track's left edge, single line, never wraps — longest sentence
  (row 2, 35 chars ≈ 301dp) fits the 320dp floor column with
  ≈19dp to spare; on narrower columns tracking yields before
  size ever does (there is no tracking to yield at 0 — so at
  <340dp width the sentence truncates never and the *slot* is the
  red flag: return to coordinators, do not shrink type).
- **Healthy tenant: NOTHING.** The ground itself, exactly as the
  reserve's emptiness is the ground itself. Not the imprint
  tenant, not a health word, not a dash. Silence in this slot is
  not a missing rendering — it is the rendering.
- **Other lawful tenants, by priority:** refusal (this joint,
  700) **>** crossing advisory (Rosa/Pia, Atkinson **400** 15sp
  lh22, e.g. `ENDS 21:40, AFTER SLEEP BEGINS AT 20:00.`) **>**
  empty. Post-latch jam sentences (Opal's slice) tenant the same
  slot in a different controller phase and never compete with a
  pre-arm refusal — phases, not priorities, separate them.
- **Hit area:** only while a refusal tenants — full slot width ×
  **48dp** tall (the 22dp slot centred; +8dp up into the gap,
  +18dp down into the S20 beat; invisible). In health the slot is
  ground, not a button: no hit area exists. His weakest grip gets
  the app's most generous target on its most important line.

**Slot I — the imprint slot (Emil's).** A different object; this
joint does not touch it, and the builder must not merge them.

- **Address:** in the imprint line under the double rule in
  chrome — a different screen region entirely (top of the room,
  not under the track). Box 12dp, Anonymous Pro 700 **10sp**
  (plate), per Emil's typesetting.
- **Tenants (Emil's law, F-TENANCY):** count tenants only — idle
  `{n} APPS WILL SHUT` · primed = the identical string re-measured
  · sealed `{n} APPS HELD`, struck at the seat frame. TRUTH in
  sentence form does **not** tenant here any more (dossier §4-B:
  chrome carries the `GUARD n/4` state line, not a sentence);
  SEALED freezes it mid-session, as filed.
- **The reconciliation, in one line:** Slot I answers *how much
  will the seal hold* in plate, always; Slot S answers *why the
  seal is refused* in body type, only when refused. Two facts,
  two sizes, two addresses — merging them would put a 15sp
  sentence in a 12dp plate box (impossible) or a plate count in
  the refusal's room (a second, weaker rendering of readiness at
  the thumb). Today's code has neither slot as specced: the count
  tenant's nearest living relative is the instrument header's
  right word (`WILL SHUT $blockedCount APPS`, Anonymous Pro 10sp,
  `TactileDeckScreen.kt:213–221`), and under the slide today
  there is **nothing at all** (§5).

---

## (4) The hand-back — repair-measured, in code events

**The sequence, event by event:**

1. **Trigger (any of three, first wins):**
   (a) **Return from Settings** — `MainActivity`'s existing
   ON_RESUME observer (`MainActivity.kt:190–192`) gains one call:
   `viewModel.onRepairReturn()`. (Today that observer calls
   `refreshSystemGuard()`, which launches the heavyweight
   `ensureSeededAndSyncPackages()` launcher scan and fills the old
   `SystemGuardStatus` — `MonolithViewModel.kt:1061–1064`. The
   hand-back must NOT ride that path; it reads the wire, not the
   scan.)
   (b) **Deck re-entry** — `navigateTo(DECK)`
   (`MonolithViewModel.kt:355`) and Deck composition run the same
   call. For row 2, the retry tap *manufactures* this trigger.
   (c) **Registry callback** — the a11y bind/unbind and shield
   `protecting` writes (Ines/Otis registry joint) flip a VM
   `StateFlow`; a false→true edge while a refusal tenants runs
   the same call, without any navigation. This is the primary
   witness for the in-app shield restart (row 3), where he never
   left the Deck.
2. **The re-sweep (≤300ms) — what actually takes the time:**
   `liveWarrant()` is synchronous (≤5ms; four volatile/AppOps
   reads). The 300ms is spent **waiting for existence**: after
   the Settings toggle, Android re-binds the service
   asynchronously; at the resume frame `instance` may still be
   null under a switch that is already on. The sweep is defined
   as: read at trigger +0ms, then re-read on every registry
   callback and at +100/+200/+300ms, whichever completes first —
   first all-live read ends the sweep; deadline expiry with any
   conjunct false ends it as *not repaired* (the slot swaps to
   the then-first failing cause, §4.5 — never to a guess). The
   shield restart is the same shape: `startForeground` + first
   monitor iteration set `protecting` when they land, not when
   asked.
3. **The stability beat (500ms) — its witness, named:** the
   **stability pair** — two all-live verdicts, `V(t)` and
   `V(t+500ms)`, equal on all four conjuncts, **and** no registry
   clear-event (instance nulling, `protecting` drop, stamp
   invalidation) inside the window. Any clear-event or unequal
   verdict restarts the beat from the next all-live read; a
   service that cannot stay alive for half a second has not been
   repaired in any sense he can arm on. (Bind flapping during
   rebind is the real case this exists for.)
4. **The swap (90ms cross-fade) — endpoints, exact:**
   - Slot S: refusal sentence (Atkinson 700 15sp, glyph ink)
     fades 1→0; the incoming tenant is **empty ground** — the
     fade is a fade *to nothing*, and that nothing is the healthy
     state of this slot. No intermediate frame prints anything.
   - Same frame (the fade's first frame): the slide cap takes
     the accent back, the travel clamp lifts, and **one tick,
     5ms @ 0.4** fires — the repair confirmed in the hand. The
     chrome Guard line re-strikes to `GUARD 4/4` as a readout
     swap (its own fact, its own home, no fade owed).
   - Slot I is not an endpoint: its count tenant never left.
   - **Total: ≤300 + 500 + 90 = ≤890ms**, and not one millisecond
     of it prints a recovery. If he repaired while away from the
     Deck, all of this has already happened silently; he simply
     meets a healthy Deck.
5. **The never-a-restored-line enforcement — by type, not
   discipline:** the Deck host renders Slot S from a sealed state
   type with exactly two shapes — `Empty` and
   `Refusal(cause)` (plus the advisory string type owned by
   Rosa/Pia's joint, at 400). There is **no `Restored`/`Fixed`
   value in the type and no restored string constant in the
   codebase to pass it**. A builder cannot print `BLOCKING IS
   ON` without first amending this spec's type — which is the
   point: the law survives contact with the build because the
   build has no word for the lie.
6. **Partial repair:** a sweep that lands on a *different* first
   cause swaps sentence→sentence in the same slot (90ms
   cross-fade, tier unchanged) — no tick, no accent return, no
   stability beat (the beat is spent only on the full truth).
   The slide stays inert. Row order in §2 decides which sentence
   he meets next.
7. **Freeze:** while SEALED, no trigger runs the hand-back into
   Slot S — tenancy is frozen out mid-session (F-TENANCY); a
   mid-session death surfaces at unlock and on the receipt, per
   settled law, never as a live eviction.

---

## (5) Against the real code (file:line, verified this run)

Live tree `Focus-app/` (not the builds/ copies):

- **The slot's host today prints nothing under the slide.**
  `ui/screens/TactileDeckScreen.kt:478–504` — the pinned commit
  bar is a `Box` (12dp horizontal, 8dp vertical padding) holding
  only the `SlideToStartView` AndroidView (68dp tall, `:491–503`);
  `labelText = "SLIDE TO ARM SESSION"` (`:495`), badge =
  per-block `"${dialMinutes}M"` (`:496`). Below the bar: nothing —
  the outer `Column` ends. Slot S is a pure insertion into this
  Box-turned-Column; no existing element moves (Δy law is cheap
  here precisely because the space is currently unspent padding
  and air).
- **The only pre-arm check today runs AFTER the paid gesture, and
  it can be overridden.** `MonolithViewModel.kt:879–887`
  (`requestArmWithPreFlightCheck`, wired as `onArmSession` at
  `MainActivity.kt:318`): accessibility off → the primer modal
  (`ShieldPermissionPrimerModal`, `MainActivity.kt:530–539`),
  whose third button is **COMMIT & ARM SESSION NOW**
  (`AirlockAndDisarmOverlays.kt:446–460` region) — an arm-anyway
  path over dead blocking. Under wire law both die at this joint:
  the verdict moves to DOWN (Vega's joint consumes it), and the
  modal + arm-anyway are deleted, not reworded. The modal's two
  Settings chips are the only repair routes the Deck has today,
  and they sit behind a spent gesture — Prima's kill-shot, in
  code.
- **The repair intents exist — in MainActivity, not the VM.**
  Accessibility `Settings.ACTION_ACCESSIBILITY_SETTINGS`
  (`MainActivity.kt:433–436`, again `:532–533`); usage
  `Settings.ACTION_USAGE_ACCESS_SETTINGS` (`:438–441`, again
  `:535–536`); overlay `Settings.ACTION_MANAGE_OVERLAY_PERMISSION`
  + package Uri (`:443–451`). All are `runCatching { startActivity }`
  lambdas passed into `SystemGuardScreen` / the primer modal.
  The Deck receives none of them today — the slot's tap needs
  them re-plumbed to the Deck host (or an effect channel from
  the VM); no new intent must be invented.
- **Shield restart: no route exists.** `startShield` is called
  only from the arm path (`MonolithViewModel.kt:902`, inside
  `armMonolithSession`, after the session insert) and `stopShield`
  at `:917/:949`. The companion (`MonolithShieldService.kt:195–208`)
  swallows all exceptions; `onBind` returns `null` (`:178`); and
  the monitor loop **stops itself when there is no active
  session** (`:72–76`) — so today a pre-arm `startShield` is a
  no-op that self-terminates, and row 3's route depends on the
  registry joint making `protecting` meaningful pre-arm (§7).
- **The verdict's shape today cannot distinguish my rows.**
  `data/MonolithRepository.kt:29–47`: `SystemGuardStatus` is
  booleans for usage/overlay/a11y/notification/device-admin plus
  clock/HMAC fields — **no shield-running field, no bind state,
  no residency stamp at all**. Its a11y field comes from
  `isAccessibilityEnabled()` (`:525–538`): a `Settings.Secure`
  *string contains the package name* — it proves a toggle was
  once flipped, not that a service is bound (Ines §0). It is
  assembled on `Dispatchers.IO` in `getSystemGuardStatus()`
  (`:603–632`) — a coroutine, the opposite of the ≤5ms
  synchronous read. `hasUsageStatsPermission()` (`:493–515`) is
  the one field that survives into the wire unchanged.
  The accessibility service has **no `onServiceConnected`
  override, no instance registry, no stamp**: `onAccessibilityEvent`
  (`MonolithAccessibilityService.kt:39`) returns early for its own
  package (`:41`), launches a coroutine that returns when there is
  no active session (`:48–49`), and only then acquires the root
  (`:51`) — pre-session, the pipeline proves nothing, which is
  exactly the hole row 2's stamp exists to close.
- **Refresh triggers today:** VM init (`MonolithViewModel.kt:300`),
  navigating to GUARD (`:359`), ON_RESUME (`MainActivity.kt:190–192`),
  and the Guard screen's REFRESH chip (`SystemGuardScreen.kt:281–286`
  region, `onRefreshStatus`) — all filling the old status via the
  launcher-scan path (`:1061–1064`). The hand-back's trigger (a)
  reuses the ON_RESUME observer; it must not reuse its payload.

---

## (6) Code sites

| Site | Change |
|---|---|
| `ui/screens/TactileDeckScreen.kt` (`:478–504`) | Pinned bar `Box` → `Column`: slide (68dp, as-is) · `Spacer(8dp)` · **Slot S** (22dp, full width, `Text` Atkinson 700 15sp lh22 glyph ink when `Refusal`, nothing when `Empty`; 48dp hit area + `clickable` only when tenanted) · existing bottom padding. New params: `refusal: RefusalState`, `onRefusalTap: () -> Unit`. |
| `ui/MonolithViewModel.kt` | `RefusalState` derivation from the wire verdict (priority resolver over the four conjuncts); tap router (rows 1/4 → effect to MainActivity's existing intents; row 2 → Deck destination bounce + re-read; row 3 → `startShield` + sweep); hand-back machine (sweep re-reads, stability pair, callback edges); `onRepairReturn()`. Deletions at this joint: `requestArmWithPreFlightCheck` (`:879–887`) + `_preFlightCheckVisible` wiring (`:111–112`, `:888–894` region) — the modal path dies. |
| `data/MonolithRepository.kt` | Pure mapper `refusalCause(a11yBound, residencyStamped, shieldProtecting, usageAllowed) -> RefusalCause?` beside `SystemGuardStatus` (`:29`). The registries + `liveWarrant()` themselves are Ines/Otis's joint — consumed here, **not** priced here. |
| `MainActivity.kt` | Pass the two existing Settings lambdas (`:433–441`) to the Deck host as the refusal-tap effect (they already exist twice; this is their third, lawful home — the primer copies at `:530–539` die with the modal); ON_RESUME observer (`:190–192`) gains `viewModel.onRepairReturn()` beside the legacy refresh. |

Explicitly NOT touched by this joint: the imprint slot and its
count tenants (Emil's joint), the strip and its cells (Rosa's /
Otis's), the slide view's clamp and refusal callback plumbing
(Vega's joint — this joint owns the sentence those callbacks
summon), PARK's board (the Guard-line tap still opens PARK for
the full board; the sentence tap is the fast path for the named
cause only).

---

## (7) Price in lines

| Piece | Gross | Net |
|---|---|---|
| Slot S composable + pinned-bar restructure + hit area (Deck host) | ~60 | ~60 |
| Refusal state + priority resolver (VM) | ~40 | ~40 |
| Tap router — 4 routes (VM + MainActivity effect wiring) | ~55 | ~55 |
| Hand-back machine — sweep, stability pair, callback edges, `onRepairReturn` (VM) | ~75 | ~75 |
| `refusalCause` mapper (Repository) | ~30 | ~30 |
| Deletions: pre-flight modal path + arm-anyway wiring (VM + MainActivity) | — | −35 |
| **Total, this joint** | **≈260** | **≈225** |

4 files. No new files, no DB, no migration. **Dependency, priced
elsewhere and not double-counted:** Ines §3's Live Wire build
(registries ~75 + shield registry ~45 + `liveWarrant()` ~55 +
VM StateFlow ~70) is the floor this joint stands on — without it,
§2's rows 2–3 are indistinguishable and this table collapses to
today's Settings-string guess. Tests (priority order, stability-pair
restart on flap, no-restored-value render test, sentence→sentence
partial swap) ≈60 lines, owed by the builder, outside the joint.

---

## (8) Risks

- **Shield `protecting` is the shakiest conjunct.** Today's
  monitor loop self-stops without a session
  (`MonolithShieldService.kt:72–76`) and `startShield` swallows
  its own failures (`:195–208`). Until the registry joint makes
  `protecting` a real pre-arm state, row 3 can neither be
  detected nor repaired. Fallback if that joint slips: row 3
  does not print — shield folds silently into the wire's a11y
  read and the table ships at 3 rows rather than fabricate a
  fourth. Never print RESTART for a restart that no-ops.
- **Bind latency > 300ms on OEM skins.** The sweep deadline can
  expire under a healthy rebind; the sentence then stands past
  his return — truthful at last read — and the registry callback
  (trigger c) completes the hand-back whenever the bind lands.
  The failure mode is a late hand-back, never a false one. If
  device trials show >300ms binds are *common* on his handset,
  the number to move is the sweep budget, not the law.
- **The residency stamp may never fire for in-app transitions.**
  The a11y service filters its own package
  (`MonolithAccessibilityService.kt:41`) and stamps (per Ines)
  only after root acquisition. If Deck re-entry generates no
  stampable event, row 2 becomes a loop: CAN'T CHECK forever,
  retry offered forever. Device trial settles it; if it fails,
  row 2's retry route — not its sentence — is what gets
  re-specced (e.g. retry via a Settings round-trip that
  guarantees foreign-window events).
- **lh22 in a 22dp box has zero leading slack.** At system font
  scale >1.0 the glyphs can clip vertically. Rosa filed these
  numbers and the Δy law forbids growing the box; the honest
  device check is a font-scale render pass before this ships.
- **Serial repair remains the priced cost** (my beyond-file §10):
  a triple-failure morning costs up to three Settings round
  trips, one sentence at a time. The mitigation is real but
  off-Deck (PARK's board lists every lack; the Guard line names
  the first). If he lives that morning and curses it, the
  amendment is pre-drafted in my beyond-file — I do not smuggle
  it in here.

---

## (9) Fork for Ren

**FORK — the sentence tap's first stop: the class pane directly
(recommended) vs the PARK board first.**
(A) **Direct** (as specced in §2): row 1/4 taps open the exact
Android pane in one tap; rows 2/3 never leave the app. The
sentence names one cause and its tap fixes that cause — Prima's
detour is the repair itself, and an intermediate board read at
04:58 is a second decision the tap didn't promise.
(B) **PARK first** (the dossier §4-B letter): the tap opens PARK,
where the board lists all lacks and each FIX key opens its pane.
One repair home for everything; answers the serial-repair
anti-case; costs +1 tap and a board read on every refusal
morning, including the common single-cause one.
**My recommendation: (A).** The Guard line already opens PARK —
the board has a door. The sentence's tap is the *fast* door, for
the one cause the Deck has already named; routing it through the
board makes TAP TO FIX a promise the tap doesn't keep. If Ren
rules (B), §2's routes change in one column (rows 1/4 → PARK
with the row pre-expanded) and no other number in this file
moves.

No other fork. The two-slot geography, the 4-row table, and the
hand-back sequence are stated as rulings, not options.

---

## (10) Mail / wall note

Inbox at start: **empty** — all six letters from prior seatings
(Orson + Mara, citation-lift relays; Emil, imprint-tenancy-law;
Joss, 0458-truth-variant; Rosa, freeze-scope; Emil,
closed-day-tense) were already in `read/`, re-read for this joint;
nothing new arrived, nothing to move. Letters sent this run:
**Emil** (the two-slot reconciliation — his imprint keeps its
count tenants entire; the sentence does not tenant his slot
under the final ruling, and his slot is not evicted by mine);
**Rosa** (her slot hosts the final pair — the `NOTHING WILL BE
HELD.` wording she filed is superseded there, the slot, priority
and reservation are hers and stand); **Joss** (his §12 slot
difference is settled by the dossier: the sentence's home is
neither his chrome header slot nor my old imprint claim — it is
Rosa's slot; his adopted hand-back stands, with its healthy
tenant named: the empty slot). One signed entry appended to the
Commons wall: the table, the two-slot answer, and the hand-back's
witness, in one breath, with the fork named for Ren.

— Suki, Wild · space
