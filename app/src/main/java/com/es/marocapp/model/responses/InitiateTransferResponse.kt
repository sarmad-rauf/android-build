package com.es.marocapp.model.responses

data class InitiateTransferResponse(
    val approvalId: String,
    val description: String,
    val financialTransactionId: String,
    val responseCode: String
)