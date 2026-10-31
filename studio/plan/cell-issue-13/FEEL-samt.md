# FEEL — STILLNESS: THE SETTLE, THE DESCENT, THE SURRENDER

SAMT, Feel Specialist 4, Wacht's cell — issue #13 (Sarthak-2007/
Focus-app, branch `agent/issue-13`). Slice: how the Session Room
FEELS at its three stillness thresholds — the settle that raises
it, the descent that dims it across the night, the battery
surrender that puts it to sleep. Audit authority:
`studio/plan/AUDIT-session-room.md` §3 A-2 + A-10(v), §4 E-2 as
ratified by Ren (V1 amended: ~50 Hz only while armed-undecided;
three-state REST / HELD / UNKNOWN; HELD suppresses both raise
paths; UNKNOWN and sensor-absent take the 20 s backstop alone).

STATE OF THE GROUND. Bauer's merge landed WHILE this audit ran:
my first read found `DeskClockSettle.kt` orphaned (no caller, no
sampler, no surrender driver); the final read (22:03 IST, files
stable across the last two checks) found the full wiring in
`MonolithViewModel.kt` + `MainActivity.kt`. Every verdict below
is against that final state. Files judged:
`app/src/main/java/com/example/ui/DeskClockSettle.kt`,
`app/src/main/java/com/example/ui/MonolithViewModel.kt`,
`app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt`,
`app/src/main/java/com/example/MainActivity.kt`. Lane reference:
`builds/flow-clock/Focus-app` (pre-amendment settle + test).
No code touched by this seat; no compile run, per orders.

## THE VERDICT IN ONE BREATH

The merged room has the right BODY and an unproven NERVOUS
SYSTEM. The arrival grammar is certainty-shaped — a REST raise
reaches a standing face in ~1.6–2.6 s from set-down, the
UNWATCHED arrival breathes (ground 260 Curve A, print 520
Curve C at +140), the room never clicks anymore (A-9 landed).
But the verdict that triggers all of it is granted on a single
one-second tick's reading of a ~0.4 s EMA, over a threshold with
no hysteresis, from a classifier that cannot say UNKNOWN —
so at the margins the phone will still seize a braced reader
on a breath-trough, and will refuse forever in a car. The
descent is honest in slope and dishonest in scope: it sinks
every timed span including a 25-minute afternoon session, and
its 60 s suspension ends in a one-frame curtain-drop. The
surrender's deadline arithmetic is ruled-correct and its fade
is a coin flip against the 1 s ticker — and when the fade is
missed, the flag clears into the fully lit Deck, not into
OLED black. Nine defects, all small, all priced below. With
F-1 + F-2 + S-1 + S-2 cured, stillness is the room's strongest
sense. Without them it is a well-dressed coin toss at 03:00.

---

## §1 · THE SETTLE — does the timing read as certainty?

### What is right, and why it feels right

- The latency budget of a REST raise is correct. Sampler EMA
  settles in ~0.4–1 s (VM:397), the ticker adds ≤ 1 s
  (VM:1154), the arrival adds 600 ms WATCHED / ~660 ms
  UNWATCHED (Overlay entry table). Total set-down → standing
  face ≈ 1.6–2.6 s. That is the tempo of a thing DECIDING:
  faster reads as a trigger, slower reads as a timeout. The
  20 s backstop now fires only where it should — UNKNOWN and
  sensor-absent (Settle:195–201) — so the "delay" feel is
  quarantined to the cases where the phone honestly knows
  nothing.
- HELD suppresses BOTH paths in the machine's law, checked
  before the cold-open and before the backstop
  (Settle:158–167). And suppression is SILENT: no print, no
  sound, no partial raise, no evidence on the face that a
  decision was ever pending. This is the whole answer to the
  brief's question: a braced reader feels HELD as respect
  precisely because nothing happens to him. The room's
  respect is indistinguishable from its absence — which is
  what respect from a machine has to look like at 03:00.
