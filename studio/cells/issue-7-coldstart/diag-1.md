# DIAG-1 — Independent verification of BINDUNG's cold-start fog chain

**Agent:** DIAG-1 (Diagnostician), BINDUNG's cold-start cure cell, issue #7
**Repo / branch:** Sarthak-2007/Focus-app, `agent/issue-7-coldstart`
**Verdict anchor:** base commit `4378dae` — the pre-cure code BINDUNG diagnosed.
All `file:line` citations below are to `4378dae` unless a section says HEAD.

> **Moving-HEAD disclosure (material to every verdict):** this clone was live
> while I traced it. HEAD moved three times during the investigation —
> `1b641aa` (active self-probe), `55bcae4` (fog sentence deleted, refusal table
> reordered), `1b44c4a` (RECORD.md) — with cure test files still uncommitted in
> the tree at writing time. I therefore re-traced every link against
> `git show 4378dae:<path>` / `git grep 4378dae`, not the working tree. §7 states
> exactly which of my base verdicts the cure commits change at HEAD. I modified
> no app code.

**Headline verdict: BINDUNG's chain is CONFIRMED in substance — with two
corrections that matter (§3, §4) and one ordering subtlety the chain must not
overstate (§4). The mechanism is a residency-stamp race with no guaranteed
stamp, and the RETRY verb is provably incapable of clearing it.**

---

## 1. `noteResidencyStart` / `stampedThisResidency` — CONFIRMED

- Definition: `WatchStore.kt:249-252` (base) — sets
  `residencyStartMono = SystemClock.elapsedRealtime()` and `poke()`s. Nothing
  else: no probe, no event request, no service interaction.
- Stamp test: `WatchStore.kt:255-257` —
  `residencyStartMono > 0 && lastRootMono >= residencyStartMono`.
- **Sole production caller:** `MainActivity.kt:201-209` — a
  `LaunchedEffect(currentDestination)` inside `MonolithRootApp`, firing when
  `currentDestination == MonolithDestination.DECK`. `git grep noteResidencyStart
  4378dae -- app/src` returns exactly two hits: the definition and this call.
- Lifecycle beat: composition-side, after first composition of the root app —
  i.e. after `setContent` in `onCreate` (`MainActivity.kt:~118-145`). The
  destination defaults to DECK (`MonolithViewModel.kt:299`,
  `_currentDestination = MutableStateFlow(MonolithDestination.DECK)`), so the
  effect fires on every cold open without any navigation.
- Thread: the effect body runs on the composition/main-thread coroutine of the
  root app; `noteResidencyStart` itself is a plain `@Volatile` write +
  `poke()` → `@Synchronized publish` (`WatchStore.kt:310-312` at HEAD numbering;
  same structure at base). No IO hop precedes the residency write.
- **Every-Deck-entry reset:** the effect is keyed on `currentDestination`, so
  returning to the Deck from any tab, and any Activity recreation, starts a
  *new* residency and discards all earlier stamps by construction
  (`lastRootMono >= residencyStartMono` fails for any pre-entry stamp).
- Other writer of `residencyStartMono`: only `resetForTest()` zeroing it
  (`WatchStore.kt:607`), called solely from `app/src/test` — never production.
- `WatchStore.bind()` (called in `MainActivity.onCreate` and in
  `onServiceConnected`) restores only LAST PROVED; it does **not** restore or
  seed residency/stamp state. Residency is process-scoped by design
  (`WatchStore.kt` header, base ~:45-57: measurements deliberately not
  persisted; "a new process is a new residency").

**Refinement BINDUNG should carry:** `ON_RESUME` does **not** re-note a
residency. The resume observer (`MainActivity.kt` DisposableEffect, base)
calls `runWatchSweep()`, `onAppResumed()`, `refreshDeckWarrant()` only. A
residency therefore dates from Deck *entry*, not from app resume, unless the
Activity was recreated (which re-runs the LaunchedEffect).

## 2. Every writer of `MonolithAccessibilityService.lastRootMono` — CONFIRMED (at base)

Exhaustive `git grep lastRootMono 4378dae -- app/src` — complete list:

| Site | Effect |
|---|---|
| `MonolithAccessibilityService.kt:312` | declaration, `= 0L`, `@Volatile`, `internal set` |
| `MonolithAccessibilityService.kt:470` | **the only positive stamp** — inside `onAccessibilityEvent`, `lastRootMono = SystemClock.elapsedRealtime()` + `WatchStore.poke()` |
| `MonolithAccessibilityService.kt:452` | reset `= 0L` in `onUnbind` (guarded `instance === this`) |
| `MonolithAccessibilityService.kt:969` | reset `= 0L` in `onDestroy` (same guard) |
| `WatchStore.kt:257` | read-only (the stamp test) |

