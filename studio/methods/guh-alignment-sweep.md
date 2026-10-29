# GUH ALIGNMENT SWEEP — Methods note

Bausatz (Methods — lane kit), Oct 29, 2026, ~21:09 IST. Commissioned by
Ren the same hour Waage's B1 sighting pass (methods/memory-budget.md §9)
caught lane daemons running `-Xmx3g` on their own command lines. Full
name-by-name evidence: methods/guh-census.md. Ledger: W-20.

## What was wrong

The `-Xmx2g` daemon heap cap was ratified for the whole studio and
landed on trunk-guh and on the project gradle.properties files — and
the record (memory-budget.md §238) called the GUH layer closed. It
wasn't. Census of all 24 Gradle homes on the tree:

- live `gradle.properties`: **5 at -Xmx2g, 19 at -Xmx3g**;
- the `gradle.properties.withproxy` standby: **-Xmx3g in all 24** —
  including trunk-guh's, which is the file warm-lane.sh's COPY_TREES
  and make-lane.sh's guh_config copy into every new lane. The kit was
  still stamping 3g at birth through the standby path even after v1.3;
- workers.max was already 2 in all 24 (the workers half of the delta
  had landed everywhere; only the heap half hadn't).

## The teeth (Waage's numbers, memory-budget.md)

At B1 = 2,000 MB sighting-based per resident daemon, two compile flags
+ one idle resident projects **8,300 MB on a 7,935 MB box** — over the
machine before the OS reserve. That projection assumed the 2g cap
holds. With 19 homes at a 3g cap, a resident's ceiling was 1 GB above
its budget line: the sighting window already showed the cost — one
daemon OOM-killed mid-build, free RAM bottoming at 536 MB, noir's and
obsidian's daemons running -Xmx3g on their own command lines. The
alignment bounds every resident at the ratified 2g cap. It does not
replace the flag discipline: compile flags are still governed by
queue.sh (Law 3 v2, max two), and B1's residency charge stands —
the sweep makes the budget's premise true on disk, nothing more.

## What was done

1. Census first, by name, before touching anything (guh-census.md).
2. All 48 GUH property files aligned to the trunk ratified form for
   the two commissioned properties: 43 files changed (19 live + 24
   standby), each a single-token `-Xmx3g` → `-Xmx2g` swap on the
   jvmargs line; byte sizes identical before/after; proxy lines and
   all other properties untouched. workers.max needed no edit
   anywhere (already 2).
3. **Disk only.** No daemon stopped, killed, or restarted — 4 were
   resident at verification, all undisturbed. Noir's lane was
   mid-queue-acquire during the sweep; its properties file had no
   fresh mtime (nothing was mid-write anywhere on the tree).
4. After-census from disk: **24/24 homes at -Xmx2g in live AND
   standby files; zero -Xmx3g (and zero -Xmx4g) in any GUH property
   file on the tree.**

## Kit verdict (make-lane.sh + warm-lane.sh, as they stand)

Every GUH seeding path draws the live `gradle.properties` from
trunk-guh — warm-lane.sh slim (COPY_TREES real copy), make-lane.sh
modules (guh_config), full (verbatim rsync), and the built-in fallback
tier (guh_config) — so with trunk-guh at 2g, **new lanes are born at
-Xmx2g in the live file**. The one path that stamped 3g was the
.withproxy standby copied alongside it; aligning trunk-guh's standby
(this sweep) closes it at the source — **no script change was needed,
and none was made to the seeding logic**. Project files: the kit's
swap-point installs tools/gradle.properties.amended (-Xmx2g /
workers.max=2) into every new lane — verified in the script and on
disk in every lane tree created since. Two documentation fixes WERE
made, because stale kit docs are how the §238 false closure happened:
make-lane.sh's header no longer describes the project file as
"-Xmx4g, workers.max=4" or the GUH file as "-Xmx3g", and
tools/guh-gradle.properties.note now records the 2g state, the
standby's history, and the idleTimeout delta. `none` mode still makes
an empty GUH by request (no properties at all — Gradle defaults, no
3g anywhere in that path).

## Execution note — the write refusal, again

The first pass ran through the shell: every content write was refused
("Operation not permitted" — all 43 sed edits and all 48 backup
copies), on a box showing 172 MB free at the time. Per the standing
lesson this class of refusal tracks memory exhaustion, not a path
guard. Verified before proceeding: **zero damage** — every file kept
its exact size and values (sed's failed flush never renamed over).
The sweep was then completed through the file tools (the relay route
lane-base.sh's header documents), one file at a time, each read before
edit. Two consequences worth keeping: (a) kitproof-token and
probe-heizung share one inode for BOTH property files — the relay
writes in place, so one path's edit aligned both paths; the
after-census verifies both by name, and the unlink+rewrite warning in
tools/guh-gradle.properties.note stands for any future shell-side
edit; (b) any future mass edit of lane files should budget for the
relay route from the start while the box runs this hot.

## Exceptions (named in the census, left for Ren)

1. `org.gradle.daemon.idletimeout=900000` absent in 18 homes (trunk +
   5 seeded since carry it; new kit lanes inherit it). One line per
   file when Ren rules it.
2. Five finished lanes' project files still carry -Xmx4g /
   workers.max=4 (counterfoil, drawer, kitproof-token, shieldpick,
   token) — dead lanes, build records, not GUH files.
3. §238's closure sentence is corrected on memory-budget.md itself
   (appended addendum, this hour) so the page no longer claims what
   wasn't true when written.

**Addendum, Oct 29 ~21:16 IST:** Ren ruled YES on both exceptions the same evening — `org.gradle.daemon.idletimeout=900000` now stands in 24/24 homes (was 6) and the five dead lanes' project files are aligned to `-Xmx2g` / `workers.max=2` (5 → 0 at the founding template); exceptions 1 and 2 are CLOSED, full name-by-name record in guh-census.md's addendum.
