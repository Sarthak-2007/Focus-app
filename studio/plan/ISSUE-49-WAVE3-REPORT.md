# ISSUE #49 — sound-engine · Wave-3 report (lead: sound-engine bundle, under PULS)

Base: origin/main e29acdf. All 5 audit findings verified against CURRENT code before fixing.
NOTE: the dispatch brief's paraphrased findings (SoundPool.load, BodyThock/ResoThock
note pairs, fire-and-forget SoundPool throws) do NOT exist in this tree — grep for
SoundPool / BodyThock / ResoThock over app/src returns ZERO hits; the engine is a
Float-PCM AudioTrack synthesizer. Those paraphrases are VERIFIED-FALSE for this
codebase; dispositions below follow the actual issue body / AUDIT-R1 slice.

## Verdicts
1. CRITICAL audio-focus theatre — STANDS→FIXED. Verified: request result discarded
   (old :533), no listener, no abandon anywhere, SDK>=O only with minSdk 24.
   Cure: requestTransientAudioFocus() returns granted/denied (denied incidental
   cues dropped in the synth loop), OnAudioFocusChangeListener tracks loss,
   legacy requestAudioFocus path for API 24–25, abandonAudioFocus() at teardown,
   un-primed seal fallback now requests focus before play. Q6 respected: the
   seal voice/knocks are untouched — no sound redesigned.
2. HIGH stopAllAudio incomplete — STANDS→FIXED. Verified: legacy track, focus,
   threads, registry all escaped; playSealThrow could resurrect a seal track.
   Cure: running gates every entry point (waveform, one-shot, enqueue, prime,
   Throw), both tracks stopped/released, focus abandoned, vibrator cancelled,
   both threads interrupted, engine unregisters from liveEngines. Idempotent.
3. HIGH eager threads + playing track, owners never shut down — STANDS→FIXED.
   Verified: init started 2 threads; synth loop built+played an AudioTrack
   before any command; repository/seal engines have no teardown caller.
   Cure: lazy lanes (ensureThreadsStarted on first real cue) + AudioTrack built
   only for a granted, sounding command. An unplayed engine now costs zero
   threads/tracks, so process-lived owners (repository/seal singleton) hold
   nothing while idle; owners that do tear down get the now-total stopAllAudio.
4. HIGH soundEnabled non-volatile public var + direct-write bypass — FIXED.
   @Volatile + internal set; SessionSealTransition's direct write replaced by
   engine.resyncSoundEnabledFromFact(); only the fact store's push writes it.
5. MEDIUM commit() ignored + hapticsEnabled stub — FIXED. setEnabled returns
   the commit verdict and pushes only on success; MonolithViewModel.toggleSound
   prints the persisted fact on failure. hapticsEnabled is now a persisted fact
   (SoundEffectsSetting.is/setHapticsEnabled, default ON = shipped behaviour),
   @Volatile, born from the fact, pushed to live engines like sound.

Tests: 5 new discriminating tests in SoundAndSocialTruthTest (lazy construction,
volatile gates via reflection, total stop + registry removal + silence,
haptics-as-fact, commit-verdict return).

