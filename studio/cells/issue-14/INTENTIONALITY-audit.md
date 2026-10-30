# INTENTIONALITY AUDIT — THE SETUP SHEET / FIRST-OPEN SURFACE

Seat: **Intentionality Designer (Anchor seat I-5, issue #14)**,
dedicated-100 programme. Cell: Pforte, branch `agent/issue-14`.
Law: synchronous — findings posted on issue #14 as they were
made, not held for a final pass. Nothing compiled; nobody
compiled for this audit. No message to Sarthak.

**Ground read whole:** `ReceptionSheet.kt` (1,296 lines,
current branch, post-Wave-2), `SetupStore.kt` (257 lines),
`MainActivity.kt` host wiring, `ParkDrawer.kt` stub row,
`GuardLine.kt`; Pforte's `studio/plan/AUDIT-setup-sheet.md`
(§1 inventory, S-1…S-15, §6 merge list, §8 acquittals);
`ISSUE-14-WAVE2-REPORT.md`; Stachel's `CRITIC-charges.md`
(C-01…C-18); the onboarding lane original
(`builds/onboarding`, ReceptionSheet.kt 692 lines) for what
the surface was before the merge.

**The test applied to every element, in order:**
1. What job does this element do that no other element on
   this surface already does?
2. If it were removed tonight, what exactly breaks — for the
   user, at first open, at 04:00?
3. Is its cost (height, attention, repetition, a tap, a
   scroll) paid by that job, or merely inherited?

An element that passes truth (Pforte/Stachel's question) can
still fail intentionality (mine): a true sentence printed
twice is dead weight once. A first-open surface has the
least right to carry dead weight of any surface in the app:
the user meets the app here, has granted it nothing yet,
and every unjustified element is read as the app's character.

**Verdict key:** KEEP (reason stated — the reason is the
element's licence to stay) · REMOVE (no sufficient reason;
named, in the open) · REDESIGN (a real job, wrong form,
size, dress, or neighbours).

---

## HEADLINES

**H-I1 · THE ROOM beat has no reason on this composition —
REMOVE until #13 lands.** (§3, element 34)
It teaches a 20-second settle this tree does not contain.
Its reason is entirely borrowed from another lane's future.
An element whose justification is "a different build will
make this true" is not an element of this surface; it is a
placeholder wearing print. Strike it from any composition
without flow-clock; restore it only in the act that composes
the settle machinery, re-worded against the real clock.

**H-I2 · The Guard derivation is printed twice on the sheet
itself — REMOVE the Guard line at the doors' foot.** (§2,
element 25)
`guardLineFor` at the doors' foot and the `GUARD n/4` total
above the tear are the same derivation, the same count, one
scroll apart. The total does strictly more (it prices every
lack, per kind). The foot line names only the first lack —
information the total, the cards, and the post-exit header
all carry. Truth acquitted it (same derivation, no fork);
intentionality does not acquit repetition. Its permanent
home is the Deck header and the PARK stub, where it is the
standing word — on the sheet it is a preview of a total the
user is about to reach anyway.

**H-I3 · The known charges, re-verdicted under
intentionality** (detail §5):
- **S-1 (total contradicts its cards):** in the current
  branch the sentence cure stands (per-kind pricing, aged-
  proof sentence asserts age only). Intentionality adds:
  the total *earns* its place only because it is the
  decision-moment price list at the exit — which is exactly
  why H-I2's duplicate must go. One total, at the tear,
  nowhere else on the sheet.
- **Reprinted register-struck sentences:** verified by grep
  on the current branch — `IT FIRES AS SET`, `YOUR STUDY
  RECORD LIVES IN YPT`, `BLOCKING STANDS WHOLE`,
  `MID-SESSION` survive **only in code comments recording
  their striking**, on no printed surface in
  `app/src/main/java`. Under intentionality these were
  never merely false: a struck sentence has *negative*
  reason — it spends trust the register already spent once
  to remove it. Verdict on the class: REMOVE, permanently;
  any future reprint is a regression by definition, no
  re-argument heard.
- **Device Admin escort line:** cured and now *earning* its
  place. The FIX opens the DeviceAdminSettings **list**
  (`MainActivity.kt` DeviceAdminSettings intent; no
  ACTION_ADD_DEVICE_ADMIN in tree), and the escort now says
  exactly that: "YOU'LL SEE A LIST OF DEVICE ADMINS. FIND
  MONOLITH, ACTIVATE, COME BACK." An escort line's only
  reason is to describe the destination truthfully enough
  that the user recognises it on arrival — the old line
  ("MONOLITH'S OWN PAGE") failed that job entirely. KEEP
  the cured form; the direct-page intent stays a priced
  follow-up, and no escort may promise it before OEM
  verification.

**H-I4 · The sheet's total length is itself an element, and
it is over budget.** Even after every KEEP below, the sheet
stands at roughly 2.3–2.9 viewports (Pforte §4 measurement:
~1,715dp all-SET to ~2,190dp all-MISSING against a ~760dp
viewport; F-6 collapse improves the SET case only). No
single element below is charged with this alone; the sum
is. Standing rule proposed for this surface: **no element
enters without an equal height leaving.** F-6 (granted
doors collapse) is the model citizen — it is the only
mechanism on the sheet that pays for itself in height.

---

## §1 · HEADER

**1. Nameplate `MONOLITH ML-01` — KEEP (weakest keep on the
sheet).**
Job: object identity, not information. It makes the sheet a
plate on a machine — the same document family as PARK's spec
plate and the stub — and costs one small line. It teaches
the user nothing and must never be asked to. Its licence is
conditional: if the plate family ever dies elsewhere, this
line dies with it; it does not stand alone.

**2. Title `SETUP + INTRODUCTION` — KEEP.**
Job: the document's name, worn identically by the PARK stub
row — one document, one name, findable again on day forty.
This is the header element that works for a living.

**3. Hairline under the title — KEEP.**
Job: separates plate from body; byte-identical ruling to
PARK's hairline (1dp @ 0.18f). A shared ruling is a reason,
not an accident: the user learns one grammar of lines.

**Header stack as a whole — noted, no charge.** Nameplate +
title + hairline + first eyebrow is four elements before the
first content sentence. Each survives individually above /
below; the stack is recorded so any future fifth header
element starts from a presumption of guilt.

## §2 · MOVEMENT 1 — THE IDENTITY

**4. Eyebrow `WHAT IT IS` — KEEP.**
Job: map, not decoration. On a 2,000dp document the three
eyebrows are the only navigation the user gets; this one
replaced the killed `THE DOOR` (which miscounted the sheet's
own doors). It names the movement's question plainly.

**5. Identity line ("While a session runs, Monolith shuts
the apps you ruled shut and the sites on your list — and
backs you out of Shorts and Reels.") — KEEP, first among
equals.**
Job: the one sentence the user must retain if he retains
nothing. It carries AppTitle weight because it is the hero
sentence, and its cured scope (ruled-shut tier + listed
sites + Shorts/Reels, S-7) is exact. If the sheet were cut
to five elements, this is one of them.

**6. The killed second sentence ("You seal a session. The
phone becomes the room…") — REMOVE stands, recorded as
precedent.**
It duplicated Movement 3 in miniature and spent truth-
credit early. Its removal is intentionality already applied
correctly by this cell; cited here so the pattern is named:
a summary of a section printed *above* that section has no
reason when the section itself is on the same surface.

**7. Boundary line ("YOUR STUDY HOURS LIVE IN YPT.
MONOLITH KEEPS NO SCORE AND GRADES NOTHING.") — KEEP.**
Job: kills two false expectations — that Monolith is his
study record, and that it will grade him — at the only
moment they can be killed cheaply, before first use. The
seat survives S-4; the words are the register's replacement,
verified in current code. YPT is undefined on the sheet and
that is acquitted: the sheet's reader runs YPT daily.

**8. Eyebrow `THE FOUR DOORS` — KEEP.**
Job: it counts, and the count is true — four cards follow.
An eyebrow that makes a checkable claim the next screenful
redeems is doing work.

## §3 · MOVEMENT 2 — THE FOUR DOORS

**9. Door card form (name + live status word + job line +
conditional escort/key + hairline) — KEEP.**
Job: the sheet's spine. The repetition across four cards is
not dead weight; it is a form being filled, and the user
learns the form once and reads three more doors at speed.
Uniformity here is the intentionality.

**10. Status words SET / NOT SET / UNPROVED / NOT CHECKED —
KEEP, as a set.**
Job: the sheet's core invention — an honest four-word
vocabulary where most setup screens print two (done /
not done) and lie by omission. Each word earns separately:
SET = fresh proof; NOT SET = measured absence; UNPROVED =
proof aged (not absence — S-1's whole lesson); NOT CHECKED
= never measured. Any future fifth word must pass the same
bar: a state the machine can actually distinguish (the
STUCK rider, S-12, is currently unmeasurable by the sweep
and correctly prints nothing).

**11. Door 1 — ACCESSIBILITY / THE INTERCEPTOR — KEEP.**
Job line ("Shuts the apps you named, the moment they
open.") is the blocking claim itself, in life words.
The appended rider "MONOLITH HOLDS NO NETWORK PERMISSION."
— KEEP: it sits at the scariest door, where the trust
question is asked, and it prints only the verifiable half
(manifest grep: INTERNET count 0; the struck "NOTHING
LEAVES THIS PHONE" overclaim stays struck — C-16).
Escort (full form on first unset card): KEEP — it pre-names
Android's strong warning before the jump, so the warning
cannot win at 04:00. That pre-naming is the escort's entire
reason and this door needs it most.

**12. Door 2 — USAGE ACCESS / THE WITNESS — KEEP.**
Job line ("Tells Monolith which app is in front, so the
seal can be proved.") earns by explaining *why a blocker
needs to watch* — the door a user would otherwise read as
surveillance for its own sake. Escort: KEEP (short, exact).

**13. Door 3 — DEVICE ADMIN / THE ANCHOR — KEEP.**
Job line cured (S-6): "While this stands, Android refuses
to uninstall Monolith." — a true, bounded claim.
Way-back rider ("TO TAKE IT BACK: SWITCH MONOLITH OFF IN
THAT SAME LIST — THEN IT UNINSTALLS LIKE ANY APP.") —
KEEP, and singled out: this is the only door that *feels*
irreversible, and the rider is the difference between a
user granting it and a user abandoning the sheet here.
Escort: KEEP in cured list form (H-I3 above).

**14. Door 4 — NOTIFICATION LISTENER / THE MAIL ROOM —
KEEP.**
Job line cured (S-10): scope is session-only, shut apps
only, digest as destination. Rider "HELD ON THIS PHONE, IN
THE DIGEST." — KEEP: same trust job as Door 1's rider, at
the door that reads private messages. Escort with warning
pre-named: KEEP.

**15. Job-line riders as a pattern — KEEP, asymmetry
intentional.**
Doors 1, 3, 4 carry a second sentence; Door 2 does not.
This is not inconsistency: each rider answers that door's
specific fear (data leaving / irreversibility / message
privacy). Door 2 has no equivalent fear and gets no rider.
Uniformity would be the accident here.

**16. Escort full form (first unset card) + short forms
(later cards) — KEEP.**
Job: the shared frame ("a list, find Monolith, come back")
is taught once, in full, where the user first needs it;
later cards keep only their door-specific clause and their
own Android warning whole. Repetition was priced and cut
(F-5) without cutting any fact. Model intentionality.

**17. STALE gloss ("THE LAST PROOF AGED OUT — CHECK
RE-PROVES IT") — KEEP.**
Job: UNPROVED is the one status word a first-time reader
cannot self-explain; the gloss is its dictionary entry,
printed only in the state it defines. Conditional print =
paid-for print.

**18. FIX key (per MISSING door, surfaceVariant dress) —
KEEP.**
Job: the only verb for a missing door, and the departure
point of the Return Chit — the key is a mechanism, not a
link. Dress: surfaceVariant, not ink — ink is DONE's alone,
so the eye learns one ink key per sheet. Label "FIX —
{name}": the repeated door name looks redundant under the
card header and is **kept on evidence** — it makes the key
a standalone, screen-reader-complete act when keys are
scanned out of context. Apparent redundancy, load-bearing.

**19. CHECK key (per STALE and per UNMEASURED door) —
KEEP.**
Job: a sweep is the only honest verb for an aged proof and
for an unread door; giving UNMEASURED a CHECK (S-12) means
NOT CHECKED is an actionable state, not a dead end. Voice:
FIX's knock values, no dip — same errand class, same sound.

**20. No key on a SET door — KEEP (an absence, credited).**
A granted door offers no action because none exists. Keys
that do nothing are how setup screens teach users to ignore
keys.

**21. Collapsed SET form (one ruled line, ~37dp vs ~89dp)
— KEEP, the sheet's best mechanism.**
Job: the sheet visibly completes as the user works, and
pays for itself in height (H-I4). It is the only progress
signal on the sheet and it is made of the doors themselves,
not a bar.

**22. Return Chit line (in the visited card) — KEEP, with
the expiry law as part of the element.**
Job: closes the excursion loop — without it, returning from
Settings is silent and the FIX's result must be re-derived
by the user from the status word alone. Its licence
includes `chitPrintsAgainst`: the verdict prints only while
the door's current print still agrees with it (C-15 cured).
Flag carried from Wave-2, still open: the ESCORT-class /
collapsed-row chit visibility must be verified for every
door class at composition — a chit that cannot print has
no reason to be judged.

**23. Card hairlines — KEEP.** The form's ruling; see
element 3.

**24. Rank line ("THE FIRST TWO MAKE BLOCKING REAL. THE
THIRD GUARDS THE MACHINE ITSELF. THE FOURTH MAKES IT
QUIET.") — KEEP.**
Job: the only element that *ranks*. The cards list; this
line prices a skip in advance, in door order, so leaving a
door unset is a decision, not an oversight. Third clause in
cured S-6 form. Two human seats acquitted it as the sheet's
best sentence after the identity; intentionality concurs —
it is the skip's price tag, printed before the skip.

**25. Guard line at the doors' foot (`guardLineFor` print)
— REMOVE from the sheet. (H-I2)**
It duplicates the total (element 36) in count and
derivation, names strictly less (first lack only), and sits
one movement before the total that supersedes it. Its
standing homes — Deck header, PARK stub — are untouched by
this verdict; there it is the permanent word and earns its
place daily. On the sheet it is a checkpoint to a checkpoint.
If composition insists a mid-sheet count is needed, the
burden is a user-visible job the total does not already do;
none was found in any filing.

## §4 · MOVEMENT 3 — HOW A SESSION RUNS

**26. Eyebrow `HOW A SESSION RUNS` — KEEP.** Map; see
element 4.

**27. Beats as a ruled table (84dp label column, Anonymous
Pro labels, hairline per row) — KEEP the form.**
Job: six one-line facts read as a ledger, not a paragraph.
PEEK and BREAK never share a line — two mechanisms, two
prices, printed apart.

**28. ARM beat ("The wheel on the Deck sets the span.
Nothing is shut yet.") — KEEP.**
Job: the second sentence. It kills the natural false belief
that arming already blocks — a misunderstanding that would
otherwise be discovered mid-study.

**29. SEAL beat (cured S-7 wording) — KEEP.**
Job: names the instant blocking becomes real and its exact
scope (shut-tier apps + listed sites). ARM says "not yet";
SEAL says "now, and this much". The pair earns both seats.

**30. PEEK beat ("One app opens for two minutes, then shuts
again. Taking it starts a 20-minute cooldown on that app.")
— KEEP today; REDESIGN at the times merge.**
The flat durations are true today against hard constants
(120s / 600s; cooldown constant by law). The day peek/break
become settings (batch-10, MIGRATION_12_13), flat print
becomes false print (S-3) — the beat must then read value
slots or be struck. Its reason is teaching the peek's price
before the first session; that reason survives the redesign.

**31. BREAK beat ("Ten minutes. The peek doors open; the
shut doors stay shut. The clock runs through it.") — KEEP
today; REDESIGN with element 30.**
Job distinct from PEEK: a break is not a long peek — the
tier clause and the running clock are the two facts a user
would get wrong. Same S-3 slot condition.

**32. FINISH beat (cured S-15 wording) — KEEP.**
Job: closes the story the identity opened — a session that
runs its course prints a receipt. It is also the sheet's
only forward reference to the receipt, the surface the user
has already loved; the promise is kept small and exact.

**33. — see element 34 ordering note: beats print
ARM / SEAL / THE ROOM / PEEK / BREAK / FINISH in code.**

**34. THE ROOM beat ("After twenty seconds untouched, the
desk clock takes the face: the time, the time left, when it
ends.") — REMOVE from this composition. (H-I1)**
No settle machinery exists in this tree (desk clock is a
manual tap; it prints one figure, not this beat's three —
Stachel C-11, Wave-2 S-8 flag in code). Every other beat
teaches a mechanism the user can use tonight; this one
teaches a future. Intentionality verdict is stricter than
the truth verdict: even if the sentence were merely
unproven rather than false, an element whose whole reason
lives in another issue (#13) has no licence on this surface.
Restore only inside the #13+#14 composition act, re-worded
against the composed clock (incl. the stopwatch and
dismissal riders), never before it.

**35. First-routine line ("NEXT ROUTINE — {name} · {hh:mm}
· SET ON SHIELD") — KEEP in cured form.**
Job: hands over the seeded day — it proves routines exist
and where they live, without making routine setup a step on
this sheet, and (S-2 cured) claims a next start, never a
firing. The struck "IT FIRES AS SET" form is element-class
REMOVE, permanently (H-I3).

**36. Total — `GUARD n/4` (Anonymous Pro Bold 15) + total
sentence — KEEP, as the sheet's only total.**
Job: the decision-moment price list directly above the
exit — every lacking door priced, per print kind, in
counted order (S-1 cure verified in current code:
`receptionTotal` composes MISSING absence sentences and
STALE aged-proof sentences; count = fresh-GRANTED only, and
the sentence set beside a 0/4-over-aged-doors count is what
stops the count reading as absence). All-set branch is
"ALL FOUR DOORS SET." — full stop (S-9 cured: the warrant's
third conjunct is not this sheet's to declare). The count
is a datum, so telemetry type carries it; the sentence is
prose, so prose type carries it. This element is why
element 25 must go.

**37. `YOUR RULES LIVE ON SHIELD` line (with "CHANGE THEM
ANY TIME BETWEEN SESSIONS", S-14) — KEEP.**
Job: locates the rules the identity line presupposed ("the
apps you ruled shut") and bounds editing to between
sessions. Without it, OPEN THE LIST below is a key to
nowhere named.

**38. OPEN THE LIST key — REDESIGN (demote the dress, keep
the door).**
Job is real: the Shield excursion exists so ruling apps is
available on day one without becoming a setup step (M-10).
But its form lies about its rank: a full-width 48dp key in
the same dress family as the FIX keys reads as *required
step five* of the setup, and the sheet's own law says the
first morning is never routed through a list. Demote to a
row-weight / text-key treatment visibly below FIX/DONE in
rank, keep the gate knock (its one voice is earned — the
act has no return verdict to pay its mass later), and keep
element 39 attached to it.

**39. Shield way-back line ("THE LIST LIVES ON SHIELD —
COME BACK TO THE DECK AND THIS SHEET RISES AGAIN.") —
KEEP.**
Job: an excursion the sheet offers mid-errand owes a printed
return (H-2). It and the host's re-raise law are one fact;
if the host law falls, this line must fall with it.

**40. LEAVE WHEN YOU LIKE line (S-11 wording: "THE HEADER
KEEPS NAMING THE FIRST THING MISSING.") — KEEP.**
Job: the anti-trap law, printed at the foot where the
decision to leave is made. It converts abandonment from a
guilt exit into a stated, priced, reversible act — and its
second clause tells the user the sheet's memory is the
header, not a nag.

**41. Stub note ("THIS SHEET WAITS IN PARK — SETUP +
INTRODUCTION.") — KEEP.**
Job: tells the user, before the tear, where this document
lives for the rest of the app's life. Printed above the
tear (F-12) because it describes the part that stays.

**42. PARK direction line ("PARK OPENS BY TAKING THE RAIL
CAP TO THE UNMARKED END OF THE RAIL, OR BY TAPPING THE
GUARD LINE AT THE TOP RIGHT.") — REDESIGN (shorten).**
Job is real — the stub is useless if PARK cannot be found —
but this is the wordiest element on the sheet relative to
its job, and it teaches *two* openings where one unconditional
door would do. Keep the rail-cap opening (the unconditional
one); drop or subordinate the Guard-line alternative. A
direction that needs a second read has failed as a direction.

**43. Tear rule (dashed, 0.35f, PerforationRow geometry) —
KEEP.**
Job: the sheet's ending made physical — it marks exactly
what stays (above: the stub's facts) from what leaves
(below: DONE). As originally built (a static dashed line
that never tore) it was the sheet's one false print; with
the tear choreography it performs its meaning.

**44. DONE key `TO THE DECK →` (ink, 52dp, the sheet's one
ink key) — KEEP.**
Job: the sole stamping exit, named for what it does. Ink
exclusivity is its licence: one ink key per sheet is how
the user knows which key *commits*. Live at any door state
— DONE is the honest skip; a gated DONE would make elements
24 and 40 liars.

**45. DONE act line ("THIS FINISHES THE SETUP. IT STARTS
NOTHING, SEALS NOTHING, AND MAY BE PRESSED WITH DOORS
UNSET.") — KEEP.**
Job: defines DONE by its three negations at the moment of
pressing. Without it, "TO THE DECK" over-promises (a user
could reasonably believe it starts a session). Ten words
that prevent the sheet's most likely misread — cheapest
insurance on the surface.

## §5 · THE STUB (PARK) AND THE HOST

**46. PARK stub row (`SETUP + INTRODUCTION` + its right
word) — KEEP.**
Job: day-forty re-entry to the same document, reprinted
live. Right word order — Armed Handover, else Guard line,
else "OPEN" — is intentional: handover is a one-shot act
instruction, Guard is the standing fact, OPEN is an
invitation, and the three never masquerade as each other.
Flag, minor: "OPEN" is the stub's weakest word — the only
non-status, non-act word on either surface. It survives
only as an invitation where no Guard fact stands
(unmeasured); if a status-honest fallback is found, it
replaces OPEN without ceremony.

**47. Dashed rule above the stub row — KEEP.**
Job: the stub reads as torn in PARK's own grammar — the
user recognises the document by its tear before reading a
word. Every other PARK rule stays a hairline; the exception
is the meaning.

**48. Armed Handover print (Deck Guard slot / stub) —
KEEP, in its own slot.**
"GUARD 4/4 — ONE ACT LEFT: SLIDE." earns its place: DONE's
founding act made visible at the one moment the slide is
in front of him. C-13's amendment binds — it extends the
slot, it does not become a fourth Guard-line state.

**49. First-open auto-rise itself (sheet opens unstamped,
re-offers each cold start, gated !sealed, BACK unstamped)
— KEEP.**
The interruption is the element everything else justifies:
permissions walked *before* the first seal attempt, never
discovered at a refusal. Its licence is elements 40 + 44 —
leave any time, stamp at any state. Remove either and this
verdict falls.

**50. Non-printing costs — sounds, motion, re-derive —
KEEP as a set, each with its act.**
Seat knocks on FIX/CHECK (identical call = same errand
class), the heavier gate knock on OPEN THE LIST (no return
verdict exists to pay later), silence-then-tear-pulse on a
stamping DONE, firm click on a reprint DONE, night amplitudes
lower (the 04:00 user is the design user). Rise 280 SEAT /
tear exit 420 / other leaves 160 THROW: the durations are a
vocabulary, not decoration. The 5-second re-derive earns its
cost: it is what allows a door to go UNPROVED in front of
the user — honesty happening live, the sheet's thesis in
motion. No sound, motion, or refresh on this surface is
unattached to an act; none was found ornamental.

## §6 · INTENTIONAL ABSENCES — credited

An intentionality audit also records what is *not* there,
when the absence is the design:

- **No wizard pagination / progress dots.** Doors are
  unordered and skippable; a progress bar would assert an
  order and a completion the doors do not have. The
  collapsing cards (element 21) are the progress signal.
- **No skip button.** DONE-at-any-state *is* the skip,
  honestly named and priced (elements 24, 44, 45). A
  separate skip would be a second, lesser exit.
- **No close X.** BACK and DONE are the exits; a third
  would need a third semantics, and none exists.
- **No account, money, or score ask.** The boundary line
  (element 7) promises it; the surface keeps the promise
  by containing none.
- **No illustrations, no AI-generated images, no green.**
  Standing law, obeyed: the sheet's visual character is
  type and ruling only — Sora display, Atkinson Hyperlegible
  body, Anonymous Pro telemetry, each family used only in
  its assigned role (status words are words, in Atkinson;
  counts are data, in Anonymous Pro; the title is display,
  in Sora).

## §7 · THE BILL — for Pforte / composition, in order

1. **REMOVE the ROOM beat** from any composition without
   #13's settle machinery (element 34). FREE. Gate named:
   whoever composes #13+#14 restores it, re-worded.
2. **REMOVE the Guard line at the doors' foot** from the
   sheet (element 25). FREE. Deck header + PARK stub
   printings untouched.
3. **REDESIGN OPEN THE LIST's dress** below FIX/DONE rank
   (element 38). SMALL. Key, knock, and way-back line stay.
4. **REDESIGN the PARK direction line** to the one
   unconditional opening (element 42). FREE.
5. **Carry the standing conditions:** PEEK/BREAK value
   slots at the times merge (elements 30–31); ESCORT-class
   chit visibility verified at composition (element 22);
   "OPEN" fallback replaced if an honest word is found
   (element 46); the height budget — no element enters
   without an equal height leaving (H-I4).
6. **No other element is licensed to move.** Every other
   element above is KEEP with its reason stated; a future
   change to any of them must answer the reason, not just
   the element.

**Counts:** 50 elements audited (incl. credited absences
as a set and non-printing costs as a set) — KEEP 42 ·
REMOVE 3 (the killed second sentence, recorded; the ROOM
beat, this composition; the doors'-foot Guard line) ·
REDESIGN 5 (OPEN THE LIST dress, PARK direction line,
PEEK + BREAK at the times merge, "OPEN" fallback minor).

## §8 · ADDENDUM — landed during this audit (synchronous)

Two commits landed on this branch while the audit was being
written: `25a5c52` (chit suffix made state-aware — strengthens
element 22's KEEP; the suffix can no longer claim ALL FOUR
SET beside an unset visited door) and `c86ddd0`, PORTAL's
`DESIGN-reception-sheet-portal.md` (paper only). PORTAL's
proposed **INV-1 proof table under the GUARD total** is a
NEW element seeking entry, so it is audited here on arrival:
it would re-print, in table form, per-door facts the cards
already print and the total's sentences already price. Under
H-I4's budget rule and element 25's precedent it starts from
a presumption of guilt: its licence would have to be a job
neither cards nor total do (e.g. a proof *derivation* the
user can check line-by-line against the cards — an audit
trail, not a summary). As a summary it is REMOVE-before-entry;
as a checkable derivation it may argue its case to Pforte
with its height paid by an equal removal. The One-Reading
construction itself (single SheetReading at the root, S-1
impossible to draw) is mechanism, not surface, and is
acquitted without an element charge.

— Intentionality Designer (Anchor seat I-5, issue #14)
