package dev.taimoor.treadpace.homeFragment

import android.content.Context
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.navOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton


import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import dev.taimoor.treadpace.homeFragment.HomeFragmentDirections
import dev.taimoor.treadpace.R
import dev.taimoor.treadpace.Util
import dev.taimoor.treadpace.room.*
import kotlinx.android.synthetic.main.home_layout.*

class HomeFragment : Fragment() {




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




        val string = getString(R.string.preference_file)
        val sharedPref = this.activity?.getPreferences(Context.MODE_PRIVATE)


        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.activity?.applicationContext)

        Log.i(Util.myTag, "VALUE FROM SHAREDMAP: ${sharedPreferences.getString("units", "")}")
        
        
        val recyclerView = recyclerview
        val adapter = RunListAdapter(this.context as Context)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this.context as Context)


        val homeViewModel by viewModels<HomeViewModel>{
            HomeViewModelFactory(RunRepository(RunRoomDatabase.getDatabase(this.requireActivity().application, lifecycleScope).runDao()))
        }
        //val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        homeViewModel.allRuns.observe(viewLifecycleOwner, Observer { runs ->
            runs?.let { adapter.setRuns(it) }
        })

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
            val action = HomeFragmentDirections.actionHomeFragmentToRunFragment()
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