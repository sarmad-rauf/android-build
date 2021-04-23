package com.es.marocapp.model.requests

data class PaymentRequest(
    val amount: String,
    val context: String,
    val receiver: String,
    val sender: String,
    val transferType:String,
    val loggedInUserProfile : String,
    val paymentType : String,
    val profile: String,
    val qrType: String,
    val qrValue: String,
    val typeOfBusiness: String,
    val merchantName: String
)

data class SimplePaymentRequest(
    val amount: String,
    val context: String,
    val receiver: String,
    val sender: String,
    val transferType:String,
    val paymentType : String,
    val profile: String,
    val qrType: String,
    val qrValue: String,
    val typeOfBusiness: String,
    val merchantName: String,
    val loggedInUserProfile : String
)