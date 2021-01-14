package com.es.marocapp.model.requests

data class GetBalanceAndGenerateOtpRequest(
    val context : String,
    val profile : String,
    val identity : String
)