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

----- (merged-in branch version below) -----
# WAVE-2 NAMED SEATS — ISSUE #9 (PARK)

*Seat-runner: Wave-2 named-types runner, working under lead VOGT
(issue #9 cell, Hof's cell; branch `agent/issue-9`, clone
`builds/cells/issue-9`). Sources read in full: `studio/plan/AUDIT-park.md`,
`studio/plan/issue-9-plan.md`, current `ParkDrawer.kt` (1,065 lines) and
`GuardLine.kt`, plus the stray builder clone's `GuardLine.kt` and
`studio/plan/issue-9-builder.md` for the fold/kill verdict in §0.
Paper only — nothing compiled, no app code touched, no commits.
Critics unrestricted; every charge below carries evidence or is
marked an acquittal. Prices are estimates in lines / S-M-L, Ren's
build on Sarthak's word is the only proof.*

---

## §0 · THE STRAY CLONE VERDICT — `GUARD_NOT_CHECKED` (builder clone) vs the landed NOT CHECKED print (main cell)

**VERDICT: KILL. Do not fold.**

Evidence, diffed directly (`builds/cells/issue-9-builder/…/GuardLine.kt`
against `builds/cells/issue-9/…/GuardLine.kt`):

- The stray change is one thing only: a public top-level
  `val GUARD_NOT_CHECKED = GuardLinePrint("GUARD — NOT CHECKED")`
  returned from the two unmeasured paths. The printed string is
  byte-identical to what the main cell already lands — the main
  cell returns `GuardLinePrint("GUARD — NOT CHECKED")` literally at
  both paths (`GuardLine.kt:39,42`), pinned by two test files
  (`GuardLinePrintTest.kt:47,55`, `ChromeWaveUnitTest.kt:157–159`).
  On the print itself there is nothing to gain; it is already shipped
  in this branch.
- The vehicle is a regression. The stray clone keeps the OLD
  signature `fun guardLineFor(...): GuardLinePrint?` (nullable).
  The main cell's F-G4 patch (plan addendum) deliberately made
  `guardLineFor` **total** — non-null return, `guardLinePrint` flow
  typed `StateFlow<GuardLinePrint>` with the NOT CHECKED print as
  its initial value, `SealFreeze.guardLine` non-null with a NOT
  CHECKED default (`MonolithViewModel.kt:631–635,698`), and the
  header band's null gate removed. Folding the stray file re-opens
  the null pathway the cell just closed in type, re-breaks every
  `.text` call site the tests now use without `?.`, and re-imports
  the builder dossier's own R-6 problem (a NOT CHECKED seal freeze
  that silently fell through to live derivation — builder §3.3),
  which the main cell answered by making the freeze carry the line.
- The stray clone is stale in a second way: its §2 CHECK sketch
  proposes `onCheckClick: () -> Unit = {}` **defaulted**; the main
  cell's plan states the opposite as a law — the parameter "has
  exactly one host (MainActivity) and no default — a second host
  cannot silently seat the drawer without wiring the verb." The
  stray branch is a superseded draft of this issue, not a patch on it.
- The one real merit in the stray idea — the literal
  `"GUARD — NOT CHECKED"` now lives at four sites in the main cell
  (GuardLine.kt ×2, MonolithViewModel.kt initial value, SealFreeze
  default) — does not need the stray file. If Vogt wants it, it is a
  fresh 15-minute refactor **inside the main cell**: hoist a
  `val` (or const-constructed print) in GuardLine.kt, keep the
  non-null signature, point all four sites at it. That is a new
  change authored against current code, not a fold of a dead clone.

Price of the kill: zero lines in the main cell; the
`issue-9-builder` clone/branch is left untouched on disk for the
record (deletion is Ren's call, not this seat's).

— Wave-2 seat-runner, for Vogt

---

## 1 · Crazy-ideas

**NEW MECHANISM — "Two Witnesses" (cross-examination CHECK).**
Today every grant is believed from exactly one witness, and the
audit's own RESIDUE + Critic C-7/C-8 record where that witness lies:
BIND rows read `instance != null` (presence, laundered fresh every
2 s by the shield loop), and the residency stamp precedes the
own-package filter, so LIVE can rest on Monolith's own windows.
CHECK currently re-asks the *same* witness louder.

