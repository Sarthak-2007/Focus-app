#!/usr/bin/env python3
"""Snapshot #3 — v4 runner. Lotse (finisher squad), Ren's commission
Oct 30 2026 04:45 IST: unstall #17, take the snapshot, close the issue.

Differences from the staged v3 runner (run_snapshot3.py), per the
issue-17 cell's v4 spec (ISSUE-17-WAVE2-REPORT.md B-3) and Ren's
finisher directive:
  - NO startup rmtree: pre-existing staging remnants (incl. attempt
    0137's 0-byte MAXIMPL.md, sole physical evidence of that attempt)
    are never deleted; this runner stages in a fresh timestamped dir
    and aborts if the target set dir already exists.
  - WRITE-HEALTH PROBE before any capture: small file write+read+
    hash-verify+delete in the target dir and the staging dir; any
    failure aborts loudly (exit 3) before a single payload byte moves.
  - FAIL-FAST: the first OSError in any phase prints file/path/phase
    and exits non-zero, partial state left untouched. No retry loop.
  - Payload is today's truth, not the staged fiction: repo studio/
    tree (D-1, from the wt-ci pure checkout) instead of the deleted
    design-refs/swarm; builds/cells/ sans .git + build outputs (D-2)
    — the Wave-1/2/3 cell work the staged runner missed entirely;
    design-refs flat files (D-6); trunk + kept lanes + records + APKs
    per SNAPSHOT-SPEC S1.
  - Source-completeness: per-root live census before the copy and
    staged census after; a root that is live-nonzero but stages zero
    aborts the run. Drift (live cells move during the copy) is
    recorded verbatim in SNAPSHOT.md, never smoothed over.
  - Target per Ren's directive: builds/snapshots/snapshot-3/ (single
    location; the spec S2 two-location ceremony is superseded for
    this set by the commissioning order — recorded in SNAPSHOT.md).
  - Restore verification: full copy of the sealed set into a scratch
    dir, full sha256 verify, file/byte count diff against the
    manifest; scratch kept until the PASS is stamped, removed after.
  - Exit 0 only on full PASS. Restore FAIL exits 4, stamps nothing.
Symlink convention inherited from v3: links preserved as links; a
link's manifest hash is the sha256 of its target string.
"""
import glob, hashlib, os, shutil, sys, time
from datetime import datetime, timezone, timedelta

HOME = os.path.expanduser("~")
CI = os.path.join(HOME, "workspace/clone-inspect")
PRES = os.path.join(HOME, "workspace/preservation")
TARGET_PARENT = os.path.join(CI, "builds/snapshots")
TARGET = os.path.join(TARGET_PARENT, "snapshot-3")
IST = timezone(timedelta(hours=5, minutes=30))
NOW = datetime.now(IST)
STAMP = NOW.strftime("%Y-%m-%d %H:%M IST")
RUNID = NOW.strftime("%H%M%S")
STAGE = os.path.join(PRES, ".staging-snapshot-3-v4-" + RUNID, "snapshot-3")
SCRATCH = os.path.join(PRES, "restore-test", "snapshot-3-v4-" + RUNID)
STUDIO_SRC = os.path.join(CI, "builds/wt-ci/studio")
PRUNE = ("guh", "build", ".gradle", "tmp")


def log(msg):
    print("[" + datetime.now(IST).strftime("%H:%M:%S") + " IST] " + msg, flush=True)


def mem_available_mb():
    with open("/proc/meminfo") as f:
        for line in f:
            if line.startswith("MemAvailable:"):
                return int(line.split()[1]) // 1024
    return -1


def sha256_of(path):
    if os.path.islink(path):
        return hashlib.sha256(os.readlink(path).encode("utf-8")).hexdigest()
    h = hashlib.sha256()
    with open(path, "rb") as f:
        for chunk in iter(lambda: f.read(1 << 20), b""):
            h.update(chunk)
    return h.hexdigest()


def fail_stop(phase, msg, exc=None, code=2):
    log("FAIL-STOP phase=" + phase + ": " + msg)
    if exc is not None:
        log("Exception: " + type(exc).__name__ + ": " + str(exc))
    log("MemAvailable at failure: " + str(mem_available_mb()) + " MB")
    log("Partial state left untouched. No retry.")
    sys.exit(code)


