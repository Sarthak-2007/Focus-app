# FEEL-5 — The Instant-Read Test, and the pill graded against it

**Cell:** issue-16 (the pill) · **Author:** Feel-5 · Instant Read (issue-16 cell)
**Sources graded (read only, nothing compiled or run):**
`builds/pill/Focus-app/app/src/main/java/com/example/service/PillFace.kt`,
`PillTheme.kt`, `PillController.kt`
**Scope:** the PEEK face — the floating 2-minute-peek countdown. Session and
Break faces are graded only where sharing the Peek face's skin makes the Peek
face misreadable (IR-2, IR-5). The derivation's *truth* is not in question;
this document is only about whether a distracted person *reads* it in time.

---

## 0. Verdict up front

**Current grade: FAIL — 2 of 12 criteria pass.**

The pill tells the truth (one derivation, ceiling-rounded, never dies a beat
early — `PillFace.kt:119-125`) and it is one compact object. Everything else
about instant reading fails: the identifying label is 9 sp, the time is a
colon clock that has to be converted, there is no analog/urgency channel at
all, and none of the three things a touch can do — tap the body, drag it,
tap the ✕ — is signalled anywhere on the face. On an unlimited-stare
screenshot most of it can eventually be decoded; on the 0.5 s flash that is
this object's actual job, it cannot. Fixes in §3: ~70 lines, one file doing
almost all of the work, no schema, no new service.

---

## 1. THE INSTANT-READ TEST (the test itself)

### 1.1 What "instant" means, operationally

The user is inside another app, mid-distraction. The pill gets **one
fixation, ≈0.5 s**, at arm's length, over an arbitrary app background
(bright or dark), possibly in peripheral vision first. After that one
glance the user must know three things — the brief's three questions:

1. **What it is** — a peek countdown for a named app, not a session timer,
   not a break, not a notification, not the other app's own UI.
2. **How long is left** — to within **±10 s**, plus the urgency band
   (comfortable / under a minute / under 30 s / about to die).
3. **What happens if touched** — body-tap vs drag vs ✕, predicted *before*
   touching, correctly enough that nobody triggers an irreversible verb
   believing it was a harmless one.

Anything that needs a second fixation, reading small print, converting
units, or remembering a manual fails by definition — the second glance is
the distraction winning.

### 1.2 How a reviewer runs it (screenshot + flash, no device lab needed)

**Materials:** a screenshot (or render) of each state: Peek at 118 s, 83 s,
47 s, 24 s, 8 s; Break and Session faces at any value; each composited over
one bright app background and one dark one.

**Flash protocol (the binding one):**

