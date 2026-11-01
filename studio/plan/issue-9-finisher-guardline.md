# ISSUE #9 — FINISHER SPEC: THE HEADER GUARD LINE, WITH NOT CHECKED

*Finisher 4 (header Guard line), issue-9 cell (for Hof). Ground:
`AUDIT-park.md` §2 P-6 (Hof's charge — the cold-open blank read
as fine; cure: `GUARD — NOT CHECKED`, never blank),
`issue-9-designer.md` §2 (the NOT CHECKED section — its rulings
stand, nothing here re-opens them), and the cell tree's own
code: `GuardLine.kt` as Hof amended it, `GuardLinePrintTest.kt`
(the DECLARED tests), `MonolithCustomComponents.kt`
(`MonolithPullDownHeader`, the band), `MonolithViewModel.kt`
(`guardLinePrint` + the two seal-freeze capture sites),
`MainActivity.kt` (the band's seat). Paper only — nobody
compiles.*

*Scope, exactly: the one line of print on the header band's
right side, in all four of its states — and finished for the
state that is new: UNMEASURED, printing `GUARD — NOT CHECKED`.
How it sits in the band, what its ink says against the 4/4 and
the lack prints, whether it fits, and what the seal freeze
does to it. NOT in scope: the CHECK verb and PARK's board
(Designer's §1, Finisher 1's lane — neighbours; the board's
name-only unmeasured rows are acquitted and are not touched
or echoed here, Designer §2.4), the warrant line, tiers/ages.*

*House law applied throughout: Sora display / Atkinson
Hyperlegible body / Anonymous Pro telemetry, off the existing
ladder only (22/15/10 — no new sizes); never green, never the
alert orange on any face in this issue; state by form first;
no icons, no imagery, no animation in the band — the band
never animates, for any state, including this one.*

*Every item is marked **SPEC-ONLY** (a ruling/verification the
builder obeys, no code delta) or **PATCH-READY** (the exact
change is given, ready to apply verbatim). One patch exists —
§3. It closes a gap between what Hof's function body already
does and what the types around it still permit.*

---

## §0 · THE FOUR PRINTS, AS THEY NOW STAND

`guardLineFor` (GuardLine.kt, as amended) is a pure function
of (snapshot, nowMono) and prints exactly one of:

| State | Print | Trigger |
|---|---|---|
| WHOLE | `GUARD 4/4` | all counted four warranted GRANTED |
| LACK | `GUARD {n}/4 — {NAME}` / `… {NAME} STALE` | set fully measured, n < 4; first lack in board order named, bare if MISSING, ` STALE` appended if stale |
| UNMEASURED | `GUARD — NOT CHECKED` | snapshot null, **or any one** of the counted four UNMEASURED |

The DECLARED tests pin all of it (`GuardLinePrintTest.kt`):
null snapshot → NOT CHECKED; three measured + one never
measured → NOT CHECKED (no count may print off a partial
set); whole set → `GUARD 4/4`; one MISSING → count + bare
name; one past the 45s stale line → count + ` STALE` name.
No fifth print exists and this spec adds none.

---

## §1 · THE DRESS — ONE INK, AND WHY "UNKNOWN" IS SAID BY FORM, NOT BY FAINTNESS

### F-G1 · NOT CHECKED wears the band's one dress, verbatim — **SPEC-ONLY**

The band is a nameplate (47dp row over a 1dp hairline,
invariant) and the Guard line has exactly one dress in it,
for every state, as built in `MonolithPullDownHeader` and
ruled in Designer §2.2:

- Anonymous Pro **Bold 15sp**, letterSpacing **+0.5sp**,
  `color = scheme.onBackground` at **alpha 1.00** (full
  primary ink — day ink, night cream, by the token's own
  flip; no token swap for this state in either scheme),
  right-aligned to the **20dp** end padding, baseline-
  identical with `GUARD 4/4` and every lack print, same
  `header_guard_line` testTag. No box, no outline, no
  pulse, no icon, no second line.

NOT CHECKED changes **none** of it. That refusal is the
finish, and it is worth stating why the two obvious
"unknown" dresses were both rejected:

- **Not dimmed.** The board next door has a half-ink
  (0.60) dress — for STALE (Finisher 1, F-B1). Borrowing
  it here would be a category error and a lie in both
  directions. On the board, half-ink means *granted, but
  the proof is old* — a known thing gone stale. NOT
  CHECKED means *never measured at all*; dressing the
  never-known in the gone-stale's clothes teaches the eye
  the wrong fact. Worse, in every UI convention a tired
  reader carries in his head, faint text reads as
  *disabled, decorative, fine-to-ignore* — and "fine to
  ignore" is precisely P-6's original disease, the blank
  wearing a costume. A cure for silence may not whisper.
- **Not alarmed.** No alert token, no weight step, no
  emphasis furniture. There is nothing here to act on
  yet — no door is accused, because no door has been
  asked. Alarm ink on an unasked question would spend
  the band's one loud voice on a non-fact, and would
  read, at 1 AM, as *something is broken* when the only
  true sentence is *nothing has been looked at*.

### F-G2 · How "unknown" is actually read — the three-way contrast — **SPEC-ONLY**

In the band, ink weight is **not** the state variable —
all three prints stand in the same full ink, and the eye
takes the state from the print's *shape*, before the words
are parsed:

| Print | Shape | What the shape says |
|---|---|---|
| `GUARD 4/4` | word + **count, closed** (`4/4`) | a measured, complete fact — the set is whole |
| `GUARD 3/4 — USAGE ACCESS` | word + **count + a named door** | a measured lack — specific, actionable, the band's only accusation form |
| `GUARD — NOT CHECKED` | word + **dash, and then no count, no name** | the count that is not there *is* the state: unmeasured |

Three rules ride with the table, and the builder verifies
them rather than building them:

- **The missing count does the work.** `4/4`'s presence
  is what "fine" looks like on this band; NOT CHECKED
  withholds it structurally, not decoratively. It can
  never be misread as a variant of the whole print —
  the shapes share nothing past the word GUARD.
- **The dash is the lack dash, emptied.** The lack form
  is `GUARD {n}/4 — {NAME}`: count, dash, culprit. NOT
  CHECKED keeps the pivot (`GUARD —`) and prints nothing
  the pivot normally introduces — no number before it,
  no name after it, only the plain admission. It reads
  as the same instrument, mid-sentence, honestly out of
  evidence — not as an error glyph and not as a status
  of a different kind.
- **Full ink is what keeps it off both ditches.** At
  1.00 it is unmistakably *printed* — a deliberate line
  the app chose to set, not a placeholder, not a faded
  leftover (never fine); at the band's ordinary dress,
  with no colour and no furniture, it makes no claim
  beyond its words (never alarm). The words themselves
  are the verdict: NOT CHECKED is a confession about
  the line, not an accusation about the phone.

---

## §2 · LETTERFIT — THE BAND HOLDS IT WITH ROOM TO SPARE

### F-G3 · Fit is arithmetic, not hope (Anonymous Pro is monospace) — **SPEC-ONLY**

Anonymous Pro advances every glyph — letter, digit, space,
and the em dash alike — by the same fixed advance. Character
count is therefore an *exact* proxy for printed width; there
are no proportional surprises and no wide-glyph exceptions.
At 15sp Bold with +0.5 tracking, one character ≈ 9.5dp of
advance-plus-tracking. The four prints, counted:

| Print | Chars | ≈ Width |
|---|---|---|
| `GUARD 4/4` | 9 | ~86dp |
| `GUARD — NOT CHECKED` | **19** | **~181dp** |
| `GUARD 3/4 — ACCESSIBILITY STALE` | 31 | ~295dp |
| `GUARD 3/4 — NOTIFICATION LISTENER` (longest possible lack) | 33 | ~314dp |

Against the band's budget (Row is `fillMaxWidth`; brand
`MONOLITH` at 10sp/+2.0 tracking on a 20dp start margin
≈ 84dp all-in; Guard line carries 20dp end padding):

