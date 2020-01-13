package dev.taimoor.treadpace

import android.app.IntentService
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task

class RunLocationService: Service() {


    //lateinit var intent : Intent
    lateinit var pendingIntent: PendingIntent
    lateinit var notification: Notification
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var locationRequest : LocationRequest? = null



    fun makeToast(){
        Toast.makeText(applicationContext, "Toast From Service", Toast.LENGTH_LONG).show()

    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        //val interval = intent?.getIntExtra("interval", -1)
        //val fastestInterval = intent?.getIntExtra("fastestInterval", -1)
        //val priority = intent?.getIntExtra("priority",-1)

        if(intent?.action == "STOP SERVICE"){
            fusedLocationClient.removeLocationUpdates(locationCallback)
            stopSelf()

        }

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
        stopSelf()

    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }


    fun startRun(locationRequest: LocationRequest){
        locationRequest?.let {
            this.startLocationUpdates(it)
        }
    }








    private fun startLocationUpdates(locationRequest: LocationRequest) {
        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }



    override fun onCreate(){

        val intent = Intent(this, MainActivity::class.java).apply {
            this.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        }

        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val builder = NotificationCompat.Builder(this as Context, "60")
        builder.setContentTitle("Notification Title")
            .setContentText("Notification text")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notification = builder.build()

        this.locationCallback = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return

                for(location in locationResult.locations){
                    Log.i("LocationService", "LATITUDE: ${location.latitude}\t LONGITUDE:${location.longitude}")
                    Toast.makeText(applicationContext, "LATITUDE: ${location.latitude}\t LONGITUDE:${location.longitude}", Toast.LENGTH_LONG).show()
                }
            }
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
    }


    /*
    fun createLocationRequest(): LocationRequest? {
        val locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        var builder: LocationSettingsRequest.Builder? = null
        var client: SettingsClient? = null

        locationRequest?.let {
            builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        }

        this?.let {
            client = LocationServices.getSettingsClient(this)
        }

        var task: Task<LocationSettingsResponse>? = null
        Log.d("In run fragment", "Making sure I get here.")

        Util.safeLet(builder, client) { b, c ->
            task = c.checkLocationSettings(b.build())
        }


        task?.addOnSuccessListener { locationSettingsResponse ->
            //Toast.makeText(context, "location settings request MADE", Toast.LENGTH_LONG).show()
        }

        task?.addOnFailureListener { exception ->

            //Toast.makeText(context, "location settings request FAILED", Toast.LENGTH_LONG).show()
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().

                    exception.startResolutionForResult(applicationContext, 1)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }

        return locationRequest
    }

     */


}
