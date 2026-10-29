# SUKI — Wild, space · PHASE 3 (GO-BEYOND, DECK)
## The tenancy law of the Truth Line, and the form of its silence

*Seat: Suki (Wild, space). Owners of the other halves: Emil (the slot is
his print object — this file writes its tenancy, not its typesetting) and
Rosa (her strip watches the slide; §4 draws the border between her watch
and my sentence). The Wall Card is the airlock's and is not in this file.
Guard's fate is Sarthak's open pick and is not in this file either: the
tap targets in §3 point at class-specific repair, which exists under
both Guard outcomes, and nothing here spends his pick.*

---

## 1. The beauty case, in plain words

A healthy Deck is a quiet room. At the top, in the nameplate, one line
of print says what the machine is doing — Emil's word, `IDLE`, set small
and calm, the way a well-made instrument carries its maker's mark. You
have seen that line two hundred times. You never read it. That is the
point: it costs a healthy morning nothing.

The art of this proposal is that a broken morning looks exactly like a
healthy one, except for the words.

The line does not move. It does not grow, redden, flash, or climb out of
the chrome onto the Deck. The wheel does not drop down the screen to
make room for a warning banner. The slide does not slide away. The
sentence that replaces `IDLE` sits in the same box, on the same
baseline, at the same size, in a heavier tier of the same ink — and it
says the one true thing, in words a man who has been awake for ninety
seconds can act on without thinking: `BLOCKING IS OFF — TAP TO FIX`.
And the slide under his thumb is dead, politely dead, in recess tone
with its orange taken away, because a live-looking slide over dead
blocking would be the screen lying with its body while its mouth told
the truth.

That is the whole aesthetic claim: **failure does not redesign the
screen.** The screen he memorised in health is the screen that tells
him the truth in failure. Nothing moves; only the words change, and
only one line of them. Trust is not a message the app sends on the bad
morning. Trust is the geometry not flinching.

And when it is fixed, the app does not congratulate itself. There is no
`BLOCKING IS ON`, no green-ish sigh of relief held on screen while he
is already reaching for the slide. The line simply hands the box back
to Emil's word, the slide takes its orange back, and the morning
resumes as if the app is slightly embarrassed it ever doubted. A
restored line that lingers is a small lie — it claims a state ("newly
fixed!") that stopped being news the instant it became true. The repair
is confirmed in his hand, not in a headline: the tick under his thumb
when the slide comes back to life is the receipt. Print keeps the
present tense. The hand keeps the news.

---

## 2. The object: one slot, two tenants, one box

**The slot** is slot 1 of Emil's Imprint Line — the state slot — treated
from this pass as **one typeset object with two lawful tenants:**

- **In health, Emil tenants it:** the session state word — `IDLE` /
  `SEALED` / `PAUSED`.
- **In failure, the Truth Line tenants it:** one sentence, the first
  failing check in blocking order, and nothing else.

The two tenants never co-print, never stack, and never split the box.
The slot's box, baseline, and type size are identical under both
tenants (§5 gives the numbers). The tenancy is the only change.

**What my wave-1 seat file got wrong, corrected here:** I set the
Truth Line at Anonymous Pro 10sp in chrome and failure labels at 13sp
in the slide track. Both die. 13sp is an unlegislated tier, and 10sp
plate is *furniture* — the ramp's own law (Set's table) is that the
moment a word carries a decision or a state it is promoted to body.
A failure sentence is the most decision-carrying words on the screen.
**The Truth Line is set as a state word, because that is what it is:
Atkinson Hyperlegible 700, 15sp, +0.5 tracking, line height 20dp** —
the locked state-word spec, no exception, no larger. §3.3 argues why
truth does not need 22sp and must not take it.

**Ink:** night — Emil's healthy word sits in the secondary tier
`#9C8E84`; a tenanted failure sentence sits one tier stronger, primary
`#D2C6BB`. Day — the same one-tier step in the day ink staircase. The
failure is *heavier, not redder* (my wave-1 rule, kept). No colour
carries the fact; a health fact is never carried by colour, and there
is no green and no amber anywhere in this file. Accent (COMMIT orange)
appears nowhere in the slot in either tenancy — and, §3.4, it is
*withdrawn* from the slide cap while the Truth Line tenants, because a
commit fill on a slide that cannot commit is a lie in the accent's own
language.

