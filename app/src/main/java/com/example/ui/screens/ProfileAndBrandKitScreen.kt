package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.DownloadForOffline
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.SdStorage
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ClipperPresets
import com.example.data.model.UserPreferences
import com.example.ui.theme.BrightGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DeepPurple
import com.example.ui.theme.JetBrainsMonoFontFamily
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
fun ProfileScreen(
    state: ClipperUiState,
    viewModel: ClipperViewModel,
    onOpenBrandKit: () -> Unit,
    onOpenPwaInspector: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Kredit Bulanan & Memori HP Card
        item {
            val remainingMin = state.preferences.remainingCreditsMinutes
            val usedMin = state.preferences.usedCreditsMinutes
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(listOf(DeepPurple.copy(alpha = 0.85f), ObsidianCard))
                    )
                    .border(1.dp, BrightGold.copy(alpha = 0.65f), RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Clipper Adha Pro",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White
                        )
                        Text(
                            text = "Sisa Kamu: $remainingMin Menit / Bulan",
                            style = MaterialTheme.typography.bodySmall,
                            color = SoftGold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(BrightGold)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "PRIBADI",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Bolt, contentDescription = null, tint = BrightGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Terpakai $usedMin / ${UserPreferences.MONTHLY_QUOTA_MINUTES} Menit",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.SdStorage, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Memori Bebas: ${state.deviceHealth.freeStorageGb}",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyanAccent
                        )
                    }
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
                    text = "Kuota otomatis reset jadi 1000 Menit setiap tanggal 1",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }

        // 1b. Install Aplikasi (PWA) Card
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(ObsidianCard)
                    .border(
                        1.dp,
                        if (state.preferences.pwaInstalled) ViralGreen.copy(alpha = 0.7f) else BrightGold.copy(alpha = 0.7f),
                        RoundedCornerShape(18.dp)
                    )
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Install Aplikasi",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Text(
                            text = "Mode standalone tanpa address bar • Offline siap pakai",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    if (state.preferences.pwaInstalled) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(ViralGreen.copy(alpha = 0.18f))
                                .border(1.dp, ViralGreen, CircleShape)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .testTag("profile_pwa_installed_badge")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = "Sudah Terinstall",
                                tint = ViralGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Sudah Terinstall ✓",
                                style = MaterialTheme.typography.labelLarge,
                                color = ViralGreen
                            )
                        }
                    }
                }

                if (!state.preferences.pwaInstalled) {
                    Button(
                        onClick = { viewModel.installPwaShortcut() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrightGold,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("profile_install_pwa_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.DownloadForOffline,
                            contentDescription = "Install Clipper Adha ke HP",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "INSTALL CLIPPER ADHA KE HP",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold)
                        )
                    }
                }
            }
        }

        // 2. Brand Kit & 52+ Template Manager Entry
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(ObsidianCard)
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(18.dp))
                    .clickable(onClick = onOpenBrandKit)
                    .padding(16.dp)
                    .testTag("open_brand_kit_btn")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(state.brandKit.primaryColorHex))
                    ) {
                        Text(
                            text = state.brandKit.logoInitials,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Brand Kit & 52+ Template", style = MaterialTheme.typography.titleMedium, color = Color.White)
                        Text(
                            text = "${state.brandKit.brandName} • Podcast, Gaming, Dakwah, Jualan",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
                Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = "Buka Brand Kit", tint = BrightGold)
            }
        }

        // 3. Pengaturan Aplikasi & Hemat Kuota
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(ObsidianCard)
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(18.dp))
                    .padding(14.dp)
            ) {
                Text("Pengaturan HP & Performa", style = MaterialTheme.typography.titleMedium, color = BrightGold)

                ProfileToggleRow(
                    title = "Mode Hemat Data",
                    subtitle = "Tampilkan thumbnail saja, tidak auto-play semua video",
                    checked = state.preferences.dataSaverMode,
                    onCheckedChange = viewModel::setDataSaver
                )
                ProfileToggleRow(
                    title = "Watermark 'Clipper Adha'",
                    subtitle = if (state.brandKit.watermarkEnabled) "Watermark tampil di pojok video" else "NONAKTIF (Fitur Pro Aktif)",
                    checked = state.brandKit.watermarkEnabled,
                    onCheckedChange = viewModel::setWatermarkEnabled
                )
                ProfileToggleRow(
                    title = "Auto-Kompres Video >80MB",
                    subtitle = "Tawarkan 480p Hemat Kuota / 720p HD sebelum upload",
                    checked = state.preferences.autoCompressOver80Mb,
                    onCheckedChange = viewModel::setAutoCompressOver80Mb
                )
                ProfileToggleRow(
                    title = "Mode Gaming Default (AllGamesClips)",
                    subtitle = "Aktifkan deteksi Kill, Rage & Slow-Motion otomatis",
                    checked = state.preferences.gamingModeDefault,
                    onCheckedChange = viewModel::setGamingDefault
                )
                ProfileToggleRow(
                    title = "Simulasi Peringatan RAM HP Lemot",
                    subtitle = "Uji banner peringatan 'Tutup aplikasi lain dulu ya'",
                    checked = state.preferences.simulateLowRamWarning,
                    onCheckedChange = viewModel::setSimulateLowRam
                )
            }
        }

        // 4. PWA Offline & Manifest Info + Reset Demo
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onOpenPwaInspector)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Code, contentDescription = null, tint = ViralGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Mode Standalone & Offline Cache", style = MaterialTheme.typography.titleSmall, color = Color.White)
                            Text("Lihat konfigurasi PWA manifest.json & cache lokal", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        }
                    }
                    Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, tint = TextSecondary)
                }

                OutlinedButton(
                    onClick = { viewModel.resetDemoWorkspace() },
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonViolet),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("reset_demo_btn")
                ) {
                    Icon(Icons.Rounded.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reset Data Demo (45 Mnt • 15 Clip • 1000 Menit)", style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(14.dp)) }
    }
}

