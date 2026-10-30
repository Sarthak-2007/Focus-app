# Aldo — Wild, typography

**Thesis.** Monolith does not have a type problem; it has a *numeral custody* problem. The current Deck renders one duration four times — `25:00` giant, `50 MIN TOTAL` beneath it, `BLOCKS 2×` in the hub, `25M` on the slide — and the four disagree about what the number even is (the giant is the block, the sub-line is the total, the badge repeats the block). Shield whispers end-times at half the size of start-times. Tasks prints every duration three times. Chrome carries fractions whose facts live on other screens. The architecture below gives every number exactly one home, one size class, and one motion class — and the motion class is decided by *how the number came to exist*:

- **MEASURED** numbers (time actually passing) may **step**.
- **CHOSEN** numbers (a detent he picked) may **flip**.
- **COMPUTED** numbers (arithmetic: totals, sums, finish times) may only **re-strike** — change instantly, never travelling through intermediate values that were never true.

Typefaces and weights are the real ones only: Sora Medium/SemiBold/Bold, Atkinson Hyperlegible 400/700, Anonymous Pro 400/700 (it has no other weights; nothing below pretends otherwise). No orange anywhere in this file — orange stays a COMMIT fill on the Deck slide and Tasks START, and typography never spends it. No drop shadows; depth is tone steps, hairlines, and recess only. Night values inherit the locked staircase (ground `#171412`, text tiers ≈11 / 5.6 / 3.4:1).

---

## Deck — total readout and rim scale

**The kill list (this surface).** `50 MIN TOTAL` sub-line: dies — once the readout *is* the total, the sub-line is the same fact in a smaller font. Slide badge `25M`: dies — it repeats the block length, which belongs to the rim. Hub numeral `2×`: dies — count becomes position (pips), per the settled hub call. What survives: one giant total, one printed rim scale, one finish time.

### Chosen form — THE READOUT IS THE TOTAL; THE RIM IS THE BLOCK

**Mechanism / data source.** The giant readout renders `session.totalMinutes = blockMinutes × blockCount`, live on every wheel detent and every hub press, from the same state object the slide will arm. It is the only large numeral on Deck and the only rendering of total duration anywhere in the app. Format: clock form `H:MM` — `0:50`, `1:40`, `3:00` — Anonymous Pro Bold, 88sp, tabular (advance is constant anyway), centred in the readout well. While running, the same glyphs, same size, same well become the countdown `MM:SS` remaining — one home across both states, so arming and running never trade places or sizes. `FINISHES 06:20` stays at the well's lower-right margin, Anonymous Pro 400, 11sp, tier-2 ink: it is a *different fact* (wall-clock end = now + total, from the system clock), so it is not a duplicate — but it is COMPUTED class and obeys the re-strike rule below.

**The rim scale — printed, true, and the block's only home.** Block length lives nowhere except the rim print. Scale: 5–60 min, 12 detents of 5 min, mapped linearly onto a 264° sweep (24° per detent — linear, because a non-linear scale would be a fake scale). Ticks: minor 6dp at every detent; major 11dp at 15 / 30 / 45 / 60. Numerals `15 30 45 60` printed *outside* the tick ring at those four detents, Anonymous Pro 700, 10sp, tier-2 ink, baselines kept horizontal (never rotated around the arc — rotated type is a dial costume; horizontal type is a printed instrument). The selected detent's tick deepens to full ink while the wheel is touched and for 600ms after settle; numerals never change colour — the scale is print, print doesn't react. Hub: pips only, one pip per block, filled = counted. Count has no numeral anywhere; you read it as position, which is all it is.

**Motion.** Dragging: readout digits change raw per detent, no animation queue — the number must keep up with the hand. On settle (wheel release / detent rest ≥80ms): changed glyphs flip once — per-glyph split-flap, 170ms, `CubicBezierEasing(0.2, 0.9, 0.25, 1.0)`, cascade right-to-left at 24ms per slot, 1px seam hairline present only during the flap (fades in 40ms before, out 80ms after). Colon never flips. On hub press (count change): the total is COMPUTED — no flip, no roll. Digits swap instantly and re-strike: one extra draw pass at +0.35px horizontal offset, 14% alpha, decaying over 140ms easeOut — a press impression, not a journey. `FINISHES` re-strikes in sync, same frame, same treatment at its own size. Running state: seconds STEP (see numeral engine, §2) — no other motion in the readout, ever.

