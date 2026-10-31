# PILL RENDER-INTENT SPEC — issue #16

**Author:** T24 Builder seat 3, #16 wave (render scaffold lane).
**Companion code:** `app/src/test/java/com/example/PillRenderScaffoldTest.kt`
on branch `agent/issue-16-render-scaffold` (base: `batch-10`, PR #231).
**Status:** scaffold + spec filed; CI verdict is read from the scaffold
PR's job log under the interim gate. Nothing in this lane touches an
existing pill/train file.

---

## 1. Why this spec exists

The programme's screens-with-every-build pipeline renders the app's
surfaces under Robolectric and uploads the captures as CI artifacts.
The pill is one of those surfaces once it lands — but it is an awkward
one, and this spec exists so nobody has to rediscover why:

- The pill is **not a composable**. Post-train (`batch-10`), it is a
  `WindowManager` overlay `LinearLayout` built privately by
  `PillController` (app/src/main/java/com/example/service/PillController.kt).
  There is no Compose node, no test tag, no screenshot API surface.
- Its face is **one pure derivation** — `PillDerivation.derive(...)`
  in service/PillFace.kt — with exactly **four faces**: `Absent`,
  `Session`, `Break`, `Peek`. The overlay (Route A) and the shield
  notification (Route B) both print what this function decides.

So pill render evidence comes in two tiers, and a capture only counts
when it satisfies the intent for its tier (§3–§4).

## 2. The eight states are scenarios, not faces

The #16 brief names eight pill states. They map onto derivation
inputs — (session row, app rows, nowMono, foreground, keyguard,
overlay grant) — not onto eight faces. Three of them derive the same
`Absent`. Any review that expects eight visually distinct captures
is asking for pictures the machinery deliberately does not produce.

| # | Brief state | Fixture (the difference that makes it) | Face | Capture must show |
|---|---|---|---|---|
| 1 | **unarmed** | no session row | Absent | **no pill** — absence is the evidence |
| 2 | **armed** | active session, elapsed 0, foreign foreground | Session | `SESSION · BLOCK 1/4` / `25:00` / no ✕ |
| 3 | **holding** | active session mid-block (elapsed derived from stored pair), foreign foreground | Session | `SESSION · BLOCK 2/4` / `19:50` / no ✕ (stopwatch form: `SESSION · ELAPSED` / `+01:45`) |
| 4 | **peek** | live lease (`activePeekUntil > now`), peeked app foreground | Peek | app name UPPERCASE / fuse `mm:ss` / **✕ VISIBLE** |
| 5 | **break** | `isInSafeBreak`, stored remaining + lastTick; live lease ALSO seeded | Break | `BREAK` / derived `mm:ss` / no ✕ — and **not** the peek (break outranks peek) |
| 6 | **spent** | lease end == now (the end instant itself) | Session (reverted) | session print, **no ✕, no peeked name** — a spent peek leaves no residue |
| 7 | **finished** | session row present, `isActive=false`, `completedSuccessfully=true` | Absent | **no pill** — same picture as unarmed; told apart by the seeded row in the test record only |
| 8 | **can't-verify** | live session + live lease, but a honesty gate closed: keyguard locked, **or** telecom foreground, **or** overlay grant revoked | Absent (derivation forms) / no window (grant form) | **no pill, with the cause named** in the test name + fixture — an unexplained absence is not evidence |

## 3. Tier A intent — derivation evidence (deterministic)

One JUnit test per state (`a_*` in the scaffold). A Tier A result
counts as evidence iff ALL of:

1. The derived face equals the expected face **by value** (data-class
   equality), including every printed number — not just the type.
2. The `renderKey` matches where the key carries meaning (peek:
   `peek:<pkg>:<seconds>`; absent: `absent`).
3. The expected print — (label, time, hand-back visibility) — equals
   the **literal strings** the renderer would show, asserted as
   literals (`"SESSION · BLOCK 2/4"`, `"19:50"`, `"PEEKED"`,
   `"02:00"`, `"BREAK"`, `"09:30"`, `"+01:45"`). Literal oracles are
   the train's own discipline (PillDerivationTest wave-2 fixes
   F1/F2/F5 replaced self-referential oracles with hand-computed
   literals for exactly this reason).
4. For absence states, `expectedPrint(face) == null` is asserted —
   the state must have *no print*, not merely an unchecked one.

Tier A proves what the pill **would print**. It never claims a window
stood. Any report that cites Tier A as a "render" is overstating it.

## 4. Tier B intent — window captures (the actual renders)

One test per visible/absent form (`b_*`): holding, peek, break,
unarmed (absence), can't-verify/overlay-revoked (absence). The real
`PillController` runs against a real in-memory Room DB; the window it
builds is harvested from the controller instance and drawn to
`pill_<state>.png` in the lane shots dir (resolved via the standard
`shots-redirect.txt` lane pattern; fallback
`$TMPDIR/monolith-shots-pill`). A Tier B capture counts iff ALL of:

1. **The window is the production window** — built by
   `PillController.buildWindow()` via its public `start()` ticker,
   not a replica layout, not a Compose stand-in. (The scaffold reads
   the controller's own `rootView` back by reflection; it constructs
   no view of its own.)
2. **The PNG is real**: file exists, PNG format, > 1 KB, dimensions
   equal the measured window (wrap-content pill, not a full-screen
   412×915 frame — a full-screen capture of a pill is a harness bug,
   not a render).
3. **The printed texts, walked from the captured view tree**, equal
   the Tier A literals for that state: label TextView, time TextView,
   and the `✕` TextView's visibility (VISIBLE only on peek).
