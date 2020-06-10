package com.es.marocapp.model.requests

data class FloatTransferQuoteRequest(
    val amount: String,
    val context: String,
    val receiver: String
)