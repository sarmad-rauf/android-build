package com.es.marocapp.model.requests

data class LoginWithCertRequest(
    val apk_version: String,
    val authorization: String,
    val context: String,
    val deviceId: String,
    val identity: String,
    val secret: String,
    val type: String,
    val view: String
)