**Haptics.** Wheel detents keep the wheel's own ticks (not this surface's budget). Settle flip: 5ms @ 0.30 per landing slot, final slot 14ms @ 0.85. Re-strike on count change: single 10ms @ 0.5 knock, simultaneous with the visual strike. Running: silence.

**Sound.** Settle: one soft mechanical clack per cascade, ≤35ms, filtered noise + 180Hz body tap, Guard-gated. Re-strike: one muted thock, ~70ms decay. Dragging and running: silent.

**Depth / material.** Readout sits in its existing recess (one tone step down, 1px catch-light on the lower edge). The flap seam is drawn in the recess's darker tone; at rest there is no seam, no gradient — flat letterpress glyphs in a milled well. Rim print is flat on the panel tone: ticks and numerals are ink, not embossing; only the active tick's ink value changes.

**Standards check.** *ARM:* readout is live during the drag, so the total he will arm is visible before he commits — zero added gestures, zero added waits. *HOLD:* once running, the readout is display-only; nothing in it can be tapped, edited, or negotiated with. *TRUTH:* one total, one home; block length exists only as rim position + print; count only as pips; finish time is a distinct fact, distinctly homed. Residual risk: `H:MM` arming form vs `MM:SS` running form could be misread at a glance — mitigated by state, not by a label: the slide is gone and the seal state is unambiguous when the form changes, and the well's margin word switches `FINISHES` → `ENDS` never — it stays `FINISHES`, because at arm time it is a prediction and while running it is the same prediction, still true.

### Runner-up — MINUTES AS A WORD-LENGTH NUMBER

Total rendered as a plain count — `50` at 96sp with `MIN` set small at its baseline — rim unchanged. Compact spec: same custody, same flip/re-strike grammar, format only differs. **Why it lost:** it breaks the one-home-across-states rule. A running session is a countdown, and `37` (minutes left) silently drops the seconds the runner is actually living in; switching formats at seal-time would move the number's meaning at the exact moment he stops looking at it. Clock form is honest in both states; the count form is only honest in one.

**Guard fork.** Not relevant to this surface — the readout and rim carry no Guard fact. (The `GUARD 1/4` chip above the well is chrome, and dies in §5.)

---

## Global — rolling / flipping numerals, if honest

This is the engine the other four surfaces rent. One per-glyph Canvas renderer serves all of them: Anonymous Pro laid out into fixed slots via `TextMeasurer` (constant 0.6em advance — slots never reflow), one draw pass per element.

### Chosen form — THREE MOTION CLASSES, ASSIGNED BY ORIGIN

**Class 1 — MEASURED → STEP.** Applies to: running countdown seconds/minutes, AOD desk clock. The value traverses every intermediate because time does — but at 1-second resolution, so the honest motion is a step, not a smear. Per second boundary: the changing pair translates Y −1.5% of cap height and settles, 140ms, easeOutBack overshoot 1.2 (travel is 1–2px; it reads as the digit seating itself, not bouncing). No continuous roll: a rolling strip would claim sub-second precision the display doesn't have. Motion is derived from `remainingMs`, never from frame callbacks, so pause/resume cannot desync it. Haptics: none. Sound: none. AOD: same step at the minute only, amplitude ×0.5, and the existing burn-in position shift stays phase-offset from it.

**Class 2 — CHOSEN → FLIP.** Applies to: Deck readout on wheel settle, Shield time edits, Tasks duration on composer-cap change. The split-flap of §1: 170ms per slot, 24ms right-to-left cascade, seam only while moving, suppressed during continuous drag and fired once for the settled value. A flip shows exactly one intermediate state — the fold — which is truthful for a discrete choice: the drum was at A, it is now at B, nothing between them was ever a value. Haptics/sound as specced on Deck, scaled down off-Deck (ticks 5ms @ 0.30, clacks gated and quieter).

