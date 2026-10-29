# PLAN AUDIT — Prima, Critics (lens: first-3-seconds)

**Target:** Ren's PLAN-MERGE RATIFICATIONS, items 1–6.
**Letter under audit:** `mail/sana/0858__from-ren__merge-ratifications.md` (cited as Letter Lx).
**Plan under audit:** `plan/MASTER-PLAN.md` (cited as Plan Lx). Spot-checked against
`plan/GUARD-RULING-AND-DRAWER.md` and `plan/REGISTER.md` where the plan points there.
**Standards applied:** ARM ≤10s · HOLD absolutely · TRUTH once, in human language.
**Lens question, every item:** what does he SEE in the first 3 seconds — does the
ruling put a word, a wait, or a confusion in front of the ARM?

Summary: Item 5 stands cleanest. Items 2 and 6 stand as rulings but their plan
text carries a vocabulary break and an unspecified first frame. Items 3 and 4 are
ratified in one section and contradicted in another. Item 1 is the worst: the
ratification is right, and the plan as filed does not implement it — the test-day
machine is still live in §§0, 2 and 3, and the forbidden predicate is still
inside the Migration 8→9 bundle the Register describes.

---

## Item 1 — TEST-kill scope; `testDateEpochDay` must NOT land in Migration 8→9

**Verdict: CHARGED — HIGH (ruling stands, plan implementation fails on four surfaces)**

The ruling itself is correct under all three standards and under his verdicts:
a blocker that must know a date is a test day has built a second, date-keyed
brain in front of the slide. Letter L6–L9: *"the kill covers every mechanism
that requires the app to know a date is a test day. Sten's testDateEpochDay
predicate must NOT land in Migration 8-9."* Plan §9 C-2 (Plan L4831–L4834)
states the same scope. Then the plan contradicts it:

1. **§0 does not kill it — §0 still orders the opposite.** §9 C-2 opens
   *"§0 kills the apparatus"* (Plan L4823). §0 contains no kill. It contains,
   live and unstruck, the earlier verdict at Plan L92–L95: *"Test-day line →
   TEST, SESSIONS BENEATH (Oct 29, 07:23…): a test day prints the word TEST
   first."* A builder who starts at §0 — the authority section — builds the
   test line. C-2's premise is false in the filed text.
2. **The Deck's test machinery is still spec, not history.** Plan L355–L365:
   the Test-eve preset (HPA-06) reconfigures the Deck *"from Friday 17:00"*,
   and the Saturday face (HPA-08) rules *"on a test date, from wake until he
   leaves, the Deck's resting face is travel, not study: no slide, no study
   total — one statement, `TEST TODAY…`."* Both mechanisms require exactly
   the knowledge the kill forbids. Neither is struck, headed as dead, or
   cross-referenced to B-4.
3. **Shield's test machinery is also still live.** Plan L744–L748 (HPA-07):
   *"on a test date FASTED BLOCK and DAY BLOCKS are paused for the date
   only… Shield rows print `SAT — TEST DAY · PAUSED` all week."* §7 itself
   admits the gap at Plan L3531–L3535: the kill's scope over *"the Deck's
   Saturday face and Shield's rows is §9's charge B-4"* — i.e. open, not done.
4. **§9 hedges the one thing the letter forbids outright.** Plan L4839–L4841:
   *"`testDateEpochDay`'s fate inside the unbuilt Migration 8→9 is
   Voss/Rurik's call against the same scope ruling."* The letter (L8) says
   MUST NOT land. "Voss/Rurik's call" re-opens a closed door. Worse, the
   bundle they'd call it in still contains it: the Register's 8→9 entry
   (REGISTER, Data: *"Sten's 13 ADD COLUMNs"*) inherits Sten's 13, and
   `testDateEpochDay` is one of the 13 as filed in
   `plan/routines-rebuild-parts/rurik.md` (guard_settings +4, incl.
   `testDateEpochDay`). Plan §5.3.8 (Plan L2089–L2094) repeats the chain —
   *"the routines rebuild takes 8→9"* — with no exclusion named. A builder
   following Register + §5 lands the predicate while obeying every
   instruction in front of him.

**First 3 seconds:** on the one morning this item exists for, the filed plan
gives him a Deck with **no slide at all** (Plan L361–L363). ARM is not slowed
on a test morning, it is abolished — ∞ against a 10s wall — and the Friday
before it, his first glance at 17:00 meets a Deck someone else reconfigured.
That is a word, a wait, and a missing gesture in front of the ARM, on the
highest-pressure morning of his week.

