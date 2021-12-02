package com.es.marocapp.model.responses

data class TransactionHistoryResponse(
    val description: String,
    val historyResponse: List<History>,
    val indexoffset: String,
    val responseCode: String,
    val totalCount: String
)

data class HistoryResponse(
    val date: String,
    val historyList: List<History>
)

data class History(
    val communicationchannel: String,
    val date: String,
    val fromaccount: String,
    val fromaccountholder: Any,
    val fromamount: String,
    val fromavailablebalance: String,
    val fromcommittedbalance: Any,
    val fromfee: String,
    val fromfri: String,
    val fromname: String,
    val fromnote: Any,
    val fromposmsisdn: Any,
    val fromtotalbalance: String,
    val initiatingaccountholder: Any,
    val initiatinguser: Any,
    val originalamount: String,
    val providercategory: Any,
    val realuser: Any,
    val toaccount: String,
    val toaccountholder: String,
    val toamount: String,
    val toavailablebalance: String,
    val tocommittedbalance: String,
    val tofee: String,
    val tofri: String,
    val tomessage: Any,
    val toname: String,
    val toposmsisdn: Any,
    val tototalbalance: String,
    val transactionid: String,
    val transactionstatus: String,
    val transfertype: String,
    val transferTypeEwp: String,
    val fromTax:String,
    val toTax:String,
    val showReceipt:Boolean
)