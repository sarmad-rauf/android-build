package com.es.marocapp.model.responses

data class LoginWithCertResponse(
    val appliedContext: String,
    val contentLength: String,
    val date: String,
    val description: String,
    val expires: String,
    val favoritesGetListResponse: Any,
    val feedBackThroughAppConfigs: Any,
    val getPaymentCompaniesResponse: Any,
    val location: Any,
    val responseCode: String,
    val setCookie: String
)