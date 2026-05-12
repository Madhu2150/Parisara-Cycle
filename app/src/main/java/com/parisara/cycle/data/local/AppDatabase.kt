package com.parisara.cycle.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(
    entities = [EcoStatsEntity::class],
    version  = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ecoStatsDao(): EcoStatsDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "parisara_db"
                ).fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}