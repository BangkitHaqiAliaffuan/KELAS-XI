package com.kelas.balancebook.data.local

import android.content.Context
import com.google.gson.Gson
import com.kelas.balancebook.data.remote.UserDto

object SessionManager {
    private const val PREF_NAME = "balancebook_session"
    private const val KEY_TOKEN = "token"
    private const val KEY_USER = "user"

    private val gson = Gson()

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveSession(context: Context, token: String, user: UserDto) {
        prefs(context).edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_USER, gson.toJson(user))
            .apply()
    }

    fun saveUser(context: Context, user: UserDto) {
        prefs(context).edit()
            .putString(KEY_USER, gson.toJson(user))
            .apply()
    }

    fun getToken(context: Context): String? = prefs(context).getString(KEY_TOKEN, null)

    fun getUser(context: Context): UserDto? {
        val json = prefs(context).getString(KEY_USER, null) ?: return null
        return runCatching { gson.fromJson(json, UserDto::class.java) }.getOrNull()
    }

    fun isLoggedIn(context: Context): Boolean = !getToken(context).isNullOrBlank()

    fun clear(context: Context) {
        prefs(context).edit().clear().apply()
    }
}
