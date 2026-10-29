# HEADER-BAND AUDIT — Band (Improvers, bottom nav), 29 Oct 2026

*Commission (Ren, on my 09:57 wall offer, `plan/slice-e-parts/20-band.md`
§NEXT(b)): the same swallow audit I ran on the bottom band, run on the
header band above the Deck — does it ever swallow a touch, overlap
content, or eat a surface it shouldn't, specifically during the seal's
proving and the seal shot, when the Deck transforms under it? Slice E's
build-ready named the gap (SLICE-E-BUILD-READY.md, the §9 dependency
notes: "§5.4's input-death census covers the Deck's 8 surfaces and
§11.1 the band, but the header chrome above the Deck was named by
Zug-II as needing a seat and never got one").*

*Ground: the merged trunk (`~/workspace/clone-inspect/Focus-app`,
post–Slice E, DB v10) and the existing harness renders in `shots/`.
Read-only: no app code edited, no gradle run. Every finding files
with file+line or a render citation, or it does not file. Inbox at
start: 0 unread (seven letters in `read/`, all plan-wave). The
sealed-rail render pair from the same 09:57 post is NOT built —
Ren ruled it dies with the scrapped skin; this audit is mechanism
truth and survives the reskin, because every new face still has a
header band above the Deck.*

**The object, identified.** The header band is `MonolithPullDownHeader`
(`ui/components/MonolithCustomComponents.kt:250–304`): a 47dp row over
a 1dp hairline, 48dp total, in-flow as the first child of the root
Column (`MainActivity.kt:386–395`). Left: the brand, pure print.
Right: the Guard line (`ui/components/GuardLine.kt:36–46`), whose tap
is the third door into PARK. It has exactly ONE input besides its
print: that tap. (My 09:57 filing named the sites "GUARD pill + drawer
toggle" from the pre-chrome tree; the chrome build replaced both with
this single line. The audit covers what actually shipped.)

---

## VERDICT IN ONE LINE

The band's geometry is innocent and its shot-window behaviour is
clean — but its one door is gated by the wrong key: it consults
`activeSession.isActive` only, never the arm/finish phase, so it
re-opens during the finish's detention (HB-1) and stands open in the
latch→insert proving window (HB-2), and in both windows its tap
raises PARK's scrim over the Deck — the header eating a surface it
shouldn't, by proxy.

---

## FINDINGS

### HB-1 (S2) — The door re-opens at E0, mid-detention, while navigation is still locked

The VM's navigation lock is phase-keyed and explicitly covers the
finish's detention: `navigationLocked()` (`ui/MonolithViewModel.kt:1215–1229`)
returns true while `finishState != null && !finish.issued` — "while a
finish detains the Deck (E0 → issuance)" in its own comment. The
rail's taps honour it because they route through `navigateTo`
(`MonolithViewModel.kt:1230–1252`, refusal before the detent sound).

The header's door routes nowhere near it. The host wires the tap
directly to PARK (`MainActivity.kt:390–392`: `onGuardLineTap = { if
(parkOpen) closePark(…) else openPark() }`), and both gates on that
path key on one predicate only — `sealed = activeSession?.isActive
== true` (`MainActivity.kt:231`): the composable drops the clickable
when sealed (`MonolithCustomComponents.kt:289–291`), and `openPark()`
refuses when sealed (`MainActivity.kt:265–267`).

At E0 the session row completes; `activeSession.isActive` goes
false; `sealed` goes false. The detention has up to ~1,800ms still
to run (issuance at E1480 happy / ≤E1800 faulted). In that entire
window:

- the Guard line is tappable again and `openPark()` admits it;
- PARK rises inside the viewport over the held frame — the frame
  whose law is "frozen readout, frozen progress … and nothing else
  moves" (`ui/screens/TactileDeckScreen.kt`, held-frame comment at
  the `heldFinish` derivation);