- **No other stamper exists at base.** Confirmed across the whole `app/src`
  tree (main + test). In particular, `onServiceConnected`
  (`MonolithAccessibilityService.kt:344-355`) sets `instance = this` and calls
  `WatchStore.onAccessibilityBound(true)` but does **not** stamp
  `lastRootMono` — binding alone is never pipeline proof.
- Event-path stamp ordering: the stamp block (`:460-476`) runs **before** the
  package early-returns (`:478-479`), so events from Monolith's own package
  and SystemUI *do* stamp. A stamp is withheld only when
  `rootInActiveWindow == null` or the query throws (`noteDetectionFailure`).
  Any retelling of the chain that claims own-package events are excluded is
  wrong; the gap is timing, not filtering (see §6).
- The service config (`res/xml/accessibility_service_config.xml`) subscribes
  only to `typeWindowStateChanged|typeWindowContentChanged`, no package
  filter, `notificationTimeout=80`. No other event type can stamp.

## 3. `deriveWarrant` + `standingWarrant` — CONFIRMED with a correction

- `standingWarrant` (`MonolithRepository.kt:692-701`): gathers only —
  `a11yBound = MonolithAccessibilityService.instance != null`;
  `stamped = a11yBound && WatchStore.stampedThisResidency()`;
  `usage = usageAccess()`; `shieldProtecting` rides along, **not** a conjunct.
  State logic lives solely in `WatchStore.deriveWarrant` (`WatchStore.kt:383-405`).
- `deriveWarrant` table (base `:390-396`), evaluated in this order:
  1. `!a11yBound` → OFF
  2. `usage == DENIED` → OFF
  3. `!stamped` → UNPROVEN
  4. `usage != ALLOWED` (i.e. UNSET) → UNPROVEN
  5. else → LIVE

**Correction to the chain's shorthand:** "UNPROVEN is exactly
(bound && !stamped)" is **not exact**, even "modulo usage rows" unless the
modulo is spelled out. The full truth is:

```
UNPROVEN  ⇔  bound ∧ usage ≠ DENIED ∧ ( ¬stamped ∨ usage = UNSET )
OFF       ⇔  ¬bound ∨ usage = DENIED
LIVE      ⇔  bound ∧ stamped ∧ usage = ALLOWED
```

