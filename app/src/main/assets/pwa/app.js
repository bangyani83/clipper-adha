(() => {
  const MONTHLY_QUOTA = 1000;
  const STORAGE_KEY = 'clipper_adha_pro_offline_v2';

  const CAPTION_STYLES = [
    'Hormozi Bold', 'MrBeast Pop', 'Podcast Clean', 'Gaming Neon', 'Gold Luxury',
    'TikTok Punch', 'Dakwah Emas', 'Cyber Glitch', 'Minimal Serif', 'Komik Box',
    'Youtube SEO', 'Reels Estetik', 'Esports HUD', 'Rage Crimson', 'Royal Violet',
    'Jualan Flash', 'News Ticker', 'Karaoke Lime', 'Soft Pastel', 'Studio Mono',
    'Headline Impact', 'Clipper Signature', 'Kajian Tenang', 'Live Promo',
    'Valorant Strike', 'MLBB Victory', 'Dark Whisper', 'Hyper Bounce',
    'Storyteller', 'Rust Terminal', 'Midnight Glow', 'Feed Square Clean'
  ];

  const TEMPLATE_CATEGORIES = ['Podcast', 'Gaming', 'Dakwah', 'Jualan'];
  const TEMPLATES = [];
  let tIdx = 1;
  TEMPLATE_CATEGORIES.forEach((cat) => {
    ['Hormozi Viral', 'Dual Speaker', 'Hook Emas', 'Split Dinamis', 'Neon Pulse', 'Minimalis Pro', 'Fast Cut', 'Spotlight', 'Storytelling', 'Clean Frame', 'High Retention', 'Cinematic', 'Zoom Punch'].forEach((sub, i) => {
      TEMPLATES.push({
        id: `tpl_${cat.toLowerCase()}_${tIdx}`,
        name: `${cat} ${sub}`,
        category: cat,
        captionStyle: CAPTION_STYLES[(tIdx + i) % CAPTION_STYLES.length]
      });
      tIdx++;
    });
  });

  const PLATFORMS = [
    { key: 'TIKTOK', label: 'TikTok', ratio: '9:16', cls: '' },
    { key: 'YOUTUBE_SHORTS', label: 'YT Shorts', ratio: '9:16', cls: '' },
    { key: 'INSTAGRAM_REELS', label: 'IG Reels', ratio: '9:16', cls: '' },
    { key: 'FACEBOOK_REELS', label: 'FB Reels', ratio: '9:16', cls: '' },
    { key: 'FACEBOOK_FEED', label: 'FB Feed', ratio: '1:1', cls: 'ratio-1-1' },
    { key: 'YOUTUBE_LONG', label: 'YT Long', ratio: '16:9', cls: 'ratio-16-9' }
  ];

  function currentMonthKey() {
    const d = new Date();
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`;
  }

  function buildPlatformVersions(title, hook, reason, dur) {
    return {
      TIKTOK: {
        hook: `STOP SCROLL! ${hook}`,
        caption: `${hook}\nSetuju gak guys? Tulis di komen! #tiktokindo #fyp #viral #clipperadha`,
        dur: Math.min(60, Math.max(18, dur))
      },
      YOUTUBE_SHORTS: {
        hook,
        caption: `${title} | Highlight Viral Indonesia. ${reason} #shorts #youtubeshorts #trending`,
        dur: Math.min(60, Math.max(20, dur))
      },
      INSTAGRAM_REELS: {
        hook,
        caption: `✨ ${title}\n${reason}\nSimpan & bagikan ke temanmu! #reelsinstagram #reelsviral`,
        dur: Math.min(90, Math.max(25, dur))
      },
      FACEBOOK_REELS: {
        hook,
        caption: `${reason} Bagaimana pendapat Anda? Diskusi di komentar! #fbreels #videoviral`,
        dur: Math.min(60, Math.max(20, dur))
      },
      FACEBOOK_FEED: {
        hook,
        caption: `Cuplikan 1:1: ${title}. ${reason} Bagikan ke rekan Anda! #facebookvideo #diskusi`,
        dur: Math.min(90, Math.max(30, dur))
      },
      YOUTUBE_LONG: {
        hook: `CHAPTER UTAMA: ${hook}`,
        caption: `Segmen highlight 16:9 (3-5 menit): ${title}. ${reason} #youtubeindonesia`,
        dur: 210
      }
    };
  }

  function createSeedClips() {
    const specs = [
      ['Kenapa Programmer Pindah ke Rust di 2026?', 98, 'Hook kontroversial + intonasi tinggi 3 detik awal.', 'Ini alasan tim kami migrasi ke Rust!', 'Eh anu pindah ke Rust kayak lebih aman gitu.', 'Pindah ke Rust bikin server lebih aman dan hemat memori!', 'NONE', 'DIJADWALKAN', 'TIKTOK'],
      ['1v4 Clutch Mustahil AllGamesClips di Final Round!', 97, 'Lonjakan desibel suara + kill beruntun.', 'TINGGAL 1 HP MASIH RATAIN 4 MUSUH?!', 'Eh sumpah anu headshot empat orang!', 'Sumpah headshot empat orang sekaligus rata!', 'KILL +1', 'TERPUBLISH', 'YOUTUBE_SHORTS'],
      ['Momen Paling Lucu: Mic Mati 10 Menit Ngomong Sendiri', 95, 'Gelak tawa pecah sangat relatable untuk FYP.', 'Udah serius ceramah taunya mic ke-mute!', 'Eh astaga anu mic-nya mati gitu!', 'Astaga ternyata tombol mute mic menyala dari tadi!', 'LUCU', 'DRAFT', 'TIKTOK'],
      ['Rahasia 1 Video Panjang Jadi 20 Konten Sebulan', 94, 'Tips praktis yang memicu banyak Save & Share.', 'Cukup syuting sekali untuk stok 30 hari!', 'Kayak gini caranya eh potong jadi 20 clip.', 'Rekam 45 menit sekali, potong otomatis jadi 20 clip!', 'NONE', 'DIJADWALKAN', 'INSTAGRAM_REELS'],
      ['RAGE MOMENT: Kena Snipe di Detik Terakhir', 93, 'Ekspresi emosi memuncak cocok untuk gaming.', 'DETIK TERAKHIR MALAH KENA HEADSHOT!', 'Waduh eh apaan tuh anu langsung rata!', 'Waduh dari belakang tanpa step langsung rata!', 'RAGE MODE!', 'DRAFT', 'TIKTOK'],
      ['Cara Bikin Hook 3 Detik Pertama Anti Skip', 92, 'Struktur kalimat tanya naikkan watch time.', '90% video gagal FYP gara-gara 3 detik awal!', 'Eh jangan mulai video pakai halo guys gitu.', 'Jangan mulai pakai halo guys, langsung kasih konflik!', 'NONE', 'DRAFT', 'YOUTUBE_SHORTS'],
      ['Benchmark Rust vs Go: Latency Turun 65%', 91, 'Data angka konkret memicu diskusi developer.', 'Memory turun drastis ganti compiler!', 'Eh Rust itu anu hemat RAM banget gitu.', 'Rust jauh lebih hemat RAM untuk beban tinggi!', 'NONE', 'DRAFT', 'FACEBOOK_FEED'],
      ['MENANG TURNAMEN! Selebrasi Tim AllGamesClips', 90, 'Klimaks kemenangan cocok slow-motion 0.5x.', 'JUARA 1 SETELAH COMEBACK 0-3!', 'Anu kita menang guys eh pecah banget!', 'Kita menang guys! Comeback paling gila musim ini!', 'MENANG!', 'DIJADWALKAN', 'INSTAGRAM_REELS'],
      ['Etika & Niat Berkarya Jadi Amal Jariyah', 89, 'Nada tenang inspiratif tinggi rasio share.', 'Pastikan kontenmu membawa manfaat!', 'Eh temen-temen anu niatkan bantu orang gitu.', 'Niatkan setiap konten untuk membantu banyak orang.', 'NONE', 'DRAFT', 'FACEBOOK_REELS'],
      ['Kesalahan Terbesar Live Jualan di Jam Sepi', 88, 'Solusi langsung untuk UMKM & affiliate.', 'Stop live jam segini kalau gak mau sepi!', 'Anu banyak seller eh salah jam tayang gitu.', 'Kesalahan utama live sepi ada pada jam tayang!', 'NONE', 'DRAFT', 'TIKTOK'],
      ['Kalah Konyol Salah Pencet Granat Sendiri', 87, 'Komedi gaming natural reaksi panik.', 'Granat malah dilempar ke tembok sendiri!', 'Eh lari woy anu salah pencet tombol!', 'Lari woy salah pencet tombol malah tumbang!', 'MATI', 'DRAFT', 'YOUTUBE_SHORTS'],
      ['Trik Edit di HP RAM 4GB Anti Lag', 86, 'Target kreator mobile pemula sangat besar.', 'Gak perlu PC mahal buat ngonten harian!', 'Eh pakai ekstrak audio dulu gitu biar ringan.', 'Pakai metode ekstrak audio agar HP tetap ngebut!', 'NONE', 'DRAFT', 'FACEBOOK_REELS'],
      ['Caption Kata-per-Kata Naikkan Retensi 2x', 85, 'Efektif untuk penonton tanpa suara.', '70% orang nonton Reels tanpa volume!', 'Jadi kayak teks per kata itu eh mengunci mata.', 'Teks per kata mengunci mata meski volume mati!', 'NONE', 'DRAFT', 'INSTAGRAM_REELS'],
      ['Double Kill Sniper Tanpa Scope di Asap!', 84, 'Aksi cepat cocok dengan efek Slow-Mo & Boom.', 'TEMBAK DI SMOKE DAPET 2 KILL!', 'Anu gak keliatan eh dapet dua kill!', 'Gak keliatan apa-apa langsung dapet dua kill!', 'KILL +1', 'DRAFT', 'TIKTOK'],
      ['Konsistensi 30 Hari Mengubah Nasib Kreator', 83, 'Penutup motivasi cocok untuk YouTube Long.', 'Kunci kreator sukses: konsisten 30 hari!', 'Eh percaya deh anu upload rutin tiap hari.', 'Upload rutin setiap hari dengan jadwal otomatis!', 'NONE', 'TERPUBLISH', 'YOUTUBE_LONG']
    ];
    return specs.map((s, idx) => ({
      id: `clip_${idx + 1}`,
      projectId: 'proj_main_45m',
      order: idx + 1,
      title: s[0],
      score: s[1],
      reason: s[2],
      raw: s[4],
      clean: s[5],
      gamingHud: s[6],
      status: s[7],
      platform: s[8],
      dur: 32 + (idx % 4) * 7,
      trimStart: 0,
      trimEnd: 100,
      captionStyle: s[6] !== 'NONE' ? 'Gaming Neon' : 'Hormozi Bold',
      autoReframe: true,
      autoZoom: true,
      removeSilence: true,
      fillerRemover: true,
      bRoll: idx % 3 === 0,
      gamingMode: s[6] !== 'NONE',
      slowMo: s[6] === 'KILL +1' || s[6] === 'MENANG!',
      bgm: s[6] !== 'NONE' ? 'Epic Esports Drop' : 'Phonk Viral Beat',
      sfxWhoosh: true,
      sfxBoom: s[6] !== 'NONE',
      versions: buildPlatformVersions(s[0], s[3], s[2], 32 + (idx % 4) * 7)
    }));
  }

  function loadState() {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      if (raw) {
        const parsed = JSON.parse(raw);
        if (parsed.quotaMonth !== currentMonthKey()) {
          parsed.quotaMonth = currentMonthKey();
          parsed.remainingMinutes = MONTHLY_QUOTA;
        }
        return parsed;
      }
    } catch (_) {}
    return {
      tab: 'beranda',
      remainingMinutes: MONTHLY_QUOTA,
      quotaMonth: currentMonthKey(),
      pwaInstalled: false,
      dataSaver: true,
      watermark: false,
      gamingDefault: false,
      simulateLowRam: false,
      brandKit: { name: 'Adha Creator Studio', initials: 'CA', intro: 'CLIPPER ADHA PRO', outro: 'Follow & Subscribe!' },
      folderFilter: 'SEMUA',
      statusFilter: 'SEMUA',
      activeProjectId: 'proj_main_45m',
      pagerIndex: 0,
      promptFilter: '',
      upload: {
        mode: 'UPLOAD',
        fileName: 'podcast_tech_gaming_45m.mp4',
        fileSizeMb: 96,
        durationMin: 45,
        compression: 'Mode Cepat (480p) Hemat Kuota',
        linkInput: '',
        linkTitle: '',
        gamingMode: false,
        prompt: '',
        isSplit: false
      },
      projects: [
        { id: 'proj_main_45m', title: 'Podcast Tech, Rust & AllGamesClips Live (45 Mnt)', folder: 'TIKTOK', sizeMb: 100, durMin: 45, source: 'Upload HP (Chunked 2MB)', audioMb: 4.2 },
        { id: 'proj_gaming', title: 'AllGamesClips Ranked Clutch & Rage', folder: 'GAMING', sizeMb: 86, durMin: 32, source: 'Twitch VOD', audioMb: 3.4 }
      ],
      clips: createSeedClips(),
      schedule30: []
    };
  }

  let state = loadState();
  let deferredInstallPrompt = null;

  function saveState() {
    try { localStorage.setItem(STORAGE_KEY, JSON.stringify(state)); } catch (_) {}
  }

  function showToast(msg) {
    const el = document.getElementById('toast');
    el.textContent = msg;
    el.classList.remove('hidden');
    clearTimeout(window.__toastTimer);
    window.__toastTimer = setTimeout(() => el.classList.add('hidden'), 3000);
  }

  window.addEventListener('beforeinstallprompt', (e) => {
    e.preventDefault();
    deferredInstallPrompt = e;
  });

  if ('serviceWorker' in navigator && (location.protocol === 'https:' || location.protocol === 'http:')) {
    window.addEventListener('load', () => {
      navigator.serviceWorker.register('./service-worker.js').catch(() => {});
    });
  }

  window.triggerInstallApp = async function () {
    if (deferredInstallPrompt) {
      deferredInstallPrompt.prompt();
      await deferredInstallPrompt.userChoice;
      deferredInstallPrompt = null;
    }
    state.pwaInstalled = true;
    saveState();
    showToast('Clipper Adha berhasil disiapkan di HP Kamu!');
    render();
  };

  window.switchTab = function (tab) {
    state.tab = tab;
    saveState();
    render();
  };

  function updateHeader() {
    document.getElementById('header-quota-text').textContent = `${state.remainingMinutes} mnt/bln`;
    document.getElementById('header-data-status').textContent = state.dataSaver
      ? 'Hemat Data Aktif • Offline Siap'
      : 'Mode Kualitas Penuh • Offline';
    document.querySelectorAll('.nav-btn').forEach((btn) => {
      btn.classList.toggle('active', btn.dataset.tab === state.tab);
    });
  }

  function showQuotaExhaustedPopup() {
    openModal(`
      <h3 class="h2" style="color:var(--gold)">Kuota Bulanan Habis</h3>
      <p style="line-height:1.5">Kuota Bulanan Habis (1000 Menit), Kuota Akan Reset Otomatis Tanggal 1 Bulan Depan</p>
      <button class="btn btn-gold btn-full" onclick="closeModal()">Mengerti</button>
    `);
  }

  window.openModal = function (html) {
    const root = document.getElementById('modal-root');
    root.innerHTML = `<div class="modal-card">${html}</div>`;
    root.classList.remove('hidden');
  };

  window.closeModal = function () {
    document.getElementById('modal-root').classList.add('hidden');
  };

  function getActiveClipsForPager() {
    let list = state.clips.filter((c) => c.projectId === state.activeProjectId);
    if (!list.length) list = state.clips;
    if (state.promptFilter.trim()) {
      const q = state.promptFilter.toLowerCase();
      const filtered = list.filter((c) =>
        c.title.toLowerCase().includes(q) ||
        c.reason.toLowerCase().includes(q) ||
        c.clean.toLowerCase().includes(q) ||
        c.gamingHud.toLowerCase().includes(q)
      );
      if (filtered.length) list = filtered;
    }
    return list;
  }

  function renderBeranda() {
    const used = Math.max(0, MONTHLY_QUOTA - state.remainingMinutes);
    const pct = Math.min(100, Math.round((used / MONTHLY_QUOTA) * 100));
    const topClips = [...state.clips].sort((a, b) => b.score - a.score).slice(0, 4);
    const schedCount = state.clips.filter((c) => c.status !== 'DRAFT').length;

    return `
      ${!state.pwaInstalled ? `
        <div class="install-banner">
          <div>
            <div class="h3">Instal Clipper Adha ke HP Kamu</div>
            <p class="muted">Akses offline penuh tanpa address bar</p>
          </div>
          <button class="btn btn-gold btn-sm" onclick="triggerInstallApp()">INSTAL SEKARANG</button>
        </div>
      ` : ''}

      <div class="card hero-card">
        <div class="gold-text">✨ AI VIDEO CLIPPER ALL-IN-ONE</div>
        <h2 class="h2">1 Video Jadi 20 Konten Viral</h2>
        <p class="muted">Support 100MB di HP (Chunked 2MB + Audio-Only AI). Sekali proses langsung jadi 4+ versi TikTok, Shorts, Reels & FB.</p>
        <div class="grid-2" style="margin-top:12px">
          <button class="btn btn-purple" onclick="switchTab('buat')">🎬 Buat Clip</button>
          <button class="btn btn-outline" onclick="openAuto30Modal()">📅 Auto 30 Hari</button>
        </div>
      </div>

      <div class="card" style="border-color: rgba(255,215,0,0.45)">
        <div class="row-between">
          <span class="gold-text">Sisa Kamu: ${state.remainingMinutes} Menit / Bulan</span>
          <span style="font-size:11px;font-weight:700">Terpakai ${used} / 1000 Menit</span>
        </div>
        <div class="progress-track"><div class="progress-fill" style="width:${pct}%"></div></div>
        <p class="muted">Reset otomatis menjadi 1000 Menit setiap tanggal 1 bulan depan</p>
      </div>

      <div class="grid-2">
        <div class="card"><div class="muted">Total Project</div><div class="h2">${state.projects.length}</div><div class="gold-text">Offline Lokal</div></div>
        <div class="card"><div class="muted">Total Clip Siap</div><div class="h2">${state.clips.length}</div><div class="green-text">4+1 Platform</div></div>
        <div class="card"><div class="muted">Jadwal / Publish</div><div class="h2">${schedCount}</div><div class="muted">${state.schedule30.length} slot 30 hari</div></div>
        <div class="card"><div class="muted">Rata-Rata Viral</div><div class="h2">91/100</div><div class="gold-text">Prediksi AI</div></div>
      </div>

      <div class="card">
        <div class="row-between" style="margin-bottom:8px">
          <div class="h3">🔥 Analytics: Prediksi Paling Viral</div>
          <span class="gold-text">Top 4</span>
        </div>
        ${topClips.map((c) => `
          <div style="background:var(--elevated);padding:10px;border-radius:12px;margin-bottom:8px;cursor:pointer" onclick="openEditorModal('${c.id}')">
            <div class="row-between">
              <span style="font-size:12px;font-weight:700">${c.title}</span>
              <span class="gold-text">${c.score}/100</span>
            </div>
            <div class="progress-track"><div class="progress-fill" style="width:${c.score}%"></div></div>
          </div>
        `).join('')}
      </div>
    `;
  }

  function renderBuatClip() {
    const u = state.upload;
    const pagerClips = getActiveClipsForPager();
    if (state.pagerIndex >= pagerClips.length) state.pagerIndex = 0;
    const activeClip = pagerClips[state.pagerIndex];
    const platObj = activeClip ? (PLATFORMS.find((p) => p.key === activeClip.platform) || PLATFORMS[0]) : PLATFORMS[0];
    const ver = activeClip ? (activeClip.versions[platObj.key] || activeClip.versions.TIKTOK) : null;

    return `
      ${state.simulateLowRam ? `
        <div class="alert-box">⚠️ <b>Peringatan RAM:</b> Tutup aplikasi lain dulu ya agar proses video di HP tetap lancar.</div>
      ` : ''}

      <div class="card">
        <div class="h2">Buat Clip Viral Otomatis</div>
        <p class="muted">Support MP4, MOV, WEBM hingga 100MB & Paste Link Sosial Media</p>

        <div class="grid-2" style="margin:12px 0">
          <button class="btn ${u.mode === 'UPLOAD' ? 'btn-purple' : 'btn-outline'} btn-sm" onclick="setUploadMode('UPLOAD')">Upload dari HP</button>
          <button class="btn ${u.mode === 'LINK' ? 'btn-purple' : 'btn-outline'} btn-sm" onclick="setUploadMode('LINK')">Paste Link Video</button>
        </div>

        ${u.mode === 'UPLOAD' ? `
          <label class="dropzone" style="display:block">
            <input type="file" accept="video/mp4,video/quicktime,video/webm,video/*" style="display:none" onchange="onLocalFilePicked(this)" />
            <div style="font-size:26px">📤</div>
            <div class="h3">Tap Pilih Video dari HP</div>
            <div class="gold-text">${u.fileName} (${u.fileSizeMb} MB • ${u.durationMin} mnt)</div>
          </label>
          <div class="chip-scroll" style="margin-top:8px">
            <button class="chip" onclick="loadSample(96, 45, 'podcast_tech_96mb.mp4', false)">Podcast 96MB</button>
            <button class="chip" onclick="loadSample(100, 38, 'allgamesclips_100mb.mp4', true)">Gaming 100MB</button>
            <button class="chip" onclick="loadSample(140, 55, 'raw_stream_140mb.mp4', true)">File >100MB (140MB)</button>
          </div>
        ` : `
          <input class="input" placeholder="Paste link YT / TikTok / IG / FB / Twitch / Drive..." value="${u.linkInput}" oninput="onLinkInput(this.value)" />
          <div class="chip-scroll" style="margin-top:8px">
            <button class="chip" onclick="onLinkInput('https://youtube.com/watch?v=tech45')">YouTube</button>
            <button class="chip" onclick="onLinkInput('https://tiktok.com/@adha/video/99')">TikTok</button>
            <button class="chip" onclick="onLinkInput('https://instagram.com/reel/cx12')">IG Reels</button>
            <button class="chip" onclick="onLinkInput('https://facebook.com/live/jualan')">FB Live</button>
            <button class="chip" onclick="onLinkInput('https://twitch.tv/videos/allgamesclips')">Twitch VOD</button>
            <button class="chip" onclick="onLinkInput('https://cloud-drive.local/file/master45m')">Cloud Drive</button>
          </div>
          ${u.linkTitle ? `<div class="alert-box alert-green" style="margin-top:8px">✅ <b>${u.linkTitle}</b> • ${u.durationMin} mnt • ~${u.fileSizeMb} MB</div>` : ''}
        `}

        ${u.fileSizeMb > 100 ? `
          <div class="alert-box alert-danger" style="margin-top:10px">
            <div><b>File ${u.fileSizeMb} MB melebihi 100MB!</b></div>
            <button class="btn btn-gold btn-full btn-sm" style="margin-top:8px" onclick="splitOver100Mb()">Pecah Otomatis jadi 2 bagian @50MB</button>
          </div>
        ` : ''}

        ${u.fileSizeMb > 80 && u.fileSizeMb <= 100 ? `
          <div class="alert-box" style="margin-top:10px">
            <div class="gold-text" style="margin-bottom:6px">File >80MB • Pilih Kompresi Otomatis:</div>
            <div class="grid-2">
              <button class="btn btn-sm ${u.compression.includes('480p') ? 'btn-purple' : 'btn-outline'}" onclick="setCompression('Mode Cepat (480p) Hemat Kuota')">480p Hemat Kuota</button>
              <button class="btn btn-sm ${u.compression.includes('720p') ? 'btn-purple' : 'btn-outline'}" onclick="setCompression('Mode HD (720p)')">Mode HD (720p)</button>
            </div>
          </div>
        ` : ''}

        <div class="alert-box alert-green" style="margin-top:10px">
          🎙️ <b>Metode Audio-Only AI:</b> Ekstrak audio ~3.8MB ke AI. Video asli tetap di HP, upload chunk 2MB.
        </div>

        <input class="input" style="margin-top:10px" placeholder="Prompt AI: Cari momen saat bahas Rust / kill / lucu" value="${u.prompt}" oninput="setPromptInput(this.value)" />
        <div class="chip-scroll" style="margin-top:6px">
          <button class="chip" onclick="applyPromptFilter('Cari momen paling lucu')">Momen Lucu</button>
          <button class="chip" onclick="applyPromptFilter('Rust')">Bahas Rust</button>
          <button class="chip" onclick="applyPromptFilter('kill')">Momen Kill</button>
          ${state.promptFilter ? `<button class="chip active" onclick="applyPromptFilter('')">Reset</button>` : ''}
        </div>

        <div id="pipeline-progress-box" class="hidden" style="margin-top:10px">
          <div class="row-between">
            <span class="gold-text" id="pipeline-stage">Memulai...</span>
            <span id="pipeline-pct" style="font-size:11px">0%</span>
          </div>
          <div class="progress-track"><div class="progress-fill" id="pipeline-bar" style="width:0%"></div></div>
        </div>

        <button class="btn btn-purple btn-full" style="margin-top:12px" onclick="startClipping()">✨ PROSES VIDEO JADI 16 CLIP VIRAL</button>
      </div>

      ${activeClip ? `
        <div class="card">
          <div class="row-between" style="margin-bottom:10px">
            <div>
              <div class="h3">Hasil Clip (1-per-1 Anti Lag)</div>
              <div class="gold-text">Clip ${state.pagerIndex + 1} dari ${pagerClips.length}</div>
            </div>
            <div class="row-gap">
              <button class="btn btn-outline btn-sm" onclick="movePager(-1)">◀</button>
              <button class="btn btn-outline btn-sm" onclick="movePager(1)">▶</button>
            </div>
          </div>

          <div class="preview-frame ${platObj.cls}">
            <div class="row-between">
              <span class="chip-pill">${platObj.label} • ${platObj.ratio}</span>
              <span class="chip-pill" style="background:var(--purple)">${state.brandKit.initials}</span>
            </div>
            ${activeClip.gamingMode && activeClip.gamingHud !== 'NONE' ? `<div class="preview-gaming-hud">🎮 ${activeClip.gamingHud}${activeClip.slowMo ? ' • 0.5x' : ''}</div>` : ''}
            <div class="preview-caption-box">${(activeClip.fillerRemover ? activeClip.clean : activeClip.raw).toUpperCase()}</div>
            <div class="row-between" style="font-size:10px">
              <span style="color:var(--soft-gold)">${state.watermark ? 'Clipper Adha' : activeClip.captionStyle}</span>
              <span class="gold-text">${ver.dur} dtk</span>
            </div>
          </div>

          <div class="row-between" style="margin-top:10px">
            <span class="gold-text">Virality Score: ${activeClip.score}/100</span>
            <span class="green-text">${activeClip.status}</span>
          </div>
          <div class="h3" style="margin-top:4px">${activeClip.title}</div>
          <p class="muted">Alasan AI: ${activeClip.reason}</p>

          <div class="chip-scroll" style="margin:10px 0">
            ${PLATFORMS.map((p) => `
              <button class="chip ${activeClip.platform === p.key ? 'active' : ''}" onclick="switchClipPlatform('${activeClip.id}', '${p.key}')">${p.label} (${p.ratio})</button>
            `).join('')}
          </div>

          <div style="background:var(--elevated);border-radius:12px;padding:10px;margin-bottom:10px">
            <div class="gold-text">Hook 3 Detik: ${ver.hook}</div>
            <p class="muted" style="margin-top:4px;color:#fff">${ver.caption}</p>
          </div>

          <div class="grid-2">
            <button class="btn btn-outline" onclick="openEditorModal('${activeClip.id}')">✏️ Edit HP</button>
            <button class="btn btn-gold" onclick="openPublishModal('${activeClip.id}')">🚀 Publish</button>
          </div>
        </div>
      ` : ''}
    `;
  }

  function renderProjectSaya() {
    const folders = ['SEMUA', 'YOUTUBE', 'TIKTOK', 'GAMING'];
    const statuses = ['SEMUA', 'DRAFT', 'DIJADWALKAN', 'TERPUBLISH'];
    const projects = state.folderFilter === 'SEMUA' ? state.projects : state.projects.filter((p) => p.folder === state.folderFilter);
    const clips = state.clips.filter((c) => (state.statusFilter === 'SEMUA' || c.status === state.statusFilter));

    return `
      <div class="row-between">
        <div>
          <div class="h2">Project Saya</div>
          <div class="green-text">📱 Tersimpan Offline Lokal HP</div>
        </div>
        <button class="btn btn-gold btn-sm" onclick="openAuto30Modal()">📅 Auto 30 Hari</button>
      </div>

      <div class="chip-scroll">
        ${folders.map((f) => `<button class="chip ${state.folderFilter === f ? 'active' : ''}" onclick="setFolderFilter('${f}')">${f}</button>`).join('')}
      </div>

      ${projects.map((p) => `
        <div class="card" style="border-color:${p.id === state.activeProjectId ? 'var(--gold)' : 'var(--border)'};cursor:pointer" onclick="selectProject('${p.id}')">
          <div class="row-between">
            <span class="gold-text">${p.folder} • ${p.sizeMb} MB • ${p.durMin} mnt</span>
            <span class="muted">Audio AI ${p.audioMb}MB</span>
          </div>
          <div class="h3" style="margin-top:4px">${p.title}</div>
          <p class="muted">${p.source}</p>
        </div>
      `).join('')}

      <div class="chip-scroll">
        ${statuses.map((s) => `<button class="chip ${state.statusFilter === s ? 'active' : ''}" onclick="setStatusFilter('${s}')">${s}</button>`).join('')}
      </div>

      ${clips.map((c) => `
        <div class="card">
          <div class="row-between">
            <span class="gold-text">#${c.order} • Skor ${c.score}/100</span>
            <span class="green-text">${c.status}</span>
          </div>
          <div class="h3" style="margin:4px 0">${c.title}</div>
          <p class="muted">${c.reason}</p>
          <div class="grid-2" style="margin-top:10px">
            <button class="btn btn-outline btn-sm" onclick="openEditorModal('${c.id}')">Edit</button>
            <button class="btn btn-gold btn-sm" onclick="openPublishModal('${c.id}')">Publish</button>
          </div>
        </div>
      `).join('')}
    `;
  }

  function renderProfil() {
    const used = Math.max(0, MONTHLY_QUOTA - state.remainingMinutes);
    const pct = Math.min(100, Math.round((used / MONTHLY_QUOTA) * 100));

    return `
      <div class="card hero-card">
        <div class="row-between">
          <div>
            <div class="h2">Clipper Adha Pro</div>
            <div class="gold-text">Sisa Kamu: ${state.remainingMinutes} Menit / Bulan</div>
          </div>
          <span class="pro-pill" style="padding:5px 10px;font-size:11px">PRIBADI</span>
        </div>
        <div class="row-between" style="margin-top:12px">
          <span style="font-size:12px;font-weight:700">⚡ Terpakai ${used} / 1000 Menit</span>
          <span class="muted">Reset tgl 1</span>
        </div>
        <div class="progress-track"><div class="progress-fill" style="width:${pct}%"></div></div>
      </div>

      <div class="card" style="border-color:${state.pwaInstalled ? 'var(--green)' : 'var(--gold)'}">
        <div class="row-between" style="margin-bottom:10px">
          <div>
            <div class="h3">Install Aplikasi</div>
            <p class="muted">Mode standalone tanpa browser • Offline siap</p>
          </div>
          ${state.pwaInstalled ? `<span class="green-text">Sudah Terinstall ✓</span>` : ''}
        </div>
        ${!state.pwaInstalled ? `
          <button class="btn btn-gold btn-full" onclick="triggerInstallApp()">INSTALL CLIPPER ADHA KE HP</button>
        ` : ''}
      </div>

      <div class="card" style="display:flex;flex-direction:column;gap:8px">
        <div class="h3" style="color:var(--gold)">Pengaturan & Brand Kit</div>
        <div class="switch-row">
          <div><div style="font-size:13px;font-weight:700">Mode Hemat Data</div><div class="muted">Thumbnail saja agar ringan</div></div>
          <input type="checkbox" ${state.dataSaver ? 'checked' : ''} onchange="togglePref('dataSaver', this.checked)" />
        </div>
        <div class="switch-row">
          <div><div style="font-size:13px;font-weight:700">Watermark "Clipper Adha"</div><div class="muted">${state.watermark ? 'Aktif' : 'Nonaktif (Pro)'}</div></div>
          <input type="checkbox" ${state.watermark ? 'checked' : ''} onchange="togglePref('watermark', this.checked)" />
        </div>
        <div class="switch-row">
          <div><div style="font-size:13px;font-weight:700">Simulasi Peringatan RAM</div><div class="muted">Tes banner HP lemot</div></div>
          <input type="checkbox" ${state.simulateLowRam ? 'checked' : ''} onchange="togglePref('simulateLowRam', this.checked)" />
        </div>
        <button class="btn btn-outline btn-full" onclick="openBrandKitModal()">🎨 Brand Kit & 52+ Template</button>
        <button class="btn btn-outline btn-full" onclick="resetDemo()">🔄 Reset Data Demo (1000 Menit)</button>
      </div>
    `;
  }

  // Interactive Global Handlers
  window.setUploadMode = (m) => { state.upload.mode = m; saveState(); render(); };
  window.loadSample = (mb, min, name, gaming) => {
    state.upload.fileSizeMb = mb; state.upload.durationMin = min; state.upload.fileName = name; state.upload.gamingMode = gaming; state.upload.isSplit = false;
    saveState(); render();
  };
  window.onLocalFilePicked = (input) => {
    const f = input.files && input.files[0];
    if (!f) return;
    const mb = Math.max(1, Math.round(f.size / (1024 * 1024)));
    state.upload.fileName = f.name;
    state.upload.fileSizeMb = mb;
    state.upload.durationMin = Math.max(3, Math.min(60, Math.round(mb / 2)));
    saveState(); render();
    showToast(`Video dipilih: ${f.name} (${mb} MB)`);
  };
  window.onLinkInput = (val) => {
    state.upload.linkInput = val;
    if (val.length > 5) {
      state.upload.linkTitle = val.includes('twitch') ? 'AllGamesClips Ranked Marathon' : 'Podcast AI & Konten Viral Indonesia';
      state.upload.fileSizeMb = val.includes('twitch') ? 99 : 92;
      state.upload.durationMin = 42;
    } else {
      state.upload.linkTitle = '';
    }
    saveState(); render();
  };
  window.splitOver100Mb = () => {
    state.upload.fileSizeMb = 50;
    state.upload.durationMin = Math.max(15, Math.round(state.upload.durationMin / 2));
    state.upload.isSplit = true;
    saveState(); render();
    showToast('File dipecah otomatis menjadi 2 bagian @50MB!');
  };
  window.setCompression = (c) => { state.upload.compression = c; saveState(); render(); };
  window.setPromptInput = (v) => { state.upload.prompt = v; saveState(); };
  window.applyPromptFilter = (q) => { state.promptFilter = q; state.upload.prompt = q; state.pagerIndex = 0; saveState(); render(); };
  window.movePager = (dir) => {
    const len = getActiveClipsForPager().length;
    state.pagerIndex = (state.pagerIndex + dir + len) % len;
    saveState(); render();
  };
  window.switchClipPlatform = (clipId, plat) => {
    const c = state.clips.find((x) => x.id === clipId);
    if (c) { c.platform = plat; saveState(); render(); }
  };
  window.setFolderFilter = (f) => { state.folderFilter = f; saveState(); render(); };
  window.setStatusFilter = (s) => { state.statusFilter = s; saveState(); render(); };
  window.selectProject = (id) => { state.activeProjectId = id; saveState(); render(); };
  window.togglePref = (k, v) => { state[k] = v; saveState(); render(); };
  window.resetDemo = () => {
    localStorage.removeItem(STORAGE_KEY);
    state = loadState();
    saveState(); render();
    showToast('Data demo & kuota 1000 Menit/Bulan di-reset!');
  };

  window.startClipping = function () {
    if (state.remainingMinutes <= 0) {
      showQuotaExhaustedPopup();
      return;
    }
    if (state.upload.fileSizeMb > 100) {
      showToast('File >100MB! Tekan Pecah Otomatis jadi 2 bagian @50MB dulu.');
      return;
    }
    const box = document.getElementById('pipeline-progress-box');
    const stageEl = document.getElementById('pipeline-stage');
    const pctEl = document.getElementById('pipeline-pct');
    const barEl = document.getElementById('pipeline-bar');
    if (box) box.classList.remove('hidden');

    const steps = [
      [25, `Upload ${Math.round(state.upload.fileSizeMb * 0.45)}/${state.upload.fileSizeMb} MB (Chunk 2MB)...`],
      [50, 'Ekstrak Audio 20% (3.8 MB)... Video asli tetap di HP'],
      [75, 'AI Menganalisis 60%...'],
      [92, 'Memotong 16 Clip & Auto 4 Platform 90%...'],
      [100, 'Selesai! 16 Clip Viral Siap']
    ];
    let i = 0;
    const timer = setInterval(() => {
      if (i >= steps.length) {
        clearInterval(timer);
        const newProjId = `proj_${Date.now()}`;
        const title = (state.upload.linkTitle || state.upload.fileName).replace(/\.[^/.]+$/, '');
        state.projects.unshift({
          id: newProjId,
          title,
          folder: state.upload.gamingMode ? 'GAMING' : 'TIKTOK',
          sizeMb: state.upload.fileSizeMb,
          durMin: state.upload.durationMin,
          source: state.upload.mode === 'LINK' ? 'Link Online' : 'Upload HP (Chunked 2MB)',
          audioMb: 3.8
        });
        state.activeProjectId = newProjId;
        state.pagerIndex = 0;
        state.remainingMinutes = Math.max(0, state.remainingMinutes - state.upload.durationMin);
        saveState();
        render();
        showToast('16 Clip Viral berhasil dibuat offline!');
        if (state.remainingMinutes <= 0) showQuotaExhaustedPopup();
        return;
      }
      const [pct, text] = steps[i++];
      if (stageEl) stageEl.textContent = text;
      if (pctEl) pctEl.textContent = `${pct}%`;
      if (barEl) barEl.style.width = `${pct}%`;
    }, 350);
  };

  window.openEditorModal = function (clipId) {
    const c = state.clips.find((x) => x.id === clipId);
    if (!c) return;
    openModal(`
      <div class="row-between" style="margin-bottom:10px">
        <div class="h3" style="color:var(--gold)">Studio Editor HP</div>
        <button class="btn btn-outline btn-sm" onclick="closeModal()">Tutup</button>
      </div>
      <div style="font-size:13px;font-weight:700;margin-bottom:8px">${c.title}</div>
      <div class="muted">Timeline 1 Track (${c.trimStart}% - ${c.trimEnd}%)</div>
      <input type="range" min="0" max="45" value="${c.trimStart}" style="width:100%" oninput="updateClipField('${c.id}','trimStart',+this.value)" />
      <input type="range" min="55" max="100" value="${c.trimEnd}" style="width:100%" oninput="updateClipField('${c.id}','trimEnd',+this.value)" />
      <div class="gold-text" style="margin:8px 0 4px">Auto-Caption (32 Gaya Viral):</div>
      <select class="input" onchange="updateClipField('${c.id}','captionStyle',this.value)">
        ${CAPTION_STYLES.map((s) => `<option value="${s}" ${c.captionStyle === s ? 'selected' : ''}>${s}</option>`).join('')}
      </select>
      <div style="display:flex;flex-direction:column;gap:6px;margin-top:10px">
        <div class="switch-row"><span>Auto-Reframe Wajah</span><input type="checkbox" ${c.autoReframe ? 'checked' : ''} onchange="updateClipField('${c.id}','autoReframe',this.checked)" /></div>
        <div class="switch-row"><span>Auto-Zoom Momen Penting</span><input type="checkbox" ${c.autoZoom ? 'checked' : ''} onchange="updateClipField('${c.id}','autoZoom',this.checked)" /></div>
        <div class="switch-row"><span>Remove Silence</span><input type="checkbox" ${c.removeSilence ? 'checked' : ''} onchange="updateClipField('${c.id}','removeSilence',this.checked)" /></div>
        <div class="switch-row"><span>Filler Remover (eh, anu, kayak)</span><input type="checkbox" ${c.fillerRemover ? 'checked' : ''} onchange="updateClipField('${c.id}','fillerRemover',this.checked)" /></div>
        <div class="switch-row"><span>AI B-Roll Otomatis</span><input type="checkbox" ${c.bRoll ? 'checked' : ''} onchange="updateClipField('${c.id}','bRoll',this.checked)" /></div>
        <div class="switch-row"><span>Mode Gaming & Slow-Mo</span><input type="checkbox" ${c.gamingMode ? 'checked' : ''} onchange="updateClipField('${c.id}','gamingMode',this.checked)" /></div>
      </div>
      <button class="btn btn-gold btn-full" style="margin-top:12px" onclick="closeModal();render();showToast('Perubahan Editor disimpan!')">Simpan Perubahan</button>
    `);
  };

  window.updateClipField = (id, key, val) => {
    const c = state.clips.find((x) => x.id === id);
    if (c) { c[key] = val; saveState(); }
  };

  window.openPublishModal = function (clipId) {
    const c = state.clips.find((x) => x.id === clipId);
    if (!c) return;
    const ver = c.versions[c.platform] || c.versions.TIKTOK;
    openModal(`
      <div class="row-between" style="margin-bottom:10px">
        <div class="h3" style="color:var(--gold)">Export & Publish</div>
        <button class="btn btn-outline btn-sm" onclick="closeModal()">Tutup</button>
      </div>
      <p style="font-size:13px;font-weight:700">${c.title}</p>
      <div style="background:var(--elevated);padding:10px;border-radius:12px;font-size:12px;margin-bottom:12px">${ver.caption}</div>
      <button class="btn btn-purple btn-full" style="margin-bottom:8px" onclick="showToast('Video 1080p (24 MB) disimpan ke Galeri!');closeModal()">💾 Simpan ke Galeri (1080p • Max 30MB)</button>
      <button class="btn btn-outline btn-full" style="margin-bottom:8px" onclick="shareClip('${c.id}')">📤 Bagikan Langsung</button>
      <button class="btn btn-gold btn-full" onclick="markPublished('${c.id}')">🚀 Publish 1-Klik Sekarang</button>
    `);
  };

  window.shareClip = async (id) => {
    const c = state.clips.find((x) => x.id === id);
    if (!c) return;
    const text = `${c.title}\n\n${(c.versions[c.platform] || c.versions.TIKTOK).caption}`;
    if (navigator.share) {
      try { await navigator.share({ title: c.title, text }); } catch (_) {}
    } else {
      try { await navigator.clipboard.writeText(text); } catch (_) {}
      showToast('Caption & Hashtag disalin untuk dibagikan!');
    }
  };

  window.markPublished = (id) => {
    const c = state.clips.find((x) => x.id === id);
    if (c) { c.status = 'TERPUBLISH'; saveState(); render(); closeModal(); showToast('Clip berhasil di-publish!'); }
  };

  window.openAuto30Modal = function () {
    if (!state.schedule30.length) {
      const plats = ['TikTok', 'YT Shorts', 'IG Reels', 'FB Reels'];
      const times = ['19.00 WIB', '12.15 WIB', '21.00 WIB', '07.30 WIB'];
      state.schedule30 = Array.from({ length: 30 }, (_, i) => {
        const clip = state.clips[i % state.clips.length];
        return { day: i + 1, title: clip.title, platform: plats[i % 4], time: times[i % 4] };
      });
      saveState();
    }
    openModal(`
      <div class="row-between" style="margin-bottom:10px">
        <div class="h3" style="color:var(--gold)">Auto Posting 30 Hari</div>
        <button class="btn btn-outline btn-sm" onclick="closeModal()">Tutup</button>
      </div>
      <p class="muted" style="margin-bottom:10px">Jadwal 30 hari penuh lintas TikTok, Shorts, Reels & FB dengan jam ramai AI:</p>
      <div style="display:flex;flex-direction:column;gap:6px;max-height:55vh;overflow-y:auto">
        ${state.schedule30.map((s) => `
          <div style="background:var(--elevated);padding:8px 10px;border-radius:10px;font-size:12px">
            <span class="gold-text">Hari ${s.day} • ${s.platform} (${s.time})</span>
            <div>${s.title}</div>
          </div>
        `).join('')}
      </div>
    `);
  };

  window.updateBrandKitField = (k, v) => { state.brandKit[k] = v; saveState(); };
  window.showToast = showToast;
  window.renderApp = render;

  window.openBrandKitModal = function () {
    openModal(`
      <div class="row-between" style="margin-bottom:10px">
        <div class="h3" style="color:var(--gold)">Brand Kit & 52+ Template</div>
        <button class="btn btn-outline btn-sm" onclick="closeModal()">Tutup</button>
      </div>
      <label class="muted">Nama Brand</label>
      <input class="input" style="margin:4px 0 8px" value="${state.brandKit.name}" oninput="updateBrandKitField('name', this.value)" />
      <label class="muted">Inisial Logo</label>
      <input class="input" style="margin:4px 0 8px" value="${state.brandKit.initials}" oninput="updateBrandKitField('initials', this.value.slice(0,4))" />
      <div class="gold-text" style="margin:6px 0">Template Siap Pakai (${TEMPLATES.length} Template):</div>
      <div style="max-height:200px;overflow-y:auto;display:flex;flex-direction:column;gap:6px">
        ${TEMPLATES.slice(0, 16).map((t) => `
          <div class="switch-row" style="cursor:pointer" onclick="showToast('Template ${t.name} diterapkan ke semua clip!')">
            <span>${t.name}</span><span class="gold-text">${t.captionStyle}</span>
          </div>
        `).join('')}
      </div>
      <button class="btn btn-gold btn-full" style="margin-top:12px" onclick="closeModal();renderApp();showToast('Brand Kit diterapkan ke semua clip!')">Terapkan ke Semua Clip</button>
    `);
  };

  function render() {
    updateHeader();
    const container = document.getElementById('main-content');
    if (state.tab === 'beranda') container.innerHTML = renderBeranda();
    else if (state.tab === 'buat') container.innerHTML = renderBuatClip();
    else if (state.tab === 'project') container.innerHTML = renderProjectSaya();
    else container.innerHTML = renderProfil();
  }

  // Boot with 2-second Splash
  setTimeout(() => {
    document.getElementById('splash-screen').classList.add('hidden');
    document.getElementById('app-shell').classList.remove('hidden');
    render();
  }, 2000);
})();
