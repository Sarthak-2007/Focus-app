# Issue #333 — Flipd Visual design (3 findings) — WELLE-7 cluster J disposition

Lead: Welle (cluster J, replacement — first lead died at bootstrap; verified
0 prior comments and no branch for #333 at claim time).
Verified against origin/main @ b7a52cc. All three findings describe
Flipd's rented/thin design layer; Monolith is the opposite on each.

Seats (12, weighted design/visual): Designer · Wow-level designer ·
Enthusiastic designer · Designer with 29 years of experience ·
Intentionality designer · Feel · Finisher · UX designer · Brutal critic ·
Purpose-questioner · Verifier · Builder. Design seats LEAD-COVERED
against the theme/type source (this is a verification cell, not a
redesign); Verifier + Brutal critic run as separate passes.

- F1 Lock/home screens are server-rendered templates — VERIFIED-FALSE
  (Monolith win, with the honest trade named). Monolith ships its
  designs in the APK: every surface is a local composable
  (`ui/screens/*.kt`, `ui/components/LauncherBlockScreen.kt`) and the
  manifest declares no INTERNET permission, so the block screen works
  sealed, offline, forever — a property a blocker must have. The cost
  the audit names (copy fixes need a rebuild) is real and accepted: it
  is the price of local-first, not a defect.
- F2 Rented design system (stock DayNight, one tonal ladder, commodity
  fonts) — VERIFIED-FALSE; the audit's suggested fix ("follow the
  system DayNight by default") is STRUCK as clashing with Sarthak's
  settled design law: Monolith exists FOR its bespoke design and feel
  ("I could use Blockit but I want my own because I want design and
  feel"). Monolith hand-rolls its schemes (`ui/theme/Theme.kt:17,42`)
  and ships real bespoke type — Sora, Atkinson Hyperlegible, Anonymous
  Pro as font files (`ui/theme/Type.kt:20-46`, `res/font/`). The
  session-driven theme is deliberate, not a missed stock behaviour.
- F3 Resource tree pretending to be translated (~90 locales, zero own
  strings) — VERIFIED-FALSE (Monolith win). Monolith's `res/` contains
  a single `values/` directory and zero `values-<locale>` folders: its
  English-only state is honestly visible, with no localisation theatre.
