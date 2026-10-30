# Issue #307 — Audit R5 · Opal supplement · Visual design (2 findings)

WELLE-7-F disposition record, verified against origin/main @ f8462a8.
Source: `studio/audits/AUDIT_ROUND5.md`, Opal — Visual design.

Seats (≥10 lens passes, weighted design-heavy per the sharpening):
Designer ×2 · Wow-level designer · Feel specialist · Finisher ·
UX designer · Brutal critic · Purpose-questioner · Builder ·
Implementation-quality · Verifier. Signed: WELLE-7-F.

## F1 [HIGH] Block-screen gallery (curated, previewable, swappable) vs one compiled-in Airlock — STANDS-BLOCKED
Verified: the Airlock is a single compiled-in Compose surface
(`app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt`)
— no block-screen catalogue, no preview affordance, no swap
mechanism exists anywhere in `app/src/main` (grep for airlock
preview/gallery: zero hits). The fact stands.
Blocked, named + priced: a gallery is a DESIGN programme, not a
patch — N block-screen designs at the studio's gallery standard
(Sarthak's bar for design work is the highest in the studio:
"I dont want mediocre work"; a mediocre gallery counts as not
delivered), a preview surface, a selection store + Airlock theme
seam ≈ the #18 whole-design revamp's block-screen lane. Shipping
one hasty variant set inside an audit surge would violate the
quality law this wave runs under. Named blocker: belongs to the
design revamp programme (#18 lineage), where the launcher block
screen is already ruled (Sarthak, Oct 30 08:20 IST).

## F2 [MEDIUM] Produced-media design system (video explainers, Lottie, CDN video) vs zero media assets — STANDS-BLOCKED
Verified: `app/src/main` contains no `res/raw`, no assets folder,
no .mp4/.lottie/video files at all (git ls-tree grep: zero hits).
The fact stands.
Blocked, named + priced: produced media is a production pipeline,
not code — scripting + producing explainer video for the sensitive
grants, animation authorship, and (for CDN delivery) a network
dependency that would burn the verified offline win (issue #310
F4: no INTERNET permission, fully self-sufficient). His standing
rules bind production too: no AI-generated imagery in anything
built for him. Price: 1 media-production lane + a ruling on
whether any surface may ever fetch remote media. Named blocker:
production capacity + the offline-principle ruling; not buildable
inside a code surge.

## Verdict
2 findings: 0 fixed / 0 struck / 2 stands-blocked, each named and
priced above. Both facts verified true against current code; both
cures are programme-level work that a surge patch would only fake.
