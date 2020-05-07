package dev.taimoor.treadpace

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import dev.taimoor.treadpace.room.RunDao
import dev.taimoor.treadpace.room.RunRepository
import dev.taimoor.treadpace.room.RunRoomDatabase
import kotlinx.coroutines.runBlocking

object ServiceLocator {
    private var database: RunRoomDatabase? = null
    @Volatile
    var runRepository: RunRepository? = null
        @VisibleForTesting set


    private val lock = Any()

    @VisibleForTesting
    fun resetRepository() {
        synchronized(lock) {
            runBlocking {
                //fAK.deleteAllTasks()
            }
            // Clear all data to avoid test pollution.
            database?.apply {
                clearAllTables()
                close()
            }
            database = null
            runRepository = null
        }
    }


    fun provideRunRepository(context: Context): RunRepository {
        synchronized(this) {
            return runRepository ?: createRunRepository(context)
        }
    }

    private fun createRunRepository(context: Context): RunRepository {
        val newRepo = RunRepository(createRunDataSource(context))
        runRepository = newRepo
        return newRepo
    }

    private fun createRunDataSource(context: Context): RunDao {
        val database = database ?: createDataBase(context)
        return database.runDao()
    }

    private fun createDataBase(context: Context): RunRoomDatabase {
        val result = Room.databaseBuilder(
            context.applicationContext,
            RunRoomDatabase::class.java, "treadmill_database"
        ).build()
        database = result
        return result
    }
}