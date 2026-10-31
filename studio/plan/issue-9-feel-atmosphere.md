# ISSUE #9 — FEEL 4 (ATMOSPHERE): PARK, DAY AND NIGHT

*Feel 4 (atmosphere), issue-9 cell (for Hof). Ground: the cell tree's
built `ParkDrawer.kt` (scrim, sheet, board, FIX + CHECK keys,
`NOT PROVED` line), `GuardLine.kt` (`GUARD — NOT CHECKED`),
`Color.kt` / `Theme.kt` (the two schemes), `MonolithCurves.kt`
(KIN-DRAWER), `MainActivity.kt` (`isNight = effectiveNight`,
park-open sweep), `AUDIT-park.md` (Hof's rulings stand — nothing
here re-opens them), `issue-9-plan.md`, `issue-9-designer.md`,
`issue-9-finisher-keys.md`, `issue-9-finisher-board.md`.
Paper only — nobody compiles.*

*Scope, exactly: PARK's atmosphere — what the room feels like,
day and night. Not the board's print tiers (Finisher 1), not the
keys as objects (Finisher 2), not touch or motion as mechanics
(Feel 1–3 own those; where they touch weather, this spec states
the felt constraint and stops). Every item is **SPEC-ONLY**
(law around code that already honours it) or **PATCH-READY**
(exact delta, anchored to the built file).*

*House law applied throughout: one tone (§8.10), orange zero,
never green or near-green — every tone below is a locked token
and satisfies the palette's own rule (green < max(red, blue));
no imagery, no icons; Sora / Atkinson Hyperlegible / Anonymous
Pro off the existing ladder only. The bar is commercial level
and above, and on this screen the bar has a specific name:*

> **PARK is the drawer a tired man opens at 1 AM. Calm is not
> its styling. Calm is the feature. Anything that makes the
> drawer feel busier than the room it covers has failed, however
> true, however useful, however well-built.**

---

## §0 · THE ATMOSPHERE THESIS — A DRAWER, NOT A DASHBOARD

A drawer and a dashboard can print the identical nine lines.
The difference is weather:

- A **dashboard** wants to be watched. It refreshes, badges,
  celebrates, ages visibly, and rewards checking with checking.
- A **drawer** wants to be believed and put away. It is opened,
  read at a glance, acted on if needed, and shut. Its highest
  compliment is that the man forgets it between openings.

Everything already acquitted in PARK serves the drawer: flat
print on one ground, re-strikes instead of animations (§8.10),
no banner on any return (audit §7), the seal turning the whole
surface to read-only print. This spec's job is to hold that
weather against the one new pressure in this issue: the CHECK
key turns proof into a *ritual the man can repeat* — and a
repeated proof ritual is exactly how a drawer becomes a
dashboard (§5). The other three sections are the weather
itself: the scrim (§1), the two dresses (§2), and the feel of
not-knowing (§3–§4).

---

## §1 · SCRIM AND GROUND — ONE ROOM, DIMMED, NEVER REPLACED

### A-1 · The relationship, as built — **SPEC-ONLY**

The numbers, from `ParkDrawer.kt` as built:

```kotlin
// fraction: 1 = shut (sheet below the host), 0 = seated open
.background(Color.Black.copy(alpha = 0.32f * (1f - fraction.value)))
// sheet:
.clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
.background(scheme.background)          // the ground itself
```

Four facts do all the atmospheric work, and all four are law:

1. **The sheet is the ground.** The sheet's fill is
   `scheme.background` — day Ground `#E4DFDA`, night Ground
   `#171412` — the same tone as the screen it rises over
   (§8.10: exactly one surface tone, 0dp elevation, no shadow).
   PARK never arrives as a card, a panel, or a foreign object.
   It is the room's own floor, lifted. This is why the drawer
   can be opened at 1 AM without a visual event: nothing new
   enters the room; the room rearranges.
