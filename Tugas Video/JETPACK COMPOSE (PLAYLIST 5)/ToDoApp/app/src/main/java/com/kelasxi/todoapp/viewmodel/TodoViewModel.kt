package com.kelasxi.todoapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kelasxi.todoapp.data.TodoDatabase
import com.kelasxi.todoapp.data.TodoItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * ViewModel untuk mengelola data TodoItem
 * Menyediakan interface antara UI dan database
 * Menggunakan AndroidViewModel untuk akses ke Application context
 */
class TodoViewModel(application: Application) : AndroidViewModel(application) {
    
    // Database instance
    private val database = TodoDatabase.getDatabase(application)
    private val todoDao = database.todoDao()
    
    // Flow untuk mengobservasi perubahan data
    val allTodos: Flow<List<TodoItem>> = todoDao.getAllTodos()
    
    /**
     * Menambahkan todo baru ke database
     * Menggunakan viewModelScope untuk coroutine scope yang aman
     */
    fun addTodo(title: String) {
        if (title.isNotBlank()) {
            viewModelScope.launch {
                val newTodo = TodoItem(title = title.trim())
                todoDao.insertTodo(newTodo)
            }
        }
    }
    
    /**
     * Menghapus todo dari database
     */
    fun deleteTodo(todo: TodoItem) {
        viewModelScope.launch {
            todoDao.deleteTodo(todo)
        }
    }
}