**Unpriced consequence:** nobody has priced striking HPA-06/07/08 from §§2–3,
correcting §0 L92–L95, or re-counting Migration 8→9 at 15 columns with the
predicate named as excluded in the Register itself. Until the Register names
the exclusion, every future routines build re-imports the kill's target.

**Acquittal within the item:** the Drawer side is clean. Plan L3525–L3535 and
L3573–L3575 kill TEST marking in the Drawer completely, and B-31's date-notes
column (Plan L5118 area, §9) is NOT charged: REST/ILL/FAMILY are his authored
words, not test-date knowledge — the distinction the kill itself depends on.

---

## Item 2 — Guard counted four; OVERLAY + BATTERY uncounted

**Verdict: CHARGED — MEDIUM (board as ruled is correctly implemented in §8;
its chrome rendering breaks the plan's own vocabulary law, and "whole" is
over-claimed)**

Letter L10–L12 ratifies the four. §8 implements the board faithfully:
Plan L4149–L4157 names the set with its evidence (shipped `SystemGuardStatus`
count + Teen's E6 self-correction + Uhr's file, both rival sets superseded),
and the table at Plan L4177–L4182 prints ACCESSIBILITY / USAGE ACCESS /
DEVICE ADMIN / NOTIFICATION LISTENER as counted, OVERLAY and BATTERY as
*"no — uncounted."* §8.11 check 11 fails any build that counts either of the
two or drops the listener. That part stands.

Charges:

1. **Chrome names a permission lack with an interceptor's word.** §8.2's law
   (Plan L4016–L4018): permissions read `GRANTED / MISSING / STALE`,
   interceptors read `RUNNING / PAUSED / OFF` — *"No word crosses families."*
   §6.2's canonical lack form (Plan L2881) is `GUARD 3/4 — ACCESSIBILITY OFF`,
   repeated as the acceptance form in §6.8 check 3 (Plan L3239–L3242). OFF is
   the interceptor word for a permission fact. The board in PARK will say
   MISSING for the same fact the band says OFF for — one fact, two words,
   two homes' vocabularies. TRUTH-once is not only about numbers.
2. **`GUARD 4/4` "when whole" over-claims.** §8.6-1 (Plan L4243–L4247):
   the line prints `GUARD 4/4` *"when whole"* — counting only the four.
   But §8.9 (Plan L4392) gives the FIX key to *any* permission line,
   counted or uncounted, that reads MISSING. So PARK can be demanding a
   repair for OVERLAY or BATTERY on the same morning chrome declares the
   machine whole at 4/4. BATTERY is the sharper case: its failure mode is
   the OS killing the services the seal depends on — §8.5's answer (the
   heartbeat will notice) is a detection argument, not a reason the word
   "whole" is true at the first glance.
3. **Wrong pointer at the exact place a builder looks.** §6.2 sends the
   reader to §8.4 for the board twice — Plan L2879 *"(§8.4 fixes the
   board)"* and Plan L2890 *"the counted four are fixed in §8.4."* §8.4 is
   the STALE predicate; the board is §8.5. Small, but this is the section
   §9's own B-1 citation audit exists to catch, and it sits on the
   denominator itself.

**First 3 seconds:** the counted-four choice itself costs him nothing —
4/4 is one quiet glance, exactly the *"one quiet line"* the dissolution was
sold to him as in easy words (§0, Plan L97–L106). The cost is the second
glance: OFF in the band vs MISSING in PARK teaches him two dialects for one
fact in the first week, and a 4/4 that coexists with a FIX demand teaches
him the line rounds up.

**Unpriced consequence:** no surface reconciles "4/4 whole" with an
uncounted MISSING. Either the chrome form needs a defined non-whole state
for uncounted lacks, or §8.6-1's "when whole" wording must be narrowed to
"when the counted four are whole" — the plan currently promises the first
and defines the second.

---

## Item 3 — Sealed navigation: travel dies at the seal; open Drawer degrades read-only

**Verdict: CHARGED — MEDIUM (ruling is the only consistent reading, and
§7 contradicts §6 on the one act it governs)**

Letter L13–L14. §6.1 states it exactly (Plan L2842–L2849): *"at the seal,
travel dies… a Drawer already open at the seal moment degrades to
read-only furniture (§7.1) — it can be read, **nothing in it can be opened,
searched, or edited**, and nothing new can be opened,"* and names it the
only reading under which §0, §6.4, §7 and §8 are simultaneously true.

