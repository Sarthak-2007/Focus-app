# PLAN AUDIT — the merged master plan, the chair's rulings, and the pipeline

**Filed by:** Hale (Critics coordinator), reconciling all 20 Critics seats
**Date:** Oct 29, 2026 · **Scope:** the widened critics' scope (Sarthak's
standing order: anything is a lawful target — plan, register, rulings,
process). **Standard:** every charge carries file + line evidence or the
user's recorded words; colour claims carry channel arithmetic.
**Parts:** plan/plan-audit-parts/<seat>.md (20 files, the full evidence).

Twenty seats filed over 160 findings. Merged and de-duplicated: **19 S1
charges, ~45 S2, ~30 S3**, plus §9 dispositions (B-51 closed in part and
sharpened in part; B-54 CLOSED; B-55 SHARPENED), a process audit, and
~90 acquittals. One headline over all of it:

> **The settlements were announced faster than the documents were
> amended. Again and again the audit finds the same shape: a ruling
> made, a correction claimed — and the plan, the register, or the
> build-ready file still printing the opposite. The plan's §9 even
> fails the plan's own acceptance checks on the merged file.**

---

## S1 — breaks a standard, a verdict, or ships a lie

**PA-S1-01 · The test-day apparatus he killed is still live law — and
§9 falsely claims §0 killed it.** *Quaere, Mutter, Prima, Vera, Una
(five seats).* Sarthak, 07:34: "How will the app even know its my test
and why does it need to know" (BUILD-CHANGES.md L157). Yet §0 L92–95
prints the superseded 07:23 verdict "TEST, SESSIONS BENEATH" as final
law; §2.5 L355–364 specs the HPA-06 test-eve preset and HPA-08 Saturday
face; §3.4 L744–748 specs HPA-07 (`SAT — TEST DAY · PAUSED`). §9 C-2
(L4823) claims "§0 kills the apparatus" — it does not — and B-4 sits
HELD even though the full-scope kill was ratified at 08:58
(BUILD-CHANGES L205–206). §9's V-6 grep sweep polices only §§4–8,
outside where the apparatus lives. Built as filed: a test morning gets
a Deck with no slide (Prima) — ARM abolished, not slowed.

**PA-S1-02 · §2.6, headed "operative", specs the machinery the plan
itself killed.** *Quaere, Una, Vera, Reyer, Dens.* §2.6 L462–479 specs
the Canary whole (sacrificial target, 300/800ms, the 1500ms deadline),
the press-poll, Park, and the `PROOF: END-TO-END` stamp — all dead per
§9 C-1 (L4806–4821), A-6/A-7, §8.7, §7.8; the seal's ARM worst case
(3.3s) is still DERIVED from the dead 1500ms deadline (Dens ARM-3,
§2.6 L533–534) and omits the 1.2s drag (sequential sum 4.5s). §2.7
L598 even carries the canary in the fold-in flight plan. Consequence,
in the plan's own terms: §9-E-1's grep check and Vera's acceptance
check 1 FAIL on the merged file. The plan fails its own acceptance
test. Canary breaches are also filed into the rebuilt Ledger tab
(§2.6), contradicting the chair's ruling that machine-fault events
have no home.

**PA-S1-03 · The room: 796 is printed in both governing documents;
the stack sums to 604; the adopted 821 is in neither.** *Dens, Spatz
(B-51a CLOSED), Orig, Prima.* Register L162 and §2.2 L202–204 fix
796dp. §2.2's own printed list sums to 604dp (+nav: 656 at the 52 rail,
668 at 64) — 140dp carried by nothing; 796 would need a 192dp nav no
source files (Spatz). The corrected sum — 821, reserve = H − 821,
lawful minimum H = 841 — was adopted by the chair (F19+F20) from
SLICE-D-E-BUILD-READY and appears in NEITHER document. The printed
fence ("return under H=820") passes devices the adopted room declares
impossible. The struck wheel centre (206, 404) is still printed in
§2.2. An open +16 contingency (to 837) rides on top.

