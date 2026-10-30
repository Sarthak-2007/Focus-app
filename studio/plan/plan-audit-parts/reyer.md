# Reyer — Critics, sound — PLAN AUDIT

*Target: §9 charge B-54, the wider sound-truth audit of the merged plan,
and the Bit-Identical Seal's government of Slice E. Evidence standard:
every charge files with file + line, or does not file. No code, no
builds, no plan edits — this file only.*

*Sources read whole or in the cited parts: `plan/MASTER-PLAN.md`
(§§2–9), `plan/SLICE-D-E-BUILD-READY.md`, `plan/maximpl-parts/10-freya-session.md`,
`plan/maximpl-parts/02-ines-arming.md`, `plan/seat-drafts/s5-ton.md`,
`invention/critics-parts/reyer.md` (my own seat file), Ren's B-5 letters
(`mail/marlowe/read/0858__from-ren__bit-identical-seal.md`,
`mail/marlowe/read/0905__from-ren__slice-de-fork-rulings.md`,
`mail/sana/0858__from-ren__merge-ratifications.md`), and Moss's letter
in my inbox (`mail/reyer/read/0547__from-moss__doubled-sounds.md`).*

---

## 1. B-54 — THE NATURAL-COMPLETION SOUND

**VERDICT: CLOSED — with evidence. The merged plan's own §5 text
implements Freya's allocation. No chair ruling is needed; what remains
is one stale note (§5.3.8) that contradicts the sections around it —
filed below as charge S4, a paperwork charge, not a reopening.**

### The two filed positions

**Freya** (`plan/maximpl-parts/10-freya-session.md:1024-1035`): natural
completion sounds **exactly one `playCompletionPulse()`** (185Hz, 64ms,
gain 0.68); the ticker path today plays `playHarmonicSeal(arming =
false)` — *"the same sound as a valve break. Completion and emergency
must never share an exit form, sound included: seal(false) becomes the
break's sound alone (§3.4), the pulse becomes completion's alone."*
Freya herself marked it UNCONVERGED, for one stated reason only:
*"Ines's file bans the pulse from the valve view only and doesn't rule
the ticker path."*

**Ines** (`plan/maximpl-parts/02-ines-arming.md:669-672`): *"Completion:
the ViewModel's existing `playHarmonicSeal(arming = false)` (480Hz,
48ms, gain 0.62) is the single completion sound… `playCompletionPulse`
never plays in this view."* Read in its own scope, this paragraph sits
inside the **valve** spec: its state table's completion row is
`Completion (total = 60)` (`02-ines-arming.md:656`) — the valve's 60
held seconds, i.e. the break. Her invariants confirm the scope:
*"`playHarmonicSeal(false)` belongs to the completed break
(ViewModel). No other element on this surface fires either"*
(`02-ines-arming.md:773-774`). Ines never assigns seal(false) to
*natural session* completion; her "completion" is the valve's, and her
pulse ban is valve-view-scoped — exactly as Freya read her.

**Ton's carry** (`plan/seat-drafts/s5-ton.md:737-745`, U4) frames the
conflict conditionally: *"**if** natural completion and a valve break
both sound `playHarmonicSeal(arming = false)`, break and completion
share a voice…"* The merged plan makes the antecedent false.

### What the merged §5 actually implements — Freya's form, four times

1. **The break gets the seal, alone, post-fact.** §5.3.5
   (`MASTER-PLAN.md:1869-1874`): *"Sound: harmonic seal (disarm
   variant), 48ms, 480Hz, gain 0.62 — fired once, by the ViewModel,
   only on `Completed`, after the transaction commits (§5.3.8). The
   valve view plays nothing at completion…"* And `:1883-1884`:
   *"no stamp, no strum, **no completion pulse**, no receipt shares
   this frame."*
2. **Natural completion gets the pulse, alone.** §5.7 FINISH
   (`MASTER-PLAN.md:2332-2334`): after the Held Frame's 400ms of true
   silence, *"then the **completion pulse alone** — reserved strictly
   for verified fully-elapsed sessions — against an unmoving picture."*
   The composite is keyed to the persisted `RUNNING → COMPLETED`
   transition, *"written before any animation… the completion happens
   once, ever"* (`:2325-2327`) — the pulse fires on a fact already
   booked, never on a ticker's guess.
