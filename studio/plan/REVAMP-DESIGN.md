# REVAMP-DESIGN — The Launcher Block Surface

**Design bundle, 5 seats. One file, five signatures, no dissent on the locks.**
**Ruling it serves (Sarthak, Oct 30, 04:29 IST):** *"the block screen should
be like a launcher or something which u cant exit and had a separate screen
for apps allowed and peek ones as usual beautiful ui. Current app looks
trash… The whole design needs to be revamped. Make the app crazy good.
I dont settle for mediocrity."*

**Built against (UMBAU, in code, in parallel):** a full-screen launcher
block surface on night tokens — one hero dial carrying session state, two
physical door plates (**ALLOWED APPS**, **PEEK APPS**) opening separate
app-grid screens. OPEN apps launch. PEEK apps raise their existing Held
Door (`APP_TIER` intercept → standard peek offer). Entry: tapping the
Guard line while sealed, and door plates on the Airlock block screen.
Lawful exits only: the existing Emergency Break (60-second hold valve)
and session end. **No new exits are designed here, and none may be added
in the name of this document.**

**The three surfaces, named once and used everywhere below:**

| # | Surface | Job |
|---|---|---|
| S1 | **Launcher** | The sealed room itself. Dial + two door plates + Guard line + Emergency Break valve. |
| S2 | **Allowed Apps** | The open-door grid. Every tile launches. |
| S3 | **Peek Apps** | The held-door grid. Every tile raises the Held Door peek offer. |

**Token lock — the only colours any seat below may use:**

| Token | Hex | Role in this revamp |
|---|---|---|
| Night ground | `#171412` | The room. Every surface's ground. Never a state colour. |
| Raised card | `#362C27` | Step 1 off the ground. Plates, tiles, the cooled dial. |
| Sheet | `#4A423C` | Step 2 / topmost hardware. Pressed fills, peek material, valve track. |
| Cream ink | `#FFE4CF` | All primary ink at night. Also the OPEN state colour (Seat lock, §A). |
| Accent (night) | `#F47442` | The ARMED state colour. Night only. |
| Accent (day) | `#F55314` | The same verb in daylight. The launcher is a night surface; this is listed only so the verb survives a theme change unchanged. |
| Alert | `#D63E06` | The HELD state colour. Nothing else may use it. |

Fonts: **Sora** (display), **Atkinson Hyperlegible** (body/interface),
**Anonymous Pro** (numbers, labels, telemetry). No new fonts. No green,
anywhere, ever. No AI-generated images — app icons are the apps' own;
everything else is type, ring, and material.

**How to read this bundle:** the five seats agree on the locks (colour
table, depth grammar, dial anatomy, type scale). Where a seat repeats a
lock, it repeats it *identically* — a lock restated differently in five
places is not a lock. Each seat's individuality is in what it stresses,
what it warns against, and what it names beautiful. Implementation
conflict rule: the hex, the state, the size, and the weight win over any
seat's prose.

---

## §A — DESIGNER

*Systems first. If it cannot be stated as a rule, it is not designed yet.*

### A1. Colour-as-verb table — canonical

Colour is not decoration on this launcher. Colour is the verb: it says
what the room is *doing* to you right now. Each state gets **exactly one
colour, forever**. A state may never borrow another state's colour, and
no colour may acquire a second state.

| State | Verb it performs | The one colour | Where it appears — and nowhere else |
|---|---|---|---|
| **ARMED** | *Sealing / burning* — a session is live and the room is shut | Accent `#F47442` (night) / `#F55314` (day) | Dial progress ring arc; the ARMED label on the dial; the hairline under the intention. Never on tiles, never on door plates, never as a button fill. |
| **OPEN** | *Permitting* — this app may launch, now | Cream ink `#FFE4CF` | App names and icons' ink on S2; the OPEN tag on an allowed tile; the ALLOWED APPS plate title. Cream is ink that has been given permission to be read at full strength. |
| **PEEK** | *Ajar* — you may look, briefly, on the Held Door's terms | Sheet `#4A423C` | Peek tile fill; the PEEK APPS plate fill; the peek tag's ground. A material colour, not a light: peek is a door opened onto a dim room, not a lamp switched on. |
| **HELD** | *Refusing* — this app is held; the door does not open | Alert `#D63E06` | Held Door offer's refusal line and hold-valve on S3's intercept; the HELD tag; a blocked attempt's flash state. Never used for errors, badges, or "attention" anywhere else in the revamp. |
| **ENDED** | *Cooling* — the session is over; the metal is going cold | Raised card `#362C27` | The dial after session end: ring, numeral surround, and plate fills all collapse to raised-card tone on the ground. Colour *withdraws* — that withdrawal is the signal. No accent survives the end. |

