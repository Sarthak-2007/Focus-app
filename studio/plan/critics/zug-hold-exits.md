# Zug — Critics, accessibility — HOLD audit part 1: every exit & escape surface

Plan wave, audit only, no code. Measured against his settled law only:
**the emergency hold is BANKED by his word — 15s checkpoints bank progress, a slip
DRAINS, 60s total, 1 token/week, target ≥88dp.** Standards: HOLD absolute, zero
mid-session decisions · TELL THE TRUTH once.

## The valve, measured (the centre of this audit)

`Emergency60sValveView`, `ui/views/BlockitSuitcaseAndSwitchesViews.kt` L490–606,
driven by `DisarmChallengeOverlay`, `ui/components/AirlockAndDisarmOverlays.kt`
L563–633, and `requestDisarmChallenge` / `confirmDisarmSession`,
`ui/MonolithViewModel.kt` L915–961.

| His law | What the code does | Verdict |
|---|---|---|
| Progress **banked at 15s** checkpoints | No checkpoint state exists anywhere. Progress is `(now − this ACTION_DOWN)` only (L512–533). 44s of holding banks exactly nothing. | **Fails completely** |
| A slip **drains** | `ACTION_UP`/`ACTION_CANCEL` sets `holdProgress = 0f` instantly (L595–603). Not a drain — a snap to zero. The file header even lies about it: "Releasing early immediately *decays* back to 0." It does not decay. | **Fails, and the comment lies** |
| 60s total | `requiredSeconds = 60`, hard-coded in the VM (L937). | Holds |
| Target **≥88dp** | Overlay sizes the valve at **68dp** high, full width (Airlock L618–622). 20dp short of his floor. | **Fails** |
| Tick escalation | Tick every 1s: 4ms latch, 8ms firm click every 5th second — a flat sawtooth that resets every 5s and never escalates with progress. The 15/30/45 checkpoints get the *same* 8ms as second 5 and 10. No checkpoint is feelable, so banking would be invisible even if it existed. | **Fails** |
| Finger stays on target | No `ACTION_MOVE` handling at all: press, slide your finger off the valve onto the table, the timer keeps running. A second finger's `ACTION_DOWN` *restarts* the timer — a stray palm at second 50 zeroes you. | Broken both ways |
| Operable by everyone (accessibility) | Raw Canvas `View`: **zero** `contentDescription`, semantics, focusability or accessibility events in the whole file. TalkBack and Switch Access users cannot find, operate, or hear this valve; the countdown is drawn pixels only, no announcement. For anyone who cannot sustain a precise 60s press (tremor, one hand, grip fatigue) the banked law *is* the accessibility feature — its absence is an accessibility failure first, a law failure second. | **Fails hardest here** |

**Overlay copy vs law** (Airlock L600): *"Hold the physical valve continuously
for 60 seconds to abort."* Three untruths in one sentence: it is not physical
(it is an on-screen canvas), "continuously" directly contradicts his banked
law, and *"You have 1 weekly emergency break token remaining"* claims a weekly
token that — see token row below — does not exist anywhere in storage.

## Full exit & escape table