2. **The scrim is the only separator.** With no shadow, no
   elevation, and no second tone available, separation between
   sheet and screen is carried entirely by (a) the scrim
   darkening what is behind, (b) the 16dp top corners revealing
   scrimmed screen at the two corners, and (c) the grab strip.
   The scrim is therefore not decoration — it is the drawer's
   only depth cue, and its calibration *is* the atmosphere.
3. **Scrim and travel are one quantity.** Alpha is
   `0.32 × (1 − fraction)` — a pure function of the sheet's
   position, sampled from the same `fraction` the offset reads.
   There is no independent fade, no lag, no lead. Rising on
   280 SEAT (`DRAWER_OPEN_MS`, `EasingSeat`), the room dims
   exactly as fast as the sheet lands; leaving on 160 THROW,
   it brightens as the sheet is thrown. During a cap drag the
   coupling is 1:1 with the finger — the room dims under his
   hand at hand speed. Any patch that animates scrim on its
   own clock, however smooth, breaks the physical claim and
   reads as UI. **Do not decouple them.**
4. **The frame never moves.** PARK is hosted in the viewport,
   under the header band, above the rail frame (file header,
   chrome C4). The band and rail keep their geometry and their
   light while the drawer travels. The dimming happens *inside*
   the room, not to the room's architecture — the man is never
   plunged into a modal void; the header's Guard line stays
   legible above the sheet for the whole visit, which is what
   lets §3's `NOT CHECKED` and the drawer's board be read as
   one continuous statement.

The travel, felt in three frames:

| Travel | Scrim | What it feels like |
|---|---|---|
| Shut (`fraction 1`) | `0.00` | No drawer. The screen is itself. |
| Mid-rise (`fraction ~0.5`) | `~0.16` | The screen recedes half a step; the sheet is arriving, not yet a place. Rows are inoperable (`interactive = visible && settled && !isSealed`) — nothing half-works half-risen. Atmosphere and honesty coincide: a room that is still moving offers no verbs. |
| Seated (`fraction 0`) | `0.32` | Full rest. The screen behind is present but demoted — readable as *where he was*, no longer competing. Exactly two rest heights exist; the drawer is never left parked at a poetic half-height. |

Dismissal path (c) — a tap on the scrim — is an atmosphere
commitment as much as an input: the dimmed region must never
contain anything that looks tappable, pressable, or informative.
As built it is bare `Color.Black` at travel alpha with
`indication = null`. Keep it bare. The dimmed room is for
putting the drawer away, nothing else.

### A-2 · The day scrim — **SPEC-ONLY** (calibration acquitted)

Day arithmetic: Ground `#E4DFDA` (228/223/218) under 32% black
lands at roughly `#9B9691` — a warm grey, still inside the
room's warm family. The sheet at full `#E4DFDA` stands one
clear, gentle step above it: separation is unmistakable at a
glance and nowhere theatrical. 0.32 is the right day weight —
less would leave the Deck's print arguing with the board's;
more would turn a drawer opening into a screen failure. No
change.

### A-3 · The night scrim — **SPEC-ONLY**, with one device measurement owed

Night arithmetic is tighter, and this spec names it plainly:
Ground `#171412` (23/20/18) under 32% black lands at roughly
`#0F0D0C`. The sheet at `#171412` is then only a whisper
lighter than its surround — the weakest edge in the whole
drawer, at exactly the hour the drawer exists for.

**Ruling: the whisper is correct, and 0.32 stays in both
dresses.** Night's ground has already done the dimming; a
heavier night scrim would double-darken the room toward a
void and make the sheet read as a lit panel floating in
black — dashboard weather, and a small floodlight at 1 AM.
PARK at night should feel like the *same dark room*, its
drawer distinguishable by form — the corner radius against
the scrim, the grab strip's `onSurfaceVariant` grip
(24dp, 28dp pressed), the ruled hairlines at
`onBackground` 18% — not by a luminance event.