**Eviction:** while the Truth Line tenants, the imprint's other two
slots (next-routine, shut count) **suspend — they print nothing.** Not
demoted, not shrunk: absent. Three reasons. (1) Width: a 15sp sentence
needs the full content width (§3.2's fit table is measured against the
whole line, not a third of it). (2) Rank: at the failure moment the
next routine's name is trivia; printing it beside the sentence teaches
the eye the two are peers. (3) Truth-once: the shut count is a
blocking fact, and while blocking health is being stated as a sentence
the count is a second, weaker rendering of the same neighbourhood.
Both slots return, unchanged, at hand-back (§6). Their absence during
tenancy is also the sentence's frame: a line that is suddenly *alone*
on the nameplate reads as important before a single word is parsed.

---

## 3. The tenancy law — when the sentence takes the slot

### 3.1 The measured fact (unchanged from my seat file)

Four checks, each a live OS query, none inferred, results no older than
**5,000ms** at the moment they are rendered:

1. **Accessibility + shield alive** — AccessibilityService connected
   *and* Shield foreground service running, both reporting now.
2. **Usage access held** — `GET_USAGE_STATS == MODE_ALLOWED`.
3. **Overlay held** — `canDrawOverlays() == true`.
4. **List real** — enforced list count > 0 *and* the service's in-memory
   set equals the persisted set (count + hash, compared at service
   bind).

One pipeline: these queries are the *same* sweep Rosa's bulb-check
strip reads during the slide (§4). Two pipelines for one fact is how
the shipped app once printed totals that disagreed; the sweep is
shared code, shared cache, shared timestamps, or this file is void.

### 3.2 Failure classes and their exact words

Priority is **blocking order** — causal order. If accessibility is off,
the shield being down is its consequence, not a second fact; the
sentence names the cause that, fixed, re-tests the rest. Only the
first failure prints. The others are never counted on the Deck (my
wave-1 `NOT READY · 2 OF 4 CHECKS MISSING` is struck — a count of
failures is a score, and the repair sheet behind the tap lists them
all; the Deck prints the head of the queue). Fix the printed one, the
next — if any — replaces it at hand-back's re-sweep (§6). Cause is
never fabricated: classes are distinguished by what was *measured*,
and the class that cannot be measured says so.

| # | Class (measured state) | Exact sentence (verbatim, caps as set) | Fit at 15sp Atkinson 700 +0.5 |
|---|---|---|---|
| A | **Accessibility off** (check 1a fails: service not connected) | `BLOCKING IS OFF — TAP TO FIX` | 28 chars ≈ 241dp ✓ |
| B | **Shield service down** (accessibility connected; check 1b fails: foreground service not running / not bound) | `BLOCKING STOPPED — TAP TO RESTART` | 33 chars ≈ 284dp ✓ |
| C | **Permission dead — usage** (check 2 fails) | `USAGE ACCESS IS OFF — TAP TO FIX` | 32 chars ≈ 275dp ✓ |
| D | **Permission dead — overlay** (check 3 fails) | `OVERLAY IS OFF — TAP TO FIX` | 27 chars ≈ 232dp ✓ |
| E | **List empty** (check 4a fails: count = 0) | `BLOCK LIST IS EMPTY — TAP TO ADD` | 32 chars ≈ 275dp ✓ |
| F | **List out of sync** (check 4b fails: hash mismatch) | `BLOCK LIST OUT OF SYNC — TAP TO FIX` | 35 chars ≈ 301dp ✓ |
| G | **Unknown** (any query throws, or sweep exceeds the 1,500ms timeout) | `CAN'T CHECK BLOCKING — TAP TO RETRY` | 35 chars ≈ 301dp ✓ |

Fit measured against the full content width — screen width minus the
20dp margins (320dp at a 360dp reference width; the longest strings
hold a ≥19dp reserve; on narrower devices the tracking, never the
size, yields first: +0.5 → 0.0 buys ≈17dp before any string may wrap,
and no string may ever wrap — a two-line truth line is a paragraph,
and paragraphs are repair-sheet furniture).

Notes on the words:

- **Class A keeps HPA-03's settled string character-for-character.**
  It was set by the Humans pass and drawn in Ruth's 04:58 frame; this
  file does not re-word a settled sentence to feel authored.
- **Class B is not worded as A on purpose.** "Off" names a switch he
  can flip in Settings; "stopped" names a process that died under a
  switch still on. The tap targets differ (B tries an in-app rebind
  first, §3.5), and a sentence that sends him to Settings to fix a
  service the app can restart itself has wasted his weakest minutes
  and lied about the cause by implication.
- **Class G is a failure, not a shrug.** Unknown tenants the slot with
  the same weight, the same slide inertness, the same everything. The
  app that cannot check does not get to print Emil's `IDLE` and hope.
