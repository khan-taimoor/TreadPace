package dev.taimoor.treadpace

import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.graphics.Color
import android.location.Location
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.run_layout.*
import android.os.SystemClock
import android.transition.TransitionManager
import android.view.MenuItem
import android.widget.Chronometer
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import dev.taimoor.treadpace.data.Phase
import dev.taimoor.treadpace.data.RunViewModel
import dev.taimoor.treadpace.data.Tick
import dev.taimoor.treadpace.databinding.RunLayoutBinding
import kotlinx.android.parcel.Parcelize
import java.util.*


class RunFragment : Fragment(), OnMapReadyCallback {

    private var map: GoogleMap? = null
    private var mapView: MapView? = null
    private var uiSettings : UiSettings? = null
    private var currentLocation : Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationRequest : LocationRequest? = null
    private var fastestInterval = 5000
    private var interval = 10000
    private var priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    private lateinit var pointsObserver : PointsObserver
    private var polylineOptions = PolylineOptions().color(Color.RED)
    private lateinit var binder: RunLocationService.RunServiceBinder
    private val connection = Connection()

    private var isPaceSet: Boolean = false
    private var timeInSplit = 0
    private var timeLastSplit = 0
    private var ticksInSplit = 0
    private var distanceInSplit = 0
    private var distanceLastSplit = 0
    private var timesOnTreadmill = 2
    private val splits = mutableListOf<Split>()

    private var phase : Phase = Phase.BEFORE_RUN