def write_probe(directory, tag):
    """Small file write+read+delete in the target dir. Abort loudly
    (exit 3) if the write wall is real in this window."""
    os.makedirs(directory, exist_ok=True)
    probe = os.path.join(directory, ".lotse-probe-" + RUNID + ".bin")
    payload = os.urandom(65536)
    want = hashlib.sha256(payload).hexdigest()
    try:
        with open(probe, "wb") as f:
            f.write(payload)
            f.flush()
            os.fsync(f.fileno())
        with open(probe, "rb") as f:
            got = hashlib.sha256(f.read()).hexdigest()
        if got != want:
            fail_stop("probe", "write probe hash mismatch in " + directory, code=3)
        os.remove(probe)
    except OSError as e:
        fail_stop("probe", "write probe failed in " + directory, e, code=3)
    log("PROBE " + tag + " PASS " + directory)


def copy_file(src, dst):
    os.makedirs(os.path.dirname(dst), exist_ok=True)
    if os.path.islink(src):
        target = os.readlink(src)
        if os.path.lexists(dst):
            os.remove(dst)
        os.symlink(target, dst)
        return
    shutil.copy2(src, dst)


def copy_tree(src_root, dst_root, prune_names=(), exclude_dirs=()):
    """Copy a tree file-by-file. exclude_dirs are absolute paths
    skipped entirely (cells: their .git). Fails fast on OSError."""
    n = 0
    for dirpath, dirnames, filenames in os.walk(src_root):
        dirnames[:] = [d for d in dirnames if d not in prune_names
                       and os.path.join(dirpath, d) not in exclude_dirs]
        rel = os.path.relpath(dirpath, src_root)
        for fn in filenames:
            s = os.path.join(dirpath, fn)
            d = os.path.join(dst_root, rel, fn) if rel != "." else os.path.join(dst_root, fn)
            copy_file(s, d)
            n += 1
            if n % 2000 == 0:
                log("  ... " + str(n) + " files (root " + os.path.basename(src_root) + ")")
    return n


def census_tree(src_root, prune_names=(), exclude_dirs=()):
    n = 0
    for dirpath, dirnames, _fns in os.walk(src_root):
        dirnames[:] = [d for d in dirnames if d not in prune_names
                       and os.path.join(dirpath, d) not in exclude_dirs]
        n += len(_fns)
    return n


