package com.es.marocapp.model.requests

data class CashInWithOtpRequest(
    val amount: String,
    val context: String,
    val otp: String,
    val receiver : String,
    val quoteid: String,
    val senderNote : String
)