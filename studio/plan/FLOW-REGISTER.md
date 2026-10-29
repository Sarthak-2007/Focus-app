# THE FLOW REGISTER — founding edition

**Keeper:** Ablauf, lead of the Flow Team (charter: plan/FLOW-TEAM-CHARTER.md).
**Filed:** Oct 29, 2026, ~22:15 IST. **First commission:** the blocking-start
flow — Sarthak's words: *"Add when blocking starts what the flow. Like what
screen does app change. Now its just same app exactly but turned black. Does
desk clock move to not burn in does it shows time and time left progress
maybe tasks etc and what not."*

**Teeth (charter):** every build programme and design wave answers this
register item by item — built, scheduled, or ruled out by name. Nothing
dies by silence. Paper only in this edition: no code was touched.

**Method for §1:** every claim read out of trunk
(~/workspace/clone-inspect/Focus-app) tonight and cited file:line.
Salz's light figures are taken from his desk (plan/REFINE-salz-aod.md),
computed from the same trunk tokens.

---

## §1 · TODAY'S TRUTH — what actually happens when blocking starts

### 1.1 The seal, second by second (hand arm, app in his hand)

| When | What happens | Where |
|---|---|---|
| The slide commits (or the tap commits) | The attempt latches. The proving cascade starts: three links must be witnessed — the session row, the shield service, the accessibility arm — on the cascade's own clocks. | ArmProvingController.kt (deadlines :694–697) |
| Latch +0–500 ms | The Deck is in the seal **shot**: scroll locked, task taps dead, navigation refused — the frame is detained while the machine proves itself. If a link has not answered by latch+500 ms the attempt is declared **jammed** (one shield re-dispatch). | TactileDeckScreen.kt:503, :751, :772; MonolithViewModel.kt:1232 (navigationLocked); SessionSealTransition.kt:52–53 |
| All links witnessed → **t=0** | The seal act runs in one fixed order — claim the row → seal the row → publish the sealed state → **the Throw sounds, last, exactly once**. A seal that cannot complete never sounds. The shield service starts; blocking is live from this instant. | SessionSealTransition.kt:12–19; ArmProvingController.kt:196 (startShield) |
| t=0 → t=0+1050 ms | The shot lands. Navigation stays locked until the shot completes; the utilities row (DESK CLOCK · HELD) takes its place at Settle — during the shot it is absent, not disabled. | MonolithViewModel.kt:1232–1247; TactileDeckScreen.kt:838–845 |
| A link still dark at latch+1500 ms | **Annulment**: the row is deleted, the attempt dies, the Deck returns to its idle face with a past-tense sentence. No Throw ever sounds for it. | SessionSealTransition.kt:52–57; ArmProvingController.kt:645 |
| After the shot | The Deck wears its **running face**: eyebrow `SESSION RUNNING`, hero = the current block's remaining time, the task line with its DONE verb, the block band (`n APPS HELD`), the utilities row. And the whole app — every destination — is forced to the night palette for as long as the session is active. | TactileDeckScreen.kt:556–558, :308; MainActivity.kt:229, :242 (`sealed` → `effectiveNight`) |

### 1.2 What does NOT change — the complaint, verified

Sarthak is right, and the code says so plainly:

- **He goes nowhere.** The destination never changes at a seal. He is on
  the Deck before the slide and on the Deck after the Throw. No surface
  succeeds the Deck; nothing "becomes" anything. (MainActivity.kt — the
  seal publishes state; no navigation call exists in the seal path.)
- **The one visible transformation is a repaint.** `effectiveNight =
  darkTheme || sealed` (MainActivity.kt:242): sealing forces the night
  scheme app-wide. Same layout, same screen, turned black — his sentence
  is the exact technical description.
- **PARK closes against him while sealed** (openPark reads the same
  door predicate, MainActivity.kt:275 ff.) — the settings drawer is not
  part of the sealed world at all.
- **The screen still sleeps.** Nothing in the seal path holds the
  display awake. When the OS timeout fires, the session's standing face
  is a dark, sleeping phone. The session has no face of its own.
- The shield service does post its standing foreground notification —
  "Monolith Locked / Protecting your focus session..."
  (MonolithShieldService.kt:62–65) — in the shade, where he has to go
  looking for it. That is the only new surface a seal creates.

### 1.3 The desk clock — full audit (Uhr's seat)

The desk clock exists: `AlwaysOnDeskClockOverlay`,
ui/components/AirlockAndDisarmOverlays.kt:68. It is an in-app Compose
overlay composed at MainActivity.kt:720–727 — it can only ever show
while Monolith is in the foreground.