def stage_payload():
    """Returns (counts, live_census) per payload root."""
    counts, live = {}, {}
    # studio/ (D-1) — the studio record, from the wt-ci pure checkout
    live["studio"] = census_tree(STUDIO_SRC)
    counts["studio"] = copy_tree(STUDIO_SRC, os.path.join(STAGE, "studio"))
    log("ROOT studio live=" + str(live["studio"]) + " staged=" + str(counts["studio"]))
    # trunk source (S1b)
    trunk_src = os.path.join(CI, "Focus-app")
    trunk_dst = os.path.join(STAGE, "trunk")
    c = copy_tree(os.path.join(trunk_src, "app/src"), os.path.join(trunk_dst, "app/src"))
    for rel in ["app/build.gradle.kts", "build.gradle.kts",
                "settings.gradle.kts", "gradle.properties",
                "metadata.json", "debug.keystore"]:
        copy_file(os.path.join(trunk_src, rel), os.path.join(trunk_dst, rel))
        c += 1
    c += copy_tree(os.path.join(trunk_src, "gradle"), os.path.join(trunk_dst, "gradle"))
    c += copy_tree(os.path.join(trunk_src, "public"), os.path.join(trunk_dst, "public"))
    counts["trunk"] = c
    live["trunk"] = (census_tree(os.path.join(trunk_src, "app/src")) + 6
                     + census_tree(os.path.join(trunk_src, "gradle"))
                     + census_tree(os.path.join(trunk_src, "public")))
    log("ROOT trunk live=" + str(live["trunk"]) + " staged=" + str(counts["trunk"]))
    # lanes (S1c): every builds/<lane> holding a Focus-app tree
    lanes_root = os.path.join(CI, "builds")
    lane_names = sorted(d for d in os.listdir(lanes_root)
                        if os.path.isdir(os.path.join(lanes_root, d, "Focus-app")))
    log("LANES " + str(lane_names))
    lc = lcl = 0
    for lane in lane_names:
        src = os.path.join(lanes_root, lane)
        lcl += census_tree(src, prune_names=PRUNE)
        lc += copy_tree(src, os.path.join(STAGE, "lanes", lane), prune_names=PRUNE)
    counts["lanes"] = lc
    live["lanes"] = lcl
    log("ROOT lanes live=" + str(lcl) + " staged=" + str(lc))
    # cells (D-2): builds/cells/** sans .git and build outputs
    cells_root = os.path.join(lanes_root, "cells")
    git_dirs = set()
    for dirpath, dirnames, _f in os.walk(cells_root):
        if ".git" in dirnames:
            git_dirs.add(os.path.join(dirpath, ".git"))
            dirnames.remove(".git")
    live["cells"] = census_tree(cells_root, prune_names=PRUNE, exclude_dirs=git_dirs)
    counts["cells"] = copy_tree(cells_root, os.path.join(STAGE, "cells"),
                                prune_names=PRUNE, exclude_dirs=git_dirs)
    log("ROOT cells live=" + str(live["cells"]) + " staged=" + str(counts["cells"]))
    # design-refs flat files (D-6)
    dr = os.path.join(CI, "design-refs")
    flat = sorted(f for f in os.listdir(dr) if os.path.isfile(os.path.join(dr, f)))
    dc = 0
    for f in flat:
        copy_file(os.path.join(dr, f), os.path.join(STAGE, "design-refs", f))
        dc += 1
    counts["design-refs"] = dc
    live["design-refs"] = len(flat)
    log("ROOT design-refs live=" + str(len(flat)) + " staged=" + str(dc))
    # root records (S1d) + attempt-0137 remnant preserved as evidence
    rec_dst = os.path.join(STAGE, "records")
    rc = 0
    for f in ["BUILD-CHANGES.md", "DEAD-CODE-AUDIT.md", "REMOVAL-LOG.md",
              "VERDICT.md", "env.sh", "env21.sh"]:
        copy_file(os.path.join(CI, f), os.path.join(rec_dst, f))
        rc += 1
    seen = set()
    for pat in ["monolith-brief-*.md", "monolith-correction-brief.md",
                "build*.log", "test-*.log", "merge-*.log", "help*.log",
                "gate-build.log", "gate-build.sh",
                "hb2-fix-verify.log", "hb2-fix-verify.sh"]:
        for p in sorted(glob.glob(os.path.join(CI, pat))):
            if p in seen:
                continue
            seen.add(p)
            copy_file(p, os.path.join(rec_dst, os.path.basename(p)))
            rc += 1
    remnant = os.path.join(PRES, ".staging-snapshot-3",
                           "2026-10-30_0137_manual-pre-truth3-merge",
                           "swarm", "MAXIMPL.md")
    if os.path.isfile(remnant):
        copy_file(remnant, os.path.join(rec_dst, "attempt-0137-remnant-MAXIMPL.md"))
        rc += 1
    counts["records"] = rc
    live["records"] = rc
    log("ROOT records staged=" + str(rc) + " (incl. attempt-0137 remnant)")
    # APKs (S1e)
    ac = 0
    for p in sorted(glob.glob(os.path.join(CI, "monolith-*.apk"))):
        copy_file(p, os.path.join(STAGE, "apks", os.path.basename(p)))
        ac += 1
    counts["apks"] = ac
    live["apks"] = ac
    log("ROOT apks staged=" + str(ac))
    return counts, live


def build_manifest(root):
    entries, total = [], 0
    for dirpath, _dn, filenames in os.walk(root):
        for fn in filenames:
            full = os.path.join(dirpath, fn)
            rel = os.path.relpath(full, root).replace(os.sep, "/")
            if rel in ("MANIFEST.sha256", "SNAPSHOT.md"):
                continue
            size = os.lstat(full).st_size
            entries.append((rel, sha256_of(full), size))
            total += size
    entries.sort(key=lambda e: e[0])
    return entries, total


def verify(root, entries):
    ok = fail = 0
    failures = []
    for rel, h, _s in entries:
        p = os.path.join(root, rel)
        if os.path.lexists(p) and sha256_of(p) == h:
            ok += 1
        else:
            fail += 1
            failures.append(rel)
    return ok, fail, failures


