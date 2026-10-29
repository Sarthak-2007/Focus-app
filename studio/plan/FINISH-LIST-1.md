# FINISH LIST 1 — the Finishers' first pass over the five screens

*Filed by Aurel, coordinator of the Finishers, Oct 29 2026.
Evidence: the current renders — shots/01_deck.png … 05_guard.png,
shots/night/n01–n05, the wheel crop (shots/b2-wheel-crop.jpg) —
judged against the settled books (REGISTER.md's staircases, accent
budget and specular rig; MASTER-PLAN §0). Every finding names the
screen, the exact surface, and the finish in builder numbers.
Ordered by what each fault costs the beauty of the whole app.
This list feeds the chrome build directly.*

*The Finishers grade finishes, not functions. Where a surface is
scheduled to move (Guard → PARK), the finish is written for the
surface, and travels with it.*

---

## F-01 — ALL SCREENS (worst: LEDGER) — the accent is spent everywhere, so it means nothing
**Seat: Tonwert (colour value), with Licht.**
The register settles an accent budget (Deck: 1 surface). The
renders break it on every screen. Day Deck: accent on the HELD 02
numeral, the SYSTEM 7 badge, the HORIZON 3 badge, *and* the slide
cap — four hits where one is law. Ledger is the worst room in the
app: the 150 numeral in full accent, the FOCUSED MIN dot-digits in
accent, "16 BRICKS" in accent, "07/07" in accent, THU's 25m in
accent, THU's fader filled accent, "+25m" in accent — seven hits.
Tasks: START 25m. Guard: the "03" in 03 SWITCHES. Orange is the
app's one precious material; printed on counts and history, it
reads as noise, and the one surface that must burn — the commit —
no longer does.
**Finish:** accent lives on exactly ONE surface per screen:
Deck = the slide cap. Tasks = START of the active task (F-15).
Ledger = the tower's current brick, nothing else — history is not
an action and never wears the action colour. Guard/PARK = the
armed dot inside the switches, nothing else. Everything else
named above drops to glyph colour (night `#EDE3D8` / day ink
`#252323`): band count badges, the HELD numeral, all Ledger
numerals and dot-digits, "07/07", THU's fader fill (→ F-03's night
fill / day ink fill), "+25m", "03 SWITCHES". One line in the theme:
no composable outside the named commit surfaces may take the
accent token.

## F-02 — DECK — the wheel pointer is a lollipop
**Seat: Rand (edges), with Glanz.**
The pointer at the wheel's rim — a black disc with a white centre
dot and a thick tail arc — reads as a map pin, a camera lens, a
lollipop. It is the object his finger lives on, the centre of the
centre screen, and it is the cheapest-looking thing in the app.
The white dot is the crime: a highlight with no light source,
printed dead centre.
**Finish:** the pointer becomes a chamfered vane. (1) The centre
dot is deleted; in its place a 6dp dimple in the ground colour at
40% alpha — a recess, not a lens. (2) Tail arc width 7dp → 4dp,
colour text-2 (day `#5F5856` / night `#9C8E84`). (3) The vane's
leading edge carries the settled marker line: 1dp, day `#FAF6F0`
@ 90% / night cream `#FFE4CF` @ 90% — the same line the caps
already wear, so the hand's object joins the app's one light
language. Canvas path change in the wheel view only; no physics,
no geometry of travel touched.

## F-03 — LEDGER, night — filled surfaces glow at full cream and shout on the nightstand
**Seat: Nacht (night face), with Licht.**
At night the tower's bricks and the faders' fills render cream
`#FFE4CF` at full value: a wall of lit windows. The WED fader — a
full cream column — is the brightest object in the entire app at
night, brighter than any numeral on any screen. A screen he
glances at from bed must be the quietest, not the loudest.
**Finish:** night fills are capped. Filled bricks and fader fills
take a night fill = cream blended 38% toward night ground
`#171412` → `#A79587`. The level still reads instantly because
each fill keeps its top-edge hairline at full cream @ 90% (the
settled marker line). The tower's CURRENT brick is exempt — it
keeps accent `#F47442` (it is Ledger's single accent, F-01). Day
fills unchanged.

## F-04 — ALL SCREENS — the floating band guillotines the last row of every scroll surface
**Seat: Fuge (seams).**
Content runs under the bottom band and is sliced mid-object:
Tasks queue #03's action row is cut through the text; Guard's
SOUND EFFECTS switch is half-swallowed; the Ledger's receipt card
is cropped at its seam. The join between a screen and the chrome
is a hard crop — the surface pretends the band isn't there, and
the eye catches the lie at every scroll's end.
**Finish:** every scroll surface takes bottom content padding =
band height 76dp + 24dp of air = 100dp, both modes. Above the
band, a 24dp ground-colour fade (transparent → ground, linear)
is anchored so the last rows dissolve into the seam instead of
being cut by it. Two numbers, five screens, one seam language.

## F-05 — SHIELD + TASKS — "selected" speaks three different languages
**Seat: Rand, with Fuge.**
Shield's loadout selection is a black cap with a cream marker.
Tasks' horizon strip shows three segments in three fills — black,
mid-grey, and (day) CREAM: the unselected 45m segment is *brighter*
than the selected one, and at night the strip inverts to a cream
first segment, a fourth language. Tasks' duration row uses a
black pill again. A user should learn selection once, in one
screen, and know it everywhere.
**Finish:** one selection language, app-wide: selected = ink fill
(day `#252323` / night cap `#4A423C`) + glyph text + the 1dp
marker line on the cap's leading edge (cream @ 90% night /
`#FAF6F0` @ 90% day); unselected = no fill, 1dp outline in text-2
@ 40%, text-2 text. No unselected element may ever be lighter
than the selected one, in either mode. The horizon strip's cream
segment dies in both modes; all three segments share one
construction.

