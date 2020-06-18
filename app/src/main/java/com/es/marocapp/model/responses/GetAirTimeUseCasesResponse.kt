package com.es.marocapp.model.responses

data class GetAirTimeUseCasesResponse(
    val description: String,
    val rechargeFixe: RechargeFixe,
    val rechargeMobile: RechargeMobile,
    val responseCode: String
)

data class RechargeFixe(
    val planList: List<String>,
    val titleName: String
)

data class RechargeMobile(
    val planList: List<Plan>,
    val titleName: String
)

data class Plan(
    val amounts: List<String>,
    val code: Int,
    val plan: String
)