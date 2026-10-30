# FLOOR — Accessibility seat, Designers cohort (coordinator: Sana)
## PLAN WAVE — THE FLOOR AUDIT (annex). No code.

Citation requirement lifted by Sarthak's direct order — this annex cites
nothing. What binds is settled law, and my floors are load-bearing in it:
any plan element below that trades against them **loses**, whoever owns it.
Violations are named as violations, addressed to the owning seat, and also
mailed to that seat's inbox.

**THE FLOORS (wave-1 C10, seconded across cohorts):**
- Nothing functional < **12sp** · nothing essential < **14sp** ·
  letterspaced caps pay a **+1.5sp tax** (tracked-caps functional floor is
  therefore **13.5sp**, essential **15.5sp**).
- **48dp targets everywhere**, both dimensions, no exceptions smuggled as
  "he's young." The hold target: **≥88dp in its short dimension**.
- Every position/colour state gets a **WORD** and a **SHAPE** as second
  channels — position + colour alone is never a state.
- Contrast, from the locked palettes only (verified §5, not re-derived):
  day text ≥4.5:1 on the surface it sits on · night tiers **11:1 / 5.8:1 /
  3.5:1** — the 3.5:1 tier carries **NOTHING essential, ever**.
- The user states that set the bar: **03:30 half-awake, one hand ·
  test-eve panic, shaking hands · 1 AM depleted.** Every verdict below was
  taken against the worst of those three, not the best.

Verdict key: **PASS** · **FAIL** (violation — fix named) · **CONDITIONAL**
(passes only under the stated condition) · **UNPLANNED** (no size specced
in the plan wave — an unspecced target is not a pass).

---

## 1. TARGET-SIZE TABLE — every interactive element in the plan

