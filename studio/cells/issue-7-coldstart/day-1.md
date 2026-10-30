# DAY-1 — The cured cold open, lived through Sarthak's actual day (issue #7, cold-start cure cell)

**Seat:** DAY-1, DAY SIMULATOR, BINDUNG's cold-start cure cell. Report to BINDUNG only.
**Branch read:** `agent/issue-7-coldstart` @ `b13ca1a` (cure = `1b641aa` probe + `55bcae4` cured
sentence table + `b13ca1a` BUILD-3 tests), off main `4378dae`.
**Method:** READ-ONLY on code. The day was walked moment by moment against the landed
mechanism: probe at residency start, re-probe at +1500 ms, re-probe on rebind, 4 s silent
grace (`WatchStore.RESIDENCY_GRACE_MS`), past-grace OFF. Every verdict cites the code path
that produces it. Nothing compiled, nothing run — execution proof is VER-1's seat; these
are code-read verdicts.
**His day (SARTHAK-WORDS.md + brief):** wake 03:30, meditation, fasted study ~05:00–08:00
(arm on waking head), gym 08:00, sadhana, study blocks through the day, sleep 20:00. The app
is a BLOCKER for study blocks only; outside blocks it is quiet. His ARM standard: arm in
≤10 s. His wound this cure exists to close: "CAN'T CHECK BLOCKING — TAP TO RETRY" on EVERY
app reopen (Oct 29, 22:06 + 22:12 IST).

**Score line: 4 PASS · 1 split (arm PASS / first-frame FAIL) · 1 conditional FAIL.**
Charges D-1 and D-2 below. Both are the same shape: the cure's grace covers the race it
named (bound-but-unstamped) and misses the race standing next to it.

---

## Moment 1 — 04:55, fasted arm. Phone idle all night, cold process start.

**What he does:** opens Monolith half-awake and slides to arm the fasted block.
**What the app shows:**

- **t = 0:** Deck composes. `deckWarrant` is still null in the fresh process, so the
  sentence slot is empty and the strip's BLOCK cell prints **UNKNOWN**
  (`refusalSentenceFor(null) = null`, `MonolithComponentLogic.kt:243` in table order;
  `blockCellFor` else-branch, `TactileDeckScreen.kt:378–382`). `noteResidencyStart()` fires
  from the Deck-entry effect (`MainActivity.kt:201–208`), and with it the probe request —
  `MonolithAccessibilityService.instance?.requestPipelineProbe()` (`WatchStore.kt:280`).
  Note the `?.`: at a cold process start the accessibility service binds into the process
  *asynchronously*, so at t = 0 the instance can legitimately still be null and the first
  probe is a **no-op by construction**.
- **t = +1 s, two sub-cases:**
  - **(a) Service already bound** (process was alive overnight): the probe stamps
    `lastRootMono` within milliseconds (`probePipelineNow`,
    `MonolithAccessibilityService.kt` cure hunk: live root → stamp + `poke()`), the VM
    collector re-reads `standingWarrant()` on the poke, warrant goes LIVE, BLOCK prints
    **ON**. He never sees anything but a blink of UNKNOWN.
  - **(b) Cold process / OEM-throttled bind:** the first warrant read that lands before
    the bind reads `instance == null` → `deriveWarrant` maps `!a11yBound → OFF`
    (`WatchStore.deriveWarrant` state table) → the sentence table's first row
    (`!warrant.a11yBound → "BLOCKING IS OFF — TAP TO FIX"`,
    `MonolithComponentLogic.kt:246–247`) fires **immediately — the 4 s grace does not
    cover this row.** Grace lives only in the UNPROVEN-unstamped row
    (`MonolithComponentLogic.kt:252–266`). Strip prints BLOCK **OFF** (FAULT) in the same
    frame. If his finger reaches the slide inside this window, `readWarrant()` returns OFF
    → travel clamps to `OFF_STOP` and the refusal sequence fires
    (`SlideGestureMath.travelLimit`, `BlockitCoreViews.kt:1342–1348`; refusal at
    `:2039–2045`). The phone tells a half-awake man his guard is dead and refuses his
    first slide — on a healthy phone whose only sin is binding slowly.
