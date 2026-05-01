package com.example.fse_project.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reports")
data class ReportErrorEntity(
    @PrimaryKey val resId : Long,
    val userId : Long,
    val stationId : Long,
    val chargerId : Long,
    val report : Report,
    val description : String
)

enum class Report(val text : String){
    CABLE_DAMAGED("Kablo/Soket Hasarlı"),
    STATION_BLOCKED("İstasyon İşgal Edilmiş"),
    CHARGING_NOT_STARTING("Şarj Başlatılamadı"),
    PAYMENT_ISSUE("Ödeme/Bakiye Hatası"),
    WRONG_LOCATION("Yanlış Konum Bilgisi"),
}
