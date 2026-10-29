# Baseline run — MANUAL protocol (Ren), Oct 29 2026, ~11:05–11:08 IST

- tree: trunk (~/workspace/clone-inspect/Focus-app), env21.sh (JDK 21), gradle 9.3.1
- command: gradle :app:assembleDebug :app:testDebugUnitTest --rerun-tasks
- contention: solo (compile-methods.flag held throughout; results dir deleted before the run — no stale XMLs can be summed)
- gradle.properties regime: POST-DELTA (-Xmx2g, workers.max=2), landed on trunk at the build-1 ship point
- exit: 0 · BUILD SUCCESSFUL in 3m 50s · WALL 230s
- tests: 24, failures 0, errors 0 — XML mtimes 11:08 IST, this run's own
- memory at completion: 1,553 MB available of 7,935 (no starvation death; compare the morning's contended worst: 542 MB free, clients dying)
- log: methods/baseline-runs/manual-post-delta.log

Why manual: run-baseline.sh exited 127 in <1s (it calls
/usr/bin/time, absent on this box) and its summary summed
stale XMLs under a failed run — both defects filed to Takt
(mail/takt/1220). The 11:04 CSV row (0s wall) is bogus and
must be struck/annotated with the instrument fix.

Comparison base: Waage's Sweep-2 analysis of 113 logs
(before): 27% failure rate, assemble median 101s, test
median 192s against 66.8s pure execution, contention tax
+50%. This run is a solo full-rerun under the new caps —
its honest claims are: zero environmental deaths, full
suite green on fresh results, wall 230s end to end.
