package dev.taimoor.treadpace


import android.graphics.Color
import android.os.Bundle
import android.transition.TransitionManager
import android.util.Log
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
import com.google.android.gms.maps.model.PolylineOptions
import com.robinhood.spark.SparkAdapter
import com.robinhood.spark.SparkView
import kotlinx.android.synthetic.main.post_run.*
import kotlinx.android.synthetic.main.run_layout.constraintLayout
import kotlinx.android.synthetic.main.run_layout.map_view


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

        val safeArgs: PostRunFragmentArgs by navArgs()

        val points = safeArgs.points
        val splits = safeArgs.splits
        val runInfo = safeArgs.runInfo


        polylineOptions = PolylineOptions().color(Color.RED).width(10f)
        polylineOptions.addAll(points?.toMutableList())
        this.map?.addPolyline(polylineOptions)


        distance_view.setText("" + runInfo?.totDistance)
        pace_treadmill_view.text = "" + runInfo?.treadmillPace
        total_time.text = "" + runInfo?.timeInSeconds
        time_treadmill.text = "" + runInfo?.numSplitsOnTreadmill + "/" + runInfo?.numSplits


        //val postRunPackage = safeArgs.postRunPackage

        val padding = 50
        /**create the bounds from latlngBuilder to set into map camera*/
        /**create the bounds from latlngBuilder to set into map camera */
        /**create the camera with bounds and padding to set into map*/
        /**create the camera with bounds and padding to set into map */
        val cu = CameraUpdateFactory.newLatLngBounds(runInfo?.bounds, padding)

        this.map?.animateCamera(cu)



        if(splits != null){

            val floatArray = Array<Float>(splits.size) {0f}

            splits.forEachIndexed { index, split ->
                floatArray[index] = split.getPaceFloat()
            }
            sparkView.adapter = MyAdapter(floatArray, runInfo?.treadmillPace?.toFloat() as Float)

            sparkView.isScrubEnabled = true
            sparkView.scrubListener = SparkView.OnScrubListener {
                if(it!= null) {
                    Log.i(Util.myTag, it.toString())
                }
            }

        }

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

    class MyAdapter(private val yData: Array<Float>, private val baseline: Float) : SparkAdapter() {
        override fun getCount(): Int {
            return yData.size
        }

        override fun getItem(index: Int): Any {
            return yData[index]
        }

        override fun getY(index: Int): Float {
            return yData[index]
        }

        override fun hasBaseLine(): Boolean {
            return true
        }

        override fun getBaseLine(): Float {
            return this.baseline
        }


    }


}