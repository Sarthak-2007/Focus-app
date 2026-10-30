# DIAG-2 — Edge states of the cold-start cure: the not-yet-bound window, the grant read, the FIX doors, the UNSET read, and the grace law

**Agent:** DIAG-2 (Diagnostician), BINDUNG's cold-start cure cell, issue #7
**Repo / branch:** Sarthak-2007/Focus-app, `agent/issue-7-coldstart`
**Anchors:** pre-cure base `4378dae` for the mechanisms in §§1–4 (cited as
"base"); the landed cure at HEAD (`1b641aa` self-probe, `55bcae4` cured
refusal table) where a section says HEAD. All tracing via `git show` /
`git grep` on pinned commits — the working tree carried other seats'
uncommitted changes while I traced and I did not touch them. I modified
no app code.

**Headline verdicts:**

1. The not-yet-bound window is REAL and UNGRADED in both base and cure:
   row 1 (`!a11yBound`) prints `BLOCKING IS OFF — TAP TO FIX` from the
   first warrant read, with no grace, at base
   (`MonolithComponentLogic.kt:191-192`) and at HEAD (`:246-247`). It
   self-corrects event-driven on bind (§1) — but bind timing itself is
   a PLATFORM FACT with no code bound, so the flash is `[0, bind
   latency)`, and the cure as landed does not close it. DAY-1's D-1,
   FEEL-1's F-1, CRIT-1's C-5 and TECH-1's B-Major-2 name the same hole;
   §5 prices its closure. **CONFIRMED.**
2. The repo already owns the grant read the closure needs:
   `MonolithRepository.isAccessibilityEnabled()` (base `:777-787`). It
   is currently NOT an input to the warrant. No `AccessibilityManager`
   usage exists anywhere in the tree. §2.
3. Both FIX doors land on their owning Settings panes. **CONFIRMED**
   (§3), with one honesty caveat: the tap opens the pane; the rebind
   itself is the user's toggle inside it — no code rebinds.
4. UNSET → `USAGE ACCESS IS OFF` is cured at base and HEAD, and a
   sweep-lag misprint of the *sentence* is **REFUTED** — the warrant
   never reads the sweep for usage (§4). Two smaller seams remain:
   exception→UNSET fabrication, and the sweep's Boolean collapse
   (Guard/Park only, not the sentence).
5. §5 states the exact grace law as one implementable paragraph plus
   the full truth table for the cured `refusalSentenceFor`.

---

## 1. The not-yet-bound window — what prints, what re-reads, how long the flash can last

### What prints, and from when

- Row 1 of the base table fires on the warrant's `a11yBound` alone —
  no state check, no age check, no usage check:

  ```
  MonolithComponentLogic.kt:191-192 (base)
      !warrant.a11yBound ->
          RefusalSentence("BLOCKING IS OFF — TAP TO FIX", RefusalAction.FIX_ACCESSIBILITY)
  ```

  HEAD's cured table keeps this row first and still ungrace'd
  (`MonolithComponentLogic.kt:246-247` at HEAD): the landed grace
  lives only in the bound-UNPROVEN-unstamped row (HEAD `:252-266`).
- The state behind it: `deriveWarrant` maps `!a11yBound → OFF` before
  every other row (`WatchStore.kt:390-391`, base), and
  `standingWarrant` gathers bound-ness as
  `MonolithAccessibilityService.instance != null`
  (`MonolithRepository.kt:693`, HEAD numbering; same at base
  `:692-694`). On a cold process `instance` is `null` until the system
  calls `onServiceConnected` — so every warrant read in the window
  derives OFF.
- The print can appear from the first composed frames. `deckWarrant`
  starts `null` (`MonolithViewModel.kt:695-696`, base) and a null
  warrant prints silence (`MonolithComponentLogic.kt:189`), but the
  first refresh is not user-gated: Deck entry runs
  `LaunchedEffect(currentDestination)` → `noteResidencyStart()` +
  `runWatchSweep()` (`MainActivity.kt:201-205`, base), the residency
  `poke()` publishes a snapshot (`WatchStore.kt:249-252`), and the
  ViewModel's snapshot collector re-reads the warrant on **every**
  emission (`MonolithViewModel.kt:923-927`). So the sequence on a
  slow-binding phone is: Deck composes → first refresh lands unbound →
  `BLOCKING IS OFF — TAP TO FIX` is on the glass while the grant is
  present in Settings and the service is mid-bind. The 800 ms bind in
  the brief is squarely inside this window: yes, that is a false
  flash, and the same flash also drives the slide gate, because the
  gate reads the same OFF derivation (DAY-1 §Moment 1(b) traced the
  refused first slide; the derivation it reads is
  `WatchStore.kt:390-391`).

### What re-reads after bind — the self-correction chain, link by link

The chain exists and is complete at base; nothing polls for it:

