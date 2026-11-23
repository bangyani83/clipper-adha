package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.CaptionPosition
import com.example.data.model.ClipStatus
import com.example.data.model.GamingMomentType
import com.example.data.model.PlatformVersion
import com.example.data.model.ProjectFolder
import com.example.data.model.ScheduledPostItem
import com.example.data.model.TargetPlatform
import com.example.data.model.ThumbnailKind
import com.example.data.model.VideoProject
import com.example.data.model.ViralClip
import org.json.JSONArray
import org.json.JSONObject

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val title: String,
    val sourceType: String,
    val sourceUrl: String,
    val folderName: String,
    val fileSizeMb: Int,
    val durationMinutes: Int,
    val resolutionLabel: String,
    val compressionMode: String,
    val audioExtractedMb: Float,
    val createdAtLabel: String,
    val isSplitPart: Boolean,
    val partLabel: String,
    val thumbnailKindName: String,
    val clipCount: Int,
    val createdAtEpoch: Long = System.currentTimeMillis()
)

@Entity(tableName = "clips")
data class ClipEntity(
    @PrimaryKey val id: String,
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
    val thumbnailKindName: String,
    val gamingMomentName: String,
    val statusName: String,
    val scheduledDateLabel: String?,
    val scheduledTimeLabel: String?,
    val selectedPlatformName: String,
    val captionStyleName: String,
    val captionColorHex: Long,
    val captionPositionName: String,
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
    val platformVersionsJson: String
)

@Entity(tableName = "scheduled_posts")
data class ScheduledPostEntity(
    @PrimaryKey val id: String,
    val clipId: String,
    val clipTitle: String,
    val projectTitle: String,
    val platformName: String,
    val dayNumber: Int,
    val dateLabel: String,
    val timeLabel: String,
    val captionPreview: String,
    val statusName: String
)

fun ProjectEntity.toDomain(): VideoProject = VideoProject(
    id = id,
    title = title,
    sourceType = sourceType,
    sourceUrl = sourceUrl,
    folder = ProjectFolder.entries.firstOrNull { it.name == folderName } ?: ProjectFolder.TIKTOK,
    fileSizeMb = fileSizeMb,
    durationMinutes = durationMinutes,
    resolutionLabel = resolutionLabel,
    compressionMode = compressionMode,
    audioExtractedMb = audioExtractedMb,
    createdAtLabel = createdAtLabel,
    isSplitPart = isSplitPart,
    partLabel = partLabel,
    thumbnailKind = ThumbnailKind.entries.firstOrNull { it.name == thumbnailKindName } ?: ThumbnailKind.PODCAST,
    clipCount = clipCount
)

fun VideoProject.toEntity(): ProjectEntity = ProjectEntity(
    id = id,
    title = title,
    sourceType = sourceType,
    sourceUrl = sourceUrl,
    folderName = folder.name,
    fileSizeMb = fileSizeMb,
    durationMinutes = durationMinutes,
    resolutionLabel = resolutionLabel,
    compressionMode = compressionMode,
    audioExtractedMb = audioExtractedMb,
    createdAtLabel = createdAtLabel,
    isSplitPart = isSplitPart,
    partLabel = partLabel,
    thumbnailKindName = thumbnailKind.name,
    clipCount = clipCount
)

fun serializePlatformVersions(map: Map<TargetPlatform, PlatformVersion>): String {
    val array = JSONArray()
    map.values.forEach { v ->
        val obj = JSONObject().apply {
            put("platform", v.platform.name)
            put("hookText", v.hookText)
            put("seoTitle", v.seoTitle)
            put("caption", v.caption)
            put("hashtags", v.hashtags)
            put("durationSec", v.durationSec)
            put("coverStyle", v.coverStyle)
        }
        array.put(obj)
    }
    return array.toString()
}

fun deserializePlatformVersions(json: String): Map<TargetPlatform, PlatformVersion> {
    val result = mutableMapOf<TargetPlatform, PlatformVersion>()
    runCatching {
        val array = JSONArray(json)
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val platform = TargetPlatform.entries.firstOrNull { it.name == obj.optString("platform") }
                ?: continue
            result[platform] = PlatformVersion(
                platform = platform,
                hookText = obj.optString("hookText"),
                seoTitle = obj.optString("seoTitle"),
                caption = obj.optString("caption"),
                hashtags = obj.optString("hashtags"),
                durationSec = obj.optInt("durationSec", 45),
                coverStyle = obj.optString("coverStyle", "Viral Cover")
            )
        }
    }
    return result
}

