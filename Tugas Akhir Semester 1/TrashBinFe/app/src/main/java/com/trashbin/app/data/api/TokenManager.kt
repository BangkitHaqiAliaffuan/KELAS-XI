package com.trashbin.app.data.api

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.trashbin.app.data.model.User

class TokenManager private constructor(context: Context) {
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("trashbin_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    fun saveToken(token: String) {
        sharedPreferences.edit().putString("auth_token", token).apply()
    }
    
    fun getToken(): String? {
        return sharedPreferences.getString("auth_token", null)
    }
    
    fun saveUser(user: User) {
        val userJson = gson.toJson(user)
        sharedPreferences.edit().putString("user_data", userJson).apply()
    }
    
    fun getUser(): User? {
        val userJson = sharedPreferences.getString("user_data", null)
        return if (!userJson.isNullOrEmpty()) {
            gson.fromJson(userJson, User::class.java)
        } else {
            null
        }
    }
    
    fun isLoggedIn(): Boolean {
        return !getToken().isNullOrEmpty()
    }
    
    fun clearToken() {
        sharedPreferences.edit().remove("auth_token").apply()
        sharedPreferences.edit().remove("user_data").apply()
    }
    
    companion object {
        @Volatile
        private var INSTANCE: TokenManager? = null
        
        fun initialize(context: Context) {
            INSTANCE = TokenManager(context.applicationContext)
            RetrofitClient.initialize(context.applicationContext)
        }
        
        fun getInstance(): TokenManager {
            return INSTANCE ?: throw IllegalStateException("TokenManager must be initialized first")
        }
        
        fun getToken(): String? = INSTANCE?.getToken()
        fun isLoggedIn(): Boolean = INSTANCE?.isLoggedIn() == true
        fun clearToken() = INSTANCE?.clearToken()
    }
}