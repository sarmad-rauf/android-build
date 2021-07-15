package com.es.marocapp.model.responses

data class BillPaymentCompaniesResponse(
    val bills: List<Bill>,
    val description: String,
    val responseCode: String
)

data class Bill(
    val companies: List<Company>,
    val name: String,
    val logo:String
)

data class Company(
    val codeCreance: String,
    val codeCreancier: String,
    val logo: String,
    val nomCreance: String,
    val nomCreancier: String,
    val serviceProvider: String
)