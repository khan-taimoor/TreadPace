package dev.taimoor.treadpace.room

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import dev.taimoor.treadpace.data.RunInfo
import dev.taimoor.treadpace.getOrAwaitValue
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.IsEqual
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.time.OffsetDateTime

@Config(sdk = [Build.VERSION_CODES.O_MR1])
@RunWith(AndroidJUnit4::class)
class RunRepositoryTest(){
    private val run1 = RunEntity(RunInfo(0.0, 0, 0, 2, 2, LatLngBounds(LatLng(1.0, 1.0), LatLng(2.0, 2.0))), emptyArray(), emptyArray(), OffsetDateTime.now())
    private val run2 = RunEntity(RunInfo(0.0, 0, 0, 2, 2, LatLngBounds(LatLng(1.0, 1.0), LatLng(2.0, 2.0))), emptyArray(), emptyArray(), OffsetDateTime.now())
    private val run3 = RunEntity(RunInfo(0.0, 0, 0, 2, 2, LatLngBounds(LatLng(1.0, 1.0), LatLng(2.0, 2.0))), emptyArray(), emptyArray(), OffsetDateTime.now())
    private val runList = mutableListOf(run1, run2, run3)

    private lateinit var fakeDataSource: FakeDataSource


    // Class under test
    private lateinit var runRepository: RunRepository


    @Before
    fun createRepository() {
        fakeDataSource = FakeDataSource(runList)
        // Get a reference to the class under test
        runRepository = RunRepository(fakeDataSource)
    }

    @Test
    fun getTasks_requestsAllTasksFromRemoteDataSource() = runBlockingTest {
        // When tasks are requested from the tasks repository
        val runs = runRepository.getRuns().getOrAwaitValue()

        assertThat(runs.toMutableList(), IsEqual(runList))
    }

}