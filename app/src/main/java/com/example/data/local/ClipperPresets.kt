package com.example.data.local

import com.example.data.model.CaptionPosition
import com.example.data.model.CaptionStylePreset
import com.example.data.model.ClipStatus
import com.example.data.model.GamingMomentType
import com.example.data.model.PlatformVersion
import com.example.data.model.ProjectFolder
import com.example.data.model.TargetPlatform
import com.example.data.model.ThumbnailKind
import com.example.data.model.VideoProject
import com.example.data.model.VideoTemplate
import com.example.data.model.ViralClip

object ClipperPresets {

    val captionStyles: List<CaptionStylePreset> = listOf(
        CaptionStylePreset("cs_01", "Hormozi Bold", "Viral", 0xFFFBBF24, true, true),
        CaptionStylePreset("cs_02", "MrBeast Pop", "Viral", 0xFF22D3EE, true, true),
        CaptionStylePreset("cs_03", "Podcast Clean", "Podcast", 0xFFF8FAFC, false, false),
        CaptionStylePreset("cs_04", "Gaming Neon", "Gaming", 0xFFA855F7, true, true),
        CaptionStylePreset("cs_05", "Gold Luxury", "Bisnis", 0xFFF59E0B, true, true),
        CaptionStylePreset("cs_06", "TikTok Punch", "Viral", 0xFFFF2D55, true, true),
        CaptionStylePreset("cs_07", "Dakwah Emas", "Dakwah", 0xFFFDE68A, false, false),
        CaptionStylePreset("cs_08", "Cyber Glitch", "Gaming", 0xFF06B6D4, true, true),
        CaptionStylePreset("cs_09", "Minimal Serif", "Estetik", 0xFFE2E8F0, false, false),
        CaptionStylePreset("cs_10", "Komik Box", "Komedi", 0xFFFACC15, true, false),
        CaptionStylePreset("cs_11", "Youtube SEO", "Edukasi", 0xFFFFFFFF, false, true),
        CaptionStylePreset("cs_12", "Reels Estetik", "Estetik", 0xFFF472B6, false, false),
        CaptionStylePreset("cs_13", "Esports HUD", "Gaming", 0xFF10B981, true, true),
        CaptionStylePreset("cs_14", "Rage Crimson", "Gaming", 0xFFEF4444, true, true),
        CaptionStylePreset("cs_15", "Royal Violet", "Viral", 0xFF8B5CF6, true, true),
        CaptionStylePreset("cs_16", "Jualan Flash", "Jualan", 0xFFFB923C, true, true),
        CaptionStylePreset("cs_17", "News Ticker", "Berita", 0xFF38BDF8, true, false),
        CaptionStylePreset("cs_18", "Karaoke Lime", "Viral", 0xFF84CC16, true, true),
        CaptionStylePreset("cs_19", "Soft Pastel", "Estetik", 0xFFC4B5FD, false, false),
        CaptionStylePreset("cs_20", "Studio Mono", "Tech", 0xFF34D399, false, false),
        CaptionStylePreset("cs_21", "Headline Impact", "Viral", 0xFFFBBF24, true, true),
        CaptionStylePreset("cs_22", "Clipper Signature", "Pro", 0xFFA855F7, true, true),
        CaptionStylePreset("cs_23", "Kajian Tenang", "Dakwah", 0xFFFCD34D, false, false),
        CaptionStylePreset("cs_24", "Live Promo", "Jualan", 0xFFF43F5E, true, true),
        CaptionStylePreset("cs_25", "Valorant Strike", "Gaming", 0xFFFF4655, true, true),
        CaptionStylePreset("cs_26", "MLBB Victory", "Gaming", 0xFFF59E0B, true, true),
        CaptionStylePreset("cs_27", "Dark Whisper", "Podcast", 0xFFCBD5E1, false, false),
        CaptionStylePreset("cs_28", "Hyper Bounce", "Komedi", 0xFF2DD4BF, true, true),
        CaptionStylePreset("cs_29", "Storyteller", "Edukasi", 0xFFFDE047, false, true),
        CaptionStylePreset("cs_30", "Rust Terminal", "Tech", 0xFFFB923C, false, true),
        CaptionStylePreset("cs_31", "Midnight Glow", "Viral", 0xFFC084FC, true, true),
        CaptionStylePreset("cs_32", "Feed Square Clean", "Facebook", 0xFF60A5FA, false, false)
    )

    val templates: List<VideoTemplate> = buildList {
        val categories = listOf(
            Triple("Podcast", ThumbnailKind.PODCAST, 0xFF8B5CF6),
            Triple("Gaming", ThumbnailKind.GAMING, 0xFFA855F7),
            Triple("Dakwah", ThumbnailKind.DAKWAH, 0xFFF59E0B),
            Triple("Jualan", ThumbnailKind.JUALAN, 0xFF10B981)
        )
        val subtitles = listOf(
            "Hormozi Viral", "Dual Speaker", "Hook Emas", "Split Dinamis", "Neon Pulse",
            "Minimalis Pro", "Fast Cut", "Spotlight Fokus", "Storytelling", "Clean Frame",
            "High Retention", "Cinematic Bar", "Zoom Punch"
        )
        var counter = 1
        categories.forEach { (cat, thumb, color) ->
            subtitles.forEachIndexed { idx, sub ->
                val style = captionStyles[(counter + idx) % captionStyles.size].name
                add(
                    VideoTemplate(
                        id = "tpl_${cat.lowercase()}_${counter.toString().padStart(2, '0')}",
                        name = "$cat $sub",
                        category = cat,
                        captionStyle = style,
                        accentHex = color,
                        thumbnailKind = thumb,
                        badge = if (idx < 3) "TERLARIS" else if (idx % 4 == 0) "BARU" else "PRO"
                    )
                )
                counter++
            }
        }
    } // 52 Ready-to-Use Templates!

