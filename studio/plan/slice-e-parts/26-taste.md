# SLICE E — TASTE (Improvers, composer keys): INDEPENDENT PRICE COUNT, E5 + E6 VIEW REGIONS

*Seated by Ilya as price counter, SLICE E build-ready wave. Designs:
Nabe (E5a, 09-nabe), Horiz (E5b, 10-horiz), Fen's adopted E6
(slice-de-parts/fen.md §3–§5; Schlitten's E6 part had not landed in
slice-e-parts at count time — priced off the adopted spec, flagged
below). Trunk read-only, no gradle.*

**METHOD (Waage):** per FILE, net = lines added + lines deleted,
each deletion counted once (a replaced line = 1 add + 1 del).
Counted by reading the actual regions in today's trunk
(`Focus-app/app/src/main/java/com/example/ui/views/BlockitCoreViews.kt`,
1,419 lines; `.../ui/components/MonolithCustomComponents.kt`, 448),
and sizing each change against the file's own idiom (the shipped
setter at SlideToStartView:1048–1054 is 7 lines; the shipped
animator at RotaryWheelView:369–378 is 10 lines for one
ValueAnimator). **Scope: those two files ONLY.** Companions outside
them (MonolithCurves, ViewModel, Deck, MainActivity) are listed as
memos so the roll-up can be compared, never folded into my totals.
Tests priced separately, always.

---

## E5a — D6 HUB LIGHT (all in BlockitCoreViews.kt, RotaryWheelView)

Hub region read: draw :686–764 (shadow/fill/hairline :688–694,
tetrad ring/bars/wells :695–764), fields :398–401, DOWN :767–800,
MOVE :803–810, UP :815–838. No `onSizeChanged`, no SweepGradient,
no depth field exist today — Nabe's premise re-verified.

| Item | Site | Adds | Dels | Net |
|---|---|---|---|---|
| Fields: depth, animator, inside-flag, `paintSpecular`, shader Matrix, 2 gradient slots, travel-gate const | beside :398–401 + paints | 9 | 0 | 9 |
| `onSizeChanged` + dual-mode stop sampling (121 stops × 2, formula-driven) | new override | 22 | 0 | 22 |
| Draw: save/translate, offset math, shader set, core draw, restore | :688–764 restructure | 10 | 0 | 10 |
| `startHubDip()` / `startHubReturn()` (return carries the already-returning guard) | beside closure machinery | 21 | 0 | 21 |
| DOWN: dip start + inside init | :774 region | 4 | 0 | 4 |
| MOVE: ×1.1 crossing sub-branch | :803 branch | 8 | 0 | 8 |
| UP/CANCEL: return-first insertion | :815 branch head | 2 | 0 | 2 |
| Test accessors (2 `internal` getters) | beside fields | 4 | 0 | 4 |
| **E5a in-file total** | | **80** | **0** | **≈80 (band 74–88)** |

