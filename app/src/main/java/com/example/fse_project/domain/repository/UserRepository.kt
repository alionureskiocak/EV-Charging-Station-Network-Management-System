package com.example.fse_project.domain.repository

import com.example.fse_project.data.local.database.entity.UserEntity
import com.example.fse_project.data.local.database.entity.VehicleEntity
import com.example.fse_project.data.local.database.entity.WalletEntity
import com.example.fse_project.domain.model.User
import com.example.fse_project.domain.model.Vehicle
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    //user,vehicle,wallet

    suspend fun createUser(userEntity: UserEntity) : Long
    suspend fun deleteUser(userEntity: UserEntity)
    suspend fun getUserProfile(userId : Int) : User
    suspend fun getAllUsers() : Flow<List<User>>

    suspend fun createVehicle(vehicleEntity: VehicleEntity) : Long
    suspend fun deleteVehicle(vehicleEntity: VehicleEntity)
    suspend fun getVehicleById(vehicleId : Int) : Vehicle
    fun getAllVehicles() : Flow<List<VehicleEntity>>
    suspend fun getVehiclesByUserId(userId : Int) : Flow<List<VehicleEntity>>

    suspend fun updateWallet(userId: Int, balance : Double)

}