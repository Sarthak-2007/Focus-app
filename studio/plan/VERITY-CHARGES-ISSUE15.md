# VERITY — BRUTAL CRITIC CHARGES · ISSUE #15 FIRST PASS (the wide one)

**Seat:** Verity, brutal critic, #15 first pass. Ostra holds the
machinery second pass; Meister and the personas have filed their
own lenses. This pass covers the epic, the merge map, the lane
claims, and Ren's hand-work list.
**Method:** every charge re-derived from the trees myself —
lanes under `~/workspace/clone-inspect/builds/`, trunk =
`~/workspace/clone-inspect/Focus-app` (batch-9, DB v12). Plan
citations are to `studio/plan/BATCH10-MERGE-PLAN.md` (Ed2) and
`BATCH10-MERGE-PLAN-EDITION-3.md` (Ed3) on this branch.
**Law observed:** nothing compiled, merged, or gradled.

---

## THE MIGRATION (MIGRATION_12_13)

**V-1 · Rows 7–8: two columns for one fact, and a ruling menu
that hides the cheapest option.**
`heldShutAtSeal` / `heldPeekAtSeal` occur in ZERO files under
app/src in all seven lanes, merge-batch9, and trunk (grep,
this pass). The merged count already exists and is already
written at birth: trunk MonolithRepository.kt:1384–1390 stamps
`heldAppsAtSeal = countHeldApps(...)` beside `heldSitesAtSeal`.
W-41's own requirement (TRUTH3-WORDING.md, W-41): "the birth
writer must store the peek-tier count beside the shut count".
Store peek alone and shut = merged − peek, forever, with no
second column to drift against the first. The composed text
carries two new columns for one new fact, with NO stated
invariant (shut + peek = heldAppsAtSeal) and NO consistency
check in step 11's per-column verdict — the chain test proves
fresh ≡ upgraded SCHEMA, never that the three counters agree.
And §6 R1's menu (confirm −1 / set 0 / ship 6, hold 2) omits
W-41's own filed fallback: the zero-column line "UNDER THE
SEAL: {n} APPS · {k} SITES". Ren is being asked to rule from a
menu missing an option the source ruling put on the record.

**V-2 · The plan's W-41 crime scene is stale — batch 9 already
captures the split; what is missing is only its persistence.**
Ed2 §1: "the imprint still prints a live recount (batch-9
VM:837-838 contradicts its own doc at :622-625 today)". Read
trunk MonolithViewModel.kt:622–628: SealFreeze's doc states
shutCount/peekCount are "the split captured at the seal, never
a live re-read". :831–838: the freeze captures `count` from the
row's heldAppsAtSeal and shut/peek counts ONCE, keyed on
session change, inside that freeze. That is a seal-time
capture, not a live recount, and it does not contradict its
doc. The true gap is narrower than §1 tells: the split lives
only in memory; the receipt assembly (Repository :1641) still
reads the merged heldAppsAtSeal, so the PERMANENT record
flattens what the Deck already separates. The cure may still
be columns — but §1 argues them from a contradiction that is
not in the code, and R1 will be ruled on that telling.

**V-3 · Two contradictory "composed orders" for guard_settings
live in the record, and the epic body cites the wrong one.**
TRUTH3-MERGE-MAP §2 (the document issue #15's body names as
its authority): append peekMinutes, breakMinutes,
shortsReelsSniperEnabled after `lastReceiptSessionId`, in that
sequence. Frist's in-tree comment repeats it (times
MonolithModels.kt, GuardSettings tail: "these two append
first among the car's guard adds; Falk's
shortsReelsSniperEnabled follows"). History overruled both:
batch 9 landed shorts FIRST (trunk Models :778, last field),
so Ed2 §1 correctly appends peek/break AFTER it. But nobody
struck the old order where it stands — not in the map the
epic cites, not in Frist's tree comment. A merger who follows
the cited map composes an entity order that disagrees with
Ed2's exit check at step 2, and both documents call themselves
the composition authority.

