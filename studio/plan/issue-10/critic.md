# CRITIC — Issue #10, the Receipt (audit fixes)

Seat: Critic, Zettel's cell. Scope unrestricted, per brief. Paper only;
nothing compiled, nothing run.

Tree state, stated once so every line below can be checked: the cell
checkout (`builds/cells/issue-10/`, branch `agent/issue-10`, HEAD
`1852181`) is byte-identical to the canonical merge-batch9 tree for
every receipt file I cite (verified by `diff` on MonolithRepository.kt,
ReceiptState.kt, MonolithModels.kt, ReceiptSheet.kt,
MonolithViewModel.kt, MonolithAccessibilityService.kt — all SAME).
DB version in this tree is **12** (`MonolithDatabase.kt:25`).

A note on citations: the audit's line numbers are systematically stale
against this tree (see N7). Every line number in this filing is the
CURRENT tree's. Where the audit's number differs, I give both.

---

## VERDICTS ON THE FOUR KNOWN CHARGES

### (a) `countHeldApps` counts PEEK-tier as HELD (W-41) — **STANDS, WORSE THAN STATED**

The function, current lines:

```kotlin
// MonolithRepository.kt:1318-1319 (audit cites :1255 — drifted)
fun countHeldApps(apps: List<BlockedAppEntity>): Int =
    apps.count { it.resolvedTier != AppBlockTier.OPEN }
```

That is LOCKED + PEEK_2M under one number, written to the row at
`MonolithRepository.kt:1384` and printed as `HELD {n} APPS`
(`ReceiptSheet.kt:129-133`). PEEK_2M is not held in any sense the word
survives: the enforcer returns without intercepting a PEEK app for the
whole of every Safe Break (`MonolithAccessibilityService.kt:278-280`,
same door in `MonolithShieldService.kt:170`) and for the whole of
every live 2-minute lease (`MonolithAccessibilityService.kt:282-286`).
Tier definitions: `MonolithModels.kt:13-36`.

Why WORSE than the audit stated it — three counts the audit did not file:

1. **This exact lie was already tried and convicted inside this same
   tree, on the Deck, and the verdict never propagated to the slip.**
   `MonolithCustomComponents.kt:438-442`, the imprint tenant's own
   comment (Truth Car 3, UNA2-02): *"A PEEK app does not shut and is
   not held: it meets the airlock and a 2-minute lease. The old single
   count (LOCKED+PEEK under WILL SHUT / HELD) named the sum with the
   stronger verb; the number was real, the noun was the lie."* The
   receipt prints the pre-verdict dialect the programme already killed
   one surface over. W-41 is not a discovery; it is an unpropagated fix.
2. **The split the cure waits for already exists — twice — and the slip
   reads neither.** `appsSplit` computes shut/peek in the VM
   (`MonolithViewModel.kt:555-562`); `SealFreeze` captures the split at
   the seal instant (`MonolithViewModel.kt:622-626, 878-879`). Group C1
   prices persisted split columns as future data work; fair for
   rehydration, but nobody in the audit notes the seal-instant split is
   already computed in-process and thrown away before the row is read.
3. **The sum lives in three places, so a one-place fix does not land.**
   `countHeldApps` (Repository:1318), an inline re-implementation for
   the Deck strip that never calls it (`MonolithViewModel.kt:542`,
   `all.count { it.resolvedTier != AppBlockTier.OPEN }`), and the
   SealFreeze fallback (`MonolithViewModel.kt:876-877`). Fix the
   function alone and the strip keeps summing; fix the semantics of
   the function and the SealFreeze fallback silently changes meaning.

The sites half of W-41 splits in two:

- **Toggle half — STANDS.** `startSession` books
  `heldSites = dao.getAllSitesSnapshot().size`
  (`MonolithRepository.kt:1385`) and never reads the browser toggle,
  though the reader exists (`MonolithRepository.kt:816`) and the
  enforcer honours it (`MonolithAccessibilityService.kt:254`). Toggle
  OFF, slip still books every site. Verified in the full body of
  `startSession` (`MonolithRepository.kt:1321-1392`): no guard read.
