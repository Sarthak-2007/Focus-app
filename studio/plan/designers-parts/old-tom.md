# OLD TOM — Designers, Veteran seat (29 years)
## THE FREEZE CLAUSE + THE DATEDNESS AUDIT — PLAN WAVE, no code

Coordinator: Sana (Designers). Seat: Veteran.

Sarthak's order, as it reached me, governs this file: citations lifted,
full freedom. What binds is settled law only, and part of my job below is
to say exactly which parts of that law are *finished* — because a system
nobody is allowed to finish is a system that gets redesigned every
quarter until it looks like every other app in the store.

My wave-1 record, stated once so the defeats are on the page and not
lurking behind this file: my radius freeze **won** (8dp / 16dp, locked).
My margin **lost** (20dp, not 24 — Raster's file makes 20 close exactly,
and a grid is the number that divides, not the number I wanted; I do not
reopen it). My knurl kill **lost** (cheap lit teeth ship, specced by
Uhr — §3.1 is my answer to that loss). My zero-idle-motion position
largely **won**: ceremony on state change only, never on the daily path,
never ambient.

---

## 1. THE FREEZE CLAUSE

*Written to be lifted verbatim into the head of the token file. Nothing
before it, nothing between its articles. If a builder reads one page of
this plan, it is this one.*

> ## ❄ FROZEN — Monolith visual system, v1 (29 Oct 2026)
>
> The articles below are **finished**. They are not a style guide, a
> starting point, or a set of defaults. Future work — new screens, new
> states, new features, redesigns — builds *inside* them or it does not
> ship. No screen, seat, cohort, or deadline may drift, fork, locally
> vary, or "temporarily" suspend any article. The only person who can
> amend this clause is **Sarthak**, by explicit order naming the article
> (see Amendment, end).
>
> **Article 1 — SURFACE SYSTEM.** Depth is a value step plus a hairline.
> Nothing else exists. Adjacent surfaces differ by exactly **one** tonal
> step — never two, never zero-with-a-border-instead. Elevation and
> shadow: **0dp, everywhere, no exceptions.** The one permitted edge is
> a **1dp hairline, ink at 12% alpha, on the light-catching side only** —
> never a full-perimeter border *plus* a fill *plus* a shadow; exactly
> one edge device per slot. A raised tone is spent **only on a part the
> finger touches** (wheel hub, sled cap, loadout cap, slide thumb, key
> cap). Static content never rises. Day staircase, by token: ground
> `#E4DFDA` → panel `#D8D1CB` → recess `#CFC6BE` → raised (touch only)
> `#FAF6F0`, ink `#252323`.
>
> **Article 2 — RADII.** Two radii exist. **`RadiusSmall = 8dp`** for
> every element ≤48dp tall: caps, chips, keys, sled tracks, tiles.
> **`RadiusPanel = 16dp`** for full panels. Nothing else. No fully
> rounded (`999dp`) silhouette on any new work — the only true circles
> in the app are the wheel itself and the 3dp status pip. A pill is a
> fashion marker, not a shape this app uses.
>
> **Article 3 — TYPE FAMILIES.** Three families exist, and no others:
> **Sora** (display), **Atkinson Hyperlegible** (body / interface),
> **Anonymous Pro** (numerals / telemetry). **Real weights only** —
> Sora as shipped in true instances; Atkinson exists at **400/700
> only**; Anonymous Pro exists at **400/700 only**. No style may request
> a weight that does not exist as a real font file; a request that
> silently resolves to another weight is a lie in the code and a build
> failure. No alias names for these families survive in the type file.
>
> **Article 4 — TYPE RAMP.** The ramp is closed: **six sizes —
> 104 / 72 / 50 / 22 / 15 / 10sp** — heroes / title / body / plate, no
> tier within 1.4× of its neighbour, minimum joint 1.44. The
> micro-label plate is one style everywhere, no exceptions:
> **Anonymous Pro Bold 10sp, +2.0 tracking.** Hierarchy inside a tier is
> built from family, weight, case and tone — **never from a seventh
> size.** A role that fits no rung is re-thought, not re-sized.
>
> **Article 5 — NIGHT STAIRCASE.** Night is dimming, not inversion.
> Ground `#171412`; steps between adjacent surfaces **≥1.35:1**;
> **recess sits BELOW ground** (`#100E0C`) — a recess that glows is a
> bug, not a variant; raised `#362C27`; control fills cap at
> **`#4A423C`** and nothing filled goes brighter. Text tiers hold at
> ≈**11 / 5.6 / 3.4:1**. **Cream `#FFE4CF` / `#EDE3D8` is a type-and-glyph
> colour only** — never a fill larger than 24×24dp. One night accent,
> `#F47442`, under Article 6. AOD ground is true **`#000000`**.
>
> **Article 6 — ACCENT RATION.** Orange is a **fill** and a **verb —
> COMMIT** — and nothing else. Day `#F55314`, night `#F47442`, on-accent
> `#471602`. **Exactly one live COMMIT fill per screen, maximum.**
> Chrome carries **zero**, with the sole exception of the 3dp armed
> lamp. Orange is never type, never a numeral, never a count, badge,
> label, or alert colour. Alerts are carried by form — position,
> weight, word — never by a second colour.
>
> **Article 7 — SPACING SCALE.** Gaps, padding, spacers and offsets may
> only be **{4, 8, 12, 20, 32, 48}dp**. Screen margin is **20dp**,
> both sides, every screen, no nesting exception. Section breaks are
> **≥32dp**. Every screen shares the same start beat. 16 and 24 survive
> only as *sizes* (a line box, a radius) — never again as gaps. Any gap
> literal outside the scale is a bug, on sight, whoever wrote it.
>
> **NOT FROZEN — read this before citing the clause as a refusal.**
> Screen composition, element form, copy, motion curves and durations,
> haptic and sound patterns, the Ledger's shape, Guard's shape, and
> every feature decision are **not** in this clause and remain open to
> any seat's better argument. The freeze protects the *material* of the
> app. It is not a licence to stop designing what the app *does*.
>
> **AMENDMENT PROCEDURE.**
> 1. Only Sarthak amends, by an explicit order that **names the
>    article** it changes. Silence, absence, and "Ren said creative
>    control" are not amendments.
> 2. A proposal reaches him with evidence, not taste: the measurement
>    that fails (contrast ratio, ratio joint, step count), day **and**
>    night renders of **every** screen the change touches, and a
>    statement of what the change breaks. A claim nobody measured is
>    not a proposal — this app has already shipped one theme "landmine"
>    that measurement acquitted and one real 1.95:1 failure nobody had
>    flagged; measurements decide.
> 3. An amendment lands **once, app-wide, in one commit** — every
>    screen, both modes, same change. A per-screen exception is drift
>    wearing an amendment's clothes and is void on sight.
> 4. Every amendment is appended below this clause with date, article,
>    and Sarthak's order in his words. The clause's history is part of
>    the clause.
> 5. **Restoring** a frozen value that code has drifted from — real
>    font weights for a lying registration, a recess re-sunk below
>    ground, a seventh size removed — is enforcement, not amendment.
>    It needs no order. It does get logged.

