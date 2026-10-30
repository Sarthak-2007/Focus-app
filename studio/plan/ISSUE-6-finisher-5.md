# ISSUE #6 — FINISHER-5 — THE DECK, WHOLE-SCREEN GALLERY REVIEW
## Finisher seat (FINISHER-5), Rost's cell · Fri Oct 30, 2026
## Status: DECLARED, paper only. Nothing compiled. Nothing committed.

**Brief:** judge, do not redesign. Hold every other cell file's spec
against one bar — better than anything, commercial level and above —
calibrated on the one surface Sarthak has loved: the receipt slip.

**Read whole, as briefed:** `AUDIT-deck.md` (§1–§11, Rost's rulings
stand), `ISSUE-6-triage.md`, `ISSUE-6-designer.md` (D1–D15),
`ISSUE-6-innovator.md` (P1–P7 + four wrong-target flags),
`ISSUE-6-builder-map.md` (F1–F22 map + F1 ledger), and
`TactileDeckScreen.kt` on this branch. Calibration read whole:
`AUDIT-receipt.md` + `RECEIPT-REVIEW.md` (the slip's two audits),
`FINISH-LIST-1.md` and `EVERY-SCREEN-REVIEW-BUILD1.md` for the
studio's measured night/luminance record.

**Tree state, verified by grep/diff at review time (not assumed):**
branch `agent/issue-6`; the working tree carries exactly one landed
item — F1's cut (20 parameters + 3 orphaned imports, 66 deletions
across Deck / MainActivity / render test, uncommitted). Everything
else in the Deck file is still the audited room: triple-printed
hero, FLOW cap live at 8dp, floored/inverted `progressFraction`,
`%` text, QUEUE print, GRACE/VALVE swap chips at 58dp in a 72dp
zone, uniform `spacedBy(12.dp)`, vertigo blur on the card itself
(peak 6dp by the `6f * 4f * t(1−t)` term), dead `shadow` import,
zero `contentDescription`/`semantics` in either Canvas view file,
68 raw-hex colour hits across Deck + components + views. Where a
census line below says "the spec," it means the cell files' spec;
where it says "the tree," it means this verified state.

---

## §0 · THE BAR — WHAT THE RECEIPT SLIP ACTUALLY IS

The slip is not loved because it is pretty. Reading its two audits
back to back, the loved object passes six tests no other screen in
this app has ever passed at once. These six are the gallery bar
for every F-item below. A spec "reaches gallery" only if, landed
exactly as written, the Deck would pass the same six.

1. **One voice, completely specified.** The slip is Anonymous Pro
   11sp at 1sp tracking, and its audit can rule on *every line it
   can print, in order* — 21 inventory items, each KEEP / MOVE /
   KILL with the exact words. There is no line whose final
   lettering is "the form to beat." If a Deck spec leaves a
   string, a size, or a token as a form-to-beat, it is below the
   slip, whose builders never had to guess a word.
2. **The omission law.** A figure the machine cannot back does
   not print — HELD vanishes whole rather than print −1; PLANNED
   vanishes on FLOW. Absence over falsehood, enforced per line.
3. **The brightest object is the document itself.** Aug's
   acquittal: "the slip the brightest object in every frame."
   Luminance order = importance order, per-pixel, in every state
   — not by intention, by measured frame.
4. **Stillness after arrival.** No re-strike, no animated numbers,
   no auto-dismiss. The finished thing does not perform.
5. **Detention honesty.** The slip arrives only when the machine
   has *proven* it stopped (E1200–E1800). Finish is gated on
   proof, never on a timer's optimism.
6. **A document, not a card.** Square-cut intent, section rules,
   grouped rhythm (8 within / 16 between), one ink for the list —
   the slip's finish rulings (A5/A6) exist precisely because its
   first shipped form *was* a Material card (24dp radius + border)
   and that was ruled a kill. Any Deck spec that finishes a card
   the Fusion is about to delete, or reuses a convicted component
   as its "finish," is spending below the bar.

