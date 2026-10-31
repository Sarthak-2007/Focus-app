# CRITIC CHARGES — ISSUE #14, THE RECEPTION MERGE

Seat: **Stachel (critic)**, Pforte's cell, GitHub issue #14,
`Sarthak-2007/Focus-app`, branch `agent/issue-14`.
Source of law: `studio/plan/AUDIT-setup-sheet.md` (S-1…S-15 §2,
merge list §6, acquittals §8). Scope unrestricted. Reading and
grep only — nothing compiled, no test run, per house rules.

**Snapshot discipline (read first):** line numbers below are the
**as-ported tree** read at audit start — `ReceptionSheet.kt`
692 lines, `ReceptionSheetTest.kt` 350 lines, working tree as
delivered by Empfang's port. During this audit the tree MOVED
under me (see C-18): `ReceptionSheet.kt` grew 692 → 784 lines as
another seat began landing §6 items live. Where an in-flight edit
changes a charge's state, the charge says so and quotes both
states. Every other line number is the as-ported state Pforte
asked me to audit.

---

## (a) STRUCK-SENTENCE CENSUS — every reprint in `app/src/`

### C-01 · PARK's plate reprints "FIRED AS SET" — a regression, not a leftover
- `app/src/main/java/com/example/ui/components/ParkDrawer.kt:109` —
  `"ROUTINES: SET BY HAND ON SHIELD · FIRED AS SET"` in `PLATE_BODY`,
  printed on PARK's spec plate.
- This is worse than the sheet's own reprint: HEAD had already
  struck it. `git diff` on ParkDrawer shows the port **replacing**
  HEAD's cured `"ROUTINES: SET BY HAND ON SHIELD"` and deleting
  HEAD's comment recording the strike (Truth Car 3 UNA-05, Hale
  W-11, "falsified on his phone (Oct 29, 20:00 SLEEP no-fire)… may
  print again only when Wache's F1–F5 are in the same build AND a
  live fire has been re-verified").
- A standing test already condemns it:
  `app/src/test/java/com/example/TruthCar3SweepTest.kt:353` —
  `onNodeWithText("FIRED AS SET", substring = true).assertDoesNotExist()`
  (test `una05_plateDoesNotClaimFiredAsSet`, :337). Merging the
  port as-is ships a plate its own test suite forbids.

### C-02 · PARK's plate reprints "STUDY RECORD: IN YPT" — same regression pattern
- `ParkDrawer.kt:111` — `"STUDY RECORD: IN YPT"` in `PLATE_BODY`.
- `git diff`: HEAD printed `"STUDY HOURS: IN YPT"`; the port
  reverted it. Condemned by
  `TruthCar3SweepTest.kt:320` —
  `onNodeWithText("STUDY RECORD: IN YPT", substring = true).assertDoesNotExist()`
  and `:319` asserting `"STUDY HOURS: IN YPT"` exists
  (test `una08_platePrintsStudyHoursNotStudyRecord`).
- W-13/W-43's replacement was already law on this surface. The
  port un-laws it.

### C-03 · "IT FIRES AS SET" — the sheet's routine line (S-2, confirmed live)
- Production: `ReceptionSheet.kt:179` —
  `return "FIRST ROUTINE — ${first.name.uppercase()} · $hh:$mm. IT FIRES AS SET."`
  inside `receptionFirstRoutineLine` (:175–180), which filters
  `it.enabled` only and takes `minByOrNull { it.startMinutes }` —
  a `daysMask == 0` routine that can never fire is named and
  promised, and "FIRST" is smallest start, not next firing.
  Exactly as S-2 charges.
- Codified in tests: `ReceptionSheetTest.kt:244` asserts the
  struck sentence verbatim; `ScreenshotHarnessTest.kt:392` feeds
  it as the fabricated `firstRoutineLine` input for the
  11_reception captures. Curing :179 without both test files
  leaves the sentence printed in the shot harness forever.

