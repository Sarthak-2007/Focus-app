# KIT-DOC FALSE-CLOSURE SWEEP — sentences that call a layer closed

**Bausatz, Methods (lane kit), Oct 29 2026, ~23:33 IST.**
My own offer, adopted in Ren's kitfix rulings (mail/bausatz/read/2031)
and queued behind the descriptor counts: sample the kit's documents
for claims of the form "this is done / proven / closed", and re-verify
each against disk. A claim sampled and found true is recorded as
VERIFIED with its evidence; a claim found false is listed BY NAME.
The type specimen is my own: memory-budget.md §238 once recorded one
heap number as existing "anywhere in the kit" when only trunk-guh had
ever been verified (corrected on record, methods/guh-census.md).

## The sample — 11 claims

| # | claim (source) | verdict |
|---|---|---|
| 1 | "18 older homes still lack it [the leash]; closing that delta is a Ren ruling" — tools/guh-gradle.properties.note | **FALSE CLOSURE — fixed this seating** |
| 2 | metadata-2.107 sits in the EXCLUDED, regenerate-per-lane set — tools/warm-lane.sh, WHY paragraph | **STALE — fixed this seating** |
| 3 | "every kit lane gets the AMENDED file installed" — tools/make-lane.sh header | VERIFIED (7/7) |
| 4 | lane-base.sh is wired into make-lane.sh at creation — make-lane.sh §5 | VERIFIED (7/7 v1.3 lanes) |
| 5 | kitfix proof: 436/436 descriptors, 439/439 files, 1,854,379 B — methods/lane-kit-metadata-fix.md | VERIFIED (point-in-time; see note) |
| 6 | "one heap number exists anywhere in the kit" — methods/memory-budget.md §238 ratification note | FALSE when written — already corrected on record before this sweep |
| 7 | GUH census AFTER: 24/24 aligned, 24/24 leash — methods/guh-census.md | TRUE at writing; superseded as instrument by tools/guh-census.sh |
| 8 | kitproof verdict (one command, ~7 s, warm GUH, 0 new bytes) — methods/kitproof.md | VERIFIED as scoped (dated v1.1/v1.2 record) |
| 9 | "Bausatz's make-lane.sh calls it exactly that way" — tools/lane-base.sh header | VERIFIED |
| 10 | "Runs NO build" — tools/make-lane.sh header | VERIFIED |
| 11 | Signal: a warmed lane GUH cannot resolve the AGP plugin marker fresh; lanes survive on config-cache reuse — mail/bausatz/2145 | **VERIFIED — mechanism + consequence, §2 below** |

## §1 — the two fixes

**#1 — tools/guh-gradle.properties.note (FALSE CLOSURE).** The note
still taught that 18 homes lack the idle leash and that closing the
delta awaited a ruling. Ren ruled YES the same evening and the delta
closed at ~21:16 IST (24/24, methods/guh-census.md addendum). The
note has been rewritten: closed state, the dated first-run result of
tools/guh-census.sh, and the property's name spelled as the files
spell it (`org.gradle.daemon.idletimeout`; the note had carried a
camel-case `idleTimeout` that matches no file on the tree).

**#2 — tools/warm-lane.sh, WHY paragraph (STALE).** The header's
opening rationale still listed metadata-2.107 among the stores
"EXCLUDED — Gradle regenerates those in the lane's own home": the
v1.2 doctrine, contradicted by the same file's v1.3 CHANGELOG a
screen below it. A first reader learns the revoked rule first.
Comment-only fix: metadata-2.107 leaves the excluded list, with a
pointer to the CHANGELOG. No code line touched.

**Verified claims, evidence in brief:** #3 — all seven v1.3 lanes'
project gradle.properties are md5-identical to
tools/gradle.properties.amended (0e3d8314…). #4 — all seven v1.3
lanes carry BASE-MANIFEST.sha256, confidence exact, generated at the
birth minute (caps 20:59 == marker 20:59; truth-3-sound 22:49 ==
22:49). Pre-wiring v1.2 kit lanes carry none, and hand lanes carry
Gitta's reconstructed manifests only where her retro pass reached
(counterfoil, drawer, routines, shieldpick, slide) — paperwork debt
on named lanes, not a false claim; the kit claim's scope is births
after the wiring. #5 — trunk today still reads 436 descriptors /
439 files; bytes moved 1,854,379 → 1,854,405 (+26 — the store is
alive), and caps's birth copy preserves the exact recorded numbers
(methods/birth-census.md). The claim was a measurement, not a
standing state, and it stands. #8 — kitproof-token spot check:
shots-own present, shots-redirect.txt exact; the note's own header
scopes it to kit v1.1/v1.2 and it makes no v1.3 claim. #9 —
make-lane.sh §5 invokes lane-base.sh with no --base-tree, as the
header says. #10 — no Gradle invocation exists in make-lane.sh; it
reads compile-*.flag only to print the queue count.

