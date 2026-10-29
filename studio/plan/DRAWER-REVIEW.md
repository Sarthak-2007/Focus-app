# THE DRAWER REVIEW — the lie-register method, applied to the Drawer

**Reviewer:** Hale, Critics lead — seated directly by Ren (flat studio),
2026-10-29. This is the approved NEXT (a) from my build-1 review,
owed since the drawer merge (batch 3) put renders on disk.

**Ground:** merged trunk, `~/workspace/clone-inspect/Focus-app` —
`ui/screens/DrawerScreen.kt`, `data/DrawerDay.kt`,
`data/DayWordStore.kt`, `data/ReceiptState.kt`, the DAO/Repository/
ViewModel paths behind every printed word, and the harness renders
`shots/04_vault.png`, `shots/06_cabinet.png`,
`shots/07_cabinet_detail.png`, `shots/night/n04_vault.png`.

**Method:** printed words vs stored facts. Every charge files with
proof — file+line, a grep, or a render — or it does not file.
What is clean is acquitted by name. This review is about TRUTH,
not taste; the look is being replaced in a separate wave and is
not the target.

**Result: 8 charges — S1 ×1 · S2 ×2 · S3 ×5 — and 11 acquittals.**
The stored-task core of the Drawer is honest: permanence is real,
the months index is true, the frozen digest is true. The rot is
concentrated in one room: the cabinet, which inherited the old
Vault's claims after the machinery behind them was deleted or
never built.

---

## CHARGES

### DRW-01 — S1 — The cabinet's requirements are fiction; forging is a dice roll

The cabinet detail prints, for each artifact:

> `REQUIRES ${artifact.requiredFocusMinutes} MIN · ${artifact.requiredInterceptions} INTERCEPTIONS`
> — DrawerScreen.kt:429-430; rendered in shots/07_cabinet_detail.png
> ("REQUIRES 25 MIN · 1 INTERCEPTIONS")

and the list rows carry the same claim compressed into the serial
codes (`TOY 06 • 300M` — MonolithModels.kt:782, rendered in
shots/06_cabinet.png). The lore repeats it as fact ("Unlocked
after your first 25-minute Pomodoro block", "Unlocked after 1 hour
of total focus", "Awarded for 5 hours of deep focus" —
MonolithModels.kt:725, 751, 788).

No code anywhere enforces, checks, or even reads those numbers:

- `grep requiredFocusMinutes|requiredInterceptions` across
  `app/src/main` returns exactly two kinds of hit: the catalog
  definitions (MonolithModels.kt:717-790) and the Drawer print
  itself. Zero readers in Repository, ViewModel, DAO, services.
- The actual forge rule, in full, is MonolithRepository.kt:1782-1784:

```kotlin
val forgedId = if (allBlocksCompleted && !isRoutineSession) {
    CurioCatalog.artifacts.shuffled().first().id
} else null
```

Any completed non-routine session — a single 10-minute FLOW block
run to its end — forges one artifact chosen uniformly at random.
The Monolith Cube, printed at 300 MIN · 12 INTERCEPTIONS, can be
forged by the shortest session the wheel can sell; a genuine
300-minute session forges exactly one random artifact and may
never touch the Cube. The printed price is false in both
directions. The FORGED state itself is a true record (the id is
stamped on the session row and read back at MonolithDao.kt:68 —
see acquittal A-10); it is the stated price of the record that is
invented. A storage surface's one job is that its records mean
what they say. These do not.

### DRW-02 — S2 — The lore describes toys that do not exist

Every artifact's lore describes a working interactive toy:
"Drag across the screen to bend its wave pattern during a
5-minute break" (MonolithModels.kt:725, rendered verbatim in
shots/07_cabinet_detail.png); "Four concentric rings that tilt and
spin under your thumb" (:738); "Drag to fold and rotate all 16
vertices across space" (:789).

The Drawer's own header confesses the truth
(DrawerScreen.kt:157-162): "The kinetic toy VISUALS were never
built as views in the shipped app and are not built here; the
cabinet is the catalog, the record, and the selection — not the
toys." There is no surface anywhere in the app where a forged
artifact can be dragged, spun, or folded. The lore is Vault-era
copy carried into a storage screen, still speaking in the present
tense about interactions that were never built.

### DRW-03 — S2 — The detail date-line composes a timestamp that never happened

DrawerScreen.kt:176-181: the detail heading prints the FILING
date beside the true clock time of the finish:

