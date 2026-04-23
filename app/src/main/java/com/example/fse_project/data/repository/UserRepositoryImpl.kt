package com.example.fse_project.data.repository

import com.example.fse_project.data.local.database.AppDao
import com.example.fse_project.data.local.database.entities.WalletEntity
import com.example.fse_project.data.mapper.toDomain
import com.example.fse_project.data.mapper.toEntity
import com.example.fse_project.domain.model.User
import com.example.fse_project.domain.model.Vehicle
import com.example.fse_project.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val dao : AppDao
) : UserRepository{
    override suspend fun login(
        email: String,
        password: String
    ): User? {
        val userEntity = dao.login(email,password)
        return userEntity?.toDomain()
    }

    override suspend fun createUser(user: User): Long {
        val id = dao.insertUser(user.toEntity())
        val wallet = WalletEntity(id,0.0)
        dao.insertWallet(wallet)
        return id
    }

    override fun getUsers(): Flow<List<User>> {
        return dao.getUsersWithVehiclesAndWallet().map {
            it.map {
                it.toDomain()
            }
        }
    }

    override suspend fun deleteUser(userId: Long) {
        dao.deleteUser(userId)
    }

    override suspend fun getUserProfile(userId: Long): Flow<User?> {
        return dao.getUserById(userId).map { it?.toDomain() }

    }

    override suspend fun getAllUsers(): Flow<List<User>>{
       val users = dao.getUsers()
           .map {
               it.map { it.toDomain() }
           }
        return users
    }

    override suspend fun createVehicle(vehicle: Vehicle): Long {
        return dao.insertVehicle(vehicle.toEntity())
    }

    override suspend fun deleteVehicle(vehicleId : Long) {
        dao.deleteVehicle(vehicleId)
    }

    override suspend fun getVehicleById(vehicleId: Long): Vehicle {
        return dao.getVehicleById(vehicleId).toDomain()
    }

    override fun getAllVehicles(): Flow<List<Vehicle>> {
        return dao.getVehicles().map {
            it.map { it.toDomain() }
        }
    }

    override fun getVehiclesByUserId(userId: Long): Flow<List<Vehicle>> {
        return dao.getVehiclesByUserId(userId).map {
            it.map {
                it.toDomain()
            }
        }
    }

    override suspend fun updateWallet(userId: Long, balance: Double) {
        dao.updateWallet(userId,balance)
    }
}