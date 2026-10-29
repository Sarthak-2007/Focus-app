# PLAN AUDIT — Gust, Critics (lens: taste — the art bar)

Target: `plan/MASTER-PLAN.md` (5,229 lines, merged plan, whole document read
across §§0–9). Standard applied: Sarthak, Oct 29 — "Make a goddamn art of an
app" (§0, L31–32); beauty graded FIRST among designs that pass the three
standards; beauty is the retention mechanism. Evidence standard: every charge
cites file + line. Inbox checked at seating: empty. Scope widened per his
Oct 29 order — charges below include the chair's own merge machinery.

---

## (1) Does the governing text still SAY it?

**In §0, yes — verbatim and unweakened.** L31: "**The top criterion is
art.**" L34–36: "Every screen in this plan is graded first on whether it
looks and feels beautiful, and his eye is the final judge. Beauty is graded
first *among designs that pass the three standards*." L36–38: craft money
"is spent where beauty is felt, never spread thin as decoration." L41–43:
any spec "may be exceeded in the build where the art demands it." L45:
"beauty is the retention mechanism."

The demotion is not in §0's words. It is in what the 5,000 lines after §0
built to *enforce* §0 — see G-1 and G-2. A governing sentence nobody's
acceptance check can fail is a motto, not a criterion.

---

## CHARGES

### G-1 — S1 — Beauty has a gate on exactly ONE screen of eight

§0 L34 promises *every* screen is "graded first" on beauty. The plan's
grading machinery is its acceptance checks, and they do not grade it:

- §2.7 Deck checks, L568–595 — 10 checks: eye order, arithmetic, stillness,
  frame equality, ARM timing, knurl gate, hub geometry, sealed law. No
  beauty verdict; a cold, cheap-looking Deck passes all 10.
- §3.9 Shield checks, L937–966 — orange count, sound-truth, tone steps,
  reseeds, furniture %. No beauty verdict.
- §4.12 Tasks checks, L1588–1647 — counts of fills/panels/heroes, dp
  measurements, type grep, feel-once. No beauty verdict.
- §5.9 Session checks, L2697–2815 — five elements, one time authority,
  span truth, token tests, luminance numbers. No beauty verdict.
- §6.8 Chrome checks, L3229–3295 — band geometry, grep-clean war line,
  travel mechanics, pixel-audit for orange. No beauty verdict.
- §8.11 PARK checks, L4648–4775 — store truth, board order, FIX behaviour,
  plate byte-identity. Its only taste-adjacent check (17, the photograph
  test) grades *non-pressability*, not beauty.
- The single exception proves the rule: §7.12 check 8, L3950–3952 — the
  Drawer face "passes Liv's render evidence (R1–R6) with no kill condition
  (K1–K6) met." One screen, of eight sections, can fail for being ugly.

Word-level corroboration: "beauty / beautiful / handsome" occur in this
document only at §0 (L31–45), in §7.11 (L3721–3908), and in the §9 register
(A-3, L5004). Five thousand lines of merge machinery carried the word into
exactly one section's law. **What the art bar demands:** a named beauty
verdict — grader, evidence set, kill words, on the Liv model (§7.11,
L3885–3908) — in every section's acceptance list, or §0 L34 amended to
admit it only ever governed the Drawer.

### G-2 — S1 — The one beauty gate has a built-in unbeautiful PASS

Worse than G-1, because this one contradicts §0 in §7's own text:

- §7.11, L3731–3735: if the Drawer face "cannot be made genuinely
  handsome, the face does not ship — **the storage ships anyway**, in the
  residue form… Permanence, search, and the month index are **law
  independent of beauty**; no stored task ever waits on her eye."
- §7.11, L3910–3922: THE RESIDUE FACE is "lawful, permanent, findable —
  and **it makes no beauty claim**, so it cannot lie about one. The Drawer
  ships in residue form while a new face is designed."
- §7.12 check 8, L3950–3952, converts this into a passing outcome: pass
  Liv's evidence "**— or the Drawer ships in the Residue Face, named as
  such.**"