## 20 seats (internal lens passes, one line each)
- Crazy-ideas: proposed focus-aware ducking of knocks under transient loss — rejected as scope creep; drop-on-denial is the honest gate.
- Maximum-implementation-potential: lazy lanes + lazy track removes the whole eager-cost class, not just the cited lines.
- Brutal critic: the brief paraphrase described a SoundPool engine that does not exist — flagged, dispositions follow the issue body only.
- Improver: commit-verdict now propagates to the VM print, closing the silent-revert loop end to end.
- Designer: no audible change — Q6 settled voices (seal Throw, quiet knocks) byte-identical; feel preserved by construction.
- Consumer: toggling sound OFF in PARK now actually reaches routine-born seals on every thread, guaranteed by @Volatile.
- Student: code comments cite issue #49 at every cure so the next reader finds the why without the audit PDF.
- User: a denied audio-focus (e.g. during a call) no longer lets knocks fight the call; the seal act still sounds.
- User-experience: stop on Activity destroy is now truly silent — no post-destroy vibration or resurrected seal track.
- Purpose-questioning: asked why two engines exist at all — process-lived seal/repository engines are by design; laziness makes them free.
- Thoughtful: stopAllAudio made idempotent and teardown final, because MainActivity + ViewModel both call it on the same engine.
- Deep-thinker: happens-before chain audited — write (push, synchronized) → @Volatile read (synth thread) is now sound.
- Enthusiastic designer: haptics-as-fact opens a future PARK haptics switch with zero engine work — store + push already exist.
- Wow-level designer: no visual surface in scope; the wow is an engine that costs nothing until it speaks.
- Designer-29-years: legacy API 24–25 focus path added with deprecation suppression — minSdk honesty over convenience.
- Intentionality designer: seal fallback requests focus but proceeds — the seal is the user's own act, incidental knocks are not; distinction documented in code.
- Synchronous collaboration: diff kept checkbox-scoped and surgical for the batch-10 rebase; no other issue's files touched except the two write-site owners (VM, seal transition).
- Implementation-quality: single cure per finding at the engine boundary — no call-site try/catch scatter.
- Technical execution: smart-cast and double-release hazards reviewed (liveTrack seam, try/catch around every track op, finally-block registry cleanup).
- Interaction-detail: toggle's confirmation detent now fires only when the persisted fact truly says ON.

## CI verdict (run 40973093859, tip 1987779)
- Assemble debug: PASSED — all main-source changes (engine, setting, VM, seal transition) compile.
- Unit tests: FAILED AT TEST-COMPILE on PRE-EXISTING breakage in origin/main, none in
  this diff. Errors (all "No value passed for parameter" / unresolved compose assertions
  against ParkDrawer params added by other lanes): ScreenshotHarnessTest.kt:350,
  ShortsToggleRenderTest.kt:66, SoundAndSocialTruthTest.kt:464 (pre-existing
  setParkContent call, untouched by this diff), TruthCar3CaptureBodyTest.kt:70,
  TruthCar3SweepTest.kt:6,7,314,346. Proof of pre-existence: main's own completed CI
  runs 40970102642 and 40969279638 are also `failure`. No error references any line
  added by this branch; the 5 new tests compile clean within their file's error set.
  Per Wave-3 law these out-of-scope failures are recorded, not fixed here
  (they belong to the ParkDrawer-param issues' branches / the merge train rebase).

## IMPACT ON THE APP (plain words, for the person holding the phone)
- Sounds now stop COMPLETELY when the app/session tears down — before, the main
  sound track, the vibration and the audio focus could keep living after the
  screen that owned them was gone; a stopped engine could even play again.
- Tapping things no longer fights other audio: if the phone denies audio focus
  (e.g. during a call), the little incidental knocks stay quiet instead of
  barging in. The seal's Throw still sounds — it is the act itself.
- On phones running Android 7.0/7.1 (API 24–25), taps now request audio focus
  properly; before they got none at all.
- The app does less invisible work: merely existing (a background routine
  process, an unused break engine) no longer spins up two threads and a playing
  sound track. Sound machinery wakes only when a sound is actually played —
  less battery/CPU burn while idle.
- Turning SOUND off/on in PARK is now guaranteed to reach every sound the app
  can make, on every thread, immediately — and if the setting ever failed to
  save to disk, the screen would keep showing the true saved value instead of
  lying until the app restarted.
- NO sound changed in character: the quiet gate knocks and the seal's single
  voice are exactly as before (Q6 settled law). No cue removed, nothing made
  thinner, haptics not weakened — haptics simply gained a real on/off fact
  behind the scenes (still ON, as shipped). No downgrade in this issue.

## Merge note (Oct 30, ~05:10 IST)
Merged origin/main 73aaeae (issue-2 landing) into agent/issue-49 — clean merge,
no conflicts. That landing also healed main's pre-existing test-compile breakage
(the ParkDrawer call sites), so this branch's CI re-run is the first where the
unit tests can actually execute.