- **Factory half — FALLS as mechanised.** The audit (item 9) and the
  designer (§4.1, designer.md:327-329) both claim an emptied list
  prints `0 SITES` "while the enforcer silently holds the 8 factory
  domains." There is no enforcer-side factory fallback in this tree —
  Truth Car 3 removed it, and the enforcer says so on its face:
  `MonolithAccessibilityService.kt:246-251` — *"There is NO factory
  fallback — an emptied list blocks no websites… they are never
  smuggled back in at enforcement time."* The real mechanism is worse
  and different — filed as N5 below.

### (b) Deflection assembly never filters outcome — **STANDS, exactly as filed**

The assembly filter chain, `MonolithRepository.kt:1608-1623`: window
query → intention match (`:1610`) → `packageName != "com.monolith.system"`
(`:1611`) → valve-break-window exclusion (`:1612`) → 30s-per-package
dedupe (`:1614-1622`) → map to `ReceiptDeflection`. `log.outcome` is
never read, though the field exists (`MonolithModels.kt:458`, entity
at `:451-460`; audit cites :434 — drifted). `ReceiptDeflection`
(`ReceiptState.kt:124-128`) drops the outcome at the contract, so the
sheet (`ReceiptSheet.kt:139-144`) cannot tell the rows apart even in
principle.

Sharpening the audit: the vocabulary that can actually reach this
filter is exactly **two** strings. `recordIntercept` has precisely
three production callers, all in the VM: `"Used 2m Peek"`
(`MonolithViewModel.kt:2412`, the let-in), `"Stayed Focused"`
(`MonolithViewModel.kt:2437`, the true turn-away), and
`"Split Into 10m Micro-Step"` (`MonolithViewModel.kt:1978`) — and the
split row is unreachable here, because it is logged with
`sessionIntention = microTask.title` (VM:1977) while assembly demands
`log.sessionIntention == row.intention` (Repository:1610). The fourth
writer, the disarm bookkeeping rows (`MonolithRepository.kt:2347-2353`),
rides package `com.monolith.system` and is already excluded. So the
printed list today = turn-aways **plus every let-in**, in identical
type. Neu C-03 verified, no wider, no narrower.

### (c) The slip has no total — **STANDS, exactly as filed**

`sealedElapsedSeconds` is declared on `PendingReceipt`
(`ReceiptState.kt:147`) and `ReceiptState` (`ReceiptState.kt:176`),
filled at assembly (`MonolithRepository.kt:1640`,
`= row.spanElapsedSeconds`), carried at publish (`:1725`) and at
rehydration (`:1777`) — and read **zero times** by the sheet. A grep
of `ReceiptSheet.kt` for `sealedElapsedSeconds` returns no hits; the
sheet prints ARMED/PLANNED (`:116-122`) and ENDED (`:123-125`) and no
duration of the lived span anywhere. On FLOW slips, where PLANNED is
lawfully null (RC-02), the slip therefore contains no duration figure
at all. Unanimous in the audit; unanimous here.

### (d) Dismissal is unpersisted — **STANDS, exactly as filed**

Dismissal lives in one host variable: `MainActivity.kt:268` —
`var dismissedReceiptSessionId by remember { mutableStateOf(-1) }`.
Plain `remember`: it dies on process death and on activity
recreation. Every dismissal door writes only it (sheet `onDismiss`,
`MainActivity.kt:783`; render gate `:776-785`). Rehydration is
unconditional: at every process birth `runSliceERecovery()` installs
`repository.rehydrateReceipt()` whenever `_receiptState` is null, with
no dismissed check anywhere (`MonolithViewModel.kt:1098-1102`). The
standing receipt's facts persist (№ + pointer + outcome in
`guard_settings`, `MonolithRepository.kt:1530-1534`); the dismissal
does not. A dismissed slip re-ambushes at cold start, mechanically
guaranteed. The designer's §3 (designer.md:199-214) re-derives the
same two-layer failure independently — concurrence noted.

---

## THE REST OF THE AUDIT, SPOT-VERIFIED

I re-checked the remaining items against code rather than trusting
the six seats. All stand unless noted:

