package com.es.marocapp.model.requests

data class PaymentQuoteRequest(
    val amount: String,
    val context: String,
    val receiver: String,
    val sender: String,
    val transferType :String,
    val loggedInUserProfile : String
)

class SimplePaymentQuoteRequest(
    val amount: String,
    val context: String,
    val receiver: String,
    val sender: String,
    val transferType :String
)