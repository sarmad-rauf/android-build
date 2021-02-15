package com.es.marocapp.model.requests

data class BillPaymentFatoratiStepTwoRequest(
    val context: String,
    val creancierID: String,
    val operation: String,
    val receiver: String,
    val sender: String
)
data class BillPaymentFatoratiStepThreeRequest(
    val context: String,
    val creancierID: String,
    val operation: String,
    val receiver: String,
    val sender: String,
    val codeCreance:String
)