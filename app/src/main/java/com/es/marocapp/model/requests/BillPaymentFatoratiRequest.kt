package com.es.marocapp.model.requests

data class BillPaymentFatoratiRequest(
    val amount: String,
    val codeCreance: String,
    val context: String,
    val creancierID: String,
    val paiementTotal: String,
    val param: Param,
    val quoteid: String,
    val receiver: String,
    val sender: String,
    val transferType: String
)

data class Param(
    val idArticle: String,
    val prixTTC: String,
    val typeArticle: String
)