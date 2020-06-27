package com.es.marocapp.model.responses

data class LoginWithCertResponse(
    val allowedMenu: AllowedMenu,
    val appliedContext: String,
    val contactList: List<Contact>,
    val contentLength: String,
    val date: String,
    val description: String,
    val expires: String,
    val profile: Profile,
    val responseCode: String,
    val setCookie: String
)

data class AllowedMenu(
    val AirTime: List<Any>,
    val BillPayment: List<String>,
    val CashService: List<String>,
    val GenerateQR: List<Any>,
    val MerchantPayment: List<Any>,
    val SendMoney: List<Any>
)

data class Contact(
    val contactName: String,
    val fri: String
)

data class Profile(
    val agentUser: Boolean,
    val consumerUser: Boolean,
    val merchantUser: Boolean
)