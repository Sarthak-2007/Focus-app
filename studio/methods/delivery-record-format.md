# MERGE / DELIVERY RECORD — format v1

*Durchlauf, Methods. Filed Oct 29, 2026. Companion to
`methods/output-path-audit.md` (the baseline this format exists to
prevent recurring). For Schablone to template; for the Keepers'
Register (plan/KEEPERS-CHARTER.md) to adopt as the row shape of
WORK-REGISTRY.md. One record per artifact per state change. A record
is a file: `records/<artifact-slug>.md`, appended sections, never
rewritten — corrections are new dated sections, same law as the wall.*

## The rule the format enforces

**Shipped = in Sarthak's hands, and this record is the only proof.**
No record, no announcement. The word SHIPPED may not appear in
BUILD-CHANGES, on the wall, or in a letter unless a delivery section
(below) exists with `delivery-evidence` filled. (Hale's rule,
WASTE-LEDGER W-11; audit §5 L-1 is what happens without it.)

## Fields

### A. Identity (filled at build time, by the builder)

| Field | Content | Why |
|---|---|---|
| `artifact` | Filename as delivered/copied, e.g. `monolith-batch1.apk` — never the Gradle default `app-debug.apk` | Kills anonymous lane APKs (L-2) and name-twins (L-7) |
| `sha256` | First 16 hex of the file's sha256 | A name is a claim; the hash is the check |
| `size-bytes` | Exact bytes | Same-size twins (iteration vs final-debug) stay distinguishable with sha |
| `lane` | Lane dir + seat/coordinator, e.g. `builds/routines (Voss)` | Provenance |
| `base` | Named base: trunk timestamp + the BUILD-CHANGES entry the lane forked from, e.g. `trunk @ 09:12 merge entry, DB v8` | Bases are currently narrated from memory (L-5); this pins them at fork time, when they are known |
| `what` | One sentence: what this artifact changes for the user | Register rows must be readable by Konto without opening code |

### B. Diff (filled at build time)

| Field | Content |
|---|---|
| `diff-stat` | Files changed / + / −, lane-vs-base, e.g. `23 files, +2470/−200` |
| `diff-file` | Path to the extracted diff, e.g. `merge-review/routines/*.diff` — extracted BEFORE merge, while the base still exists (L-3) |
| `renders` | Paths of every render produced, or `none`. Renders live with the record if they live anywhere (L-4) |

### C. Verification (filled at build time)

| Field | Content |
|---|---|
| `build` | `GREEN @ <log path>` or `FAILED @ <log path>` — a lane with a failed final build does not get to write "built" |
| `tests` | `24/24 GREEN @ <log path>` / counts + log path. Empty or missing log = `UNVERIFIED`, spelled that way |

### D. Merge (filled by whoever merges — currently Ren; witnessed by Zeuge)

| Field | Content |
|---|---|
| `merged-by` / `merged-at` | Name + IST timestamp |
| `merge-evidence` | BUILD-CHANGES entry heading + the trunk files that now carry the lane's work (presence check, e.g. "trunk has EmergencyBreakLedgerTest.kt"). A merge claimed without a presence check is how counterfoil merged on a sentence (audit §2) |
| `merge-disposition` | `FULL` / `PARTIAL: <what was left out and why>` / `REJECTED: <reason>` — partial is a first-class outcome, not a failure to confess later |

### E. Delivery — the ship section (filled only when it ships)

| Field | Content |
|---|---|
| `shipped-to` | `Sarthak` — the only value that makes it shipped. "Clone chat" is a channel, not a recipient: record it under evidence and mark `shipped-to: PENDING` until he has it |
| `shipped-at` | IST timestamp of the delivery, not of the build |
| `delivery-evidence` | The checkable thing: chat + message context ("clone chat, file attached, 08:52"), his tap/reply quoting it, or device confirmation. "Delivered in clone chat" alone is evidence of sending, not of receipt — PA-S1-19 is the standing example of the gap |
| `device-state` | `PHONE-VERIFIED` / `PHONE-UNVERIFIED` — unproven-on-device stays labelled unproven (Pipeline stage 8); never upgraded silently |

### F. Stranding (filled the moment any of the above stalls > 1 day, by Konto/Mahner)

| Field | Content |
|---|---|
| `status` | `IN-LANE` / `MERGED-UNSHIPPED` / `SHIPPED` / `STRANDED` / `RETIRED-BY-RULING: <ruling ref>` |
| `stranded-reason` | One line, factual: `tests red in-lane`, `awaiting chrome train`, `build FAILED 10:21, dexBuilder`. The shieldpick knob is the model: stranded-by-ruling is a healthy terminal state when it is named |

## Blank template (for Schablone)

```markdown
# RECORD — <artifact>
artifact: <filename>
sha256: <16 hex> · size-bytes: <n>
lane: builds/<lane> (<seat/coordinator>)
base: <named base + BUILD-CHANGES ref>
what: <one sentence>

## Diff
diff-stat: <files, +/−>
diff-file: merge-review/<lane>/
renders: <paths | none>

## Verification
build: <GREEN|FAILED> @ <log>
tests: <n/n GREEN @ log | UNVERIFIED>

## Merge
merged-by: — · merged-at: —
merge-evidence: —
merge-disposition: —

## Delivery
shipped-to: PENDING
shipped-at: —
delivery-evidence: —
device-state: PHONE-UNVERIFIED

## Status
status: IN-LANE
stranded-reason: —
```

## Adoption notes for the Keepers

1. **Register** seeds WORK-REGISTRY.md (0 bytes at audit time) with
   one row per artifact in the audit's §2/§3 tables, statuses copied
   from the audit's verdicts — the baseline is already written; it
   needs transcribing, not re-investigating.
2. **Konto** owns §F: any artifact whose status is not SHIPPED or
   RETIRED-BY-RULING after 24h gets a `stranded-reason` or an
   escalation to Mahner. Silence is the failure mode, not stranding.
3. **Zeuge** witnesses §D/§E: merge-evidence and delivery-evidence
   are presence checks Zeuge performs on disk, not claims Zeuge
   transcribes.
4. **Nachweis** spot-checks `sha256` against the file quarterly —
   the final-debug ≡ shield identity collision (audit L-7) is the
   class of error this catches.
5. BUILD-CHANGES keeps its narrative role; this record is the
   ledger underneath it. When the two disagree, the record wins and
   BUILD-CHANGES gets a correction entry — never a quiet edit.

— Durchlauf, Methods · Oct 29, 2026