**PA-S1-04 · The register is not current — settlements announced,
documents unchanged (systemic).** *Orig, Anselm, Cato-II, Dens, Reyer,
Vera.* Instances, each verified in the printed text: (i) TRAVEL —
Register L44 still prints 180dp; the chair's fork letter claims
"register corrected"; the only correction letter actually sent was
the specular one, and (ii) SPECULAR — Register L88–94 and L268–274
still register `specularSwept` + 720-entry LUTs as shipped truth;
MASTER §2.4 L279–285 presents the same unshipped rig as the build,
including dual sub-draws the plan's own C-11 (L4908–4912) killed.
(iii) BAND — Register's main body L150–156 still prints 85.5/77.5 as
"operative"; the correct 73.5/65.5 lives only in an amendment
(L193–198). (iv) CREAM — the ratified marker colour #FFE4CF still
reads "provisional, flagged for ratification" at Register L318–320
and appears nowhere in MASTER-PLAN. (v) B-12 — still "pending
ratification" in §9 though ratified (sana/0858 item 5). (vi) §9
asserts strikes (C-10/C-11/B-51) never applied to the §2.4/§2.2 text
they strike. The register's one job is one value per constant; in at
least six places it currently prints the losing value as the law.

**PA-S1-05 · The plan describes an app that no longer exists.**
*Blok, Moss.* The token baseline is stale by a full shipped build:
§0.1-9 L152–153 files token persistence as "build debt", §5.3.8
L2047–2049 says the VM holds a bare in-memory `MutableStateFlow(1)`
that process death refills, §9 B-15 holds Migration 7→8 OPEN — while
the tree is already DB version 8 with `MIGRATION_7_8` registered
(MonolithDatabase.kt:24, 41–52), the destructive fallback gone, and
the token a ledger-derived StateFlow (VM:150–151 ← Repo:1202–1209 ←
Dao:86–93). §5.9 L2800–2804 even says so, contradicting §5.3.8 inside
the same document. Same species: the wheel's locked physics (ζ = 0.38,
the (θ, ω) integrator — shipped in B2) appears in NEITHER document
(grep: zero hits; §2.4 gives one wheel two incompatible laws eleven
lines apart, L266 vs L277, and Register L253–259 "ratifies the fork"
as two seat regimes); §5.2's "penalty-blind remainingSeconds" is
penalty-aware in the tree (MonolithModels.kt:281–285); "engine coerces
blocks 1..4" (§2.4 L299–301) is `coerceIn(1, 8)` (Repo:986) — priced
from a stale KDoc. **And the poisoned inheritance: LR2-S1-2's seeded
150 minutes of fabricated history (Repo:271–355) is ordered deleted
NOWHERE — no section, no §9 charge. The Drawer, whose whole law is a
truthful record, will inherit fabricated history unless a strike is
filed.** (Blok's acquittal stands beside it: the plan does not
oversell its NEW work; its lies are about the old app.)