4. **Time tolerance is a named window, not a point.** Tier B fixtures
   anchor stored ends to the *live* shadow `elapsedRealtime` at seed
   time; the ticker and the pump consume real seconds before the
   capture. Expected times are therefore the seeded value minus
   ≤ ~6 s of pump, asserted as an explicit set (holding:
   `20:00…19:56`; peek: `02:00…01:56`; break: `10:00…09:56`). A
   capture outside its set fails. The set is part of this spec —
   widening it to make a capture pass is falsifying evidence.
5. **Absence captures manufacture no PNG.** For unarmed and
   can't-verify, the evidence is the asserted null window after a
   full pump with all other gates open. A screenshot of an empty
   screen proves nothing and is not produced.
6. **Skin check (visual, human or panel):** the PNG must show the
   pill wearing `PillTheme.palette` (default: warm near-black glass
   `0xE61F1A16`, hairline border `0xFF4A4038`, cream figures
   `0xFFFFE4CF`, muted label `0xFFB9ACA0`, accent ✕ `0xFFF47442`) —
   Anonymous Pro figures, capsule (999dp corner radius), seated at
   the right edge ~22% down. **Never green** (studio law). A capture
   with default Android styling is a failed render even if the
   strings match.

## 5. Known drift risk, named

Tier A's `expectedPrint` is a *mirror* of `PillController.render()`'s
string logic (label formats, `formatClock`, ✕ visibility). If the
train changes the renderer's strings, Tier A keeps passing against
stale literals while the pill prints something else. Tier B's
TextView walk is the pin that catches that drift — which is why a
lane report must never present Tier A alone as render evidence, and
why the scaffold keeps both tiers in one file: **Tier B red + Tier A
green = the renderer moved; Tier A red = the derivation moved.**
That diagnostic split is deliberate.

## 6. Seams and can't-verify honesty

The controller has two gates that are **not injectable** in the
post-train API:

- **Foreground:** `MonolithRepository.queryForegroundPackage()` —
  needs the `GET_USAGE_STATS` AppOp ALLOWED and a UsageStatsManager
  `MOVE_TO_FOREGROUND` event for a foreign package; otherwise the
  controller's last-foreground stays at own-package and every face
  derives Absent (the designed safe side).
- **Overlay:** `Settings.canDrawOverlays(context)` — the
  `SYSTEM_ALERT_WINDOW` AppOp; without it the window never draws
  and Route B (shield notification) carries the time.

The scaffold probes both gates through the Robolectric 4.17 shadows
**by reflection** (probe-and-report, no compile dependency on shadow
internals). Outcomes:

- Probes open both gates → Tier B runs as specified in §4.
- A probe fails → the affected Tier B test fails with a
  `SEAM-REPORT` naming the gate and the probe results (AppOp setMode
  shape, `canDrawOverlays` readback, usage-access readback, event
  seeding result, foreground readback). **That report is a finding,
  not a scaffold bug to be silently routed around** — it means the
  pill's visible faces cannot be render-verified under Robolectric
  without a seam.
- **CI run 1 evidence (PR #655, run 41155367631, head dfadf64e):**
  compile green (assemble SUCCESS; test-compile warnings only);
  460 tests executed, 4 failed — all 11 Tier A tests and both Tier B
  absence tests PASS. The 3 visible-face Tier B tests failed at the
  seam, with the report naming the exact gate: AppOps probes landed
  (usage access read back ALLOWED; the seeded foreground event read
  back through the production query in the peek test) but
  **`Settings.canDrawOverlays` stayed false** — the String-op AppOps
  store is not the one Robolectric 4.17's overlay readback consults.
  The scaffold's probes were widened in response (Int-op setMode +
  the SYSTEM_ALERT_WINDOW permission-grant route + foreground
  seed-retry, after the run also showed seed readback is racy).
  If run 2 still reads `canDrawOverlays=false` with every probe
  landed, the overlay gate is declared Robolectric-closed on this
  version and the §6 cure below is the only route to Tier B.
- **Priced cure (train owners K1/W6, not this lane):** a
  `@VisibleForTesting` constructor seam on `PillController` —
  injectable foreground provider + overlay-grant provider,
  defaulting to the production reads. ~15 lines in PillController.kt
  + the scaffold's Tier B re-pointed at it. This lane does not land
  it: collision guard (existing train files are K1/W6's).
- The can't-verify/**overlay-revoked** state has the same property
  one level down: if the revoke probe cannot move
  `canDrawOverlays` under Robolectric, that state is **device-only
  evidence** (grant/revoke on a phone, capture there) and must be
  reported as such — never as covered.

## 7. What this scaffold deliberately does not do

- No drag/snap, body-tap, or hand-back *interaction* verification —
  the ✕ is asserted present/visible; tapping it routes through
  `repository.recordPeekEnded`, which is PillLeaseRepositoryTest's
  jurisdiction on the train.
- No Route B (notification) captures — the shield notification's
  peek face is a separate surface with its own lane.
- No look/world redress — `PillTheme.palette` is read as-is; the
  seven looks redress it elsewhere.
- No local compiling was performed for this lane, per standing law;
  the scaffold PR's CI is the only compile court, judged by the
  interim gate from the job log (unit-test step compiled and
  executed tests > 0; no compilation-error step failure).

## 8. Filing

- Branch: `agent/issue-16-render-scaffold` (new files only:
  this spec + `PillRenderScaffoldTest.kt`).
- PR: against `batch-10` (the scaffold compiles only against the
  post-train API; a PR against `main` would be a false court).
- #245 carries the PR line in the same run, per programme law.
- Merge of this scaffold is the train owners' call after the train
  lands; on `main`, the file joins the suite the
  screens-with-every-build pipeline already runs.

— T24 Builder seat 3, #16 wave
