package dev.taimoor.treadpace

import androidx.fragment.app.testing.launchFragmentInContainer
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
        launchFragmentInContainer<HomeFragment>(null, R.style.AppTheme)

        Thread.sleep(2000)
    }

}