# RECORD — Issue #7 Cold-Start Cure Cell (BINDUNG's cell)

Keeper: **KEEP-1, Keeper of Record**. This file is the lane's durable
record. It is updated in stages — opened / builds landed / verdicts /
PR / merge — and committed at each stage (RECORD.md only). The lane is
not done until its PR shows **MERGED** on PR TRACKER #245.

- Repo: `Sarthak-2007/Focus-app` (Monolith)
- Branch: `agent/issue-7-coldstart`, off main `4378dae`
- Issue: #7 — "Main-app pass — Shield (audit fixes)"; this cell owns
  only the cold-start fog cure on that thread (see §2, file ownership)
- **Lane stage at this commit: STAGE 2 — BUILDS LANDING.**
  Record opened; build groups 1 and 2 of 3 landed on the branch
  (commits `1b641aa`, `55bcae4`); test group not yet landed; no PR
  opened yet; #245 not yet updated for this lane.

---

## 1. Seat table — every seat, name by name

| Seat | Type | Job (one line) | Deliverable | State |
|---|---|---|---|---|
| BINDUNG | Lead | Owns the cure: root-cause verification, cure design, file ownership, coordination on #7 | Coordination comment `6515653955` on issue #7 (2026-10-30T05:22:32Z) | Landed (coordination posted) |
| DIAG-1 | Diagnostician | Verify the stamp race in the warrant / residency machinery | `studio/cells/issue-7-coldstart/diag-1.md` | Seated |
| DIAG-2 | Diagnostician | Verify the stamp race on the accessibility-event path | `studio/cells/issue-7-coldstart/diag-2.md` | Seated |
| BUILD-1 | Builder | Active self-probe: `WatchStore.kt` residency machinery + `MonolithAccessibilityService.kt` probe | `app/src/main/java/com/example/service/WatchStore.kt`, `app/src/main/java/com/example/service/MonolithAccessibilityService.kt` — commit `1b641aa` | Landed |
| BUILD-2 | Builder | Cured refusal-sentence table: `MonolithComponentLogic.kt` only | `app/src/main/java/com/example/ui/components/MonolithComponentLogic.kt` — commit `55bcae4` | Landed |
| BUILD-3 | Builder | Fog-row test cures + new cold-start warrant test | Fog rows in `app/src/test/java/com/example/SliceDGestureAndStripTest.kt` and `app/src/test/java/com/example/TruthCar3InterceptTest.kt`; new `app/src/test/java/com/example/ColdStartWarrantTest.kt` | Seated — not yet landed (no ColdStartWarrantTest on branch at this commit) |
| CRIT-1 | Critic (brutal) | Unrestricted-scope brutal critique of the cure, evidence-bound charges | `studio/cells/issue-7-coldstart/crit-1.md` | Seated |
| CRIT-2 | Critic | Critique of the cure, evidence-bound charges | `studio/cells/issue-7-coldstart/crit-2.md` | Seated |
| TECH-1 | Technical execution | Mechanism audit: probe timing, grace arithmetic, stale-print seam | `studio/cells/issue-7-coldstart/tech-1.md` | Seated |
| MAX-1 | Maximizer | Push the cure past the brief where the mechanism allows it | `studio/cells/issue-7-coldstart/max-1.md` | Seated |
| FEEL-1 | Feel | The grace silence and the OFF landing as felt states (no text while the machine works) | `studio/cells/issue-7-coldstart/feel-1.md` | Seated |
| DAY-1 | Day simulator | The cure lived through a real day: cold opens, quiet Deck, revoked grant mid-day | `studio/cells/issue-7-coldstart/day-1.md` | Seated |
| FRESH-1 | Fresh-eyes | Read the cured table cold; catch what familiarity hides | `studio/cells/issue-7-coldstart/fresh-1.md` | Seated |
| KEEP-1 | Keeper of Record | This record — seats, decisions, behaviour table, PR/#245 bookkeeping, open charges | `studio/cells/issue-7-coldstart/RECORD.md` (this file) | Landed (opened; updates at every stage) |
| VER-1 | Verifier | Independent verification of builds + tests when seated | `studio/cells/issue-7-coldstart/ver-1.md` | Not yet seated |
| FIN-1 | Finisher | Final finish pass + PR shepherding when seated | `studio/cells/issue-7-coldstart/fin-1.md` | Not yet seated |

Neighbouring lanes, explicitly NOT this cell's seats (named so nothing
is lost): **ZEUGE** — owns the Shield sentence
(`computeShieldBlockingStatus` and the Shield surface) on issue #7;
consulted, not collided with (see §2). **VOGT's lane** on branch
`agent/issue-7` — untouched by this cure.

---

## 2. Decision log

### Root cause — verified (BINDUNG, against main `4378dae`)

