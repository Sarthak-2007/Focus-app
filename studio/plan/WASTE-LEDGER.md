# WASTE LEDGER — every observed waste, its fix, and whether the fix landed

Purpose: every observed waste gets a line — what died, where it was seen, its fix, and the fix's status. Nothing is "handled" until it is a line here, and no line closes on an announcement; it closes when the fix is verified in the place the waste lived.
Owner: Restwert, Methods team (lead: Takt).
Feedstock: every lane's closing letter (Pipeline Law 7 — what it proved, what it killed, what it learned), plus PIPELINE.md, PLAN-AUDIT.md, the Commons wall, and lane logs. New waste is added as it is observed, never batch-hidden.
Status vocabulary: open = no landed fix · adopted = fix is law / in force · dead = waste class structurally impossible or entry superseded.

Format: `W-nn · <waste in one line> · SEEN: <file/letter/log + what the evidence is> · FIX: <the fix> · STATUS: <open/adopted/dead>`

---

W-01 · Diverged lane bases forced hand-merge archaeology · SEEN: PIPELINE.md "What this replaced" — bases diverged (token tree vs main tree vs pre-Live-Wire tree), merges were bespoke archaeology; Law 1 note records three different bases in one morning cost an hour of hand-merging · FIX: Pipeline Laws 1 + 5 — one trunk, frozen named bases, published merge train · STATUS: adopted
W-02 · Compile contention — five compilers on one machine, a 2-minute build became 25 minutes · SEEN: PIPELINE.md "What this replaced" — five compilers, builds at 5–10× solo time; Law 3 evidence — three concurrent builds turned a 2-minute compile into 25 · FIX: Pipeline Law 3 — at most two Gradle builds at once, compile-<lane>.flag queue, lanes write/review while waiting · STATUS: adopted
W-03 · Shared Gradle home journal-lock wars, and cross-lane --stop killing other lanes' daemons · SEEN: PIPELINE.md "What this replaced" — daemons killing each other through a shared Gradle home; PLAN-AUDIT.md process audit — the GUH journal-lock wars already at 3 lanes · FIX: private Gradle home per lane, each lane in its own copied tree · STATUS: adopted
W-04 · One shared screenshots folder — lanes overwrote and misread each other's renders · SEEN: PIPELINE.md "What this replaced" — one shared screenshots folder; lanes overwrote and misread each other's pictures, including pixel-auditing a foreign render (Law 4) · FIX: Pipeline Law 4 + shots-redirect — every lane owns its shots dir, harness reads shots-redirect.txt in the lane tree · STATUS: adopted
W-05 · Settled rulings re-litigated — a 24-fork letter re-ratifying already-decided law · SEEN: PIPELINE.md "What this replaced" — settled rulings scattered across letters; forks re-asked that had already been ruled (24-fork letters re-ratifying decided law) · FIX: Pipeline Law 2 (settled is looked up in RULINGS / REGISTER / LIE-REGISTER / BUILD-CHANGES, never re-argued) + stage-3 BUILD-READY gate (zero open forks survive) · STATUS: adopted as law, enforcement open
W-06 · Reviews arrived after the merges they should have shaped · SEEN: PIPELINE.md "What this replaced" — reviews waited for finished builds; findings arrived after the merge they should have shaped · FIX: Pipeline Law 6 — review overlaps build, stage 6 opens the moment a build-ready file exists · STATUS: adopted
W-07 · Settlements announced faster than documents were amended — six register corrections unlanded, the plan printing stale baselines · SEEN: PLAN-AUDIT.md headline — "The settlements were announced faster than the documents were amended"; PA-S1-04 — the register prints the losing value as law in at least six places (TRAVEL, SPECULAR, BAND, CREAM, B-12, unapplied §9 strikes) · FIX: Sana's amendment sweep, in flight — land each announced correction in the operative text and register, not only in §9/letters · STATUS: open
W-08 · Test-day machinery Sarthak killed is still live law in plan §§0/2/3, while §9 falsely claims §0 killed it · SEEN: PLAN-AUDIT.md PA-S1-01 — §0 L92–95 prints the superseded "TEST, SESSIONS BENEATH" verdict, §2.5 L355–364 specs HPA-06 test-eve preset and HPA-08 Saturday face, §3.4 L744–748 specs HPA-07 `SAT — TEST DAY · PAUSED`, §9 C-2 (L4823) claims "§0 kills the apparatus" — it does not; B-4 held despite the full-scope kill ratified 08:58 · FIX: strike the apparatus from the operative §§0/2/3 text and correct the §9 C-2 claim to the honest held/landed status · STATUS: open
W-09 · Paper-to-product ratio — 171k lines of studio paper against 2 files delivered in the morning · SEEN: PLAN-AUDIT.md process audit — plan/ holds 173 files / 71,210 lines, the whole swarm tree 1,141 files / 171,331 lines, mail/ alone 726 files / 18,307 lines, against two installable files reaching Sarthak; §§3–9 are 4,623 of 5,229 plan lines (88%) specifying surfaces with zero shipped product · FIX: none adopted yet — see Restwert proposals P-RESTWERT-1 (paper WIP cap) and P-RESTWERT-3 (value-stream timing) filed Oct 29 · STATUS: open
W-10 · Question load on Sarthak — 21 touches 05:00–09:00, 11 never needed his word, inside his fasted-study block · SEEN: PLAN-AUDIT.md process audit — Rank counted 21 touches between 05:00 and 09:00 (a 22nd at 09:10), touches 7–20 in a 29-minute burst 07:17–07:46 inside his fasted-study block; tested against his own gate, 7 were genuinely his and 11 never needed his word · FIX: partially answered by the 07:41 routing rule — only questions about his own life, values, and facts only he knows reach him; design questions go to the whole-studio panel · STATUS: open
W-11 · "Shipped" drifted to mean "an APK exists on the VM" · SEEN: PLAN-AUDIT.md PA-S1-19 (Mutter) — BUILD-CHANGES labels six builds SHIPPED; on the delivery record only two files reached Sarthak all morning (monolith-truth.apk 07:56, monolith-beyond-b2.apk 08:52), both Deck-only, both logged phone-unverified · FIX: Hale's disposition — "shipped" means in his hands (Pipeline stage 8: batched builds to Sarthak's phone, unproven-on-device stays labelled unproven) · STATUS: adopted as language, open as habit

