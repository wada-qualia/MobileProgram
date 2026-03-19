package com.example.Russify.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
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
import com.example.Russify.model.Track
import com.example.Russify.presentation.state.AppLanguage
import com.example.Russify.presentation.state.MusicPlayerState
import com.example.Russify.ui.theme.*

@Composable
fun TrackRowItem(
    track: Track,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onAddToQueue: () -> Unit,
    onPlayNext: () -> Unit,
    language: AppLanguage = AppLanguage.RU,
    playerState: MusicPlayerState? = null
) {
    var showMenu by remember { mutableStateOf(false) }

    val titleColor = if (playerState?.useOceanTheme == true) Color.White else Color.Black
    val artistColor = if (playerState?.useOceanTheme == true) Color.White.copy(alpha = 0.8f) else Color.Black.copy(alpha = 0.7f)
    val durationColor = if (playerState?.useOceanTheme == true) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f)
    val likeIconColor = if (playerState?.useOceanTheme == true) Color.White else Color.Black

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AnimatedContent(
            targetState = track.coverColor,
            transitionSpec = {
                fadeIn(animationSpec = androidx.compose.animation.core.tween(300)) togetherWith
                        fadeOut(animationSpec = androidx.compose.animation.core.tween(300))
            },
            label = "CoverAnim"
        ) { color ->
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {

            AnimatedContent(
                targetState = track.title,
                transitionSpec = {
                    slideInVertically(initialOffsetY = { it }) + fadeIn() togetherWith
                            slideOutVertically(targetOffsetY = { -it }) + fadeOut()
                },
                label = "TitleAnim"
            ) { title ->
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor,
                    maxLines = 1
                )
            }


            AnimatedContent(
                targetState = track.artist,
                transitionSpec = {
                    slideInVertically(initialOffsetY = { it }) + fadeIn() togetherWith
                            slideOutVertically(targetOffsetY = { -it }) + fadeOut()
                },
                label = "ArtistAnim"
            ) { artist ->
                Text(
                    text = artist,
                    fontSize = 14.sp,
                    color = artistColor,
                    maxLines = 1
                )
            }
        }


        AnimatedContent(
            targetState = track.durationFormatted,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "DurationAnim"
        ) { duration ->
            Text(
                text = duration,
                fontSize = 12.sp,
                color = durationColor,
                modifier = Modifier.padding(end = 8.dp)
            )
        }


        AnimatedContent(
            targetState = track.isFavorite,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "LikeAnim"
        ) { isFavorite ->
            IconButton(onClick = onToggleFavorite, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (isFavorite) Color.Red else likeIconColor
                )
            }
        }

        Spacer(modifier = Modifier.width(4.dp))

        Box {
            IconButton(onClick = { showMenu = true }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = likeIconColor)
            }

            MaterialTheme(shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(12.dp))) {
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier
                        .background(Brush.verticalGradient(listOf(HeaderGradientStart, DarkBackground)))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = if (language == AppLanguage.RU) "Добавить в плейлист" else "Add to Playlist",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        onClick = {
                            showMenu = false
                            onAddToPlaylist()
                        }
                    )

                    HorizontalDivider(color = Color.White.copy(alpha = 0.2f))

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = if (language == AppLanguage.RU) "Играть следующим" else "Play Next",
                                color = Color.White
                            )
                        },
                        onClick = {
                            showMenu = false
                            onPlayNext()
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = if (language == AppLanguage.RU) "Добавить в очередь" else "Add to Queue",
                                color = Color.White
                            )
                        },
                        onClick = {
                            showMenu = false
                            onAddToQueue()
                        }
                    )
                }
            }
        }
    }
}