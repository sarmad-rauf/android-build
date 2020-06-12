package com.es.marocapp.model.responses

data class CashInWithOtpResponse(
    val description: String,
    val fee: String,
    val financialTransactionId: String,
    val offerIdentities: String,
    val receiverBalanceAfter: String,
    val responseCode: String,
    val senderBalanceAfter: String
)