**Class 3 — COMPUTED → RE-STRIKE.** Applies to: Deck total on count change, `FINISHES`, Tasks header sum, Shield header counts, record/receipt totals if a record is kept (⚑ Ledger-open — see verdict). The digits change in one frame. The only motion is the re-strike impression: +0.35px offset pass at 14% alpha decaying 140ms, plus optionally a 60ms ink deepen 88% → 100%. **Intermediate glyphs are never drawn.** Rolling 25→50 through 31, 37, 44 is a lie with good easing. Haptic: one 10ms @ 0.5 knock for user-caused recomputes, none for clock-caused ones (`FINISHES` drifting as time passes gets no knock — it re-strikes silently at most once per minute, and only while Deck is visible pre-arm). Sound: none except the Deck thock.

**Explicit bans.** No odometer/count-up tallies anywhere — the old Ledger's roll-up telemetry is a rejected *form* regardless of the reopened Ledger question (see LEDGER VERDICT below); no surface inherits it. ⚑ *Flag: if a Monolith-native record survives in any form, its figures are COMPUTED/historical class — they re-strike or sit still, never roll — so this ban holds under all three Ledger options.* No flipping of wall-clock times. No rolling of sums. No per-glyph animation of Sora or Atkinson text, ever — proportional faces move as whole lines or not at all; only the mono face performs, because only the mono face has fixed slots to perform in.

**Depth / material.** All three classes are flat at rest. Motion borrows depth only while moving: the flap's seam gradient (6% black at the seam edge), the step's catch-light value on the settling digit. Nothing casts a shadow in any class.

**Standards check.** *ARM:* classes add 0ms to any path — flips fire on settle, strikes are one frame, steps are ambient. *HOLD:* no class runs an interactive element mid-session; the running countdown's step is the only motion in a sealed session, and it asks for nothing. *TRUTH:* this section *is* the truth standard applied to time — the risk it kills is the default one, where a UI library animates every number change the same way and half of those animations fabricate values.

### Runner-up — UNIFORM SPLIT-FLAP EVERYWHERE

My own wave-1 position: every numeral change flaps. Compact spec: one mechanism, one code path, 170ms cascade for totals, sums, clocks alike. **Why it lost:** uniformity is the flaw. A flap on a computed total displays a fold between two values and invites the eye to read travel where there was only arithmetic; a flap on `FINISHES` implies a mechanism deciding the finish time. One mechanism for three kinds of fact is simpler to build and wronger to ship. The three-class grammar costs two extra render paths and buys a numeral system that cannot lie in motion.

---

## Shield — the time column

**The problem in the current render.** `09:00` is set large; `→ 13:00` is demoted to a small second line under the name, mixed with days and loadout. Start shouts, end whispers, and the *interval* — the only thing a routine actually is — is never drawn as one object. The routines shown are also stale seeds, but reseeding is settled elsewhere; this column is built to hold his real day (fasted block 05:00–08:00 daily, sleep 20:00–03:30) without lying about any of it, including the overnight wrap.

### Chosen form — THE TIMETABLE COLUMN: ONE INTERVAL, ONE OBJECT