**PA-S1-06 · The Laid Seal still builds in the file the builders
build from.** *Reyer.* Ren's B-5 ruling (Bit-Identical Seal governs;
Laid Seal dead) was delivered twice to Marlowe (0858, 0905) and once
to Sana (0858). SLICE-D-E-BUILD-READY.md still specs the Laid Seal as
building (§0:53–54 the Throw-or-Laid predicate; E2:414–443 "BUILDS
≈185 net"; F14:728–732 wet send). Grep verified at reconciliation:
"Laid Seal" 3 hits, "Bit-Identical" 0 hits in that file. §9 B-5 still
lists the question OPEN. MASTER-PLAN itself is clean (acquitted).

**PA-S1-07 · §8.10, the spec plate, is charged from four lenses.**
*Lind, Tand, Nox, Quaere.* One passage, four independent failures:
(a) TYPE — the plate is set Anonymous Pro **400**, 10sp, **+1.2**
tracking, lh16 (L4533–4536, "Teen's setting unchanged") against the
register (L170–172), the ramp ("one style, no exceptions", L58–61)
and §7 itself (L3775): 700 / +2.0 / lh12. Three parameters wrong on
the app's one fixed specimen, printed as deliberate verbatim law.
(b) IDENTITY — the plate text is Teenage-Engineering hardware-plate
copy verbatim (`MONOLITH ML-01 · UNIT Nº 01 · NOT FOR SALE · POWER:
YOUR PHONE`), and the plan says "identity lives on the plate".
(c) LOGIC — ratification 12 rules the LEDGER clause "cut in product,
kept in paper"; §8.11-16 (L4724) requires renders byte-identical to
§8.10's verbatim plate, so cutting the line fails the test and
keeping it violates the cut. (d) TRUTH — the line `SESSION RECORD:
THE LEDGER` is literally false under §7.8/§7.2 (sessions are not
Drawer content; tasks are), against the plate's own literal-truth
law (L4557–4559).

**PA-S1-08 · The donor's palette is locked in as skin.** *Tand,
Cato-II.* Blockit's exact values verbatim — accent `#F55314`
(L1195), ink `#252323` (L324), cream `#FBF1EC` frozen as a literal
against the token system (L778) — matching the donor kit's four-value
system (blockit-design-kit SPEC/KIT). Standing law: Blockit supplies
invisible craft only, never skin. A hex on the slide cap is visible
skin. The same ink `#252323` is also the register's error: Register
prints day text `#2C2A29` (44/42/41), which appears in ZERO plan
sections (Cato-II C-3).

**PA-S1-09 · A green-leaning hex stands in print in the governing
register.** *Cato-II, with arithmetic.* `#758F88` as printed in both
files is R117 **G143** B136 (G−R = +26, hue 164°) — the only
G-dominant literal in the plan, under a standing never-green law.
The verified token is ARGB `0x758F8888` = A117 R143 G136 B136 (warm
grey; the leading 0x75 is alpha). The register files that exact byte
trap ("0x75 ≈ 46%") two entries above its own transcription, then
walks into it. The charge targets the printed digits only; Ren's
no-swap ruling is not re-opened.

**PA-S1-10 · The spectacle budget is overspent: a fifth freeze,
spent on the most frequent gesture.** *Gust, Moss, Dens (three
seats, same lines).* §4.4 L1162–1163 spends a 40ms hit-stop on the
ORDINARY sub-step — none of the four ledger freezes (§2.6 L540–544).
§4.8's own freeze verification L1450–1455 convicts that exact
hit-stop to 0ms. Both sections cannot be built. Moss's full ledger:
5 freeze events against the budgeted 4, plus 2 unbudgeted arrests
(HELD FRAME 1200ms, §5.7 — 30× the largest freeze, never charged to
the budget; refusal hold 300ms); ledgered freeze #1 (slide commit
40ms) is budgeted but has no mechanism home in §2.6's sequence.

**PA-S1-11 · The art bar has no enforcement: seven of eight sections
can pass every check and still be cold.** *Gust.* §0 L31–45's words
survive ("top criterion is art", graded first among designs passing
the three standards). The machinery demoted it: only §7.12 check 8
can fail a screen for ugliness — and even the Drawer passes
UNBEAUTIFUL via the Residue Face (§7.11 L3731–3735, L3910–3922:
permanence "law independent of beauty", a face that "makes no beauty
claim" ships lawfully). Sarthak's retention law is that he keeps
only what feels beautiful; the merged plan cannot fail a merely
functional screen anywhere except one check in one section.

**PA-S1-12 · Chair ruling 6 misattributes Sarthak's word.**
*Day8.* The ruling prints the SEALED line dying to BLANK as
"Sarthak's 07:22 word, exactly." His word, as carried in §0, §5.6
and §6.1, is NO SCREEN CHANGE — silence during, geometry invariant
under seal. Blanking `SEALED · UNTIL 07:00` is a screen change in
its least interpretable form. The dossier's own caveat ("in a way he
may or may not notice") was erased in the letter. (Acquitted in
part: the as-filed keep-printing-SEALED text deserved to die, and
the BROKEN receipt booking faithfully carries his verdict.)

**PA-S1-13 · The AOD revert broke a working truth.** *Nox.* On
ratification 9's authority, shipped break-progress code was
reverted. In the tree, the repository freezes elapsed at the block
target during a Safe Break (MonolithRepository.kt L1052–1079), so
the desk clock now prints **00:00 for the entire 10-minute break** —
a break reading as FINISH. The FLOW bar remains fabricated as
elapsed/1200f (AirlockAndDisarmOverlays.kt L120–121) against
§5.9-14, and the hero burns cream #FFE4CF at luminance 0.813
against §5.8.5's 0.25 ceiling. §5's text matches the tree on no
major dimension. The derivation (Nacht) is sound in-plan; the
revert of WORKING code for a face §5 admits is "not yet built"
(L2800–2812) was disproportionate and selective.

