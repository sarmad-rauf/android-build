package com.es.marocapp.model.responses

data class BillPaymentFatoratiStepOneResponse(
    val creanciers: List<Creancier>,
    val description: String,
    val message: String,
    val responseCode: String
)

data class Creancier(
    val codeCreance: String,
    val codeCreancier: String,
    val nomCreance: String,
    val nomCreancier: String,
    val logoPath: String
)