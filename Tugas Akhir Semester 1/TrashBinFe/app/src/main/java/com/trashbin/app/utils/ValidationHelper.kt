package com.trashbin.app.utils

import android.util.Patterns
import com.google.android.material.textfield.TextInputEditText

object ValidationHelper {
    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPhone(phone: String): Boolean {
        // Indonesia phone format: 08xxxxxxxxx
        val pattern = Regex("^08\\d{8,11}$")
        return pattern.matches(phone)
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    fun validateRequired(editText: TextInputEditText, errorMessage: String): Boolean {
        val text = editText.text.toString().trim()
        return if (text.isEmpty()) {
            editText.error = errorMessage
            false
        } else {
            editText.error = null
            true
        }
    }

    fun validateEmail(editText: TextInputEditText): Boolean {
        val email = editText.text.toString().trim()
        return if (!isValidEmail(email)) {
            editText.error = "Email tidak valid"
            false
        } else {
            editText.error = null
            true
        }
    }

    fun validatePhone(editText: TextInputEditText): Boolean {
        val phone = editText.text.toString().trim()
        return if (!isValidPhone(phone)) {
            editText.error = "Nomor telepon tidak valid (format: 08xxxxxxxxx)"
            false
        } else {
            editText.error = null
            true
        }
    }
}