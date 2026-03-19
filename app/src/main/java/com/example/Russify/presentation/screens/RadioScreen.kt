package com.example.Russify.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Russify.model.Album
import com.example.Russify.model.PlayingContext
import com.example.Russify.model.Playlist
import com.example.Russify.presentation.components.TrackRowItem
import com.example.Russify.presentation.state.MusicPlayerState
import com.example.Russify.ui.theme.*

@Composable
fun RadioScreen(playerState: MusicPlayerState) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchText by remember { mutableStateOf("") }

    var showPlaylistsOverlay by remember { mutableStateOf(false) }

    if (playerState.openedPlaylist != null || playerState.openedAlbum != null) {
        BackHandler { playerState.openedPlaylist = null; playerState.openedAlbum = null }
    } else if (showPlaylistsOverlay) {
        BackHandler { showPlaylistsOverlay = false }
    } else if (selectedTab != 0) {
        BackHandler { selectedTab = 0 }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = listOf(HeaderGradientStart, DarkBackground)))
                .statusBarsPadding(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                    Text("Коллекция", fontSize = 36.sp, fontWeight = FontWeight.Black, color = TextWhite)
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LargeTabButton("Треки", selectedTab == 0, Modifier.weight(1f)) { selectedTab = 0 }
                    LargeTabButton("Альбомы", selectedTab == 1, Modifier.weight(1f)) { selectedTab = 1 }
                    LargeTabButton("Плейлисты", showPlaylistsOverlay, Modifier.weight(1f)) { showPlaylistsOverlay = true }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(56.dp),
                    placeholder = { Text("Поиск...", color = Color.White.copy(alpha = 0.6f)) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.White) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.15f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            when (selectedTab) {
                0 -> {
                    val tracks = playerState.allTracks.filter { it.isFavorite } // ИСПРАВЛЕНО
                    if (tracks.isEmpty()) {
                        item { Text("Нет избранных треков", modifier = Modifier.padding(24.dp), color = Color.White) }
                    } else {
                        item { Spacer(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).height(12.dp).background(PlaylistListBackground, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))) }

                        itemsIndexed(tracks) { _, track ->
                            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).background(PlaylistListBackground)) {
                                TrackRowItem(
                                    track = track,
                                    onClick = { playerState.playTrack(track, PlayingContext.AllTracks, tracks) },
                                    onToggleFavorite = { playerState.toggleFavorite(track) },
                                    onAddToPlaylist = { playerState.openAddToPlaylistDialog(track) },
                                    onAddToQueue = { playerState.addToQueue(track) },
                                    onPlayNext = { playerState.playNext(track) },
                                    playerState = playerState
                                )
                            }
                        }

                        item { Spacer(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).height(12.dp).background(PlaylistListBackground, RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))) }
                    }
                }
                1 -> {
                    item { Spacer(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).height(12.dp).background(PlaylistListBackground, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))) }
                    itemsIndexed(playerState.albums) { _, album ->
                        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).background(PlaylistListBackground)) {
                            AlbumRowItem(album) { playerState.openedAlbum = album }
                        }
                    }
                    item { Spacer(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).height(12.dp).background(PlaylistListBackground, RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))) }
                }
            }
        }

        if (showPlaylistsOverlay) {
            AllPlaylistsOverlay(
                playerState = playerState,
                onDismiss = { showPlaylistsOverlay = false }
            )
        }

        playerState.openedPlaylist?.let { pl -> PlaylistDetailOverlay(pl, { playerState.openedPlaylist = null }, playerState) }
        playerState.openedAlbum?.let { al -> AlbumDetailOverlay(al, { playerState.openedAlbum = null }, playerState) }
    }
}

