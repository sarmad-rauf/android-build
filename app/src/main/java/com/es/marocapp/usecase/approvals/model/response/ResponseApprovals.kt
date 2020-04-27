package com.es.marocapp.usecase.approvals.model.response

import com.squareup.moshi.Json

data class ResponseApprovals(
        var userId: String,
        var id: String,
        var title: String,
        var completed: Boolean
)
