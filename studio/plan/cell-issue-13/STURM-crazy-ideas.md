# STURM — CRAZY IDEAS for #13 (Session Room merge)
*Seat: Sturm, crazy-ideas, dedicated-100 programme. Filed on branch agent/issue-13 for the Wacht cell. Grounded in AUDIT-session-room.md + MERGE-PLAN.md (V1 as amended, E-1 ratified veil + Landing, §C-2 one-verb discipline, M-4 keep-awake as amended). Where an idea pushes against a standing ruling, it says so in plain words. Bindings honoured: no green, no AI images, blocker not tracker.*

The room already has superb bones: a vigil between thresholds. The audit's own verdict: the hours are one room; the ARRIVAL and the STAND-DOWN are where the accidents live. So Sturm's fire concentrates exactly there — the veil, the landing, and the raise — plus the physics of the face while it runs.

---

## S13-1 · THE VEIL BREATHES, THEN PARTS AT YOUR FINGER
**What:** E-1's night veil (black scrim 0.80 after a routine finish, until first touch) is not a flat scrim. It *breathes*: alpha oscillates 0.80 ↔ 0.84 on a slow ~4 s sine, subliminal, like something asleep behind it. And first touch doesn't just dismiss it — the veil PARTS from the touch point: a radial melt, the scrim dissolving outward from where the finger landed, on the same 520 Curve C as the Landing bloom.
**The crazy part:** You don't turn the room on. You touch the night and it opens where you touched it. The dismissal and the Landing become one gesture instead of two states.
**Build:** Veil scrim gets (a) a slow alpha oscillator — one infinite transition, stops on first touch; (b) a radial-gradient mask centred on the recorded first-touch coordinates, radius animated 0 → diagonal over 520 ms Curve C. The Landing's 0.55 state lives underneath, revealed by the parting. No new states; E-1 semantics untouched (controls live from first touch — the parting is pure surface).

## S13-2 · THE VEIL IS FROSTED, NOT BLANK
**What:** Under the scrim, freeze the face as it was at the finish instant: capture the last frame, downscale, heavy blur, render it beneath the 0.80 scrim. The veil is frosted glass over the room you left — not a black void.
**The crazy part:** At 03:30 the room doesn't vanish; it goes behind frost. When S13-1's parting melts the frost at your finger, the blur sharpens locally before the Landing takes over — for ~200 ms you see the room's ghost clear. One bitmap, held only while veiled, dropped on dismissal.
**Build:** One downscaled bitmap (≤ 480 px wide) captured at finish, BlurEffect or RenderScript-free manual downscale-blur (downscale IS the blur). Memory: trivial. Process death while veiled → bitmap gone → fall back to today's flat scrim. Degrades honestly.

## S13-3 · ONE EYE OPEN — the slit under the door
**What:** Under the veil, exactly ONE element survives above the scrim: the wall-clock time, printed at 0.55 — a slit of light under a closed door. Everything else is under the night. And the slit responds to the charger: plug in while veiled and the time's brightness steps up one floor (the door-light grows); unplug, it steps back down.
**The crazy part:** The veiled phone on the nightstand reads as a sleeping room with light under its door — and the light honestly encodes the one thing worth knowing at 3 AM without waking the room: the time, and whether it's feeding.
**Build:** Veil state already intersects charger machinery (M-4 surrender deadlines read charger disconnects). Render order: scrim → time at 0.55 → nothing else. Two brightness floors keyed to charger state. No new machinery.

## S13-4 · THE ROOM STAYS UPRIGHT IN A TILTING WORLD
**What:** During the raise (V1's armed-undecided sampling at ~50 Hz is already listening), the face counter-rotates against the phone's pitch — clamped to ±8° — so as the hand lifts and tilts the device, the hero digits stay level with the horizon. The room doesn't tilt when the world does.
**The crazy part:** Raise-to-start stops feeling like *operating a phone* and starts feeling like *lifting a lantern*: the light stays level while your hand moves around it. When REST verdict lands and the session raises, the counter-rotation eases to zero over 400 ms as the phone settles face-up on the desk — the room sits down with the phone.
**Build:** The rotation-vector / game-rotation stream is already open during armed-undecided (V1 as amended). Feed pitch into a graphicsLayer rotationZ counter-term, clamp ±8°, low-pass at the sampler's own rate. Scope it to armed-undecided + the first second after raise; zero cost while running (sampler closes per the ruling).

## S13-5 · YOUR RAISE SETS THE BLOOM — the room matches your energy once
**What:** V1 already computes band energy (7–14 Hz) to reach its REST / HELD verdict. Take the peak band energy of the raise that started the session and map it to the opening bloom duration: a decisive lift blooms in ~300 ms; a hesitant, slow lift blooms over ~900 ms. Clamp both ends. Then never again — this is not a metric, it's not stored, it's the room answering the body's one sentence.
**The crazy part:** Two mornings feel different because *you* were different — the room noticed how you picked it up. And because nothing is recorded, it can never become a score, a streak, or a judgement (blocker, not tracker — by construction).
**Build:** Peak band energy is a by-product of the discriminator; one mapping function (linear, clamped) feeding the existing bloom animation's duration parameter. If energy data is absent (UNKNOWN path / 20 s backstop), bloom defaults to the standard 520 Curve C — the honest fallback already in the Landing spec's family.

## S13-6 · THE IRIS — the room closes around you
**What:** Session start draws the room's four edges inward: hairlines (#493EA4 at low alpha) sweep in from the display edges over 300 ms and settle as a 1 dp inner frame, 8 dp inset. The face you know, but *enclosed*. Exit reverses it — the frame breathes back out to the edges and is gone.
**The crazy part:** Monolith's core act — a launcher you cannot exit (Sarthak's design ruling) — gets a threshold you can FEEL: crossing into the session, the world visibly narrows to a doorway's width. No new verbs (§C-2 untouched: tap still = exit; the iris is transition, not chrome).
**Build:** One inset-frame composable animated on session-state transitions; purely visual layer above the face, below the veil. Zero machinery.

