# Issue #7 — Shield (audit fixes) — Finisher Spec: FASSUNG — Semantics & Operability

**Issue:** Sarthak-2007/Focus-app #7 — “Main-app pass — Shield (audit fixes)”
**Role:** FASSUNG, FINISHER 4, in VOGT's issue #7 cell
**Slice:** TalkBack semantics, state announcements, focus order, and touch-target
integrity for the new/changed Shield surfaces only — the R-5 live-status sentence,
routine rows wearing ARMED / WON'T FIRE / RUNNING NOW, and the R-1 grouped catalogue
headers — plus every surface those fixes touch (cap selector, sleds, tally, filters,
websites editor).
**Status:** DECLARED — spec only. No file under `app/src/` edited by me, nothing
compiled, no tests run. Exact patches for MONTEUR (sole code editor) are in §8.
**Sources read:** `studio/plan/AUDIT-shield.md` in full (R-1…R-13, R-11 is my charter);
`designer-form.md` (FORM) and `innovator-funke.md` (FUNKE) in this cell;
`ShieldMatrixScreen.kt` (working tree, re-read after MONTEUR's 22:01 edit landed —
see §0.2), `ShieldBlockingStatus.kt`, `MonolithViewModel.kt` (shield-status diff),
`AppRules.kt` (`groupedForCatalogue`), `ShieldCapSelector.kt`,
`BlockitSuitcaseAndSwitchesViews.kt` (`AppRowSledToggleView`, `SledSwitchView`),
`MonolithCustomComponents.kt` (`FullBleedSectionHeader`, `BlockitActionChip`),
`MonolithRepository.kt` (peek constants), `MonolithModels.kt` (`RoutineSchedule`).

---

## 0. Ground truth this spec is built on

### 0.1 The R-11 charge, verified in this tree, not quoted

`grep` for `contentDescription`, `semantics`, `stateDescription`, `liveRegion`,
`customActions`, `heading()` across `ShieldMatrixScreen.kt`,
`ShieldBlockingStatus.kt`, `ShieldCapSelector.kt`, and both sled views returns
**zero hits**. The audit's sentence stands exactly as filed: to TalkBack, the
Shield cannot be operated. Every control below is, today, either an anonymous
node, a text fragment, or — for the two Canvas sleds — not a node at all.

### 0.2 The tree is mid-merge; this spec targets the landed + ruled state

MONTEUR is editing concurrently (the screen file changed under my first read).
What is **landed** in the working tree and therefore specced as-is:

- Status sentence as LazyColumn item 0, a plain `Text` (`testTag
  shield_blocking_status`), text = `ShieldBlockingStatus.headline`, derivation in
  `ShieldBlockingStatus.kt` with four proofs: `LIVE`, `NO_SESSION`, `UNPROVEN`,
  `STALE`. The line is **not tappable** and there is **no CAN'T CHECK state**.
- `routineCapWord(enabled, daysMask)` → `ON` / `WON'T FIRE` / `OFF`, cap widened
  via `widthIn(min = 48.dp)`; `routineRunningNowSuffix(routine, nowMillis)` →
  `" · RUNNING NOW"` from `RoutineSchedule.isActiveNow`, riding the meta line.
- ROUTINES header right side: `liveCount` + `" ARMED"`.
- Tally survives alone (YOUR RULES header + `StandingTallyLine`); slip lines
  retired; catalogue grouped via `groupedForCatalogue()` (PEEK → SHUT → OPEN,
  empty groups omitted) with `CatalogueGroupHeader(tier, count)` printing the
  word left and the count right as two separate `Text`s.

What is **ruled but not yet landed** — my spec marks every dependent item
CONDITIONAL and names the dependency; nothing below silently assumes it:

