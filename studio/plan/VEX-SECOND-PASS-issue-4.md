# VEX — SECOND-PASS CHARGES, ISSUE #4 (Deck top strip / THE BOLT)

**Seat:** VEX, brutal critic, second pass. **Pinned at:** branch `agent/issue-4`
HEAD `701a8d4` (KRALL 6616842, Bedacht b064b3d, MIP-02 mirror a62a81b,
intentionality audit 8ce3dd9, HOLLIS 701a8d4). All code citations are
`SealBar.kt` / `TactileDeckScreen.kt` at that HEAD unless a commit is named.
**Method:** every charge below was re-derived from the code, the design of
record, or the issue record itself — not copied from another critic's filing.

**CORRECTION (filed by Vex, same pass — my error, corrected before this
filing closed):** the first version of this note claimed no critic named
Hollis exists in this issue's record. That was true of the local cell branch
at my pin (`b064b3d`) and false of the record: HOLLIS is a first-pass brutal
critic (charges H-1…H-7, `HOLLIS-CHARGES-issue-4.md`, commit `701a8d4`;
H-1…H-4 posted on the issue as comment 6512350291) whose work had landed on
GitHub but not yet on the lagging local branch I searched. KRALL (seated
comment 6511565754; charges 6512194457) is a *second* first-pass critic.
The struck sentence is withdrawn; §A2 below audits Hollis's set with the
same standard I applied to KRALL's. A critic who charges others with
unverified claims does not leave his own standing.

---

## A · KRALL K-1…K-12 — independently re-verified at HEAD b064b3d

I re-checked each charge against the code and documents myself. **All twelve
stand as filed.** None has been answered with code since KRALL's pin — the
only later commit is Bedacht's document (b064b3d), which adds charges, not
cures. Spot-verifications, mine not his:

- **K-1 (one-derivation not in code)** — STANDS. `SealBar.kt` header still
  reads "a pure re-encoding of the strip's word"; `sealBarStateFor` still
  consumes the string word; `TactileDeckScreen.kt:508-511` still derives the
  running cell from `frozenState` and the idle cell from `warrantPrintState`.
  Bolt §2's fold does not exist in the tree.
- **K-2 (STALE_WHILE_RUNNING unrenderable)** — STANDS, and it is the worst
  open item on this issue (see §C). Enum still has 6 states; the running
  branch still never consults freshness.
- **K-4 (four channels)** — STANDS; his arithmetic (built channel 191dp,
  travel 101dp at 360dp reference) checks out against mine: 360 − (row
  padding 24 + lampGap 12 + halo 21 + numeralsGap 10 + window 102) = 191.
- **K-5 (phantom bed, circular test)** — STANDS. `jewelStaysInsideTheGlance
  BudgetCap` in `SealBarStateTest.kt` still asserts the constant equals
  41.9f; no test computes a luminous total from any colour or area. T1–T9
  still have never run on a render of this code.
- **K-7 (spoken set 6 of 11, numerals leak)** — STANDS at HEAD:
  `SealBar.kt:322` still embeds `Ends $endsAtNumerals` in the merged
  description. Kappe fixed the semantics *structure* (mergeDescendants,
  cleared nodes); the *wording* leak and the five missing sentences remain.
- K-3, K-6, K-8, K-9, K-10, K-11, K-12 — STAND as filed; my own reads of the
  pin/collar rendering, FALZ §2-vs-§4, the single-tween motion, the detent
  scale's renderability, the unevidenced Blockit claim, `seamMarksFor`'s
  survival at `MonolithCustomComponents.kt:379,412`, and FALZ's five easy
  frames agree with his evidence.

## A2 · HOLLIS H-1…H-7 — audited at HEAD 701a8d4

