package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CallSplit
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material.icons.rounded.Compress
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TargetPlatform
import com.example.data.model.ViralClip
import com.example.ui.components.VideoPreviewCanvas
import com.example.ui.components.thumbnailDrawableFor
import com.example.ui.theme.AlertRed
import com.example.ui.theme.BrightGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DeepPurple
import com.example.ui.theme.LuxuryGold
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianElevated
import com.example.ui.theme.RoyalPurple
import com.example.ui.theme.SoftGold
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.ViralGreen
import com.example.ui.theme.WarningAmber
import com.example.viewmodel.ClipperUiState
import com.example.viewmodel.ClipperViewModel
import kotlinx.coroutines.launch

@Composable
fun CreateClipScreen(
    state: ClipperUiState,
    viewModel: ClipperViewModel,
    onOpenEditor: (String) -> Unit,
    onOpenPublish: (String) -> Unit
) {
    val pipeline = state.uploadPipeline
    val scope = rememberCoroutineScope()

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            viewModel.onVideoPickedFromPhone(uri)
        }
    }

    val activeClipsForPager = remember(
        state.clips,
        pipeline.activeProjectIdForPager,
        state.activePromptFilter
    ) {
        val projectClips = state.clips.filter { it.projectId == pipeline.activeProjectIdForPager }
        val base = projectClips.ifEmpty { state.clips }
        val query = state.activePromptFilter.trim()
        if (query.isBlank()) {
            base
        } else {
            val filtered = base.filter {
                it.title.contains(query, true) ||
                    it.reason.contains(query, true) ||
                    it.displayTranscript.contains(query, true) ||
                    it.gamingMoment.label.contains(query, true)
            }
            filtered.ifEmpty { base }
        }
    }

    var playingClipId by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Low RAM Warning if applicable
        if (state.deviceHealth.isLowRam || state.preferences.simulateLowRamWarning) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(WarningAmber.copy(alpha = 0.16f))
                        .border(1.dp, WarningAmber, RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Memory,
                        contentDescription = "Peringatan RAM",
                        tint = BrightGold
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Peringatan RAM HP Terbatas (${state.deviceHealth.availableRamMb} MB bebas)",
                            style = MaterialTheme.typography.titleSmall,
                            color = BrightGold
                        )
                        Text(
                            text = "Tutup aplikasi lain dulu ya biar proses potong video tetap ngebut.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // 1. Upload / Import Selector Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(ObsidianCard)
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Buat Clip Viral Otomatis",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
                Text(
                    text = "Support MP4, MOV, WEBM hingga 100MB & Link Sosial Media",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Mode Switch Tabs
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ModeTogglePill(
                        label = "Upload dari HP",
                        selected = pipeline.inputMode == "UPLOAD",
                        onClick = { viewModel.setInputMode("UPLOAD") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("mode_upload_hp")
                    )
                    ModeTogglePill(
                        label = "Paste Link Video",
                        selected = pipeline.inputMode == "LINK",
                        onClick = { viewModel.setInputMode("LINK") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("mode_paste_link")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (pipeline.inputMode == "UPLOAD") {
                    // Dropzone / File Picker
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(ObsidianElevated)
                            .border(
                                1.5.dp,
                                Brush.linearGradient(listOf(RoyalPurple, LuxuryGold)),
                                RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                videoPickerLauncher.launch(arrayOf("video/mp4", "video/quicktime", "video/webm", "video/*"))
                            }
                            .padding(18.dp)
                            .testTag("pick_video_zone")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CloudUpload,
                            contentDescription = "Pilih Video HP",
                            tint = BrightGold,
                            modifier = Modifier.size(34.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap Pilih Video dari Galeri HP",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White
                        )
                        Text(
                            text = "Terpilih: ${pipeline.fileName} (${pipeline.fileSizeMb} MB • ${pipeline.durationMinutes} mnt)",
                            style = MaterialTheme.typography.labelSmall,
                            color = SoftGold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Simulasi File Cepat (Uji Teknis 100MB):",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        PresetChip("Podcast 96MB (45m)") {
                            viewModel.loadSampleFilePreset("podcast_tech_96mb.mp4", 96, 45, false)
                        }
                        PresetChip("Gaming 100MB") {
                            viewModel.loadSampleFilePreset("allgamesclips_ranked_100mb.mp4", 100, 38, true)
                        }
                        PresetChip("File >100MB (140MB)") {
                            viewModel.loadSampleFilePreset("raw_4k_stream_140mb.mp4", 140, 55, true)
                        }
                    }
                } else {
                    // Paste Link Section
                    OutlinedTextField(
                        value = pipeline.linkInput,
                        onValueChange = { viewModel.updateLinkInput(it) },
                        label = { Text("Paste Link YT, TikTok, IG, FB, Twitch, Drive") },
                        placeholder = { Text("https://youtube.com/watch?v=...") },
                        leadingIcon = {
                            Icon(Icons.Rounded.Link, contentDescription = null, tint = BrightGold)
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrightGold,
                            unfocusedBorderColor = ObsidianBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = BrightGold
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("paste_link_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        val quickLinks = listOf(
                            "YouTube" to "https://youtube.com/watch?v=techpodcast45",
                            "TikTok" to "https://tiktok.com/@adhapro/video/98123",
                            "IG Reels" to "https://instagram.com/reel/Cx99123",
                            "FB Live" to "https://facebook.com/live/usaha99",
                            "Twitch VOD" to "https://twitch.tv/videos/allgamesclips",
                            "Google Drive" to "https://drive.google.com/file/d/master45m"
                        )
                        quickLinks.forEach { (label, url) ->
                            PresetChip(label) { viewModel.updateLinkInput(url) }
                        }
                    }

                    // Auto Thumbnail + Title + Duration Preview
                    pipeline.linkPreview?.let { preview ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(ObsidianElevated)
                                .border(1.dp, NeonViolet.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                                .padding(10.dp)
                        ) {
                            Image(
                                painter = painterResource(id = thumbnailDrawableFor(preview.thumbnailKind)),
                                contentDescription = preview.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(width = 86.dp, height = 56.dp)
                                    .clip(RoundedCornerShape(10.dp))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${preview.platformName} • ${preview.durationLabel} • ${preview.estimatedMb} MB",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BrightGold
                                )
                                Text(
                                    text = preview.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = Color.White,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                // >100MB Auto-Split Card (Never Error!)
                AnimatedVisibility(visible = pipeline.fileSizeMb > 100) {
                    Column(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(AlertRed.copy(alpha = 0.14f))
                            .border(1.dp, AlertRed, RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.WarningAmber, contentDescription = null, tint = BrightGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Ukuran ${pipeline.fileSizeMb} MB (>100MB)",
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Agar upload & RAM HP tetap stabil, pecah file otomatis sebelum AI memproses.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.splitOver100MbFile() },
                            colors = ButtonDefaults.buttonColors(containerColor = BrightGold, contentColor = Color.Black),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("split_100mb_button")
                        ) {
                            Icon(Icons.Rounded.CallSplit, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Pecah Otomatis jadi 2 bagian @50MB",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }

                if (pipeline.isSplitCompleted) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "✓ ${pipeline.splitPartsLabel}",
                        style = MaterialTheme.typography.labelSmall,
                        color = ViralGreen
                    )
                }

                // >80MB Compression Selector
                AnimatedVisibility(visible = pipeline.fileSizeMb in 81..100) {
                    Column(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(ObsidianElevated)
                            .border(1.dp, LuxuryGold.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Compress, contentDescription = null, tint = BrightGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "File >80MB • Pilih Kompresi Otomatis:",
                                style = MaterialTheme.typography.titleSmall,
                                color = BrightGold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        val modes = listOf(
                            "Mode Cepat (480p) Hemat Kuota",
                            "Mode HD (720p)"
                        )
                        modes.forEach { mode ->
                            val selected = pipeline.compressionMode == mode
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selected) DeepPurple.copy(alpha = 0.7f) else ObsidianCard)
                                    .border(
                                        1.dp,
                                        if (selected) BrightGold else ObsidianBorder,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { viewModel.selectCompressionMode(mode) }
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = if (selected) "●  $mode" else "○  $mode",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (selected) SoftGold else Color.White
                                )
                            }
                        }
                    }
                }

                // Audio-Only AI Explanation Pill
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ViralGreen.copy(alpha = 0.12f))
                        .border(1.dp, ViralGreen.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.GraphicEq,
                        contentDescription = "Metode Audio Only",
                        tint = ViralGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Metode Audio-Only AI: Hanya ekstrak audio (~3.8 MB) dikirim ke AI. Video 100MB tetap di HP, upload chunk 2MB anti gagal.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                }

                // Gaming Mode & Prompt Manual
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ObsidianElevated)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = Icons.Rounded.SportsEsports,
                            contentDescription = "Mode Gaming",
                            tint = NeonViolet
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Mode Gaming (AllGamesClips)",
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.White
                            )
                            Text(
                                text = "Deteksi Kill, Mati, Menang, Kalah, Lucu, Rage",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }
                    Switch(
                        checked = pipeline.gamingMode,
                        onCheckedChange = { viewModel.toggleUploadGamingMode(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BrightGold,
                            checkedTrackColor = DeepPurple
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Prompt Manual AI
                OutlinedTextField(
                    value = pipeline.customPrompt,
                    onValueChange = { viewModel.updateCustomPromptInput(it) },
                    label = { Text("Prompt Manual AI (Opsional)") },
                    placeholder = { Text("Contoh: Cari momen saat bahas Rust") },
                    leadingIcon = {
                        Icon(Icons.Rounded.Search, contentDescription = null, tint = NeonViolet)
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonViolet,
                        unfocusedBorderColor = ObsidianBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_prompt_input")
                )

                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    listOf(
                        "Cari momen paling lucu",
                        "Cari momen saat bahas Rust",
                        "Cari momen kill"
                    ).forEach { promptOption ->
                        PresetChip(promptOption) {
                            viewModel.applyPromptFilterToCurrentClips(promptOption)
                        }
                    }
                    if (state.activePromptFilter.isNotBlank()) {
                        PresetChip("Reset Filter") {
                            viewModel.applyPromptFilterToCurrentClips("")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Progress Pipeline Display
                AnimatedVisibility(visible = pipeline.isProcessing || pipeline.progress > 0f) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(ObsidianElevated)
                            .padding(12.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = pipeline.stageLabel,
                                style = MaterialTheme.typography.labelMedium,
                                color = BrightGold,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "${(pipeline.progress * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { pipeline.progress },
                            color = BrightGold,
                            trackColor = ObsidianBorder,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape)
                        )
                    }
                }

                Button(
                    onClick = { viewModel.startAiClippingPipeline() },
                    enabled = !pipeline.isProcessing,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RoyalPurple,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("start_ai_clipping_btn")
                ) {
                    Icon(Icons.Rounded.AutoAwesome, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (pipeline.isProcessing) "AI SEDANG MEMPROSES..." else "PROSES VIDEO JADI 16 CLIP VIRAL",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }

        // 2. Single-Clip Pager (Tampilkan satu per satu agar HP tidak lag!)
        if (activeClipsForPager.isNotEmpty()) {
            item {
                val pagerState = rememberPagerState(
                    initialPage = 0,
                    pageCount = { activeClipsForPager.size }
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(ObsidianCard)
                        .border(1.dp, ObsidianBorder, RoundedCornerShape(20.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Hasil AI Clip (1-per-1 Anti Lag)",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            Text(
                                text = "Geser kiri/kanan • Clip ${pagerState.currentPage + 1} dari ${activeClipsForPager.size}",
                                style = MaterialTheme.typography.labelSmall,
                                color = BrightGold
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        val prev = (pagerState.currentPage - 1).coerceAtLeast(0)
                                        pagerState.animateScrollToPage(prev)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                                    contentDescription = "Clip Sebelumnya",
                                    tint = Color.White
                                )
                            }
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        val next = (pagerState.currentPage + 1).coerceAtMost(activeClipsForPager.lastIndex)
                                        pagerState.animateScrollToPage(next)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                                    contentDescription = "Clip Berikutnya",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxWidth()
                    ) { pageIndex ->
                        val clip = activeClipsForPager[pageIndex]
                        SingleClipWorkbenchCard(
                            clip = clip,
                            state = state,
                            isPlaying = playingClipId == clip.id,
                            onTogglePlay = {
                                playingClipId = if (playingClipId == clip.id) null else clip.id
                            },
                            onSelectPlatform = { platform ->
                                viewModel.switchClipPlatform(clip, platform)
                            },
                            onEditClick = { onOpenEditor(clip.id) },
                            onPublishClick = { onOpenPublish(clip.id) }
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(14.dp)) }
    }
}

@Composable
private fun SingleClipWorkbenchCard(
    clip: ViralClip,
    state: ClipperUiState,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onSelectPlatform: (TargetPlatform) -> Unit,
    onEditClick: () -> Unit,
    onPublishClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        VideoPreviewCanvas(
            clip = clip,
            brandKit = state.brandKit,
            isPlaying = isPlaying,
            dataSaverMode = state.preferences.dataSaverMode,
            onTogglePlay = onTogglePlay,
            compactMode = true
        )

        // Virality Score & Suitability
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(LuxuryGold.copy(alpha = 0.2f))
                    .border(1.dp, BrightGold, CircleShape)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Virality Score: ${clip.viralityScore}/100",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = BrightGold
                )
            }

            Text(
                text = clip.status.label,
                style = MaterialTheme.typography.labelSmall,
                color = ViralGreen
            )
        }

        Text(
            text = clip.title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )

        Text(
            text = "Alasan AI: ${clip.reason}",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Text(
            text = clip.suitabilityLabel,
            style = MaterialTheme.typography.labelSmall,
            color = CyanAccent
        )

        // Auto Versi Semua Platform (1 Klik Jadi 4+1)
        Text(
            text = "Auto Versi Platform (1 Klik Berubah Ukuran & Caption):",
            style = MaterialTheme.typography.labelMedium,
            color = SoftGold
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            TargetPlatform.entries.forEach { platform ->
                val selected = clip.selectedPlatform == platform
                FilterChip(
                    selected = selected,
                    onClick = { onSelectPlatform(platform) },
                    label = {
                        Text(
                            text = "${platform.shortName} (${platform.ratioLabel})",
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RoyalPurple,
                        selectedLabelColor = Color.White,
                        containerColor = ObsidianElevated,
                        labelColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("platform_chip_${platform.name.lowercase()}")
                )
            }
        }

        // Dynamic Platform Caption Preview
        val activeVer = clip.activeVersion
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(ObsidianElevated)
                .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp))
                .padding(10.dp)
        ) {
            Text(
                text = "Hook 3 Detik: ${activeVer.hookText}",
                style = MaterialTheme.typography.labelMedium,
                color = BrightGold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Judul SEO: ${activeVer.seoTitle}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = activeVer.caption,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = activeVer.hashtags,
                style = MaterialTheme.typography.labelSmall,
                color = NeonViolet
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = onEditClick,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonViolet),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("edit_clip_btn")
            ) {
                Icon(Icons.Rounded.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Edit HP", style = MaterialTheme.typography.labelLarge)
            }

            Button(
                onClick = onPublishClick,
                colors = ButtonDefaults.buttonColors(containerColor = BrightGold, contentColor = Color.Black),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("publish_clip_btn")
            ) {
                Icon(Icons.Rounded.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Publish", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun ModeTogglePill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) RoyalPurple else ObsidianElevated)
            .border(
                1.dp,
                if (selected) BrightGold.copy(alpha = 0.7f) else ObsidianBorder,
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) Color.White else TextSecondary
        )
    }
}

@Composable
private fun PresetChip(
    label: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(CircleShape)
            .background(ObsidianElevated)
            .border(1.dp, ObsidianBorder, CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.ContentPaste,
            contentDescription = null,
            tint = BrightGold,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White
        )
    }
}
