package com.es.marocapp.model.responses

data class BillPaymentFatoratiStepTwoResponse(
    val description: String,
    val creances:  List<creances>,
    val responseCode: String
)


data class creances(
    val nomCreance:String,
    val codeCreance: String
)

data class BillPaymentFatoratiStepThreeResponse(
    val description: String,
    val param: Param,
    val refTxFatourati: String,
    val responseCode: String
)

data class Param(
    val libelle: String,
    val nomChamp: String,
    val typeChamp: String
)