- The wake re-raise re-arms the sampler (VM:338,
  Settle:83–90) — the return is sensor-built, never
  time-only, as A-10(v) / Nacht's condition demanded.
- A-9 landed: the toggle's clock path is silent both ways
  (VM:1856–1860). The vigil opens and closes without a click.

### Defects

**F-1 (HIGH) — REST is sampled, not sustained: the seizure
survives at the breath's trough.**
`DeskClockSettle.kt:187–194` raises on the FIRST tick whose
`sensorVerdict == REST`. The verdict the tick reads
(VM:398–401) is the latest value of an EMA with τ ≈ 0.4 s
(0.95/sample at ~50 Hz, VM:397). There is no persistence
requirement anywhere: one tick — one reading of a sub-second
window — decides the session forever. A braced reader's
tremor is not constant: it breathes (4–5 s cycle), it ebbs
when he shifts grip, it ebbs as a braced hold steadies.
Each trough is a REST candidate, and the first trough that
coincides with a tick seizes the screen — in as little as
~1–2 s of holding still, FASTER than the 4–8 s seizure E-2
convicted the original V1 of. The amendment cured the
aliasing and kept the trigger-happiness.
CURE (exact): REST earns a dwell in the machine. Add
`restSinceMono` beside `heldSinceMono`: on a REST tick, if
`restSinceMono == 0` stamp it and return false; raise only
when `nowMono − restSinceMono ≥ REST_DWELL_MILLIS` (1,500 ms)
with every intervening tick also REST; any HELD or UNKNOWN
tick clears the stamp. HELD needs no dwell — suppression is
the safe direction and delay there costs nothing. 1.5 s of
sustained furniture-quiet still reads as instant against the
20 s it replaces; what it buys is that the phone must be
sure for longer than one breath. New constant + ~8 lines in
`tick`, pure, testable in the machine's own discipline.

**F-2 (HIGH) — No hysteresis, and UNKNOWN is unreachable from
data: refusal without end in a car, limbo at the threshold.**
`deskClockTremorVerdict` (Settle:236–243) is a single
threshold: energy ≤ 1.5 × floor → REST, anything above →
HELD. It returns UNKNOWN only when `bandEnergy` is null or
the floor is dead. But the sampler (VM:398–401) always passes
a non-null energy — so from real traces the three-state
verdict has only two states. Consequences, both felt:
(a) Broadband vibration — a car, a train, a desk sharing a
wall with a washing machine — produces band energy above the
floor with no hand present, and reads HELD: both raise paths
suppressed, indefinitely. E-2 scoped the car as UNKNOWN
(backstop); the merged classifier sends it to the one state
that never raises. The room refuses all night for a reason
that is not a hand.
(b) At the threshold's edge the verdict flaps HELD ↔ REST ↔
HELD at tick grain. A REST flap seizes (compounds F-1). An
UNKNOWN/non-HELD flap is worse in slow motion:
Settle:168–175 clears the held state AND the arm on ANY
non-HELD beat, and Settle:183–186 re-arms with
`armStartMono = nowMono` on that same beat — so every flap
restarts the 20 s backstop from zero. A marginal trace can
hold the session in limbo forever: never seized, never
raised, sampler never duty-cycled (the duty anchor,
`heldSinceMono`, resets with every flap).
CURE (exact): two changes, both in the pure layer.
(1) Dual thresholds in `deskClockTremorVerdict`: REST at
≤ 1.5 × floor; HELD at ≥ 2.5 × floor (new constant
`HELD_FLOOR_RATIO`); between them, return the caller's
previous verdict (pass it in — the function gains a
`previous: DeskClockVerdict` parameter; first call UNKNOWN).
A verdict that can only change by crossing a band cannot
flap on noise inside the band.
(2) HELD latches in the machine (Funke P-3(c)'s reduced
form): Settle:168–175 clears the held state only on a
positive REST verdict, never on UNKNOWN. UNKNOWN while held
is ignorance, and ignorance is not evidence the hand left.
With (1) + (2): flapping dies, the duty cycle engages, the
backstop can only be re-armed by a genuine state change.
(3) The car needs its second channel: the sampler's
differenced magnitude (VM:395–397) is structurally blind to
low-frequency sway — differencing deletes it. Track a sway
energy beside the band energy (variance of the gravity
vector's direction, or of a 0.5 s moving average of the
magnitude): HELD requires band-above-floor AND sway below a
ceiling; band high + sway high is a vehicle, not a hand →
UNKNOWN → the backstop E-2 promised it. ~15 lines in the
listener, one more parameter in the verdict function.

