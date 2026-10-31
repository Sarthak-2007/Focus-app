# FINISH — THE EDGE TABLE: EVERY DOOR-STATE THE RECEPTION CAN PRINT

Seat: **Kante (finisher, seat 4)**, Pforte's cell, GitHub issue #14.
Scope: gallery standard **at the edges only** — the CURED total
(AUDIT-setup-sheet.md §2 S-1 + §5 Blank item 6) against the door
cards, for **every** combination of door states the sheet can
ever print, exhausted and proved consistent. No app code edited.
Nothing compiled, no test run (build law).

Ground, read whole from the working tree (`agent/issue-14`):
`ui/components/ReceptionSheet.kt` (`receptionTotal`,
`ReceptionDoorCard`, `receptionDoorWord`, `receptionDoorJob`,
`receptionDoorPrepare`, `receptionAbsenceSentence`,
`receptionAgedProofSentence`), `service/WatchStore.kt`
(`PermPrint`, `WatchStore.permPrintAt`, `STALE_AFTER_MS =
45_000`), `ui/components/GuardLine.kt` (`guardLineFor`),
`studio/plan/AUDIT-setup-sheet.md` §2 S-1/S-12, §4 F-4, §5
Blank item 6, and `app/src/test/.../ReceptionSheetTest.kt`
(as the builder's lane left it — the flipped stale test is
already on disk under a new name; §6 reconciles).

**Verdict, up front:** after the S-1 + Blank-6 + S-12 cure,
**no combination survives in which the total and a card
factually disagree — proven absence, §4.** Two candidate
charges were derived and discharged (§4, C-1/C-2), one
precedence silence is priced and intentional (C-3), and one
non-truth delta remains open against the builder: **DELTA-1,
the F-4 key dress is specced but not yet in the code** (§3).

---

## §0 · THE DERIVATION GROUND — why one table can be exhaustive

Per counted door, the print kind is a pure function of
(snapshot, item, nowMono) — `WatchStore.permPrintAt`:

| Measured fact | Print kind |
|---|---|
| no measurement for the item, or measurement with `perm == null` | `UNMEASURED` |
| `perm == GRANTED` and `nowMono − measuredMono > 45_000` (strictly) | `STALE` |
| `perm == GRANTED` and `nowMono − measuredMono ≤ 45_000` | `GRANTED` |
| `perm == MISSING`, **any age** (a missing grant never stales) | `MISSING` |

Boundary, exact: age **45_000 → GRANTED**; age **45_001 →
STALE**. The four kinds are mutually exclusive and jointly
exhaustive per door, so the four counted doors
(`WatchItemId.COUNTED`, board order **ACCESSIBILITY ·
USAGE ACCESS · DEVICE ADMIN · NOTIFICATION LISTENER**) admit
exactly 4⁴ = **256 assignments**, plus the **null snapshot**.

Three structural facts do the consistency work before any
row is read:

1. **One function.** Cards, the doors'-foot Guard line, and
   the total all derive from the *same* `permPrintAt`.
2. **One snapshot, one instant.** In the `ReceptionSheet`
   composable the card loop, `guardLineFor(snapshot, nowMono)`
   and `receptionTotal(snapshot, nowMono)` are all fed the
   same `snapshot` and the same remembered `nowMono` (re-derived
   on the 5 s ticker). No frame can split an instant between
   a card and the total.
3. **Kind-counts collapse the 256.** `receptionTotal` reads
   only the *counts and identities* of each kind: its branch
   is chosen by (any UNMEASURED) → (any MISSING) → (any
   STALE) → else, its count is `g` alone, and within a branch
   the sentence is a join over the relevant doors **in
   COUNTED board order**. So the 256 assignments collapse to
   the 35 solutions of `g+m+s+u = 4` (g/m/s/u = counts of
   GRANTED/MISSING/STALE/UNMEASURED), plus the null row.
   Identity still matters *inside* the sentence (§1's
   dictionaries), never to the branch or the count.

Notation: a row is written `(g, m, s, u)`. **Assignments** =
`4!/(g!·m!·s!·u!)` — the number of the 256 door-assignments
the row covers. The row sums are the exhaustiveness proof:
UNMEASURED rows 175 + MISSING rows 65 + STALE rows 15 +
all-GRANTED row 1 = **256**. ✓

---

## §1 · THE SENTENCE DICTIONARIES — every exact word the total can print

The total prints exactly one of four sentence forms. The two
per-door forms are dictionaries; a multi-door sentence is the
relevant entries joined with **one space**, in COUNTED board
order, no other separator, no conjunction.

