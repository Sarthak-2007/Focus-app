# LEDGER EXAMINATION — Linea (Improvers, Deck task line)
## Elements: hero minutes · receipts — PLAN WAVE, no code
Coordinator: Ilya. Question reopened by Sarthak: keep the Ledger or not.
Standards applied, the only three: TRUTH once · ARM ≤10s · HOLD absolute.

Sources read first-hand: `CurioVaultScreen.kt`, `TileNumberCounterView` /
`TowerStreakView` / `DayFadersView` in `BlockitTowerFadersAndCatalogViews.kt`,
`MonolithViewModel` (total derivation), `MonolithModels` / `MonolithRepository`
(session entity + signing), renders `shots/04_vault.png` + `shots/night/n04_vault.png`,
SYNTHESIS item 31 (wave-1 rebuilt-Ledger spec — treated as evidence, not law).

**The overlap, stated honestly once, up front.** YPT tracks his *study* — hours,
groups, the study log. Monolith tracks what only Monolith can: sessions actually
run under the seal, deflections absorbed, emergency holds used, routine
attendance. Hero minutes are Monolith-session minutes — a **subset** of YPT
hours, always smaller, because he also studies unsealed. Receipts are
per-session records only Monolith holds. Neither element duplicates YPT's
data; both currently duplicate *each other and themselves*, which is the
actual disease.

---

## ELEMENT 1 — HERO MINUTES

### Current form, truthfully

There is no hero. There is a 2×2 grid of equal 74dp tiles under the header
"COUNTER MACHINE TELEMETRY", and the minutes tile (`TileNumberCounterView`)
prints the same integer **twice in one tile, in the same glance, at the same
size class**: once as a 3-digit 4dp dot-matrix (accent orange), once as a
22sp crisp mono numeral (accent orange) right beside it. One fact, two
costumes, one `onDraw` — the view literally formats `digitsStr` and then
draws `numberValue.toString()` again a few lines later. This is the TRUTH-once
exhibit and it is worse in the hand than on paper:

- **The dot-matrix side lies at scale.** It is clamped `coerceIn(0, 999)`.
  Past 999 lifetime minutes the dots freeze at 999 while the numeral keeps
  counting — the two costumes eventually *disagree with each other*.
- **The dot-matrix side is illegible anyway.** In both renders the SESSIONS
  tile's dots read as noise next to their own numeral; at night the FOCUSED
  MIN dots smear into a texture. A rendering nobody can read is not a
  rendering, it's a costume.
- **The number itself is floored.** `totalFocusedMinutes` in the ViewModel
  is `coerceAtLeast(35)`, initial value 75. A fresh install's "hero" prints
  75 minutes that were never studied. The Ledger's biggest number is a
  minimum lie with real minutes added on top.
- **The name lies in two more directions.** (a) It is unlabelled subset:
  printed as "FOCUSED MIN" with no "UNDER SEAL" qualifier, it invites direct
  comparison with YPT study hours and under-reports his work. (b) It counts
  every non-active session's elapsed seconds, completed or broken — aborted
  sessions mint "focused" minutes. (c) It includes the *live* session's
  elapsed time, so the lifetime total ticks during the very session it
  supposedly summarises.
- **Accent theft.** It spends Ledger orange (the one COMMIT colour) on a
  passive count, in two renderings at once.

### Peak examination

Does minutes-under-seal have a peak form worth keeping anywhere? The
*lifetime cumulative* form does not, at any altitude: a number that only
grows, never changes a decision, and plays YPT's role as a strictly smaller
subset cannot be a hero — its only emotional register is monument, and
monument is the tower's job, not a counter's.

The **day-scoped** fact is a different element wearing the same data, and it
does have a peak. "How much has the seal held *today*" is the one minutes
fact with a decision attached: at 03:30 it reads 0 = not yet started; at
session end it is the score of the day so far against his 12h war. Its peak:

- **Home 1 — session end (primary).** The closing line of the session-end
  receipt (Element 2, specced below): `TODAY UNDER SEAL 2H 25M`, Anonymous
  Pro Bold 44sp, ink, **no orange** (orange is a verb; nothing at session
  end is committable). Computed number → re-strikes in place, no roll, no
  intermediate glyphs. It lands once, at the only moment it changes by a
  whole session.