What is owed is a measurement, not a patch: on device, at
night dress, mid-rise and seated, confirm the sheet's top
edge and corners are findable in peripheral vision without
hunting. If — and only if — that check fails, the priced
fix is a night scrim of `0.44f` (same black, same formula,
`isNight` branch at the one call site), never a sheet tone
change: the ground is locked, the scrim is the only lawful
variable. Until the measurement fails, no code moves.

### A-4 · The seal changes operability, never weather — **SPEC-ONLY**

Under the seal the drawer — if open — is print: both keys
absent (Finisher 2 §8), sleds touch-swallowed, board readable.
The scrim, the ground, the travel, and the dresses do not
change at the seal. A man who leaves PARK open across the
20:00 SLEEP seal should notice that the verbs left, not that
the room changed. Atmosphere is continuous across the seal;
only permission ends. Protect this in any future patch: no
seal tint, no seal dim, no "locked" dress exists or may be
invented for this surface.

---

## §2 · DAY AND NIGHT DRESS — THE BOARD AND THE TWO KEYS

### A-5 · What "night" means here — **SPEC-ONLY**

PARK has no clock and no manual toggle. `isNight` arrives as
`effectiveNight = darkTheme || sealed` (`MainActivity`), and
`darkTheme` is derived from the one theme evaluation
(`MonolithViewModel.evaluateTheme`): a session active →
NIGHT; else an enabled SLEEP window active → NIGHT, owned
and named by that routine; else DAY, owned by nothing. The
THEME row inside the drawer prints that same evaluation
(`AUTO · NIGHT — SESSION ACTIVE` / `AUTO · NIGHT — SLEEP
{HH:mm}` / `AUTO · DAY — NO ROUTINE OWNS IT`), so the dress
and its explanation can never disagree.

Two atmospheric consequences are law:

- **The dress change is a 0ms cut, never a sunset.** The
  evaluation re-derives on a 1-second stamp and the scheme
  swaps on its frame (§8.10 theme ruling). If a routine
  boundary lands while PARK stands open, the drawer re-prints
  in the new dress instantly — no crossfade, no dimming
  animation, no dusk. A fading drawer would be weather
  *theatre*; a re-struck drawer is weather *fact*, in the
  same print grammar as every word on the board.
- **At 1 AM the drawer is almost certainly in night dress —
  by ownership, not by hour.** Either his SLEEP routine owns
  the night or a session does. The night dress below is
  therefore not the exception case for this screen; it is
  the primary case, and it is finished first in this spec's
  acceptance (§6).

### A-6 · The board's dress, both schemes — **SPEC-ONLY**

The board is print on the ground (§8.10): no fills, no chips,
no zones. Its entire wardrobe is ink tiers on one tone:

| Element | Day | Night |
|---|---|---|
| Ground (sheet + screen) | `#E4DFDA` warm stone | `#171412` |
| Row name (`PillBadgeTextStyle`, Anonymous Pro Bold 10sp, +2.0) | `onSurfaceVariant` `#5F5856` | `onSurfaceVariant` `#776A60` — the true mute tier, 3.5:1 over ground |
| Word `GRANTED` / `RUNNING` (quiet) | `onSurfaceVariant` `#5F5856` | `onSurfaceVariant` `#776A60` |
| Word lack — `MISSING`, full ink (Atkinson Bold 15sp, +0.5) | `onBackground` `#2C2A29` | `onBackground` `#D2C6BB` — the 11:1 body tier, deliberately *not* the ~16:1 shout the old night text used |
| Hairline (1dp ruling) | `onBackground` at 18% | `onBackground` at 18% — alpha on the scheme's own ink, so the ruling halves toward its own ground in both dresses by construction |
| `LAST PROVED` instant (Anonymous Pro Bold 15sp) | `onBackground` | `onBackground` |
| Plate head / body (Anonymous Pro 10sp, furniture) | `onBackground` / `onSurfaceVariant` | same tokens — the plate is byte-identical in day and night (file header law); only the ink tiers flip with the scheme, as all print does |

