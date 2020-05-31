package com.es.marocapp.model.requests

data class UserApprovalRequest(
    val approvalid: String,
    val approved: String,
    val context: String
)