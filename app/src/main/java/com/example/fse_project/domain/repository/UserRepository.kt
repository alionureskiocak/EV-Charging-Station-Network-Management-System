package com.example.fse_project.domain.repository

import androidx.room.Insert
import com.example.fse_project.data.local.database.entities.AdminEntity
import com.example.fse_project.data.local.database.entities.OperatorEntity
import com.example.fse_project.domain.model.Admin
import com.example.fse_project.domain.model.Operator
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

    suspend fun loginAdmin(email : String, password : String) : Admin?
    suspend fun loginOperator(email : String, password : String) : Operator?

    suspend fun insertAdmin(admin: Admin): Long
    suspend fun insertOperator(operator: Operator): Long


    fun getUsers() : Flow<List<User>>

    suspend fun createVehicle(vehicle: Vehicle) : Long
    suspend fun deleteVehicle(vehicleId : Long)
    suspend fun getVehicleById(vehicleId : Long) : Vehicle
    fun getAllVehicles() : Flow<List<Vehicle>>
    fun getVehiclesByUserId(userId : Long) : Flow<List<Vehicle>>

    suspend fun updateWallet(userId: Long, balance : Double)

}