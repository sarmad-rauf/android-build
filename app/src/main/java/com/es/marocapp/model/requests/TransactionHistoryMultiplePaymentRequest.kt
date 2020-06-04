package com.es.marocapp.model.requests

data class TransactionHistoryMultiplePaymentRequest(
    val context: String,
    val enddate: String,
    val identity: String,
    val indexoffset: String,
    val startdate: String,
    val transactiontypesarr: List<String>
)