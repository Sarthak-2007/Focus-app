# PHASE-2 AUDIT — Vera + Una (Critics) vs DESIGNERS

**Auditors:** Vera — Critics, formal IA · Una — Critics, negative capability.
Coordinator: Hale (Critics); audited coordinator: Sana (Designers).
**Material audited:** `plan/designers-parts/plan.md` (Plan, IA seat — the Shield
composition of record), every seat file present in `plan/designers-parts/`
(19 seat files at audit time), `plan/TYPE-RAMP-TABLE.md`, and
`plan/LEDGER-designers.md` (Plan's verdict file, incorporated by reference in
plan.md §screen-map item 7).
**Standards applied (as charged):** TELL THE TRUTH once above all · ARM ≤10s ·
HOLD absolute · Ledger = DEMOTE (surviving forms: receipt + routine-context
footnote, exactly) · accent = COMMIT fill only (Shield = ZERO) · type ramp
heroes 44–112 / titles 18–26 / body 13–15 / labels 9–11, no two tiers within
1.4×. Our Phase-1 exhibits (D/C/S series) and CRITICS-PLAN §3 are the baseline;
Dieter-II's fact census (F1–F34), filed inside the Designers cohort itself, is
used as their own stated law where it is stricter than ours — a cohort is
audited against its own census first.
**Inboxes:** vera/ and una/ both empty at re-seating; nothing was waiting.

---

## 1. THE STRIP FACT-SPLIT RULING — the headline claim, tested element by element

**The claim (plan.md, hero statement + §3):** the routines time column is the
hero; the 24h Section strip is *"registration, not restatement: it carries
position-in-day, the rows carry everything else"*; §3's fact split: *"the strip
carries ONLY position-and-span-in-day. Names, days, durations, states live in
the rows and are NOT reprinted in or under the strip."* §2.2 strengthens it:
duration is *"stated here and nowhere else on Shield (not in the strip, not in
NEXT)."*

**Ruling: the claim is FALSE as stated. The strip restates three of the rows'
facts in a second channel, and plan.md contradicts itself in its own §3.**
Element by element — every fact the strip renders, against the rows:

| Strip element (quoted from plan.md §3) | Fact it renders | Where the rows already render it | Verdict |
|---|---|---|---|
| *"its blocks are the same routines as §2's rows, in the same left-to-right chronological truth as the rows' top-to-bottom order"* | Routine order / identity-by-position | Row order (§2.3: *"strictly chronological by start time"*) | **RESTATEMENT** — admitted in the sentence itself: "the same … truth" |
| *"A block's width = duration at true scale (content width ÷ 24h per hour)"* | **Duration** | §2.2 meta line: `DAILY · STUDY · 3H 00M` — *"DURATION printed. Duration is stated here and nowhere else on Shield (not in the strip …)"* | **RESTATEMENT + self-contradiction.** §3's width is §2.2's forbidden second rendering, specced two sections later. Geometry is a rendering (Dieter census preamble: *"Renderings include numerals, words, positions, fills … A chart of a list is a rendering of the list"*; F10 lists *"Section strip block lengths"* under duration's Must-NOT-appear) |
| Block's left edge on the 00:00–24:00 axis | **Start time** | §2.1 axis: start time `05:00`, *"Every start time in the column sits on this axis"* | **RESTATEMENT** — position-in-day *is* the start time, drawn. "Registration" is the name given to this restatement; a fact does not stop being rendered because the second rendering is spatial |
| Block's right edge | **End time** | §2.2 Line 3: `WINDOW OPEN — ENDS 08:00`; §2.3: *"the end time lives only in that line"* | **RESTATEMENT** — the right edge lives in §3. "Only in that line" is false the moment the strip is drawn |
| *"ON routine = filled block … OFF = hairline outline only"* | **Enabled state (ON/OFF)** | §2.4 miniature sled (position) + margin word `ON`/`OFF` | **RESTATEMENT** — F11's fact, second channel |
| *"auto-paused/ghost-prone = dashed outline"* | **Attendance state (paused / ghost-prone)** | §2.2 conflict line: `MISSED ×3 — AUTO-PAUSED`; and again in RECORD's attendance rows (LEDGER §3.1) | **RESTATEMENT, third rendering** — attendance is drawn in the strip, printed on the row, and printed again at the foot |
| SLEEP block *"drawn as two segments (20:00→edge, edge→03:30) joined by continuation marks"* | Overnight span / wrap | §2.3: SLEEP is *"one routine, one row"*; its span is the meta duration + the NEXT line's `ENDS 03:30` | **RESTATEMENT** of span; the continuation marks are the only genuinely new glyph — they encode "same routine, continued," which the one-row form already states |
| Now-line: *"1dp vertical line at the current hour … the row it touches already says WINDOW OPEN"* | Current time; by intersection, **which routine is live** | The phone's clock (current time); §2.2 Line 3 `WINDOW OPEN` (liveness, F12's one home) | **RESTATEMENT by intersection** — line × block is a liveness rendering whose home is the row. Plan's own defence ("the row already says it") concedes the strip adds nothing here except a second telling |
| Hour ticks `00 06 12 18 24` | Clock scale | Nothing in the rows — this is the strip's axis furniture | **NOT a restatement** — but it is furniture, not a fact in the split's favour |

