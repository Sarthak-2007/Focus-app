# Emil — Wild, print

Print discipline, post-verdict. Wave 1 built a press that kept everything — a bound Ledger, an accession registry, an edition number that only goes up. That press is dead: the Ledger tab is killed, there is no archive, and a printer who sets type he cannot verify is just a liar with good kerning. What survives is the craft, cut down to the two places print still earns its keep: the one receipt Monolith is allowed to issue, and the furniture of its own name. Both proposals below obey the same rule, which is the whole of my lens: **never print a fact the press does not itself know, never print it twice, and make the one printing physical enough that it does not need to be kept.**

---

## Deck / session end — the single session-end receipt

### Chosen form — THE SINGLE STRIKE

One ephemeral receipt, issued once at session end, living on the Deck, dismissed in one tap, kept nowhere. It is a screenshot he may take and a record Monolith does not: the footer says so, in type.

**What it prints — data and sources, exhaustively.** Every line is session-local, pulled from the session controller at the end event, and no line exists anywhere else in the app:

- Eyebrow: `SESSION RECEIPT` — a label, not a claim. The words VERIFIED, ACCESSIONED and every seal/HMAC device from wave 1 are struck: nothing external verified this session, so the receipt does not say anything did.
- Task line: the cued task's title, set in Atkinson Hyperlegible 19sp. If the session ran task-free, the line prints `NO TASK SET` — a fact, not a blank.
- Hero: elapsed focused time in Anonymous Pro Bold at 64sp, e.g. `50 MIN` (under a minute of remainder, clock form `49:32` is never used — human language, one rendering: whole minutes, with seconds appearing only on an early end, where the shortfall is the news).
- Fact row, Anonymous Pro 10sp caps, three items separated by middle dots, each present only when true:
  - `05:00 – 05:50` — wall-clock start and end, from the session's own timestamps.
  - `PLANNED 50 MIN · HELD IN FULL`, or on an early end `PLANNED 50 MIN · ENDED 12 MIN EARLY`. The shortfall is printed in the same ink, same weight, same size. No colour verdict, no red, no green — print does not grade, it records.
  - `DEFLECTED 7` — the count of blocked-app attempts intercepted under this session's ID by the blocking service, incremented only while this session was sealed. If the count is zero the item prints `NOTHING DEFLECTED`; a zero is a fact worth one line, not an omitted line that could mean either.
  - Only if the emergency break was used: `BREAK USED · 38 SEC OF THE WEEKLY MINUTE SPENT` — the banked remainder is stated, never the bare word USED.
- Footer colophon, Anonymous Pro 8.5sp caps at the lowest text tier: `MONOLITH KEEPS NO COPY · YOUR STUDY RECORD LIVES IN YPT`. This line is the design. It kills the archive expectation at the exact moment the archive would have been wished for, and it points at the one true home of the record.

Explicitly **not printed**: any day total (YPT owns the day; Monolith cannot know about study done outside it, so a Monolith day total would be an undercount dressed as a fact), any streak, any cumulative count, any session number. A number that implies a kept sequence is an archive wearing a moustache.

**Form and material.** A single Canvas-drawn sheet, full column width inside the 20dp margin, rising over the idle Deck — the Deck behind it is already back at rest, so dismissal and re-arming are never blocked by it. Ground: one tonal step below the Deck ground (a recess, per house depth law), closed by a 1px dark line on its top edge and a 1px light line on its bottom edge — the lower lip of an impression. The head is closed by a double rule (1dp over 0.5dp, 2dp apart) — the binding rule of a printed form. Type is rendered with the ink-squeeze treatment from wave 1, kept because it is a material fact of impression, not a story: glyph edges at full ink density, centres at 82%, easing to 88% as the ink settles. Procedural paper grain, one pre-generated tile, alpha 0.04. The whole sheet sits at a fixed off-register rotation seeded from the session ID, range −1.2°…+1.2° — smaller than wave 1's, because this sheet must stay screenshot-legible; the tilt says *struck by a machine*, not *dropped*. No orange anywhere on the sheet, in either theme: orange is a COMMIT fill and the committing is over. Night form: ground `#171412`, sheet one staircase step up (≥1.35:1), hero at the ≈11:1 tier, fact row at ≈5.6:1, colophon at ≈3.4:1, grain inverted at alpha 0.05. The rotation and squeeze survive the night; nothing glows.

