# Issue #79 — STURM3-13 port note (inherits STURM3-02 / WELLE cell)

STURM3-02 errored mid-work. Its branch `agent/issue-79` was audited by
STURM3-13 before anything was reused:

- **Diagnostic residue on #79: CLEAN, byte-proven.** Workflow blob on
  `agent/issue-79` tip = `07732ace`, identical to `origin/main`'s
  `.github/workflows/rescue-build.yml` blob. `bac140a`'s "restore
  pristine" claim is TRUE; the temp-diagnostic commits `b955bcf` /
  `ff857f7` leave no residue in the tree.
- **But the branch itself is stale.** It merged main at `144f000`;
  main has since gained the Wave-3 #42 host-exact matcher
  (`extractUrlHost`) and the #92 window sweep. Merging the old branch
  wholesale would regress those. So the WELLE delta was re-applied as
  a patch onto current `origin/main` (this branch), not merged.

## Verdicts vs CURRENT main (re-verified by STURM3-13, file:line)

- F1 proxy unwrap — LIVE on main (no translate.goog /
  translate?u= candidate generation anywhere on main). Ported.
- F2 Shorts spellings (/watch?v=, youtu.be) — LIVE on main (main's
  matcher strips the rule path, so the Shorts rule incidentally
  blocked all of youtube.com in the browser while the audit's
  spelling forms were handled by neither design). Ported: path rules
  are now honoured precisely; Shorts rule covers /shorts, /watch?v=
  and youtu.be, and no longer over-blocks YouTube home/channels.
- F3 embed scan — LIVE on main (no embed handling). Detectable part
  ported (window-text embed scan, focused-bar guard, parsed-host
  matching per STURM3-02's hardening `a499255`).
- F3 residue — **WELLE claim UPHELD by STURM3-13**: an embed whose
  player exposes no text/content-description nodes in the
  accessibility tree (canvas WebView, undescribed cross-origin
  iframe) yields no signal any accessibility-based check can match;
  and an embed URL cannot prove a Short, so the Shorts rule honestly
  cannot fire on embeds. Closing it needs network-level inspection
  (VPN/DNS) — a product decision for Sarthak, not attempted.
  #79 therefore closes with F3 dispositioned FIXED-IN-DETECTABLE-PART
  + residue stands-blocked (platform limit), stated on the issue.
- F4 normalizeDomain — LIVE on main (old prefix-strip impl at
  MonolithModels.kt:671). Ported: authority parsing strips userinfo,
  port, trailing dot; empty-label hosts rejected; matcher
  canonicalises stored rules at match time so legacy dead rules
  revive with no migration.

Tests: UrlBlockerBypassesTest, 18 pure tests (17 WELLE + 1 hardening),
ported unchanged. Existing TruthCar3 UNA-06 and Wave-3 #42 matcher
tests were checked line-by-line against the new semantics before
porting: all expectations hold (una06 watch?v= case returns
"youtube.com" via the Shorts rule; wildcard returns bare domain;
lookalike/query-mention cases stay null).

— STURM3-13, STURM-3 cell
