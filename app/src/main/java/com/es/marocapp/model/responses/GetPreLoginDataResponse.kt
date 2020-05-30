package com.es.marocapp.model.responses

data class GetPreLoginDataResponse(
    val androidOtpExpiryTime: String,
    val androidOtpLength: Int,
    val bankDomainForRegistration: String,
    val cnLength: String,
    val cnRegex: String,
    val consumerRegistrationProfile: String,
    val currency: String,
    val dateFormat: String,
    val description: String,
    val dobInputRequired: String,
    val genderList: List<String>,
    val iosOtpExpiryTime: String,
    val iosOtpLength: Int,
    val motherNameInputRequired: String,
    val msisdnLength: String,
    val msisdnPrefix: String,
    val publicKey: Any,
    val responseCode: String,
    val url: String,
    val version: String
)