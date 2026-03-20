package com.example.Russify.presentation.state

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.Russify.data.MusicRepository
import com.example.Russify.model.Album
import com.example.Russify.model.PlayingContext
import com.example.Russify.model.Playlist
import com.example.Russify.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class AppLanguage { RU, EN }
enum class RepeatMode { NONE, ALL, ONE }

class MusicPlayerState(private val context: Context) {
    private val exoPlayer = ExoPlayer.Builder(context).build()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var progressJob: Job? = null

    var currentTrack by mutableStateOf<Track?>(null)
    var isPlaying by mutableStateOf(false)
    var isPlayerExpanded by mutableStateOf(false)
    var allTracks by mutableStateOf<List<Track>>(emptyList())
    var currentQueue by mutableStateOf<List<Track>>(emptyList())
    var playingContext by mutableStateOf<PlayingContext>(PlayingContext.AllTracks)
    var repeatMode by mutableStateOf(RepeatMode.NONE)
    var currentLanguage by mutableStateOf(AppLanguage.RU)
    var useOceanTheme by mutableStateOf(false)
    var activeMoodCategory by mutableStateOf<String?>(null)
    var playerErrorMessage by mutableStateOf<String?>(null)

    var playbackProgressMs by mutableLongStateOf(0L)
    var durationMs by mutableLongStateOf(0L)

    var playlists by mutableStateOf<List<Playlist>>(emptyList())
    var albums by mutableStateOf<List<Album>>(emptyList())

    var openedPlaylist by mutableStateOf<Playlist?>(null)
    var openedAlbum by mutableStateOf<Album?>(null)
    var showCreatePlaylistDialog by mutableStateOf(false)
    var showAddToPlaylistDialog by mutableStateOf(false)
    var trackToAdd by mutableStateOf<Track?>(null)