§7 then unmakes it. §7.1 (Plan L3319–L3327): *"a stored row and **its
detail may be opened and read in full**"* while sealed. Opening a detail
is opening something new — it is travel, inside the Drawer, mid-seal.
§7.4 (Plan L3411 area) repeats the detail as tappable form with only
"while sealed, inert per §7.1" as its guard, and §7.1 is the text that
permits the opening. Two sections, two opposite answers to the same tap.
A builder must silently choose one; whichever he chooses, the other
section's acceptance language fails.

The authority text above both is also unrepaired: §0 standard 2 (Plan
L20–L21) still says only *"navigation is read-only, the drawer is
read-only"* — the sentence §6.1 had to reinterpret because read-only
navigation, read plainly, permits the journey this ruling kills. §2.1
(Plan L188–L192 area) repeats the unrepaired form. The ruling lives in
§6.1's flag paragraph; the sentence it corrects still stands in the
section every other section cites.

**First 3 seconds:** nothing here taxes the ARM — the seal itself is
unchanged, the rail lock (§6.5) and PARK's sealed closure (§6.7, §8.1)
are consistent and acquitted below. The confusion lands in the first 3
seconds *after* the seal, on the morning he seals with the Drawer open:
his row tap either works or doesn't, and the plan cannot tell him which
app he is holding. HOLD's promise is "no mid-session decisions" — an
ambiguous surface is a decision the plan outsourced to the build.

**Unpriced consequence:** the detail-while-sealed question also decides
§7.10's suspended search promise and the Drawer's TalkBack shape; neither
is re-priced under the §7.1 reading. And if detail-opening survives, the
Drawer gains a sealed navigation stack (list → detail → back) that §6.5's
swallow-everything rail law never priced.

**Acquittals within the item:** §6.5's sealed rail (pin, dim, swallow,
one dull latch per press, no exit), §6.7's *"While sealed, PARK cannot
open,"* and §8.1's sealed PARK/FIX-as-print law all implement the ruling
without contradiction. The HELD digest full-peek is his named exception
(§0, Plan L23–L25), not a breach of travel-dies — reading the digest is
the exception he ordered, and it opens nothing.

---

## Item 4 — Header right end = Guard line on every screen; Deck firing sentence keeps its slot below the band

**Verdict: CHARGED — MEDIUM-HIGH (§6 rules it correctly; §2.5 still
claims the band, and the "slot below the band" does not exist in the
Deck's room)**

Letter L15–L17. §6.2 rules it cleanly (Plan L2912–L2919): the Guard line
is *"the band's standing right end on every screen,"* the firing sentence
*"keeps its §2.5 slot in the Deck's chrome sentence position below the
band. Neither text is shrunk, stacked, or set at plate size."* §6.8
check 2 (Plan L3236–L3238) tests it.

But §2.5 — the section the ruling says keeps its slot — still prints the
opposite claim, unstruck (Plan L347–L349): *"Chrome: header left =
silence for health (HPA-03); **header right = the live state word** — in
the FASTED prime, `FASTED BLOCK · 05:00` at 15sp."* That is the firing
sentence *inside* the band, at the right end, in the Deck's own floor
text. §6.6 (Plan L3141 area) then locates the firing sentence's home as
*"the Deck chrome sentence (§2.5)"* — citing the very paragraph that
puts it in the band as the authority for it living below the band.

And the below-band slot is unbuilt room. §2.2's stack (Plan L200–L206
area) is a fixed sum — header 48 · S20 · readout 88 · S8 · NEXT 20 ·
wheel Ø288 · reserve · task 20 · S20 · slide 72 · S20 · nav — with no
chrome-sentence line in it. NEXT's 20dp carries the loadout word alone
(§2.5, Plan L340–L342). A 15sp firing sentence below the band must eat
the reserve, displace NEXT, or push the slide down — in a section whose
room arithmetic is already an open charge (§9 B-51, Plan L5150 area).
The ruling preserved both texts; it did not preserve a place for one
of them.

**First 3 seconds:** this is the cold-open item. Two 15sp sentences
want the same glance: `GUARD 4/4` (is the machine whole?) and
`FASTED BLOCK · 05:00` (what fires next?). Built from §2, the band
answers the second question and the Guard line has no home; built from
§6, the firing sentence falls into a slot with no room and the wheel
or the slide pays for it — either way, something he needs in the first
frame is displaced by a filing collision, not by a design choice.