- No class ever prints a count, a percentage, a check name in code
  (`GET_USAGE_STATS` is a source, never a string), or the word
  "permission" as a generic — each sentence names the specific dead
  thing in his language: blocking, usage access, overlay, block list.

### 3.3 The size argument — the size the slot allows *is* the size truth needs

The challenge in the brief: set it at the size the slot allows, or
argue it needs more. It needs exactly the slot's lawful size, and the
argument is that **this sentence's importance is carried by four things
that are not size:**

1. **Eviction** (§2) — it is suddenly the only line on the nameplate.
2. **Tier** (§2) — one ink tier stronger than every healthy word there.
3. **The dead slide** (§3.4) — the frame's only verb, inert, de-oranged.
   His thumb learns the fact even if his eyes skip the line.
4. **Sole tenancy of the tap** (§3.5) — the whole line is the repair
   door; nothing else on the chrome competes for the press.

Against 22sp (title tier): a 22sp sentence breaks the slot's box,
forces the geometry change §5 exists to forbid, and — worse — it
*shouts in the type system*. Title tier is for names of things, not
for states; a state screamed at title size reads as an alarm, and an
alarm is a feeling, and feelings at 04:58 are how a man puts the phone
down and goes back to the cushion. 15sp at full width, alone, in the
stronger tier, in the slot his eye already knows, is a foreman's
voice, not a siren. Truth does not need to be bigger. It needs to be
*the only thing being said*.

### 3.4 What tenancy does to the rest of the Deck

Exactly two other changes, both already settled by HPA-03, restated
here as tenancy effects so the merge has them in one place:

- **The slide is inert.** No travel (0dp — my wave-1 resistance drag,
  40% at 1:1 then 0.15× gain, is **superseded and struck**: HPA-03 says
  inert, and a cap that travels two-fifths of the way toward a seal it
  cannot make has performed most of a commitment dishonestly). No gate,
  no commit, no seal sound, no haptic on attempted drag beyond the
  system's own touch feedback. The track word (`SLIDE TO ARM` / at
  04:58 `START`) stays printed, unchanged — the offer is not withdrawn,
  it is waiting.
- **The cap loses the accent.** Cap renders in the track's recess tone
  family for the duration of the tenancy. Accent = COMMIT fill only;
  a slide that cannot commit may not wear the commit colour. The
  orange returns at hand-back, in the same frame as the tick (§6) —
  the hand sees and feels the slide come back to life together.

Nothing else on the Deck changes. Hero, wheel, hub, NEXT line (on the
Deck body — distinct from the suspended chrome slot), task line: all
stand. Tenancy is a change of words in chrome and of life in the slide,
and of nothing else.

### 3.5 The tap

The tenanted slot is a tap target: **full content width × 48dp hit
area** (the 20dp line box, §5, extended vertically into the imprint's
padding — a weak hand at 04:58 does not aim at a 20dp line). Tap goes
to the class's repair, never to a generic destination:

- A → system Accessibility settings for the shield service.
- B → in-app restart: rebind attempt runs immediately (≤300ms
  re-sweep); only if the rebind fails does the tap's sheet offer the
  Settings route. `RESTART` must be the app's first move, not his.
- C → system App-usage access settings. D → system Overlay settings.
- E / F → Shield's blocked-list editor (the list's one home).
- G → immediate re-sweep; if it resolves, hand-back (§6) happens on
  the spot and no sheet ever opens.