Read the night column as a single decision: **night lowers
the ceiling, not just the floor.** The ground drops to near-
black, but the loudest ink also steps *down* (cream body tier
at 11:1, a genuine mute at 3.5:1 where the old scheme had
none). A proved board at night is a page read by low light:
names and good words in the mute tier, only a measured lack
at full ink. `STALE`'s half-ink third dress (audit P-4;
Finisher 1 F-B1/F-B2 — `onBackground` at 0.60) belongs to
this same logic and this spec adopts it as atmosphere law:
staleness is a *dimming*, the one board state whose dress is
literally the light going off a word. When Finisher 1's
patch lands, day and night both inherit it through the
alpha, with no token swap — do not let any later patch give
STALE a colour of its own.

### A-7 · The two keys' dress, both schemes — **SPEC-ONLY** on tokens, **PATCH-READY** on the ladder

The keys are the drawer's only objects (FIX the board's
first, CHECK its second — Hof P-1), and they share one body
(Finisher 2 §1): 48dp, radius 8dp, `PrimaryButtonTextStyle`
(Sora Bold 15sp). Their fill is `inkControlFill()` /
`onInkControlFill()` (`MonolithCustomComponents.kt`), whose
night behaviour is the single most atmospheric decision in
the drawer:

| | Day | Night |
|---|---|---|
| Key fill | `onBackground` `#2C2A29` — the ink itself | `#4A423C` — the cap tone (`NightColorSurfaceSheet`, 1.86:1 over ground) |
| Word on key | `#FBF1EC` | `#FFE4CF` cream |
| Why | By day the key *is* ink: the darkest mass on a light ground, findable instantly. | At night the ink token flips to cream `#FFE4CF`; a cream-filled 48dp full-width key would be the brightest mass in the app — a floodlight in the quietest room. So night inverts the *relationship*, not the token: a warm dark step carries the key, and cream is demoted to the word alone. The key stays the most solid object in the drawer and stops being the brightest. |

Neither key branches on `isNight` for dress — the fill
functions read the scheme's luminance; `isNight` exists in
this drawer only inside the knock's amplitude (§2, A-8).
Orange is zero on both keys in both dresses, including
press states; green does not exist in this file.

**The ladder — PATCH-READY (atmosphere's first patch).**
As built, CHECK wears the ink dress *permanently* — the
same full mass as FIX, fresh board or stale. Stacked, the
two keys are 96dp of ink plus their gap: the drawer's only
objects doubled, permanently lit, on the screen whose whole
claim is quiet. A control that is always at full volume
teaches the eye to live on it — dashboard behaviour (§5).

Apply **Finisher 2 §4.1 verbatim** (the `checkNeeded` /
`checkInk` derivation beside the built `checking`
derivation; print dress = transparent ground, 1dp `outline`
border, word in `onBackground`; ink dress exactly when a
row is STALE or UNMEASURED or a sweep is in flight; MISSING
does *not* promote CHECK — its answer is the FIX key above
it). This spec adds the atmosphere reading that makes the
patch non-optional for this seat: **the dress ladder is how
CHECK asks.** In print dress it is furniture that happens to
be pressable — present, honest, ignorable. In ink dress it
is the room's one raised voice, raised only when pressing
it is the answer. Dress swaps are instant at the state
boundary (fills do not crossfade in this app) — promotion
must never *pulse, breathe, or animate* toward him. A
pulsing CHECK is a notification wearing a key's body; cut
on sight, now and in every future patch.

### A-8 · The sound dress — **SPEC-ONLY**, one imbalance flagged

Sound is atmosphere here, and it already has a night dress —
the drawer's only direct `isNight` branch, identical in both
keys as built:

```kotlin
soundEngine?.playSeatKnock(durationMs = 10,
    amplitude01 = if (isNight) 0.06f else 0.1f, pitchHz = 300f)
```

Night's knock is 60% of day's: a 10ms, 300Hz seat knock at
the engine's floor — at 1 AM the press is *felt more than
heard*, which is the correct register for a man who may not
be alone in the house. The completion voices follow the
same economy: a whole landing earns one `playFirmClick()`;
a landing with lacks earns **silence** — the board's
re-print and FIX's appearance are the verdict (built
`LaunchedEffect(checking)`). No failure sound exists in
this drawer and none may be added: bad news in PARK is
always print, never noise.

