package dev.taimoor.treadpace

import android.os.Binder
import android.os.IBinder
import android.os.Parcelable
import android.widget.Chronometer
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Split(val distance: Int, val numTicks: Int, val startTime: Int, val endTime: Int, var onTreadmill: Boolean): Parcelable{
    fun getPace(): Double{
        return (distance*1.0)/(endTime-startTime)
    }

    fun getPaceFloat(): Float{
        return ((distance*1.0)/(endTime-startTime)).toFloat()
    }
}