- **Item 2 (№ greyer than title)** — stands. №: 13sp Bold,
  `onSurfaceVariant` (`ReceiptSheet.kt:103-109`); title: 15sp Bold,
  `onBackground` (`:91-98`).
- **Item 3 (no date)** — stands. The sheet's only formatter is
  `hhmm` = `"HH:mm"` (`ReceiptSheet.kt:199-200`); `ReceiptState`
  carries no date field at all.
- **Item 4 (PLANNED recompute)** — stands, mechanism verified:
  `plannedSeconds = row.spanTargetSeconds` (`Repository:1639`), and
  `spanTargetSeconds = totalStudyBlocks * effectiveTargetSeconds +
  (blocks-1)*600` with `effectiveTargetSeconds = targetDurationSeconds
  + penaltySecondsAdded` (`MonolithModels.kt:379-380, 399-401`). A
  mid-session tamper penalty is inside the printed plan. C3's
  dependency is real and currently unlanded: `sealedSpanTargetSeconds`
  exists nowhere in the tree (grep: no hits).
- **Item 6 (ENDED edge)** — stands as filed: unstamped rows fall back
  to the caller's instant (`Repository:1567`).
- **Item 7 (outcome words)** — stands, including the double-print,
  which is mechanical: `receiptOutcomeWord` returns
  `"ENDED · SHIELD GAP"` iff `row.outcome == ELAPSED_SHIELD_GAP`
  (`Repository:1688`), and `shieldWasOff` is set from the **same**
  condition (`Repository:1645`), so the gap prints twice on one slip
  whenever it prints at all (`ReceiptSheet.kt:124` + `:145-153`).
  `"SLEPT THROUGH"` at `Repository:1686`; tautological plain `"ENDED"`
  at `:1688`, printed unconditionally at `ReceiptSheet.kt:123-125`.
  **One correction to the audit (see Attack 4):** PARTIAL's evidence
  is *not* discarded.
- **Item 8 (WITHIN live re-derivation)** — stands. Hand-armed rows
  re-derive at assembly (`Repository:1583-1585`) through
  `routineWindowLabelAt`, which reads the **current** routine table
  and its **current** `enabled` flags (`Repository:1654-1655`) — at
  assembly and again at rehydration. A routine disabled or renamed
  after the session rewrites the restored slip.
- **Item 10 (BREAK BANKED)** — stands, live path verified end to end:
  the only spend writer inserts `heldSeconds =
  EMERGENCY_BREAK_HELD_SECONDS` = 60 (`Repository:2235-2242`, constant
  `:121`); the routine branch finalises the session into a publishable
  judgement (`Repository:2294` → `finaliseRoutineSession`); assembly
  sums the ledger (`Repository:1593-1594`); the sheet prints
  `bankedBreakSeconds / 60` — floor (`ReceiptSheet.kt:134-138`) —
  while the same file's `longSpan` ceilings (`ReceiptSheet.kt:204-211`).
  The 60-second hold **price** prints as 1 MIN **banked**. A manual
  valve break publishes nothing (`VALVE_BROKE` → `finishKindOf` null,
  `ReceiptState.kt:100-105`), as the audit said.
- **Item 11 WINDOW** — stands: routine rows extend the deflection
  window to `maxOf(trueEndedAtWallMs, row.windowEndMillis)`
  (`Repository:1600-1604`).
- **Item 13 / W-43 (footer)** — stands on clause 2. The footer string
  is verbatim at `ReceiptSheet.kt:176`. Clause 1's precision holds:
  the machine's log of the slip is № + pointer + outcome only
  (`Repository:1530-1534`). One addition the audit missed: the cured
  dialect already exists in-tree — the Park Drawer prints
  `"STUDY HOURS: IN YPT"` (`ParkDrawer.kt:122`). The footer is not
  waiting for an invention; it is inconsistent with its own app.
