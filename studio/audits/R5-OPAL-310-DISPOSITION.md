# Issue #310 — Audit R5 · Opal supplement · Comparative audit (4 findings)

WELLE-7-F disposition record, verified against origin/main @ f8462a8.
All four lines are marked WINS for Monolith in the source report
(`studio/audits/AUDIT_ROUND5.md`, Opal — Comparative audit). Each was
re-verified against current code; all four still stand. No code gap
exists on any line, so this record is the deliverable.

Seats (≥10 lens passes, weighted to a verification-shaped issue):
Verifier ×3 · Diagnostician ×2 · Brutal critic · Purpose-questioner ·
Technical execution · Implementation-quality · Builder (LEAD-COVERED —
no build warranted) · Fresh-eyes · Keeper of Record. Signed: WELLE-7-F.

## F1 — No widget / no Quick Settings tile on either app — WIN STANDS
Monolith side re-verified: `git grep "AppWidgetProvider|TileService|appwidget"`
over `app/src/main` on origin/main returns zero hits — Monolith ships
no widget provider and no QS tile, exactly the parity the report
records for Opal's APK. No gap on either side; nothing to build.

## F2 — Opal's Location rule is vapour; Monolith ships no such rule — WIN STANDS
Monolith manifest (`app/src/main/AndroidManifest.xml`) declares zero
location permissions (grep `location`: no hits) and no location-based
rule exists anywhere in the codebase — Monolith never offers a
condition it cannot enforce. The rival's vapour is not replicated.

## F3 — Sound: Monolith's synth out-engineers Opal's three .ogg files — WIN STANDS, WIDENED
`app/src/main/java/com/example/service/TactileSoundEngine.kt` is
1,056 lines on current main (the audit measured 857) — a bit-exact
procedural PCM synthesizer with per-event voices and a golden
SHA-256-pinned seal render, vs Opal's entire audio asset base of
three gem-crack .ogg files. The lead grew after the audit.

## F4 — Monolith is fully self-sufficient offline — WIN STANDS
The manifest declares no `INTERNET` and no `ACCESS_NETWORK_STATE`
permission (grep: no hits); there are no network-fetched design
surfaces anywhere in `app/src/main`. Every surface renders from
on-device state, connected or not — the property Opal's
network-fetched surfaces fail without a connection.

## Verdict
4 verified / 0 fixed-in-code / 0 struck-false / 0 blocked. All four
are confirmed wins requiring no code change.
