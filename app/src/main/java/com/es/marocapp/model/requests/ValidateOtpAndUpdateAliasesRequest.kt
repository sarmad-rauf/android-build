package com.es.marocapp.model.requests

data class ValidateOtpAndUpdateAliasesRequest(
  //  val alias : String,
    val newalias: String,
    val context: String,
    val identity: String,
    val otp: String
)