    val bgMusicOptions = listOf(
        "Phonk Viral Beat",
        "Lo-Fi Podcast Hangat",
        "Epic Esports Drop",
        "Acoustic Inspirasi",
        "Cyberpunk Bassline",
        "Tanpa Musik"
    )

    val aiPrimeTimeSlots = listOf(
        "19.00 WIB" to "Jam Ramai Tertinggi (+42% reach)",
        "12.15 WIB" to "Istirahat Siang (Cocok TikTok & Reels)",
        "21.00 WIB" to "Peak Malam Gaming & Podcast",
        "07.30 WIB" to "Morning Commute (Shorts & FB)"
    )

    fun buildPlatformVersions(
        title: String,
        hook: String,
        summary: String,
        baseDurationSec: Int,
        isGaming: Boolean
    ): Map<TargetPlatform, PlatformVersion> {
        val topicTag = if (isGaming) "#allgamesclips #gamingindo #clutch" else "#podcastindo #kontenviral #tipsbisnis"
        return mapOf(
            TargetPlatform.TIKTOK to PlatformVersion(
                platform = TargetPlatform.TIKTOK,
                hookText = "STOP SCROLL! $hook",
                seoTitle = title,
                caption = "$hook\n\nGak nyangka endingnya begini! Setuju gak guys? Tulis di komen! $topicTag",
                hashtags = "#tiktokindo #fyp #viral $topicTag #clipperadha",
                durationSec = baseDurationSec.coerceIn(18, 60),
                coverStyle = "Teks Kuning Tebal + Emoji"
            ),
            TargetPlatform.YOUTUBE_SHORTS to PlatformVersion(
                platform = TargetPlatform.YOUTUBE_SHORTS,
                hookText = hook,
                seoTitle = "$title | Highlight Viral Indonesia",
                caption = "Ringkasan momen terbaik: $summary. Tonton sampai habis agar tidak ketinggalan poin pentingnya.",
                hashtags = "#shorts #youtubeshorts #trending $topicTag",
                durationSec = baseDurationSec.coerceIn(20, 60),
                coverStyle = "Judul SEO Kontras Tinggi"
            ),
            TargetPlatform.INSTAGRAM_REELS to PlatformVersion(
                platform = TargetPlatform.INSTAGRAM_REELS,
                hookText = hook,
                seoTitle = title,
                caption = "✨ $title\n\n$summary\n\nSimpan dulu biar gak hilang, bagikan ke temanmu yang wajib nonton ini.",
                hashtags = "#reelsinstagram #reelsviral #inspirasi $topicTag",
                durationSec = baseDurationSec.coerceIn(25, 90),
                coverStyle = "Cover Estetik Ungu Emas"
            ),
            TargetPlatform.FACEBOOK_REELS to PlatformVersion(
                platform = TargetPlatform.FACEBOOK_REELS,
                hookText = hook,
                seoTitle = title,
                caption = "$summary. Bagaimana pendapat Anda tentang momen ini? Mari berdiskusi di kolom komentar.",
                hashtags = "#fbreels #videoviral #komunitas $topicTag",
                durationSec = baseDurationSec.coerceIn(20, 60),
                coverStyle = "Clean Frame 9:16"
            ),
            TargetPlatform.FACEBOOK_FEED to PlatformVersion(
                platform = TargetPlatform.FACEBOOK_FEED,
                hookText = hook,
                seoTitle = "$title (Versi Feed 1:1)",
                caption = "Cuplikan lengkap: $summary. Bagikan pendapat Anda di kolom komentar dan tag rekan yang relevan!",
                hashtags = "#facebookvideo #diskusi #edukasi $topicTag",
                durationSec = baseDurationSec.coerceIn(30, 90),
                coverStyle = "Square 1:1 Headline Bar"
            ),
            TargetPlatform.YOUTUBE_LONG to PlatformVersion(
                platform = TargetPlatform.YOUTUBE_LONG,
                hookText = "CHAPTER UTAMA: $hook",
                seoTitle = "$title - Full Highlight Segment (3-5 Menit)",
                caption = "Segmen highlight panjang 16:9 dari pembahasan utama: $summary.",
                hashtags = "#youtubeindonesia #podcastlengkap $topicTag",
                durationSec = (baseDurationSec * 4).coerceIn(180, 290),
                coverStyle = "Thumbnail 16:9 Sinematik"
            )
        )
    }

