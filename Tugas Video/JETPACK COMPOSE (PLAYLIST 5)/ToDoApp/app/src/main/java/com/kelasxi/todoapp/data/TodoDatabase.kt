package com.kelasxi.todoapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room Database class untuk aplikasi TodoApp
 * Menyediakan akses ke database dan DAO
 */
@Database(
    entities = [TodoItem::class],
    version = 1,
    exportSchema = false
)
abstract class TodoDatabase : RoomDatabase() {
    
    /**
     * Abstract method untuk mengakses TodoDao
     */
    abstract fun todoDao(): TodoDao
    
    companion object {
        // Singleton instance dari database
        @Volatile
        private var INSTANCE: TodoDatabase? = null
        
        /**
         * Mendapatkan instance database dengan pattern Singleton
         * Thread-safe menggunakan synchronized block
         */
        fun getDatabase(context: Context): TodoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TodoDatabase::class.java,
                    "todo_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}