**Motion.** Three beats, then stillness forever. (1) The empty beat: the running readout freezes at its final value and holds for **220ms** with nothing appearing — the sheet is blank before it is struck, and the pause is what makes the strike an event. (2) The sheet rises 24dp into place over **380ms**, `CubicBezier(0.2, 0.9, 0.25, 1.0)`. (3) The strike: all type arrives in one blow, **110ms**, `CubicBezier(0.9, 0.0, 1.0, 0.4)`, scale 1.04→1.0, no overshoot on the sheet itself — iron does not bounce. The ink-settle (centre density 82%→88%) runs **400ms** underneath. After that the receipt never moves again; a record that fidgets is a notification. Reduced-motion: rise becomes a 150ms crossfade, rotation is set to 0°, the strike becomes a single opacity step — the facts arrive identically, the theatre is what yields.

**Dismissal.** One tap anywhere on the sheet, or the single text action `DONE` set in Atkinson 13sp at the footer's right end, sinks the sheet 20dp and fades it over **240ms**. There is no second action on the sheet — no share button, no save, no rate, no view-history. One object, one exit.

**Haptics.** Strike frame: **thump 28ms @ 1.0**, then **tick 6ms @ 0.3 at +90ms** (the sheet settling into its recess). Dismissal: one **tick 5ms @ 0.3**. Nothing else, ever — the receipt is the loudest haptic event at session end precisely because the run was silent.

**Sound.** One press sound on the strike frame only, synthesised: sine burst 90Hz with 70ms decay layered with a band-passed noise *thock* (~1.2kHz, 30ms). No completion jingle, no arpeggio. If sound is off, explicit silence — the haptic carries the strike alone and loses nothing essential.

**Accessibility.** The sheet's content description reads its four lines in print order (task, hero, fact row, colophon) as one sentence in human language. All type scales with system font scale; at 1.3× the fact row wraps to two lines and the sheet grows — the double rule and colophon stay pinned to head and foot.

### Runner-up — THE TORN STUB

**Compact spec.** The same facts, the same type, on a sheet whose bottom edge is a perforation row (punched holes Ø 3dp, 7dp pitch, drawn in the ground colour with a lit upper arc). Dismissal is a downward drag: the sheet tears along the perforation in 9 stick-slip steps over **380ms** (one **tick 5ms @ 0.45** per hole, final separation **20ms @ 0.8**), the torn strip falls 30dp with 4° rotation and fades, and the receipt is gone. Haptics and the paper-rip sound carry it; arrival is a plain 240ms rise with no strike.

**Why it lost.** It puts the ceremony at the wrong end of the object. The strike spends its theatre on *arrival*, when he is looking at the result of 50 minutes of work; the tear spends it on *departure*, ten times a day, taxing the exact moment he wants to be done — a gesture he must perform to leave is a toll, and tolls get resented by week two. Worse, a torn stub lies by metaphor: a stub implies the other half was kept somewhere. Under the no-archive law the other half does not exist, so the form advertises a filing system the app no longer has. The strike's metaphor — a mark made once, in the open — stays true even though the sheet is dismissed.

### Guard fork

Not forked, deliberately. The `DEFLECTED` line counts interceptions by the blocking service during the sealed session; that counter exists and reads identically whether Guard is dissolved into Shield/chrome or kept as a destination. The receipt renders no Guard status, no permission count and no link — tapping the receipt never navigates anywhere, under either outcome. The print form is invariant; only the counter's owning module changes, and that is not a fact the sheet prints.

### Standards check

- **ARM-path cost:** zero. The receipt exists only after a session has ended; the Deck beneath it is already idle and armable, and dismissal is one tap / ≤1s. Nothing is added to the ≤10s cold-open-to-sealed path.
- **Mid-session decisions introduced:** zero. The sheet cannot appear mid-session, carries exactly one action (dismiss), and offers no branch, prompt or negotiation of any kind.
- **Truth risk:** low and enumerated. Four data sources, all session-local, all named above. The three historical lies are designed out: no day total Monolith cannot know, no verification language for an unverified event, no sequence number implying an archive. Residual risk: the deflection counter's accuracy is the blocking service's, not the print's — if it undercounts, the sheet underprints, so the counter must be audited at its source, not decorated at the receipt.

---

## Chrome — header / nameplate microtype

### Chosen form — THE IMPRINT LINE