| Pending item | Ruling | My dependent spec |
|---|---|---|
| Tally words-as-jumps | R-1 / FORM §3.1 | §5.3 (three-node variant) |
| PEEK contract line in the group header | R-6 / FORM §3.1 | §4.2 (spoken contract ships with, or ahead of, print — see note) |
| Glyph legend at catalogue head | R-10 / FORM §3.1 | §4.4 |
| Websites move up + failed-ADD prints & keeps text | R-3 | §7.3 announcement |
| Cap Ghost Preview + source print; LOADOUT header killed | R-4 | §6.3 |
| In-place ratchet refusal; banner loses “VAULT” | R-7 (VM string at `MonolithViewModel.kt:2118` still reads “…DURING ACTIVE VAULT”) | §2.4 |
| Search placeholder → “SEARCH APPS” | R-6 (still “Filter apps…” in tree) | §5.4 (spoken label is independent of the print fix) |
| Slip furniture removal (`SlipLineRow`, `SlipSectionHeader`, `SlipNoneLine`, `SlipLine`/`RestingFlip` still present in file) | R-1 | MONTEUR's cleanup; no semantics attach to dead code — do not wire any |

### 0.3 Divergences VOGT must hold, because semantics inherit them

1. **FORM vs landed derivation.** FORM specced a tappable `CAN'T CHECK BLOCKING —
   TAP TO RETRY` state. The landed derivation has no such state and no tap:
   uncertainty prints as `UNPROVEN` / `STALE`, inert. **Semantics follow the
   landed code: the status node gets no role, no click, no custom action.** If
   VOGT later adopts FORM's retry, add exactly one custom action (§1.4) — never
   invent a door in the description that the finger cannot use.
2. **FUNKE's RUNNING NOW objection stands unresolved.** FUNKE §5b: `isActiveNow`
   proves the *window* contains now, not that a session carries the routine;
   the honest witness is `WindowState.OPEN_RUNNING`. MONTEUR landed the audit's
   named source (`isActiveNow`). My spoken strings (§3) say “running now”
   because the print says RUNNING NOW — semantics must never contradict print —
   but the *truth* of that spoken sentence is exactly as strong as the
   derivation VOGT ships. Flagged, not fixed, in my slice.
3. **Spoken ≠ printed, by design, in one direction only.** Descriptions may
   expand what print abbreviates (punctuation, single letters, glyphs) and may
   add the contract a blind user cannot be assumed to have read on PARK. They
   may never soften, upgrade, or contradict a printed word. `WON'T FIRE` is
   spoken “won't fire”, never “on”.

---

## 1. The live-status sentence (R-5) — semantics

### 1.1 One node, spoken form derived beside the headline — never parsed from it

The visual headline is built for eyes: caps, em-dashes, `HH:MM`. TalkBack reads
`—` inconsistently and `03:30` as a number fragment. MONTEUR therefore adds a
`spoken` field to `ShieldBlockingStatus`, constructed in
`computeShieldBlockingStatus` at the same `when` that builds `headline`
(patch P-1) — string surgery on the headline at the composable is forbidden;
it would break the first time the print grammar changes.

**Exact spoken strings** (label = `shieldSessionLabel(session)` spoken in
sentence case, e.g. `SLEEP SESSION` → “Sleep session”; ends = the same
`hhmm(endsAt)` the headline prints):

| Proof | Visual headline (landed) | `spoken` (exact) |
|---|---|---|
| LIVE, timed | `BLOCKING NOW — SLEEP SESSION · ENDS 03:30` | `Blocking now. Sleep session, ends 03:30.` |
| LIVE, stopwatch | `BLOCKING NOW — SLEEP SESSION` | `Blocking now. Sleep session. No set end.` |
| LIVE + Safe Break | `… · SAFE BREAK — PEEK APPS OPEN` | Base string + ` Safe break, peek apps open.` appended |
| NO_SESSION | `NOT BLOCKING — NO SESSION RUNNING` | `Not blocking. No session running.` |
| UNPROVEN | `NOT BLOCKING — SLEEP SESSION RUNNING, ENFORCEMENT NOT PROVEN` | `Not blocking. Sleep session running, enforcement not proven.` |
| STALE | `NOT BLOCKING — SHIELD PROOF STALE` | `Not blocking. Shield proof stale.` |
| Fallback, `blockingStatus == null`, session active (composable-owned) | `NOT BLOCKING — SESSION RUNNING, ENFORCEMENT NOT PROVEN` | `Not blocking. Session running, enforcement not proven.` |
| Fallback, `blockingStatus == null`, no session | `NOT BLOCKING — NO SESSION RUNNING` | `Not blocking. No session running.` |

