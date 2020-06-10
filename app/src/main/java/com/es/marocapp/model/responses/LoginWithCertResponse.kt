package com.es.marocapp.model.responses

data class LoginWithCertResponse(
    val appliedContext: String,
    val contentLength: String,
    val date: String,
    val description: String,
    val expires: String,
    val favoritesGetListResponse: Any,
    val feedBackThroughAppConfigs: FeedBackThroughAppConfigs,
    val getPaymentCompaniesResponse: Any,
    val location: Any,
    val profile: Profile,
    val responseCode: String,
    val setCookie: String
)

data class FeedBackThroughAppConfigs(
    val feedbackLoginDaysConfig: String,
    val feedbackMinimumBalanceLimit: String,
    val feedbackResponseTime: String,
    val feedbackTransactionsList: List<String>
)

data class Profile(
    val agentUser: Boolean,
    val consumerUser: Boolean,
    val merchantUser: Boolean
)