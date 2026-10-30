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
## FINISH pass — missing seats seated by the finish lead (LEAD-COVERED, signed)

The full-roster relay (Feel specialists ×3 + Keeper of Record) died in the
infrastructure restart that killed the first bundle lead. Per Wave-3 law a thin
seat is covered by the lead and marked LEAD-COVERED — these four seats were run
by the finish lead directly over the landed code @1987779 (branch tip 2d6f866),
review-only, no code changed by this section.

### Feel specialist 1 — cue texture / timing (LEAD-COVERED, finish lead)
- Diff-verified: commit 1987779 touches NO seal constant, timing or render —
  SEAL_THROW_TIMINGS/AMPLITUDES, PARTIAL_WEIGHTS, SEAL_SAMPLES and
  renderSealBuffer are byte-identical; the knock/click/detent bodies are
  unchanged. The seal still speaks with its single settled voice and the quiet
  gate knocks are still contact sounds, exactly as Q6 binds.
- The only audible deltas are gates, not voices: (a) an incidental cue whose
  audio-focus request is DENIED is now dropped instead of barging in — silence
  in a moment the phone has given to someone else (a call), never a thinner
  cue; (b) the legacy AudioTrack is now built on the synth thread at the first
  granted cue, so the very first knock of a process may land a few ms later
  than the old pre-played track — off the UI thread, inside the existing queue,
  imperceptible in the hand. No cue removed, no envelope reshaped.

### Feel specialist 2 — stop behaviour, heard/felt right after stopAllAudio (LEAD-COVERED, finish lead)
- After stopAllAudio: NOTHING further is heard or felt. Both tracks are
  stopped+released (the release hard-cuts any in-flight cue tail rather than
  letting its ≤160 ms write finish), the vibrator is cancelled mid-pattern,
  audio focus is abandoned, both threads are interrupted out of their 500 ms
  polls, and the engine leaves the live registry so a later settings push
  cannot resurrect it.
- Every public entry point is gated on `running` (waveform, one-shot haptic,
  enqueue, prime, Throw — and tear/completion pulses route through those same
  gated helpers), so a late tap, a late Throw or a stale caller produces
  silence, not a zombie knock. Teardown is final and idempotent: MainActivity
  and the ViewModel can both call it on the shared engine safely.

### Feel specialist 3 — settings feel, mid-session toggle honesty (LEAD-COVERED, finish lead)
- Toggling SOUND mid-session now reaches the synth thread on its very next
  command: the gate is @Volatile, so the happens-before chain (fact store →
  synchronized push → volatile read) is sound; before, a toggle could in
  principle never be seen by the sound thread. A cue already mid-write is not
  retro-muted — it finishes its ≤160 ms tail or is cut by the next command's
  gate; no half-heard lies beyond one in-flight knock.
- The confirmation detent is now honest: toggleSound prints the PERSISTED fact
  and plays the ON detent only when the disk actually took the setting. On a
  failed commit the screen keeps showing the true saved value instead of
  cheerfully ticking for a setting that would silently revert at process death.
- Haptics feel is unchanged in the hand (default ON, as shipped) — it simply
  has a real persisted fact behind it now, so a future PARK haptics switch
  needs zero engine work.

### Keeper of Record — disposition ledger reconciliation (LEAD-COVERED, finish lead)
- Issue body: 5/5 findings dispositioned [x] FIXED-IN-CODE @1987779 with
  evidence notes — reconciled line-by-line against the landed diff by the
  finish lead: F1 focus (verdict returned, listener on both paths, API 24–25
  legacy path, abandon at teardown, seal fallback requests) HOLDS; F2
  stopAllAudio totality (both tracks, focus, vibrator, threads, registry,
  running-gates on every entry point) HOLDS; F3 lazy lanes/track (construction
  starts nothing; track built only for a granted sounding command) HOLDS; F4
  @Volatile + internal set + resyncSoundEnabledFromFact (grep: the ONLY writes
  to soundEnabled/hapticsEnabled outside the fact pushes are none —
  SessionSealTransition direct write is gone) HOLDS; F5 commit verdict
  honoured + haptics persisted fact HOLDS. No finding struck, none blocked.
- DECISION-NEEDED: NONE. No cure cheapens sound or feel (see Feel seat 1), so
  the no-downgrade rule sends nothing to Sarthak. Impact flags: the IMPACT
  section above stands as written — re-read and confirmed plain-word accurate
  by the finish lead.
- 20 seats: present, one line each, in the section above (internal lens
  passes, bundle-signed). The 4 relay seats missing after the restart are the
  ones in THIS appendix, LEAD-COVERED and signed by the finish lead.
- CI / merge record: run 40974971942 (branch tip 2d6f866): assembleDebug
  SUCCESS, unit tests EXECUTED — 263 tests completed, 37 failed. The 37-name
  failed set is BYTE-IDENTICAL to main's own CI run 40973388591 on main
  @73aaeae (258 tests, same 37 failures, incl. the 4 pre-existing
  SoundAndSocialTruthTest failures SocialBoardWordFollowsTheToggle,
  SoundSettingPersistsAcrossProcessDeath, SocialToggleRenderedAndWritesThrough,
  SoundEngineBirthOrder) — ZERO failures are #49-owned, and all 5 new #49
  tests pass (263 = 258 + 5, none in the failed set). Earlier branch runs
  40973093859 (@1987779) and 40974172273 (@b681035) failed at test-COMPILE on
  the since-healed pre-existing ParkDrawer breakage recorded above. Full
  pre-existing failed set (37): BreakMigrationTest ×1, CascadeDeckRenderTest
  ×5, ChromeWaveUnitTest ×1, DrawerRenderTest ×7, ScreenshotHarnessTest ×14,
  ShortsSniperTruthTest ×1, SoundAndSocialTruthTest ×4 (named above),
  TetradRenderTest ×1, TruthCar3SweepRoboTest ×2, TruthCar3SweepTest ×1.
  Closure + merge + post-merge main CI: recorded in the closing comment on
  issue #49 (finish pass, same sitting).
