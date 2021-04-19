package com.es.marocapp.model.responses

data class InitiateTransferQuoteResponse(
    val accountHolderInfoResponse: Any,
    val accountNumber: Any,
    val clearingNumber: Any,
    val description: String,
    val employeeId: Any,
    val quoteList: List<InitiateTransferQuote>,
    val responseCode: String,
    val taxList :List<DetailsList>
)

data class InitiateTransferQuote(
    val discount: Any,
    val fee: InitiateTransferQuoteFee,
    val feefri: String,
    val loyfee: Any,
    val loyfeefri: Any,
    val loyreward: Any,
    val loyrewardfri: Any,
    val offeridentities: Any,
    val promotion: Any,
    val quoteid: String
)

data class DetailsList(
    val name:String,
    val amount :Ammount
)

data class Ammount(
    val amount:Any,
    val currency:String
)

data class InitiateTransferQuoteFee(
    val amount: Double,
    val currency: String
)