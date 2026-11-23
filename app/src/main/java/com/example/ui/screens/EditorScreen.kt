package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CenterFocusStrong
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.SlowMotionVideo
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.Subtitles
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ClipperPresets
import com.example.data.model.BrandKitConfig
import com.example.data.model.CaptionPosition
import com.example.data.model.GamingMomentType
import com.example.data.model.TargetPlatform
import com.example.data.model.ViralClip
import com.example.ui.components.VideoPreviewCanvas
import com.example.ui.theme.BrightGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DeepPurple
import com.example.ui.theme.LuxuryGold
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianElevated
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.RoyalPurple
import com.example.ui.theme.SoftGold
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.ViralGreen
import com.example.viewmodel.ClipperViewModel

@Composable
fun EditorScreen(
    clip: ViralClip,
    brandKit: BrandKitConfig,
    dataSaverMode: Boolean,
    viewModel: ClipperViewModel,
    onBack: () -> Unit,
    onOpenPublish: () -> Unit
) {
    BackHandler(onBack = onBack)

    var isPlaying by remember { mutableStateOf(true) }
    var selectedEditorTab by remember { mutableIntStateOf(0) }
    var selectedTemplateCategory by remember { mutableStateOf("Podcast") }

    val totalWindow = (clip.endSec - clip.startSec).coerceIn(15, 90)
    var trimStartFraction by remember(clip.id) { mutableStateOf(0f) }
    var trimEndFraction by remember(clip.id) { mutableStateOf(1f) }
    val trimmedSeconds = ((trimEndFraction - trimStartFraction) * totalWindow).toInt().coerceAtLeast(8)

    val editorTabs = listOf(
        "Caption (32+)" to Icons.Rounded.Subtitles,
        "AI Smart" to Icons.Rounded.AutoAwesome,
        "Gaming" to Icons.Rounded.SportsEsports,
        "Audio & SFX" to Icons.Rounded.MusicNote,
        "Template (52+)" to Icons.Rounded.Palette
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("editor_screen")
    ) {
        // Top Editor Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .background(ObsidianSurface)
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("editor_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Kembali",
                    tint = Color.White
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Studio Editor HP",
                    style = MaterialTheme.typography.titleSmall,
                    color = BrightGold
                )
                Text(
                    text = clip.title,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Button(
                onClick = onOpenPublish,
                colors = ButtonDefaults.buttonColors(containerColor = BrightGold, contentColor = Color.Black),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("editor_publish_btn")
            ) {
                Icon(Icons.Rounded.Share, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Export", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold))
            }
        }

        // Scrollable Mobile-First Editor Body (Preview Top + 1-Track Timeline + Controls)
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Platform Ratio Quick Switcher
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    TargetPlatform.entries.forEach { platform ->
                        val selected = clip.selectedPlatform == platform
                        FilterChip(
                            selected = selected,
                            onClick = { viewModel.switchClipPlatform(clip, platform) },
                            label = { Text("${platform.shortName} ${platform.ratioLabel}") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RoyalPurple,
                                selectedLabelColor = Color.White,
                                containerColor = ObsidianCard,
                                labelColor = TextSecondary
                            )
                        )
                    }
                }
            }

            // 2. Vertical Preview Video Canvas
            item {
                VideoPreviewCanvas(
                    clip = clip,
                    brandKit = brandKit,
                    isPlaying = isPlaying,
                    dataSaverMode = dataSaverMode,
                    onTogglePlay = { isPlaying = !isPlaying },
                    compactMode = true
                )
            }

            // 3. Timeline Simpel 1 Track (Anti-Lag, Drag Potong Awal/Akhir)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(ObsidianCard)
                        .border(1.dp, ObsidianBorder, RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Timeline Simpel 1 Track",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White
                        )
                        Text(
                            text = "Durasi: $trimmedSeconds dtk" + if (clip.removeSilence) " (Jeda dihapus)" else "",
                            style = MaterialTheme.typography.labelSmall,
                            color = BrightGold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Waveform Canvas
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(ObsidianElevated)
                    ) {
                        val bars = 46
                        val barWidth = size.width / bars
                        for (i in 0 until bars) {
                            val norm = i.toFloat() / bars.toFloat()
                            val inTrim = norm in trimStartFraction..trimEndFraction
                            val isSilenceGap = i % 11 == 5
                            val amp = if (isSilenceGap) 0.14f else (0.3f + ((i * 7) % 10) * 0.065f)
                            val barH = size.height * amp
                            val color = when {
                                !inTrim -> Color.Gray.copy(alpha = 0.25f)
                                isSilenceGap && clip.removeSilence -> Color(0xFFEF4444).copy(alpha = 0.45f)
                                i % 3 == 0 -> BrightGold
                                else -> NeonViolet
                            }
                            drawLine(
                                color = color,
                                start = Offset(i * barWidth + barWidth / 2f, (size.height - barH) / 2f),
                                end = Offset(i * barWidth + barWidth / 2f, (size.height + barH) / 2f),
                                strokeWidth = (barWidth * 0.55f).coerceAtLeast(2f)
                            )
                        }
                    }

                    RangeSlider(
                        value = trimStartFraction..trimEndFraction,
                        onValueChange = { range ->
                            if (range.endInclusive - range.start >= 0.15f) {
                                trimStartFraction = range.start
                                trimEndFraction = range.endInclusive
                            }
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = BrightGold,
                            activeTrackColor = RoyalPurple,
                            inactiveTrackColor = ObsidianBorder
                        )
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Geser titik kiri/kanan untuk potong awal & akhir",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                        if (clip.fillerRemover) {
                            Text(
                                text = "Filler bersih ✓",
                                style = MaterialTheme.typography.labelSmall,
                                color = ViralGreen
                            )
                        }
                    }
                }
            }

            // 4. Tool Tabs Selector
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    editorTabs.forEachIndexed { idx, (label, icon) ->
                        val selected = selectedEditorTab == idx
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selected) RoyalPurple else ObsidianCard)
                                .border(
                                    1.dp,
                                    if (selected) BrightGold else ObsidianBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedEditorTab = idx }
                                .padding(horizontal = 12.dp, vertical = 9.dp)
                                .testTag("editor_tab_$idx")
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (selected) BrightGold else TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // 5. Active Tool Panel
            item {
                when (selectedEditorTab) {
                    0 -> CaptionToolPanel(clip = clip, onUpdate = viewModel::updateClip)
                    1 -> AiSmartToolPanel(
                        clip = clip,
                        onUpdate = viewModel::updateClip,
                        onAddBRoll = { viewModel.addAiBRoll(clip) }
                    )
                    2 -> GamingToolPanel(clip = clip, onUpdate = viewModel::updateClip)
                    3 -> AudioSfxToolPanel(clip = clip, onUpdate = viewModel::updateClip)
                    else -> TemplatePickerPanel(
                        clip = clip,
                        selectedCategory = selectedTemplateCategory,
                        onSelectCategory = { selectedTemplateCategory = it },
                        onApplyTemplate = { tpl -> viewModel.applyTemplateToClip(clip, tpl) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CaptionToolPanel(
    clip: ViralClip,
    onUpdate: (ViralClip) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(ObsidianCard)
            .border(1.dp, ObsidianBorder, RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        Text(
            text = "Auto-Caption Kata-per-Kata (32 Gaya Viral)",
            style = MaterialTheme.typography.titleSmall,
            color = BrightGold
        )

        // Position Selector
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CaptionPosition.entries.forEach { pos ->
                FilterChip(
                    selected = clip.captionPosition == pos,
                    onClick = { onUpdate(clip.copy(captionPosition = pos)) },
                    label = { Text("Posisi ${pos.label}") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DeepPurple,
                        selectedLabelColor = Color.White,
                        containerColor = ObsidianElevated,
                        labelColor = TextSecondary
                    )
                )
            }
        }

        // Color Picker Dots
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Warna:", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            val palette = listOf(0xFFFBBF24L, 0xFFA855F7L, 0xFF22D3EEL, 0xFF10B981L, 0xFFFF2D55L, 0xFFFFFFFFL)
            palette.forEach { hex ->
                val isSelected = clip.captionColorHex == hex
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(hex))
                        .border(
                            width = if (isSelected) 2.5.dp else 1.dp,
                            color = if (isSelected) Color.White else ObsidianBorder,
                            shape = CircleShape
                        )
                        .clickable { onUpdate(clip.copy(captionColorHex = hex)) }
                )
            }
        }

        // 32 Caption Styles Grid/Wrap
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            ClipperPresets.captionStyles.forEach { preset ->
                val selected = clip.captionStyleName == preset.name
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selected) DeepPurple else ObsidianElevated)
                        .border(
                            1.dp,
                            if (selected) Color(preset.accentHex) else ObsidianBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            onUpdate(
                                clip.copy(
                                    captionStyleName = preset.name,
                                    captionColorHex = preset.accentHex
                                )
                            )
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = if (preset.isUppercase) preset.name.uppercase() else preset.name,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(preset.accentHex)
                    )
                    Text(
                        text = preset.category,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = TextSecondary
                    )
                }
            }
        }

        // Transcript preview showing Filler Remover result
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(ObsidianElevated)
                .padding(10.dp)
        ) {
            Text(
                text = if (clip.fillerRemover) "Transkrip Bersih (Kata 'eh, anu, kayak, gitu' dihapus):" else "Transkrip Asli:",
                style = MaterialTheme.typography.labelSmall,
                color = SoftGold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = clip.displayTranscript,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White
            )
        }
    }
}