- **Item 14 (faulted sheet)** — stands on wording, pulse, and retry
  silence: `"(SERVICE DIDN'T STOP)"` at `ReceiptSheet.kt:159`; the
  retry **is** scheduled (`MonolithViewModel.kt:1279-1281`,
  `faultWall + 30_000L`) and `ReceiptState` has no field to carry it;
  the E700 completion pulse fires on `finishPulses(kind, row)` alone
  (`MonolithViewModel.kt:1246-1255`) while the fault verdict cannot
  exist before E1520 (`Detention.FAULT_DEADLINE_MS`,
  `ReceiptState.kt:206`) — a faulted finish sounds clean first,
  whenever its kind pulses. **One overstatement:** the "title-height
  void" — the faulted branch substitutes an empty `Box` with
  `Modifier.weight(1f)` and **no height** (`ReceiptSheet.kt:99-102`);
  the Row's height collapses to the №'s 13sp. The horizontal void is
  real; no title-height is reserved. Aug's frame charge should be
  re-filed as a width void, not a height void.
- **Items 15-18 (object)** — all stand on the code: 24dp radius +
  1dp `outlineVariant` border (`ReceiptSheet.kt:77-79`); uniform
  `spacedBy(8.dp)`, zero rules (`:83`); scrim 0.12 (`:69`); 12dp
  padding on all sides (`:71`); `fadeIn`/`fadeOut` (`:60-64`) with
  `visible = true` **hardcoded** — the exit fade is not merely dead,
  it is unreachable, since the host unmounts the composable instead
  of toggling visibility (`MainActivity.kt:776-785`). And the sheet
  never reads `receipt.restored` or `receiptPublishedMonoMs` (grep:
  no hits in `ReceiptSheet.kt`), so a restored slip re-performs the
  arrival the contract (Z6) forbids — the flag is carried and ignored.
- **Item 19 (dismissal gesture)** — stands: the scrim's
  `.clickable(onClick = onDismiss)` (`ReceiptSheet.kt:70`) is a
  Compose clickable — it fires on UP, swallows the tap's own target,
  and arms from the first frame.
- **Item 20 (keeping)** — stands: `standingReceipt()` is a single
  slot (`Repository:1530-1534`); no surface can summon an older №.
- **Item 21 (silences)** — stands: MISSED / LATE_FIRE judgements map
  to no finish kind (`ReceiptState.kt:90-98`) and get no slip, while
  PARTIAL does.

---

## ATTACKS ON THE PLANNED FIXES

### Attack 1 — A4's placement is the whole cure; the audit never proves it, and one wrong layer breaks routine judgement (HIGH)

The audit's A4 says "filter to blocked outcomes only" without naming
the layer. There are three candidate layers and two are wrong:

- **At the DAO / `getInterceptLogsBetween`** — breaks
  `computeAttendedSeconds` (`Repository:965`), the routine-judgement
  evidence reader, which consumes the same query. See N1.
- **At the sheet** — impossible: `ReceiptDeflection` already dropped
  the outcome (`ReceiptState.kt:124-128`).
- **Inside `assemblePendingReceipt` only** — safe, and provably so:
  assembly's only callers are the publish path
  (`MonolithViewModel.kt:1211, 1358`) and `rehydrateReceipt`
  (`Repository:1767`); both feed the slip and nothing else. The
  judgement reader bypasses assembly entirely.

The designer lands on the right layer (D1.1) and adds a genuinely
correct ordering law (D1.3: the outcome filter must run **before**
dedupe, because today a peek row enters the dedupe map at
`Repository:1614-1622` and can swallow a true block of the same
package 20s later). Credit both. But Zettel should not merge A4 on
the audit's wording alone — the audit's one-line cure, handed to a
builder who reaches for the shared query, silently rewrites routine
judgements. The layer must be written into the cure.

### Attack 2 — The designer's SHUT allowlist contains a string that can never print, classified on a guess (MEDIUM)

Designer D1.2 (designer.md:74-79) allowlists
`{"Stayed Focused", "Split Into 10m Micro-Step", "BLOCKED"}`. Two of
the three entries fail his own "enumerated, not assumed" standard:

- `"Split Into 10m Micro-Step"` is **unreachable** at assembly — its
  rows are logged under the micro-step's title, not the session's
  intention (VM:1977 vs Repository:1610, see (b)). The designer
  enumerated *writers*; he never checked *reachability*. The entry is
  inert today and armed tomorrow: the day anyone "fixes" the
  intention mismatch as a consistency bug, splits — a man redirecting
  his own task, neither shut out nor let in in the slip's sense —
  start printing as deflections, by a classification nobody argued.