*End of clause.*

One sentence of commentary, outside the clause, for whoever maintains
it after me: the freeze will be attacked as rigidity exactly once —
the first time a good designer wants a local exception for a good
reason. That is the attack it was written for. Local exceptions for
good reasons are how this app arrived at eleven gap values and four
renderings of one duration.

---

## 2. THE DATEDNESS AUDIT

Method: I walked the planned app screen by screen — chrome, Deck,
Shield, Tasks, running state, AOD, overlays, Guard/System, the record —
and asked one question of every element: **in October 2028, does this
read as an instrument, or as 2026?** Three verdicts only. **KEEP** =
ages on its own merits. **AGE-GRACEFULLY** = will show its year, but
quietly, and the cost of removing it exceeds the cost of its ageing.
**KILL** = fashion wearing function's clothes; it will be the first
thing a future redesign is embarrassed by, so be embarrassed now,
cheaply.

### 2.1 Already dead — do not resurrect

| Element | Note |
|---|---|
| Dot-matrix digits | Killed wave 1 (my K1). Novelty alphabet, values printed twice to be readable. Any reappearance — AOD, receipt, loading state, "just for the hero" — is resurrection, not homage. |
| Streak Tower brick grid | Killed wave 1 (my K6). Toy bricks inside an instrument. The column chart that replaces it is a drawing of data; the tower was a picture of a toy. |
| Hardware faders for read-only data | Killed wave 1 (my K7). A slot that promises travel the finger never gets is a lie first and a fashion second. |
| Floating pill nav, DAY/NIGHT pill, nameplate data pills | 2021–24 Material fashion, already ageing in the current build. Their replacements (band rail, war line) are audited below on their own merits. |

