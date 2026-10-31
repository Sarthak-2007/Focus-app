# Issue #10 — Receipt (audit fixes) · FINISHER 3 (deflections print)

**Seat:** Finisher 3, Zettel's cell for GitHub issue #10, "Main-app pass — Receipt (audit fixes)".
**Slice:** the DEFLECTION list's print finish only — how the true list *looks* on paper.
**Ruling finished here:** audit item 11, PRINT half / cure **A10** (Druck P-6, ruled for Druck
over Aug's dim acquittal; silence span rules = Emil P5, "quiet chapters").
**Sources:** `studio/plan/AUDIT-receipt.md` item 11 + cure A10 (read in the `wt-ci` clone —
the audit file is not present in this clone) and the code in *this* clone. Every line
citation below is to this clone unless a path says otherwise.
**Acquitted form, protected:** the list is *listed, never summed* (no count, no total, no
header word anywhere in this section — the contract itself carries no total field,
`data/ReceiptState.kt:124–128` and its doc comment); assembly's reductions stand as ruled
(30s-per-package dedupe, first kept; `com.monolith.system` rows excluded —
`data/MonolithRepository.kt:1596–1630`). The outcome filter (A4, the truth half of item 11)
is another seat's; this spec composes with whatever list survives it and never re-filters.
**Binding character, untouched:** Anonymous Pro mono ledger (11sp / 1sp tracking), long-form
durations, zero buttons / zero CTA, omission law (absent over false). No new colour, face,
image, or symbol anywhere in this section. Nothing here compiles, builds, or tests — paper only.

---

## 0. Current print, in evidence (the faults this slice closes)

| Element | Today (this clone) | Fault |
|---|---|---|
| Deflection loop | `ReceiptSheet.kt:139–144` — every entry prints `"${hhmm(d.atWallMillis)} · ${d.appName}"` via `ReceiptMonoLine(..., dim = true)` | The list's only distinction from the ledger above it is a *greyer ink* — exactly what P-6 kills. No rule, no indent structure: entries are flush ledger lines that happen to be dim. |
| `ReceiptMonoLine` | `ReceiptSheet.kt:188–197` — Anonymous Pro 11sp, letterSpacing 1sp, `color = if (dim) scheme.onSurfaceVariant else scheme.onBackground` | The `dim` branch *is* the second ink. Its only caller in the file is the deflection loop (L142). |
| Column rhythm | padding 20dp (L81), uniform `spacedBy(8.dp)` (L83); standard-card inner width ≈348dp | No section separation exists to inherit; A6 owns the global re-rhythm — §2 states this section's printed joints so it composes with A6 or stands alone. |
| Contract | `ReceiptDeflection(appName, packageName, atWallMillis)` (`data/ReceiptState.kt:124–128`) | Carries nothing that can tell a site entry from an app entry — see §4.1 for why the sheet cannot guess it. |
| Site row shape | `service/MonolithAccessibilityService.kt:255–267`: a site intercept launches with `packageName = <the browser's package>`, `appName = matchedDomain`, `path = InterceptPath.SITE` (enum L46–58) | On the slip a site row therefore prints the *domain* in the name slot while its `packageName` names the browser (e.g. `com.android.chrome`) — indistinguishable in form from an app row, whose `packageName` is the app's own (e.g. `com.google.android.youtube`, `appName = "YouTube"`). |
| Stored domain forms | `data/MonolithModels.kt:509–516` — the seeded list holds bare domains (`instagram.com`, `reddit.com`, `x.com`) *and* path forms (`youtube.com/shorts`, L516; parser L528) | Any print-side test on the string's shape ("contains a dot ⇒ site") is guesswork and is forbidden by this spec (§4.1). |

Sample printed list used throughout (post-filter truth assumed): a 09:00–10:30 sitting —
`09:02` site `youtube.com` via Chrome · `09:05` app `YouTube` · `09:33` app `Instagram` ·
`10:14` app `YouTube`. Gaps on the printed list: 3 min, 28 min, 41 min.

---

## 1. F3-1 — One ink: the dim second ink dies

- Every deflection entry renders through the ledger's own token, unmodified:
  `AnonymousProFamily`, 11sp, letterSpacing 1sp, **`color = scheme.onBackground`** —
  the identical `Color` object every other ledger line uses (ARMED / ENDED / HELD),
  alpha 1.0, in day and night schemes alike. No `onSurfaceVariant`, no alpha variant,
  no tint anywhere in the section — entries, span rules (§3), and the section rule (§2)
  are all struck in this one ink.
- Builder shape: the deflection loop at L139–144 calls `ReceiptMonoLine` **without**
  `dim`. Since L142 is the parameter's only caller, the `dim` parameter itself
  (L188, L196) can be deleted whole; if the builder keeps it for another seat's sake,
  no deflection may pass `true`.
- Consequence, stated plainly: an entry is now told apart from the ARMED/ENDED ledger
  *structurally* — by the section rule above it (§2) and its hanging indent (§2.2) —
  never tonally. That is the whole of P-6.

## 2. F3-2 — The section: one rule above, hanging indent inside

### 2.1 The section rule

- When `receipt.deflections` is non-empty, the list opens with a rule:
  **height 1dp, `fillMaxWidth()` (the Column's inner width — ≈348dp at the standard
  card), `background(scheme.onBackground)`** — solid, no dashes, no alpha, not
  `outlineVariant`. It is a printed line, not a UI divider.
- Printed joints (distances, not mechanisms — A6's re-rhythm may supply them, but the
  distances are this spec):
  - preceding ledger line → rule: **16dp** (the audit item-16 section gap; today the
    Column gives 8dp, so the section contributes one extra 8dp);
  - rule → first entry: **8dp** (within-section rhythm);
  - entry → entry: **8dp** (the Column's existing rhythm, unchanged);
  - last entry (or last span rule) → whatever follows the section (shield-was-off line,
    fault line, or footer): **16dp**, same section law.
- The rule exists only if the section exists. Empty list ⇒ **no rule, no extra gap, no
  placeholder, no "none" line** — omission law, whole (§5).

### 2.2 Hanging indent — the entry's two columns

Each entry is a two-column row, not a single wrapping string:

- **Time column:** `Text(hhmm(d.atWallMillis))` in the §1 token, `maxLines = 1`,
  `softWrap = false`, intrinsic width (no fixed dp). Every time is `HH:mm` — five
  glyphs in a monospace face — so the column is self-aligning at any fontScale with
  no magic width to break.
- **Gap:** 8dp between the time column and the name column.
- **Name column:** `Text("· " + <entry text per §4>)`, `Modifier.weight(1f)`, same §1
  token. Wrapped continuation lines align at the name column's left edge — i.e. they
  hang **under the `·`**, indented by (time column + 8dp) from the section's left edge,
  never back under the time. That alignment *is* the hanging indent of the ruling.
- Print form, app entry: `09:05 · YouTube`. No bullet, no check, no index number —
  the acquitted bare ledger form, now in full ink on an indented column.
- Print order = list order as assembled (chronological); the sheet never re-sorts,
  never caps, never truncates the list.

## 3. F3-3 — Silence span rules inside the list (Emil P5)

- **Trigger:** between two consecutive *printed* entries, when
  `next.atWallMillis − prev.atWallMillis ≥ 1_200_000` ms (**≥ 20 minutes**, boundary
  included), a span rule prints in the gap's slot. Gaps are measured on the printed
  (post-filter, post-dedupe) list only — a row another seat's truth cure removes
  lengthens the silence honestly, and this seat never measures around it.
- **Form — a labelled rule, one ledger line tall:** a `Row`, `CenterVertically`,
  occupying one normal 8dp-rhythm slot, containing in order:
  1. hairline segment — `Box(Modifier.weight(1f).height(1.dp).background(scheme.onBackground))`;
  2. 8dp gap;
  3. the label — `Text("{longSpan(gapSeconds)} QUIET")` in the §1 token;
  4. 8dp gap;
  5. a second hairline segment, identical to (1).
  
  I.e. it prints as `──── 28 MIN QUIET ────`. The label uses the file's own long-form
  dialect via `longSpan()` (`ReceiptSheet.kt:203–211` — `28 MIN`, `41 MIN`, `1H 05M`;
  its ceiling rounding is the slip's standing span dialect and is inherited, not
  re-derived). `gapSeconds = (next.atWallMillis − prev.atWallMillis) / 1000`.
- **Fit:** worst realistic label `3H 50M QUIET` = 12 glyphs × ≈7.6dp ≈ 91dp, plus 16dp
  of gaps, leaves ≈120dp of hairline per side at the 348dp inner width. It fits at
  every lawful gap; the hairlines absorb all slack, the label never wraps
  (`maxLines = 1`, `softWrap = false`).
- **Edges:** a span rule prints *only between* entries — never before the first entry
  (the ARMED line already bounds that silence) and never after the last (ENDED bounds
  it). Every qualifying gap gets its own rule; two rules never merge.
- **Distinction from the §2 section rule:** the section rule is unbroken, full-width,
  and sits on a 16dp section joint; the span rule is broken by its label and sits
  inside the list's 8dp rhythm. The two can never be confused on paper.
- The span rule is not an entry: it carries no time, is never counted (nothing in this
  section is counted), and its hairlines are decorative `Box`es; the label `Text`
  carries the fact for semantics.

Worked on the §0 sample: `09:02`→`09:05` = 3 min, no rule; `09:05`→`09:33` = 28 min,
prints `28 MIN QUIET`; `09:33`→`10:14` = 41 min, prints `41 MIN QUIET`.

## 4. F3-4 — Site entries print distinguishably from app entries

### 4.1 The data the print consumes (named, priced — no guessing at render)

The sheet must not infer kind from the printed string: an app label can contain a dot
and a stored domain can be a path form (`youtube.com/shorts`), so shape-sniffing would
mislabel real rows. The distinction exists at the source — a site intercept arrives via
`InterceptPath.SITE` (`MonolithAccessibilityService.kt:46–58`, fired at L266) — and the
print consumes it as a carried fact:

- `ReceiptDeflection` gains **`val isSite: Boolean`**, set at assembly
  (`MonolithRepository.kt:1596–1630`, in the same `.map` that mints the deflections):
  `isSite = (log.appName ∈ enforced site domains)` — an **exact match** of the row's
  `appName` against `dao.getAllSitesSnapshot()` domains, the very list the enforcer
  matched (`MonolithAccessibilityService.kt:255–256`). Hard evidence, not string shape;
  no DB change, no schema — the field is derived at assembly, so restored slips
  (which re-run assembly via `rehydrateReceipt`) print identically to fresh ones.
- Named edge, honestly: if a domain is removed from his site list between the session
  and a later rehydration, that row's exact match fails and it prints as an app entry.
  Accepted and recorded here rather than hidden; the alternative (a persisted kind
  stamp on `InterceptLogEntity`, `data/MonolithModels.kt:452–460`) is a DB change and
  is Zettel/Ren's call, not this seat's to price into A10.

### 4.2 The printed distinction

- **Site entry:** the name column prints the domain **exactly as stored** (lowercase,
  path form intact — a domain is a literal address, never uppercased, never trimmed),
  followed by the tag ` · SITE`:

  `09:02 · youtube.com · SITE` · `09:47 · youtube.com/shorts · SITE`

- **App entry:** the name column prints the app label alone, untagged:
  `09:05 · YouTube`, `09:33 · Instagram`.
- The tag is the ledger's own vocabulary (the HELD line already books `SITES`); same
  face, same size, same one ink — the distinction is a *word*, not a colour, icon, or
  weight change. The browser's name/package never prints: the browser was the vehicle,
  the domain was the reach, and the slip books the reach.
- Site entries obey §2.2's hanging indent and §3's gap measurement identically to app
  entries; a silence is measured across kinds without distinction.

## 5. F3-5 — Omissions and invariants

- Empty deflection list ⇒ the section does not exist on the slip: no rule, no gaps,
  no heading, no zero. (Omission law; the acquitted form lists what happened.)
- A single entry still earns the §2 section rule above it, and never a span rule.
- Nothing in this section is ever summed, counted, headed, or footered: no
  `DEFLECTIONS` caption, no `n TURNED AWAY`, no per-kind subtotal. Listed, never summed.
- Entries print `HH:mm` only. The cross-midnight date cure (A11) belongs to the head
  seat; the list is one sitting and repeats no date inside itself.
- No entry is ever dimmed, shrunk, italicised, or weight-shifted for any reason —
  including the last one, the longest name, or a site among apps.

## 6. Acceptance (paper checks)

1. **One ink:** code inspection finds no `dim = true` (or any second colour) in the
   deflection section; entries, span labels, span hairlines, and the section rule all
   resolve to `scheme.onBackground`.
2. **Section:** a slip with ≥1 deflection shows exactly one solid full-width 1dp rule
   above the first entry, 16dp clear of the ledger line above it; a slip with 0
   deflections shows no rule and no added space.
3. **Hanging indent:** a name long enough to wrap (or fontScale raised until one does)
   wraps with continuation lines aligned under the `·`, time column unmoved.
4. **Span rules on the §0 sample:** the printed list reads, in order —
   `09:02 · youtube.com · SITE` / `09:05 · YouTube` / `──── 28 MIN QUIET ────` /
   `09:33 · Instagram` / `──── 41 MIN QUIET ────` / `10:14 · YouTube`.
5. **Boundary:** a gap of exactly 20:00 prints a span rule (`20 MIN QUIET`); 19:59
   prints none. No span rule appears before the first or after the last entry.
6. **Sites:** a site row prints its domain verbatim + ` · SITE`; an app row prints no
   tag; no browser name appears anywhere in the section; a restored slip's section is
   byte-identical in form to the fresh slip's.
7. **No sum:** the section contains no numeral other than entry times and span-rule
   durations.

## 7. What this slice deliberately does not touch

- The outcome filter (A4) and the routine-window bound (item 11's truth/window halves)
  — other seats; this print finishes whatever true list they hand it.
- The global rhythm and the *other* sections' rules (A6), the RAN total and its rule
  (A1/designer §2), HELD (C1/designer §4), head & № (finisher 1), footer (A3), shape /
  anchor (A5), arrival (A7), dismissal (A8), the two machine statements' voice (item 16).
- The persisted-kind-stamp alternative in §4.1 — named for Zettel/Ren, not built here.
- TEAR and SLIP BOOK — parked, issue #21; nothing here pre-builds them.

— Finisher 3 (deflections print), Zettel's cell (issue #10). Paper only; nothing compiled.
