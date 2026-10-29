# Opal — Wild, machinery

*Lens: every control in Monolith is a part in one machine. A part has a position, a travel, a detent, a catch, and a failure mode — and its position must always say which truth it is reporting: INTENT (a hand moved it) or PROOF (the system verified something). The whole file is built on that split. Where wave 1 blurred intent and proof into one animation, this spec separates them into two physical parts, because a machine that rounds its own state up is lying, and TRUTH once forbids it.*

*Settled law honoured throughout: no narrative wrapper, no auto-start theatre, no cocked-Deck ceremony — every sequence below is started by his thumb and only by his thumb. Routines propose nothing here; if a routine window is open, the parts sit exactly where he left them until he arms.*

*⚑ LEDGER STATUS: OPEN (reopened by Sarthak — "run ledger through agents, im not sure if we should keep or not"). Nothing below silently assumes it dead or alive. The reopening fact is precise: YPT tracks his study; it does not track Monolith itself — sessions sealed, deflections absorbed, holds spent, routine attendance exist only in this app. Every place my machinery depends on that answer is flagged in place with ⚑ LEDGER-DEPENDENT, and mapped here:*

- *⚑ D1 — Cascade, HALTED exit: whether a jam writes a machine fault event (see cascade).*
- *⚑ D2 — Cascade, standards check: the mid-session permission fact's home — receipt only, or receipt + record (see cascade).*
- *⚑ D3 — Cascade, Guard fork: "last-proven timestamps" as diagnostic depth presupposes a retained event record (see cascade).*
- *⚑ D4 — Too good to lose #2, the service position: a per-app proof history is a Ledger in miniature; it lives or dies with the verdict (see there).*
- *⚑ D5 — My full verdict, with form and placement: "LEDGER VERDICT FROM OPAL," after Too good to lose.*

*The old Ledger screen's forms stay rejected regardless of the verdict: no study-minute duplication (YPT owns study), no streak tower, no HMAC theatre, no dead curios. The only question reopened is the Monolith-native record.*

---

## Deck — slide-to-arm (the Drawbar)

**What it is:** a horizontal drawbar in a milled slot — not a button with a drag skin. The thumb pulls a sled along a track; the sled's travel is INTENT. A separate **proof pawl** on the wheel hub drops only when the blocking chain (next element) returns full proof; that drop is PROOF, and it — not the thumb — starts the timer.

### Chosen form — Drawbar with gate, latch, and proof pawl

**Mechanism and state / data source**

- Track: full-width slot, 264dp travel, 56dp tall, sled 64×48dp. Travel is 1:1 with the finger, no amplification, no rubber-band except a 4dp over-travel cushion past the seat pocket.
- State machine (owned by the session controller, single source; the view renders position only):

  `REST → ENGAGED → TRAVERSING → GATE → SEATED-LATCHED → PROVING → SEALED-RUNNING`
  with two exits: `SEATED-LATCHED → RELEASED` (thumb drags back before latch, see below) and `PROVING → JAMMED` (owned by the cascade element).

- **ENGAGED:** finger down on the sled captures it; sled rises one tone step (picked up), slot's printed scale appears — hairline ticks at 25 / 50 / 75% of travel, no numerals, no percentage readout anywhere. A percentage would be a second rendering of travel that the hand already knows.
- **TRAVERSING:** sled follows 1:1. Fill keying rule: the sled's orange COMMIT fill is keyed to *animated position*, not to a committed flag — below the gate it is ink-toned; the orange floods in across a 12dp band straddling the gate, so colour itself reports "you are crossing." Orange exists nowhere else on this screen.
- **GATE at 0.65 travel (~172dp):** a physical notch in the slot's lower edge and a spring ball in the sled. Crossing it costs a deliberate push-through: resistance rises over 8dp (rendered as the sled compressing 2dp in height — strain shown in the part, not in a strain-wall that taxes the thumb; there is no 2:1 wall, the gate is a click, not a hill). Past the gate, the latch *comes to the thumb*: the seat pocket's catch is spring-loaded toward the sled, so commitment completes at 0.82 travel, not at the slot's end. Nobody drags to the last pixel at 03:30.
- **SEATED-LATCHED:** the catch snaps over the sled's heel; the sled drops one tone step into the pocket (seated, not hovering) and can no longer be dragged back by a casual swipe — release now requires a deliberate 28dp reverse drag against the catch, available only in this state and in PROVING's first 300ms. Intent is now recorded: session parameters (duration from the wheel, loadout from the seated cap) are snapshotted at this frame and frozen. Nothing downstream may quietly re-read a changed wheel.
- **PROVING:** the latch fires the blocking cascade. The sled stays seated and visibly *held* — a thin hold-bar slides across the slot behind the sled (one tone darker, 6dp wide) so the geometry says "the machine has your instruction and is working." Duration readout does not start. No countdown, no elapsed time, no ghost minutes: the session clock's zero is defined as the proof pawl's drop.
- **SEALED-RUNNING:** proof pawl drops on the wheel hub (see cascade), hold-bar retracts, sled's orange drains to ink within 400ms — once running, the screen holds zero orange; motion and the readout are the signal. The drawbar is now inert for the session's life: touches on it do nothing, render nothing, offer nothing (HOLD absolute, enforced in the part, not in a dialog).
- **Emergency break interface:** the drawbar plays no part in the banked 60s hold. That hold lives on its own control; the drawbar never grows a second job.

**Motion**