**Fixed sentences**

- Reading (null snapshot, or any UNMEASURED):
  `THE DOORS ARE STILL BEING READ.`
- All fresh-granted (S-9, full stop — never a blocking claim):
  `ALL FOUR DOORS SET.`

**Absence dictionary** — `receptionAbsenceSentence`, one entry
per MISSING door (A-2 verbatim; S-6: the admin entry drops
"MID-SESSION"). Note the listener entry names **THE
LISTENER**, not its board name — the one identity exception:

| Door | Exact absence sentence |
|---|---|
| ACCESSIBILITY | `WITHOUT ACCESSIBILITY, BLOCKING CANNOT PROVE ITSELF — SESSIONS WILL NOT SEAL.` |
| USAGE ACCESS | `WITHOUT USAGE ACCESS, BLOCKING CANNOT PROVE ITSELF — SESSIONS WILL NOT SEAL.` |
| DEVICE ADMIN | `WITHOUT DEVICE ADMIN, MONOLITH CAN BE UNINSTALLED.` |
| NOTIFICATION LISTENER | `WITHOUT THE LISTENER, MESSAGES ARRIVE AS THEY COME — NOTHING IS HELD.` |

**Aged-proof dictionary** — `receptionAgedProofSentence`, one
entry per STALE door: `<boardName> IS SET — THE LAST PROOF
AGED OUT. CHECK RE-PROVES IT.` — i.e. the four literal
strings with `ACCESSIBILITY` / `USAGE ACCESS` / `DEVICE
ADMIN` / `NOTIFICATION LISTENER` in the board-name slot.

Worked joins (so no row below needs re-derivation):

- Two lacks, ACCESSIBILITY + NOTIFICATION LISTENER missing:
  `WITHOUT ACCESSIBILITY, BLOCKING CANNOT PROVE ITSELF — SESSIONS WILL NOT SEAL. WITHOUT THE LISTENER, MESSAGES ARRIVE AS THEY COME — NOTHING IS HELD.`
- All four stale: the four aged-proof entries in board order,
  space-joined — `ACCESSIBILITY IS SET — …` through
  `NOTIFICATION LISTENER IS SET — THE LAST PROOF AGED OUT. CHECK RE-PROVES IT.`

---

## §2 · THE EXHAUSTIVE TABLE — THE CURED TOTAL, BY KIND-COUNT

**Precedence (the cured `receptionTotal`, in code order):**
null snapshot → READING. Else any `u ≥ 1` → READING.
Else any `m ≥ 1` → ABSENCE. Else any `s ≥ 1` → AGED-PROOF.
Else → ALL-SET. **Count:** `null` (withheld) in every READING
row; `g` in every other row. On the sheet the count renders
as `GUARD <count>/4` **only when non-null** — a READING row
prints the sentence alone, no number anywhere in the total.

### Row 0 — no snapshot

| Case | Assignments | Count | Sentence |
|---|---|---|---|
| `snapshot == null` | — (outside the 256) | `null` — no `GUARD n/4` prints | `THE DOORS ARE STILL BEING READ.` |

(Cards in this case: the composable substitutes
`PermPrint.UNMEASURED` for every door — see §3; the total
and the cards are in the same row, by construction.)

### Branch R — READING: `u ≥ 1` (20 rows, 175 assignments)

Every row in this branch prints **count `null`** and the
single sentence `THE DOORS ARE STILL BEING READ.` — whatever
the other three doors are. No absence sentence, no aged-proof
sentence, no number is printed over an unread door (A-4).

| (g, m, s, u) | Assignments | | (g, m, s, u) | Assignments |
|---|---|---|---|---|
| (0, 0, 0, 4) | 1 | | (3, 0, 0, 1) | 4 |
| (1, 0, 0, 3) | 4 | | (2, 1, 0, 1) | 12 |
| (0, 1, 0, 3) | 4 | | (2, 0, 1, 1) | 12 |
| (0, 0, 1, 3) | 4 | | (1, 2, 0, 1) | 12 |
| (2, 0, 0, 2) | 6 | | (1, 1, 1, 1) | 24 |
| (1, 1, 0, 2) | 12 | | (1, 0, 2, 1) | 12 |
| (1, 0, 1, 2) | 12 | | (0, 3, 0, 1) | 4 |
| (0, 2, 0, 2) | 6 | | (0, 2, 1, 1) | 12 |
| (0, 1, 1, 2) | 12 | | (0, 1, 2, 1) | 12 |
| (0, 0, 2, 2) | 6 | | (0, 0, 3, 1) | 4 |