Spec: each counted door gets a second, independent read through a
different Android surface than the primary query —
ACCESSIBILITY: enabled-services list parsed from
`Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES` vs the live
`instance != null`; USAGE ACCESS: raw `AppOpsManager` OPSTR check
vs the repository's `usageAccess()`; DEVICE ADMIN: active-admins
list membership vs `isDeviceAdminActive()`; NOTIFICATION LISTENER:
enabled-listener flat names vs `instance != null`. CHECK runs both
reads per door; agreement prints as today; **disagreement prints a
new board word, `DISPUTED`, in FULL ink**, and a disputed door
cannot count toward the warranted n/4 or complete LAST PROVED's
set. No decoy is posted, no pipeline is rehearsed — this is
cross-examination of grants, not the Live-Fire Strip.

Price: M, ~150–200 lines (second-witness queries in
`MonolithRepository.watchGrantQuery`'s neighbourhood, one
`witnessAgree` field on `WatchMeasurement`, one print word + ink
mapping in ParkDrawer, tests per door for agree/disagree). No DB,
no new permission, no new surface. It shrinks C-7/C-8 from
"stands, escalated" to a printed word — the residue becomes visible
on the board instead of true in a filing cabinet.

Second idea, which touches the parked Live-Fire Strip and is
therefore **not proposed for build**: a long-press on CHECK that
escalates from re-measurement to rehearsal. Recorded under
DECISION-FOR-SARTHAK at the foot of this file; issue #21 owns it.

— Wave-2 seat: Crazy-ideas

## 2 · Maximum-implementation-potential

**NEW MECHANISM — "The Vigil": while PARK stands open, no row is
ever allowed to cross into STALE unwitnessed.** The machinery is
all landed: `STALE_AFTER_MS = 45_000`, the open drawer already runs
a 5 s `nowMono` beat (`ParkDrawer.kt`, `LaunchedEffect(visible)`),
and the host already re-sweeps on open. The Vigil closes the loop:
while the drawer is open and unsealed, compute the nearest stamp
expiry among GRANTED rows (`measuredMono + 45s − now`); when the
beat finds a row within one beat of expiring, dispatch
`runWatchSweep()` once. Watching the board then means the board
keeps itself proved — CHECK remains as the manual verb and the
cold-open cure; the Vigil is CHECK made continuous for exactly the
duration a man is looking at it, and it dies with the drawer
(close, seal, or dismissal all end it — the existing
`LaunchedEffect(visible)` scope is the whole implementation
surface, plus the host callback PARK already holds).

