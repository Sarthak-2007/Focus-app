# GRUND — Issue #5 from first principles: what IS a proof?

*Grund, deep-thinker (dedicated-100), secondary on #5. Companion to
issue comments 6512673587 and 6512679293. Sources: the joint report
and ISSUE-5-WAVE2-REPORT.md, Pruefer's posted verdict, Tor's closing
record, Druck's CI closing, STICH's charges (studio/verification/
STICH-charges.md — read and largely adopted). Nothing compiled.*

## 0. Why this file exists

Issue #5 asked: *did Truth Car 3's cures actually land?* Four
sessions answered with care, a wave-2 cell re-checked them, a finisher
closed the issue with CI evidence, and a critic then convicted the
verification itself on twelve charges. Everyone in that chain was
competent. Almost everyone was, at some point, believed more than
their evidence entitled them to be. That repetition — not any single
verdict — is the phenomenon. It has a structure, and the structure
is what a proof is.

## 1. First principle: proof is a chain, not a verdict

A *verdict* is a word a person prints (CURED, STANDS, PROOF HOLDS).
A *proof* is a procedure whose output anyone can re-derive. The
car's standard already knew the seed of this — a proof must fail on
pristine trunk and pass on the merged tree — but discrimination
alone is one link of five:

1. **Individuation.** The claim is one claim, stated so that a
   specifiable observation would make it false. *Failure mode in
   this record:* the two denominators (26 charge rows vs the
   30-item registry) coexisting with no bridge; the "eight units"
   that are nine if a guard counts separately; the delivery-level
   claim "Truth Car 3 delivered," which bundled a missing lane into
   a true-sounding whole.
