package dev.taimoor.treadpace.data

import android.util.Log
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import dev.taimoor.treadpace.R
import dev.taimoor.treadpace.Split
import dev.taimoor.treadpace.Util
import kotlinx.android.synthetic.main.run_layout.*

class PostRunViewModel : ViewModel() {


    val timesOnTreadmill = MutableLiveData(2)
    val timesOnTreadmillText : LiveData<String> = Transformations.map(timesOnTreadmill){
        "$$it"  // need divisor
    }

    val distance = MutableLiveData(0)
    val distanceText : LiveData<String> = Transformations.map(distance){
        "$it"
    }

    val pace = MutableLiveData(0.0)
    val paceText : LiveData<String> = Transformations.map(pace){
        "$it"
    }

    val time = MutableLiveData(0)
    val timeText : LiveData<String> = Transformations.map(time){
        "$it"
    }


    val split = MutableLiveData(Split(0,0,0,0, false))
    val splitText : LiveData<String> = Transformations.map(split){
        "$it"
    }
}


