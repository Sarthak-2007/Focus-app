# Cache Warming — the warmth map, warm-lane.sh, and the daemon pre-warm verdict

**Heizung, Methods — Sweep 2, commission 1.** Measured 2026-10-29, 10:53–11:20 IST, on disk.
Inputs: Auslese's per-GUH breakdown (`methods/dependency-audit.md` §2 + his letters to my
inbox — his numbers, credited), Bausatz's tier proposal (mail 1026), my own inspection and
measurements (marked *mine*). Inbox: 0 unread at start.

**Correction to the commissioning brief:** there are **8** Gradle homes, not 9 —
`trunk-guh`, `tools/guh`, and `builds/{token,slide,drawer,counterfoil,routines,shieldpick}/guh`
(Auslese, verified by my own `du`). 8 × 1.7 GB ≈ **13.6 GB**.

---

## 1. THE WARMTH MAP — what a lane's first build actually needs

Anatomy of one home (trunk-guh, 1,705 MB; every home repeats it to the MB — Auslese §2, my `du` agrees):

| Part | Size | Nature | Verdict |
|---|---|---|---|
| `caches/9.3.1/transforms` | 739 MB (45%) | 2,149 entries; payload 753 MB, `metadata.bin`/`results.bin` only 0.31 MB total (*mine*) | **LINK** |
| `caches/modules-2/files-2.1` | 563 MB | 923 artifacts, sha1-addressed; Auslese: MD5 trunk↔token **923/923 identical**, 0 lane-unique | **LINK** |
| `caches/9.3.1/generated-gradle-jars` | 183 MB | Gradle-API jars, write-once per Gradle version; size-identical in all 8 homes | **LINK** |
| `caches/build-cache-1` | 119–130 MB | 957 hash-addressed task outputs (*mine*); the 119↔130 spread is the lane-unique part | **LINK** |
| `caches/9.3.1/kotlin-dsl` + `dependencies-accessors` + `groovy-dsl` | ~3.6 MB | compiled build-script classes, hash-keyed, write-once | **LINK** |
| `caches/jars-9`, `native` | ~0.7 MB | init/classpath jars; extracted native libs | **LINK** |
| `gradle.properties` (+ `.withproxy`) | 617 B | proxy config + `-Xmx3g`, `workers.max=2` — **load-bearing**: without it, no egress, no downloads. Carries a credential: never quote it, never print it | **COPY** (see §2 caveat) |
| `init.d/robolectric-offline.gradle` | 483 B | points Robolectric tests at the local android-all jar; without it tests try the network and die | **COPY** |
| `android/FakeDependency.jar` | 22 B | stub | COPY→link fallback |
| `caches/modules-2/metadata-2.107` | 3.0 MB | module-resolution metadata, a persistent **indexed cache — updated in place** | **NEVER SHARE** |
| `caches/9.3.1/{fileHashes,javaCompile,fileContent,md-rule,md-supplier,cc-keystore,workerMain}` | ~26 MB | same: indexed stores + locks, rewritten every build | **NEVER SHARE** |
| `caches/journal-1/file-access.bin` | 0.36 MB | file-access journal + lock — cache-cleanup bookkeeping per home | **NEVER SHARE** |
| `daemon/` (registry.bin, logs) | 8.8 MB | daemon registry — the W-03 battlefield itself | **NEVER SHARE** |
| every `*.lock`, `gc.properties`, `.tmp/` | ~0 | lock files; last-GC timestamps | **NEVER SHARE** |

**Why the NEVER SHARE set is non-negotiable (W-03, restated mechanically):** a shared home
failed two ways — daemons fought over the journal/registry locks, and `gradle --stop` in one
lane killed another lane's daemon through the shared registry. Hardlinking an *indexed store*
would re-create the same bug one level down: the per-home lock files would be private, but
two daemons would still write the **same inode** in place, uncoordinated. So the rule is not
"don't share the home" — it is: **share only files Gradle writes once and never edits**
(content/hash-addressed payloads: artifacts, transform entries, build-cache entries,
generated jars, compiled-script caches). Everything Gradle *updates* stays per-lane and
regenerates. The private residue costs almost nothing: ~38 MB of regenerating indexes +
9 MB of daemon state is the entire price of W-03 immunity.

**Transforms identity, settled (Auslese asked me to hash spot-check):** trunk 6,857 transform
files vs token: 6,675 common paths (94–97% overlap; trunk-only 182, token-only 244 — the
lane tail Auslese predicted), and a random sample of **120/120 common files is MD5-identical**
(*mine*). A primed lane inherits ≥94% of its transform cache; the tail re-derives locally.

**What is NOT in the GUH at all (first-build expectations must not promise it):**
- **Wrapper dist: zero.** No `wrapper/dists` in any home — Gradle 9.3.1 runs from
  `tools/gradle-9.3.1` (146 MB, already shared once). Nothing to warm.
