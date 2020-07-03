package dev.taimoor.treadpace.homeFragment

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.floatingactionbutton.FloatingActionButton


import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.onNavDestinationSelected
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import dev.taimoor.treadpace.R
import dev.taimoor.treadpace.RunRepoApplication
import dev.taimoor.treadpace.Util
import dev.taimoor.treadpace.room.*
import kotlinx.android.synthetic.main.home_layout.*

class HomeFragment : Fragment() {


    var bool : Boolean = false



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


        val safeArgs: HomeFragmentArgs by navArgs()

        val message = safeArgs.message
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.activity?.applicationContext)

        Log.i(Util.myTag, "VALUE FROM SHAREDMAP: ${sharedPreferences.getString("units", "")}")

        val requestPermissionLauncher =
            this.activity?.registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // TODO: Navigate to activity, also confirm that this block of code gets reached
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            }

        val recyclerView = recyclerview
        val adapter = RunListAdapter(this.context as Context)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this.context as Context)


        val homeViewModel by viewModels<HomeViewModel>{
            HomeViewModelFactory(((requireContext().applicationContext as RunRepoApplication).runRepository))
        }

        if(message == Util.delete_run){
            val mySnackbar = Snackbar.make(coordinator, R.string.delete_run_message, Snackbar.LENGTH_SHORT)
            mySnackbar.show()
        }

        if(message == Util.save_run){
            val mySnackbar = Snackbar.make(coordinator, R.string.save_run_message, Snackbar.LENGTH_SHORT)
            mySnackbar.show()
        }

        homeViewModel.allRuns.observe(viewLifecycleOwner, Observer { runs ->
            runs?.let { adapter.setRuns(it) }
        })

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)

        this.bool = false


//        val options = navOptions {
//            anim {
//                enter = R.anim.slide_in_right
//                exit = R.anim.slide_out_left
//                popEnter = R.anim.slide_in_left
//                popExit = R.anim.slide_out_right
//            }
//        }




        fab?.setOnClickListener {
            createLocationRequest()
        }

        fab?.show()
    }

    // from Google
    fun createLocationRequest(): Pair<LocationRequest?, Boolean> {

        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        var builder: LocationSettingsRequest.Builder
        var client: SettingsClient

        locationRequest.let {
            builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest).setAlwaysShow(true)
        }

        this.let {
            client = LocationServices.getSettingsClient(this.context as Context)
        }

        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            this@HomeFragment.bool = true
            //Toast.makeText(this@HomeFragment.context, "success", Toast.LENGTH_SHORT).show()



            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED -> {

                    val action = HomeFragmentDirections.actionHomeFragmentToRunFragment()
                        .setFastestInterval(10000)
                        .setInterval(5000)
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setLocationRequest(locationRequest)

                    findNavController().navigate(action)
                }
                // Sometimes this doesn't show up if permissions weren't set?
                (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                        || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
                        || shouldShowRequestPermissionRationale(Manifest.permission.FOREGROUND_SERVICE)) -> {

                    val blder: AlertDialog.Builder? = this.activity?.let {
                        AlertDialog.Builder(it)
                    }

                    blder?.setTitle("Request location permission")

                    blder?.setNegativeButton("Deny") { dialog, id ->
                        Log.i(Util.myTag, "Exit run without saving")
                    }

                    blder?.setPositiveButton("Allow") { dialog, id ->
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            1
                        )
                    }

                    blder?.show()
                }
                else -> {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        1
                    )
                }
            }
//                ActivityResultContracts.RequestPermission
//
//                shouldShowRequestPermissionRationale(PermissionRequest.)
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //ActivityCompat.requestPermissions(requireActivity(), )

        }



        task.addOnFailureListener { exception ->

            //Toast.makeText(this@HomeFragment.context, "FAILURE", Toast.LENGTH_SHORT).show()
            Log.i(Util.myTag, "failed")
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                this@HomeFragment.bool = false
                try {
                    // TODO: show a custom message? something more informative
                    //exception.startResolutionForResult(this.activity, 1)
                    startIntentSenderForResult(exception.resolution.intentSender, 0, null, 0, 0, 0, null);
                } catch (sendEx: IntentSender.SendIntentException) {
                }
            }
        }







        return Pair(locationRequest, this@HomeFragment.bool)


    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(findNavController())
                || super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()

        inflater.inflate(R.menu.menu_main, menu)

    }
}