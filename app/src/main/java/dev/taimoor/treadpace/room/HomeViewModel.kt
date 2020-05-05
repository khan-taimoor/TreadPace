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
        allRuns = repository.getRuns()

    }

    fun insert(run: RunEntity) = viewModelScope.launch {
        repository.insert(run)
    }

    //TODO: Add ability to delete multiple runs from home screen by highlighting
    fun delete(run: RunEntity) = viewModelScope.launch {
        repository.delete(run)
    }


}
