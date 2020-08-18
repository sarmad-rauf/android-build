package com.es.marocapp.model.responses

data class BalanceInfoAndLimitResponse(
    var balance: String?,
    val currnecy: String?,
    val description: String?,
    val firstname: String?,
    val limitsList: List<Limits>?,
    val profilename: String?,
    val responseCode: String?,
    val surname: String?,
    val email: String?
)

data class Limits(
    val name: String?,
    val periodType: String?,
    val periodlength: String?,
    val threshhold: String?
)