package com.kelasxi.myapplication.util

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

/**
 * Manages app language preference using SharedPreferences.
 * Supports Indonesian ("id") and English ("en").
 */
object LanguageManager {

    private const val PREFS_NAME = "lang_prefs"
    private const val KEY_LANGUAGE = "selected_language"
    const val LANG_ID = "id"
    const val LANG_EN = "en"

    /** Returns the currently saved language code, defaults to "id". */
    fun getSavedLanguage(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LANGUAGE, LANG_ID) ?: LANG_ID
    }

    /** Persists the chosen language code. */
    fun saveLanguage(context: Context, langCode: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LANGUAGE, langCode)
            .apply()
    }

    /**
     * Wraps [base] context with the saved locale so that string resources
     * are resolved in the correct language.
     */
    fun applyLocale(base: Context): Context {
        val langCode = getSavedLanguage(base)
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val config = Configuration(base.resources.configuration)
        config.setLocale(locale)
        return base.createConfigurationContext(config)
    }

    /** Convenience: apply a specific locale (without persisting). */
    fun applyLocaleImmediate(context: Context, langCode: String): Context {
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}
