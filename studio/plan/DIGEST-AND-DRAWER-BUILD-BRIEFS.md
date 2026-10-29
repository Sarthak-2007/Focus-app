# DIGEST + DRAWER — BUILD BRIEFS

*Ruth, coordinator, Humans — 29 Oct 2026. Design + file-level spec only;
Ren builds. Every claim below is read from the app tree as it stands
this morning (paths + line numbers are the evidence). Prices are net
line counts against the current files.*

**Prices up top (as amended by the Moment Walk appendix — all 15
Humans seats walked these briefs against the actual moments, and the
amendments below are adopted into the steps, not left as
commentary):**
- **Brief A — Frozen Digest: ≈ +135 lines across 5 files, 0 schema,
  0 migration. Risk LOW.** (Was +89 as first filed; the walk added
  the session-keyed freeze, per-row capture times, a live-mode
  per-item delete, and a pending-frame guard.)
- **Brief B — The Drawer: ≈ +619 / −306 lines across 7 files (net
  ≈ +313), 0 schema, 0 migration. Risk MEDIUM-LOW.** (Was +490/−290;
  the walk added paged search, permanence guards, the seed fix, the
  03:30 day boundary, matched-line search rows, and day-word capture.)

Open flags are collected at the end. Nothing in either brief needs a
word from Sarthak: A executes his full-peek verdict's own terms; B
executes Sana's ratified Drawer ruling section by section. (One
priced option — Wieder's Last-Finished Line — is attached to FLAG
B-3 for Ren to put in front of him, because it builds a surface no
ruling named.)

---

## BRIEF A — THE FROZEN DIGEST

### The charge

His verdict (Oct 29, 05:48): the HELD digest may be **read**
mid-session, bodies included; reading is free, **acting stays
sealed**. The shipped digest betrays both halves of that sentence:

1. **It is a feed.** The sheet renders the live Room flow, so captures
   arriving while he reads append at the top under his eyes — the
   5–10 minutes of re-check residue Probe measured in the wave.
2. **It offers an act.** CLEAR ALL sits in the header, live, while
   sealed — and the chain behind it deletes *every* capture in the
   table, with no session check anywhere from chip to SQL.

The fix: while sealed, the digest is a **snapshot, not a feed** —
taken at the open, stamped, unmoving, and incapable of any act.

### What the code does today (evidence)

- **The sheet:** `CapturedNotificationsDigestSheet`,
  `ui/components/AirlockAndDisarmOverlays.kt:205–311`. Stateless: it
  receives `notifications: List<CapturedNotificationEntity>`,
  `onClearAll`, `onDismiss`. Header (L242–248) prints
  `HELD NOTIFICATIONS • {notifications.size}` — a **live count**.
  The CLEAR ALL chip (L250–252) is composed unconditionally.
  Rows carry no tap affordance already (L276–296) — that half of
  full-peek is already honest.
- **The feed path:** `service/MonolithNotificationListener.kt:51`
  inserts captures straight into the DAO while sessions run →
  `data/MonolithDao.kt:89–90` (`Flow`, `ORDER BY timestampMillis DESC
  LIMIT 100`) → `MonolithRepository.kt:59` → `MonolithViewModel.kt:275`
  (`StateFlow`, `WhileSubscribed(5000)`) → MainActivity passes the
  collected list straight into the sheet (`MainActivity.kt:~514–519`).
  Open sheet + new capture = the list changes mid-read. Proven feed.
- **The clear path:** sheet chip → `MainActivity.kt:517`
  (`viewModel::clearAllCapturedNotifications`) →
  `MonolithViewModel.kt:481–486` → `MonolithRepository.kt:1081–1083` →
  `MonolithDao.kt:98–99`: `DELETE FROM captured_notifications`. **No
  sealed check at any layer.** It also wipes captures from *earlier*
  sessions, not just this one.
- **Entry points while sealed:** Deck (`MainActivity.kt:331`) and the
  unified status drawer (`MainActivity.kt:264`) both call
  `viewModel.toggleNotificationDigestSheet(true)` (`MonolithViewModel.kt:476–479`)
  — a bare visibility flag; no state is captured at open.
- **Per-item delete:** `MonolithViewModel.kt:488` →
  `MonolithRepository.kt:1077` → `MonolithDao.kt:95–96`. **No UI
  caller exists today** — unreachable, but ungated; the same
  repository gate below covers it so no future surface inherits a
  live wire.
- **The sealed idiom:** `activeSession.value?.isActive == true`
  (VM, passim), and the repository's one-shot
  `dao.getActiveSessionSnapshot()` (precedent:
  `MonolithRepository.kt:604, 1055, 1086`).
  `FocusSessionEntity.isActive` is `MonolithModels.kt:267`.

### The change — file by file

1. **`data/MonolithDao.kt` (+4).** One-shot snapshot query beside
   L89 — same SQL, non-Flow:
   `suspend fun getCapturedNotificationsSnapshot(): List<CapturedNotificationEntity>`.
2. **`data/MonolithRepository.kt` (+12).** Passthrough
   `capturedNotificationsSnapshot()`. **Gate both delete paths:**
   `clearAllCapturedNotifications(): Boolean` and
   `deleteCapturedNotification(id): Boolean` return `false` without
   touching the DAO when
   `dao.getActiveSessionSnapshot()?.isActive == true`; otherwise run
   and return `true`. The repository is the binding gate — a hidden
   chip was never the requirement; a dead chain is.
