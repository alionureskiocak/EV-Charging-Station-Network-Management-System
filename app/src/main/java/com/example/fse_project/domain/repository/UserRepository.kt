package com.example.fse_project.domain.repository

import com.example.fse_project.domain.model.User
import com.example.fse_project.domain.model.Vehicle
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    //user,vehicle,wallet

    suspend fun createUser(user: User) : Long
    suspend fun deleteUser(userId : Int)
    suspend fun getUserProfile(userId : Int) : User
    suspend fun getAllUsers() : Flow<List<User>>

    fun getUsersWithVehiclesAndWallet() : Flow<List<User>>

    suspend fun createVehicle(vehicle: Vehicle) : Long
    suspend fun deleteVehicle(vehicleId : Int)
    suspend fun getVehicleById(vehicleId : Int) : Vehicle
    fun getAllVehicles() : Flow<List<Vehicle>>
    fun getVehiclesByUserId(userId : Int) : Flow<List<Vehicle>>

    suspend fun updateWallet(userId: Int, balance : Double)

}