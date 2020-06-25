package com.es.marocapp.model.responses

data class BillPaymentFatoratiResponse(
    val arguments: List<Any>,
    val currency: String,
    val description: String,
    val discount: String,
    val feeAmount: String,
    val financialReceiptResponse: FatoratieFinancialReceiptResponse,
    val masterPassTransactionId: String,
    val responseCode: String,
    val senderBalanceafter: String,
    val transDate: String,
    val transTime: String,
    val transactionId: String
)

data class FatoratieFinancialReceiptResponse(
    val description: String,
    val financialreceipt: Financialreceipt,
    val responseCode: String
)

data class Financialreceipt(
    val amount: FatoratieAmount,
    val commitdate: String,
    val discount: String,
    val fee: FatoratieFee,
    val financialtransactionid: String,
    val loyfee: String,
    val loyreward: String,
    val promotion: String,
    val startdate: String,
    val taxes: String,
    val transactionstatus: String,
    val transfertype: String
)

data class FatoratieAmount(
    val amount: Double,
    val currency: String
)

data class FatoratieFee(
    val amount: Double,
    val currency: String
)