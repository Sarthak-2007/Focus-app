# 19 — LINEA (Improvers, the Deck task line) · SLICE E BUILD-READY
## The Deck's END STATE under E4/E6 — the frame, element by element

*Seated by Ilya (Improvers coordinator) for the SLICE E BUILD-READY
wave. My joint: the Deck task line and the frame it sits in
(`TactileDeckScreen.kt`) — what the Deck renders from E0 (the
completion flip) through the HELD FRAME, the E1200 CUT, the detained
receipt, and the Sled Goes Home. Ghosts' internals are Rad's (wheel)
and Zug-II's (sled); the frame they sit in, their tenancy, and the
task line are mine. All file:line against TODAY's trunk
(`~/workspace/clone-inspect/Focus-app`), read this run. No code
edited, no gradle run. Inbox at start: no unread letters (six in
`read/`, none touching this joint).*

**Headline verdicts**

1. **The end state is a FROZEN RECORD inside LIVE FURNITURE.** From
   E0 to rest (E3130) every session-derived element — hero, task
   line, imprint tenant, sled — renders from snapshots and cannot
   be rewritten by any live flow. The strip's BLOCK cell alone
   stays live (heartbeat furniture, not a session fact). §1–§2.
2. **The task line's input death must be built at two layers** —
   today all three of its verbs are live mid-session, ungated in
   both the composable and the ViewModel. Absent-as-target in
   composition + refused at the VM gate, gate extended through
   detention. ≈30 net + tests. §3.
3. **The defocus is UNBUILT** — zero blur machinery anywhere in the
   tree. Its build is one rack Animatable + three plane wrappers +
   a tone scrim, ≈+32 net inside E4's share on top of Wren's
   already-priced rack host. One unfiled constant (the tone step's
   magnitude) and one platform wall (minSdk 24 vs RenderEffect
   API 31) named in §4.
4. **FORK F-L1: the receipt has NO lawful room on the Deck.** The
   821dp sheet's only unallocated vertical space is the reserve
   (23dp at H=844, 75dp at H=896), and the reserve is lawfully
   empty forever. The smallest lawful slip measures ≈104dp; a
   typical slip ≈184dp. Four options priced in §5; recommendation:
   generalise the faulted branch's aperture sheet (option C) or,
   if "no overlay" is held absolute, the wheel-zone host (option
   B) with its spec casualties named. **No receipt host is priced
   into this slice until Ren rules.**

---

## 1 — THE CLOCK (recap, so the inventory's phases are exact)

E0 = the repository completion flip — `MonolithRepository.kt`
`updateSessionProgress` writes `isActive = !allBlocksCompleted,
completedSuccessfully = allBlocksCompleted` (:1110–1111) — observed
on the session StateFlow edge (E4's amendment; today the VM finds
it up to 1000ms late in `startTickerLoop`'s 1s poll and answers it
with the *seal* sound — both die in E4). Phases:

| Phase | Span | Governing text |
|---|---|---|
| E0 flip | one frame | MASTER §5.7; SLICE-D-E §0.6 |
| HELD FRAME | E0 → E1200 | bed out 300 · true silence 400 · pulse at E700 |
| CUT + RACK | E1200 → receipt t=0 | 0ms cut to defocused Deck; RACK 280 from R0 = max(E1200, P) |
| Receipt t=0 | E1480 happy path (≤E1800 all branches) | detention formula, Wren §4.3 |
| Sled trigger | receipt +1000 = E2480 | Fen, E6 |
| Rest | E3130 | sled home; end state closes |

One tap anywhere during E0–E1200 skips straight to the cut (Wren's
skip law). That tap is consumed by the finish controller at Deck
level — it never falls through to any element target (§3).

---

## 2 — THE END-STATE INVENTORY (Q1)

