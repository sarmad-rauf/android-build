package com.es.marocapp.model.responses

data class CashInWithOtpQuoteResponse(
    val accountHolderInfoResponse: Any,
    val accountNumber: Any,
    val clearingNumber: Any,
    val description: String,
    val employeeId: Any,
    val quoteList: List<CashInWithOtpQuote>,
    val responseCode: String
)

data class CashInWithOtpQuote(
    val discount: Any,
    val fee: CashInWithOtpFee,
    val feefri: String,
    val loyfee: Any,
    val loyfeefri: Any,
    val loyreward: Any,
    val loyrewardfri: Any,
    val offeridentities: Any,
    val promotion: Any,
    val quoteid: String
)

data class CashInWithOtpFee(
    val amount: Double,
    val currency: String
)