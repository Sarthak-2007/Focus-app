# AUDIT — THE SHIELD, INTENTIONALITY PASS · Anchor seat I-3

Auditor: Intentionality Designer (Anchor seat I-3, issue #7).
Filed against `agent/issue-7` @ 096c413 (post wave-2): `ShieldMatrixScreen.kt`
(1,112 lines), `ShieldCapSelector.kt`, `ShieldBlockingStatus.kt`,
`AppRules.kt`, `BlockitSuitcaseAndSwitchesViews.kt` (the sled).
Read against Vogt's `AUDIT-shield.md` (R-1…R-13) and the cell records
in `studio/cells/issue-7/`. Paper only; nobody compiled.

## THE QUESTION THIS PASS ASKS

Vogt's audit asked two questions: *is it true?* and *is it printed
twice?* This pass asks the prior question, of every element on the
surface, one by one:

**Why does this exist — and would anything be lost if it didn't?**

An element earns its place only if it answers with a job no other
element already does, in the moment the man is standing on this
screen, at the hour he actually uses it (1 AM, one thumb, half
asleep). "It was in the render he picked", "the previous screen
had it", "the audit didn't strike it" are not reasons. Three
verdicts: **KEEP** (reason stated), **REMOVE** (no reason, or the
reason is already another element's job), **REDESIGN** (a real job,
wearing the wrong form). Standing law applied throughout: words
only where the object can't speak; Sora / Atkinson Hyperlegible /
Anonymous Pro only; never green; no AI-generated images.

## HEADLINE FLAGS (also posted on issue #7 as they landed)

**I-1 · The cap's stop ticks — REMOVE.** R-4(d) already killed
these: "position is already the channel; the ticks duplicate it."
They are still drawn (`ShieldCapSelector.kt`, "Stop ticks printed
under the track", 3 boxes, 2×8dp) and the file's own header still
lists ticks among the chosen object's parts. Position says which
stop; the word in the cap says which stop; the ticks say it a
third time. Worse than redundant: an element that survives its
own recorded death sentence teaches every future builder that
rulings are decorative. Strike the ticks and the header line.

**I-2 · The cap's marker line — REMOVE.** Inside the travelling
cap, under the selected word, sits a 10×2dp line in hardcoded
`Color(0xFFFBF1EC)`. Its job is never stated anywhere in the code
or the cell records — it appears to be "the settled loadout marker
line" by family resemblance to the sleds. But the cap *is* the
marker: its position and its filled word already print the
selection, twice. A third print of the same fact, in a hardcoded
colour of exactly the kind R-7 tokenised out of the ratchet banner
for being unprovable in the day theme, has no reason to exist.

**I-3 · The ±30 chips — REMOVE (with the wheels, not after them).**
R-8 ruled them dead and delegated the cure to Voss's time wheels
(#15 lane). Until that merge, four chips (START −30/+30, END
−30/+30 in every expanded routine row) remain the only time
editor on this screen — and they cannot make 20:40 from 20:00 in
any number of taps. Their continued presence is not a stopgap, it
is a false affordance: the screen's only offered verb for "change
the time" cannot reach most times. When the wheels land, the
chips must die in the same commit. The known failure mode —
wheels arrive, chips stay as a "quick adjust" — is duplication
by nostalgia and is pre-struck here.

**I-4 · The cap-source line — REDESIGN (by dying).** Under the
cap, Anonymous Pro 11sp: "THIS CAP SHOWS YOUR LAST HAND PICK", or,
while a routine session runs, "A ROUTINE SESSION IS RUNNING — ITS
LOADOUT GOVERNS, NOT THIS CAP". This line's entire job is to
confess that the element above it does not show what it appears
to show (R-4b, half-cured in wave-2). A permanent disclaimer is
not a cure; it is the defect, documented in place. And the second
form exposes the worse half: while a routine governs, the cap
stays live — one tap rewrites every app's tier by category
underneath a session whose loadout it does not govern, with no
preview, no confirm, no undo (Ghost Preview, R-4a, still
unlanded). Cure, in order of preference: (a) the cap derives its
position from the currently applied loadout and its source, as
R-4(b) originally ruled — then no line is needed; (b) under a
running routine session the cap is inert and prints that state
in its own body. Either way the footnote dies. Until then it is
the most honest line on the screen, which is the indictment.

**I-5 · `groupedByRule()` / `AppRules` — REMOVE.** R-1 retired
the Standing Orders slip's per-app lines. The slip's grouping
derivation survives in `AppRules.kt`, its doc comment still
calling it "the derivation under the Shield standing-orders
slip" — a surface that no longer exists. Production Shield
enumerates via `groupedForCatalogue()`; on this branch only
tests (`AppCatalogReconcileTest`, `AppListTruthTest`) still call
`groupedByRule()`. This is the duplicated Standing Orders material
in its last hiding place: not on the screen, but in the data
layer, keeping a second enumeration of the same decisions alive
for a dead surface. Delete the type and the function; re-point
the two tests at `groupedForCatalogue()` (its PEEK/SHUT halves
are the same lists).

## ELEMENT BY ELEMENT — top to bottom, as the man meets them

### 0 · The status sentence (item 0)
- **The sentence itself — KEEP.** The only element on the screen
  whose job is the screen's founding question: *is the wall
  standing right now.* Witness-proved (shield protecting-flip for
  this session id + accessibility armed for this session id +
  the 10s beat law), never derived from `isSessionActive` alone.
  It earns its place completely; everything below it is qualified
  by it.
- **The ●/○ solidity glyph — REDESIGN (minor).** One glyph, two
  forms, four proof states: LIVE gets ●; NO_SESSION, UNPROVEN and
  STALE all get ○. But "no session running" (calm, expected at
  noon) and "a session is running and enforcement is unproven"
  (the wall is *down while claiming to stand*) are opposite
  situations wearing the same mark. At a glance — the glance this
  screen is judged by — they are indistinguishable; only reading
  the words separates them. Panzer's filed form already answers
  this (55% glyph for unproven/stale). Land it, or record why the
  verb alone is allowed to carry a distinction the glyph refuses.
- **The fixed 40dp slot + whole-line 260ms re-strike — KEEP.**
  Reason: a state change must never shove the screen under his
  thumb, and a verdict must never half-print. Governed manners,
  same as the tally's.
- **The fallback headline ("ENFORCEMENT NOT PROVEN — NO WITNESS
  READ YET") — KEEP.** It exists for the cold-start instant and
  render harnesses, and it refuses to guess in either direction.
  The rare default that is a truth, not an accident.

### 0.5 · The ratchet banner
- **REDESIGN — still owed by R-7.** Wave-2 tokenised its colour
  and reworded it ("CANNOT WEAKEN 'X' DURING THIS SESSION"), but
  it still prints at item 1 of the column — the top of the screen —
  when the refusal happened at a sled three viewports down, where
  the man is standing and sees nothing happen (Muede T1, Fremd
  C12; R-7 ruled refusal *at the point of refusal*). As an
  intentionality matter: a global banner for a local event is an
  element in the wrong place, kept because the column had a slot
  there. It also sits in the LazyColumn as an item even when
  invisible (an `AnimatedVisibility` around an empty box), taxing
  the arrangement gap on every ordinary day. Keep it for
  Deck-origin refusals only; the Shield-origin refusal prints on
  the row that refused, per R-7. Until then, flagged, not acquitted.

### 1 · The loadout cap
- **The cap object (STUDY / WORK / SLEEP) — REDESIGN.** Its stated
  reason is 1-tap bulk ruling. Interrogate it: the catalogue sleds
  rule app-by-app; routines apply loadouts on a schedule; the cap
  is a third hand on the same facts, and the heaviest one — one
  tap rewrites *every* app's tier by category
  (`applySmartLoadout`), silently flipping side-effects (STUDY
  also arms the Shorts sniper + browser blocker per the audit's
  C4d), with no preview, no confirm, no undo, and no printed
  consequence anywhere on this surface: the only text describing
  what STUDY/WORK/SLEEP *do* lives in `SmartLoadout`'s
  title/subtitle/badge fields in `MonolithModels.kt`, which this
  screen never renders. A destructive bulk verb that explains
  itself nowhere does not earn a selector's quiet clothes. It
  survives this audit on one legitimate job — recovering a
  hand-scrambled rulebook in one gesture — and only with R-4(a)'s
  Ghost Preview landed (first tap previews the resulting tally +
  names the side-effects, second tap commits) or an equivalent
  commit step. Without that, my verdict would be REMOVE.
- **Stop ticks — REMOVE.** See I-1.
- **Marker line inside the cap — REMOVE.** See I-2.
- **Unselected words on the track — KEEP.** They are the cap's
  affordance: the two destinations print before you travel. The
  object cannot speak its alternatives without them.
- **The cap's click sound (`playFirmClick`) — KEEP, conditionally.**
  A firm click for a bulk rewrite is wildly underweight *if* the
  tap commits; it becomes honest the moment Ghost Preview makes
  the first tap a question instead of a deed. Ties to the cap's
  REDESIGN above; KLANG/TAST's filed sound defects govern the rest.
- **The cap-source line — REDESIGN (by dying).** See I-4.
- **The 84dp slot — KEEP.** It is the RombiKnob's old slot, kept so
  everything below starts at the same line; a deliberate fit, not
  an accident. (Its ~30dp of internal air is the ticks' home —
  striking I-1 lets the slot shrink or the track breathe; builder's
  call, priced in the finish pass.)

### 2 · Routines
- **The header "ROUTINES · n ARMED" — KEEP.** The count is the
  scheduler's own count (enabled AND ≥1 day), and ARMED is the
  honest word — armed to fire, not firing. One home for the
  aggregate; the rows below are controls, not counts. No
  duplication.
- **The reset note ("YOUR DAY, SEEDED — …") — KEEP, time-boxed.**
  A one-time fact (his day was reseeded; his own times are never
  rewritten) that must be said exactly once, in print, because
  the reseed is invisible otherwise. Its reason expires: R-6 ruled
  its residency dies on first edit or 48h. Verify the residency is
  actually implemented at the call site — on this screen it is a
  caller-supplied boolean, and a "one-time" note with no death
  wired is a permanent banner in waiting.
- **The collapsed row: time column (start over →end) — KEEP.**
  The day reads as one column of windows; start-over-end on one
  axis is how a schedule is scanned at 1 AM. The arrow on the end
  line is the one piece of punctuation that earns itself — it
  says "until" without a word.
- **Name (Atkinson Bold 15sp) — KEEP.** Sora was evicted from
  these rows in the finish pass; one name grammar for working
  surfaces is the law, and this obeys it.
- **Meta line (days · loadout · RUNNING NOW) — KEEP, one charge.**
  As the collapsed summary of what the expanded editor edits, it
  is not duplication — it is the row's answer before the question
  is asked. Charge: it is `maxLines = 1`, and RUNNING NOW — the
  only session-truth on the row — rides at its tail, first in
  line for the ellipsis on a long name/day string (Panzer filed
  the same). A marker that can be truncated away has a reason
  that fails exactly when the line is full. Give RUNNING NOW its
  own carrier or shorten from the days end, never from the tail.
- **The ON / OFF / WON'T FIRE cap — KEEP.** It prints the row's
  armed truth, including the WON'T FIRE state a bare ON would lie
  about, in the middle density so the clothes match the claim.
  Payload-sized. This is what an intentional control looks like.
- **Row tap-to-expand with no visible door — REDESIGN (minor).**
  Nothing on the collapsed row says it opens: no chevron, no
  word, no affordance. The reset note says "TAP A ROW TO OPEN
  IT" — but only while the note lives; after it dies, the editor
  is hidden knowledge. The object cannot speak here, so one word
  or mark must. Cheapest honest form: the existing house door
  grammar (the tally jumps' ink + bar treatment) on the row, or
  accept the cap as the only visible verb and print EDIT once.
  Builder's choice; "he'll learn it" is not a reason.
- **±30 chips (×4 per expanded row) — REMOVE.** See I-3.
- **The loadout-cycle chip — REDESIGN.** In the expanded row, a
  primary-ink chip prints the routine's loadout (STUDY/WORK/
  SLEEP) and tapping it *cycles* to the next one. It wears a
  label's clothes and does a button's work: nothing says it
  cycles, nothing shows what the next state is, and one slip
  overshoots into the wrong loadout with no print of the change's
  consequence. The per-routine loadout itself earns its place
  (it is not the cap's duplication — a routine's loadout is what
  fires at 20:00 while he sleeps; the cap is his hand). The form
  must confess the verb: cycle arrows, a "NEXT: WORK" print, or
  the cap grammar in miniature. As-is, it is a hidden verb.
- **Day chips M T W T F S S — REDESIGN.** Seven single letters;
  T appears twice, S appears twice. Position carries the meaning
  only if he counts from the left — at 1 AM, on the third routine,
  that is a coin flip between Tuesday and Thursday. The mechanism
  (tap a day, toggle it) earns its place; the labels don't. Print
  two-letter days (MO TU WE TH FR SA SU) or give each chip a real
  content description at minimum (FASSUNG's semantics pack) — and
  note the chips' `isPrimaryInk` state grammar is the third
  on/off dialect on this screen (sled, cap, chip) — tolerated
  because each is its object's native grammar, recorded so the
  next dialect needs a reason.
- **Expanded body as a whole — KEEP (minus chips above).** It
  exists only when summoned, costs nothing collapsed, and every
  control in it maps to one field of the routine. The indent +
  hairline the finish pass specced should land with it.

### 3 · Your Rules — the tally
- **"YOUR RULES" header — KEEP.** It heads a three-number line
  that would otherwise float ownerless between ROUTINES and the
  websites. Its job is naming whose decisions the tally counts —
  and it is the one header on this screen printed in full ink
  while the others sit in the quiet tier. That exception is
  deliberate (Liv) and defensible: his decisions outrank the
  furniture. It stays because the exception has a stated reason.
- **The tally line (counts as jumps) — KEEP.** Three numbers that
  sum to the phone; the words are doors to the catalogue groups.
  This is the slip's legitimate heir: everything the slip *said*
  that was worth keeping, none of what it duplicated. The 250ms
  coalesce + whole-line re-strike is governed print law, kept.
- **The folded tally under search ("n SHUT · n PEEK" in the
  header row) — KEEP.** Under search the tally folds rather than
  vanishes, and deliberately drops OPEN (the search state is
  about findings, not the book's balance) while never letting
  search *filter* the counts. An asymmetry with a written reason
  in the code. Acquitted.
- **The prune note — KEEP.** One line, directly above the
  catalogue it describes, announcing what the installed-only
  reconcile removed; tap dismisses, first row tap dismisses.
  A change to his rulebook that happened off-screen must print
  on-screen once. Reason intact; same residency check as the
  reset note applies.

### 4 · Blocked Websites
- **The section's existence above ALL APPS — KEEP.** R-3 moved it
  from ~6,100dp deep; an editor nobody reaches does not exist.
  Websites are rulings of the same kind as app tiers — same wall,
  same screen is a reason, not an accident of the Guard screen's
  dissolution (the code records it as a MOVE, the Guard original
  deleted — no duplication survives).
- **Header count "n SITES" — KEEP.** The list's size, one home
  (the tally counts apps, never sites). No second print of it.
- **Domain rows + REMOVE chip — KEEP.** Each row is one ruling
  and its revoke, the sled-row pattern in text form.
- **SHOW ALL / SHOW FEWER chip — KEEP.** Exists only past five
  rows; the cap keeps the catalogue's rent at ~300dp worst case.
  A control that appears only when needed is the model citizen
  of this audit.
- **The add field — KEEP; its placeholder — REDESIGN (words).**
  "Add a site — e.g. news.ycombinator.com" is sentence-case prose
  in a screen whose other field prints "SEARCH APPS" in the house
  field grammar. One screen, one voice for empty fields. Print
  the house form (e.g. "ADD A SITE — DOMAIN ONLY"), and drop the
  worked example or keep it — but decide; an example domain is a
  suggestion wearing a placeholder's clothes.
- **The error line — KEEP.** A rejected ADD prints why and keeps
  the text (R-3/Fremd C9b). The screen judging with the
  repository's own normaliser is the truth pattern done right.
- **The section's missing contract line — REDESIGN (add one).**
  PEEK got a contract line ("2 MINUTES, THEN SHUT AGAIN · 20 MIN
  BETWEEN PEEKS") because its name alone didn't state its terms.
  Websites have the same gap and never got the line: nothing on
  this screen says *when* a listed site is blocked — always, or
  only while a session stands (the status sentence's world).
  A list of blocked sites under a headline that may read NOT
  BLOCKING invites the same misread R-5 was built to kill. One
  derived line, or an explicit ruling that the status sentence
  governs this section too and a pointer to it. Silence is not
  a position.

### 5 · Search & the category filter
- **ALL APPS header + conditional "SHOWING n" — KEEP.** The count
  prints only when the catalogue is actually filtered — a number
  with a condition on its existence. Model behaviour.
- **The search field ("SEARCH APPS") — KEEP.** It finds by name
  what grouping by ruling and filtering by category both miss:
  the app he can name but not place. Raised and visible in
  wave-2's order. Earns it.
- **Category filter words — KEEP, with the standing naming charge.**
  Their reason is real: finding by *kind* when the name is
  unknown, orthogonal to the ruling groups (a filter is not a
  second grouping — it narrows, it doesn't re-enumerate). But
  Muede T7 stands unclosed: the taxonomy's words diverge from
  their names — the category keyed SHOPPING prints "OTHER APPS"
  (its `code` is "Tools"; three names for one bucket in the data
  layer alone). A filter word that doesn't name its bucket makes
  the man guess what he's filtering to. Close T7 in the wording
  pass; the strip itself stays.
- **Filter words' form (ink + 2dp bar, no pills) — KEEP.** State
  by position and weight; the same door grammar the tally jumps
  and (proposed) routine rows use. One grammar for "this word is
  a door" across the screen is intentional; keep it that way.

### 6 · The catalogue
- **Group headers (PEEK / SHUT / OPEN, word only, no counts) —
  KEEP.** The counts' refusal to print here is the R-1 discipline
  working: one home per number (the tally). The header's job is
  purely to name the ruling the rows beneath share.
- **The PEEK contract line — KEEP.** Prints once, in the one
  group it describes, derived from the constants so it can never
  go stale. The template every other contract on this screen
  should follow (see websites, §4).
- **The missing glyph legend — REDESIGN (add, or strike the
  glyphs).** R-10's settlement of name-is-not-a-door promised "a
  one-line glyph legend at the catalogue head (solid = SHUT,
  hollow = PEEK, none = OPEN)". It is not in the code. So every
  sled row currently speaks a three-symbol language — solid
  brick, hollow brick, *absence* — with no key printed anywhere
  on the screen, and the third symbol is an absence, the one
  symbol no one can look up. Either print the legend (one line,
  at the catalogue head, where R-10 put it) or admit the glyphs
  are decoration for initiates and strike them. A code without
  a key is not a language; it's a habit.
- **The sled row entire — KEEP.** Margin glyph (subject to the
  legend above), name-weight grammar (decided = bold ink, OPEN =
  quiet), the 3-stop sled printing its stop words SHUT/PEEK/OPEN
  in the track, cap colour following the rocker mid-flight so the
  word under it never lies. Acquitted by Dens and Panzer before
  me; my pass finds each part answering to a job. The row is the
  screen's unit of decision — read, made and revoked in one
  place, exactly once per app. This is the standard the rest of
  the screen is measured against.
- **The no-match line ("NO APP BY THAT NAME ON THIS PHONE") —
  KEEP.** An absence printed as a fact instead of a void. Cheap,
  honest, earns it.
- **Bottom clearance (100dp) — KEEP.** It exists so the last row
  is never trapped under the floating rail. Invisible when it
  works, which is the point.

### Cross-cutting
- **`effectiveNight = isDarkTheme || isSessionActive` — REDESIGN.**
  The sleds dress for night whenever the session *flag* says a
  session is active — the same flag the status sentence, two
  items above, refuses to trust as proof of anything. The screen
  thus *feels* like a standing wall on evidence its own headline
  calls unproven. LUFT A-1 already ruled the cure (session-night
  keyed on proven LIVE only); it sits in the unlanded follow-up.
  Until it lands, the atmosphere contradicts the sentence, and
  atmosphere is what he reads first.
- **Fonts — PASS, with one residue.** Sora survives nowhere on
  this surface after the routine-row eviction; Atkinson works the
  names and headers, Anonymous Pro the numbers, times and the
  status line. The status sentence printing *words* in Anonymous
  Pro (Panzer/FORM wanted Atkinson words + mono numerals, the
  tally's construction) is a recorded disagreement, not an
  accident — flagged here so it is decided once, in the open,
  rather than by whichever hand touches the file next.
- **Colour — PASS.** No green anywhere on the surface; the accent
  is unspent by the cap (REGISTER: budget 0) and the one hardcoded
  colour found is I-2's marker line, struck above.

## SCORE

Elements audited: 41. **KEEP 26 · REMOVE 5 · REDESIGN 10**
(of the redesigns, three are one-line or one-mark fixes; the
cap's is the only structural one, and it is R-4's own unfinished
business, not a new invention of this pass).

The pattern, plainly: nothing on this screen is junk, and almost
nothing is accidental *in origin* — every element was placed by a
named hand for a stated reason. The accidents are all of one
kind: **reasons that expired and were never re-asked** — ticks
outliving their acquittal-turned-execution, chips outliving the
lattice they impose, a grouping function outliving its surface,
a disclaimer outliving the defect it confesses, a legend that
was promised and never born. Intentionality is not a property
an element has once; it is a rent it keeps paying. This file is
the rent book. The five removals and the cap cure are priced in
hours, not days — Vogt's finish pass can carry them.

— Intentionality Designer (Anchor seat I-3, issue #7), Oct 30 2026
