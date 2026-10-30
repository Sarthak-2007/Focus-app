# CRIT-1 — Brutal critique of the cold-start cure, AS LANDED (issue #7)

**Seat:** CRIT-1, BRUTAL CRITIC, BINDUNG's cold-start cure cell. Report to BINDUNG only.
**Branch judged:** `agent/issue-7-coldstart` @ `faa99af` — cure = `1b641aa` (BUILD-1,
active self-probe) + `55bcae4` (BUILD-2, cured refusal table) + `b13ca1a` (BUILD-3,
tests). All three builds had landed when judged; this is a judgment of landed code,
not of the spec. (First CRIT-1 seat died at bootstrap; this seat started clean and
re-read everything. The branch moved during the review — BUILD-3, FEEL-1, DAY-1
landed mid-read — and every charge below was re-based on the final tree.)
**Method:** READ-ONLY on code. No compile, no run — charges rest on file:line
evidence quoted or cited exactly. Where a sibling seat (DAY-1, FEEL-1) reached a
charge first, I say so and only concur where my own read confirms it.

**Headline verdict:** The cure kills the fog *sentence* and genuinely closes the
bound-and-quiet cold start. It does NOT kill the fog *disease*. It replaces a print
that claimed too little ("can't check") with a proof that claims too much: a
self-issued stamp the seam's own binding definition forbids, minted once and never
expiring, beside a seam print the cure never touched that still says UNSEEN while
the new sentence says OFF. BINDUNG's claim "the fog cannot rest" is false as
stated — see C-3 and the scope charge C-9.

---

## Charges

### C-1 — HIGH — Probe-as-proof is a self-assertion, and LIVE BIND's own definition forbids it

The event stamp and the probe are **not the same fact**, and the code's own
doctrine says so.

- The seam's binding definition (`MonolithComponentLogic.kt:54–63`): the grade is
  taken over "a system-delivered root-stamp **event**" and "**No leg is the
  service's self-assertion** (the shield-protecting leg is excluded from the LIVE
  conjuncts for exactly this reason)". LIVE prints `"BIND HELD · THIS VISIT"` /
  `"PROOF: LIVE BIND"` (`:65`).
- The event path honours that: `onAccessibilityEvent` stamps `lastRootMono` only
  when the system actually delivers an event and its root is acquired
  (`MonolithAccessibilityService.kt:~512–518`, stamp before any early return).
- The probe (`MonolithAccessibilityService.kt:377–391`) stamps the **same**
  `lastRootMono` from inside the process, on the service's own IO scope, with no
  event delivered, no event thread involved, nothing system-delivered at all. It
  is precisely a self-assertion — the one leg type the definition excludes.

**The failure mode the probe waves through:** event *delivery* wedged while root
queries still answer. The system stops dispatching to `onAccessibilityEvent`
(OEM event throttling/killed dispatch, a service-info mismatch, a dead event
thread) while the service stays bound and `rootInActiveWindow` keeps returning a
window to any caller. Probe stamps → `deriveWarrant` reads LIVE
(`WatchStore.kt:443–450`) → seam prints LIVE BIND, the slide commits
(`BlockitCoreViews.kt:1352–1353`), the imprint prints WILL SHUT over that warrant
(`imprintTenantText`, LIVE-only tenant) — a full certification of a blocker whose
intercept path (`onAccessibilityEvent` → conflated channel → `processEvent`) has
processed nothing and will process nothing. The old fog lied by under-claiming at
rest; LIVE-on-probe can lie by over-claiming in exactly the state the app exists
for. That is the disease this cure claims to kill, wearing a better suit.

Partial mitigation, honestly noted: the probe does prove bind + root-API
liveness, which is most of the real cold-start failure space, and a wedged-
dispatch-but-live-root state is rarer than the stamp race. Rare is not never,
and the print does not say "PROBABLY LIVE". Either the probe must be demoted
(probe-stamp proves BIND ONLY; LIVE still requires one delivered event) or the
definition at `:54–63` must be amended in the open — right now the doc and the
code contradict each other, and the doc is the one Sarthak's truth law rests on.

### C-2 — HIGH — One stamp, minted at T+0, never expires: the zombie-bound service reads LIVE all day

`stampedThisResidency()` is a bare comparison with no age, no TTL, no renewal:
`residencyStartMono > 0 && lastRootMono >= residencyStartMono`
(`WatchStore.kt:298–300`). `deriveWarrant` performs no stamp-age check. After the
+1500 ms re-probe there is **no further probe, ever** — no cadence, no heartbeat
re-proof (grep: `requestPipelineProbe` has exactly three call sites —
`WatchStore.kt:280`, `WatchStore.kt:292`, `MonolithAccessibilityService.kt:353`).

