# FRESH-1 — Fresh-eyes stranger test, cold-start cure (issue #7)

Seat: FRESH-1 (FRESH-EYES), BINDUNG's cold-start cure cell.
Repo: Sarthak-2007/Focus-app, branch `agent/issue-7-coldstart`, worktree
`~/workspace/clone-inspect/builds/coldstart/Focus-app`. READ-ONLY pass.

Method, as briefed: I read ONLY (1) the cured `refusalSentenceFor` table in
`MonolithComponentLogic.kt` as it stands on this branch, (2) the Deck sentence
slot + seam rendering in `TactileDeckScreen.kt` / `MonolithComponentLogic.kt`
(plus the slot/strip composables and the FIX handlers they call, because Q3
asks where FIX goes), and (3) `WatchStore.deriveWarrant`. I did NOT read this
cell's other reports or the issue #7 thread before writing §1–§3. §4 was added
afterwards, after reading BINDUNG's coordination comment, and says so.

One evidence note before the verdicts: the cured sentence table is present in
this worktree as an uncommitted modification of
`MonolithComponentLogic.kt` (313 lines; the committed HEAD version still has
the old fog row). Everything below judges the cured table in front of me —
`refusalSentenceFor(warrant, nowMono)` at
`MonolithComponentLogic.kt:242-271` — not the committed old one.

---

## 1. First impression — a stranger picks up this phone

### Where my eye actually goes

Not to the status prints. The Deck opens on the instrument: a recipe eyebrow
(`1 × 50`, or `2 × 50 + BREAKS`), a huge span/readout, the wheel, and the
pinned slide at the bottom. The wire's prints live in the pinned stack *under*
the scroll — a 53dp proof strip (`BLOCK / APPS / TIME`) and, below the slide,
a 22dp sentence slot (`RefusalSentenceSlot`,
`MonolithCustomComponents.kt:534,565`). Both are small print (strip words at
15sp, seam marks would be badge-size). So the honest first-glance answer:
**for the first second I learn what session I can start, not whether blocking
works.** The status system is a second, deliberate glance downwards. A broken
blocker and a healthy blocker look identical in the hero.

### What each wire state tells me in that second glance

Derivation under test — `WatchStore.deriveWarrant`,
`WatchStore.kt:426-438`: `!a11yBound → OFF`; `usage DENIED → OFF`;
`!stamped → UNPROVEN`; `usage != ALLOWED → UNPROVEN`; else `LIVE`.
On the Deck the always-visible wire word is the strip's BLOCK cell
(`TactileDeckScreen.kt:378-383`): `LIVE → "ON"`, `OFF → "OFF"`, anything
else (incl. null) → `"UNKNOWN"`. The sentence slot is the second print.

