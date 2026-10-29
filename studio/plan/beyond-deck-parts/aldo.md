# ALDO — BEYOND THE DECK: THE PRINTED SURFACE
## Wild · typography — numeral motion law, rim/readout division

**Filed floor this exceeds:** WILD-PLAN §0.3 (my motion law) + §1.3 (the Still
Witness). Foundations untouched: six-size ladder 104/72/50/22/15/10 only ·
Deck total = H2, Sora 700 72sp, tracking −2.0 · rim majors Anonymous Pro 700
15sp · plate Anonymous Pro 700 10sp +2.0, nothing below · margin 20dp ·
pressed ink · zero numerals in chrome · night tiers #D2C6BB / #9C8E84 /
#776A60, glyph #EDE3D8, cream #FFE4CF **reserved — spent nowhere in this
file, not even at FLOW; FLOW is not a celebration.**

One confession before the beyonds: my wave-1 seat specced a split-flap on
this readout. The merged law (§0.3) killed it — a flap is a reel, a reel
lies. What survives of that instinct is the strike, specced properly in §1.
The flap does not come back.

---

## 1. THE TOTAL, SET LIKE TYPE ON A PRESS

### The beauty case, plain

A number on a press does not *arrive*. It is struck. The filed spec — hard
cut + 120ms ink-settle — has the right instinct and no anatomy: "settle"
could mean a fade, and a fade is a number dissolving, which is cinema, not
print. What actually happens when type bites paper: contact is instant,
ink is full at the edges first, density feathers in as the impression
takes, and a hair of ink squeezes sideways out of the stroke. That event —
not a fade, not a flap — is the most beautiful true thing a computed
number can do, and it costs 120ms that were already budgeted. Second: the
filed formats print `50 MIN` and `2H 30M` as one undifferentiated string
at 72sp, which sets the *units* as loud as the *quantity*. No printed
instrument does this. Digits are the fact; unit letters are its grammar,
and grammar is set smaller, on the same baseline, in the same ink. Third:
at FLOW the hero's home receives a word. A word is not a failed number —
set it as language, openly, and let the substitution be a typesetting
decision instead of a state flag.

### 1A — String classes: one line, three grammars

The total line is composed of spans. Digits never change size; unit
letters take H3 — a ladder size, at exactly the ladder's own 1.44 step —
on the shared baseline. Advances and kerning are measured on the whole
composed line once; nothing is ever measured per-span in isolation, so
the line's internal spacing is the font's, not arithmetic's.

| Class | Strings | Digits | Unit letters | Tracking |
|---|---|---|---|---|
| **N — minutes** | `5 MIN` … `55 MIN` | Sora 700, 72sp | `MIN` Sora 700, **50sp**, baseline-aligned | digits −2.0; units −1.5 |
| **H — hours** | `1H 00M`, `2H 30M` … `12H 00M` | Sora 700, 72sp; minute component zero-padded, always two digits | `H`, `M` Sora 700, **50sp**, baseline-aligned; the inter-component space takes the 50sp span's space width | digits −2.0; units −1.5 |
| **W — word** | `FLOW` | — (no digits exist) | the word itself, Sora 700, 72sp | **−0.5** (exception argued below) |

- **Why units at 50sp.** At 72sp, `MIN` in `50 MIN` shouts over the `50`;
  the eye reads a word with a number attached. At 50sp the hierarchy
  inverts to the true one: quantity first, grammar second, same ink, same
  baseline, one breath. This is how every good instrument dial and every
  good newspaper figure column sets units. It uses no new size: 50 is H3,
  and 72→50 is the ladder's own minimum ratio (1.44), the same step as
  104→72. *Flagged to Lind by mail before filing — this borrows H3's size
  for a non-number role inside H2's line, and Set's table should rule on
  it, not discover it.*
