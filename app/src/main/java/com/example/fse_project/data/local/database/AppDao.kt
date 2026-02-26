package com.example.fse_project.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.fse_project.data.local.database.entity.ChargerEntity
import com.example.fse_project.data.local.database.entity.ReservationEntity
import com.example.fse_project.data.local.database.entity.StationEntity
import com.example.fse_project.data.local.database.entity.UserEntity
import com.example.fse_project.data.local.database.entity.VehicleEntity
import com.example.fse_project.data.local.database.entity.WalletEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    /////////////// USER //////////////////////////

    @Insert
    suspend fun insertUser(userEntity: UserEntity)

    @Delete
    suspend fun deleteUser(userEntity: UserEntity)

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId : Int) : UserEntity

    @Query("SELECT * FROM users")
    fun getUsers() : Flow<List<UserEntity>>


    /////////////// VEHICLE //////////////////////////

    @Insert
    suspend fun insertVehicle(vehicleEntity: VehicleEntity)

    @Delete
    suspend fun deleteVehicle(vehicleEntity: VehicleEntity)

    @Query("SELECT * FROM vehicles WHERE id = :vehicleId")
    suspend fun getVehicleById(vehicleId : Int) : VehicleEntity

    @Query("SELECT * FROM vehicles")
    fun getVehicles() : Flow<List<VehicleEntity>>

    @Query("SELECT * FROM vehicles WHERE ownerId = :userId")
    suspend fun getVehicleByUserId(userId : Int) : VehicleEntity


    /////////////// WALLET //////////////////////////

    @Insert
    suspend fun insertWallet(walletEntity: WalletEntity)

    @Delete
    suspend fun deleteWallet(walletEntity: WalletEntity)

    @Update
    suspend fun updateWallet(userId: Int)

    @Query("SELECT * FROM wallets WHERE userId = :userId")
    suspend fun getWalletByUserId(userId : Int) : WalletEntity

    @Query("SELECT * FROM wallets")
    fun getWallets() : Flow<List<WalletEntity>>


    /////////////// STATION //////////////////////////

    @Insert
    suspend fun insertStation(stationEntity: StationEntity)

    @Delete
    suspend fun deleteStation(stationEntity: StationEntity)

    @Query("SELECT * FROM stations WHERE id = :stationId")
    suspend fun getStationById(stationId : Int) : StationEntity

    @Query("SELECT s.*, c.* FROM stations s" +
            " INNER JOIN chargers c ON " +
            "s.id = c.stationOwnerId " +
            "WHERE c.id = :chargerId")
    suspend fun getStationByChargerId(chargerId : Int) : StationEntity

    @Query("SELECT * FROM stations")
    fun getStations() : Flow<List<StationEntity>>


    /////////////// CHARGER //////////////////////////

    @Insert
    suspend fun insertCharger(chargerEntity: ChargerEntity)

    @Delete
    suspend fun deleteCharger(chargerEntity: ChargerEntity)

    @Query("SELECT * FROM chargers WHERE id = :chargerId")
    suspend fun getChargerById(chargerId : Int) : ChargerEntity

    @Query("SELECT * FROM chargers WHERE stationOwnerId = :stationId")
    fun getChargersByStation(stationId : Int) : Flow<List<ChargerEntity>>

    @Query("SELECT * FROM chargers")
    fun getChargers() : Flow<List<ChargerEntity>>


    /////////////// RESERVATION //////////////////////////

    @Insert
    suspend fun insertReservation(reservationEntity: ReservationEntity)

    @Delete
    suspend fun deleteReservation(reservationEntity: ReservationEntity)

    @Query("SELECT * FROM reservations WHERE id = :reservationId")
    suspend fun getReservationById(reservationId : Int) : ReservationEntity

    @Query("SELECT * FROM reservations WHERE userId = :userId")
    suspend fun getReservationsByUserId(userId : Int) : Flow<List<ReservationEntity>>

    @Query("SELECT * FROM reservations WHERE vehicleId = :vehicleId")
    suspend fun getReservationsByVehicleId(vehicleId : Int) : Flow<List<ReservationEntity>>

    @Query("SELECT * FROM reservations WHERE chargerId = :chargerId")
    suspend fun getReservationsByChargerId(chargerId : Int) : Flow<List<ReservationEntity>>

    @Query("SELECT * FROM reservations")
    fun getReservations() : Flow<List<ReservationEntity>>










}