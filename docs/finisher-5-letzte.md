# FINISHER 5 — CROSS-DOOR CONSISTENCY (Issue #11, Held Door)

Seat: LETZTE, Finisher 5, Tor's cell · Lead: Tor · For: Tor only
Slice: the five doors wearing one face — does the *fixed* face print
one grammar on every path, or five dialects?
Ground read whole/in-part in the issue-11 clone (branch
`agent/issue-11`): `studio/plan/AUDIT-held-door.md`;
`docs/designer-skeleton.md` (the buildable fixed face — cited as
*Skeleton* §); `docs/tor-plan.md`;
`service/MonolithAccessibilityService.kt` (call sites :207 guard,
:233 sniper, :261 site, :288 app tier; `InterceptPath` :46);
`service/MonolithShieldService.kt` (shield door :178–190);
`ui/components/AirlockAndDisarmOverlays.kt` (`AirlockInterceptOverlay`
:548, prints :227–272); `ui/MonolithViewModel.kt` (`peekOfferFor`
:141, `AirlockInterceptState` :153, `triggerAirlockIntercept` :2315);
`MainActivity.kt` (intent intake :126, overlay wiring :793);
`data/MonolithModels.kt` (`AppBlockTier` :13);
`data/AppCatalogReconcile.kt` (factory presets :52).
Nobody compiled. Nothing here messages the user.

**Basis.** The matrix below describes the FIXED face — audit
FIX-1…FIX-8 as specced to buildable numbers by the Skeleton — not
the clone's current code. Clone state at read time is intermediate:
FIX-1/FIX-2 are in (paths passed at all four a11y call sites,
`leaseLive` derived at the hoisted read, span end at VM :2368),
FIX-3…FIX-8 are specced, not built (the overlay still carries the
hero numeral, `EMERGENCY PEEK`, the `AIRLOCK INTERCEPTION •`
eyebrow, and both self-ticking counters). Where a flag depends on
an unratified Skeleton item, it says so. (`docs/tor-round3.md`,
filed during this pass, rules gate conduct — silence, linear
derived drain, no `0:00` frame, chip tappable through the fade —
inside the single shared overlay; it is path-independent and
changes no cell below.)

---

## 1. What cannot diverge (the single-sourced spine)

Everything downstream of `triggerAirlockIntercept` is computed
once, in one place, for all five paths — so these slots are
consistent *by construction*, and I acquit them as a family:

- **One overlay, one skeleton** (Skeleton §3): top-anchored card,
  Night palette, border α0.38, body α0.92, entry `fadeIn(specA 180)`,
  exit `fadeOut(specA 260)`, eyebrow dead, badge word alone in the
  footer (Anonymous Pro Bold 10sp, `NightTextColorSecondary`,
  uppercased).
- **Intention (H2)** — his words bare at 16sp directly under the
  name, blank → empty-held silence, identical on all five doors
  (one dependency: flag X-5).
- **Session line (H3 line 1)** — one function
  (`airlockSessionLine`), one value (FIX-2's span end, computed
  once in the VM regardless of path). See §4, A-1.
- **RETURN** — `RETURN TO FOCUS`, 56dp slab, ink #471602 on
  #F47442, live from frame one on every door in every state.
- **Honest exit** — `END THE SESSION INSTEAD` under RETURN on
  every door, including never-owed doors (Skeleton §8 matrix).
- **Peek derivation** — one pure function (`peekOfferFor`) plus
  grant-time re-derivation; one withheld vocabulary; one price
  sentence; one badge derivation. The offer matrix itself is
  acquitted by the audit and I do not reopen it.

Everything that *can* diverge enters upstream of the VM: the five
call sites compose `appName` / `reasonBadge` / `path` by five
different hands. The matrix is therefore a map of the call sites,
not of the overlay.

## 2. The consistency matrix — the fixed face, door by door

Slots: **H1** name (Sora Bold 24sp, one line, ellipsis) ·
**Sub-line** = the door-specific line, H3 line 2 in the Skeleton's
stack (the intention H2 is common to all doors, §1) ·
**Badge** = footer word alone, uppercased as printed ·
**Peek zone** = Skeleton §3.3/§5 variants: `OFFER-PENDING` (gate
stack `PEEK OPENS IN {M:SS}` + draining rule, price standing in
Z2, memory in Z3) → `OFFER-ARMED` (48dp chip `PEEK` + derived
badge, price in Z2) · `HELD` (withheld line in Z1 from frame one,
Z2 empty, Z3 memory, FIX-5c in-place resolution) · `ABSENT`
(Z = 0dp, no peek line of any kind, 292dp card; the 110dp
variants stand on a 418dp card) ·
**Session line** = H3 line 1.