**PA-S1-14 · The HELD digest has no door.** *Una.* His FULL PEEK
verdict (in §0, specced in §5.5) opens the digest "on a HELD tap" —
but §5.4's five-elements law for the running screen and §5.9-1's
acceptance test leave no surface for that tap to exist on. A
verdict he gave is specced behind a door the same document
abolished.

**PA-S1-15 · §6.1 amends a standard while claiming to read it.**
*Una, Prima.* §0 standard 2 says "navigation is read-only" (L20).
§6.1's "travel dies" (L2844) replaces it — sealed §§3.1/4.1
read-only screens become unreachable, and the already-open rescue
§6.1 grants the Drawer is granted to no other screen — while
presenting itself as a reading of the standard. §7.1 meanwhile
lets a stored row's "detail be opened and read in full" while
sealed (Prima), the flat opposite of §6.1. One of §6.1/§7.1 must
be struck; the standard neither of them may amend is §0's.

**PA-S1-16 · Shield prints an all-time total — the score-shape the
Drawer law and the receipt forbid.** *Quaere.* §3.7's `47
DEFLECTED` all-time total rebuilds the tally §3.3 killed and
§5.7's receipt forbids as score-shaped. Same family: attendance
grammar (`ATTENDED 2H 50M OF 3H`, §3.4) survives on Shield after
the Drawer buried it (S2-1 in quaere.md).

**PA-S1-17 · The refusal can arrive in neither channel.** *Zug.*
Fork F12 keeps the refusal knock at 10ms @ 0.40 in spec — below
Zug's filed perceptibility floor (≥12ms @ 0.70) — with the
constant deferred to an ungated device session. The chair's note
claims the spec is "as you filed it"; Joss's actual filing
(joss.md L451–458) was the opposite: a build gate — do not let
the slide reach his hand before the floor question is settled.
A sub-perceptual knock plus a sentence in the one slot Suki flags
as clipping at font scale >1.0 is a refusal that may be neither
felt nor read.

**PA-S1-18 · The chair's 09:00 answer answered a different
complaint.** *Rank.* He said "very slow" (pace). The answer was
"no more design waves" (planning volume) — the wrong variable,
corrected by him within minutes ("Dont stop planning dude / Make
the pipeline better not smaller / Use your brain", MEMORY.md
L32). A pace complaint treated as a scope complaint cost him a
second intervention in his own study morning.

**PA-S1-19 · "SHIPPED" in the build log means "an APK exists on
the VM".** *Mutter.* BUILD-CHANGES labels six builds SHIPPED; on
the delivery record only two files reached Sarthak all morning
(monolith-truth.apk 07:56, monolith-beyond-b2.apk 08:52), both
Deck-only, both logged as phone-unverified. The 06:17 entry
self-downgrades to "delivered in clone chat"; B2 admits feel
"unproven until his phone"; the token and counterfoil builds are
private lane trees, not installable files. The word does the
announcing the product didn't.

---

## S2 — contradictions that will cost a build, a ruling, or the user

**Numbers & ARM.** §9 contradicts itself: A-13 (L5014) declares the
ARM collision CLOSED while B-17 (L5085–5087) holds it HELD; the two
worst cases (6.1s in §0.1-2 L135–136, 8.0s binding in the Register)
both stand in print (Dens ARM-1; Quaere S2-3; Vera S1-2 — §0.1 also
cites §5.2, which contains no ARM accounting). The binding 8.0s is
derived NOWHERE; the body's maximum is 7.2s — 0.8s unexplained (Dens
ARM-2). Deck typical lower bound: 1.2 + 0.6 = 1.8 ≠ printed 2.0
(ARM-4, S3-grade arithmetic in an S2 neighbourhood). The Register
carries ZERO prices despite claiming every load-bearing constant;
the real slice totals (D ≈1,550 / E ≈1,700 net) live outside both
documents (Dens PRICE-1).

**Slide, seal, motion registry.** Register still registers the
≥120ms dwell of the Park machinery it files dead (Moss M-04,
Register L358–360 vs L43–46). §2.4 opens with Uhr's rival wheel
scale (36 detents, 10°) against the register's frame, corrected
~4,650 lines later at C-10 (Dens WHEEL-1). Curve disorder: CurveA
undefined yet governing §3.7; two unregistered springs in §4
(ζ0.62/300, ζ0.55/380); THROW-SPRING registered governing nothing
while the sled runs on MechanicalOvershoot — the animator Kin's
audit deleted by name, kept by the Register for §3 and leaked into
§4.7; loadout commit gated at animator end (Moss M-06–M-10).
§2.6's seal is gated on the dead machinery while §9 C-1 supersedes
with the Live Wire — two lawful preconditions for one seal (Reyer).
F6's LIVE BIND truth condition is unenforceable as written: no
per-link stamp exists to "drop", no checker is named, and all three
links are mixed system/self evidence — it re-solves a problem
Rosa's definition already solved (Orig F6). F1's precedent sentence
("the register's literal loses to the view's real geometry") is
unbounded: the register's other literals (0.84 vs shipped 0.82,
64dp vs shipped 80dp) exist precisely to overrule the view (Orig
F1). In-session sounds are licensed with no voice, owner, or firing
proof (Airlock Catch, peek expiry — Reyer S3).

**Chair rulings, detail.** Day8: ruling 1 kills Reihe's per-routine
NEXT — computed against the draft, a prospective fact the strip
cannot carry — flattened in the dossier and unpriced in the letter;
ruling 5 adopts Horiz's settlement unconditionally, stripping his
own §7.4 ship-gate and §7.2.3 warning ("will read to most users as
a broken button… I have priced no trial"). Meta: 6/6 adopted
letter-for-letter, zero conditions, three written doubts
unmentioned, and the letter's "two prices" paragraph lists three
figures covering only items 1, 2, 6. Prima on the ratifications:
item 2's chrome says OFF where PARK says MISSING for the same fact,
against §8.2's no-crossing law, and §6.2 mis-points to §8.4 twice;
item 4's "slot below the band" does not exist in §2.2's room stack;
item 6 specces no blank first frame for chrome despite §8.2's
blank-until-proven law, and the longest STALE form (39 monospace
chars at 15sp) does not fit the band beside MONOLITH. Zug on the
17 ratifications (see the coordinator's correction below): 10
charged — F4 strict abort vs the one-handed grip, F7
annulment-by-deletion leaving only a fading sentence as trace, F10
label fade at 0% before the commit point, F13 seal voice left
spent in ~13 places, F16 cap words at 10sp with a 250ms UNDO
window nobody can read, F17 ghost retention with no
reduced-motion clause, F24 direct-to-Settings ejection on a
slot-wide hair trigger. His meta-pattern names the mechanism
behind Day8's and Orig's charges: **the accessible clause exists
somewhere in the files for nearly every charge — en-bloc adoption
is exactly where it gets dropped.**

**Record & Drawer.** The seam verdict is closed at A-14 and built
nowhere in 5,229 lines (Quaere S2-2). The answer-keys hold has no
lawful operative form — the Friday-toggle precedent is noted,
never specced (Quaere S2-4). The receipt's accession counter
`№ 048` violates §9 V-4's ban across §§4–8 with no charge owning
it (Vera). The prime is specced as a complete 04:58 frame
(including placeholder `Queue #01`) carried by a prime function
and Prep Bench the unbuilt routines rebuild still owns — no §9
charge covers its provenance (Vera; the arithmetic itself, 3×50 =
170, is acquitted everywhere). §5.3.9 orders deletion of a
"shipped" body sentence with zero hits in the tree — the overlay
already prints Freya's replacement and is already Ledger-Direct
(Blok). §0.1-6's weekly line "lives inside the tab (§7)" while
§7.9 says no week line exists and C-9 records the clause dead
(Vera). Two refusal sentences stand for one fact at the slide
(§2.6 vs §8.7/§8.8 — Una). §5.7 homes the spend line while C-6/§9-E-3
hold its home vacant (Una). §5.3.9's standing Deck chip violates
§5.3.8's "exactly one standing rendering" (Una).

