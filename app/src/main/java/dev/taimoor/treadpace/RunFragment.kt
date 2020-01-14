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
import androidx.navigation.fragment.findNavController
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
    private var locationRequest : LocationRequest? = null
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


        start_run_button.setOnClickListener {
            serviceIntent.putExtra("locationRequest", locationRequest)
            ContextCompat.startForegroundService(context as Context, serviceIntent)
            start_run_layout.visibility = View.GONE
            in_run_layout.visibility = View.VISIBLE
        }

        end_run_button.setOnClickListener {
            this.activity?.applicationContext?.stopService(serviceIntent)
            val action = RunFragmentDirections.actionRunFragmentToPostRunFragment()
            findNavController().navigate(action)
        }


    }

    override fun onPause() {
        super.onPause()
        Log.i("RunFragment", "On Pause")
    }
}