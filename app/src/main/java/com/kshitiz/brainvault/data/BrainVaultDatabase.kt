package com.kshitiz.brainvault.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class BrainVaultDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile private var INSTANCE: BrainVaultDatabase? = null

        fun getDatabase(context: Context): BrainVaultDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    BrainVaultDatabase::class.java,
                    "brainvault_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}