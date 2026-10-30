# Wren — Wild, cinema

**Lens:** Cinema does not arrange objects in space; it arranges attention in time — what is sharp, what is huge, what is cropped out, and the exact frame where the eye is allowed to move. Monolith gets one optical system, used everywhere, so the app feels *shot* rather than swapped.

**The body law (applies to every element below):** the chrome is the camera body. The top nameplate rail (MONOLITH wordmark, session-state word, deflection count), the bottom nav rail, and the 1dp hairline frame around the viewport live *outside* every transition container, in the parent scaffold. They never blur, slide, fade, scale, or crossfade. Body text changes are hard cuts — engraved labels do not dissolve. The only chrome motion ever permitted is the nav detent: a 1dp ink tick under the active destination that *jumps* (0ms) at the moment a transition lands. Zero orange in chrome, always. Everything below happens inside the aperture; the body is what makes it read as one instrument instead of five screens.

**Shared transition tokens (one family, no exceptions):** RACK 280ms · VERTIGO 1050ms in / 850ms out · HELD FRAME 1200ms · SHEET 260ms in / 190ms out · CUT 0ms. Blur curve `CubicBezier(0.32, 0, 0.18, 1)` everywhere blur is used. Depth is tone steps, hairlines, and recess only — blur here is optical defocus, never a drop shadow. Reduced-motion system setting collapses RACK → CUT, VERTIGO → 200ms peripheral fade with the readout static, and disables all weave/hold motion.

---

## Deck / Shield / Tasks — inter-screen transitions

**Chosen form — RACK FOCUS.** Screens never cut and never slide; the machine's eye refocuses.

- **Mechanism / data source:** one NavHost holds outgoing and incoming destinations simultaneously, composed only for the transition's duration (incoming `zIndex` above). A single `Animatable<Float> focusT` 0→1 drives both layers from the one navigation state flow, so the pair can never desync. Both screens render the *same* underlying state objects — the cued task title, session totals, blocked-app list are the identical strings/values on both sides; no number ever tweens, counts up, or is re-rendered in a second form mid-transition.
- **Motion:** 280ms total. Outgoing: blur 0→10dp, alpha 1→0.94, a tonal scrim rising to 0.16 of the ground tone, scale 1→0.995 — a defocused plane goes dimmer and sinks one tone step (night: toward `#171412`). Incoming: blur 9dp→0, scale 1.012→1.0, sharpening in three depth planes on the same `focusT` with offset starts — primary plane first (Deck readout / Shield knob + bag strip / Tasks composer + active row) at t=0, secondary rows at t+50ms, tertiary meta at t+100ms. In the last 50ms the incoming scale hunts 1.0→0.9985→1.0: the tiny settle of a lens finding focus. No horizontal or vertical travel — a rack focus happens in place; travel would make it a pan and invent a spatial map these three modes do not have. A reverse tap mid-transition reverses `focusT` from its current value; no jump, no restart.
- **Haptics:** silence on tap-down. One landing tick, 6ms @ 0.35, at ≈230ms when incoming blur passes 1dp — the tick is focus *found*, not the tap acknowledged. A reversed transition fires no second tick.
- **Sound:** explicit silence, always. A focus pull is silent; a whoosh makes it a presentation transition.
- **Depth / material:** separation is blur differential + one tone step, nothing else. The blurred layer is clipped inside the viewport hairline, so the body's frame stays pin-sharp over it — that contrast is the whole effect.
- **Sealed regime:** while a session is sealed, this transition does not exist. Nav taps are absorbed — the detent tick tries and stays, one 4ms @ 0.2 tick in the rail, viewport unchanged. Every other surface is a decision surface, and HOLD allows none. Deflections during a session surface in the body itself — one truthful line in the top rail (`3 BLOCKED`, Anonymous Pro caps) — with detail deferred to the end-of-session receipt.

**Runner-up — REEL ADVANCE.** Outgoing screen translates up and out of the aperture while the incoming enters from below, 340ms `CubicBezier(0.5, 0, 0.7, 0.3)`, a sprocket hairline running down the viewport's left edge, landing tick 6ms @ 0.35, sound silent, depth by one tone step on the trailing edge. It lost on three counts: it asserts a vertical geography (Deck *above* Tasks?) that the app does not mean; its travel axis collides with the Deck's slide-to-arm gesture direction; and text in motion is illegible longer than text in defocus — a rack keeps every word in place while it softens, a slide smears position *and* content.

