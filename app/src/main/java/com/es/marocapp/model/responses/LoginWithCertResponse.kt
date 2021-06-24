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
    val setCookie: String,
    val getAccountHolderInformationResponse:GetAccountHolderInformationResponse
)

data class AllowedMenu(
    val AirTime: List<String>,
    val BillPayment: List<String>,
    val CashService: List<String>,
    val GenerateQR: List<String>,
    val MerchantPayment: List<String>,
    val SendMoney: List<String>,
    val ConsumerRegistration: List<String>,
    val CashInViaCard: List<String>,
    val MyApprovals: List<String>,
    val TransferCommission: List<String>
)

data class Contact(
    val contactname: String,
    val billproviderfri: String,
    val billproviderusername: String,
    val billproviderdescriptivename: String,
    val customerreference: String,
    val billreference: String,
    val billprovidercontactid: Int
)

data class Contacts(
    val contactName: String,
    val billproviderfri: String,
    val billproviderusername: String,
    val billproviderdescriptivename: String,
    val customerreference: String,
    val billreference: String,
    val billprovidercontactid: Int
)

data class AccountHolderEmailResponse(
    val responseCode: String,
    val description: String,
    val email: String
)

data class Profile(
    val agentUser: Boolean,
    val consumerUser: Boolean,
    val merchantUser: Boolean
)