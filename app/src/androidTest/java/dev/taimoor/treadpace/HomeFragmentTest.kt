package dev.taimoor.treadpace

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class HomeFragmentTest{

    @Test
    fun testHomeFragment(){

        launchFragmentInContainer<HomeFragment>(null, R.style.AppTheme)
    }

}