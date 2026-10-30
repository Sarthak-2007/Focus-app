# ISSUE #68 SCOUT — Audit R1 · security-privacy (5 findings)
## Security-practice scout for PRISMA (Wave-3 cluster C10)

- **Repo / base:** Sarthak-2007/Focus-app, `origin/main @ e29acdf`, base `~/workspace/clone-inspect/builds/cells/w3c10-base`
- **Bundle read first:** `ISSUE-68-VERIFY.md` — F1 OWNED-BY #45, F2 OWNED-BY #36, F3 OWNED-BY #42, F4 OWNED-BY #44, F5 HOLDS (Prisma cures)
- **Method:** Live web/GitHub sweep 30 Oct 2026. Every URL below is copied verbatim from a search-result `Full URLs` list — no constructed links. Where a secondary source summarises an official policy, that is said; the official page is cited wherever it was returned.
- **Standing rule applied:** Any practice that trades away a user-good (weaker blocking, lost protection, a ruled feature) is flagged **DECISION-NEEDED** and is never silently recommended.
- **No code was edited. No issue was edited.**

---

## Scout's cure adjustments — one line per finding (summary; detail + ranking below)

| # | Scout's cure adjustment |
|---|---|
| **F1** | **ADJUST #45:** Split, don't move — keep only minimal lock state in device-protected storage and put `captured_notifications` in a separate credential-protected (ideally SQLCipher, raw-Keystore-key + WAL) database; TTL + `onNotificationRemoved` stand, but metadata-only/title-only capture is **DECISION-NEEDED** because it kills Sarthak's ruled held-messages full-peek. |
| **F2** | **ADJUST #36:** Bundle's fail-closed cure stands and is the whole honest cure — Play Integrity and remote key attestation are **not available to a local-only, no-server app** and must not be promised; add the bundle-missing piece: verify `KeyInfo.getSecurityLevel()` and label the seal truthfully ("device-local tamper-evidence, owner can still defeat it with time/root"), never "hardware integrity". |
| **F3** | **ADJUST #42:** Disclosure rewrite stands, but compliance is a *package*, not a string — non-accessibility-tool declaration + demo video of the disclosure/consent flow + listing disclosure + a written "why UsageStatsManager is insufficient" (Shorts/Reels node detection + URL-bar reading), worded for Play's Oct-2025 deterministic rule-based automation exception; narrowing the service away is **DECISION-NEEDED** (loses Shorts/Reels + site blocking). |
| **F4** | **ADJUST #44:** Go further than the bundle — delete the Device Admin receiver entirely rather than keeping a truthfully-worded warning grant; **DECISION-NEEDED:** this *does* lose the deactivation-friction user-good, and the honest replacement (in-session uninstall/deactivation *detection* + a 60-second-hold in-app exit ritual) is friction, not prevention, and must be sold as such. |
| **F5** | **REPLACES Tier 2 of the bundle spec:** Keep Tier 1 revalidation verbatim, but drop the prefs-nonce — split the intercept into a separate `exported="false"` intercept activity reached only by explicit in-app intents, and make the exported LAUNCHER `MainActivity` deaf to intercept extras; no shared state, no 30 s replay/expiry window, no process-death edge. |

---

# F1 — Notification title+body persisted indefinitely, unencrypted, device-protected Room DB [CRITICAL → #45]

Bundle spec (adopted as floor): TTL prune on insert + on connect (24 h–7 d), hard row cap at insert, `onNotificationRemoved` honouring sender deletion (persist `sbn.key`), minimise stored body, address the device-protected storage class.

### Ranked practices

