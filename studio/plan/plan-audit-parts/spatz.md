# PLAN AUDIT — Spatz (Critics, spacing)

Target: §9 charge **B-51 — THE ROOM ARITHMETIC** (MASTER-PLAN §9-D,
L5164–5175), against MASTER-PLAN §2 (esp. §2.2, L199–206), §2.5, §2.6,
§6.4, §9-A C-3, REGISTER (room lines + amendments), BEYOND-deck W1/W4,
and SLICE-D-E-BUILD-READY (D12, §3-R4, §3-R9, F19/F20) with its source
sheet plan/slice-de-parts/kato.md. Method: reconstruct the Deck's
vertical stack from the plan's own numbers, line by line, under every
reading the text permits; a verdict files only where the arithmetic
closes. Inbox read first: empty (no unread letters; `read/` empty).
No code, no builds, no plan edits — this file only.

**B-51 as filed** (MASTER L5164–5175): "the Register's 796dp headline
stands (two sources file it), but the filed §2.2 stack does not
arithmetically produce 796 on any single reading, and the printed
wheel centre (206, 404) contradicts the stack's own S20 charge — the
centre is struck as a constant… Tied to it: the band rail's height —
§6 builds 52dp (DESIGNERS §8.3 + §11-R17) against Raster/Kato's filed
64dp contest, and PARK's strip costs 64dp of room on Raster's
accounting. Closes when: the audit files one decomposition that sums,
and one rail height, and the Register is amended to match."