**Guard fork — his call.** If Guard stays a destination, it racks identically: same 280ms, same planes, no special entrance — a settings surface given a ceremony would be lying about its rank. If Guard is dissolved, nothing new is invented: its status facts move into the chrome (the body — always sharp, never transitioning at all) and its website list moves into Shield, arriving as Shield's secondary plane inside the same rack. Either way the transition count and timing are unchanged.

**Standards check:** *ARM* — not on the arm path; costs one nav tap and 280ms, and arming never requires a navigation. *HOLD* — adds zero mid-session decisions; in the sealed regime it adds zero mid-session *anything*. *TRUTH* — one state source rendered on both layers, numbers fixed at true values throughout, no ghost totals.

---

## Deck — entering the running state

**Chosen form — VERTIGO SEAL.** The subject holds its size; the room falls away behind it. (This replaces my wave-1 Academy Leader as the daily form — see runner-up for why the leader was demoted.)

- **Mechanism / data source:** the commit gesture is the trigger — the Deck slide reaching its end detent, or START on Tasks (which racks to Deck in 280ms first, then runs this on the same commit; START never starts a *different* ceremony). At t=0 the session state flips IDLE→SEALED in the state flow and the timer starts counting from that instant. The animation is a picture of a fact that has already happened, never the fact itself. One `Animatable<Float> vertigoT` 0→1 drives four coupled transforms so the shot cannot desync: (1) the giant readout — scale fixed at exactly 1.0 for all 1050ms, the invariant of the shot, while its container padding grows so neighbours move and it does not; (2) the wheel — scale 1→0.80, translationY +20dp, alpha 1→0.5, dropping out of the focal plane; (3) task card and chips — scale →0.94, diverging ±10dp vertically, alpha →0 (demoted controls are *gone* in the running state, not greyed); (4) the ground — a Canvas field of concentric hairline rings in existing tone steps, centred behind the readout, ring spacing ×1.38.
- **Motion:** 1050ms, `CubicBezier(0.5, 0, 0.85, 0.35)` — slow departure, rushing middle, decelerating settle. Overshoot is forbidden; a real dolly cannot overshoot. The slide's orange COMMIT fill is spent during the first 200ms, draining to ink — the running Deck carries zero orange, because orange is the commit verb and the commit is over. The readout ticks truthfully throughout: at reveal it already shows the first elapsed second counting from t=0, never a frozen `25:00` held for the camera.
- **Haptics:** a receding tick train under the shot — t = 0 / 170 / 320 / 460 / 580 / 680 / 760ms, amplitudes decaying 0.30→0.12, spacing tightening as amplitude dies (something moving *away*). Settle knock at 1050ms: 16ms @ 0.7. The slide's own detent ticks before t=0 are unchanged and lighter — the body must feel the difference between adjusting and committing.
- **Sound:** silence by default. If app sound is on: filtered brown-noise room tone, −30dB→−23dB across the shot, cut dead at settle — the running state sounding like a *quieter room* than the idle state is the point.
- **Depth / material:** no new surfaces. Depth is scale differential plus the expanding tone-step ring field; the wheel fading over the rings reads as a plane of focus leaving, consistent with the rack's lens language.

**Runner-up — ACADEMY LEADER (3·2·1).** Full-screen countdown leader: crosshair hairlines, concentric circles, a linear rotating sweep wedge, huge Anonymous Pro numerals cutting in hard at 600ms intervals (1.8s total), letterbox bars in at 240ms and snapping out in 90ms onto the already-running clock, per-count clunk 14ms @ 0.8, one 45ms sync blip on the `2` only. It lost as the daily form, decisively: 1.8s of pure wait on every arm, roughly ten blocks a day, is ceremony taxing the arm path — exactly what the settled law forbids — and a countdown falsely implies a pre-seal window in which the session has not yet committed, blurring the truth of when HOLD began. It survives once only — see *Too good to lose*.