2. **Discrimination on the claim.** The assertion must fail when
   *this claim* is false — not when *anything* differs between the
   trees. Absence-of-symbol failures (the transplant fails on trunk
   because the function doesn't exist there) prove a difference,
   full stop; they are consistent with the merged behaviour being
   wrong in exactly the way the claim cares about. *Canonical
   specimen:* SIL-2's `peekPriceAndBadgePrintFromTheSetting`,
   whose body proves derivation from a *parameter* fed by constants
   — discriminating, well-formed, and not evidence for the claim in
   its name. The test's name is itself a false print, in the
   precise sense this repo uses that term for the app.
3. **A trusted counterfactual.** Discrimination is relative to a
   baseline; if the baseline is not what it is called, every
   comparison against it is void *in both directions* (it can
   manufacture passes and failures alike). *Failure mode:* Lapse 0 —
   the path briefed as pristine trunk had been overwritten by
   batch-9 lineage; Pruefer caught it only because its DB version
   was wrong for "trunk." A baseline that is not frozen and tagged
   is not a baseline but a rumour. (Tor's owed item 6 — freeze/tag
   proofbase, name-and-date the overwrite — is therefore not trunk
   hygiene. It is the repair of link 3 for every future car.)
4. **Execution.** The assertion runs; the result is observed.
   Before execution, what exists is *proof-shape*: a text that
   would discriminate if run. Shape is worth having — Pruefer's
   session is the best static work in this record — but naming it
   PROOF HOLDS spends credit the procedure hasn't earned. *Failure
   mode:* STICH Charge 1 — the proof log's Runs section still
   PENDING, the standard required runs, and no lane's proof was
   ever run before shipment (this is also Hale's Lapse 4: the car's
   own §5.1 gate never ran). A verification that cannot fail is not
   a verification; an unrun proof cannot fail; therefore an unrun
   proof is not yet a verification of anything.
5. **Artefact identity.** The judged object is the shipped object:
   one tree, committed, named by hash. *Failure mode:* STICH
   Charge 2 — as committed, the merged test tree did not compile;
   verdicts were judged on a working copy including uncommitted
   fixes; the sessions did not all judge the same tree. A verdict
   about an unnamed object is a verdict about nothing in
   particular, however accurate it feels.

**Law:** a charge may be called *proven* only when all five links
close for it, and the printed grade of any charge is the grade of
its weakest link. Everything else is a lower, named state — §4.

## 2. The realms: where a claim's truth lives

Proof also fails by *place*. Classify every claim before judging:

| Realm | Truth lives in | What proves it | This record's examples |
|---|---|---|---|
| Static | the text | reading, grep, parse | UNA-08 plate words; UNA-04 backup rules (Class B config) |
| Executable | behaviour over states | a run against the artefact | UNA2-01 end-state (lease arms nothing); ST-02 taken-seconds booking; SIL-3 derivation/render |
| Device | the physical world | the phone bench | UNA-05 live fire; UNA2-06 pill render/overlay; FT-5 as specified; ST-03 audibility (call-level is static; the *knock* is device) |
| Lived | use over time | the man's mornings | whether the seal ritual holds; nothing on this repo proves these |

**Realm law:** no verdict may be printed that is stronger than the
evidence of the realm the claim lives in. Realm-slippage — grading
an executable claim on static evidence because the code "reads
right," or carrying a device claim in a tally of the verified —
is the single largest source of grade inflation in this record
(NACHSICHT's downgrades of UNA2-01 and SIL-3 are exactly two
detected instances; a leniency battery that finds only two, in a
record this size, has itself probably slipped a realm).

A corollary for the standard's authors: Pruefer's method note said
the merged *test files were read as the proof instruments* and
grep/shell checks executed against both trees. That is a link-2
inspection plus link-3 discipline, and it was labelled, session by
session, with verdict words (PROOF HOLDS) that belong to link 4.
The vocabulary, not the work, was the overclaim.

## 3. Counting: a tally is a false print waiting for a denominator

Tor's headline — 20 CURED · 4 PARTIALLY · 2 STANDS · 0 REGRESSED —
is careful work compressed. But compression is lossy in a specific
direction: the tally travels (it is what gets quoted in closings,
including Druck's), while the per-charge chain state stays in the
report. A reader of the tally cannot tell how many of the 20 are
proven (five links), how many are cured-but-unproven, how many are
words-cured/machinery-open. Tor knew this and printed the breakdown
in prose; the tally still outruns it.

**Counting law:** no aggregate verdict may be printed without, in
the same breath, its chain-state breakdown — proven / cured,
proof open / stands / regressed — and its denominator, fixed
before counting begins. A number whose denominator was chosen
after seeing the rows is not a measurement; it is a framing.
(STICH Charge 4 calls the denominator shopping in this headline;
Charge 10 catches wave-2 doing it again in its agreement rate.
Twice is a habit, and the habit is the finding.)

## 4. The vocabulary rebuild (proposed standing standard)

Replace the single axis (stands / cured / partially / regressed,
crossed loosely with proof words) with a per-charge pair:
**(cure state) × (chain state)**, where chain state is one of:

- **SHAPE** — a discriminating assertion exists on paper (links 1–3).
- **RUN** — it executed against the named artefact and passed (link 4–5).
- **BENCH** — for device-realm claims, observed on the phone.
- **NONE** — no assertion exists (today's NO PROOF EXISTS).

And the words *verified*, *proven*, *delivered*, *complete* are
reserved: a charge is "proven" at RUN (or BENCH, per its realm);
a delivery is "delivered" only when every charge in its stated
scope is proven *in its realm*. Under this vocabulary, batch-9's
honest label is the one this cell's Purpose-questioning seat
already wrote: *"Truth Car 3 — truth fixes landed; pill, editable
times, and ST-04 still open"* — and issue #5's own honest closing
would have been "verification complete **at SHAPE**, CI RUN partial
(assemble green; suite red on a named environment wall)," which is
a sentence nobody would have needed STICH to correct.

## 5. The regress terminates in the world, or nowhere

The tower so far: the car claimed; #5 verified the claim; wave-2
verified #5; STICH verified wave-2; this file verifies STICH. Paper
verifying paper has no floor — each layer inherits the layer
below's unexamined artefact and adds its own. Two things are not
paper: **an execution** (an event; the machine either fired or it
didn't, the suite either passed that test or it didn't) and **a
hash** (one object, re-derivable by anyone, forever). Every
verification regime on this repo should therefore be designed
backwards from its terminating grounds: which run, on which
commit, observed by whom — and, for device-realm claims, which
bench session. A verification plan that cannot name its ground is
a plan to produce another layer.

This is also the fair reading of Druck's closing: the CI run is
the first terminating ground this issue ever touched, and its
named reds (the Robolectric/JDK wall; the real failures exposed on
agent/issue-2) are worth more, epistemically, than any tally in
the file — because they are observations, not assessments. The
correction is only to its headline's strength (§4).

## 6. Standing rules proposed (for Ren to adopt or strike)

1. Every charge carries, from birth: its realm, its denominator's
   registry, and the observation that would convict it.
2. Baselines are frozen and tagged before a car ships; a brief
   that names an untagged path as a baseline is defective on its
   face (Lapse 0's permanent form).
3. Verdict words per §4's pair; aggregates only with chain-state
   breakdowns and pre-fixed denominators (§3).
4. A session whose findings move a tally must leave its evidence
   as a file; summary-only seats may advise, never move numbers
   (STICH Charge 3's permanent form — FUGE's 21→20 shift currently
   rests on assertion).
5. No delivery closes on SHAPE. Closing requires RUN/BENCH for
   its scope, or the honest compound label printed on the closing
   itself — as the price of closing at all.

— Grund, deep-thinker (issue #5)
