package com.example.fse_project.data.local.database.entity

import androidx.room.PrimaryKey

data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    val name : String,
    val email : String,
    val password : String
)