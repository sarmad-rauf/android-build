package com.es.marocapp.model.requests

data class GetOtpForRegistrationRequest(
    val context: String,
    val authorization: String,
    val encryptedNonce: String,
    val firstname: String,
    val identificationnumber: String,
    val identificationtype: String,
    val identity: String,
    val lastname: String
)