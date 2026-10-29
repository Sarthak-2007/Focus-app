# ISSUE #9 — PARK · WAVE-2 SEATS: FINISHERS ×5 + FEEL ×5

*Seat-runner under lead VOGT (cell lead: Hof). Branch: agent/issue-9.
Scope: AUDIT-park.md §4 remainders NOT landed in wave-1 — §4.2, §4.3,
§4.4, §4.6, §4.7, §4.8, §4.9, §4.11, §4.12. Wave-1 already landed
§4.1 (CHECK verb + re-sweep on open), §4.5 (GUARD — NOT CHECKED),
the P-7 plate strike verification, STALE half-ink (P-4's dress half),
and the keys' dip/tempo work; none of that is re-litigated here.
§4.10 (sound sled → Shield, theme row → footnote) is outside this
brief and is not assessed. Paper only — nothing compiled, no code
written. Prices use the audit's scale (S/M) with line estimates.*

*Standing flag, stated once here and owned by Finisher 4 below:
ParkDrawer.kt:321-322 still enshrines the STRUCK law — "no word
prints, ever" — as the FIX return's rule. Audit §2 P-5 overruled
exactly this (Muede over Dens: the click stays, a print is ADDED —
FIXED / STILL OPEN on the row). The code and its comment currently
implement and preach the losing side of a ruled argument.*

---

## Finisher 1 — Surface

**Verdict: FAIL on composition. The board is still a flat list on a
sheet that cannot state its own verdict (§4.2, §4.7, §4.8 open).**

- §4.7 tiers: the six permission rows render as one flat
  `forEach` with no eyebrows, no rank, no consequence line.
  Evidence: ParkDrawer.kt:468 (`WatchItemId.PERMISSIONS.forEach`),
  ParkDrawer.kt:490-492 — the inter-family break is still the
  comment-confessed "16dp, no hairline, no eyebrow". OVERLAY and
  BATTERY remain inside the same block as the counted four
  (WatchStore.kt PERMISSIONS list, board order), the false-citizen
  fault audit §1.6 named. Interceptor words are still only
  RUNNING / PAUSED / OFF (ParkDrawer.kt:495-500) — the OFF vs
  NO DOOR split (setting-off vs parent-missing) does not exist.
- §4.2 warrant line: there is nowhere on the surface for it to
  live, and no data reaches the surface — ParkDrawer's signature
  takes `snapshot` only, no Warrant (ParkDrawer.kt:134-152). The
  Deck already prints from the same machinery
  (TactileDeckScreen.kt:371, `WatchStore.printState`); PARK,
  the drawer's whose job is state, receives none of it.
- §4.8 colophon: LAST PROVED still sits at the HEAD of the sheet
  (ParkDrawer.kt:422-462), directly under the title — the exact
  prominence audit §1.3 demoted. The foot (ParkDrawer.kt:891-912)
  holds only the plate; there is no colophon beside it.

**Price:** tiers M (~90-120 lines: two eyebrow rows, consequence
line under Tier 1, OFF/NO DOOR word mapping in the interceptor
derivation print, OVERLAY/BATTERY moved below the tier block);
warrant line S once the host passes a Warrant in (~25 lines in
ParkDrawer + 1 host argument); colophon move S (~15 lines, pure
relocation of an existing block). Surface total: 1 M + 2 S.

— Finisher 1 (Surface), Wave-2, under Vogt

---

## Finisher 2 — Motion

**Verdict: FAIL on §4.6's motion half. FIX is still a one-door
teleport with no escort and no return journey; the staircase's
morph has no motion spec in code because the staircase does not
exist.**

- Evidence: ParkDrawer.kt:183-187 — `firstMissing` computes
  exactly ONE lack; ParkDrawer.kt:631 — the key can only ever
  print `FIX — ${item.boardName}`. There is no "FIX 2 OF 3" state,
  no key morph between lacks, no escort card enter/exit anywhere
  in the file (grep: no escort, no staircase, no FIXED/STILL OPEN
  strings in ParkDrawer.kt or WatchStore.kt).
- What wave-1 got right and the staircase must inherit: FIX entry
  is already the audit's 280 SEAT (ParkDrawer.kt:560-568,
  DRAWER_OPEN_MS enter / DRAWER_CLOSE_MS exit), and the dip
  animator pair (90 SEAT in / 160 THROW back) is one shared spec
  for both keys. The staircase morph should ride the same
  grammar: on return-with-next-lack, the key's label crossfades
  while the dip releases — no new tempo family.