Spec/price: S, ~40 lines (one expiry computation + one dispatch in
the existing beat loop, one test: open drawer + ageing stamps →
sweep dispatched before any row prints STALE; sealed drawer → no
dispatch). No DB, no permission, no setting, no new object on the
surface. Battery cost is bounded by the drawer's open minutes —
this is not a background cadence and must never become one.
Maximum potential read of the rest of the branch: the generation
machinery (C-1's cure) is stronger than its current job — the same
`lastCompletedSweepGen` parity that releases CHECKING could stamp
the foot verdict line (§4.3 remainder) for free when that pass lands.

— Wave-2 seat: Maximum-implementation-potential

## 3 · Brutal critic

**Finding: this branch cures 2 of the audit's 12 merge items and
must not be allowed to wear the audit's verdict.** Evidence is the
cell's own plan ("This branch carries §4.1 + §4.5 + the P-7
verification only") read against the current file: the header
inside PARK still prints bare `GUARD` (no `{n}/4`, §4.3 unlanded);
there is still no warrant line, so the board still cannot say LIVE
(§4.2, P-2 — the audit's S1 spine, answered here only by escalation
C-6/C-7); bottom padding is still `bottom = 100.dp`
(ParkDrawer.kt:420) against §4.11's ruled cut to 24; the plate
still prints 8 body lines including the COLOUR line the audit cut
for contradicting the file's own "orange zero" law and the
`DATA: ON THIS PHONE ONLY` segment Fremd falsified (§4.9); the
FIX return verdict is still, in the code's own words, "no word
prints, ever" (§4.6 / P-5 unlanded). Hof's §8 bar — "with §4
landed, PARK becomes what its first half always claimed" — is not
met by this branch and the plan, to its credit, never claims it is.
The charge is against any *future* telling that lets "issue #9
done" blur into "PARK audit done": 8 S + 3 M of merge list remain,
priced in the audit, owned by nobody yet.

Price of the cure: the audit's own §4 total for the remaining
items; price of the dishonesty if it happens: the whole audit.

— Wave-2 seat: Brutal critic

## 4 · Improver

**Finding: one literal, four homes — dedupe the NOT CHECKED print
inside the main cell (the salvageable kernel of the stray clone,
done right).** Evidence: `"GUARD — NOT CHECKED"` is constructed at
GuardLine.kt:39 and :42, MonolithViewModel.kt:~635 (SealFreeze
default) and :~698 (flow initial value). Four constructions of the
app's most truth-critical string is three chances for a future edit
to fix one site and lie at another. Improvement: one
`val GUARD_NOT_CHECKED_PRINT` in GuardLine.kt, all four sites
reference it, signature stays non-null (§0's terms). Price: S,
~10 lines net, existing GuardLinePrintTest already pins the string
so the refactor is test-covered before it starts. Second, smaller:
GuardLine.kt's doc head says "Four states, no fifth" and then
enumerates three bullets (WHOLE / LACK / UNMEASURED — STALE is a
dress of LACK, not a fourth bullet); fix the doc to say what it
means before a future reader invents the missing state. Price: 2
lines of comment.

— Wave-2 seat: Improver

## 5 · Designer

**Finding: the board still has no hierarchy — the tier composition
(§4.7 / §3 of the audit) is wholly unlanded, and the spacing module
is honoured only around the new keys.** Evidence: the board renders
six permission rows flat, a single 16 dp whitespace break, then
three interceptor rows — the exact structure Liv's §1.5 killed
("the 16dp whitespace family break — KILL as the board's only
structure"). No tier eyebrows, no consequence sentences, OVERLAY +
BATTERY still sit as false citizens in the counted family's visual
block. Off-module spacers persist outside the key zone Finisher 2
cleaned: 6 dp under the GUARD header, 14 dp above the board, 32 dp
above the plate — against the ruled {8, 16, 24} module (§4.11).
What the branch did land, it landed in dress: `ParkWordInk`
QUIET/HALF/FULL is a real three-tier ink system and STALE's 0.60
half-ink is the audit's P-4 dress, correctly built. The complaint
is composition, not craft: dress without ranking still reads as
nine equal citizens, and the warrant derivation (§3) proves they
are not equal.

Price: §4.7 as audited — M (eyebrows, consequence line, OFF/NO
DOOR split, false citizens out); spacing sweep S (~15 lines).

— Wave-2 seat: Designer

## 6 · Consumer

**Finding: the plate still sells a number the app no longer owns.**
Evidence: `PLATE_BODY` prints `"PEEK: 2 MINUTES, THEN SHUT AGAIN"`
as a hard-coded string while the lease reads
`PEEK_DURATION_SECONDS = 120` — the plan's own W-08 section names
this "one fact, two sources, already a lie waiting for Frist's
editable times (batch 10) to make it print a stale number," and
prepares the input contract (same value the lease reads; null →
omit the number; 0 → OFF form; 1 → singular). The contract is
prepared; the plate is not built; the consumer-facing fact on the
furniture is still the hard-coded one. A buyer of this app's
premise — it tells the truth about itself — is being told a
constant dressed as a measurement. Acquittal on the neighbouring
charge: `FIRED AS SET` is genuinely gone from the plate (grep
finds it only in the UNA-05 comment and paper), and the surviving
`ROUTINES: SET BY HAND ON SHIELD` claims only what is true.

Price: batch-10's plate work as already contracted; interim
one-line honesty fix if batch 10 slips: none honest exists short
of the contract — do not paper it, schedule it.

— Wave-2 seat: Consumer

## 7 · Student

**Finding: a first-time reader cannot learn from this drawer what
"SELF-REPORT" means, and the audit's cure for that was never
landed.** Evidence: the LAST PROVED line prints
`{HH:mm} · SELF-REPORT`; AUDIT §1.3 ruled the qualifier stays but
"gets one glossary line where the reception reprints it" — no
glossary line exists in ParkDrawer.kt, and the phrase appears
nowhere else in the drawer a student could learn it from. The
distinction it carries is the drawer's whole lesson — this instant
is the app reporting on itself, not a witness — and it is printed
in the app's most compressed register to the reader least equipped
to decode it. Second, same reader: the drawer's header says
`GUARD`, the header band outside says `GUARD — NOT CHECKED` /
`GUARD {n}/4`, and nothing inside the drawer ever reconciles the
two prints (§4.3 remainder) — a student reasonably asks which one
is the count.

Price: glossary line S (~5 lines at the colophon when §4.8 lands;
standalone, one ruled line under LAST PROVED); header count is
§4.3's price, already counted by the Designer seat.

— Wave-2 seat: Student

## 8 · User

**Finding (the 1 AM test, Muede's hour): the one repair trip still
abandons him at the border and still answers in silence.** Evidence
from the current return effect in ParkDrawer.kt: on FIX return the
code waits 800 ms, reads the store live, and — repaired or not —
prints nothing; the comment stands verbatim: "no word prints,
ever." P-5's cure (escort card per door, printed FIXED / STILL OPEN
verdict, Healing Staircase walking all lacks in one trip) is
§4.6, unlanded; FIX still names one lack, still lands him on the
general Accessibility list with "find MONOLITH" said nowhere on
this surface. The CHECK key genuinely improves his hour — he can
now re-prove without leaving — but CHECK cannot fix; the moment a
row reads MISSING, he is back on the silent trip. Partial
acquittal: FIX now commits on release, not on DOWN (HEAD commit
`6237468`, Feel 1 T-1), so a scroll that starts on the key no
longer launches him into Settings — a real 1 AM mercy, landed.

Price: §4.6 as audited — M (~80 lines for the Staircase per Vega's
pricing in the audit, plus the escort card and the printed verdict).

— Wave-2 seat: User

## 9 · User-experience

**Finding: CHECK's success and FIX's success speak in two different
voices, and only one of them prints.** Evidence: a whole CHECK
landing plays the firm click AND clears/prints a verdict line
(`NOT PROVED` + first lack, in board grammar, persistent until the
board reads whole); a FIX return plays the same firm click but is
barred from print by design ("no word prints, ever"). Two repair
verbs, one surface, opposite feedback laws — the user must learn
that on this board a click sometimes means "read the new line" and
sometimes means "nothing will ever be written." Consistency cure
is not to silence CHECK (its print is the audit's P-1 cure working)
but to give FIX the print P-5 already ruled. Second: the
`NOT PROVED` verdict is rendered as a `ParkBoardRow` with name
`NOT PROVED` and the lack as its word — visually a tenth board row;
a scanning eye can file it as a door named NOT PROVED. It wants a
dress distinction (it is a verdict, not a citizen) when §4's
composition pass touches the board.

Price: FIX verdict print is inside §4.6's M (User seat);
NOT PROVED dress distinction S (~10 lines, spacing/rule variant)
whenever the board is next opened.

— Wave-2 seat: User-experience

## 10 · Purpose-questioning

**Finding: PARK's ruled job sentence is still only half obeyed —
the junk-room clause was struck in the audit and the junk is still
in the drawer.** Evidence: AUDIT §0 struck "parks everything that
has no other home" and ruled PARK is "GUARD's drawer, not the app's
junk room"; the current drawer still houses the Sound sled (§1.9
ruled MOVE → SHIELD), the Shorts + Reels sled, a Theme row (§1.10
ruled KILL as a row, survive as footnote), and the spec plate —
alongside the board that is its actual job. The Sound/Them moves
are §4.10, unlanded, so this is not a builder's disobedience; it is
a purpose debt the programme still carries: every surface that
keeps a second job teaches the user that no surface has one job,
which is the exact habit the Deck was corrected for an hour before
the audit. Question put to the lead, not answered here: if §4.10
keeps slipping behind newer work, is the job sentence still the
law, or has it been quietly re-struck?

Price: §4.10 as audited — M (sled to Shield, theme row to
footnote/LOOK slot); the question costs one ruling from Vogt/Hof.

— Wave-2 seat: Purpose-questioning

## 11 · Thoughtful

**Finding: the seal still blinds him to a door that dies at night,
and the cheap cure was priced, ruled, and not landed.** Evidence:
AUDIT P-8 — a door dying after the 20:00 SLEEP seal "cannot even
be viewed until morning — disclosed nowhere"; the cure's cheap
half (the seal prints any ungranted door at seal time, §4.12)
"SHIPS" per the audit, and Critic C-4's ruling in this cell's plan
leans on exactly that cure ("its cure is the seal-time warning
line (§4.12, wider pass)") to justify keeping CHECK dead under the
seal. But §4.12 is not in this branch: the ruling's premise is a
future pass. Until it lands, the compound state is real — sealed,
a door dies, PARK is print-only, the print was frozen at the seal
frame, and no warning was ever printed. The thoughtful detail:
this is the one failure in the drawer that happens *while he is
asleep and cannot act*, which is precisely when a truth-telling
app should have done the telling in advance.

Price: §4.12 as audited — S (seal-time warning line + PARK gate
composing `doorLocked`); until then, C-4's record should carry an
asterisk wherever it is cited.

— Wave-2 seat: Thoughtful

## 12 · Deep-thinker

**Finding: CHECK proves grants; its name lets a man believe it
proves blocking — and the branch's own critic file proves the gap
is structural, not rhetorical.** Evidence: Critic C-6 (interceptor
RUNNING has no stale form; `poke()` bypasses the derivation it
claims to refresh; interceptor stamps re-dated at publish), C-7
(presence-proved BIND doors; NOTIFICATIONS' RUNNING never
captured), C-8 (residency stamp precedes the own-package filter)
all STAND after this branch, escalated as "P-2's machinery half."
So a CHECK that lands whole, plays the firm click, and advances
LAST PROVED certifies a *complete set of permission measurements*
(plan C-11 is precise about this) while the three interceptor rows
below — the ones that say whether anything is actually being
blocked — rest on derivation and presence. The two wholes are
named honestly in the code comments (C-11); they are not named
anywhere the user reads. The deep problem is not that CHECK
overclaims in its copy — its copy claims almost nothing — it is
that a verb named CHECK beside rows named RUNNING invites the
inference the machinery cannot cash. The warrant line (§4.2) is
the honest bridge and it is the cheapest unlanded item on the
list.

Price: §4.2 warrant line — S, as audited; C-6/C-7/C-8 remain
machinery work for Ren to route, unpriced here by design (their
own verifier first, per the plan).

— Wave-2 seat: Deep-thinker

## 13 · Enthusiastic designer

**Finding: the CHECK key's dress ladder is the best new object the
drawer has grown — and it deserves its missing third state.**
Evidence: Finisher 2's ladder (print dress on a fresh board; ink
exactly when a row is STALE/UNMEASURED or a sweep is in flight;
MISSING never promotes it because MISSING's answer is FIX) is a
genuinely intelligent piece of interaction design — the key's ink
*is* its recommendation. What it lacks is a rest state that says
"proved just now": after a whole landing, the key returns to print
dress indistinguishable from a board that was never checked this
visit. Proposal, small and in-grammar: for the remainder of the
open, after a whole landing, CHECK carries the LAST PROVED time as
a quiet suffix in Anonymous Pro (`CHECK · 03:41`) — the key signs
its own last work, using the instant the board already computes.
No new colour, no new object, no animation beyond the existing dip.

Price: S, ~15 lines (one text composition from
`snapshot.lastProvedWallMillis`, one render test pinning the
suffix appears only post-landing and clears on close).

— Wave-2 seat: Enthusiastic designer

## 14 · Wow-level designer

**Finding: the drawer has no signature moment — its most dramatic
event, a board going whole, is celebrated with one click sound.**
Evidence: the whole-landing path (plan §1) plays `playFirmClick()`
and clears a lack line; nothing on the surface moves, aligns, or
resolves. Compare the drawer's own grammar elsewhere — the grip
grows under a finger, keys dip 1 dp and return, the sheet seats at
280 SEAT — this app believes in physical punctuation, and the
moment the machine proves itself whole is the one moment that gets
none. Proposal, inside the material law (print + the two keys are
the only objects; no colour, no new surface): on a whole CHECK
landing, the six board rows' hairlines resolve in sequence — each
rule redraws at full ink for one beat, top to bottom at the board's
existing 49 dp pitch rhythm, ~90 ms apart — a proof visibly
*travelling* down the board it just measured, ending at the LAST
PROVED line as its time advances. It is the receipt printing,
staged. Restraint clause: it fires only on a CHECK landing that
turns the board whole, never on open, never on a FIX return, never
twice in one open.

Price: M, ~60–80 lines (per-row rule alpha animation driven from
the existing landing effect; one test that it does not fire on
partial landings). Filed as a proposal — Wow does not outrank the
§4 remainders; it is priced so Vogt can sequence it honestly.

— Wave-2 seat: Wow-level designer

## 15 · Designer with 29 years of experience

**Finding: twenty-nine years says check the declaration order,
because nobody has compiled this branch — and it does not
compile as written.** Evidence: `ParkDrawer.kt:263–272` —
`val fixDip by animateDpAsState(targetValue = if (fixPressed) …)`
reads `fixPressed` (and the animation spec branches on it twice
more), but `var fixPressed by remember { mutableStateOf(false) }`
is not declared until **line 301**, below the dip animator block.
Kotlin forbids reading a local variable before its declaration in
the same scope; this is not a style wart, it is a first-compile
failure waiting for Ren's one permitted build to discover it —
on a branch whose entire status is DECLARED and whose plan
promises the build as the verdict. The fix is beneath contempt,
which is exactly why it survived fourteen seats: move the
`fixPressed` / `fixPressedItem` declarations (and their
reset effect, which references nothing above it) to above the dip
animator block. Zero behaviour change, zero design change.

Price: S, ~10 lines moved, no lines written. This is MUST-1 at the
foot of this file. (Filed with the veteran's hedge intact: if
Ren's compile proves the ordering legal after all, the move is
still free and the charge converts to a readability note — but no
one should bet a build slot on it.)

— Wave-2 seat: Designer with 29 years of experience

## 16 · Intentionality-focused designer

**Finding: every object in this drawer now has a stated intention
except the oldest one — the grab strip's dismissal law and the
sheet's two rest heights are inherited, and the branch never
re-asks whether PARK, now carrying a verb, still wants to be
dismissible by accident.** Evidence: dismissal has three inputs
(scrim tap, sheet drag past 40 %, PARK re-tap — header comment,
ParkDrawer.kt), and a CHECK in flight is abandoned silently by all
three (`LaunchedEffect(visible)` resets `checkPressGen = -1` on
close; the sweep completes in the store, but the *verdict* — the
firm click or the NOT PROVED line — is composed in the drawer and
dies with it: Critic C-2 blesses the sweep's survival, not the
verdict's). The intentionality question: a man who presses CHECK
and brushes the scrim has asked the app a question and closed the
drawer on the answer. Either that is fine (the board will show
fresh stamps on reopen — true, and arguably enough) or the next
open should print the landed verdict once. The branch chose the
first by default, in a comment about state reset, not by ruling.

Price: ruling costs Vogt one paragraph; implementing the
verdict-survives-reopen variant is S (~20 lines — hold
`lastCheckLackName` at host/store scope instead of drawer
`remember`). Recommendation of this seat: rule it, don't drift it.

— Wave-2 seat: Intentionality-focused designer

## 17 · Synchronous collaboration

**Finding: this issue ran two clones, two GuardLines, and two
CHECK designs in parallel, and the synchronisation between them
was a human reading both — the stray clone is the evidence, not
an embarrassment.** Evidence: `builds/cells/issue-9-builder/`
(branch `agent/issue-9-builder`, base trunk `3ea05bd`) implemented
P-6 with a nullable signature and a defaulted-CHECK sketch
(builder dossier §§2–3) while the main cell was landing the
opposite decisions (total function, no-default parameter) on the
same files; the builder dossier even flags its own open questions
for "Hof" (R-1, R-6, §4 scope note) in a document the main cell's
plan never cites. Both cells were right about the string and
diverged on everything structural. The collaboration mechanism
that eventually caught it is this seat-run, after the fact.
Proposal for every future multi-clone issue: one shared
decisions page per issue in `studio/plan/` (the plan file already
exists — make it the *live* one: signature-level decisions like
total-vs-nullable and defaulted-vs-required get one line there the
hour they are ruled, and any second clone's first act is reading
it). §0's kill is the cleanup; this is the prevention.

Price: process only — one standing paragraph in the cell-plan
template, zero code. The cost of not doing it is priced in this
file: one full seat (§0) spent un-merging a clone that never
needed to diverge.

— Wave-2 seat: Synchronous collaboration

## 18 · Implementation-quality

**Finding: the C-1 cure is the highest-quality implementation in
the branch, and its pattern should be named so it is reused.**
Evidence: CHECKING is released only by
`snapshot.lastCompletedSweepGen >= checkPressGen` — a press is
answered exclusively by the completing set of the sweep it
dispatched, in stated parity with LAST PROVED's complete-set rule;
the plan records the first form's failure mode (raw stamp times,
laundered by the shield loop's 2 s presence re-stamps) and the
DECLARED tests pin both halves. That is derivation-over-timer,
generation-over-timestamp, with the failure history written in the
code comment at the site. Contrast, same file, lower quality: the
FIX return verdict still runs on `delay(800L)` — a wall-clock
guess ("the host's sweep lands within ~300 ms; the stability beat
runs 500 ms past it") in a file that just proved it knows how to
wait on a generation instead. When §4.6 lands the printed FIX
verdict, it should wait on the same generation machinery CHECK
built, not on the 800 ms beat.

Price: reuse note, zero lines now; inside §4.6's M, swapping the
800 ms delay for a generation wait is ~15 lines and deletes a race.

— Wave-2 seat: Implementation-quality

## 19 · Technical execution specialist

**Finding: the branch's execution risk is concentrated exactly
where its author said it would be — the uncompiled surface — and
one defect is already visible on paper (see seat 15, MUST-1).**
Corroborating evidence from the technical seat: (a) two separate
5 s clocks now exist to defeat the same staleness-derivation lag —
PARK's `nowMono` beat inside ParkDrawer and the VM's `guardClock`
flow added for Critic C-3 — correct individually, but they are
independent timers drifting against each other, so the band and
the board can disagree about a STALE boundary for up to one beat;
a single shared tick is the tidy form (S, ~20 lines, one flow
consumed by both). (b) `guardLineFor`'s freshness now depends on
callers passing an honest `nowMono`; the VM passes
`SystemClock.elapsedRealtime()` at combine time — correct — but
the function's purity contract means every future caller inherits
the obligation silently; the doc comment states it, acceptably.
(c) Acquittal: the sweep-overlap race the builder dossier feared
(R-2) is answered in the landed design — completion is judged by
generation parity under `publish()`'s synchronization, and C-2
turns the double-press into the escape hatch rather than a fault.
That acquittal is on the code as read, not on a run: nothing here
has executed.

Price: (a) S as above; (b) zero — documented; MUST-1 carries the
compile risk.

— Wave-2 seat: Technical execution specialist

## 20 · Interaction-detail specialist

**Finding: the two keys part company on the smallest possible
detail — when the press commits — and only one of them is right
for its cost.** Evidence: FIX commits on RELEASE
(`tryAwaitRelease()` gates `onFixClick`; HEAD commit `6237468`)
because its consequence is a trip to Settings; CHECK commits on
DOWN (`onCheckClick()` fires before `tryAwaitRelease()` in its
`onPress`) because its sweep is idempotent and costs nothing — the
code comment argues this explicitly, and the argument is sound as
far as it goes. What it misses: CHECK sits directly below FIX in a
vertically scrolling sheet; a scroll gesture that begins on CHECK
dispatches a sweep the user never asked for, and — the detail that
matters — re-bases `checkPressGen`, so if a deliberate CHECK was
already in flight, a stray scroll-touch silently replaces its
generation and its verdict now rides on the accidental sweep.
Harmless to the store, unfaithful to the press. Cure in the FIX
grammar: dispatch on release for CHECK too; the dip and knock stay
on DOWN (they cost nothing), the sweep joins FIX in costing a
completed press. The idempotence argument survives — it argues the
sweep is cheap, not that accidental dispatch is honest.

Price: S, ~6 lines moved inside the existing `onPress`, plus one
line in the T-5-style render test (press-and-cancel dispatches
zero sweeps). Second detail, acquitted: both keys carry
`maxLines = 1` and spoken content descriptions ("Fix {name}",
"Check guard state") — the accessibility dress on the new objects
is complete as built.

— Wave-2 seat: Interaction-detail specialist

---

## MUST FIXES (before this branch's build is requested)

1. **MUST-1 — Declaration order, ParkDrawer.kt (seat 15).**
   `fixDip` (line 263) reads `fixPressed`, declared at line 301.
   Move the `fixPressed` / `fixPressedItem` declarations above the
   dip animator block (~10 lines moved, zero behaviour change) so
   Ren's single build is not spent discovering it.
2. **MUST-2 — Kill the stray clone's GuardLine change ( §0 ).**
   Do not fold `agent/issue-9-builder`'s `GUARD_NOT_CHECKED` version
   into this branch, in whole or in part: identical string, worse
   (nullable) signature, reverses F-G4's totality. If the constant
   is wanted, author it fresh in the main cell per §0/Improver.

## REMAINDERS (ruled, priced, owned by the wider §4 pass — not this branch, not forgotten)

- §4.2 Warrant line on the board (S) — Deep-thinker seat: the
  honest bridge over C-6/C-7/C-8, which STAND and stay escalated.
- §4.3 Headline `GUARD {n}/4` + Linea's signed foot line (S, ~30 lines).
- §4.4 Age column per row (S) — the dress half (STALE half-ink)
  landed here; the ages did not.
- §4.6 FIX escort card + printed FIXED / STILL OPEN verdict +
  Healing Staircase + placement (M) — User / UX /
  Implementation-quality seats; build the verdict wait on CHECK's
  generation machinery, not the 800 ms delay.
- §4.7 Tier composition: eyebrows, consequence sentences, OFF vs
  NO DOOR, false citizens out of the tier block (M) — Designer seat.
- §4.8 LAST PROVED → foot colophon (S), with the SELF-REPORT
  glossary line §1.3 promised (Student seat, ~5 lines).
- §4.9 Plate cut to 4 lines (S) — COLOUR line and
  `DATA: ON THIS PHONE ONLY` still print (Consumer / Brutal critic).
- §4.10 Sound sled → Shield; Theme row → footnote / LOOK row (M) —
  Purpose-questioning seat.
- §4.11 Bottom padding 100 → 24 dp + spacing module {8,16,24}
  throughout (S) — 6/14/32 dp spacers still off-module.
- §4.12 PARK gate composes `doorLocked`; seal-time warning line
  (S) — Thoughtful seat; C-4's ruling carries an asterisk until
  this lands.
- W-08 dynamic plate (batch 10, Ren's): the PEEK line still prints
  a hard-coded 2 minutes; the input contract is prepared in the
  plan, the plate is not built (Consumer seat).
- New proposals filed by this run, unowned until Vogt sequences
  them: Two Witnesses cross-examination CHECK (Crazy-ideas, M);
  the Vigil — open-drawer self-proving (Max-impl, S);
  CHECK · {time} suffix after a whole landing (Enthusiastic, S);
  hairline-resolve on a whole landing (Wow, M); NOT CHECKED literal
  dedupe (Improver, S); shared 5 s tick for band + board
  (Technical execution, S); CHECK commits on release
  (Interaction-detail, S); CHECK verdict surviving a close
  (Intentionality — ruling first, S if built).

## DECISION-FOR-SARTHAK

- **Live-Fire Strip territory (parked, issue #21) — not built, not
  designed here, per the parked decision:** Crazy-ideas' second
  proposal (seat 1) — a long-press on CHECK that escalates from
  grant re-measurement to a witnessed pipeline rehearsal
  (sacrificial decoy the shield must block, self-posted
  notification the listener must catch). It touches the parked
  strip, so it is recorded here for Sarthak and nowhere else; the
  strip itself, and Vega's original §5 filing, remain exactly as
  parked in issue #21. Nothing in this file's other proposals
  depends on it: Two Witnesses and the Vigil were both scoped
  specifically to stay outside it.
- No other item in this run requires Sarthak: every other finding
  is a ruling for Vogt/Hof or a remainder already priced in
  AUDIT-park §4.

*— Wave-2 named-seats runner, issue #9, under Vogt. Twenty seats,
twenty filings, none silent. Paper only; Ren compiles, on
Sarthak's word.*
