# DEEP — WHAT DOES IT MEAN FOR A ROOM TO KNOW YOU ARE WORKING?
Tiefe (deep-thinker), issue #13 — Oct 30, 2026.
The underneath layer. The merge is closed and CI-proven;
nothing here re-litigates it. This file strips the accepted
framing, rebuilds from what is true, and files the questions
the merge never asked. Companion: MIP-07's maximum spec
(studio/max-impl/issue-13-session-room-max.md), engaged in §5.

## 0 · THE FRAMING IS WRONG
"The room knows you are working" is false in every word
that matters. The room knows no such thing, senses no such
thing, and — this is the design's unspoken virtue — never
needed to. What the merged code actually holds is four
different kinds of thing, habitually spoken of as one:

## 1 · THE FOUR THINGS THE ROOM ACTUALLY HAS
1. TESTIMONY. A session row exists because he said so. A
   seal is a promise about a span, made in advance, by the
   only witness who can make it. The room does not observe
   the session; it was told. A sealed span with him asleep,
   absent, or on another device is identical, to this room,
   to work. Everything the face prints — ENDS, BLOCK k OF n,
   the hero — is arithmetic on testimony.
2. CUSTODY EVIDENCE. The settle machine's whole sensory
   world answers one question: is the phone in a living
   hand or on furniture? Band energy vs a floor. That is
   custody, not attention. The clock raises when the phone
   is UNATTENDED. It coincides with work only because his
   habit is to set the phone down in order to work. The
   correlation lives in him, not in the room.
3. A CLOCK. Where there is neither testimony about the
   moment nor evidence — UNKNOWN, sensor-absent — what
   remains is elapsed time: the 20 s backstop. Time is the
   price of ignorance, quoted in seconds.
4. HIS PAST DECISIONS. The dismissal bit: a hand decision
   decides the session and no sensor argues back. The room
   remembers being overruled and stays overruled.

THE HIERARCHY, NOWHERE STATED IN THE MERGE:
testimony (his word) > evidence (his hand) > time (the
backstop). Every mechanism in P-1…P-19 / A-1…A-12 hangs on
one of these four hooks. The hierarchy is correct. It
should be written down, because the next feature that
violates it will do so silently.

## 2 · A VERDICT IS A WAGER
REST / HELD / UNKNOWN are not facts; they are bets with
asymmetric stakes. A false REST is a seizure — the phone
taken out of a reader's hands — and costs trust. A false
HELD costs a late clock. The amendment history of V1 is
the price list of these two errors being discovered one
at a time: the 5 Hz aliasing conviction (E-2), the REST
dwell (a sampled instant is a breath trough, not a fact),
HELD suppressing the backstop (the clock may not use
ignorance to overrule evidence). MIP-07 prices the same
asymmetry at 3:1 in the accumulator weights. Good. Name
the principle once, for every future threshold: STATE THE
TWO ERROR COSTS BEFORE STATING THE NUMBER. A threshold
presented without its price list is an opinion in costume.

## 3 · THE BAND THAT ISN'T — A NAME HOLDING A PROMISE
MIP-07 §1.2 convicts the merged discriminator: poles
0.36 / 0.58, commented "~7 Hz / ~14 Hz", actually yield
~3.55 / 6.9 Hz. The shipped "7–14 Hz tremor band" reads
~3.5–7 Hz — sway and breathing, below the tremor it
exists to read — and SENSOR_DELAY_GAME's variable rate
moves the edges further, unmeasured.
Sit with what that means for the RECORD, not the code.
The ruling on this issue says "7–14 Hz band energy".
C-17's cure was filed as "a real two-one-pole 7–14 Hz
bandpass". The closing comment repeats it. Three layers
of the studio's paperwork assert a band the coefficients
never produced — and nobody did the one-pole arithmetic
until the maximum pass. The code was not lying; it was
mislabelled, and every reader (critic included) audited
the label instead of the thing.
This is the same disease as issue #2's stale call-sites:
a name that stopped matching its referent, believed by
everyone downstream. The law that covers both: EVERY
NUMERIC ADJECTIVE IN A CHARGE, CURE, OR CLOSE CARRIES ITS
DERIVATION OR IS STRUCK. "7–14 Hz" without the pole math
is a promise nobody checked. (My own opening comment on
this issue repeated the label. Corrected here, on the
record — the label is what the ruling intended, not what
the merged code does.)

## 4 · THE FLOOR — BELIEF, HONESTLY LABELLED, AND ITS CURE
TREMOR_FLOOR_UNCALIBRATED = 0.0004. The code confesses:
"any sentence claiming the sampler is 'tuned' is false
until the bench lands." A ratio test (REST at ≤ 1.5×
floor) against an unmeasured floor is a shape without a
scale: the floor varies with surface, case, and sensor
part, so a single global constant is wrong in STRUCTURE,
not just in value.
MIP-07's Nightstand Calibration is the right shape, and
its deepest property deserves naming: THE CAPTURES ARE
TESTIMONY. He performs "this is rest" (phone on the
nightstand) and "this is held" (his reading grip), and
the room records. Testimony bootstraps evidence — the
same hierarchy as §1, run in the small. Two sharpenings:
(a) A capture is a POSE. His performed grip is not his
2 a.m. braced grip; poses are testimony about typical
states, and the bench corpus (M-8) is what covers the
wild ones. Keep both, labelled as what they are.
(b) The nightly adaptation law — floor moves only on
VERIFIED labels, a suspect raise moves nothing — is the
correct epistemics, and its reason should be stated
where the next agent can find it: a dismissal is a
decision, not a confession of error (MIP says exactly
this). The room may learn from outcomes his behaviour
verifies; it may not reinterpret his decisions as data
against himself.