**When it can appear — by hand only, and only mid-session.**
The single raiser in the entire tree is the Deck's `DESK CLOCK` chip
(TactileDeckScreen.kt:845–856), wired at MainActivity.kt:510, and the
chip itself renders only while a session is running and no shot is
active (`isSessionRunning && !shotActive`). Consequences:

- **Sealing never raises it.** No seal, finish, routine, or timer path
  writes `true` to the visibility flow (MonolithViewModel.kt:185–186,
  default `false`; the toggle is at :1378–1380).
- **Idle, it is unreachable** — with no session running there is no chip,
  so the overlay's idle branch can only be met in edge instants (a
  session row present but no longer active under an already-open clock).
- The VM only ever **hides** it: at the finish edge
  (MonolithViewModel.kt:1051), on disarm (:2075), on a completed
  emergency break (:2102). The machine knows how to close the clock and
  never how to open it.

**Burn-in — real, and it moves.** While visible, the clock holds the
screen awake itself (FLAG_KEEP_SCREEN_ON set on show, cleared on
dispose, AirlockAndDisarmOverlays.kt:84–100). The whole column steps on
a 5×5 grid: `dx = ((step % 5) − 2) × 3`, `dy = (((step / 5) % 5) − 2) × 3`
(:130–131) — offsets −6…+6 dp in 3 dp steps, 25 positions. The step
advances once per 60 ticker seconds (MonolithViewModel.kt:831,
mod 360). Salz's desk acquits the mechanism: it spends nothing and
saves the panel (REFINE-salz-aod.md §1). One softness: the step lives
in the VM's ticker, so the grid only advances while the app process
lives — true whenever the clock is on screen, since the clock is
in-app. Not a defect; named so nobody "fixes" it later.

**What it prints, state by state** (all figures AirlockAndDisarmOverlays.kt):

