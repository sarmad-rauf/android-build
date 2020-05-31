package com.es.marocapp.model.requests

data class ForgotPasswordRequest(
    val context: String,
    val credentialtype: String,
    val identity: String,
    val newsecret: String,
    val otp: String,
    val repeatedsecret: String
)