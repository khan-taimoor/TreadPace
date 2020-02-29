package dev.taimoor.treadpace.postRunFragment

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import dev.taimoor.treadpace.Split
import dev.taimoor.treadpace.Util
import dev.taimoor.treadpace.settings.UnitSetting




class PostRunViewModel(val unitSetting: UnitSetting) : ViewModel() {

//    val pref = PreferenceManager.getDefaultSharedPreferences().getString("units",  "mi")
//    val unitSetting : UnitSetting = UnitSetting.valueOf(pref as String)




    val timesOnTreadmill = MutableLiveData(Pair(0,2))
    val timesOnTreadmillText : LiveData<String> = Transformations.map(timesOnTreadmill){
        "${it.first} / ${it.second}"
    }

    val distance = MutableLiveData(0)
    val distanceText : LiveData<String> = Transformations.map(distance){
        "${ "%.2f".format((it*unitSetting.conversion))} ${unitSetting.name}"
    }

    val pace = MutableLiveData(0.0)
    val paceText : LiveData<String> = Transformations.map(pace){
        "${ "%.2f".format(it * unitSetting.conversion)} ${unitSetting.name}"
    }

    val time = MutableLiveData(0)
    val timeText : LiveData<String> = Transformations.map(time){
        "${it/60}:${it%60}"
    }

    val split = MutableLiveData(Pair(-1, 0.0))
    val splitText : LiveData<String> = Transformations.map(split){
        if (it.first == -1){
            "Slide to show split info"
        }
        else{
            "Split: ${it.first} Pace: ${"%.2f".format(it.second * unitSetting.conversion)} ${unitSetting.name}"
        }
    }

    val savingRun = MutableLiveData(false)
}