### 2.2 The audit proper

| # | Element (where) | Verdict | Why |
|---|---|---|---|
| D1 | **Cheap knurl** (Deck wheel rim) | **KEEP — conditional, test in §3.** Automatic KILL if the test fails. | See §3. This is the single most fashion-exposed object left in the plan and the only one I lost. It survives on conditions, not on goodwill. |
| D2 | **Travelling specular rig** (wheel, caps, sleds) | **AGE-GRACEFULLY** | A fixed light that parts pass through is old industrial truth, and Mara's law (light carries no data) keeps it honest. It ages because *simulated* light on glass is a 2020s skeuomorphic revival. It ages gracefully **only** at its specced ceiling (≤10% day / ≤8% night lift). Past that it is chrome, and chrome is jewellery. If a future hand "enriches" it, it converts to KILL. |
| D3 | **First Light dawn ramp** (Deck, first arm of the day) | **KILL** | A dawn gradient pulled up the screen once a day is wellness-app language — Calm with a slide rule. It is ceremony placed on the daily path, which settled law forbids, wearing sunrise cosplay, which no law needs to forbid because the eye will do it by 2027. The AOD's elapsed reading survives without it: a number is not improved by pretending to be a horizon. |
| D4 | **Escapement heartbeat** (running state) | **KILL the beat, KEEP the arc** | A stepped arc showing time's passage is a measured rendering — keep it. A *heartbeat* — a pulsing beat borrowed from horology — is ambient motion plus watch cosplay, two datedness debts on one element. Instruments tick on state change; they do not throb to reassure. |
| D5 | **Program Tape** (phase-2 spine) | **KILL as a rendering; KEEP its 20%** | NEXT ribbon and rim arcs at true duration scale are data drawn to scale — they age like a scale does. The full Tape — the day rendered as a paper strip with tear marks — is print/steampunk cosplay that will read as a 2026 concept-video prop. Phase 2 should spend its money on the arcs being *right*, not on the tape being charming. |
| D6 | **Paper grain / fibre texture** (receipt slips, any "paper" surface) | **KILL** | Grain that encodes nothing is wallpaper, and textured-paper UI is a named, peaked fashion (the paper-tablet era). Monolith is an instrument, not a notebook. The receipt is lines of type on the ground tone; it needs no fibres to be believed. Grain seeded to travel with a slip is cleverer wallpaper, not a different verdict. |
| D7 | **Skirted key-cap bank** (Tasks composer) | **KILL the skirt, KEEP the travel** | Mechanical-keyboard merch is the fastest-peaking aesthetic of this decade, and a skirt profile is its silhouette. What ages well is the *function*: 1–2dp of travel, answer on DOWN, spring back. A plain cap that moves will look right in ten years; a cap drawn to look like a keycap product photo will look like 2024. |
| D8 | **Melodic pitch sets for keys/steps** (composer caps, sub-steps) | **KILL the melody, KEEP the thock** | Assigning musical pitches to routine taps — wooden-bar sets, scale notes, a strum on the final step — is ASMR/game-feel fashion. Sound in this app is diegetic: one tick family, one thock, one seal, one gain. A control that plays a note is performing; a control that thocks is working. |
| D9 | **Accession number + stamp** (completion / record) | **KILL** | A sequence number is an archive in disguise and a stamp is museum cosplay; both also smuggle the Ledger back in through the receipt's footer (see §4). The receipt's one line of fact needs no provenance theatre. Verification language for an event the app witnessed itself is decoration lying about its job. |
| D10 | **Letterpress double-strike** (plate, war line) | **AGE-GRACEFULLY** | The plate itself — Anonymous Pro Bold 10sp, +2.0 tracking — is print discipline and timeless. The simulated pressed offset (+0.75dp shadow strike) is a texture effect, and texture effects date. It survives because it is a single pass at furniture scale, never a bevel, never a gradient, never on text that carries a decision. The day it grows a second pass, it is D6 with better typography: kill it then. |
| D11 | **Model plate `MONOLITH ML-01` / Guard spec plate** | **AGE-GRACEFULLY — quarantined** | Product-plate printing is borrowed Teenage Engineering identity, and borrowed identity is cosplay's definition. It ages gracefully **only** in its one permitted home: printed once, on the product's back (Guard/spec surface). If it appears in chrome, on Deck, or on a second screen, it has become a logo looking for merchandise — KILL on spread, no hearing. |
| D12 | **Section strip** (Shield, 24h elevation) | **KEEP** | An architectural elevation is a scale drawing of a real day: blocks are routines, gaps are measured and labelled, the now-line is ink. Scale drawings do not date; *styling* them does. Keep it stripped — the moment it grows serifs, leader-line ornament, or a second colour for charm, re-audit it as D5. |
| D13 | **Light Well** (Tasks) | **KEEP the steps, KILL the shaft** | A queue descending by tone steps is the staircase doing honest work — timeless. A drawn *shaft of light* falling on the active task is a lighting effect pretending to be a metaphor, and it will date with every other gradient-beam UI of this period. Tone and offset only; no beam, no glow, no source. |
| D14 | **Sleds, loadout band, slide-to-arm** (Deck / Shield / Guard) | **KEEP** | Position-as-state, a cap straddling its slit, one long committing gesture: these were old when I started and they will be old in the good way in 2028. Their risk is not form but *finish* — every specular flourish or skirt added to them moves them toward D2/D7. Built plain, they are the app's timeless layer. |
| D15 | **Band-switch nav + war line** (chrome) | **KEEP** | A rail with printed stops is a switch, and switches outlive menus. The war line is a masthead only in the sense that it prints facts in a line; facts in type do not date. Both verdicts assume chrome stays at zero accent and zero counts-as-badges, per Articles 6–7. |
| D16 | **AOD desk clock** | **KEEP** | Two/three readings, huge numerals, true black, a patrol shift nobody sees: this is the Punkt/Braun lineage, the oldest surviving language in the app. Nothing in it references its decade. |
| D17 | **Warm-grey + cream + single orange palette** | **AGE-GRACEFULLY** | Honesty requires naming it: warm neutrals are *this* decade's fashion, as surely as cool greys were the last one's. The palette will show its year. It ages gracefully because its structure — one ink, one accent, tonal steps instead of shadows — is older than its hues, and because Article 1/5 freeze means that when the hues do date, changing them is one measured amendment, not a redesign. Do not pretend it is eternal; make it replaceable. |
| D18 | **Toy gestures proposed in this wave** (block-stack counters, puck rows, discharge chutes, for Tasks/Deck) | **KILL — do not admit** | Physics toys for task completion are game cosplay aimed at a man sealing study blocks at 03:30. They are charming in a seat file and embarrassing in month three, when the hand wants a row, a word, and a strike. Tasks are rows. Counts are numerals. The stack/puck/chute family does not enter the build through any door, including "just as an experiment." |

