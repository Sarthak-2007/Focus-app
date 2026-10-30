# Issue #311 — Audit R5 · Opal supplement · Head-to-head losses (7 findings)

WELLE-7-F disposition record, verified against origin/main @ f8462a8.
Source: `studio/audits/AUDIT_ROUND5.md`, Opal — Head-to-head losses.

Seats (≥10 lens passes): Purpose-questioner ×2 · Brutal critic ×2 ·
Designer · Technical execution · Diagnostician · Verifier ×2 ·
Implementation-quality · Builder · Consumer. Signed: WELLE-7-F.

## F1 [CRITICAL] Overlay-window blocking vs launch-self Airlock — STRUCK (VERIFIED-FALSE as a defect)
Fact confirmed: detection is the accessibility pipeline
(`MonolithAccessibilityService.kt:616` window-state events, `:1041`
startActivity of the intercept) and the block surface is the app's own
full-screen Airlock; `SYSTEM_ALERT_WINDOW` is declared
(AndroidManifest.xml:13) but no overlay window is drawn anywhere
(grep `addView|TYPE_APPLICATION_OVERLAY`: zero hits).
Struck as a defect because the full-screen launcher shape is
Sarthak's own settled ruling (Oct 30, 08:20 IST): the block screen is
"a launcher type thing which u cant exit", un-exitable BY
CONSTRUCTION — Back is consumed at the Airlock (MainActivity.kt,
issue #27 F3 handler) and exit-and-return is exactly what he
rejected. An owned overlay window is the weaker shape against that
ruling, not the stronger one. The one true residual — no presence
inside an already-open app — is F2, priced there.

## F2 [HIGH] No in-use Autofocus overlay (live today-total inside the distracting app) — STANDS-BLOCKED
Verified: zero in-app presence once a user is inside another app (no
overlay code exists). Price: a persistent overlay HUD service
(window lifecycle, per-package today-total feed from the usage
poller, draw-over-apps grant already declared) ≈ 1 focused build
lane + phone proof. Blocked on a design ruling: a live counter
floating over the distracting app is a screen-time surface inside
the moment of temptation — whether it serves the block or feeds the
reach is a panel/Ren call, not a builder's. Named blocker: design
ruling required before build.

## F3 [HIGH] managedAuthority (school/family-owned rules, compliance report, Sick Day Pass) — STRUCK (purpose clash)
Verified: the data model has exactly one authority — no managed/
authority construct exists in `app/src/main`. Struck under Sarthak's
settled purpose: Monolith is a personal BLOCKER he arms for his own
study blocks (SARTHAK-WORDS, 09:01–09:21 IST). Third-party rule
ownership and compliance reporting are a different product
(parental/MDM control); building them here would contradict the
purpose, not serve it. Never silently skipped — struck here, in the
open, with the reason.

## F4 [HIGH] One versioned catalogue entry binding app + website + behaviour flag — STANDS-BLOCKED
Verified: apps and sites are disjoint systems — `BlockedAppEntity`
vs `BlockedSiteEntity` (separate tables, MonolithDatabase entities,
DB v12), and loadout category assignment is a per-device substring
stencil (`MonolithRepository.applySmartLoadout`, category→tier map
over package substrings). Price: a unified versioned catalogue is a
schema programme — new catalogue table + migration (DB v13),
loadout/apply rewrite, seed dataset authorship — ≈ 2–3 build lanes,
and it touches the DB the look lanes are forbidden to change this
wave. Named blocker: schema programme, out of this surge's surgical
scope; queued as its own issue-grade build.

## F5 [HIGH] Opal Score (Sleep/Focus/Rest composite) — STRUCK (purpose clash)
Verified: no score exists; the only daily aggregate is
`DailyFocusStat` (totalMinutes, blocks25mCount, interceptionsCount)
from the app's own session ledger. Struck under settled purpose:
Monolith is NOT a tracker (Sarthak, verbatim — tracking lives in his
timer/syllabus tools, YPT + GMC). A three-signal focus score fed by
sleep/usage harvest is precisely the tracker shape he ruled out.
The refusal is the finding's answer, recorded with evidence.

## F6 [MEDIUM] Streak-by-configuration vs tower-pays-completion, empty history floored at 1 brick — VERIFIED-FALSE
The premise is dead on current code: `TowerStreakView` was DELETED
(issue #61 truth fixes) — see the header of
`app/src/main/java/com/example/ui/views/BlockitTowerFadersAndCatalogViews.kt:9-22`:
the 70-claim/56-drawn lie, the fake tap callback and the fabricated
default streak (19, the floor family this finding cites) all died
with the view; "a future streak surface must be built on real data
or show nothing." There is no tower, no clamp, no floor to fix.

## F7 [MEDIUM] Server-delivered collectible catalogue vs six hardcoded artifacts — STRUCK (offline win governs), residual priced
Verified: `CurioCatalog` at `MonolithModels.kt:983` defines exactly
six KineticArtifacts in a Kotlin list. The server-delivered shape is
struck by F4 of issue #310 (verified win): the manifest declares no
INTERNET permission — Monolith is fully self-sufficient offline,
and a server dataset would burn that win to win this line.
Residual, priced separately: growing the LOCAL catalogue beyond six
is content work (artifact design + lore, his no-AI-images rule
applies) ≈ 1 design/content lane; not blocked on any technology.

## Verdict
7 findings: 0 fixed-in-code / 4 struck with evidence (F1, F3, F5,
F7) / 1 verified-false (F6) / 2 stands-blocked, named + priced
(F2 design ruling, F4 schema programme).
