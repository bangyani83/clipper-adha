package com.example.data.local

import com.example.data.model.BrandKitConfig
import com.example.data.model.ClipStatus
import com.example.data.model.ScheduledPostItem
import com.example.data.model.TargetPlatform
import com.example.data.model.VideoProject
import com.example.data.model.ViralClip
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ClipperRepository(
    private val dao: ClipperDao,
    val prefsRepo: UserPreferencesRepository
) {
    val allProjects: Flow<List<VideoProject>> = dao.observeAllProjects().map { list ->
        list.map { it.toDomain() }
    }

    val allClips: Flow<List<ViralClip>> = dao.observeAllClips().map { list ->
        list.map { it.toDomain() }
    }

    val scheduledPosts: Flow<List<ScheduledPostItem>> = dao.observeScheduledPosts().map { list ->
        list.map { it.toDomain() }
    }

    suspend fun ensureSeeded() {
        if (dao.getProjectCount() == 0) {
            seedInitialData()
        }
    }

    suspend fun resetDemoData() {
        val projects = dao.observeAllProjects()
        // Clear and re-seed cleanly
        ClipperPresets.seedProjects().forEach { dao.deleteProjectById(it.id) }
        dao.clearScheduledPosts()
        seedInitialData()
        prefsRepo.resetMonthlyCredits()
    }

    private suspend fun seedInitialData() {
        val projects = ClipperPresets.seedProjects()
        val clips = ClipperPresets.seedClips()
        dao.insertProjects(projects.map { it.toEntity() })
        dao.insertClips(clips.map { it.toEntity() })

        // Pre-populate a few scheduled items so dashboard & calendar look immediately rich
        val initialSchedule = clips.filter { it.status == ClipStatus.DIJADWALKAN }.mapIndexed { idx, clip ->
            ScheduledPostItem(
                id = "sched_seed_${idx + 1}",
                clipId = clip.id,
                clipTitle = clip.title,
                projectTitle = projects.firstOrNull { it.id == clip.projectId }?.title ?: "Project",
                platform = clip.selectedPlatform,
                dayNumber = idx + 1,
                dateLabel = clip.scheduledDateLabel ?: "24 Nov",
                timeLabel = clip.scheduledTimeLabel ?: "19.00 WIB",
                captionPreview = clip.activeVersion.caption,
                status = ClipStatus.DIJADWALKAN
            )
        }
        dao.insertScheduledPosts(initialSchedule.map { it.toEntity() })
    }

    suspend fun saveNewProjectWithClips(project: VideoProject, clips: List<ViralClip>) {
        dao.insertProject(project.toEntity())
        dao.insertClips(clips.map { it.toEntity() })
    }

    suspend fun updateClip(clip: ViralClip) {
        dao.insertClip(clip.toEntity())
    }

    suspend fun deleteProject(projectId: String) {
        dao.deleteClipsByProjectId(projectId)
        dao.deleteProjectById(projectId)
    }

    suspend fun applyBrandKitToAll(config: BrandKitConfig) {
        prefsRepo.updateBrandKit(config)
        val tpl = ClipperPresets.templates.firstOrNull { it.id == config.activeTemplateId }
        val captionStyle = tpl?.captionStyle ?: "Hormozi Bold"
        dao.applyBrandKitToAllClips(
            captionStyle = captionStyle,
            colorHex = config.accentColorHex,
            templateId = config.activeTemplateId
        )
    }

    suspend fun generate30DaySchedule(clips: List<ViralClip>, projects: List<VideoProject>): List<ScheduledPostItem> {
        if (clips.isEmpty()) return emptyList()
        val platforms = listOf(
            TargetPlatform.TIKTOK,
            TargetPlatform.YOUTUBE_SHORTS,
            TargetPlatform.INSTAGRAM_REELS,
            TargetPlatform.FACEBOOK_REELS
        )
        val times = listOf("19.00 WIB", "12.15 WIB", "21.00 WIB", "07.30 WIB")
        val monthsDayOffset = 24 // Start from 24 Nov
        val generated = (1..30).map { day ->
            val clip = clips[(day - 1) % clips.size]
            val platform = platforms[(day - 1) % platforms.size]
            val timeSlot = times[(day - 1) % times.size]
            val projTitle = projects.firstOrNull { it.id == clip.projectId }?.title ?: "Clipper Adha Project"
            val dayOfMonth = monthsDayOffset + (day - 1)
            val dateLabel = if (dayOfMonth <= 30) "$dayOfMonth Nov" else "${dayOfMonth - 30} Des"
            val version = clip.platformVersions[platform] ?: clip.activeVersion

            ScheduledPostItem(
                id = "sched_30d_${day.toString().padStart(2, '0')}",
                clipId = clip.id,
                clipTitle = clip.title,
                projectTitle = projTitle,
                platform = platform,
                dayNumber = day,
                dateLabel = dateLabel,
                timeLabel = timeSlot,
                captionPreview = version.caption,
                status = ClipStatus.DIJADWALKAN
            )
        }
        dao.clearScheduledPosts()
        dao.insertScheduledPosts(generated.map { it.toEntity() })

        // Mark involved clips as scheduled
        clips.take(15).forEachIndexed { idx, clip ->
            val slot = generated.getOrNull(idx)
            if (slot != null && clip.status == ClipStatus.DRAFT) {
                dao.insertClip(
                    clip.copy(
                        status = ClipStatus.DIJADWALKAN,
                        scheduledDateLabel = slot.dateLabel,
                        scheduledTimeLabel = slot.timeLabel
                    ).toEntity()
                )
            }
        }
        return generated
    }
}
