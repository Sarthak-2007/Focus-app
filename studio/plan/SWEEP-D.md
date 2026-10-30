# SWEEP-D — total sweep, inboxes s–z (Sarthak's order, Oct 29, 19:52 IST)

Agent: Sweep-D, directly under Ren. Range: every mail/ inbox whose name starts s–z (83 dirs). Excluded per brief: voss (running; its root was empty anyway) and the six art-lane names (obsidian/signal/dawn/noir/temple/blueprint — no such inbox dirs exist). sana is in range and was worked: its root held **0 unread** (82 letters already sit in sana/read/ from its coordinator run; unread = root only, per brief).

## Counts

- Inboxes with unread letters: **15** · letters read in full: **26**
- Acted upon already → deleted + logged: **18**
- Acted by Sweep-D (records-level action) → deleted + logged: **1**
- Still open, left in place: **7**
- Inboxes in range with zero unread: 68 (incl. sana)

Per inbox (read / acted+deleted / acted-by-me / still-open):
saum 5/5/0/0 · schlitten 1/1/0/0 · siegel 1/1/0/0 · spule 1/1/0/0 · streit 3/2/1/0 · suki 1/1/0/0 · tief 1/1/0/0 · teo 1/1/0/0 · wren 2/2/0/0 · zug-ii 1/1/0/0 · speicher 2/0/0/2 · takt 3/1/0/2 · tomas 1/0/0/1 · waage 1/1/0/0 · wartung 2/0/0/2 · sana 0/0/0/0

Every deletion is logged name-by-name in plan/ACTED-DELETE-LOG.md (Sweep-D section) with its evidence. Headline evidence: DB 9→10 unified manifest shipped batch 7 with heldAppsAtSeal AND heldSitesAtSeal in MonolithDatabase.kt:125–126; all 19 Slice E forks ruled in plan/RULINGS.md §12; ReceiptSheet.kt:151 prints Draht's exact fault line; queue.sh v2 + trunk gradle delta (-Xmx2g/workers 2) + constants.json gate_release 0.66 enact Ren's Sweep-2 rulings.

## Closed during the sweep by Ren's parallel ruling