- **The W-class tracking exception.** Numerals at −2.0 are tight because
  digits are a column, not a sentence. `FLOW` at −2.0 in Sora 700 reads
  as a logo — compressed, branded, pretending to be a quantity's peer in
  the quantity's own compression. At **−0.5** the letters stand at
  reading distance from each other: unmistakably language that has taken
  the number's seat, not a number in costume. The home does not move —
  same baseline, same cap height, same line height 72, same centre. Only
  the tenant changes, and the tenant's clothes announce what it is.
  *Also flagged to Lind: this amends the hero row's single tracking value
  for one string class.*
- **FLOW's second line.** At FLOW there is no finish time, and `FINISHES`
  must not print — a label with no value is a lie of omission. Its slot
  (the readout's lower margin register, Anonymous Pro 700 15sp +0.5) is
  taken by `UNTIL YOU STOP`, same face, same size, same tracking, tier-2
  tone. The margin register always answers one question — *how does this
  end?* — with a time, or with the words that there is no end. One slot,
  one question, two honest answers.
- **Tones.** Hero line: night glyph **#EDE3D8** (Sana's D1 — the numeral
  steps up a tier; the word inherits the tier because it inherits the
  home), day ink #252323. Units take the *identical* tone as their digits
  — hierarchy is by size only, never by ink; a dimmer unit would read as
  a different fact. `UNTIL YOU STOP` / `FINISHES`: night tier-2 **#9C8E84**,
  day tier-2 token. Cream: nowhere.

### 1B — The strike: the ink-settle, anatomised

Every change of the total is a **hard cut plus one strike**. The strike
has two coupled components, both confined to glyphs whose character
actually changed; unchanged glyphs are not re-struck — a press only
prints what it prints.

1. **Density.** The changed glyphs render at **86% of their tone token**
   (an opacity shading operation on the named token — H-7 legal) and take
   ink to 100% over **120ms**, curve **`CubicBezierEasing(0.16, 1.0,
   0.3, 1.0)`** — an easeOutExpo character: ~60% of the density lands in
   the first ~25ms (the contact), the remaining ~40% feathers across the
   last ~95ms (ink wicking into the sheet). A linear ramp reads as a
   fade; an easeIn reads as hesitation, a press that doubts itself. The
   long tail is the whole point: the number is *instantly present and
   still becoming solid*, which is exactly what a struck impression does.
2. **Squeeze.** Simultaneously, each changed glyph is drawn once more at
   **(+0.4dp, 0)** — horizontal only — at **12% alpha**, decaying
   **linearly to 0 at 90ms**, i.e. absorbed before the density finishes.
   This is ink pushed sideways out of the stroke by the strike. At 72sp,
   0.4dp is sub-visible as displacement and fully visible as a momentary
   *fattening* — the glyph bites, then is only itself. The squeeze is
   horizontal because the strike is vertical: Sana's D3 deboss ghosts are
   the static vertical anatomy (y−1 / y+1) and are present from frame 0
   at their rest values; they never animate. Squeeze (moving, horizontal,
   90ms) and deboss (still, vertical, permanent) are two crafts and never
   share an axis, so they can never muddy into blur together.

**Per-event behaviour** (every row passes §0.3 — no intermediate values
are ever drawn, in any row):

| Event | Class | Behaviour |
|---|---|---|
| Wheel detent-crossing, wheel still moving | chosen, unseated | Hard cut, **no strike**. The press cannot strike moving paper; the number keeps up with the hand raw, exactly as filed. |
| Wheel seat frame | chosen, seated | Hard cut (if the seated value differs from the last crossing) **+ full strike** on changed glyphs. Rides Vega's seat knock — no added haptic. |
| Hub commit (count change) | computed | Hard cut **+ full strike**, one frame, plus the engine's single 10ms @ 0.5 knock. The total was arithmetic; arithmetic gets a strike, never a journey. |
| `FINISHES` at seat / hub commit | computed | Re-strikes **in the same frame** as the total, same 120ms density component at its own size, **no squeeze** (at 15sp a 0.4
...[truncated 10841 chars]dp squeeze is mush, not bite). While the wheel moves it holds its filed ~55% dim and does not recompute. |
| Entering / leaving FLOW | chosen (a detent) / computed (leaving restores a product) | Governed by §3 — a format change is a re-set, not a strike. |