A **stamp race between residency start and an event-only stamp**:

- Deck entry notes a residency start —
  `WatchStore.noteResidencyStart` sets `residencyStartMono`.
- The warrant reads LIVE only when
  `MonolithAccessibilityService.lastRootMono >= residencyStartMono`.
- `lastRootMono` was stamped **only** on the accessibility-event
  path, when a root window is acquired.
- A cold open's event burst races the residency start; a quiet Deck
  then produces no further events, the stamp never lands, the warrant
  rests at `UNPROVEN` / unstamped, and `refusalSentenceFor`
  (`MonolithComponentLogic.kt`) printed
  `CAN'T CHECK BLOCKING — TAP TO RETRY` as a **resting state** —
  on every cold reopen, on a healthy phone.
- It is a stamp race, not a settings problem. Sarthak reported it
  twice (2026-10-29 22:06 + 22:12 IST): "CAN'T CHECK BLOCKING — TAP
  TO RETRY" on EVERY app reopen — the warrant's
  `a11yStampedThisResidency` never lands on a cold start.

### Cure design — as coordinated, as built

1. **Active self-probe.** The bound accessibility service proves its
   own pipeline on demand: `probePipelineNow()` performs the event
   path's own proof (live root → stamp `lastRootMono` + poke;
   null/throw → no stamp, returns false). `WatchStore.noteResidencyStart()`
   probes immediately at residency start and re-probes **once at
   +1500 ms**, only if the same residency is still unstamped;
   `onServiceConnected` re-probes when a residency is already active
   (rebind mid-residency). — commit `1b641aa` (BUILD-1).
2. **4 s grace.** `WatchStore.RESIDENCY_GRACE_MS = 4_000`.
   `Warrant` carries `residencyStartMono` (stamped by
   `deriveWarrant`; no change to its state table). While an
   UNPROVEN + unstamped warrant (usage ALLOWED) is inside the grace,
   the sentence slot prints **nothing** — the slot's reserved
   emptiness, the house pattern for machine-working states.
3. **Past grace → OFF.** Past the grace — or with no residency
   carried at all (`residencyStartMono == 0`: age unknown, never
   fresh) — a bound-but-never-proving pipeline rests at
   `BLOCKING IS OFF — TAP TO FIX` (`FIX_ACCESSIBILITY`; the tap
   rebinds the service, the one act that can change the fact).
4. **Fog deletion.** `CAN'T CHECK BLOCKING — TAP TO RETRY` is
   deleted from the refusal table, not reworded; per commit
   `55bcae4` the string `CAN'T CHECK BLOCKING` exists nowhere in
   `app/src`. The single-arg `refusalSentenceFor(warrant)` overload
   now delegates to the timed overload with
   `SystemClock.elapsedRealtime()`, so no caller path can resurrect
   the fog, and the stale-print guard
   (`WatchStore.warrantStaleForPrint`) rides the same cured table.
5. **Row re-ranking (part of the cure).** Usage `DENIED` and `UNSET`
   rows now stand **above** the unstamped row: the old table fired
   its fog row first for an unstamped `DENIED` warrant, hiding a
   fixable lack behind a retry that could never fix it. Not-bound
   still outranks everything: `BLOCKING IS OFF — TAP TO FIX`.

### Coordination

- BINDUNG's coordination comment on issue #7: **comment `6515653955`**,
  posted 2026-10-30T05:22:32Z —
  https://github.com/Sarthak-2007/Focus-app/issues/7#issuecomment-6515653955
  It states the verified root cause, the file ownership (this cell:
  `WatchStore.kt`, `MonolithAccessibilityService.kt`,
  `MonolithComponentLogic.kt` refusal table only, the fog rows in
  `SliceDGestureAndStripTest` / `TruthCar3InterceptTest`, plus new
  `ColdStartWarrantTest`), the cure shape, and that PR tracker is #245.
- **ZEUGE consultation state:** ZEUGE is named in comment `6515653955`
  as keeper of the Shield sentence (`computeShieldBlockingStatus` +
  Shield surface) and is **consulted there, not collided with** —
  this cure does not touch the Shield sentence or surface. As of this
  commit, **no ZEUGE reply is posted on issue #7** after `6515653955`
  (the only later comment is the WELLE-5 design-checklist routing,
  `6515744252`). State: **consultation open, awaiting ZEUGE**; the
  ownership boundary is respected in both landed build commits
  (neither touches `ShieldBlockingStatus` / Shield surface files).
