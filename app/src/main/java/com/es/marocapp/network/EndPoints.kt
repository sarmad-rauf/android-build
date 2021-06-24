package com.es.marocapp.network

object EndPoints {

    // Approvals API
    const val approvals = "todos/1"

    // GetPreLoginData
    const val getprelogindata = "getprelogindata"

    //GetAccountHolderInformation
    const val getaccountholderinformation = "getaccountholderinformation"

    //GetProfile
    const val getProfile = "getprofile"

    //GetAccountDetail
    const val getaccountDetail = "getaccountdetails"

    //GetAccountHolderInformation
    const val getaccountholderinformationnew = "getreceiverinfo"

    //GetAccountHolderPersonalInformation
    const val getaccountholderpersonalinformation = "getuserdata"

    //GetInitialAuthDetials
    const val getinitialauthdetails = "getinitialauthdetails"

    //GetOTPForRegistration
    const val getotpforregistration = "getotpforregistration"

    //RegisterUser
    const val registeruser = "registeruser"

    // Level 2 registration
    const val levelTwoRegisterUser = "registeruser/level2"

    //RegisterConsumerUser
    const val registerconsumer_viaagent = "registerconsumer/viaagent"

    //Activate
    const val activate = "activate"

    //verifyotpandupdatealias
    const val verifyotpandupdatealias = "verifyotpandupdatealias"

    //getotp
    const val getotp = "getotp"

    //verifyotp
    const val verifyotp = "verifyotp"

    //createcredentials
    const val createcredentials = "createcredentials"

    //forgotpassword
    const val forgotpassword = "forgotpassword"

    //loginwithcert
    const val loginwithcert = "loginwithcert"

    //getaccountholderdefaultnotificationemail
    const val getAccountHoldelEmail = "getaccountholderdefaultnotificationemail"

    //getbalanceinfoandlimits
    const val getbalanceinfoandlimits = "getbalanceinfoandlimits"

    //getaccounts
    const val getaccounts = "getaccounts"

    //updateEmail
    const val updateEmail = "setaccountholderdefaultnotificationemail"

    //updateAdress city and adress
    const val updateAdress = "updateaccountholderaddress"

    //updateaccountholderidentification CIN number
    const val updateaccountholderidentification = "updateaccountholderidentification"

    //updateaccountholderpersonalinformation firstName Lastname MiddleName DOB
    const val updateaccountholderpersonalinformation = "updateaccountholderpersonalinformation"

    //getaccounts/su
    const val getaccountsu = "getaccounts/su"

    //changepassword
    const val changepassword = "changepassword"

    //getapprovals
    const val getapprovals = "getapprovals"

    //userapproval
    const val userapproval = "userapproval"

    //getaccountholderadditionalinformation
    const val getaccountholderadditionalinformation = "getaccountholderadditionalinformation"

    //transfer
    const val transfer = "transfer"

    //pull commision fund call
    const val transfercommision = "transfer/sysuser "

    //transfer/quote
    const val transfer_quote = "transfer/quote"

    //merchantpayment/quote
    const val merchantpayment_quote = "merchantpayment/quote"

    //merchantpayment
    const val merchantpayment = "merchantpayment"

    //payment/quote
    const val payment_quote = "payment/quote"

    //payment
    const val payment = "payment"

    //gettransactionhistory
    const val gettransactionhistory = "gettransactionhistory"

    //getbalance
    const val getbalance = "getbalance_v12"

    //floattransfer/quote
    const val floattransfer_quote = "floattransfer/quote"

    //floattransfer
    const val floattransfer = "floattransfer"

    //getTranslations
    const val getTranslations = "getlanguagelist"

    //initiatetransfer/quote changed to cashout_quote
    const val cashout_quote = "cashout/quote"

    //initiatetransfer
    const val initiatetransfer = "initiatetransfer"

    //generateotp
    const val generateotp = "generateotp"

    //cashin/quote
    const val cashin_quote = "cashin/quote"

    //cashin
    const val cashin = "cashin"

    //getfinancialresourceinformationStep1//DefaultAccountStatus
    const val getfinancialresourceinformationStep1 = "getfinancialresourceinformation/step1"

    //getfinancialresourceinformationStep2//DefaultAccountStatus
    const val getfinancialresourceinformationStep2 = "getfinancialresourceinformation/step2"

    //logout
    const val logout = "logout"

    //getairtimeusecases
    const val getairtimeusecases = "getairtimeusecases"

    //getusertypeprofiles
    const val getusertypeprofiles = "getusertypeprofiles"

    //getfinancialresourceinformation/step1
    const val getfinancialresourceinformation_step1 = "getfinancialresourceinformation/step1"

    //getfinancialresourceinformation/step2
    const val getfinancialresourceinformation_step2 = "getfinancialresourceinformation/step2"

    //payment/airtime/quote
    const val payment_airtime_quote = "payment/airtime/quote"

    //payment/airtime
    const val payment_airtime = "payment/airtime"

    //addcontact
    const val addcontact = "addcontact"

    //addcontactNew
    const val addBillProvideriContact = "addbillprovidercontact"

    //getcontactNew
    const val getBillProvideriContactList = "getbillprovidercontactlist"

    //deletecontact
    const val deletecontact = "deletebillprovidercontact"

    //getfinancialresourceinformation/bill/postpaid
    const val getfinancialresourceinformation_bill_postpaid =
        "getfinancialresourceinformation/bill/postpaid"

    //payment/bill/postpaid/quote
    const val payment_bill_postpaid_quote = "payment/bill/postpaid/quote"

    //payment/bill/postpaid
    const val payment_bill_postpaid = "payment/bill/postpaid"

    //bills/fatourati/step1
    const val bills_fatourati_step1 = "bills/fatourati/step1"

    //getfinancialresourceinformation/bill/fatourati/step1
    const val getfinancialresourceinformation_bill_fatourati_step1 =
        "getfinancialresourceinformation/bill/fatourati/step1"

    ///getfinancialresourceinformation/bill/fatourati/step2
    const val getfinancialresourceinformation_bill_fatourati_step2 =
        "getfinancialresourceinformation/bill/fatourati/step2"

    ///getfinancialresourceinformation/bill/fatourati/step3
    const val getfinancialresourceinformation_bill_fatourati_step3 =
        "getfinancialresourceinformation/bill/fatourati/step3"

    ///getfinancialresourceinformation/bill/fatourati/step2-3
    const val getfinancialresourceinformation_bill_fatourati_step2_3 =
        "getfinancialresourceinformation/bill/fatourati/step2_3"


    //getfinancialresourceinformation/bill/fatourati/step4
    const val getfinancialresourceinformation_bill_fatourati_step4 =
        "getfinancialresourceinformation/bill/fatourati/step4"

    //payment/bill/fatourati/quote
    const val payment_bill_fatourati_quote = "payment/bill/fatourati/quote"

    //payment/bill/fatourati
    const val payment_bill_fatourati = "payment/bill/fatourati"

    //Unregister DefaultAccountStatus
    const val unregisterDefaultAccount = "getfinancialresourceinformation/unregister"

    //Update Languaga
    const val updateLanguage = "updatelanguage"

    //Upgrde profile
    const val upgradeProfile = "setprofile"
    const val upgradeProfileFileUploadPDF = "uploadfile/pdf"
    const val upgradeProfileFileUploadImage = "uploadfile/jpg"

    //getfaq
    const val getfaq = "getfaq"

    //getbalanceandgenerateotp
    const val getbalanceandgenerateotp = "getbalanceandgenerateotp"

}