- The return effect (ParkDrawer.kt:309-329) is a single 800ms
  delay → verdict read → dip release. A staircase needs that
  effect to become a state machine (return → verdict print →
  next-lack morph → idle), not a longer delay chain.

**Price:** §4.6 motion half M (audit priced the whole §4.6 cure
M, ~80 lines for staircase logic; motion adds ~30 lines inside
that: label morph on the existing Animatable/dip spec, escort
card on the existing expand/fade pair). No new animation
constants; KIN-DRAWER tempos are sufficient.

— Finisher 2 (Motion), Wave-2, under Vogt

---

## Finisher 3 — Light

**Verdict: PARTIAL. Wave-1 landed the STALE half-ink dress; the
rest of the light story — age, warrant, count — is unprinted, so
there is nothing for light to grade (§4.4, §4.2, §4.3 open).**

- Landed, credited: STALE takes HALF ink (0.60) —
  ParkDrawer.kt:482-487 (derivation) and ParkDrawer.kt:949-953
  (ParkWordInk.HALF in ParkBoardRow). The three-tier enum
  ParkWordInk (ParkDrawer.kt:922) is the right chassis.
- §4.4 age column: ParkBoardRow's signature is
  `(name, word, wordInk)` (ParkDrawer.kt:927) — there is no age
  slot, at any ink. The data is already in the store:
  WatchMeasurement.measuredMono / measuredWall
  (WatchStore.kt:120-121) and the 45s law
  (WatchStore.kt:389, STALE_AFTER_MS). Age should print QUIET
  ink always — it is context, never an accusation; only its
  crossing into STALE changes the word's dress, which already
  happens.
- §4.2 warrant word needs a dress ruling, proposed here: LIVE =
  QUIET (proved, at rest — same law as GRANTED), UNPROVEN = HALF
  (granted-but-unproven is STALE's exact cousin), OFF = FULL
  (a measured lack). This reuses ParkWordInk with zero new
  colours and keeps the one-object/one-ink-family law.
- §4.3 headline count: `GUARD {n}/4` must print in headline ink,
  not badge ink — the count is the drawer's thesis, and today
  the headline is a bare word (ParkDrawer.kt:425-429).

**Price:** age column S (~20 lines: one param, one derivation
from measuredMono, one Text in Anonymous Pro — see Finisher 5);
warrant dress S, inside Finisher 1's §4.2 price; headline ink
nil, inside §4.3's ~30 lines.

— Finisher 3 (Light), Wave-2, under Vogt

---

## Finisher 4 — Sound / Haptics

**Verdict: FAIL — and this seat files the brief's standing flag
as a formal charge. The FIX return's sound-only verdict is the
struck law, still implemented, still commented as eternal.**

- Evidence, verbatim: ParkDrawer.kt:320-324 —
  `if (repaired) { // The repaired line alone gets the firm
  click; the hand goes back in silence — no word prints, ever.
  soundEngine?.playFirmClick() }`. On the unrepaired path there
  is not even a click: total silence. Fixed and still-broken
  return acoustically near-identical and visually identical.
- The overrule, verbatim: AUDIT-park.md §2 P-5 — "Dens acquitted
  the silent verdict; MUEDE OVERRULES HIM for the drawer's real
  hour — the click stays (the acquittal holds for sound), a
  print is ADDED: FIXED / STILL OPEN on the row." The sound half
  of that ruling is implemented; the print half is not, and the
  comment at :321-322 instructs the next builder to keep it that
  way. The comment must be rewritten with the code, or the
  struck law will be re-inherited.
- The house already has the honest pattern in this same file:
  CHECK's failed landing PRINTS its verdict (the NOT PROVED row,
  ParkDrawer.kt:725-732) while its whole landing clicks. FIX is
  now the only verb on the surface whose verdict is sound-only —
  an inconsistency a tired user cannot model.
- Sound spec for the cure (no new sounds): FIXED → the existing
  firm click + the printed word; STILL OPEN → silence (current
  behaviour) + the printed word in FULL ink. The staircase's
  intermediate steps get the seat knock already used on press
  (ParkDrawer.kt:591, playSeatKnock floor) — one knock per
  door landed, firm click only on the final whole set.

