# PLAN AUDIT — Nox (Critics, night seat)
## Target: Ren's PLAN-MERGE RATIFICATIONS, items 7–12
Letter under audit: `mail/sana/0858__from-ren__merge-ratifications.md`
Governed texts: `plan/MASTER-PLAN.md` §5 (L1657), §7 (L3297), §8 (L3966ff);
`plan/REGISTER.md`; shipped tree `Focus-app/app/src/main/java/…` (read-only).
Lens: the night side — this app is read at 03:30, in the dark, by a tired man
mid-session. Every verdict carries file + line. Colour charges carry channel
arithmetic (R/G/B, relative luminance, contrast).

---

## Item 7 — Multi-word Drawer search (token-split AND/OR)
**Letter:** `0858__from-ren__merge-ratifications.md` L20–21.
**Governed text:** MASTER-PLAN §7.5 L3425–3432.

### CHARGED — minor (verification did not widen with the promise)
The widening itself is honest, and I acquit it (below). The charge is the
test that never followed it:

- §7.5 ratifies the widening in print, with provenance: *"Multi-word queries,
  ruled at merge (Old Tom §5.2, Sana adopting — flagged for ratification)…
  The ruling's promise was one word"* (MASTER-PLAN L3425–3430).
- But the operative acceptance test is still the one-word test, verbatim:
  *"The thirty-day test — one typed word"* (MASTER-PLAN L3597), *"open the
  Drawer, tap the search field, type the one word"* (L3605). §7.12 check 2
  likewise tests *"a distinctive word"* — singular. No plant anywhere tests
  two tokens living in two different fields (the exact burial mode §7.10
  claims *"closed by the token-split ruling"*), no plant tests that AND
  refuses a row matching only one token, and no clause specs empty tokens
  from double spaces. A ratified behaviour with no test is a promise the
  build can silently fail — the same shape as the 200-cap failure §7.10
  itself caught, caught there only because a test existed.

### ACQUITTED — the widening as filed
Provenance is printed, not smuggled (§7.5 L3425–3432 names its author, its
flag, and that it exceeds the original promise), the price is printed
(≈ +25 lines + tests, no migration), and one-word behaviour is preserved
(L3428–3429). Night note: this serves his stated use — open after a month
and type stuff — and §7.1 lawfully suspends search while sealed, so the
widening creates no mid-session surface. The ruling stands; its test
suite is one plant short.

---

## Item 8 — Valve rest form (Pit 200×48 rest / 112dp working / Barriere strip dead)
**Letter:** `0858…` L22–24.
**Governed text:** MASTER-PLAN §5.3.2 L1734, L1752–1754; §5.4 L2214–2216, L2226.

### CHARGED — minor (the survivor is printed twice; the dead form is buried without its number)
- Survivor, verified in two places that agree: rest = *"recessed slot
  **200dp × 48dp**, nested in the arc's 90° bottom gap"* (§5.4 L2214–2216);
  working form = *"Bar: full screen width × 112dp"* (§5.3.2 L1734),
  restated at §5.4 L2226 (*"full-width × 112dp touch field"*). No third
  geometry survives anywhere in §5. That half stands.
- The burial is incomplete. §5.3.2 L1752–1754 kills *"Barriere's
  identical-coordinates entry strip"* by nickname only — the dimension
  **56dp appears nowhere in §5 as the strip's size**; it exists only in
  Ren's letter (L22–24). Worse, the only 56dp left standing in the valve
  geometry is a *live* one — the exit chip, *"≥56dp tall"* (§5.3.2
  L1756–1757). A builder reading §5 alone cannot tell which 56dp thing
  died, and can resurrect the strip believing he is honouring the chip.
- The dead form's name is then reused for the survivor: §5.3.4's
  micro-timings (L1854–1855) say *"entry strip expands to the 112dp
  surface"* — the strip that was superseded is the strip that expands.
  Name the survivor "slot" there, as §5.4 does, and print "56dp" once in
  the kill sentence. Two words of repair; until then the burial is a
  nickname, not a grave.

