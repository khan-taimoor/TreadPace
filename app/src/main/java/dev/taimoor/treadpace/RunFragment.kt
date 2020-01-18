package dev.taimoor.treadpace

import android.app.Service
import android.content.*
import android.graphics.Color
import android.graphics.Point
import android.location.Location
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.run_layout.*
import android.os.SystemClock
import com.google.android.gms.maps.model.Polyline
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

            Intent(this.activity, RunLocationService::class.java).also { intent ->
                this.activity?.bindService(intent, connection, Context.BIND_IMPORTANT)
            }
            ContextCompat.startForegroundService(context as Context, serviceIntent)
            before_run.visibility = View.GONE
            during_run.visibility = View.VISIBLE

            total_time.base = SystemClock.elapsedRealtime()
            total_time?.start()


            total_time?.setOnChronometerTickListener {
                val time = SystemClock.elapsedRealtime() - it.base
                val h = (time / 3600000).toInt()
                val m = ((time - h * 3600000)).toInt() / 60000
                val s = ((time - h * 3600000 - m * 60000)).toInt() / 1000

                //Log.i(Util.myTag, "$s")
            }



            val numba = 10
            polylineOptions = PolylineOptions().color(Color.RED).width(numba.toFloat())
            this.map?.addPolyline(polylineOptions)
        }


        end_run_button.setOnClickListener {
            val endServiceIntent = Intent(this.context, RunLocationService::class.java)
            this.activity?.applicationContext?.stopService(endServiceIntent)
            this.activity?.applicationContext?.unbindService(connection)
            val action = RunFragmentDirections.actionRunFragmentToPostRunFragment()
            total_time?.stop()
            binder.removeObserver()
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

    inner class PointsObserver : Observer{
        override fun update(observable: Observable?, p1: Any?) {
            polylineOptions.add(binder.getMostRecentLatLng())

            // do this a better way
            map?.clear()
            map?.addPolyline(polylineOptions)
            distance_view.setText("" + binder.getDistance())
            //distance_view.set

            Log.i(Util.myTag, "yeet${binder.getMostRecentLatLng()}")
        }
    }

}