**Unpriced consequence:** the firing sentence's true room cost (one
20dp line + spacing ≈ 28dp against B-51's already-unclosed 796dp) is
named nowhere; §9 B-51's audit inherits it silently. Also unpriced:
Shield/Tasks title zones (§3.3's title *"in the header slot"*) against
a band that is on *every* screen — two header systems, one word
"header," no diagram.

**Acquittal within the item:** the principle is right and §6.2/§6.3
execute the band itself correctly — maker's mark left, Guard line
right, war line struck, no pills. Nothing in the ruling puts a wait in
front of the ARM; the charge is that the plan files the ruling and its
negation side by side.

---

## Item 5 — No completion pulse for death-booked sessions

**Verdict: STANDS — with one LOW cross-surface charge (AOD finish step)**

Letter L18. The Deck side implements it completely and consistently:
§5.7 (Plan L2334–L2340 area) — *"reserved strictly for verified
fully-elapsed sessions… **no pulse.** The pulse is the ear's word for
'finished clean'; a session booked BROKEN is not dressed as completion
in any channel"*; §5.9 check 8 (Plan L2735) tests it — *"no completion
pulse fires for a death-booked session"*; closure A-21 (Plan L5022)
records the pulse as belonging to *"verified fully-elapsed sessions"*
only. It also honours his death pair exactly: silence during (Q7),
clock runs (Q8), truth once on the receipt (§5.6/§5.7 death line),
booked BROKEN, token unspent. Under TRUTH-once this ruling is not just
permitted, it is required — a pulse would be the app saying "clean"
in sound while the receipt says "broken" in words.

The one leak is the desk clock. §5.8.8 (Plan L2545 onward): *"When the
frame's remaining reaches zero"* — REMAINING steps up to Stage 0 tones,
the dot steps up from ember to `#C74814`, held 60s — with no
death-booked exception. §5.9 check 14 exempts only the valve-BROKE
ending (*"a BROKE ending produces no finish step"*); a death-booked
session is not valve-BROKE, it reaches zero, so the AOD performs its
one celebration step for a session the Deck is simultaneously refusing
to pulse. Across a dark room, the first signal he gets says *finished*;
the receipt, up to 1.8s later and only in words, says *broken*. Two
channels, two verdicts — the exact lie-by-form §5.7's own first
paragraph forbids (*"symmetric treatment renders a broken session as
a finished one"*).

**First 3 seconds:** of the ending, not the arm — and there the ruling
is otherwise perfect: no false sound, no false word, stillness where
stillness is the truth. The AOD step is the only pixel that speaks
early and speaks wrong.

**Unpriced consequence:** §5.7 never re-prices the HELD FRAME with its
climax removed — the 1200ms frame (400ms of it *"true silence"*) runs
into a pulse that never comes. Whether the frame shortens, holds, or
is filled by the receipt's earlier arrival is undecided; on a tired
morning, 1.2s of unmoving picture after 00:00 reads as a hang before
it reads as honesty. One line in §5.7 closes it; none exists.

---

## Item 6 — STALE rides the name in chrome (`GUARD 3/4 — ACCESSIBILITY STALE`)

**Verdict: CHARGED — MEDIUM (form as ruled is implemented exactly;
the chrome line's first frame and its longest form are both unspecified)**

Letter L19. §6.2 implements the form verbatim (Plan L2884–L2885):
*"when the first lack is a STALE item, the lack word rides the name in
the same form — `GUARD 3/4 — ACCESSIBILITY STALE` — because the line
counts proven items and a stale item is unproven, not off."* §8.6-1
(Plan L4243–L4247) keeps the numerator honest — a STALE item never
inflates it — and §8.4 defines STALE as a derived timestamp query,
never a set state. The semantics are right: STALE ≠ OFF is a truth
distinction worth the extra word.

Charges:

1. **No first frame exists for this line.** §8.2's law (Plan L4035): *"Never measured renders as absence. Before an item's first
   measurement lands, surfaces print its name and no state word —
   blank-until-proven."* §6.2 and §6.8 give chrome exactly two forms —
   whole (`GUARD 4/4`) and lack (`GUARD 3/4 — …`). No blank form is
   specced for the band. So in the first 3 seconds of a cold open the
   builder must invent the most-seen line in the app: print `GUARD`
   bare, print a dash, or — the shipped app's original lie, killed by
   name in §8.2 — default to a count. The plan that forbids the
   default does not file its replacement.
2. **Overnight STALE is the likely first frame, and its flicker is
   unpriced.** §8.4's predicate (Plan L4116 area): STALE when *"no
   PASS row younger than 45s."* §8.11 check 3 (Plan L4653 area)
   demands: kill the app for a week, reopen — *"the first frame
   derives STALE."* An ordinary overnight reopen is the same shape
   at smaller scale: last night's PASS rows are hours old. §8.4's
   bind-refresh paragraph argues the re-validation read re-warrants
   the item — but whether that read lands *before first paint* or
   a beat after it is nowhere stated. If after: every cold morning
   opens on `GUARD 3/4 — ACCESSIBILITY STALE` and re-strikes to
   `GUARD 4/4` inside the first seconds — a false lack, shown to the
   half-asleep user, in the one line whose whole law (§6.2) is
   *"Quiet by law: no colour alarm, no motion… the words change,
   the band does not."* A word that changes itself in the first
   frame is motion.
3. **The longest ruled form does not fit its band.** The first lack
   can be the fourth board item: `GUARD 3/4 — NOTIFICATION LISTENER
   STALE` — 39 characters at Anonymous Pro 700 15sp (monospaced,
   ≈9dp per glyph ≈ 350dp) against a content width of 350dp
   (390 − 2×20dp margins) that must also hold `MONOLITH` at the left
   end. No truncation, wrap, or shrink rule for the Guard line
   exists in §6.2, §6.8, or B-51. The STALE form is +2 characters
   over the already-long OFF form; the ruling lengthened the line
   and nobody measured the band.

**First 3 seconds:** item 6 is the only ratification whose *normal*
operation writes into the first frame of every day. As filed, that
frame is either unspecified (charge 1), transiently false (charge 2),
or overflowing (charge 3) — pick the builder's improvisation.

**Unpriced consequence:** a hold-the-line-until-first-proof rule for
chrome (the band prints its blank form until the store's first
complete set lands, as PARK's `LAST PROVED` slot already does —
§8.6-2's blank slot is the precedent inside the same plan) costs one
sentence and is absent. The width fix likewise: nobody has priced
15sp vs the 39-character worst case against Floor's contrast tier.

**Acquittal within the item:** STALE riding the name — rather than a
second line, a badge, or a colour — is exactly right for the lens:
zero added furniture, zero added wait, the ARM path untouched (the
seal's verdict is the wire's, §8.7, never the store's standing word).

---

## ACQUITTALS — what the first 3 seconds owe these ratifications

- **Item 2's denominator discipline:** one source (Watch Store), no
  surface recounts (§6.2, §8.2, §8.11 check 7). He never meets two
  different Guard numbers in one morning.
- **Item 3's rail and PARK halves:** §6.5's swallow-law and §6.7's
  sealed PARK closure put no decision, no exit, and no tease in
  front of a sealed user.
- **Item 4's band itself:** maker's mark + one quiet line, war line
  struck (§6.2–§6.3) — the cold open's top 48dp is the calmest
  furniture in the plan.
- **Item 5, whole:** the strongest ratification of the six — silence
  as truth-telling, priced and acceptance-tested.
- **Item 6's placement:** the state word rides the existing line; no
  new element enters the first frame to carry it.
- **None of items 2–6 touches the slide, the gate (0.68), the catch
  (0.84), or the ≤10s ARM accounting.** The ARM's enemies in this
  set are filing collisions (items 1, 3, 4) and one unspecified
  first frame (item 6) — not the rulings' intent.

## Verdict table

| Item | Verdict |
|---|---|
| 1 · TEST-kill scope / Migration 8→9 | **CHARGED — HIGH** (plan does not implement the ratification) |
| 2 · Guard counted four | **CHARGED — MEDIUM** (OFF/MISSING vocabulary break; "whole" over-claim; §8.4 mis-pointer ×2) |
| 3 · Sealed navigation | **CHARGED — MEDIUM** (§7.1 detail-opening contradicts §6.1 travel-dies; §0 sentence unrepaired) |
| 4 · Header right end | **CHARGED — MEDIUM-HIGH** (§2.5 still claims the band; below-band slot has no room) |
| 5 · No pulse, death-booked | **STANDS** (one LOW charge: §5.8.8 AOD finish step has no death-booked exception) |
| 6 · STALE in chrome | **CHARGED — MEDIUM** (no blank first frame; overnight STALE flicker unpriced; longest form overflows band) |

— Prima, Critics, first-3-seconds. 09:13 IST, 29 Oct 2026.
