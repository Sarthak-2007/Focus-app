# WAVE-2 NAMED SEATS — ISSUE #7 (SHIELD)

Seat-runner under lead VOGT, issue #7 cell, branch `agent/issue-7`.
Sources read in full: `studio/plan/AUDIT-shield.md` (Vogt's coordinator
verdict, R-1…R-13), `studio/cells/issue-7/lead-vogt.md` (what actually
landed, DECLARED/uncompiled), current `ShieldMatrixScreen.kt` and
`ShieldBlockingStatus.kt` — every charge below was re-grepped against
the current tree, not copied from the audit. One signed section per
named type. A charge files with evidence or not at all; an acquittal
files with evidence too. Nobody compiled; nothing here is code.

**Headline law, held throughout:** Shield must say truthfully
whether blocking is live *right now*, witness-proved — never from a
session row's flag. State of that law today: **substantially met on
paper, not yet proven on a device, and one witness short** (see
Brutal Critic §3 and Max-Impl §2). The landed derivation
(`computeShieldBlockingStatus`) requires both cascade witnesses for
this session id — shield `protecting` for the session + accessibility
armed for the session — plus beat freshness under the repository's
10s gap law, and prints `NOT BLOCKING — … ENFORCEMENT NOT PROVEN`
for anything less. That is the correct shape. The 17 truth-table
tests in `ShieldBlockingStatusTest.kt` have never run.

---

## 1. Crazy-ideas — signed: CRAZY-IDEAS seat, Wave-2

**NEW MECHANISM — "THE TURNED-AWAY TICKER" (a living receipt, not a
status claim).** The status sentence proves the wall *should* be
standing; nothing on this screen proves it *did anything lately*. A
blocker that has never turned anything away and a blocker that
turned away Instagram 40 seconds ago print the identical line
today. Proposal: under the status sentence, one Anonymous Pro line
fed by the accessibility service's intercept log —
`LAST TURNED AWAY — INSTAGRAM · 00:41 AGO` — ticking up in real
time, resetting on each intercept, and printing
`NOTHING TURNED AWAY YET THIS SESSION` in the first minutes.
Spec: (a) the a11y service already fires the intercept; persist
`lastInterceptPackage` + `lastInterceptWallMillis` on the session
row at intercept time (one write per intercept, no loop cost);
(b) Shield reads it through the same 1s ticker that already feeds
`shieldBlockingStatus` — no new polling; (c) the line is a *fact
about the past*, so it can never overclaim the present: if the
ticker's age exceeds the session, the line ages honestly instead of
freezing; (d) during SAFE BREAK it prints the break's own truth
(`SAFE BREAK — NOTHING TURNED AWAY, BY YOUR RULE`). **Price:
~60 lines (one column write in the service, one derivation branch,
one Text) + 3 truth-table tests. No schema change if the two fields
ride the existing session row's spare columns — if they need a DB
bump, price rises by one migration + test, Ren's call.** Why it
matters: it converts "trust the derivation" into "watch it work" —
the single most persuasive object a discipline app can show at 1 AM
is evidence of its own last act.

— Crazy-ideas

## 2. Maximum-implementation-potential — signed: MAX-IMPL seat, Wave-2

**Finding.** The proof is a duet where the enforcement is a trio.
Evidence: SALZ C-2, recorded in `lead-vogt.md` as a named residual —
the shield loop's polling door `queryForegroundBlockedApp` returns
null without usage access (`MonolithRepository.kt`: `if
(!hasUsageStatsPermission() || blockedPackages.isEmpty()) return
null`), so with usage access revoked mid-session the derivation can
still print BLOCKING NOW on two witnesses while one enforcement
path is blind. Vogt's ruling names a third conjunct "a named
follow-up for Ren's merge call" — it is still not in
`computeShieldBlockingStatus` (parameter list verified: session,
shield×3, accessibility×2, now — no usage term).

