package com.es.marocapp.model.requests

data class BillPaymentFatoratiQuoteRequest(
    val amount: String,
    val codeCreance: String,
    val context: String,
    val creancierID: String,
    val paiementTotal: String,
    val param: FatoratiQuoteParam,
    val receiver: String,
    val sender: String,
    val transferType: String,
    val refTxFatourati: String,
    val totalAmount: String
)

data class FatoratiQuoteParam(
    val idArticle: String,
    val prixTTC: String,
    val typeArticle: String
)