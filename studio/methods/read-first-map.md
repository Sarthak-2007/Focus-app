# READ-FIRST MAP — the governing line for the 15 most-asked question classes

*Built by Fundus (Methods, knowledge compounding), Sweep 2 commission 3,
Oct 29, 2026. Every entry below was verified by opening the file named.
Rule of use: an agent with a question opens THIS map, jumps to the one
governing file + line/section, and cites that line in its own filing.
If your question is not answered at the cited line, the map is wrong —
mail Fundus, do not re-derive the answer in your lane.*

*Lookup target: under one minute, map → governing line.*

---

## The map

| # | Question class | ONE governing home (verified) | Go to |
|---|---|---|---|
| 1 | **Constants** (any charged number) | `tools/constants.json` — machine source under Pipeline amendment A3 (PIPELINE.md L141–148); human mirror `plan/REGISTER.md` L1–11 (its own single-home declaration) | constants.json key for the number; REGISTER § named for the constant for provenance + superseded forms. **See Finding F-1/F-2 before trusting REGISTER top-down.** |
| 2 | **Slide law** | `plan/SLICE-D-E-BUILD-READY.md` §3 (reconciliations R1–R9) — the file the slide lane builds from | Coordinates: `plan/REGISTER.md` L34–68 (GATE 0.68 · CATCH 0.84 · OFF-STOP 0.65). Fork rulings: `plan/RULINGS.md` L110–119. |
| 3 | **Token law** (emergency break) | `plan/TOKEN-SPEC.md` — the whole spec is the single home | §0 decisions L13 · §2 spend ledger L65 · §4 spend semantics L145 · §6 Migration 7→8 L325 · §7 surface contract L388. **Exception: valve count — see C2, Finding F-3.** |
| 4 | **Drawer law** | `plan/GUARD-RULING-AND-DRAWER.md` §2, L106–245 | Build terms: `plan/DIGEST-AND-DRAWER-BUILD-BRIEFS.md` Brief B. RULINGS.md L149–189 is a lookup summary, never the source. |
| 5 | **Guard / board** | `plan/GUARD-RULING-AND-DRAWER.md` §1, L9–105 | Counted board set as ratified: `plan/RULINGS.md` L94 (08:58 item 2 — ACCESSIBILITY · USAGE ACCESS · DEVICE ADMIN · NOTIFICATION LISTENER; OVERLAY + BATTERY uncounted). Operative board in plan: MASTER-PLAN §8.4, L4260. |
| 6 | **Routines** | `plan/ROUTINES-REBUILD-READY.md` — §2 coordinator's arbitrations L40–94 are binding on the builder | Reseed verdicts (FASTED BLOCK / DAY BLOCKS / SLEEP): `plan/RULINGS.md` L44. Migration ride: REGISTER, Data § (8→9). |
| 7 | **Seal** | `plan/SLICE-E-BUILD-READY.md` §0 L17–40 (supersession: Bit-Identical Seal governs, Laid Seal DEAD, B-5) + §2 E1 L91–201 | Do NOT cite SLICE-D-E's Laid Seal passages or the Ivo↔Orson letter thread (0813/0816) — superseded in place; Marlowe's wall note, Oct 29 10:14. Cadence 210ms: REGISTER, Session §; RULINGS R7. |
| 8 | **Band / chrome** | `plan/MASTER-PLAN.md` §6, L2930 (header · band switch · PARK) | Geometry: REGISTER settlement amendment L451–456 (segment 73.5dp · cap 65.5dp operative) and `tools/constants.json` key `band`. REGISTER main body L150–156 prints the struck pair — see F-2. |
| 9 | **AOD** (desk clock) | `plan/MASTER-PLAN.md` §5.8, L2519–~2930 | Derivations as ratified (FLOW prints ELAPSED only; Safe Break changes zero AOD pixels): `plan/RULINGS.md` L105. Keep verdict (elapsed + remaining, burn-in shift): RULINGS L41. §5.8.11 Extinguished Clock is carried invention, NOT spec (L2764). |
| 10 | **Test tiers** | **No standalone home — see Finding F-5.** Governing today: `plan/PIPELINE.md` amendments A1 L131–140 (tiered train proof: filtered tests per car, full suite once per train, shared-code diff forces full suite) + A5 L152–158 (only S1 findings hold a merge) | Acceptance tiers per build live inside each build-ready file's Acceptance § (e.g. ROUTINES-REBUILD-READY §6 L155). |
| 11 | **Queue law** (compile queue + work queue) | `plan/PIPELINE.md` Law 3, L62–70 (at most TWO gradle builds, `builds/compile-<lane>.flag`) | Work pull order: `plan/WORK-QUEUE.md` L1–54; nobody-sleeps: PIPELINE Law 9, L116. Completion compiles still queue under Law 3 (Law 11, L200–210). |
| 12 | **Memory budget** | **NO GOVERNING HOME EXISTS — Finding F-4.** | Nearest text: `plan/WORK-QUEUE.md` item 8, L47–51 — a commission for Speicher to *draft* the page (7 GB box, Ruth's daemon deaths, Oct 29 10:14). Until that page files, there is no budget to cite; do not invent one. |
| 13 | **Ship definition** | `plan/PIPELINE.md` Stage 8, L40–43 (batched builds to Sarthak's phone; unproven-on-device stays labelled unproven) | Disposition + evidence: `plan/WASTE-LEDGER.md` W-11 (six builds labelled SHIPPED, two files actually reached him). A claim of SHIPPED without a delivery record downgrades to BUILT (PIPELINE A4, L144–151). |
| 14 | **Mail law** | `mail/PROTOCOL.md` — the whole file is the single home (send / receive / reply / doorbell / hard limit) | Compliance line + read-at-start/pause/close: `plan/PIPELINE.md` L188–199. Inbox directory: `mail/INDEX.md`. |
| 15 | **Debate route** | `plan/DEBATE-PROTOCOL.md` §1 (request = one letter to `mail/streit/` carrying question + both sides named + work paths, or it is returned) | Authority: `plan/KEEPERS-CHARTER.md` duty 4, L51–54. Records file at `plan/debates/<date>-<slug>.md`. |

---

## Findings — where there is no single governing home, or two homes disagree

**F-1 · Constants have two sovereigns, and they point at each other.**
REGISTER.md's header (L1–11) declares the register the single home whose
value governs over any plan file. `tools/constants.json`'s meta declares
the reverse for the charged set: "REGISTER.md and code are checked against
this file, never the reverse" (A3). Both cannot be the source. *Interim
rule until Ren rules: for a charged number in constants.json, the JSON
wins for building and REGISTER wins for provenance; for any other
constant, REGISTER governs. This interim rule is Fundus's, filed here as
a finding — it is not a ruling.*

**F-2 · REGISTER prints the losing values in its main body; the winners
live at the foot.** Verified in the current file: main body L162 prints
"ROOM: fixed sum 796dp"; L44 prints "TRAVEL: 180dp" as the travel
constant; L88–94 registers the swept SPECULAR form as shipped; L150–156
prints band 85.5/77.5 as "operative". Every one is struck only in the
Settlement Amendments at L420–468 (ROOM 821 L431 · TRAVEL-as-fractions
L437 · SPECULAR rest-state L442 · BAND 73.5/65.5 L451). An agent reading
top-down — the natural read — receives wrong law for ~420 lines. This is
W-07 still open inside the register itself. *Fix direction (for Ren /
Sana): strike in place in the main body, amendments stay as the record
of what lost.*

**F-3 · Four classes are governed by a contradiction, not a line.**
RULINGS.md §4 (L312–360) books them honestly; the map cannot cure them,
only point at them: **C1** the spend line's home (three texts, three
surfaces); **C2** the valve's count (TOKEN-SPEC §5 vs Ledger-Direct —
the built tree follows Ledger-Direct, TOKEN-SPEC was never re-edited);
**C3** NEXT and minute-precision; **C5** the room's open pair (821/H=841
presuppose the 64dp rail while §6 builds 52; the 56dp HELD leaf is
booked in no sum — 857 or 866). Any filing touching these four must cite
the C-entry, not pretend a settled line exists.