Frame = Kato's sheet as amended (§3-R4): top stack 536 (chrome 48 ·
S32 · header slot 20 · S12 · readout 88 · S8 · NEXT 20 · S20 ·
wheel 288) · reserve H−821 · bottom stack 285 (task 20 · S20 ·
strip 53 · S6 · slide 72 · S8 · sentence slot 22 · S20 · nav 64).

### 2.1 Element-by-element, phase-by-phase

| Element | E0 flip | HELD FRAME E0→E1200 | E700 pulse | E1200 CUT | Receipt t=0 (E1480) | E2480 sled trigger | E3130 rest |
|---|---|---|---|array>
| **Hero** (readout 88) | Steps to `00:00` on its normal per-second cut; arc's final segment lands with its 150ms beat, arc standing **full** (§5.7) | Frozen. Final total at full size, scale continuous 1.0; colon does not move; no element moves (Wren §4.2) | **Pixel-identical to E699** — the pulse is audio only, never illustrated | Cut to the idle Deck's span (dial as left, e.g. `170` / `FLOW`), defocused with the room (§4) | Sharp idle span | Unchanged; receipt still present (§5) | Live again — first wheel detent re-strikes it |
| **Wheel ghost** (288 box) | Persists at its running settle values (Rad: scale 0.80 · y+20dp · α0.50 · blur 6dp) — trunk deletes it at :273, ghost retention is Rad's build | Frozen ghost; angle/value never moved during the session (input was dead) | Unchanged | **Found** at scale 1.0 in defocus — no rewind, nothing was restored (Wren §4.4) | Sharp, at rest angle | Unchanged | Live; hub/wheel input returns this frame |
| **Sled / slide** (72 track) | Seated at fraction 1.00, ink, inert (Fen rest contract: SEALED/RUNNING owner = controller state) | Nobody owns the thumb; no animator alive (FINISHING row) | Unchanged | Still seated in the defocused Deck | Still seated; idle cap words already swapped in at receipt (Fen/Aldo interface) | **Trigger fires** (ReceiptState +1000ms); return animator becomes sole writer | Fraction exactly 0; animator releases ownership; gesture owns the thumb |
| **Task line** (20 register) | Collapses to the held frame's **micro-line**: `{TITLE} — {true length}` form (Wren §4.2: `PHYSICS — 50 MIN`), from the E0 snapshot (§2.2) | Frozen micro-line; verbs already dead since t=0 (§3) | Unchanged | Re-renders as the idle-form line in the defocused Deck — **secondary plane**, sharpens at R0+50 (§4) — still snapshot content, still targetless | Snapshot content; the receipt below/nearby prints the same title from the same snapshot — one fact, one pipeline | Unchanged | **Re-derives live in one cut** from the task flows; any deferred change (§2.2) lands here, whole, unanimated |
| **Strip / rack band** (53) | Band is in its latch→receipt rack tenancy (D6/D8; release rack under the F9 gate) — reverse cascade renders here, backward via the pawl ride | Rack steps as links CONFIRM-OPEN inside the hold; invisible work, visible band | Unchanged | Slug moment: band returns to strip tenancy at the cut | **Strip again**: BLOCK → APPS → TIME, re-measured once at the swap | Unchanged | Standing furniture, live as in any idle state |
| **Imprint slot** (12 plate + 4 lip) | SEALED tenant + pressure lip **persist** (Wren amendment E2 — swap is timed to the cut, not E0; printing IDLE over the held frame would put two contradictory facts on one screen for 1200ms) | SEALED, frozen at the sealed count for the whole hold | Unchanged | Slug swap SEALED → re-measured IDLE (150ms, complete by E1350, inside the rack); lip fade runs E1200–1440 *under* the defocus | IDLE tenant, current loadout | Unchanged | IDLE tenant, live |
| **Sentence slot** (22, reserved) | Healthy tenant = *nothing* | Nothing | Nothing | Nothing | Nothing — `STILL BLOCKED` is homed on the receipt only (E4), never tenants this slot | Nothing | Nothing |
| **NEXT slot** (20) | Running Deck carries no NEXT (its tenant is the primed loadout of an *idle* Deck) | Absent | Absent | Returns with the idle Deck, defocused, tertiary plane (R0+100) | Sharp; live primed loadout word | Unchanged | Live |
| **Header slot** (20) | Running form (state word / count tenant per D9's re-homing) | Frozen with the picture | Unchanged | Idle form, tertiary plane | Sharp | Unchanged | Live |
| **Utilities row** (DESK CLOCK / HELD, trunk :454–472) | **Dies at E0.** It is running-state furniture; it has no register in the 821 sheet at all (an unbooked element in trunk form — Kato's sheet never carried it, and the end state does not invent room for it). The HELD digest's post-session home is the Drawer/record, not the Deck's end state | Absent | Absent | Absent (does not return with the idle composition at the cut — it was a session verb, and the session is over) | Absent | Absent | Absent |
| **Chrome / nav** | Session-end event: pin lifts 180ms, dimmed scale restores 260ms (MASTER §6.5 — unlock is on session end's arrival frame, *not* at receipt dismissal) | Travel still dead *in effect*: the skip tap owns all Deck touches E0–E1200; nav taps during the rack are rack-law taps (a reverse tap reverses it; nothing queues — Wren §4.2) | — | Nav detent jumps (0ms) at rack end | Free | Free | Free |

### 2.2 Frozen vs live — the task line and the queue (the heart of Q1)

**The session's task is NOT done by definition.** In trunk, tasks
and sessions are fully decoupled: the completion flip
(Repository :1110–1111) touches no task row; task completion is a
separate user act (`completeTaskAndAdvance`,
MonolithViewModel.kt:587). Session elapsed ≠ task done, in code
and in spec. The held frame's micro-line therefore prints the task
*as it stood* — title + true length — and claims nothing about its
completion. Any end-state rendering that strikes the title
through, ticks it, or advances the line to the next queued task at
E0 is a lie told by form.

**What freezes, and from which snapshot.** The end state's task
line renders from a **FinishSnapshot taken at E0**: `{taskId,
title as it stood at sealing, true length, queueCount}`. The title
is the arm-time value (Uma's provenance law, §5.7: the receipt
carries the title *as it stood at sealing* — the micro-line and
the receipt read the same snapshot, one pipeline, or the two
surfaces can disagree on one screen).

**Can the line change mid-detention if a task completes
elsewhere? The pathways exist; the answer is NO, by gate:**

1. *Tasks screen.* Nav travel dies at the seal (§2.1/§6.1 sealed
   law) and chrome unlocks only at session end — but detention
   (E1200–E3130) is *post*-session: he can lawfully reach Tasks
   during it, complete the task there, and Room will emit a new
   `activeTask` to any Deck composition reading the flow.
2. *The VM's own collector.* `MonolithViewModel.kt:318–325`:
   `activeTask.collect` overwrites `_intentionText` unconditionally
   and `_dialMinutes` whenever `activeSession.value?.isActive !=
   true` — after E0 the session is inactive, so a task emission
   mid-detention would move the dial and the hero *behind the
   receipt*. A live leak, in trunk, today.
3. *Routine priming* writing a new cued task into the same flow —
   same emission path as (1).

**Mechanism (my build, priced §6):** while the VM's finish state
is RELEASING or RECEIPT (E0 → rest), the Deck's end-state branch
reads FinishSnapshot, never `activeTask`; the VM collector's
dial/intention writes are additionally gated on
`finishState == IDLE`. Deferred changes are not lost and not
queued as animations: at rest (E3130) the line re-derives from the
live flows in one cut — stillness law (a number changes only when
its fact changes, all at once) applied at the one lawful boundary.

**The queue behind it.** `QUEUE n` in the line is a count fact
frozen in the same snapshot. Queue edits made elsewhere during
detention land at rest with the title, in the same cut. The
session's own task, if he completes it on Tasks during detention,
is *still* the micro-line's subject until rest — the receipt has
already printed it; the end state does not rewrite history under
a printed receipt.

**The one live exception, by design:** the strip's BLOCK cell.
It is standing furniture driven by the services heartbeat, not a
session fact; if blocking dies during detention the cell says so
the frame the heartbeat does. APPS/TIME re-measure once at the
E1200 swap (Emil's re-measured IDLE), then behave as ordinary idle
furniture. Furniture lives; the record freezes. That asymmetry is
the inventory's whole law.

---

## 3 — INPUT DEATH ON THE TASK LINE (Q2)

**Today: the line is fully alive, in every state.** Trunk
`TactileDeckScreen.kt` task section (:372–472, composed whenever
`activeTask != null`, with no session gate anywhere):

| Target | Site | Callback |
|---|---|---|
| Title (expands sub-steps) | clickable on the title Text when `subSteps.isNotEmpty()` | local `stepsExpanded` toggle |
| `DONE` | clickable Text | `onCompleteActiveTask(activeTask.id)` → VM `completeTaskAndAdvance` (:587) — **no sealed guard** |
| `QUEUE n` | clickable Text | `onOpenTaskSequencer()` — a door, mid-session |
| Each sub-step row | clickable Row | `onToggleTaskSubStep(id, idx)` → VM `toggleTaskSubStep` (:705) — **no sealed guard** |

Wren's A4 already kills these verbs at t=0 *by state* as part of
the shot; MASTER §2.5 (my Rung 4) files the form: **DONE absent,
steps frozen, QUEUE a static fact**. What neither filed is the
mechanism on this line. Mine, two layers — either alone is a lie
(a hidden-but-live target, or a dead-looking button whose VM call
still lands from another surface):

**Layer 1 — composition: targets ABSENT, not disabled.** In the
sealed branch and the end-state branch, the line composes with no
`clickable` modifier on any element: title is plain Text, DONE is
**not composed at all** (absent, never greyed — §5.9 check 4:
inert *and* absent as a target), QUEUE renders as plain Text (the
count, no door), and the sub-step subtree is not composed
(`stepsExpanded` is cleared at the seal and cannot be re-raised).
No ripple, no pressed state, no semantics role. TalkBack reads
the line as text, once.

**Layer 2 — the VM/repository gate, extended through detention.**
`completeTaskAndAdvance`, `toggleTaskSubStep`, and `deferTask`
refuse while `activeSession.isActive == true` **OR**
`finishState != IDLE` (RELEASING/RECEIPT). The detention extension
is the part the sealed gate alone misses: after E0 the session is
inactive, so a seal-only gate re-opens the verbs *during the held
frame* — precisely when the Deck is showing the frozen record.
Refusal is silent (no banner, no sentence — detention offers zero
decisions; a refusal surface inside the held frame would be one).
The same gate covers the Tasks screen's own rows during detention
(Zeile's Rung 4 locks them during a session; this extends his lock
to the finish, one predicate, both surfaces).

**The skip tap does not resurrect anything.** E0–E1200 touches are
consumed by a Deck-level absorber owned by the finish controller
(Wren's skip law); the tap that jumps to the cut is spent there
and is never dispatched to the line, the wheel, or the sled.
During the rack, touches are rack-law navigation taps; the line
has no target to receive one in any case (Layer 1).

**Verdict: not tappable — at no phase from t=0 (seal) to rest.**
The first lawful task-line target after that is on the *rested
idle Deck* at E3130, when the line re-derives live (§2.2) with its
idle verbs restored by the same state branch that removed them.

Price: Layer 1 ≈18 net (state branch + DONE/steps excision in the
end-state composition); Layer 2 ≈12 net (one predicate, three call
sites); tests ≈30 (§7, T3–T5). Inside my share, §6.

---

## 4 — THE DEFOCUS (Q3): UNBUILT — ITS BUILD, NAMED AND PRICED

**Trunk fact: there is no defocus in the tree.** Grep over
`app/src/main/java` this run: no `Modifier.blur`, no
`RenderEffect`/`BlurEffect`, no `graphicsLayer` in
`TactileDeckScreen.kt` at all; the only "blur" tokens in the app
are `blurRadius = 0f` text shadows (Deck :216, :224) and one
`graphicsLayer` in FocusSequencerScreen.kt:392 (unrelated).
Wren §2.1 confirms from his run. **The defocus is a build, not a
setting.**

**What it IS, in spec (Wren §4.4, the only filed definition):**
at E1200, CUT 0ms — the whole idle Deck, fully composed at final
positions, rendered defocused: **blur 9dp · scale 1.012 · one tone
step down**. RACK 280 from R0 = max(E1200, P): blur 9→0 on
`CubicBezier(0.32, 0, 0.18, 1)`; planes stagger — primary (wheel +
slide) sharpens from R0, **secondary (task line, receipt line) at
R0+50**, tertiary meta at R0+100; settle hunt 1.0→0.9985→1.0 in
the last 50ms; landing tick 6ms @ 0.35 at blur passing 1dp
(≈R0+230); nav detent jumps 0ms at rack end.

**What it is in code (my build definition):** it is *all three*
of the brief's candidates, as one sampled value —

- **Blur (the body of it):** one `Animatable<Float> rackT` owned
  by the Deck's finish host, sampled per frame; each plane wrapper
  applies `Modifier.graphicsLayer { renderEffect =
  BlurEffect(radiusX = radiusY = 9dp·(1−rackTₚ), …) }` where
  rackTₚ is rackT delayed by the plane's offset (0 / 50 / 100ms).
  One clock, three samples — picture and defocus cannot desync
  (the Vertigo's own discipline, Wren §3.2).
- **Scale:** the primary plane's graphicsLayer carries
  `scaleX = scaleY = 1.012 − 0.012·rackT` plus the settle hunt in
  the last 50ms.
- **Tone step (a scrim, concretely):** a full-Deck overlay of the
  ground colour at alpha sampled from the same rackT. **The step's
  magnitude is filed nowhere** — Wren says "one tone step down"
  and no seat has priced its alpha. My proposal, for Mara/Floor
  ratification, not asserted as law: ground scrim α 0.12 at the
  cut → 0 at rack end, linear in rackT. Priced below; if the
  ratification changes the constant, the build does not change.

**Platform wall, named:** `app/build.gradle.kts:17` —
**minSdk 24**. `RenderEffect` blur is API 31+; Compose's
`Modifier.blur` is a no-op below 31. Fallback, pre-named (the
Wren §6.1 discipline): on API <31 the defocus is carried by the
tone scrim + scale alone (α raised to 0.20 on those devices so
the cut still reads as a focus change, never as nothing), and the
wheel — an `AndroidView` — keeps Wren's filed fallback on all
APIs if the device pass janks: its blur freezes at a fixed 2dp
and scale/alpha carry its plane. The rack's *timing* is untouched
by either fallback.

**Price inside E4's share.** Wren's E4 price (≈410) already
carries "Deck held-frame composition + CUT/RACK host ≈70 (rack
machinery built once, ≈110 gross, shared with nav)". Not in any
filed price: the plane wiring for *my* two secondary elements and
the scrim. My increment, inside E4:

| Item | Net |
|---|---|
| Secondary-plane wrapper (task line + receipt line, R0+50 sample) | ≈14 |
| Tone scrim (overlay + rackT sample + API<31 α variant) | ≈8 |
| API<31 fallback branch (tone+scale defocus, blur skipped) | ≈10 |
| **Increment to E4** | **≈+32 net** (E4 ≈410 → ≈442) |

---

## 5 — THE RECEIPT'S INLINE HOME (Q4): FORK F-L1

MASTER §5.7: *"One flat slip, inline on the Deck's end state — no
overlay, no archive."* Wren §4.3: during detention the receipt is
*data in the VM, not pixels*; its faulted form rises as "an
aperture sheet over the still-defocused Deck". Emil's receipt
composition is excluded from E4's price and **no seat has ever
booked the slip a height**. I measured it.

### 5.1 The slip's height, line by line (filed type only)

Plate lines lh12, body lines (Atkinson 15sp) lh20, beats 8,
slip padding 12 top/bottom, one top rule:

| Line | dp |
|---|---|
| Top rule + top padding | 13 |
| Header: `№ 048` · arm/seal timestamps (plate) | 12 |
| Beat | 8 |
| Task line: title as at sealing + true length (body) | 20 |
| Beat | 8 |
| Counts line: loadout as sealed (plate) | 12 |
| Beat | 8 |
| Footer: `THE MACHINE KEEPS ITS OWN LOG · YOUR STUDY RECORD LIVES IN YPT` (plate; 62 chars ≈ 366dp at 10sp — one line at the 372 measure, two at any narrower slip) | 12–24 |
| Bottom padding | 12 |
| **Fixed core (zero deflections, no conditional lines)** | **≈104** |
| Routine line, when true (`WITHIN FASTED BLOCK` form) | +28 |
| Banked-break line, when true (seconds printed separately, never folded) | +28 |
| Death line, when true (`THE SHIELD WAS OFF FOR PART OF THAT SESSION`) | +28 |
| Deflections: header `AS LOGGED BY THE SHIELD` +12/beat, rows 20 each | +20 + 20n |
| `STILL BLOCKED`, per faulted link (≤3, faulted branch) | +28 each |

**Typical slip** (3 deflections, nothing else true):
104 + 20 + 60 = **≈184dp**.
**Fullest happy-path slip** (routine + break + death + 3
deflections): **≈268dp**. Faulted slips exceed 288dp at three
STILL BLOCKED lines — which is consistent with, and only with,
the faulted branch already being a sheet (Wren §4.3).

### 5.2 Every candidate slot in the 821 sheet, measured

| Candidate | Room | Verdict |
|---|---|---|
| **The reserve** (H−821) | 23dp @ H=844 · 75dp @ H=896 | **Unlawful at any height.** §2.2: "The reserve is empty, always — no element may enter it." And insufficient anyway: 75 < 104 minimum slip |
| **The sled's vacated track** (slide 72) | 72dp | **Occupied at the only moment that matters.** At receipt t=0 (E1480) the sled is still seated (Fen: RECEIPT state, thumb 1.00, nobody owns it); it vacates only E2480–E3130, *through* this track. A slip hosted here either strikes under a seated sled or must lift at E2480 mid-reading. And 72 < 104 regardless |
| **Task line register** (20) | 20dp | The slip's provenance *includes* the task; the register fits one of its lines. No |
| **Strip band** (53) | 53dp | Tenanted again from the cut (strip returns at receipt, D6); 53 < 104. No |
| **NEXT slot** (20) / header slot (20) / sentence slot (22) | 20–22dp | Fixed registers with their own tenants/laws. No |
| **Bottom stack wholesale** (task 20 + S20 + strip 53 + S6 + slide 72 + S8 + slot 22 + S20 = 221) | 221dp | Fits typical (184), not fullest (268); and it is not a slot — it is four living tenants (strip live, sled in transit, sentence slot reserved) that an end-state reflow would have to evict and later restore. See option A |
| **Wheel zone** (288) | 288dp | Fits every happy-path slip (≤268). The only single lawful-sized vacancy on the screen. See option B |

**Conclusion: on the Deck as specced, the receipt has no lawful
room.** This is not a squeeze problem — 796's disease — it is an
unbooked element: the room was summed for an idle Deck and a
running Deck, never for an *end-state* Deck carrying a slip.

### 5.3 FORK F-L1 — for Ren, four options, priced

- **A · End-state reflow.** At the cut, the bottom stack retires
  as one block and the slip hosts in its 221dp; strip/slide/
  sentence return at rest. Casualties: the sled's return path
  crosses the slip's zone (Fen's contract needs a re-route or a
  pre-receipt return, re-opening F2); the strip is absent during
  the very detention its BLOCK cell is the one live witness for
  (§2.2); slips >221dp (fullest happy path) still do not fit and
  need a deflection-row cap §5.7 does not grant. ≈+85 net +
  Fen re-price. **Not recommended.**
- **B · Wheel-zone host.** The slip hosts where the wheel stood:
  at the cut the wheel ghost retires instead of being "found at
  scale 1.0", and the slip (≤268dp) occupies the 288 zone under
  the hero until the next arm or nav-away; the wheel returns,
  settled, when the slip is superseded. Casualties, named:
  Wren §4.4's "wheel found at rest in defocus" dies (the rack
  sharpens a slip, not a wheel — arguably the rack's secondary
  plane *becomes* the slip, which fits its R0+50 sample suspiciously
  well); §2.7's eye-order (total → slide → task) is an idle-Deck
  law and survives for idle, but the end state loses the wheel
  from its picture. ≈+35 net (host + wheel retire/return, no
  strike replay — keyed once-ever per §5.7/Fen). **Runner-up;
  the only option that keeps "inline, no overlay" literally
  true.**
- **C · Generalise the aperture sheet.** §5.7's "no overlay" is
  struck; the slip rises as a sheet over the Deck in *all*
  branches — the form the faulted branch already lawfully uses
  (Wren §4.3) and the form MASTER §6.5's surviving-motion grammar
  already assumes for it (240ms rise, one-gesture dismissal,
  seat-before-rise). The strike (220ms beat → 24dp rise over
  380ms → type in one 110ms blow) is sheet grammar already; only
  the word "inline" was ever layout grammar. The Deck beneath
  stays the full idle Deck — wheel, strip, sled return all
  exactly as specced, zero casualties in §§2–4 of this file.
  ≈+60 net (sheet host + dismissal + once-ever keying; the
  faulted branch's sheet work in E4 is reused, not duplicated).
  **RECOMMENDED** — the cheapest option that costs no other
  seat's law.
- **D · Shrink the slip to the task+strip zone (99dp).**
  Provenance core only on the Deck; deflection itemisation
  dismissed to the record. Violates §5.7's "it carries, and only
  carries" (itemisation is mandatory content, headed, on the
  slip). **Rejected by me, filed for completeness.**

**Until F-L1 is ruled, my build assumes only this:** the receipt
*line* (the secondary-plane element the rack sharpens at R0+50)
is the slip's header line, hosted wherever the ruling puts the
slip; §4's plane wiring is identical under A, B, or C.

### 5.4 The slip's exit (unfiled everywhere; my frame ruling)

No filed text says when the slip leaves. Fen: the receipt has no
dismiss verb; §5.7: it is a *moment*, held in a single
`lastReceipt` slot overwritten by the next session. Ruling for
the frame (under any F-L1 option): the slip **persists at rest**,
settled, with **no strike replay ever** (keyed by sessionId to
the persisted transition — reopen cannot re-fire it); it leaves
on the next arm's DOWN (the PROVING state supersedes it) or on
nav-away, and a return to the Deck re-renders it settled. Price:
inside the host options above; no separate line.

---

## 6 — PRICE ROLL-UP (my joint only)

| Item | Net |
|---|---|
| FinishSnapshot (VM: title/length/queueCount at E0) + collector gate (`finishState == IDLE`) | ≈25 |
| Task line end-state branch (snapshot read, micro-line, live re-derive at rest) | ≈30 |
| Input death Layer 1 (targets absent; DONE/steps uncomposed) | ≈18 |
| Input death Layer 2 (VM gate incl. detention extension, 3 call sites) | ≈12 |
| Defocus increment inside E4 (§4: secondary plane, scrim, API<31 fallback) | ≈+32 |
| Receipt host | **FORK F-L1** — A ≈+85 / B ≈+35 / C ≈+60, unpriced into the slice |
| Tests (§7) | ≈45 lines |
| **My share, ex-fork** | **≈117 net + 45 test** |

Excluded by name: ghost internals (Rad/Zug-II), reverse cascade
(Opal/E4), ReceiptState itself (Fen/E6), Emil's receipt
*composition* (his), the strip's re-measure (D6/D9).

## 7 — ACCEPTANCE TESTS

- **T1 (snapshot integrity):** complete the session's task from
  Tasks during E1200–E2480; micro-line and receipt still print
  the E0 title; at E3130 the line re-derives in one cut.
- **T2 (collector leak):** emit a new activeTask mid-detention;
  hero, dial, and intention do not move before rest.
- **T3 (no target):** UI-tree assertion — in sealed and end-state
  branches the task line subtree contains zero clickable/semantics
  targets; DONE is absent from the tree, not disabled.
- **T4 (VM gate):** `completeTaskAndAdvance` /
  `toggleTaskSubStep` / `deferTask` during RELEASING and RECEIPT
  write nothing (DAO row unchanged), produce no banner.
- **T5 (skip purity):** a tap on the task line's bounds during
  E0–E1200 produces exactly the skip (cut) and zero task writes.
- **T6 (pulse invisibility):** screenshot-diff E699 vs E700 is
  empty; E0 vs E0+150ms differs only by the arc's final segment.
- **T7 (defocus planes):** at R0+25 the task line's measured blur
  equals the primary plane's R0 value delayed 50ms (±1 frame);
  scrim alpha reaches 0 at rack end; on API 24–30 the cut still
  produces a measurable tone step (scrim α 0.20 path).
- **T8 (imprint order):** SEALED tenant present at E1199, IDLE
  tenant by E1350, lip alpha 0 by E1440, receipt strikes E1480.
- **T9 (sled contract):** thumb fraction is 1.00 at every sampled
  frame E0–E2480; return spans E2480–E3130; a cap DOWN mid-return
  captures from the frozen fraction (Fen §3d, re-asserted here
  because the frame hosts it).
- **T10 (once-ever):** process death at E1500; relaunch renders
  the receipt settled, strike not replayed, sled at its
  ReceiptState-derived position; sequence № unchanged.

## 8 — RISKS

- **R-L1 · F-L1 blocks the receipt host, nothing else.** §§2–4
  build without it; a builder who "finds room" by squeezing the
  reserve or the strip has repeated 796. The fork is the
  deliverable, not a blocker to route around.
- **R-L2 · Chrome unlock vs detention.** §6.5 unlocks chrome at
  session end; my detention freeze assumes he *can* leave and
  edit tasks mid-detention and the Deck must not care. If Ren
  instead rules travel dead until rest, T1/T2 get easier and
  §6.5 gets an amendment — either way the snapshot build is the
  same.
- **R-L3 · minSdk 24.** Half the defocus's body (blur) is
  API 31+. If his device is <31, the filed RACK reads as tone +
  scale only — lawful per the pre-named fallback, but the
  device pass must say which world it measured.

## NEXT

- **What I want next:** the F-L1 ruling's build — host the slip
  under whichever option Ren picks (B or C), with the rack's
  secondary plane as its entrance; then the Tasks-side half of
  the detention gate with Zeile (his rows, my predicate).
- **What I could do unassigned:** measure the slip against real
  deflection counts from the intercept log (is n=3 typical or
  generous?); audit the AOD finish step (§5.8.8) against this
  inventory — its tap "goes to the Deck, where the receipt
  lives" and nobody has checked what that tap lands on during
  detention.

— Linea, Improvers · the Deck task line