3. **The acceptance check repeats the allocation as test law.** §5.9-9
   (`MASTER-PLAN.md:2737-2741`): *"FINISH = full arc + HELD FRAME
   1200ms + pulse (clean seals only)… BROKE = partial arc, 0ms splice,
   **no pulse**…"*
4. **§9's own closure register already states it.** A-21
   (`MASTER-PLAN.md:5022`): *"The completion pulse belongs to verified
   fully-elapsed sessions and to the Deck — one completion, one
   voice"* (level PLAN). A closure and an open charge for the same
   question cannot both stand; the closure is the one the body text
   supports.

The pulse's voice is defined once, at its Deck call site
(`MASTER-PLAN.md:1494-1499`: `playCompletionPulse()`, waveform
`[0,10,24,10,24,14]`ms, sound 185Hz / 64ms / gain 0.68 — Freya's
numbers verbatim), and §5.7 spends that same primitive. One voice,
two completion occasions (task DONE on the Deck tear, session FINISH),
never an emergency occasion. That is Freya's law — *"never share an
exit form"* governs completion-vs-emergency, and both pulse occasions
are completions.

**Cost of the road not taken, for the record:** had the chair instead
ruled Ines's ticker sentence into a natural-completion law (seal(false)
for natural completion too), break and natural completion would share
one voice and §5.3.5, §5.7, §5.9-9 and A-21 would all need rewriting —
four merged passages traded for one sentence in a valve spec that, on
its own lines, was never about the ticker. The evidence does not
support paying it.

**Residue (not a reopening):** §5.3.8's carried note
(`MASTER-PLAN.md:2108-2113`) still prints the question as open —
*"the distinctness claim cannot be finally true until Voss/Ines/Freya
close it (§9, charge B-54)"* — directly against §5.7/§5.9/A-21 in the
same merged file. Strike the note; B-54 closes with it. See S4.

---

## 2. SOUND-TRUTH CHARGES

*The Yuki sled lesson, as the audit's rule: a sound fired before the
mechanism it reports has decided is a lie in the hand; two sounds for
one event is a flam, and a flam is a bug. Severity: S1 = a builder
following the text builds a lying or dead-ruled sound; S2 = a sound
is authorised with no truth condition, so its firing truth is
invented at build time; S3 = stale or missing paperwork that will
misroute a builder or a future audit.*

### S1 — The Laid Seal still BUILDS in the Slice D/E sheet, against the chair's B-5 ruling

Ren's ruling is unambiguous and twice delivered: *"Reyer's
BIT-IDENTICAL SEAL governs Slice E. The Laid Seal is dead as filed"*
(`mail/marlowe/read/0858__from-ren__bit-identical-seal.md:1-7`);
*"Every Laid Seal passage in the D/E file — F14's wet-send treatment
and the section 5 dependency… — is to be reconciled to the
Bit-Identical Seal's build path, with the swap noted in the file"*
(`mail/marlowe/read/0905__from-ren__slice-de-fork-rulings.md`, F14
paragraph); repeated to Sana (`mail/sana/0858__from-ren__merge-ratifications.md`,
"B-5… the BIT-IDENTICAL SEAL governs").

The file was not reconciled. As it stands today,
`plan/SLICE-D-E-BUILD-READY.md` still:

- routes the arm seal through the dead voice at its summary level —
  §0 step 5 (`:53-54`): *"At t=0: the seal's voice fires (one call
  site, **Throw or Laid Seal by Ivo's fact predicate**)"*;