- **Home 2 — chrome (secondary, idle only).** One quiet mono fragment in
  the nameplate line, labelled so it can never impersonate a study total:
  `SEALED TODAY 2H 25M`, 11sp, secondary ink. While a session runs it
  *hides* — the running session's own clock on Deck is that fact's live
  rendering, and chrome already carries HELD state; printing a ticking
  near-duplicate above it would re-break TRUTH once. It returns when the
  seal opens. Read-only, zero interaction: ARM unchanged, HOLD untouched.
- **Lifetime total — demoted to a whisper or nothing.** If a receipts
  logbook survives (Element 2), one 11sp mono line at its foot:
  `ALL TIME UNDER SEAL 41H 10M`. No tile, no dots, no orange. If the
  logbook dies, lifetime dies with it — it gets no independent home.
- **Truth repairs are preconditions, not polish:** kill the 35-minute
  floor and the 75 initial (0 is a printable number; a zero day is
  information), count only completed sessions toward any printed total
  *or* label the figure "TIME UNDER SEAL" so broken sessions are honestly
  inside it — my pick: rename the fact to time-under-seal everywhere and
  count elapsed sealed time, because that is what the DB actually knows;
  "focused" is a claim about his mind that no sensor in this app can make.

### VERDICT — hero minutes: **DEMOTE**

The hero form (lifetime tile, dot-matrix + numeral) is DEAD at any
altitude. The fact survives smaller and re-scoped: **today's minutes under
seal**, single rendering, hero size only at session end, a labelled
fragment in idle chrome, lifetime reduced to one mono line inside the
logbook if the logbook lives.

### The strongest case against my own verdict

Demotion keeps the worst possible version of the number. Scoped to today,
sealed minutes will read as a *fraction of failure* almost every day of
his life: he studies unsealed (fasted morning block, revision outside the
app), so chrome will spend its life printing `SEALED TODAY 2H 25M` next to
a 12-hour ambition — a subset number wearing a total's clothes no matter
how carefully it's labelled, teaching him either shame the machine didn't
earn or contempt for the one Monolith-native figure there is. The lifetime
total I killed was the only version that never shamed a light day and the
only one YPT genuinely cannot give him the feeling of: two hundred sealed
hours staring back is the monument a dropper actually draws will from, and
daily-scoped numbers are precisely YPT's home turf, where Monolith's will
always be the smaller, sadder copy. If TRUTH once is the law, the honest
verdict is not demote — it's kill minutes entirely and let YPT own time.
A demoted fragment that misleads daily is worse than a dead one.

---

## ELEMENT 2 — RECEIPTS

### Current form, truthfully

A card list of the **last 8** sessions (`sessionHistory.take(8)`) under the
header "VERIFIED SESSION RECEIPTS — TOTAL n", where n counts *all*
sessions: the header and the list silently disagree about what "total"
means. Each card (12dp radius, surfaceVariant fill, border) carries:
intention (14sp bold), `+Nm` (orange if completed — colour is the *only*
completion signal on the card, never explained), a divider, then
`profileName • TASKS n`, and at right: `HMAC•` + the first 8 characters of
the session's HmacSHA256 signature, uppercased.

- **The stamp is proof for an audience of one, and it proves nothing to
  him.** The crypto underneath is real — AndroidKeyStore HmacSHA256 over
  start times, boot count and target, verified during progress updates,
  with a +15-minute penalty lock on tamper. That machinery is honest
  *internally*. Printed, it becomes theatre: 8 hex characters (a 32-bit
  fragment of a MAC never designed as a display token), never re-verified
  at render, no verify action anywhere, shown to the one person who holds
  both the key and the phone.
- **On rows with no signature it prints `HMAC•` and then nothing** —
  visible in both day and night renders. A stamp certifying an empty
  string, under a header that says VERIFIED. The header asserts a check
  the UI never performs on history rows. That's not a rendering flaw; the
  section's title is false.
- **The card omits the most receipt-like fact it owns: when.** The entity
  carries `startWallTimeMillis`; the row renders no date, no start time.
  A receipt that cannot say when it happened is a name tag.
- **It also omits every Monolith-only fact the row exists for.** The
  entity knows `phantomReachesBlocked`, target vs elapsed, completed or
  broken, hold/penalty state. The card shows none of them. What it shows —
  intention, minutes, profile, task count — is the *least* exclusive
  slice of the record, plus a stamp.
- The AES-256-GCM backup export chip below the list is a separate,
  honest function (data portability) and is not on trial here; it
  survives any verdict on the rows.

