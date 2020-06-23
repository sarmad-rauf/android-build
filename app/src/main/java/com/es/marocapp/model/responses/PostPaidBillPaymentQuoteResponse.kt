package com.es.marocapp.model.responses

data class PostPaidBillPaymentQuoteResponse(
    val accountHolderInfoResponse: Any,
    val accountNumber: Any,
    val clearingNumber: Any,
    val description: String,
    val employeeId: Any,
    val quoteList: List<PostPaidBillPaymentQuote>,
    val responseCode: String
)

data class PostPaidBillPaymentQuote(
    val discount: Any,
    val fee: PostPaidBillPaymentFee,
    val feefri: String,
    val loyfee: Any,
    val loyfeefri: Any,
    val loyreward: Any,
    val loyrewardfri: Any,
    val offeridentities: Any,
    val promotion: Any,
    val quoteid: String
)

data class PostPaidBillPaymentFee(
    val amount: Double,
    val currency: String
)