    private val viewModel : RunViewModel by activityViewModels()
    lateinit var binding: RunLayoutBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity?.applicationContext as Context)

        Log.i(Util.myTag, "At run fragment ${this.activity?.supportFragmentManager?.backStackEntryCount}")

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this){

            backButton((1 shl 0) or (1 shl 1) or (1 shl 2))
            Log.i(Util.myTag, "back pressed")
        }
        callback.isEnabled = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        setHasOptionsMenu(true)

        val safeArgs: RunFragmentArgs by navArgs()
        fastestInterval = safeArgs.fastestInterval
        interval = safeArgs.interval
        priority = safeArgs.priority
        locationRequest = safeArgs.locationRequest
        phase = Phase.BEFORE_RUN
        Log.i(Util.myTag, "in on create view")


        this.binding = DataBindingUtil.inflate(inflater, R.layout.run_layout, container, false)
        binding.viewmodel = this.viewModel
        binding.lifecycleOwner = this

        val phase = androidx.lifecycle.Observer<Phase> {newPhase ->
            val layout: Int by lazy {
                when{
                    newPhase == Phase.PHASE_ONE -> R.layout.run_layout_phase_1
                    newPhase == Phase.PHASE_TWO -> R.layout.run_layout_phase_2
                    newPhase == Phase.PHASE_THREE -> R.layout.run_layout_phase_3
                    else -> R.layout.run_layout_phase_4
                }
            }

            Log.i(Util.myTag, "should be fukin loading in $layout")
            //loadLayout(layout)
        }


        viewModel.currentPhase.observe(binding.lifecycleOwner as LifecycleOwner, phase)





        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = map_view

        if(mapView != null){
            mapView?.onCreate(null)
            mapView?.onResume()
            mapView?.getMapAsync(this)
        }

        Log.i(Util.myTag, "View Created")


    }

    override fun onMapReady(map: GoogleMap?) {
        MapsInitializer.initialize(context)
        this.map = map
        map?.isMyLocationEnabled = true

        uiSettings = map?.uiSettings
        uiSettings?.isRotateGesturesEnabled = false

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if(location != null){
                //this.map?.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder().tilt(45.0f).zoom(15.0f).target(LatLng(location.latitude, location.longitude)).build()))
                currentLocation = location
                this.map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 15.0f))
            }
        }





        start_run_button.setOnClickListener {
            Log.i(Util.myTag, "Starting start button actions")

            val serviceIntent = Intent(this.context, RunLocationService::class.java)
            serviceIntent.putExtra("locationRequest", locationRequest)


            ContextCompat.startForegroundService(context as Context, serviceIntent)

            Intent(this.activity?.applicationContext, RunLocationService::class.java).also { intent ->
                this.activity?.bindService(intent, connection, Context.BIND_IMPORTANT)
            }

            loadLayout(R.layout.run_layout_phase_1)
            phase = Phase.PHASE_ONE
            //viewModel.phase = Phase.PHASE_ONE
            isPaceSet = false
            timeInSplit = 0
            timeLastSplit = 0
            ticksInSplit = 0
            distanceInSplit = 0
            distanceLastSplit = 0
            timesOnTreadmill = 2
            splits.clear()

            total_time.base = SystemClock.elapsedRealtime()
            total_time?.start()


            polylineOptions = PolylineOptions().color(Color.RED).width(10f)
            this.map?.addPolyline(polylineOptions)
        }


        end_run_button.setOnClickListener {
            backButton((1 shl 2))
        }


        /** Defines callbacks for service binding, passed to bindService()  */


    }

    override fun onPause() {
        super.onPause()
        Log.i("RunFragment", "On Pause")
    }


    inner class Connection : ServiceConnection{
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            binder = service as RunLocationService.RunServiceBinder
            pointsObserver = PointsObserver()
            binder.addObserver(pointsObserver)
        }

        override fun onServiceDisconnected(className: ComponentName) {
            Log.i(Util.myTag, "Service to Binder ended")
        }
    }

    inner class PointsObserver : Observer {
        override fun update(observable: Observable?, p1: Any?)
        {
            addNewPoint()
            tick()


            Log.i(Util.myTag, "ticks in split: $ticksInSplit")

            if (!isPaceSet) {
                paceIsntSet()
            }
            else {
                paceIsSet()
            }
        }
    }



    fun Chronometer.getTimeInSeconds(): Int{
        val time = SystemClock.elapsedRealtime() - this.base

        return time.toInt()/1000
    }


    private fun resetTicks(){
        timeLastSplit = total_time.getTimeInSeconds()
        distanceLastSplit = binder.getDistance()
        timeInSplit = 0
        ticksInSplit = 0
    }

    private fun tick(){
        //distance_view.setText("" + binder.getDistance())
        viewModel.updateDistance(binder.getDistance())
        ticksInSplit += 1
        timeInSplit = total_time.getTimeInSeconds() - timeLastSplit
        distanceInSplit = binder.getDistance() - distanceLastSplit


        viewModel.receiveTick(Tick(total_time.getTimeInSeconds(), binder.getDistance()))

    }

    private fun addNewPoint(){
        polylineOptions.add(binder.getMostRecentLatLng())
        map?.clear()
        map?.addPolyline(polylineOptions)
    }

    private fun paceDeltaTest(){
        val pace_difference = kotlin.math.abs(splits.last().getPace() - averagePace())
        if(pace_difference < .5){
            splits.last().onTreadmill = true
            timesOnTreadmill +=1
        }
    }

    private fun paceIsntSet(){
        if (timeInSplit >= 30) {
            splits.add(Split(distanceInSplit, ticksInSplit, timeLastSplit, total_time.getTimeInSeconds(), true))
            //pace_split_view.setText("" + distanceInSplit / timeInSplit)
            resetTicks()
        }

        if (splits.size == 2) {
            isPaceSet = true
            val avgPace = averagePace()

            //pace_treadmill_view.setText("" + "%.2f".format(avgPace))
            //pace_current_view.setText("STARTING A NEW SPLIT")

            phase = Phase.PHASE_THREE
            loadLayout(R.layout.run_layout_phase_3)

        }
        else if (splits.size == 1) {
            loadLayout(R.layout.run_layout_phase_2)
            if(phase == Phase.PHASE_ONE){
                phase = Phase.PHASE_TWO
                //viewModel.updatePhase(Phase.PHASE_TWO)
                loadLayout(R.layout.run_layout_phase_2)
                //pace_current_view.setText("STARTING A NEW SPLIT")
            }
        }

        if (timeInSplit != 0) {
            //pace_current_view.setText("" + distanceInSplit / timeInSplit)
        }
    }

    private fun paceIsSet(){
        if (timeInSplit >= 30) {
            splits.add(Split(distanceInSplit, ticksInSplit, timeLastSplit, total_time.getTimeInSeconds(), false))
            paceDeltaTest()

            //time_treadmill.setText("$timesOnTreadmill/${splits.size}")
            //pace_split_view.setText("" + distanceInSplit / timeInSplit)
            //pace_current_view.setText("STARTING A NEW SPLIT")

            resetTicks()

            if(phase == Phase.PHASE_THREE){
                loadLayout(R.layout.run_layout_phase_4)
                phase = Phase.PHASE_FOUR
            }
        }
        else if (timeInSplit != 0) {
            //pace_current_view.setText("" + distanceInSplit / timeInSplit)
        }
    }


    fun averagePace(): Double{
        if(splits.size >= 2){
            return (splits[0].getPace() + splits[1].getPace()) /2
        }
        else{
            return -1.0
        }
    }

    private fun loadLayout(resourceId: Int){
        TransitionManager.beginDelayedTransition(constraintLayout)
        val constraintSet = ConstraintSet()
        constraintSet.load(this@RunFragment.context, resourceId)
        constraintSet.applyTo(constraintLayout)
        TransitionManager.beginDelayedTransition(constraintLayout)
        constraintSet.applyTo(constraintLayout)
    }




    private fun arrayOfSplits(): Array<Split?>{
        val array = Array<Split?>(this@RunFragment.splits.size) {null}

        this.splits.forEachIndexed { index, split ->
            array[index] = split
        }

        return array
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            backButton((1 shl 0) or (1 shl 1))
        }
        return true
    }



    // if before run, end
    // if in phase 4, offer to go to next run fragment or to exit early. STOP SERVICE ALSO
    // if before phase 4, offer to quit run early. STOP SERVICE EARLY
    private fun backButton(flags: Int){

        val builder: AlertDialog.Builder? = this.activity?.let {
            AlertDialog.Builder(it)
        }

        if(this.phase == Phase.BEFORE_RUN){

            builder?.setTitle("Return to home page")

            builder?.setNegativeButton("Stay"){ dialog, id ->
                Log.i(Util.myTag, "Exit run without saving")
            }

            builder?.setPositiveButton("Return") { dialog, id ->
                Log.i(Util.myTag, "Return to run screen")
                findNavController().navigate(RunFragmentDirections.actionRunFragmentToHomeRunFragment())
            }
        }

        if(this.phase > Phase.BEFORE_RUN){

            builder?.setTitle("Change run state?")
            if(flags and (1 shl 0) == (1 shl 0)){
                builder?.setNeutralButton("Return"){ dialog, id ->
                    Log.i(Util.myTag, "resuming run")
                }
            }
            if(flags and (1 shl 1) == (1 shl 1)){
                builder?.setNegativeButton("Exit w/o saving"){ dialog, id ->
                    Log.i(Util.myTag, "Exit run without saving")
                    endService()
                    findNavController().navigate(RunFragmentDirections.actionRunFragmentToHomeRunFragment())

                }
            }
        }

        if (this.phase >= Phase.PHASE_FOUR) {
            if (flags and (1 shl 2) == (1 shl 2)) {
                builder?.setPositiveButton("Save") { dialog, id ->
                    Log.i(Util.myTag, "Exit and save")
                    endAndSaveRun()
                }
            }
        }
        builder?.show()
    }

    private fun endAndSaveRun(){


        splits.add(Split(distanceInSplit, ticksInSplit, timeLastSplit, total_time.getTimeInSeconds(), false))

        val action = RunFragmentDirections.actionRunFragmentToPostRunFragment()
            .setBounds(binder.getLatLngBounds())
            .setPoints(binder.getPoints())
            .setSplits(arrayOfSplits())
            .setRunInfo(RunInfo(averagePace(), total_time.getTimeInSeconds(), binder.getDistance(),timesOnTreadmill, splits.size, binder.getLatLngBounds()))


        endService()


        findNavController().navigate(action)
    }

    private fun endService(){

        binder.removeObserver()
        val endServiceIntent = Intent(this.context, RunLocationService::class.java)
        this.activity?.applicationContext?.stopService(endServiceIntent)
        total_time?.stop()

    }
}