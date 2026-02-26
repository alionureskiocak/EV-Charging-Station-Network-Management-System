package com.example.fse_project.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.fse_project.data.local.database.entity.ChargerEntity
import com.example.fse_project.data.local.database.entity.ReservationEntity
import com.example.fse_project.data.local.database.entity.StationEntity
import com.example.fse_project.data.local.database.entity.UserEntity
import com.example.fse_project.data.local.database.entity.VehicleEntity
import com.example.fse_project.data.local.database.entity.WalletEntity

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