**NEW MECHANISM — "WITNESS TRIAD + ON-DEMAND CANARY" (close the
proof, then let him strike the wall himself).** Spec: (1) add
`usageAccessGranted: Boolean` as a third conjunct in
`computeShieldBlockingStatus`; its absence degrades LIVE →
UNPROVEN with `missingProof = "USAGE ACCESS"`, headline unchanged
in shape. (~15 lines + 4 truth-table rows.) (2) The canary: a
`TEST THE WALL` chip in the status slot's long-press / a small text
button under the sentence that launches a first-party probe
activity whose package sits in a reserved always-SHUT slot; the
a11y service must intercept it and return the user to Shield with
the measured intercept latency printed —
`WALL TESTED — TURNED AWAY IN 212MS · JUST NOW`. A failed canary
prints `WALL TEST FAILED — PROBE REACHED THE SCREEN` in the alert
tone and is the one Shield event allowed to be loud. This is the
maximum form of the headline law: not derived truth, *demonstrated*
truth, on demand. **Price: triad ~15 lines + tests (~½h paper);
canary ~180 lines (probe activity + manifest entry + reserved slot
+ result plumbing through the existing ticker) + device
verification that only Ren's compile + the phone can give. No
schema change.** Sequencing: triad in the batch-10 merge; canary as
its own named build immediately after — do not let the canary's
size delay the triad's truth fix.

— Maximum-implementation-potential

## 3. Brutal critic — signed: BRUTAL CRITIC seat, Wave-2

Scope unrestricted; charges with evidence only.

1. **The screen still lies by costume in its most destructive
   control.** The LOADOUT cap still commits `applySmartLoadout` on
   a single tap (`ShieldMatrixScreen.kt`, cap `onSelect` → STUDY /
   WORK_SPRINT / SLEEP with no preview, no confirm, no undo), and
   the repository body it calls rewrites *every* app's tier by
   category (`MonolithRepository.kt:1126`). R-4's Ghost Preview was
   ruled in the audit and did **not** land in this pass — grep for
   any preview/ghost path in the screen returns nothing. A tab's
   clothes on a bulk-rewrite weapon, one release later.
2. **The cap still displays history as state.** `loadoutCapIndex`
   is, by its own comment, "his last hand pick" — while a routine's
   loadout may be the one actually running. R-4(b)'s source print
   (`BY HAND` / `BY SLEEP · UNTIL 03:30`) did not land. The status
   sentence now tells the truth at the top of the screen while the
   cap, 100dp below it, still wears a state's clothes over a
   history fact.
3. **The headline law is one witness short and everyone senior
   knows it.** See Max-Impl §2: usage access. Shipping "BLOCKING
   NOW" on a two-witness proof for a three-witness enforcement is
   precisely the class of overclaim this issue exists to kill; the
   residual is recorded in the lead file, which is honest — but a
   recorded overclaim that ships is still an overclaim on the
   phone.
4. **Zero semantics.** `grep -c contentDescription` on the current
   screen: **0**. R-11 called this disqualifying at the stated bar,
   "not polish". It remains disqualifying; the sleds are Canvas/
   AndroidView objects TalkBack cannot operate, and no
   `stateDescription` exists anywhere in the screen file.
5. **The refusal still shouts from the wrong room.** The ratchet
   banner still renders as item ~1 at the top of the LazyColumn
   (screen L238) with the hardcoded colour `Color(0xFFFBF1EC)`
   (L251) the audit called unprovable in the day theme. R-7 —
   refusal at the point of refusal, tokenised colour — did not
   land in this pass.
**Price to cure the set:** items 1–2 = R-4 as already priced
(~120 lines Ghost Preview + source print, audit §R-4); item 3 =
~15 lines (Max-Impl); item 4 = FASSUNG's semantics pack, already
specced in the lead's follow-up; item 5 = small (audit merge item
8). None of these need new design decisions — they need merge
slots.

— Brutal critic

## 4. Improver — signed: IMPROVER seat, Wave-2

