package com.es.marocapp.model.responses

data class FloatTransferQuoteResponse(
    val accountHolderInfoResponse: String,
    val accountNumber: Any,
    val clearingNumber: Any,
    val description: String,
    val employeeId: Any,
    val quoteList: List<FloatTransfeQuote>,
    val responseCode: String,
    val taxList :List<DetailsList>
)

data class FloatTransfeQuote(
    val discount: String,
    val fee: FloatTransfeFee,
    val feefri: String,
    val loyfee: Any,
    val loyfeefri: Any,
    val loyreward: Any,
    val loyrewardfri: Any,
    val offeridentities: Any,
    val promotion: Any,
    val quoteid: String
)

data class FloatTransfeFee(
    val amount: Double,
    val currency: String
)