**Flagged imbalance (no patch in this file):**
`playFirmClick()` takes no level — fixed gain 0.5, fixed
haptic strike (225/135) — while the press that precedes it
is night-scaled to a 15/255 haptic whisper. By day the
press-to-verdict step is ~25→225; at night it is ~15→225.
The verdict therefore shouts *relatively louder* at night
than by day, on the one screen where the ratio matters
most. The engine is shared app-wide (sled throws, detents),
so PARK cannot lawfully retune it from `ParkDrawer.kt`.
Priced proposal for Hof/Ren: an optional amplitude
parameter on `playFirmClick()` defaulting to today's
behaviour, with PARK's two call sites (FIX return, CHECK
whole landing) passing a night-scaled value in the same
0.6 ratio as the knock. Until that lands, the imbalance
stands as built — named here so it is a known debt, not a
discovery.

---

## §3 · `GUARD — NOT CHECKED` — HOW NOT-KNOWING SHOULD FEEL

### A-9 · The state and its two homes — **SPEC-ONLY**

`GUARD — NOT CHECKED` is the header Guard line's print
whenever the counted set is unmeasured — snapshot null, or
any one of the counted four `UNMEASURED` (`GuardLine.kt`,
both paths; audit P-6's cure for the cold-open blank that
read as FINE). Inside the drawer its twin is quieter still:
the board prints **names only** — no word, no fill, no
placeholder — and the `LAST PROVED` slot beside the `GUARD`
header stays blank until the first complete set lands
(acquitted, audit §7). The summary line names the ignorance;
the rows simply decline to speak. Both behaviours stand;
this section rules only how the state must *feel*.

### A-10 · Honest, not alarming — the five mechanics — **SPEC-ONLY**

The line already achieves the feeling; these are the
mechanics that achieve it, stated so no future patch
decorates them away:

1. **Same voice.** NOT CHECKED wears the Guard line's one
   dress verbatim — Anonymous Pro Bold 15sp, +0.5 tracking,
   `onBackground` at full ink, right-aligned in the band,
   baseline-identical with `GUARD 4/4` and every lack print
   (Designer §2.2; Finisher 4 F-G1). Ignorance is spoken in
   the band's normal tone of voice. The moment unknown gets
   a fainter ink it becomes a whisper to be missed; the
   moment it gets a louder one it becomes a warning. It is
   neither — it is a fact about the app, printed like one.
2. **The words blame nobody.** "NOT CHECKED" says *nobody
   has looked* — not that anything is broken, missing, or
   his fault. It is the negation of a measurement, never a
   failure word. The drawer's whole negative vocabulary
   obeys this: `MISSING` (a measured fact), `STALE` (an old
   fact), `NOT PROVED` (§4) — and `FAILED`, `ERROR`,
   `BROKEN`, `WARNING` appear on no face in this issue.
   Keep the register: at 1 AM, wording is volume.
3. **No count, ever.** The line never prints `GUARD 0/4`
   off an unmeasured set (Designer §2.1). A fabricated
   number would feel precise and be a lie; the absent count
   *is* the form saying "unmeasured" before the words are
   read. Calm here comes from refusing false precision —
   the line is relaxed because it is not pretending.
4. **No alarm furniture.** No alert orange (the alert token
   does not enter this drawer, not even for failure —
   Designer, house law), no icon, no box, no pulse, no
   second line, no animation — the band never animates, for
   any state. Decoration on an unknown argues with it; the
   cure for the old blank was a printed line, nothing more.
