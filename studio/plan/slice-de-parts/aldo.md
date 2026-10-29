# ALDO — Slice D/E, the slide's print, whole
## Wild · typography (numeral custody) · seat 18 · re-seated by Marlowe

## 1. VERDICT

**SHIP, with one amendment and one fork.** The badge dies clean and
orphans nothing (§2a). Mutter's cap words are adopted verbatim, set
at plate, with a five-state print machine (§3b) — but they print on
the **track face**, not the sled face: measured at plate, the shorter
string is 92.8dp wide before tracking and the filed sled is 68dp.
Mutter's words survive her geometry; the fork (§7) is Ren's only
because placement touches Fen's sled arithmetic. Strip type and
refusal type confirmed/amended at §3c–d. All of it reconciled to
LIVE WIRE: no Park, no UNKNOWN verdict, no CHECKING on the cap —
those states are dead and get no words.

## 2. AGAINST THE REAL CODE (file:line)

Tree: `Focus-app/app/src/main/java/com/example/ui/`.

**The badge (dies).**
- `views/BlockitCoreViews.kt:1057` — field `subMonoBadge: String =
  "50M"` (setter :1058–1063). DIES.
- `views/BlockitCoreViews.kt:1127–1135` — the badge draw: `paintMono`,
  `sp(11.5f)` (off-ladder — 11.5 is not in 104/72/50/22/15/10),
  RIGHT-aligned at `w − dp(18f)`, alpha shared with the label. DIES.
- `screens/TactileDeckScreen.kt:498` — the host lie itself:
  `view.subMonoBadge = if (dialMinutes < 10) "FLOW" else
  "${dialMinutes}M"` — the BLOCK length, hardcoded in the host's
  `update` block, re-sent on every recomposition. DIES. This is the
  line Ines §0 flagged (`plan/maximpl-parts/02-ines-arming.md:15–17`)
  and Slice A law executes.

**The label (transformed, not killed).**
- `views/BlockitCoreViews.kt:1049` — field `labelText`, default
  `"SLIDE TO ARM SESSION"`; host hardcodes the same string at
  `screens/TactileDeckScreen.kt:497`. Both die as strings.
- `views/BlockitCoreViews.kt:1119–1125` — the label draw: `paintWord`
  (Atkinson Bold, base class :176), `sp(12.5f)` (off-ladder), CENTER
  at `w * 0.54f`. The draw SITE survives; its setting does not (§3b).
- No words exist on the sled/thumb face today — its only mark is the
  grip line (`BlockitCoreViews.kt:1143–1148`). "The cap's current
  words" are the track label's; the sled face is blank.

**The fade (survives, scope amended).**
- `views/BlockitCoreViews.kt:1113` — `textAlpha = (1f − dragFraction
  * 1.3f)` clamped: Ines §0's `1 − 1.3·frac`, already shipped, label
  and badge dying together by frac ≈ 0.77. The badge takes its copy
  to the grave; the law survives for the START print on hand-drags
  only (§3b, S1/S2 split).

**Custody transfer — verified, nothing orphaned.**
- TOTAL → hero, live today: `screens/TactileDeckScreen.kt:142`
  `totalSpanMinutes = dialMinutes * studyBlocksCount +
  (studyBlocksCount - 1) * 10`; rendered :235–248 (`heroAnnotated`:
  `50 MIN` / `2H 30M` span classes under the `TOTAL` eyebrow). The
  hero owns the total. ✓
- BLOCK LENGTH → rim scale, live today: `views/BlockitCoreViews.kt:
  650–665` prints majors `30…180` in Anonymous Pro 15sp, comment
  :650–652 verbatim: *"the scale states the block length; the hero
  states the total."* The chosen length reads as dot position on
  that printed scale. Honest caveat, by design (my filed rim law):
  at non-major detents (e.g. 50) the block length exists as
  *position*, not as an exact printed numeral — that is the scale
  doing its job, not a gap the badge was filling.