**Identity & taste (S2 band).** The Deck's centre of gravity is the
donor's instrument: an unshrinkable Ø288 wheel (~316 lines of
instrument in §§2.4–2.6 against ~37 lines of his day in §2.5),
adopted from a TE proportion paper without re-pricing from
Sarthak's hand (Tand). §0's "no ceremony" against the slide-and-
seal — the donor's named ritual — as "the centre gesture" (Tand).
In reverse: the vocabulary sweep bans VAULT and erases Curio (0
hits plan-wide) with no verdict of his killing either — native
names banned while donor hexes were locked (Tand). Shield's §3.8
verb-code legend and Chrome's check-only acceptance (§6.8: dp and
grep checks, no render verdict, on the most-seen surface in the
app) are functional-cold under the art bar (Gust). Decoration debt:
the Sled's 650ms powered return (session over, proves nothing,
beauty unargued) and PARK's ~25 lines of specular law for a rig
whose own text says nothing is lost without it (Gust); §5 spends
spectacle without once naming the budget (Gust G-5).

**Colour & type (S2 band).** Day muted has two hexes (`#5E5A56`
94/90/86 once vs registered `#5F5856` 95/88/86); sixteen plan hexes
(queue ladder 8, AOD stage ladder 6, dot pair 2) have no register
literal against the register's own "unregistered doesn't build"
law; the AOD budget is registered 0 while §5.8 spends ember
`#7A3410` permanently and finish `#C74814` — the accent's exact
hue — for 60s (Cato-II C-2/C-4/C-5). Night "primary ink" names two
hexes one tier apart (C-9). Tasks Footing units at 22sp rest on a
mis-cited F-UNIT precedent (§2.3 names 50sp, not a tier rule; the
ramp sets the queue total at 50) (Lind C-2).

