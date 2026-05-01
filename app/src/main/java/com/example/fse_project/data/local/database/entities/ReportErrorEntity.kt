package com.example.fse_project.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reports")
data class ReportErrorEntity(
    @PrimaryKey(autoGenerate = true) val id : Long,
    val userId : Long,
    val stationId : Long,
    val report : Report,
    val description : String
)

enum class Report(val text : String){
    CABLE_DAMAGED("Soket Hasarlı"),
    STATION_BLOCKED("İstasyon Bloklanmış"),
    CHARGING_NOT_STARTING("Şarj Başlatılamadı"),
    PAYMENT_ISSUE("Ödeme Hatası"),
    WRONG_LOCATION("Yanlış Konum Bilgisi"),
}