## S13-7 · DAWN BREAKS ALONG THE PERFORATION
**What:** The dawn ramp (A-3, last minute) currently brightens the face. Re-anchor it: the light enters FROM the tear rule — a vertical gradient whose origin is pinned to the rule's Y position, spreading upward (and faintly downward) as the last minute runs. Dawn doesn't wash over the room; it breaks along the perforated line where blocks join, like light through a seam.
**The crazy part:** The tear rule (V3, notches at block boundaries) becomes the room's horizon line. The most structural element on the face turns out to have been the sky all along.
**Build:** Dawn ramp already exists as an animated brightness/alpha system; this re-pins its gradient geometry to the rule's measured Y (onGloballyPositioned, one value). No timing change, no new states.

## S13-8 · THE EXHALE — stand-down as one breath out
**What:** A-10 composed the 03:30 stand-down (ceremony + standing receipt + erasable record collided; the act is designed there). Add the room's body-language to it: at a routine finish, before the veil falls, the hero performs ONE slow exhale — scale 1.00 → 0.96 with a matching dim, over 6 s, once — then the veil (S13-1) breathes in over it.
**The crazy part:** The night ends the way a meditator ends: out-breath, then stillness. If he's asleep, nothing sounds, nothing demands — a 4% scale change is beneath waking. If he's awake, the room visibly *relaxes* instead of announcing. The stand-down stops being an event and becomes a posture.
**Build:** One 6 s tween on the hero's graphicsLayer at the finish transition, sequenced before veil onset. Composes with E-1 (veil still lands after routine finish, until first touch). No sound, no haptic — explicitly.

## S13-9 · DEEP STILL — the room rewards stillness by going quieter
**What:** While RUNNING, if the device is perfectly still for 10+ minutes (desk, nightstand), the face drops one rendering register: digit changes cut hard instead of cross-fading, brightness steps down one V4 floor, the per-second tick (under one hour) renders without interpolation flourishes. The room gets quieter the stiller the world is. Any motion restores the normal register within a second.
**The crazy part:** An inverted attention economy: every other app shouts louder when ignored; the Session Room *dims itself further* — stillness is the one input it rewards, by withdrawing. It also saves burn-in and battery as a side effect of being polite.
**Build — machinery price stated plainly:** V1's sampler is ruled to run only while armed-undecided. This asks for a duty-cycled exception while RUNNING: 1 s of sampling per 30 s, band energy below REST threshold = still. That's a machinery amendment for Ren/Wacht to price against battery; if refused, the idea dies and nothing else here depends on it.

## S13-10 · LANDING LIGHTS FOR THE DECIDING ROOM — *filed against a ruling, openly*
**What:** ARMED-UNDECIDED currently prints NOTHING (Nox A-N7, ruled KEEP — the state's numbers are machinery, hidden). Sturm files one dissent: during the 20 s backstop only, two faint vertical rails (#493EA4, alpha 0.18) fade in at the hero's flanks — runway lights for a room that is deciding whether you meant it. They vanish the instant the verdict lands, either way. No numbers, no spinner, no text.
**Why file against A-N7 anyway:** A silent 20 s is the one moment a first-time user concludes the raise "didn't work" and shakes the phone — corrupting the very band energy V1 is judging. Two rails say *the room heard you* without printing a single machinery number. If the cell holds A-N7, Sturm's fallback: rails appear only after 3 s of backstop (the long tail of UNKNOWN), still gone at verdict.
**Build:** Alpha fade on two 2 dp rails keyed to armed-undecided duration. Pure surface; the discriminator is untouched.

---
*Filed by Sturm. The veil set (S13-1/2/3) is one organism — take it whole if you take it. The raise pair (S13-4/5) spends data V1 already computes and stores nothing.
— Sturm, crazy-ideas*
