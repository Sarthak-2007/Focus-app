# PRINT-TRUTH POLISH — FIX-3 + FIX-5 (Issue #11, Held Door)

Seat: **FEIN, Finisher 4**, Tor's cell · For: Tor (lead) · Slice:
every string the door prints and every value the door books, for
FIX-3 (ledger pack) and FIX-5 (cooldown truth pack) only.
Ground read whole, issue-11 clone at filing time (pre-builder —
every line below is the shipped truth the fixes land on):
`ui/components/AirlockAndDisarmOverlays.kt` (door :548–720, prints
:233–275), `ui/MonolithViewModel.kt` (`AirlockInterceptState`
:153–182, `peekOfferFor` :141, `peekSecondsRemaining` :187,
`triggerAirlockIntercept` :2315, `start2MinutePeek` :2384,
`transmuteImpulseAndReturn` :2428, `splitActiveTaskAndReturn`
:1966, ticker doctrine :180–188/:958), `data/MonolithRepository.kt`
(cooldown :1176–1183, lease :1194–1225, `recordIntercept`
:2104–2125, disarm insert :2347, receipt deflection read :1608),
`data/MonolithModels.kt` (`DistractionTriggerReason` :195–224,
`InterceptLogEntity` :452–460), `MainActivity.kt` (intent
:133–150, wiring :793–799), both services' door call sites.
The Held Door Truth Baseline binds. Nobody compiled.

