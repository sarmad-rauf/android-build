package com.es.marocapp.model.responses

data class AirTimeQuoteResponse(
    val accountHolderInfoResponse: Any,
    val accountNumber: Any,
    val clearingNumber: Any,
    val employeeId: Any,
    val quoteList: List<Quote>,
    val description: String,
    val responseCode: String,
    val taxList :List<DetailsList>
)

data class AirTimeQuote(
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

data class AirTimeQuoteFee(
    val amount: Double,
    val currency: String
)