- Drag: direct 1:1, no easing (the finger is the curve).
- Gate crossing: sled squash 120ms, `CubicBezierEasing(0.3, 0, 0.2, 1)`; recovery 90ms.
- Catch snap (SEATED-LATCHED): 70ms linear drop of 3dp into the pocket, then stillness.
- Hold-bar slide-in: 160ms `CubicBezierEasing(0.7, 0, 0.2, 1)` — slow breakaway, hard stop.
- Proof pawl drop: 90ms fast-in, 2° wheel nudge-and-settle over 110ms (the wheel acknowledges the seal; it does not celebrate it).
- Orange drain on seal: 400ms linear tone lerp. No bounce anywhere in this element; mass does not bounce.

**Haptics** (one gesture, one owner — the view; no ViewModel duplicates)

- Detent ticks while traversing: **4ms @ 0.30** at each printed tick (3 total).
- Gate: **12ms @ 0.80** single click, simultaneous with the squash.
- Latch: **22ms @ 1.0** knock — the loudest haptic on the arm path; commitment should be the thing the hand remembers.
- Pawl drop / seal: **8ms @ 0.70**, 60ms after the latch knock at the earliest (they must never blur into one buzz; if PROVING is fast, the pawl haptic is delayed to preserve the gap — sequence honesty applies to the hand too).
- JAMMED (from cascade): **20ms @ 1.0 ×3, 90ms spacing**, and the sled shudders 2dp once. Unmistakable, not alarming.

**Sound**

- Silence by default. With the mechanical sound set enabled (his toggle): gate = dry click, latch = low *thock*, pawl = one tick a semitone above the gate click. All synthesised bursts, −16 to −18dB under media, gated to Deck foregrounded. No seal chord, no completion flourish — the cascade's rhythm is the sound design.

**Depth / material**

- Slot is the Deck's one true recess: one step below ground (`#171412` night staircase), 1px light catch-line on the slot's lower edge only. Sled face one step above ground at REST, one step above that when ENGAGED (lifted), flush-seated when latched. Proof pawl: the hub's only matte-ink moving part, triangular, pivoted at 7-o'clock on the hub rim, drawn over the hub recess one step darker. No shadows anywhere; depth is tone steps and the recess, per locked foundations.

**Standards check**

- *ARM cost:* this element's share is one drag of ~0.6–0.9s. Cold open → latched ≈ 2–3s including wheel-set on a bad morning; the full budget is accounted in the cascade element (total ≤ ~4.5s worst normal case, well inside 10s).
- *Mid-session decisions introduced:* zero. After SEALED the part is inert; before latch, reversing is a gesture, not a decision dialog.
- *Truth risk:* the one real risk — sled seated reading as "blocked" before proof exists — is engineered out by the split: seated = intent (hold-bar geometry says *working*), pawl = proof, clock zero = pawl. The readout can never mint a minute the chain hasn't earned.

### Runner-up — the Throw Lever

A pivoting lever at the Deck's right edge: thumb hooks it at ~20° and throws it through 110° to a latched horizontal; the throw's second half drives the same latch/proof split (the state machine above is reused unchanged — only the input part differs).

- Compact spec: pivot in a recessed boss, lever arm 72dp, detent ball at 55° (the gate analogue), end-stop knock **20ms @ 1.0**, throw animation is finger-driven 1:1 in angle, spring completes the final 15° once past 95° (the latch comes to the thumb, rotationally). Sound and depth treatment identical to the drawbar.
- *Why it lost:* three machinery facts. (1) One-handed reach: the lever's hook point sits outside the natural thumb arc for a left-edge grip and demands a grip shift — the drawbar starts wherever the thumb already rests. (2) Occlusion: mid-throw, the hand covers the readout — the one big number disappears at the exact moment of commitment. (3) Failure legibility: a half-thrown lever reads as "nearly on" to a tired eye; a sled at 40% of a slot reads as *travel remaining*, which is the truth. The drawbar's incompleteness is self-explaining; the lever's is not.

**Guard fork:** not relevant to this element — the drawbar neither hosts nor hides Guard state. (The interlock strip that reports chain progress lives above this slot; its placement fork is handled under the cascade.)

---

## The blocking cascade — the Series Interlock

**What it is:** the real sequence that turns a latched intention into a sealed session. Its law is a sentence: **apps are blocked in series, each link is proven before the next is commanded, and the timer starts only when the last proof returns.** This is the element where Monolith either tells the truth about blocking or becomes theatre; everything here is sequenced to make theatre impossible.

### Chosen form — Series chain with per-link proof and a visible halt

**Mechanism and state / data source**

