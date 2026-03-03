package com.example.fse_project.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.fse_project.data.local.database.entities.ChargerEntity
import com.example.fse_project.data.local.database.entities.ReservationEntity
import com.example.fse_project.data.local.database.entities.ReservationStatus
import com.example.fse_project.data.local.database.entities.StationEntity
import com.example.fse_project.data.local.database.entities.UserEntity
import com.example.fse_project.data.local.database.entities.VehicleEntity
import com.example.fse_project.data.local.database.entities.WalletEntity
import com.example.fse_project.data.local.database.relations.StationWithChargers
import com.example.fse_project.data.local.database.relations.UserWithVehiclesAndWallet
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    /////////////// USER //////////////////////////

    @Insert
    suspend fun insertUser(userEntity: UserEntity) : Long

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId : Int)

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId : Int) : UserEntity

    @Query("SELECT * FROM users")
    fun getUsers() : Flow<List<UserEntity>>

    @Transaction
    @Query("SELECT * FROM users")
    fun getUsersWithVehiclesAndWallet() : Flow<List<UserWithVehiclesAndWallet>>


    /////////////// VEHICLE //////////////////////////

    @Insert
    suspend fun insertVehicle(vehicleEntity: VehicleEntity) : Long

    @Query("DELETE FROM vehicles WHERE id = :vehicleId")
    suspend fun deleteVehicle(vehicleId : Int)

    @Query("SELECT * FROM vehicles WHERE id = :vehicleId")
    suspend fun getVehicleById(vehicleId : Int) : VehicleEntity

    @Query("SELECT * FROM vehicles")
    fun getVehicles() : Flow<List<VehicleEntity>>

    @Query("SELECT * FROM vehicles WHERE ownerId = :userId")
    fun getVehiclesByUserId(userId : Int) : Flow<List<VehicleEntity>>


    /////////////// WALLET //////////////////////////

    @Insert
    suspend fun insertWallet(walletEntity: WalletEntity)

    @Query("DELETE FROM wallets WHERE userId = :userId")
    suspend fun deleteWallet(userId : Int)

    @Query("UPDATE wallets SET balance = :balance WHERE userId = :userId")
    suspend fun updateWallet(userId: Int, balance : Double)

    @Query("SELECT * FROM wallets WHERE userId = :userId")
    suspend fun getWalletByUserId(userId : Int) : WalletEntity

    @Query("SELECT * FROM wallets")
    fun getWallets() : Flow<List<WalletEntity>>


    /////////////// STATION //////////////////////////

    @Insert
    suspend fun insertStation(stationEntity: StationEntity) : Long

    @Query("DELETE FROM stations WHERE id = :stationId")
    suspend fun deleteStation(stationId : Int)

    @Query("SELECT * FROM stations WHERE id = :stationId")
    suspend fun getStationById(stationId : Int) : StationEntity

    @Query("SELECT s.*, c.* FROM stations s" +
            " INNER JOIN chargers c ON " +
            "s.id = c.stationOwnerId " +
            "WHERE c.id = :chargerId")
    suspend fun getStationByChargerId(chargerId : Int) : StationEntity

    @Query("SELECT * FROM stations")
    fun getStations() : Flow<List<StationEntity>>

    @Transaction
    @Query("SELECT * FROM stations")
    fun getStationWithChargers() : Flow<List<StationWithChargers>>


    /////////////// CHARGER //////////////////////////

    @Insert
    suspend fun insertCharger(chargerEntity: ChargerEntity) : Long

    @Query("DELETE FROM chargers WHERE id = :chargerId")
    suspend fun deleteCharger(chargerId : Int)

    @Query("SELECT * FROM chargers WHERE id = :chargerId")
    suspend fun getChargerById(chargerId : Int) : ChargerEntity

    @Query("SELECT * FROM chargers WHERE stationOwnerId = :stationId")
    fun getChargersByStation(stationId : Int) : Flow<List<ChargerEntity>>

    @Query("SELECT * FROM chargers")
    fun getChargers() : Flow<List<ChargerEntity>>


    /////////////// RESERVATION //////////////////////////

    @Insert
    suspend fun insertReservation(reservationEntity: ReservationEntity) : Long

    @Query("DELETE FROM reservations WHERE id = :reservationId")
    suspend fun deleteReservation(reservationId : Int)

    @Query("UPDATE reservations SET status = :status WHERE id = :resId")
    suspend fun updateReservationStatus(resId : Int,status : ReservationStatus)

    @Query("SELECT * FROM reservations WHERE id = :reservationId")
    suspend fun getReservationById(reservationId : Int) : ReservationEntity

    @Query("SELECT * FROM reservations WHERE userId = :userId")
    fun getReservationsByUserId(userId : Int) : Flow<List<ReservationEntity>>

    @Query("SELECT * FROM reservations WHERE vehicleId = :vehicleId")
    fun getReservationsByVehicleId(vehicleId : Int) : Flow<List<ReservationEntity>>

    @Query("SELECT * FROM reservations WHERE chargerId = :chargerId")
    fun getReservationsByChargerId(chargerId : Int) : Flow<List<ReservationEntity>>

    @Query("SELECT * FROM reservations")
    fun getReservations() : Flow<List<ReservationEntity>>

}