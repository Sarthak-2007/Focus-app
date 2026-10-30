# CRIT-2 — Truth-of-print audit of the WHOLE warrant print family (issue #7, cold-start cure cell)

**Seat:** CRIT-2, CRITIC (truth-of-print scope), BINDUNG's cold-start cure cell. Report to BINDUNG only.
**Branch judged:** `agent/issue-7-coldstart` @ `25f623d` (cure = `1b641aa` BUILD-1 self-probe +
`55bcae4` BUILD-2 cured sentence table + BUILD-3 tests), off main `4378dae`. Shield sentence judged
on `origin/agent/issue-7` (Vogt lane — ZEUGE's surface per BINDUNG's filing on #7, 2026-10-30T05:22Z;
no ZEUGE-signed filing existed on #7 at audit time — the sentence's only landed form is MONTEUR's
`ShieldBlockingStatus.kt` there, which BINDUNG's note assigns to ZEUGE to keep).
**Method:** READ-ONLY on code. Every charge carries file:line. Nothing compiled, nothing run.
Scope per brief: the cure (BUILD-1/2) is in scope; everything else is FILED for Ren, not built.
**Relation to siblings:** CRIT-1 judged the cure's internals (probe-as-proof C-1, stamp TTL C-2,
slide-gate silence C-5, unbound-row no-grace C-5/DAY-1 D-1 — I concur on those by my own read and
do not re-file them). This seat judges the *rest of the print family* — and corrects one CRIT-1
frame claim at §1.3 below.

**Headline:** The Deck's fog sentence is dead and its grace is honest. The disease — *a check the
app cannot perform printed as a resting state; confidence printed without its proof* — survives
in three places the cure never touched, ranked by how directly they hit Sarthak's actual cold
reopen: **(1) the Launcher prints SEALED / n SHUT off a database row alone, forever, with no
witness at any age; (2) the header Guard line's cold-open blank is the exact print PARK audit
P-6 already condemned, and the header has no clock, so a resting GUARD print never ages on its
own; (3) the Shield sentence on Vogt's lane prints a proven-negative headline (`NOT BLOCKING —`)
over an unproven state — the mirror image of the fog the cure just deleted, and it will merge
un-cured.** The seam marks, the Park board and the desk clock are acquitted with notes (§4–§6).

---

## §0 — The cold-reopen timeline the verdicts are judged against