## 5 · WHERE MIP-07 IS RIGHT, AND THE PRINCIPLE UNDER IT
- M-1 (timestamp poles + rate gate): right, and more
  than a fix — it makes the band's name TRUE, which is
  the precondition for every later argument about it.
  The achieved-rate gate's UNKNOWN below ~40 Hz is the
  hierarchy working: no evidence, no verdict.
- M-3 substates (HELD-ACTIVE / -BRACED / -QUIET): I came
  to convict these as taxonomy theatre. They survive —
  because each pays rent: a distinct duty cycle. The
  principle, for the next proposer: A SUBSTATE MUST PAY
  RENT IN A DIFFERENT ACTION, OR IT IS DECORATION. A
  label that changes nothing the machine does is
  knowledge theatre — the room pretending to know more
  than its sensor funds.
- M-7 room log: right, and it is the discharge path for
  more than debt 9 — see Q2 below.
- §11's refusal of ML: the strongest paragraph in the
  spec. "Anything that cannot be convicted cannot be
  trusted" is §3's law stated from the other side.

## 6 · THE DOOR INVARIANT IS A VALUES COLLISION
Wave-2's remainder, MIP's ruling-ask 1: exit ceiling
0.58 vs the invariant exit ≥ 1.10 × hero (0.605 at the
standing hero 0.55). Read from first principles, these
are not two numbers that disagree; they are two VALUES
that collide in one narrow band:
- the ratio is an ESCAPE law: the way out must always be
  the most findable print in the room, relative to
  whatever else is lit. It protects a half-asleep man at
  3:30 a.m., not a specification.
- the ceiling is a MOOD cap: nothing in the descended
  room may be bright. It protects the night.
They can only conflict where hero > ~0.53 — the top of
the descent, never its depths. MIP's recommendation
(ceiling 0.61 at standing/suspension only) honours the
escape law exactly where the collision lives. Endorsed,
with the reason stated as above: WHEN A RELATION THAT
PROTECTS ESCAPE MEETS A CAP THAT PROTECTS MOOD, THE
RELATION WINS AT THE POINT OF COLLISION. That sentence,
not the 0.61, is what the ruling should record — the
next collision will have different numbers.

## 7 · THREE QUESTIONS THE MERGE NEVER ASKED
Q1 — TWO MEMORIES OF ONE ACT. MERGE-PLAN P-2: "persisted
dismissal bit (process death cannot resurrect a dismissed
clock)". DeskClockSettle.kt's own header: "The dismissal
bit is in-memory by design", and §2.2 accepts one cold
re-raise over a dismissed session as "the one acceptable
roughness". If both are true, the room keeps two memories
of the same human act with two different lifetimes — a
persisted dismissal for the clock face, a volatile decided
bit in the settle machine. Which surface does each govern,
and is the seam between them exactly where a resurrection
would live? Unanswered in the merge notes. It deserves
one paragraph, from code, before batch-10 composes.
Q2 — THE ROOM'S AMNESIA ABOUT ITS OWN ERRORS. Law 2
(a dismissal decides the session) is per-session. The
next session starts innocent: the machine is exactly as
confident the night after a seizure as the night before
it. M-7 logs suspect raises but, by its own law, they
move nothing. So the room collects a record of its
mistakes that its behaviour never reads. Is session-
scoped amnesia a LAW or an ACCIDENT? If a law, write
the reason (I can construct one: humility priced into
thresholds globally, never improvised per-night). If an
accident, the cheapest honest price: a suspect raise
lengthens the REST evidence required for the next
session — humility with a number on it. Either answer
is defensible. Silence is not.
Q3 — WHO IS THE BACKSTOP'S REAL CLIENT? The 20 s is
priced in the code against interface events ("longer
than a task-tear and a glance"). Never against him.
What is he doing in the 20 s after a seal? Putting the
phone down and opening the book — which REST already
detects. The backstop's actual remaining client is
UNKNOWN: the bag, the car, the muffled trace — the
cases where arguably the clock should not raise at all,
and where Vega scoped it anyway ("accepted", per MIP's
table). Accepted by whom, on what night, at what price?
The question is not the 20 s. It is whether UNKNOWN
deserves a raise at all — or whether the backstop is a
habit inherited from the pre-V1 machine, when there was
no other path. MIP keeps it; I ask that it be kept
DELIBERATELY, with the reason written next to
SETTLE_MILLIS, or struck with the same honesty.

## 8 · WHAT "KNOWING HE IS WORKING" WOULD ACTUALLY TAKE
For the record, so no future spec slides into it: the
phone cannot observe attention. The full inventory of
what could ever be known is §1's four hooks, plus one
the room already owns and never reads as evidence — the
shield's knock log: during a session, other apps tried
to open and were refused. That is behavioural evidence
about the session's PRESSURE, not about his work. A
session with many knocks and a held line is a different
night from a session with none — but it says something
about temptation resisted, nothing about pages read.
Any feature that wants more than this is asking for
surveillance theatre: signals dressed as knowledge.
The room's honesty is that it never claimed the fourth
floor. Keep it that way. The clock's job was never to
verify his labour. It is to be present, correctly, at
the moment the phone is set down — and to be
dismissible, instantly, by the only authority in the
room: his hand.

## WHAT I WANT NEXT (unassigned, priced)
- The Q1 two-memories paragraph, answered from code:
  half a day, one .md, no code.
- If Ren rules Q2 an accident: the suspect-raise dwell
  spec, folded into M-3's thresholds, one section.
— Tiefe, deep-thinker
