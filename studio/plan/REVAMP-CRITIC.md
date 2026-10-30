# REVAMP-CRITIC — the 4-seat adversarial bundle on UMBAU's launcher revamp

**Bundle:** Brutal critic · Purpose-questioning · Thoughtful · Deep-thinker.
Each charge is signed by the seat that files it. Scope unrestricted; every
charge carries file evidence from this tree (`agent/design-revamp` @ e29acdf)
or it does not file.

**Target, as briefed:** full-screen launcher block surface while sealed —
hero dial (progress ring + remaining numeral + intention); door plates to
separate ALLOWED (OPEN tier) and PEEK (PEEK_2M tier) app-grid screens; OPEN
tap launches via PackageManager launch intent; PEEK tap routes through
`viewModel.triggerAirlockIntercept(pkg, name, badge, InterceptPath.APP_TIER)`;
entry via Guard-line tap while sealed and via plates inside the Airlock
overlay; exits claimed to remain RETURN TO FOCUS, the Emergency Break 60s
valve, and session end. Night tokens, Sora/Atkinson/Anonymous Pro, never green.

**Verdict in one line:** the PEEK plumbing is honest (see Acquittals), but the
OPEN plate as specified is an exit with a menu on it, the hero dial has no
truthful derivation named, and the PEEK *plate* — as opposed to the peek
*door* — should be killed. 15 charges: 2 CRITICAL, 1 PURPOSE-CRITICAL,
7 HIGH, 5 MEDIUM.

---

## CHARGES

### C-01 — The ALLOWED plate ships a browser: OPEN tier includes the open internet
**Seat:** Brutal critic · **Severity:** CRITICAL (new exit)

Chrome is *born* OPEN: `AppCatalogReconcile.kt:53-64` factory presets —
`com.android.chrome` → `AppRulePreset(AppBlockTier.OPEN, 60)`. Every other
browser falls to the same place: `categorizePackage` maps
chrome/browser/firefox/brave to `AppCategory.BROWSERS`
(`MonolithRepository.kt:514-528`), and the reconcile's default tier for any
category that is not SHORT_VIDEO/GAMES/MESSAGING is OPEN
(`AppCatalogReconcile.kt:85-93`). The site blocker does not close this:
it only inspects 5 named browser packages and only their URL-bar nodes, it
skips the bar while the user is typing, and the enforced list is the user's
stored list with *no factory fallback* — an emptied list blocks nothing
(`MonolithAccessibilityService.kt:273-292` for the per-package tier door the
site door hangs off; the site door's own matching is in the same file's
browser step). So one tap on the ALLOWED plate hands a sealed, tired user a
general-purpose browser whose only fence is a partial domain list. That is
not an "allowed app". That is the whole internet with 8 names crossed out.

**Cure:** browsers never appear on the ALLOWED plate — exclude
`AppCategory.BROWSERS` and every package `isSupportedBrowser` knows (plus
any package whose launcher label/pkg matches the `categorizePackage`
browser branch) from the launcher grid regardless of tier, and print the
exclusion on the plate ("BROWSERS STAY SHUT — SET ON SHIELD" is a lie if
Shield says OPEN; print no reason, just absence, and bar the same packages
in the VM launch verb in C-03 so no second surface can re-add them).

### C-02 — OPEN apps are exit factories: stores, installers, and content hosted inside an OPEN process
**Seat:** Brutal critic · **Severity:** CRITICAL (new exit)

Enforcement in this app is per-*package* and nothing else: the accessibility
service reads `event.packageName`, looks up that one row, and OPEN returns
(`MonolithAccessibilityService.kt:273-275`). Three consequences the plan
never prices:

1. **Install vector.** Anything in the SHOPPING bucket defaults OPEN
   (`AppCatalogReconcile.kt:85-93`, `MonolithRepository.kt:514-528`), which
   is where a store app lands. From a store, the user installs a brand-new
   app — and a package with **no row is never blocked at all**:
   `dao.getAllAppsSnapshot().find { … } ?: return@launch`
   (`MonolithAccessibilityService.kt:273`). The new app is OPEN in fact
   until the next `ensureSeededAndSyncPackages` sync
   (`MonolithViewModel.kt:768,2451` are the only callers).