**Accessibility.** The valve has no TalkBack/switch/hands-free
path despite §5.3.1's own "never test grip endurance" law (banks,
grace and apron genuinely bound tremor cost — partial acquittal
recorded) (Zug). The refusal slot's font-scale clipping is
unconditioned in the merged plan (Zug). The Frozen Digest specs
everything except body type and semantics, and gives its
reassurance line <1.5s of visibility (Zug). AOD Stage-2 ELAPSED
sits at luminance 0.075 ≈ 2.5:1, unlabelled from Stage 1, on the
across-the-room face (Zug). A reduced-motion clause exists exactly
once in 5,229 lines (Zug).

**The plan as a delivery to Sarthak.** It is about to be handed to
him whole, in easy language, per his order. As filed it has: no
plain cover page and no self-identification as a studio document;
two binding ARM numbers in one file; 37 open charges disclosed
only at L5220–5223; a room sum its own appendix contradicts
(Mutter). Whatever its studio value, as a delivery it fails the
standard he set for everything that reaches him.

## S3 — friction, filed compactly

Ramp header certifies Sora 400 (verified files are 500/600/700
only) — unspent, a loaded gun (Lind). §2.5's test-eve line specced
only as "≥14sp", off-ladder (Lind). Ines's TOTAL formatter kept as
"recorded fallback" in forms the one-formatter law forbids (Lind).
`BREAK SPENT` named both hero word and plate with no type setting
anywhere (Lind). Shield search CLEAR at 10sp, no weight (Lind).
The ramp retains a RECORD hero at 50sp that §7 forbids in the
Drawer (Lind). Chamfer/knurl-gate tenses stale — the gate ran and
passed; the plan files it as future (Anselm). Hub specular core
specced as carried, drawn nowhere (Anselm). PARK entry tick pitch
unassigned; wheel detent has no sole owner named (Reyer).
§0.1-10 misstates both "unclosed" charges (Vera). §2.6's 19:30
frame counts "1 pip" after §2.4 killed pips (Vera). Section map
misdescribes §8 and the sled's direction (Vera). F5's gate cannot
complete as adopted (a precondition the letter never gives) and
"deleted unbuilt" contradicts itself (Orig). F19+F20 bundling
silently kills the relocation option (Orig). AOD finish-minute
tap-anywhere vs §5.8.9's tap-is-transient (Zug). MASTER still
prints the dead `NOTHING WILL BE HELD` pair Slice §3-R2 killed
(Zug). §3.4's row both forbids and specs a fourth line (Una).
PARK prints `SLEEP 20:00` against §6.8-10 (Una). AOD becomes a
running face at a routine "firing" the windows law abolishes
(Una). The 20dp reserve floor is unreachable at the printed fence
(Dens). Vernier-caliper composer and "plate style" as app-wide
label species (Tand). Emil's imprint 12+4=16dp verification still
owed (Spatz). B-51's own record conflates two unrelated 64s
(Spatz). §5.3.8's stale B-54 note contradicts its own section
(Reyer).