**Net of the audit:** three structural KILLs that are still live in the
plan (D3 dawn ramp, D5 tape-as-rendering, D9 accession/stamp), four
cosplay trims (D6 grain, D7 skirts, D8 melody, D18 toys), one metaphor
amputated from its honest core (D4), and one conditional reprieve (D1).
Everything else that survives does so because it is a scale, a switch,
a gesture, or a number — the four things that have never dated in my
29 years. Everything I have killed, in both waves, was a *picture of a
thing* standing where the thing itself, or a plain number, belonged.

---

## 3. THE KNURL TEST — cheap vs costume

I lost the kill. Uhr's cheap version ships: 120 flat teeth, alternating
tones, lit by the travelling specular. My remaining job is the one the
brief gave me — make sure "cheap" does not become "costume." So here is
the test, written as a gate with a pre-committed consequence. It is run
on the built wheel, day and night, before the Deck is shown as done.
**Fail any one item and the knurl is deleted and the rim ships as a
clean machined edge — no appeal, no reduced version, no second test in
the same build.** A test without a consequence is a comment.

1. **The flatness count.** Every tooth is **one flat fill** from the
   alternating pair (day `#FAF6F0` / `#CFC7C0`; night `#4A423C` /
   `#241E1A`). Grep/inspect the draw: zero per-tooth gradients, zero
   highlight/shade line pairs, zero bevels. The moment a single tooth
   is individually *modelled*, it is jewellery.
2. **The fixed-light proof.** Screenshot the wheel; rotate it 180°;
   screenshot again. The flare sits at **315° screen angle in both**.
   If the light travelled with the wheel, the "metal" is a sticker —
   costume by definition.
