package com.es.marocapp.model.requests

data class BillPaymentFatoratiRequest(
    val amount: String,
    val codeCreance: String,
    val context: String,
    val creancierID: String,
    val paiementTotal: String,
    val quoteid: String,
    val receiver: String,
    val sender: String,
    val transferType: String,
    val isMultipleInvoice : String,
    val refTxFatourati: String,
    val totalAmount: String,
    val params: List<Param>,
    val companyName:String?
)

data class Param(
    val idArticle: String,
    val prixTTC: String,
    val typeArticle: String
)
