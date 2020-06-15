package com.es.marocapp.model.responses

data class GetAirTimeUseCasesResponse(
    val description: String,
    val rechargeFixe: List<String>,
    val rechargeMobile: List<RechargeMobile>,
    val responseCode: String
)

data class RechargeMobile(
    val amounts: List<String>,
    val code: Int,
    val plan: String
)