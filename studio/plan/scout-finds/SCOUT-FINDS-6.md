# Scout Finds 6 — Onboarding Ceremonies

**Best serves: GitHub Issue #14 — Setup Sheet + onboarding** (first-open sheet that walks the user through accessibility / usage access / device admin / notification-listener grants with live honest states — SET / NOT SET / UNPROVED / NOT CHECKED — and ends with a tear-away DONE). Secondary use: the launcher-style block screen revamp, because several finds below are about how a *threshold moment* — tap, hold, tear — makes a block feel entered, not just toggled.

Hunting ground: first-run experiences that feel like a ritual or induction rather than a form. Only elite, genuinely interesting examples kept. Generic carousel tours, quiz-paywall funnels, and "3 screens + Skip" onboarding were discarded.

---

## 1. Arc Browser — The Unboxing & Membership Card

**Source:**
- https://www.inverse.com/input/design/the-browser-company-arc-design-interview
- https://www.howtogeek.com/887230/arc-browser-review/

**What makes it elite:**
Arc refused to open with settings. Its first run is explicitly called an "unboxing": a lush, multi-step tutorial in film/game language that teaches by doing, finishes with choosing a colour/space, and ends by issuing a personalised Arc "membership card" — *you've completed the training; you're in the club.* The Browser Company built it because they had been onboarding people manually on calls; the ceremony had to carry that human induction at scale. The card is useless functionally. That's the point — it's proof of passage, kept, shareable, named.

**What applies to Monolith:**
Monolith's setup sheet should not end with a green tick and a dismiss button. It should end by *issuing* something: a stamped setup card on the sheet itself — date, which grants were proved live, device name — that then tears away (see Find 6). Sequence like Arc too: don't ask for the scariest grant first. Arc earns each step by letting you *use* the thing you just configured. For Monolith: after Accessibility is SET, immediately prove it — show the live foreground-app name changing as the user switches apps for 3 seconds inside the sheet. Use before next ask. The membership-card lesson: people remember an induction they can point to; they forget a checklist they endured.

---

## 2. blockit — Honest Accessibility Disclosure, Written Like a Contract

**Source:**
- https://play.google.com/store/apps/details?id=com.hypenet.focused&hl=en_US
- https://www.appbrain.com/app/blockit-%7C-stop-phone-addiction/com.hypenet.focused

**What makes it elite:**
blockit is Monolith's closest ancestor (Teenage Engineering-inspired analog wheel, haptic, "set timer → initiate session → embrace freedom", a *suitcase* you pack with allowed apps, a scarce *parachute* as the only early exit). Its Play Store listing contains the best permission prose in the category, verbatim in spirit: it uses AccessibilityService "for one single purpose… detects which app is brought to the foreground… does NOT read your screen content and does NOT collect, store, or share any personal or sensitive data… processed on-device only and only while a session is active. The service does nothing when no session is running. Enabling it is optional and requires your explicit consent in system settings, but without it blockit cannot prevent…"

Four moves in one paragraph: single purpose, explicit NOT-list, when it is inactive, and the honest cost of refusing. No euphemism, no hiding that it's powerful.

**What applies to Monolith:**
Copy this structure per-grant, inside the sheet, *before* the system Settings jump — not in a store listing afterwards. Each Monolith grant gets: (1) one purpose sentence, (2) a NOT-list ("does NOT read screen content / messages / passwords"), (3) when it does nothing, (4) what breaks without it in plain words ("Without this, Monolith cannot pull you back to the block screen — it can only watch usage"). Keep blockit's object-naming too: suitcase/parachute made abstract states graspable. Monolith's grants deserve physical names on the sheet (e.g. the Watcher = accessibility, the Ledger = usage access) *only if* the honest states stay literal underneath — name for memory, state word for truth, never name *instead* of truth.

---

## 3. Brick — The Tap as Ceremony: Crossing a Threshold in the Physical World

**Source:**
- https://www.fastcompany.com/91611256/people-are-bricking-their-smartphones-on-purpose-to-stop-doomscrolling
- https://cybernews.com/reviews/brick-phone-blocker-review/

