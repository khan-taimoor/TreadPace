package dev.taimoor.treadpace

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import dev.taimoor.treadpace.data.RunInfo
import dev.taimoor.treadpace.homeFragment.HomeFragment
import dev.taimoor.treadpace.room.RunDao
import dev.taimoor.treadpace.room.RunEntity
import dev.taimoor.treadpace.room.RunRepository
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.OffsetDateTime

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import dev.taimoor.treadpace.homeFragment.HomeFragmentDirections
import dev.taimoor.treadpace.room.FakeAndroidTestRepository
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify


@MediumTest
@RunWith(AndroidJUnit4::class)
class HomeFragmentTest{

    private lateinit var repository: RunDao

    @Before
    fun initRepository(){
        repository = FakeAndroidTestRepository()
        ServiceLocator.runRepository = RunRepository(repository)
    }

    @After
    fun cleanUpDb() = runBlockingTest {
        ServiceLocator.resetRepository()
    }

    @Test
    fun testHomeFragment() = runBlockingTest {
        val run1 = RunEntity(RunInfo(0.0, 0, 0, 2, 2, LatLngBounds(LatLng(1.0, 1.0), LatLng(2.0, 2.0))), emptyArray(), emptyArray(), OffsetDateTime.now())
        val run2 = RunEntity(RunInfo(0.0, 0, 0, 2, 2, LatLngBounds(LatLng(1.0, 1.0), LatLng(2.0, 2.0))), emptyArray(), emptyArray(), OffsetDateTime.now())
        val run3 = RunEntity(RunInfo(0.0, 0, 0, 2, 2, LatLngBounds(LatLng(1.0, 1.0), LatLng(2.0, 2.0))), emptyArray(), emptyArray(), OffsetDateTime.now())

        repository.insert(run1)
        repository.insert(run2)
        repository.insert(run3)

        val scenario = launchFragmentInContainer<HomeFragment>(null, R.style.AppTheme)

        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

//        // WHEN - Click on the first list item
//        onView(withId(R.id.recyclerview))
//            .perform(
//                RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
//                hasDescendant(withText("0.0")), click()))
//
//
//        // THEN - Verify that we navigate to the first detail screen
//        verify(navController).navigate(
//            HomeFragmentDirections.actionHomeFragmentToPostRunFragment(run1)
//        )

    }

}