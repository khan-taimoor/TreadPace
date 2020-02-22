package dev.taimoor.treadpace.room

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.android.gms.maps.model.LatLng
import dev.taimoor.treadpace.RunInfo
import dev.taimoor.treadpace.Split
import kotlinx.android.parcel.Parcelize
import org.jetbrains.annotations.NotNull
import java.time.OffsetDateTime
import java.util.*

@Parcelize
@Entity(tableName = "RUN_TABLE")
@TypeConverters(Converters::class)
data class RunEntity(@NotNull val runInfo: RunInfo,
    @NotNull val splits: Array<Split>,
    @NotNull val points: Array<LatLng>,
    @NotNull val run_date: OffsetDateTime) : Parcelable{

        @PrimaryKey(autoGenerate = true)
        var id = 0
}