    fun seedProjects(): List<VideoProject> = listOf(
        VideoProject(
            id = "proj_main_45m",
            title = "Podcast Tech, Rust & AllGamesClips Live (45 Mnt)",
            sourceType = "Upload HP (Chunked 2MB)",
            sourceUrl = "local://DCIM/ClipperAdha/podcast_gaming_45m.mp4",
            folder = ProjectFolder.TIKTOK,
            fileSizeMb = 100,
            durationMinutes = 45,
            resolutionLabel = "720p HD (Kompresi Cerdas)",
            compressionMode = "Mode HD (720p)",
            audioExtractedMb = 4.2f,
            createdAtLabel = "Hari ini • 15 Clip Viral",
            isSplitPart = false,
            partLabel = "",
            thumbnailKind = ThumbnailKind.PODCAST,
            clipCount = 15
        ),
        VideoProject(
            id = "proj_gaming_agc",
            title = "AllGamesClips Ranked Clutch & Rage Moments",
            sourceType = "Twitch VOD",
            sourceUrl = "https://twitch.tv/videos/allgamesclips_9921",
            folder = ProjectFolder.GAMING,
            fileSizeMb = 86,
            durationMinutes = 32,
            resolutionLabel = "480p Cepat",
            compressionMode = "Mode Cepat (480p) Hemat Kuota",
            audioExtractedMb = 3.4f,
            createdAtLabel = "Kemarin • 3 Clip Gaming",
            isSplitPart = false,
            partLabel = "",
            thumbnailKind = ThumbnailKind.GAMING,
            clipCount = 3
        ),
        VideoProject(
            id = "proj_yt_bisnis",
            title = "Strategi Live Jualan & Konten Konsisten 30 Hari",
            sourceType = "YouTube Link",
            sourceUrl = "https://youtube.com/watch?v=adhaPro2026",
            folder = ProjectFolder.YOUTUBE,
            fileSizeMb = 74,
            durationMinutes = 28,
            resolutionLabel = "720p HD",
            compressionMode = "Mode HD (720p)",
            audioExtractedMb = 3.1f,
            createdAtLabel = "2 hari lalu • 2 Clip Edukasi",
            isSplitPart = false,
            partLabel = "",
            thumbnailKind = ThumbnailKind.JUALAN,
            clipCount = 2
        )
    )