fun ClipEntity.toDomain(): ViralClip = ViralClip(
    id = id,
    projectId = projectId,
    indexOrder = indexOrder,
    title = title,
    viralityScore = viralityScore,
    reason = reason,
    suitabilityLabel = suitabilityLabel,
    startSec = startSec,
    endSec = endSec,
    transcriptRaw = transcriptRaw,
    transcriptClean = transcriptClean,
    thumbnailKind = ThumbnailKind.entries.firstOrNull { it.name == thumbnailKindName } ?: ThumbnailKind.PODCAST,
    gamingMoment = GamingMomentType.entries.firstOrNull { it.name == gamingMomentName } ?: GamingMomentType.NONE,
    status = ClipStatus.entries.firstOrNull { it.name == statusName } ?: ClipStatus.DRAFT,
    scheduledDateLabel = scheduledDateLabel,
    scheduledTimeLabel = scheduledTimeLabel,
    selectedPlatform = TargetPlatform.entries.firstOrNull { it.name == selectedPlatformName } ?: TargetPlatform.TIKTOK,
    captionStyleName = captionStyleName,
    captionColorHex = captionColorHex,
    captionPosition = CaptionPosition.entries.firstOrNull { it.name == captionPositionName } ?: CaptionPosition.BOTTOM,
    autoReframe = autoReframe,
    autoZoom = autoZoom,
    removeSilence = removeSilence,
    fillerRemover = fillerRemover,
    bRollAdded = bRollAdded,
    gamingModeEnabled = gamingModeEnabled,
    gamingSlowMo = gamingSlowMo,
    gamingOverlayEnabled = gamingOverlayEnabled,
    bgMusicName = bgMusicName,
    bgMusicVolume = bgMusicVolume,
    sfxWhoosh = sfxWhoosh,
    sfxBoom = sfxBoom,
    templateId = templateId,
    platformVersions = deserializePlatformVersions(platformVersionsJson)
)

fun ViralClip.toEntity(): ClipEntity = ClipEntity(
    id = id,
    projectId = projectId,
    indexOrder = indexOrder,
    title = title,
    viralityScore = viralityScore,
    reason = reason,
    suitabilityLabel = suitabilityLabel,
    startSec = startSec,
    endSec = endSec,
    transcriptRaw = transcriptRaw,
    transcriptClean = transcriptClean,
    thumbnailKindName = thumbnailKind.name,
    gamingMomentName = gamingMoment.name,
    statusName = status.name,
    scheduledDateLabel = scheduledDateLabel,
    scheduledTimeLabel = scheduledTimeLabel,
    selectedPlatformName = selectedPlatform.name,
    captionStyleName = captionStyleName,
    captionColorHex = captionColorHex,
    captionPositionName = captionPosition.name,
    autoReframe = autoReframe,
    autoZoom = autoZoom,
    removeSilence = removeSilence,
    fillerRemover = fillerRemover,
    bRollAdded = bRollAdded,
    gamingModeEnabled = gamingModeEnabled,
    gamingSlowMo = gamingSlowMo,
    gamingOverlayEnabled = gamingOverlayEnabled,
    bgMusicName = bgMusicName,
    bgMusicVolume = bgMusicVolume,
    sfxWhoosh = sfxWhoosh,
    sfxBoom = sfxBoom,
    templateId = templateId,
    platformVersionsJson = serializePlatformVersions(platformVersions)
)

fun ScheduledPostEntity.toDomain(): ScheduledPostItem = ScheduledPostItem(
    id = id,
    clipId = clipId,
    clipTitle = clipTitle,
    projectTitle = projectTitle,
    platform = TargetPlatform.entries.firstOrNull { it.name == platformName } ?: TargetPlatform.TIKTOK,
    dayNumber = dayNumber,
    dateLabel = dateLabel,
    timeLabel = timeLabel,
    captionPreview = captionPreview,
    status = ClipStatus.entries.firstOrNull { it.name == statusName } ?: ClipStatus.DIJADWALKAN
)

fun ScheduledPostItem.toEntity(): ScheduledPostEntity = ScheduledPostEntity(
    id = id,
    clipId = clipId,
    clipTitle = clipTitle,
    projectTitle = projectTitle,
    platformName = platform.name,
    dayNumber = dayNumber,
    dateLabel = dateLabel,
    timeLabel = timeLabel,
    captionPreview = captionPreview,
    statusName = status.name
)
