# FEEL — THE SOUND SCORE OF THE RECEPTION SHEET

Seat: **Klang (feel specialist, seat 3)**, Pforte's cell, GitHub
issue #14, `Sarthak-2007/Focus-app`, branch `agent/issue-14`.
Scope: SOUND ONLY — every voice the sheet has, and every silence.
Paper work only; nobody compiled, nobody ran tests.

Ground, read whole: `ReceptionSheet.kt` (every `soundEngine`
call site — there are exactly three: ll. 296, 563, 680),
`service/TactileSoundEngine.kt` (the voices themselves),
`MainActivity.kt` host calls (Reception host ll. 816–835, BACK
l. 379, `openFixPane`, `stampSetupAndEnter`),
`studio/plan/AUDIT-setup-sheet.md` §3 — Letzt A-5 binds the
audio design: **no failure voice; the firm click stays as the
second channel at the return verdict; the tear takes
`playTearPulse` in place of the firm click at DONE.**
Cross-checked against the house precedent: `ParkDrawer.kt`
(the pattern the sheet copies whole) and the counterfoil tear
on Tasks (`FocusSequencerScreen.kt:153`, same pulse, same
180/260 ms phases).

**Night rule for this whole score:** the sheet's canonical
hour is the 04:00 first open. Every voice below is judged at
its night rendering. Only one voice on the sheet knows night
exists — see the inventory.

---

## THE VOICES (engine inventory)