Ground `#171412` is deliberately **not** in the table. It is the room,
not a verb. Assigning it a state would make the room itself lie.

**Enforcement:** a screenshot test. Grep any future screenshot of S1–S3:
accent present ⇒ session ARMED; alert present ⇒ something is HELD;
neither present and dial in raised-card ⇒ ENDED. If a screenshot can
show accent on an ended session, the build is wrong, not the table.

### A2. Depth grammar — door plates and app tiles

Material: **Monolith metal and paper** — machined plate, engraved label,
pressed key. Explicitly **not** leather, not wood, no grain, no stitching,
no skeuomorphic texture of any organic material. Four states only
(Scout-1 find #15), identical grammar on plates (S1) and tiles (S2/S3):

| State | Meaning | Construction |
|---|---|---|
| **Raised** | Resting, tappable, proud of the ground | Fill one step up (`#362C27` on ground; `#4A423C` where a plate sits on a raised card). 1dp cream hairline on the **top edge only** — light catches the top of a machined plate, never all four sides. Soft drop shadow below, ground-dark, 8dp blur, 2dp drop. Corner radius 16dp plates / 12dp tiles. |
| **Pressed** | Finger is down; the key is travelling | Fill steps **up one tone** to sheet `#4A423C` (the plate compresses toward the hardware behind it), shadow collapses to 0dp, whole plate translates **2dp down**, top hairline disappears. Duration: exactly the touch. No ripple — ripples are paper soaking, this is metal travelling. |
| **Seated** | Resting in its grid slot; not the focus; the default tile state | Flush with its slot: fill = ground `#171412` on a raised grid card, or raised `#362C27` directly on ground — but **no shadow and no top hairline**. A seated tile is *docked*, drawn as an engraved outline (hairline, all four sides, sheet tone) with icon + name inside. It becomes Raised only on focus/hover of the finger approaching (touch-down proximity is Pressed's job; seated is pre-touch). |
| **Inset** | A well, not a key: things that receive, never things you press | Recessed **below** its surround: fill = ground `#171412` when the surround is raised `#362C27`; inner shadow on the top edge, hairline on the **bottom edge only** (light falls into a well and catches its far lip). Used for: the dial's numeral well, the Emergency Break valve track, empty grid slots, the peek-count well on S3. An inset element must never be tappable as a whole — tapping a well makes no physical sense and users learn it in one try. |

Plates vs tiles: same four states, different scale. A door plate is a
Raised object you strike once to change rooms; a tile is a Seated object
that raises under your finger. If a tile ever casts a plate's shadow at
rest, the hierarchy inverts and the grid shouts over the doors. It must not.

### A3. The dial object — exact anatomy

One object, centre of S1, the only hero. Four parts, four jobs, no part
does another part's job:

| Part | Job | Spec |
|---|---|---|
| **Progress ring** | *How much of the session has burned.* Elapsed proportion only — never a decoration, never a second data series. | Arc in the state colour (ARMED `#F47442`; ENDED collapses to `#362C27`). Track = sheet `#4A423C` at the ring's full circle, so the unburned remainder is always visible as hardware. Stroke 6dp. Starts at 12 o'clock, sweeps clockwise. No glow, no gradient. |
| **Remaining numeral** | *How long is left.* The single number the room exists to protect. | Anonymous Pro, set per §A4. Counts down in real seconds, rendered as `HH:MM:SS` over 1h, `MM:SS` under. It is a numeral, not a sentence — no "left", no "remaining" inside the well. Those words live in the label tier, once. |
| **Intention** | *Why this session exists.* The user's own words for this block, verbatim. | Set under the numeral, inside the dial's well. Wraps to at most 2 lines, then ellipsises — an intention that needs 3 lines is a paragraph, and the dial is not a page. Never rewritten, never "improved", never replaced by a motivational line. Empty intention ⇒ the line is absent entirely, not filled with placeholder text. |
| **End-time** | *When this ends, in wall-clock time.* The promise the room makes. | Anonymous Pro small tier (§A4), format `ENDS 19:42`, seated below the intention / at the dial's 6 o'clock rim. It is computed once from session end and does not tick — a ticking end-time is a bug wearing a costume. |

**TP-7 test — state readable 3 ways.** Cover any two, the third still
tells the truth:
1. **By colour** — ring + label in the state colour (§A1).
2. **By shape/motion** — ring sweep proportion + numeral counting down
   (ARMED) vs. ring full/still and numeral at `00:00` (ENDED) vs. ring
   replaced by the Held Door valve arc (HELD peek offer).
3. **By text** — the state label word itself (`ARMED` / `PEEK` / `HELD` /
   `ENDED` / `OPEN`) in the Anonymous Pro label tier, plus end-time.
A colour-blind user, a user across the room, and a screen reader must
all arrive at the same state. If any leg depends on colour alone, TP-7 fails.

### A4. Type scale — every text element, S1–S3

No new fonts. Weights restricted to weights that actually ship:
Sora 400/500/600/700, Atkinson 400/700, Anonymous Pro 400/700.

**S1 — Launcher**

| Element | Font | Weight | Size / line-height | Tracking / case |
|---|---|---|---|---|
| Remaining numeral (dial) | Anonymous Pro | 700 | 72sp / 72sp | −1.0sp, tabular |
| Intention (dial) | Atkinson Hyperlegible | 400 | 15sp / 22sp | 0, sentence case, user's words |
| End-time `ENDS 19:42` | Anonymous Pro | 400 | 15sp / 20sp | +0.5sp, caps for `ENDS` |
| State label `ARMED` | Anonymous Pro | 700 | 10sp / 12sp | +2.0sp, caps |
| Door plate title `ALLOWED APPS` / `PEEK APPS` | Sora | 700 | 22sp / 26sp | −0.3sp, caps |
| Door plate count `6 APPS` / `3 APPS · 2 PEEKS LEFT` | Anonymous Pro | 400 | 10sp / 12sp | +2.0sp, caps |
| Guard line | Atkinson Hyperlegible | 400 | 15sp / 22sp | 0, sentence case |
| Emergency Break label `EMERGENCY BREAK — HOLD` | Anonymous Pro | 700 | 10sp / 12sp | +2.0sp, caps, alert only while the hold is actively being performed |

**S2 — Allowed Apps grid**

| Element | Font | Weight | Size / line-height | Tracking / case |
|---|---|---|---|---|
| Screen title `ALLOWED APPS` | Sora | 700 | 22sp / 26sp | −0.3sp, caps |
| Session strip (remaining, mini) | Anonymous Pro | 700 | 15sp / 20sp | +0.5sp |
| App name (tile) | Atkinson Hyperlegible | 700 | 15sp / 20sp | 0, app's own name |
| Tile tag `OPEN` | Anonymous Pro | 700 | 10sp / 12sp | +2.0sp, caps, cream |
| Empty-state line (no allowed apps) | Atkinson Hyperlegible | 400 | 15sp / 22sp | 0, one line, states the fact and the lawful way to change it — nothing else |

**S3 — Peek Apps grid**

| Element | Font | Weight | Size / line-height | Tracking / case |
|---|---|---|---|---|
| Screen title `PEEK APPS` | Sora | 700 | 22sp / 26sp | −0.3sp, caps |
| Peeks-remaining readout | Anonymous Pro | 700 | 15sp / 20sp | +0.5sp, e.g. `2 PEEKS LEFT` |
| App name (tile) | Atkinson Hyperlegible | 700 | 15sp / 20sp | 0, app's own name |
| Tile tag `PEEK` | Anonymous Pro | 700 | 10sp / 12sp | +2.0sp, caps, on sheet fill |
| Held Door offer title (intercept) | Sora | 700 | 22sp / 26sp | −0.3sp |
| Held Door offer body | Atkinson Hyperlegible | 400 | 15sp / 22sp | 0, the standard peek offer's existing words — this revamp does not rewrite them |
| Held Door refusal / held line | Anonymous Pro | 700 | 10sp / 12sp | +2.0sp, caps, alert `#D63E06` |

Ratios hold the existing ladder discipline: no two adjacent tiers within
1.4×. The dial numeral (72) is the only display-scale element on any of
the three surfaces. If everything is big, nothing is the hero.

### A5. The one beautiful thing, per surface

- **S1 Launcher — the dial as a machined instrument.** One ring, one
  number, lit in one colour, in a dark room. *Trash version:* three
  widgets competing — a streak flame, a quote card, a mini graph — each
  in its own colour. The moment the dial has company, it has none.
- **S2 Allowed — the grid as a key rack.** Identical seated tiles, icons
  in a true grid, names aligned to a shared baseline, generous air.
  *Trash version:* tiles at mixed sizes, shadows at rest, "recently used"
  reordering mid-session — a rack that rearranges itself is a junk drawer.
- **S3 Peek — the dimness itself.** The whole surface sits one material
  step lower than S2; you feel you have opened a door onto a darker room.
  *Trash version:* peek tiles styled exactly like allowed tiles with a
  tiny badge difference — the user must *feel* the tier, not decode it.

— **Designer** ✓ signed

---

## §B — WOW-LEVEL DESIGNER

*System is table stakes. I am here for the three seconds after the
Guard line is tapped, when the room seals and the user either thinks
"this is serious" or thinks nothing at all.*

### B1. Colour-as-verb — I sign §A1 without changing a hex

Wow dies the moment colour gets cheap. My one addition is a **spending
rule**, not a new colour: on S1, accent `#F47442` may cover **no more
than the ring arc and one label word** — by area, under 4% of the screen.
That scarcity is the entire effect. A supernova in a teaspoon. The day
accent becomes a button fill, a plate background, or a "delightful"
gradient, ARMED stops meaning *sealed* and starts meaning *orange app*.

The ENDED collapse (§A1) is the most dramatic moment available to this
product and costs nothing: accent drains, dial falls to raised-card
`#362C27`, the room goes cold in one frame. Specify it as a single
400ms colour transition, ring and label together — never staggered,
never bouncy. Cold does not bounce.

PEEK in sheet `#4A423C` is the brave choice and I defend it against the
inevitable "make peek pop" note: peek *should not pop*. The wow on S3
is restraint — a whole screen that whispers. Pop is S2's cream, spent
on names that are actually allowed to open.

### B2. Depth grammar — where the theatre lives

I sign §A2's four states. The theatrical beat is the **Pressed travel**:
2dp down, shadow gone, top hairline extinguished, fill stepping to
sheet. On a door plate that reads, physically, as *a heavy plate
accepting your hand*. Specify the plate press at 120ms ease-out down,
180ms return — doors are slower to release than to accept.

One wow-grade detail, buildable inside the grammar: when a door plate
is pressed and the grid screen opens, the new screen's title plate
arrives **already Raised in the same screen position** as the plate that
was pressed (shared-element position, no crossfade soup). The door you
pushed is the room you entered. That single continuity is worth more
than any entrance animation package.

Tiles: the Seated→Raised lift under an approaching finger (§A2) is the
grid's only motion. Keep it to shadow + hairline only, 100ms. Tiles that
scale, wobble, or glow are a game menu. This is an instrument panel.

### B3. Dial anatomy — the hero shot

I sign §A3. Composition ruling from the wow seat: the dial occupies the
**upper two-thirds** of S1, optically centred (geometric centre +4dp up —
heavy numerals read low). The door plates sit below it as a matched
pair, same width, same height, separated by a 12dp channel of bare
ground. That channel matters: two plates fused into one card is a menu;
two plates with darkness between them is an airlock.

The ring is the only element allowed a **perfect** geometry — a true
circle, true 12 o'clock start. Everything else on S1 is rectilinear
plate. Circle-vs-plate is the surface's whole visual argument: *time is
round; doors are square; you live in both.*

TP-7 (§A3) from the wow seat: the third leg — text — must never be the
*prettiest* leg. If the ARMED word is more beautiful than the ring, the
design is showing off its typography at the state's expense.

### B4. Type scale — I sign §A4, with one flourish permitted

The only flourish I ask for, inside the scale: the remaining numeral's
**colon blinks are forbidden**, but the numeral may crossfade its final
digit change at the session's last 10 seconds — no. Withdrawn as I write
it. The numeral never performs. Its stillness *is* the performance.
§A4 stands exactly as written; the wow is that a 72sp Anonymous Pro
numeral, alone in a well, in cream, over near-black, needs nothing.

### B5. The one beautiful thing, per surface

- **S1 — the seal moment.** Guard line tapped → dial lights its ring in
  accent, plates raise, room is shut. One coordinated 300ms settle, then
  absolute stillness for the rest of the session. *Trash:* a looping
  pulse/glow on the ring "to feel alive" — a sealed room does not
  breathe at you; looping motion on a focus tool is a slot machine.
- **S2 — negative space.** The grid's beauty is how much ground shows
  between tiles. *Trash:* filling the air with section headers, search
  bars, and sort chips until the rack looks like a settings page.
- **S3 — the Held Door offer rising as a sheet plate** (`#4A423C`, §A2
  Raised at sheet step) over the dim grid — the one genuinely cinematic
  overlay in the revamp. *Trash:* a centred pop-up card with rounded
  "dialog" styling and two candy buttons; the Held Door is a door, it
  slides and seats, it does not pop.

— **Wow-level designer** ✓ signed

---

## §C — ENTHUSIASTIC DESIGNER

*I love this brief. A launcher you cannot leave, built like an
instrument. Let's make every touch feel GREAT — inside the locks,
which I sign in full: §A1 colour, §A2 depth, §A3 dial, §A4 type.*

### C1. Colour-as-verb — the joy is in the discipline!

One colour per verb is not a limitation, it is a *game the user learns
in one session and then reads fluently forever*. ARMED orange! CREAM
means go! SHEET means peek — dim, special, borrowed time! ALERT means
held, full stop! ENDED goes cold! I want the first-run experience to
teach the table without a tutorial: the first time a session arms, the
ring lights and the ARMED label lights **in the same frame** — colour
and word introduced as a pair, once, and the pairing is never broken
afterwards. Delight = comprehension speed. If a user can glance and
*know*, that is the most fun this app is allowed to have, and it's a lot.

### C2. Depth grammar — touch should feel like machinery that likes you

Signing §A2 and adding the feel spec: every Pressed (§A2) gets a single
short haptic tick on press-down and a softer one on the plate's return —
the click of a well-made switch, both ways. Tiles lifting Seated→Raised
get **no** haptic (a rack shouldn't buzz as you browse it); door plates
and the Emergency Break valve get them, because those are commitments.

Enthusiastic but firm: Pressed must trigger on **down**, not on release.
A plate that waits for finger-up feels broken no matter how pretty it
is. Latency budget: visual press state within one frame of touch-down.

### C3. Dial anatomy — my favourite object in the app

Signing §A3, and championing the **intention line** as the emotional
centre: the numeral tells you the time, the intention tells you *you
chose this*. It is set in Atkinson 400 15sp (§A4) — deliberately body
size, not display size — because it is the user's own voice, not the
app's headline. Keep it verbatim, keep it quiet, keep it always in the
same place so returning eyes land on it before the numeral. That
ordering — *why, then how long* — is the kindest thing this surface does.

TP-7 enthusiasm note: three readable ways means three *audiences* —
glancing user (colour), far user (ring + numeral), careful user (text).
All three deserve the design effort, not just the glancer.

### C4. Type scale — signing §A4, defending the small tiers

The 10sp Anonymous Pro caps labels (+2.0 tracking) are the app's
"engraving" voice and I will defend them against every "too small"
note: they are labels on hardware, read at arm's length, always
redundant with colour or position (TP-7), never the sole carrier of
anything urgent. The urgent thing — the numeral — is 72sp. The scale's
*range* is its kindness.

### C5. The one beautiful thing, per surface

- **S1 — the intention, sitting inside the dial,** in the user's own
  words, under the number. *Trash:* replacing it with rotating tips,
  quotes, or streak encouragement. Nobody's intention is "You've got this!"
- **S2 — tapping a tile and the app simply opens.** Instantly. No splash,
  no confirmation, no animation tax on a permitted act. *Trash:* any
  interstitial whatsoever between an OPEN tile and its app — permission
  that makes you wait is a peek wearing cream.
- **S3 — the peek offer's honesty.** The Held Door says what a peek
  costs and how many are left, in the §A4 tiers, before you commit.
  *Trash:* a peek button that spends the peek before showing the terms,
  or peeks-remaining hidden behind a tap. Surprise spending is a dark
  pattern and it would poison the prettiest grid in the world.

— **Enthusiastic designer** ✓ signed

---

## §D — DESIGNER, 29 YEARS OF EXPERIENCE

*I have shipped blockers, launchers, kiosk modes, and focus tools since
before app stores. Every failure mode below is one I have personally
shipped, paid for, and sworn off. I sign §A1–§A4; my section is the
fine print that keeps them true in year two, not just on launch day.*

### D1. Colour-as-verb — the table will be attacked; armour it

§A1 is correct, and it will be attacked by every future feature: a new
badge will want accent, a warning will want alert, a celebration will
want *something*. Standing rulings from this seat:

1. **Alert `#D63E06` is spent on HELD and on the live act of performing
   the Emergency Break hold — and the second of those is not a new
   state, it is HELD's own valve.** Any other use (sync errors, low
   battery, update nags) is forbidden on S1–S3. A block surface that
   cries alert for a software update teaches the user to ignore held.
2. **Accent creep test:** any PR that adds accent outside the ring +
   ARMED label must name which existing accent it removes. Accent area
   is a fixed budget (§B1, <4% of S1), not a growing one.
3. Day accent `#F55314` is the same verb, not a new colour to design
   with. If the launcher is ever shown in day theme, the table ports
   hex-for-role, unchanged.

Contrast, checked, because I always check: cream `#FFE4CF` on ground
`#171412` ≈ 13.5:1 — passes everything. Accent `#F47442` on ground
≈ 5.9:1 — passes for the ring and large label use it is locked to;
it must never carry 10sp body-critical text alone, which is why §A4
sets the state label at 700 with the ring as its redundant leg (TP-7).
Alert `#D63E06` on ground ≈ 3.9:1 — acceptable **only** because HELD is
always triple-coded (word + valve/door position + colour). Do not ever
set a paragraph in it.

### D2. Depth grammar — gravity is a contract

§A2's four states survive contact with reality only if they are
**monotonic**: Raised > Seated > Inset in apparent height, always, on
every surface, with Pressed as the only temporary inversion. The classic
failure I have watched three teams ship: a "selected" tile getting a
shadow *and* a badge *and* a colour, until selected out-ranks the door
plates. Selection on S2/S3 is shown by the Raised state alone. One
signal per meaning — that sentence has saved more products than any
aesthetic I know.

Material honesty: metal/paper means **shadows are earned by height and
nothing casts a shadow while flush**. No ambient "card glow", no
coloured shadows (a orange-tinted shadow under the dial is accent
spending by the back door — see §D1.2, it counts against the budget).

Also: plates and tiles must keep their grammar at **200% font scale and
in landscape**. The plate title at Sora 700 22sp caps will wrap before
it overflows; if it cannot, the plate grows taller — text never shrinks
below the §A4 floor to fit a plate. Accessibility outranks geometry.

### D3. Dial anatomy — the end-time is where products lie

§A3 is right, and the end-time is its most dangerous part. Twenty-nine
years of timers says: **the end-time and the numeral must derive from
the same single session-end timestamp**, rendered independently. The
classic bug — numeral counts from a duration, end-time computed from a
clock that later shifts (timezone travel, NTP correction) — produces a
dial that disagrees with itself, and a focus tool caught disagreeing
once is never trusted again. TP-7's real fourth leg, unspoken: *the
parts never contradict each other.*

Ring granularity: the arc sweeps in steps no coarser than 1 second's
worth of arc; a ring that visibly jumps every minute reads as broken,
and a ring smoothed into fake continuous motion across a suspended
app reads as lying. Recompute from the timestamp on every frame —
never integrate elapsed time locally.

ENDED anatomy, specified because launch teams always forget it: at
session end the numeral reads `00:00`, the ring is **full** (the whole
session burned — full, not empty; the work was done, the circle closed),
colour collapsed to `#362C27` per §A1, end-time line replaced by the
single label `ENDED`. Nothing flashes. Nothing celebrates. The drawer
of finished sessions is where history lives; S1 just goes quiet.

### D4. Type scale — floors are floors

§A4's tiers are minimums, not targets. Three standing rules:
(i) system font scaling applies to Atkinson tiers fully, to Anonymous
Pro tiers up to the point the dial numeral would break its well — at
which point the well grows, the numeral does not shrink below 50sp;
(ii) app names on tiles are the apps' own names, untruncated to one
line with ellipsis, never renamed by us; (iii) no text element may be
added to S1–S3 without a row in §A4 — an unlisted element is an
undesigned element, and undesigned elements are how launchers rot.

### D5. The one beautiful thing, per surface

- **S1 — that it looks identical on day 1 and day 100.** Same dial, same
  plates, same stillness. Beauty here is *bore-proof trustworthiness*.
  *Trash:* seasonal themes, holiday skins, "fresh look" refreshes of a
  sealed room. You do not redecorate a vault.
- **S2 — every allowed app reachable in one tap, always in the same
  slot.** Muscle memory is the beauty. *Trash:* frequency-based
  reordering. A launcher that moves your apps is gaslighting your hands.
- **S3 — the peeks-remaining readout that never flatters.** `2 PEEKS
  LEFT` means two. *Trash:* hiding the count to "reduce anxiety", or
  rounding a spent peek as anything but spent. This app's whole brand
  is TELL-THE-TRUTH; the peek counter is where it proves it daily.

— **Designer, 29 years of experience** ✓ signed

---

## §E — INTENTIONALITY-FOCUSED DESIGNER

*My question for every element is the dial's own question: why are you
here? An element that cannot answer in the user's own terms is removed,
however beautiful. I sign §A1–§A4 — they are, read closely, an
intentionality document already: one colour per verb, one job per dial
part, one hero per surface.*

### E1. Colour-as-verb — colour as a kept promise

Read §A1 as five promises. ARMED in accent promises *the room will not
open early*. OPEN in cream promises *this tap will simply work*. PEEK
in sheet promises *this is borrowed, dimmer, and counted*. HELD in
alert promises *no means no, and the app will not negotiate*. ENDED in
raised-card promises *it is over, and nothing here will try to keep
you*. A sixth colour, or a second meaning for any of the five, is not a
design change — it is a promise renegotiated without the user's consent.
That is why the table says **forever**: the user's fluency in this
table is an asset they built, session by session, and design does not
get to spend it.

### E2. Depth grammar — depth as honesty about consequence

§A2's grammar is moral before it is visual. Raised things are *real
commitments your hand can make*; Inset things are *facts you can only
look at* (time left, peeks left, the valve track). The Emergency Break
is the test case: its track is Inset — a well, a fact — and only the
60-second hold itself raises the valve. A break-glass control styled as
a cheerful Raised button would invite casual use; styled as pure Inset
text it would hide in an emergency. The grammar threads it exactly:
findable, physical, uninviting. Depth, used this way, is the interface
telling the truth about weight.

The door plates earn Raised for the same reason: crossing from S1 to S2
or S3 is a real change of room and permission. If plates were flat rows,
the design would be claiming that opening the peek room is the same
weight as reading the Guard line. It is not, and the hand should know
before the eye finishes reading.

### E3. Dial anatomy — the intention is the load-bearing part

Strip the dial to parts (§A3) and ask which one the session cannot
survive losing. Not the ring — a numeral alone still times. Not the
end-time — arithmetic recovers it. **The intention.** A block without
its stated intention is just a locked phone and an annoyed person; a
block *with* it, centred in the one hero object, is a person keeping an
appointment with themselves. That is why §A3/C3 set it verbatim, quiet,
and unskippable-by-substitution: the app may never speak in that slot.
The moment the dial shows a tip, a quote, or a generated encouragement
where the user's intention belongs, the surface has replaced the user's
purpose with the product's voice — the single most trash move available
to a focus tool, and it is forbidden here by name.

TP-7, restated in my terms: state readable 3 ways means the room's
meaning survives the user being tired, distracted, colour-blind, or
furious. Intentionality that only works for a calm, fully-sighted user
at noon is decoration.

### E4. Type scale — size is a claim about importance

§A4's scale is an argument: the numeral at 72sp claims *time is the
fact*; the intention at 15sp body claims *your words don't need to
shout to run this room*; the 10sp engraved labels claim *the machine's
annotations are the least important voice here*. I sign it, and I add
the deletion test for any proposed element: if it needs to be bigger
than the intention to be noticed, it is competing with the user's
purpose, and it goes — or the intention's slot is defended and the
element goes anyway.

### E5. The one beautiful thing, per surface

- **S1 — the dial holds the user's own sentence at the centre of a room
  they cannot leave.** Beauty = being surrounded by your own stated
  reason. *Trash:* anything in that centre that the user did not write.
- **S2 — permission without friction or apology.** The allowed grid
  never explains itself, never warns, never upsells attention back.
  *Trash:* a banner, a tip, a "did you know" — anything that makes a
  permitted room feel conditional.
- **S3 — refusal without punishment.** The peek room and its Held Door
  say no (or "briefly, this much") plainly, count honestly, and leave
  the user's dignity and session intact — no shame copy, no red
  scolding beyond the one alert verb doing its one job. *Trash:* guilt
  language, "Are you sure you want to waste…", or a held app shaking
  its head at you. A door that moralises will be ripped off its hinges;
  a door that is simply, beautifully shut will be respected for years.

— **Intentionality-focused designer** ✓ signed

---

## Bundle locks — summary for UMBAU (implementation checklist)

1. Colour table §A1 is canonical: ARMED `#F47442` · OPEN `#FFE4CF` ·
   PEEK `#4A423C` · HELD `#D63E06` · ENDED `#362C27`; ground `#171412`
   is never a state. Day accent `#F55314` = ARMED's day hex only.
2. Depth §A2: Raised / Pressed / Seated / Inset, metal-and-paper only,
   top-edge hairline on Raised, bottom-edge hairline on Inset, shadow
   only when Raised, Pressed = sheet fill + 2dp travel + shadow gone.
3. Dial §A3 + §D3: ring = elapsed proportion from the single session-end
   timestamp; numeral = remaining; intention = user's words verbatim,
   ≤2 lines, slot never substituted; end-time = static wall-clock from
   the same timestamp. TP-7: colour + shape/motion + text, always all 3.
4. Type §A4 is exhaustive for S1–S3: Sora / Atkinson / Anonymous Pro
   only, shipped weights only, 72sp numeral is the sole display tier.
5. Exits: none designed, none added. Emergency Break (60s hold) and
   session end are the only ways out, exactly as they already exist.

*Filed by the 5-seat design bundle — Designer · Wow-level designer ·
Enthusiastic designer · Designer with 29 years of experience ·
Intentionality-focused designer — for UMBAU's launcher build,
branch `agent/design-revamp`.*