| State (as derived) | BLOCK cell | Sentence slot (cured table) | Do I believe it? |
|---|---|---|---|
| No warrant yet (null, before first read) | `UNKNOWN` | empty — `refusalSentenceFor(null) → null`, `MonolithComponentLogic.kt:243` | Half. `UNKNOWN` is honest; the empty slot says nothing at all. Loading? Broken? No way to tell. |
| LIVE, fresh (bound + stamped + usage ALLOWED) | `ON` | empty — falls to `else -> null`, `:269` | **Yes — the one clean pass.** `ON` is plain English, and the imprint underneath prints `{n} WILL SHUT · {m} PEEK` only in this state (`imprintTenantText`, `:145-148`), which is the most concrete promise on the screen: it names what will happen to how many apps. |
| OFF because accessibility not bound | `OFF` (FAULT) | `"BLOCKING IS OFF — TAP TO FIX"` → `FIX_ACCESSIBILITY`, `:246-247` | **Yes, completely.** Two prints agree, both in plain words, one names the act. Best row in the table. |
| OFF because usage DENIED | `OFF` (FAULT) | `"USAGE ACCESS IS OFF — TAP TO FIX"` → `FIX_USAGE`, `:248-249` (usage row outranks the OFF row) | Mostly. The sentence names a *different* thing than the cell: cell says blocking off, sentence says a permission called "USAGE ACCESS" is off. As a stranger I don't know usage access *is* part of blocking — but the fix it offers is specific, so I follow it. |
| Usage UNSET (derives UNPROVEN) | `UNKNOWN` | `"USAGE ACCESS IS OFF — TAP TO FIX"` → `FIX_USAGE`, `:250-251` | **Split verdict on screen.** The sentence states a fact (`IS OFF`); the cell beside it states ignorance (`UNKNOWN`). Both are defensible to an insider (cell = whole warrant, sentence = one grant), but a stranger sees the phone disagree with itself in one frame. |
| UNPROVEN, unstamped, usage ALLOWED, residency ≤ 4,000ms (the cold-start race, mid-cure) | `UNKNOWN` | empty — grace returns `null`, `:252-262` against `RESIDENCY_GRACE_MS = 4_000L`, `WatchStore.kt:186` | Acceptable *for four seconds*. `UNKNOWN` reads naturally as "still checking", and the self-probe (residency start + re-probe at +1,500ms, `WatchStore.kt:270-292`) is genuinely working behind it. This silence is earned in a way the old fog sentence never was. |
| Same state, past grace (or no residency carried) | `UNKNOWN` | `"BLOCKING IS OFF — TAP TO FIX"` → `FIX_ACCESSIBILITY`, `:263-265` | **No — this is the cured table's one unbelievable frame.** The sentence now declares `OFF` as fact while the BLOCK cell in the same frame still says `UNKNOWN`. If it is off, why doesn't the cell know? If it is unknown, why is the sentence certain? The code comment's logic — bound-but-blind is "off in fact" (`:256-258`) — is an insider inference (a bound service that never stamped can't be trusted to intercept). Nothing on the glass carries that inference to me. |
| Stale read (> 45,000ms, `STALE_AFTER_MS`, `WatchStore.kt:177`) | LIVE degrades to `UNKNOWN` (`printState`, `WatchStore.kt:477-480`); OFF stays `OFF` | empty — stale guard returns `null` before any row, `:244` | Quietly consistent. The sentence vanishing is invisible reasoning, but `UNKNOWN` carries the doubt on its own. I don't notice a problem here. |
| Session running | follows frozen verdict, degrades to `OFF`/`UNKNOWN` if the wire drops (`TactileDeckScreen.kt:534-548`) | forced empty — `isSessionRunning -> null`, `TactileDeckScreen.kt:1170-1175` | Out of cold-start scope, noted only because it is another meaning of the same empty slot (see §2). |

### "BIND HELD · UNSEEN" — what do I think it means?

First, the fact that reframes the question: **on the Deck, I never see it.**
`ProofStripBand` is called with `stampLeft = null, stampRight = null`
(`TactileDeckScreen.kt:1002-1003`), the band renders stamps only when both
are non-null (`MonolithCustomComponents.kt:498`), and `seamMarksFor` has
**zero callers** in `app/src/main`. The seam is dead print on this screen —
defined, tested, unshown. So as a Deck first impression it cannot pass or
fail; it isn't there. If it is meant to reassure on the Deck, the cure
didn't land it. If its real home is elsewhere (the Deck code comment at
`TactileDeckScreen.kt:998-1001` says the stamps' "receiving home is PARK"),
then this is a PARK question and should be stranger-tested there.

Judged as words alone, from the table at `MonolithComponentLogic.kt:62-85`:

- `"BIND HELD · UNSEEN"` (`:77`, UNPROVEN/unstamped; also `:82`,
  OFF/unstamped) — as a stranger: *held* sounds secure, *unseen* sounds
  unverified, and *BIND* is pure jargon (an Android service binding — I have
  never bound anything in my life). My honest parse is "something is held
  but hasn't been seen", which is either a contradiction or a threat. It
  does not tell me whether blocking works. Worse, in the OFF row the same
  left mark `"BIND HELD · UNSEEN"` pairs with `"PROOF: POSITIVE"` (`:82`) —
  held, unseen, *and* positive proof, while the state is OFF. Three insider
  terms, zero stranger meaning.
- `"BIND HELD · THIS VISIT"` (`:65`) vs `· UNSEEN` — the distinction is the
  residency stamp: did the pipeline process an event *this visit*. "THIS
  VISIT" I half-get (like a stamp in a passport), but I would never guess
  the unseen one means "the guard is running but hasn't proven it since I
  opened the app".
- `"PROOF: LIVE BIND"` / `"PROOF: BIND ONLY"` / `"PROOF: NONE"` /
  `"PROOF: POSITIVE"` / `"PROOF: STALE"` (`:65,75,73,80,103`) — a grading
  scale with no visible scale. "BIND ONLY" especially: only a bind? Is that
  good? Compared to what? A stranger cannot rank these five.
- The one seam mark that *would* land: `"BIND NOT HELD"` (`:80`) — plain,
  negative, believable. And `"BIND UNCONFIRMED"` (`:73`) is at least honest
  English, though that branch is production-unreachable by the function's
  own comment.

