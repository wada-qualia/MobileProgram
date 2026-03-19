package com.example.Russify.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.window.Dialog
import com.example.Russify.model.Playlist
import com.example.Russify.presentation.state.AppLanguage
import com.example.Russify.ui.theme.*

@Composable
fun StyledDialogBackground(
    title: String,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.verticalGradient(colors = listOf(HeaderGradientStart, DarkBackground)))
                .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title.uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                content()
            }
        }
    }
}

@Composable
fun CreatePlaylistDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit,
    language: AppLanguage = AppLanguage.RU
) {
    var text by remember { mutableStateOf("") }

    StyledDialogBackground(
        title = if (language == AppLanguage.RU) "Новый плейлист" else "New Playlist",
        onDismiss = onDismiss
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = {
                Text(
                    if (language == AppLanguage.RU) "Название" else "Name",
                    color = Color.White.copy(alpha = 0.7f)
                )
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = SalmonRed,
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                cursorColor = SalmonRed
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { if (text.isNotBlank()) onCreate(text) },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .border(1.dp, Color.White, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (language == AppLanguage.RU) "СОЗДАТЬ" else "CREATE",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AddToPlaylistDialog(
    playlists: List<Playlist>,
    onDismiss: () -> Unit,
    onSelect: (Playlist) -> Unit,
    language: AppLanguage = AppLanguage.RU
) {
    StyledDialogBackground(
        title = if (language == AppLanguage.RU) "Добавить в плейлист" else "Add to Playlist",
        onDismiss = onDismiss
    ) {
        LazyColumn(
            modifier = Modifier.heightIn(max = 300.dp)
        ) {
            items(playlists) { playlist ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(playlist) }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(playlist.title, fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        "${playlist.tracks.size} ${if (language == AppLanguage.RU) "треков" else "tracks"}",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onDismiss) {
            Text(
                text = if (language == AppLanguage.RU) "Отмена" else "Cancel",
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}