package com.es.marocapp.model.responses

data class AirTimeResponse(
    val amount: String,
    val context: String,
    val maxNumberOfRetries: String,
    val plan: String,
    val quoteid: String,
    val receiver: String,
    val sender: String,
    val transferType: String
)