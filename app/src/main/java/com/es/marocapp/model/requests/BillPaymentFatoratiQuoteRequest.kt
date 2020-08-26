package com.es.marocapp.model.requests

data class BillPaymentFatoratiQuoteRequest(
    val amount: String,
    val codeCreance: String,
    val context: String,
    val creancierID: String,
    val paiementTotal: String,
    val receiver: String,
    val sender: String,
    val transferType: String,
    val refTxFatourati: String,
    val totalAmount: String,
    val params: List<FatoratiQuoteParam>
)

data class FatoratiQuoteParam(
    val idArticle: String,
    val prixTTC: String,
    val typeArticle: String
)

/*
data class dummyFatorati(
    val amount: String,
    val codeCreance: String,
    val context: String,
    val creancierID: String,
    val paiementTotal: String,
    val params: List<Param>,
    val `receiver`: String,
    val sender: String,
    val transferType: String
)

data class Param(
    val idArticle: String,
    val prixTTC: String,
    val typeArticle: String
)*/
