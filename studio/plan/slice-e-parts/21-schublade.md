# 21 — SCHUBLADE (Improvers, the drawer) · SLICE E BUILD-READY

## The collision sweep: Slice E × the Drawer build (Ruth's lane)

*Seated by Ilya (Improvers coordinator). My joint is only the
collision surface between Slice E's write set and the Drawer
build in flight in `builds/drawer` (MASTER-PLAN §7: the Drawer
stores COMPLETED TASKS only — "sessions are not Drawer content",
§7.8 / §5.7). I build nothing here; I prove overlaps or name
forks, with file evidence. Trunk is read-only to me; no gradle
was run.*

*Stamp of the evidence (all read this run, Oct 29, ~09:54–10:20
IST): trunk `Focus-app` is DB **v8** — `data/MonolithDatabase.kt:24`
(`version = 8`), `:41` `MIGRATION_7_8`, `:79` registers that one
migration only, destructive fallback gone. The Drawer lane's
tree (`builds/drawer/Focus-app`) is also v8 with the same single
migration. Slice E parts on disk at sweep time: 01-siegel,
02-riegel, 03-klinke, 04-reihe, 05-draht, 06-nocke, 07-rad,
08-zug-ii, 09-nabe, 10-horiz, 11-spule, 13-schlitten, 14-zettel,
15-saum, 19-linea, 24-fenja, 25-fach, 26-taste. Inbox at start:
empty (no unread letters; `read/` empty).*

---

## 0 — VERDICTS, UP FRONT

| # | Item | Verdict |
|---|---|---|
| 1 | E writes vs Drawer reads | **CLEAR** — no E write lands on any table/store the Drawer reads. Two named interactions, neither a collision (§1.3). |
| 2 | Migration sequencing / `day_notes` | **The collision as hypothesised does NOT exist** — no Drawer-train document promises `day_notes` 9→10; every one promises **8→9**. The real collision is the inverse: the 8→9 vehicle as build-ready (Rurik / ROUTINES-REBUILD-READY) is a **closed census with zero new tables and no `day_notes` in it**. **COLLISION, fork F-SCH-1** (§2.2), recommendation: amend 8→9 before it lands. |
| 3 | Receipt footer vs Drawer storage law | **CLEAR / CONSISTENT** — §5.7's own reconciliation carries it; the footer distinguishes the machine log (record store) from the study record (YPT), and the Drawer disowns sessions as *content*, never as *existence* (§3). Two standing conditions named. |
| 4 | Zettel's № store in Room | **BLESS Room — COLLISION on the home.** Room is the only lawful store (atomicity proof, §4.1), but three different homes for the same two facts are now filed (Zettel's new `receipt_store` table; Nocke's `guard_settings` columns; Reihe's `guard_settings` column), and Saum's filed `MIGRATION_9_10` object contains **none of them**. Fork F-SCH-2 (§4.2): one home, one unified 9→10 manifest; recommendation: `guard_settings` columns, Zettel's transaction semantics adopted whole. |

---

## 1 — ITEM 1: NO E WRITE LANDS ON A DRAWER-READ TABLE/STORE

### 1.1 The Drawer's read set (the target set)

From Brief B (`plan/DIGEST-AND-DRAWER-BUILD-BRIEFS.md`, Brief B)
as built in Ruth's lane — brief and tree agree:

| # | Store / table | Read | Evidence |
|---|---|---|---|
| R1 | `focus_tasks`, `status='COMPLETED'` rows only | `getStoredPage` (paged 50, 03:30-day bounds), `getStoredMonths`, `searchStoredTasks` (title/category/notes LIKE) | Brief B steps 2–3 ("the Drawer is status-filtered reads of `focus_tasks`. No archive table. No schema change. No migration."; "**No Drawer query touches `focus_sessions`**" — of `completedTasksSummary`: "It stays exactly where it is lawful — the finish receipt."); built: `builds/drawer/…/data/MonolithDao.kt:158–165`, `MonolithRepository.kt:1584–1606`, VM Drawer state `MonolithViewModel.kt:757ff` |
| R2 | SharedPreferences `monolith_day_words` (the non-Room day-word store) | `DayWordStore.all()` → `repository.dayWords()`; merged into headings + months index (union) | Brief B step 8 (capture ships 0-schema in a tiny non-Room store); built: `builds/drawer/…/data/DayWordStore.kt:22` (`PREFS_NAME = "monolith_day_words"`), `MonolithRepository.kt:1610–1616` |
| R3 | `focus_sessions` active row — **gating read only, no content** | `isSealed = activeSession?.isActive == true` disables Drawer search input while sealed | Brief B step 5; built: `builds/drawer/…/MainActivity.kt:418`, `DrawerScreen.kt:137,320` |