## F-06 — DECK — the card's middle is a desert
**Seat: Mass (proportion & spacing).**
The Deck card — the most-seen surface in his life with this app —
is top-heavy and bottom-heavy with a void between: from "50 MIN
TOTAL" to the wheel's top edge, roughly 90dp of empty card. The
hero speaks, then the room goes silent, then the wheel. The
rhythm is a held breath, not a rest.
**Finish:** redistribute the card's air; the wheel does not move
and does not grow. Hero block bottom margin 32dp → 16dp;
"50 MIN TOTAL" sits 10dp under the hero (from ~18dp); the
FLOW key + FINISHES row rise 20dp toward the wheel (bottom
internal padding 24dp → 16dp, wheel-to-row gap 28dp). Net law:
no internal vertical gap inside the Deck card exceeds 40dp. The
builder measures against the render; the void band is the spec.

## F-07 — SOUND, all screens — a finished subtask out-shouts the seal
**Seat: Echo (sound finish). Evidence: TactileSoundEngine.kt
constants, read directly.**
Gain hierarchy today: completion pulse 0.68 · harmonic seal 0.62
· mechanical latch 0.54 · firm click 0.50 (12ms) · detent 0.38
(6ms). The gravest act in the app — the seal, the centre
gesture's consummation — is quieter and shorter (48ms) than
ticking off a subtask. And the latch (16ms) and the firm click
(12ms) are near-identical bursts wearing two names: two sounds,
one sound.
**Finish:** re-voice the hierarchy in the engine's own constants.
Seal: gain 0.62 → 0.72, duration 48ms → 96ms, and a low body
partial added at −12 semitones, gain 0.25 — a stamp landing, not
a click. Completion pulse: gain 0.68 → 0.55, duration 64ms →
52ms. Latch: a second strike at +26ms, gain 0.30 (ka-chunk — the
two-stage sound its own haptic waveform already tells: 215/0/150)
so latch and click are cousins, not twins. Detent: 6ms → 8ms,
tail partial amplitude 70 → 90, so a turn of the wheel has a body
and not only a tick. Nothing else in the engine changes.

## F-08 — LEDGER — the telemetry cards say every number twice, in two voices
**Seat: Stoff (material), with Glatt.**
Each telemetry card prints its value twice: once as a dot-matrix
figure, once as a clean numeral beside it. The dots were meant
to make the screen a counter machine; doubled with a plain
numeral they read as dice, as a QR fragment — a toy, not an
instrument. Two voices in one card is no voice.
**Finish:** one numeral voice per card: the dot-grid is the SOLE
rendering of the value (dot pitch 4dp as settled, dots in glyph
colour, accent nowhere — F-01), and the clean duplicate numeral
is deleted. Card label moves to the card's bottom-left edge,
baseline aligned across all four cards. Fallback, specced now so
nobody improvises: values over 3 digits shrink pitch to 3dp,
never truncate. The tower below gains the invention in F-09 and
the screen becomes one instrument with one accent.

## F-09 — LEDGER — INVENTION: the tower's bricks have no light on their top faces
**Seat: Luce (light), with Stoff. Priced invention — the current
vocabulary has no finish for the tower as a built object.**
Sixteen black/cream slabs stacked in rows read as a spreadsheet
grid, not a tower, because stacked physical things catch light
on their top faces and these catch none.
**Finish (new, priced):** every filled brick gains a 1dp top-edge
light line — day `#FAF6F0` @ 70% / night cream @ 50% (night: the
line, not the fill, carries the brightness, serving F-03) —
drawn inside the brick's top edge, corner-to-corner minus 2dp
each side. Empty bricks get no line. Cost: one line per brick in
the tower's existing Canvas/composable, ~15 lines, no data, no
geometry change. If Ren strikes it, the tower still gets F-03
and F-08; this is the difference between a grid and a building.

## F-10 — GUARD → PARK — eight REMOVE billboards are the screen's heaviest rhythm
**Seat: Mass, with Rand.**
The blocked-websites list is a column of eight identical pale
slabs (each ~88×48dp) all shouting REMOVE. A quiet, reversible
housekeeping act is dressed as eight billboards, and they — not
the sites, not the switches — are what the screen looks like.
**Finish:** REMOVE loses its body: text-only, Anonymous Pro 700,
text-2 colour, right-aligned, 44dp hit target, no fill, no
outline. Rows take a hairline separator (text-2 @ 18%) so the
list reads as a list. The freed weight goes to the site names:
15sp → 16sp, glyph colour. ADD remains the one filled button on
the screen — one slab, earned. The finish travels to PARK
unchanged when the surface moves.

## F-11 — DECK, chrome — the DAY/NIGHT pill steals the first glance
**Seat: Aug (first-glance read), with Licht.**
At 300ms the eye must land: hero → wheel → slide. Today on the
day Deck the second-darkest object on screen is the black DAY
pill (top-right); at night the cream-filled NIGHT pill is a
luminance rival to the hero numerals themselves. A mode toggle
is plumbing; it is dressed as a headline object and it taxes
every glance, every screen, forever.
**Finish:** the mode pill loses its fill in both modes: text-only
("DAY" / "NIGHT", text-2, Anonymous Pro 700, 44dp hit target),
no capsule, no outline. The brightest filled objects on any
screen are then content again. (Chrome-wide; lands with the
chrome build.)

