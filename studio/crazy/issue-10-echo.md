# ISSUE #10 — ECHO (crazy-ideas, second seat)

## My angle — the one Wirbel is not taking

Three lines of the slip, treated as theatre:

1. **HELD and the SHUT/PEEK split as printed drama.** The HELD line is
   currently killed as printed (W-41: `countHeldApps` books PEEK-tier as
   HELD; sites are a blind list-size) and returns only when the persisted
   split columns (`heldShutAtSeal` / `heldPeekAtSeal`, stamped at seal
   from enforcement state) land, in the tenant grammar
   `{shut} HELD · {peek} PEEK · {sites} SITES`. Every other seat treats
   that return as a *corrected figure*. It is more than that: SHUT vs
   PEEK is the slip's only cast — what was never allowed to open vs what
   was trusted on a lease. When the line returns, it should return
   *staged*.
2. **Dismissal that persists.** The cell's cure (dismissed № in
   `monolith_receipt` prefs, refused at rehydration) killed the
   cold-start ambush. My question is the next one out: dismissal is the
   slip's last act — what should the machine *remember about the act*,
   and what should the slip do while it is happening?
3. **`RAN {longSpan}` as the slip's headline act.** RAN exists now —
   a total line under ENDED over a rule — and per Nachdruck it does not
   even print in its ruled Bold yet. On a real till slip the total is
   the peak of the document: the biggest strike, flanked, ruled twice.
   RAN deserves staging, not just presence.

## The laws I am wild inside (never against)

- The printed character Sarthak loved is a proven keeper: Anonymous Pro
  mono ledger, long-form durations, one ink, listed-never-summed,
  stillness after arrival, zero buttons on the paper.
- Omission law untouched: a figure the machine cannot back does not
  print. Every idea below that needs split figures is *gated on the
  same C1 dependency* the HELD line itself waits for — no idea here
  resurrects a false figure early.
- Fonts: Anonymous Pro on the paper; Sora / Atkinson Hyperlegible only
  where the app already speaks in them (off-slip echoes). Never green.
  No AI-generated images — every visual below is type and rules only.
