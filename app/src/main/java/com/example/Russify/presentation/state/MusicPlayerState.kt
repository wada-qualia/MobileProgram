package com.example.Russify.presentation.state

import android.content.Context
import androidx.compose.runtime.*
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.Russify.model.Album
import com.example.Russify.model.PlayingContext
import com.example.Russify.model.Playlist
import com.example.Russify.model.Track
import com.example.Russify.data.MusicRepository

enum class AppLanguage { RU, EN }
enum class RepeatMode { NONE, ALL, ONE }

class MusicPlayerState(private val context: Context) {
    private val exoPlayer = ExoPlayer.Builder(context).build()

    var currentTrack by mutableStateOf<Track?>(null)
    var isPlaying by mutableStateOf(false)
    var isPlayerExpanded by mutableStateOf(false)
    var allTracks by mutableStateOf<List<Track>>(emptyList())
    var playingContext by mutableStateOf<PlayingContext>(PlayingContext.AllTracks)
    var repeatMode by mutableStateOf(RepeatMode.NONE)
    var currentLanguage by mutableStateOf(AppLanguage.RU)
    var useOceanTheme by mutableStateOf(false)
    var activeMoodCategory by mutableStateOf<String?>(null)

    // Списки
    var playlists by mutableStateOf<List<Playlist>>(emptyList())
    var albums by mutableStateOf<List<Album>>(emptyList())

    // Диалоги и оверлеи
    var openedPlaylist by mutableStateOf<Playlist?>(null)
    var openedAlbum by mutableStateOf<Album?>(null)
    var showCreatePlaylistDialog by mutableStateOf(false)
    var showAddToPlaylistDialog by mutableStateOf(false)
    var trackToAdd by mutableStateOf<Track?>(null)

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlayingNow: Boolean) {
                isPlaying = isPlayingNow
            }
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) skipToNext()
            }
        })
    }

    fun playTrack(track: Track, context: PlayingContext = PlayingContext.AllTracks, playlistTracks: List<Track> = emptyList()) {
        currentTrack = track
        playingContext = context

        val audioUrl = track.audioUrl
        if (audioUrl.isNotEmpty()) {
            val mediaItem = MediaItem.fromUri(audioUrl)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()
        }
    }

    fun togglePlayPause() {
        if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
    }


    fun toggleMoodPlayback() {
        togglePlayPause()
    }

    fun skipToNext() {
        if (allTracks.isEmpty() || currentTrack == null) return
        val currentIndex = allTracks.indexOfFirst { it.id == currentTrack?.id }
        if (currentIndex != -1) {
            val nextIndex = (currentIndex + 1) % allTracks.size
            playTrack(allTracks[nextIndex], playingContext)
        }
    }

    fun skipToPrevious() {
        if (allTracks.isEmpty() || currentTrack == null) return
        val currentIndex = allTracks.indexOfFirst { it.id == currentTrack?.id }
        if (currentIndex != -1) {
            val prevIndex = if (currentIndex - 1 < 0) allTracks.size - 1 else currentIndex - 1
            playTrack(allTracks[prevIndex], playingContext)
        }
    }

    fun toggleRepeatMode() {
        repeatMode = when (repeatMode) {
            RepeatMode.NONE -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.NONE
        }
        exoPlayer.repeatMode = when (repeatMode) {
            RepeatMode.NONE -> Player.REPEAT_MODE_OFF
            RepeatMode.ALL -> Player.REPEAT_MODE_ALL
            RepeatMode.ONE -> Player.REPEAT_MODE_ONE
        }
    }

    fun toggleFavorite(track: Track) {
        val updatedTrack = track.copy(isFavorite = !track.isFavorite)
        allTracks = allTracks.map { if (it.id == track.id) updatedTrack else it }
        if (currentTrack?.id == track.id) currentTrack = updatedTrack
    }

    fun createNewPlaylist(name: String) {
        val newPlaylist = MusicRepository.createPlaylist(name, "User")
        playlists = playlists + newPlaylist
        showCreatePlaylistDialog = false
    }

    fun openAddToPlaylistDialog(track: Track) {
        trackToAdd = track
        showAddToPlaylistDialog = true
    }

    fun addTrackToPlaylist(playlist: Playlist) {
        trackToAdd?.let { track ->
            MusicRepository.addTrackToPlaylist(playlist.id, track)
            playlists = MusicRepository.getPlaylists().toList()
        }
        showAddToPlaylistDialog = false
        trackToAdd = null
    }

    fun closeMiniPlayer() {
        currentTrack = null
        exoPlayer.stop()
    }

    fun playMoodMixesOnly(mood: String) {
        activeMoodCategory = mood
    }

    fun addToQueue(track: Track) {}
    fun playNext(track: Track) {}

    fun release() {
        exoPlayer.release()
    }
}