### C-04 · "YOUR STUDY RECORD LIVES IN YPT" — two production surfaces, §6 cures one
- `ReceptionSheet.kt:305–306` — the sheet's boundary line
  (S-4's seat).
- `app/src/main/java/com/example/ui/components/ReceiptSheet.kt:176` —
  `text = "THE MACHINE KEEPS ITS OWN LOG · YOUR STUDY RECORD LIVES IN YPT"`,
  the receipt footer, printed after every finished session.
- Audit §6 item 3 cures S-4 on the sheet only. Nothing in §6
  touches ReceiptSheet. If this merge is the struck-sentence
  sweep, the receipt reprint survives it untouched and the census
  Pforte ordered is incomplete the day it lands. Either §6 gains
  the receipt line or the merge report must name it as surviving
  by decision, not by omission.

### C-05 · "BLOCKING STANDS WHOLE" — total's all-set branch (S-9, confirmed live)
- `ReceptionSheet.kt:158` —
  `null -> "ALL FOUR DOORS SET — BLOCKING STANDS WHOLE."`
- Codified: `ReceptionSheetTest.kt:147` asserts it verbatim.
- The overclaim is structural: the sheet never reads the
  warrant's third conjunct. `WatchStore.deriveWarrant`
  (`app/src/main/java/com/example/service/WatchStore.kt`, the
  `deriveWarrant` function) requires accessibility bound **AND**
  `a11yStampedThisResidency` **AND** usage ALLOWED before LIVE;
  four GRANTED door prints establish only the doors. The slide can
  still refuse while this sentence declares blocking whole.

### C-06 · "MID-SESSION" family — total branch, job line, rank line
- `ReceptionSheet.kt:163` (total, admin branch) —
  `"WITHOUT DEVICE ADMIN, MONOLITH CAN BE UNINSTALLED MID-SESSION."`
  Codified at `ReceptionSheetTest.kt:183`.
- `ReceptionSheet.kt:117` (job line, same false mechanism in
  prose) — `"THE ANCHOR. Stops Monolith being uninstalled or switched off mid-session."`
  Admin's receiver returns a warning string on disable; the
  mid-session switch-off paths are the interceptor's, not
  Admin's (S-6).
- `ReceptionSheet.kt:340–341` (rank line) —
  `"THE THIRD MAKES IT UNBREAKABLE."` — the same overreach one
  level up; S-6's cure ("GUARDS THE MACHINE ITSELF") is the only
  lawful form.
- Non-print acquitted: `ShieldMatrixScreen.kt:203` contains
  "mid-session" in a code comment only. No charge.

**Census summary:** firing family ×3 production prints
(sheet :179, PARK plate :109, plus the harness input :392);
YPT-record family ×3 production prints (sheet :305, receipt
:176, PARK plate :111 in its short form); WHOLE ×1 (:158);
MID-SESSION ×2 prints (:163, :117) + rank variant (:340).
Every one is also asserted or fed by a test — no cure lands by
editing production files alone.

---

## (b) S-1 VERIFICATION — CONFIRMED, mechanism quoted

### C-07 · The total converts UNPROVED into WITHOUT — audit's mechanism is exact
The converting code, `ReceptionSheet.kt:147–167` (as ported):

```kotlin
fun receptionTotal(snapshot: WatchSnapshot?, nowMono: Long): ReceptionTotal {
    …
    val count = prints.count { it.second == PermPrint.GRANTED }
    val firstLack = prints.firstOrNull { it.second != PermPrint.GRANTED }?.first
    val sentence = when (firstLack) {
        null -> "ALL FOUR DOORS SET — BLOCKING STANDS WHOLE."
        WatchItemId.ACCESSIBILITY, WatchItemId.USAGE_ACCESS ->
            "WITHOUT ${firstLack.boardName}, BLOCKING CANNOT PROVE ITSELF — " +
                "SESSIONS WILL NOT SEAL."
        …
```

There is no branch on print KIND anywhere in the function:
`firstLack` is the first print `!= GRANTED`, so a STALE print
takes the MISSING branch verbatim. `count` likewise counts only
fresh GRANTED.

The staleness bound, `WatchStore.kt:150` —
`const val STALE_AFTER_MS: Long = 45_000L` — applied at
`WatchStore.kt:355–363`:

```kotlin
perm == PermState.GRANTED && nowMono - m.measuredMono > STALE_AFTER_MS ->
    PermPrint.STALE
```

The re-derive beat, `ReceptionSheet.kt:217–224` (as ported):
a `LaunchedEffect(visible)` loop sets
`nowMono = SystemClock.elapsedRealtime()` then `delay(5_000L)` —
the sheet re-derives every 5 s but takes no new measurement
while it stands. The sweeps are wired at the host:
`MainActivity.kt:824` `onShown = { viewModel.runWatchSweep() }`
and `:826` `onCheckClick = { viewModel.runWatchSweep() }`.
**One correction to the audit's wording:** a fourth trigger
exists — `MainActivity.kt`'s `Lifecycle.Event.ON_RESUME` observer
calls `runWatchSweep()` — but it fires on returning from an
excursion, never for a man standing still reading the sheet. It
does not save the standing reader. The audit's substance stands:
~45–50 s of continuous reading collapses the total to
`GUARD 0/4` over four granted doors while each card prints
UNPROVED — two contradictory prints, one screen, one instant.

The codifying test, `ReceptionSheetTest.kt:216–225`:

```kotlin
fun staleGrantReadsAsLackInTheTotal() {
    // Granted 60s ago — past the store's 45s freshness proof,
    // so the print is STALE and the total must not claim it.
    val stale = allGranted(now - 60_000L)
    val total = receptionTotal(stale, now)
    assertEquals(0, total.count)
    assertTrue(total.sentence.startsWith("WITHOUT ACCESSIBILITY"))
```

The test's own comment says "must not claim it" and then asserts
the claim's opposite error: absence. **S-1 CONFIRMED in full —
bound, beat, triggers, and the test that guards the lie.**

---

## (c) S-3 FACT CHECK — the GuardSettings premise is REFUTED for this tree

### C-08 · This tree's GuardSettings carries NO peek/break minute fields
`GuardSettingsEntity`, `app/src/main/java/com/example/data/MonolithModels.kt:745–775`,
complete field list:

`id`, `browserBlockerEnabled`, `routineReseedV1Done`,
`routineReseedNoteDismissed`, `lastBootAtMillis`,
`lastRoutineTickMillis`, `lastCompositeSessionId`,
`lastReceiptOutcome`, `receiptSequence`, `lastReceiptSessionId`,
`shortsReelsSniperEnabled`.

No `peekMinutes`, no `breakMinutes`, no duration field of any
kind. A tree-wide grep for `peekMinutes` / `MIGRATION_12_13`
returns **zero hits** in `app/src/`. The database is
`version = 12` (`MonolithDatabase.kt:24`); its migrations are
`MIGRATION_7_8`, `8_9`, `9_10`, `10_11`, `11_12` only
(`MonolithDatabase.kt`, registered at `getInstance`). Frist's
12→13 columns are batch-10 work and **do not exist here**.

What this tree actually holds: hard constants —
`MonolithRepository.kt:106` `PEEK_DURATION_SECONDS = 120`,
`:108` `PEEK_COOLDOWN_MILLIS = 20 * 60 * 1000L`,
`:110` `SAFE_BREAK_DURATION_SECONDS = 10 * 60` — and the tier
itself is named `PEEK_2M` (`MonolithModels.kt:25–29`). So in
THIS tree the sheet's flat prints (`ReceptionSheet.kt:388–395`
as ported: "two minutes" / "Ten minutes") are **true today**.
S-3 is a future-false charge, not a present one.