```kotlin
val filing = DrawerDay.filingDate(detail.completedAtMillis)
"${headingLabel(filing, ...)} · ${clockTime(detail.completedAtMillis)}"
```

DrawerDay.kt:27-30 defines the filing date as the local date of
(instant − 210 minutes). So a task finished at 02:00 on Thu 29 Oct
files under Wed 28 Oct and the detail prints
**"WED 28 OCT · 02:00"** — one composed timestamp denoting a
moment 24 hours before the instant it names. The list rows share
the composition (a filing-date heading over true-clock row times,
DrawerScreen.kt StoredRow + buildDayGroups).

Each half is honest alone and the filing rule is his (the day
starts at 03:30; times print true clock — DrawerDay.kt:11-17
states both in the open). The composition is the lie: two true
facts set side by side with a "·" read, in the app's own voice
everywhere else, as a single date-time. Every finish between
00:00 and 03:29 prints a date-time pair that never occurred.

### DRW-04 — S3 — EARLIER is asserted from a page-size guess

The EARLIER row prints whenever a page came back full:
`_drawerHasEarlier.value = page.size == 50` —
MonolithViewModel.kt:1684, 1698, and the search pair at :1717,
:1731, over DAO queries with `LIMIT 50` (MonolithDao.kt:196, 202).
At an exact multiple of 50 stored rows the row asserts earlier
records that do not exist; tapping it loads an empty page and the
claim retracts in silence. A one-row existence probe would make
the print a fact; as built it is an inference wearing the voice
of one.

### DRW-05 — S3 — The screen's "today" is frozen at first composition; a declaration can file under a different date than its heading

DrawerScreen.kt:166-167:

```kotlin
val today = remember { DrawerDay.filingDate(System.currentTimeMillis()) }
val currentYearMonth = remember { DrawerDay.filingYearMonth(System.currentTimeMillis()) }
```

Unkeyed `remember` — computed once per composition lifetime,
never re-derived. But the write path computes its own date fresh:
MonolithViewModel.declareTodayWord stamps
`DrawerDay.filingDate(System.currentTimeMillis())` at tap time.
Hold the Drawer composed across the 03:30 boundary and the
declaration row keeps sitting under yesterday's heading while the
word files under the new filing day — the printed attachment and
the stored fact diverge, and the heading marks a date as "today"
that no longer is. The landing/month grouping reads the same
frozen `currentYearMonth` while `refreshDrawer()` re-derives the
ViewModel's month independently (MonolithViewModel.kt:1663-1664).

### DRW-06 — S3 — SELECT is a dead verb; SELECTED is consumed by nothing

Cabinet detail prints SELECT / SELECTED (DrawerScreen.kt:452-475,
rendered in shots/07_cabinet_detail.png — where a harness artifact
the app pre-selects already wears SELECTED). The verb's whole
effect is MonolithViewModel.kt:2181-2184: a detent sound and a
StateFlow assignment. A tree-wide grep for readers of
`selectedArtifact` returns only MainActivity.kt:223 (collect) and
:600 — passing it straight back into the Drawer that set it. No
Deck surface, sound path, or service reads the selection; the
toys it once chose between do not exist (DRW-02). The state is
also memory-only (MonolithViewModel.kt:311), so the "selection"
does not survive a relaunch either. SELECTED is literally true of
a variable and false of the app.

### DRW-07 — S3 — "HARMONIC SYNTH UNLOCKED" names a synth that does not exist

Five taps on the cabinet serial plate toggle
`_harmonicSynthUnlocked` (MonolithViewModel.kt:2186-2193) and the
detail prints HARMONIC SYNTH UNLOCKED (DrawerScreen.kt:478). The
flag's only readers are MainActivity.kt:224/601, feeding the same
print. There is no harmonic synth in the tree for it to unlock —
the soundscape mixer was removed by the user's own order in the
revamp, and no other consumer exists. The flag is memory-only
(MonolithViewModel.kt:331), and the "unlock" toggles back off on
the next five taps. An easter egg is a fine thing; printing the
name of a removed feature as an achieved state is not.

### DRW-08 — S3 — The tenth sub-step prints "010"

Drawer detail, DrawerScreen.kt:277: `text = "0${stepIdx + 1}"` —
string-prefix padding. Steps 1-9 print 01-09; step 10 prints
"010", step 11 "011". The app's two-digit voice breaks exactly
where it starts carrying information.

**Cross-reference:** the day word's own persistence durability
(declared word vs process death) is filed as G-02 in
plan/ESR11-GREP.md — the Drawer's capture is that file's second
live site.

