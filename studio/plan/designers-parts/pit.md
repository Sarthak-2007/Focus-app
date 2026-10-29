# PIT — Cockpit seat, Designers cohort (coordinator: Sana)
## PLAN WAVE — the RUNNING SESSION composition spec. No code.

Sarthak's order stands over this file: citations lifted, full freedom inside
the settled law. That law, as it binds this screen: Concept = NONE · ARM ≤10s ·
**HOLD absolute — the running session is a cloister: nav locked, editing
locked, no decisions exist mid-session** · TRUTH once — one fact, one
rendering. This screen is night-scheme at noon: the running session forces
the armed-lockdown scheme regardless of the clock or the app theme.

**Tokens, locked, used throughout and nowhere contradicted:**
ground `#171412` · recess `#100E0C` · raised `#362C27` ·
text `#D2C6BB` (primary) / `#9C8E84` (secondary) / `#776A60` (tertiary) ·
glyph `#EDE3D8` · margin **20dp** · gaps only from **4/8/12/20/32/48** ·
radius **8/16** (this screen uses 8 only; nothing here earns 16) ·
accent: **NONE — except the armed dot.** I do not argue that clause; §7
says why the cockpit *wants* it.

Type rides Set's frozen ladder {104, 72, 50, 22, 15, 10} — this screen
spends exactly four rungs: **72** (hero), **50** (hold-overlay countdown
only), **15** (every word and second number), **10** (one plate label).
No size is invented here.

---

## 1. The composition — five things, in fixation order

The running screen shows exactly five elements. Everything else that the
idle Deck carries — nav, wheel, slide, chips, pills, NEXT ribbon — is not
dimmed, not greyed, **not drawn**. A locked panel that still draws its
controls is lying about what it can do (my wave-1 law, unchanged).

Vertical stack, 360dp reference width, content 320dp inside the 20dp
margins. Heights and gaps are the spec; on taller screens the surplus
falls as empty ground *below* zone 5 — nothing is added, nothing stretches
except the void.

| Zone | Content | Height | Gap after |
|---|---|---|---|
| 0 | Status strip — armed dot + state word | 48dp | 12 |
| 1 | Current task line | 40dp (reserves 2 lines) | 32 |
| 2 | `REMAINING` plate + hero numerals | 12 + 8 + 72 | 20 |
| 3 | Stepped arc, Ø280dp, emergency valve nested in its gap | 280 | 20 |
| 4 | Second reading — elapsed · finishes | 20 | — |
| 5 | Void (ground, defended) | remainder | — |

Stack total before the void: **584dp**. On a short viewport the gaps
compress in this order only: 32→20, then 20→12. Zones never compress.

### Zone 0 — status strip: the state, stated once, in two channels

- Left-aligned on the margin axis: **armed dot, 6dp, `#F47442`** — the
  screen's only saturated pixel-area, static, never pulsing. Beside it,
  8dp gap, the state word **`RUNNING`**, Atkinson 700, 15sp, +0.5
  tracking, text-primary `#D2C6BB`.
- Dot + word is not a TRUTH-once violation: they are two *channels* of
  one state (position/colour + word), the redundancy the accessibility
  floor demands of every state in this app. The *fact* is stated once;
  it is confirmed twice, which is a different thing.
- Nothing else lives in this strip. No clock, no HELD/GUARD counts, no
  date. Chrome is not content, and mid-session there is no chrome.

### Zone 1 — current task: the only sentence on the screen

- Atkinson 400, 15sp, line height 20, text-primary `#D2C6BB`, left axis.
  One task, the one he is in. Up to two lines; the zone reserves both, so
  a long name — or the next task arriving at a block boundary — never
  moves a single pixel below it. Layout stillness is a feature: a screen
  that reflows mid-session is a screen that asks to be looked at.
- No checkbox, no DONE, no step toggles, no queue count. HOLD: the task
  line *informs*; it offers nothing. Sub-steps, if the task has them,
  are not rendered — completing a step is a decision, and decisions do
  not exist here. The task changes only when the session's own program
  advances it, in one hard cut, digits and arc untouched.

