package com.example.Russify.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Russify.model.PlayingContext
import com.example.Russify.presentation.state.MusicPlayerState
import com.example.Russify.presentation.state.RepeatMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullPlayerScreen(
    playerState: MusicPlayerState
) {
    val track = playerState.currentTrack ?: return
    var sliderPosition by remember { mutableFloatStateOf(0.3f) }
    var navigationDirection by remember { mutableIntStateOf(0) }

    BackHandler(enabled = playerState.isPlayerExpanded) {
        playerState.isPlayerExpanded = false
    }

    val themeBgColor = if (playerState.useOceanTheme) Color(0xFF004D40) else Color(0xFFD98880)
    val themeTextColor = if (playerState.useOceanTheme) Color.White else Color(0xFF212121)
    val themeIconColor = if (playerState.useOceanTheme) Color.White else Color.Black

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(themeBgColor)
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            IconButton(onClick = { playerState.isPlayerExpanded = false }) {
                Icon(
                    Icons.AutoMirrored.Filled.List,
                    "Collapse",
                    tint = themeIconColor,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        AnimatedContent(
            targetState = track.id,
            transitionSpec = {
                val slideDirection = if (navigationDirection > 0) 1 else -1
                slideInHorizontally(initialOffsetX = { it * slideDirection }) + fadeIn() togetherWith
                        slideOutHorizontally(targetOffsetX = { -it * slideDirection }) + fadeOut()
            },
            label = "CoverAnim"
        ) { trackId ->
            val currentTrack = playerState.allTracks.find { it.id == trackId } ?: track
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(currentTrack.coverColor)
                    .border(2.dp, themeIconColor.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AnimatedContent(
                targetState = track.id,
                transitionSpec = {
                    val slideDirection = if (navigationDirection > 0) 1 else -1
                    slideInHorizontally(initialOffsetX = { it * slideDirection }) + fadeIn() togetherWith
                            slideOutHorizontally(targetOffsetX = { -it * slideDirection }) + fadeOut()
                },
                label = "TitleAnim"
            ) { trackId ->
                val currentTrack = playerState.allTracks.find { it.id == trackId } ?: track
                Text(
                    text = currentTrack.title,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = themeTextColor,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedContent(
                targetState = track.id,
                transitionSpec = {
                    val slideDirection = if (navigationDirection > 0) 1 else -1
                    slideInHorizontally(initialOffsetX = { it * slideDirection }) + fadeIn() togetherWith
                            slideOutHorizontally(targetOffsetX = { -it * slideDirection }) + fadeOut()
                },
                label = "ArtistAnim"
            ) { trackId ->
                val currentTrack = playerState.allTracks.find { it.id == trackId } ?: track
                Text(
                    text = currentTrack.artist,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    color = themeTextColor.copy(alpha = 0.8f),
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val contextText = when(val ctx = playerState.playingContext) {
                is PlayingContext.AllTracks -> if (playerState.currentLanguage == com.example.Russify.presentation.state.AppLanguage.RU) "Все треки" else "All Tracks"
                is PlayingContext.AlbumContext -> if (playerState.currentLanguage == com.example.Russify.presentation.state.AppLanguage.RU) "Альбом: ${ctx.name}" else "Album: ${ctx.name}"
                is PlayingContext.PlaylistContext -> if (playerState.currentLanguage == com.example.Russify.presentation.state.AppLanguage.RU) "Плейлист: ${ctx.name}" else "Playlist: ${ctx.name}"
            }
            Text(contextText, fontSize = 14.sp, color = themeTextColor.copy(alpha = 0.5f))
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Slider(
                value = sliderPosition,
                onValueChange = { sliderPosition = it },
                thumb = { Box(modifier = Modifier.size(16.dp).background(themeIconColor, CircleShape)) },
                track = { s ->
                    SliderDefaults.Track(
                        colors = SliderDefaults.colors(
                            activeTrackColor = themeIconColor,
                            inactiveTrackColor = themeIconColor.copy(alpha = 0.2f)
                        ),
                        sliderState = s,
                        modifier = Modifier.height(4.dp)
                    )
                }
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("01:23", fontSize = 12.sp, color = themeTextColor)
                // ИСПРАВЛЕНО ТУТ: track.duration -> track.durationFormatted
                Text(track.durationFormatted, fontSize = 12.sp, color = themeTextColor)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = { playerState.toggleRepeatMode() }) {
                val icon = when(playerState.repeatMode) {
                    RepeatMode.NONE -> Icons.Default.Repeat
                    RepeatMode.ALL -> Icons.Default.Repeat
                    RepeatMode.ONE -> Icons.Default.RepeatOne
                }
                val tint = if(playerState.repeatMode == RepeatMode.NONE) themeIconColor.copy(alpha = 0.4f) else themeIconColor
                Icon(icon, "Repeat", tint = tint, modifier = Modifier.size(28.dp))
            }

            IconButton(
                onClick = {
                    navigationDirection = -1
                    playerState.skipToPrevious()
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.SkipPrevious, "Prev", tint = themeIconColor, modifier = Modifier.size(40.dp))
            }

            IconButton(
                onClick = { playerState.togglePlayPause() },
                modifier = Modifier.size(72.dp).background(themeIconColor, CircleShape)
            ) {
                Icon(
                    if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    "Play",
                    tint = themeBgColor,
                    modifier = Modifier.size(36.dp)
                )
            }

            IconButton(
                onClick = {
                    navigationDirection = 1
                    playerState.skipToNext()
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.SkipNext, "Next", tint = themeIconColor, modifier = Modifier.size(40.dp))
            }

            AnimatedContent(
                targetState = track.isFavorite,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "LikeAnim"
            ) { isFavorite ->
                IconButton(onClick = { playerState.toggleFavorite(track) }) {
                    Icon(
                        if(isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        "Like",
                        tint = if(isFavorite) Color.Red else themeIconColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}