**ACQUITTED on the migration:** no literal double-count — none
of the 8 ALTERs duplicates a column already in trunk (trunk
MIGRATION_11_12 carries only shortsReelsSniperEnabled +
breakTakenSeconds, MonolithDatabase.kt:192–193). Frist's five
pinned verbatim at times MonolithDatabase.kt:197–201, defaults
2/10/600/0/'' exactly as §1's table states. withinLabel pinned
nullable, NO default, matches Voss's source (routine-edit
MonolithDatabase.kt:184; MonolithModels.kt:370 `String? =
null`). Frist and Voss do append at the SAME anchor —
receiptNumber is the last constructor field in both lanes and
in trunk — so C12's collision is real and Ed2's fixed composed
order is necessary.

---

## THE LANE CLAIMS

**V-4 · Step 3's exit condition would sign off a compile
break: warrant's WatchStore deletes readMono, and the plan
only mapped that trap for nofire.**
Warrant's WatchStore.kt: grep `readMono` = 0 hits. Trunk:
WatchStore.kt:75 (field), :301/:316 (deriveWarrant stamps it);
§2 M7 itself names the consumers (MonolithCustomComponents
:415/:527, VM :1013) — in the NOFIRE paragraph. Ed2 §0 says
warrant "lands as written… This fix exists ONLY in this lane",
and step 3's exit is a "file-identity check against the lane
(its two files + test, nothing else moved)". Execute step 3 as
written — warrant FIRST, file-identical — and WatchStore loses
readMono on day one of the merge; the exit check passes it.
The plan found this exact landmine in the second lane's copy
of the file and never checked the first lane's.

**V-5 · "CONFIRMED" is one label over three evidence levels.**
- warrant: assemble PASS (warrant/completion-build.log, BUILD
  SUCCESSFUL) — but test-build.log reads "ACQUIRE FAILED
  (timeout) — no gradle run". Zero tests ever executed.
- nofire: proof.log ends "BUILD FAILED in 3m 23s…
  ASSEMBLE_EXIT=1… PROOF_DONE assemble=1 test=99". The lane
  has never produced a passing build of anything.
- onboarding: ReceptionSheetTest.kt carries exactly 13 @Test
  (verified count) and the lane contains NO build logs at all.
Ed2 §0 stamps warrant and onboarding "CONFIRMED as declared"
and nofire "CONFIRMED" — beside a §4 definition where
CONFIRMED means "evidence in tree/test". Code presence is
tree evidence; it is not test evidence, and for nofire it is
not even build evidence. At merge time these three will be
read with the same confidence. They have not earned the same
confidence.

**V-6 · Voss's admission artifact does not exist — §0's law,
§4A2's hole.**
§0: PARTIAL lanes are admitted "on their confirmed delta
inventory only — the delta list is the admission artifact".
§4A2's inventory for routine-edit names exactly ONE delta:
the RC-03 freeze (withinLabel). But the lane's editor work is
real and in-tree (verified: VM setRoutineTime :1335,
persistRoutine :1299; the dead voiced surface selectArtifact
:2233 / tapSerialPlateEasterEgg :2238 exactly where Ed2 says),
the epic's own checklist promises setRoutineTime + snap wheels
+ EDIT chip + create/rename/delete + E-14(c), and step 6
promises a hunk ledger — written DURING the merge. So either
the epic overpromises (only a freeze was ever admitted) or
the editor enters on a ledger that does not exist at the
admission gate step 1 is supposed to be. §4A3 has the same
disease by reference: flow-clock's inventory is "as enumerated
verbatim by issue #13's merge list" — an artifact in another
cell, not in this plan, for a step (7) whose source tree Ed3
itself describes as partly uncommitted. An admission gate
whose artifacts live elsewhere or later is a gate in name.