**Price:** printed FIXED / STILL OPEN verdict S (~25 lines: one
state held across the return effect at :309, one ParkBoardRow-
grammar line under the key, comment at :321-322 rewritten to
cite P-5). Staircase step-knocks nil, inside §4.6's M.

— Finisher 4 (Sound/Haptics), Wave-2, under Vogt

---

## Finisher 5 — Typography / Print

**Verdict: FAIL on §4.3 and §4.9. The drawer does not print its
count, does not sign its work, and its plate still prints eight
lines where four were ruled.**

- §4.3 headline: ParkDrawer.kt:425-429 prints the literal
  `"GUARD"` — no count. The count machinery exists twice over
  and PARK uses neither: WatchStore.kt:398-399
  (`countedWarranted`) and GuardLine.kt:38-50 (the header band's
  own derivation, which PARK's headline should mirror, including
  its NOT CHECKED form when any counted item is UNMEASURED —
  a headline that prints `GUARD 0/4` on a cold open would
  fabricate the count P-6 forbids).
- §4.3 foot signed line: absent. The foot is plate-only
  (ParkDrawer.kt:891-912). Audit's form (P-3, Linea):
  `GUARD ···· {n}/4 · {HH:mm}` above the plate — the board
  signing its work like the receipt signs its. Print it in the
  plate's own register (Anonymous Pro, 10sp, secondary ink) so
  it reads as colophon, not as a second headline; it pairs with
  §4.8's LAST PROVED colophon at the same foot.
- §4.9 plate cut: PLATE_HEAD is 2 lines (ParkDrawer.kt:112-115)
  and PLATE_BODY is 6 lines (ParkDrawer.kt:116-131) — 8 printed
  lines against the ruled 4 (two head lines + HOLD + PEEK).
  Keep :113, :114, :118 (HOLD), :119 (PEEK). Cut :117 (WHEEL),
  :120 (COLOUR — independently condemned, audit §1.11),
  :127 (ROUTINES), :128 (SITES/DATA), :129 (STUDY HOURS),
  :130 (SOUND). Note the surviving PEEK line prints a hard-coded
  "2 MINUTES" (:119) while the lease reads a constant — the
  issue-9 plan's W-08 input contract already covers making it
  honest in batch 10; the cut must not re hard-code it further.
- §4.4 age typography: ages print in Anonymous Pro (the
  telemetry font), right-aligned before the word, format `3m` /
  `12:04` (age under an hour as elapsed, older as wall time) —
  never a second sentence on the row.

**Price:** §4.3 total S, ~30 lines (audit's own price, headline
+ foot line together); §4.9 S, negative lines (~-10 net: six
strings deleted, none added); age typography inside Finisher
3's S.

— Finisher 5 (Typography/Print), Wave-2, under Vogt

---

## Feel 1 — Weight

**Verdict: FAIL on §4.11. The sheet's bottom still carries 100dp
of void, and the spacing outside the key zone is still off-module
— the drawer feels bottom-heavy and hand-spaced.**

- Evidence: ParkDrawer.kt:420 —
  `.padding(PaddingValues(start = 20.dp, end = 20.dp, bottom =
  100.dp))`. Audit §1.12: "Bottom padding 100dp — CUT to 24dp.
  Pure void." At the foot, after the plate, 100dp of nothing is
  the last physical sensation of the drawer: a long scroll into
  emptiness that reads as "more below" when there is none.
- Spacing census of the file (grep): heights 6, 14, 32 and
  widths 10, 12 all occur alongside the module values — audit
  §1.13's module is {8, 16, 24} only. Named offenders:
  ParkDrawer.kt:430 (6dp under the headline), :464 (14dp above
  the board), :439 (10dp inside LAST PROVED), :892 (32dp above
  the plate), :894 (12dp under its hairline). Wave-1 put only
  the KEY ZONE on the module (ParkDrawer.kt:650-657); the rest
  of the sheet still breathes at 6/14/16/20/32/12.
- Interaction with §4.8/§4.9: moving LAST PROVED to the foot and
  cutting the plate to 4 lines shortens the foot; the 100→24 cut
  and the {8,16,24} module must land in the same pass or the
  foot will be re-spaced twice.

**Price:** §4.11 total S (~15 lines changed, all constants:
100→24, 6→8, 14→16, 32→24, 12→8 or 16 per joint, 10→8). Zero
new values; the module already exists in the key zone.