And the audit's cure premise is false here twice over:
§2 S-3 / §6 item 5 say "value slots from the GuardSettings row
**the host already reads**." The host reads no such row for the
sheet — `ReceptionSheet`'s parameter list (as ported :185–200)
takes `snapshot, isFirstOpen, isNight, returnTick, soundEngine,
firstRoutineLine` and callbacks; no GuardSettings, no durations,
enter the composable at all.

**Ruling I ask Pforte to record:** S-3's slots **cannot land in
this merge**. They are flagged batch-10 / issue-dependent
(times merge + MIGRATION_12_13), exactly like S-8's ROOM beat.
Any builder who "lands" them here must either invent a settings
surface that does not exist or hard-code the constants behind a
slot-shaped lie. The correct merge act is the flag, printed in
the merge report — not the slot.

---

## (d) ATTACK THE MERGE — §6 as written ships new lies and breaks acquittals

### C-09 · §6 item 1 cures S-1's sentence and leaves S-1's count standing
§6.1 reads: "total branch on print kind + flip the codifying
test." The headline of S-1 is the **collapse to GUARD 0/4** —
and the count is `prints.count { it.second == PermPrint.GRANTED }`
(`ReceptionSheet.kt:156` as ported), which no sentence-branching
touches. Implemented literally, §6.1 ships a sheet whose sentence
says "aged proof" under a count that still reads 0/4 over four
set doors. The count is part of the total; a cure that does not
name it does not cure it. §6.1 must specify the stale count's
print (e.g. the Guard line's own grammar, `GuardLine.kt:39–46`,
which names the stale door instead of zeroing it) or explicitly
rule the 0/4 lawful — silence is not a ruling.