@Composable
fun AllPlaylistsOverlay(playerState: MusicPlayerState, onDismiss: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(DarkBackground).statusBarsPadding()) {
        Column {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDismiss) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) }
                Spacer(modifier = Modifier.width(16.dp))
                Text("Плейлисты", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Surface(modifier = Modifier.fillMaxSize(), color = PlaylistListBackground, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        CreatePlaylistButton(onClick = { playerState.showCreatePlaylistDialog = true })
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    itemsIndexed(playerState.playlists) { _, playlist ->
                        PlaylistRowItem(playlist) { playerState.openedPlaylist = playlist }
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
fun LargeTabButton(text: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.height(50.dp).clickable { onClick() }.border(width = if (isSelected) 2.dp else 1.dp, color = if (isSelected) Color.White else Color.White.copy(alpha = 0.3f), shape = RoundedCornerShape(12.dp)),
        color = if (isSelected) Color.White.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(contentAlignment = Alignment.Center) { Text(text = text, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White) }
    }
}

@Composable
fun CreatePlaylistButton(onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = ButtonColor), shape = RoundedCornerShape(12.dp)) {
        Icon(Icons.Default.Add, null, tint = TextWhite)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Создать новый плейлист", color = TextWhite, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PlaylistRowItem(playlist: Playlist, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 4.dp).background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(12.dp)).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(50.dp).background(Color.Gray.copy(alpha = 0.4f), RoundedCornerShape(8.dp)))
        Spacer(Modifier.width(16.dp))
        Column {
            Text(playlist.title, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 16.sp)
            Text("${playlist.tracks.size} треков", color = Color.Black.copy(alpha = 0.6f), fontSize = 12.sp)
        }
    }
}

@Composable
fun AlbumRowItem(album: Album, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 4.dp).background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(12.dp)).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(50.dp).background(Color.DarkGray.copy(alpha = 0.4f), RoundedCornerShape(8.dp)))
        Spacer(Modifier.width(16.dp))
        Column {
            Text(album.title, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 16.sp)
            Text(album.authorName, color = Color.Black.copy(alpha = 0.6f), fontSize = 12.sp)
        }
    }
}

@Composable
fun PlaylistDetailOverlay(playlist: Playlist, onDismiss: () -> Unit, playerState: MusicPlayerState) {
    BackHandler { onDismiss() }
    Box(modifier = Modifier.fillMaxSize().background(DarkBackground).statusBarsPadding()) {
        Column {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDismiss) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) }
                Spacer(modifier = Modifier.width(16.dp))
                Text(playlist.title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Surface(modifier = Modifier.fillMaxSize(), color = PlaylistListBackground, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    itemsIndexed(playlist.tracks) { _, track ->
                        TrackRowItem(
                            track = track,
                            onClick = { playerState.playTrack(track, PlayingContext.PlaylistContext(playlist.title), playlist.tracks) },
                            onToggleFavorite = { playerState.toggleFavorite(track) },
                            onAddToPlaylist = { playerState.openAddToPlaylistDialog(track) },
                            onAddToQueue = { playerState.addToQueue(track) },
                            onPlayNext = { playerState.playNext(track) },
                            playerState = playerState
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
fun AlbumDetailOverlay(album: Album, onDismiss: () -> Unit, playerState: MusicPlayerState) {
    BackHandler { onDismiss() }
    Box(modifier = Modifier.fillMaxSize().background(DarkBackground).statusBarsPadding()) {
        Column {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDismiss) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) }
                Spacer(modifier = Modifier.width(16.dp))
                Text(album.title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Surface(modifier = Modifier.fillMaxSize(), color = PlaylistListBackground, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    itemsIndexed(album.tracks) { _, track ->
                        TrackRowItem(
                            track = track,
                            onClick = { playerState.playTrack(track, PlayingContext.AlbumContext(album.title), album.tracks) },
                            onToggleFavorite = { playerState.toggleFavorite(track) },
                            onAddToPlaylist = { playerState.openAddToPlaylistDialog(track) },
                            onAddToQueue = { playerState.addToQueue(track) },
                            onPlayNext = { playerState.playNext(track) },
                            playerState = playerState
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}