# LEDGER — the Critics verdict

**Question (reopened by Sarthak):** keep the Ledger, demote it, or kill it?
**Critics' answer: (b) DEMOTE — unanimous across three independent lenses**
(Quaere, purpose · Day8, retention economics · Nox+Lind, rendering/truth),
with the coordinator's exhibits concurring.

Kill the tab, the tiles, the tower, the faders, and the receipts list.
The record survives in exactly two forms, neither of them a place:

1. **A session-end receipt** — an event, not a destination. Fired by
   completion (and by valve-disarm, stated as partial). One rendering, human
   language, unfloored numbers: held time summed across blocks, tasks
   finished, reaches turned away. Dismiss-only. No history, no re-opening.
2. **The stored record itself** — session rows stay in the database, where
   the banked-hold token, routine attendance, and the app's own integrity
   checks read them. Data is infrastructure, not a surface.

Everything else the tab showed either duplicates YPT, duplicates another
Monolith surface, or was never true.

---

## 1. The audit — every number on the Ledger vs its source

| Rendered claim | Source | Verdict |
|---|---|---|
| FOCUSED MIN tile (hero) | `totalFocusedMinutes` = Σ elapsed, **floored at 35, initialised 75**, seeded with 150 fake minutes | **LIE** three ways before his first real session exists |
| DEFLECTED tile | `max(intercept log size, Σ per-app counts)`, initialised 6, polluted by his own early quits written in as intercepts | **LIE** — two ledgers, larger wins; exits counted as deflections |
| SESSIONS tile | `sessionHistory.size.coerceAtLeast(4)` | **LIE** — the tile floors his history at 4 |
| TOWER BRICKS tile + tower | `(totalFocusedMinutes / 25) + completedCount`, floored at **16** (14 in overlay); renders 16 where real inputs give 9 | **FABRICATED** — a composite invented by arithmetic, monument to a formula |
| Receipts header `TOTAL n` vs SESSIONS tile | Header counts all; tile floors at 4; same screen shows TOTAL 3 and SESSIONS 4 | **SELF-CONTRADICTION** on one screen |
| Receipt list | `sessionHistory.take(8)` under the TOTAL header; minutes `(elapsed/60).coerceAtLeast(1)`; "+" printed for failed sessions; no date/start time though the entity carries both | Truncated silently, rounded up, failures dressed as gains |
| `HMAC•XXXXXXXX` stamp under a "VERIFIED SESSION RECEIPTS" header | Signature covers **none** of the displayed fields (not elapsed, intention, tasks, completion); verifies true even when blank; renders literally `HMAC•` on unsigned rows | **THEATRE** — the stamp certifies nothing it stands next to |
| 7-day faders | `compute7DayHistory`; 0-minute days draw a **12%-height stub**; blocks inflate (15m → a 25m block); a **hardcoded fake week (45–110m, Oct 22–28)** renders if stats load late | Absence rendered as presence; fiction as fallback |
| EXPORT AES-256-GCM LEDGER BACKUP | Button exports nothing anywhere; payload omits the sessions; import callback re-exports. AES is real; fallback keys are hardcoded strings in the APK | **DEAD CONTROL** wearing a spec sheet |
| Drawer `BANKED 150m • DEFLECTED ` | BANKED = floored total + live session + seed minutes; DEFLECTED prints **no number at all** | The Ledger's one-line summary is false in both halves |
| Deflections *with their triggers* — the one fact Day8 priced as actionable | Collected in the intercept log | **NEVER RENDERED.** The Vault gathers the only fact he'd act on and shows him everything except it |

**One-fact-one-rendering count (Nox+Lind): ~30 visible renderings of ~6
distinct facts.** Focused minutes alone appear as: hero tile, tower bricks,
fader heights, fader numerals, receipt "+Xm" rows, drawer BANKED — six
costumes, and they disagree with each other (floors differ per costume).

**Facts that exist only here** (Monolith's, not YPT's): sealed minutes,
deflections, hold usage, routine attendance, in-session tasks. YPT owns his
*study*; it cannot say whether the seal held. This is the entire honest case
for keeping anything — and every one of those facts survives demotion (the
receipt speaks them once, the store keeps them). What duplicates YPT is the
*meaning* of "FOCUSED MIN": a subset of his study wearing the same label,
disagreeing with YPT daily. Two totals for one day is not a record; it is an
argument he has to referee.

## 2. The deeper question — can ANY rendering of this record pass?

TELL THE TRUTH has three clauses: the fact must be true, said **once**, in
**human language**. The current Ledger fails clause one in at least nine
places (§1) — that alone disqualifies keep-as-is. But the reopened question
is whether a *rebuilt* record could pass, and the standards answer it
structurally:

- **A tab is a standing invitation to check.** Its only verb is LOOK.
  Checking is a loop: open, decode, compare, close — nothing decided, and
  the loop itself is the friction the standards exist to remove. Worse, it
  is reachable *mid-session* (Quaere: nav fully live while sealed), so the
  record competes with the session it records.
