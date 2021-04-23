package com.es.marocapp.model.requests

data class AirTimeRequest(
    val amount: String,
    val context: String,
    val maxNumberOfRetries: String,
    val plan: String,
    val receiver: String,
    val sender: String,
    val transferType: String
)