**Noted, not charged:** DayHeading carries a copy-pasted block of
dead cabinet parameters (DrawerScreen.kt, the DayHeading
signature) — inert, prints nothing. Housekeeping, not truth.

---

## ACQUITTALS — checked, clean, by name

- **A-01 · The months-index SQL twin.** MonolithDao.kt:199
  subtracts the same 12,600s and formats with `'localtime'` —
  the SQLite twin agrees with DrawerDay's local-zone Kotlin rule.
  No phantom months, no UTC drift at the boundary.
- **A-02 · Word-only months are addressable.** Repository
  .storedMonths() (MonolithRepository.kt:2492-2503) unions
  task-months with declared-word months, so a month holding only
  a word still appears in ALL MONTHS and the word can be read
  back. My own suspicion, checked, dead.
- **A-03 · Day words: union, precedence, and the closed list.**
  allDayWords() (MonolithRepository.kt:2521-2524) unions
  day_notes ∪ DayWordStore prefs, prefs winning — a word survives
  its own drain. The print set is closed by construction
  (DayWordStore.kt:47-48: REST / ILL / FAMILY); TEST cannot be
  declared, drained, or printed.
- **A-04 · The frozen digest never shows a live frame.**
  MainActivity.kt:708-718: a freeze holder's items are passed, or
  — sealed with the mint still in flight — an empty list. The
  bare "HELD" frame (AirlockAndDisarmOverlays.kt:288-296) shows
  no stamp and no rows rather than one frame of the live list.
- **A-05 · The AS OF stamp.** mintFrozenDigest
  (MonolithViewModel.kt:789-801) samples frozenAtMillis at
  publish, after the snapshot read and a session-identity guard
  that drops stale mints. At the stamp's own minute precision the
  claim is true; rows additionally carry their own capture times
  (captureTimeLabel, AirlockAndDisarmOverlays.kt:214-220), so no
  row borrows the stamp's date.
- **A-06 · The digest's standing sentence.** "Anything newer
  waits until the session ends" — checked against the freeze
  watch (MonolithViewModel.kt:766-778): a remint fires only when
  a NEW session is active under the open sheet, i.e. after the
  freezing session has ended. Inside one session the snapshot
  never moves. The sentence holds.
- **A-07 · The sealed gate is at the repository, not the paint.**
  deleteCapturedNotification / clearAllCapturedNotifications
  refuse while a session is active and touch nothing
  (MonolithRepository.kt:1878-1890). In frozen mode CLEAR ALL is
  absent, not disabled (AirlockAndDisarmOverlays.kt:296-308), and
  frozen rows render no DEL (ibid. :372-382).
- **A-08 · Stored-row permanence.** No delete path exists for a
  COMPLETED row: the repository refuses at both guards
  (MonolithRepository.kt:377-384 and :2439-2445), the
  clear-completed chain is deleted, not unrendered (ibid.
  :2474-2480). The header's claim — "never auto-deleted, and
  nothing on this screen can change one" — is true of the code.
- **A-09 · Estimate and spent print as separate facts.**
  DrawerScreen.kt:246-258 prints ESTIMATE and SPENT as two plain
  lines — no delta, no percentage, no verdict. spentLabel
  (ibid. :140-150) formats the stored actualSecondsSpent
  faithfully (45 SEC · 24 MIN · 1H 05M verified against the
  function).
- **A-10 · FORGED as a record.** The badge reads DISTINCT
  forgedArtifactId from real session rows (MonolithDao.kt:68) —
  the stamp exists or it doesn't, and the print follows the
  stamp. What is false is the price tag beside it (DRW-01), not
  the record.
- **A-11 · The empty landing and the sealed posture.**
  shots/04_vault.png is true of the harness seed state: STORED,
  the search field, NOTHING STORED YET, today's heading with the
  three words offered, CURIOS — and no totals, counts, or
  streaks anywhere on the screen, matching the screen's stated
  law. While sealed: search input disabled, declaration hidden,
  cabinet SELECT disabled, rows and detail still read
  (DrawerScreen.kt — isSealed gates at the search field,
  DayHeading, and cabinet SELECT). Read-only furniture, as
  ruled. The search empty-print ("NOTHING STORED UNDER "X"",
  ibid. :562) is true of the searched space, which covers title,
  category, and the sub-steps blob (MonolithDao.kt:202), and the
  field's own placeholder scopes it to tasks.

---

*Filed by Hale. Charges stand on the cited lines; strike any of
them by showing the line says otherwise.*
