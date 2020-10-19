package com.es.marocapp.model.requests

data class RegisterConsumerUserRequest(
    val accountholder: Accountholder,
    val context: String,
    val deviceId: String,
    val email: String,
    val identity: String,
    val otp: String
)