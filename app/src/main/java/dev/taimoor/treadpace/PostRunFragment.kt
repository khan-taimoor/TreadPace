package dev.taimoor.treadpace


import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import kotlinx.android.synthetic.main.post_run.distance_view
import kotlinx.android.synthetic.main.post_run.pace_treadmill_view
import kotlinx.android.synthetic.main.post_run.sparkView
import kotlinx.android.synthetic.main.post_run.time_treadmill
import kotlinx.android.synthetic.main.post_run.total_time
import kotlinx.android.synthetic.main.post_run_phase_2.*
import kotlinx.android.synthetic.main.run_layout.constraintLayout
import kotlinx.android.synthetic.main.run_layout.map_view
import org.w3c.dom.Text


class PostRunFragment : Fragment(), OnMapReadyCallback {


    private var map: GoogleMap? = null
    private var mapView: MapView? = null
    private lateinit var polylineOptions : PolylineOptions
    private var points : Array<LatLng>? = null
    private var splits : Array<Split>? = null
    private var runInfo: RunInfo? = null
    private val polylines = mutableListOf<PolylineOptions>()

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


        this.points = safeArgs.points
        this.splits = safeArgs.splits
        this.runInfo = safeArgs.runInfo




        polylineOptions = PolylineOptions().color(Color.RED).width(10f)
        polylineOptions.addAll(points?.toList())
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
        /**create the camera with bounds and padding to set into map
         *
         */
        val cu = CameraUpdateFactory.newLatLngBounds(runInfo?.bounds, padding)

        val speed = this.activity?.findViewById<TextView>(R.id.speedy2)
        this.map?.animateCamera(cu)

        loadLayout(R.layout.post_run_phase_2)




        if(splits?.size != null){

            val splitSize = splits?.size
            val floatArray = Array<Float>(splitSize as Int) {0f}
            splits?.forEachIndexed { index, split ->
                floatArray[index] = split.getPaceFloat()
            }
            sparkView.adapter = MyAdapter(floatArray, runInfo?.treadmillPace?.toFloat() as Float)

            sparkView.isScrubEnabled = true
            sparkView.scrubListener = SparkView.OnScrubListener {
                if(it!= null) {
                    val index = it as Int
                    val pace = splits?.get(index)?.getPace()
                    pace?.let {
                        Log.i(Util.myTag, "$index $pace")
                        speed?.setText("$index $pace")


                    }



                }
            }



            sparkView.setBackgroundResource(R.drawable.border)
            //sparkView.setBackgroundColor(Color.RED)
            //sparkView.setBackgr
        }

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

        override fun getItem(index: Int): Int {
            return index
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