# CRITICS — PHASE 3 register (Hale, coordinator)

*Scope: the MERGED plan only (plan/MASTER-PLAN.md), plus the two open
charges that needed only legwork. Charges against cohort files stay in
CRITICS-PHASE2.md. Law applied: MASTER-PLAN §0 as written, including his
verdicts and the two chair-level laws (art bar, go-beyond).*

---

## A. Charges against MASTER-PLAN.md as it stands (§0 + §0.1 + verdict addenda)

### P3-1 — The law is stated twice, with different content. HOLD.
§0, standard 2 still prints the sealed-state law as *"digest bodies are
shut."* The 05:48 verdict addendum, appended below §0.1, carves the
opposite for exactly that surface: *"the held-notification digest is
fully readable, bodies included."* Both statements sit in the document
of record; neither is marked as the amendment of the other in place.
A builder who reads §0 — the section titled "Authority and law" — and
stops there ships digest bodies shut and is wrong by his verdict.
This is my own Phase-2 merge condition 5 failing at the level of the
law itself: the law must be stated once, in place, in full. The verdict
addenda are provenance (quote, date, authority) — they must not be a
second, divergent text of the law.
**Required:** integrate each verdict into the standard it amends
(standard 2 carries the digest exception inline); addenda shrink to
one-line provenance pointers.

### P3-2 — The register calls settled charges open. TRUTH.
§0.1, disposition 10: *"Open charges carried — HELD digest walk …, slide
tap-to-commit, … carried in §9, unclosed."* Both named charges were
settled by Sarthak's verdicts at 05:48 and are recorded as settled in
this same document, pages apart. A register that lists a settled
question as open invites every future builder to re-litigate it — the
exact failure the document of record exists to prevent.
**Required:** disposition 10 lists only what is genuinely open after
the verdicts: DEEP label (closed below, §B), GUARD n/4, dead export
payload (closed below, §B), Still Witness name. The two verdict-settled
items move to the verdict list where they already live.

### P3-3 — Citations to sections that do not exist. WATCH, standing.
§0 and §0.1 cite §5.1 (gate vocabulary), §5.2 (ARM accounting), §5.3
(valve spec, token debt), §6 (chrome state), §7 (Ledger conditions),
§8 (HELD digest walk), §9 (open charges) as the homes of settled
matter. The document today is 121 lines: §0, §0.1, and the verdict
addenda. None of the cited sections has landed. This is not yet a
violation — the merge is in progress — but it is filed now so it
cannot age into one: **no section may be cited as authority before it
lands, and the merge audit checks every §-citation in §0 against a
landed section before the plan is called complete.** The valve line in
§0 ("Built and shipped — spec §5.3") is the type specimen: the shipped
APK is real, the cited spec is not yet in the document.

### P3-4 — "Graded first on beauty" needs its boundary sentence. TRUTH/ARM.
§0 holds both that screens are *"graded first on whether it looks and
feels beautiful"* and that the three standards are *"the only walls
that are never exceeded."* Read together by a careful builder these
are consistent — standards as constraints, beauty as the objective
among compliant designs. Read quickly, "first" licenses spending ARM
seconds or HOLD surface on looks, which the go-beyond clause expressly
forbids ("beyond applies to beauty and craft, never to honesty" — and
ARM/HOLD are named as never-exceeded in the same breath). One sentence
in §0 closes the gap: *beauty is graded first among designs that pass
the three standards; no aesthetic treatment may spend ARM time, HOLD
surface, or truth.* Filed now because the beyond wave (BEYOND-deck.md,
in flight) is where this sentence will be tested first.

---

## B. Open charges closed by legwork

### CLOSED — the DEEP / WORK_SPRINT label. Ruling: one loadout, four names; "DEEP" is false.
Walked in the shipped code, not the plan files:

- The middle loadout is **one real, distinct configuration**:
  `SmartLoadout.WORK_SPRINT` (data/MonolithModels.kt:49). Its applied
  behaviour (data/MonolithRepository.kt, `applySmartLoadout`, L697):
  SHORT_VIDEO + GAMES → LOCKED, SOCIAL → PEEK_2M, everything else OPEN.
  Genuinely distinct from STUDY (locks SOCIAL too; peeks YouTube /
  Chrome / MESSAGING) and from SLEEP (locks all but phone/clock/maps).
  It is the light tier: kills short video and games, rations social,
  leaves messaging and docs open. It is not a phantom and not a
  duplicate.
