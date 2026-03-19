package com.example.Russify.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.Russify.presentation.state.MusicPlayerState

// ==================== PINK THEME (DEFAULT) ====================
val PinkDarkBackground = Color(0xFF0A0A0A)
val PinkHeaderGradientStart = Color(0xFFC2185B)
val PinkHeaderGradientEnd = Color(0xFF4A148C)
val PinkTextWhite = Color(0xFFFFFFFF)
val PinkTextGray = Color(0xFFE0E0E0)
val PinkPlaylistListBackground = Color(0xFFE593AB)
val PinkTrackAvatarColor = Color(0xFFD1C4E9)
val PinkPlaylistCoverColor = Color(0xFF424242)
val PinkButtonColor = Color(0xFFA04060)
val PinkSalmonRed = Color(0xFFF28C8C)
val PinkBottomNavColor = Color(0xFF212121)
val PinkActiveIconColor = Color(0xFFE57373)

// ==================== OCEAN THEME (ALTERNATIVE) ====================
val OceanDarkBackground = Color(0xFF001A1A)
val OceanHeaderGradientStart = Color(0xFF006064)
val OceanHeaderGradientEnd = Color(0xFF004D40)
val OceanTextWhite = Color(0xFFFFFFFF)
val OceanTextGray = Color(0xFFB2DFDB)
val OceanPlaylistListBackground = Color(0xFF004D40)
val OceanTrackAvatarColor = Color(0xFF80CBC4)
val OceanPlaylistCoverColor = Color(0xFF00695C)
val OceanButtonColor = Color(0xFF00897B)
val OceanSalmonRed = Color(0xFF80CBC4)
val OceanBottomNavColor = Color(0xFF00251A)
val OceanActiveIconColor = Color(0xFF4DB6AC)

// ==================== СТАРЫЕ ИМЕНА (АЛИАСЫ ДЛЯ СОВМЕСТИМОСТИ) ====================
// Эти цвета используются в старых файлах - не удалять!
val DarkBackground = PinkDarkBackground
val HeaderGradientStart = PinkHeaderGradientStart
val HeaderGradientEnd = PinkHeaderGradientEnd
val TextWhite = PinkTextWhite
val TextGray = PinkTextGray
val PlaylistListBackground = PinkPlaylistListBackground
val TrackAvatarColor = PinkTrackAvatarColor
val PlaylistCoverColor = PinkPlaylistCoverColor
val ButtonColor = PinkButtonColor
val SalmonRed = PinkSalmonRed
val BottomNavColor = PinkBottomNavColor
val ActiveIconColor = PinkActiveIconColor
val CalmPink = Color(0xFFF8BBD0)
val TextBlack = Color(0xFF000000)

// ==================== THEME-AWARE COLORS ====================
object ThemeColors {

    fun darkBackground(playerState: MusicPlayerState): Color {
        return if (playerState.useOceanTheme) OceanDarkBackground else PinkDarkBackground
    }

    fun headerGradientStart(playerState: MusicPlayerState): Color {
        return if (playerState.useOceanTheme) OceanHeaderGradientStart else PinkHeaderGradientStart
    }

    fun headerGradientEnd(playerState: MusicPlayerState): Color {
        return if (playerState.useOceanTheme) OceanHeaderGradientEnd else PinkHeaderGradientEnd
    }

    fun textWhite(playerState: MusicPlayerState): Color {
        return if (playerState.useOceanTheme) OceanTextWhite else PinkTextWhite
    }

    fun textGray(playerState: MusicPlayerState): Color {
        return if (playerState.useOceanTheme) OceanTextGray else PinkTextGray
    }

    fun playlistListBackground(playerState: MusicPlayerState): Color {
        return if (playerState.useOceanTheme) OceanPlaylistListBackground else PinkPlaylistListBackground
    }

    fun trackAvatarColor(playerState: MusicPlayerState): Color {
        return if (playerState.useOceanTheme) OceanTrackAvatarColor else PinkTrackAvatarColor
    }

    fun playlistCoverColor(playerState: MusicPlayerState): Color {
        return if (playerState.useOceanTheme) OceanPlaylistCoverColor else PinkPlaylistCoverColor
    }

    fun buttonColor(playerState: MusicPlayerState): Color {
        return if (playerState.useOceanTheme) OceanButtonColor else PinkButtonColor
    }

    fun salmonRed(playerState: MusicPlayerState): Color {
        return if (playerState.useOceanTheme) OceanSalmonRed else PinkSalmonRed
    }

    fun bottomNavColor(playerState: MusicPlayerState): Color {
        return if (playerState.useOceanTheme) OceanBottomNavColor else PinkBottomNavColor
    }

    fun activeIconColor(playerState: MusicPlayerState): Color {
        return if (playerState.useOceanTheme) OceanActiveIconColor else PinkActiveIconColor
    }
}

// ==================== MOOD COLORS (PINK) ====================
val MoodVigorLight = Color(0xFF00FBFF)
val MoodVigorDeep = Color(0xFF008B8E)
val MoodDynamicLight = Color(0xFFFF0000)
val MoodDynamicDeep = Color(0xFF800000)
val MoodFreshnessLight = Color(0xFF00FF40)
val MoodFreshnessDeep = Color(0xFF00641A)
val MoodJoyLight = Color(0xFFFFD700)
val MoodJoyDeep = Color(0xFFB8860B)
val MoodSadnessLight = Color(0xFF9D00FF)
val MoodSadnessDeep = Color(0xFF310062)
val MoodPartyLight = Color(0xFFFF0080)
val MoodPartyDeep = Color(0xFF8B0046)

// ==================== MOOD COLORS (OCEAN) ====================
val OceanMoodVigorLight = Color(0xFF4DB6AC)
val OceanMoodVigorDeep = Color(0xFF00695C)
val OceanMoodDynamicLight = Color(0xFF80CBC4)
val OceanMoodDynamicDeep = Color(0xFF004D40)
val OceanMoodFreshnessLight = Color(0xFFB2DFDB)
val OceanMoodFreshnessDeep = Color(0xFF00695C)
val OceanMoodJoyLight = Color(0xFF80DEEA)
val OceanMoodJoyDeep = Color(0xFF00838F)
val OceanMoodSadnessLight = Color(0xFF4DD0E1)
val OceanMoodSadnessDeep = Color(0xFF006064)
val OceanMoodPartyLight = Color(0xFF26C6DA)
val OceanMoodPartyDeep = Color(0xFF00838F)