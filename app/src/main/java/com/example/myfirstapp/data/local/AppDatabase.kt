package com.example.myfirstapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [RegistroEntity::class],
    version = 2,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun registroDao(): RegistroDao
}