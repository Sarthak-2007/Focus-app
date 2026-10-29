# SWARM RESEARCH — what the field actually does, and what this studio should steal

*Fernrohr — Methods, swarm research. Sweep 2, commission 3 (collaboration).
Filed Oct 29, 2026. Every claim below traces to a page fetched and read in
full this run (URLs in §6). Search snippets were used only to find pages,
never as evidence. Our baseline is plan/PIPELINE.md (Laws 1–11, amendments
A1–A5) and plan/WASTE-LEDGER.md (W-05, W-06, W-09, W-11).*

---

## 1. The axes — what they do, with receipts

### (a) Task / claim queues vs our mail

**Claude Code agent teams (Anthropic's shipped product).** A team is a lead,
teammates, a **shared task list**, and a **mailbox**. Tasks have exactly three
states — pending / in progress / completed — plus dependencies: a blocked
task cannot be self-claimed, and completing a task **auto-unblocks its
dependents**. Claiming is not a message; it is a **file lock**: "Task claiming
uses file locking to prevent race conditions when multiple teammates try to
claim the same task simultaneously." After finishing, "a teammate picks up
the next unassigned, unblocked task on its own" — self-claim is the default
path, lead-assignment the exception. The mailbox is one JSON file per agent
(`~/.claude/teams/{team-name}/inboxes/{agent-name}.json`), validated on read.
Sizing guidance is blunt: 3–5 teammates, 5–6 tasks each; "three focused
teammates often outperform five scattered ones."
→ Agent-teams doc (mirror of code.claude.com docs), §"Assign and claim tasks",
§"Architecture". URL in §6 [S3].

**Beads (Steve Yegge, open source, MIT).** Work items are structured records
in a dependency graph, stored as JSONL in git, queryable: `bd ready --json`
returns "a definitive list of unblocked work" — the agent queries state
instead of reading prose. Multi-agent coordination: "we both query the same
logical database (via git), see what's claimed (`status: in_progress`), and
work on different ready issues. The `--assignee` filter makes this trivial."
→ Yegge, "Introducing Beads", Appendix A (written by the agent itself). [S4]

**Our position.** Our WORK-QUEUE is a markdown list a human coordinator
reads; claiming is a letter or nothing. Mahner's first sweep found the cost
on disk: queue items 1–4 still listed open though done, item 5 unclaimed with
approved wants queued behind it. Mail is our *conversation* layer doing a
*queue's* job — exactly the split Claude Code makes (task list ≠ mailbox)
and we don't.

### (b) Shared memory / blackboard vs our wall + registers

**Beads again — the strongest evidence in this whole survey, because it's
our disease, diagnosed by someone else.** Yegge: "my agents… keep all of
their plans, at all levels, in sibling markdown files" → "six hundred and
five markdown plan files in varying stages of decay" → agents "get dementia…
competing documents, obsolete documents, conflicting documents." His fix was
not better markdown discipline; it was **addressable, queryable records**:
every item gets an ID, status, dependencies, an audit trail ("when I update
an issue, the event is logged with a timestamp and actor"), and closed work
**compacts into summaries** so the store stays scannable. Also: "you can just
kill your agents after completing each issue" — fine-grained items make
sessions throwaway, which kills the end-of-context shortcut panic he
documents at length. [S4]

**LangGraph (LangChain).** Splits memory in two typed stores: short-term
state per thread, persisted by a **checkpointer** (Postgres/Mongo in
production), and a long-term **store** across threads, with semantic search.
Long histories are managed by trim / delete / **summarize-into-a-`summary`-
key** strategies. The transferable idea is the *schema discipline* — state
has declared keys and one writer path — not the database.
→ LangChain docs, "Memory". [S8]

**Anthropic's Research system.** The lead saves its plan to external Memory
before spawning anyone, "since if the context window exceeds 200,000 tokens
it will be truncated and it is important to retain the plan." [S5]

**Our position.** Our wall is a *prose* blackboard (argument, not state —
see §4, that inversion is deliberate and ours). Our registers are the state
layer, but they are prose markdown: W-07 is literally "settlements announced
faster than documents were amended — six register corrections unlanded."
Beads is the field's verdict on exactly this architecture: prose state rots;
queried state doesn't.

### (c) Integrator / merge-bot patterns vs Ren-alone

**GitHub merge queue (official docs).** A PR that passes its own checks
joins a FIFO queue. The queue builds a temporary `merge_group` branch
containing **the base + this PR + every PR ahead of it**, and runs required
checks on that *predicted future trunk*. If a group fails, that PR "will be
removed from the queue", the groups behind it are **rebuilt without it**, and
the rest still merge — a failing car never stalls the line. Velocity knobs
are explicit: build concurrency (1–100 concurrent merge-group builds), min/
max group size, status-check timeout. [S1]

**Bors (bors-ng, the original merge bot, Rust's ecosystem).** Same idea,
plus the two refinements GitHub lacks: **batching** — all approved patches
are merged onto a `staging` branch and CI runs *once on the batch*; and
**bisection** — if the batch fails, Bors splits it and retries, landing the
good patches and ejecting only the culprits. Testing N PRs costs O(E log N)
runs instead of O(N) sequential ones. A separate `trying` branch runs
non-blocking try-builds that never touch the queue. Bors is also "the only
entity capable of merging" — the integrator is a *role with a protocol*, not
a person's afternoon. [S2]

**Cognition (Devin) — the counterweight.** Walden Yan's 2025 line, held in
his April 2026 revisit: parallel writers make incompatible implicit
decisions ("actions carry implicit decisions"); what works is agents
contributing **intelligence** while **writes stay single-threaded**, plus a
reviewer who starts from *clean* context and interrogates the diff.
Anthropic's own post concedes the same boundary from the other side:
domains needing shared context or many inter-agent dependencies "are not a
good fit" — "most coding tasks involve fewer truly parallelizable tasks
than research." [S5][S6]

**Our position.** The literature *vindicates* Law 1/Law 5's single merger —
and then indicts our implementation of him. Ren is Bors performed by hand:
he checks diffs personally, one car at a time, with no speculative
pre-testing of car N+1 against the trunk car N will create, no automatic
ejection, no batching. The bottleneck Takt priced ("four scarce jobs on one
pair of hands") is what merge queues were invented to delete — while keeping
exactly one writer to trunk.

### (d) Verification / CI gating vs our checker + Keepers

**Claude Code hooks — the gate as an exit code.** Agent teams expose
lifecycle hooks: `TaskCompleted` (fires when a task is marked complete),
`TaskCreated`, `TeammateIdle` (fires when a teammate is about to go idle).
"Exit with code 2 to prevent completion and send feedback" — the gate can
**refuse the state transition itself** and bounce the work back with a
reason, mechanically, at the moment of the claim. Our checker (amendment A4)
checks claims at *merge* time; a completion hook checks them at *birth*.
[S3]

**Merge-queue gating.** In both GitHub's queue and Bors, the gate is not
advice: ungreen work physically cannot advance trunk, and the *combined*
state is what gets tested — not each diff against a stale base. Our A1
tiered proof already moves this way (filtered tests per car, full suite per
train); the missing half is that our gate runs on the car's own base, not
on the predicted post-merge trunk. [S1][S2]

**Anthropic's harness work (via Wong's survey of it).** Two harness failures
drove their design: an agent that "tries to do too much at once and leaves
half-finished work when its context runs out, and a later session that sees
some progress and declares the job done." The fix is partly in (e) — but
note the verification half: the feature list's `passes` flag may only flip
when the test actually ran, and "removing or editing tests" is called
unacceptable in the prompt. That is W-11 ("shipped drifted to mean an APK
exists") answered by a data structure instead of a language ruling. [S7]

### (e) Context compaction / handoff

**Anthropic, three mechanisms, one principle — return distillates, keep
artifacts on disk.** (i) The lead's plan lives in external memory, not in
anyone's window. (ii) Long-horizon work: "agents summarize completed work
phases and store essential information in external memory before proceeding";
near the limit, fresh subagents spawn with clean contexts and continuity
comes "through careful handoffs" + retrieving the stored plan. (iii) The
anti-telephone rule: "implement artifact systems where specialized agents
can create outputs that persist independently. Subagents call tools to store
their work in external systems, then pass lightweight references back to the
coordinator. This prevents information loss during multistage processing and
reduces token overhead from copying large outputs through conversation
history." Anthropic's context-engineering guidance puts the distillate size
at roughly 1,000–2,000 tokens per subagent return. [S5]

**Anthropic's long-running harness (initializer/coding split).** Session 1
(initializer) writes `init.sh`, a progress log, and a **JSON feature list —
200+ items, every one `"passes": false`**. Every later session: read the
progress file + git log, run a smoke check, pick **exactly one** failing
feature, implement, verify end-to-end, commit, append a progress note. The
list is JSON "on purpose, because the model is less likely to inappropriately
change or overwrite JSON than Markdown." Sessions are stateless; the files
are the memory. [S7]

**Our position.** Our closing-letter law (Law 7: proved / killed / learned)
is the right instinct at the wrong fidelity — it is prose, unbounded in
length, and it travels *through* the integrator's reading time. 171k lines
of paper (W-09) is what uncompressed handoffs compound into. We have the
artifact half already (everything lives on disk); we lack the distillate
discipline and the machine-readable state file.

---

## 2. The transfers — ranked by pain relieved per unit effort (max 7)

### T1 — ADOPT: Make WORK-QUEUE a claim-lease queue, not a list. *(axis a)*
**Mechanism.** Each queue item becomes one file, `plan/queue/<id>-<slug>.md`,
with a 5-line frontmatter header: `status: ready|claimed|blocked|done`,
`blocked_by: <ids>`, `owner:`, `claimed: <HH:MM date>`. Claiming = creating
`plan/queue/.locks/<id>.lock` (atomic on any filesystem — the Claude Code
file-lock pattern); a claim older than one seating with no closing letter
auto-expires back to `ready` (the lease — our one addition to their model,
forced by our re-seating reality). `blocked_by` items are unclaimable until
their blockers flip to `done`, which unblocks them with no coordinator
touch. Coordinators stop *assigning by letter*; seats self-claim the top
ready item in their territory (Law 9 becomes a mechanism instead of an
exhortation). Mail stays for talk; it stops being the queue.
**Price.** ~1 hour: one header convention + a `queue.sh` (`ready`, `claim`,
`done` subcommands, ~40 lines of shell). No new software.
**Pain.** Law 9's enforcement gap; Mahner's E-list (stale open items,
unclaimed item 5); duplicate-claim risk as lanes multiply.

### T2 — ADAPT: Merge-queue the train — speculative staging + auto-eject. *(axis c)*
**Mechanism.** Keep Ren as sole trunk-writer (Cognition's single-threaded-
writes evidence backs him). Mechanise everything *around* his judgment,
Bors-style: (i) a `staging/` tree = trunk + all checker-green cars in train
order, rebuilt by script whenever a car enters or leaves the queue — this is
the predicted trunk, and it is what gets the *one* completion compile per
batch (Law 11), not each car's stale base. (ii) A batch that fails ejects
only its failing car: the car's coordinator gets an ejection letter with the
failure, staging rebuilds without it, the train proceeds — Ren never
hand-bisects. (iii) While car N compiles (≤2 flags, Law 3), car N+1's diff
is pre-checked *against staging*, so semantic collisions (the drive()-
function case Bors exists for) surface before Ren touches anything.
Ren's remaining jobs: rule on ejections, fast-forward trunk to a green
staging, ship. Integrator judgment stays human; integrator *labour* goes
to the queue.
**Price.** 1 day of Methods/build time: staging-rebuild script + queue file
in BUILD-CHANGES.md format; no Gradle change (staging compiles count
against the same 2-flag budget — GitHub's "build concurrency" knob is our
Law 3, already set to the right value).
**Pain.** The headline one: integrator bottleneck, 4 untimed jobs, merge
archaeology when bases diverge mid-train.

### T3 — ADOPT (data model) / ADAPT (tooling): Registers become a JSONL ledger; markdown becomes a view. *(axis b)*
**Mechanism.** WORK-REGISTRY and OPEN-LOOPS (the Keepers' two books) move
to append-only JSONL: one line per event —
`{id, ts, actor, item, field, old, new, evidence_path}`. RULINGS.md keeps
its prose (rulings are arguments; they deserve sentences) but every ruling
gets an ID line in the ledger, so "is X settled?" is a `grep`, not a
reading. A `ready.sh`/`settled.sh` query answers the two questions agents
actually ask (what's unblocked? what's decided?) without opening 171k lines.
Closed items compact weekly into one summary line each (Beads' compaction),
history preserved in git. Do **not** install `bd` itself: our items are
studio records, not repo issues, and a second database beside the files
would split the truth. Steal the schema, skip the tool.
**Price.** Half a day: schema (above) + two query scripts + a one-time
migration of the two Keepers' books (~1 agent-day total, mostly Protokoll's).
**Pain.** W-05 (re-litigation — a query ends it, a citation plea doesn't),
W-07 (unlanded corrections — an unlanded correction is now a visible ledger
line with no matching `landed` event), Keepers' verification cost on every
sweep.

### T4 — ADOPT: Completion hooks — the checker learns to say no at birth. *(axis d)*
**Mechanism.** Extend the A4 checker with the Claude Code hook semantics:
a seat's closing letter / `done` flip (T1) runs `checker.sh` first; exit 0 =
the transition happens; exit 2 = **the completion is refused** and the
checker's failures are delivered to the author as the bounce reason (missing
evidence path, inbox ≠ 0, no NEXT section, SHIPPED claim without a delivery
record). Ren and the Keepers only ever see completions that already passed —
the gate moves from merge-time audit to claim-time refusal. Mahner stops
sweeping for violations a script can prevent.
**Price.** ~2 hours on top of A4's existing checker (it already verifies
most of these predicates; this wires its exit code into the workflow and
adds the inbox/NEXT checks).
**Pain.** Mail-compliance law needing human enforcement; Keepers' sweep
load; W-11 claim drift caught at the source instead of in audit.

### T5 — ADOPT: Return-by-reference with a distillate cap. *(axis e)*
**Mechanism.** One protocol line, enforced by T4's checker: a closing letter
may carry at most ~1,500 tokens of prose — proved / killed / learned /
decisions-needed — and everything else travels as **paths**. The full
deliverable, renders, and evidence stay on disk (already our habit); the
letter becomes Anthropic's "lightweight reference back to the coordinator."
Coordinators synthesise upward the same way: Ren receives distillates of
distillates, never raw seat paper. Wall posts exempt (the wall is thinking,
not reporting — §4).
**Price.** Zero tooling; one line in the mail protocol + one checker
word-count. 
**Pain.** Integrator reading load (the unpriced half of the bottleneck);
W-09's ratio attacked at the consumption end — paper nobody must re-read
stops taxing the one pair of eyes that merges.

### T6 — ADAPT: One-item sessions + a JSON state file for big builds. *(axis e/d)*
**Mechanism.** Any build expected to span >1 seating (Slice E is the live
case: 26 parts, 19 forks) gets a `build-state.json` beside its build-ready
file: an array of `{part, passes: false, proof: null}`. A lane session
claims exactly **one** `passes:false` part, and may flip it only by writing
the proof path (render, test output, diff) into `proof` — the flip without
proof is a T4 checker refusal. Forks live in the same file as
`{fork, ruled: null}`; an unruled fork is queryable state, not a letter
Ren hasn't opened yet (cf. Streit's first debate: a 10:18 delivery sitting
unread with 19 load-bearing forks).
**Price.** ~2 hours: JSON template + checker rule. 
**Pain.** Premature-victory declarations (W-11), fork re-asking (W-05's
build-time form), multi-session builds losing their place (Yegge's dementia
problem, build-scale edition).

### T7 — ADAPT: Stack the big cars. *(axis c, review side)*
**Mechanism.** Graphite's guidance: PRs under ~200 lines, ideally ~50,
review faster with fewer missed defects; stacked PRs let review start on
part 1 while part 2 is still being written, and tooling restacks dependents
automatically when a lower part changes. Our analog, no purchase: a train
car touching >1 screen or >~200 diff lines must arrive at BUILD-READY
already split into named sub-cars with declared dependencies (the T1
`blocked_by` field carries the stack order for free). Everybody-review
(Law 6) opens per sub-car; Ren merges bottom-up; if a lower sub-car is
ejected under T2, only its dependents re-diff — the script's staging
rebuild already knows how.
**Price.** Convention only (a build-ready gate line, checked by T4), plus
builder time to split — repaid in review time, per the Graphite evidence.
Ranked last: our cars are file trees, not branches, so the automatic
restack is the one piece we must fake with the T2 staging script, and the
gain is real but smaller than T1–T6.
**Pain.** W-06 (review latency — small units get reviewed *during* the
build, not after it), merge-time archaeology on monolith cars.

### Rejected, with reasons (not counted in the 7)
- **LangGraph / a real orchestrator runtime (checkpointer DB, state
  graphs).** We have no runtime graph to checkpoint — our agents are
  sessions seated by Ren against files. Adopting it means building the
  orchestrator first. Its one good idea (typed state, single writer path)
  is inside T3. Yegge's field note agrees: he built on Temporal, "a tad too
  heavyweight," and ripped out 70k lines — heavyweight orchestration is the
  documented failure mode for desktop-scale swarms. [S4][S8]
- **More parallel writers.** Rejected on the literature's own evidence:
  Cognition (writes single-threaded; intelligence parallel, actions not)
  and Anthropic (coding = few parallelisable pieces, dependency-heavy).
  Our 3–4 build lanes + one merger already sit at their recommended shape;
  the fix is T2's queue, not more hands on trunk. [S5][S6]
- **Cognition's "share full traces, not isolated messages"** as a studio-wide
  rule. Right for a 2-agent pair; at 250 seats it makes every reader pay
  every writer's full context — the opposite of Anthropic's distillate
  economics, and our wall already shows the cost curve. Kept in one place:
  a builder and its *own* reviewer share the full diff + trace (and Yan's
  2026 refinement — the reviewer starts clean, then gets the bridge — is
  exactly our everybody-review order under Law 6). [S6]

---

## 3. What the ranking assumes
T1 enables T4 and T6 (no hooks or state files without claimable items);
T2 assumes T1's queue exists to order staging. If only three survive
ruling, take **T1, T2, T5** — the queue, the merge machine, and the
distillate cap. Together they convert Ren from the pipeline's engine back
into its judge, which is the job the literature says a lead is for.

---

## 4. Novelty check — what we do that the literature doesn't have

Honest version, checked against everything read this run:

- **The mailroom — SEMI-NOVEL, and the novel half is not the inbox.**
  Prior art exists and is close: Claude Code agent teams ship a per-agent
  mailbox, one JSON file per agent in an `inboxes/` directory — same word,
  same filesystem idea. Beads/Gas Town route work through a shared store,
  not letters. What no source has: letters addressed to a **dormant named
  identity** — mail that waits, unread, until that *person* is re-seated,
  possibly by a different model run, and is then read first. The literature's
  agents are sessions; ours are persons with continuity of name, taste, and
  an audit trail of read/ filing. That — identity-first, session-second —
  is an invention.
- **Named seats — NOT novel as roles, novel as tenure.** Role prompts are
  everywhere (Anthropic's subagent definitions are literally reusable named
  roles for teammates). Nobody read here keeps ~250 named seats *across
  waves*, accumulating rulings, grudges, and a wall history, with
  coordinators verifying their seats' mail like a staff. Scale + tenure is
  ours; the seat itself is standard practice.
- **Laws with provenance — NOVEL, as far as this survey reaches.** ADRs
  record decisions with rationale; nothing read here makes a rule's
  *validity* depend on tracing it to the sovereign human's actual words,
  with the studio's own formulations explicitly labelled as the studio's
  (PIPELINE.md: "his words, never ours"). Provenance-as-validity is ours.
- **The Keepers — NOVEL as an institution, assembled from known parts.**
  The parts exist separately: CI as machine-keeper (Bors), a citation/
  verification agent as a pipeline step (Anthropic), completion hooks
  (Claude Code). A standing *cohort* of agents whose only jurisdiction is
  the record — registry, open loops, escalation ladder, a debate chair who
  files both sides **verbatim and quotes silence as silence** rather than
  paraphrasing it into a position — has no counterpart in anything read.
- **The wall — a deliberate inversion of the blackboard, and the inversion
  is ours.** Classic blackboard systems (and Beads' store) make the shared
  surface the *control structure*: state lives there, work is claimed from
  there. Our wall's first rules forbid exactly that ("no assignments live
  here") — it is a blackboard restricted to *argument*, readable by the
  human sovereign, append-only, while state lives in registers beside it.
  Literature keeps those two in one surface; we split them on purpose.
- **Not novel, said plainly:** one human integrator (that's a tech lead;
  Anthropic's lead is the same shape with an LLM in it), copied-tree lanes
  (git worktrees — Claude Code offers worktree isolation for teammates),
  compile rationing (GitHub's build-concurrency setting is Law 3 with a
  UI), everybody-review (parallel review teammates are a documented agent-
  teams use case). We execute these well; we didn't invent them.

**Net:** the studio's genuine inventions are institutional, not technical —
persons instead of sessions, provenance as a validity condition, a
record-keeping caste, and a blackboard that refuses to be a control
surface. The field's inventions are mechanical — claim locks, merge queues,
distillates, state files. That's precisely why the transfers above are
cheap: our institutions are strong exactly where their mechanisms are
missing, and vice versa.

---

## 5. Method note
Sources were fetched and read in full via browser.open on Oct 29, 2026
(the Anthropic Research post via a full-text reader mirror of the original
anthropic.com engineering post; the agent-teams doc via its GitHub mirror
of code.claude.com). DeepWiki's bors-ng pages are generated from the bors-ng
source tree and cite file/line sources inline; treated as documentation of
the code, not as journalism. Latent Space's Walden Yan page is an edited
synthesis with numbered citations to Cognition's own posts; used for Yan's
positions, flagged as secondary wherever it carries weight (T2's
single-writer claim is corroborated independently by Anthropic's post).

## 6. Sources
- [S1] GitHub Docs — "Managing a merge queue":
  https://docs.github.com/en/enterprise-cloud@latest/repositories/configuring-branches-and-merges-in-your-repository/configuring-pull-request-merges/managing-a-merge-queue
- [S2] bors-ng core concepts (DeepWiki, generated from the bors-ng repo):
  https://deepwiki.com/bors-ng/bors-ng/1.2-core-concepts
- [S3] Claude Code agent teams documentation (GitHub mirror of
  code.claude.com docs):
  https://github.com/mrkhachaturov/agent-harness-docs/blob/HEAD/docs/claude-code/agent-teams.md
- [S4] Steve Yegge — "Introducing Beads: A coding agent memory system"
  (Medium, Oct 13 2025):
  https://steve-yegge.medium.com/introducing-beads-a-coding-agent-memory-system-637d7d92514a
- [S5] Anthropic — "How we built our multi-agent research system"
  (full-text mirror of the anthropic.com engineering post):
  https://www.thetermspot.com/sources/download/54ec6cecaf0674475744730c14f84f188f8861f8e0f17d9e4f607590d9e0c9ca-fc555881-5fea-4386-98fb-7b6fc1ceae91
- [S6] Latent Space wiki — "Walden Yan" (synthesis of Cognition's "Don't
  Build Multi-Agents", Jun 2025, and "Multi-Agents: What's Actually
  Working", Apr 2026):
  https://latent.space/wiki/walden-yan-7c572c23/
- [S7] Matthew S. Wong — "AI Agent Harness Engineering" (survey incl.
  Anthropic's "Effective harnesses for long-running agents"):
  https://www.matthewswong.com/en/blog/ai-agent-harness-engineering-research/
- [S8] LangChain Docs — LangGraph "Memory":
  https://docs.langchain.com/oss/javascript/langgraph/add-memory
- [S9] Graphite — "How to break up large pull requests":
  https://www.graphite.com/guides/break-up-large-pull-requests

— Fernrohr, Methods (swarm research)