    val progressFraction: Float
        get() = if (durationMs > 0) {
            (playbackProgressMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }

    val currentPositionFormatted: String
        get() = formatMillis(playbackProgressMs)

    val currentDurationFormatted: String
        get() = formatMillis(durationMs)

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlayingNow: Boolean) {
                isPlaying = isPlayingNow
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                durationMs = normalizedDuration()
                if (playbackState == Player.STATE_ENDED) {
                    skipToNext()
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                isPlaying = false
                playerErrorMessage = error.localizedMessage ?: "Playback failed"
            }
        })

        startProgressUpdates()
    }

    fun playTrack(
        track: Track,
        context: PlayingContext = PlayingContext.AllTracks,
        playlistTracks: List<Track> = emptyList()
    ) {
        currentTrack = track
        playingContext = context
        currentQueue = when {
            playlistTracks.isNotEmpty() -> playlistTracks
            allTracks.isNotEmpty() -> allTracks
            else -> listOf(track)
        }
        durationMs = (track.durationSeconds * 1000L).coerceAtLeast(0L)
        playbackProgressMs = 0L
        playerErrorMessage = null

        val audioUrl = track.audioUrl
        if (audioUrl.isBlank()) {
            isPlaying = false
            playerErrorMessage = "Track URL is missing"
            return
        }

        val mediaItem = MediaItem.fromUri(audioUrl)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    fun togglePlayPause() {
        if (currentTrack == null) {
            if (allTracks.isNotEmpty()) {
                playTrack(allTracks.first(), PlayingContext.AllTracks, allTracks)
            } else {
                playerErrorMessage = "No tracks available"
            }
            return
        }

        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
    }

    fun toggleMoodPlayback() {
        togglePlayPause()
    }

    fun seekToFraction(fraction: Float) {
        val duration = durationMs
        if (duration <= 0) return

        val targetPosition = (duration * fraction.coerceIn(0f, 1f)).toLong()
        exoPlayer.seekTo(targetPosition)
        playbackProgressMs = targetPosition
    }

    fun skipToNext() {
        val queue = activeQueue()
        val track = currentTrack ?: return
        if (queue.isEmpty()) return

        val currentIndex = queue.indexOfFirst { it.id == track.id }
        if (currentIndex == -1) return

        val nextIndex = (currentIndex + 1) % queue.size
        playTrack(queue[nextIndex], playingContext, queue)
    }

    fun skipToPrevious() {
        val queue = activeQueue()
        val track = currentTrack ?: return
        if (queue.isEmpty()) return

        val currentIndex = queue.indexOfFirst { it.id == track.id }
        if (currentIndex == -1) return

        val previousIndex = if (currentIndex == 0) queue.lastIndex else currentIndex - 1
        playTrack(queue[previousIndex], playingContext, queue)
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
        val targetFavoriteState = !track.isFavorite
        updateTrackEverywhere(track.copy(isFavorite = targetFavoriteState))

        scope.launch {
            MusicRepository.setTrackFavorite(track.id, targetFavoriteState)
                .onFailure {
                    updateTrackEverywhere(track)
                    playerErrorMessage = it.localizedMessage ?: "Failed to update favourites"
                }
        }
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
        exoPlayer.stop()
        isPlayerExpanded = false
        currentTrack = null
        currentQueue = emptyList()
        playbackProgressMs = 0L
        durationMs = 0L
        playerErrorMessage = null
    }

    fun playMoodMixesOnly(mood: String) {
        activeMoodCategory = mood
    }

    fun addToQueue(track: Track) {
        if (currentQueue.none { it.id == track.id }) {
            currentQueue = activeQueue().toMutableList().apply { add(track) }
        }
    }

    fun playNext(track: Track) {
        val current = currentTrack ?: run {
            currentQueue = listOf(track)
            return
        }
        val queue = activeQueue().toMutableList()
        if (queue.none { it.id == track.id }) {
            val currentIndex = queue.indexOfFirst { it.id == current.id }
            if (currentIndex >= 0) {
                queue.add(currentIndex + 1, track)
            } else {
                queue.add(track)
            }
            currentQueue = queue
        }
    }

    fun release() {
        progressJob?.cancel()
        scope.cancel()
        exoPlayer.release()
    }

    private fun activeQueue(): List<Track> {
        return if (currentQueue.isNotEmpty()) currentQueue else allTracks
    }

    private fun updateTrackEverywhere(updatedTrack: Track) {
        allTracks = allTracks.map { if (it.id == updatedTrack.id) updatedTrack else it }
        currentQueue = currentQueue.map { if (it.id == updatedTrack.id) updatedTrack else it }

        if (currentTrack?.id == updatedTrack.id) {
            currentTrack = updatedTrack
        }

        openedPlaylist = openedPlaylist?.let { playlist ->
            playlist.copy(
                tracks = playlist.tracks.map { track ->
                    if (track.id == updatedTrack.id) updatedTrack else track
                }.toMutableList()
            )
        }

        openedAlbum = openedAlbum?.let { album ->
            album.copy(
                tracks = album.tracks.map { track ->
                    if (track.id == updatedTrack.id) updatedTrack else track
                }
            )
        }

        playlists = playlists.map { playlist ->
            playlist.copy(
                tracks = playlist.tracks.map { track ->
                    if (track.id == updatedTrack.id) updatedTrack else track
                }.toMutableList()
            )
        }

        albums = albums.map { album ->
            album.copy(
                tracks = album.tracks.map { track ->
                    if (track.id == updatedTrack.id) updatedTrack else track
                }
            )
        }
    }

    private fun startProgressUpdates() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (true) {
                playbackProgressMs = exoPlayer.currentPosition.coerceAtLeast(0L)
                durationMs = normalizedDuration()
                delay(500)
            }
        }
    }

    private fun normalizedDuration(): Long {
        val duration = exoPlayer.duration
        return if (duration > 0) duration else 0L
    }

    private fun formatMillis(value: Long): String {
        val safeValue = value.coerceAtLeast(0L) / 1000L
        val minutes = safeValue / 60
        val seconds = safeValue % 60
        return "%02d:%02d".format(minutes, seconds)
    }
}
