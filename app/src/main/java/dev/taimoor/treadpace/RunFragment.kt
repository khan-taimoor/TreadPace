package dev.taimoor.treadpace

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        setHasOptionsMenu(true)

        val safeArgs: RunFragmentArgs by navArgs()
        this.locationRequest = safeArgs.locationRequest
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


        //val bndl = bundleOf(Pair("locationRequest", locationRequest)
         //   , Pair("fusedLocationClient", Bundle.putPfusedLocationClient))

        //val bundle = Bundle()
        //bundle.putParcelable("locationRequest", locationRequest)
        //bundle.putParcelable("fusedLocationClient", fusedLocationClient as Parcelable?)

        //serviceIntent.putExtras(bundle)
        ContextCompat.startForegroundService(context as Context, serviceIntent)

        //val binder = service as RunLocationService.LocalBinder

        /*
        val intent = Intent(this.context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        }

        val pendingIntent = PendingIntent.getActivity(this.context, 0, intent, 0)


        /*
        val pending = NavDeepLinkBuilder(this.context as Context)
            .setGraph(R.navigation.mobile_navigation)
            .setDestination(R.id.runFragment)
            .createPendingIntent()
         */


        val builder = NotificationCompat.Builder(this.context as Context, "60")
        builder.setContentTitle("Notification Title")
            .setContentText("Notification text")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)



        with(NotificationManagerCompat.from(this.activity as Context)){
            notify(60, builder.build())
        }
        Log.i("RunFragment", "Notification Created?")

        */




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