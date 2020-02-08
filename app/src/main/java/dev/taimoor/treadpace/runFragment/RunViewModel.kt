package dev.taimoor.treadpace.runFragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import dev.taimoor.treadpace.RunOrder
import dev.taimoor.treadpace.Util

class RunViewModel : ViewModel() {


    val numSplits = MutableLiveData(2)

    val currentDistanceInt = MutableLiveData(0)
    val currentDistance : LiveData<String> = Transformations.map(currentDistanceInt){
        "$it"
    }

    val timesOnTreadmill = MutableLiveData(2)
    val timesOnTreadmillText : LiveData<String> = Transformations.map(numSplits){
        //"$it/${splits.size}"
        "${timesOnTreadmill.value}/$it"
    }


    val paceTreadmillDouble = MutableLiveData(0.0)
    val paceTreadmillText: LiveData<String> = Transformations.map(paceTreadmillDouble){
        "%.2f".format(it)
    }

    val paceSplitDouble = MutableLiveData(0.0)
    val paceSplitText: LiveData<String> = Transformations.map(paceSplitDouble){
        "%.2f".format(it)
    }

    val paceCurrentDouble = MutableLiveData(0.0)
    val paceCurrentText: LiveData<String> = Transformations.map(paceCurrentDouble){
        "%.2f".format(it)
    }

    val currentPhase = MutableLiveData(Phase.BEFORE_RUN)

    val buttonText = "Start Run"

    fun receiveRunOrder(runOrder: RunOrder){

        runOrder.distance?.let {
            currentDistanceInt.value = it
        }

        runOrder.paceTreadmillDouble?.let {
            paceTreadmillDouble.value = it
        }

        runOrder.paceCurrentDouble?.let {
            paceCurrentDouble.value = it
        }

        runOrder.paceSplitDouble?.let {
            paceSplitDouble.value = it
        }

        runOrder.timesOnTreadmill?.let {
            timesOnTreadmill.value = it
        }

        runOrder.numberSplits?.let {
            numSplits.value = it
        }

        runOrder.phase?.let {
            if(currentPhase.value != it){
                currentPhase.value = it
            }
        }
    }
}

data class Tick(val timeInSeconds: Int, val distanceInSplit: Int, val point: LatLng){}

enum class Phase {
    BEFORE_RUN, PHASE_ONE, PHASE_TWO, PHASE_THREE, PHASE_FOUR
}

