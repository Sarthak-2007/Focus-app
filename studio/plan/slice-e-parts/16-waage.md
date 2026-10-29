# 16 — WAAGE (Improvers, the scales) · SLICE E RE-WEIGH + ROLL-UP

*Seat: Waage, Improvers — the price, re-weighed. Coordinator: Ilya.
Filed LAST in this wave, as briefed: my own count first (E1's engine
share, E4's VM share, both read whole in today's trunk), then every
landed part reconciled into a per-file union.*

## METHOD (stated once, held against every seat including me)

- **Net lines are counted per FILE from the actual insertion regions
  in today's trunk** (`~/workspace/clone-inspect/Focus-app`, read this
  run) — never from SLICE-D-E-BUILD-READY.md's stale line numbers.
- Classic net = adds − deletes. Where a seat filed in Waage count
  (adds + deletes, Fenja/Taste), its classic-net figure is used and
  the conversion is shown.
- **Overlapping joints are priced once at the file level.** If two
  seats price the same region, the region enters the union once;
  the second seat's number is recorded as a cross-check, not added.
- **Tests are priced separately, always.** A filed total that folds
  tests into "production" is corrected, not repeated.
- **A joint whose region sits inside Marlowe's in-flight slide
  rewrite (`builds/slide`) is priced with a stated uncertainty
  band**, never false precision.