- At **360dp** (the narrow common width): brand 84 +
  NOT CHECKED ~181 + end 20 ≈ 285dp — **~75dp of spacer
  to spare**, single line, no wrap, no squeeze.
- At **320dp** (narrowest handset class): the same sum
  leaves **~35dp** to spare. Still one line.
- NOT CHECKED is ~58% of the longest lack's width. The
  band's fit problem, where one exists, belongs to the
  lack prints the app already ships; this state introduces
  **no new fit risk of any kind** and needs no defensive
  furniture: no `maxLines`, no ellipsis, no shrink-to-fit.
  (An ellipsis on this print would be a small P-6 of its
  own — `GUARD — NOT CHEC…` is a line hiding what it
  knows. It is forbidden here by name.)
- Font-scale rider, stated for the record: at enlarged
  system font scale every print in the band grows
  together and the 33-character lack reaches the band's
  edge long before the 19-character NOT CHECKED does.
  This state is the *last* print that could ever crowd
  the nameplate, never the first. No per-state treatment
  is specified or permitted.

Acceptance is by the counts above and one glance at the
narrow preview: the line sits on the same baseline, at the
same right edge, with clear air between it and the brand.

---

## §3 · THE ONE PATCH — MAKE THE LINE UNBLANKABLE IN THE TYPES, NOT ONLY IN THE BODY

### F-G4 · Totality — **PATCH-READY**