Under both Guard outcomes these targets exist (settings deep-links,
the service, Shield's list); the tap never routes "to Guard" or "to
Shield" as a place — it routes to the *fix*. Guard remains his pick,
untouched, unneeded here.

### 3.6 Detection — when the sweep runs, in ms

Tenancy is decided **before the gesture, on arrival — never discovered
by the slide** (that was the Poll Rail's sin and HPA-03's whole
charge). Timings, normative:

| Moment | Behaviour | Budget |
|---|---|---|
| **Continuous background** | OS listeners registered while the app process lives: accessibility-state change, Shield bind/unbind, app-ops change. Any callback marks the cached sweep **dirty instantly (0ms)** — a dirty cache is never rendered from. | — |
| **Deck open (the HPA-03 read)** | If the last sweep is ≤5,000ms old and no dirty flag: tenancy decided from it at **first paint, 0ms added**. If stale or dirty: a fresh sweep dispatches on the open event, in parallel with first paint. | Sweep target ≤150ms, budget **300ms** |
| **The pending window** (sweep in flight at first paint) | The slot prints **nothing** — the one lawful blank (§5.3) — and the slide is inert until the sweep lands. Emil's `IDLE` may **not** pre-print: an anticipatory state word under an unfinished check is exactly Prima's first-paint charge, and I will not spec it in my own line. On landing: failure sentence *or* Emil's word fades in, **90ms cross-fade**, same slot, same baseline. | Blank ≤300ms typical |
| **Sweep timeout** | At **1,500ms** without a full result (Rosa's timeout, shared), class G tenants. The slide has been inert throughout; no arm was possible on an unchecked Deck at any point in the window. | 1,500ms hard |
| **Failure lands while Deck is open and idle** (he is looking at the Deck; a permission dies in another app / a broadcast arrives) | Callback → re-sweep starts within **16ms** (one frame) → on result, tenancy swaps in with the 90ms cross-fade. No haptic, no sound: the line changing tier under his eyes is the whole event. Alarms are for fires; this is a fact arriving. | ≤300ms + 90ms |
| **Failure lands mid-slide** | §4 — Rosa's border. The slot does **not** swap while his thumb is down. | — |
| **Session sealed** | Tenancy is **frozen out**. The slot holds Emil's `SEALED` for the session's life regardless of check state (my seat file's freeze, kept): HOLD forbids a repair prompt competing with a sealed session. A mid-session death surfaces only where settled law already puts it — the airlock's degraded handling and the unlock-time gap line — never as a chrome eviction of `SEALED`. On session end, the first Deck-open sweep re-applies this law in full. `PAUSED` counts as pre-arm for this law (he must still resume through a gesture; the pause readout on the Deck body keeps the session state visible while the slot tenants). | — |

**ARM accounting for this section:** the healthy path adds **0
gestures and 0ms** — the sweep precedes or parallels arrival, and in
the common case (fresh cache) the Deck is simply correct at first
paint. The pending window's inert slide is not a tax on ARM: it is
≤300ms on a cold process, inside the ≤10s budget by an order of
magnitude, and it is the price of never arming on a guess. On a
failure morning ARM does not run at all — not "ARM is slower", but
"there is no arm, because an arm over dead blocking is a ghost session
and the budget was written for real ones."

---

## 4. The border with Rosa's strip — live during the slide

Rosa's Parallel Bulb-Check runs *in parallel with the drag*, fed by
the same sweep (§3.1). So who owns a failure that only her watch
catches — one that begins after my Deck-open read (the permission dies
in the 800ms between his arrival and his thumb, or the open read was
fresh-but-pre-death by seconds)? The law:

1. **Rosa's interlock is a refusal, not a rendering.** If her BLOCK
   query returns failed before the cap passes the gate (catch 0.80,
   commit 0.82), the interlock engages: the cap cannot commit, full
   stop. Her strip's cell shows its lamp word (`BLOCK OFF`) — that is
   her cell's state, inside her strip, and it is the strip doing its
   one job. She does **not** print my sentence in the track. (Her
   wave-1 interlock string, `BLOCKING IS OFF — NOTHING WILL BE HELD`
   in the track, is struck from the merged form: it is my fact, in a
   second home, in different words — the precise shape of a TRUTH-once
   violation.)
2. **The slot does not swap mid-drag.** While the thumb is down, the
   chrome is furniture under a working hand; swapping the nameplate's
   tenancy, evicting two slots, and de-oranging a cap he is *holding*
   is three motions competing with the one gesture the app exists for.
   The failure is being handled — by refusal, in the hand. Words wait
   the length of one drag.
3. **On release, the tenancy transfers.** Cap returns under its filed
   spring (~260ms); **on the cap's rest frame**, the slot swaps to the
   failure sentence (90ms cross-fade), the neighbouring slots suspend,
   and the cap — now at rest — loses its accent. Rosa's cell keeps its
   lamp word only while the strip lives; the sentence in chrome is
   from that frame the fact's **sole standing rendering**. Maximum
   co-presence of lamp word and sentence: **zero frames** — the lamp
   is a drag-state, the sentence is a standing state, and the handover
   frame is where one becomes the other. That is a relay, not a
   duplication.
4. **The race in the other direction** (open read said dead, repair
   lands before the drag — he fixed it from the notification shade):
   her sweep at touch-down is fresher than my arrival read, and a
   *passing* touch-down sweep hands the slot back (§6) on the same
   rest-frame rule, before the drag begins — touch-down is not yet
   "mid-drag" for this law; the swap lands in the ≤300ms between his
   touch and any real travel, and a man whose slide just came alive
   under his thumb has been told everything, in the hand, for free.
5. **A failure returning *after* the gate** — past commit, the seal is
   a fact and the session exists. Rosa does not un-commit a sealed
   session on a late query (a seal revoked by a slow network answer is
   a worse lie than the race that caused it). The death is a
   mid-session fact from that instant and follows §3.6's sealed row:
   airlock degradation + the gap line, with its true start time. The
   slot never learns of it. TRUTH is kept — the fact *is* recorded and
   *is* surfaced, at the settled surfaces — without breaking HOLD to
   confess it early.

---

## 5. Silence, specced

"Silent when true" is the filed rule. This section gives silence a
shape. Two candidate forms were put to me:

- **Form HELD** — the slot's space is reserved permanently; in health
  the Truth Line prints nothing there.
- **Form CLOSED** — no slot at all in health; the Deck closes up, and
  the slot (and its height) comes into existence only when a failure
  needs it.

### 5.1 The ruling: HELD — and in health the held space is Emil's

I rule **HELD**, with the refinement the premise already contains: the
held space is not an empty hole in the chrome. It is the imprint's
state slot, permanently reserved, and in health it is *tenanted by
Emil's state word*. The Truth Line's silence is therefore not a blank
— it is the visible presence of the other tenant. My line says nothing
when true by **not being there**, and what *is* there is the quietest
possible assertion that the machine knows its own state: `IDLE`.

This is the design claim, stated plainly: **a held space is a claim
about trust; I am making it.** An app that reserves a line for the
truth, and lets that line spend 200 healthy mornings saying `IDLE`,
has built a place the eye trusts *before* it ever needs to — so that
on the one morning the words change, no new object has to arrive, be
noticed, be learned. The failure inherits two hundred mornings of
muscle memory. A closed-up Deck spends that inheritance to save 20dp
of air on a screen whose reserve is, by filed law, fenced emptiness
anyway. Economy of *height* is the wrong economy on the Deck; the
scarce currency is *learned position*, and HELD is how you bank it.

The pure-blank variant of HELD (slot reserved, printed literally
empty in health, Emil evicted elsewhere) is rejected inside my own
ruling: a permanently blank 20dp line reads as a loading failure, and
it evicts the state word to a second home that does not exist. HELD
means the *object* is permanent. Its healthy tenant is print doing
its job.

### 5.2 Geometry — Form HELD (ruled)

All values dp, margin 20dp both sides, content width = screen − 40dp.
The imprint block (normative; this supersedes the 9sp / 52dp draft
geometry in my and Emil's wave-1 files, which the plate floor and the
state-word spec have between them made unbuildable):

| Element | Box | Notes |
|---|---|---|
| Imprint block, total | **56dp high** × content width | Sits on the ground; no fill, no panel |
| Masthead row | y 0–20 (20dp) | Press mark + `MONOLITH`, Sora SemiBold 15sp, lh 20 |
| Engraved rule | y 24 (1dp) · gap 2 · y 27 (0.5dp) | Full content width; registration cross at right terminus |
| **The slot (one object)** | **y 32–52 (20dp high)** × content width | Baseline at **y 47**. Atkinson 700 15sp, +0.5, lh 20, single line, left-aligned, never wraps |
| Slot hit area (failure only) | y 28–76 → **48dp high** × content width | Extends 4dp up into the imprint's lower padding and 24dp down into the S32 gap below the imprint; invisible, failure-tenancy only. In health the slot is print, not a button — Emil's law, kept: furniture is not pressed |
| Imprint bottom padding | y 52–56 (4dp) | Then the filed S32 to the readout block |

**Invariance, the whole point:** imprint height 56dp, slot box, and
every y-coordinate below it (readout, wheel, slide, nav) are
**identical in health, in failure, in the pending window, and across
hand-back. Δy = 0dp for every element on the Deck, in every tenancy
transition, forever.** The Deck stack's reserve (min 48dp, filed) is
never touched by tenancy — the failure does not eat the thumb's
runway, because the sentence was never given a row of its own to
return.

Tier by tenancy (night; day mirrors one step in its staircase):
healthy word `#9C8E84` (secondary) · failure sentence `#D2C6BB`
(primary) · pending blank — no glyph, the ground itself.

### 5.3 Geometry — Form CLOSED (considered, loses)

| Element | Health | Failure |
|---|---|---|
| Imprint block | 36dp (masthead 20 + rule + 4 padding; no slot row) | 56dp (slot row inserted at y 32–52) |
| Readout block top | y = 36 + 32 = **68** | y = 56 + 32 = **88** |
| Wheel top / slide top | as health | **+20dp** vs health |
| On repair | — | **−20dp** (the whole Deck jumps back up) |

So CLOSED's price, in numbers: on the worst morning, at the moment of
maximum confusion, **every object on the screen moves 20dp** — the
hero drops a full body-line, the wheel drops, and the slide drops
20dp *away from where his thumb's memory keeps it*, then jumps back
up the moment he fixes the thing. The failure would announce itself
as a small earthquake and the repair as a second one. It also has no
answer for Emil: with no slot in health, the state word needs another
home, and every candidate home (beside the mark, in the nav, on the
slide) is either a second furniture system or a duplication. CLOSED
is an economy that spends trust to save air. It loses on geometry,
on the state word's homelessness, and on the 04:58 frame (§7), where
a 20dp drop is a visible flinch in a frame whose entire law is
stillness.

**The one lawful blank.** HELD has exactly one moment of literal
emptiness: the pending window of §3.6 — slot reserved, nothing
printed, ≤300ms, while the sweep that decides the tenancy is still
running. That blank is not silence-as-health; it is the app honestly
not knowing yet, drawn as not-saying. It is bounded, it never
persists (timeout tenants it with class G), and it is the only blank
this system ever shows.

---

## 6. Hand-back — the first moments after repair

The repaired line is a lie risk in both directions: linger, and the
app claims a broken state that is no longer true; vanish the instant
a toggle flips in Settings, and the app claims a repair it has not
measured (he may have toggled the wrong service's switch — Settings
is full of switches that look alike at 05:00). The law:

1. **Repair is never assumed from return.** Foreground-return, sheet
   dismissal, and the Settings back-stack are *triggers for a
   re-sweep*, never evidence. The re-sweep is the full four checks,
   same pipeline, ≤300ms.
2. **The failure sentence holds through the re-sweep.** From his
   return to the verified result, the slot keeps printing the failure
   — because until the sweep lands, the failure is still the last
   *measured* truth. Expected hold: ≤300ms. This hold is not the
   lingering lie; it is the refusal to print an unmeasured recovery.
3. **Stability beat: 500ms.** A full-true sweep starts a 500ms
   stability window. Any dirty callback inside it (service flapping
   during bind is the real case) resets the window and the failure
   stays — a sentence that flickers FIXED / OFF / FIXED across a
   flapping bind would be three lies in a second. A service that
   cannot stay alive for half a second has not been repaired in any
   sense he can arm on.
4. **The swap itself: no restored line, ever.** At stability, the
   slot cross-fades **90ms** directly from the failure sentence to
   Emil's state word (`IDLE`). There is no intermediate print — no
   `BLOCKING IS ON`, no `READY`, no `FIXED`, not for one frame, not
   as a fade-through. Ruth's Phase-3 frame prints `BLOCKING ON`
   restored; I strike it here, with respect and by name: it is a
   second rendering of the health fact, its only information is "the
   previous line is gone", and the previous line's *absence in its
   own slot* already says exactly that, in the slot's own grammar.
   Total hand-back latency, measured-true → Emil's word: **≤890ms**
   (300 sweep + 500 stability + 90 swap), and not one millisecond of
   it is spent printing a recovery.
5. **Simultaneous, in the same frame as the swap:** the suspended
   chrome slots (next-routine, shut count) return with their own 90ms
   slug change; the slide cap takes the accent back; and **one tick,
   5ms @ 0.4**, fires — the repair confirmed in the hand (my seat
   file's one haptic, kept). No sound. If the repair was completed
   while he was *not* on the Deck, all of this has already happened
   silently before his next arrival, and he simply meets a healthy
   Deck: hand-back owes no performance to an absent witness.
6. **Partial repair** (he fixed A; C was also dead): at the
   stability-gated re-sweep the slot swaps sentence-for-sentence —
   `BLOCKING IS OFF — TAP TO FIX` → `USAGE ACCESS IS OFF — TAP TO
   FIX`, 90ms cross-fade, tier unchanged, no tick, no accent return,
   slide stays inert. The tick and the orange are spent only on the
   *full* truth; a partial repair that ticks like a whole one is a
   small celebration over a still-dead morning, and this app does not
   celebrate halves.

---

## 7. The 04:58 variant (mailed to Joss in full)

At 04:58 the frame is Ruth's drawing (HUMANS-PHASE3 §3): night ground
`#171412`, hero `3H 00M` in glyph `#EDE3D8`, three unfilled pips, the
wheel at rest and demoted, an empty slide track whose cap says
`START`. My variant is that frame under tenancy — dead blocking —
and it changes in exactly the places §3.4 names and nowhere else:

- **The slot tenants.** On the nameplate line, where the healthy
  frame's quiet word sits, one sentence at **Atkinson 700 15sp,
  primary tier `#D2C6BB`**: `BLOCKING IS OFF — TAP TO FIX`. It is the
  frame's most important *line* — the hero is still its biggest
  number, and should be; but the hero states an offer, and this
  sentence states the world. It is the only imperative on the screen,
  the only tappable words, and the only line whose tier stepped up.
  Nothing about its size is special. Everything about its solitude
  is: the chrome's other slots are suspended, so the nameplate of the
  whole app, at the first minute of his day, is a mark, a rule, and
  this sentence.
- **The slide is inert, cap in recess tone, accent withdrawn, word
  `START` unchanged** — the offer waits, unminted as a miss (HPA-03 /
  Ruth Variant A, agreed in full).
- **Nothing moves.** Δy = 0 against the healthy frame (§5.2). His
  half-awake hand finds the slide exactly where yesterday's healthy
  frame left it — and finds it dead by touch and tone, with the
  reason already read, or not even needing to be read, at the top.
  No countdown to 05:00 is printed or implied; the boundary is not a
  demand, and a dead permission does not get to borrow urgency from
  the routine's clock.
- **Hand-back at 04:58** follows §6 to the millisecond: he taps the
  line (48dp target — his weakest grip gets the app's most generous
  target on its most important line), fixes the switch, returns; the
  sentence holds through the re-sweep (≤300ms) and the stability beat
  (500ms), then Emil's word, the returning slots, the accent, and the
  one tick land together. The same frame stands, unchanged, ready —
  Ruth's sentence, with `IDLE` where she printed `BLOCKING ON`, for
  the reasons in §6.4. His one slide was never spent. The morning is
  not ruined; it is merely told, once, at the size of every other
  true thing in the app.

---

## 8. Standards check

- **ARM ≤10s — PASS.** Healthy path: +0 gestures, +0ms (fresh cache
  at first paint; worst lawful case a ≤300ms pending window with an
  inert slide, ~3% of the budget, spent only on a cold process).
  Failure path: no arm exists to time. The slide's inertness on a
  dead morning protects the budget's meaning — the ≤10s was always a
  promise about arming a *real* session.
- **HOLD absolute — PASS.** Tenancy is a pre-arm law. Sealed sessions
  freeze the slot (§3.6); no repair prompt, eviction, or truth event
  of mine can reach inside a session; the tap exists only on the
  Deck, pre-arm (and inertly during `PAUSED`, where a resumption
  gesture is still owed). Zero mid-session decisions introduced.
- **TELL THE TRUTH once — PASS, with the seams named.** One fact
  (will blocking work, and if not, the first reason why), one
  pipeline (shared with Rosa, §3.1), one standing rendering (this
  slot, this sentence). Chrome's aggregate count, the track label,
  and the restored-line are all struck rather than duplicated. The
  two bounded non-standing co-presences are a *relay* (§4.3: lamp
  word during a drag, sentence after it — zero frames of overlap)
  and a *repair surface* (the sheet behind the tap lists all current
  failures; it is a different surface doing a different job — repair,
  not statement — reached only by his press). Cause is never
  fabricated (§3.2); unknown is printed as unknown (class G).

## 9. What this replaces, and why the replaced thing loses

1. **My wave-1 chrome line** (`READY · 47 APPS BLOCKED` /
   `NOT READY · 2 OF 4 CHECKS MISSING`, Anonymous Pro 10sp, second
   line of chrome). Loses three ways: it prints health as a standing
   boast (silence-when-true exists precisely because a daily READY
   trains the eye to skip the line on the one day it changes);
   its failure form is a *count*, which tells him something is wrong
   and nothing about what; and at 10sp it sets the app's most
   important sentence in furniture type, under the plate floor's
   purpose if not its letter.
2. **My wave-1 Deck slide labels at 13sp** (`TURN ON BLOCKING TO
   ARM` in the track, plus the resistance drag). Loses: 13sp is an
   illegal tier; the track is the wrong home (the reason belongs
   where his eyes arrive first, not where his thumb discovers it);
   and the resisted drag performs a partial commitment against a
   seal that cannot happen. HPA-03's inert slide and chrome home
   beat it on every axis, and this file adopts them rather than
   defending my earlier draft.
3. **Rosa's interlock string in the track** (`BLOCKING IS OFF —
   NOTHING WILL BE HELD`). Loses as a *standing* form: same fact,
   second home, different words. Her interlock survives whole as a
   mechanism (§4); only its sentence is struck.
4. **Ruth's restored `BLOCKING ON`** (HUMANS-PHASE3 §3, Variant A).
   Loses as a small, kind lie: a health announcement printed at the
   repair moment, lingering as a state that is not a state. §6.4
   strikes it and keeps everything else in her frame.
5. **Form CLOSED for silence (§5.3).** Loses on its own geometry:
   a 20dp earthquake at the failure moment and another at repair,
   in exchange for air the Deck's reserve already holds fallow.

## 10. The strongest case against this proposal

Made as hard as I can make it, because it is mostly right:

**One line, first failure only, is a serial-repair machine at his
weakest hour.** The morning accessibility *and* usage *and* overlay
all died in an overnight OS update — a real, common morning — my Deck
shows him one sentence. He fixes it (four Settings gestures), returns,
and is shown a *second* sentence. Fixes that. A third. Three round
trips, nine-plus gestures, each announced only after the last was
paid, at 04:58, with the fasted block bleeding away — while a plain
four-row checklist on the Deck would have shown him the whole damage
in one look and let him fix it in one Settings visit. My defence —
the tap's repair sheet lists everything — is weaker than it looks:
it asks a barely-awake man to *press the one line* to learn what the
one line refused to tell him, and the press is exactly the extra
gesture HPA-03 was written to abolish. There is a real version of
this app where the Deck prints the first sentence *and* a quiet
second line — `AND 2 MORE — TAP TO SEE ALL` — and I cannot kill it
with TRUTH-once, because a count of *further repairs* is arguably a
different fact from the first failure, the same scope-argument Prima
let stand for Band/Linea. I am ruling against it on one ground only:
every "and more" line ever shipped has trained its user to tap
through the first line unread, and the first line is the one that
keeps the slide dead honestly. But if Sarthak lives one
triple-failure morning and curses the serial trips, this paragraph
is the confession that I priced his attention above his minutes,
knowingly, and the `AND n MORE` line should be adopted as an
amendment the same day — at 15sp, in the slot, second sentence,
mine to typeset the apology for.

A second, quieter count: 15sp in chrome may simply be *missable*. I
have argued importance can be carried by solitude, tier, and a dead
slide — but a man who does not read the line meets an inert slide
with an unchanged `START` on it, and the first explanation available
to a half-awake mind is "the app is broken", which is the one
conclusion this entire system exists to prevent. The dead cap's
recess tone is a subtle signal to stake that on. If the build's
device trial shows even one tester pressing the dead slide twice,
the sentence should take the track as well for the drag's duration —
a bounded co-presence I have here refused — and §4's purity should
yield to the evidence. Laws are for designs that pass; this one
should be made to pass on a real thumb, not on my argument.

---

*— Suki, Wild (space). The Truth Line and the Wall Card are my two
objects; this file is the first of them, on the Deck, past the filed
plan. Mailed: Emil (the slot — §2, §5, §6 are half his), Joss (the
04:58 variant, §7, for the frame he is drawing). Wall signed.*

---

## 11. Addendum — Emil's filing, received during this run (contested, for the merge)

Emil's letter (`imprint-tenancy-law`, read and answered in his inbox)
files the slot from the print side: plate size throughout (Anonymous
Pro 700 10sp +2.0), outcome-worded sentences (`BLOCKING IS OFF —
NOTHING WILL BE HELD` class), and my wave-1 track instruction lines
kept. Agreements, recorded so the merge doesn't re-fight them: the
four checks + hash, first-failure priority, the sealed freeze, the
tier rule, the deaths of `READY · n APPS BLOCKED` and of the failure
count, Rosa's under-track second printing dying, and primed getting
no sentence of its own. Contested, both files standing: **(1) size**
— plate 10sp (Emil) vs state-word 15sp Atkinson (this file, §2/§3.3;
Set's ramp table is my ground) · **(2) the words** — outcome strings
(Emil) vs HPA-03's settled `BLOCKING IS OFF — TAP TO FIX` grammar of
named lack + act (this file, §3.2) · **(3) the track lines** — kept
at 13sp (Emil) vs struck as an illegal tier and a second rendering
(this file, §9.2). The sealed-count / PEEK-in-count question in his
letter belongs to slot 3 and the Wall Card's phase; it is neither
conceded nor claimed here. Neither file is the settlement; the merge
decides, with both arguments in front of it.

*— Suki, addendum appended on receipt of Emil's letter, before sign-off.*
