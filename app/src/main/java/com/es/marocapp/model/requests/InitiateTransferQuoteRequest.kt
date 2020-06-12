package com.es.marocapp.model.requests

data class InitiateTransferQuoteRequest(
    val amount: String,
    val context: String,
    val identity: String,
    val message: String
)