**In-flight proof this is not hypothetical:** the live edit now
in the tree (784-line snapshot) branches the sentence exactly as
§6.1 says, leaves `count` untouched, and its new aged-proof
sentence reads `"${item.boardName} IS SET — THE LAST PROOF AGED OUT. CHECK RE-PROVES IT."`
(`receptionAgedProofSentence`). That sentence **re-asserts as
present fact the very grant the card above it prints UNPROVED**:
STALE means the proof aged, not that the grant is known to
stand — the grant may have been revoked inside those 46 s, which
is the entire reason the print exists. Card: UNPROVED. Total:
IS SET, under GUARD 0/4. Three prints, three verdicts, one
screen. S-1's self-contradiction survives its own cure,
inverted. The builder's own comment concedes the sentence is
composed, not quoted: the audit names it "Stachel's words"
without quoting a total sentence — my words on record are the
S-12 gloss ("THE LAST PROOF AGED OUT — CHECK RE-PROVES IT"),
which pointedly does **not** say IS SET.

### C-10 · §6 item 1 + §5 item 6, implemented together, erase STALE whenever MISSING exists
The per-lack composition (§5.6, §6.14: "the total composes one
sentence per lacking door") defines "lacking" off the acquitted
A-2 MISSING branches. A door that is STALE is lacking fresh
proof but is not MISSING. The in-flight implementation shows the
failure mode concretely (current `receptionTotal`): `missing`
non-empty → the sentence is built from `missing` only; `stale`
is computed and discarded. A sheet with one revoked door and
three aged doors prices one door and says nothing about the
other three — at the exact decision moment §5.6 says the total
must price **every** lack. §6 as written has no ordering rule
for the mixed case. It needs one before a builder invents it.

### C-11 · The ROOM beat (S-8) — printed sentence quoted; this tree has NO settle machinery; #13-gated, never shippable here
Printed, `ReceptionSheet.kt:381–383` (as ported; :440–442 in the
live tree, words unchanged):

```kotlin
label = "THE ROOM",
line = "After twenty seconds untouched, the desk clock takes " +
    "the face: the time, the time left, when it ends."
```

Evidence there is no such machinery in this tree:
- The desk clock's only entry is a manual tap:
  `MainActivity.kt:553` `onOpenAlwaysOnDeskClock = { viewModel.toggleAlwaysOnDeskClock(true) }`,
  a Deck utility row (`TactileDeckScreen.kt:907–909`).
  `toggleAlwaysOnDeskClock` (`MonolithViewModel.kt:1563–1565`)
  merely sets a boolean. No idle timer, no 20 s counter, no
  untouched detection exists anywhere in `app/src/` — grep for
  `settle` in the flow sense returns only animation/test uses;
  the 20 s settle lives only in `builds/flow-clock`, a different
  tree (audit §2 S-8, confirmed).
- The sentence is also false about the clock that DOES exist:
  `AlwaysOnDeskClockOverlay`
  (`AirlockAndDisarmOverlays.kt:70ff`) prints ONE figure —
  span-remaining, or elapsed on a stopwatch run — plus a progress
  bar. There is no separate "when it ends" wall time on its face.
  On a stopwatch run "the time left" does not exist at all
  (the audit's own stopwatch rider, §6.6, concedes this) and the
  clock never "takes the face" — a hand taps it in, and a tap
  dismisses it (`onDismiss`, tap anywhere).
**Verdict: the ROOM beat is issue-#13 / flow-clock dependent.
It must be flagged as such in the merge report and re-worded or
struck for any composition without that lane — never silently
shipped as true, and §6.6's "ZERO now, gate at composition"
must name who holds the gate. Right now it names nobody.**

### C-12 · The merge's premise is false: the port is a stale-base graft that deletes Truth-Car-3 work outside the sheet
§6 prices this merge as sheet items 1–15. The actual diff is
not that. `git diff` against HEAD shows the ported
`ParkDrawer.kt` and `MainActivity.kt` are **older bases** with
the reception grafted on:
- ParkDrawer loses the Shorts + Reels sled (Truth Car 3 UNA-01,
  the persisted `shortsReelsSniperEnabled` control) and the
  Social Sites sled row (UNA-02) — both deleted wholesale, with
  their constructor parameters (`shortsReelsSniperEnabled`,
  `onToggleShortsReelsSniper`, `socialSitesEnabled`,
  `onToggleSocialSites`).
- MainActivity loses the `InterceptPath` peek-offer wiring
  (UNA2-01: `EXTRA_INTERCEPT_PATH` parsing, removed; the
  3-arg `triggerAirlockIntercept` call restored), the UNA-07
  `loadoutCapIndex`, `pruneNote`, `appsSplit`, and the
  `onEndSafeBreak` Deck prop.
None of this is in §6, none of it is in §8's acquittals as
struck, and §8 acquits the PARK stub "as one document at two
moments" — not PARK minus two shipped controls. **Merging the
port as file replacements ships a regression dressed as an
introduction.** The lawful merge is a graft: ReceptionSheet +
SetupStore + stub row + host wiring, applied onto HEAD's
ParkDrawer/MainActivity — never the ported files whole.

### C-13 · §6 item 9 (Armed Handover) breaks the Guard line's acquitted grammar
§3 Proposal 3 / §6.9: DONE hands the Deck a handover "printed in
the Guard line's own slot: `GUARD 4/4 — ONE ACT LEFT: SLIDE.`"
The Guard line's law is `GuardLine.kt:9–27`: "**Three states, no
fourth**," a pure function of (snapshot, now) whose sameness is
what lets the seal freeze it honestly. A one-shot, unpersisted,
total-derived string in that slot is a fourth state wearing the
Guard line's clothes — and §8 acquits "the Guard foot" as the
header's own derivation. Worse, in the non-whole case the
handover prints "the standing sentence otherwise" in that slot,
displacing the lack-naming line (`GUARD n/4 — {NAME}`) at the
one moment — arrival on the Deck, slide in front of him — where
the named lack is the refusal's own price tag. The handover
needs its own slot or it does not ship.

### C-14 · §6 item 8 (Setup Ledger) can mint a false "WAS SET AT SETUP"
§3 Proposal 2 / §6.8: `doorsAtStampMask` recorded at DONE, later
printing `"WAS SET AT SETUP — SINCE LOST"` on PARK for a
regressed door. Today's `SetupStore.kt` (whole file, 38 lines)
holds exactly one boolean, `setup_done`. The proposal never
specifies the mask's evidence standard. DONE is live at **any**
door state (acquitted, §8) and pressed at whatever staleness the
standing sheet happens to show: a mask taken from the sheet's
prints at stamp time can record a door "set at setup" on a STALE
print — the same aged evidence S-1 forbids the total to claim —
or record GRANTED for a door revoked minutes later, and PARK
will then testify "SINCE LOST" about a loss nobody measured.
Unless the mask is defined as *fresh-GRANTED at a sweep taken at
the stamp*, §6.8 ships a new fabricated history. As priced
(SMALL) it also hides a SetupStore schema + PARK print + refusal
call-in — three surfaces, one line item.

### C-15 · §6 item 7 (Return Chit) prints a verdict that ages into a contradiction on the same card
§3 Proposal 1: the chit prints `"WENT OUT NOT SET · CAME BACK SET."`
in the visited card, with "SET in bold ink for that printing."
The verdict is honest at its instant (it requires
`measuredMono > departureMono` — that part is sound). But the
chit is print, and print stands: 46 s later the same card's door
word re-derives to UNPROVED while the chit beneath it still says
CAME BACK SET in bold ink. The sheet will carry a stale verdict
in its loudest dress directly under the word that refutes it —
S-1's disease, one card high. The chit needs an expiry law
(it dies with the next re-derive past the staleness bound, or
prints its tense honestly) before it ships; §6.7 has none.

### C-16 · §6 items 13–14 carry two qualifiers in §3/§5 that the merge list itself drops
1. **The tear.** §3 rules "Tear only when the press actually
   stamps; PARK reprints keep today's behaviour." §6.13 lists
   the tear choreography with no such qualifier. But DONE on a
   PARK reprint calls the same host path —
   `MainActivity.kt` `stampSetupAndEnter()` calls
   `setupStore.markCompleted()` unconditionally — so a builder
   following §6 alone will tear on every reprint, performing a
   first-completion ceremony on day forty. The qualifier must be
   in the merge item, not only in the mechanism section.
2. **The privacy lines.** §5.1 / §6.14 ship
   `"NOTHING LEAVES THIS PHONE — MONOLITH HOLDS NO NETWORK PERMISSION."`
   The permission half is verified: `AndroidManifest.xml:6–12`
   lists VIBRATE, FOREGROUND_SERVICE, POST_NOTIFICATIONS,
   RECEIVE_BOOT_COMPLETED, SYSTEM_ALERT_WINDOW — grep count for
   INTERNET is **0**. But "NOTHING LEAVES THIS PHONE" does not
   follow from it: the app hands URLs to the browser and intents
   to Android by design, and a manifest proves what *this app*
   may do, not what leaves the device. The audit itself refuses
   a stronger claim two sentences later (no "does not read"
   line); the same discipline applies to "nothing leaves." The
   lawful print is the permission sentence alone. As written,
   §6.14 ships the overclaim it warns against.

### C-17 · §6 item 4 (S-12 rider) is underpriced: the "distinguishing query" exists but the sweep never calls it
§2 S-12's rider says the enabled-but-unbound ("stuck") query
"exists in the tree and the sweep must call it." It exists —
`MonolithRepository.kt:682` `isAccessibilityEnabled()` reads
`Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES` — and its only
production caller is the legacy guard snapshot at `:780`. The
sweep's query, `watchGrantQuery` (`MonolithRepository.kt:645–654`),
answers ACCESSIBILITY purely as
`MonolithAccessibilityService.instance != null`. So the stuck
state is **unmeasurable by the sheet's only data source** until
`watchGrantQuery` changes — a WatchStore/repository change
outside the "card family, SMALL" price in §6.4. Landing §6.4's
prepare words without that change prints a state the sweep
cannot distinguish: a new lie with a priced ticket.

### C-18 · Process charge: this audit was taken on a moving tree — freeze before merge
`ReceptionSheet.kt`: 692 lines at audit start → 740 → 784 lines
during the audit (md5 `36dc35d4…` at last snapshot, 22:03 UTC);
`ReceptionSheetTest.kt` and `MainActivity.kt` likewise changed
md5 mid-audit (MainActivity's `receptionFirstRoutineLine` call
gained its `nowMillis` argument between two of my reads — before
that fix the live tree's call site and the new 2-arg signature
did not match, i.e. the in-flight tree was uncompilable on paper
for at least that window). Fibel's composition spec in this same
cell folder anchors explicitly on "the pre-merge tree
(ReceptionSheet.kt 692 lines)." If §6 keeps landing seat-by-seat
on a shared working tree with no freeze point, every seat's
line-anchored spec is being applied to a file another seat is
rewriting. Pforte: name a freeze, snapshot it (commit the lane),
and make all remaining §6 work cite the frozen hash — or accept
that the merge's evidence trail cannot be re-read by anyone,
including the merge re-audit §9 already owes (S-8, S-3).

---

## Disposition summary for Pforte

- **Merge-blockers:** C-01, C-02 (plate regressions vs HEAD +
  standing tests), C-07/S-1 (confirmed, cure must include the
  count — C-09), C-11 (ROOM beat #13-gated), C-12 (stale-base
  graft; merge as graft, not replacement).
- **Flag, do not land here:** C-08/S-3 (batch-10 dependent,
  premise refuted for this tree).
- **§6 must be amended before building, not after:** C-09
  (count + the IS SET sentence), C-10 (mixed MISSING+STALE
  ordering), C-13 (handover slot), C-14 (ledger evidence
  standard), C-15 (chit expiry), C-16 (tear qualifier + privacy
  wording), C-17 (S-12 rider's real price), C-04 (receipt
  reprint in or explicitly out).
- **Process:** C-18 (freeze + hash).

Nothing above touches §8's acquittals except where §6 itself
breaks them (C-13, and the A-2 tension inside C-10). The sheet's
spine — door cards, the acquitted MISSING branches, the honest
count's seat, DONE live at any state — stands.

— Stachel (critic), issue #14 cell
