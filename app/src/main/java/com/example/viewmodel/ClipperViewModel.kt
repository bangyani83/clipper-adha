package com.example.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ClipperDatabase
import com.example.data.local.ClipperPresets
import com.example.data.local.ClipperRepository
import com.example.data.local.UserPreferencesRepository
import com.example.data.model.BrandKitConfig
import com.example.data.model.CaptionPosition
import com.example.data.model.ClipStatus
import com.example.data.model.GamingMomentType
import com.example.data.model.MainTab
import com.example.data.model.ProjectFolder
import com.example.data.model.ScheduledPostItem
import com.example.data.model.SubScreen
import com.example.data.model.TargetPlatform
import com.example.data.model.ThumbnailKind
import com.example.data.model.UserPreferences
import com.example.data.model.VideoProject
import com.example.data.model.VideoTemplate
import com.example.data.model.ViralClip
import com.example.util.DeviceHealthInfo
import com.example.util.DeviceTelemetry
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LinkPreviewInfo(
    val url: String,
    val platformName: String,
    val title: String,
    val durationLabel: String,
    val durationMinutes: Int,
    val estimatedMb: Int,
    val thumbnailKind: ThumbnailKind
)

data class UploadPipelineState(
    val inputMode: String = "UPLOAD", // UPLOAD or LINK
    val fileName: String = "podcast_tech_gaming_45m.mp4",
    val fileSizeMb: Int = 96,
    val durationMinutes: Int = 45,
    val linkInput: String = "",
    val linkPreview: LinkPreviewInfo? = null,
    val compressionMode: String = "Mode Cepat (480p) Hemat Kuota",
    val gamingMode: Boolean = false,
    val customPrompt: String = "",
    val isSplitCompleted: Boolean = false,
    val splitPartsLabel: String = "",
    val isProcessing: Boolean = false,
    val progress: Float = 0f,
    val stageLabel: String = "",
    val chunkIndex: Int = 0,
    val chunkTotal: Int = 48,
    val audioOnlyMb: Float = 3.8f,
    val activeProjectIdForPager: String = "proj_main_45m"
)

data class ExportUiState(
    val isExporting: Boolean = false,
    val exportProgress: Float = 0f,
    val exportStatusText: String = "",
    val completedExportSummary: String? = null
)

data class ClipperUiState(
    val showSplash: Boolean = true,
    val activeTab: MainTab = MainTab.BERANDA,
    val subScreen: SubScreen = SubScreen.None,
    val projects: List<VideoProject> = emptyList(),
    val clips: List<ViralClip> = emptyList(),
    val scheduledPosts: List<ScheduledPostItem> = emptyList(),
    val preferences: UserPreferences = UserPreferences(),
    val brandKit: BrandKitConfig = BrandKitConfig(),
    val deviceHealth: DeviceHealthInfo = DeviceHealthInfo("24.0 GB", 2400, 4096, false),
    val selectedFolder: ProjectFolder = ProjectFolder.SEMUA,
    val selectedStatusFilter: ClipStatus? = null,
    val activePromptFilter: String = "",
    val selectedProjectIdInProjectsTab: String = "proj_main_45m",
    val uploadPipeline: UploadPipelineState = UploadPipelineState(),
    val exportState: ExportUiState = ExportUiState(),
    val showQuotaExhaustedDialog: Boolean = false,
    val snackbarMessage: String? = null
)

class ClipperViewModel(application: Application) : AndroidViewModel(application) {

    private val database = ClipperDatabase.getInstance(application)
    private val prefsRepo = UserPreferencesRepository(application)
    private val repository = ClipperRepository(database.clipperDao(), prefsRepo)

    private val _showSplash = MutableStateFlow(true)
    private val _activeTab = MutableStateFlow(MainTab.BERANDA)
    private val _subScreen = MutableStateFlow<SubScreen>(SubScreen.None)
    private val _deviceHealth = MutableStateFlow(DeviceTelemetry.readDeviceHealth(application))
    private val _selectedFolder = MutableStateFlow(ProjectFolder.SEMUA)
    private val _selectedStatusFilter = MutableStateFlow<ClipStatus?>(null)
    private val _activePromptFilter = MutableStateFlow("")
    private val _selectedProjectIdInProjectsTab = MutableStateFlow("proj_main_45m")
    private val _uploadPipeline = MutableStateFlow(UploadPipelineState())
    private val _exportState = MutableStateFlow(ExportUiState())
    private val _showQuotaExhaustedDialog = MutableStateFlow(false)
    private val _snackbarMessage = MutableStateFlow<String?>(null)