### Peak examination — session end, judged hardest

Receipts are my own element's family: the Deck's NOW line, the DONE verb,
the logbook. So the candidate homes in order of suspicion: (1) session
end, (2) the standing list, (3) Shield/chrome — dismissed fast: a receipt
is an *event* record; Shield is state and chrome is a glance, and parking
past events in either re-creates the Ledger in places with less room.

**The list form fails on its own terms.** Nobody re-reads receipts. Eight
identical bordered cards are an archive performing diligence; the 9th
session silently evicts the 1st while the header still counts it. As a
logbook it is too fat to scan and too thin to trust.

**Session end is the receipt's natural and only full-form moment.** A
receipt belongs to the close of a transaction: the fact is freshest, he
is present (the seal just opened — HOLD absolute means nothing competes
for this beat, and the card delays nothing: it lives *after* the session,
and the next ARM starts through it, see below), and the entry is written
at the only time it will ever be read with feeling. The home is currently
vacant — today completion silently flips a flag in the DB, the fact's
first-ever surface is the Ledger card days later. Peak spec — **the DONE
card**:

- **Trigger:** the persisted completion transition only (all blocks
  complete), keyed by sessionId, fired once, unreplayable across process
  death. Ceremony on state change, never ambient.
- **Form:** one full-width card on Deck as the cloister releases. Depth:
  single raised surface, hairline border, no shadow stack, no second card
  behind it. Contents — every fact once, human language, no orange
  anywhere (nothing here can be committed):
  - Verb line, Sora SemiBold 20sp: `DONE — <intention as armed>`
  - Held line, Anonymous Pro 13sp ink: `HELD 50M OF 50M · 14 TURNED AWAY ·
    NO BREAK USED`. If the emergency hold ended it, the line says so in
    the same ink, same size, no shame formatting: `OPENED BY THE HOLD AT
    31M · 6 TURNED AWAY`. Truth once, judgement never.
  - Tasks line, 13sp, **only if tasks were completed**: `TASKS DONE 2`.
    Absent entirely otherwise — absence, never `TASKS 0`.
  - Closing line (hero minutes' peak, Element 1): `TODAY UNDER SEAL
    2H 25M`, Anonymous Pro Bold 44sp.
  - Footer, 10sp secondary: `YOUR STUDY RECORD LIVES IN YPT — THIS IS THE
    SEAL'S ACCOUNT.` The overlap, named inside the product, on the one
    surface where the two records could be confused. (Not "Monolith keeps
    no copy" — it does keep the rows, and printing otherwise would be a
    lie in nice type.)
- **Motion/sound:** one stamp knock at settle — a single low thock, the
  completion sound, fired by the state transition, not the animation.
  Card rises and settles with a letterpress weight-pulse; all numbers
  re-strike in place. Nothing loops, nothing glitters, no random
  collectible is minted for finishing (the shuffled-artifact forge on
  completion should die with this redesign — it writes fiction into the
  record).
- **Dismissal / ARM safety:** persists until one tap anywhere, or the
  next slide-to-arm begins — the drag dismisses it as it starts, so it
  adds **zero seconds and zero required interactions** to the next ARM.
  It is the Deck's completion state, not a modal gate.
- **The standing list, demoted (conditional home).** If the Ledger tab
  survives the cohort verdict, receipts shrink to a hairline **logbook**:
  one Anonymous Pro 11sp line per session, no cards, no borders, no
  stamp, header word LOGBOOK (VERIFIED dies):
  `TUE 04 NOV · 05:02 · HELD 50M · STUDY · 14 TURNED AWAY`.
  The *when* becomes the spine of the row — date, start time, held
  duration, loadout, deflections; broken sessions say `OPENED 31M` in the
  same ink. All sessions in range or an honestly stated window ("LAST 30
  DAYS") — never 8 rows under a total that counts more. If the tab dies,
  the list dies with it and nothing is lost: the DONE card carried the
  moment, the DB keeps the data, the GCM backup remains the archive.
- **HMAC as display: DEAD at any altitude**, in every home, including the
  DONE card. He was there; a witness does not notarise his own signature
  for himself. The signing/verification machinery stays exactly where it
  is honest — inside, guarding elapsed time, never printed.

### VERDICT — receipts: **KEEP-AT-PEAK**

Home: **session end** — the DONE card above, full form, once per session.
Standing list DEMOTED to the hairline logbook inside a rebuilt Ledger if
the tab survives; acceptable death if it doesn't. HMAC/VERIFIED display
DEAD unconditionally.

### The strongest case against my own verdict

The DONE card taxes the exact moment the app should disappear. HOLD
absolute's spirit is that the machine asks nothing and shows nothing — a
man finishing a block at 04:40 with gym, sadhana and the next block queued
does not need a ceremony, a 44sp score and a footer; he needs the Deck,
instantly, ready. This codebase already proves what completion surfaces
become: the tower monument overlay exists, and it is wallpaper — one more
tap between him and the next thing, 4 times a day, 700 times a year,
until his thumb dismisses it blind and the "peak form" is a 200ms
annoyance with good typography. Worse, a scored ending is a thing to
perform for: sessions get chosen for prettier receipts, and the honest
`OPENED BY THE HOLD` line I specced becomes the one he avoids by not
finishing at all. And ephemerality is a con I dressed as purity — the one
time he truly needs the record is weeks later, at 1 AM, asking *was
October actually held, which routine is leaking* — exactly what a
dismissed card cannot answer and a demoted hairline list answers badly.
The audience-of-one mockery cuts the other way: he *is* the one, and a
dropper fighting his own memory needs the record to feel stamped, not
vibed. Kill the card, keep a real ledger, let completion be silent like
every serious tool.

---

## Cross-element notes (mailed, not just noted)

- **To Band (deflections + stat tiles):** my DONE card and logbook rows
  spend per-session deflection counts (`phantomReachesBlocked`, already
  on the entity, currently rendered nowhere). If your verdict gives
  deflections a tile home, the per-session figure and the total must not
  read as rivals — mine is "this session", yours is the aggregate; labels
  carry the scope.
- **To Fader (tower + 7-day columns):** tower bricks are *derived from my
  number* (`minutes/25 + completed`, then floored at 16, on a minutes
  figure itself floored at 35). If the floors die — they should — your
  brick count drops on honest data. A tower built on floored minutes is
  a monument to sessions that never happened; spec your peak against
  unfloored, completed-only inputs or say plainly that you keep the lie.

— Linea, Improvers (Deck task line), Ledger examination, 29 Oct 2026.
Plan only. No code touched.

---

## Addendum — mail received during the examination (05:19–05:28 IST), answered

**Band (deflections + stat tiles) — clash named, NOT resolved, flagged for
Ilya.** Band's file gives deflections one home: a scoped mono line on
Shield (`DEFLECTED TODAY 07`), and rejects session-end because receipts
are ephemeral and out-of-session bounces would vanish from any
receipt-only count. His condition: the receipt's session-window count wins
only if the Shield line dies. My answer, mailed: part and whole are
different facts, and both my elements already run on that distinction
(this session's minutes vs today's minutes). The DONE card's `14 TURNED
AWAY` is the *session's* account, scoped by the card itself; Shield's
`DEFLECTED TODAY` is the day aggregate, the only home that can hold
out-of-session bounces — Band is right that those exist and right that a
receipt can't carry them. Neither yields; the scopes are labelled in the
strings themselves. If Ilya rules one-number-only regardless of scope,
that's a chair decision above both seats — recorded here as an open
conflict, not a consensus. **Concurrence with Band, unqualified:**
sessions-as-stat-tile is dead; a count is honest only sitting on the list
it counts. My logbook spec adopts it: if the list survives, its header
may carry its own count (`LOGBOOK — 23`) and no sessions figure appears
anywhere else.

**Fader (tower + 7-day columns) — overlap accepted with one condition.**
Fader's peak: tapping a column prints one readout line under the chart
(date, sealed duration, session count, deflections) as an index into the
receipts. Tower is DEAD in his file too — the floored-minutes objection
above is concurrence, not attack. On the readout line: no cut demanded
from my side. A day aggregate is a different fact from the per-session
rows beneath it (same part/whole rule as Band), and a tap-summoned,
selection-scoped line is an index, not a rival rendering. **One binding
condition:** the readout's duration, session count and deflections must
derive from the same session rows the logbook prints. Today's code keeps
two pipelines (`sevenDayStats` vs `sessionHistory`) and they already
disagree in the render — SESSIONS tile 4, receipts TOTAL 3, same screen.
An index that can contradict its own list is worse than no index.

— Linea, addendum 05:29 IST.
