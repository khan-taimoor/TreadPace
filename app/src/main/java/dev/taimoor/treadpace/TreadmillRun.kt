package dev.taimoor.treadpace

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import dev.taimoor.treadpace.runFragment.Phase
import dev.taimoor.treadpace.runFragment.RunViewModel
import dev.taimoor.treadpace.runFragment.Tick

class TreadmillRun(val viewModel: RunViewModel?) : RunInterface {
    override val bnds: LatLngBounds.Builder
    override val points: MutableList<LatLng>
    var currentDistance = 0
    var timeInSplit = 0
    var timeLastSplit = 0
    var ticksInSplit = 0
    var distanceInSplit = 0
    var distanceLastSplit = 0
    var timesOnTreadmill = 2
    val splits = mutableListOf<Split>()
    var isPaceSet: Boolean = false
    var currentPhase = (Phase.BEFORE_RUN)


    init {
        bnds = LatLngBounds.Builder()
        points = mutableListOf<LatLng>()
    }

    override fun addPointToRun(tick: Tick): RunOrder.RunOrderBuilder {
        bnds.include(tick.point)
        val builder = RunOrder.RunOrderBuilder()

        currentDistance = tick.distanceInSplit
        builder.distance(currentDistance)

        ticksInSplit += 1

        timeInSplit = tick.timeInSeconds - timeLastSplit
        distanceInSplit = tick.distanceInSplit - distanceLastSplit

        if (!isPaceSet){
            paceIsntSet(tick, builder)
        }
        else{
            paceIsSet(tick, builder)
        }

        updateViewModel(viewModel as RunViewModel, builder)

        return builder
    }

    private fun paceIsntSet(tick: Tick, builder: RunOrder.RunOrderBuilder){
        if (timeInSplit >= 30) {
            splits.add(
                Split(
                    distanceInSplit,
                    ticksInSplit,
                    timeLastSplit,
                    tick.timeInSeconds,
                    true
                )
            )
            resetTicks(tick)

            if (splits.size == 2){
                isPaceSet = true
                builder.paceTreadmillDouble(averagePace())
                builder.paceSplitDouble(splits.last().getPace())
                builder.paceCurrentDouble(0.0)
                builder.phase(Phase.PHASE_THREE)
                currentPhase = Phase.PHASE_THREE
            }

            if (splits.size == 1){
                builder.paceSplitDouble(splits.last().getPace())
                builder.paceCurrentDouble(0.0)
                builder.phase(Phase.PHASE_TWO)
                currentPhase = Phase.PHASE_TWO
            }
            return
        }

        if (timeInSplit != 0) {
            builder.paceCurrentDouble(distanceInSplit.toDouble() / timeInSplit)
        }
    }

    private fun paceIsSet(tick: Tick, builder: RunOrder.RunOrderBuilder){
        if (timeInSplit >= 30) {
            splits.add(
                Split(
                    distanceInSplit,
                    ticksInSplit,
                    timeLastSplit,
                    tick.timeInSeconds,
                    false
                )
            )
            paceDeltaTest(splits.last(), builder)
            builder.paceSplitDouble(splits.last().getPace())
            builder.paceCurrentDouble(0.0)

            resetTicks(tick)

            if(currentPhase == Phase.PHASE_THREE){
                currentPhase = Phase.PHASE_FOUR
                builder.phase(Phase.PHASE_FOUR)
            }
        }
        else if (timeInSplit != 0) {
            builder.paceCurrentDouble(distanceInSplit.toDouble() / timeInSplit)
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

    private fun paceDeltaTest(split: Split, builder: RunOrder.RunOrderBuilder){
        val paceDifference = kotlin.math.abs(split.getPace() - averagePace())

        if(paceDifference < .5){
            split.onTreadmill = true
            timesOnTreadmill +=1
            builder.timesOnTreadmill(timesOnTreadmill)

        }
        builder.numberSplits(splits.size)
    }

    fun getArrayOfSplits(): Array<Split?> {
        val array = Array<Split?>(splits.size) { null }

        this.splits.forEachIndexed { index, split ->
            array[index] = split
        }

        return array
    }

    fun finishSplit(endTime: Int) {
        if (endTime != timeLastSplit) {
            splits.add(
                Split(
                    distanceInSplit,
                    ticksInSplit,
                    timeLastSplit,
                    endTime,
                    false
                )
            )
        }
    }

    fun sizeSplits(): Int{
        return this.splits.size
    }



    override fun getLatLngBounds(): LatLngBounds{
        return this.bnds.build()
    }

    fun startRun(){
        currentPhase = Phase.PHASE_ONE
        updateViewModel(this.viewModel as RunViewModel, RunOrder.RunOrderBuilder().phase(Phase.PHASE_ONE))
    }

    fun getRunInfo(timeInSeconds: Int): RunInfo {
        return RunInfo(averagePace(), timeInSeconds, currentDistance, sizeSplits(), timesOnTreadmill, getLatLngBounds())
    }

    override fun updateViewModel(runViewModel: RunViewModel, builder: RunOrder.RunOrderBuilder) {
        runViewModel.receiveRunOrder(builder.build())
    }
}