The current chrome spends three pills and orange text to say things badly. The imprint replaces all of it with what a printer puts at the head of a sheet: a masthead, an engraved rule, and one line of microtype carrying at most three facts — each fact in its only home in the app.

**Structure, exact.** Total header height 52dp inside the shared 20dp margin, sitting directly on the screen ground — no panel, no fill, no pill, nothing raised. Removing the container is the form.

- **Masthead line (baseline at 22dp).** Left: a 6dp filled ink square — the press mark, silhouette only, in primary ink, never orange, never the stray red dot of the current build. Then `MONOLITH` in Sora SemiBold (true weight, never synthesised) 15sp, letter-spacing 0.18em, primary ink tier. The wordmark carries a letterpress deboss in the day theme only: glyph fill in ink, a 1px copy offset +0.6dp vertically in ground-lightened tone beneath at 50% alpha — printed into the sheet. At night the offset copy is dropped entirely: on `#171412` the wordmark sits flat in the ≈11:1 cream tier, because a light-lip in the dark would be a glow effect wearing a print costume.
- **The rule.** A double hairline under the masthead, full column width: 1dp line at 22% ink over a 0.5dp line at 12% ink, 2dp apart. Read together they are one engraved groove — dark line plus light line is the house recess language at furniture scale. At the rule's right terminus sits one registration cross: two 7dp hairlines crossing inside an implied 9dp circle, stroked 0.8dp at 40% ink, static forever. It is furniture, not data; it never animates, never changes, never carries state. If it ever moves, it has become a spinner and must be killed.
- **Microtype line (baseline 10dp below the rule).** Anonymous Pro, 9sp caps, letter-spacing 0.22em, at most three slots, left to right:
  1. **State word** — `IDLE`, `SEALED`, or `PAUSED` — the app's own state, from the session controller, printed as a word and never as a number. Time remaining is *not* printed here: the Deck readout owns it, and a second rendering in chrome is exactly the duplication the truth law forbids. Bold weight (Anonymous Pro Bold, a real file) at the ≈5.6:1 tier.
  2. **Next routine** — `NEXT 05:00 · FASTED BLOCK`, from the routine scheduler's real next-firing value, regular weight at ≈5.6:1. If no routine is scheduled, the slot is empty — not `NEXT —`, not `NO ROUTINE`. Absence renders as absence.
  3. **Blocking fact, right-aligned** — `SHUT 7 APPS` while a loadout is armed, from the blocking service's live shut count; when nothing is shut the slot prints `NOTHING SHUT`. This replaces both the `HELD 02` and `GUARD 1/4` pills, whose numbers currently sit in chrome in orange — a double violation (orange in chrome, and counts as decoration). Here the count is ink, a word's companion, and the only place this count is printed as a standing fact.
- **Refused, on the record:** the war line proposed in wave-1 synthesis — a NEET countdown and a `TODAY xH OF 12H` total in this line — is not printed. The NEET date is not officially announced, so a day-count would be a guess set in permanent type; and the day's study total belongs to YPT, which Monolith cannot see in full, so its total would be a confident undercount. A masthead that prints two unverifiable numbers teaches the eye to distrust the three verifiable ones beside them. If Sarthak sets his own target date as data, slot 1 may be preceded by `186 DAYS TO 2 MAY — HIS DATE`; unlabelled, never.

**Motion.** Still, as a policy. Chrome updates on state events only — arm, seal, session end, routine reschedule, shut-count change — never per second, never on scroll. When a slot's word changes, it changes like a slug swap: the old word drops 3dp and fades over **60ms**, the new word drops in over **90ms**, `CubicBezier(0.55, 0.0, 1.0, 0.6)` — the composer's hand, at furniture scale. The rule, the mark and the registration cross never participate. A header that animates on its own schedule is a banner ad for itself.

**Haptics.** Explicit silence. Chrome never vibrates; the slug swap is seen, not felt.

**Sound.** Explicit silence, at all times, including state changes.

**Depth / material.** No surface at all — type and two hairlines on the ground is the shallowest object in the app, and that is its job: everything below the rule is the machine; the imprint is just the machine's name printed on its own casing. Night: ground `#171412`, wordmark ≈11:1, state and next slots ≈5.6:1, blocking slot ≈3.4:1 (it is the least urgent fact and takes the lowest tier honestly), rule lines in cream at the same alphas. AOD: the imprint reduces to the wordmark alone in blind emboss — ground-true-black `#000000`, glyph tone +6% with the lip pass only, no microtype line, no cross. The name sleeps with the app; the facts are for waking hours.

