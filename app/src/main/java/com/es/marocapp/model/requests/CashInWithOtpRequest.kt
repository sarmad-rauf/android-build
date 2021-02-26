package com.es.marocapp.model.requests

data class CashInRequest(
    val amount: String,
    val context: String,
    val receiver : String,
    val quoteid: String,
    val senderNote : String
)