    fun seedClips(): List<ViralClip> {
        data class SeedSpec(
            val title: String,
            val score: Int,
            val reason: String,
            val hook: String,
            val raw: String,
            val clean: String,
            val startSec: Int,
            val endSec: Int,
            val thumb: ThumbnailKind,
            val gaming: GamingMomentType,
            val status: ClipStatus,
            val date: String?,
            val time: String?,
            val platform: TargetPlatform
        )

        val mainSpecs = listOf(
            SeedSpec(
                title = "Kenapa Programmer Pindah ke Rust di 2026?",
                score = 98,
                reason = "Hook kontroversial + intonasi antusias tinggi di 3 detik awal, retensi diprediksi 92%.",
                hook = "Ini alasan kenapa tim kami migrasi server ke Rust!",
                raw = "Eh jadi anu alasan kita kayak pindah ke Rust itu karena memori aman banget gitu dan zero cost abstraction!",
                clean = "Alasan utama kita pindah ke Rust karena memori super aman dan zero-cost abstraction!",
                startSec = 120, endSec = 168,
                thumb = ThumbnailKind.PODCAST, gaming = GamingMomentType.NONE,
                status = ClipStatus.DIJADWALKAN, date = "24 Nov", time = "19.00 WIB",
                platform = TargetPlatform.TIKTOK
            ),
            SeedSpec(
                title = "1v4 Clutch Mustahil AllGamesClips di Final Round!",
                score = 97,
                reason = "Lonjakan desibel suara + momen kill beruntun + reaksi teriak tim.",
                hook = "TINGGAL 1 HP MASIH BISA RATAIN 4 MUSUH?!",
                raw = "Eh sumpah anu liat HP gue tinggal satu kayak gak mungkin eh ternyata headshot empat orang rata!",
                clean = "Sumpah liat HP tinggal satu gak mungkin banget, ternyata headshot empat orang rata!",
                startSec = 410, endSec = 452,
                thumb = ThumbnailKind.GAMING, gaming = GamingMomentType.KILL,
                status = ClipStatus.TERPUBLISH, date = "23 Nov", time = "21.00 WIB",
                platform = TargetPlatform.YOUTUBE_SHORTS
            ),
            SeedSpec(
                title = "Momen Paling Lucu: Mic Mati Selama 10 Menit Ngomong Sendiri",
                score = 95,
                reason = "Gelak tawa host pecah + ekspresi kaget yang sangat relatable untuk FYP.",
                hook = "Udah serius ceramah 10 menit taunya mic ke-mute!",
                raw = "Eh astaga anu dari tadi gue ngomong panjang lebar gitu eh ternyata tombol mute-nya nyala dong!",
                clean = "Astaga dari tadi gue ngomong panjang lebar ternyata tombol mute-nya nyala dong!",
                startSec = 680, endSec = 720,
                thumb = ThumbnailKind.PODCAST, gaming = GamingMomentType.LUCU,
                status = ClipStatus.DRAFT, date = null, time = null,
                platform = TargetPlatform.TIKTOK
            ),
            SeedSpec(
                title = "Rahasia 1 Video Panjang Jadi 20 Konten Sebulan",
                score = 94,
                reason = "Tips praktis langkah demi langkah yang memicu banyak tombol Save & Share.",
                hook = "Cukup syuting sekali buat stok konten 30 hari!",
                raw = "Jadi kayak gini caranya eh kalau kamu rekam 45 menit anu tinggal potong jadi 20 clip vertikal.",
                clean = "Begini caranya: rekam 45 menit sekali, lalu potong otomatis jadi 20 clip vertikal!",
                startSec = 890, endSec = 940,
                thumb = ThumbnailKind.JUALAN, gaming = GamingMomentType.NONE,
                status = ClipStatus.DIJADWALKAN, date = "25 Nov", time = "12.15 WIB",
                platform = TargetPlatform.INSTAGRAM_REELS
            ),
            SeedSpec(
                title = "RAGE MOMENT: Kena Snipe di Detik Terakhir Sebelum Booyah",
                score = 93,
                reason = "Ekspresi emosi memuncak & momen kaget cocok untuk audiens gaming.",
                hook = "DETIK TERAKHIR MAU MENANG MALAH KENA HEADSHOT!",
                raw = "Waduh eh apaan tuh anu dari belakang kayak gak ada step gitu langsung rata dong!",
                clean = "Waduh apaan tuh dari belakang tanpa suara langkah langsung rata dong!",
                startSec = 1040, endSec = 1076,
                thumb = ThumbnailKind.GAMING, gaming = GamingMomentType.RAGE,
                status = ClipStatus.DRAFT, date = null, time = null,
                platform = TargetPlatform.TIKTOK
            ),
            SeedSpec(
                title = "Cara Bikin Hook 3 Detik Pertama Biar Gak Di-Skip",
                score = 92,
                reason = "Struktur kalimat tanya + jeda dramatis menaikkan watch time awal.",
                hook = "90% video gagal FYP gara-gara salah di 3 detik awal!",
                raw = "Eh dengerin ya anu jangan mulai video pakai halo guys kayak gitu langsung kasih konflik!",
                clean = "Dengerin ya: jangan mulai video pakai 'halo guys', langsung kasih konflik di detik pertama!",
                startSec = 1210, endSec = 1255,
                thumb = ThumbnailKind.PODCAST, gaming = GamingMomentType.NONE,
                status = ClipStatus.DRAFT, date = null, time = null,
                platform = TargetPlatform.YOUTUBE_SHORTS
            ),
            SeedSpec(
                title = "Benchmark Rust vs Go: Latency Turun 65% di HP Kentang",
                score = 91,
                reason = "Data angka konkret (65%) memicu komentar dan diskusi developer.",
                hook = "Memory usage turun drastis cuma gara-gara ganti compiler!",
                raw = "Pas kita tes eh Rust itu anu hemat RAM banget gitu dibanding runtime lain yang berat.",
                clean = "Pas kita tes, Rust hemat RAM banget dibanding runtime lain yang jauh lebih berat!",
                startSec = 1400, endSec = 1448,
                thumb = ThumbnailKind.PODCAST, gaming = GamingMomentType.NONE,
                status = ClipStatus.DRAFT, date = null, time = null,
                platform = TargetPlatform.FACEBOOK_FEED
            ),
            SeedSpec(
                title = "MENANG TURNAMEN! Momen Selebrasi Tim AllGamesClips",
                score = 90,
                reason = "Klimaks kemenangan + sorakan tim + cocok diberi efek slow-motion.",
                hook = "AKHIRNYA JUARA 1 SETELAH COMEBACK 0-3!",
                raw = "Anu kita menang guys eh gila banget comeback-nya kayak mimpi asli pecah!",
                clean = "Kita menang guys! Gila banget comeback-nya, berasa mimpi asli pecah!",
                startSec = 1590, endSec = 1635,
                thumb = ThumbnailKind.GAMING, gaming = GamingMomentType.MENANG,
                status = ClipStatus.DIJADWALKAN, date = "26 Nov", time = "21.00 WIB",
                platform = TargetPlatform.INSTAGRAM_REELS
            ),
            SeedSpec(
                title = "Etika & Niat Berkarya Biar Konten Jadi Amal Jariyah",
                score = 89,
                reason = "Nada suara tenang penuh makna, sangat tinggi rasio share di WhatsApp & IG.",
                hook = "Jangan cuma kejar viral, pastikan kontenmu bawa manfaat!",
                raw = "Eh temen-temen anu kalau bikin video kayak gini niatkan juga buat bantu orang lain gitu.",
                clean = "Teman-teman, kalau bikin video niatkan juga untuk membantu dan menginspirasi orang lain.",
                startSec = 1750, endSec = 1805,
                thumb = ThumbnailKind.DAKWAH, gaming = GamingMomentType.NONE,
                status = ClipStatus.DRAFT, date = null, time = null,
                platform = TargetPlatform.FACEBOOK_REELS
            ),
            SeedSpec(
                title = "Kesalahan Terbesar Live Jualan di Jam Sepi",
                score = 88,
                reason = "Solusi langsung untuk UMKM & affiliate TikTok Shop.",
                hook = "Stop live jam segini kalau gak mau penonton nol!",
                raw = "Anu banyak seller eh bingung kenapa sepi kayak gitu padahal jam upload-nya salah.",
                clean = "Banyak seller bingung kenapa sepi, padahal kesalahan utamanya ada di jam tayang!",
                startSec = 1910, endSec = 1955,
                thumb = ThumbnailKind.JUALAN, gaming = GamingMomentType.NONE,
                status = ClipStatus.DRAFT, date = null, time = null,
                platform = TargetPlatform.TIKTOK
            ),
            SeedSpec(
                title = "Kalah Konyol Gara-Gara Salah Pencet Granat Sendiri",
                score = 87,
                reason = "Komedi gaming natural dengan reaksi panik yang menghibur.",
                hook = "Musuh di depan, granatnya malah dilempar ke tembok!",
                raw = "Eh eh lari woy anu salah pencet tombol kayaknya kita mati bareng nih haha!",
                clean = "Lari woy salah pencet tombol! Kita malah tumbang barengan nih haha!",
                startSec = 2050, endSec = 2088,
                thumb = ThumbnailKind.GAMING, gaming = GamingMomentType.MATI,
                status = ClipStatus.DRAFT, date = null, time = null,
                platform = TargetPlatform.YOUTUBE_SHORTS
            ),
            SeedSpec(
                title = "Trik Edit di HP RAM 4GB Anti Lag Tanpa Laptop Mahal",
                score = 86,
                reason = "Target audiens kreator pemula HP yang sangat besar di Indonesia.",
                hook = "Gak perlu PC 20 juta buat ngonten tiap hari!",
                raw = "Eh pakai metode ekstrak audio dulu gitu jadi anu HP RAM kecil juga tetep ngebut.",
                clean = "Pakai metode ekstrak audio dulu, jadi HP RAM kecil juga tetap ngebut tanpa lag!",
                startSec = 2180, endSec = 2225,
                thumb = ThumbnailKind.PODCAST, gaming = GamingMomentType.NONE,
                status = ClipStatus.DRAFT, date = null, time = null,
                platform = TargetPlatform.FACEBOOK_REELS
            ),
            SeedSpec(
                title = "Kenapa Caption Kata-per-Kata Naikkan Retensi 2x Lipat?",
                score = 85,
                reason = "Penjelasan psikologi penonton tanpa suara (muted scroll).",
                hook = "70% orang nonton Reels tanpa nyalain volume!",
                raw = "Jadi kayak teks warna-warni per kata itu eh bikin mata penonton terkunci gitu.",
                clean = "Teks warna-warni kata-per-kata bikin mata penonton terkunci meski volume mati!",
                startSec = 2310, endSec = 2354,
                thumb = ThumbnailKind.PODCAST, gaming = GamingMomentType.NONE,
                status = ClipStatus.DRAFT, date = null, time = null,
                platform = TargetPlatform.INSTAGRAM_REELS
            ),
            SeedSpec(
                title = "Double Kill Sniper Tanpa Scope di Asap Tebal!",
                score = 84,
                reason = "Aksi cepat cocok dengan efek Slow-Motion 0.5x & SFX Boom.",
                hook = "TEMBAK ASAL DI DALAM SMOKE DAPET 2 KILL!",
                raw = "Anu gak keliatan apa-apa eh gue tembak aja gitu eh dapet dua dong!",
                clean = "Gak keliatan apa-apa, gue tembak aja langsung dapet dua kill sekaligus!",
                startSec = 2440, endSec = 2475,
                thumb = ThumbnailKind.GAMING, gaming = GamingMomentType.KILL,
                status = ClipStatus.DRAFT, date = null, time = null,
                platform = TargetPlatform.TIKTOK
            ),
            SeedSpec(
                title = "Pesan Penutup: Konsistensi 30 Hari Mengubah Nasib Kreator",
                score = 83,
                reason = "Penutup emosional & motivasi tinggi, cocok untuk YouTube Long & Reels.",
                hook = "Yang membedakan kreator sukses cuma satu: konsisten 30 hari!",
                raw = "Eh percaya deh anu upload tiap hari pakai jadwal otomatis kayak gini hasilnya pasti kerasa.",
                clean = "Percaya deh, upload rutin setiap hari dengan jadwal otomatis pasti terasa hasilnya!",
                startSec = 2560, endSec = 2610,
                thumb = ThumbnailKind.DAKWAH, gaming = GamingMomentType.NONE,
                status = ClipStatus.TERPUBLISH, date = "22 Nov", time = "19.00 WIB",
                platform = TargetPlatform.YOUTUBE_LONG
            )
        )

        val mainClips = mainSpecs.mapIndexed { idx, spec ->
            val isGaming = spec.gaming != GamingMomentType.NONE
            val dur = (spec.endSec - spec.startSec).coerceAtLeast(20)
            ViralClip(
                id = "clip_main_${(idx + 1).toString().padStart(2, '0')}",
                projectId = "proj_main_45m",
                indexOrder = idx + 1,
                title = spec.title,
                viralityScore = spec.score,
                reason = spec.reason,
                suitabilityLabel = "Cocok untuk TikTok / YouTube / IG / FB",
                startSec = spec.startSec,
                endSec = spec.endSec,
                transcriptRaw = spec.raw,
                transcriptClean = spec.clean,
                thumbnailKind = spec.thumb,
                gamingMoment = spec.gaming,
                status = spec.status,
                scheduledDateLabel = spec.date,
                scheduledTimeLabel = spec.time,
                selectedPlatform = spec.platform,
                captionStyleName = if (isGaming) "Gaming Neon" else "Hormozi Bold",
                captionColorHex = if (isGaming) 0xFFA855F7 else 0xFFFBBF24,
                captionPosition = CaptionPosition.BOTTOM,
                autoReframe = true,
                autoZoom = true,
                removeSilence = true,
                fillerRemover = true,
                bRollAdded = idx % 3 == 0,
                gamingModeEnabled = isGaming,
                gamingSlowMo = spec.gaming == GamingMomentType.KILL || spec.gaming == GamingMomentType.MENANG,
                gamingOverlayEnabled = isGaming,
                bgMusicName = if (isGaming) "Epic Esports Drop" else "Phonk Viral Beat",
                bgMusicVolume = 0.35f,
                sfxWhoosh = true,
                sfxBoom = isGaming,
                templateId = if (isGaming) "tpl_gaming_14" else "tpl_podcast_01",
                platformVersions = buildPlatformVersions(spec.title, spec.hook, spec.reason, dur, isGaming)
            )
        }

        val extraClips = listOf(
            ViralClip(
                id = "clip_agc_01",
                projectId = "proj_gaming_agc",
                indexOrder = 1,
                title = "Ace 5 Kill Tercepat di Map Cyber Arena",
                viralityScore = 96,
                reason = "5 eliminasi dalam 18 detik, ritme sangat padat untuk Shorts & TikTok.",
                suitabilityLabel = "Cocok untuk TikTok / YouTube / IG / FB",
                startSec = 95, endSec = 130,
                transcriptRaw = "Eh maju terus anu sikat semua satu dua tiga empat lima rata!",
                transcriptClean = "Maju terus sikat semua: satu, dua, tiga, empat, lima rata!",
                thumbnailKind = ThumbnailKind.GAMING,
                gamingMoment = GamingMomentType.KILL,
                status = ClipStatus.DRAFT,
                scheduledDateLabel = null, scheduledTimeLabel = null,
                selectedPlatform = TargetPlatform.TIKTOK,
                captionStyleName = "Esports HUD",
                captionColorHex = 0xFF10B981,
                captionPosition = CaptionPosition.BOTTOM,
                autoReframe = true, autoZoom = true, removeSilence = true, fillerRemover = true,
                bRollAdded = false, gamingModeEnabled = true, gamingSlowMo = true, gamingOverlayEnabled = true,
                bgMusicName = "Epic Esports Drop", bgMusicVolume = 0.45f, sfxWhoosh = true, sfxBoom = true,
                templateId = "tpl_gaming_14",
                platformVersions = buildPlatformVersions(
                    "Ace 5 Kill Tercepat di Map Cyber Arena",
                    "18 DETIK RATAIN SATU SQUAD PENUH!",
                    "Aksi Ace 5 kill super cepat tanpa reload.",
                    35, true
                )
            ),
            ViralClip(
                id = "clip_agc_02",
                projectId = "proj_gaming_agc",
                indexOrder = 2,
                title = "Prank Teman Sendiri Pas Lagi Defuse Bomb",
                viralityScore = 91,
                reason = "Momen lucu & kalah konyol memicu komentar ramai.",
                suitabilityLabel = "Cocok untuk TikTok / YouTube / IG / FB",
                startSec = 340, endSec = 378,
                transcriptRaw = "Anu jangan ditutup pintunya woy eh kalah deh kita haha!",
                transcriptClean = "Jangan ditutup pintunya woy, malah kalah konyol kita haha!",
                thumbnailKind = ThumbnailKind.GAMING,
                gamingMoment = GamingMomentType.KALAH,
                status = ClipStatus.DIJADWALKAN,
                scheduledDateLabel = "27 Nov", scheduledTimeLabel = "21.00 WIB",
                selectedPlatform = TargetPlatform.YOUTUBE_SHORTS,
                captionStyleName = "Komik Box",
                captionColorHex = 0xFFFACC15,
                captionPosition = CaptionPosition.BOTTOM,
                autoReframe = true, autoZoom = true, removeSilence = true, fillerRemover = true,
                bRollAdded = false, gamingModeEnabled = true, gamingSlowMo = false, gamingOverlayEnabled = true,
                bgMusicName = "Phonk Viral Beat", bgMusicVolume = 0.3f, sfxWhoosh = true, sfxBoom = false,
                templateId = "tpl_gaming_15",
                platformVersions = buildPlatformVersions(
                    "Prank Teman Sendiri Pas Lagi Defuse Bomb",
                    "TINGGAL DEFUSE MALAH KE-TROLL TEMAN!",
                    "Komedi gaming saat momen genting defuse.",
                    38, true
                )
            ),
            ViralClip(
                id = "clip_agc_03",
                projectId = "proj_gaming_agc",
                indexOrder = 3,
                title = "Setup Sensitivitas Aim Pro Player Mobile",
                viralityScore = 88,
                reason = "Konten tutorial gaming yang selalu dicari dan di-save.",
                suitabilityLabel = "Cocok untuk TikTok / YouTube / IG / FB",
                startSec = 600, endSec = 648,
                transcriptRaw = "Eh pakai gyro segini ya anu biar recoil senjata kayak gak gerak.",
                transcriptClean = "Pakai settingan gyro ini biar recoil senjata stabil dan aim nempel!",
                thumbnailKind = ThumbnailKind.GAMING,
                gamingMoment = GamingMomentType.MENANG,
                status = ClipStatus.DRAFT,
                scheduledDateLabel = null, scheduledTimeLabel = null,
                selectedPlatform = TargetPlatform.INSTAGRAM_REELS,
                captionStyleName = "Gaming Neon",
                captionColorHex = 0xFFA855F7,
                captionPosition = CaptionPosition.BOTTOM,
                autoReframe = true, autoZoom = true, removeSilence = true, fillerRemover = true,
                bRollAdded = true, gamingModeEnabled = true, gamingSlowMo = false, gamingOverlayEnabled = true,
                bgMusicName = "Cyberpunk Bassline", bgMusicVolume = 0.25f, sfxWhoosh = true, sfxBoom = true,
                templateId = "tpl_gaming_16",
                platformVersions = buildPlatformVersions(
                    "Setup Sensitivitas Aim Pro Player Mobile",
                    "Salin settingan ini biar aim langsung nempel kepala!",
                    "Panduan setting sensitivitas aim paling stabil.",
                    48, true
                )
            ),
            ViralClip(
                id = "clip_yt_01",
                projectId = "proj_yt_bisnis",
                indexOrder = 1,
                title = "Cara Bikin Penonton Checkout dalam 15 Detik Live",
                viralityScore = 93,
                reason = "Teknik copywriting lisan langsung mendongkrak konversi jualan.",
                suitabilityLabel = "Cocok untuk TikTok / YouTube / IG / FB",
                startSec = 140, endSec = 185,
                transcriptRaw = "Eh pas mulai live anu sebutin masalah pembelinya kayak gitu baru kasih promo.",
                transcriptClean = "Pas mulai live, sebutkan masalah pembelinya dulu baru tunjukkan promo!",
                thumbnailKind = ThumbnailKind.JUALAN,
                gamingMoment = GamingMomentType.NONE,
                status = ClipStatus.TERPUBLISH,
                scheduledDateLabel = "21 Nov", scheduledTimeLabel = "12.15 WIB",
                selectedPlatform = TargetPlatform.TIKTOK,
                captionStyleName = "Jualan Flash",
                captionColorHex = 0xFFFB923C,
                captionPosition = CaptionPosition.BOTTOM,
                autoReframe = true, autoZoom = true, removeSilence = true, fillerRemover = true,
                bRollAdded = true, gamingModeEnabled = false, gamingSlowMo = false, gamingOverlayEnabled = false,
                bgMusicName = "Acoustic Inspirasi", bgMusicVolume = 0.2f, sfxWhoosh = true, sfxBoom = false,
                templateId = "tpl_jualan_40",
                platformVersions = buildPlatformVersions(
                    "Cara Bikin Penonton Checkout dalam 15 Detik Live",
                    "Jangan jelasin spek dulu sebelum bilang kalimat ini!",
                    "Formula opening live shopping agar konversi tinggi.",
                    45, false
                )
            ),
            ViralClip(
                id = "clip_yt_02",
                projectId = "proj_yt_bisnis",
                indexOrder = 2,
                title = "Pola Upload 4 Platform Sekaligus Tanpa Burnout",
                viralityScore = 89,
                reason = "Workflow efisien kreator solo, tinggi minat di YouTube & FB.",
                suitabilityLabel = "Cocok untuk TikTok / YouTube / IG / FB",
                startSec = 420, endSec = 468,
                transcriptRaw = "Anu jangan edit ulang empat kali eh cukup pakai auto reframe dan caption otomatis.",
                transcriptClean = "Jangan edit ulang empat kali! Cukup pakai auto-reframe dan auto-caption sekali klik.",
                thumbnailKind = ThumbnailKind.JUALAN,
                gamingMoment = GamingMomentType.NONE,
                status = ClipStatus.DRAFT,
                scheduledDateLabel = null, scheduledTimeLabel = null,
                selectedPlatform = TargetPlatform.YOUTUBE_SHORTS,
                captionStyleName = "Gold Luxury",
                captionColorHex = 0xFFF59E0B,
                captionPosition = CaptionPosition.BOTTOM,
                autoReframe = true, autoZoom = true, removeSilence = true, fillerRemover = true,
                bRollAdded = false, gamingModeEnabled = false, gamingSlowMo = false, gamingOverlayEnabled = false,
                bgMusicName = "Lo-Fi Podcast Hangat", bgMusicVolume = 0.25f, sfxWhoosh = true, sfxBoom = false,
                templateId = "tpl_jualan_41",
                platformVersions = buildPlatformVersions(
                    "Pola Upload 4 Platform Sekaligus Tanpa Burnout",
                    "1 file master cukup untuk TikTok, Shorts, Reels, dan FB!",
                    "Strategi distribusi multi-platform hemat waktu.",
                    48, false
                )
            )
        )

        return mainClips + extraClips
    }

