package com.example.demosih11.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Lot::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lotDao(): LotDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ewaste_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}