### Zone 2 — the hero: remaining, and only remaining

- Label: `REMAINING`, the plate — Anonymous Pro 700, 10sp, +2.0 tracking,
  text-tertiary `#776A60`. This is the screen's only plate text: furniture
  naming the instrument, per Set's rule (plate = furniture, body = truth).
- Numerals: **Sora 700, 72sp, line height 72, tracking −2.0, glyph
  `#EDE3D8`**, tabular figures, format **`M:SS` in total minutes** —
  `47:32`, `107:32`. One format at every duration: no H:MM switch at the
  hour, no layout shift at 60:00, ever. Left axis, same baseline family
  as the task line above it.
- Update: **once per second, hard cut.** A measured number may step —
  that is how time exists. No roll, no flip, no fade: intermediate
  glyphs would be values the clock never held. The per-second step is
  the finest motion this screen permits itself, and it is information,
  not animation.
- Remaining is stated **once in the entire app at this moment**: not on
  the arc (§3, position is not a statement), not in the status strip,
  not as a percentage anywhere. If a numeral elsewhere in the build
  repeats it, that numeral dies, not this one.

### Zone 3 — the stepped arc: the escapement, restrained

Geometry:

- **Ø280dp** circle, centred on the content axis (40dp of slack inside
  the 320dp content width). Stroke **10dp**. Sweep **270°**, from 135°
  to 45° clockwise; the **90° gap sits at the bottom** (6 o'clock) and
  houses the emergency valve (§5). Printed scale: long tick (14×2dp) at
  0/25/50/75/100% of total, short tick (7×1dp) every 10%, all in
  text-tertiary `#776A60`, *inside* the rim. No numerals on the arc —
  the hero owns the number; the rim only orients the eye.
- Centre: **empty.** No pip, no hub text, no block count. The armed
  state already has its home in zone 0; a centre pip would be its
  second rendering wearing a smaller coat.

Step logic — quartz law, never a sweep:

- The arc is divided into **N segments, N = total session minutes ÷ 5**
  (the wheel's own detent quantum), clamped 4–36. A 25-minute session:
  5 segments. Fifty minutes: 10. Three hours: 36. Segment separation
  2dp of ground between segments, so a step reads as a *segment
  arriving*, not a bar growing.
- A segment fills when its 5 minutes have **elapsed** — in **one frame,
  hard cut**, on the exact boundary frame. Between boundaries the arc
  is frozen. It never sweeps, eases, or interpolates; a sweeping arc
  would claim fractions the step has not earned, and continuous motion
  in peripheral vision is a summons to look — the one thing this screen
  must never issue.
- Fill tones: completed segments text-secondary `#9C8E84`; uncompleted
  segments recess `#100E0C` against the ground. The arc is tonal, never
  glyph-bright — glyph `#EDE3D8` is spent on the hero alone, so the eye's
  first landing is always the number, the arc second.
- **The beat.** On the boundary frame, the newly landed segment renders
  in glyph `#EDE3D8` for exactly **150ms**, then settles to
  `#9C8E84`. One flash per five minutes, 150ms long — that is the whole
  heartbeat. No pulse loop, no breathing, no haptic, no sound: while a
  session runs the machine is mute, and the beat is a visual tick the
  peripheral eye *may* catch, never one it must. This is SYNTHESIS 20's
  "stepped arc + beat" at its most restrained legal dose.
- Truth split, stated so nobody "fixes" it later: the arc understates
  elapsed by <5 minutes by construction (it shows *completed* quanta).
  That is not a lie and not a duplicate — position is the glance
  channel, the hero's digits are the truth channel, exactly as a sled's
  position and its margin word divide the labour everywhere else in
  this app. Anyone who wants the arc to state exact time is asking for
  a second hero; the answer is no.

### Zone 4 — the second reading: elapsed and finish, one line, once each

- One centred line, Anonymous Pro 700, 15sp, +0.5 tracking,
  text-secondary `#9C8E84`:
  **`ELAPSED 12 MIN · FINISHES 4:38 PM`**
- Elapsed in whole minutes, no seconds — seconds live in the hero only;
  an elapsed `0:12:41` beside a remaining `47:32` invites arithmetic,
  and a cloister screen that invites arithmetic has failed. Finish as
  wall-clock with AM/PM: the commitment translated into the world's
  time, the one translation a tired man should never do in his head.
- Session **total is never stated on this screen.** It was stated at
  arm, it is elapsed + remaining, and printing it would be its second
  rendering. Same for block count and block length: setup facts, dead
  here.

---

## 2. Deflections count — CUT from the running screen

Cockpit's test for every candidate element: *does it inform a decision,
or does it decorate?* Mid-session there are no decisions — HOLD sees to
that — so the bar is higher: an element must inform the *state*, the way
remaining and finish do, or it does not ship.

The deflections count (blocked attempts absorbed) fails three ways:

1. **It is not actionable.** Knowing 3 apps were turned away changes
   nothing he can do — there is nothing he is permitted to do. A number
   with no verb attached is a scoreboard.
2. **It gamifies the distraction it counts.** A rising count rewards
   checking the screen — it pays attention to the exact behaviour the
   session exists to starve, and it pays it *during* the session.
3. **It duplicates by anticipation.** Its true home already exists: the
   finish state (§6) and the session record state it once, rested,
   where it can be reflected on instead of chased.

**Ruling: no deflections count anywhere on the running screen, in any
tier, at any size.** It earns exactly one rendering, at finish (§6).
The blocking itself stays silent while it works — a deflection is the
machine doing its job, and a machine doing its job does not file
progress reports. (The exception is not the count but the failure:
if a protection *drops*, that is a WARNING, §4 — one line, no number.)

---

## 3. What is absent, and why the absences are the design

- **Nav bar** — absent, not disabled-styled. HOLD locks navigation;
  drawing a dead nav teaches that locks are cosmetic.
- **Back / close affordance** — absent. The only exit that exists is the
  banked hold (§5) and finishing. A cheap exit drawn beside an expensive
  one is an argument the screen is having with itself.
- **Wall-clock time** — absent. Remaining is the time that matters;
  finish is its translation. A third clock is a third rendering.
- **Peek/airlock** — not this screen's composition and not drawn on it.
  If a blocked-app attempt summons the airlock, that overlay is a system
  surface laid *over* a frozen cloister: hero and arc keep counting
  underneath (the session clock never lies for a peek), and when it
  expires the cloister is simply there again, pixel-identical.

## 4. Alarm discipline on this screen (wave-1 law, applied)

- **STATUS** — the armed dot + `RUNNING` word (zone 0). Geometric,
  permanent, silent. Nothing else on this screen is status furniture.
- **CAUTION** — **has no rendering here, by design.** A caution lives as
  one dot at its owning row on Shield/Guard — screens that are
  unreachable mid-session. A caution that followed him into the cloister
  would be a decision knocking on a door HOLD welded shut.
- **WARNING** — the app's sole strong signal, and the only event that
  may break this screen's composition. Fires only when the machine's
  promise breaks *while running* (a protection drops mid-session). Form:
  a 2dp rule in `#F47442` directly under zone 0, full content width, and
  one line under it, Atkinson 700, 15sp, glyph `#EDE3D8`:
  `UNGUARDED — BROWSER BLOCKER OFF`. While a warning is active it is the
  only saturated thing on the screen — **the armed dot dims to
  text-tertiary `#776A60`** until it clears (rank: warning outranks
  status; two burning dots would be two voices shouting). It clears
  itself when the protection returns. It never requires dismissal —
  a warning you must tap away trains tapping-away, and this screen
  accepts no taps but one (§5).

## 5. The emergency valve — resting presence (mechanics: Ton/Floor; place and announcement: mine)

The banked hold lives in the arc's bottom gap — the one place on the
screen that is empty by geometry, reachable by a thumb that is not
aiming, and findable eyes-off because it is the **only operable element
the screen contains**.

Resting form:

- A recessed slot, **200dp wide × 48dp high** (48dp = the touch floor;
  width centred on the content axis), nested inside the arc's 90° gap,
  its vertical centre on the arc's lower third. Fill recess `#100E0C`,
  radius **8dp**, 1dp hairline text-tertiary `#776A60` at 55% — a
  boundary, not an invitation.
- Inside, centred: one grip line, 24dp × 2dp, text-tertiary, and under
  it the words **`HOLD TO BREAK`**, Anonymous Pro 700, 10sp, +2.0
  tracking, text-tertiary `#776A60`. The plate, at furniture tone: the
  gesture is *named* — a state this expensive is never a mystery-meat
  icon — and never advertised: no orange, no count, no `1 LEFT THIS
  WEEK`, no word `EMERGENCY`. The weekly cost is stated by the hold
  overlay once begun, and by Shield/Guard while rested — never by a
  badge glowing through his study block. **Reachable, never advertised**
  is a tonal spec, not a slogan: the valve is the dimmest object on the
  screen that is still legible, and it is legible because everything
  around it is emptier.

Announcement once touched (the press is the announcement):

- On finger-DOWN, in that frame: every other zone dims one full tier
  (hero glyph→text-secondary, arc and task→text-tertiary), and the slot
  expands in place to the hold overlay — countdown at Set's overlay tier,
  **Sora 700, 50sp**, banked segments shown as filled 5dp blocks on the
  slot's own axis. The eye is given exactly one number to watch, and it
  is not the session's.
- **Correction to my wave-1 file, entered plainly:** I specced the
  remaining digits as *frozen* during the hold. Wrong — freezing prints
  a time that stops being true the moment the hold starts, and on
  release the digits would jump, which is a lie with a delay on it.
  The hero keeps counting during any hold attempt, dimmed. Truth does
  not pause because he is deciding to break it.
- On release before completion: overlay collapses in one cut, tiers
  restore in one cut, banked progress stays banked (mechanics Ton/Floor)
  and is **not** summarised on the resting slot — no partial-progress
  residue may decorate the valve at rest. The screen returns
  pixel-identical to before the touch. A failed attempt leaves no mark;
  that is what makes a second, honest attempt possible.
- On completion: the session ends as BROKE (§6). The valve does not
  reset, pulse, or celebrate. It is simply gone with the screen.

## 6. Session end — the transition into the finish state

**Completion (timer reaches zero):**

1. `t=0` — the hero steps to `00:00` on its normal per-second cut; the
   arc's final segment lands with its standard 150ms beat. Nothing else
   moves. The completion sound/haptic triplet is Ton's; visually, the
   screen holds **400ms of stillness** after zero — the machine
   exhales before it speaks.
2. Then **one hard cut** — no fade, no cascade, no ceremony chain — to
   the FINISHED state. It is a state, not an animation.
3. FINISHED composition (same grid, same tokens): status strip reads
   dot + `FINISHED`; the arc stands **full** — every segment filled,
   geometry announcing the ending before a word is read; the hero is
   replaced by the session total stated once, `50 MIN`, Sora 700, 72sp;
   one new line at zone 4's position, Anonymous Pro 700, 15sp:
   **`TURNED AWAY 3`** — the deflections count's single rendering in the
   whole flow, delivered rested, where it is a fact about the session
   instead of a score during it. If the count is zero the line reads
   `TURNED AWAY 0` — stated, not hidden; a clean sheet is also truth.
4. **One action only:** a single raised-fill button, 200×48dp, radius
   8, glyph text `RELEASE`, sitting where the valve sat. Tapping it
   returns to the idle Deck; navigation unlocks only *after* that tap —
   the finish state is still the cloister's last room, not a lobby with
   the nav leaking back in. No auto-dismiss: he may be across the room,
   and a finish state that evaporates is a record that lies about
   having been seen.

**Broken (banked hold completed):** completion and emergency never share
an exit form — a broken session rendered as a finished one is a lie told
by composition. So: the arc stays **partial**, frozen at the segments it
had earned; the status word reads `BROKE`; the hero states the elapsed-
at-break once, `32 MIN IN`; the `TURNED AWAY` line still renders (it is
still true); the single action reads `OPEN`. The difference is carried
by geometry first (full vs partial arc), word second, number third —
in that order, at a glance, across the room.

## 7. The accent clause — cockpit concurs, in writing, to Sana

I was invited to argue against "no accent on the running screen except
the armed dot." I don't. A cockpit spends its one loud colour the way
this screen spends `#F47442`: on the single dot that says the machine is
armed, and it *reserves* the same hue's shout for the single warning
that says the promise broke (§4) — at which point the dot yields. An
accent anywhere else on a screen a man sits inside for fifty minutes
would either mean something (a second fact, a TRUTH violation) or mean
nothing (decoration competing with the hero). There is no third option,
and this screen has no use for either. The clause stands as written;
Sana has this paragraph as the cockpit's signature under it, and a copy
by mail.

## 8. AOD readings — SETTLED with Nacht (thread `aod-readings-settlement`)

Nacht owns the AOD's physics; I own its readings count. We exchanged
settlements by mail (mine 05:21, his counter the same hour, filed as
decided in his plan file §1) and **I countersign HIS terms** — his
counter beat my proposal on two points using my own logic, and a
cockpit seat that won't take its own medicine has no business rationing
anyone else's signals. Where my 05:21 letter and his terms conflicted,
his stand. The settled spec:

1. **Two readings, standing:** REMAINING (Sora 700, **104sp** — hero,
   always, hierarchy never swaps) and ELAPSED (Sora 600, **50sp**),
   plus the armed dot (6dp, ember). Nacht killed his own wave-1
   30-minute large-swap himself: a glance-parse learned once stays
   valid all night; burn-in is carried by the patrol + staged dimming.
   I accept the kill — it is the stronger form of the objection I had
   mailed him about the task line.
2. **The progress bar SHIPS** — 120dp × 3dp under the elapsed line,
   fraction by fill length. My 05:21 letter killed it as a fourth
   rendering; Nacht's answer is correct and it is my own §3 logic
   turned back on me: the bar is *geometry, not a reading* — position
   is the glance channel, digits are the truth channel, and the AOD
   has no needle, so the bar is the needle's twin. It stays.
3. **The task line is TRANSIENT, not standing.** My version (standing
   for 20 minutes) loses on Nacht's law: a static title is the worst
   burn-in shape on the face — same glyphs, same duty, session-length —
   and he chose the task minutes ago; the desk clock's job is time.
   Settled form: task appears at session start (first 60s) and for
   10s after any wake-touch or pickup, Atkinson 400, 15sp, then gone.
   Orientation on demand, darkness at rest. `NEXT` lives on the idle
   AOD face only; mid-session it is noise, and it is not a reading here.
4. **`OF total` never prints on AOD.** Elapsed + remaining + the bar
   carry the whole fact; the total would be arithmetic's third
   rendering — the same cut my running screen makes in zone 4.
5. **Mechanics, Nacht's wholesale:** pure `#000000`, no element above
   luminance 0.25, colon at 45%, no seconds ever, updates once per
   minute on the minute boundary (update + patrol step are one wakeup),
   rigid cluster on the 12-anchor patrol (no repeat before all 12 are
   visited, never more than one grid step per move), staged dimming
   after 20 continuous minutes. At finish, one step only: hero holds
   `0:00` at stage-0 brightness for 60s, the dot steps up, then the
   face hands over to idle. A deflection mid-session changes **nothing**
   on the AOD — the same scoreboard refusal as §2, on the darker face.

Disagreement 9 is closed: one AOD spec, two signatures.

---

*Pit — Cockpit, Designers cohort. No code touched. The running screen
spec'd above — zones, dp, step logic, tones, transitions — is final as
written and implementable without follow-up questions.*