    fun generateClipsForNewVideo(
        projectId: String,
        sourceTitle: String,
        promptHint: String,
        gamingMode: Boolean,
        count: Int = 16
    ): List<ViralClip> {
        val cleanPrompt = promptHint.trim()
        val isRust = cleanPrompt.contains("rust", ignoreCase = true)
        val isFunny = cleanPrompt.contains("lucu", ignoreCase = true) || cleanPrompt.contains("ngakak", ignoreCase = true)
        val isKill = cleanPrompt.contains("kill", ignoreCase = true) || gamingMode

        val templatesTitles = if (isRust) {
            listOf(
                "Kenapa Ownership Rust Bikin Server Zero Crash",
                "Borrow Checker Bikin Pusing Tapi Menyelamatkan Produksi",
                "Migrasi Microservice ke Rust: Hemat Biaya Server 60%",
                "Async Tokio vs Go Goroutine di Beban Tinggi",
                "Error Handling Rust Pakai Result Tanpa Null Pointer"
            )
        } else if (isKill) {
            listOf(
                "Triple Kill Cepat Pakai Senjata Jarak Dekat!",
                "Momen Clutch 1v3 di Zona Terakhir",
                "Headshot Jarak Jauh Tanpa Miss!",
                "Rage Musuh Setelah Kena Counter Attack",
                "Kombo Skill Menang Telak di Final"
            )
        } else if (isFunny) {
            listOf(
                "Ketawa Pecah Gara-Gara Salah Baca Komentar Live",
                "Momen Kaget Audio Bocor Saat Syuting Serius",
                "Reaksi Kocak Tantangan Spontan Studio",
                "Plot Twist Jawaban Tamu yang Bikin Ngakak",
                "Gagal Fokus Gara-Gara Kucing Masuk Frame"
            )
        } else {
            listOf(
                "Hook Pembuka Paling Tajam dari $sourceTitle",
                "Rahasia Terbesar yang Jarang Dibahas Kreator",
                "Kesalahan Pemula yang Bikin Viewers Pergi",
                "Studi Kasus Nyata: Hasil Naik Dalam 7 Hari",
                "Debat Panas Soal Tren Konten 2026"
            )
        }

        val thumb = when {
            isKill -> ThumbnailKind.GAMING
            sourceTitle.contains("dakwah", ignoreCase = true) -> ThumbnailKind.DAKWAH
            sourceTitle.contains("jualan", ignoreCase = true) -> ThumbnailKind.JUALAN
            else -> ThumbnailKind.PODCAST
        }

        return (1..count).map { idx ->
            val baseTitle = templatesTitles[(idx - 1) % templatesTitles.size]
            val fullTitle = if (idx <= templatesTitles.size) baseTitle else "$baseTitle (Part ${idx})"
            val score = (98 - idx * 2).coerceIn(72, 99)
            val start = idx * 145
            val dur = 28 + (idx % 4) * 8
            val gType = when {
                isKill && idx % 4 == 1 -> GamingMomentType.KILL
                isKill && idx % 4 == 2 -> GamingMomentType.MENANG
                isKill && idx % 4 == 3 -> GamingMomentType.RAGE
                isFunny -> GamingMomentType.LUCU
                else -> GamingMomentType.NONE
            }
            val reason = if (cleanPrompt.isNotBlank()) {
                "Cocok dengan prompt \"$cleanPrompt\" — puncak emosi & audio jernih (Skor AI $score/100)."
            } else {
                "AI mendeteksi hook kuat di 3 detik awal + dinamika suara tinggi pada segmen ini."
            }
            val raw = "Eh jadi anu bagian ini tuh kayak penting banget buat $fullTitle gitu lho guys!"
            val clean = "Bagian ini paling krusial untuk memahami $fullTitle, wajib dicoba!"
            val plat = TargetPlatform.entries[(idx - 1) % 4]
            ViralClip(
                id = "${projectId}_clip_${idx.toString().padStart(2, '0')}",
                projectId = projectId,
                indexOrder = idx,
                title = fullTitle,
                viralityScore = score,
                reason = reason,
                suitabilityLabel = "Cocok untuk TikTok / YouTube / IG / FB",
                startSec = start,
                endSec = start + dur,
                transcriptRaw = raw,
                transcriptClean = clean,
                thumbnailKind = thumb,
                gamingMoment = gType,
                status = ClipStatus.DRAFT,
                scheduledDateLabel = null,
                scheduledTimeLabel = null,
                selectedPlatform = plat,
                captionStyleName = if (gamingMode) "Gaming Neon" else "Hormozi Bold",
                captionColorHex = if (gamingMode) 0xFFA855F7 else 0xFFFBBF24,
                captionPosition = CaptionPosition.BOTTOM,
                autoReframe = true,
                autoZoom = true,
                removeSilence = true,
                fillerRemover = true,
                bRollAdded = idx % 2 == 0,
                gamingModeEnabled = gamingMode,
                gamingSlowMo = gType == GamingMomentType.KILL,
                gamingOverlayEnabled = gamingMode,
                bgMusicName = if (gamingMode) "Epic Esports Drop" else "Phonk Viral Beat",
                bgMusicVolume = 0.35f,
                sfxWhoosh = true,
                sfxBoom = gamingMode,
                templateId = if (gamingMode) "tpl_gaming_14" else "tpl_podcast_01",
                platformVersions = buildPlatformVersions(fullTitle, fullTitle, reason, dur, gamingMode)
            )
        }
    }
}