**ARM / HOLD / TRUTH.** *ARM:* the strike fires at or after the seat/
commit frame — 0ms added to any path to the slide; worst case the total
is still feathering its last 40% of ink while his thumb is already
travelling, and it is fully legible from frame 0. *HOLD:* nothing here
runs while sealed; the running screen's 72sp is Sana's D3 domain and
inherits only the static deboss, never the strike. *TRUTH:* the cut is
instant and total — the new value is the only value ever drawn; the
strike animates *ink*, never *digits*. Density is not a value. A glyph
at 86% ink is the same number as at 100%; a reel at 37 was a different
number than 50. That distinction is the whole law, kept.

**What it replaces, and why that loses.** The filed "hard cut + 120ms
ink-settle" as an unexamined phrase — it survives in name and gains a
body. It also replaces my own wave-1 split-flap (already struck by the
merged law; recorded here so no builder resurrects it from my seat file)
and any uniform treatment of the three string classes — `50 MIN`, `2H
30M`, and `FLOW` are three grammars, and one-string-fits-all typesetting
is how the current app ended up shouting its units.

**Strongest case against.** The 50sp units demote the unit at exactly
the moment a barely-awake user most needs it: at 03:30, is it `50` hours
or minutes? The digits say *how much*, the small letters say *of what*,
and I have made the *of what* 30% smaller on hierarchy grounds imported
from print, where the reader is awake and seated. Worse, the squeeze is
one tuning failure away from reading as a rendering glitch — a 0.4dp
ghost is either a bite or a bug, and the difference is only visible on
the device (D3's removal trial should extend to it: if it reads as blur
at arm's length, the squeeze dies and the density ramp ships alone).
And the W-class tracking exception spends the ladder's cleanest rule —
one hero, one spec — on a word most sessions never show. If Set and Lind
hold the single-spec line, FLOW at −2.0 is still honest, merely
logo-tight; my exception is a refinement, not a rescue.

---

## 2. THE RIM AS A PRINTED INSTRUMENT

### The beauty case, plain

A printed instrument is beautiful because its vocabulary is *closed*.
A thermometer does not editorialize; a good rim says six numbers, marks
its stops, punctuates its one lie — and stops. The filed rim already has
the numerals right (majors only, 15sp, horizontal). What it lacks is a
decision about the break: Vega filed it as two short parallel hairlines,
and two hairlines *parallel to the ticks* are just two more ticks — the
rim's single most important mark is camouflaged as the thing it
interrupts. Punctuation must differ in kind from letters. Give the break
the one non-radial gesture on the rim, keep every word off the rim
entirely, and the rim becomes what it should be: the block's printed
scale, readable at a glance, silent about everything else.

### Spec

- **The rim's entire vocabulary:** six numerals — `30 60 90 120 150 180`
  — Anonymous Pro 700, 15sp, tracking 0, horizontal baselines always
  (never rotated around the arc; rotated type is a dial costume),
  printed at the 30-minute majors, night tone tier-1 **#D2C6BB**, day
  ink token. Ticks are geometry, per Vega's hierarchy (5-min short,
  15-min medium, 30-min long), tones tier-3 **#776A60** minors / tier-2
  **#9C8E84** majors at night. That is all the rim ever prints. No minor
  numerals, no unit letters, no `MIN`, no second register of any kind.
- **The axis-break — the rim's only punctuation.** Two slashes, each
  **1dp wide**, spanning the **full radial depth of the tick band**
  (inner tick edge to rim edge — longer than any tick, so length alone
  separates them from stops), separated by a **2.5dp gap**, the pair
  centred on the **bisector** between stop 0 and stop 1 (4.5° from
  each), and **tilted 35° off radial** — leaning against the direction
  of travel, the way a printed chart's break slashes lean against the
  axis they interrupt. Tone: tier-2 **#9C8E84** at night, printed,
  permanent, **never reacting** — not to touch, not to selection, not
  to the wheel passing beneath. Print doesn't react; that was my wave-1
  law for the numerals and it covers the punctuation too.
