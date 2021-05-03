package com.es.marocapp.model.requests

data class RegisterUserRequest(
    val accountholder: Accountholder,
    val context: String,
    val deviceId: String,
    val email: String,
    val identity: String,
    val reason: String
)

data class Accountholder(
    val birthdate: String,
    val cin: String,
    val firstname: String,
    val gender: String,
    val pstlAdr: String,
    val surname: String,
    val city:String
)