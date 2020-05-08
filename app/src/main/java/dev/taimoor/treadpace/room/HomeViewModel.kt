package dev.taimoor.treadpace.room

import androidx.lifecycle.*
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: RunRepository) : ViewModel() {

    val allRuns: LiveData<List<RunEntity>>

    init {
//        val runDao = RunRoomDatabase.getDatabase(application, viewModelScope).runDao()
//        repository = RunRepository(runDao)
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

@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory (
    private val runRepository: RunRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (HomeViewModel(runRepository) as T)
}