Subtotal: 1 + 12 + 54 + 108 = **175** assignments. ✓
(Includes the empty snapshot — `(0,0,0,4)` — and any
measurement carrying `perm == null`, which `permPrintAt`
also reads as UNMEASURED.)

### Branch A — ABSENCE: `u = 0, m ≥ 1` (10 rows, 65 assignments)

Count = `g`. Sentence = the absence entries (§1) for
**exactly the missing doors**, board order, space-joined —
one sentence per lacking door (Blank item 6); a single-lack
row prints exactly the pre-cure sentence. STALE doors, when
present, contribute **no** sentence in this branch (see §4
C-1 — priced, intentional, not a contradiction).

| (g, m, s, u) | Assignments | Count | Sentence(s) |
|---|---|---|---|
| (3, 1, 0, 0) | 4 | 3 | absence ×1 — the one missing door's entry (4 identity variants) |
| (2, 1, 1, 0) | 12 | 2 | absence ×1 — the missing door's entry; the stale door silent here |
| (1, 1, 2, 0) | 12 | 1 | absence ×1; both stale doors silent here |
| (0, 1, 3, 0) | 4 | 0 | absence ×1; all three stale doors silent here |
| (2, 2, 0, 0) | 6 | 2 | absence ×2, board order (6 identity pairs) |
| (1, 2, 1, 0) | 12 | 1 | absence ×2, board order; stale door silent here |
| (0, 2, 2, 0) | 6 | 0 | absence ×2, board order; both stale doors silent here |
| (1, 3, 0, 0) | 4 | 1 | absence ×3, board order (4 identity triples) |
| (0, 3, 1, 0) | 4 | 0 | absence ×3, board order; stale door silent here |
| (0, 4, 0, 0) | 1 | 0 | absence ×4 — all four entries, board order |

Subtotal: 4+12+12+4+6+12+6+4+4+1 = **65**. ✓

### Branch P — AGED PROOF: `u = 0, m = 0, s ≥ 1` (4 rows, 15 assignments)

Count = `g` (fresh proofs only — the count's house meaning
everywhere, GuardLine §2.3: GRANTED **and not stale**).
Sentence = the aged-proof entries (§1) for **exactly the
stale doors**, board order, space-joined. The word `WITHOUT`
can never appear in this branch.

| (g, m, s, u) | Assignments | Count | Sentence(s) |
|---|---|---|---|
| (3, 0, 1, 0) | 4 | 3 | aged-proof ×1 — the one stale door's entry (4 identity variants) |
| (2, 0, 2, 0) | 6 | 2 | aged-proof ×2, board order (6 identity pairs) |
| (1, 0, 3, 0) | 4 | 1 | aged-proof ×3, board order (4 identity triples) |
| (0, 0, 4, 0) | 1 | 0 | aged-proof ×4 — all four entries, board order |

Subtotal: 4+6+4+1 = **15**. ✓

### Branch G — ALL SET: `u = m = s = 0` (1 row, 1 assignment)

| (g, m, s, u) | Assignments | Count | Sentence |
|---|---|---|---|
| (4, 0, 0, 0) | 1 | 4 | `ALL FOUR DOORS SET.` |

**Grand total: 175 + 65 + 15 + 1 = 256 assignments, 35
kind-count rows, + the null row. Exhausted.** No fifth
sentence form exists in the function; no branch is
reachable by a row outside its own section (the precedence
chain is total and the sections are disjoint by
construction: `u≥1` / `u=0,m≥1` / `u=0,m=0,s≥1` / `g=4`).

---

## §3 · THE CARD SIDE — THE FULL RENDER CONTRACT, PER PRINT KIND

`ReceptionDoorCard`, as cured (S-12 landed in code; F-4 is
the contract, with DELTA-1 below where code still differs).
Every card, every kind: the door's `boardName` in
`PillBadgeTextStyle` (onSurfaceVariant), the door word at its
right (Atkinson Hyperlegible Bold 15sp), the **job line
always** (§3 annex), a hairline under the card.

