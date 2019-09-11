package dev.taimoor.treadpace

import android.app.Activity
import android.content.ClipData
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import androidx.navigation.ui.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.navigation_activity)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val host: NavHostFragment = supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment

        val navController = host.navController

        appBarConfiguration = AppBarConfiguration(navController.graph)





        setupActionBar(navController, appBarConfiguration)

        //setupNavigationMenu(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val dest: String = try {

                resources.getResourceName(destination.id)
            } catch (e: Resources.NotFoundException) {
                Integer.toString(destination.id)
            }

            Toast.makeText(this@MainActivity, "Navigated to $dest",
                Toast.LENGTH_SHORT).show()
            Log.d("NavigationActivity", "Navigated to $dest")
        }


    }
    private fun setupActionBar(navController: NavController,
                               appBarConfig: AppBarConfiguration) {
       // This allows NavigationUI to decide what label to show in the action bar
       // By using appBarConfig, it will also determine whether to
       // show the up arrow or drawer menu icon
        setupActionBarWithNavController(navController, appBarConfig)
    }

/*

    //TODO: put in if I decide to go with the bottom nav and split screen.
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val retValue = super.onCreateOptionsMenu(menu)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        // The NavigationView already has these same navigation items, so we only add
        // navigation items to the menu here if there isn't a NavigationView
        if (navigationView == null) {
            menuInflater.inflate(R.menu.menu_main, menu)

            return true
        }
        return retValue
    }

*/

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
//        // Allows NavigationUI to support proper up navigation or the drawer layout
//        // drawer menu, depending on the situation
        return findNavController(R.id.my_nav_host_fragment).navigateUp(appBarConfiguration)
//    }
        // TODO END STEP 9.7
    }
}