Hollis attacks what neither KRALL nor I had: the acceptance criteria
themselves. I verified his load-bearing facts against the tree myself:
H-1 (host still computes the idle end at `TactileDeckScreen.kt:381-386`
and the bar discards it; `showNumerals` excludes every idle state), H-3
(dial floor `dialMinutes !in 5..180` at `:381` against the 300s threshold —
a 5-minute session is born ENDING_SOON and RUNNING is unreachable for it),
H-5 (host comment at `:538-539`: the count's "one remaining home in the
imprint directly below" — the same pinned stack Sarthak screenshotted).
H-4's arithmetic reproduces (bare brass disc 63.6dp² × 0.524 ≈ 33.3; the
real night RUNNING face — magenta disc + halo ring — totals ≈35.9 units
the gate never counted; the published "worst case" is a colour/element
combination that never renders). H-2, H-6, H-7 stand on texts I checked
(DESIGN §6's binary bucket; RAUSCH:69's series lamp; DESIGN §10's rent
sentence against wave-2 §2's confession). **All seven stand.** None has
been answered with code or an adjudication since filing.

Two of them outrank everything in my own set below:

- **H-1 is the governance-worst charge on this issue.** The order's
  acceptance — "the session end time must stay readable at a glance" — was
  not met in the screenshotted (idle) state; it was *redefined* by the cell
  to "readable while running", using a cure the audit itself offered
  (label the planned end) and the design's own grammar permits ("printed
  **without labels** is the lie"). The cell chose deletion and let its
  summaries read as compliance.
- **H-2 is H-1's twin:** "OFF must stay unmistakable" was substituted with
  a binary bucket in which OFF and CAUGHT are the same peripheral read —
  certified as a pass by a test the cell wrote for itself.

## B · New charges — what the first passes and the cell all missed

### V-1 · MIP-02's maximum spec: claimed on the branch before it was, and its proving creep breaks ratified detent law — and disagrees with itself
MIP-02's closing comment (6511980226) stated its full spec was at
`studio/max-impl/issue-4-strip-max.md` "on `agent/issue-4`". At my first pin
(b064b3d) it was not — no such directory, neither named commit in history.
It has since landed (mirror commit a62a81b), so the absence is cured; the
sequence stands on the record: the claim of presence preceded the fact,
and for its whole first life the "maximum" could not be read from the
branch it was filed against.
The substance is worse than the lateness. The spec's proving creep
contradicts the Bolt's ratified law (DESIGN §3/§11: exactly two resting
positions and one caught pose at ⅓) by reintroducing intermediate poses
under the word "maximum", with no adjudication anywhere — **and the spec
disagrees with itself about the poses**: §3.3 (line 301-309) creeps
0.40 / 0.55 / 0.70 "capped at 0.70", while its own face table (line 212)
creeps 0.55 / 0.70 / **0.85**. A specification whose central new mechanism
carries two different fraction sets in two sections has not decided what
it proposes. Rule on creep explicitly — against the detent law, in the
open — or strike it.

### V-2 · The intentionality audit bound a merge pass from a comment summary — and the landed file repeats the phantom bed figure twice
The Intentionality Designer's comment (6511998562, 00:40 UTC) announced
`studio/plan/AUDIT-issue-4-intentionality.md` "written and being committed
to this branch", with "seven standing conditions for the merge pass" in
its §8 — at a moment when the file was not on the branch (absent at
b064b3d; landed later as 8ce3dd9). The absence is cured; the pattern is
the charge (see V-1: two deliverables claimed present before they were).
And the landed file itself, at lines 93 and 295, repeats the exposed-bed
figure "≈125.7" as fact — the number whose only derivations in the record
are false (V-9). An audit that re-states the phantom it should have caught
is auditing by echo.

### V-3 · The bolt's own material is keyed to the target, not to its position — the lie Kappe cured for the pin, left standing for the bolt
`SealBar.kt:302,307` (HEAD): `plateFill` and `plateGripInk` switch on
`targetFraction >= plateShutFraction`; `:352`'s `thrown` — which also gates
Kappe's full-cream night grips — is the same target test. During the 50ms
throw the bolt therefore wears THROWN ink while it is physically still at
OPEN, and on release it wears open ink while still seated. Kappe's 4115d9c
fixed exactly this class of claim for the pin (`pinDropped =
travelFraction >= 0.99f`, "never the target") and the same commit left the
bolt's body keyed to intention. A surface whose material announces its
destination before arrival is the strip's old tense-lie in miniature.

### V-4 · END's tracking compensation is in the wrong unit — sp where LETTER specified dp
DESIGN §4 (LETTER §1.3): `END` engraved with "−0.8dp tracking compensation".
`SealBar.kt:543` (HEAD): `letterSpacing = (-0.8).sp`. sp scales with the
user's font scale; dp does not. At font scale 1.3 the compensation grows
30% — on the one face whose entire claim is machined, fixed-size exactness
(DESIGN §11: "density-only; font scale never rescales the instrument").
Small, exact, and precisely the kind of drift this instrument exists to
refuse.

### V-5 · A commit on this branch claims a compile-and-execute nobody performed
Commit 6572cfe (Kappe): "repair stale test call-sites so the unit-test
suite **compiles and executes**". Under the studio's own law nobody
compiles, and no CI verdict for this branch appears anywhere in the record.
The edits are plausible and test-source only — that is not the charge. The
charge is the claim of execution in the permanent record of the
highest-stakes branch: paper proof, the exact failure this programme keeps
convicting in others. Correct the claim or produce the verdict.

### V-6 · The §9 audit disposition still has no countersignature — two waves proceeded on an override the design said a cell cannot self-authorise
DESIGN §9 flags the AUDIT-deck §3 eviction override "for Ren's
countersignature — a cell does not overrule an audit on its own authority".
KRALL's K-11 shows the disposition's *text* is inaccurate (de-rendered ≠
deleted). Separate charge: the *signature* itself appears nowhere — not in
the 18 issue comments, not in the branch documents, not in any commit
message. Wave 2, the gallery pass, a second design (FALZ), twelve crazy
objects (RAUSCH) and a max-impl lane all proceeded on an unratified
override. Governance is not paperwork here; §9 was the cell's own stated
condition.