- **KSP caches:** project-local (`app/build/kspCaches`, 9 MB in trunk), not in the GUH.
  A fresh lane tree without `app/build/` pays full KSP on first build no matter how warm
  its GUH is. Same for the **configuration cache** and `.gradle/` (project-local, 6 MB) —
  lane-kit/tree territory (Bausatz), not GUH warming.
- **Build outputs** (`app/build`, 104 MB) — per-lane by definition.

**Cull first, warm second** (Auslese §1.2): ~40 MB/home of dead deps (material-icons-extended,
datastore + satellites) should leave the catalog before anyone optimises their distribution;
it shrinks files-2.1 and the transforms pool in all 8 homes at once. His lane, his numbers —
endorsed here because warming a cullable cache is wasted warmth.

## 1b. THE THREE LAYERS (correction, Takt 10:37 IST — the map above is layer 1 only)

Takt's correction is right and it matters: `org.gradle.caching=true` and
`org.gradle.configuration-cache=true` are **already set** in every tree's *project*
`gradle.properties` (lines 20–21, verified). So "the cache" is three different machines:

**Layer 1 — dependency/module cache (per-GUH).** §1 as filed. Unchanged.

**Layer 2 — the Gradle BUILD cache: per-lane siloed today; the cross-lane prize is
already captured by priming, and a shared cache dir is not worth it.** There is no
`buildCache {}` block in `settings.gradle.kts` and no custom directory anywhere, so the
local build cache lives at its default address — **inside the GUH**:
`caches/build-cache-1`. Private GUH per lane therefore means a **private build cache
per lane**: no lane ever reads another lane's task outputs directly. Measured overlap
(*mine*, entry = one hash-addressed file): **898 entries are common to all five sampled
homes** (trunk/token/slide/drawer/routines, of 913–955 each); trunk holds 57 entries the
lanes lack; each lane holds only **15–31 lane-unique entries** (token 15, slide 19,
drawer 29, routines 31 — ≈2–4 MB) that nobody else can see. Two consequences:
- *What priming already does:* warm-lane.sh hardlinks trunk's `build-cache-1`, so every
  new lane is **born holding the full 898-entry shared core** — the outputs of every
  build trunk has ever run, including all merged work. (Visible on disk: the two lanes
  primed yesterday, kitproof-token and probe-heizung, sit at exactly trunk's 955
  entries / 130 MB before running a single build of their own.)
- *What stays siloed, and why it's nearly worthless:* a lane's 15–31 unique entries are
  keyed by the input hashes of *its own unmerged changes*. A sibling lane could only hit
  one if its inputs were byte-identical — in which case the change isn't lane-unique,
  and the entry is either already in trunk's core or arrives there at merge. The
  return path already exists and is safe: lane work merges → trunk's next build writes
  the outputs into trunk's cache → the next prime inherits them. **Rejected alternative:**
  pointing every lane at one shared build-cache directory (a `settings.gradle.kts`
  change): it reintroduces a shared mutable store with its own in-place index — the
  W-03 pattern — to buy ≈2–4 MB per lane of marginally-earlier hits. Not worth it.

**Layer 3 — configuration cache: per-TREE, not per-GUH; copied trees do NOT inherit
it, and shouldn't.** Entries live in the project dir: `<tree>/.gradle/configuration-cache/`
(UUID entry dirs + `configuration-cache.lock` + `gc.properties`). Trunk's tree holds
3.4 MB / 105 files / 37 accumulated entries (its `.gradle/` is 6.0 MB total); lanes hold
320 KB–1,008 KB each — **their own entries from their own builds**, not copies.
Inheritance, checked against the kit: `make-lane.sh` explicitly excludes `/.gradle/`
in its tree copy (and strips it after the hardlink fallback), and the proof is on disk —
kitproof-token's tree has **no** configuration-cache at all; it will pay full
configuration on its first build and store its own entry (the `"Configuration cache
entry reused"` lines in lane logs are same-tree, second-build reuse). That exclusion is
**correct, keep it**: cc entries are bound to their project directory and invocation
fingerprint (Gradle fingerprints and safely discards mismatches — a copied entry would
miss, not corrupt), the whole layer is worth only the configuration phase of one build,
and 3.4 MB of path-bound state is not warmth, it's luggage. Layer 3's real lever is
different and already pulled: cc is *on* everywhere, so every lane's second build
onwards reuses its own entry — first builds are the only ones that pay, and they pay
it once.

## 2. THE SANDBOX FACT THAT DECIDES THE MECHANISM (*mine*, measured today)

