package com.es.marocapp.model.requests

data class GetAirTimeUseCasesRequest(
    val context: String,
    val language: String,
    val profileName: String,
    val userType: String
)