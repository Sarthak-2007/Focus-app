# FEEL AUDIT — ReceptionSheet (issue #14, branch `agent/issue-14` @ dee277f)

**Seat: HAUT (feel specialist, for Zange, finisher #14) · READ-ONLY — no commits, no edits, no gradle run.**
Method: read all five Wave-2 feel docs (`FEEL-first-ten-seconds / -motion / -sound / -touch / -0400-walk`) + `FINISH-type-print` / `FINISH-tear-exit`, then audited the code as it stands at tip. Headline finding up front: **most Wave-2 charges have already landed in this tree** — the tear, the release law, the CHECK/OPEN knocks, the chit-bound verdict click, F-5 short escorts, the mono total. What remains is a short, specific list.

---

## AXIS 1 · FIRST TEN SECONDS

**HOLDS**

- **One sheet, no wizard, identity first.** Nameplate → `SETUP + INTRODUCTION` → identity one-liner in AppTitle weight → boundary line → THE FOUR DOORS, in that order (`ReceptionSheet.kt:536–585`). The 0400-walk's praise #2 and #8 survive in code: the errand's shape is visible from the first screen, nothing dumps a second list as a setup step.
- **Escort of the first missing item — landed and better than the docs judged.** `firstMissingIdx` is precomputed before any card renders (`:595`), and only that card gets the full escort; later MISSING cards get `receptionDoorPrepareShort` (`:609`, `:1092–1095`). The four-identical-escorts repetition Blank (§2) and Halbwach (§2) both charged is dead. Warnings print WHOLE on every card that has one (short forms at `:294–310`) — the pre-named Android warnings, the sheet's best felt property, are untouched.
- **The total no longer contradicts itself.** Aged doors price as aged, never as absent: `receptionAgedProofSentence` = "THE LAST PROOF OF … AGED OUT — CHECK RE-PROVES IT" (`:215–216`), composed per-lack in COUNTED order beside the fresh count (`receptionTotal`, `:221–250`). Blank's M-24 (card says UNPROVED, total says IS SET) is cured. While any door is UNMEASURED the count is withheld entirely — no invented 0/4 (`:225–228`).
- **PARK is now findable from the sheet.** Stub note above the tear plus the direction line naming both real openings (`:832–848`) — Blank's M-25 cured.
- **The bringIntoView escort exists.** On a judged chit, `LaunchedEffect(chit?.state, settled)` calls `cardRequesters[c.door]?.bringIntoView()` (`:500–511`), requesters attached to both card forms (`:982`, `:1031`). The visited card is placed under the returning thumb — the 0400-walk's felt priority mechanism is in code.

**BREAKS**

1. **The exit is still invisible for the first two movements.** `LEAVE WHEN YOU LIKE…` prints once, at `:794` — after Movement 3 and OPEN THE LIST, ~1,200dp past the fold. Blank §1's ten-second truth ("the app is clear, the ask is clear, the exit is invisible") still describes the code. *Felt:* a refuser at the head sees only Android BACK, consequence unstated. *Cure:* **DECISION-NEEDED** (see below) — the head sticky key was struck by ruling H-4; I do not re-prescribe it.
2. **ARM still doesn't cash the wheel.** `:673` — "The wheel on the Deck sets the span. Nothing is shut yet." The Deck is now named (partial cure), but what the wheel *is* is still nowhere on the sheet; Blank's N-01 / M-13, the worst line in the census, stands. *Felt:* the session's core mechanic is six words with an unexplained noun at the exact point of action. *Cure:* FREE — one clause, e.g. "The dial on the Deck sets how long. Nothing is shut yet."
3. **THE ROOM beat prints a falsehood — and the code knows it.** `:686–695` prints the 20-second desk-clock beat with an in-code flag: "INTERIM FALSEHOOD, DO NOT SHIP SILENTLY… this tree has no 20 s untouched settle and its desk clock prints one figure, not this beat's three… true only when issue #13's settle machinery composes." *Felt:* a first-open user is promised a face the app doesn't have. *Cure:* not mine to price inside #14 — Zange must keep the MERGE-PLAN item-6 flag attached at compose time with #13; if #13 doesn't land the settle, the beat must be re-worded or struck in that act. **Merge-blocking to track, already flagged by the builder.**
4. **No "this fills itself" line for the unread sheet (Blank N-06).** Cold open = four NOT CHECKED cards with CHECK keys; the only "being read" sentence on the sheet is the total's, at the foot (`:804ff`). Nothing under THE FOUR DOORS tells him the readings arrive on their own. *Felt:* NOT CHECKED reads as breakage; his first act may be pressing CHECK on a door about to fill itself. *Cure:* FREE — one conditional line under the doors eyebrow, printed only while any door is UNMEASURED.
5. **The routine still isn't marked seeded (Blank M-19 / N-05).** `receptionFirstRoutineLine` (`:315–330`) prints "NEXT ROUTINE — … · SET ON SHIELD" with no SEEDED/example marker; S-2 killed the firing claim but not the presumption. *Felt:* an app he's never opened appears to already hold a 09:00 plan for him. *Cure:* FREE — one word in the line.
6. **The foot still assembles late (Halbwach F-7, uncured half).** Guard foot is `if (guard != null)` with no height reservation (`:647–659`); the total count is `if (total.count != null)` likewise (`:804ff`). Each arrival shoves everything below down 20–30dp while he walks toward it. The key band got the F-3 pair; the foot got nothing. *Cure:* SMALL — reserve each line's landed height while unmeasured, F-7's own pattern.
7. **The sheet still un-finishes itself under a slow reader (Halbwach F-2).** `STALE_AFTER_MS = 45_000` (`WatchStore.kt:150`); the standing loop only re-derives `nowMono` every 5s (`ReceptionSheet.kt:465–470`) and `onShown` sweeps only on showing (`MainActivity.kt:1039`) — no re-sweep while the sheet stands. A man reading the beats for ~46s crosses the bound standing still and watches SET doors flip UNPROVED above his scroll position. The total stays truthful; it *feels* like his work was taken back. *Cure:* SMALL — host re-fires the sweep on a slow beat (~30s) while the sheet is visible. (Freezing the derivation is rejected — Halbwach's reasoning stands: it would print unmeasured freshness.)

## AXIS 2 · MOTION

**HOLDS**

- **Rise / plain leave exact.** 280ms `DRAWER_OPEN_MS` + `EasingSeat` in, 160ms `DRAWER_CLOSE_MS` + `EasingThrow` out (`:428`, `:430`; constants `MonolithCurves.kt:97–98`). Interactivity gated on `fraction.value == 0f && !tearing` (`:435`) — no key hittable mid-travel.
- **The tear is arithmetically locked as Atem ruled.** Stamp books first (see Axis 3), then `coroutineScope` runs both animatables **concurrently**: drop+fade `specA(DURATION_180)` (`:448–451`), dashes halve to 0.5 `specB(DURATION_260)` (`:454–457`); the driver returns when the longer lands, then the host's 160 THROW — **420 total, never the voiding 600 serial version**. Everything above the rule is motionless, enforced against the hand too: scroll disables during the tear (`verticalScroll(..., enabled = !tearing)`, `:527`), and the stub note is latched through it (`tearLatchedFirstOpen`, `:410`, `:907`). Torn block geometry matches spec: `alpha = 1f − tearDrop`, `translationY = tearDrop × 10.dp` (`:859–860`); tear rule dash 3dp × `dashScale`, gap held (`:1274ff`).
- **Standing flip: datum snaps, furniture moves.** The door word is a plain `Text` swap — 0ms, no crossfade, no pulse, no sound (Atem §2a). The gloss/CHECK/FIX blocks enter and leave on the exact F-3 pair, both key blocks: `expandVertically + fadeIn` / `shrinkVertically + fadeOut`, `tween(160, EasingSeat/EasingThrow)`, fades uneased (`:1112–1117`, `:1171–1176`). Scroll is untouched by standing flips — no scroll effect is keyed on `nowMono` or print (Atem §2c holds).
- **No unbounded or looping animation anywhere.** Every tween is finite; the only loops are the 5s derivation beat (cancels with visibility) and the tear driver (fires once, reset on entry, `:419–427`). The 5s tick re-derives the whole column — a recomposition cost, not a jank risk at this tree size; no `Animatable` is left running at rest.

**BREAKS**

1. **F-6's animated collapse did not land — the fold is a branch swap.** Atem §3 ruled a sequenced 340ms fold (chit scroll 260 specA first → key exit 160 THROW → body fold 180 SEAT). In code, a GRANTED card takes an early return (`:970`) and the expanded body (job line, escort) leaves composition in the same frame the print flips; the F-3 exit pair only animates transitions *within* the expanded form. The sheet's own comment concedes it: "the collapse glide is the repair return's settle — deviation recorded in the merge plan" (`:1104–1110`). *Felt:* on the return that matters most, the card doesn't fold — it blinks short. The "sheet visibly completes as he works" reward Halbwach (§2, foot) told the merge to guard is paid only as a length change behind his back, not as a seen fold. *Cure:* MEDIUM — implement Atem §3's two phases on the card (key block exit, then body `shrinkVertically(180, EasingSeat)`), or have Zange formally strike §3 and accept the snap; what it must not do is ship claiming §3.
2. **The chit scroll has no clock.** `bringIntoView()` at `:509` is called with no rect and no spec — whatever the default `BringIntoViewSpec` animation is, it is not the ruled `animateScrollTo` at 260ms specA (Atem §3 rule 1), and nothing sequences it against the (currently instant) collapse: scroll and shrink can chase the same target. *Felt:* the one moment the document moves *for* him moves on an unnamed curve at an unnamed duration — off-vocabulary motion on the sheet's most important return. *Cure:* SMALL — drive the chit scroll explicitly at `tween(260, EasingSeat)` and finish it before any fold phase (pairs with Break 1; if Break 1 is struck, this cure stands alone).
3. **Minor:** the chit scroll effect doesn't check `chitPrintsAgainst` — a judged chit whose verdict the door's current print no longer agrees with (SetupStore expiry law, `ReceptionSheet.kt:340–349`) prints nothing but can still scroll him to that card on state change. *Cure:* FREE — gate `:503–510` on the same predicate the print uses.

## AXIS 3 · TOUCH + SOUND

**HOLDS**

- **Release law landed on all four sites (Haut T-3 cured).** Feel on DOWN, act only on `tryAwaitRelease() == true`, result read everywhere: FIX (`:1142–1155`), CHECK (`:1195–1202`), OPEN (`:756–763`), DONE (`:900–913`). Drag-off aborts; a scroll starting on a key spends only the knock/dip graze; FIX's cancel path lifts the dip and departs nothing (`onFixCancel`, `:626–630`).
- **Targets exact.** FIX/CHECK/OPEN full-width × 48dp (`:1125`, `:1185`, `:740`), DONE × 52dp (`:875`) — the size hierarchy matches the weight hierarchy; no lateral miss exists on this sheet.
- **Dips are states, not animations (Atem §5 acquittal preserved).** FIX: `offset(y = if (fixPressed) 1.dp else 0.dp)` (`:1125`), set on DOWN (`:614`), **held through the absence and released by the chit leaving DEPARTED** (`:488–494`) — the 800ms timer verdict of the Wave-2 tree is dead. DONE: 1dp silent dip on DOWN (`:875`, `:900`), does not return on a stamping commit — it continues into the tear drop; snaps back on drag-off (`:901–903`). Exactly Haut §4.4/§6, including simultaneity: dip release and chit print read the same chit state.
- **Sound score is now honest, voice by voice.**
  - FIX knock: floor values verbatim — `playSeatKnock(10, if (isNight) 0.06f else 0.1f, 300f)` (`:1142–1146`).
  - CHECK knock: **landed** (Klang K-2 / Haut T-1 cured) — identical call, voice only, no dip (`:1195–1199`).
  - OPEN THE LIST knock: **landed at Haut §4.3's heavier price**, not Klang's floor proposal — `playSeatKnock(14, if (isNight) 0.30f else 0.50f, 300f)` (`:756–760`). Correct call: OPEN has no return verdict to pay its mass later.
  - DONE stamping press: **the lying firm click is gone.** Order is G-01: `onStamp()` books first, *then* `playTearPulse()`, then the tear arms (`:904–908`). One act, one voice (T-4's doubling hazard avoided — `playFirmClick` is removed from this branch, not layered).
  - DONE reprint press: firm click stays, plain 160 leave, no tear (`:910–912`) — Klang row 10 as ruled.
  - Return verdict: the click moved host-side and is bound to a measurement — `MainActivity.kt:382–408`: `chitVerdictFor` judges only on a measurement with `measuredMono > departureMono` (`SetupStore.kt:240–256`), and `playFirmClick()` fires **only** on `RETURNED_REPAIRED`, at resolution. No failure voice anywhere (A-5 holds); the chit itself survives process death in `SetupStore` (`writeDepartureChit`/`resolveChit`, committed synchronously).
  - **Seal-sound law: clean.** `ReceptionSheet.kt` contains zero calls to `playHarmonicSeal` / `playSealThrow` (grep-verified; the sheet's only engine calls are the three knocks, the tear pulse, and the reprint click). Setup borrows nothing from the seal.
  - Lawful silences intact: rise, excursion departure (beyond the press knock), not-repaired/unread returns, and BACK leave carry no sound call.

**BREAKS**

1. **The tear still has no night (Klang K-4, unresolved and now genuinely contested).** `playTearPulse()` at `:906` takes no night parameter — full day gain at the sheet's canonical 04:00 press. Klang priced a night form SMALL (engine + one call site). But Haut's touch ruling (§4.4) says the opposite: no night variant, a stamp that whispers can be doubted. The two feel seats disagree on the shipped code. **DECISION-NEEDED** — do not let a builder pick a side silently.
2. **Inherited, recorded, not re-charged:** an `AnimatedVisibility` exit keeps its key composed and hit-testable for its 160ms (Letzt Flag F, PARK's own property, mirrored verbatim). During that window the key is leaving but still pressable. Accepted by the tear spec; noted so the merge carries it knowingly. No cure priced.

## AXIS 4 · TYPE / PRINT

**HOLDS**

- **Sanctioned families only, everywhere.** Every `Text` on the sheet resolves to Sora / Atkinson Hyperlegible / Anonymous Pro via `Type.kt`'s named ladder or inline values in those families (grep-verified: no fourth family, no off-ladder size remains). Specifically cured since Fein's census: the identity one-liner carries `AppTitleTextStyle` (Atkinson Bold 15/20, F-2); the escort and STALE gloss print in `DoorJobTextStyle` = Atkinson 15/22 (`Type.kt`, DoorJob definition) — Fein's V-2 off-ladder 12sp inline is dead.
- **Receipt grammar holds: count mono, sentence prose.** Guard foot (`:650–657`) and total count (`:804ff`) both print Anonymous Pro Bold 15, ls 0.5 — the same spec in both seats (Fein V-1/V-3 cured), with the total sentence in `BodyTextStyle` beneath. This is the receipt family's division (`ReceiptSheet.kt`: number in Anonymous Pro Bold, lines via `ReceiptMonoLine`, prose in Atkinson) at document scale. The printed-slip character — nameplate in badge dress, hairlines at 1dp ink 18% (`ReceptionHairline`), the dashed tear rule at ink 35% with PerforationRow geometry — is intact and shared.
- **Beats print as the ruled table (F-9 landed).** `ReceptionBeatLine`: label in `PillBadgeTextStyle` in a fixed 84dp column, line in `BodyTextStyle`, hairline beneath each row (`:1240ff`).
- **Chit line size cured (Halbwach F-5 substantially).** The chit prints in `DoorJobTextStyle` (Atkinson 15/22 — BodyText's values), not the 10/12 badge size Funke specced, with the repaired SET bolded in ink via `chitLineText` (`:1016`, `:995ff`). It is still worn in `onSurfaceVariant` dim — a shade quieter than F-5 asked, but legible at body size.
- **Ink discipline landed (F-4).** FIX and CHECK both wear `surfaceVariant` (`:1130`, `:1185`); DONE is the sheet's only `inkControlFill()` key (`:877`). Five shouting black keys → one. This also means the sweep's landing flips a word, never a target's colour under an aiming thumb.
- **No green, no images.** The sheet uses scheme tokens only (Color.kt's zero-green token law governs them); its only graphics are the Canvas tear rule and hairlines. No AI imagery, nothing to flag.

**BREAKS**

1. **The chit's NEXT suffix still rides the failure path (Halbwach F-6).** `chitLineFor` applies the same suffix logic to both states (`:273–286`): `RETURNED_UNCHANGED` prints "WENT OUT … · CAME BACK NOT SET. · NEXT — …". *Felt:* on a failed return the sheet moves him along past a door still open, when his hand wants *this* door again. *Cure:* FREE — drop the suffix in the UNCHANGED branch only.
2. **Naming debt (Fein §C, still open).** The door word, Guard foot, and total count carry their Anonymous/Atkinson specs inline at three sites instead of as named styles in `Type.kt`. Values currently agree — this is how V-3 happened last time. *Cure:* FREE — promote the mono-datum style (Anonymous Pro Bold 15, ls 0.5) to a named style and point all sites at it. No felt change today; insurance against the next drift.

---

## DECISION-NEEDED — would feel like a downgrade or a reversal if changed; Sarthak's call, not the builder's

1. **Tear-pulse night volume** (Axis 3, Break 1): Klang wants it softened at night; Haut's touch ruling wants it full. Softening the sheet's one terminal act is a feel downgrade risk — flagged, not prescribed.
2. **Exit visibility at the head** (Axis 1, Break 1): any cure with presence at the top (sticky key, head leave-line) re-opens struck ruling H-4. The residual cost is real and recorded; the remedy is his.
3. **F-5 short escorts, as landed:** Halbwach's warning stands on the record — at 04:00 the full escorts' sameness was a handrail, and the short forms trade a small feel price for ~17dp/card of composition. If anyone proposes reverting to full escorts (or shortening further), that is a visible feel change to the errand, not a tidy-up.
4. **The tear itself, the 1dp dips, the night-floored FIX knock, and DONE as sole ink key** are keepers under the standing rule: any future "simplification" that removes the 420ms ceremony, animates the dip, or re-inks the door keys is a downgrade and needs his approval before it is even specced.

## BOTTOM LINE FOR ZANGE

The shipped sheet is much closer to its feel docs than the docs' own tree was: tear, release law, all three knock cures, chit-bound verdict, mono total, ink discipline — **HOLDS**. Open items, in felt order: **(1)** THE ROOM interim falsehood — merge-plan flag must survive composition with #13; **(2)** F-6 animated collapse + clocked chit scroll (Atem §3) — implement or formally strike, don't ship claiming it; **(3)** three FREE wording cures — ARM's wheel clause, UNCHANGED chit suffix, SEEDED routine marker (+ the N-06 fills-itself line); **(4)** foot height reservation + standing re-sweep (SMALL ×2); **(5)** two DECISION-NEEDED items (tear night volume, head exit) go up, not to a builder.

— **HAUT** (feel), issue #14 cell