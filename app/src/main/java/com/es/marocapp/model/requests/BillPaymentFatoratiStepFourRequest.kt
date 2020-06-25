package com.es.marocapp.model.requests

data class BillPaymentFatoratiStepFourRequest(
    val codeCreance: String,
    val context: String,
    val creancierID: String,
    val nomChamp: String,
    val operation: String,
    val receiver: String,
    val refTxFatourati: String,
    val sender: String
)