package dev.taimoor.treadpace


import android.graphics.Color
import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.run_layout.*


class PostRunFragment : Fragment(), OnMapReadyCallback {


    private var map: GoogleMap? = null
    private var mapView: MapView? = null
    private lateinit var polylineOptions : PolylineOptions


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        setHasOptionsMenu(true)


        return inflater.inflate(R.layout.post_run, container, false)



    }

    override fun onMapReady(map: GoogleMap?) {
        this.map = map


        val Delhi = LatLng(28.61, 77.2099)
        //val Chandigarh = LatLng(30.75, 76.78)
        //val SriLanka = LatLng(7.000, 81.0000)
        //val America = LatLng(38.8833, 77.0167)
        //val Arab = LatLng(24.000, 45.000)

        val safeArgs: PostRunFragmentArgs by navArgs()

        val bounds = safeArgs.bounds
        val points = safeArgs.points

        val numba = 10
        polylineOptions = PolylineOptions().color(Color.RED).width(numba.toFloat())
        polylineOptions.addAll(points?.toMutableList())
        this.map?.addPolyline(polylineOptions)

        //val postRunPackage = safeArgs.postRunPackage

        val padding = 50
        /**create the bounds from latlngBuilder to set into map camera*/
        /**create the bounds from latlngBuilder to set into map camera */
        /**create the camera with bounds and padding to set into map*/
        /**create the camera with bounds and padding to set into map */
        val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)

        this.map?.animateCamera(cu)

        loadLayout(R.layout.post_run_phase_2)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = map_view

        if(mapView != null){
            mapView?.onCreate(null)
            mapView?.onResume()
            mapView?.getMapAsync(this)
        }




    }

    private fun loadLayout(resourceId: Int){
        TransitionManager.beginDelayedTransition(constraintLayout)
        val constraintSet = ConstraintSet()
        constraintSet.load(this@PostRunFragment.context, resourceId)
        constraintSet.applyTo(constraintLayout)
        TransitionManager.beginDelayedTransition(constraintLayout)
        constraintSet.applyTo(constraintLayout)
    }




}