| Surface | Where (file) | What it costs to exit | Matches his law? | Verdict | Plan fix |
|---|---|---|---|---|---|
| Emergency valve (above) | BlockitSuitcaseAndSwitchesViews.kt L490 · AirlockAndDisarmOverlays.kt L606 | 60s unbroken hold + 1 token (claimed) | No — no banking, snap-zero, 68dp, flat ticks | **Gate behind the banked valve** (rebuild it as his law, don't kill it) | Bank at 15/30/45 into persisted state; release drains at a fixed rate toward the last banked checkpoint, never below it; distinct checkpoint tick (longer, pitched up per checkpoint); height ≥88dp; `ACTION_MOVE` bounds check + ignore secondary pointers; contentDescription + progress announcements at each checkpoint; copy rewritten: banked, not "continuous", not "physical" |
| Token gate in `confirmDisarmSession` | MonolithViewModel.kt L944–949 | Nothing enforced here: if tokens = 0 it *still disarms* — the decrement is conditional, the disarm is not. The only enforcement in the app is the valve view refusing touch events at 0. | No — the law lives in a UI prop, not in the ViewModel/repository | **Kill** (this shape of it) | Token check becomes a hard precondition inside `confirmDisarmSession`/repository: no token, no disarm, full stop. UI refusal is decoration, never the gate. |
| Token counter itself | MonolithViewModel.kt L139–140 | — | No. `MutableStateFlow(1)`, in-memory only. **No persistence, no week, no timestamp, no reset logic anywhere in the codebase.** Force-stop, recents-kill, crash or reboot refills it to 1. Reinstall refills it. Clock changes are irrelevant — nothing ever reads a date. "Weekly" is a string in two labels and nowhere else. | **Kill** | Persist token + week-anchor in Room next to the session record (same HMAC-signed family): spent token survives restart/reinstall-restore; refill only when the anchored week rolls. Record each spend in the session's one truthful line. |
| Grace-string bypass | MonolithViewModel.kt L946 · MainActivity.kt L321–323 | `confirmDisarmSession("Accidental Start Grace Cancel")` skips the token on the strength of a **string**, and the function never checks elapsed time itself — the ≤10s gate exists only in the Deck UI (TactileDeckScreen.kt L463). Any present/future caller with the string gets a free exit at any minute. | No — invariant held by UI + a magic string | **Gate behind the banked valve** (the check moves) | VM verifies verified-elapsed ≤10s itself before honouring grace; reason becomes an enum, not a string match (repository `disarmActiveSession` also string-matches "Grace Cancel", MonolithRepository.kt L1089 — same fix there). |
| GRACE WINDOW ABORT (first 10s) | TactileDeckScreen.kt L463–475 · 58dp chip | One tap, no token, and the record is *erased*: repository zeroes elapsed and writes no log line for grace cancels (MonolithRepository.kt L1089–1110) | Half — the undo is legitimate, the erasure is not | **Justify** the window, **kill** the erasure | Keep it: a mis-arm undone inside 10s is an ARM-time correction, not a mid-session decision (it mirrors ARM ≤10s). But it must leave its one truthful line — "Cancelled in the grace window, Ns in, no token spent" — with real elapsed, not 0. Chip is 58dp ✓ ≥48. |
| Stopwatch: tap-finish after 20m floor | MonolithViewModel.kt L921–931 · MonolithRepository.kt L69, L876–878 | **One tap, no hold, no token** — and the tap lands on a chip still labelled "EMERGENCY BREAK VALVE (60S HOLD) · 1/1 WEEK" (TactileDeckScreen.kt L476–485) | Mode: yes. Surface: no — the label promises a valve and a cost; the tap delivers neither, and the record says `completed = true, "Stopwatch Completed"` for what was a choice to stop | **Justify** the mode, **kill** the lying surface | Stopwatch is chosen by the rested user at arm time (dial <10m), so a free exit after its floor is his decision executed, not a mid-session decision. After the floor the chip must rename itself "FINISH STOPWATCH" and the record must read "Ended by choice after the 20-minute floor", not a generic completion. Below the floor it correctly routes to the valve — keep that. |
| Natural completion | MonolithRepository.kt L1017–1045 | Serving the full duration | Yes — the only exit that fully does | **Justify** | None. (Note: stopwatch sessions never auto-complete — by design, open-ended.) |
| Safe Break (10m between blocks) | MonolithRepository.kt L971–1014 · AccessibilityService L102–105 · ShieldService L113 | Automatic, not chosen mid-session: all PEEK-tier apps open *freely* for 10 min; LOCKED apps, Shorts/Reels and blocked sites stay shut | Yes in kind (decided at arm time by block count) — one timing lie: break countdown decrements 1 per tick *call*, and both the VM ticker (1s) and Shield loop (2s) call it, so a "10-minute" break can end early | **Justify** | Drive the break from verified elapsed like the main timer, not tick counts, so 10 minutes means 10 minutes. |
| Airlock 2-min PEEK | AirlockAndDisarmOverlays.kt L545–555 · MonolithViewModel.kt L996–1031 · Repository L745–768 | Wait out a 5s countdown, tap PEEK: 2 min in one PEEK-tier app, 20-min cooldown (DB-anchored, monotonic — survives everything except reboot, where it fails *safe*, re-cooling up to the full 20m) | Mostly — it's an arm-time tier decision, not a session exit (session stays sealed). Two nits: named "EMERGENCY PEEK" though it costs no token and is no emergency; unknown packages (`appEntity == null`, VM L973) are offered a peek by default | **Justify** | Rename to "PEEK · 2M" so emergency means the valve and only the valve; default unknown packages to no-peek. Chips here are 42dp — see targets row. |
| Phone/VoIP call bypass | MonolithAccessibilityService.kt L54–56, L128–138 | Nothing — while `AudioManager` reports ring/in-call/in-communication, **all** blocking pauses for **every** package, not just the call app | Justified in purpose (a call must never be blocked — his sick-day door), over-broad in scope | **Justify**, narrow it | Scope the bypass to telephony + the call's own package; other blocked apps stay blocked during a call. |
| **Smart Loadout applied mid-session** | ShieldMatrixScreen (onApplySmartLoadout) → MonolithViewModel.kt L810–820 → MonolithRepository.kt L681–725 | **One tap.** Rewrites *every* app's tier from the loadout template: WORK_SPRINT sets previously LOCKED social apps to PEEK and everything else OPEN. Session stays "active" on paper while its shield is dismantled. The per-app ratchet (below) is never consulted. | **No — this is the widest hole in HOLD.** A mid-session decision that unmakes the seal, at zero cost, in one tap | **Kill** (during a session) | Loadout/profile application joins the ratchet family: while a session is active, any change that would loosen any tier is refused with the existing ratchet warning; tightening-only applies. Pre-session it stays one tap. |
| **Profile applied mid-session** | MonolithViewModel.kt L842–849 → MonolithRepository.kt L798–815 | **One tap.** `applyProfile` sets every app whose category is outside the profile to OPEN — same wholesale loosening as loadouts, same absence of any session check | No — same hole, second door | **Kill** (during a session) | Same ratchet gate as loadouts: one shared "would this loosen anything during an active session?" check at the repository boundary, so no future caller can reopen the door. |
| Browser shield OFF + site removal mid-session | Drawer + SystemGuardScreen → MonolithViewModel.kt L412–419, L468–473 · AccessibilityService L91–92 reads both live | One tap on the browser toggle (persisted, enforced within one accessibility event) or one tap on a site's REMOVE chip (42dp) unblocks that site instantly, mid-session, no ratchet, no record | No — in-session weakening, free and silent. (Guard's overall fate is his open pick; these switches are the live edge of that question) | **Gate behind the banked valve** | While a session is active, turning the browser shield off or removing a site requires the banked valve (or is simply refused ratchet-style — my recommendation: refuse, like tiers; the valve is for ending sessions, not editing them). Adding sites / turning it on stays free. Shorts toggle note below. |
| Shorts & Reels sniper toggle | MonolithViewModel.kt L96–97, L407–410 | One tap — and **nothing happens**: the flag is in-memory only and *no enforcement code reads it*; the AccessibilityService snipes unconditionally during a session (L74–86) | Inverse lie: not an escape, a fake control. Off still blocks; the user is told a decision exists that doesn't | **Kill** (the toggle as-is) | Either wire it (ratcheted, tightening-only mid-session) or remove the switch. A control that lies fails TELL THE TRUTH whichever way it points. |
| Per-app tier / daily-limit / schedule ratchet | MonolithViewModel.kt L755–860 | Tightening free; loosening refused with a plain-language warning during an active session | **Yes — the one place HOLD is actually implemented as law** | **Justify** — this is the model | Extend this exact pattern to loadouts, profiles, browser shield and sites (above) instead of inventing a second mechanism. |
| Monolith's own Settings launchers → kill-the-guard chain | MainActivity.kt L438–470 (Guard), L536–545 (pre-flight primer) · SystemGuardScreen | Monolith itself offers buttons into Accessibility Settings *during* a session. That settings page names the service but contains none of the tamper words the blocker listens for (see next row), so the accessibility service can be switched off unopposed — after which Settings, force-stop and uninstall are all unguarded except the device-admin nag | No — the app hands the user the door and its own guard doesn't watch it | **Gate behind the banked valve** | During an active session the Guard screen's settings launchers are inert (Guard fate itself stays his open pick — this is only about *in-session* reachability). |
| Settings / package-installer tamper blocker | MonolithAccessibilityService.kt L140–161 | Free to attempt; intercepted *only if* the accessibility service is alive **and** the Settings window's visible text (depth ≤14, English) contains "monolith" plus one of force stop / deactivate / uninstall / clear storage / clear data / device admin | Partially — real but brittle: localisation, OEM wording, or killing accessibility first (above) defeats it silently, and **nothing records the attempt** as a session event | **Justify** as a layer, never as the wall | Keep, plus: on next launch, if a session is active in DB but accessibility is off, say so once in plain words on the Deck ("The guard was switched off at HH:MM — the session was open, not held") instead of silently resuming as if nothing happened. |
| Force-stop / recents swipe-kill | (no `onTaskRemoved` anywhere) · ShieldService is START_STICKY foreground | Free, instant. Enforcement dies; the session row stays `isActive` in Room, **no record is written** of the gap. On relaunch the ticker quietly resumes the session as if it had been held throughout | No — the silent resume is the lie: an unheld interval gets written into history as held time (elapsed is monotonic-derived across the gap) | **Gate behind the banked valve** (can't be prevented — must be *confessed*) | Truth fix, not a lock fix (Android cannot stop force-stop): stamp "enforcement interrupted" with the last tick time when the service dies mid-session; on resume, the record shows held time and gap separately. Token persistence (above) removes the current bonus prize for force-stopping. |
| Power off / reboot | MonolithBootReceiver.kt · Repository L586–600, L933–946 · ShieldService restarts on boot | Free (hold the power button). Session survives in Room; boot receiver restarts the shield before unlock; elapsed resumes from accumulated + since-boot; tamper-checked | Closest to honest of the out-of-app exits — the seal genuinely re-forms. Residual lie: time powered-off counts via the since-boot reconstruction only from boot, but the *off* interval itself is invisible in the record | **Justify** | Record the reboot as a line in the session's story ("Phone restarted; session resumed") — the data (boot-count change) is already detected, just never told. |
| Clock change | Repository L583–593 | Free, and useless to the cheater: elapsed is monotonic-derived; wall/mono drift >90s flags tamper and adds a 15-min penalty lock (L930, +900s) | Yes — one of the few places the code distrusts the user correctly | **Justify** | None. (Clock gaming the *token*, by contrast, is moot — the token has no clock to game.) |
| Uninstall / clear data / reinstall | Manifest `allowBackup=true`, empty backup rules · MonolithDeviceAdminReceiver.kt (warning text only — Android lets the user deactivate anyway; if admin was never enabled, nothing at all) | Free once accessibility is off (chain above). Everything dies: session, history, record. Reinstall is total amnesty | Platform limit, not a code lie — but the "Kernel-Level Uninstall Guard" comment overclaims: it is a nag, not a guard | **Justify** (as a limit), **kill** the overclaim | Rename the claim in copy/comments to what it is. Local-only storage cannot survive uninstall; say that plainly in Guard instead of implying a kernel guard exists. |
| Back / Home / Recents navigation | MainActivity.kt L201–206 | Free to leave the app — and correct: Back only navigates to the Deck, Home leaves a session that stays enforced by accessibility + usage polling. Neither ends or weakens the session | Yes | **Justify** | None. |
| Routine window END disarms a routine session | RoutineAlarmReceiver.kt L54–66 | Automatic at the window's end; only ever disarms a session *that routine started* ("Routine: <name>"), never the user's own | Yes — routines are his, and a routine ending its own session is the rested user's earlier decision executing | **Justify** | None. (Editing routines mid-session is also clean: it reschedules future boundaries and never touches the live session.) |
| Desk-clock overlay, notification digest | AirlockAndDisarmOverlays.kt L68ff · MainActivity L545–553 | Tap to open/dismiss *inside* Monolith; digest items aren't tappable out to their apps; neither touches the session | Yes | **Justify** | None — checked, clean. |

## Touch targets on exit surfaces (§f)

His floor for the valve is ≥88dp; the generic accessible floor is ≥48dp.

| Target | Size in code | Passes |
|---|---|---|
| Emergency valve | 68dp high × full width | ✗ his 88dp law · ✓ 48dp |
| Deck chip opening the valve / Grace Abort chip | 58dp high (`height(58.dp)` at call site) | ✓ 48dp |
| KEEP FOCUSING (the safe choice in the disarm overlay) | **42dp** — `BlockitActionChip` hard-codes `height(42.dp)` (MonolithCustomComponents.kt L167) and this call site passes no height | ✗ 48dp — the *stay* button is the smallest target on the exit screen |
| Airlock RETURN TO FOCUS / EMERGENCY PEEK | 42dp (same component default) | ✗ 48dp |
| Guard site REMOVE / ADD chips | 42dp (same default) | ✗ 48dp |
| Desk-clock dismiss | full screen | ✓ |

Plan fix, one sweep: `BlockitActionChip` default height goes to ≥48dp everywhere
(it is the shared component — one change heals every chip), the valve goes to
≥88dp as its own law requires, and no exit-surface call site may pass a height
below the component floor.

## Worst finding

**The weekly emergency token does not exist — it is an in-memory counter that refills to 1 on every app restart, force-stop or reboot, the disarm function itself doesn't even enforce it, and the one real exit it supposedly rations can meanwhile be bypassed in a single tap by applying a Smart Loadout or profile mid-session, which rewrites every app's tier while the session record goes on claiming the seal held.**

— Zug, Critics (accessibility), 05:20 IST

## Addendum 05:2x IST — Hale's safe-break note, measured

Hale (mail, thread safe-break-exit) flags the Safe Break for this table and
carries its record half as a TRUTH exhibit. The break is tabled above
(**Justify** — designed at arm time by the rested self; nobody chooses or
refuses it mid-session, which is precisely why it is not a mid-session
decision). One measurement distinction on his record claim: break minutes do
**not** inflate `elapsedSeconds` — the counter freezes at the finished block's
target through the break and resets for the next block (Repository L971–1001).
The truth failure is absence, not inflation: the finished session's record
carries no break line at all, so nothing anywhere says that a multi-block
session held 10 open-PEEK minutes per boundary. Fix stands with the row
above, plus: the session's one truthful line names its breaks ("3 blocks,
2 breaks, PEEK apps open in breaks").

— Zug
