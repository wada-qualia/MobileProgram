package com.example.Russify.presentation.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Russify.presentation.state.MusicPlayerState
import com.example.Russify.ui.theme.*

@Composable
fun MiniPlayer(
    playerState: MusicPlayerState
) {
    val track = playerState.currentTrack ?: return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp) // Чуть выше для пространства
            .clickable { playerState.isPlayerExpanded = true }
            .background(Color(0xFF4A2C5A))
    ) {
        LinearProgressIndicator(
            progress = { 0.4f },
            modifier = Modifier.fillMaxWidth().height(2.dp),
            color = SalmonRed,
            trackColor = Color.White.copy(alpha = 0.2f)
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(track.coverColor)
            )

            Spacer(modifier = Modifier.width(16.dp))


            Column(modifier = Modifier.weight(1f)) {
                AnimatedContent(
                    targetState = track,
                    transitionSpec = {
                        (slideInVertically { height -> height } + fadeIn()).togetherWith(slideOutVertically { height -> -height } + fadeOut())
                    },
                    label = "TrackTitleAnim"
                ) { targetTrack ->
                    Column {
                        Text(
                            text = targetTrack.title,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = targetTrack.artist,
                            color = TextGray,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }


            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp) // Увеличенное расстояние
            ) {

                IconButton(
                    onClick = { playerState.togglePlayPause() },
                    modifier = Modifier
                        .size(40.dp)
                        .border(1.dp, Color.White.copy(alpha = 0.8f), CircleShape)
                ) {
                    Icon(
                        if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }


                IconButton(
                    onClick = { playerState.skipToNext() },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.SkipNext,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }


                IconButton(
                    onClick = { playerState.closeMiniPlayer() },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        null,
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}