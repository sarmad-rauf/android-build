package com.es.marocapp.model.requests

data class TransferRequest(
    val amount: String,
    val context: String,
    val receivingFri: String
)