Memo, outside scope: PRESS+SEAT interpolators in MonolithCurves.kt
+6 (Nabe's figure, accepted). **E5a all-in ≈86.**

## E5b — ORANGE EXIT (view-region share only)

Slide region read: trail :1102–1109, cap :1133, `tokAccent()`
resolver :139. Components read: chip badge rule :180, session dot
:227.

| Item | Site | Adds | Dels | Net |
|---|---|---|---|---|
| Shaded-accent resolver (per-channel multiply, round-half-up) | BlockitCoreViews beside :139 | 10 | 0 | 10 |
| `exitPhase` property (setter + invalidate, file's own 7-line setter idiom) | SlideToStartView fields | 6 | 0 | 6 |
| B2 trail consumes resolver / terminal tone | :1104–1106 | 2 | 1 | 3 |
| B1 cap consumes resolver / terminal `tokControlFill()` | :1133 | 3 | 1 | 4 |
| B5 chrome session dot born at ink (`scheme.onBackground`) | MonolithCustomComponents :227 | 1 | 1 | 2 |
| B6 badge rule loses its accent branch | MonolithCustomComponents :180 | 1 | 1 | 2 |
| **E5b in-file total** | | **23** | **4** | **≈27 (band 23–31)** |

Memos, outside scope: Deck phase plumbing + B3/B4/B6-callsite
≈17 and the F23 scheme pin ≈22 (Horiz's figures, not re-weighed —
not my files). **E5b all-in on Horiz's out-of-file numbers ≈66.**

## E5 TOTALS

| | Filed | Designers' re-price | **Taste (this count)** |
|---|---|---|---|
| In the two view files | — | — | **≈107** (80 + 27) |
| All-in | ≈130 | ≈146 (Nabe 85 + Horiz 61) | **≈152** (band ≈140–160) |

## E6 — THE SLED GOES HOME (BlockitCoreViews.kt only; zero in Components)

Slide region read whole: class :1046–1227. **BANDED, not
point-priced:** the cap/trail/touch region sits inside Marlowe's
in-flight Slice D slide rewrite — every site below re-pins on the
merged view, and if the rewrite lands Fen's single-writer
`animateThumbTo(target, law)` entry point, item (d) shrinks by
≈8; if it doesn't, E6 builds it. Band ≈40–58, centre ≈49.

| Item | Site (today's pins) | Adds | Dels | Net |
|---|---|---|---|---|
| (a) `isArmedState` setter de-fanged (position reset deleted; colour flag survives) | :1064–1072 | 0 | 1 | 1 |
| (b) `initialFraction` support (field + apply-once before first draw) | fields + onDraw head | 7 | 0 | 7 |
| (c) Return fields: `isReturning`, `returnAnimator`, resume-once flag | :1076–1078 | 3 | 0 | 3 |
| (d) `animateReturnHome()` (650ms, ReturnHome curve, writes `dragFraction`) + cancel helper | beside :1214 | 19 | 0 | 19 |
| (e) Accent trail suppressed while returning | :1102–1109 | 1 | 1 | 2 |
| (f) DOWN pre-emption: cancel return frame-one, freeze, was-returning bookkeeping | :1159–1167 | 5 | 0 | 5 |
| (g) Resume on UP-without-capture (duration 650 × frozen fraction, once) | :1176–1196 | 7 | 0 | 7 |
| (h) `onDetachedFromWindow` cancels animator (mid-return navigation) | new override | 5 | 0 | 5 |
| **E6 in-file total** | | **47** | **2** | **≈49 (band 40–58)** |

**E1 memo (per brief):** the dying seal site at :1182–1192 — the
commit-branch `animateSpringBack(0f)` call at :1191 is **E1's
deletion, credited to E1 (−1; −3 if his seat law also retires the
:1183–1184 snap pair)**. It is excluded from E6's math above; E6
never touches that block.
Memos, outside scope (Fen's figures, accepted): ViewModel
ReceiptState + job ≈45, MonolithCurves ReturnHome +3, Deck host
≈8. **E6 all-in ≈105.**

## DISAGREEMENTS WITH THE FILED PRICES, WITH REASONS

- **E5 filed ≈130 → mine ≈152 all-in.** I confirm the direction
  of both designers' re-prices and go ≈6 past their combined
  ≈146. Inside E5a my internal shifts nearly cancel (gradient
  sampling +5 over Nabe — his 17 cannot hold the dual-mode loop,
  the bearing→gradient conversion and the cos² `when`; helpers
  +5 — the trunk's own one-animator block at :369–378 is 10
  lines, and the return helper carries a guard he priced at
  zero; draw −3, MOVE −4 back). The real delta is E5b's view
  share: Horiz priced the `exitPhase` property **inside** his
  ≈6 for B1/B2 consumption — the file's own setter idiom
  (labelText, :1048–1054) is 7 lines for the property alone,
  before either carrier consumes it. That is +7 of my +6 net
  delta; the rest is rounding. F23's pin (+22) remains the
  single largest underprice in the filed E5, as Horiz showed.
- **E6 filed ≈102 → mine ≈105 all-in: AGREEMENT.** Fen's slide
  row (≈46) survives Waage conversion almost untouched — my
  in-file ≈49 is his number with deletions counted once (+2)
  and the detach-cancel override he implied but didn't line-item
  (+5), offset by the setter de-fang costing 1 deletion, not the
  block replacement his gross assumed. The filed E6 price was
  honest; its risk is not size but the band: it is priced on a
  slide view that is being rewritten under it.

## TESTS (separate, never in the totals above)

E5a ≈95 (Nabe's HubSpecularTest — not disputed) · E5b ≈55
(Horiz T1–T6; T7 is a grep gate, 0 lines) · E6 ≈60 (Fen).
**Tests total ≈210.**

## NEXT

(a) *What I want next:* the same count for E3+E4's view regions —
E4's ≈410 is the wave's biggest unverified number and it touches
this same slide region after Marlowe's merge.
(b) *What I could do unassigned:* re-pin this entire count against
the merged slide tree the day `builds/slide` lands — half an hour,
and it converts every band above into a point price.

— Taste, Improvers · the composer keys