**Verdict on the seam:** insider posology. Every mark is precise about
*liveness mechanics* and mute about the only thing a stranger asks — *will
my apps be blocked?* The BLOCK cell (`ON / OFF / UNKNOWN`) already answers
that question in plain words; the seam answers a different, unasked one.

## 2. The silence question — is an empty slot "everything works"?

On a healthy phone: slot empty for a moment, then empty forever (LIVE prints
nothing, `:269`). Is that legible as health?

**Not by itself, and not on a first-ever open.** The problem is that the
identical 22dp emptiness carries at least five meanings in this design:

1. healthy LIVE — nothing wrong;
2. no warrant read yet — nothing *known* (`:243`);
3. the 4s grace — machine still proving itself (`:261-262`);
4. stale read — the phone has *stopped trusting* its last check (`:244`);
5. a session is running — the slot is suppressed whatever the wire does
   (`TactileDeckScreen.kt:1170-1175`).

One silence, five meanings, three of which are "we don't know yet". A
first-time user has no way to separate meaning 1 from meanings 2–4. My
first-open read of a reserved blank strip under the slide is not "all good" —
it is "nothing is on", or "this bit hasn't loaded". Reserved emptiness is a
convention you learn *after* you have once seen a fault sentence occupy the
slot and then leave; before that experience it is just a gap.

**What saves it — barely, and only in composition:** the slot is not the
health signal; the strip is. `BLOCK: ON` (PASS density) + the imprint's
`{n} WILL SHUT` underneath is a real, plain-language affirmative. A stranger
who finds the strip *can* conclude "blocking is on" without the slot saying
anything. So the system as a whole does communicate health — through a cell
the eye has to be taught to check, not through the slot.