| State | Hero numerals | Progress strip | Banner | Notes |
|---|---|---|---|---|
| **Running, timed** | **Span** remaining (`spanRemainingSeconds`, :102–108) — the whole session incl. breaks, never the block's | Span elapsed / span target, floored at 0.04 so it never reads empty (:118–122) | Active task title, else the session's intention, else **"Deep Focus Session"** (:198–203) | The Deck hero meanwhile counts the **block's** remaining (:308) — two faces, two remainders, both true, never labelled as different |
| **Running, stopwatch** | Elapsed seconds (:107) | Elapsed / 1200, pinned full from minute 20 (:123–124) | Same as above | — |
| **Break** | Span remaining, unchanged | Unchanged — the break earns **no rendering at all** on this face (deliberate in code, :113–117: Nacht's law — the break's one rendering is the Deck's `BREAK · n MIN` eyebrow, TactileDeckScreen.kt:556–557) | Same | A man who picks the phone up mid-break cannot tell from this face that the doors are open |
| **Idle** | **`dialMinutes × 60` formatted as a time** (:109–110) — the composer's dial setting wearing a clock's clothes: Tief's ST-01 lie, alive in code | 0 (honest, :127) | Intention or the fabricated "Deep Focus Session" | No face in the app prints the **current time** — not this one, not any (ST-01, plan/SMALL-THINGS.md:31–36) |

Plus, in all states: the exit line `NIGHTSTAND DESK CLOCK • TAP
ANYWHERE TO EXIT` (:150–157); tap anywhere dismisses to the Deck
(:141). Nothing else prints. No current time, no end time, no date.

**What it costs in light (Salz, REFINE-salz-aod.md §§1–2).**
Over the SLEEP span (7.5 h, timed branch) the overlay spends
**0.2734 full-screen-luminance-hours**: numerals 71.6%, strip fill
17.9%, banner text 5.2%. Numerals, strip fill and banner text all print
at NightColorPrimary full strength — computed **0.8125** relative
luminance against Nacht's 0.25 ceiling for the night face: a **3.3×
breach**, all night, at reading distance. Even the exit line's
dimmed 0.6 alpha composites to 0.2607 — over, by 0.011 (cure computed:
alpha 0.58 → ≈0.246). The ground is true black and acquitted. As
shipped, the clock is a kind clock to the panel (burn-in) and an
unkind one to the room: raised at 20:00 it is the brightest lawful
object in a dark bedroom, and it gets brighter — the strip's share
grows all night and the frame sum rises +39% first hour to last.

---

## §2 · THE DESIGNED FLOW — what the app becomes when blocking starts

One sentence: **you seal the phone, and the phone becomes a clock on a
desk.** The Deck is the workshop where a session is made; the desk
clock is the room the session is lived in. Today the room exists and
nobody is ever taken to it. The flow below is the walk.

### 2.1 The moment, beat by beat

- **The slide / the tap (second 0).** Unchanged. The proving cascade,
  the shot, the detention — that machinery is honest and stays. The
  Throw sounds at t=0 as it does today: claim → seal → publish →
  Throw. Seal physics and the seal's voice are not this team's to
  touch.
- **The shot lands (t≈1 s).** The Deck's running face stands: eyebrow,
  block countdown, `n APPS HELD`, the task line. It holds long enough
  to be read — this is the proof that the machine did what it said.
  Nothing is taken from him yet.
- **The settle (t≈20 s untouched).** If his hand leaves the phone —
  no touch on the Deck for 20 seconds after the seal or after his
  last interaction, whichever is later — the desk clock fades in over
  the Deck (the overlay's existing specB fade, 420 ms). The app has
  changed rooms. He puts the phone down and what stands on the desk
  is a clock that knows his session.
  - Why not at t=0: the first seconds after a seal are working
    seconds — he tears a task, reads the held count, sets the phone
    down in his own order. A face that seizes the screen at the Throw
    would fight the hand that just armed it. The clock arrives when
    the hand leaves, which is the moment it is for.
  - Why 20 s: longer than a task-tear and a glance, shorter than any
    default screen timeout race that matters — and if the OS sleeps
    the display first, nothing breaks: the wake-up rule (2.3) covers
    the pickup.
- **Hour three.** Nothing changes, and that is the design. The strip
  has filled; the column has walked its grid a hundred and eighty
  times; the face is still. A session's middle should be the quietest
  object in the room (Stille's hush). No new information arrives
  mid-session because no new information exists: remaining, progress,
  the task. Stillness is not absence of design; it is the design.
- **The last minute.** The strip stands full. The exit line gives its
  place to a fact the row already knows: `ENDS 22:55` — the span's
  end, in wall time, the one number a finishing man wants. Print-layer
  only; the end wall time is on the session row.
- **The finish.** The clock closes itself — the VM already hides it at
  the finish edge (MonolithViewModel.kt:1051); the flow keeps that
  law. What stands underneath is the Deck and the ending's own
  surface: the detention, then the numbered receipt, which stands
  until he dismisses it (the shipped F-B1 law). The clock never
  outlives its session by a second.

### 2.2 The desk clock's role — SETTLED: automatic, on three laws

1. **Raise once per session**, on the settle (2.1), and **once at a
   cold open** into a live session (2.3). Never repeatedly.
2. **A dismissal is a decision.** If he taps the clock away, it stays
   away for that session. The DESK CLOCK chip remains on the Deck for
   re-entry by hand. The machine does not argue with the hand.
3. **It is a read face.** No verbs live on it except leaving: tap
   anywhere → the Deck, where the task's DONE and every other verb
   already live. The clock shows; the Deck does.

Why automatic at all: because a study display that must be fetched
through a chip, on a screen he may never look at again for three
hours, is not a display — it is a rumour of one. His Oct 29 ruling
kept the desk clock **as a study display**; a display's whole job is
to be what the phone is showing. The machinery cost is near zero
(2.6): the overlay, the visibility flow and the keep-screen-on flag
all exist — the seal path simply never writes `true`.

### 2.3 Pickups — the middle of the flow

- **He wakes the phone mid-session** (screen slept, app in front):
  the face he left is the face he gets — the clock if it had raised,
  the Deck if he had dismissed it. No re-raise, no surprise.
- **He opens Monolith cold mid-session** (app was closed/swiped):
  RootApp already re-derives the live session from the stored row;
  the flow adds one line to that derivation — **the clock is the
  first surface**, the Deck one tap beneath it. Opening the app
  inside a block should land in the session's room, not its
  workshop.
- **He picks it up mid-break:** the clock's banner gains the break's
  one honest line — `BREAK — DOORS OPEN · n MIN LEFT` — from state
  already on the row (`isInSafeBreak`, `safeBreakSecondsRemaining`,
  MonolithModels.kt:283–284). The span hero does not change (Nacht's
  law stands: the clock tells session time); but a man holding the
  phone in a break must be able to learn, without leaving the face,
  that the doors are open and for how long. Today he cannot (§1.4).
- **He opens a blocked app:** unchanged — the Held Door is that
  moment's surface, designed and audited elsewhere. The flow does not
  touch the door.

### 2.4 What the clock prints — the designed face

Print law (what it shows) is look-independent; each look skins it at
its own merge. Against the shipped face, the flow changes four prints:

1. **The current time joins the face.** A small line under the hero —
   the actual wall-clock time. ST-01's cure on the surface that needs
   it most: at 03:30 the first question is *what time is it*, and
   today no face in the app can answer. (Print-layer: the time
   exists everywhere; it is simply never printed.)
2. **The idle lie dies.** Idle, the hero must never again wear the
   dial setting as a time. Under this flow the idle clock is barely
   reachable at all (it raises only into live sessions), but the
   branch is rewritten wherever it survives: idle prints the current
   time, labelled as itself, or nothing.
3. **The fabricated banner dies.** No task and no intention → the
   banner prints nothing (absent, not invented). "Deep Focus
   Session" is a name the app made up for his work; Una's held-door
   appendix caught the same habit in the desk clock's banner and it
   ends here too.
4. **The light is brought under law.** Numerals, strip fill and
   banner text composite down to Nacht's ceiling for this surface —
   the 55% composite two desks have already computed (≈0.216, under
   0.25) — and the exit line takes Salz's one-constant cure
   (0.60 → 0.58). An automatic all-night face at 0.8125 would be a
   lamp, not a clock; the cure is priced in constants (2.6) and is a
   condition of the auto-raise shipping at all.

### 2.5 Routine-born sessions — the flow when no hand seals

At 05:00 (FASTED BLOCK) and 20:00 (SLEEP) the seal fires with nobody
holding the phone — and on the audited evidence (plan/
ROUTINE-NOFIRE-AUDIT.md) the firing chain itself is the subject of
the F1–F5 fix programme; this flow assumes a seal that lands. When it
lands: nothing can or should light the phone in an empty room. The
designed answer is deferred to his first touch: the shield's standing
notification already says the phone is locked, and the first open of
Monolith lands on the clock (2.3). For SLEEP specifically, the clock
under this flow is the nightstand face for the whole span — current
time printing, light under law, walking its grid — and at 03:30 the
span ends, the finish edge closes the clock, and the stand-down is
the plain deactivation it is today (RoutineHeartbeat.kt:125–128).
The first designed surface of his day is the morning Deck he picks
up; designing *that* face is a register seed (FL-11), not this
commission.

### 2.6 Machinery — priced honestly

| Piece | Price | Basis |
|---|---|---|
| Auto-raise on the settle | **SMALL** | One flag write (`_isAlwaysOnDeskClockVisible = true`) on a 20 s no-touch condition inside the VM's existing per-second ticker (MonolithViewModel.kt:820 ff.); touch stamps already flow through the Deck's interactions. The overlay, fade and keep-screen-on exist. |
| Cold-open raise | **SMALL** | One derivation at RootApp composition: live session row present + clock not dismissed this session → visible. The dismissal law needs one remembered bit per session id (in-memory is enough; a cold process may re-raise once — acceptable, and stated). |
| Break line on the banner | **PRINT-LAYER** | Both fields are on the session row (MonolithModels.kt:283–284); the Deck already derives the same sentence for its eyebrow (:556–557). |
| Current-time line + idle cure + banner absence | **PRINT-LAYER** | Formatting and branch edits inside the one overlay composable. |
| `ENDS HH:MM` last-minute line | **PRINT-LAYER** | The span's end wall time is derivable from the row the overlay already receives. |
| Light cure to ≤0.25 | **CONSTANTS** | Alpha/composite on three prints + the exit line's 0.60→0.58 (Salz). No machinery. |
| Explicitly **not** priced, because not needed | — | No new service, no system window (the overlay is in-app by design — a session's clock belongs to the app's foreground; when he is in another app the doors, not the clock, are the surface), no new store, no DB change. |

### 2.7 Refused, on the record

- **Raise at t=0, under the Throw.** Fights the working hand (2.1).
- **A separate break face for the clock.** Breaks are regions of the
  span, not rooms; one line on the banner carries the fact.
- **Anything counted, scored or streaked on the clock.** A blocker,
  not a tracker — his standing verdict. The clock shows a session,
  never a judgement.
- **Raising the clock over other apps** (a system overlay clock).
  The pill's seat (Freya's spec, plan/PILL-AND-TIMES.md) owns the
  in-other-apps glance; the clock's kingdom is the app's own
  foreground.

---

## §3 · THE REGISTER — founding finds and seeds

Each item: the moment, the find in one line, where today's answer
lives (or that none exists). Programmes answer item by item — built,
scheduled, or ruled out by name.

**From the first commission (the blocking-start flow):**

- **FL-01 · Seal.** A seal changes no surface: the user goes nowhere;
  the app's only transformation is a forced night repaint
  (MainActivity.kt:242). The designed flow is §2 of this register.
- **FL-02 · Desk clock.** The study display exists but nothing ever
  raises it — its visibility flow is written `true` by exactly one
  hand-tap (TactileDeckScreen.kt:850) and `false` by three machine
  paths (MonolithViewModel.kt:1051, :2075, :2102). It opens only
  inside a running session, so the display is absent from every
  moment it was kept for.
- **FL-03 · Desk clock, idle.** The idle hero prints the composer's
  dial setting formatted as a time (AirlockAndDisarmOverlays.kt:
  109–110) — Tief's ST-01 — and no face in the app prints the current
  time at all. Answer today: a lie, in code. Cure designed §2.4.
- **FL-04 · Desk clock, light.** Used all night, the shipped clock
  spends 0.2734 luminance-hours at 3.3× the night ceiling (Salz,
  plan/REFINE-salz-aod.md) — the all-night face exists only by hand
  and burns bright if used. Any auto-raise ships only with the §2.4
  light cure.
- **FL-05 · Mid-session, screen asleep.** Once the OS timeout fires,
  a running session has no standing face at all — the seal never
  holds the display, and no surface succeeds the Deck. The §2
  settle + pickup rules are the first designed answer.
- **FL-06 · Desk clock banner.** With no task and no intention, the
  banner fabricates a session name — "Deep Focus Session"
  (AirlockAndDisarmOverlays.kt:200). Same habit Una's appendix
  caught in this overlay's family. Absence is the honest print.

**Seeds — the other moments, one line each:**

- **FL-07 · Routine firing.** A routine-born seal has no designed
  moment at all: the chain fires (or, per the no-fire audit, annuls
  itself) in the background, and the first evidence is a locked phone
  or a standing notification. Answer lives in RoutineHeartbeat.kt +
  plan/ROUTINE-NOFIRE-AUDIT.md (fix spec F1–F5, unbuilt); the
  *surface* of a firing is undesigned.
- **FL-08 · Break start.** No event exists: a break is a derived
  region of the span (MonolithModels.kt:391–398), so nothing marks
  the flip on any surface — the Deck's eyebrow merely reads
  differently next time he looks. ST-03's boundary voices are ruled
  in at the Small Things register and unbuilt.
- **FL-09 · Break end.** The same silence in reverse: the doors
  re-close with no knock, no print change anywhere except the
  eyebrow's next read. ST-03 covers both flips; neither is built.
- **FL-10 · Peek start / end.** A peek writes a 120-second lease to
  no row and no surface counts it (Repository's lease,
  MonolithViewModel's tick-counted pill state with no surface —
  Freya, plan/PILL-AND-TIMES.md); ending it early is unreachable
  code. The designed answers live in Freya's pill spec (unbuilt) —
  the *moment* of the peek, start to end, is currently lived blind.
- **FL-11 · 03:30 wake.** The SLEEP span ends and the stand-down is
  a plain deactivation (RoutineHeartbeat.kt:125–128); no morning
  surface is designed — the first thing his day meets is whatever
  face he left at 20:00. Alltag's seat owns this one next.
- **FL-12 · Finish + receipt.** The one fully designed ending in the
  app: detention → a numbered receipt that stands until dismissed
  (MainActivity's F-B1 law), and the desk clock already closes
  itself at the finish edge (MonolithViewModel.kt:1051). Answer
  exists; filed so the register records a pass, not only failures.
- **FL-13 · App opened mid-session.** RootApp re-derives the live
  session from the stored row and lands him on his last destination
  as if nothing had happened — no designed "you are inside a block"
  landing exists. §2.3's cold-open rule is the proposed answer.
- **FL-14 · The phone on the desk all night.** Unless the clock was
  raised by hand during an evening session, the phone spends the
  night dark and asleep under a SLEEP seal — which may be exactly
  right; nobody has ever designed the question. Uhr + Alltag to
  walk it before the next edition.

---

*Filed by Ablauf, Flow Team lead. The register stands open; the
team's seats (Anfang, Mitte, Ende, Uhr, Schirm, Uebergang, Alltag)
take their moments from §3 in the next seating. Obviousness pass
owed on §2 before any build ships it: if Sarthak would have had to
ask for a beat in it, the beat does not ship until it is in.*
