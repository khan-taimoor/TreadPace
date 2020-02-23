package dev.taimoor.treadpace.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RunDao {
    @Query("select * from RUN_TABLE order by datetime(run_date) desc")
    fun getRuns(): LiveData<List<RunEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(run: RunEntity)

    @Delete
    suspend fun delete(vararg run: RunEntity)

    @Query("delete from RUN_TABLE")
    suspend fun deleteAll()
}