| Element | GRANTED | MISSING | STALE | UNMEASURED |
|---|---|---|---|---|
| **Word** (`receptionDoorWord`, K-1 reprint) | `SET` | `NOT SET` | `UNPROVED` | `NOT CHECKED` |
| Word colour | onSurfaceVariant (dim — the standing board grammar, acquitted) | onBackground | onBackground | onBackground |
| **Job line** | always, the door's job (§3 annex) | always | always | always |
| **Gloss** | none | none | **yes** — `THE LAST PROOF AGED OUT — CHECK RE-PROVES IT` (Atkinson 400, 12sp/17sp, onSurfaceVariant; **no full stop** — it is not the total's sentence) | none |
| **Prepare line** (S-12) | none | **yes** — the door's full escort (§3 annex) | none (the gloss stands where an escort would) | **none** — an unread door carries no escort (S-12) |
| **Key** | **none** | `FIX — <boardName>`, tag `reception_fix_<ITEM>` | `CHECK — <boardName>`, tag `reception_check_<ITEM>` | `CHECK — <boardName>`, tag `reception_check_<ITEM>` (S-12: a sweep is the only honest verb for an unread door) |
| **Key dress** (F-4 contract) | — | **surfaceVariant fill, onBackground text** — identical to CHECK and to `OPEN THE LIST`; ink is DONE's alone | surfaceVariant fill, onBackground text (as built ✓) | surfaceVariant fill, onBackground text (as built ✓) |
| Key geometry / voice | — | 48dp, radius 8dp, 1dp dip while `fixPressed`, seat knock on press (10ms; amplitude 0.06 night / 0.10 day; 300Hz), fires `onFix` | 48dp, radius 8dp, no dip, no knock in code, fires `onCheck` | 48dp, radius 8dp, no dip, no knock in code, fires `onCheck` |

**DELTA-1 (open, builder's — dress, not truth).** The F-4
contract above is **not yet the code for FIX**: the MISSING
branch still builds `.background(inkControlFill())` with
`color = onInkControlFill()`, byte-identical in dress to
DONE — so as built, the sheet carries **two** ink keys and
its "one ink key" claim is still false. The cure is the
two-line change specced in DESIGN-composition.md §12a
(`inkControlFill()` → `scheme.surfaceVariant`,
`onInkControlFill()` → `scheme.onBackground`; dip, knock,
tag, height untouched; PARK's own FIX key keeps its ink —
F-4 rules this sheet only). DELTA-1 changes no word, count
or sentence in §2 and no verdict in §4; it is filed here so
the contract in this table is never mistaken for the
as-built dress.

### §3 annex — job and prepare lines, verbatim per door

Job lines (`receptionDoorJob`; kind-invariant, printed on
every card):

- ACCESSIBILITY: `THE INTERCEPTOR. Shuts the apps you named, the moment they open. NOTHING LEAVES THIS PHONE — MONOLITH HOLDS NO NETWORK PERMISSION.`
- USAGE ACCESS: `THE WITNESS. Tells Monolith which app is in front, so the seal can be proved.`
- DEVICE ADMIN: `THE ANCHOR. While this stands, Android refuses to uninstall Monolith. TO TAKE IT BACK: SWITCH MONOLITH OFF IN THAT SAME LIST — THEN IT UNINSTALLS LIKE ANY APP.`
- NOTIFICATION LISTENER: `THE MAIL ROOM. Holds the shut apps' messages while a session runs — you read them in the digest. HELD ON THIS PHONE, IN THE DIGEST — NOTHING LEAVES THIS PHONE.`

Prepare lines (`receptionDoorPrepare`; printed **only**
under a MISSING card):

- ACCESSIBILITY: `YOU'LL SEE A LIST OF APPS. FIND MONOLITH, SWITCH IT ON, COME BACK. ANDROID WARNS IN STRONG WORDS — THAT WARNING IS STANDARD FOR EVERY BLOCKING APP. IT IS THE DOOR WORKING.`
- USAGE ACCESS: `YOU'LL SEE A LIST OF APPS. FIND MONOLITH, ALLOW USAGE ACCESS, COME BACK.`
- DEVICE ADMIN: `YOU'LL SEE A LIST OF DEVICE ADMINS. FIND MONOLITH, ACTIVATE, COME BACK.`
- NOTIFICATION LISTENER: `YOU'LL SEE A LIST OF APPS. FIND MONOLITH, ALLOW, COME BACK. ANDROID MAY WARN — THAT WARNING IS STANDARD. IT IS THE DOOR WORKING.`

---

## §4 · THE CONSISTENCY PROOF — TOTAL AGAINST CARDS, EVERY ROW

Five invariants, each a direct read of §0's ground. Where
`count` is non-null (Branches A/P/G):

- **I-1 · Count = SET cards.** `total.count` counts prints
  `== GRANTED`; a card prints `SET` iff its print is
  GRANTED. So `total.count` = the number of `SET` words on
  the sheet, and the rendered `GUARD <count>/4` equals the
  doors'-foot Guard line's number (same derivation,
  `guardLineFor`). In Branch R no number prints in the
  total **and** the Guard line prints nothing — both
  withhold together.
- **I-2 · Keys = non-granted cards.** #FIX keys = `m`
  (exactly the `NOT SET` cards), #CHECK keys = `s + u`
  (exactly the `UNPROVED` + `NOT CHECKED` cards), #keyless
  cards = `g`. No card offers a verb its kind forbids:
  FIX only for a lack, CHECK for aged proof and for the
  unread, nothing for a door that stands fresh-proved.
- **I-3 · Absence ↔ NOT SET, 1:1.** In Branch A the absence
  sentences and the `NOT SET` cards name the **same door
  set** (both are `filter { MISSING }` over COUNTED, board
  order). A door priced `WITHOUT …` in the total always
  stands `NOT SET` + FIX + escort directly above it; a
  `NOT SET` door is never unpriced in the total.
- **I-4 · Aged proof ↔ UNPROVED, 1:1 (Branch P).** The
  aged-proof sentences and the `UNPROVED` cards name the
  same door set, and each such card carries the gloss whose
  words the total's sentence extends with the door's name
  and `IS SET —`. In Branch P no card and no sentence
  anywhere on the sheet says `WITHOUT` or `NOT SET` of a
  stale door — **this is S-1's cured lie, and it is dead in
  every row of the branch.**
- **I-5 · No kind is ever re-priced as another kind.**
  GRANTED is priced only as set (Branch G / silence);
  MISSING only as absence; STALE only as aged proof
  (Branch P), as card-local gloss + CHECK (Branch A, C-1),
  or under the reading withholding (Branch R); UNMEASURED
  only as unread. The total's branch chain and the card's
  `when (print)` are both exhaustive over the same four
  kinds — there is no fall-through row on either side.

### Candidate charges, derived and discharged

**C-1 · Mixed rows `(g, m≥1, s≥1, 0)` — the total is silent
about the stale doors.** Six rows — (2,1,1,0), (1,1,2,0),
(0,1,3,0), (1,2,1,0), (0,2,2,0), (0,3,1,0); 50 assignments.
Derivation: Branch A's sentence is the join over MISSING
only, so a stale door gets no total sentence, while its
card prints `UNPROVED` + gloss + CHECK. **Verdict: NOT a
disagreement.** Silence is not a counter-claim: no total
sentence asserts the stale door is missing, granted-fresh,
or priced at all, and the screen as a whole prices it —
on its own card, in the same instant, with the gloss and
the CHECK key. The count also stays honest: `g` excludes
the stale door on both sides (I-1: it prints `UNPROVED`,
not `SET`). This precedence is S-1's own text ("any
MISSING → …; **no MISSING but** any STALE → …"), chosen by
the audit, not an accident of the join. Residual, recorded
honestly: the total **alone**, read without the cards,
under-reports stale in mixed rows. If Pforte ever wants
the total itself exhaustive over kinds, the priced change
is appending the aged-proof join after the absence join in
Branch A — **not** filed as a charge, because nothing
printed is false.

**C-2 · Branch P's worst surface: row (0, 0, 4, 0) prints
`GUARD 0/4` over four `… IS SET — …` sentences, while the
four cards print `UNPROVED`, not `SET`.** Derivation:
three different claims are being conflated by a cold read,
and each is true under its own derivation — (i) the count
is **fresh-proof count** (GuardLine §2.3: GRANTED *and not
stale*), so four aged proofs count 0 on the header, the
Guard foot-line, and the total alike — one fact, one
number, three surfaces; (ii) the card word `UNPROVED` is
K-1's reprint of the **print kind** STALE — a claim about
proof, not about the grant; (iii) the total's `IS SET` is
a claim about the **underlying grant**, and it is exactly
true: STALE derives only from `perm == GRANTED` aged past
45 s (§0), never from MISSING — and the same sentence
carries its own qualifier, `— THE LAST PROOF AGED OUT.
CHECK RE-PROVES IT.`, so read whole it asserts precisely
(ii) + (iii) together. **Verdict: NOT a disagreement** —
no derivation yields opposite truth values for the same
proposition. The lexical tension is real and already
owned: the code's own comment records that the audit
named the aged-proof sentence "Stachel's words" **without
quoting a total sentence verbatim**; the sentence was
composed from the S-12 gloss and flagged for Pforte's
confirmation. That confirmation is the only open item on
this row, and it is a wording ruling, not a truth defect.

**C-3 · Branch R precedence: known lacks are silent in the
total.** In e.g. (0, 3, 0, 1) three doors stand `NOT SET` +
FIX while the total prints only `THE DOORS ARE STILL
BEING READ.` Derivation: the sentence is true (one door
*is* still being read), the count is withheld rather than
printed short, and A-4 acquits the rule — a total is never
printed over an unread door. The cards carry the lacks;
the total carries the withholding; neither contradicts
the other. **Verdict: NOT a disagreement** — the same
posture the Guard line takes (it prints nothing in
Branch R, I-1).

**C-4 · The boundary instant.** At age exactly 45_000 a
door is GRANTED on its card and counted in the total; at
45_001 it is STALE on both (§0, strict `>`). Because card
and total share one `nowMono` per frame (§0.2), no frame
exists in which one side has crossed and the other has
not — the 5 s ticker moves both together, and a door going
`UNPROVED` in front of him (the lane's stated behaviour)
moves its total sentence in the same recomposition.
**Verdict: no split-instant row exists.**

**C-5 · DELTA-1 (F-4 dress) as a candidate charge.** A
dress difference asserts no fact about a door; it cannot
make the total and a card disagree about a state. Filed
as a builder delta in §3, not a charge here.

**Conclusion: across Row 0 + all 35 kind-count rows
(256 assignments), after the S-1 + Blank-6 + S-12 cure,
there is no combination in which the total and a card
factually disagree. Proven absence — the two discharges
above are the only rows where a contradiction could even
be alleged, and both allegations fail on derivation.**

---

## §5 · THE TEST SPEC — WHAT THE FLIPPED `ReceptionSheetTest` MUST CARRY

Status key: **[disk]** = already present in the builder's
test file as read for this table · **[prescribe]** = must be
added (or an on-disk assertion strengthened, as noted).
Fixtures assumed: the on-disk `snapshotWith` (one shared
`measuredMono`), `snapshotWithMonos` (per-door instants —
the S-1 fixture), `allGranted`. Pure-function tests assert
on `receptionTotal(...)` unless marked *(compose)*.
**No test in this list was run by this seat.**

### The total — Branch G and Branch A

1. **`totalCountsWholeWhenAllFourGranted`** [disk] —
   (4,0,0,0): `count == 4`; `sentence == "ALL FOUR DOORS SET."`
   (and, by exact equality, never the struck
   "…BLOCKING STANDS WHOLE.").
2. **`totalNamesTheSealSentenceForAWitnessLack`** [disk] —
   (3,1,0,0), USAGE ACCESS missing: `count == 3`; sentence ==
   the USAGE ACCESS absence entry, exactly.
3. **`totalNamesTheSealSentenceForAnInterceptorLack`** [prescribe] —
   (3,1,0,0), ACCESSIBILITY missing: `count == 3`; sentence ==
   the ACCESSIBILITY absence entry, exactly. (Shares a code
   branch with #2 but interpolates a different board name —
   identity is part of the contract, §1.)
4. **`totalNamesTheAnchorAndMailRoomSentences`** [disk] —
   (3,1,0,0) DEVICE ADMIN missing: sentence ==
   `WITHOUT DEVICE ADMIN, MONOLITH CAN BE UNINSTALLED.`
   (no "MID-SESSION" — S-6); (3,1,0,0) NOTIFICATION LISTENER
   missing: sentence == the THE LISTENER entry, exactly.
5. **`totalPricesEveryMissingDoorOneSentenceEach`** [disk] —
   (2,2,0,0), ACCESSIBILITY + NOTIFICATION LISTENER missing:
   `count == 2`; sentence == the two entries joined with one
   space, board order (the §1 worked join).
6. **`totalPricesThreeMissingDoorsInBoardOrder`** [prescribe] —
   (1,3,0,0) (e.g. USAGE ACCESS + DEVICE ADMIN + NOTIFICATION
   LISTENER missing): `count == 1`; sentence == the three
   entries joined, board order, no aged-proof or all-set text.
7. **`totalPricesAllFourMissingDoors`** [prescribe] —
   (0,4,0,0): `count == 0`; sentence == all four absence
   entries joined, board order.

### The total — Branch P and the flipped stale test

8. **`staleGrantReadsAsLackInTheTotal` — FLIPPED** [disk,
   renamed `staleGrantReadsAsAgedProofNotLackInTheTotal`;
   either name carries it, the assertions are the contract] —
   this is the test that used to **codify S-1's lie**
   (it asserted count 0 and a sentence starting
   `WITHOUT ACCESSIBILITY` for an all-stale snapshot).
   Flipped assertions, both halves mandatory:
   - one stale, (3,0,1,0), ACCESSIBILITY granted at
     `now − 60_000`, the rest fresh: `count == 3`;
     `sentence == "ACCESSIBILITY IS SET — THE LAST PROOF AGED OUT. CHECK RE-PROVES IT."`;
   - all stale, (0,0,4,0), all granted at `now − 60_000`:
     `count == 0`; sentence contains **no** `WITHOUT`;
     every counted door's board name appears.
   **[prescribe strengthening]** the all-stale half must
   assert the **exact** four-entry join (§1), not only
   `contains(boardName)` — contains() cannot catch a
   duplicated, reordered, or absence-contaminated join.
9. **`totalAgedProofSentencesJoinPerStaleDoorInBoardOrder`** [prescribe] —
   (2,0,2,0): `count == 2`, sentence == the two stale doors'
   aged-proof entries joined, board order; (1,0,3,0):
   `count == 1`, sentence == the three entries joined.
   Guards the Branch-P rows #8 leaves at ×1 and ×4.
10. **`totalMissingBranchWinsOverStale`** [disk] —
    (2,1,1,0), ACCESSIBILITY stale + USAGE ACCESS missing:
    `count == 2`; sentence == the single USAGE ACCESS
    absence entry, exactly (exact equality is what proves
    the stale door added no sentence — C-1's guard).
11. **`totalMissingBranchSuppressesEveryAgedProofSentence`** [prescribe] —
    (0,2,2,0): `count == 0`; sentence == the two absence
    entries joined; `assertFalse(sentence.contains("AGED OUT"))`.
    Guards the mixed rows at s = 2, where #10 (s = 1) cannot
    see a per-stale leak.

### The total — Branch R and Row 0

12. **`totalNeverPrintsOverAnUnreadDoor`** [disk, strengthen] —
    null snapshot: `count == null` **[prescribe: also assert
    `sentence == "THE DOORS ARE STILL BEING READ."` — on disk
    only the count is asserted for null]**; one-door snapshot
    (1,0,0,3): `count == null`, sentence == the READING
    sentence [disk].
13. **`totalReadsAsStillBeingReadForEveryUnmeasuredKindCount`** [prescribe] —
    one looping test over fixtures for **all 20 Branch-R
    rows** (each `(g,m,s,u)` of §2 Branch R, built by omitting
    the `u` doors from the map and ageing the `s` doors via
    `snapshotWithMonos`): for every fixture,
    `count == null` and `sentence == "THE DOORS ARE STILL BEING READ."`.
    This is the test that makes Branch R *exhausted*, not sampled.
14. **`totalWithholdsCountForEmptySnapshot`** [prescribe] —
    `WatchSnapshot.EMPTY` (= (0,0,0,4)): `count == null`,
    sentence == READING.
15. **`totalTreatsNullPermMeasurementAsUnmeasured`** [prescribe] —
    a counted door carrying a measurement with `perm == null`,
    the other three fresh-granted: `count == null`, sentence
    == READING. (Guards `permPrintAt`'s second UNMEASURED
    source, §0 — a state no `snapshotWith` fixture can reach.)

### The derivation seam — boundary and never-stale

16. **`staleBoundaryIsStrictlyPastFortyFiveSeconds`** [prescribe] —
    one door granted at `now − 45_000`, rest fresh:
    total counts it (Branch G posture for that door — no
    aged-proof sentence). The same fixture at
    `now − 45_001`: that door is STALE — (3,0,1,0) aged-proof
    sentence, `count == 3`. Guards §0's strict `>` and C-4.
17. **`missingMeasurementNeverStales`** [prescribe] —
    one door MISSING at `now − 3_600_000`, rest fresh-granted:
    (3,1,0,0) absence posture — `count == 3`, sentence ==
    that door's absence entry, no `AGED OUT` anywhere.
    Guards §0's "a missing grant never stales" against any
    future symmetry refactor of `permPrintAt`.

### The cards — compose guards for §3's contract

18. **`doorWordsReprintTheBoardGrammar`** [disk] —
    GRANTED→`SET`, MISSING→`NOT SET`, STALE→`UNPROVED`,
    UNMEASURED→`NOT CHECKED`, exact.
19. **`sheetPrintsEachDoorByItsLiveState`** [disk] *(compose)* —
    (3,1,0,0) USAGE ACCESS missing: `SET` ×3, `NOT SET` ×1,
    `reception_fix_USAGE_ACCESS` exists, `GUARD 3/4` and the
    absence sentence on the surface.
20. **`sheetMissingCardPrintsPrepareAndFixKeyAndNoGloss`** [prescribe] *(compose)* —
    for a MISSING door: its exact prepare line (§3 annex) is
    on the surface; its gloss text is **not**; no
    `reception_check_<ITEM>` node exists for it.
21. **`sheetStaleCardPrintsGlossAndCheckKeyAndNoPrepareNoFix`** [prescribe] *(compose)* —
    a STALE door (granted at `now − 60_000`, rest fresh):
    `UNPROVED` on the surface; gloss
    `THE LAST PROOF AGED OUT — CHECK RE-PROVES IT` exists;
    `reception_check_<ITEM>` exists; `reception_fix_<ITEM>`
    does not exist; that door's prepare line is **not** on
    the surface; the total on the same surface reads
    `GUARD 3/4` + the aged-proof sentence (I-4, on one screen).
22. **`sheetUnmeasuredCardPrintsNotCheckedAndCheckKeyAndNoPrepare`** [prescribe] *(compose)* —
    snapshot null (Row 0): `NOT CHECKED` ×4;
    `reception_check_<ITEM>` exists (spot-check one door);
    no prepare line and no `GUARD n/4` text anywhere; the
    READING sentence is on the surface.
23. **`sheetGrantedCardCarriesNoKeyNoGlossNoPrepare`** [prescribe] *(compose)* —
    all fresh-granted: `SET` ×4; zero `reception_fix_*` and
    zero `reception_check_*` nodes; no gloss, no prepare
    text; `GUARD 4/4` + `ALL FOUR DOORS SET.` on the surface.
24. **`sheetCheckKeyRoutesOnCheckForStaleAndUnmeasured`** [prescribe] *(compose)* —
    clicking a STALE door's CHECK and (in a second fixture)
    an UNMEASURED door's CHECK each fire `onCheckClick`
    exactly once; FIX routing stays guarded by the on-disk
    `sheetFixKeyRoutesTheLackingDoor`.
25. **`sheetFixKeyWearsSurfaceVariantNotInk`** [prescribe —
    BLOCKED on DELTA-1] — a screenshot-harness colour
    assertion (key-fill sampled at the FIX key's centre ==
    the CHECK key's fill, ≠ DONE's fill), not a compose-text
    assertion; it can only pass once §3's DELTA-1 lands, and
    it is the guard that keeps F-4 from regressing silently.

### The keystone — every row, one loop

26. **`totalAndCardsNeverDisagreeAcrossAllKindCounts`** [prescribe] —
    a table-driven loop over **representative assignments
    for all 35 kind-count rows + the null snapshot** (for
    rows whose sentence is identity-dependent, one fixture
    per row with a stated door assignment, the dictionaries
    of §1 supplying the expected string). Per fixture assert
    all of: (a) `count == g`, or `count == null` iff the row
    is Branch R / Row 0; (b) `sentence` == the row's §2
    sentence, exact; (c) `receptionDoorWord(permPrintAt(...))`
    per door == the kind's §3 word; (d) the I-2 key census
    implied by the kinds (#FIX == m, #CHECK == s+u).
    Tests 1–17 give each branch a *named* failure; this loop
    is what makes the word "every" in this table's title a
    guarded claim rather than a prose one.

**Coverage check (rows → tests):** Branch G → 1, 23 · Branch
A singles → 2, 3, 4 · A joins → 5, 6, 7 · Branch P → 8, 9 ·
mixed A-over-P → 10, 11 · Branch R (all 20 rows) → 12, 13,
14, 15, 22 · Row 0 → 12, 22 · boundary/never-stale seam →
16, 17 · card contract per kind → 18, 19, 20, 21, 22, 23,
24 · F-4 dress → 25 (blocked on DELTA-1) · all rows at
once → 26. **No row of §2 and no cell of §3's contract
table is left without a named guard.**

---

Filed for Pforte's merge read. Open items carried, none
hidden: **DELTA-1** (F-4 FIX dress, §3 — builder's two-line
change, guard test 25 waits on it); **C-2's wording ruling**
(the composed aged-proof sentence awaits Pforte's
confirmation, already flagged in the code's own comment);
**C-1's optional exhaustiveness** (aged-proof join appended
in Branch A) is Pforte's call, not a charge. The S-1 lie
itself is dead in all 256 assignments + the null row.

— Kante (finisher), issue #14 cell
