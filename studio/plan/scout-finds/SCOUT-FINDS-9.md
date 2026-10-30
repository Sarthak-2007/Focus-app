# SCOUT FINDS 9 — Study-Focus Science as Product Features

**Best serves: GitHub Issue #20 — the urge / feeling question** (what the block screen should ask, and what it should *do* with the answer). Several finds also feed #13 Session Room, but #20 is the one they change most: they turn a check-in from a survey into a mechanism.

**Design constraint honoured throughout:** Monolith never lectures and never tracks. It blocks and holds. So every find below is translated into something the app *does* in the moment — a hold, a phase, a surface change — never a lesson, a score, a streak, or stored history. User: NEET student, 50-minute blocks, 12 h/day goal.

---

## 1. Urge surfing — an urge you don't feed changes your response to the next one, even if it doesn't shrink

**The science:** Alan Marlatt's "urge surfing" (Marlatt & Gordon, 1985), tested by Bowen & Marlatt (2009): 123 college smokers were cue-exposed to provoke urges; half got brief mindfulness instructions — observe the urge as a wave that rises, crests and falls, don't fight it, don't obey it. The groups did **not** differ in how strong the urges felt. The surfing group smoked significantly fewer cigarettes over the next 7 days. The mechanism is not urge-erasure, it's urge–response decoupling. Clinical summaries put the typical crest at roughly 15–30 minutes if not fed.

**Sources:**
- https://psycnet.apa.org:443/doi/10.1037/a0017127
- https://stilldetox.com/urge-surfing/

**Evidence strength:** One small RCT (n=123) plus decades of clinical use inside MBRP/DBT/ACT — real, but the headline effect is behavioural follow-through, not craving reduction.