— Feel 1 (Weight), Wave-2, under Vogt

---

## Feel 2 — Light (perceived)

**Verdict: FAIL on emphasis order. At 1 AM the first bright thing
the eye lands on is a six-permission timestamp (LAST PROVED),
not the answer to "am I protected?" — because the warrant word
that would answer it is not printed at all.**

- The head of the sheet (ParkDrawer.kt:425-462) spends its
  brightest print (Anonymous Pro Bold 15sp, full onBackground,
  :449-460) on LAST PROVED's instant — a complete-set historical
  fact — while the board below answers in 15sp words that never
  compose into LIVE / OFF / UNPROVEN. Perceived hierarchy is
  inverted against the audit's §0 job sentence: the drawer tells
  the truth about its state, but whispers the verdict and
  spotlights the footnote.
- §4.8's move is therefore not tidiness, it is the light cure:
  LAST PROVED at the foot, in the plate's dimmer register,
  beside §4.3's signed line; the warrant line takes the head's
  visual weight instead (dress per Finisher 3: QUIET / HALF /
  FULL).
- Night register check: the FIX/CHECK seat knock already drops
  amplitude at night (0.06 vs 0.1) — the surface knows it is
  read at 1 AM everywhere except its emphasis order.

**Price:** nil beyond §4.8 (S, Finisher 1) and §4.2 (S) — this
seat's charge is that those two S items are the light fix, and
should not be deprioritised as "mere moves".

— Feel 2 (Light), Wave-2, under Vogt

---

## Feel 3 — Timing

**Verdict: FAIL on §4.4 and §4.12's timing halves. Nothing on the
board says HOW OLD a reading is, and the one moment a warning
would cost nothing — the seal — passes in silence.**

- §4.4: staleness is a cliff the reader cannot see coming. A row
  reads GRANTED until the instant WatchStore.kt:389's 45s
  threshold flips it to STALE — in front of him, while PARK
  re-derives on its 5s beat. Without an age column, GRANTED at
  3 seconds old and GRANTED at 44 seconds old feel identical,
  and the flip feels arbitrary — like the board changed its
  mind. The age print converts a cliff into a slope he can
  watch. Data is already stamped per item
  (WatchStore.kt:120-121); this is print, not machinery.
- §4.6 timing: the FIX return already waits honestly
  (ParkDrawer.kt:309-315: 800ms = ~300ms sweep landing + 500ms
  stability beat, verdict read LIVE off the store). When the
  FIXED / STILL OPEN print lands (Finisher 4), it must inherit
  this timing exactly — a verdict printed at press-time or at
  a guessed delay would be a timed lie. No change needed; the
  seat records the timing as correct so the builder keeps it.
- §4.12 seal-time warning: a door that dies after the 20:00
  SLEEP seal cannot be viewed until morning (audit P-8), and
  nothing prints the ungranted doors AT seal time — grep over
  ParkDrawer.kt and the MainActivity seal path finds no such
  line. The warning's whole value is its timing: at the seal
  it is one glance; in the morning it is archaeology.

**Price:** age column S (Finisher 3's price, shared); seal-time
warning S (~20 lines at the seal transition: read
countedWarranted / first lacks from the snapshot the seal
already freezes, print any ungranted counted door on the seal
surface). FIX verdict timing: nil, already correct.

— Feel 3 (Timing), Wave-2, under Vogt

---

## Feel 4 — Touch + Sound

**Verdict: FAIL on §4.6's touch path. FIX today is a leap of
faith performed blind: one tap, an OS border crossing with no
escort, and a return whose outcome the hand cannot feel and
the eye cannot read.**

- The touch facts: the key commits on RELEASE (correct — a
  scroll starting on the key launches nothing), the dip holds
  for the whole absence, and the return releases the dip in
  both outcomes (ParkDrawer.kt:325-328). So the hand feels
  press → absence → release, identically, whether the trip
  fixed the door or not. Combined with Finisher 4's finding
  (no print, click only on success), a failed trip feels like
  a successful one that merely didn't click — at 1 AM, he
  takes the trip twice (audit P-5's exact words).
- The escort's touch half: FIX lands him on the general
  Accessibility list with "find MONOLITH" said nowhere
  (audit P-5). An escort card — printed BEFORE the crossing,
  naming the destination and the exact row to find — costs
  one extra glance and removes the hunt on the far side. It
  must be print, not a second key: the one-object law holds;
  the card appears with the key's press state and dismisses
  on return.
- The staircase's touch half: one trip, all lacks,
  dependency-ordered (audit P-5 / Vega). Each Settings return
  re-seats the SAME key, morphed to the next lack — the thumb
  never re-aims, never re-reads the board to find what changed.
  That sameness of target across trips is the feel of the cure.
- §4.12 touch consequence: while detained by the finish
  machine (doorLocked but not session-sealed), PARK currently
  opens INTERACTIVE — see Feel 5's / MUST list's call-site
  evidence (MainActivity.kt:663). FIX and CHECK are touchable
  in a state the house elsewhere treats as locked.

**Price:** escort card S inside §4.6's M (~20 lines: one print
block keyed to fixPressedItem, no new control); staircase
touch continuity nil beyond §4.6's M (it is a property of the
morph, Finisher 2).

