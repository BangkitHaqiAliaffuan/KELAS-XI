package com.kelasxi.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelasxi.todoapp.screens.TodoScreen
import com.kelasxi.todoapp.ui.theme.ToDoAppTheme
import com.kelasxi.todoapp.viewmodel.TodoViewModel

/**
 * MainActivity - Entry point aplikasi ToDo
 * Menggunakan Jetpack Compose untuk UI dan setup ViewModel
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToDoAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Setup ViewModel menggunakan viewModel() composable
                    val todoViewModel: TodoViewModel = viewModel()
                    
                    // Menampilkan TodoScreen dengan padding dari system bars
                    TodoScreen(
                        viewModel = todoViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}