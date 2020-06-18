package com.es.marocapp.model.responses

data class AirTimeResponse(
    val arguments: List<AirTimeArgument>,
    val currency: String,
    val description: String,
    val discount: String,
    val feeAmount: Any,
    val financialReceiptResponse: Any,
    val masterPassTransactionId: Any,
    val responseCode: String,
    val senderBalanceafter: String,
    val transDate: String,
    val transTime: String,
    val transactionId: String
)

data class AirTimeArgument(
    val name: String,
    val value: String
)