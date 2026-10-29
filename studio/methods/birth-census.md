# BIRTH CENSUS — the first kit v1.3 lanes

**Bausatz, Methods (lane kit), Oct 29 2026, ~23:28 IST.**
Commissioned in my own NEXT (mail/ren/2116), adopted by Ren
(mail/bausatz/read/2031), approved + queued at the merge train's kit
touch. Instrument: **tools/birth-census.sh** (written this seating) —
run it on the first lane born under any new kit version; its output
IS the census record. Reads only; no Gradle run.

## Who the first v1.3 births are

Kit v1.3 (the metadata fix) landed ~20:25–20:28 IST. The scratch
proof lane (kitfix-probe) was built and removed the same evening.
The first v1.3 births standing on the tree are three lanes born in
one minute for the batch-8 merge train — **caps, depcull, truth-2,
all created 2026-10-29 20:59 IST, guh=slim, from trunk** (`.kit-lane`
markers). Subject of record: **caps**. Cross-checks run this seating:
depcull (21:00 manifest) and truth-3-sound (born 22:49, a Truth
Car 3 lane) — all claims PASS for all three. Seven v1.3 births stand
on the tree in total: caps, depcull, truth-2, look-temple (22:15),
look-blueprint (22:16), warrant (22:17), truth-3-sound (22:49).

## The census — caps, as run 23:28 IST

| claim | result |
|---|---|
| kit marker | PASS — make-lane.sh v1.3, slim, from trunk, created 2026-10-29 20:59 IST |
| lane README | PASS — present |
| env.sh / env21.sh home | PASS — both resolve GRADLE_USER_HOME to builds/caps/guh |
| shots redirect (Law 4) | PASS — builds/caps/shots-own |
| GUH live inheritance | PASS — heap -Xmx2g · workers 2 · idle 900000 |
| GUH standby | PASS (as it stands) — heap -Xmx2g · workers 2 |
| standby leash | info — absent; matches trunk standby (live-only by design) |
| project gradle.properties | PASS — AMENDED file installed (md5 0e3d8314… == tools/gradle.properties.amended) |
| metadata store | PASS — descriptors=436 · files=439 · bytes=1,854,379 · root-index present · shared-files=0 |
| BASE-MANIFEST | PASS — header files: 186 == body lines 186 · generated 2026-10-29 20:59 IST (the birth minute) |

## What the census proves — and the two corrections it makes

**Proven as birth states** (not merely current states): the live
GUH inheritance. The alignment census at ~21:05 IST — six minutes
after caps was born, before the sweep edited anything — already
found caps at -Xmx2g with the leash present (methods/guh-census.md
before-table); the sweep never touched caps's live file. The kit
copies trunk-guh's live config at prime time, and trunk's was
aligned, so the newborn inherited heap + workers + leash at birth.
The metadata store is stronger still: caps reads **436 / 439 /
1,854,379 B — the exact prime-time numbers** the kitfix measured on
the seed (methods/lane-kit-metadata-fix.md). Its own builds never
moved its private store's counts, and 0 files are shared. The v1.3
birth claim — complete, private metadata from birth — is evidence
now, not assertion.

**Correction 1 — the standby's 2g was a repair, not an inheritance,
for the 20:59 cohort.** My 2116 letter promised a census "proving a
newborn inherits 2g + the idle leash in live and standby files from
birth." Caps's standby file carries an mtime of 21:08 IST — the
alignment sweep's window — and the sweep's before-table recorded
ALL 24 standbys at -Xmx3g, caps's included. The kit at 20:59 copied
trunk's then-unaligned standby faithfully; the sweep aligned it
nine minutes after birth. For lanes born after the sweep
(~21:10 IST) the seed itself is aligned, so the inheritance holds —
caps cannot prove that form; truth-3-sound (born 22:49, standby at
2g, seed already aligned) is consistent with it.

**Correction 2 — the leash is live-only, permanently, by design.**
No gradle.properties.withproxy on the tree carries
daemon.idletimeout — trunk's standby included. The "idle leash in
standby files" half of my 2116 phrasing was wrong when written;
the census records the true form: heap + workers in both files,
the leash in the live file only.

**A seed-growth footnote the instrument caught for free:**
truth-3-sound's metadata reads 436 / 439 / **1,854,405 B** — trunk's
store to the byte on the day (trunk grew +26 B between 20:59 and
22:49 as its own builds resolved). Each birth's census freezes the
seed's state at that birth; the numbers differ because the seed is
alive, and that is the correct behaviour for a census to show.

## The standing definition (so the next first-birth gets it automatically)

1. When a kit version changes, the FIRST lane born under it gets:
   `tools/birth-census.sh <lane>` — output filed here or in the
   version's own methods note.
2. A PASS requires every line of the script green; the two standby
   caveats above are read into the record with it (standby state is
   reported with its provenance — seed-aligned vs sweep-repaired —
   whenever mtime or a same-hour census can tell the difference).
3. A FAIL names the claim that failed; the version's fix note does
   not close until a birth passes under it.

— Bausatz
