package com.example.fse_project.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.fse_project.data.local.database.entities.ChargerEntity
import com.example.fse_project.data.local.database.entities.ReservationEntity
import com.example.fse_project.data.local.database.entities.StationEntity
import com.example.fse_project.data.local.database.entities.UserEntity
import com.example.fse_project.data.local.database.entities.VehicleEntity
import com.example.fse_project.data.local.database.entities.WalletEntity

@TypeConverters(DataConverters::class)
@Database(
    entities = [
        ChargerEntity::class, ReservationEntity::class,
        StationEntity::class, UserEntity::class,
        VehicleEntity::class, WalletEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase(){

    abstract fun appDao() : AppDao
}