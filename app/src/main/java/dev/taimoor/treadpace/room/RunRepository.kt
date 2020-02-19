package dev.taimoor.treadpace.room

import androidx.lifecycle.LiveData

class RunRepository(private val runDao: RunDao) {

    val allRuns: LiveData<List<RunEntity>> = runDao.getRuns()

    suspend fun insert(run: RunEntity){
        runDao.insert(run)
    }

    suspend fun delete(run: RunEntity){
        runDao.delete(run)
    }
}