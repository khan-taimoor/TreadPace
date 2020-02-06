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

class RunViewModel : ViewModel() {

    val currentDistance = MutableLiveData(0)

    private var timeInSplit = 0
    private var timeLastSplit = 0
    private var ticksInSplit = 0
    private var distanceInSplit = 0
    private var distanceLastSplit = 0
    //private var timesOnTreadmill = 2
    private val splits = mutableListOf<Split>()
    private var isPaceSet: Boolean = false



    val numSplits = MutableLiveData(2)

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

    fun updateDistance(newDistance: Int){
        currentDistance.value = newDistance
    }


    private fun incrementTimesOnTreadmill(){
        timesOnTreadmill.value = (timesOnTreadmill.value?: 0) + 1
    }

    fun setPhase(phase: Phase){
        currentPhase.value = phase
    }

    fun receiveTick(tick : Tick){
        ticksInSplit += 1
        timeInSplit = tick.timeInSeconds - timeLastSplit
        distanceInSplit = tick.distanceInSplit - distanceLastSplit

        if (!isPaceSet){
            paceIsntSet(tick)
        }
        else{
            paceIsSet(tick)
        }

//
//
//        if (!isPaceSet) {
//            paceIsntSet()
//        }
//        else {
//            paceIsSet()
//        }

    }


    private fun paceIsntSet(tick: Tick){
        if (timeInSplit >= 30) {
            splits.add(Split(distanceInSplit, ticksInSplit, timeLastSplit, tick.timeInSeconds, true))
            //paceSplitDouble.value = distanceInSplit.toDouble() / timeInSplit
            resetTicks(tick)

            if (splits.size == 2){
                isPaceSet = true
                paceTreadmillDouble.value = averagePace()
                paceSplitDouble.value = splits.last().getPace()
                paceCurrentDouble.value = 0.0
                currentPhase.value = Phase.PHASE_THREE
            }

            if (splits.size == 1){
                paceSplitDouble.value = splits.last().getPace()
                paceCurrentDouble.value = 0.0
                currentPhase.value = Phase.PHASE_TWO
            }
            return
        }

        if (timeInSplit != 0) {
            paceCurrentDouble.value = distanceInSplit.toDouble() / timeInSplit
        }
    }

    private fun paceIsSet(tick: Tick){
        if (timeInSplit >= 30) {
            splits.add(Split(distanceInSplit, ticksInSplit, timeLastSplit, tick.timeInSeconds, false))
            paceDeltaTest(splits.last())

            //time_treadmill.setText("$timesOnTreadmill/${splits.size}")
            paceSplitDouble.value = splits.last().getPace()
            paceCurrentDouble.value = 0.0
            //pace_split_view.setText("" + distanceInSplit / timeInSplit)
            //pace_current_view.setText("STARTING A NEW SPLIT")

            resetTicks(tick)

            if(currentPhase.value == Phase.PHASE_THREE){
                currentPhase.value = Phase.PHASE_FOUR
            }
        }
        else if (timeInSplit != 0) {
            paceCurrentDouble.value = distanceInSplit.toDouble() / timeInSplit
            //pace_current_view.setText("" + distanceInSplit / timeInSplit)
        }
    }


    private fun resetTicks(tick: Tick){
        timeLastSplit = tick.timeInSeconds
        distanceLastSplit = tick.distanceInSplit
        timeInSplit = 0
        ticksInSplit = 0
    }

    fun averagePace(): Double{
        if(splits.size >= 2){
            return (splits[0].getPace() + splits[1].getPace()) /2
        }
        else{
            return -1.0
        }
    }

    private fun paceDeltaTest(split: Split){
        val pace_difference = kotlin.math.abs(split.getPace() - averagePace())

        if(pace_difference < .5){
            split.onTreadmill = true
            incrementTimesOnTreadmill()
        }

        numSplits.value = splits.size
    }

    fun finishSplit(endTime: Int) {
        if (endTime != timeLastSplit) {
            splits.add(Split(distanceInSplit, ticksInSplit, timeLastSplit, endTime, false))
        }
    }

    fun getTimesOnTreadmill(): Int{
        return (timesOnTreadmill.value?: 2)
    }

    fun getSplitsSize(): Int{
        return splits.size
    }

    fun getCurrentPhase(): Phase{
        return currentPhase.value as Phase
    }

    fun getArrayOfSplits(): Array<Split?> {
        val array = Array<Split?>(splits.size) { null }

        this.splits.forEachIndexed { index, split ->
            array[index] = split
        }

            return array

    }
}

data class Tick(val timeInSeconds: Int, val distanceInSplit: Int){}

enum class Phase {
    BEFORE_RUN, PHASE_ONE, PHASE_TWO, PHASE_THREE, PHASE_FOUR
}