Designer §2.3's implementation contract: *the print must be
unblankable — wherever the band renders, one of the prints
is on the glass; making `guardLineFor` total (non-null
return) and dropping the band's null gate is the clean way
to guarantee it.* Hof's amendment landed the body half:
`guardLineFor` now returns a NOT CHECKED print on both
former-null paths and can no longer produce null in fact.
But the *types* around it still declare the blank lawful,
and one real blank survives in the seams:

- `guardLineFor` is still typed `GuardLinePrint?`.
- `MonolithViewModel.guardLinePrint` is a
  `StateFlow<GuardLinePrint?>` whose `stateIn` initial
  value is **null** — so on a cold process start, until
  the combine first emits, `MainActivity` hands the band
  null and the band prints nothing. A brief blank, on
  exactly the cold open P-6 was filed about. The cure
  currently depends on the combine emitting fast; a
  finish may not depend on fast.
- `SealFreeze.guardLine` is still `GuardLinePrint? = null`,
  and the freeze branch is gated on `freeze?.guardLine
  != null` — a freeze minted without a capture would fall
  back to the live line under seal, quietly breaking the
  freeze's tense (see §4).
- The band still gates on `if (guardLine != null)` — the
  old silence, kept as a branch.

The patch closes all four, changing no printed string, no
dress, no behaviour beyond deleting the blank. Four files:

**(a) `GuardLine.kt`** — the signature tells the truth the
body already keeps:

```kotlin
fun guardLineFor(snapshot: WatchSnapshot?, nowMono: Long): GuardLinePrint {
```

**(b) `MonolithViewModel.kt`** — the freeze's field and the
flow, initial value first:

```kotlin
        val guardLine: com.example.ui.components.GuardLinePrint =
            com.example.ui.components.GuardLinePrint("GUARD — NOT CHECKED")
```

```kotlin
    val guardLinePrint: StateFlow<com.example.ui.components.GuardLinePrint> = combine(
        com.example.service.WatchStore.snapshots,
        _sealFreeze,
        activeSession
    ) { snap, freeze, session ->
        if (session?.isActive == true && freeze != null) {
            freeze.guardLine
        } else {
            com.example.ui.components.guardLineFor(
                snap, android.os.SystemClock.elapsedRealtime()
            )
        }
    }.stateIn(
        viewModelScope, SharingStarted.Eagerly,
        com.example.ui.components.GuardLinePrint("GUARD — NOT CHECKED")
    )
```

(The freeze's NOT CHECKED default is a floor, not an
expectation: both capture sites — the session-activation
collector and the cascade's t=0 re-capture — already pass
an explicit `guardLineFor(...)` value, and keep doing so.
The default exists so no future construction site can mint
a freeze that carries no line at all.)

**(c) `MonolithCustomComponents.kt`** — the band takes a
print, not a maybe, and the gate dies:

```kotlin
fun MonolithPullDownHeader(
    guardLine: GuardLinePrint,
    isSealed: Boolean,
    onGuardLineTap: () -> Unit
)
```

and the `if (guardLine != null) { … }` wrapper around the
Guard `Text` is removed; the `Text` itself — text, family,
weight, size, tracking, colour, padding, clickable-when-
unsealed, `header_guard_line` testTag — is byte-for-byte
as built.

**(d) `GuardLinePrintTest.kt`** — no assertion changes;
the five prints are untouched. The safe-call `?.text` at
each assertion site may drop its `?` once (a) lands —
cosmetic, and the builder may equally leave it; the suite's
meaning does not move.

After the patch, "the band prints nothing" is not a state
the code can express: not at cold open (the flow is *born*
holding NOT CHECKED), not on a null snapshot, not under
seal. That is the difference between a line that happens
never to be blank and a line that cannot be.

---

## §4 · THE SEAL FREEZE — A SESSION SEALED ON NOT CHECKED KEEPS IT FOR LIFE, AND THAT IS HONEST

### F-G5 · The frozen NOT CHECKED — **SPEC-ONLY**

The mechanism, as built (and unchanged by §3 except in
type): a seal captures the Guard print at the seal frame —
twice over, the session-activation capture and the
cascade's t=0 re-capture, the second being the seal's own
frame and the value of record — and while the session is
active `guardLinePrint` serves the freeze's captured print,
untouched by anything the Watch Store does after. If the
counted set is unmeasured at that frame, the capture is
`GUARD — NOT CHECKED`, and the band holds it from seal to
session end, even if a sweep lands a minute later and the
live line would by then read `GUARD 4/4`.

**Ruling: this is the honest outcome. It stands; do not
fix it.** Four reasons, in the order a challenger meets
them:

1. **The freeze is a tense, not a lag.** Every field in
   `SealFreeze` — the held count, its SHUT/PEEK split,
   the warrant state, this line — is the seal instant,
   kept in the seal's tense for the session's life. The
   Guard line is not a live readout that happens to be
   cached; it is part of the record of *what stood when
   he committed the phone*. Rewriting one field of that
   record mid-session because better evidence arrived
   would make the freeze a draft, and every other frozen
   print on the session's surfaces a suspect.
