package com.example.Russify.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Russify.model.PlayingContext
import com.example.Russify.model.Track
import com.example.Russify.presentation.components.TrackRowItem
import com.example.Russify.presentation.state.MusicPlayerState
import com.example.Russify.ui.theme.*

@Composable
fun PlaylistDetailPopup(
    title: String,
    author: String,
    tracks: List<Track>,
    playerState: MusicPlayerState,
    onClose: () -> Unit,
    isAlbum: Boolean = false
) {
    val context = if (isAlbum) PlayingContext.AlbumContext(title) else PlayingContext.PlaylistContext(title)
    val language = playerState.currentLanguage

    val themeHeaderStart = ThemeColors.headerGradientStart(playerState)
    val themeDarkBg = ThemeColors.darkBackground(playerState)
    val themeTextWhite = ThemeColors.textWhite(playerState)
    val themeTextGray = ThemeColors.textGray(playerState)
    val themePlaylistBg = ThemeColors.playlistListBackground(playerState)
    val themePlaylistCover = ThemeColors.playlistCoverColor(playerState)
    val themeButtonColor = ThemeColors.buttonColor(playerState)

    BackHandler { onClose() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(themeHeaderStart, themeDarkBg)))
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            IconButton(onClick = onClose, modifier = Modifier.padding(16.dp)) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    "Back",
                    tint = themeTextWhite,
                    modifier = Modifier.size(32.dp)
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(themePlaylistCover),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.MusicNote,
                        null,
                        modifier = Modifier.size(80.dp),
                        tint = Color.Black
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(title, color = themeTextWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(
                    if (isAlbum) "Альбом • $author" else "Плейлист • $author",
                    color = themeTextGray
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (tracks.isNotEmpty()) {
                            playerState.playTrack(tracks.first(), context, tracks)
                            playerState.isPlayerExpanded = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = themeButtonColor),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, null, tint = Color.Black)
                    Text("Слушать", color = themeTextWhite, modifier = Modifier.padding(start = 8.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                color = themePlaylistBg,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(
                        items = tracks,
                        key = { track -> track.id }
                    ) { track ->
                        val actualTrack = playerState.allTracks.find { it.id == track.id } ?: track

                        TrackRowItem(
                            track = actualTrack,
                            onClick = {
                                playerState.playTrack(actualTrack, context, tracks)
                                playerState.isPlayerExpanded = true
                            },
                            onToggleFavorite = {
                                playerState.toggleFavorite(actualTrack)
                            },
                            onAddToPlaylist = { playerState.openAddToPlaylistDialog(actualTrack) },
                            onAddToQueue = { playerState.addToQueue(actualTrack) },
                            onPlayNext = { playerState.playNext(actualTrack) },
                            language = language,
                            playerState = playerState
                        )
                    }

                    item { Spacer(modifier = Modifier.height(100.dp)) }
                }
            }
        }
    }
}