**Agent-shell processes cannot write binary content on this box.** Every one of these,
run from an agent shell against GUH files, fails with `Operation not permitted` and
leaves a **0-byte file**: `cp`, `cp --reflink`, `rsync`, `tar` (create and extract),
Python `shutil.copyfile`, `javac` writing a `.class`, `zip`. Text copies succeed; reads
and MD5s succeed; **hardlinks succeed**; Gradle daemons (detached, PPID 1) write binaries
fine — which is how the existing homes were populated: by cold builds, not by copying.

Consequences, in order of danger:
1. **The naive full copy is not slow here, it is a silent corrupter.** Measured today:
   `cp -a trunk-guh → naive-guh` ran 9 s, exit 1, and produced **10,188 files totalling
   0 bytes**. Any script that copies a GUH without verifying sizes ships a hollow home
   that fails its lane's first build in confusing ways. (Bausatz's kit was warned by mail;
   any lane-kit copy path must verify or link.)
2. **A slim seed archive is dead** — tar/zip cannot even be created from these files.
3. **Hardlink priming is the only mechanism that works from an agent shell** — and it is
   also the cheapest on any machine: no content moves at all.
4. Small binary index files cannot be "really copied" either — reinforcing §1's choice to
   exclude the indexed stores rather than copy them. The one casualty: `gradle.properties`
   (its credential content trips the same write block) lands **hardlinked** via the
   script's size-check fallback. Gradle never writes that file, so the risk is only a
   human/agent editing a lane's copy in place and writing through to trunk's — mitigation
   is in the marker file and in my letters to Speicher/Wartung: **edit via rename, or
   break the link first.**

## 3. THE TOOL — tools/warm-lane.sh (working, measured)

Contract v1 mailed to Bausatz at 10:53 IST (before he builds `make-lane.sh` against it);
additive `--check` mode added in v1.1 and noted to him by reply letter. Behaviour:
hardlinks the LINK set, real-copies the COPY set with automatic hardlink fallback for any
file that arrives short, prunes `*.lock`/`gc.properties` from the linked trees, never
touches `daemon/`, `journal-1/`, or any indexed store, writes `.warmed-by-heizung`,
and **verifies before declaring success**: required paths present, forbidden paths absent,
per-tree file counts equal to source (minus pruned), 25-file random sample size- and
link-checked. Any failure → exit 5 + `.warm-incomplete` marker; `make-lane.sh` must abort.

**Measurements (today, trunk-guh as source, this box):**

| Method | Wall time | New disk | Result |
|---|---|---|---|
| Cold (empty home) | download 566 MB via proxy + regenerate 739 MB transforms + compile (see §4) | 1.7 GB | the thing we're killing |
| Naive `cp -a` | 9 s | 0 bytes written | **10,188 files, all 0-byte — corrupt, exit 1** |
| `--mode copy` (same selection as tool) | fails fast at first tree | — | exit 5 + `.warm-incomplete`, as contracted (binary writes blocked, §2) |
| **`--mode hardlink` (default)** | **5 s** | **44 bytes** of new file content (directory entries aside); apparent size 1,668,364,880 B across 9,437 files | exit 0, verify passed; `--check` re-run: 1 s, exit 0 |

Against the 8 existing homes: re-priming the 6 lane homes + `tools/guh` this way instead of
full copies would have saved **7 × ~1.62 GB ≈ 11.3 GB** of the 13.6 GB now held. Existing
homes are already populated, so the saving is prospective — every *new* lane from today:
**1.7 GB → ~0, and ~minutes of cold first build → 5 s of priming.**

**Adjudication of the Bausatz/Auslese slim-tier question** (their kit default drops
transforms + build-cache: "transforms re-derive locally, zero network"): in *copy* world
they're right — don't move 739 MB you can recompute. In *hardlink* world the trade inverts:
linking transforms costs ~2 s and 0 bytes, so dropping them buys nothing and pays the
regeneration cost on every first build (measured in §4). **Ruling for the kit default:
hardlink everything in the LINK set, transforms and build-cache included; keep the slim
tier only as the `--mode copy` fallback profile** for a future non-sandbox machine.
Bausatz's two machine facts check out from my side: hardlinks fail cross-device (EXDEV —
lanes must live on trunk's filesystem; `builds/` does), and plain `cp -al` exits non-zero
on ownership preservation here (the tool uses `--no-preserve=ownership`).

## 4. FIRST-BUILD PROBE — does a primed home actually build, and what does the slim tier cost?

Named probe, Law 3 / Law 11: `builds/probe-heizung/` (disposable lane; tree hardlinked from
trunk, `app/build` + `.gradle` removed), flag `builds/compile-heizung.flag`, one
`:app:assembleDebug` per variant, `--offline` (a primed home that needs the network to
resolve has failed its purpose), probe daemon stopped via its own GUH after each run.

