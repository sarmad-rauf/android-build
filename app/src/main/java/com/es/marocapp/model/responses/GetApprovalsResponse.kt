package com.es.marocapp.model.responses

data class GetApprovalsResponse(
    val approvaldetails: List<Any>,
    val description: String,
    val responseCode: String
)