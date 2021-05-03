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
    val params: List<Param>,
    val refTxFatourati: String,
    val responseCode: String
)

data class Param(
    val libelle: String,
    val nomChamp: String,
    val typeChamp: String,
    val listVals: List<String>
)

data class ValidatedParam(
    val valChamp: String,
    val nomChamp: String
)

data class RecievededParam(
    val libelle: String,
    val nomChamp: String,
    val typeChamp: String,
    val errorText:String,
    val errorEnabled:Boolean,
    val hintVisibility:Int,
    val inputValue:String,
    val listVals: List<String>
)