**Verdict in one breath:** the audit's FIX-3/FIX-5 charges are all
confirmed as written, and the walk found four more in the same
family: a **fourth** `Habit` injection point the audit missed
(the disarm path's system row inherits it silently), a log
**timestamp that stamps the resolution, not the arrival** (every
downstream window read inherits the error), an intercept that is
**booked only if it resolves** (the taper and the memory line
will count resolutions while calling them arrivals), and
`phantomReachesBlocked` incremented on a **taken peek** — a reach
the door honoured, booked as a reach it blocked. Corrections
C-01…C-12 are gallery-standard: exact strings, exact sites, exact
tests. Two need Tor's word (C-11's shape, C-07's precedence);
the rest are mechanical.

---

## §1 — TRUTH TABLE: THE PRINTS

`Where` = file:line in the clone. *Becomes false* = the first
moment the print can diverge from the machinery it names.

| # | Print (exact form) | Where | Source derivation, as built | Verdict | Becomes false when… |
|---|---|---|---|---|---|
| P-01 | `PEEK COOLDOWN — AGAIN IN {n} MIN` | Overlay :690 via `peekCooldownPrint` :233 | State snapshot `peekCooldownRemainingSeconds` (repo **floored**, §3) → overlay-local counter decrementing on `delay(1000)` → print **ceilings** the counter | **FALSE — three stacked errors (FIX-5's whole charge, plus one)** | (i) At birth: floor-then-ceiling understates by a full minute for any true remainder in (k·60, k·60+1)s — §3, case A. (ii) Within ~1 s of expiry it prints `AGAIN IN 0 MIN` and **keeps printing it** — the branch keys on the *state's* `peekWithheld == COOLDOWN`, which never re-derives, while the local counter sits at 0 for the rest of the visit (case C). (iii) Every second after open: the delay loop's period is >1000 ms, so the counter runs slow and *overstates* the remainder by an amount that grows with the visit — a print that disagrees with `peekOfferFor`'s own read of the same row. The comment at :579 ("the print never quotes a number minutes stale") is itself stale: it quotes a number *seconds-to-minutes wrong in both directions*. |
| P-02 | `A PEEK IS ALREADY RUNNING` | Overlay :697 | `state.peekWithheld == ALREADY_ACTIVE`, derived once at trigger (VM :2345) | TRUE at trigger; **STALE thereafter, and usually unreachable** | The pill ends mid-visit (≤120 s) and the line stands for the rest of the visit — nothing re-derives it. Worse, precedence at VM :2343–2345 checks `cooldownLeft > 0` **before** `pillActive`: for the leased package itself the floored/capped cooldown read is >0 for the lease's whole life, so this line loses to P-01 exactly when it is the true one (latent today — every door excludes a leased package — armed the day any path fires mid-lease; C-07). |
| P-03 | `PEEK IS OFF` | Overlay :704 | `PEEK_DURATION_SECONDS <= 0` (VM :2343) | TRUE-BUT-DORMANT | Never prints today (constant is 120). Correct in form; it arms the day the editable store can set 0. No correction — recorded so nobody "fixes" a branch that is load-bearing later. |
| P-04 | Withheld lines print **only at `countdownSecs == 0`** | Overlay :686 | Branch condition `countdownSecs == 0 && !canUse2MinutePeek` | **STALE BY CONSTRUCTION (5 s)** | From frame one until the wall expires: a refusal the machine already knows is withheld from the man it refuses. Audit C-6/FIX-6 SECOND 0 cures the gating; recorded here because the *cooldown value* it finally prints at second 5 is the P-01 counter after 5 s of drift — FIX-6 moves the line, FIX-5 must move its source, or the earlier print is just an earlier lie. |
| P-05 | Hero numeral `0{d}` / `00` (88sp) | Overlay :636–643 | Local `countdownSecs`, literal seed 5 (:562–571), free-running `delay` loop | **MISLEADING + A FORMAT BUG IN WAITING** | It gates only the peek prints (P-04) but stands as the face's hero, implying RETURN is gated — RETURN never is (acquitted). Arithmetic: the loop's period exceeds 1000 ms, so the wall runs *long*, never short — the safe direction, still unanchored (FIX-5's F-1 twin: anchor to a stored end-mono). Format: `String.format("0%d", secs)` yields `010` the day the FIX-8 taper sets a wall of 10 — a direct violation of the tree's own two-digit law (DRW-08, `twoDigitCount`, Models :529). The numeral dies as hero under FIX-6; if any interim build keeps it, C-10 applies. |
| P-06 | Chip badge `{d/60}m` — `peekBadgeText(120)` = `2m` | Overlay :665 via :257 | `durationSeconds / 60`, integer division, from `PEEK_DURATION_SECONDS` | TRUE TODAY, **FALSE BY TRUNCATION** at the first non-multiple-of-60 | 90 s prints `1m` — a 30 s understatement on the offer itself (FIX-8 charges this; the correction is C-08, designer's §5.3 rule). The existing test (`TruthCar3InterceptTest:227`) pins only 300→`5m` and 120→`2m` — both survive C-08; the 90 s case is unpinned. |
| P-07 | Price `TAKING THE PEEK STARTS A {n} MINUTES COOLDOWN ON THIS APP` | Overlay :672 via `peekPriceText` :265 | `PEEK_COOLDOWN_MILLIS / 60_000`, floored; plural keyed on the floored value | **FALSE IN TIMING TODAY; FALSE IN NUMBER AT THE FIRST ODD COOLDOWN** | Timing: the repository anchors the cooldown at the lease's **end** (`getPeekCooldownRemainingSeconds` reads `max(lastPeekEnd, activePeekUntil) + COOLDOWN`), so from the tap the true exclusion is lease + cooldown — the sentence claims the clock starts at taking (FIX-8 re-times the words; C-09). Number: floor division makes a 90 s cooldown print `1 MINUTE` (and trips the singular branch on a value that is not one minute). Also gated with the chip at countdown 0 — price after temptation for 5 s (FIX-6's I-2 cure; cross-ref only). |
| P-08 | `SESSION ENDS {HH:mm}` / `SESSION RUNNING · NO SET END` | Overlay :627 via `airlockSessionLine` :244 + `endsAtText` (Components :427) | Snapshot at trigger: `now + spanRemainingSeconds·1000` (VM :2368–2370 — FIX-2's span cure, **present and correct in this clone**) | TRUE AT TRIGGER (post-FIX-2); **STALE AT THREE EVENTS** | (i) The session **ends** while the door stands (disarm completion paths do not clear `_airlockState` — Innovator P-2d): the door prints a past instant in the future tense over a dead session. (ii) A tamper penalty lands mid-visit (+900 s): the true end moves, the print doesn't. (iii) A block flip re-anchors the session row's start (advancePastBreak rewrites `startWallTimeMillis`); the span end is invariant under that rewrite, so this one is *not* a falsifier — recorded to save the builder checking. Gallery bar: the line is a snapshot of a *stable* instant, lawful as a snapshot; events (i)/(ii) are state-clearing bugs, not print bugs — C-11's arrival-row work touches the same state and should clear the airlock on every disarm completion (Innovator 2d, endorsed). |
| P-09 | `ACTIVE INTENTION: {sessionIntention}` | Overlay :649 | Trigger: `active?.intention ?: _intentionText.value` (VM :2354) | **FABRICATED WHEN NO SESSION IS ARMED** | With no active session the state carries the **composer's draft** — whose VM initial value is the literal `"Finish Chapter 4 Problem Set"` (VM :~230) — and the overlay prints it under `ACTIVE INTENTION:` as though declared for a session that does not exist. FIX-3e kills the fallback (C-03): no armed session → blank → no line (the overlay's `isNotBlank` guard at :648 already implements the silence; only the VM's fallback defeats it). With a session armed the print is the row's own intention — true. (The prefix and slot are FIX-6's; the *value* is FIX-3's.) |
| P-10 | Eyebrow `AIRLOCK INTERCEPTION • {BADGE}` | Overlay :610 | Badge carried verbatim from the firing service: tier badge (`Blocked` / `2m Peek`), or literals `Uninstall Guard` / `Shorts Blocked` / `Reels Blocked` / `Site Blocked`; fallbacks `Locked` (VM :157/:2318, MainActivity :137, ShieldService :185) | MOSTLY TRUE; **ONE LITERAL LIES BY DURATION** | `2m Peek` embeds a duration literal in the badge slot: the day the peek length is edited, the eyebrow understates/overstates it while the chip's derived badge (P-06) tells the truth two lines below — two prints of one fact, one derived, one not (designer §4 flags this for Tor; print-truth concurs: footer prints `PEEK`, duration lives only on the chip). `Locked` fallback ×4 sites (designer found the fourth, ShieldService :185) → `Blocked` per FIX-6/C-10. Fallback *name* `Short-Form Feed` (MainActivity :135) is a label invented at the intent seam for a blank name extra — both services always send a name today, so it is latent; it should fall back to the **package name verbatim**, never a coined label (same law as C-03). |
| P-11 | Name slot `{interceptedAppName}` (24sp) | Overlay :619 | Service-composed at the call site (a11y :211/:237/:265/:291, Shield :184) | TRUE FOR APP_TIER / SHIELD / SNIPER; **FICTION ON TWO DOORS** | Guard door prints the literal `System Settings Guard` — a door label in the fact slot (FIX-6, C-8). Site door prints the **matched domain** as the name, with the browser nowhere on the face. Both are FIX-6's charges; recorded here only to complete the walk — no Fein correction beyond endorsing designer §4's table, including its state gap (guard's opened-target fact composed at the call site, package verbatim if unresolvable). |
| P-12 | `RETURN TO FOCUS` | Overlay :655 | Static label on `onReturnToFocus` | **TRUE** | Never becomes false: the tap always runs the return path, live from frame one, ungated in every state. Its *booking* is the lie, not its label — see L-02. |
| P-13 | `EMERGENCY PEEK` | Overlay :665 | Static label | **MISNOMER (FIX-8 kills it)** | Borrows the Disarm face's gravity for a 2-minute lease from the first frame it prints. One string; correction is FIX-8's (`PEEK` + derived badge, designer §5.9). Recorded, not re-specced. |
| P-14 | (Absence) no peek line on never-owed doors | Overlay :686–713 | `peekWithheld == null` by construction (VM :2338–2340) | **TRUE — ACQUITTED** | Baseline law 4 holds in code, not just in prose: LOCKED tier, guard, sniper, site, unknown all carry null and print nothing. Verified in the walk; no correction. |
| P-15 | Pill seconds (door-adjacent, same derivation family) | VM ticker :985–1005 | `peekSecondsRemaining(leaseEnd, nowMono)` — **ceiling** from the stored end (VM :187–191) | **TRUE — THE MODEL CITIZEN** | This is the doctrine FIX-5 extends to the door: derived per tick from the row's `activePeekUntilElapsedRealtime`, null at expiry, never counted. Cited as the pattern C-05 copies verbatim. (The pill *face* is Pastille's audit; the arithmetic is this slice's precedent.) |

---

## §2 — TRUTH TABLE: THE LEDGER WRITES

Every `recordIntercept` caller, plus the two writes that ride
inside it, plus the one intercept-shaped insert that bypasses it.
Booked values are permanent — the Drawer, the receipt's
deflection list (Repo :1608), the attendance evidence (Repo
:965), the taper and the memory line (FIX-8) all read this table
later, in colder rooms.

| # | Booked field / value | Writer chain | What was actually observed | Verdict | False from… |
|---|---|---|---|---|---|
| L-01 | `triggerReason = "Habit"` on RETURN and PEEK bookings | State default `MUSCLE_MEMORY` (VM :159) → set unconditionally at trigger (VM :2355) → `state.selectedReason.shortTag` = `"Habit"` (Models :201) at VM :2413/:2438 | **Nothing.** No picker is composed anywhere: the overlay receives `onSelectReason` (Overlay :552, wired MainActivity :795) and **never invokes it** — grep shows the parameter declared and dropped. `selectedReason` can only ever hold the default. | **FABRICATED (FIX-3a)** | The first booking ever written. Every historical row's `Habit` is a claim about a man's motive that no man made and no UI could have collected. C-01. |
| L-02 | `outcome = "Stayed Focused"` on RETURN | `transmuteImpulseAndReturn` (VM :2437) | A tap on RETURN at the door. Nothing after the tap is observed — he may open the app again in ten seconds. | **BOOKED BEYOND OBSERVATION (FIX-3c)** | The instant it is written: it books a sequel as though it were the act. Ruled replacement: `RETURNED AT DOOR` (C-02). Whether he stayed focused is bookable at session end, by the session's own record — downstream, Lade's crew. |
| L-03 | `outcome = "Used 2m Peek"` on grant | `start2MinutePeek` (VM :2412), booked after `startPeekLease` in the same coroutine | The tap + a grant that re-derived the offer and wrote a lease (acquitted machinery). He has not yet spent a second inside the app. | TRUE ENOUGH — ONE WORD PAST OBSERVED | `Used` claims consumption; the observed act is *taking*. Gallery wording: `Took 2m Peek` (parallel to L-02's discipline — book the act at the door, not its sequel). Small; Tor's call, no machinery depends on the string except humans reading the Drawer. |
| L-04 | `packageName = "com.zhiliaoapp.musically"` (TikTok) when state's package is blank | `transmuteImpulseAndReturn` (VM :2434) | A door whose package was blank — i.e. a door that named **no app at all** | **FABRICATED (FIX-3d)** | Latent today (the intent seam refuses blank packages, MainActivity :132, and both services always send one) — but the fallback sits armed in the booking path: the first blank-package door books a TikTok intercept that never happened, increments TikTok's `interceptCount` (L-08), and enters the receipt's deflection window under TikTok's name. Ruled: blank books blank (C-03). Its sibling fallbacks fall with it: appName `"Blocked App"` (VM :2435 — a generic label paired with a *specific* wrong package: the row contradicts itself), `"2m Peek App"` (VM :2397, unreachable — a blank package fails `peekOfferFor`'s entity check first — dead text, delete anyway), YouTube `"com.google.android.youtube"` in the split path (L-06). |
| L-05 | `sessionIntention` = composer's draft when blank | Trigger fallback (VM :2354) + booking fallbacks `ifBlank { _intentionText.value }` (VM :2411/:2436) | No declared intention — the draft box's current text, initially the seed literal | **FABRICATED (FIX-3e)** | Poisons two downstream reads that match on this string: the receipt's deflections filter `log.sessionIntention == row.intention` (Repo :1610) silently drops real deflections whose door carried a draft, and could adopt a stranger session's. Blank books blank (C-03); with a session armed, trigger already carries the row's intention, so the booking fallbacks are pure risk and no function. |
| L-06 | `outcome = "Split Into 10m Micro-Step"`, `triggerReason = "Stuck"`, package fallback YouTube | `splitActiveTaskAndReturn` (VM :1974–1979) | **Nothing — the function has no caller in the tree** (grep: definition only). It is a fourth booking path the audit's FIX-3 sweep does not name. | **DEAD BUT ARMED** | If ever wired to the coaching flow it implies, it books a *stated* reason (`STUCK_ON_PROBLEM`) from a tap on a remedy, not on a reason — the same fabrication as L-01 in a second coat — and books the **new micro-step's title** as the intercept's `sessionIntention`, guaranteeing the receipt's intention-match (L-05) drops it. FIX-3b's dead-wire sweep must include it: delete, or rewire booking `UNSTATED` + the door's own package/intention verbatim (C-01/C-03 cover it if kept). |
| L-07 | `triggerReason = "Habit"` on **system rows** | `disarmActiveSession` insert (Repo :2347–2355) omits `triggerReason` → entity default `"Habit"` (Models :459) | A session ending (Partial Stamp / Unlatched Early) — not an intercept, and nobody's habit | **FABRICATED — THE AUDIT'S MISSED FOURTH SITE** | FIX-3 names two `Habit` defaults (state + Repo :2109). There are **three defaults in the chain** — VM state, `recordIntercept`'s parameter (Repo :2109), and the **entity column default** (Models :459) — and this insert bypasses the first two and inherits the third. Kill only the named two and `Habit` survives on every system row. The receipt assembly already special-cases these rows out of deflections (`packageName != "com.monolith.system"`, Repo :1611) — proof the table's other readers have been stepping around them. C-01 kills all three layers and makes this insert pass `UNSTATED` explicitly. |
| L-08 | `interceptCount += 1` on the booked package's row | Inside `recordIntercept` (Repo :2111) | A resolution (RETURN or PEEK) for that package — not an arrival | MISCOUNTED BY CONSTRUCTION | (i) Under L-04's fallback it increments a package that was never intercepted. (ii) Arrivals that never resolve (L-10) never increment it, while logs and counts are later compared as though they measure the same thing — `totalInterceptions = maxOf(logs.size, Σ interceptCount)` (VM :526) papers over the divergence by printing whichever ledger is *larger*: a max of two undercounts is still an undercount wearing a reconciliation's clothes. Downstream print, flagged for Lade; the booking discipline that feeds it is C-11. |
| L-09 | `phantomReachesBlocked += 1` on the active session, **every outcome** | Inside `recordIntercept` (Repo :2122–2124) | RETURN: a reach turned back — true. **PEEK: a reach the door honoured with a lease — the opposite of blocked.** | **FALSE FOR PEEK BOOKINGS** | Feeds `compute7DayHistory`'s `interceptionsCount` (Repo :2391) and the shield notification's "urge stopped" family (audit rider C-13). A taken peek is counted as a stopped urge at the source; every downstream surface that quotes the session's count inherits it. The notification print is Pastille's/Lade's; the *increment* is this door's booking and this slice's correction: C-12. |
| L-10 | **No row at all** for a visit that doesn't resolve through RETURN/PEEK | Trigger books nothing (VM :2315–2372); booking lives only in the two resolution paths | The arrival itself — the one event every other row presupposes | **THE LEDGER'S STRUCTURAL HOLE** | A visit ended by process death, by a disarm completion over the standing door (P-08i), or by the FIX-8 honest-exit route (which books nothing, by design — designer §7) leaves **no trace**. FIX-8's taper (`5 + this session's booked intercepts of this package`) and the memory line (`{n}TH TIME THIS SESSION`) both count *booked* intercepts: as specified they will count **resolutions** while printing **arrivals** — the exact species of lie this audit exists to kill, re-entering through the fix itself. C-11 (Tor's decision, my recommendation inside). |
| L-11 | `timestampMillis` = booking instant | Entity default `System.currentTimeMillis()` at insert (Models :454), inside `recordIntercept`, *after* the resolution tap and any lease write | The intercept **arrival** — which happened a wall (5–10 s) plus unbounded deliberation earlier | **STALE AT BIRTH, BY THE VISIT'S WHOLE LENGTH** | Every windowed reader treats this stamp as the intercept instant: receipt deflections (`getInterceptLogsBetween` over the session window + 30 s dedupe, Repo :1608–1625), attendance evidence instants (Repo :965–966), and FIX-8's session-window count (Innovator P-4b). A man who stares at the door for two minutes books his deflection two minutes late — possibly across a break-window exclusion or a session boundary, into a different evidentiary bucket. C-11 fixes it at the root (stamp at arrival); the cheap half-measure (carry `interceptedAtMillis` in state, pass it explicitly) is priced inside C-11. |
| L-12 | `coachingTip` strings ×4 (Models :203/:208/:213/:218) | `DistractionTriggerReason` enum, read by **no composable** | Counsel written for a picker that was never composed (see L-01) | **DEAD TEXT (FIX-3b)** | Not a lie in the ledger — a lie in the source tree: their presence implies a coaching surface exists. They die with the enum's booking role (C-01). (`label` fields die the same death; nothing renders them either.) |

**Also walked, no charge:** `recordIntercept`'s insert and count
run in the caller's coroutine with no transaction — a crash
between `incrementInterceptCount` and `insertInterceptLog` leaves
the two ledgers (L-08) permanently diverged by one. Pre-existing,
one-line cure (`withTransaction`), noted for the builder while
C-01/C-03 have the function open.

---

## §3 — THE FIX-5 ARITHMETIC, WORKED (so the builder cannot re-floor it)

Repository today (Repo :1180–1181):
`remainingMs = (anchorEnd + 1_200_000 − now).coerceAtMost(1_200_000)`;
returns `remainingMs / 1000` — **floor**. Print (Overlay :229):
`(seconds + 59) / 60` — **ceiling**. Composition: floor then
ceiling ≠ ceiling.

- **Case A — the stolen minute.** True remainder 60.4 s
  (`remainingMs = 60_400`). Repo floors → 60 s. Print ceilings
  60 s → **`AGAIN IN 1 MIN`**. Truth, by the ceiling law the
  print itself proclaims: 60.4 s is into its second minute →
  **`AGAIN IN 2 MIN`**. The door re-offers the app a full minute
  early *in its own words* — and `peekOfferFor` agrees with the
  print, not the truth, because it reads the same floored value.
  The error class is every remainder in (k·60 s, k·60 s + 1 s),
  for all k — a 1-second-wide trap at every minute boundary,
  hit whenever the man's return lands in it.
- **Case B — the early offer.** True remainder 0.4 s
  (`remainingMs = 400`). Repo floors → **0**. `cooldownLeft == 0`
  → `peekOfferFor` returns true, `withheld = null`: the door
  **offers a peek up to 999 ms before the cooldown the price
  named has run**. The grant's re-derivation (VM :2392) reads
  the same floored function, so the second gate waves it
  through too. Sub-second, real, and in the one direction this
  face swears it never goes.
- **Case C — `0 MIN`, reachable today, not merely
  representable.** A COOLDOWN visit stands. The local counter
  (Overlay :582–592) ticks the floored seed down to 0 — up to
  ~1 s *before* the true expiry (Case A's floor) — and the
  branch at :686 still composes, because `state.peekWithheld`
  never changes. From that frame until the man leaves, the door
  prints **`PEEK COOLDOWN — AGAIN IN 0 MIN`**: a cooldown
  simultaneously asserted and expired. `peekCooldownPrint(0)`
  = `(0 + 59)/60` = 0 — the function itself has no floor of 1,
  and must not gain one (printing `1 MIN` at expiry would be
  the mirror lie). The cure is construction, not a guard
  (C-05/C-06): at derived 0 there is no cooldown line because
  the derivation that owed it now owes an offer re-check.
- **The cap's quiet role.** `coerceAtMost(PEEK_COOLDOWN_MILLIS)`
  saturates the read at exactly 1200 s whenever the anchor is
  in the future — i.e. during a live lease, when
  `anchor = leaseEnd` and the true time-to-next-peek is
  lease-remainder + 20 min (up to 22 min). Unreachable for the
  leased package today (all doors exclude it); when C-05 moves
  the derivation to an end-mono, the cap must move with it as
  `endsAt = anchorEnd + COOLDOWN` **unclamped** — a clamped end
  instant would resurrect the saturation inside the new form.

---

## §4 — GALLERY-STANDARD CORRECTIONS

Exact unless marked *Tor's call*. File:line = the site as built;
the builder's FIX-3/FIX-5 pass owns the edits.

**C-01 — `UNSTATED` at every layer, or it survives.** (FIX-3a)
Four edits, all four, or the fabrication persists in whichever
layer was skipped:
1. VM :159 — `selectedReason` becomes nullable with **no**
   default reason: `val selectedReason: DistractionTriggerReason? = null`;
   VM :2355 — the trigger assignment is **deleted** (a fresh
   state is born unstated by construction).
2. Booking sites VM :2413/:2438 — `triggerReason = state.selectedReason?.shortTag ?: "UNSTATED"`.
3. Repo :2109 — parameter default `"Habit"` → `"UNSTATED"`.
4. Models :459 — entity default `"Habit"` → `"UNSTATED"`, **and**
   Repo :2351's system insert passes `triggerReason = "UNSTATED"`
   explicitly (L-07: defaults are how this value hid; the system
   row states its reason in the open).
With FIX-3b, `selectDistractionReason` (VM :2375), the overlay's
`onSelectReason` parameter and its MainActivity wire (:795) die;
`DistractionTriggerReason`'s `coachingTip`/`label` strings die
(L-12). If Tor keeps the enum type for a future *stated* picker,
its entries may live — what dies is every path by which one is
booked unstated. **Tests:** booking with untouched state writes
`UNSTATED`; no string `Habit` remains in `app/src/main` (grep
assertion in the FIX-3 test pass); a system-row insert carries
`UNSTATED`.

**C-02 — RETURN books the act.** (FIX-3c) VM :2437 —
`outcome = "RETURNED AT DOOR"` (Scharte's string, ruled in the
audit; verbatim, capitals as ruled). Nothing else in the
function changes. **Test:** `transmuteImpulseAndReturn` writes
exactly one log with that outcome; the string `Stayed Focused`
no longer exists in `app/src/main`.

**C-03 — Blank books blank; drafts book nothing.** (FIX-3d/e)
VM :2434–2436 — `packageName = state.interceptedPackageName`
verbatim (drop `.ifBlank { "com.zhiliaoapp.musically" }`),
`appName = state.interceptedAppName` verbatim (drop
`"Blocked App"`), `sessionIntention = state.sessionIntention`
verbatim (drop the draft fallback). VM :2354 —
`sessionIntention = active?.intention ?: ""`. VM :2397/:2411 —
same surgery in the peek path (drop `"2m Peek App"` and the
draft fallback; :2397's is unreachable, delete regardless —
unreachable fallbacks are how L-04 armed itself). MainActivity
:135 — blank name extra falls back to the **package verbatim**,
not `"Short-Form Feed"`. **Tests:** a state with blank package/
name/intention books a row with three blank fields; a no-session
trigger produces `sessionIntention == ""` and the overlay
composes no intention line.

**C-04 — The repository returns the ceiling form.** (FIX-5a)
Repo :1181 — `if (remainingMs > 0L) ((remainingMs + 999L) / 1000L).toInt() else 0`.
One arithmetic, one place: this is `peekSecondsRemaining`'s own
formula (VM :190) — extract or mirror it exactly, so the door
and the pill can never disagree by a second again. **Tests**
(pure, over the derivation): 60_400 ms → 61 s (was 60);
400 ms → 1 s (was 0); 60_000 ms → 60 s; 0/negative → 0. Then
end-to-end: 61 s through `peekCooldownPrint` → `AGAIN IN 2 MIN`.

**C-05 — The print derives from a stored end-mono.** (FIX-5b)
State loses the snapshot-as-value and gains the anchor:
`peekCooldownEndsAtElapsedRealtime: Long = 0L` in
`AirlockInterceptState`, set at trigger from the repository —
the repo exposes the anchor it already computes
(`anchorEndMono + PEEK_COOLDOWN_MILLIS`, **unclamped**, §3) via
`getPeekCooldownEndsAtElapsedRealtime(app): Long` (0 = no
cooldown). The overlay **deletes** both local counters for this
value (Overlay :579–592): the withheld line derives
`peekSecondsRemaining(endsAt, SystemClock.elapsedRealtime())`
at each recomposition, and any ticker that remains is a redraw
trigger only — the VM's own doctrine (VM :180–188), the pill's
pattern (P-15). (The wall's counter, :562–576, takes the twin
cure under FIX-5/F-1 — designer §5.8 specced it; I endorse and
do not re-spec.) `peekCooldownRemainingSeconds` may survive one
build as the first-frame seed only if the end-mono rides beside
it; gallery standard is the mono alone. **Tests:** with
`endsAt = now + 61_000`, the derived print is `AGAIN IN 2 MIN`
at open and `AGAIN IN 1 MIN` at now + 60_500 — from derivation,
with no counter state to inspect.

**C-06 — `0 MIN` unrepresentable, by construction.** (FIX-5c)
The withheld branch composes **only while the derived value is
non-null**: `peekSecondsRemaining(endsAt, now)?.let { secs ->
peekCooldownPrint(secs) }`. At derived null the visit
re-derives: the VM re-runs the trigger's withheld/offer
computation for the standing state (one small function —
`refreshAirlockOffer()` reading the live row, cooldown and
pill, the same inputs as VM :2328–2347) and the face crossfades
to the chip, the next true withheld reason, or silence
(designer §5.5 owns the motion; the *derivation* is this
correction). No `coerceAtLeast(1)` anywhere near the print —
see Case C. **Test:** a state whose `endsAt` is in the past
composes **no** cooldown line in any frame; the string
`AGAIN IN 0 MIN` is unreachable across the print's whole input
domain because the only caller passes a non-null derivation
whose minimum print is `AGAIN IN 1 MIN` (derived 1 s).

**C-07 — Withheld precedence: the live lease outranks its own
cooldown.** *Tor's call, small.* VM :2341–2347 — when the
standing pill's package **is this package**, `ALREADY_ACTIVE`
is derived before `COOLDOWN` (the cooldown read during a lease
is lease-remainder + cooldown; labelling that `COOLDOWN`
misnames the binding constraint, and its printed number under
C-05's honest end-mono would read up to 22 MIN under a
`COOLDOWN` caption — a true number under a false name). For a
pill on a *different* package, current order stands (this
app's own cooldown is its binding fact). Latent today (§1,
P-02); priced at one condition because FIX-5's re-derivation
(C-06) makes mid-lease re-derivation a live path for the first
time.

**C-08 — Badge renders m + s.** (FIX-8's charge, this slice's
print) Overlay :257 — `peekBadgeText`: whole minutes, then
remaining seconds if non-zero; sub-minute prints seconds
alone. `120 → "2m"`, `90 → "1m30s"`, `60 → "1m"`, `45 → "45s"`,
`125 → "2m5s"` (designer §5.3's rule, adopted verbatim).
**Tests:** the five cases above; existing pins (300→`5m`,
120→`2m`) unchanged.

**C-09 — Price derives honestly, in number and in tense.**
Overlay :265 — minutes by **ceiling** (`(millis + 59_999) /
60_000`), plural keyed on the ceiling value (a 90 s cooldown
prints `2 MINUTES`, never `1 MINUTE`); wording per FIX-8's
ruled re-timing — `WHEN THE PEEK ENDS, A {n} MINUTES COOLDOWN
STARTS ON THIS APP` (designer §5.6) — because the anchor the
repository actually uses is the lease's end (P-07). **Tests:**
60_000 → `…A 1 MINUTE…`; 90_000 → `…A 2 MINUTES…`;
1_200_000 → `…A 20 MINUTES…`.

**C-10 — Fallback-word sweep, completed.** `Locked` →
`Blocked` at **four** sites (VM :157, VM :2318, MainActivity
:137, ShieldService :185 — designer §4's count, verified).
Interim numeral discipline (only if a build ships before
FIX-6 lands): P-05's format goes through `twoDigitCount` —
`010` never prints. **Test:** grep assertion — no `"Locked"`
fallback literal remains in `app/src/main`.

**C-11 — Book the arrival; stamp the arrival.** *Tor's call —
recommendation: adopt in full.* (L-10/L-11) The trigger writes
the intercept row at arrival — package, name, intention and
reason (`UNSTATED`) verbatim, `timestampMillis` = the arrival
instant, outcome `AT DOOR` — and carries its row id in state;
RETURN/PEEK **update that row's outcome** (`RETURNED AT DOOR`
/ L-03's string) instead of inserting a second row. DAO gains
one update query; `recordIntercept` splits into
`bookInterceptArrival(...) : Long` and
`resolveIntercept(id, outcome)`. Every exit that books nothing
today (honest exit, disarm-over-door, process death) now
leaves the truthful residue `AT DOOR` — a visit that was never
resolved reads as exactly that. The taper and memory line then
count what they print: arrivals. **Price: SMALL–MED** (one DAO
query, one state field, both booking paths re-pointed).
*Cheaper half-measure if Tor defers the restructure:* keep
resolution-booking, but (a) carry `interceptedAtWallMillis` in
state from trigger and pass it as the row's explicit timestamp,
and (b) FIX-8's count is renamed in its own derivation to
resolved-intercepts, with the memory line's wording ruled
against that definition before it prints — a count of
resolutions labelled as arrivals is not an acceptable resting
state. **Tests:** arrival with no resolution leaves exactly
one row, outcome `AT DOOR`, timestamp = trigger instant;
RETURN updates that row in place (row count unchanged,
outcome `RETURNED AT DOOR`, timestamp still the arrival).

**C-12 — A honoured peek is not a blocked reach.** (L-09)
Repo :2122–2124 — `phantomReachesBlocked` increments only when
the outcome is a turn-back: under C-02's vocabulary, exactly
`RETURNED AT DOOR` (and C-11's unresolved `AT DOOR` rows do not
increment at insert — a reach is counted when it is turned
back, or, if Tor prefers arrival-counting, the counter is
renamed in the same pass so no reader mistakes it). The peek
path's booking leaves the counter untouched. **Test:**
`start2MinutePeek`'s booking leaves the session's
`phantomReachesBlocked` unchanged; RETURN's increments it by
one. Downstream surfaces (shield notification, 7-day history)
inherit the cure without edits — their charge is C-13's,
already referred by the audit.

---

## §5 — WHAT IS ALREADY TRUE (acquittals inside this slice)

- `peekSecondsRemaining` (VM :187) — ceiling from the stored
  end, null at expiry: the correct derivation, already in the
  tree, already obeyed by the pill. FIX-5 is an *extension* of
  this law to the door, not a new invention.
- `peekCooldownPrint`'s ceiling arithmetic **itself** (acquitted
  by the audit): given an honest seconds value it never
  understates. Its crimes are all upstream (C-04/C-05) or
  compositional (C-06).
- The grant-time re-derivation in `start2MinutePeek` (VM
  :2391–2395): a stale door cannot sell a lease. (It reads the
  floored cooldown today, so C-04 strengthens it for free.)
- Baseline law 4 in code (P-14): never-owed doors print no peek
  line, by null construction.
- FIX-2's span cure is **present** in this clone's trigger
  (VM :2368–2370) — the session line's *value* needs no further
  work in my slice; its staleness events are P-08's, owned by
  state-clearing, not derivation.

## §6 — HANDOFF NOTES FOR TOR

- **Designer dependencies confirmed:** §5.8's anchoring law is
  C-05/C-06 in layout language — the two filings agree on every
  number. Her §3.3 uniform-110dp question interacts with C-06:
  under suppressed resolution (the 34dp alternative) P-01's
  `0 MIN` frame becomes a *standing* print for the whole visit,
  not a transient one — print-truth votes for uniform 110dp
  with live resolution, for that reason alone.
- **Innovator dependencies:** P-4a (`doorPrints` pure function)
  is the natural home for C-04…C-09's derivations — if Tor
  adopts it, these corrections should land *as* that function's
  clauses and its JUnit matrix, not as scattered edits. P-4b's
  count warning (lifetime `interceptCount` ≠ session count)
  compounds with L-08/L-10: three different numbers currently
  answer "how many times" — C-11 makes the log the one source.
- **Downstream (Lade's Drawer crew, per the audit's rider):**
  L-02's historical rows (`Stayed Focused`, `Habit`) are already
  written and permanent. The Drawer must not re-print them as
  fact without a ruling on historical rows — flagged, not
  decided here: my recommendation is the rows stand (they are
  the record of what the *app* claimed) and any Drawer rendering
  of `triggerReason`/`outcome` for pre-FIX-3 rows carries the
  era, silently, by date — Tor/Ren's call, outside this slice.
- **One sweep line for the builder's FIX-3 pass:** after
  C-01…C-03, `grep -rn '"Habit"\|"Stayed Focused"\|MUSCLE_MEMORY\|com.zhiliaoapp.musically'
  app/src/main` must return **zero** hits. That grep is the
  fix's receipt.

— FEIN, Finisher 4, Tor's cell, Issue #11. Print-truth only;
nothing compiled, nobody but Tor reported to.