There is a second, stamp-independent UNPROVEN room — **bound ∧ stamped ∧
UNSET** — which reaches UNPROVEN via row 4. It does not print the fog
(§4's UNSET exclusion routes it to the usage sentence), but any cure or test
that equates UNPROVEN with unstamped will misclassify it.

## 4. `refusalSentenceFor` — fog preconditions CONFIRMED; DENIED ordering REFUTED for production warrants

Base `MonolithComponentLogic.kt:188-213` (single-arg table; the timed
overload `:222-226` first returns null on a stale-for-print read,
`WatchStore.STALE_AFTER_MS = 45_000`).

Fog row fires iff **all** of:
1. `warrant != null`, and (timed path) read not stale-for-print;
2. `warrant.a11yBound == true` (row 1, `!a11yBound` → "BLOCKING IS OFF", precedes);
3. `warrant.state == WarrantState.UNPROVEN`;
4. `!warrant.a11yStampedThisResidency`;
5. `warrant.usage != UsageAccess.UNSET` (the C3 exclusion, `:193-194`).

→ `RefusalSentence("CAN'T CHECK BLOCKING — TAP TO RETRY", RETRY_CHECK)`,
rendered by the **only** call site, `TactileDeckScreen.kt:1175`
(`else -> refusalSentenceFor(deckWarrant, nowMono)` in the sentence slot,
when not Jammed/Proving/session-running/armSentence).

**Usage-DENIED ordering — the subtlety:** *textually* the fog row (`:193`)
sits above the DENIED row (`:201`), so a hand-built warrant
(UNPROVEN, unstamped, DENIED) prints the fog. **In production that warrant
is unreachable:** `deriveWarrant` row 2 maps DENIED → OFF *before* the stamp
row is consulted, so a real unstamped+DENIED read has `state == OFF`,
fails fog precondition 3, and prints "USAGE ACCESS IS OFF — TAP TO FIX"
(`:201-202`). The shipped test suite confirms the production reading: it
exercises DENIED only as OFF (`SliceDGestureAndStripTest.kt:189`, base) and
never constructs UNPROVEN+DENIED.

So: if the chain claims "fog outranks the usage sentence when
unstamped+DENIED" as a *user-visible* behaviour — **REFUTED**. As a statement
about branch order inside `refusalSentenceFor` alone — confirmed, and
production-irrelevant. For the actual cold-open case (bound, unstamped,
usage **ALLOWED** — the granted phone), fog is exactly what prints.

**RETRY is a dead verb (chain-relevant, verified):** the Deck wires
`onRetryBlockingCheck = viewModel::refreshDeckWarrant`
(`MainActivity.kt:691` at HEAD numbering; same wiring at base), and
`refreshDeckWarrant()` (`MonolithViewModel.kt:698-700`) only re-runs
`standingWarrant()` over the same living facts. It does not re-note the
residency, does not bind or probe the service, and cannot manufacture an
a11y event. If no event has stamped, a retry re-derives the identical
UNPROVEN warrant and the fog reprints — indefinitely.

## 5. Other resting-fog paths — NONE FOUND (chain's single-site claim CONFIRMED)

Searched all of `app/src/main` at base for the fog string, equivalents
("can't check", "check blocking", "TAP TO RETRY", `RETRY_CHECK`), and every
`refusalSentenceFor` / `RefusalSentenceSlot` reference:

- The string `"CAN'T CHECK BLOCKING — TAP TO RETRY"` exists at exactly **one**
  site: `MonolithComponentLogic.kt:200`. No second copy, no resource-string
  copy (`res/values/strings.xml` clean).
- `LauncherBlockScreen.kt`: **no hits** — no refusal sentence, no UNPROVEN
  print, no retry text. It is not a fog path.
- `GuardLine.kt`: **no hits**. `guardLineFor` prints only
  `GUARD n/6` / `GUARD n/6 — <name>` (`GuardLine.kt:42-69`) — a count line,
  incapable of printing the fog or an equivalent.
- `ParkDrawer.kt` (Park surfaces): **no hits** — the board renders Watch
  Store snapshot rows/FIX keys; no "can't check" equivalent.
- `TactileDeckScreen.kt`: the only consumer — slot at `:1159-1194`, decision
  at `:1175`. Its preview/default warrant (`:133-141`, UNPROVEN/unbound/UNSET)
  is a default parameter for previews/harnesses; fed through the table it
  would print "BLOCKING IS OFF" (unbound row), not the fog.
- `MonolithCustomComponents.kt:534` holds only the `RefusalSentenceSlot`
  composable (renderer, no logic).

One adjacent non-fog print worth naming so nobody conflates it: the Deck's
strip prints `UNKNOWN` for an UNPROVEN print-state
(`TactileDeckScreen.kt:378-383`, `blockCellFor`) — a different word on a
different surface, not a resting-fog path.

## 6. Cold-open timeline — the race window, step by step

1. **Process start.** `lastRootMono = 0`, `residencyStartMono = 0`, service
   `instance = null` — all in-memory, nothing persisted (§1). System begins
   binding the a11y service on its own schedule; nothing in the app orders
   it against Activity start.
2. **`MainActivity.onCreate`.** `WatchStore.bind()` (LAST PROVED only),
   repository/ViewModel created, `setContent`. Destination already DECK
   (`MonolithViewModel.kt:299`).
3. **Deck composition → residency starts.** First composition runs
   `LaunchedEffect(currentDestination)` → `noteResidencyStart()`:
   `residencyStartMono = T0 = elapsedRealtime()` (`MainActivity.kt:201-204`,
   `WatchStore.kt:249-252`). **Every stamp before T0 is voided** by the
   `>= T0` test — including the window-state event the launch itself
   generated, if it was delivered before this effect ran.
4. **Warrant reads begin immediately.** `noteResidencyStart`'s `poke()`
   publishes a snapshot; the ViewModel's snapshot collector
   (`MonolithViewModel.kt:923-927`) calls `refreshDeckWarrant()` →
   `standingWarrant()` on every emission. The 1-second ticker
   (`startTickerLoop`, `MonolithViewModel.kt:1078 ff.`) also re-reads a stale
   warrant, keeping any printed sentence fresh rather than letting the
   timed overload's 45 s staleness gate silence it.
   - If the service is **not yet bound** at a read: `state = OFF` →
     "BLOCKING IS OFF — TAP TO FIX". **Not the fog.** (Chain correction:
     "not bound yet" alone never prints fog.)
   - The instant the service binds, `onServiceConnected` →
     `onAccessibilityBound(true)` → publish → collector re-reads: now
     `bound = true`, `lastRootMono` still 0 or pre-T0 → `stamped = false`
     → (usage ALLOWED) `state = UNPROVEN` → **fog prints**.
   - If the service was bound *before* T0 (warm process / fast bind), the
     same fog prints from the first read: its last event predates T0.
5. **First qualifying a11y event.** Only `onAccessibilityEvent` with
   `rootInActiveWindow != null` stamps (§2). When it lands, `poke()` →
   collector → re-read → LIVE (if usage ALLOWED) → fog clears by itself.
6. **The window.** The race window is **T0 → first post-T0 a11y event with a
   non-null root**. Nothing in the base code bounds it: the subscribed event
   types are window state/content changes, and an idle Deck sitting in the
   foreground generates neither on any schedule the code controls (its own
   5-second `nowMonoTick`, `TactileDeckScreen.kt:369-375`, changes no text at
   rest). If the launch's own window events all landed pre-T0 and the user
   simply looks at the Deck, the window can stay open **forever** — and
   RETRY (§4) cannot close it. That is the whole bug.

**Duration caveat (UNCERTAIN, honestly):** whether a given OEM dispatches a
`typeWindowContentChanged` for Monolith's own window shortly after T0 is
device behaviour not decidable from this tree. The code guarantees only
that no stamp is *guaranteed*. Sarthak's report — fog on every cold reopen,
surviving taps — matches the no-event branch exactly.

## 7. What changed at HEAD while I traced (cure commits, for the record)

- `1b641aa` adds a **second positive stamper**: 
  `MonolithAccessibilityService.probePipelineNow()` (live root → stamp +
  poke; null/throw → no stamp), requested from `noteResidencyStart`
  (immediate + one re-probe at +1500 ms if the same residency is still
  unstamped) and from `onServiceConnected` when a residency is active.
  My §2 verdict "event path is the only stamper" is true at `4378dae` and
  **expires at `1b641aa`** — do not quote it against HEAD.
- `55bcae4` rewrites the refusal table: usage DENIED/UNSET rows now sit
  **above** the unstamped row, the fog sentence is deleted, and an unstamped
  UNPROVEN warrant prints silence inside `RESIDENCY_GRACE_MS = 4_000`, else
  "BLOCKING IS OFF". My §4 verdicts describe the base table only.
- Neither commit touches §1's caller structure, §3's derivation table
  (state logic unchanged; `Warrant` gains a `residencyStartMono` field), or
  §5's single-site finding at base.

## Verdict summary

| # | Link | Verdict |
|---|---|---|
| 1 | Sole caller, Deck-entry LaunchedEffect, residency reset per entry | **CONFIRMED** |
| 2 | Event path sole positive stamper at base; bind ≠ stamp; resets on unbind/destroy | **CONFIRMED** (expires at HEAD `1b641aa`) |
| 3 | UNPROVEN = bound ∧ ¬stamped | **CONFIRMED with correction** — full form in §3 (stamped∧UNSET room; DENIED pre-empts to OFF) |
| 4 | Fog preconditions | **CONFIRMED**; fog-over-DENIED ordering **REFUTED for production warrants**, true only textually |
| 5 | No other resting-fog path (Launcher / GuardLine / Park / Deck) | **CONFIRMED** — single site |
| 6 | Race window T0 → first post-T0 event, unbounded, RETRY-incurable | **CONFIRMED** in mechanism; exact duration per device **UNCERTAIN** |

## Efficiency account (DIAG-1)

- **Artefacts:** this file (`studio/cells/issue-7-coldstart/diag-1.md`) +
  signed summary comment on issue #7. No app code touched (verified
  read-only: all tracing via `git show`/`git grep` on `4378dae` + working-tree
  reads; the only writes are this file and its commit).
- **Idling:** none self-inflicted; wall-clock was consumed by two GitHub API
  timeouts (~90 s total) and by re-anchoring after HEAD moved three times
  mid-investigation — that re-anchoring produced §7, which the cell needs,
  so it was not waste, but it was unplanned.
- **Token waste admitted:** one oversized `cat` of WatchStore +
  AccessibilityService (~90 KB output, truncated) before I switched to
  targeted greps; and two reads of `MonolithComponentLogic.kt` /
  `MainActivity.kt` that returned different content minutes apart (cure
  landing) and had to be redone against the base commit. Method corrected
  mid-run: all subsequent evidence taken from pinned-commit reads.

— DIAG-1