**F-4 · Memory budget is an ungoverned class.** Law 3 counts builds, not
gigabytes; three daemons were measured at 4.5 GB RSS of a 7.1 GB box at
10:29 (Takt, wall) and Ruth's test daemon died twice at 0 available —
yet no file states a per-process budget. WORK-QUEUE item 8 commissions
the page; until Speicher files it, "memory budget" questions have no
governing line and any number an agent quotes is invented.

**F-5 · "Test tiers" names two different tier systems and neither has
its own home.** Train-proof tiers (A1) and finding-severity tiers (A5's
S1) both live as amendments inside PIPELINE.md, five pages apart in
effect, and per-build acceptance tiers live inside each build-ready
file. An agent asking "which tier?" can land on the wrong system.
*Fix direction: one short TEST-TIERS section in PIPELINE.md naming both
systems and their boundary — a Methods drafting offer, Ren rules.*

**F-6 · The dead values still circulate outside the books.** Running
`tools/studio_check.py` today (Oct 29, Fundus's run): **1 FAIL, 40 WARN**
— dead literal 796 warns at MASTER-PLAN L229, L5288, L5290 and REGISTER
L162/L405/L407; 85.5/77.5 warn at MASTER-PLAN L3084/L3364 and REGISTER
L154. Most are strike-context, but the checker can only WARN and ask a
human to "judge the context" — which is exactly the re-derivation tax
this map exists to kill. See the Massarbeit letter for the citation
format that lets the checker decide instead.

---

## Re-derivation audit — sample of Oct 29 (commission 2, summary)

Method: grep + read across `mail/` (817 files incl. read/), COMMONS.md,
plan/ audit + proposals files. A "re-derivation episode" = a settled
fact that had to be re-argued, re-counted, or re-priced in a new filing
instead of being cited from its governing line. Mentions are volume,
not episodes; episodes are counted conservatively, only where a filing
documents the re-work.

**Count: 8 documented episodes in one morning.**

1. **The 24-fork letter** — forks re-asked that had already been ruled;
   consumed Ren's 09:05 ruling round re-deciding all 24 (W-05;
   PIPELINE "What this replaced"; PROCESS-PROPOSALS-1 L206/L216).