- `"BLOCKED"` has **no production writer**; its only in-tree source
  is test fixtures (`ScreenshotHarnessTest.kt:482, 491`). The
  designer's justification — "the dialect older rows may carry"
  (designer.md:61) — is invented provenance offered in an evidence
  table. Including it is harmless (fail-closed against a string that
  may not exist); presenting it as enumerated fact is not.

### Attack 3 — A1 as designed imports a rounding lie into the one line whose only job is to be the fact (MEDIUM)

Audit A1 and designer D2.1/D2.7 both specify
`RAN {longSpan(sealedElapsedSeconds)}` with the sheet's existing
formatter. That formatter **ceilings**: `(seconds + 59) / 60`
(`ReceiptSheet.kt:205`). For PLANNED — a target — ceiling is a
dialect. For RAN — "the lived span," the figure six seats demanded
because it is what actually happened — ceiling overstates every
non-exact span by up to 59 seconds: a 61-second session prints
`RAN 2 MIN`; a 20-second FLOW finish prints `RAN 1 MIN`. The designer
embraces this verbatim ("`RAN 1 MIN` (the ceiling dialect's smallest
nonzero)", designer.md:172-173) while his own D2.4 warns that if RAN
and the wall stamps "disagree by a minute, the field wins" — his
formatter *manufactures* that minute on most slips. Nobody in the
audit or the cell priced an exact-seconds or floor dialect for the
total. As specced, A1 ships a systematic overstatement wearing the
audit's unanimity.

A second A1 interaction nobody filed: for routine rows,
`sealedElapsedSeconds = row.spanElapsedSeconds`
(`Repository:1640`), and after finalise the row's `elapsedSeconds`
**is** the attended-seconds figure (see Attack 4), from which
`spanElapsedSeconds` derives (`MonolithModels.kt:414-426`). On a
PARTIAL slip, A1's `RAN` and B5's proposed
`PARTIAL · {attended} OF {window}` would print the **same seconds**
under two different names on one slip. A1 and B5 cannot both land as
specced without resolving which figure RAN names on routine rows.

And a third: the audit contradicts itself on where its total goes.
Item 5 places it "under ENDED over a rule"; item 16's sectioning is
"head / body / total / foot" — a total *section*, which in the
audit's own grouping sits after the body (the deflection list). The
designer silently picked item 5 (designer.md:134-151) and argued
against the foot placement the audit's item 16 implies, without
noting the conflict. Zettel/Ren should rule the placement explicitly;
right now two seats can each claim the audit backs them.

### Attack 4 — The audit overstates B5: PARTIAL's evidence is not discarded (MEDIUM)

Audit item 7: attended seconds are "computed at judgement and
discarded," so B5 needs "a small state addition." False on the code:
`finaliseRoutineSession` **books** them — `elapsedSeconds = attended`
in the judgement write (`Repository:1036-1039`, computing at
`:1024`). The figure is on the row the assembly already reads. B5's
cure is a formatting decision at assembly, not a state addition; the
audit priced a schema change for a field that exists. (Caveat, stated
plainly: what the row carries is attended seconds in `elapsedSeconds`;
whether `spanElapsedSeconds` returns it untouched for every routine
shape is the builder's check — see Attack 3's interaction.)

### Attack 5 — A9 was priced as mergeable-now; it is a schema migration (LOW-MEDIUM)

