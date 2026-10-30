# DURCH-A4 PORT — Issue #4 (THE BOLT fold, branch → main)

**Seat:** DURCH-A4 (third seat, cell captain under DURCH-A). **Date:** Oct 30, 2026.
**What this is:** the hand-port of the issue-#4 closer's fold from branch
`agent/issue-4` (tip `a8c3a5ac`, 647 commits behind main — a textual merge
was not survivable) onto current main, in the studio's established
hand-port form (cf. PR #474). PR #193 (the old branch's PR) is superseded
by the port PR; its fold commit is accounted for here — merged another
way, not lost.

## Ported verbatim from `a8c3a5ac` (zero content changes)

- `app/src/main/java/com/example/ui/components/SealBar.kt` (858 lines:
  `BoltInputs` / `BoltFace` / `boltStateFor`, STALE face, 12-sentence
  spoken set, glance totals over rendered elements, INTERIM tween named)
- `app/src/test/java/com/example/SealBarStateTest.kt` (fold truth table)
- `app/src/test/java/com/example/BoltRenderGateTest.kt` (executable
  render gate: OFF/RUNNING night pair measured, STALE shutter,
  planned-end differential, ending-soon inversion, spoken-set parity)
- `studio/plan/DESIGN-issue-4-the-bolt.md` (design of record `c5de8d2`,
  as amended in the open by the closer)
- `studio/plan/DESIGN-falz-deck-top-strip.md` (with FALZ correction note)
- `studio/plan/CLOSER-DISPOSITION-issue-4.md` (per-charge verdicts —
  the disposition of record for K-1…K-12, H-1…H-7, V-1…V-9, B-1…B-3,
  F-1…F-6; carried onto main unchanged)

## Host wiring (`TactileDeckScreen.kt`, surgical — the only adapted file)

- `blockCellFor` / `idleBlockCell` / `blockCell` **deleted**; the
  UNA2-04 + issue-#6 C5(b) laws live on as `boltStateFor` precedence
  rows (the merge-order note is honoured: #4's fold lands first, as
  its own PR; C5(b) is replaced, never co-merged).
- One fold block: `BoltInputs` from the host's ONE warrant read +
  `heldFinish` (B-C2 input, already at this host) → `SealBar` in the
  pinned stack, replacing `ProofStripBand` (the component itself is
  NOT deleted — K-11's corrected law: de-rendered ≠ deleted).
- Adaptation vs the branch host, exactly one: `readStale` reads the
  host's `nowMono`, which on main is the 5-second tick state
  (`nowMonoTick`) rather than a direct `SystemClock` read — the fold
  therefore re-evaluates staleness on the tick, same 45 s print law.
- TAP CAN COMMIT (Sarthak, Oct 29 05:48) honoured: the verdict binds
  the Deck's slide (`SlideToStartView`, untouched by this port), and
  the bar itself carries no commit gesture and no slide-only gate —
  per DRUCK §1 a tap on the bolt's pixels is a housing press, never
  a handle; the fold neither adds nor removes any commit path.

## Re-verification at port time (DURCH-A4, against current main)

- Main had **no** `SealBar.kt` at all and the Deck still printed the
  text strip (`ProofStripBand` fed by `blockCellFor`): the gap the
  auditor charged (K-1/F-1) stood on main in full.
- Every fold input exists host-side on main (`heldFinish`,
  `liveWarrantState`, `WatchStore.printState` / `warrantStaleForPrint`,
  `frozenState`, `spanRemainingSeconds` / `spanTargetSeconds`), and
  main's `endsAtText` still returns the `"ENDS "` prefix
  `endsAtNumeralsFor` requires — no helper port needed.
- Grep gates after the port: no `blockCell` / `ProofStripBand`
  derivation or call remains in the host (comment mentions only);
  `SealBar.kt` contains no `WatchStore` / `seamMarksFor` call
  (comments stating the law only) — the closer's K-1 gate, re-run.
- No main test references `blockCell` / `ProofStripBand` / `SealBar`;
  Deck render tests assert seal-proving sentences, not strip words.

## Carried blockers (NOT cured by this port — named, per the disposition)

1. **Ren's countersignature** — DESIGN §9 as amended (K-11) and H-1's
   labelled-planned-end amendment both await it (V-6, H-1).
2. **H-2** — "OFF unmistakable" vs the OFF/CAUGHT binary bucket:
   escalated to Ren; no summary from this lane claims it.
3. **H-5** — the split count still prints in the imprint directly
   below the bar (off the bar, not off the face): Ren's ruling owed.
4. **Panel verdict** — Bar/Truth/Beauty stood NOT YET at the closer's
   seating; a port cannot seat its own panel.
5. **T1–T9 human battery** — the executable half is landed
   (`BoltRenderGateTest`); cold reads by people, 1 AM simulation and
   the cold-read-of-record require humans + a device. OWED.
6. **K-8 remainder** — keyframed throw (0/24/62/94), pin-at-seat,
   2 dp SEATED sink: the 50 ms tween stands, named INTERIM in code.
7. **B-C1 shield-beat form** — the fold reads the warrant's 45 s law
   only; the shield's 10 s beat input remains owed VM plumbing.
8. **REL-TB merge** (K-7 remainder) — bar semantics node + refusal
   sentence node merge: a Deck composition change, still owed.
9. **K-10** — the Blockit comparison stands downgraded to Sarthak's
   rhetorical question adopted as principle; filing a real Blockit
   surface is a device/browser task for Ren.

## PR #193 accounting (tracker law: no closed-unmerged work unaccounted)

The branch's remaining commits are: the fold commit (ported here);
the wave-1/wave-2 Bolt iterations it superseded (SealBar v0 + docs —
superseded by the fold itself); the design/charge/audit documents
(the design of record + FALZ + disposition are ported above; the
charge files — KRALL/HOLLIS/VEX/BEDACHT/FASSUNG/GLANZ/RAUSCH/AUDIT —
are evidence whose verdicts are carried in full by the disposition);
two unrelated test files (`SealThrowIdentityTest`, Slice-E;
`SealTransitionDirectTest`, issue #48) that rode the branch — not #4
cargo, owned by their own lanes; and merges of since-landed main.
Nothing on the branch is deleted; the branch stays on the remote.

— DURCH-A4, under DURCH-A, for Ren.