**What makes it elite:**
Brick is a $59 battery-free NFC puck. You configure modes in the app, then you *tap your phone to the puck* to block, and tap again — walking back to wherever you left it — to unblock. Reviewers keep returning to the same word: ceremony / threshold. "Crossing that threshold feels like a physical commitment rather than a transient setting adjustment." Remote-start is allowed; unlock is not remote — it demands the return trip, plus a ration of 5 Emergency Unbricks per device. The ritual works because the body does it: stand, walk, tap. Software-only blockers fail at the exact moment Brick succeeds, because a toggle has no weight.

**What applies to Monolith:**
Monolith has no puck, so it must manufacture the tap from what it has: the return-from-Settings *is* the walk. Design setup around that instead of apologising for it. Each grant is: leave Monolith (walk away) → do the deed in Android Settings (tap the puck) → return and be *re-checked live* on the spot. Make the return a beat — sheet re-scans, state flips NOT SET → SET with a haptic/stamp, progress physically accumulates. And borrow the Emergency Unbrick ration logic for honesty at setup time: state the emergency-break budget *during* onboarding (e.g. "1 emergency break / week, 60-second hold") as part of the induction, not discovered later in settings. A commitment you were told the price of up front is a ceremony; one you discover is a trap.

---

## 4. one sec — Rehearse the Ritual During Setup: Friction + Deliberation + Dismiss

**Source:**
- https://pmc.ncbi.nlm.nih.gov/articles/PMC9974409/
- https://apps.apple.com/am/app/one-sec-screen-time-focus/id1532875441

**What makes it elite:**
one sec doesn't block — it inserts a ~10-second delay with a moving screen, a deliberation message naming what you're about to do, and an explicit option to dismiss opening the target app. The peer-reviewed study (with Max Planck Institute collaborations cited by the app) decomposes it into exactly three components: *friction (delay), deliberation (message), choice (dismiss)* — and usage drops ~57% on average in their reported research. Crucially, the intervention is identical every time, so it becomes a recognisable rite the nervous system learns, not a surprise dialog to swat away.

**What applies to Monolith:**
Setup should *rehearse* the block ceremony once, not just describe it. After all grants read SET/UNPROVED honestly, run a 30–60 second practice block inside onboarding: user tries to open a chosen distracting app, Monolith pulls them to the launcher-style block screen, holds, then releases. They leave onboarding having already lived the rite — first real block is then a repetition, not a jump-scare. Also steal the three-part decomposition for the setup sheet's final hold (see Find 7): the tear-away DONE should have friction (a sustained hold, not a tap), deliberation (a line naming what is now armed: which grants, which apps, what the emergency break costs), and a genuine dismiss/back path until the hold completes.

---

## 5. Opal — The First Session Is the Initiation: A Short, Uncancellable Proof

**Source:**
- https://www.commonsensemedia.org/app-reviews/opal-screen-time-for-focus

**What makes it elite:**
Opal's onboarding doesn't end at permission + schedule. Once set up via its (honestly explained) VPN/Screen Time permissions, it pushes you straight into a first challenge: a 10-minute focus session "in which you can't refresh data in social media, video, news or other distracting apps. During that time, there's no way to cancel the session, so it ends only when the clock runs out." Ten minutes is the genius duration: long enough to prove the block is real and to feel the itch, short enough that agreeing costs almost nothing. The product's promise is demonstrated on your own phone, with your own apps, before any paywall conversation matters.

**What applies to Monolith:**
This is the strongest argument for ending Monolith's setup sheet with a *proof block*, not a success screen. Specifically: DONE tears away → sheet offers/starts a first block of ~5–10 minutes with the user's already-chosen apps → block screen shows live honest state throughout. If a grant was UNPROVED at setup (e.g. notification listener granted but no notification has yet arrived to prove it listens), the proof block is where UNPROVED gets resolved by evidence or stays UNPROVED on screen — truthfully. Never let onboarding declare "You're protected" on the basis of toggles alone; Opal's lesson is that a short lived sentence beats a paragraph of assurance. Keep it uncancellable *except* the stated emergency break, exactly as disclosed in Find 3.

---

## 6. 1Password — The Emergency Kit: A Printed, Hand-Filled, Tear-Away Artifact

**Source:**
- https://support.1password.com/emergency-kit/

