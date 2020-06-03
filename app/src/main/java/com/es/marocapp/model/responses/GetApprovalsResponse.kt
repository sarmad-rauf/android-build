package com.es.marocapp.model.responses

data class GetApprovalsResponse(
    val approvaldetails: List<Approvaldetail>,
    val description: String,
    val responseCode: String
)

data class Approvaldetail(
    val amount: Amount,
    val approvalexpirytime: Long,
    val approvalid: Int,
    val approvaltype: String,
    val discount: Any,
    val fee: FeeAprroval,
    val initiatingaccountholderid: String,
    val message: String,
    val offeridentities: Any,
    val status: String
)

data class Amount(
    val amount: Double,
    val currency: String
)

data class FeeAprroval(
    val amount: Double,
    val currency: String
)