- The chain is the session's app set, in a fixed, printed order: the loadout's app list in Shield order (top to bottom). Order is never shuffled, parallelised, or "optimised" — a fixed order is what makes a halt locatable.
- Per-link states, driven by real checks against the blocking stack (AccessibilityService presence + the app's package state + ShieldService enforcement confirmation), never by elapsed time:

  `OPEN → COMMANDED → CONFIRMED-SHUT` · failure exits: `JAMMED-NOTFOUND` (package absent), `JAMMED-NOPERM` (accessibility/usage permission lost), `JAMMED-TIMEOUT` (no confirmation within 400ms for that link).

- Chain state machine: `IDLE → RUNNING(link n) → … → COMPLETE` or `→ HALTED(link n, reason)`. Link *n+1* is commanded only on link *n*'s CONFIRMED event. This is a gated state machine, not a timed animation with checks stapled to it: if a confirmation never arrives, the chain visibly waits at that link, then halts on timeout. The animation has nothing to play until reality moves.
- **Rendering — the interlock strip:** one hairline row seated directly above the drawbar slot on Deck: one 10dp square cell per chain link, in chain order, each cell a recess one step below ground. A cell fills with ink **only on CONFIRMED** — never on COMMANDED. A commanded-but-unproven cell shows its outline breathing nothing, animating nothing: it shows a 2dp ink dot at its centre (command sent — a fact) inside an unfilled cell (proof absent — also a fact). Two facts, two marks, neither impersonating the other. Margin label in Anonymous Pro micro-caps, state word at the margin per the state grammar: `GUARDS 4 / 7` while running; the word flips only when the count actually changes.
- On Shield, the same chain renders on the app rows themselves: each row's shutter plate (see sleds element) slides over the row's track on that row's CONFIRMED event. One chain, two renderings of *different* facts — Deck shows count/order, Shield shows which app — this is not a duplicated total; neither surface repeats the other's fact.
- **COMPLETE:** the last CONFIRMED fires the proof pawl (drawbar element), the session clock starts at that timestamp, and the snapshot's parameters become the session. Elapsed-before-proof is never counted, never rounded into the session, never shown.
- **HALTED:** the chain stops. Downstream links remain OPEN and render OPEN. Already-confirmed links *hold* their block (releasing them instantly would flicker apps open under a man who is trying to start studying — and their state is true: they are blocked). The jammed row's sled sits at a 12° skew (sleds element) and the Deck strip's halted cell shows the skew in miniature: the cell rotates 12° inside the strip. Margin word, human language, one line, no error codes: `INSTAGRAM — PERMISSION OFF` / `TIKTOK — APP NOT FOUND` / `X — NO ANSWER`. His two exits, both gestures, no dialog:
  - **Push the jammed sled square** (tap the skewed cell / row): retries *that link only*. If it confirms, the chain resumes from that link. Retry is per-link because the fault is per-link.
  - **Drag the drawbar back** (the 28dp reverse against the catch): full release — chain unwinds in reverse order, confirmed links reopen at 40% of forward speed, sled returns to REST, and **no session is written**: a jammed arm leaves no session, no minutes, no receipt — as a *session*, nothing happened. ⚑ LEDGER-DEPENDENT (D1): whether the *jam itself* is filed as a Monolith-native fault event (`CHAIN JAMMED — INSTAGRAM, PERMISSION OFF, 05:04`) is exactly the reopened question. Under my verdict (b, below) it is filed — a machine that hides its own faults from its service log is lying by omission. Under (c) it is not, and this release is total. Neither is assumed anywhere else in this file.
- Timing budget (engineering targets, measured on the real stack, not animation durations): per link 60–150ms typical, 400ms timeout; chain of 7 ≈ 0.6–1.1s typical, 1.8s hard ceiling before HALTED-TIMEOUT logic has already resolved per-link. PROVING therefore costs ~1s on a normal morning.

**Motion**

- Cell fills: instant on the CONFIRMED frame (a fill that eases in would claim a gradient of proven-ness that does not exist). The only motion is the shutter plates on Shield: 140ms slam, `CubicBezierEasing(0.8, 0, 0.3, 1)`, staggered *by the confirmations themselves* — if confirmations arrive bunched, the slams bunch; the rhythm is the system's real rhythm, never a metronome imposed on it.
- Unwind on release: plates retract in reverse chain order, 90ms each, `CubicBezierEasing(0.2, 0, 0.4, 1)`.

**Haptics**

- Per CONFIRMED: **nothing.** Seven apps must not mean seven buzzes competing with the latch knock the hand is meant to remember. The chain is silent in the hand while it runs.
- COMPLETE: the pawl's **8ms @ 0.70** (shared with the drawbar — one event, one haptic).
- HALTED: the drawbar's triple **20ms @ 1.0 ×3 @ 90ms** and nothing else.

**Sound**

- Silence by default. Mechanical set on: each shutter slam is the same short plate *clack*, identical pitch — same machine, same part — so a halt is audible as the clacks *stopping*, which is the most honest alarm this app owns: an absence, not a siren.

**Depth / material**

- Strip cells: recess one step below ground, fill = ink tone one step above ground, centre dot = panel tone. Skewed halted cell: same tones, rotated geometry only — no alert colour exists in this element; form carries the fault (position/weight/word grammar). Shutter plates: sled ink, one step darker, 1px catch-line on the seating edge.

**Standards check**

- *ARM cost:* PROVING ≈ 0.6–1.1s typical. Full cold path: open → wheel set (~1.5s) → drag + latch (~0.8s) → proving (~1s) → sealed ≈ **3.5–4.5s**, ceiling ~6s on a slow morning. Inside 10s with margin, and every millisecond of it is either his hand or a real check — no ceremony tax.
- *Mid-session decisions introduced:* zero. All chain behaviour is pre-seal. A halt presents a fault and two gestures before any session exists; it never interrupts a running one. If a permission dies *mid-session*, the chain does not reopen a negotiation: the session continues, the fact is held, and it is stated once on the end-of-session receipt (`INSTAGRAM — PERMISSION OFF FROM 14:20`). Truth delivered at the one moment he's rested enough to act on it. ⚑ LEDGER-DEPENDENT (D2): the receipt line stands under every outcome — it is an issuance, not an archive. Whether that same fault *also* files into a retained record is the reopened question; under my verdict (b) the receipt is the copy he sees at completion and the service-log sheet (verdict section) is the fact's home — one fact, one home, one issuance.
- *Truth risk:* this element *is* the truth risk, and the design is the mitigation: fills on proof only, clock zero at final proof, halt leaves downstream honestly open, jammed arm writes no record.

### Runner-up — the Parallel Barrage

All links commanded simultaneously on latch; cells fill as confirmations land in whatever order they return; a single aggregate bar under the strip sweeps toward full.

- Compact spec: same per-link checks and timeouts; same pawl on final confirmation; bar = confirmed count / total as a continuous fill.
- *Why it lost:* it is faster on paper (~0.4s saved) and worse at everything that matters. (1) The aggregate bar is a readiness rendering — a percentage of proven-ness — exactly the decorative readiness TRUTH once kills; worse, at 6/7 it *looks* sealed. (2) Fault location dies: when two links are pending, a halt cannot say which one the machine is waiting on, so the error line becomes a list and the retry becomes "retry all." (3) The haptic/sound rhythm becomes mush — seven overlapping slams read as one event, so the audible halt (clacks stopping) disappears. Series costs 400ms and buys locatability, honesty, and a rhythm. Machinery pays for those every time.

**Guard fork — his call, both placements specced**

The cascade depends on permission health (accessibility + usage access). That health has to live somewhere:

- **If Guard is dissolved:** permission health lives as a single margin line at the top of Shield's app section — `BLOCKING READY` or `BLOCKING — PERMISSION OFF`, human words, no pill, no fill — and the interlock strip on Deck carries the per-link truth during arming. A pre-flight check runs when the Deck is opened (one real query, ~30ms, result = that line's state everywhere it appears; one fact, one source).
- **If Guard survives as a destination:** Guard hosts the chain's diagnostic depth — per-app permission state, last-proven timestamps — while Shield keeps only the one margin line and Deck only the strip. Guard becomes the machine's inspection hatch, not a second settings pile. ⚑ LEDGER-DEPENDENT (D3): *current* permission state is operational and lives under every outcome; *last-proven timestamps* are history and presuppose the record survives in some form (under my verdict (b) they live in the service-log sheet, and Guard — if it exists — reads from that same store, never a second copy).
- My lens argues for **dissolved** (labelled: his call): the chain's truth already lives on Deck and Shield at the moments it's needed; a destination adds a place to *check* safety instead of a machine that *shows* it where the hand already is. But the cascade's function is identical under both — only the diagnostic depth moves.