**What the strip uniquely carries** (facts the rows do *not* state, and the
only honest defence available): the **gaps** between routines (uncovered hours
of the day), **overlaps** made visible at a glance, and **proportion of the
day covered**. Plan never claims any of these. It claims position-and-span —
precisely the two facts that *are* duplicated.

**Disposition:** the fact split is struck. Two lawful ends, Sana's pick, no
third: **(a) KILL the strip** — Dieter-II P11, filed inside Designers itself:
*"The 24h day strip (Kato's Section; Teo's Bedded Day — two geometries, one
disease). F9/F10 drawn a second time as blocks over the rows that already
state them — the Horizon bar's exact failure, rotated. Strike both; rows +
time column are the day."* We adopt P11 in full. **(b) RE-JUSTIFY it as the
gap/overlap rendering** — rename its job in the spec (it carries *the day's
holes*, nothing else), and strip its state channels: no fill/outline/dash
encoding of ON/OFF/paused (state stays on the rows), which also kills the
dashed attendance family (see V-04). What may not ship is the current text:
a hero claim ("registration, not restatement") that its own §3 refutes.

**Every number's source, checked (the part of the claim that holds):** the
reseeds' arithmetic is internally true — `05:00 + 3H 00M` = the NEXT line's
`ENDS 08:00`; `20:00 + 7H 30M` = `ENDS 03:30`; `09:00 + 10H 30M` ends 19:30,
clear of SLEEP at 20:00. Durations in meta, ends in NEXT, widths in the strip
all derive from one routine entity (`durationMinutes()` exists in the code,
per our S8). The numbers agree; the sin is not falsehood, it is *count* —
the same true numbers, rendered again.

---

## 2. VIOLATIONS TABLE

Quote = the audited file's own words. Owner = the seat whose file carries it;
Sana as coordinator owns every unreconciled pair.

