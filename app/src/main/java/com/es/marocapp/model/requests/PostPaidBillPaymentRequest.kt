package com.es.marocapp.model.requests

data class PostPaidBillPaymentRequest(
    val amount: String,
    val code: String,
    val context: String,
    val custId: String,
    val customerName: String,
    val maxNumberOfRetries: String,
    val quoteid: String,
    val `receiver`: String,
    val sender: String,
    val totalAmount: String,
    val transferType: String,
    val domain: String,
    val invoiceMonth: String,
    val invoiceOhrefnum : String,
    val invoiceOhxact: String,
    val invoiceOpenAmount: String,
    val isMultipleInvoice : String
)