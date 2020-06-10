package com.es.marocapp.model.responses

data class FloatTransferResponse(
    val description: String,
    val extension: Any,
    val financialTransactionId: String,
    val responseCode: String,
    val senderBalanceAfter: String,
    val senderFee: String
)