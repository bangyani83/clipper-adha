package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CenterFocusStrong
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SlowMotionVideo
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.BrandKitConfig
import com.example.data.model.CaptionPosition
import com.example.data.model.GamingMomentType
import com.example.data.model.ThumbnailKind
import com.example.data.model.ViralClip
import com.example.ui.theme.BrightGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.LuxuryGold
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ViralGreen
import kotlinx.coroutines.delay

fun thumbnailDrawableFor(kind: ThumbnailKind): Int = when (kind) {
    ThumbnailKind.PODCAST -> R.drawable.img_thumb_podcast_1795440341262
    ThumbnailKind.GAMING -> R.drawable.img_thumb_gaming_1795440342750
    ThumbnailKind.DAKWAH -> R.drawable.img_thumb_dakwah_1795440341682
    ThumbnailKind.JUALAN -> R.drawable.img_thumb_jualan_1795440342074
}

@Composable
fun VideoPreviewCanvas(
    clip: ViralClip,
    brandKit: BrandKitConfig,
    isPlaying: Boolean,
    dataSaverMode: Boolean,
    onTogglePlay: () -> Unit,
    modifier: Modifier = Modifier,
    compactMode: Boolean = false
) {
    val targetRatio by animateFloatAsState(
        targetValue = clip.selectedPlatform.aspectRatio,
        animationSpec = tween(320),
        label = "aspect_ratio_anim"
    )

    val words = remember(clip.displayTranscript) {
        clip.displayTranscript.split(" ").filter { it.isNotBlank() }
    }
    var activeWordIndex by remember(clip.id) { mutableIntStateOf(0) }

    LaunchedEffect(isPlaying, clip.id, clip.gamingSlowMo) {
        if (isPlaying && words.isNotEmpty()) {
            while (true) {
                delay(if (clip.gamingSlowMo) 520L else 280L)
                activeWordIndex = (activeWordIndex + 1) % words.size
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "auto_zoom")
    val zoomPulse by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isPlaying && clip.autoZoom) 1.09f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "zoom_scale"
    )

    val maxBoxWidth = when {
        compactMode && targetRatio < 0.7f -> 195.dp
        compactMode && targetRatio == 1f -> 235.dp
        compactMode -> 320.dp
        targetRatio < 0.7f -> 235.dp
        targetRatio == 1f -> 280.dp
        else -> 360.dp
    }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = maxBoxWidth)
                .fillMaxWidth()
                .aspectRatio(targetRatio)
                .clip(RoundedCornerShape(20.dp))
                .background(ObsidianBg)
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            Color(brandKit.primaryColorHex),
                            Color(brandKit.accentColorHex)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable(onClick = onTogglePlay)
                .testTag("video_preview_canvas")
        ) {
            // Video Thumbnail / Frame
            Image(
                painter = painterResource(id = thumbnailDrawableFor(clip.thumbnailKind)),
                contentDescription = clip.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .scale(if (isPlaying) zoomPulse else 1f)
            )

            // Scrim for legibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.55f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.75f)
                            )
                        )
                    )
            )

            // Top Overlays: Platform + Ratio + Auto-Reframe + Brand Logo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.72f))
                            .border(1.dp, NeonViolet.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${clip.selectedPlatform.shortName} • ${clip.selectedPlatform.ratioLabel}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = Color.White
                        )
                    }

                    if (clip.autoReframe) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ViralGreen.copy(alpha = 0.22f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CenterFocusStrong,
                                contentDescription = "Auto Reframe Wajah",
                                tint = ViralGreen,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Wajah Tengah",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = ViralGreen
                            )
                        }
                    }
                }

                // Brand Kit Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(brandKit.primaryColorHex).copy(alpha = 0.9f))
                        .border(1.dp, Color(brandKit.accentColorHex), RoundedCornerShape(8.dp))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = brandKit.logoInitials,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color.White
                    )
                }
            }

            // Gaming Mode Overlay (KILL +1 / MENANG! / Slow-Mo)
            if (clip.gamingModeEnabled && clip.gamingOverlayEnabled && clip.gamingMoment != GamingMomentType.NONE) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF991B1B), LuxuryGold)
                            )
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SportsEsports,
                        contentDescription = "Overlay Gaming",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = clip.gamingMoment.overlayText,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color.White
                    )
                    if (clip.gamingSlowMo) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Rounded.SlowMotionVideo,
                            contentDescription = "Slow Motion",
                            tint = Color.Black,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "0.5x",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.Black
                        )
                    }
                }
            }

            // B-Roll Picture-in-Picture indicator
            AnimatedVisibility(
                visible = clip.bRollAdded,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 10.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(ObsidianCard.copy(alpha = 0.9f))
                        .border(1.dp, CyanAccent, RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = "AI B-Roll Aktif",
                        tint = CyanAccent,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "AI B-ROLL",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                        color = CyanAccent
                    )
                }
            }

            // Center Play/Pause Button & Data Saver Notice
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.Center)
            ) {
                if (!isPlaying) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.65f))
                            .border(1.5.dp, BrightGold, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = "Putar Preview Clip",
                            tint = BrightGold,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    if (dataSaverMode) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.72f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Hemat Data • Thumbnail",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = ViralGreen
                            )
                        }
                    }
                } else {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.42f))
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Pause,
                            contentDescription = "Jeda Preview",
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Auto-Caption Word-by-Word Overlay
            val captionAlign = when (clip.captionPosition) {
                CaptionPosition.TOP -> Alignment.TopCenter
                CaptionPosition.CENTER -> Alignment.Center
                CaptionPosition.BOTTOM -> Alignment.BottomCenter
            }
            val captionTopPad = if (clip.captionPosition == CaptionPosition.TOP) 72.dp else 0.dp
            val captionBottomPad = if (clip.captionPosition == CaptionPosition.BOTTOM) 28.dp else 0.dp

            val windowStart = (activeWordIndex / 4) * 4
            val visibleWords = words.drop(windowStart).take(4)
            val accentColor = Color(clip.captionColorHex)

            Box(
                modifier = Modifier
                    .align(captionAlign)
                    .padding(
                        start = 12.dp,
                        end = 12.dp,
                        top = captionTopPad,
                        bottom = captionBottomPad
                    )
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black.copy(alpha = 0.68f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = buildAnnotatedString {
                        visibleWords.forEachIndexed { localIdx, word ->
                            val globalIdx = windowStart + localIdx
                            val isActive = globalIdx == activeWordIndex
                            val formatted = if (clip.captionStyleName.contains("Hormozi", true) ||
                                clip.captionStyleName.contains("MrBeast", true) ||
                                clip.captionStyleName.contains("Gaming", true)
                            ) {
                                word.uppercase()
                            } else {
                                word
                            }
                            withStyle(
                                style = SpanStyle(
                                    color = if (isActive) accentColor else Color.White,
                                    fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Bold,
                                    fontSize = if (isActive) 15.sp else 13.sp
                                )
                            ) {
                                append(formatted)
                            }
                            if (localIdx < visibleWords.lastIndex) append(" ")
                        }
                    },
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Bottom Watermark & Duration Pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (brandKit.watermarkEnabled) {
                    Text(
                        text = "Clipper Adha",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = Color.White.copy(alpha = 0.72f)
                    )
                } else {
                    Text(
                        text = clip.captionStyleName,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = accentColor.copy(alpha = 0.9f)
                    )
                }

                Text(
                    text = "${clip.activeVersion.durationSec} dtk",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = BrightGold
                )
            }
        }
    }
}
