# FEEL-4 — Atmosphere — Issue #12: Tasks + F-1

**Issue:** Sarthak-2007/Focus-app #12 — Tasks + F-1
**Role:** Feel-4 · atmosphere (issue #12 cell)
**Status:** DECLARED — atmosphere spec only, uncompiled, no code written, no Gradle/tests run
**Sources:** `studio/plan/AUDIT-tasks.md` (Amt's Tasks audit, Weiss's measurements) and `studio/plan/PLAN-issue-12-tasks-f1.md` (Amt, cell lead) in the cell clone on branch `agent/issue-12`
**Palette law throughout:** no green anywhere, ever. Every tone named below is a warm neutral already on record in this app.

— Feel-4 · atmosphere (issue #12 cell)

---

## 0. The two hours this screen actually lives in

The Tasks screen has two real appointments, and neither of them is daytime.

**~1 AM — queueing tomorrow.** He is half-asleep, in the dark, doing one errand: put tomorrow's first work on top of the pad. He should not have to wake up to do it. The screen must be readable without effort, tappable without precision, and forgettable the second he puts the phone down. Any brightness, chatter, or ambiguity here is not a style flaw — it costs sleep.

**05:00 — the seal.** He wakes at 03:30, bathes, meditates for an hour, and the FASTED block seals at 05:00 while he is studying fasted. He does not sit down and operate the app at 05:00; the session arrives around work already begun. What he meets — on Tasks, on the Shield that carries the sealed intention — must feel like a door he closed himself last night, now shut. Not an interface state. A fact.

Everything below is specified against those two hours. A choice that looks fine at noon and fails at either hour fails.

---

## 1. Night legibility is atmosphere: contrast as kindness, not spec

### 1.1 The measured fact

Weiss, in the audit (item 19, fix F-8): night plate print — the small plate/index print on a leaf — sits at **3.51:1** and *fails a tired eye*. The fix on record is **leaf-local `#9C8E84` at 5.78:1**.

Feel-4 adopts that fix exactly, and names why it is an atmosphere decision and not a compliance number:

- At 3.51:1, the tired eye does not read the plate — it *works* at it. Squinting is a physical act; it pulls him awake. A screen that makes a man squint at 1 AM has taxed the exact resource the errand was supposed to protect.
- At 5.78:1 in `#9C8E84`, the print is simply *there* when the eye crosses it. No effort, no lean-in, no second pass. That effortlessness is the feeling: **the pad is considerate in the dark.**

### 1.2 The lift must stay leaf-local — do not brighten the room

The kindness is precisely in the word *leaf-local*:

- **Lift only the print that the hand needs to read** — plate/index, and by the same register the leaf's small telemetry — to `#9C8E84`.
- **Do not lift the ground, the leaf paper, or the screen as a whole.** The night ground stays the locked night ground (`#171412`, per the app's night staircase). A globally brighter screen at 1 AM is a lamp turned on in a bedroom: legible, yes, and hostile. The room must stay dark while the words on the leaf become readable.
- `#9C8E84` is the right *temperature* for this, not just the right ratio: a warm taupe-grey, kin to the paper and the night ground — not a blue-white glare tone, not a cold grey, and never green. It reads as ink warmed by lamplight, not as a backlight turned up. The title register above it (`#D2C6BB`, 10.95:1 on the night staircase) stays untouched; the ladder keeps its order — title brightest, plate print lifted to readable, quiet print quietest — the eye is never asked to re-sort the leaf at night.

**Acceptance, in felt terms:** at arm's length, in a dark room, half-asleep, the plate print is read in one pass with no squint and no lean — while the screen as a whole still feels *dark*. If a finisher achieves the ratio by brightening the room, it is a fail even though the number passes.

### 1.3 The same kindness governs the other 1 AM traps the audit found

- The 5,000 ms undo window (audit item 3) is short for a 1 AM thumb. Atmosphere cannot re-price machinery, but it records the felt fact: at 1 AM, *undo expiring before he notices it* feels like the pad snatching a leaf back. Whatever window the machinery seat rules, the banner must not add urgency of its own — no countdown theatrics, no flashing.
- The sub-step strike's silent full-width tap target (audit item 10, fix F-6) is a 1 AM trap in felt terms too: a half-asleep tap that silently changes the leaf feels like the paper moved under his hand. The audit's cure stands — the tear's own tick, or a narrowed target — because a sound *confirms the hand*, it does not decorate the tap.
- The composer moving to the foot of the pad as its next blank leaf, with an end-key ADD and a FIRST key (audit item 2, fix F-2), is the correct 1 AM posture: he writes on the pad where the next leaf already is, instead of reaching to a loud full-width key at the top that out-shouts the real commit. The errand should feel like *writing one line and putting the pad down*.

---

## 2. The pad of paper: the metaphor's temperature

### 2.1 Tones that step truly are what make it paper

The audit acquits the pad edge's *tones* and convicts its *geometry* (item 16). Both halves matter to feel:

- **Tones step truly — keep them exactly.** Weiss measured the stack tones stepping day **1.89 / 1.50 / 1.27** and night **2.04 / 1.56 / 1.27** — each leaf behind measurably quieter than the one in front. That true stepping is the whole metaphor: paper recedes; it does not repeat. If the steps were equal, or faked with a shadow graphic, the edge would read as a UI border wearing a paper costume. The warmth of the pad is in this recession — front leaf present and warm, leaves behind progressively cooler and quieter, like pages in low light.
- **Saturation at 3 leaves behind is honest — keep the cap** (audit item 16). Deeper stacks do not claim more paper than is drawn. A pad that pretends to be fifty leaves thick is a lie about quantity; three steps and then rest is the truthful depth of a working pad.

### 2.2 The stack must touch the leaf — the geometry fix is a feel fix

As built, the stack lines cluster **1–7dp from the edge** against a leaf rim at **11 / 14 / 17dp**: 9–10dp of bare ground sits between leaf and stack, and reads — Weiss/Amt's words, adopted here — as *a margin stripe, not a stack*.

In felt terms: the leaves behind are *hovering near* the pad instead of *being* the pad. At a glance in daylight that is a nit; at 1 AM it is the difference between "my pad, where I left it" and "a diagram of a pad." The audit's cure (fix F-8) is therefore an atmosphere requirement, not polish:

- **Re-anchor the edge lines to rim + 1 + 3k**, so each line sits against the leaf it belongs to.
- **Leaf gap 12dp → 4dp**, so successive leaves touch and the pad reads as one object with thickness, not a column of separated cards floating over the ground.
- **Queued leaves get their foot rule** (audit item 18): queued paper currently sits at **1.00:1** — the surface *is* the ground, so queued leaves feel like print floating on the desk rather than paper lying on it. The state-only foot rule gives each leaf a bottom edge to rest on. No fake raise for queued leaves — only the head is lifted.
- **The head raise stays thin and honest, finished by its foot-shadow** (audit item 18, Weiss's priced spec): **1.23:1 day / 1.35:1 night** is a real raise, not a card effect. The head leaf should feel like the top sheet of the pad — same paper, nearest the hand — not like a promoted tile. If it glows, floats, or casts a product shadow, the temperature is broken.
- **One perforation tone for all leaves: `outlineVariant` at alpha 0.55 (2.18–2.46:1)** (audit item 17, fix F-8). The inversion Weiss found — the head leaf's cut *weaker* (1.23/1.35:1) than the queued leaves' (1.50/1.56:1), despite the code comment claiming the head's cut is "at full strength" — is felt before it is measured: the one leaf he can tear is the one whose tear-line he can least see, in the dark, with a tired eye. One tone, the stronger register, everywhere: the cut is part of the paper, identical on every sheet.

**Temperature summary for the finisher:** warm, low, and contiguous. Warm — every tone a brown-grey from the existing staircase, cream appearing as text only, never as a glowing fill, no green, no blue-white. Low — nothing on this screen rises in brightness except the head title and the one accent verb. Contiguous — leaves touch, stack touches leaf, cut belongs to paper. The moment any element floats, glows, or separates, it stops being a pad and becomes an app again.

---

## 3. The sealed state: a closed door, not a greyed screen

### 3.1 The law, in felt terms

The audit's law (item 12, fix F-5; acquitted as already-honest in the tear's own behaviour): under a seal, **every verb on the leaf is absent, not greyed** — DEL, CUE/START, SPLIT and the sub-step toggles join the tear in withdrawing; the perforation's solid overprint is the mark; the QUEUE header gains the one printed word — `N TASKS · SEALED` (audit item 4, Muede's F-3).

Feel-4 states what that law *feels* like, because the two implementations of "you can't touch this" produce opposite mornings:

- **A greyed screen says: the machine is withholding.** Greyed verbs are still *present* — they advertise everything he may not do, at reduced brightness, like controls behind glass. At 05:00, fasted, mid-meditation's afterglow, that reads as negotiation: the app standing between him and the leaf, visibly refusing. It also reads as *broken* — grey is the colour of a disabled form, a thing waiting to be re-enabled. A sealed morning should never look like a malfunction.
- **An absent verb says: the room has been rearranged.** The leaf under seal is a *record* — paper already written on and put under the seal. There is nothing to refuse because there is nothing offered. The leaf is complete the way a closed door is complete: you do not look at a shut door and feel greyed-out; you feel *shut*. That finality is calm. It does not argue, and because it does not argue, it does not need willpower from him to respect.

### 3.2 The seal's two marks, and nothing else

Atmosphere permits the seal exactly two expressions, both already ruled:

1. **The perforation's solid overprint** — the physical mark on the paper itself. It should feel like a stamp pressed across the cut-line or a fold pressed flat: a change *in the paper*, not a badge pinned *on* the screen. No new icon, no lock glyph, no shield graphic on the leaf. Paper seals paper.
2. **The one word `SEALED` in the QUEUE header** — the sentence that explains the room. One word, in the header's existing telemetry register, next to the count he already trusts. Not a banner, not a tint across the screen, not a darkening — the audit explicitly rejects "a silent darkening," and Feel-4 seconds it: a dimmed screen at 05:00 reads as the phone's failure or the app's mood, never as *his* decision taking effect.

Everything else about the sealed Tasks screen stays at full, normal legibility: titles readable, sub-steps readable as quiet print, the queue still consultable. A closed door is not a dark corridor — he can still *see* tomorrow's leaves behind today's; he simply cannot rearrange them mid-block. That consultability is the trust half of HOLD: the seal hides nothing from him, it only removes his own levers. Greying would have hidden the print along with the verbs; absence keeps the print honest and removes only the verbs.

### 3.3 The transition into seal

No ceremony is specified and none should be added. The seal happens at 05:00 by routine while he is already working; a sealing *animation* on Tasks would be theatre for an audience that isn't watching, and if he does glance at it, theatre reads as the machine congratulating itself. The state simply *is* — verbs gone, overprint present, header word changed — the way a door is shut when you come back into the room, without anyone announcing the shutting.

---

## 4. The one atmosphere risk of F-1: the morning reads his own hand back to him

### 4.1 The risk, stated plainly

F-1 (PLAN-issue-12) is mechanically small and true: `DesiredSession.intention = headTaskTitle ?: routine.name`, the head resolved at the seal instant, intention stored as a snapshot string, the task never consumed, no `taskId` written. The machinery is honest.

The atmosphere risk is in **whose voice the sealed session speaks with at 05:00.**

The title was written at ~1 AM, half-asleep, in shorthand, possibly misspelt, possibly terse to the point of rudeness — because at 1 AM it was a note *to himself*, on paper, in private. At 05:00 that same string becomes the session's headline: printed large, carried on the Shield for the whole fasted block, archived on the receipt and in the Drawer. If anything in that journey makes the words feel *machine-processed*, the seal stops feeling like his own decision coming back to him and starts feeling like the app *announcing* him to himself. That is the one way F-1 — the audit's headline fix, the fix that makes the pad's promise true — could ship mechanically correct and still feel wrong every single morning.

### 4.2 The naming law: his hand, verbatim, in its own register

Feel-4 rules the following as atmosphere requirements on every surface that prints a queue-derived intention (Shield headline, receipt, Drawer):

1. **Verbatim, character for character.** His capitalisation, his spelling, his shorthand, his punctuation — including a half-asleep typo. No title-casing, no auto-capitalise, no spell-correction, no trimming beyond pure display truncation (storage never truncated; the plan's snapshot guarantee stands). A "corrected" title at 05:00 says: *the machine edited you while you slept.* Even a helpful correction breaks authorship.
2. **No fused provenance.** Never `FASTED BLOCK — <title>`, never `<title> (FASTED)`, never a category prefix like `STUDY: <title>`. The routine name and the title are two different voices — the schedule's and his — and fusing them into one headline makes the machine sound like the author of the sentence. Where provenance is printed at all, it lives in its own small telemetry line, in Anonymous Pro, physically separate from the headline: the headline is *his words alone*, set as a headline; the telemetry is the machine's bookkeeping, set as bookkeeping. The eye must never be able to mistake which voice is speaking.
3. **No quotation costume.** Do not wrap his title in quotation marks, italics-as-quotation, or any framing that *quotes him to himself*. Quotation is what you do to someone else's words. His title on the sealed session is not a citation — it *is* the session's name now.
4. **Same words as on the leaf.** The string he sees sealed at 05:00 must be recognisably the string he saw on the head leaf at 1 AM — same words, same order, same form. Typeface and size may change with the surface; the wording may not gain or lose a single decorative element (no added duration suffix — minutes are dead on this screen, and a `· 90m` the composer never asked for would be the Horizon lie returning as a subtitle; no `#01` plate number carried into the headline).
5. **The fallback must not impersonate him.** When the queue is empty or DEFERRED-only, the plan falls back to `routine.name` — and that is atmospherically *correct*, precisely because the routine name does not pretend to be his handwriting. It is the schedule's name in the schedule's register, plainly itself. The dangerous fallback would be any generated, human-flavoured placeholder ("Your morning focus," "Today's first task") — machine prose imitating intention. The plan's honest fallback stands; no surface may dress it up to look hand-written, and no surface may dress his real title down to look generated. The two registers stay visibly distinct, so he always knows whether the morning is speaking with his voice or the timetable's.
6. **A terse or ugly title is not an error state.** If he queued something blunt at 1 AM, the sealed headline prints it at full headline dignity — same size, same weight, same colour as any other intention. Never shrink, grey, or apologise for his wording. The moment the app flinches at his own words, it has taken a position *about* him, and a blocker that judges its owner's handwriting will not be trusted with his mornings. (And per §1: full dignity never means a warning colour, and never green.)
7. **Repetition across a morning is a refrain, not a bug.** The plan rules that two routine sessions in one morning both read the same head, because sealing never consumes the task. In felt terms this is right *only if* the naming law above holds: his own words returning for the second block reads as *the same intention, continued* — his hand, again. If the title were machine-dressed, the repetition would read as a stuck announcement. Verbatim is what makes the refrain bearable — indeed, what makes it his.

### 4.3 The 1 AM side of the same risk

The risk is set at queueing time, not at seal time. Two small guards, both atmosphere-cheap:

- **The head leaf is the teaching surface.** At 1 AM he must be able to see — without being taught, without a hint line or a tooltip — that the top leaf is the one tomorrow will be named by: raised paper, bold title, `#01`, the accent verb (the audit's honest head signals, item 7). If head-ness is legible, then the man who writes carelessly on the head leaf has only himself to meet at 05:00, and meeting himself is the design. If head-ness is *not* legible, a surprise headline at 05:00 feels like the machine chose for him — the exact failure F-1 exists to kill.
- **No night-time editorialising about the queue.** No "are you sure?", no "this will be tomorrow's session name" warning, no smart suggestions, no autocomplete offering to finish or improve his title. At 1 AM the pad is paper: it takes what he writes, as he writes it. Every assistive layer between his thumb and the leaf is one more machine fingerprint on words that must arrive at 05:00 as purely his.

### 4.4 What success feels like

At 05:00, fasted, he glances at the sealed session and sees — in his own shorthand, his own spelling, standing alone as the headline — the thing he wrote in the dark. The recognition is instant and unmediated: *I chose this. It is already decided. There is nothing to renegotiate.* The session does not introduce itself; it *reminds* him. That is the whole emotional payload of F-1, and it is carried entirely by the discipline of §4.2: the machine did the carrying, and left no fingerprints on the words.

---

## 5. Consolidated atmosphere requirements (for Builder / Finisher)

| # | Requirement | Source |
|---|---|---|
| A1 | Night plate/index print: leaf-local `#9C8E84` (5.78:1). Ground and paper tones unchanged — legibility by ink, never by brightening the room. | Audit item 19 / F-8; §1 |
| A2 | Pad edge re-anchored to rim + 1 + 3k; leaf gap 12dp → 4dp; stack tones and 3-leaf cap exactly as measured. | Audit item 16 / F-8; §2 |
| A3 | Queued leaves: foot rule, no raise. Head leaf: existing raise + Weiss foot-shadow, no glow/card effect. One perforation tone for all leaves, `outlineVariant` @ 0.55. | Audit items 17–18 / F-8; §2 |
| A4 | Under seal: all leaf verbs **absent** ( DEL / CUE / START / SPLIT / sub-step toggles); seal expressed only by the perforation's solid overprint + `SEALED` word in the QUEUE header. No greying, no dimming, no overlay tint, no lock/badge iconography. Sealed print stays fully legible. | Audit items 4, 12 / F-3, F-5; §3 |
| A5 | Queue-derived intention printed **verbatim** on Shield / receipt / Drawer: no case/spell edits, no fused routine prefix or category, no quotation framing, no duration suffix. Provenance, if printed, only as a separate small Anonymous Pro telemetry line. Routine-name fallback stays in its own plain register, never dressed as hand-writing, with no generated placeholder text. | PLAN F-1; §4 |
| A6 | No new night surfaces around queueing: no confirmation, no warning that the head will name a session, no autocomplete/suggestion layer in the composer. Head-ness carried only by the audit's honest signals (raised paper, bold title, `#01`, accent verb). | Audit items 2, 7 / F-2; §4.3 |
| A7 | No green in any tone, state, or accent introduced or touched by this pass. Warm staircase tones only. | Standing law; §0 |

Nothing in this file requires a schema change, a new surface, or a compile. Where this spec and the plan disagree, the plan's machinery rulings stand and this file's felt requirements are to be reconciled by Amt — none are intended to contradict it; A5 in particular is the plan's snapshot-verbatim guarantee stated in felt terms.

**DECLARED — atmosphere complete, uncompiled, ready for Amt.**

— Feel-4 · atmosphere (issue #12 cell)

---

## Ready-to-post comment for issue #12 (AMT posts this)

> **Atmosphere — Feel-4, issue #12 cell**
>
> I walked the two hours this screen actually lives in: ~1 AM, queueing tomorrow half-asleep in the dark, and 05:00, when the FASTED block seals while he is already studying fasted. Full spec: `feel-4.md` in the cell work dir. The rulings in felt terms:
>
> **Night legibility is kindness, not spec.** Weiss's measurement stands — night plate print at 3.51:1 fails a tired eye; the fix is leaf-local `#9C8E84` at 5.78:1. The lift must stay leaf-local: raise the ink, never the room. The night ground stays dark, the tone stays warm — at 1 AM a squint is a tax on sleep, and a globally brightened screen is a lamp in a bedroom. Readable in one pass, screen still feels dark, or it fails even if the number passes.
>
> **The pad's temperature is true stepping + contact.** The stack tones step truly (night 2.04/1.56/1.27 — keep them, keep the 3-leaf cap), but the stack lines hover 9–10dp off the leaf and read as a margin stripe, not paper. F-8's cure is a feel requirement: re-anchor to rim + 1 + 3k, leaf gap 12dp → 4dp, foot rule under queued leaves (currently 1.00:1 — print floating on the desk), head raise kept thin and honest, and one perforation tone for all leaves — the one tearable leaf currently has the *weakest* cut, in the dark, for a tired eye. Warm, low, contiguous; the moment anything floats or glows it stops being a pad.
>
> **Sealed = a closed door, not a greyed screen.** The audit's absence-not-greying law, in felt terms: greyed verbs advertise everything he may not do, like controls behind glass — that reads as the machine withholding, even as a malfunction. Absent verbs read as a room rearranged: the leaf is a record, complete the way a shut door is complete. The seal gets exactly two expressions — the perforation's solid overprint (a change *in* the paper) and the one word `SEALED` in the QUEUE header. No dimming, no tint, no lock icon. And sealed print stays fully legible: he can still consult tomorrow's leaves; only his levers are gone. That consultability is the trust half of HOLD.
>
> **F-1's one atmosphere risk: the morning reads his own hand back to him.** The plan's mechanism is honest — intention = head title at the seal instant, snapshot, never consumed. But that title was written at 1 AM, in shorthand, possibly misspelt, as a private note to himself — and at 05:00 it becomes a headline he lives inside all block, then a Drawer record. So: **verbatim, character for character** — no title-casing, no spell-fixes, no fused `FASTED BLOCK —` prefix, no category, no duration suffix, no quotation marks quoting him to himself. Provenance, if printed, only as a separate small Anonymous Pro telemetry line — his voice and the timetable's voice never fused into one sentence. The routine-name fallback is atmospherically correct precisely because it doesn't impersonate his handwriting; never dress it up, and never dress his real title down. A blunt half-asleep title gets full headline dignity — the app does not flinch at his words. If anything machine-shaped touches the wording, the seal stops feeling like his own decision returning and starts feeling like the app announcing him to himself — F-1 mechanically correct and wrong every morning. Verbatim, it is instant recognition: *I chose this; it is already decided.*
>
> No green anywhere in any of this. **DECLARED** — atmosphere only, uncompiled.
>
> — Feel-4 · atmosphere (issue #12 cell)
