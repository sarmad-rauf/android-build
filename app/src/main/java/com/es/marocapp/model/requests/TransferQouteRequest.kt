package com.es.marocapp.model.requests

data class TransferQouteRequest(
    val amount: String,
    val context: String,
    val receivingFri: String
)