Two acquittals from the Deck's own audit also stand as part of
the bar, because they are already at it: the **header band**
(audit §3: "the one surface already at gallery standard") and the
**wheel as an object** (unanimous best object in the app). The
Deck does not need to become the slip; it needs its *specs* to be
as finished as the slip's audit is. Most of them are not. That
gap — between a correct verdict and a finished specification —
is what this census measures.

**Census key:** REACHES (landed as written, the frame passes the
six tests) · NEAR (one named step short) · SHORT (the spec itself,
not the code, is unfinished — a builder landing it faithfully
still ships below gallery).

**Tally: REACHES 2 · NEAR 6 · SHORT 13 · bar-debt open 1 (F22).**
(F21 is counted in REACHES only as record hygiene; it earns the
screen no gallery credit — see its line.)

---

## §1 · FINISH CENSUS — F1–F22

### F1 — Cut the 21 dead parameters · **SHORT** (as a finish; the edit itself is exemplary)
The builder's ledger is the best paper in this cell: per-parameter
body-count, call-site, and side-effect proof, and the honesty to
report **20 removed, 1 already absent** (`onSimulateIntercept`
exists nowhere on this tree) against the audit's 21. That is
slip-grade record-keeping. The *spec* still falls short of
gallery on the innovator's flag, which I uphold in full: F1 is
priced and sequenced as a standalone S that declares the disease
cured at its cheapest symptom. The flat signature that grew 21
dead wires still stands (now ~41 parameters), the default-value
camouflage the innovator names (`onEndSafeBreak = {}`,
`warrantProvider = { …UNPROVEN }`) still stands, and no gate
prevents wire 22. The receipt's standard is a *law that holds
when nobody is watching* (the −1 law holds in the migration, not
in a comment). F1's law holds in a ledger, once. Gallery requires
P3's shape or at minimum the graveyard test; as specced, F1 is a
clean room in a house whose doors still open onto the swamp.