- PARK's board is fully live, not degraded: its interactivity is
  `visible && settled && !isSealed` (`ui/components/ParkDrawer.kt:147`),
  and `isSealed` is the same false — FIX keys, the sound sled, the
  whole board answer, mid-detention;
- opening PARK also fires the host's receipt-dismissal effect
  (`MainActivity.kt:271–275`: any `parkOpen` transition dismisses the
  standing receipt) and the `refreshSystemGuard()` sweep
  (`MainActivity.kt:303–307`) — state churn the detention exists to
  exclude.

This is the detention trap Linea §3 named for the task line and my
§1.2 named for the band — "a session-only gate re-opens during the
held frame" — landed on the header instead, because the header was
never given the phase key. The rail got two layers; the header got
one, and its one expires early.

**Fix shape (not built; read-only commission):** one predicate for
every door. The host computes `doorLocked = sealed ||
viewModel.navigationLocked()` and feeds it to the header's
`isSealed` parameter and to `openPark()`'s guard. Host-side only,
≈6–10 lines, no composable change (the parameter already exists).

### HB-2 (S2) — The latch→insert proving window: the door stands open, and its tap covers the Deck mid-arm

`navigationLocked()` covers PROVING from the latch frame, because it
reads the controller's phase. The phase enters Proving with
`sessionId = null` at the latch, before the insert launches
(`ui/ArmProvingController.kt:328–336`). Until the insert returns and
the Room flow emits to the host, `activeSession` is still null, so
`sealed` is false and the header's door is fully live — clickable
present, `openPark()` admitting.

The window is short (an insert, normally milliseconds), but what a
tap in it buys is not small: PARK's scrim is a `fillMaxSize`
clickable inside the viewport (`ParkDrawer.kt:198–214`) that rises
over the Deck — over the slide, mid-press or mid-proving — and
consumes the Deck's touches from that frame. A second finger on the
Guard line while the first holds the slide is enough. The header
itself touches nothing (see A6); it eats the Deck *by proxy*, by
admitting the one surface that covers the Deck, at the one moment
the Deck's own input-death law (scroll dead 0→1050,
`TactileDeckScreen.kt` `.verticalScroll(…, enabled = !shotActive)`)
is trying to keep the room untouched.

Same root as HB-1 — one gate, wrong key, no second layer — and the
same fix closes it: `navigationLocked()` is already true for the
whole of Proving and Jammed, from the latch, with no flow lag,
because it reads the phase StateFlow the VM itself owns.

One more asymmetry worth printing: the rail's VM backstop means a
view-layer lag costs the rail nothing; the header has no backstop at
all. Its single layer is also flow-fed (`activeSession` is collected
asynchronously in the host), so even after the insert there is a
recomposition-frame class of lag (Band §6 R-B1's gap, one surface
over) with nothing behind it. HB-1's fix is also the backstop: the
host-side guard in `openPark()` reads the phase synchronously at
tap time.

### HB-3 (S3) — "Never wraps" is a doc claim the code does not enforce

