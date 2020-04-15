package dev.taimoor.treadpace.settings

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import androidx.activity.addCallback
import androidx.fragment.app.Fragment


import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import androidx.preference.*
import dev.taimoor.treadpace.R
import dev.taimoor.treadpace.Util
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : PreferenceFragmentCompat() {
    val entries = arrayOf("Miles", "Kilometers")

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        setHasOptionsMenu(true)
        val context = preferenceManager.context
        val screen = preferenceManager.createPreferenceScreen(context)




        val runSettingsCategory = PreferenceCategory(context)
        runSettingsCategory.key = "run_settings_category"
        runSettingsCategory.title = "Run Settings"
        screen.addPreference(runSettingsCategory)


        val unitsDialog = ListPreference(context)
        unitsDialog.key = "units"
        unitsDialog.title = "Select desired units"
        unitsDialog.setDefaultValue("Miles")

        unitsDialog.entries = entries
        unitsDialog.entryValues = arrayOf("mi", "km")

        runSettingsCategory.addPreference(unitsDialog)

//        val callback = requireActivity().onBackPressedDispatcher.addCallback(this){
//            findNavController().navigate(R.id.global_go_home)
//            Log.i(Util.myTag, "back pressed ye heard")
//        }
//        callback.isEnabled = true




        this.activity?.actionBar?.setDisplayHomeAsUpEnabled(true)



//
//        val notificationPreference = SwitchPreferenceCompat(context)
//        notificationPreference.key = "notifications"
//        notificationPreference.title = "Enable message notifications"
//
//        val notificationCategory = PreferenceCategory(context)
//        notificationCategory.key = "notifications_category"
//        notificationCategory.title = "Notifications"
//        screen.addPreference(notificationCategory)
//        notificationCategory.addPreference(notificationPreference)
//
//        val feedbackPreference = Preference(context)
//        feedbackPreference.key = "feedback"
//        feedbackPreference.title = "Send feedback"
//        feedbackPreference.summary = "Report technical issues or suggest new features"
//
//        val helpCategory = PreferenceCategory(context)
//        helpCategory.key = "help"
//        helpCategory.title = "Help"
//        screen.addPreference(helpCategory)
//        helpCategory.addPreference(feedbackPreference)



        preferenceScreen = screen
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            findNavController().navigate(R.id.global_go_home)
        }
        return true
    }



}