| # | Quote (source) | Standard | Reasoning | Fix / Kill | Owner |
|---|---|---|---|---|---|
| V-01 | *"A block's width = duration at true scale"* (plan.md §3) vs *"Duration is stated here and nowhere else on Shield (not in the strip, not in NEXT)"* (plan.md §2.2) | TRUTH once | The spec forbids and then specs the same second rendering. Width is duration in geometry (Dieter F10 names strip block lengths under Must-NOT-appear) | Kill the strip, or kill the width encoding and the §2.2 sentence's claim with it — see §1 ruling (a)/(b) | Plan; Sana |
| V-02 | *"ON routine = filled block … OFF = hairline outline only; auto-paused/ghost-prone = dashed outline"* (plan.md §3) | TRUTH once | Enabled state (F11: sled + margin word) and attendance state (row conflict line + RECORD row) are re-rendered as fill pattern. Three renderings of attendance exist in one Shield plan | Under ruling (b): strip draws position/span only, in one neutral register for all routines; state never leaves the rows | Plan |
| V-03 | *"the end time lives only in that line"* (plan.md §2.3) — while §3 draws every block's right edge at its end time | TRUTH once | "Only" is false inside the same file. The axis start time is likewise drawn twice (text §2.1, position §3) | As V-01 | Plan |
| V-04 | *"MISSED ×3 — AUTO-PAUSED"* (plan.md §2.2 conflict line); *"`— PAUSED` only if auto-paused by attendance"* (§2.2 Line 3); *"3 misses → auto-pause is a decision that needs its data somewhere"* (LEDGER-designers §1.3) | Rested user decides (CRITICS-PLAN C3; Hale's letter to Sana, 06:07, already filed) | Auto-pause is the machine editing his routine — a decision taken at his tiredest, then confessed in three invented UI states (row line, PAUSED line, strip dash). The lawful form is the fact alone: `MISSED ×3`, his finger pauses or doesn't. We second Hale's kill-shot and extend it to the LEDGER file, which his letter did not cite | Strike AUTO-PAUSED as a state everywhere; conflict line keeps `MISSED ×3` and gains nothing; dashed strip family dies with it | Plan (both files); Sana |
| V-05 | Plan.md Shield spec, entire: sleds (§2.4, §4.3), *"Row tap … opens the routine editor"* (§2.2), `+ NEW ROUTINE` (§2.5), `+ ADD SITE` (§4.4), search — **no mid-session clause anywhere in the file**, under a header claiming *"HOLD absolute"* | HOLD absolute | Composition is a decision about reachability, and this composition decides nothing: every Shield control is specced live with no session state. Built as written, Shield ships editable mid-session — per-app sled loosening one row at a time (the §2.1 hole of our HOLD audit, retail instead of wholesale), routine ON/OFF and routine editing inside a seal. The seats know the law — Mikro §5: *"Mid-session (HOLD): app rockers inert … routine mini-sleds inert likewise"*; Stoff §2: *"during a session the sled is inert (HOLD)"*; Razor §0.7 locks the rail — the coordinator's Shield file omits what her seats remembered | Plan.md gains the clause, in its header block, verbatim in effect: while a session is sealed, Shield is read-only — sleds inert, editor/create/add unreachable, words remain as fact. One paragraph; its absence is the violation | Plan; Sana |
| V-06 | *"`186D TO NEET · TODAY 2H30 / 12H`"* (razor.md §0.2); *"NEET UG 2027 is unannounced. Assumption … exam = Sunday 2 May 2027"* (§0.3); typed again as *"Header war line (`186D TO NEET · TODAY 2H30 / 12H`)"* (set.md §2) and carried in TYPE-RAMP-TABLE with only a *"do not hardcode"* note | TRUTH once | Two lies in one masthead, on every screen: (1) TODAY is YPT's fact — Monolith cannot know study done outside it; any in-app rendering is an undercount dressed as a fact (Dieter F17/F18; LEDGER-designers §1.4: *"Study hours … YPT — never Monolith. Not duplicated"*). (2) 186D is computed from a date Razor concedes is assumed — a guess printed as a fact (our T-19/C5). Razor's stale-degradation (segment vanishes unsynced) concedes the first halfway | Kill both segments from chrome. If Sarthak orders a NEET count after a real announcement, its home is the Deck readout block, never the header (Dieter P1/P2, already mailed to Razor by Dieter-II — unanswered in the files) | Razor; Set (typed it); Sana (table carried it) |
| V-07 | *"`○ GUARD 3/4` … `● GUARD 4/4`"* at *"the war line's end"* (razor.md §3.1); Teen's board counts *"the permission family"* as ACCESSIBILITY / USAGE ACCESS / DEVICE ADMIN / **BATTERY** (teen.md §3.3) | TRUTH once; open charge C2 | The header pill our audit killed returns de-filled and calls itself a readout — Dieter's F29 gives readiness exactly one home, the Guard surface, and a header rendering is a second home whatever its fill. The denominator is again a typed literal. Worse, the counted set silently changed: the code's four (our C2: accessibility, usage stats, device admin, notification listener) become Teen's four with BATTERY substituted for the notification listener — same `/4`, different facts, no note anywhere | Header form dies; the count lives on the Guard surface only, built from the list's size, and the four are the code's four until a change is named as a change | Razor; Teen (board set) |
| V-08 | *"`12 SHUT · 3 PEEK` (OPEN count omitted — OPEN is the default of the world, not a Monolith fact worth ink)"* (plan.md §4.1) — then *"`OPEN — 41`"* in the group header (§4.2) | TRUTH once | Our S1 lie in miniature, self-inflicted: the census omits a tier on principle, then prints that tier one section later. The census now has two homes (§4.1 line, §4.2 headers) plus the grouped rows themselves. Either OPEN is a fact worth ink (print all three tiers once, honest tense — Dieter F16) or it is not (print it nowhere, headers carry words only) | One census line, all three tiers, one home; group headers lose their counts | Plan |
| V-09 | *"`3 ROUTINES` — (A count of what is below, stated once, here — never repeated in chrome)"* (plan.md §1) | TRUTH once (minor) | The rows below are the count — a list renders its own length (Dieter F21's logic for Tasks). "Never repeated in chrome" guards the wrong repetition; the repetition is the list itself, 20dp below | Kill the count; the title zone carries `SHIELD` and air | Plan |
| V-10 | *"ACCENT `#F55314` fill, word `COMMIT` in ON-ACCENT … on the swatch"* at the foot of Shield (iris.md §5); *"8×8dp squares … filled with the exact tone … orange `#F55314` (night: `#F47442`)"* (teen.md §2); the Guard plate prints the code a second time as a spec line (teen.md §3.5) | Accent = COMMIT fill only; Shield = ZERO; TRUTH once (legend *"printed exactly once"*, plan.md §5) | Plan's headline is *"Shield accent = ZERO, everywhere, no exceptions"* — two of her seats then print an actual orange fill on Shield and call it a specimen. Iris flags her own carve-out honestly (*"§3's Shield ZERO refers to live controls; the legend swatch is … print, not … a live fill"*), but ZERO with a footnote is not zero: the eye does not audit categories, it sees orange on Shield. Teen compounds it: the code is printed twice in the app (Shield key + Guard plate line), against plan.md's *"Printed exactly once in the app"* — Teen concedes the plate hit is *"the second and only other permitted hit,"* i.e. the once-claim is false in his own file. Note Plan's own legend form (§5) threads it correctly — the orange entry is a dash, `— ORANGE = COMMIT`, no orange ink spent | Legend swatch for COMMIT carries no fill (Plan's dash form is the surviving geometry, app-wide); the plate's code line dies or the once-claim dies — pick, and print the pick | Iris; Teen |
| V-11 | *"Armed lamp: Ø3dp dot, orange (day `#F55314` / night `#F47442`) … visible only while a session is armed/running. The sole orange in chrome"* (razor.md §0/§0.4 table); repeated mikro.md (*"ARMED LAMP … It is the ONLY orange in chrome"*), ton.md (*"The 3 dp armed lamp at FOCUS is the only orange in the rail"*), pit.md (*"accent: NONE — except the armed dot"*) | Accent = COMMIT fill only | A lamp is not a commit. The commit already happened at the slide/START; this dot spends the commit colour on a *state*, for the whole length of every session, in chrome — and chrome rides over Shield, so Shield-as-displayed is not zero either. Iris §1.4 states the correct law against her own cohort: *"NONE during the hold. The commit already happened."* Dieter's sign-off excepts the lamp by name — a declared exception, not a smuggle, and we credit the declaring; it still fails the standard we were charged to apply | Kill the lamp, or re-ink it (glyph tone — the AOD armed dot already renders in GLYPH `#EDE3D8` per Iris §1.5; chrome can do the same). Armed state has words and the sealed bar; it does not need the verb's colour | Razor (author); Mikro, Ton, Pit (repeaters) |
| V-12 | *"on completion of the hold, the state word `BREAK SPENT` may render in `#FF8A5C` — a word, never a fill"* (iris.md §1.7/§4.3), approved by Nacht (mail 0533; nacht.md GAP-N6) | Accent = COMMIT fill only | Orange-family *text* — the form Liv and Dieter both ban outright (*"orange type banned everywhere"*) — spent on the anti-commit: the one act that un-commits. Nacht's approval is an internal ruling, argued and bounded (night only, ≤2%, two placements); bounded and argued, it remains accent-as-text for a non-commit under the binding standard. Floor's contrast table even grades the hex *"PASS even as text,"* normalising it | The word renders in TEXT like every other state word; form (weight/position/surface step — Iris's own §4.1 machinery) carries the irreversibility | Iris; Nacht (approver) |
| V-13 | *"the earned segment's fill steps from live accent α180 to solid control-fill"* at bank events (saft.md); *"LIVE chamber (currently filling): accent fill at α180 — the bar's only orange"* + *"DRAINING fill: diagonal hatch (4dp stripes, accent α180 / transparent)"* (mikro.md) | Accent = COMMIT fill only | The emergency hold — the anti-commit surface — carries live orange for the entire 60 seconds. This directly contradicts Iris §1.7 (*"NONE as a fill"*) inside the same cohort: two seats spec the hold orange, one specs it zero, and no file reconciles them | The chamber fills in ink/control tones (banked = solid, live = hatch or step — Floor §3 already proves the shapes read without colour: *"solid vs glowing is a silhouette difference"*). Accent leaves the overlay | Saft; Mikro |
| V-14 | Plan.md Shield sizes, complete set: title `20sp` (§1), axis `15sp` (§2.1), name `18sp` (§2.2), meta `13sp` (§2.2), NEXT `11sp` (§2.2), words/heads/legend `10sp` (§§2.4/4.1/5), ticks `9sp` (§3), site sub-line `11sp` (§4.4) | Type ramp: no two tiers within 1.4× | One screen runs **seven sizes {20, 18, 15, 13, 11, 10, 9}** — more than Set's ladder allows the *entire app* (six). Every adjacent pair is illegal: 20/18 = 1.11, 18/15 = 1.20, 15/13 = 1.15, 13/11 = 1.18, 11/10 = 1.10, 10/9 = 1.11. TYPE-RAMP-TABLE strikes exactly two of these (18 → 22, RECORD 44 → 50) and leaves the rest standing, so the table and the Shield spec cannot both be built | Plan.md is re-struck onto the ladder {22 / 15 / 10} for every Shield role, or Sana rules in writing which Plan sizes amend the ladder. Silence is not a ruling | Plan; Sana |
| V-15 | *"`ON` / `OFF`, Anonymous Pro Bold 10sp"* (plan.md §2.4); app margin words `SHUT`/`PEEK`/`OPEN` *"in the same 10sp register"* (§4.3); NEXT line *"Anonymous Pro 11sp"* (§2.2) | Type ramp discipline (table: *"State words … Atkinson 700 15sp"*; *"the moment a word carries a decision or a state it is promoted to 15sp"*); TRUTH (a state word is a channel) | Shield's most consequential words — the rested decisions themselves — are set at plate size, the tier the table reserves for furniture. Floor §3, their own annex: a word below the floor *"is not a channel, it's a rumour of one."* Plan whispers the truth and engraves the furniture at the same size | State words and NEXT at 15sp per the table; plate returns to furniture only | Plan |
| V-16 | RECORD hero: *"at 44sp Sora"* (LEDGER-designers §3.1) vs *"Ledger hero stat 72"* (set.md §2/§4) vs *"RECORD hero … 50 †(Plan's 44 illegal, Set's 72 was duration-shaped)"* (TYPE-RAMP-TABLE); RECORD rows *"body tier 13sp Atkinson"* (LEDGER §3.1) vs table receipt lines at 15sp | Type ramp; TRUTH once (one fact, one size) | One number, three sizes, in three files of one cohort — the RECORD hero is rendered three ways *in the plan itself* before a line of code exists. Sana's table ruling (50) is correct and cures it **if** the table is binding; neither source file is re-struck, so a builder reading the verdict file builds 44 | Re-strike LEDGER-designers §3.1 (hero 50, rows 15) and set.md's Ledger rows; the table's † marks become deletions, not annotations | Plan (LEDGER file); Set; Sana (cure incomplete) |
| V-17 | *"(iii) Chrome corner — the record invoked from chrome itself: the word `LEDGER` … at the right end of the war line"* (razor.md §1.2); drawer *"4. (Only in the §4-resolved V-DEMOTE combination) `RECORD` → opens the record sheet"* (§3.1) | Ledger = DEMOTE, exactly two forms | As filed, Razor specs **two extra doors** to the record beyond receipt + RECORD footnote. His §4b addendum kills both (*"The chrome corner form (iii) is NOT named — it dies … no RECORD row is needed in the PARK drawer"*) — we credit the self-kill in full. It remains a contained violation, not a clean pass: the specs stand in the body of the file, and a build stacked as §0 + §1.2 + §3.1 without the addendum ships them | Delete §1.2(iii) and §3.1 row 4 from the file body; an addendum is not a strike-through | Razor |
| V-18 | *"if the Ledger returns as a surface, synthesis item 12 already budgets it exactly one accent (the accession №). This map neither grants nor spends it"* (iris.md §3, footnote) | Ledger = DEMOTE; accent law | A dormant third form kept warm in a footnote: a returning Ledger, an accent spend, and an accession number — the counter Dieter P8 and Old Tom D9 both kill as *"an archive in disguise."* A demote that budgets the tab's return has not finished demoting | Strike the footnote. If Sarthak re-opens the Ledger, it is re-argued then, not pre-budgeted now | Iris |
| V-19 | Guard plate line: *"STUDY RECORD: IN YPT · SESSION RECORD: THE LEDGER"* (teen.md §3.5) | Ledger = DEMOTE | The plate — print that *"is identical on day 1 and day 100"* — names a dead destination as the session record's home. Under the demote there is no THE LEDGER; the record lives as receipt + Shield-foot RECORD. Permanent print carrying a superseded IA is a lie with a long half-life | Plate line reprints as `SESSION RECORD: RECEIPT + SHIELD FOOT` (or drops the second clause — the plate *"keeps no numbers"* and needs no record address) | Teen |
| V-20 | *"Furniture ≤ 35% of the first viewport. First viewport contains: title zone + all three routine rows + the Section strip + list head/search — the apps themselves begin at or below ~65% of the first screenful"* (plan.md §4.5) | TRUTH (the spec's own arithmetic); ARM's spirit (cost placed where it belongs) | The paragraph refutes itself: if the apps begin at 65%, furniture is ~65%, not ≤35% — the budget is failed by the spec's own inventory (title 52 + gap 20 + rows 216 + strip 48 + break 32 + head/search ≈ 400dp+ before the first app row). Shield does not tax the arm (see Passes), but it taxes its own primary job: the app list — the thing he opens Shield to tend — starts below the fold behind the hero, the strip, and a sticky search | Either the budget is restated honestly (≈65% furniture, defended as routines-first) or the composition changes: strip dies (V-01) and search un-sticks, and the number is recomputed and printed once | Plan |
| V-21 | One part, four specs: routine/app sled track **36×20dp, cap 16×16** (plan.md §2.4) vs app track **112×34, cap 36×30** (stoff.md §2) vs app sled **168×36, cap 54×30** (mikro.md §5) vs **96×32 / 56×32** (raster, per floor.md §1); app row **52dp** (plan.md §4.3) vs *"64dp tall"* row module (stoff.md §3); search **36dp** (plan.md §4.1) vs *"48dp tall"* (stoff.md §2); legend swatches **12dp r8 squares** (plan.md §5) vs **8×8 r0** (teen.md §2) vs **sled-thumb miniature** (iris.md §5) | TRUTH (a plan that disagrees with itself has no buildable truth) | A builder cannot build "the sled" from this corpus — four widths, three heights, two row modules for identical elements. Each file is internally coherent; the cohort never converged the part. Floor already FAILs three of the four heights on target law | Sana names one sled geometry in the merged plan; the other three files are re-struck or marked superseded in place | Plan / Stoff / Mikro / Raster; Sana |
| V-22 | *"Size: 36dp wide × 20dp high … Cap: 16dp × 16dp"* (plan.md §2.4); *"Search: sticky, 36dp high"* (§4.1) | ARM's cost law applied to tending (48dp target floor — Floor §1, the cohort's own annex) | Floor, Designers' own accessibility seat, FAILs both in writing: the routine sled is *"the smallest target in the entire plan, on the control that pauses a routine"*; the search field is *"12dp under floor."* Plan.md was not re-struck after its own annex landed. Not an arm-path cost — a tending cost, charged at 03:30 (see §3) | Hit boxes ≥48dp (visual may stay, per Latch's pattern Floor praises); search 48dp | Plan |
| V-23 | *"Loadout band STUDY / DEEP / SLEEP"* (mikro.md §5; also kin.md M-11, ton.md row 13, teen.md §1, floor.md §3) — while plan.md contains **no loadout band at all** and never names its death | TRUTH; open charge S2 (DEEP) | See §4, charge 3: DEEP survives in five files. Compounding it, the band's fate is undecidable from the corpus — Dieter F13 keeps the band as loadout identity's home; Plan deletes it by silence; Mikro/Kin/Ton keep throwing its cap. Three incompatible Shields | Sana rules the band in or out, in the merged plan, in words. If out: DEEP dies in all five files and F13's home is reassigned. If in: its stops print STUDY / WORK / SLEEP (our S2 fix) | Mikro / Kin / Ton / Teen / Floor; Sana |
| V-24 | *"SHOWING"* — plan.md §4 specs a static head (*"`APPS & SITES`"* + counts) and state groups, but **no filtered-state title**; search field kept while Dieter §2A rules *"a 12-item list gets one retrieval apparatus … the text field dies"* | TRUTH (our S10: *"Title follows the state"*) | Under search/filter, Plan's head still claims the whole list's name over a subset — S10's lie in its quiet form. And Shield carries two retrieval apparatuses (text search + state grouping) against the cohort census's one-apparatus rule; nobody argues the exception Dieter demands (*"reverse it only with a written reason"*) | Head follows state (`SHOWING n OF m` while filtered, per F34); the search-vs-grouping exception is argued to Sana in writing or the field dies | Plan |
| V-25 | Deck/Tasks ramp adoption (pattern evidence for V-14): readout *"Anonymous Pro Bold 104sp"* on the Deck (vau.md) vs table *"104 is AOD-only"*; Footing *"Sora Bold 44sp"* (druck.md) vs table 50; rim numerals **13sp** (uhr.md) vs **15sp** (set.md) vs *"plate 10sp or deleted — never 15"* (dieter-ii P7) | Type ramp | The ladder is obeyed exactly where Set holds the pen. Everywhere else the cohort specs from memory: three sizes for the rim alone. The table's † strikes (Vau 104, Druck 44) exist precisely because the files were never re-struck | Same cure as V-14/V-16: strikes become deletions in the source files, or the merged plan lists every superseded size by file and line | Vau; Druck; Uhr; Sana |

---

## 3. ARM ≤10s — does the Shield composition move the cost?

**Verdict: Shield does not tax the arm. It taxes tending, and its own
furniture accounting (V-20). The arm-path discipline in the wider corpus is
genuinely strong.**

- Plan.md puts nothing on the arm path: no Shield element gates, precedes, or
  prices the Deck slide. Routines and sleds are rested-user furniture, specced
  as such. The composition's ARM exposure is zero — as it should be, and we
  say so plainly.
- The re-spec tax from our Day8 charge (dial/blocks unpersisted, ~8s of the
  10s budget on an ordinary re-arm) is answered on paper: Vau's Deck *"idles
  at the last session, armed-ready: on a normal day the wheel is touched zero
  times and the slide is the one act."* That is the fix we demanded, specced.
- Teen's FIX line carries its own ARM note — *"FIX is not on the arm path"* —
  and Razor's header GUARD readout *"is a readout, not a target: tapping it
  does nothing,"* so readiness never spends the slide (our A-1) in any
  Designers file. Dieter P12 strikes even a truthful readiness poll from the
  arm path. Clean.
- The costs Shield *does* move: onto the rested-but-tired tending hand —
  a 36×20dp routine sled with a 16dp cap (V-22), a 36dp search field, and an
  app list pushed below the first viewport (V-20). None of these touch the
  10s budget; all of them touch the 03:30 check of *"why didn't it fire."*
  Composition honesty requires naming them as costs of this layout, not
  discovering them in the build.

## 4. HOLD absolute — reachability, settled per file

**Verdict: the seats hold; the Shield composition of record does not say.**

- **Holds, in writing:** Mikro §§4–5 (sleds/rockers inert, *"no dip"*),
  Stoff §2 (sled inert, *"a press is answered with stillness"*), Razor §0.7
  (*"the rail locks … PARK detent is inert … no drawer, no Guard detour, no
  Ledger peek"*), Pit (the running screen as *"cloister: nav locked, editing
  locked"*), Iris §1.4 (no operable raised surface mid-session), LEDGER §3.1
  (RECORD: *"Interaction: none"*).
- **Fails by omission:** plan.md (V-05) — the one file a builder treats as
  Shield's composition. Every other HOLD-correct file is a part-spec; the
  composition that assembles them never closes the door they each describe.
- **One residue:** Razor §0.7 keeps the war line *"live (TODAY accrues)"*
  mid-session — not a decision surface, so not a HOLD breach, but a live
  YPT rendering inside the cloister, and it dies with V-06 anyway.

## 5. PASSES — their discipline, where it is real

The impression of strength survives audit in these places; we prove it by
naming what we tried to break and couldn't:

1. **Duration-once inside the rows (Plan §2.2).** The meta line is duration's
   only *text* home on Shield; NEXT carries no duration; the reseeds'
   arithmetic verifies end-to-end (§1, source check). The intention is
   exactly our T-11 fix. The strip betrays it (V-01) — the row discipline
   itself passes.
2. **WINDOW OPEN, never LIVE (Plan §2.2; Mikro §5 ARMED; Dieter F12).**
   Our S8 complaint — rows never said LIVE/NEXT — is answered with the
   correct word and the correct refusal: *"a routine window is not a session
   and the copy does not pretend otherwise."* Three files, one wording, no
   dissent.
3. **The `2 ACTIVE` lie is dead (Plan §1/§2).** No header counts enabled as
   active anywhere in the Shield plan. S6 is closed on paper.
4. **Conflicts live on the row they accuse (Plan §2.2)** — *"never a badge,
   never orange, never in chrome."* The placement law is right; only the
   auto-pause content fails (V-04).
5. **Dieter's census closes our Deck charges on paper** (F1 hero total,
   F2 pointer-only block length, F4 FINISHES killed outright — stronger than
   our replacement — F26 receipt form, F27 deflections nowhere). See §6.
6. **The demote's main line is exact.** Plan's screen map, Dieter §2C,
   Old Tom §4, Mikro's receipt states, and Razor's §4b final stack all land
   on the same two forms — ephemeral receipt + non-interactive Shield-foot
   RECORD — with chrome at zero record data and absence rendered as absence
   (*"`HOLD — UNSPENT` (a word, not a zero)"*; hero absence as `—` /
   `NOTHING HELD YET`). LEDGER §1.4's fact table is the cleanest
   one-fact-one-home mapping in the wave. The violations (V-17/18/19) are
   edges and footnotes, not the verdict.
7. **Accent in the main line is one fill per committing screen, zero
   elsewhere:** Vau (slide only, *"zero orange on the wheel"*), Druck
   (START only; *"ADD is ink, never orange"*), Nacht (gate fill = the
   slide's own commit), Iris §3's audit table (Deck 1 / Tasks 1 / all else
   0). The failures are named exceptions (V-11/12/13) and the legend
   (V-10) — deviations from a map that is otherwise correctly drawn.
8. **Set's ladder arithmetic is clean.** {104, 72, 50, 22, 15, 10}: every
   ratio ≥1.44, every size inside its band, the impossibility proof for a
   seventh size shown, and the Atkinson "Medium" fraud — a weight that does
   not exist — caught and named in Type.kt. The ramp fails by adoption
   (V-14/16/25), never by construction.
9. **Floor's annex is real internal QC.** The cohort's own accessibility
   seat FAILed Plan's sled, search, and type before we opened the files,
   named owners, and mailed them. A cohort that audits itself this hard
   has earned the passes above being believed.

## 6. OPEN-CHARGE STATUS (from vera-una-truth-deck-shield.md / CRITICS-PLAN §3)

| Charge | Status after Designers' plan | Evidence |
|---|---|---|
| **Five disagreeing duration renderings** (D1–D5; the readout/caption/badge/FINISHES/% pile-up) | **CLOSED on paper** | Dieter F1 (total = Deck hero only), F2 (block length = pointer vs printed scale, no numeral), F4 (FINISHES killed outright — *"a derived time printed beside its own inputs is a second rendering"*), Vau hero TOTAL, Set's single 72 hero. Caveat: closed in spec, not in build; Vau's 104 (V-25) must die for the closure to survive contact with the Deck spec |
| **Drawer "DEFLECTED" with no number** (C4) | **CLOSED on paper** | The dangling word dies with its fact: Dieter F27 (lifetime deflections NOWHERE — *"a vanity score"*), LEDGER §1.4 (*"a lifetime deflection counter … is killed outright"*), Razor's drawer rows carry permissions/sound/theme only. Per-session deflections survive solely on the receipt (F26), with a number, once |
| **The DEEP / WORK_SPRINT mislabel** (S2) | **STILL OPEN** | V-23: Mikro §5, Kin M-11, Ton row 13, Teen §1 and Floor §3 all still print the band as STUDY / **DEEP** / SLEEP, applying the loadout the code calls Work Sprint. Plan's silent deletion of the band does not close a charge that five other files keep alive |
| **GUARD n/4's hardcoded denominator** (C2) | **STILL OPEN** | V-07: Razor re-hardcodes `GUARD n/4` as a header readout; the counted set itself changed silently (Teen's BATTERY for the notification listener). No file builds the fraction from the list's size, and the count still has two homes (header + board) |

Two closed, two open. The two that closed are the two the Designers' own
census aimed at; the two that remain are the two nobody in the cohort owned.

---

## 7. MAIL SENT WITH THIS AUDIT

Kill-shots mailed per protocol, same thread discipline as this file:
Sana (strip ruling + V-05 + V-04 extension + ramp adoption), Plan (V-01–V-05,
V-08, V-14/15, V-20–V-22, V-24), Razor (V-06, V-07, V-11, V-17),
Iris (V-10, V-12, V-18), Teen (V-10, V-19, V-07 board set),
Mikro (V-11, V-13, V-23).

— Vera (Critics, formal IA) & Una (Critics, negative capability)