### 1.2 Announcement: a polite live region, and why it cannot spam

Attach to the status `Text` (patch P-2):
`Modifier.semantics { contentDescription = <spoken>; liveRegion = LiveRegionMode.Polite }`.

- **Polite, never assertive.** This line changes when sessions start/stop under
  his eyes; it must queue behind whatever he is doing, not interrupt it. The
  one assertive voice on this screen is the ratchet refusal (§2.4).
- **Spam-proof by construction — verified, not assumed.** The VM refreshes the
  status every second, but the spoken string only changes when the *state*
  changes: for a routine session `endsAt` is the frozen `windowEndMillis`; for
  a timed hand session it is `now + spanRemainingSeconds`, and both terms
  advance together, so the derived end instant — and its `HH:MM` — is constant
  for the life of the session (± sub-second jitter; an `HH:MM` flip can occur
  at most once if the jitter straddles a minute boundary, then settles).
  TalkBack announces live-region *changes*; a stable string re-emitted every
  second announces nothing. Acceptance check A-3 (§9) exercises exactly this.
- **No heading, no role, no action.** It is a fact, not a door (§0.3.1). It is
  focus stop #1 in traversal (§5.1) because it is item 0 — that falls out of
  composition order; do not add a focus requester (M6's saveable-scroll law:
  auto-focus would fight return-is-memory).

### 1.3 What the sentence never announces

`RUNNING NOW` appearing on a routine row, tally re-strikes, and sled commits
made from *another* surface (a loadout landing) produce **no** announcement
from this node. One voice per fact: the sentence owns blocking-state
transitions; §2 owns user-initiated commits.

### 1.4 CONDITIONAL — only if FORM's retry state is ever adopted

If a `CAN'T CHECK` state with a real retry callback lands, the node gains
exactly: `onClick` label `"Retry blocking check"` and spoken suffix
`" Double-tap to retry the check."` Until that callback exists, both are
forbidden — a described door that does nothing is the slip's C5 lie in
spoken form.

---

## 2. Announcements — the complete table for the changed surfaces

Compose mechanism: `liveRegion` on the node whose description changed, or
`View.announceForAccessibility` inside the Canvas sleds (they are Views).
Strings are exact; `<App>` is `appName` as printed, `<Name>` the routine name
as printed (not upper-cased — TalkBack pitch-shouts all-caps words letter by
letter on some engines; descriptions always use the name in its natural case).

