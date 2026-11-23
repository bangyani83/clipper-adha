package com.example

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.MainTab
import com.example.data.model.SubScreen
import com.example.ui.components.ClipperSplashScreen
import com.example.ui.components.ClipperTopHeaderBar
import com.example.ui.screens.AutoSchedule30Screen
import com.example.ui.screens.BrandKitManagerScreen
import com.example.ui.screens.CreateClipScreen
import com.example.ui.screens.EditorScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ProjectsScreen
import com.example.ui.screens.PublishSingleClipScreen
import com.example.ui.screens.PwaManifestInspectorScreen
import com.example.ui.theme.BrightGold
import com.example.ui.theme.ClipperAdhaTheme
import com.example.ui.theme.DeepPurple
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.ObsidianElevated
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.SoftGold
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.ClipperViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT)
        )
        setContent {
            ClipperAdhaTheme {
                ClipperAdhaRootApp()
            }
        }
    }
}

@Composable
fun ClipperAdhaRootApp(
    viewModel: ClipperViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.snackbarMessage) {
        val msg = state.snackbarMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    if (state.showQuotaExhaustedDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissQuotaExhaustedDialog() },
            containerColor = ObsidianElevated,
            titleContentColor = BrightGold,
            textContentColor = Color.White,
            title = {
                Text(
                    text = "Kuota Bulanan Habis",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    text = "Kuota Bulanan Habis (1000 Menit), Kuota Akan Reset Otomatis Tanggal 1 Bulan Depan",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissQuotaExhaustedDialog() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrightGold,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.testTag("quota_exhausted_ok_btn")
                ) {
                    Text("Mengerti")
                }
            }
        )
    }

    Crossfade(targetState = state.showSplash, label = "splash_transition") { showingSplash ->
        if (showingSplash) {
            ClipperSplashScreen()
        } else {
            val activeSubScreen = state.subScreen
            if (activeSubScreen != SubScreen.None) {
                Box(modifier = Modifier.fillMaxSize()) {
                    when (activeSubScreen) {
                        is SubScreen.Editor -> {
                            val clip = state.clips.firstOrNull { it.id == activeSubScreen.clipId }
                                ?: state.clips.firstOrNull()
                            if (clip != null) {
                                EditorScreen(
                                    clip = clip,
                                    brandKit = state.brandKit,
                                    dataSaverMode = state.preferences.dataSaverMode,
                                    viewModel = viewModel,
                                    onBack = { viewModel.closeSubScreen() },
                                    onOpenPublish = { viewModel.openSubScreen(SubScreen.Publish(clip.id)) }
                                )
                            } else {
                                viewModel.closeSubScreen()
                            }
                        }

                        is SubScreen.Publish -> {
                            val clip = state.clips.firstOrNull { it.id == activeSubScreen.clipId }
                                ?: state.clips.firstOrNull()
                            if (clip != null) {
                                PublishSingleClipScreen(
                                    clip = clip,
                                    state = state,
                                    viewModel = viewModel,
                                    onBack = { viewModel.closeSubScreen() },
                                    onOpenAuto30 = { viewModel.openSubScreen(SubScreen.AutoSchedule30) }
                                )
                            } else {
                                viewModel.closeSubScreen()
                            }
                        }

                        SubScreen.AutoSchedule30 -> {
                            AutoSchedule30Screen(
                                state = state,
                                viewModel = viewModel,
                                onBack = { viewModel.closeSubScreen() }
                            )
                        }

                        SubScreen.BrandKitManager -> {
                            BrandKitManagerScreen(
                                state = state,
                                viewModel = viewModel,
                                onBack = { viewModel.closeSubScreen() }
                            )
                        }

                        SubScreen.PwaManifestInspector -> {
                            PwaManifestInspectorScreen(
                                isInstalled = state.preferences.pwaInstalled,
                                onInstallClick = { viewModel.installPwaShortcut() },
                                onBack = { viewModel.closeSubScreen() }
                            )
                        }

                        SubScreen.None -> Unit
                    }

                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier
                            .align(androidx.compose.ui.Alignment.BottomCenter)
                            .padding(bottom = 24.dp, start = 16.dp, end = 16.dp)
                    ) { data ->
                        Snackbar(
                            snackbarData = data,
                            containerColor = ObsidianElevated,
                            contentColor = SoftGold
                        )
                    }
                }
            } else {
                Scaffold(
                    containerColor = ObsidianBg,
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    topBar = {
                        ClipperTopHeaderBar(
                            remainingCreditsMin = state.preferences.remainingCreditsMinutes,
                            freeStorageGb = state.deviceHealth.freeStorageGb,
                            dataSaverMode = state.preferences.dataSaverMode,
                            onCreditsClick = { viewModel.selectTab(MainTab.PROFIL) }
                        )
                    },
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState) { data ->
                            Snackbar(
                                snackbarData = data,
                                containerColor = ObsidianElevated,
                                contentColor = SoftGold
                            )
                        }
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = ObsidianSurface,
                            contentColor = Color.White,
                            windowInsets = WindowInsets.navigationBars
                        ) {
                            MainTab.entries.forEach { tab ->
                                val selected = state.activeTab == tab
                                val icon = when (tab) {
                                    MainTab.BERANDA -> if (selected) Icons.Filled.Home else Icons.Outlined.Home
                                    MainTab.BUAT_CLIP -> if (selected) Icons.Filled.AddCircle else Icons.Outlined.AddCircleOutline
                                    MainTab.PROJECT_SAYA -> if (selected) Icons.Filled.Folder else Icons.Outlined.Folder
                                    MainTab.PROFIL -> if (selected) Icons.Filled.Person else Icons.Outlined.Person
                                }
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { viewModel.selectTab(tab) },
                                    icon = {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = tab.label
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = tab.label,
                                            style = MaterialTheme.typography.labelMedium,
                                            maxLines = 1
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = BrightGold,
                                        selectedTextColor = BrightGold,
                                        indicatorColor = DeepPurple.copy(alpha = 0.75f),
                                        unselectedIconColor = TextSecondary,
                                        unselectedTextColor = TextSecondary
                                    ),
                                    modifier = Modifier.testTag("nav_${tab.name.lowercase()}")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (state.activeTab) {
                            MainTab.BERANDA -> HomeScreen(
                                state = state,
                                onInstallPwaClick = { viewModel.installPwaShortcut() },
                                onGoToCreateClip = { viewModel.selectTab(MainTab.BUAT_CLIP) },
                                onOpenAutoSchedule30 = { viewModel.openSubScreen(SubScreen.AutoSchedule30) },
                                onOpenClipEditor = { clipId -> viewModel.openSubScreen(SubScreen.Editor(clipId)) },
                                onGoToProjects = { viewModel.selectTab(MainTab.PROJECT_SAYA) }
                            )

                            MainTab.BUAT_CLIP -> CreateClipScreen(
                                state = state,
                                viewModel = viewModel,
                                onOpenEditor = { clipId -> viewModel.openSubScreen(SubScreen.Editor(clipId)) },
                                onOpenPublish = { clipId -> viewModel.openSubScreen(SubScreen.Publish(clipId)) }
                            )

                            MainTab.PROJECT_SAYA -> ProjectsScreen(
                                state = state,
                                viewModel = viewModel,
                                onOpenEditor = { clipId -> viewModel.openSubScreen(SubScreen.Editor(clipId)) },
                                onOpenPublish = { clipId -> viewModel.openSubScreen(SubScreen.Publish(clipId)) },
                                onOpenAutoSchedule30 = { viewModel.openSubScreen(SubScreen.AutoSchedule30) }
                            )

                            MainTab.PROFIL -> ProfileScreen(
                                state = state,
                                viewModel = viewModel,
                                onOpenBrandKit = { viewModel.openSubScreen(SubScreen.BrandKitManager) },
                                onOpenPwaInspector = { viewModel.openSubScreen(SubScreen.PwaManifestInspector) }
                            )
                        }
                    }
                }
            }
        }
    }
}
