# REVAMP-CRITIC2 — post-build critics on Slice 1 AS BUILT

**Scope:** the built code, not the plan. Files read line by line:
`app/src/main/java/com/example/ui/components/LauncherBlockScreen.kt` (724 lines),
`app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt`,
`app/src/main/java/com/example/MainActivity.kt`, plus the header/chip components
(`MonolithCustomComponents.kt`), `MonolithModels.kt` (span derivations),
`MonolithViewModel.kt` (airlock/grant paths), `Color.kt`, `Type.kt`,
`AndroidManifest.xml`, and `LauncherPartitionTest.kt`.

**Relation to REVAMP-CRITIC.md:** C-01…C-15 are NOT repeated. Where a C-charge's
cure landed, it is acquitted below with as-built evidence; where a cure landed
half-way, the residue is filed as a new D-charge only when the defect lives in
code the plan-critics could not see. Sarthak's ruling itself (launcher +
separate ALLOWED/PEEK screens) is his word and is not relitigated anywhere here.

**Verdict in one line:** the pure logic (partition, print, progress) is sound
and tested, but both doors INTO the built launcher are broken in the wiring —
the Guard line's tap is stripped by the header before the host's gate can ever
run, and the Airlock's doors open the launcher *underneath* a still-visible
Airlock whose only exit writes a false "Stayed Focused" record.

**Counts: 20 charges — 2 CRITICAL, 5 HIGH, 8 MEDIUM, 5 LOW. 12 acquittals.**

---

## IN-FLIGHT FIX-FORWARD — seen on disk, not re-filed

The parallel fix-forward for C-01…C-15 was in flight while this pass ran.
Status observed on disk; none of these are re-charged:

- **Back law** — landed: `LauncherBlockScreen.kt:210-212` (grid → HOME, HOME swallowed). See A2-12.
- **Stopwatch arc** — landed: no arc for stopwatch (`LauncherBlockScreen.kt:252`, draw guard in the dial Canvas). See A2-10.
- **Emergency Break on grids** — landed: foot row on `LauncherAppsPage` reuses `launcher_emergency_break`. See A2-09.
- **Airlock chip countdown gating** — landed: doors absent until `countdownSecs == 0` (`AirlockAndDisarmOverlays.kt:731`). See A2-08.
- **Tap-time launch/entry gates** — landed for the OPEN launch and the Guard entry predicate (`MainActivity.kt:838-871`, `:441-470`). See A2-06. D-03 below is the one branch the gate does not cover (Safe-Break peek), not a re-file of the gate itself.
- **Safe-Break peek** — landed: direct launch during a break (`MainActivity.kt:872-893`). D-03 charges only its missing revalidation.
- **Safe-Break eyebrow** — landed (`LauncherBlockScreen.kt:397-410`). D-04 charges only the landed wording, which contradicts the model's span law.
- **Browser/store bars** — landed in the launcher filter (`LauncherBlockScreen.kt:225-232`) and in the launch gate (`MainActivity.kt:858-859`). **One half-done residue, noted as IN-FLIGHT, not charged:** the Airlock chips' counts are still unfiltered — `MainActivity.kt:916-921` counts every OPEN row (`allowedAppsCount = allApps.count { it.resolvedTier == OPEN }`), while the launcher door counts the filtered `allowedVisible` list. The same "ALLOWED APPS" label therefore prints two different numbers on the Airlock and on the launcher HOME (off by exactly the browser + store rows). Whoever owns the browser/store fix-forward owns this line too.

---

## CHARGES