| Element (owner) | Planned size | Verdict |
|---|---|---|
| **Wheel — drag surface** (Rad / Vau) | Ø312dp disc (Vau); Rad's drag annulus ≈64dp radial band, inner dead zone | **PASS** — the whole disc is the target; a resting palm sets nothing. The drawn 14dp thumb dot (Rad) is a marker, not a target — PASS only because nothing asks a tap on it. |
| **Hub / BLOCKS cap** (Nabe) | Peak: touch Ø88dp, visual Ø64dp, 32–44dp-radius annulus is a dead rest zone · Rung 3: Ø88dp visual = touch · Shipped geometry Ø112dp drawn / ~Ø98dp touch | **PASS** at every rung (≥48 both dims; touch = drawn at Rung 3+, the honest form). |
| **Slide-to-arm — TRACK** (own line) | Vau: 72dp tall × full content width (350dp) · Zug-II A3: 72dp · Raster: 64dp | **PASS** vs 48dp (all three). The three heights disagree (72/72/64) — Raster's 64 is lawful but is the odd one; converge on **72**. |
| **Slide cap / thumb** (own line, cont.) | Vau: 84 × 56dp · Zug-II A3: 80 × 60dp · Raster: 48 × 48dp thumb in a 64dp track | Vau **PASS** · Zug-II **PASS** · Raster **CONDITIONAL** — 48×48 is exactly the floor with zero margin for a shaking thumb on the app's one committing gesture; Raster: grow the thumb or accept Vau/Zug geometry. |
| **App sleds — SHUT/PEEK/OPEN** (Schlitten) | Sled 168 × 34dp → Rung 4: 168 × 36dp, cap 54 × 30dp, inside 52–54dp rows | **FAIL — violation, to Schlitten.** The sled is a drag/tap control in its own right; 34–36dp tall is 12–14dp under the floor. The 52dp row does not rescue it — a thumb aiming at the sled hits the sled's box, not the row's. **Fix:** sled touch box extends to the full row height (52dp) with ≥48dp per-stop hit width (168 ÷ 3 = 56dp wide already passes), visual sled unchanged. |
| **Routine sled / pause switch** (Plan) | Track 36 × 20dp, cap 16 × 16dp, inside a 72dp row | **FAIL — violation, to Plan.** 20dp tall, cap 16dp: the smallest target in the entire plan, on the control that pauses a routine — a rested-user control, but rested ≠ precise at 03:30 when he checks why nothing fired. **Fix:** the whole 72dp row's right zone is the switch target (≥48 × 48dp hit box, visual track may stay 36 × 20 only if the hit box is real and focussable). |
| **Loadout sled** (Raster / Plan family) | Raster: 96 × 32dp standard sled · mini-sled 56 × 32dp | **FAIL — violation, to Raster.** 32dp tall, both forms. Same fix as Schlitten: hit box ≥48dp tall (the Z40 zone it sits in is itself only 40dp — the zone must grow or the hit box must overlap air, not neighbours). |
| **FLOW key** (Latch) | Touch box grows 42 × 26 → **72 × 48dp** at peak (visual cap 56 × 32dp inside) | **PASS** — the model fix: visual size and touch size specced separately, touch at/above floor in both dims. Every other seat should copy this pattern. |
| **Composer keys / key-bank caps** (Taste / Fach) | **No size specced in any plan-wave file that has landed** | **UNPLANNED — to Taste and Fach.** No pass is available. Floor for their files: every key ≥48 × 48dp touch (ADD already passes, below); a key-bank drawn at keycap scale with a smaller touch box is Latch's shipped bug, not a style. |
| **Composer field + ADD** (Raster) | Field 48dp tall · ADD row: field 276 + ADD 84dp wide, row 48dp | **PASS** (exactly at floor on height — no shrink in build). |
| **Nav stops — band switch** (Razor) | Rail 52dp tall, track inset 4dp → **cap 44dp tall**; stop widths 65.5–106dp | **FAIL — violation, to Razor.** Widths pass easily; the cap — the thing the thumb actually lands on — is **44dp tall, 4dp under the floor**, on the control he hits half-awake to change screens. **Fix:** rail 56dp, inset 4dp, cap 48dp. Nothing else in Razor's geometry changes. |
| **Nav stops** (Raster variant) | Rail 64dp, cap zone 48dp, stop width 64dp | **PASS** — Raster's rail is the lawful one; Razor and Raster must converge, and they converge on Raster's height. |
| **PARK detent** (Razor §3.1) | Reserved zone **44dp wide** inside a 44dp-tall track; opens by tap or cap-drag | **FAIL — violation, to Razor.** 44 × 44dp: under floor in *both* dimensions, and it is the door to permissions — the one drawer he may need on a broken morning. **Fix:** PARK zone ≥48dp wide and the rail fix above gives it 48dp tall; the 4dp it steals comes out of the stops track (segments 98 → 97.3dp at 3 stops — the words still fit). |
| **PARK drawer rows** (Razor §3.1) | Rows 52dp tall, whole row is the target | **PASS** on size. (Its state words fail on type — §2.) |
| **PARK drawer grab strip** (Razor §3.1) | 32dp tall drag strip | **FAIL — violation, to Razor.** Drag-down dismiss is a gesture target at 32dp. **Fix:** grab strip hit height 48dp (visual score line unchanged), or whole-sheet drag. Scrim-tap dismiss already exists as the second path — keep both. |
| **Bottom sheets — grab zone** (Raster) | Grab zone 20dp tall, grabber 48 wide | **FAIL — violation, to Raster.** Same defect as Razor's strip, worse. **Fix:** 48dp hit height. Sticky action foot 72dp with a 48dp action — **PASS**. |
| **Routine rows** (Plan) | 72dp rows, full row (minus sled) opens editor | **PASS** with 24dp to spare. |
| **App / site rows** (Plan) | 52dp rows | **PASS** on height. The sled inside them fails — see app sleds above. |
| **Search field** (Plan) | **36dp high**, sticky | **FAIL — violation, to Plan.** A text field he must tap precisely to type into, 12dp under floor. **Fix:** 48dp. It is sticky and always on screen — it can afford the height. |
| **`+ NEW ROUTINE` row** (Plan) | 52dp high | **PASS** on size. (Its type fails — §2.) |
| **Task rows — queued one-liners** (Zeile) | **44dp** one-liners, tap = promote to ACTIVE | **FAIL — violation, to Zeile.** A tappable row at 44dp. **Fix:** 48dp pitch (44 + 4dp air is already in her gap system — spend it in the row, not between rows). |
| **Task rows — action words DONE/SPLIT/DEL** (Zeile) | Bare `Text.clickable` today; her Rung 1 padding → **≈40dp hit height** | **FAIL — violation, to Zeile.** Her own fix stops 8dp short of the floor. **Fix:** vertical padding to a true 48dp hit height; the words are verbs he hits with a shaking hand on test eve. |
| **Task step rows** (Zeile / Linea) | Zeile: 28dp per step · Linea: 36dp visual / **48dp effective** via padding | Zeile **FAIL** (28dp toggle targets) · Linea **PASS** — Linea's visual/effective split is the correct pattern; Zeile adopts it or grows the step grid to 48. |
| **Deck task line** (Linea) | Main row min-height **48dp**; DONE/QUEUE merged into the row target at her top rungs (Rung 1 alone was ~40dp) | **PASS at the top rung** — and a standing warning: Rung 1 shipped alone would fail. The ladder must not stop at Rung 1. |
| **Deck task line** (Vau variant) | Task line 20dp high, NEXT line 14dp high | **FAIL if tappable — to Vau.** If either line opens Tasks, 20dp is a violation. If both are pure print, they are not targets and this row is N/A — Vau must say which, in the file. Silence reads as tappable. |
| **Shield GUARD › word** (Razor §3.2) | Label plate with a stated **48dp target** | **PASS** — explicitly specced; the pattern PARK and the nav cap should have had. |
| **Valve / HOLD TARGET** (own line — §4) | Zug-II B3: bar **68dp tall**, full width; chambers ≈84dp wide, claimed in his file as "above the 88dp grip target's spirit" | **FAIL — violation, to Zug-II.** 84 < 88, and width was never the dimension at risk — height is: 68dp tall is 20dp under the ≥88dp law for this target. Full spec and fix in §4. |