**F-3 (MEDIUM) — The cold-start verdict is REST by default.**
`tremorBandEnergy` initialises to 0.0 (VM:291) and the EMA
charges from zero (VM:397): for the first ~0.5–1 s after the
listener registers, a HELD phone's energy is still climbing
through the REST ceiling, and the verdict function happily
returns REST on a window that has not filled. If a tick lands
in that window — the session arms while he is already holding
the phone, e.g. a routine-born SLEEP arm in his hand — the
raise can fire off an empty window.
CURE: the listener counts samples; until ≥ 50 samples
(~1 s at SENSOR_DELAY_GAME) have arrived for the current
registration, the verdict stays UNKNOWN. One counter, reset
in `updateTremorSampler` on every register. (F-1's dwell
covers most of this once landed; the gate makes the first
second honest instead of merely lucky.)

**F-4 (MEDIUM) — The "noise floor" is a hardcoded guess, and
nothing in the tree proves it.**
VM:400 passes `noiseFloor = 0.02` — an uncalibrated literal
in (m/s²)² of successive-difference magnitude, wearing the
name of a measured quantity. Accelerometer noise varies
severalfold across devices; on a noisy unit the desk itself
can sit above 1.5 × 0.02 (permanent HELD, the clock that
never comes — read by its owner as broken, not respectful),
and on a quiet unit a relaxed one-hand hold can sit below it
(REST in the hand). There are no bench fixtures in this tree
to catch either: the lane's `DeskClockFlowTest.kt` was not
carried into the cell (the cell's test source set has no
DeskClock test at all), and §7 debt 9's seven named traces
(braced-two-hand, one-hand, chest-rest, lap-rest + desk /
stand / pocket) exist nowhere as data. Until they do,
"tuned" is an assertion, per Funke P-1.
CURE: rename the literal to what it is
(`TREMOR_ENERGY_THRESHOLD`) so no future reader trusts it as
a measurement; adopt Funke P-1 whole (fixtures + replay
harness, ±20 % margin assertions) as this slice's proof debt
§5.1; and evaluate the floor per registration where lawful —
the median EMA over the first second after a known
interaction-free beat is a candidate self-calibration, but it
ships only if the fixtures say it beats the constant.

**F-5 (LOW, priced-and-named, not a block) — The duty cycle's
set-down wait.**
Once HELD has persisted 30 s, the sampler runs 6 s in 30 s
(Settle:103–110, VM:372–418). A man who held the phone for
ten minutes and then sets it down inside an off-window waits
up to ~24 s + a probe window for a raise the sensor path
owes him in ~2 s. This is the audit's own floor, knowingly
priced — I do not re-open it — but its feel must be named:
it is the one moment the amended V1 reads as the OLD V1's
delay, arriving exactly when he did the right thing. Funke
P-3's reduced form (change-detection wake + the F-2 latch)
retires it; recommended for the next merge, not this one.
Housekeeping in the same lines: `tremorBandEnergy` and
`tremorVerdict` are never reset when the session changes
(VM:286–291 are VM-lifetime fields) — a new session inherits
the old session's charged energy and verdict for its first
ticks. Reset both in `updateTremorSampler` when the armed
session id changes. Three lines; fold into F-3's cure.

