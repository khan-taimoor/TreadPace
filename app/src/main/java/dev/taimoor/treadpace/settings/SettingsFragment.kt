package dev.taimoor.treadpace.settings

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment


import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import dev.taimoor.treadpace.R
import dev.taimoor.treadpace.Util
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        setHasOptionsMenu(true)

        return inflater.inflate(R.layout.fragment_settings, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView = list_view

        val myStringArray = arrayOf("hi", "k", "lol")



        val mySettingArray = arrayOf(
            Setting("Units", "Description", Runnable { Log.i(Util.myTag, "Adf Setting") }),
            Setting("Time Interval", "Description", Runnable { Log.i(Util.myTag, "Do stuff") }))


        val adapter = SettingsListAdapter(this.context as Context, mySettingArray)



        //val adapter = ArrayAdapter<Setting>(this.context as Context, R.layout.settings_item, R.id.settings_text_view, mySettingArray)
        //val adapter = ArrayAdapter<String>(this.context as Context, R.layout.settings_item, myStringArray)

        listView.adapter = adapter
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(findNavController())
                || super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }
}