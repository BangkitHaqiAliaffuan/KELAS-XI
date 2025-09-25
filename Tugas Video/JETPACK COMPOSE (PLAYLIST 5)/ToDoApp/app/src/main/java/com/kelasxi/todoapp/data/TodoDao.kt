package com.kelasxi.todoapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) untuk operasi database TodoItem
 * Menyediakan method untuk insert, delete, dan query data
 */
@Dao
interface TodoDao {
    
    /**
     * Mengambil semua todo items dari database
     * Diurutkan berdasarkan tanggal dibuat (terbaru di atas)
     * Menggunakan Flow untuk reactive programming
     */
    @Query("SELECT * FROM todo_items ORDER BY createdDate DESC")
    fun getAllTodos(): Flow<List<TodoItem>>
    
    /**
     * Menambahkan todo item baru ke database
     */
    @Insert
    suspend fun insertTodo(todo: TodoItem)
    
    /**
     * Menghapus todo item dari database
     */
    @Delete
    suspend fun deleteTodo(todo: TodoItem)
}