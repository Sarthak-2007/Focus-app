# Issue #26 — manifest-permissions · Wave-3 Cluster C1 (GRUND)

Verified against origin/main @ e29acdf by the 9-seat bundle (Designer, Consumer,
Student, User, UX, Enthusiastic designer, Wow-level designer, 29-years designer,
Intentionality designer, Interaction-detail) + lead application. All 5 findings
dispositioned; cures applied in this branch.

| Finding | Verdict | Cure landed |
|---|---|---|
| 26-1 Device-admin consent shows accessibility description (HIGH) | HOLDS — manifest:123 pointed the uninstall-guard receiver at @string/accessibility_service_description | New `device_admin_description` string (uninstall/session wording); manifest repointed. Accessibility string untouched for its real user (accessibility_service_config.xml:7). |
| 26-2 Accessibility reads every window; disclosure claims only launch detection (HIGH) | HOLDS — capability real AND used (rootInActiveWindow, view-id scraping, collectWindowTextLowercase for Shorts/Reels sniper, blocked-URL blocker, Settings tamper check); the lie was the disclosure. Removing capability or adding packageNames would kill the blocker. | `accessibility_service_description` rewritten truthfully: foreground-window content, active session only, on-device, names all four uses. No capability removed. |
| 26-3 limit-password declared, never exercised (MEDIUM) | HOLDS — sole DevicePolicyManager use is isAdminActive (MonolithRepository.kt:708-711) | `<limit-password/>` + empty `<uses-policies>` removed from device_admin_receiver.xml, explanatory comment left. Guard behaviour unchanged (works from active-admin status). |
| 26-4 Battery-exemption prompt fired without declaring the permission (MEDIUM) | HOLDS — MainActivity.kt:394 fires ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS; manifest declared no such permission | `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` declared with an explanatory comment. |
| 26-5 MainActivity directBootAware for no stated reason (LOW) | PARTIALLY — declaration held; "no reason anywhere" overstated (direct-boot design is documented for DB/services/receivers: MonolithDatabase device-protected storage, LOCKED_BOOT_COMPLETED). Activity-specific rationale missing. | `directBootAware` removed from MainActivity ONLY; kept on services/receivers so the shield still enforces pre-unlock; launcher UI now waits for first unlock. |

Positive verifications (not "fixed", confirmed sound): allowBackup=false + full
backup/data-extraction excludes; every declared permission is used; no
SCHEDULE_EXACT_ALARM added (routines use setAndAllowWhileIdle only — adding it
would be the same overreach disease as 26-3); no INTERNET, no QUERY_ALL_PACKAGES.

Seat one-liners (full sections in the bundle report to Ren):
Designer — one source of truth; Consumer — consent words are where trust is spent;
Student — 26-4 is whether the block survives 2 a.m.; User — prompts must explain
themselves; UX — wrong text on the scariest step kills completion;
Enthusiastic designer — the machinery is honest, now its words are;
Wow-level designer — a blocker that misdescribes its power reads as malware;
29-years designer — declarations fossilise unless audited; Intentionality —
align declaration with deed in both directions (26-2 + 26-3 are one disease);
Interaction-detail — micro-copy is behaviour.

— GRUND, Wave-3 C1 lead