3. **The scale-separation count.** Counted, not eyeballed: exactly
   **36 ticks, 6 numerals, 120 teeth**, teeth in their own band,
   separated from the tick ring. Then the human check: a viewer given
   the wheel for three seconds must not be able to mistake any tooth
   for a detent or read the band as a second scale. If the rim needs
   explaining, it is decoration competing with the instrument.
4. **The brightness ceiling.** At night, squint/blur: the knurl band
   must not be the brightest mass on the Deck (the numerals are).
   Specular lift on any tooth stays within the rig's cap (≤10% day /
   ≤8% night). A rim that out-shines the readout is a bezel on a watch
   advertisement, not a grip on a tool.
5. **The removal trial.** Build one variant with the band rendered as
   plain face tone. Use both, eyes on the readout, setting durations
   by hand. The knurl earns its place **only** if the toothed rim is
   felt — in peripheral vision and in the hand's expectation — as the
   wheel's *edge of grip*, the place the thumb's work happens. If the
   honest answer is "it just looks more detailed," that is the costume
   confessing. Delete it.

Cheap passes all five because cheap is a *manufacturing* fact: flat
fills, alternating tones, light doing the work. Costume fails because
costume is a *rendering* ambition: somebody wanted the rim to be
*admired*. This wheel is not admired. It is turned, at 03:30, by a man
who is looking at the number.

---

## 4. ON THE LEDGER — one paragraph, addressed to the verdict

*To Plan, for the cohort verdict file — please file this as written:*
I killed the dot-matrix and the dashboard in wave 1, and I want it on
record that I never killed the record — a machine that seals a man's
phone owes him an honest account of what it did, and Monolith's four
facts (sessions held, deflections absorbed, holds spent, routines kept
or ghosted) exist nowhere else, because YPT tracks his study and
nothing tracks his instrument; so now that the question is honestly
open, I stand with **DEMOTE**, and I stand with it as a veteran, which
means I distrust both of its neighbours for the same reason: a tab is a
dashboard asking for tiles, tiles ask for charts, and within a year the
record would be a Vault again with my wave-1 kills quietly resurrected
inside it, while killing the record outright leaves HOLD-absolute with
no memory of its own spent break and ghost routines with no evidence
that can convict them; the receipt at completion plus RECORD at the
foot of Shield — one hero count, hairline rows, zero accent, absence
rendered as absence — is the record at its correct weight, a logbook
kept at the back of the manual rather than a room he must walk through
daily, with one condition I attach in writing: demotion must not become
burial, so the hold's spent state must also live on the hold control
itself at the moment of need, and if six months of his actual behaviour
show a Sunday audit that goes looking for this record and cannot find
it at Shield's foot, the demote has failed its own test and I will
argue for the tab myself.

---

## 5. THE VETERAN'S SINGLE WARNING

**This plan will not be defeated by a bad decision. It will be
defeated by a thousand compliant-looking local implementations.**

Twenty-nine years of watching plans meet code teaches one pattern, and
this app has already lived it once: the renders looked right while the
code underneath lied — font weights that did not exist, a settle
animator animating a variable nothing drew, a slide that was secretly
a button, one duration rendered four ways with three different values,
placeholders standing where the first frame's truth should be. None of
that was a design failure. Every screen had been *designed* correctly.
It was the accumulation of small, unowned gaps between the spec and
the state — each one locally reasonable, each one somebody else's
surface, none of them caught because the work that catches them is
invisible and unscheduled: verifying that the number on the screen is
the number in the engine, that the token in the file is the value in
the draw, that the frozen article is frozen in *code* and not only in
a plan document like this one.

So, concretely, the mistake this build is most likely to make: **each
element will be built, screen by screen, to its own seat file — and
the cross-screen invariants that make those files one system (Articles
1–7 above, one fact one rendering, the knurl gate, the arrival tests)
have no owner and no scheduled audit, so the first deadline will
produce the first local constant "just for this screen," and the
freeze will die silently in a diff nobody reads, months before anyone
notices in a render.** My one prescription, and it is not optional
flavour: before paint, name **one owner for the invariants** — a
person whose whole job is the grep, the count, and the screenshot
test — and run that audit at every round's end, against the code, not
the intentions. Plans do not drift. Implementations do, one reasonable
line at a time, and only a named pair of eyes has ever stopped it.

— Old Tom, Designers (Veteran). 29 years. The freeze is §1, the test
is §3, the warning is §5; if the build keeps those three, it may do
what it likes with the rest of this file.
