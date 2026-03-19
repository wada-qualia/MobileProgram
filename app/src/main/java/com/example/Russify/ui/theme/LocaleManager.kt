package com.example.Russify.ui.theme

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import com.example.Russify.presentation.state.AppLanguage
import java.util.Locale

object LocaleManager {
    fun updateContext(context: Context, language: AppLanguage): Context {
        val locale = when (language) {
            AppLanguage.RU -> Locale("ru")
            AppLanguage.EN -> Locale("en")
        }
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            context
        }
    }
}