- Deliberately NOT repeated: the TEAR and the SLIP BOOK (parked in #21,
  group B — they are Emil's and they stay his), the SENTENCE (B7),
  quiet-chapter silence rules and `· SITE` (Finisher 3's, in flight),
  PARTIAL-with-evidence (B5), the corrected-duplicate stamp (B4).

Costs: XS ≈ a formatter / a conditional line · S ≈ one small state or
render change · M ≈ a new snapshot field, gesture, or surface touch ·
L would mean a new surface — nothing here is L.

---

## ACT I — HELD: the cast comes back on stage

### E1 · THE PROPORTION RULE
Under the returned HELD line, one mono line of block glyphs split in
SHUT/PEEK proportion — `███████░░` for 7 shut / 3 peek — capped at the
line width, minimum one glyph per non-zero side. The split is *seen*
before it is read; it is the only "chart" a typewriter can draw.
- **Mechanism:** pure string builder over `heldShutAtSeal` /
  `heldPeekAtSeal` at assembly; renders only when both figures print.
- **Cost:** XS. No state, no schema.

### E2 · THE FULL SHUT STAMP
When PEEK apps existed on lease but *none was ever entered*, the slip
strikes a boxed stamp under HELD: `[ FULL SHUT ]` — double-ruled box,
same ink, Anonymous Pro Bold. The machine's only celebration, delivered
the way machines celebrate: a stamp, not praise vocabulary (which the
standing laws ban from the paper anyway). If even one peek was entered,
no stamp — the absence is the statement.
- **Mechanism:** one boolean the enforcer already knows
  (`peekEnteredAtSeal`, derivable from peek-lease/intercept outcomes at
  seal) + a conditional boxed line in assembly.
- **Cost:** S. One seal-time boolean; the stamp is three text lines.

### E3 · THE LEASE LEDGER (PEEK, itemised by behaviour)
PEEK splits on the slip into the two things it actually was:
`{n} PEEK · NEVER ENTERED` and, per entered app, its lease minutes
listed — never summed with deflections, in their own ruled sub-section:
the doors that were opened *lawfully*, printed as plainly as the doors
that knocked and were refused. Today peeks print *nowhere true* — the
deflection filter (correctly) drops them, so a session where he leased
three times reads identically to one where he never touched them. The
slip should be able to say both things without confusing them.
- **Mechanism:** peek lease spans already pass through intercept/lease
  records; assembly lists entered peeks (time · name · leased span) in
  a separate section gated on the outcome vocabulary the A4 cure
  enumerates.
- **Cost:** M. Assembly section + lease-span plumbing; no schema if
  lease start/end already persist on the intercept row — priced as M
  until a builder confirms that field.

### E4 · THE ROSTER (long spans earn names)
At `RAN ≥ 4 HOURS`, the SHUT side prints *by name* — a centred mono
roster under HELD, credits-style: the apps that stayed shut for four
hours have earned naming; a 20-minute sit has not. Below the threshold,
counts only (the current tenant grammar, unchanged). Names are listed,
never summed — the roster obeys the slip's oldest law.
- **Mechanism:** seal-time name snapshot for the SHUT set
  (`heldShutNamesAtSeal`, a string list beside the split columns) +
  a threshold-gated roster block in the sheet.
- **Cost:** M. One persisted list on the seal row; render is a loop.

### E5 · TALLY STRIKES
Alternative print for the SHUT count at small numbers (≤12): tally
strokes grouped in fives — `||||  ||||  ||` — the wall-scratch count of
a held line, in the mono face. Counts you can recount with a finger.
Ships as an either/or against E1, never both (two charts is noise).
- **Mechanism:** formatter over `heldShutAtSeal`, grouped-by-5 string.
- **Cost:** XS.

---

## ACT II — RAN: the total takes the stage

### E6 · PROMISE vs KEPT (the duel) — *my headline pick*
RAN never prints alone again. PLANNED and RAN strike as facing totals
over a double rule, and between them the variance, in the slip's own
dialect: `OVERSHOT +12 MIN` / `CUT SHORT −38 MIN` / on an exact hit,
`KEPT TO THE MINUTE`. Every other line on the slip is a fact; this pair
is the *story* — what was promised at ARMED vs what the machine
witnessed at ENDED. FLOW slips (no PLANNED, lawfully) skip the duel and
E7 carries them.
- **Mechanism:** `sealedElapsedSeconds − sealedSpanTargetSeconds`
  (both fields already exist — Frist's seal-time source, C3) through
  the existing `longSpan` formatter with a sign; two ruled lines in
  the total section.
- **Cost:** S. Arithmetic + layout; no new state.

### E7 · THE ASTERISK TOTAL (span-escalating ceremony)
Till slips flank the total with asterisks, and the ceremony grows with
the amount. RAN's strike escalates by bucket: `<2h` plain ruled total
(as built) · `2–8h` flanked `* RAN 4 HOURS 12 MIN *` · `>8h` full
strike `*** RAN 12 HOURS ***` over a double rule. Nothing animates,
nothing recolours — the *type itself* gets louder, which is the only
loudness this paper allows.
- **Mechanism:** bucket on `sealedElapsedSeconds` selects one of three
  string templates in the total line.
- **Cost:** XS.

### E8 · AMOUNT IN WORDS (the cheque strike)
At `RAN ≥ 10 HOURS`, RAN prints twice: figures, then words on the next
line — `TEN HOURS AND FORTY MINUTES` — the way a cheque writes its
amount in words so it cannot be altered or skimmed. A double-digit
hour session is the slip's rarest artefact; it should read like a
document that knows it.
- **Mechanism:** a small number-to-words converter for the hour/minute
  pair (bounded domain: 10–24h, 0–59m) feeding one extra mono line.
- **Cost:** S–M. The converter is the only real code; ~40 lines, fully
  unit-testable, zero state.

### E9 · RAN IN THE HEAD (FLOW slips only)
On FLOW slips PLANNED is lawfully absent and RAN is — per the audit —
"the headline figure; flow's whole point is the duration." So print it
like one: RAN moves directly under the title at Anonymous Pro Bold
20sp, the largest strike on the paper, and the ENDED section keeps its
times only. Sealed/routine slips keep RAN in the total section (E6's
duel lives there); this is FLOW-only, where no duel exists.
- **Mechanism:** branch on slip kind in the sheet; one type-scale and
  placement change.
- **Cost:** XS–S.

---

## ACT III — DISMISSAL: the last act, remembered

### E10 · HEAVY PAPER (dismissal weighted by RAN) — *the act that ties all three*
The slip's dismissal drag resistance scales with its span bucket: a
short slip files on one pull (as today, DOWN after the 2s dwell); a
`≥8h` slip *half-feeds* on the first pull — slides a third of its
height, settles back into stillness — and files only on the second.
Not a lock, not a puzzle: weight. The paper is heavier because the day
was. Faulted slips are untouched (their explicit-act law stands).
- **Mechanism:** drag-offset threshold + settle-back spring parameterised
  by the same span bucket E7 uses; second pull within the dwell of the
  settle completes the existing dismissal path (persisted №, as built).
- **Cost:** M. Gesture maths on the sheet; no state beyond what the
  dismissal cure already persists.

### E11 · THE CARBON BACK
When dismissal completes, before the slip unmounts it shows its back
for one beat (~350ms): a plain mono face reading `FILED {HH:mm} · №{n}`
— the carbon copy acknowledging the filing. Then it is gone, and the
persisted dismissal means it stays gone. Dismissal stops being a
swat and becomes a *filing the machine receipts*.
- **Mechanism:** a back-face composable in the sheet + a timed
  two-step exit in the host (dismiss intent → back face → unmount),
  persisting the dismissed № at intent, exactly as the cure does now.
- **Cost:** M. One composable, one timer; no schema.

### E12 · THE GHOST LINE (same-day echo, off the paper)
For the rest of the day, the Deck's bottom hairline carries the filed
slip in one line — `№041 · RAN 4 HOURS 12 MIN · FILED 07:42` —
Atkinson, quiet, gone at midnight. Not the Slip Book (no surface, no
archive, nothing tappable): just the machine remembering, in the room
where the next session starts, what the last one ran. If he dismisses
it in anger at 1 AM, the day still knows.
- **Mechanism:** extend the existing `monolith_receipt` prefs record
  with the RAN seconds + filed-at stamp already known at dismissal;
  Deck reads it if `filedAt` is today.
- **Cost:** S. Two prefs fields + one conditional line on the Deck.

### E13 · READ-AT PROVENANCE (the stub remembers the reader)
The persisted dismissal record gains `readForSeconds` (published →
dismissed). It prints nowhere on the slip — it surfaces only if/when a
stub or re-read surface exists, as `READ FOR {longSpan}` under the №:
how long the verdict sat with him before he filed it is a fact about
the session too, and today it evaporates.
- **Mechanism:** both stamps exist (`receiptPublishedMonoMs`, dismissal
  instant); persist their difference beside the dismissed №.
- **Cost:** S. One prefs long; zero render until a surface wants it.

---

## The three I would build first, if Ren makes me choose

1. **E6 PROMISE vs KEPT** — cheapest story on the slip; two existing
   fields, one subtraction, and the total section finally *means*
   something. (S)
2. **E2 FULL SHUT** — the HELD split's homecoming gift: one boolean,
   one stamp, and the returned line arrives with drama already in it
   instead of as a bare corrected figure. (S, gated on C1 like HELD
   itself)
3. **E10 HEAVY PAPER** — the only idea here that makes dismissal
   *feel* like what it closes; it borrows E7's buckets, so the slip's
   whole back half speaks one dialect of weight. (M)

## What I am explicitly NOT doing

- Not touching the TEAR / SLIP BOOK / SENTENCE — parked in #21, other
  seats' inventions, respected as such.
- Not printing any split figure before the C1 seal columns land —
  every Act I idea inherits HELD's own gate, by design.
- Not proposing colour, icons, images, or animation on the paper —
  stillness and one ink are binding acquittals, not limitations.

— Echo (crazy-ideas, issue #10)
