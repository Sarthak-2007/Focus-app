# IRIS — Colourist, Designers (coordinator: Sana)
## THE COLOUR APPLICATION MAP — Monolith, plan wave (no code)

Every element on every screen, named token + hex, day beside night. Source of truth:
`Color.kt` as locked by Sarthak's settled law. Where the locked palette cannot say
something a screen needs, §6 names it — I invent no hex anywhere in this document.

**How to read the tables:** `TOKEN-NAME #HEX`. Token names are the short semantic
names; the `Color.kt` identifier is given once in §0 and used nowhere else, so this
map survives a rename of the file but never a change of value.

---

## 0. The token register (the whole vocabulary — nothing else exists)

| Short name | Color.kt (day / night) | DAY | NIGHT | The one job it holds |
|---|---|---|---|---|
| GROUND | Background | `#E4DFDA` | `#171412` | the room every screen stands in |
| SURFACE | Surface | `#E4DFDA` | `#171412` | flat default — deliberately = GROUND, both modes |
| SHEET | SurfaceSheet | `#E4DFDA` | `#4A423C` | day: flat sheet (see gap G7) · night: the cap — topmost hardware fill |
| RECESS | SurfaceVariant | `#D8D1CB` | `#100E0C` | anything milled *in*: wells, tracks, fields. Night RECESS sits **below** ground |
| RAISED | SurfaceBright | `#FAF6F0` | `#362C27` | the one raised card step (timer card, nav capsule, key caps) |
| INK | Primary | `#252323` | — (see CREAM) | primary control fill + SHUT verb, day only |
| ON-INK | OnPrimary | `#FBF1EC` | — | words/glyphs sitting on INK |
| CREAM | NightColorPrimary | — | `#FFE4CF` | **TEXT AND GLYPHS ONLY. Never a fill, never a swatch, never a bar.** |
| ON-CREAM | NightColorOnPrimary | — | `#1B110D` | words on a cream *primary* surface — in this map that surface never occurs as a fill, so ON-CREAM has exactly one legal home: the OPEN verb's night word is NOT it (see §5). ON-CREAM is registered, and unused. That is correct, not an omission. |
| ACCENT | PrimaryContainer | `#F55314` | `#F47442` | COMMIT. A fill only, ≤1 per screen, §3 audits every placement |
| ON-ACCENT | OnPrimaryContainer | `#471602` | `#471602` | the word on the accent fill — identical both modes, by design |
| PILL | SecondaryContainer | `#615B5A` | `#4A423C` | nav selection fill. Night PILL = the cap (locked merger, gap G5) |
| ON-PILL | OnSecondaryContainer | `#F4EFEE` | `#FFE4CF` | nav selection word/glyph — night content is CREAM *as text on the cap*, which the cream law permits |
| GLYPH | OnSurface | `#4E4948` | `#EDE3D8` | primary icons, dial numerals, strokes that must read as drawn |
| MUTED | OnSurfaceVariant | `#5F5856` | `#776A60` | the quietest tier that still speaks: meta glyphs, disabled words |
| OUTLINE | Outline | `#615B5A` | `#807A7A` | strong hairlines, borders that carry structure |
| HAIRLINE | OutlineVariant (α 0x75) | `#615B5A @75%` | `#8F8888 @75%` | ruled lines, row separators, track lines |
| BLOCKBAR | TokenBlockBarSurface / NightBlockBarSurface | `#2E2A26` | `#161311` | the session bar — darkest object on its screen, both modes |
| TEXT | TextColorPrimary | `#2C2A29` | `#D2C6BB` (11:1) | body + hero numerals |
| TEXT-2 | TextColorSecondary | `#615B5A` | `#9C8E84` (5.8:1) | secondary text, meta lines |
| MUTED-TEXT | (rides MUTED) | `#5F5856` | `#776A60` (3.5:1) | captions that may be missed without loss — never state, never state words |
| AOD-GROUND | — | `#000000` | `#000000` | true black; AOD is a night condition in both modes (§1.6) |
| ~~ALERT~~ | TokenAlert / NightAlert | ~~`#D63E06`~~ | `#FF8A5C` *exception only, §4.3* | TokenAlert as a colour is **dead**. Day: zero placements. Night: two word-placements, argued in §4.3 and flagged as a deliberate exception |

**Standing rules the whole map obeys**