| Door (path) | H1 — NAME prints | Sub-line (H3 L2) | Badge (footer) | Peek zone it can show | Session line |
|---|---|---|---|---|---|
| **App tier** (`APP_TIER`, a11y branch 5) | The app row's real name, verbatim (`appRule.appName` — e.g. WhatsApp, YouTube) | Break appointment only: `BREAK AT {HH:mm} OPENS THIS APP` iff tier is `PEEK_2M` and the session's schedule has a next break; else empty-held | Tier's badge: `BLOCKED` (LOCKED) or `2M PEEK` (PEEK_2M) — see flag X-2 | **Full range.** PEEK_2M → 110dp: OFFER-PENDING → OFFER-ARMED, or HELD (`PEEK COOLDOWN — AGAIN IN {n} MIN` / `A PEEK IS ALREADY RUNNING` / `PEEK IS OFF`). LOCKED → ABSENT | Identical on all five doors: timed → `SESSION ENDS {HH:mm}` (span end, FIX-2); stopwatch → `SESSION RUNNING · NO SET END`; no session → silence (unreachable from any service — all five fire only inside an active session) |
| **Shield path** (`SHIELD_APP`, shield 2s poll) | Same row, same source as app tier (`blockedApp?.appName`); row-missing fallback `Blocked App` | Same as app tier (break appointment on PEEK_2M + next break; else empty) | Same tier badge as app tier; row-missing fallback `Blocked` after the sweep (flag X-3) | **Full range, identical derivation** — for the same package + tier this door is print-for-print indistinguishable from the app-tier door (the good kind of sameness) | Identical (see app tier) |
| **Uninstall guard** (`UNINSTALL_GUARD`, a11y branch 2) | **What he opened** (ruled): the Settings app's label for `com.android.settings`; the installer's label for `com.google.android.packageinstaller` / `com.android.packageinstaller` / `com.samsung.android.packageinstaller` / `com.miui.packageinstaller`; unresolvable → package name verbatim. The fiction `System Settings Guard` dies | None — always empty-held (never a peek door, never a site door; this is the emptiest sub-line of the five) | `UNINSTALL GUARD` — a hardcoded door-identity literal at the call site, not a tier badge (flag X-1) | **ABSENT, always.** `canHonourPeekLease` false, package unlisted; withheld is null by construction. 292dp card every visit | Identical (fires inside breaks too — span line is the FIX-2 cure's main beneficiary here along with sniper/site) |
| **Shorts/Reels sniper** (`SNIPER`, a11y branch 3) | **Kept** (ruled): `YouTube Shorts` / `Instagram Reels` — the feed, not the app; chosen by `pkgName.contains("youtube")`, else Reels | None — always empty-held | `SHORTS BLOCKED` / `REELS BLOCKED` — hardcoded literals paired to the name choice (flag X-7) | **ABSENT, always** — even for YouTube, whose factory preset tier is `PEEK_2M` (flag X-4). A live lease silences this door entirely (UNA2-01); a running cooldown prints nothing here | Identical (fires during Safe Breaks by design; pre-fix this door printed "ends now" for whole breaks — FIX-2 cures it) |
| **Social/site door** (`SITE`, a11y branch 4) | **The browser's name** (ruled): the intercepted package's app label — Chrome / Brave / Firefox / Edge / Samsung Internet (`com.android.chrome`, `com.brave.browser`, `org.mozilla.firefox`, `com.microsoft.emmx`, `com.sec.android.app.sbrowser`). Mechanism unspecced — flag X-4a | **The matched domain**, verbatim from his stored list (first containing match, `matchBlockedDomain`), in `nameSubLine` — the only door with a factual sub-line on *every* visit; contention-free because SITE never owes a break appointment | `SITE BLOCKED` — hardcoded door-identity literal | **ABSENT, always** — even if the browser's row were PEEK_2M; Chrome's factory preset is OPEN, so this is normally the *only* door Chrome ever meets (flag X-6) | Identical (fires during Safe Breaks by design; same FIX-2 note as sniper) |

**Reading the matrix in one breath:** two doors (app tier, shield)
print *app + tier* and carry the full peek grammar; three doors
(guard, sniper, site) print *event + door badge* and carry no peek
grammar at all. That split is the audit's law (Baseline 3 + 4),
not an accident — but it is the seam every flag below hangs on.

## 3. Flags

### BREAKS — the fixed grammar does not hold as specced

**X-1 · The footer slot speaks two grammars. (All five doors;
structural, uncured by any FIX.)**
On the honouring doors the badge is the app's *tier*
(`Blocked` / `2m Peek` from `AppBlockTier.badge`); on the three
unconditional doors it is the *door's identity*
(`Uninstall Guard` / `Shorts Blocked` / `Site Blocked`). Same
slot, same typeface, two different questions answered: "what is
this app?" vs "which door stopped you?". The audit's name-slot
cure (fact in H1, door in the badge) actually *creates* the
clean version of this on the guard door — and leaves the
app-tier doors as the odd ones: a LOCKED app-tier door's footer
says only `BLOCKED`, the least informative print on the face,
while every unconditional door's footer names its mechanism.
Not a fiction; a grammar flip at the seam. Tor's call whether
the tier-badge family is ratified as its own dialect — if yes,
say so in the wording book so no future seat "fixes" one side
into the other by accident.

**X-2 · Footer `2M PEEK` is the one literal duration left
standing. (App tier + shield, PEEK_2M only.)**
Skeleton §4 flags it; I second it as a consistency break, not a
taste note. FIX-8 makes the chip's badge derived (`90s → 1m30s`,
never truncated) while the footer would still print the tier's
stored literal `2m Peek` → `2M PEEK`. The day the editable peek
length lands, one face carries two badges for one lease and one
of them lies. Every other duration on the fixed face is derived.
Tor to ratify the Skeleton's recommendation (footer prints
`PEEK` alone on peek-tier doors) *before* the builder reaches
FIX-6's badge sweep — it is a one-word decision now and a
string-hunt later.