**What I would need, WITHOUT adding clutter** (no standing "ALL GOOD"
sentence — that becomes wallpaper within a week and trains the eye to skip
the slot entirely, killing the fault sentences' power):

- *Nothing new standing.* The affirmative already exists; do not duplicate
  it in sentence form.
- If anything: make the one existing affirmative findable, not bigger text
  everywhere — the failure is eye-routing (§1), not wording. `BLOCK: ON`
  is doing the job; it sits in the least-looked-at band on the screen.
- Accept the learning curve honestly: silence-as-health works from the
  *second* fault onward. For the very first open, `BLOCK: UNKNOWN → ON`
  visibly changing in the strip is the onboarding — the cure's probe makes
  that transition happen in seconds on a healthy phone, which is exactly
  the moment a stranger learns "empty slot + ON = fine". That transition
  is the teacher. It only works if the user is looking at the strip, which
  circles back to eye-routing, not to the slot.

**Verdict:** silence is the right design for the slot and the wrong thing to
rely on as the health signal. It passes *only* because BLOCK/ON and
WILL SHUT exist beside it. Tested as "does empty = works, yes/no": **no for
a first-timer, yes for anyone who has seen one fault come and go.**

## 3. "BLOCKING IS OFF — TAP TO FIX" — clear? Does FIX go where I expect?

**The words: yes, the clearest sentence in the table.** Subject, state,
act — `BLOCKING IS OFF — TAP TO FIX`
(`MonolithComponentLogic.kt:246-247`). No jargon, no fog. The deleted
`"CAN'T CHECK BLOCKING — TAP TO RETRY"` appears nowhere in
`app/src/main` anymore; `RETRY_CHECK` survives only as an enum value
(`:178`) and a now-unreachable Deck handler branch
(`TactileDeckScreen.kt:1181-1191`). Good riddance, confirmed in code, not
just in comments.

**The tap target:** the sentence text itself is the button — the whole
`Text` gets `Modifier.clickable(role = Role.Button)` when the action is
non-null (`MonolithCustomComponents.kt:567-579`), inside a 22dp box
(`:565`). The words say TAP, so I tap the words; that works. But the
clickable area is the glyphs, not the full-width slot — a tap on the blank
slot beside the left-aligned sentence does nothing. A stranger's sloppy tap
has a real chance of missing and concluding the sentence is decorative.

**The destination:** `FIX_ACCESSIBILITY` → `onFixBlockingAccess`
(`TactileDeckScreen.kt:1179`) → `MainActivity.kt:679-684` launches
`Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)` through `launchSettings`
(`MainActivity.kt:486`), which falls back to generic Settings, then to a
plain-language toast, if the OEM throws. So:

- For the genuinely-unbound row: **right neighbourhood, not the doorstep.**
  I land on Android's Accessibility *list*. I must find this app in the
  list, open it, toggle it, and confirm Android's warning dialog. "TAP TO
  FIX" promises a one-tap fix; the reality is tap → hunt → tap → toggle →
  confirm. That is mostly Android's constraint, not this app's lie — there
  is no reliable intent straight to one service's page — but the sentence
  takes the credit for "FIX" while the work is still mine. I expected the
  switch; I got a directory.
- For the past-grace bound-but-blind row (`:263-265`) — **the sentence sends
  me to a pane that shows no lack.** The service *is* bound; Accessibility
  Settings will show it ON. I arrive, see the thing already on, and have no
  idea the intended act is an off/on re-toggle to force a rebind (the code
  comment at `:221-223` knows this; the glass never says it). This row
  reuses a true sentence for a state where its fix instructions silently
  change meaning. A stranger's verdict on the spot: "the app told me to fix
  something that isn't broken — the app is confused." That is exactly the
  trust failure the cold-start cure exists to kill, relocated from the
  sentence to the Settings pane.
- The usage twin is cleaner: `"USAGE ACCESS IS OFF — TAP TO FIX"` →
  `ACTION_USAGE_ACCESS_SETTINGS` (`MainActivity.kt:685-690`). Same
  list-hunt cost, but there the pane *does* show the lack it names, so the
  promise and the pane agree.

**Timing footnote:** the past-grace sentence can only appear when the Deck
recomposes — its clock is a 5-second ticker (`TactileDeckScreen.kt:369-376`).
Grace ends at 4s; worst case the sentence lands ~9s after open. Not a lie,
but "immediately after the grace" it is not.

**Verdict on Q3:** wording passes clean. Destination passes for unbound and
for usage. Destination *fails* for the bound-but-blind row — same words,
same pane, no visible problem to fix once I get there.


---

## 4. After BINDUNG's coordination comment (issue #7, comment 6515653955)

*This section was written after §1–§3, after reading BINDUNG's coordination
comment — the one permitted cross-read. Nothing above was edited.*

BINDUNG's explanation, in short: the fog was a **stamp race**, not a
settings problem — `lastRootMono` was stamped only on the accessibility
event path, a cold open's event burst races residency start, a quiet Deck
then produces no events, the stamp never lands, and the warrant rested
UNPROVEN with the fog as its resting sentence. The cure's shape: the bound
guard **self-probes** at residency start with the service's own
root-acquisition probe (the same proof the event stamp carries); **within
the 4s grace the slot stays silent** ("checking, no text"); **past the
beat, a bound-but-never-proving pipeline rests at
`BLOCKING IS OFF — TAP TO FIX`**; the fog string is deleted from the table.
Scope note in the same comment: the cell owns `WatchStore.kt`,
`MonolithAccessibilityService.kt`, and the sentence table in
`MonolithComponentLogic.kt` only; the Shield sentence is ZEUGE's and
untouched.

### Did the explanation change any verdict?

**One verdict strengthened, none reversed.**

- **Grace silence (§1, §2) — strengthened, stands.** I had guessed the
  silence was "earned" because a probe was working behind it. BINDUNG
  confirms the probe carries *the same proof* as the event stamp — not a
  weaker substitute check. The 4 seconds of `BLOCK: UNKNOWN` + empty slot
  are an honest picture of a machine mid-proof. Still a pass.
- **Fog deletion (§3) — confirmed, stands.** "It will exist nowhere in the
  app" matches what I found in `app/src/main`: the string is gone, only the
  dead `RETRY_CHECK` enum/handler remain.
- **Past-grace frame (§1) — NOT changed, and this is the point of the
  exercise.** BINDUNG's comment makes the reasoning fully clear *to me*:
  bound-but-never-proving is ruled OFF as a three-state truth law. But
  that explanation lives on an issue thread. On the glass, the frame is
  still `BLOCK: UNKNOWN` beside `BLOCKING IS OFF` — the certainty is
  asserted by the sentence and contradicted by the cell, and nothing a
  stranger can see carries the inference "bound but blind = off in fact".
  An explanation that only insiders read cannot cure a print that
  strangers read. Verdict stands.
- **Blind-row FIX destination (§3) — NOT changed.** The coordination
  comment does not address it: past the beat, the sentence sends the user
  to Accessibility Settings, where the bound service shows ON and the
  required act (toggle off/on to rebind) is printed nowhere. The cure
  relocates the fog from the sentence to the Settings pane for exactly
  the state it newly creates a resting sentence for.
- **Seam marks (§1) — corroborated by scope, stands.** BINDUNG's cure
  never claimed the seam; the Deck separately nulls the stamps
  (`TactileDeckScreen.kt:1002-1003`). My finding stands as filed: the
  `BIND HELD · UNSEEN` vocabulary is untested-by-presence on the Deck and
  untranslated for strangers anywhere it may land (PARK, per the Deck's
  own comment).

### Where the design still depends on knowledge only insiders have

1. **The stamp model itself.** Residency, stamp, proving, grace — the
   entire causal story is invisible. The user thinks in *app opens*; the
   warrant thinks in *residencies*. Consequence a stranger will actually
   meet: **every cold open replays `BLOCK: UNKNOWN` briefly** before the
   probe lands `ON`. An insider reads rigour ("proof is per-visit"); a
   stranger who glances early every morning reads an app that "always
   says unknown at first" — mild, daily, and never explained on the glass.
2. **Silence = checking.** BINDUNG's parenthetical "(checking, no text)"
   is an assigned meaning. On the glass, grace-silence, healthy-silence,
   stale-silence and not-yet-read-silence are the same 22dp of nothing
   (§2's five meanings). The assignment is learnable only by watching the
   BLOCK cell change — i.e., by already knowing where status lives.
3. **Bound ≠ working, and its corollary blind = off.** The two load-bearing
   inferences of the cured table. Neither is printed anywhere in words a
   non-builder holds.
4. **The rebind ritual.** Past-grace FIX works only if the user intuits
   off/on in a pane that reports no fault. Insider knowledge, full stop.
5. **UNSET = "IS OFF".** Insiders know UNSET is a lack-state with an
   owner, not a measurement; the stranger sees the sentence assert what
   the adjacent cell disclaims (`UNKNOWN`). The priority reorder that
   fixed the masking bug (usage rows above the unstamped row,
   `MonolithComponentLogic.kt:248-251` before `:252`) is correct logic
   that still produces a visibly self-disagreeing frame in this one case.

### Overall stranger verdict

**Not a clean pass — a pass with two named failures.** What passes from
fresh eyes: the fog is truly gone; the unbound row is perfect; LIVE reads
true through `BLOCK: ON` + `WILL SHUT`; the grace silence is honest and
short. What fails: (a) the past-grace frame disagrees with itself
(sentence certain, cell ignorant), and (b) that same state's FIX door
opens onto a pane showing nothing to fix. Both failures sit in the one
state the cure *creates* as a resting state — bound-but-blind — which is
why they matter more than their size: they are the cure's own signature
frame, and it is the one frame a stranger cannot read.

— FRESH-1 (FRESH-EYES), BINDUNG's cold-start cure cell, issue #7.
DECLARED — read-only pass, no code edited, nothing compiled.

## 5. Efficiency account (FRESH-1)

- **Artefacts:** this file (`studio/cells/issue-7-coldstart/fresh-1.md`) +
  a signed summary comment on issue #7. Nothing else created or edited.
- **Reads:** exactly the briefed surface — `SARTHAK-WORDS.md`,
  `MonolithComponentLogic.kt` (full), `WatchStore.kt` (derivation +
  residency machinery), the Deck's strip/slot derivations and pinned stack
  in `TactileDeckScreen.kt`, the slot/strip composables in
  `MonolithCustomComponents.kt`, the FIX handlers in `MainActivity.kt`,
  and — only after §1–§3 were written — BINDUNG's coordination comment
  (issue #7 comment 6515653955). I did not read other cell reports.
- **Idling/waste, admitted:** (1) My first read of
  `MonolithComponentLogic.kt` caught the pre-cure table — the cured table
  landed in this worktree mid-pass (uncommitted, 313-line version); I
  re-read the file in full and re-based every line citation on the cured
  version, at the cost of one duplicated read. (2) `gh issue view`
  GraphQL timed out once (~3.5 min) before the REST comments call worked;
  that wait is the session's largest single cost and produced nothing the
  REST call didn't. (3) One full read of `WatchStore.kt` where the
  derivation + grace constants would have sufficed — the residency-probe
  detail did end up cited in §1, so the waste is partial, not total.
- **Not done, deliberately:** no compiling, no test runs (read-only seat);
  the cured table's tests (`ColdStartWarrantTest.kt`) were located but
  not audited — that is the table-test seat's evidence, not fresh eyes'.
