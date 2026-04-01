package com.kelasxi.myapplication.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * AccessibleRatingStars - Komponen bintang rating ramah aksesibilitas.
 * Menggunakan selectableGroup + Role.RadioButton agar Screen Reader
 * membaca setiap bintang sebagai pilihan terpisah (e.g. "Beri 3 bintang").
 * Touch target minimum 48dp terpenuhi via padding + size.
 */
@Composable
fun AccessibleRatingStars(
    rating: Int,
    onRatingChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier.selectableGroup(), // Aksesibilitas: tandai sebagai kelompok pilihan
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            val isSelected = i <= rating
            Icon(
                imageVector = if (isSelected) Icons.Filled.Star else Icons.Outlined.StarBorder,
                contentDescription = null, // Dideskripsikan oleh parent semantics di modifier
                tint = if (isSelected) Color(0xFFFFCC00) else Color(0xFFBDBDBD),
                modifier = Modifier
                    .size(48.dp) // Minimum touch target 48dp untuk aksesibilitas
                    .selectable(
                        selected = (i == rating),
                        onClick = { if (enabled) onRatingChange(i) },
                        enabled = enabled,
                        role = Role.RadioButton // Screen reader tahu ini opsi untuk dipilih
                    )
                    .semantics {
                        // Deskripsi eksplisit untuk setiap bintang
                        contentDescription = "Beri $i bintang${if (i == rating) " (dipilih)" else ""}"
                    }
                    .padding(4.dp)
            )
        }
    }
}