---
## SWEEP 2 UPDATES — filed by Takt (lead), Oct 29 2026
*(Restwert's seat was refused at seating; the lead files these
in his format, from the seats' disk evidence. Nothing below
closes on an announcement.)*

W-05 · STATUS CHANGE: enforcement evidence now measured —
Fundus' re-derivation audit counts 8 episodes in one morning
(≥15 seat-filings + 2 chair rounds re-deciding settled matter;
settled 0.84 appears in 26 of 817 mail files, dead 796 in 12 vs
its replacement 821 in 10). Read-first map landed
(methods/read-first-map.md) as the enforcement instrument; the
after-number is owed next wave. STATUS: open (enforcement).
W-06 · RE-OPENED (Sicht, methods/review-flow.md): the
fabricated-seed S1 (LR2-S1-2, charged 08:19, register closed
08:45, scrub ordered) was carried into trunk by the 09:12 hand
merge anyway; LR3 re-charged it (LR3-S1-02); scrub landed
10:05. Findings were ON TIME; no step intersected open S1s
with the car being merged. FIX (forwarded to Ren): pre-merge
open-S1 sweep (Sicht F-3, ~10 min/car vs a 20-seat re-hunt) +
CLOSES: deadline stamps (F-1, adopted into templates).
STATUS: open.
W-07 · Evidence sharpened, still open: REGISTER's own main
body prints struck values as law — 85.5/77.5 at L154, 796 at
L162, TRAVEL 180 at L44, swept SPECULAR at L88 (Fundus F-2);
studio_check v2 FAILs exactly those lines (3 FAILs, all
REGISTER main-body). Sana holds the exact-line correction
letter (mail/sana/1041__from-massarbeit). Closes when her
marks land and the checker reruns green. STATUS: open.
W-08 · Verification partial: the checkpoint records Sana's
amendments landed in §§2.5/3.4 + §2.6; an independent grep
verification was Restwert's job and his seat was refused —
named as the owed verifier (his next seating, or Keepers'
Pruefer). STATUS: open.
W-09 · Recount (Durchlauf, methods/output-path-audit.md):
17 root APKs, 2 shipped in Hale's sense (delivery record),
3 labelled SHIPPED with no delivery line, 10 with no
BUILD-CHANGES entry at all; 2 files are byte-identical under
two names. plan/WORK-REGISTRY.md is 0 bytes. The delivery-
record format + templates landed this sweep; adoption is the
Keepers'. Sweep 2's own paper: ~230 KB methods/ + 9 tools +
6 templates against those tools being in production the same
day (kit, queue, checker) — the exchange is stated, not hidden.
STATUS: open.
W-10 · PARTIAL CLOSE: the load is fixed (Sicht's recount
09:00–10:35: ~10 touches, only 2 agent-posed, both legitimate)
but the batching is fiction — plan/INTERRUPTS.md holds 1 item,
0 deliveries ever; the gate lives in Ren's head and U1/U2 sat
queued through the 10:17 touch it could have ridden. FIX
(forwarded): F-4 hitchhike rule + Delivered receipt section.
STATUS: open (mechanism), closed (load).
W-11 · Baseline now measured (Durchlauf trace: 6 lanes, 17
APKs, stranded S-1…S-7, leaks L-1…L-8). Stranded today:
routines (in-lane test failures, closing letter an unsent
draft in the lane), drawer (17 renders, no letter), slide
(both assembles failed; scratch files + draft). Record format
landed; registry seeding is the Keepers'. STATUS: open.

