# MIKRO — Designers, Micro-interactions seat
## THE MICRO-STATE TABLE — every operable element × its states
### Plan wave · NO CODE · Coordinator: Sana · Chrome layout: Razor (I hold its behaviour)

Sarthak's order stands over this file: citations lifted, full freedom. What binds
is his settled law only, carried in verbatim and not relitigated:

- **Standards:** ARM ≤10s · HOLD absolute · TRUTH once.
- **State grammar:** everything answers on DOWN, in-frame · state words flip ON
  ARRIVAL, never mid-flight · ripple dead · one mover per input · ceremony on
  state change only.
- **Locked:** margin 20dp · radius 8dp keys / 16dp panels · label plate =
  Anonymous Pro Bold 10sp, +2.0 tracking, everywhere a plate is printed.
- **No-shadow law:** depth is ONE tonal step + a 1dp hairline / letterpress
  offset. Every wave-1 "shadow collapse" below is translated to a tone step.
  No elevation, no blur shadow, anywhere in this table.
- **Chrome:** band-switch nav (Razor §0) · war-line nameplate · zero orange in
  chrome except the Ø3dp armed lamp at FOCUS · drawer off the PARK detent in
  the dissolved-Guard branch (G-DISSOLVED).

**How to read this file.** §0 fixes the shared vocabulary (tones, the six states,
Kin's timing names). §§1–11 are the table — one section per operable element,
states as rows, exact visual delta per state. §12 proves the table survives all
three Ledger variants (V-TAB / V-DEMOTE / V-KILLED) × both Guard branches.
§13 lists every timing I need that Kin has not yet settled — flagged, not forked.

Tones are named, not hexed, except where Razor already pinned chrome. Night
values in brackets where they differ from day. The staircase: `ground` →
`recess` (one step BELOW ground) → `surface` (ground) → `bright` (one step
ABOVE ground) → `cap` (control fill, the highest a control may sit).
Ink ladder: `ink-100` (primary) · `ink-70` · `ink-55` · `ink-35` (hairline only,
never functional text). A "tone step" in any row below means exactly one rung
of one of these two ladders — never two, never a new colour.

---

## §0 — SHARED VOCABULARY

### 0.1 The six states (every element uses these names, no synonyms)

| State | Meaning | Universal rule |
|---|---|---|
| **REST** | Untouched, available. | Still. No idle motion, no pulse, no hint animation — ever. |
| **DOWN** | Finger is on it, this frame. | Acknowledged **same frame (≤16ms)**, in-frame, by translation or tone only. Never by scale except the two named exceptions (wheel dot §1, flat filter words §6). |
| **TRAVELLING** | The part is moving — under the finger (drag) or under power (throw / seat / return). | Position is the only state signal. Words, fills that mean state, and pips hold their OLD value for the whole travel. |
| **ARRIVED** | First frame within max(1% travel, 0.5dp) of the target stop. | The one frame where the word flips, the state fill lands, and the seat feel fires — together, never spread across frames. (Arrival-frame definition per Kin/Saft agreement, Kin's inbox 05:24.) |
| **REFUSED** | Input received, state cannot / will not take it (locked, spent, no-op, short drag, hold-without-throw). | Physical stillness or a damped return. No shake, no flash, no red, no toast, no grey-mush disable. The part either does not move or comes home and stops. |
| **ARMED** | The element's committed / session-live form (slide seated, session running, hold banked, lamp lit). | Entered only via ARRIVED. Exited only by its own named gesture — never by mirroring the entry gesture. |

### 0.2 Kin's timing grammar — the pointers every row uses

Kin (Motion seat) owns all curves and durations. I name them; I do not
redefine them. Source: Motion seat grammar (collapsed vocabulary) as amended
live on the wall / in Kin's inbox this wave.

| Pointer | Spec (Kin's) | Used by |
|---|---|---|
| **KIN-PRESS-IN** | 90ms, linear — press travel only | Every cap, key, hub, drawer handle (§§2, 6, 9) |
| **KIN-PRESS-OUT** | 180ms, CurveA `cubic-bezier(0.20,0,0,1)` | Every press release / cancel return |
| **KIN-THROW** | 240ms, CurveA / `SpringSpec(520, 0.90)`; distance-scaled `180 + 90 × stops crossed`, cap 340ms | Sleds, rockers, band, routine sleds (§§4–5) |
| **KIN-TRAVEL** | 360ms class, CurveA, distance-scaled | Full traverses, foreground routine fire |
| **KIN-SNAP** | 140ms, CurveA | Powered seats: slide commit seat, sheet snap points, overlay exits at 160ms |
| **KIN-WHEEL-SNAP** | `SpringSpec(280, 0.78)`, overshoot ≤2°, one pass, total settle ≤380ms | Wheel dot settle (§1) |
| **KIN-WHEEL-FLING** | Exponential decay — **CONTESTED: 2.4/s (Kin) vs 3.0/s (Saft)** — then KIN-WHEEL-SNAP | Wheel dot fling (§1). Mikro takes no side; §1's visual law holds under either. |
| **KIN-RETURN** | `SpringSpec(300, 1.00)`, critically damped, zero overshoot, ≈260ms from half travel | Slide short-drag return (§3), sheet fling-dismiss return (§11) |
| **KIN-NAV** | Razor §0.4 as built on Kin's spring: 180ms + 60ms per stop crossed (adjacent 240ms, 4-stop end-to-end 360ms), critically damped, zero overshoot; content crossfade 120ms starting on the seat frame | Nav cap (§7) |
| **KIN-TAB-IN** | Outgoing fade 120ms linear; incoming translate 14dp (direction = stop order) + fade, 240ms CurveA | Screen content behind the nav cap (§7) |
| **KIN-STEP** | 120ms, CurveA micro-tween per 1s step; digits hard-cut, never rolled / flipped / faded | Running clock, hold hero numeral (§10) |
| **KIN-DRAWER** | 280ms, CurveA, height expansion, content clipped, inner rows visible from frame one (no stagger) | Guard drawer (§9), routine-row editors (§5 note) |
| **KIN-SHEET** | **NOT YET IN KIN'S GRAMMAR — flagged, §13 F1.** Interim pointer used in §11: rise 240ms CurveA (= disarm-overlay class), exit 160ms | Completion sheet (§11) |

Haptic sizes (Ton owns the engine; named here only so the visual frame and the
feel frame are the same frame): **1ms = detent crossed · 4ms = contact / minor
commit · 8ms = throw arrived · 10ms = irreversible-feeling commit.** No row
below invents a fifth size.

### 0.3 Three cross-element laws the table enforces everywhere

1. **Skirt travel.** Every raised cap/key sits in a printed well. At REST the
   cap shows a **2dp skirt** (the well's inner wall, one tone step below the
   cap, visible as a 2dp band on the cap's lower edge). On DOWN the cap
   travels **+1dp** into the well and the skirt narrows to **1dp**; a fully
   sunk selected key (§6) travels **+2dp** and the skirt is **0dp** — flush
   with the well floor. Skirt width IS the press depth, readable in a still.
2. **Word-on-arrival.** A travelling part carries its OLD word at ink-55 for
   the whole travel. On the ARRIVED frame the word swaps (hard cut, no
   crossfade on the glyphs themselves) and returns to its seated ink. No word
   in this app ever flips at a progress midpoint.
3. **One mover.** While any part travels, nothing else on its screen moves in
   response to the same input. Readouts that are *computed* from the moving
   part (wheel total, hold remaining) re-strike in place on their own law
   (§1, §10) — they do not tween, and they are not a second mover: they are
   the same fact, restated at its one home.

---

## §1 — WHEEL DOT (Deck · `RotaryWheelView` active dot, Ø28dp on the rim)

The dot is the wheel's only grip point that admits touch. The disc never does.

| State | Exact visual delta |
|---|---|
| **REST** | Dot Ø28dp (r14) at the active detent angle, fill `cap` tone, single 3dp cream marker line across its centre. Rim, ticks, arc: unchanged. Readout shows the committed value only. |
| **DOWN** (on rim/hub annulus, outside hub core) | Same frame: dot scales **1.00 → 1.12** (r14 → r15.7dp) over 180ms CurveA **and** drops **one tone step** (`cap` → `bright`) — this pair replaces wave-1's shadow-collapse: bigger + one step DOWN reads as *pinched into the rim*, not lifted. No haptic yet. Disc, knurl, ticks: no change. |
| **TRAVELLING** (drag) | 1:1 angular coupling, zero lag filter beyond 1-frame velocity smoothing. Dot travels continuously between detents; **nothing else reacts between detents.** On each 5-min detent crossing: tick feel 1ms, readout + FINISHES re-strike same frame (hard cut; new glyphs at 88% ink settling to 100% over 120ms — letterpress, not a fade of the old glyph). 15-min crossings: 4ms feel, same visual law. FINISHES sits at ink-55 while the wheel is in motion (provisional) and returns to ink-100 on the seat frame. |
| **TRAVELLING** (fling) | Release velocity carries the dot under KIN-WHEEL-FLING; detent ticks keep firing on crossings (throttled ≥24ms apart, dropped never queued). Dot stays at scale 1.12 / `bright` for the whole fling — the grip look persists while the part is unseated, finger or no finger. |
| **ARRIVED** (settle) | KIN-WHEEL-SNAP to the nearest detent, ≤2° single-pass overshoot. On the seat frame, together: dot scale **1.12 → 1.00** and tone **`bright` → `cap`** over 180ms CurveA; landing feel 4ms; readout already true (it struck on the crossing) — no second update. Every rotation ends seated exactly on a detent; a dot resting between detents is a bug, not a state. |
| **REFUSED** (end stops, 5 / 180) | Hard clamp at the stop angle. First outward MOVE per drag: one 4ms end-stop knock, no visual change beyond the clamp itself. Further outward MOVEs: nothing moves, nothing fires. **Open flag (§13 F4):** Kin specced dead clamp; Saft specced rubber-band ×0.32 capped +7°. Mikro's requirement either way: overshoot past the stop, if any, is ≤7°, never oscillates, and the dot's scale/tone do NOT change at the stop — the wall is felt, not performed. |
| **REFUSED** (locked session) | Total inertness. DOWN produces no scale, no tone step, no knock. A locked instrument does not acknowledge touch; the silence is the state. |
| **ARMED** | n/a as a dot state — under session the wheel is replaced by the running track (existing behaviour, kept). The dot's last seated position is where the track's progress starts; no replay of the settle on session start. |

**FLOW boundary:** crossing into 5 (FLOW) is a detent like any other in travel,
but the detent IS the commitment there (the setter takes it on crossing), so on
that crossing frame the hub word swaps (§2) and the readout swaps to its FLOW
form — the table's one sanctioned mid-gesture word change, because there is no
later arrival for it to wait for.

---

## §2 — HUB (Deck · centre cap, drawn Ø ≈112dp, touch = drawn — no shrink factor)

A pushbutton in the middle of a dial. It cycles BLOCKS 1→4. It never rotates,
never carries a numeral other than the count, never previews.

| State | Exact visual delta |
|---|---|
| **REST** | Cap at `bright` fill, 1dp hairline, 2dp skirt visible. Word `BLOCKS` (plate, ink-70) over count (`2×` — count glyph in Anonymous Pro; the count's ONLY home is here as pips-or-glyph per the hub owner's final form — micro-behaviour below is identical for pips or glyph: the *mark* changes on arrival, defined next row). In FLOW: word `FLOW`, mark `∞`. |
| **DOWN** | Same frame: cap translates **+1dp** toward the panel (KIN-PRESS-IN, 90ms linear), skirt 2dp → 1dp, fill drops one tone step (`bright` → `surface`). Feel 4ms. **The count does NOT change on DOWN** — see ARRIVED. (This corrects wave-1's fire-on-DOWN: a pushbutton actuates at the bottom of its travel, and under the settled grammar the state word/mark flips on arrival — the press's arrival is its seat, next row.) Rotation input is dead while the hub is down. |
| **TRAVELLING** | The press travel itself: 1dp over 90ms. Nothing else moves. If the finger slides off the cap mid-press, the press cancels: cap returns over KIN-PRESS-OUT, no count change, no feel — the standard press-cancel, finally present on this control. |
| **ARRIVED** (UP inside bounds, press completed) | On the release frame: count cycles (4 wraps to 1 with identical feel — the wrap is not special), mark re-strikes in place (hard cut, 180ms ink settle as §1), cap returns +1dp → 0 over KIN-PRESS-OUT, skirt back to 2dp, fill back to `bright`. Feel 4ms at the cycle frame. Long-press: nothing extra — no reset, no FLOW shortcut, ever. |
| **REFUSED** | Press on the already-maximum count is NOT refused — it wraps (above). True refusals: hub DOWN during wheel drag (input already owned — no dip, no feel), hub in locked session (total inertness, §1 law). |
| **ARMED** | Under session the hub is not drawn (running track replaces the wheel assembly). No armed micro-state exists and none is invented. |

**Conflict flagged, not forked:** Improvers' hub plan (Nabe Rung 2) specced
fire-on-release for the hub — this table agrees and generalises it: *every*
cap in §§2/6 commits on completed press, state on arrival. Wave-1 Mikro
specced hub fire-on-DOWN; superseded by the settled word-on-arrival law.

---

## §3 — SLIDE CAP + GATE LINE (Deck · slide-to-arm, the one commitment gesture)

Thumb 76–80dp wide in its track; commit threshold **0.82** of displayed travel;
gate catch at **0.80**; warn tick at **0.68** (structure per Saft/Kin live
agreement — the numbers are Kin's to settle, the micro-states below hold at
any threshold in 0.80–0.82). Thumb-only grip: a DOWN anywhere else on the bar
is dead — no drag, no feel, no visual change, event not consumed.

| State | Exact visual delta |
|---|---|
| **REST** | Darkest bar in the app. Cap at left inset, fill = the screen's ONE orange (accent), one cream grip line 3dp at its leading edge, 2dp skirt below the cap. Label `SLIDE TO ARM SESSION` centred at ink-100; duration badge at right at ink-100 (badge carries a MODE word only — `FLOW` — or nothing; a timed number there is a second rendering of the total and is dead). Nothing pulses. |
| **DOWN** (on cap ±8dp slop) | Same frame: cap drops one tone step *within* the accent register (accent → accent-deep, the alert-adjacent step — the cap darkens under the finger, it never lightens), skirt 2dp → 1dp, lit top edge +1 step. Feel 4ms. Label/badge unchanged yet. Grip offset captured — the cap never re-centres under the finger, on DOWN or ever. |
| **TRAVELLING** (drag) | Cap follows 1:1 with Kin's resistance map applied to *displayed* fraction (gentle only — the finger always wins; a perfectly linear 1:1 reads as a swipe and must not ship). Trail behind cap: cream at α16 (the accent exists exactly once — on the cap). Label + badge alpha = `1 − smoothstep(0.55 → 0.85)` of displayed fraction — gone exactly at the seat zone, not before. 12 travel buckets, 1ms tick each on bucket entry, symmetric both directions. At **0.68**: warn tick (feel only, no visual change). At **0.80 gate**: gate feel (firm click) + **gate line event** — the cap's grip line thickens **3dp → 4dp** and the seat-zone floor (0.82 → 1.0) steps one tone DOWN for as long as displayed fraction ≥ 0.80. Crossing back below 0.80 (with 0.03 hysteresis): line back to 3dp, floor back, gate feel at ×0.6 — the gate must be *leavable* without lying about having been crossed. |
| **ARRIVED** (release ≥ 0.82 — commit) | Powered seat: cap drives the remaining travel over **140ms CurveA (KIN-SNAP)**, compressing **2dp** against the end stop on the seat frame (width −2dp for the dwell). **Hit-stop: 40ms freeze** of the cap at the seat (Saft's number, flagged §13 F2 — if Kin's merge keeps his 120ms dwell instead, the freeze is the first 40ms of it; the micro-law is: the cap is *visibly stopped dead* before anything else happens). Seal feel fires inside the freeze. Grip line flashes 3 → 5dp for the dwell, then the Deck reconfigures in Kin's D3 order (label swap 120ms → wheel drain 240ms CurveA → running values strike at t≈270ms), total ≤510ms. The callback fires only after the cap has visually arrived — commitment is reported by the part reaching the stop, never by the finger leaving the glass. |
| **REFUSED** (release < 0.82) | KIN-RETURN: critically damped return, zero overshoot, ≈260ms. No bucket ticks on the way home (the machine does not applaud a retreat); one dull 4ms thud at the re-seat frame; label/badge alpha restores by the same smoothstep law in reverse — words fully back only when the cap is home. No state change, no callback, no partial credit, no record. Re-press mid-return cancels the animator and re-enters DOWN from the cap's *current* position with a fresh grip offset. Velocity-throw commit (release >2,200dp/s at displayed ≥0.70) routes to ARRIVED, never to a cheaper gate. |
| **ARMED** (running / grace) | The same bar, sealed: cap drawn seated at the RIGHT in control fill (never cream-filled at night), grip line at its trailing edge, centre line `ARMED · {remaining}` struck once and then only on KIN-STEP boundaries. First 10s (grace): cap in accent at the right, mirrored seat pocket printed at the left labelled `ABORT`, label `SLIDE BACK TO ABORT` — reverse drag obeys this same table mirrored (gate 0.82 of reverse travel, abort seal on arrival). After grace: bar inert as a drag; a tap opens the disarm overlay (§10's home). No slide-to-disarm ever exists outside grace. |

---

## §4 — SLEDS (Guard · binary sleds: Shorts & Reels, Browser Link, Sound Effects)

Two stops only. Throw zone = the track (76 × 34dp) + 8dp slop; the row's text
area is inert — a switch thrown by tapping its label is a list item in costume.

| State | Exact visual delta |
|---|---|
| **REST** | Cap seated at its stop in the recessed track (track floor = `recess`). Cap fill carries the tier: ON = control fill, OFF = recess fill + 1dp inner outline. State word printed at the track's margin (never inside the cap — position = power, words at margins), ink-100 seated. Armed pip beside the track (ON only): a 3dp ink dot — position's echo, not a second state channel; it follows ARRIVAL only. |
| **DOWN** | Same frame: cap drops one tone step, skirt 2dp → 1dp. No feel yet, no state change. DOWN outside the track: nothing. A held finger (>600ms, no drag) is NOT a throw: on release the dip simply returns over KIN-PRESS-OUT, no throw, no feel. |
| **TRAVELLING** (tap-throw) | Cap travels under KIN-THROW (240ms). Word holds OLD word at ink-55 for the whole travel; pip holds old state; cap fill lerps by fractional position between the two stop fills (colour and position are the same number on every frame). |
| **TRAVELLING** (drag) | Cap follows 1:1 inside travel, clamped at both stops (hard clamp, no rubber). 1ms tick as it crosses the midpoint detent. On release: snap to nearest stop under KIN-THROW scaled to remaining distance; a release exactly at midpoint returns to the stop it came from (the machine defaults to no-change). |
| **ARRIVED** | Seat frame, together: word flips (hard cut, ink-55 → ink-100), pip appears/vanishes, cap fill lands on the stop fill, skirt restores to 2dp, throw feel 8ms (engaging sharper / disengaging duller — direction is the meaning). A new DOWN mid-throw cancels the animator and takes the cap from its current position — no queueing, no double-throws. |
| **REFUSED** | No-op tap (tap resolving to the current stop): total silence — no feel, no sound, no callback, cap does not re-throw. While a session runs (HOLD): sleds are inert — DOWN gives no dip; the sled stays drawn full-size at its state. |
| **ARMED** | n/a — a sled's ON *is* its committed state and is fully described by REST-at-ON + the pip. No extra armed styling is invented. |

---

## §5 — ROCKERS (Shield app rows SHUT / PEEK / OPEN · Loadout band STUDY / DEEP / SLEEP · Routine ON/OFF mini-sled)

Three stops, one grammar. App-row geometry: sled 168 × 36dp, cap 54 × 30dp,
pad 3dp. Band cap carries one constant cream marker line (`#FBF1EC` both modes,
20dp long — a control mark, never a text tone). Routine rows use the §4 binary
form shrunk to a mini-sled + margin word; identical states, smaller travel.

| State | Exact visual delta |
|---|---|
| **REST** | Cap seated on its stop; floor notches (10 × 2dp, one per stop) printed on the track floor; cap's seat line (18 × 2.5dp) sits exactly over the seated notch at 100% presence. Stop fills descend by material: SHUT = control fill (solid) → PEEK = one step below control fill → OPEN = recess fill + 1dp outline (hollow) — identical logic day and night, grayscale-true; night PEEK must NOT reuse SHUT's `#4A423C` (the shipped collision — PEEK night = `bright` `#362C27`, OPEN night = `recess` `#100E0C`). Margin words for all three stops printed at ink-55; the seated stop's word at ink-100. **First bind / rebind: cap is placed, never thrown** — a row that animates on load is motion that means nothing. |
| **DOWN** | Same frame, on the cap/track zone only: cap one tone step down, skirt 2dp → 1dp, seat line hides (travel is about to be visible ahead of and behind the cap). Silent (no sound on grab in a 12-row list); grab feel 1ms. The app-name area is inert — the wave-1 name-tap cycle (including the OPEN→SHUT wrap on the least deliberate gesture on the row) is deleted. |
| **TRAVELLING** (tap-throw) | KIN-THROW, distance-scaled: adjacent 240ms, two-stop 320ms — and a two-stop throw is **ratcheted**: two chained 180ms segments + 24ms dwell at the middle stop, pass tick at the dwell. Cap fill = continuous lerp between adjacent stop fills by fractional position; each stop word's ink = lerp by proximity to the cap. Seat line at 35% presence in transit. |
| **TRAVELLING** (drag) | DOWN grabs with offset; MOVE 1:1, clamped [stop 1, stop 3]; crossing the middle stop's centre fires the pass tick (1ms, direction-aware, never on landing). Release snaps to nearest stop (180ms CurveA for the short snap — KIN-THROW scaled); tie returns to origin stop. |
| **ARRIVED** | Seat frame, together: seat line snaps to 100% exactly over the notch, seated word to ink-100 (others to ink-55), cap fill lands, skirt restores, seat feel 8ms pitched by tier (SHUT lowest/heaviest → OPEN highest/lightest — position has a pitch). Row's status pip and tier tone follow on this frame and not before: while the rocker travels, the row still reads as its previous tier everywhere except cap position. **Programmatic arrival** (a routine applying a loadout): same travel, same arrival law — type never flips at t=0 while the cap glides behind it (the shipped band bug; the setter must animate through position like a finger did). |
| **REFUSED** | No-op tap on the seated stop: silence (no feel / sound / callback). Mid-session (HOLD): app rockers inert, no dip; routine mini-sleds inert likewise — routines are edited by the rested user, never mid-session. |
| **ARMED** | A routine's mini-sled at ON during its window: cap seated ON, margin word `WINDOW OPEN — ENDS {time}` in ink — never `LIVE`, never `RUNNING`. A window is not a session and the row does not pretend otherwise. |

*Routine-row editor (disclosure, for completeness):* expand 420ms / collapse
260ms on Kin's drawer pair, content readable only in the last 180ms of expand,
hairline fixed at the row's final bottom edge throughout, one row open at a
time. The ON/OFF sled inside the row header keeps §4/§5 grammar with its own
hit area — header taps never toggle, sled taps never expand.

---

## §6 — COMPOSER KEYS + ADD (Tasks · duration keys, FLOW key, filter words, ADD)

One dip vocabulary for every word that acts. Keys are radius 8dp, in printed
wells (§0.3 skirt law). Selection is ink fill; a key never shows its selected
fill before its press completes.

| Element / State | Exact visual delta |
|---|---|
| **Keys — REST** | Unselected: `surface` fill, 2dp skirt, word ink-70. Selected: ink fill, word in cap-content tone, **sunk +2dp, skirt 0dp** — selection reads as a key held down by the machine, not a key painted. |
| **Keys — DOWN** | Same frame: translate **+1dp** (KIN-PRESS-IN 90ms linear), skirt 2dp → 1dp, fill one tone step toward the panel. Text does not move, scale, or recolour. No feel on DOWN — keys are cheap and frequent; the commit speaks. |
| **Keys — ARRIVED** (UP inside) | Feel 4ms; cap returns over KIN-PRESS-OUT **to its new rest depth**: newly selected key settles at +2dp sunk / skirt 0; the previously selected key rises +2dp → 0 / skirt 2dp over the same 180ms — the rise and the sink are one input's two halves, the table's one sanctioned two-part move (they are a single radio mechanism, not two movers). State (duration, preset, loadout) lands on this frame. Re-pressing the selected key: full dip + feel, changes nothing — radio keys do not toggle off. |
| **Keys — REFUSED / cancel** | Finger slides off / UP outside: return over KIN-PRESS-OUT, no feel, no commit. Duration keys during a running session: inert, no dip (HOLD). |
| **FLOW key (Deck footer)** | Same grammar, two extra laws. (1) Its press commits on arrival like any key — the doubled ViewModel voicing (latch + wheel tick for an untouched wheel) is a Ton/Mikro joint kill: one press, one feel (4ms latch family). (2) FLOW must be *leavable*: pressing FLOW when already in FLOW is a no-op (silence, per §4 law) — leaving FLOW happens by pressing a duration key / turning the wheel past 10, which un-sunks FLOW on that input's arrival frame. A one-way key that eats the dial and blocks settings on the way through is not a micro-state problem, it is a trap; flagged to Plan/Latch in §13 F5 — the micro-table's requirement is only: *whatever exits FLOW, FLOW's cap rises on that exit's arrival frame.* |
| **Filter words (flat text, no cap body)** | The one scale exception besides the wheel dot: DOWN = scale **0.97** + ink 100 → 80 over 90ms; commit on UP (feel 4ms), underline + ink-100 land on the commit frame only; restore 180ms CurveA. A filter word never grows a background pill on press — words stay words. |
| **ADD — REST** | Ink key (the composer's one ink key; NOT orange — Tasks' one commit fill belongs to START). Word `ADD`, 2dp skirt. Docked composer keeps its duration/loadout between adds: ADD appends and the field clears **without the keyboard dropping** — batch entry is type → ADD → type. |
| **ADD — DOWN / ARRIVED** | DOWN: +1dp, skirt → 1dp, one tone step (as keys). ARRIVED (UP inside): feel 4ms pitched as a punch (Ton's family), the new row strikes in at the queue position **instantly** (lists arrive still; only *changes* the user just made may move — the appended row fades its ink 88% → 100% over 120ms, letterpress, no height animation, no cascade into neighbours). Composer field clears on the same frame. |
| **ADD — REFUSED (empty field)** | ADD does not dip, does not tick, renders at ink-35 on its well — inert, not greyed into a different object. Typing the first character restores full ADD on that keystroke's frame (no animation). |
| **Sub-step / task verbs** (DONE / START / DEL, in-row) | Flat-text press as filter words. Sub-step strike **draws left-to-right over 180ms CurveA** on commit, text dims one tone step over the same 180ms; un-strike reverses identically. Final sub-step escalates everything *except* the strike on the parent title (steps-done ≠ task-done — two facts): 110ms freeze, heavier wipe, title block recesses one tone step, hairline closes left→right. The title strikes only on DONE itself. DONE's landing feel scales with the task's minutes (Ton). |

---

## §7 — NAV CAP + STOPS + ARMED LAMP (chrome · Razor §0.4 layout, Mikro behaviour)

Rail 52dp, recess fill, track inset 4dp → 342 × 44dp usable; cap 44dp tall,
radius 8dp, width = segment − 8dp. Geometry per variant is Razor's formula
(§12 below repeats the worked numbers because the micro-states must land on
them). Words are printed ON THE RECESS; the cap travels *under* the selected
word and re-inks it.

| State | Exact visual delta |
|---|---|
| **REST** | Cap seated under the current stop's word: that word in cap-content tone at ink-100; all other stop words on the recess at ink-55. Cap at `cap` fill, 2dp skirt-equivalent (its lower 2dp band one step below cap fill — the rail has no wells, the skirt is drawn as the cap's own bottom band). Rail's top edge: 1dp letterpress light-line. Nothing else in chrome moves, ever. |
| **DOWN** (on any stop word / on the cap) | Same frame: the pressed word inks **ink-55 → ink-100** (if unselected) — the press is answered by the *word*, in-frame, before the cap moves a pixel. The cap does NOT depart on DOWN. No scale, no ripple. If the pressed stop is already selected: word already ink-100, so the answer is the cap's bottom band stepping one tone down for the press duration (the chrome equivalent of a dip — the cap cannot translate inside a flush rail without breaking the recess line). |
| **TRAVELLING** (release-or-drag) | Cap departs on release (tap) or follows the finger 1:1 (drag, grabbable, detent tick 1ms at each stop centre crossed). Travel under KIN-NAV: critically damped, **zero overshoot past the target stop** — a cap that bounces past a printed word has lied about where it stopped. While travelling: the departing word stays ink-100 until the cap has fully left its segment; the target word stays ink-55 on the recess until arrival. Content behind: KIN-TAB-IN starts **on the seat frame**, never mid-flight — for the whole travel the old screen is simply still there. A drag ending between stops seats to the nearest stop; the cap never parks between words. |
| **ARRIVED** | Seat frame, together: target word re-inks to cap-content tone ink-100, origin word drops to ink-55 **on the recess** (it is now printed on the rail again), content crossfade/translate (KIN-TAB-IN) begins, feel 4ms (navigation is not a throw — the 8ms size is never used here). |
| **REFUSED** | Mid-session (HOLD): stops accept no press — DOWN gives no ink change, cap does not depart; cap parked at FOCUS, lamp lit (§0.7 Razor). The refusal is total stillness: chrome offers no mid-session decisions and does not flinch when asked. Dragging the cap toward PARK (§8) during session: PARK is inert, same stillness. |
| **ARMED LAMP** | Ø3dp orange (day `#F55314` / night `#F47442`), centred 6dp above the FOCUS word's cap-top edge — i.e. it rides **on the cap's segment geometry, not on the cap**: it is printed in chrome at the FOCUS stop position and is visible in every nav position while a session is armed/running. States: **off** (no session — the dot is absent, not dimmed; absence renders as absence) → **on** at the arm commit's ARRIVED frame (§3), steady, **never blinking, never pulsing** → **off** at session end's arrival frame. It carries state (armed), never a number, never a count. It is the ONLY orange in chrome in all six variant×branch combinations. |

---

## §8 — PARK DETENT (G-DISSOLVED only · the rail's end position, not a stop)

PARK is a **position, not a destination**: a detent zone reserved inside the
track at the rail's end (Razor §3.1 subtracts it before segmenting — 3-stop +
PARK: segment 73.5dp class; exact numbers are Razor's delta). No word is
printed at PARK. The drawer (§9) is what lives there.

| State | Exact visual delta |
|---|---|
| **REST** | The PARK zone reads as rail: recess fill, a single 1dp end-wall line at the track's end, and a **6dp grip notch** (short vertical line, ink-35) centred in the zone — the only mark. It does not look like a stop: no word, no segment rhythm. |
| **DOWN / drag toward PARK** | The cap, dragged past the last stop, travels into the PARK zone under the same 1:1 law but meets **rising resistance in the last 12dp** (Kin's resistance map, gentle class) and the grip notch steps ink-35 → ink-70 as the cap crosses the zone's inner edge — the hand feels a detent *approach*, not another stop. Detent tick at zone entry: 1ms, lower pitch family than stop ticks (it is not a stop and must not sound like one — Ton to confirm pitch, §13 F6). |
| **ARRIVED at PARK** | Cap seats against the end-wall with a soft detent (feel 4ms, duller than a stop seat). On this frame the drawer (§9) begins its rise — PARK's arrival IS the drawer's invocation; there is no second tap, no handle tap required. The cap stays parked in the zone for as long as the drawer is open (chrome state: drawer open). |
| **Leaving PARK** | Any stop press / cap drag out: drawer dismisses first (§9 exit law), cap departs on the same input — one gesture, drawer exit and cap travel may overlap (they are one mechanism: the cap leaving its detent is what lets the sheet fall). Cap seats on the chosen stop under §7 law. |
| **REFUSED** | Mid-session: PARK inert (§7 REFUSED). In G-DESTINATION branch and in V-anything without G-DISSOLVED: **PARK does not exist** — the track segments fill the whole 342dp and there is no zone, no notch, no end-wall beyond the rail's own radius. Build-time absence, never a runtime greyed zone. |

---

## §9 — DRAWER HANDLE (G-DISSOLVED · Guard drawer off PARK)

The drawer is a sheet that rises (ground one step ABOVE app ground — a sheet
rises, a rail recesses). It carries Guard's bulk: permissions, sound, theme
*report* (never a theme control). Its handle is the only part of it that
exists when it is closed — and when PARK exists, the handle's job is mostly
done by the cap (§8); the handle below is the drawer's own edge, operable
whenever the drawer is open or peeking.

| State | Exact visual delta |
|---|---|
| **REST (closed)** | No separate handle bar floats over content. Closed state = PARK zone at rest (§8) + nothing. If the build keeps a visible drawer edge at the screen foot in this branch, it is a 1dp hairline + the 6dp grip notch only — never a pill, never a `GUARD` chip (a second door labelled like a destination would fork Razor's naming law). |
| **DOWN** (on the open drawer's top edge / handle zone, 24dp tall grab strip) | Same frame: handle strip's centre grip line (24 × 3dp, ink-55) steps to ink-100 and widens 24 → 28dp; sheet drops one tone step at its top 8dp band only (the sheet *takes weight* where the hand is). No sheet movement yet. |
| **TRAVELLING** | Drag: sheet follows 1:1 vertically, clipped, inner rows visible from frame one (KIN-DRAWER law — no stagger, no fade-in of rows). Invocation travel (from PARK arrival): sheet rises under KIN-DRAWER 280ms CurveA to its detent height. The sheet has exactly **two rest heights: closed and open** — no half-detents; a drag released above 40% of open-height seats open, below seats closed, both under KIN-SNAP scaled. |
| **ARRIVED** | Open seat frame: grip line back to 24dp / ink-70, top band tone restores, feel 4ms. The drawer's rows are operable only from this frame — a row tapped mid-rise is not answered (its DOWN law starts at ARRIVED). |
| **REFUSED / dismissal** | Dismissal gestures (exactly these): drag down past 40% → seat closed; tap on the scrim/content above the sheet → sheet seats closed over 160ms exit; cap leaving PARK (§8). All three are the same micro-event: sheet travels down under KIN-RETURN if dragged, KIN-SNAP if tapped. On the closed seat frame: cap is freed (it may already be travelling — §8), PARK notch returns to ink-35. Mid-session: drawer cannot be invoked at all (§8 REFUSED) — there is no closed-drawer tease state. |
| **ARMED** | n/a. While armed, the drawer is unreachable by law, not by hiding: PARK is inert and the sheet stays closed behind a running session. |

---

## §10 — HOLD TARGET (session overlay · the banked 60s emergency break)

The full-width hold bar (whole bar is the target; height 68dp in the overlay,
target width = content width — operable eyes-off, no aimed round pad). The
banked model: `banked ∈ {0, 15, 30, 45}`, `held` = seconds above banked in the
current grip, total = banked + held; on slip, `held` drains, `banked` never
moves; at 60 the week's break is spent. **The bank must be readable with the
numeral covered.** That is this section's whole design problem, solved by
chambers + notches + words, three channels that agree.

**The chambered bar.** Interior = **four chambers**, each 15s wide, separated
by 3dp vertical walls (cream α55 day / α45 night). Chamber floor = lane
(recess). Three fill languages, mutually exclusive per chamber:

- **BANKED chamber:** solid control fill (`#4A423C` night / `#615B5A` day) —
  stored metal, heavier and quieter than live progress.
- **LIVE chamber (currently filling):** accent fill at α180 — the bar's only
  orange, and it is *at risk* by definition.
- **DRAINING fill:** diagonal hatch (4dp stripes, accent α180 / transparent)
  collapsing right-to-left — the only hatched thing in the app; it cannot be
  mistaken for progress.

| Moment | Exact visual delta (all changes on the stated frame, no tween unless named) |
|---|---|
| **REST (no grip, banked = b)** | Chambers 1..b/15 solid-banked; remaining chambers at lane floor. Under the bar, the **bank rail**: four notch slots printed on the plate line; banked notches filled (ink-100 ticks, 10 × 3dp), unbanked notches as outlines. Margin word at the bar's left end: `BANKED {b} OF 60 · HOLD TO CONTINUE` — or at b = 0, `HOLD 60S · 1 BREAK THIS WEEK`. Hero remaining numeral (Set's H-tier) present but *secondary by design*: everything below must work if it is covered. |
| **DOWN** | Same frame: the bar's live edge advances **2dp** instantly (contact acknowledged before the first second ticks) and the current chamber's floor steps one tone down under the finger's chamber. Pitch-rising per-second detent ticks begin (1L route, pitch climbs 420 → 660Hz *within* each 15s segment and resets at each bank — the ear hears climbing-toward-the-next-bolt). |
| **TRAVELLING (holding, inside a segment)** | Live fill grows **stepped, not smooth**: the fill edge advances in 1-second steps on KIN-STEP (each step a 120ms micro-tween of the edge only; the hero numeral hard-cuts per second). Continuous smooth fill would make it a game; stepped fill makes it a fuse. Every 5th second: firmer tick feel. No word changes inside a segment. |
| **BANK at 15 / 30 / 45 — ARRIVED (the bank event)** | On the crossing frame, four things land together: **(1) NOTCH:** the chamber's right wall thickens 3dp → 5dp and the bank rail's next notch fills solid (hard cut). **(2) TONE:** the just-completed chamber's fill converts accent-α180 → solid banked control fill (hard cut — banked metal does not fade in; it *is*, the frame it is earned). **(3) WORD:** the margin word re-strikes: `BANKED 15` → `BANKED 30` → `BANKED 45` (Anonymous Pro Bold, hard cut, 120ms ink settle). **(4) FEEL:** escalating bolt — 15: latch weight · 30: double seat, lower pitch · 45: triple seat, lowest pitch (waveforms are Ton's/Zug-II's B2 table; the visual law here only requires the feel and the three visuals share the frame). Read with the numeral covered: solid chambers = safe, one orange chamber = at risk, notch count = banks. |
| **SLIP (release / contact loss)** | Grace first: contact-loss gaps <150ms freeze accrual AND drain (sensor dropout is not a decision). Deliberate release: **800ms no-drain grace** (§13 F3 — Rosa 800 vs Saft 600, merge decides once; the micro-law is: a felt pause exists and drain never starts on the release frame), then `held` drains at **2 banked-seconds per real second** under the hatch rendering, descending tick stream (Ton). Drain **stops dead at the bank floor**: catch feel (latch at ×0.7) + the live chamber snaps to empty lane (hard cut) + margin word re-strikes to the banked line. If banked = 0, drain ends at zero with the latch-release feel and the bar returns to REST — nothing caught, nothing was banked. Re-grab mid-drain: drain cuts within one frame, one firm re-seat click, 250ms re-seat pause (no accrual), then accrual resumes. Bank persists across overlay dismiss/reopen within the same disarm attempt; session end or break completion clears it. |
| **ARRIVED at 60 (spend)** | All four chambers render banked-style for **180ms** (four solid chambers, four filled notches, word `BANKED 60`), then the spend event fires (disarm seal, arming=false family) and the overlay exits at 160ms. Banking alone never spends the break — only completion does. The spend is the week's one break; the bar's post-spend form is REFUSED below. |
| **REFUSED (break already spent this week)** | The bar stays drawn, full-size, in place: chambers show the spent week's final bank state at ink-55 fills, margin word `NO BREAK LEFT THIS WEEK`, hero numeral absent (absence, not `0`). DOWN on it: **nothing** — no dip, no fill advance, no feel. A spent valve does not acknowledge touch; it is a printed fact, not a disabled button. |
| **REFUSED (mid-session non-emergency)** | The hold target does not exist on any running surface except via its overlay invocation (sealed-bar tap, §3 ARMED). HOLD absolute: no other surface grows a hold, a peek, or a shortcut. |

---

## §11 — COMPLETION SHEET in V-DEMOTE (the record's demoted form (i) — Mikro owns this behaviour)

Plan's verdict (Designers) demotes the Ledger to two forms; the Receipt *is*
the completion sheet. Razor's chrome consequence for this form is **zero** —
no button, no corner, no stop invokes it. It is **presented by the
session-end event**, over FOCUS, with the nav cap already home. Everything
about how it arrives, sits, and leaves is mine, specced here.

**Content law (Plan §3.2, honoured, not expanded):** one line of fact in human
language — `HELD 50 MIN · STUDY · 3 DEFLECTED`, or for a broken session the
same grammar unsoftened: `BROKEN AT 31 MIN · HOLD SPENT`. 15sp Atkinson body.
No hero number, no accession number, no seal, no verification language. It
keeps no copy of itself; its facts pass into RECORD's aggregates (Shield foot)
and the sheet is gone. TRUTH once means once.

| State | Exact visual delta |
|---|---|
| **INVOCATION (arrival)** | Trigger: session-end event only. Precondition rendered truthfully: the Deck has already unlocked (wheel re-accents, §3 D3 order completes) and the nav cap is seated at FOCUS **before** the sheet moves — the sheet never races the cap. Sheet rises from the screen foot over FOCUS: ground = drawer ground (one step ABOVE app ground — a sheet rises), radius 16dp top corners, 0dp bottom (it is docked, not floating), height = content height (one line + its plate line + 32dp of air; ≈148dp at reference — it never covers the readout). Motion: **KIN-SHEET interim** — rise 240ms CurveA with an 8dp travel settle (overlay class), scrim behind at ink α40 fading in over the same 240ms. No bounce, no overshoot: a receipt does not spring. On the seat frame: one low thock feel (completion family — stillness 400ms after session end, then the sheet, then the thock is *inside* the seat frame, per the ritual seat's persisted-transition law: fired once, keyed by sessionId, unreplayable even by process death). |
| **REST (presented)** | Still. The fact line at ink-100; beneath it the plate line `MONOLITH KEEPS NO COPY · YOUR STUDY RECORD LIVES IN YPT` (plate type, ink-55) — the sheet states its own ephemerality on its face. No buttons on the sheet. No close ×. No actions at all: there is nothing to do with a receipt except read it and let it go. The Deck behind the scrim is inert (sheet is modal for touch, not for sight — the readout stays visible above the sheet's top edge). |
| **DOWN (its one gesture)** | The sheet's **one gesture is a downward drag — or a single tap anywhere on the sheet.** DOWN on the sheet: sheet drops one tone step at its top band (as §9 handle law) and follows the finger 1:1 if dragged. A tap (DOWN+UP, <8dp movement) is the same gesture compressed: it dismisses. There is no other gesture: no swipe-up (nothing is above it), no button, no long-press, no second tap target. |
| **DISMISSAL (travelling + arrived)** | Drag dismissal: release above 40% of sheet height → seats back open under KIN-SNAP (the §9 two-height law); below 40% → continues down under KIN-RETURN if dragged, KIN-SNAP if tapped, exit travel **160ms** (leaving is faster than arriving), scrim fading with it. On the sheet's closed seat frame: focus returns to an **untouched Deck and an untouched rail** — cap still at FOCUS, lamp now off (§7 law fired at session end, not at dismissal: the sheet's leaving changes no chrome state). Dismissal feel: one 4ms soft seat at closed — quieter than invocation; putting a receipt away is not an event. |
| **REFUSED / edge laws** | (1) The sheet never appears mid-session, never for a routine ghost (no session sat = no receipt; absence renders as absence). (2) If a new arm begins… it cannot: the sheet is modal; the slide behind the scrim is inert until dismissal — ARM's ≤10s budget is unaffected because dismissal is one tap and the sheet is never on the *next* morning's Deck (it does not survive process death as a presented sheet; its facts survive only as RECORD aggregates). (3) Re-invocation: impossible — no surface re-presents a past sheet, ever. (4) In **V-TAB**, this sheet does not exist (the record has its stop; completion lands on the Deck's plain unlock, Razor §1.1). In **V-KILLED**, the session-end truth has *no* sheet — the Deck unlock + RECORD-absent is the whole event, and this section is build-time dead, exactly like a missing PARK (§8 law). Mikro's table does not fork by variant at runtime; the builder stacks it or doesn't. |

**Shield-section form (demote form (ii)) — micro-note only:** RECORD at
Shield's foot is *not operable*: no rows tap, no sheet invokes from it, no
press states exist there. Its numbers re-strike in place or not at all. The
moment RECORD grows a DOWN state it is growing back into a destination.
(Chrome consequence zero — Razor §1.2(ii) — and behaviour consequence zero:
that identity is the spec.)

---

## §12 — VARIANT INVARIANCE — the table holds in all six combinations

Build-time stacking (Razor): §0 base + one V-delta + one G-delta. My table
adds no runtime variant checks. What changes per combination is *which rows
exist* and the nav geometry the §7 states land on:

| Combination | Nav stops / segment / cap | §7–§9 consequences |
|---|---|---|
| **V-TAB, no PARK** (Guard = destination) | 4 stops · segment 85.5dp · cap 77.5dp · end-to-end 256.5dp / 360ms | §11 sheet: **does not exist**. §8 PARK: **does not exist**. §9 drawer: **does not exist** (Guard is a screen, reached by its own route, not this drawer). All other sections unchanged. |
| **V-TAB + PARK** (G-DISSOLVED) | 4 stops in reduced track · segment 73.5dp class (Razor §3.1) · travel shortens, KIN-NAV formula unchanged | §8/§9 fully live. §11 sheet: does not exist. Lamp law unchanged. |
| **V-DEMOTE, no PARK** | 3 stops · segment 114dp · cap 106dp · end-to-end 228dp / 300ms | §11 sheet: **live** (its home variant). §8/§9: do not exist. Wider cap = same states, longer skirt band; DOWN word-ink law identical. |
| **V-DEMOTE + PARK** (Plan's expected stack) | 3 stops + PARK zone (segment per Razor §3.1 after PARK subtraction) | §8, §9, §11 all live. Sheet invocation precondition (cap home at FOCUS) is unaffected by PARK: PARK is a position, the cap's home stops are unchanged. |
| **V-KILLED, no PARK** | 3 stops (as V-DEMOTE geometry) | §11: dead at build time. RECORD section: absent everywhere. Session end = Deck unlock only. No row in this table references the record. |
| **V-KILLED + PARK** | 3 stops + PARK | §8/§9 live; §11 dead. The drawer carries Guard only — it must NOT grow a record row as compensation (one fact, one rendering; a killed record has no rendering). |

**Stop-word note (flagged, not forked):** Razor's stops read
FOCUS / SYSTEM / HORIZON (/ LEDGER); Plan's verdict prints DECK · SHIELD ·
TASKS. The micro-states in §7 are word-agnostic — ink, travel, and arrival
laws are identical under either naming — but the printed words must be ONE
set everywhere (nav, headers, speech) and that choice is Plan/Sarthak's, not
chrome's and not mine. Until it lands, §7 ships with Razor's words.

**Session lock (all combinations):** HOLD absolute overrides every row —
while a session runs: nav stops refuse (§7), PARK inert (§8), drawer
uninvocable (§9), sleds/rockers/keys inert (§§4–6), wheel/hub inert (§§1–2),
war line stays live. Chrome unlocks on session end; the sheet (§11), where it
exists, is presented only *after* that unlock.

---

## §13 — FLAGS (needs from other seats — stated once, not forked around)

- **F1 → Kin: KIN-SHEET does not exist yet.** §11 runs on an interim pointer
  (rise 240ms CurveA + 8dp settle, exit 160ms, borrowed from the disarm-overlay
  class). If Kin wants the receipt on its own curve, name it and §11 re-points
  in one line. Everything else in this table points at settled Kin names.
- **F2 → Kin/Saft: slide hit-stop.** Saft: 40ms freeze at the seat. Kin D3:
  seat + dwell (≈120ms). §3 takes: freeze is the first 40ms of the dwell —
  compatible with both builds; merge confirms the total dwell once.
- **F3 → Kin/Rosa/Saft: hold grace.** 800ms (Rosa/Kin) vs 600ms (Saft) before
  drain starts. §10 requires *a* felt pause and no drain on the release frame;
  the number is the merge's, decided once, in one place.
- **F4 → Kin/Saft: wheel end-stops + fling decay.** Dead clamp vs rubber-band
  ×0.32/+7°; decay 2.4/s vs 3.0/s; snap stiffness 280 vs 320. §1's visual laws
  (grip look persists unseated; ≤7° non-oscillating overshoot if any; no
  scale/tone change at the wall) hold under every combination — Kin settles
  the numbers.
- **F5 → Plan/Latch: FLOW exit.** §6 requires an exit gesture whose arrival
  frame un-sunks FLOW. Which gesture (duration key, wheel past 10, or a
  FLOW re-press that unlatches) is an IA/feature call, not a micro one — but
  *no* build of §6 is complete while FLOW is one-way.
- **F6 → Ton: PARK detent tick pitch.** §8 requires PARK's entry tick to be
  audibly *not-a-stop*. One pitch assignment in Ton's family closes it.
- **F7 → Razor: file completeness.** Razor's chrome file as received ends
  mid-§1.2 (the (iii) corner form and §§2–3 deltas are not in the file on
  disk). §§7–9 above are built strictly on Razor §0 + Plan's §2 dependencies
  and do not invent Razor geometry beyond his stated formulas; when his
  deltas land, I re-check §8's PARK numbers against §3.1's worked figures
  and flag any collision by mail rather than forking. Mailed concurrently.

— Mikro, Designers (micro-interactions), for Sana
