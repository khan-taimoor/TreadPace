package dev.taimoor.treadpace.runFragment

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
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.maps.model.PolylineOptions
import dev.taimoor.treadpace.*
import dev.taimoor.treadpace.R
import dev.taimoor.treadpace.databinding.RunLayoutBinding
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



    private val viewModel : RunViewModel by viewModels()
    private lateinit var treadmill: TreadmillRun
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
        Log.i(Util.myTag, "in on create view")




        this.binding = DataBindingUtil.inflate(inflater,
            R.layout.run_layout, container, false)
        binding.viewmodel = this.viewModel
        binding.lifecycleOwner = this@RunFragment

        val phase = androidx.lifecycle.Observer<Phase> { newPhase ->
            val layout: Int by lazy {
                when{
                    newPhase == Phase.BEFORE_RUN -> -1
                    newPhase == Phase.PHASE_ONE -> R.layout.run_layout_phase_1
                    newPhase == Phase.PHASE_TWO -> R.layout.run_layout_phase_2
                    newPhase == Phase.PHASE_THREE -> R.layout.run_layout_phase_3
                    newPhase == Phase.PHASE_FOUR -> R.layout.run_layout_phase_4
                    else -> -1
                }


            }

            if(layout == -1)
                return@Observer

            Log.i(Util.myTag, "should be loading in $layout")

            loadLayout(layout)
        }


        viewModel.currentPhase.observe(binding.lifecycleOwner as LifecycleOwner, phase)

        treadmill = TreadmillRun(viewModel)

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

            treadmill.startRun()


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
        }
    }



    fun Chronometer.getTimeInSeconds(): Int{
        val time = SystemClock.elapsedRealtime() - this.base

        return time.toInt()/1000
    }

    private fun tick(){
        this.treadmill.addPointToRun(
            Tick(
                total_time.getTimeInSeconds(),
                binder.getDistance(),
                binder.getMostRecentLatLng()
            )
        )
    }

    private fun addNewPoint(){
        polylineOptions.add(binder.getMostRecentLatLng())
        map?.clear()
        map?.addPolyline(polylineOptions)
    }

    private fun loadLayout(resourceId: Int){
        TransitionManager.beginDelayedTransition(constraintLayout)
        val constraintSet = ConstraintSet()
        constraintSet.load(this@RunFragment.context, resourceId)
        constraintSet.applyTo(constraintLayout)
        TransitionManager.beginDelayedTransition(constraintLayout)
        constraintSet.applyTo(constraintLayout)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            backButton((1 shl 0) or (1 shl 1))
        }
        return true
    }

    private fun endAndSaveRun(){
        this.treadmill.finishSplit(total_time.getTimeInSeconds())

        val action = RunFragmentDirections.actionRunFragmentToPostRunFragment()
            .setBounds(treadmill.bnds.build())
            .setPoints(treadmill.points.toTypedArray())
            .setSplits(treadmill.splits.toTypedArray())
            .setBounds(treadmill.getLatLngBounds())
            .setRunInfo(treadmill.getRunInfo(total_time.getTimeInSeconds()))

      endService()

        findNavController().navigate(action)
    }

    private fun endService(){
        binder.removeObserver()
        val endServiceIntent = Intent(this.context, RunLocationService::class.java)
        this.activity?.applicationContext?.stopService(endServiceIntent)
        total_time?.stop()
    }



    // if before run, end
    // if in phase 4, offer to go to next run fragment or to exit early. STOP SERVICE ALSO
    // if before phase 4, offer to quit run early. STOP SERVICE EARLY
    private fun backButton(flags: Int){

        val builder: AlertDialog.Builder? = this.activity?.let {
            AlertDialog.Builder(it)
        }

        val phase = treadmill.currentPhase
        if(phase == Phase.BEFORE_RUN){

            builder?.setTitle("Return to home page")

            builder?.setNegativeButton("Stay"){ dialog, id ->
                Log.i(Util.myTag, "Exit run without saving")
            }

            builder?.setPositiveButton("Return") { dialog, id ->
                Log.i(Util.myTag, "Return to run screen")
                findNavController().navigate(RunFragmentDirections.actionRunFragmentToHomeRunFragment())
            }
        }

        if(phase > Phase.BEFORE_RUN){

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

        if (phase >= Phase.PHASE_FOUR) {
            if (flags and (1 shl 2) == (1 shl 2)) {
                builder?.setPositiveButton("Save") { dialog, id ->
                    Log.i(Util.myTag, "Exit and save")
                    endAndSaveRun()
                }
            }
        }
        builder?.show()
    }
}