## F-12 — DECK, night — the wheel's tick ring nearly vanishes at the hour it's read most
**Seat: Luce, with Nacht.**
Night ticks sit in muted `#776A60` on recess `#100E0C` — roughly
2:1 contrast. The instrument he reads at 3:30 AM, half asleep, is
the one rendered faintest; the ring dissolves and only the
numerals float.
**Finish:** night tick alphas: minor ticks text-2 `#9C8E84` @
55%, major ticks glyph `#EDE3D8` @ 80%, drawn over the existing
recess. Day ticks unchanged. One derivation in the wheel view's
night branch.

## F-13 — DECK, night — the card's boundary dissolves on the nightstand
**Seat: Nacht, with Rand.**
In n01 the Deck card separates from the ground by its hairline
alone; the fills read as one continuous dark, and at arm's
length — bedside, bleary — the card is not a place. The settled
night staircase already owns the cure (ground `#171412` → raised
`#362C27` is a real step); the render doesn't show it.
**Finish:** verify the Deck card's night fill token against
`#362C27`; if the token is right, the harness night render
predates the staircase and a fresh night render of all five
screens is a required proof in the chrome build's set — the
Finishers will re-judge from it. Either way this closes with a
number, not an opinion. Companion debt, recorded: **no render of
the AOD desk-clock overlay exists in the shots set at all** — the
one surface that is literally a nightstand object has never been
finish-judged. Nacht claims it for Pass 2 the day a render
exists.

## F-14 — SHIELD — routine ON pills wear the same black slab as commit actions
**Seat: Fuge, with Tonwert.**
One black pill currently means four things: ON (a state),
SHUT (a chosen protection), ADD (a commit), and the band's
selected stop. When every verb wears the same body, none of them
has weight.
**Finish:** black/ink fill is reserved for commitment. Routine
ON becomes an outline pill: 1dp outline ink @ 60%, ink text, no
fill — a state reporting itself, not an action begging a tap.
SHUT keeps its fill (inside its sled it IS the commitment);
ADD keeps its fill; the band is chrome and keeps its own law.
The loadout cap keeps F-05's language.

