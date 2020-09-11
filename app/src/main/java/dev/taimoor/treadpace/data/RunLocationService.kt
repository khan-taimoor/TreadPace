package dev.taimoor.treadpace.data

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import dev.taimoor.treadpace.MainActivity
import dev.taimoor.treadpace.R
import dev.taimoor.treadpace.Util
import java.util.*

class RunLocationService: Service() {

    lateinit var pendingIntent: PendingIntent
    lateinit var notification: Notification
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val thread = HandlerThread("Service")
    private lateinit var binder : RunServiceBinder
    private val points = ObservablePoints()


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val locationRequest = intent?.getParcelableExtra<LocationRequest>("locationRequest")

        locationRequest?.let {
            this.startRun(it)
        }

        startForeground(60, notification)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.i(Util.myTag, "From Ending")
        thread.quitSafely()
        stopSelf()

    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    // look into return value needed here
    override fun onUnbind(intent: Intent?): Boolean {
        Log.i(Util.myTag, "Unbinded service")
        return true
    }


    fun startRun(locationRequest: LocationRequest){
        locationRequest?.let {
            this.startLocationUpdates(it)
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates(locationRequest: LocationRequest) {
        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }

    override fun onCreate(){

        this.binder = RunServiceBinder()

        val intent = Intent(this, MainActivity::class.java).apply {
            this.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        }

        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val builder = NotificationCompat.Builder(this as Context, "60")
        builder.setContentTitle("Run in progress")
            .setContentText("Click for more info")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        thread.start()

        notification = builder.build()

        this.locationCallback = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return

                for(location in locationResult.locations){
                    Log.i("LocationService", "LATITUDE: ${location.latitude}\t LONGITUDE:${location.longitude}")
                    //Toast.makeText(applicationContext, "LATITUDE: ${location.latitude}\t LONGITUDE:${location.longitude}\n${locationResult.locations.size}", Toast.LENGTH_LONG).show()
                    points.addPoint(LatLng(location.latitude, location.longitude))


                    //Log.i(Util.myTag, "notifying observers\n${points.countObservers()}\n${points.observerSet}}")
                }
            }
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.applicationContext)
    }


    inner class RunServiceBinder : Binder(){
        fun addObserver(observer: Observer){
            this@RunLocationService.points.addObserver(observer)
        }

        fun removeObserver(){
            this@RunLocationService.points.deleteObservers()
        }

        fun getMostRecentLatLng(): LatLng{
            return this@RunLocationService.points.mostRecentLatLng()
        }

        fun getDistance(): Int{
            return this@RunLocationService.points.distance
        }

    }


    inner class ObservablePoints : Observable(){
        val points = mutableListOf<LatLng>()
        val latLngBounds = LatLngBounds.Builder()
        var distance = 0

        fun addPoint(point: LatLng){
            points.add(point)
            latLngBounds.include(point)

            if(points.size>=2){
                // TODO: Create extension function
                val mostRecent = Location("").also{
                    it.latitude = points[points.lastIndex].latitude
                    it.longitude = points[points.lastIndex].longitude
                }

                val secondMostRecent = Location("").also {
                    it.latitude = points[points.lastIndex-1].latitude
                    it.longitude = points[points.lastIndex-1].longitude

                }

                distance += mostRecent.distanceTo(secondMostRecent).toInt()
            }

            this.setChanged()
            this.notifyObservers()

        }
        fun mostRecentLatLng(): LatLng{
            return points[points.size - 1]
        }
    }



}
