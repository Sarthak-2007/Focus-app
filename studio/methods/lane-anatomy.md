# Lane Anatomy — what a lane actually consists of

*Bausatz (Methods, lane kits), Oct 29 2026, Sweep 2 commission 2.
Evidence: direct inspection of all six hand-built lanes in
`~/workspace/clone-inspect/builds/{token,counterfoil,routines,drawer,slide,shieldpick}`,
trunk env files, trunk-guh, and `ScreenshotHarnessTest.kt`. All sizes `du` on disk, Oct 29 ~10:24 IST.*

## The six parts of a lane

A lane is `builds/<name>/` containing:

| # | Part | What it is |
|---|------|-----------|
| 1 | `Focus-app/` | A full copy of the source tree (its own `.git` included, HEAD `f1f24ea` in every lane checked). The lane edits/builds here; nobody else's tree moves. |
| 2 | `env.sh` + `env21.sh` | Two-line wrappers: source the trunk env, then override `GRADLE_USER_HOME` to the lane's own `guh/`. `env.sh` = JDK 17 (trunk default), `env21.sh` = JDK 21. |
| 3 | `guh/` | Private Gradle User Home — the lane's daemon, dependency cache, transforms. Private so parallel daemons never share/kill each other (Pipeline Law 3's companion). |
| 4 | `shots-own/` | The lane's private render output dir (Pipeline Law 4). |
| 5 | `Focus-app/shots-redirect.txt` | One line, absolute path of the lane's `shots-own/`. `ScreenshotHarnessTest` (trunk version) reads it from module root / repo root / `app/` and renders there instead of the shared trunk `shots/`. No file → shared default → lanes overwrite each other's renders. |
| 6 | `Focus-app/gradle.properties` | The **project** gradle.properties (inside the tree, part of the copy): daemon heap, parallelism, workers, caching, Kotlin compiler strategy for every build the lane runs. Identical in trunk + all lanes today (md5 `9b891939901812534bd1e73a6d5f007b`). **Not** the GUH `gradle.properties` (part 3's home-level file: proxy + `-Xmx3g`, workers=2) — two different files with two different jobs. *(Part added on Takt's correction, Oct 29 10:37.)* |

Plus lane-local debris that is *not* anatomy: build logs, `letter-draft.md`,
`scratch-*.kt`, `tmp/` — working files the lane's owner dropped in the root.

## Part-by-part findings