- **Witnessing is not deciding.** Quaere's distinction, which the cohort
  adopts: deciding is a fork, checking is a loop, and witnessing done right
  is a **full stop** — a statement that closes something. The past tense is
  the one tense no other surface can speak: *"You kept your word for N
  hours this week."* That sentence is worth saying — once, at the moment it
  becomes true, in words. That is the receipt.
- **ARM/HOLD economics:** a tab earns attention only if it originates or
  closes an action. The receipt closes the session (the transaction's end).
  The tab originates nothing: no arm is faster, no hold stronger, for its
  existence. Day8's worst-week test seals it: on his worst week the honest
  record says "you were absent" — as a fader wall of clamped stubs or a
  tower of ghost bricks, the current screen either accuses falsely or lies
  kindly. As one line on Sunday (`SEALED 3H THIS WEEK — ROUTINE FIRED 5,
  ATTENDED 1`), the same fact is a rested-user decision input. Form is the
  difference between an accusation and an instrument.
- **Beauty/retention:** the Vault is the app's only screenshot-worthy
  surface (wave-1 retention finding) — and that is precisely the danger.
  A monument you visit is a habit; a receipt you read once is a period at
  the end of a sentence. Retention in this app must come from the arm and
  the seal being beautiful, not from a museum of himself.

## 3. The option, precisely

**(b) DEMOTE.** Dies: the tab and its nav entry, the four tiles, the tower
as a screen object, the fader wall, the receipts list, the HMAC stamp, the
backup button (relocated — §5). Survives: (1) the session-end receipt as
specified above — with a hold-opened session stated in the same ink, no
shame formatting, no floors, no stamps; (2) a single weekly line where the
rested user plans (Sunday, on Shield with the routines it judges: sealed
time, fires vs attendance); (3) the stored record as infrastructure.
Deflections-with-triggers — the one actionable fact — belongs to the
receipt and the weekly line, not to a browsing surface.

## 4. The strongest case AGAINST this verdict (stated at full strength)

**For kill-entire (c), against our demote:** the receipt still lands a
verdict-number on a depleted man at the exact moment he is emptiest — every
block ends with a grade. The purist kill notes that in-the-moment honesty
already exists inside the seal (ratchet refusals in plain words, the
airlock's intention line, the valve's confession lines) — TELL THE TRUTH is
satisfied *during* the act, and the past tense may be a need the app should
refuse to feed at all: YPT shows him his week socially witnessed; Monolith
adding a private second witness doubles the audience for failure without
adding a decision. And the weekly line, however demoted, still requires the
attendance record to exist — the same record whose ghost-minting this
cohort has spent pages condemning; keeping the record for the line keeps
the temptation to render it. If the cohort is wrong anywhere, it is in
believing a number can be spoken once and not echoed in his head all
evening.

**For keep-rebuilt (a), against our demote:** existence *is* an argument
when the facts are load-bearing — hold usage is the HOLD standard's own
ledger, and attendance is the only evidence that can convict a ghost
routine; burying both in infrastructure and a one-line receipt makes them
unauditable, and an unauditable record drifts back toward theatre (the
HMAC stamp began as exactly such an internal guard before it put on a
costume). The tab is also the only surface that ever made him *proud* —
the tower's pull on the next 05:00 arm is a retention mechanism this
cohort's own law (beauty is retention) should price honestly instead of
pathologising as a "checking habit."

**Why demote survives both:** the kill case proves too much — it would
silence the token's own accounting and leave HOLD's scarcest fact
unspoken anywhere, which TRUTH-once forbids. The keep case confuses
*storage* with *surface*: everything it wants auditable stays auditable in
the store and speakable in the two surviving lines; what it cannot defend
is the room — thirty renderings, a nav slot, a mid-session door. Demote
keeps the sentence and burns the museum.

## 5. Dependencies flagged (main plan continues with Ledger OPEN — these bind it)

- **Persisted hold state is not optional under ANY option** — the banked
  token must live in storage beside the session rows. Even (c) keeps the
  store; only surfaces die.
- **Routine attendance** (fires vs presence) must remain *recorded* for
  the weekly line and for any `MISSED ×3` fact to be printable — with
  presence defined honestly (CRITICS-PLAN T-7), or the line dies too.
- **AES-GCM backup export relocates** (Shield, or Guard's drawer if Guard
  is kept — Guard's fate is Sarthak's open pick, flagged both branches).
  The payload must actually contain the sessions or the feature dies as a
  dead control.
- **Nav + chrome:** the LEDGER tab label dies with the tab; the deflection
  count's settled home (Shield/chrome) and the receipt's home (Deck,
  session end) are already consistent with the earlier settlement this
  verdict re-derives from evidence.
- **Convergence:** Designers, MaxImpl, and Wild independently filed (b)
  demote; Improvers' exam demotes the hero and keeps the receipt at peak.
  Five lenses, one answer. The master plan may treat the destination as
  dead and argue only about the surviving forms.

— Hale, for the Critics. Verdict seats: Quaere (purpose), Day8 (economics),
Nox+Lind (truth/rendering). Audit seats: Prima, Moss, Zug, Vera, Una.
