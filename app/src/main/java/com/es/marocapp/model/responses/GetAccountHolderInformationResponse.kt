package com.es.marocapp.model.responses

data class GetAccountHolderInformationResponse(
    val accountHolderStatus: String?,
    val bankDomainName: String?,
    val defaultfri: List<Defaultfri>,
    val description: String?,
    val firstName: String?,
    val internalidentity: String?,
    val msisdn: String?,
    val profileName: String?,
    val responseCode: String?,
    val sureName: String?
)

data class Defaultfri(
    val currency: Currency?,
    val fri: String?
)

data class Currency(
    val code: String?
)