2. **Hosted content.** A LOCKED service rendered inside an OPEN app's
   WebView or a Custom Tab hosted by an OPEN browser never changes the
   foreground package, so no tier check ever fires. The wall cannot see it.
3. **Settings adjacency** is only partly guarded: the catalog sync excludes
   `com.android.settings*` from discovery (`MonolithRepository.kt:217-230`),
   but vendor settings/launcher-alias packages are not in that exclusion.

**Cure:** (a) store/installer packages (`com.android.vending` and any
package `categorizePackage` cannot classify that exposes an install
surface) are barred from the plate in the VM verb, same as C-01;
(b) flip the unlisted default while sealed: in
`MonolithAccessibilityService.kt:273`, a launcher-discoverable package with
no row intercepts (APP_TIER path, badge "Unlisted") instead of returning —
unlisted must mean SHUT, never silently OPEN; (c) for hosted content there
is no per-package cure, which is exactly why C-01's browser exclusion is
mandatory rather than cosmetic.

### C-03 — The OPEN launch has no tap-time gate; the PEEK launch has two
**Seat:** Brutal critic · **Severity:** HIGH (HOLD weakening)

The peek grant is double-gated: the offer is computed at intercept
(`MonolithViewModel.kt:141-151`) and **re-derived from the current row,
cooldown and pill at grant time** (`MonolithViewModel.kt:2387-2395` — "a
stale airlock state can never sell a lease"). The plan's OPEN path is a raw
PackageManager launch intent fired from the composable: no re-read, no
gate. Between grid composition and the tap, the row can be tightened
(ratchet, `MonolithViewModel.kt:2064-2095`; a loadout landing on a session
edge, `MonolithRepository.kt:1107-1150`) — and the composed grid would
still launch it as OPEN on a stale print. A door that launches on a cached
tier is a door that launches SHUT apps on the frames where it matters.

**Cure:** no launch intent in any composable. Add
`MonolithViewModel.launchAllowedApp(packageName)`: it re-reads the DAO
snapshot, requires `resolvedTier == OPEN` *at the tap instant*, requires a
live sealed session and `!navigationLocked()`, excludes the C-01/C-02
barred packages, and only then fires the intent. Refusal stays on the
launcher surface and prints why in one line.

### C-04 — After an OPEN launch there is no door back, and "you can't exit" is false
**Seat:** Deep-thinker · **Severity:** HIGH (TELL-THE-TRUTH — the plan's own exit list)

The plan claims "the only exits remain RETURN TO FOCUS (on the door), the
Emergency Break valve, and session end." RETURN TO FOCUS is the Airlock's
chip (`transmuteImpulseAndReturn`, `MonolithViewModel.kt:2428-2437`). Once
an OPEN app is foregrounded there is no Monolith surface inside it, no pill
(the `FloatingPeekPillState` at `MonolithViewModel.kt:255-256` has **no
rendering composable anywhere in this tree** — grep it), and no return
affordance of any kind; the way back is the system Home/Recents — i.e. the
*real* launcher, which also falsifies the "un-exitable launcher" framing:
MainActivity declares no HOME category and holds no launcher role
(`AndroidManifest.xml`, MainActivity activity block), and the accessibility
service ignores SystemUI outright
(`MonolithAccessibilityService.kt:182`). Home leaves Monolith whenever the
user presses it, sealed or not. The revamp does not create that fact, but
its spec *claims the opposite*, and a spec that misstates its own exit list
will be built and tested against the misstatement.

**Cure:** strike "the only exits remain…" from the plan and replace it with
the true list, including Home and post-launch system navigation; and give
an OPEN launch the peek's discipline — on grant, raise the same pill state
with the session's remaining time and a RETURN action that re-foregrounds
Monolith — or do not call the surface a launcher or a door. If neither is
acceptable: **no cure — kill the feature's exit-list claim and the
"launcher" name; ship it honestly as an Allowed-apps drawer.**

### C-05 — The new navigation depth has no Back law, so Back becomes an exit
**Seat:** Thoughtful · **Severity:** HIGH (new exit)

The host owns exactly two Back handlers: PARK open, and non-DECK
destinations (`MainActivity.kt:347-351`). The revamp adds two levels of
depth (launcher home → grid screen) inside the sealed Deck and the plan
names no Back behaviour for either. Default Activity Back on the Deck
finishes MainActivity — out to whatever is behind it (the real launcher, a
previously foregrounded app). A user who taps into the PEEK grid and
presses Back expecting the launcher home gets ejected from Monolith
instead: the one gesture every Android user treats as "one level up"
becomes the fastest exit the sealed surface has ever had.

**Cure:** pin it in the spec and the code: while sealed, Back on a grid
screen returns to the launcher home; Back on the launcher home is
swallowed (consumed, no-op, no sound that implies travel); Back inside the
Airlock-hosted plates returns to the Airlock. Implement as explicit
`BackHandler`s keyed to the launcher's own state, tested beside the two
existing handlers in `MainActivity.kt:347-351`.

### C-06 — Guard-line entry conflates two different locks and turns a frozen verdict into a door
**Seat:** Thoughtful · **Severity:** HIGH (HOLD + TRUTH)

Three facts the plan's "previously a dead tap" glosses over:

1. The tap is dead in **two** places, on purpose: the header strips the
   click while sealed (`MonolithCustomComponents.kt:285-293`; its own
   spec comment, `:242` — "While sealed the line is frozen at its
   seal-frame value and the tap is gated — print, not a door"), and
   `openPark()` refuses (`MainActivity.kt:289-303`).
2. The header's `isSealed` is not `sealed` — it is `doorLocked`
   (`MainActivity.kt:250-265,422-433`): sealed **or** the finish machine's
   detention, the HB-1/HB-2 fix that stopped the band printing DOOR: OPEN
   over a held frame. `navigationLocked()`
   (`MonolithViewModel.kt:1388-1402`) also covers proving and jammed arms.
   A builder who re-enables the tap on `sealed` alone re-opens the
   launcher over a finish's held frame and over a proving seal.
3. The Guard line the user taps is a **frozen** print — while sealed,
   `guardLinePrint` serves the seal-frame capture, not a live measurement
   (`MonolithViewModel.kt:663-676`). Acting (launching apps) on a verdict
   frozen at the seal, without re-measuring the wire, is acting on stale
   proof.

**Cure:** entry predicate is a new tap-time synchronous check —
`sealed && !viewModel.navigationLocked()` evaluated inside the handler
exactly as `openPark()`'s HB-2 comment prescribes — never the composed
`doorLocked` value alone; on entry, run the Watch sweep and require a
fresh warrant read before any launch verb arms (launches refuse while the
warrant is UNPROVEN, in print, on the launcher surface).

### C-07 — The hero dial names no derivation, and the two candidate derivations disagree for every Safe Break
**Seat:** Deep-thinker · **Severity:** HIGH (TELL-THE-TRUTH)

This tree already convicted this exact lie once. Per-block
`remainingSeconds` reads 0 for the whole of a Safe Break; the model's own
comment says a surface with no block context must read the span
derivations or it shows a break as FINISH (`MonolithModels.kt:388-429`).
The surfaces split: the Deck's running readout uses per-block
`remainingSeconds` (`TactileDeckScreen.kt:306-312`), while the Deck's own
TIME cell and the desk clock use `spanRemainingSeconds`
(`TactileDeckScreen.kt:522-525`; desk clock in
`AirlockAndDisarmOverlays.kt:105-115`). The plan says "remaining numeral"
and names neither. Copy the Deck readout and the launcher prints 00:00 —
session apparently over — for up to ten minutes per break, with no BREAK
eyebrow in the plan to qualify it. Copy the span and the launcher's number
contradicts the Deck hero one tap away for the same minutes. Either way
one of the app's surfaces lies, by construction, on a schedule.

**Cure:** the spec must name the derivation: hero numeral =
`spanRemainingSeconds`, ring = `spanElapsedSeconds / spanTargetSeconds`
(both `MonolithModels.kt:399-429`), the BREAK eyebrow printed exactly as
the Deck prints it (`TactileDeckScreen.kt:615`), and the intention read
from `activeSession.intention` — the sealed row's own words, following
the source order `triggerAirlockIntercept` already uses
(`MonolithViewModel.kt:2359`) — never the live editable `intentionText`,
which the task collector rewrites mid-session.

### C-08 — A progress ring for a stopwatch is a fabricated denominator
**Seat:** Deep-thinker · **Severity:** HIGH (TELL-THE-TRUTH)

A stopwatch session has no set end — the Airlock states it in words
("SESSION RUNNING · NO SET END") and `triggerAirlockIntercept` refuses to
invent an end for it (`MonolithViewModel.kt:2360-2370`,
`sessionIsStopwatch`). The existing ring/track hacks divide elapsed by a
made-up 1200 (`TactileDeckScreen.kt:335-336`; the desk clock's identical
constant in `AirlockAndDisarmOverlays.kt:128-137`): the ring sits pinned
at 100% from the 20-minute floor onward while the session runs for hours,
printing "complete" about a session whose defining property is that it
isn't. The plan mandates the ring for the hero in all states. It also
never states the ring's direction: the Deck's timed fraction is
*remaining*/target used as a fill (`TactileDeckScreen.kt:338-339`), so a
builder who fills elapsed/target — the natural reading of "progress
ring" — produces a hero whose % contradicts the Deck's % on the same
session in the same minute.

**Cure:** stopwatch hero = count-up numeral only, no ring, no %. Timed
hero: one derivation only — the Deck's (remaining fraction,
`TactileDeckScreen.kt:338-339`) — labelled REMAINING if a % prints at all.
During a Safe Break the ring must keep reading the span (C-07), never the
finished block's fraction.

### C-09 — Plate counts have three possible sources in this tree, and they disagree by design
**Seat:** Thoughtful · **Severity:** MEDIUM (TELL-THE-TRUTH)

A plate that prints counts ("ALLOWED n", "PEEK n") can drink from:
the live split (`appsSplit`, `MonolithViewModel.kt:555`), the combined
live count (`appsCellCount`, `MonolithViewModel.kt:541` — LOCKED+PEEK
summed, the wrong shape for either plate), or the seal-frozen split on
`sealFreeze` / the row's `heldAppsAtSeal`. These are *supposed* to
differ: tier tightening mid-session is lawful (the ratchet,
`MonolithViewModel.kt:2064-2095`), so a live plate count and the Deck
imprint's frozen count will legitimately part mid-session. Two numbers
for the same-named fact, in the same session, with no tense marking, is
the exact failure UNA2-02/UNA2-04 spent a car killing.

**Cure:** plates print no counts at all — the doors are named, not
numbered. If a count is non-negotiable, it is the live split from
`appsSplit` only, printed with an explicit NOW tense, and never a summed
LOCKED+PEEK figure on either plate.

### C-10 — During a Safe Break the PEEK route sells what is already free — and lets the cooldown burn during free use
**Seat:** Brutal critic · **Severity:** HIGH (HOLD + TRUTH)

During a Safe Break, PEEK_2M apps are simply open: the accessibility
service returns for them (`MonolithAccessibilityService.kt:278-279`) and
the shield's blocked set excludes them
(`MonolithShieldService.kt:170`). But `peekOfferFor` has **no break
input** — its whole signature is path, entity, cooldown, pill
(`MonolithViewModel.kt:141-151`) — so the launcher's PEEK route during a
break offers a lease, and `startPeekLease` arms the 20-minute cooldown
immediately (`MonolithRepository.kt:1194-1203`; cooldown derivation
`:1176-1182`). Two harms, both concrete:

1. **The price print lies by omission.** The offer prints "TAKING THE
   PEEK STARTS A 20 MINUTE COOLDOWN ON THIS APP" for access the user
   already holds free for the rest of the break.
2. **Cooldown is served during free use.** Take the "peek" early in a
   break: the 120s lease expires, the pill retires the lease, and the
   user *keeps using the app freely* until the break ends — while the
   cooldown clock runs. Up to ~8 minutes of the 20 are served inside
   the app the cooldown exists to keep him out of. One peek buys up to a
   full break of access at a discounted cooldown.

**Cure:** add `isInSafeBreak` to `peekOfferFor`'s inputs. During a break
the PEEK plate behaves as the break law says: those apps launch directly
(no lease, no cooldown, no Airlock), the plate is labelled for the break
state, and the Airlock route for a PEEK app during a break refuses with
the honest print — the app is already free until the break ends.

### C-11 — The one-peek-at-a-time gate is memory-only; process death opens a second simultaneous peek
**Seat:** Deep-thinker · **Severity:** MEDIUM (HOLD weakening, pre-existing — the launcher makes it a standing door)

`pillActive` in both peek gates comes from `_floatingPeekPill`, an
in-memory `MutableStateFlow` (`MonolithViewModel.kt:255-256`, read at
`:2326` and `:2393`). The lease itself lives in Room and is honoured by
the accessibility service per-package
(`MonolithAccessibilityService.kt:225-228`). Kill Monolith's process
mid-lease (or let Android kill it) and the pill state is reborn false
while the first lease still runs: the launcher — a door that is now
always one Guard tap away, instead of an accident of reaching for a
blocked app — will sell a second PEEK grant on a second app, and both
leases run at once. The gate that was supposed to serialize peeks
serializes only within one process lifetime.

**Cure:** derive the pill gate from the store, not the state flow: in
both gates, `pillActive = apps.any { it.activePeekUntilElapsedRealtime >
SystemClock.elapsedRealtime() }` (any live lease row), keeping
`_floatingPeekPill` as a rendering of that fact, never its source.

### C-12 — Plates inside the Airlock defeat its breathing wall and falsify its intercept record
**Seat:** Purpose-questioning · **Severity:** HIGH (HOLD + TRUTH)

The Airlock is a friction device: a 5-second breathing countdown runs
before the peek chip even composes (`AirlockAndDisarmOverlays.kt:570-578`;
the chip is gated at `:671`). The plan puts door plates *inside* this
overlay and says nothing about gating them on the countdown. Plates live
at second 0 means the wall now guards only the app he was caught
reaching for, while every OPEN app is one tap away instantly — friction
for the named sin, none for its substitutes.

Worse, the record: leaving the Airlock via RETURN TO FOCUS fires
`transmuteImpulseAndReturn`, which writes an intercept log with outcome
"Stayed Focused" for the *original* package
(`MonolithViewModel.kt:2428-2437`), and the receipt's deflections are
assembled from those logs. A user who entered the Airlock, browsed the
plates, and left into an allowed app will be recorded as having stayed
focused at that intercept. The session's own history becomes a lie the
launcher wrote.

**Cure:** plates are absent (not disabled — absent) until the countdown
reaches 0, inheriting the wall exactly as the peek chip does; and any
exit from the Airlock into the launcher logs its own outcome
("Left via Allowed door" / "Left via Peek door", with the destination
package) in place of the transmute record for that intercept. RETURN TO
FOCUS may only write "Stayed Focused" when the user actually returns to
the Deck.

### C-13 — A browsing menu inside the seal contradicts the app's own law: the tired user executes, he does not shop
**Seat:** Purpose-questioning · **Severity:** PURPOSE-CRITICAL

The studio's settled law, in the plan's own words: "the rested user
decides, the tired user executes" (`studio/plan/MASTER-PLAN.md:54`);
the critics' measuring instrument is "HOLD absolute, zero mid-session
decisions" (`studio/plan/CRITICS-PLAN.md`, §1 header). Tier choices are
rested decisions — they live on Shield, behind the one-way ratchet. The
launcher moves a *catalogue* into the sealed moment: two grids of icons,
PEEK badges on exactly the apps the presets mark as cravings (YouTube
PEEK, WhatsApp PEEK — `AppCatalogReconcile.kt:55,59`), rendered
beautifully, one Guard tap from anywhere. That is not a door for a tool
he already decided he needs; it is a menu presented at the point of
weakness, endorsed by Monolith's own hand and typography. Every prior
surface understood this: PARK while sealed is "read-only: print and
reading, no FIX, no sled" (`ParkDrawer.kt:96-98`). The launcher makes the
same tap an act.

The distinction that saves part of the feature: a *peek* already has its
lawful door — the intercept itself, which fires only when he has
actually reached for the app. Intent precedes the door. A PEEK *plate*
reverses that order: the menu precedes, and manufactures, the intent.

**Cure:** for the ALLOWED plate, Thoughtful's mitigation in C-03/C-05/
C-06 (gated verb, back law, honest counts) is the minimum, and the plate
must list study tools only per C-01/C-02. For the PEEK plate:
**no cure — kill the feature.** Peeks stay reachable exactly one way —
by reaching for the app and meeting the Airlock — which is the design
the rest of this codebase already proved.

### C-14 — The Emergency Break valve's reachability from inside the launcher is unspecified
**Seat:** Thoughtful · **Severity:** MEDIUM (HOLD — the one lawful exit must stay reachable)

The valve lives on the Deck (`TactileDeckScreen.kt:1047-1054`), as does
the grace-window abort chip in a session's first ten seconds. A
full-screen launcher composed over the Deck buries both. The plan
asserts the valve "remains" an exit but never pins: how many taps from
the deepest grid screen to a live valve, or whether the
`DisarmChallengeOverlay` — today composed at the root, above the
viewport (`MainActivity.kt`, full-screen overlays section) — still
renders above the launcher if the launcher is itself built as a root
overlay. An exit that exists in the tree but takes four discoveries to
reach at the moment of crisis is an exit in the spec and a wall in the
hand.

**Cure:** pin both in the spec: the Disarm overlay composes above the
launcher always (root, last, as today), and every launcher screen
carries the same valve affordance the Deck carries, at the same address,
so the path to the 60s hold is ≤1 tap from any launcher state — grace
abort included during its window.

### C-15 — The grid's source is unnamed; the wrong source invents tiers
**Seat:** Brutal critic · **Severity:** MEDIUM (TRUTH + HOLD)

Two app lists exist in this tree: the Room rows behind
`MonolithViewModel.apps` (`MonolithViewModel.kt:424`), reconciled to
exactly the installed launcher set with ghosts pruned
(`MonolithRepository.kt:217-260`, `AppCatalogReconcile.kt:66-93`), and
the raw PackageManager launcher query that reconcile consumes
(`MonolithRepository.kt:217-230`). A grid built from the PM query shows
packages that have no row — no tier — and whatever default the builder
picks for them becomes a ruling nobody made (this codebase's services
already default an unlisted package to *unblocked*,
`MonolithAccessibilityService.kt:273`, the hole in C-02). Icon/label
sourcing is the same trap in miniature: the row's `appName` is the
record; a live-loaded label is a costume any update can change.

**Cure:** the grids read the VM's `apps` flow only, keyed by
`packageName` (the row's primary key, `MonolithModels.kt`,
`BlockedAppEntity`), filtered by `resolvedTier` at composition *and*
re-validated at the tap by C-03's verb. A package with no row does not
appear. No PM query in any launcher composable.

---

## ACQUITTALS — tried to convict, could not

- **A-01 · The PEEK grant's gates themselves (all four seats).** Verified
  line by line: `peekOfferFor` (`MonolithViewModel.kt:141-151`) requires
  a lease-honouring path (`InterceptPath.APP_TIER`/`SHIELD_APP`,
  `MonolithAccessibilityService.kt:46-63`), a non-null row, tier exactly
  PEEK_2M, cooldown exactly 0, no active pill. `start2MinutePeek`
  re-derives all of it at grant time (`MonolithViewModel.kt:2387-2395`).
  Routing the launcher's PEEK tap through `triggerAirlockIntercept(...,
  InterceptPath.APP_TIER)` therefore inherits the real gates — no
  cooldown bypass, no pill bypass, no stale-state sale. **Acquitted**
  (the pill gate's *memory-only source* is a separate charge, C-11; the
  Safe Break hole is in the function's inputs, C-10 — neither is a
  bypass of the gates as written).
- **A-02 · Cooldown arithmetic.** `startPeekLease` pre-arms the cooldown
  at lease start (`MonolithRepository.kt:1194-1203`) and the remaining
  computation anchors on `max(lastPeekEnd, activePeekUntil)`
  (`:1176-1182`), so ending a peek early cannot reset it to zero and
  re-peeking inside the window is refused by both gates. **Acquitted.**
- **A-03 · Package aliasing / duplicate launcher activities.** One row
  per package, by construction: the reconcile dedupes by package
  ("A package with several launcher activities is still one app",
  `AppCatalogReconcile.kt:73-74`) and `packageName` is the row's primary
  key. An activity-alias shares its package's tier; there is no alias
  that lands on a different ruling. **Acquitted.**
- **A-04 · Deep-link chains into LOCKED apps.** Leaving an OPEN app into
  a LOCKED one changes the foreground package, and both enforcers meet
  it: the accessibility tier door
  (`MonolithAccessibilityService.kt:273-292`) and the shield loop's
  blocked set (`MonolithShieldService.kt:167-190`). The chain breaks at
  the locked door. **Acquitted** — the exception is content that never
  changes the foreground package (hosted WebViews/Custom Tabs), which
  is charged as its own mechanism in C-02.
- **A-05 · OPEN launch as a new *capability* against the tiers.**
  Brutal tried to convict the launch intent itself and could not: OPEN
  apps are excluded from both blocked sets
  (`MonolithShieldService.kt:169`, `MonolithAccessibilityService.kt:275`)
  and were always reachable via the system launcher during a session.
  The intent adds no capability against LOCKED/PEEK. What it adds is
  endorsement, advertisement, and zero friction — convicted on those
  grounds in C-01, C-02 and C-13, not here. **Acquitted on capability.**
- **A-06 · The Emergency Break token.** No path in the plan touches
  `completeEmergencyBreak` or the grace cancel except through the
  existing valve overlay and Deck chip; the launcher creates no new
  disarm surface and no token shortcut. **Acquitted** (the valve's
  *reachability* from inside the new surface is C-14).
- **A-07 · Theme and type.** The plan's night tokens exist and obey the
  zero-green law (`Color.kt:7,32-50`), and all three families are real
  (`Type.kt:20,33,43` — Sora, Atkinson Hyperlegible, Anonymous Pro).
  Nothing in the plan as written introduces green. **Acquitted**, with
  one standing warning to the builder: app icons are foreign colours —
  they render as icons, never sampled into surfaces, badges or the ring.
- **A-08 · Session integrity.** Nothing in the plan writes the session
  row, extends a span, or interferes with the finish machine's
  derivations; session end remains an exit that fires on the row's own
  facts. **Acquitted.**

---

## SEAT CLOSINGS

**Brutal critic:** Kill the browsers and the store off the plate, gate
the launch at tap time, and fix the Safe Break sale — or this "launcher"
is the best-built exit the app has ever shipped. — *Brutal*

**Purpose-questioning:** The PEEK plate is a menu where a door already
exists. Kill the plate; keep the Airlock. A blocker that advertises has
changed sides. — *Purpose*

**Thoughtful:** The OPEN door can be made honest — back law, tap-time
locks, no counts, valve in reach (C-03, C-05, C-06, C-09, C-14). Build
it to that standard or don't build it. — *Thoughtful*

**Deep-thinker:** The deepest problem is not any single hole; it is that
the spec's sentences about itself ("only exits remain", "remaining",
"progress") are unexamined in a codebase that has spent three cars
learning that an unexamined sentence becomes a lie on a surface. Name
every derivation, or the dial will lie beautifully. — *Deep-thinker*
