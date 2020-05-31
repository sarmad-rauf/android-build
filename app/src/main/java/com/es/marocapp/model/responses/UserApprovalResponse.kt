package com.es.marocapp.model.responses

data class UserApprovalResponse(
    val amount: Any,
    val approvalexpirytime: Any,
    val approvalid: Int,
    val approvaltype: Any,
    val description: String,
    val discount: Any,
    val fee: Any,
    val initiatingaccountholderid: Any,
    val message: Any,
    val offeridentities: Any,
    val responseCode: String,
    val status: Any
)