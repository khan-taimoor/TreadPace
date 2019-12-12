package dev.taimoor.treadpace

import android.content.Context
import android.location.Location
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.run_layout.*

class RunFragment : Fragment(), OnMapReadyCallback {


    private var map: GoogleMap? = null
    private var mapView: MapView? = null
    private var uiSettings : UiSettings? = null
    private var currentLocation : Location = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        setHasOptionsMenu(true)

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
    }


    override fun onMapReady(map: GoogleMap?) {
        MapsInitializer.initialize(context)
        this.map = map
        map?.isMyLocationEnabled = true

        uiSettings = map?.uiSettings
        uiSettings?.isRotateGesturesEnabled = false

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if(location != null){
                Toast.makeText(context, "yea", Toast.LENGTH_SHORT).show()
                //this.map?.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder().tilt(45.0f).zoom(15.0f).target(LatLng(location.latitude, location.longitude)).build()))
                currentLocation = location
                this.map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(currentLocation.latitude, currentLocation.longitude), 15.0f))
            }
        }



    }

}