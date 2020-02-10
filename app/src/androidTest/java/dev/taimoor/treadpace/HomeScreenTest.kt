package dev.taimoor.treadpace

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.taimoor.treadpace.runFragment.RunFragment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @Test
    fun testNavigationToRunScreen() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.mobile_navigation)

        val homeScenario = launchFragmentInContainer<HomeFragment>()

//        homeScenario.onFragment { fragment ->
//            Navigation.setViewNavController(fragment.requireView(), navController)
//        }
//
//        onView(ViewMatchers.withId(R.id.fab)).perform(ViewActions.click())
//        assertEquals(R.id.runFragment, navController.currentDestination?.id)

    }
}