So: probe stamps at residency start; at T+1 s the event pipeline dies while the
service stays bound (`instance != null`, so `standingWarrant()`'s `a11yBound`
stays true, `MonolithRepository.kt:~692–694`); every fresh re-read re-derives
LIVE from the corpse stamp for the residency's whole life. The pre-cure design
had the same no-TTL hole, but its stamp at least cost one *real delivered event
in this visit* — some evidence the pipeline processed something after the visit
began. The cure makes the minting free, instant, and self-generated (C-1), which
converts a theoretical hole into the normal path: **every** residency's LIVE now
rests on a single self-query whose result is trusted forever. The anti-stale
purpose of the residency stamp (anti-inheritance) survives — A-3 — but its
anti-zombie purpose never existed, and the cure's marketing ("the pipeline
proves itself") implies it does.

Priced fix: stamp TTL + renewal — a stamp older than N minutes degrades to
UNPROVEN and triggers a re-probe; the probe machinery already exists, only the
cadence is missing (~15 lines in `WatchStore` + one derivation input).

### C-3 — HIGH — Past grace, the same Deck prints OFF and BIND HELD · UNSEEN in the same frame; BINDUNG's "the fog cannot rest" is false

The cure touched the sentence slot and nothing else. The seam — the other print
on the same Deck — was left exactly as it was:

- Sentence, past grace, UNPROVEN + unstamped: `"BLOCKING IS OFF — TAP TO FIX"`
  (`MonolithComponentLogic.kt:256–268`).
- Seam, same warrant, same instant: `"BIND HELD · UNSEEN"` to
  `"PROOF: BIND ONLY"` (`MonolithComponentLogic.kt:77`; stale variant `:101`).
- Header Guard line, same instant, can simultaneously print `GUARD 6/6`
  (`GuardLine.kt:59`) — its ACCESSIBILITY item is measured by service *existence*
  (`MonolithRepository.watchGrantQuery`), not by the stamp.

Three prints, two contradictory verdicts, one screen. Worse, the OFF sentence
commits the seam's own cardinal sin in reverse: the seam reserves `POSITIVE` for
"a directly observed dead signal" (`:47–49`), but nothing dead was observed past
grace — a probe merely *didn't stamp*. Absence of proof is printed as proof of
absence; the cure's doc even asserts it: "bound-but-blind is off in fact"
(`:223–226`). It is not a fact. It is an inference from silence — the same
inference-from-silence the fog made, hardened into a stronger word. And
`BIND HELD · UNSEEN` *is* the fog's surviving equivalent: held, but we haven't
seen it work — that is "can't check" in the seam's vocabulary, untouched by a
cure whose lead claims the fog "cannot rest". It rests. It's at
`MonolithComponentLogic.kt:77`, `:82`, and `:101`.

### C-4 — MEDIUM — There is no third net: two probes can both miss, and then OFF is permanent and unrecoverable in place

