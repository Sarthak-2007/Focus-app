(function () {
  "use strict";

  const state = {
    minutes: 45,
    isArmed: false,
    remainingSeconds: 45 * 60,
    targetSeconds: 45 * 60,
    focusedMinutes: 75,
    transmutedCount: 6,
    soundEnabled: true,
    serialTaps: 0,
    orbTaps: 0,
    lissajousPhase: 0.6,
    freqA: 3.0,
    freqB: 2.0,
    apps: [
      { name: "TikTok", pkg: "com.zhiliaoapp.musically", category: "SHORT_VIDEO", blocked: true },
      { name: "Instagram", pkg: "com.instagram.android", category: "SHORT_VIDEO", blocked: true },
      { name: "YouTube Shorts", pkg: "com.google.android.youtube", category: "SHORT_VIDEO", blocked: true },
      { name: "X (Twitter)", pkg: "com.twitter.android", category: "SOCIAL", blocked: true },
      { name: "Reddit", pkg: "com.reddit.frontpage", category: "SOCIAL", blocked: true },
      { name: "Clash Royale", pkg: "com.supercell.clashroyale", category: "GAMES", blocked: false },
      { name: "Discord", pkg: "com.discord", category: "MESSAGING", blocked: false }
    ]
  };

  let audioCtx = null;
  function playTone(freq, durationMs, type) {
    if (!state.soundEnabled) return;
    try {
      if (!audioCtx) {
        const AudioContextClass = window.AudioContext || window.webkitAudioContext;
        if (AudioContextClass) audioCtx = new AudioContextClass();
      }
      if (!audioCtx) return;
      const osc = audioCtx.createOscillator();
      const gain = audioCtx.createGain();
      osc.type = type || "sine";
      osc.frequency.setValueAtTime(freq, audioCtx.currentTime);
      gain.gain.setValueAtTime(0.16, audioCtx.currentTime);
      gain.gain.exponentialRampToValueAtTime(0.001, audioCtx.currentTime + durationMs / 1000);
      osc.connect(gain);
      gain.connect(audioCtx.destination);
      osc.start();
      osc.stop(audioCtx.currentTime + durationMs / 1000);
    } catch (_) {}
  }

  // Elements
  const rotaryCanvas = document.getElementById("rotaryCanvas");
  const rCtx = rotaryCanvas.getContext("2d");
  const artifactCanvas = document.getElementById("artifactCanvas");
  const aCtx = artifactCanvas.getContext("2d");
  const orbCanvas = document.getElementById("airlockOrbCanvas");
  const oCtx = orbCanvas.getContext("2d");

  const dialTimeReadout = document.getElementById("dialTimeReadout");
  const dialModeLabel = document.getElementById("dialModeLabel");
  const dialMetaLabel = document.getElementById("dialMetaLabel");
  const armSwitchBtn = document.getElementById("armSwitchBtn");
  const statusDot = document.getElementById("statusDot");
  const sealedCountVal = document.getElementById("sealedCountVal");
  const transmutedVal = document.getElementById("transmutedVal");
  const appListContainer = document.getElementById("appListContainer");
  const intentionInput = document.getElementById("intentionInput");
  const airlockModal = document.getElementById("airlockModal");
  const orbTapHint = document.getElementById("orbTapHint");

  function renderApps() {
    appListContainer.innerHTML = "";
    const sealed = state.apps.filter((a) => a.blocked).length;
    sealedCountVal.textContent = String(sealed);

    state.apps.forEach((app, index) => {
      const row = document.createElement("div");
      row.className = "app-row" + (app.blocked ? " blocked" : "");
      row.innerHTML =
        '<div class="app-info">' +
        '<span class="app-title">' + app.name + "</span>" +
        '<span class="app-pkg">' + app.pkg + " • " + app.category + "</span>" +
        "</div>" +
        '<span class="app-pill">' + (app.blocked ? "SEALED" : "OPEN") + "</span>";
      row.addEventListener("click", function () {
        state.apps[index].blocked = !state.apps[index].blocked;
        playTone(state.apps[index].blocked ? 540 : 360, 45, "triangle");
        if (window.MonolithBridge && window.MonolithBridge.onToggleAppFromWeb) {
          window.MonolithBridge.onToggleAppFromWeb(app.pkg);
        }
        renderApps();
      });
      appListContainer.appendChild(row);
    });
  }

  function updateReadout() {
    if (state.isArmed) {
      const m = Math.floor(state.remainingSeconds / 60);
      const s = state.remainingSeconds % 60;
      dialTimeReadout.textContent =
        String(m).padStart(2, "0") + ":" + String(s).padStart(2, "0");
      dialModeLabel.textContent = "MONOTONIC LOCK ACTIVE";
      dialMetaLabel.textContent = "ZERO DRIFT // SHIELD ARMED";
      armSwitchBtn.textContent = "DISARM MONOLITH SHIELD";
      statusDot.classList.add("armed");
    } else {
      dialTimeReadout.textContent = String(state.minutes).padStart(2, "0") + ":00";
      dialModeLabel.textContent = state.minutes === 42 || state.minutes === 90 ? "HARMONIC RESONANCE" : "ROTATE BEZEL TO SET";
      dialMetaLabel.textContent = state.minutes + " MIN // HARMONIC SEAL";
      armSwitchBtn.textContent = "ARM MONOLITH SHIELD (" + state.minutes + "M)";
      statusDot.classList.remove("armed");
    }
  }

  function drawRotaryDial(timestamp) {
    const w = rotaryCanvas.width;
    const h = rotaryCanvas.height;
    const cx = w / 2;
    const cy = h / 2;
    const outerR = w / 2 - 10;
    const trackR = outerR - 18;
    const coreR = trackR - 22;

    rCtx.clearRect(0, 0, w, h);

    // Outer Bezel
    rCtx.beginPath();
    rCtx.arc(cx, cy, outerR, 0, Math.PI * 2);
    rCtx.fillStyle = "#1E212B";
    rCtx.fill();
    rCtx.lineWidth = 2;
    rCtx.strokeStyle = state.isArmed ? "#FF4F18" : "#323748";
    rCtx.stroke();

    const progress = state.isArmed
      ? state.remainingSeconds / Math.max(1, state.targetSeconds)
      : state.minutes / 120;

    // 60 Engraved Ticks
    for (let i = 0; i < 60; i++) {
      const frac = i / 60;
      const angle = (frac * 360 - 90) * (Math.PI / 180);
      const isMajor = i % 5 === 0;
      const r1 = outerR - 6;
      const r2 = r1 - (isMajor ? 10 : 5);
      rCtx.beginPath();
      rCtx.moveTo(cx + r1 * Math.cos(angle), cy + r1 * Math.sin(angle));
      rCtx.lineTo(cx + r2 * Math.cos(angle), cy + r2 * Math.sin(angle));
      rCtx.lineWidth = isMajor ? 2.2 : 1.2;
      rCtx.strokeStyle = frac <= progress
        ? (state.isArmed ? "#FF4F18" : "#3B68FF")
        : "#646878";
      rCtx.stroke();
    }

    // Progress Arc
    const startAngle = -Math.PI / 2;
    const endAngle = startAngle + progress * Math.PI * 2;
    rCtx.beginPath();
    rCtx.arc(cx, cy, trackR, startAngle, endAngle);
    rCtx.lineWidth = 6;
    rCtx.lineCap = "round";
    rCtx.strokeStyle = "#FF4F18";
    rCtx.stroke();

    // Thumb Knob
    const tx = cx + trackR * Math.cos(endAngle);
    const ty = cy + trackR * Math.sin(endAngle);
    rCtx.beginPath();
    rCtx.arc(tx, ty, 8, 0, Math.PI * 2);
    rCtx.fillStyle = "#F4F1EA";
    rCtx.fill();

    // Inner Lissajous Wave
    rCtx.beginPath();
    const phase = (timestamp || 0) * 0.002;
    for (let s = 0; s <= 100; s++) {
      const t = (s / 100) * Math.PI * 2;
      const x = cx + coreR * 0.7 * Math.sin(3 * t + phase);
      const y = cy + coreR * 0.32 * Math.sin(2 * t);
      if (s === 0) rCtx.moveTo(x, y);
      else rCtx.lineTo(x, y);
    }
    rCtx.lineWidth = 1.5;
    rCtx.strokeStyle = "rgba(255, 79, 24, 0.28)";
    rCtx.stroke();
  }

  function drawArtifactCanvas(timestamp) {
    const w = artifactCanvas.width;
    const h = artifactCanvas.height;
    const cx = w / 2;
    const cy = h / 2;
    aCtx.clearRect(0, 0, w, h);

    const tShift = (timestamp || 0) * 0.0015 + state.lissajousPhase;
    aCtx.beginPath();
    for (let i = 0; i <= 220; i++) {
      const t = (i / 220) * Math.PI * 2;
      const x = cx + (w * 0.36) * Math.sin(state.freqA * t + tShift);
      const y = cy + (h * 0.36) * Math.sin(state.freqB * t);
      if (i === 0) aCtx.moveTo(x, y);
      else aCtx.lineTo(x, y);
    }
    aCtx.lineWidth = 2.4;
    aCtx.strokeStyle = "#FF4F18";
    aCtx.stroke();
  }

  function drawAirlockOrb(timestamp) {
    if (airlockModal.classList.contains("hidden")) return;
    const w = orbCanvas.width;
    const h = orbCanvas.height;
    const cx = w / 2;
    const cy = h / 2;
    oCtx.clearRect(0, 0, w, h);

    const pulse = 0.85 + 0.15 * Math.sin((timestamp || 0) * 0.004);
    const r = 64 * pulse;
    oCtx.beginPath();
    oCtx.arc(cx, cy, r, 0, Math.PI * 2);
    oCtx.lineWidth = 3;
    oCtx.strokeStyle = "#FF4F18";
    oCtx.stroke();

    const nodes = 3 + state.orbTaps;
    for (let i = 0; i < nodes; i++) {
      const a = (timestamp || 0) * 0.003 + (i * Math.PI * 2) / nodes;
      const nx = cx + 42 * Math.cos(a);
      const ny = cy + 42 * Math.sin(a);
      oCtx.beginPath();
      oCtx.arc(nx, ny, 5, 0, Math.PI * 2);
      oCtx.fillStyle = "#FFB020";
      oCtx.fill();
    }
  }

  function handleDialPointer(clientX, clientY) {
    if (state.isArmed) return;
    const rect = rotaryCanvas.getBoundingClientRect();
    const cx = rect.left + rect.width / 2;
    const cy = rect.top + rect.height / 2;
    let deg = (Math.atan2(clientY - cy, clientX - cx) * 180) / Math.PI + 90;
    if (deg < 0) deg += 360;
    const mins = Math.max(5, Math.min(120, Math.round(((deg / 360) * 120) / 5) * 5));
    if (mins !== state.minutes) {
      state.minutes = mins;
      playTone(420 + mins * 3, 20, "sine");
      updateReadout();
    }
  }

  let draggingDial = false;
  rotaryCanvas.addEventListener("pointerdown", function (e) {
    draggingDial = true;
    handleDialPointer(e.clientX, e.clientY);
  });
  window.addEventListener("pointermove", function (e) {
    if (draggingDial) handleDialPointer(e.clientX, e.clientY);
  });
  window.addEventListener("pointerup", function () {
    draggingDial = false;
  });

  artifactCanvas.addEventListener("pointermove", function (e) {
    if (e.buttons > 0 || e.pointerType === "touch") {
      state.lissajousPhase += 0.08;
    }
  });

  document.querySelectorAll(".chip").forEach(function (btn) {
    btn.addEventListener("click", function () {
      if (state.isArmed) return;
      document.querySelectorAll(".chip").forEach((c) => c.classList.remove("active"));
      btn.classList.add("active");
      state.minutes = parseInt(btn.getAttribute("data-min"), 10);
      playTone(600, 30, "sine");
      updateReadout();
    });
  });

  armSwitchBtn.addEventListener("click", function () {
    state.isArmed = !state.isArmed;
    if (state.isArmed) {
      state.targetSeconds = state.minutes * 60;
      state.remainingSeconds = state.targetSeconds;
      playTone(520, 180, "triangle");
      if (window.MonolithBridge && window.MonolithBridge.onArmSessionFromWeb) {
        window.MonolithBridge.onArmSessionFromWeb(intentionInput.value, state.minutes);
      }
    } else {
      playTone(340, 140, "sine");
      if (window.MonolithBridge && window.MonolithBridge.onDisarmSessionFromWeb) {
        window.MonolithBridge.onDisarmSessionFromWeb();
      }
    }
    updateReadout();
  });

  document.getElementById("simulateInterceptBtn").addEventListener("click", function () {
    state.orbTaps = 0;
    orbTapHint.textContent = "Tap the Resonance Core 3 times to stabilize impulse (0/3)";
    document.getElementById("interceptedIntentionQuote").textContent =
      '"' + intentionInput.value + '"';
    airlockModal.classList.remove("hidden");
    playTone(480, 90, "triangle");
  });

  orbCanvas.addEventListener("click", function () {
    state.orbTaps += 1;
    playTone(480 + state.orbTaps * 60, 50, "sine");
    orbTapHint.textContent =
      "Tap the Resonance Core 3 times to stabilize impulse (" + state.orbTaps + "/3)";
  });

  document.getElementById("transmuteReturnBtn").addEventListener("click", function () {
    state.transmutedCount += 1;
    transmutedVal.textContent = String(state.transmutedCount);
    airlockModal.classList.add("hidden");
    playTone(660, 160, "triangle");
    if (window.MonolithBridge && window.MonolithBridge.onTransmuteFromWeb) {
      window.MonolithBridge.onTransmuteFromWeb();
    }
  });

  document.getElementById("serialPlate").addEventListener("click", function () {
    state.serialTaps += 1;
    playTone(500 + state.serialTaps * 80, 30, "sine");
    if (state.serialTaps >= 5) {
      state.serialTaps = 0;
      document.getElementById("synthBar").classList.toggle("hidden");
    }
  });

  document.querySelectorAll(".synth-key").forEach(function (key) {
    key.addEventListener("click", function () {
      const f = parseFloat(key.getAttribute("data-freq"));
      playTone(f, 240, "triangle");
    });
  });

  document.getElementById("audioToggleBtn").addEventListener("click", function (e) {
    state.soundEnabled = !state.soundEnabled;
    e.target.textContent = state.soundEnabled ? "SND: ON" : "SND: OFF";
  });

  document.getElementById("chassisThemeBtn").addEventListener("click", function (e) {
    document.body.classList.toggle("titanium-mode");
    const isLight = document.body.classList.contains("titanium-mode");
    e.target.textContent = isLight ? "OBSIDIAN" : "TITANIUM";
    playTone(500, 35, "sine");
  });

  setInterval(function () {
    if (state.isArmed && state.remainingSeconds > 0) {
      state.remainingSeconds -= 1;
      updateReadout();
    }
  }, 1000);

  function loop(ts) {
    drawRotaryDial(ts);
    drawArtifactCanvas(ts);
    drawAirlockOrb(ts);
    requestAnimationFrame(loop);
  }

  renderApps();
  updateReadout();
  requestAnimationFrame(loop);
})();
