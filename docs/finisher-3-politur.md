# FINISHER 3 — POLITUR: THE WORD SURFACES OF FIX-8 (Issue #11)

Seat: POLITUR, Finisher 3, Tor's cell · For: Tor (lead)
Slice: every string the **fixed** Held Door can print, taken to
gallery standard — final wording, capitalisation, number formats,
one voice, and a truth check of each string against the machinery
(Held Door Truth Baseline). Design geometry is the Designer's
(`docs/designer-skeleton.md`); this file owns only the words that
stand in her slots. **Nobody compiled. Nothing here messages the
user.**

Ground read whole: `studio/plan/AUDIT-held-door.md` (FIX-8 and
Conflicts 4 ruled there), `docs/tor-plan.md` §6,
`docs/designer-skeleton.md` (§§4–5, 8), `docs/innovator-proposals.md`
(P-4b's count warning), `docs/feel-5-druck.md` (feel charges —
see §0.3 for what is and is not adopted), the shipped prints in
`AirlockAndDisarmOverlays.kt` (`peekCooldownPrint` :233,
`airlockSessionLine` :244, `peekBadgeText` :257, `peekPriceText`
:265, chip :665, withheld :692–705), `endsAtText`
(`MonolithCustomComponents.kt:427`), `peekOfferFor` / withheld
derivation (`MonolithViewModel.kt:128–151, 2331–2345`), constants
(`MonolithRepository.kt:106–108`: peek 120 s, cooldown 20 min),
tier badges (`MonolithModels.kt:13–35`), door call sites
(`MonolithAccessibilityService.kt:211, 237, 265, 291`).

---

## 0. Decisions taken in this seat (Tor ratifies or strikes)

### 0.1 The memory line's scope word: `THIS SESSION` is FINAL;
`TONIGHT` is not

The audit's standing example is `THIRD TIME TONIGHT`, and the
brief to this seat repeats it. But FIX-8 defines the taper's count
as **this session's booked intercepts of this package** — and a
session is not a night: a session can start before midnight and
be counted after it; two sessions can share one night. Printing
`TONIGHT` over a session-scoped count is a Baseline violation in
miniature (the Designer, §5.7, flagged exactly this for Tor;
the Innovator's P-4b and Druck both independently print
`THIS SESSION`). **Final string family: `{ORDINAL} TIME THIS
SESSION`** (§7). `TONIGHT` revives only if Tor re-scopes the
count's derivation to tonight — in which case the wall's
definition changes with it, not just the word. One word is the
whole price of the flip; the table below is built so nothing
else moves.

### 0.2 The footer on peek-tier doors prints `PEEK`, never `2M PEEK`

The tier badge feeding the footer is the literal `"2m Peek"`
(`MonolithModels.kt:27`); uppercased by the face it would print
`2M PEEK` — a duration **literal** standing on the door the day
the peek length is edited, the exact disease FIX-8 cures on the
chip. Baseline law 5 (derived values, never literals) binds the
footer too. **Final: peek-tier doors print `PEEK` alone in the
footer; the duration lives in exactly one place — the chip's
derived badge (§3).** All other doors print their badge verbatim
(§9.3). This adopts the Designer's §4 recommendation as this
seat's ruling, for Tor's ratification.

### 0.3 Druck's sentence-case programme (D-F8) is NOT adopted

Druck proposes moving all body lines to sentence case
(`"When the peek ends, a 20 minutes cooldown…"`,
`"End the session instead"`, `"Peek is off."`). It is an
**unruled feel charge**, and adopting it here would silently
overturn Hale's register (W-29/W-30 supplied every one of these
strings in caps) and Tor's FIX-8 rulings, which quote the caps
forms verbatim. **Final: the ruled caps forms stand, everywhere
in this table.** Druck's charges are recorded in §12 for Tor to
rule as a register question — if he rules sentence case, it is
a one-pass transform of this table and no derivation changes;
until he rules, a builder who ships Druck's casing has shipped
unruled words. The same applies to D-F13's full stop, D-F10's
`"Third time this session — the wait is longer."`, D-F6's
appointment timing, and P-3a's `HOLD TO TAKE THE PEEK` microcopy:
none of them appear in the final table; all are listed in §11
as not-final so they cannot leak in by familiarity.

### 0.4 Druck's gate/memory pairing is arithmetically wrong — do
not copy it into a test

Druck §3 / D-F10 pairs `THIRD TIME` with `PEEK OPENS IN 0:08`.
Under FIX-8's definition (wall = 5 + **prior** booked intercepts
of this package, cap 10 — the current visit is booked only at
its outcome, `recordIntercept` fires from the RETURN and PEEK
paths, never at arrival), the third visit has 2 priors and its
wall is **7**: `THIRD` pairs with **`0:07`**. `0:08` pairs with
`FOURTH`. The full pairing is fixed in §6; any test written from
Druck's example fails a true door.

