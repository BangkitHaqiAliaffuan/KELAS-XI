package com.kelasxi.myapplication.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * FormInlineError - Pesan error inline yang muncul dengan animasi.
 * Digunakan pada field form yang membutuhkan validasi real-time,
 * bukan hanya saat submit. Mudah diakses screen reader.
 */
@Composable
fun FormInlineError(
    isError: Boolean,
    errorMessage: String,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isError,
        enter = fadeIn(tween(200)) + slideInVertically(tween(200)) { -it / 2 },
        exit  = fadeOut(tween(150)) + slideOutVertically(tween(150)) { -it / 2 }
    ) {
        Text(
            text = "⚠ $errorMessage",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = modifier
                .padding(start = 16.dp, top = 4.dp, bottom = 2.dp)
                .semantics { contentDescription = "Error validasi: $errorMessage" }
        )
    }
}
