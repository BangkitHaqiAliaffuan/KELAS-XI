package com.trashbin.app

import android.app.Application
import com.trashbin.app.data.api.TokenManager

class TrashBinApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        TokenManager.initialize(this)
    }
}