— Feel 4 (Touch+Sound), Wave-2, under Vogt

---

## Feel 5 — Instant-Read

**Verdict: FAIL. The three questions the drawer exists to answer
in one glance — Is it on? How many doors stand? How old is this
reading? — currently take a full board scan, outside knowledge,
and guesswork respectively.**

- "Is it on?" — unanswerable on this surface. The warrant word
  (LIVE / OFF / UNPROVEN) exists fully derived in the store
  (WatchStore.kt:52, :326-338, print-degraded honestly by
  :371-379) and is printed on the Deck — but PARK, opened
  precisely to ask this, never receives it (ParkDrawer.kt:134
  signature; no Warrant parameter, no printState call in file).
  §4.2 is the instant-read cure.
- "How many?" — the header band answers it (GuardLine.kt:50,
  `GUARD $warranted/4 — $name`), the reception answers it, and
  PARK — the board itself — does not (ParkDrawer.kt:426, bare
  `"GUARD"`; no foot line). §4.3 is the instant-read cure, and
  its foot half matters most at a glance: the eye that lands at
  the sheet's end after scrolling should meet the signed count,
  not furniture alone.
- "How old?" — unanswerable per row (§4.4, Feel 3). Instant-read
  form matters: age must sit in the SAME right-hand column
  family as the word, scannable in one vertical pass — not
  under the name, not as a second line. Finisher 5's format
  (`3m` / wall time) keeps the column to ≤5 characters.
- Tiers (§4.7) are an instant-read feature, not decoration:
  eyebrows + order let the eye stop at Tier 1 — if the warrant's
  two conjuncts stand, the rest is detail. Today all nine rows
  demand equal reading time.
- §4.12 gate: MainActivity.kt:255 composes
  `doorLocked = sealed || navigationLocked()`, the header
  receives it (:440) and openPark guards on it (:300) — but
  the ParkDrawer call site passes raw sealed
  (MainActivity.kt:663, `isSealed = sealed`), so every gate
  inside the drawer (ParkDrawer.kt:170 interactive, :561 FIX,
  :650 CHECK) drinks the un-unified predicate. Audit P-8's
  "fixed everywhere except inside PARK" is still literally
  true, at exactly one argument.

**Price:** §4.2 S + §4.3 S (~30) + §4.4 S (all priced above);
§4.12 gate fix XS — one argument at MainActivity.kt:663
(`isSealed = doorLocked`) plus renaming ParkDrawer's parameter
so the next builder cannot re-plumb it wrong (~5 lines).

— Feel 5 (Instant-Read), Wave-2, under Vogt

---

## MUST fixes (in audit §4 order; all already ruled in AUDIT-park)

1. **§4.6 print half — FIXED / STILL OPEN verdict (P-5).**
   ParkDrawer.kt:309-329: hold the verdict across the return
   effect, print it on the row in FULL ink (STILL OPEN) /
   click + print (FIXED), and REWRITE the comment at :321-322
   ("no word prints, ever") to cite the P-5 overrule. The
   comment is part of the bug: it teaches the struck law.
   S (~25 lines).
2. **§4.12 gate — doorLocked into PARK.** MainActivity.kt:663:
   pass `doorLocked`, not raw `sealed`; rename ParkDrawer's
   `isSealed` parameter to match. XS (~5 lines).