His reopen (cold process, the wound of Oct 29 22:06/22:12 IST — "CAN'T CHECK BLOCKING on EVERY
app reopen"):

| t | Deck sentence | Deck BLOCK cell | Header Guard line | Seam |
|---|---|---|---|---|
| 0 (process start) | silent (`deckWarrant` null → `refusalSentenceFor(null)=null`, `MonolithComponentLogic.kt:243`) | `UNKNOWN` (`TactileDeckScreen.kt:378–382` via `printState(null)`) | **blank** (snapshot `EMPTY`, all UNMEASURED → `guardLineFor` null, `GuardLine.kt:44–45`) | not rendered anywhere (§4) |
| Deck entry | residency + sweep + probe fire (`MainActivity.kt:203–204`, `WatchStore.kt:270–294`) | — | — | — |
| 0–4 s, bound+unstamped | **silent by grace** (`MonolithComponentLogic.kt:260–262`) — honest, bounded | `UNKNOWN` | blank until *all six* sweep queries land | — |
| >4 s, still unstamped | `BLOCKING IS OFF — TAP TO FIX` (`:264`) — the 5 s Deck ticker (`TactileDeckScreen.kt:369–376`) lands this ≤5 s after grace expiry with no new read needed | `UNKNOWN`→ per print state | `GUARD n/6` once measured | — |
| session running (relaunch into a live block) | slot suppressed entirely (`TactileDeckScreen.kt:1175`: `isSessionRunning -> null`) | running form | live or seal-frozen (§2.3) | — |

The cure's own standard, which I apply to every surface below: a transient may be **silent only
if it is bounded and the machine is verifiably working it** (probe at 0/+1500 ms/rebind, 4 s
grace, 5 s ticker). A print may rest only on a proof that is **current at print time or carries
its age/tense on its face** (the family's own laws: `PROOF: STALE`, `· AS SEALED`, `NAME STALE`).

---

## §1 — RANK 1 · LauncherBlockScreen: `SEALED` + `n SHUT` printed off the session row alone — RESTING-FOG

**Print:** `SEALED` (`LauncherBlockScreen.kt:293`) and `"$shutCount SHUT"` (`:379`, only when
`shutCount > 0`), plus `ENDS HH:mm` (`:371`) and the dial's remaining time (`:333–337`).

**Derivation:** there is none from the wire. The host gates the surface on
`sealed = activeSession?.isActive == true` (`MainActivity.kt:283`, passed at `:979–985`) — a
persisted database flag. `shutCount` is `partition.shut.size`, a count of app rows whose
*stored tier* is LOCKED (`launcherPartition` `LauncherBlockScreen.kt:102–110`, computed at `:201`).
No warrant, no `shieldProtecting`, no accessibility-armed id, no heartbeat age is read anywhere
in the file (grep: zero warrant/shield references). CRIT-1's A-6 acquitted this file because it
"carries no warrant print" — that is precisely the defect: `SHUT` **is** a wire-truth word. It is
the Shield tally's enforcement noun, printed in the present tense, under a `SEALED` heading, on
the surface that *is* the phone's home during a block (`LauncherBlockScreen.kt:82–88`).

**Cold-open behaviour — the direct hit:** process death + relaunch into a live session (his
normal mid-block reopen: kill the app / phone reboots / OEM reaps the process, then he opens
Monolith or an intercept raises the Airlock doors to this launcher). The session row survives in
Room, so from the **first frame** the launcher prints `SEALED` and `n SHUT` — while in the new
process the accessibility service may not have re-bound, the shield service may not have
restarted, and *nothing has attested anything*. The Deck in the same process at the same moment
honestly prints `UNKNOWN` and keeps its sentence silent-or-graced; the launcher has no grace,
no stale law, and **no mechanism that ever re-derives the print** — it will say `SHUT` at
t+1 hour exactly as at t+0, on the row's say-so alone. This is the pre-cure Deck disease in its
purest surviving form: a check the app cannot perform (is anything actually being intercepted
right now?) printed as a resting state.

Secondary, same surface: `ENDS` / remaining are derived at composition from
`session.spanRemainingSeconds` (`:215–219`), itself derived from the *stored* `elapsedSeconds`
column (`MonolithModels.kt:545–559`). After a long process death the stored elapsed is frozen
at death-time until the shield loop's next verified tick rewrites it — so the dial can open
overstating the remaining time by the whole dead interval. Bounded (self-heals on the first
shield tick) and not a wire claim, so filed as a note, not part of the charge.

**Verdict: RESTING-FOG — CHARGE L-1 (HIGH, filed for Ren).** Cure shape, priced for the owner:
gate `SHUT` (and arguably `SEALED`'s present tense) on the same witnesses the Shield sentence
requires (§3), or print the tier count as a ruling, not a state — e.g. `n RULED SHUT` — which is
true from the rows alone at any age. The first is a derivation change (~30 lines + tests, owner:
launcher lane); the second is a one-word print change. Silence-until-proven is also available:
the Deck already holds this address to that law.

## §2 — RANK 2 · GuardLine + header band: the condemned blank, and a print with no clock

### §2.1 The cold-open blank violates the family's own standing cure — P-6, unlanded here

**Print:** none. On cold open the header band's right side is empty
(`MonolithCustomComponents.kt:296–310`: the Guard text composes only `if (guardLine != null)`).

**Derivation:** `guardLineFor` returns null when the snapshot is null **or any of the six items
is UNMEASURED** (`GuardLine.kt:42–45`). In a fresh process the snapshot is `WatchSnapshot.EMPTY`
until the Deck-entry sweep (`MainActivity.kt:204`) lands all six queries.

**The law it breaks:** PARK audit P-6 — *"THE COLD-OPEN BLANK READS AS FINE… CURE: the line
prints `GUARD — NOT CHECKED` until the set is measured. Never blank"* (`studio/plan/AUDIT-park.md`,
P-6; merge list item 5). That print exists only on `agent/issue-9` (per
`studio/plan/ISSUE-58-WAVE3-REPORT.md`: "#9's GuardLine change is only the UNMEASURED print
`GUARD — NOT CHECKED`"). **On main and on this branch it was never landed.** The cure's silence
doctrine does not acquit this: the Deck's grace-silence is *bounded* (4 s, probe running, ticker
armed). The Guard blank is **unbounded** in two ways:

- **(a) A thrown sweep query makes it permanent.** `runSweep` writes *no stamp* when a query
  throws (`WatchStore.kt:382–390`, deliberate since issue #76) — correct for the item, but it
  means one permanently-throwing query (e.g. a platform call an OEM breaks) leaves the header
  blank **forever**, re-armed only by Deck entry / resume / FIX return (`MonolithViewModel.kt:709–714`).
  Blank-forever is the fog's resting state with the words removed.
- **(b) Cold-open Park vs Deck asymmetry.** The Deck entry sweep is also Park's only pre-fill:
  open Park inside the first sweep and the board prints names-only (honest, §5) while the header
  above it prints *nothing at all* — the summary line is the one surface in the family with no
  unmeasured print, exactly the inversion P-6 named ("the board's name-only unmeasured print is
  acquitted — it is the summary line that lied by silence").

**Verdict: RESTING-FOG (by silence) — CHARGE G-1 (MEDIUM-HIGH, filed for Ren).** Land P-6's
`GUARD — NOT CHECKED` on this line (the #9 hunk rebases onto the null path only — the #58 report
already confirmed no overlap with the fraction path), or adopt a *bounded* silence with a stated
bound. The current form is neither.

### §2.2 The header Guard print has no clock — it cannot go STALE on its own

**Derivation:** `guardLinePrint` is a `combine(snapshots, sealFreeze, activeSession)`
(`MonolithViewModel.kt:782–795`). It re-derives **only when one of those flows emits**. There is
no ticker input — unlike the Deck (5 s, `TactileDeckScreen.kt:369–376`) and Park (5 s,
`ParkDrawer.kt:262–270`), which both re-derive against a ticking `nowMono` precisely so a print
can age in front of him.

**Cold-open / resting behaviour:** at idle rest (no session, no shield loop publishing), the
last computed print rests indefinitely. Measurements cross the 45 s staleness line
(`WatchStore.kt:177`, `permPrintAt` `:496–505`) with no emission, and the header keeps printing
`GUARD 6/6` in its fresh grammar — no `STALE` suffix can ever appear until some unrelated
publish happens. Open Park at that moment and the board (ticking, same store) prints the same
items `STALE` while the header band *above the drawer* still reads `GUARD 6/6`.

**Verdict: CONTRADICTS-PARK + RESTING-FOG — CHARGE G-2 (MEDIUM, filed for Ren).** Priced fix:
feed the header derivation the same 5 s ticker the Deck already runs (~10 lines in the
ViewModel combine), or stamp the print's derivation instant and let the band degrade it.

### §2.3 The seal freeze prints wholeness with no tense marker

While a session is active, `guardLinePrint` serves the freeze captured at the seal frame for
the session's whole life (`MonolithViewModel.kt:788–790`; freeze captures at `:973` and `:1014`;
doctrine stated at `GuardLine.kt:36–38`). The imprint family solved this exact problem with a
tense suffix — `· AS SEALED` when the live wire disagrees (UNA2-04,
`MonolithComponentLogic.kt:118–135`). The frozen Guard line has **no such qualifier**: a
permission revoked mid-session leaves the header printing `GUARD 6/6` for hours, in the same
grammar as a live 6/6. One mitigation, honestly noted: on *relaunch* into a live session the
freeze is re-captured at first emission (`MonolithViewModel.kt:960–980` region) and
`guardLineFor(EMPTY)` is null, so a cold process never inherits a dead process's frozen line —
the fog is mid-session only, not a cold-open hit. **Verdict: RESTING-FOG (mid-session) —
CHARGE G-3 (LOW-MEDIUM, filed).** Fix shape: the imprint's own — freeze keeps its words, gains
`· AT SEAL` (or the AS SEALED disagreement suffix) on its face.

## §3 — RANK 3 · The Shield sentence (ZEUGE's surface, Vogt lane): a proven-negative headline over an unproven state — CONTRADICTS-DECK

Judged on `origin/agent/issue-7:app/src/main/java/com/example/ui/ShieldBlockingStatus.kt`
(MONTEUR's build; BINDUNG's #7 filing assigns this sentence to ZEUGE — consulted here by reading
that lane's code and FORM's filed spec, the open-consultation route available to this seat).

**Print:** for an active session whose two witnesses (shield protecting-this-session,
accessibility armed-this-session) do not both attest:
`NOT BLOCKING — {LABEL} RUNNING, ENFORCEMENT NOT PROVEN` (`ShieldBlockingStatus.kt:127–138`).

**Derivation:** pure and honest in its inputs — `isActive` alone never suffices (`:21–40`,
`:116–126`), a stale shield beat prints `NOT BLOCKING — SHIELD PROOF STALE` (`:145–151`), and
`BLOCKING NOW` requires both witnesses + a fresh beat (`:153–164`). FORM's original spec had
the third state as its own headline — the Deck's `CAN'T CHECK` verbatim (filed on #7,
2026-10-29T22:14Z). The build folded that state into the **negative's** headline.

**Cold-open behaviour:** relaunch into a live session in a fresh process — the witness ids are
process memory (`shieldProtectingSessionId`, `accessibilityArmedSessionId` start unattested),
so for the whole service-rebind window Shield's headline word is `NOT BLOCKING`: a *proven
negative's grammar* resting on a state its own enum names `UNPROVEN` (`:49–50`). The qualifier
carries the truth; the headline carries the verdict, and the headline is what a tired man
reads. Compare the cured Deck at the same instant: sentence silent (session running) or graced —
the Deck now *refuses* to rest any verdict on an unproven wire, in either direction. Past grace
the Deck asserts `BLOCKING IS OFF` from an *observed* bound-but-blind state (its overreach is
CRIT-1's C-3, not re-filed); Shield asserts `NOT BLOCKING` from *no observation at all* — the
first seconds of every cold reopen into a block. The two surfaces also disagree in tense about
the same wire in the no-session cold open's mirror case: none — there they agree
(`NOT BLOCKING — NO SESSION RUNNING` is a proven negative: no row, nothing enforces, `:116–121`).

**Verdict: CONTRADICTS-DECK (grammar/tense) — CHARGE S-1 (MEDIUM, filed for Ren + ZEUGE).**
Not built here: this branch does not contain the file, and BINDUNG's ownership note reserves it.
Fix shape for its owner: the UNPROVEN state's headline must not borrow the negative's word —
the qualifier's own grammar promoted (`ENFORCEMENT NOT PROVEN — {LABEL} RUNNING`), mirroring how
the cured Deck lets silence, not a verdict, hold the unproven address. **Consult question to
ZEUGE, open route, on #7 with this filing:** does the Shield lane accept the cured Deck's law —
no resting verdict, positive or negative, on an unproven wire — as binding on its headline?

## §4 — Seam marks (`seamMarksFor`): TRUTHFUL per row; prints nowhere; two filed notes

**Every combination, judged against `deriveWarrant`'s reachable states**
(`WatchStore.kt:426–455`: `!bound→OFF`; `DENIED→OFF`; `!stamped→UNPROVEN`; `UNSET→UNPROVEN`;
else `LIVE`) and Rosa's grade definitions (`MonolithComponentLogic.kt:20–49`):

| State / legs | Left | Right | Verdict |
|---|---|---|---|
| LIVE | `BIND HELD · THIS VISIT` (`:65`) | `PROOF: LIVE BIND` | TRUTHFUL — all three legs proven by derivation |
| UNPROVEN, bound, stamped (usage UNSET) | `BIND HELD · THIS VISIT` (`:75`) | `PROOF: BIND ONLY` | TRUTHFUL — bind+stamp proven, grant is the unproven leg, and "BIND ONLY" says exactly that |
| UNPROVEN, bound, unstamped — **the cold-start state** | `BIND HELD · UNSEEN` (`:77`) | `PROOF: BIND ONLY` | TRUTHFUL — both marks are present-tense *leg* facts (bind held: live system read; unseen: no stamp yet); neither claims blocking or pipeline |
| UNPROVEN, unbound | `BIND UNCONFIRMED` (`:73`) | `PROOF: NONE` | TRUTHFUL, production-unreachable by design (`deriveWarrant` maps unbound→OFF); totality row, correctly never prints BIND HELD |
| OFF, unbound | `BIND NOT HELD` (`:80`) | `PROOF: POSITIVE` | TRUTHFUL — observed dead signal |
| OFF, bound, stamped (usage DENIED) | `BIND HELD · THIS VISIT` (`:81`) | `PROOF: POSITIVE` | TRUTHFUL — the positive rests on the live usage read |
| OFF, bound, unstamped (usage DENIED) | `BIND HELD · UNSEEN` (`:82`) | `PROOF: POSITIVE` | TRUTHFUL — same read, stamp irrelevant to it |
| any state, read >45 s (timed overload) | leg facts retained (`:99–102`) | `PROOF: STALE` | TRUTHFUL as a pair — the right mark carries the age the left omits (UNA2-03 doctrine, `:88–94`) |

**Does the seam contradict the cured sentence in any state?** At the *function* level, one
pairing reads as a contradiction to a lay eye — past grace, sentence `BLOCKING IS OFF`
(`:264`) beside seam `BIND HELD · UNSEEN / PROOF: BIND ONLY` (CRIT-1's C-3). On the facts they
reconcile (bind ≠ blocking; the seam never claims the pipeline), and during grace the seam
needs **no** grace of its own: its marks are proven leg facts at read time, not a pipeline
verdict, so printing them inside the 4 s window would not recreate the fog. **But the frame
CRIT-1 charged does not exist in production:** the Deck renders **no seam at all** —
`stampLeft = null, stampRight = null` at `TactileDeckScreen.kt:998–1005` (issue #6 F8: the
stamps left the Deck; their "receiving home is PARK (other cell, at merge)"), and Park never
received them (grep: no BIND/PROOF print anywhere in `ParkDrawer.kt`). `seamMarksFor` today has
**zero production callers** — tests only. So: no screen today prints OFF beside UNSEEN; the
contradiction is latent in the functions, armed only if the Park merge lands the seam as-is
beside the cured sentence. **Verdict: TRUTHFUL (latent tension, not a live contradiction) —
filed as NOTE M-1** for whoever lands the Park seam: print the pair and the sentence under one
tense law, or the C-3 frame becomes real.

**NOTE M-2 — overload asymmetry (LOW).** The cure made `refusalSentenceFor`'s single-arg form
delegate to the timed one so "no caller can resurrect the fog" (`:230–231`). `seamMarksFor` got
no such treatment: its single-arg form (`:62`) is the natural call and silently drops the STALE
protection — a future caller using it re-opens UNA2-03 on this surface. One-line delegation,
same pattern, filed.

**NOTE M-3 — stale-overload left-mark divergence (LOW).** For an unbound UNPROVEN warrant the
fresh overload prints `BIND UNCONFIRMED` (`:73`); the stale overload's left derivation prints
`BIND NOT HELD` for the same warrant (`:99–102`). Same fact, two lefts, chosen by age — and
NOT HELD is the OFF row's stronger word. Harmless today (unreachable + unrendered), filed so
the Park-seam owner meets it awake.

## §5 — Park board: TRUTHFUL on cold open (one date note)

**Cold-open behaviour, Deck vs Park:** Park opens only from the Deck (Guard-line tap /
`MainActivity.kt:353` region), i.e. *after* the Deck-entry sweep has fired — so Park's cold
timeline starts one sweep ahead of the Deck's. What it prints is honest at every instant:

- Permission rows derive per-item at render from `permPrintAt` with a **5 s re-derivation
  ticker** while open (`ParkDrawer.kt:262–270`): `GRANTED` / `MISSING` / `STALE`
  (MISSING_STALE prints `STALE`, `:538–552`) / UNMEASURED → **name only, no word** — the
  acquitted form (P-6 acquits the board explicitly). A never-measured snapshot prints the
  sled rows' `SETTING` word — a persisted fact labelled as one (`:626–680` region).
- Interceptor rows print the derived state word or nothing (`:556–585` region); the derivation
  re-runs at every publish against the parents' *prints* (`WatchStore.kt:619–640`), so a word
  cannot outlive its parents' 45 s freshness even though the derived measurement's own stamp
  refreshes per publish.
- STALE is actionable, not a dead end: RE-CHECK key (`ParkDrawer.kt`, §3b) re-fires the sweep
  in place; FIX targets stale denials as denials (`:271–285`).

**Verdict: TRUTHFUL.** It does not rest unproven the way the Deck did: it has an unmeasured
print (silence *per row*, beside the item's name — not a summary claiming wholeness), a clock,
and a remedy on the board.

**NOTE P-1 (LOW):** `LAST PROVED HH:mm · SELF-REPORT` (`ParkDrawer.kt:514–523`) is restored at
cold open from the persisted wall instant (`WatchStore.kt:205–228`) before any sweep of this
process — truthful as history and honestly labelled SELF-REPORT, but it prints a bare `HH:mm`
with **no date**: yesterday's 21:40 proof reads, on this morning's cold open, like today's.
The digest sheet's frozen rows solved this with a date-aware label (`captureTimeLabel`,
`AirlockAndDisarmOverlays.kt`). Filed, one-line class.

## §6 — Desk clock / AOD: no wire-truth prints — TRUTHFUL (out of family)

`AlwaysOnDeskClockOverlay` (`AirlockAndDisarmOverlays.kt:78–231`) prints exactly three things:
a time derived from the session's span numbers or the dial (`:113–123`), a progress fraction
of the same (`:131–140`), and a banner that is a real task title or a declared intention or
**absent** (`:204`, `deskClockBannerText` `:233–234` — the fabricated "Deep Focus Session"
default is dead and documented dead). No SEALED / HELD / SHUT / blocking claim exists on the
surface; there is no wire for it to lie about. The Airlock's own session line is likewise
honest (`airlockSessionLine`, `:276–280`: an end from the session's numbers, "NO SET END" for
a stopwatch, no line without a session) — and the Airlock only ever stands because an intercept
fired, which is the pipeline proving itself in the act. **Verdict: TRUTHFUL; no charge.** (The
stopwatch progress denominator `1200f` at `:138` is a bar scale, not a truth claim — noted,
not charged.)

---

## Charge table (all filed for Ren; nothing built by this seat)

| # | Surface | Verdict | Directness on his cold reopen |
|---|---|---|---|
| L-1 | Launcher `SEALED` / `n SHUT` | **RESTING-FOG (HIGH)** | Direct: first frame of every relaunch into a live block, no witness ever |
| G-1 | Header Guard blank vs P-6 `GUARD — NOT CHECKED` | **RESTING-FOG by silence (MED-HIGH)** | Direct: every cold open, unbounded if a sweep query throws |
| G-2 | Header Guard print has no ticker | **CONTRADICTS-PARK / RESTING-FOG (MED)** | Resting: any idle period >45 s, header vs Park disagree |
| S-1 | Shield UNPROVEN headline `NOT BLOCKING —` (Vogt lane) | **CONTRADICTS-DECK grammar (MED)** | Direct on merge: first seconds of every relaunch into a block |
| G-3 | Seal-frozen Guard line, no tense marker | **RESTING-FOG, mid-session (LOW-MED)** | Indirect: post-seal revocation |
| M-1/M-2/M-3 | Seam: latent C-3 frame if Park lands it; single-arg STALE hole; stale left divergence | TRUTHFUL today, notes filed | None today — prints nowhere |
| P-1 | Park `LAST PROVED` HH:mm without date | TRUTHFUL, note filed | Cosmetic on cold open |
| — | Seam rows (all), Park board, desk clock/AOD, Airlock session line | **TRUTHFUL** | — |

**Bottom line for BINDUNG:** the cure is truthful where it operated. The family's remaining fog
lives where the cure didn't: a launcher that certifies from a row, a header summary whose
unmeasured print was prescribed two audits ago and never landed on main, and a Shield headline
on the neighbouring lane that spends a proven negative on an unproven state. L-1 and G-1 are
the two that meet his hand on an actual cold reopen.

## Efficiency account (CRIT-2)

- **Artefacts:** this file (committed alone) + the signed summary/consult comment on issue #7.
  Read-only on code honoured; no other file touched. No crit-2.md stub from the dead first
  seat existed (checked the branch at start; the cell dir itself was created by siblings).
- **Read in full:** SARTHAK-WORDS.md; BUILD-1/BUILD-2 diffs; `MonolithComponentLogic.kt`
  (whole); `GuardLine.kt` (whole); `WatchStore.kt` residency/derivation/sweep sections;
  `ParkDrawer.kt` board + keys; `LauncherBlockScreen.kt` (whole); AOD overlay section of
  `AirlockAndDisarmOverlays.kt`; `computeShieldBlockingStatus` + FORM's spec on the Vogt lane;
  PARK audit P-6 + #58 wave-3 report; CRIT-1/DAY-1 filings (to concur-or-correct, not duplicate);
  issue #7 comments incl. BINDUNG's ownership filing.
- **Idling/waste, admitted:** `gh` reads on this box take 45–90 s; two ran in background and
  none blocked evidence (all charges rest on local git/file reads). The branch moved three
  times under me (BUILD-3, sibling filings) — one re-verification pass of line numbers against
  `25f623d` was the cost. No compiles, no spawns.
- **What I would do next, unassigned:** re-judge L-1's fix when the launcher lane prices it —
  specifically whether witness-gating `SHUT` reintroduces a blank-launcher window at relaunch
  that the ruling-word form (`n RULED SHUT`) avoids for free.

— CRIT-2, CRITIC (truth-of-print), cold-start cure cell. Report to BINDUNG only.
