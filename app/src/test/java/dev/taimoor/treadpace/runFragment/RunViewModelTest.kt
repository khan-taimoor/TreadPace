package dev.taimoor.treadpace.runFragment

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.taimoor.treadpace.data.RunOrder
import dev.taimoor.treadpace.getOrAwaitValue
import dev.taimoor.treadpace.settings.UnitSetting
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.O_MR1])
@RunWith(AndroidJUnit4::class)
class RunViewModelTest{
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var runViewModel: RunViewModel

    @Before
    fun setup(){
        runViewModel = RunViewModel(UnitSetting.mi)
    }

    // LiveData that is binded to views in XML layouts
    @Test
    fun testTextOneTick(){
        val order = RunOrder.RunOrderBuilder()
        order.distance(1000).numberSplits(10).paceCurrentDouble(3.0)
        runViewModel.receiveRunOrder(order.build())

        val numberSplitsText = runViewModel.timesOnTreadmillText.getOrAwaitValue()
        assertThat(numberSplitsText, `is`("2/10"))

        // Conversion rounds str down to 0.
        val paceCurrentDoubleText = runViewModel.paceCurrentText.getOrAwaitValue()
        assertThat(paceCurrentDoubleText, `is`("0.00 mi/hr"))

        val distanceText = runViewModel.currentDistance.getOrAwaitValue()
        assertThat(distanceText, `is`("0.62 mi"))
    }

    @Test
    fun testDefaultValues(){
        val distanceText = runViewModel.currentDistance.getOrAwaitValue()
        assertThat(distanceText, `is`("0.00 mi"))
    }
}