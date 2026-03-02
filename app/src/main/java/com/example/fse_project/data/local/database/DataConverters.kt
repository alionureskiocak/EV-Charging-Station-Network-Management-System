package com.example.fse_project.data.local.database
import androidx.room.TypeConverter
import com.example.fse_project.data.local.database.entities.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DataConverters {

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME) }
    }

    @TypeConverter
    fun fromConnectorType(value: ConnectorType): String {
        return value.name
    }

    @TypeConverter
    fun toConnectorType(value: String): ConnectorType {
        return ConnectorType.valueOf(value)
    }

    @TypeConverter
    fun fromChargerType(value: ChargerType): String {
        return value.name
    }

    @TypeConverter
    fun toChargerType(value: String): ChargerType {
        return ChargerType.valueOf(value)
    }


    @TypeConverter
    fun fromPowerOutput(value: PowerOutput): String {
        return value.name
    }

    @TypeConverter
    fun toPowerOutput(value: String): PowerOutput {
        return PowerOutput.valueOf(value)
    }


    @TypeConverter
    fun fromChargerStatus(value: ChargerStatus): String {
        return value.name
    }

    @TypeConverter
    fun toChargerStatus(value: String): ChargerStatus {
        return ChargerStatus.valueOf(value)
    }


    @TypeConverter
    fun fromReservationStatus(value: ReservationStatus): String {
        return value.name
    }

    @TypeConverter
    fun toReservationStatus(value: String): ReservationStatus {
        return ReservationStatus.valueOf(value)
    }
}