**Feature for Monolith — the Surf Hold (the core #20 mechanic):** When he tries to open a blocked app, the block screen does not show a countdown, a warning, or his stats. It asks **one tap only: "How strong? 1–10"** — then the screen *becomes* the wave: a slow visual swell that crests and visibly subsides over ~90 seconds, hold unbreakable throughout. Then it asks the same one tap again. Almost always the second number is lower, and he has *felt* the crest pass instead of being told urges pass. No record is kept, no graph is drawn. If the second number is still ≥8, the wave runs once more — the app out-waits the crest with him, silently. This is a check-in that does work, not data collection.

## 2. The struggle phase — the brain won't pay flow's entry cost if it expects to be interrupted

**The science:** Kotler's flow cycle (Flow Research Collective): flow is preceded by a **struggle phase** — an unpleasant loading period (practitioner accounts put it around 15 minutes) before release into flow. Rian Doris's sharp extension: struggle is only tolerated if a prolonged, uninterrupted immersion is *promised* on the other side. If you know in advance you'll be interrupted after 15 minutes, you get approach–avoidance conflict and never properly engage — "no waves to catch." The guarantee is not a nicety; it's a precondition for engagement.

**Sources:**
- https://riansweetdoris.substack.com/p/give-me-14-minutes-and-ill-destroy
- https://bigthink.com/videos/hack-your-flow-understanding-flow-cycles-with-steven-kotler/

**Evidence strength:** Practitioner framework built on Csikszentmihalyi, not a controlled trial — the struggle→flow sequence is widely reported, the "guarantee" mechanism is Doris's reasoned claim, untested in isolation.

**Feature for Monolith — sell the guarantee, harden the first 15 minutes:** (a) At arming, the one thing the Session Room states, large, is the promise itself: **this block will not end early — no peek, no pause, no exception path exists.** Certainty is the product. (b) Minutes 0–15 of a block are a visually distinct **Struggle phase** on the block screen (different texture/colour state, no other change) — so when it's hard at minute 8, the screen's state silently says "this is the phase working as designed," without a word of coaching. Emergency-break affordance stays at its weekly ration and 60-second hold, but offers no shortcut variant during struggle.

## 3. Attention residue + the ready-to-resume plan — a 20-second note before a break protects the *next* block

**The science:** Sophie Leroy (2009): switching away from an **unfinished** task leaves part of your attention stuck on it, measurably degrading performance on the next task. Leroy & Glomb (2018, four studies): the damage is largely cancelled by a brief **"ready-to-resume" plan** — before switching, note where you are and what the next step is. The brain lets go of a task it trusts it can re-enter.

**Sources:**
- https://www.uwb.edu/news/2024/05/17/an-ongoing-study-on-the-success-of-staying-focused
- https://aakinshin.net/library/leroy2009/

**Evidence strength:** Strong for a lab literature — original experiments plus a four-study mitigation paper in *Organization Science*; tasks were lab/work tasks, not NEET study, so transfer is plausible, not proven.

**Feature for Monolith — the Resume Card:** A block cannot slip into its break until he leaves **one line**: where he stopped + the next action ("Optics Q14 — ray diagram first"). It is not saved to any history, log, or analytics. It exists for exactly one purpose: it is the **only text on the block screen when the next block arms**, then it is gone. Same mechanic in reverse justifies zero mid-block peeks: a peek isn't a 30-second cost, it's residue smeared across the rest of the block — which is an argument for Monolith's absolute hold that needs no moralising, just physics.

## 4. Brain drain — the phone taxes his brain even face-down, off, and untouched

**The science:** Ward et al. (2017, ~800 participants): people with their phone **in another room** significantly outperformed people with it face-down on the desk on working-memory / cognitive-capacity tests; pocket/bag sat in between. On/off and face up/down made no difference — the cost is the brain spending resources *not* checking it, worst in the most phone-dependent users.

**Sources:**
- https://news.utexas.edu/2017/06/26/the-mere-presence-of-your-smartphone-reduces-brain-power/?query-page=529
- https://www.bps.org.uk/research-digest/revisiting-brain-drain-effect

**Evidence strength:** Honest downgrade: famous and well-powered originally, but a later partial replication (Hartmann et al.) found no effect on other memory measures — treat as "presence probably costs something," not settled law.

**Feature for Monolith — make the phone stop looking like a phone:** He studies *with* this device in the room, so Monolith can't remove it — it can remove its salience. During a block: full-screen launcher surface with **no app icons, no badges, no notification previews, no clock-app clutter**; allowed apps live on a *separate* screen (his launcher ruling), so the resting surface is a near-blank slab — a study object, not a phone. The design target is Ward inverted: if a visible phone drains capacity, a phone visually demoted to furniture should drain less.

## 5. Implementation intentions — a pre-written if–then in *his own words* beats any in-the-moment warning

**The science:** Gollwitzer's implementation intentions: a goal ("study") underperforms a pre-committed plan ("**If** situation X arises, **then** I do Y") because the cue, not willpower, triggers the response. Gollwitzer & Sheeran (2006) meta-analysis: **94 tests, effect d = 0.65** on goal attainment, with the largest effects on *getting started* (d ≈ .61) and *not getting derailed* (d ≈ .77) — exactly a blocker's two failure points. A 2025 synthesis pooling 642 tests confirms the effect holds when the plan is in true if–then format and rehearsed at least once.

**Sources:**
- http://dccps.cancer.gov/sites/default/files/2020-06/goal_intent_attain.pdf
- https://psychology.com/tools/implementation-intentions

**Evidence strength:** Among the strongest in this file — large, replicated meta-analytic effect for a two-minute intervention; caveat: most trials run days-to-weeks, not a full drop year.

**Feature for Monolith — the Arming Contract:** At setup (once, editable, never nagged again), he writes **one** if–then in his own words: *"If I reach for Instagram during a block, then I put the phone face-down and go back to the question I was on."* Monolith stores no behaviour data from it. Its only appearance: **verbatim, in his words, as the single line on the block screen when that exact app is attempted.** The app never lectures him — *past-him* does, in the format the research says actually fires. One plan only: the literature warns a stack of plans dilutes the cue link.

## 6. 50 minutes is a ceiling, not a floor — vigilance collapses inside a single unbroken block

**The science:** Ariga & Lleras (2011, *Cognition*, n=84): on a 50-minute repetitive task, performance declined steadily for everyone — **except** the group given just *two* brief diversions (responding to memorised digits), whose performance showed **no drop at all**. Their account: attention fails not from a drained resource tank but because the goal deactivates; briefly deactivating and reactivating the goal keeps it live. Prolonged unbroken attention is itself the problem.

**Sources:**
- http://www.sciencedaily.com/releases/2011/02/110208131529.htm
- https://medicalxpress.com/news/2011-02-diversions-vastly-focus.html

**Evidence strength:** One clean, much-cited lab experiment on a deliberately tedious computer task — direction is trustworthy, exact transfer to MCQ practice is not measured.

**Feature for Monolith — validate the 50, and put the reset *inside* it, phone-free:** (a) Never offer 75/90-minute block lengths as an "upgrade" — at 12 h/day, the science says the 50-minute unit is already at the vigilance edge; longer blocks buy fatigue, not depth. (b) At ~minute 25, the block permits a **60-second eyes-off reset that never unlocks anything**: block screen shifts to a distant-focus / blank state ("look far" is the whole instruction, as a visual change, not text coaching), then returns. A goal deactivation–reactivation beat that costs no hold integrity — the phone stays a slab throughout.

## 7. Breaks restore *vigour*, not performance — so a break that includes the phone is not a break

**The science:** Albulescu et al. (2022, *PLOS ONE*) meta-analysis of micro-breaks (≤10 min): 22 samples, N=2,335. Breaks significantly **boosted vigour (d = .36) and reduced fatigue (d = .35)** — but overall performance effect was **non-significant (d = .16)**, longer breaks performed better, and highly cognitively demanding work likely needs **more than 10 minutes** to recover. Companion protocol, weaker evidence: Huberman's NSDR (non-sleep deep rest / yoga nidra) after learning — the study he cites is a single PET study (Kjaer et al., 2002) in experienced practitioners doing an hour-long session, showing increased striatal dopamine availability; the popular "20 minutes after studying boosts retention ~50%" version outruns that evidence.

**Sources:**
- https://journals.plos.org/plosone/article?id=10.1371/journal.pone.0272460
- https://ai.hubermanlab.com/s/i9f46ohE

**Evidence strength:** Micro-break wellbeing effect: solid meta-analysis, small effects. NSDR: promising, thin — one small imaging study plus practitioner extrapolation; do not build claims on it.

**Feature for Monolith — the Locked Break:** Between blocks, the phone **stays blocked for the full break** (10 min standard, longer after double blocks). A break that opens Instagram is Albulescu's control condition, not a break. The break surface is near-empty: a timer, the Resume Card (Find 3), nothing to tap. After every *second* consecutive block, offer — never push — a 20-minute **NSDR break variant**: same locked screen, audio body-scan allowed-app exception, phone face-down. Sold honestly in-app by saying nothing at all: it's just a longer, darker break. The vigour math is what makes a 12-hour day survivable; performance comes from the blocks, recovery is the break's only job.

## 8. One clear proximal goal is a flow precondition — so arming should require exactly one

**The science:** Csikszentmihalyi, Abuhamdeh & Nakamura (APA *Handbook of Competence and Motivation*, 2005), summarising decades of experience-sampling work: flow requires three conditions together — **clear (proximal) goals, immediate feedback, and challenge–skill balance**. Vague goals ("study Bio") fail the first condition structurally; a goal sized to the next stretch of work ("this block: 40 Cell Cycle MCQs") satisfies it, and the questions themselves supply the immediate feedback a blocker app never could.

**Source:**
- https://psycnet.apa.org/record/2005-08058-032

**Evidence strength:** Foundational theory with extensive experience-sampling support for the *conditions*; it describes when flow happens, it does not prove that forcing a goal-entry field causes it.

**Feature for Monolith — No Goal, No Arm:** The Session Room cannot arm a block until **one proximal goal for that block** is set — typed in a line or picked from his own recent wordings (kept only as reusable phrasing shortcuts, never as a study log). That line becomes the block/launcher screen's sole content alongside the phase state. Pair it with the #20 check-in's second use: on a blocked attempt, below the urge rating, one optional tap — **Too easy / Matched / Too hard** — which changes nothing in the app and records nothing; it exists because noticing challenge–skill mismatch *is* the flow skill, and the next block's goal is where he'll act on it. Sung's related observation fits here as a design note, not a feature: he treats *sleepiness during study as a signal of passive, low-encoding work* — if the tiredness check-in (Find 1's sibling) reads high repeatedly within a block, the honest app response is Find 7's real break, not a harder hold.

---

### The through-line for #20

Ask **one question, only at the moment of an attempt, and make the answer change what the screen does**: urge strength → Surf Hold (Find 1); tiredness → legitimate locked break instead of a shame-hold (Finds 7–8); challenge mismatch → nothing but his own awareness, carried into the next goal (Find 8). A check-in that stores answers is tracking. A check-in that *acts* on them is a blocker doing its job.

— Strom (Scout 9)
