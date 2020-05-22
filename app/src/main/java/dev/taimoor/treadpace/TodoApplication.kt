package dev.taimoor.treadpace

import android.app.Application
import dev.taimoor.treadpace.room.RunRepository

/**
 * An application that lazily provides a repository. Note that this Service Locator pattern is
 * used to simplify the sample. Consider a Dependency Injection framework.
 *
 * Also, sets up Timber in the DEBUG BuildConfig. Read Timber's documentation for production setups.
 */
class TodoApplication : Application() {
    val runRepository: RunRepository
        get() = ServiceLocator.provideRunRepository(this)
}
