package dev.taimoor.treadpace.postRunFragment


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import androidx.transition.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.robinhood.spark.SparkAdapter
import com.robinhood.spark.SparkView
import dev.taimoor.treadpace.*
import dev.taimoor.treadpace.R
import dev.taimoor.treadpace.data.RunInfo
import dev.taimoor.treadpace.data.Split
import dev.taimoor.treadpace.databinding.PostRunPhase2BindingImpl
import dev.taimoor.treadpace.room.*
import dev.taimoor.treadpace.settings.UnitSetting
import kotlinx.android.synthetic.main.post_run.*
import kotlinx.android.synthetic.main.run_layout.map_view


class PostRunFragment : Fragment(), OnMapReadyCallback {


    private var map: GoogleMap? = null
    private var mapView: MapView? = null
    private lateinit var polylineOptions : PolylineOptions
    private var points : Array<LatLng>? = null
    private var splits : Array<Split>? = null
    private var runInfo: RunInfo? = null
    private var polyline: Polyline? = null
    private var savingRun : Boolean ? = null

    private var currentIndexSplit : Int? = null

    val safeArgs: PostRunFragmentArgs by navArgs()


    lateinit var runEntity: RunEntity


    private lateinit var viewModel : PostRunViewModel
    lateinit var binding: PostRunPhase2BindingImpl


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this){
            exitPrompt()
        }
        callback.isEnabled = true


        val unit = PreferenceManager.getDefaultSharedPreferences(this.context as Context).getString("units", "mi").toString()

        viewModel = ViewModelProviders.of(this,
        UnitSettingViewModelFactory(UnitSetting.valueOf(unit))).get(PostRunViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        setHasOptionsMenu(true)


        runEntity = safeArgs.runEntity

        viewModel.timesOnTreadmill.postValue(Pair(runEntity.runInfo.numSplitsOnTreadmill.toInt(),
            runEntity.runInfo.numSplits.toInt()))
        viewModel.distance.postValue(runEntity.runInfo.totDistance.toInt())
        viewModel.pace.postValue(runEntity.runInfo.treadmillPace)
        viewModel.time.postValue(runEntity.runInfo.timeInSeconds)


        this.binding = DataBindingUtil.inflate(inflater,
            R.layout.post_run_phase_2, container, false)
        binding.viewmodel = this.viewModel
        binding.lifecycleOwner = this@PostRunFragment

        return binding.root
    }

    override fun onMapReady(map: GoogleMap?) {

        this.map = map

        this.points = runEntity.points
        this.splits = runEntity.splits
        this.runInfo = runEntity.runInfo
        this.savingRun = safeArgs.savingRun

        viewModel.savingRun.postValue(this.savingRun)

        polylineOptions = PolylineOptions().color(Color.RED).width(10f)
        polylineOptions.addAll(points?.toList())
        this.map?.addPolyline(polylineOptions)
        this.map?.moveCamera(CameraUpdateFactory.newLatLngZoom(points?.last(), 15.0f))



        //loadLayout(R.layout.post_run_phase_2)

//        val cu = CameraUpdateFactory.newLatLngBounds(runInfo?.bounds, 200)
//
//        //val callba
//        this.map?.animateCamera(cu )


        if(splits?.size != null){

            val splitSize = splits?.size
            val floatArray = Array<Float>(splitSize as Int) {0f}
            splits?.forEachIndexed { index, split ->
                floatArray[index] = split.getPace().toFloat()
            }
            sparkView.adapter =
                MyAdapter(
                    floatArray,
                    ((this.splits!![0].getPace() + this.splits!![1].getPace()) / 2).toFloat()
                    //runInfo?.treadmillPace?.toFloat() as Float
                )

            sparkView.isScrubEnabled = true
            sparkView.scrubListener = SparkView.OnScrubListener {
                if(it!= null) {
                    val index = it as Int
                    val pace = splits?.get(index)?.getPace()
                    splitToCreateForPolyline(index)
                    pace?.let {
                        viewModel.split.postValue(Pair(index, pace))


                    }



                }
            }
            sparkView.setBackgroundResource(R.drawable.border)




            save_run_button.setOnClickListener {
                val homeViewModel by viewModels<HomeViewModel>{
                    HomeViewModelFactory(((requireContext().applicationContext as TodoApplication).runRepository))
                }
                homeViewModel.insert(runEntity)
                val go_home = PostRunFragmentDirections.goHomeMessage(Util.save_run)
                findNavController().navigate(go_home)
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

        val splits = this.splits
        val points = this.points

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
        Log.i(Util.myTag, "In Load layout")




        val transition = AutoTransition()
        transition.duration = 500
        transition.addListener(TranListener(runInfo?.bounds, this.map))


        TransitionManager.beginDelayedTransition(constraintLayoutPostRun, transition)
        val constraintSet = ConstraintSet()
        constraintSet.load(this@PostRunFragment.context, resourceId)
        constraintSet.applyTo(constraintLayoutPostRun)
        TransitionManager.beginDelayedTransition(constraintLayoutPostRun)

        val saving = safeArgs.savingRun


        viewModel.savingRun.postValue(saving)

    }

    class TranListener(val bounds: LatLngBounds?, val map: GoogleMap?) : TransitionListenerAdapter() {
        override fun onTransitionStart(transition: Transition) {

            //val cu = CameraUpdateFactory
        }

        override fun onTransitionEnd(transition: Transition) {
            val cu = CameraUpdateFactory.newLatLngBounds(bounds, 200)

            map?.animateCamera(cu)

            Log.i(Util.myTag, "End")


        }
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
            exitPrompt()
        }
        else if(item.itemId == R.id.delete){
            Log.i(Util.myTag, "Delete being pressed")
            val homeViewModel by viewModels<HomeViewModel>{
                HomeViewModelFactory(((requireContext().applicationContext as TodoApplication).runRepository))
            }
            homeViewModel.delete(runEntity)
            val go_home = PostRunFragmentDirections.goHomeMessage(Util.delete_run)
            findNavController().navigate(go_home)

        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()

        // TODO: Only show this option when the run was opened from home screen
        if(!safeArgs.savingRun) {
            inflater.inflate(R.menu.post_run_menu, menu)
        }
    }

    private fun exitPrompt(){

        if (this.savingRun!!) {
            val builder: AlertDialog.Builder? = this.activity?.let {
                AlertDialog.Builder(it)
            }

            builder.apply {
                this?.setTitle("Exit without saving?")
                this?.setPositiveButton("Exit") { dialog, id ->
                    findNavController().navigate(R.id.global_go_home)
                }
                this?.setNegativeButton("Stay") { dialog, id ->
                    //do nothing
                }
            }


            builder?.show()
        } else {
            findNavController().navigate(R.id.global_go_home)
        }
    }

}
@BindingAdapter("app:showIfSavingRun")
fun showIfSavingRun(view: View, savingRun: Boolean){
    Log.i(Util.myTag, "in show if saving run, savingRun = $savingRun")
//    if(!savingRun){
//        view.visibility = View.GONE
//    }
    view.visibility = if (savingRun) View.VISIBLE else View.GONE
}

