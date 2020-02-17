package dev.taimoor.treadpace

import android.content.Context
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton


import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import dev.taimoor.treadpace.room.HomeViewModel
import dev.taimoor.treadpace.room.RunEntity
import dev.taimoor.treadpace.room.RunListAdapter
import kotlinx.android.synthetic.main.home_layout.*

class HomeFragment : Fragment() {


    val args : HomeFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        setHasOptionsMenu(true)

        return inflater.inflate(R.layout.home_layout, container, false)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = recyclerview
        val adapter = RunListAdapter(this.context as Context)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this.context as Context)

        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        homeViewModel.allRuns.observe(this, Observer { runs ->
            runs?.let { adapter.setRuns(it) }
        })


        Log.i(Util.myTag, "${args.test}")


        args.completedRun?.let {
            Log.i(Util.myTag, "Inserting a run")
            homeViewModel.insert(it)

        }



        val fab = view.findViewById<FloatingActionButton>(R.id.fab)



        val options = navOptions {
            anim {
                enter = R.anim.slide_in_right
                exit = R.anim.slide_out_left
                popEnter = R.anim.slide_in_left
                popExit = R.anim.slide_out_right
            }
        }

        fab?.setOnClickListener {
            val action = HomeFragmentDirections
                .actionHomeFragmentToRunFragment()
                .setFastestInterval(10000)
                .setInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setLocationRequest(createLocationRequest())
            findNavController().navigate(action)
        }

        fab?.show()
    }

    // from Google
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
            client = LocationServices.getSettingsClient(this.context as Context)
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
                    exception.startResolutionForResult(this.activity, 1)

                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }

        return locationRequest
    }



}