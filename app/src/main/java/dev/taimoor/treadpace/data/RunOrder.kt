package dev.taimoor.treadpace.data

import dev.taimoor.treadpace.runFragment.Phase

data class RunOrder private constructor(val builder: RunOrderBuilder){
    var distance : Int? = null
    var paceCurrentDouble: Double? = null
    var paceTreadmillDouble: Double? = null
    var paceSplitDouble: Double? = null
    var phase: Phase? = null
    var numberSplits: Int? = null
    var timesOnTreadmill: Int? = null

    init {
        this.distance = builder.distance
        this.paceCurrentDouble = builder.paceCurrentDouble
        this.paceTreadmillDouble = builder.paceTreadmillDouble
        this.paceSplitDouble = builder.paceSplitDouble
        this.phase = builder.phase
        this.numberSplits = builder.numberSplits
        this.timesOnTreadmill = builder.timesOnTreadmill
    }

    class RunOrderBuilder {
        var distance : Int? = null
        var paceCurrentDouble: Double? = null
        var paceTreadmillDouble: Double? = null
        var paceSplitDouble: Double? = null
        var phase: Phase? = null
        var numberSplits: Int? = null
        var timesOnTreadmill: Int? = null
            private set

        fun distance(distance: Int) = apply { this.distance = distance }
        fun paceCurrentDouble(paceCurrentDouble: Double) = apply { this.paceCurrentDouble = paceCurrentDouble }
        fun paceTreadmillDouble(paceTreadmillDouble: Double) = apply { this.paceTreadmillDouble = paceTreadmillDouble }
        fun paceSplitDouble(paceSplitDouble: Double) = apply { this.paceSplitDouble = paceSplitDouble }
        fun phase(phase: Phase) = apply { this.phase = phase }
        fun numberSplits(numberSplits: Int) = apply { this.numberSplits = numberSplits }
        fun timesOnTreadmill(timesOnTreadmill: Int) = apply { this.timesOnTreadmill = timesOnTreadmill }

        fun build() = RunOrder(this)
    }
}