- **Stop 0 carries no word.** The rim does not print `FLOW`. Stop 0's
  mark is the **origin tick**: the long (30-min) tick length **+3dp**,
  full tier-1 ink — the scale's origin drawn as its heaviest stop, the
  way a printed scale weights its zero. The word FLOW has exactly one
  home in the app at arm time: the readout (§1A, class W). If the rim
  also printed it, the mode would have two homes three centimetres
  apart, and rim = block / readout = total collapses at the one stop
  where the division matters most — because at FLOW the block *is* the
  total, and only one surface may say so.
- **The FLOW strait — where rim printing stops.** West of the break
  (stop 0's sector): geometry only — the origin tick, the break, the
  band. No type of any kind, at any size, ever. East of the break:
  geometry until the first major at 30 speaks. The strait between them
  carries the slashes and nothing else. Language lives above the rim,
  in the readout; the rim's side of the strait is a printed scale that
  simply has not started yet — which is the truth: before 10, there are
  no minutes to print.

### ARM / HOLD / TRUTH

*ARM:* a legible break and an unmissable origin make the FLOW stop
findable in one glance instead of one discovery — the fling that
overshoots into FLOW is recognisably *somewhere else*, not at a small
number. *HOLD:* the rim at rest during a session is print; nothing in
this section animates, reacts, or invites. *TRUTH:* the break marks the
one non-linear jump instead of hiding it (Vega's intent, kept); the
tilt makes the mark *say* interruption rather than stop; the absent word
keeps one fact in one home.

**What it replaces, and why that loses.** Vega's filed axis-break (two
short parallel hairlines across the rim). If those hairlines run
radially — parallel to every tick — they read as a doubled stop and the
scale's one dishonesty-by-omission survives in miniature: a user can
count ticks straight through the break and never notice the axis jumped.
Shortness loses too: a break shorter than the band's depth reads as a
scratch. Full-depth, tilted, paired: unmistakably a *mark about the
scale*, not a mark *on* it. (Coordinated with Vega by mail this run —
if his hairlines were already specified non-radial, this section is a
sharpening, not a correction, and I'll say so in his thread.)

**Strongest case against.** Discovery. A first-time user at stop 0 sees
a heavy tick, two slashes, and a readout that says FLOW — and must
infer the rim's silence is a system, not an omission. A small `FLOW`
printed at stop 0 (plate register, 10sp) would teach the wheel in one
reading, and my one-home law is a law about *facts*, while a stop's
name on its own scale is arguably a label, not a restatement — labels
live on instruments everywhere (a thermometer prints CELSIUS at its
foot and nobody calls it duplication). If Marlowe's synthesis finds
first-run confusion is the Deck's real cost, the plate word at stop 0
is the concession I would sign — at 10sp, in the plate register, as a
label — and the origin tick would carry the argument alone from then on.

---

## 3. MY OWN BEYOND — THE RE-SET

*The idea I held back in Phase 1 as too much: format changes are
re-compositions, and the readout should typeset them like one.*

### The beauty case, plain

A compositor who changes one digit reaches into the line and swaps a
sort. A compositor who changes the *form* of the line — minutes becoming
hours, a number becoming a word — lifts the whole line out and sets it
again. The filed law treats both as the same event (hard cut + strike),
and that is a typesetting falsehood of manner, not of fact: `55 MIN` →
`1H 00M` is not an edit, it is a new line that happens to be worth one
minute more. Watch a man's thumb cross the 60-minute boundary on the
wheel: the digits he was reading cease to exist as a form. The screen
should admit that — briefly, quietly — instead of pretending the new
line was always sitting there.

### Spec

- **Trigger — class changes only:** N→H, H→N, anything→W, W→anything.
  Within-class changes (digit edits, hub commits that stay in class)
  never re-set; they strike per §1B, untouched.
- **Phase 1 — the lift (50ms).** The whole outgoing line — digits, units,
  all spans — dims to **55% of its tone token**, `easeOut`, no glyph
  changes, no movement. 55% is the app's established *not-currently-
  warranted* register: FINISHES already speaks it while the wheel moves.
  A dimmed line is not a claim; it is a claim being withdrawn.
- **The cut (0ms).** At 50ms the outgoing line is replaced whole by the
  incoming line. No overlap, no crossfade of glyphs — two lines never
  share the well.
- **Phase 2 — the strike (120ms).** The incoming line lands per §1B in
  full: density 86→100% on the same `CubicBezierEasing(0.16, 1.0, 0.3,
  1.0)`, squeeze included. Because the format is new, *every* glyph is a
  changed glyph — the whole line strikes, which is exactly the
  compositor's point.
- **Total event: 170ms**, occurring at seat/commit frames only — never
  mid-drag (crossings during a drag are raw cuts per §1B; if a drag
  crosses a class boundary, the re-set fires once, at the seat, for the
  seated class). `FINISHES`/`UNTIL YOU STOP` swap in the cut frame, in
  their own register, with no lift of their own.
- **Rarity is the licence.** Class changes happen at the 60-minute
  boundary and at the FLOW stop — a handful of times in a life of arming,
  always at a threshold the user deliberately crossed. Ceremony rationed
  to thresholds is punctuation; ceremony on every edit would be theatre.

### ARM / HOLD / TRUTH

*ARM:* 170ms at a seat frame, after the gesture that caused it — 0ms on
any path; the slide is untouched and unhurried by it. *HOLD:* seat-frame
only, pre-arm; while sealed the readout is the running screen's, which
never re-sets (a countdown crossing an hour boundary mid-session is a
live-class step, §0.3 — it steps its digits and keeps its form; the
re-set is an arming instrument and dies at the seal). *TRUTH:* no false
value is ever drawn solid. The outgoing line spends 50ms *dimmed* — the
grammar this app already uses for "not warranted" — and the incoming
line is true from its first frame, merely not yet fully inked. The one
honest discomfort: for 50ms the machine holds the new total and shows
the old one, dimmed. I judge the dim sufficient disclosure — it is the
same settlement FINISHES lives under for the entire duration of every
drag — and I flag it rather than hide it.

**What it replaces, and why that loses.** §1B's uniform strike for class
changes. Uniformity loses because it lies about manner: it presents a
re-composition as an edit, and over hundreds of armings it teaches the
eye that `55 MIN` and `1H 00M` are the same kind of object. They are
not; one is a count, the other is a structure. The re-set is the only
place in the app where the *form* of a number is allowed to be an event.

**Strongest case against.** It is 50ms of ceremony purchased with the
truth law's own coin. The FINISHES precedent is weaker than I make it:
FINISHES dims because its value is *stale*; at a seat frame the outgoing
total is not stale, it is *superseded in the same instant* — and a law
that says "the display shows what the machine holds" can reasonably
refuse to show anything else for even 50ms, dimmed or not. There is
also the glitch reading: a line that dips before it changes is, to an
eye that has never been told this grammar, a flicker — and flicker is
the one thing a Still Witness must never be accused of. If this ships,
it ships behind the same removal trial as D3 and §1B's squeeze: on
device, if the lift reads as instability at 03:30, it dies in one round
and class changes strike like everything else. I would rather lose it
to the trial than win it on paper.

---

## Mail / wall note

Mail sent this run: **Vega** (thread `axis-break-marking`) — the break
as tilted full-depth punctuation, no word at stop 0, origin tick +3dp;
his fight invited in the open. **Lind** (thread `hero-line-ratios`) —
pre-filing notice on the two ladder-adjacent proposals: unit letters at
H3 50sp inside the H2 line (ratio 1.44, the ladder's own step) and the
W-class tracking exception (−2.0 → −0.5 for `FLOW`). Inbox at start:
empty (two read letters from Phase 1 already filed). Wall: signed on
completion.

— Aldo, Wild · typography
