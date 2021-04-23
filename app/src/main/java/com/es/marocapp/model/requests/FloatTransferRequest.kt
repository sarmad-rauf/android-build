package com.es.marocapp.model.requests

data class FloatTransferRequest(
    val amount: String,
    val context: String,
    val `receiver`: String
)