**F-6 (LOW) — The pocket lost its acquittal.**
Fen's bench acquitted the pocket on proximity alone; the
merged sampler takes no proximity input at all (VM:385–412
reads only the accelerometer). A phone standing still in a
pocket — magnitude quiet, delta at the floor — reads REST
and raises into fabric: invisible, but it arms keep-awake
and starts the surrender clock against a screen nobody can
see. Not a seizure; a small waste wearing the cure's name.
CURE: if a proximity sensor exists, proximity-covered forces
the verdict to UNKNOWN (the backstop E-2 scoped) — never
REST, never HELD. One gate in `updateTremorSampler`.

### The braced reader, walked through

He arms, braces the phone two-handed against his chest, and
reads for forty minutes. Merged behaviour, post-cure: HELD
within a second, both paths suppressed, sampler duty-cycled
after 30 s, and — the part that matters — NOTHING on the face
ever tells him a decision was pending. There is no "refusing"
to feel, because refusal requires an event and HELD is the
absence of one. Pre-cure (the tree as it stands tonight), his
first long breath inside the first minute is a live seizure
risk (F-1), and if his grip couples tremor weakly to the
magnitude axis his hold may never read HELD at all (F-4).
The suppression as DESIGNED is respect. The suppression as
WIRED is respect with a trapdoor.

---

## §2 · THE DESCENT — is the pace honest across 7.5 hours?

### What is right

- The shape is honest. `deskClockPrintAlphas`
  (Overlay:127–150) is linear in the named variable,
  span-elapsed fraction: across a 7.5 h SLEEP span the hero
  moves 0.55 → 0.36 at ~7 × 10⁻⁶ alpha per second-tick —
  no visible stepping, no acceleration, no point at which
  the night suddenly deepens. A 03:00 glance finds the night
  it actually is (Nacht A-N5, acquitted and confirmed here).
- The floors are the audit's: number lines floor at 0.50,
  hero and tear rule sink together to 0.36, the exit line
  excepted to its 0.40 floor (Overlay:134–148). The tear
  rule takes the hero's alpha, so the room dims as one
  object, not as layers arguing.
- The dawn ramp (A-3) is continuous with the descent: at
  60 s remaining its factor is 0 — the descended value,
  untouched — and it reaches standing exactly as the span
  ends (Overlay:142–146). No seam, no snap. Information and
  legibility peak together, as Nacht ruled.

### Defects

**D-1 (HIGH) — The descent fires on every timed span; the
ruled scope is the night.**
Overlay:276–283 computes the fraction for ANY active timed
session. But the audit's room wears the descent in exactly
one state — §1's inventory lists it under SLEEP (charger),
and E-3 treats descent off the SLEEP path as an EXTENSION
reserved for the merge after this one, precisely so A-2's
floors land on the ruled scope first. As merged, a man arms
a 25-minute session at 15:00 and watches his working clock
sink to 0.36 / 0.50 by 15:25 in full daylight; a two-hour
evening span spends its second hour at vigil light while he
is awake and using the face as an instrument. The descent's
meaning — "the night is deepening" — becomes false: it now
means "a timer is running," and a signal that fires for
everything signifies nothing. This is the descent's one real
dishonesty, and it is a scope bug, not a pace bug.
CURE: gate the fraction by the same derivation the
stand-down uses — the SLEEP-routine row (the
`isSleepLoadout` path inside `finishKindOf`,
ReceiptState.kt:88): for any other timed row the fraction is
0 f until E-3's night-progress extension lands in its own
merge and changes the trigger, not the shape. The dawn ramp
and suspension inherit the gate unchanged.