- **waage/1058__from-kraftarm__merge-log-minimal-format-reconcile.md** — when I read it, the reconciliation was genuinely unruled (Waage's CSV spec filed at methods/merge-log-format.md, never adopted; no merge-log.csv exists; merge-log.md holds only the build-1 line). During this sweep Ren ruled on the counterpart letter (kraftarm/1038, logged in ACTED-DELETE-LOG): Kraftarm's minimal format is RULED SUFFICIENT, Waage's CSV-wrapper spec is SUPERSEDED, the reconciliation debt dies with the ruling. On that evidence I deleted waage/1058 as acted and logged it. Note for the record: the merge-log itself still carries only the build-1 line — under the ruling that is now a sufficiency question for Kraftarm's script, not an open reconciliation.

## Records action taken by Sweep-D

- **Debate closure filed**: plan/debates/2026-10-29-slice-e-unruled.md still stated OPEN — Ren's 1205 letter ordered Streit to record it CLOSED and nobody had. I appended a signed Sweep-D closure addendum (state CLOSED, citing RULINGS §12 + batch 7 shipment). That discharged streit/1205, which was then deleted as acted.

## Still open — every item, its ask, my recommendation

1. **speicher/1052__from-probe__test-jvm-footprint.md** — Probe's per-test-JVM footprint findings + recommendation (explicit forkEvery=0, maxParallelForks=1 standing / 2 only solo, maxHeapSize "1g" if forks rise), asking Speicher to ratify against a memory-budget page. Speicher's seat was refused at seating (W-18) and Ren's 1150 ruling records "Speicher's budget page stays uncovered"; OPEN-LOOPS L25 still names Speicher as owner of a page that cannot be written by that seat. **Recommend:** Ren formally closes or reassigns the budget page (the landed -Xmx2g/2 caps do its practical work, per his own 1150 wording) and rules Probe's test-JVM settings into the lane kit or declines them — one ruling kills both this letter and L25.
2. **speicher/1107 + wartung/1107__from-heizung__daemon-idle-timeout-setting.md** (same letter, two inboxes) — add `org.gradle.daemon.idleTimeout=900000` to the GUH gradle.properties template (trunk-guh), by unlink+rewrite (the file is hardlinked and carries the proxy credential). Verified NOT applied: trunk-guh/gradle.properties has no idleTimeout line; tools/guh-gradle.properties.note already records it as "AMENDMENT OWED at next Bausatz/Heizung seating". **Recommend:** one deliberate edit closes both letters — Ren applies it himself or seats Wartung/Bausatz for it. Until then daemons keep the 3 h default on a 7.9 GB box.
3. **takt/1220__from-ren__baseline-instrument-defects.md** — Ren's own two defects in methods/run-baseline.sh: (1) calls /usr/bin/time, absent on this box; (2) summary sums stale test XMLs under a failed run's header. Verified STILL UNFIXED: run-baseline.sh:51 still calls /usr/bin/time -v; no staleness guard or INSTRUMENT FAILURE header exists in the script (the 11:04 bogus CSV row also still needs striking/annotating). **Recommend:** seat Takt/Waage to fix the script in the kit — small, priced by Ren's letter itself; the manual-post-delta baseline already filed means nothing is blocked meanwhile.
4. **takt/1905__from-sana__direction-wave-render-throughput.md** — Sana (as Designers coordinator) asks Methods to prime the direction-wave lanes' Gradle homes, watch queue.sh for stale flags, and bring a priced proposal if the two-slot law bottlenecks. Partly superseded: the coordinator layer is abolished (Sarthak, 18:59) and the wave was re-seated flat as 6 lanes (obsidian/signal/dawn/noir/temple/blueprint), not Sana's 9 dir-* lanes. The throughput duty itself is LIVE tonight while those lanes render. **Recommend:** Ren folds queue-watch + priming verification into the art-wave merge step directly (no coordinator intermediary); letter dies when the wave lands.
5. **tomas/0452__from-ren__mailroom-open.md** — Ren's mailroom-open letter; its ask is a reply from Tomas on his next seating ("one thing the direct channel would have lost"). Tomas has never been seated since. **Recommend:** leave standing — it is his first task whenever he is seated; no one can answer it for him.
6. **wartung/1028__from-stellwerk__stale-flags-dead-daemons.md** — Stellwerk's proposal (1): queue.sh, when reaping a dead-PID flag, drops builds/reaped-<lane>.note so Wartung's sweep can free the orphaned daemon's RAM. Verified NOT implemented: queue.sh reaps and prints, drops no note file. Items (2)/(3) were audit information, already copied onward by Stellwerk himself. **Recommend:** Ren rules yes/no on a half-page of shell; if yes, Stellwerk implements and this + the daemon-leash item (2) together close Wartung's whole file.
7. **Preserved from a deleted letter (teo/0814)**: Rhea's charge 2 — the hand judges the tray pile as duration whatever the census variable says — stands as her starting point if she is ever re-seated on the hub. Recorded here so the deletion loses nothing.

## Trailing items inside deleted letters (not lost — they live elsewhere)

- takt/1150's PIPELINE.md amendment text (Law 3 v2 wording, S1-WAIVED name) was not found by grep in plan/PIPELINE.md — Ren's "next filing pass" still owes it; the instruments themselves (queue.sh v2, constants.json, REGISTER) are live.
- takt/1150 ruling 6 / WASTE-LEDGER W-line 99: Bausatz GUH farm conversion is "adopted (new lanes), open (live)" — its trigger (the merge train complete, lanes idle) fired at batch 7 (17:19). Ren to schedule.
- streit/1033's Schablone debate.md template never landed (tools/templates/ holds 6 templates, no debate.md) — the mechanics file's four-field form carried the one debate run so far.

## §3 — registry lines still RECEIVED/READ but in fact ruled/shipped (for Ren's registry merge; I did not edit WORK-REGISTRY.md)

All plan/ deliverables by this range's senders in slice-e-parts, slice-de-parts and beyond-deck-parts already read RULED — no stale lines there. The stale ones are mail lines:

- `mail/ren/0454__from-ruth__drawer-digest-starved-at-proof.md | RECEIVED` — Ruth's drawer/digest work shipped as batch 3 (proof 101/101). → RULED.
- `mail/ren/1021__from-streit__debate-slice-e-unruled.md | RECEIVED` — the debate was answered, all 19 forks ruled (RULINGS §12), Slice E shipped batch 7; debate file now CLOSED (my addendum). → RULED.
- `mail/ren/read/0739__from-sana__guard-ruling-and-drawer.md | READ` — Guard dissolved by panel ruling, Drawer shipped batch 3, chrome batch 5. → RULED.
- `mail/ren/read/0842__from-sana__master-plan-assembly.md | READ` — MASTER-PLAN.md itself registers RULED. → RULED.
- `mail/ren/read/0941__from-sana__rulings-book.md | READ` — RULINGS.md registers RULED and has since carried the §12 act. → RULED.
- `mail/ren/read/0543__from-sana__type-ramp-table-build-track.md | READ` — the type ramp (104/72/50/22/15/10) is built in trunk theme. → RULED.
- `mail/ren/read/0941__from-takt__sweep-1.md | READ` — Sweep 1's asks were answered by seating Sweep 2 (OPEN-LOOPS records Takt's 1012 wants as RULED/ACTED) and ruling its consolidation (takt/1150, itself deleted this sweep as enacted). → RULED.
- `mail/ren/read/1012__from-takt__twenty.md | READ` — Methods 10→20 expansion seated and worked (Sweep 2 consolidation + rulings). → RULED.
- `mail/ren/read/0832__from-ruth__moment-walk-merged.md | READ` — merged by its own filing; Ruth's drawer line then shipped batch 3. → RULED.

— Sweep-D, Oct 29, ~20:00 IST
