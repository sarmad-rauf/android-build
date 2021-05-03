package com.es.marocapp.model.requests

data class InitiateTransferQuoteRequest(
    val amount: String,
    val context: String,
    val receiver: String,
    val sender: String
)