- builds the Laid Seal as a priced joint — E2 (`:414`): *"Seal
  cadence, entry 2: the Laid Seal — Ivo — **BUILDS**, 5 amendments ·
  ≈185 net (≈110 over Throw-only)"*, with its full variant recipe
  (`:415-427`: gain 0.124, 12ms raised-cosine attack, LP 2.4kHz,
  amended haptic waveform) and its provenance fact rule (`:432-443`:
  primed snapshot at DOWN, DAO EXISTS query on Migration 8→9 columns,
  *"manual arms always Throw"*) — i.e. the seal's timbre is a function
  of how the session was reached, the exact provenance-rendering the
  Sound-Neutrality Law kills (`invention/critics-parts/reyer.md:45-59`:
  SNL-1, `seal(proofGrade, outputCeiling)` as the only entry point,
  the Laid parameter set *"deleted entire"*, acceptance = bit-identical
  PCM + blind ABX at chance, `:60-64`);
- carries the dead voice's fork — F14 (`SLICE-D-E-BUILD-READY.md:728-732`):
  *"the Laid Seal's −26dB wet send"*, recommended as a field-only
  no-op that *"goes live when both land."*

No swap is noted anywhere in the file (grep: the file contains no
"Bit-Identical"). A builder landing Slice E from this sheet builds
the Laid Seal, spends ≈110 net lines over Throw-only plus the 8→9
dependency, and ships a seal that tells him — in tone — whether his
rested self or his hand armed the session. **Fix owed (not mine to
make): E2 struck, §0 step 5 reduced to the Throw alone, F14 struck,
the swap noted in the file, per Ren's letter.** MASTER-PLAN itself is
clean here — see Acquittal A8.