So the plan's only beauty-graded surface may ship with no beauty at all,
lawfully, by naming itself. §0 L34–35 says every screen is graded first on
beauty; §7 says this screen's permanence outranks beauty and ships without
it. Both cannot be the governing text. (The honesty of the residue —
"makes no beauty claim, so it cannot lie" — is real, and is acquitted
below as craft; it does not cure the contradiction with §0. A label that
says "this one is not art" on the app Sarthak ordered to be art is a
demotion with good typography.)

### G-3 — S1 — A FIFTH FREEZE, named precisely: the ordinary sub-step hit-stop, §4.4

The budget, closed at §2.6, L540–544: "the ledger stands at **exactly
four freezes** (slide commit 40ms · final sub-step 50ms · DONE landing
40ms · bank catch 40ms)… A fifth freeze may be proposed only by naming
which of the four dies."

- §4.4, L1162–1163, speccing the *ordinary* step strike: "Hit-stop at
  ~70% for **40ms** (Zeile Rung 2)." An ordinary sub-step is none of the
  four: it is not the slide commit, not the *final* sub-step (#2), not
  DONE landing (#3), not the bank catch (#4).
- §4.8, L1450–1455, says the opposite of §4.4 on the same screen:
  "**Freeze verification:** this screen spends exactly two of the
  Register's four freezes — final sub-step 50ms (#2) and DONE landing
  40ms (#3). Every other candidate is convicted: **ordinary sub-step
  hit-stops (Zeile 40ms, Luz 60ms) spend 0ms**."

§4.4 and §4.8 cannot both be built. If §4.4 ships, the ledger has five
freeze events and L541's "exactly four" is false. If §4.8 ships, §4.4 is
dead text sitting in the operative spec where a builder will implement it.
Either way the budget the plan calls "(S2, closed)" at L540 is not closed
inside its own document.

### G-4 — S2 — A second unbudgeted stillness: the HELD FRAME, 1200ms, §5.7

§5.7, L2338–2342: at FINISH, "then the **HELD FRAME — 1200ms** whole
(Register): the Room Stops (bed out 300ms), 400ms of true silence… then
the **completion pulse alone**… against an **unmoving picture**." §5.9
check 9 (L2697 section, item 9) repeats it as acceptance law alongside
Release Proof and Sled Goes Home.

A 1,200ms whole-screen unmoving picture is a freeze in every plain sense —
30× the largest ledger freeze (50ms) — and it appears nowhere in the
L541–544 list. Anticipated defence: the Register may reserve "freeze" for
hit-stops. Then L541's claim is narrower than its words: it says "exactly
four freezes," unqualified, in the paragraph that closes the budget. The
HELD FRAME has a real job (silhouette separation, §5.9 check 9's
across-the-room test) and real beauty (stillness as completion) — this
charge is not that it should die; it is that the plan's most expensive
piece of stillness was never charged to the budget the plan says is
closed. Unbudgeted spectacle with a job is still unbudgeted.

### G-5 — S2 — §5 spends spectacle without ever naming the budget; §4 and §8 defend it by name

The budget is defended where it is defended in writing:

- §4.8, L1450–1455 — freeze verification, candidates convicted by name.
- §8.10 proposal, L4637–4638 — Vau's pawl priced at "zero freezes (the
  tempting fifth freeze — a hit-stop on the pawl's seat — is **refused by
  name**)."
- §9, A-15, L5016 — "the spectacle budget is exactly four freezes."

§5, the section with the largest spectacle spend, never mentions it:

- Receipt entrance, §5.7, L2369–2373: "220ms empty beat → the sheet rises
  24dp over 380ms → all type arrives in one 110ms blow" — a 710ms
  ceremony, uncharged.
- Sled Goes Home, §5.7, L2344–2346: "at receipt +1000ms the sled returns
  **powered over 650ms** — silent, accent-free, interruptible." No budget
  reference, no beauty argument, no standard served: the session is over
  (ARM is moot), nothing is held (HOLD is moot), and nothing is *proved*
  by the travel — contrast Release Proof in the same paragraph-sequence,
  which earns its motion by proving links CONFIRMED-OPEN. A powered
  650ms return for a reset that the next Deck paint could render at rest
  is **decoration debt** (see also §(4) below): restraint adjectives
  ("silent, accent-free") are not an art argument.

Verdict on audit question (2): the plan treats spectacle as a budget to
defend in Tasks and PARK, and as a resource already spent — no questions
asked — in Session. The seal's own Strike (§2.6, L497–500: 110ms strike,
400ms ink-settle, a pressure lip that "lives exactly as long as the
seal") likewise never passes through the L540 budget paragraph printed
~40 lines later in its own section.

### G-6 — S3 — Merely functional: the Shield verb-code legend, §3.8

§3.8, L919–935: the legend is "plate style, all words secondary ink (the
line explains colour; it doesn't wear it), swatches 8×8dp radius 0" —
`■ ORANGE = COMMIT · ■ INK = SHUT · ■ GREY = PEEK · ■ PORCELAIN = OPEN`,
printed once, 16dp below the last row, and the Shield foot after it is an
S48 break and a supersession note. This is the one Shield surface with no
One Moment (Shield's moment lives at §3.5, L764–767), no feel, no sound,
and no acceptance check — §3.9's ten checks (L937–966) never mention it.
It is correct, lawful, and cold: a colour key that refuses to show colour
at sled scale teaches nothing to a tired eye at 03:30. **What the art bar
would have demanded:** the legend rendered as the sleds themselves —
four caps at true 54×30dp sled geometry in their true materials, words
beside them — or the honesty to cut it; an 8×8 chip is a settings-screen
answer on the screen whose strip (§3.5) proved this cohort can do print.

### G-7 — S3 — Chrome: the most-seen surface has no taste test at all

Chrome is on every screen, in every state, forever. §6.2 (L2857–2921)
takes a real taste position — maker's mark as furniture, Guard line quiet
by law — and §6.8 (L3229–3295) then grades chrome on band height (48dp),
hairline position (y48), grep-cleanliness, travel mechanics and an
orange pixel-audit. Nothing in §6.8 could fail a band that is merely
cheap-looking: correct dp, correct words, dead feeling. PARK, a surface
he visits rarely, gets a photograph test (§8.11 check 17); the Drawer
gets a cold-photograph kill condition (§7.11 K4, L3896–3899); chrome,
which frames every other surface's beauty, gets a ruler. **What the art
bar would have demanded:** one render verdict for the band + rail — cold,
unnamed, at arm's length — of the same kind the plan already knows how
to write twice elsewhere.

### G-8 — S3 — Decoration debt in law form: PARK's specular exception, §8.10

§8.10, L4601–4626 grants a specular-dimming exception (×0.50 / ×0.75 /
×1.00) inside PARK, then empties it: its territory "contains exactly one
candidate: **the FIX key, while pressed**" (L4608–4612), and "If the FIX
key is built flat… the exception has **zero application inside PARK and
nothing is lost**" (L4623–4626). The section's own defence — "The
exception survives in PARK as law, not as decoration looking for a
surface" (L4625–4626) — is noted and not accepted at full value: ~25
lines of merged law, plus a §9 register entry (A-16, L5017), are spent
keeping a light rig warm for a surface that by the plan's own text may
never exist, on the app's deliberately flattest surface. That is
spectacle budgeted to a hypothetical — the mirror image of G-4/G-5,
where real spectacle went unbudgeted.

---

## (4) Decoration debt — summary

- **Sled Goes Home, 650ms powered return** (§5.7, L2344–2346) — G-5:
  motion after every standard has expired, beauty unargued. Debt.
- **PARK specular exception** (§8.10, L4601–4626) — G-8: law for a
  decoration that may never be built. Debt, in reserve.
- **Not debt, acquitted here:** Release Proof (§5.7, L2342–2345) — its
  cascade *proves* opening, TRUTH pays for it. The receipt ceremony
  (§5.7, L2369–2373) — it is the one telling, Emil's filed moment; its
  cost is uncharged (G-5) but its purpose is argued. The AOD patrol
  (§5.8.4, L2467–2482) — burn-in is function, and its 1-frame-cut form is
  restraint argued in the text.

---

## ACQUITTALS — where the art bar visibly governed

- **§0 itself (L31–45).** The governing text survived the merge intact,
  correctly subordinated ("among designs that pass the three standards,"
  L35–36) and correctly armed ("may be exceeded… where the art demands
  it," L41–43). The failure is downstream enforcement (G-1/G-2), not
  this paragraph.
- **Deck, §2.4 (L280–331).** The specular rig is convicted and rebuilt on
  truth grounds (point-sampling "runs it backwards, a truth failure in
  the light itself," L280–283); the Broken Flare's per-tooth variance is
  compile-time only with a "hard no-writer-ever clause" (L286–289);
  the knurl ships only through a five-test gate where "any one failure →
  the rim ships clean, no appeal" (§2.7 check 6, L582–584) and the hero
  ships flat if D3 blurs (check 7). Craft money spent exactly where §0
  L36–38 says: where beauty is felt, with kill conditions attached.
- **Shield strip, §3.5 (L764–767).** Liv's One Moment — *The Printed
  Day*: "a 24h ink skyline whose familiarity compounds instead of
  decaying." Beauty argued as the surface's reason to exist.
- **Tasks, §4.5–4.8 (L1266–1268; L1450–1455).** *The Struck Line* —
  strikethrough "promoted from footnote to the screen's signature
  gesture; evidence, not feedback — still true in month six" — and the
  feel ledger defends the freeze budget by convicting rival hit-stops
  by name. The model §5 should have followed.
- **Running screen, §5.4 (L2138–2155).** Zone 5 is "Void (ground,
  defended)" — emptiness specced, bounded, and argued for; the cloister's
  beauty is restraint with a defence on file, not absence of thought.
- **Drawer, §7.11 (L3721–3908) — the strongest beauty governance in the
  plan.** Liv's condition carried into law; kill conditions K1–K6
  (L3885–3906) including the cold-photograph test (K4: reads as "a
  settings list or a dashboard" → the face fails) and the verdict words
  at L3907–3908: "BEAUTIFUL passes; TIDY fails; UGLY fails." *The Kept
  Shelf* (L3802–3815) — "the only screen in the app that gets handsomer
  the more it is used" — is the one place the merged plan thinks about
  beauty the way §0 orders. (G-2 stands against its residue escape
  hatch, not against this section's craft.)
- **PARK, §8.10–8.11.** The material law ("one ground, print, one
  object," L4583–4600 approx.) argues flatness as identity, and Vau's
  pawl proposal refuses the fifth freeze by name (L4637–4638). PARK's
  taste was argued; G-8 charges only the specular exception's keep-warm.
- **Extinguished Clock, §5.8.11 (L2651–2695).** A beautiful invention —
  the AOD fading to pixel-off black in stillness — is *refused entry to
  spec*, with reasons and its author's kill condition carried in force.
  The merge knew how to say no to spectacle when evidence was missing.
  It should have applied the same discipline to its own HELD FRAME.

---

## Bottom line for the coordinator

Worst charge: **G-2/G-1 as one** — §0's art criterion survives as words
and dies as machinery: one of eight sections can fail for ugliness, and
that one section may ship unbeautiful by naming itself Residue. Second:
**G-3** — the "closed" four-freeze ledger is contradicted inside Tasks
itself (§4.4 vs §4.8). The plan is full of beautiful surfaces; it is not
yet a plan in which beauty is *graded first*, because grading requires a
gate, and the merge built gates for truth, time and counts — and, except
in the Drawer, forgot to build one for art.

— Gust, Critics (taste), 09:13 IST, Oct 29 2026