### 1. Tree — 141 MB copied, 22 MB of it source
Trunk `Focus-app` is 141 MB; excluding `build/`, `app/build/`, `.gradle/`,
`.kotlin/` it is **22 MB**. The hand lanes copied everything, build outputs
included (token's copy carries 104 MB of `app/build` alone) — ~119 MB per lane
of pure regenerable output, copied for nothing. `.git` is 8.9 MB and *earns*
its place: lanes diff their work against HEAD for the merge train.
Kit decision: copy source only, keep `.git`, exclude the four build-output dirs.

**Trap the kit fixes:** a lane tree copied verbatim inherits the *old lane's*
`shots-redirect.txt`. The kit excludes any inherited redirect and writes a
fresh one (see part 5).

### 1b. Project gradle.properties — the silent governor (added on Takt's correction)
`Focus-app/gradle.properties` rides inside the tree copy, which is why hand
assembly never got it wrong — but it is a lane constituent in its own
right, because it prices every build the lane runs:

```
org.gradle.jvmargs=-Xmx4g -Dfile.encoding=UTF-8
org.gradle.parallel=true
org.gradle.workers.max=4
org.gradle.caching=true
org.gradle.configuration-cache=true
kotlin.compiler.execution.strategy=in-process
```

Verified identical (md5 `9b891939…`) in trunk and every lane, kitproof
included. On a 2-core / ~8 GB box this is a 4 GB daemon-heap licence with
4 workers — the template's choice, never the studio's (see Takt's Sweep-2
baseline correction). Schmiede is preparing an **amended** version with
Speicher's caps; until it lands, the kit copies the tree's file as-is and
md5-verifies it in the new lane. `make-lane.sh` carries a marked
**SWAP-POINT** (`tools/gradle.properties.amended`): when the amended file
lands there, the kit installs it instead of the tree's file — unlinking
first, so a hardlinked proof tree's shared inode is never overwritten.

### 2. Env files — the one perfectly consistent part
All six lanes' `env.sh`/`env21.sh` are byte-identical except the lane name in
the `GRADLE_USER_HOME` path:

```bash
#!/bin/bash
source $HOME/workspace/clone-inspect/env.sh        # env21.sh: env21.sh
export GRADLE_USER_HOME=$HOME/workspace/clone-inspect/builds/<name>/guh
```

Trunk `env.sh` sets JDK 17, Android SDK, Gradle 9.3.1 on PATH, the proxy
truststore, and `GRADLE_USER_HOME=…/trunk-guh`; the lane file's override is
the whole lane mechanism. The kit uses this exact template.

### 3. GUH — 1.7 GB, two provenances, one irreplaceable core
Every lane GUH measures ~1.69–1.70 GB (`trunk-guh` = 1,705,351,065 B).
Breakdown (trunk-guh; Auslese's census in `methods/dependency-audit.md` §2
matches to the MB):

| Component | Size | Nature |
|---|---|---|
| `caches/9.3.1/transforms` | 739 MB | Derived from modules-2 jars; regenerates locally, no network |
| `caches/modules-2` (of which `files-2.1` 563 MB) | 566 MB | **The dependency cache. The only part whose loss costs a network download+verify of every dependency.** |
| `caches/9.3.1/generated-gradle-jars` | 183 MB | Generated from the Gradle distribution; content-stable |
| `caches/build-cache-1` | 119–130 MB | Task outputs; the spread is lane-unique |
| `caches/9.3.1/javaCompile` + kotlin-dsl + fileHashes | ~26 MB | Regenerable |
| `daemon/`, `.tmp/`, `kotlin-profile/`, `native/` etc. | ~12 MB | Runtime state — copying it is meaningless |
| `gradle.properties` (+`.withproxy`) | 4 KB | Proxy + `-Xmx3g`, workers=2 for the GUH-level daemon |

Identity (Auslese, proven): `files-2.1` = 923 files, path sets identical and
MD5-identical trunk↔token, zero lane-unique files. The duplication across
8 homes (trunk, tools, 6 lanes) is real disk: ~3.96 GB of identical modules.

**Provenance split (mtime evidence):** token, counterfoil, routines and
shieldpick GUHs keep dir mtime **2026-10-28 17:06:16 UTC** — a timestamp-
preserving copy (`cp -a`/`rsync -a`) of a warm home. Drawer (03:38 UTC) and
slide (03:37 UTC) GUHs carry seating-time mtimes — a different, later copy
route. Nobody recorded which; nothing about the contents differs materially.

**Kit strategy (Heizung/Auslese's call, Bausatz's machine):** default tier
`slim` delegates to Heizung's `tools/warm-lane.sh` (contract v1): the
write-once payload — `files-2.1`, `transforms`, `generated-gradle-jars`,
build-cache — is hardlinked from trunk-guh (content-addressed + MD5-proven
⇒ sharing is safe; Gradle deletes whole entries, never edits in place; a
lane deleting a link cannot corrupt trunk), mutable indexes
(metadata-2.107, fileHashes, javaCompile…) are excluded to regenerate
per-lane, daemon/locks never copied. Fallback tiers in the kit:
`modules`, `full`, `none`, plus a built-in slim if warm-lane.sh is absent.
Live machine facts: hardlinks work inside `~/workspace` (same filesystem,
nlink verified) but fail cross-device (EXDEV toward /tmp) and `cp -al`
needs `--no-preserve=ownership` to exit 0 here; reflink/CoW clones are
EPERM-dead on this filesystem. **Sandbox law (found by Bausatz, confirmed
independently by Heizung): agent exec sessions cannot content-copy
pre-existing files — rsync/cp/tar/git all EPERM mid-write after reading
protected content; agent-authored files, stat, renames and hardlinks are
unaffected. The kit writes all fresh files before any copy step and
falls back to a stamped READ-ONLY-PROOF hardlink tree where copies are
blocked.**

### 4+5. Shots — where hand assembly actually drifted

| Lane | `shots-own/` | `shots-redirect.txt` | Harness in its tree |
|---|---|---|---|
| token | **missing** | **missing** | old (hardcoded trunk `shots/`) |
| counterfoil | present (12 renders) | **missing** | old (hardcoded trunk `shots/`) |
| drawer | present | present → own dir | new (redirect-aware) |
| routines | present | present → own dir | new |
| shieldpick | present | present → own dir | new |
| slide | **missing** | present → `slide/shots-own` (nonexistent) | new |

Three of six lanes would misplace or share renders if their harness ran as
copied — slide's self-heals only because the harness `mkdirs()` its target.
This drift, not malice, is the case for the kit: the redirect is written by
the same command that creates the dir it points at.

## What the kit therefore does (`tools/make-lane.sh`)

`make-lane.sh <name> [--from <base>] [--guh slim|modules|full|none]` →
clean source copy (22 MB class, `.git` kept, no inherited redirect),
template env files, tiered GUH (slim/hardlinked by default), `shots-own/` +
fresh redirect, README with base HEAD + dirty count, `.kit-lane` marker.
Idempotent (kit lane re-run = no-op), refuses any pre-existing non-kit dir,
self-verifies env + redirect with **no build**, prints per-phase timing.
Compile proof remains the lane's own completion compile under Law 3/Law 11.

— Bausatz