- FLOW → hero only: `screens/TactileDeckScreen.kt:237` prints
  `FLOW`; the badge's `FLOW` case (:498) was a second home and dies
  with it (F-FLOW). ✓
- Accepted consequence, named: after the death, the slide bar itself
  carries no duration at the commit instant. The hero, one glance
  up, carries the total. That is Slice A law's intent, not a loss.

**Helpers that already exist (build on them, invent nothing).**
- `views/BlockitCoreViews.kt:163–179` — `monoTypefaceBold` =
  Anonymous Pro Bold (:164–166), `paintMono` (:178); `paintWord` =
  Atkinson Bold (:153–158, :176). The plate paint is `paintMono`
  re-set, nothing new.
- `views/BlockitCoreViews.kt:258–297` — `drawMixedWordAndMono(...)`:
  word + mono segments on ONE shared baseline. This is the strip's
  baseline mechanism, already in the tree (§3c).
- `ui/theme/Type.kt:85` — `PillBadgeTextStyle`, the Compose plate
  slot (re-spec'd to 10sp/+2.0 by TYPE-RAMP-TABLE §audit).

## 3. AMENDED SPEC, EXACT

### (a) Badge death certificate — §2 above. Fields: `subMonoBadge`
dies; host line :498 dies; no replacement field, no replacement
print. The slide's print inventory after death: cap words (§b),
grip line, seat print. Nothing else may be added to the bar.

### (b) THE CAP WORDS — print state machine

**Setting (both strings, no exceptions):** Anonymous Pro 700,
**10sp**, tracking **+2.0**, line height 12, single line, centred,
glyph ink on the bar surface (shipped `LIGHT_ON_PRIMARY` tier).
Canvas note for the builder: `Paint.letterSpacing` is in **em** —
+2.0sp at 10sp ≡ `letterSpacing = 0.2f`. Getting this wrong by
treating it as dp inflates the line ~10×; the measure test (§5)
exists to catch exactly that.

**Measured widths** (Anonymous Pro Bold advance 0.5459em, font-table
sums, tracking applied per glyph):
- `SLIDE OR TAP TO START` — 21 sorts → 114.6dp advance, **≈155dp
  composed**.
- `TAP AGAIN TO UNDO` — 17 sorts → 92.8dp advance, **≈125dp
  composed**.

**Placement (the fork, §7 — recommended form stated here):** on the
**track face**, in the shipped label's home. START centred in the
free span right of the sled at rest; UNDO centred on the full track
width. On Fen's filed track (248dp, `beyond-deck-parts/fen.md:12`)
the rest free span is ≈168dp — START fits with ≈5dp slack per side,
which is why the measure gate (§5) is not optional. On the shipped
full-width bar the span is far wider; the gate binds whichever
track ships.

**State table — the words change ONLY at these boundaries:**

| State | Entry boundary | Print | Alpha / motion |
|---|---|---|---|
| S0 REST | Deck unsealed idle; sled home | `SLIDE OR TAP TO START` | full ink, still |
| S1 DRAG | finger DOWN on sled → release or seat | S0 string, **FROZEN** — no swap at gate 0.68, no swap at catch 0.84, no swap at OFF-stop | dies by `1 − 1.3·frac` (Ines §0, hand-drag only) |
| S2 TAP-MESH | qualifying tap (≥90ms) finger-UP, mesh-start frame → onLatch (140ms mesh + 110ms spring) | `TAP AGAIN TO UNDO` | full ink for the whole window; the frac fade does **NOT** apply — frac here is machine-driven, and fading would kill the mercy word exactly while it is usable |
| S3 ABORT | second touch DOWN inside S2 (buttonhole accept) | reverts to S0 string **at the accept frame**; the 250ms spring-back rides home printing S0 | full ink |
| S4 REFUSAL | OFF-stop at 0.65 mid-drag (a drag — S1 freeze governs), dull knock, dwell, spring return | S0 string throughout; the refusal itself prints ONLY in its own slot at 15sp (§d) — the cap never carries a refusal word | frozen, then S0 at home |
| S5 SEALED | onLatch / session running | **no print** — the bar is not composed (`TactileDeckScreen.kt:486` composes it only when `!isSessionRunning`); Sled Goes Home returns an unprinted sled; S0 resumes when the Deck re-enters unsealed idle | — |

**Freeze reconciliation (Teo):** the freeze law — *no printed change
mid-slide* — and Mutter's swap do not collide, because the swap is
not mid-slide. A drag is one print state (S1) from DOWN to seat;
the string is a **state print**, and its only change points are
state boundaries: mesh-start, abort-accept, latch. UNDO never
prints on the drag path at all — a drag's retreat is positional
(drag back, release pre-catch), and printing UNDO there would
advertise a second-touch mercy the drag does not have.

**Dead states — no word assigned, do not build:** PARK,
SEATED-PROVING, `CHECKING` on the cap. Killed by Live Wire
(REGISTER: verdict synchronous ≤5ms at DOWN and at the gate; the
Park and UNKNOWN class dead). Contingency only: if the Warrant
fallback ever revives the Park, the Critics' filed word is
`CHECKING`, plate, same slot — it enters by that door or not at all.

### (c) THE STRIP'S WORDS — type setting

Vocabularies are the owners'; I set only the type. (Rosa's BLOCK,
Uma's APPS tiers, Pia's TIME content; Live Wire strikes noted.)

