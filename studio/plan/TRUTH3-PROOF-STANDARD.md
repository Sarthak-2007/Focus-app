# TRUTH CAR 3 — THE PROOF STANDARD

**Seat:** Pruefer, Keepers of Record (proof desk). Filed Oct 29, 2026,
22:4x IST, before any of the car's six lanes has declared itself
written — the bar exists before the work it judges.
**Ground:** Una's two audits (plan/AUDIT-park-claims.md,
plan/AUDIT-deck-intercept-claims.md), Freya's machinery spec
(plan/PILL-AND-TIMES.md §§3–4), Tief's register (plan/SMALL-THINGS.md),
WORK-QUEUE item 20 + item 21(b)(c). Trunk = batch-8 state, DB v11
(audits were read against v10; line numbers may have shifted — this
standard binds behaviours, not lines).
**Twin seat:** Una re-verifies the words and machinery; this standard
is the test-side twin. A copy sits in mail/una/ — where Una and this
desk disagree on what proves a charge, the disagreement goes to Ren
in writing. It is never settled silently between us.

---

## THE STANDARD

A test proves a cure only if it **fails on the trunk the lane was
copied from and passes on the cured lane**. Both runs are mine, not
the builder's: the lane's suite in the lane's tree, and the lane's
new tests transplanted into the pristine proofbase
(builds/truth-3-proofbase/, trunk as copied 22:42 IST, DB v11) where
they must fail. A test that passes on trunk proves nothing and is
reported as such, by name.

Two discrimination classes, marked per charge below:

- **CLASS B — behavioural.** The behaviour exists on trunk and is
  wrong there. The transplanted test must fail on an assertion
  against trunk behaviour. Nothing weaker countersigns the charge.
- **CLASS A — absence.** The cure is a surface, verb, or store that
  does not exist on trunk at all; the transplant fails because the
  API is absent. Accepted — the charge itself is an absence — but
  only when the lane-side test asserts END-STATE FACTS (stored
  values, enforcer behaviour, printed words), never mere existence
  of the new code path.

**Guards** are labelled GUARD: they pass on trunk and must keep
passing. A guard is regression cover for the audits' acquittals; it
is never counted as proof of a cure. If a lane's cure breaks a
guard — or any acquittal in A-01…A-14 (PARK) / A-01…A-12 (Deck) —
I report the lane as failed on that ground even if its own charge
tests are green. The lane's full existing suite runs with its new
tests; a red pre-existing test is a failure of the lane, reported
exactly.

