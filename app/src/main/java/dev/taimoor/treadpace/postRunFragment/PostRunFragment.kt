package dev.taimoor.treadpace.postRunFragment


import android.graphics.Color
import android.os.Bundle
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.robinhood.spark.SparkAdapter
import com.robinhood.spark.SparkView
import dev.taimoor.treadpace.*
import dev.taimoor.treadpace.RunInfo
import dev.taimoor.treadpace.Split
import dev.taimoor.treadpace.databinding.PostRunBinding
import dev.taimoor.treadpace.room.HomeViewModel
import dev.taimoor.treadpace.room.RunEntity
import kotlinx.android.synthetic.main.post_run.*
import kotlinx.android.synthetic.main.run_layout.constraintLayout
import kotlinx.android.synthetic.main.run_layout.map_view
import java.time.OffsetDateTime


class PostRunFragment : Fragment(), OnMapReadyCallback {


    private var map: GoogleMap? = null
    private var mapView: MapView? = null
    private lateinit var polylineOptions : PolylineOptions
    private var points : Array<LatLng>? = null
    private var splits : Array<Split>? = null
    private var runInfo: RunInfo? = null
    private var polyline: Polyline? = null

    private var currentIndexSplit : Int? = null


    private val viewModel : PostRunViewModel by viewModels()
    lateinit var binding: PostRunBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this){
            findNavController().navigate(R.id.global_go_home)
        }
        callback.isEnabled = true

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        setHasOptionsMenu(true)



        this.binding = DataBindingUtil.inflate(inflater,
            R.layout.post_run, container, false)
        binding.viewmodel = this.viewModel
        binding.lifecycleOwner = this@PostRunFragment

        return binding.root
    }

    override fun onMapReady(map: GoogleMap?) {

        this.map = map

        val safeArgs: PostRunFragmentArgs by navArgs()


        val gson = Gson()
        this.points = safeArgs.points
        this.splits = safeArgs.splits
        this.runInfo = safeArgs.runInfo
        Log.i(Util.myTag, gson.toJson(runInfo))
        Log.i(Util.myTag, gson.toJson(splits))
        Log.i(Util.myTag, gson.toJson(points))




        polylineOptions = PolylineOptions().color(Color.RED).width(10f)
        polylineOptions.addAll(points?.toList())
        this.map?.addPolyline(polylineOptions)


        //TODO: move to view model
        distance_view.text= ("${runInfo?.totDistance}")
        pace_treadmill_view.text = " ${"%.2f".format(runInfo?.treadmillPace)}"
        total_time.text = "${runInfo?.timeInSeconds}"
        time_treadmill.text = "${runInfo?.numSplitsOnTreadmill} / ${runInfo?.numSplits}"

//        viewModel.distance.value = runInfo?.totDistance as Int
//        viewModel.pace.value = runInfo?.treadmillPace as Double
//        viewModel.time.value = runInfo?.timeInSeconds as Int
//        viewModel.timesOnTreadmill.value = runInfo?.numSplitsOnTreadmill as Int



        //val postRunPackage = safeArgs.postRunPackage

        loadLayout(R.layout.post_run_phase_2)

        val cu = CameraUpdateFactory.newLatLngBounds(runInfo?.bounds, 50)
        this.map?.animateCamera(cu)

        val speed = this.activity?.findViewById<TextView>(R.id.speedy2)

        if(splits?.size != null){

            val splitSize = splits?.size
            val floatArray = Array<Float>(splitSize as Int) {0f}
            splits?.forEachIndexed { index, split ->
                floatArray[index] = split.getPace().toFloat()
            }
            sparkView.adapter =
                MyAdapter(
                    floatArray,
                    runInfo?.treadmillPace?.toFloat() as Float
                )

            sparkView.isScrubEnabled = true
            sparkView.scrubListener = SparkView.OnScrubListener {
                if(it!= null) {
                    val index = it as Int
                    val pace = splits?.get(index)?.getPace()
                    splitToCreateForPolyline(index)
                    pace?.let {
                        //Log.i(Util.myTag, "$index $pace")
                        speed?.setText("Split: $index Pace: $pace")


                    }



                }
            }
            sparkView.setBackgroundResource(R.drawable.border)




            save_run_button.setOnClickListener {
                val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
                homeViewModel.insert(RunEntity(this.runInfo as RunInfo, this.splits as Array<Split>,
                    this.points as Array<LatLng>, OffsetDateTime.now()))

                findNavController().navigate(R.id.global_go_home)

            }


        }

    }

    private fun splitToCreateForPolyline(index: Int){

        if (currentIndexSplit != null && currentIndexSplit == index){
            return
        }



        if(polyline != null){
            polyline?.remove()
        }

        var pointsStart = 0

        val splits = this?.splits
        val points = this?.points

        for (x in 0 until index){
            pointsStart += (splits?.get(x)?.numTicks as Int)
        }

        var pointsEnd = pointsStart + (splits?.get(index)?.numTicks as Int)


        if(pointsEnd - pointsStart == 0){
            return
        }

        currentIndexSplit = index

        val boundsBuilder = LatLngBounds.builder()

        val pointsList = mutableListOf<LatLng>()
        for (point in pointsStart until pointsEnd){
            pointsList.add(this.points?.get(point) as LatLng)
            boundsBuilder.include(this.points?.get(point))
        }

        Log.i(Util.myTag, "index:$index start:$pointsStart end:$pointsEnd")

        val polylineOptions = PolylineOptions().color(Color.YELLOW).width(20f).zIndex(1f)


        polylineOptions.addAll(pointsList)
        polyline = this.map?.addPolyline(polylineOptions)
        val cu = CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100)
        this.map?.animateCamera(cu)


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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            findNavController().navigate(R.id.global_go_home)
        }
        return true
    }




}