5. **The remedy is in the same room.** NOT CHECKED is never
   a dead end: unsealed, the line itself is a door into
   PARK, and inside, the board's unmeasured rows sit
   directly above a CHECK key wearing ink dress (§2, A-7 —
   UNMEASURED promotes it). The unknown and its one honest
   verb share a screen, at one glance, with nothing to hunt
   for. Anxiety in interfaces is mostly *distance between a
   problem and its verb*; here the distance is ~500dp of
   print he is already looking at, and opening PARK has
   already fired the sweep before his finger arrives
   (host park-open effect). By the time he could press
   CHECK, the answer is usually already landing.

And the seal case, ruled plainly: if the seal freezes the
line while unmeasured, the frozen print reads NOT CHECKED
for the session's whole life (Designer §2.3). That is the
honest freeze, not a defect — do not "fix" it by
re-measuring under the seal. A sealed night that admits it
was never checked is calmer, and truer, than one that
quietly upgrades itself.

**The feeling, in one line for the acceptance pass:** NOT
CHECKED should feel like a note left on the drawer in the
house's own handwriting — *nobody has looked in here yet* —
not like a light left on in a corridor.

---

## §4 · THE `NOT PROVED` LINE — FAILURE AT ROOM TEMPERATURE

### A-11 · Its dress, as built — **SPEC-ONLY**

When a CHECK lands unwhole, the built drawer prints one
persistent line under the key, in the board's own row
grammar (`ParkBoardRow(name = "NOT PROVED", word =
lackName, wordIsLack = true)`): name in the mute tier,
the first unproved counted door in full ink, ruled under
by the same 18% hairline. It persists until the next
press, the drawer's close, or the board reading whole by
any road — it never auto-expires, because a man reading
at 1 AM must not be able to miss it by looking up late.