3. **`ui/MonolithViewModel.kt` (+~56, as amended by the Moment
   Walk).** A frozen holder:
   `data class FrozenDigest(items: List<CapturedNotificationEntity>, frozenAtMillis: Long, sessionId: Int)`
   in a `MutableStateFlow<FrozenDigest?>`. **The freeze is keyed to
   the session, not to the open** (Muede's 19:15 walk + Panik's
   Saturday walk, independently: a per-open freeze turns dismiss +
   reopen into a pull-to-refresh loop — and on Saturday evening,
   into a serial harvest of answer keys, a smuggled override of
   HELD-till-Sunday):
   - In `toggleNotificationDigestSheet(true)`: if sealed and the
     holder's `sessionId` equals the active session's id, **reuse
     the holder** — same stamp, same rows; reopening while sealed
     provably pays nothing. If sealed and the holder is null or
     stale, launch the one-shot snapshot and mint, stamped with
     `System.currentTimeMillis()`. If unsealed, holder = null
     (live mode).
   - In `toggleNotificationDigestSheet(false)`: clear the holder
     **only when no session is active**. A dismiss while sealed
     keeps it for the session's next open.
   - **A collector on `activeSession` while the sheet is visible**
     (Dopa's 19:45 walk + Barriere's boundary walk): if a session
     becomes active whose id differs from the holder's — a routine
     arming under an open live sheet, or a new session arming under
     a stale frozen one — snapshot and (re)mint the holder for that
     session, stamped at the arm moment. Transitions to *inactive*
     are ignored: the latch-on-end decision below survives
     byte-for-byte. Same-session phase flips (Safe Break → next
     block) share the session id and never refreeze (Bus's walk).
   - **Pending-frame guard (Quaere's + Bus's note, ~+4):** while
     sealed and the holder has not yet landed, the sheet renders
     the frozen frame with no rows — never one frame of the live
     list. (Any tap in that frame would die at the repository gate
     anyway; this removes the flash, not a hole.)
   - `clearAllCapturedNotifications()`: play the latch sound **only**
     when the repository returns `true`. A sealed attempt is silent —
     no toast, no new copy; silence is the sealed register, and in
     practice the chip is not even composed (step 5).
4. **`MainActivity.kt` (+~5).** Collect the frozen holder; pass the
   sheet `frozenAtMillis = frozen?.frozenAtMillis` and
   `notifications = frozen?.items ?: capturedNotifications`.
5. **`ui/components/AirlockAndDisarmOverlays.kt` (+~56, as amended
   by the Moment Walk).** One new param, `frozenAtMillis: Long? =
   null`. Two render modes:
   - **Frozen** (`frozenAtMillis != null`): header row prints
     `HELD · AS OF {HH:mm}` — time formatted from `frozenAtMillis`
     (`SimpleDateFormat("HH:mm")`; the Deck's finish-time formatting
     is the in-app precedent). **No count** (the filed header carries
     none; a count is a live-shaped number). **The CLEAR ALL chip is
     not composed at all** — absent, not disabled, not greyed.
     Directly under the header, one line, Atkinson 13sp,
     `onSurfaceVariant` (copy as amended by Freund's family-worry
     walk — the line must bound "everything" to the stamp, say what
     happens to newer captures, and name the horizon; one string,
     0 net lines):
     `Everything here is as of the time above. Nothing here opens. Anything newer waits until the session ends.`
     Then the divider, then the snapshot rows — **with one change
     (Probe's 06:40 walk, ~+8): each frozen row's sender line carries
     the capture's own stored time** —
     `{appName} • {senderTitle} • {HH:mm}` when the capture is from
     today, `{appName} • {senderTitle} • {d MMM} · {HH:mm}` when it
     is not. The table is never cleared between sessions in practice,
     so yesterday's held rows legitimately sit in this morning's
     snapshot; without their own times, `AS OF` dates the snapshot
     while the rows pretend to be fresh, and the single-fact glance
     ("did the expected message arrive?") is unanswerable. Ruth's
     call, recorded: the snapshot query is **not** session-bounded —
     undelivered held items from earlier sessions belong in the
     digest; their printed times carry the truth. Empty snapshot in
     frozen mode prints `Nothing has been held.` (the shipped
     "Zero notifications intercepted during focus." is a count-word
     for absence — Probe's flag; the live mode keeps the shipped
     line untouched, per the byte-identical law below).
   - **Unfrozen:** byte-identical to today's sheet — live list, live
     count, CLEAR ALL present.
   `CLOSE DIGEST` is unchanged in both modes; closing is not an act
   on the held world.

6. **Live-mode per-item delete (Dopa's 08:00 walk, +~20).** The
   unsealed CLEAR ALL is global and irreversible — it wipes every
   capture ever held, including ones he has never read — and the
   per-item delete chain (VM:488 → Repository:1077 → Dao:95) exists
   with no UI caller. At the day's first triage that leaves exactly
   two options: keep all ~100 rows, or destroy a wanted capture
   with the rest. Wire the existing chain into the **live mode
   only**: rows gain a delete affordance in unfrozen mode
   (AirlockAndDisarmOverlays +~18, MainActivity `onDeleteItem`
   wiring +2, VM 0). Frozen rows keep no affordance of any kind;
   the repository gate already refuses the call while sealed.

### Freeze semantics (Ruth's calls, as amended by the Moment Walk)

- **The freeze is keyed to the session** (step 3, as amended). One
  snapshot per session, minted at the first sealed open or at the
  arm moment if a routine seals under an open sheet; reopens inside
  the same session reuse it; a dismiss while sealed keeps it; it
  clears on a dismiss while unsealed.
- **The freeze latches past the session's end.** If the session
  ends while the sheet is up, the open sheet keeps its snapshot
  until dismissed — `AS OF` stays true for the life of that reading
  (Barriere's clock-runs-out walk: PASS, verified against the
  ticker — the sheet is never touched at session end). Dismiss +
  reopen after the session = live mode, CLEAR ALL back.
- **A Safe Break → block flip is not a session end** (Bus's walk):
  same session id, holder untouched, nothing refreezes.
- Captures arriving during a frozen read are **not lost**: the
  listener keeps writing to Room; they surface at the next open
  after the session. (This is why the wave's *It Will Keep*
  companion needs no work in this brief — its hard precondition,
  provable persistence, is already true in the shipped code. The
  *Landing Line* companion is a Deck-side item and is out of scope
  here.)
- **CORRECTION to the first filing:** this brief originally declared
  "arming under the sheet is not reachable — arming lives on the
  Deck." **That is false**, as Dopa's and Barriere's walks proved
  from the tree: `RoutineAlarmReceiver` arms sessions on
  AlarmManager boundaries with no UI involved (seeded routines
  09:00 Mon–Fri, 22:30 daily). Sessions begin under open sheets in
  both directions. The step-3 collector exists precisely because
  the non-goal was wrong; the claim is struck, not footnoted.
### Acceptance

1. Seal a session; open the digest; trigger captures (the VM's
   `simulateCapturedNotification` suffices) → the open sheet does not
   change; header reads `HELD · AS OF {open time}`; no CLEAR ALL
   exists in the sheet.
2. Call the clear path while sealed (any entry) → repository returns
   `false`; `captured_notifications` row count unchanged.
3. Dismiss, end the session, reopen → live list including the
   captures that arrived during the frozen read; CLEAR ALL present
   and functional.
4. Unsealed open → today's sheet, unchanged in every pixel class.
5. **(Walk-added)** Dismiss and reopen inside the same session → the
   identical snapshot, the identical stamp. New captures during the
   interval do not appear. (Muede / Panik.)
6. **(Walk-added)** Leave the sheet open across a routine boundary
   (22:30 suffices) → at the arm moment the sheet freezes for the
   new session: CLEAR ALL vanishes, the header stamps the arm time,
   and the rows are the new session's snapshot. An open frozen sheet
   whose session ends and is replaced by a routine session refreezes
   the same way. (Dopa / Barriere.)
7. **(Walk-added)** A frozen row from yesterday prints yesterday's
   date beside its time; today's rows print time only. (Probe.)
8. **(Walk-added)** In live mode, a single capture can be deleted
   from its row; in frozen mode no row carries any affordance.
   (Dopa.)

### Price

| File | Δ |
|---|---|
| MonolithDao.kt | +4 |
| MonolithRepository.kt | +12 |
| MonolithViewModel.kt | +56 |
| MainActivity.kt | +7 |
| AirlockAndDisarmOverlays.kt | +56 |
| **Total** | **≈ +135, risk LOW** |

Only risk is state plumbing, and it is deliberately one-directional:
the frozen path uses a one-shot read and never touches the
`WhileSubscribed` flow the live path depends on. The walk's
amendments add a session collector to the VM; it reads
`activeSession` and writes only the holder.

---

## BRIEF B — THE DRAWER

### Authority

`plan/GUARD-RULING-AND-DRAWER.md` §2, ratified. The kept Ledger stop
opens **the Drawer**: permanent storage for finished tasks, read back
by month and by search. No totals, no grades, no streaks, no
percentages, no summons. Section numbers below are the ruling's.

### What exists today (evidence)

**The stored record already exists — as live task rows.**
`FocusTaskEntity` (`data/MonolithModels.kt:94–104`) carries the
ruling's stored set field for field: `id`, `title`,
`notesOrSubsteps` (substeps serialized; `parseSubSteps` at L124),
`estimatedMinutes`, `actualSecondsSpent`, `categoryTag`, `status`,
`completedAtMillis`, `completedInSessionId`.

- **Completion writes it.** `MonolithRepository.completeTaskAndAdvance`
  (L1248) stamps `status = COMPLETED`, `completedAtMillis = nowWall`,
  `completedInSessionId` (L1266–1275). `actualSecondsSpent` accrues
  honestly, one second per tick on the ACTIVE task, via
  `incrementActiveTaskSecond` (L1158–1166).
- **Reopen already behaves as ruled.** The same function (L1252–1263)
  toggles a COMPLETED task back to QUEUED/ACTIVE with
  `completedAtMillis = 0` — the row stops matching `status =
  'COMPLETED'` and leaves any Drawer view; re-completion re-stamps a
  fresh timestamp. (One behaviour to print in the build notes:
  reopening does **not** reset `actualSecondsSpent`, so a
  re-completed task's stored "spent" is its lifetime seconds across
  both runs. That is the truth of the task; the Drawer prints the
  row, so it prints that.)
- **Therefore: the Drawer is status-filtered reads of `focus_tasks`.
  No archive table. No schema change. No migration.**
- **The string that must never feed it:**
  `FocusSessionEntity.completedTasksSummary` (`MonolithModels.kt:272`),
  appended at completion (`MonolithRepository.kt:1277–1289`). It
  stays exactly where it is lawful — the finish receipt. No Drawer
  query touches `focus_sessions`.
- **The violation to kill — the clear chain.**
  `MonolithDao.kt:123–124` (`DELETE FROM focus_tasks WHERE status =
  'COMPLETED'`) ← `MonolithRepository.kt:1415` ←
  `MonolithViewModel.kt:664` ← wired as `onClearCompleted` into the
  Tasks screen (`MainActivity.kt:392`; param declared
  `FocusSequencerScreen.kt:84`). Precise state: the callback is
  invoked **nowhere inside the Tasks screen today** — no rendered
  control exists — but the chain is live code, one chip away from
  resurfacing, and the ruling ("No completed-task Clear control
  appears in Tasks or the Drawer"; "retention is permanent") requires
  the chain **removed**, not merely unrendered.
- **The tally beside it.** The Tasks queue header prints
  `DONE {completedTasks.size}` (`FocusSequencerScreen.kt:254–258`) —
  a count of stored tasks with no lawful action attached. It dies
  with the chain (Ruth's call under the ruling's no-tally law;
  flagged to Ren in the covering letter).
- **The current VAULT face is tracker theatre, end to end.**
  `ui/screens/CurioVaultScreen.kt` (274 lines): counter tiles
  FOCUSED MIN / DEFLECTED / SESSIONS / TOWER BRICKS (L77–153 —
  including `sessionHistory.size.coerceAtLeast(4)`, a *padded*
  count), the Streak Tower (L155–174), 7-day faders (L177–205), and
  session receipts under a `TOTAL n` header with `+{mins}m` deltas
  (L208–271). Every section is on the ruling's kill list (aggregates
  die, the week face dies, session receipts are not Drawer content).
  **The file is replaced, not edited.**
- **DB state.** Version 7 (`data/MonolithDatabase.kt:21`) with
  `fallbackToDestructiveMigration(true)` (L49). TOKEN-SPEC owns
  `Migration(7, 8)`; two 7→8 migrations must never exist
  (TOKEN-SPEC §6). **The Drawer core needs no migration and must
  not create one.** One correction from Tief's permanence walk: the
  first filing deferred the fallback's removal to TOKEN-SPEC with
  the rest — over-cautious. Striking `fallbackToDestructiveMigration(true)`
  creates no migration and collides with nothing; leaving it armed
  under years of version bumps risks the whole `focus_tasks` table.
  **It is struck in this build (−1 line, `MonolithDatabase.kt`).**
  TOKEN-SPEC still owns the 7→8 migration itself.
- **A second permanence hole the first filing missed (Tief's walk,
  verified in the tree).** `ensureSeededAndSyncPackages`
  (`MonolithRepository.kt:403–410`, run on ViewModel init) dedups
  tasks by `title.lowercase()|status` and deletes the later rows
  through the unguarded `dao.deleteTaskById`. Repeat-titled
  completions are the normal shape of a drop year — under the
  shipped code the Drawer would silently prune them at the next
  launch, keeping only the first of any title. The kill list in
  this brief was incomplete without it; step 3 carries the guard.
- **Date notes (REST / ILL / FAMILY) have no storage.** No entity,
  column or table anywhere in the data layer holds a declared day
  word today. The ruling *permits* printing it beside a date heading;
  it does not require it for the Drawer to be lawful. See step 7.

### The change — file by file

1. **`data/MonolithModels.kt` (+5).** One projection POJO:
   `data class StoredMonth(val year: Int, val month: Int)`.
   `FocusTaskEntity` is **unchanged**.
2. **`data/MonolithDao.kt` (+~30 / −2).**
   - `getStoredPage(fromMillis, toMillis, offset)`:
     `SELECT * FROM focus_tasks WHERE status='COMPLETED' AND
     completedAtMillis >= :fromMillis AND completedAtMillis <
     :toMillis ORDER BY completedAtMillis DESC, id DESC LIMIT 50
     OFFSET :offset`. (The `id DESC` tiebreak is load-bearing: two
     tasks can share a millisecond, and OFFSET paging without a
     total order can skip or repeat a row at a page boundary.)
   - `getStoredMonths()`: `SELECT CAST(strftime('%Y',
     completedAtMillis/1000, 'unixepoch', 'localtime') AS INTEGER)
     AS year, CAST(strftime('%m', completedAtMillis/1000,
     'unixepoch', 'localtime') AS INTEGER) AS month FROM focus_tasks
     WHERE status='COMPLETED' AND completedAtMillis > 0 GROUP BY
     year, month ORDER BY year DESC, month DESC` →
     `List<StoredMonth>`. An empty result *is* the global empty
     state; no separate count query exists anywhere in this build.
   - `searchStoredTasks(q, offset)`: `SELECT * FROM focus_tasks WHERE
     status='COMPLETED' AND completedAtMillis > 0 AND (title LIKE
     '%' || :q || '%' OR categoryTag LIKE '%' || :q || '%' OR
     notesOrSubsteps LIKE '%' || :q || '%') ORDER BY
     completedAtMillis DESC, id DESC LIMIT 50 OFFSET :offset`.
     Search reads the serialized notes column, which is exactly the
     stored substep text — the ruling's "search covers title,
     category, notes and substep text" is satisfied by the storage
     that already exists. **Paged, as amended by Tief's years-later
     walk:** the first filing's `LIMIT 200` with no paging silently
     amputated the *oldest* matches — at years scale, exactly the
     era he comes back for. Search pages at 50 with the same
     `EARLIER` affordance as month views (step 7); there is no cap
     a match can fall off.
   - **DELETE** `clearCompletedTasks` (L123–124).
3. **`data/MonolithRepository.kt` (+~62 / −18, as amended by the
   Moment Walk).** Month-bounds helper (`java.util.Calendar`, local
   timezone: first instant of month → first instant of next month —
   bounds computed on the **03:30 day**, step 7's day rule) +
   passthroughs `storedPage(year, month, offset)`, `storedMonths()`,
   `searchStoredTasks(q, offset)`.
   **DELETE** `clearCompletedTasks` (L1415–1417).
   **Permanence guards (Tief's walk, +~6):** the dedup pass in
   `ensureSeededAndSyncPackages` (L403–410) skips COMPLETED rows
   entirely, and `deleteTaskById` (L1387) refuses a COMPLETED row —
   after this build, no repository path deletes a stored task; the
   Tasks queue's delete-with-undo reaches uncompleted rows only
   (FLAG B-2's boundary, now enforced in code, not just in UI
   reachability).
   **Seed fix (Neu's first-week walk, −15):** delete the seeded
   COMPLETED demo task ("Review Spaced-Repetition Deck (30 Cards)",
   seed block ~L385–399) from `ensureSeededAndSyncPackages`'s seed
   list. It passes every Drawer filter, so a fresh install's Drawer
   opens on a finished task he never did, timestamped an hour
   before install — the Drawer's promise broken on its first
   rendering, `NOTHING STORED YET` unreachable, and (with the clear
   chain dead and the detail read-only) no shipped path able to
   remove it, ever. FLAG B-1 is therefore not deferred: it is
   decided here. The QUEUED/ACTIVE demo seeds stay; the wider
   demo-data question stays open where it was. (An optional one-time
   scrub for already-seeded test installs, ~+6, must not gate this.)
4. **`ui/MonolithViewModel.kt` (+~92 / −7, as amended).** **DELETE**
   `clearCompletedTasks` (L664–669). ADD Drawer state:
   `storedMonths: StateFlow<List<StoredMonth>>` (refreshed when the
   VAULT destination opens and whenever the tasks flow emits),
   `drawerMonth` (default: current local year/month),
   `drawerRows: List<FocusTaskEntity>` (accumulated pages) +
   `loadEarlier()` (next offset; a short page ends paging),
   `drawerQuery` + `drawerResults` (search runs against the paged
   `searchStoredTasks`, all years, with its own `loadEarlier()` —
   Tief's amendment, +~11 of this step). Month switching resets the
   accumulated list and loads page 0. Day grouping keys are computed
   on the **03:30 day** (step 7). The screen derives `isSealed`
   from the existing `activeSession` flow at the call site (step 5).
5. **`MainActivity.kt` (~−12 net).** The VAULT branch (L405–419) is
   rewired to the new screen: drop `totalFocusedMinutes`,
   `totalInterceptions`, `selectedArtifact`, `sessionHistory`,
   `interceptLogs`, `sevenDayStats`, `harmonicSynthUnlocked`,
   `onSelectArtifact`, `onPlayHarmonicChord`; pass the Drawer state +
   callbacks + `isSealed = (activeSession?.isActive == true)`.
   Remove the `onClearCompleted` wiring (L392). Nothing else in
   MainActivity changes — those flows keep feeding Deck/overlays as
   today; they simply stop feeding the record stop.
6. **`ui/screens/FocusSequencerScreen.kt` (−6).** Remove the
   `onClearCompleted` param (L84) and the `DONE n` tally
   (L254–258 → title-only `QUEUE` header).
7. **`ui/screens/CurioVaultScreen.kt` → REPLACED by the Drawer
   screen** (new file `ui/screens/DrawerScreen.kt`, or the same file
   rewritten — Ren's choice; **keep `testTag("curio_vault_screen")`
   on the root**: the screenshot harness navigates by destination,
   not tag (`ScreenshotHarnessTest.kt:99, 155`), so 04_vault/n04_vault
   simply become Drawer shots, and keeping the tag protects any
   tag-based lookup at zero cost). Structure, all copy exact:
   - **Header:** plate `STORED` (Anonymous Pro), and one search
     field whose hint is exactly `TYPE TO FIND A TASK`. While
     sealed the field is composed but input-disabled — reading is
     free, typing is an edit, edits are inert while sealed (ruling
     §2, while-sealed clause).
   - **The day rule (Kaeufer's midnight walk, +~22 across steps
     2/3/4/7):** his day starts at **03:30**, not midnight — he wakes
     at 03:30. Every Drawer derivation of "which day / which month"
     uses the 03:30 boundary: the DAO months index (`strftime` with
     a `'-210 minutes'` modifier), the repository month bounds, the
     VM group keys, and every heading the screen prints (one shared
     helper). Without it, a task finished at 00:02 on a test-eve
     night files under the *next* day and the *next* month — the
     lived Saturday shows 9 of its 10 tasks and a phantom month
     appears in the index. **Row finish times stay true clock**
     (`HH:mm` as lived); only the filing boundary moves.
   - **Landing (no active query):** an `ALL MONTHS` entry row,
     always present, opening the index view; beneath it the current
     month's rows, newest first, grouped under date headings of the
     form `THU 29 OCT` (03:30-day date, label only — nothing follows
     a heading). Grouping is computed over the accumulated list, so a
     day straddling a page boundary merges when `EARLIER` lands.
   - **Month index:** one row per stored month, `OCTOBER 2026`,
     newest first, label only. No counts. A month with nothing
     stored is absent — the query guarantees it; the UI invents
     nothing. **As amended by step 8:** the index is the union of
     task-months and declared-word months (a month holding only a
     declared word still appears — it is an address where his word
     lives), and a declared word prints beside its date heading,
     including on a date with no rows: `TUE 28 OCT · ILL`, heading
     alone, nothing beneath it, no total.
   - **Month view:** the landing list scoped to the chosen month,
     same grouping; `EARLIER` at the foot loads the next 50, and
     renders only when the last page came back full.
   - **Row:** title (max 2 lines), category, finish time (`HH:mm`),
     time spent rendered from `actualSecondsSpent` in the app's
     existing duration voice. **No estimate on the row**, no
     checkbox, no bar, no rank.
   - **Detail (tap a row):** an in-destination page (local
     selected-task state; a `BACK` word returns). Full title,
     category, finish date and time — `THU 29 OCT · 10:48`, with the
     year added whenever it is not the current year (`29 OCT 2025 ·
     10:48`) — then estimate and actual as two separate plain lines
     (`ESTIMATE 25 MIN` / `SPENT 24 MIN` — never a delta, never a
     percentage), then the stored notes/substeps via
     `parseSubSteps`, done steps struck exactly as Tasks renders
     them. **Read-only:** BACK is the only action on the page.
   - **Search results:** while the query is non-empty, results
     replace the list: completion order, full date including year
     on every group heading, **no result count printed**, across
     all years, paged by the same `EARLIER` (Tief's amendment).
     **Each result row gains one plain line under the title
     (Moench's month-later walk, +~20, this file only): the first
     stored notes/substep line containing the query** — via the
     existing `parseSubSteps`, no highlight, no count. His search
     key is a word from the notes; without this line, a chapter's
     near-identical rows are indistinguishable twins and every
     wrong guess costs a row-tap + BACK. Month and landing rows
     are unchanged — the line exists only where a query does.
   - **Empty states — exact wording (ruling §2):** globally empty →
     `NOTHING STORED YET`. Search with no hits →
     `NOTHING STORED UNDER “{query}”` (curly quotes, as filed).
     Current month empty while the Drawer is not → the landing
     shows the `ALL MONTHS` row over an empty list, **in silence**.
     (The first filing's derived line `NOTHING STORED THIS MONTH`
     is **struck** on Moench's walk: it greets a full Drawer on a
     new month's morning like a verdict on a month that has barely
     begun. The ruling's absence law is better served by saying
     nothing.)
   - **Nothing else exists on this screen.** No delete, no clear,
     no reopen, no badge, no chart, no orange.
   ≈ 395 lines new against the 274 removed (as amended: matched
   search lines, search paging, the 03:30 heading helper, and the
   step-8 word merge).
8. **Date notes — SPLIT (Selbst's ILL-day walk; the first filing's
   deferral is struck).** The original step 8 deferred date notes
   wholesale to the 8→9 migration window. Selbst's walk breaks it
   on two verified facts: (1) the deferral defers **capture, not
   printing** — with no storage anywhere, every REST/ILL/FAMILY
   word declared during the shipped window has nowhere to go and
   can never be backfilled (the same no-backfill law TOKEN-SPEC §6
   states for spends); a month later the declared sick day reads as
   lost data, then as a slack day, inside a permanent record.
   (2) The deferred spec couldn't have printed its own case anyway:
   headings group over task rows and the months index groups
   `focus_tasks` alone, so a word-only day gets no heading and a
   word-only month never enters `ALL MONTHS`. **The split:**
   - **Capture ships WITH Brief B (+~30, 0 schema, 0 migration):**
     a tiny non-Room store (`dateIso → word`, SharedPreferences),
     a repository passthrough, and the declaration write path wired
     to wherever the declaration is made. No TOKEN-SPEC collision:
     nothing touches Room.
   - **Print ships WITH Brief B (+~30, in steps 4/7):** stored
     words merge into the heading set — a word prints beside its
     date heading; a word-only date renders as heading + word
     alone; the months index unions word-months (step 7).
   - **The drain waits:** at the 8→9 window, move the store into
     the specced `day_notes(dateIso TEXT PRIMARY KEY, word TEXT)`
     table (+~10 then, outside this price). **One exclusion,
     written now (Panik's watch): the word list for `day_notes` is
     REST / ILL / FAMILY and nothing else — never TEST, not now,
     not at the drain.**
   If only half of this step can be carried, carry capture: an
   unprinted word is a delay; an uncaptured one is a false record
   forever.

### Acceptance (the ruling's §2 checks, mapped to this build)

1. Complete a task → it appears once, under today's heading, and in
   no second rendering (the `completedTasksSummary` string is never
   read by this surface).
2. Reopen it (the existing repository toggle) → it leaves the
   Drawer. Re-complete → it returns under the **new** timestamp.
3. 51 completions inside one month → `EARLIER` pages 50 + 1, no row
   skipped or doubled at the seam (id tiebreak).
4. A distinctive word from an old task's notes finds it by search
   across years; its month is reachable from `ALL MONTHS` in two
   taps from the landing.
5. Grep the shipped Drawer surface: no day/week/month total, no
   streak, no percentage, no chart, no rank, no all-time count, no
   `DONE n` tally anywhere in Tasks or the Drawer, and no code path
   that deletes a COMPLETED row by date, batch or "clear".
   (`deleteTaskById` survives only as the Tasks queue's per-task
   delete-with-undo for *uncompleted* rows — see Flag B-2.)
6. A day with no finished tasks prints no line, no zero, no grade.
7. Sealed: the Drawer opens, rows and detail read fully, the search
   field accepts nothing, and no control on the screen can change
   any stored row.
8. **(Walk-added, Neu)** Fresh install → the Drawer is empty and
   `NOTHING STORED YET` renders; no seeded row appears. First real
   completion → it is the only row under today's heading.
9. **(Walk-added, Tief)** Two completions with the same title →
   relaunch the app → both are still stored. Search with a generic
   word matching >50 stored tasks → `EARLIER` reaches the oldest
   match; nothing is silently cut.
10. **(Walk-added, Kaeufer)** A completion at 00:02 files under the
    day that began the previous 03:30 (and its month), while its
    row still prints `00:02`.
11. **(Walk-added, Selbst)** Declare ILL on a day with no
    completions → a month later that date renders as heading +
    `ILL`, alone; a month holding only a declared word appears in
    `ALL MONTHS`.

### Price

| File | Δ |
|---|---|
| MonolithModels.kt (StoredMonth POJO) | +5 |
| MonolithDao.kt (queries in, clear out, 03:30 index) | +32 / −2 |
| MonolithRepository.kt (bounds + passthroughs + guards + day-word store, clear + seed out) | +92 / −18 |
| MonolithViewModel.kt (drawer state + paged search + word merge in, clear out) | +100 / −7 |
| MainActivity.kt (VAULT rewired, clear wiring out) | ≈ −12 net |
| FocusSequencerScreen.kt (param + tally out) | −6 |
| MonolithDatabase.kt (destructive fallback struck) | −1 |
| CurioVaultScreen.kt → DrawerScreen.kt | +395 / −274 |
| **Total** | **≈ +619 / −306 (net ≈ +313), risk MEDIUM-LOW** |

Risk notes: (a) paging/grouping interplay — mitigated by grouping
over the accumulated list + the id tiebreak; (b) SQLite `strftime`
with `'localtime'` and the `'-210 minutes'` day modifier in the
months index — correct per SQLite docs, but timezone behaviour is
exactly what render tests cannot see; acceptance 10 exists because
of it — verify on the phone build against a post-midnight
completion; (c) MainActivity rewiring is contained to the VAULT
branch; (d) the day-word store is SharedPreferences by design
(step 8) — its drain into Room at 8→9 is specced, priced, and
excluded from this total.

---

## OPEN FLAGS — for Ren (decisions the sources do not settle)

- **FLAG B-1 — RESOLVED by the Moment Walk (Neu).** The seeded
  COMPLETED demo task is deleted in-build (Brief B step 3). Neu's
  first-week walk proved the flag could not stay open: the row
  breaks the Drawer's promise on its first rendering, makes
  `NOTHING STORED YET` unreachable on any real install, and — with
  the clear chain dead, the detail read-only and reopen homeless —
  no shipped path could ever remove it; leaving the flag open
  would have decided it irreversibly for every install seeded
  meanwhile. The wider demo-data question (the QUEUED/ACTIVE demo
  seeds, Vera's hardcoded prime) stays open where it was; only the
  COMPLETED seed is struck.
- **FLAG B-2 — per-task delete vs stored rows (unchanged, now
  code-enforced).** Brief B step 3 makes `deleteTaskById` refuse
  COMPLETED rows, so the boundary no longer rests on UI
  reachability. If any future surface lists stored rows beside a
  DEL, that is a fresh ruling question — this brief builds no such
  surface and takes no position.
- **FLAG B-3 — reopening has no home; a priced design is now
  attached (Wieder's walk: BREAK).** The walk, end to end: DONE is
  an unguarded 12sp word on every queue row; one tired tap stamps
  a false completion (and, mid-session, a false receipt entry);
  today there are four dead ends and the false row is permanent —
  and Brief B as first filed hardened that (visibility up via the
  Drawer, recoverability still zero, a later honest completion
  storing the task twice, one false forever). Wieder's design,
  priced for Sarthak via Ren because it builds a surface no ruling
  named — **THE LAST-FINISHED LINE**: one slot under the Tasks
  QUEUE header, latest completion only (replaced, never
  accumulated): struck title · `FINISHED {HH:mm}` · the word
  `REOPEN`, composed only while unsealed (while sealed the same
  line reads as pure record — the ruling's while-sealed clause
  itself presupposes reopening exists unsealed). REOPEN calls the
  existing repository toggle; the Drawer row vanishes per
  acceptance 2. No expiry timer — any window is shorter than the
  within-the-minute notice this exists for. **Price ≈ +50 lines /
  3 files (VM +18, MainActivity +4, FocusSequencerScreen +28),
  0 schema, Drawer untouched, risk LOW.** Recommended add-on
  **+15**: reopen also repairs the session receipt (strip the false
  `completedTasksSummary` entry, decrement the count) → ≈ +65 /
  4 files. Rejected in the walk: REOPEN on the Drawer detail
  (breaks the read-only ruling), confirm-on-DONE (taxes every
  honest finish), a timed undo banner (window < notice time).
  **Awaiting his word; the Drawer ships lawfully without it.**
- **Derived wording — RESOLVED (struck).** `NOTHING STORED THIS
  MONTH` is struck on Moench's walk; the empty current month
  renders in silence under the `ALL MONTHS` row (Brief B step 7).

---

## APPENDIX — THE MOMENT WALK (all 15 Humans seats, Oct 29)

*Sarthak's standing order: the whole studio stays in the equation.
Ren re-seated all 15 Humans seats; each walked the briefs against
the actual moment its surface lives in. Breaks are flagged below
with the moment named; every adopted fix is already merged into
the steps and prices above. Letters: `mail/ruth/read/0815__from-<seat>__moment-walk.md`.*

**Brief A — the Frozen Digest:**

- **The 06:40 single-fact glance (Probe) — BREAK.** The snapshot
  query had no time bound and rows printed no time: yesterday's
  undelivered captures sat in this morning's snapshot wearing no
  date, so "did the expected message arrive?" was unanswerable
  whenever the sender had any prior capture. *Fix adopted (step
  5): every frozen row prints its own capture time; the query
  stays unbounded, by Ruth's recorded call.*
- **The angered thumb (Quaere) — PASS.** Every act path tried
  while sealed: absent chip, repository gate, per-item delete (no
  UI caller), status-drawer entry, dismiss-and-reopen, end-session
  — acting stays sealed. The pending-frame note became the step-3
  guard.
- **19:15, the depleted re-check (Muede) — BREAK.** A per-open
  freeze turns the feed loop into a pull-to-refresh loop; the close
  gesture silently armed the next snapshot. *Fix adopted (step 3):
  the freeze is keyed to the session; reopening while sealed
  reuses the same snapshot and provably pays nothing.*
- **The Safe Break between blocks (Bus) — PASS.** `isActive` holds
  across the break, so break-opens freeze identically from both
  entry points; the break→block flip is now named in the freeze
  semantics (+1 line of brief, 0 app lines).
- **The clock runs out mid-read (Barriere, moment 1) — PASS.** The
  latch holds; the ticker never touches the sheet; silence during,
  truth after dismiss.
- **A routine arms under the sheet (Barriere, moment 2 + Dopa,
  19:45 seam) — BREAK.** Brief A's "arming is not reachable"
  non-goal was false — `RoutineAlarmReceiver` arms with no UI
  (09:00 Mon–Fri, 22:30 daily), in both directions: a live sheet
  sealed under (composed-but-dead CLEAR ALL), and a new session
  arming under a stale frozen sheet. *Fix adopted (step 3): the
  session-keyed collector refreezes on any session-id change under
  an open sheet. The false claim is struck in the freeze
  semantics.*
- **The 08:00 triage (Dopa) — BREAK.** Unsealed CLEAR ALL is
  global and irreversible and per-item delete had no UI: keep
  ~100 rows or destroy an unread wanted capture with them. *Fix
  adopted (step 6): per-item delete wired into live-mode rows
  only.* Midday seam: PASS.
- **The mother mid-block (Freund) — BREAK (copy).** The frozen
  sentence didn't bound "everything" to the stamp, didn't say
  newer captures wait, gave no horizon. *Fix adopted (step 5):
  the line rewritten at 0 net lines.* The freeze itself protects
  the moment: her words readable at full peek, no act possible,
  callback after the block, per his verdict.
- **Saturday evening, keys in the digest (Panik, A) — BREAK.**
  Per-open freeze = serial reopen harvest of answer keys — a
  Saturday-night override smuggled in as a dismiss, against
  HELD-till-Sunday. *Same session-keyed fix.* First-open reading
  of held bodies is his full-peek verdict, not a break; Brief A
  does no content filtering — that is the Envelope's job.

**Brief B — the Drawer:**

- **Saturday's evening task (Panik, B) — PASS.** It files plain
  under Saturday's date; nothing resurrects TEST-day marking.
  Watch adopted: step 8's word list carries a never-TEST
  exclusion.
- **A month later, his own use case (Moench) — BREAK (one
  decision).** Routes pass: search = 2 taps + typing across all
  years; ALL MONTHS = the promised 2 taps to the month. But search
  rows showed nothing from the notes while a notes word was his
  only key — near-identical chapter rows, 2 taps per wrong guess.
  *Fix adopted (step 7): search rows carry the first stored
  notes/substep line containing the query.* He also struck the
  derived `NOTHING STORED THIS MONTH` line (see Flags).
- **2029, the doctor looks back (Tief) — PASS browse / BREAK
  search / BREAK permanence.** Months index and heavy-month paging
  hold at years scale. Search `LIMIT 200` silently amputated the
  oldest matches — his target era. *Fix adopted: paged search.*
  Permanence: the seed dedup deletes repeat-titled completions at
  launch, and the destructive-migration fallback sat armed. *Fixes
  adopted (steps 2–3 + database): dedup skips COMPLETED,
  `deleteTaskById` refuses COMPLETED, fallback struck.*
- **The first week (Neu) — BREAK.** FLAG B-1, resolved in-build:
  the seeded fake completion breaks the Drawer on its first
  rendering and could never be removed by any shipped path.
  *Fix adopted (step 3): the seed row is deleted.*
- **The declared ILL Tuesday (Selbst) — BREAK.** Step 8's deferral
  deferred capture, not printing — declared words would have been
  lost unbackfillably, and the deferred print couldn't render
  word-only days or months anyway. *Fix adopted (step 8 split):
  capture and print ship with Brief B via a non-Room store; the
  Room table waits for 8→9.*
- **The biggest task, that evening (Stolz) — PASS, 0 lines.** The
  no-estimate row serves; ESTIMATE/SPENT without a delta holds —
  strongest beat: two-sitting tasks make `actualSecondsSpent` a
  lifetime figure, so any printed delta would be a false
  efficiency number. `DONE n`'s death loses nothing here.
- **The mistaken DONE (Wieder) — BREAK.** FLAG B-3: a false
  completion is unrecoverable today and would stay so under
  Brief B, permanently, duplicated by the later honest finish.
  *Design attached to FLAG B-3 with its price (+50, +15 add-on),
  for Sarthak's word — it builds a surface no ruling named, so
  the cohort prices it and does not presume it.*
- **The heaviest month (Kaeufer) — PASS paging / PASS index /
  BREAK midnight.** 64 tasks = one EARLIER press; a day split at
  the seam merges loss-free. But a 00:02 completion filed under
  the wrong day AND month at three derivation points at once, and
  minted a phantom month. *Fix adopted (steps 2/3/4/7): one
  03:30 day boundary at every derivation point; row times stay
  true clock.*

**Walk totals:** Brief A — 3 PASS, 6 BREAK (all adopted; price
+89 → +135, risk stays LOW). Brief B — 5 PASS, 7 BREAK (six
adopted in-build; one — Wieder's — priced and referred to
Sarthak; price +490/−290 → +619/−306, risk stays MEDIUM-LOW).
Two of the breaks were errors in Ruth's own first filing (the
arming non-goal; the dedup's reach) — corrected in the steps, not
footnoted.

— **Ruth, Humans coordinator.** Inbox read and filed (Ren's four
letters: Q1/Q3 verdicts, Q4 verdict, Q6 fact, HPA-40 amendment; and
the 15 moment-walk letters, in `mail/ruth/read/`). No app-tree
files were touched; this brief, the covering letters and the wall
posts are the only writes.

---

## AMENDMENT (Ren, 09:10 IST Oct 29) — B-3 RULED BY SARTHAK: BUILD THE LAST-FINISHED LINE

Sarthak was asked in plain words whether a mistaken DONE should be
recoverable, with Wieder's priced design attached. His answer:
"Add it — let me undo a mistaken finish." Brief B therefore builds
the Last-Finished Line as specced by Wieder: on the Tasks screen,
the latest completion only, REOPEN available while unsealed, using
the existing toggle path (+50 lines / 3 files; the optional +15
receipt repair rides with it). It is part of the Drawer build, not
an add-on. No other stored row gains a delete or reopen path -
B-2's code-enforced guard stands untouched.
