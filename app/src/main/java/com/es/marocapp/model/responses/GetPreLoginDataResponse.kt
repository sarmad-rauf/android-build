package com.es.marocapp.model.responses

data class GetPreLoginDataResponse(
    val amountConversionValue: String,
    val androidOtpExpiryTime: String,
    val androidOtpLength: Int,
    val bankDomainForRegistration: String,
    val bannerImages: List<String>,
    val billFavoriteLength: String,
    val cilLength: String,
    val cilRegex: String,
    val cmiWebpageUrl: String,
    val cnLength: String,
    val cnRegex: String,
    val commissionsBalanceLimitKey: String,
    val consumerRegistrationProfile: String,
    val currencyOnEwp: String,
    val currencyToShow: String,
    val dateFormat: String,
    val defaultAccountOtpLength: String,
    val defaultAccountOtpRegex: String,
    val defaultLanguage: String,
    val description: String,
    val faqs: String,
    val genderList: List<String>,
    val getAgentReceiverAlias: String,
    val getAirtimeReceiverAlias: String,
    val getFatouratiAlias: String,
    val getMerchantReceiverAlias: String,
    val getPostPaidFixedDomainAlias: String,
    val getPostPaidInternetDomainAlias: String,
    val getPostPaidMobileDomainAlias: String,
    val getTransferReceiverAlias: String,
    val helpLineNumber: String,
    val iosOtpExpiryTime: String,
    val iosOtpLength: Int,
    val merchantTypePayment: String,
    val msisdnLength: String,
    val msisdnMaxLength: String,
    val msisdnPrefix: String,
    val msisdnRegex: String,
    val numberAlias: String,
    val numberOfTransactions: String,
    val operationTypeCreance: String,
    val operationTypeCreancier: String,
    val operationTypeImpayes: String,
    val paymentTypeInitiateMerchant: String,
    val paymentTypeSendMoney: String,
    val portalOtpExpiryTime: Any,
    val portalOtpLength: Int,
    val postpaidBillCodeRegex: String,
    val postpaidBillFixedRegex: String,
    val postpaidBillInternetRegex: String,
    val postpaidBillMobileRegex: String,
    val publicKey: Any,
    val quickAmounts: List<String>,
    val quickRechargeAmounts: List<String>,
    val responseCode: String,
    val termsAndConditions: String,
    val transferTypePayment: String,
    val typeBillPayment: String,
    val typeCashIn: String,
    val typeCommisioning: String,
    val typePayment: String,
    val url: String,
    val version: String,
    val billTypePostPaid: String,
    val walletBalanceLimitKey: String
)