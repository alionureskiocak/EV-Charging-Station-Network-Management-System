package com.example.fse_project.domain.repository

import com.example.fse_project.domain.model.User
import com.example.fse_project.domain.model.Vehicle
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    //user,vehicle,wallet

    suspend fun createUser(user: User) : Long
    suspend fun deleteUser(userId : Long)
    suspend fun getUserProfile(userId : Long) : Flow<User?>
    suspend fun getAllUsers() : Flow<List<User>>
    suspend fun login(email : String, password : String) : User?

    fun getUsers() : Flow<List<User>>

    suspend fun createVehicle(vehicle: Vehicle) : Long
    suspend fun deleteVehicle(vehicleId : Long)
    suspend fun getVehicleById(vehicleId : Long) : Vehicle
    fun getAllVehicles() : Flow<List<Vehicle>>
    fun getVehiclesByUserId(userId : Long) : Flow<List<Vehicle>>

    suspend fun updateWallet(userId: Long, balance : Double)

}