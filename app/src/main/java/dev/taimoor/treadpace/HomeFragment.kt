package dev.taimoor.treadpace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton


import android.view.*
import android.widget.Button
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.google.android.material.navigation.NavigationView

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
            findNavController().navigate(R.id.runFragment, null, options)
        }

        fab?.show()
    }

}