### F2 — FLOW cap killed; mode printed · **NEAR**
The principle is gallery: one door (the wheel), the mode printed
never hidden, theatre struck. The designer's successor print —
hub primary `FLOW`, foot line `NO END` — is exact and final. Two
shortfalls keep it NEAR. (a) The *interim* form (builder map +
F4's eyebrow recipe) posts the mode's explanation on the eyebrow,
the screen's quietest voice at 10sp — the innovator's
wrong-address charge applies to the interim exactly as to F4,
and the interim is what ships first. (b) The builder map prices
the `onSelectStudyPreset` cascade as a knock-on inside F2, but
the triage's LAND NOW list does not carry the cascade explicitly;
a merge that kills the cap and leaves the wire recreates F1's
disease inside F2's own landing. One line in the triage would
close it; the step is named, therefore NEAR, not SHORT.

### F3 — % killed; progress made true · **SHORT** (interim) — the truth reaches, the object does not
The truth terms are complete and slip-grade: elapsed/target,
filling, no floors, FLOW carries no bar — the omission law
applied to a denominator. But the interim object the spec leaves
standing is a 6dp Material track at 3dp radius with an ink fill:
the most generic progress furniture in Android, specified by
nobody in this cell (no width language, no end treatment, no
tone step against the card). The slip's bar (test 6) is that a
generic component wearing the app's ink is still a generic
component. Only F18's rim ink is a finished object, and F3's
spec does not require the interim track to borrow any of its
discipline. A builder lands F3 faithfully and the running Deck
still carries a stock bar under a bespoke wheel. SHORT.

### F4 — Eyebrow prints the recipe · **SHORT** (as F4; cured only by being absorbed into F18)
I uphold the innovator's flag as the census verdict: right words,
wrong address. The unexplained arithmetic is committed at the
wheel (hand sets 50, a distant numeral says 2H 30M); F4 posts
the explanation at 10sp on the element furthest from the hand.
The designer agrees in substance — D3 rules the eyebrow's final
form is *deletion*, the recipe living in the hub sub-line
(`+10 BREAK`, `3 × 50`). So F4 as a standalone fix is a
transitional tenancy its own designer has already evicted, and
the audit's S price buys lettering on a condemned line. What
reaches gallery is D8's hub sub-line, counted under F18. F4's
own spec, judged alone as the brief demands: SHORT — and the
cell should stop pricing it as a landing and price it as F18's
first commit.

### F5 — Break hero: the hero IS the break · **REACHES**
The first full pass in the census. The designer's restatement
for the Fusion is complete: hub = break countdown (the largest
print answers the state's one question), foot line `BACK AT
HH:MM`, END BREAK in the utilities' room at its existing correct
seat, grace countdown in the hub by the same law. Every word is
final, every address is named, the 1 AM failure it cures is the
audit's own walk. It passes tests 1, 3 and 4 outright: one voice,
the break is the brightest fact because it is the *only* hero
fact, and nothing else moves. Caveat recorded, not charged
against the verdict: the builder map still routes F5 through the
old hero/eyebrow addresses, so the map — not the design — is the
stale document here. Rost should have the builder re-map F5
onto D8's addresses before it is built from.

### F6 — The chair: one verb STOP · **NEAR**
The audit's strongest verdict and the cell's best composition:
one chair, one verb, whole session, price printed on the chair
itself, countdown in the hero/hub, the lying `(60S HOLD)` dead.
It falls one step short of the slip's test 1: the chair's final
lettering does not exist. `STOP — USES YOUR 1 BREAK THIS WEEK`
is explicitly "the form to beat," and no cell file rules (a)
whether the token count prints when it is not 1, (b) the free
window's lettering inside 10s (does the same chair print plain
`STOP`? the designer implies it, nobody writes it), or (c) the
type step — the chair is the running Deck's only verb and its
size/weight/tracking are nowhere specified, while the slip's
audit specifies its two bold statements to the sp. A verb whose
words are unfinished is not yet gallery, however right its
grammar is. NEAR, and the closest NEAR in the list.

### F7 — Task row → ghost title · **NEAR**
The ghost is specified to gallery restraint: idle read-only,
Atkinson 15sp at quiet ink, ellipsis, no label, no count; running
at full ink with DONE in its key-cap body. That is slip-grade
demotion — the line earns exactly its question ("what am I
sealing myself into?") and no more. Two steps short. (a) The
Tasks side of the move is assumed, not specified: the builder
map says `FocusSequencerScreen.kt` needs "no change expected"
for receiving sub-steps and the QUEUE door — an expectation is
not a spec, and a door that lands nowhere finished makes the
Deck's eviction a forwarding address to an unjudged room.
(b) DONE's finished body depends on F13's radius and F17's ink
rank, both SHORT below; the ghost's one surviving verb inherits
two unfinished specs. NEAR.

### F8 — Seam stamps → PARK · **REACHES** (Deck side; PARK side unjudged and flagged)
As a Deck finish this is complete: both stamps struck, no
forwarding print left behind (designer D14 correctly refuses the
"moved to PARK" courtesy line that would re-render the fact),
the studio stops talking to itself in public. Subtraction
specified this cleanly *is* finish work — it is the same move as
the slip's outcome-word kills. The flag: the builder map grows a
stamp row on `ParkDrawer` with no finish language at all for the
receiving surface (no type, no rank, no silence law for PARK's
board). The Deck reaches gallery by this eviction; whether PARK
falls by receiving it is outside every cell file in this issue,
and nobody should call F8 closed-system until PARK's seat for
the stamps is at least one paragraph long.

### F9 — Sentence words plain; FIX re-aimed; fault promotes · **SHORT**
Intent is right on all three limbs and the placement ruling
(designer D10) is exact. The wording — the limb that *is* this
item — is unfinished in every file: the audit offers one form to
beat (`NOT SURE BLOCKING IS ON — TAP TO CHECK AGAIN`), the
builder map repeats "rewrite plain," and neither enumerates the
final sentence for each cause class (accessibility / unstamped /
usage-denied / usage-unset / OFF backstop), though the classes
exist and are acquitted as correctly routed in the intercept
audit. Compare the slip: its audit rules the footer's corrected
sentence *verbatim, clause by clause*. Five sentences that a
frightened man reads at 1 AM cannot ship as "plain, to be
written." The misroute cure is likewise specified as a direction
("the pane that owns the lack") without the branch-by-branch
destination table. SHORT — this is F6's defect without F6's
excuse of a single string: here five strings are missing.

### F10 — Hero single-press · **SHORT**
Schein's ruling is the right finish and the designer carries it
faithfully (catch ghost dies, one tokenised shadow ghost, ghosts
off on FLOW and on any ticking readout, kill-both fallback). The
spec still falls short in its own terms: the surviving ghost has
no token *value* anywhere in this cell — no day/night offset
tone, no opacity, no offset distance beyond "one direction" —
because it waits on F17, whose token table also does not exist
(see F17). The current tree's ghosts are raw hex
(`0x80000000` / `0x99D8D1CB` shadow, `0x594A423C` / `0xB3FAF6F0`
catch) plus a fake day emboss `0x66FFFFFF` on the eyebrow that
the audit orders dead. And the designer's burn-in rider — the
ticking hub inherits the AOD's shift discipline — is recorded
as "builder to confirm the mechanism exists view-side": an
unconfirmed mechanism inside a finish spec is a hope, and the
receipt's standard (test 5) is that finish is gated on proof.
SHORT, by dependency and by one unproven mechanism.

### F11 — The card gets its shadow, or stops claiming lift · **SHORT — the weakest finish spec in the list**
An either/or is not a finish. "2dp day / tone-only night" names
an elevation with no shadow colour, no alpha, no offset, and the
alternative ("delete the import and the claim") is priced as
the same item — two opposite landings, one price, no render
criterion for choosing. The designer then correctly rules the
whole item interim-to-moot: under F18 the panel dies and a
ground-standing wheel "claims no lift and needs none." Spending
a sitting finishing the shadow of a condemned card, to a spec
that cannot be checked, is the exact inverse of the slip's
discipline (its card was killed, not shadow-tuned). The tree
meanwhile keeps the dead `shadow` import at line 36 — the claim
and the cure both still fictional. SHORT; recommend Rost strike
F11 as a landing and let it die with the panel at F18, recording
the strike the way the builder recorded F1's 21st parameter.

### F12 — Unlight the utilities · **SHORT**
I uphold the innovator's third flag in full. The audit specifies
the cure *as a raw hex* (`#2A2320`) inside the same fix list
whose F17 exists because raw hex made the screen
un-recolourable. The designer repairs the form (the hex becomes
a scheme token; the law becomes the staircase) but no file
states the actual law as a checkable relation — glyph > card >
controls > ground with any ratio, tolerance, or per-scheme
value — and the night-only scope leaves the day staircase
unspecified while the Lamp Test (§2) must judge both. The HELD
noun fix (`HELD · n NOTIFICATIONS`) is bundled into this item's
sitting without its own line or price in the builder map, so a
merge can land the ink and drop the noun unnoticed. SHORT: a
luminance *relationship* frozen into one night value, in the
file that forbids exactly that.

### F13 — Key-cap radii 8dp → 12dp · **NEAR**
The crispest small spec in the list: two named caps, one number,
"two radii app-wide, no third." It is NEAR, not REACHES, on two
counts. (a) "App-wide" is asserted from the Deck alone — the
builder map verifies two `RoundedCornerShape(8.dp)` sites in the
Deck file and no other screen; the slip's standard is that a
law's census is complete before the law is stated. (b) The
Fusion deletes one of its two subjects (the FLOW cap's successor
is hub print, not a cap), so the item as written finishes a cap
that F18 removes; the durable content is "DONE = 12dp," which
should be re-stated as a property of F7's verb body, not a
two-cap item. As a sitting's edit it is flawless; as a gallery
claim it is one cap wearing a plural.

### F14 — Group by silence + scroll-edge fade · **SHORT**
The principle is the slip's own rhythm law (test 6: 8 within /
16 between, ruled sections) and the Deck's version of it is
half-written. The designer prices exactly one number — 20dp
*between* objects — and leaves "tighter inside the object"
without a single inside value: the instrument's internal beats
(eyebrow→glyph→wheel→foot, hub sub-line gaps under F18) have no
spacing table in any cell file, though the tree's current
uniform `spacedBy(12.dp)` + card `spacedBy(10.dp)` is the precise
disease (uniform spacing is how five machines read as one pile).
The fade is worse: no height, no gradient stops, no token — the
studio already owns this number (Finish List F-04: 24dp fade,
transparent → ground) and no Deck file imports it. A finish item
whose two halves are one number and one gesture is SHORT.

### F15 — Vertigo moves to the ground · **NEAR**
The best motion spec in the cell: page recedes 0.98, blur peak
3dp, same curves, same 1050ms, instrument seals in focus — and
under the Fusion it sharpens correctly (ghost title, utilities
and ground recede; the wheel never blurs, never scales). It is
the Deck's one ceremony and it is finally aimed at the room
instead of the instrument. NEAR on proof and scope, not on
taste: (a) "the ground" is never enumerated post-Fusion — scroll
column only, or pinned stack too, and does the STOP chair recede
in the same frame it appears? — and the builder map's "root
Column behind the instrument" predates the Fusion's layout, so
the transform's target view is genuinely ambiguous between the
two files; (b) the tree's current peak is 6dp on the card, so
the spec halves a blur nobody in this cell has rendered at 3dp
on the ground — no frame, no census. The slip's arrival (feed,
not fade) was ruled from frames; this ceremony has none yet.

