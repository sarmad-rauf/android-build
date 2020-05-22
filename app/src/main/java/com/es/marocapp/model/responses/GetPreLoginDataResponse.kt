package com.es.marocapp.model.responses

data class GetPreLoginDataResponse(
    val androidOtpExpiryTime: String?,
    val androidOtpLength: Int?,
    val description: String?,
    val dobInputRequired: String?,
    val iosOtpExpiryTime: String?,
    val iosOtpLength: Int?,
    val motherNameInputRequired: String?,
    val publicKey: Any?,
    val responseCode: String?,
    val url: String?,
    val version: String?,
    val versionios: Any?
)