package com.example.fse_project.data.repository

import com.example.fse_project.data.local.database.AppDao
import com.example.fse_project.data.local.database.entity.UserEntity
import com.example.fse_project.data.local.database.entity.VehicleEntity
import com.example.fse_project.data.local.database.entity.WalletEntity
import com.example.fse_project.data.mapper.toDomain
import com.example.fse_project.domain.model.User
import com.example.fse_project.domain.model.Vehicle
import com.example.fse_project.domain.repository.UserRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ViewModelScoped
class UserRepositoryImpl @Inject constructor(
    private val dao : AppDao
) : UserRepository{
    override suspend fun createUser(userEntity: UserEntity): Long {
        val wallet = WalletEntity(userEntity.id,0.0)
        dao.insertWallet(wallet)
        return dao.insertUser(userEntity)
    }

    override suspend fun deleteUser(userEntity: UserEntity) {
        dao.deleteUser(userEntity)
    }

    override suspend fun getUserProfile(userId: Int): User {
        return dao.getUserById(userId).toDomain()
    }

    override suspend fun getAllUsers(): Flow<List<User>>{
       val users = dao.getUsers()
           .map {
               it.map { it.toDomain() }
           }
        return users
    }

    override suspend fun createVehicle(vehicleEntity: VehicleEntity): Long {
        return dao.insertVehicle(vehicleEntity)
    }

    override suspend fun deleteVehicle(vehicleEntity: VehicleEntity) {
        dao.deleteVehicle(vehicleEntity)
    }

    override suspend fun getVehicleById(vehicleId: Int): Vehicle {
        return dao.getVehicleById(vehicleId).toDomain()
    }

    override fun getAllVehicles(): Flow<List<Vehicle>> {
        return dao.getVehicles().map {
            it.map { it.toDomain() }
        }
    }

    override fun getVehiclesByUserId(userId: Int): Flow<List<Vehicle>> {
        return dao.getVehiclesByUserId(userId).map {
            it.map {
                it.toDomain()
            }
        }
    }

    override suspend fun updateWallet(userId: Int, balance: Double) {
        dao.updateWallet(userId,balance)
    }
}