| # | Event | Channel | Exact announcement |
|---|---|---|---|
| 2.1 | Status proof transition (any §1.1 row → another) | Status node, Polite live region | The new row's `spoken` string |
| 2.2 | Sled commit initiated **in the sled** (tap stop / throw / TalkBack action) | Sled view announce | `<App> set to shut.` / `<App> set to peek.` / `<App> set to open.` |
| 2.3 | Sled commit from TalkBack custom/scroll action | Same as 2.2 — one path, one voice; the action handlers call the same `commitTo` | Same |
| 2.4 | Ratchet refusal (Shield-origin, in-place text when R-7 lands; banner until then) | **Assertive** live region on the refusal text node / banner | `Cannot weaken <App> during this session. <App> stays shut.` (tier word = the tier that remains) |
| 2.5 | Cap selection commits a loadout | Cap track node, Polite (patch P-5) | `Study loadout applied.` / `Work loadout applied.` / `Sleep loadout applied.` |
| 2.6 | Routine turned on, daysMask = 0 | Row cap node state change + Polite announce on the row's live region (P-6) | `<Name> is on, but won't fire. No days set.` |
| 2.7 | Last day chip untoggled (row becomes WON'T FIRE) | Same row live region, Polite | `No days set. <Name> won't fire.` |
| 2.8 | Day chip retoggled (row leaves WON'T FIRE) | Same, Polite | `<Name> armed. <Days spoken>.` e.g. `Sleep armed. Monday.` |
| 2.9 | ±30 time chip activated | Row time-column live region, Polite (P-6) | `<Name>, 22:30 to 07:00.` (full window after the change, both ends — one number alone is half a fact) |
| 2.10 | Routine loadout chip cycles | Same row live region, Polite | `<Name> loadout, sleep.` (new value) |
| 2.11 | Website added / removed (R-3 print state) | Websites header node, Polite (P-8) | `Added <domain>.` / `Removed <domain>. <n> sites blocked.` |
| 2.12 | Website ADD rejected (CONDITIONAL on R-3's printed reason) | Assertive, on the printed reason node | The printed reason, verbatim |
| — | Tally change, RUNNING NOW on/off, group counts changing, external re-tiers | **None. Ever.** | These are readable on arrival; announcing them double-speaks 2.2 and turns the screen into a slot machine — the exact manners M4 banned visually |

Ordering guarantee for 2.2 → 2.4: a refused sled commit announces 2.2's
optimistic string only if the commit was accepted into `commitTo`; the VM's
refusal is synchronous through `onSetAppTier`, and the banner/in-place
assertive announcement (2.4) lands after and **corrects** it. That sequence —
act, then correction at the point of refusal — is R-7's own grammar; the
alternative (silence until proof) would leave TalkBack users with no feedback
at all on the gesture they just made.

---

## 3. Routine rows — ARMED / WON'T FIRE / RUNNING NOW in speech

### 3.1 The row is two focus stops, in this order, and no more

1. **Header node** (the expand row): `Role.Button`, composed description from a
   pure builder `routineRowDescription(routine, nowMillis)` placed beside
   `routineCapWord` in `ShieldBlockingStatus.kt` (patch P-6) so the spoken row
   and the printed row derive from the same inputs and are unit-testable
   together. `stateDescription` = `"Expanded"` / `"Collapsed"`.
2. **Cap node** (the ON / WON'T FIRE / OFF cap): a **switch** —
   `toggleable(role = Role.Switch, value = routine.enabled)` on the 48dp hit
   box (§7.2), action-labelled by verb, state described by truth (§3.3).
   It must be a *separate* node from the header: today the cap is a clickable
   nested inside the clickable row, and without explicit semantics TalkBack
   presents two overlapping activatable areas with one word between them.

Expanded controls follow in visual order (§5.2).

### 3.2 Header description — exact grammar

```
<Name>. <start> to <end>. <days spoken>. <Loadout> loadout. <state spoken>[ Running now.]
```

- Times: `fmtRoutineTime` output verbatim (`22:30`, `07:00`).
- Days spoken (expands the printed abbreviation; single source =
  `routineDaysLabel`'s mask logic): `127 → "every day"` · `31 → "Monday to
  Friday"` · `0 → "no days set"` · otherwise full day names in Mon→Sun order
  joined with `", "` (e.g. `"Monday, Wednesday, Friday"`).
- State spoken — this is where the three ruled words live in speech:
  `routineCapWord` `ON` → `"on, armed"` · `WON'T FIRE` → `"on, but won't
  fire, no days set"` · `OFF` → `"off"`.
- `RUNNING NOW` (suffix non-empty) → append `" Running now."` — present iff
  the print carries it, derived from the same `routineRunningNowSuffix` call,
  never re-derived in the semantics layer.

Worked examples (exact):

- `Sleep. 22:30 to 07:00. Every day. Sleep loadout. On, armed. Running now.`
- `Morning. 09:00 to 13:00. Monday to Friday. Study loadout. On, armed.`
- `Sleep. 22:30 to 07:00. No days set. Sleep loadout. On, but won't fire, no days set.`
- `Morning. 09:00 to 13:00. Monday to Friday. Study loadout. Off.`

### 3.3 Cap node — exact strings

| Row truth | `contentDescription` | `stateDescription` |
|---|---|---|
| enabled, daysMask ≠ 0 | `Turn off <Name> routine` | `On, armed` |
| enabled, daysMask = 0 | `Turn off <Name> routine` | `On, won't fire, no days set` |
| disabled | `Turn on <Name> routine` | `Off` |

The switch's checked state alone would lie for the middle row (checked, and
inert) — the `stateDescription` is what keeps the spoken cap as honest as
the printed one. This is R-6's whole point, one modality over.

### 3.4 The ROUTINES header — ARMED is spoken where it is printed

`FullBleedSectionHeader` currently emits title and right-side as separate
text nodes: TalkBack reads `ROUTINES`, then later an orphan `2 ARMED`.
Patch P-9 merges each section header into one heading node. Exact merged
descriptions for Shield's headers:

- `Routines, 2 armed.` (number from the same `liveCount` call)
- `Loadout.` · `Your rules.` · `All apps, showing 61.` · `Blocked websites, 6 sites.`

All carry `semantics { heading() }` so TalkBack's heading navigation works —
on a ~74-row catalogue screen, headings are the map.

### 3.5 Expanded editor controls — exact semantics

| Control | Semantics | Exact description |
|---|---|---|
| `−30` / `+30` (start) | Button | `Decrease start by 30 minutes` / `Increase start by 30 minutes` |
| `−30` / `+30` (end) | Button | `Decrease end by 30 minutes` / `Increase end by 30 minutes` |
| Loadout chip | Button | `Change loadout, currently sleep` (current value spoken; activation = cycle, result announced per 2.10) |
| Day chips M T W T F S S | `toggleable(role = Role.Checkbox)`, value = mask bit | **Full day names** — `Monday` … `Sunday`; `stateDescription` = `Selected` / `Not selected` |

The day chips are the sharpest spoken defect in the editor: as printed
single letters they contain two indistinguishable `T`s and two `S`s. Print
may keep the letter (Panzer's grammar); speech may not. Full names in the
description, letter on the cap — same fact, each modality in its own grammar.

(The ±30 chips themselves are DELEGATED territory — Voss's batch-10 wheels
replace them per R-8 / FORM §4. This spec changes none of their behaviour,
only their labels; when the wheels land, each wheel inherits: one adjustable
node, description `<Name> start time` / `<Name> end time`, state = current
time spoken, scroll actions stepping one minute — filed here so the wheel
merge cannot re-open R-11 on arrival.)

---

## 4. The grouped catalogue (R-1) — headers and sleds

### 4.1 One heading node per group — never word-then-count fragments

`CatalogueGroupHeader` merges into a single semantics node with `heading()`
(patch P-7). The two printed `Text`s stay visually separate (the finish pass
owns their placement); the semantics merge via `mergeDescendants = true` on
the Row **plus** an explicit `contentDescription`, because the merged
children would read as `PEEK` … `4` with no grammar between them.

### 4.2 Exact group-header descriptions

| Group | `contentDescription` (exact) |
|---|---|
| PEEK | `Peek, <n> apps. Peek opens an app for 2 minutes, then shut again. 20 minutes between peeks.` |
| SHUT | `Shut, <n> apps.` |
| OPEN | `Open, <n> apps.` |

- Singular: `1 app.` — the builder handles it; a spoken “1 apps” on a
  gallery-standard screen is a tell.
- The PEEK contract numbers are **derived from the constants**
  (`MonolithRepository.PEEK_DURATION_SECONDS` = 120,
  `PEEK_COOLDOWN_MILLIS` = 20 min), computed in the same expression that —
  per R-6 / FORM §3.1 — prints the contract in this header. CONDITIONAL
  note: if MONTEUR lands the semantics before the printed contract line,
  the spoken contract still ships. It states a true, constant-derived fact,
  it contradicts nothing printed, and a blind user has no PARK visit in his
  past that sighted users are assumed to have. What must never happen is
  the reverse drift: print and speech deriving the numbers separately.
  One builder, `peekContractSpoken()` / printed sibling, same constants.
- Empty groups emit no header (landed behaviour in `groupedForCatalogue`)
  and therefore no spoken header; the tally's zero is the absence's one
  home, in both modalities.

### 4.3 The sled (`AppRowSledToggleView`) — the heart of R-11

A Canvas `View` with a custom touch handler is, to TalkBack today: not
focusable, not clickable-flagged, undescribed — swipe navigation skips it or
lands on a mute node, and its drag gesture has no non-touch equivalent.
This is the single patch that makes the Shield operable (P-3, in
`BlockitSuitcaseAndSwitchesViews.kt`, `AppRowSledToggleView` — the view
self-describes, so any host surface inherits it):

**Node identity (set in `init`, kept in sync by the existing setters):**

- `isFocusable = true`, `isClickable = true`, `importantForAccessibility =
  IMPORTANT_FOR_ACCESSIBILITY_YES`
- `contentDescription` = `"<AppName>"` (name in natural case, from the
  `appName` setter)
- `stateDescription` (API 30+; pre-30 it folds into the description as
  `"<AppName>, <tier spoken>"`) from the `currentTier` setter:
  `LOCKED → "Shut"` · `PEEK_2M → "Peek, 2 minutes, then shut again"` ·
  `OPEN → "Open"`

**Operability — two grammars, both through the existing `commitTo`:**

1. **Adjustable (seekbar pattern).** Node info carries
   `AccessibilityNodeInfo.RangeInfo(RANGE_TYPE_INT, 0f, 2f, currentIndex)`
   and `ACTION_SCROLL_FORWARD` / `ACTION_SCROLL_BACKWARD`. TalkBack's
   swipe-up/down (and Switch Access step) then moves **one stop**:
   forward = toward OPEN, backward = toward SHUT, from the *committed*
   tier — never from a mid-animation rocker position.
2. **Direct set (custom actions).** Three actions on the node, labelled
   exactly: `Set to shut`, `Set to peek`, `Set to open`. The action for the
   *current* tier is omitted (an action that does nothing is a lying door).

**Deliberately rejected: virtual view hierarchy per stop.** Splitting the
Canvas into three virtual nodes (ExploreByTouchHelper) would triple the
traversal length of a ~74-row catalogue to reach the same three outcomes
the custom actions already give in one stop per row. One focus stop per
app is the right economy for this screen; the stops are reachable, the
row stays one decision.

**Keyboard / D-pad:** `onKeyDown` — `DPAD_RIGHT` = one stop toward OPEN,
`DPAD_LEFT` = one stop toward SHUT, handled = true; other keys pass
through. (Switch Access and D-pad users get the same one-stop grammar as
TalkBack's scroll actions — all four routes call `commitTo`.)

**Feedback:** a successful user-initiated commit announces per §2.2 from
inside `commitTo`'s existing `targetTier != currentTier` branch. External
re-tiers (a loadout landing, the `update {}` path) update the description
**silently** — no announcement; if the node currently holds accessibility
focus, TalkBack re-reads its changed state description on its own.

**Ratchet interaction:** a TalkBack-driven weakening during a session goes
through the identical commit path and is refused identically; the refusal
is announced per §2.4 by the Compose-side refusal node. The rocker re-seats
(the landed S-3/currentTier behaviour); the node's state description never
leaves the tier that is actually committed — mid-drag and mid-animation
positions are **never** written into semantics. Position is the glanceable
channel for eyes (audit-acquitted); for ears, only the commit is a fact.

### 4.4 CONDITIONAL — glyph legend (R-10 / FORM §3.1)

If the legend line (`■ = SHUT · □ = PEEK · NO MARK = OPEN`) lands at the
catalogue head: mark it `invisibleToUser()`. Every fact it teaches is
already carried, in words, by every sled node's state description and by
the group headers. Spoken, it is pure noise — glyphs read as “black
square, equals, shut…” — in front of a blind user's first app. Print
teaches eyes; speech must not pay for it.

### 4.5 Row order inside groups

Alphabetical, as landed (`groupedForCatalogue` sorts by `appName`).
Traversal order = composition order = print order; no semantics sorting is
applied anywhere on this screen. A blind user's map must be the sighted
user's map.

---

## 5. Focus order — the whole screen, specified

### 5.1 Traversal sequence (TalkBack swipe order = LazyColumn item order)

1. **Status sentence** (§1) — text node, live region.
2. Ratchet banner, when visible (§2.4) — assertive live region; focusable
   as text. (When hidden, `AnimatedVisibility` composes nothing: no ghost
   stop. Verified in the landed code.)
3. `Loadout.` heading.
4–6. Cap stops **Study / Work / S
...[truncated 9533 chars]