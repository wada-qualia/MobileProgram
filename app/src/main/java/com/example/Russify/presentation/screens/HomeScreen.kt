package com.example.Russify.presentation.screens

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import com.example.Russify.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Russify.presentation.state.AppLanguage
import com.example.Russify.presentation.state.MusicPlayerState
import com.example.Russify.model.PlayingContext
import com.example.Russify.presentation.components.TrackRowItem
import com.example.Russify.ui.theme.*

data class MoodItem(val titleResId: Int, val lightColor: Color, val deepColor: Color)

@Composable
fun HomeScreen(
    playerState: MusicPlayerState,
    context: Context
) {
    val themeHeaderStart = ThemeColors.headerGradientStart(playerState)
    val themeDarkBg = ThemeColors.darkBackground(playerState)
    val themeTextWhite = ThemeColors.textWhite(playerState)
    val themePlaylistBg = ThemeColors.playlistListBackground(playerState)

    var targetColor by remember { mutableStateOf(themeHeaderStart) }
    val animatedBgColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 800)
    )

    var searchQuery by remember { mutableStateOf("") }
    val language = playerState.currentLanguage

    val moodData = listOf(
        MoodItem(R.string.mood_vigor, MoodVigorLight, MoodVigorDeep),
        MoodItem(R.string.mood_dynamic, MoodDynamicLight, MoodDynamicDeep),
        MoodItem(R.string.mood_freshness, MoodFreshnessLight, MoodFreshnessDeep),
        MoodItem(R.string.mood_joy, MoodJoyLight, MoodJoyDeep),
        MoodItem(R.string.mood_sadness, MoodSadnessLight, MoodSadnessDeep),
        MoodItem(R.string.mood_party, MoodPartyLight, MoodPartyDeep)
    )

    fun getString(resId: Int): String {
        return context.getString(resId)
    }

    val filteredTracks = remember(searchQuery, playerState.allTracks) {
        playerState.allTracks.filter { track ->
            searchQuery.isBlank() ||
                track.title.contains(searchQuery, ignoreCase = true) ||
                track.artist.contains(searchQuery, ignoreCase = true)
        }
    }

    val canStartPlayback = playerState.currentTrack != null || filteredTracks.isNotEmpty()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(animatedBgColor, themeDarkBg))),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = 20.dp, bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (playerState.currentTrack != null) {
                        val playingText = if (language == AppLanguage.RU) "Сейчас играет:" else "Now playing:"
                        "$playingText ${playerState.currentTrack?.title.orEmpty()}"
                    } else if (playerState.activeMoodCategory != null) {
                        val playingText = if (language == AppLanguage.RU) "Играет:" else "Playing:"
                        "$playingText ${playerState.activeMoodCategory}"
                    } else {
                        getString(R.string.mood_mix)
                    },
                    color = themeTextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(75.dp)
                        .shadow(12.dp, CircleShape, spotColor = Color.Black)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable(enabled = canStartPlayback) {
                            if (playerState.currentTrack != null) {
                                playerState.togglePlayPause()
                            } else if (filteredTracks.isNotEmpty()) {
                                playerState.playTrack(
                                    filteredTracks.first(),
                                    PlayingContext.AllTracks,
                                    filteredTracks
                                )
                            } else {
                                playerState.playerErrorMessage = if (language == AppLanguage.RU) {
                                    "Нет доступных треков"
                                } else {
                                    "No tracks available"
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = if (canStartPlayback) Color.Black else Color.Black.copy(alpha = 0.35f),
                        modifier = Modifier.size(44.dp)
                    )
                }
            }
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                placeholder = {
                    Text(
                        getString(R.string.search_hint),
                        color = themeTextWhite.copy(alpha = 0.4f)
                    )
                },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = themeTextWhite) },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.12f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.08f),
                    focusedTextColor = themeTextWhite,
                    unfocusedTextColor = themeTextWhite
                )
            )
        }

        item {
            playerState.playerErrorMessage?.let { message ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x33FF6B6B))
                ) {
                    Text(
                        text = message,
                        color = themeTextWhite,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .border(
                        BorderStroke(2.dp, Color.White.copy(alpha = 0.2f)),
                        RoundedCornerShape(28.dp)
                    )
                    .clip(RoundedCornerShape(28.dp))
                    .background(themePlaylistBg.copy(alpha = 0.95f))
                    .padding(14.dp)
            ) {
                moodData.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        rowItems.forEach { mood ->
                            val moodTitle = getString(mood.titleResId)

                            // Проверяем, играет ли сейчас этот конкретный микс
                            val isThisMoodPlaying = playerState.activeMoodCategory == moodTitle && playerState.isPlaying

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .shadow(
                                        elevation = if (isThisMoodPlaying) 25.dp else 14.dp,
                                        shape = RoundedCornerShape(22.dp),
                                        clip = false,
                                        spotColor = mood.deepColor.copy(alpha = 0.8f)
                                    )
                                    .border(
                                        width = 1.dp,
                                        brush = Brush.verticalGradient(
                                            listOf(Color.White.copy(alpha = 0.4f), Color.Transparent)
                                        ),
                                        shape = RoundedCornerShape(22.dp)
                                    )
                                    .clip(RoundedCornerShape(22.dp))
                                    .background(Brush.verticalGradient(listOf(mood.lightColor, mood.deepColor)))
                                    .clickable {
                                        targetColor = mood.deepColor
                                        playerState.playMoodMixesOnly(moodTitle)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = if (isThisMoodPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = themeTextWhite,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = moodTitle,
                                        fontWeight = FontWeight.Black,
                                        color = themeTextWhite,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = if (language == AppLanguage.RU) "Треки" else "Tracks",
                    color = themeTextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (searchQuery.isBlank()) {
                        if (language == AppLanguage.RU) "Все доступные треки" else "All available tracks"
                    } else {
                        if (language == AppLanguage.RU) "Результаты поиска" else "Search results"
                    },
                    color = themeTextWhite.copy(alpha = 0.7f),
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (filteredTracks.isEmpty()) {
            item {
                Text(
                    text = if (language == AppLanguage.RU) {
                        "Треки пока не загружены"
                    } else {
                        "Tracks are not available yet"
                    },
                    color = themeTextWhite.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }
        } else {
            items(items = filteredTracks, key = { it.id }) { track ->
                TrackRowItem(
                    track = track,
                    onClick = {
                        playerState.playTrack(track, PlayingContext.AllTracks, filteredTracks)
                        playerState.isPlayerExpanded = true
                    },
                    onToggleFavorite = { playerState.toggleFavorite(track) },
                    onAddToPlaylist = { playerState.openAddToPlaylistDialog(track) },
                    onAddToQueue = { playerState.addToQueue(track) },
                    onPlayNext = { playerState.playNext(track) },
                    language = language,
                    playerState = playerState
                )
            }
        }
    }
}
