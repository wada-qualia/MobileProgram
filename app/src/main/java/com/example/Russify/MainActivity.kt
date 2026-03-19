package com.example.Russify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.Russify.data.MusicRepository
import com.example.Russify.model.Playlist
import com.example.Russify.presentation.components.AddToPlaylistDialog
import com.example.Russify.presentation.components.CreatePlaylistDialog
import com.example.Russify.presentation.screens.*
import com.example.Russify.presentation.state.MusicPlayerState
import com.example.Russify.ui.theme.JopaPisunTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val playerState = remember { MusicPlayerState(context) }
            var currentScreen by remember { mutableIntStateOf(0) }
            var isAuthenticated by remember { mutableStateOf(false) }


            LaunchedEffect(Unit) {
                playerState.allTracks = MusicRepository.loadTracksFromServer()
                playerState.playlists = MusicRepository.getPlaylists()
                playerState.albums = MusicRepository.getAlbums()
            }


            JopaPisunTheme(useOceanTheme = playerState.useOceanTheme) {
                if (!isAuthenticated) {
                    AuthScreen(
                        onAuthSuccess = { isAuthenticated = true },
                        playerState = playerState
                    )
                    return@JopaPisunTheme
                }

                Scaffold(
                    bottomBar = {
                        NavigationBar(
                            containerColor = if (playerState.useOceanTheme)
                                com.example.Russify.ui.theme.OceanBottomNavColor
                            else
                                com.example.Russify.ui.theme.PinkBottomNavColor
                        ) {

                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Home, contentDescription = null) },
                                label = { Text(stringResource(R.string.nav_home)) },
                                selected = currentScreen == 0,
                                onClick = { currentScreen = 0 }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                                label = { Text(stringResource(R.string.nav_favorites)) },
                                selected = currentScreen == 1,
                                onClick = { currentScreen = 1 }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                                label = { Text(stringResource(R.string.nav_settings)) },
                                selected = currentScreen == 2,
                                onClick = { currentScreen = 2 }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Person, contentDescription = null) },
                                label = { Text(stringResource(R.string.nav_profile)) },
                                selected = currentScreen == 3,
                                onClick = { currentScreen = 3 }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                        when (currentScreen) {
                            0 -> HomeScreen(playerState = playerState, context = context)
                            1 -> FavoritesScreen(playerState = playerState, context = context)
                            2 -> SettingsScreen(
                                playerState = playerState,
                                onLogout = { isAuthenticated = false },
                                onChangeAccount = { isAuthenticated = false }
                            )
                            3 -> ProfileScreen(
                                onLogout = { isAuthenticated = false },
                                playerState = playerState
                            )
                        }

                        Column(modifier = Modifier.align(Alignment.BottomCenter)) {
                            if (playerState.currentTrack != null && !playerState.isPlayerExpanded) {
                                MiniPlayer(playerState = playerState)
                            }
                        }
                    }
                }

                if (playerState.isPlayerExpanded) {
                    FullPlayerScreen(playerState = playerState)
                }

                if (playerState.showCreatePlaylistDialog) {
                    CreatePlaylistDialog(
                        onDismiss = { playerState.showCreatePlaylistDialog = false },
                        onCreate = { name -> playerState.createNewPlaylist(name) },
                        language = playerState.currentLanguage
                    )
                }

                if (playerState.showAddToPlaylistDialog) {
                    AddToPlaylistDialog(
                        playlists = playerState.playlists,
                        onDismiss = { playerState.showAddToPlaylistDialog = false },
                        onSelect = { playlist: Playlist -> playerState.addTrackToPlaylist(playlist) },
                        language = playerState.currentLanguage
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}