**X-3 · The fallback sweep is four sites; the audit and the
plan say three. (Shield door is the survivor if swept by the
book.)**
`"Locked"` occurrences in the clone: `MonolithViewModel.kt:157`
(state default), `MonolithViewModel.kt:2318` (trigger default),
`MainActivity.kt:133` (intent fallback),
`MonolithShieldService.kt:185` (shield extra fallback). Audit
FIX-6 and `tor-plan.md` both price the sweep at ×3. A builder
following the plan's count stops one short — and the likeliest
survivor is the shield service's, i.e. the *shield door becomes
the only door in the app that can still print `Locked`*, in the
exact slot FIX-6 claims to have unified. Ruled truth for the
builder's checklist: sweep ×4, grep `"Locked"` to zero in those
four files. (The shield notification's title `Monolith Locked`
at `MonolithShieldService.kt:65` is a different surface — not a
badge site; do not sweep it under this FIX.)

**X-4 · Same package, two faces: YouTube. (Sniper vs app tier;
ruled, but the sharpest felt-difference in the set.)**
Factory preset: YouTube is `PEEK_2M`, Instagram is `LOCKED`
(`AppCatalogReconcile.kt:54–55`). So one package lawfully
produces two different doors: open YouTube's home feed →
app-tier door, H1 `YouTube`, 110dp peek zone (offer or held,
cooldown printed); land on Shorts → sniper door, H1
`YouTube Shorts`, zone ABSENT, cooldown silent, badge
`SHORTS BLOCKED`. Every individual print is true and ruled
(sniper names keep; Baseline 4 silence). Together they are the
one place a man can watch the same app change its face, its
name, and its memory within one craving. I do not propose
breaking the rulings; I flag that this pair — not any single
door — is what "different app" will feel like if it is felt
anywhere, and it deserves one line in Tor's closing comment so
the ruling is on record as *seen*, not stumbled into.

**X-4a · The site door's new name has no mechanism and no
fallback. (SITE; buildability gap in the fixed spec.)**
Today the service passes `matchedDomain` *as* `appName`
(a11y :264) and resolves no browser label anywhere. The fixed
face needs the browser's installed label. The guard door got an
explicit law for the same problem (compose at the call site;
unresolvable → package verbatim, Skeleton §4). The site door
got neither: Chrome has a catalog row (label = installed label
via sync), but the other four supported browsers only have rows
if the catalog sync created them, and no PackageManager fallback
is specced. If the builder improvises per-browser, the five
browsers print by up to three different derivations. Spec it
like the guard: label resolved at the call site (row label,
else PackageManager label, else package verbatim) — one ladder
for both re-named doors.