**D-2 (MEDIUM) — The stopwatch descends against an invented
span.**
Overlay:280–281: for a stopwatch the fraction is
`elapsedSeconds / 1200f` — a 20-minute span that exists in
no ruling, no filing, no constant with a name. A stopwatch
hits the hero floor at 20 minutes and sits at vigil alphas
for the rest of its run, however long, at whatever hour.
The stopwatch face was the audit's "most honest state on
the face" (§1) with no descent specced for it; a man timing
an open-ended evening session gets a night face by 20:20.
CURE: stopwatch fraction is 0 f, always. The stopwatch is
the awake instrument; if a future night extension wants it,
it keys to night progress (E-3's variable), never to a
denominator invented at the call site.

**D-3 (MEDIUM) — The 60 s suspension ends in a one-frame
curtain-drop.**
A-2(iii)'s letter is implemented faithfully — suspension
holds standing alphas (Overlay:138–141), then "resumes from
the span-progress value." The letter permits the step; the
eye does not. At suspension expiry the hero steps in a
single frame from 0.55 to the descended value: −0.095 at
half-span, −0.19 at span's end (a wake re-raise near the end
of a SLEEP span is exactly Moench's 03:00-glance case — he
raises the face, reads it bright, and at +60 s, still
looking, the room drops its curtain mid-glance). The descent
spends 7.5 hours moving 0.19 and then, once per raise, moves
it again in 16 ms. Every other motion in this room is eased
on a named curve; the resume is a cut.
CURE: resume on the room's own curve. `deskClockPrintAlphas`
gains a `resumeFraction: Float` input (0 at expiry, 1 at
+520 ms, Curve C-shaped by the caller or by an easing inside
the function): while `resumeFraction < 1`, each alpha lerps
suspended-value → span-progress-value by it. The overlay
derives it from the same stamp it already keeps
(Overlay:289–296): `elapsedRealtime() − raisedAtMono −
60_000`, clamped to [0, 520]. ~10 lines across the two sites;
the pure function stays pure and the step becomes the
520 Curve C exhale the surrender already uses — one gesture
vocabulary for dimming, everywhere.

**D-4 (MEDIUM) — The door invariant is false at standing
light, and the comment beside it says otherwise.**
Overlay:147–148 computes exit as `max(0.40, hero × 1.10)`
capped at `DESK_CLOCK_EXIT_ALPHA = 0.58` (Overlay:84). At
standing hero 0.55 the invariant demands 0.605; the cap
returns 0.58 — ratio 1.055, not 1.10. The invariant therefore
fails for the first ~12 % of every span, for every suspended
minute, and at the dawn ramp's end — i.e. at most of the
moments a man is actually looking at the face. (Mond's
difference form fails too: 0.58 − 0.55 = 0.03 < 0.04.) The
header at Overlay:95–100 pins the ratio "kept by
construction"; at standing light it is not kept at all.
Felt consequence is small — the exit is still the brightest
print — but A-2(iv) exists because two acquitted constants
composed into a lie once already; shipping a second
composition that violates the pinned form, under a comment
claiming it cannot, is how the first one happened.
CURE: one constant. `DESK_CLOCK_EXIT_ALPHA` 0.58 → 0.61
(= 0.55 × 1.10, rounded up): the invariant then holds at
every fraction by construction, as the comment claims.
Cost, named for Wacht's ruling: Salz's desk puts the exit at
0.58 ≈ 0.246 relative luminance against Nacht's 0.25 ceiling
for the night face; 0.61 lands ≈ 0.259 — one verb line, ~4 %
over the ceiling, on the one line whose whole job is to be
findable in a sleeping hand. This seat recommends paying it:
the door outranks the ceiling. The alternative — hero
standing 0.55 → 0.527 so 0.58 satisfies the ratio — dims
every print on the face to rescue one line's arithmetic,
and is rejected here.

**D-5 (LOW) — The banner slab does not descend.**
The banner's TEXT floors at 0.50 with the other number
lines, but its background is `NightColorSurfaceBright` at
full strength whenever a line is present (Overlay:469). At
deep descent the hero sits at 0.36 while a full-strength
#362C27 rounded card glows behind the banner — by area and
by luminance the brightest object on the face is furniture,
not print. A-2's principle is dim BY AREA; the slab is the
largest lit area in the room and it is exempt.
CURE: the background descends with the hero. Compose it as
`NightColorSurfaceBright.copy(alpha = alphas.hero /
DESK_CLOCK_PRINT_ALPHA)` — 1.0 at standing, ≈ 0.65 at the
floor — so card and print sink as one object. One line.

