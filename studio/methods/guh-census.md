# GUH CENSUS — every Gradle User Home on the clone-inspect tree

Bausatz (Methods — lane kit), Oct 29, 2026, ~21:07 IST. Commissioned by Ren
after Waage's B1 sighting pass caught lane daemons running `-Xmx3g` on their
own command lines, against the ratified `-Xmx2g` daemon heap cap
(memory-budget.md, ruled mail/takt/1150). The ratification note at
memory-budget.md §238 ("Also closed this hour: the GUH gradle.properties
jvmargs is aligned to -Xmx2g… one heap number exists anywhere in the kit")
was true of trunk-guh only when written. This census is the disk truth,
name by name.

Scope: every directory on the tree that serves as a Gradle User Home —
`trunk-guh`, `tools/guh`, and every `builds/*/guh` plus Dawn's second home
`builds/dawn/guh2` (referenced by builds/dawn/render-run.sh; the lane's
env.sh/env21.sh point at `guh`). 24 homes. Values read from each home's
`gradle.properties` (`org.gradle.jvmargs`, `org.gradle.workers.max`), plus
two hazards recorded alongside: the `gradle.properties.withproxy` standby
file each home carries, and `org.gradle.daemon.idletimeout` presence.

## BEFORE — as found, Oct 29 ~21:05 IST

Ratified reference (trunk-guh): `org.gradle.jvmargs=-Xmx2g
-Djava.net.preferIPv6Addresses=true -Dfile.encoding=UTF-8`,
`org.gradle.workers.max=2`, `org.gradle.daemon.idletimeout=900000`.

| # | GUH | jvmargs heap | workers.max | idleTimeout | notes |
|---|-----|--------------|-------------|-------------|-------|
| 1 | trunk-guh | **-Xmx2g** | 2 | present | the ratified reference |
| 2 | tools/guh | -Xmx3g | 2 | absent | non-lane home (tooling), on the tree since Oct 28 |
| 3 | builds/blueprint/guh | -Xmx3g | 2 | absent | |
| 4 | builds/caps/guh | **-Xmx2g** | 2 | present | |
| 5 | builds/cascade/guh | -Xmx3g | 2 | absent | |
| 6 | builds/chrome/guh | -Xmx3g | 2 | absent | |
| 7 | builds/counterfoil/guh | -Xmx3g | 2 | absent | |
| 8 | builds/dawn/guh | -Xmx3g | 2 | absent | |
| 9 | builds/dawn/guh2 | -Xmx3g | 2 | absent | Dawn's render home (render-run.sh) |
| 10 | builds/depcull/guh | **-Xmx2g** | 2 | present | |
| 11 | builds/drawer/guh | -Xmx3g | 2 | absent | |
| 12 | builds/kitproof-token/guh | -Xmx3g | 2 | present | **shares inode 840925 with probe-heizung** — one physical file behind two paths |
| 13 | builds/noir/guh | -Xmx3g | 2 | absent | lane LIVE at sweep time (queue acquire in flight); daemon untouched |
| 14 | builds/obsidian/guh | -Xmx3g | 2 | absent | daemon sighted at -Xmx3g by Waage (B1, pid 63534) |
| 15 | builds/probe-heizung/guh | -Xmx3g | 2 | present | **shares inode 840925 with kitproof-token** |
| 16 | builds/routine-edit/guh | -Xmx3g | 2 | absent | lane live this evening (Voss) |
| 17 | builds/routines/guh | -Xmx3g | 2 | absent | |
| 18 | builds/shieldpick/guh | -Xmx3g | 2 | absent | |
| 19 | builds/signal/guh | -Xmx3g | 2 | absent | |
| 20 | builds/slicee/guh | -Xmx3g | 2 | absent | |
| 21 | builds/slide/guh | -Xmx3g | 2 | absent | |
| 22 | builds/temple/guh | **-Xmx2g** | 2 | absent | heap aligned; idleTimeout delta remains (see below) |
| 23 | builds/token/guh | -Xmx3g | 2 | absent | |
| 24 | builds/truth-2/guh | **-Xmx2g** | 2 | present | |

**Before counts:** 24 homes · **5 at -Xmx2g · 19 at -Xmx3g** (18 of them
lane homes under builds/, counting Dawn's guh2; Waage's "17" counted named
build lanes — the render home and the shared-inode pair reconcile the
difference) · workers.max = 2 in all 24 (no workers edits needed) ·
idleTimeout present in 6 (trunk-guh, caps, depcull, kitproof-token,
probe-heizung, truth-2), absent in 18 · exactly one `org.gradle.jvmargs`
line per file (no shadowed duplicates anywhere) · no symlinks among the
property files · one shared inode (kitproof-token ↔ probe-heizung), so
every edit below is unlink+rewrite per path (sed -i rename semantics),
never an in-place write.

**The standby hazard:** every home also carries
`gradle.properties.withproxy` — and **all 24 copies carry
`org.gradle.jvmargs=-Xmx3g`**, including trunk-guh's and the five homes
whose live file was already 2g. The .withproxy is the egress variant a
lane swaps in when it needs the proxy; an unaligned standby means the 3g
cap returns the first time anyone swaps it in. warm-lane.sh's COPY_TREES
copies it into every new lane — so the kit was still stamping 3g at birth
through this one path (see the sweep note). It is aligned in this sweep
with the same one-token change.

**Adjacent finding (recorded, NOT touched — outside this sweep's scope):**
five *project* `gradle.properties` files in finished lane trees still
carry the founding template `-Xmx4g` / `workers.max=4` — counterfoil,
drawer, kitproof-token, shieldpick, token. Project properties govern over
GUH properties where both set jvmargs. All five lanes are complete and
their daemons dead; trunk and every live lane tree (all six art lanes,
routine-edit, caps, truth-2, depcull, routines, and the rest) carry the
amended `-Xmx2g` / `workers.max=2` project file, and make-lane.sh's
swap-point installs that amended file into every new lane. If Ren wants
the record trees aligned too, it is a five-line follow-up — flagged, not
assumed.

**Live-lane note:** no GUH gradle.properties on the tree had a fresh
mtime at sweep time (newest: 16:33 UTC batch) — no file was mid-write.
Noir's lane was actively acquiring the compile queue during the sweep
and three JDK21 daemons were resident; per the commission, **no daemon
was stopped, killed, or restarted**. Running daemons keep the heap cap
they were born with until they exit naturally (or hit the idle leash
where present); every daemon born after this sweep inherits 2g.

## AFTER — aligned and verified, Oct 29 ~21:09 IST

Every home re-read from disk after the edits (full listing in
methods/guh-alignment-sweep.md):

| measure | before | after |
|---------|--------|-------|
| homes at -Xmx2g (live gradle.properties) | 5 / 24 | **24 / 24** |
| homes at -Xmx3g (live) | 19 / 24 | **0** |
| .withproxy standby at -Xmx2g | 0 / 24 | **24 / 24** |
| .withproxy standby at -Xmx3g | 24 / 24 | **0** |
| workers.max = 2 (live files) | 24 / 24 | 24 / 24 (unchanged) |
| -Xmx3g anywhere in any GUH property file | 43 files | **0 files** |
| -Xmx4g anywhere in any GUH property file | 0 | 0 |

Files changed: 43 of 48 (19 live files 3g→2g; 24 standby files 3g→2g;
kitproof-token's two files were aligned through their shared inode with
probe-heizung — one physical file behind both paths, verified at both
paths by name). File sizes are byte-identical before/after (617 / 514 /
860 B) — a one-token swap, nothing else in any file was touched; the
proxy lines and every other property are exactly as found. The five
live files already at 2g (trunk-guh, caps, depcull, temple, truth-2)
were not edited.

**Exceptions — named, with reasons (the only things NOT closed):**
1. `org.gradle.daemon.idletimeout=900000` still absent in 18 homes
   (present: trunk-guh, caps, depcull, kitproof-token, probe-heizung,
   truth-2). Outside this sweep's two-property commission; kit-seeded
   homes inherit it from trunk-guh at prime time, so the delta only
   shrinks. Closing it in the 18 is a one-line-per-file follow-up
   awaiting Ren's ruling.
2. Five *project* gradle.properties in finished lane trees still carry
   -Xmx4g / workers.max=4 (counterfoil, drawer, kitproof-token,
   shieldpick, token) — recorded in the before-state above; those
   lanes are complete, their daemons dead, and their trees are build
   records. Not GUH files; flagged for Ren, not assumed.
3. Running daemons (4 resident at verification) keep the heap cap
   they were born with — by the commission's explicit terms, none
   was stopped, killed, or restarted. The alignment binds every
   daemon born from this hour on, and every resident the moment it
   next restarts.

---

## ADDENDUM — both exceptions closed, Oct 29, 2026, ~21:16 IST

Ren ruled YES on both named exceptions the same evening (Bausatz
re-seated to execute). Disk only, same discipline as the sweep: no
daemon stopped, killed or restarted — no Gradle command was run at
all in this seating; running daemons keep their birth state and
take the leash at their next birth.

### Delta 1 — `org.gradle.daemon.idletimeout=900000` into the 18 homes that lacked it

Before (re-censused this seating; matched the table above exactly):
present in 6, absent in 18. After: **present in 24/24**, exactly one
line per file; heap still `-Xmx2g` and `workers.max` still 2 in all
24 (re-verified in the same pass).

The 18, by name (each 617 B → 654 B; +37 B = the one line):

- **by shell append (3):** `tools/guh` · `builds/dawn/guh2` ·
  `builds/temple/guh` — tools/guh verified byte-prefix-identical:
  its original 617 B hash unchanged under the appended line.
- **by the file-tools relay (15):** `builds/blueprint/guh` ·
  `builds/cascade/guh` · `builds/chrome/guh` ·
  `builds/counterfoil/guh` · `builds/dawn/guh` ·
  `builds/drawer/guh` · `builds/noir/guh` · `builds/obsidian/guh` ·
  `builds/routine-edit/guh` · `builds/routines/guh` ·
  `builds/shieldpick/guh` · `builds/signal/guh` ·
  `builds/slicee/guh` · `builds/slide/guh` · `builds/token/guh`.

Route note: the first shell pass landed 3 of 18, then the
write-refusal weather returned (15 × `printf: write error:
Operation not permitted`; free RAM 463–678 MB during the seating —
the RAM class, as in the first pass). Every refused file was
verified byte-intact before the relay pass (617 B, zero
`idletimeout`, heap `-Xmx2g`, clean tail bytes) — **zero damage** —
then finished one file at a time through the relay, each edit
anchored on the file's `workers.max` line.

Scope note: the property went into the live `gradle.properties`
only. No `gradle.properties.withproxy` standby on the tree carries
it — including the ratified reference, trunk-guh — so live-only
matches the reference form exactly; the standby swap path neither
gains nor loses a leash it ever had.

Untouched (already present, 860 B each, unchanged): `trunk-guh` ·
`builds/caps/guh` · `builds/depcull/guh` ·
`builds/kitproof-token/guh` · `builds/probe-heizung/guh` ·
`builds/truth-2/guh`. The kitproof-token ↔ probe-heizung shared
inode was not in the delta set — no interplay this time.

### Delta 2 — the five dead lanes' project files aligned

`builds/<lane>/Focus-app/gradle.properties` — two tokens each and
nothing else (line 9 `org.gradle.jvmargs`: `-Xmx4g` → `-Xmx2g`;
line 23 `org.gradle.workers.max`: `4` → `2`):

- `builds/counterfoil/Focus-app/gradle.properties`
- `builds/drawer/Focus-app/gradle.properties`
- `builds/kitproof-token/Focus-app/gradle.properties`
- `builds/shieldpick/Focus-app/gradle.properties`
- `builds/token/Focus-app/gradle.properties`

Both swaps preserve byte length; each file stands at 1,546 B, the
size it was found at, and each was re-read from disk after its
edit. Nothing else in the five trees was touched — they remain
the build records of merged work; they now wake safe if ever
woken. (Execution honesty: token's two parallel edit calls
returned "not found" errors while the relay applied them — the
closest-match text in both errors was already the aligned value,
the file's mtime sits in the same second-batch as the other four,
and the disk re-read confirms 2g/2. Recorded so the account
doesn't claim a cleaner run than happened.)

Tree-wide confirmation (grep, this seating): no project
`gradle.properties` anywhere on the tree still carries `-Xmx4g`
or `workers.max=4`, and no GUH property file, live or standby,
carries `-Xmx4g` or `-Xmx3g`.

### Counts

| measure | before this seating | after |
|---------|--------------------|-------|
| homes with `daemon.idletimeout=900000` | 6 / 24 | **24 / 24** |
| project files at the founding template (`-Xmx4g` / workers 4) | 5 | **0** |
| homes at `-Xmx2g` / workers.max=2 (live) | 24 / 24 | 24 / 24 (unchanged) |

Exceptions 1 and 2 of the census above are **CLOSED**. Exception 3
stands as written — it is a statement about running daemons, not
a disk state, and closes itself as residents exit naturally (now
on the 15-minute leash, everywhere).

**CORRECTION to the tree-wide sentence above (Oct 29, ~21:24 IST,
same seating):** the sentence "no project `gradle.properties`
anywhere on the tree still carries `-Xmx4g` or `workers.max=4`"
is wrong as a string claim and is superseded here. The full
tree-wide grep returned 17 project files, and every matching line
in all 17 was then read: **all are comments, zero are live
property lines.** Fifteen lane files match at line 12 —
`#   org.gradle.jvmargs   -Xmx4g -> -Xmx2g`, the amended
template's own annotation of the swap it performs, stamped into
every lane born from tools/gradle.properties.amended (slide,
routines, cascade, chrome, slicee, dawn, signal, obsidian,
temple, routine-edit, noir, blueprint, caps, truth-2, depcull) —
and two files match at line 23, trunk `Focus-app/gradle.properties`
and `builds/probe-heizung/Focus-app/gradle.properties`:
`# (Was 4 — template setting on a 2-core / 7,935 MB box; the
-Xmx4g heap …`, a historical note on the amendment. The live
claim stands exactly as verified — every live `jvmargs` and
`workers.max` property line on the tree reads `-Xmx2g` / `2`
(trunk lines 9/26; the five dead lanes lines 9/23; the sweep's
after-census for the rest) — but the strings survive in
explanatory comments, including in the kit's own template, and
the record says so now. The comments are true and were left
untouched: they document the change, they govern nothing. The
GUH half of the original sentence is unaffected — both greps
confirm no GUH property file, live or standby, carries `-Xmx4g`
or `-Xmx3g` in any line, comment or property.

**Second scoping note (Oct 29, ~21:29 IST, same seating):** the
anchored live-line grep (`^org.gradle.jvmargs=…Xmx4g`,
`^org.gradle.workers.max=4`, whole tree) has also completed. Its
hits are not property files that configure any build: they are
Gradle's own test fixtures — `gradle.properties.bak` files under
processed test-output trees (e.g. caps's lane-to-guh3-001
caps-out test_data), daemon-sample text files inside GUH caches
and `.tmp`, a node_modules JS bundle in temple's lane, and one
in-app asset string (`aether_rack.json`). Deliberately untouched:
test data is not a build record, and neither ruling reaches it.
Net state, precisely: every `gradle.properties` that configures a
build on this tree — trunk, every lane project file, every GUH
live and standby file — reads `-Xmx2g` / `workers.max=2`, and
every GUH additionally carries the idle leash. The old strings
survive only in comments (previous note) and in test-fixture
data (this note).
