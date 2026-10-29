# ISSUE #6 — WAVE-2 REPORT · ANTLITZ (cluster-B lead, flat under Ren)
## Main-app pass — Deck (audit fixes, 22 items F1–F22)
## Branch agent/issue-6 · All work DECLARED, uncompiled. Never compiled.
## Wave-1 record stands: Rost's ISSUE-6-verdict.md (F1 done, C1–C11 ruled).

## 1 · WHAT WAS FIXED IN CODE THIS WAVE (lead, ANTLITZ)
Wave-1 closed F1 in code (20 dead params, −66 lines) and specced the rest
for the batch-10 merge. This wave pulled three specced items forward into
code — each small, local, and machinery-free:
1. **C3 / F9 — the usage-UNSET live lie dies.** `refusalSentenceFor`
   (MonolithCustomComponents.kt): the `UsageAccess.UNSET` row printed
   "CAN'T CHECK BLOCKING — TAP TO RETRY" with RETRY_CHECK while the
   Deck's tap routed to the Usage settings pane. It now prints
   "USAGE ACCESS IS OFF — TAP TO FIX" with FIX_USAGE — the sentence
   names the door the hand opens. The Deck's RETRY_CHECK special-case
   for UNSET is now unreachable from this row (left in place, harmless,
   noted for the merge tidy). Test updated in SliceDGestureAndStripTest
   (`causeTablePicksOneSentenceByCausalPriority` now asserts the UNSET
   sentence + FIX_USAGE). The UNPROVEN/a11y-stamp RETRY row is untouched —
   retry is honest there.
2. **F11b / C11 — unwired slide defaults UNPROVEN, never LIVE.**
   BlockitCoreViews.kt `readWarrant()`: `?: WarrantState.LIVE` →
   `?: WarrantState.UNPROVEN`, and the warrantProvider KDoc corrected.
   An unwired surface is unmeasured; the old default inverted the truth
   law. Production wires the provider today — previews now render
   honestly unproven instead of claiming proof.
3. **F3 — the floored, inverted, invented fraction dies.**
   TactileDeckScreen.kt `progressFraction`: timed progress is now
   ELAPSED/TARGET filling with `coerceIn(0f, 1f)` — no 0.03/0.05 floors,
   never remaining/target born-full-and-draining (frozen branch uses
   target − frozenDisplaySeconds as elapsed). FLOW/stopwatch = 0f in
   both running and frozen branches: NO END means no bar, and the
   invented /1200 denominator is gone. Idle = 0f (no session, no
   progress). **The % text is KILLED** (audit §3, unanimous) — the row
   under the track is removed; the track itself is the reading.

## 2 · ITEM DISPOSITIONS (F2–F22, updated by this wave)
- F1 CLOSED (wave-1 code). F3 CLOSED (this wave). F11b CLOSED (this wave).
  C3 CLOSED (this wave).