def count_tree(root):
    n = b = 0
    for dirpath, _dn, filenames in os.walk(root):
        for fn in filenames:
            full = os.path.join(dirpath, fn)
            rel = os.path.relpath(full, root).replace(os.sep, "/")
            if rel in ("MANIFEST.sha256", "SNAPSHOT.md"):
                continue
            n += 1
            b += os.lstat(full).st_size
    return n, b


def main():
    t0 = time.time()
    log("SNAPSHOT #3 v4 — Lotse (finisher squad), Ren's commission")
    log("MemAvailable at start: " + str(mem_available_mb()) + " MB")
    if os.path.exists(TARGET):
        fail_stop("preflight", "target already exists, refusing to overwrite: " + TARGET, code=5)
    # Write-health probes BEFORE any capture (Ren's Methods fix).
    write_probe(TARGET_PARENT, "target")
    write_probe(os.path.dirname(STAGE), "staging")
    write_probe(os.path.dirname(SCRATCH), "scratch")
    os.makedirs(STAGE)
    try:
        counts, live = stage_payload()
    except OSError as e:
        fail_stop("stage", "payload copy died", e)
    log("COUNTS " + str(counts))
    log("MemAvailable after payload copy: " + str(mem_available_mb()) + " MB")
    # Source-completeness: no live-nonzero root may stage zero.
    for root in counts:
        if live.get(root, 0) > 0 and counts[root] == 0:
            fail_stop("completeness", "root " + root + " live=" + str(live[root]) + " staged=0")
    entries, total_bytes = build_manifest(STAGE)
    manifest_text = "".join(h + "  " + rel + "\n" for rel, h, _s in entries)
    mpath = os.path.join(STAGE, "MANIFEST.sha256")
    with open(mpath, "w") as f:
        f.write(manifest_text)
        f.flush()
        os.fsync(f.fileno())
    manifest_sha = sha256_of(mpath)
    log("FILES " + str(len(entries)) + " BYTES " + str(total_bytes)
        + " MANIFEST_SHA256 " + manifest_sha)
    ok, fail, failures = verify(STAGE, entries)
    log("STAGE-VERIFY ok=" + str(ok) + " fail=" + str(fail))
    if fail:
        fail_stop("verify-stage", "staging verification failed on " + str(failures[:5]))

    drift = "; ".join(r + ": live " + str(live[r]) + " / staged " + str(counts[r])
                      for r in counts)
    snapshot_md = """# SNAPSHOT #3 — snapshot-3

- trigger: manual — finisher unstall of issue #17 (Ren's commission,
  Sarthak's finisher-squad order Oct 30 04:45 IST; two lanes had
  stood #17 down: the staged v3 runner deleted its own staging at
  startup and write-health was unproven in their windows)
- operator: Lotse (finisher/closer, flat under Ren)
- taken: STAMP_PLACEHOLDER
- runner: run_snapshot3_v4.py (v4 amendments: no startup rmtree,
  write-health probe before capture, fail-fast, D-1/D-2/D-6 payload)
- location: ~/workspace/clone-inspect/builds/snapshots/snapshot-3/
  (single location, per Ren's finisher directive; the SNAPSHOT-SPEC
  S2 two-location ceremony is superseded for this set by that order
  and the fact is recorded here, not hidden)
- source state: trunk at batch-9 state (DB v12), post-GitHub-studio
  migration (repo studio/ captured from the wt-ci pure checkout);
  builds/cells/ captured sans .git at the copy minute — Wave-2/3
  cells were LIVE and writing during the copy, so per-root drift
  below is the honest record, not an error term: DRIFT_PLACEHOLDER
- files: FILES_PLACEHOLDER | total bytes: BYTES_PLACEHOLDER
- manifest sha256: MANIFEST_PLACEHOLDER
- manifest note: MANIFEST.sha256 excludes itself and SNAPSHOT.md
  (spec S4). Symlinks preserved as links; a link's manifest hash is
  the sha256 of its target string (v3 convention). records/ carries
  attempt-0137's sole surviving remnant (0-byte MAXIMPL.md) as
  evidence, copied — the original staging was NOT deleted.
- restore-test: PENDING
"""
    snapshot_md = (snapshot_md.replace("STAMP_PLACEHOLDER", STAMP)
                   .replace("DRIFT_PLACEHOLDER", drift)
                   .replace("FILES_PLACEHOLDER", str(len(entries)))
                   .replace("BYTES_PLACEHOLDER", str(total_bytes))
                   .replace("MANIFEST_PLACEHOLDER", manifest_sha))
    spath = os.path.join(STAGE, "SNAPSHOT.md")
    with open(spath, "w") as f:
        f.write(snapshot_md)
        f.flush()
        os.fsync(f.fileno())

    # Install staging -> target, once, no overwrite.
    try:
        shutil.copytree(STAGE, TARGET, symlinks=True)
    except OSError as e:
        fail_stop("install", "staging -> target copy died", e)
    log("INSTALLED " + TARGET)
    ok, fail, failures = verify(TARGET, entries)
    m_same = sha256_of(os.path.join(TARGET, "MANIFEST.sha256")) == manifest_sha
    log("TARGET-VERIFY ok=" + str(ok) + " fail=" + str(fail)
        + " manifest_matches=" + str(m_same))
    if fail or not m_same:
        fail_stop("verify-target", "target verification failed: " + str(failures[:5]))

    # Restore verification: copy the sealed set to scratch, verify
    # every hash, diff counts/bytes against the manifest.
    try:
        shutil.copytree(TARGET, SCRATCH, symlinks=True)
    except OSError as e:
        fail_stop("restore-copy", "target -> scratch copy died", e)
    ok, fail, failures = verify(SCRATCH, entries)
    rn, rb = count_tree(SCRATCH)
    count_diff = rn - len(entries)
    byte_diff = rb - total_bytes
    log("RESTORE ok=" + str(ok) + " fail=" + str(fail) + " files=" + str(rn)
        + " bytes=" + str(rb) + " count_diff=" + str(count_diff)
        + " byte_diff=" + str(byte_diff))
    spots = []
    for tag, rel in [("studio-md", "studio/plan/SNAPSHOT-SPEC.md"),
                     ("records-md", "records/VERDICT.md")]:
        p = os.path.join(SCRATCH, rel)
        if os.path.isfile(p):
            with open(p, encoding="utf-8", errors="strict") as f:
                head = f.read(200)
            spots.append(tag + ":" + str(len(head.strip()) > 20))
        else:
            spots.append(tag + ":MISSING")
    kt = None
    for dirpath, _dn, fns in os.walk(os.path.join(SCRATCH, "trunk/app/src")):
        for fn in sorted(fns):
            if fn.endswith(".kt"):
                kt = os.path.join(dirpath, fn)
                break
        if kt:
            break
    if kt:
        with open(kt, encoding="utf-8", errors="strict") as f:
            head = f.read(200)
        spots.append("trunk-kt:" + os.path.basename(kt) + ":" + str(len(head.strip()) > 20))
    else:
        spots.append("trunk-kt:MISSING")
    log("SPOT-OPEN " + " ".join(spots))
    restore_pass = (fail == 0 and ok == len(entries) and count_diff == 0
                    and byte_diff == 0 and all(s.endswith(":True") for s in spots))
    log("RESTORE-TEST " + ("PASS" if restore_pass else "FAIL"))
    if not restore_pass:
        log("Scratch kept for evidence: " + SCRATCH)
        sys.exit(4)
    stamp = datetime.now(IST).strftime("%Y-%m-%d %H:%M IST")
    result_line = ("- restore-test: PASS " + stamp + " from the sealed set, "
                   + str(len(entries)) + " files, 0 checksum failures, "
                   + "count/byte diff 0/0, spots " + " ".join(spots) + "\n")
    tpath = os.path.join(TARGET, "SNAPSHOT.md")
    with open(tpath) as f:
        txt = f.read()
    with open(tpath, "w") as f:
        f.write(txt.replace("- restore-test: PENDING\n", result_line))
        f.flush()
        os.fsync(f.fileno())
    shutil.rmtree(SCRATCH, ignore_errors=True)
    shutil.rmtree(os.path.dirname(STAGE), ignore_errors=True)
    log("MemAvailable at end: " + str(mem_available_mb()) + " MB")
    log("SUMMARY set=snapshot-3 files=" + str(len(entries)) + " bytes=" + str(total_bytes)
        + " manifest_sha256=" + manifest_sha + " restore=PASS"
        + " elapsed_s=" + str(round(time.time() - t0, 1)))


if __name__ == "__main__":
    main()
