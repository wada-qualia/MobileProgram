package com.example.Russify.presentation.screens

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Russify.model.Album
import com.example.Russify.model.PlayingContext
import com.example.Russify.model.Playlist
import com.example.Russify.presentation.components.TrackRowItem
import com.example.Russify.presentation.state.AppLanguage
import com.example.Russify.presentation.state.MusicPlayerState
import com.example.Russify.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    playerState: MusicPlayerState,
    context: Context
) {
    val language = playerState.currentLanguage

    val themeHeaderStart = ThemeColors.headerGradientStart(playerState)
    val themeDarkBg = ThemeColors.darkBackground(playerState)
    val themeTextWhite = ThemeColors.textWhite(playerState)
    val themePlaylistBg = ThemeColors.playlistListBackground(playerState)
    val themeActiveIcon = ThemeColors.activeIconColor(playerState)

    var searchQuery by remember { mutableStateOf("") }
    var isShowingAlbums by remember { mutableStateOf(false) }
    var showPlaylistsOverlay by remember { mutableStateOf(false) }

    if (showPlaylistsOverlay) {
        BackHandler { showPlaylistsOverlay = false }
    } else if (isShowingAlbums) {
        BackHandler { isShowingAlbums = false }
    }

    val allTracks = playerState.allTracks
    val userPlaylists = playerState.playlists
    val userAlbums = playerState.albums

    val filteredPlaylists = userPlaylists.filter { it.title.contains(searchQuery, ignoreCase = true) }
    val filteredAlbums = userAlbums.filter { it.title.contains(searchQuery, ignoreCase = true) }
    val filteredTracks = allTracks.filter {
        it.isFavorite &&
            (it.title.contains(searchQuery, ignoreCase = true) || it.artist.contains(searchQuery, ignoreCase = true))
    }

    val displayTracks = remember(filteredTracks) { filteredTracks }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(themeHeaderStart, themeDarkBg))).statusBarsPadding()
        ) {
            Text(
                text = if (language == AppLanguage.RU) "Избранное" else "Favorites",
                color = themeTextWhite, fontSize = 36.sp, fontWeight = FontWeight.Black,
                modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 16.dp)
            )

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { showPlaylistsOverlay = true }, modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (showPlaylistsOverlay) themeTextWhite.copy(alpha = 0.3f) else Color.Transparent, contentColor = themeTextWhite),
                    shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, themeTextWhite.copy(alpha = 0.5f))
                ) { Text(text = if (language == AppLanguage.RU) "Плейлисты" else "Playlists", fontSize = 16.sp, fontWeight = FontWeight.Bold) }

                Button(
                    onClick = { isShowingAlbums = true }, modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isShowingAlbums) themeTextWhite.copy(alpha = 0.3f) else Color.Transparent, contentColor = themeTextWhite),
                    shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, themeTextWhite.copy(alpha = 0.5f))
                ) { Text(text = if (language == AppLanguage.RU) "Альбомы" else "Albums", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(44.dp).clip(RoundedCornerShape(8.dp)).background(themeTextWhite.copy(alpha = 0.2f)), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Search, null, tint = themeTextWhite.copy(alpha = 0.6f), modifier = Modifier.padding(start = 12.dp, end = 8.dp))
                BasicTextField(
                    value = searchQuery, onValueChange = { searchQuery = it }, modifier = Modifier.weight(1f),
                    textStyle = TextStyle(color = themeTextWhite, fontSize = 16.sp), singleLine = true, cursorBrush = SolidColor(themeTextWhite),
                    decorationBox = { innerTextField -> Box(contentAlignment = Alignment.CenterStart) { if (searchQuery.isEmpty()) Text(if (language == AppLanguage.RU) "Поиск" else "Search", color = themeTextWhite.copy(alpha = 0.6f), fontSize = 16.sp); innerTextField() } }
                )
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }, modifier = Modifier.padding(end = 4.dp)) { Icon(Icons.Default.Close, "Clear", tint = themeTextWhite.copy(alpha = 0.6f), modifier = Modifier.size(20.dp)) }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(modifier = Modifier.fillMaxWidth().weight(1f), color = themePlaylistBg, shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)) {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (!isShowingAlbums) {
                        item { Text(if (language == AppLanguage.RU) "Избранные треки" else "Liked Tracks", color = themeTextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp)) }
                        if (displayTracks.isEmpty()) {
                            item { Text(if (language == AppLanguage.RU) "Нет избранных треков" else "No liked tracks", color = themeTextWhite.copy(alpha = 0.6f), fontSize = 14.sp) }
                        } else {
                            items(items = displayTracks, key = { it.id }) { actualTrack ->
                                TrackRowItem(
                                    track = actualTrack,
                                    onClick = {
                                        playerState.playTrack(actualTrack, PlayingContext.AllTracks, displayTracks)
                                        playerState.isPlayerExpanded = true
                                    },
                                    onToggleFavorite = { playerState.toggleFavorite(actualTrack) }, onAddToPlaylist = { playerState.openAddToPlaylistDialog(actualTrack) },
                                    onAddToQueue = { playerState.addToQueue(actualTrack) }, onPlayNext = { playerState.playNext(actualTrack) }, language = language, playerState = playerState
                                )
                            }
                        }
                    } else {
                        item { Text(if (language == AppLanguage.RU) "Сохраненные альбомы" else "Saved Albums", color = themeTextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp)) }
                        if (filteredAlbums.isEmpty()) {
                            item { Text(if (language == AppLanguage.RU) "Нет избранных альбомов" else "No liked albums", color = themeTextWhite.copy(alpha = 0.6f), fontSize = 16.sp) }
                        } else {
                            items(filteredAlbums) { album -> AlbumItem(album = album, onClick = { playerState.openedAlbum = album }, themeTextWhite = themeTextWhite, themeActiveIcon = themeActiveIcon, language = language) }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(visible = showPlaylistsOverlay, enter = slideInVertically(initialOffsetY = { it }), exit = slideOutVertically(targetOffsetY = { it })) {
            PlaylistsOverlayScreen(playerState = playerState, playlists = filteredPlaylists, onDismiss = { showPlaylistsOverlay = false }, themeDarkBg = themeDarkBg, themePlaylistBg = themePlaylistBg, themeTextWhite = themeTextWhite, themeActiveIcon = themeActiveIcon, language = language)
        }
    }
}

