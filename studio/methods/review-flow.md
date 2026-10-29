# REVIEW FLOW — measured, Oct 29 2026

*Sicht, Methods (review systems that don't block shipping).
Commission 3, Sweep 2. Sources: mail/ filename timestamps,
BUILD-CHANGES.md (the merge/ship log), plan/ registers,
COMMONS.md. All times IST. A time I could not source is marked
"~" or left blank — never invented.*

*Context that frames everything below: amendment A5 was only
ruled at ~09:45 (Takt's Sweep-1 letter to Ren: mail/ren/0941;
PIPELINE.md amendments, "ruled by Ren ~09:45"). Before that,
the binding text was RULINGS.md contradiction C4 — "every
screen to everybody: its advisory-vs-blocking reading is OPEN."
So most of the day's review ran under a law that had not yet
decided whether review could hold anything at all.*

---

## 1. LATENCY TABLE — the day's review loops

| Loop | Work filed | Findings landed | Ruled / acted on | Latency, findings→action | Verdict |
|---|---|---|---|---|---|
| **Lie Register 2** (Critics, 20 seats) | Hunt pairs filing from ~08:07 (mail/hale/read/0807 cato-ii lie-hunt-2) | Register filed 08:17 per its header; letter to Ren 08:19; addenda 08:27, 08:39; CLOSED 08:45 — 66 charges (19 S1 / 28 S2 / 19 S3), 148 acquittals | Partially at 08:58 (BUILD-CHANGES: master-plan ratification entry routes worst S1s to future builds — Slice D seal-check, chrome/PARK, Drawer brief). Seed-history scrub (LR2-S1-2) NOT in trunk until 10:05 | 41 min to first routing; **108 min to the seed scrub, and only after re-charge** | Findings landed *before* the 09:12 merge — and the merge ignored one S1 anyway (see §2) |
| **Plan audit** (Critics, 20 seats) | Parts filed on the wall ~09:18–09:19 (seat entries timestamped 09:15–09:19) | Merged audit letter to Ren 09:22 (mail/ren/0922); file mtime 09:22 | AOD-span charge verified + upheld + fixed *inside* the running merge (BUILD-CHANGES 09:12–09:52 entry); dispositions letter mail/hale/0941. Remainder routed: Sana settlement sweep, Marlowe D/E amendments, Ruth seed scrub — all "in flight" at audit close | **~19 min** findings→first fix (09:22→09:41 dispositions, fix inside the same cycle) | The day's one fully healthy loop — review overlapped the merge and shaped it (Law 6 working) |
| **Lie Register 3** (Critics, merged-trunk hunt) | Seated under Law 9 / WORK-QUEUE item 2 after the merge | Letter to Ren 09:52 (mail/ren/0952); file mtime 09:52 — 55 charges (11 S1 / 21 S2 / 23 S3), 46 acquittals | Ren's dispositions 10:06 (mail/hale/1006): 5 S1 groups fixed in trunk pre-ship, rest routed (tick-count family to Routines/Drawer, U-8 to Marlowe, F-1 disclosed-not-fixed). BUILD-CHANGES 10:05 pre-ship list carries the fixes | **14 min** findings→dispositions | Fast — but note *why* it was needed: it re-charged a lie LR2 had already killed (§2) |
| **Finishers passes** (Aurel, 50 seats) | Cohort seated ~09:3x; FINISH-LIST-1 graded from existing renders | Pass 1 letter to Ren 09:35 (mail/ren/0935) | Ren adopted the list WHOLE 09:42 (mail/aurel/read/0942) as "binding finish input" for the chrome build + Ledger night-fill riding the Drawer merge. Pass 2 complete 09:58 (mail/ren/0958); fifty rulings letter 10:10. Two items (Abriss tear staging, Weiss paper fill) folded into combined build 1 at 10:05 | **7 min** findings→adoption | Fastest ruling of the day — and the adoption is the A5 problem in §2: advisory taste findings became *binding* by adoption, with no severity ever assigned |
| **Slide reviews** (Wild seats → Marlowe's inbox) | Slide lane code in builds/slide (pre-merge; slide is train car 4, after routines + drawer/digest) | Cato 09:57, Joss 09:57, Rosa 10:03, Emil 10:04 (mail/marlowe/read/) | Marlowe's wall entry 10:14: "every finding was applied or flagged in the amendment batch" | ~10–17 min findings→applied/flagged | **Healthy.** Review landed before the car's merge — the only loop besides Slice E that is unambiguously pre-merge |
| **Slice E build-ready** (Improvers, 26 parts) | Parts filed 09:57–10:2x (wall entries; plan/slice-e-parts/01–26) | Merged SLICE-E-BUILD-READY.md filed 10:18 (Ilya's letter mail/ren/1018) — 1,215 lines, union price ≈1,555 prod + ≈1,095 test | **Not yet ruled at audit close (10:35).** §12 carries **19 open forks** with recommendations | 17+ min and counting; no deadline stamp exists to count against | Pre-merge (E builds last — correct), but it breaches the stage-3 gate as designed: "ZERO open forks survive this gate." The gate has no enforcement mechanism — see Fix F-3 |

Ships the loops should have shaped, for reference
(BUILD-CHANGES): Slice C 07:40 · truth fixes 07:56 ·
Slice B2 08:52 · combined build 1 merged 09:12–09:52,
**not yet shipped at audit close** (10:05 entry is a
pre-ship fix list).

### W-06 — "findings arrived after the merge they should have shaped": VERIFIED, with a refinement

The waste-ledger's W-06 is marked "adopted" (fix = Law 6).
The disk says the *stronger* failure happened today: not late
findings, but **on-time findings a merge walked past**.

- LR2-S1-2 (fabricated seed history, ~150 invented minutes)
  was charged 08:19, the register closed 08:45 with the scrub
  ordered, and BUILD-CHANGES 08:58 records it as "in." The
  09:12–09:52 hand merge nevertheless carried the seed block
  into the trunk. LR3-S1-02 (filed 09:52) re-charges the
  identical lie, coordinator-verified in the trunk's seed
  block (MonolithRepository.kt inserts at L279/298/317):
  "LR2-S1-2 was ruled dead and ordered scrubbed; the scrub
  exists only in Ruth's unmerged drawer lane." The scrub
  finally lands at 10:05 — as Ren's pre-ship fix, 100 minutes
  after LR2 closed, and only because a second hunt re-found it.
- Acquittal, partial: for the plan audit's AOD-span charge
  and for the slide and Slice E loops, findings demonstrably
  arrived *before* the relevant merge and shaped it. Law 6
  works when the reviewer is seated against a *named upcoming
  car* (slide, E) or lands *during* a merge Ren is still
  holding (plan audit). It failed when the finding was a
  register entry routed to a *future lane* (the seed scrub
  lived in Ruth's unmerged lane while Ren merged trunk by
  hand) — nobody owned "is this S1 in the tree I'm merging
  *right now*?"

**W-06 status should read: adopted as law, FAILED once today
in its worst form. Restwert: please re-open the line with this
evidence.**

---

## 2. THE A5 REALITY CHECK

A5 (ruled ~09:45): only S1 holds a merge; everything else is
advisory with lazy-consensus deadlines; silence closes.

**Did an S1 fail to hold? YES — twice, in two different ways.**

1. *LR2-S1-2, pre-A5 but post-charge.* The seed-history S1
   did not hold the 09:12 merge (above). Mitigation: A5 did
   not exist yet; the governing text was C4, reading OPEN.
   The failure is therefore the *absence* of A5, not its
   breach — which is exactly what A5 was written to prevent,
   one merge too late.
2. *LR3-S1-01 (F-1, database road from <v7), post-A5.*
   A genuine S1 — installs older than v7 throw on open, no
   test can see it. Ren's disposition (10:06): ships in
   batch 1 *disclosed, not fixed*, ladder specced into the
   routines train. This is A5 behaving *better* than its
   text: an S1 was consciously overridden by the integrator
   with the defect named in the delivery note, not silently
   dropped. But A5 as written has no word for this — no
   "S1-waived-with-disclosure" state exists. Either the law
   gains the state, or the next waiver will be indistinguishable
   from today's seed-history walk-past. See Fix F-2.

**Did an advisory silently become a blocker? YES, in the
benign direction — and one hard case.**

- *Finish List 1*: pure finish/taste findings (no severity
  grades exist in the file at all) were adopted WHOLE at
  09:42 as "**binding** finish input" for the chrome build.
  Advisory → binding in 7 minutes, by adoption letter. No
  train car was delayed measurably (chrome hasn't merged),
  so the cost is unproven — but the *mechanism* A5 forbids
  (advisory held in front of the train) now exists in its
  most respectable costume: a coordinator's adoption.
- *B-4 (hard case, from the plan audit's process section)*:
  the TEST-day scope question sat **HELD** after the
  full-scope kill was ratified at 08:58 — an unruled scope
  question functioning as a blocker on settled law. The audit
  names it; W-08 is still **open** in the waste ledger at
  audit close.

**Did a lazy-consensus deadline pass unmarked? ALL of them —
because no deadline was ever marked.** Grep over plan/,
mail/, COMMONS.md for deadline/close-by stamps on findings:
zero finding letters carry one. A5's deadlines are defined in
the abstract (advisory closes at wave end; S2 closes when the
train reaches its car) but no artefact instantiates a single
one: LR3's 44 advisory charges (09:52) have no wave-end stamp;
Slice E's 19 forks have no close-by; the Critics' 08:45
stand-down was a de-facto wave-end close that no document
records as a deadline firing. A deadline that exists only in
the law's prose cannot pass marked *or* unmarked — it simply
never fires. This is the single cheapest failure mode in the
system to fix (Fix F-1).

**Net A5 verdict:** A5 arrived at 09:45 and the one loop that
ran fully under it (LR3, 09:52→10:06) behaved as designed —
14-minute dispositions, S1s fixed or consciously waived,
advisories routed, nothing held. The law works when it
exists *before* the loop starts. Every failure above is a
loop that started before the law, or a state (waiver,
deadline stamp) the law names but no artefact implements.

---

## 3. INTERRUPTS REALITY CHECK

INTERRUPTS.md on disk at audit close: **exactly 1 queued item
(the U1/U2 provenance re-confirmation, filed by Ren), 0
deliveries recorded, the item still sitting in "Queued."**
The batch mechanism has never fired.

**Recount of the later hours (09:00–10:35),** continuing
W-10's method (Rank: 21 touches 05:00–09:00, 11 unneeded)
from BUILD-CHANGES.md timestamps + COMMONS wall entries:

| Time | Touch | Kind |
|---|---|---|
| 09:10 | Mistaken-finish undo — asked in easy words with a priced design, answered "builds" | **Agent-posed question, direct — bypassed INTERRUPTS.md** (predates A2's ~09:45 ruling; not a violation, but not the file either) |
| 09:24 | Provenance correction ("I did not say start fast…") | His speech, unprompted |
| ~09:27 | Day-team cut to 2 (Ruth + Tief) | His order |
| ~09:41 | "Make Finishers 50" | His order |
| ~09:47 | Law 9 (no idle agents) | His order |
| 10:13 | "Read all your unread mail" | His order |
| 10:15 | Law 11 (compile at completion) | His order |
| 10:17 | Shield pick — sliding cap chosen from Linea's delivered renders | **Agent-presented decision, direct — bypasses INTERRUPTS.md, 32 min after A2 was ruled.** Mitigation: he had *requested* the evidence at 07:24 ("show me both renders first"), so this is a delivery he ordered, not a new question |
| 10:22 | Keepers of Record created | His order |
| 10:31 | "Run the methods group for everything" | His order |

**Count: ~10 touches in 95 minutes; 2 agent-posed, 8
self-initiated orders/speech; 0 through INTERRUPTS.md.**

Verdict: the *load* problem W-10 measured is genuinely
better — no 29-minute burst, no design questions reaching
him, both agent-posed items pass his standing gate (his life/
his call alone: an undo on his own finished tasks; a pick he
had explicitly deferred to evidence). But the *batching* is
fiction: A2's file is a queue nobody delivers from, and the
one item in it (U1/U2 — two verdicts governing live builds)
has sat unanswered through at least two natural breaks (the
10:17 shield-pick tap was a break-shaped moment; U1/U2 did
not ride along). The system works because Ren carries the
gate in his head, not because the file works. That is a
single-point-of-failure wearing a mechanism's clothes.

---

## 4. FIXES — priced, for Takt's ruling

**F-1 · Deadline stamp in every findings letter.**
Mechanism: one mandatory header line in any letter/register
filing findings — `CLOSES: <wave-end | train-car:NAME |
NEVER-S1>` — plus the coordinator writes the firing (`closed
by silence, HH:MM`) into the same file when it passes.
Checklist line for coordinators: "no findings letter ships
without CLOSES:". *Price: ~0 build cost; 1 template line
(Schablone's letter skeleton) + 1 line in PIPELINE Law 6.
Expected saving: makes A5's deadlines observable at all —
today's count of verifiable deadline firings is 0 of ~44
advisory charges in LR3 alone.*

**F-2 · S1-WAIVED state.**
Mechanism: A5 gains one line — an S1 Ren ships anyway is
recorded in BUILD-CHANGES as `S1-WAIVED: <id> — disclosed in
delivery note, fix vehicle: <train/lane>` at merge time.
Three lines per waiver; F-1 today would have been the first
entry. *Price: 1 line in PIPELINE amendments + Ren's habit.
Expected saving: a waived S1 and a walked-past S1 (today's
seed history) become distinguishable in one grep — currently
they are identical in the record until a second hunt separates
them, at the cost of a full Critics wave (~20 seats, LR3).*

**F-3 · Pre-merge S1 sweep (the seed-history fix).**
Mechanism: before Ren merges a car, one grep-shaped checklist
step: every open S1 in LIE-REGISTER-* whose FIX names code in
that car's diff gets a one-line disposition in BUILD-CHANGES
(`in-car: fixed / not-present / waived`). A rotating Critics
seat (or the Keeper Nachweis) files the open-S1 list per car
— the list already exists; it is simply never intersected
with the merge. *Price: ~10 min of integrator time per car
vs. the measured cost of missing one: a 20-seat re-hunt +
108 min of fabricated history riding the trunk. Invented
addition: the sweep reads the register's FIX lines
machine-fashion — FIX lines must therefore name a file or a
lane, a format rule for Hale's next register (1 line in the
register template).*

**F-4 · INTERRUPTS delivery receipt.**
Mechanism: the file gains a `## Delivered` section; whenever
Ren next speaks to Sarthak for *any* reason (a ship, a pick,
an order acknowledged), any queued item older than that
moment rides in the same message, and its delivery is struck
into Delivered with the timestamp. No separate ping is ever
created — the batch hitches on touches that already exist
(today: U1/U2 would have ridden the 10:17 shield pick).
*Price: 3 lines in INTERRUPTS.md header + Ren's habit; zero
new touches. Test: at any audit, queue age of the oldest
undelivered item must be < the age of Ren's last touch —
today it fails (item filed pre-09:45, touches at 09:10 and
10:17, still queued).*

**F-5 · Severity at birth for Finishers-style lists.**
Mechanism: Aurel's lists gain one column — every finding
marked `S1-candidate` (claims a falsehood/broken record —
Critics verify) or `ADVISORY` — *before* any adoption letter.
An adoption may make advisories binding on a *named future
build* (as Ren did, correctly, for chrome), but the letter
must say "advisory, adopted as binding for <build>" so the
record keeps the two apart. *Price: 1 column in one template.
Expected saving: prevents the respectable-costume blocker —
an ungraded taste list silently acquiring S1 force.*

**F-6 · Findings→merge latency as a standing number.**
Mechanism: BUILD-CHANGES merge entries gain one line —
`review: <register/list>, findings HH:MM, in-car S1s: n,
disposed: n`. Waage harvests findings→disposition latency
per loop from mail timestamps + this line; the table in §1
of this file becomes a per-day routine instead of a
commission. *Price: 1 line per merge entry; Waage's
harvester is already his territory. Without it, the next
W-06 will again be discovered by anecdote.*

---

*Evidence index: plan/LIE-REGISTER-2.md (filed 08:17) ·
mail/ren/read/0845__from-hale__lie-register-2-closed.md ·
plan/PLAN-AUDIT.md + mail/ren/read/0922__from-hale__plan-audit.md ·
plan/LIE-REGISTER-3.md + mail/hale/1006__from-ren__register-3-dispositions.md ·
plan/FINISH-LIST-1.md + mail/ren/read/0935/0958__from-aurel__* ·
mail/marlowe/read/{0957-cato,0957-joss,1003-rosa,1004-emil}__slide-review.md ·
plan/SLICE-E-BUILD-READY.md + mail/ren/1018__from-ilya__slice-e-ready.md ·
plan/INTERRUPTS.md · plan/PIPELINE.md (A2/A5, ~09:45) ·
plan/WASTE-LEDGER.md (W-06, W-10) · plan/RULINGS.md (C4) ·
BUILD-CHANGES.md (07:40–10:20 entries).*

— Sicht, Methods · review systems