1. `MonolithAccessibilityService.onServiceConnected` sets
   `instance = this` **first** (`MonolithAccessibilityService.kt:346`,
   base), then calls `WatchStore.onAccessibilityBound(true)`
   (`:352`).
2. `onAccessibilityBound` is exactly `stampPermission(ACCESSIBILITY,
   bound)` (`WatchStore.kt:276-277`), which builds a fresh measurement
   and calls `publish` (`WatchStore.kt:473-487`).
3. `publish` is `@Synchronized` and ends with
   `_snapshots.value = next` (`WatchStore.kt:496-497, 538`) — a real
   StateFlow emission (the generation/measurement change defeats
   StateFlow's equal-value suppression).
4. The ViewModel collector (`MonolithViewModel.kt:923-927`) receives
   it and calls `refreshDeckWarrant()` (`:698-700`), which re-runs
   `standingWarrant()` over the living facts — `instance` is now
   non-null, so the next warrant derives bound.
5. `deckWarrant` is collected by the Deck host with lifecycle
   (`MainActivity.kt:258`) and the slot re-evaluates
   `refusalSentenceFor(deckWarrant, nowMono)`
   (`TactileDeckScreen.kt:1175`, base).

At HEAD the chain gains its last link: `onServiceConnected` also
re-probes the pipeline when a residency is already active
(`MonolithAccessibilityService.kt:352-354` at HEAD;
`probePipelineNow` at HEAD `:377-391` stamps `lastRootMono` + `poke()`
on a live root), so bind → stamp → LIVE can complete with no event
and no hand.

**Neither the 1-second VM ticker nor the Deck's 5-second tick is in
the correction path.** The ticker re-reads only a stale-for-print
warrant (`MonolithViewModel.kt:1144-1153`, base; threshold
`STALE_AFTER_MS = 45_000`, `WatchStore.kt:168`). Correction is purely
event-driven off the bind publication: once the system binds, the
false OFF clears within one publish → collect → recompose cycle.
(Code gives no millisecond figure for that cycle and I invent none;
it contains no delay, no debounce, and no timer.)

### Time-bounding the flash — what code can and cannot say

- **Lower bound: zero.** If the first warrant read happens after the
  bind (warm process, fast bind, collector subscribing late), the
  unbound row never prints at all.
- **Upper bound: the remaining bind latency after the first unbound
  read — and code cannot bound it.** PLATFORM FACT: an accessibility
  service is bound by the system, on the system's schedule, after the
  process starts; Android offers the app no deadline, no ordering
  guarantee against Activity composition, and no API to bind its own
  accessibility service. Nothing in this tree attempts one (no
  `bindService` for the a11y service exists; the service's lifecycle
  entry points are system-called, `MonolithAccessibilityService.kt:
  344-355` base). Therefore: flash duration ∈ **[0, bind latency)**,
  bind latency is OEM/system behaviour, and any millisecond figure
  for it — 800 ms included — is a scenario, not a bound.
- **The one case where it is not a flash at all:** grant absent in
  Settings. Then `instance` stays null, no `onServiceConnected` ever
  fires, and OFF is the resting truth, not a transient. The tragedy
  of the window is that frame one cannot tell these two cases apart
  *from `instance` alone* — which is exactly what §2's grant read
  fixes.
- **What the flash becomes at bind, per version.** Base: OFF →
  the fog (bound, unstamped, usage ALLOWED — DIAG-1 §6), i.e. one
  false print traded for another. HEAD: OFF → silence if the
  residency is still inside its 4 s grace (HEAD
  `MonolithComponentLogic.kt:252-266`), then LIVE when the rebind
  probe stamps. The cure shortens the *tail* of the window; its
  *head* — the ungrace'd unbound row — is untouched. That head is
  the remaining defect §5 closes.

---

## 2. Knowing the grant is present before binding — the repo's existing helpers

### Exhaustive search result

`git grep` over `4378dae -- app/src` for
`ENABLED_ACCESSIBILITY_SERVICES`, `isAccessibilityServiceEnabled`,
`AccessibilityManager`, `enabled_accessibility`:

| Hit | What it is |
|---|---|
| `MonolithRepository.kt:777-787` | **`isAccessibilityEnabled()`** — reads `Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES` (`:779-782`) and returns `enabledServices.contains(context.packageName, ignoreCase = true)` (`:783`); null string or any exception → `false` (`:782, 784-786`) |
| `MonolithRepository.kt:48, 62, 882` | The result plumbing into `SystemGuardStatus.isAccessibilityServiceEnabled`, produced only by `getSystemGuardStatus()` (`:859-891`) |
| `MonolithViewModel.kt:500` | A hardcoded `false` default for that status field |
| `AccessibilityManager` | **Zero hits in the whole tree** — no `getEnabledAccessibilityServiceList`, no `isEnabled`, nothing. Any cure built on `AccessibilityManager` would be a new dependency path, not a reuse. |

### Where the existing helper is *not* wired — the gap

- `standingWarrant()` gathers bound, stamped, usage, shield — never
  the grant (`MonolithRepository.kt:692-701`, base and HEAD alike).
- The sweep's ACCESSIBILITY query answers **bind**, not grant:
  `WatchItemId.ACCESSIBILITY -> MonolithAccessibilityService.instance
  != null` (`MonolithRepository.kt:712` at HEAD; `:711-712` base).
- The helper's only live consumer is `getSystemGuardStatus()`, whose
  result "renders NOWHERE" by its own call-site comment — PARK fires
  it as a warm-up diagnostic only (`MainActivity.kt:404-420`, base).

So today the Settings grant and the warrant never meet. The
information needed to distinguish "grant present, bind pending" from
"grant absent" has been in the repository the whole time, on a dead
wire.

### Which helper the cure should reuse — ruling

**Reuse `MonolithRepository.isAccessibilityEnabled()`
(`MonolithRepository.kt:777-787`) verbatim. Do not introduce
`AccessibilityManager`.** Reasons, each checkable:

1. It reads the authoritative store: `ENABLED_ACCESSIBILITY_SERVICES`
   is the system's own list of enabled services; presence there *is*
   the grant, independent of binding.
2. It is synchronous and cheap in exactly the way `usageAccess()`
   already is — and `standingWarrant()`'s gather is already a set of
   synchronous live reads (`MonolithRepository.kt:692-700`), so the
   grant joins the existing pattern with no new threading.
3. Its failure mode is fail-closed (`false` on null/exception), which
   under §5's law degrades to today's behaviour (immediate OFF),
   never to a false silence past the grace.
4. Precision is sufficient *for this app*: the substring test is the
   package name, and the manifest declares exactly one accessibility
   service (`AndroidManifest.xml:67`,
   `.service.MonolithAccessibilityService`), so a package match
   cannot be satisfied by a second service of this app. **Limitation,
   stated so nobody discovers it later:** the helper does not parse
   the colon-separated flattened `package/class` entries, so it
   cannot distinguish *which* of an app's services is enabled —
   moot at one declared service; if a second a11y service is ever
   declared, this helper must be hardened to a component match in
   the same change.

---

## 3. The FIX doors — what FIX_ACCESSIBILITY / FIX_USAGE taps actually open

**CONFIRMED: both doors land on the pane that owns the lack.**

The dispatch chain, base lines:

1. The Deck slot's `onAction` maps the action enum to host callbacks:
   `FIX_ACCESSIBILITY -> onFixBlockingAccess()`,
   `FIX_USAGE -> onFixUsageAccess()`
   (`TactileDeckScreen.kt:1177-1180`).
2. The host binds those callbacks to `launchSettings` with the exact
   intents (`MainActivity.kt:679-690`):
   - `onFixBlockingAccess` → `Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)`,
     label `"ACCESSIBILITY"` (`:679-684`);
   - `onFixUsageAccess` → `Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)`,
     label `"USAGE ACCESS"` (`:685-690`).
3. `launchSettings` (`MainActivity.kt:486-498`) fires the exact pane
   first; on an OEM throw it falls back to Settings home; if even
   that fails it tells the user in words via Toast. A FIX tap never
   dies silently.
4. The PARK board's FIX door is the same pair of panes through
   `openFixPane` (`MainActivity.kt:503-508`) — one door per grant,
   everywhere.
5. The return trip is instrumented: `ON_RESUME` fires
   `runWatchSweep()` + `refreshDeckWarrant()`
   (`MainActivity.kt:429-437`), so the hand-back is judged against
   fresh reads, not the pre-trip warrant.

**The past-grace OFF sentence specifically:** at HEAD the
bound-but-blind row and the not-bound row both carry
`FIX_ACCESSIBILITY` (`MonolithComponentLogic.kt:246-247, 263-265` at
HEAD), so both land on the accessibility pane — the pane where the
one act that can change the fact lives: toggling the service off/on,
which makes the system unbind and rebind it, which fires
`onServiceConnected`, which at HEAD re-probes and stamps
(`MonolithAccessibilityService.kt:352-354` at HEAD).

**Honesty caveat (the limit of the confirmation):** the tap opens
the pane; it does not itself rebind anything, and no app code can —
PLATFORM FACT: only the system binds an accessibility service, in
response to the enabled-list changing. The "real remedy = rebind"
claim is therefore true as a *user ritual on that pane* (toggle off,
toggle on), not as an app action. The sentence's verb "TAP TO FIX"
names the door correctly; the fix completes at the toggle and is
verified on return by link 5 above.

---

## 4. UsageAccess UNSET — semantics, the cured print, and the sweep-timing question

### Semantics in code

`MonolithRepository.usageAccess()` (base `:651-677`):

- Reads AppOps `OPSTR_GET_USAGE_STATS` for this uid/package —
  `unsafeCheckOpNoThrow` on Q+, `checkOpNoThrow` below
  (`:655-667`).
- Mapping (`:669-673`): `MODE_ALLOWED → ALLOWED`;
  **`MODE_DEFAULT → UNSET`**; everything else (`MODE_IGNORED`,
  `MODE_ERRORED`, …) → `DENIED`.
- Two fabricated-UNSET paths: AppOps service null → `UNSET`
  (`:653-654`), and **any thrown exception → `UNSET`** (`:674-676`).
- The function's own doc (`:648-650`): the old Boolean collapsed
  UNSET ("never asked, fresh device") into DENIED and lied on first
  runs — the three-valued read is itself an earlier cure.

PLATFORM FACT, for the record: `MODE_DEFAULT` is AppOps' "no
explicit decision recorded" mode — for usage stats it is the
fresh-install / never-granted state, distinct from an explicit
ignore/deny. The code's UNSET name is faithful to that.

### The cured print — CONFIRMED at base and HEAD

- Base table: UNSET is excluded from the fog row
  (`MonolithComponentLogic.kt:193-194`) and claimed by its own row:
  `USAGE ACCESS IS OFF — TAP TO FIX` / `FIX_USAGE`
  (`:203-208`) — the #6 C3/F9 cure, present already at `4378dae`.
- HEAD table: UNSET (and DENIED) rows stand *above* the unstamped
  row (`MonolithComponentLogic.kt:248-251` at HEAD), so no stamp
  state can mask them. The Deck tap-handler's old UNSET reroute
  (base `TactileDeckScreen.kt:1181-1190`) is now redundant but
  harmless: the sentence already names the usage door.
- Derivation interaction (DIAG-1 §3's correction, restated because
  the table in §5 depends on it): bound ∧ stamped ∧ UNSET derives
  UNPROVEN via `WatchStore.kt:394`, yet prints the usage sentence,
  because the sentence table keys on `usage`, not on `state` alone.

### Can a fresh UNSET read in the first frames misprint before the sweep lands? — REFUTED for the sentence; two real seams named

The sweep-lag theory fails on the wiring:

1. **The warrant never reads the sweep for usage.**
   `standingWarrant()` calls `usageAccess()` synchronously at every
   gather (`MonolithRepository.kt:698` at HEAD; `:692-700` base).
   There is no cached usage value in the warrant path for a sweep
   landing to correct.
2. **The first print cannot precede the first live read.**
   `deckWarrant` is `null` until the first `refreshDeckWarrant()`
   (`MonolithViewModel.kt:695-700`), and null prints silence
   (`MonolithComponentLogic.kt:189`). The earliest UNSET sentence is
   therefore the product of a contemporaneous AppOps call — truth at
   that instant by construction, not a stale pre-sweep value.
3. **What the sweep actually carries for usage is a different,
   coarser channel:** `watchGrantQuery(USAGE_ACCESS)` collapses to
   Boolean `usageAccess() == ALLOWED` (`MonolithRepository.kt:713`
   at HEAD), so the sweep stamps UNSET and DENIED identically as
   MISSING — for the Guard line and the Park board, never for the
   refusal sentence. Sweep queries do land async (dispatched at Deck
   entry from the same effect as the residency,
   `MainActivity.kt:201-205`; run in parallel on
   `Dispatchers.IO`, `WatchStore.kt:217, 329-347`), and each landing
   publishes → the collector re-reads the warrant
   (`MonolithViewModel.kt:923-927`) — but that re-read re-asks AppOps
   live; it does not import the sweep's Boolean.
4. **Consequence:** if AppOps says `MODE_DEFAULT` in the first
   frames, `USAGE ACCESS IS OFF` is not a misprint awaiting a sweep
   correction — the mode only changes when the user grants on the
   usage pane, and the return-trip refresh (§3, link 5) is what
   clears it.

**Seam A — exception-UNSET (real, small).** A thrown AppOps call or a
null AppOps service also returns UNSET (`:653-654, 674-676`). A
transient platform hiccup in the first frames would therefore print
the usage sentence on a granted phone until the next refresh (next
snapshot emission, or the collector path in point 3) re-reads it
successfully. Code proves the seam exists; whether a given device
ever throws here is not decidable from this tree — I mark the
*occurrence* PLATFORM-DEPENDENT, the *mechanism* CONFIRMED.

**Seam B — UNSET gets no grace, and needs none.** Unlike bind and
stamp, the AppOps mode is not racing anything the machine controls:
there is no pending process that turns `MODE_DEFAULT` into
`MODE_ALLOWED` on its own. §5 therefore prints UNSET immediately at
every age — gra
...[truncated 6736 chars]