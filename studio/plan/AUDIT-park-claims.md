# AUDIT — PARK & SETTINGS: EVERY PRINTED CLAIM vs THE CODE THAT KEEPS IT

**Auditor:** Una — Critics, consistency seat. Working directly under Ren.
**Date:** Oct 29, 2026 (~21:06 IST). **Method:** reading audit only, no compilation.
**Ground:** PARK (the rail's end-detent drawer) and every settings-flavoured
surface — the Guard board and header line, toggles and their descriptions,
counts, labels, status lines, the Shield settings sections, and PARK's spec
plate. Trunk = `~/workspace/clone-inspect/Focus-app` (batch-7 state, DB v10).
**Standard:** a charge files with file+line on both sides — where the words
print, and where the code keeps or breaks them — or not at all.

**Result: 9 charges (2 S1, 4 S2, 3 S3) · 14 acquittals.**

Cross-references, not duplicates: the cabinet's invented
"REQUIRES 300 MIN · 12 INTERCEPTIONS" is already charged as DRW-01 in
Truth Car 2 — not re-filed here. Band's HB-1/HB-2 own the Guard DOOR's
mechanics; the Guard LINE's words are mine and are acquitted (A-03).
Salz owns the desk clock's light budget; no clock words fell in my hands.
Hale owns the Drawer's own prints; UNA-08 charges only PARK's plate line
about the record, not the Drawer itself.

---

## CHARGES

### UNA-01 (S1) — SHORTS + REELS: a setting that exists in no layer

PARK's board prints SHORTS + REELS as a setting line of the interceptor
family: its word is derived from a *setting* plus parent warrants —
OFF is defined as "setting off, or any parent MISSING or STALE"
(`service/WatchStore.kt:409–418`, inputs at `:162`). The setting is a
phantom at all three layers:

- **The toggle owns nothing.** `MonolithViewModel.kt:1287–1289` flips an
  in-memory StateFlow (declared `:165`, default `true`, forced back to
  `true` by `applySmartLoadout` at `:1887`). It is persisted nowhere —
  no Room or prefs write exists for it — so process death silently
  restores ON.
- **The toggle is rendered nowhere.** Shield accepts the value and the
  callback and never uses them (`ui/screens/ShieldMatrixScreen.kt:71, 84`);
  the Deck does the same (`ui/screens/TactileDeckScreen.kt:112`). The one
  titled switch view in the codebase — "SHORTS & REELS BLOCKER" /
  "Auto-back out of vertical short feeds"
  (`ui/views/BlockitSuitcaseAndSwitchesViews.kt:33, 46`) — is instantiated
  exactly once, in PARK, in chrome mode with its title suppressed, as the
  SOUND sled (`ui/components/ParkDrawer.kt:451`). If anyone ever seats it
  non-chrome, a live printed claim wakes up wired to this same dead chain.
- **The enforcer never reads it.** The sniper fires during any active
  session for YouTube/Instagram with no enabled check at all
  (`service/MonolithAccessibilityService.kt:164–177`). The settings row
  documented as "the accessibility service must honour"
  (`data/MonolithModels.kt:669`) has no shorts field
  (`data/MonolithModels.kt:670–688`).

So the board's word for this line reports a switch that cannot be
switched, is forgotten when the process dies, and gates nothing. Cure
directions (priced by builders, not me): mirror the browser flag —
a `guard_settings` column the service actually reads — or strike the
setting from the derivation and let the line print as the pure
parent-warrant it already is in fact.

### UNA-02 (S2) — SOCIAL SITES: a real flag whose switch died with the Guard screen

The chain under this one is honest — which is what makes the missing
switch chargeable. The flag is persisted (`data/MonolithModels.kt:672`),
the ViewModel inits its flow from Room
(`ui/MonolithViewModel.kt:639`), its toggle writes Room
(`ui/MonolithViewModel.kt:1292–1296` → `data/MonolithRepository.kt:750`),
and the service honours the stored value
(`service/MonolithAccessibilityService.kt:179–180`). PARK's board derives
the printed word from the same flow (`service/WatchStore.kt:163, 418`).

But the toggle is rendered on **no surface**: Shield and Deck both accept
`browserUrlBlockerEnabled` / `onToggleBrowserUrlBlocker` and ignore them
(`ui/screens/ShieldMatrixScreen.kt:72, 85`;
`ui/screens/TactileDeckScreen.kt:113`). In the shipped UI the flag can
only ever be forced ON (STUDY loadout,
`ui/MonolithViewModel.kt:1888, 1892`); nothing can turn it off. The
switch died when its Guard home was dissolved in the chrome wave and
the parameters were left wired into the void. PARK already hosts one
sled (sound); it is the natural heir for this one.

### UNA-03 (S1) — SOUND EFFECTS: PARK's OFF neither persists nor governs every engine

Printed at `ui/components/ParkDrawer.kt:432` with its ON/OFF margin word
fed by the ViewModel flow. The word is honest about the flow; the flow
is the problem, twice:

- **It persists nowhere.** `toggleSound`
  (`ui/MonolithViewModel.kt:1254–1257`) flips a var on one engine
  instance (`service/TactileSoundEngine.kt:254`, default `true`). No
  Room/prefs write for `soundEnabled` exists anywhere (occurrences:
  engine, ViewModel, MainActivity, ParkDrawer — all reads). Any process
  restart prints ON again over a setting he chose OFF.
- **A second engine can be minted behind it.** The seal transition is a
  first-caller-wins singleton (`service/SessionSealTransition.kt:288–303`):
  the routine heartbeat calls `get(appContext)` with **no engine**
  (`service/RoutineHeartbeat.kt:142`). If a routine boundary births the
  process — the normal case for the 05:00 and 20:00 arms — the
  transition binds a fresh engine with `soundEnabled = true`, and the
  ViewModel's later `get(appContext, soundEngine, repository)`
  (`ui/MonolithViewModel.kt:577–579`) returns that instance, discarding
  the UI engine. PARK then prints SOUND EFFECTS OFF while every seal
  still sounds — decided by process birth order, invisible to him.

Cure directions: persist the flag where PARK already keeps a persisted
bit (the chrome prefs), have every engine read it, and pass the one
engine at every `get()`.

### UNA-04 (S2) — The plate: "DATA: ON THIS PHONE ONLY"

Printed at `ui/components/ParkDrawer.kt:104–115` (PLATE_BODY). The app
itself keeps the claim — no INTERNET permission
(`app/src/main/AndroidManifest.xml:6–13`) and no network code anywhere
in `app/src/main/java`. The shipped *configuration* breaks it:
`android:allowBackup="true"` (`app/src/main/AndroidManifest.xml:25`)
with `app/src/main/res/xml/backup_rules.xml` still the untouched
Android Studio sample (every include/exclude commented out — the
default is *everything backs up*) and
`app/src/main/res/xml/data_extraction_rules.xml`'s cloud-backup section
empty. The Room database (sessions, finished tasks, captured
notifications) and the prefs are therefore eligible for the OS's
Google Drive device backup whenever device backup is on. The plate
states an absolute the manifest doesn't keep. One line each way:
`allowBackup="false"` or exclude-all rules.

### UNA-05 (S2) — The plate: "ROUTINES: … FIRED AS SET"

Printed at `ui/components/ParkDrawer.kt:104–115`. Falsified once in the
only live test: the 20:00 SLEEP routine did not fire on Sarthak's phone
(his report, Oct 29 20:21). The machinery is Voss's — full audit at
`plan/ROUTINE-NOFIRE-AUDIT.md`, fix spec F1–F5 adopted as WORK-QUEUE
item 18 — and I do not re-try it. My ground is the words, and the
trunk-side fact under them is mine to cite: the chain alarm is inexact,
`setAndAllowWhileIdle` (`service/RoutineScheduler.kt:44`), with no
exact-alarm surface anywhere in `service/`. Until F1–F5 land in a
build, the plate asserts as furniture a guarantee the product has
already broken. The plate line should wait for the fix, or print the
weaker true sentence.

### UNA-06 (S2) — BLOCKED WEBSITES: "0 SITES" re-arms the factory list

The section's own contract, in its header comment: "the list below is
the list the blocker enforces" (`ui/screens/ShieldMatrixScreen.kt:308–313`);
the printed count is his list's size (`:318–322`), and PARK's plate
says the same ("SITES: KEPT WITH APPS ON SHIELD"). The enforcer
disagrees at exactly one state — the emptied list:
`userDomains.ifEmpty { BlockedBrowserDomains.defaultDomains }`
(`service/MonolithAccessibilityService.kt:182–185`). Remove all eight
seeded rows one by one — every REMOVE honoured — and the blocker
silently enforces the eight factory domains
(`data/MonolithModels.kt:449–458`: instagram.com, tiktok.com,
reddit.com, x.com, twitter.com, facebook.com, snapchat.com,
youtube.com/shorts) while Shield prints "0 SITES". Print and
enforcement are exact opposites in precisely the state the editor
claims is most possible. The cure is one line: drop the `ifEmpty`
fallback — the defaults are already inserted as real rows at first
run (`data/MonolithRepository.kt:170`), so first-run behaviour is
unchanged and empty means empty.

### UNA-07 (S3) — The LOADOUT cap rests on a guess

`ui/screens/ShieldMatrixScreen.kt:103`: `selectedLoadoutIndex` is a
`remember` starting at 0 — every entry to Shield seats the cap on
STUDY, whatever loadout is actually applied. Directly above it, the
header counts are the *current tiers'* live counts (`:105–106, 147–152`),
so after applying SLEEP and returning, one section prints cap-on-STUDY
over SLEEP's numbers — two claims, one section, disagreeing. The
ViewModel does track the applied loadout
(`ui/MonolithViewModel.kt:296`, set at `:1885, 1918`) and Shield even
receives it (`ui/screens/ShieldMatrixScreen.kt:74`) and never prints
or seats from it. PARK's board refuses to print an unmeasured word
(A-01); the Shield's selector prints an unmeasured selection. Cure:
derive the cap's rest position from the applied-loadout state.

### UNA-08 (S3) — The plate: "STUDY RECORD: IN YPT"

Printed at `ui/components/ParkDrawer.kt:104–115`. YPT holds his study
*hours* — that division is his and stands. But the app keeps a study
record of its own, on this phone, permanently: the Drawer is built
from stored session rows month by month
(`data/DrawerDay.kt:1–26`; `data/MonolithRepository.kt:2484`), session
rows carry the completed-task summaries the Drawer prints
(`data/MonolithRepository.kt:2259–2265`), and his declared day words
persist in `day_notes`. On the plate's plain reading the sentence is
false; the true sentence is one word longer: "STUDY HOURS: IN YPT".
(The Drawer's own prints are Hale's ground — cross-reference only.)

### UNA-09 (S3) — "n ACTIVE" counts routines that can never fire

The ROUTINES header counts `routines.count { it.enabled }`
(`ui/screens/ShieldMatrixScreen.kt:173–177`). A routine can be enabled
with `daysMask == 0` — its row honestly prints "NO DAYS SET"
(`:403–409`) beside an ON cap (`:494–501`) — while the scheduler's own
math refuses it a boundary: `nextBoundary` skips `daysMask == 0`
routines outright (`data/RoutineDerive.kt`, nextBoundary:
`if (!routine.enabled || routine.daysMask == 0) continue`), and
`RoutineSchedule.nextStartMillis` returns null at mask 0
(`data/MonolithModels.kt:545–546`). The header's ACTIVE counts a
routine the engine treats as unschedulable. Cure: count
`enabled && daysMask != 0`, or let the header take its word from the
same derivation the scheduler uses.

---

## ACQUITTALS — claims the code keeps exactly, by name

**A-01 · The board's six permission words.** Every word is measured by
its own query, never proxied: accessibility = live service instance,
usage = AppOps verdict, admin = admin check, listener = live instance,
overlay = `Settings.canDrawOverlays`, battery =
`isIgnoringBatteryOptimizations` (`data/MonolithRepository.kt:591–600`).
Staleness is derived at render, never stored
(`service/WatchStore.kt:313–322`; threshold `:142`); MISSING never
stales; a never-measured line prints its name only
(`ui/components/ParkDrawer.kt`, board block). This is the surface the
rest of the app should be judged against.

**A-02 · "LAST PROVED … · SELF-REPORT".** The instant advances only
when a complete six-item set from one sweep era has landed — never on
a partial (`service/WatchStore.kt:380–390`, the copy at `:386–387`) —
and PARK prints it as time + the qualifier (`ui/components/ParkDrawer.kt:289–324`).
SELF-REPORT is the honest word: the sweep reads the OS's own reports,
and the plate says so instead of claiming proof from outside.

**A-03 · The header Guard line.** A pure function of (snapshot, now)
(`ui/components/GuardLine.kt:34–47`): the counted four in board order
(`service/WatchStore.kt:90–93`), `GUARD n/4 — {first lack}`, STALE
named as stale, and *nothing printed at all* while any of the four is
unmeasured. The host recomputes it live
(`ui/MonolithViewModel.kt:549–557`) and the seal freezes that same
print, not a second derivation.

**A-04 · The FIX key.** It names the first missing item in board order
(`ui/components/ParkDrawer.kt`, FIX block, key text `:411`) and opens
*that grant's own* Settings pane for all six — accessibility, usage
access, device admin (direct component with a security-settings
fallback), notification listener, overlay (package-scoped), battery
exemption (with fallback) (`MainActivity.kt:328–374`). On return, the
verdict re-reads the store live and the firm click sounds only if the
line actually repaired (`ui/components/ParkDrawer.kt:197–220`).

**A-05 · The THEME readout.** One evaluation drives both the colour
scheme and the printed word: `darkTheme` is `themeEvaluation.isNight`
mapped from the same flow PARK prints
(`ui/MonolithViewModel.kt:370–391`; print at
`ui/components/ParkDrawer.kt:494–500`). The readout's three sentences
(session owns it / named SLEEP routine owns it / no routine owns it)
are exactly the evaluation's three branches
(`ui/MonolithViewModel.kt:71–92`) — "owns" speaks of the theme, which
is the only thing the evaluation ever asks about.

**A-06 · "SCHEDULE RESET TO YOUR DAY — TAP ANY ROW TO CHANGE IT".**
Prints only when a real reseed transform ran on an existing install:
the gate is `reseedV1Done && !noteDismissed`
(`ui/MonolithViewModel.kt:421–425`), the transform marks done *without*
dismissing (`data/MonolithRepository.kt:797–848`), any row edit
dismisses (`:852–858`), and fresh installs mark both flags so the note
never claims a reset that never happened (`:176–186`). Rows do expand
on tap to the full control set (`ui/screens/ShieldMatrixScreen.kt:436–441`).

**A-07 · Routine day letters.** The chips, the label, and the firing
math share one bit order: chip index → bit
(`ui/screens/ShieldMatrixScreen.kt:531–537`), label bit 0 = MON
(`:403–409`), scheduler `bitFor` maps Calendar Monday → 0 … Sunday → 6
(`data/MonolithModels.kt:533–534`). The days a row prints are the days
it fires.

**A-08 · The live counts.** "SHOWING n" is the filtered list's size
(`ui/screens/ShieldMatrixScreen.kt:211–215`); "n SHUT • m PEEK" counts
the rows' resolved tiers at render (`:105–106, 147–152`); a routine's
ON/OFF cap prints its stored `enabled` verbatim (`:494–501`).

**A-09 · The websites editor, in every state but one.** Add and remove
write the same Room list the service reads
(`data/MonolithRepository.kt:170`;
`service/MonolithAccessibilityService.kt:182–185`). The single
exception is charged as UNA-06, not held against the editor itself.

**A-10 · The NOTIFICATIONS board word.** No setting for it exists and
none is faked: the derivation rides the listener binding alone
(`service/WatchStore.kt:409–424`), and the listener really captures —
posted notifications are inserted to Room
(`service/MonolithNotificationListener.kt:49, 77`).

**A-11 · The plate: "HOLD: 60 SECONDS, BANKED EVERY 15 · ONE BREAK A WEEK".**
The valve's bank interval is `totalMs / 4` = 15 s at the 60 s default,
banks lock at 15/30/45, and completion fires only at the full 60 held
(`ui/views/BlockitSuitcaseAndSwitchesViews.kt:412, 457`). The weekly
remainder is `(1 − spends)` over a Monday-anchored week
(`data/MonolithWeek.kt:30–41`), and a spend is booked only inside
`completeEmergencyBreak` (`data/MonolithRepository.kt:1900–1902, 1948`).

**A-12 · The plate: "PEEK: 2 MINUTES, THEN SHUT AGAIN".** The lease is
exactly `nowMono + 120_000` (`data/MonolithRepository.kt:1087–1088`),
and both enforcers cut access at its end
(`service/MonolithAccessibilityService.kt:209`;
`service/MonolithShieldService.kt:171`). The app row's middle stop
prints "2m" for the same tier (`ui/views/BlockitSuitcaseAndSwitchesViews.kt:296`).

**A-13 · The plate: "COLOUR IS A VERB: ORANGE = COMMIT · INK = SHUT ·
GREY = PEEK · PORCELAIN = OPEN".** The app-row sled's cap palette keeps
the three state verbs: SHUT wears the ink control fill, PEEK the grey
secondary container (by day), OPEN the bright porcelain surface
(`ui/views/BlockitSuitcaseAndSwitchesViews.kt:285–296`), and the accent
is fenced to the commit path (the accent token serves the wire/commit
fills, `ui/views/BlockitCoreViews.kt:141–152`; Round 1c's law in code).

**A-14 · The plate: "WHEEL SETS TIME · SLIDE ARMS · THE HOLD BREAKS".**
The falsifiable clause is the third, and it holds: the valve completes
only at the full hold (A-11's citations) and completion runs the break
— spend booked, session ended — through
`completeEmergencyBreak` (`data/MonolithRepository.kt:1948`). Wheel
and slide are the Deck's shipped arm mechanism, the cascade's home
(`ui/ArmProvingController.kt:587–592` runs the one seal transition).

**Non-claims, noted so they are not mistaken for findings:** the plate
head ("MONOLITH ML-01 · UNIT Nº 01 · BUILT FOR ONE USER · NOT FOR
SALE") is furniture — true of this build and falsifiable in no
particular. The ratchet banner prints no standing claim; it speaks
only when the ViewModel's ratchet path fires a refusal
(`ui/MonolithViewModel.kt:223–235`;
`ui/screens/ShieldMatrixScreen.kt:129–144`).

— Una, Critics (consistency), Oct 29, 2026.