---

## 1. Format laws (bind every string below)

**L1 — Case.** Every system string on this face is ALL CAPS,
with exactly three exceptions, all of them *data, not system
copy*: (a) the app name, intention, domain, and opened-target
name print **verbatim** as stored/typed — no `uppercase()`
transform may touch them (the intention's sentence case is its
attribution, Druck D-F4, adopted on this narrow point because
it changes no system string); (b) the chip badge's units are
lowercase `m` / `s` (§3); (c) the footer's badge word is the
one place a transform is lawful: the stored Title-Case badge
prints uppercased (§9.3).

**L2 — Punctuation.** No system string ends with a full stop.
The face's entire punctuation inventory is: the comma inside
the price (§4), the spaced em dash ` — ` inside the cooldown
line (§5), the spaced middle dot ` · ` inside the stopwatch
session line (§8), and the colon inside every clock print.
Nothing else — no exclamation, no ellipsis in system copy
(ellipsis is a *rendering* overflow on data slots, never a
printed character).

**L3 — Numbers, four dialects, never mixed.**
| Dialect | Form | Used by |
|---|---|---|
| Clock instant | `HH:mm`, 24-hour, both fields zero-padded (`09:05`, `20:01`, `00:40`) | session line, break line — via `endsAtText`, `Locale.US`, one derivation |
| Gate count | `M:SS`, minutes **unpadded**, seconds zero-padded to 2 (`0:05`, `0:10`) | gate line only |
| Badge duration | whole minutes then remaining seconds, **no padding, no spaces** (`1m`, `1m30s`, `2m5s`) | chip badge only |
| Counted minutes | plain integer, unpadded (`2 MIN`, `20 MINUTES`) | cooldown line (ceiling), price (exact) |

**L4 — One verb, one noun.** The act is `PEEK` on every surface
(chip, withheld lines, footer, tier family). The consequence is
`COOLDOWN` in both sentences that name it (price, withheld).
The scope phrase is `ON THIS APP` in the price; the withheld
line needs no scope phrase — it stands on that app's door. No
string may borrow `EMERGENCY` — that word belongs to the Disarm
face's `EMERGENCY BREAK` alone (Conflicts 4).

**L5 — Spacing.** Chip: word and badge separated by exactly one
space (`PEEK 2m`). Gate: one space after `IN`. Price: single
spaces, comma attached to `ENDS`. No double spaces, no leading
or trailing space in any print (the `" Balanced"` badge literal
elsewhere in `MonolithModels.kt` is another face's defect and
a warning: badge literals are data, and this face prints them
verbatim — sweep them at the source, never trim at the door).

---

## 2. Master table — every string the fixed door can print

Slot names are the Designer's (H1/H2/H3/Z1/Z2/Z3/R/X/F).
"Timing" is when the string may stand; derivations name the
single source each number is read from.

| # | Slot | State | FINAL STRING (template) | Example |
|---|---|---|---|---|
| 1 | H1 | every visit | *data:* the opened thing's real name, verbatim, 1 line, ellipsis (§8.3) | `YouTube` |
| 2 | H2 | intention declared | *data:* his intention, bare, verbatim (§8.3) | `Finish the mock first` |
| 3 | H3·1 | timed session | `SESSION ENDS {HH:mm}` | `SESSION ENDS 20:01` |
| 4 | H3·1 | stopwatch session | `SESSION RUNNING · NO SET END` | (verbatim) |
| 5 | H3·1 | no session | — silence (§10) — | |
| 6 | H3·2 | PEEK door + next break exists | `BREAK AT {HH:mm} OPENS THIS APP` | `BREAK AT 14:30 OPENS THIS APP` |
| 7 | H3·2 | site door | *data:* matched domain, verbatim (§8.3) | `instagram.com` |
| 8 | Z1 | offer pending (wall running) | `PEEK OPENS IN {M:SS}` (§6) | `PEEK OPENS IN 0:07` |
| 9 | Z1 | offer armed | `PEEK` + badge (§3) | `PEEK 1m30s` |
| 10 | Z1 | held — cooldown | `PEEK COOLDOWN — AGAIN IN {n} MIN` (§5) | `PEEK COOLDOWN — AGAIN IN 2 MIN` |
| 11 | Z1 | held — already active | `A PEEK IS ALREADY RUNNING` | (verbatim) |
| 12 | Z1 | held — peek off | `PEEK IS OFF` | (verbatim) |
| 13 | Z1–Z3 | never owed a peek | — whole zone absent (§10) — | |
| 14 | Z2 | offer pending **and** armed | `WHEN THE PEEK ENDS, A {n} MINUTE(S) COOLDOWN STARTS ON THIS APP` (§4) | `WHEN THE PEEK ENDS, A 20 MINUTES COOLDOWN STARTS ON THIS APP` |
| 15 | Z2 | held | — silence (§10) — | |
| 16 | Z3 | 2nd visit onward, any peek-owing door | `{ORDINAL} TIME THIS SESSION` (§7) | `THIRD TIME THIS SESSION` |
| 17 | Z3 | 1st visit | — silence (§10) — | |
| 18 | R | every visit, every state | `RETURN TO FOCUS` | (verbatim) |
| 19 | X | every visit, every state | `END THE SESSION INSTEAD` | (verbatim) |
| 20 | F | every visit | badge word alone, uppercased (§9.3) | `BLOCKED` / `PEEK` / `SITE BLOCKED` |

