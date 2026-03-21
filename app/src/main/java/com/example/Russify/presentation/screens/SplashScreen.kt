package com.example.Russify.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.Russify.R
import kotlinx.coroutines.delay

@Composable
fun FullSplashScreen(onFinished: () -> Unit) {

    Image(
        painter = painterResource(id = R.drawable.splash_logo),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )


    LaunchedEffect(Unit) {
        delay(1000)
        onFinished()
    }
}