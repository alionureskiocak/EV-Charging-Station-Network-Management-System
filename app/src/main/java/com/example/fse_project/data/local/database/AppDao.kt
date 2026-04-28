package com.example.fse_project.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.fse_project.data.local.database.entities.ChargerEntity
import com.example.fse_project.data.local.database.entities.ChargerStatus
import com.example.fse_project.data.local.database.entities.FavoriteEntity
import com.example.fse_project.data.local.database.entities.ReservationEntity
import com.example.fse_project.data.local.database.entities.ReservationStatus
import com.example.fse_project.data.local.database.entities.StationEntity
import com.example.fse_project.data.local.database.entities.UserEntity
import com.example.fse_project.data.local.database.entities.VehicleEntity
import com.example.fse_project.data.local.database.entities.WalletEntity
import com.example.fse_project.data.local.database.relations.ReservationWithDetails
import com.example.fse_project.data.local.database.relations.StationWithChargers
import com.example.fse_project.data.local.database.relations.UserWithVehiclesAndWallet
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    /////////////// USER //////////////////////////

    @Insert
    suspend fun insertUser(userEntity: UserEntity): Long

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    suspend fun login(email : String, password : String) : UserEntity?

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: Long)

    @Query("SELECT * FROM users WHERE id = :userId")
     fun getUserById(userId: Long): Flow<UserWithVehiclesAndWallet?>

    @Query("SELECT * FROM users")
    fun getUsers(): Flow<List<UserEntity>>

    @Transaction
    @Query("SELECT * FROM users")
    fun getUsersWithVehiclesAndWallet(): Flow<List<UserWithVehiclesAndWallet>>


    /////////////// VEHICLE //////////////////////////

    @Insert
    suspend fun insertVehicle(vehicleEntity: VehicleEntity): Long

    @Query("DELETE FROM vehicles WHERE id = :vehicleId")
    suspend fun deleteVehicle(vehicleId: Long)

    @Query("SELECT * FROM vehicles WHERE id = :vehicleId")
    suspend fun getVehicleById(vehicleId: Long): VehicleEntity

    @Query("SELECT * FROM vehicles")
    fun getVehicles(): Flow<List<VehicleEntity>>

    @Query("SELECT * FROM vehicles WHERE ownerId = :userId")
    fun getVehiclesByUserId(userId: Long): Flow<List<VehicleEntity>>


    /////////////// WALLET //////////////////////////

    @Insert
    suspend fun insertWallet(walletEntity: WalletEntity)

    @Query("DELETE FROM wallets WHERE userId = :userId")
    suspend fun deleteWallet(userId: Long)

    @Query("UPDATE wallets SET balance = :balance WHERE userId = :userId")
    suspend fun updateWallet(userId: Long, balance: Double)

    @Query("SELECT * FROM wallets WHERE userId = :userId")
    suspend fun getWalletByUserId(userId: Long): WalletEntity

    @Query("SELECT * FROM wallets")
    fun getWallets(): Flow<List<WalletEntity>>


    /////////////// STATION //////////////////////////

    @Insert
    suspend fun insertStation(stationEntity: StationEntity): Long

    @Query("DELETE FROM stations WHERE id = :stationId")
    suspend fun deleteStation(stationId: Long)

    @Query("SELECT * FROM stations WHERE id = :stationId")
    suspend fun getStationById(stationId: Long): StationEntity

    @Query(
        "SELECT s.*, c.* FROM stations s" +
                " INNER JOIN chargers c ON " +
                "s.id = c.stationOwnerId " +
                "WHERE c.id = :chargerId"
    )
    suspend fun getStationByChargerId(chargerId: Long): StationEntity

    /////////////// FAVORITES //////////////////////////

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavorites(favoriteEntity: FavoriteEntity)

    @Delete
    suspend fun removeFavorites(id : Long)

    @Query("""
        SELECT stations.* FROM stations
         INNER JOIN favorites ON stations.id = favorites.stationId
         WHERE favorites.userId = :userId
    """)
    fun getFavoriteStationsByUser(userId : Long) : Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS (SELECT 1 FROM favorites WHERE userId = :userId AND stationId = :stationId)")
    fun isStationFavorite(userId : Long, stationId : Long) : Flow<Boolean>

    @Query("SELECT * FROM stations")
    fun getStations(): Flow<List<StationEntity>>

    @Transaction
    @Query("SELECT * FROM stations")
    fun getStationWithChargers(): Flow<List<StationWithChargers>>


    /////////////// CHARGER //////////////////////////

    @Insert
    suspend fun insertCharger(chargerEntity: ChargerEntity): Long

    @Query("DELETE FROM chargers WHERE id = :chargerId")
    suspend fun deleteCharger(chargerId: Long)

    @Query("SELECT * FROM chargers WHERE id = :chargerId")
    suspend fun getChargerById(chargerId: Long): ChargerEntity

    @Query("SELECT * FROM chargers WHERE stationOwnerId = :stationId")
    fun getChargersByStation(stationId: Long): Flow<List<ChargerEntity>>

    @Query("SELECT * FROM chargers")
    fun getChargers(): Flow<List<ChargerEntity>>

    @Query("UPDATE chargers SET chargerStatus = :newStatus WHERE id = :chargerId")
    suspend fun updateChargerStatus(chargerId : Long, newStatus : ChargerStatus)


    ///////////// RESERVATION ////////////////////////////

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReservation(reservationEntity: ReservationEntity): Long

    @Query("DELETE FROM reservations WHERE id = :reservationId")
    suspend fun deleteReservation(reservationId: Long)

    @Query("UPDATE reservations SET status = :status WHERE id = :resId")
    suspend fun updateReservationStatus(resId: Long, status: ReservationStatus)

    @Transaction
    @Query("SELECT * FROM reservations WHERE id = :reservationId")
    suspend fun getReservationDetailsById(reservationId: Long): ReservationWithDetails

    @Transaction
    @Query("SELECT * FROM reservations WHERE userId = :userId")
    fun getReservationDetailsByUserId(userId: Long): Flow<List<ReservationWithDetails>>

    @Transaction
    @Query("SELECT * FROM reservations WHERE vehicleId = :vehicleId")
    fun getReservationDetailsByVehicleId(vehicleId: Long): Flow<List<ReservationWithDetails>>

    @Transaction
    @Query("SELECT * FROM reservations WHERE chargerId = :chargerId")
    fun getReservationDetailsByChargerId(chargerId: Long): Flow<List<ReservationWithDetails>>

    @Transaction
    @Query("SELECT * FROM reservations")
    fun getAllReservations(): Flow<List<ReservationWithDetails>>

}