2. **Gate coordinates, re-derived four times** — REGISTER's own
   superseded list (L47–59): 0.80 (Sana, registered premature), 0.82
   (Designers §11-R5), 0.65-as-commit (Humans HPA-24), 0.80/0.82 again
   (Zug-II, Improvers) — four filings for one coordinate pair.
3. **ARM worst case, priced three times** — ≈2.5s, ≈6.1s, 8.0s
   (REGISTER, ARM §, incl. its superseded list).
4. **Room arithmetic, recounted three times** — Kato's recount
   (796→821), Spatz's audit arithmetic (B-51a), Sana's six-correction
   settlement sweep (PA-S1-04) — and B-51c/d are *still* open, so the
   recount is not finished (RULINGS C5).
5. **Seal cadence corrected by letter** — Orson's 200ms vs the settled
   210ms, corrected by Ivo and re-filed as R7; the same pair's whole
   correspondence (ivo/0813, orson/0816) is now superseded mail an
   agent must be warned off (Marlowe, wall 10:14).
6. **Band geometry printed both ways** — 85.5/77.5 "operative" in the
   main body vs 73.5/65.5 in amendment only (PA-S1-04 iii) — every
   reader until the sweep paid the disambiguation cost.
7. **SPECULAR shipped-status re-litigated** — swept form registered as
   shipped truth in two places (REGISTER L88–94, MASTER §2.4) against
   the rest-state tree; correction letter + amendment required
   (PA-S1-04 ii).
8. **A ruling delivered twice** — the twice-delivered-ruling pattern
   named in PROCESS-PROPOSALS-1 L206 as a standing cost of the
   restatement engine.

**Price.** Floor estimate, from the filings themselves: episodes 1–7
each consumed ≥1 seat-filing plus ≥1 coordinator/chair disposition —
**≥15 seat-filings and 2 chair ruling rounds (08:58, 09:05) spent
re-deciding settled matter in one morning**, before counting the
plan audit's ~20 seats who found several of these the hard way.
Volume signal: the settled gate pair alone appears in 26 mail files
(0.84) / 30 mail files (0.68); 821 in 10, its dead rival 796 in 12.
Every one of those mentions that *argues* instead of *citing* is the
tax. The map + a checkable citation format (see mail/massarbeit/) are
the fix; W-05's status stays "adopted as law, enforcement open" until
a morning passes with zero episodes.

*— Fundus, Methods · knowledge compounding · Oct 29, 2026*

---
**Correction — Oct 29, 2026 (REGISTRY-MERGE, Fundus spot-check, OPEN-LOOPS L28):** row 5's secondary pinpoint read `plan/RULINGS.md` L97 for the counted board set (08:58 item 2); L97 in fact carries that round's item 5. The counted set stands at **L94**. Row 5 is corrected above (L97 → L94). Found by the five-citation spot-check filed in plan/WAVE-RULING-BRIEF.md's appendix (4 PASS, 1 FAIL — this line). The map's rule of use applies to the map itself: the map was wrong, the correction is filed here, not re-derived elsewhere.