The audit books dismissed-№ persistence in Group A ("batch-10
mergeable now… small, no new surface," item 20/A9). Any Room home for
the dismissed identity is a **v12 → v13 migration** plus a
targeted-UPDATE DAO write — the designer prices this correctly
(designer.md:453-459, Dependency 3) and the audit does not mention it
at all. Not a fatal flaw, but Group A's "mergeable now" label is
wrong for A9, and the batch should pay one version bump for A9 + C1
together, as the designer suggests.

Two holes in the designer's otherwise strong §3, for the record:
(i) his host gate is "initialised from the persisted value (via the
VM)" (designer.md:279-282), but VM recovery is asynchronous
(`MonolithViewModel.kt:1099`, a `viewModelScope.launch`) and the host
composes with `-1` first (`MainActivity.kt:268`) — his acceptance
case 1 ("Deck opens clean; no slip, ever") does not survive the first
frames unless first composition is held until the dismissal state
loads; the design never specifies that hold. (ii) His B3.3 counts
dismiss-by-navigation as a dismissal event — correct — but the
navigation door today fires through the same `LaunchedEffect` that
also fires on ordinary Deck exits (designer.md:204-206 cites
`MainActivity.kt:303-308`); persisting from it means a slip he never
saw (issued while he was on another tab) is dismissed unseen. That
may be the intent; it should be ruled, not inherited.

### Attack 6 — C1's columns, written by the existing writer, inherit a birth-not-seal lie (MEDIUM — see also N4)

Group C1 prices `heldShutAtSeal`/`heldPeekAtSeal` "seal columns."
The existing columns with those names are written at **birth**, in
`startSession`'s insert transaction (`Repository:1383-1391`) — the
migration comment admits it in so many words: *"Fader/Zettel — the
held counts, captured at birth"* (`MonolithDatabase.kt:124`), four
lines after the same file calls them *"the persisted seal record"*
(`MonolithDatabase.kt:109-111`). The seal is a separate, later act
(`sealSession`, `Repository:1414-1445`) that never touches them, and
the cascade's own E-11 note explains why birth is the wrong instant:
tier writes land inside the proving window, so the seal re-captures
the freeze at t=0 (`MonolithViewModel.kt:857-862`). If C1's new
columns are written by the same birth writer — the path of least
resistance — they will be birth counts wearing seal names, and W-41's
return line (designer D4.4) will print them as seal truth. The cure
must name its writer: seal-instant, or rename the claim.

### Attack 7 — The designer's returned HELD line re-lumps what W-41 split (MEDIUM)

