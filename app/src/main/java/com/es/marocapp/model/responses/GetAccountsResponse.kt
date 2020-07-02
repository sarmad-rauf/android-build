package com.es.marocapp.model.responses

data class GetAccountsResponse(
    val accounts: List<Account>,
    val description: String,
    val responseCode: String
)

data class Account(
    val accountFri: String,
    val accountStatus: String,
    val accountType: String,
    val balance: String,
    val bankDomainName: String,
    val currency: String,
    val profileName: String,
    val referenceProfileName: String
)