package dev.taimoor.treadpace.postRunFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.taimoor.treadpace.settings.UnitSetting

class UnitSettingViewModelFactory(val unitSetting: UnitSetting) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(UnitSetting::class.java).newInstance(unitSetting)

    }
}