### F16 — Pinned stack collapses; proof drawer · **SHORT** (composition REACHES; finish SHORT — split verdict, counted SHORT)
The composition is the audit's core and the designer's D9 is
its most careful page: drawer at 0dp when shut (never a reserved
slot), one settle beat, TIME/ENDS never visiting the drawer,
app count's standing home the imprint, BLOCK state-only, Dens's
law (fault gets more room than health) governing the growth.
As *arrangement* this reaches gallery. As *finish* it is the
least specified major object in the issue: the drawer has no
surface language at all — no tone step against ground, no radius
or square-cut ruling, no hairline treatment, no open/close motion
(slide? fade? instant cut? — each reads as a different app), and
its body is specified as a *reuse* of `ProofStripBand`, the very
component whose standing form the audit convicted, seam stamps
and all. The slip's test 6 again: the loved object stopped being
a Material card by explicit ruling; the Deck's new second-most-
important object is currently "the old strip, sometimes." Until
the drawer gets one paragraph of surface + motion language, F16
lands a better room with an unfinished object in its centre.
SHORT, and the highest-leverage SHORT in the list.

### F17 — Tokenise the light · **SHORT**
Direction is gallery and the designer's token vocabulary is
right — exactly four ranks (heroGlyph / instrument / control /
ground) per scheme plus the single-press ghost token, "if a Deck
colour cannot be named as one of those ranks, it does not belong
on the Deck." But the sweep's census is incomplete against the
tree: my grep counts 68 raw-hex hits across the three named
files; the builder map inventories roughly a dozen sites (5 Deck
hexes named, "5 sites" in components, wheel/slide paints
unnamed). No token *table* exists — no token names with per-
scheme values that a builder could land without choosing colours
himself, which means the most colour-critical item in the issue
delegates its actual decisions to build time. F10 and F12 both
wait on this table. A tokenisation whose inventory misses most
of its instances and whose values are unwritten is a policy,
not a finish. SHORT.