**V-7 · sealedSpanTargetSeconds is double-booked: admitted as
Frist's confirmed delta AND listed as Ren's hand-written
replacement.**
§4A1 admits "sealedSpanTargetSeconds derivation
(MonolithModels.kt:440)" as part of Frist's confirmed delta
inventory. §2's span-getter seam says the composed getter "is
hand-written at merge" and that Frist's own comment prescribes
a hybrid that "exists in NO lane" — verified at times
MonolithModels.kt:445–458, where his S-4 note specifies a
composed spanElapsed built on Tamm's breakTakenSeconds, a
column his lane does not carry, while his shipped getter below
it still prices breaks by full-cycle arithmetic. Both bookings
cannot be the plan: if the derivation is admitted, step 4
lands the arithmetic Ed2 calls the ST-02 lie; if it is
hand-written, A1's inventory overstates what Frist confirmed.
One of the two ledgers is wrong, and step 4's exit proof
("arithmetic verified") names the provider's arithmetic only —
not the span's.

**V-8 · Onboarding's "as declared" tree is ambiguous three
ways, and step 8 has no hash to land against.**
Ed2 §0/§4A7: onboarding CONFIRMED as declared, lands LAST,
blocked by #14's S-1 regardless. Ed3 census: the onboarding
LANE (builds/onboarding) is one tree; issue #14's cell sits at
local 9858a0b; its own errata then corrects that origin/agent/
issue-14 is at aa3bc36, "relation between the two commits is
UNVERIFIED". Three candidate sources, no commit named in any
step-8 entry condition. "Lands as declared" — declared by
which tree? The same ambiguity, smaller, hangs over step 7:
#13's merge source is a working tree Ed3 forbids merging from
until committed, yet no step names the commit that discharges
the ban.

**ACQUITTED on the lanes:** pill's "exactly 18 tests" —
PillDerivationTest 15 @Test + PillLeaseRepositoryTest 3 @Test,
counted. Flow-clock's 20 s settle is real (DeskClockSettle.kt
:109 `SETTLE_MILLIS = 20_000L`) with 14 @Test in
DeskClockFlowTest. Routine-edit's v10 character is real:
ReceiptState plannedSeconds is non-null `Int` (routine
ReceiptState.kt:136/164) against trunk's `Int?` (:146/175),
and UndoDeleteStore.kt is absent from its data/. The BASE-WINS
law itself is correct and load-bearing: Frist's GuardSettings
does not even contain shortsReelsSniperEnabled (his entity
ends peekMinutes/breakMinutes, Models :835–836) — an overlay
of his Models would silently delete a live v12 column, exactly
as §0 warns.

---

## REN'S HAND-WORK (step 9) — SPECIFIED OR VIBES?

**V-9 · The epic body understates the hand-work by ~14 items;
Ed3 admits it, the issue never does.**
Issue #15 body, Ren's hand-work: W-08, W-41, W-43. Three
items. Ed2 actually assigns Ren, all OWED (existing in no
tree): §4B6 the Repository-head unlisted-package guard; §4B7
three S-7 peek-value rewires (withheld-reason rekey, the
composed canPeek expression, the airlock badge rewire); §4F2
Receipt Group A's A1–A11 beyond the two feeds (nine cures);
§4F3's Frist-side print plumbing. Ed3 step 9 note, in the
cell's own words: "this step is the epic's true ceiling — none
of it exists in any tree". The ceiling of the epic is a step
whose contents grew by ~14 items between the issue body and
the plan, and the issue body — the scope statement at the top
of this thread — was never amended. Anyone pricing Batch 10
from the epic reads a third of the hand-work.

**V-10 · W-08/W-41/W-43 had named owners; the plan silently
reassigns them to "Ren at merge" with no discharge.**
TRUTH3-WORDING.md's own routing: W-08 → Bodo (plate owner);
W-41 → Rurik ("a column on Rurik's migration spine"); W-43 →
Rurik, twin-bound to W-13. Ed2 §4B1/B2–B5 reassign all of it
to "OWED — Ren at merge" without recording that the original
assignees never delivered and were never discharged. Maybe
the reassignment is right — the lanes are closed — but a
ledger that rewrites ownership without a transfer entry is
how work gets lost between "his job" and "mine". REGISTER's
§4 tracks every other input's custody (B5 even notes W-43's
custody "on record: MAIL-AUDIT-2.md:83"); the three transfers
into Ren's hands get no such entry.