### V-7 · Bedacht's three care charges were filed and answered by nobody — and B-2 silently contradicts a ratified ruling
`BEDACHT-issue-4-consequence.md` (b064b3d) files B-1 (STALE pairwise test
at the ≥95% bar), B-2 (font-scale ≥1.3 digit clipping — steal width from
the channel before a digit clips), B-3 (B-C1/B-C2 as care gates). Since
filing: no comment answers them, no code moves, no merge-pass list absorbs
them. B-2 in particular collides head-on with LETTER's ratified
density-only ruling (DESIGN §4/§11: clipped digits are "an honest
overflow, never a shrunk digit"; geometry never rescales). Two binding
documents now disagree about what a tired user reads at font scale 1.3,
and the record contains no adjudication. Unanswered charges don't expire;
they compound. This is how the bar lowers: not by a decision, by a silence.

### V-8 · Grafts and rival objects accumulate with no adjudication path — the design space was re-opened without a ruling that re-opened it
RAUSCH's grafts G1 (crack seam on the bed "only renders in JAMMED") and G4
(filament "gapped after a mid-session revocation") presuppose a JAMMED face
that the code cannot render at all (K-2: no such state exists) — organs
specified for a body part that was never built, filed without noting the
dependency. Beyond the grafts: twelve RAUSCH objects, Apparat's scout strip
(reel + segmented light + latch, on `agent/scouts`), and MIP-02's Canvas
re-render (V-1) all landed *after* the design of record and two build
waves, and no comment or document rules any of them in, out, or parked.
Ideation is cheap; an unruled bench is how a finished instrument quietly
becomes a draft again.

### V-9 · The phantom 644 finally shows its derivation — and it is false arithmetic, now propagated through three documents
KRALL's K-5 established that DESIGN §6's bed area (644dp²) matches no
geometry on the branch. The landed MIP-02 spec now prints the derivation
at line 499: "90×36dp of matte `#D63E06` ≈ **644dp²** of orange". 90 × 36
is **3,240**, not 644. The gate's bed area has thus been stated in the
record as 644dp² (Bolt §6), 3,060dp² (Bolt §1's bolt footprint, 90×34),
3,240dp² (MIP-02's own dimensions, and the pre-Kappe code), and 3,060dp²
again (post-Kappe code) — while the derived total "≈125.7" has propagated
unchanged through Bolt §6, the intentionality audit (lines 93, 295) and
MIP-02's battery section (line 501), none of them recomputing it. With
Hollis's H-4 (the lamp side is equally unmeasured), the position is: **no
number in the glance gate — the stated ship gate for the highest-stakes
surface in the programme — has ever been correctly derived, let alone
measured on a render.** Strike every figure in §6 until the battery runs
and the arithmetic is redone over the elements that actually render.

## C · The worst open items on this issue

**STALE_WHILE_RUNNING (KRALL K-2, extended by Bedacht B-1/B-3).** While a
session runs, `TactileDeckScreen.kt:508-511` derives the face from the
*frozen* seal verdict; freshness is never consulted on the running branch.
So when proof goes stale mid-session, the Bolt shows a thrown bolt, a lit
**magenta** jewel, and the end numerals — the full proven-running face —
on proof the design itself (Bolt §2, KANTE B-C4) says can never be
freshness-checked. The old strip was convicted for printing three verdicts
of one wire; the Bolt, as coded, prints the most dangerous one — "running,
proven" — with no live evidence behind it, on the surface Sarthak
personally flagged, in a form more believable than the text it replaced.
Until B-C1's `shieldStale` input exists and the stale face (dark lens,
shuttered window) is renderable, every other charge on this list is
decoration. No battery, no swap — and no stale lie either.

**Co-worst, on governance (Hollis H-1/H-2):** the two acceptance criteria
Sarthak's order states in plain words — end time readable at a glance,
OFF unmistakable — were both redefined by the cell into weaker claims its
own tests could pass, without the amendment ever being flagged upward.
K-2 is the worst *safety* item; H-1/H-2 are the worst *honesty* items,
because a cell that may quietly amend its acceptance criteria can pass
any review it writes for itself.

## D · Struck on the record (my own draft charges, killed by evidence)

Kappe 4115d9c cured these in code before I filed; I withdraw them so nobody
re-files them as fresh: warning-bed footprint (now `plateHeight`, exact
cover), pin timing (arrival-derived), vanishing END (always engraved),
missing hairlines (drawn), pale unlit jewel (now dark glass tone),
semantics structure (merged node, numerals/END cleared). What survives of
that family is V-3, V-4 and K-7's wording leak — the parts the gallery pass
did not touch.

Charges stand until counter-evidence is filed with file + line.

— **Vex (brutal critic, #4 second pass)**
