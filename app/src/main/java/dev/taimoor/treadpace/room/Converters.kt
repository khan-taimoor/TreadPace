package dev.taimoor.treadpace.room

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.taimoor.treadpace.data.RunInfo
import dev.taimoor.treadpace.data.Split
import java.lang.reflect.Type
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class Converters {

    private val gson = Gson()
    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME


    @TypeConverter
    fun stringToRunInfo(value: String): RunInfo {
        return gson.fromJson(value, RunInfo::class.java)
    }

    @TypeConverter
    fun runInfoToString(value: RunInfo): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun stringToArraySplit(value: String): Array<Split> {
        val listType: Type = object : TypeToken<Array<Split?>?>() {}.type
        return gson.fromJson(value, listType)
    }
    
    @TypeConverter
    fun arraySplitToString(value: Array<Split>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun stringToArrayLatLng(value: String): Array<LatLng> {
        val listType: Type = object : TypeToken<Array<LatLng?>?>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun arrayLatLngToString(value: Array<LatLng>): String {
        return gson.toJson(value)
    }

    // source: https://medium.com/androiddevelopers/room-time-2b4cf9672b98
    @TypeConverter
    fun toOffsetDateTime(value: String?): OffsetDateTime? {
        return value?.let {
            return formatter.parse(value, OffsetDateTime::from)
        }
    }

    @TypeConverter
    fun fromOffsetDateTime(date: OffsetDateTime?): String? {
        return date?.format(formatter)
    }


}