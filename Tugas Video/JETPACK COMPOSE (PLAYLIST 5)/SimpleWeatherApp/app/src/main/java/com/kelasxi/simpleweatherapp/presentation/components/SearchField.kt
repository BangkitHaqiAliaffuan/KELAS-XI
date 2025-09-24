package com.kelasxi.simpleweatherapp.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

/**
 * Composable function untuk search field
 * @param query current search query
 * @param onQueryChange callback saat query berubah
 * @param onSearch callback saat tombol search ditekan
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            label = { Text("Search for any location", color = Color.White.copy(alpha = 0.8f)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearch(query)
                    keyboardController?.hide()
                }
            ),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.White.copy(alpha = 0.8f),
                unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White.copy(alpha = 0.9f),
                cursorColor = Color.White
            ),
            modifier = Modifier
                .weight(1f)
                .shadow(4.dp, RoundedCornerShape(16.dp))
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        IconButton(
            onClick = {
                onSearch(query)
                keyboardController?.hide()
            },
            modifier = Modifier
                .shadow(4.dp, RoundedCornerShape(12.dp))
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}