2. **The alternative is the worse lie.** A session that
   sealed unchecked and later re-printed `GUARD 4/4`
   would claim, in the present tense, a checked guard —
   and would silently erase the fact that the seal itself
   was taken on no measurement. P-6 struck a blank for
   claiming "fine" by saying nothing; a retro-upgraded
   freeze would claim "checked" by forgetting when. The
   frozen NOT CHECKED accuses nobody and invents nothing:
   it says the seal was taken before the first check,
   which is true, forever, about that seal.
3. **Nothing operational rides on the frozen print.** The
   sealed band is print, not a door — the tap is gated
   under seal, PARK degrades to read-only, and the live
   machinery (the store, the sweep, the shield) keeps
   measuring and acting regardless of what the nameplate
   holds. No decision during the session is taken *from*
   this line, so holding it costs him nothing but a true
   sentence. The moment the session ends, the freeze's
   jurisdiction ends with it: the band returns to the
   live print at the next frame, `4/4` and all.
4. **It is the same print the live band would have made.**
   The capture consumes the identical pure function at the
   seal instant (§2.3's contract in the Designer spec).
   Frozen NOT CHECKED is not a special sealed dialect of
   the line — it is the line, at t=0, kept. Any "repair"
   that re-measured under seal would need a second,
   sealed-only semantics for the same words, and the
   band would thereafter speak two tenses in one dress.

One boundary, stated so the ruling is not over-read: the
freeze holds for *the session that sealed unchecked*.
It is not a stain on the app or the next session, and this
spec gives it no persistence, no history print, and no
morning-after echo — when the session dies, the print
dies with it, exactly as every other frozen field does.

---

## §5 · BEHAVIOUR IN THE SMALL — THE REMAINING SEAMS, ALL SETTLED

### F-G6 · Swap, tap, and stillness — **SPEC-ONLY**

- **Arrival and departure are swaps, never shows.** Cold
  open: NOT CHECKED is on the glass from the first frame
  (§3's initial value — no blank frame, no `0/4` flash
  before it; a `0/4` flash would be the day-one lie at
  60fps). When the counted set completes its first
  measurement, the print swaps in place to the measured
  form — same slot, same type, same baseline, no
  animation, no fade (the band never animates; a crossfade
  would pass through inks and letterforms that mean
  nothing). The reverse swap obeys the same law if the
  set ever returns to unmeasured (a fresh residency
  before its first sweep): NOT CHECKED re-prints
  instantly, unapologetically.
- **The tap is worth most in this state.** Unsealed, the
  line remains the third door into PARK — in NOT CHECKED
  above all, because PARK is where the CHECK verb lives
  and the drawer's open-sweep is the cure for the state
  the line names. The line's tap treatment is unchanged
  from the other states (the print itself is the target;
  no enlarged slab, no special-casing this state — a
  door that changes size with its label is furniture
  moving under his thumb). Sealed: print only, tap gated,
  as today — and §4 is why that gating loses him nothing.
- **No clock lives on this line.** NOT CHECKED carries
  no age, no "since", no countdown to the sweep. It is
  not a progress state and must not borrow one's grammar;
  it ends when measurement lands, and until then it
  simply stands — which is all a not-yet can honestly do.

---

## §6 · ACCEPTANCE, BY EYE (builder + Hof, on paper until Ren compiles)

1. Cold-start the app: the band reads `GUARD — NOT CHECKED`
   from the first frame — no blank frame before it, no
   `0/4`, ever — in the same ink, size, and right edge as
   `GUARD 4/4` beside a settled band.
2. Let the first sweep land: the print swaps in place to
   the measured form, single frame, nothing else in the
   band moves; brand, hairline, and band height untouched.
3. Stand the three prints side by side (whole / lack /
   NOT CHECKED), day and night: same dress throughout;
   NOT CHECKED confusable with neither — no count to read
   as fine, no colour or weight to read as alarm.
4. Narrow preview (320dp and 360dp): NOT CHECKED on one
   line, clear air to the brand, no truncation, no
   ellipsis, no wrap.
5. Tap the line unsealed in NOT CHECKED state: PARK opens
   and its open-sweep fires. Seal a session while
   unmeasured: the band holds `GUARD — NOT CHECKED` for
   the session's whole life, tap gated, even after a
   sweep lands under the seal; end the session and the
   live print returns at once.
6. Diff check on the §3 patch: no printed string changed;
   `guardLineFor` non-null in signature; the band's null
   gate gone; `guardLinePrint`'s initial value is the
   NOT CHECKED print; tests untouched in meaning.

— Finisher 4 (guard line), issue-9 cell (for Hof)
