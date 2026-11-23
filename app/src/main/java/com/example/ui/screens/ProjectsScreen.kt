package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClipStatus
import com.example.data.model.ProjectFolder
import com.example.data.model.VideoProject
import com.example.data.model.ViralClip
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
import com.example.viewmodel.ClipperUiState
import com.example.viewmodel.ClipperViewModel

@Composable
fun ProjectsScreen(
    state: ClipperUiState,
    viewModel: ClipperViewModel,
    onOpenEditor: (String) -> Unit,
    onOpenPublish: (String) -> Unit,
    onOpenAutoSchedule30: () -> Unit
) {
    val filteredProjects = remember(state.projects, state.selectedFolder) {
        if (state.selectedFolder == ProjectFolder.SEMUA) {
            state.projects
        } else {
            state.projects.filter { it.folder == state.selectedFolder }
        }
    }

    val activeProjectId = remember(filteredProjects, state.selectedProjectIdInProjectsTab) {
        if (filteredProjects.any { it.id == state.selectedProjectIdInProjectsTab }) {
            state.selectedProjectIdInProjectsTab
        } else {
            filteredProjects.firstOrNull()?.id ?: ""
        }
    }

    val displayedClips = remember(state.clips, activeProjectId, state.selectedStatusFilter) {
        val projClips = if (activeProjectId.isNotBlank()) {
            state.clips.filter { it.projectId == activeProjectId }
        } else {
            state.clips
        }
        val status = state.selectedStatusFilter
        if (status == null) projClips else projClips.filter { it.status == status }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header + Local Storage Badge + 30 Day Button
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Project Saya",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Smartphone,
                            contentDescription = null,
                            tint = ViralGreen,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Tersimpan Offline di HP • Hemat Kuota",
                            style = MaterialTheme.typography.labelSmall,
                            color = ViralGreen
                        )
                    }
                }
                Button(
                    onClick = onOpenAutoSchedule30,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrightGold,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("projects_auto30_btn")
                ) {
                    Icon(Icons.Rounded.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Auto 30 Hari", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
            }
        }

        // Folder Tabs: Semua, YouTube, TikTok, Gaming
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                ProjectFolder.entries.forEach { folder ->
                    val selected = state.selectedFolder == folder
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.selectFolder(folder) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Folder,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                        },
                        label = { Text(folder.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RoyalPurple,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = BrightGold,
                            containerColor = ObsidianCard,
                            labelColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("folder_tab_${folder.name.lowercase()}")
                    )
                }
            }
        }

        // Project Cards List
        items(filteredProjects, key = { it.id }) { project ->
            val isSelected = project.id == activeProjectId
            ProjectFolderCard(
                project = project,
                isSelected = isSelected,
                onClick = { viewModel.selectProjectInProjectsTab(project.id) },
                onDelete = { viewModel.deleteProject(project.id) }
            )
        }

        // Status Filter Chips: Draft, Dijadwalkan, Terpublish
        item {
            Column {
                Text(
                    text = "Daftar Clip (${displayedClips.size}) • Mode Hemat Data (Thumbnail Saja)",
                    style = MaterialTheme.typography.titleSmall,
                    color = SoftGold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    FilterChip(
                        selected = state.selectedStatusFilter == null,
                        onClick = { viewModel.selectStatusFilter(null) },
                        label = { Text("Semua Status") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DeepPurple,
                            selectedLabelColor = Color.White,
                            containerColor = ObsidianCard,
                            labelColor = TextSecondary
                        )
                    )
                    ClipStatus.entries.forEach { st ->
                        FilterChip(
                            selected = state.selectedStatusFilter == st,
                            onClick = { viewModel.selectStatusFilter(st) },
                            label = { Text(st.label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DeepPurple,
                                selectedLabelColor = Color.White,
                                containerColor = ObsidianCard,
                                labelColor = TextSecondary
                            )
                        )
                    }
                }
            }
        }

        if (displayedClips.isEmpty()) {
            item {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(ObsidianCard)
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Belum ada clip dengan status ini.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        } else {
            items(displayedClips, key = { it.id }) { clip ->
                LightweightClipRowCard(
                    clip = clip,
                    onEdit = { onOpenEditor(clip.id) },
                    onPublish = { onOpenPublish(clip.id) }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(14.dp)) }
    }
}

@Composable
private fun ProjectFolderCard(
    project: VideoProject,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(if (isSelected) ObsidianElevated else ObsidianCard)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) BrightGold else ObsidianBorder,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick)
            .padding(12.dp)
            .testTag("project_card_${project.id}")
    ) {
        Image(
            painter = painterResource(id = thumbnailDrawableFor(project.thumbnailKind)),
            contentDescription = project.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(width = 76.dp, height = 56.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(RoyalPurple.copy(alpha = 0.28f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = project.folder.label,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = NeonViolet
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${project.fileSizeMb} MB • ${project.durationMinutes} mnt",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = SoftGold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = project.title,
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${project.sourceType} • Audio AI ${project.audioExtractedMb}MB • ${project.createdAtLabel}",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Rounded.DeleteOutline,
                contentDescription = "Hapus Project",
                tint = AlertRed.copy(alpha = 0.8f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun LightweightClipRowCard(
    clip: ViralClip,
    onEdit: () -> Unit,
    onPublish: () -> Unit
) {
    val statusColor = when (clip.status) {
        ClipStatus.DRAFT -> TextSecondary
        ClipStatus.DIJADWALKAN -> BrightGold
        ClipStatus.TERPUBLISH -> ViralGreen
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(ObsidianCard)
            .border(1.dp, ObsidianBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onEdit)
            .padding(10.dp)
            .testTag("clip_row_${clip.id}")
    ) {
        Box {
            Image(
                painter = painterResource(id = thumbnailDrawableFor(clip.thumbnailKind)),
                contentDescription = clip.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(width = 62.dp, height = 82.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = 0.78f))
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "${clip.viralityScore}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = BrightGold
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(statusColor.copy(alpha = 0.16f))
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = clip.status.label,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = statusColor
                    )
                }
                Text(
                    text = "${clip.selectedPlatform.shortName} ${clip.selectedPlatform.ratioLabel} • ${clip.activeVersion.durationSec}d",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = CyanAccent
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "#${clip.indexOrder} ${clip.title}",
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (!clip.scheduledDateLabel.isNullOrBlank()) {
                Text(
                    text = "Jadwal: ${clip.scheduledDateLabel} • ${clip.scheduledTimeLabel ?: ""}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = LuxuryGold
                )
            } else {
                Text(
                    text = clip.reason,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(ObsidianElevated)
                        .border(1.dp, RoyalPurple.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                        .clickable(onClick = onEdit)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Rounded.Edit, contentDescription = "Edit Clip", tint = Color.White, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", style = MaterialTheme.typography.labelSmall, color = Color.White)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(LuxuryGold.copy(alpha = 0.18f))
                        .border(1.dp, BrightGold, RoundedCornerShape(8.dp))
                        .clickable(onClick = onPublish)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Rounded.Share, contentDescription = "Publish Clip", tint = BrightGold, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Publish", style = MaterialTheme.typography.labelSmall, color = BrightGold)
                }
            }
        }
    }
}