Designer D4.4 returns the line as
`HELD {shut} SHUT · {peek} PEEK · {sites} SITES` (designer.md:357-372),
defending the umbrella HELD with "PEEK apps were held behind a
lease-door." That is the W-41 metaphor re-argued. The in-tree grammar
he claims to mirror does the opposite: the imprint tenant prints
`"{n} HELD · {m} PEEK"` (`MonolithCustomComponents.kt:475-483`) —
HELD binds to the shut count only; PEEK stands as its own noun, never
under HELD. His D4.7 also prints `HELD 0 SHUT · …` in full, where the
same tenant's omission law refuses a zero segment under the verb
outright (`MonolithCustomComponents.kt:471-474`: *"0 WILL SHUT is
never a sentence"*). If the line returns, it should return in the
grammar the app already ruled, not under a resurrected umbrella.

### Attack 8 — Finisher-3's site-marking cure re-derives at assembly what was known at intercept (MEDIUM)

Audit item 11 requires site entries to print distinguishably.
Finisher-3 (finisher-3-deflections.md:138-145) proposes
`isSite = (log.appName ∈ dao.getAllSitesSnapshot() domains)` —
membership in the **current** site list, tested at **assembly** time.
That is the WITHIN sin (item 8) rebuilt: a fact that was certain at
intercept time — the enforcer launches site intercepts with
`path = InterceptPath.SITE` (`MonolithAccessibilityService.kt:262-267`)
and the airlock state **carries that path** (`interceptPath`,
`MonolithViewModel.kt:2359`) — is instead guessed later from a list
the user may have edited in between. The finisher names the
removed-domain edge himself (finisher-3-deflections.md:145) and ships
it anyway. The truthful cure is at the write: the three
`recordIntercept` call sites (VM:1974, 2408, 2433) sit on state that
knows the path and pass everything *except* it; `InterceptLogEntity`
has no field to receive it (`MonolithModels.kt:451-460`). One column,
written at the only moment the fact exists, beats a membership guess
at assembly — and the same column would give A4 a stored class to
filter on instead of free-text outcome matching.

---

## NEW CHARGES (not in the audit)

### N1 — A peek counts as attendance: the outcome-blindness is load-bearing in routine judgement (HIGH — most serious)

`computeAttendedSeconds` — the evidence engine behind ATTENDED /
PARTIAL / MISSED — reads intercept logs through the same DAO query
with **no outcome filter and no intention filter at all**
(`MonolithRepository.kt:965-966`): every log in the window becomes a
5-minute coverage arc (`EVIDENCE_COVERAGE_MILLIS = 300_000`,
`Repository:139`; union at `:976-988`), feeds the judgement
thresholds (`Repository:1030-1035`), and sets the E3 evidence bit
(`Repository:1042-1043`). Consequences, plainly:

- A `"Used 2m Peek"` row — the machine **letting him in** — books the
  same attendance evidence as a turn-away. Presence is proven by the
  act the session existed to prevent.
- The reader is not even session-bound: intercepts from a *manual*
  session overlapping the window count toward the routine's
  attendance, because association is timestamp-only
  (`MonolithDao.kt:320-324` comment admits the design).

This is the same dataset as charge (b), read by a more consequential
consumer. The audit treated outcome-blindness as a print defect; it
is a judgement input. It also cuts the other way for the fix: any
builder who "fixes" (b) at the shared query silently changes
judgements. Forward to Lade's routine audit with priority — but the
receipt cell must know A4's blast radius before it merges anything.

### N2 — The live shield counts a let-in as an "urge stopped" (HIGH)

`recordIntercept` is outcome-blind at the write, not just the read:
for **every** resolution — peek included — it increments the app's
`interceptCount` and the session's `phantomReachesBlocked`
(`MonolithRepository.kt:2111-2124`). That counter prints on the live
shield notification as `"{n} urges stopped"`
(`MonolithShieldService.kt:144`) and sums into the 7-day history's
interception counts (`MonolithRepository.kt:2391`); the VM's
`totalInterceptions` likewise takes `maxOf(logs.size, sum of
interceptCount)` (`MonolithViewModel.kt:526-529`), peeks included.
So while the slip (b) is the audit's exhibit, the same conflation is
running live, mid-session, on the surface he watches while it
happens. A4 cures one of three readers. Nobody in the audit or the
cell has filed the other two.

### N3 — The deflection list is a list of *resolutions*, not of attempts — biased in both directions (HIGH)

Intercept logs are written **only** when he resolves the airlock one
of three ways (VM:1974, 2408, 2433). The intercept itself writes
nothing: `triggerAirlockIntercept` (`MonolithViewModel.kt:2315-2373`)
records no row, and the accessibility service logs nothing either —
it launches the airlock and returns (`MonolithAccessibilityService.kt:
288-293`). An attempt he abandons — backs out, waits out, never
resolves — leaves no trace and can never print. Meanwhile the
contract defines the list as *"a blocked attempt the machine turned
away"* (`ReceiptState.kt:119-123`). Both halves are false: let-ins
print (charge (b)), and un-resolved turn-aways — plausibly the
majority of real blocks — are absent by construction. Even a perfect
A4 leaves the slip presenting a self-selected sample (only attempts
he stopped to adjudicate) as the session's deflection narrative. The
audit verified the list's rows; it never asked which attempts never
become rows.

### N4 — The held counts are birth counts; enforcement is live; the two diverge by design (MEDIUM-HIGH)

Filed at Attack 6 as a cure-defect; it is also a standing data
defect. During an active session his rules can only be **tightened**
(`MonolithViewModel.kt:2042-2055`, the one-way ratchet), and the
enforcer reads tiers **live** on every event
(`MonolithAccessibilityService.kt:273-275`). So a session can
lawfully end up holding *more* apps than its slip will ever print:
the slip's figure froze at birth (`Repository:1384`), the shield's
set did not. Even a perfectly split C1 figure, birth-written,
understates a tightened session. The audit asked "is the number the
right set?" — the prior question is "the set **as of when**?", and
the row, the freeze, and the enforcer currently answer three
different ways (birth / seal / live).

### N5 — The sites lie's real mechanism: an emptied list silently resurrects (MEDIUM-HIGH)

Supersedes the audit's factory-fallback story (see (a), sites half).
`ensureSeededAndSyncPackages` re-inserts all 8 factory domains
**whenever the sites table is empty** (`MonolithRepository.kt:
194-196`; defaults at `MonolithModels.kt:508-517`), with no
seeded-once flag for sites — unlike routines, which carry reseed
flags. It runs at every VM birth (`MonolithViewModel.kt:767-768`)
and on every `refreshSystemGuard` (`:2451`). Therefore: he empties
his site list (his ruled right — the Guard list *is* the enforced
list), and the app quietly un-empties it at next launch. The slip's
`0 SITES` scenario is nearly unreachable; what actually happens is
his edit is undone without a word, and a session born mid-process
after emptying can still diverge across a restart (born at 0, reseeded
to 8 mid-session, enforced at 8, printed at 0). The designer's D4.3
requirement of an "enforcer-derived sites figure" was specced against
the wrong mechanism: until someone rules whether reseed-on-empty
survives, there is no stable sites truth for any figure to derive
from. This is a settings-integrity charge that happens to surface on
the slip.

### N6 — Dedupe is per-package; site rows all share the browser's package (MEDIUM)

The 30s dedupe keys on `packageName` (`Repository:1614-1622`). Site
intercepts are logged under the **browser's** package
(`com.android.chrome` etc.) with the domain in the name slot
(`MonolithAccessibilityService.kt:262-267` → VM resolution writes).
So two *different* blocked sites, in the same browser, 20 seconds
apart, dedupe into one printed line — while two *different apps* never
collide. The audit verified the dedupe as contracted ("30s dedupe…
verified as contracted," Acquittals) without noticing the key is the
wrong identity for half the list's population. Post-A10, when site
entries print distinguishably, this undercount becomes visible and
will read as a missing entry.

### N7 — The audit's evidence lines are stale throughout (LOW, process)

Examples: `countHeldApps` cited at Repository:1255, actual 1318-1319;
`InterceptLogEntity` cited at Models:434, actual 451-460; spend write
cited at "Repository:2047ff," actual 2235-2242 (assembly read at
1593); B1/B4 price "+DB v12" stamps when the tree is already **at**
v12 (`MonolithDatabase.kt:25`) — those stamps are v13 work now. Every
charge I re-verified landed on substance, so this is not an accuracy
conviction; it is a handling warning: a builder who navigates by the
audit's numbers lands in wrong functions (Repository:1255 today sits
inside loadout tier resolution), and two of the cell's own filings
(designer.md:327-329 on the factory domains) inherited the audit's
stale mechanism without re-reading the enforcer. Evidence discipline
decayed exactly where the audit demanded it of others.

---

## WHAT THE AUDIT AND THE CELL GOT RIGHT (so it survives this filing)

- The four known charges' substance: all stand. The audit's verdict
  breath — "the words are a receipt; several of its figures are not
  yet facts" — is accurate and, on (a), understated.
- Designer D1.1/D1.3 (assembly-local filter, filter-before-dedupe)
  and §3's dismissal contract (pair storage, two gates, faulted
  exception, v13 pricing at Dependency 3) are the strongest fix work
  in the cell, subject to Attacks 2, 5, 7.
- The acquittals I could test held: ENDED from `endWallMillis`
  (`Repository:1567`), the omission law's sentinel gate
  (`ReceiptSheet.kt:129`), FLOW's absent PLANNED (`Repository:1639`),
  GRACE_CANCEL / VALVE_BROKE silence (`ReceiptState.kt:100-105`).

## BOTTOM LINE FOR ZETTEL

Merge order matters more than merge content here: A4 must land
**assembly-local with the vocabulary and ordering written in**
(Attack 1), or it either does nothing or rewrites routine
judgements. A1 must not land with the ceiling formatter unexamined
(Attack 3). C1 must name its writer before its columns (Attack 6).
And the cell should stop treating outcome-blindness as a receipt
problem: N1 and N2 are the same defect on surfaces that judge and
narrate live, and they outrank every print cure in this issue.

— Critic, Zettel's cell (issue #10). Paper only; nothing compiled.
