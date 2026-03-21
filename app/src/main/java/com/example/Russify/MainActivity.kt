package com.example.Russify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.Russify.data.MusicRepository
import com.example.Russify.data.local.TokenManager
import com.example.Russify.data.repository.AuthRepository
import com.example.Russify.model.Playlist
import com.example.Russify.presentation.components.AddToPlaylistDialog
import com.example.Russify.presentation.components.CreatePlaylistDialog
import com.example.Russify.presentation.screens.*
import com.example.Russify.presentation.state.MusicPlayerState
import com.example.Russify.ui.theme.JopaPisunTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Инициализация системного сплэша
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            val playerState = remember { MusicPlayerState(context) }

            // СОСТОЯНИЯ
            var showSplash by remember { mutableStateOf(true) }
            var currentScreen by remember { mutableIntStateOf(0) }
            var isAuthenticated by remember { mutableStateOf(tokenManager.isLoggedIn()) }

            // Загрузка данных
            LaunchedEffect(Unit) {
                playerState.allTracks = MusicRepository.loadTracksFromServer()
                playerState.playlists = MusicRepository.getPlaylists()
                playerState.albums = MusicRepository.getAlbums()
            }

            JopaPisunTheme(useOceanTheme = playerState.useOceanTheme) {

                if (showSplash) {
                    // ЭКРАН 1: Полноэкранный сплэш с текстом
                    FullSplashScreenContent(onTimeout = { showSplash = false })
                } else if (!isAuthenticated) {
                    // ЭКРАН 2: Авторизация
                    AuthScreen(
                        onAuthSuccess = {
                            currentScreen = 0
                            isAuthenticated = true
                        },
                        playerState = playerState
                    )
                } else {
                    // ЭКРАН 3: Главное приложение
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
                }

                // Дополнительные UI элементы
                if (!showSplash && isAuthenticated) {
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
    }
}

/**
 * Компонент для отображения картинки на весь экран + Текст Russify
 */
@Composable
fun FullSplashScreenContent(onTimeout: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {

        Image(
            painter = painterResource(id = R.drawable.splash_logo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )


        Text(
            text = "Russify",
            color = Color.White,
            fontSize = 48.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(bottom = 100.dp)
        )
    }


    LaunchedEffect(Unit) {
        delay(1000)
        onTimeout()
    }
}