### The 7.5-hour walk, as merged

22:30 arm (charger): standing light. 00:00: hero ≈ 0.51 —
a sleeping glance finds a room slightly deeper than the one
he left, which is the truth. 03:45, half-span: 0.455; he
wakes, taps, gets standing light for 60 s — then (D-3) the
curtain-drop. 06:00: 0.38. Final minute: the dawn ramp walks
everything home and the face ends at full standing light
exactly as the span does. The slope never lies. What lies
is that the same slope ran at 15:00 that afternoon on a
25-minute span (D-1), and that the stopwatch he ran at
dinner sank too (D-2). Pace: acquitted. Scope: convicted.

---

## §3 · THE SURRENDER — the room goes to sleep in its own curve?

### What is right

- The deadline is the ruled formula, wired in the VM's
  session machinery where A-10(v) put it:
  `max(settle, last interaction, last charger disconnect) +
  10 min` on battery (VM:1191–1195, Settle:263–277). A plug-in
  before the deadline cancels (VM:1209–1211); after it, only
  the wake path resurrects, and that path re-arms the
  sampler (F-acquittal §1). Keep-awake ownership genuinely
  moved — MainActivity:258–263 applies/clears
  FLAG_KEEP_SCREEN_ON from the VM's flow; the overlay touches
  no flag.
- The fade's SHAPE in the overlay is exactly as designed:
  print only, 520 Curve C to 0, ground holds black
  (Overlay:256–263). When it runs, it is beautiful — the
  room exhales in Curve C, the same curve it descended on,
  reversed. One gesture vocabulary, kept.

### Defects

