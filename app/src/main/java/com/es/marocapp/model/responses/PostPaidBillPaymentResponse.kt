package com.es.marocapp.model.responses

data class PostPaidBillPaymentResponse(
    val arguments: List<PostPaidBillArgument>,
    val currency: Any,
    val description: String,
    val discount: Any,
    val feeAmount: Any,
    val financialReceiptResponse: Any,
    val masterPassTransactionId: Any,
    val responseCode: String,
    val senderBalanceafter: Any,
    val transDate: Any,
    val transTime: Any,
    val transactionId: Any
)

data class PostPaidBillArgument(
    val name: String,
    val value: String
)