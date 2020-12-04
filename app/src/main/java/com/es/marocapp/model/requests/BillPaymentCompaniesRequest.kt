package com.es.marocapp.model.requests

data class BillPaymentCompaniesRequest(
    val context: String,
    val operation: String,
    val receiver: String,
    val sender: String
)