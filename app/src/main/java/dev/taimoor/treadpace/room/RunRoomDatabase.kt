package dev.taimoor.treadpace.room

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

abstract class RunRoomDatabase : RoomDatabase() {

    abstract fun runDao(): RunDao

    companion object {
        @Volatile

        private var INSTANCE: RunRoomDatabase? = null

        fun getDatabase(context: Context): RunRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RunRoomDatabase::class.java,
                    "run_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

}