**Finding — the landed sentence is right but motionless, and a
still line reads as a stale line.** Evidence: the status item is a
bare `Text` in a variable-height slot; the lead's own follow-up
already records the convergent fix (fixed 40dp slot, 260ms Curve-A
whole-line re-strike, 250ms anti-flap coalesce — PANZER/SCHWUNG).
Improvement beyond that filing, small and priced: **give the
sentence the tally's governed manners** — the tally already
coalesces at 250ms before re-striking (screen: `LaunchedEffect(
tallyNow) { delay(250) … }`); the status derivation arrives on a
1s ticker, so a witness flicker at a session edge can print
BLOCKING → UNPROVEN → BLOCKING inside three seconds. Route the
headline through the same 250ms stability coalesce before print
(one `LaunchedEffect` mirroring the tally's, ~10 lines). Truth is
not harmed — a sub-250ms flicker was never a printable fact — and
the line stops strobing at exactly the moment (session start) when
he is watching it hardest. **Price: ~10 lines, 1 test on the
coalesce behaviour at the composable's seam or a recorded paper
argument if the seam resists unit testing.**

— Improver

## 5. Designer — signed: DESIGNER seat, Wave-2

**Finding — the status line's colour still editorialises against
the lead's own ruling.** Evidence: current screen tone map —
LIVE = `onBackground`, NO_SESSION = `onSurfaceVariant`, UNPROVEN/
STALE = `scheme.error`. The lead file records four seats'
convergent measurement: `scheme.error` by day is **3.49:1** —
below legibility comfort for the one line on the screen that must
never be misread — and the named fix (full ink in all states,
state carried by verb + a 10dp solidity glyph) is follow-up item
#1, not landed code. A not-blocking state is a *fact*, not an
alarm; painting facts in error-red trains him to either panic at a
scheduled down-wall or ignore the colour entirely — both destroy
the channel. **Price: as filed in the lead's follow-up — the glyph
joins the solidity scale already adopted in R-12 (SHUT solid /
PEEK 55% / OPEN frame), ~½ builder-day with the rest of that pass;
the colour change alone is ~5 lines and should not wait for the
glyph if the pass slips.**

— Designer

## 6. Consumer — signed: CONSUMER seat, Wave-2

**Finding — as a buyer reading this screen cold, I cannot tell
what I own.** Evidence: a consumer's three questions — *is it on,
what does it block, how do I get out* — score: (1) answered, top
line, if the derivation is supplied; (2) answered by the tally +
grouped catalogue (R-1 landed, verified: one enumeration,
`groupedForCatalogue()`, PEEK → SHUT → OPEN); (3) **nowhere** —
no line on Shield says how a session ends early, what the one
emergency break is, or where it lives. That may be deliberate
(friction by design in a discipline app) and this seat does not
demand a button — but a consumer who cannot find the exit assumes
there isn't one and uninstalls at the first false lockout. One
printed line in the status slot's neighbourhood, when LIVE —
`ENDS 03:30 · BREAK LIVES ON THE DECK` (wording Ren's) — converts
a trap-feeling into a contract. **Price: ~3 lines, printed only in
the LIVE branch where `endsAt` is already computed.**

— Consumer

## 7. Student — signed: STUDENT seat, Wave-2

**Acquittal with evidence, one barb.** The student's use is the
1 AM test, and on the catalogue path it now passes on paper:
tally → group → sled is one enumeration (R-1 landed), the tally
sums to the phone (`tallyText` prints SHUT · PEEK · OPEN from
counts that partition `allApps`), and RUNNING NOW is session-keyed,
not window-guessed. The barb, with evidence: the student sets
SLEEP 20:00 → 20:40 the night before a test — **still impossible
on this branch**: the expanded RoutineRow still offers only
`−30` / `+30` chips (`onAdjustStart(±30)`, `onAdjustEnd(±30)` in
the current screen). The lead delegates this to Voss's batch-10
wheels "unanimous across the cell", which this seat accepts as a
delegation, not a cure — until the wheels merge, the audit's
unreachable-time charge stands open against the *product*, however
clean the paperwork. **Price: already paid in the routine-edit
lane (432-line diff per the audit); the cost is merge order, and
the merge order should name it item #1 of batch-10, not a rider.**

— Student

## 8. User — signed: USER seat, Wave-2

**Finding — the websites editor still exiles him, and still eats
his typing.** Evidence, current tree: BLOCKED WEBSITES is SECTION
6, *below* the entire grouped catalogue (~74 rows); R-3's move-up
(header + add field + ≤5 rows + SHOW ALL, above ALL APPS) did not
land in this pass. Worse, the ADD path still clears the field
unconditionally on a non-blank tap (`onAddSite(newSiteText);
newSiteText = ""`, L572–575) with no rejection branch — R-3's
riding truth fix ("a rejected ADD prints why and does NOT clear
the field") is absent; an invalid or duplicate domain vanishes
from the field exactly as if accepted. Silent success-costume on
a failed act is the same species of lie as the old status flag.
**Price: move + cap ≈25 lines (Dens F4, as ruled); rejection print
= the repository must return a reason — if `onAddSite` stays
fire-and-forget, the screen cannot print honestly, so price
includes changing the callback to return a result string (~15
lines across the seam).**

— User

## 9. User-experience — signed: UX seat, Wave-2

**Finding — two ruled word-cures are still unprinted, and one of
them sits in the catalogue's own doorway.** Evidence, current
tree: (a) search placeholder still reads `Filter apps…` (L403) —
R-6 ruled `SEARCH APPS` because the field *is* a search (it drives
the catalogue + the no-match line `NO APP BY THAT NAME ON THIS
PHONE`); "filter" understates it and mismatches the category
words beside it. (b) The PEEK group header prints the bare word
`PEEK` (`CatalogueGroupHeader` = `tierWord(tier)` only) — R-6's
once-on-this-screen contract line (`PEEK — 2 MINUTES, THEN SHUT
AGAIN · 20 MIN BETWEEN PEEKS`, derived from constants) is absent;
grep for the contract text across screen + status file: no match.
A first-time user meets the PEEK group with no idea what PEEK
*buys* — the one ruling whose meaning is not in its name.
(c) The reset note still prints the old sentence (`SCHEDULE RESET
TO YOUR DAY — TAP ANY ROW TO CHANGE IT`, L305) instead of R-6's
repository-proven reword, and nothing in the screen enforces the
48h residency death (that half may live in the caller — unverified
here, flagged not charged). **Price: (a) 1 line; (b) ~6 lines +
deriving from the peek constants rather than hardcoding, per R-6;
(c) 1 line + a caller check.**

— User-experience

## 10. Purpose-questioning — signed: PURPOSE-QUESTIONING seat, Wave-2

**Question filed against the screen's reason to exist — and
answered in its favour, with one condition.** If the Deck arms the
session and PARK holds the sniper/blocker switches, why does
Shield deserve a tab? The audit's one-job sentence answers it:
Shield is the *ledger of his decisions* — what is SHUT, what
PEEKs, what stays OPEN — and the only surface where a ruling is
read, made and revoked in the same place (R-1's achievement,
landed). The status sentence could be accused of trespassing on
the Deck's warrant — but the lead's vocabulary ruling holds the
border correctly: Deck asks "may I arm?" before the fact, Shield
states "is it standing?" during it; different questions, different
words (`CAN'T CHECK BLOCKING` stays Deck's). **Condition:** the
trespass becomes real if Shield ever grows arming controls.
Recorded as a standing watch, not a charge: any future proposal
that puts a START/ARM act on Shield should be refused by citing
this section. **Acquittal of the screen's purpose, with evidence
above. Price: none.**

— Purpose-questioning

## 11. Thoughtful — signed: THOUGHTFUL seat, Wave-2

**Finding — the fallback line is the most honest sentence in the
app, and it will be the one he sees at the worst moment.**
Evidence: when no derivation is supplied, the screen prints
`ENFORCEMENT NOT PROVEN — NO WITNESS READ YET` (Salz C-3's cure:
a cold-start absence of Room emissions is not a measured
NOT BLOCKING). Consider when that line actually appears: cold
start, mid-session, at night — precisely when a false comfort or
a false alarm both cost him. The wording is truthful but it is
*system*-voiced, not man-voiced; "NO WITNESS READ YET" asks him to
know what a witness is. Thoughtful does not ask for a lie of
omission — it asks that the same fact wear his vocabulary:
`CHECKING THE WALL…` is a claim about the app's activity that is
also true in that instant and resolves within the ticker's first
second. If the derivation has not arrived after, say, 3 seconds,
*then* print the unproven sentence in full — a delay before an
alarming truth is not a suppression of it, provided the delay is
bounded and the interim line is itself true. **Price: ~8 lines
(a timed state in the status item) + 1 paper note in the status
file recording the bound, so no future editor stretches it.**

— Thoughtful

## 12. Deep-thinker — signed: DEEP-THINKER seat, Wave-2

**Finding — the proof architecture has a clock problem nobody has
priced.** Evidence: staleness is judged on *wall* time —
`nowWallMillis - beatRef > SHIELD_GAP_THRESHOLD_MILLIS` (10s),
where `beatRef` is a persisted wall-clock heartbeat. Wall clocks
move: NTP corrections, timezone travel, a user nudging the clock
forward 11 seconds mid-session flips a live wall to `SHIELD PROOF
STALE` (a false negative — safe direction, trust-eroding), and a
clock set *backwards* extends a dead beat's freshness (a false
positive — the dangerous direction, bounded by the two witnesses
still needing to attest, which limits but does not erase it). The
deep fix is a monotonic reference (`elapsedRealtime`) persisted
alongside the wall beat, with wall time kept only for the printed
ENDS hour. **Price: one extra persisted field + comparison swap,
~20 lines + 2 truth-table rows (clock-forward, clock-back) —
cheap now, expensive to retrofit after the phone has taught him
to distrust the sentence once.** This seat files it as a
remainder, not a MUST: the witnesses gate the dangerous direction
today.

— Deep-thinker

## 13. Enthusiastic designer — signed: ENTHUSIASTIC DESIGNER seat, Wave-2

**Endorsement with a buildable flourish, priced.** The grouped
catalogue (R-1) quietly created the screen's best object and
nobody has celebrated it into existence yet: three group headers
— PEEK, SHUT, OPEN — that are a *map of his discipline* in one
scroll. Enthusiasm, made concrete: let the tally at the top be
the headers' twin — tapping `4 PEEK` in the tally should land him
at the PEEK header (Plan §5.1's words-as-jumps, ruled in R-1,
**not landed**: grep finds no `scrollToItem` / `animateScrollToItem`
in the screen; the tally is still print-only). The mechanism is
half-built: `listState` is already the screen's saveable
LazyListState, and the group header items already carry stable
keys (`catalogue_group_<tier>`). What remains is an index map
from tier → first index, computed as the LazyColumn items are
emitted, plus the tally's words becoming click targets.
**Price: ~40 lines. This is the difference between a screen that
*reports* his rules and a screen that *moves* like his rules —
ship it in the finish pass and the Shield becomes the tab he
shows people.**

— Enthusiastic designer

## 14. Wow-level designer — signed: WOW-LEVEL seat, Wave-2

**Finding — the screen has no single unforgettable frame, and the
material for one is already computed.** Wow is not decoration; it
is the moment the object tells you it understands you. Candidate
frame, fully priced: **the session-night ground.** LUFT's A-1
(already in the lead's follow-up: night keyed on proven LIVE)
means the whole screen's ground shifts when — and only when —
the wall is *proven* standing. Take it one step further: at the
LIVE transition, the status sentence's re-strike (Improver §4's
260ms Curve-A) is accompanied by the ground stepping down one
density across the full screen in the same 260ms — no glow, no
green, no fanfare; the room dims because the wall is up, the way
a theatre dims because the play has begun. When proof drops, the
ground steps back up. He will *feel* enforcement state peripherally
without reading a word — the strongest possible answer to the
headline law, delivered below conscious attention. Spec guards:
keyed strictly on `proof == LIVE` (never on `isSessionActive`),
one step only, `MonolithCurves.EasingA`, respects the existing
`effectiveNight` logic rather than replacing it. **Price: ~25
lines (animate the background colour target on the root
LazyColumn) + the discipline to refuse every request to make it
bigger.**

— Wow-level designer

## 15. Designer with 29 years of experience — signed: DESIGNER-29Y seat, Wave-2

**Finding — twenty-nine years says: the unfinished item that
kills mature screens is never the new feature, it is the dead
parameter.** Evidence: the screen's signature still carries the
seven ghost wires R-9 ordered struck — `shortsReelsSniperEnabled`,
`browserUrlBlockerEnabled`, `profiles`, `selectedProfileName`,
`onToggleShortsReelsSniper`, `onToggleBrowserUrlBlocker`
(L90–93, L115–116 of the current file), each occurring at
declaration only; their real home is PARK (sleds verified in the
audit at ParkDrawer :584 / :753). This pass (understandably)
spent itself on the status sentence and left the ghosts for
"merge". I have watched "merge" mean "never" for three decades:
every future reader of this composable must now re-derive whether
these parameters matter, and one day a tired builder will wire
one of them to something and resurrect a second home for the
sniper switch — the exact duplication disease R-1 just cured one
section down. Deletions are the cheapest truth there is.
**Price: negative lines (~−10 plus the unused-import sweep);
the cost is one call-site pass at the merge, which Ren owns.
Filed as a MUST below for exactly that reason.**

— Designer with 29 years of experience

## 16. Intentionality-focused designer — signed: INTENTIONALITY seat, Wave-2

**Finding — every element on this screen can now answer "why are
you here?" except one: the LOADOUT header.** Evidence: R-4(c)
ruled it killed — "the cap self-labels" — yet the current screen
still prints `FullBleedSectionHeader(titleWord = "LOADOUT")`
above the cap, a section title for a three-position selector
whose positions (STUDY / WORK / SLEEP) are already its label.
Intentionality test: the header carries no count (deliberately
removed by Liv D3), no state, no action — it is a nameplate on a
self-naming object, pure ceremony. Its 84dp slot also delays the
ROUTINES section, the screen's second-most-alive system. Kill
the header, let the cap sit in the tightened ~52dp slot R-4
priced, and the screen's intentionality audit goes clean: status
sentence (proof), cap (the bulk act), routines (the schedule),
tally (the ledger), catalogue (the rulings), websites (the other
ledger) — six objects, six reasons, zero ceremony. **Price:
−8 lines, slot 84→~52dp, rides free inside R-4's merge item.**

— Intentionality-focused designer

## 17. Synchronous collaboration — signed: SYNC-COLLABORATION seat, Wave-2

**Finding — this screen is where three other lanes' work comes to
be believed, and the seams are currently held by comments, not
contracts.** Evidence of three live seams: (1) Voss's batch-10
time wheels must land inside `RoutineRow`'s expanded body, whose
current contract is `onAdjustRoutineTime(routine, isStart, delta)`
— a ±delta API the wheels (absolute `setRoutineTime`) do not
speak; nobody has filed the API change, so the wheels' merge will
discover it at the seam. (2) GLANZ's finish-pass items P-1…P-9
(tally jumps, sticky headers, PEEK contract) all touch the same
LazyColumn regions this pass restructured — the lead file lists
them as one follow-up job, which is the right instinct; this seat
asks that the follow-up be *one builder, one branch*, not five
finishers editing one file in parallel. (3) The cap's source
print (R-4b) needs the repository to expose the *currently
applied* loadout — a ViewModel/repository deliverable that no
screen-side seat can build; it must be commissioned as its own
line in the merge list or R-4(b) will silently shrink to the
header-kill. **Price: three lines in the merge plan, written now,
or three surprises later. Collaboration is a scheduling act, not
a sentiment.**

— Synchronous collaboration

## 18. Implementation-quality — signed: IMPLEMENTATION-QUALITY seat, Wave-2

**Acquittal of the landed code's construction, with one
reservation.** Evidence for the acquittal: the derivation is
*pure* — every fact a parameter, no Android imports beyond the
session entity — which is why 17 truth-table tests could be
written against it at all; the vocabulary discipline is real
(`missingProof` is explicitly "a diagnostic word, never printed
as the headline"); the screen honours the seam (it prints
`blockingStatus?.headline` or the unproven fallback, and the
comment at the call site correctly refuses to derive BLOCKING
from `isSessionActive`); R-1's deletion was actually executed
(~200 lines of slip machinery retired, per the lead's diff count
+227/−317 across 6 files) rather than commented out. Reservation:
`shieldSessionEndsAtMillis` computes a hand session's end as
`now + spanRemainingSeconds` at derivation time — correct on a
1s ticker, but it means the printed ENDS can drift a second
either side of the Deck's desk clock if the two tickers phase
apart; a shared "ends at" on the status object, computed once per
emission by the ViewModel, closes it. **Price: ~10 lines if Ren
wants it closed; filed as remainder — a one-second disagreement
between two honest clocks is a blemish, not a lie.**

— Implementation-quality

## 19. Technical execution specialist — signed: TECHNICAL EXECUTION seat, Wave-2

**Finding — the sentence's freshness is a 1s ticker plus a 10s
gap law, and the arithmetic has an unexamined edge at session
birth.** Evidence: `beatRef = max(shieldLastBeatWallMillis,
startWallTimeMillis)` — correct for the never-yet-beaten row.
But consider a session whose first beat lands at +9s and whose
witnesses attest at +2s: from +10s (gap law measured from *start*)
until the beat's own cadence re-establishes margin, the status
can flap LIVE → STALE → LIVE on beat jitter near the threshold,
because the reference stays the *last* beat and the loop's period
is 2s against a 10s threshold — five missed beats of headroom in
theory, but the threshold is measured from the most recent beat,
so a single 10s stall (Doze, OEM battery kill of the loop's
coroutine) prints STALE even though the service lives. That is
the *safe* direction and this seat does not ask to widen the law
— Vogt ruled the 10s law governs. The execution ask is narrower:
the STALE headline (`NOT BLOCKING — SHIELD PROOF STALE`) should
carry the beat's age in its diagnostic path so a device report
can distinguish "loop dead" from "loop stalled once" —
`missingProof`-style, unprinted or printed small, either way
*recorded*. **Price: ~5 lines (populate the existing diagnostic
field for STALE, currently empty) + 1 truth-table assertion.**

— Technical execution specialist

## 20. Interaction-detail specialist (buttons, 3D, vibration, peak perfection) — signed: INTERACTION-DETAIL seat, Wave-2

**Findings at the fingertip scale, each with evidence:**

1. **The sled's drag/draw mismatch survives this pass.** Audit
   R-10 S-1: drag pitch 56dp/stop vs draw pitch 54dp/stop — a
   ~4dp lag at the far stop, the cap trailing the thumb. The
   sled view was untouched by this pass (the screen still hosts
   `AppRowSledToggleView` at 54dp row height), so the mismatch
   stands until the finish pass makes them one constant.
   **Price: 1 constant, as filed.**
2. **No provisional cap.** R-10 adopted Vega V-2 (rocker draws
   hollow between detents until the seat, ~15 lines) — not
   landed; mid-drag, the cap still commits its colour story only
   at the seat, so at 1 AM a half-throw reads as no-throw.
3. **Refusal has no body.** TAST T-1 / KLANG F-1 stand as filed
   in the lead's follow-up: a refused sled throw can voice a
   commit-shaped sound, and `applySmartLoadout` fires the Throw's
   body for a non-seal. The lead ruled: no sound changes this
   pass; print-only refusals on Shield rows (R-7's letter). This
   seat records the *haptic* half of that ruling as still open:
   a refused throw should return a short, dull, *non*-vibrating
   seat-knock — or deliberate stillness — but never the success
   detent's vibration pattern. Peak perfection is a refused act
   feeling refused in the hand before the eye reads why.
   **Price: items 1–2 as filed (~15 lines + 1 constant); item 3
   is one sound/haptic mapping decision for Ren (KLANG's
   seat-knock dialect is the recorded candidate), ~10 lines once
   decided.**
4. **Acquittal, with evidence:** the sled throw itself — 1:1
   drag, 800dp/s flick, Curve A settle, cancel re-seats, cap
   colour follows the rocker not the commit (audit R-10,
   Panzer's full acquittal) — remains the best-feeling object in
   the app. This seat does not touch acquitted physics.

— Interaction-detail specialist

---

## MUST FIXES (before this issue can call itself done)

1. **Third witness (usage access) into the proof** — Max-Impl §2 /
   Brutal §3. ~15 lines + 4 tests. Until then BLOCKING NOW can
   overclaim on a revoked permission. Ren's merge call, already
   named in the lead file — this wave makes it a MUST, not a
   follow-up.
2. **Ghost Preview + cap source print (R-4 a/b)** — Brutal §1–2.
   ~120 lines as priced in the audit. The bulk-rewrite cap must
   preview its consequences (including STUDY's sniper/blocker
   side-effects, named in print) and print whether its position
   is BY HAND or BY routine.
3. **Semantics pack (R-11)** — Brutal §4. Zero
   contentDescriptions is disqualifying at the stated bar;
   FASSUNG's pack (sled self-description at its core) is already
   specced.
4. **Strike the ghost parameters (R-9)** — Designer-29Y §15.
   Negative lines; do it in the same merge as #2 before a tired
   hand wires one.
5. **Status line colour cure** — Designer §5. Full ink in all
   states; the ~5-line colour change lands even if the glyph
   waits for the finish pass. 3.49:1 error-red on the truth line
   is a legibility defect, not a style choice.
6. **Compile + run the 17 tests + device proof (Ren only, on
   Sarthak's word).** Everything above — and everything the lead
   landed — is DECLARED paper. The headline law is not satisfied
   by a derivation that has never executed.

## REMAINDERS (priced, named, not blocking)

- R-7 in-place ratchet refusal + banner colour tokenised
  (`0xFFFBF1EC`, screen L251) — small, audit merge item 8.
- R-3 websites move-up + capped list (~25 lines) and rejected-ADD
  prints + keeps text (~15 lines across the callback seam) —
  User §8.
- R-6 word set completion: `SEARCH APPS` (1 line), PEEK contract
  line in the group header (~6 lines, derived from constants),
  reset-note reword + 48h residency check — UX §9.
- LOADOUT header kill + slot tightening (rides R-4) —
  Intentionality §16.
- Tally words-as-jumps (~40 lines) — Enthusiastic §13; sticky
  group headers per GLANZ if the phone proves the need (lead's
  standing alternative stands).
- Status coalesce 250ms (Improver §4, ~10 lines); fixed 40dp
  status slot + Curve-A re-strike (lead follow-up, PANZER/
  SCHWUNG).
- Sled S-1 constant unification + V-2 provisional cap (~15
  lines); refusal sound/haptic mapping pending Ren's decision —
  Interaction-detail §20.
- Monotonic-clock beat reference (~20 lines) — Deep-thinker §12.
- STALE diagnostic age (~5 lines) — Technical execution §19.
- Shared ends-at on the status object (~10 lines) —
  Implementation-quality §18.
- Exit-contract line in LIVE state (~3 lines) — Consumer §6.
- Fallback wording bound (`CHECKING THE WALL…` ≤3s, then the
  full unproven sentence) — Thoughtful §11. Needs Ren's wording
  ruling; the bound must be recorded in the status file.
- Time wheels (batch-10, Voss) + the `onAdjustRoutineTime`
  absolute-time API change filed *before* that merge —
  Student §7, Sync-collaboration §17. Day Rail remains the named
  successor mechanism (R-8).
- Crazy-ideas' Turned-Away Ticker (~60 lines) and Max-Impl's
  Wall-Test Canary (~180 lines) — new mechanisms, proposed with
  spec + price above; neither blocks the merge; both serve the
  headline law. Ren sequences them.

## DECISION-FOR-SARTHAK

- **None requiring his design judgment.** All open items above
  are engineering/merge-order calls for Ren under standing
  authority (agents never compile; Ren compiles only on
  Sarthak's explicit word — MUST #6 waits for that word, it does
  not request it here).
- One item is flagged *for Ren to route only if he judges it a
  values call*: Thoughtful §11's fallback wording — whether the
  cold-start line may say `CHECKING THE WALL…` for up to 3s
  before printing the full unproven sentence. This wave's
  recommendation: Ren rules it as a wording pass under SAUM's
  copy table; it does not need to reach Sarthak.

— Wave-2 seat-runner, issue #7 cell, under VOGT (lead),
Oct 30 2026. DECLARED: paper only, nothing compiled, no code
written — this file is the whole delivery.