**Cross-car charges do not close on my countersign alone:**
UNA-05 (words half here; the machinery half is item 18's, Wache's
lane), UNA2-06 (in-app half here; the overlay half is the pill
lane's, proven only on the phone bench per Freya §4.5), and FT-5
(Freya §4.4 test 5 — the pill derivation table — belongs to the pill
lane, not this car; it is listed so nobody double-claims it). The
proof log records these as HALF-PROVEN until the other half speaks.

**The joint migration (Freya §4.1, renumbered 11→12):** the times,
shorts, and sound lanes each carry migration code (Rurik owns the
spine; Falk's shorts column and Imke's sound column ride local
11→12s; Konto maps, Ren composes the single shipped migration).
Per-lane I verify each lane's chain in its own tree. At merge I
re-run the chain test against Ren's composed migration — that
countersign is separate, and no lane's in-lane chain pass substitutes
for it.

**What I run, per lane, when it declares itself written:** (1) the
lane's full unit suite in its tree (JDK21, its env21.sh), (2) the
lane's new tests transplanted into proofbase, same suite filter,
(3) the grep companions named below. Every run lands in
plan/TRUTH3-PROOF-LOG.md — lane, command, exit, counts, and the
per-charge verdict: PROVEN (class noted) / UNPROVEN / GUARD-ONLY /
HALF-PROVEN / FAILED. Ren merges only countersigned lanes.

Lane map: truth-3-times (Rurik) · truth-3-shorts (Falk) ·
truth-3-sound (Imke) · truth-3-sweep (Bodo) · truth-3-intercept
(Suki) · truth-3-breaks (Tamm).

---

## LANE truth-3-shorts — UNA-01 (S1) SHORTS + REELS IS A PHANTOM

Trunk behaviour: the toggle flips an in-memory StateFlow
(VM:1287–1289, declared :165), persists nowhere, renders nowhere,
and the accessibility sniper fires with no enabled check at all
(MonolithAccessibilityService:164–177); GuardSettingsEntity has no
shorts field (MonolithModels:670–688).

Required tests:
1. **ShortsSettingPersistsAcrossProcessDeath** — CLASS A→B hybrid:
   write shorts OFF through the car's write path, recreate store +
   ViewModel from Room only, read back OFF. On trunk there is no
   field to read; at merge this same assertion runs behaviourally
   against the pre-car read (default true). Lane-side, the test
   must assert the READ-BACK VALUE from a fresh instance, not the
   flow it just wrote.
2. **SniperHonoursShortsSetting** — CLASS B: with a session active
   and the setting OFF, a YouTube/Instagram short-feed event
   produces NO sniper intercept; with ON, it does. Trunk fires
   regardless — the OFF half fails on trunk behaviourally.
3. **ShortsToggleRenderedAndTrue** — CLASS A: a rendered toggle
   exists on the surface PARK's board derives from, and its
   printed state equals the stored value in both positions.
   End-state assertion: toggling the rendered control changes the
   stored row, not a flow.

## LANE truth-3-sound — UNA-03 (S1) + UNA-02 (S2)

UNA-03 trunk behaviour: toggleSound (VM:1254–1257) flips a var on
one engine instance; no Room/prefs write exists; the seal
transition is a first-caller-wins singleton
(SessionSealTransition:288–303) and the routine heartbeat calls
get() with no engine (RoutineHeartbeat:142) — a routine-born
process binds a sound-ON engine while PARK prints OFF.

Required tests:
1. **SoundSettingPersistsAcrossProcessDeath** — CLASS A/B hybrid,
   same discipline as shorts: set OFF, fresh engine + fresh VM,
   the derived print state is OFF and the engine's enabled flag
   is false. Trunk defaults true → fails.
2. **SoundEngineBirthOrder** — CLASS B: in a fresh process state,
   bind the transition via the heartbeat path FIRST (get() with
   no engine), then via the VM path — the one resulting engine
   honours the stored OFF (a seal attempt makes no sound call).
   Trunk binds the sound-on engine at first call → fails.
3. **EveryEngineGetReadsTheSetting** — CLASS B: every get() call
   site in the tree returns an engine whose enabled state equals
   the persisted flag. (Grep companion: enumerate get( call sites
   in SessionSealTransition's callers at landing.)

UNA-02 trunk behaviour: the social-sites flag is persisted and
honoured, its VM toggle writes Room — but no surface renders the
toggle (ShieldMatrixScreen:72,85 and TactileDeckScreen:113 accept
and ignore it); in the shipped UI the flag can only be forced ON
by a loadout.

Required tests:
1. **SocialToggleRenderedAndWritesThrough** — CLASS A: the toggle
   renders on a surface (PARK is the audit's named heir; the lane
   may argue another) and driving the RENDERED control (semantics,
   not a direct VM call) flips the stored Room value both ways.
2. **SocialBoardWordFollowsTheToggle** — CLASS B at the print:
   after toggling OFF via the surface, the board's derived word
   for the line (WatchStore:163,418) reads OFF. On trunk no
   surface path can produce this state → the driven-through-the-
   surface form fails on trunk.

## LANE truth-3-sweep — UNA-04, 05, 06, 07, 08, 09

**UNA-04 (S2) "DATA: ON THIS PHONE ONLY".** Trunk: allowBackup=
"true" (AndroidManifest:25), backup_rules.xml is the untouched
sample (everything backs up), data_extraction_rules cloud section
empty — the Room DB and prefs are Drive-backup-eligible.
Required: **BackupExcludesTheRecord** — CLASS B (config): parse
the manifest + both rules files from the lane tree; assert the
database and prefs are excluded from cloud backup AND device
transfer, or allowBackup="false". Trunk's own files fail the
assertion verbatim.

**UNA-05 (S2) "FIRED AS SET" — WORDS HALF ONLY.** Trunk: the
plate prints the absolute while the chain alarm is inexact
(setAndAllowWhileIdle, RoutineScheduler:44, no exact-alarm
surface in service/). Required: **RoutinePlateWordsAreTrue** —
CLASS B: the plate's routines line prints only what the shipped
machinery keeps — either the weakened true sentence, or the
absolute restored WITH the exact-alarm machinery present in the
same build (assert setExactAndAllowWhileIdle / USE_EXACT_ALARM
in the composed tree at merge). On trunk the absolute prints
over an inexact alarm → fails. HALF-PROVEN by construction: the
firing itself is item 18's proof (Wache), never mine to close.
Lane note: item 20's text does not name UNA-05's owner lane; the
sweep holds the plate's other lines, so the standard assigns the
words half here — if no lane claims it, it does not close, and
that goes to Ren from this desk.

**UNA-06 (S2) "0 SITES" re-arms the factory list.** Trunk:
userDomains.ifEmpty { defaultDomains }
(MonolithAccessibilityService:182–185) — empty the list and the
blocker enforces the 8 factory domains while Shield prints 0.
Required: **EmptyWebsiteListStaysEmpty** — CLASS B: empty the
stored list; the enforcer's computed domain set is EMPTY.
Trunk returns the eight → fails.
GUARD: **FirstRunStillSeedsRealRows** — a fresh install seeds
the defaults as real rows (Repository:170) exactly as today;
passes on trunk, must keep passing (A-09's kept behaviour).

**UNA-07 (S3) loadout cap rests on STUDY by remember.** Trunk:
selectedLoadoutIndex is a remember starting at 0
(ShieldMatrixScreen:103) while the applied loadout lives in the
VM (:296) and is even passed in (:74) unprinted.
Required: **LoadoutCapRestsOnTheAppliedLoadout** — CLASS B:
apply SLEEP, recreate the Shield's composition state, the cap's
rest position is SLEEP's index. Trunk rests on STUDY → fails.

**UNA-08 (S3) "STUDY RECORD: IN YPT".** Trunk prints it
(ParkDrawer:104–115) while the Drawer keeps an on-phone record.
Required: **PlateSaysStudyHours** — CLASS B (words): the plate
line reads STUDY HOURS (the audit's true sentence) and the old
string exists nowhere in the tree. Grep companion for the old
string at landing.

**UNA-09 (S3) "n ACTIVE" counts daysMask==0.** Trunk: header
counts routines.count { it.enabled } (ShieldMatrixScreen:
173–177); the scheduler skips daysMask==0 outright
(RoutineDerive.nextBoundary; MonolithModels:545–546).
Required: **ActiveCountEqualsSchedulersCount** — CLASS B: a set
of one enabled mask-0 + one enabled mask-nonzero routine yields
header count 1. Trunk counts 2 → fails.

## LANE truth-3-intercept — UNA2-01…07, SIL-2, SIL-3, APPENDIX

**UNA2-01 (S1) peek offered on three doors that cannot honour
it.** Trunk: canPeek derives from tier alone, null entity
defaults ON (VM:2091–2112); only the per-app path ever reads a
lease (AccessibilityService:209–213); the guard, site, and
sniper paths intercept unconditionally.
Required tests:
1. **NoPeekOfferOnGuardDoor** — CLASS B: trigger the airlock via
   the tamper/settings path → no peek offer exists. Trunk
   defaults it ON → fails.
2. **NoPeekOfferOnSiteDoor** — CLASS B: site-block path → no
   offer. Trunk offers from tier → fails.
3. **NoPeekOfferOnSniperDoor** — CLASS B: sniper path → no offer
   (the lease cannot be honoured there). Trunk offers → fails.
4. **PeekLeaseForUnlistedPackageArmsNothing** — CLASS B:
   startPeekLease against an unlisted package leaves NO lease
   state and NO cooldown armed. Trunk's write lands nowhere yet
   the grant is treated as made → fails.
GUARD: **PeekOfferOnPerAppDoorStands** — per-app PEEK-tier path,
cooldown clear → the offer prints and the lease the chip sells
is the lease both enforcers read (A-12). Passes on trunk; the
honest door must survive the cure.

**UNA2-02 (S2) the count's noun is the lie.** Trunk:
appsCellCount counts resolvedTier != OPEN — SHUT+PEEK together
(VM:451–454) — printed as "n APPS WILL SHUT" / "n APPS HELD"
(MonolithCustomComponents:408–431), both tenses, and frozen so
at the seal.
Required: **ImprintCountsWhatItNames** — CLASS B: with 3 SHUT +
2 PEEK rows, the tenant's printed text (idle AND sealed forms)
never claims 5 under SHUT/HELD — it prints the split, or counts
the SHUT tier alone with LOCKED+PEEK carried under its own
name, matching the cure direction the lane declares in its
letter. Trunk prints 5 with the strong verb → fails. If the
lane's chosen direction differs from both named directions,
that is a ruling for Ren, flagged from this desk — the test
then asserts the ruled direction.

**UNA2-03 (S2) the warrant cannot age.** Trunk: the Warrant
value carries no read instant (WatchStore:62–67); refresh is
event-driven only; the Deck's ticker never re-reads it — a
silently killed bind leaves "PROOF: LIVE BIND" standing over a
warrant of unbounded age. PARK's board derives staleness past
its threshold (WatchStore:142, 313–322).
Required: **DeckWarrantDegradesWithAge** — CLASS B: advance
time past PARK's staleness threshold with no new sweep; the
Deck's seam/tenant prints degrade (STALE or blank) as the
board's do — they no longer assert LIVE. Trunk asserts it
forever → fails.
Rider: **IdleEndsIsWarrantAware** — CLASS B: with BLOCK reading
OFF, the idle TIME cell does not print its unconditional
PASS-toned "ENDS HH:mm" (TactileDeckScreen:365–369). Trunk
prints it → fails.

**UNA2-04 (S2) frozen present tense.** Trunk: BLOCK + tenant
print the seal-time freeze while the seam + sentence print the
live warrant in the same frame (:459–485, :990) — kill the
bind mid-session and one frame reads ON / HELD / BIND NOT
HELD / BLOCKING IS OFF.
Required: **RunningStripNeverContradictsItself** — CLASS B:
seal, then drop the live warrant to OFF; the frame's prints
either follow the seam down or carry their tense on their
face ("AT SEAL") — the four unqualified prints never stand
together. Trunk stands them together → fails. (The freeze's
design intent is acquitted, A-06; only its tense is charged —
a cure that kills the freeze itself must argue against A-06
to Ren, not silently.)

**UNA2-05 (S3) the spent face's sentence.** Trunk prints
"Sessions end when their time does."
(AirlockAndDisarmOverlays:630–634) — false on its plain
reading twice over.
Required: **SpentFacePrintsOnlyTheSpentFact** — CLASS B
(words): the spent face names the spent break and the old
sentence exists nowhere in the tree. Grep companion at
landing.

**UNA2-06 (S3) the peek runs furniture-less — IN-APP HALF.**
Trunk: FloatingPeekPillState (VM:113–118) has zero render
sites; end2MinutePeekEarly (:2152–2161) is unreachable from
any surface.
Required: **PeekCountdownRendersInApp** — CLASS A: during a
live lease, a Monolith surface (airlock at grant and/or the
Deck) renders the remaining time DERIVED FROM THE STORED END
— the end-state assertion is that the printed remaining
tracks the stored lease end, not a second clock (Freya §1.2's
two-clocks rot is the thing being killed; a render fed by a
fresh ticker that can disagree with the stored end does not
prove this charge).
**PeekEarlyEndIsReachableAndReal** — CLASS A/B: a rendered
affordance invokes the early-end verb; after the press, both
enforcers treat the lease as ended at the press, not at the
original end. The reachability half is absence on trunk; the
enforcer half is behavioural.
HALF-PROVEN by construction: the overlay half is the pill
lane's and proves only on the phone bench (Freya §4.5). This
charge does not close on my countersign.

**UNA2-07 (S3) "ACTIVE INTENTION: " over a blank.** Trunk
prints the label verbatim over an empty intention
(AirlockAndDisarmOverlays:548–554).
Required: **IntentionLineAbsentWhenEmpty** — CLASS B: an
intention-less session's airlock renders NO intention line
(no label node). Trunk renders the label → fails.
GUARD: **IntentionLinePrintsWhenDeclared** — with a declared
intention the line prints it verbatim. Passes on trunk.

**SIL-2 — the peek's price is never printed** (Freya §4.3:
Car 3's words, this spec's values). Required:
**PeekPricePrintedFromTheSetting** — CLASS B: before a peek
is taken, the airlock names the price — the peek's length
read from peekMinutes (set it to 5 in the test; the print
must say 5, never a hardcoded 2) and the 20-minute cooldown.
Trunk names neither → fails. Grep companion: no surface
hardcodes the peek's 2 or the cooldown's 20 after landing
(Freya §4.3's one-source rule; the constants survive only as
defaults' ancestors).

**SIL-3 — a withheld peek gives no reason.** Required:
**WithheldPeekPrintsReasonAndRemaining** — CLASS B: during a
running cooldown, the airlock renders the withheld state WITH
the remaining cooldown from the existing derivation
(Repository:1073–1079) — not a silently absent chip. Trunk
renders nothing → fails.

**APPENDIX — kill-or-verify, each by name:**
- **APP-1 desk-clock banner.** Trunk: no task + no intention →
  the clock prints "Deep Focus Session"
  (AirlockAndDisarmOverlays:197). Required:
  **ClockBannerNeverFabricates** — CLASS B: in that state the
  banner prints nothing (or the true session name); the
  fabricated string exists nowhere. Grep companion.
- **APP-2 listener's invented body.** Trunk: an empty-text
  capture is stored with body "Suppressed notification
  during study session" (MonolithNotificationListener:82)
  and the digest prints it as the notification's words.
  Required: **CaptureBodyIsNeverInvented** — CLASS B: an
  empty-text capture round-trips with an empty (or honestly
  marked) body; the invented string exists nowhere in main
  sources. Grep companion.
- **APP-3 primer modal's dead sentences.** Trunk:
  ShieldPermissionPrimerModal prints "hardware-level app
  interception" (AirlockAndDisarmOverlays:436–447); its
  binding is removed, so it prints nowhere — dead print with
  a false sentence inside. Required:
  **PrimerFalseSentenceIsDead** — CLASS B (words): the
  sentence exists nowhere in the tree (killed), OR the modal
  is resurrected only with a fresh audit — which is Una's
  call, not the lane's; absent her audit, absence is the
  only passing state.
- **APP-4 onSimulateIntercept.** Trunk: the Deck's parameter
  is wired in MainActivity:475–481 to a hardcoded fake
  TikTok airlock, never invoked — one call from live.
  Required: **SimulateInterceptWiringRemoved** — CLASS B:
  no code path can construct the fake intercept; the
  parameter and its wiring are gone. Grep companion for
  onSimulateIntercept + the fake's badge string.

## LANE truth-3-times — FREYA'S EDITABLE TIMES (FT) + ST-04

Freya §4.4's enumerated tests, restated as discriminations.
Her numbering is kept so her spec and this standard can be
read side by side.

- **FT-1 MigrationChainTo12** (§4.4.1) — CLASS A: a v11
  database migrates clean to v12; peekMinutes = 2 and
  breakMinutes = 10 land on the settings row; pre-existing
  session rows carry breakSeconds = 600. Trunk has no v12 →
  fails. (Per-lane in its tree; composed-migration re-run at
  merge, per the joint-migration rule above.)
- **FT-2 SpanReadsTheStamp** (§4.4.2) — CLASS B: sessions
  stamped breakSeconds ∈ {0, 300, 3600} — span target, cycle
  length, and in-break elapsed all read the stamp; at 0 the
  span is the bare block sum. Trunk's span derives from the
  three literal 600s → fails at every non-default stamp.
  Grep companion (mine, at landing): the three literal 600s
  are gone from MonolithModels.kt.
- **FT-3 LeaseReadsTheSetting** (§4.4.3) — CLASS B:
  peekMinutes = 5 → a grant writes end = now + 300 000;
  peekMinutes = 0 → no grant, no offer, no row touched.
  Trunk grants the constant 120 000 regardless → fails.
- **FT-4 MidFlightImmovability** (§4.4.4) — CLASS A, and the
  standard says so plainly: grant at 2, edit to 30 mid-lease
  — the stored end does not move; create at break 10, edit
  to 60 mid-break — the pair, the stamp, and the span do not
  move; the NEXT session stamps 60. On trunk the settings do
  not exist, so this test's discrimination is by absence;
  its proof weight comes from combination with FT-2/FT-3,
  which fail behaviourally. A lane claiming FT-4 alone as
  its times proof is UNPROVEN.
- **FT-5** — the pill derivation table (§4.4.5): the pill
  lane's, cross-referenced, NOT this car's. No lane of this
  car may claim it.
- **FT-6 SetterBoundsClamp** (§4.4.6) — CLASS A: writes of
  −5 and of 90 clamp to the bounds on both numbers; the row
  never holds an out-of-range value. Absence discrimination,
  same caveat as FT-4.

**ST-04 — add time to a running block, WITH the honesty
condition** (register ruling: the receipt/Drawer entry PRINTS
the extension).
- **AddTimeGrowsTheTargetByHisHand** — CLASS B: mid-session,
  add N minutes → the effective target grows by exactly N
  through the new write, and the tamper-penalty ledger is
  untouched by it. Trunk's only growing writer is the
  penalty (Repository:1680) → the by-his-hand half fails.
- **TheExtensionIsPrinted** — CLASS B: the finished session's
  receipt/Drawer figures show the extension as an extension
  (planned-vs-lived carries the added minutes visibly, not
  silently absorbed). Trunk never prints one → fails.

## LANE truth-3-breaks — ST-02 + ST-03

**ST-02 — a break cannot be ended early.** Trunk: the safe
break ends only at expiry (Repository:1724–1759); no verb
exists.
- **EndBreakEarlyFlipsToNextBlock** — CLASS A: during a
  break with remaining > 0, the new verb (Deck chip → VM
  call) zeroes the remaining and the session flips into the
  next block, the accrual pair closed honestly. End-state
  assertions on the session's stored state, not on the
  chip's existence.
GUARD: **BreakStillEndsByTimer** — untouched, the break ends
at expiry as today. Passes on trunk.

**ST-03 — neither break boundary makes a sound.** Trunk:
both flips are silent state changes; the engine's voices are
the seal, the finish E-clock, and detents only.
- **BreakStartKnockFires** + **BreakEndKnockFires** — CLASS B:
  each flip emits exactly one knock from the existing
  vocabulary. Proof is at the call level — the voice call
  fires on the flip (engine fake/seam assertion in the VM
  ticker's path), never an acoustic claim. Trunk fires none
  → fails. If the lane finds no test seam into the engine,
  it builds the seam and says so; a test that asserts a
  boolean the lane invented for the purpose, unconnected to
  the flip, is GUARD-ONLY theatre and will be reported as
  UNPROVEN.

---

Filed by Pruefer, Keepers of Record (proof desk), Oct 29, 2026.
Runs begin the hour a lane declares itself written; the log is
plan/TRUTH3-PROOF-LOG.md. Disagreements with Una's seat go to
Ren in writing from whichever of us finds them.