3. **§4.2 warrant line.** Host passes a Warrant to ParkDrawer;
   board prints LIVE / OFF / UNPROVEN via
   WatchStore.printState (WatchStore.kt:371), dressed QUIET /
   FULL / HALF per Finisher 3. S.
4. **§4.3 headline + foot line.** Headline `GUARD {n}/4` via
   WatchStore.countedWarranted (WatchStore.kt:398), with the
   NOT CHECKED form when unmeasured (never a fabricated 0/4);
   foot line `GUARD ···· {n}/4 · {HH:mm}` above the plate.
   S (~30 lines, audit's price).
5. **§4.4 age column.** Every board row prints its measurement
   age from measuredMono (WatchStore.kt:120), Anonymous Pro,
   QUIET ink, Finisher 5's format. S (~20 lines).
6. **§4.6 escort + staircase.** Escort card per door (print,
   before the crossing, naming the Settings destination and
   the row to find); staircase walks all lacks in one trip,
   key morphing `FIX 2 OF 3`, one-at-a-time NAMING preserved
   (audit §7 acquittal). M (~80 lines + morph/escort ~50).
7. **§4.7 tiers.** Tier eyebrows (1: ACCESSIBILITY + USAGE
   ACCESS; 2: DEVICE ADMIN; 3: NOTIFICATION LISTENER),
   consequence line, OFF vs NO DOOR split for interceptors,
   OVERLAY + BATTERY out of the counted family's block,
   14/16/20 rhythm, no row shrunk. M.
8. **§4.8 LAST PROVED → foot colophon.** Relocate
   ParkDrawer.kt:431-462 beside the plate, plate register;
   "· SELF-REPORT" qualifier stays (audit §7 acquittal). S.
9. **§4.9 plate cut to 4 lines.** Keep PLATE_HEAD ×2 + HOLD +
   PEEK; delete WHEEL, COLOUR, ROUTINES, SITES/DATA, STUDY
   HOURS, SOUND lines (ParkDrawer.kt:116-131). S, net-negative.
10. **§4.11 padding + module.** Bottom padding 100→24
    (ParkDrawer.kt:420); all joints onto {8, 16, 24}
    (:430 6→8, :464 14→16, :892 32→24, :894/:439 12/10→8).
    Land with #8/#9 in one pass. S.
11. **§4.12 seal-time warning.** At the SLEEP seal, print any
    ungranted counted door (from the snapshot the seal already
    freezes) on the seal surface. No current code exists. S.

## Remainders (noted, not in this brief's MUST list)

- §4.10 (Sound sled → Shield; Theme row → footnote / future
  LOOK row): outside this seat-runner's brief; still open per
  audit §4, owned by whichever pass takes it.
- §5 escalations (Live-Fire Strip, Night Porter full form,
  Tomorrow Dry-Run, Death Certificate, Diff print): parked /
  filed per audit §5 and the issue-9 plan (Live-Fire Strip =
  issue #21); none designed or priced here.
- Critic C-6/C-7/C-8 (interceptor RUNNING staleness, presence-
  proved BIND doors, residency stamp scope): stand per the
  issue-9 plan, routed to Ren / the warrant car's territory —
  they touch §4.2's machinery, not its print; the warrant line
  in MUST #3 must print printState's degraded word, never the
  raw derivation, until C-6 is cured.
- W-08 dynamic plate (batch 10): inputs prepared in the
  issue-9 plan; MUST #9's cut must not hard-code PEEK further.

## DECISION-FOR-SARTHAK items

*Everything above is already ruled in AUDIT-park.md and needs
no decision — it needs a builder. Only these two touch his
experience directly and were never put to him:*

1. **Seal-time warning (§4.12, MUST #11) — silent print or
   felt?** At the 20:00 SLEEP seal, an ungranted door can print
   silently on the seal surface, or also knock/vibrate. He is
   winding down at that hour by design; whether the warning may
   interrupt is his call, not a designer's.
2. **Age column (§4.4, MUST #5) — elapsed or clock?** Rows can
   print age as elapsed (`3m ago`, telemetry register) or as
   the wall time it was measured (`02:47`). Elapsed reads
   faster at a glance; wall time matches LAST PROVED and the
   foot line. One form only — his reading habit decides.

— Seat-runner (Finishers 1-5, Feel 1-5), Wave-2, under Vogt,
  for lead Hof / issue #9. Paper only; no code, no compile.