@Composable
fun PlaylistsOverlayScreen(
    playerState: MusicPlayerState, playlists: List<Playlist>, onDismiss: () -> Unit, themeDarkBg: Color, themePlaylistBg: Color, themeTextWhite: Color, themeActiveIcon: Color, language: AppLanguage
) {
    Column(modifier = Modifier.fillMaxSize().background(themeDarkBg).statusBarsPadding()) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onDismiss) { Icon(Icons.Default.ArrowBack, "Back", tint = themeTextWhite) }
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (language == AppLanguage.RU) "Мои плейлисты" else "My Playlists", color = themeTextWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        Surface(modifier = Modifier.fillMaxSize(), color = themePlaylistBg, shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)) {
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    Button(onClick = { playerState.showCreatePlaylistDialog = true }, modifier = Modifier.fillMaxWidth().height(50.dp).padding(bottom = 8.dp), colors = ButtonDefaults.buttonColors(containerColor = themeActiveIcon), shape = RoundedCornerShape(12.dp)) {
                        Icon(Icons.Default.Add, null, tint = Color.Black, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (language == AppLanguage.RU) "Создать плейлист" else "Create Playlist", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
                if (playlists.isEmpty()) {
                    item { Text(if (language == AppLanguage.RU) "У вас пока нет плейлистов" else "No playlists yet", color = themeTextWhite.copy(alpha = 0.6f), fontSize = 14.sp, modifier = Modifier.padding(top = 16.dp)) }
                } else {
                    items(playlists) { playlist -> PlaylistItem(playlist = playlist, onClick = { playerState.openedPlaylist = playlist }, themeTextWhite = themeTextWhite, themeActiveIcon = themeActiveIcon, language = language) }
                }
                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }
    }
}

@Composable
fun PlaylistItem(playlist: Playlist, onClick: () -> Unit, themeTextWhite: Color, themeActiveIcon: Color, language: AppLanguage) {
    Card(modifier = Modifier.fillMaxWidth().height(70.dp).clickable { onClick() }, colors = CardDefaults.cardColors(containerColor = themeTextWhite.copy(alpha = 0.1f)), shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(themeActiveIcon.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) { Icon(Icons.Default.MusicNote, null, tint = themeTextWhite, modifier = Modifier.size(28.dp)) }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(playlist.title, color = themeTextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                Text("${playlist.tracks.size} ${if (language == AppLanguage.RU) "треков" else "tracks"}", color = themeTextWhite.copy(alpha = 0.6f), fontSize = 13.sp)
            }
            Icon(Icons.Default.KeyboardArrowRight, null, tint = themeTextWhite.copy(alpha = 0.5f), modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
fun AlbumItem(album: Album, onClick: () -> Unit, themeTextWhite: Color, themeActiveIcon: Color, language: AppLanguage) {
    Card(modifier = Modifier.fillMaxWidth().height(80.dp).clickable { onClick() }, colors = CardDefaults.cardColors(containerColor = themeTextWhite.copy(alpha = 0.1f)), shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)).background(themeActiveIcon.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) { Icon(Icons.Default.Album, null, tint = themeTextWhite, modifier = Modifier.size(32.dp)) }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(album.title, color = themeTextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(album.authorName, color = themeTextWhite.copy(alpha = 0.6f), fontSize = 13.sp, maxLines = 1)
            }
            Text("${album.tracks.size} ${if (language == AppLanguage.RU) "треков" else "tracks"}", color = themeTextWhite.copy(alpha = 0.5f), fontSize = 12.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.KeyboardArrowRight, null, tint = themeTextWhite.copy(alpha = 0.5f), modifier = Modifier.size(24.dp))
        }
    }
}