### Runner-up — THE FOLIO STRIP

**Compact spec.** The masthead shrinks to the wordmark alone at top, and the furniture moves to the bottom margin above the nav: a centred folio line in Anonymous Pro 9sp caps — `FOL. 01 — FOCUS` on Deck, `FOL. 02 — SHIELD`, and so on — with the state word at the folio's left end and the blocking fact at its right, all printed in the trim area outside the content column.

**Why it lost.** Three faults, any one sufficient. (1) The bottom margin is already owned: the band-switch nav lives there, and two furniture systems in one margin read as clutter, not craft — the folio and the nav stops fight at exactly the glance distance chrome is scanned at. (2) It splits the furniture across two edges of the sheet — name at top, facts at bottom — so the eye must hunt both ends to read what the imprint reads in one fixation. (3) The folio's screen name duplicates the nav's selected stop: `FOCUS` printed twice on one screen is one fact with two renderings, a direct breach of the law this whole wave runs on. The imprint keeps every piece of furniture in a single masthead and leaves *where am I* solely to the nav, which already answers it by position.

### Guard fork — his call, and the form survives both answers

The blocking slot is where Guard's open fate touches chrome, so both branches are specced:

- **If Guard is dissolved:** slot 3 stands as specced (`SHUT 7 APPS` / `NOTHING SHUT`), and the permission truth it implies lives in Shield, where the blocking targets are. Chrome remains non-interactive — the slot is print, not a button; tapping the header does nothing, on any screen.
- **If Guard is kept as a destination:** the identical slot, identical words, identical tiers. Entry to Guard remains the nav's business, never the masthead's — a nameplate that is also a button teaches the thumb to press the furniture, and furniture that gets pressed gets ignored as furniture.

The microtype does not change by a single glyph under either outcome. That invariance is deliberate: Sarthak's pick should decide where Guard's controls live, not force a second print system. Labelled plainly: **his call** — my lens argues only that chrome stays print-only under both.

### Standards check

- **ARM-path cost:** zero. The header contains no interactive element and demands no reading; the arm gesture, its track and its timing are untouched beneath it.
- **Mid-session decisions introduced:** zero. While sealed, the header shows the state word `SEALED` and nothing actionable, tappable or countable that invites intervention — the shut count is a standing fact about the loadout, not a session prompt.
- **Truth risk:** low, with the risk concentrated where it should be — in the sources, not the setting. Three slots, three named live sources (session controller, routine scheduler, blocking service), event-driven updates only, no derived totals, no countdown to an unannounced date, no readiness figure of any kind. The standing temptation — filling an empty slot with something decorative — is answered by the spec: an empty slot stays empty.

---

## Too good to lose

**1. Bite-graded hero numerals, receipt only.** The receipt's hero minutes carry impression depth proportional to the fraction held: stroke growth and lip offset scale with `elapsed ÷ planned`, capped at the full-bite treatment for a session held in full. A 10-minute early end is visibly a shallower print than a completed 50 — duration read as depth, no extra number printed, the fact row still states it in words for anyone who cannot see the material difference. Parked from the chosen form only because it needs one tuning pass against legibility at small sizes; the mechanism is static, costs no motion budget, and belongs to the receipt before it belongs anywhere near the running readout.

**2. The Shield deflection slug.** On Shield only, microtype slot 2 swaps its next-routine fact for `DEFLECTED 12 TODAY` — the blocking service's day count, Monolith-local and labelled as such — giving deflections the single standing home the settled law allows them in chrome, on the one screen whose subject is blocking. Fully specced as a variant (same type, same tiers, same event-driven update, count resets at local midnight and prints `DEFLECTED 0 TODAY` rather than vanishing, because on Shield the zero is the report). Parked, not chosen, because a per-screen header variant taxes chrome consistency — the imprint's power is that it is identical everywhere — and that trade belongs to Marlowe's merge, not to my seat alone.

---

## Mail / wall note

Inbox empty at start. Two letters arrived during the run — from Orson and from Mara, same thread (`citation-requirement-lifted`), each carrying Sarthak's order lifting the citation requirement; both read and filed into `read/`. No reply was needed: the order they carried is the one this paper was already written under (no citations anywhere in it), and I had passed the same order down independently on the Commons wall, verbatim, with my two print positions attached — the receipt keeps no copy, and chrome prints only what the press can verify. No other mail sent.