- Open consultation stands for every seat, any type, any route
  (Sarthak's order, Oct 30 09:45–10:06 IST).

---

## 3. Before / after behaviour table

The sentence slot on the Deck (`refusalSentenceFor`). "Before" = main
`4378dae`. "After" = this branch at `55bcae4`.

| Case | Before | After |
|---|---|---|
| **Cold open, healthy phone** (service bound, usage ALLOWED, quiet Deck — stamp races residency start) | `CAN'T CHECK BLOCKING — TAP TO RETRY` printed as a resting state, indefinitely, until some a11y event happened to stamp — a retry prompt for a check the hand cannot perform | Slot **silent** inside the 4 s grace (reserved emptiness — the machine is working); the self-probe at residency start (+1500 ms re-probe) stamps the pipeline, the warrant goes LIVE, and the slot stays empty because healthy prints nothing. No fog, ever |
| **Dead pipeline** (service bound, usage ALLOWED, pipeline never proves — bound but blind) | `CAN'T CHECK BLOCKING — TAP TO RETRY` forever — the fog claimed the state was unknowable | Silence inside the grace; **past 4 s** (or no residency carried): `BLOCKING IS OFF — TAP TO FIX` — the true resting state; the tap rebinds the service |
| **Revoked grant** (accessibility not bound) | `BLOCKING IS OFF — TAP TO FIX` | `BLOCKING IS OFF — TAP TO FIX` — unchanged; still the first, outranking row (observed, not inferred) |
| **Unstamped + DENIED** (usage access DENIED, stamp not yet landed) | `CAN'T CHECK BLOCKING — TAP TO RETRY` — the fog row fired before the DENIED row and masked a known, fixable lack behind an unfulfillable retry | `USAGE ACCESS IS OFF — TAP TO FIX` — DENIED/UNSET rows outrank the unstamped state and name the door the hand actually opens (`FIX_USAGE`) |

Fog-string audit at this commit: `CAN'T CHECK BLOCKING` — 0 hits in
`app/src` on this branch (commit `55bcae4`'s claim; KEEP-1 grep at
record opening also returned 0 hits in `app/src`).

---

## 4. PR + #245 bookkeeping

- **PR:** not yet opened. (BUILD-3's test group has not landed; the
  PR opens when the full cure — probe, table, tests — is on the
  branch.)
- **CI verdict:** none yet (no PR, no run for this branch recorded
  here).
- **Merge state:** not merged. Lane stage = **STAGE 2 — BUILDS
  LANDING** (2 of 3 build groups landed: `1b641aa` BUILD-1,
  `55bcae4` BUILD-2; BUILD-3 tests pending).
- **#245 update for this lane:** not yet posted — it is posted in the
  same run the PR opens, with the PR number and CI verdict, and
  again at merge. The lane is **not done** until the PR shows
  **MERGED** on #245; this section will carry the PR number, the CI
  verdict, the merge commit/state, and the exact text of every #245
  update posted for this lane, verbatim, as they happen.

Commits on this branch so far (cold-start cure only):

| Commit | What | Seat |
|---|---|---|
| `1b641aa` | Active self-probe — service probes its own pipeline at residency start (`MonolithAccessibilityService.kt` +46, `WatchStore.kt` +51/−2) | BUILD-1 |
| `55bcae4` | Fog sentence deleted — cured refusal table (`MonolithComponentLogic.kt` +76/−31) | BUILD-2 |
| (this commit) | RECORD.md opened — sections 1–3 full, 4–5 at current state | KEEP-1 |

---

## 5. Open charges carried

Charges are entered here **name by name** as CRIT-1, CRIT-2, TECH-1,
FEEL-1 and DAY-1 file. At this commit (record opening), **none of
those seats has filed yet** — no charge is carried, and none is
invented for them. Every charge that survives the PR will be listed
here with its disposition — *fixed here* / *filed for Ren* /
*rejected with reason* — before the lane is declared done.

| Charge | Filed by | Disposition |
|---|---|---|
| — (none filed yet) | — | — |

---

## Efficiency account — KEEP-1

- Read first: `SARTHAK-WORDS.md` (his words win), BINDUNG's
  coordination comment `6515653955` in full, the branch log and both
  landed build commits (`1b641aa`, `55bcae4`) name by name, and the
  live diff/grep state of `WatchStore.kt`,
  `MonolithAccessibilityService.kt`, `MonolithComponentLogic.kt`.
- Artefacts: this RECORD.md (opened, sections 1–3 complete from
  primary sources; 4–5 honest at current state — no PR number, CI
  verdict, or charge invented to fill the table).
- Waste admitted: one `gh` call grouping diff + issue + comment hung
  ~90 s and was killed; the comment was re-fetched alone with a
  bounded timeout. GitHub API latency in this session is ~70–90 s
  per call — noted for the next stage update.
- Idling: none — record opened in the same run the builds landed;
  next KEEP-1 update fires when BUILD-3's tests land or the PR opens,
  whichever is first.