That is the complete print surface. Any string a build of the
fixed door emits that is not a row of this table (or a data
value in rows 1/2/7) is a defect by definition.

---

## 3. The offer — chip and badge (FIX-8, cure of `peekBadgeText`)

**Chip print:** `PEEK` (Sora Bold) + one space + badge
(Anonymous Pro Bold), same ink, one line, on the 48 dp chip.
The word `EMERGENCY` never appears (§11).

**Badge derivation (the one rule, replacing today's
`"${durationSeconds / 60}m"` truncation at :257).** Let
`s` = the lease length in seconds, read from the same source
the lease machinery reads (`PEEK_DURATION_SECONDS`, or its
editable-store successor — one derivation, two readers).
Let `m = s / 60`, `r = s % 60`:

- `r == 0` → **`{m}m`**
- `m == 0` → **`{r}s`**
- otherwise → **`{m}m{r}s`** — seconds **not** zero-padded
  (`2m5s`, never `2m05s`: the badge is a duration, not a clock;
  padding belongs to the gate's dialect, L3)

| Lease `s` | FINAL badge | Full chip print | Today's buggy print (for the test that kills it) |
|---|---|---|---|
| **60 s** | `1m` | `PEEK 1m` | `1m` (right by accident) |
| **90 s** | `1m30s` | `PEEK 1m30s` | `1m` — **the truncation lie FIX-8 names** |
| **120 s** (today's constant) | `2m` | `PEEK 2m` | `2m` (right by accident) |
| **600 s** | `10m` | `PEEK 10m` | `10m` (right by accident) |
| 45 s | `45s` | `PEEK 45s` | `0m` — a zero badge on a real lease |
| 61 s | `1m1s` | `PEEK 1m1s` | `1m` |
| 125 s | `2m5s` | `PEEK 2m5s` | `2m` |
| 599 s | `9m59s` | `PEEK 9m59s` | `9m` |
| 0 s | — no chip — | withheld row 12 prints instead | `0m` — **unrepresentable** in the fixed door |

Truth check: the badge is the lease, neither rounded nor
floored — `90 s` of access is never sold as `1m`. A refused
grant (re-derivation fails at tap) prints nothing new: the
chip stands, no error string exists on this face (§11).

---

## 4. The price (FIX-8, re-timed; cure of `peekPriceText` :265)

**FINAL template:**

```
WHEN THE PEEK ENDS, A {n} {UNIT} COOLDOWN STARTS ON THIS APP
```

`{n}` = the cooldown the repository will actually arm, in
whole minutes, read from the same source the arming reads
(`PEEK_COOLDOWN_MILLIS` today). `{UNIT}` = `MINUTE` if
`n == 1`, else `MINUTES` (the existing singular/plural logic
at :267 is preserved verbatim — it is the face's only plural
rule and the cooldown line's invariant `MIN` is a different
family, §5).

| Cooldown | FINAL price print |
|---|---|
| 60 000 ms (`n = 1`) | `WHEN THE PEEK ENDS, A 1 MINUTE COOLDOWN STARTS ON THIS APP` |
| 20 min (today's constant) | `WHEN THE PEEK ENDS, A 20 MINUTES COOLDOWN STARTS ON THIS APP` |
| 5 min | `WHEN THE PEEK ENDS, A 5 MINUTES COOLDOWN STARTS ON THIS APP` |

Notes the builder may not improvise around:

- **The comma is part of the string** (audit, Designer §5.6,
  and this seat's brief all carry it). The dead string
  `TAKING THE PEEK STARTS A …` differs in tense, anchor, and
  punctuation; no fragment of it survives.
- **The anchor is the peek's end, because the machine's is:**
  `startPeekLease` pre-schedules the cooldown anchor at lease
  end; `recordPeekEnded` re-anchors at actual end. The old
  sentence started the clock at the tap — from the tap, the
  true exclusion is lease + cooldown, and the new sentence
  says exactly when its clock starts. It does **not** state
  the total exclusion as a duration, and no variant may
  (Baseline law 1).
- **`A 20 MINUTES` stands as ruled.** Strict grammar wants
  `A 20-MINUTE COOLDOWN`; Hale's register (W-29's supplied
  text, whose timing clause alone FIX-8 replaces) carries
  `A {n} MINUTES`, and consistency with the register beats
  a lone grammatical polish. Recorded here so a future
  copy-editor does not "fix" one face out of voice.
- **Timing:** the price stands from frame one — while the
  gate counts (row 8) as well as at offer (row 9). A price
  that arrives with the chip is a trap sprung, not a price.
- **Guards.** (a) If a future store ever sets a non-whole-minute
  cooldown, `{n}` must **ceiling**, never floor — the current
  `cooldownMillis / 60_000L` floors, and a floored price
  understates the one number this face swears never to
  understate (same doctrine as FIX-5a). Whole minutes are the
  only lawful values until that cure lands. (b) `n == 0`:
  the template would print `A 0 MINUTES COOLDOWN STARTS`,
  claiming a consequence that does not exist — **no final
  string is supplied for `n == 0`**; the case is Tor's (§13),
  and until ruled, a zero-cooldown store value must not reach
  this print.

---

## 5. The withheld lines (state AND reason; from second 0)

Withheld lines are **never gated by the wall** (FIX-6, SECOND 0)
and print from the first frame, on doors that could have
honoured a peek and whose tier owed one. The derivation order
is the code's own (`MonolithViewModel.kt:2343–2345`):
`PEEK_OFF` → `COOLDOWN` → `ALREADY_ACTIVE`.

| Reason (state) | FINAL STRING | Derivation / notes |
|---|---|---|
| `COOLDOWN` | `PEEK COOLDOWN — AGAIN IN {n} MIN` | `{n}` = **ceiling** minutes of the derived remaining seconds: `n = ⌈seconds / 60⌉`, derived per recomposition from the stored cooldown end (FIX-5b) — never the self-ticking local counter. `MIN` is invariant: never `MINS`, never `MINUTE(S)` (that full-word family belongs to the price alone). |
| `ALREADY_ACTIVE` | `A PEEK IS ALREADY RUNNING` | Verbatim, unchanged. True exactly while a lease/pill for a peek is live; names the fact, promises no instant it cannot prove. |
| `PEEK_OFF` | `PEEK IS OFF` | Verbatim, unchanged. Stands when the peek length is set to 0 — his own setting stated back, no verb promised, none implied (Hale W-30). |
| `null` (never owed) | — silence — | Baseline law 4: LOCKED tier, sniper / site / guard / unknown doors print **no peek line of any kind** — no refusal for an offer that was never on the table. |

**Cooldown ceiling representatives** (the print's arithmetic is
acquitted; its upstream floor is FIX-5a's cure):

| Derived remaining | FINAL print |
|---|---|
| 1 s … 60 s | `PEEK COOLDOWN — AGAIN IN 1 MIN` |
| 61 s | `PEEK COOLDOWN — AGAIN IN 2 MIN` |
| 300 s | `PEEK COOLDOWN — AGAIN IN 5 MIN` |
| 900 s | `PEEK COOLDOWN — AGAIN IN 15 MIN` |
| 1 200 s (the cap the repository enforces) | `PEEK COOLDOWN — AGAIN IN 20 MIN` |
| 0 s | — **line unrepresentable** — the same recomposition re-runs the offer derivation (FIX-5c): the chip stands in its place, or the line re-derives to its new true state. `AGAIN IN 0 MIN` must be impossible by construction, not by a string guard. |

Truth check: ceiling means the print can overstate the wait
by under a minute and can never understate it — the one
direction this face permits itself.

---

## 6. The gate line and the taper (FIX-8; FIX-5 anchoring)

**FINAL gate template:** `PEEK OPENS IN {M:SS}` — Anonymous Pro
Bold, statement rank, over Schwelle's draining 2 dp rule. It
names the one thing it gates — the peek chip's appearance —
and nothing else: RETURN, the withheld lines, the price, the
memory line, and the exit are never behind it.

**The taper (machinery the gate reports):**
`wall = min(5 + prior, 10)` seconds, where `prior` = this
session's booked intercepts of **this package** before this
visit (one count drives wall and memory line alike — Innovator
P-4b: derive it from the session-window intercept logs, never
from the lifetime `interceptCount` on the app row, which would
print a lifetime total in tonight's clothing). The wall is
anchored to a stored end-mono; the gate's figures derive
`remaining = ⌈(end − now) / 1000⌉` at each recomposition
(FIX-5's F-1 twin cure) — the ticker is a redraw trigger, never
a counted value.

**Every taper count — first, second, third, capped:**

| Visit | `prior` | Wall | Gate at open | Gate sequence (each print, in order) | Memory (§7) |
|---|---|---|---|---|---|
| 1st | 0 | 5 s | `PEEK OPENS IN 0:05` | `0:05` → `0:04` → `0:03` → `0:02` → `0:01` → chip | silence |
| 2nd | 1 | 6 s | `PEEK OPENS IN 0:06` | `0:06` → `0:05` → `0:04` → `0:03` → `0:02` → `0:01` → chip | `SECOND TIME THIS SESSION` |
| 3rd | 2 | 7 s | `PEEK OPENS IN 0:07` | `0:07` → `0:06` → `0:05` → `0:04` → `0:03` → `0:02` → `0:01` → chip | `THIRD TIME THIS SESSION` |
| 4th | 3 | 8 s | `PEEK OPENS IN 0:08` | `0:08` → … → `0:01` → chip | `FOURTH TIME THIS SESSION` |
| 5th | 4 | 9 s | `PEEK OPENS IN 0:09` | `0:09` → … → `0:01` → chip | `FIFTH TIME THIS SESSION` |
| 6th — **cap engages** | 5 | **10 s** | `PEEK OPENS IN 0:10` | `0:10` → `0:09` → … → `0:01` → chip | `SIXTH TIME THIS SESSION` |
| 7th onward — **capped** | ≥ 6 | **10 s** | `PEEK OPENS IN 0:10` | identical 10 s sequence | ordinal keeps counting honestly (§7) |

Format laws for the gate, exhaustively:

- `PEEK OPENS IN 0:00` **never prints** — at derived 0 the chip
  replaces the gate in the same recomposition (§5.4 of the
  skeleton). A printed `0:00` would announce an opening that
  has not happened in the frame that shows it.
- Seconds are always two digits (`0:05`, never `0:5`); minutes
  are never padded (`0:05`, never `00:05`). The lawful range
  today is `0:05`–`0:10` only; the `M:SS` rule generalises
  lawfully (`1:00`, `1:05`) if a future ruling ever lifts the
  cap past 60 s — no such value is final today.
- The gate exists **only** on the offer-pending branch. Held
  visits have no gate and no wall (§5 prints at second 0);
  never-owed visits have neither (§10).
- The gate is not the hero numeral: the shipped `05`…`00` at
  88 sp is dead (Conflicts 1), including its terminal `00`
  frame, which printed a zero over a door that had not opened.

---

## 7. The memory line — the taper's count (Z3)

**FINAL template:** `{ORDINAL} TIME THIS SESSION`, where
`{ORDINAL}` is the spelled-out ordinal of this visit's number,
`visit = prior + 1` — the same `prior` that sets the wall (§6),
one derivation with two readers, incapable of disagreeing.
Anonymous Pro Bold, footer/gate family (letterspaced badge
register), centred. Prints from frame one, on offer **and**
held peek-owing visits alike (the memory is true regardless
of the peek's state). Scope ruling: §0.1.

| Visit | FINAL memory print |
|---|---|
| 1st | — silence — the door claims no memory it does not have; a `FIRST TIME` print would be a tally of nothing |
| 2nd | `SECOND TIME THIS SESSION` |
| 3rd | `THIRD TIME THIS SESSION` |
| 4th | `FOURTH TIME THIS SESSION` |
| 5th | `FIFTH TIME THIS SESSION` |
| 6th | `SIXTH TIME THIS SESSION` |
| 7th | `SEVENTH TIME THIS SESSION` |
| 8th | `EIGHTH TIME THIS SESSION` |
| 9th | `NINTH TIME THIS SESSION` |
| 10th | `TENTH TIME THIS SESSION` |
| 11th–20th | `ELEVENTH` … `TWENTIETH TIME THIS SESSION`, same pattern |
| 21st onward | digits + suffix in the same register: `21ST TIME THIS SESSION`, `22ND…`, `23RD…`, `{n}TH…` — suffix by the English rule (`ST/ND/RD` for 1/2/3 endings, `TH` otherwise, except 11/12/13 which take `TH`). Digits are lawful here: Anonymous Pro is tabular, and a spelled `THIRTY-SEVENTH` would be embroidery, not honesty. |

Truth check (Funke's never-list, endorsed by the audit): this
line is a count the ledger can prove, scoped in its own words,
or silence. It is never a streak (no days, no consecutiveness
claimed), never a score, never a prediction, never a comparison
with another session or night. The capped wall (§6) does not
cap the count: at the 7th visit the gate honestly says `0:10`
and the memory honestly says `SEVENTH` — the wall's cap is a
rule about waiting, not a licence to stop counting.

*Recorded dissent, not adopted:* Druck D-F10 would print the
count only when it lengthens the wall, reworded as an
explanation (`"Third time this session — the wait is
longer."`). The ruled form above prints on every return visit
in instrument register; if Tor adopts D-F10, the strings in
this section — and only these — are re-cut. See §12.

---

## 8. Session lines, the break appointment, and the data slots

### 8.1 Session line (H3·1) — form acquitted, value is FIX-2's

Verbatim from `airlockSessionLine` — **no wording change is
finalised or permitted in this seat**; the value behind it is
the span end after FIX-2, never per-block remaining:

| Condition | FINAL print | Notes |
|---|---|---|
| Timed session | `SESSION ENDS {HH:mm}` | Composed as `"SESSION " + endsAtText(end)`; the instant is the session **span** end (FIX-2) — the same instant the Deck's TIME cell prints, so the two faces can never disagree. `HH:mm` 24-hour, zero-padded (L3). A span ending past midnight prints its clock time only (`SESSION ENDS 00:40`) — `endsAtText` carries no date, by acquitted design. |
| Stopwatch session | `SESSION RUNNING · NO SET END` | Verbatim, including the spaced middle dot. The door never invents an end for a session that has none. |
| No session | — silence — | `airlockSessionLine` returns null; the slot stands empty (Designer §3.1). |

**No variant of this line may state a remaining duration.**
`SESSION ENDS IN …`, `{n} MIN LEFT`, and Halbwach's struck
`8 MIN LEFT` rewrite are forbidden (§11) under Baseline law 1,
whatever their readability. The optional companion
`THE FEED WILL STILL BE THERE AT {HH:mm}` is **Ren's call in
the audit, not ruled in** — it has no row in the master table
and must not be built from this file.

### 8.2 The break appointment (H3·2) — the errand's kernel

**FINAL:** `BREAK AT {HH:mm} OPENS THIS APP` (the Designer's
§4.5 wording; the instant derives from the session schedule's
next break, `HH:mm` as §8.1). Example: `BREAK AT 14:30 OPENS
THIS APP`.

Prints **only** when all three hold: the door is PEEK-tier,
this app's tier is `PEEK_2M` (exactly the doors where a Safe
Break actually opens this app — service rule verified), and
the session's schedule has a next break. Otherwise silence.
It promises no lease and books no cooldown — a break opening
is the session's own law, not a peek — and it must never
acquire P-3c's coda (`— NO PEEK SPENT`): pricing the free
option against the lease is a mid-craving comparison, against
the endorsed never-list (§11).

### 8.3 Data slots (H1, H2, H3·2 site variant) — verbatim law

These slots print facts, not copy; gallery standard here is
*fidelity*, not wording:

- **H1 name:** the app's real name from its row (app-tier,
  shield); the feed name on sniper doors (`YouTube Shorts` /
  `Instagram Reels` — keep, the feed is the fact); the
  **browser's name** on site doors (the domain moves to
  H3·2); on guard doors, **what he actually opened** (the
  Settings app / installer, composed at the service call site
  from the firing package; if its human name cannot be
  resolved, the package name verbatim). `System Settings
  Guard` is dead (§11) — a fiction in the slot that everywhere
  else holds a fact. One line, ellipsis on overflow (F-9).
- **H2 intention:** his declared words, **bare** — the
  `ACTIVE INTENTION:` prefix is dead (§11) — verbatim,
  including his casing, no label, no quotation marks added.
  Blank → silence. The intention is the session's declared
  intention only: the fallback to the composer's draft dies
  with FIX-3e, and no door may quote a draft as a declaration.
- **H3·2 site domain:** the matched domain **verbatim** as
  matched (e.g. `instagram.com`) — case is part of the fact;
  do not uppercase it into system register. It yields its
  slot to nothing: on a site door the break line (§8.2) can
  never be owed, so no contention exists.

---

## 9. The two standing verbs and the footer

### 9.1 RETURN (R)

`RETURN TO FOCUS` — verbatim, unchanged, Sora Bold on the
56 dp accent slab, ink `#471602`. Live and tappable from frame
one in **every** state in this table, never dimmed, disabled,
relabelled, or gated by wall, taper, or withheld state. Its
ledger outcome is FIX-3c's `RETURNED AT DOOR` — a booking,
not a print; no success, gratitude, or streak string follows
it on any face this file governs.

### 9.2 The honest exit (X)

`END THE SESSION INSTEAD` — verbatim (FIX-8; Halbwach I-4),
Atkinson Bold 12.5 sp, bare text, no slab, no icon, no badge,
no terminal punctuation. Prints in **every** state — counting,
offer, held, and never-owed alike; a door that hides its exit
on any visit has re-walled itself. It is a **route, not a
spend**: the tap opens the existing Disarm face
(`EMERGENCY BREAK` and its valve carry the gravity there);
this line ends nothing, books nothing, and carries no price
text of its own — adding one would make it a spend against
the ruling. If the Disarm face does not open (no active
session / clean-completion path), the Held Door remains
standing — the exit must never strand him on no face at all
(Designer §7's dead-end guard is part of this string's
truth: `END THE SESSION INSTEAD` promises a door, and a tap
that delivers nothing breaks the promise).

### 9.3 Footer badge (F) — the badge word alone

The `AIRLOCK INTERCEPTION •` eyebrow is dead (§11); the footer
prints the badge word **alone**, Anonymous Pro Bold,
uppercased (L1c), footer tier. Complete lawful set:

| Door / tier | Stored badge | FINAL footer print |
|---|---|---|
| LOCKED tier (app-tier / shield) | `Blocked` | `BLOCKED` |
| PEEK tier (app-tier / shield) | `2m Peek` | **`PEEK`** (§0.2 — never `2M PEEK`) |
| Uninstall guard | `Uninstall Guard` | `UNINSTALL GUARD` |
| Site | `Site Blocked` | `SITE BLOCKED` |
| Sniper — YouTube | `Shorts Blocked` | `SHORTS BLOCKED` |
| Sniper — Instagram | `Reels Blocked` | `REELS BLOCKED` |
| Any fallback / default | — | **`BLOCKED`** |

Fallback law: `Locked` → `Blocked` at **every** site — the
audit counts three; the Designer found four occurrences in
the tree (`MonolithViewModel.kt` state default + trigger
default, `MainActivity.kt` intent fallback,
`MonolithShieldService.kt` extra fallback). The builder sweeps
all four; a single surviving `Locked` (any casing) is a failed
sweep, not a variant. An OPEN-tier badge never reaches this
door — OPEN apps are never intercepted.

---

## 10. The silences (prints of nothing, specified as strictly
as the prints of something)

| Where | Condition | What stands |
|---|---|---|
| Z (whole zone, 0 dp branch) | never-owed doors: LOCKED tier; sniper / site / guard / unknown paths | No chip, no gate, no price, no withheld line, no memory line, no labelled empty slot — the zone is absent, not blanked (Designer §3.3). Baseline law 4. |
| Z2 | held visits | Empty-held at full height. A withheld offer has no price; printing one would advertise a transaction that cannot happen. |
| Z3 | 1st visit of the session (any branch) | Empty-held. See §7. |
| H2 | blank intention | Empty-held. No placeholder word, no label (§8.3). |
| H3·1 | no session | Empty-held (§8.1). |
| H3·2 | no break owed / not a site door | Empty-held (§8.2). |
| Z1 | grant refused at tap (re-derivation fails) | No change, no error print. The acquitted machinery refuses silently; the door does not apologise for arithmetic. |
| Whole face | PEEK taken / RETURN tapped | No farewell, congratulation, or confirmation string. The skeleton freezes and fades; the pill (another face) takes over a taken peek. |

---

## 11. Forbidden strings — what the fixed door must never print

A build containing any of these on this face fails the wording
pass, regardless of behaviour:

| Forbidden | Why it dies |
|---|---|
| `EMERGENCY PEEK` (any casing) | Conflicts 4 / FIX-8. The word belongs to the valve alone. |
| `AIRLOCK INTERCEPTION • …` eyebrow | FIX-6, three seats. The footer badge word alone survives (§9.3). |
| `ACTIVE INTENTION: …` prefix | FIX-6. His words print bare (§8.3). |
| `System Settings Guard` as a name | FIX-6 (C-8/B-7). A fiction in the fact slot (§8.3). |
| `Locked` as any badge/fallback (any casing) | FIX-6 (C-10). `Blocked` at all four sites (§9.3). |
| `2M PEEK` in the footer | §0.2. A literal duration; the footer's peek word is `PEEK`. |
| `TAKING THE PEEK STARTS A … COOLDOWN …` | FIX-8. Wrong anchor (the tap); replaced by §4. |
| Truncated badge (`1m` for 90 s, `0m`, any floor) | FIX-8 (F-8). §3's rule or nothing. |
| `PEEK OPENS IN 0:00` | §6. The chip, not a zero, marks the opening. |
| `AGAIN IN 0 MIN` | FIX-5c. Unrepresentable by construction (§5). |
| The hero numeral (`05`…`00` at display size) | Conflicts 1. The gate line is its only lawful heir. |
| Any remaining duration (`… MIN LEFT`, `ENDS IN …`, `8 MIN LEFT`) | Baseline law 1; Conflicts 5 struck it in full. |
| `THE FEED WILL STILL BE THERE AT …` | Ren's call, not ruled in (§8.1). Not buildable from this file. |
| `… OPENS THIS APP — NO PEEK SPENT` | §8.2. A mid-craving comparison; never-list. |
| `HOLD TO TAKE THE PEEK` (Innovator P-3a microcopy) | Unruled proposal. The chip is a tap in every ruling this file rests on. |
| Sentence-case re-cuts of ruled strings (Druck D-F8/D-F13/D-F16 forms, e.g. `Peek is off.`, `End the session instead`) | §0.3 / §12. Unruled register change; Tor rules it as a register question or it does not ship. |
| Any error/apology string on a refused grant | §10. Silence is the acquitted behaviour. |

---

## 12. Recorded dissents this table does not adopt (for Tor)

Filed so the rulings are knowing, not silent. None changes a
string above unless Tor rules it, in which case the re-cut is
confined to the named rows:

1. **Druck D-F8 (sentence case for body lines)** — the lecture
   charge: caps read as posted regulations at 1 AM. A register
   question for Tor/Hale, not a truth question; every derivation
   in this table survives either casing. Rows affected if
   adopted: 3, 4, 6, 10, 11, 12, 14, 19.
2. **Druck D-F10 (memory line only when it lengthens the wall,
   reworded as its explanation)** — §7 records the counter-ruling
   (print on every return, instrument register). Rows: 16.
3. **Druck D-F6 (break appointment prints at offer, not frame
   one)** — a timing charge, not a wording one; §8.2's string is
   final under either timing. Rows: 6.
4. **Druck D-F13 (`PEEK IS OFF` carries no fact)** — true and
   priced: the line states his own setting, which is the only
   fact the state has. No truer string exists for it in Hale's
   register; inventing one (a settings path, a duration) would
   promise navigation or numbers the door does not have.
   Rows: 12.


---

## 13. Builder's string checklist (exact values to encode)

Pure-print deltas — signatures kept where they exist, so the
tests stay pointed at the same functions:

1. `peekBadgeText(s)`: `60→"1m"`, `90→"1m30s"`, `120→"2m"`,
   `600→"10m"`, `45→"45s"`, `125→"2m5s"`. (Today's tests assert
   `peekBadgeText(120)=="2m"` — keep; add the 90 s case that
   kills the truncation.)
2. `peekPriceText(cooldownMillis)`: body becomes
   `"WHEN THE PEEK ENDS, A $minutes $unit COOLDOWN STARTS ON THIS APP"`;
   `20*60_000L → "WHEN THE PEEK ENDS, A 20 MINUTES COOLDOWN STARTS ON THIS APP"`,
   `60_000L → "… A 1 MINUTE …"`. The two existing price
   assertions in `TruthCar3InterceptTest` are re-pointed to
   these values — they currently encode the dead sentence.
3. New pure print for the gate (name is the builder's):
   `gate(5)=="PEEK OPENS IN 0:05"`, `gate(7)=="PEEK OPENS IN 0:07"`,
   `gate(10)=="PEEK OPENS IN 0:10"`; no input produces `0:00`.
4. New pure print for the memory (name is the builder's):
   `prior=0 → null`, `prior=1 → "SECOND TIME THIS SESSION"`,
   `prior=2 → "THIRD TIME THIS SESSION"`,
   `prior=5 → "SIXTH TIME THIS SESSION"` — and the wall paired
   with each: `5, 6, 7, 10` (never Druck's `THIRD↔0:08`, §0.4).
5. `peekCooldownPrint` and `airlockSessionLine`: **unchanged**
   — their existing assertions stand verbatim.
6. Tree sweep, zero hits after the fix: `EMERGENCY PEEK`,
   `AIRLOCK INTERCEPTION`, `ACTIVE INTENTION:`,
   `System Settings Guard`, `TAKING THE PEEK STARTS`,
   `"Locked"` as a badge/fallback at all four sites (§9.3).
7. Chip composition asserts `wordLabel == "PEEK"` with the
   derived badge as its `monoBadge` — the word and the badge
   are separate fields, joined by exactly one space at render.

**Open for Tor (two, both small):** (a) §4's `n == 0` price —
no lawful string exists; rule silence, a supplied string, or
a store constraint that keeps 0 unreachable. (b) §0.1/§0.2
ratifications — `THIS SESSION` over the audit's `TONIGHT`
example, and footer `PEEK` over the literal `2M PEEK`; both
are one-word flips if struck, and both are flagged rather
than buried precisely so the flip is cheap.

— POLITUR, Finisher 3, Tor's cell, issue #11. Word surfaces
only; nothing compiled, nothing built, no geometry changed.
Report to Tor.