## F-15 — TASKS — DONE and START shout at the same volume; DEL whispers by accident, not design
**Seat: Aug, with Tonwert.**
On the active task's action row: DONE (ink), START 25m (accent),
DEL (muted) — the accent is right (F-01 keeps it) but thin:
15sp accent text at regular weight reads as a coloured word, not
the money verb it is; and DONE/DEL's step-down is left to chance.
**Finish:** a three-step voice ladder, set once: START —
Anonymous Pro 700, accent, letter-spacing 0.5; DONE — Anonymous
Pro 700, ink/glyph; DEL — Anonymous Pro 400, muted. Same ladder
on every task row (queued rows: CUE takes DONE's step). The
tear row (DONE — TEAR) inherits: TEAR prints at DONE's step,
never accent — the accent is spent on START alone (F-01).

## F-16 — DECK wheel — the specular's lit arc doesn't reach the quadrant the finger lives in
**Seat: Glanz (specular & sheen). Verify-first finding.**
The register carries a real specular rig (rest-state,
0.16 + 0.66·t³, lamp 220°, never over text, ≤ 35%). In the day
wheel crop the light reads only along the top arc; the lower
quadrant — where the pointer parks and the finger works — is
flat matte, so the wheel feels lit like a picture of a wheel,
not like a thing in the room.
**Finish:** confirm in the wheel view whether the rest-state
falloff spans the lamp quadrant (200°–250°); if it does not,
extend the same settled curve across it — no new numbers, the
rig's own curve, wider arc — and let the F-02 vane inherit the
lamp so pointer and ring share one light source. If the code
already spans it, this finding closes itself and the render
harness owes us a truer crop.

---

## Protected — do not touch
**The Deck's hero time readout.** Sora numerals at hero size,
the slashed zero, the tight leading, and the mixed-case "1H 00M"
form on the wheel face. It is the app's voice at its loudest and
it is already right — distinctive without decoration, an
instrument's face. No seat, builder, or future pass "improves"
the zero, letterspaces the hero, or swaps the face. Finishing
happens *around* it (F-02, F-06, F-12), never *to* it.

— Aurel, for the Finishers (Luce · Stoff · Tonwert · Glanz ·
Rand · Fuge · Mass · Glatt · Echo · Nacht · Aug · Licht)

---

# PASS 2 — Deck and Tasks, deepened by the new benches

*Appended by Aurel, Oct 29 2026, from the first Pass-2 wave:
Abriss + Weiss (Tasks), Wirbel (Deck motion), Mond (night bench).
Schein's Deck-light deepening is still in flight and appends
when it lands. Judged on the merged trunk renders (shots/03 +
n01/n03, native resolution) and the trunk code. Same standard:
exact surface, builder numbers. Where a Pass-1 item is confirmed
still open in trunk, it says so — the chrome build inherits both
passes.*

## P2-A — TASKS, the tear (Abriss)

**P2-A1. The tear's verb is guillotined at rest.** Queue leaf
#03's action row is sliced mid-glyph by the band in both modes —
F-04 still open on Tasks. Finish = F-04 as written (100dp bottom
padding + 24dp fade), applied to the Tasks LazyColumn.

**P2-A2. The tear's last act is a teleport; the 260ms settle has
never been visible.** The PerforationRow sits inside the fading
graphicsLayer, so the dash-halving's final 80ms play on an
invisible leaf; completion fires at delay(180ms), amputating the
settle (Critics' MO-2, confirmed); and no animateItem spec
exists — at t=180ms the remaining leaves snap up a full
leaf-height (~150dp) in 0ms. Finish: (1) PerforationRow moves
OUT of the fading layer — the torn edge belongs to the pad, not
the departing leaf; (2) completion delay 180 → **260ms**, dash
settle unchanged (specB, 3→1.5dp); (3) remaining leaves take
`animateItem(placementSpec = specB 260ms)` — the pad glides
shut. Total event ≈520ms tap-to-rest.

**P2-A3. The fall is a sigh, not a tear.** 10dp + 180ms on
curve A (zero slope both ends) is a glide; paper releases and
drops. Finish: drop 10dp → **14dp**, 180 → **140ms**, curve A →
new **curve D = CubicBezierEasing(0.42, 0, 1, 1)** (priced: the
settled set has no gravity curve). Fade on a separate track:
alpha holds 1.0 for 40ms (the rip seen at full ink), then fades
180ms curve A, gone at 220ms. What lingers is the settle
(P2-A2), never the piece.

**P2-A4. Night perforation is the faintest structural mark on
the screen.** Night dashes on #171412 fall below the pad edge's
contrast; the night tear tears along a line that isn't there.
Finish: night dash colour = text-2 **#9C8E84 @ 55%** (F-12's
settled night derivation); day unchanged; the sealed overprint
inherits the token.

**P2-A5. The held blank is a stub, not a held page — and has
never been rendered.** Empty-queue branch totals ~72dp against
a real leaf's ~150dp; the pad collapses ~80dp in the same
un-animated snap. Finish: blank Box **56dp → 112dp** (NOTHING
QUEUED keeps its first-line position; the second 56dp is empty
ruled air — see P2-W6 for the missing rule), blank takes the
same animateItem specB 260ms glide. PROOF OWED: empty-queue
stills, day + night, in the harness set.

**P2-A6. F-15's third step not landed.** DEL/CUE wear Atkinson
Bold 12sp (stroke weight = DONE's) and TEAR's letter-spacing
1sp splits the label into three floating tokens. Finish: DEL +
CUE → **Anonymous Pro 400, 12sp**, colour unchanged; START stays
Atkinson Bold 12sp accent (it reads as the money verb — do not
move it; but see P2-W7 for the ladder conflict); TEAR
letterSpacing **1sp → 0.5sp**.

**P2-A7. The first leaf's perforation binds to the header.**
~24–28dp of air above the first perforation vs ~12dp below it:
the dashes read as the header's underline. Finish: air above
the first perforation **≤ 12dp**; below unchanged.

**P2-A8. PROOF DEBT (verify-first).** No mid-tear frame exists
anywhere. Required before P2-A2/A3 can close: a frame sequence
at **t = 0 / 70 / 140 / 200 / 260ms**, native resolution, day +
night (harness with seeded tearing state + advanced clock, or
device screenrecord), plus the stills owed: sealed-state Tasks,
empty-queue Tasks (P2-A5). Warning to every future judge: the
circulated 700px stills misrepresent the tear (perforation →
apparent solid rule; DONE — TEAR → apparent DONE) — judge at
native resolution or not at all.

## P2-W — TASKS, the light of the paper (Weiss)

**P2-W1. There is no paper. (The pass's deepest finding.)**
Code- and pixel-verified: TaskLeafItem carries **no background
at all** — leaf interior = ground exactly, day #E4DFDA, night
#171412, **1.00:1 in both modes**. The counterfoil's whole light
premise — a white stub on a greyer desk — never lands; the
perforation is printed on the desk. The theme owns the paper
white (SurfaceBright #FAF6F0) and Tasks spends it on a horizon
segment (P2-W3) instead of the paper. Finish: the ACTIVE (head)
leaf alone takes the paper step, full rect behind its print,
corner radius 0dp: day **#FAF6F0** (1.23:1), night **#362C27**
(1.35:1). Queued leaves stay flush (resting-slip law: zero tone
at rest, the top slip catches the light — right, and unbuilt).
On the raised active leaf, its perforation dashes switch to
ground colour at 100% — punched holes, not ink.

**P2-W2. Night horizon segment 0 is a floodlight.** Built from
tokPrimary(), which flips: night = **cream #FFE4CF at full
value, 15.06:1** — the brightest filled object on night Tasks.
The night stub does not glow (it has no fill); the glow
migrated to the bar above it. Finish: night segment 0 → cap
**#4A423C** (1.86:1), glyph #FFE4CF, 1dp top-edge marker line
#FFE4CF @ 90%. Absolute ceiling for any lighter active: F-03's
#A79587 — never raw #FFE4CF as a fill. Day unchanged.

**P2-W3. Day horizon staircase runs off the paper scale,
backwards.** Day segments ≥2 = SurfaceBright #FAF6F0 — an
unselected element lighter than the selected one (F-05's crime,
live in trunk). Finish: day staircase compressed onto the desk:
seg 0 #252323, seg 1 #615B5A, seg ≥2 **#D8D1CB** (SurfaceVariant,
1.14:1 below ground — recess, not raise), glyph #2C2A29.
#FAF6F0 on Tasks is then reserved for the active leaf (P2-W1)
and the nav capsule. (Mond's M-2 files the same disease at
night with the F-05 construction as cure — one staircase, both
modes, settled once.)

**P2-W4. The pad edge reads as a rendering error.** Pixel-
verified: 1dp lines at **2dp pitch**, one colour, full leaf
height (4dp inset) — at arm's length they fuse into a ~3dp grey
bar, align leaf-to-leaf into a continuous right-edge rule
(scrollbar artifact), and print depth at identical alpha so
depth must be counted, never felt. Finish: pitch **2 → 3dp**
(centres 1.5/4.5/7.5dp, width 1dp; content end-padding
leavesBehind×2+6 → ×3+6dp); value stepped by depth — day k0
#A8A39F, k1 #BDB7B4 (30%), k2 #CCC7C3 (18%); night k0 #4E4847,
k1 #3B3735, k2 #2D2927; top/bottom inset 4 → **8dp**.
Verify-first: re-render the 3-leaf queue; if the pair still
fuses at 360dp width, go 4dp pitch, not higher alpha.

**P2-W5. Perforation and pad edge share one ink.** Both draw
outlineVariant, same alpha: a cut and a stack edge in one voice.
Finish: queued-leaf perforation alpha 46% → **30%** (day blend
#BDB7B4, night #3B3735); active-leaf perforation = P2-W1's
ground-colour holes; pad edge = P2-W4's stepped scale. Cut ≠
edge ≠ rule at a glance, both modes.

**P2-W6. The ruled blank is unruled.** Code-verified: the blank
leaf has no rule line at all, though the plan names it a ruled
blank. Finish: one solid rule under NOTHING QUEUED, baseline
+8dp, 1dp, full leaf width minus pad inset: day #615B5A @ 18%,
night #8F8888 @ 18%. Text stays muted (pencil tier is right).
The blank stays flush even after P2-W1 — it is the bottom of
the pad, not the top leaf.

**P2-W7. TEAR outweighs START in everything but colour — the
F-15 ladder conflicts with P2-A6's read and is settled here.**
Code-verified: DONE — TEAR = Anonymous Pro Bold 15sp, spacing
1sp; START = Atkinson Bold 12sp accent (day 2.59:1 — the weakest
strong mark on the row). F-15's specced ladder (START at
Anonymous Pro 700, accent, spacing 0.5, DONE's step) is absent
from trunk. Finish: exactly F-15 — START → **Anonymous Pro 700,
accent, letter-spacing 0.5, 15sp step**. Abriss's "do not move
START" (P2-A6) is respectfully overruled by his own evidence:
colour alone is not the ladder. If P2-W1 lands first, START's
day contrast on the raised leaf improves to 3.18:1 — no
alert-step substitution needed. Night accent #F47442 (6.48:1)
unchanged.

## P2-R — DECK, the wheel's motion (Wirbel)

**P2-R1. The FLOW→pointer arc bridges the Strait.** A ~7dp ink
arc (night: the brightest stroke on the wheel) runs unbroken
through basin and strait; Vega's strait is a discontinuity —
knurl cut, floor dropped, 9° of silence — and the arc denies
it, visually and (growing through the step) in motion. Finish:
no stroke for **24°** centred on the strait (18° + ±3° shore);
basin segment, if any survives, is a different object: **2dp,
text-2 @ 40%, at the recess radius**, never the pointer's
full-value ink. Rest-state wake length = 0° outside the seated
value's own arc.

**P2-R2. The fling clamp is exactly the strobe frequency.**
ω₀ clamp ±540°/s at 60fps = **9.0°/frame = one detent pitch per
frame**: the tick ring appears frozen while the pointer
travels, and in the hand detents cross at 60/s — a buzz, not
ticks. Finish: clamp **540 → 480°/s** (8.0°/frame, 1.0°/frame
off pitch). Above |ω| > 180°/s: minor ticks fade to 0; detent
haptics sub-sample to **≤24 events/s** (fire every ⌈ω/216⌉-th
detent). PROOF: frame-stepped 60fps capture at 540/480/360°/s,
tick phase printed per frame — 540 must freeze, 480 must not.

**P2-R3. Three coast laws are co-registered and no two agree.**
Exponential decay 2.4/s (Designers §06) + viscous τ(n)
0.34→0.48s (≡ 2.94→2.08/s) + a Coulomb brake ∝ √I_rel, all
registered at once; implemented together a max fling coasts
~101°, under 2.4/s alone **225° — 69% of the wheel's whole 324°
travel, 25 detents / 125 min from one flick**. And the
FLING DEADLINE ≤1,400ms never says whether the ≤520ms seat
lives inside it (release-to-rest up to ~1,920ms on the
coast-only reading). As registered, the invariant set passes
under any implementation — unfalsifiable, therefore unworthy.
Finish: ONE coast law in the register — λ_eff(n) defined once,
brake folded in, no separate 2.4. Deadline = **release-to-rest
≤1,400ms total** → coast budget 880ms → **λ_eff ≥ 2.82/s (n=1)
→ 2.44/s (n=8)**, capture entry at ω ≤ 45°/s. Max coast ≈170°.
PROOF: a published (θ, ω) integrator trace from the B2 build at
ω₀ = max, n = 1 and n = 8. No trace, no verdict.

**P2-R4. The coast seat's amplitude is uncapped.** Drag seat
(280, ζ 0.78, ≤2°, crisp) vs coast seat (210, ζ ≈ 0.38, two
overshoots, ≤520ms) — the register caps count and time, nowhere
amplitude; at capture entry 45–65°/s the first overshoot is
≈3.4–4.9°, able at the top of that range to re-cross into the
neighbour well and fire a second knock for a seat that already
happened. Finish: amplitude envelope — **overshoot 1 ≤ 2.0°,
overshoot 2 ≤ 0.5°**, seat knock fires **once**, at first seat
crossing only. Either cap capture entry at ω ≤ 30°/s for the
210/0.38 spring, or raise the coast seat toward k ≥ 280;
builder picks, the P2-R3 trace decides.

**P2-R5. The Strait toll is regressive.** Fixed −90°/s is a
50% amputation at the minimum crossing speed (180°/s — a wall,
felt as malfunction) and 17% at 540. Finish: proportional toll
**Δω = −25% of ω at the centreline, floor −30°/s, cap −90°/s**
(180 → −45; ≥360 → −90). Fall-back threshold (<180°/s)
unchanged.

**P2-R6. The pointer hides the very tick it seats on.**
Measured: pointer centre at clock-angle 246.0°, the 30 tick at
254.6° — the seat is true (one exact 9° detent, 25 min), but
the disc spans ~2 tick pitches: the seated tick is under the
disc, both ±9° neighbours clipped; the seat is unverifiable by
eye in every still and every approach's last step. Finish
(motion's addendum to F-02): pointer width at the tick band
(R114–130) **≤ 12dp ≈ 5.6° at R122**; knurl hidden under the
pointer at any seat ≤ 4 teeth (currently ~10).

**P2-R7. Knurl and tick band speak one language — and the
pitch hides seat error.** Tooth 8.0×2.4dp vs minor tick 8×1.0dp:
same length, same grammar, 6dp clearance — one doubled ring.
Pitch 3.000° = 9°/3 exactly: a seat error up to ±1.5° is
invisible against the knurl's symmetry. Finish: tooth **8.0 →
5.0dp**, band out to **R139–144** (clearance 6 → 9dp); keep the
3° pitch (honest under a held finger) but break the
length-equality so scale and grip separate at a glance.

**P2-R8. Hub torsional lag is unjudged, not passed —
verify-first.** The rig specs lag 4° (n=1) → 9° (n=8), settle
≤480ms, ζ ≈ 0.45 — but current renders still show loose pips,
not the tetrad closure bars the lag was designed to bend; a
lagging pip ring reads as delay error, not weight. PROOF: a
render/trace of the tetrad hub mid-fling at n = 8, bar trailing
by a measurable angle, settling ≤480ms.

*Protected (Wirbel): the held drag — 1:1 with grab offset, no
teleport, no easing at any count; the seat lands exactly on the
detent; FLOW carries zero load (I_rel = 1). No finding above
touches any of it.*

## P2-M — THE NIGHT SCREENS + THE AOD CAPTURE SPEC (Mond)

*Measured = dominant-colour census of the named surface,
n01/n03 at native resolution; judged at arm's length, 03:30.
Headline: F-03, F-12, F-13 are all still live in the current
night renders.*

**P2-M1. The NIGHT pill is a lit window in the bedroom.**
Night fill measured **#FFE4CF at full value** on both screens —
brightest filled object after the hero (on Tasks, brightest of
all). Finish = F-11 exact: fill + outline deleted at night;
text-only "NIGHT", Anonymous Pro 700, text-2 #9C8E84, 44dp hit
target. Interim if chrome cannot land it: fill capped to
#A79587 with #171412 text — never full cream.

**P2-M2. Tasks night horizon: segment 1 burns full cream.**
Measured: seg 1 #FFE4CF full cream, seg 2 #4A423C, seg 3
#362C27 — two full-cream slabs in Tasks' top 160dp, two
unselected fills for one state. Finish = P2-W3's staircase,
night construction per F-05 (selected #4A423C + glyph #EDE3D8
+ 1dp marker #FFE4CF @ 90%; unselected no fill, outline
#9C8E84 @ 40%). The proof the cure exists: the duration row
below already speaks F-05 correctly (measured #4A423C).

**P2-M3. F-13 closes as FAILED, with a number.** The night Deck
card interior measures **#171412 = ground exactly (594,620 px)
— zero luminance step**; separation is a 1dp hairline ≈ #4E4847
alone. The card is not a place. Finish: card fill token →
settled raised **#362C27**; dependent step: the wheel inner
disc is currently #362C27 and would merge — step the disc to
night cap **#4A423C**. Hairline unchanged. A fresh night render
of all five screens remains a required chrome-build proof.

**P2-M4. F-12 still live; the numerals float.** Annulus census:
minor ticks effectively absent at render width; numerals (30…
180) measured #9C8E84 at ~13sp — at 60cm they smear into the
ring. Finish = F-12 exact (minor #9C8E84 @ 55%, major #EDE3D8
@ 80%) + Mond's addition: the six duration numerals rise to
glyph **#EDE3D8 @ 70%** (≈ #ADA59D on #171412), same size and
font — a number you must read to set the session may not sit
at label value.

**P2-M5. The lollipop is the brightest small object on the
night wheel.** Pointer disc measured #EDE3D8 with the
dead-centre dot; the set-arc also runs full #FFE4CF. Finish =
F-02 with night values: disc → night cap **#4A423C**; dot
deleted → 6dp dimple in ground #171412 @ 40%; tail 7 → 4dp in
#9C8E84; leading edge 1dp marker #FFE4CF @ 90%. Set-arc capped
per F-03 → #A79587, top-edge hairline kept full cream @ 90%.

**P2-M6. The idle slide cap advertises at day saturation in a
dark room.** Measured #F47442, ~14,750px — the largest accent
area in either night render. F-01 keeps it the Deck's sole
accent; Mond's distinction: Ledger's current brick is live
state, this cap on an unarmed screen is an invitation. Finish
(proposal, priced as a state split): night + idle → cap =
accent blended 20% toward ground → **#C86138**, marker line
stays #FFE4CF @ 90%; armed/active returns to full #F47442.
Fallback if struck: one flat night value **#D3663B** (15%
blend), both states.

**P2-M7. The struck subtask is a grey scratch.** "Draw
free-body diagrams…" measures #776A60 with its strike — neither
legible nor gone; the active card must read cleanest and reads
damaged. Finish: struck text → **#9C8E84 @ 45%** (≈ #534B45 on
#171412), strike 1.5dp same colour @ 70%; line height and row
position unchanged.

**P2-M8. F-04's guillotine still live at night.** Queue #03
shows three subtasks, then the surface ends under the band;
its action row never appears, no fade. Finish = F-04 exact
(100dp padding + 24dp fade to #171412). Deck acquitted on this
count — its NOW line and slide sit clear with air to spare.

**P2-M9. The one number a night glancer wants is tied for
faintest on the card.** TOTAL / WILL SHUT 7 APPS / FINISHES
10:23 AM all in #776A60 tracked caps. The labels deserve to
dissolve; the finish TIME is glance-information #2 at 03:30.
Finish: labels stay #776A60; the time value only → **#9C8E84
@ 100%**, same size, weight, tracking.

*Acquitted at night, in writing (Mond): the protected hero
readout (#EDE3D8 on #171412 — the model every finding points
back to); the band staircase (#362C27 / #4A423C measured);
Tasks' duration row + ADD (#4A423C); the task input recess
(#100E0C); the HELD/GUARD header chips (they dissolve,
correctly).*

**P2-M10. THE OWED AOD CAPTURE — spec for the chrome build.**
So the desk clock is finish-judged in ONE pass (Nacht first
claim, Mond seconds; a set missing any item is returned
unjudged):
- *Format:* lossless PNG, native resolution, no JPEG, no
  harness downscale. Filename: `aod_<state>_<elapsed>_
  <remaining>_<block>.png`. Each PNG ships with a colour census
  (top-10 colours + pixel counts). Ground rule the census
  settles in one line: **AOD ground must sample #000000** —
  if the face paints #171412, every pixel glows.
- *Brightness caps (state them in the build note):* hero ≤
  glyph #EDE3D8 @ 60% effective; no filled surface besides the
  hero above the F-03 night cap #A79587; no full-cream #FFE4CF
  fill anywhere; accent #F47442 on at most one element, ≤ 2%
  of frame area.
- *Ten states, one seeded 25m session family:* **01** idle,
  wall 03:30, inside the SLEEP window (the actual nightstand
  case) · **02** armed, not started · **03** active early
  (03:00/22:00, block 1 of 1) · **04** the seam pair — block 1
  of 3 at remaining 00:10 + first frame of block 2, same seed
  (the span charges live here; bar/elapsed must not regress
  between the pair) · **05** on break (05:00 of 10:00, state
  word BREAK) · **06** safe break, valve spent (tokens 0/1,
  cold-open first frame included) · **07** final minute
  (00:45) · **08** penalty state (seed P=900; filename prints
  the stored elapsed/remaining the render must match) · **09**
  sealed/complete, captured 60s after completion — does the
  face persist, dim, or blank? · **10** motion/burn-in/seam in
  three parts: (a) frames at t=0/5/10s of state 03 — any
  per-second animation is a wake risk, judged as one; (b) the
  same state after ≥10 min at the shifted burn-in offset —
  translation only, shift ≥ 4dp, nothing clipped; (c) a seam
  pair: last AOD frame + first full-Deck frame on tap, same
  seed.
- *Distances of information — each capture at 100% + the
  harness's own 50% and 25% downscales:* **Tier A (~2m, the
  25% file):** wall time + remaining hero ONLY — anything else
  still competing here fails. **Tier B (arm's length, 50%):**
  elapsed, remaining, state word, block x-of-y. **Tier C (in
  hand, 100% only):** finishes-at time, session name, tokens,
  penalty note — and Tier C matter must be absent or illegible
  at Tier A. That separation is the desk clock's whole finish.

— Aurel, filing for the fifty (Pass 2, first wave)

## P2-S — DECK, the light (Schein) — appended on landing, Pass 2 complete

*Pixel-sampled at native resolution against REGISTER.md's
specular entry (rest-state 0.16 + 0.66·t³, lamp θ_L ≈ 245.2°,
hub core azimuth 315°, caps ≤14% day / ≤8% night, tint
#FFF6EC). Headline verdict: the specular rig does NOT read as
one lamp — the rim arc says 300°, the bezel says due south,
the register says 245.2°, and at night three unrelated cream
sources burn at full value while the actual specular sits at
grey-smudge level.*

**P2-S1. F-13 settles, both modes, worse than filed.** Pixel
count: day card interior = #E4DFDA = ground (75.1% of frame);
night card interior = #171412 = ground (75.0%). The wheel face
is the same colour — outside, card and face are one plane,
separated only by the 1dp hairline. Proof the staircase exists
in the tree: hub disc and bottom band already carry the raised
step (day #FAF6F0 / night #362C27); the card alone misses it.
Finish: Deck card fill → raised token, day **#FAF6F0** / night
**#362C27**; nothing else moves (outside stays ground, wheel
face stays ground, recess and hub unchanged). One token
restores the four-step relief. (Mond's P2-M3 is the night half
of this finding; Schein's disc-step caution and Mond's agree —
the wheel's inner disc, currently the raised value, steps to
night cap #4A423C where it would otherwise merge with the
raised card.)

**P2-S2. F-16 deepens: the specular is a constant chalk line,
centred 54.8° off the lamp, hard ends, no falloff.** Measured
at r 120–121dp: lit span exactly 250.4°–349.6°, centre 300.0°,
colour uniform across 96° (day #F6F2EC — not the registered
tint #FFF6EC; night #35302B), switching on/off in one pixel at
both ends. At the registered lamp (245°) the rendered face is
plain ground in both modes. Centre 300° sits inside the HUB
core's registered zone (315° ±34°) — the rim appears to be
wearing the hub's lamp. Finish: re-centre on **θ_L = 245.2°**;
stroke 2dp at r 120dp; tint #FFF6EC; alpha = **0.16 +
0.66·cos³(θ − 245.2°)** for |Δ| ≤ 90°, 0 beyond — ends fade
instead of stepping. Peak within register caps. VERIFY-FIRST:
a static render cannot show whether this arc is screen-fixed
under B2 physics — one mid-drag render settles it; if the arc
rotates with the rim, S-2 is a code bug, not a tuning bug.

**P2-S3. The outer bezel is two flat half-rings with a hard
step at 90°/270° — a lamp due south.** At r ≈136dp: upper half
day #B4AEA9 / night #0D0B0A; lower half day #DDD7D2 / night
#31302E (night luminance 11 vs 48 — 4.4×). A ring under one
lamp grades continuously; it does not step at east/west.
Finish: delete the two 180° arcs; tone bezel/knurl per tooth
(3.000° pitch) with S-2's curve — base mixed toward #FFF6EC by
(0.16 + 0.66·t³), peak ≤14% day / ≤8% night, brightest tooth
at 245.2°, darkest at 65.2°, no adjacent-tooth step > ~2%
luminance.

**P2-S4. F-02 deepens: the lollipop survives, and at night it
inverts into the wheel's brightest disc.** Disc Ø26dp, dot
Ø8dp, both unchanged. Day dot = #FAF6F0, luminance 246 — the
brightest thing on the wheel, a highlight no lamp in the room
can produce. Night disc flips to full glyph #EDE3D8. The tail
IS now 4dp (F-02's 7→4dp landed), but day tail = ink #252323
(not text-2), night tail = full cream #FFE4CF at 15.6:1
against the recess — louder than major ticks (4.7:1) and
numerals (5.8:1). Finish: F-02 as filed (dot → 6dp dimple,
ground @ 40%; leading edge 1dp marker) + night disc → cap
**#4A423C**, day disc stays ink #252323, tail stays 4dp but
day → text-2 **#5F5856**, night → glyph **#EDE3D8 @ 70%**,
never full cream.

**P2-S5. The hub has no specular core; its dot ring steals the
core's seat at night.** Hub disc Ø127dp is one flat colour edge
to edge; the registered core (315°, ±9°, cos² to ±34°, +10%
day / +8% night toward #FFF6EC) is absent. Instead, two of the
eight Ø~9.5dp dots sit at 290° and exactly 315° and at night
render full cream #FFE4CF at 100% — two small lamps where the
law allows an 8% lift. Finish: build the registered core as
written (hub disc only, never over the dots); night active
dots #FFE4CF → **#EDE3D8 @ 80%**, Ø9.5 → 8dp; day actives
unchanged. Verify-first: renders cannot say what the dots
mean; if they are data, the night cap above still binds. No
banked-valve surface is visible in either rest render — if the
valve lives in the running arc's rest slot it has no render in
the set (same unpaid debt as the AOD clock).

**P2-S6. F-12 deepens: night majors half-rescued, minors
drown in both modes.** Night major #8C7F77 on face = 4.73:1
(better than filed, short of the ~7:1 finish); night minors
#4E4847 on recess = 2.15:1, unchanged; day minors #A8A39F on
#D8D1CB = 1.65:1, proportionally worse. Finish: majors night →
glyph **#EDE3D8 @ 80%** over face (blend ≈ #C0B2A4); minors
night → text-2 **#9C8E84 @ 55%** over recess (blend ≈ #5E564F,
≈3.2:1); minors day → muted **#5F5856 @ 55%** over recess
(blend ≈ #9A9189, ≈2.2:1). Day majors unchanged. One
derivation per mode branch, as F-12 said.

**P2-S7. F-06 CLOSES inside the card — do not re-chase.**
Measured gaps in current trunk: card top → TOTAL ≈19dp, hero →
wheel **38dp**, wheel → FLOW/FINISHES 17dp, row → card bottom
17dp — maximum 38dp, under the 40dp law (2dp slack only: any
hero-margin change reopens it). The desert moved OUTSIDE the
card: task line → slide track ≈ **179dp of bare ground** —
chrome/slide territory, flagged for Mass and the chrome build,
no Deck numbers here. Corroboration for F-11/P2-M1: the night
NIGHT pill is a 53×31.5dp slab of full #FFE4CF (luminance 232)
— with S-4's tail and S-5's dots, three full-cream sources
plus the accent on one night screen.

**P2-S8. The slide cap's stripe is 3dp in a 1dp marker
language.** Cap interior is perfectly flat accent (fine for a
commit cap); its cream stripe measures **3dp × 36dp**. If it
is the marker line it is 3× the settled width; if it is a grip
groove it lacks the groove's shadow. Verify-first/finish: the
builder names which it is in code. If marker: 3 → **1dp**,
#FFE4CF @ 90% both modes. If groove: keep 3dp, add a 1dp
shadow line **#471602 @ 35%** immediately left of the stripe.

*Protected (Schein): the recessed tick channel — a perfectly
uniform black groove at night (#100E0C, r ≈126–134dp) with the
pointer's tail running in it like a needle in a machined slot.
Keep its colour and width exactly; P2-S4 only lowers the
tail's alpha inside it.*

*Schein's NEXT (Law 10, his own words, condensed): (a) Pass 2
on the Shield's sled caps under the same lamp — the register
gives caps a travelling specular (peak 14% day / 8% night) and
no render shows whether the shipped caps carry it, a static
stripe, or nothing. (b) Unassigned: re-judge S-2/S-3/S-5 from
the first post-fix night render set — a half-sitting
verification pass.*

— Aurel, filing the last seat (Pass 2 first wave COMPLETE)