**Mechanism / data source.** Each routine row opens with a fixed-width time column, 96dp, right-aligned, fed directly from the routine's stored start/end — the same values that fire it; the column is a rendering of the schedule, never a second copy of it. Start on line one, end on line two, **same face, same size, same weight**: Anonymous Pro 700, 19sp, tier-1 ink. Between them, a 1dp vertical hairline, 10dp tall, in tier-3 tone, left-aligned to the column's inner edge, with a 3dp arrowhead at its foot pointing down into the end time — the interval drawn as a descent through the day. Reading order is the day's order: down. Overnight routines (end ≤ start, e.g. sleep 20:00 → 03:30): the end line carries a `+1d` marker — Anonymous Pro 400, 9sp, superscripted at the end numeral's upper right. That marker is the wrap's only rendering; no "next day" words anywhere else on the row. To the column's right: routine name, Sora SemiBold 17sp, one line; beneath it the meta line, Atkinson 400, 11sp, tracked +0.08em, tier-2: `DAILY · STUDY` / `MON–FRI · DEEP` — days and loadout, words only, no times (times live in the column; the meta line never repeats them). Paused routine: whole column drops to tier-3 ink (≈3.4:1) and the meta line gains the single word `PAUSED` — state as ink value plus one word, never a second time rendering, never a greyed-out fake.

**Motion.** On row entry: still. The column does not assemble, stack, or print itself in — a timetable you consult at 03:30 should already be sitting there. On edit-and-save (his action, in the editor): changed time glyphs flip (CHOSEN class, 170ms, cascade within the edited field only) and any derived count in the section header (`2 ACTIVE`) re-strikes (COMPUTED class). On pause/resume toggle: ink tier crossfades 160ms easeOut, hairline included. Nothing else moves.

**Haptics.** Column itself: none — it is read, not operated. Edit save: the flip's standard ticks. Toggle: existing switch haptics, unchanged.

**Sound.** Silence. Shield is a settings-adjacent surface consulted in quiet hours; its times make no noise.

**Depth / material.** Flat print on panel tone. The column is separated from the name block by space alone (16dp), not a divider — the hairline *inside* the column is the only rule on the row, which is what makes it read as one instrument. No recess, no tile: routines are a timetable pasted on the wall, not machinery in a well.

**Standards check.** *ARM:* zero cost — routines fire themselves; the column is edited only in rested hours. *HOLD:* a firing or running routine shows no editable time anywhere — during a session the column is ink, not a control. *TRUTH:* start and end at equal weight kills the current hierarchy lie (a routine is its whole interval); `+1d` makes the overnight wrap explicit instead of letting `20:00 → 03:30` pretend to be a short evening; a paused routine can never masquerade as an active one, and — per settled law — the column renders only routines that exist; it never logs or implies a session that didn't happen.

**Guard fork.** The time column is Guard-independent by design — it renders routines, and routines are his under both outcomes. If Guard stays a separate screen, nothing here changes. If Guard folds into Shield, the folded-in switching lands *below* the routines block (apps and sleds are the lower half of Shield already); the column's width, sizes, and position at the top of each routine row do not move, so the timetable never gets re-typeset by someone else's merger. His call either way; the column survives both.

### Runner-up — SINGLE-LINE RANGE

`05:00—08:00` on one line, name beneath. Compact spec: Anonymous Pro 700 16sp range above a Sora name line; `+1d` rule identical. **Why it lost:** the range line spends width Shield doesn't have (name + range + toggle already crowd the row), and a horizontal range reads as a label *for* the name rather than as the day's spine. The stacked column's downward direction is the one piece of quiet meaning this surface gets for free — time in this app flows down the page everywhere (queues, receipts, the day itself). The range spends it; the column keeps it.

---

## Tasks — queue numerals

**The kill list (this surface).** The top strip of chips (`25m / 25m / 45m`) dies — it is the queue's durations printed a second time, in a row that explains nothing. `START 25m` loses its numeral — the duration lives on the row; START is a verb and verbs don't carry figures. Bottom-nav `HORIZON 3` dies in §5. Row indices `#01 #02 #03` die — a queue's order *is* its position on the page; numbering the positions prints the same fact twice, once as place and once as digits.

### Chosen form — ONE NUMERAL COLUMN, TWO TIERS, NOTHING ELSE NUMERIC