### F18 — The Fusion · **NEAR — the closest object to the bar in this issue**
This is the only item that aims where the slip sits: one object,
one address, Δ = 0 across the seal, the dial becoming its own
clock, progress as rim ink, FLOW's ring never completing (the
omission law in geometry). The designer's three-lives spec
(idle span + recipe sub-line / locked at seal / ticking hub) is
real specification, and its kill-list is honestly checkable
("if any of the five is still composable after F18 lands, the
Fusion has not landed"). It stays NEAR on finish completeness,
measured against the slip's 21-line inventory standard:
(a) hub typography is sized by class, not number — idle span and
MM:SS are "same size class," the sub-line is specced (Anonymous
Pro 10sp) but the primary's sp/weight/tracking are inherited by
reference to a hero the Fusion deletes; (b) rim ink has no
mechanics — fill direction, per-tick granularity, the exact
rendering of "never completes" for FLOW (empty ring? partial
arc that refuses the last tick?) are all unwritten, and rim ink
is the running screen's only progress fact; (c) the wheel's own
print finish (BEYOND-deck D9's staircase voices for ticks and
numerals, the reader dot at glyph tier) is not imported into
any issue-6 file, so the Fusion's instrument inherits the
audited wheel's unresolved light (measured in Finish List Pass
2: specular centred 300° against a 245.2° registered lamp,
night minors at ~2.15:1) without ruling on it. None of these
needs a redesign — they need one more page of numbers. NEAR,
and the page is owed before build, not after.

