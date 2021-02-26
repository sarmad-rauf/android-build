package com.es.marocapp.model.requests

data class CashInQuoteRequest(
    val amount: String,
    val context: String,
    val receiver: String,
    val senderNote: String
)