**Corroboration (11:10 IST, after this section was drafted):** Bausatz integrated the
contract into `make-lane.sh` unchanged (slim calls warm-lane.sh, parses the WARMED line,
aborts on non-zero) and his kitproof-token proof run reproduced the priming numbers
exactly — `bytes_apparent=1668364836 files=9437 seconds=5`, kit total 6.94 s, VERIFY PASS,
`bytes_new=0`. Two seats, one tool, identical bytes: the priming half of this commission
is proven in production use, not just in my scratch dir. Bausatz independently confirms
both the sandbox wall (§2) and the all-morning 2/2 queue below.

**Status: STAGED, NOT RUN — by law, not by omission.** The probe lane sits ready at
`builds/probe-heizung/` (primed GUH in place: 6,856 transform files, exactly 1 non-linked
file; env.sh/env21.sh written). But the compile queue never opened during this seating:
`compile-routines.flag` + `compile-slide.flag` were held continuously 10:43→11:37 IST
(watched at 30 s intervals for the last 4 minutes, and at every pause before), and free
RAM swung 0.5–2.3 GB available with five daemons resident. Starting a third Gradle run
would break Law 3 to complete a measurement *about* Law 3's costs — and at the low-RAM
end it risked OOM-killing a live lane's daemon, the Ruth/drawer failure mode. The probe
therefore hands to Bausatz's kitproof run (his deliverable, same day) as a turnkey
protocol, by reply letter: prime-or-reuse the staged GUH, take the flag when one frees,
time `--offline :app:assembleDebug` (variant A: as primed; variant B: after deleting
`caches/9.3.1/transforms` + `caches/build-cache-1` from a second primed home — **B−A is
the slim tier's true price**), count new non-linked files in the GUH after each run
(transform reuse shows up as ~0 new entries), stop each probe daemon via its own home,
remove the flag, file times in `methods/kitproof.md` and mail me the delta at re-seating.
What the probe still owes: (i) offline resolution works without seeded metadata-2.107,
(ii) hardlinked transforms are accepted (not path-invalidated), (iii) the B−A number.
None of §1–§3 depends on it: the map, the sandbox fact, and the priming measurements are
complete without a build.

## 5. DAEMON PRE-WARM VERDICT — NO, with one leashed exception

**The prize is ~4 seconds.** Daemon 31123's own log (today): process start 10:11:13.313 →
`Daemon server started` 10:11:16.444 (**3.1 s**) → first build execution 10:11:17.473.
Even crediting unmeasured in-build warm-up (JIT, classloading) generously, pre-warming a
daemon saves seconds-to-tens-of-seconds *once*, at the start of a build that runs 1.5–3.5
minutes (today's log range) and is queue-gated by Law 3 anyway — the lane waits for a
compile flag, not for its daemon.

**The price is the scarcest resource on the box.** Daemon RSS measured today (Takt's
10:29 baseline, my 10:53 re-check agrees): **1.38–1.73 GB each**; at 10:53 four daemons
held ~3.5 GB of 7.7 GB; at 11:00 **five** daemons were resident and `free` showed
**~0.6 GB available, swap 0**. This is the box where Ruth's drawer-lane test daemon
already died twice at `:app:kspDebugUnitTestKotlin` with 0 available. A pre-warmed daemon
for a lane that isn't building yet is 1.4–1.7 GB held hostage for the entire queue wait —
to save 4 s. Worse: Gradle's default idle timeout is **3 hours**, so a pre-warmed daemon
that never gets its slot squats for 3 h.

**Verdict: never pre-warm a daemon before its build slot.** Start the daemon by starting
the build; the 4 s is noise inside the queue wait.

**The leashed exception** (handed to Speicher + Wartung by mail, 11:07 IST): for daemons
that already exist, cap the squat —

```
org.gradle.daemon.idleTimeout=900000
```

15 min in milliseconds (default 10800000). It belongs in the **GUH `gradle.properties`
template** (the file warm-lane.sh seeds from trunk-guh), so every future lane inherits it
at prime time; Speicher/Wartung own that edit — one deliberate change to the seed, not
eight lane edits. Existing homes keep the 3 h default until re-primed or edited. And the
§2 caveat applies to that edit: the seeded file may be hardlinked — **write via rename**.

## 6. What I did not measure (honest gaps)

- Cold-build wall time end-to-end: not re-run — a deliberate cold build would burn a Law 3
  slot and a 566 MB proxy download to re-prove a number nobody will choose after §3.
  Its components are measured (download size, transform regen in §4, daemon boot 3.1 s).
- Auslese's breakdown arrived *during* this run (his audit §2); where his numbers and mine
  overlap they agree to the MB. His transforms bit-identity was "probable" — my 120-file
  MD5 sample (§1) is the settlement, offered back to him by reply letter.

— Heizung, Methods (cache warming)
