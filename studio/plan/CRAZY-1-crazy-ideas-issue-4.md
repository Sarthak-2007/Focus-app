# CRAZY-1 — Crazy-ideas for issue #4 (Deck top strip as an object)

Seat: CRAZY-1, Crazy-ideas / Wild / Innovators (AGENT-TYPES T01), seat 1 of 5, under DURCH-A.
Date: Oct 31, 2026. Status: proposals only — nothing built, nothing compiled. The Bolt (design
of record c5de8d2, PR #500) remains the design of record; PR #500 is HELD on 5
BoltRenderGateTest failures (IllegalStateException at BoltRenderGateTest.kt:62, setContent),
compile green, SealBarStateTest 16/16 (DURCH-MOP verdict, PR #500 comment 6532041733).

## Constraints honoured by every idea
- Strip is an OBJECT, never a text dump. Face vocabulary stays: END engraving + HH:mm only.
- NO CLUTTER is a judged axis — every idea carries an element count.
- Commit stays on the slide register below (Slide → TAP CAN COMMIT, Sarthak Oct 29);
  the strip is print, never a handle (DRUCK §1). Housing tap, if ever ruled, routes to the
  existing refusal/inspect sentence slot below — never a second commit gesture.
- One derivation: every face folds from the existing BoltInputs / boltStateFor. No new
  inputs, no DB, no service, no permission. B-C1's shield-beat input stays owed, never faked.
- 60dp housing, 360dp reference, window 102×36dp, Anonymous Pro numerals, Sora/Atkinson.
- Palette only: #F04FCB / #C84481 magenta family, #E2BC39 brass, #493EA4 indigo,
  #E47E35, #377ED0, #34323B, warning #D63E06. Never green. Hand-built Canvas only.
- No idle animation. Verdict changes never animated; pose changes ≤ the existing 50ms throw.
- All faces mapped: OFF, OFF_WHILE_RUNNING (renders as OFF, spoken sentence differs),
  UNPROVEN, NO_READ, BLOCKING, RUNNING, RUNNING_UNPROVEN, STALE_WHILE_RUNNING,
  ENDING_SOON, DETENTION_FAULT, FLOW.

## C1 · THE TAMPER TAPE — proof as pattern continuity (flagship)
Mechanism, faces, build, price (M), clutter (3 elements) and kill test: as posted in
wave 1 on #4 (comment 6535379700). Core claim: chevron phase alignment is a second,
independent physical proof channel — pose (tape pressed) and proof (pattern aligned)
can disagree on the face, which is exactly RUNNING_UNPROVEN and STALE drawn honestly.

## C2 · THE FERROFLUID JEWEL — a lamp that cannot remember (graft-grade)
As wave 1. Spiked / puddle / puddle+residue-ring, 3 static Canvas poses in the existing
17dp bezel. Price S. Grafts onto the Bolt unchanged, or onto C1's keeper end.
STALE's residue ring is a physical trace ("was live, is not"), not a shutter.

## C3 · THE CANAL LOCK — gates + waterline (full replacement candidate)
As wave 1. Gates = pose, waterline = contents/proof channel, locking beam = the
RUNNING_UNPROVEN distinction (shut but unbarred). Price M–L. STALE = shut gates,
drained chamber — an empty lock cannot be misread as protection.

## C4 · THE AIRLOCK — the warrant logic, drawn as an interlock
Two door leaves in series with a small chamber between them — the Deck IS an airlock
between the student and the apps, and an airlock's one law is physical: both doors are
never open together, and the chamber only pressurises when both are shut.
- Pose channel: outer leaf (left) = the seal act; inner leaf (right) = the shield side.
- Proof channel: chamber fill. Empty/dark chamber = unproven. Brass fill = BLOCKING.
  Magenta fill = RUNNING. Both leaves shut + chamber empty = RUNNING_UNPROVEN and
  STALE share the drawing "shut but unpressurised" — separated by the window shutter
  (STALE keeps the Bolt's shuttered window; RUNNING_UNPROVEN keeps live numerals).
- OFF = outer leaf open, chamber and inner leaf visible, floor orange. UNPROVEN =
  outer leaf at 1/3, inner shut. DETENTION = both shut, chamber magenta, inner leaf
  bowed 1dp against its frame. ENDING_SOON = chamber fill unchanged, window inversion.
- Build: 2 leaf rects with pivot offsets, 1 chamber rect (fill by state), window as-is.
  Price M (~300 LOC). Element count 4. Consultation value: this is the only object
  whose mechanism is the same shape as the system's actual two-barrier logic
  (warrant + shield, B-C1) — when B-C1 lands, the chamber can be driven by the
  shield beat and the inner leaf by the warrant, and the face becomes a true
  two-input instrument instead of a one-input fold. Filed as a forward design, not
  a claim about today's inputs.

## C5 · THE BREAK-GLASS BOX — fragility as proof, fog as STALE
A small glass pane in a brass frame, hammer chained beside it (the fire-alarm box).
Glass can only be unbroken if the seal was never disturbed — tamper evidence by
fragility. OFF = pane swung open on its hinge, empty orange interior. UNPROVEN =
pane closed, latch unthrown (visible 2dp gap at the latch edge). BLOCKING/RUNNING =
pane closed, latched, glass clear, jewel visible behind the glass (brass/magenta) —
proof seen THROUGH an intact pane. RUNNING_UNPROVEN = pane closed and latched,
jewel behind it dark. STALE = pane intact but fogged from inside (alpha wash +
droplet dots): something is behind the glass and you cannot verify it — the most
literal STALE face proposed on this issue. REVOKED/JAMMED (mid-session bind loss) =
star-crack from one corner, cracks held in laminate, jewel dark behind cracked glass.
ENDING_SOON = window inversion only. DETENTION = pane latched, hammer lifted off
its hook against the glass, unstruck.
- Build: pane rect + hinge, crack = 6–8 polylines from an impact point (JAMMED only),
  fog = gradient + ~20 dots (STALE only), jewel reused. Price M (~280 LOC).
  Element count 4 (frame+pane counted as one object, hammer, window).
- Honest risk: the hammer invites a tap reading. Mitigation: hammer is 10dp,
  chained, rendered flat print like the Bolt's pin — and the kill test is a cold
  read: if testers tap the glass expecting commit, C5 dies; commit lives on the slide.

## C6 · THE KEYSTONE ARCH — Monolith as masonry (icon candidate)
A low arch of 5 stone blocks spanning the channel; the centre keystone drops in last
and the arch only stands because it is there — remove it and the voussoirs slump.
Name-true: a monolith is stone; this is the strip as a built stone structure.
- OFF = keystone resting in a tray notch at the right, blocks slumped 2–3dp at
  uneven heights, gaps dark orange at the crown. UNPROVEN = keystone wedged at 1/3
  drop, arch visibly racked (one side 1dp lower). BLOCKING = keystone seated,
  arch true, crown line brass. RUNNING = seated, crown line magenta. RUNNING_UNPROVEN
  = keystone seated but crown line dark and one hairline crack in the keystone —
  standing, unproven. STALE = arch true, crown dark, dust/matte wash over the stones
  (gloss gone, as the Wax Seal's stale). ENDING_SOON = window inversion. DETENTION =
  keystone seated with a brass strap across the crown (it refused to come out).
- Build: 5 trapezoids (Canvas paths), keystone offset by state, crown line 2dp.
  Price M (~260 LOC). Element count 3 (arch, keystone counted in arch, window).
- Weakness, self-filed: STALE vs RUNNING_UNPROVEN differ only in wash vs crack —
  too close at 10% brightness. C6 needs C2's residue logic or it stays an icon
  sketch, not a strip. Ranked accordingly.

## C7 · THE ICE BAR — the seal as a phase of water (short spec)
A shallow tray across the channel. OFF = tray empty, orange floor. UNPROVEN =
slush (broken pale shards, gaps of dark water). BLOCKING = one solid translucent
ice bar, brass tint at its core. RUNNING = solid bar, magenta core, trapped bubble
dots frozen mid-bar. RUNNING_UNPROVEN = solid bar, core dark — frozen shape,
no live centre. STALE = bar still solid but surface frosted opaque white-grey:
shape says held, surface says you can no longer see in. ENDING_SOON = hairline
cracks multiply in the bar (3 poses: 0 / 2 / 5 cracks by remaining time buckets,
window inversion still primary). REVOKED = bar split in two with a melt gap.
- Build: rounded rect + crack polylines + bubble dots, all static poses. Price S–M
  (~200 LOC). Element count 3. Risk: translucency at night mode / 10% brightness
  may collapse all faces into one pale bar — render-gated like C2.

## Grafts onto the Bolt (steal without changing its body)
- GA · Chevron seam on the bolt/keeper: print C1's two-half chevron across the
  bolt's leading edge and the keeper socket. Bolt thrown + chevrons aligned =
  proven; thrown + offset = RUNNING_UNPROVEN gets a face-level proof mark the
  dark jewel alone cannot give. ~40 LOC of Canvas in the existing plate draw.
  This is my single highest value-per-line proposal on the issue.
- GB · Ferrofluid jewel (C2) in the existing bezel. Price S.
- GC · Fog, not just shutters, for STALE: add C5's interior fog to the window
  glass in STALE so "unverifiable" reads on the window as well as the lamp.
  ~20 LOC (one gradient), reuses the shutter row of the fold.
- GD · Keystone notch: re-cut the bolt's pin notch as a wedge (keystone profile)
  so the dropped pin reads as a seated wedge, not a peg — arrival witness KRALL
  K-3 asked for, in one path change. Geometry unchanged otherwise.
- GE · Residue ring (from C2) printed around the jewel bezel in STALE only —
  works even if the full ferrofluid lamp is rejected.

## Bench (one-liners, free to steal, not specced)
Pneumatic tube capsule (capsule docked far = sealed, stuck mid-tube = caught,
here = off; ring seal on the capsule = proof) · blister-pack dome (convex =
intact dose, concave torn = spent — one 12dp dome as a secondary proof mark)
· hurricane lamp (chimney + static flame silhouette; rejected as primary —
flame reads as decoration, not state) · ECG trace (REJECTED: a trace is a
printout wearing a line's clothes — this issue exists to kill printouts) ·
sundial shadow (REJECTED: shadow position confuses state with time-of-day).

## Ranked verdict (full text on #4, wave 2)
1. Ship the Bolt (fix the #500 harness hold) + grafts GA + GB/GE + GC.
2. If the Bolt is ever reopened as an object: C1 TAMPER TAPE is the replacement
   I would panel against it; C3 CANAL LOCK second; C4 AIRLOCK third (first
   once B-C1 lands). C5/C6/C7 stay on file with their kill tests named.

— CRAZY-1 (Crazy-ideas / Wild / Innovators, T01)
