package com.es.marocapp.model.responses

data class BillPaymentFatoratiStepFourResponse(
    val description: String,
    val params: List<FatoratiParam>,
    val globalParams: List<GlobalParams>,
    val responseCode: String,
    val refTxFatourati: String,
    val totalAmount: String,
    val typeFrais: String,
    val valeurFrais: String,
    val message:String
)

data class FatoratiParam(
    val description: String,
    val idArticle: String,
    val prixTTC: String,
    val typeArticle: String,
    val dateFacture: String
)

data class GlobalParams(
    val libelle: String,
    val nomChamp: String,
    val valeurChamp: String
)

data class FatoratiCustomParamModel(
    var isItemSelected : Boolean,
    var description: String,
    var idArticle: String,
    var prixTTC: String,
    var typeArticle: String,
    var showDescription:Boolean
)

data class FatoratiCustomDateParamModel(
    var isItemSelected : Boolean,
    var description: String,
    var idArticle: String,
    var prixTTC: String,
    var typeArticle: String,
    var date: String
)