**Not closures, flagged for a wording pass:** the "sandbox blocks
content writes after a process has READ pre-existing content"
attribution in make-lane.sh's ORDERING LAW, lane-base.sh's VM-quirk
note, and kitproof.md's sandbox-wall section. The Sweep-2 root-cause
ruling (RAM exhaustion; AGENTS.md carries it) supersedes the
mechanism; the mitigations baked on the old theory still function.
Sentences are stale, not false closures — listed here so the next
kit touch rewrites them with the true mechanism.

## §2 — Signal's GUH finding, verified

**The finding** (Ren's routing, mail/bausatz/2145): Signal's lane
runs used `gradle -g trunk-guh` because his own warmed home could not
resolve the AGP plugin marker fresh — Gradle queried only Maven
Central per his summary; Google answered 200 via curl; trunk-guh
resolves fine; lanes seem to survive on config-cache reuse.

**Evidence, all read this seating:**
- builds/signal/diag.log + diag2.log: `Resource missing. [HTTP HEAD:
  https://repo.maven.apache.org/maven2/com/android/application/
  com.android.application.gradle.plugin/9.1.1/….pom]`, then FAILURE —
  `Plugin [id: 'com.android.application', version: '9.1.1'] was not
  found` (searched: Google, MavenRepo, Gradle Central Plugin
  Repository). Three failed builds across diag*.log.
- builds/signal/guh metadata, today: **2 descriptor.bin / 5 files** —
  and the 2 are exactly the foojay-resolver pair his own builds
  regenerated (…/org.gradle.toolchains.foojay-resolver-convention/…
  and …/org.gradle.toolchains/foojay-resolver/…). The store is the
  v1.2 hollow birth plus self-regenerated crumbs: the Obsidian
  pattern, digit for digit.
- Trunk's metadata carries `descriptors/com.android.application/
  com.android.application.gradle.plugin` — the catalogue entry the
  fresh resolution needed.
- The marker ARTIFACT is present in signal's own files-2.1
  (`com.android.application/` exists there — hardlinked payload).
  The warehouse held the box; the catalogue had no card for it.
- builds/signal/Focus-app/settings.gradle.kts declares `google()`
  with the `com\.android.*` content filter, mavenCentral(),
  gradlePluginPortal() — **so this is not a repository-declaration
  gap**; the settings are the trunk settings, correct.
- signal's guh/gradle.properties vs trunk-guh's: identical but for
  line order of one systemProp — not a home-config difference either.
- builds/signal/render-run.log, line 3: "Reusing configuration
  cache." — the survival mode is on the lane's own logs.

**Verdict:** VERIFIED, and it is the metadata family in another
dress, as Ren suspected — the v1.2 hollow-birth defect (no metadata
seeded ⇒ cached artifacts unresolvable ⇒ fresh network resolution
on a box whose egress path then fails the marker fetch, whatever the
proximate network reason) wearing a network failure's clothes.
Signal's lane was born 19:02 IST under kit v1.2 (`.kit-lane`, on
disk) — six hours before the v1.3 fix existed.

**Consequence for new-lane births (filed for the kit):** a v1.3
birth carries a private copy of trunk's metadata INCLUDING the AGP
marker descriptor, so it resolves the shipped graph offline from
birth — kitfix-probe proved exactly this (full offline resolution,
BUILD SUCCESSFUL) and the birth census proves the store lands
complete (methods/birth-census.md). The residual exposure is any
module trunk's metadata does NOT describe at prime time — a new
dependency or a version bump introduced inside a lane: its first
fresh resolution walks the path that failed for Signal, and curl
reaching Google does not clear that path. Standing guidance: a lane
that must introduce a module trunk never resolved should have it
resolved in trunk first (the descriptor lands in trunk's store),
then be born or re-primed — never trust a lane's first fresh
resolution on this box as the plan of record.

— Bausatz
