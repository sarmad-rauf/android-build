package com.es.marocapp.model.requests

data class MerchantPaymentRequest(
    val amount: String,
    val context: String,
    val quoteid: String,
    val `receiver`: String
)