**V-11 · B4's print law is two laws in one sentence, and the
distinction it hides is the whole design.**
§4B4: '"HELD {n} SHUT · {m} PEEK · {k} SITES", zero segments
omitted, all-or-nothing gate'. The sources keep two different
laws apart: W-41/W-23's SEGMENT omission (a zero segment does
not print) and AUDIT-receipt item 9's LINE omission (while the
split is unknown — the −1 sentinel — the line does not print
at all). B4's sentence, read alone, contradicts itself: a
session with 0 peek apps must print "HELD {n} SHUT · {k}
SITES" under the first law and omit the line under a flat
reading of the second. Step 9's phrasing ("HELD prints only
when shut, peek AND sites are all ≥ 0") rescues it — ≥ 0
admits the legitimate zero — but the rescue lives in a
different section from the print spec it governs. The man
composing the slip at step 9 should not need a critic to
reconcile his two paragraphs.

**V-12 · The hand-work carries no tests of its own.**
Every lane delta in §4A arrives with named proof tests. The
§4B items — a new Repository guard (B6), a seal-time writer
(B3), a split print (B4), three value rewires (B7) — name NO
test, no FAIL-baseline at step 0 (§5 G2 requires FAIL→PASS
with hashes for "a cure"; §4B's items are cures by any reading
of W-08/W-41/W-43), and step 11's register inherits whatever
step 0 captured. If the baselines for B1–B7 are not captured
by name at step 0, the epic's most original work — the part
no lane ever wrote — is also its least provable. The plan
assigns the chain test (step 11) and the reconciliation table
(step 10) by name; for step 9 it assigns adjectives.

---

## THE EPIC ITSELF

**V-13 · The epic's cited authority is a map history already
falsified.**
Issue body: "per the merge map (plan/TRUTH3-MERGE-MAP.md)".
That map's §2 composed a FOUR-column 11→12 (peek, break,
breakSeconds, shorts). What actually landed in batch 9: TWO of
the four (shorts, breakSeconds' siblings aside — peekMinutes,
breakMinutes and breakSeconds themselves never landed), plus
breakTakenSeconds, a column §2's table never listed — Ed2's
header concedes this in one sentence
("batch 9's actual 11→12 took only two of its four listed
columns, plus breakTakenSeconds, which §2 never listed").
Half the map's migration was wrong in both directions —
promised columns absent, landed column unpromised — and the
epic still opens by citing it as the composition authority
without a correction line. Ed2 supersedes it in fact; nothing
supersedes it on the record Sarthak reads.

**V-14 · Step 11's migration verdict cannot catch the failure
mode this programme already suffered once.**
Step 11 exit (b): "the migration chain verdict per column
(12→13, fresh ≡ upgraded)". Fresh ≡ upgraded is a SCHEMA
equality. The live phone's risk is not schema: it is a v12
database written by batch 9's code meeting v13 entities whose
field ORDER Ed2 fixes by hand (C12) — with exportSchema=false
there is no schema file to diff against, so the "verdict" is
one hand-run chain test, itself an OWED artifact (§6, "blocks
step 11"), written by the same merge it certifies. That may
be the best available instrument. The plan should say so,
instead of presenting (b) as a verdict line beside suite
counts and a sha256, as if all four lines carried the same
weight of proof.

---

**Count: 14 charges (V-1…V-14), 3 acquittal blocks.**
Worst first: V-4 (an exit condition that certifies its own
compile break), V-1 (schema debt for one fact, ruled from a
short menu), V-9 (the epic's ceiling hidden from the epic).

— Verity (brutal critic, #15 first pass)
