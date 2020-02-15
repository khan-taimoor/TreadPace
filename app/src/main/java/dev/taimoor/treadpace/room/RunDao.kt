package dev.taimoor.treadpace.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RunDao {
    @Query("select * from RUN_TABLE order by datetime(run_date)")
    fun getRuns(): LiveData<List<RunEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(run: RunEntity)

    @Query("delete from RUN_TABLE")
    suspend fun deleteAll()
}