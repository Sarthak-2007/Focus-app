# Issue #64 — resources · Wave-3 Cluster C1 (GRUND)

Verified against origin/main @ e29acdf by the 9-seat bundle (Designer, Consumer,
Student, User, UX, Enthusiastic designer, Wow-level designer, 29-years designer,
Intentionality designer, Interaction-detail) + lead application.

| Finding | Verdict | Cure |
|---|---|---|
| 64-1 strings.xml decorative, UI bypasses it (CRITICAL) | HOLDS — 21 strings, 0 stringResource() calls, 2 production R.string uses, 70 hard-coded UI literals; tab_*/ritual_* strings unreferenced | Full migration — separate commit on this branch (see below). |
| 64-2 Zero localisation (HIGH) | HOLDS | Structure landed: res/xml/locales_config.xml (en only — no faked translations) + android:localeConfig on <application>. Translation content unblocks once 64-1 lands. |
| 64-3 colors/dimens/themes dead + diverged (HIGH) | HOLDS — R.color/R.dimen refs = 0; night colors diverged from Color.kt (background #FF201D1A vs token #FF171412, secondaryContainer peach #FFFFB38F vs cap #FF4A423C, …) | dimens.xml DELETED. colors.xml + values-night/colors.xml synced verbatim from ui/theme/Color.kt (light onSurfaceVariant → #FF5F5856, nothing_red → alert #FFD63E06; night: background/surface #FF171412, sheet #FF4A423C, variant #FF100E0C, bright #FF362C27, secondaryContainer #FF4A423C, onSecondaryContainer #FFFFE4CF, onSurface #FFEDE3D8, onSurfaceVariant #FF776A60, text #FFD2C6BB/#FF9C8E84, nothing_red #FFFF8A5C). Lying legacy aliases (bauhaus_* incl. grey "ultramarine", warm_indigo_dusk, sunlit_paper) deleted both configs. Theme.MyApplication given a real window (windowBackground = token ground) in values/ + new values-night/themes.xml — the token splash, no stock DeviceDefault blink. |
| 64-4 Launcher icon off-palette hex (MEDIUM) | HOLDS — #1E40FF/#F5B800/#E6332A existed nowhere in the token system | Geometry kept (it is the identity); every fill now an @color/ token ref (ring+pointer → colorPrimary ink, plate → colorSurfaceBright, sector → colorPrimaryContainer accent, hub → nothing_red alert; background → colorBackground + colorSurfaceVariant frame). Legacy rasters REGENERATED from the same geometry + token palette at all 5 densities (PIL render, 8× supersampled), round variants circular-masked — pre-26 devices no longer ship the disowned palette. |
| 64-5 Fonts 80% of res/ bloat (MEDIUM) | HOLDS, worse than audited — fonts were 493,384 B of 545,221 B res/ = 90.5% | All 8 TTFs subset to Latin + app punctuation (U+0020–007E, U+00A0–00FF, dashes/quotes/ellipsis/bullet/№/arrows) via fontTools: **493,384 → 271,308 B (−45%)**. anonymous_pro_bold anomaly explained + normalised: it was a 624-glyph fuller cut vs regular's 216; same subset leaves both at ~209 glyphs. No weights deleted, no downloadable fonts (offline blocker), mandated families only. |

## 64-1 migration (the CRITICAL) — method
Every user-facing literal under app/src/main/java/com/example/ui/** moves to
strings.xml and is read via stringResource(R.string.…); templates parametrised
with %1$s/%1$d. tab_*/ritual_* strings get wired at their call sites or deleted —
no third state. Manifest hard-coded labels (:86 "Monolith Notification Catcher",
:126 "Monolith Uninstall Guard") become @string/. Non-display strings (view IDs,
log tags, keys) stay in code, each exception named. Gate at close: grep census of
residual `text = "` in ui/**, published in the closing comment.

— GRUND, Wave-3 C1 lead