### D-01 — The Guard-line entry is dead code: the header strips the tap while sealed, so the host's launcher gate can never fire
**Severity: CRITICAL (the launcher's primary entry does not exist in the hand)**

`MainActivity.kt:440-441` hands the header `isSealed = doorLocked` and an
`onGuardLineTap` whose sealed branch is the launcher's entry:

> `MainActivity.kt:457-462` — `val tapSealed = viewModel.activeSession.value?.isActive == true; if (tapSealed && !viewModel.navigationLocked()) { viewModel.runWatchSweep(); launcherPage = LauncherPage.HOME; launcherOpen = true }`

But the header never delivers a sealed tap. `MonolithCustomComponents.kt:290-291`:

> `if (isSealed) Modifier else Modifier.clickable { onGuardLineTap() }`

While sealed — the only state in which the launcher branch can run — the
Guard line has **no clickable modifier at all**. The elaborate tap-time
predicate, the watch sweep, and the navigation-lock check are unreachable
from the surface they were written for. This is precisely the HB-1/HB-2
pattern this tree already convicted once (a composed print standing in for
a lock), inverted: here a composed *gate* stands in for a door.

**User-visible consequence:** a sealed user taps the Guard line — the
briefed, documented way into the launcher — and nothing happens. No
launcher, no feedback, no print. The feature's main door is a wall.

**Fix:** the header must stay clickable while sealed and let the host
handler decide (the handler already re-reads the seal and the lock at tap
time — that is what it is for). The header's own spec comment
("print, not a door") must be amended in the same change, or the next
builder will "fix" the clickable back off.

### D-02 — The Airlock's doors open the launcher underneath a still-visible Airlock — and the only way out of that stack writes a false "Stayed Focused" record
**Severity: CRITICAL (second entry broken + TELL-THE-TRUTH record lie, realised in wiring)**

`MainActivity.kt:922-929`:

> `onOpenAllowedApps = { launcherPage = LauncherPage.ALLOWED; launcherOpen = true }` (and identically for PEEK)

Nothing here hides the Airlock. The Airlock's visibility is
`_airlockState.isVisible` in the VM, cleared only by the peek grant
(`MonolithViewModel.kt:2399`), by `transmuteImpulseAndReturn` (`:2431`),
or by the micro-step path (`:1970`) — none of which these callbacks call.
Composition order finishes the trap: the launcher is composed at
`MainActivity.kt:832` and the Airlock **after** it (`MainActivity.kt:~908`,
under the comment "hosted under the Airlock, so a fresh intercept's door
always stands above it"), so the freshly opened launcher renders *below*
the Airlock that summoned it.

**User-visible consequence, step by step:** user waits out the 5-second
wall, taps ALLOWED APPS — nothing visibly changes. The launcher is open
but covered. The only Airlock affordance that dismisses it is RETURN TO
FOCUS, which calls `transmuteImpulseAndReturn`
(`MonolithViewModel.kt:2428-2437`) and writes an intercept log with
outcome **"Stayed Focused"** for the intercepted package — after which
the launcher finally appears and the user walks into an app. The
session's own history now records a deflection that never happened, and
the receipt assembled from those logs inherits the lie. This is C-12's
record charge, except the plan version was hypothetical; the as-built
wiring *forces* the false record as the price of using the door.

**Fix:** `onOpenAllowedApps` / `onOpenPeekApps` must dismiss the Airlock
through a dedicated VM verb that (a) sets `isVisible = false` WITHOUT the
transmute record, and (b) writes the intercept outcome for what it was
("Left via Allowed door" / "Left via Peek door", with the Airlock's
intercepted package). RETURN TO FOCUS may only write "Stayed Focused"
when the user actually returns to the Deck.

### D-03 — The Safe-Break peek launch revalidates nothing: it launches the stale composed entity on the break flag alone
**Severity: HIGH (HOLD weakening — a stale grid can launch a tightened app)**

`MainActivity.kt:872-893` (the `onPeekApp` Safe-Break branch):

> `val tapSession = viewModel.activeSession.value; if (tapSession != null && tapSession.isActive && tapSession.isInSafeBreak) { launcherOpen = false; runCatching { context.packageManager.getLaunchIntentForPackage(app.packageName)…`

The `app` here is the entity captured at grid composition. Unlike the
OPEN path directly above it (`:838-871`), this branch never re-reads the
row: no `resolvedTier` check at the tap instant, no
`navigationLocked()` check, no existence check. Between composition and
tap the row can be tightened to LOCKED (the ratchet is lawful
mid-session), the finish machine can detain navigation, or the package
can be disabled — and this branch launches it anyway, because at
composition time it sat on the PEEK shelf and a break is running.

**User-visible consequence:** during a Safe Break, the PEEK grid is a
launch surface with composition-time truth; a just-locked app launches
from a grid that still shows it as peek.

**Fix:** mirror the OPEN gate in this branch: re-read
`viewModel.apps.value.find { … }`, require the row to still be PEEK_2M
(or a member of the break's actually-free set, derived at tap time),
require `!viewModel.navigationLocked()`, and refuse in print on the
launcher when the re-read fails.

### D-04 — The Safe-Break eyebrow prints "THE SESSION CLOCK IS PAUSED" while the session clock demonstrably keeps counting under it
**Severity: HIGH (TELL-THE-TRUTH — two surfaces contradict in the same minute)**

`LauncherBlockScreen.kt:399`:

> `text = "ON A SAFE BREAK — THE SESSION CLOCK IS PAUSED"`

The model's own law says the opposite. `MonolithModels.kt:388-398`
(span derivations): "During a break the span simply keeps counting —
the break is not rendered AS a break… session time is," and
`spanElapsedSeconds` (`:414-427`) explicitly adds the in-break elapsed
term `(SAFE_BREAK_DURATION_SECONDS - safeBreakSecondsRemaining)`. The
launcher's own dial obeys the model, not the eyebrow: `endsAtMs` /
`nowMs` (`LauncherBlockScreen.kt:236-248`) keep ticking through the
break, so the numeral drops second by second directly beneath the word
PAUSED. The Deck's eyebrow for the same state is the truthful one:
`TactileDeckScreen.kt:616` — `"BREAK · ${(safeBreakSecondsRemaining + 59) / 60} MIN"`.

**User-visible consequence:** the launcher tells the user his clock is
paused while he watches it run; the Deck, one surface away, tells him
how many break minutes remain. One of them is lying in his face, and
it's the new one. (Per-block time pauses; span time — the only time
this dial prints — does not. The eyebrow conflates the two.)

**Fix:** print the Deck's eyebrow verbatim in form —
`BREAK · N MIN` from `safeBreakSecondsRemaining` — and delete the
PAUSED claim. (The eyebrow's *existence* was the in-flight C-07 fix;
this charge is solely its landed wording.)

### D-05 — Launch failure is silent and the launcher closes before the launch is known to exist; refusal is silent while the tap haptic already said "seated"
**Severity: HIGH (TELL-THE-TRUTH — feedback claims a launch the code hasn't secured)**

Both launch branches set `launcherOpen = false` **before** resolving
the intent:

> `MainActivity.kt:861-870` — `launcherOpen = false; runCatching { context.packageManager.getLaunchIntentForPackage(liveApp.packageName)?.let { … startActivity(launch) } }` (Safe-Break branch identical at `:882-891`)

If `getLaunchIntentForPackage` returns null (package disabled,
uninstalled between sync and tap, no launcher activity) or
`startActivity` throws, the `?.let` / `runCatching` swallows it: the
launcher is already gone, the user lands on the Deck, and nothing
prints why. The refusal path is worse paired with the tile: when the
OPEN gate refuses (`launchAllowed == false`), `onLaunchApp` simply
does nothing — but the tile tap already fired
`SealHaptics.tileSeat(context)` in `LauncherBlockScreen.kt`'s
`onAppTap` wrapper before the gate ran, so the finger was told
"seated" for a launch that was refused in silence.

**User-visible consequence:** tap an app, feel the seat tick, watch the
launcher close, arrive at the Deck with no app and no explanation — an
interaction indistinguishable from a crash.

**Fix:** resolve the intent first; close the launcher only after
`startActivity` succeeds. On refusal or null intent, keep the launcher
open and print one line on it ("NO LONGER OPEN", "APP NOT AVAILABLE").
Fire the tile-seat haptic from the success path, not the tap path.

### D-06 — App icons are decoded and rasterised on the main thread, inside composition, with no cache — the 100+ app grid will hitch on every scroll and every reopen
**Severity: HIGH (performance — grid perf with a full phone of apps)**

`LauncherBlockScreen.kt:663-675` (`LauncherAppTile`):

> `val iconBitmap: Bitmap? = remember(app.packageName) { runCatching { val drawable = context.packageManager.getApplicationIcon(app.packageName); val bmp = Bitmap.createBitmap(96, 96, Bitmap.Config.ARGB_8888); … drawable.draw(canvas); bmp }.getOrNull() }`

`getApplicationIcon` is a PackageManager call (IPC + resource load) and
the rasterisation is CPU work; both run synchronously during
composition, on the main thread, once per tile as the LazyVerticalGrid
scrolls it into view. `remember` is per-composition-instance: tiles
disposed by scroll distance or by switching ALLOWED ↔ PEEK decode
again on return, and nothing is shared between tiles, pages, or
launcher openings. With 100+ rows the grid's first fling is a decode
parade on the UI thread. Two rendering defects ride along: the bitmap
is a fixed 96×96 shown at 40.dp — undersampled (blurry) on densities
above ~2.4 — and adaptive icons are drawn full-bleed into the square,
ignoring the adaptive safe zone, so foregrounds clip differently from
every other launcher on the phone.

**User-visible consequence:** the "crazy good" grid stutters exactly
when browsed; icons look soft and inconsistently cropped.

**Fix:** decode off the main thread (`produceState` /
`LaunchedEffect` on `Dispatchers.IO`, or the app's own image cache),
into a process-wide LruCache keyed by package + density-sized target
(40.dp × density), and draw adaptive icons with their background or
the platform's adaptive rendering. Composition should only ever read
an already-decoded bitmap or the monogram fallback.

### D-07 — Launcher state lives in two layers of plain `remember`: rotation, font-scale, theme or process death while the launcher is open destroys it and re-fires the seal haptic
**Severity: HIGH (process death / configuration change)**

- Host: `MainActivity.kt:285-286` — `var launcherOpen by remember { mutableStateOf(false) }`, `var launcherPage by remember { … }`.
- Surface: `LauncherBlockScreen.kt:202` — `var page by remember(visible, session?.id) { mutableStateOf(initialPage) }`.
- The grid's scroll state is a default `LazyVerticalGrid` state (`LauncherBlockScreen.kt:~605-618`) — also unsaved.
- `AndroidManifest.xml`: MainActivity declares **no** `android:configChanges`, so rotation / font-scale / dark-theme / locale changes recreate the Activity.

Nothing here is `rememberSaveable`, VM-held, or in a SavedStateHandle.
Any recreation — or Android killing the process while the user is in a
launched allowed app, the single most likely death for this surface —
reborns `launcherOpen = false`: the user returns to the Deck, not the
launcher room he left, and his place in a 100-app grid is gone. On
recreation with the launcher re-opened, `LaunchedEffect(visible,
session?.id)` (`LauncherBlockScreen.kt:237-238`) runs fresh and fires
`SealHaptics.sealWeight(context)` — a THUD for a rotation, not a seal.

**User-visible consequence:** tilt the phone or bump system font size
inside the PEEK grid and the room, the page, and the scroll position
vanish; the phone buzzes as if a session had just sealed.

**Fix:** hoist `launcherOpen` + page to the ViewModel (or
`rememberSaveable` at minimum, page as an enum name), give the grid a
saveable `LazyGridState`, and fire `sealWeight` only on a genuine
closed→open transition, not on effect (re)creation.

### D-08 — The one-second ticker recomposes the whole launcher every second — including the app grids, which print no time at all
**Severity: MEDIUM (performance / battery — recomposition scope)**

`nowMs` is state held at the top of `LauncherBlockScreen`
(`:236-242`), read at `:248` to derive `remainingSeconds`, and the
`LaunchedEffect` loop runs unconditionally — it is not keyed to the
page. Every tick therefore invalidates the whole surface: when the
user is browsing `LauncherAppsPage`, the `LazyVerticalGrid`, every
visible tile's press-animation state and the foot row recompose once
per second to display a clock that isn't on that page. `LauncherHome`
additionally allocates a `SimpleDateFormat` per recomposition
(`:422`) — i.e. once per second, forever, while the launcher stands.

**User-visible consequence:** no visible lie, but a standing
per-second tax on the exact surface whose scroll performance D-06
already endangers, plus avoidable battery draw on an always-on-
during-study surface.

**Fix:** move the ticker, `endsAtMs`, `remainingSeconds` and
`progress` inside `LauncherHome` (the only consumer), so HOME ticks
and the grids are still. Hoist the `HH:mm` formatter to a remembered
instance.

### D-09 — The stopwatch numeral is a snapshot, not a count: the dial's two branches tick by different clocks
**Severity: MEDIUM (correctness — derivation inconsistency)**

Timed sessions tick locally (`remainingSeconds` from `nowMs`,
`:248`). The stopwatch branch ignores the ticker entirely.
`LauncherHome`'s dial text:

> `session.isStopwatchMode -> launcherRemainingPrint(session.spanElapsedSeconds)`

`spanElapsedSeconds` is whatever the last `activeSession` emission
carried. The local ticker dutifully recomposes the surface every
second and the stopwatch numeral only moves when the VM's session
flow happens to emit — on the shield's beat cadence, in steps —
while the timed numeral beside the same code path moves every
second. A count-up that advances in visible jumps (or sits frozen
between emissions) reads as a broken clock on the surface whose
whole job is the clock.

**Fix:** give the stopwatch the same treatment as the timed branch:
anchor at composition (`anchorElapsed = session.spanElapsedSeconds`
at open, `anchorWall = System.currentTimeMillis()`) and print
`anchorElapsed + (nowMs - anchorWall)/1000`. One ticker, both
branches, no snapshot numerals.

### D-10 — "ENDS HH:mm" re-anchors on every upstream emission, so the comment's guarantee — "the two surfaces can never print two different ends" — is not what the code does
**Severity: MEDIUM (TELL-THE-TRUTH — a guarantee printed in code that the code doesn't keep)**

`LauncherBlockScreen.kt:233-235` claims the derivation is "the same
derivation the Airlock's session line uses (now + the span's
remaining), so the two surfaces can never print two different ends."
But `endsAtMs` is `remember(session?.id, session?.spanRemainingSeconds)`
(`:244-247`): every time the session flow emits a new
`spanRemainingSeconds`, the anchor is recomputed as
`System.currentTimeMillis() + remaining`. The Airlock's
`sessionEndsAtMillis` is computed **once**, at intercept time
(`MonolithViewModel.kt:~2365`). Whenever the VM's span figure is
even a beat stale relative to wall time, the launcher's re-anchored
ENDS and the Airlock's frozen ENDS differ by that staleness — and the
launcher's ENDS can visibly *move* (re-anchor jump) while the user
watches a number that is presented as a fixed appointment. Minute
precision hides most of it; "can never" hides all of it, falsely.

**Fix:** anchor once per session (key `remember` on `session?.id`
only, capturing the remaining at first composition — the current
local-ticker design already keeps it honest from there), or read a
single VM-owned ends-at fact on both surfaces. Strike "can never"
from the comment until one of those is true.

### D-11 — Launcher HOME is a fixed, non-scrollable column: a long intention or large font scale pushes the Emergency Break off the bottom of the screen
**Severity: MEDIUM (the lawful exit must stay reachable — layout, not composition)**

`LauncherHome` is a plain `Column` (`LauncherBlockScreen.kt:~300`)
stacking fixed heights: 28.dp vertical padding, headers, 30.dp
spacer, a fixed 248.dp dial, eyebrow, the intention `Text` at
`:411-420` with **no `maxLines` and no ellipsis**, ENDS, SHUT, 30.dp
spacer, two door plates, then `Spacer(Modifier.weight(1f))` and the
RETURN / EMERGENCY BREAK foot row. When content exceeds the
viewport — a two-line intention at 1.3× font scale, or any
short-height device — the weighted spacer collapses to zero and the
column overflows: Compose clips the bottom, which is exactly where
the foot row lives. A2-09 acquits the valve's *composition* on every
page; on HOME, the landing page, it can still be clipped out of
reach by the user's own intention text.

**Fix:** cap the intention at 2 lines with ellipsis (it is a print,
not an editor), or make the column `verticalScroll` with the foot
row pinned outside the scroll region. The valve row must be
structurally un-clippable.

### D-12 — TalkBack reads the launcher as fragments: the dial has no semantics, door plates read as three unrelated stops, and every tile announces its name twice
**Severity: MEDIUM (accessibility)**

- The dial `Canvas` (`LauncherBlockScreen.kt:~345-375`) carries no
  `contentDescription` / semantics: the ring — the surface's primary
  state object — is invisible to TalkBack. The numeral is a separate
  `Text`, so time is reachable, but ring + label + intention are
  never presented as one fact.
- `LauncherDoorPlate` (`:~500-545`) is one clickable `Row` containing
  three separate `Text`s (title, sub, count) with no merged
  semantics: TalkBack stops three times — "ALLOWED APPS", "OPEN —
  TAP TO USE", "12" — the count read as a bare number bound to
  nothing.
- `LauncherAppTile` (`:688-722`): the `Image` sets
  `contentDescription = app.appName` (`:700`) directly above a
  visible `Text` of the same `app.appName` — every tile is announced
  twice — and the clickable `Column` itself carries no role, label,
  or action description distinguishing "launches the app" (ALLOWED)
  from "opens the peek door" (PEEK).

**Fix:** `semantics(mergeDescendants = true)` on plates and tiles
with a single composed description ("Allowed apps, 12. Open — tap
to use" / "<name>, tap to launch" vs "<name>, tap to open the peek
door"); mark the tile `Image` decorative (`contentDescription =
null`) since the label below speaks; give the dial a semantics
description carrying remaining/elapsed + intention.

### D-13 — The launcher's text buttons and the Airlock's door chips are all below the 48dp minimum touch target
**Severity: MEDIUM (accessibility — touch target sizes)**

Measured from the code: HOME foot row "RETURN TO THE DECK" /
"EMERGENCY BREAK" — 12.sp text with `padding(horizontal = 10.dp,
vertical = 10.dp)` (`LauncherBlockScreen.kt:~462-490`) ≈ 36.dp
tall. Grid pages: back link "‹ LAUNCHER" at 8.dp vertical padding
(`:~565`) ≈ 33.dp; grid foot row links at 10.dp ≈ 36.dp. The
Airlock door chips fare no better: `BlockitActionChip` is fixed at
`.height(42.dp)` (`MonolithCustomComponents.kt:201`) — RETURN TO
FOCUS, the peek chip, and both launcher door chips
(`airlock_allowed_apps_button`, `airlock_peek_apps_button`) are all
42.dp targets, on the surface a tired user operates by feel.

**Fix:** enforce a 48.dp minimum touch height on all of these
(`heightIn(min = 48.dp)` / `minimumInteractiveComponentSize`) while
keeping the visual plates exactly as drawn — the target grows, the
drawing doesn't have to.

### D-14 — The one muted line on the dial fails contrast: "$n SHUT" at 11sp in the 3.5:1 tier colour
**Severity: MEDIUM (accessibility — the green law passes; the contrast law doesn't)**

`LauncherBlockScreen.kt:429-434`: the SHUT count prints in
`NightColorOnSurfaceVariant` — `Color(0xFF776A60)` on the
`0xFF171412` ground. `Color.kt`'s own comment for this token admits
the tier: "Night muted tier — 3.5:1". At 11.sp that is below the
4.5:1 small-text floor, on a factual count (how much of his phone is
shut) that the dial claims as one of its three truth-tellers. Every
other launcher text uses `NightTextColorSecondary` (5.8:1 per
`Color.kt`) or brighter; this one line was demoted below legibility
for hierarchy's sake.

**Fix:** print the SHUT line in `NightTextColorSecondary` like its
siblings. Hierarchy can come from size and spacing, not from
unreadability.

### D-15 — The launcher's BackHandler stays live under overlays that have none: Back presses meant for the Airlock act on the hidden launcher instead
**Severity: MEDIUM (correctness — cross-surface Back theft)**

The launcher's `BackHandler` (`LauncherBlockScreen.kt:210-212`) is
composed whenever the launcher is visible, and neither
`AirlockInterceptOverlay` nor `DisarmChallengeOverlay` declares a
`BackHandler` of its own. In the D-02 stack (Airlock over open
launcher), a Back press is consumed by the invisible surface: if the
hidden page is a grid it silently resets to HOME; if HOME, it's
swallowed — while the user, looking at the Airlock, sees the press
do nothing at all and learns "Back is dead in Monolith." (The
Emergency path escapes this only because `onRequestEmergencyBreak`
closes the launcher first, `MainActivity.kt:904-907` — the Airlock
path has no equivalent.)

**Fix:** hoist Back arbitration to the host: exactly one enabled
handler per visible-stack state, topmost surface first (Disarm >
Airlock > Launcher > host). At minimum, the launcher's handler must
disable itself while `airlockState.isVisible ||
disarmChallenge.isVisible`.

### D-16 — Duplicate display names are indistinguishable tiles: same name, near-identical icons, no disambiguator
**Severity: LOW (edge case — wrong-door risk)**

The partition deliberately keeps same-named rows (proven by
`LauncherPartitionTest.partition_appsSharingADisplayName_areAllKept`
— two "Files" rows survive), and the tile prints only icon +
`appName` (`LauncherBlockScreen.kt:696-722`). Two "Files" / two
"Settings" / a browser and its beta present as twins; TalkBack
announces them identically (D-12), and a sighted user must guess
which package a tile launches.

**Fix:** when a page contains duplicate `appName`s, print a
distinguishing caption under the duplicates only (category or a
short package suffix). Unique names keep the clean tile.

### D-17 — The monogram fallback splits surrogate pairs: `appName.take(1)` prints tofu for emoji- or astral-first names
**Severity: LOW (edge case — rendering)**

`LauncherBlockScreen.kt:705`: `text = app.appName.take(1).uppercase(Locale.US)`.
`take(1)` takes one UTF-16 code unit: a name beginning with an
astral character (emoji-branded apps, some CJK extension names)
yields a lone surrogate, rendered as a broken glyph in exactly the
fallback meant for when the icon failed. Names beginning with
whitespace or a combining mark produce a blank/odd monogram too.

**Fix:** take the first grapheme cluster (`BreakIterator.getCharacterInstance`)
and fall back to "?" when it isn't a letter/digit.

### D-18 — Zero-count doors are still live doors into empty rooms
**Severity: LOW (edge case — zero allowed / zero peek)**

HOME composes both plates unconditionally
(`LauncherBlockScreen.kt:~440-455`): with `allowedVisible.size ==
0` (e.g. every OPEN row is a barred browser) or `peekCount == 0`,
the plate still invites a tap that opens a room whose entire content
is "NOTHING IS OPEN IN THIS SESSION" / "NO PEEK APPS ARE SET", from
which the only move is back. The count 0 is printed honestly — the
*invitation* is the dishonest part; a door that can only lead to an
apology should say so on its face.

**Fix:** at count 0, the plate's sub prints the empty fact
("NOTHING IS OPEN") and the plate is non-clickable (absent as a
door, present as a print) — or the plate is omitted and the fact
prints in its place.

### D-19 — Two sources of truth for the launcher's page, and `initialPage` is not a remember key: a door request can be silently ignored when the launcher is already open
**Severity: LOW (correctness — latent; D-02 currently masks it)**

`MainActivity` holds `launcherPage` (`:286`); `LauncherBlockScreen`
holds its own `page`, initialised from `initialPage` via
`remember(visible, session?.id)` (`:202`) — `initialPage` is **not**
among the keys. If a door callback fires while the launcher is
already visible (`visible` never toggles, same session), the
requested room is ignored and the internal page stays where the
user left it. Today the Airlock stack (D-02) hides this; the day
D-02 is fixed by dismissing the Airlock instead of stacking, this
becomes the next "the door went to the wrong room" bug.

**Fix:** one source of truth — either the host owns the page
(controlled component) or the surface owns it and the host sends
one-shot navigation events. If `initialPage` stays a parameter,
key the remember on it.

### D-20 — The seal-weight haptic fires on entry paths that are not seals
**Severity: LOW (feel honesty — the haptic vocabulary lies)**

`SealHaptics.sealWeight` is documented as "the seal's own weight,
for the dial at the moment of opening" and is fired from
`LaunchedEffect(visible, session?.id)` (`LauncherBlockScreen.kt:237-239`).
That effect also (re)runs when: the launcher opens directly onto a
grid from an Airlock door (no dial is ever shown — a THUD for a door
tap), the session id changes under an open launcher, and any
configuration recreation (D-07). A vocabulary this deliberate
(CLICK for doors, TICK for tiles, THUD for the seal) spends its
heaviest word on rotations and room changes.

**Fix:** fire `sealWeight` only on a genuine transition into the
HOME dial (closed → open at `LauncherPage.HOME`), once per opening.

---

## ACQUITTALS — tried to convict, could not

- **A2-01 · The partition itself.** `launcherPartition`
  (`LauncherBlockScreen.kt:104-114`) is a complete, disjoint
  partition by `resolvedTier`, case-insensitively name-sorted, and
  `LauncherPartitionTest.kt` genuinely proves it: tier routing,
  completeness/disjointness, empty list, resolvedTier-over-flag,
  garbage-tier fallback (never to PEEK), sort order, duplicate-name
  retention, and no mutation of the caller's list. **Acquitted.**
- **A2-02 · The two pure derivations.** `launcherProgress` clamps
  to [0,1] and returns 0 for non-positive targets;
  `launcherRemainingPrint` coerces negatives to "00:00" and formats
  MM:SS / H:MM:SS — both exhaustively unit-tested including
  `Int.MIN_VALUE` / `Int.MAX_VALUE` edges. **Acquitted.** (How the
  dial *feeds* them is D-09/D-10; the functions themselves are
  honest.)
- **A2-03 · The green law, on every colour the launcher itself
  paints.** The launcher file contains **zero** `Color(0x…)`
  literals (grep-verified); every colour is a night token, and each
  used token satisfies green < max(red, blue): Background
  `171412` (20 < 23), SurfaceBright `362C27` (44 < 54),
  SurfaceSheet `4A423C` (66 < 74), Primary `FFE4CF` (228 < 255),
  PrimaryContainer `F47442` (116 < 244), TextSecondary `9C8E84`
  (142 < 156), OnSurfaceVariant `776A60` (106 < 119 — passes the
  green law; its *contrast* is D-14). **Acquitted on green.**
- **A2-04 · Fonts.** All 21 `Text` call sites in
  `LauncherBlockScreen.kt` name one of Sora / Atkinson
  Hyperlegible / Anonymous Pro (grep count 21 = 21), each in its
  lawful role (Sora display/intention, Atkinson interface,
  Anonymous numerals/counts). The Airlock door chips print via
  `rememberMixedTypographyText` (`MonolithCustomComponents.kt:77-107`),
  Atkinson words + Anonymous badges. No fourth family anywhere in
  the slice. **Acquitted.**
- **A2-05 · Icon colours do not leak into surfaces.** Per the
  standing warning in REVAMP-CRITIC A-07: icons render *as icons*
  on fixed token backgrounds (`NightColorSurfaceBright` tile
  wells, `LauncherBlockScreen.kt:~690`); no icon pixel is sampled
  into any surface, badge, ring, or text colour anywhere in the
  file. That foreign colour — including green app icons — exists
  on screen is what an app icon *is*; the chrome never wears it.
  **Acquitted.** (Icon *decoding cost* is D-06; colour leakage is
  not chargeable.)
- **A2-06 · The OPEN tap-time gate, as written.** `MainActivity.kt:838-871`
  re-reads the live row at the tap, requires `resolvedTier == OPEN`
  at that instant, a live seal, `!navigationLocked()`, and bars
  `com.android.vending` + `AppCategory.BROWSERS` in depth. C-03's
  cure, landed faithfully. **Acquitted** (its silent *refusal
  feedback* is D-05; the gate's logic is not).
- **A2-07 · Grid sourcing.** The grids read the VM's `apps` flow
  only (`MainActivity.kt:835`), keyed by `packageName`
  (`LauncherBlockScreen.kt:615`), labels from the row's `appName` —
  no PackageManager query in any launcher composable. C-15's cure
  holds as built. **Acquitted.**
- **A2-08 · Airlock door timing.** The launcher doors inside the
  Airlock are **absent** (not disabled) until `countdownSecs == 0`
  (`AirlockAndDisarmOverlays.kt:731-749`), inheriting the breathing
  wall exactly as the peek chip does. C-12's timing half, landed.
  **Acquitted** (C-12's record half is D-02).
- **A2-09 · Emergency Break composition on every launcher page.**
  HOME foot row plus the grid pages' foot row
  (`LauncherBlockScreen.kt:~625-660`, tag
  `launcher_emergency_break`, unique because one page composes at
  a time), and `DisarmChallengeOverlay` composes after the
  launcher at the root (`MainActivity.kt:~932`), so the valve
  overlay can never be buried by the launcher. C-14's composition
  half, landed. **Acquitted** (HOME *clipping* under overflow is
  D-11).
- **A2-10 · The stopwatch ring.** No arc is drawn for a stopwatch:
  `progress` is forced to 0 (`:252`) and the Canvas draw is
  additionally guarded by `session?.isStopwatchMode != true`. No
  fabricated denominator (no /1200 hack) exists in this file.
  C-08's cure, landed. **Acquitted** (the stopwatch *numeral's*
  ticking is D-09).
- **A2-11 · Haptics cannot crash the surface.** `SealHaptics`
  wraps vibrator acquisition, primitive-support probing, and
  vibration in `runCatching` with a predefined-effect fallback
  (`LauncherBlockScreen.kt:~140-185`), and `VIBRATE` is declared
  in the manifest. No unguarded haptic path found. **Acquitted**
  (when the THUD *fires* is D-20).
- **A2-12 · The Back law inside the launcher itself.** The handler
  exists only while the surface is visible (the `if (!visible)
  return` at `:200` precedes it), returns grids to HOME, and
  swallows HOME — no path in this file finishes the Activity.
  C-05's cure, landed as specified. **Acquitted** (the handler's
  behaviour *under other overlays* is the host's defect, D-15).

---

## CLOSING

The slice's foundations — partition, derivations, tokens, type,
gates — survived a hostile read; several C-cures landed better than
their specs. The slice's *wiring* did not: a launcher whose two
entrances are respectively stripped and stacked is, as built, a
room with no working door, and its one forced exit writes a lie
into the session record. Fix D-01 and D-02 before any polish pass;
everything else queues behind a launcher the user can actually
enter and truthfully leave. — *Post-build critics*
