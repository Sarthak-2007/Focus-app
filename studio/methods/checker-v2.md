# CHECKER v2 — definitions, judgments, dispositions

*Massarbeit (Methods, spec translation), Sweep 2, Oct 29 2026.*
*Tool: tools/studio_check.py v2 (changelog in its header). Full run:*
*methods/studio-check-v2-run.txt. Baseline v1, run live at the start*
*of this commission: 1 FAIL, 40 WARN, exit 1; density 0/31 + 0/29.*

---

## 1. The evidence-density metric — definition and proof

**Definition (printed by the tool on every run).** The unit is the
CLAIM BLOCK: an entry heading plus its body lines up to the next
entry. Entry starts are `##`/`###` headings, bold ID leads
(`**LR3-S1-03 …`), and bold bullets (`- **…`). A heading whose whole
body is ≤2 lines before the next entry is a section divider, not an
entry. An entry carries evidence if its block contains any of:

- **STRONG** — a Kotlin file (`*.kt`/`*.kts`), a file:line or
  file-Lline reference, a bare `L<line>` reference, `line(s) <n>`,
  or a sha fragment;
- **TRACE** — a named source document (`.md` path, MEMORY.md,
  BUILD-CHANGES, a `mail/` path), a render path (`shots/*.png|jpg`),
  a hex colour, or a section/step reference (§n, step n, Brief A/B)
  *paired* with a named source in the block or in its section's
  declared Home (Fundus req 1's pairing rule, with the Home
  extension the RULINGS book's own format requires).

A preamble (file-level, or a section's italic Home line) that
declares an evidence base gives its entries **inherited** credit,
reported as a separate `+N` — never folded into the direct number.

**Before / after** (same files, live corpus):

| file | v1 (headed lines) | v2 direct | of which STRONG | + inherited |
|---|---|---|---|---|
| LIE-REGISTER-3.md | 0/31 | **49/53** | 25 | +0 |
| FINISH-LIST-1.md | 0/29 | **36/60** | 1 | +24 |
| RULINGS.md | (not measured) | **212/230** | 0 | +7 |

The differences are the files' real shapes, now visible instead of
flattened to 0: the Lie Register cites code in its bodies (STRONG
25); RULINGS cites documents, never code (STRONG 0 is correct, not
a defect); FINISH-LIST's evidence is a render set declared once in
its preamble (hence +24 inherited) plus per-finding surface names —
its direct 36/60 is the honest signal that its findings cite less
per-entry than the registers do.

**Hand-check (the 10-line sample).** LIE-REGISTER-3.md lines 62–71
are the complete block of entry LR3-S1-03. By hand:

- L64: `(MonolithRepository.kt:1441-1450)` — STRONG, file:line. ✔
- L66: `(L1464-1475)` — STRONG, L-reference. ✔
- L71: `(moss.md; converged by U-6.)` — TRACE, named source. ✔

The checker counts this entry as evidenced; the hand-check agrees.
Negative control, also by hand: the §"THE VERDICT" block (L19–41)
contains charge IDs (M-1, Maas A-1/A-2) but no file, line, sha or
named source — the checker counts it **not** directly evidenced,
and it is one of the 4 non-evidenced LR3 entries (the other 3 are
the Convergences, Acquittals and Not-Hunted section blocks — prose
sections, not charges). RULINGS spot-check: the L196 bullet cites
"Brief A, step 3"; its section's Home (L191–194) names
DIGEST-AND-DRAWER-BUILD-BRIEFS.md — paired, counts direct. ✔

---

## 2. The ROUTINES-REBUILD-READY ruling (the v1 FAIL)

**Ruling: the FAIL was checker literalness. The brief's naming IS
sufficient for a builder. v2 passes it — by evidence, not by
relaxation.**

Reasons, all checkable on disk:

1. The brief's own header states its format: four seat parts
   "incorporated by reference — this brief governs conflicts, the
   parts govern detail", each named **by path**
   (`plan/routines-rebuild-parts/sten.md` etc.). The parts carry
   **45 `.kt` citations** between them (sten 10, rurik 6, otis 19,
   freya 10), most with line numbers (`MonolithRepository.kt:869ff`,
   `MonolithModels.kt:249–274`, …). A builder following the brief
   reaches exact code sites in one hop.
2. The brief itself names sites as **exact symbols**, which in a
   1,000-line repository file are *more* precise than a filename:
   `RoutineDerive.derive(...)` with its full signature,
   `RoutineHeartbeat.tick(source)`,
   `MonolithRepository.setBrowserBlockerEnabled`,
   `ensureSeededAndSyncPackages`, `disarmActiveSession`,
   `Migration(8,9)` + `.addMigrations(MIGRATION_7_8, MIGRATION_8_9)`,
   the table/column names of the A1 census. No builder can land on
   the wrong file from these.
3. The build sequence (§3) is keyed to part sections (Sten §1,
   Otis §§1–4, Rurik's block "verbatim"), the price is consolidated
   (§5), and the forks a merged brief would otherwise carry are
   disposed in §2 as binding coordinator's arbitrations A1–A6 under
   the header's "Nothing below waits on a question."

**The v2 gate therefore judges reachability, three routes:** (a)
`.kt` in the brief; (b) `.kt` in a plan file the brief incorporates
by path; (c) ≥3 unambiguous symbol sites in the brief. ROUTINES
passes (b) *and* would pass (c). **Control case (Fundus req 5):**
the gate was extracted and run standalone — a synthetic brief with
a price and a forks word but no reachable sites returns None and
still FAILs; a brief citing a `.kt` passes route (a). The rule was
not relaxed; it was taught the studio's other two site formats.
That is the spec-translation fix: the format is now named, so no
future brief re-litigates it — merged briefs must name sites in one
of the three reachable forms.

The two v1 forks WARNs fall with the same lesson: ROUTINES carries
its forks as §2 arbitrations + "nothing waits on a question";
DIGEST-AND-DRAWER carries them as "## OPEN FLAGS — for Ren
(decisions the sources do not settle)" (L617). v2's marker set
accepts forks / open flags / open questions / arbitrations /
waits-on-a-question. Both pass.

The five v1 SHIPPED WARNs were the same disease at line scope:
BUILD-CHANGES L18/58/64 headings claim SHIPPED and their entries
carry the marker in the body (`.apk` sha; L18 also "delivered in
clone chat") — v2 judges the **entry** (heading + body), and a
marker in the heading itself also counts (three headings filed
during this very run carry `.apk, sha …` in the title line and
pass). L82 ("TWO SHIPPED LIES found") and L105 ("GREEN IN SHIPPED
CODE") are findings *about* shipped code, not shipping claims —
v2 only treats section headings as claims. Result: 0 SHIPPED warns.

---

## 3. The dead-literal dispositions — all of them

v1 reported 40 WARN = 33 dead-literal + 5 SHIPPED + 2 forks.
v2 scans **every** occurrence of the three dead literals (796,
85.5, 77.5) in plan/ + code: **41 occurrences** (v1 silently
suppressed 8 that were struck on their own line). Each is
classified against a ±4-line window, then by position. Classes:

- **FAIL — governing position, no local context (3).** Fundus req 2
  adopted: REGISTER and MASTER-PLAN are lookup books; a dead value
  printed as law in a governing zone is not saved by a strike
  hundreds of lines later. Corrections filed to Sana (letter,
  mail/sana/) — inline strike marks at the exact lines, record
  preserved, per REGISTER's own convention (L428–429) plus the
  marker the lookup reader actually meets.
- **WARN — real dead literal, no context anywhere in the doc (2).**
- **INFO — context in window (36):** the literal is quoted *as*
  dead — in a strike, a correction, a prohibition, an audit, a
  conditional. Each prints its class in the run output.

| # | where | literal | disposition |
|---|---|---|---|
| 1 | REGISTER.md:154 | 85.5 | **FAIL** — main body: "Operative geometry = … (segment 85.5dp, cap 77.5dp)"; strike lives at L453–456. → Sana |
| 2 | REGISTER.md:154 | 77.5 | **FAIL** — same line. → Sana |
| 3 | REGISTER.md:162 | 796 | **FAIL** — main body: "ROOM: fixed sum 796dp; reserve = H − 796"; strike at L431–436. → Sana |
| 4 | BEYOND-deck.md:282 | 796 | **WARN, real** — frame law "reserve = H − 796 (see W4 debt)"; no 821 or strike anywhere in the doc. → Sana |
| 5 | BEYOND-deck.md:577 | 796 | **WARN, real** — "Fixed sum is now **796**; reserve = H − 796" in the ROOM DEBT record; superseded by R9/B-51a (821) but never marked here. → Sana |
| 6–7 | DESIGNERS-PLAN.md:628–629 | 85.5, 77.5 | INFO — conditional variant: the V-TAB pair is Plan's filed alternative, expressly "only if Sarthak reverses the Ledger verdict". Not operative; no correction owed. |
| 8 | MASTER-PLAN.md:229 | 796 | INFO — corrected in place: "Corrected sum 821 … the 796 filed here was wrong". |
| 9–10 | MASTER-PLAN.md:5288, 5290 | 796 | INFO — §9 charges register quoting REGISTER's headline as an OPEN audit record ("the record: the Register's 796dp headline stands…"). |
| 11–12 | MASTER-PLAN.md:3084 | 85.5, 77.5 | INFO — amendment narrative: governing pair 73.5/65.5 in the same window; the dead pair is quoted as the draft's error. |
| 13 | MASTER-PLAN.md:3364 | 85.5 | INFO — prohibition: "a build that silently substitutes 85.5dp segments … fails this check". The literal is the forbidden value. |
| 14–16 | PLAN-AUDIT.md:52, 55, 56 | 796 | INFO — the audit finding itself (PA-S1-03), quoting both governing docs against the adopted 821 in-window. |
| 17 | PLAN-AUDIT.md:416 | 796 | INFO — B-51 closure record: "796 is dead; the fixed sum is 821". |
| 18–19 | PLAN-AUDIT.md:73 | 85.5, 77.5 | INFO — PA-S1-04 finding quoting the main body's "operative" pair against the amendment. |
| 20–22 | PROCESS-PROPOSALS-1.md:135 | 796, 85.5, 77.5 | INFO — P-MASSARBEIT-3 quoting PA-S1-03/04 (the waste this proposal exists to kill); governing values in-window. |
| 23–24 | PROCESS-PROPOSALS-1.md:139, 145 | 796 | INFO — strike context ("the 796/821 fork", "796 returns only death-r…"). |
| 25–29 | REGISTER.md:405, 407, 431, 432, 436 | 796 | INFO — the settlement record itself: "the centre is struck" (heading, L403), "821 governs … is STRUCK", "796 is dead". |
| 30–35 | REGISTER.md:194, 453, 455 | 85.5, 77.5 (×3 lines) | INFO — the correction and strike lines themselves ("was wrong"; "is STRUCK: segment 73.5dp · cap 65.5dp"). |
| 36 | RULINGS.md:274 | 796 | INFO — B-51a CLOSED record: "796 is dead; the fixed room sum is 821". |
| 37 | RULINGS.md:296 | 796 | INFO — R9 correction record, arrow form "room 796→821". |
| 38–39 | SLICE-D-E-BUILD-READY.md:362, 363 | 796 | INFO — "796 IS WRONG" (D12 heading) + the filed-debt derivation it corrects. |
| 40 | SLICE-D-E-BUILD-READY.md:599 | 796 | INFO — arrow correction "fixed sum 796 → **821** (R4)". |
| 41 | SLICE-D-E-BUILD-READY.md:707 | 796 | INFO — F19 historical ("the squeeze is how 796 happened"), corrected sum 821 in-window. |

Code scan: 0 occurrences in any `.kt` file (unchanged from v1).

Net: v1's 40 WARN resolve to **3 FAIL + 2 WARN + 36 INFO** (the
INFO set includes the 8 occurrences v1 never showed). The 3 FAILs
and 2 WARNs are the whole real residue, and all five lines are in
Sana's correction letter with their exact current text.

---

## 4. constants.json validated against REGISTER + code (A3)

Method: every charged value in constants.json was looked up in
REGISTER.md (the governing book), the specialist tables, and the
trunk code. Internal derivations (band segment, room min,
spectacle sum) still pass inside the checker.

| constant | verdict |
|---|---|
| arm.worst_case_seconds 8.0 | ✔ HUMANS-PLAN HPA-23 "8.0s worst hand"; MASTER-PLAN "worst case, 8.0s, governs" |
| slide.gate 0.68 | ✔ REGISTER slide §: "GATE: 0.68" |
| slide.catch_commit 0.84 | ✔ slide §: "CATCH / COMMIT: 0.84" |
| slide.off_stop 0.65 | ✔ slide §: "OFF-STOP: 0.65" |
| slide.travel_floor_dp 180 | ✔ slide § "TRAVEL: 180dp"; REGISTER needle "180dp stands as the FLOOR" present |
| **slide.gate_release 0.66** | **✘ DRIFT — no governing doc states a slide release value of 0.66. The only 0.66 anywhere in the governing corpus is Mara's specular coefficient (`0.16 + 0.66·t³`, REGISTER L277/L445). Almost certainly cross-contamination at constants v1's writing. v2 WARNs it on every run (slide-section-scoped check). Fix is Ren's: delete the field or name its source; constants.json is his file, so I report, not edit.** |
| band 73.5 / 65.5 / park 48 / total 342 | ✔ REGISTER amendment L193–197: (342 − 48) ÷ 4 = 73.5/65.5. (The main body's contradiction is §3's FAIL, not a constants drift.) Not yet in trunk code — band build is queued, so code absence is roadmap, not drift. |
| room 821 / lawful 841 / reserve 20 | ✔ REGISTER settlement L431–434 "821 governs … H = 841"; internal 821 + 20 = 841 ✔ |
| spectacle_freezes_ms [40,50,40,40] | ✔ REGISTER L111–112, item for item: slide commit 40 · final sub-step 50 · DONE landing 40 · bank catch 40 (sum 170) |
| type_ramp_sp [104,72,50,22,15,10] | ✔ TYPE-RAMP-TABLE: "Six sizes: 104 / 72 / 50 / 22 / 15 / 10". Code: Type.kt carries 104/72/22/15/10 as named styles; 50 is used inline (TactileDeckScreen hero spans). Values agree; the 50 tier not being a named style is an observation, not drift. |
| night palette (11 hexes) | ✔ every value present in REGISTER's colour amendment and in ≥2 trunk `.kt` files each |
| wheel.size_dp 288 | ✔ REGISTER "WHEEL: Ø288dp"; in code: TactileDeckScreen.kt `.height(288.dp)` (Slice B1) |
| wheel.stop_step 9° / 5 min | ✔ REGISTER "SCALE: 9° = 5 min" |
| wheel.dead_sector 36° | ✔ REGISTER L72 + L247 |
| wheel.lamp 245.2° | ✔ REGISTER L281 "Light vector θ_L ≈ 245.2°" |
| wheel.strait 108°→126°, flow_seat 108° | **PARTIAL** — width verified: 126 − 108 = 18° = REGISTER's "STRAIT: 18°". The absolute datum (FLOW seat at 108°) is stated in no governing doc I can find; it is contradicted by none. Flagged here so Ren/Sana can source it or strike it — v2 does not WARN it (a checker must not fail what it cannot source either way). |

One drift (gate_release), one unsourced datum (wheel 108°). All
other charged values trace.

---

## 5. Fundus's requirements — disposition

From mail/fundus 1025 (checker-findability-requirements), answered
in full in my reply letter to Fundus:

1. **Claim-block scoring + his four evidence forms — ADOPTED**
   (they are the v2 definition, §1 above; the §-pairing rule is
   his, the Home extension is mine and is reported separately).
2. **Position-aware severity — ADOPTED** (§3: governing position
   with no local context FAILs; REGISTER L154/L162 are the cases
   he named, and they fail).
3. **GOVERNS citation format — ARMED.** v2 verifies any
   `GOVERNS: plan/REGISTER.md:L34-68` line (file exists, range in
   bounds) and the keyword form `… "821 governs"` (keyword present
   inside the range — the bare form carries nothing to check a
   value against, so the keyword is the load-bearing part and the
   format should be quoted-keyword by convention). Corpus count
   today: **0** — reported every run, not failed; retro-failing a
   corpus for a format invented today would be theatre.
4. **READ-FIRST blocks in build-ready files — recommended, not
   imposed.** The checker verifies GOVERNS citations wherever they
   appear, so a build-ready file that opens with them is checked
   today. Making them mandatory is a Pipeline ruling for Ren/Takt,
   not a checker's to invent; it is in my closing letter as a
   proposal with the mechanism already built.
5. **ROUTINES as control case — answered in §2.** The FAIL stands
   for any brief with no reachable sites (proven on a synthetic);
   ROUTINES was never that brief.

---

## 6. What v2 does not do (named limits)

- Window classification reads ±4 lines; a strike recorded further
  than 4 lines from a *non-governing* hit with no other context
  still WARNs for human judgment — deliberate: the checker flags,
  the studio judges. In today's corpus this case does not occur.
- The evidence metric counts citation *forms*, not citation
  *truth* — a `File.kt:9999` that doesn't exist would still count
  STRONG. (Spot-verifying cited line ranges against the trunk is
  the natural v3 check; GOVERNS verification already does exactly
  this for governing-line citations.)
- Governing zones are defined for REGISTER and MASTER-PLAN only —
  the two lookup books Fundus named. Other plan docs' hits stay
  WARN-class.

— Massarbeit, Methods · spec translation