Every engine voice is a **twin**: a haptic waveform + a float-PCM
acoustic transient fired from the same call. The gates split the
twin — `soundEnabled` gates only the acoustic half
(`enqueueTransient`, TactileSoundEngine.kt:607), `hapticsEnabled`
only the waveform (:557). PARK's SOUND OFF therefore never fully
silences this sheet; it loses timbre, never the event (the seal
lane's S0 law). "Voice" below always means the pair.

| Voice | Engine | Haptic (waveform ms @ amp/255) | Acoustic | Night rendering |
|---|---|---|---|---|
| **Seat knock** (floor values) | `playSeatKnock(10, amp, 300f)` :665 | `[0, 10, 5]` @ `[0, amp, amp×0.35]` | 300 Hz, 16 ms, chord 0.9, gain `0.22 + 0.3×amp` | **The sheet's only night-aware voice** (call site passes `isNight`): amp 0.06 → haptic 15/5, gain 0.238 — vs day amp 0.1 → haptic 25/8, gain 0.25. Note: the night drop is almost entirely *felt* (−40% haptic) and barely *heard* (−5% acoustic). The night law is haptic-led. |
| **Firm click** | `playFirmClick()` :644 | `[0, 13, 20, 8]` @ `[0, 225, 0, 135]` — 41 ms, hard strike, breath, softer seat | 620 Hz, 12 ms, gain 0.5, chord 1.1 | **None.** Zero parameters; fires at full day weight at 04:00. On this sheet that is deliberate where it is a *verdict* (row 4), and indefensible where it is not (row 9). |
| **Tear pulse** | `playTearPulse()` :817 | `[0, 8, 14, 8]` @ `[0, 160, 0, 110]` — 30 ms rip: strike, gap, seat | 240 Hz, 40 ms, **gain 0.68**, chord 1.21 | **None** — see Charge K-4. |

**`playTearPulse` EXISTS — no existence follow-up is owed.**
It is the counterfoil tear's own voice (engine comment, F-N2):
*"the tear is not a completion and must not spend the completion
pulse"* — same body family as `playCompletionPulse` (:833) but
chord 1.21 against the pulse's 1.33, a shorter ripping haptic
against the pulse's three softening ticks, so it reads as paper
and can never be mistaken for the finish. **Relation to the
tear's 180/260 ms phases:** the pulse is an *onset strike* — all
its energy is spent by ~40 ms, inside the first quarter of the
180 ms specA drop/fade and the first sixth of the 260 ms specB
dash-halving. The remaining ~140/220 ms of tear motion is
silent paper separating. That is correct and is the shipped
house precedent (Tasks: the identical pulse over the identical
180/260 ghost tear, `FocusSequencerScreen.kt:153`). **Do not
stretch the voice to 260 ms** — a 260 ms synth rip at gain 0.68
would be the longest, joint-loudest sound in the app and would
read as an error buzz. Price of the voice itself: **0 — it
exists, shipped, already in use.**

---

## THE SCORE — 11 events

| # | Event | Voice today (evidence) | Voice as cured | Silence / truth ruling |
|---|---|---|---|---|
| 1 | **Rise** (sheet appears, 280 ms SEAT) | **Silence.** `LaunchedEffect(visible)` ReceptionSheet.kt:255–257 runs only `onShown()` (the sweep) + `animateTo(0f, tween(DRAWER_OPEN_MS=280, EasingSeat))`. No sound call exists in the block. | **Silence — unchanged.** | **LAWFUL silence.** No hand has touched anything yet; an unsolicited voice at a 04:00 first open is an alarm, not a welcome. PARK rises silent too. The document arrives; it does not announce itself. |
| 2 | **FIX press** (per MISSING door) | **Seat knock at its floor** — ReceptionSheet.kt:680–684, on DOWN, before `onFix()`: `playSeatKnock(durationMs=10, amplitude01 = if (isNight) 0.06f else 0.1f, pitchHz=300f)`, plus the 1 dp dip held for the whole absence. Night render: haptic `[0,10,5] @ [0,15,5]`, acoustic 300 Hz / 16 ms / gain 0.238. | **Unchanged.** | Honest. The knock performs the *press and the departure* — a pawl seating — never the repair, which has not happened. Acquitted in the audit (§8: seat-knock values). This is the quietest voice in the engine and the right weight for a key he may press four times in a row. |
| 3 | **Excursion departure** (Android's Settings pane opens; the app backgrounds) | **Silence.** `openFixPane` (MainActivity) fires the intent with no sound; the knock at the press (row 2) is the only voice spent. | **Silence — unchanged.** | **LAWFUL silence.** The departure is already voiced at its cause (the press). A second voice at intent-launch would double-speak, and it would sound on Android's surface, not ours. The gap between knock and pane is Android's latency, not a missing voice. (The Shield excursion's departure is row 8 — its press is *not* yet voiced, which is the difference.) |
| 4 | **Return verdict — REPAIRED** (door reads GRANTED on return) | **Firm click** — ReceptionSheet.kt:296, the sheet's only judged voice: `LaunchedEffect(returnTick)` waits a fixed `delay(800)` (:289), reads the LIVE store, fires `playFirmClick()` only if `permPrintAt(...) == GRANTED`. Full night weight: haptic 225/135, acoustic gain 0.5. | **Firm click STAYS** — A-5 binds it as the second channel beside the Return Chit's new print line. Two conditions ride with it: **(a)** the click must inherit the chit's freshness gate (verdict only on a measurement with `measuredMono > departureMono`, audit §3 Proposal 1) so sound and print can never judge different instants; **(b)** the verdict must survive process death with the chit (SetupStore), not die with the `remember` values. | Honest today, but **raced and mortal** (audit F-1 stands). Precision on the race: as coded it cannot *false-click* — the departed door's last pre-departure measurement printed MISSING, so no stale GRANTED for that item can predate the departure. Its failure mode is the opposite: a sweep landing after the fixed 800 ms pays a repaired door **nothing** — a false silence, cured by (a). The unscaled night weight is acquitted here: a verdict must land even at 04:00; it is the loudest *lawful* voice on the sheet. |
| 5 | **Return verdict — NOT REPAIRED** (door still MISSING on return) | **Silence.** ReceptionSheet.kt:294–297: `repaired == false` → no call; the dip simply releases. | **Silence — unchanged.** The Return Chit adds *print* ("WENT OUT NOT SET · CAME BACK …"), never sound. | **LAWFUL silence — the sheet's binding one (A-5: no failure voice).** Any buzz, thud or descending tone here would punish a man at 04:00 for what Android's pane did or didn't do. The absence of the click *is* the verdict's second half; he already knows how to hear it because row 4 taught him the click. |
| 6 | **Return verdict — UNREAD** (return measurement UNMEASURED / STALE / no snapshot) | **Silence** — by collapse, not by branch: ReceptionSheet.kt:294–297 has no unread case; anything `!= GRANTED` falls into the same no-call path as not-repaired. | **Silence — unchanged in sound.** The chit's `RETURNED-UNREAD` state separates this case **in print only**. | **Silence lawful; the conflation is not mine to cure.** Unread is neither success nor failure, so the only honest voice is none — the sound score acquits the silence. But sound is the verdict's *only* channel today, so today unread and unrepaired are indistinguishable to the ear *and* the eye; that charge belongs to the print seat and is already filed as audit F-1 / the Return Chit. Sound must not "fix" it with a third voice — a maybe-tone is a lie in both directions. |
| 7 | **CHECK press** (per STALE or UNMEASURED door) | **Silence.** ReceptionDoorCard's CHECK branch, ReceptionSheet.kt:714–715: `onPress { onCheck(); tryAwaitRelease() }` — no sound call, no dip. A 48 dp full-width key swallows the touch mute while its FIX twin speaks. | **Seat knock at FIX's exact floor values** — `playSeatKnock(10, if (isNight) 0.06f else 0.1f, 300f)`, **voice only, no 1 dp dip** (the key's dress is surfaceVariant, not ink; the dip is FIX's). | **MISSING silence — Charge K-2.** The cure is already ruled: audit §3, Letzt F-4+F-5 micro-pass ships *"CHECK takes FIX's knock values (voice only, no dip)"*. The knock performs "sweep requested" and nothing more. **No verdict voice may ever attach to CHECK's result**: when the sweep lands, the door word reprints (SET / UNPROVED / NOT CHECKED) in silence — a re-proof by sweep is not a repair he performed on an excursion, and row 4's click must stay unambiguous. |
| 8 | **OPEN THE LIST** (Shield excursion press) | **Silence.** ReceptionSheet.kt:492–494: `onPress { onOpenShield(); tryAwaitRelease() }` — no sound call. Host (MainActivity:830–833) closes the sheet unstamped and navigates to Shield. | **Seat knock at the floor values** (night 0.06 / day 0.1, 300 Hz), voice only, no dip — mirroring row 7's cure. **Klang's proposal, priced FREE** (one call site, identical arguments to :680). | **MISSING silence — Charge K-3 (Klang's, not the audit's).** Audit H-2 gives the Shield excursion *the same departure law as FIX*; FIX's departure speaks (row 2) and this one, the identical act — leaving the sheet mid-errand for another surface, with a promised return — speaks nothing. Two departures, one voice short. The knock performs departure only; arrival on Shield is Shield's own surface and stays silent here. If Pforte rules this navigation-not-excursion, the fallback is lawful silence — but the parity argument should be answered, not ignored. |
| 9 | **DONE stamp** (first-open press; the press actually stamps) | **Firm click — THE LIE. Charge K-1.** ReceptionSheet.kt:563–564: `onPress { soundEngine?.playFirmClick(); onDone(); tryAwaitRelease() }`. | **`playTearPulse()` REPLACES the firm click** (audit §3, Letzt F-1 ships), in the house G-01 order: **stamp books first** (`SetupStore.markCompleted()` commit returns inside `stampSetupAndEnter`), **then** the tear voice, then the tear motion — block below the rule drops 10 dp + fades over **180 specA**, dashes halve over **260 specB**, everything above the rule motionless (the stub staying), then the **160 THROW**. Exit total **420 ms**. | See K-1 below. The cured voice is honest because of its *order* as much as its identity: the tear sounds over an already-booked stamp, exactly as the Tasks tear sounds over an already-booked completion (G-01). The tear performs the sheet separating — an act, not a success — so it stays truthful at GUARD 0/4, where DONE is lawful and any success voice would not be. |
| 10 | **DONE non-stamp** (PARK reprint; setup already stamped — press tears nothing, stamps nothing) | **Firm click** — the same unconditional call at ReceptionSheet.kt:563; the DONE handler cannot distinguish this press from row 9's. | **Firm click STAYS, no tear, no 420 exit** — plain press + the ordinary 160 THROW leave. Audit §3 binds it: *"Tear only when the press actually stamps; PARK reprints keep today's behaviour."* The builder branches on the stamp fact (host's `setupStamped` / `SetupStore.isCompleted()` read *before* the press), which is also what `isFirstOpen` already carries. | Honest as cured, accidental today. As a pure press voice on a reprint the click claims nothing — but today it is *the same unjudged call* that lies in row 9; one call site is serving two facts, which is how the lie got in. Split the site, keep both voices. Edge case verified: leave via BACK unstamped → re-open from PARK → `setupStamped` still false → the press *does* stamp → tear. Correct. |
| 11 | **Leave** (BACK, unstamped; sheet falls 160 ms THROW) | **Silence.** MainActivity:379 `BackHandler { receptionOpen = false }`; ReceptionSheet.kt:259 `animateTo(1f, tween(DRAWER_CLOSE_MS=160, EasingThrow))`. No sound call on either side. | **Silence — unchanged.** | **LAWFUL silence.** The sheet prints its promise — "LEAVE WHEN YOU LIKE" (cured at S-11 to name the header's continuing watch) — and a leave voice would price the exit it promises is free: any tone here reads as dismissal or penalty. PARK's dismissal is silent on every path. Leaving stamps nothing, proves nothing, and sounds like exactly that. |

---

## CHARGES

**K-1 · THE LYING VOICE — DONE's firm click (merge-blocking for
the tear item).** One voice, two spendings: at
ReceptionSheet.kt:296 the firm click is a *judged verdict* —
fired only after a live-store reading proves a door GRANTED.
At :563 the identical voice fires **unjudged, on DOWN, before
`onDone()` (:564) has even run** — before `SetupStore`'s commit
exists — at *any* door state, including GUARD 0/4. A listener
who learned the voice at the return verdict (its only other
use on this sheet, and PARK's) hears *proved success*; the
machine has proved nothing and, at the instant of sounding,
has not yet stamped anything either. That is a voice performing
success the machine hasn't proved. Cure is row 9's: tear
voice, stamp-first order, stamp/non-stamp split (row 10).

**K-2 · CHECK's missing voice (audit-ruled).** Row 7. Cure
ships in the audit's own micro-pass (F-4/F-5): floor seat
knock, voice only, no dip. Price: FREE.

**K-3 · OPEN THE LIST's missing voice (Klang's charge).**
Row 8. Parity with FIX's departure under H-2's same-departure
law. Cure proposed: floor seat knock, voice only. Price:
FREE. Not in the audit's merge list — Pforte to rule.

**K-4 · The tear has no night (optional engine follow-up,
NOT merge-blocking).** `playTearPulse` fires at gain 0.68 —
joint-loudest in the engine, tied with the completion pulse —
with haptic 160/110 and no night variant, on a sheet whose
canonical press is ~04:00 and whose only other press voice
was deliberately floored at night (0.06). Audit F-1 ships the
tear as-is, and as-is is lawful; this is a volume charge, not
a truth charge. **Priced follow-up (SMALL, engine + one call
site):** `playTearPulse` gains a night form mirroring the
seat knock's pattern — Reception passes `isNight`
(TactileSoundEngine callers on Tasks keep today's exact
values as the default). Klang's proposed night render:
acoustic gain 0.68 → 0.45, haptic 160/110 → 110/75, Hz /
duration / chord untouched — the tear must stay recognisably
the tear, only spoken at night volume. Ren/Pforte to ratify
the numbers; nothing else in this score waits on it.

**Acquittals, for the record:** the rise silence, the
departure silence, the not-repaired silence (A-5), the leave
silence, the seat-knock values day and night, and the firm
click's unscaled weight *at the verdict* are all designed and
stand. The sheet's sound problem was never its silences of
law — it is one borrowed voice (K-1) and two keys that
swallow touch (K-2, K-3).

**Counts:** 11 events scored · 3 voices in the engine
inventory · 3 sound call sites in the sheet today, 5 as
cured (CHECK + OPEN THE LIST gain the knock; DONE's site
splits stamp/non-stamp) · lawful silences 5 (rows 1, 3, 5,
6, 11) · missing silences 2 (rows 7, 8) · lying voices 1 (K-1).

— Klang (feel), issue #14 cell