---

## Shield — loadout seating (the Keyed Cap)

**What it is:** the loadout band selects which app set the chain will enforce. Its part is a cap running on a printed track with a notch per loadout and a drop-catch that seats behind it. The governing idea: **the cap is already seated when he wakes.** Loadout is a rested decision; the band's job on the arm path is to be found seated, legible, and impossible to nudge — not to be operated.

### Chosen form — keyed cap, discrete notches, drop catch, seated-by-default

**Mechanism and state / data source**

- Track: horizontal, recessed one step, with one machined notch per loadout (STUDY / SLEEP / TEST / plus his own created loadouts — the notches are generated from the real loadout list, in list order; a deleted loadout's notch disappears, a created one appears at the end; the track never prints a stop that doesn't exist).
- Cap state machine: `SEATED(n) → LIFTING → TRAVERSING → DROPPING → SEATED(m)`, plus `BETWEEN` — the honest unseated state (below).
- **The catch:** a 6dp rectangular tooth housed in the track's top edge above each notch. While SEATED it sits dropped into the cap's heel notch — the cap physically cannot slide; a drag attempt moves it 0dp and produces the catch's resistance tick. To move the cap, the drag's first 6dp of travel is *vertical work*: press-and-push lifts the catch (catch translates up 6dp over 80ms, cap rises one tone step — picked up), only then does horizontal travel begin. Two-phase initiation is deliberate: loadout changes are rested-user acts and may cost 80ms of intent; accidental brushes in a scroll must never reseat a session's app set.
- **Seating:** entering a notch's capture zone (±10dp), the cap is drawn the final distance by the notch (spring capture, finger may already be leaving); the catch drops behind it (70ms linear) and only *then* does the data commit: the persisted `activeLoadoutId` writes on catch-drop, never on drag, never on hover. The margin label — loadout name + app count in Atkinson body / Anonymous Pro figures, placed at the track's left margin, never inside the cap (position = power; words at margins) — flips on catch-drop, never mid-flight.
- **BETWEEN:** if a drag ends outside every capture zone, the cap does not spring to a neighbour and does not pretend: it rests where it lies, the margin label reads `NOT SEATED`, and the Deck's drawbar is disabled with its slot label stating the one reason — `LOADOUT NOT SEATED`. One fact, stated at the place it blocks. (This state is reachable only by a deliberate two-phase drag abandoned mid-track; it cannot occur from a routine, a reseat, or an update, because every programmatic path writes only seated states.)
- **Persistence & the arm path:** `activeLoadoutId` persists across sessions and days. The rested path — he changes loadout at night, or never — means the 05:00 arm finds the cap SEATED in the right notch and the band costs **zero gestures and zero seconds**.
- SLEEP loadout note: seating SLEEP follows the same mechanics; nothing about the band auto-fires, auto-arms, or pre-seats itself at 20:00. The cap moves when his hand moves it.

**Motion**

- Catch lift: 80ms, `CubicBezierEasing(0.4, 0, 0.2, 1)`. Horizontal drag: 1:1. Capture draw into notch: 110ms spring (stiffness ~380, damping ~0.85 — one visible settle of ≤2dp, no oscillation train). Catch drop: 70ms linear. Label flip: instant on catch-drop frame.

**Haptics**

