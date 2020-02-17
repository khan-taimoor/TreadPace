package dev.taimoor.treadpace.room

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: RunRepository

    val allRuns: LiveData<List<RunEntity>>

    init {
        val runDao = RunRoomDatabase.getDatabase(application, viewModelScope).runDao()
        repository = RunRepository(runDao)
        allRuns = repository.allRuns
    }

    fun insert(run: RunEntity) = viewModelScope.launch {
        repository.insert(run)
    }


}