- **t = +5 s:** the bind has landed on any realistic phone; `onServiceConnected`
  re-probes because a residency is active (`MonolithAccessibilityService.kt:344–352` —
  the cure's third net, and the one that actually catches this sub-case), the stamp
  lands, the warrant goes LIVE, the false OFF clears. 

**Does the probe fire before he slides?** In (a), yes. In (b), it *cannot* — the probe
is addressed to an instance that does not exist yet; the rebind probe is the net, and it
fires at bind time, not at residency start.
**Does the slide's warrant read see the stamp in time?** Once a stamp exists, yes,
completely: the slide re-reads synchronously at DOWN, at every gate transition, and at UP
(`BlockitCoreViews.kt:1699`, `:2021–2024`, `endDrag`), `travelLimit(UNPROVEN) = CATCH`
lets the finger travel while unproven, and `commits()` requires a LIVE latch-frame read
(`:1352–1353`) — so a stamp landing mid-slide converts the attempt with no re-gesture.
The in-gesture race is cured. The in-front-of-the-gesture bind race is not.
**Arm in ≤10 s?** Yes in both sub-cases on any bind latency a real phone produces.

**Verdict: arm PASS / first-frame FAIL — Charge D-1.** The cure's own comments name this
exact race ("on a cold open the service may not be bound yet when the Deck appears",
`WatchStore.kt` `noteResidencyStart` doc) and then give the grace to the other row.
FEEL-1 reached the same mechanism independently (Charge F-1, `feel-1.md` §1.4) from the
choreography side; DAY-1 adds the gesture consequence: it is not only a false print, it
is a *refused first slide* at the one moment of the day he can least afford friction.
**Smallest cure, priced:** teach the warrant the third bind state with a read the repo
already owns — `MonolithRepository.isAccessibilityEnabled()` (Settings.Secure
`ENABLED_ACCESSIBILITY_SERVICES`, `MonolithRepository.kt:777`), currently *not* an input
to `standingWarrant()`/`deriveWarrant()`. Enabled-but-unbound inside grace → derive
UNPROVEN (slot silent, slide travels to the catch, no commit) instead of OFF;
enabled-but-unbound past grace → OFF (a bind that never comes is a fact, and moment 4's
instant OFF is preserved for the genuinely not-enabled case, which is what
`ColdStartWarrantTest.unboundReadsBlockingOffAtEveryAge` actually means to protect — that
test must split into not-enabled vs enabled-unbound). ≈25 lines: one Boolean through
`deriveWarrant`, one row condition in the table, the slide needs no change (it already
treats UNPROVEN correctly), test split + 2 cases. No new permissions.

---

## Moment 2 — Midday reopens between blocks. Warm process, Deck re-entry.

**What he does:** between blocks he checks things — Shield, Tasks, back to Deck, several
times a day.
**Mechanism:** residency is per Deck *entry*, not per app open. The
`LaunchedEffect(currentDestination)` in `MainActivity.kt:201–208` fires
`noteResidencyStart()` on **every** arrival at DECK, tab-switch returns included. Each
entry: `residencyStartMono` resets → the previous visit's stamp no longer proves anything
(`stampedThisResidency()` requires `lastRootMono >= residencyStartMono`,
`WatchStore.kt:297–299`) → probe + grant sweep re-fire → a fresh 4 s grace opens.

- **t = 0 / +1 s / +5 s, healthy phone:** the probe is one `rootInActiveWindow` read on
  the service's scope; the stamp lands in milliseconds, typically before the recomposed
  Deck has settled. Cost is at most a one-frame BLOCK **UNKNOWN** flicker on return, and —
  mid-session — the running BLOCK cell's honest degrade ON → UNKNOWN until the re-stamp
  (`TactileDeckScreen.
...[truncated 9102 chars]