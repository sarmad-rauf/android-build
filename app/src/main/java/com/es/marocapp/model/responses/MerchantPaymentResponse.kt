package com.es.marocapp.model.responses

data class MerchantPaymentResponse(
    val arguments: List<Any>,
    val description: String,
    val discount: Any,
    val financialReceiptResponse: FinancialReceiptResponse,
    val responseCode: String,
    val senderBalanceAfter: String,
    val senderFee: String,
    val transactionId: String
)

data class FinancialReceiptResponse(
    val description: String,
    val financialreceipt: Any,
    val responseCode: String
)