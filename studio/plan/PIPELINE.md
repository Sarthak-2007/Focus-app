# THE PIPELINE — how work travels, and how it compounds

*Filed by Ren, Oct 29 2026, on Sarthak's order: "Make the pipeline
better. So no effort is wasted and it compounds." This document is
the studio's operating law for HOW work moves. It binds every
cohort, every lane, and Ren. Amendments are appended with date and
provenance, never silently edited in.*

## The sovereign aim (his words, never ours)

The app is ART — the most beautiful, best app. Beauty first.
Every stage below serves that aim. Working tests the studio uses
(the panel's three standards, the spectacle budget, the type ramp)
are the studio's own tools, labelled as ours — never his words.

## The eight stages

1. **WORD.** A verdict from Sarthak, or a commission from Ren.
   Nothing enters the pipeline without one of the two.
2. **WAVE.** The studio invents and argues. Panels rule design
   questions; Ren ratifies. Only questions about Sarthak's own
   life reach him — everything else is settled here.
3. **BUILD-READY.** One file per build: exact numbers, exact code
   sites, a price, named risks, ZERO open forks. **No fork
   survives this gate.** A question discovered during a build
   goes back to stage 2, not into the build's lap.
4. **BUILD.** 3–4 lanes in parallel, each in its own copied tree
   with a private Gradle home, branched from a FROZEN trunk
   snapshot (see Law 1). Lanes never touch each other's trees.
5. **PROOF.** The lane's own evidence: green assemble, green
   tests, renders. A lane ships proof, not claims.
6. **EVERYBODY REVIEW.** Every screen's spec and renders go to
   the whole studio (Sarthak's order, Oct 29). Review starts from
   the build-ready file and mid-build renders — it does not wait
   for stage 5 to finish. Findings are letters to the builder;
   the builder answers or amends.
7. **MERGE.** Ren alone merges, in the published merge-train
   order (Law 5), checking every diff personally. Trunk advances
   one train car at a time; after each merge the full suite runs.
8. **SHIP.** Batched builds to Sarthak's phone — one file per
   batch, never a dribble of partials. His phone verdict feeds
   stage 1. Unproven-on-device stays labelled unproven.

## The anti-waste laws (the compounding)

**Law 1 — One trunk, frozen bases.** `Focus-app/` is the trunk;
only Ren's merges move it. A lane branches from a named trunk
stamp recorded in BUILD-CHANGES.md, and never rebases mid-build.
A lane's return is a diff against its named base. Three different
bases in one morning cost an hour of hand-merging — that class of
waste is now structurally impossible.

**Law 2 — Settled is looked up, never re-argued.** Every ruling
lives in exactly one home: RULINGS.md (every verdict, his and the
chair's, one line each with provenance), REGISTER.md (numbers),
LIE-REGISTER (defects), BUILD-CHANGES.md (build state). A brief
that needs a settled fact CITES the line. A seat that re-opens a
settled question without new evidence is pointed at the line.
Knowledge compounds because it is stored as state, not re-derived
as prose every wave.

**Law 3 — Compilers are the scarce resource; queue for them.**
At most TWO gradle builds run at once across all lanes. Before a
gradle run, a lane creates `builds/compile-<lane>.flag`; it
deletes the flag when the run ends. If two flags exist, the lane
writes code, tests on paper, or reviews — it does not start a
third compile and slow all three. (Evidence: three concurrent
builds turned a 2-minute compile into 25. Four lanes writing and
two compiling beats five lanes all compiling.)

**Law 4 — Every lane owns its surfaces.** Renders write to the
lane's own shots dir (the harness reads `shots-redirect.txt` in
the lane tree). Logs, scratch and diffs live in the lane's
directory. A lane's output is auditable without another lane's
noise — nobody ever pixel-audits a foreign render again.

**Law 5 — The merge train is published before it runs.** Order
for the current train (Oct 29): token (merged) → counterfoil
(merged) → routines → drawer + digest → slide → chrome/PARK →
Slice E. A lane prepares its diff against the trunk it will
actually meet. New cars join the back of the train, never the
middle.

**Law 6 — Review overlaps build.** Stage 6 opens the moment a
build-ready file exists. By merge time, a screen has already
survived the studio once; post-merge review is for what the
merge changed, not the whole screen again.

**Law 7 — Every wave leaves the books richer.** A lane's closing
letter must name: what it proved, what it killed, what it learned
that the next wave should not re-derive. Ren files those into
RULINGS / REGISTER / LIE-REGISTER / BUILD-CHANGES before the
next wave seats. That filing — not the wave itself — is the
compounding.

**Law 8 — Ren's hands are a stage, not a queue.** Ren merges and
ships; while a merge runs, at least two lanes are building and
one wave is thinking. If everything is waiting on Ren, the
pipeline has failed, not the people.

## What this replaced (Oct 29's evidence)

- Bases diverged (token tree vs main tree vs pre-Live-Wire tree);
  merges were bespoke archaeology. → Law 1, Law 5.
- Five compilers on one machine; builds at 5–10× solo time;
  daemons killing each other through a shared Gradle home.
  → Private homes (already) + Law 3.
- One shared screenshots folder; lanes overwrote and misread
  each other's pictures. → Law 4 + the harness redirect.
- Settled rulings scattered across letters; forks re-asked that
  had already been ruled (24-fork letters re-ratifying decided
  law). → Law 2 + the stage-3 gate.
- Reviews waited for finished builds; findings arrived after
  the merge they should have shaped. → Law 6.

**Law 9 — Nobody sleeps while work exists.** *(Added Oct 29,
09:47, on Sarthak's order.)* A coordinator whose commission
completes pulls the next matching item from plan/WORK-QUEUE.md
and seats it immediately. Sleeping is a scheduling failure, not
a rest state: while one lane builds, every other cohort holds a
live commission — a review, a build-ready file, a hunt, a render
pair. Builders keep a standing bench: during a long build, the
lane's coordinator seats a verification wave on the lane's
previous output, so seats work while compilers and mergers do.

## Amendments — Methods Sweep 1, ruled by Ren (Oct 29, ~09:45)

On Takt's Sweep 1 (39 proposals, plan/PROCESS-PROPOSALS-1.md),
Ren rules the top five IN, effective now:

- **A1 — Tiered train proof** (amends Law 5's merge practice):
  each train car proves itself with the filtered tests for the
  screens its diff touches; the FULL suite runs once per train,
  not per car. A diff touching shared code (theme, tokens,
  ViewModel, data) always forces the full suite. Precondition:
  one isolated suite run is timed first (Waage owns the number).
- **A2 — One question queue** (new): questions for Sarthak batch
  into plan/INTERRUPTS.md and reach him once per break, only
  through the standing gate (his life, his values, facts only he
  knows). No seat, lane, or coordinator pings him ad hoc.
- **A3 — One constants source** (extends Law 2): charged numbers
  live in ONE machine-readable file; REGISTER.md and the app's
  constants are generated/checked from it, and a validator fails
  literals that don't resolve and sums that don't recompute.
  Staged: charged constants migrate first.
- **A4 — The checker** (new merge gate): a script proves register
  corrections landed (old value grep-gone), build-ready files
  carry price + code sites + zero open forks, findings carry
  file+line evidence, and SHIPPED claims carry a delivery
  record — or the claim downgrades to BUILT. What fails bounces
  to its author; Ren sees only what passed.
- **A5 — Review law** (settles Law 6's open edge): only S1
  findings (breaks his recorded word, ships a falsehood, breaks
  a build) can HOLD a merge. Everything else is advisory with a
  lazy-consensus close deadline; silence closes, it never blocks.

**The integrator is priced from today.** Takt's headline finding
stands adopted: merging, Law-7 filing, the question gate, and
claim verification are four scarce jobs on one pair of hands,
and the pipeline now treats them as a measured resource
(Waage instruments merge time and queue depth in the waste
ledger), not as free air. Bauzeit's territory (Gradle speed)
stands uncovered after a policy refusal in that seat's run —
recorded openly in WASTE-LEDGER; a future sweep re-seats it.

**Law 10 — No silent shutdowns.** *(Added Oct 29, 09:49, on
Sarthak's order: "when an agent is about to shutdown let it
propose what it wants to do next and what it could do.")* Every
coordinator and builder, before it finishes, files in its
closing letter a NEXT section with two parts: (a) WHAT IT WANTS
TO DO NEXT — its own chosen follow-on work, stated as a pull
request on the work queue; (b) WHAT IT COULD DO — capacities and
unassigned problems it noticed while working. Ren rules on the
wants the same day (approved wants enter plan/WORK-QUEUE.md
under the proposer's name); the coulds file into the queue as
candidates. An agent that shuts down without proposing has
broken the law, and its coordinator answers for it. The queue is
fed by the studio itself, not by Ren alone.

**Inventors' remit widened.** *(Oct 29, 09:49, his order:
"Inventors also apply to design and user facing stuff too.")*
The Wild cohort invents on design and user-facing surfaces as
of right — screens, interactions, look, feel — not mechanisms
alone. Their surface inventions travel the same road as
everyone's: studio argument, panel ruling, Ren's ratification,
everybody review. No cohort owns a surface's kind of idea.

**Mail compliance.** *(Added Oct 29, 10:12, on Sarthak's direct
order: "Tell every agent to read all its mails that are unread
by it.")* An agent holding unread mail is out of compliance.
Coordinators read their full inbox at seating, at every pause,
and before finishing — and verify their seats have done the
same before a wave closes. Read letters move to read/, never
deleted. A closing letter opens with the compliance line:
"inbox: N unread at start, 0 at close" — an agent that shuts
down over unread mail declares the count and the senders, and
its coordinator answers for the gap. Unread mail discovered to
have changed settled work is escalated to Ren the same hour.

**Law 11 — Compile at completion, never per change.** *(Added
Oct 29, 10:13, on Sarthak's order: "compiling is not necessary
after every change. Compile when everything finished.")* A lane
writes its whole piece, reviews it on paper, and compiles ONCE
when the piece is complete — that compile is its proof stage.
Ren merges a whole train car, then compiles once. Fix-and-
recompile after a failed completion compile is completion
iteration, not per-change compiling. A mid-build compile is
allowed only as a named probe (checking one specific
uncertainty), kept rare and declared in the lane's letter. The
queue law (Law 3) governs those completion compiles as before.
The waste this kills: this morning's trunk compiled three times
for one batch while lanes recompiled per change on a starved
machine — daemons dying of memory is the receipt.