- It wears **four names**: `WORK_SPRINT` (enum + persisted DB string),
  "Work Sprint" (its own enum title), `WORK` (routine-row rendering,
  ui/screens/ShieldMatrixScreen.kt:305), and **`DEEP`** — the physical
  knob's middle stop (ui/views/BlockitSuitcaseAndSwitchesViews.kt:174,
  `levelLabels = ["STUDY", "DEEP", "SLEEP"]`; Shield's own header
  comment repeats "STUDY / DEEP / SLEEP").
- **"DEEP" is borrowed from a different object**: `StudyModePreset
  .DEEP_50_10` ("50m Deep", MonolithModels.kt:70) — a 50/10 duration
  preset. A user who turns the knob to DEEP is promised the deep-work
  preset and handed the work-sprint loadout. One word, two referents,
  and the knob picks the wrong one. (Third job, prose-grade: STUDY's
  enum title is "Deep Study" — the word should retire from loadouts
  entirely.)
- **Fix, priced:** knob middle label `DEEP` → `WORK` (one string in one
  array; Shield header comment corrected in the same pass). The enum
  name and the persisted string do **not** change — no DB migration,
  no routine-row churn; routine rows already render it as WORK, so the
  fix also collapses four names to two (code name + one human name,
  "Work Sprint", with WORK as its knob-short form). TRUTH restored for
  the price of a string.

### CLOSED — the dead export payload. Ruling: it is not a backup; kill it, priced.
Full trace in the shipped code:

- **Surface:** CurioVaultScreen (the Ledger) chip — "EXPORT AES-256-GCM
  LEDGER BACKUP", badge turns `READY` when a payload exists
  (ui/screens/CurioVaultScreen.kt:278).
- **Path:** chip → `MonolithViewModel.exportEncryptedStateBackup()`
  (L509) → `MonolithRepository.exportEncryptedBackup()` (L1155) builds
  a JSON of app tiers/limits + session counts → `CryptoIntegrityManager
  .encryptBackupAesGcm()` → the string lands in a `StateFlow`
  (ViewModel L129–130), collected in MainActivity (L173) and passed
  back down to the Vault (L410).
- **The payload's only consumer in the entire app is
  `encryptedBackupPayload.isNotEmpty()`** — the READY badge. There is
  no share intent, no file write, no clipboard anywhere in the codebase
  (grep: zero hits for ACTION_SEND / createChooser / clipboard /
  FileOutputStream). The blob can never leave the process.
- **The restore half is equally dead:** `decryptBackupAesGcm`
  (CryptoIntegrityManager L168) exists and has **zero callers**. A
  backup with no egress and no restore is not a backup; the button
  performs encryption as theatre and the label "EXPORT … BACKUP" is a
  false statement about what happens when it is tapped. TRUTH charge,
  proven, not argued.
- **Kill price (itemised, every removed symbol verified caller-free):**
  - CurioVaultScreen: the chip item + now-orphaned params
    (`encryptedBackupPayload`, the refresh callback) — ~15 lines.
  - MainActivity: state collection (L173) + prop pass (L410) — 2 lines.
  - MonolithViewModel: payload StateFlow (L129–130) +
    `exportEncryptedStateBackup()` — ~9 lines.
  - MonolithRepository: `exportEncryptedBackup()` incl. doc comment —
    ~28 lines. (`querySystemBootCount` stays — five other callers.)
  - CryptoIntegrityManager: `encryptBackupAesGcm` +
    `decryptBackupAesGcm` + `getHardwareAesKeyOrNull` (called only by
    those two) — ~85 of the file's 194 lines.
  - **Total ≈ 140 lines across 5 files. Zero behaviour change for any
    living feature; zero DB impact.**
- **Boundary of this kill:** the HMAC session signing in the same file
  (`signSessionState` / `verifySessionState`, the receipt stamps) is
  **not** part of this charge — it is already condemned under the Ledger
  rebuild conditions (his keep-rebuilt verdict inherits "the crypto
  theatre stays dead"). It dies in the Ledger rebuild, not here; this
  charge closes the export path only, so the two kills are never
  double-counted.

---

## C. Standing items

- **BEYOND-deck.md** (Marlowe/Sana, in flight): not landed at filing
  time. On arrival it gets the full audit against the standards —
  beyond is licensed for beauty, never for honesty; P3-4 is the test
  it will be read under.
- **Screen sections (§2–§9)** of MASTER-PLAN.md: audited as they land,
  charges appended to this register. The merge audit convenes in full
  when Ren calls it — nobody has clocked out.

— Hale, Critics coordinator, Phase 3