### ACQUITTED — geometry choice, night reading
200×48 recessed in the arc's gap is the dimmest legible object on the
running screen (§5.4 L2216–2220: no orange, no count, no EMERGENCY word) —
correct for a surface he must find eyes-off and must not be summoned by.
The 112dp working bar exceeds the ≥88dp valve floor by 24dp for a thumb
flattened under a 60s load (§5.3.2 L1734–1737). No night charge on the
choice itself.

---

## Item 9 — AOD derivations (Nacht) + the revert of shipped code
**Letter:** `0858…` L25–30 (incl. *"the truth-fix branch that showed break
progress on the desk clock is REVERTED in the main tree today"*).
**Governed text:** MASTER-PLAN §5.8.2 (FLOW ruling), §5.8.7 (zero-pixel
events), §5.9 check 14; build status §5 L2800–2812.
**Tree evidence:** `Focus-app/app/src/main/java/com/example/ui/components/
AirlockAndDisarmOverlays.kt` (the desk clock), `…/data/MonolithRepository.kt`,
`…/data/MonolithModels.kt`, `…/ui/theme/Color.kt`.

### CHARGED — MAJOR (three charges, one root: a paper derivation was enforced against working code, selectively, and the tree it produced is *less* true at night than the branch it killed)

**9a. The FLOW derivation is sound in the plan — and the tree does the opposite.**
In the plan, Nacht's derivation is correct and I defend it: a FLOW session
has no remaining and no total in the frame, so *"the hero slot therefore
renders nothing… ELAPSED stands as the face's sole reading, and the bar is
absent"* (§5.8.2). A bar without a total can only fabricate its fraction —
that is the 0.65f sin in a new coat. For a man mid-FLOW at 03:30, ELAPSED
alone is the honest glance: how long he has been in, no invented pressure,
cluster geometry never reflows.
The tree renders the bar anyway, from a fabricated denominator:
`AirlockAndDisarmOverlays.kt` L120–121 — stopwatch (FLOW) branch:
`elapsedSeconds.toFloat() / 1200f`. 1200s is no session fact; the bar
saturates at 20 minutes and then prints "full" for the remaining hours of
a FLOW session. §5.9 check 14 requires *"in FLOW and on the idle face the
bar does not render at any fraction."* The revert policed the break
branch and left this fabrication standing in the same function, twelve
lines below the comment that announces the policing (L108–121).

**9b. What the revert cost: on the shipped clock, every Safe Break now reads as FINISH.**
The reverted branch was the only code that distinguished break state on
this surface. What replaced it, end to end:
- Repository: during a Safe Break the tick returns early — `elapsedSeconds`
  is **not** advanced, only `safeBreakSecondsRemaining` decrements
  (`MonolithRepository.kt` L1052–1079); at break entry elapsed was set to
  the block target (L1083–1091), so `remainingSeconds =
  effectiveTarget − elapsed = 0` for the whole break (`MonolithModels.kt`
  L285–287).
- Clock: `displaySecs = remainingSeconds` for a timed session
  (`AirlockAndDisarmOverlays.kt` L101–105) and progress =
  `remaining / effectiveTarget` (L119–120) → the desk clock prints
  **00:00 with a 0.04-floor bar for the entire 10-minute break**.
§5.8.7's law — Safe Break changes zero pixels, *"REMAINING keeps stating
the frame remaining throughout"* — is satisfied only vacuously: the
pixels do not change because the clock is frozen at zero, not because a
span-true remaining (plan span = n × block + (n−1) × 10, §5.2) keeps
stating. At night this is the worst glance the surface can offer: a man
comes back from his break, reads 00:00 across the room, and believes the
session ended. The break-progress branch — whatever its one-rendering
offence — could not have told a bigger lie than that. Reverting it
*before* the §5.8 face exists (see 9c) traded a duplication for a false
finish. Disproportionate.

**9c. §5's text does not match the tree, and the ratification's one executed consequence was the revert.**
§5's own build status admits the frame-derived AOD *"is not yet built —
where the shipped app differs, this section, not the shipped app, is the
record"* (MASTER-PLAN L2800–2812). The differences are not marginal:
tree = one 96sp Anonymous Pro numeral (`AirlockAndDisarmOverlays.kt`
L155–162) vs §5.8.3's two Sora readings at 104sp/50sp; tree = full-width
16dp rounded bar (L166–181) vs §5.8.3's 120×3dp bar; tree hero colour =
`NightColorPrimary` = cream `#FFE4CF` (`Color.kt` L37; R255 G228 B207,
lum **0.813**) vs §5.8.5's ceiling 0.25 — a 3.25× breach on the darkest
surface in the app; tree progress = remaining-fraction (inverted vs
§5.8.2's *"frame elapsed ÷ true span"*) on a per-block span (elapsed
resets per block, `MonolithRepository.kt` L1065–1066). Against that
field of unbuilt spec, the single §5.8 consequence actually executed in
the main tree was the removal of working break rendering, on the
authority of a derivation for a face that does not ship. Enforcement
that lands only on the branch that was *improving* truth, and nowhere
else, is not enforcement of the plan — it is the plan used as a warrant.

*Minor rider (plan-internal, night):* in FLOW at Stage 1+ the ELAPSED
label drops (§5.8.5: labels gone from 20 min) leaving the sole reading
an unlabelled 50sp numeral under an empty hero register — the one FLOW
glance that can be misread as REMAINING. Low severity; a kept label in
FLOW only would close it.

---

## Item 10 — Marker-line colour: Mara's cream #FFE4CF @ 90%
**Letter:** `0858…` L31.
**Governed text:** REGISTER L318–320; MASTER-PLAN §2 L402–403; rivals in
`plan/seat-drafts/register-iris.md` L219–225.

### CHARGED — moderate (ratified colour has no home in the governed whole, is still flagged provisional in its only home, and is the weakest filed candidate on the night cap by arithmetic)
- **No home in the plan Sarthak receives.** MASTER-PLAN prints the marker
  line's behaviour — *"the cap's marker line still thickens 4→5dp at the
  catch"* (§2, L402–403) — and never prints its colour. The colour lives
  only in REGISTER L318, where it still reads *"**provisional, flagged
  for ratification**"* (L319–320). Ren ratified it; neither text was
  amended. A builder working from the plan + register — the whole that
  goes to Sarthak — meets a provisional colour and no ratification.
- **Channel arithmetic.** Cream `#FFE4CF` = R255 G228 B207, warm-ordered,
  lum 0.813. At 90% over the night cap `#F47442` (R244 G116 B66, lum
  0.321): composite (254, 217, 193), contrast **2.14:1**. Over the day
  cap `#F55314` (R245 G83 B20, lum 0.256): composite (254, 214, 188),
  **2.54:1**. The losing candidates on the same night cap: glyph
  `#EDE3D8` (R237 G227 B216) = 2.23:1; Vau's night ink `#252323`
  (R37 G35 B35) = **5.53:1** — 2.6× the ratified figure. The marker is
  the cap's one state line, read at the catch instant, on a 4–5dp
  stroke, in the dark (his sessions run 03:30 and to 20:00 — night is
  its home case, `register-iris.md` L219–225 files the three-way
  contest with no contrast winner named). The ratification chose on
  identity ("nothing else") with no arithmetic in any governed text.
- Tension, noted not charged separately: the Register's staircase
  legislates cream *"text/glyph only, never a fill"* (REGISTER L301–303).
  A painted marker line survives only if "glyph" is read generously;
  the ratification does not say so.

---

## Item 11 — #FF8A5C stays dead
**Letter:** `0858…` L32.
**Governed text:** MASTER-PLAN §5.3.7 L1938–1944; §5.8.8 L2550–2551; REGISTER (colour section).

### STANDS — ACQUITTED
Arithmetic verified independently: `#FF8A5C` = R255 G138 B92,
warm-ordered, relative luminance **0.402** — exactly the figure §5.3.7
and the Register print — against the AOD ceiling 0.25 (breach +0.152).
In the plan it appears only inside kill sentences: §5.3.7 (*"`#FF8A5C`
does not print"*), §5.8.8 (*"is not used on this face"*), §9 A-17.
`BREAK SPENT` prints word-only in glyph ink, ≤2% area, never on AOD.
No live use survives in any governed section. The door is shut in paper.
*Observation, not a charge on this ratification:* the tree still defines
it — `Color.kt` L50 `NightAlert = Color(0xFFFF8A5C)`, wired as the night
scheme's `error` colour at `Theme.kt` L61, plus an uncalled `tokAlert()`
at `BlockitCoreViews.kt` L150. Dead in the plan is not yet dead in code;
that is build debt for the merge audit's grep list, and it does not
fault Ren's ruling.

---

## Item 12 — Spec plate LEDGER clause: "cut in product, kept in paper"
**Letter:** `0858…` L33–35.
**Governed text:** MASTER-PLAN §8.10 L4533–4565 (plate verbatim, line at
L4549); §8.11 check 16 L4724; §9 V-4-E L4968–4974; §7.8 (Drawer content law).

### CHARGED — MAJOR (the distinction collapses at the plate's own acceptance test, and the kept line is false under the plan's own content law)
- **The plate is product.** §8.10 specs it as shipped furniture at PARK's
  foot, *"Teen's text verbatim, Teen's setting unchanged"* (L4533–4535),
  and §8.11 check 16 requires *"plate text byte-identical to §8.10 in
  day and night renders"* (L4724). There is therefore exactly one plate
  text, and it is the printed one. "Cut in product, kept in paper" asks
  for two plates with one name: a paper plate (§8.10, with the line) and
  a product plate (without it) that no section specs, no acceptance
  check tests, and that check 16 would actively *fail* (a builder who
  cuts the line in the render is no longer byte-identical to §8.10).
  Whichever way the builder jumps, one governed text is violated. A
  distinction the plan's own test forbids is not a distinction the plan
  can honestly maintain.
- **The line is false, not merely contested.** The plate's own law:
  *"Its lines are literally true of the shipped build or they are
  reprinted"* (§8.10 L4557–4559). The line — `SESSION RECORD: THE LEDGER`
  (L4549) — is not literally true under §7: *"Sessions, holds, and
  deflections — not Drawer content"* (§7.8); the Drawer stores finished
  **tasks** only (§7.2), and the session's record is the receipt's moment
  plus the record store (§5.7, §5.3.8). Session record ≠ the Ledger, by
  the plan's own sections. V-4-E (L4968–4974) already carries Teen's
  ruling that the line is *cut, not reworded*; §8.10 L4562–4565 keeps it
  *"on the plate only, as furniture."* Furniture is still print. When
  the plan goes to Sarthak whole, he will read a plate that (a) the plan
  says is cut, (b) the plan prints verbatim, and (c) states a thing the
  plan elsewhere proves false — in the one object whose entire law is
  literal truth. Honest options exist — cut it in both, or keep it in
  both and reword §7's claim — and the ratification chose neither.

---

## Summary
| Item | Verdict |
|---|---|
| 7 Multi-word search | CHARGED minor — test suite not widened (ruling acquitted) |
| 8 Valve rest form | CHARGED minor — burial lacks the dead form's dimension; name reused (geometry acquitted) |
| 9 AOD derivations + revert | **CHARGED MAJOR** — revert disproportionate; tree fabricates FLOW bar (/1200), reads every Safe Break as 00:00 FINISH, breaches AOD ceiling 3.25× |
| 10 Marker cream #FFE4CF @90% | CHARGED moderate — no home in MASTER-PLAN, Register still "provisional", 2.14:1 on night cap vs ink's 5.53:1 |
| 11 #FF8A5C dead | **STANDS** — acquitted; lum 0.402 verified (tree still defines it: build debt, noted) |
| 12 Plate LEDGER clause | **CHARGED MAJOR** — "cut in product, kept in paper" is forbidden by §8.11-16's byte-identical test; line false under §7.8/§7.2 |

— Nox, Critics (night), 09:16 IST, 29 Oct 2026. Read-only throughout; no tree file touched.
