package dev.taimoor.treadpace

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.run_layout.*







class RunFragment : Fragment(), OnMapReadyCallback {


    private var map: GoogleMap? = null
    private var mapView: MapView? = null
    private var uiSettings : UiSettings? = null
    private var currentLocation : Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var locationRequest : LocationRequest? = null
    lateinit var runLocationService: RunLocationService
    private var mBound = false


    private var fastestInterval = 5000
    private var interval = 10000
    private var priority = LocationRequest.PRIORITY_HIGH_ACCURACY




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



        val serviceIntent = Intent(this.context, RunLocationService::class.java)


        serviceIntent.putExtra("locationRequest", locationRequest)


        //bundle.putParcelable("locationRequest", locationRequest)

        ContextCompat.startForegroundService(context as Context, serviceIntent)





        end_run_button.setOnClickListener {

            val stopServiceIntent = Intent(this.context, RunLocationService::class.java)
            stopServiceIntent.setAction("STOP SERVICE")
            this.activity?.applicationContext?.stopService(serviceIntent)
            //runLocationService.stopService(true)
        }
    }

    override fun onPause() {
        super.onPause()
        Log.i("RunFragment", "On Pause")
    }




    // https://www.reddit.com/r/androiddev/comments/6nuxb8/null_checking_multiple_vars_in_kotlin/ reddit user gonemad16
    // take in 2 variables, p1 and p2, and a lambda
    // if both vars aren't none, execute the block
    fun <T1: Any, T2: Any, R: Any> safeLet(p1: T1?, p2: T2?, block: (T1, T2)->R?): R? {
        return if (p1 != null && p2 != null) block(p1, p2) else null
    }



}