**Standards check:** *ARM* — gestures: set duration, cue task, one slide (all pre-existing); system adds 1.05s of motion *after* the seal, in parallel with a timer already running. Cold open to sealed ≈ gesture time + 0ms of blocking wait — far inside 10s, and nothing in this form can be shortened by impatience because nothing in it is a wait. *HOLD* — the end state contains no settings, no nav, no prompts: readout, task line, and the one pre-decided emergency control. *TRUTH* — elapsed time counts from the commit instant, not the animation's end; the orange commit is shown spent, not lingering as if still available.

---

## Deck — exiting: completion and emergency

**Chosen form — TWO EXITS, TWO CUTS.** The exits must not share a form; identical treatment would lie about what happened.

**Completion — THE HELD FRAME, then the room returns.**
- **Mechanism / data source:** triggered by the timer reaching zero in the session state flow — no user gesture, no decision. Phase 1, the hold: settle knock 24ms @ 1.0 at the zero instant; the final elapsed total stays alone at full giant size (Anonymous Pro) with one letterpress micro-line under it — task name and true length, e.g. `PHYSICS — 50 MIN` — for 1200ms of total stillness. No spinner, no progress ring, no congratulation copy; the implementation of the hold is a delay with nothing in it. Phase 2: the vertigo runs in reverse at 850ms — wheel, cards, and margins rush back in — landing on the idle Deck, where the receipt sheet rises (next element). One tap anywhere skips Phase 1 only.
- **Sound:** silence, except the room tone (if on) cuts out at the knock, leaving the hold genuinely quieter than the session.
- **Depth / material:** the flattest surface in the app — one number, printed, on one uninterrupted plane. Beauty here is restraint measured in milliseconds.

**Emergency — THE SPLICE CUT, on a banked hold.**
- **Mechanism / data source:** the break control is a recessed hold-well. Pressing it runs a drain-clock from a monotonic timer: the well fills in two honest tones — solid ink up to the last banked checkpoint (15 / 30 / 45s), one tone step lighter for unbanked progress beyond it. The true banked count renders once, in the well, in Anonymous Pro: `23 / 60`. On a slip (finger lifts), the unbanked portion visibly drains back to the banked floor over 300ms linear — losing progress to the checkpoint, never to zero, and *showing* that loss rather than hiding it. At 60 banked, the exit is a hard cut, 0ms: one-frame vertical slip of +2dp on the viewport content (a splice passing the gate), then the unsealed idle Deck, plus one plain statement in the viewport — `EMERGENCY BREAK USED — NEXT AVAILABLE <weekday>` — and a receipt that says what actually happened: true elapsed time, labelled `ENDED BY EMERGENCY BREAK`, never dressed as a completion. The one weekly use is spent at the cut, not at the first press — an abandoned hold spends nothing.
- **Haptics:** bank checkpoints 15/30/45: knock 12ms @ 0.6 each. Slip: descending double tick, 8ms @ 0.5 then 8ms @ 0.3 at +60ms. At 60: release pulse 30ms @ 0.8, then nothing. Silence otherwise.
- **Sound:** explicit silence throughout. An emergency exit gets no theatre in either direction — no reward fanfare, no punishment sting.
- **Depth / material:** the well is a recess (tone steps down), its fill flush inside it; the splice cut adds no surface at all.

**Runner-up — SYMMETRIC REVERSE VERTIGO for both exits.** Elegant, and wrong. It renders an interrupted session with the exact grammar of a finished one — a lie told by form — and it charges 850ms of travel on an exit whose 60 seconds have already been paid, at the one moment speed is the entire point.

**Guard fork — his call.** Under either Guard outcome, both exits land on Deck, never on Guard: no one should surface from a finished or broken session onto a settings/permissions surface. If Guard is dissolved, the post-emergency statement still lives on Deck, not in any drawer.

**Standards check:** *ARM* — exits are not the arm path; the emergency's 60s is his settled price, banked exactly as he set it, and completion costs zero gestures. *HOLD* — both doors are pre-decided by the rested user (the timer's end, the banked break); no exit presents a menu, a confirm dialog, or a "keep going?" prompt at any point. *TRUTH* — the banked fill, the drain, the elapsed totals, and the emergency label each render one fact once; a broken session is never rendered as a completed one.

---

## All surfaces — overlay entrances and exits

**Chosen form — APERTURE SHEET.** Overlays are trays sliding into the camera body, not windows floating over it.