### F19 — Receipt dismissal · **SHORT**
A Deck item whose entire spec lives in another screen's file,
and the borrowed version is thinner than its source. The builder
map's two lines (scrim tap shakes; swipe or PARK/rail dismisses)
omit the receipt audit's own fuller law for the same gesture —
dismissal on a real DOWN, the 2-second dwell from the existing
publication stamp during which taps pass through, faulted slips
dismissing only explicitly. The shake, this item's one original
finish word, has no amplitude, duration, or curve anywhere.
Judged as a Deck spec it is a cross-reference with a gesture
name. SHORT. (The receipt's own A8/A9, landed whole, would make
this REACHES by reference; the cell should import them verbatim
rather than paraphrase.)

### F20 — Print PARK on the rail · **NEAR**
Functionally complete and honestly flagged by the builder as
host chrome outside the Deck touch list. As finish it is one
word with no voice: no file states its size, family, weight,
ink rank, or letterspacing relative to the rail's four named
stops ("same print voice" in the builder map is an intention,
not a spec — the stops' own values are never quoted). The audit
charged this zone precisely because a 48dp unnamed target is
unfinished print; printing the word in an unspecified voice
half-discharges it. NEAR — five values close it.

### F21 — Comment truth pass · **REACHES** (as record hygiene; earns no gallery credit)
Fully specified, fully checkable: sections renumbered or
unnumbered, the header's "12dp universal spacing" promise
corrected against the real 24dp-radius / 16–18dp card, idle
ENDS labelled planned — and the designer carries the operative
half (`· PLANNED` on the foot line) into the composition where
it belongs. I record REACHES so the item is not punished for
being small. But the census must say plainly: this is the only
REACHES besides F5/F8 that costs the screen nothing and shows
the user nothing. A fix list can be padded with such items; the
gallery question is about the other twenty.

### F22 — Accessibility semantics (wheel, slide, rail) · **BAR DEBT — counted SHORT, and the census's standing objection**
The audit's own sentence governs: "bar-disqualifying until
done," "no merge calls this screen finished while it stands."
The triage carries it, correctly, as not-closed. But as a *spec*
in this cell it is the emptiest L in the list: the builder map's
entire content is "give each Canvas view a name, value and
action" — no names, no values, no action labels for the three
controls, on the screen whose primary control is about to be
rebuilt by F18. The innovator's sequencing flag is upheld: the
hub's semantics belong inside F18's acceptance criteria (the
designer even writes the principle — the hub's printed span IS
the value its semantics must speak — then files it as a note
"for the builder," not as a criterion). The receipt passes the
bar partly because every one of its lines was inventoried; a
home screen a blind user cannot operate has not been
inventoried at all. Any report that calls issue #6's Deck
"finished" with F22 in this state repeats, in accessibility,
the exact false-close the studio convicted in ESR-13 (code-true,
render-false). SHORT, flagged to Rost as the item that caps
every other verdict in this file.

### Innovator proposals P1–P7 — finish note (not F-items; judged as specs only)
P1 (edge-triggered truth) and P2 (the truth token) are the two
proposals that would let F-items *stay* finished — P2 is, in
finish terms, the omission law made structural — and P3 is
F1's missing permanence. All seven are machinery specs: not one
carries type, ink, motion, or surface language, so none can be
judged gallery or below it; they are below the finish layer by
design. My objection is sequencing, filed for Rost: P2's single
vocabulary function would have *written* F9's five sentences as
a side effect, and P1's utterance slot would have given F16's
drawer its mouth. The cell is finishing surfaces whose
regenerators (the innovator's word, upheld) are specced in the
same room and scheduled in none. That is how this screen got
its first audit.

---

## §2 · THE LAMP TEST — THREE FRAMES, PURE LUMINANCE

Method, as Schein specified it and the audit endorsed: strip
colour, read each frame as a luminance staircase only. Order
required (designer D13): **glyph > instrument > controls >
ground**, fault excepted by design (drawer/sentence on top).
Verdicts are on the *specced* Deck (all F-items landed as
written in the cell files), with the tree's measured night
record (Finish List Pass 2, Every-Screen Review) as the base
the specs must overcome.

### Frame 1 — IDLE · **CONDITIONAL PASS**
The specced staircase is monotonic on paper: hub glyph in the
glyph token, tick ring/instrument below it, controls stepped
down (F12's intent), ground last, one lit object because the
Fusion killed the card and the drawer is shut. It passes *if*
F17's four-rank token table lands with real per-scheme steps —
the measured base says distrust exactly this: in the shipped
night frames the card interior sampled **#171412 = ground
exactly (594,620 px, zero step)** and the card token was right
in the theme while the render was wrong (ESR-13's class). No
cell file requires a post-landing luminance census of the Deck
as the acceptance proof for F12/F17. Pass, conditional on a
census nobody has specced. A gallery process does not leave its
central claim unverified by construction.

### Frame 2 — SEALED (running, night) · **FAIL — as specced**
The audit already records tonight's Deck failing this frame;
the cell's specs do not cure it, and one fix makes it worse.
Per-pixel, the hub glyph is brightest — that part holds. By
*mass*, the sealed frame as specced carries: the STOP chair
filling its full 72dp zone (F6, correct grammar) in primary ink
(`inkControlFill()` = #4A423C at night in the current components)
— the largest single filled area on the running face — plus
utilities chips in the same ink fill (DESK CLOCK primary,
HELD), plus rim ink accumulating around the ring. F12 steps the
*utilities* down; no file steps the *chair* down, ranks it, or
even names its luminance rank — D13's order says "controls"
third, but the chair at 72dp × full width in control ink is a
dark mass of exactly the class Schein convicted in the current
Deck ("three dark masses shout at rest"). Total-luminance
reading of the specced sealed frame: a bright small clock over
a broad mid-dark band of chair + chips that outweighs it in
area and rivals it in glance order, at the hour (sealed night)
when the frame is seen longest. The receipt in the same state
is one bright slip on a 0.12 scrim — nothing competes. **The
specced Deck still fails the sealed frame.** This is the Lamp
Test's headline and defect #1 below.

### Frame 3 — FAULT · **FAIL on luminance (PASS on position — and the test is luminance)**
The designer deliberately inverts the staircase in fault:
sentence promoted above the slide (D10), drawer open, fault
"the loudest Deck state by design." Position delivers: the
sentence is first, the drawer has the stage. Luminance does
not: no cell file assigns fault a brightest token. The refusal
sentence in the current components renders in strip tones at
body register; the drawer's fault density is a *fill class*
(cap-tone fill in the older strip language) whose night value
is the same #4A423C family as the chair and chips it must
outrank. Stripped to luminance, the specced fault frame reads:
same masses as sealed, one small sentence brighter than nothing
in particular. The audit's original conviction — "its alarm
quieter than its decoration" — survives the fix list intact,
because the fix promoted the alarm's *address* and never its
*light*. A fault that must be read to be noticed has failed the
test the fault frame exists for. **FAIL.**

**Lamp Test summary: idle conditional pass · sealed FAIL ·
fault FAIL. The Deck, fully fixed to this cell's specs, still
fails Schein's test in two of three frames** — both times not
because a ruling is wrong but because no spec assigns luminance
rank to the two objects the fixes themselves create or promote
(the full-zone chair, the fault sentence/drawer).

---

## §3 · TOP 5 FINISH DEFECTS REMAINING — RANKED

**1. The sealed frame has no luminance law for the STOP chair.**
(F6 × F12 × D13.) The fix list creates the running Deck's
largest filled mass — a 72dp full-width primary-ink chair —
and no file ranks it. Until the chair's token/step is written
into D13's staircase (and the sealed frame passes a measured
census), the Deck's longest-seen frame is finished by accident.
*Cost of cure: one token decision + one census. Cost of
skipping: the Lamp Test fails forever at sealed.*

**2. F22 is an empty L capping a "finished" claim.** Three
Canvas controls, zero specified names/values/actions, scheduled
after the F18 rebuild that should carry them as acceptance
criteria. The audit calls it bar-disqualifying; the cell's
paper treats it as a footnote. Either F18 absorbs it (designer's
own principle, promoted to a criterion) or no closing report
for this issue may use the word finished.

**3. The lettering debt: F9's five sentences + F6's chair words
are unwritten.** The two most-read texts on the fixed Deck —
what fault says, what stopping costs — exist as forms-to-beat.
The slip's audit wrote its footer verbatim and killed three
outcome words by name; this cell has not written its equivalent
page. Every other finish can land and the Deck still speaks in
drafts at its two most human moments (1 AM fault, mid-session
stop).

**4. The proof drawer — the restructure's centrepiece — has no
surface or motion language.** (F16.) Tone, edge, radius, and
appearance motion all unspecified; body specified as reuse of
the convicted strip band. F16 is the L the whole alternative
hangs on, and it is currently specified to the standard of a
layout sketch, not of the object that must be *believed* at
05:00. One paragraph (surface + motion + the settle beat's
duration in ms) closes it; without it the builder invents the
Deck's second-most-important object at build time.

**5. The token table does not exist, and three items are
mortgaged to it.** (F17, dragging F10 + F12.) 68 raw-hex hits on
the tree against a ~12-site inventory; four rank names with no
per-scheme values; the ghost token, the utilities step-down,
and the staircase itself all "to land under F17." F17 is priced
M and sequenced as a sweep; as specced it is a policy statement
whose actual colour decisions — the finish decisions — are
deferred to whoever builds it. Gallery work does not defer its
palette.

*Just below the cut, recorded so they are not lost:* F14's
missing inside-spacing table and unimported 24dp fade number
(the studio owns both numbers already); F18's unwritten rim-ink
mechanics and hub primary size (the best spec in the cell, one
page short); F19's paraphrase of a law that exists verbatim one
screen over.

---

## CLOSING — FOR ROST

The verdicts in this cell are right. Rost's audit is a true
audit, Plan's presentation is the right room, Vega's Fusion is
the right instrument, Muede's chair is the right verb. What this
census finds is that the cell then *stopped one pass early*:
the slip Sarthak loves was not finished by its verdicts — it was
finished by an inventory in which every line, word, token, and
gesture was written down to the sp and the millisecond, and then
measured in frames. Of 22 items, 2 reach that standard outright,
6 stand one named step from it, 13 are specified below it, and
1 open bar debt forbids the word "finished" over the whole.

The Deck does not need new ideas from this seat, and I have
offered none. It needs its last pass: chair + fault luminance
ranked, F22 folded into F18, the sentences written, the drawer
given a surface, the tokens given values — five pieces of
paper, all of them small, none of them optional if the receipt
slip is honestly the bar.

— FINISHER-5, Rost's cell, issue #6. DECLARED, uncompiled,
uncommitted. File: `studio/plan/ISSUE-6-finisher-5.md`.
