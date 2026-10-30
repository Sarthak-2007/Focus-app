#!/bin/bash
cd ~/workspace/clone-inspect/builds/cells/issue-134-comp
mk(){ src=$1; name=$2; out="payloads/$name.json"; expect=$(( $(wc -c < "$src") * 4 / 3 + 100 )); for a in $(seq 1 8); do { printf '{"content":"'; base64 -w0 "$src"; printf '","encoding":"base64"}'; } > "$out" 2>/dev/null; sz=$(wc -c < "$out"); if [ "$sz" -gt "$expect" ]; then echo "ok $name $sz"; return; fi; sleep 6; done; echo "FAIL $name $sz/$expect"; }
mk app/src/main/java/com/example/data/MonolithDatabase.kt MonolithDatabase
mk app/src/main/java/com/example/data/WallAccrual.kt WallAccrual
mk app/src/test/java/com/example/CallExclusionTest.kt CallExclusionTest
mk app/src/test/java/com/example/CallMigrationTest.kt CallMigrationTest
mk app/src/test/java/com/example/RoutinesMigrationChainTest.kt RoutinesMigrationChainTest
mk app/src/test/java/com/example/SliceEMigrationChainTest.kt SliceEMigrationChainTest
mk app/src/test/java/com/example/ShortsSniperTruthTest.kt ShortsSniperTruthTest
mk studio/plan/ISSUE-134-WAVE3-REPORT.md REPORT
echo BATCH1-DONE
