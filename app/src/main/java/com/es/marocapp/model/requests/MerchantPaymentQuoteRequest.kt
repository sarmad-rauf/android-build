package com.es.marocapp.model.requests

data class MerchantPaymentQuoteRequest(
    val amount: String,
    val context: String,
    val `receiver`: String
)