package com.example.Russify.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Russify.presentation.state.AppLanguage
import com.example.Russify.presentation.state.MusicPlayerState
import com.example.Russify.ui.theme.*

@Composable
fun SettingsScreen(
    playerState: MusicPlayerState,
    onLogout: () -> Unit,
    onChangeAccount: () -> Unit
) {
    val themeDarkBg = ThemeColors.darkBackground(playerState)
    val themeTextWhite = ThemeColors.textWhite(playerState)
    val themeTextGray = ThemeColors.textGray(playerState)
    val themeActiveIcon = ThemeColors.activeIconColor(playerState)
    val language = playerState.currentLanguage

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(themeDarkBg)
            .statusBarsPadding()
    ) {
        Text(
            text = if (language == AppLanguage.RU) "Настройки" else "Settings",
            color = themeTextWhite,
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(start = 24.dp, top = 40.dp, bottom = 24.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
        ) {
            SettingsOptionItem(
                icon = Icons.Default.Language,
                title = if (language == AppLanguage.RU) "Язык" else "Language",
                value = if (language == AppLanguage.RU) "Русский" else "English",
                onClick = {
                    playerState.currentLanguage = if (language == AppLanguage.RU) AppLanguage.EN else AppLanguage.RU
                },
                iconTint = themeActiveIcon,
                titleColor = themeTextWhite,
                valueColor = themeTextGray
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = Color.White.copy(alpha = 0.1f)
            )

            SettingsOptionItem(
                icon = Icons.Default.Palette,
                title = if (language == AppLanguage.RU) "Тема" else "Theme",
                value = if (playerState.useOceanTheme) "Ocean" else "Pink",
                onClick = {
                    playerState.useOceanTheme = !playerState.useOceanTheme
                },
                iconTint = themeActiveIcon,
                titleColor = themeTextWhite,
                valueColor = themeTextGray
            )
        }

        Spacer(modifier = Modifier.weight(1f))


        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(56.dp)
                .clickable { onChangeAccount() }
                .border(2.dp, Color.Blue.copy(alpha = 0.7f), RoundedCornerShape(16.dp)),
            color = Color.Blue.copy(alpha = 0.15f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (language == AppLanguage.RU) "СМЕНИТЬ АККАУНТ" else "CHANGE ACCOUNT",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(56.dp)
                .clickable { onLogout() }
                .border(2.dp, Color.Red.copy(alpha = 0.7f), RoundedCornerShape(16.dp)),
            color = Color.Red.copy(alpha = 0.15f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (language == AppLanguage.RU) "ВЫЙТИ ИЗ АККАУНТА" else "LOGOUT",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun SettingsOptionItem(
    icon: ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit,
    iconTint: Color,
    titleColor: Color,
    valueColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                color = titleColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                color = valueColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.3f),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}