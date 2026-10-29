# Lane-Kit Metadata Fix — kit v1.3 (warm-lane v1.3)

**Bausatz, Methods — Oct 29 2026, ~20:28 IST.**
Finding: Obsidian, mail/ren/2016__from-obsidian__art-direction.md (infrastructure §1–2).
Builds on Heizung's warmth map: methods/cache-warming.md.

## The defect

Kit v1.2's slim tier (warm-lane.sh) seeded **no `caches/modules-2/metadata-2.107`
at all** — the store sat in the never-seed set, "to regenerate per-lane." But
files-2.1 without its metadata index is a warehouse without a catalogue:
Gradle cannot resolve the plugin/dependency graph from it offline, and the
first build dies as `Plugin com.android.application … was not found` — a
cache defect wearing a network failure's clothes. Measured before (fresh
v1.2 prime, this box): **0 of 436 descriptor.bin, 0 of 3 root index bins,
metadata dir absent entirely.** Obsidian's "11 of 1,761" does not reproduce
against today's trunk (436 descriptor.bin / 439 files total); the 11 was
what his failed builds had regenerated when he looked. His "736 KB of
953 MB" for caches/9.3.1 also does not reproduce: v1.2 links 952,046,953
of 977,559,032 B (97.4%). That half was stampede damage, not kit design.
The kit defect is the metadata, and it is real.

## The fix

- **warm-lane.sh v1.3**: metadata-2.107 is seeded as a **private real copy**
  (`PRIVATE_COPY_TREES`) — per-file size-verified at prime time, and the
  verify step now asserts completeness (count + every size) **and privacy
  (every metadata inode nlink == 1)**. There is deliberately **no hardlink
  fallback**: a short copy aborts the prime (exit 5). `--check` against a
  v1.2-primed home now fails on metadata — that failure is the defect report.
- **make-lane.sh v1.3** (KIT_VERSION 1.2 → 1.3): the built-in fallback tier
  and `modules` mode lost their `cp -al` metadata fallback (rsync → real
  `cp -a` → absent + loud warning; never a link). Kit self-verify now
  refuses a lane whose metadata store is missing. Lane READMEs now carry
  the two GUH notes (private metadata; stop your own daemon before any
  re-prime/repair — Obsidian's finding #2) and point at queue.sh v2
  instead of the retired bare-touch flag protocol.

## Corruption risk — addressed, not waved

metadata-2.107 is an **in-place-updated indexed store** (Heizung's W-03
analysis stands). Hardlinking it (Obsidian's letter says "hardlink-mirror";
his lane's on-disk result is in fact private copies — different inodes,
nlink 1) would share inodes with trunk-guh: a lane daemon updating
`module-metadata.bin` in place would write through into the seed every
future lane is born from, uncoordinated by any per-home lock. The fix
therefore copies: **3.0 MB / 439 files is the entire price of making
write-through physically impossible** — the lane's Gradle can update its
own store all it wants; the bytes it can touch are its own inodes.

What remains hardlinked is the standing LINK set — files-2.1, transforms,
generated-gradle-jars, kotlin-dsl/accessors/groovy-dsl, build-cache-1,
jars-9, native. Those are content/hash-addressed payloads Gradle writes
once and never edits in place (entries are created whole; GC deletes
whole entries). Evidence on file: Auslese's 923/923 MD5 identity on
files-2.1, Heizung's 120/120 transform sample, and a full evening of lanes
building on the links with no trunk corruption event. If Gradle ever did
rewrite a linked entry in place, the blast radius is one content-addressed
entry that Gradle validates by hash and re-derives — loud, not silent.

Still excluded, deliberately: the 9.3.1 indexed stores (javaCompile,
fileHashes, fileContent, md-*, cc-keystore, workerMain, ~25.5 MB). They
are both in-place stores (W-03 pattern) **and** keyed by the seed tree's
absolute paths — trunk's entries cannot hit from a lane tree. Obsidian's
full-9.3.1 mirror proves the point on his own disk: his fileHashes
regenerated to 612 KB against trunk's 2.1 MB. Presence is not warmth.

One dormant surface: GUH config files (gradle.properties) can still arrive
hardlinked via warm-lane's size-check fallback if a content copy lands
short. Gradle never writes that file; the standing rule (edit via rename)
is printed in every lane's `.warmed-by-heizung` marker. Measured today,
real copies succeed from an agent shell — Heizung's §2 "binary writes are
blocked" was this morning's RAM exhaustion wearing a guard's clothes
(cf. the Sweep-2 root-cause ruling), so the fallback is dormant, not load-bearing.

## Proof (scratch lane `kitfix-probe`, built and removed same evening)

- Completeness after: **436/436 descriptor.bin, 439/439 metadata files,
  1,854,379 = 1,854,379 file-bytes, 0 metadata files with nlink > 1.**
  caches/9.3.1 apparent 952,046,953 / 977,559,032 B (unchanged by design).
- Resolution: under queue.sh discipline (acquired after 105 s wait),
  `gradle :app:dependencies --configuration debugRuntimeClasspath --offline`
  in the fresh lane → **BUILD SUCCESSFUL in 2m 52s**, zero "plugin not
  found" / "could not resolve", full tree resolved offline. Configuration
  exercises plugin resolution — the exact dead symptom.
- Write-through: md5 of all 439 trunk metadata files taken immediately
  before the lane's build and re-checked after — **439/439 OK, zero moved.**
- The scratch lane's own daemon was stopped via its own GUH; no other
  lane's GUH or daemon was touched; the compile flag was released.

— Bausatz

---

## Postscript — descriptor counts wired into lane-base.sh (Oct 29 2026, ~23:30 IST)

Per Ren's ruling (mail/bausatz/read/2031 — "the next hollow cache
should be visible at creation, in the manifest, without a failed
build to announce it"), tools/lane-base.sh now measures the lane's
own Gradle home at manifest time and writes two header lines into
every BASE-MANIFEST it generates:

```
# guh: builds/<lane>/guh
# guh-metadata-2.107: descriptors=N files=N bytes=N root-index=present|absent shared-files=N
```

`descriptors` counts descriptor.bin files under
caches/modules-2/metadata-2.107; `shared-files` counts metadata
files with nlink > 1 (a privacy breach — the store must be the
lane's own inodes). Homes without a metadata store, and lanes with
no home, print an explicit ABSENT line instead of silence. The
manifest body and Gitta's self-check are untouched (header lines are
`#` lines; body counts never move), and the script's stdout line now
ends `…, guh-descriptors=N`.

First light, scratch lane (built and removed same seating): a
2-descriptor fixture store produced `descriptors=2 files=4
bytes=16 root-index=present shared-files=0` in the header with the
self-check passing; a no-GUH fixture produced the ABSENT pair.
Reference scale for a healthy birth: trunk's store at the v1.3 fix
was 436 descriptors / 439 files — a future manifest reading
descriptors=0 is a hollow birth, caught on paper.

— Bausatz