That is the whole set. The Drawer reads no session content, no
captures, no spends, no settings, no receipt state. (Grep of the
built `DrawerScreen.kt` for `№|lastReceipt|receipt|YOUR STUDY
RECORD` this run: **0 hits**.)

### 1.2 Slice E's write set (every write in the filed parts)

| # | Write | Target | Source |
|---|---|---|---|
| W1 | Session insert with `heldAppsAtSeal` written in the inserted row's constructor, counted once from `dao.getAllAppsSnapshot()` (LOCKED + PEEK_2M) inside `startSession` | `focus_sessions` (insert) + a **read** of `blocked_apps` | Emil, `slice-de-parts/emil.md` §3.2; Zettel `14-zettel.md` §3.1; capture site priced by Saum (COMMONS, 09:5x) at trunk `MonolithRepository.kt:950` |
| W2 | **Second** F15 column `heldSitesAtSeal` (Zettel's extension of F15, separable, flagged to Saum/Ilya) feeding the same provenance line | `focus_sessions` (insert) | Zettel §1.3 field table, §3.4 (see §4.3 note — his file's §3.4 is not yet on disk) |
| W3 | Completion flip — **pre-existing writer**, E only observes its StateFlow edge as E0 | `focus_sessions` (update, `updateSessionProgress`, trunk `MonolithRepository.kt:1110–1111` per Zettel §1.1) | SLICE-D-E §2-E4; Zettel §1.1; Linea `19-linea.md` §2.2 (verified there: the flip "touches no task row; task completion is a separate user act") |
| W4 | Annulment of a PROVING session at L+1500, pre-t0 only: `annulProvingSession = dao.deleteSessionById(id)` + stopShield | `focus_sessions` (delete, pre-completion rows only) | Klinke `03-klinke.md` (annulment row, DAO:75–76 exists today); SLICE-D-E D8/F7 |
| W5 | Re-anchor + re-sign of the session row at t=0 | `focus_sessions` (update) | SLICE-D-E §0 step 5, D8 |
| W6 | Release-fault persistence: `releaseFaultShield`, `releaseRetriesDone`, `releaseFaultedAtWallMillis` (+ heal writes clearing them) | `focus_sessions` (columns + updates) | Draht `05-draht.md` §5.2 |
| W7 | Ending discriminator + shield-death witness: `outcome`, `shieldLastBeatWallMillis`, `shieldGapSeen` | `focus_sessions` (columns + updates by every ending writer) | Nocke `06-nocke.md` §2.5 |
| W8 | Receipt claim/publish: № increment + lastReceipt latch/overwrite, one `withTransaction` | `guard_settings` singleton **or** new `receipt_store` table — **the Item-4 fork** | Nocke §3 (`claimComposite`); Reihe `04-reihe.md` §3 rule 3; Zettel §2.2–2.3 (`publishReceipt`) |
| W9 | Routine finalise bookings (Reihe's interface consumes them; writers are the routines rebuild's, not E's) | `focus_sessions` (update) | Reihe §3; ROUTINES-REBUILD-READY A2 |
| — | Everything else in E is **VM/view state or sound, no store**: PROVING controller StateFlow (D8), `PendingReceipt` (Zettel §1.1), `ReceiptState` StateFlow (Fen/Zettel — Schlitten `13-schlitten.md` §3.1: "In-memory only. No Room row, no DataStore, no SharedPreferences… this is a clause of the contract"), `SealShotState`/ghosts (Rad, Zug-II), hub light / orange exit (Nabe, Horiz), motion tokens (Spule), sound renders (Siegel, Riegel). E4's receipt assembly **reads** `intercept_logs` and `emergency_break_spends` (`bankedBreakSeconds`, Zettel §1.3) — reads, not writes. | — | as cited |

### 1.3 The intersection — and the two named interactions

**Intersection of {W1–W9} with {R1, R2} is empty.** Every E write
lands on `focus_sessions`, on the receipt-claim store (Item 4),
or on no store at all. Nothing in any filed E part writes
`focus_tasks`, writes the `monolith_day_words` prefs, or writes
any store the Drawer renders from. Session completion in E
provably does not complete a task (W3 / Linea §2.2), so E cannot
mint, alter, or delete a Drawer row even indirectly. **Item 1:
CLEAR, with evidence.**

Two interactions exist and are named so nobody later calls them
collisions:

- **(a) Linea's detention gate touches the task-write *path* —
  as a refusal, never a write.** Linea §3/T4: during RELEASING
  and RECEIPT (detention, ≤ ~E3130) the VM gate refuses
  `completeTaskAndAdvance` / `toggleTaskSubStep` / `deferTask`,
  with a test asserting "DAO row unchanged". Worst case for the
  Drawer: a completion attempted inside that ~3-second window
  lands seconds late, byte-identical. No suppression, no
  alteration, no loss. (Linea's own §2.2 also walks a Tasks-screen
  completion mid-detention as possible — her two passages are in
  tension over the gate's scope; under *either* reading the
  Drawer impact is nil. The tension is hers to close, not a
  Drawer fork.)
- **(b) R3's sealed gate rides the row E re-anchors.** The
  Drawer derives `isSealed` from the active-session flow; E's
  t=0 re-anchor (W5) and pre-t0 annulment delete (W4) change
  that row's lifecycle, never its `isActive` truth: a PROVING
  row is active and the Drawer correctly reads sealed; an
  annulled row is gone and no session is sealed. Gating stays
  honest in every E branch.

**Named risk (not a collision):** W1/W2's capture reads
`blocked_apps` via `getAllAppsSnapshot()` at insert. The Drawer
never reads `blocked_apps`; a slow snapshot costs the seal, not
the Drawer. No shared lock, table, or prefs file exists between
the two lanes — Ruth's build can merge in any order against E
on this axis.

---

## 2 — ITEM 2: MIGRATION SEQUENCING — `day_notes` WAS NEVER
## PROMISED 9→10; ITS 8→9 RIDE IS UNBOOKED

### 2.1 What each number already carries (the manifest)

| Migration | Owner / contents | Evidence |
|---|---|---|
| 7→8 | TOKEN-SPEC alone, CLOSED: `emergency_break_spends` table + destructive fallback deleted. **Landed** (trunk + drawer tree, v8). | REGISTER "Data"; MASTER-PLAN §5.3.8; trunk `MonolithDatabase.kt:24,41,79` |
| 8→9 | Routines rebuild. Rurik's census as amended by ROUTINES-REBUILD-READY A1 (Otis's tick stamp): **16 ADD COLUMNs** — `routines` 2, `focus_sessions` 8 (Sten 7 + Rurik `taskId`), `focus_tasks` 1 (Nadia), `guard_settings` 5 (Sten 4 + `lastRoutineTickMillis`). "**Zero new tables**", no new entity registered, migration writes no data. **Not yet in trunk.** | ROUTINES-REBUILD-READY §§1–3, 5 ("Zero new tables, permissions, alarms…"); Rurik `routines-rebuild-parts/rurik.md` §§1, 4, 6 |
| 9→10 | Plumbing batch (Register baseline): Rurik's `task_sub_steps` + `task_completion_events` + Otis's readiness log — "**Append/child tables only**; nothing existing changes meaning." F15's `heldAppsAtSeal` ADD COLUMN rides it by SLICE-D-E fork F15 (rec: adopt). Since then, more riders have filed onto the same number — see §4.2's manifest table. | REGISTER "Data"; Rurik §6; SLICE-D-E §5 item 5, §6-F15; RULINGS.md (migration sequencing line) |