Probe schedule, complete: residency start (`WatchStore.kt:280`), +1500 ms if
still unstamped (`:288–294`), rebind (`MonolithAccessibilityService.kt:353`).
Nothing else. Both scheduled probes can legitimately miss on a healthy phone:
`rootInActiveWindow` transiently null during a window transition, lockscreen, or
system dialog at both instants. The null-root miss is also the *silent* miss —
`probePipelineNow` returns false without counting it
(`MonolithAccessibilityService.kt:382–384`; only a thrown query reaches
`noteDetectionFailure`). On a quiet Deck no event will ever rescue the state, so
the Deck sits at BLOCKING IS OFF (C-3's overclaim) indefinitely, until he leaves
and re-enters the Deck or some unrelated event happens to fire. A cure for a
race that can permanently lose its own race — with the loss invisible in the
failure counter — is unfinished. Priced fix: while UNPROVEN + unstamped, re-probe
on a slow cadence (e.g. every grace window) or at minimum on the sentence's own
print path; count null-root misses like thrown ones (~10 lines).

### C-5 — MEDIUM — The grace's silence coincides with a physically refused slide, and the worst cold case gets no grace at all

The grace silences the *sentence*; it does not move the *gate*. Commit requires
LIVE at the latch read (`SlideGestureMath.commits`, `BlockitCoreViews.kt:1352–
1353`) and UNPROVEN travel stops at the catch (`travelLimit`, `:1342–1347`). So
inside the 4 s, on a slow-but-healthy phone, he slides, the cap stops, nothing
commits — and by design **nothing on the glass says why**. Silence was chosen as
"the machine is working"; at the one moment he *acts*, silence reads as a broken
slide. The cure speeds the healthy arm (A-5) but, in the slow tail, trades a
lying sentence for an unexplained refusal.

I independently confirm DAY-1's D-1 / FEEL-1's F-1 from my own read, and it is
worse than the grace question: the unbound row has **no grace at all**. At a cold
process start `instance` is legitimately still null, the residency-start probe
is a no-op by construction (`WatchStore.kt:280`, `instance?.`), the first warrant
derives `!a11yBound → OFF`, and the table's first row prints BLOCKING IS OFF
*immediately* (`MonolithComponentLogic.kt:246–247`) — plus the slide's first
read returns OFF and clamps to `OFF_STOP` with the refusal sequence
(`BlockitCoreViews.kt:1342–1348`). The cure's own comment names this race
("the service may not be bound yet when the Deck appears") and then graces the
other row. At 04:55, half-awake, the healthy phone's first frame can still tell
him his guard is dead and refuse his first slide. That was the original wound,
in its first-frame form, and it survives.

### C-6 — MEDIUM — The FIX door does not do what the cure's doc says it does

Cure doc: past grace, BLOCKING IS OFF prints and "its FIX door rebinds the
service, which is the one act that can change the fact"
(`MonolithComponentLogic.kt:223–226`). The handler does no such thing:
`onFixBlockingAccess` opens `Settings.ACTION_ACCESSIBILITY_SETTINGS`
(`MainActivity.kt:679–684`). In C-4's transient cases and C-5's slow-bind case
the grant in that pane is already ON — there is nothing to fix, the actual
rebind (toggle off/on) is nowhere named, and a man sent to "fix" a healthy
grant learns to distrust the sentence. Either wire the door to a real re-probe/
rebind act the app owns, or stop claiming in the doc that it does.

### C-7 — LOW — The probe inherits the event stamp's oldest weakness: it can stamp on Monolith's own window

At residency start the active window *is* Monolith's own Deck, so the probe's
successful root acquisition proves the root API answers for the app's own
foreground — not that a foreign foreground would be seen. The event path shares
this (its stamp is taken before the own-package early return,
`MonolithAccessibilityService.kt:~512–525`), so the cure did not create it; I
file it because C-1's demotion fix is the natural place to also require the
proving root to be a non-self window, and because nobody in this cell had named
it. Inherited, low, real.

### C-8 — MEDIUM (process) — The load-bearing link is the one with zero automated proof

BUILD-3's own test header concedes it: `probePipelineNow()` "needs a live
service + window root" and is device-only; the Robolectric half proves the
contract by writing `lastRootMono` directly and checking the comparison
(`ColdStartWarrantTest.kt:40–47`, `:390–417`). That verifies arithmetic, not the
mechanism. The actual chain — `noteResidencyStart` → `instance?.` →
fire-and-forget `scope.launch` on `Dispatchers.IO`
(`MonolithAccessibilityService.kt:283`, `:397–402`) → root query → stamp → poke
→ collector re-read — has no completion signal and no test: a probe coroutine
that is never scheduled, is cancelled, or loses to process death is
indistinguishable in the code from a probe that ran and failed. The grace tests
all use hand-built warrants. Under the PEAK BAR, the cure may not be called
verified until VER-1 (or a device run) exercises the real chain end-to-end on a
cold process start. This charge is against the cell's completeness claim, not
the builder's honesty — BUILD-3 disclosed the gap in its own header, which is
more than most lanes do.

### C-9 — MEDIUM (process, against the lead's framing) — Scope honesty

BINDUNG's brief to this seat claims the cure means "the fog cannot rest". As
landed: the fog *sentence* cannot rest in the sentence slot (A-1). The fog
*condition* — bound, held, unproven, unexplained — still prints on the seam
(C-3), still gates the slide without a word (C-5), and still gets minted into
LIVE by a proof weaker than the one the doctrine demands (C-1). The brief also
described the cure as judged on BUILD-1 + BUILD-2; the tests that make the table
change survivable were a third build the brief didn't mention. Leads' summaries
are how Sarthak hears about this work; a summary that outruns the diff is the
first step of every drift he has ever had to correct. Price of the fix: one
corrected sentence on #7 — "fog sentence deleted; seam UNSEEN and probe-proof
questions open under CRIT-1 charges".

---

## Acquittals — what survives, and why it matters

**A-1 — The fog string is truly deleted, not reworded.** Grep over
`app/src/main`: `CAN'T CHECK BLOCKING` appears nowhere as a print (only a
paraphrase inside a comment, `MonolithComponentLogic.kt:212`). `RETRY_CHECK`
has no producer left in the cured table, and BUILD-3 sweeps the full table
proving the fog sentence and `RETRY_CHECK` unreachable
(`ColdStartWarrantTest.kt:284–318`). The single-arg overload delegates to the
timed one with the process clock (`MonolithComponentLogic.kt:230–231`), so no
caller can resurrect the old row. This was the named wound — on every reopen —
and for the bound case it is closed.

**A-2 — Usage DENIED/UNSET outranking the unstamped row is correct and correctly
built.** Table order (`:248–251`) puts both usage rows above the stamp-race row;
a known lack with a known owner and a working door is never again masked by a
transient. BUILD-3 pins it at every residency age
(`ColdStartWarrantTest.kt:189–255`).

**A-3 — The probe fails honestly and cannot launder stamps.** Null root or a
thrown query stamps nothing and returns false (`MonolithAccessibilityService.kt:
377–391`); `onUnbind` zeroes `lastRootMono` (`:494–503`), so a rebound service
never inherits its predecessor's proof; the +1500 ms re-probe fires only for the
*same* residency still unstamped (`WatchStore.kt:291–293`), and BUILD-3 proves a
new residency supersedes the old stamp (`ColdStartWarrantTest.kt:419–456`). The
anti-inheritance core of the residency design is intact.

**A-4 — The grace arithmetic is sound.** Age is computed from the warrant's own
carried `residencyStartMono` (`:259–261`), clamped at zero, and a warrant with
no residency carried (0) gets **no** grace — age unknown is never treated as
fresh. The stale-print guard runs before the table (`:244`), so no sentence is
ever ordered off a dead read. The boundary is pinned by tests: silent at
T+4000, OFF from T+4001 (`ColdStartWarrantTest.kt:117–160`).

**A-5 — The healthy arm is genuinely faster, and the gate was not weakened to
get there.** The cure does not let UNPROVEN commit, does not touch
`travelLimit`/`commits`, and does not gate the slide on the sentence. It removes
the wait-for-an-event-that-may-never-come by making the proof active; on a bound
phone the stamp lands in milliseconds and the in-gesture reads (DOWN, gate, UP)
convert a mid-slide stamp with no re-gesture. For the case Sarthak actually
hits every morning — bound service, quiet Deck — the wound is closed. That is
the cure's real win and it is not small.

**A-6 — Two of the three audited surfaces are clean.** GuardLine has no fog
equivalent: unmeasured prints *nothing* (`GuardLine.kt:44–46`), and its lack
form names a permission and its staleness honestly. `LauncherBlockScreen`
carries no warrant print at all (grep: no warrant/seam references in the file) —
it cannot fog because it does not speak about the wire. BINDUNG's "fog cannot
rest" claim holds on these two surfaces; it fails only at the seam (C-3).

---

## Bottom line for BINDUNG

Ship-shape for the sentence slot; not yet truthful as a system. Before this
lane calls itself done: answer C-1 (demote probe-proof or amend the definition
in the open), C-2 (stamp TTL/renewal), C-3 (reconcile seam and sentence past
grace — one verdict per screen), and C-5/D-1 (grace the unbound row via the
enabled-but-unbound read DAY-1 priced). C-4 and C-6 are cheap. C-8 gates any
"verified" claim. The fog sentence is dead; the fog's *logic* — claiming more
than the proof holds, in whichever direction flatters the moment — is wounded,
not dead.

## Efficiency account (CRIT-1)

- **Artefacts:** this file + the signed summary comment on issue #7. Nothing else
  created, nothing else touched (read-only seat honoured; only `crit-1.md`
  committed).
- **Read, in full where it mattered:** SARTHAK-WORDS.md; all three cure diffs
  (`1b641aa`, `55bcae4`, `b13ca1a`); `MonolithAccessibilityService.kt` probe/event/
  unbind sections; `WatchStore.kt` residency + derivation; `MonolithComponentLogic.kt`
  seam + cured table; slide gate (`BlockitCoreViews.kt`), GuardLine,
  LauncherBlockScreen (grep-verified), FIX handler (`MainActivity.kt`); sibling
  reports DAY-1, FEEL-1 (charges), RECORD.md; BUILD-3's test file structure.
- **Idling/waste, admitted:** one early diff read was taken seconds before
  BUILD-2 committed and had to be re-read — minutes, not more. One `gh` issue
  read was slow; I did not wait on it for evidence — every charge rests on local
  git/file evidence. No agents spawned, no compiles run (read-only brief), no
  duplicate of DAY-1/FEEL-1 charges filed as my own — D-1/F-1 is filed as a
  confirmed concurrence (C-5), not a discovery.
- **What I would do next, unassigned:** re-judge after the C-1/C-3 fixes land —
  specifically whether a demoted probe-proof (BIND ONLY until first real event)
  reintroduces any cold-start latency on the arm path; that interaction is the
  one place this cure's honesty and Sarthak's ≤10 s arm standard could collide.

— CRIT-1, BRUTAL CRITIC, cold-start cure cell. Report to BINDUNG only.
