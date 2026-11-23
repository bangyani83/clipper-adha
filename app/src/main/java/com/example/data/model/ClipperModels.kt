package com.example.data.model

enum class MainTab(val label: String) {
    BERANDA("Beranda"),
    BUAT_CLIP("Buat Clip"),
    PROJECT_SAYA("Project Saya"),
    PROFIL("Profil")
}

enum class ProjectFolder(val label: String) {
    SEMUA("Semua"),
    YOUTUBE("YouTube"),
    TIKTOK("TikTok"),
    GAMING("Gaming")
}

enum class ClipStatus(val label: String) {
    DRAFT("Draft"),
    DIJADWALKAN("Dijadwalkan"),
    TERPUBLISH("Terpublish")
}

enum class TargetPlatform(
    val shortName: String,
    val fullName: String,
    val ratioLabel: String,
    val aspectRatio: Float,
    val defaultMaxSec: Int
) {
    TIKTOK("TikTok", "TikTok", "9:16", 9f / 16f, 60),
    YOUTUBE_SHORTS("YT Shorts", "YouTube Shorts", "9:16", 9f / 16f, 60),
    INSTAGRAM_REELS("IG Reels", "Instagram Reels", "9:16", 9f / 16f, 90),
    FACEBOOK_REELS("FB Reels", "Facebook Reels", "9:16", 9f / 16f, 60),
    FACEBOOK_FEED("FB Feed", "Facebook Feed", "1:1", 1f, 90),
    YOUTUBE_LONG("YT Long", "YouTube Utama", "16:9", 16f / 9f, 240)
}

enum class GamingMomentType(val label: String, val overlayText: String) {
    NONE("Normal", ""),
    KILL("Kill", "KILL +1"),
    MATI("Mati", "TERELIMINASI"),
    MENANG("Menang", "MENANG! GG"),
    KALAH("Kalah", "NICE TRY!"),
    LUCU("Lucu", "NGAKAK ABIS"),
    RAGE("Rage", "RAGE MODE!")
}

enum class ThumbnailKind(val label: String) {
    PODCAST("Podcast Studio"),
    GAMING("Gaming Arena"),
    DAKWAH("Inspirasi & Dakwah"),
    JUALAN("Live Jualan")
}

enum class CaptionPosition(val label: String) {
    TOP("Atas"),
    CENTER("Tengah"),
    BOTTOM("Bawah")
}

data class PlatformVersion(
    val platform: TargetPlatform,
    val hookText: String,
    val seoTitle: String,
    val caption: String,
    val hashtags: String,
    val durationSec: Int,
    val coverStyle: String
)

data class ViralClip(
    val id: String,
    val projectId: String,
    val indexOrder: Int,
    val title: String,
    val viralityScore: Int,
    val reason: String,
    val suitabilityLabel: String,
    val startSec: Int,
    val endSec: Int,
    val transcriptRaw: String,
    val transcriptClean: String,
    val thumbnailKind: ThumbnailKind,
    val gamingMoment: GamingMomentType,
    val status: ClipStatus,
    val scheduledDateLabel: String?,
    val scheduledTimeLabel: String?,
    val selectedPlatform: TargetPlatform,
    val captionStyleName: String,
    val captionColorHex: Long,
    val captionPosition: CaptionPosition,
    val autoReframe: Boolean,
    val autoZoom: Boolean,
    val removeSilence: Boolean,
    val fillerRemover: Boolean,
    val bRollAdded: Boolean,
    val gamingModeEnabled: Boolean,
    val gamingSlowMo: Boolean,
    val gamingOverlayEnabled: Boolean,
    val bgMusicName: String,
    val bgMusicVolume: Float,
    val sfxWhoosh: Boolean,
    val sfxBoom: Boolean,
    val templateId: String,
    val platformVersions: Map<TargetPlatform, PlatformVersion>
) {
    val activeVersion: PlatformVersion
        get() = platformVersions[selectedPlatform]
            ?: platformVersions[TargetPlatform.TIKTOK]
            ?: PlatformVersion(
                platform = selectedPlatform,
                hookText = title,
                seoTitle = title,
                caption = title,
                hashtags = "#viral #fyp #clipperadha",
                durationSec = (endSec - startSec).coerceAtLeast(15),
                coverStyle = "Purple Gold Glow"
            )

    val displayTranscript: String
        get() = if (fillerRemover) transcriptClean else transcriptRaw
}

data class VideoProject(
    val id: String,
    val title: String,
    val sourceType: String,
    val sourceUrl: String,
    val folder: ProjectFolder,
    val fileSizeMb: Int,
    val durationMinutes: Int,
    val resolutionLabel: String,
    val compressionMode: String,
    val audioExtractedMb: Float,
    val createdAtLabel: String,
    val isSplitPart: Boolean,
    val partLabel: String,
    val thumbnailKind: ThumbnailKind,
    val clipCount: Int
)

data class ScheduledPostItem(
    val id: String,
    val clipId: String,
    val clipTitle: String,
    val projectTitle: String,
    val platform: TargetPlatform,
    val dayNumber: Int,
    val dateLabel: String,
    val timeLabel: String,
    val captionPreview: String,
    val status: ClipStatus
)

data class BrandKitConfig(
    val brandName: String = "Adha Creator Studio",
    val logoInitials: String = "CA",
    val primaryColorHex: Long = 0xFF8B5CF6,
    val accentColorHex: Long = 0xFFF59E0B,
    val fontName: String = "Outfit Bold",
    val introEnabled: Boolean = true,
    val introText: String = "CLIPPER ADHA PRO",
    val outroEnabled: Boolean = true,
    val outroText: String = "Follow & Subscribe!",
    val watermarkEnabled: Boolean = false,
    val activeTemplateId: String = "tpl_podcast_01"
)

data class VideoTemplate(
    val id: String,
    val name: String,
    val category: String,
    val captionStyle: String,
    val accentHex: Long,
    val thumbnailKind: ThumbnailKind,
    val badge: String
)

data class CaptionStylePreset(
    val id: String,
    val name: String,
    val category: String,
    val accentHex: Long,
    val isUppercase: Boolean,
    val hasGlow: Boolean
)

data class UserPreferences(
    val remainingCreditsMinutes: Int = MONTHLY_QUOTA_MINUTES,
    val dataSaverMode: Boolean = true,
    val gamingModeDefault: Boolean = false,
    val autoCompressOver80Mb: Boolean = true,
    val pwaInstalled: Boolean = false,
    val simulateLowRamWarning: Boolean = false
) {
    val usedCreditsMinutes: Int
        get() = (MONTHLY_QUOTA_MINUTES - remainingCreditsMinutes).coerceIn(0, MONTHLY_QUOTA_MINUTES)

    companion object {
        const val MONTHLY_QUOTA_MINUTES = 1000
    }
}

sealed class SubScreen {
    data object None : SubScreen()
    data class Editor(val clipId: String) : SubScreen()
    data class Publish(val clipId: String) : SubScreen()
    data object AutoSchedule30 : SubScreen()
    data object BrandKitManager : SubScreen()
    data object PwaManifestInspector : SubScreen()
}
