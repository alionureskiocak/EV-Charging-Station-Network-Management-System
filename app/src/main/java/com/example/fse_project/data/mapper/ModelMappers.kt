package com.example.fse_project.data.mapper

import com.example.fse_project.data.local.database.entities.ChargerEntity
import com.example.fse_project.data.local.database.entities.FavoriteEntity
import com.example.fse_project.data.local.database.entities.ReportErrorEntity
import com.example.fse_project.data.local.database.entities.ReservationEntity
import com.example.fse_project.data.local.database.entities.StationEntity
import com.example.fse_project.data.local.database.entities.UserEntity
import com.example.fse_project.data.local.database.entities.VehicleEntity
import com.example.fse_project.data.local.database.entities.WalletEntity
import com.example.fse_project.data.local.database.relations.ReservationWithDetails
import com.example.fse_project.data.local.database.relations.StationWithChargers
import com.example.fse_project.data.local.database.relations.UserWithVehiclesAndWallet
import com.example.fse_project.domain.model.Charger
import com.example.fse_project.domain.model.Favorite
import com.example.fse_project.domain.model.ReportError
import com.example.fse_project.domain.model.Reservation
import com.example.fse_project.domain.model.Station
import com.example.fse_project.domain.model.User
import com.example.fse_project.domain.model.Vehicle
import com.example.fse_project.domain.model.Wallet

fun UserEntity.toDomain(): User = User(
    id = id,
    name = name,
    email = email,
    password = password,
    vehicles = emptyList(),
    wallet = Wallet(id,0.0)
)

fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    name = name,
    email = email,
    password = password,
)

fun UserWithVehiclesAndWallet.toDomain() : User{
    return User(
        id = this.user.id,
        name = this.user.name,
        email = this.user.email,
        password = this.user.password,
        vehicles = this.vehicles.map { it.toDomain() },
        wallet = this.wallet.toDomain()
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
    chargerStatus = chargerStatus,
    pricePerKwh = pricePerKwh
)

fun Charger.toEntity(): ChargerEntity = ChargerEntity(
    id = id,
    stationOwnerId = stationOwnerId,
    chargerName = chargerName,
    chargerType = chargerType,
    powerOutput = powerOutput,
    connectorType = connectorType,
    chargerStatus = chargerStatus,
    pricePerKwh = pricePerKwh
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

fun Reservation.toEntity(): ReservationEntity = ReservationEntity(
    id = id,
    userId = user.id,
    vehicleId = vehicle.id,
    stationId = station.id,
    chargerId = charger.id,
    startTime = startTime,
    endTime = endTime,
    pricePerKwh = pricePerKwh,
    status = status,
    actualKwh = actualKwh,
    totalAmount = totalAmount
)


fun ReservationWithDetails.toDomain() : Reservation{
    return Reservation(
        id = this.reservation.id,
        user = this.user.toDomain(),
        vehicle = this.vehicle.toDomain(),
        station = this.station.toDomain(),
        charger = this.charger.toDomain(),
        startTime = this.reservation.startTime,
        endTime = this.reservation.endTime,
        pricePerKwh = this.reservation.pricePerKwh,
        status = this.reservation.status,
        actualKwh = this.reservation.actualKwh,
        totalAmount = this.reservation.totalAmount
    )
}

// --- Wallet Mapper ---
fun WalletEntity.toDomain(): Wallet = Wallet(
    userId = userId,
    balance = balance
)

fun Wallet.toEntity(): WalletEntity = WalletEntity(
    userId = userId,
    balance = balance
)

fun FavoriteEntity.toDomain() = Favorite(
    userId = userId,
    stationId = stationId
)

fun Favorite.toEntity() = FavoriteEntity(
    userId = userId,
    stationId = stationId
)

fun ReportErrorEntity.toDomain() = ReportError(
    resId = resId,
    userId = userId,
    stationId = stationId,
    chargerId = chargerId,
    report = report,
    description = description
)

fun ReportError.toDomain() = ReportErrorEntity(
    resId = resId,
    userId = userId,
    stationId = stationId,
    chargerId = chargerId,
    report = report,
    description = description
)