- MERGE-PASS (specced in wave-1 files, unchanged): F2 (FLOW cap killed as
  control — note: the FLOW key-cap still renders in the running/hero zone
  in this tree; its kill lands with F16/F18), F4 eyebrow recipe, F5 break
  hero (span-clock defect stands), F6 STOP chair, F7 task ghost, F8 seam
  → PARK (strip-zone edge only; #4's band is separate), F9 remainder
  (fault promotion above the slide; five sentences inside innovator P2's
  Truth Token lane), F10 hero single-press, F11 card shadow truth,
  F12 night utilities step-down, F13 key-cap radii 8→12dp, F14 spacing +
  scroll fade, F15 ground recession, F16+F18 proof drawer / Fusion (one
  change), F17 token table (GATE: no table, no F10/F12 credit),
  F19 receipt dismissal, F20 rail PARK print, F21 comment truth pass.
- F22 CARRIED DEBT — stands, by design: no merge calls this screen
  finished while its accessibility semantics stand open.
- C4 (ENDS derived, slides on ticker stall; stored-end stamp owed) and
  C5 (warrant's LIVE conjuncts exclude shieldProtecting; post-seal
  re-check is machinery) remain ESCALATED to Ren / Truth-Car ground —
  no Deck-side print may call running BLOCK truthful until C5 lands.
  C5's shieldStale input is also issue #4's B-C1: one machinery fix
  discharges both.

## 3 · FINISHERS ×5 — signed sections (cluster report, condensed faithfully)
- **F-OBERFLÄCHE (surface):** F13's two-radius law and F11's shadow truth
  are the surface debt; both MERGE-PASS (F17 table first). F3's empty
  FLOW track is a surface that finally tells the truth. — *F-Oberfläche*
- **F-BEWEGUNG (motion):** F15 ground recession + F21's struck "settle
  decelerates" claim: transform curve kept verbatim, translation deleted
  at merge. No motion item was CODE-NOW-safe this wave. — *F-Bewegung*
- **F-LICHT (light):** F12 night step-down (#2A2320) and STOP-chair ranked
  mass in the F17 table; Color.kt wins over the views' parallel palette
  (one recorded value change). Gate stands: no table, no credit. — *F-Licht*
- **F-KLANG (sound/haptics):** no new sounds in any #6 item; seat-knock
  pitch terms are haptic-only (Feel-2 R-1/R-2, adopted wave-1). — *F-Klang*
- **F-DRUCK (typography/print):** C3 was the worst print on the screen —
  a sentence naming the wrong door. Closed this wave. F4's recipe line
  (`3 × 50 + BREAKS`) remains the highest-value print fix at merge. — *F-Druck*

## 4 · FEEL SPECIALISTS ×5 — signed sections
- **FEEL-SCHWERE (weight):** the STOP chair's dark mass is ranked, capped,
  intentional (F17 table) — weight by decision, not accident. — *Schwere*
- **FEEL-LICHT (light):** idle-at-03:30-outside-SLEEP evaluating to DAY and
  blazing porcelain is an evaluation-level ruling for Ren, NOT a Deck
  patch — re-flagged, priced M (see §6). — *Licht*
- **FEEL-ZEIT (timing):** drawer settle 520ms proof linger vs 400ms ground
  step kept separate (wave-1 ruling stands); F3's filling bar now moves
  in the session's own time, not against it. — *Zeit*
- **FEEL-KONTAKT (touch+sound):** F19's scrim-tap shake answers stray taps
  (A7 lands before F19; Zettel/#10 notified via wave-1 verdict). The
  slide's refusal-in-the-hand remains the Deck's best felt truth. — *Kontakt*
- **FEEL-LEKTÜRE (instant-read):** with % dead and FLOW bar-less, the
  running Deck reads in one look: hero time, a filling track, nothing
  else competing. C6/C7 censuses corrected (max 5 simultaneous
  renderings; FLOW ×2 idle only). — *Lektüre*

## 5 · THE 20 NAMED TYPES — signed sections (cluster: 10 full seats + lead synthesis)
Full seats seated and reported (their sections in the cluster record):
- **Crazy-ideas:** priced P1/P4/P6/P7 stay filed to the merge programme,
  unbuilt — wildness with a price tag, not a landing. — *Crazy-ideas*
- **Maximum-implementation-potential:** three items (C3, F11b, F3) were
  CODE-NOW all along under the "merge-pass" label — labels are not
  evidence; this wave's haul is the proof. — *Max-impl*
- **Brutal critic:** C5 stands as the audit's headline and this issue's
  shame: the Deck prints enforcement from a warrant that excludes the
  shield conjunct. Until machinery lands, every BLOCK print is suspect —
  said plainly, again. — *Brutal critic*
- **Improver:** F3's fix also deleted a whole Row of UI (the %) — the
  best fixes subtract. Next subtraction candidate: the FLOW key-cap (F2). — *Improver*
- **Designer:** "One Question Per State" + Fusion remains the winning
  direction; this wave deliberately did not pre-build fragments of it
  that F16/F18 must land as one change. — *Designer*
- **Consumer:** the UNSET fix is the one a consumer would actually hit:
  the phone now tells him which setting to open, in the sentence he
  taps. — *Consumer*
- **Student:** at 05:00 before FASTED, the recipe eyebrow (F4, merge)
  matters more than any other pending print — endorsed as merge order
  priority #1 among the print items. — *Student*
- **User:** FLOW with no bar and no % finally stops answering a question
  FLOW never asked ("how far?") — it has no end; the face agrees now. — *User*
- **User-experience:** F22 is the UX debt that outlives cosmetics: wheel,
  slide and rail semantics. Carried, named, never silently dropped. — *UX*
- **Purpose-questioning:** the Deck's one job (audit §1) — set it, see
  it's true, seal it — is the filter every disposition above passed
  through; F-items that failed it were struck in wave-1, not here. — *Purpose*
Lead-covered lenses (LEAD-COVERED — ANTLITZ, from the wave-1 record and
this wave's code): **Thoughtful** (sequencing restraint: F16+F18 as one
change, P3 after F16+F18), **Deep-thinker** (C4/C5 are one root — prints
derived from unverified presents; the stored-end stamp + shield
re-check are the same cure family), **Enthusiastic designer** (the
Fusion's dial-as-its-own-clock remains the enthusiasm of record, F18),
**Wow-level designer** (rim-ink progress under Fusion replaces the track
this wave merely made honest), **Designer 29-years** (print laws outlive
screens: one voice per fact, labels name their gesture — C3 was a
29-year rule broken and now restored), **Intentionality** (every
remaining print on the Deck now has a named owner or a merge date),
**Synchronous collaboration** (F19 vs Receipt A8 precedence ruled
wave-1; #10's cell notified; #4's B-C1 = #6's C5 cross-linked in §2),
**Implementation-quality** (fixes landed with their tests updated in
the same commit — SliceD test asserts the new UNSET sentence),
**Technical execution** (all three fixes are single-function, no
signature changes, no caller updates — compile-risk minimal, DECLARED),
**Interaction-detail** (buttons: STOP chair keeps one verb whole-session
at merge (F6); 3D: hero single-press tokenised ghost (F10); vibration:
no haptic invented in any #6 item — the slide's refusal-in-the-hand is
the standard every other control is measured against). — *ANTLITZ, lead-covered*

## 6 · DECISION-FOR-SARTHAK
- None from #6's own list. Two items are REN-level, recorded not escalated
  to Sarthak: (a) Feel-Licht's 03:30/day-evaluation ruling (priced M);
  (b) C5/C4 machinery (post-seal shield re-check + stored-end stamp) —
  Truth-Car / batch-10 ground, Ren's call.

## 7 · WHAT I WANT NEXT (ANTLITZ)
At batch-10: land F17's token table first (it gates F10/F12 credit),
then F4+F5 (hero/eyebrow/break), then F16+F18 as one change — and close
F22's semantics in the same pass or keep this issue honestly open.

— ANTLITZ, cluster-B lead, issue #6. DECLARED, uncompiled, awaiting Ren.

## APPENDIX — exact GitHub comment text (fallback if the comment call blocks)
Posted / to post on issue #6: see §1–§2 above, signed — ANTLITZ.

## STATUS ADDENDUM (lead, after commit c5730b5)
- Push to GitHub FAILED in this cell: `fatal: could not read Username for https://github.com` — commit c5730b5 is LOCAL on agent/issue-6 (ahead 1 of origin/agent/issue-6). Push is owed by Ren; nothing here claims otherwise.
- The signed summary comment DID post on issue #6 via the GitHub connector (comment id 6510075562).

## ADDENDUM 2 — the 20-types cluster, full seats (arrived after the main report)
All 20 named types reported in full; the lead-synthesis in §5 stands, and
these seats ADD: (1) Tech-exec's C5(b) asymmetric degrade — **IMPLEMENTED
by the lead in this addendum's commit** (running blockCell now degrades
to UNKNOWN on a live-UNPROVEN or stale-for-print wire; the durable C5
cure — post-seal shieldProtecting re-check — remains machinery for Ren).
(2) Interaction-detail's full Deck spec (wheel detent ticks 12ms / 50-min
and FLOW-boundary 20ms pulses; slide 80%-travel 15ms pre-arm tick, seat
25ms double-pulse, refusal 30ms thud + 1dp sink; key-cap 12dp radius,
2dp→0 edge compress ≤80ms, 8ms click; STOP-chair hold pulses 15/20/25ms
at 25/50/75% + 40ms completion; rail PARK ≥48dp, scrim-tap shake, no
haptic) — filed as the F6/F10/F13/F19/F20 merge-pass implementation spec.
(3) Purpose-questioning + Thoughtful flag C5's re-check and C4's
stored-end stamp as **DECISION-FOR-SARTHAK via Ren** (machinery approvals,
not Deck patches) — recorded here exactly as flagged; the lead's §6
"Ren-level" wording is corrected to match: these two go UP to Sarthak
through Ren, never guessed. Phase-2 Vega B + Muede's 05:00 door stay
struck pending his separate word.
(4) No seat endorses calling #6 "finished" while F22 + C5's suspect
running print stand — the report's wording already complies.
— ANTLITZ, lead. DECLARED, uncompiled.