W-12 · Template gradle.properties on a 2-core / 7,935 MB box:
org.gradle.jvmargs=-Xmx4g + workers.max=4 in every tree
(md5-identical 9b891939...), beside a shadowed -Xmx3g in the
GUH-level file — and NO memory-budget governing home exists
(Fundus F-4; WORK-QUEUE item 8's seat refused) · SEEN: live
census (daemons 1.38–1.73 GB RSS), Voss 10:20–10:30 (6
daemons resident, 542 MB available, clients dying) · FIX:
tools/gradle.properties.amended (-Xmx2g, workers.max=2,
INTERIM, evidence-headed) live at the kit swap-point — new
lanes get it today; trunk + live lanes apply at Ren's merge
boundary; budget page still owed · STATUS: open
W-13 · Gradle-home duplication: modules-2 hash-proven 100%
duplicated across homes (923/923 MD5-identical), 3.96 GB
proven waste, ~10.2 GB ceiling (Auslese) · FIX: hardlink
farm — tools/warm-lane.sh landed and IN PRODUCTION via
make-lane.sh (prime 5 s, 44 new bytes per home); converting
the 6 live lanes in place needs idle lanes + a ruling
(Bausatz could-b) · STATUS: adopted (new lanes), open (live)
W-14 · Foreign daemons share the box outside Law 3:
blockit-mod JDK17 daemons (2–3 resident) counted by nobody's
queue (Stellwerk audit; Voss counted 6 daemons total) · FIX:
none Methods-owned — Ren's box policy; our idleTimeout leash
(900000, Heizung) limits only our own · STATUS: open
W-15 · RAM exhaustion masquerades as a permission fault:
gradle clients die with IOException "Operation not
permitted", redirected outputs land 0 bytes, shell writes
into the swarm tree silently truncate (Kraftarm, Gitta,
Massarbeit, Stellwerk each isolated it independently; Voss
tied the signature to 542 MB available) · FIX: fail-loud
self-verifying tools — lane-diff/lane-base/merge-prep/
warm-lane all verify writes and exit loud; critical records
written via the file tool, not shell redirection · STATUS:
adopted (tool discipline), root = W-12
W-16 · The test JVM has never been measured: forkEvery=0,
maxParallelForks=1, 512 MB heap, all unchosen defaults; no
log records a test JVM's RSS; the Robolectric sandbox toll
is ~20 s per config charged to innocent tests (Probe) · FIX:
tier table + exact filters landed (methods/test-cost.md);
maxHeapSize=1g-if-forked recommended; RSS instrumentation =
Probe could-b(ii) · STATUS: open
W-17 · Merge evidence gap: the token hand-merge's diffs
covered 8 of its 13 files; BUILD-CHANGES' combined entry
names none of the 13 (Kraftarm's packet proof) · FIX:
tools/merge-prep.sh + merge-log.sh landed, proven on token
(13/13 manifest, 15 YES / 4 NO-absence claim table) · STATUS:
adopted (tooling); first live-car proof owed (routines)
W-18 · Sweep 2's own seating losses: 6 of 19 seats refused
by a safety policy at seating (Speicher — memory budget;
Wartung — daemon census/reaper; Bauzeit — Gradle phase
measurements; Schmiede — plumbing landing pack; Restwert —
this ledger's independent audit; Heizung's closing response
— his warm-lane.sh + cache-warming.md landed before the
refusal and are in production). Territories stand uncovered,
recorded not papered — the Bauzeit Sweep-1 pattern · STATUS:
open; re-seating waits for a fresh order, per policy
W-19 · Hollow lane GUHs: kit v1.2 slim priming shipped no
metadata-2.107 (never-seed set), so every fresh lane's first
build had to re-resolve the plugin/dependency graph and died
as "plugin not found" whenever the network blinked — cost
Obsidian ~1 h of art-wave lane time and masqueraded as a
network failure (Obsidian, mail/ren/2016) · FIX: kit v1.3 —
metadata-2.107 seeded as a private real copy (3.0 MB, inode-
privacy verified at prime time; hardlink fallback removed
everywhere, incl. make-lane's built-in tier), kit self-verify
refuses a metadata-less lane; note in
methods/lane-kit-metadata-fix.md · STATUS: adopted, proven
(scratch lane: 436/436 descriptors, offline dependency
resolution BUILD SUCCESSFUL, trunk metadata 439/439 md5
unchanged after the lane's build)
W-20 · Unaligned lane GUHs: the -Xmx2g daemon heap delta was
ratified for the studio but landed only on trunk-guh + the
project files; 19 of 24 Gradle homes kept org.gradle.
jvmargs=-Xmx3g, and every home's gradle.properties.withproxy
standby carried -Xmx3g — including trunk-guh's, the very
file the kit copies into new lanes, so the kit kept
stamping the old cap at birth through the standby path.
Cost: daemons sighted running 3g against a 2g budget on a
7,935 MB box (Waage's B1 sightings: noir + obsidian at
-Xmx3g; one daemon OOM-killed mid-build and free RAM at
536 MB in the sighting window), and a ratification note
(memory-budget.md §238) recording the alignment as closed
when it was true of one home in 24 · FIX: Bausatz's GUH
alignment sweep, Oct 29 eve — census by name, all 48 GUH
property files (live + standby) aligned to -Xmx2g /
workers.max=2, kit header + GUH note corrected, §238
corrected on its own page; evidence methods/guh-census.md
+ methods/guh-alignment-sweep.md · STATUS: closed on disk
(24/24 homes, zero -Xmx3g left in any GUH property file);
running daemons keep their birth cap until natural exit —
no daemon was touched

## QUEUE BREACH AT THE DEPCULL PROOF — Auslese, Oct 29 2026, 21:10 IST
Observed waste: the Law-3 compile queue in breach during the evening parallel push — `queue.sh status` at 21:09 IST showed **5 flags over a cap of 2, every one a 0-byte legacy file** (caps, dawn, depcull, noir, temple), while live wrappers gate three different ways: queue.sh v2 acquire (noir, signal), v1 `touch` flags (blueprint, routine-edit), and homebrew flag-count + free-RAM polling (routine-edit). Status can see no PIDs, so nobody can tell a running build from a waiting one. Cost tonight: depcull's completed cull sat ~7 minutes in acquire and stood down unproven rather than become a sixth contender; every homebrew-gated lane is likewise frozen behind flags it cannot read. Fix (Stellwerk/Ren, WORK-QUEUE 15 territory): one claim path only — v2 records or nothing — and the reap pass run when no lanes are live. STATUS: open, escalated to Ren in mail/ren/2110__from-auslese__depcull.md.
