# STOFF — Materials seat, Designers cohort (coordinator: Sana)
## PLAN WAVE — Surface & Material spec: Shield slab + the app-wide material rulebook. No code.

Sarthak's order stands over this whole file: citations lifted, full freedom.
What binds is his settled law only — Concept NONE · ARM ≤10s · HOLD absolute ·
TRUTH once · beauty is the grading axis — plus the locked tokens and the
material law below. Everything else in this file is my call as the materials
seat, and I own it.

---

## 0. The material law, stated once

**Depth is exactly ONE tonal step + a hairline.** Never drop shadows, never
blur, never gloss gradients — except the one travelling specular rig, whose
parameters Uhr owns and whose *permitted places* I own (§5).

**Surfaces are flush inlays.** A control zone inside a slab is a finish
change on one continuous plane — matte recess, smooth cap, printed ink —
never a bordered box sitting on the slab. **Cards are dead in lists.**

The staircase (the only tones a surface may ever take):

| Step | Day | Night | Reads as |
|---|---|---|---|
| Recess (−1) | `#D8D1CB` | `#100E0C` | milled into the slab |
| Ground (0) | `#E4DFDA` | `#171412` | the slab itself |
| Raised (+1) | `#FAF6F0` | `#362C27` | a piece seated on the slab |
| Cap / sheet (+2, night only) | — (day cap = Raised) | `#4A423C` | hardware: caps, sheets |

Day has three surface tones, night has four — night's extra step exists
because a cap must stay findable on a near-black ground. No fifth tone may
be invented. Ink `#252323` (day) / cream family (night), block bar
`#2E2A26` (day) / `#161311` (night) and outline `#615B5A` (day) / `#807A7A`
(night) are **print and object tones, not surfaces** — they never fill a
zone larger than a cap, a bar, or a line.

**Hairline grammar (the whole of it — every hairline in the app obeys this):**

- **Recess** → one 1dp catch hairline on the **bottom inner edge**, in the
  Raised tone. That is the edge a milled pocket catches light on.
- **Raised** → one 1dp parting hairline on the **bottom outer edge**, in
  outline at 0x75 alpha (day `#75615B5A`). That is the seam where the piece
  seats — a parting line, never a shadow.
- **Flush neighbours** (finish change, no depth change) → **no hairline.**
  The finish is the boundary.
