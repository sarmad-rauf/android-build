package com.es.marocapp.model.requests

data class CashInWithOtpQuoteRequest(
    val amount: String,
    val context: String,
    val otp: String,
    val receiver: String,
    val senderNote: String
)