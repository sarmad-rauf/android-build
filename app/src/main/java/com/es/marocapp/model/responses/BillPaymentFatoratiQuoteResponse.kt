package com.es.marocapp.model.responses

data class BillPaymentFatoratiQuoteResponse(
    val accountHolderInfoResponse: Any,
    val idArticle: String,
    val accountNumber: Any,
    val clearingNumber: Any,
    val description: String,
    val employeeId: Any,
    val quoteList: List<FatoratiQuoteQuote>,
    val responseCode: String,
    val taxList :List<DetailsList>
)

data class FatoratiQuoteQuote(
    val discount: Any,
    val fee: FatoratiQuoteFee,
    val feefri: String,
    val loyfee: Any,
    val loyfeefri: Any,
    val loyreward: Any,
    val loyrewardfri: Any,
    val offeridentities: Any,
    val promotion: Any,
    val quoteid: String
)

data class FatoratiQuoteFee(
    val amount: Double,
    val currency: String
)