package com.kelasxi.todoapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class yang merepresentasikan sebuah item ToDo dalam database
 * Menggunakan Room annotations untuk definisi tabel database
 */
@Entity(tableName = "todo_items")
data class TodoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    val title: String,
    
    val createdDate: Long = System.currentTimeMillis()
)