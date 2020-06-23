package com.es.marocapp.model.responses

data class GetPreLoginDataResponse(
    val amountConversionValue: String,
    val androidOtpExpiryTime: String,
    val androidOtpLength: Int,
    val bankDomainForRegistration: String,
    val cnLength: String,
    val cnRegex: String,
    val consumerRegistrationProfile: String,
    val currencyOnEwp: String,
    val currencyToShow: String,
    val dateFormat: String,
    val description: String,
    val genderList: List<String>,
    val helpLineNumber: String,
    val iosOtpExpiryTime: String,
    val iosOtpLength: Int,
    val msisdnLength: String,
    val msisdnPrefix: String,
    val numberOfTransactions: String,
    val publicKey: Any,
    val quickAmounts: List<String>,
    val responseCode: String,
    val url: String,
    val version: String
)