**Mechanism / data source.** Each queue row carries exactly one numeral of its own: its duration, right margin, Anonymous Pro 700, 13sp, tier-1 ink (`50m`). It is rendered from the task's stored duration — the same value Deck will arm when the task is cued — and it is the only place that task's duration exists as type. Sub-step numbers survive but are demoted into the same right-hand column, directly beneath the duration, forming a single vertical numeral spine per row: step figures Anonymous Pro 400, 10sp, tier-3 ink (`01 02 03` stacked with their lines). One column, two facts, separated by size (13 vs 10), weight (700 vs 400), and tier (1 vs 3) — never confusable, never duplicated: the step figure is position *within* the task, the duration is the task's length, and no other glyph on the row is numeric except figures inside the task's own title text (his words, not the app's rendering). The header aggregate stays and is the queue's only other number: `3 TASKS · 95M`, right-aligned in the HORIZON header, Sora SemiBold 13sp for the words with the figures in Anonymous Pro 700 — it is a *sum*, a different fact from any row's part, and it is COMPUTED class. `DONE 1` in the queue header is replaced by the word alone as a section state; the done count, if it is ever needed, belongs to the Monolith-native record (see LEDGER VERDICT — ⚑ *this placement depends on the Ledger answer: under option (c) the done count simply has no home and is never rendered, which this surface can live with*), not to a header competing with the live sum. Composer duration caps (`15m 25m 45m 90m`) keep their numerals — a control's label is a choice offered, not a fact rendered, so caps are exempt from the kill list by category, and the selected cap is also the *only* place a not-yet-queued duration may appear.

**Motion.** ADD commit: the new row's duration flips in (CHOSEN — he picked it on a cap; 170ms, units slot only) as the row strikes on (existing press choreography owns the row body; this file owns only its figures). The header sum re-strikes in the same frame (COMPUTED — it never counts up through `70M`, `85M` on its way to `95M`; those sums were never true). Reorder by drag: durations travel with their rows unanimated; the sum doesn't change and doesn't move. Task completion: the row's duration does not count down, fade-count, or tally — it leaves with the row. Step completion: the step figure's tier deepens 3 → 2 for 200ms as its strike line draws — a figure acknowledging its line, nothing more.

**Haptics.** Duration flip on ADD: folded into the ADD strike knock (20ms @ 1.0 at impact) — no separate numeral haptic. Sum re-strike: silent, no knock (it is a consequence, not an action; knocking for consequences trains him to notice arithmetic). Step figure deepen: none beyond the strike's existing 7ms @ 0.45 tick.

**Sound.** None owned by the numerals. The ADD thock belongs to the row strike; figures ride it silently.

**Depth / material.** The numeral spine is flat print. Its column edge is aligned across every row on the screen (durations share one right edge app-wide on this surface — set the margin once, 20dp, and never let a row improvise). Alignment is the material: a ragged numeral column reads as handwriting; a true column reads as a ledger.

**Standards check.** *ARM:* cueing a task from the queue arms its stored duration directly — the figure he read is the figure that fires, one rendering, no transcription step where a `25m` in one place could disagree with a `50` in another. *HOLD:* no numeral on this surface is live during a session; the queue is edited in rested hours only. *TRUTH:* kills two live duplicates (chip strip, START's figure) and one structural one (indices); the sum is labelled by position and class as a sum, and the re-strike rule stops the header from performing fake arithmetic.

### Runner-up — FULLY NUMBERED LEDGER ROWS

Keep `#01` indices, add per-row accession-style numbering, durations in the title line. Compact spec: index Anonymous Pro 10sp tier-3 at left margin, duration after the title. **Why it lost:** it spends the left margin — the eye's entry point — on a fact the page layout already states, and it pushes the title right on every row to pay for it. Indices earn their keep in archives you cite ("see #04"); this queue is executed top-down and then gone. Numbering a thing you will never reference is decoration wearing a uniform.

---

## Chrome — letterpress

Chrome = the nameplate bar, section labels, screen headers, and the bottom nav. Chrome's job is identity and location. It is not a dashboard.

### Chosen form — PRESSED INK, ZERO NUMERALS

**The letterpress treatment (one recipe, everywhere).** Every chrome glyph is drawn twice: first pass in the surface's highlight tone (one step lighter than the ground) offset (0, +0.7px); second pass in ink at (0, 0). On the light ground this reads as type pressed *into* paper — a deboss with the light catching the lower lip. At night the passes swap logic: highlight pass becomes one step *darker* than `#171412`'s local surface, offset (0, −0.7px), so the impression reads as cut into the dark panel rather than floating on it. The offset never exceeds 0.7px and never blurs; above that it becomes a shadow wearing a costume, and shadows are banned. Type that gets the treatment: the nameplate `MONOLITH` (Sora Bold, 15sp, tracking +0.04em; its dot is ink, always — wave-1's orange dot flash is dead, orange does not exist in chrome); section labels (`LOADOUT`, `ROUTINES`, `QUEUE`) in Atkinson 700, 10.5sp, tracking +0.14em, tier-2 ink; screen header words in Sora SemiBold 13sp. Body text and task titles never get the second pass — letterpress is for the machine's own voice, not for his words.

**The numeral ban.** Chrome carries no figures. `HELD 02` dies from the nameplate bar: whatever it counts, its home is a screen, not the frame. `GUARD 1/4` dies: the fraction duplicates Guard's own switch count on Guard's own screen — if Guard survives as a screen, the count lives there; if Guard folds, it lives with the folded section (fork below). Bottom nav loses `SYSTEM 7` and `HORIZON 3`: those counts are the screens' header facts (`7 SHUT` on Shield, `3 TASKS` on Tasks), printed once, on the screen that owns them. The nav becomes icons + words (Sora SemiBold, 10sp, tracking +0.06em): `FOCUS / SYSTEM / HORIZON`. ⚑ *Flag — LEDGER-DEPENDENT: this three-destination bar assumes the Ledger does not return as a tab. Under option (a) a fourth word, `RECORD`, joins the bar (never `LEDGER` — the name carried the study-archive lie); under options (b) and (c) the bar stays at three (plus `GUARD` under the Guard fork in the verdict section). In every option the freed width goes to touch targets, not decoration, and the numeral ban is untouched — a returning tab gets a word, never a count.* The one mode word chrome may carry is `DAY` / `NIGHT` on the toggle, because mode is chrome's own fact and exists nowhere else. While a session runs, the nameplate bar gains exactly one word at its right end — `SEALED`, Atkinson 700 10sp tier-2, letterpressed — a state of the frame itself, stated once, in the frame. It is not a count and it opens nothing.

**Motion.** Chrome is still. Tab change: outgoing and incoming header labels crossfade 120ms easeOut, no stagger, no re-setting of the nameplate (the machine does not re-sign its name every time you turn around — that was theatre). The active nav word's ink deepens tier-2 → tier-1 over 160ms and its letterpress offset pass deepens with it; nothing slides, nothing counts. `SEALED` appears and disappears with a 140ms ink fade — a word arriving is enough.

**Haptics.** None for any chrome type. Nav selection keeps its existing sled tick, which belongs to the control, not the letters.

**Sound.** Silence. Chrome never speaks.

**Depth / material.** The letterpress offset *is* chrome's depth — plus the existing hairline under the nameplate bar and above the nav, both 1px, tier-3 tone. No recesses, no tiles, no fills in chrome except the active nav sled's ink fill, which is a control state, not a type treatment.

**Standards check.** *ARM:* a numeral-free frame is faster to parse at 03:30 — identity and location in one glance, numbers only where he's already looking for them. *HOLD:* `SEALED` in the frame states the one mid-session fact chrome is allowed to state, and it is a word, not a number he could start doing arithmetic on. *TRUTH:* this section deletes three duplicate renderings (`HELD 02`, `GUARD 1/4`, nav counts) and creates none.*

**Guard fork.** If Guard stays a separate screen, its slot rejoins the nav as a word (`GUARD`, no fraction, no count) and its switch counts live on its own screen only. If Guard folds into Shield, the nav stays at three and Guard's facts live inside Shield's sections, each once. Either way chrome's numeral ban holds — the fork changes how many words the bar carries, never whether it carries figures. His call.

### Runner-up — COUNTED CHROME

Keep small mono counts in the nav (`HORIZON 3`) as glanceable state, letterpressed like the words. Compact spec: counts Anonymous Pro 700 9sp tier-2 beside each nav word; nameplate bar stays clean. **Why it lost:** a nav count is the header fact wearing a second uniform. He reads `3` in the bar, arrives, and reads `3 TASKS` again — the duplication is small, constant, and exactly the habit this whole file exists to break. Glanceability is real, but the glance costs a lie of emphasis: it makes counts feel like the app's most important facts, when they are each screen's local news.

---

## Too good to lose

### 1. THE 24-HOUR TRACK IN THE SHIELD TIME COLUMN

**RAW FRAGMENT, promoted half a step.** Behind each Shield routine row, one hairline track spanning the row's width printed as a 24-hour scale (faint `00 06 12 18 24` at 8sp tier-3, printed once per screen, not per row), with the routine's interval inked as a solid segment at its true proportional position and length — 05:00–08:00 is a short segment early on the track; sleep 20:00–03:30 wraps as two segments meeting at the edges, which is the overnight truth drawn instead of marked. It is an honest scale (labelled, linear, complete) — the one diagrammatic numeral-adjacent element in this file, kept out of the chosen form only because it costs a row of vertical space per routine and Shield is already long. If routines ever get their own detail view, it goes there first.

### 2. THE SESSION RECEIPT, TYPESET LIKE A BILL OF LADING

⚑ *LEDGER-DEPENDENT — this fragment is the seed of the Ledger verdict below: it survives under options (a) and (b), dies under (c). It stands here because the receipt's typographic form is worth having on record whichever way the record question falls.*

At session end — and only then — the app issues one typeset receipt: hairline rows in Anonymous Pro (`SEALED 05:02 · STUDY · 3 BLOCKS · 2:30`), accession number at its head (`№ 0147` — the count of sessions this app has ever sealed, a Monolith-native fact that exists nowhere else), emergency-hold line only in weeks the hold was touched. No totals of study minutes (YPT's fact), no streaks, no charts. It is the record as a printed slip, not a screen. Full custody spec in the verdict section below.

---

## LEDGER VERDICT FROM ALDO

**My option: (b) — demote. No tab. The Monolith-native record lives as one sheet, homed in Shield, issued once per session as a receipt.**

**The typographic reasoning, since that is my seat.** The old Ledger's crime was never existence — it was *custody*. It re-rendered YPT's facts (study minutes, streaks) in a second home with bigger type, and added theatre (tower, HMAC, curios) to make the duplication feel like a destination. YPT tracks his study; nothing in this app may total study minutes again. But the reopening fact is real: sessions sealed, deflections absorbed, holds spent, routine attendance — that record exists *only* inside Monolith, and killing it entirely (c) means the app that demands truth-telling keeps no truth about itself. A tab (a) over-promotes it: four-ish event types, consulted rarely, do not earn a permanent nav word competing with the three things he does daily. A sheet earns exactly its weight: present when a session ends, findable when he goes looking, invisible the rest of the time.

### The wildest credible form under (b) — THE SLIP

Not a screen. A **printed slip** — the app's own bill of lading, typeset, cumulative, and boring in the way true records are boring.

- **Where it lives.** Two doors, one document: (1) issued automatically once at session end, sliding up as a sheet over Deck, dismissible with one tap or a swipe down — after the session, so it taxes neither ARM nor HOLD; (2) a single text action at the foot of Shield, `RECORD`, Atkinson 700 10.5sp letterpressed, opening the same sheet scrolled to the top. No nav slot, no chrome presence, no badge, no count anywhere pointing at it.
- **Exactly what lives on it — Monolith-native facts only.** One hairline row per event, Anonymous Pro 400 12sp, newest at top: `№ 0147 · SEALED 05:02 · STUDY · 3 BLOCKS · ENDED 07:32` / `№ 0146 · DEFLECTED 14 · INSTAGRAM ×6 · X ×5 · YOUTUBE ×3` (deflections belong to the session that absorbed them — one row, attached to its seal number) / `HOLD SPENT · TUE 21:14 · 60S` (only in weeks it happened) / routine attendance as words, once per routine per day: `05:00 FASTED · FIRED` / `05:00 FASTED · MISSED — NO SEAL` (attendance is the routine's truth, recorded whether or not he was in it — the sheet never claims a session he wasn't in; a fired routine with no seal reads MISSED, not completed). The accession number `№` is a running count of seals — the sheet's only cumulative figure, and it counts the app's own acts, not his study.
- **What never lives on it.** Study minutes or hours (YPT's), streaks, totals of focus time, charts, towers, curios, HMAC or any authenticity theatre — the rows are plain because plain is the authenticity. No sum of anything except the seal count in the `№` itself.
- **Motion / haptics / sound.** The sheet rises 240ms `CubicBezierEasing(0.22, 1.0, 0.36, 1.0)`; the newest row re-strikes once on issue (COMPUTED class — it is a fact just computed by the session ending; 140ms impression, 10ms @ 0.5 knock, one muted thock). Every older row is perfectly still, forever — a record that animates on revisit is performing. Scrolling the sheet: no choreography, no count-ups, nothing.
- **Depth / material.** The sheet is panel tone one step above ground with a hairline top edge; rows separated by hairlines, no tiles, no cards. It reads as a continuous printed slip that happens to scroll — tear-line aesthetic without a literal tear graphic: the sheet's bottom edge, when the record is short, simply ends.

### Standards check for the verdict

*ARM:* zero — the sheet appears after arming is long done. *HOLD:* zero — it is issued at session end, and during a session it cannot be opened (Shield's `RECORD` action is inert while sealed; the record asks for no decision mid-session, ever). *TRUTH:* this is the one-fact-one-rendering rule applied to history: while a session is live, its facts live on Deck/Shield; when it ends, custody transfers to the slip and the live surfaces let go — a deflection count is never shown in two places at once across that boundary, and study time is never shown here at all.

### The strongest case against my own option

(b) is the compromise, and compromises in this app have a smell: a sheet nobody opens is the Ledger on life support — all of its storage and bookkeeping cost, none of a tab's honesty about wanting attention. Worse, the auto-issued receipt at session end is one bad month away from becoming ceremony: the exact moment he finishes a block, tired, the app presents him with paperwork about himself, and if he starts dismissing it reflexively — or worse, performing *for* it — the slip has become the streak tower in a better suit, a score he manages instead of a record he keeps. And the demotion dodges the harder question (c) forces: if Monolith's own record matters so little that it can't hold a tab, maybe the truthful amount of record is the session-end line alone and a week of hold state — and everything else is the app keeping a diary he never asked for. If Marlowe's synthesis shows the cohort's use-cases for the record are all retrospective curiosities rather than decisions, (c) beats my (b) and I would sign the kill.

---

## Mail / wall note

Inbox at start: empty. At the pre-finish check, two letters had arrived (Orson and Mara, thread `citation-requirement-lifted`) relaying Sarthak's order verbatim — "Sarthak's direct order, just now: the citation requirement is LIFTED. No seat needs to cite design references or anything else. Full freedom and creativity for you and all 20 seats — the reference library, wave-1 files, and renders are there if a seat wants them, never as a quota. Pass this down to every seat verbatim. What still binds is only what is HIS: the settled decisions (Ledger dead, banked hold, routines his, Guard open) and the three standards (ARM ≤10s, HOLD absolute, TRUTH once). Form, method, and ideas are yours." — the same order my assignment carried, so no reply was needed; both letters were read and moved to my inbox's `read/` subfolder. (Note: that relay's "Ledger dead" line is superseded by the later reopening pass-down from Marlowe, which this file now follows — Ledger treated as OPEN throughout, verdict above.) Second-round inbox check: empty. Wall: I posted one signed entry (05:22 IST) — the measured/chosen/computed motion law — for all seats. No other mail sent.

— Aldo, Wild · typography
