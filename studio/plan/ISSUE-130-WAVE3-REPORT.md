# Issue #130 — Wave-3 composition report (browser-as-loophole, Audit R3b)

**Original lane:** commit `c70403e` on `agent/issue-130`, base `0a00e95`, CI run
40985335284 (assemble green; BrowserLoopholeTest 6/6 + TruthCar3InterceptTest green;
remaining failures = shared floor).
**This composition:** the same five findings re-based onto current `main`
(`d1d3a78a2d4ca15a204f5ee4e3fca39797519312`, merge-base with the original lane is
exactly `0a00e95`) as one commit whose parent is that main tip. Method: true 3-way
per file (base `0a00e95` vs `c70403e` vs current main). Where main had restructured
a file, #130's semantics were re-expressed in main's structure by hand — never
mechanically merged.

## What main had gained since 0a00e95 (all preserved)

- **#42 (Wave-3):** the accessibility service is now a pipeline — synchronous
  fact capture (`captureEventFacts`/`EventFacts.browserUrlTexts`), one conflated
  worker (`processEvent`), per-call bypass, session gate, host-exact
  `matchBlockedDomain` (substring matching killed), URL bars found by
  resource-ID suffix in `collectUrlBarTexts`. `isSupportedBrowser` on main
  already covered DuckDuckGo, Vivaldi, Opera, Opera Beta.
- **#131 (merged, PR #187):** the sniper gate no longer consults the peek lease;
  its comment in `processEvent` explicitly kept the site door on the blanket
  lease ("the amendment is sniper-only"), and `ShortsReelsLivedTest` pins that
  with source tripwires.
- **#132 + #107 (merged, incl. the REISE composition):** classification lives in
  ONE classifier, `AppCategorizer` (AppCatalogReconcile.kt) — MUSIC first by
  token, bare-"play" GAMES trap killed; `MonolithRepository.categorizePackage`
  is now a one-line delegate. `LoadoutRules`/`setAppCategory` untouched.
- **ViewModel:** the withheld-reason computation was extracted into the pure
  `peekWithheldFor(...)`; `start2MinutePeek` gained the #99 F4 launch-to-app
  block; expiry goes through `expirePeekAndEvict` → `end2MinutePeekEarly`.
- **Repository:** DayWord/DrawerDay work, #99's `expiredPeekPackages`, prune
  note, seal transaction restructured (held-sites written in the same
  transaction, two return paths).

## Per-file resolution

### `service/MonolithAccessibilityService.kt` — hand-composed (3-way apply conflicted; main's pipeline wins as the chassis)
- `InterceptPath.SITE` joins `canHonourPeekLease` (+ doc), as in `c70403e`.
- **Site gate (finding 1):** the `!leaseLive` blanket is removed from branch 4,
  exactly #130's cure, re-expressed in `processEvent` over captured facts. The
  only exemption is `sitePeekExempts(...)` over `SitePeekStore` + the browser
  row's lease end — #130's code, verbatim semantics.