- **Mechanism / data source:** applies to the session receipt, routine create/edit (opened from Shield, in the open state only), and any confirm sheet. The current screen stays composed behind and racks *shallow*: blur 0→7dp, one tone step down — the same optics as navigation at half depth, so the user reads "same room, attention moved" instead of "new place." The sheet itself is a full-width plate flush to the viewport's left and right hairlines and to the nav rail's top hairline; it translates up from the aperture's bottom edge, clipped by the frame, its top hairline leading. Content state is single-home: the receipt is generated once from the session-end event and the sheet owns those facts while open (the Deck behind it never displays the same total in a second form); the routine editor binds to the stored routine object, so there is no draft copy that can diverge from what Shield shows.
- **Motion:** entrance 260ms on the shared blur curve `CubicBezier(0.32, 0, 0.18, 1)`, background defocus driven by the same animatable so plate and room move as one shot. Exit 190ms, `CubicBezier(0.5, 0, 0.8, 0.4)`, plate descending, background refocusing in sync. Every dismiss route — swipe-down on the plate, tap on the defocused ground, system back — runs this same exit; there is no fade-only shortcut for any of them.
- **Haptics:** landing tick 6ms @ 0.35 when the plate settles; exit silent. No haptic on the background tap itself.
- **Sound:** explicit silence.
- **Depth / material:** the plate sits one tone step above the ground (filled areas never above the `#4A423C` cap at night), 1dp hairline on its top and side edges, square corners — a machined tray, not a card. Separation from the background is defocus + tone step; no shadow is cast in either direction. Sheets never stack: opening one first exits any open one (190ms), because a stack of trays is a drawer system by stealth.

**Runner-up — CENTRE DIALOG (platform default).** Fade + scale 0.96→1 over 180ms, centred, dim scrim, silent, no haptics. It lost because everything about it fights the body: it floats (implying elevation the material law forbids), its half-faded midpoint leaves both layers half-legible, and the centre-prompt pattern is precisely the shape a mid-session interruption would take — keeping that shape out of the app entirely is a HOLD decision, not a taste one.

**Guard fork — his call.** Sheets must not become a shadow Guard. If Guard remains a destination, its permissions, sound, and theme controls live *there*, on that screen, and are never duplicated into a sheet over Deck. If Guard is dissolved, those same controls may live in an aperture sheet opened from Shield — same entrance, same single-sheet rule — because in that outcome the sheet *is* their stated home, not a copy of it.

**Standards check:** *ARM* — no overlay sits on the arm path; the receipt appears only after a session has ended. *HOLD* — zero overlays can appear while sealed; the receipt is post-session by construction, and no sheet can interrupt a running state. *TRUTH* — one fact, one home, enforced structurally: single-source receipt data, write-through routine binding, no duplicated totals behind glass.

---

## Too good to lose

1. **ACADEMY LEADER, once in a lifetime.** The runner-up countdown (3·2·1, 600ms hard cuts, sweep wedge, letterbox snap) fired on the *first arm a user ever makes* — and never again. One premiere teaches what the seal means; ten a day would tax it to death. After that single firing the leader is retired permanently, its absence itself the sign the ritual was learned. Cost: one flag in stored state; zero effect on any later arm path.
2. **GATE WEAVE on the AOD desk clock.** The study clock (elapsed + remaining, coarse position shift against burn-in, unchanged) gains a sub-pixel tremor: container offset driven by a slow 1D value-noise sum, excursion ≤ ±0.9dp, updated at 4Hz, plus one single-tick +2dp vertical slip on each minute change — a splice passing the gate. Haptics: never (a haunted phone on a desk). Sound: none. Brightness flicker is rejected in-spec, permanently: weave is position only, and AOD legibility and true black are untouched. It makes the hours nobody is touching the phone read as a living machine instead of a screenshot.

## Mail / wall note

Inbox empty at start. Two letters arrived mid-run — from Orson (05:14) and Mara (05:17), same thread, *citation-requirement-lifted* — each relaying Sarthak's citation-lifting order verbatim, the same order my assignment already carried. Both read and moved to my inbox's `read/` subfolder before finishing; no reply needed, as they asked for nothing beyond the relay I had already executed. I posted that order verbatim on the Commons wall under my name so every seat can read it there. No other mail sent.