One filing defect in B-51's own record, noted before the verdicts:
it runs **two different 64s together**. (a) The rail/nav block 64dp
(Raster's construction S8 · 48 cap zone · S8 — designers-parts/
raster.md L235; Kato's grid, beyond-deck-parts/kato.md L68). (b) The
standing strip's **room cost** 64dp = its 56dp height + the 8dp beat
above the track (REGISTER L384: "The strip's room cost: 64dp
(Raster's accounting)" — filed under Deck geometry, beside the slide,
not under PARK; Kato sheet §2.2 verifies the booking +56 +8 = +64).
Same numeral, unrelated quantities. B-51's phrase "PARK's strip
costs 64dp" matches neither source as written. The audit treats the
two 64s separately throughout; conflating them is how this charge
stayed tangled.

---

## 1. THE STACK, RECONSTRUCTED — four readings, four sums

### Reading A — MASTER §2.2's printed list, taken literally

MASTER §2.2 (L199–203): "One room, margin 20, content 372, wheel
centre (206, 404). Stack: header 48 · S20 (room-debt charge: the
filed S32 readout→wheel beat paid −12 for the standing strip) ·
readout 88 · S8 · NEXT 20 · wheel Ø288 · reserve · task 20 · S20 ·
slide 72 · S20 · nav. Fixed sum 796; reserve = H − 796."

Sum the printed fixed elements (reserve is the flex and is excluded
from any fixed sum by §2.2's own definition, L203–206):

48 + 20 + 88 + 8 + 20 + 288 + 20 + 20 + 72 + 20 = **604**, plus "nav",
which §2.2 prints **with no number at all**.

- nav = 64 (Kato's block / DESIGNERS §8.4): 604 + 64 = **668**
- nav = 52 (§6.4's rail, MASTER L2955–2956): 604 + 52 = **656**
- nav as printed (unnumbered): the sum is **undefined**, and reaching
  796 would require nav = 796 − 604 = **192dp** — a figure no source
  in the plan files for any nav, rail, or block.

**Reading A does not produce 796 on any nav value the plan knows.**
Shortfall against the debt stack (Reading B): 796 − 668 = **128dp**,
in two exact halves of 64 — the top stack omits S32 (beat→header) +
header slot 20 + S12 + the charged NEXT→wheel beat (32 + 20 + 12 +
20 = 84) and prints one S20 (20) in their place: 84 − 20 = **64
missing**; the bottom stack omits the strip entirely (56 + its 8dp
beat = **64 missing**). §2.2's list is not a compressed version of
the room; it is a different, shorter room wearing the room's sum.

### Reading B — the room-debt stack (the only stack that ever summed to 796)

Kato's original grid (slice-de-parts/kato.md §2.1, from
beyond-deck-parts/kato.md §1.1): top stack beat 48 · S32 · header
slot 20 · S12 · readout 88 · S8 · NEXT 20 · S32 · wheel 288 = **548**;
bottom stack task 20 · S20 · slide 72 · S20 · nav block 64 = **196**;
548 + 196 = **744**. BEYOND-deck W4 (L577–579) and MASTER §2.2's own
parenthetical then book the strip at +64 and charge the
readout→wheel beat S32→S20 at −12:

744 + 64 − 12 = **796** ∎

This arithmetic is internally clean (Kato sheet §1 concedes it
verbatim). But note what it is a sum *of*: a room **without** the
refusal-sentence slot, with the nav at **64**, and with the −12
charged at NEXT→wheel — three properties §2.2's printed list does
not have (it omits the strip the +64 pays for, places its lone S20
after the header rather than before the wheel, and never numbers
its nav). 796 is the sum of Reading B, printed over Reading A.

### Reading C — Reading B plus the element MASTER itself mandates

MASTER §2.6 (L443–445) mandates, in the merged plan's own operative
text: refusal sentences "in Rosa's reserved **22dp slot 8dp below
the track, reserved always** so a refusal moves nothing by a single
dp." A reserved-always slot is fixed room. It appears in **no** sum
above: not in §2.2's list (which ends "slide 72 · S20 · nav"), not
in REGISTER's room line (L162: "fixed sum 796dp; reserve = H − 796").

Kato sheet §2.3 states the accounting rule and the delta: the slot
pays its stated geometry (+8 offset +22 slot = **+30**) and the
filed S20 below the slide survives below it as the slot→nav beat.

796 + 30 = **826** — Kato's corrected sheet (slice-de-parts/kato.md
§2.4) sums it row by row: top fixed (rows 1–9) 48+32+20+12+88+8+20+
20+288 = **536**; bottom fixed (rows 11–19) 20+20+56+8+72+8+22+20+
64 = **290**; 536 + 290 = **826**. Reserve = H − 826; lawful minimum
H = 826 + 20 (floor) = **846**.

### Reading D — Reading C with Slice D/E's accepted compression

SLICE-D-E-BUILD-READY D12 (L350–356) adopts Kato's correction and
applies Rosa's already-accepted strip compression: band 56→53
(−3), lower beat 8→6 (−2), total −5. §3-R4 (L563–567) rules it:

826 − 5 = **821** · reserve = H − 821 · floor 20 · **lawful minimum
H = 841** · H=844 reserve = 23 (lawful). The compressed strip's room
cost falls consistently: 64 → 53 + 6 = 59, and 64 − 59 = 5 ∎

**Which reading the document's own text supports:** Reading D is
the latest merged correction, filed as a coordinator reconciliation
"binding on the build" (SLICE §3 header), and §3-R9 (L591) already
lists the Register correction it implies (796 → 821) as owed.
Reading B is supported only as history. Reading A is supported by
nothing — it is the one §2.2 prints.

Corroboration that 796 was never self-consistent even on its own
terms: its fence arithmetic doesn't derive. Floor 20 over a 796 sum
implies escalation under H = 816; MASTER §2.2 (L203–204), BEYOND W4
and REGISTER (L162–163) all print "under 820" — **4dp that no source
derives**. And §2.2's worked examples (48dp reserve at H=844, 100dp
at H=896) are 796's children: under 821 they are 23 and 75.

---

## 2. VERDICTS PER SUB-CHARGE

### B-51a · The 796 headline — **CLOSED. 796 is dead; the corrected fixed sum is 821.**

Proof chain, every link cited above: 744 (Kato original, honest for
a room with neither strip nor slot) + 64 (strip cost, verified
booking) − 12 (filed charge) + 30 (sentence slot MASTER §2.6 itself
mandates, never booked) = 826 (Kato sheet, row-summed) − 5 (Rosa
compression, accepted in Slice) = **821**. Reserve = H − 821;
reserve floor 20 stands; lawful minimum room **H = 841**.
MASTER §2.2 (L202–204) and REGISTER (L162) still print 796 /
H − 796 / under-820: those lines are superseded text awaiting the
Register amendment SLICE §3-R9 already owes — the discrepancy is
acknowledged in the plan, not hidden by it, but until the amendment
lands a builder reading only MASTER builds the wrong room. Two
named contingencies can still raise 821; neither reopens 796 —
they are filed as S3 (imprint) and S1 (leaf) below, because both
are elements whose booking is still owed, exactly as the slot was.

### B-51b · The wheel centre (206, 404) — **CLOSED. Struck correctly; the centre is derived, never a constant.**

Centre y = wheel top + 144 (R144, MASTER §2.4 L253). The three
candidate tops give three centres:

- §2.2 literal list: 48+20+88+8+20 = 184 → centre **(206, 328)**
- Kato original (S32 uncharged): top = 260 → centre **(206, 404)**
- Charged stack (S32→S20 at NEXT→wheel): top = 248 → **(206, 392)**

The printed (206, 404) is the *uncharged* original's centre
(BEYOND W1, L281–282: wheel y260–548, centre (206, 404)) printed in
the same §2.2 sentence that books the −12 charge which moves it.
Raster's audit (seat-drafts/register-raster.md L64) states the same
proof in one line: centre y = 228 + gap + 144 = 404 only at gap 32;
at S20 it falls to 392. REGISTER's amendment (L403–410, "the centre
is struck as a constant — derived, not fixed") is therefore
**ratified by this audit**. The centre's *value* is not closed —
it is exactly Fork F20 (SLICE §6): re-base to (206, 392) under the
charge as filed, or relocate the −12 to the slot→nav beat (S20→S8)
and keep (206, 404) at the same sum (Kato sheet §8, Fork 2 — sum
826 under every combination; likewise 821). That is a ruling for
Ren, already filed as a fork; B-51's evidentiary part — the printed
constant contradicts the stack — is closed. (x = 206 is acquitted:
margin 20 + content 372/2 = 206, consistent everywhere.)

### B-51c · Rail height 52 vs 64 — **SHARPENED** (see S2)

What evidence closes: the two figures are **different
constructions, not two measurements of one rail** — 52 = 4dp inset
+ 44dp cap + 4dp inset (MASTER §6.4, L2955–2965: rail 52, "usable
track 342 × 44dp"; DESIGNERS §8.3: rail 52, cap 44) against 64 =
S8 + 48dp cap zone + S8 (raster.md L235; DESIGNERS §8.4 L641 "the
rail's 64dp block"; Kato sheet row 19). They cannot both be the
room's nav line, and the choice is not free-standing: **the 821
sum presupposes 64**, because Kato's sheet — the sheet Slice D12/
R4 adopt — books nav at 64. If §6's 52 governs the room, the sum
is 821 − 12 = **809** and the lawful minimum is **829**, and no
filed text says so. Conversely Raster's pair-swap (register-raster
L64) shows why 796 survived both readings for so long: (S20 +
rail 64) and (S32 + rail 52) both total the same — the rail fight
and the datum fight cancel, which is precisely why neither was
ever forced. The exact settling question is filed as S2.

### B-51d · The Counterfoil leaf (Linea 56dp vs §2.2's 20dp task slot) — **SHARPENED** (see S1)

What evidence closes: the contradiction is real and internal to
MASTER itself. §9-A C-3 (L4844–4850) rules Linea's HELD blank leaf
at **full 56dp** "supersedes §2.5's 'the task line is absent when
empty,'" and states the 56dp against §2.2's 20dp slot "is **not
corrected by inventing a number** — it stands as filed against the
arithmetic." Yet §2.5 as merged (L342–345) still prints the dead
law — "Task line (20dp)… No task cued → line absent, reserve
grows" — and no sum in §1 above books a leaf delta: not 796, not
826, not 821. C-3 also cites the leaf's home as "§4.10 as merged";
§4.10 (L1548) is Tasks type discipline and contains no leaf — the
pointer does not land. What evidence cannot close: the leaf's
booked delta, because Linea's own filing (invention/improvers-parts/
05-linea-task-line-reinvention.md §2.1, L123–127) specs more than
56 — a perforation (1dp tall, 12dp below the panel's bottom edge),
an 8dp gap, then the leaf at min-height 56 — while simultaneously
claiming (§2.1, L105–107) the foil "occupies exactly the task
line's merged zone — Δy = 0 against W1 for slide, rail, and
reserve in every state." Both cannot be booked without naming
which existing beat absorbs the perforation and gap. S1 states
the exact reconciliation owed.

---

## 3. REMAINING CHARGES

### S1 — The leaf's delta is unbooked in every sum on record

**Passages to reconcile:** MASTER §2.5 (L342–345, task line 20dp,
absent-when-empty) + §9-A C-3 (L4844–4850, leaf HELD at full 56dp,
supersession claimed) against Linea's reinvention §2.1 (perforation
+ 8dp gap + 56dp leaf, Δy = 0 claimed) — and against all three
sums, none of which carries the leaf.
**The single ruling that settles it:** Kato/Raster file the leaf's
y-map in the corrected sheet's coordinates (top and bottom y of
perforation, gap, and leaf against sheet rows 11–19), naming which
booked beat — if any — absorbs the perforation and its gap. The
outcomes are arithmetic once that map exists: leaf-replaces-slot
alone → +36 (56 − 20), sum **857**, minimum H **877**; full block
booked (1 + 8 + 56 = 65 against the 20 slot) → +45, sum **866**,
minimum H **886**. Until the map is filed, 821 is the sum of a
Deck whose task line is still the 20dp line C-3 says is dead.

### S2 — Rail height and the datum charge are one decision, filed as two

**Passages to reconcile:** MASTER §6.4 (L2955–2965, rail 52, cap
44, builds at 52 "until the Register is amended") + REGISTER
amendment (L202–204, 52 registered, 64 contest on record) against
Kato's sheet row 19 + raster.md L235 (nav block / rail 64, cap zone
48) — the sheet SLICE §3-R4's 821 is built on.
**The single ruling that settles it:** one ruling naming **both**
the rail construction (52 = 4+44+4, or 64 = 8+48+8) **and** the
beat that pays or receives the 12dp difference (NEXT→wheel S20
charged, or S32 restored). Any ruling that names the rail alone
silently changes the sum: rail 52 + S20 kept → **809 / min 829**;
rail 52 + S32 restored → 821 stands and the centre reverts to
(206, 404) (this is Fork F20(b) wearing the rail's clothes);
rail 64 + S20 → 821 and (206, 392) as filed. Ren's F19/F20 answers
close S2 only if they are given as this pair.

### S3 — Emil's imprint allocation: the 837 contingency is still an owed verification

SLICE D12 (L364–367) files it against itself: Kato to confirm
Emil's imprint allocation (**12dp plate + 4dp lip** under Suki's
two-slot geography, SLICE D7 L236–238 / §3-R3) "sits inside the 744
base's slide-zone lines; if it does not, **the sum becomes 837**
and the fence returns with F19." 821 + 16 = 837 ∎ — and 837 +
floor 20 = lawful minimum **857**, which un-lawfuls the H=844
fence phone R4 just rescued. This is the same failure shape as
the sentence slot — an element mandated in one passage, booked in
no sum — caught this time *before* the merge instead of after.
**The single measurement that settles it:** Kato's filed
verification, one line, against his sheet's slide-zone rows
(track 72 + its two S8s): does the 16dp imprint box sit inside
lines already paid, yes or no. Until it is filed, 821 carries an
asterisk of exactly 16dp.