- **One hex, one job** — with two inherited collisions in the *locked source itself*
  (day `#615B5A`, night `#4A423C`) that I cannot repair without unlocking the palette.
  Both are named, bounded, and mitigated in §6 (G4, G5). I add no new collision
  except one flagged borrow (night PEEK, §5/G3).
- **Orange is a fill and a verb (COMMIT), never type.** No orange numeral, count,
  badge, label, hairline, or glyph anywhere in this map.
- **Cream is type, never paint** (night). Every night appearance of `#FFE4CF` below
  is a word or a glyph stroke sitting on a darker token.
- **Never green.** Every hex above already satisfies the zero-green law.
- **State is never colour alone.** Every colour change in this map is paired with
  position, weight, or a word (§4 is the general form of that law).

---

## 1. The maps, screen by screen

### 1.1 DECK (the arm screen)

| Element class | DAY | NIGHT |
|---|---|---|
| Ground | GROUND `#E4DFDA` | GROUND `#171412` |
| Panel (screen body) | SURFACE `#E4DFDA` — flat, no card | SURFACE `#171412` — flat |
| Recess | dial well, preset tiles, slide track bed: RECESS `#D8D1CB` | RECESS `#100E0C` — the well drops *below* the room; the dial sits in a real hole |
| Raised control | timer card, wheel hub plate, key caps: RAISED `#FAF6F0` | RAISED `#362C27`; operable caps that are *hardware* (hub plate) step up to SHEET/cap `#4A423C` |
| Cap fill | caps are RAISED with an OUTLINE skirt — day has no separate cap tone (gap G7's cousin: day hardware tops out at RAISED) | SHEET/cap `#4A423C` — topmost hardware, reserved for the hub plate + slide thumb housing |
| Hero numeral (duration) | TEXT `#2C2A29` on the RAISED timer card | TEXT `#D2C6BB` on RAISED `#362C27` |
| Dial numerals / ticks | GLYPH `#4E4948` in the RECESS well | GLYPH `#EDE3D8` in the RECESS well — brightest glyph on the darkest step; ticks are strokes, not fills |
| Body text | TEXT `#2C2A29` | TEXT `#D2C6BB` |
| Meta text (finishes-at, last-session line) | TEXT-2 `#615B5A` | TEXT-2 `#9C8E84` |
| Hairline | HAIRLINE `#615B5A @75%` | HAIRLINE `#8F8888 @75%` |
| State word (READY / ARMED / HELD) | TEXT `#2C2A29`, set in the state word's fixed slot under the dial — word + weight carry it, no colour change | TEXT `#D2C6BB`, same slot. At ARMED the word gains the BLOCKBAR chip behind it (see session bar below) — position changes, hue does not |
| Session preview bar | BLOCKBAR `#2E2A26`, word ON-INK `#FBF1EC` | BLOCKBAR `#161311`, word CREAM `#FFE4CF` (text on the bar ✓) |
| **THE accent** | **slide-to-arm thumb + its filled travel: ACCENT `#F55314`, label ON-ACCENT `#471602`** | **same element: ACCENT `#F47442`, label ON-ACCENT `#471602`** |

Zero orange anywhere else on Deck: not the wheel (GLYPH ticks only), not the
duration, not the count of blocks. The slide is the screen's single commit, so it
is the screen's single orange.

### 1.2 SHIELD (the loadout — apps, routines, websites, verb legend)

| Element class | DAY | NIGHT |
|---|---|---|
| Ground | GROUND `#E4DFDA` | GROUND `#171412` |
| Panel | SURFACE `#E4DFDA` flat; rows are ruled, not carded | SURFACE `#171412` flat |
| Recess | sled tracks, search field bed, website field bed: RECESS `#D8D1CB` | RECESS `#100E0C` |
| Raised control | sled thumbs in OPEN state (porcelain, §5): RAISED `#FAF6F0` | sled thumbs are hardware: SHEET/cap `#4A423C` family — see verb table §5 |
| Cap fill | — day sled caps top out at RAISED / INK per verb (§5) | SHEET/cap `#4A423C` |
| Hero numeral (e.g. permission count `1 OF 4`, if the hero is kept) | TEXT `#2C2A29` | TEXT `#D2C6BB` |
| Body text (app names, routine names) | TEXT `#2C2A29` | TEXT `#D2C6BB` |
| Meta text (counts, times, "granted" lines) | TEXT-2 `#615B5A` | TEXT-2 `#9C8E84` |
| Hairline | HAIRLINE between every row; OUTLINE `#615B5A` for the one strong rule above the verb legend | HAIRLINE; OUTLINE `#807A7A` above the legend |
| State word (OPEN / PEEK / SHUT; routine ON/OFF word) | the verb word in TEXT, printed beside its sled — the word is half the state (§5) | verb word in TEXT `#D2C6BB`; OFF word drops to MUTED-TEXT `#776A60` — the only tier-drop a state word is allowed, and it is paired with thumb position |
| **THE accent** | **NONE. Shield is a rested-user decision surface; nothing on it commits a session. ZERO orange — audited in §3.** | **NONE. ZERO.** |

### 1.3 TASKS (the queue + composer)

| Element class | DAY | NIGHT |
|---|---|---|
| Ground | GROUND `#E4DFDA` | GROUND `#171412` |
| Panel | SURFACE flat; task rows ruled by HAIRLINE | SURFACE flat |
| Recess | composer field bed, inline duration well, selected-row bed: RECESS `#D8D1CB` | RECESS `#100E0C` |
| Raised control | composer key-bank caps: RAISED `#FAF6F0`; ADD key: INK `#252323` fill, ON-INK `#FBF1EC` word (ADD is a primary control, not a commit — ink, not orange) | key caps: RAISED `#362C27`; ADD key: SHEET/cap `#4A423C` fill, CREAM `#FFE4CF` word (text on cap ✓) |
| Cap fill | key caps = RAISED (day hardware ceiling) | SHEET/cap `#4A423C` for the ADD key only; ordinary caps stay RAISED |
| Hero numeral (queue total / selected duration) | TEXT `#2C2A29` | TEXT `#D2C6BB` |
| Body text (task titles) | TEXT `#2C2A29`; a struck (done) title drops to TEXT-2 with the strike ruled in TEXT-2 — completion is a *line through type*, not a colour | TEXT `#D2C6BB`; struck title → TEXT-2 `#9C8E84`, strike in TEXT-2 |
| Meta text (sub-step counts, durations) | TEXT-2 `#615B5A` | TEXT-2 `#9C8E84` |
| Hairline | HAIRLINE row rules; OUTLINE under the composer | HAIRLINE; OUTLINE `#807A7A` under the composer |
| State word (DONE / NEXT / queued position word) | TEXT; DONE word is INK-weight, same slot at the row's end | TEXT `#D2C6BB` |
| **THE accent** | **START key on the selected/next task: ACCENT `#F55314`, word ON-ACCENT `#471602`** | **START key: ACCENT `#F47442`, word ON-ACCENT `#471602`** |

One orange, and it is the one that starts a session from the queue. ADD is ink/cap
deliberately: adding a task is paperwork, starting one is a commit — the palette
must not let those two acts share a colour.

### 1.4 RUNNING SESSION (the held screen)

| Element class | DAY | NIGHT |
|---|---|---|
| Ground | GROUND `#E4DFDA` around the bar | GROUND `#171412` |
| Panel | the BLOCKBAR is the panel: BLOCKBAR `#2E2A26`, full-bleed | BLOCKBAR `#161311` — near-black on near-black ground; it reads by its *words* and its edge rule (OUTLINE `#807A7A` top rule), not by a visible step. That near-invisibility is correct for a held phone at night |
| Recess | elapsed track carved in the bar: HAIRLINE `#615B5A @75%` ruled track (no darker token than BLOCKBAR exists by day — gap G8) | elapsed track: RECESS `#100E0C` against BLOCKBAR `#161311` — a whisper step, read by the fill front, not the track |
| Raised control | none operable mid-session (HOLD absolute — there is nothing to press, and the palette says so by offering no raised surface) | none. Same law |
| Cap fill | none | none |
| Hero numeral (remaining) | ON-INK `#FBF1EC` on BLOCKBAR | CREAM `#FFE4CF` on BLOCKBAR (text ✓ — this is cream's most important legal appearance in the app) |
| Body text (elapsed, task name on the bar) | ON-INK `#FBF1EC` | CREAM `#FFE4CF` for the task word; elapsed meta steps down to TEXT-2 `#9C8E84` |
| Meta text (below the bar: session facts, stated once) | TEXT-2 `#615B5A` on GROUND | TEXT-2 `#9C8E84` on GROUND |
| Hairline | HAIRLINE | HAIRLINE |
| State word | `HELD` — ON-INK on the bar, fixed slot, top-left of the bar | `HELD` — CREAM on the bar, same slot |
| Elapsed fill (the one moving colour) | RAISED `#FAF6F0` filling the track — porcelain advancing on near-black | RAISED `#362C27` filling the track — a warm step advancing on `#161311`; deliberately *not* cream (cream is never a fill, not even here) |
| **THE accent** | **NONE during the hold.** The commit already happened at the slide / START key; a held session spends no orange | **NONE.** Session-end word: see the §4.3 exception — the only non-form mark allowed anywhere near this screen |

### 1.5 AOD (always-on desk display)

AOD is a night condition in both modes: it always renders in the night register
on true black. There is no day AOD palette and none is invented.

| Element class | BOTH MODES |
|---|---|
| Ground | AOD-GROUND `#000000` |
| Panel / recess / raised / cap | none — AOD has no surfaces. Anything that looks like a surface on AOD is a bug |
| Hero numeral (remaining) | TEXT `#D2C6BB` (the 11:1 tier) — *not* cream: cream at AOD luminance is a lamp in a dark room, and cream's job is words on controls, not a glowing clock |
| Body text (task word, if shown) | TEXT-2 `#9C8E84` (5.8:1) |
| Meta text (elapsed) | TEXT-2 `#9C8E84`; nothing on AOD may use the 3.5:1 MUTED tier — at AOD sizes it falls under the legibility floor |
| Hairline | none, or a single HAIRLINE `#8F8888 @75%` rule if the layout needs a divider — prefer none |
| State word / armed dot | the armed dot is a GLYPH `#EDE3D8` filled circle — a glyph, not a surface; the word, if any, is TEXT-2 |
| **THE accent** | **NONE. Ever. An orange pixel field on a sleeping room's wall is the budget spent at the worst possible address** |
| Dimming / patrol | **not a colour** — implemented as alpha over `#000000` and position shift. The palette contains no AOD dim token (gap G2); brightness is a behaviour, and this map does not fake it with a hex |

### 1.6 AIRLOCK OVERLAY (the 2-minute peek)

| Element class | DAY | NIGHT |
|---|---|---|
| Scrim | GROUND `#E4DFDA @~80%` — an alpha borrow of the ground token; the palette has no scrim token (gap G1) | GROUND `#171412 @~80%` — same borrow |
| Sheet (panel) | SHEET `#E4DFDA` — flat against scrim by OUTLINE `#615B5A` frame rule + shadowless step; inner card RAISED `#FAF6F0` carries the content (gap G7 mitigation) | SHEET/cap `#4A423C` — the overlay is the topmost hardware in the app at night, and the cap is exactly that job |
| Recess | countdown track: RECESS `#D8D1CB` | RECESS `#100E0C` (on the cap sheet it reads as a milled groove) |
| Raised control | the single dismiss/return control: RAISED `#FAF6F0`, INK word | control word CREAM `#FFE4CF` on cap; no second fill |
| Hero numeral (countdown) | TEXT `#2C2A29` | TEXT `#D2C6BB` on cap (7+ :1 class — reads without strain at arm's length) |
| Body text (app name, "you are peeking") | TEXT | CREAM `#FFE4CF` for the app name (a word on the cap ✓), TEXT `#D2C6BB` for the rest |
| Meta text (what happens when it closes) | TEXT-2 `#615B5A` — stated once, plainly | TEXT-2 `#9C8E84` |
| Hairline | OUTLINE frame + HAIRLINE interior rules | OUTLINE `#807A7A` frame |
| State word | `PEEK` — TEXT, and the sheet's top rule is ruled in the PEEK verb tone `#615B5A` (§5): the one place the verb grey appears as a *band*, tying the overlay to its sled state | `PEEK` — TEXT `#D2C6BB`; top rule in MUTED `#776A60` (the night PEEK borrow, §5) |
| Countdown fill | INK `#252323` advancing in the RECESS track | GLYPH `#EDE3D8` advancing — a bright stroke-front, not a cream fill |
| **THE accent** | **NONE.** A peek is not a commit; orange here would teach the hand that orange can mean "temporary" | **NONE** |

### 1.7 EMERGENCY OVERLAY (the 60-second hold / the week's one break)

| Element class | DAY | NIGHT |
|---|---|---|
| Scrim | GROUND `@~80%` (borrow, G1) | GROUND `@~80%` (borrow, G1) |
| Sheet (panel) | RAISED `#FAF6F0` — the emergency sheet is the one day sheet that *lifts*: it must feel different in the hand from every flat day sheet, because it is the only sheet that costs something | SHEET/cap `#4A423C` |
| Recess | hold track: RECESS `#D8D1CB` ring/track bed | RECESS `#100E0C` groove on the cap |
| Raised control | the hold surface itself is the sheet; no separate button exists to mis-tap | same |
| Cap fill | — | cap is the sheet |
| Hero numeral (seconds held / remaining) | TEXT `#2C2A29` | TEXT `#D2C6BB` |
| Body text (what this costs, stated once: "your one break this week") | TEXT `#2C2A29` | TEXT `#D2C6BB`; the cost line may step up to CREAM `#FFE4CF` — a word on the cap, cream's legal register |
| Meta text | TEXT-2 `#615B5A` | TEXT-2 `#9C8E84` |
| Hairline | OUTLINE `#615B5A` — strong frame; this sheet gets the strong rule, not the hairline | OUTLINE `#807A7A` |
| State word | `HOLD` → `BREAK` — the word changes, the slot never moves, weight escalates (§4). **No alert hex by day** | `HOLD` → `BREAK` in TEXT; on completion only, the §4.3 exception word may appear (below) |
| Hold progress | a contracting ring stroked in GLYPH `#4E4948`, thickening as it closes — a stroke is a glyph, not a fill; the ring's *geometry* is the progress | ring stroked in GLYPH `#EDE3D8` on the cap; thickening + contraction carry it |
| **THE accent** | **NONE.** The emergency break is the anti-commit; painting it the commit colour would be a lie in the app's own verb code | **NONE as a fill.** The sole exception in the entire map: on *completion* of the hold, the state word `BREAK SPENT` may render in `#FF8A5C` — a word, never a fill, ≤2% of screen area. Argued and flagged in §4.3 |

### 1.8 CHROME (nameplate, bottom nav, global furniture)

| Element class | DAY | NIGHT |
|---|---|---|
| Ground behind chrome | GROUND `#E4DFDA` | GROUND `#171412` |
| Panel (nav capsule) | RAISED `#FAF6F0` — the capsule is the app's second raised card, same step as the timer card: one raised language everywhere | RAISED `#362C27` |
| Recess | — chrome has no recess; if a chrome well is ever drawn, it is RECESS and it is wrong — delete it | — same |
| Raised control | capsule = RAISED (above) | capsule = RAISED `#362C27`; the *selected* pill inside it steps to cap |
| Cap fill | — day selection is PILL, not a cap | selected pill: SHEET/cap `#4A423C` (= PILL at night, locked merger G5) |
| Hero numeral | chrome has no hero numeral. Counts in chrome are meta, never heroes | same |
| Body text (nameplate line) | TEXT `#2C2A29` | TEXT `#D2C6BB` |
| Meta text (counts, war-line figures) | TEXT-2 `#615B5A` | TEXT-2 `#9C8E84` |
| Hairline | HAIRLINE under the nameplate; capsule skirt OUTLINE | HAIRLINE; capsule skirt OUTLINE `#807A7A` |
| State word (selected destination) | selected pill: PILL `#615B5A` fill, ON-PILL `#F4EFEE` word; unselected: GLYPH `#4E4948` glyph + MUTED-TEXT word | selected pill: cap `#4A423C` fill, CREAM `#FFE4CF` word (text ✓); unselected: GLYPH `#EDE3D8` + MUTED-TEXT `#776A60` word |
| **THE accent** | **ZERO. Chrome never commits, so chrome never wears orange — no badge, no dot, no count, no selected state** | **ZERO** |

---

## 2. Reading the staircase off this map (a sanity check, not a rule)

Day depth reads: RECESS `#D8D1CB` → GROUND `#E4DFDA` → RAISED `#FAF6F0` up;
INK `#252323` / BLOCKBAR `#2E2A26` down. Night depth reads: RECESS `#100E0C` →
GROUND `#171412` → RAISED `#362C27` (≥1.35:1) → cap `#4A423C` (1.86:1) up;
BLOCKBAR `#161311` sits a breath under ground as the bar's own gravity.
Every adjacent pair used adjacently in §1 respects those steps; where a pair
*doesn't* step (day SHEET = GROUND; night BLOCKBAR ≈ GROUND), §1 says so at the
element and §6 logs it as a locked-palette fact, not a choice of mine.

---

## 3. Accent placement audit — the whole orange in the app

| Surface | Orange fills | The element | Day | Night |
|---|---|---|---|---|
| Deck | **1** | slide-to-arm thumb + filled travel | `#F55314` | `#F47442` |
| Shield | **0** | — Shield commits nothing | — | — |
| Tasks | **1** | START key (selected/next task only) | `#F55314` | `#F47442` |
| Running session | **0** | the commit is spent; the hold is orange-free | — | — |
| AOD | **0** | ever | — | — |
| Airlock overlay | **0** | a peek is not a commit | — | — |
| Emergency overlay | **0 fills** | §4.3's night word-exception is not a fill and is not the accent hex | — | (word only) |
| Chrome | **0** | no badge/dot/count/selection in orange, anywhere | — | — |

**Confirmation, stated once:** no screen in this map carries a second orange fill;
no orange appears as text, numeral, glyph, hairline, or badge on any screen;
ON-ACCENT `#471602` appears only *on* the two audited fills. Screens that can be
visible simultaneously with a fill (overlays over Deck) never add their own —
the airlock and emergency overlays are orange-free even while the Deck's slide
sits beneath their scrim, so the *visible* count also never exceeds one.

*Out of my assigned screen list, noted for Sana/Ren so it isn't lost: if the
Ledger returns as a surface, synthesis item 12 already budgets it exactly one
accent (the accession №). This map neither grants nor spends it.*

---

## 4. Alerts are form — the spec

`TokenAlert` dies as a colour. An alert in Monolith is a **change of form in a
fixed grammar**, identical in both modes, in this order of escalation:

### 4.1 The four moves (an alert is all four, never fewer)

1. **POSITION — it takes the state word's slot, or the slot above the hero.**
   Alerts never appear in meta position, never as a badge on chrome, never as a
   dot floating free. If a fact is alert-grade, it *evicts* whatever quiet word
   held the state slot on that screen. One alert per screen; a second alert-grade
   fact queues behind the first in the same slot (TRUTH once — the slot tells
   the most important true thing, not all of them).
2. **WEIGHT + SCALE — the word gets heavier and one tier larger.**
   The alert word renders in the boldest weight of the screen's text face at
   +1 type tier over the state word it replaced, tracking tightened. Nothing
   else on the screen changes size. (Sizes are Set's jurisdiction; the *rule* —
   alert = state word +1 tier, boldest weight — is mine.)
3. **WORD — a plain human sentence-fragment, present tense, no codes.**
   `BLOCKING OFF` · `PERMISSION LOST` · `COUNT WRONG — 11 OF 12`. Never `ERROR`,
   never an icon alone, never ALL-CAPS shouting *plus* colour *plus* motion —
   the word is the message; moves 1, 2 and 4 are only its envelope.
4. **SURFACE STEP — the owning element climbs one staircase step.**
   The row/panel that owns the alert steps **up one surface step** (RECESS →
   GROUND → RAISED by day; RECESS → GROUND → RAISED → cap by night) and its
   rule upgrades HAIRLINE → OUTLINE. The alert is *embossed*, not painted.
   When the condition clears, the element steps back down. This is why an
   alert can be read across a room, in peripheral vision, in greyscale, and
   by a tired user who cannot read yet: the screen's topography changed.

### 4.2 The alert register, per screen

| Screen | Alert-grade facts | Form it takes |
|---|---|---|
| Deck | blocking service dead at arm time | slide halts at its detent (form in the gesture itself); state slot: `BLOCKING OFF` (TEXT, +1 tier, boldest); timer card steps RAISED → (day: OUTLINE-framed RAISED; night: cap `#4A423C`) |
| Shield | a permission lost; sled proof fails (commanded SHUT, verified OPEN) | the owning row steps up one step, rule → OUTLINE, verb word replaced by the true word (`OPEN — NOT SHUT`) in TEXT at +1 weight. The sled keeps showing commanded position; the *word* tells the truth — intent and proof never share one rendering |
| Tasks | none alert-grade. A queue fact is never an alarm; Tasks has no alert register and inventing one would cheapen the others | — |
| Running session | blocking drops mid-session | `HELD` evicted by `BLOCKING OFF` in the bar's fixed slot, ON-INK/CREAM, +1 tier. The bar itself does not change colour — it is already the darkest object; there is nowhere darker to go, which is precisely why the word must do it |
| AOD | none. AOD never alerts. A sleeping room is not interrupted by a display | — |
| Airlock | peek expiring | not an alert — a schedule. Countdown numeral takes the final-minute form (seconds shown); the sheet steps down and closes. Expiry is expected, and expected things are not alarms |
| Emergency | the overlay *is* the alert register for the break | §4.3 |

### 4.3 The one exception — argued, flagged, bounded

**DELIBERATE EXCEPTION, flagged as Sarthak's law requires:** I keep wave-1 C4's
night exception, narrowed. At **night only**, in the **emergency/session-end
register only**, a **word** (never a fill, never a numeral, never a bar) may
render in `#FF8A5C`, in exactly two placements:

- Emergency overlay, on completion of the 60 s hold: `BREAK SPENT`.
- Running session, if a session ends *by emergency break* (never by normal
  completion): the end word `ENDED — BREAK SPENT`.

Why this survives the form law, argued plainly: every other alert in §4.2 is a
*fact about the machine* (blocking off, permission lost) and form carries those
perfectly. These two words are a fact about **an irreversible spend** — the
week's one break is gone and cannot be un-gone. Form says "pay attention";
this word must additionally say "this cannot be undone," at 3:30 AM, to a man
who will read exactly one word. It is bounded hard: night only (by day the
same words render in TEXT — daylight gets form alone, and that asymmetry is
intentional: day has contrast to spare, night has none), word only, ≤2% of
screen area, never on any other screen, never for any other fact. If Sarthak
strikes it, §4.1 carries both placements without loss — the map does not
depend on it. Day `TokenAlert #D63E06` has **zero** placements in this map
under all circumstances: it is close enough to the day accent to read as a
tired COMMIT, and a colour that can be misread as the commit verb is worse
than no colour.

---

## 5. The verb code — as it prints at the foot of Shield

Printed once, at the foot of Shield's list, above a strong OUTLINE rule, as four
swatch + word pairs in one ruled row. The swatch is a sled-thumb miniature —
same proportions as the real thumbs — so the legend is a picture of the thing
it decodes, not an abstract chip chart.

| Verb | Meaning | DAY swatch + word | NIGHT swatch + word |
|---|---|---|---|
| **COMMIT** | this act starts/seals a session | ACCENT `#F55314` fill, word `COMMIT` in ON-ACCENT `#471602` on the swatch; in the legend row the word beside the swatch is TEXT (the swatch alone carries the fill — the legend must not become a second orange *element* on Shield; the swatch is a specimen, counted as print, not as a live fill. §3's Shield ZERO refers to live controls; the legend swatch is the code's own printing. Flagged here so the audit stays honest.) | ACCENT `#F47442` fill, ON-ACCENT `#471602` word on swatch |
| **SHUT** | app is blocked | INK `#252323` fill, word ON-INK `#FBF1EC` | BLOCKBAR `#161311` fill with OUTLINE `#807A7A` edge (on a dark list the darkest fill needs its edge drawn), word CREAM `#FFE4CF` (text ✓). Night has no ink fill — cream is text-only — so SHUT's night body is the block bar tone: the darkest object does the shutting, in both modes, by the same logic |
| **PEEK** | app is peekable (airlock, 2 min) | `#615B5A` fill, word ON-PILL `#F4EFEE` — the tasked hex, verbatim | **borrow flagged:** MUTED `#776A60` fill, word GLYPH `#EDE3D8`. `#615B5A` at night would be the *lightest* fill on the screen (L* ≈ 39 against a staircase that tops out at the cap) — a half-state shouting louder than the topmost hardware is a broken ladder. The night PEEK takes the staircase's own mid-neutral instead (gap G3) |
| **OPEN** | app is free | RAISED `#FAF6F0` (porcelain) fill with OUTLINE edge, word TEXT `#2C2A29` | RAISED `#362C27` — night's porcelain step — fill with OUTLINE `#807A7A` edge, word TEXT `#D2C6BB`. Porcelain is a *step*, not a hex: the lightest neutral surface of the mode's staircase |

On the sleds themselves the same four fills apply, always paired with the
printed verb word beside the sled (§1.2) — colour is the third channel,
after position and word, never the only one.

---

## 6. Where the locked palettes cannot say what a screen needs

Named, not patched. No hex below is invented; each item is a limitation of the
locked source, with the workaround this map actually uses.

- **G1 — No scrim token.** Airlock and emergency overlays need a dimmed ground.
  The palette has no overlay/scrim value. *Workaround used:* GROUND at ~80%
  alpha, both modes — an alpha of a locked token, not a new colour. If alpha
  borrows are ever banned, the overlays have no legal ground and the palette,
  not the map, must change.
- **G2 — No AOD dim token.** AOD needs staged dimming (luminance ceiling,
  patrol). The palette offers only the night text tiers on `#000000`.
  *Workaround used:* dimming specified as alpha-over-black behaviour (§1.5).
  Brightness is unexpressible as a token; any seat who "specs" an AOD hex
  dimmer is inventing a colour.
- **G3 — Night has no free mid-neutral for PEEK.** Every night neutral already
  holds a job, and the tasked day PEEK hex `#615B5A` breaks the night ladder
  (it would out-light the cap). *Workaround used:* one flagged borrow —
  MUTED `#776A60` as the night PEEK **fill only**. It never meets itself:
  muted-as-text and peek-as-fill never occur on the same element, and the
  verb word always sits beside the fill. This is the map's single deliberate
  one-hex-two-jobs, entered here in the open.
- **G4 — Day `#615B5A` is triple-booked in the locked source** (PILL fill,
  OUTLINE, TEXT-2 — and the verb code adds PEEK as a fourth). I did not do
  this and cannot undo it without unlocking day. *Mitigation:* the jobs are
  separated by element class — fill, stroke, and text never coincide on one
  element, and PEEK sleds always carry their word — so the collision is
  legible in practice. It remains the day palette's weakest joint and the
  first thing a future unlock should fix.
- **G5 — Night `#4A423C` is the sheet, the cap, and the nav pill** in the
  locked source. *Treatment:* this map reads them as **one job family** —
  "topmost hardware fill" — of which sheet, cap, and selected pill are three
  addresses. Under that reading the merger is a system, not a collision.
  If Sana or Sarthak wants them separated, that is a palette unlock, not a
  mapping decision.
- **G6 — No disabled/unavailable tone.** A dead control cannot be greyed by
  token; MUTED-TEXT is the floor and it still reads as "quiet," not "dead."
  *Workaround used:* unavailable = RECESS step-down + word (`OFF`, `LOST`,
  `NOT GRANTED`) — form again, per §4. Anything subtler would need a token
  the palette doesn't have.
- **G7 — Day SHEET = GROUND = SURFACE (`#E4DFDA` ×3).** A day sheet cannot be
  distinguished from its ground by fill at all. *Workaround used:* day sheets
  separate by OUTLINE frame + a RAISED inner card (§1.6), and the emergency
  sheet — the one sheet that must feel different — is promoted to RAISED
  outright (§1.7). A day sheet that needs to read as *lifted paper* on fill
  alone is unexpressible in the locked day palette.
- **G8 — Nothing is darker than BLOCKBAR, and nothing steps inside it.**
  Tracks and grooves *within* the session bar have no token pair. *Workaround
  used:* day track = HAIRLINE at alpha on the bar; night track = RECESS
  `#100E0C` against BLOCKBAR `#161311` (a real but whisper-quiet step), with
  the elapsed *fill front* (RAISED advancing) carrying readability instead of
  the track.
- **G9 — No focus token.** Keyboard/composer focus (the Tasks field while the
  key bank is up) has no colour. *Workaround used:* focus = the field stepping
  RECESS → RAISED-surround + OUTLINE rule — a surface step, per §4's grammar.
  If Floor (accessibility) needs a stronger focus signal than a step, the
  palette cannot give it one; that is a palette conversation, not a mapping one.
- **G10 — ON-CREAM is homeless (by design).** `#1B110D` exists to sit on a
  cream fill; cream fills are banned; therefore ON-CREAM has no placement in
  this map. Recorded so nobody "fixes" the omission by giving cream a fill.
  The token stays registered and unused until/unless the cream law changes.

---

*— Iris, Colourist seat, Designers cohort. Plan only; no code touched.
Companion notes for Nacht (night PEEK borrow, §4.3 exception) sent by mail.*
