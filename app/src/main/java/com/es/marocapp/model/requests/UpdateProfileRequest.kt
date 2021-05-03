package com.es.marocapp.model.requests

import com.es.marocapp.model.responses.Account

data class UpdateProfileRequest(
    val context: String,
    val identity: String
)

data class UpdateEmailRequest(
    val context: String,
    val identity: String,
    val email: String,
    val reason: String
)

data class UpdateCINRequest(
    val context: String,
    val identity: String,
    val cin: String
)

data class UpdateAdressRequest(
    val context: String,
    val identity: String,
    val address: String,
    val city: String
)

data class UpdatePersonalInformationRequest(
    val context: String,
    val identity: String,
    val firstname: String,
    val lastname: String,
    val dob: String
)

data class UpdateProfileResponse(
    val description: String,
    val responseCode: String
)

