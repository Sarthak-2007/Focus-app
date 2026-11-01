# FINISHER 2 — TYPOGRAPHY + INK, THE HELD DOOR FACE

Seat: KANTE, Finisher 2, Tor's cell · Issue #11 (Held Door audit fixes)
For: Tor (lead) · Scope: **type and ink only** — every text element on
the Held Door face, brought to gallery standard. Layout geometry is
the Designer's skeleton (`docs/designer-skeleton.md`); I adopt its
slot heights and do not re-litigate them. Nobody compiles.

Ground read, whole, in the issue-11 clone (branch `agent/issue-11`):

- `studio/plan/AUDIT-held-door.md` — FIX-6/FIX-8 rulings, Conflicts 1–3.
- `app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt`
  — `AirlockInterceptOverlay` at :550–720 (all line cites below are
  this file unless named otherwise).
- `app/src/main/java/com/example/ui/components/MonolithCustomComponents.kt`
  — `BlockitActionChip` at :170, `rememberMixedTypographyText` at :95.
- `app/src/main/java/com/example/ui/theme/Color.kt` (Night tokens),
  `theme/Type.kt` (families + named styles), `theme/Theme.kt`
  (`SealedVaultNightColorScheme` at :42).

Method: every contrast below is WCAG 2.x relative-luminance contrast,
computed by me from the token hexes — not quoted. Alpha inks are
blended over the surface they actually sit on (the card,
`NightColorSurfaceBright` #362C27) before the ratio is taken; a ratio
quoted against the wrong surface is a lie I flag where I found one
(Flag K-1). Thresholds used: AA normal text 4.5:1, AA large text
(≥18sp, or ≥14sp bold) 3:1, AAA normal 7:1, non-text edges 3:1.

---

## 0. Verdict in one paragraph

Every ruled value in my slice is **achievable exactly as specced in
the Night palette — no new hex, no new font file is needed.** All
three families ship true Bold files (`Type.kt`: Sora has real
Medium/SemiBold/Bold instances; Atkinson and Anonymous Pro ship
Regular + Bold, which is all this face asks of them), and the ruled
RETURN ink #471602 is already wired into the Night scheme as
`onPrimaryContainer` (`Theme.kt:46`). The blocker is not the palette,
it is the **chip**: `BlockitActionChip` as written cannot render a
single one of the three ruled chip values (height, label register,
RETURN ink) — §4. Two items still fail gallery standard even after
every ruling is applied faithfully: the **footer badge word at
4.28:1** (K-1, the audit trail quotes 5.8:1 against the wrong
surface) and the **card border at 2.94:1 against its own fill**
(K-2, a hair under the 3:1 non-text line, though it passes against
the ground where the edge is actually read). Both are flagged with
options for Tor; neither is silently fixed here.

---

## 1. The finished face — complete type/ink table (the deliverable)

This is the face **as ruled** (FIX-6 + FIX-8 + Designer's skeleton),
element by element, top to bottom. "Effective ink" = the hex after
alpha-blending over the card #362C27. All text sits on the card
unless the row says otherwise.

| # | Element (slot) | Prints | Font | Weight | Size / line-height / tracking | Ink token → hex | α | Effective ink | Contrast | Align | Lines |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 1 | **APP NAME** (H1) — the face's one hero | The intercepted app's real name (§4 of the skeleton for guard/site/sniper variants) | **Sora** | Bold | **24sp** / 28sp / −0.3sp | `NightColorPrimary` #FFE4CF | 1.00 | #FFE4CF | **11.16:1** ✓ AAA | Center | **maxLines 1, Ellipsis** (F-9) |
| 2 | **INTENTION** (H2) | His words **bare** — `ACTIVE INTENTION:` prefix dead | Atkinson Hyperlegible | Normal | **16sp** / 20sp / 0 | `NightColorPrimary` #FFE4CF | **0.92** | #EFD5C2 | **9.68:1** ✓ AAA | Center | maxLines 1, Ellipsis; blank → prints nothing, slot stands empty |
| 3 | **SESSION LINE** (H3 L1) | `SESSION ENDS {HH:mm}` / `SESSION RUNNING · NO SET END` / nothing | Atkinson Hyperlegible | Normal | 12.5sp / 20sp (slot line) / 0 | `NightColorPrimary` #FFE4CF | **0.92** | #EFD5C2 | **9.68:1** ✓ AAA | Center | 1 |
| 4 | **SUB-LINE** (H3 L2) | Break appointment `BREAK AT {HH:mm} OPENS THIS APP` (PEEK doors only) **or** matched domain (site door only) **or** nothing | Atkinson Hyperlegible | Normal | 12.5sp / 20sp / 0 | `NightColorPrimary` #FFE4CF | **0.92** | #EFD5C2 | **9.68:1** ✓ AAA | Center | 1 |
| 5 | **GATE LINE** (Z1, while counting) | `PEEK OPENS IN {M:SS}` — format `%d:%02d` | **Anonymous Pro** | Bold | 15sp / 20sp / 0 | `NightColorPrimary` #FFE4CF | 1.00 | #FFE4CF | **11.16:1** ✓ AAA | Center | 1 |
| 5b | Draining rule under gate line (non-text) | 2dp rule, drains with the wall | — | — | 2dp high, full content width | Fill `NightColorPrimaryContainer` #F47442; track `NightColorPrimary` @ α0.20 | fill 1.00 / track 0.20 | fill #F47442 / track #5E5149 | fill **4.80:1** ✓ (non-text); track 1.78:1 — decorative only (K-5) | — | — |
| 6 | **PEEK LABEL** (Z1, at offer) | `PEEK` — `EMERGENCY PEEK` dead (FIX-8) | **Sora** | Bold | 15sp / 20sp / +0.5sp (`PrimaryButtonTextStyle` register) | `NightColorPrimary` #FFE4CF, on chip fill `NightColorSecondaryContainer` #4A423C | 1.00 | #FFE4CF | **8.08:1** ✓ AAA | Center (chip-centred) | 1 |
| 7 | **PEEK BADGE** (same chip, after one space) | Derived duration: `2m`, `1m30s`, `45s` — m+s honestly, never truncated (FIX-8) | **Anonymous Pro** | Bold | 15sp / 20sp / 0 | Same ink as row 6 (chip's own foreground — the app-wide badge rule) | 1.00 | #FFE4CF | **8.08:1** ✓ AAA | — | 1 |
| 8 | **PRICE** (Z2) | `WHEN THE PEEK ENDS, A {n} MINUTES COOLDOWN STARTS ON THIS APP` (re-timed, FIX-8; singular `1 MINUTE` preserved) — printed **from frame one**, counting included | Atkinson Hyperlegible | Normal | 12.5sp / 17sp / 0 | `NightColorPrimary` #FFE4CF | **0.92** | #EFD5C2 | **9.68:1** ✓ AAA | Center | maxLines 2 (34dp slot = 2 × 17sp exactly) |
| 9 | **WITHHELD LINE** (Z1, held doors) | `PEEK COOLDOWN — AGAIN IN {n} MIN` (ceiling) / `A PEEK IS ALREADY RUNNING` / `PEEK IS OFF` — printed **from frame one**, never wall-gated (SECOND 0) | Atkinson Hyperlegible | Normal | 12.5sp / 17sp / 0 | `NightColorPrimary` #FFE4CF | **0.92** | #EFD5C2 | **9.68:1** ✓ AAA | Center | maxLines 2 inside Z1 |
| 10 | **MEMORY LINE** (Z3) | `{ORDINAL} TIME THIS SESSION` (≥1 prior booked intercept; wording per Designer's flag — Tor ratifies vs the audit's `TONIGHT`) | **Anonymous Pro** | Bold | 10sp / 12sp / +2.0sp (`PillBadgeTextStyle` register) | `NightColorPrimary` #FFE4CF | **0.92** | #EFD5C2 | **9.68:1** ✓ AAA | Center | 1 |
| 11 | **RETURN LABEL** (R, 56dp slab) | `RETURN TO FOCUS` | **Sora** | Bold | 15sp / 20sp / +0.5sp (`PrimaryButtonTextStyle` verbatim) | **`NightColorOnPrimaryContainer` #471602**, on slab fill `NightColorPrimaryContainer` #F47442 | 1.00 | #471602 | **5.37:1** ✓ AA normal + AAA large (K-3) | Center (slab-centred) | 1 |
| 12 | **HONEST EXIT** (X) | `END THE SESSION INSTEAD` — bare text, no fill, no border | Atkinson Hyperlegible | Bold | 12.5sp / 17sp / 0 | `NightColorPrimary` #FFE4CF | **0.92** | #EFD5C2 | **9.68:1** ✓ AAA | Center | 1 |
| 13 | **FOOTER BADGE** (F) | The badge word **alone** (`AIRLOCK INTERCEPTION •` dead): e.g. `BLOCKED`, `UNINSTALL GUARD` | **Anonymous Pro** | Bold | 10sp / 12sp / +2.0sp (`PillBadgeTextStyle` verbatim) | `NightTextColorSecondary` #9C8E84 | 1.00 | #9C8E84 | **4.28:1 ✗ AA normal** (K-1) | Center | 1 |
| 14 | Card border (non-text) | 1dp, 24dp corner, around the card | — | — | 1dp | `NightColorPrimary` #FFE4CF | **0.38** | #827267 | 2.94:1 vs card fill / **3.97:1 vs ground** (K-2) | — | — |
| — | Card fill / ground (surfaces, for reference) | — | — | — | — | Card `NightColorSurfaceBright` #362C27; ground `NightColorBackground` #171412 | 1.00 | — | Card vs ground only **1.35:1** — the border (row 14) is what actually defines the card's edge | — | — |

**Removed from the face (negative table — these inks no longer exist):**

| Removed element | Was | Why it dies |
|---|---|---|
| Eyebrow `AIRLOCK INTERCEPTION • {BADGE}` (:606) | Atkinson Bold 11.5sp, cream @ 1.00, 11.16:1 | FIX-6: three seats killed it; the badge word alone survives at footer tier (row 13). Its contrast was never the problem — its rank was: a second shout above the hero. |
| Hero countdown numeral (:636) | Anonymous Pro Bold **88sp**, cream @ 1.00 | Conflicts 1: dies as hero, 5 seats to 1. At 88sp it outranked the name (24sp) by 3.7× and gated only surrender's neighbour. Its figures survive, demoted and captioned, as row 5. |
| `ACTIVE INTENTION:` prefix (:648) | Part of the intention line at 12.5sp @ 0.80 | FIX-6 / Halbwach's slot: his words print bare, directly under the name, at 16sp (row 2). |
| `EMERGENCY PEEK` string (:665) | Chip word label | FIX-8 / Conflicts 4: the studio's one-word law. The chip is now verb + derived fact (rows 6–7). |

---

## 2. Today vs ruled — the delta, with code cites

Every row below is a defect in the **current** overlay code measured
against the ruling it violates. The builder fixes type/ink by this
table; nothing here asks for a new decision.

| Element | Today (code) | Ruled (§1 row) | Δ |
|---|---|---|---|
| App name | Atkinson Bold 24sp, cream @ 1.00 (:615) — **no `maxLines`, no overflow, no `textAlign`** anywhere in the file | Sora Bold 24sp, 1 line + ellipsis, centred (row 1) | Family, overflow, alignment. Size and ink already right. Uncapped, a long name wraps and pushes the whole centred column — it is a co-author of F-2's reflow, not a bystander. |
| Intention | `ACTIVE INTENTION: {…}` at **12.5sp @ 0.80** (:648), printed **below the 88sp numeral**, i.e. fourth in the hierarchy | Bare at **16sp @ 0.92**, second on the face, directly under the name (row 2) | Prefix, size (+3.5sp), alpha, position. Today the man's own stated reason for the session reads smaller and fainter than the session line — hierarchy inverted against its meaning. |
| Session line | Atkinson 12.5sp @ **0.80** (:629) | Same size, @ **0.92** (row 3) | Alpha only. |
| Hero numeral | Anonymous Bold **88sp** (:638) | Dead; replaced by the 15sp gate line (row 5) | −73sp and a caption. |
| RETURN | `BlockitActionChip(isAccentArmed = true)` (:655): **42dp** high, label at **12sp** Atkinson Bold, ink **#FBF1EC hardcoded** in the chip (`MonolithCustomComponents.kt`, `isAccentArmed` fg branch), border `scheme.outlineVariant` | **56dp** slab, **Sora Bold 15sp**, ink **#471602**, border cream @ 0.38 (row 11) | Height, family, size, ink, border — all five. Contrast 2.55:1 → 5.37:1 (§3). |
| PEEK chip | `BlockitActionChip` (:665): **42dp**, label `EMERGENCY PEEK` at 12sp, badge from the truncating `peekBadgeText` (:257, `"${s/60}m"` — 90s prints `1m`), fill `scheme.surfaceVariant` #100E0C, ink `scheme.onBackground` #D2C6BB | **48dp**, `PEEK` Sora Bold 15sp + honest m+s badge, fill #4A423C, ink cream (rows 6–7) | Height, wording, family, size, fill, ink, badge derivation. (Today's chip contrast, 11.50:1, was fine — everything else about it was wrong.) |
| Price | `TAKING THE PEEK STARTS A …` at 12.5sp @ **0.80** (:677), composed **only after** countdown 0 | Re-timed wording, @ **0.92**, centred, from frame one (row 8) | Wording (truth), alpha, alignment, timing. |
| Withheld lines | 12.5sp @ **0.80** (:694/:701/:708), composed **only after** countdown 0 | @ **0.92**, centred, from frame one (row 9) | Alpha, alignment, timing. A refusal that appears 5s late reads as a decision the door made, not a fact it knew. |
| Gate line | **Does not exist.** The wall is the bare 88sp numeral + a free-running local counter (:562) | `PEEK OPENS IN {M:SS}`, Anonymous Bold 15sp, anchored derivation (row 5) | New element; the counter's anchoring is FIX-5's, the register is mine to pin: 15sp puts it at statement rank — level with the two verbs, below the hero, above the body. |
| Memory line, honest exit, footer badge | **Do not exist** (the eyebrow carries the badge today, at the top, at 11.5sp) | Rows 10, 12, 13 | New / relocated elements. |
| Card border | Cream @ **0.20** (:598) | Cream @ **0.38** (row 14) | Alpha. 1.78:1 → 2.94:1 vs fill (K-2). |
| Alignment (global) | **No `Text` in `AirlockInterceptOverlay` sets `textAlign`.** The Column's `CenterHorizontally` centres each text's *box*; a wrapped line's *lines* still set start-aligned (Compose default). On every 1-line print this is invisible; on the 2-line price it is a ragged left edge inside a centred block — the worst of both. | `textAlign = Center` on **every** multi-line-capable text (F-11): rows 2, 3, 4, 8, 9, 12 — and rows 1, 5, 10, 13 for uniformity | This is the easiest ruling to "implement" and still miss: adding the parameter is the whole fix, and nothing in the current file has it. |
| Entry fade (type-adjacent) | `fadeIn(specB())` = 420ms ghost (:560) | `fadeIn(specA(180))` (FIX-7b) | Not ink, but it is how the ink arrives: at 420ms the face's text spends its first craving-second at partial alpha — every contrast in §3 is temporarily halved by the fade. 180ms is the ruled cure; I record it here because a finisher's contrast table that ignores the entry fade overstates the face he actually sees. |

---

## 3. Contrast ledger — every number, computed

All ratios computed by me from the token hexes (WCAG luminance:
cream #FFE4CF L=0.8125; card #362C27 L=0.0273; ground #171412
L=0.0073; accent #F47442 L=0.3212; #471602 L=0.0192;
#4A423C L=0.0568; #9C8E84 L=0.2808). Alpha rows are blended first.

| Pairing | Ratio | AA normal (4.5) | AAA normal (7) | Note |
|---|---|---|---|---|
| Name / gate / PEEK-on-cap cream @ 1.00 on card or cap | 11.16 / 8.08 | ✓ | ✓ | The face's working ink is unimpeachable. |
| All body lines @ **0.92** → #EFD5C2 on card | **9.68** | ✓ | ✓ | Rows 2, 3, 4, 8, 9, 10, 12. |
| Same lines @ 0.80 (today) → #D7BFAD on card | 7.72 | ✓ | ✓ | Honest note: **0.80 was not a WCAG failure** — it already clears AAA. The raise to 0.92 is a gallery/1-AM-legibility ruling (F-10), worth +1.96 of ratio and, more importantly, uniformity: after the fix there is exactly one body alpha on the face, so no line reads as less true than another. Do not let anyone re-sell F-10 as an accessibility rescue; it is a hierarchy cure. |
| **RETURN ruled: #471602 on #F47442** | **5.37** | ✓ | ✗ (✓ AAA large, 4.5) | Matches the audit's F-3 figure exactly — the ruling's arithmetic checks out. See K-3. |
| RETURN today: #FBF1EC on #F47442 (chip hardcode) | **2.55** | ✗ | ✗ | Matches the audit exactly. Fails even AA *large* (3:1). The lowest contrast on the face, on the one word he must hit — the single worst ink defect in my slice. |
| (For the record) cream #FFE4CF on the accent | 2.32 | ✗ | ✗ | Worse than today's chip ink. Any "just use the face's cream on the slab" shortcut goes backwards. |
| Footer #9C8E84 on **card** | **4.28** | **✗ by 0.22** | ✗ | **K-1.** On ground it is 5.78 — but the footer does not sit on the ground. |
| Border cream @ 0.38 → #827267 | 2.94 vs card fill; **3.97 vs ground** | n/a (non-text, 3:1) | — | **K-2.** |
| Border cream @ 0.20 (today) → #5E5149 | 1.78 vs fill; 2.40 vs ground | n/a | — | The card's edge was close to invisible: card-vs-ground fill contrast alone is only 1.35:1, so the @ 0.20 border was carrying the edge at 1.78–2.40. F-5's raise is the difference between a card and a smudge. |
| Draining-rule fill #F47442 on card | 4.80 | n/a ✓ | — | The accent's second and only other appearance; it echoes the RETURN slab without competing (2dp vs 56dp). |
| PEEK chip fill #4A423C vs card | 1.38 (fill-to-fill) | n/a | — | The chip's edge is carried by its own cream @ 0.38 border (blended #8F8074 over the fill), not by fill contrast — same discipline as the card. Correct, provided the builder actually gives the chip that border (today's chip border is `outlineVariant`, a grey at α≈0.46 — a different, colder edge; §4). |

---

## 4. Achievability — palette yes, chip no

**Palette (all ✓):** every ruled ink is an existing Night token —
`NightColorPrimary`, `NightColorPrimaryContainer`,
`NightColorOnPrimaryContainer`, `NightColorSecondaryContainer`,
`NightTextColorSecondary` (`Color.kt`, Night table). Every ruled
family/weight resolves to a real font file (`Type.kt`): Sora Bold,
Atkinson Normal + Bold, Anonymous Pro Bold. Two named styles can be
reused verbatim: `PrimaryButtonTextStyle` (Sora Bold 15sp, +0.5sp)
for both slab labels, `PillBadgeTextStyle` (Anonymous Bold 10sp,
+2.0sp) for footer + memory. The Night scheme already maps
`onPrimaryContainer = #471602` (`Theme.kt:46`) — the correct RETURN
ink is one `scheme.onPrimaryContainer` read away.

**The chip blocks three rulings (builder must not inherit it):**
`BlockitActionChip` (`MonolithCustomComponents.kt:170`) hardcodes
(1) height **42dp** — cannot be RETURN's 56dp or PEEK's 48dp;
(2) label size **12sp** via `rememberMixedTypographyText`, word in
Atkinson Bold — cannot be Sora Bold 15sp, and its badge colour
logic, while correct in spirit (badge = chip foreground), is fed
the wrong foreground; (3) the `isAccentArmed` foreground
**#FBF1EC hardcoded** — the exact 2.55:1 ink F-3 kills; reading
`scheme.onPrimaryContainer` instead would fix the ink but not the
height or the register. Conclusion, agreeing with the Designer
(§5.3/§6 of the skeleton): the door's two slabs are **door-local
composables** (or a parameterised chip: height + text style + ink
as parameters). What must not happen is the quiet outcome —
"RETURN, but through the chip" — which would ship all three
defects under a FIX-6 heading. The stationarity test should also
assert the slab's *ink token*, not only its bounds.

**Line-height discipline (finisher's pin, where the skeleton gives
slots but not line heights):** I have set line heights in §1 so
that text fills its slot exactly — price 2 × 17sp = its 34dp slot;
session lines at 20sp = their 20dp slot lines; name 28sp inside
32dp. Current code sets **no lineHeight on any Held Door text**,
so today's leading is font-default and slot arithmetic is
unverifiable. Builder: take §1's lineHeight column as spec, not
suggestion — an unpinned line height is a jitter source that
survives every other fix.

---

## 5. Jitter audit — the gate line and everything that ticks

Schliff's A-4 (tabular figures, zero jitter), recorded in the audit
for exactly this line, is **satisfiable by construction** — with
three conditions the builder must not fumble:

1. **Family does the work.** Anonymous Pro is a true monospace:
   every glyph, digit or colon, has the same advance width. No
   `fontFeatureSettings = "tnum"` is needed or wanted — tabular
   figures are the font's only figures. The gate line in any other
   family (or the session line's `HH:mm` promoted into a ticking
   role) would break this; the session line is safe only because
   its instant never changes during a visit.
2. **Format does the rest.** `"%d:%02d"` (minutes unpadded, seconds
   zero-padded). Across the taper's whole lawful range (5–10s) the
   print is always 4 glyphs — `0:05` … `0:10` — so even the string
   *length* never changes; centred in a full-width slot, the line's
   centre is bit-stable per second. A `"%d:%d"` slip (unpadded
   seconds) reintroduces a half-glyph jump at 0:10 → 0:09 — the
   format string is a gallery detail, and it belongs in a test:
   `gateText(9) == "0:09"`, `gateText(10) == "0:10"`, equal length.
3. **Derivation, not counting.** The figures derive from the
   anchored end-mono at each recomposition (FIX-5). The current
   free-running `delay(1000)` loop (:566) drifts against the wall
   clock and can double-step under recomposition — a *temporal*
   jitter no font can cure. Mine to flag, the builder's to anchor.

Residual jitter elsewhere, judged honestly: the withheld cooldown
line (row 9) re-centres when its minute count changes width
(`…IN 10 MIN` → `…IN 9 MIN`) — once a minute, in a proportional
family, on a line that is not being aimed at. **Accepted**: it is
a fact changing, not a figure trembling. The price and memory
lines never change during a visit. The app name never changes.
RETURN never moves (skeleton law) and never re-labels. After the
fix, the only per-second motion on the face is the gate figures
changing in place and the rule draining — which is the correct
inventory for a door that is supposed to feel still.

---

## 6. Hierarchy audit — one hero, honestly ranked

Finished rank order by size × weight × ink strength:
**Name 24sp Sora Bold (11.16:1) → Intention 16sp (9.68:1) →
RETURN / PEEK / Gate 15sp Bold → Body 12.5sp (9.68:1) →
Exit 12.5sp Bold → Memory / Footer 10sp tracked.**
That is the audit's intent made visible: what he opened outranks
what he wants, which outranks what it costs, which outranks the
door's furniture. Today's face inverts it twice (88sp numeral over
the name; intention at body size below the numeral).

Two proximities to watch, flagged not re-ruled — the audit ruled
both sizes explicitly, so I record the tension for Tor's eye:

- **15sp vs 12.5sp is a 1.20× step**, inside the type ladder's
  "no two tiers within 1.4×" discipline (`Type.kt` ladder note).
  On this face the step is carried by weight (Bold vs Normal),
  ink (full vs 0.92), and form (slabs vs bare lines), not by size
  alone — it reads cleanly. But it means a future change that
  bolds a body line or regularises a label collapses a tier.
  The table in §1 is the contract that prevents it.
- **Intention 16sp vs session line 12.5sp is 1.28×**, same
  discipline, same mitigation (slot order + the intention's
  position directly under the hero). Accepted for the same reason.

No green appears anywhere in §1 (checked token by token — the
Night palette is zero-green by construction and I introduced no
hex of my own). No second accent fill: the accent appears exactly
twice, as RETURN's fill and the rule's fill, at a 28:1 area ratio.

---

## 7. Flags for Tor (still failing / needs a ruling)

- **K-1 — FOOTER CONTRAST (the one hard failure left).**
  `NightTextColorSecondary` #9C8E84 on the card measures
  **4.28:1**, failing AA for 10sp text by 0.22. The Designer's
  skeleton (§2) calls this token the "5.8:1 tier" — that figure is
  its contrast against the **ground** #171412 (I compute 5.78:1
  there), but the footer prints on the **card**. Options, in my
  order of preference: (a) accept 4.28:1 as a *waivered* quiet
  tier — the badge word is redundant furniture (the door's type is
  already known from context), never the sole carrier of a fact;
  if Tor waivers it, the waiver should be written on the audit so
  no future finisher "discovers" it again; (b) step the footer to
  `NightTextColorPrimary` #D2C6BB (8.11:1 on card) — passes AAA,
  still visibly quieter than cream, costs the face its only
  use of the secondary token; (c) cream @ 0.92 (9.68:1) — passes,
  but the footer stops being a footer. I recommend **(a) with the
  waiver recorded, falling back to (b)** if Tor holds AA absolute.
  What I do not recommend is leaving the 5.8:1 figure standing in
  the record uncorrected.
- **K-2 — BORDER AT 2.94:1 vs its own fill.** Cream @ 0.38 blended
  over the card is 2.94:1 against the card fill — 0.06 under the
  3:1 non-text guideline — while reading **3.97:1 against the
  ground**, which is the comparison the eye actually makes at the
  card's edge. Verdict: **gallery-pass, guideline-marginal.** The
  ruled 0.38 stands; I flag it so the record is honest that F-5
  lands a hair under one line and comfortably over the one that
  matters perceptually. If a future pass wants both, α 0.40
  computes to ≈3.1:1 vs fill — a 0.02 alpha step, not a redesign;
  Tor's call, not mine to slip in.
- **K-3 — RETURN AT 5.37:1 is the palette's ceiling, and that's
  fine.** It passes AA normal and AAA large; it cannot reach AAA
  normal (7:1) in any palette-legal ink — the only darker token,
  `NightColorOnPrimary` #1B110D, computes 6.55:1 on the accent and
  is the wrong semantic (it is the ink for cream fills, not for
  the container). #471602 is the scheme's own `onPrimaryContainer`
  pairing; 5.37:1 at 15sp Bold on a 56dp slab is gallery standard
  for a primary action. Closed — recorded so nobody reopens it
  chasing 7:1 off-palette.
- **K-4 — FONT SCALE 1.3 (device verification, not paper).** The
  Designer already files this for the price (§11.8 of the
  skeleton). My slice adds the **intention**: 16sp at 1.3 scale is
  effectively 20.8sp in a 24dp slot with `maxLines = 1` — it will
  ellipsis earlier and sit tight; it must clip, never grow the
  card. And the **name** at 1.3 scale ellipsises more aggressively
  — correct per F-9, but Ren's device pass should eyeball one
  long-named app (e.g. a 20+ character name) to confirm the
  ellipsis reads as a name, not a truncation accident.
- **K-5 — RULE TRACK IS DECORATIVE, SAY SO IN CODE.** Track @ 0.20
  (1.78:1) is below any legibility line by design — the figures
  (row 5) carry the fact redundantly, so the track is pure feel.
  Builder: comment it as decoration on the draining rule, or a
  future audit will charge it as a contrast defect and someone
  will "fix" the door's quietest element into a stripe.
- **K-6 — SIBLING NOTE (out of my face, one line).** The Disarm
  overlay in the same file still wears border @ 0.20 (:~753) and a
  body line @ 0.75 — if Tor's cross-face pass applies F-5/F-10
  uniformly, those are its two ink deltas. Its own audit seat
  owns the ruling; I only mark that my table must not be read as
  covering it.

---

## 8. Builder's type/ink checklist (extract of §1, for the DECLARED close)

1. Name: Sora Bold 24sp/28sp, cream @ 1.00, Center, `maxLines = 1`,
   `TextOverflow.Ellipsis`. No Atkinson name survives on this face.
2. Intention: bare string, Atkinson Normal 16sp/20sp, cream
   @ 0.92, Center, 1 line + ellipsis.
3. Every body line (session, sub-line, price, withheld): Atkinson
   Normal 12.5sp, cream @ **0.92**, `textAlign = Center` **set
   explicitly on the Text** — Column alignment does not count.
4. Gate: `PEEK OPENS IN ` + `"%d:%02d"`, Anonymous Bold 15sp/20sp,
   cream @ 1.00, Center; rule 2dp, fill #F47442, track cream @ 0.20
   (commented decorative, K-5).
5. RETURN: door-local 56dp slab, fill #F47442, label
   `RETURN TO FOCUS` in `PrimaryButtonTextStyle` with colour
   `scheme.onPrimaryContainer` (#471602). **Not** through
   `BlockitActionChip` as-is (§4). testTag preserved.
6. PEEK: door-local 48dp chip, fill #4A423C, border cream @ 0.38,
   label `PEEK` (PrimaryButton register, cream) + badge
   (Anonymous Bold 15sp, same ink) from the honest m+s derivation.
   The string `EMERGENCY PEEK` exists nowhere in the tree after.
7. Memory + footer: `PillBadgeTextStyle`; memory ink cream @ 0.92,
   footer ink per Tor's K-1 ruling.
8. Exit: Atkinson Bold 12.5sp, cream @ 0.92, Center, bare text in
   its 36dp slot.
9. Card border: cream @ **0.38**. Entry fade `specA(180)`.
10. Assertions worth encoding (paper, for Ren's compile): RETURN
    ink token == `onPrimaryContainer`; no Held Door text lacks an
    explicit `textAlign` where §1 requires one; gate format length
    stable across 5–10s; badge `90 → "1m30s"`.

— KANTE, Finisher 2, Tor's cell, Issue #11. Type/ink only; nothing
compiled, nothing built. All contrast figures are my own
computation from the Night tokens in the issue-11 clone.
