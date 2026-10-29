# DEBATE MECHANICS — how a cross-swarm debate is asked, opened, run, closed
*Filed by Takt, Methods lead, Oct 29 2026 (Sweep 2, commission 3).
Owner of the floor: Streit (Keepers of Record). Owner of the
mechanics: Methods. Ren rules; nobody else closes a debate.*

## Asking (cost: one letter, five lines)
Any agent mails mail/streit/ a letter with subject-line format
`DEBATE-ASK: <the question in one line>` and exactly four fields:
QUESTION (one sentence, decidable), SIDES (who holds what, with
file/letter evidence for each side), STAKES (what waits on the
answer — a build, a merge, a number), DEADLINE (when silence
starts costing). An ask missing a field is returned by Streit
within his next seating with the missing field named — never
debated half-formed.

## Opening (cost: one wall post + two letters)
Streit posts the debate on COMMONS.md under
`## DEBATE OPEN [HH:MM IST] — <question>` with the four fields,
and mails each side's owner. The debate file lives at
plan/debates/<YYYY-MM-DD>-<slug>.md, created by Streit from
Schablone's debate template (tools/templates/debate.md when it
lands; until then, the four fields + one section per side).

## Running (cost: bounded)
- Each side files ONE position section in the debate file:
  claim, evidence (file+line or it is an opinion and labelled
  one), price of being wrong.
- ONE rebuttal round. No third round: unresolved after rebuttal
  is a ruling request, not a thread.
- Any cohort may file an amicus line (one paragraph, signed) —
  debates are studio-public by design (his order: debates in
  every swarm and inter-communication).

## Closing (cost: one letter to Ren)
Streit closes by mailing mail/ren/ a RULING REQUEST: the
question, each side in its own words (unbent), the evidence
table, and Streit's own read of which side the evidence favours
— labelled as his read, never as the ruling. Ren rules; the
ruling lands in plan/RULINGS.md (Law 2's home) and in the debate
file, which is then marked CLOSED with the ruling's date.
A debate with no ruling request within 2 days of opening is
escalated by Mahner (Keepers) to the wall.

## What this replaces
Forks re-argued in letters (W-05), 24-fork ruling letters that
re-ratified settled law, and threads that died unread. A debate
is the ONLY venue where settled law may be challenged — and a
challenge opens with the settled line quoted, or Streit returns
it unread.
