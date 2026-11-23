package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ClipperPresets
import com.example.data.model.TargetPlatform
import com.example.data.model.ViralClip
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
import com.example.util.DeviceTelemetry
import com.example.viewmodel.ClipperUiState
import com.example.viewmodel.ClipperViewModel

@Composable
fun PublishSingleClipScreen(
    clip: ViralClip,
    state: ClipperUiState,
    viewModel: ClipperViewModel,
    onBack: () -> Unit,
    onOpenAuto30: () -> Unit
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current

    var selectedDate by remember { mutableStateOf("24 Nov 2026") }
    var selectedTime by remember { mutableStateOf("19.00 WIB") }
    val activeVer = clip.activeVersion

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("publish_screen")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(ObsidianSurface)
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Kembali", tint = Color.White)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Export, Bagikan & Jadwal", style = MaterialTheme.typography.titleMedium, color = BrightGold)
                Text(
                    text = clip.title,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Platform Selector (4+1)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(ObsidianCard)
                        .border(1.dp, ObsidianBorder, RoundedCornerShape(18.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = "Pilih Platform Target (Auto Caption & Rasio)",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        TargetPlatform.entries.forEach { plat ->
                            FilterChip(
                                selected = clip.selectedPlatform == plat,
                                onClick = { viewModel.switchClipPlatform(clip, plat) },
                                label = { Text("${plat.shortName} (${plat.ratioLabel})") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = RoyalPurple,
                                    selectedLabelColor = Color.White,
                                    containerColor = ObsidianElevated,
                                    labelColor = TextSecondary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(ObsidianElevated)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Caption Otomatis (${clip.selectedPlatform.fullName}):",
                            style = MaterialTheme.typography.labelSmall,
                            color = BrightGold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = activeVer.caption,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = activeVer.hashtags,
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonViolet
                        )
                    }
                }
            }

            // 2. Simpan ke Galeri (1080p Max 30MB) & Share Sheet HP
            item {
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
                        text = "Simpan & Bagikan Instan",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White
                    )

                    AnimatedVisibility(visible = state.exportState.isExporting || state.exportState.completedExportSummary != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(ObsidianElevated)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = state.exportState.exportStatusText,
                                style = MaterialTheme.typography.labelMedium,
                                color = BrightGold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { state.exportState.exportProgress },
                                color = ViralGreen,
                                trackColor = ObsidianBorder,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(7.dp)
                                    .clip(CircleShape)
                            )
                            state.exportState.completedExportSummary?.let { summary ->
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Rounded.CheckCircle,
                                        contentDescription = null,
                                        tint = ViralGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(summary, style = MaterialTheme.typography.labelSmall, color = ViralGreen)
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { viewModel.saveClipToGallery1080p(clip) },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("save_gallery_1080p_btn")
                    ) {
                        Icon(Icons.Rounded.Download, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Simpan ke Galeri (1080p • Max 30MB)", style = MaterialTheme.typography.labelLarge)
                    }

                    OutlinedButton(
                        onClick = {
                            DeviceTelemetry.openShareSheet(
                                context = context,
                                title = activeVer.seoTitle,
                                caption = activeVer.caption,
                                hashtags = activeVer.hashtags
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BrightGold),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BrightGold),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("share_sheet_btn")
                    ) {
                        Icon(Icons.Rounded.Share, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Bagikan Langsung (TikTok / YT / IG / FB / WA)", style = MaterialTheme.typography.labelLarge)
                    }

                    Button(
                        onClick = { viewModel.publishClipOneClick(clip, clip.selectedPlatform) },
                        colors = ButtonDefaults.buttonColors(containerColor = BrightGold, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("publish_1klik_btn")
                    ) {
                        Icon(Icons.Rounded.RocketLaunch, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Publish 1-Klik ke ${clip.selectedPlatform.fullName}", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            // 3. Jadwal Posting + AI Prime Time Suggestion
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(ObsidianCard)
                        .border(1.dp, ObsidianBorder, RoundedCornerShape(18.dp))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Schedule, contentDescription = null, tint = BrightGold, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Jadwal Posting & Saran Jam Ramai AI", style = MaterialTheme.typography.titleSmall, color = Color.White)
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        listOf("24 Nov 2026", "25 Nov 2026", "26 Nov 2026", "27 Nov 2026", "28 Nov 2026").forEach { d ->
                            FilterChip(
                                selected = selectedDate == d,
                                onClick = { selectedDate = d },
                                label = { Text(d) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DeepPurple,
                                    selectedLabelColor = Color.White,
                                    containerColor = ObsidianElevated,
                                    labelColor = TextSecondary
                                )
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        ClipperPresets.aiPrimeTimeSlots.forEach { (time, desc) ->
                            val selected = selectedTime == time
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selected) DeepPurple.copy(alpha = 0.7f) else ObsidianElevated)
                                    .border(
                                        1.dp,
                                        if (selected) BrightGold else ObsidianBorder,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedTime = time }
                                    .padding(horizontal = 12.dp, vertical = 9.dp)
                            ) {
                                Column {
                                    Text(time, style = MaterialTheme.typography.titleSmall, color = BrightGold)
                                    Text(desc, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                }
                                Text(
                                    text = if (selected) "DIPILIH" else "PILIH",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (selected) SoftGold else TextSecondary
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.scheduleSingleClip(clip, clip.selectedPlatform, selectedDate, selectedTime)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ViralGreen, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("schedule_single_btn")
                    ) {
                        Icon(Icons.Rounded.CalendarMonth, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Jadwalkan Clip Ini ($selectedDate • $selectedTime)", style = MaterialTheme.typography.labelLarge)
                    }

                    OutlinedButton(
                        onClick = onOpenAuto30,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonViolet),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = BrightGold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Buka Auto Posting 30 Hari Sekaligus", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

@Composable
fun AutoSchedule30Screen(
    state: ClipperUiState,
    viewModel: ClipperViewModel,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("auto_schedule_30_screen")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(ObsidianSurface)
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Kembali", tint = Color.White)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Auto Posting 30 Hari", style = MaterialTheme.typography.titleMedium, color = BrightGold)
                Text(
                    text = "1x upload, stok konten sebulan penuh lintas 4 platform",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.linearGradient(listOf(DeepPurple.copy(alpha = 0.8f), ObsidianCard))
                        )
                        .border(1.dp, BrightGold.copy(alpha = 0.7f), RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Generator Jadwal 30 Hari AI",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "AI menyusun jadwal otomatis dengan jam ramai (19.00, 12.15, 21.00, 07.30 WIB) dan caption berbeda untuk TikTok, Shorts, Reels, dan Facebook.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.generateAutoPosting30Days() },
                        colors = ButtonDefaults.buttonColors(containerColor = BrightGold, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("generate_30_day_btn")
                    ) {
                        Icon(Icons.Rounded.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (state.scheduledPosts.size >= 30) "PERBARUI JADWAL 30 HARI" else "BUAT JADWAL 30 HARI OTOMATIS",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            items(state.scheduledPosts, key = { it.id }) { post ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(ObsidianCard)
                        .border(1.dp, ObsidianBorder, RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(56.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DeepPurple.copy(alpha = 0.65f))
                            .padding(vertical = 8.dp)
                    ) {
                        Text("H-${post.dayNumber}", style = MaterialTheme.typography.labelSmall, color = BrightGold)
                        Text(post.dateLabel, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(post.platform.fullName, style = MaterialTheme.typography.labelSmall, color = CyanAccent)
                            Text("• ${post.timeLabel}", style = MaterialTheme.typography.labelSmall, color = SoftGold)
                        }
                        Text(
                            text = post.clipTitle,
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = post.captionPreview,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
