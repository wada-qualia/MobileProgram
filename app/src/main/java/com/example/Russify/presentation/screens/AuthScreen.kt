package com.example.Russify.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Russify.presentation.state.AppLanguage
import com.example.Russify.presentation.state.MusicPlayerState
import com.example.Russify.ui.theme.*

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    playerState: MusicPlayerState? = null
) {
    val language = playerState?.currentLanguage ?: AppLanguage.RU
    var isLoginMode by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(HeaderGradientStart, DarkBackground)))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isLoginMode) {
                if (language == AppLanguage.RU) "ВХОД" else "LOGIN"
            } else {
                if (language == AppLanguage.RU) "РЕГИСТРАЦИЯ" else "REGISTRATION"
            },
            fontSize = 42.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(40.dp))

        if (!isLoginMode) {
            AuthTextField(
                value = username,
                onValueChange = { username = it },
                label = if (language == AppLanguage.RU) "Имя пользователя" else "Username",
                icon = Icons.Default.Person,
                language = language
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        AuthTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            icon = Icons.Default.Email,
            language = language
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField(
            value = password,
            onValueChange = { password = it },
            label = if (language == AppLanguage.RU) "Пароль" else "Password",
            icon = Icons.Default.Lock,
            isPassword = true,
            language = language
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onAuthSuccess,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .border(2.dp, Color.White, RoundedCornerShape(16.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = if (isLoginMode) {
                    if (language == AppLanguage.RU) "ВОЙТИ" else "SIGN IN"
                } else {
                    if (language == AppLanguage.RU) "СОЗДАТЬ АККАУНТ" else "CREATE ACCOUNT"
                },
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (isLoginMode) {
                if (language == AppLanguage.RU) "Ещё нет аккаунта? Зарегистрироваться" else "No account yet? Register"
            } else {
                if (language == AppLanguage.RU) "Уже есть аккаунт? Войти" else "Already have an account? Sign In"
            },
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.clickable { isLoginMode = !isLoginMode },
            fontSize = 14.sp
        )
    }
}

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isPassword: Boolean = false,
    language: AppLanguage = AppLanguage.RU
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label, color = Color.White.copy(alpha = 0.6f)) },
        leadingIcon = { Icon(icon, null, tint = Color.White) },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color.White
        )
    )
}