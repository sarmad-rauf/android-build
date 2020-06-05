package com.es.marocapp.model.requests

data class PaymentRequest(
    val amount: String,
    val context: String,
    val quoteid: String,
    val receiver: String,
    val sender: String,
    val transferType:String,
    val loggedInUserProfile : String
)

data class SimplePaymentRequest(
    val amount: String,
    val context: String,
    val quoteid: String,
    val receiver: String,
    val sender: String,
    val transferType:String
)