## §9 dispositions — what this audit closes and sharpens

- **B-51 (room arithmetic) — CLOSED in part, SHARPENED in part**
  (Spatz, arithmetic shown): B-51a CLOSED — 796 is dead; the fixed
  sum is **821**, reserve = H − 821, lawful minimum H = 841. B-51b
  CLOSED — the printed centre (206, 404) is the *uncharged* stack's
  centre; charged centre is (206, 392); the register's strike of
  the centre as a constant is ratified. B-51c SHARPENED (S2) — rail
  52 vs 64 are different constructions (4+44+4 vs 8+48+8); 821
  presupposes the 64 block; ruling 52 alone silently makes the sum
  809 / minimum 829; rail and datum cancel 12-for-12, so F19/F20
  must be answered as ONE paired ruling. B-51d SHARPENED (S1) —
  C-3 claims supersession of §2.5, yet merged §2.5 still prints the
  absence law and no sum books the 56dp Counterfoil leaf; it
  settles when Kato/Raster file the leaf's y-map (outcomes: 857
  or 866).
- **B-54 (natural-completion sound) — CLOSED with evidence**
  (Reyer): merged §5 implements FREYA's form — natural completion
  = `playCompletionPulse()` alone (§5.3.5 L1869–1884; §5.7
  L2332–2334; §5.9-9 L2737–2741; closure A-21 L5022). Ines's
  contrary text was valve-view-scoped and never ruled the ticker;
  Ton's U4 is conditional and its antecedent is false in the
  merged text. The Freya-vs-Ines "disagreement" was a scope
  confusion. Residue: §5.3.8's stale note (paperwork, S3 above).