Companion staleness: MASTER §9 still lists B-5 as OPEN
(`MASTER-PLAN.md:5053-5056`: *"closes when: a chair ruling names which
seal law governs"*) — the ruling exists, in the mailroom, and the
register does not know it. Same pattern at B-12 (see S4).

### S2 — §2.6's seal gate is the dead Canary machinery; the plan carries two different lawful preconditions for the same seal

Merged MASTER §2.6, headed *"(BEYOND W4, operative)"*
(`MASTER-PLAN.md:369`), still gates the seal on the old evidence
machine: *"**The seal may fire only on a press-poll BLOCK pass
returned inside this press.** A standing pass, a stale pass, a cached
pass seal nothing"* (`:462-464`), followed by the Canary itself
(`:464-470`: sacrificial target, 300ms intercept, 800ms UNKNOWN inside
the 1500ms deadline).

§9's own corrections ledger kills that machine in the same file:
C-1 (`MASTER-PLAN.md:4806-4821`) — every element *"superseded by
Ines's Live Wire… register-stamps replaced by volatile reads
synchronously inside the gesture (DOWN + the 0.68 gate, ≤5ms, no
deadline)… nothing is `UNKNOWN` outside the one narrow fresh-enable
room (§8.7)"*; B-2 (`:5040-5043` family) records filed §2.6 as still
speccing the dead machinery, HELD. And §5 imports the dead gate by
reference: *"Session t=0 is §2.6's PROOF-SEAL FRAME; the seal's voice
and shot (cadence 210ms) are specced there"* (`:1705-1707`).

The sound consequence, which is my lane: **the precondition for the
centre gesture's voice differs by which section a builder reads.**
§2.6: seal lawful only on a press-poll pass inside a canary regime.
§8/Live Wire + Slice E E1: seal lawful at one call site,
`playSealThrow()` in the PROVING→SEALED-RUNNING transition, *after
the session insert returns* (`SLICE-D-E-BUILD-READY.md:405-411`).
These are not two wordings of one gate; the canary gate can refuse
(or UNKNOWN-downgrade) arms the wire gate seals, and vice versa.
§2.6's own truth sentence is the right law and survives either gate —
*"the seal sounds once, only when the session row exists —
celebration never precedes the fact"* (`MASTER-PLAN.md:506-507`) —
but the gate that enforces it must be one. **Closes when §2.6 is
struck and refiled on the §8.3 engine (B-2's own closing evidence),
with the seal's precondition restated once, in wire terms.**

Note what this charge is *not*: the old **double** pre-session seal
(view on release + VM pre-launch) does **not** appear in the merged
MASTER text — see Acquittal A7. It lives in the shipped code, where
Slice E E1 names and kills both sites. The merged plan's fault is a
forked gate, not a doubled voice.

### S3 — The airlock Catch + peek expiry are authorised in-session sounds with no spec anywhere

§5.3.8's silence law (`MASTER-PLAN.md:2096-2098`): *"inside a session
the only sounds are the valve's own events (§5.3.4–5.3.5), **the
airlock Catch + peek expiry**, and the finish."* That sentence is the
airlock's entire sound spec in the merged plan. Grep of
`MASTER-PLAN.md` for "airlock" returns three hits: this licence, a
list of things *not* drawn on the running screen (`:2245`), and a
board-line remark in §8 (`:4190`). No voice, no owner ([V]/[VM]), no
trigger proof is specced for either sound: the Catch on what evidence
(the app actually caught and shut? the peek pass granted?), the
expiry on whose clock and on what confirmation (timer elapsed, or
the app verified re-shut?). Those are different truths — a Catch that
fires on pass-grant claims a shut that hasn't happened; an expiry
that fires on the timer claims a re-seal nobody checked. Under the
Yuki rule, an authorised sound with no firing condition is a lie
waiting for a builder to invent its timing. **Closes when: one
passage specs both sounds with voice, owner, and the verified event
each reports — or the licence sentence is struck and the airlock is
silent in-session.**

### S4 — Register staleness around sounds already ruled (three S3 items, one pattern)

1. **§5.3.8's open note vs §5.7/§5.9/A-21** — B-54's residue, §1 above
   (`MASTER-PLAN.md:2108-2113` against `:2332-2339`, `:2737-2741`,
   `:5022`).
2. **B-12's "pending ratification" is spent.** §5.7 prints the death
   ruling *"pending Ren's ratification"* (`:2336-2337`) and §5.9-8
   repeats it (`:2735-2736`); Ren ratified it —
   `mail/sana/0858__from-ren__merge-ratifications.md` item 5: *"No
   completion pulse for death-booked sessions RATIFIED (B-12)."* The
   ruling is correct everywhere (see the verification table, §3);
   only its status word is stale.
3. **A-21's evidence pointer is rotten.** A-21 (`MASTER-PLAN.md:5022`)
   cites *"§4.11; §5.7 as merged"* for the pulse law. Merged §4.11
   (`:1571-1585`) is Zeile's OPEN queue re-planner — it contains no
   pulse text at all. The Deck pulse law A-21 means is §4.8's DONE
   boundary (`:1494-1506`). A merge audit following the citation
   (B-1's walk) will find nothing and may re-open a closed question.
   Belongs to B-1's family; filed here because the rotted pointer
   guards a sound law.

### S5 — Two smaller gaps (S3)

- **PARK's entry tick has no voice.** §6.7 (`MASTER-PLAN.md:3175-3177`):
  *"a detent tick at zone entry pitched below the stop family — it is
  not a stop and must not sound like one (the exact pitch is Ton's to
  assign; Mikro's flag to him stands open)."* Honestly flagged, not
  hidden — but as filed, a sound is specced to fire with its defining
  property (not-a-stop pitch) unassigned. A builder must invent the
  one number the sentence exists to constrain. Closes when Ton
  assigns the pitch or the tick is struck.
- **Moss's wheel/hub doubles are unclosed in the merged Deck text.**
  Moss's letter (`mail/reyer/read/0547__from-moss__doubled-sounds.md`)
  charged three doubles in the shipped feel path: seal ×2, wheel
  detent ×2 (view `fireFeel` + VM `setDialMinutes` at a second
  pitch), hub (view latch + VM detent stacked). The merged plan
  closes the seal (Slice E E1) and states sole-owner law explicitly
  for the sled (§3), the valve (§5.3.5), submit and DONE (§4) — but
  merged §2.4's whole sound content for the wheel is *"Detent feels
  at 5/15/30 per Ton's grades"* (`MASTER-PLAN.md:268`) plus a seat
  knock that *"scales"* (`:282-284` family), with no owner, no sole
  call site, and no sound check in §2.7's ten acceptance items. The
  hub in §2.4 is specced with no sound at all. Silence about the hub
  is safe; silence about the wheel's owner is how the detent double
  survives a rebuild. Closes when §2 names the wheel detent's sole
  owner, as §3/§4/§5 name theirs.

---

## 3. DEATH-BOOKED SESSIONS — THE NO-PULSE RULING, VERIFIED EVERYWHERE

The ratified ruling (merge ruling in §5.7; ratified by Ren,
`mail/sana/0858…` item 5): a session booked BROKEN gets **no
completion pulse**, in any channel. Checked at every passage where a
pulse, a finish form, or a receipt could leak one:

| Passage | Text | Verdict |
|---|---|---|
| §5.6 death law | `MASTER-PLAN.md:2297-2305` — at the death: *"no knock, no word, no screen change, **no sound, no AOD change**"*; truth told once on the finish receipt; *"the record books the session as BROKEN, never as a clean seal"*; token unspent | HONOURS |
| §5.7 FINISH | `:2332-2339` — pulse *"reserved strictly for verified fully-elapsed sessions"*; death-booked session that elapsed fully: *"**no pulse.** The pulse is the ear's word for 'finished clean'; a session booked BROKEN is not dressed as completion in any channel"* | HONOURS |
| §5.7 BROKE (valve) | `:2355-2357` — *"never the completion's held frame, never a run-down, **no completion pulse**"* | HONOURS |
| §5.7 receipt | `:2365-2395` family — the receipt carries the death line (`THE SHIELD WAS OFF FOR PART OF THAT SESSION`) and the break label as *print*; no sound of any kind is specced on the receipt, and the sequence № *"increments only at genuine completion"* (`:2743-2746`, §5.9-10) | HONOURS |
| §5.8 AOD | `:2424` *"The AOD is unconditionally silent"*; §5.8.8 finish is a tonal step only (`:2543-2561`) and a valve-ended session gets *"**no finish step** on this face"* (`:2558-2561`); §5.8.7: blocking death changes zero pixels (`:2530-2534`) | HONOURS |
| §5.9 checks | `:2731-2736` (check 8: *"**no completion pulse fires for a death-booked session**"*) and `:2737-2741` (check 9: pulse *"clean seals only"*, BROKE *"no pulse"*) | HONOURS |
| §5.8.11 Extinguished Clock | carried invention, NOT spec (`:2651` family); its completion-while-extinguished contradiction is already B-55 and blocks promotion only — it cannot leak a pulse into spec | NO LEAK |

**No passage in §§5–8 specs a completion pulse, a finish step, or a
receipt sound for a death-booked session. The ruling holds
everywhere, including AOD and the receipt. Acquitted as a whole,
with proof above** (the stale "pending ratification" wording is
S4-2, a status word, not a sound).

---

## 4. ACQUITTALS

- **A1 — §3's sled (the Yuki fix, stated as law).**
  `MASTER-PLAN.md:822-836`: the VM *"plays detent sounds BEFORE the
  ratchet decides — a refused throw clicks success"* is named a live
  truth bug; fix: *"the VM goes silent for tiers and returns Boolean
  acceptance synchronously; the view is the sole sound owner, at its
  own settle's `onAnimationEnd`"*; programmatic changes silent —
  *"twelve landing sounds for one loadout tap would be a lie about
  how many acts occurred."* Acceptance check §3.9-2 (`:942-943`):
  refused throw silent, accepted throw sounds exactly once, from the
  view, at landing. This is the pattern every other section is
  measured against. (The §3.10 bulk proposal's one-landing-feel is
  *proposal, not spec* per §9 C-5, and its partial-refusal reporting
  is specced in words — `SHUT 21 OF 23` — so it files no charge.)
- **A2 — §4's submit.** F-5 (`MASTER-PLAN.md:1461`): the Card Punch
  is *"[V] — fired **on the FiledReceipt, after the commit**, never
  on finger-up"*; §4.7 (`:1412-1416`): *"fired from inside the
  transaction's receipt, never before the write it reports. (The
  shipped double pre-commit sound… is a truth bug and dies.)"*
  §4.12-11 (`:1634-1636`) greps the composer path for pre-commit
  sound calls: none. Post-fact, single-owner, tested.
- **A3 — §4's DONE boundary.** `:1494-1506`: Deck tear fires
  `playCompletionPulse()` and nothing else; on Tasks *"the
  completion pulse NEVER fires"*; *"whichever surface's act commits
  it first owns the voice; the other renders the result silently"*;
  a second DONE is *"a null act — silent, no haptic, no write."*
  One fact, two homes, one voice — Moss's double-fire pattern,
  closed for this act.
- **A4 — §5.3.5's break seal.** `:1869-1874` ( §1 above): once, by
  the ViewModel, only on `Completed`, after the transaction commits;
  the view plays nothing; a 300ms silence moat follows. The exemplar
  of a post-fact call site in this plan.
- **A5 — §5.7's FINISH keying.** `:2325-2327`: keyed to the persisted
  transition by `sessionId`, *"written before any animation, so
  process death or reopen cannot re-fire it — the completion happens
  once, ever."* The pulse cannot precede, duplicate, or resurrect
  its fact.
- **A6 — Silence where silence is the truth.** §5.6 (death: no sound,
  `:2297-2299`); §5.3.6 ABANDONED (chip tap exits in *"silence"*,
  `:1902-1905`) and SPENT entry (*"no sound plays"*, `:1906-1910`);
  §5.3.7
  spent plate (*"zero touch acknowledgement — no sound, no haptic…,
  ever"*, `:1927-1929`); §7 Drawer (*"Sound: none. Haptics:
  none"*, `:3794-3797`); §5.5 Landing Line (*"No sound, no haptic"*,
  `:2289`); §3.7 CANCEL and returns (silent); §4 F-14 CUT
  (*"Silence. Deletion is clerical"*). Refusals and non-events do not
  speak anywhere in §§3–8. §8.9's repair click is the same law from
  the other side: *"The repaired line alone gets the firm click.
  When — and only when — the line… re-seats `GRANTED`"*; *"If he
  fixed nothing, there is no sound at all"* (`:4439-4444`).
- **A7 — No double seal in the merged MASTER text.** §2.6
  (`:506-507`): *"the seal sounds once, only when the session row
  exists"*; §2.6's t=0 (`:371-377`) attaches the seal sound to the
  PROOF-SEAL FRAME *"and no other."* The shipped double (view +
  pre-launch VM) is charged against the code by Slice E E1
  (`SLICE-D-E-BUILD-READY.md:405-411`) and killed there; the merged
  plan does not re-spec it. (The gate fork is S2, a different
  charge.)
- **A8 — MASTER-PLAN carries no Laid Seal.** Grep of
  `plan/MASTER-PLAN.md` for "Laid" / "Throw": no voice-variant text
  — §5.2 defers the seal's voice to §2.6's single cadence
  (`:1705-1707`), and §9 B-5 records the collision as open rather
  than quietly adopting either side (`:5053-5056`). The dead voice
  survives only in the Slice D/E sheet (S1) and in seat drafts
  (e.g. `plan/seat-drafts/s5-pit.md:52`, *"the Throw / the Laid
  Seal"* — a draft, not plan law). The Bit-Identical Seal's own
  government of Slice E is established by Ren's letters (S1); what
  contradicts it is the un-reconciled build sheet, not the master
  plan.
- **A9 — §6.5's sealed rail.** `:3102-3109`: every swallowed press
  returns *"exactly one dull acknowledgement — the 4L latch feel,
  throttled to once per press"* and nothing else — no navigation is
  claimed, none is sounded; §6.8-9 tests ten presses → ten latches,
  zero navigations. The feel reports the swallow, which is the act
  that happened. Truthful.

---

*Charges: S1 (Laid Seal builds against a chair ruling) · S2 (§2.6's
dead seal gate) · S3 (airlock sounds, no truth condition) · S4
(register staleness ×3) · S5 (PARK pitch; wheel sole-owner). B-54:
CLOSED on Freya's form, evidence §1. Death-booked no-pulse: verified
at every passage, §3. No code touched, no plan edited.*

*— Reyer, Critics (sound)*