- Catch lift: **6ms @ 0.60** (the two-part *ka-* of a ka-chunk). Traversal over intermediate notches: **4ms @ 0.30** per notch passed — the track is countable in the hand, so TEST seated past STUDY is felt as two passes and a seat. Catch drop / seat: **14ms @ 0.90** (the *-chunk*). Drag against a dropped catch (accidental brush): single **5ms @ 0.40** resistance tick, no motion — a refusal the thumb understands before the eye does.

**Sound**

- Silence by default. Mechanical set: lift click + seat *thock* a semitone below the Deck latch (same family, different part — the ear learns two locks, Deck and Shield, and never confuses them).

**Depth / material**

- Track recess one step below ground; printed notch ticks and loadout micro-labels letterpressed on the track bed (Anonymous Pro micro-caps, panel tone). Cap: one step above ground, ink marker line on its top edge in constant cream `#FBF1EC` (the line is a pointer, not state — state is the margin word). Catch: cap ink, one step darker, catch-line on its seating edge. No orange: the band is Shield chrome, and orange is a COMMIT fill that lives on the Deck slide and Tasks START only.

**Standards check**

- *ARM cost:* 0s / 0 gestures on the normal path (pre-seated by persistence). A deliberate reseat costs ~1.2s but that act belongs to the rested user, off the arm path by construction.
- *Mid-session decisions introduced:* zero — during SEALED-RUNNING the catch is locked by the session state on top of its own drop: the cap cannot lift, the band renders its session overlay word at the margin (`IN SESSION — STUDY`) and otherwise ignores touch. Changing the loadout mid-session is not hard; it is absent.
- *Truth risk:* two, both closed: commit-on-catch-drop (a cap dragged across three loadouts has selected none of them until one seats), and BETWEEN rendered as BETWEEN (no nearest-loadout rounding). The Deck block reason names the actual state.

### Runner-up — the Index Drum

A vertical-axis drum (rotary wheel logic, horizontal strip): loadout names printed on a cylinder, flick to rotate, the centred name is the selection, committed on settle.

- Compact spec: drum settles with detent spring per name, **5ms @ 0.40** per detent, selection commits when angular velocity < threshold for 120ms, margin readout duplicates the centred name.
- *Why it lost:* (1) The drum shows one loadout and hides the set — the keyed track shows *all stops and the cap's distance from each*, which is the shape of the decision; machinery shows its range. (2) Settle-commit is a guess dressed as a fact: a drum still creeping at threshold commits a loadout the hand may not have chosen — the catch-drop commit has no such ambiguity, a tooth is in a notch or it isn't. (3) It duplicates the wheel's gesture language for a 3–5 item list, spending the Deck's hero mechanic on Shield's quietest control.

**Guard fork:** not loadout's business, with one exception — if Guard survives as a destination, loadout *editing* (create / rename / delete app sets) may live there rather than in Shield's band long-press; the seating part itself stays on Shield under both outcomes, because the chain that consumes the loadout is armed from Shield's neighbour screens and the cap must sit beside the sleds it governs.

---

## Shield — app sleds (policy sleds + session shutter)

**What it is:** one row per app, one sled per row. The core move of this file, applied at row scale: **two parts, two truths.** The *sled* is the rested decision — per-app policy, set by hand, persisted. The *shutter plate* is the session's enforcement — driven by the cascade, never by the hand. Wave 1 let one part play both roles; this spec never does.

### Chosen form — three-stop policy sled with drop detents, plus an independent shutter plate

**Mechanism and state / data source**

- Sled track: 96dp wide at the row's right end, recessed one step, three detent notches: **OPEN · PEEK · SHUT**. Printed micro-labels under the track's stops, letterpressed; the row's margin word (left, beside the app name) states the policy in words — position and word are two renderings *of the same stored value*, kept in sync by rendering both from `policy(appId)` on the same frame, margin word flipping only on detent seat.
  - OPEN: the app is not the machine's business this session.
  - PEEK: the app is blocked in session, reachable only through the peek pass (the existing 2-minute airlock peek) — policy means the app *may* be peeked, not that it is open.
  - SHUT: blocked, no peek.
