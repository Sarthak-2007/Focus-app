# SMALL THINGS — the register (founding edition: Tief's hunt, Oct 29, 2026)

Standing register of the Small Things team (charter:
plan/SMALL-THINGS-CHARTER.md). Every design wave and build
programme answers each item by name — BUILT, SCHEDULED, or
RULED OUT BY NAME. Nothing here dies by silence.

Method: the day-walk, hour by hour, against trunk code — every
find verified absent or invisible in the tree before filing;
everything that exists and shows itself is an acquittal below.
Prices are rough (none / small / real); Methods refines.

## The yardstick — his orders, NOT re-listed as finds

The pill over other apps (session time, break time, peek
time) · editable peek duration · editable break duration.
Already spoken for, recorded so the register never pretends
they were its news. Their components already in the tree,
waiting for the pill: the peek countdown exists as state
(MonolithViewModel.kt:110, ticked at :833) and is rendered by
no surface anywhere; the end-peek-early verb exists
(VM:2150) and its only caller is the expiry ticker itself
(VM:838) — no hand can reach it. Also already law elsewhere,
not re-listed: the intercept's missing remaining time, its
unprinted peek cooldown and its reasonless peek refusals
(Una's audit, plan/AUDIT-deck-intercept-claims.md — Held Door
baseline + Truth Car 3).

## Finds

- **ST-01 · No clock anywhere tells the time.** Every face
  formats HH:mm for other facts (ENDS, WITHIN, receipts) and
  no face prints the current time; the nightstand desk clock
  least of all — running, its hero is the span countdown
  (AirlockAndDisarmOverlays.kt:102–111); idle, it renders the
  composer's dial setting as a time ("50:00").
  *Bites:* the nightstand hours, 03:30–05:00, and every
  glance all day. *Price:* **none** — the device holds the
  fact and the tickers already run; only the print is missing.
- **ST-02 · A break cannot be ended early.** The 10-minute
  safe break ends only when its timer expires
  (MonolithRepository.kt:1724–1759); no end/skip/resume verb
  exists anywhere in the tree. A man ready at minute four
  waits out six dead minutes, or stares at a countdown.
  *Bites:* between blocks, all day. *Price:* **small** — the
  flip into the next block already exists; a verb that zeroes
  the remaining is a Deck chip and one VM call.
- **ST-03 · Neither break boundary makes a sound.** Block →
  break and break → next block are silent state flips
  (Repository, same span; no sound call observes them — the
  engine's voices are the seal, the finish E-clock and
  interaction detents only). Eyes on paper miss the break
  starting; a man away from the desk gets no knock before
  the break dies and peek apps re-lock under his thumbs.
  *Bites:* every block boundary, sharpest mid-break.
  *Price:* **small** — the VM ticker already watches the
  session every second; one knock per flip.
- **ST-04 · No way to add time to a running block.** The
  session's clock grows by exactly one writer in the tree:
  the tamper penalty (Repository:1680). A chapter that needs
  seven more minutes offers him two doors — break the seal
  or lose the thread. *Bites:* any block that runs long,
  evening blocks most. *Price:* **small** — the penalty path
  already mutates the target under a fresh signature; the
  same write by his hand instead of the machine's suspicion.
  (Honesty flag for Ren's ruling, not a design: the slip's
  planned-vs-lived figures must show the extension.)
- **ST-05 · An ending is silent unless the app is open.** The
  one completion voice lives in the UI-layer finish machine
  (MonolithViewModel:1063–1090). The machinery that actually
  runs the night commands no sound at any ending: the sleep
  session's 03:30 stand-down is "a plain deactivation"
  (RoutineHeartbeat.kt:124–131), and a timer completion with
  the app closed earns a shade notification and nothing
  else (MonolithShieldService:112–117). *Bites:* 03:30,
  across the room, sharpest — also any session that ends in
  a bag or a pocket. *Price:* **small** — the heartbeat
  already reaches the seal transition for arms; the shield
  loop already posts at the exact completion moment.
- **ST-06 · Nothing warns before a routine arms.** The
  heartbeat computes the next boundary and arms the chain
  (RoutineHeartbeat:170–180); the first signal of the 20:00
  sleep lock is the seal at 20:00 itself. Mid-sentence in a
  peek app, the lock is the warning. *Bites:* 19:59 daily
  (and 04:59, 08:59 in miniature). *Price:* **small** — the
  boundary is already computed every tick; one knock a
  minute ahead is a comparison the same tick can make.
- **ST-07 · The idle Deck never says what happens next.** The
  next routine boundary is computed every heartbeat and
  printed nowhere in the UI; at 19:45 the Deck shows a wheel
  and says nothing about SLEEP at 20:00. *Bites:* the
  evening gap, 19:30–20:00, and any idle glance.
  *Price:* **small** — one already-computed fact carried to
  a face that already prints facts.
- **ST-08 · Taking a peek doesn't take him back to the app —
  and its clock is already running.** The peek tap starts
  the 120-second lease and hides the airlock
  (MonolithViewModel:2126–2147; MainActivity:751 wires
  nothing further); he walks back to the app himself while
  the paid seconds burn. The intercept machinery launches
  an activity on the same evidence in reverse every day.
  *Bites:* every peek, mid-block. *Price:* **small** — one
  launch intent for a package the machine already names.

## Acquittals — checked, exists, visible

- The shade notification is a real surface: remaining time /
  "Safe Break MM:SS" / block n-of-N / urges stopped, refreshed
  every 2 s through the session, and a "Session Complete"
  post at the end (MonolithShieldService:121–150). Its limit
  is the pill's ground — it lives in the shade, not over apps.
- The break is printed on the Deck while it runs:
  "BREAK · N MIN" (TactileDeckScreen:556–557).
- The running strip's TIME cell prints the block's end in
  wall time — "ENDS HH:mm" (MonolithCustomComponents:408).
- Held messages are readable mid-session: the Deck's digest
  door opens the sheet; rows print sender and full body; at
  the finish it freezes as HELD · AS OF {time}
  (MainActivity:483, :709; AirlockAndDisarmOverlays:223–420).
- The week's emergency-break count is printed on the Deck
  before it is needed — "N/1 WEEK" (TactileDeckScreen:964).
- A finish the app witnesses has a voice even with the Deck
  uncomposed — E-clock knocks and the completion pulse fired
  from the VM job (MonolithViewModel:1063–1090). ST-05 is
  about its reach, not its existence.
- During a safe break, peek-tier apps unlock freely while
  locked apps stay locked — the break's whole point, built
  (MonolithAccessibilityService:203; MonolithShieldService:170).
- A routine's arm sounds the same seal as a hand arm
  (RoutineHeartbeat:152–162) — the boundary itself is
  audible; ST-06 is about the minute before it.
- The desk clock holds a true-black ground and shifts its
  position against burn-in (AirlockAndDisarmOverlays:136–143).

— Tief, Humans (deep thinker), Oct 29, 2026. Walked 03:30 →
20:00 against the trunk; every find grep- or read-verified;
two candidates cut by my own scepticism before filing and
named in my closing letter so the cutting is on record.
