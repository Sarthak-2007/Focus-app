# LIMIT CENSUS — the data layer, trunk, Oct 29, 2026 (~21:20 IST)

Filed by MARKO (MaxImpl — loadout), the caps lane's approved NEXT (b),
under Ren. Method: every `LIMIT` in the data layer found by grep over
`data/MonolithDao.kt` and `data/MonolithRepository.kt` (trunk,
`~/workspace/clone-inspect/Focus-app/app/src/main/java/com/example/data/`),
each entry named by its function and classified:

- **HISTORY-CAP** — silently truncates Sarthak's standing record. A charge.
- **PAGING** — a window into a record that stays whole (LIMIT + OFFSET,
  has-more semantics). Lawful.
- **SINGLE-ROW** — LIMIT 1 on an identity/active lookup; the row is the
  whole answer by construction. Lawful.
- **NOT A LIMIT** — grep noise (the per-app `dailyLimitMinutes` product
  field, a Kotlin `take(10)` on a name string). Recorded so the census
  is complete against its own grep.

Companion sweep: `.take(` / `takeLast` / `subList` over both files —
one hit, Repository L1187 (a loadout name trimmed to 10 chars for
display; not a record read). No Kotlin-side truncation of any record
list exists in the data layer.

---

## Charges — HISTORY-CAP (3, all being killed by WORK-QUEUE item 16)

1. **MonolithDao.kt:63** — `getSessionHistoryFlow()`
   `SELECT * FROM focus_sessions ORDER BY startWallTimeMillis DESC LIMIT 60`
   Session history, live flow. Past the 60th session the record
   silently stops existing for every reader of this flow.
2. **MonolithDao.kt:71** — `getSessionHistorySnapshot()`
   The same `LIMIT 60` on the one-shot snapshot read — the second cap,
   caught by the caps lane: killing only the flow would have left the
   snapshot (the read the Drawer/frozen surfaces trust) still capped.
3. **MonolithDao.kt:134** — `getInterceptLogsFlow()`
   `SELECT * FROM intercept_logs ORDER BY timestampMillis DESC LIMIT 80`
   The intercept record silently truncated at 80.

   **Disposition of 1–3:** the caps lane (`builds/caps`) removes all
   three — diff-verified at exactly those three lines, nothing else —
   with `RecordCapsTest.kt` as its proof (75 sessions and 100
   intercepts read back whole, order head + tail). Lands under the
   build gate with the gated build; this census files the charges so
   they are named even before the merge.

## Ruled-kept (1 — adjudicated, NOT a charge)

4. **MonolithDao.kt:141** — `getCapturedNotificationsFlow()`, `LIMIT 100`
   The live flow over captured notifications keeps its shipped cap by
   a ruled split (Tief's audit; WORK-QUEUE 16): the sealed-truth read
   beside it — `getCapturedNotificationsSnapshot()` (L153) — is
   deliberately UNCAPPED, per its own KDoc (L144–152: "a held item
   past any cap is still held, and a frozen read that silently
   dropped it would lie by omission"). The cap touches only the live
   peek surface, never the frozen record. Stands.

## PAGING — lawful (2)

5. **MonolithDao.kt:196** — `getStoredPage(...)`, `LIMIT 50 OFFSET :offset`
   The Drawer's stored-tasks page read. The Drawer section header
   (L187–194) states the law it implements: retention is permanent,
   every read is paged, "no read may silently cap the record."
6. **MonolithDao.kt:202** — `searchStoredTasks(...)`, `LIMIT 50 OFFSET :offset`
   Same paging law over Drawer search.

## SINGLE-ROW — lawful (8)

7. **MonolithDao.kt:57** — `getActiveSessionFlow()`, `LIMIT 1` (the one active session).
8. **MonolithDao.kt:60** — `getActiveSessionSnapshot()`, `LIMIT 1` (same, one-shot).
9. **MonolithDao.kt:86** — `getSessionById(id)`, `LIMIT 1` (identity lookup).
10. **MonolithDao.kt:89** — `getSessionByIdFlow(id)`, `LIMIT 1` (identity lookup).
11. **MonolithDao.kt:171** — `getTaskById(id)`, `LIMIT 1` (identity lookup).
12. **MonolithDao.kt:222** — `getGuardSettingsSnapshot()`, `LIMIT 1` (singleton row, id = 1).
13. **MonolithDao.kt:225** — `getGuardSettingsFlow()`, `LIMIT 1` (singleton row, id = 1).
14. **MonolithDao.kt:273** — routines by-id read, `LIMIT 1` (identity lookup).

## NOT A LIMIT — grep noise, recorded (4)

- **MonolithDao.kt:37–38** — `updateDailyLimit(...)`: `dailyLimitMinutes`
  is the per-app daily-limit product column, not a query cap.
- **MonolithRepository.kt:238, 1118–1119, 1133** — the same product
  field (seed values + the `updateAppDailyLimit` pass-through).
  **MonolithRepository.kt contains zero SQL LIMIT clauses.**
- **MonolithRepository.kt:1187** — `name.take(10)`: a loadout name
  trimmed for display. Not a record.

---

## Verdict

The data layer holds exactly three silent truncations of the record —
Dao L63, L71, L134 — and all three are already written out of the
caps lane, awaiting their proof compile and the gated merge. Every
other LIMIT in the layer is a page window, a single-row identity
read, an adjudicated live-flow cap whose sealed counterpart is
uncapped, or not a limit at all. No new charges beyond item 16's
three; nothing here needs a future car.

— MARKO, Oct 29, 2026