- **Sniper gate (#131) untouched:** still `shortsSniperArmed(...) &&
  sniperFeedVisible`, no lease anywhere near it. The joint lease comment was
  rewritten to state both amendments (#131 sniper half, #130 site half) so the
  next reader does not "restore" either blanket. The now-dead `leaseLive` val
  was removed; `leaseRow` remains (branch 4 reads its end, branch 5 its tier).
- **Supported browsers (finding 3):** new top-level `SUPPORTED_BROWSER_PACKAGES`
  + `isSupportedBrowserPackage` (what `BrowserLoopholeTest` imports). The set
  is the **union** of #130's 13 and main's 9 — main-only Vivaldi
  (`com.vivaldi.browser`) and Opera Beta (`com.opera.browser.beta`) are NOT
  dropped. The private `isSupportedBrowser` now delegates to it.
- **Generic address-bar scan (finding 3, capture half):** #130's
  `findBlockedDomainInAddressBar` returned a matched domain from the node
  tree; main's pipeline separates capture from matching, so the scan became a
  second leg in `collectUrlBarTexts`: an unfocused `EditText` whose whole text
  is a URL (no spaces, plausible host, scheme or host-at-head) is captured as a
  URL text; the worker's host-exact matcher still decides. Same false-positive
  discipline as #130's original (whole-text only, focused bar never read).
- **`matchBlockedDomain` — the one real semantic negotiation (see below).**

### `service/SitePeekStore.kt` — new file, carried over unchanged from `c70403e`
(+ the pure `sitePeekExempts` in the same file).

### `ui/MonolithViewModel.kt` — hand-composed
- `peekOfferFor`: #130's form (SITE bypasses the tier gate; all doors keep
  row/cooldown/pill discipline), doc updated.
- `peekWithheldFor` (main's extracted pure function — where #130's inline
  `when` now lives): its tier gate gained the same SITE exemption, keeping
  main's pill-before-cooldown ordering (the #55 verifier fix).
- `start2MinutePeek`: `SitePeekStore.grant(interceptedAppName, pkg)` on the
  SITE path, placed where `c70403e` placed it (after pkg/name, before the
  latch sound); main's #99 F4 launch block and re-derivation gate untouched.
- `end2MinutePeekEarly`: `SitePeekStore.clearIf(peek.packageName)` added, as in
  `c70403e`. Expiry retires the tag too, via `expirePeekAndEvict` →
  `end2MinutePeekEarly` (fail-closed, as designed).

### `data/MonolithRepository.kt` — hand-composed
- `heldSitesCount(siteCount, browserBlockerEnabled)` added beside #99's
  `expiredPeekPackages` (both top-level helpers survive; the 3-way conflict was
  purely positional) and wired into the seal transaction exactly where #130
  wired it (toggle read from the same snapshot row the service reads,
  default ON).
- **Categorize (finding 3, catalogue half):** #130 edited the repository's
  inline `categorizePackage` `when`. That copy no longer exists on main — the
  repository delegates to `AppCategorizer`. The six recognition strings
  (opera, duckduckgo, kiwibrowser, mark.via, yandex, emmx) were therefore
  folded into `AppCategorizer.categorize`'s BROWSERS branch in
  `data/AppCatalogReconcile.kt`, below #132's MUSIC-first token check and
  #107's cured branches, which are unchanged.

### `data/MonolithModels.kt` — clean 3-way apply
`youtube.com` added to `BlockedBrowserDomains.defaultDomains` after
`youtube.com/shorts`, verbatim from `c70403e` (finding 2).

### Tests
- `test/BrowserLoopholeTest.kt` — new file, carried over unchanged.
- `test/TruthCar3InterceptTest.kt` — clean 3-way apply (SITE now sells a peek).
- `test/ShortsReelsLivedTest.kt` — **modified, deliberately** (see below).

## The two collisions, and how they were resolved

1. **`matchBlockedDomain`: #42 host-exact vs #130's "youtube.com/shorts" print.**
   #42's matcher normalises a stored entry with `substringBefore('/')`, so the
   seeded `youtube.com/shorts` entry (a) silently behaved as a whole-host
   block and (b) could only ever print `youtube.com`. #130's test pins
   `matchBlockedDomain("…/shorts/xyz", defaults) == "youtube.com/shorts"` —
   "the /shorts entry survives for its more specific print". Neither side is
   disposable: #42's anti-false-positive law is pinned by
   `Wave3Issue42AccessibilityTest` + `TruthCar3SweepTest`, #130's print by
   `BrowserLoopholeTest`. **Composed law:** a path-carrying entry matches only
   when the host matches host-exact AND the URL path is that path or beneath
   it (segment boundary), and reports the full entry; a pathless entry matches
   host-exact and reports the bare domain (wildcards included), exactly as #42
   pinned. First match in list order wins (the seed lists the specific entry
   before the bare host). Verified offline against all 14 pinned cases from
   the three suites — all pass. Behaviour note: on main today, a watch-page
   URL matched via the shorts entry's stripped host; after this composition
   it matches via #130's newly seeded `youtube.com` entry — same verdict,
   honest provenance. New pure helper `extractUrlPath` sits beside
   `extractUrlHost`.

2. **`ShortsReelsLivedTest` site-door tripwire vs #130 finding 1.**
   #131's test asserts the service source contains
   `if (!leaseLive && (guardSettings?.browserBlockerEnabled` — its amendment
   was explicitly sniper-only and it froze the site door's blanket lease into
   a tripwire. #130 finding 1 is the separately-ordered site-door amendment:
   the blanket must go. Both *findings* survive (sniper lease-free per #131;
   site blanket-free, scoped-peek-only per #130); the one thing that cannot
   survive is #131's source-string assertion about a door its own lane placed
   out of scope. It was updated to pin the composed truth (blanket string
   absent; `sitePeekExempts(` present), with the reason written into the test
   itself. **Flagged for GESTALT's eye — this is the only sibling-test edit.**
   While there: the same test's sniper-gate substring
   (`if (shortsSniperArmed(guardSettings, pkgName))`) no longer exists on main
   either — the #42 pipeline merge re-shaped that gate to
   `... && snapshot.facts.sniperFeedVisible` after #131 wrote the tripwire, so
   that assertion was already red on current main. It was repaired to the
   gate's current shape (`if (shortsSniperArmed(guardSettings, pkgName) &&`),
   same intent; if CI shows ShortsReelsLivedTest red on main's last run too,
   this repair is the reason it goes green here.

## User-facing changes (restated)

- Peeking a browser app no longer unlocks every blocked website for 2 minutes;
  blocked sites stay blocked during an app peek.
- youtube.com (home + watch, not only /shorts) is on the seeded blocked-sites
  list.
- Opera (×3), DuckDuckGo, Kiwi, Via, Yandex, UC Browser join Chrome, Brave,
  Firefox, Edge, Samsung Internet (and Vivaldi, Opera Beta from main) as
  browsers the site blocker actually polices, including browsers whose URL
  bar has no known view ID; those browsers also file as BROWSERS in the app
  catalogue instead of SHOPPING.
- A blocked site can itself be peeked: the site door sells a 2-minute peek on
  any browser tier, scoped to that one domain in that one browser; every other
  blocked domain still intercepts.
- Session receipts/seal counts claim held sites only when the browser blocker
  toggle is on (off ⇒ 0 held sites, whatever the list holds).

## CI

**Run 41002063168** (composed tip `0e289c0`, parent `d1d3a78`):
https://github.com/Sarthak-2007/Focus-app/actions/runs/41002063168
- Assemble / main-source compile: **GREEN** (the build job's only failing
  step was "Unit tests"; `:app:compileDebugKotlin` passed).
- `:app:compileDebugUnitTestKotlin` **FAILED — a pre-existing break on
  current main, not from this composition**: six call sites in four test
  files (AirlockOverlayIssue55RenderTest ×2, ReceiptSheetTruthTest,
  ShieldSetupTruthTest, VaultHistoryTruthTest ×2) pass
  `dynamicColor = false` to `MyApplicationTheme`, a parameter #63
  deliberately deleted ("There is deliberately no dynamicColor
  parameter", Theme.kt) and whose sibling call sites #63 already
  cured (0861179, de1a859). These four files kept the dead arg, so at
  `d1d3a78` the whole unit-test source set does not compile and NO
  branch based on that main can produce test verdicts. Main's own run
  for `d1d3a78` (41000205295) was still queued at composition time.
- **Repair (separate commit on this branch, after the composition
  commit):** the dead `dynamicColor = false` argument removed at
  exactly those six sites — verbatim #63's own cure — so the test
  suite can run and #130's tests can be judged. It touches no #130
  semantics and no main-source file; GESTALT may take it with the
  composition or lift it into the test-floor lane.
- Post-repair verdict: see the follow-up run on this branch
  (BrowserLoopholeTest + TruthCar3InterceptTest must be green;
  remaining failures must sit inside the shared floor: 
  ScreenshotHarnessTest, DrawerRenderTest, CascadeDeckRenderTest,
  SoundAndSocialTruthTest, TruthCar3Sweep(Robo)Test, BreakMigrationTest,
  ShortsSniperTruthTest migration test, ChromeWaveUnitTest,
  TetradRenderTest, AirlockOverlayIssue55RenderTest, ~40 on main).


---

## Addendum \u2014 recomposition onto main `43f03f1c` (PR #445 conflict resolution)

**New tip's parent:** `43f03f1c586afe8138f6c1a66d382bd984335797`.
PR #445 (agent/issue-130 \u2192 main) went conflicted as main moved
in three steps during this work: `d1d3a78` \u2192 `976863e7`
(design-revamp merges) \u2192 `aa3d8c03` (#140/#147) \u2192
`43f03f1c` (#153 change-of-plans, #282, #317, #318, #163
fix-forward, #44 boot log, platform/visual pin tests). Method
identical to the first composition: the `d1d3a78`\u2192`ed6bafe`
delta re-applied per file onto current main by true 3-way merge;
every hunk hand-verified against this report's semantics.
(Previous tip `ed6bafe`'s final verdict, for the record: run
41015693699 \u2014 541 tests, 40 failed, all shared floor;
BrowserLoopholeTest + TruthCar3InterceptTest + ShortsReelsLivedTest
green; assemble green, run 41011518011.)

### What main gained this generation (all preserved)

- **#133 merged (PR #398; recomposition `60aad322`):** EDUCATION
  category \u2014 `AppCategorizer` gained `DETERMINISTIC_HOMES`
  (Anki \u2192 EDUCATION) and a study token branch ahead of the
  entertainment branches; `LoadoutRules` gained its STUDY branch
  (untouched here, exactly as before); `MonolithModels` lost the
  dead allowed-study-sites list.
- **Classifier restructured:** `AppCategorizer.categorize` no longer
  matches substrings on a lowercased string \u2014 it tokenises
  package+label and matches whole-token sequences (`matches(...)`,
  fresh-eyes L2 discipline).
- **#63 floor repair landed on main itself (PR #390):** all six
  dead `dynamicColor` call sites that the previous generation's
  labelled repair commit fixed are already cured on main \u2014 the
  code half of that commit is a no-op on this main and was dropped;
  the tree still carries the repair, inherited from main.
- **#140 daily-limit-ratchet + #147 stopwatch-floor (`aa3d8c03`):**
  service branch 5 binds OPEN apps at their daily limit
  (`overLimit`); ViewModel ratchet wording, `cycleAppDailyLimit`,
  `sessionEndsSuffix`, airlock/disarm floor fields \u2014 all in
  regions disjoint from every #130 hunk, preserved by the merge.
- **Third move (`43f03f1c`):** #153/#282/#317/#318/#163/#44 and the
  pin-test lanes touched `MonolithModels`, `MonolithRepository`
  and `MonolithViewModel` \u2014 again disjoint from #130's hunks
  (verified against the compare patch set) and preserved.

### Per-overlap resolution

- `data/AppCatalogReconcile.kt` \u2014 **the only textual conflict,
  hand-resolved.** #133's classifier kept whole (deterministic
  homes, EDUCATION branch, token GAMES/MESSAGING). #130's six
  browser recognitions re-expressed in token discipline in the
  BROWSERS branch: `opera`, `duckduckgo`, `kiwibrowser`, `mark via`
  (the `mark.via.gp` package tokenised), `yandex`, `emmx` \u2014
  same recognitions, main's current grammar.
- `data/MonolithModels.kt` \u2014 clean apply: the `youtube.com`
  seed lands directly above #133's dead-list-removal note; both
  survive, as does the third move's Models work.
- `service/MonolithAccessibilityService.kt`,
  `ui/MonolithViewModel.kt`, `data/MonolithRepository.kt` \u2014
  3-way merged onto main's current shapes; each #130 hunk verified
  by hand: site gate free of the `!leaseLive` blanket with
  `sitePeekExempts` as the only door; `SUPPORTED_BROWSER_PACKAGES`
  union set; generic address-bar leg in `collectUrlBarTexts`;
  composed `matchBlockedDomain` path law + `extractUrlPath`; joint
  lease comment stating both amendments; sniper gate (#131)
  untouched; SITE in `canHonourPeekLease`; `peekOfferFor` /
  `peekWithheldFor` SITE branch; `SitePeekStore.grant` in
  `start2MinutePeek` and `clearIf` in `end2MinutePeekEarly`;
  `heldSitesCount` wired into the seal transaction.
- `service/SitePeekStore.kt`, `test/BrowserLoopholeTest.kt` \u2014
  carried unchanged (new files).
- `test/ShortsReelsLivedTest.kt` \u2014 carried (unchanged on main
  through all three moves; its tripwires assert against the
  composed service and hold). `test/TruthCar3InterceptTest.kt`
  \u2014 clean apply (SITE sells a peek).

### CI

Verdict for this tip: the branch run on this tip, reported in the
#130 handoff to GESTALT (assemble + test compilation must pass;
BrowserLoopholeTest + TruthCar3InterceptTest + ShortsReelsLivedTest
green; failures within the shared floor).

---

## Final addendum — landing onto main `5f476674` under GESTALT ruling (a) (#79 wins)

**New tip's parent:** `5f476674f3575ab7a95ed565af09b7cde85bd2fd`
(main had moved `43f03f1c` → `f4d00677` → `5f476674` during the
landing: PR #442/#304, PR #470/#120, PR #396/#80). Method: true
3-way per file (base `43f03f1c` vs branch tip `4a63d396` vs current
main), the service and ViewModel hand-resolved as below.

### The collision

Since the `43f03f1c` composition, main landed **Issue #79**
(PR #383): a wholesale rewrite of the domain matcher in
`service/MonolithAccessibilityService.kt` — `urlCandidates` /
`parseSiteRule` / `ruleMatches` machinery, proxy unwrapping,
`BlockedBrowserDomains.normalizeDomain` canonicalisation. Its law:
a matched rule prints its **canonical host**. That collides with
exactly one piece of #130: the composed path-print matcher
(`extractUrlPath` + the path-aware `matchBlockedDomain`) and the
test assertion pinning its print,
`matchBlockedDomain("…/shorts/xyz", defaults) == "youtube.com/shorts"`.

### Ruling (a), applied

- **#79's matcher and its host-print law WIN.** #130's matcher
  implementation is DROPPED in full: `extractUrlPath` is gone, and
  the merged service's matcher region is byte-identical to main's
  (`matchBlockedDomain`, `matchEmbeddedBlockedDomain`,
  `parseSiteRule`, `urlCandidates`, `ruleMatches` — zero diff vs
  main across the whole #79 machinery).
- **Amended assertion** (`BrowserLoopholeTest`): the shorts-print
  assertion now pins #79's host print —
  `matchBlockedDomain("https://www.youtube.com/shorts/xyz", defaults) == "youtube.com"` —
  with a comment citing this composition ruling (#79 canonical
  host print supersedes #130 entry print, GESTALT ruling on the
  #130 landing). The other two seed assertions already pinned
  `"youtube.com"` and are unchanged.
- **Everything else of #130 survives unchanged:** `youtube.com`
  seed in `MonolithModels`; site gate free of the `!leaseLive`
  blanket with `sitePeekExempts` as the only door;
  `InterceptPath.SITE` honouring the peek lease;
  `peekOfferFor`/`peekWithheldFor` SITE branch (composed with
  #120's FINANCE branch, which landed on main after #130's base —
  SITE checked first on its own door, FINANCE verbatim on the app
  doors, withheld condition = the union of both exemptions);
  `SitePeekStore` grant/clear in start/end peek;
  `SUPPORTED_BROWSER_PACKAGES` union set; generic address-bar
  capture leg; `heldSitesCount` truth fix; browser token keys in
  the classifier's BROWSERS branch (AppCatalogReconcile, token
  grammar intact); #131's sniper gate untouched; joint lease
  comment.

### Site-peek scoping, re-verified against #79's matcher (ruling's required check)

- The peek tag is whatever the SITE intercept carried as its app
  name, and the service sets that to `matchedDomain` — the
  matcher's return. Under #79 that is the canonical host
  (`rule.host`), so the tag and the gate's later `matchedDomain`
  live in the same print space on both sides of the grant.
- `sitePeekExempts` is exact string equality on (domain, package)
  plus a live Room lease end. A peek granted on one host cannot
  exempt another host; a peek granted under one package cannot
  exempt another package. `BrowserLoopholeTest`'s site-peek tests
  assert exactly that discipline (granted-domain/other-domain,
  granted-browser/other-browser, null tag, spent/absent lease,
  scoped `clearIf`) at full strength, unchanged — they use bare
  host prints (`reddit.com`, `instagram.com`), which are #79
  prints too. **No #130 test relied on entry-print for peek
  scoping** — the only entry-print reliance anywhere was the
  display assertion amended above. Nothing was narrowed.

### Two further collisions found on this main (not visible to the earlier analysis)

1. **#80's step 4b (any-host loaded-URL door) arrived after
   #130's base, carrying its own `!leaseLive` blanket** — and the
   branch's deletion of the now-dead `leaseLive` val would have
   left 4b referencing a deleted val (uncompilable) while keeping
   the blanket #130 exists to remove. Resolution, per #80's own
   comment ("honoured exactly as in step 4") and ruling (a)'s
   "site gate free of the blanket, `sitePeekExempts` the only
   door": 4b's loaded-URL leg is cured the same way as branch 4 —
   no blanket, scoped site peek only, keyed to the host package
   (the package a SITE peek on that door is granted under; its
   matcher is `matchBlockedDomain` itself, so prints agree). 4b's
   host-surface sub-door is an app-naming door, not a site door:
   it keeps honouring the lease, so `leaseLive` is restored for
   it (and branch 5). `ShortsReelsLivedTest`'s blanket-absence
   tripwire therefore holds over the whole file at full strength.
2. **#79's `UrlBlockerBypassesTest.embed_pathRules_doNotFireOnEmbeds`
   asserted embed-null on `defaults`** — true only while defaults
   held no bare YouTube rule. #130's surviving seed adds bare
   `youtube.com`, and a bare host rule legitimately fires on
   embeds. Re-expressed truthfully, not narrowed: the path-rule
   discipline is now asserted against the explicit path-rule list
   (`listOf("youtube.com/shorts")` → null, same intent, full
   strength), and the defaults case pins the seed's real effect
   (`"youtube.com"` via the bare rule). Flagged for GESTALT: this
   is a sibling-test edit beyond the ruling's named amendment,
   forced by the seed the ruling kept.
- Merge casualty caught and repaired: the 3-way merge had
  silently dropped main's #78 `airlockIntentRevalidated`
  definition from `MonolithViewModel.kt` (its call sites
  survived). Restored verbatim from main.
- `DeskClockLivedTest` dead-arg: main's floor lane already
  applied the identical cure — inherited from main, not
  duplicated (this tip does not touch the file).

### CI

Verdict for this tip: reported in the #130 handoff to GESTALT
(test compilation must pass; BrowserLoopholeTest +
TruthCar3InterceptTest + ShortsReelsLivedTest green; failures
within the shared floor).

---

## DURCH-D landing recomposition (2026-10-30, closure lead under DURCH-D)

Recomposed PR #445 onto main `2531c847` (merge PR #489, heiler-drift) — the
#283/#287 `extendActiveSession` clash that blocked the earlier landing is
cured on main (distinct verbs: `extendActiveSessionBySeconds` /
`extendActiveSessionReturningSession`). Merge conflicts: exactly 2, both
resolved as unions —
- `AppCatalogReconcile.kt`: BROWSERS token set = main's tokens ∪ #130's
  (`kiwibrowser`, `mark via`).
- `MonolithRepository.kt`: main's Issue-67 `db()` + W-41 held split kept;
  `heldSites` computed via #130's `heldSitesCount(...)` (0 when the browser
  blocker is off).
All other #130 files auto-merged; verified post-merge: site doors carry no
`!leaseLive` blanket (MonolithAccessibilityService), `InterceptPath.SITE`
honours the peek lease, `SitePeekStore` present, `youtube.com` seeded
(MonolithModels), `BrowserLoopholeTest` present.
