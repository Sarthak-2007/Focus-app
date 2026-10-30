# Kitproof — make-lane.sh proven on the TOKEN lane

*Bausatz (Methods, lane kits), Oct 29 2026, Sweep 2 commission 2.
Kit: `tools/make-lane.sh` v1.2 (v1.1 at first proof; v1.2 adds the
gradle.properties constituent + swap-point on Takt's 10:37 correction,
re-proven below). Anatomy: `methods/lane-anatomy.md`.
GUH warming: Heizung's `tools/warm-lane.sh` (contract v1) + `methods/cache-warming.md`;
cache census: Auslese, `methods/dependency-audit.md` §2.*

## The proof run (final, delegated GUH)

```
$ make-lane.sh kitproof-token --from ~/workspace/clone-inspect/builds/token
make-lane: 'kitproof-token'  from=.../builds/token/Focus-app  guh=slim  kit=v1.1
  [env.sh + env21.sh + redirect + README + marker (fresh writes)] 0.04s
  [tree provision (clean source, mode=hardlink-readonly)] 0.83s
  WARMED target=.../builds/kitproof-token/guh mode=hardlink \
         bytes_apparent=1668364836 bytes_new=0 files=9437 seconds=5
  [guh provision (slim, hardlinked 1668364836 B)] 5.25s
VERIFY: PASS (env -> .../builds/kitproof-token/guh, redirect -> .../kitproof-token/shots-own)
make-lane: DONE 'kitproof-token' total 6.94s · base HEAD f1f24ea (dirty 0) \
  · tree 22M (mode=hardlink-readonly) · guh 1.6G (slim) · compile flags 2/2 (no build run)
EXIT=0   (wall 6.97s by `time`)
```

An earlier pre-delegation run (built-in slim tier) also passed end-to-end:
8.62s total, GUH 754 MB apparent / 778,636,176 B hardlinked, same VERIFY PASS.
The delegated tier is strictly warmer (transforms + build-cache included)
at less new disk, so it is the shipped default.

**v1.2 re-proof (after Takt's gradle.properties correction):** same command,
EXIT 0, VERIFY PASS — now including an md5 check that the new lane's
project `gradle.properties` is identical to the base tree's (md5
`9b891939901812534bd1e73a6d5f007b`, verified across trunk + all lanes) —
total **2.50s** (fresh writes 0.03s, tree 0.21s, warm-lane GUH 2.02s at
bytes_new=0). The kit logs the constituent explicitly:
`gradle.properties: base tree's file as-is (no amended file at
tools/gradle.properties.amended)`. The **swap-point** is live code: when
Schmiede's amended file (with Speicher's caps) lands at
`tools/gradle.properties.amended`, the kit installs it instead of the
tree's file, unlinking first so a hardlinked tree's shared inode is
never overwritten in place, and the verify step then md5-checks against
the amended file.

## Kit lane vs hand-built original

| Measure | `builds/token` (hand, Oct 29 ~08:16 IST) | `builds/kitproof-token` (kit) |
|---|---|---|
| Total apparent size | 1.8 GB | 1.7 GB (776 MB in the pre-delegation slim run) |
| Tree | 128 MB (incl. ~104 MB `app/build` outputs) | 22 MB (source only, `.git` kept, no build outputs) |
| GUH apparent | 1.7 GB | 1.6 GB |
| GUH **new physical disk** | ~1.7 GB (full copy) | **0 B** (warm-lane `bytes_new=0`; hardlinks shared with trunk-guh) |
| `shots-own/` | **missing** | present |
| `shots-redirect.txt` | **missing** (old harness, shared trunk shots) | present → own `shots-own/` |
| env.sh / env21.sh | template (correct) | same template, verified by sourcing: `GRADLE_USER_HOME=…/kitproof-token/guh`, env21 → JDK 21 |
| Setup wall-time | **estimate** ≤14m23s (lane dir created 02:46:45 UTC, env written 02:47:49, first build log complete 03:01:08 — window includes the build itself; setup-only share not separable from mtimes) | **measured 6.94s** end-to-end, no build |

Independent re-verification after the run (separate session): sizes as
tabulated; a modules-2 jar in the kit GUH shows nlink=3 (shared inode,
not a copy); `source env21.sh` yields the kit GUH + JDK 21; redirect file
content exact; no `app/build`/`.gradle` in the kit tree.

## Behaviour proofs

- **Idempotent:** second identical invocation → "already kit-built …
  idempotent no-op", exit 0, nothing overwritten.
- **Live-lane refusal:** `make-lane.sh token` → REFUSED, exit 2, token untouched.
- **No build run:** compile flags were 2/2 throughout (routines + slide)
  — Law 3 queue full, so no completion assemble was started; the kit's
  env/redirect self-verify is the proof, exactly as commissioned.

## The sandbox wall (affects every Methods seat)

In the agent exec sandbox, content-copies of pre-existing files are
EPERM-blocked (rsync/cp/tar/git/python all fail mid-write; 0-byte stubs
result), while agent-authored files copy fine, and stat/rename/hardlink
are unaffected. Heizung hit and documented the same wall independently.
Consequences baked into the kit: (1) all fresh-content files are written
before any copy step; (2) a blocked tree copy falls back to a hardlink
farm stamped **hardlink-readonly** — `kitproof-token`'s tree is such a
proof tree: layout/env/GUH fully verified, but it must NOT be edited in
place (shared inodes); delete it and re-run the kit in an unguarded
shell for a working lane. In an unguarded shell the same kit performs a
real rsync copy (its primary path; expected tens of seconds for ~800 MB
— unmeasured here because the sandbox blocks the measurement, stated
plainly rather than guessed).

## Verdict

The one-command lane is real: **one command, ~7 seconds, a fully warm
private GUH for 0 new bytes of disk, correct env/redirect by
construction, and the two drift defects of the hand-built token lane
(missing shots-own, missing redirect) impossible by design.** Remaining
open measurement, owned by Heizung: first-build cost delta of a
warm-lane GUH (regenerated indexes) vs a copied GUH — needs a Law 3
queue slot.

— Bausatz