@Composable
fun BrandKitManagerScreen(
    state: ClipperUiState,
    viewModel: ClipperViewModel,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var brandName by remember(state.brandKit) { mutableStateOf(state.brandKit.brandName) }
    var initials by remember(state.brandKit) { mutableStateOf(state.brandKit.logoInitials) }
    var introText by remember(state.brandKit) { mutableStateOf(state.brandKit.introText) }
    var outroText by remember(state.brandKit) { mutableStateOf(state.brandKit.outroText) }
    var selectedCategory by remember { mutableStateOf("Podcast") }

    val templatesInCat = remember(selectedCategory) {
        ClipperPresets.templates.filter { it.category == selectedCategory }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("brand_kit_screen")
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
            Column {
                Text("Brand Kit & 52+ Template", style = MaterialTheme.typography.titleMedium, color = BrightGold)
                Text("Terapkan logo, warna, intro/outro ke semua clip", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
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
                    Text("Identitas Brand", style = MaterialTheme.typography.titleSmall, color = BrightGold)
                    OutlinedTextField(
                        value = brandName,
                        onValueChange = { brandName = it },
                        label = { Text("Nama Brand / Channel") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrightGold,
                            unfocusedBorderColor = ObsidianBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = initials,
                        onValueChange = { initials = it.take(4) },
                        label = { Text("Inisial Logo (maks 4 huruf)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrightGold,
                            unfocusedBorderColor = ObsidianBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = introText,
                        onValueChange = { introText = it },
                        label = { Text("Teks Intro Video") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalPurple,
                            unfocusedBorderColor = ObsidianBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = outroText,
                        onValueChange = { outroText = it },
                        label = { Text("Teks Outro CTA") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalPurple,
                            unfocusedBorderColor = ObsidianBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Warna Brand:", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                        listOf(0xFF8B5CF6L, 0xFFF59E0BL, 0xFF10B981L, 0xFFEF4444L, 0xFF06B6D4L).forEach { hex ->
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(Color(hex))
                                    .border(
                                        width = if (state.brandKit.primaryColorHex == hex) 2.5.dp else 1.dp,
                                        color = Color.White,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        viewModel.updateBrandKit(
                                            state.brandKit.copy(
                                                brandName = brandName,
                                                logoInitials = initials,
                                                introText = introText,
                                                outroText = outroText,
                                                primaryColorHex = hex
                                            )
                                        )
                                    }
                            )
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.applyBrandKitToAllClips(
                                state.brandKit.copy(
                                    brandName = brandName,
                                    logoInitials = initials,
                                    introText = introText,
                                    outroText = outroText
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrightGold, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("apply_brandkit_all_btn")
                    ) {
                        Icon(Icons.Rounded.Palette, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Terapkan Brand Kit ke Semua Clip", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

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
                    Text("Galeri 52+ Template Siap Pakai", style = MaterialTheme.typography.titleSmall, color = Color.White)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        listOf("Podcast", "Gaming", "Dakwah", "Jualan").forEach { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = RoyalPurple,
                                    selectedLabelColor = Color.White,
                                    containerColor = ObsidianElevated,
                                    labelColor = TextSecondary
                                )
                            )
                        }
                    }

                    templatesInCat.forEach { tpl ->
                        val active = state.brandKit.activeTemplateId == tpl.id
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (active) DeepPurple.copy(alpha = 0.7f) else ObsidianElevated)
                                .border(1.dp, if (active) BrightGold else ObsidianBorder, RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.updateBrandKit(
                                        state.brandKit.copy(activeTemplateId = tpl.id)
                                    )
                                    viewModel.showToast("Template '${tpl.name}' dipilih untuk Brand Kit!")
                                }
                                .padding(10.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(tpl.name, style = MaterialTheme.typography.titleSmall, color = Color.White)
                                Text("Style: ${tpl.captionStyle}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            }
                            Text(
                                text = if (active) "AKTIF" else tpl.badge,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(tpl.accentHex)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PwaManifestInspectorScreen(
    isInstalled: Boolean,
    onInstallClick: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("pwa_manifest_screen")
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
            Column {
                Text("Konfigurasi PWA & Offline HP", style = MaterialTheme.typography.titleMedium, color = BrightGold)
                Text("Standalone Fullscreen • Service Worker Cache Aktif", style = MaterialTheme.typography.labelSmall, color = ViralGreen)
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
                        .clip(RoundedCornerShape(16.dp))
                        .background(ObsidianCard)
                        .border(1.dp, ObsidianBorder, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Text("manifest.json", style = MaterialTheme.typography.titleSmall, color = BrightGold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = DeviceTelemetry.pwaManifestJsonPreview,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        ),
                        color = SoftGold
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(ObsidianCard)
                        .border(1.dp, ObsidianBorder, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Text("Service Worker (sw.js)", style = MaterialTheme.typography.titleSmall, color = ViralGreen)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = DeviceTelemetry.pwaServiceWorkerRoutes,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        ),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("HTML Head Tags (Semua Halaman)", style = MaterialTheme.typography.titleSmall, color = BrightGold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = DeviceTelemetry.pwaMetaTagsPreview,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        ),
                        color = SoftGold
                    )
                }
            }

            item {
                if (isInstalled) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ViralGreen.copy(alpha = 0.2f))
                            .border(1.dp, ViralGreen, RoundedCornerShape(12.dp))
                    ) {
                        Text(
                            text = "Sudah Terinstall ✓",
                            style = MaterialTheme.typography.labelLarge,
                            color = ViralGreen
                        )
                    }
                } else {
                    Button(
                        onClick = onInstallClick,
                        colors = ButtonDefaults.buttonColors(containerColor = BrightGold, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("INSTALL CLIPPER ADHA KE HP", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileToggleRow(
    title: String,
    subtitle: String,
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
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = Color.White)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
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
