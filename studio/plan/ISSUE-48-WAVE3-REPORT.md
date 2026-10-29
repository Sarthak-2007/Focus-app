# Issue #48 — Audit R1 · seal-watchstore — Wave-3 report

Bundle lead: seal-watchstore (under PULS, cluster C6). Base: origin/main @ e29acdf.
Branch: agent/issue-48.

NOTE — brief/issue mismatch, flagged for PULS/Ren: the dispatch brief's five
findings (printState custody snapshot, WATCH-17 60s-vs-90s window, ArmProving
parity, abs() skew, wall-vs-monotonic stamps) do NOT match the live issue body.
The body (read in full via `gh issue view 48`) carries the audit's actual
seal-watchstore slice — persistence, poke(), seal-marker ordering, tombstones,
sealed-set race — matching `studio/audits/AUDIT-R1.txt` lines 1310–1365. The
issue body is what was worked and dispositioned; the brief's five items appear
to belong to a different slice's drafting and should be re-homed by PULS.

## Per-finding verdicts (all verified against current origin/main)

1. **[HIGH] WatchStore zero persistence — HELD, FIXED-IN-CODE (durable core).**
   Was exactly as charged (header admitted "Persistence: NONE"; StateFlow-only).
   Cure: LAST PROVED — the one fact whose loss the audit names and the only
   watch fact that stays true forever — now persists to SharedPreferences the
   instant it advances (forward-only; it could previously regress), restored
   by `WatchStore.bind()` called from MainActivity + the three writer
   services. Measurements are deliberately NOT persisted: every writer
   re-measures from the living services within one pass, and a restored
   measurement would print old proof as fresh — the lie this store exists to
   prevent (TELL-THE-TRUTH). Residency stays process-scoped by definition.
   The full Readiness Log (append-only Room table) was priced and rejected
   for this checkbox: a DB schema change colliding with #30/#31 lanes to
   store facts that re-derive in seconds. Test:
   `lastProved_survivesProcessDeath_viaBind`.
2. **[HIGH] poke() is a no-op — HELD, FIXED-IN-CODE.** Exactly as charged
   (bare `copy()` on a data class, assigned outside the @Synchronized publish
   lock — a no-op that could also clobber). Cure: `WatchSnapshot.pokeGen`;
   poke() routes through publish{} and bumps the generation, so the emission
   is real and lock-safe. Callers (ShieldService ×2, AccessibilityService ×1)
   now get the re-read they always claimed. Test: `poke_actuallyEmits`.
3. **[HIGH] executeSeal marker-before-publish, unguarded — HELD,
   FIXED-IN-CODE.** Sequence was claim → sealRow → writeSealMarker →
   publish() → Throw, no guard: a throwing publish left the on-disk marker
   testifying "sealed and heard" over a seal that never published or sounded,
   and recover() would adopt it silently. Cure: publish wrapped in
   runCatching; on failure `clearSealMarker()` strikes the session from the
   in-memory set and from both persisted slots (legacy int falls back to
   another still-sealed session or clears), executeSeal returns false; the
   claim stays spent by design, so recovery re-proves from the true
   marker-absent state. Test:
   `executeSeal_publishThrows_markerRolledBack_noFalseTestimony`.
4. **[HIGH] Annulment tombstones memory-only — HELD, FIXED-IN-CODE.**
   Tombstones were an in-memory set; annulment deletes the row, so process
   death resurrected a refused routine window. Cure: tombstones persist in
   the seal prefs (`annulled_windows`, "routineId:windowStart" set), written
   in prove()'s annulment breath, loaded lazily by any fresh
   SessionSealTransitionImpl, pruned past a 72h keep (heartbeat derive
   lookback is 48h — beyond it no derivation can name the window).
   RoutineHeartbeat's "for this process's life" comment corrected.
   Test: `annulmentTombstone_survivesProcessDeath` (real prove() annulment
   via fake clock, real repository, in-memory Room).
5. **[MEDIUM] Sealed set racy + single-slot marker — HELD, FIXED-IN-CODE.**
   `writeSealMarker` mutated `sealed` unsynchronized while isSealed/recover
   used synchronized(sealed); the persisted marker was one int, so a second
   seal erased the first session's only on-disk proof. Cure: the write is
   synchronized; the marker gains `sealed_session_ids` (string set, every
   sealed session) written alongside the legacy int;
   `MonolithRepository.sealedSessionIdsFromMarker()` unions set + legacy int
   (older builds' seals still testify); isSealed and recover() consult it.
   Test: `sealMarker_holdsEverySession_acrossProcesses`.

Batch-10 cross-check: its WatchStore diff (sweep-gen tracking) fixes none of
these five and does not collide beyond a defaulted-field neighbourhood in
WatchSnapshot — rebase stays cheap.

## The 20 seats (lens passes, signed)

- Crazy-ideas — proposed persisting the whole snapshot to Room; talked down by the truth law (a restored GRANTED is stale proof wearing fresh clothes) — landed the inversion: persist only what stays true forever. ✔
- Maximum-implementation-potential — five findings, five code cures, four new/changed test cases; zero comment-only dispositions. ✔
- Brutal critic — attacked the F1 cure as a partial fix until the disposition named exactly what persists and why the rest must not; attacked F3's rollback until the legacy-int fallback was proven in code. ✔
- Improver — LAST PROVED is forward-only now; previously any completing set overwrote it, even backwards in time. ✔
- Designer — no visual surface in scope; the truth the surfaces print is what changed. ✔
- Consumer — after process death the Deck no longer opens at "never proved" when a proof was landed yesterday. ✔
- Student — every KDoc that taught the old truth was rewritten with the code (WatchStore header, transition header, heartbeat comment). ✔
- User — a refused routine window stays refused after a reboot; a seal that died mid-publish no longer masquerades as heard. ✔
- User-experience — poke() now produces the re-read the Deck warrant's callers always assumed they were getting. ✔
- Purpose-questioning — asked why persist measurements at all; the persistence budget went only where loss was permanent: LAST PROVED, tombstones, seal proof. ✔
- Thoughtful — clearSealMarker strikes only slots that name THIS session; another session's testimony is never collateral. ✔
- Deep-thinker — traced every marker reader (recover, ESR-02 intention refusal, isSealed) before changing the marker's shape; the legacy int survives as a union member. ✔
- Enthusiastic designer — LEAD-COVERED: no UI in this issue; the seat's energy went into the emission-generation design for poke(). ✔
- Wow-level designer — LEAD-COVERED: same scope note; reviewed the bind-site spread (activity + 3 services) for earliest-possible restore. ✔
- Designer-29-years — chose SharedPreferences over a Room migration: three tiny wall-clock facts do not earn a DB version bump. ✔
- Intentionality designer — every persisted fact has exactly one writer and one stated reason; nothing stored "just in case". ✔
- Synchronous collaboration — diffed origin/batch-10 before writing a line: no cure duplicated, no FIXED-ELSEWHERE claims needed. ✔
- Implementation-quality — all `sealed` mutations now under the lock its readers use; tombstone pruning tied to the heartbeat's own 48h horizon + margin. ✔
- Technical execution — SealTransitionDirectTest is the Impl's first direct test class: real transition + real repository + in-memory Room, fake-clock annulment end to end. ✔
- Interaction-detail — LEAD-COVERED for visuals; the detail pass went to the publish-failure path's state hygiene (claim spent, marker struck, recovery re-proves — no half-states). ✔

## CI

Pushed on agent/issue-48; verdict recorded in the issue's evidence comment.
