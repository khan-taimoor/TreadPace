package dev.taimoor.treadpace.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations

class FakeAndroidTestRepository(val runs: MutableList<RunEntity> = mutableListOf()) : RunDao {
    override fun getRuns(): LiveData<List<RunEntity>> {
        val lst = MutableLiveData(runs)
        val live_lst = Transformations.map(lst){
            runs.toList()
        }
        return live_lst
    }

    override suspend fun insert(run: RunEntity) {
        runs.add(run)
    }

    override suspend fun delete(vararg run: RunEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAll() {
        TODO("Not yet implemented")
    }
}