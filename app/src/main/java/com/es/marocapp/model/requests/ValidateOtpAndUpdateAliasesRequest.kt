package com.es.marocapp.model.requests

data class ValidateOtpAndUpdateAliasesRequest(
    val alais: String,
    val context: String,
    val identity: String,
    val otp: String
)