**1. Split storage by sensitivity — minimal data in device-protected, sensitive data in credential-protected**
- Source: https://android-developers.googleblog.com/2016/04/developing-for-direct-boot.html?utm_campaign=android_series_blogpost_062116&amp
- Practice (one line): In Direct Boot, device-protected storage gets only the minimum needed pre-unlock (Google's own example: a narrow-scope token / a count); *"all sensitive, private information, like the full message history… should still be saved in credential protected storage."*
- Corroborated verbatim in the API reference — device-protected data *"is available without user authentication… storing sensitive authentication tokens or passwords in the device-protected area is strongly discouraged"*: http://developer.android.com/reference/kotlin/android/test/IsolatedContext
- And bluntly: *"Device Encrypted Storage should not be used to store any sensitive data!"* — https://www.securecodewarrior.com/blog/secure-coding-technique-android-device-encryption-and-direct-boot
- **Mapping: ADJUSTS the bundle.** The bundle offered "move the table out *or* state why pre-unlock plaintext is accepted". The elite shape is neither: **split the database.** Lock/session state (the thing that must survive reboot and fire pre-unlock) stays device-protected and minimal; `captured_notifications` moves to a second, credential-protected Room DB. Monolith's whole DB currently sits in device-protected storage (`MonolithDatabase.kt:233-243`), so a table move alone does nothing — it is a DB split.
- Cost: ~1 new Room DB + migration that *deletes* (not copies) pre-split captured rows after the retention window; 0.5–1 day incl. tests. No blocking loss: the shield never needed message *bodies* pre-unlock.

**2. Do not record content unless the function absolutely requires it; minimise + expire by design**
- Source: https://tmliu06.github.io/pdfs/notification-listener-issta2025.pdf
- Practice: *"For apps legitimately using NLS, the fundamental principle should be to avoid recording notification content unless it is absolutely necessary for their intended functionalities"* — with encryption + secure storage required when recording is necessary (ISSTA 2025, "Walls Have Ears: Demystifying Notification Listener Usage in Android Apps").
- Same pattern in a current secure-messaging design: ciphertext-only persistence, TTL expiration enforced via WorkManager cleanup, and no content-bearing push payloads — https://arxiv.org/html/2603.16735v2
- **Mapping: ADOPTS the bundle's TTL/cap/`onNotificationRemoved` (items 1–2) unchanged; FLAGS item 3.** Title-only / truncated-body-by-default directly contradicts a ruled feature — held-messages full-peek was confirmed as Sarthak's ruling (U2, Oct 29). **DECISION-NEEDED:** metadata-only capture is the privacy-best practice *and* it deletes a feature he chose. Scout's recommended middle, still his call: full body kept, but only until session end + a short grace (e.g. digest expiry at session end + 24 h), then body column wiped leaving sender + timestamp. That keeps the peek, kills the archive.
- Cost: TTL/cap/removal-sync ≈ 30–50 lines + DAO tests (bundle estimate stands). Body-wipe-on-expiry ≈ +10 lines.

**3. Honour removal as a lifecycle, and respect Android 15 sensitive-notification limits**
- Source: https://copyprogramming.com/howto/listening-for-notification-on-android
- Practice: Handle both `onNotificationPosted` and `onNotificationRemoved`; *"avoid logging or sending raw notification text off-device unless essential and clearly disclosed"*; Android 15's `RECEIVE_SENSITIVE_NOTIFICATIONS` regime blocks normal apps from reading sensitive notifications such as OTPs unless they hold a special role — do not design around capturing OTP-class content at all.
- **Mapping: ADOPTS bundle item (2)** and adds: persist `sbn.key` + `postTime` (bundle already requires this), delete on `REASON_APP_CANCEL` / delete-for-everyone equivalents, and explicitly exclude OTP/sensitive-category notifications from body capture where the platform signals them.
- Cost: included in (2); sensitive-category exclusion ≈ 5–10 lines.

**4. If bodies must be at rest, SQLCipher — but configured, or it costs 685 ms an open for nothing**
- Source: https://medium.com/@salesfrancisco73/what-sqlcipher-really-costs-on-android-not-the-encryption-the-configuration-7f86006fecc2
- Practice (measured, Galaxy A55, Android 16, Oct 2026): encryption itself is *"0–3% more space and ~60 µs of CPU per page"*; the real costs are configuration — pass a random Keystore key as a **raw key** (*"~685 ms vs. ~5 ms"* per open vs the PBKDF2 passphrase path), **use WAL explicitly** (rollback-journal commits were 15–18× slower), and batch writes.
- Older canonical cost account agrees on the shape: APK size grows with the native libraries, and the PBKDF2 open cost is paid once per process if the DB is opened once — https://commonsware.com/Room/pages/chap-sqlcipher-004
- **Mapping: ADJUSTS the bundle.** Encryption is *second-order* here: after the split in (1), the captured-notifications DB is already credential-protected (file-based encryption tied to the user's credential). SQLCipher adds defence against an unlocked-device / rooted extraction, at the price of a second SQLite engine, native-lib APK growth, and a non-trivial unencrypted→encrypted migration (create new DB, copy, delete old). Scout ranking: **do (1)+(2) first; SQLCipher only on the notifications DB, raw-key + WAL, if PRISMA wants the extra layer — never on the device-protected lock DB** (a Keystore/CE-gated key cannot be available pre-unlock, so SQLCipher there would break Direct Boot blocking — **that variant is DECISION-NEEDED / effectively a blocking regression**).
- Cost: ≈10 lines of builder/factory code (per the measurement article) + migration + open-latency regression test; ongoing: one more native dependency to track.

---

# F2 — HMAC forgeable: hardcoded fallback key + blank-signature bypass [CRITICAL → #36]

Bundle spec (adopted as floor): delete the fallback literal + all fallback paths, fail closed when no Keystore key, blank signature on an active session verifies **false** (one logged re-sign-or-invalidate migration), `MessageDigest.isEqual`, stop claiming "hardware" without a security-level check.

### Ranked practices

**1. A shipped symmetric key is not a key — OWASP MASWE-0004 / CWE-321, CWE-798**
- Source: https://mas.owasp.org/MASWE/MASVS-STORAGE/MASWE-0004/
- Practice: *"The app does not hardcode sensitive data in the application package"* — hardcoded *"cryptographic material such as symmetric or private keys embedded directly in the package (as opposed to being generated and stored in a platform keystore)"* is recoverable by anyone who downloads the package.
- **Mapping: ADOPTS the bundle verbatim.** This is the authoritative citation #36 should carry: `CryptoIntegrityManager.kt:29` is a textbook MASWE-0004 hit, and the silent downgrade paths (`:64-79`) make every signature ambiguous. Test-only keys belong in test source sets, injected — never in production code.
- Cost: bundle's — deletion + fail-closed branch; no new dependency.

**2. Check the key's actual security level; never assert "hardware" from the API name alone**
- Source: https://developer.android.com/privacy-and-security/keystore
- Practice: Obtain a `KeyInfo` for the key; for targetSdk ≥29 inspect `getSecurityLevel()` — *"Return values matching `TRUSTED_ENVIRONMENT` or `STRONGBOX` indicate that the key resides within secure hardware"*; below that, `isInsideSecurityHardware()`.
- API surface confirmed at: http://developer.android.com/reference/kotlin/android/security/keystore/KeyInfo
- Honest limit, well stated here: the `KeyInfo` check *"tells you, on the device, what level of hardware protection the key actually has — but it is not remotely verifiable, and the check is running in the same process that holds the key"*; symmetric (AES/HMAC) keys also cannot get the certificate-chain attestation asymmetric keys can — https://attomus.com/blog/2026-android-keystore-aes-attestation-limits/
- **Mapping: ADJUSTS the bundle's item (4) from a wording fix into a mechanism:** at key creation, read `securityLevel` once, persist it alongside signatures (or as an app-level integrity state), and drive both behaviour and copy from it — `STRONGBOX`/`TRUSTED_ENVIRONMENT` → "hardware-backed seal"; `SOFTWARE` → seal still works as tamper-evidence against casual edits, UI/docs must not say "hardware". **DECISION-NEEDED (small):** if PRISMA instead wants to *require* TEE+ and fail closed on software-only devices, low-end devices lose session integrity entirely — that is a blocking-availability trade, not a free hardening.
- Cost: ~15 lines + one persisted field; no dependency.

**3. Play Integrity is the industry-standard answer — and it is structurally unavailable to this app**
- Source: https://developer.android.com/google/play/integrity/standard?hl=en&authuser=19
- Practice: A standard request ends with *"Your app passes the integrity token to your app's backend. Your app's backend sends the token to a Google Play server. The Google Play server decrypts and verifies the verdict… Your app's backend determines how to proceed."* The classic flow is the same shape — server-generated nonce, verdict verified on the backend: https://developer.android.com/google/play/integrity/classic
- Verdict vocabulary (genuine binary / genuine Play install / genuine device) is likewise defined around forwarding the verdict *"to your server for verification"*: https://support.google.com/googleplay/android-developer/answer/11395166?hl=en-mt&ref_topic=13878452
- **Mapping: REPLACES any hope of "attestation will fix F2" — by ruling it out, honestly.** Monolith is local-only, has no backend, and verdict *verification* is the server's job; a purely on-device check of its own token is the same self-attestation circle as a local HMAC, plus a Play-install/network dependency. **DECISION-NEEDED if anyone proposes adding it:** it would trade away offline/local-only operation and non-Play installs for a signal that still does not stop the device owner. Scout recommendation: do not adopt; record the rejection *in #36* so a future audit does not re-propose it.
- Cost if adopted (for the record): backend service + Play Console/Cloud project + token plumbing — disproportionate, and out of scope for a local blocker.

**4. What a local-only app can truthfully claim (the replacement claim)**
- Synthesis of (1)+(2) + the Keystore guarantee itself: Keystore key material bound to secure hardware *"is never exposed outside of secure hardware"* and an attacker who can read storage *"might be able to use any app's Android Keystore keys on the Android device, but it can't extract them from the device"* — https://developer.android.com/privacy-and-security/keystore
- Practice: The honest claim is **tamper-evidence against in-app / casual modification, and non-exportability of the key** — not unforgeability against the owner. The owner-adversary (the bundle's own threat note: the user cheating his own lock) can, with root/instrumentation and time, invoke the key or patch the verifier. Elite local-only posture is therefore: fail-closed Keystore HMAC + `MessageDigest.isEqual` + security-level labelling + *no fallback, no blank-pass, no "hardware/unbreakable" wording anywhere the seal is described*.
- **Mapping: ADOPTS the bundle and gives #36 its closing sentence** — copy along the lines of: "Session state is sealed with a non-exportable device key. Editing the database, restoring an old session, or forging a signature fails verification. This raises the cost of cheating; it does not make cheating impossible on a device you fully control."
- Cost: copy only.

---

# F3 — Accessibility reads all window content/URLs; description claims launch-detection only [HIGH → #42]

Bundle spec (adopted as floor): rewrite `strings.xml:4` to disclose window-content + address-bar reading, in-app prominent disclosure before enabling, narrow/justify config flags, produce a privacy policy / Data Safety draft or explicitly accept the declaration risk.

### Ranked practices

**1. Compliance is a package: declaration + video + in-app disclosure + listing — a privacy policy alone is not enough**
- Source (official policy summary): https://support.google.com/googleplay/android-developer/answer/16909972
- Practice: *"Only services designed to help people with disabilities… are eligible to declare themselves as accessibility tools by setting `isAccessibilityTool=true`… For all other uses… you will be required to complete an accessibility declaration in Play Console and must implement a clear in-app disclosure explaining data access and use, and obtain affirmative user consent."* The same page bars Accessibility from being used to *"prevent the ability for users to disable or uninstall any app or service unless authorized by a parent or guardian through a parental control app or by authorized administrators through enterprise management software"* — note the direct collision with F4.
- Corroborated in current rejection practice: *"Apps that aren't accessibility tools need a prominent in-app disclosure (a privacy policy isn't enough), affirmative consent and a Play Console declaration"* — https://www.appsubmitter.io/app-rejection-guides/google-play-sensitive-permissions-policy/
- **Mapping: ADJUSTS the bundle.** Items (1)+(2) are necessary but not sufficient; #42 must also own: (a) Play Console AccessibilityAPI declaration under the **non-accessibility** path, (b) a screen-recording video demonstrating the shipped disclosure + consent flow exactly as built, (c) the same disclosure language in the Play listing, (d) a privacy policy + Data Safety section that names window-content and URL-bar access. A disclosure/build mismatch is itself a top rejection cause (see 2).
- Cost: disclosure screen ≈ bundle's; declaration + video + listing/policy drafting ≈ 0.5–1 day of non-code work, recurring on material changes.

**2. Frame the use inside Play's current narrow exception: deterministic, user-defined, rule-based automation**
- Source: https://github.com/hereisjayesh-coder/orlune/blob/HEAD/docs/accessibility-service-compliance.md
- Practice: Per the October 2025 policy update (enforcement on/around Jan 28, 2026, as summarised there), *"any use of the Accessibility API that enables an app to autonomously initiate, plan, and execute actions or decisions is strictly prohibited"*, with an exception for *"deterministic, rule-based automation, where behavior follows a static, human-defined script"* — a fixed user-configured rule ("if package X is foregrounded during window Y, show overlay") is written to sit inside it, and the declaration + in-app disclosure must state it identically.
- The Oct-2025 clarification is independently reported here: https://asoworld.com/blog/october-2025-google-play-policy-updates-key-changes-for-child-safety-finance-health-compliance/
- **Mapping: ADJUSTS the bundle's rewrite with the exact argument structure** #42's declaration should use: Monolith does not decide anything — the user writes the rules (apps, sites, session windows) in advance; the service matches foreground/window/URL-bar text against *those* rules and shows the airlock. Declaration, `strings.xml`, and in-app disclosure must use the same sentences.
- Cost: wording discipline only — but it constrains future features: any future autonomous/adaptive blocking would fall outside the exception.

**3. Prominent-disclosure mechanics are judged literally**
- Source: https://support.google.com/googleplay/android-developer/thread/457911309?hl=en&msgid=457920937
- Practice (Google's rejection wording, quoted there): the disclosure *"must present the consent dialog clearly and unambiguously before requesting the permission; must require affirmative user action (e.g., tap to accept, tick a check-box); must not interpret navigation away from the disclosure (including tapping away or pressing the back or home button) as consent."*
- **Mapping: ADOPTS bundle item (2) and specifies its shape:** a dedicated pre-enable screen, shown *before* the intent to Accessibility Settings, Accept/Decline buttons, back = decline, no pre-ticked anything, and the full read list in plain words (window text for Shorts/Reels + tamper screens; address-bar text in the 5 named browsers for the user's site list; nothing leaves the device).
- Cost: included in (1); test it as a flow, not a string.

**4. Expect — and pre-answer — "why not UsageStatsManager?"**
- Source: https://dev.to/lui61140/why-i-removed-accessibilityservice-from-my-android-app-and-finally-got-into-play-store-4i52
- Practice: A blocker author who audited honestly found his AccessibilityService duplicated UsageStats work, removed it, and kept only a *"700 ms of detection latency"* difference — after repeated *"Missing prominent disclosure"* rejections. Reviewers ask what Accessibility does that Usage Access cannot.
- **Mapping: ADJUSTS the bundle — adds a required artefact, not a code change.** #42's declaration needs the honest necessity paragraph Monolith *can* truthfully give (and the dev.to author's app could not): UsageStats reports *which app* is foreground; it cannot see an in-app Shorts/Reels surface, cannot read a browser address bar to enforce a per-site list, and cannot detect a Settings tamper screen. Those three are Monolith's actual readers (`MonolithAccessibilityService.kt:319, 382-414`).
- **DECISION-NEEDED (explicitly not recommended silently):** the alternative cure — delete Accessibility, keep UsageStats-only blocking — is a real option some blockers take, but for Monolith it deletes Shorts/Reels blocking, site-list blocking, and tamper detection. That is a core-product trade, Sarthak's call only.
- Cost: one paragraph in the declaration; zero code.

**5. How shipping blockers phrase it (pattern, not a template to copy blindly)**
- Pattern from live listings in the sweep: blockers that survive state the mechanism in the listing itself — e.g. *"On first launch, allow the Accessibility Service when asked. [It] uses it to notice when a blocked app opens"* — https://applass.com/blog/best-app-to-block-social-media-on-android-in-2026 — and pair it with an explicit local-only line (*"100% on-device… Nothing leaves your phone"*, Lucid pattern in the same sweep results).
- **Mapping: ADOPTS the bundle's "honest copy is better copy" direction** (bundle seat 13 said the same): Monolith's compliant description should read approximately — *"Monolith uses an Accessibility Service during a focus session you start, to: detect when an app you chose to block opens; read on-screen text only to recognise Shorts/Reels feeds and Monolith tamper screens in Settings; and read the address-bar text in Chrome, Brave, Firefox, Edge and Samsung Internet only to check it against the site list you wrote. Nothing is sent off this device."* That sentence, in `strings.xml`, in-app, and in the listing — identically.
- Cost: copy only.

---

# F4 — Device Admin as uninstall guard + irrelevant `<limit-password/>` + copy-pasted description [HIGH → #44]

Bundle spec (adopted as floor): delete `<limit-password/>`, give the receiver its own truthful description (fix `AndroidManifest.xml:123`), strike "Kernel-Level Uninstall Guard", make `onDisableRequested` session-aware or drop the session wording, record the Play policy exposure as a named accepted risk *or* remove the grant.

### Ranked practices

**1. Device Admin is a deprecated legacy API — there is no modernisation path for a consumer app**
- Source: https://www.trio.so/blog/device-admin-android
- Practice: *"Google marked device admin as deprecated for enterprise use from Android 9. From Android 10, four policies (disable camera, disable keyguard features, password expiry and password limits) throw a security exception for apps that target API level 29. In Android 11 the reset password policy stopped working."* Google's replacements are ownership models a consumer app cannot claim — Device Owner (company-owned) and Work Profile (BYOD).
- Directly on point for Monolith: the one policy it declares, `<limit-password/>`, sits in exactly the password-policy family the platform has been disabling — it is not merely unused, it is a dead class of policy.
- **Mapping: ADJUSTS the bundle toward its stronger branch.** The bundle left "keep the grant as a named, accepted risk" open. Scout's ranking: **remove the receiver, the policy file, and the grant entirely.** A warning string (`onDisableRequested`'s only real power) does not justify holding a deprecated, policy-exposed privilege whose consent screen currently shows the *wrong service's* description. Keeping it — even honestly worded — keeps a Play-review liability for a sentence of friction text.
- Cost: deletion is *negative* code (≈ −40 lines + manifest entries); the real cost is (3) below.

**2. Play policy independently forbids the surrounding pattern for non-parental, non-enterprise apps**
- Source: https://support.google.com/googleplay/android-developer/answer/16909972
- Practice: Accessibility (and by extension elevated privileges generally) cannot be used to *"prevent the ability for users to disable or uninstall any app or service unless authorized by a parent or guardian through a parental control app or by authorized administrators through enterprise management software."* Monolith is neither — so "uninstall guard" is not a use case Play recognises for it, in any wording.
- **Mapping: ADOPTS bundle item (4) and sharpens it** — the exposure is not "device-admin policy" in the abstract; it is that the app's stated purpose for the grant (friction against the owner uninstalling/disabling protections) is outside the permitted categories. That sentence belongs verbatim in #44's risk record if any part of the grant survives.
- Cost: none — it is the reason for (1).

**3. The honest replacement for uninstall friction: ritual + detection, labelled as friction — DECISION-NEEDED**
- No elite source offers a consumer-API replacement that *prevents* uninstall, because none exists: outside Device Owner / parental-control / enterprise enrolment, Android deliberately reserves uninstall and deactivation to the user. What shipping self-control apps use instead is in-app friction of the kind Monolith already owns elsewhere — a continuous-hold / cooldown ritual before protection can be stood down (Monolith's own lock law already specifies exactly one emergency break per week via a continuous 60-second hold), plus *detection*: on next launch / via UsageStats, notice that protections were disabled mid-session and record/show that truthfully, rather than pretending it was prevented.
- **Mapping: REPLACES the bundle's item (3) end-state.** Instead of a session-aware `onDisableRequested` string, the cure is: (a) admin gone, (b) deactivation/uninstall of Monolith's *other* protections (Accessibility off, app uninstalled and reinstalled, data cleared) is treated as a detectable, disclosed event in the app's own record — never as a blocked one, (c) all copy says "Monolith cannot stop you uninstalling it. It makes pausing a deliberate act, and it keeps an honest record."
- **DECISION-NEEDED — flagged, not hidden:** this *is weaker protection* than even the current theatrical version feels like. The current version's real power is only a warning dialog, so the *actual* loss is small — but the perceived loss is real, and Sarthak must choose: honest friction (Play-safe, deprecated-API-free) vs. keeping a deprecated grant for one warning sentence (Play-exposed). Scout recommendation: honest friction.
- Cost: detection/logging ≈ 20–30 lines reusing existing WatchStore/session machinery; ritual reuses the existing 60-second-hold component.

---

# F5 — Exported MainActivity blindly trusts intercept extras [MEDIUM — HOLDS, Prisma cures]

Bundle spec: Tier 1 revalidation in `triggerAirlockIntercept` (active session + ruled, non-OPEN app + badge derived from the row) and Tier 2 single-use nonce via app-private prefs (30 s, consume-on-read, nonce-gates peek `path`). Tier 1 is adopted unchanged. Tier 2 is where the scout changes the cure.

### Ranked practices

**1. Split the surface: exported launcher stays, intercept moves to a non-exported activity — REPLACES Tier 2**
- Source: https://docs.ostorlab.co/kb/INTENT_SPOOFING/index.html
- Practice: *"Avoid exporting components unless the component is specifically designed to handle requests from untrusted applications"*; if a component must be exported, *"dynamically check the caller's identity"* and *"require Signature or SignatureOrSystem permissions to limit [its] exposure to a set of trusted applications."*
- Same fix shape in a 2026 vendor patch: set `exported="false"` on internal-only activities, and where export is genuinely required add a signature-level `android:permission` plus runtime caller verification — https://www.cypherbyte.io/blog/cve-2026-20983-samsung-dialer-improper-export-activity/
- **Mapping: REPLACES bundle Tier 2.** Both legitimate senders (AccessibilityService, ShieldService) are *inside the same app* — they never needed an exported door at all. The elite shape is: a new `InterceptActivity` (or equivalent internal entry) with `android:exported="false"`, launched by explicit `ComponentName` intents from the two services, carrying the same four extras; `MainActivity` keeps `MAIN`/`LAUNCHER`, stays exported, and **ignores/deletes its intercept-extra handling entirely**. That is strictly stronger than the nonce (no token to mint, store, expire, replay, or lose to process death), smaller in state, and it also fixes the bundle's noted delivery quirk — cold-start spoof timing and the `onNewIntent` drop (`MainActivity.kt:99-124`) — because the launcher path no longer carries intercepts at all.
- Caveat priced in: same-UID / same-app callers are the only ones who can reach a non-exported activity, which is exactly the trust Monolith needs; a *rooted-owner* adversary is out of scope here, as in F2.
- Cost: ≈ bundle Tier 1 + Tier 2 combined effort, reallocated — one new activity entry + retargeting two call sites (`MonolithAccessibilityService.kt:422-429`, `MonolithShieldService.kt:~183`) + deleting the extras path in `MainActivity` — ~40–60 lines net, 3–4 files, plus tests that a foreign explicit intent is refused by the platform. Risk: low-medium (task/back-stack behaviour of a separate intercept activity must be checked against the current airlock UX — single-task/affinity choices need one design pass).

**2. Revalidate against ground truth, never against the intent — ADOPTS Tier 1**
- Source: https://www.iphoneincanada.ca/2026/10/27/microsoft-authenticator-bug-let-a-blank-android-app-grab-work-mail/
- Practice (CVE-2026-80097, Oct 2026): Microsoft Authenticator's exported `AccountChooserActivity` *"accepted nearly all incoming parameters straight from the calling app"* and *"completely failed to verify whether the actual application making the request matched the identity being claimed"* — an unprivileged app harvested work-account tokens by spoofing extras. Extra-supplied identity is not identity, in production, this year.
- **Mapping: ADOPTS bundle Tier 1 verbatim** as the permanent floor under (1): even for the internal activity, `triggerAirlockIntercept` must require an active session, look the package up in the user's own app rows, refuse absent/`OPEN` rows, and derive the badge from the row — extras may *select*, never *assert*. This also bounds (1)'s residual: a compromised in-app caller still cannot fabricate a truthless airlock.
- Cost: bundle's ~20 lines + 1 gate test. Unchanged.

**3. Do not substitute `getCallingActivity()` / `getCallingPackage()` / action-string checks for (1)+(2)**
- Source: https://wiki.sei.cmu.edu/confluence/plugins/viewsource/viewpagesrc.action?pageId=87150960
- Practice (CERT DRD09-J): validate the caller before acting, but *"it is generally not recommended to use the package name for validating the caller"* — check the developer **certificate** instead; `getCallingActivity()` also only exists for `startActivityForResult`-style flows, which the launcher path is not.
- Stronger still: *"Never trust caller specs as sole authentication factors. CVE-2024-40654 demonstrated that `FLAG_ACTIVITY_FORWARD_RESULT` can spoof `getCallingPackage()` and `getCallingActivity()` returns"*; intent actions are *"public labels, not secrets… Never use an intent action as an authentication mechanism"* — https://copyprogramming.com/howto/java-get-action-caller-specs-android-code-example
- And a live demonstration that caller-supplied package strings were forgeable below API 30 via the binder interface: https://badranh1.medium.com/running-commands-on-a-waymo-riders-account-07440e62008c
- **Mapping: ADJUSTS the menu of options — by removing three tempting wrong ones** so a future implementer does not "simplify" (1) into a calling-package check, an action-string secret, or a signature permission bolted onto the LAUNCHER activity (which would break launching: the launcher is not signed with Monolith's key). Signature-level permissions remain the right tool for exported *services/receivers/providers* — Monolith should audit its other exported components against that rule, but the intercept's correct protection level is non-export, per (1).
- Cost: none — avoided work.

**4. If the split in (1) is rejected, the bundle's nonce is the correct fallback — keep it as Plan B**
- The bundle's Tier 2 (single-use, timestamped ≤30 s, consume-on-read, nonce-gates peek eligibility, Tier 1 as degradation floor) is a sound in-band provenance pattern and is strictly better than any caller-identity check in (3). It is ranked last only because (1) removes the attack surface instead of authenticating traffic across it, and carries no expiry/replay/process-death edges.
- Cost: bundle's ~35 lines, 3 files + tests — retained as the fallback estimate.

---

## Cross-cutting note for PRISMA

Three of the five findings share one root the bundle already named: **wording that claims more than the machinery does** — "hardware" HMAC (F2), "launch-detection only" accessibility (F3), "Kernel-Level Uninstall Guard" (F4), and a plate that says data stays on the phone while notification bodies never expire (F1). Every elite source above converges on the same cure order: first make the claim true or make the wording honest (cheap, immediate), then do the structural fix (DB split, activity split, admin deletion). None of the structural fixes requires a server, a new third-party service, or a Play-incompatible privilege — the two that would (Play Integrity, Device Owner) are recorded above as rejected, with reasons, so they are not re-proposed.

## Sources — all URLs verbatim from search results

F1: tmliu06.github.io (ISSTA 2025) · arxiv.org/html/2603.16735v2 · copyprogramming.com/howto/listening-for-notification-on-android · medium.com SQLCipher measurement (Oct 2026) · commonsware.com/Room SQLCipher chapter · android-developers.googleblog.com Direct Boot post · developer.android.com IsolatedContext reference · securecodewarrior.com Direct Boot guide — full URLs inline above.
F2: mas.owasp.org MASWE-0004 · developer.android.com/privacy-and-security/keystore · developer.android.com KeyInfo reference · attomus.com Keystore attestation limits · developer.android.com Play Integrity standard + classic · support.google.com Play Integrity overview — full URLs inline above.
F3: support.google.com/googleplay/android-developer/answer/16909972 · appsubmitter.io sensitive-permissions guide · github.com/hereisjayesh-coder/orlune accessibility compliance doc · asoworld.com Oct-2025 policy updates · support.google.com prominent-disclosure rejection thread · dev.to AccessibilityService removal post · applass.com blocker comparison — full URLs inline above.
F4: trio.so device-admin guide · support.google.com/googleplay/android-developer/answer/16909972 — full URLs inline above.
F5: docs.ostorlab.co INTENT_SPOOFING KB · cypherbyte.io CVE-2026-20983 · iphoneincanada.ca CVE-2026-80097 · wiki.sei.cmu.edu DRD09-J · copyprogramming.com caller-specs guide · badranh1.medium.com Waymo callingPackage write-up — full URLs inline above.

*Scout seat (security practice), PRISMA Wave-3 cluster C10. Read-only: no base files edited, no commits, no issue edits, no messages to the user.*
