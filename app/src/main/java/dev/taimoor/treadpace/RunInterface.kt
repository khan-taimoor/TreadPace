package dev.taimoor.treadpace

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import dev.taimoor.treadpace.runFragment.RunViewModel
import dev.taimoor.treadpace.runFragment.Tick

interface RunInterface {

    val bnds: LatLngBounds.Builder
    val points : MutableList<LatLng>

    fun addPointToRun(tick: Tick)
    fun updateViewModel(runViewModel: RunViewModel, builder: RunOrder.RunOrderBuilder)
    fun getLatLngBounds(): LatLngBounds
}