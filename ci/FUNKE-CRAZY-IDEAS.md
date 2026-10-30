# FUNKE — CRAZY IDEAS for Issue #3: the build circuit as showman, witness, and monument

*Filed by Funke (crazy-ideas seat, dedicated-100 programme) on branch `agent/issue-3`.*
*Companion to the signed comment on issue #3. Everything here is buildable with
workflow YAML + shell; nothing touches the app. No green anywhere — success is
printed in ink. Nothing here compiles anything locally; the circuit itself is
the only compiler, which is the point.*

## Where this starts from

Seven runs. Runs #1–#3 died in plumbing (setup-android, sdkmanager PATH, the
missing Gradle wrapper). Runs #4–#7 died step-for-step identically at
`:app:validateSigningDebug` — a gitignored debug keystore that never existed in
the repo. The cure is one `keytool -genkeypair` step in the workflow. The bar:
one run with assemble success + `monolith-debug` artifact + unit-test step
**executed** (any verdict). This file is about what the circuit does *after* it
can do that — what it shows, what it proves, what it celebrates.

---

## SHOW — the circuit makes itself legible

### S-1 · The Proof Slip (receipt artifact)
- **What:** on success, a final step writes `proof-slip-<run_number>.txt` —
  a monospace till receipt: circuit name, run number + ID, head SHA + branch,
  per-step outcomes, assemble wall-time, APK filename/size/sha256, unit-test
  verdict line, closing line `— printed by the circuit, no hand touched it`.
  Uploaded alongside the APK as a second artifact file (or folded into the
  same artifact zip).
- **Why:** the finish receipt is the one surface Sarthak has explicitly loved.
  The circuit should speak in the app's own print language. A human (or a
  future agent) verifies a run by reading a slip, not spelunking a log.
- **Build cost:** ~15 lines of bash (`printf`, `sha256sum`, `stat`). Zero risk
  to the proof: generate it in an `if: always()` step *after* upload.

### S-2 · Build Weather (`ci/WEATHER.md`)
- **What:** a scheduled workflow (daily) queries the Actions API for the last
  20 `rescue-build` runs and rewrites a table: run, date, head, verdict,
  duration, APK size. Commits only when changed.
- **Why:** circuit health at a glance; drift (durations creeping, sizes
  ballooning) becomes visible before it becomes failure.
- **Build cost:** one workflow + one python script using `gh api`. 

### S-3 · APK Birth Certificate
- **What:** a pre-Assemble step writes `app/src/main/assets/build-info.json`:
  `{sha, runId, runNumber, workflow, builtAtUtc}`. Shipped inside every APK.
- **Why:** provenance for any binary found on a phone, in a folder, in a chat
  attachment months later. Pairs with S-1: slip says what the run made; the
  certificate says which run made *this* file.
- **Build cost:** 3 lines of bash. (App-side: one debug screen or the About
  surface can display it — optional garnish, cell's call.)

### S-4 · Size-delta commentary + ledger
- **What:** `ci/size-ledger.tsv` (run, sha, apk_bytes) appended each green
  run; the run comments on its issue/PR with the delta vs the previous green
  and the three largest entries from `unzip -l`.
- **Why:** bloat is caught the run it happens. The ledger doubles as the raw
  data for S-2's weather.

---

## PROVE — the circuit cross-examines itself

### P-1 · Signing Canary (promote from the wave-2 post-closure package)
- **What:** weekly scheduled workflow, clean checkout, runs only
  `./gradlew :app:validateSigningDebug`. ~1 minute. Failure opens/comments on
  issue #3 automatically.
- **Why:** the keystore wall silently ate four runs. The canary is the alarm
  on that exact door, forever.

### P-2 · Clean-Room Twin job
- **What:** a parallel job, same workflow: no Gradle cache restore, fresh
  `GRADLE_USER_HOME`, assembles from the bare checkout.
- **Why:** distinguishes "the circuit works" from "the circuit works because
  the cache was warm". The strongest form of the #3 acceptance bar.

### P-3 · Sabotage Drills (chaos runs)
- **What:** `workflow_dispatch` inputs select a sabotage: `skip-keystore`,
  `break-wrapper`, `wrong-sdk`. The drill asserts non-zero exit AND greps the
  log for the expected failure sentence; the drill itself passes only if the
  circuit failed *correctly*.
- **Why:** TELL-THE-TRUTH applied to CI. A pipeline that fails silently or
  misleadingly is worse than one that fails. This proves the failure modes
  are as honest as the success mode.

### P-4 · Attestation + verify job
- **What:** `actions/attest-build-provenance@v2` on the APK; a downstream job
  downloads the artifact, runs `sha256sum`, and compares against the Proof
  Slip's printed hash + `gh attestation verify`.
- **Why:** the artifact stops needing anyone's word. Proof-of-proof.

---

## CELEBRATE — the circuit keeps its own mythology

### C-1 · The Wall of Seven
- **What:** `ci/FINDINGS.md` keeps runs #1–#7 permanently at the top, named
  and dated, under the heading "The Founding Failures". Never pruned by any
  future cleanup.
- **Why:** the seven failures *are* the circuit's origin story; deleting them
  would make every later green look cheaper than it was.

### C-2 · Streak names + streak-broken posts
- **What:** the workflow computes the current failure streak; at streak ≥3 it
  posts the streak's name on issue #3 (first streak retroactively: "the
  Signing Winter, runs #4–#7"). On the run that breaks a named streak, a
  "streak broken" comment: length, killer run, head SHA.
- **Why:** morale machinery for a studio that works through walls. Cheap:
  one API query + arithmetic in the verdict-summary step that already exists.

### C-3 · `latest-green` rolling release
- **What:** green runs on `main` update a single GitHub Release tagged
  `latest-green`, APK + proof slip attached, release notes = the slip.
- **Why:** exactly one blessed download link at all times; ends "which APK
  was that?" forever. (Debug-signed; labelled as such in the notes — truth.)

### C-4 · CI Postcards
- **What:** each green run renders a deterministic SVG postcard — pure
  typography (Sora / Anonymous Pro), palette inks (#F04FCB, #493EA4, #E2BC39,
  #C84481, #E47E35, #377ED0, #34323B — never green), run number, date, head
  SHA, the words BUILD CIRCUIT — PROVEN. Saved to `ci/postcards/<run>.svg`.
- **Why:** a gallery of victories that accumulates for free and weighs
  kilobytes. Hand-built SVG only; no generated imagery, per standing law.

---

## If the cell takes only two

**S-1 (Proof Slip)** and **P-1 (Signing Canary)**: one makes every run
legible, the other guards the door that killed four. Together under an hour
of workflow work, neither can falsify the proof.

— Funke, crazy-ideas
