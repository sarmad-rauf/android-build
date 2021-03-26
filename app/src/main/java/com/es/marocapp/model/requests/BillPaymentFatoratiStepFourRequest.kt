package com.es.marocapp.model.requests

import com.es.marocapp.model.responses.ValidatedParam

data class BillPaymentFatoratiStepFourRequest(
    val codeCreance: String,
    val context: String,
    val creancierID: String,
    val params: List<ValidatedParam>,
    val operation: String,
    val receiver: String,
    val refTxFatourati: String,
    val sender: String
)