| Cell | Vocabulary (owner's, as filed) | Family | Weight | Size | Tracking | LH |
|---|---|---|---|---|---|---|
| BLOCK | `ON` · `OFF` · `CHECKING` · `STALE`→(Critics' re-voice is Rosa's re-grade, this wave) | Atkinson Hyperlegible | 700 | 15sp | **+0.5** | 20 |
| APPS | `7 OF 7` · `6 OF 7` · `CHECKING` | Anonymous Pro | 700 | 15sp | **0** | 20 |
| TIME | `ENDS 20:00` · `UNKNOWN` (schedule unreadable — a cell fact, not the dead verdict class) | Anonymous Pro | 700 | 15sp | **0** | 20 |

Rules:
- **Family law** (W4, kept verbatim): a word-cell gets the word
  family, a numeral-cell the numeral family. `CHECKING` in the APPS
  cell is a word wearing a numeral cell's address — it keeps the
  cell's family (Anonymous Pro). Family follows the CELL, not the
  token, or the column's texture changes with state.
- **Tracking law:** tracking is a word treatment. Word-cell +0.5
  (ramp state-word style); numeral-cells **0** — mono sorts sit on
  their own grid, and tracked numerals break the tabular read the
  rim numerals already established (rim: Anon Pro 700 15, tracking
  0, TYPE-RAMP-TABLE).
- **Baseline law (the cross-family joint):** all three state words
  share **one baseline coordinate** — in the 56dp cell, baseline at
  cellTop + 38dp — set via the existing shared-baseline helper
  (`drawMixedWordAndMono`, BlockitCoreViews.kt:258). Never centre
  each cell independently: Atkinson 700 cap height ≈0.72em vs
  Anonymous Pro ≈0.68em puts cap tops ~0.5dp apart on a shared
  baseline, which is correct print behaviour; independent centring
  would put the *baselines* ~0.5dp apart, which reads as a step.
  Alignment is by baseline, full stop.
- **Cell labels** (`BLOCK` / `APPS` / `TIME` eyebrows): plate —
  Anonymous Pro 700 10sp +2.0, lh 12, baseline at cellTop + 16dp,
  same x-origin rule in every cell.
- **Flips:** one frame, no fade (Rosa's bulb law) — type inherits:
  a word swap is a re-strike, never a cross-fade.
- **The stamp on the seam:** plate, Anonymous Pro 700 **10sp
  +2.0**, lh 12, secondary ink at rest; baseline set so the glyph
  body sits **on** the seam — baseline at seamY − 3dp (caps/digits
  only, no descenders in the stamp's alphabet, so nothing crosses
  the hairline). Grade words and age digits take the same line,
  same setting — a stamp is one impression, not two styles. (What
  the stamp *says* after the Canary's death is Rosa's re-grade;
  whatever she files prints in this setting or it doesn't print.)

### (d) REFUSAL SENTENCES — type

**CONFIRMED with one amendment.** Suki's tier stands: the failure
tier is **15sp** (F-TENANCY), never plate — a sentence that carries
a decision is body-register truth, and plate is furniture.
Setting, exact: **Atkinson Hyperlegible 700, 15sp, tracking +0.5,
line height 20**, glyph ink, one line, in Rosa's reserved 22dp slot
8dp below the track.
- *Amendment:* Rosa's filing of the slot sentences at lh 22 /
  tracking 0 (`beyond-deck-parts/rosa.md`, interlock passage) is
  superseded — lh 22 / tracking 0 is the Atkinson **400 body-copy**
  setting misapplied to a 700 state sentence. Suki's setting
  (+0.5 / lh 20, `beyond-deck-parts/suki.md:77`) matches the ramp's
  state-word row and governs. One sentence style app-wide.
- *Measure gate:* the slot is 372dp wide; the longest filed
  sentence (Suki's classes F/G, 35 chars ≈ 301dp at this setting;
  Mutter's re-voiced 44 chars ≈ 352dp) fits **one line**. Law: a
  sentence that measures over 372dp returns to its owner for
  re-wording — it never shrinks, never wraps, never drops tracking.
  (The re-voiced strings themselves are Suki's joint this wave;
  the type above is register-identical for old or new wording.)

## 4. CODE SITES

| Site | Change |
|---|---|
| `views/BlockitCoreViews.kt:1057–1063` | delete `subMonoBadge` field |
| `views/BlockitCoreViews.kt:1127–1135` | delete badge draw block |
| `views/BlockitCoreViews.kt:1049, 1113, 1119–1125` | label → cap-word print: `paintMono`, 10sp, `letterSpacing 0.2f` em; fade scoped to hand-drag; string driven by print-state (S0/S2), not a host string |
| `screens/TactileDeckScreen.kt:497–498` | delete both hardcodes; host passes print-state (rest / mesh) only |
| strip view (Slice D build, Rosa's element) | §3c settings; baseline via `drawMixedWordAndMono` pattern (BlockitCoreViews.kt:258) |
| refusal slot (Suki/Rosa) | §3d style constant — one shared style, no per-sentence copies |

No new paints, no new typefaces, no new sizes. Every size used —
10, 15 — is on the ladder.

## 5. PRICE

- Badge death: **−17 lines** (field 7, draw 9, host 1).
- Cap-word state print: **≈ +14** (re-set draw ~6 changed lines,
  state-string selection + mesh flag ~8 — Mutter's ~8, confirmed
  within 2).
- Strip type (my delta only; the strip build is Rosa's): **≈ +30**
  shared-baseline three-cell setting + plate labels + stamp line.
- Refusal type: **≈ +3** (one style constant).
- Measure tests (composed-line ≤ field, both cap strings, longest
  refusal sentence, strip cells): **≈ +20** test lines.
- **Net: ≈ +50 lines incl. tests, −17 of them deletions. 0dp, 0ms
  on every path.** ARM/HOLD untouched; TRUTH is the point.

## 6. RISKS

1. **START slack is thin on Fen's track:** ≈5dp per side in the
   rest free span (248dp track). Font substitution or a tracking
   slip overflows into the sled. Mitigation is the measure gate:
   composed line > span ⇒ the print refuses solid ink (my filed
   not-warranted register) and the build fails its test — never a
   shrink, never an overlap shipped silently.
2. **UNDO occlusion mid-mesh:** the sled rides over the centred
   UNDO line; at mid-mesh it covers the middle sorts. Measured:
   the operative words `TAP` and `UNDO` stay visible at the ends
   at every mesh instant. Acceptable; if Rhea's hit-test makes the
   whole bar the abort target, it is also sufficient.
3. **`letterSpacing` em/d
...[truncated 1982 chars]