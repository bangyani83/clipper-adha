package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ProjectEntity::class, ClipEntity::class, ScheduledPostEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ClipperDatabase : RoomDatabase() {
    abstract fun clipperDao(): ClipperDao

    companion object {
        @Volatile
        private var INSTANCE: ClipperDatabase? = null

        fun getInstance(context: Context): ClipperDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ClipperDatabase::class.java,
                    "clipper_adha_local.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