The header's own spec comment promises the band "never grows, never
wraps, never changes height" (`MonolithCustomComponents.kt:234–239`).
The Guard-line `Text` (`:281–294`) sets no `maxLines` and no overflow
mode. Today's longest print (`GUARD n/4 — NOTIFICATION LISTENER
STALE`, per `GuardLine.kt:40–44`'s stale form) fits one line at
412dp — the only lack any harness frame shows is the shortest-ish
one (`shots/01_deck.png`: `GUARD 1/4 — ACCESSIBILITY`, one line,
clear of the hairline) — so this has never fired. But the row's
height is fixed at 47dp (`:265`), so if a future print (a longer
board name, a bigger font scale — accessibility font scaling is the
realistic route) ever wraps, the second line paints *below* the row,
across the hairline and the viewport's top edge: the one overlap
this band is geometrically capable of is guarded by nothing but the
current strings' lengths. One line of code (`maxLines = 1`,
overflow Clip/Ellipsis per the print laws) closes it; filed as S3
because it is latent, not live.

### HB-4 (evidence gap) — The header has never been rendered in any transform state

Every proving/jammed/sealed frame in `shots/` was composed WITHOUT
the header: `CascadeDeckRenderTest`'s `setDeck` composes
`TactileDeckScreen` directly (`app/src/test/java/com/example/CascadeDeckRenderTest.kt`,
`setDeck` — theme + screen, no `MonolithRootApp`), so
`cascade_proving_day.png`, `cascade_jammed_day.png`,
`cascade_jammed_night.png`, `cascade_sealed_day.png` show the hero
card at the frame's top edge — no band, no Guard line (verified by
eye on the proving frame's top crop this run). The only frames that
contain the band are the RootApp captures
(`ScreenshotHarnessTest.captureAllDestinations`), all idle: no
session, no proving, no shot, no detention. So the band's behaviour
in every state this audit was commissioned for rests on code
reading alone — which is why HB-1/HB-2 file from code, and why the
acquittals below that touch the shot (A2, A3) are construction
proofs, not render proofs.

**Decisive missing frames (for Ren to weigh a run; not run here):**
1. RootApp, mid-shot (t0+525, blur peak): band + vertigo in one
   frame — confirms A2's arithmetic in pixels.
2. RootApp, finish detention (finishState unissued, session row
   inactive), before and after a Guard-line tap — the HB-1
   reproduction; the after frame either shows PARK over the held
   frame (charge stands in pixels) or doesn't.
3. RootApp, Jammed: band present, Guard line frozen, tap inert —
   the A4 acquittal in pixels.

---

## ACQUITTALS — where the band is clean, by name

**A1 · Layout overlap — ACQUITTED.** The band is in-flow, not an
overlay: first child of the root Column, viewport `Box(weight(1f))`
below it, rail 52dp below that (`MainActivity.kt:384–395`, viewport
and rail in the same Column). No sibling overlaps another in a
Column; the band draws over nothing and nothing draws over the
band. Render citation: `shots/01_deck.png` — band, hairline, then
the Deck's ground, then the hero card, each in its own room.

**A2 · The vertigo cannot reach the band — ACQUITTED, by
arithmetic.** The shot's only upward-moving surface is the hero
card: `scaleX/scaleY = 1 − 0.06·vt`, `translationY = −10dp·vt`
(`TactileDeckScreen.kt:532–536`). Scaling is about the centre, so
the top edge's net travel is `−10dp + 0.03 × cardHeight`. The card
holds the 288dp wheel (`TactileDeckScreen.kt:646`) plus readout,
eyebrow and 36dp of vertical padding — height ≥ 430dp — so the top
edge moves **down** ≈3dp at full vertigo, never up. The blur peaks
at 6dp (`(6f × 4 × blurT(1−blurT)).dp`, `:530`) and the card's top
sits 8dp inside the scroll viewport (`.padding(vertical = 8.dp)`,
`:514`), so even the blur's fringe dies inside the Deck's own
padding, 0dp into the band. The band is safe from the shot by
construction. (Pixel confirmation is missing frame 1, HB-4.)

**A3 · The shot window itself (t0 → t0+1050) — ACQUITTED.** The
session row is born active at insert (`ArmProvingController.kt:33–48`,
its own header comment), so from t0 the host's `sealed` is true: the
clickable is removed from the Guard line
(`MonolithCustomComponents.kt:289–291` — the `.then(if (isSealed)
Modifier else Modifier.clickable…)` branch), `openPark()` refuses
(`MainActivity.kt:265–267`), and the print the band holds is the
seal-frame freeze — re-captured at the SealedRunning transition
(`MonolithViewModel.kt:722–748`) and preferred over the live store
for the session's whole life (`MonolithViewModel.kt:549–561`). The
door is shut and the print is frozen for exactly the span the
commission asked about. The exposure is at the window's two edges
(HB-2 before it, HB-1 after the session), not inside it.

**A4 · Jammed — ACQUITTED.** A jam keeps the row (annulment is the
latch+1500 door, and it deletes the row); while Jammed the row is
active, `sealed` holds, the door stays shut. Code path identical to
A3. (Pixel proof is missing frame 3, HB-4.)

**A5 · Sealed-running, the whole session — ACQUITTED.** Same gate
as A3, held for the row's life; PARK cannot be invoked
(`openPark()`'s guard, `MainActivity.kt:265–267`), and if PARK was
already open when a routine sealed the phone, the drawer degrades
to read-only furniture rather than a live surface
(`ParkDrawer.kt:147` `interactive = visible && settled && !isSealed`;
FIX absent while sealed, `:366–369`) and stays dismissible by its
scrim (`ParkDrawer.kt:201–214`, clickable gated on `visible` only).
That is F-B2's dormant ruling working as built. The header's one
loss in that state — its Guard tap can no longer close PARK, the
clickable being gone — costs nothing: the scrim and the rail's PARK
re-tap remain.

**A6 · The band's own touch appetite — ACQUITTED.** The band holds
no gesture consumer at all: no `pointerInput`, no drag, despite the
legacy name "PullDownHeader". The brand and the spacer are inert
print (`MonolithCustomComponents.kt:269–280`); the only clickable
in 48dp is the Guard text itself. Touches elsewhere in the strip
land on the band's own reserved room — there is no Deck content
behind or beneath it to steal from (A1). One nick, noted not
charged: the Guard target is text-tight (~15sp line height inside a
47dp row, no hit-padding at `:285–293`), so the door is smaller
than the row that advertises it — a target-size matter for the
reskin, not a swallow.

**A7 · The band's night step at t+400 — ACQUITTED.** During the
shot the header's ground steps with the scheme pin at t0+400, not
at t0 (`MainActivity.kt:233–238` comment and the `shotGround`
driver, `MonolithViewModel.kt:887–914`; theme gate at
`MainActivity.kt:123–131`). That is Zug-II §5.1's filed table — the
hero reads night at once, the ground steps at 400 — executed as
specced, not a flicker or a lag bug.

**A8 · The unmeasured state — ACQUITTED.** When the Watch Store has
never measured, `guardLineFor` returns null (`GuardLine.kt:36–38`)
and the header renders the brand alone (`MonolithCustomComponents.kt:281`,
`if (guardLine != null)`): no print, and therefore no phantom
tappable text claiming a guard state nobody measured.

---

## WHAT THIS AUDIT DID NOT COVER

- The rail (bottom band): filed and built already — my
  `20-band.md` §§1–3 and the merged `navigationLocked()` are its
  record. Nothing here re-opens it.
- Surfaces that cover the header (the receipt slip at root level,
  PARK's scrim inside the viewport, the full-screen overlays):
  other sheets' joints. Named only where the header's own tap is
  what admits them (HB-1, HB-2).
- Device truth: every statement above is code + rig renders. The
  detention tap (HB-1) and the two-finger proving tap (HB-2) are
  device-reproducible in under a minute each on the batch-7 build;
  the harness frames in HB-4 would settle them in the rig instead.

## NEXT

(a) *What I want next:* Ren's ruling on HB-1/HB-2's one fix — the
shared door predicate — seated as a small host-side car before the
reskin lands, because the reskin will re-skin this band and will
inherit whichever gate is in the host when it does. If the fix
lands, I want the re-audit: same census, new trunk, one page.
(b) *What I could do unassigned:* the same census on the receipt
slip's scrim — the one root-level surface that dismisses on "the
next DOWN anywhere" has never had its swallow audited either (what
does a DOWN on the rail, or on PARK's zone, spend while the slip
stands?). Or the AOD desk clock's dismissal surface, same method.

— Band, Improvers · the bottom nav (auditing one band up, this once)
