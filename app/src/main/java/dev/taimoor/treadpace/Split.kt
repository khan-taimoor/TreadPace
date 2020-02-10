package dev.taimoor.treadpace


import android.os.Parcelable
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RunInfo(val treadmillPace: Double, val timeInSeconds: Int,
                   val totDistance: Number, val numSplits: Number,
                   val numSplitsOnTreadmill: Number, val bounds: LatLngBounds) : Parcelable{}


@Parcelize
data class Split(val distance: Int, val numTicks: Int, val startTime: Int, val endTime: Int, var onTreadmill: Boolean): Parcelable{
    fun getPace(): Double{
        return (distance*1.0)/(endTime-startTime)
    }
}


