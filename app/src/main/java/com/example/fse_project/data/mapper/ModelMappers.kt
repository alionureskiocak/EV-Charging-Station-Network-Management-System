package com.example.fse_project.data.mapper

import com.example.fse_project.data.local.database.entities.ChargerEntity
import com.example.fse_project.data.local.database.entities.ReservationEntity
import com.example.fse_project.data.local.database.entities.StationEntity
import com.example.fse_project.data.local.database.entities.UserEntity
import com.example.fse_project.data.local.database.entities.VehicleEntity
import com.example.fse_project.data.local.database.entities.WalletEntity
import com.example.fse_project.data.local.database.relations.StationWithChargers
import com.example.fse_project.data.local.database.relations.UserWithVehiclesAndWallet
import com.example.fse_project.domain.model.Charger
import com.example.fse_project.domain.model.Reservation
import com.example.fse_project.domain.model.Station
import com.example.fse_project.domain.model.User
import com.example.fse_project.domain.model.Vehicle
import com.example.fse_project.domain.model.Wallet

fun UserEntity.toDomain(): User = User(
    id = id,
    name = name,
    email = email,
    vehicles = emptyList(),
    wallet = Wallet(-1,0.0)
)

fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    name = name,
    email = email
)

fun UserWithVehiclesAndWallet.toDomain() : User{
    return User(
        id = this.user.id,
        name = this.user.name,
        email = this.user.email,
        vehicles = this.vehicles,
        wallet = this.wallet
    )
}

// --- Vehicle Mapper ---
fun VehicleEntity.toDomain(): Vehicle = Vehicle(
    id = id,
    ownerId = ownerId,
    brand = brand,
    model = model,
    capacity = capacity,
    connectorType = connectorType,
    licensePlate = licensePlate
)

fun Vehicle.toEntity(): VehicleEntity = VehicleEntity(
    id = id,
    ownerId = ownerId,
    brand = brand,
    model = model,
    capacity = capacity,
    connectorType = connectorType,
    licensePlate = licensePlate
)

// --- Charger Mapper ---
fun ChargerEntity.toDomain(): Charger = Charger(
    id = id,
    stationOwnerId = stationOwnerId,
    chargerName = chargerName,
    chargerType = chargerType,
    powerOutput = powerOutput,
    connectorType = connectorType,
    chargerStatus = chargerStatus
)

fun Charger.toEntity(): ChargerEntity = ChargerEntity(
    id = id,
    stationOwnerId = stationOwnerId,
    chargerName = chargerName,
    chargerType = chargerType,
    powerOutput = powerOutput,
    connectorType = connectorType,
    chargerStatus = chargerStatus
)

// --- Station Mapper ---
fun StationEntity.toDomain(): Station = Station(
    id = id,
    name = name,
    latitude = latitude,
    longitude = longitude,
    address = address,
    chargers = emptyList()
)

fun Station.toEntity(): StationEntity = StationEntity(
    id = id,
    name = name,
    latitude = latitude,
    longitude = longitude,
    address = address
)

fun StationWithChargers.toStation() : Station{
    return Station(
        id = this.station.id,
        name = this.station.name,
        latitude = this.station.latitude,
        longitude = this.station.longitude,
        address = this.station.address,
        chargers = this.chargers.map { it.toDomain() }
    )
}

// --- Reservation Mapper ---
fun ReservationEntity.toDomain(): Reservation = Reservation(
    id = id,
    userId = userId,
    vehicleId = vehicleId,
    chargerId = chargerId,
    startTime = startTime,
    endTime = endTime,
    pricePerKwh = pricePerKwh,
    status = status
)

fun Reservation.toEntity(): ReservationEntity = ReservationEntity(
    id = id,
    userId = userId,
    vehicleId = vehicleId,
    chargerId = chargerId,
    startTime = startTime,
    endTime = endTime,
    pricePerKwh = pricePerKwh,
    status = status
)

// --- Wallet Mapper ---
fun WalletEntity.toDomain(): Wallet = Wallet(
    userId = userId,
    balance = balance
)

fun Wallet.toEntity(): WalletEntity = WalletEntity(
    userId = userId,
    balance = balance
)