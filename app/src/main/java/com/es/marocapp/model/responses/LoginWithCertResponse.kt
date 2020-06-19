package com.es.marocapp.model.responses

data class LoginWithCertResponse(
    var allowedMenu: AllowedMenu,
    var appliedContext: String,
    var contactList: List<Contact>,
    var contentLength: String,
    var date: String,
    var description: String,
    var expires: String,
    var profile: Profile,
    var responseCode: String,
    var setCookie: String
)

data class AllowedMenu(
    var QR: List<String>,
    var accounts: List<String>,
    var defaultAccountStatusPopup : List<String>,
    var mobileRecharge: List<String>,
    var payments: List<String>,
    var sendMoney: List<String>
)

data class Contact(
    var contactName: String,
    var fri: String
)

data class Profile(
    var agentUser: Boolean,
    var consumerUser: Boolean,
    var merchantUser: Boolean
)