### 2.2 `day_notes`: every promise says 8→9 — and 8→9 has no seat for it

The Drawer train's promises, exhaustively:

- Brief B step 8: capture ships in the non-Room store now;
  "**The drain waits: at the 8→9 window**, move the store into
  the specced `day_notes(dateIso TEXT PRIMARY KEY, word TEXT)`
  table (+~10 then, outside this price)."
- MASTER-PLAN §7.7 storage note: "the Drawer brief specs a
  one-table add **riding migration 8→9** (§9, charge B-31)."
- MASTER-PLAN §9, charge B-31: "the one-table add … **rides
  Migration 8→9**; closes when the column lands as specced."
  (B-31 says "column" for a table — wording sloppiness in the
  plan, noted; the object is Brief B's table.)
- RULINGS.md: "the drain into a Room `day_notes` table **waits
  for the 8→9 window**; the word list is REST / ILL / FAMILY
  and nothing else — never TEST."
- Ruth's covering letter (`mail/ren/0832__from-ruth__moment-walk-merged.md`):
  "the `day_notes` table still waits for **8→9**".
- The built lane itself: `builds/drawer/…/data/DayWordStore.kt`
  header — "The `day_notes` Room table **waits for the 8→9
  window**, which will drain this store."

**No document in the Drawer train promises, expects, or reserves
9→10 for `day_notes`.** The collision in my brief's hypothesis —
Drawer expecting "the NEXT migration number to be 9→10" for
`day_notes`, colliding with F15's claim on 9→10 — **does not
exist as stated. Proven by the six citations above.**

The real collision is the mirror image, and it is live:
**the 8→9 migration, in its build-ready form, cannot carry the
drain it was promised to.** Rurik's §1 census closes with "No
other schema change is in 8→9. In particular: **no new entity
is registered** in `@Database` (**zero new tables**…)";
ROUTINES-REBUILD-READY §5 repeats "Zero new tables";
`day_notes` is named in **neither** file (grep this run: 0 hits
for `day_notes` in ROUTINES-REBUILD-READY and in all four
routines-rebuild parts). Rurik's chain tests (his §5, tests
8a/8b) assert the v9 shape field-for-field — a `day_notes`
table appearing inside `MIGRATION_8_9` breaks the census, the
"zero new tables" sentence, and the test fixtures as filed;
its absence breaks B-31's closure condition ("closes when the
column lands as specced" — in 8→9).