---

## 4. SWEEP — every other dp figure that touches the room

| Figure | Sources | Verdict |
|---|---|---|
| Wheel Ø288 | MASTER §2.2 L202, §2.4 L253 (radial stack 64+8+42+16+6+8 = 144 = R144 ∎); BEYOND W1; Kato sheet row 9; SLICE D12/R4 | **Consistent.** Never shrinks in any reading. |
| Running arc Ø280 | MASTER §5 zone table / §5.4; REGISTER L397 | **Acquitted** — a different surface (Running), not the Deck wheel; no room charge. |
| Sentence slot 22dp | MASTER §2.6 L443–445; SLICE D7 (slot height lh22) + §3-R6 (lh20 is the sentences' *setting*, lh22 the slot's *height* — both stand, never the same number) | **Figure consistent; its absence from §2.2's sum is B-51a, now closed.** |
| Strip 56dp height | MASTER §2.6 L451–452 ("56dp… 8dp above the track"); Kato sheet §2.2/row 13; SLICE D6 ("one 56dp address, compressed render 53dp") | **Consistent.** 64dp is its room *cost* (56 + 8 beat), not a rival height — see the filing defect at the head of this file. |
| Strip compression −5 | SLICE D12 L353–354; §3-R4 L563–568 (band 56→53, beat 8→6; rack step 53/3 ≈ 17.7 supersedes 18.7 consistently) | **Consistent**, and it is what makes 821, not 826, the build sum. |
| Reserve = H − 821 | SLICE D12/R4 only; MASTER §2.2 + REGISTER still print H − 796 | **Superseded text, acknowledged** (SLICE §3-R9 lists the Register correction as owed). Not a hidden contradiction; an unlanded amendment. |
| Header / chrome beat 48 | MASTER §2.2; Kato sheet row 1 + §3.1 (code reaches 48 by 8 + 32 + 8) | **Consistent.** §2.2's error is the beats around it, not the 48. |
| Readout 88 | Kato sheet row 5 (plate lh12 + S4 + numeral lh72 = 88 ∎); MASTER §2.2 | **Consistent.** |
| NEXT 20 / task 20 slots | MASTER §2.2, §2.5; Kato sheet rows 7/11 | **Consistent as slots.** The task slot's *tenant* is S1. |
| Slide track 72 | MASTER §2.2; REGISTER L383; Kato sheet row 15 | **Consistent in plan.** Code's 68dp host (Kato §3.4) is a build target, not a plan contradiction. |
| Nav in code 80dp | Kato sheet row 19 note + risk R3 | **Code leak, not a plan figure.** If kept, any sheet sum leaks +16 — a builder warning already filed by Kato; it does not reopen B-51. |
| BEYOND D5 breathing gap (S32→S48 at reserve ≥96) | BEYOND-deck D5 vs FIGHT F-D5 ("Kato wins… preserved as the losing half") | **Acquitted** — a preserved loser, named as such in its own file; no live text builds on it. |
| "Under 820" threshold | MASTER L204; BEYOND W4; REGISTER L162–163 | **Dead with 796** (B-51a) — and never derived even from it (796 + 20 = 816 ≠ 820; 4dp unexplained in every source). |

---

## 5. ACQUITTALS

- **Kato's 744** — honest arithmetic for the room he filed (no
  strip, no slot); the fault was the merge booking its elements
  into his room without paying their prices, not his sum.
- **The strip's +64 booking** — verified exact (Kato sheet §2.2):
  56 height + 8 beat, the S20 above it preserved, no double charge.
- **REGISTER's striking of the centre** — correct call, ratified
  (B-51b); the Register flagged its own headline's decomposition
  as unclosed rather than papering it.
- **Rosa's compression** — arithmetically clean (3 + 2 = 5) and
  consistently propagated (sum, strip cost, rack step).
- **SLICE §3-R9** — the Slice files its own Register corrections
  as owed instead of silently diverging; the 796/821 split between
  MASTER and SLICE is an acknowledged amendment-in-flight, not a
  concealed contradiction. It still needs to land.

**Bottom line for the register of charges:** B-51 closes in part —
796 dead and 821 summed (B-51a), centre struck and derived
(B-51b) — and stays open in part, as S1 (leaf), S2 (rail × datum,
one paired ruling), S3 (imprint, one measurement). "One
decomposition that sums" now exists: Reading D. "One rail height"
does not, and S2 names why it cannot be ruled alone.

— Spatz, Critics · spacing