- **B-55 (Extinguished Clock) — SHARPENED, not closed** (Anselm):
  the contradiction is verified in the source (moench-moments.md
  L174–178 "bypasses the extinguished state instantly and at full
  lawful brightness" vs L180–182 "behaves exactly as it does
  today") and carried into §5.8.11 (L2665–2666 vs L2682–2691).
  Governing text leans bypass (§5.8.5, §5.8.7, §5.8.8) but
  §5.8.11 reserves intent to Moench/Ruth. The exact question,
  priced both ways, is in anselm.md: bypass spends 60s of light
  face-down into the desk on the common case;
  extinction-outranks silently amends §5.8.8 and gives FINISH the
  BROKEN silhouette on the AOD.

## The process audit — the day priced in his hours

*Rank and Mutter, synthesized; the full ledgers are in their parts.*

**The paper-to-product ratio.** Counted with wc/grep on the day:
plan/ holds 173 files / 71,210 lines; the whole swarm tree 1,141
files / 171,331 lines; mail/ alone 726 files / 18,307 lines.
Against that, two installable files reached Sarthak (PA-S1-19).
Named paper-only hours: the 07:15–07:24 invention wave (100 seats,
no build), the 08:10 rulings dossier, and 08:58–09:05 — the peak
paper hour, after the last delivery of the morning. §§3–9 of the
plan = 4,623 of 5,229 lines (88%) specify surfaces with zero
shipped product; the slide/seal centre gesture carries 780 lines
of build-ready paper pricing ~3,250 unbuilt code lines.

**The question load.** Rank counted 21 touches between 05:00 and
09:00 (a 22nd at 09:10), touches 7–20 landing in a 29-minute burst
(07:17–07:46) inside his fasted-study block. Tested against his
own gate (only questions about his life, values, and facts only he
knows): **7 were genuinely his; 11 never needed his word** —
including the Guard widget (a design question under his 07:41
rule), two taps to settle one death-telling design decision, the
declaration-word lock, the Shield knob form, and the TEST-day tap,
which the chair's own 07:34 log admits "should never have reached
him", taken 11 minutes after his answer was superseded and after
his 06:14 gate clarification. One touch carried a false report —
"green in code" — repeated to him as fact at 07:18 and retracted
at 07:27 (the retraction is acquitted; the report is charged).

**The bottleneck, named.** Not seats, not effort: **merge labour**
— one integrator ("code lands through one builder at a time",
BUILD-CHANGES 06:40, repeated 06:55; the token merge deferred
twice; every ruling and ratification one hand) — **sitting on a
compute ceiling** (Rank's live measurement: 2 cores, 7 GB RAM,
load 6.84 before any build; one uncontended build 15m59s; the GUH
journal-lock wars and a load-killed Robolectric suite already at
3 lanes). Question load is third for throughput but first in
AIR-1 cost. Verdict on the 200-seat expansion: doubling producers
doubles only input — merge queue, unverified claims, question
candidates — not throughput; on this record it makes the 09:00
complaint worse, and no log entry records the ceiling ever being
put to him alongside the expansion order. (The order itself is
his and is not charged.)

**The new standing rule, audited** (Hale, coordinator's addendum —
"every screen to everybody", ordered ~09:17, mid-audit): the rule
**pays as an advisory gate and costs as a blocking one**, and as
stated it does not say which. Pays: whole-studio review caught
LR2-S1-18 (the arm-anyway lie), the hardcoded prime, and — in this
audit — a live test-day apparatus inside a document about to be
handed to Sarthak whole; review is parallel and consumes no gradle.
Costs: this audit is itself proof that review output outruns
execution (B-4 HELD after ratification; six register corrections
unlanded) — an unbounded blocking gate would gate shipping on the
slowest stage. The order's own second sentence ("Ren merges what
survives") is the budget mechanism that saves it; the ambiguity is
filed as an S2, and the Critics accept standing reviewer duty on
the advisory reading.

## Acquittals — what the audit verified honest

The plan's core is not rotten; its settlements are unlanded. ~90
acquittals stand in the parts; the load-bearing ones: **every one
of Sarthak's thirteen verdict items checked line-by-line is
honoured in its operative section** — the Drawer as permanent,
scoreless storage (§7.2–7.9), HELD FULL PEEK (§5.5), the
blocking-death law — silence, clock runs, BROKEN booking — across
four sections, family calls never through, declaration words
changeable all day (§7.7), the banked 60s valve at Barriere's exact
finals (§5.3), tap-can-commit's form, the sync HOLD (B-56), the
war-line/YPT-GMC boundary (Quaere, Una). **Death-booked sessions
get no completion pulse at any passage** — §5.6, §5.7, the receipt,
the AOD (Reyer, nine checks). The token family arithmetic verifies
(drain 0.8 + 3.0@1× + 5.95@2× = 9.75 ≈ 9.7s; Dens). Slide
coordinates 0.68/0.84/0.65 verify, and the buried 0.80/0.82 have
zero occurrences in the plan (Dens). The library palette occurs 0
times in the app plan; `#FF8A5C` appears only in its death records;
every printed AOD luminance recomputes exactly (Cato-II).
Spectacle: ripple is fully purged (all five mentions are death
sentences); the 210ms seal cadence is one in all tellings; the
Sled's numbers agree across all three documents; closure settle
never gates input (Moss). §0's art-bar TEXT itself, the Deck
specular rig and knurl gate, Shield's Printed Day, Tasks' Struck
Line, the Drawer's K1–K6 ("BEAUTIFUL passes; TIDY fails"), PARK's
material law (Gust). Chair rulings 2, 3, 4 stand on retention
grounds — ruling 3 killed the prettier object correctly (Day8);
forks F2 and F21 stand (Orig); ratifications F9, F11, F15, F22,
F23 stand (Zug); Nox acquits ratification 11 in full and Nacht's
FLOW derivation in-plan. The chair owned its misses same-turn in
writing all morning — the 07:27 green retraction, the TEST gate
failure, the GUH fix (Rank). And the day's paper bought real
correctness where it became product: the 07:56 truth fixes, Slice
A's 150-for-170 span catch, Slice C's cap-8 catch (Mutter).

## Coordinator's own corrections

Two errors in this audit's own paperwork, filed before anyone
else has to: (1) My brief to Zug stated **18** ratifications; the
build-ready file lists **17**, of which the chair adopted 16 en
bloc and superseded F14 separately. Zug's part corrects the count
with proof from all three documents; the audit adopts his count.
(2) Quaere's interim summary carried a §6.1 charge her formal
filing does not sustain (her formal audit acquits the sealed-state
laws in part); the merged PA-S1-15 rests on Una's and Prima's
evidence, not on the interim note. The formal parts govern
everywhere.

— Hale, Critics coordinator, Oct 29 2026