**Target summary: 9 FAILs, 1 UNPLANNED, 2 CONDITIONAL.** Every FAIL is a
height failure. Nothing in this plan is too small in width; everything
that fails fails the way a tired thumb fails — vertically, at 03:30.

---

## 2. TYPE-FLOOR AUDIT — every text role carrying essential information

Caps tax applied throughout: tracked caps must clear **13.5sp functional /
15.5sp essential**. Set's ladder (104/72/50/22/15/10) is the reference
ramp; where a plan role matches Set, it passes by his arithmetic as well
as mine.

### 2.1 The plate rule — decoration vs information (the spec the law demands)

The 10sp plate (+2.0 tracking) is **11.5sp-effective at best** against the
tax — it can never carry essential information, and it carries functional
information only where a larger rendering of the same fact exists on the
same screen. Classification, binding on every seat:

| Plate use | Class | Verdict |
|---|---|---|
| Model plate `MONOLITH ML-01`, section eyebrows that duplicate a 22sp title directly below, footer furniture | **Decoration** — the fact lives elsewhere at legal size | **PASS** at 10sp |
| Nav stop words (FOCUS / SYSTEM / HORIZON / LEDGER / PARK) | **Information** — the word is the *sole* identity of the destination; position + cap are shape channels, the word itself is the label | **FAIL at 10sp, to Razor + Set.** Fix: nav words at **15sp** (Set's body tier) inside the enlarged rail (§1), or — minimum lawful — untracked 12sp+ with the tax removed by removing the tracking. A destination you cannot read is a destination you guess. |
| War line `186D TO NEET · TODAY 2H30 / 12H` (Razor §0.2) | **Information** — two essential facts (countdown, today's total), no other rendering in chrome | **FAIL at 10sp plate, to Razor.** This is the worst type violation in the plan: the two numbers he steers his day by, set at the smallest size in the app. Fix per Set's own ramp: war line **15sp Anonymous Pro 700** (Set already specs it there — Razor's plate spec contradicts Set; Set wins). |
| Shield verb-code legend (COMMIT/SHUT/PEEK/OPEN swatch words) | **Information** — it *defines* the sled code every row then uses | **FAIL at plate size wherever specced as plate, to Plan/Iris.** The legend is a dictionary; dictionaries are essential. **15sp.** |
| `GUARD n/4` header readout (Razor §3.1) | **Information** — a permission count he must act on | **FAIL at plate size, to Razor.** 15sp, or the count rides the war line at 15sp per Set's chrome rule. |
| `RECORD` section head + `HELD THIS WEEK` label (Ledger-designers) | **Information** — the label is what makes the hero number mean anything | **FAIL at 10sp, to the Designers' Ledger file.** Hero stat also specced at **44sp Sora** there — off Set's ladder (50 is the rung) and under-specified; fix: label 15sp, hero 50sp. Its receipt rows at **13sp Atkinson** also **FAIL** the 14sp essential floor — receipts are the record; 15sp. |

### 2.2 Role-by-role audit (non-plate roles)

| Role (owner) | Planned | Floor | Verdict |
|---|---|---|---|
| Deck total readout / running time (Set) | 72sp Sora 700 | essential ≥14 | **PASS** |
| Hold countdown numerals (Set) | 50sp Sora 700 | hero while holding | **PASS** — and §4 holds the line: the countdown never drops below this |
| Hold countdown numerals (Zug-II B5) | **44sp** Anonymous Pro + label `SECONDS STILL OWED` at **11sp** | 50 per Set; label essential ≥14 (15.5 tracked) | **FAIL — violation, to Zug-II.** 44sp contradicts Set's ladder and shrinks the one number read at the moment of weakest resolve. Label at 11sp fails outright. Fix: 50sp Sora per Set; label 15sp. |
| Valve centre line `BANKED 30 · HOLDING 7` (Zug-II B1) | **13sp** | essential — this *is* the bank state, read mid-hold | **FAIL — violation, to Zug-II.** 13 < 14 before the tax; it is tracked-caps-adjacent mono carrying the session's most consequential number. Fix: **15sp**. |
| Valve idle line `BANKED 30 OF 60 · HOLD TO CONTINUE` + caption `15s banks. A slip drains…` (Zug-II B1/B4) | inherits 13sp / unspecced | essential instruction | **FAIL, to Zug-II** — same fix, 15sp. An instruction about the rule, set below the floor for reading the rule. |
| Sealed bar `ARMED · {remaining}` (Zug-II A5) | **11 / 11.5sp** | essential — session state + time left, mid-session, tired eyes | **FAIL — violation, to Zug-II.** The running state's own label is the joint-worst text in the plan. Fix: 15sp. |
| Slide lane scale `0 · 25 · 50 · 75 · SEAT` (Zug-II A3) | **8.5sp** at α45% | functional — it calibrates the committing gesture | **FAIL — violation, to Zug-II.** Below even the 12sp functional floor, at half alpha. Either the scale is information (15sp, full ink) or it is decoration and the numerals die, leaving tick marks. There is no third option. |
| Slide badge `25M` / `FLOW` (Zug-II A3) | **9sp** | duplicates the 72sp readout | Decoration by duplication — **PASS** only while the readout exists on the same screen. |
| Slide label `SLIDE TO ARM SESSION` / `RELEASE TO ARM` (Zug-II) | **12.5sp** Atkinson Bold caps | tracked caps functional = 13.5 | **FAIL (marginal) — to Zug-II.** Set specs slide words at 15sp Sora 700. Adopt Set's; the marginal fail disappears. |
| State words SHUT/PEEK/OPEN, ON/OFF, HELD (Set) | **15sp** Atkinson 700 +0.5 | essential ≥14, tracked ≥15.5 — +0.5 tracking is below the ≥0.08em tax threshold Set's spec uses; at 15sp it clears the plain essential floor | **PASS** |
| FLOW key legend (Latch) | 11sp shipped → 11.5sp → **12sp** at peak, letterSpacing 1.5sp | the word FLOW *is* the state word for the mode | **FAIL (marginal) — to Latch.** 12sp tracked caps = under the 13.5 tracked floor. His target fix (§1) is exemplary; his legend stops 1.5sp short. Fix: legend 15sp per Set's state words, or drop the tracking and keep 12sp untracked. |
| Routine meta `MON–FRI · STUDY · 4H 00M` (Set) | 15sp | essential | **PASS** |
| Routine row state words in PARK drawer (Razor §3.1) | **13sp** Atkinson (`3 OF 4`, `ON`/`OFF`, `GRANTED`/`MISSING`) | state words = essential ≥14 | **FAIL — violation, to Razor.** Drawer state words are the drawer's whole content. 15sp per Set. |
| App names / queued task titles (Set) | 15sp Atkinson 700 | functional | **PASS** |
| Deck task line DONE / QUEUE words (Linea) | **11sp** inherited, tone fixed, size never re-specced in her ladder | functional verbs/doors ≥12, tracked ≥13.5 | **FAIL — violation, to Linea.** She fixed QUEUE's contrast (3.5:1 tier → 5.8:1 tier — correct) and left its size at 11sp. Fix: 15sp per Set's button/state words. |
| Deck step count `2/5` (Linea) | **11sp** | functional count | **FAIL — to Linea.** 15sp or fold into the title line at 15. |
| Task row meta `#02 · 25m`, sub-step numerals (Zeile) | **11sp / 10sp** | functional — order + duration | **FAIL — violation, to Zeile.** 10sp numerals are plate size doing a body's job. Fix: meta and step numerals 15sp (Set's meta tier), or the axis numeral dies and order is carried by position alone with the count stated once at 15sp in the ACTIVE meta. |
| ACTIVE task title (Zeile 20sp vs Set 22sp) | 20sp | essential ≥14 | **PASS on my floors** — flagged as a Set-ladder conflict, not a Floor violation. Settle it between them; either size is lawful here. |
| Search placeholder `SEARCH` (Plan) | **13sp** | functional (the field's only label until typed in) | **FAIL — to Plan.** 15sp; the typed text at 15sp (Set) already passes — the placeholder must match it. |
| Section header rows in the apps list (Plan) | 10sp plate, tertiary, sticky | **Information** if the header is the only place the group is named (it is) | **FAIL — to Plan**, same class as nav words. 15sp, or an eyebrow directly under a 22sp title (then it's decoration). As specced — sticky group headers alone on the list — they are information. |
| `+ NEW ROUTINE` (Plan) | **13sp** Atkinson | functional action label | **FAIL — to Plan.** It is a button word; Set puts button words at 15sp. |
| Rim numerals, major detents (Set) | **15sp** Anon Pro 700 | functional orientation | **PASS** |
| Rim numerals (Vau) | **10sp** Anon Pro *Regular* at 55% ink | orientation numerals duplicating no single readout value-for-value | **FAIL — violation, to Vau.** Set's 15sp Bold is the spec; 10sp at 55% ink fails size and contrast together (§5). |
| AOD remaining / elapsed / task line (Set) | 104 / 50 / 15sp | essential, across a dark room | **PASS** |
| Overlay body sentence + state word (Set) | 15sp / 15sp | essential at panic distance | **PASS** |
| QUEUE word on Deck at night (Linea's catch) | was bound to the **3.5:1 tier while tappable at 11sp** | 3.5:1 carries nothing essential | Her fix (re-bind to the 5.8:1 secondary tier) is **correct and adopted** — the standing rule for every seat: **any word that can be tapped, or that states a state, binds to the 5.8:1 tier at night minimum, the 11:1 tier if it is the screen's primary readout.** |

**Type summary:** Set's ladder itself passes everywhere it is actually
used. Every type FAIL in the plan is a seat *not using it* — inheriting a
shipped 11–13sp, or setting information in the plate. The ladder is not
the problem. The exceptions are.

---

## 3. SECOND-CHANNEL TABLE — every state, its WORD and its SHAPE

Rule: position and/or colour may be the *first* channel. The state is not
specced until a **word** (text, ≥ the §2 floors) and a **shape** (a form
difference visible in silhouette, not a hue difference) both exist.

| State | Word channel | Shape channel | Verdict |
|---|---|---|---|
| Sled **SHUT** | `SHUT` 15sp (Set) | Cap at left stop + seat notch (Schlitten) + ink fill silhouette | **PASS** when Schlitten's notch ships; position-only until then |
| Sled **PEEK** | `PEEK` — must read `PEEK 2M` wherever row width allows (wave-1 D3, still binding) | Cap at middle stop + 2dp top-edge tick on the selected segment | **CONDITIONAL — to Schlitten/Plan:** `2m` alone is not a word for the state; the duration is part of the state's name. |
| Sled **OPEN** | `OPEN` 15sp | Cap at right stop + hollow/open silhouette (cap outline, no fill) | **PASS** |
| Sled **commanded vs proven** (intent ≠ proof) | True word replaces commanded word on failure: `OPEN — NOT SHUT` (Iris §alert) | Proof pawl drops only on CONFIRMED (Opal); jammed sled sits **skewed** | **PASS as planned** — the strongest second-channel work in the wave. Keep both halves; neither alone is enough. |
| Loadout **STUDY / DEEP / SLEEP** selected | Mode word + header line `LOADOUT — STUDY ACTIVE` | Ink underline bar, 3dp, full label width, under the selected word only | **PASS** (wave-1 D3 spec, carried into plan) |
| Routine **ON / OFF** | `ON` / `OFF` word at right of row (15sp — after §2 fix to Razor's drawer spec / Plan rows) | Sled cap position + OFF word set in a recessed well (Razor drawer: state as form) | **PASS** conditional on the 15sp fix |
| Routine **window open now** | `WINDOW OPEN — ENDS 03:30` on the row it touches (Kato) — never `LIVE`, never `RUNNING` | Now-line in **ink** (the orange now-line is dead — Kato's own correction) | **PASS** |
| Routine **paused** | `PAUSED` word on the row | Cap position + row's time axis segment drawn broken/dashed | **CONDITIONAL — to Plan:** the word is specced via the switch state; the *shape* (broken axis segment) is my requirement — a paused routine must not draw the same unbroken column as a live one. |
| Nav **selected** | Stop word re-inked to full strength *on arrival* of the cap (Razor) | Cap (raised slab) seated under the word | **PASS** on channels; the word's size fails §2, the cap's height fails §1 — channels right, dimensions wrong. |
| Nav **PARK** (drawer open) | `PARK` word in the zone | Cap seated past the score line + heavier detent tick + drawer visibly risen | **PASS** on channels (55%-ink word is a contrast fail — §5). |
| Session **idle** | `SLIDE TO ARM SESSION` on the slide (15sp after fix) | Cap at rest, ghost outline of its home position | **PASS** |
| Session **gate crossed** (slide ≥0.82) | `RELEASE TO ARM` replaces the label, shown only while true (Zug-II A2) | Thumb fill steps to alert tone + grip line thickens 3→4dp | **PASS** — word + shape + haptic, the complete pattern. |
| Session **armed / running** | `ARMED · {remaining}` on the sealed bar (15sp after §2 fix) + state word beside the Deck time | Thumb stays seated right — the gesture of no-return does not return (Zug-II A1/A5) + Ø3dp armed lamp at FOCUS in chrome | **PASS** conditional on the type fix. The lamp alone would fail (colour/dot only); it never stands alone — the bar's word is its pair. |
| Session **grace (first 10s)** | `SLIDE BACK TO ABORT` + pocket labelled `ABORT` | Mirrored seat pocket printed at the track's left end | **PASS** |
| Wheel **FLOW (no limit)** | Readout says `NO LIMIT`; rim prints the word `FLOW` at the start stop — never the numeral `5` (Rad) | Start-stop detent position | **PASS** — Rad's catch: a scale that prints 5 where it means FLOW is a lying shape; fixed in his plan. |
| Wheel **timed value** | 72sp readout is the word channel (the number itself) | Thumb dot on the arc channel + detent ticks | **PASS** |
| Hub **block count 1–4** | Caption factorisation in words: `2 BLOCKS OF 25 MIN` (Nabe) | Filled rim segments = the count (Nabe peak) / pips (Rung 3) | **PASS** — count as construction *and* as words. Pips alone at Ø4.5–10dp would be shape-only and too small to count at a glance; the caption is load-bearing, never delete it as "duplication" — it is the second channel, not a second rendering. |
| Task **queued** | Position word in meta (`#02`) at legal size (after §2 fix) | Outline (hollow) marker square in the axis cell (Zeile) | **PASS** conditional on the type fix |
| Task **ACTIVE** | `ACTIVE` word in the meta line | **Filled** marker square (vs queued's outline) + the one expanded block | **PASS** |
| Task **done (step)** | Step number goes Bold + tone step (Linea) — *open conflict with Zeile's strikethrough* | Linea: tone/weight only · Zeile: drawn strike | **UNRESOLVED — to Linea + Zeile (already in correspondence):** pick ONE shape app-wide. My floor ruling: either passes *if* the word/number channel stays (it does in both) — but two encodings of the same fact on Deck and Tasks is a consistency violation in waiting. Converge before build. |
| Task **done (task)** | `DONE` word (verb, rightmost, Linea) | Title strike **only** on DONE itself (Luz's correction: the last step must not strike the title — steps-done ≠ task-done) | **PASS** |
| Session **running — Tasks frozen** | Mono line `RUNNING · 12:04 LEFT · ENDS 14:35` (Zeile) replaces the action row | All click targets absent — not greyed, *absent* (Linea Rung 4) | **PASS** — absence is the shape; a greyed verb at 03:30 reads as a broken verb. |
| Valve **idle, token available** | `1 LEFT THIS WEEK` stated as text *before* the hold begins (wave-1 D4e — binding) | Badge `1/1 LEFT` | **CONDITIONAL — to Zug-II:** his B-ladder inherits the shipped badge but never re-states the pre-hold sentence at legal size. Spec it at 15sp in B1's readout family. |
| Valve **holding (unbanked)** | `HOLDING {n}` in the split readout `BANKED 30 · HOLDING 7` (15sp after fix) | Accent fill advancing inside the current chamber | **PASS** conditional on §2/§4 fixes |
| Valve **banked (15/30/45)** | Chamber numeral `15`/`30`/`45` stamped at the chamber's right end + `BANKED {n}` in the readout | Chamber filled **solid** (stored metal) vs live accent fill + 1dp stamp line under the numeral | **PASS** — solid vs glowing is a silhouette difference, readable with the numbers covered. Exemplary. |
| Valve **draining (slip)** | Readout `HOLDING` number falls; drain is announced in the content description at drain-stop | **Diagonal hatch** collapsing right-to-left — the only hatched thing in the app | **PASS** — an unmistakable shape, impossible to mistake for progress. |
| Valve **frozen (sensor gap <150ms)** | No word — deliberately: frozen time is not an event in his intent (Zug-II B4) | Fill stops dead: no hatch, no motion | **PASS** — silence is the correct channel here; a "PAUSED" flash would tax a tremor. The 1.5s cap keeps the silence honest. |
| Valve **complete / token spent** | Badge flips `1/1 LEFT` → `0/1 · SPENT {day}` + caption turns past tense, same frame as the seal | All four chambers banked-style for 180ms, then the spend | **PASS** |
| Valve **no tokens** | `NO WEEKLY EMERGENCY TOKENS REMAINING` sentence | Touch disabled + bar in inert tone | **PASS** — the sentence, not the grey, carries it. |
| Guard **permission granted / missing** | `GRANTED` / `MISSING` word per row + header `GUARD 4/4` vs `3/4` | Pip: **filled** ring vs **hollow** ring (Razor §3.1) | **PASS** on channels — hollow vs filled is a shape, the count is the word. (Sizes fail §2; pip Ø6dp is a marker, not a target — PASS as shape.) |
| Guard **sled proof fail** | Word replaced by the truth: `OPEN — NOT SHUT` (Iris) | Owning row steps up one surface step, rule → OUTLINE | **PASS** |
| AOD **armed** | Armed mark text (plate — decoration; the *readings* are the information) | Armed dot + the two/three readings present | **PASS** |
| Theme **day / night** | No word, no toggle — theme follows routine/schedule (settled) | The palette itself | **PASS — exemption, stated:** theme is an ambient condition, not a state he acts on; it is the one state exempt from the word rule, because there is no decision it feeds. The drawer *reports* its source in words (`FOLLOWS SLEEP ROUTINE`) — that report is information and obeys §2. |

**Second-channel summary:** no state in the plan is left on colour
alone. Two states are left on *position + colour with the word too small
to read* (nav selected, valve sealed/idle lines) — a word that fails §2
is not a channel, it's a rumour of one. Those are counted as FAILs in §2,
not forgiven here.

---

## 4. THE HOLD TARGET SPEC — banked hold, my amendments, binding

Settled law (his decision): **full 60s owed · banks at 15s · slip drains
the unbanked tail only · banked floors at 15/30/45 never drain ·
completion alone spends the 1/week token.** Zug-II's B1–B5 engine,
chambers, bolts and drain grammar are adopted. What follows is the
target + readout layer, which is mine, and where his ladder fails it:

1. **Target size: the full bar, ≥88dp in its short dimension — spec
   112dp tall × full content width.** Rosa's full-width 112dp bar is the
   correct form and I adopt it over Zug-II's 68dp bar: a round pad you
   must aim at is a target designed for calm hands; a full-width bar is
   operable eyes-off by a shaking one. The 112dp is overlay layout, not
   bar decoration: chambered bar centred in a 112dp touch field, the
   whole field is the target, contact anywhere in it counts.
2. **Contact tolerance:** finger drift up to **12dp** from initial
   contact does not break the hold (wave-1 D4c, still binding); the
   target tracks the finger within that radius. Sensor gaps <150ms
   freeze (Zug-II B4, capped 1.5s/attempt) — adopted.
3. **Position:** bar in the **lower third, thumb-natural, one-handed** —
   reachable by the same thumb that armed the session without a grip
   change; never centred-screen, never top. At 1 AM he holds the phone
   in one hand, in bed; the bar lives where that thumb already is.
4. **Countdown placement — hero, above the bar, never inside it:**
   remaining seconds at **50sp Sora 700** (Set's overlay tier), centred
   above the bar, with the split readout directly under it at **15sp**:
   `BANKED 30 · HOLDING 7`. The number owed is the biggest thing on the
   overlay; the bar is what his thumb reads, the numeral is what his
   eyes read, and neither ever substitutes for the other.
5. **Drain readability:** while draining, the hero numeral **keeps
   counting the truth** (owed = 60 − banked − held, by construction) and
   the unbanked fill hatches (Zug-II B3) — but the numeral must also
   *say* a drain happened when it stops: on drain-stop the readout line
   becomes `SLIPPED — BANKED 30 SAFE` for 2s before returning to the
   split readout. A silent drain is a number that changed for no visible
   reason at the exact moment he was looking away. The catch at the bank
   floor is felt (B2 latch) *and* stated.
6. **Eyes-free completion:** escalating ticks are Ton's sound and
   Zug-II's B2 pitch ladder (420→660Hz climb per segment, bolts at
   560/470/390Hz) — the hold must be completable without looking once:
   bank bolts felt, drain glide heard, floor catch felt, spend = the
   disarm seal. My requirement on their specs: **no bank, drain or
   completion event may exist in only one of {sound, haptic, visual}** —
   each fires at least two.
7. **Pre-hold truth:** before the first touch, the overlay states at
   15sp: `EMERGENCY BREAK — 1 LEFT THIS WEEK` and the rule once —
   `15s banks. A slip drains what isn't banked.` (Zug-II B4's caption,
   promoted to legal size.) The cost is never discovered after it's spent.
8. **Accessibility path:** TalkBack/switch users start the same 60s as
   an attended timer with a `KEEP HOLDING` confirmation press every 15s
   (wave-1 D4d) — same 60s, same token, no sustained motor demand. The
   content description updates at banks and drain-stops only (Zug-II
   B5) — adopted; per-second announcements would be cruelty by verbosity.

---

## 5. CONTRAST VERIFICATION — planned pairings, computed, not re-derived

Palette is locked; I verified the pairings the plan actually uses
(WCAG luminance, computed from the Color.kt hexes this run):

| Pairing | Ratio | Verdict |
|---|---|---|
| Day muted `#5F5856` on ground `#E4DFDA` | **5.26:1** | **PASS** — the re-ink from illegal `#787170` (3.61:1) is verified, not assumed |
| Day muted `#5F5856` on recess `#D8D1CB` | **4.61:1** | **PASS, margin 0.11** — lawful, fragile. Any alpha under 100% or a dimmer surface breaks it; muted text on recess ships at full alpha or not at all |
| Day muted `#5F5856` on raised `#FAF6F0` | **6.47:1** | **PASS** |
| Day secondary `#615B5A` on recess `#D8D1CB` | **4.41:1** | **FAIL if used for text on recess** — secondary ink is a ground/raised ink only. Any seat binding row sub-lines on recessed surfaces to `#615B5A` re-creates the wave-1 bug one hex over. |
| Night primary text `#D2C6BB` on ground `#171412` | **10.95:1** (≈ the stated 11:1 tier) | **PASS** — tier verified |
| Night secondary `#9C8E84` on ground | **5.78:1** (≈ the 5.8:1 tier) | **PASS** — tier verified; this is the *minimum* tier for any tappable or state word at night (Linea's QUEUE fix binds here — correct) |
| Night muted `#776A60` on ground | **3.51:1** (the 3.5:1 tier) | **DECORATION ONLY** — verified as specced. It may carry nothing essential, nothing tappable, no state. |
| Night muted `#776A60` on raised `#362C27` | **2.60:1** | **FAIL — new catch:** the muted tier collapses *below even the decorative tier* on raised surfaces. Night muted text/ink is banned from raised cards and caps outright; on recess `#100E0C` it is 3.68:1 (decorative-pass only). Any seat printing muted on the night timer card or hub plate is in violation. |
| Night glyph `#EDE3D8` on ground | **14.48:1** | **PASS** |
| Cream `#FFE4CF` on night cap `#4A423C` | **8.08:1** | **PASS** — words on caps at night are safe |
| On-accent `#471602` on day accent `#F55314` | **4.43:1** | **CONDITIONAL PASS** — fails 4.5, passes the 3:1 large-text bar. Lawful *only* at ≥14sp bold / ≥18sp. Set's button words (15sp Sora 700) clear it; nobody sets small text on the orange fill, ever. Cream on day accent (3.08:1) is banned as its "fix." |
| On-accent `#471602` on night accent `#F47442` | **5.37:1** | **PASS** |
| PEEK word `#F4EFEE` on day PEEK fill `#615B5A` | **5.85:1** | **PASS** |
| PEEK word `#EDE3D8` on night PEEK fill `#776A60` (Iris's flagged borrow) | **4.13:1** | **FAIL — violation, to Iris.** The borrow fixes the staircase and breaks the word: the one verb whose fill is the muted tier gets a sub-4.5 word on it. Fix: night PEEK word in `#1B110D`-family dark ink on the `#776A60` fill (dark-on-mid, ≈4.9:1 — verify in build), or the night PEEK fill deepens one step and the word stays glyph. The word does not ship at 4.13. |
| Day alert `#D63E06` as *text* on ground | **3.49:1** | **FAIL as text, PASS as fill/shape** — alert sets no sentences in day mode (wave-1 D1, re-verified). Zug-II's gate thumb stepping to alert *fill* with a cream grip *line* is lawful (shape, not text). |
| Night alert `#FF8A5C` on ground | **7.90:1** | **PASS** even as text |
| Razor's unselected nav words at **55% ink** on the recess | day ≈**3.18:1** · night ≈**4.14:1** (blended) | **FAIL — violation, to Razor.** Unselected is still a destination label he must read to choose. 55% ink is a whisper aimed at the tiredest eye in the app. Fix: unselected words at the day muted `#5F5856` / night secondary `#9C8E84` tier minimum (both verified above) — hierarchy by the cap's presence, not by starving the unselected words. PARK's word at 55% ink fails identically. |
| Letterpress plate shadow strike at 35% ink | ≈**2.03:1** vs ground | **PASS as decoration only** — it is a shadow under a full-strength main strike; it may never be the strike. (Noted because two plan files spec the shadow without re-stating that the main strike is 100%.) |
| Vau rim numerals at 55% ink, 10sp | below every text tier at that alpha | **FAIL** — counted in §2; the contrast half of that violation lives here. |

**Contrast summary:** the locked palette is honest — day muted passes
everywhere at full alpha, the night tiers measure what Iris's file says
they measure. Every contrast FAIL above is a *pairing* choice (55% ink,
muted-on-raised, a word on the muted fill), not a palette defect. Do not
re-open the palette to fix them; fix the pairings.

---

## 6. THE LEDGER OF VIOLATIONS (for the merge — Sana / Ren)

1. **Zug-II** — valve bar 68dp tall (<88) · countdown 44sp + 11sp label ·
   `BANKED · HOLDING` 13sp · sealed `ARMED` line 11sp · lane scale 8.5sp ·
   slide label 12.5sp tracked. His engine is the best in the wave; his
   *readouts* are the worst. All fixes are size-only; no mechanism changes.
2. **Razor** — nav cap 44dp tall · PARK zone 44 × 44dp · grab strip 32dp ·
   war line + nav words + `GUARD n/4` in the 10sp plate · drawer state
   words 13sp · unselected words at 55% ink (3.18:1 day).
3. **Plan** — routine sled 36 × 20dp (cap 16dp) · search field 36dp ·
   search placeholder 13sp · list section headers as plate · `+ NEW
   ROUTINE` 13sp · verb legend at plate size.
4. **Raster** — sleds 96 × 32 / 56 × 32dp · sheet grab zone 20dp · slide
   thumb exactly 48×48 on the committing gesture (conditional).
5. **Zeile** — queued rows 44dp · action words ≈40dp · step rows 28dp ·
   meta 11sp · step numerals 10sp.
6. **Schlitten** — app sled 168 × 34/36dp as its own target.
7. **Iris** — night PEEK word on muted fill at 4.13:1.
8. **Linea** — DONE/QUEUE held at 11sp · step count `2/5` at 11sp.
9. **Latch** — FLOW legend 12sp tracked (marginal; his target spec is the
   model the others should copy).
10. **Vau** — rim numerals 10sp Regular at 55% · Deck/NEXT lines unspecced
    as targets-or-print.
11. **Ledger-designers file** — RECORD head + label at plate size · hero
    44sp off-ladder · receipt rows 13sp.
12. **Taste / Fach** — composer keys UNPLANNED. No size, no pass.

**What passes clean, for the record:** Set's ladder wherever it is used ·
Latch's touch-box pattern · Linea's 48dp-effective step rows and her
night-tier catch · Nabe's hub at every rung · Opal's proof pawl + Iris's
truth-word (the intent/proof split) · Zug-II's chamber/hatch/bank shapes ·
Razor's GUARD pip (hollow/filled) · Kato's ink now-line. The plan's
*shapes* are nearly all lawful. Its *dimensions* are where it bleeds —
and dimensions are the cheapest thing in this entire wave to fix. Fix
them in the plan, not in the build, and not on his eyes.

— Floor, Accessibility seat, Designers cohort