**What makes it elite:**
The scariest setup in consumer software — a Secret Key 1Password itself cannot recover — is made trustworthy by turning setup into a paper ritual. On account creation you are asked to save the Emergency Kit: a PDF with sign-in address, email, Secret Key, a Setup Code, and a *blank space to write your password by hand*. Official guidance: print it, fill it in by hand, store it with passport/birth certificate or in a safe deposit box, give a copy to someone you trust. The hand-writing is not nostalgia; it forces slowness, proves you actually handled your one unrecoverable secret, and creates an artifact whose existence is the setup's receipt. The ceremony admits the danger out loud — lose this and we cannot save you — and that admission is why people trust it.

**What applies to Monolith:**
Direct model for the tear-away DONE. The end of Monolith's sheet should produce a *setup record* the user keeps — in-app, in the quiet drawer: dated, listing each grant and its final honest state at completion (SET / NOT SET / UNPROVED / NOT CHECKED — never upgraded for tidiness), what Monolith can and cannot do with them, and how to revoke each one in Android Settings. Tearing away the DONE stub should feel like tearing the perforated receipt off that record: the stub goes, the record stays, forever viewable. And copy 1Password's candour pattern for Device Admin specifically: name the worst true sentence on the sheet ("This lets Monolith make itself hard to uninstall during a block. You can remove it any time at Settings → …; Monolith will then show NOT SET and stop claiming it can hold a block.") — the grant users fear most converts best when its exit is printed next to its entry.

---

## 7. Headway's "Seal the Pact" Hold + Duolingo's Ordering — Commitment Is a Physical Act, Asked Last

**Source:**
- https://onboardme.substack.com/p/how-to-make-users-love-your-app-onboarding-positive-feedback-loop-duolingo-headway-fabulous
- https://gummble.com/blog/duolingo-onboarding-flow-analysis

**What makes it elite:**
Two halves of one lesson. Headway ends its onboarding by making the user *press and hold* a button to "seal the pact": the hold creates a pause, the word "pact" adds weight, and the physical duration makes the moment memorable in a way a Continue tap never is. Duolingo supplies the ordering: personalisation questions → a real first lesson (value delivered) → streak/commitment device → account creation *only at screen ~13*, framed as "save what you earned," after ~5 minutes of investment. Commitment devices in Duolingo are also humane — streak *freezes/repairs* exist so one bad day doesn't nuke progress. Ask last, ask physically, forgive explicitly.

**What applies to Monolith:**
This is the interaction spec for the tear-away DONE itself:
- **Order:** no grant is asked before the user has chosen what to block and seen why each grant maps to that choice (Duolingo: value/purpose before asks). The sheet is grants *in service of a named block list*, not four system toggles in a row.
- **Act:** DONE is a sustained press-and-hold (Monolith already uses a 60-second hold for its weekly emergency break — reuse the same muscle at shorter length, ~3 seconds, so onboarding *teaches* the hold gesture the app will later demand). On completion the stub tears away with haptic + visual perforation.
- **Forgiveness printed in advance:** like streak freeze, the pact screen states the exits before sealing — emergency break ration, how to revoke any grant, what state the app will honestly show if you do. A pact with its exits written on it is induction; a pact without them is a dark pattern, and Monolith's whole brand is TELL-THE-TRUTH.

---

## Cross-cutting spec distilled for #14 (Scout's synthesis)

1. **Pre-permission card per grant** (blockit prose structure): purpose / NOT-list / idle behaviour / cost of refusing — shown *before* leaving for Settings, in Sora/Atkinson type, no jargon.
2. **Leave-and-return is the ceremony** (Brick): on return, re-check live and stamp the state change in view. States are only ever SET / NOT SET / UNPROVED / NOT CHECKED as actually measured at that moment; returning to the sheet re-measures, never caches a hopeful SET.
3. **Proof, not promise** (Opal + one sec): after grants, one short rehearsal/proof block resolves UNPROVED by evidence or leaves it labelled UNPROVED.
4. **A kept artifact** (1Password + Arc): setup record stays in the drawer; the DONE stub tears off it via a sustained hold (Headway) that also teaches the emergency-break gesture.
5. **Exits printed before sealing** (Duolingo freezes + 1Password candour): emergency-break budget and per-grant revocation paths are part of the induction text, not settings archaeology.

— Schwelle (Scout 6)
