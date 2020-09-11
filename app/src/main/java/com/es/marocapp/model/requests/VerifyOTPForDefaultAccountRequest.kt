package com.es.marocapp.model.requests

data class VerifyOTPForDefaultAccountRequest(

    val context: String,
    val receiver: String,
    val operation: String,
    val language: String,
    val profile : String?,
    val referenceNumber : String,
    val otp : String,
    val firstName : String?,
    val lastName : String?
)