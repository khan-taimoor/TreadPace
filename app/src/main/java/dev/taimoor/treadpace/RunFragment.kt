package dev.taimoor.treadpace

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
import android.widget.Chronometer
import androidx.constraintlayout.widget.ConstraintSet
import com.google.android.gms.maps.model.PolylineOptions
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

    val splits = mutableListOf<Split>()
    private var timeInSplit = 0
    private var timeLastSplit = 0
    private var ticksInSplit = 0
    private var distanceInSplit = 0
    private var distanceLastSplit = 0
    private var timesOnTreadmill = 2





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

        return inflater.inflate(R.layout.run_layout, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity?.applicationContext as Context)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = map_view

        if(mapView != null){
            mapView?.onCreate(null)
            mapView?.onResume()
            mapView?.getMapAsync(this)
        }

        Log.i("RunFragment", "View Created")

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

            TransitionManager.beginDelayedTransition(constraintLayout)
            val constraintSet = ConstraintSet()
            constraintSet.load(this.context, R.layout.run_layout_phase_1)
            constraintSet.applyTo(constraintLayout)
            TransitionManager.beginDelayedTransition(constraintLayout)
            constraintSet.applyTo(constraintLayout)


            total_time.base = SystemClock.elapsedRealtime()
            total_time?.start()


            total_time?.setOnChronometerTickListener {
                val time = SystemClock.elapsedRealtime() - it.base
                val h = (time / 3600000).toInt()
                val m = ((time - h * 3600000)).toInt() / 60000
                val s = ((time - h * 3600000 - m * 60000)).toInt() / 1000
            }



            val numba = 10
            polylineOptions = PolylineOptions().color(Color.RED).width(numba.toFloat())
            this.map?.addPolyline(polylineOptions)
        }


        end_run_button.setOnClickListener {
            binder.removeObserver()

            val endServiceIntent = Intent(this.context, RunLocationService::class.java)
            this.activity?.applicationContext?.stopService(endServiceIntent)
            //this.activity?.applicationContext?.unbindService(connection)
            val action = RunFragmentDirections.actionRunFragmentToPostRunFragment()
            total_time?.stop()
            findNavController().navigate(action)
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
        override fun update(observable: Observable?, p1: Any?) {
            Log.i(Util.myTag, "puttin in teh point")
            polylineOptions.add(binder.getMostRecentLatLng())
            map?.clear()
            map?.addPolyline(polylineOptions)

            distance_view.setText("" + binder.getDistance())
            ticksInSplit += 1
            timeInSplit = total_time.getTimeInSeconds() - timeLastSplit
            distanceInSplit = binder.getDistance() - distanceLastSplit


            if (!isPaceSet) {

                if (timeInSplit >= 30) {
                    /*
                    Log.i(Util.myTag, "Adding run:\ndistance=$distanceInSplit\nticks=$ticksInSplit" +
                            "\ntimeLastSplit=$timeLastSplit\ncurrentTime=${total_time.getTimeInSeconds()}")
                     */

                    splits.add(Split(distanceInSplit, ticksInSplit, timeLastSplit, total_time.getTimeInSeconds(), true))
                    pace_split_view.setText("" + distanceInSplit / timeInSplit)

                    timeLastSplit = total_time.getTimeInSeconds()
                    distanceLastSplit = binder.getDistance()
                    timeInSplit = 0
                    ticksInSplit = 0
                }

                if (splits.size == 2) {
                    isPaceSet = true
                    val avgPace = splits[0].getPace() + splits[1].getPace()
                    pace_treadmill_view.setText("" + avgPace)
                    pace_current_view.setText("STARTING A NEW SPLIT")

                    TransitionManager.beginDelayedTransition(constraintLayout)
                    val constraintSet = ConstraintSet()
                    constraintSet.load(this@RunFragment.context, R.layout.run_layout_phase_3)
                    constraintSet.applyTo(constraintLayout)
                    TransitionManager.beginDelayedTransition(constraintLayout)
                    constraintSet.applyTo(constraintLayout)


                    //TODO: change UI to design in OneNote
                } else if (splits.size == 1) {
                    pace_current_view.setText("STARTING A NEW SPLIT")


                    TransitionManager.beginDelayedTransition(constraintLayout)
                    val constraintSet = ConstraintSet()
                    constraintSet.load(this@RunFragment.context, R.layout.run_layout_phase_2)
                    constraintSet.applyTo(constraintLayout)
                    TransitionManager.beginDelayedTransition(constraintLayout)
                    constraintSet.applyTo(constraintLayout)

                }

                if (timeInSplit != 0) {
                    pace_current_view.setText("" + distanceInSplit / timeInSplit)
                }

            }
            else {
                if (timeInSplit >= 30) {
                    splits.add(Split(distanceInSplit, ticksInSplit, timeLastSplit, total_time.getTimeInSeconds(), false))

                    val pace_difference = kotlin.math.abs(splits.last().getPace() - averagePace())
                    if(pace_difference < .5){
                        splits.last().onTreadmill = true
                        timesOnTreadmill +=1
                    }


                    time_treadmill.setText("$timesOnTreadmill/${splits.size}")
                    pace_split_view.setText("" + distanceInSplit / timeInSplit)
                    pace_current_view.setText("STARTING A NEW SPLIT")

                    timeLastSplit = total_time.getTimeInSeconds()
                    distanceLastSplit = binder.getDistance()
                    timeInSplit = 0
                    ticksInSplit = 0

                    TransitionManager.beginDelayedTransition(constraintLayout)
                    val constraintSet = ConstraintSet()
                    constraintSet.load(this@RunFragment.context, R.layout.run_layout_phase_4)
                    constraintSet.applyTo(constraintLayout)
                    TransitionManager.beginDelayedTransition(constraintLayout)
                    constraintSet.applyTo(constraintLayout)

                }
                else if (timeInSplit != 0) {
                    pace_current_view.setText("" + distanceInSplit / timeInSplit)
                }




            }
        }
    }

    fun Chronometer.getTimeInSeconds(): Int{
        val time = SystemClock.elapsedRealtime() - this.base

        return time.toInt()/1000
    }

    fun averagePace(): Double{
        if(splits.size >= 2){
            return splits[0].getPace() + splits[1].getPace()/2
        }
        else{
            return -1.0
        }
    }

    data class Split(val distance: Int, val numTicks: Int, val startTime: Int, val endTime: Int, var onTreadmill: Boolean){
        fun getPace(): Double{
            return (distance*1.0)/(endTime-startTime)
        }


    }

}