**S-1 (HIGH) — The fade races a 1 s ticker, and loses about
half the time.**
The VM sets the fade flag only while
`deadline − 520 ≤ clockNowMono < deadline` (VM:1200–1202),
evaluated on the per-second ticker. The window is 520 ms
wide; the ticker's grain is 1,000 ms. Whether any tick lands
inside the window is a coin flip on phase: in roughly half
of all surrenders the flag is never set, and the print is
CUT at the deadline beat — the precise vanishing A-10(v)
was written to forbid ("the room does not vanish; it goes
to sleep in its own curve, reversed"). In the other half,
the fade starts up to 520 ms early relative to the clear
and is either still running when the flag clears (cut
mid-exhale) or completes and sits black for a beat (the one
harmless outcome). A designed gesture that lands by phase
luck is not a gesture; it is a sometimes-gesture.
CURE (exact): make the yield two-phase and scheduled, not
window-tested. On the first tick at or after
`deadline − SURRENDER_FADE_MILLIS`: set
`_deskClockSurrenderFade = true` and launch the clear in
`viewModelScope` with `delay(deadline − clockNowMono)` —
at the deadline, stamp yielded, clear visible + keep-awake,
reset the fade flag. The ticker discovers the moment; a
coroutine keeps the appointment. (A 100 ms surrender loop
for the final 10 s reaches the same end with more machinery;
the scheduled clear is the smaller cure.) Proof is
statistical — §5.3: ten consecutive battery surrenders must
fade ten times.

**S-2 (HIGH) — The charger-disconnect stamp is written only
while the clock is INVISIBLE: the 03:00 unplug yields at
once.**
The disconnect edge is maintained in the `!visible` branch
(VM:1212–1224). But A-10(v)'s worked example is an unplug
while the clock STANDS: plugged at 22:30, face up all night,
unplugged at 03:00. On that beat the visible branch
(VM:1187–1208) reads `chargerPlugged() = false` and computes
the deadline from `lastChargerDisconnectMono` — which was
never stamped, because every beat of the plugged night ran
the visible branch and the stamp lives in the other one.
With the disconnect stamp at 0, the max falls to the settle
and interaction stamps — hours old — and the deadline is
already past: the face yields IMMEDIATELY on unplug (no
fade: the S-1 window is behind it too). The exact failure
the amendment exists to prevent — "an unplug at 03:00 no
longer kills the face at a deadline hours past" — is
reproduced by the stamp's address.
CURE: edge-detect the plug in BOTH branches, before the
visibility split: keep `wasPlugged` beside the stamp; on any
beat where `wasPlugged && !pluggedNow`, set
`lastChargerDisconnectMono = clockNowMono`; on any beat
where plugged, leave the stamp (a plug-in cancels by the
plugged branch's own logic, not by erasing history). ~6
lines at the top of the desk-clock beat, VM:1151's
neighbourhood. The 03:00 example then yields at 03:10, faded.

**S-3 (MEDIUM) — The yield clears into the lit Deck, not
into OLED black.**
At the deadline the VM sets visible = false and keep-awake
= false (VM:1203–1207). The overlay uncomposes — and what
stands in the dark bedroom is the Deck, at full Deck light,
for the whole of the OS screen timeout (30 s on many
devices; minutes if he ever set it long for study). A-10(v)'s
invariant was that cut and off are indistinguishable because
the flag clears INTO black. As merged, the surrender's last
act is to reveal the brightest surface in the app and leave
it standing over a sleeping man until the OS puts it out.
The print kept its promise; the room did not.
CURE: yield to black, not to the Deck. While a session is
yielded (the VM already tracks exactly this in
`deskClockYieldedSessionId`, VM:310) and still live,
MainActivity composes a plain true-black full-screen box
ABOVE the Deck (no print, no verb, consumes nothing — the
existing wake interaction path, VM:329–348, un-yields on
his first touch and re-raises SEEDED). The panel may sleep
whenever the OS decides; what it shows until then is OLED
black, which is the audit's invariant restored by
composition instead of by timing. This cure also absorbs
the merged code's deviation from A-10(v)'s letter (the fade
is keyed to the designed deadline, not to the readable
system timeout, VM:1196–1199): keyed-to-deadline is honest
exactly when black persists past the fade, which is what
this cure makes true.

**S-4 (LOW) — The wake tap is spent twice.**
The wake path is `noteUserInteraction` (VM:333–347): the
first interaction after a yield re-raises the clock SEEDED,
at full alpha, frame one. If that interaction was a tap
aimed at the Deck — he unlocked the phone in the morning
meaning to check the day, and his first in-app touch lands
on a control — the tap both fires its target AND covers the
screen with the clock. During a SLEEP span his morning
intent is almost certainly the clock's own dismissal, so
the harm is small; it is named because A-4's SEEDED snap
gives him no frame in which to stop it.
CURE: arm the wake re-raise on the Activity's first resume
after the yield, and consume exactly one interaction as the
wake — if that interaction was already consumed by a Deck
control, let the Deck keep it and re-raise on the next.
Wacht may price this as polish and defer it; it must not
defer silently.

### Acquittal inside the surrender

The surrender also fires for a hand-raised clock on battery
in daylight (arm at a desk, unplugged, raise by chip, +10 min
still → yield). That is the ruled weather, not a bug: the
wake tap returns the face in one touch, SEEDED, and nothing
is decided against him. Recorded so no future panel "fixes"
the room's one consistent battery law.

---

## §4 · ACQUITTALS (feel, this slice)

- Entry totals: UNWATCHED ≈ 660 ms (a held breath, acquitted
  for exactly that witness state), WATCHED 600 ms, HAND
  440 ms, SEEDED frame-one. The watched black-slab cure
  (A-4) holds: print lands only on completed black in every
  row. A machine raise at 03:00 arrives like a breath, not
  a slap.
- A-9 silence on the clock path (VM:1856–1860). The seal's
  Throw and machinery voices untouched, as ruled.
- The dawn ramp's continuity (Overlay:142–146) and the
  descent's per-tick smoothness at any span length.
- HELD's silence as a design: suppression with no surface
  evidence is the correct emotional register for a machine
  that has decided to wait.

## §5 · PROOF DEBTS THIS SLICE ADDS (to audit §7)

1. Funke P-1's bench, adopted as this slice's debt: the seven
   named traces (desk, stand, pocket, braced-two-hand,
   one-hand, chest-rest, lap-rest) replayed through the
   classifier + settle machine, with ±20 % energy-margin
   assertions — run BEFORE any threshold in F-4 is called
   tuned. (Extends audit debt 9 from a bench session to a
   fixture set.)
2. A braced-reader capture per F-1: two-hand braced hold,
   60 s, breathing normally — the merged build must show NO
   raise; the same hold with a deliberate 2 s full stillness
   inside it must still show no raise (the dwell's whole
   point, photographed).
3. Ten consecutive battery surrenders, timed: the 520
   Curve C fade must complete before the clear in 10 of 10
   (S-1's cure is statistical or it is nothing), and frame
   +1 s after the clear must be true black, not Deck (S-3).
4. The 03:00 unplug, simulated on the merged build: charger
   pulled mid-SLEEP with the clock standing — yield at
   +10 min from the unplug, faded, never at the unplug
   (S-2; this is A-10(v)'s own worked example, run as a
   test, not retold as a comment).
5. Suspension resume frames at +59 s / +60 s / +60.5 s after
   a mid-span re-raise: with D-3 cured, the +60.5 s frame
   sits on the resume curve, not on the descended value.
6. One descended L05 frame on the SLEEP path only (audit
   debt 4, re-scoped by D-1): past 50 % of a SLEEP span,
   hero + rule + one number line + the banner slab (D-5) at
   their floors, photographed.

## §6 · PRICE SUMMARY

F-1 SMALL (one constant, ~8 lines, pure) · F-2 SMALL–MEDIUM
(dual threshold + latch pure; sway channel ~15 lines at the
listener) · F-3 SMALL (folds into F-5's reset) · F-4 words +
the P-1 debt (MEDIUM, bench time, already priced by Funke) ·
F-5 LOW, deferred by name · F-6 SMALL · D-1 SMALL (one gate)
· D-2 ZERO–SMALL (one branch returns 0 f) · D-3 SMALL (one
input, two sites) · D-4 ZERO (one constant; the luminance
excess is the priced cost, Wacht rules) · D-5 ZERO–SMALL
(one line) · S-1 SMALL (one scheduled clear) · S-2 SMALL
(~6 lines) · S-3 SMALL (one black box + the yielded flow
already exists) · S-4 LOW, deferrable by name only.
No new surface, no DB change, no verb on the read face.

---

## CLOSING — THE ONE FEEL DECISION I DEFEND HARDEST

**REST must be sustained, not sampled (F-1).** Everything
else in this slice is negotiable — thresholds can be tuned,
the resume can ease or step, the fade can be scheduled two
ways. This one is the difference between the room's promise
and its betrayal, and it is not a tuning question: a verdict
that can be reached inside a single breath can be reached
BY a single breath, and the man it seizes will not
experience a threshold error — he will experience the phone
taking his screen while he was reading, which is the one
event this entire lane, V1, its amendment, and this audit
exist to prevent. Certainty is not a reading; certainty is
a duration. A desk is still for minutes. A hand is still
only between breaths. The machine that cannot tell those
two stillnesses apart has not decided anything — it has
twitched, quickly, in a well-lit animation. Give REST its
1.5 seconds of proof, and every other cure in this file
becomes refinement. Without it, they are decoration on a
seizure that learned to knock first.

— SAMT, Feel Specialist 4 (Wacht's cell, issue #13),
Oct 30, 2026. No commit, no push, no post: Wacht carries
this to the repo.