    private var processingJob: Job? = null
    private var exportJob: Job? = null

    private data class NavSlice(
        val showSplash: Boolean,
        val activeTab: MainTab,
        val subScreen: SubScreen,
        val folder: ProjectFolder,
        val statusFilter: ClipStatus?
    )

    private data class FilterSlice(
        val promptFilter: String,
        val selectedProjectId: String,
        val uploadPipeline: UploadPipelineState,
        val exportState: ExportUiState,
        val showQuotaExhaustedDialog: Boolean,
        val snackbar: String?
    )

    private val navFlow = combine(
        _showSplash,
        _activeTab,
        _subScreen,
        _selectedFolder,
        _selectedStatusFilter
    ) { splash, tab, sub, folder, status ->
        NavSlice(splash, tab, sub, folder, status)
    }

    private val filterFlow = combine(
        _activePromptFilter,
        _selectedProjectIdInProjectsTab,
        _uploadPipeline,
        _exportState,
        _showQuotaExhaustedDialog,
        _snackbarMessage
    ) { values ->
        FilterSlice(
            promptFilter = values[0] as String,
            selectedProjectId = values[1] as String,
            uploadPipeline = values[2] as UploadPipelineState,
            exportState = values[3] as ExportUiState,
            showQuotaExhaustedDialog = values[4] as Boolean,
            snackbar = values[5] as String?
        )
    }

    private val dataFlow = combine(
        repository.allProjects,
        repository.allClips,
        repository.scheduledPosts,
        prefsRepo.userPreferences,
        prefsRepo.brandKit
    ) { projects, clips, scheduled, prefs, brand ->
        DataSlice(projects, clips, scheduled, prefs, brand)
    }

    private data class DataSlice(
        val projects: List<VideoProject>,
        val clips: List<ViralClip>,
        val scheduled: List<ScheduledPostItem>,
        val prefs: UserPreferences,
        val brand: BrandKitConfig
    )

