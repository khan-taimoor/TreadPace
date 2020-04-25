package dev.taimoor.treadpace

import com.google.android.gms.maps.model.LatLng
import com.nhaarman.mockitokotlin2.mock
import dev.taimoor.treadpace.data.TreadmillRun
import dev.taimoor.treadpace.runFragment.Phase
import dev.taimoor.treadpace.runFragment.RunViewModel
import dev.taimoor.treadpace.runFragment.Tick
import org.junit.Test

import org.junit.Assert.*

class TreadmillRunTest {

    val runViewModel = mock<RunViewModel>()
    val testRun = TreadmillRun(runViewModel)
    val latLngPoint = LatLng(0.0, 0.0)

    @Test
    fun testPhaseBefore(){
        assertEquals(Phase.BEFORE_RUN, testRun.currentPhase)
    }

    @Test
    fun testStartRun(){
        testRun.startRun()
        assertEquals(Phase.PHASE_ONE, testRun.currentPhase)
        assertEquals(0, testRun.splits.size)
    }

    @Test
    fun testPhaseTwoOneTick(){
        testRun.addPointToRun(Tick(30, 0, latLngPoint))
        assertEquals(Phase.PHASE_TWO, testRun.currentPhase)
        assertEquals(1, testRun.splits.size)
    }

    @Test
    fun testPhaseThreeTwoTicks(){
        testRun.addPointToRun(Tick(30, 0, latLngPoint))
        testRun.addPointToRun(Tick(60, 0, latLngPoint))
        testRun.addPointToRun(Tick(90, 0, latLngPoint))
        assertEquals(3, testRun.splits.size)
        assertEquals(Phase.PHASE_FOUR, testRun.currentPhase)
    }
}