1. Seat the reviewer facing away. Show one state for **0.5 s** (a timer,
   a slide auto-advance, a tab flick — method doesn't matter, duration does),
   then blank.
2. Within 5 s of blanking, the reviewer answers, in order, without
   revising earlier answers:
   a. What was that object, and for which app/mode?
   b. How many seconds were left (a number, not "about a minute")?
   c. Which band: >60 s · 30–60 s · 10–30 s · <10 s?
   d. If you tap its body, what happens? If you drag it? If you tap the ✕?
3. Repeat for all states, shuffled. **5 reviewers** (or 5 separated sittings
   by fewer people, states re-shuffled, no coaching between).
4. **Screenshot protocol (the floor, not the goal):** same questions with
   unlimited staring. Passing this but failing the flash = "legible,
   not instant" = **FAIL overall**. The flash is the test.

**Pass bar per criterion:** ≥4 of 5 reviewers correct on ≥4 of 5 Peek
states, on *both* backgrounds, unless the criterion states its own bar.

### 1.3 The checklist (score each PASS / FAIL)

**A — Identity**

- **IR-1 · Object + app in one glance.** Reviewer names "peek countdown"
  (any wording: peek / temporary pass / fuse) *and* the app, unprompted.
  "A timer" is not a pass. Bar: standard.
- **IR-2 · Mode discrimination.** Shown Peek, Break, and Session flashes
  interleaved, reviewer never confuses which clock they saw. The faces
  must differ by more than small-print wording: word + at least one of
  {colour, shape, icon, position of a mark}. Bar: zero confusions in 5
  interleaved rounds.

**B — Time**

- **IR-3 · Absolute read ±10 s.** |reported − actual| ≤ 10 s. No conversion
  step may be required beyond reading the printed number(s): if the face
  prints `m:ss`, the reviewer must still land within 10 s in 0.5 s —
  in practice this is where colon clocks die (see §2). Bar: standard.
- **IR-4 · Urgency band without digits.** Reviewer gets the band (1.2c)
  right even when the digits are masked in a second run of the same
  screenshots (digits blurred, everything else intact). Requires a
  pre-attentive channel: depleting bar/ring/fill, shrinking form, or a
  threshold colour change. Digits alone cannot pass this by construction.
- **IR-5 · Unit + direction unmistakable.** Reviewer states the number is
  *seconds remaining* (not elapsed, not clock time, not hours:minutes)
  without being told. A unit or a "left"-type word must be on the face,
  or the format must be incapable of any other reading.

**C — Consequence of touch**

- **IR-6 · Body tap.** Reviewer predicts the actual body-tap verb
  (return to Monolith / focus) from the face alone. Some mark on the body
  — glyph, arrow, word — must point at that verb.
- **IR-7 · Drag.** Reviewer predicts the pill can be dragged/moved, and
  does *not* predict that dragging triggers the body-tap verb (or vice
  versa). A grip/handle affordance must exist. (The implemented split is
  a 6 dp travel threshold, `PillController.kt:336` — invisible by nature,
  so the *face* has to teach it.)
- **IR-8 · The ✕.** Reviewer predicts the actual ✕ verb — **ends the peek
  early** (lease zeroes, cooldown re-anchors: `PillController.kt:285-292`) —
  and specifically does *not* predict "dismisses/hides the pill," which is
  what ✕ conventionally means and what this ✕ does not do. Bar: 5 of 5 —
  this verb is irreversible-in-the-moment, so a single confident misread
  fails the criterion.

**D — Legibility floors (checked on the screenshot, arithmetic not opinion)**

- **IR-9 · Label floor.** Identifying text ≥ **11 sp**, contrast ≥ 4.5:1
  against the pill's *own* background at its *actual* alpha over a white
  app (worst case, since the bg is translucent — `PillTheme.kt:37`,
  `0xE6` ≈ 90 % opaque).
- **IR-10 · Time floor.** Primary figures ≥ **20 sp**; typeface tabular/
  monospace (no width jitter between seconds); contrast ≥ 7:1 vs pill bg.
  The figures are the object; they get the biggest type on it, unshared.
- **IR-11 · One cluster.** Identity, time, and controls all inside one
  fixation-sized cluster (≤ ~160 × 64 dp), time reachable without reading
  past the label, controls visually separable from the read-only parts.
- **IR-12 · Touch floors.** Every tappable element ≥ **48 × 48 dp** hit
  area (Android floor), regardless of glyph size; read-only parts must
  not look tappable and tappable parts must not look read-only.

**Scoring:** all 12 = PASS (instant-read). Any FAIL in A or C = FAIL
overall regardless of the rest — a beautiful clock you misidentify, or
whose ✕ you misjudge, has failed its job. FAILs in B/D = FAIL overall
with the §3 fix list as the re-test entry ticket.

---

## 2. GRADING THE CURRENT PILL

What is actually rendered (Peek face) — `PillController.kt:175-177`,
`buildWindow()` at `:183-226`: a horizontal pill, corner radius 999 dp
(`:219`), padding 14/8/12/8 dp (`:216`), background `#1F1A16` at 90 %
(`PillTheme.kt:37`), 1 dp border. Inside: a vertical column — label on
top, time below — plus a ✕ to the right, visible only on Peek. Total
height ≈ 46 dp (8+8 padding + ~11 dp label + ~19 dp time). Born at the
right edge, 22 % down the screen (`:237`, post-layout snap in `:248-254`),
position not persisted.

| # | Grade | Evidence |
|---|---|---|
| IR-1 Identity | **FAIL** | Peek label is the app name alone, uppercased — `labelText?.text = face.appName.uppercase()` (`:175`). No word PEEK / PASS / FUSE anywhere, no icon. "INSTAGRAM 01:32" over Instagram reads as the app's own furniture or a session label as easily as a fuse. And the one identifying element is the 9 sp tier (IR-9), so in the flash it isn't read at all. |
| IR-2 Mode discrimination | **FAIL** | All three faces share identical layout, typeface, sizes and palette; only the 9 sp label text differs: `SESSION · BLOCK n/m` / `SESSION · ELAPSED` (`:157-160`), `BREAK` (`:170`), app name (`:175`). Same `formatClock` for all (`:165,171,176`). Small print is the *only* discriminator — exactly what a flash doesn't deliver. |
| IR-3 Time ±10 s | **FAIL** (flash) / pass (stare) | The number is *true*: ceiling rounding, dies at the end instant, never early (`PillFace.kt:119-125`); recomputed from stored ends each tick, both routes print the same derivation (`PillFace.kt:7-19`). But `formatClock` is `String.format("%02d:%02d", s/60, s%60)` (`:383-386`): at 83 s the face prints `01:23` and the reviewer must strip a leading zero, parse a colon, and multiply-add (60+23) inside 0.5 s. That is a conversion, and conversions blow the ±10 s bar under flash conditions. Unlimited staring passes — which §1.2 explicitly scores as FAIL. |
| IR-4 Urgency band, digits masked | **FAIL** | No analog channel exists. No bar, ring, fill, or shrink anywhere in `buildWindow()`; colours are set from the static palette per render (`:149-155`) and never vary with `remainingSeconds`. Palette has no urgency tier at all (`PillTheme.kt:36-42`). Mask the digits and the pill at 118 s is pixel-identical to the pill at 8 s except the label. |
| IR-5 Unit + direction | **FAIL** | `MM:SS` with no unit, no "left", no arrow. `02:00` at lease start is equally readable as 2 hours, a clock time, or elapsed. The stopwatch face even prints the same format with a `+` prefix (`:163`) — remaining and elapsed share one costume, separated by one small character. |
| IR-6 Body tap | **FAIL** | Body tap = return to Monolith (`onBodyTap()`, `:298-310`, fired from `DragListener` `:345-346`). The body carries no glyph, arrow, or word suggesting return/go-back/focus. Nothing to predict from; reviewers will guess "opens the app it names" or "does nothing." |
| IR-7 Drag | **FAIL** | Draggable with edge-snap (`:336-368`) but no grip, dots, handle, or edge hint anywhere on the face. Worse, tap-vs-drag is decided by an invisible 6 dp Manhattan threshold (`:336`): a sloppy tap becomes a drag and vice versa, and the face teaches neither. |
| IR-8 The ✕ | **FAIL** | Two stacked failures. (a) *Meaning:* the glyph is a bare `"✕"` (`:205`) — the universal "dismiss this UI" — but the verb is `recordPeekEnded` (`:289-292`): the peek ends, the cooldown re-anchors. That is the single most consequential misread available on this object, and the face invites it. (b) *Target:* 15 sp glyph (`:207`) + padding 10/6/2/6 dp (`:209`) ≈ **21 × 30 dp hit area** — under half the 48 dp floor on the width axis. One credit: it exists only on Peek (`:177` vs `:167,172`) and is the one accent-coloured element (`PillTheme.kt:41`) — the right instinct, under-built. |
| IR-9 Label floor | **FAIL** | Label 9 sp (`:195`) — below the 11 sp floor, below Android's practical small-text floor entirely. Its colour, muted `#B9ACA0` (`PillTheme.kt:40`), is ≈7.8:1 against the *opaque* bg on paper, but the bg is 90 % alpha over an unknown app; over a bright app the effective contrast and the 9 sp size together put the label outside a 0.5 s read. The face's identity is printed in its least readable tier — the hierarchy is inverted. |
| IR-10 Time floor | **FAIL** (size) / pass (face + contrast) | Time 16 sp (`:189`) vs the 20 sp floor — the primary datum is mid-sized and shares the pill with a label row that eats ~40 % of its height. What passes: Anonymous Pro monospace (`PillTheme.kt:52-61`) — tabular, no jitter — and cream `#FFE4CF` (`PillTheme.kt:39`) at ≈14.2:1 vs bg. Keep both; grow the size. |
| IR-11 One cluster | **PASS** | One `WRAP_CONTENT` object (`:228-231`), one column + one control, everything inside ~46 dp of height. No scanning, no second object, notification Route B prints the same derivation so the shade can't contradict the glass. The compactness instinct is right — the budget is just spent on the wrong tier (label row over figure size). |
| IR-12 Touch floors | **FAIL** | ✕ hit area ≈21 × 30 dp (see IR-8). Body is large enough but is simultaneously tap-target, drag-target, and read-only display with no visual separation of the three roles — the inverse failure of the same criterion. |

**Tally: PASS — IR-11, and the truth/monspace/contrast sub-parts of
IR-3/IR-10. FAIL — IR-1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12. Overall: FAIL.**

The summary in one line: *a true clock, printed too small, in the wrong
format, wearing no name and no instructions.*

---

## 3. THE SMALLEST CHANGES THAT PASS ALL 12

Design rule for the fixes: the machinery does not move. Every change is in
`PillController.kt`'s render/build code, one added field in `PillFace.kt`,
and two added palette colours in `PillTheme.kt`. No DB, no service, no
derivation change. Words are rationed: on an object this small only three
words earn their place — **PEEK** (what it is), **left** (direction; the
unit rides with the number, see Fix 1), and **END** (what the ✕ does).
Everything else is glyphs and geometry.

### Fix 1 — Seconds, not a colon clock, on the Peek face (IR-3, IR-5)

Peek only: print total seconds as one integer + unit suffix:

```
timeText = "${face.remainingSeconds}s"      // "118s" … "9s" — one number, no conversion
```

- One number needs no parsing and no multiply-add; ±10 s in 0.5 s becomes
  reading, not arithmetic. The `s` kills the hours/clock ambiguity (IR-5);
  direction is carried by Fix 2's label word.
- Break keeps `m:ss` (breaks run longer; out of this cell's scope) — but
  that *helps* IR-2: Peek no longer shares Break/Session's format.
- Cost: 1 line changed at `:176` + a `peekFormat()` beside `formatClock`
  (`:383`). ~4 lines. Zero risk — `remainingSeconds` already exists
  (`PillFace.kt:51-55`).

### Fix 2 — The label earns one word, at a readable size (IR-1, IR-2, IR-9)

```
labelText.text = "PEEK · ${face.appName.uppercase()} · LEFT"   // or "PEEK — <APP>"
label size: 9sp → 11sp                                          // :195
```

- "PEEK" is the identity; the app name already has its place; "LEFT"
  states direction (with Fix 1's `s`, IR-5 is doubly closed). If width
  pinches on long app names, drop "· LEFT" before dropping "PEEK" —
  direction is then still implied by Fix 3's draining bar.
- Ellipsise the app name at 12 chars so the pill can't balloon
  (`WRAP_CONTENT`, `:229` currently lets a long name widen the object).
- Cost: ~3 lines at `:175,195`.

### Fix 3 — A draining bar: the analog channel (IR-4, IR-2)

Add one 3 dp bar under the column, inside the pill, whose filled width =
`remainingSeconds / leaseTotal`. Needs the lease total on the face:

- `PillFace.Peek` gains `totalSeconds: Int` (the granted lease length),
  set at derivation from the same stored end minus its start — if start
  isn't stored, the grant constant (120) is passed in. `PillFace.kt:51-55`,
  `:118-125`. ~4 lines.
- Renderer: a `View` with a `GradientDrawable`/solid accent-muted fill,
  width set per render — but note `render()` early-returns unless
  `renderKey` changes (`:146`), and the key already changes every second
  (`PillFace.kt:56`), so the bar updates on the existing tick for free.
- Masked-digits re-test (IR-4) now has something to read; the shrink also
  makes Peek visually unlike Session/Break at a glance (IR-2).
- Cost: ~15 lines (one view in `buildWindow()`, one width set in
  `render()`, the face field). This is the most expensive fix and the
  only one touching `PillFace.kt` — and it is non-negotiable: IR-4
  cannot pass without a non-digit channel.

### Fix 4 — Urgency thresholds on the figures (IR-4, IR-10 support)

```
when { remaining <= 10 -> urgent colour; remaining <= 30 -> accent #F47442; else -> cream }
```

- Two thresholds, no flashing (flashing on a distraction-control object is
  self-defeating). Add one `urgentArgb` to the palette
  (`PillTheme.kt:20-31,36-42`); ≤30 s may reuse the existing accent.
- Cost: ~6 lines in `render()` + 1 palette field.

### Fix 5 — Grow the figures, reclaim the label row (IR-10, IR-11)

- Time: 16 sp → **24 sp** (`:189`). Pill height grows ≈46 → ≈58 dp —
  still one cluster, still small; the growth goes to the datum, not the
  frame. Padding vertical 8 → 7 dp claws 2 dp back.
- Floors now on record for any future redress: **time ≥20 sp, label
  ≥11 sp, ✕ glyph ≥16 sp, pill height ≥52 dp.** A look lane that shrinks
  the figures below floor fails IR-10 by arithmetic, no taste argument.
- Cost: 2 numbers changed.

### Fix 6 — Teach the touch: grip, return glyph, honest ✕ (IR-6, IR-7, IR-8, IR-12)

Layout order becomes: **[grip] [label/time column] [✕ chip]**

- **Grip (IR-7):** a `⋮⋮`-type grip (two-column dot glyph or a 3-line
  `View` drawable) at the left edge, muted colour, ~10 dp wide. The
  universal "drag me" mark; costs no word. ~8 lines in `buildWindow()`.
- **Return glyph (IR-6):** prefix the label's row — no; better, put a
  `↩` (or `←`) at 13 sp, muted, immediately left of the column, on the
  body-tap's behalf. Tap-the-body now has a visible verb-glyph. If the
  studio prefers a word, "FOCUS" is the only candidate that earns it —
  glyph first, word only if the flash re-test fails IR-6. ~4 lines.
- **Honest ✕ (IR-8, IR-12):** text `"✕"` → `"END ✕"` at 12 sp accent,
  inside its own chip padding so the **hit area is a true 48 × 48 dp**
  (glyph stays visually ~16 sp; the padding is the target, e.g.
  `setPadding(dp(14), dp(14), dp(10), dp(14))` and a minimum width).
  "END" is the third and last earned word: it converts the most
  dangerous misread ("dismiss") into the true verb ("end the peek").
  Pill widens ~30 dp on Peek only — acceptable, and the width itself
  becomes a Peek tell (IR-2). ~5 lines at `:203-211`.

### Price summary

| Fix | Files | Lines | Risk |
|---|---|---|---|
| 1 Seconds format | PillController | ~4 | none — data already present |
| 2 Label word + 11 sp | PillController | ~3 | long app names → ellipsize |
| 3 Draining bar | PillController + PillFace | ~19 | needs lease total on the face |
| 4 Urgency colour | PillController + PillTheme | ~7 | none |
| 5 Figure size 24 sp | PillController | ~2 | pill +~12 dp tall — intended |
| 6 Grip / ↩ / END ✕ + 48 dp | PillController | ~17 | pill +~40 dp wide on Peek only |
| **Total** | **3 files, render-only** | **~52–70** | no schema, no service, no derivation logic |

**Re-test:** run §1.2 unchanged against renders of the fixed face at the
same five values, both backgrounds, digits-masked run included. Entry
ticket is Fixes 1–6 all in; a partial set predictably strands IR-4
(without Fix 3) or IR-8 (without Fix 6), and §1.3 scores either as FAIL
overall.

### Explicitly not proposed

- No new words beyond PEEK / LEFT / END. No "tap to return" sentence —
  a sentence on a 58 dp object is a paragraph it can't afford; glyph +
  re-test decides.
- No ring/pie redesign of the whole pill — the draining bar buys the
  analog channel for 19 lines inside the existing skin; a ring is a
  look-lane decision, not an instant-read requirement.
- No position persistence, no sound, no haptic on thresholds — none of
  them move any criterion in §1.3.

---

*Feel-5 · Instant Read (issue-16 cell) — graded from code only, per the
cell's hard rules: nothing compiled, nothing run, Sarthak not contacted.*