**X-4b · Fixing the site name erases the domain from the
ledger. (SITE; downstream of the name cure.)**
The domain's new home, `nameSubLine`, lives only in
`AirlockInterceptState`. `recordIntercept` (and
`InterceptLogEntity`) has no sub-line field: a RETURN from the
site door will book browser package + browser name, and *which
site* he reached for is gone at dismissal. Pre-fix, the wrong
slot at least preserved the fact (domain sat in `appName`).
The Drawer downstream (audit Rider, Lade's crew) will read a
run of identical "Chrome" returns with the one distinguishing
fact missing. Cure is small and belongs to this issue: book the
sub-line with the outcome (append to the booked app name or
carry it in the log row) — Tor to price; without it the fixed
face is more truthful and the record is less.

### SURVIVALS — fictions the fix list leaves alive

**X-5 · The intention fallback is not in the builder's
state-gap list. (All doors equally — consistent, and wrong.)**
`triggerAirlockIntercept` sets
`sessionIntention = active?.intention ?: _intentionText.value`
(VM :2354) — the composer's draft. FIX-3e kills exactly this
(no session → blank → no line), and Skeleton §9 records the
dependency, but Skeleton §10's builder checklist does not carry
it. Reachability is narrow (all five services gate on an active
session, so `active` is present when a real door fires) — but a
path-less/stale intent through FIX-4's new `onNewIntent` route
is precisely where `active` can be absent at composition, and
that door would print a draft intention ("Finish Chapter 4
Problem Set" is the seeded default) as if declared for the
session. Add FIX-3e to §10's list verbatim; consistency is not
the issue, survival is.

**X-6 · The guard badge overclaims on the installer sub-path.
(GUARD, internal split.)**
The guard is two triggers wearing one face: Settings fires only
on Monolith + tamper-verb evidence in the window text
(a11y :318–329 — the badge `Uninstall Guard` is *earned*);
any of the four installer packages fires on foreground presence
alone (:311–316 — installing an unrelated app mid-session gets
the same name + badge). The fixed name cure helps (H1 honestly
prints the installer) but the badge still asserts an uninstall
guard over a possibly benign install. No FIX touches this.
Smallest honest cure, if Tor wants one: none on the face — the
badge names the *guard*, not his intent — but the wording book
should record that reading, because the Settings half invites
the stronger one.

**X-7 · The sixth door is missing from the five-door story.
(UNKNOWN.)**
Any intent that arrives without a parseable path lands on
`UNKNOWN`: never-owed zone (consistent), badge = whatever the
intent carried or the `Blocked` fallback — and name fallback
`Short-Form Feed` (`MainActivity.kt:131`), a fiction for any
non-feed package that ever arrives path-less. The Skeleton
keeps it ("no new invention"); FIX-4 routes *more* arrivals
through intent consumption, not fewer. It cannot be in the
matrix's five, but the closing comment should name it: the
fixed face has a sixth, worst-dressed door, and its name slot
is the only one still allowed to invent.

### FELT DIFFERENCES — ruled, consistent on paper, uneven in the hand

**X-8 · RETURN has two addresses. (Peek-owing vs never-owed
visits.)**
Within a visit RETURN never moves — the Skeleton law, acquitted.
Across visits its centre sits at 294dp from the card top on
110dp-zone doors and 168dp on ABSENT doors (Skeleton §3.4, card
top-anchored) — a 126dp difference in where the thumb lands,
split exactly along the peek seam: PEEK apps train one address,
guard/sniper/site/LOCKED train the other. This is also why the
Skeleton's uniform-110dp withheld (vs Schliff's 34dp) matters
beyond FIX-5c: a 34dp withheld would mint a *third* address.
Finisher's position for Tor's §12.1 ruling: **uniform 110dp**
— two addresses is the floor this design can reach; three is a
regression. (A bottom-anchored RETURN would erase the split
entirely; that is a Leaf-scale call, recorded here, not priced.)

**X-9 · The door's memory exists on only two of five doors.
(App tier + shield, PEEK_2M visits only.)**
The taper and its count line (`{ORDINAL} TIME THIS SESSION`,
Z3) live inside the 110dp zone. A man who hits the uninstall
guard four times in a session meets an amnesiac door every
time — same wall (none), no count — while the PEEK door
remembers him by name. The taper *gating* only the offer is
ruled and right; the count *printing* only where a zone exists
is a layout consequence nobody ruled on explicitly. It follows
from Baseline 4 (never-owed doors print no peek line) if the
count is a peek line — I judge it is not: it is a session fact,
like the session line, which prints on all five. Flagged for
Tor as a classification call, not a defect claim. Either way,
**`THIS SESSION` over `TONIGHT`** (Skeleton §5.7): the count is
session-scoped on every door it ever prints on; `TONIGHT`
would be the fixed face's one self-inflicted scope lie.

**X-10 · Arrival parity for the shield door is promised, not
mechanised. (SHIELD_APP vs the other four.)**
FIX-7a: app-tier takes the missing `GLOBAL_ACTION_BACK`;
guard/sniper/site already perform it (a11y :207/:233/:261).
The shield service is a plain `Service` — it has no
`performGlobalAction`; nothing in the clone routes its launch
through `MonolithAccessibilityService.instance`. Until the
builder names the mechanism (a11y-instance BACK before
`startActivity`, with a stated behaviour when the a11y service
is unbound), the shield door's arrival differs in the one way
FIX-7 exists to kill: the blocked app can stand visible through
launch latency. Print-identical, arrival-divergent — the face
is consistent, the *door swing* is not.

**X-11 · Minor, recorded so they are not rediscovered as
findings:** (a) Sniper name/badge are near-duplicates
(`YouTube Shorts` + `SHORTS BLOCKED`) — the only door where the
badge echoes the name instead of adding a fact; harmless, ruled
by the keep. (b) H3 line 2's domain has no truncation rule while
H1 has F-9's ellipsis — a long stored domain (`youtube.com/shorts`)
at font scale 1.3 in a 20dp line is the one print in the matrix
with no stated overflow behaviour. (c) The shield door re-fires
on a 2s poll where the a11y doors are event-driven; combined
with the nonce hole the Innovator files (P-4c), repeat shield
arrivals are where the tapered wall is most likely to be
skipped — wall grammar equal on paper, uneven by arrival count
until the nonce lands.

## 4. Acquittals (verified, not assumed)

- **A-1 · Session line.** One derivation, span-based, shared
  with the Deck's TIME cell in the same cure (FIX-2). The three
  doors that fire inside Safe Breaks (guard, sniper, site) —
  historically its worst victims — print the same true instant
  as the two that don't. No path can drift it.
- **A-2 · Offer/held/absent derivation.** `peekOfferFor` +
  withheld logic are path-parameterised pure functions, unit-
  tested door by door (`TruthCar3InterceptTest`: SNIPER, SITE,
  UNINSTALL_GUARD, UNKNOWN all false even for a PEEK entity).
  The zone a door shows is computed, never hand-set at a call
  site — the strongest consistency mechanism on this face.
- **A-3 · Withheld wording.** One vocabulary, ceiling minutes,
  from-frame-one after FIX-6's SECOND 0 — identical on both
  honouring doors; correctly silent (not apologetic, not
  euphemised) on the three that never owed.
- **A-4 · Chrome's `Open` vs `SITE BLOCKED`.** Looks like a
  contradiction (Shield matrix prints Chrome Open; the door
  says Blocked) and is not one *after* the name cure: H1 Chrome
  + sub-line domain + badge `SITE BLOCKED` together state the
  true law — the app is open, the site is not. This trio is in
  fact the fixed face's best argument for the name/sub-line
  split, and the pre-fix form (domain in H1) could not say it.

## 5. What Tor needs to rule before the builder's FIX-6 pass

1. Footer on peek-tier doors: `PEEK` alone (X-2) — Skeleton §12.4.
2. Withheld zone height: uniform 110dp (X-8) — Skeleton §12.1.
   Finisher supports the designer's deviation from Schliff.
3. Memory wording + classification: `THIS SESSION`, and whether
   the count is a peek line (zone-bound) or a session line
   (all doors) (X-9) — Skeleton §12.2.
4. Fallback sweep written as ×4 with the file list (X-3).
5. Site-door name ladder + ledger booking of the domain
   (X-4a/X-4b); FIX-3e added to the builder's state gaps (X-5);
   shield BACK mechanism named (X-10).

None of these block the matrix in §2 — it is the fixed face as
ruled today, with its five open seams marked where they fall.

— LETZTE, Finisher 5, Tor's cell, issue #11. Paper only;
nobody compiled; report to Tor.

## NEXT (Law 10)

(a) I want: to re-run this exact matrix against the builder's
DECLARED close the hour it lands — every cell in §2 is a
grep-able string or a call-site line, so the verification is a
one-pass sweep (names at the five call sites, `"Locked"` at
zero, `"EMERGENCY PEEK"` at zero, footer derivation, §10 gaps
present in state) and any cell that drifted from this filing
files as a named regression, not a rediscovery.
(b) I could do, unassigned: carry this cross-door matrix method
to the Disarm face's doors (Deck entry, honest-exit entry, and
the stopwatch past-floor shortcut the Innovator flags) — it is
the next face where several paths wear one face, and its
badge/name slots have never been laid side by side.
