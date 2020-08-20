package com.es.marocapp.model.responses

data class BillPaymentFatoratiStepFourResponse(
    val description: String,
    val params: List<FatoratiParam>,
    val responseCode: String,
    val refTxFatourati: String,
    val totalAmount: String,
    val message:String
)

data class FatoratiParam(
    val description: String,
    val idArticle: String,
    val prixTTC: String,
    val typeArticle: String
)

data class FatoratiCustomParamModel(
    var isItemSelected : Boolean,
    var description: String,
    var idArticle: String,
    var prixTTC: String,
    var typeArticle: String
)