- Ground that moved since D/E filed: the **token merge** (VM token
  is now derived from the Room spend ledger; `completeEmergencyBreak
  FromValve()` exists), **Live Wire** (`service/LiveWire.kt`, 98
  lines, already in trunk), the **AOD span fix** (whole-span
  derivations already in `MonolithModels.kt:287ff` and the Deck),
  and **two in-flight lane rewrites** (token + slide; the slide
  lane's `BlockitCoreViews.kt` is 1,971 lines against trunk's 1,419
  and its slide region no longer contains the view's seal call).

---

## 1 — MY OWN COUNT A: E1's ENGINE SHARE
### (`service/TactileSoundEngine.kt`, read whole — 378 lines)

The transient renderer, as it stands today:

- `SynthCommand` (:58–63 in Riegel's pinning; the data class above
  the queue) carries exactly four fields: Hz, durationSamples,
  gain, chordRatio. No noise, no filter, no delay line, no kind.
- The synth loop's buffer is capped at **160 ms**: `maxSamples =
  (44100 × 0.16)` = 7,056 samples, one `FloatArray(maxSamples)`,
  and the loop clamps every command to that cap.
- `enqueueTransient` clamps harder still: duration samples are
  coerced to **96…6,000** — a 1,000 ms request is silently cut to
  ≈136 ms before it ever reaches the loop.
- The loop is **serial**: one thread polls one command, renders the
  whole buffer, then `WRITE_BLOCKING`-writes it. A 44,100-sample
  render therefore (a) occupies the only audio thread for its full
  render + write, and (b) — decisive — **renders before the first
  sample sounds**: rendered synchronously at t=0, the Throw's
  attack onset is late by the whole render time. Priming at latch
  (the 210 ms window) is not garnish; it is what makes "attack at
  t=0" true.

**What a 1,000 ms render path therefore costs, itemised:**
three caps must move in concert (enqueue 6,000, loop 7,056, buffer
allocation — change any one alone and the render truncates
silently, a lie by arithmetic); a new command kind; a fixed-seed
noise source + one-pole 900 Hz bolt at sample 1,323; two feedback
combs (1,310 / 1,372 samples, g = 0.836, loop LP 1.8 kHz) + allpass
+ wet mix + the 900→1000 ms fade. My independent recount of
Orson's branch lands at ≈98 — **Siegel's +90 (01) stands** after
the B-5 delta below, and I adopt it.

**Engine verdict:** Orson's mechanism +150 → Siegel **+138**
(B-5 kills the Entry-2 parameter generality before it is built,
−12; the saving is spent on identity tests, §3). **Riegel (02)
then replaces the queued mechanism with a dedicated seal lane**
(separate track + render thread, sequence barrier, track preempt)
at **+82 production over Orson** — and my reading of the serial
loop above is exactly his evidence: on the queued form, t=0 is a
measurement; on the lane, it is a property. The roll-up takes the
lane form as the build-ready headline and bands the alternative.

---

## 2 — MY OWN COUNT B: E4's VM SHARE
### (`ui/MonolithViewModel.kt`, read whole — 1,123 lines; ticker + collectors)

Today's finish, in the real code:

- `startTickerLoop()` is the **only driver** of session progress:
  it calls `repository.updateSessionProgress(current)` once a
  second, and the completion response lives inside that poll —
  `playHarmonicSeal(arming = false)` at today's :344, AOD hide and
  stats refresh beside it. Completion is discovered up to
  1,000 ms late and answered with the wrong voice.
- `playCompletionPulse()` is spent by `triggerRatchetWarning`
  (today's :159) — the mis-spend E4 corrects.
- The collectors: `activeSession` and `sessionHistory` are both
  `stateIn(WhileSubscribed(5000))`; `init` launches seeding +
  an `activeTask` collector, then starts the ticker.

**The trap nobody's filed price named:** `getActiveSessionFlow`
is `WHERE isActive = 1 … LIMIT 1`. On natural completion the row
flips and the flow emits **null** — the completed entity, with
`completedSuccessfully`, is never emitted on that flow. A naive
StateFlow-edge collector on `activeSession` cannot distinguish
natural completion from a disarm, a grace cancel, or an emergency
break: all four arrive as active→null. Classification must ride
the `sessionHistory` head (or the controller must own the drive
that caused the flip), and the ticker's *drive* must survive —
delete the whole branch and no session ever completes in Room.
The token merge helps here: the valve/disarm paths are now
VM-initiated through one function, so a manual-end guard flag is
cheap and concrete.

**VM share, by insertion region in today's file:**

| Region | Net |
|---|---|
| Finish types + `_finishState` fields (beside `DisarmChallengeState`) | +28 |
| E0 edge collector: history-head classification, session-id guard, manual-end guard (token-merge form) | +38 |
| Finish state machine + E-clock: RELEASING → held 1,200 ms → RELEASED / ENDED-FAULT, detention ≤ E1800, pulse re-gated to E700, AOD/stats moves | +82 |
| Ticker completion response deleted (drive call kept) | −7 |
| Ratchet's `playCompletionPulse` call deleted | −1 |
| **VM subtotal, sequencer + retry in their own files (Draht/Klinke placement)** | **≈ +140** |
| Same, if sequencer (+≈105) and retry (+≈38) land inside the VM instead | ≈ +283 |

Wren filed the VM share at ≈150 — **confirmed within counting
slack** (+140 mine, +≈10 of classification guard the token-merge
era makes necessary = his 150). Draht (05) and Klinke (03) both
place the sequencer in D8's `ArmProvingController` file, so the
+140 form is the union form. Cross-seams priced once, not twice:
`ReceiptState` publication is **E6's** VM line (Schlitten M3),
not E4's; Reihe's E0 predicate +22 (04) is this same collector
seen from the routines side — counted here, not again there;
E3's `t0Mono` plumbing (+25, Wren) shares the `init` launch
scaffolding with this collector (≈5 lines, charged once).

---

## 3 — PER-JOINT ROLL-UP

Production = classic net. Tests separate. "As filed" = SLICE-D-E
§2/§4. Bands are stated where a region sits in the slide rewrite
or behind an unruled fork.

### E1 · The Throw (Orson) — engine share mine, §1
| | |
|---|---|
| As filed (D/E §4) | ≈210 "production" |
| Correction to the filing itself | That 210 was Orson's **all-in** total: +150 engine, −2 view, +2 VM, **+60 tests**. Filed production was really **150**. |
| Re-weighed production | **226** = Siegel engine/VM/view 138 (B-5 −12) + Riegel seal-lane delta +82 + Klinke claim-block increment +6 (his +9 gross, call/prime duplicates absorbed; view death −2 counted once here, re-homed to D1 by Klinke — union unchanged) |
| Re-weighed tests | **152** = Orson base 60 + Siegel identity +12 + Riegel +35 + Klinke plumbing +45 |
| Band | If Ren ships Siegel's queued-prime form instead of Riegel's lane: production **144**. Routine-arm convergence (Siegel fork F-S1) is **not** in this number: Siegel prices a minimal convergence at +18; Reihe (04) prices the full named interface at +104 + 85t — the two filings disagree by 86 lines and the fork is unruled; carried as contingent, §5. |

### E2 · Laid Seal — STRUCK (B-5)
Filed ≈0 → re-weighed **0 production / 0 tests**. Siegel (01)
confirms: one voice, no parameter set, no predicate. No delta.

### E3 · Vertigo (Wren)
| | |
|---|---|
| As filed | ≈350 — Wren's §5a says **gross** in his own part; D/E §4 re-labelled it net. |
| Re-weighed production | **285** = Deck file at Fenja's deletes-once union **140 classic net** (184 Waage; her count already unions Rad's wheel regions, Zug-II's Deck regions and the :273 condition charged once) + `BlockitCoreViews.kt` E3 regions +67 (Rad `freezeAtSeal` +22; Zug-II `sealGhost` +30 ⚠ slide region + wheel input-death +15) + upstream night split MainActivity/Theme +20 (Zug-II — without it the Deck deferral lies) + VM +40 (Wren `t0Mono` +25; Zug-II ground mirror +15, fork-option) + Curves E3 allocation +8 (file union §4) + nav absorption +10 (Wren; Zug-II named-not-priced) |
| Re-weighed tests | **≈130** (Fenja: Rad T1–T5 ≈70 + Zug-II Deck assertions ≈60) |
| Band | **±30**: the slide-region +30 ⚠ re-pins after Marlowe's merge (Fenja's Deck region G band 8–30 moves the Deck line inside her stated 165–205 Waage); ring-field scenery +45 gross is **cuttable and excluded**; Rad's own share (142 + 70t) and Zug-II's total (365 net / 410 gross) are cross-checks whose Deck regions are **not** added on top of Fenja's union. |

### E4 · Release Proof + the finish (Wren) — VM share mine, §2
| | |
|---|---|
| As filed | ≈410 (Wren's own itemisation sums to 370 excl. the rack machinery he also lists at ≈110 shared, 480 incl. — D/E picked the middle without saying so) |
| Re-weighed production | **509** = Wren 410 + Draht Δ **+55** (his interface total 204 vs the merged sequencer+retry scope of 150: reverse cascade simplified to one commanded link, 70 in D8's controller, −40; retry done properly as scheduler + receiver + boot re-arm + fault persistence, +85 against Wren's 40; LiveWire leg +4, DAO +5, Deck-open hook +10) + Linea defocus increment **+32** (secondary-plane wiring + scrim + API<31 fallback — in no filed price) + Linea VM gate **+12** (Layer-2 input-death predicate; her Layer-1, FinishSnapshot +25 and task-line branch +30 ride regions already priced in Wren's 410 / Fenja's Deck union and are **not** added again) |
| Re-weighed tests | **155** = Draht ≈110 + Linea ≈45 |
| Band | −32 if Mara/Floor refuse the defocus scrim; Draht's fault persistence rides F15's migration (Saum, §4) — +8 standalone if F15 is refused. My VM recount (§2) confirms Wren's VM 150 inside the 410; no delta taken there. |

### E5 · D6 hub light + orange exit (Mara)
| | |
|---|---|
| As filed | ≈130 (a) 80 + (b) 50 |
| Re-weighed production | **141** = Nabe E5a **85** (+5 vs filed: two gradients forced by the live night push + dedicated specular paint/matrix, partly offset by a smaller crossing branch; his +6 Curves lines are inside his 85 and inside the §4 Curves union — counted once) + Horiz E5b **61** (+11 vs filed: the F23 scheme pin, +22, was carried as an unpriced ruling dependency and is now ADOPT and real — VM hold field, MainActivity root sample, Deck glyph/pinned split; core came in ≈5 under on E3's piggyback) − ≈5 phase-plumbing duplicate (Zug-II's E3 exit-plumbing +10 and Horiz's +13 are the same wiring; Fenja's Deck region G already carries its Deck half) |
| Re-weighed tests | **150** = Nabe ≈95 + Horiz ≈55 |
| Cross-check | Taste (26), independent Waage count of the two view files: E5 all-in ≈152 Waage (band 140–160) — confirms both owners' direction, ≈6 past their combined classic net; the difference is counting convention plus his B1/B2 setter-idiom reading. Not added. E5b standalone (built before E3) would be ≈76 — do not quote 61 without its piggyback. |

### E6 · The Sled Goes Home (Fen)
| | |
|---|---|
| As filed | ≈102 + ≈60 tests |
| Re-weighed production | **141** = Schlitten (13) **134 marginal** (his standalone is +147: view +60, VM +49 — `ReceiptState` + `SledReturnGate` — Deck +31, MainActivity +4, Curves +3; the −13 is the D-slide rewrite absorbing the teleport death and hit-test he would otherwise rebuild) + Spule (11) resume-law increment ≈+7 (his +14 over Fen for playTime caching / seek-resume / freeze records, half absorbed by Schlitten's V3/V6) |
| Re-weighed tests | **138** = Schlitten +120 (gate unit +50, Robolectric view +70 — Fen's ≈60 assumed a fraction seam the private field doesn't provide) + Spule resume test ≈18 |
| Band | Taste (26) priced the Fen-form at ≈105 all-in Waage and called it agreement with Fen — i.e. **the downside of this band is ≈105–116 if Ren builds Fen's sketch instead of Schlitten's gate form**; Schlitten's §7.3 names what the extra buys (detach law, origin shim, the 4-stage unwitnessed gate). Headline takes the buildable form. |

### Slice-E seams not owned by E1–E6
- **Saum (15) — Migration 9→10 (F15, ruled ADOPT): +23 production
  / +95 tests.** Trunk is DB v8 today; 8→9 is Voss's routines lane
  in flight, so this migration composes on his registered name.
  Union-adjusted **+17 production** (Draht's fault-column DDL
  overlaps ≈6 — his accessors/writers stay in E4's price).
- **Spule (11) — `MonolithCurves.kt` file union: +21 for the file
  across Slice E.** Owners already carry most of it inside their
  totals (Nabe +6, Schlitten +3, E3 allocation +8 = 17). Union
  adds the unallocated remainder **+4** (duration constants,
  consumer-law KDoc, `MechanicalOvershoot` deletion −2).

---

## 4 — THE UNION TOTAL

| Joint | Filed (D/E) | Re-weighed production | Tests (re-weighed) |
|---|---|---|---|
| E1 Throw | 210 *(all-in, mislabelled)* | **226** | 152 |
| E2 Laid Seal | 0 | **0** | 0 |
| E3 Vertigo | 350 *(gross, mislabelled)* | **285** | 130 |
| E4 Release Proof | 410 | **509** | 155 |
| E5 Hub light + exit | 130 | **141** | 150 |
| E6 Sled Goes Home | 102 | **141** | 138 |
| Seams (Saum F15 + Curves remainder) | — *(not in D/E's E sum)* | **21** | 95 |
| **SLICE E UNION** | **§4 claimed ≈1,700** | **≈1,323** | **≈820** |

**Union band: ≈1,210–1,430 production** (slide re-pin regions,
the E6 form dispute, the E4 defocus ratification, the E1 lane
choice at −82). All-in with tests: ≈2,143.
**Excluded, named:** Reihe's routines-convergence interface
+104 / +85t (contingent on F-S1/F-R3 ruling); Linea's receipt-host
fork F-L1 +35…+85 (unruled, unpriced into the slice by its own
seat); ring-field scenery +45 gross (cuttable); D8's gated rack
+≈370 gross and Nils's Tear-Seal +≈680 gross (D-slice gates,
unchanged).

## 5 — THE THREE LARGEST DELTAS, EXPLAINED

1. **The filed 1,700 was never the sum of its joints (−498 of
   phantom).** D/E §4's own E rows sum to **1,202**, and its stated
   method — per-file union — can only shrink a sum, never grow it.
   The ≈498 gap has no line item anywhere in the merged file; the
   only nearby objects of that size are the Live Wire substrate
   (Ines ≈400, D-priced) and D8's controller (≈200 of the cascade's
   465, D-priced) — substrate E1/E4 hang off, silently carried into
   E's headline. Re-weighed, E stands on its own joints: 1,323.
   Net delta vs the 1,700 headline: **−377 = −498 phantom + 121
   real growth** (below).
2. **E1: the filing folded its tests into production, and the
   render mechanism then grew for real (+76 production vs the
   true filed 150).** Siegel's B-5 correction (−12) is swamped by
   Riegel's dedicated seal lane (+82): the serial queue renders
   before it writes, so a queued Throw's t=0 onset is a hope, not
   a property (§1). Largest single real growth in the slice.
3. **E3: the filing re-labelled gross as net (−65).** Wren priced
   350 *gross*; Fenja's deletes-once union of the one file three
   owners priced (Deck: Rad ≈120 net, Zug-II ≈285, Wren ≈195
   implied — the same regions three times) lands at 140 classic
   net for that file, with moved blocks at 0/0 and samplers
   charged once. Even after adding what nobody had priced
   (upstream night split +20, VM ground mirror +15), E3 falls.

Runner-up, for the record: **E4 grew +99 honestly** (410 → 509) —
Draht's retry as real machinery instead of a scheduler sketch
(+55), Linea's defocus plane (+32) and VM gate (+12). Nobody
padded it; the merged file had under-built the retry.

## 6 — GAPS, NAMED (not guessed)

Parts received and reconciled: **01 Siegel, 02 Riegel, 03 Klinke,
04 Reihe, 05 Draht, 07 Rad, 08 Zug-II, 09 Nabe, 10 Horiz,
11 Spule, 13 Schlitten, 15 Saum, 19 Linea, 24 Fenja, 26 Taste.**

- **Missing at filing: parts 06, 12, 14** — no files in
  `plan/slice-e-parts/` under those numbers, no letters in my
  inbox. Their joints are unknown to me and are **not** priced
  here; if they land, this roll-up needs a one-pass amendment,
  not a re-weigh.
- **No dedicated re-weigh landed for:** Emil's receipt
  *composition* (Linea expressly excludes it; it rides inside
  Wren's 410 unexamined — the one number in E4 nobody re-read);
  the D9 imprint swap/lip hooks at E1200 (Wren ≈10, carried
  as filed); nav absorption (Zug-II named-not-priced; Wren's
  +10 carried as filed).
- **Unruled forks whose prices are contingent, not in the
  union:** F-S1 routine-arm convergence (+18 Siegel-minimal vs
  +104 Reihe-full — an 86-line disagreement between two landed
  parts, for Ilya/Ren, not for me to split); F-L1 receipt host
  (+35…+85).

— Waage, Improvers · the scales
