package dev.taimoor.treadpace.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.android.gms.maps.model.LatLng
import dev.taimoor.treadpace.RunInfo
import dev.taimoor.treadpace.Split
import org.jetbrains.annotations.NotNull
import java.time.OffsetDateTime
import java.util.*

@Entity(tableName = "RUN_TABLE")
@TypeConverters(Converters::class)
data class RunEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @NotNull val runInfo: RunInfo,
    @NotNull val splits: Array<Split>,
    @NotNull val points: Array<LatLng>,
    @NotNull val run_date: OffsetDateTime)