Atmospherically this is exactly right, and worth saying
why: failure here is **a board line, not an event**. It
takes the drawer's most ordinary form — the same 48dp
pitch, the same inks, the same ruling as `MISSING` above
it — so it reads as *one more true line on the board*,
continuous with the lack it names (which the board itself
already prints). It claims nothing more than the
measurement: every door answered; a MISSING is an answer;
the line names the door and stops (cell plan's correction
of the Designer's draft wording stands — no "DID NOT
ANSWER", no blame). No sound accompanies it (A-8). Keep
all of it.

### A-12 · Its arrival — **PATCH-READY** (the one jar in the new work)

As built, the line appears by plain recomposition: in one
frame the sheet's content grows by a spacer + a 48dp row,
and everything below it — Sound sled, Shorts + Reels sled,
Theme line, plate — drops by that height instantly. The
*content* arriving instantly is acquitted law (re-strike);
the *furniture below it jumping* is not — it is the single
physical jar the CHECK work introduces, felt precisely at
the moment the news is worst.

The FIX key already owns the lawful grammar for an object
*arriving* in this drawer: `AnimatedVisibility` with
`expandVertically` + `fadeIn`. NOT PROVED is the same kind
of arrival — a new line joining the board's family, not a
word re-striking on an existing row — so it takes the same
grammar. In `ParkDrawer.kt`, replace the plain block:

```kotlin
val lackName = lastCheckLackName
if (lackName != null && !checking) {
    Spacer(modifier = Modifier.height(14.dp))
    ParkBoardRow(name = "NOT PROVED", word = lackName, wordIsLack = true)
}
```

with (imports already present in the file):

```kotlin
val lackName = lastCheckLackName
AnimatedVisibility(
    visible = lackName != null && !checking,
    enter = expandVertically(
                tween(MonolithKin.DRAWER_CLOSE_MS, easing = MonolithKin.EasingSeat)) +
            fadeIn(tween(MonolithKin.DRAWER_CLOSE_MS)),
    exit = shrinkVertically(
                tween(MonolithKin.DRAWER_CLOSE_MS, easing = MonolithKin.EasingThrow)) +
            fadeOut(tween(MonolithKin.DRAWER_CLOSE_MS))
) {
    val name = lackName ?: return@AnimatedVisibility
    Column {
        Spacer(modifier = Modifier.height(14.dp))
        ParkBoardRow(name = "NOT PROVED", word = name, wordIsLack = true)
    }
}
```

Notes for the builder: the 14dp spacer rides *inside* the
visibility block, exactly as FIX's does, so a clearing
verdict takes its gap with it and leaves no orphan void.
(When Finisher 2 §7's spacing module lands, this spacer
becomes 16dp with the rest of the key zone — one commit,
not two.) 160ms is deliberate: the line *settles* at THROW
duration with SEAT easing on entry — present quickly,
without the drawer's full 280ms ceremony. The row's word
itself still re-strikes instantly whenever `lackName`
changes while visible; only the line's arrival and
departure are eased. Persistence, clearing conditions,
dress, and wording are untouched.

---

## §5 · THE ONE ATMOSPHERE RISK — THE PROOF RITUAL

### A-13 · The risk, named precisely — **SPEC-ONLY** (this section is a fence)

CHECK is the first verb in PARK that can be *repeated for
reassurance*. FIX cannot become a ritual — it exists only
while a lack does, and it exports the man to Settings.
CHECK is always present, always lawful, costs one press,
takes ~300ms, and pays out a firm click and a fresh
`LAST PROVED` time when the board is whole. Every
ingredient of a dashboard loop is present in embryo:

- a **short staleness horizon** — grants cross to STALE at
  45s (`WatchStore.STALE_AFTER_MS`), *while the drawer
  stands open*, on the built 5-second re-derivation beat.
  A man can watch the board age in his hands;
- a **visible reward** — the click, the advancing instant,
  the key's promotion and demotion;
- a **repeatable gesture** with no cost, no cooldown, and
  no diminishing return printed anywhere.

Run that loop nightly and the quietest screen in the app
becomes the one he *checks* — the drawer has become a
dashboard with better typography, and calm, the feature,
is gone. Worse: it becomes a dashboard that can only ever
report SELF-REPORT — the ritual would grow while the
evidence grade never does, which is precisely the lie the
parked Live-Fire Strip (issue #21) exists to cure properly
and CHECK must never impersonate (Designer §1.1, §1.8).

### A-14 · How it stays a drawer — the six restraints, all binding

Most of the cure is already law in the built work; this
spec collects it as one fence so a future patch cannot
trade a restraint away one line at a time:

1. **Success prints nothing.** A whole landing adds no
   line, no banner, no word of its own — the verdict is the
   board's re-print, FIX's absence, and the `LAST PROVED`
   instant advancing where it already lives (built). The
   asymmetry is the calm: *failure gets a line (§4);
   success gets silence and a click.* Any future "PROVED ✓"
   furniture — however small, however tasteful — inverts
   the asymmetry and starts the reward loop. **Cut on
   sight.**
2. **The key demotes itself.** With A-7's ladder landed,
   a fresh board returns CHECK to print dress — a drawer
   tool that goes back to being print when its work is
   done. Dashboard controls stay lit; drawer tools rest.
   This restraint is why A-7 is PATCH-READY and not a
   nicety.
3. **No telemetry on the face.** `CHECKING` is one word
   and a 1dp dip — no spinner, no ellipsis, no percentage,
   no "3 OF 6", no sweep generation, no countdown to
   staleness (built; Designer §1.5/§1.8). Numbers that move
   are watched; PARK prints instants, not processes.
4. **Nothing invites the press.** Opening PARK already
   fires the sweep (host effect) — routine proof happens
   *without* the ritual, so CHECK is for when he asks, not
   a chore the drawer sets him. Promotion to ink dress is
   a static swap at the stale boundary (A-7): no pulse, no
   badge, no dot, no "needs checking" nudge anywhere in
   the app may point at this key. The drawer does not
   recruit.
5. **No memory of the ritual.** PARK keeps no count, no
   history, no streak, no "last checked" beyond the one
   acquitted `LAST PROVED` complete-set instant — which
   belongs to the *board's proof*, not to his behaviour,
   and advances identically whoever's sweep completed the
   set (built derivation: any writer's stamp counts). A
   proof history in this drawer is a score, and this app
   keeps its scores elsewhere by his standing rule.
6. **Proof never traps him.** The sweep is the store's,
   not the drawer's: closing mid-CHECKING abandons the
   key's claim, never the sweep, and the next opening
   prints its landings (built). All three dismissal paths
   stay live during and after a check. A ritual you cannot
   walk away from mid-gesture is a modal; a drawer you can
   shut with a sweep still in flight is furniture.

**The 45s horizon, ruled:** the board visibly ageing while
held open is *honesty*, not a defect to be hidden — the
alternative ( freezing the print while open) is the one
lie this drawer may never tell. The atmosphere answer is
not to slow the decay but to keep its *presentation* at
room temperature: STALE re-strikes instantly at half-ink
(A-6), CHECK promotes silently (A-7), and nothing on the
surface expresses urgency about either. He may re-press
in a minute if he wants to. The drawer's job is to make
the second press feel exactly as plain as the first —
no escalation, no celebration, no fatigue. **The kill
test for any future CHECK-adjacent proposal: press it
three times in a minute on device. If the third press
feels different from the first — more rewarding, more
urgent, more alive — the proposal has made a dashboard.
It dies.**

---

## §6 · ACCEPTANCE — BY EYE AND EAR, NIGHT FIRST

1. **Night, seated:** open PARK in night dress. The sheet's
   top edge and corners are findable without hunting (A-3's
   measurement); the scrimmed Deck behind reads as the same
   room, dimmed — not a void, not a second screen.
2. **Travel:** drag the cap slowly. Scrim and sheet move as
   one quantity at every point of the drag; release past
   40% and the room brightens on the same 160 THROW that
   throws the sheet. No frame where one moves without the
   other.
3. **Night keys:** on a fresh board, CHECK rests in print
   dress; FIX (when present) is a warm dark mass with a
   cream word — the most solid object in the drawer and
   *not* its brightest pixel. Let a row cross 45s: CHECK
   promotes to ink in one instant swap. No pulse, before
   or after.
4. **Sound at night:** press CHECK — knock is felt more
   than heard (0.06). Whole landing: one firm click,
   nothing else. Landing with a lack: silence, and the
   `NOT PROVED` line settles in over 160ms (A-12) while
   the sleds and plate glide — nothing below the key zone
   jumps a single frame.
5. **Cold open:** first frame of the app prints
   `GUARD — NOT CHECKED` in the band's ordinary dress —
   no blank, no `0/4`, no colour event. Open PARK:
   name-only board, CHECK in ink, and the state reads as
   *unlooked-in*, not *alarmed*.
6. **The ritual test (A-14):** press CHECK three times in
   a minute on a whole board. Each press: same knock, same
   dip, same click, same advancing instant, no new print,
   no escalation. The third press is as boring as the
   first. Boring is the pass mark.
7. **Day parity:** repeat 1–6 in day dress. Every number
   above holds with the day column of A-6/A-7 substituted;
   the knock is a step louder (0.10) and nothing else in
   the room's behaviour changes.
8. **Seal continuity:** leave PARK open across a seal.
   The keys leave; the weather does not change — same
   scrim, same ground, same print, now read-only.

## PATCH INDEX

- **A-7** — CHECK dress ladder: apply Finisher 2 §4.1
  verbatim; atmosphere acceptance at §6.3/§6.6.
- **A-12** — `NOT PROVED` arrival: `AnimatedVisibility`
  wrapper in `ParkDrawer.kt`, exact code above.
- Everything else is **SPEC-ONLY** law, plus two debts
  named, not patched: the night sheet-edge measurement
  (A-3, patch priced and waiting on a failed check) and
  the unscaled firm click (A-8, engine-level proposal
  for Hof/Ren).

No DB change, no new permission, no new colour, no new
curve, no new size, no new word on any face. Zero orange,
zero green, day and night.

— Feel 4 (atmosphere), issue-9 cell (for Hof)
