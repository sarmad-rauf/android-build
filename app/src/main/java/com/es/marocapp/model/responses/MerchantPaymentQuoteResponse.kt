package com.es.marocapp.model.responses

data class MerchantPaymentQuoteResponse(
    val accountHolderInfoResponse: Any,
    val accountNumber: Any,
    val clearingNumber: Any,
    val description: String,
    val employeeId: Any,
    val quoteList: List<Quote>,
    val responseCode: String,
    val taxList :List<DetailsList>
)

data class MerchantPaymentQuote(
    val discount: Any,
    val fee: Fee,
    val feefri: String,
    val loyfee: Any,
    val loyfeefri: Any,
    val loyreward: Any,
    val loyrewardfri: Any,
    val offeridentities: Any,
    val promotion: Any,
    val quoteid: String
)

data class MerchantPaymentQuoteFee(
    val amount: Double,
    val currency: String
)