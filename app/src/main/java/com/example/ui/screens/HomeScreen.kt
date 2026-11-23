package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.DownloadForOffline
import androidx.compose.material.icons.rounded.FolderSpecial
import androidx.compose.material.icons.rounded.MovieCreation
import androidx.compose.material.icons.rounded.ScheduleSend
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClipStatus
import com.example.data.model.UserPreferences
import com.example.data.model.ViralClip
import com.example.ui.components.thumbnailDrawableFor
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
import com.example.viewmodel.ClipperUiState

@Composable
fun HomeScreen(
    state: ClipperUiState,
    onInstallPwaClick: () -> Unit,
    onGoToCreateClip: () -> Unit,
    onOpenAutoSchedule30: () -> Unit,
    onOpenClipEditor: (String) -> Unit,
    onGoToProjects: () -> Unit
) {
    val totalProjects = state.projects.size
    val totalClips = state.clips.size
    val scheduledOrPublished = state.clips.count {
        it.status == ClipStatus.DIJADWALKAN || it.status == ClipStatus.TERPUBLISH
    }
    val avgVirality = if (state.clips.isEmpty()) 0 else {
        state.clips.sumOf { it.viralityScore } / state.clips.size
    }
    val topViralClips = state.clips.sortedByDescending { it.viralityScore }.take(4)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. PWA / Home Shortcut Install Banner
        if (!state.preferences.pwaInstalled) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(DeepPurple.copy(alpha = 0.9f), Color(0xFF2B1848))
                            )
                        )
                        .border(1.dp, BrightGold.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(LuxuryGold.copy(alpha = 0.2f))
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.DownloadForOffline,
                                    contentDescription = "Instal Aplikasi",
                                    tint = BrightGold
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Instal Clipper Adha ke HP Kamu",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = Color.White
                                )
                                Text(
                                    text = "Buka project offline & fullscreen tanpa lag",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SoftGold.copy(alpha = 0.85f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = onInstallPwaClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrightGold,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("install_pwa_button")
                        ) {
                            Text(
                                text = "INSTAL SEKARANG",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold)
                            )
                        }
                    }
                }
            }
        }

        // 2. Hero Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF22133C),
                                ObsidianCard,
                                Color(0xFF281D0A)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(listOf(NeonViolet.copy(alpha = 0.7f), LuxuryGold.copy(alpha = 0.7f))),
                        shape = RoundedCornerShape(22.dp)
                    )
                    .padding(18.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = BrightGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AI VIDEO CLIPPER ALL-IN-ONE",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = BrightGold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "1 Video Jadi 20 Konten Viral",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Support upload 100MB di HP (Chunked + Ekstrak Audio Saja). Sekali proses langsung jadi 4+ versi untuk TikTok, Shorts, Reels & FB.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = onGoToCreateClip,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RoyalPurple,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("hero_create_clip_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.MovieCreation,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Buat Clip",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        OutlinedButton(
                            onClick = onOpenAutoSchedule30,
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryGold),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BrightGold),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("hero_auto_30_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CalendarMonth,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Auto 30 Hari",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }

        // 2b. Kuota Bulanan Card (1000 Menit / Bulan)
        item {
            val usedMin = state.preferences.usedCreditsMinutes
            val remainingMin = state.preferences.remainingCreditsMinutes
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(ObsidianCard)
                    .border(1.dp, LuxuryGold.copy(alpha = 0.55f), RoundedCornerShape(18.dp))
                    .padding(14.dp)
                    .testTag("home_quota_card")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Sisa Kamu: $remainingMin Menit / Bulan",
                        style = MaterialTheme.typography.titleSmall,
                        color = BrightGold
                    )
                    Text(
                        text = "Terpakai $usedMin / ${UserPreferences.MONTHLY_QUOTA_MINUTES} Menit",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { (usedMin.toFloat() / UserPreferences.MONTHLY_QUOTA_MINUTES.toFloat()).coerceIn(0f, 1f) },
                    color = BrightGold,
                    trackColor = ObsidianBorder,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Reset otomatis menjadi 1000 Menit setiap tanggal 1 bulan depan",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }

        // 3. Dashboard Stats Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DashboardStatTile(
                        title = "Total Project",
                        value = "$totalProjects",
                        subtitle = "Disimpan di HP",
                        icon = Icons.Rounded.FolderSpecial,
                        accent = NeonViolet,
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onGoToProjects)
                    )
                    DashboardStatTile(
                        title = "Total Clip Siap",
                        value = "$totalClips",
                        subtitle = "4+1 Versi Platform",
                        icon = Icons.Rounded.MovieCreation,
                        accent = BrightGold,
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onGoToProjects)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DashboardStatTile(
                        title = "Jadwal / Publish",
                        value = "$scheduledOrPublished",
                        subtitle = "${state.scheduledPosts.size} slot kalender",
                        icon = Icons.Rounded.ScheduleSend,
                        accent = CyanAccent,
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onOpenAutoSchedule30)
                    )
                    DashboardStatTile(
                        title = "Rata-Rata Viral",
                        value = "$avgVirality/100",
                        subtitle = "Prediksi AI Engine",
                        icon = Icons.Rounded.TrendingUp,
                        accent = ViralGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 4. Analytics Sederhana - Clip Paling Diprediksi Viral
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(ObsidianCard)
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Whatshot,
                            contentDescription = null,
                            tint = BrightGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Analytics: Prediksi Paling Viral",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }
                    Text(
                        text = "Top 4",
                        style = MaterialTheme.typography.labelSmall,
                        color = SoftGold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                topViralClips.forEach { clip ->
                    ViralAnalyticsRow(
                        clip = clip,
                        onClick = { onOpenClipEditor(clip.id) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        // 5. Auto Posting 30 Hari Summary Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(ObsidianCard)
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(20.dp))
                    .clickable(onClick = onOpenAutoSchedule30)
                    .padding(16.dp)
                    .testTag("home_schedule_card")
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Auto Posting 30 Hari",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            Text(
                                text = if (state.scheduledPosts.isEmpty()) {
                                    "Sekali klik, AI isi jadwal 1 bulan penuh!"
                                } else {
                                    "${state.scheduledPosts.size} jadwal aktif lintas TikTok, Shorts, Reels & FB"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                            contentDescription = "Buka Jadwal 30 Hari",
                            tint = BrightGold
                        )
                    }

                    if (state.scheduledPosts.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        state.scheduledPosts.take(3).forEach { item ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(ObsidianElevated)
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.clipTitle,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${item.platform.fullName} • ${item.dateLabel} • ${item.timeLabel}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SoftGold
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(ViralGreen.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "Hari ${item.dayNumber}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ViralGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }
    }
}

@Composable
private fun DashboardStatTile(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(ObsidianCard)
            .border(1.dp, ObsidianBorder, RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = accent,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
            color = Color.White
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = accent
        )
    }
}

@Composable
private fun ViralAnalyticsRow(
    clip: ViralClip,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ObsidianElevated)
            .clickable(onClick = onClick)
            .padding(10.dp)
    ) {
        Image(
            painter = painterResource(id = thumbnailDrawableFor(clip.thumbnailKind)),
            contentDescription = clip.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(10.dp))
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = clip.title,
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(5.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.45f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = (clip.viralityScore / 100f).coerceIn(0.1f, 1f))
                        .height(7.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(listOf(RoyalPurple, BrightGold))
                        )
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "${clip.selectedPlatform.fullName} • ${clip.activeVersion.durationSec} dtk",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(LuxuryGold.copy(alpha = 0.18f))
                .border(1.dp, LuxuryGold.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Text(
                text = "${clip.viralityScore}",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = BrightGold
            )
        }
    }
}
