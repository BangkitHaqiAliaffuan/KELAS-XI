package com.kelasxi.myapplication

import android.app.Application
import android.content.Context
import com.kelasxi.myapplication.util.LanguageManager

/**
 * Custom Application class that applies the saved locale before any
 * Activity is created, so all string resources resolve correctly.
 */
class TrashCareApplication : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LanguageManager.applyLocale(base))
    }
}
