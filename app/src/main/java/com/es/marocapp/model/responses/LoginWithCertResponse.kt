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
    val QR: List<Any>,
    val accounts: List<Any>,
    val defaultAccountStatusPopup : List<Any>,
    val mobileRecharge: List<String>,
    val payments: List<Any>,
    val sendMoney: List<String>
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