**Severity, honestly:** sequencing/bookkeeping, **not data
loss**. Selbst's no-backfill law is already satisfied — capture
ships in prefs *now* (R2, built and verified above); the words
sit safely in `monolith_day_words` for as long as the drain
waits. What collides is two closed documents, not two stores.

### 2.3 FORK F-SCH-1 — where `day_notes` lands (for Ren/Voss)

- **(a) Amend 8→9 before it lands — RECOMMENDED.** One
  `CREATE TABLE day_notes (dateIso TEXT PRIMARY KEY, word TEXT)`
  inside Rurik's `MIGRATION_8_9` object + the `DayNote` entity
  registered + drain at first seed-sync/open after upgrade
  (Brief B's +~10, plus ≈+15 entity/DAO/drain plumbing Rurik
  didn't price). Census becomes "16 ADD COLUMNs + 1 CREATE
  TABLE"; the "zero new tables" sentences in
  ROUTINES-REBUILD-READY §§1/5 and Rurik §§1/6 are struck and
  re-filed; chain tests 8a/8b gain one assertion (table exists,
  empty). Why now: 8→9 is **not yet in trunk** (trunk is v8,
  one migration registered) — amending costs paper today and a
  migration number tomorrow. Every Drawer-side document
  already points here, and B-31 closes as written.
- **(b) Move the drain to 9→10.** Technically lawful — a CREATE
  TABLE is exactly what Register's 9→10 admits ("append/child
  tables only"), arguably a *cleaner* fit there than in the
  routines batch. Costs: B-31, §7.7, RULINGS.md and Brief B
  step 8 all get re-filed; the drain queues behind the most
  contested batch in the plan (§4.2 lists seven claimed
  riders); words keep waiting in prefs (safe, but the promise
  was 8→9). Take this only if Voss refuses to reopen the
  census in (a).
- **(c) Standalone 10→11.** Rejected: a whole migration
  ceremony and a third chain-test leg for a two-column table,
  bought with nothing. Mint it only if both batches have
  already landed without the table.

**Either way, one ruling must be written down before the
routines build merges:** after that merge, option (a) ceases
to exist and the fork collapses to (b)/(c) by default — a
decision made by silence.

---

## 3 — ITEM 3: THE RECEIPT FOOTER vs THE DRAWER'S
## STORAGE-NOT-REPORT-CARD LAW — CONSISTENT

**The footer** (MASTER-PLAN §5.7, the Numbered Single Strike):
`THE MACHINE KEEPS ITS OWN LOG · YOUR STUDY RECORD LIVES IN YPT`
(the brief to me quoted its second clause; the full string is
above, confirmed in Linea §5.1's slip-height table).

**The apparent tension:** the receipt is a *session* record —
provenance, deflections, №, a `lastReceipt` slot — while §7.2's
content law says the Drawer holds completed tasks only and
§7.8 says "Sessions, holds, and deflections — **not Drawer
content**." Does the slip imply a session record the Drawer
disowns?

**No — because §5.7 itself files the reconciliation, in its
own text.** The death-line bullet of §5.7 (emphasis mine):

> "the record's BROKEN booking is its permanent form (**§7's
> content law governs what the Drawer holds; the session
> record lives in the record store, §5.3.8's reconciliation
> applies by analogy: sessions are not Drawer content**)."

And §5.3.8's reconciliation, the one appealed to:

> "TOKEN-SPEC §4.5's spend rendering *as a tab row* is
> **SUPERSEDED by the Drawer ruling** (§9, C-6): **the Drawer
> stores finished tasks only**; what survives of §4.5 is the
> record itself — **the spend ledger row + the session row it
> joins** — and exactly one standing rendering anywhere in the
> app: the valve's SPENT state."

§7.8 completes the circuit from the Drawer's side: sessions
are not Drawer content, and "**the finish receipt remains
their moment**." Three sections, one doctrine:

1. A **record store** exists (the `focus_sessions` row, the
   spend ledger) — the machine's log. The footer's first
   clause *says so on the slip's face*; it does not smuggle
   the record in, it names it.
2. The **Drawer** is one surface over that store's *task*
   rows only. It disowns sessions as **content of its
   surface**, never as facts that exist. A disowned-content
   law is not a non-existence law, and nothing in §7 asks the
   receipt — a Deck surface — to pretend the session row
   isn't there.
3. The **study record** — hours, syllabus, the graded story
   of his prep — lives in YPT (and GMC), per his Oct 29 ruling
   ("this app is not a study tracker", §7 head). The footer's
   second clause is that ruling printed on the slip. The №
   counter and the single `lastReceipt` slot are machine-log
   artefacts, not study tracking: §5.7 designs them as "the
   only permanent artefact" *of a session*, single-slot,
   overwritten by the next session, proving existence without
   storing the slip — and Zettel §2.2 honours that literally
   (the slot holds a number and a pointer; payload is
   re-derived from the session row in the record store).

**Built-lane check:** the Drawer as built renders none of it
(§1.1's grep: 0 hits for receipt/№/footer in `DrawerScreen.kt`;
its queries never touch `focus_sessions` content). The receipt
lives on the Deck or nowhere. **Item 3: CLEAR / CONSISTENT.**

**Two standing conditions** (the consistency is load-bearing
on them; name them so a future builder can't quietly break
the reconciliation):

- **C-1:** the `lastReceipt` slot stays **single** — one №,
  one session pointer, overwritten at the next genuine
  completion (§5.7; Zettel §1.4/§2.2). The day a build keeps a
  *list* of receipts, the app has grown a session archive,
  the footer becomes false advertising ("nothing is stored"
  is its whole grammar), and §7.8's "the receipt remains
  their *moment*" dies with it.
- **C-2:** no receipt element (№, slip, footer, session
  provenance) ever renders **in the Drawer**. It hasn't
  (grep above); §7.2's "never the session's
  `completedTasksSummary` string" is the same wall seen from
  the other side. The receipt reads session facts on the
  Deck; the Drawer reads task rows in the Vault; the two
  pipelines never cross (§7.9's one-pipeline law).

---

## 4 — ITEM 4: ZETTEL'S № STORE — BLESS ROOM, VETO THE
## DUPLICATED HOME

### 4.1 Why Room is right (the blessing, with his evidence)

Zettel's ruling (`14-zettel.md` §0 point 2, §2.2): the №
counter and the `lastReceipt` slot live **in Room** — in his
filing, a new singleton-row table `receipt_store` — NOT
DataStore. His three reasons verify against the trunk and I
adopt them:

1. **Atomicity is the whole design.** Uma's spec (the source
   of §5.7, `wild-seats/14-uma.md`): "A single persisted
   integer counter, **incremented atomically** at — and only
   at — the moment a completed session's slip is rendered…
   the sequence has no gaps." Zettel §2.2: increment + slot
   overwrite + already-published latch "must be one act or a
   death between them mints a № with no slip, or a slip with
   a reused №. Room gives `withTransaction`; two stores
   cannot be transacted together, full stop." Reihe §3 rule 3
   independently presupposes the same: his
   `lastReceiptSessionId` is "written at receipt issuance **in
   the same transaction as the §5.7 sequence-counter
   increment**" — a sentence that is only buildable if the
   counter is Room-resident in the same database.
2. **The alternatives are dead on trunk evidence.**
   DataStore-Preferences is a declared dependency with **zero
   usages** in code (Zettel §2.2, grep-verified by him);
   SharedPreferences for session state was rejected in the
   same terms by Draht §5.2 ("a one-record prefs file
   contradicts the Room-only pattern"). I add the Drawer-side
   precision: the one prefs store that *does* exist —
   `monolith_day_words` (R2) — is authored words with **no
   atomicity coupling to any Room transaction**; it is a
   precedent for prefs-as-capture, not for prefs-as-counter.
   A № in prefs could increment without its latch, or latch
   without its slot, across exactly the process deaths §5.7's
   once-ever law exists to survive.
3. **Derivation is not available.** §5.3.8's token doctrine
   ("a fact, not a counter", derived from a ledger) does not
   transfer: there is no session-completion ledger to derive
   from (9→10's `task_completion_events` counts *task*
   completions — a different fact), and Zettel §2.5 kills the
   fallback derivation (COUNT of completed session rows) by
   name: history contains valve-ended, uncompleted and
   fabricated seed sessions, and annulment (W4) deletes rows —
   a counted № could repeat or skip. The specced artefact is
   a **stored, monotonic, never-backfilled scalar**. Room is
   where this app stores such things (the `guard_settings`
   singleton, trunk `MonolithModels.kt:504–508`, is the
   standing precedent; `emergency_break_spends` is the
   transactional one).

**Survival note, priced into the blessing:** Room residence
means the № dies with the database file (uninstall / data
clear → next completion is № 001) — Zettel §2.5 states this
honestly ("permanent *for the life of the install's
database*"), which matches §7.2's own two honest limits of
"permanent" for the Drawer. No store available to this app
does better; the claim as filed does not overclaim.

### 4.2 The collision: three homes, four manifests, one number

Blessing the *store kind* does not bless the current filings —
they disagree about **where in Room**, and about what else is
in Migration 9→10:

| Filing | № + lastReceipt home | Also claims on 9→10 |
|---|---|---|
| Zettel `14-zettel.md` §2.2 | **New table** `receipt_store` (singleton: `lastReceiptNo`, `lastReceiptSessionId`, published wall/mono, ended-at, routine label) | `heldSitesAtSeal` — a **second** `focus_sessions` column (§3.4 extension of F15) |
| Nocke `06-nocke.md` §§2.5, 3 | **`guard_settings` columns**: `receiptSequence` (the №), `lastCompositeSessionId` (composite latch), `lastReceiptOutcome`; increment inside `claimComposite`'s `withTransaction` on the singleton | `focus_sessions`: `outcome`, `shieldLastBeatWallMillis`, `shieldGapSeen` (W7) |
| Reihe `04-reihe.md` §3 rule 3 | **`guard_settings` column**: `lastReceiptSessionId`, riding 9→10 "in the same batch as emil's `heldAppsAtSeal` (F15)" | — (his interface itself is process-scoped, no store) |
| Saum `15-saum.md` §1 (COMMONS 09:5x) | **Absent.** His filed `MIGRATION_9_10` object is **four `focus_sessions` ADD COLUMNs only** (`heldAppsAtSeal` + Draht's three, W6), "zero new tables" | Excludes — by omission — the Register baseline tables (Rurik/Otis), Zettel's table + sites column, Nocke's six, Reihe's one |
| Register / Rurik baseline | — | `task_sub_steps` + `task_completion_events` + readiness log (CREATE TABLEs) |

Two distinct defects:

- **Duplicated fact, two candidate rows.** The № and the
  last-receipt pointer cannot live in both `receipt_store`
  and `guard_settings`. If two builders land both, the first
  publish transaction to run wins the truth and the second
  store becomes a lying copy — the exact one-fact-two-homes
  disease §7.9's one-pipeline law and TOKEN-SPEC's
  one-source surface contract both exist to kill. Nocke's and
  Zettel's **increment anchors also differ** (Nocke increments
  at *claim*, before any animation; Zettel at *publish*/
  issuance, §1.1's two-phase contract) — same counter,
  different birthdays; a merged build must pick one anchor or
  the № is consumed on a different event than the slip it
  numbers.
- **Saum's object cannot be THE migration.** His four-column
  object is correctly formed and correctly sequenced ("9→10
  composes on Rurik's `MIGRATION_8_9` name, never instead of
  it"), but if it lands verbatim as version 10, every other
  rider in the table above is pushed to a 10→11 none of them
  filed — the same failure mode as F-SCH-1, one number later.

### 4.3 FORK F-SCH-2 — one home, one manifest (for Ren/Voss)

**Recommendation — adopt Zettel's semantics, Nocke/Reihe's
address:**

1. **Home: `guard_settings` columns** — `receiptSequence`
   (№, Nocke's name), `lastReceiptSessionId` (Reihe's name),
   `lastCompositeSessionId` (Nocke's composite latch — a
   *different* latch from the publish latch, and both are
   real: the composite fires once per session, the publish
   happens once per №). Three ALTERs on the existing
   singleton row; no new table, no new entity registration,
   and the row that already absorbs every global scalar in
   8→9 (five columns there, incl. the tick stamp) absorbs
   three more. Zettel's `receipt_store` table **dies as an
   address**; everything else in his §2 survives the move
   unchanged — his publish transaction, his
   already-published latch, his rehydration rules, his
   never-backfill law are address-independent, and his
   extra slot fields (published wall/mono, ended-at, routine
   label) either become three more `guard_settings` columns
   at the same trivial price or — my preference — **die**:
   the slot's payload is re-derived from the session row at
   rehydration (his own §2.2 principle: "the slot holds a
   number and a pointer"), and the three publish-time
   samples are the only payload that cannot be re-derived;
   if composition truly needs them, price them as columns,
   not as a table.
   *Why not Zettel's table:* his strongest argument for it is
   Register purity ("a CREATE TABLE fits 'append/child
   tables only' cleanly, unlike the ADD COLUMNs") — true of
   the Register baseline, but F15 already broke that purity
   *by ruling* (F15 rec: adopt; Saum's object is all
   columns), so purity no longer decides anything here.
   Against the table: two singleton-row tables for global
   scalars is a second pattern founded for one record, and
   two of the three filings already converge on
   `guard_settings`.
2. **Anchor: Zettel's publish.** The № increments inside
   `publishReceipt` at issuance (his §1.1/§2.3), not at
   Nocke's claim. Nocke's claim keeps its own latch
   (`lastCompositeSessionId`) and consumes no number — a
   claimed-but-never-issued composite (possible only if
   issuance's ≤E1800 guarantee itself fails) then leaves no
   gap in the sequence, which is Uma's "no gaps, no excuses"
   stated as a test.
3. **One manifest, one owner.** Saum (the seam seat) re-files
   `MIGRATION_9_10` as the **single** object containing, in
   order: Register baseline tables (Rurik/Otis) ·
   `heldAppsAtSeal` + `heldSitesAtSeal` (F15 as extended) ·
   Draht's three fault columns · Nocke's three session
   columns · the three `guard_settings` receipt columns
   above. Nobody lands a partial 9→10; the version bump to
   10 happens exactly once, with chain tests (v8→v10,
   v9→v10) asserting every rider's defaults.

**Migration consequence, priced:** as recommended, the №
store costs **+3 ALTERs + 3 entity fields + DAO accessors
(≈+6)** inside the already-ceremonied 9→10 — no extra
version bump, no chain leg, and **zero Drawer impact** (the
Drawer reads neither `guard_settings` nor any receipt store;
§1.1). The publish/claim machinery itself is already priced
by its owners (Zettel ≈+150 net production for the contract;
Nocke inside his ≈+395). **Vetoed forms, by name:** (i) the
№ or slot in DataStore/SharedPreferences (§4.1.2); (ii) a
standalone Migration 10→11 for the counter alone while the
9→10 batch is still open — a fourth ceremony for one integer;
(iii) any form that stores slip *payloads* as a list or
archive (breaks Item 3's condition C-1 and §5.7's
single-slot law); (iv) deriving the № from row counts
(§4.1.3).

**Filing defect, named for Ilya/Zettel:** `14-zettel.md` as
on disk at sweep time **ends mid-sentence at §3.3 (439
lines)** — §§3.4 (`heldSitesAtSeal` detail), 4 and 5 are not
yet on disk. My blessing covers his §2 as filed (complete);
the `heldSitesAtSeal` rider enters F-SCH-2's manifest on the
strength of his §1.3 field table only, and Zettel owes the
completed file before the manifest is frozen. Similarly,
Nocke §2.5 carries a stray claim that the routines lane's
8→9 census "incl. `heldAppsAtSeal`" — false against Rurik's
census (the column is 9→10 in every other filing, Saum's
included); harmless to his own DDL, but it should be struck
before a builder reads it as a capture already landed.

---

## 5 — RISK REGISTER (named, owned)

- **R-SCH-1 · The 8→9 window closes silently.** F-SCH-1(a)
  exists only until the routines build merges. If it merges
  first, `day_notes` falls to F-SCH-1(b) by default and B-31
  needs re-filing. Owner: Voss/Ren, before the routines merge.
- **R-SCH-2 · Partial 9→10 landings.** Any single rider
  landing version 10 alone (Saum's object is the ready one)
  strands the rest at 10→11. Owner: Saum, per F-SCH-2.3.
- **R-SCH-3 · Duplicate № homes.** If Zettel's table and
  Nocke's columns both land, the counter forks silently and
  the first symptom is a repeated № on a slip — a printed
  lie of exactly the class §5.7 exists to prevent. Owner:
  whoever merges the 9→10 manifest.
- **R-SCH-4 · Receipt creep into the Drawer.** Item 3's
  consistency is conditional (C-1/C-2). Any future surface
  that lists receipts re-opens Item 3 as a collision.
  Standing watch, no owner — flagged here so the condition
  survives this file.

## NEXT

- **(a) What I want next:** the F-SCH-1 ruling (Voss/Ren)
  before the routines merge, and a re-sweep of the unified
  `MIGRATION_9_10` manifest the hour Saum re-files it —
  same joint, verdict updated in place.
- **(b) What I could do unassigned:** the same write/read
  intersection sweep for Slice D's in-flight slide build
  against the Drawer (heldApps capture currently lives in
  `remember{}` there, per Emil's slide review — a Drawer-
  adjacent truth risk of the same family), and an audit of
  every "rides Migration *n*→*n+1*" sentence in `plan/`
  against the two closed censuses (8→9, 9→10) — F-SCH-1
  suggests the pattern has other instances.

— **Schublade, Improvers · the drawer**
