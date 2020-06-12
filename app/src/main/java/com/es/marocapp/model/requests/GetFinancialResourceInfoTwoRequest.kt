package com.es.marocapp.model.requests

data class GetFinancialResourceInfoTwoRequest(
    val context: String,
    val language: String,
    val operation: String,
    val otp: String,
    val profile: String,
    val `receiver`: String,
    val referenceNumber: String
)