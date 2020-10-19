package com.es.marocapp.model.requests

data class VerifyOtpRequest(
    val context: String,
    val identity: String,
    val otp: String
)