- **Row dividers** → outline at 0x75 alpha, 1dp, inset to the text column
  (starts at the text's left edge, ends at the screen margin), never
  full-bleed, never drawn through a sled track or a well.
- Exactly **one** hairline per depth change. A closed border loop around
  anything is a card trying to resurrect itself. Delete it.

**Finishes (how zones on the same plane are told apart):** ground and
recess are *matte* — flat fill, no sheen, ever. Raised pieces and caps are
*smooth* — flat fill by default; they are the only surfaces the specular
rig may touch, and only when they move (§5). Type is *printed* — ink-family
tones set into the surface they label, never on a chip or backing plate.

**Radius:** 8dp on keys, caps, wells, fields. 16dp on panels, sheets, the
nav capsule. True circles and slot ends (radius = half the slot height)
are the only exceptions. Nothing else has a radius; in particular no text
ever sits inside a pill.

---

## 1. Shield, slab construction — top to bottom

Shield is **one slab of Ground**, full screen, top to bottom. Nothing on it
is a card. Zones are either the slab itself, one recess milled into it, or
one raised piece seated on it. Scrolling scrolls the slab; recesses and
pieces scroll with it — they are in the material, not floating over it.

| # | Zone | Day | Night | Depth | Hairline |
|---|---|---|---|---|---|
| 1 | Slab / ground field | Ground `#E4DFDA` | Ground `#171412` | 0 | — |
| 2 | Loadout band (panel) | Recess `#D8D1CB` | Recess `#100E0C` | −1 | catch, bottom inner, Raised tone |
| 3 | Knob well (in the band) | Recess (band tone) | Recess (band tone) | −1, flush with band | catch arc, see §1.2 |
| 4 | Knob cap | Block bar `#2E2A26` | Cap `#4A423C` | object | seated line, bottom, outline-variant |
| 5 | Routine section strip | Ground | Ground | 0 | parting line above, outline-variant |
| 6 | Routine row field | Ground | Ground | 0 | divider below, inset |
| 7 | Routine time wells (in row) | Recess | Recess | −1 | catch, bottom inner, Raised tone |
| 8 | Apps section strip | Ground | Ground | 0 | parting line above, outline-variant |
| 9 | Search field | Recess | Recess | −1 | catch, bottom inner, Raised tone |
| 10 | Filter word row | Ground | Ground | 0 | none — underline bar on selected word only |
| 11 | App row field | Ground | Ground | 0 | divider below, inset, stops before sled |
| 12 | App icon well (in row) | Recess | Recess | −1 | catch, bottom inner, Raised tone |
| 13 | App sled track (in row) | Recess | Recess | −1 | catch, bottom inner, Raised tone |
| 14 | App sled cap | by state, §2 | by state, §2 | object | seated line, bottom, outline-variant |
| 15 | Block bar (session running) | Block bar `#2E2A26` | Block bar `#161311` | bar | parting line above, outline-variant |
| 16 | Nav capsule | Raised `#FAF6F0` | Raised `#362C27` | +1 | parting line, bottom outer, outline-variant |
| 17 | Nav selected pill (travels) | Outline `#615B5A` | Cap `#4A423C` | inlay in capsule | none — finish change only |

### 1.1 Loadout band — the one big recess

The band is Shield's only large milled feature: a full-width panel inset
16dp from each screen edge, radius 16, filled Recess, carrying the knob,
the stop scale, the loadout words and the counts line. One recess, one
catch hairline along its bottom inner edge — that single line is what
makes the whole top of Shield read as machined rather than drawn.

Everything inside the band is **printed on the recess** or **seated in
it**: stop ticks and the words STUDY / WORK / SLEEP are letterpress ink on
the recess tone; the counts line (`SHUT n · PEEK n`) is printed text, not
a chip; there is no raised card inside the band and no second recess
inside it except the knob well, which is flush with the band (same tone —
it is located by its catch arc, §1.2, not by a second step down; two
stacked steps are illegal).

### 1.2 The loadout knob — merged single knob, an object in a well

- **Well:** Ø96dp circle of band-recess, centred in the band. Its only
  mark is a **catch arc**: a 1dp arc in the Raised tone along the well's
  lower inner edge, spanning roughly the bottom 100° — the crescent where
  a circular pocket catches light. The rest of the well's edge has no line.
- **Cap:** Ø84dp true circle seated in the well (6dp of well shows around
  it — the clearance gutter, same idea as the sled skirt, §2). Day fill
  Block bar `#2E2A26`; night fill Cap `#4A423C` (night block bar on night
  recess would vanish; the cap tone is what the night staircase's fourth
  step is *for*). A 1dp seated hairline in outline-variant along the cap's
  bottom outer edge.
- **Marker line:** one line, **constant cream `#FFE4CF` in both modes**,
  2dp wide, drawn radially from the cap's centre to 70% of its radius,
  ends rounded. It never changes colour, never doubles, and is the only
  cream object on the day screen. Position is the state; the line is how
  the position is read at a glance.
- **Scale:** the three stops are printed ticks on the well/band at the
  detent angles — short 6dp ticks in outline tone, the selected stop's
  word in full ink beside the cap, unselected words in secondary ink.
  Words confirm; they do not carry the state alone, and no word gets a
  background.

### 1.3 Section strips

A section strip (ROUTINES, ALL APPS) is **not a bar**. It is a 16dp-tall
band of plain Ground with one parting hairline above it (outline-variant,
full content width) and the micro-label printed into the ground at its
left, counts printed at its right. No fill change, no raised strip, no
bottom hairline — the first row's own rhythm closes the group. If a strip
ever gets a background, it has become a card header; builders: refuse it.

### 1.4 Routine rows

- **Field:** Ground, 56dp module, flush with its neighbours (§3's module
  logic applies verbatim). Divider hairline below, inset to the text
  column.
- **Time wells:** start and end times sit in the row's only recesses —
  shallow wells, radius 8, Recess fill, catch hairline on the bottom
  inner edge. Times are Anonymous Pro printed *in* the well. A well is a
  key (radius 8) because it is pressable to adjust; a time that cannot be
  pressed is printed on the ground, not welled.
- **State is a word.** Routine on/off is printed `ON` / `OFF` at the row's
  right edge — no drawn sled (travel is not the function; settled law).
  The routine's loadout is a printed word that cycles on tap. Nothing in
  a routine row moves except ink weight: the ON word and the routine name
  take full ink; OFF rows print name and times in secondary ink. Tone of
  type, never tone of surface, carries routine state.
- **Current window:** the row whose window is open now gets a 3dp-wide
  Block bar tick in the left gutter, full row height (night: outline
  `#807A7A` — night block bar is indistinguishable from ground and a tick
  that cannot be seen is a lie). No fill change, no LIVE/RUNNING word:
  a window is not a session.

### 1.5 Search field and filter words

- **Search field:** one Recess well, full content width, 48dp tall,
  radius 8, catch hairline bottom inner. Placeholder and query are
  printed in the well in body type. No border, no raised surround, no
  search-icon medallion — a magnifier glyph in secondary ink inside the
  well's left inset is the whole ceremony. On focus the field does not
  change tone and does not rise; the cursor is the focus state.
- **Filter words:** a row of printed words on Ground (ALL / SHUT / PEEK /
  OPEN). Selected word: full ink + a 2dp underline bar in Ink, exactly the
  word's width, 4dp below its baseline. Unselected: secondary ink, no
  bar. No chips, no pills, no fills — the row must look like print that
  happens to be tappable, because it is.

### 1.6 The block bar and nav (Shield's foot)

- If a session is running, the block bar is a full-bleed strip in Block
  bar tone sitting directly above the nav capsule, parting hairline on
  its top edge (outline-variant against the ground above it). Its text
  is cream print on the bar. It is a bar, not a card: square corners
  where it meets the screen edges, no radius, no margin.
- **Nav capsule:** the screen's one Raised piece, radius 16, parting
  hairline along its bottom outer edge. Inside it, the selected
  destination is a **sled pill** (band nav travels, so a drawn sled is
  legal here): radius 16, inset 3dp inside the capsule, fill Outline by
  day / Cap by night, moving between destinations. It is a finish change
  inside the capsule — no hairline of its own, no specular (§5), its
  word/glyph prints in cream.

---

## 2. The sled as a material object

One construction for every sled in the app — app-row sleds, binary sled
switches, band nav (above), and (at hero scale, owned by Deck) the
slide-to-arm. If a builder dimensions a sled differently, it's a bug.

**Anatomy, outside in:**

1. **Track** — a slot milled into the slab. Recess fill, height **34dp**,
   corner radius **17dp** (half height — a slot's ends are semicircles,
   this is geometry, not a pill style). Catch hairline: 1dp, Raised tone,
   along the track's **bottom inner edge** only. Stop ticks printed on the
   track's inner top edge at each detent: 1dp × 6dp, outline tone.
2. **Skirt (clearance gutter)** — the cap is inset **2dp** from the track
   on all four sides, so a 2dp frame of bare recess shows around the cap
   at all times and travels with nothing — it is the visible clearance
   between a moving part and its slot. This gutter *is* the sled's depth
   cue together with the catch hairline. There is no shadow under a cap,
   ever; the gutter replaces it.
3. **Cap** — height **30dp**, width **36dp**, radius **8** (it is a key).
   1dp seated hairline, outline-variant, along its bottom edge. The cap
   never rises above the slab's plane in the material fiction: it fills
   its slot up to one step below the raised register, and its fill —
   not its height — codes its state.
4. **Cap fill = the fixed verb code** (printed once as a legend at the
   foot of Shield's app list, nowhere else):

   | Position | Day fill | Night fill |
   |---|---|---|
   | SHUT | Ink `#252323` | Block bar `#161311` + outline seated line at full alpha (silhouette rescue) |
   | PEEK / HALF | Outline `#615B5A` | Outline `#807A7A` |
   | OPEN | Raised `#FAF6F0` | Raised `#362C27` |

5. **Marker line** — one line on the cap's face, **perpendicular to
   travel** (vertical on Shield's horizontal sleds), **2dp × 16dp**,
   centred, ends rounded. Colour: cream `#FFE4CF` on SHUT and PEEK caps
   in both modes, and on the night OPEN cap; **Ink `#252323` on the day
   OPEN cap** — the single licensed exception, because cream on porcelain
   is not a line, it's a rumor. The line is the only high-contrast mark
   on the sled; value is read from its position, never from a tooltip.
6. **State word** — printed on the row beside the sled (SHUT / PEEK /
   OPEN, secondary ink, micro-label plate). Position says it, word
   confirms it, fill codes it — three renderings of the resting decision
   is deliberate redundancy for a control operated half-asleep; *during*
   a session the sled is inert (HOLD), the word remains as fact, and a
   press is answered with stillness, not a buzz.

**Dimensions, by sled:**

| Sled | Track | Cap | Travel | Stops |
|---|---|---|---|---|
| App row (3-stop) | 112 × 34dp | 36 × 30dp | 72dp | 0 / 36 / 72dp |
| Binary switch (2-stop) | 64 × 34dp | 36 × 30dp | 24dp | 0 / 24dp |
| Band nav pill | capsule inner width ÷ destinations | full-height inset 3dp | per destination | per destination |

Detents are mechanical: the cap seats at a stop or it is in the hand —
no resting position exists between stops, and the word flips on arrival,
never mid-flight.

**Proof vs intent (materials consequence):** the sled renders the *rested
decision*. Session enforcement is a different fact and gets a different
material: a row actually being enforced shows a 6dp Block bar square
printed in the row's left gutter (night: outline). The sled never changes
fill, tone or position to claim enforcement — one part, one truth.

---

## 3. The app-row module — a flush inlay, repeated

The app list is **one continuous Ground slab** with identical milled
features repeated down it. A row is not a container; it is a *register*
of the slab — 64dp tall, same construction every time, so the eye learns
the module once and afterwards reads only deviations.

**Across the row, left to right:**

1. **Gutter (16dp):** bare ground. Home of the enforcement square (§2)
   when present; otherwise empty. Nothing else may live here.
2. **Icon well:** 40 × 40dp, radius 8, Recess fill, catch hairline on its
   bottom inner edge. The app's real icon sits flush inside it at 28dp,
   clipped to radius 6, leaving a 6dp recess frame on all sides — the
   inlay frame. The well is matte; the icon keeps its own colours (icons
   are content, not surface, and are the only saturated thing in the list
   besides the one rationed orange fill elsewhere on the screen). No
   icon, no well: a missing icon prints the app's initial in the well in
   secondary ink — the well never disappears and breaks the register.
3. **Text column:** app name in body ink on the ground; beneath it the
   state word and any secondary fact in secondary ink. Text sits *on the
   slab*, never on the well's tone, never on a backing.
4. **Sled:** §2 construction, right-aligned, its track milled into the
   same slab. The row's divider hairline stops **8dp before the track** —
   a divider never strikes through a milled feature.

**How a row reads as one slab with its neighbours:** there is no gap, no
fill, and no radius between rows — only the divider hairline (inset from
the text column's left edge to the right margin, stopping before the
sled) and the 64dp rhythm. Three or more rows together must read as one
machined plate with repeated features, the way a cockpit panel does.
The moment a row acquires its own background — selected, blocked,
pressed-and-held — the plate shatters into cards. Therefore:

- **Blocked state changes nothing about the row's surface.** Only the
  sled's position/fill and the printed word change.
- **Press feedback is a dent, not a highlight:** while a finger holds the
  row (outside the sled), the row's field steps down to Recess for the
  duration of contact and returns on release. One step, no fade longer
  than the contact, no ripple. The slab dents under the thumb; that is
  the whole interaction material.
- **Search filtering removes rows; it never restyles them.** A filtered
  list is the same plate with fewer registers.

The routine rows (§1.4) are this same module at 56dp with time wells in
place of the icon well and a word in place of the sled — one module
family, two registers, no third row design anywhere in the app.

---

## 4. The material rulebook — 10 rules for any screen

A builder facing a screen I have never seen applies these, in order.
They are sufficient: every zone decision is one of these rules firing.

1. **ONE STEP ONLY.** Depth is exactly one tonal step from the
   neighbouring surface, plus one hairline. Never stack two steps
   (no recess inside a recess, no raised card on a raised sheet).
   Caps and knobs are objects seated in slots — their depth is read
   from their gutter and seated hairline, not from extra steps.
2. **NO SHADOW, NO BLUR, NO GLOSS.** Elevation 0 everywhere. The only
   light effect in the app is the one travelling specular rig, only
   where §5 permits it. If a surface looks lit and it isn't on that
   list, it's a defect.
3. **RECESS GOES DOWN, RAISED GOES UP.** Recess is always the darker
   neighbour, raised always the lighter — in both modes. At night, a
   surface lighter than Raised is a cap or a sheet, never a card, and
   there are at most four surface tones on any screen.
4. **ONE HAIRLINE PER DEPTH CHANGE, PLACED BY KIND.** Recess: catch
   line, bottom inner edge, Raised tone. Raised: parting line, bottom
   outer edge, outline at 0x75 alpha. Flush finish change: no line.
   Border loops are forbidden — a closed outline is a card.
5. **FINISH BEFORE OUTLINE.** Control zones are defined by finish
   change inside one continuous slab — matte recess, smooth cap,
   printed ink. If you reached for a border to separate two zones,
   change a finish instead and delete the border.
6. **CARDS ARE DEAD IN LISTS.** List rows live on the ground plane,
   separated by an inset hairline and rhythm only. A raised or filled
   row is a false claim of importance; state changes ink, words, and
   control positions — never the row's surface.
7. **SLEDS ONLY WHERE TRAVEL IS THE FUNCTION.** Slide-to-arm, app
   sleds, sled switches, band nav: drawn. Everything else: state is a
   **word** printed on the surface it describes. Never draw a switch
   for a fact a word can carry.
8. **CAPS ARE OBJECTS WITH ONE LINE.** Cap fill codes state by the
   fixed verb tones (Ink SHUT / Outline HALF / Porcelain OPEN); every
   cap carries exactly one marker line, cream on dark caps, ink on the
   day porcelain cap, perpendicular to travel; the 2dp recess gutter
   around the cap is its skirt — never a shadow.
9. **RADIUS IS 8 OR 16.** 8 for keys, caps, wells, fields; 16 for
   panels, sheets, the nav capsule. True circles and slot ends
   (half-height) are the only exceptions. No other radius exists, and
   no text ever wears a pill.
10. **PRINT, DON'T FLOAT.** Labels are letterpress: ink-tone type set
    into the surface they label, never on chips, badges or backing
    plates. Orange is a rationed fill for one commit per screen —
    never a surface tone, never a hairline, never type.

---

## 5. Where the specular rig is allowed to appear

Uhr owns the rig's parameters (source position, falloff, speed). I own
its territory. The rig is light on **moving hardware** — it exists so a
part's motion reads as a physical object passing under one fixed light.
It may appear on exactly these surfaces, and no others:

- **The loadout knob cap** (Shield) — while it turns, and at rest.
- **Sled caps** — app-row sleds, binary sled switches, the slide-to-arm
  cap — while they travel, and at rest.
- **The Deck wheel's crown and hub** (cross-screen inheritance; Deck's
  seat dimensions it, the territory grant is the same rule).
- **Key caps while pressed** — the rig may answer a press on a smooth
  cap; it may not idle on one.

Forbidden territory, by name: ground, recess fields and wells, tracks
(the slot stays matte — the cap shines *in* it, which is the contrast
that sells both), section strips, rows, text and printed labels of any
kind, sheets that are not caps, the nav pill (a travelling *fill* —
shine on it would render selection twice), the block bar, and any
surface at rest that a finger cannot move. Light never carries data:
if the rig's brightness would change with state, progress or time, it
is cut from that surface entirely.

— Stoff, Materials, Designers
