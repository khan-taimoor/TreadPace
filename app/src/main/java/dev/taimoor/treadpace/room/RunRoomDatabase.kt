package dev.taimoor.treadpace.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope

@Database(entities = [RunEntity::class], version = 1, exportSchema = false)

abstract class RunRoomDatabase : RoomDatabase() {

    abstract fun runDao(): RunDao

//    companion object {
//        @Volatile
//
//        private var INSTANCE: RunRoomDatabase? = null
//
//        fun getDatabase(context: Context, scope: CoroutineScope): RunRoomDatabase {
//            val tempInstance = INSTANCE
//            if (tempInstance != null) {
//                return tempInstance
//            }
//            synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    RunRoomDatabase::class.java,
//                    "treadmill_database"
//                ).build()
//                INSTANCE = instance
//                return instance
//            }
//        }
//    }

}