- Sled state machine (per app, independent): `SEATED(stop) → TRAVERSING → SEATED(stop')`, with detent capture ±8dp and commit-on-seat exactly like the loadout cap (persist on seat, never on drag). A sled abandoned between stops springs back to its *origin* stop — return, not nearest: a half-finished policy change reverts to the policy that was actually in force, because nearest-seating would invent a decision. (Contrast with the loadout cap's BETWEEN state: the band can rest unseated because an unseated band blocks arming honestly; a sled resting between stops would render a policy that exists nowhere in data. Different parts, different failure geometry — each matched to what its lie would cost.)
- **Shutter plate:** a plate the width of the sled track, housed above it, driven only by the cascade. On the app's CONFIRMED-SHUT (for SHUT and PEEK policies alike — both are blocked at seal), the plate slides down over the track, covering the sled completely, its leading edge carrying the 1px catch-line. Covered = *this policy is currently enforced by a running session.* On session end (or unwind after a jam), plates retract in reverse chain order. Plate motion is never finger-driven; touching a covered sled does nothing — the part under the plate is unreachable, which is HOLD absolute expressed as geometry.
- **Fault geometry:** if the cascade halts on this app, its sled — plate never having arrived — rotates 12° in its track and stays skewed until retried square or the arm is released. If a permission failure is detected outside arming (the pre-flight check on Deck/Shield open), no sled moves; instead the row's margin word gains the second line `NOT ENFORCED — PERMISSION OFF` while the sled keeps showing the policy: policy is his intent, enforcement is the system's fact, and the row can now show both without either lying.
- **App missing:** package uninstalled → sled rendered seated at OPEN, track notches unprinted except OPEN's, margin word `NOT INSTALLED`, sled immovable (drag produces the resistance tick only). An uninstalled app cannot hold a SHUT policy that the chain would later jam on — the fault is moved to the earliest moment it can be known, which is the moment it happens.
- Grouping and list behaviour stay as settled (group by state, OPEN collapsed); the sled's *part design* is what this seat owns.

**Motion**

- Manual drag: 1:1. Detent capture draw: 90ms spring (stiffness ~420, damping ~0.9). Return-to-origin on abandoned drag: 160ms, `CubicBezierEasing(0.3, 0, 0.5, 1)` — slightly slower than a seat, because a revert should read as the machine *undoing*, not choosing.
- Shutter plate slam (on CONFIRMED): 140ms, `CubicBezierEasing(0.8, 0, 0.3, 1)`, timing dictated by the confirmation, not a stagger table. Retraction: 90ms each in reverse order.
- Skew on jam: 120ms rotate to 12° with a 1dp drop (a part out of its track sits lower — gravity as punctuation). Un-skew on retry: 100ms.

**Haptics**

- Detent pass while dragging: **4ms @ 0.30**. Seat: **10ms @ 0.70** — quieter than the loadout seat (14ms @ 0.90) and the Deck latch (22ms @ 1.0): the haptic hierarchy is a volume hierarchy of consequence, and a per-app policy tick must never shout over a session commit.
- Abandoned-drag revert: **6ms @ 0.50** on re-seat at origin.
- Shutter slam: **no haptic** — the cascade is silent in the hand by design (see cascade); the slam's clack exists only in the optional sound set.
- Skew/jam: covered by the cascade's triple pulse; the row adds nothing.

**Sound**

- Silence by default. Mechanical set: detent seat = soft tick; shutter slam = the chain's uniform plate *clack*. No per-app pitches — seven apps with seven voices would be a xylophone, not a machine.

**Depth / material**

- Track recess one step below ground, sled one step above, plate one step above the sled (the plate must read as *over*, the only stacking overlap in the element — justified because coverage is literally its meaning). Detent notches letterpressed into the track bed. Skewed sled keeps its tones; geometry carries the fault. Night: SHUT sled label cream on the `#4A423C` capped fill, per the locked pairing fix. No orange, no alert colour anywhere on Shield.

**Standards check**

- *ARM cost:* zero on the normal path — policies persist; the cascade consumes them without asking. Setting policy is rested-user work in Shield, deliberately off the Deck's clock.
- *Mid-session decisions introduced:* zero — plates cover the sleds for the session's duration; there is nothing left to decide *with*.
- *Truth risk:* the historical risk — a sled animation claiming enforcement that hasn't happened — is removed structurally: the hand's part (sled) can only ever claim policy, the system's part (plate) can only move on CONFIRMED. `NOT ENFORCED` appears the moment a pre-flight check knows it, not at the next failed arm.

### Runner-up — the two-stop sled (OPEN / SHUT, PEEK deleted as a stop)

Same track, detents, commit-on-seat and shutter plate; PEEK removed — peeking becomes a global loadout property instead of a per-app policy.

- Compact spec: travel shortens to 64dp, two notches, seat haptic **10ms @ 0.70**, all other numbers inherited.
- *Why it lost:* it lies by compression. Per-app peek is a real distinction in his machine — one app he may need mid-session (a messenger) against twenty he may not — and folding it into a global flag forces the rested user to choose between "everything peekable" and "nothing peekable," neither of which is his policy. Three stops cost 32dp of track and one extra detent tick; the truth they preserve is worth more than the simplicity bought. Simplicity that deletes a real state is not simplicity; it is a rounding error with good posture.

**Guard fork — his call, both placements specced**

- **If Guard is dissolved:** website blocking joins this list as rows of the same construction (a site row's sled governs its domain set), and the pre-flight `BLOCKING READY / — PERMISSION OFF` line sits at this section's head (see cascade fork). Nothing about the sled part changes.
- **If Guard survives as a destination:** per-site sleds and permission diagnostics live on Guard; Shield's sleds govern apps only. The parts remain identical — a sled is a sled on either screen — so the fork moves inventory, not design.

---

## Too good to lose

**1. The audible halt as a first-class signal (fragment of the chosen cascade, specced to keep).** With the mechanical sound set on, the cascade's identical plate *clacks* stopping mid-chain is the failure alarm — an absence, not a siren. Cost: zero additional assets (the clack already exists per slam); the requirement is only that slams stay pitch-identical, forever, against any future urge to "vary" them. The day someone pitches the clacks per app, the halt becomes inaudible and this dies. Spec it as a constraint, not a feature: *all shutter slams, one pitch, one burst, −16dB, no exceptions.*

**2. RAW FRAGMENT — the service position.** ⚑ LEDGER-DEPENDENT (D4): this fragment *is* the Ledger question in miniature, and it no longer pretends otherwise. Long-press any shutter plate (post-session, plates retracted) and that row's sled track *extends* 24dp to a fourth, unprinted position past SHUT: the service position, where the row unfolds its proof history for that app — last CONFIRMED timestamp, last jam reason, in one Anonymous Pro line. A part pulled one notch past its working range, the way a machine's inspection position works. Under my verdict (b, below) this fragment is *absorbed*: the per-app line reads from the same single event store as the service-log sheet — the fragment becomes a window into the record, not a second record. Under (c) it dies outright, and honestly: a proof history with no record behind it would be theatre. Unbuilt question that survives either way: whether the extra travel confuses the three-stop grammar. Kept because the instinct — *diagnostics live inside the part they diagnose* — is the machinery lens at its best.

---

## LEDGER VERDICT FROM OPAL

**My option: (b) — demote. No tab. The Monolith-native record survives as a service log inside the machine: one sheet, fed by one event store, plus exactly two operational slices that live where decisions are made. Everything else about the old Ledger stays dead.**

The machinery reasoning, plainly: every real machine carries a service log — a plate riveted inside the housing, an hour-meter, a fault register — and no machine ever built carries a *room* you walk into to admire its own history. A tab is a room. It invites browsing, and browsing a record of yourself is a pastime that competes with the next arm. But killing the record entirely (c) asks the machine to forget the only facts nobody else holds: YPT cannot say how many launches Monolith absorbed at 14:00, whether Tuesday's routine was attended, or when the week's one hold was spent. A machine that cannot account for *itself* fails TRUTH once just as surely as one that duplicates YPT's minutes. So: the record is real, it is small, it is filed — and it is housed like a log plate, not a destination.

### The wildest credible form under (b) — THE SERVICE LOG: tally drums over an event roll

One sheet. Summoned only when no session is running, from two doors that open the *same* sheet (one store, one rendering): a `MACHINE RECORD` row at the foot of Shield, and a long-press (600ms) on the MONOLITH nameplate. During SEALED-RUNNING both doors are inert — the log cannot be opened mid-session, so it can never become a mid-session decision or a distraction surface (HOLD absolute, applied to the record itself).

**What the sheet is, top to bottom:**

1. **Three tally drums.** Mechanical counter drums — odometer wheels, one recessed window each, Anonymous Pro digits on rotating drums — carrying the three lifetime Monolith-native totals, and no others:
   - `SESSIONS SEALED` (count of proof-pawl drops — seals that actually happened, never arms attempted, never ghost sessions)
   - `DEFLECTIONS ABSORBED` (count of blocked launch attempts the blocking stack turned away)
   - `HOLDS SPENT` (count of completed banked-hold breaks, all-time; the *weekly remaining* fact is operational and lives in Shield, below — the drum is the machine's lifetime tally, the Shield line is this week's state, two different facts)
   When the sheet opens and a drum's value has changed since the sheet was last opened, that drum *rolls* to the new value: stepped rotation, one digit position at a time. A count that did not change does not move. The roll is the machine showing its work since you last looked — never ambient, never on open if nothing changed.
2. **The event roll.** Below the drums, the filed events as stamped hairline rows — newest first, each row carrying a sequential event number (the machine's own count of its own events: provenance, not cryptography — no hashes, no HMAC, nothing performing security), an Anonymous Pro timestamp, and one human-language line. Exactly these event types exist, and no others:
   - `SEALED 05:02 · STUDY · 3 BLOCKS` / `SESSION COMPLETE` / `SESSION ENDED — HOLD` (a session ended via the banked break is filed as ended, with the hold event below as its cause — never dressed as a completion)
   - `HOLD SPENT · 60S BANKED · 1 THIS WEEK`
   - `PEEK USED · INSTAGRAM · 14:22` (per peek, timestamped)
   - `DEFLECTIONS 07 · SESSION OF 05:02` (one roll entry per session, aggregated — never one row per absorbed tap; the drum carries the running total, the roll carries the per-session figure, the live in-session count is the same counter before filing)
   - `ROUTINE ATTENDED · FASTED BLOCK` / `ROUTINE MISSED · DAY BLOCKS` / `ROUTINE HELD — MANUAL SESSION RUNNING` (attendance is written only *after* a routine window closes: attended = a sealed session overlapped the window; missed = window closed with no overlap; held = a manual session was already running — three different facts, three different words, none inferred mid-window)
   - `CHAIN JAMMED · INSTAGRAM — PERMISSION OFF` (see ⚑ D1) / `PERMISSION LOST · 14:20` / `PERMISSION RESTORED`
   The roll is finite in ambition: it is a log, not a feed — no charts, no streaks, no week views, no totals of study time anywhere on it.

**Mechanism and data source**

- One append-only local event store, written at the moment of the event by its owner and by nobody else: the session controller writes SEALED / COMPLETE / ENDED; the cascade writes CHAIN JAMMED; the blocking service writes deflection counts (debounced — launches within a 5s burst aggregate into the session's count, so a thumb rattling an icon doesn't mint 30 events) and PEEK USED; the hold control writes HOLD SPENT only when the banked 60s completes; the routine engine writes attendance at window close, computed from actual seal intervals overlapping the window.
- Drums are *derived*, never stored separately: a drum's value is a count over the store, so drum and roll can never disagree. One store, one truth, two renderings of genuinely different facts (totals vs. events).
- Nothing is inferred retroactively: an event type whose sensor didn't exist yet starts counting from when it ships, and its drum starts at the true count from that date. No backfilled zeros pretending to be history.

**Motion**

- Sheet rise: 280ms, `CubicBezierEasing(0.3, 0, 0.2, 1)`, from the bottom edge, settling flush — a plate sliding out of the housing, no bounce.
- Drum roll: 44ms per digit step, stepped (no ease — counters step), total capped at 700ms; if a drum owes more steps than the cap allows, it rolls the final three steps visibly and lands — the *landing value* is always exact; only the theatre is truncated, never the number.
- Event roll rows: still. A log does not animate its entries.

**Haptics**

- Sheet open: **10ms @ 0.60** (a plate unlatching). Drum roll: **3ms @ 0.25** per step, capped with the motion — felt as a faint rattle under the sheet, the counter physically turning. No haptic on scroll, on rows, on close beyond a **6ms @ 0.50** seat.

**Sound**

- Silence by default. Mechanical set on: sheet = one plate slide (filtered noise, 200ms, −22dB); drum steps = matched counter clicks at −24dB. Nothing else on the sheet makes a sound, ever.

**Depth / material**

- The sheet is a recessed plate: ground one step below the screen it covers, drum windows recessed one step further (the sheet's only double-recess — justified: counters sit *inside* housings). Drum digits cream on ink drums; event rows letterpressed hairlines, event numbers in panel tone. Zero orange — the record commits nothing, so it earns no COMMIT fill. No green. No shadows.

### Exactly what lives where (the full placement table)

| Fact | Home | Notes |
|---|---|---|
| Sessions sealed / deflections absorbed / holds spent — lifetime totals | **Service-log sheet** (tally drums) | Monolith-native only; derived from the event store |
| Filed events (seals, completions, holds, peeks, jams, permission changes, routine attendance history) | **Service-log sheet** (event roll) | The record's only historical home |
| This week's hold remaining (1 − spent this week) | **Shield**, at the hold control | Operational state, not history — he acts on it there |
| Today's routine attendance word + NEXT / LIVE | **Shield**, on the routine row | Current-day state where routine decisions are made; history files to the roll at window close |
| Live deflection count during a running session | **Session chrome** (Deck running state / Shield) | The same counter the drum will inherit at filing — shown live because the machine absorbing hits *now* is present-tense feedback, not a record |
| One session's facts at its end (seal time, deflections, hold/fault lines) | **End-of-session receipt** | An *issuance* of the filed events at completion — a copy handed to him, not a second home; the store is the home |
| Per-app last-proof / last-jam line (service-position fragment, ⚑ D4) | **Shield row**, reading from the same store | A window into the record, not a record |
| Study minutes, hours, totals, streaks | **Nowhere in Monolith** | YPT owns study. The drums never count a minute. |
| Task completions, curios, badges | **Nowhere** | Not machine events; curios stay dead in every outcome |

**Standards check**

- *ARM cost:* zero. The sheet is unreachable from the arm path and inert during sessions; no door to it sits on the Deck's working surface (nameplate long-press is a deliberate 600ms act, and it is disabled while running).
- *Mid-session decisions introduced:* zero — the sheet cannot open mid-session at all.
- *Truth risk:* the design's whole shape is the mitigation — one store; drums derived from it; receipt demoted to issuance; operational slices (hold remaining, today's attendance, live deflections) are *different facts* from the filed history, each stated once, in the place it's acted on. The residual risk is scope creep (below).

**If Sarthak lands on (a) instead:** nothing above changes except the housing — the same drums, the same roll, the same store become the fourth tab's entire content, unexpanded. My form is deliberately housing-agnostic so his pick costs no redesign; what I am actually defending is the *contents discipline*, and it survives promotion intact. **If he lands on (c):** the store, drums, roll, and the service-position fragment (⚑ D4) all die; ⚑ D1 resolves to "jams are never filed," ⚑ D2's receipt line becomes the fault's only trace, and the operational slices in Shield/chrome remain (they are present-tense state, not a record — they survive (c) honestly).

### The strongest case against my own option

It deserves to be stated at full strength, because it might be right: **(b) is a compromise, and compromises about records rot.** First: a sheet is where facts go to be forgotten — if the Monolith-native record matters enough to keep truthfully, it matters enough to stand somewhere he will actually see it; buried under a Shield footer row and a nameplate long-press, the attendance roll will be opened twice and never again, and an unread log cannot do the one job that justifies it, which is showing him — at the moment he's deciding whether the machine earns its place — that it has been working. A tab costs the arm path *nothing* (tabs are inert during sessions too) and gives the record a sovereign home where one-fact-one-rendering is trivially enforced; my scattering — drums on a sheet, attendance words on Shield rows, a live count in chrome, an issuance at completion — creates four surfaces that must be kept in agreement forever, and every future feature will find it easier to add a fifth rendering than to check the store. Second: (b) satisfies neither camp. The kill camp still ships an append-only store of his behaviour — the data exists, growing daily, whether or not he looks at it, and "we kept it but hid it" is the worst answer to a man who asked *should we keep it at all*. Third, and sharpest for my own lens: real service logs work because a *technician* reads them on a schedule. Monolith has no technician. Its only reader is a tired student at 03:30 — and for that reader, a record he must remember to summon is functionally (c) with extra storage. If the panel believes the record will genuinely be *used*, the honest vote is (a). My (b) stands only on this: the record's real use is rare and specific — fault diagnosis, hold accounting, attendance truth when he questions a routine — and rare, specific uses are exactly what hatches and sheets are for. If that premise is wrong, my option is wrong with it.

---

## Mail / wall note

Inbox held two letters, both relaying Sarthak's citation-lift order verbatim — Orson (Wild) at start, Mara (Wild) arriving before wrap-up; both already carried in my assignment, both read and moved to `mail/opal/read/`. Inbox empty at this wrap-up (checked before finishing the Ledger revision). No mail sent; no seat needed an addressed word that the wall didn't say better. Posted two signed entries to the Commons wall: 05:22 IST staking this file's central claim — intent vs. proof as two separate parts — and 05:31 IST filing my Ledger verdict (b, the Service Log) for Marlowe's LEDGER-wild synthesis. The Ledger revision flags every dependency in place (⚑ D1–D5, mapped in the intro) and assumes the question dead nowhere and alive nowhere.
