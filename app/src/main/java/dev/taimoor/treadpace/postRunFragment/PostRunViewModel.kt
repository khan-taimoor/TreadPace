package dev.taimoor.treadpace.postRunFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import dev.taimoor.treadpace.Split

class PostRunViewModel : ViewModel() {


    val timesOnTreadmill = MutableLiveData(Pair(0,2))
    val timesOnTreadmillText : LiveData<String> = Transformations.map(timesOnTreadmill){
        "${it.first} / ${it.second}"
    }

    val distance = MutableLiveData(0)
    val distanceText : LiveData<String> = Transformations.map(distance){
        "$it"
    }

    val pace = MutableLiveData(0.0)
    val paceText : LiveData<String> = Transformations.map(pace){
        "%.2f".format(it)
    }

    val time = MutableLiveData(0)
    val timeText : LiveData<String> = Transformations.map(time){
        "$it"
    }

    val split = MutableLiveData(Pair(-1, 0.0))
    val splitText : LiveData<String> = Transformations.map(split){
        if (it.first == -1){
            "Slide to show run info"
        }
        else{
            "Split: ${it.first} Pace: ${"%.2f".format(it.second)}"
        }
    }

    var savingRun = MutableLiveData(false)
}


