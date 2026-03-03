package com.example.fse_project.data.local.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.fse_project.data.local.database.entities.UserEntity
import com.example.fse_project.data.local.database.entities.VehicleEntity
import com.example.fse_project.data.local.database.entities.WalletEntity
import com.example.fse_project.domain.model.Vehicle
import com.example.fse_project.domain.model.Wallet

data class UserWithVehiclesAndWallet(
    @Embedded
    val user : UserEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "ownerId"
    )
    val vehicles : List<Vehicle>,

    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    val wallet : Wallet
)