@Composable
private fun AiSmartToolPanel(
    clip: ViralClip,
    onUpdate: (ViralClip) -> Unit,
    onAddBRoll: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(ObsidianCard)
            .border(1.dp, ObsidianBorder, RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        Text(
            text = "AI Smart Editing (1 Tombol)",
            style = MaterialTheme.typography.titleSmall,
            color = BrightGold
        )

        EditorSwitchRow(
            title = "Auto-Reframe Wajah",
            subtitle = "Wajah selalu di tengah di 9:16, 1:1, 16:9",
            icon = Icons.Rounded.CenterFocusStrong,
            checked = clip.autoReframe,
            onCheckedChange = { onUpdate(clip.copy(autoReframe = it)) }
        )
        EditorSwitchRow(
            title = "Auto-Zoom Momen Penting",
            subtitle = "Zoom in otomatis saat teriak / ketawa / penekanan",
            icon = Icons.Rounded.ZoomIn,
            checked = clip.autoZoom,
            onCheckedChange = { onUpdate(clip.copy(autoZoom = it)) }
        )
        EditorSwitchRow(
            title = "Remove Silence",
            subtitle = "Hapus jeda diam otomatis agar retensi naik",
            icon = Icons.Rounded.GraphicEq,
            checked = clip.removeSilence,
            onCheckedChange = { onUpdate(clip.copy(removeSilence = it)) }
        )
        EditorSwitchRow(
            title = "Filler Remover",
            subtitle = "Hapus kata 'eh, anu, kayak, gitu' otomatis",
            icon = Icons.Rounded.Subtitles,
            checked = clip.fillerRemover,
            onCheckedChange = { onUpdate(clip.copy(fillerRemover = it)) }
        )

        Button(
            onClick = onAddBRoll,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (clip.bRollAdded) CyanAccent else RoyalPurple,
                contentColor = if (clip.bRollAdded) Color.Black else Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("add_broll_button")
        ) {
            Icon(Icons.Rounded.AutoAwesome, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (clip.bRollAdded) "AI B-Roll Aktif (Tap Nonaktifkan)" else "Tambah B-Roll Otomatis",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun GamingToolPanel(
    clip: ViralClip,
    onUpdate: (ViralClip) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(ObsidianCard)
            .border(1.dp, ObsidianBorder, RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        Text(
            text = "Khusus Gaming (AllGamesClips)",
            style = MaterialTheme.typography.titleSmall,
            color = BrightGold
        )

        EditorSwitchRow(
            title = "Mode Gaming",
            subtitle = "Aktifkan deteksi momen esports & HUD",
            icon = Icons.Rounded.SportsEsports,
            checked = clip.gamingModeEnabled,
            onCheckedChange = { onUpdate(clip.copy(gamingModeEnabled = it)) }
        )

        Text(
            text = "Tag Momen Deteksi AI:",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            GamingMomentType.entries.forEach { moment ->
                FilterChip(
                    selected = clip.gamingMoment == moment,
                    onClick = {
                        onUpdate(
                            clip.copy(
                                gamingMoment = moment,
                                gamingModeEnabled = moment != GamingMomentType.NONE
                            )
                        )
                    },
                    label = { Text(moment.label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LuxuryGold,
                        selectedLabelColor = Color.Black,
                        containerColor = ObsidianElevated,
                        labelColor = Color.White
                    )
                )
            }
        }

        EditorSwitchRow(
            title = "Efek Slow-Motion Otomatis (0.5x)",
            subtitle = "Slow-motion dramatis saat momen Kill / Menang",
            icon = Icons.Rounded.SlowMotionVideo,
            checked = clip.gamingSlowMo,
            onCheckedChange = { onUpdate(clip.copy(gamingSlowMo = it)) }
        )

        EditorSwitchRow(
            title = "Overlay Otomatis ('KILL +1', 'MENANG!')",
            subtitle = "Tampilkan badge gaming otomatis di layar",
            icon = Icons.Rounded.SportsEsports,
            checked = clip.gamingOverlayEnabled,
            onCheckedChange = { onUpdate(clip.copy(gamingOverlayEnabled = it)) }
        )
    }
}

@Composable
private fun AudioSfxToolPanel(
    clip: ViralClip,
    onUpdate: (ViralClip) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(ObsidianCard)
            .border(1.dp, ObsidianBorder, RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        Text(
            text = "Musik Background & Sound Effect",
            style = MaterialTheme.typography.titleSmall,
            color = BrightGold
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            ClipperPresets.bgMusicOptions.forEach { track ->
                FilterChip(
                    selected = clip.bgMusicName == track,
                    onClick = { onUpdate(clip.copy(bgMusicName = track)) },
                    label = { Text(track) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RoyalPurple,
                        selectedLabelColor = Color.White,
                        containerColor = ObsidianElevated,
                        labelColor = TextSecondary
                    )
                )
            }
        }

        Text(
            text = "Volume Musik: ${(clip.bgMusicVolume * 100).toInt()}%",
            style = MaterialTheme.typography.labelMedium,
            color = Color.White
        )
        Slider(
            value = clip.bgMusicVolume,
            onValueChange = { onUpdate(clip.copy(bgMusicVolume = it)) },
            colors = SliderDefaults.colors(
                thumbColor = BrightGold,
                activeTrackColor = RoyalPurple
            )
        )

        EditorSwitchRow(
            title = "SFX 'Whoosh' Transisi",
            subtitle = "Bunyi whoosh halus tiap perpindahan scene/teks",
            icon = Icons.Rounded.GraphicEq,
            checked = clip.sfxWhoosh,
            onCheckedChange = { onUpdate(clip.copy(sfxWhoosh = it)) }
        )
        EditorSwitchRow(
            title = "SFX 'Boom' Momen Punchline",
            subtitle = "Efek bass punch saat hook atau kill",
            icon = Icons.Rounded.GraphicEq,
            checked = clip.sfxBoom,
            onCheckedChange = { onUpdate(clip.copy(sfxBoom = it)) }
        )
    }
}

@Composable
private fun TemplatePickerPanel(
    clip: ViralClip,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    onApplyTemplate: (com.example.data.model.VideoTemplate) -> Unit
) {
    val categories = listOf("Podcast", "Gaming", "Dakwah", "Jualan")
    val templatesInCat = remember(selectedCategory) {
        ClipperPresets.templates.filter { it.category == selectedCategory }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(ObsidianCard)
            .border(1.dp, ObsidianBorder, RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        Text(
            text = "52+ Template Siap Pakai",
            style = MaterialTheme.typography.titleSmall,
            color = BrightGold
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            categories.forEach { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { onSelectCategory(cat) },
                    label = { Text(cat) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LuxuryGold,
                        selectedLabelColor = Color.Black,
                        containerColor = ObsidianElevated,
                        labelColor = Color.White
                    )
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            templatesInCat.forEach { tpl ->
                val isSelected = clip.templateId == tpl.id
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) DeepPurple.copy(alpha = 0.7f) else ObsidianElevated)
                        .border(
                            1.dp,
                            if (isSelected) BrightGold else ObsidianBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onApplyTemplate(tpl) }
                        .padding(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = tpl.name,
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White
                        )
                        Text(
                            text = "Caption: ${tpl.captionStyle}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(tpl.accentHex).copy(alpha = 0.22f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (isSelected) "DIPAKAI" else tpl.badge,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(tpl.accentHex)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EditorSwitchRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ObsidianElevated)
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(icon, contentDescription = null, tint = BrightGold, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleSmall, color = Color.White)
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = BrightGold,
                checkedTrackColor = DeepPurple
            )
        )
    }
}
