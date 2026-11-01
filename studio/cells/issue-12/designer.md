# Issue #12 — Main-App Pass: Tasks + F-1 — Designer Spec

**Issue:** Sarthak-2007/Focus-app #12 — Tasks + F-1
**Role:** Designer, AMT cell
**Status:** DECLARED — design only, uncompiled, no code written, no Gradle run
**Source note:** Cell clone `~/workspace/clone-inspect/builds/cells/issue-12/` does not exist on this box at time of writing. This spec is worked from the cell brief alone. `studio/plan/AUDIT-tasks.md` could not be read. Where the audit's exact screen wording differs from this spec, the audit's facts win and this spec's flows should be reconciled against it before build. Nothing below invents audit wording.

— Designer (issue #12 cell)

---

## 0. Principle governing all of F-1

A sealed session's intention is a claim about what the user meant to do.

Today it is false: `RoutineDerive.kt` writes `intention = routine.name`, so a 05:00 FASTED study block seals as "FASTED" / the routine's name even if the user queued "Physics — Rotational Mechanics PYQs" the night before. The queue does nothing.

After F-1, the seal must pass TELL-THE-TRUTH:

> Intention text on the Shield, on the receipt, and in the drawer is either (a) the verbatim title of the queue head at the instant of sealing, or (b) an explicit statement that there was no queued task. Nothing else. Never the routine name disguised as an intention, never a generated placeholder.

Routine name remains where it belongs: as provenance — which routine fired — in small telemetry type, never as the headline intention.

---

## 1. Exact user-visible flow — routine fires, session seals

### 1.1 Vocabulary used on screen

| Term | Meaning | Where shown |
|---|---|---|
| Window opens | The routine's scheduled start arrives (e.g. 05:00) | Pre-seal preview on Shield |
| Seal | The instant the session locks and blocking becomes active. Atomic. | Transition to active Shield |
| Intention | Queue-head title snapshot taken at Seal | Shield headline, receipt, drawer entry |
| Provenance | `ROUTINE 05:00 FASTED · FROM QUEUE` or `· NO QUEUE` | Anonymous Pro, small, under intention |

### 1.2 Timeline

**T — night before / any time before the window**
- User orders the Tasks queue. Head of queue is marked `NEXT` (see §4.3). No other action needed. Queueing *is* setting tomorrow's intention. No separate "set intention" field, no duplicate entry.
- No confirmation dialog. The `NEXT` marker is the confirmation.

**T0 — Window opens (e.g. 05:00)**
- Routine arms in the normal way (ARM-fast standard unchanged).
- If the user opens the app / wakes the Shield preview before sealing, they see the **pre-seal preview**, visually distinct from a sealed session:
  - Pre-seal card, dashed / unsealed border (unsealed = not yet torn shut, see §5 language in reverse).
  - Label in Anonymous Pro: `ABOUT TO SEAL — 05:00 FASTED`
  - Headline: current queue head title, verbatim, in Sora. Example: `Physics — Rotational Mechanics PYQs`
  - Sub-line: `FROM QUEUE · HEAD · WILL LOCK AT SEAL`
  - This preview is **live**: if the queue changes, the headline changes with it (rule in §3.1). It must never be mistaken for the sealed intention — hence the `ABOUT TO SEAL` label and the unsealed border are mandatory, not decorative.
- If the user never looks at the phone, nothing is required of them. Sealing does not depend on viewing the preview.

**T-seal — Seal instant**
- Session seals by the existing routine mechanism. At that exact instant, in one atomic read:
  1. Read queue head.
  2. Copy its title **verbatim** — no trimming beyond display truncation (§6.4), no case change, no added category, no duration suffix.
  3. Store that string as the session's intention snapshot, plus provenance (§7, cost-flagged).
- The Shield transitions: unsealed border → sealed state.
  - Headline: the snapshot. Same words the user wrote in the queue.
  - Sub-line changes to past tense: `SEALED 05:00 · FROM QUEUE · 05:00 FASTED`
  - From this moment the headline is **frozen**. It does not track the queue any more (§3.2).

**During the session**
- Shield shows the frozen intention headline for the whole session. This is the point of F-1: at 07:30, mid-block, the phone tells the user what *they* chose last night, not what the schedule is called.
- No queue UI on the Shield. No "change intention" affordance on the Shield. HOLD-absolutely: mid-session intention editing would be an escape hatch wearing a productivity costume. Queue edits are still possible if the user leaves via normal routes and opens Tasks — but they do not touch the sealed session (§3.2).

**Session end — receipt**
- Receipt headline: same frozen intention, word for word.
- Provenance line: `05:00 FASTED · QUEUE HEAD AT SEAL 05:00`
- If the queue head has since changed, receipt may add one factual line (§3.3) — receipt only, never retro-edited into the headline.
- Drawer entry stores the same frozen intention and is searchable by it. A user searching their drawer for "Rotational" must find this session.

### 1.3 Worked example — the audit's case

1. 22:40, night before: user puts `Physics — Rotational Mechanics PYQs` at head of queue. It shows `NEXT`.
2. 05:00: FASTED routine window opens. Preview (if viewed) shows that title.
3. Seal: intention snapshot = `Physics — Rotational Mechanics PYQs`.
4. Shield all block long: that title. Not "FASTED". Not "Study".
5. Receipt / drawer: that title, provenance `05:00 FASTED · FROM QUEUE`.

Queueing tomorrow's first task now *does* something. That is the whole fix, visible.

---

## 2. Empty-queue behaviour — designed honestly

### 2.1 The rule

If the queue is empty at the seal instant, the session still seals and blocking still holds — an empty queue must never be a reason the blocker fails to arm. But the intention is **none**, and the screens must say so plainly.

Forbidden fallbacks, explicitly:
- ❌ Routine name as intention (the current bug).
- ❌ `"Study"`, `"Focus"`, `"Session"`, `"Deep Work"` or any generated category word.
- ❌ Last session's intention carried over silently.
- ❌ Yesterday's queue head resurrected.
- ❌ A blank headline with no explanation (reads as a bug, not as truth).

### 2.2 What shows

**Pre-seal preview (window open, queue empty):**
- Headline: `No task queued`
- Sub-line: `QUEUE EMPTY · SESSION WILL SEAL WITH NO INTENTION`
- One action, plainly labelled: `Add a task` → goes to Tasks with the add field focused. Not a modal, not a blocker on sealing.

**Sealed Shield (sealed with empty queue):**
- Headline: `No task queued`
- Sub-line: `SEALED 05:00 · NO QUEUE · 05:00 FASTED`
- The headline is set in the same Sora size/weight as a real intention. Shrinking or greying it into apologetic small print would hide the truth; the absence should be as legible as a presence. Colour: body text colour, **not** an error red — empty queue is a fact, not a fault state.

**Receipt:**
- Intention field: `— No task queued —`
- Provenance: `05:00 FASTED · QUEUE EMPTY AT SEAL 05:00`
- Drawer entry: intention stored as null / empty with source `NONE` (§7), displayed as `No task queued`, and searchable under no false term — it must *not* appear if the user searches a task title.

### 2.3 The night-before nudge — the only proactive element

Designer proposes, as optional and cheap: when a routine with a study-type window is scheduled within the next 12 hours and the queue is empty, the Tasks screen (only there — no notification, no ping) shows a standing strip: `Tomorrow 05:00 FASTED has no queued task.` Dismissible by adding a task; no other dismissal needed because it lives only on Tasks. 

No push notification is specified. Monolith does not nag; and a 22:00 notification would be a new surface with its own permission and annoyance cost. **Cost flag: LOW** — Tasks-screen strip only, reads existing routine + queue state.

---

## 3. Queue changes mid-window — the rule and its visible trace

### 3.1 Before seal: live

Window open ≠ sealed. Between window-open and seal, the preview tracks the queue in real time:
- Reorder → new head becomes the preview headline immediately.
- Edit head title → preview shows edited title.
- Delete head → preview shows the new head; if that was the last item, preview switches to the empty-queue form (§2.2).
- Rationale: nothing has been claimed yet, so nothing can be falsified. Live tracking also gives the user a last, honest chance to fix the queue.

### 3.2 At and after seal: frozen snapshot

At seal, the intention is a snapshot, detached from the queue row.
- Edit the head's title after seal → sealed intention unchanged. The drawer must not be rewritten by a later typo fix. Implementation consequence (design-level, for the builder): intention is stored as a copied string, **not** as a live foreign-key render of the task row. See §7 cost flag.
- Reorder after seal → sealed intention unchanged. The reordered head becomes relevant to the *next* session.
- Delete the originating task after seal → sealed intention unchanged; the task is gone from the queue, the record of what was intended remains in the drawer. Deleting a task must never delete history.
- Complete / check off the head during or after the session → sealed intention unchanged. Completion state lives on the task, not on the session headline.

Rule in one line, for the plan file: **Live before seal, frozen at seal, never rewritten after seal.**

### 3.3 Visible trace of divergence

The user should be able to tell, afterwards, that the world moved after sealing — without the Shield being disturbed mid-session.

- **On the Shield during the session:** no divergence notice. Zero. A "your queue changed" note mid-block is noise inside a HOLD surface.
- **On the receipt, only if true at session end:** one Anonymous Pro line, factual, no judgement:
  - If head differs: `QUEUE MOVED AFTER SEAL · NOW: <current head title>`
  - If queue now empty: `QUEUE EMPTIED AFTER SEAL`
  - If head unchanged: line is omitted entirely. Absence of the line = nothing moved.
- **In the drawer entry:** same line persisted, so the record is complete a month later, not just on the transient receipt.

**Cost flag: LOW–MEDIUM** — receipt/drawer line needs the stored snapshot (already in §7) compared against queue head at session end; a comparison, no new user data.

---

## 4. Tasks screen after Horizon + SPLIT die

### 4.1 What dies, and why it must leave no ghost

- **Horizon bar:** a proportional bar whose segment widths came from `estimatedMinutes.coerceAtLeast(10)`. Proportions derived from clamped estimates are decoration claiming to be data. It dies completely — not shrunk, not made optional, not kept "for looks". A smaller fake bar is still fake.
- **SPLIT generator:** auto-splitting a task into generated pieces with category hard-coded `"Study"`. It dies completely, including its button, its generated sub-items, and the hard-coded category. Any sub-items previously generated remain as ordinary user-visible tasks in the queue (deleting the generator must not silently delete queue items — builder to confirm migration behaviour; designer ruling: generated items stay, become plain tasks, lose any special styling).

No tombstone UI. No "Horizon removed" banner. The screen just stops lying.

### 4.2 What replaces them

**Honestly: nothing replaces the bar. Nothing replaces the generator.**

What the space is given back to:
- The queue list itself gets the reclaimed vertical space. More tasks visible without scrolling — on a phone screen, that is a real gain, not a gap.
- The head-of-queue treatment (§4.3) becomes the screen's visual anchor instead of the bar.

### 4.3 New layout, top to bottom

1. **Header:** `Tasks` (Sora) + count in Anonymous Pro: `7 QUEUED`. No total-minutes figure in the header — a total summed from the same unreliable estimates would be the Horizon bar in number form. If a task carries a user-entered duration, it may show on that task's own row as text (`· 90 MIN`, as written by the user) — per-item fact, never aggregated, never drawn to scale.
2. **Head card (`NEXT`):** first queue item, visually separated — full-width card, `NEXT · SEALS AT <next routine time if one exists, else omitted>` marker in Anonymous Pro, title in Sora. This card is the F-1 teaching surface: it shows the user, in place, which title will appear on tomorrow's Shield. If no routine is upcoming, marker reads only `NEXT`. No invented time.
3. **Queue list:** remaining items in order, ordinal numerals `02, 03, …` in Anonymous Pro, title, drag handle for reorder. Row height uniform — **no row or segment sized by duration.** Order is the only encoding; order is true.
4. **Add field:** pinned at bottom, as today in spirit: type a title, add to tail; long-press / drag to promote. Adding to head directly: an explicit `Add as next` affordance on the add field — because F-1 makes head position meaningful, getting a task to head must be one gesture, not drag-through-six.
5. **Empty state:** `Queue is empty.` + one line connecting to §2: `Routine sessions will seal with no intention.` + add field focused. No illustration, no Horizon-shaped skeleton.

### 4.4 Layout consequences, stated plainly

- Screen becomes a single-column list with one hero card. Simpler hierarchy, one less custom-drawn component to maintain.
- No proportional layout code remains on this screen; screenshot tests for Tasks get simpler, not harder.
- The visual interest the bar provided must be carried by typography and the `NEXT` card — Sora headline, Anonymous Pro telemetry, the app palette. If the screen feels plain in review, the fix is type/spacing craft, never resurrecting a data-shaped decoration.
- SPLIT's removal leaves task authoring fully manual. A user who wants two tasks makes two tasks. The add field should therefore be fast: return key adds and keeps focus for the next entry (rapid-fire entry), priced **LOW**, no schema.

**Cost flag for §4 overall: NEGATIVE-to-LOW** — net deletion plus one card treatment and one marker. No new schema. The only risk is data migration for previously SPLIT-generated items (builder to verify how they are stored; if they carry a generated flag, clearing the flag is a one-time migration — **MEDIUM** if a DB column must be touched, LOW if they are already plain rows).

---

## 5. ZIP-TEAR — the house tear standard

ZIP-TEAR is adopted as the standard for the next tear build and, by this spec, for every tear surface after it. "Tear" = the gesture that opens/ends something sealed (session receipt opening, sealed drawer entry opening, any future sealed artefact). This section is spec-level: interaction, sound, shape. No code.

### 5.1 What "zip-tear" means, concretely

A tear that behaves like opening a zip-seal / perforated strip: **one continuous linear pull, progress locked 1:1 to the finger, material separating progressively behind the pull point, finishing with a clean release.** Not a tap. Not a long-press. Not a swipe that triggers an animation which then plays itself.

### 5.2 Interaction standard (mandatory for any tear surface)

1. **Direction:** single axis per surface, declared by the surface: horizontal left→right for receipts/drawer entries (reading direction), never diagonal, never free-form.
2. **1:1 tracking:** separation front sits exactly under the fingertip for the entire drag. Finger stops → tear stops, mid-tooth, indefinitely. Finger reverses → front retreats and teeth visually re-close up to the reversal point (no ratchet lie before completion). No easing, no inertia carry after finger lift before threshold.
3. **Initiation:** finger must start on the tear strip itself (the perforated band, §5.4). Drags starting elsewhere scroll or do nothing, per the host screen — they never tear.
4. **Completion threshold:** 92% of strip length. Crossing it under finger → the remainder separates in one short finishing motion (≤150 ms) and the surface declares itself torn. Below threshold on finger-lift → tear reseals smoothly back to 0 (≤200 ms), surface unchanged, no partial state persisted.
5. **Irreversibility point:** once completed, a tear does not un-happen on that artefact. Re-sealing is a new seal (new session / new artefact), never the old tear run backwards. Receipts torn once stay torn in the drawer (torn edge persists as the artefact's state — see §5.4).
6. **Speed honesty:** fast zip and slow peel are both valid and must feel different only in tempo, not in outcome. No minimum-speed requirement, no velocity-triggered auto-complete below threshold.
7. **Accessibility alternative (mandatory, not optional):** a declared non-gesture path — press-and-hold the tear strip for 800 ms with a filling progress ring, or a system-accessibility action labelled `Tear open` — producing the identical torn result. A motor-impaired user must not be locked out of their own receipts. **Cost flag: LOW** — one alternate trigger, same end state.
8. **Interruption:** incoming call / app backgrounding mid-tear → tear freezes; on return, it is either still under threshold and resealed, or already completed and torn. Never left visually half-torn as a persisted state.

### 5.3 Sound + haptic language

- **Sound image:** dry, close, fibrous — a zip-strip / perforated-card rip, not a paper-crinkle sample loop and not a synthetic "whoosh". Delivered as discrete micro-ticks, one per tooth (§5.4), grouped tightly as speed rises, so tempo is heard, not just seen. Silence at standstill mid-tear: finger still = sound still.
- **Volume/behaviour:** follows system media volume; fully silent when the device is muted; never plays on the reseal retreat at full level (retreat ticks at reduced level or none — resealing should sound like closing, not tearing backwards). Sound is accompaniment, never the only signal: every audio event has a visual counterpart.
- **Haptics:** one light tick per tooth crossing, matched to the visual tooth pitch, so a tooth can be felt, heard, and seen at the same position. Completion: a single, slightly firmer snap. Reseal: no snap. Haptics off in system settings → visual standard stands alone, unchanged.
- **Cost flag: MEDIUM** — per-tooth haptic/audio sync is new feel-work needing on-device tuning. Flagged honestly: this is the one part of ZIP-TEAR that cannot be verified off a phone.

### 5.4 Shape language

- **Perforation band:** the untorn artefact shows a straight perforated band across its width: a row of punched slots/dots in Anonymous-Pro-telemetry spirit — regular, mechanical, honest about where it will separate. Band sits at a consistent inset from the artefact's edge across all tear surfaces (house dimension, same on receipt and drawer).
- **Teeth:** separation produces a regular zig-zag / serrated edge on both resulting edges — tooth pitch constant across surfaces (house pitch, builder to fix one value in dp and reuse). The two edges are complementary: tooth on one side, notch on the other. No random jaggedness — zip-tear is a machined tear, not a ripped page. This regularity is what makes it a *standard* rather than an effect.
- **Separation gap:** as the front passes, the two sides part by a small, constant offset so light/background shows through the opened slit progressively. The opened region reads as *open*, not merely scored.
- **Residue:** the torn edge persists. A torn receipt in the drawer keeps its serrated edge forever; untorn and torn artefacts are distinguishable at a glance in a list. State is in the silhouette, not in a badge.
- **Colour:** tear surfaces use the existing app palette only; the perforation band is drawn in a muted tone of the artefact's own surface, the opened slit shows what is beneath. No new accent colour introduced for tearing, and per standing rule, no green anywhere.
- **Typography during tear:** text on the artefact does not move, warp, or fade with the tear. Only the band separates. Legibility is never the price of the effect.

### 5.5 Acceptance checklist for the next tear build (designer's)

- [ ] Finger stops mid-tear → front, sound, haptics all stop at the same tooth.
- [ ] Release at 50% → reseals, artefact byte-identical in state to before.
- [ ] Cross 92% → completes once, snap once, state persists across app restart.
- [ ] Torn artefact reopened from drawer → edge still serrated, no re-tear offered on same artefact.
- [ ] Muted device → full tear possible, visual standard complete alone.
- [ ] Accessibility path (§5.2.7) reaches the identical torn state.
- [ ] Tooth pitch + band inset identical on receipt and drawer surfaces.

---

## 6. Edge cases — designed answers

1. **Queue empty at seal.** Session seals, intention = none, screens per §2.2. Blocking never fails for lack of a task.
2. **Head deleted after window opened, before seal, queue now empty.** Preview flips to empty form live; seal follows §2. Deletion is a legitimate way to say "nothing queued".
3. **Head edited (retitled) before seal.** Preview + snapshot use the edited title — at seal, the queue's current truth is the intention.
4. **Head edited after seal.** Snapshot unchanged (§3.2). Drawer keeps the words as they were when sealed, even if the live task now reads differently.
5. **Queue reordered after seal so a *different* task is head.** Snapshot unchanged; receipt gains the `QUEUE MOVED AFTER SEAL` line (§3.3).
6. **Two routines fire back-to-back / overlapping windows.** Each seal reads the queue at *its own* seal instant, independently. If the head wasn't consumed/removed between them, both sessions may legitimately seal with the same intention — that is truthful (user queued one thing, did it across two blocks), not a bug. No auto-advance of the queue on seal: **designer ruling — sealing never consumes or dequeues the task.** Only the user completes or removes tasks. Auto-dequeue would silently destroy the queue when a session is cut short. Cost: none; this is a non-feature, stated so nobody builds it.
7. **Manual (non-routine) session sealed while a queue head exists.** Out of F-1's strict scope, but the same truth rule should apply for consistency: manual sessions already take a user-given intention; if the user instead picks "use queue head", same snapshot mechanics. If they type a free intention, it wins over the queue (explicit beats derived). Builder/parent to confirm scope — designer flags this as a **decision needed**, not silently expanded scope.
8. **Very long head title.** Snapshot stores the full title. Display: Shield headline wraps to max 3 lines, then ellipsises visually only; receipt shows full title wrapped; drawer search matches the full string. Storage never truncated.
9. **Head title is whitespace / empty string / emoji-only.** A queue item with no visible text is treated as **no valid head**: seal proceeds to the next item with visible text as head; if none, empty-queue behaviour. Emoji-only counts as visible text (it is what the user wrote) and is stored/shown verbatim. Queue UI should prevent creating blank items in the first place (**LOW** — input validation on add).
10. **App killed / device rebooted between window open and seal.** No preview state is persisted as intention. At seal after restart, the atomic read happens then — queue as it stands at the real seal instant. A stale pre-reboot preview must never be restored as if it were a snapshot.
11. **Routine edited or deleted mid-window (time moved, routine cancelled).** If the routine is cancelled before seal, no seal, no intention, nothing in the drawer — a session that never happened leaves no receipt. If its time moves, the new window's seal reads the queue at the new seal instant. Rule §3 covers both without special cases.
12. **Clock / timezone change between queueing and seal.** Provenance timestamps are recorded in device local time at seal and displayed as recorded; no retro-conversion. The intention *text* is unaffected by clock games — another reason the snapshot is a string, not a time-derived value.
13. **Originating task completed during the session.** Intention headline unchanged (§3.2). Receipt may add `TASK MARKED DONE DURING SESSION` — designer says **omit**: completion is visible on the task itself; the receipt line would be judgement-adjacent noise. Ruled: no line.
14. **Duplicate titles in queue (two identical tasks).** No problem for F-1: snapshot is a string; provenance does not need to disambiguate which twin it was. If §7's task-id snapshot is stored, it quietly does disambiguate for the record, with no user-facing difference.
15. **Seal fires while user is mid-edit of the head title in Tasks (editor open, unsaved).** Seal reads the last *saved* queue state. An open editor's unsaved keystrokes are not queue truth. If the user then saves, that edit is a post-seal edit (§3.2 applies).

---

## 7. Data / schema cost flags (summary for builder + innovator)

| Item | Need | Flag |
|---|---|---|
| Intention as copied string at seal | Session record must hold a detached string, not a live task reference | Likely already the storage shape (`intention` field exists today, filled from routine name) — if so, **NO schema cost**, source change only. Builder to verify. |
| Provenance: source enum (`QUEUE_HEAD` / `NONE` / future `MANUAL`) + routine label + sealed-at time on the session | New columns if not present | **MEDIUM** — DB migration, version bump, drawer/receipt rendering of old rows must default old sessions to an honest `SOURCE NOT RECORDED` rather than back-filling a guessed source. Old routine-born rows were routine-named; do not relabel them as queue-derived. |
| Snapshot of originating queue task id (nullable) | Optional, record-only (§6.14) | **LOW** if added in the same migration as above; skip entirely if migration cost must be minimised — string + source already deliver the user-visible fix. Designer ranks this nice-to-have, not required for F-1 to ship. |
| `QUEUE MOVED AFTER SEAL` receipt line | Comparison at session end; persist boolean/current-head string on the receipt record if drawer must keep it after further queue changes | Persisting the *line as rendered at session end* (a string) is **LOW**; recomputing it live in the drawer forever would falsify it later — store the rendered line. |
| Horizon / SPLIT removal | Deletion; possible flag-clearing for generated items | **LOW**, **MEDIUM** only if generated items need a migration (§4.4). |
| ZIP-TEAR per-tooth haptics/audio sync | No schema; device tuning | **MEDIUM** effort, phone-verification required (§5.3). |
| Tasks `NEXT` card, empty-queue Tasks strip | Existing routine + queue reads | **LOW**. |

No item in this spec requires network, accounts, or new permissions.

---

## 8. What this spec deliberately does not do

- Does not auto-advance, consume, or complete queue items at seal or session end (§6.6).
- Does not add notifications for empty queues (§2.3).
- Does not put queue editing on the Shield (§1.2).
- Does not replace Horizon/SPLIT with new visualisations (§4.2).
- Contains no code, no compile step, and no message to Sarthak.

**DECLARED — design complete, uncompiled, ready for cell review.**

— Designer (issue #12 cell)