    val uiState: StateFlow<ClipperUiState> = combine(
        navFlow,
        filterFlow,
        dataFlow,
        _deviceHealth
    ) { nav, filt, data, health ->
        ClipperUiState(
            showSplash = nav.showSplash,
            activeTab = nav.activeTab,
            subScreen = nav.subScreen,
            projects = data.projects,
            clips = data.clips,
            scheduledPosts = data.scheduled,
            preferences = data.prefs,
            brandKit = data.brand,
            deviceHealth = health,
            selectedFolder = nav.folder,
            selectedStatusFilter = nav.statusFilter,
            activePromptFilter = filt.promptFilter,
            selectedProjectIdInProjectsTab = filt.selectedProjectId,
            uploadPipeline = filt.uploadPipeline,
            exportState = filt.exportState,
            showQuotaExhaustedDialog = filt.showQuotaExhaustedDialog,
            snackbarMessage = filt.snackbar
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ClipperUiState()
    )

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
            val wasResetForNewMonth = prefsRepo.checkAndResetMonthlyQuotaIfNeeded()
            if (wasResetForNewMonth) {
                _snackbarMessage.value = "Tanggal 1 tiba! Kuota bulanan otomatis di-reset menjadi 1000 Menit."
            }
        }
        viewModelScope.launch {
            delay(2000L)
            _showSplash.value = false
        }
    }

    fun dismissQuotaExhaustedDialog() {
        _showQuotaExhaustedDialog.value = false
    }

    fun refreshDeviceHealth() {
        _deviceHealth.value = DeviceTelemetry.readDeviceHealth(getApplication())
    }

    fun selectTab(tab: MainTab) {
        _activeTab.value = tab
        _subScreen.value = SubScreen.None
        refreshDeviceHealth()
    }

    fun openSubScreen(screen: SubScreen) {
        _subScreen.value = screen
    }

    fun closeSubScreen() {
        _subScreen.value = SubScreen.None
        _exportState.value = ExportUiState()
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun showToast(msg: String) {
        _snackbarMessage.value = msg
    }

    fun installPwaShortcut() {
        val requested = DeviceTelemetry.requestPinHomeShortcut(getApplication())
        viewModelScope.launch {
            prefsRepo.setPwaInstalled(true)
        }
        _snackbarMessage.value = if (requested) {
            "Shortcut Clipper Adha ditambahkan ke layar utama HP!"
        } else {
            "Clipper Adha aktif dalam Mode Standalone Offline!"
        }
    }

    // --- Upload & Import Pipeline ---

    fun setInputMode(mode: String) {
        _uploadPipeline.update { it.copy(inputMode = mode) }
    }

    fun onVideoPickedFromPhone(uri: Uri) {
        val meta = DeviceTelemetry.readPickedVideoMeta(getApplication(), uri)
        _uploadPipeline.update {
            it.copy(
                inputMode = "UPLOAD",
                fileName = meta.fileName,
                fileSizeMb = meta.sizeMb,
                durationMinutes = (meta.sizeMb / 2).coerceIn(3, 60),
                isSplitCompleted = false,
                splitPartsLabel = "",
                chunkTotal = (meta.sizeMb / 2).coerceAtLeast(1)
            )
        }
        _snackbarMessage.value = "Video dipilih: ${meta.fileName} (${meta.sizeMb} MB)"
    }

    fun loadSampleFilePreset(name: String, sizeMb: Int, durationMin: Int, gaming: Boolean) {
        _uploadPipeline.update {
            it.copy(
                inputMode = "UPLOAD",
                fileName = name,
                fileSizeMb = sizeMb,
                durationMinutes = durationMin,
                gamingMode = gaming,
                isSplitCompleted = false,
                splitPartsLabel = "",
                chunkTotal = (sizeMb / 2).coerceAtLeast(1)
            )
        }
    }

    fun updateLinkInput(url: String) {
        val preview = parseLinkToPreview(url)
        _uploadPipeline.update {
            it.copy(
                inputMode = "LINK",
                linkInput = url,
                linkPreview = preview,
                fileName = preview?.title ?: it.fileName,
                fileSizeMb = preview?.estimatedMb ?: it.fileSizeMb,
                durationMinutes = preview?.durationMinutes ?: it.durationMinutes,
                gamingMode = preview?.platformName?.contains("Twitch") == true || it.gamingMode
            )
        }
    }

    private fun parseLinkToPreview(rawUrl: String): LinkPreviewInfo? {
        val url = rawUrl.trim()
        if (url.length < 6) return null
        return when {
            url.contains("youtube", true) || url.contains("youtu.be", true) -> LinkPreviewInfo(
                url = url,
                platformName = "YouTube",
                title = "Podcast AI & Creator Economy Indonesia (Full Episode)",
                durationLabel = "42:18",
                durationMinutes = 42,
                estimatedMb = 94,
                thumbnailKind = ThumbnailKind.PODCAST
            )
            url.contains("tiktok", true) -> LinkPreviewInfo(
                url = url,
                platformName = "TikTok",
                title = "Live Highlight Jualan & QnA Viral TikTok",
                durationLabel = "18:40",
                durationMinutes = 19,
                estimatedMb = 58,
                thumbnailKind = ThumbnailKind.JUALAN
            )
            url.contains("instagram", true) || url.contains("ig.me", true) -> LinkPreviewInfo(
                url = url,
                platformName = "Instagram Reels",
                title = "Sesi IG Live Strategi Konten Konsisten",
                durationLabel = "25:10",
                durationMinutes = 25,
                estimatedMb = 72,
                thumbnailKind = ThumbnailKind.DAKWAH
            )
            url.contains("facebook", true) || url.contains("fb.watch", true) -> LinkPreviewInfo(
                url = url,
                platformName = if (url.contains("live", true)) "Facebook Live" else "Facebook Video",
                title = "Siaran Langsung Diskusi Komunitas & Tips Bisnis",
                durationLabel = "36:05",
                durationMinutes = 36,
                estimatedMb = 88,
                thumbnailKind = ThumbnailKind.PODCAST
            )
            url.contains("twitch", true) -> LinkPreviewInfo(
                url = url,
                platformName = "Twitch VOD",
                title = "AllGamesClips Ranked Marathon - Final Clutch",
                durationLabel = "48:50",
                durationMinutes = 49,
                estimatedMb = 99,
                thumbnailKind = ThumbnailKind.GAMING
            )
            url.contains("drive.google", true) -> LinkPreviewInfo(
                url = url,
                platformName = "Google Drive",
                title = "Master_Video_Studio_Raw_Export.mp4",
                durationLabel = "45:00",
                durationMinutes = 45,
                estimatedMb = 100,
                thumbnailKind = ThumbnailKind.PODCAST
            )
            else -> LinkPreviewInfo(
                url = url,
                platformName = "Video Link",
                title = "Video Online Terdeteksi Siap Diproses",
                durationLabel = "30:00",
                durationMinutes = 30,
                estimatedMb = 82,
                thumbnailKind = ThumbnailKind.PODCAST
            )
        }
    }

    fun selectCompressionMode(mode: String) {
        _uploadPipeline.update { it.copy(compressionMode = mode) }
    }

    fun toggleUploadGamingMode(enabled: Boolean) {
        _uploadPipeline.update { it.copy(gamingMode = enabled) }
    }

    fun updateCustomPromptInput(prompt: String) {
        _uploadPipeline.update { it.copy(customPrompt = prompt) }
    }

    fun applyPromptFilterToCurrentClips(prompt: String) {
        _activePromptFilter.value = prompt.trim()
        _uploadPipeline.update { it.copy(customPrompt = prompt) }
        if (prompt.isNotBlank()) {
            _snackbarMessage.value = "AI memfilter momen sesuai prompt: \"$prompt\""
        }
    }

    fun splitOver100MbFile() {
        val currentSize = _uploadPipeline.value.fileSizeMb
        val partSize = (currentSize / 2).coerceAtMost(50)
        _uploadPipeline.update {
            it.copy(
                fileSizeMb = partSize,
                durationMinutes = (it.durationMinutes / 2).coerceAtLeast(10),
                isSplitCompleted = true,
                splitPartsLabel = "Dipecah Otomatis: Bagian 1 (${partSize}MB) & Bagian 2 (${partSize}MB)",
                chunkTotal = (partSize / 2).coerceAtLeast(1)
            )
        }
        _snackbarMessage.value = "Berhasil! File dipecah jadi 2 bagian @50MB agar aman di HP."
    }

    fun startAiClippingPipeline() {
        val pipeline = _uploadPipeline.value
        if (pipeline.isProcessing) return
        if (pipeline.fileSizeMb > 100) {
            _snackbarMessage.value = "File >100MB! Tekan 'Pecah Otomatis jadi 2 bagian @50MB' dulu."
            return
        }

        processingJob?.cancel()
        processingJob = viewModelScope.launch {
            prefsRepo.checkAndResetMonthlyQuotaIfNeeded()
            val currentRemaining = uiState.value.preferences.remainingCreditsMinutes
            if (currentRemaining <= 0) {
                _showQuotaExhaustedDialog.value = true
                return@launch
            }
            val totalMb = pipeline.fileSizeMb.coerceIn(10, 100)
            val totalChunks = (totalMb / 2).coerceAtLeast(5)
            val audioSize = (totalMb * 0.042f).coerceIn(2.4f, 5.0f)

            _uploadPipeline.update {
                it.copy(
                    isProcessing = true,
                    progress = 0.05f,
                    chunkIndex = 1,
                    chunkTotal = totalChunks,
                    audioOnlyMb = audioSize,
                    stageLabel = "Kompresi ${pipeline.compressionMode}..."
                )
            }
            delay(450L)

            // Phase 1: Chunked Upload 2MB/chunk
            for (chunk in 1..totalChunks step (totalChunks / 5).coerceAtLeast(1)) {
                val uploadedMb = (chunk * 2).coerceAtMost(totalMb)
                val pct = 0.10f + (chunk.toFloat() / totalChunks.toFloat()) * 0.30f
                _uploadPipeline.update {
                    it.copy(
                        chunkIndex = chunk,
                        progress = pct,
                        stageLabel = "Upload $uploadedMb/$totalMb MB (Chunk $chunk/$totalChunks @2MB)..."
                    )
                }
                delay(280L)
            }

            // Phase 2: Audio-Only Extraction
            _uploadPipeline.update {
                it.copy(
                    progress = 0.50f,
                    stageLabel = "Ekstrak Audio Saja (${String.format("%.1f", audioSize)} MB) 20% -> Video asli tetap di HP"
                )
            }
            delay(500L)

            // Phase 3: AI Analyzing
            _uploadPipeline.update {
                it.copy(
                    progress = 0.72f,
                    stageLabel = "AI Menganalisis Transkrip, Emosi & Hook 60%..."
                )
            }
            delay(650L)

            // Phase 4: Cutting 16 Clips
            _uploadPipeline.update {
                it.copy(
                    progress = 0.92f,
                    stageLabel = "Memotong 16 Clip & Auto 4 Versi Platform 90%..."
                )
            }
            delay(500L)

            val newProjectId = "proj_${System.currentTimeMillis()}"
            val titleClean = pipeline.fileName.substringBeforeLast(".").ifBlank { "Video Baru" }
            val folder = when {
                pipeline.gamingMode -> ProjectFolder.GAMING
                pipeline.linkPreview?.platformName?.contains("YouTube") == true -> ProjectFolder.YOUTUBE
                else -> ProjectFolder.TIKTOK
            }
            val thumb = when {
                pipeline.gamingMode -> ThumbnailKind.GAMING
                pipeline.linkPreview != null -> pipeline.linkPreview.thumbnailKind
                else -> ThumbnailKind.PODCAST
            }
            val newProject = VideoProject(
                id = newProjectId,
                title = titleClean,
                sourceType = if (pipeline.inputMode == "LINK") {
                    pipeline.linkPreview?.platformName ?: "Link Video"
                } else {
                    "Upload HP (Chunked 2MB)"
                },
                sourceUrl = pipeline.linkInput.ifBlank { "local://DCIM/${pipeline.fileName}" },
                folder = folder,
                fileSizeMb = totalMb,
                durationMinutes = pipeline.durationMinutes,
                resolutionLabel = if (pipeline.compressionMode.contains("480p")) "480p Hemat Kuota" else "720p HD",
                compressionMode = pipeline.compressionMode,
                audioExtractedMb = audioSize,
                createdAtLabel = "Baru saja • 16 Clip Viral",
                isSplitPart = pipeline.isSplitCompleted,
                partLabel = pipeline.splitPartsLabel,
                thumbnailKind = thumb,
                clipCount = 16
            )

            val generatedClips = ClipperPresets.generateClipsForNewVideo(
                projectId = newProjectId,
                sourceTitle = titleClean,
                promptHint = pipeline.customPrompt,
                gamingMode = pipeline.gamingMode,
                count = 16
            )

            repository.saveNewProjectWithClips(newProject, generatedClips)
            val newRemaining = prefsRepo.deductCredits(pipeline.durationMinutes)
            if (newRemaining <= 0) {
                _showQuotaExhaustedDialog.value = true
            }

            _uploadPipeline.update {
                it.copy(
                    isProcessing = false,
                    progress = 1f,
                    stageLabel = "Selesai! 16 Clip Viral Siap",
                    activeProjectIdForPager = newProjectId
                )
            }
            _selectedProjectIdInProjectsTab.value = newProjectId
            _snackbarMessage.value = "Sukses! 16 clip viral dibuat dari audio ${String.format("%.1f", audioSize)}MB (Hemat kuota!)."
        }
    }

    fun selectActiveProjectForCreatePager(projectId: String) {
        _uploadPipeline.update { it.copy(activeProjectIdForPager = projectId) }
    }

    // --- Clip & Editor Actions ---

    fun switchClipPlatform(clip: ViralClip, platform: TargetPlatform) {
        viewModelScope.launch {
            repository.updateClip(clip.copy(selectedPlatform = platform))
        }
    }

    fun updateClip(updated: ViralClip) {
        viewModelScope.launch {
            repository.updateClip(updated)
        }
    }

    fun applyTemplateToClip(clip: ViralClip, template: VideoTemplate) {
        viewModelScope.launch {
            repository.updateClip(
                clip.copy(
                    templateId = template.id,
                    captionStyleName = template.captionStyle,
                    captionColorHex = template.accentHex,
                    gamingModeEnabled = template.category == "Gaming" || clip.gamingModeEnabled
                )
            )
            _snackbarMessage.value = "Template '${template.name}' diterapkan!"
        }
    }

    fun addAiBRoll(clip: ViralClip) {
        viewModelScope.launch {
            repository.updateClip(clip.copy(bRollAdded = !clip.bRollAdded))
            _snackbarMessage.value = if (!clip.bRollAdded) {
                "AI B-Roll otomatis ditambahkan ke segmen talking-head!"
            } else {
                "AI B-Roll dinonaktifkan"
            }
        }
    }

    // --- Projects Tab Filters ---

    fun selectFolder(folder: ProjectFolder) {
        _selectedFolder.value = folder
    }

    fun selectStatusFilter(status: ClipStatus?) {
        _selectedStatusFilter.value = status
    }

    fun selectProjectInProjectsTab(projectId: String) {
        _selectedProjectIdInProjectsTab.value = projectId
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            repository.deleteProject(projectId)
            _snackbarMessage.value = "Project dihapus dari memori lokal HP"
        }
    }

    // --- Publish, Export 1080p <= 30MB, & 30-Day Auto Posting ---

    fun saveClipToGallery1080p(clip: ViralClip) {
        if (_exportState.value.isExporting) return
        exportJob?.cancel()
        exportJob = viewModelScope.launch {
            _exportState.value = ExportUiState(
                isExporting = true,
                exportProgress = 0.15f,
                exportStatusText = "Merender 1080p (${clip.selectedPlatform.ratioLabel})..."
            )
            delay(450L)
            _exportState.value = ExportUiState(
                isExporting = true,
                exportProgress = 0.55f,
                exportStatusText = "Kompresi Cerdas -> Target Max 30MB..."
            )
            delay(500L)
            _exportState.value = ExportUiState(
                isExporting = true,
                exportProgress = 0.90f,
                exportStatusText = "Menyimpan ke Galeri HP..."
            )
            delay(400L)
            val finalSizeMb = (14 + (clip.activeVersion.durationSec % 13)).coerceAtMost(28)
            _exportState.value = ExportUiState(
                isExporting = false,
                exportProgress = 1f,
                exportStatusText = "Tersimpan di Galeri!",
                completedExportSummary = "${clip.title.take(28)}... • 1080p • $finalSizeMb MB (Hemat Memori)"
            )
            _snackbarMessage.value = "Video 1080p ($finalSizeMb MB) berhasil disimpan ke Galeri HP!"
        }
    }

    fun publishClipOneClick(clip: ViralClip, platform: TargetPlatform) {
        viewModelScope.launch {
            repository.updateClip(
                clip.copy(
                    selectedPlatform = platform,
                    status = ClipStatus.TERPUBLISH,
                    scheduledDateLabel = "Hari ini",
                    scheduledTimeLabel = "Baru saja"
                )
            )
            _snackbarMessage.value = "Berhasil Publish 1-Klik ke ${platform.fullName}!"
        }
    }

    fun scheduleSingleClip(clip: ViralClip, platform: TargetPlatform, dateLabel: String, timeLabel: String) {
        viewModelScope.launch {
            repository.updateClip(
                clip.copy(
                    selectedPlatform = platform,
                    status = ClipStatus.DIJADWALKAN,
                    scheduledDateLabel = dateLabel,
                    scheduledTimeLabel = timeLabel
                )
            )
            _snackbarMessage.value = "Dijadwalkan ke ${platform.fullName} pada $dateLabel jam $timeLabel"
        }
    }

    fun generateAutoPosting30Days() {
        viewModelScope.launch {
            val state = uiState.value
            val created = repository.generate30DaySchedule(state.clips, state.projects)
            _snackbarMessage.value = "Auto Posting 30 Hari dibuat (${created.size} jadwal lintas TikTok, Shorts, Reels & FB)!"
        }
    }

    // --- Brand Kit & Preferences ---

    fun updateBrandKit(config: BrandKitConfig) {
        viewModelScope.launch {
            prefsRepo.updateBrandKit(config)
        }
    }

    fun applyBrandKitToAllClips(config: BrandKitConfig) {
        viewModelScope.launch {
            repository.applyBrandKitToAll(config)
            _snackbarMessage.value = "Brand Kit & Template diterapkan ke semua clip!"
        }
    }

    fun setDataSaver(enabled: Boolean) {
        viewModelScope.launch { prefsRepo.setDataSaver(enabled) }
    }

    fun setWatermarkEnabled(enabled: Boolean) {
        viewModelScope.launch {
            prefsRepo.updateBrandKit(uiState.value.brandKit.copy(watermarkEnabled = enabled))
        }
    }

    fun setGamingDefault(enabled: Boolean) {
        viewModelScope.launch { prefsRepo.setGamingModeDefault(enabled) }
    }

    fun setAutoCompressOver80Mb(enabled: Boolean) {
        viewModelScope.launch { prefsRepo.setAutoCompressOver80Mb(enabled) }
    }

    fun setSimulateLowRam(enabled: Boolean) {
        viewModelScope.launch { prefsRepo.setSimulateLowRam(enabled) }
    }

    fun resetDemoWorkspace() {
        viewModelScope.launch {
            repository.resetDemoData()
            _showQuotaExhaustedDialog.value = false
            _snackbarMessage.value = "Data demo 45 menit (15 clip) & kuota 1000 Menit/Bulan di-reset!"
        }
    }
}
