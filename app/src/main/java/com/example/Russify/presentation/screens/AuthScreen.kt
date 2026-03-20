package com.example.Russify.presentation.screens

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Russify.data.local.TokenManager
import com.example.Russify.data.repository.AuthRepository
import com.example.Russify.presentation.state.AppLanguage
import com.example.Russify.presentation.state.MusicPlayerState
import com.example.Russify.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    playerState: MusicPlayerState? = null
) {
    val context = LocalContext.current
    val language = playerState?.currentLanguage ?: AppLanguage.RU
    val coroutineScope = rememberCoroutineScope()

    // Инициализация AuthRepository
    val tokenManager = remember { TokenManager.getInstance(context) }
    val authRepository = remember { AuthRepository(tokenManager) }

    var isLoginMode by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Проверка валидации
    val isValidEmail = email.contains("@") && email.length >= 3
    val isValidPassword = password.length >= 8
    val isValidUsername = username.length >= 3

    val canSubmit = if (isLoginMode) {
        isValidEmail && isValidPassword
    } else {
        isValidEmail && isValidPassword && isValidUsername
    }

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
                onValueChange = {
                    username = it
                    errorMessage = null
                },
                label = if (language == AppLanguage.RU) "Имя пользователя" else "Username",
                icon = Icons.Default.Person,
                language = language,
                isError = username.isNotEmpty() && !isValidUsername,
                enabled = !isLoading
            )
            if (username.isNotEmpty() && !isValidUsername) {
                Text(
                    text = if (language == AppLanguage.RU) "Минимум 3 символа" else "Minimum 3 characters",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        AuthTextField(
            value = email,
            onValueChange = {
                email = it
                errorMessage = null
            },
            label = "Email",
            icon = Icons.Default.Email,
            language = language,
            isError = email.isNotEmpty() && !isValidEmail,
            enabled = !isLoading
        )
        if (email.isNotEmpty() && !isValidEmail) {
            Text(
                text = if (language == AppLanguage.RU) "Неверный email" else "Invalid email",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = null
            },
            label = if (language == AppLanguage.RU) "Пароль" else "Password",
            icon = Icons.Default.Lock,
            isPassword = true,
            language = language,
            isError = password.isNotEmpty() && !isValidPassword,
            enabled = !isLoading
        )
        if (password.isNotEmpty() && !isValidPassword) {
            Text(
                text = if (language == AppLanguage.RU) "Минимум 8 символов" else "Minimum 8 characters",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Показать ошибку если есть
        errorMessage?.let { error ->
            Text(
                text = error,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = {
                coroutineScope.launch {
                    isLoading = true
                    errorMessage = null

                    val result = if (isLoginMode) {
                        authRepository.login(email, password)
                    } else {
                        authRepository.register(username, email, password)
                    }

                    result.onSuccess {
                        isLoading = false
                        onAuthSuccess()
                    }.onFailure { e ->
                        isLoading = false
                        errorMessage = when {
                            e.message?.contains("401") == true ->
                                if (language == AppLanguage.RU) "Неверный email или пароль" else "Invalid email or password"
                            e.message?.contains("409") == true ->
                                if (language == AppLanguage.RU) "Пользователь уже существует" else "User already exists"
                            e.message?.contains("timeout") == true ->
                                if (language == AppLanguage.RU) "Время ожидания истекло" else "Request timeout"
                            else ->
                                if (language == AppLanguage.RU) "Ошибка подключения к серверу" else "Server connection error"
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .border(2.dp, Color.White, RoundedCornerShape(16.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White.copy(alpha = 0.15f)
            ),
            shape = RoundedCornerShape(16.dp),
            enabled = canSubmit && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
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
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (isLoginMode) {
                if (language == AppLanguage.RU) "Ещё нет аккаунта? Зарегистрироваться" else "No account yet? Register"
            } else {
                if (language == AppLanguage.RU) "Уже есть аккаунт? Войти" else "Already have an account? Sign In"
            },
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.clickable {
                if (!isLoading) {
                    isLoginMode = !isLoginMode
                    errorMessage = null
                }
            },
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
    language: AppLanguage = AppLanguage.RU,
    isError: Boolean = false,
    enabled: Boolean = true
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
        isError = isError,
        enabled = enabled,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (isError) Color.Red else Color.White,
            unfocusedBorderColor = if (isError) Color.Red else Color.White.copy(alpha = 0.3f),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            disabledTextColor = Color.White.copy(alpha = 0.5f),
            disabledBorderColor = Color.White.copy(alpha = 0.3f),
            cursorColor = Color.White
        )
    )
}